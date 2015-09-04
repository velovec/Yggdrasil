#
#    Licensed under the Apache License, Version 2.0 (the "License"); you may
#    not use this file except in compliance with the License. You may obtain
#    a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#    License for the specific language governing permissions and limitations
#    under the License.

import uuid

from oslo_config import cfg
from oslo_log import log as logging
import oslo_messaging as messaging
from oslo_messaging.notify import dispatcher as oslo_dispatcher
from oslo_messaging import target

CONF = cfg.CONF

RPC_SERVICE = None
NOTIFICATION_SERVICE = None

LOG = logging.getLogger(__name__)


class ResultEndpoint(object):
    @staticmethod
    def process_result(context, result, *args):
        pass


def notification_endpoint_wrapper(priority='info'):
    def wrapper(func):
        class NotificationEndpoint(object):
            def __init__(self):
                setattr(self, priority, self._handler)

            def _handler(self, ctxt, publisher_id, event_type,
                         payload, metadata):
                if event_type == ('imagination.%s' % func.__name__):
                    func(payload)

            def __call__(self, payload):
                return func(payload)
        return NotificationEndpoint()
    return wrapper


@notification_endpoint_wrapper()
def track_instance(payload):
    print "track_instance:", payload


@notification_endpoint_wrapper()
def untrack_instance(payload):
    print "untrack_instance:", payload


@notification_endpoint_wrapper()
def report_notification(report):
    print "report_notification:", report


def _prepare_rpc_service(server_id):
    endpoints = [ResultEndpoint()]

    transport = messaging.get_transport(CONF)
    s_target = target.Target('imagination', 'results', server=server_id)
    return messaging.get_rpc_server(transport, s_target, endpoints, 'eventlet')


def _prepare_notification_service(server_id):
    endpoints = [report_notification, track_instance, untrack_instance]

    transport = messaging.get_transport(CONF)
    s_target = target.Target(topic='imagination', server=server_id)
    dispatcher = oslo_dispatcher.NotificationDispatcher(
        [s_target], endpoints, None, True)
    return messaging.MessageHandlingServer(transport, dispatcher, 'eventlet')


def get_rpc_service():
    global RPC_SERVICE

    if RPC_SERVICE is None:
        RPC_SERVICE = _prepare_rpc_service(str(uuid.uuid4()))
    return RPC_SERVICE


def get_notification_service():
    global NOTIFICATION_SERVICE

    if NOTIFICATION_SERVICE is None:
        NOTIFICATION_SERVICE = _prepare_notification_service(str(uuid.uuid4()))
    return NOTIFICATION_SERVICE