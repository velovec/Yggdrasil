#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
# implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import uuid

import eventlet.debug
from oslo_config import cfg
from oslo_log import log as logging
import oslo_messaging as messaging
from oslo_messaging import target
from oslo_serialization import jsonutils

from imagination.common.helpers import token_sanitizer
from imagination.common import rpc
from imagination.engine import actions as eng_actions

CONF = cfg.CONF

RPC_SERVICE = None
PLUGIN_LOADER = None

LOG = logging.getLogger(__name__)

eventlet.debug.hub_exceptions(False)


def _prepare_rpc_service(server_id):
    endpoints = [TaskProcessingEndpoint()]

    transport = messaging.get_transport(CONF)
    s_target = target.Target('imagination', 'tasks', server=server_id)
    return messaging.get_rpc_server(transport, s_target, endpoints, 'eventlet')


def get_rpc_service():
    global RPC_SERVICE

    if RPC_SERVICE is None:
        RPC_SERVICE = _prepare_rpc_service(str(uuid.uuid4()))
    return RPC_SERVICE


class TaskProcessingEndpoint(object):
    @classmethod
    def handle_task(cls, context, task):
        result = cls.execute(task)
        rpc.api().process_result(result, task_id=task['id'])

    @staticmethod
    def execute(task):
        s_task = token_sanitizer.TokenSanitizer().sanitize(task)
        LOG.info('Starting processing task: {task_desc}'.format(
            task_desc=jsonutils.dumps(s_task))
        )

        result = None

        try:
            task_executor = TaskExecutor(task)
            result = task_executor.execute()
            return result
        finally:
            LOG.info('Finished processing task: {task_desc}'.format(
                task_desc=jsonutils.dumps(result))
            )


class TaskExecutor(object):
    @property
    def action(self):
        return self._action

    def __init__(self, task):
        self._action = task.get('action')
        self._token = task.get('token')

    def execute(self):
        if hasattr(eng_actions, self._action['object_id']):
            instance = getattr(eng_actions, self._action['object_id'])()
            if hasattr(instance, self._action['method']):
                return getattr(instance, self._action['method'])(**self._action['args'])
        return {
            'error': "Action '{object_id}:{method}' doesn't exists".format(
                **self._action
            )
        }