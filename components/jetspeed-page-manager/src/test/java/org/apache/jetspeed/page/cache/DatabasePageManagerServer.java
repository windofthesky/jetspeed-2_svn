/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.page.cache;

import org.apache.jetspeed.components.jndi.JetspeedTestJNDIComponent;
import org.apache.jetspeed.components.test.AbstractJexlSpringTestServer;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.PageManagerTestShared;
import org.apache.jetspeed.security.PrincipalsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * DatabasePageManagerServer
 * 
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: $
 */
public class DatabasePageManagerServer extends AbstractJexlSpringTestServer {

    protected static Logger log = LoggerFactory.getLogger(DatabasePageManagerServer.class);
    
    private JetspeedTestJNDIComponent jndiDS;
    private PageManager pageManager;

    private String user;
    private String groups;
    private String roles;
    private Subject userSubject;
    
    @Override
    public void initialize() throws Exception {
        // setup jetspeed test datasource
        jndiDS = new JetspeedTestJNDIComponent();
        jndiDS.setup();

        // initialize component manager and server
        super.initialize();

        // access page manager
        pageManager = scm.lookupComponent("pageManager");

        log.info( "DatabasePageManager server initialized");
    }

    @Override
    protected String getBeanDefinitionFilterCategories() {
        return "default,jdbcDS";
    }

    @Override
    protected String[] getBootConfigurations() {
        return new String[]{"boot/datasource.xml"};
    }

    @Override
    protected String[] getConfigurations() {
        return new String[]{"database-page-manager.xml", "transaction.xml"};
    }

    @Override
    protected Map<String,Object> getContextVars() {
        Map<String,Object> contextVars = new HashMap<String,Object>();
        contextVars.put("pageManager", scm.lookupComponent("pageManager"));
        contextVars.put("pageManagerServer", this);
        return contextVars;
    }

    @Override
    public void terminate() throws Exception {
        // shutdown page manager
        pageManager.shutdown();

        // terminate component manager and server
        super.terminate();

        // tear down test datasource
        jndiDS.tearDown();

        log.info( "DatabasePageManager server terminated");
    }

    @Override
    public Subject getUserSubject() {
        if ((userSubject == null) && (user != null)) {
            Set<Principal> userPrincipals = new PrincipalsSet();
            userPrincipals.add(new PageManagerTestShared.TestUser(user));
            if (groups != null) {
                String [] groupsArray = groups.split(",");
                for (int i = 0; (i < groupsArray.length); i++) {
                    userPrincipals.add(new PageManagerTestShared.TestGroup(groupsArray[i].trim()));
                }
            }
            if (roles != null) {
                String [] rolesArray = roles.split(",");
                for (int i = 0; (i < rolesArray.length); i++) {
                    userPrincipals.add(new PageManagerTestShared.TestRole(rolesArray[i].trim()));
                }
            }
            userSubject = new Subject(true, userPrincipals, new HashSet<Principal>(), new HashSet<Principal>());
        }
        return userSubject;
    }

    /**
     * Get user principal name.
     * 
     * @return user principal name
     */
    public String getUser() {
        return user;
    }
    
    /**
     * Set user principal name.
     * 
     * @param user user principal name
     */
    public void setUser(String user) {
        this.user = user;
        this.userSubject = null;
    }
    
    /**
     * Get user group principal names.
     * 
     * @return CSV list of group principal names
     */
    public String getGroups() {
        return groups;
    }

    /**
     * Set user group principal names.
     * 
     * @param groups CSV list of group principal names
     */
    public void setGroups(String groups) {
        this.groups = groups;
        this.userSubject = null;
    }

    /**
     * Get user role principal names.
     * 
     * @return CSV list of role principal names
     */
    public String getRoles() {
        return roles;
    }

    /**
     * Set user role principal names.
     * 
     * @param roles CSV list of role principal names
     */
    public void setRoles(String roles) {
        this.roles = roles;
        this.userSubject = null;
    }

    /**
     * Server main entry point.
     * 
     * @param args not used
     */
    public static void main(String [] args) {
        Throwable error = (new DatabasePageManagerServer()).run();
        if (error != null) {
            log.error( "Unexpected exception: "+error, error);
        }
    }
}
