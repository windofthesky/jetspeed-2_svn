insert into PROFILING_RULE values ('j1', 
   'org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule',
   'The default profiling rule following the Jetspeed-1 hard-coded profiler fallback algorithm.');
insert into RULE_CRITERION values ('j1', 0, 'request', 'name', 'default');
insert into RULE_CRITERION values ('j1', 1, 'standard', 'user', null);
insert into RULE_CRITERION values ('j1', 2, 'standard', 'mediatype', null);
insert into RULE_CRITERION values ('j1', 3, 'standard', 'language', null);
insert into RULE_CRITERION values ('j1', 4, 'standard', 'country', null);

insert into PROFILING_RULE values ('role-fallback', 
     'org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule',
     'A role based fallback algorithm based on Jetspeed-1 role-based fallback');
insert into RULE_CRITERION values ('role-fallback', 0, 'request', 'name', 'default');
insert into RULE_CRITERION values ('role-fallback', 1, 'standard', 'role-based', null);
insert into RULE_CRITERION values ('role-fallback', 2, 'standard', 'mediatype', null);
insert into RULE_CRITERION values ('role-fallback', 3, 'standard', 'language', null);
insert into RULE_CRITERION values ('role-fallback', 4, 'standard', 'country', null);
