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
insert into test_user values (101, 'dilbert', 'Dilbert', 'Funny')
insert into test_user values (102, 'outm', 'Out', 'of Mind')
insert into test_user values (201, 'pointy', 'Pointy', 'Hair')
insert into test_user values (202, 'outt', 'Out', 'of Touch')
insert into test_user values (301, 'userd', 'User', 'Dumb')
insert into test_user values (302, 'userp', 'User', 'Picky')

insert into test_group values (100, 'engineers')
insert into test_group values (200, 'managers')
insert into test_group values (300, 'users')

insert into test_user_group values (1, 101, 100)
insert into test_user_group values (2, 102, 100)
insert into test_user_group values (3, 201, 200)
insert into test_user_group values (4, 202, 200)
insert into test_user_group values (5, 301, 300)
insert into test_user_group values (6, 302, 300)
