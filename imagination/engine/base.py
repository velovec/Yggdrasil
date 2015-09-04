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


import abc
import six


@six.add_metaclass(abc.ABCMeta)
class Engine(object):
    """Engine interface."""

    @abc.abstractmethod
    def start_action(self, action_name, action_input,
                     description=None, **params):
        """Starts the specific action.

        :param action_name: Action name.
        :param action_input: Action input data as a dictionary.
        :param description: Execution description.
        :param params: Additional options for action running.
        :return: Action execution object.
        """
        raise NotImplementedError

    @abc.abstractmethod
    def on_action_complete(self, action_ex_id, result):
        """Accepts action result and continues the workflow.

        Action execution result here is a result which comes from an
        action/workflow associated which the task.
        :param action_ex_id: Action execution id.
        :param result: Action/workflow result. Instance of
            imagination.workflow.base.Result
        :return:
        """
        raise NotImplementedError

