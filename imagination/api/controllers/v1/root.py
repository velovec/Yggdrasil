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

import pecan
from wsme import types as wtypes
import wsmeext.pecan as wsme_pecan

from imagination.api.controllers import resource


class RootResource(resource.Resource):
    """Root resource for API version 2.

    It references all other resources belonging to the API.
    """

    uri = wtypes.text


class Controller(object):
    """API root controller for version 2."""

    @wsme_pecan.wsexpose(RootResource)
    def index(self):
        return RootResource(uri='%s/%s' % (pecan.request.host_url, 'v2'))
