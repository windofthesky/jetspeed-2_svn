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
package org.apache.jetspeed.statistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.test.AbstractSpringTestCase;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.request.RequestContext;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;

/**
 * TestStatistics
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */
public class TestStatistics extends AbstractSpringTestCase
{
    private PortalStatistics statistics = null;
    
    /*
     * (non-Javadoc)
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        ctx.close();
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
        { TestStatistics.class.getName() });
        
        
    }

    
    protected void setUp() throws Exception
    {
        super.setUp();
        this.statistics = (PortalStatistics) ctx.getBean("PortalStatistics");
        assertNotNull("statistics not found ", statistics);
    }
    
    public void clearDBs() {
        
        try
        {
            Connection con = statistics.getDataSource().getConnection();

            PreparedStatement psmt = con
                    .prepareStatement("DELETE FROM USER_STATISTICS");
            psmt.execute();
            psmt.close();
            psmt = con.prepareStatement("DELETE FROM PAGE_STATISTICS");
            psmt.execute();
            psmt.close();
            psmt = con.prepareStatement("DELETE FROM PORTLET_STATISTICS");
            psmt.execute();
            psmt.close();
            if (con != null) con.close();
        } catch (SQLException e)
        {
            fail("problem with database connection:" + e.toString());
        }
    }
    
    public int count(String query) {
        int val = -1;
        try
        {
            Connection con = statistics.getDataSource().getConnection();

            PreparedStatement psmt = con
                    .prepareStatement(query);
            ResultSet rs = psmt.executeQuery();
            
            if(rs.next()) {
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
    public int countPages() {
        return count("SELECT count(*) from PAGE_STATISTICS");
    }
        
    public int countPortlets() {
        return count("SELECT count(*) from PORTLET_STATISTICS");
    }
    public int countUsers() {
        return count("SELECT count(*) from USER_STATISTICS");
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestStatistics.class);
    }
    
    public void testPortletStatistics() 
    throws Exception
    {
        statistics.forceFlush();
        clearDBs();
        
        assertNotNull("statistics service is null", statistics);
        
        RequestContext request = initRequestContext();
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("MyApp");
        PortletDefinitionImpl portlet = new PortletDefinitionImpl();
        portlet.setPortletApplicationDefinition(app);
        portlet.setName("TestPortlet");
        portlet.setPortletApplicationDefinition(app);
        long elapsedTime = 123;
        statistics.logPortletAccess(request, portlet.getUniqueName(), "401",elapsedTime);
        statistics.logPageAccess(request, "401",elapsedTime);
        statistics.logUserLogin(request,elapsedTime);
        statistics.logUserLogout(request,elapsedTime);
        
        statistics.forceFlush();
        int x = 1;
        int pages = this.countPages();
        int users = this.countUsers();
        int portlets = this.countPortlets();
        assertEquals("User Log count incorrect ",2*x,users);
        assertEquals("Portlet Log count incorrect ",x,portlets);
        assertEquals("Page Log count incorrect ",x,pages);
        
    }
    
    public void testLotsOfPortletStatistics() 
    throws Exception
    {
        statistics.forceFlush();
        clearDBs();
        
        int x = 37;
        assertNotNull("statistics service is null", statistics);
        for(int i = 0; i < x ; i++) {
            RequestContext request = initRequestContext();
            PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
            app.setName("MyApp");
            PortletDefinitionImpl portlet = new PortletDefinitionImpl();
            portlet.setPortletApplicationDefinition(app);
            portlet.setName("TestPortlet");
            portlet.setPortletApplicationDefinition(app);
            long elapsedTime = 123+i;
            //System.out.println("logging something, number "+i);
            statistics.logPortletAccess(request, portlet.getUniqueName(), "401",elapsedTime);
            statistics.logPageAccess(request, "401",elapsedTime);
            statistics.logUserLogin(request,elapsedTime);
            statistics.logUserLogout(request,elapsedTime);
            try { Thread.sleep(200);} catch(InterruptedException ie) {}
        }
        
        statistics.forceFlush();
        int pages = this.countPages();
        int users = this.countUsers();
        int portlets = this.countPortlets();
        assertEquals("User Log count incorrect ",2*x,users);
        assertEquals("Portlet Log count incorrect ",x,portlets);
        assertEquals("Page Log count incorrect ",x,pages);
        
        
    }

    private RequestContext initRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();

        
        request.setRemoteAddr("192.168.2.3");
        request.setSession(session);
        request.setServerName("www.sporteportal.com");
        request.setScheme("http");
        request.setContextPath("/jetspeed");
        request.setServletPath("/portal");
        request.setPathInfo("/news/default-page.psml");
        request.setRequestURI("/jetspeed/portal/news/default-page.psml");
        request.setMethod("GET");    
        RequestContext rc = new MockRequestContext(request, response);
        return rc;
    }
    
    protected String[] getConfigurations()
    {
        return new String[]{"statistics.xml"};
    }
    
    protected String[] getBootConfigurations()
    {
        return new String[]{"test-repository-datasource-spring.xml"};
    }

}
