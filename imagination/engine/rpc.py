#
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.

from oslo_config import cfg
from oslo_log import log as logging
import oslo_messaging as messaging
from oslo_messaging.rpc import client

from imagination import context as auth_ctx
from imagination.engine import base
from imagination import exceptions as exc

LOG = logging.getLogger(__name__)


_TRANSPORT = None

_ENGINE_CLIENT = None


def cleanup():
    """Intended to be used by tests to recreate all RPC related objects."""

    global _TRANSPORT
    global _ENGINE_CLIENT

    _TRANSPORT = None
    _ENGINE_CLIENT = None


def get_transport():
    global _TRANSPORT

    if not _TRANSPORT:
        _TRANSPORT = messaging.get_transport(cfg.CONF)

    return _TRANSPORT


def get_engine_client():
    global _ENGINE_CLIENT

    if not _ENGINE_CLIENT:
        _ENGINE_CLIENT = EngineClient(get_transport())

    return _ENGINE_CLIENT


class EngineServer(object):
    """RPC Engine server."""

    def __init__(self, engine):
        self._engine = engine

    def start_workflow(self, rpc_ctx, workflow_name, workflow_input,
                       description, params):
        """Receives calls over RPC to start workflows on engine.

        :param rpc_ctx: RPC request context.
        :return: Workflow execution.
        """

        LOG.info(
            "Received RPC request 'start_workflow'[rpc_ctx=%s,"
            " workflow_name=%s, workflow_input=%s, description=%s, params=%s]"
            % (rpc_ctx, workflow_name, workflow_input, description, params)
        )

        return self._engine.start_workflow(
            workflow_name,
            workflow_input,
            description,
            **params
        )

    def start_action(self, rpc_ctx, action_name,
                     action_input, description, params):
        """Receives calls over RPC to start actions on engine.

        :param rpc_ctx: RPC request context.
        :param action_name: name of the Action.
        :param action_input: input dictionary for Action.
        :param description: description of new Action execution.
        :param params: extra parameters to run Action.
        :return: Action execution.
        """
        LOG.info(
            "Received RPC request 'start_action'[rpc_ctx=%s,"
            " name=%s, input=%s, description=%s, params=%s]"
            % (rpc_ctx, action_name, action_input, description, params)
        )

        return self._engine.start_action(
            action_name,
            action_input,
            description,
            **params
        )

    def on_task_state_change(self, rpc_ctx, task_ex_id, state):
        return self._engine.on_task_state_change(task_ex_id, state)

    def on_action_complete(self, rpc_ctx, action_ex_id, result_data,
                           result_error):
        """Receives RPC calls to communicate action result to engine.

        :param rpc_ctx: RPC request context.
        :param action_ex_id: Action execution id.
        :return: Action execution.
        """

        return self._engine.on_action_complete(action_ex_id, None)


def wrap_messaging_exception(method):
    """This decorator unwrap remote error in one of MistralException.

    oslo.messaging has different behavior on raising exceptions
    when fake or rabbit transports are used. In case of rabbit
    transport it raises wrapped RemoteError which forwards directly
    to API. Wrapped RemoteError contains one of MistralException raised
    remotely on Engine and for correct exception interpretation we
    need to unwrap and raise given exception and manually send it to
    API layer.
    """
    def decorator(*args, **kwargs):
        try:
            return method(*args, **kwargs)

        except client.RemoteError as e:
            exc_cls = getattr(exc, e.exc_type)
            raise exc_cls(e.value)

    return decorator


class EngineClient(base.Engine):
    """RPC Engine client."""

    def __init__(self, transport):
        """Constructs an RPC client for engine.

        :param transport: Messaging transport.
        """
        serializer = auth_ctx.RpcContextSerializer(
            auth_ctx.JsonPayloadSerializer())

        self._client = messaging.RPCClient(
            transport,
            messaging.Target(topic=cfg.CONF.engine.topic),
            serializer=serializer
        )

    @wrap_messaging_exception
    def start_action(self, action_name, action_input,
                     description=None, **params):
        """Starts action sending a request to engine over RPC.

        :return: Action execution.
        """
        return self._client.call(
            auth_ctx.ctx(),
            'start_action',
            action_name=action_name,
            action_input=action_input or {},
            description=description,
            params=params
        )

    def on_task_state_change(self, task_ex_id, state):
        return self._client.call(
            auth_ctx.ctx(),
            'on_task_state_change',
            task_ex_id=task_ex_id,
            state=state
        )

    @wrap_messaging_exception
    def on_action_complete(self, action_ex_id, result):
        """Conveys action result to Mistral Engine.

        This method should be used by clients of Mistral Engine to update
        state of a action execution once action has executed. One of the
        clients of this method is Mistral REST API server that receives
        action result from the outside action handlers.

        Note: calling this method serves an event notifying Mistral that
        it possibly needs to move the workflow on, i.e. run other workflow
        tasks for which all dependencies are satisfied.

        :return: Task.
        """

        return self._client.call(
            auth_ctx.ctx(),
            'on_action_complete',
            action_ex_id=action_ex_id,
            result_data=result.data,
            result_error=result.error
        )
