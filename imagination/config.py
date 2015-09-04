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

"""
Configuration options registration and useful routines.
"""

import itertools

from oslo_config import cfg
from oslo_log import log

from imagination import version


launch_opt = cfg.ListOpt(
    'server',
    default=['all'],
    help='Specifies which imagination server to start by the launch script. '
         'Valid options are all, api and engine.'
)

api_opts = [
    cfg.StrOpt('host', default='0.0.0.0', help='Imagination API server host'),
    cfg.IntOpt('port', default=8899, help='Imagination API server port')
]

pecan_opts = [
    cfg.StrOpt('root', default='imagination.api.controllers.root.RootController',
               help='Pecan root controller'),
    cfg.ListOpt('modules', default=["imagination.api"],
                help='A list of modules where pecan will search for '
                     'applications.'),
    cfg.BoolOpt('debug', default=False,
                help='Enables the ability to display tracebacks in the '
                     'browser and interactively debug during '
                     'development.'),
    cfg.BoolOpt('auth_enable', default=True,
                help='Enables user authentication in pecan.')
]

use_debugger = cfg.BoolOpt(
    "use-debugger",
    default=False,
    help='Enables debugger. Note that using this option changes how the '
         'eventlet library is used to support async IO. This could result '
         'in failures that do not occur under normal operation. '
         'Use at your own risk.'
)

engine_opts = [
    cfg.StrOpt('engine', default='default',
               help='Imagination engine plugin'),
    cfg.StrOpt('host', default='0.0.0.0',
               help='Name of the engine node. This can be an opaque '
                    'identifier. It is not necessarily a hostname, '
                    'FQDN, or IP address.'),
    cfg.StrOpt('topic', default='iamgination_engine',
               help='The message topic that the engine listens on.'),
]


CONF = cfg.CONF

API_GROUP = 'api'
ENGINE_GROUP = 'engine'
PECAN_GROUP = 'pecan'

CONF.register_opts(api_opts, group=API_GROUP)
CONF.register_opts(engine_opts, group=ENGINE_GROUP)
CONF.register_opts(pecan_opts, group=PECAN_GROUP)

CLI_OPTS = [
    use_debugger,
    launch_opt
]

CONF.register_cli_opts(CLI_OPTS)

_DEFAULT_LOG_LEVELS = [
    'amqp=WARN',
    'sqlalchemy=WARN',
    'oslo_messaging=INFO',
    'iso8601=WARN',
    'eventlet.wsgi.server=WARN',
    'stevedore=INFO',
    'oslo_service.periodic_task=INFO',
    'oslo_service.loopingcall=INFO',
    'imagination.services.periodic=INFO',
    'kazoo.client=WARN'
]


def list_opts():
    return [
        (API_GROUP, api_opts),
        (ENGINE_GROUP, engine_opts),
        (PECAN_GROUP, pecan_opts),
        (None, itertools.chain(
            CLI_OPTS,
            []
        ))
    ]


def parse_args(args=None, usage=None, default_config_files=None):
    log.set_defaults(default_log_levels=_DEFAULT_LOG_LEVELS)
    log.register_options(CONF)
    CONF(
        args=args,
        project='imagination',
        version=version,
        usage=usage,
        default_config_files=default_config_files
    )