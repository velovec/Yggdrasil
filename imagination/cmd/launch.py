#!/usr/bin/env python
# -*- coding: utf-8 -*-
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

import sys

import eventlet

eventlet.monkey_patch(
    os=True,
    select=True,
    socket=True,
    thread=False if '--use-debugger' in sys.argv else True,
    time=True)

import os

POSSIBLE_TOPDIR = os.path.normpath(os.path.join(os.path.abspath(sys.argv[0]),
                                   os.pardir,
                                   os.pardir))
if os.path.exists(os.path.join(POSSIBLE_TOPDIR, 'imagination', '__init__.py')):
    sys.path.insert(0, POSSIBLE_TOPDIR)

from oslo_config import cfg
from oslo_log import log as logging
import oslo_messaging as messaging
from wsgiref import simple_server

from imagination.api import app
from imagination import config
from imagination import context as ctx
# from imagination.db.v1 import api as db_api
# from imagination.engine import default_engine as def_eng
from imagination.engine import rpc
# from imagination.services import expiration_policy
# from imagination.services import scheduler
from imagination import version


CONF = cfg.CONF

LOG = logging.getLogger(__name__)


def launch_engine(transport):
    target = messaging.Target(
        topic=cfg.CONF.engine.topic,
        server=cfg.CONF.engine.host
    )

    # engine_v1 = def_eng.DefaultEngine(rpc.get_engine_client())

    # endpoints = [rpc.EngineServer(engine_v1)]
    endpoints = []

    # Setup scheduler in engine.
    # db_api.setup_db()
    # scheduler.setup()

    # Setup expiration policy
    # expiration_policy.setup()

    server = messaging.get_rpc_server(
        transport,
        target,
        endpoints,
        executor='eventlet',
        serializer=ctx.RpcContextSerializer(ctx.JsonPayloadSerializer())
    )

    # engine_v1.register_membership()

    server.start()
    server.wait()


def launch_api(transport):
    host = cfg.CONF.api.host
    port = cfg.CONF.api.port

    server = simple_server.make_server(
        host,
        port,
        app.setup_app()
    )

    LOG.info("imagination API is serving on http://%s:%s (PID=%s)" %
             (host, port, os.getpid()))

    server.serve_forever()


def launch_any(transport, options):
    # Launch the servers on different threads.
    threads = [eventlet.spawn(LAUNCH_OPTIONS[option], transport)
               for option in options]

    print('Server started.')

    [thread.wait() for thread in threads]


# Map cli options to appropriate functions. The cli options are
# registered in imagination's config.py.
LAUNCH_OPTIONS = {
    'api': launch_api,
    'engine': launch_engine,
}


imagination_TITLE = """
Imagination Build Service, version %s
""" % version.version_string()


def print_server_info():
    print(imagination_TITLE)

    comp_str = ("[%s]" % ','.join(LAUNCH_OPTIONS)
                if cfg.CONF.server == ['all'] else cfg.CONF.server)

    print('Launching server components %s...' % comp_str)


def get_properly_ordered_parameters():
    """In oslo it's important the order of the launch parameters.
    if --config-file came after the command line parameters the command
    line parameters are ignored.
    So to make user command line parameters are never ignored this method
    moves --config-file to be always first.
    """
    args = sys.argv[1:]

    for arg in sys.argv[1:]:
        if arg == '--config-file' or arg.startswith('--config-file='):
            conf_file_value = args[args.index(arg) + 1]
            args.remove(conf_file_value)
            args.remove(arg)
            args.insert(0, arg)
            args.insert(1, conf_file_value)

    return args


def main():
    try:
        config.parse_args(get_properly_ordered_parameters())

        print_server_info()

        logging.setup(CONF, 'imagination')
        transport = rpc.get_transport()

        if cfg.CONF.server == ['all']:
            # Launch all servers.
            launch_any(transport, LAUNCH_OPTIONS.keys())
        else:
            # Validate launch option.
            if set(cfg.CONF.server) - set(LAUNCH_OPTIONS.keys()):
                raise Exception('Valid options are all or any combination of '
                                'api, engine, and executor.')

            # Launch distinct set of server(s).
            launch_any(transport, set(cfg.CONF.server))

    except RuntimeError as excp:
        sys.stderr.write("ERROR: %s\n" % excp)
        sys.exit(1)


if __name__ == '__main__':
    main()
