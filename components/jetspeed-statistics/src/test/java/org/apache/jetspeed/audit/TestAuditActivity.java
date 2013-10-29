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
package org.apache.jetspeed.audit;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.audit.impl.ActivityBean;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Test Audit Activity
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class TestAuditActivity extends DatasourceEnabledSpringTestCase
{

    private AuditActivity audit = null;

    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestAuditActivity.class.getName()});

    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        this.audit = scm.lookupComponent("org.apache.jetspeed.audit.AuditActivity");
        assertNotNull("audit activity service not found ", this.audit);
    }
    
    public void clearDBs()
    {
        try
        {
            Connection con = audit.getDataSource().getConnection();

            PreparedStatement psmt = con
                    .prepareStatement("DELETE FROM ADMIN_ACTIVITY");
            psmt.execute();
            psmt.close();
            psmt = con.prepareStatement("DELETE FROM USER_ACTIVITY");
            psmt.execute();
            psmt.close();
            if (con != null) con.close();
        } catch (SQLException e)
        {
            fail("problem with database connection:" + e.toString());
        }
    }

    public int count(String query)
    {
        int val = -1;
        try
        {
            Connection con = audit.getDataSource().getConnection();

            PreparedStatement psmt = con.prepareStatement(query);
            ResultSet rs = psmt.executeQuery();

            if (rs.next())
            {
                val = rs.getInt(1);
            }
            psmt.close();
            if (con != null) con.close();
        } catch (SQLException e)
        {
            fail("problem with database connection:" + e.toString());
        }
        return val;
    }

    public int countAdminActivity()
    {
        return count("SELECT count(*) from ADMIN_ACTIVITY");
    }

    public int countUserActivity()
    {
        return count("SELECT count(*) from USER_ACTIVITY");
    }
 
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestAuditActivity.class);
    }

    public void testUserActivity() throws Exception
    {
        assertNotNull("Audit Activity service is null", audit);
        clearDBs();

        audit.setEnabled(true);
        assertTrue(audit.getEnabled());
        
        // Log User Activity
        audit.logUserActivity(USER, IP1, AuditActivity.AUTHENTICATION_SUCCESS, MSG_AUTHENTICATION_SUCCESS);
        audit.logUserActivity(USER, IP1, AuditActivity.AUTHENTICATION_FAILURE, MSG_AUTHENTICATION_FAILURE);
        
        int userCount = this.countUserActivity();
        assertEquals(userCount, 2);
        
        ActivityBean userBean = lookupUserActivity(USER_QUERY, AuditActivity.AUTHENTICATION_SUCCESS);
        assertEquals(userBean.getActivity(), AuditActivity.AUTHENTICATION_SUCCESS);
        assertEquals(userBean.getCategory(), AuditActivity.CAT_USER_AUTHENTICATION);
        assertEquals(userBean.getUserName(), USER);
        assertNotNull(userBean.getTimestamp());
        assertEquals(userBean.getIpAddress(), IP1);
        assertEquals(userBean.getDescription(), MSG_AUTHENTICATION_SUCCESS);
        
        userBean = lookupUserActivity(USER_QUERY, AuditActivity.AUTHENTICATION_FAILURE);
        assertEquals(userBean.getActivity(), AuditActivity.AUTHENTICATION_FAILURE);
        assertEquals(userBean.getCategory(), AuditActivity.CAT_USER_AUTHENTICATION);
        assertEquals(userBean.getUserName(), USER);
        assertNotNull(userBean.getTimestamp());
        assertEquals(userBean.getIpAddress(), IP1);
        assertEquals(userBean.getDescription(), MSG_AUTHENTICATION_FAILURE);        

        // Test logging User Attribute activity
        audit.logUserAttributeActivity(USER, IP1, AuditActivity.USER_ADD_ATTRIBUTE, ATTRIBUTE_NAME_1, ATTRIBUTE_VALUE_BEFORE_1, ATTRIBUTE_VALUE_AFTER_1, MSG_ATTRIBUTE);
        
        userBean = lookupUserActivity(USER_QUERY, AuditActivity.USER_ADD_ATTRIBUTE);
        assertEquals(userBean.getActivity(), AuditActivity.USER_ADD_ATTRIBUTE);
        assertEquals(userBean.getCategory(), AuditActivity.CAT_USER_ATTRIBUTE);
        assertEquals(userBean.getUserName(), USER);
        assertNotNull(userBean.getTimestamp());
        assertEquals(userBean.getIpAddress(), IP1);
        assertEquals(userBean.getDescription(), MSG_ATTRIBUTE);        
        assertEquals(userBean.getBeforeValue(), ATTRIBUTE_VALUE_BEFORE_1);
        assertEquals(userBean.getAfterValue(), ATTRIBUTE_VALUE_AFTER_1);
        
        
        // Log Admin Activity
        audit.logAdminUserActivity(ADMIN_USER, IP1, USER, AuditActivity.USER_CREATE, MSG_ADDING_USER);
        audit.logAdminCredentialActivity(ADMIN_USER, IP1, USER, AuditActivity.PASSWORD_CHANGE_SUCCESS, MSG_CHANGING_PW);
        audit.logAdminAttributeActivity(ADMIN_USER, IP1, USER, AuditActivity.USER_ADD_ATTRIBUTE, ATTRIBUTE_NAME_1, ATTRIBUTE_VALUE_BEFORE_1, ATTRIBUTE_VALUE_AFTER_1, MSG_ATTRIBUTE);
        
        int adminCount = this.countAdminActivity();
        assertEquals(adminCount, 3);
        
        ActivityBean adminBean = lookupAdminActivity(ADMIN_QUERY, AuditActivity.USER_CREATE);
        assertEquals(adminBean.getActivity(), AuditActivity.USER_CREATE);
        assertEquals(adminBean.getCategory(), AuditActivity.CAT_ADMIN_USER_MAINTENANCE);
        assertEquals(adminBean.getAdmin(), ADMIN_USER);
        assertEquals(adminBean.getUserName(), USER);
        assertNotNull(adminBean.getTimestamp());
        assertEquals(adminBean.getIpAddress(), IP1);
        assertEquals(adminBean.getDescription(), MSG_ADDING_USER);
        assertTrue(adminBean.getName() == null || adminBean.getName().equals(""));
        assertTrue(adminBean.getBeforeValue() == null || adminBean.getBeforeValue().equals(""));
        assertTrue(adminBean.getAfterValue() == null || adminBean.getAfterValue().equals(""));

        adminBean = lookupAdminActivity(ADMIN_QUERY, AuditActivity.PASSWORD_CHANGE_SUCCESS);
        assertEquals(adminBean.getActivity(), AuditActivity.PASSWORD_CHANGE_SUCCESS);
        assertEquals(adminBean.getCategory(), AuditActivity.CAT_ADMIN_CREDENTIAL_MAINTENANCE);
        assertEquals(adminBean.getAdmin(), ADMIN_USER);
        assertEquals(adminBean.getUserName(), USER);
        assertNotNull(adminBean.getTimestamp());
        assertEquals(adminBean.getIpAddress(), IP1);
        assertEquals(adminBean.getDescription(), MSG_CHANGING_PW);
        assertTrue(adminBean.getName() == null || adminBean.getName().equals(""));
        assertTrue(adminBean.getBeforeValue() == null || adminBean.getBeforeValue().equals(""));
        assertTrue(adminBean.getAfterValue() == null || adminBean.getAfterValue().equals(""));

        adminBean = lookupAdminActivity(ADMIN_QUERY, AuditActivity.USER_ADD_ATTRIBUTE);
        assertEquals(adminBean.getActivity(), AuditActivity.USER_ADD_ATTRIBUTE);
        assertEquals(adminBean.getCategory(), AuditActivity.CAT_ADMIN_ATTRIBUTE_MAINTENANCE);
        assertEquals(adminBean.getAdmin(), ADMIN_USER);
        assertEquals(adminBean.getUserName(), USER);
        assertNotNull(adminBean.getTimestamp());
        assertEquals(adminBean.getIpAddress(), IP1);
        assertEquals(adminBean.getDescription(), MSG_ATTRIBUTE);
        assertEquals(adminBean.getName(), ATTRIBUTE_NAME_1);
        assertEquals(adminBean.getBeforeValue(), ATTRIBUTE_VALUE_BEFORE_1);
        assertEquals(adminBean.getAfterValue(), ATTRIBUTE_VALUE_AFTER_1);
        
        audit.setEnabled(false);
        assertFalse(audit.getEnabled());
        audit.logAdminAttributeActivity(ADMIN_USER, IP1, USER, AuditActivity.USER_ADD_ATTRIBUTE, ATTRIBUTE_NAME_1, ATTRIBUTE_VALUE_BEFORE_1, ATTRIBUTE_VALUE_AFTER_1, MSG_ATTRIBUTE);        
        adminCount = this.countAdminActivity();
        assertEquals(adminCount, 3);        
    }
    
    private static String USER_QUERY = "SELECT * FROM USER_ACTIVITY WHERE ACTIVITY = ?";
    private static String ADMIN_QUERY = "SELECT * FROM ADMIN_ACTIVITY WHERE ACTIVITY = ?";
    
    private static String MSG_AUTHENTICATION_SUCCESS = "logging on via Jetspeed Portal";
    private static String MSG_AUTHENTICATION_FAILURE = "failure logging on via Jetspeed Portal";
    private static String MSG_ADDING_USER = "adding new user";
    private static String MSG_CHANGING_PW = "changing password";
    private static String MSG_ATTRIBUTE = "Attribute added for user";
    
    private static String ADMIN_USER = "admin";
    private static String USER = "nelson";
    private static String IP1 = "123.234.145.156";
    private static String ATTRIBUTE_NAME_1 = "attribute1";
    private static String ATTRIBUTE_VALUE_BEFORE_1 = "value1BEFORE";
    private static String ATTRIBUTE_VALUE_AFTER_1 = "value1AFTER";
    

    private ActivityBean lookupUserActivity(String query, String keyActivity) throws SQLException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;        
        try
        {
            con = audit.getDataSource().getConnection();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, keyActivity);
            rs = pstmt.executeQuery();
            rs.next();
            ActivityBean bean = new ActivityBean();
            bean.setActivity(rs.getString(1));
            bean.setCategory(rs.getString(2));
            bean.setUserName(rs.getString(3));
            bean.setTimestamp(rs.getTimestamp(4));
            bean.setIpAddress(rs.getString(5));
            bean.setName(rs.getString(6));
            bean.setBeforeValue(rs.getString(7));
            bean.setAfterValue(rs.getString(8));            
            bean.setDescription(rs.getString(9));
            return bean;
        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
            if (rs != null)
            {
                rs.close();
            }            
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException ee)
                {}
            }
        }        
    }

    private ActivityBean lookupAdminActivity(String query, String keyActivity) throws SQLException
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            con = audit.getDataSource().getConnection();
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, keyActivity);
            rs = pstmt.executeQuery();
            rs.next();
            ActivityBean bean = new ActivityBean();
            bean.setActivity(rs.getString(1));
            bean.setCategory(rs.getString(2));
            bean.setAdmin(rs.getString(3));
            bean.setUserName(rs.getString(4));
            bean.setTimestamp(rs.getTimestamp(5));
            bean.setIpAddress(rs.getString(6));
            bean.setName(rs.getString(7));
            bean.setBeforeValue(rs.getString(8));
            bean.setAfterValue(rs.getString(9));
            bean.setDescription(rs.getString(10));
            return bean;
        }
        catch (SQLException e)
        {
            throw e;
        }
        finally
        {
            if (pstmt != null)
            {
                pstmt.close();
            }
            if (rs != null)
            {
                rs.close();
            }
            if (con != null)
            {
                try
                {
                    con.close();
                }
                catch (SQLException ee)
                {}
            }
        }        
    }    

    protected Properties getInitProperties()
    {
        Properties props = new Properties();
        try 
        {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("jetspeed.properties");
            if (is != null)
                props.load(is);
        } catch (FileNotFoundException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return props;
    }
    
    protected String[] getConfigurations()
    {
        return new String[]
        { "statistics.xml", "transaction.xml"};
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml"};
    }
    
}