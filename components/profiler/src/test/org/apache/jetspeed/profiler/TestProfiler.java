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
package org.apache.jetspeed.profiler;

import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.profiler.impl.JetspeedProfiler;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.SecurityHelper;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;

/**
 * TestProfiler
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestProfiler extends PersistenceSupportedTestCase
{
    private JetspeedProfiler profiler = null;
    protected static final Properties TEST_PROPS = new Properties();
    
    static
    {         
        TEST_PROPS.put("defaultRule", "j1");
        TEST_PROPS.put("anonymousUser", "anon");
        TEST_PROPS.put("locator.impl", "org.apache.jetspeed.profiler.impl.JetspeedProfileLocator");
        TEST_PROPS.put("principalRule.impl", "org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl");
        TEST_PROPS.put("profilingRule.impl", "org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule");
    }
    
    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
      super.tearDown();
    }
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestProfiler(String name)
    {
        super(name);
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[] { TestProfiler.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        profiler = new JetspeedProfiler(persistenceStore, TEST_PROPS);
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestProfiler.class);
    }

    private static final String DEFAULT_RULE = "j1";
    private static final String FALLBACK_RULE = "role-fallback";
    private static final int EXPECTED_STANDARD = 1;
    private static final int EXPECTED_FALLBACK = 1;
    private static final String DEFAULT_PAGE = "default-page";
        
    /**
     * Tests
     *
     * @throws Exception
     */
    public void testRules() throws Exception
    {
        assertNotNull("profiler service is null", profiler);
        
        // Test Default Rule        
        ProfilingRule rule = profiler.getDefaultRule();
        assertNotNull("Default profiling rule is null", rule);
        assertTrue("default rule unexpected, = " + rule.getId(), rule.getId().equals(DEFAULT_RULE));
        assertTrue("default rule class not mapped", rule instanceof StandardProfilingRule);

        // Test anonymous principal-rule
        ProfilingRule anonRule = profiler.getRuleForPrincipal(new UserPrincipalImpl("anon"));
        assertNotNull("anonymous rule is null", anonRule);
        assertTrue("anonymous rule is j1", anonRule.getId().equals(DEFAULT_RULE));
        
        // Test Retrieving All Rules
        int standardCount = 0;
        int fallbackCount = 0;        
        Iterator rules = profiler.getRules().iterator();
        while (rules.hasNext())
        {
            rule = (ProfilingRule)rules.next();
            if (rule.getId().equals(DEFAULT_RULE))                                       
            {
                assertTrue("standard rule class not mapped", rule instanceof StandardProfilingRule);
                checkStandardCriteria(rule);
                standardCount++;                                 
            }
            else if (rule.getId().equals(FALLBACK_RULE))                                       
            {
                assertTrue("role fallback rule class not mapped", rule instanceof RoleFallbackProfilingRule);
                checkFallbackCriteria(rule);                
                fallbackCount++;             
            }
            else
            {
                // assertTrue("Unknown rule encountered: " + rule.getId(), false);            
            }
                        
        }
        assertTrue("didnt find expected number of standard rules, expected = " + EXPECTED_STANDARD, standardCount == 1);
        assertTrue("didnt find expected number of fallback rules, expected = " + EXPECTED_FALLBACK, fallbackCount == 1);
        
    }    
    
    private void checkStandardCriteria(ProfilingRule rule)
    {
        Collection criteriaCollection = rule.getRuleCriteria();
        assertNotNull("Criteria is null", criteriaCollection);
        Iterator criteria = criteriaCollection.iterator();
        int count = 0;
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            assertNotNull("criteria type ", criterion.getType());
            System.out.println("criteria name = " + criterion.getName());            
            switch (count)
            {
                case 0:
                    assertTrue("criteria name " + criterion.getName(), 
                                criterion.getName().equals(ProfilingRule.STANDARD_PAGE));
                    assertNotNull("criteria value", criterion.getValue());
                    assertTrue("criteria value", criterion.getValue().equals(DEFAULT_PAGE));
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                    break;
                case 1:
                    assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_USER));
                    assertNull("criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                    break;
                case 2:
                    assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_MEDIATYPE));
                    assertNull("criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                    break;
                case 3:
                    assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_LANGUAGE));
                    assertNull("criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                    break;
                case 4:
                    assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_COUNTRY));
                    assertNull("criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                    break;                                    
            }   
            count++;
        }        
    }
    
    private void checkFallbackCriteria(ProfilingRule rule)
    {
        Collection criteriaCollection = rule.getRuleCriteria();
        assertNotNull("Criteria is null", criteriaCollection);
        Iterator criteria = criteriaCollection.iterator();
        int count = 0;
        while (criteria.hasNext())
        {
            RuleCriterion criterion = (RuleCriterion)criteria.next();
            assertNotNull("fallback criteria type", criterion.getType());
            
            switch (count)
            {
                case 0:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_ROLE));
                    assertNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_LOOP);                    
                    break;
                case 1:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_PAGE));
                    assertNotNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback criteria value", criterion.getValue().equals(DEFAULT_PAGE));
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                    break;                                        
                case 2:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_MEDIATYPE));
                    assertNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);                    
                    break;
                case 3:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_LANGUAGE));
                    assertNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                    break;
                case 4:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_COUNTRY));
                    assertNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                    break;                                    
            }
            count++;               
        }        
    }

    public void testStandardRule()
        throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        RequestContext request = new MockRequestContext("default-other");
        
        request.setSubject(SecurityHelper.createSubject("anon"));
        request.setLocale(new Locale("en", "US"));        
        request.setMediaType("HTML");
        request.setMimeType("text/html");
        Map params = request.getParameterMap();
        params.put("page", "default-other");
        params.put("path", "/sports/football/nfl/chiefs");
        
        ProfileLocator locator = profiler.getProfile(request);
        assertNotNull("rule test on getProfile returned null", locator);
        String path = locator.getLocatorPath();
        System.out.println("locator = " + path);        
        assertTrue("locator key value unexpected: " + path, 
                    path.equals(
              "page:default-other:user:anon:mediatype:HTML:language:en:country:US"));

        // test fallback
        Iterator fallback = locator.iterator();
        int count = 0;
        while (fallback.hasNext())
        {
            ProfileLocatorProperty [] locatorProperties = (ProfileLocatorProperty [])fallback.next();
            assertTrue("locatorProperties is not null", (locatorProperties != null));
            String locatorPath = locator.getLocatorPath(locatorProperties);
            switch (count)
            {
                case 0:
                    assertTrue("locatorPath[0]: " + locatorPath, 
                                locatorPath.equals(
                        "page:default-other:user:anon:mediatype:HTML:language:en:country:US"));
                    break;
                case 1:
                    assertTrue("locatorPath[1]: " + locatorPath, 
                                locatorPath.equals(
                         "page:default-other:user:anon:mediatype:HTML:language:en"));
                    break;                
                case 2:
                    assertTrue("locatorPath[2]: " + locatorPath, 
                                locatorPath.equals("page:default-other:user:anon:mediatype:HTML"));
                    break;                
                case 3:
                    assertTrue("locatorPath[3]: " + locatorPath, 
                                locatorPath.equals("page:default-other:user:anon"));
                    break;                
                
            }
            count++;
            System.out.println("path = " + locatorPath);                             
        }
        assertTrue("fallback count = 4, " + count, count == 4);
        
        // create a simple locator
        RequestContext request2 = new MockRequestContext("/test");
        ProfileLocator locator2 = profiler.createLocator(request2);
        locator2.add("page", "test");
        fallback = locator2.iterator();
        count = 0;
        while (fallback.hasNext())
        {
            ProfileLocatorProperty [] locatorProperties = (ProfileLocatorProperty [])fallback.next();
            assertTrue("locatorProperties is not null", (locatorProperties != null));
            String locatorPath = locator.getLocatorPath(locatorProperties);
            assertTrue("locatorPath: " + locatorPath, 
                        locatorPath.equals("page:test"));
            
            System.out.println("Simple Test: path = " + locatorPath);
            count++;            
        }
        assertTrue("fallback count = 1, " + count, count == 1);

        // create an empty locator
        RequestContext request3 = new MockRequestContext("/");
        ProfileLocator locator3 = profiler.createLocator(request3);
        fallback = locator3.iterator();
        count = 0;
        while (fallback.hasNext())
        {
            ProfileLocatorProperty [] locatorProperties = (ProfileLocatorProperty [])fallback.next();
            count++;            
        }
        assertTrue("fallback count = 0, " + count, count == 0);
                
    }

    public void testPage() throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        RequestContext request = new MockRequestContext();
    
        request.setSubject(SecurityHelper.createSubject("anon"));
        request.setLocale(new Locale("en", "US"));        
        request.setMediaType("HTML");
        request.setMimeType("text/html");
        Map params = request.getParameterMap();
        // params.put("page", "default");
    
        ProfileLocator locator = profiler.getProfile(request);
        assertNotNull("rule test on getProfile returned null", locator);
        System.out.println("page = " + locator.getValue("page"));
    }
   
    public void testPath() throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        RequestContext request = new MockRequestContext("/football/nfl/chiefs");
        ProfilingRule rule = profiler.getRule("path");            
        ProfileLocator locator = profiler.getProfile(request, rule);
        assertNotNull("rule test on getProfile returned null", locator);
        String path = locator.getLocatorPath();
        System.out.println("locator = " + path);
        assertTrue("locator path: " + path, path.equals("path:/football/nfl/chiefs"));
    }
    
}
