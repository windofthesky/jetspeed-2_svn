/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.profiler;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.PortalContext;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.impl.UserPrincipalImpl;
import org.apache.jetspeed.test.JetspeedTest;

/**
 * TestProfiler
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestProfiler extends JetspeedTest
{
    private ProfilerService service = null;
    
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

    public void setup()
    {
        getService();
    }

    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestProfiler.class);
    }

    protected ProfilerService getService()
    {
        if (service == null)
        {
            service = (ProfilerService) CommonPortletServices.getPortalService(ProfilerService.SERVICE_NAME);
        }
        return service;
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
        service = getService();               
        assertNotNull("profiler service is null", service);
        
        // Test Default Rule        
        ProfilingRule rule = service.getDefaultRule();
        assertNotNull("Default profiling rule is null", rule);
        assertTrue("default rule unexpected, = " + rule.getId(), rule.getId().equals(DEFAULT_RULE));
        assertTrue("default rule class not mapped", rule instanceof StandardProfilingRule);

        // Test anonymous principal-rule
        ProfilingRule anonRule = service.getRuleForPrincipal(new UserPrincipalImpl("anon"));
        assertNotNull("anonymous rule is null", anonRule);
        assertTrue("anonymous rule is j1", anonRule.getId().equals(DEFAULT_RULE));
        
        // Test Retrieving All Rules
        int standardCount = 0;
        int fallbackCount = 0;        
        Iterator rules = service.getRules().iterator();
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
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_PAGE));
                    assertNotNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback criteria value", criterion.getValue().equals(DEFAULT_PAGE));
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                    break;                    
                case 1:
                    assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_ROLE));
                    assertNull("fallback criteria value", criterion.getValue());
                    assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_LOOP);                    
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
        service = getService();               
        assertNotNull("profiler service is null", service);

        PortalContext pc = Jetspeed.getContext();
        RequestContext request = new MockRequestContext(pc);
        
        request.setSubject(createSubject("anon"));
        request.setLocale(new Locale("en", "US"));        
        request.setMediaType("HTML");
        request.setMimeType("text/html");
        Map params = request.getParameterMap();
        params.put("page", "default-other");
        params.put("path", "/sports/football/nfl/chiefs");
        
        ProfileLocator locator = service.getProfile(request);
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
            String locatorPath = (String)fallback.next();
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
        ProfileLocator locator2 = service.createLocator();
        locator2.add("page", "test");
        fallback = locator2.iterator();
        count = 0;
        while (fallback.hasNext())
        {
            String locatorPath = (String)fallback.next();
            assertTrue("locatorPath: " + locatorPath, 
                        locatorPath.equals("page:test"));
            
            System.out.println("Simple Test: path = " + locatorPath);
            count++;            
        }
        assertTrue("fallback count = 1, " + count, count == 1);

        // create an empty locator
        ProfileLocator locator3 = service.createLocator();
        fallback = locator3.iterator();
        count = 0;
        while (fallback.hasNext())
        {
            String locatorPath = (String)fallback.next();
            count++;            
        }
        assertTrue("fallback count = 0, " + count, count == 0);
                
    }
    
    private Subject createSubject(String principalName)
    {
        Principal principal = new UserPrincipalImpl(principalName);
        Set principals = new HashSet();
        principals.add(principal);
        return new Subject(true, principals, new HashSet(), new HashSet());        
    }

    public void testPage() throws Exception
    {
        service = getService();               
        assertNotNull("profiler service is null", service);

        PortalContext pc = Jetspeed.getContext();
        RequestContext request = new MockRequestContext(pc);
    
        request.setSubject(createSubject("anon"));
        request.setLocale(new Locale("en", "US"));        
        request.setMediaType("HTML");
        request.setMimeType("text/html");
        Map params = request.getParameterMap();
        // params.put("page", "default");
    
        ProfileLocator locator = service.getProfile(request);
        assertNotNull("rule test on getProfile returned null", locator);
        System.out.println("page = " + locator.getValue("page"));
        
        Page page = service.getPage(locator);
        assertNotNull("page is null", page);                
    }
   
    public void testPath() throws Exception
    {
        service = getService();               
        assertNotNull("profiler service is null", service);

        PortalContext pc = Jetspeed.getContext();
        RequestContext request = new MockRequestContext(pc, "/football/nfl/chiefs");
        ProfilingRule rule = service.getRule("path");            
        ProfileLocator locator = service.getProfile(request, rule);
        assertNotNull("rule test on getProfile returned null", locator);
        String path = locator.getLocatorPath();
        System.out.println("locator = " + path);
        assertTrue("locator path: " + path, path.equals("path:/football/nfl/chiefs"));
    }
    
}
