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

from oslo_config import cfg
from oslo_config import types
from oslo_log import log as logging

from imagination import version

portType = types.Integer(1, 65535)

paste_deploy_opts = [
    cfg.StrOpt('flavor', help='Paste flavor'),
    cfg.StrOpt('config_file', help='Path to Paste config file'),
]

bind_opts = [
    cfg.StrOpt('bind-host', default='0.0.0.0',
               help='Address to bind the Imagination API server to.'),
    cfg.Opt('bind-port',
            type=portType,
            default=8899,
            help='Port the bind the Imagination API server to.'),
]

keystone_opts = [
    cfg.BoolOpt('insecure', default=False,
                help='This option explicitly allows Imagination to perform '
                     '"insecure" SSL connections and transfers with '
                     'Keystone API running Keystone API.'),

    cfg.StrOpt('ca_file',
               help='(SSL) Tells Imagination to use the specified certificate file '
                    'to verify the peer when communicating with Keystone.'),

    cfg.StrOpt('cert_file',
               help='(SSL) Tells Imagination to use the specified client '
                    'certificate file when communicating with Keystone.'),

    cfg.StrOpt('key_file', help='(SSL/SSH) Private key file name to '
                                'communicate with Keystone API')
]

imagination_opts = [
    cfg.StrOpt('url', help='Optional imagination url in format '
                           'like http://0.0.0.0:8899 used by Imagination engine'),

    cfg.BoolOpt('insecure', default=False,
                help='This option explicitly allows Imagination to perform '
                     '"insecure" SSL connections and transfers used by '
                     'Imagination engine.'),

    cfg.StrOpt('cacert',
               help='(SSL) Tells Imagination to use the specified client '
               'certificate file when communicating with Imagination API '
               'used by Imagination engine.'),

    cfg.StrOpt('cert_file',
               help='(SSL) Tells Imagination to use the specified client '
                    'certificate file when communicating with Imagination '
                    'used by Imagination engine.'),

    cfg.StrOpt('key_file', help='(SSL/SSH) Private key file name '
                                'to communicate with Imagination API used by '
                                'Imagination engine.'),

    cfg.StrOpt('endpoint_type', default='publicURL',
               help='Imagination endpoint type used by Imagination engine.'),

    cfg.ListOpt('enabled_plugins', default=None,
                help="List of enabled Extension Plugins. "
                     "Remove or leave commented to enable all installed "
                     "plugins."),

    cfg.StrOpt('region_name_for_services',
               help="Default region name used to get services endpoints.")
]

CONF = cfg.CONF
CONF.register_opts(paste_deploy_opts, group='paste_deploy')
CONF.register_cli_opts(bind_opts)
CONF.register_opts(keystone_opts, group='keystone')
CONF.register_opts(imagination_opts, group='imagination')


def parse_args(args=None, usage=None, default_config_files=None):
    logging.register_options(CONF)
    logging.setup(CONF, 'imagination')
    CONF(args=args,
         project='imagination',
         version=version.version_string,
         usage=usage,
         default_config_files=default_config_files)