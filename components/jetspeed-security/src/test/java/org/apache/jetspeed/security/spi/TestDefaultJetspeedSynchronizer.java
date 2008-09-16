package org.apache.jetspeed.security.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.jetspeed.security.JetspeedPrincipalAssociationType;
import org.apache.jetspeed.security.JetspeedPrincipalType;
import org.apache.jetspeed.security.mapping.SecurityEntityManager;
import org.apache.jetspeed.security.mapping.impl.SecurityEntityRelationTypeImpl;
import org.apache.jetspeed.security.mapping.ldap.dao.DefaultLDAPEntityManager;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityDAO;
import org.apache.jetspeed.security.mapping.ldap.dao.EntityRelationDAO;
import org.apache.jetspeed.security.mapping.model.AttributeDef;
import org.apache.jetspeed.security.mapping.model.Entity;
import org.apache.jetspeed.security.mapping.model.impl.AttributeDefImpl;
import org.apache.jetspeed.security.mapping.stubs.StubEntityDAO;
import org.apache.jetspeed.security.mapping.stubs.StubEntityFactory;
import org.apache.jetspeed.security.mapping.stubs.StubEntityRelationDAO;


public class TestDefaultJetspeedSynchronizer extends TestCase
{

    private SecurityEntityManager entityManager;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        Collection<AttributeDef> attrDefs = new ArrayList<AttributeDef>();  
        attrDefs.add(new AttributeDefImpl("fullname"));
        attrDefs.add(new AttributeDefImpl("company"));        
        
        StubEntityFactory stubFactory = new StubEntityFactory();
        stubFactory.setAttributeDefs(attrDefs);
        
        Entity user_jsmith=stubFactory.createEntity("jsmith", "user", new String[]{"John Smith","IBM"});
        Entity user_jdoe=stubFactory.createEntity("jdoe", "user", new String[]{"Jane Doe","Apple"});

        StubEntityDAO userDao = new StubEntityDAO();
        userDao.addEntity(user_jsmith);
        userDao.addEntity(user_jdoe);
        
        Entity role_manager=stubFactory.createEntity("manager", "role", new String[]{"Manager Role",""});
        Entity role_admin=stubFactory.createEntity("admin", "role", new String[]{"Admin Role",""});
        Entity role_random=stubFactory.createEntity("random", "role", new String[]{"Random Role",""});
        Entity role_yetAnother=stubFactory.createEntity("yetAnotherRole", "role", new String[]{"Yet Another Role",""});
        
        StubEntityDAO roleDao = new StubEntityDAO();
        userDao.addEntity(role_manager);
        userDao.addEntity(role_admin);
        userDao.addEntity(role_random);
        userDao.addEntity(role_yetAnother);

        
        Entity group_programmers=stubFactory.createEntity("programmers", "group", new String[]{"Group for Programmers",""});
        Entity group_board=stubFactory.createEntity("boardOfDirectors", "group", new String[]{"Group for the board of directors",""});
        Entity group_random=stubFactory.createEntity("randomGroup", "group", new String[]{"Random Group",""});
        Entity group_yetAnother=stubFactory.createEntity("yetAnotherGroup", "group", new String[]{"Yet Another Group",""});

        StubEntityDAO groupDao = new StubEntityDAO();
        userDao.addEntity(group_programmers);
        userDao.addEntity(group_board);
        userDao.addEntity(group_random);
        userDao.addEntity(group_yetAnother);

        StubEntityRelationDAO userToRole = new StubEntityRelationDAO(new SecurityEntityRelationTypeImpl(
                JetspeedPrincipalAssociationType.IS_MEMBER_OF_ASSOCIATION_TYPE_NAME,
                JetspeedPrincipalType.USER_TYPE_NAME,
                JetspeedPrincipalType.ROLE_TYPE_NAME));
        userToRole.relate(null, null, user_jsmith, role_manager);
        userToRole.relate(null, null, user_jsmith, role_random);
        userToRole.relate(null, null, user_jdoe, role_manager);
        userToRole.relate(null, null, user_jdoe, role_random);
        userToRole.relate(null, null, user_jdoe, role_admin);

        StubEntityRelationDAO userToGroup = new StubEntityRelationDAO(new SecurityEntityRelationTypeImpl(
                JetspeedPrincipalAssociationType.IS_CHILD_OF_ASSOCIATION_TYPE_NAME,
                JetspeedPrincipalType.USER_TYPE_NAME,
                JetspeedPrincipalType.GROUP_TYPE_NAME));
        userToGroup.relate(null, null, user_jsmith, group_programmers);
        userToGroup.relate(null, null, user_jsmith, group_random);
        userToGroup.relate(null, null, user_jdoe, group_board);
        userToGroup.relate(null, null, user_jdoe, group_yetAnother);
        userToGroup.relate(null, null, user_jdoe, group_random);
        
        StubEntityRelationDAO groupToRole = new StubEntityRelationDAO(new SecurityEntityRelationTypeImpl(
                JetspeedPrincipalAssociationType.IS_CHILD_OF_ASSOCIATION_TYPE_NAME,
                JetspeedPrincipalType.GROUP_TYPE_NAME,
                JetspeedPrincipalType.ROLE_TYPE_NAME));
        
        groupToRole.relate(null, null, group_board, role_manager);
        groupToRole.relate(null, null, group_programmers, role_yetAnother);
        
        Map<String,EntityDAO> entityDAOs = new HashMap<String,EntityDAO>();
        entityDAOs.put(JetspeedPrincipalType.USER_TYPE_NAME,userDao);
        entityDAOs.put(JetspeedPrincipalType.ROLE_TYPE_NAME,roleDao);
        entityDAOs.put(JetspeedPrincipalType.USER_TYPE_NAME,groupDao);

        Collection<EntityRelationDAO> entityRelationDAOs = new ArrayList<EntityRelationDAO>();
        entityRelationDAOs.add(userToRole);
        entityRelationDAOs.add(userToGroup);
        entityRelationDAOs.add(groupToRole);

        DefaultLDAPEntityManager entityMan = new DefaultLDAPEntityManager();
        entityMan.setEntityDAOs(entityDAOs);
        entityMan.setEntityRelationDAOs(entityRelationDAOs);
        
        
        
    }

    @Override
    protected void tearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.tearDown();
    }

    
}
