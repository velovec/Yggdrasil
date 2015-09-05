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

from imagination.common import rpc
from imagination.db import models
from imagination.db.services import actions as actions_db


class ActionServices(object):
    @staticmethod
    def create_action_task(action_name, target_obj, args, token):
        action = None
        if action_name and target_obj:
            action = {
                'object_id': target_obj,
                'method': action_name,
                'args': args or {}
            }

        task = {
            'action': action,
            'token': token,
        }

        return task

    @staticmethod
    def update_task(action, task, unit):
        task_info = models.Task()
        task_info.action = task['action']
        status = models.Status()
        status.text = 'Action {0} is scheduled'.format(action[1]['name'])
        status.level = 'info'
        task_info.statuses.append(status)
        with unit.begin():
            unit.add(task_info)

    @staticmethod
    def submit_task(action_name, target_obj, args, token, unit):
        task = ActionServices.create_action_task(action_name, target_obj, args, token)
        task_id = actions_db.update_task(action_name, task, unit)
        task.update({
            'id': task_id
        })
        rpc.engine().handle_task(task)
        return task_id

    @staticmethod
    def get_result(task_id, unit):
        task = unit.query(models.Task).filter_by(id=task_id).first()

        if task is not None:
            return task.result

        return None
