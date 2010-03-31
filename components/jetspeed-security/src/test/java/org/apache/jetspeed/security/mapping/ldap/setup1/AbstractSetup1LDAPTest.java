/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.security.mapping.ldap.setup1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.jetspeed.security.mapping.impl.SecurityEntityRelationTypeImpl;
import org.apache.jetspeed.security.mapping.ldap.AbstractLDAPTest;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultLDAPEntityManager;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.LDAPEntityDAOConfiguration;
import org.apache.jetspeed.security.mapping.ldap.dao.impl.AttributeBasedRelationDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.impl.SpringLDAPEntityDAO;
import org.apache.jetspeed.security.mapping.ldap.filter.SimpleFilter;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class AbstractSetup1LDAPTest extends AbstractLDAPTest
{

    protected Map<String, AttributeDef> userAttrDefs;

    protected Map<String, AttributeDef> roleAttrDefs;

    protected AttributeBasedRelationDAO hasRoleDAO;

    protected SpringLDAPEntityDAO userDAO;
    
    protected SpringLDAPEntityDAO roleDAO;
    
    public Resource[] initializationData()
    {
        final ClassPathResource ldapPersonInfo = new ClassPathResource(
                "resources/setup1.ldif", getClass());
        return new Resource[]
        { ldapPersonInfo};
    }

    @Override
    public void internalSetUp() throws Exception
    {
        Map<String, AttributeDef> basicAttrDefs = new HashMap<String, AttributeDef>();
        basicAttrDefs.put(UID_DEF.getName(), UID_DEF);
        basicAttrDefs.put(CN_DEF.getName(), CN_DEF);

        // setting up user DAO
        userAttrDefs = new HashMap<String, AttributeDef>();
        userAttrDefs.putAll(basicAttrDefs);
        userAttrDefs.put(GIVEN_NAME_DEF.getName(), GIVEN_NAME_DEF);
        userAttrDefs.put(SN_DEF.getName(), SN_DEF);

        userSearchConfig = new LDAPEntityDAOConfiguration();
        userSearchConfig.setLdapBase("o=sevenSeas");
        userSearchConfig.setSearchBase("");
        userSearchConfig
                .setSearchFilter(new SimpleFilter("(objectClass=person)"));
        userSearchConfig.setLdapIdAttribute("cn");
        userSearchConfig.setAttributeDefinitions(userAttrDefs.values());
        userSearchConfig.setEntityType("user");
        userSearchConfig.setObjectClasses("inetOrgPerson,");

        userDAO = new SpringLDAPEntityDAO(userSearchConfig);
        userDAO.setLdapTemplate(ldapTemplate);

        // setting up role DAO

        roleAttrDefs = new HashMap<String, AttributeDef>();
        roleAttrDefs.putAll(basicAttrDefs);
        roleAttrDefs.put(DESCRIPTION_ATTR_DEF.getName(), DESCRIPTION_ATTR_DEF);
        roleAttrDefs.put(UNIQUEMEMBER_ATTR_DEF.getName(), UNIQUEMEMBER_ATTR_DEF);

        LDAPEntityDAOConfiguration roleSearchConfig = new LDAPEntityDAOConfiguration();
        roleSearchConfig.setLdapBase("o=sevenSeas");
        roleSearchConfig.setSearchBase("");
        roleSearchConfig.setSearchFilter(new SimpleFilter(
                "(objectClass=groupOfUniqueNames)"));
        roleSearchConfig.setLdapIdAttribute("cn");
        roleSearchConfig.setAttributeDefinitions(roleAttrDefs.values());
        roleSearchConfig.setEntityType("role");

        roleDAO = new SpringLDAPEntityDAO(roleSearchConfig);
        roleDAO.setLdapTemplate(ldapTemplate);

        ArrayList<EntityDAO> daos = new ArrayList<EntityDAO>();
        daos.add(userDAO);
        daos.add(roleDAO);
        // relation DAOs
        ArrayList<EntityRelationDAO> relationDaos = new ArrayList<EntityRelationDAO>();

        // hasRole relation DAO
        hasRoleDAO = new AttributeBasedRelationDAO();
        hasRoleDAO.setRelationAttribute("uniqueMember");
        // use attribute on target entity (of "role" type); user IDs are stored
        // in a multi-valued attribute on roles.
        hasRoleDAO.setUseFromEntityAttribute(false);
        hasRoleDAO.setRelationType(new SecurityEntityRelationTypeImpl("hasRole","user","role"));
        hasRoleDAO.setAttributeContainsInternalId(true);
        relationDaos.add(hasRoleDAO);

        entityManager = new DefaultLDAPEntityManager(daos, relationDaos);
    }

    @Override
    protected void internaltearDown() throws Exception
    {
        // TODO Auto-generated method stub

    }

}
