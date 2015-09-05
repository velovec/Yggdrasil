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
from oslo_messaging import target

CONF = cfg.CONF

RPC_SERVICE = None

LOG = logging.getLogger(__name__)


class ResultEndpoint(object):
    @staticmethod
    def process_result(context, result, *args, **kwargs):
        print result, args, kwargs


def _prepare_rpc_service(server_id):
    endpoints = [ResultEndpoint()]

    transport = messaging.get_transport(CONF)
    s_target = target.Target('imagination', 'results', server=server_id)
    return messaging.get_rpc_server(transport, s_target, endpoints, 'eventlet')


def get_rpc_service():
    global RPC_SERVICE

    if RPC_SERVICE is None:
        RPC_SERVICE = _prepare_rpc_service(str(uuid.uuid4()))
    return RPC_SERVICE
