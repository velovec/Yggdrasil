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

import routes

from imagination.api.v1 import actions
from imagination.common import wsgi


class APIv1(wsgi.Router):
    @classmethod
    def factory(cls, global_conf, **local_conf):
        return cls(routes.Mapper())

    def __init__(self, mapper):
        actions_resource = actions.create_resource()
        mapper.connect('/actions/',
                       controller=actions_resource,
                       action='create_action',
                       conditions={'method': ['POST']}, path='')
        mapper.connect('/actions/{task_id}',
                       controller=actions_resource,
                       action='get_results',
                       conditions={'method': ['GET']}, path='')

        super(APIv1, self).__init__(mapper)
