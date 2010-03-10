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
package org.apache.jetspeed.security.mapping.ldap.setup2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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

/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public abstract class AbstractSetup2LDAPTest extends AbstractLDAPTest
{

    protected Set<AttributeDef> userAttrDefs;

    protected Set<AttributeDef> roleAttrDefs;

    protected Set<AttributeDef> groupAttrDefs;

    protected AttributeBasedRelationDAO hasRoleDAO;

    protected AttributeBasedRelationDAO isMemberOfGroupDAO;

    public Resource[] initializationData()
    {
        final ClassPathResource ldapPersonInfo = new ClassPathResource(
                "resources/setup2.ldif", getClass());
        return new Resource[]
        { ldapPersonInfo};
    }

    @Override
    public void internalSetUp() throws Exception
    {

        Set<AttributeDef> basicAttrDefs = new HashSet<AttributeDef>();
        basicAttrDefs.add(UID_DEF);
        basicAttrDefs.add(CN_DEF);

        // setting up user DAO
        userAttrDefs = new HashSet<AttributeDef>();
        userAttrDefs.addAll(basicAttrDefs);
        userAttrDefs.add(GIVEN_NAME_DEF);

        userSearchConfig = new LDAPEntityDAOConfiguration();
        userSearchConfig.setBaseDN("o=sevenSeas");
        userSearchConfig.setSearchDN("");
        userSearchConfig.setSearchFilter(new SimpleFilter(
                "(objectClass=inetOrgPerson)"));
        userSearchConfig.setLdapIdAttribute("uid");
        userSearchConfig.setAttributeDefinitions(userAttrDefs);
        userSearchConfig.setEntityType("user");
        userSearchConfig.setObjectClasses("inetOrgPerson,organizationalPerson,person,top");

        SpringLDAPEntityDAO userDAO = new SpringLDAPEntityDAO(userSearchConfig);
        userDAO.setLdapTemplate(ldapTemplate);

        // setting up role DAO

        roleAttrDefs = new HashSet<AttributeDef>();
        roleAttrDefs.addAll(basicAttrDefs);
        roleAttrDefs.add(DESCRIPTION_ATTR_DEF);

        LDAPEntityDAOConfiguration roleSearchConfig = new LDAPEntityDAOConfiguration();
        roleSearchConfig.setBaseDN("o=sevenSeas");
        roleSearchConfig.setSearchDN("ou=Roles,o=Jetspeed");
        roleSearchConfig.setSearchFilter(new SimpleFilter(
                "(objectClass=groupOfUniqueNames)"));
        roleSearchConfig.setLdapIdAttribute("cn");
        roleSearchConfig.setAttributeDefinitions(roleAttrDefs);
        roleSearchConfig.setEntityType("role");
        roleSearchConfig.setObjectClasses("groupOfUniqueNames,extensibleObject");

        SpringLDAPEntityDAO roleDAO = new SpringLDAPEntityDAO(roleSearchConfig);
        roleDAO.setLdapTemplate(ldapTemplate);

        groupAttrDefs = new HashSet<AttributeDef>();
        groupAttrDefs.addAll(basicAttrDefs);
        groupAttrDefs.add(DESCRIPTION_ATTR_DEF);
        groupAttrDefs.add(UNIQUEMEMBER_ATTR_DEF);

        LDAPEntityDAOConfiguration groupSearchConfig = new LDAPEntityDAOConfiguration();
        groupSearchConfig.setBaseDN("o=sevenSeas");
        groupSearchConfig.setSearchDN("ou=Groups,o=Jetspeed");
        groupSearchConfig.setSearchFilter(new SimpleFilter(
                "(objectClass=groupOfUniqueNames)"));
        groupSearchConfig.setLdapIdAttribute("cn");
        groupSearchConfig.setAttributeDefinitions(groupAttrDefs);
        groupSearchConfig.setEntityType("group");
        groupSearchConfig.setObjectClasses("groupOfUniqueNames,extensibleObject");
        
        SpringLDAPEntityDAO groupDAO = new SpringLDAPEntityDAO(groupSearchConfig);
        groupDAO.setLdapTemplate(ldapTemplate);

        Map<String, EntityDAO> daos = new HashMap<String, EntityDAO>();
        daos.put("user", userDAO);
        daos.put("role", roleDAO);

        entityManager = new DefaultLDAPEntityManager();
        entityManager.setEntityDAOs(daos);
        daos.put("user", userDAO);
        daos.put("role", roleDAO);
        daos.put("group", groupDAO);

        entityManager = new DefaultLDAPEntityManager();
        entityManager.setEntityDAOs(daos);

        // relation DAOs
        Collection<EntityRelationDAO> relationDaos = new ArrayList<EntityRelationDAO>();

        // hasRole relation DAO
        // use attribute on from entity (of "user" type); user IDs are stored
        // in the "j2-role" attribute
        hasRoleDAO = new AttributeBasedRelationDAO();
        hasRoleDAO.setRelationAttribute("uniqueMember");
        hasRoleDAO.setUseFromEntityAttribute(false);
        hasRoleDAO.setRelationType(new SecurityEntityRelationTypeImpl("hasRole","user","role"));
        hasRoleDAO.setAttributeContainsInternalId(true);
        relationDaos.add(hasRoleDAO);

        entityManager.setEntityRelationDAOs(relationDaos);

    }

    @Override
    protected void internaltearDown() throws Exception
    {
        // TODO Auto-generated method stub

    }

}