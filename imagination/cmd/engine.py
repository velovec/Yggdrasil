#!/usr/bin/env python
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

import os
import sys

import eventlet


if os.name == 'nt':
    eventlet.monkey_patch(os=False)
else:
    eventlet.monkey_patch()

# If ../imagination/__init__.py exists, add ../ to Python search path, so that
# it will override what happens to be installed in /usr/(local/)lib/python...
root = os.path.join(os.path.abspath(__file__), os.pardir, os.pardir, os.pardir)
if os.path.exists(os.path.join(root, 'imagination', '__init__.py')):
    sys.path.insert(0, root)

from oslo_log import log as logging
from oslo_service import service

from imagination.common import config
from imagination.common import engine

CONF = config.CONF


def main():
    try:
        config.parse_args()

        logging.setup(CONF, 'imagination')
        launcher = service.ServiceLauncher(CONF)
        launcher.launch_service(engine.get_rpc_service())

        launcher.wait()
    except RuntimeError as e:
        sys.stderr.write("ERROR: %s\n" % e)
        sys.exit(1)


if __name__ == '__main__':
    main()
