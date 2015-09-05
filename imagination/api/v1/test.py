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

from oslo_log import log as logging
from imagination.common import wsgi
from imagination.common import rpc

LOG = logging.getLogger(__name__)


class Controller(object):

    def get(self, request, test_id):
        rpc.engine().handle_task({
            'action': 'test',
            'model': 'Test',
            'test_id': test_id,
            'token': '',
            'tenant_id': '',
            'id': 'qwjehqkjwhekjqw'
        })
        return {}

    def post(self, request, test_id, body):
        return {}


def create_resource():
    return wsgi.Resource(Controller())