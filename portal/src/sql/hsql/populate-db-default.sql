insert into PROFILING_RULE values ('j1', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.');
insert into RULE_CRITERION values (1, 'j1', 0, 'standard', 'desktop', 'default-desktop', 0);
insert into RULE_CRITERION values (2, 'j1', 1, 'standard', 'page', 'default-page', 0);
insert into RULE_CRITERION values (3, 'j1', 2, 'standard', 'group.role.user', null, 0);
insert into RULE_CRITERION values (4, 'j1', 3, 'standard', 'mediatype', null, 1);
insert into RULE_CRITERION values (5, 'j1', 4, 'standard', 'language', null, 1);
insert into RULE_CRITERION values (6, 'j1', 5, 'standard', 'country', null, 1);

insert into PROFILING_RULE values ('role-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
insert into RULE_CRITERION values (7, 'role-fallback', 0, 'standard', 'desktop', 'default-desktop', 0);
insert into RULE_CRITERION values (8, 'role-fallback', 1, 'standard', 'page', 'default-page', 0);
insert into RULE_CRITERION values (9, 'role-fallback', 2, 'standard', 'roles', null, 2);
insert into RULE_CRITERION values (10, 'role-fallback', 3, 'standard', 'mediatype', null, 1);
insert into RULE_CRITERION values (11, 'role-fallback', 4, 'standard', 'language', null, 1);
insert into RULE_CRITERION values (12, 'role-fallback', 5, 'standard', 'country', null, 1);

insert into PRINCIPAL_RULE_ASSOC values ( 'anon', 'j1' );



