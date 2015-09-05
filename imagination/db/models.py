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

"""
SQLAlchemy models for imagination data
"""
from oslo_db.sqlalchemy import models
from oslo_utils import timeutils
import sqlalchemy as sa
from sqlalchemy.ext import declarative
from sqlalchemy import orm as sa_orm
import uuid

from imagination.db.sqla import types as st


class TimestampMixin(object):
    __protected_attributes__ = set(["created", "updated"])

    id = sa.Column(sa.String(36), primary_key=True,
                   default=lambda: uuid.uuid4().hex)
    created = sa.Column(sa.DateTime, default=timeutils.utcnow,
                        nullable=False)
    updated = sa.Column(sa.DateTime, default=timeutils.utcnow,
                        nullable=False, onupdate=timeutils.utcnow)

    def update(self, values):
        """dict.update() behaviour."""
        self.updated = timeutils.utcnow()
        super(TimestampMixin, self).update(values)

    def __setitem__(self, key, value):
        self.updated = timeutils.utcnow()
        super(TimestampMixin, self).__setitem__(key, value)


class _ImaginationBase(models.ModelBase):
    def to_dict(self):
        dictionary = self.__dict__.copy()
        return dict((k, v) for k, v in dictionary.iteritems()
                    if k != '_sa_instance_state')


Base = declarative.declarative_base(cls=_ImaginationBase)


class Task(Base, TimestampMixin):
    __tablename__ = 'task'

    started = sa.Column(sa.DateTime, default=timeutils.utcnow, nullable=False)
    finished = sa.Column(sa.DateTime, default=None, nullable=True)
    description = sa.Column(st.JsonBlob(), nullable=False)
    action = sa.Column(st.JsonBlob())

    statuses = sa_orm.relationship("Status", backref='task',
                                   cascade='save-update, merge, delete')
    result = sa.Column(st.JsonBlob(), nullable=True, default={})

    def to_dict(self):
        dictionary = super(Task, self).to_dict()
        if 'statuses' in dictionary:
            del dictionary['statuses']
        return dictionary


class Status(Base, TimestampMixin):
    __tablename__ = 'status'

    entity_id = sa.Column(sa.String(255), nullable=True)
    entity = sa.Column(sa.String(10), nullable=True)
    task_id = sa.Column(sa.String(32), sa.ForeignKey('task.id'))
    text = sa.Column(sa.Text(), nullable=False)
    level = sa.Column(sa.String(32), nullable=False)
    details = sa.Column(sa.Text(), nullable=True)

    def to_dict(self):
        dictionary = super(Status, self).to_dict()
        if 'deployment' in dictionary:
            del dictionary['deployment']
        return dictionary


class Lock(Base):
    __tablename__ = 'locks'
    id = sa.Column(sa.String(50), primary_key=True)
    ts = sa.Column(sa.DateTime, nullable=False)


def register_models(engine):
    """Creates database tables for all models with the given engine."""
    models = (Status, Task, Lock)
    for model in models:
        model.metadata.create_all(engine)


def unregister_models(engine):
    """Drops database tables for all models with the given engine."""
    models = (Status, Task, Lock)
    for model in models:
        model.metadata.drop_all(engine)
