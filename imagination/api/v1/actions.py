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
from webob import exc

from imagination.common import wsgi
from imagination.db import session as db_session
from imagination.services import actions


LOG = logging.getLogger(__name__)


class Controller(object):

    def get_results(self, request, task_id):
        LOG.debug('Action:GetResult <TaskId: {0}>'.format(task_id))

        unit = db_session.get_session()
        result = actions.ActionServices.get_result(task_id, unit)

        if result is not None:
            return result

        msg = 'Result for task with task_id: {} was not found.'.format(task_id)

        LOG.error(msg)
        raise exc.HTTPNotFound(msg)

    def create_action(self, request):
        LOG.debug('Action:Create')

        try:
            unit = db_session.get_session()
            task_id = actions.ActionServices.submit_task(
                request.json_body['action'], request.json_body['object_id'],
                request.json_body['args'], 'test_token', unit)

            return {'action_id': task_id}
        except KeyError:
            return {
                'error': 'Unable to create action'
            }

def create_resource():
    return wsgi.Resource(Controller())