/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.administration;

/**
 * PortalConfiguration portal configuration contants
 * TODO: integrate Configuration with JMX 
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface PortalConfigurationConstants
{
    /** EMAIL */
    /** email of the system administrator */
    static final String EMAIL_ADMIN = "email.admin";
    /** email of the system manager */
    static final String EMAIL_MANAGER = "email.manager";
    /** email sender */
    static final String EMAIL_SENDER = "email.sender";
    /** email user info attribute **/
    static final String EMAIL_USERINFO_ATTRIBUTE = "email.userinfo.attribute";
    
    /** LAYOUT */
    /** the default page layout if none is specified */
    static final String LAYOUT_PAGE_DEFAULT = "layout.page.default";
    
    /** Decorators */
    /** default page decorator if none specified */
    static final String DECORATOR_PAGE_DEFAULT = "decorator.page.default";
    /** default portlet decorator if none specified */    
    static final String DECORATOR_PORTLET_DEFAULT = "decorator.portlet.default";
    
    /** PSML **/
    /** default psml page */
    static final String PSML_PAGE_DEFAULT = "psml.page.default";
    
    /** PSML Templates */
    /** PSML Folder Template to copy during new user creation and registration */
    static final String PSML_TEMPLATE_FOLDER = "psml.template.folder";
    
    /** PROFILER **/
    static final String PROFILER_RULE_NAMES_DEFAULT = "profiler.rule.names.default";
    static final String PROFILER_RULE_VALUES_DEFAULT = "profiler.rule.values.default";
    
    /** Registration */ 
    /** Registration default Roles assigned during registration or new user creation **/
    static final String REGISTRATION_ROLES_DEFAULT = "registration.roles.default";
    /** Registration default groups assigned during registration or new user creation **/    
    static final String REGISTRATION_GROUPS_DEFAULT = "registration.groups.default";
    /** Registration default profiling rules assigned during registration or new user creation **/        
    static final String REGISTRATION_RULES_DEFAULT = "registration.rules.default";
    
    /** Users */
    static final String USERS_DEFAULT_ADMIN = "default.admin.user";
    static final String USERS_DEFAULT_GUEST = "default.user.principal";
}
