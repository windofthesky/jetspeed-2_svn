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
package org.apache.jetspeed.audit.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.jetspeed.audit.AuditActivity;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * <p>
 * Gathers information about security auditing activity  
 * </p>
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class AuditActivityImpl extends PersistenceBrokerDaoSupport implements AuditActivity
{
    protected final static Logger log = LoggerFactory.getLogger(AuditActivityImpl.class);
    
    protected DataSource ds;
    protected String anonymousUser = "guest";
    protected boolean enabled = true;

    public AuditActivityImpl(DataSource dataSource)
    {
        this.ds = dataSource;        
    }
    
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
    
    public boolean getEnabled()
    {
        return this.enabled;
    }
    
    public DataSource getDataSource()
    {
        return ds;
    }
    
    public void logAdminAttributeActivity(String adminName, String ipAddress, String targetUser, String activity, String name, String beforeValue, String afterValue, String description)
    {
        if (enabled)
        {
            logAdminActivity(adminName, ipAddress, targetUser, activity, description, AuditActivity.CAT_ADMIN_ATTRIBUTE_MAINTENANCE, name, beforeValue, afterValue);
        }
    }

    public void logAdminCredentialActivity(String adminName, String ipAddress, String targetUser, String activity, String description)
    {
        if (enabled)
        {
            logAdminActivity(adminName, ipAddress, targetUser, activity, description, AuditActivity.CAT_ADMIN_CREDENTIAL_MAINTENANCE, "", "", "");
        }
    }

    public void logAdminAuthorizationActivity(String adminName, String ipAddress, String targetUser, String activity, String value, String description)
    {
        if (enabled)
        {
            logAdminActivity(adminName, ipAddress, targetUser, activity, description, AuditActivity.CAT_ADMIN_AUTHORIZATION_MAINTENANCE, "", value, "");
        }
    }
    
    public void logAdminUserActivity(String adminName, String ipAddress, String targetUser, String activity, String description)
    {
        if (enabled)
        {
            logAdminActivity(adminName, ipAddress, targetUser, activity, description, AuditActivity.CAT_ADMIN_USER_MAINTENANCE, "", "", "");
        }
    }
    
    public void logAdminRegistryActivity(String adminUser, String ipAddress, String activity, String description)
    {
        if (enabled)
        {
            logAdminActivity(adminUser, ipAddress, "", activity, description, AuditActivity.CAT_ADMIN_REGISTRY_MAINTENANCE, "", "", "");
        }        
    }
    
    
    protected void logAdminActivity(String adminName, String ipAddress, String targetUser, String activity, String description, String category, String name, String beforeValue, String afterValue)
    {
        Connection con = null;
        PreparedStatement stm = null;        
        try
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            con = ds.getConnection();
            stm  = con.prepareStatement("INSERT INTO ADMIN_ACTIVITY (ACTIVITY, CATEGORY, ADMIN, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION) VALUES(?,?,?,?,?,?,?,?,?,?)");
            stm.setString(1, activity);
            stm.setString(2, category);
            stm.setString(3, adminName);
            stm.setString(4, targetUser);
            stm.setTimestamp(5, timestamp);
            stm.setString(6, ipAddress);
            stm.setString(7, name);
            stm.setString(8, beforeValue);
            stm.setString(9, afterValue);
            stm.setString(10, description);            
            stm.execute();            
        } 
        catch (SQLException e)
        {
            log.error(e.getMessage(),e);
        } 
        finally
        {
            try
            {
                if (stm != null) stm.close();
            } 
            catch (SQLException se) 
            {}
            releaseConnection(con);
        }
    }
    
    public void logUserActivity(String userName, String ipAddress, String activity, String description)
    {
        logUserActivities(userName, ipAddress, activity, "", "", "", description, AuditActivity.CAT_USER_AUTHENTICATION);
    }
 
    public void logUserAttributeActivity(String userName, String ipAddress, String activity, String name, String beforeValue, String afterValue, String description)
    {
        logUserActivities(userName, ipAddress, activity, name, beforeValue, afterValue, description, AuditActivity.CAT_USER_ATTRIBUTE);               
    }
    
    protected void logUserActivities(String userName, String ipAddress, String activity, String name, String beforeValue, String afterValue, String description, String category)
    {
        if (enabled)
        {
            Connection con = null;
            PreparedStatement stm = null;        
            try
            {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                con = ds.getConnection();
                stm  = con.prepareStatement("INSERT INTO USER_ACTIVITY (ACTIVITY, CATEGORY, USER_NAME, TIME_STAMP, IPADDRESS, ATTR_NAME, ATTR_VALUE_BEFORE, ATTR_VALUE_AFTER, DESCRIPTION) VALUES(?,?,?,?,?,?,?,?,?)");
                stm.setString(1, activity);
                stm.setString(2, category);
                stm.setString(3, userName);
                stm.setTimestamp(4, timestamp);
                stm.setString(5, ipAddress);
                stm.setString(6, name);
                stm.setString(7, beforeValue);
                stm.setString(8, afterValue);                
                stm.setString(9, description);
                stm.executeUpdate();
            } 
            catch (SQLException e)
            {
                // todo log to standard Jetspeed logger
                e.printStackTrace();
            } 
            finally
            {
                try
                {
                    if (stm != null) stm.close();
                } 
                catch (SQLException se) 
                {}
                releaseConnection(con);
            }
        }
    }    
    
    void releaseConnection(Connection con)
    {
        try
        {
            if (con != null) con.close();
        } catch (SQLException e)
        {
        }
    }
}