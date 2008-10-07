-----------------------------------------------------------------------------
-- Copyright 2004 The Apache Software Foundation
-- 
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
-- http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-----------------------------------------------------------------------------
create table test_user
(
	id int identity,
	login_name varchar,
	first_name varchar, 
	last_name varchar
);
go

create table test_group
(
	id int identity,
	name varchar
);
go

create table test_user_group
(
	id int identity,
	user_id int,
	group_id int,
	foreign key (user_id) references test_user(id),
	foreign key (group_id) references test_group(id)
);
go
