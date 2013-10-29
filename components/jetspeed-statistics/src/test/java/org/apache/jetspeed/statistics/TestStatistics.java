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
package org.apache.jetspeed.statistics;

import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import com.mockrunner.mock.web.MockHttpSession;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.portlet.PortletDefinition;
import org.apache.jetspeed.om.portlet.impl.PortletApplicationDefinitionImpl;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.statistics.impl.StatisticsQueryCriteriaImpl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * TestStatistics
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer </a>
 * @version $Id: $
 */
public class TestStatistics extends DatasourceEnabledSpringTestCase
{
	String USERNAME = "anotherFaker";

    private PortalStatistics statistics = null;

    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestStatistics.class.getName()});

    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        this.statistics = scm.lookupComponent("PortalStatistics");
        assertNotNull("statistics not found ", statistics);
    }

    public void clearDBs()
    {
        try
        {
            DatabaseMetaData dmd = statistics.getDataSource().getConnection().getMetaData();        
            System.out.println("Oh... just for reference we're running against "+dmd.getDatabaseProductName());
            System.out.println("with the driver = "+dmd.getDriverName());
            System.out.println("   with the url = "+dmd.getURL());
            
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

    public int count(String query)
    {
        int val = -1;
        try
        {
            Connection con = statistics.getDataSource().getConnection();

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

    public int countPages()
    {
        return count("SELECT count(*) from PAGE_STATISTICS");
    }

    public int countPortlets()
    {
        return count("SELECT count(*) from PORTLET_STATISTICS");
    }

    public int countUsers()
    {
        return count("SELECT count(*) from USER_STATISTICS");
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestStatistics.class);
    }

    public void testPortletStatistics() throws Exception
    {
        System.out.println("testing one of each ");
        statistics.forceFlush();
        clearDBs();

        assertNotNull("statistics service is null", statistics);

        RequestContext request = initRequestContext();
        PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
        app.setName("MyApp");
        PortletDefinition portlet = app.addPortlet("TestPortlet");
        long elapsedTime = 123;
        statistics.logPortletAccess(request, portlet.getUniqueName(), "401",
                elapsedTime);
        statistics.logPageAccess(request, "401", elapsedTime);
        statistics.logUserLogin(request, elapsedTime);

        assertEquals("number of users incorrect", 1, statistics
                .getNumberOfLoggedInUsers());

        List l = statistics.getListOfLoggedInUsers();
        assertNotNull("list returned is null", l);
        assertEquals("wrong number of users in list", 1, l.size());

//        statistics.logUserLogout("123.234.145.156", "SuperFakeyUser",
//                elapsedTime);
        statistics.logUserLogout("123.234.145.156", USERNAME,
                elapsedTime);

        statistics.forceFlush();

        assertEquals("number of users incorrect", statistics
                .getNumberOfLoggedInUsers(), 0);

        int x = 1;
        int pages = this.countPages();
        int users = this.countUsers();
        int portlets = this.countPortlets();
        assertEquals("User Log count incorrect ", 2 * x, users);
        assertEquals("Portlet Log count incorrect ", x, portlets);
        assertEquals("Page Log count incorrect ", x, pages);

    }

    public void testLotsOfPortletStatistics() throws Exception
    {
        System.out.println("testing Multiple portlet stats");
        statistics.forceFlush();
        clearDBs();

        int x = 37;
        assertNotNull("statistics service is null", statistics);
        for (int i = 0; i < x; i++)
        {
            RequestContext request = initRequestContext();
            PortletApplicationDefinitionImpl app = new PortletApplicationDefinitionImpl();
            app.setName("MyApp");
            PortletDefinition portlet = app.addPortlet("TestPortlet");
            long elapsedTime = 123 + i;
            //System.out.println("logging something, number "+i);
            statistics.logPortletAccess(request, portlet.getUniqueName(),
                    "401", elapsedTime);
            statistics.logPageAccess(request, "401", elapsedTime);
            statistics.logUserLogin(request, elapsedTime);
            assertEquals("number of users incorrect", 1, statistics
                    .getNumberOfLoggedInUsers());
            List l = statistics.getListOfLoggedInUsers();
            assertNotNull("list returned is null", l);
            assertEquals("wrong number of users in list", 1, l.size());

//            statistics.logUserLogout("123.234.145.156", "SuperFakeyUser",
//                    elapsedTime);
            statistics.logUserLogout("123.234.145.156", USERNAME,
                    elapsedTime);
            try
            {
                Thread.sleep(200);
            } catch (InterruptedException ie)
            {
            }
        }

        statistics.forceFlush();

        assertEquals("number of users incorrect", statistics
                .getNumberOfLoggedInUsers(), 0);

        int pages = this.countPages();
        int users = this.countUsers();
        int portlets = this.countPortlets();
        assertEquals("User Log count incorrect ", 2 * x, users);
        assertEquals("Portlet Log count incorrect ", x, portlets);
        assertEquals("Page Log count incorrect ", x, pages);

    }

    
    public void testQuerySystem() throws Exception
    {
        System.out.println("testing Query System");
        StatisticsQueryCriteria sqc = new StatisticsQueryCriteriaImpl();
        sqc.setQueryType(PortalStatistics.QUERY_TYPE_USER);
        int desired = 5;
        sqc.setListsize(""+desired);
        sqc.setSorttype("count");
        sqc.setSortorder("desc");
        AggregateStatistics as = statistics.queryStatistics(sqc);
        assertNotNull(as);
        System.out.println("user = " + as);
        int size = as.getStatlist().size();
        assertTrue( (size <=desired));

        sqc.setQueryType(PortalStatistics.QUERY_TYPE_PORTLET);
        sqc.setListsize(""+desired);
        sqc.setSorttype("count");
        sqc.setSortorder("desc");
        as = statistics.queryStatistics(sqc);
        assertNotNull(as);
        System.out.println("portlet = " + as);
        size = as.getStatlist().size();
        assertTrue( (size <=desired));

        sqc.setQueryType(PortalStatistics.QUERY_TYPE_PAGE);
        sqc.setListsize(""+desired);
        sqc.setSorttype("count");
        sqc.setSortorder("desc");
        as = statistics.queryStatistics(sqc);
        assertNotNull(as);
        System.out.println("page = " + as);
        size = as.getStatlist().size();
        assertTrue( (size <=desired));

    }

    private RequestContext initRequestContext()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockHttpSession session = new MockHttpSession();

        request.setUserPrincipal(new Principal(){ public String getName(){ return USERNAME; } });

        request.setRemoteAddr("123.234.145.156");
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
        { "statistics.xml", "transaction.xml", "boot/datasource.xml"};
    }

    protected String[] getBootConfigurations()
    {
        return new String[]
        { "boot/datasource.xml"};
    }
    
}
