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
package org.apache.jetspeed.profiler;

import junit.framework.Test;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.mockobjects.MockHttpServletRequest;
import org.apache.jetspeed.mockobjects.request.MockRequestContext;
import org.apache.jetspeed.profiler.impl.JetspeedProfilerImpl;
import org.apache.jetspeed.profiler.rules.ProfileResolvers;
import org.apache.jetspeed.profiler.rules.ProfilingRule;
import org.apache.jetspeed.profiler.rules.RuleCriterion;
import org.apache.jetspeed.profiler.rules.impl.RoleFallbackProfilingRule;
import org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule;
import org.apache.jetspeed.request.RequestContext;
import org.apache.jetspeed.security.JetspeedSubjectFactory;
import org.apache.jetspeed.security.PrincipalsSet;
import org.apache.jetspeed.security.SecurityDomain;
import org.apache.jetspeed.security.impl.RoleImpl;
import org.apache.jetspeed.security.impl.SecurityDomainImpl;
import org.apache.jetspeed.security.impl.UserImpl;
import org.apache.jetspeed.security.spi.SecurityDomainAccessManager;
import org.apache.jetspeed.security.spi.SecurityDomainStorageManager;
import org.apache.jetspeed.serializer.JetspeedSerializer;

import javax.security.auth.Subject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * TestProfiler
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestProfiler extends DatasourceEnabledSpringTestCase
{
    private Profiler profiler = null;
    private ProfileResolvers resolvers = null;
    protected SecurityDomainStorageManager domainStorageManager;
    protected SecurityDomainAccessManager domainAccessManager;
    
    protected static final Properties TEST_PROPS = new Properties();

    static
    {
        TEST_PROPS.put("defaultRule", "j1");
        TEST_PROPS.put("anonymousUser", "anon");
        TEST_PROPS.put("locator.impl", "org.apache.jetspeed.profiler.impl.JetspeedProfileLocator");
        TEST_PROPS.put("principalRule.impl", "org.apache.jetspeed.profiler.rules.impl.PrincipalRuleImpl");
        TEST_PROPS.put("profilingRule.impl", "org.apache.jetspeed.profiler.rules.impl.AbstractProfilingRule");
    }

    /**
     * Override the location of the test properties by using the jetspeed properties found in the default package.
     * Make sure to have your unit test copy in jetspeed properties into the class path root like:
     <blockquote><pre>
        &lt;resource&gt;
            &lt;path&gt;conf/jetspeed&lt;/path&gt;
            &lt;include&gt;*.properties&lt;/include&gt;                                        
        &lt;/resource&gt;                                         
     </pre></blockquote>
     */
    @Override    
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
           
    /**
     * Start the tests.
     * 
     * @param args
     *            the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(new String[]
        { TestProfiler.class.getName() });
    }

    
    protected void setUp() throws Exception
    {
        super.setUp();
        
        // Need to ensure required Security Domains are setup.
        domainStorageManager =  scm.lookupComponent(SecurityDomainStorageManager.class.getName());
        domainAccessManager = scm.lookupComponent("org.apache.jetspeed.security.spi.SecurityDomainAccessManager");

        SecurityDomain domain = domainAccessManager.getDomainByName(SecurityDomain.SYSTEM_NAME); 
        if (domain == null){
            
            SecurityDomainImpl newDomain = new SecurityDomainImpl();
            newDomain.setName(SecurityDomain.SYSTEM_NAME);
            domainStorageManager.addDomain(newDomain);
        } 
        domain = domainAccessManager.getDomainByName(SecurityDomain.DEFAULT_NAME); 
        if (domain == null){
            
            SecurityDomainImpl newDomain = new SecurityDomainImpl();
            newDomain.setName(SecurityDomain.DEFAULT_NAME);
            domainStorageManager.addDomain(newDomain);
        }
        
        this.profiler = scm.lookupComponent("profiler");
        JetspeedProfilerImpl profilerImpl = scm.lookupComponent("profilerImpl");
        assertNotNull("profiler not found ", profiler);
        ProfileResolvers resolvers = scm.lookupComponent("ProfileResolvers");
        assertNotNull("resolvers not found ", resolvers);
        profilerImpl.setDefaultRule(JetspeedProfilerImpl.DEFAULT_RULE);
    }

    public static Test suite()
    {
        return createFixturedTestSuite(TestProfiler.class, "firstTestSetup", "lastTestTeardown");
    }

    private static final String DEFAULT_RULE = "j1";

    private static final String FALLBACK_RULE = "role-fallback";

    private static final int EXPECTED_STANDARD = 1;

    private static final int EXPECTED_FALLBACK = 1;

    private static final String DEFAULT_PAGE = "default-page";

    private static final String URF_CRITERIA [] =
    {
        "user",
        "navigation",
        "role",
        "path.session"
    };

    private static final String URCF_CRITERIA [] =
    {
        "user",
        "navigation",
        "rolecombo",
        "path.session"
    };

    
    public void firstTestSetup() throws Exception
    {
        System.out.println("firstTestSetup");
        JetspeedSerializer serializer = scm.lookupComponent("JetspeedSerializer");
        serializer.deleteData();
        serializer.importData(getBaseDir()+"target/test-classes/j2-seed.xml");
    }
    
    public void lastTestTeardown() throws Exception
    {
        System.out.println("lastTestTeardown");
        JetspeedSerializer serializer = scm.lookupComponent("JetspeedSerializer");
        serializer.deleteData();
    }
    
    public void testUserRoleFallback() 
    throws Exception
    {
        assertNotNull("profiler service is null", profiler);
        System.out.println("START: running test user role fallback...");
        
        // make sure rule is set correctly
        ProfilingRule rule = profiler.getRule("user-role-fallback");
        assertNotNull("rule is null ", rule);
        Iterator<RuleCriterion> iterator = rule.getRuleCriteria().iterator();
        int ix = 0;
        while (iterator.hasNext())
        {
            RuleCriterion rc = iterator.next();
            assertTrue("criterion type check " + rc.getType(), rc.getType().equals(URF_CRITERIA[ix]));
            System.out.println(rc.getType());
            ix++;
        }
        
        // test applying it
        RequestContext context = new MockRequestContext();
        Subject subject = createSubject();
        context.setPath("/homepage.psml");        
        context.setSubject(subject);
        ProfileLocator locator = rule.apply(context, profiler);
        System.out.println("locator = " + locator);
        assertTrue("locator string " + locator.toString(), locator.toString().equals("/homepage.psml:user:david:navigation:/:role:ATP:role:NB:role:ATP-NB:page:/homepage.psml"));
        
        System.out.println("COMPLETED: running test user role fallback.");
    }

    public void testUserRoleComboFallback() 
    throws Exception
    {
        assertNotNull("profiler service is null", profiler);
        System.out.println("START: running test user rolecombo fallback...");
        
        // make sure rule is set correctly
        ProfilingRule rule = profiler.getRule("user-rolecombo-fallback");
        assertNotNull("rule is null ", rule);
        Iterator<RuleCriterion> iterator = rule.getRuleCriteria().iterator();
        int ix = 0;
        while (iterator.hasNext())
        {
            RuleCriterion rc = iterator.next();
            assertTrue("criterion type check " + rc.getType(), rc.getType().equals(URCF_CRITERIA[ix]));
            System.out.println(rc.getType());
            ix++;
        }
        
        // test applying it
        RequestContext context = new MockRequestContext();
        Subject subject = createSubject2();
        context.setPath("/homepage.psml");        
        context.setSubject(subject);
        ProfileLocator locator = rule.apply(context, profiler);
        System.out.println("locator = " + locator);                            //     /homepage.psml:user:david:navigation:/:role:ATP:role:NB:page:/homepage.psml
        assertTrue("locator string " + locator.toString(), locator.toString().equals("/homepage.psml:user:david:navigation:/:role:ATP-NB:page:/homepage.psml"));
        
        System.out.println("COMPLETED: running test user role fallback.");
    }
    
    protected Subject createSubject()
    {
        PrincipalsSet principals = new PrincipalsSet();
        Set<Principal> publicCredentials = new HashSet<Principal>();
        Set<Principal> privateCredentials = new HashSet<Principal>();
        
        principals.add(new UserImpl("david"));
        principals.add(new RoleImpl("ATP"));
        principals.add(new RoleImpl("NB"));        
        principals.add(new RoleImpl("ATP-NB"));
        
        Subject subject = new Subject(true, principals, publicCredentials, privateCredentials);        
        return subject;
    }

    protected Subject createSubject2()
    {
        PrincipalsSet principals = new PrincipalsSet();
        Set<Principal> publicCredentials = new HashSet<Principal>();
        Set<Principal> privateCredentials = new HashSet<Principal>();

        principals.add(new UserImpl("david"));
        principals.add(new RoleImpl("ATP"));
        principals.add(new RoleImpl("NB"));
        
        Subject subject = new Subject(true, principals, publicCredentials, privateCredentials);        
        return subject;
    }
    
    /**
     * Tests
     * 
     * @throws Exception
     */
    public void testRules() throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        // Test Default Rule
        ProfilingRule defaultRule = profiler.getDefaultRule();
        assertNotNull("Default profiling rule is null", defaultRule);
        assertTrue("default rule unexpected, = " + defaultRule.getId(), defaultRule.getId().equals(DEFAULT_RULE));
        assertTrue("default rule class not mapped", defaultRule instanceof StandardProfilingRule);

        // Test anonymous principal-rule
        ProfilingRule anonRule = profiler.getRuleForPrincipal(new UserImpl("anon"),
                ProfileLocator.PAGE_LOCATOR);
        assertNotNull("anonymous rule is null", anonRule);
        assertTrue("anonymous rule is j1", anonRule.getId().equals(DEFAULT_RULE));

        // Test Retrieving All Rules
        int standardCount = 0;
        int fallbackCount = 0;
        for (ProfilingRule rule : profiler.getRules())
        {
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
                // assertTrue("Unknown rule encountered: " + rule.getId(),
                // false);
            }

        }
        assertTrue("didnt find expected number of standard rules, expected = " + EXPECTED_STANDARD, standardCount == 1);
        assertTrue("didnt find expected number of fallback rules, expected = " + EXPECTED_FALLBACK, fallbackCount == 1);

    }

    private void checkStandardCriteria(ProfilingRule rule)
    {
        Collection<RuleCriterion> criteriaCollection = rule.getRuleCriteria();
        assertNotNull("Criteria is null", criteriaCollection);
        int count = 0;
        for (RuleCriterion criterion : criteriaCollection)
        {
            assertNotNull("criteria type ", criterion.getType());
            System.out.println("criteria name = " + criterion.getName());
            switch (count)
            {
            case 0:
                assertTrue("criteria name " + criterion.getName(), criterion.getName().equals(
                        ProfilingRule.STANDARD_PAGE));
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
        Collection<RuleCriterion> criteriaCollection = rule.getRuleCriteria();
        assertNotNull("Criteria is null", criteriaCollection);
        int count = 0;
        for (RuleCriterion criterion : criteriaCollection)
        {
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

    public void testStandardRule() throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        RequestContext request = new MockRequestContext("default-other");
        request.setSubject(JetspeedSubjectFactory.createSubject(new UserImpl("anon"), null, null, null));
        request.setLocale(new Locale("en", "US"));
        request.setMediaType("HTML");
        request.setMimeType("text/html");
        Map params = request.getParameterMap();
        params.put("page", "default-other");
        params.put("path", "/sports/football/nfl/chiefs");

        ProfileLocator locator = profiler.getProfile(request, ProfileLocator.PAGE_LOCATOR);
        assertNotNull("rule test on getProfile returned null", locator);
        String path = locator.getLocatorPath();
        System.out.println("locator = " + path);
        assertTrue("locator key value unexpected: " + path, path
                .equals("page:default-other:user:anon:mediatype:HTML:language:en:country:US"));

        // test fallback
        Iterator<ProfileLocatorProperty[]> fallback = locator.iterator();
        int count = 0;
        while (fallback.hasNext())
        {
            ProfileLocatorProperty[] locatorProperties = fallback.next();
            assertTrue("locatorProperties is not null", (locatorProperties != null));
            String locatorPath = locator.getLocatorPath(locatorProperties);
            switch (count)
            {
            case 0:
                assertTrue("locatorPath[0]: " + locatorPath, locatorPath
                        .equals("page:default-other:user:anon:mediatype:HTML:language:en:country:US"));
                break;
            case 1:
                assertTrue("locatorPath[1]: " + locatorPath, locatorPath
                        .equals("page:default-other:user:anon:mediatype:HTML:language:en"));
                break;
            case 2:
                assertTrue("locatorPath[2]: " + locatorPath, locatorPath
                        .equals("page:default-other:user:anon:mediatype:HTML"));
                break;
            case 3:
                assertTrue("locatorPath[3]: " + locatorPath, locatorPath.equals("page:default-other:user:anon"));
                break;

            }
            count++;
            System.out.println("path = " + locatorPath);
        }
        assertTrue("fallback count = 4, " + count, count == 4);

        // create a Simple locator
        RequestContext request2 = new MockRequestContext("/test");
        ProfileLocator locator2 = profiler.createLocator(request2);
        locator2.add("page", "test");
        fallback = locator2.iterator();
        count = 0;
        while (fallback.hasNext())
        {
            ProfileLocatorProperty[] locatorProperties = fallback.next();
            assertTrue("locatorProperties is not null", (locatorProperties != null));
            String locatorPath = locator.getLocatorPath(locatorProperties);
            assertTrue("locatorPath: " + locatorPath, locatorPath.equals("page:test"));

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
            fallback.next();
            count++;
        }
        assertTrue("fallback count = 0, " + count, count == 0);

    }

    public void testPage() throws Exception
    {
        assertNotNull("profiler service is null", profiler);

        RequestContext request = new MockRequestContext();

        request.setSubject(JetspeedSubjectFactory.createSubject(new UserImpl("anon"), null, null, null));
        request.setLocale(new Locale("en", "US"));
        request.setMediaType("HTML");
        request.setMimeType("text/html");

        ProfileLocator locator = profiler.getProfile(request, ProfileLocator.PAGE_LOCATOR);
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

    public void testGetLocatorNames() throws Exception
    {
        assertNotNull("profiler service is null", profiler);
        String[] result = profiler.getLocatorNamesForPrincipal(new UserImpl("guest"));
        for (int ix = 0; ix < result.length; ix++)
        {
            System.out.println("$$$ result = " + result[ix]);
            assertTrue("locator name = " + result[ix], result[ix].equals("page"));
        }
    }

    public void testMaintenance() throws Exception
    {
        System.out.println("Maintenance tests commencing....");
        assertNotNull("profiler service is null", profiler);
        ProfilingRule rule = new StandardProfilingRule(resolvers);
        rule.setClassname("org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule");
        rule.setId("testmo");
        rule.setTitle("The Grand Title");
        profiler.storeProfilingRule(rule);
        ProfilingRule rule2 = profiler.getRule("testmo");
        assertNotNull("rule couldnt be added", rule2);
        assertTrue("rule id bad", rule.getId().equals(rule2.getId()));

        rule2.setTitle("The New Title");
        profiler.storeProfilingRule(rule2);

        ProfilingRule rule3 = profiler.getRule("testmo");
        assertNotNull("rule couldnt be retrieved", rule3);
        assertTrue("rule title is bad", rule3.getTitle().equals(rule2.getTitle()));

        profiler.deleteProfilingRule(rule);
        ProfilingRule rule4 = profiler.getRule("testmo");
        assertNull("rule couldnt be deleted", rule4);

        System.out.println("Maintenance tests completed.");
    }

    protected String[] getConfigurations()
    {
        return new String[] { "profiler.xml", "transaction.xml", "serializer.xml", "security-providers.xml", "cache-test.xml",
                              "capabilities.xml", "registry.xml", "search.xml", "jetspeed-spring.xml", "security-managers.xml",
                              "security-spi.xml", "security-spi-atn.xml", "security-atz.xml", "static-bean-references.xml",
                              "pluto-services.xml",
                              "JETSPEED-INF/spring/JetspeedPrincipalManagerProviderOverride.xml",
                              "JETSPEED-INF/spring/JetspeedPreferencesOverride.xml",
                              "JETSPEED-INF/spring/JetspeedSerializerOverride.xml"};
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return "security,serializer,registry,search,capabilities,profiler,dbSecurity,transaction,cache,noRequestContext,jdbcDS";
    }
    
    protected RuleCriterion addRuleCriterion(ProfilingRule rule,  
                                   String criterionName, String criterionType, String criterionValue,int fallbackOrder, int fallbackType)
    throws Exception
    {
        assertTrue("ProfilingRule is not null", (rule != null));

        
        RuleCriterion c = profiler.createRuleCriterion();
        assertTrue("RuleCriterion is not null", (c != null));
        c.setFallbackOrder(fallbackOrder);
        c.setFallbackType(fallbackType);
        c.setName(criterionName);
        c.setType(criterionType);
        c.setValue(criterionValue);
        c.setRuleId(rule.getId());
        rule.getRuleCriteria().add(c);
        return c;
    }
    
    
 
    private void createStandardCriteria(ProfilingRule rule) throws Exception
    {
        RuleCriterion criterion;
        assertNotNull("ProfilingRule is null", rule);

        for (int count = 0; count < 5; count++)
        {
            switch (count)
            {
            case 0:
                
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_PAGE, "type-" + count, DEFAULT_PAGE, count, RuleCriterion.FALLBACK_STOP);
                assertTrue("criteria name " + criterion.getName(), criterion.getName().equals(
                        ProfilingRule.STANDARD_PAGE));
                assertNotNull("criteria value", criterion.getValue());
                assertTrue("criteria value", criterion.getValue().equals(DEFAULT_PAGE));
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                break;
            case 1:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_USER, "type-" + count, null, count, RuleCriterion.FALLBACK_STOP);
                assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_USER));
                assertNull("criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                break;
            case 2:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_MEDIATYPE, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_MEDIATYPE));
                assertNull("criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            case 3:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_LANGUAGE, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_LANGUAGE));
                assertNull("criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            case 4:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_COUNTRY, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("criteria name", criterion.getName().equals(ProfilingRule.STANDARD_COUNTRY));
                assertNull("criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            }

        }
    }



    
    private void createFallbackCriteria(ProfilingRule rule) throws Exception
    {
        RuleCriterion criterion;
        assertNotNull("ProfilingRule is null", rule);

        for (int count = 0; count < 5; count++)
        {

            switch (count)
            {
            case 0:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_ROLE, "type-" + count, null, count, RuleCriterion.FALLBACK_LOOP);
                assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_ROLE));
                assertNull("fallback criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_LOOP);
                break;
            case 1:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_PAGE, "type-" + count, DEFAULT_PAGE, count, RuleCriterion.FALLBACK_STOP);
                assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_PAGE));
                assertNotNull("fallback criteria value", criterion.getValue());
                assertTrue("fallback criteria value", criterion.getValue().equals(DEFAULT_PAGE));
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_STOP);
                break;
            case 2:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_MEDIATYPE, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_MEDIATYPE));
                assertNull("fallback criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            case 3:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_LANGUAGE, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_LANGUAGE));
                assertNull("fallback criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            case 4:
                criterion = this.addRuleCriterion(rule,ProfilingRule.STANDARD_COUNTRY, "type-" + count, null, count, RuleCriterion.FALLBACK_CONTINUE);
                assertTrue("fallback criteria name", criterion.getName().equals(ProfilingRule.STANDARD_COUNTRY));
                assertNull("fallback criteria value", criterion.getValue());
                assertTrue("fallback type", criterion.getFallbackType() == RuleCriterion.FALLBACK_CONTINUE);
                break;
            }
        }
    }
    
    /**
     * Tests
     * 
     * @throws Exception
     */
    public void testNewRules() throws Exception
    {
        assertNotNull("profiler service is null", profiler);
        String ruleId1 = "j1-test";
        String ruleId2 = "j2-test";
        
        
        // create org.apache.jetspeed.profiler.rules.impl.StandardProfilingRule
        ProfilingRule rule = profiler.createProfilingRule(true);
        assertNotNull("rule is null ", rule);
        rule.setId(ruleId1);
        rule.setTitle("Test Rule 1");
        this.createStandardCriteria(rule);
        
        profiler.storeProfilingRule(rule);
        //Check
        ProfilingRule rule2 = profiler.getRule(ruleId1);
        assertNotNull("default rule couldnt be added", rule2);
        assertTrue("default rule id bad", rule.getId().equals(rule2.getId()));
        
        rule = profiler.createProfilingRule(false);
        assertNotNull("rule is null ", rule);
        rule.setId(ruleId2);
        rule.setTitle("Test Rule 2");
        
        this.createFallbackCriteria(rule);

        profiler.storeProfilingRule(rule);
        //Check
        rule2 = profiler.getRule(ruleId2);
        assertNotNull("fallback rule couldnt be added", rule2);
        assertTrue("fallback rule id bad", rule.getId().equals(rule2.getId()));

        // Test Retrieving All Rules
        int standardCount = 0;
        int fallbackCount = 0;
        for (ProfilingRule profilingRule : profiler.getRules())
        {
            if (profilingRule.getId().equals(ruleId1))
            {
                assertTrue("standard rule class not mapped", profilingRule instanceof StandardProfilingRule);
                checkStandardCriteria(profilingRule);
                standardCount++;
            }
            else if (profilingRule.getId().equals(ruleId2))
            {
                assertTrue("role fallback rule class not mapped", profilingRule instanceof RoleFallbackProfilingRule);
                checkFallbackCriteria(profilingRule);
                fallbackCount++;
            }
            else
            {
                // assertTrue("Unknown rule encountered: " + rule.getId(),
                // false);
            }

        }
        assertTrue("didnt find expected number of standard rules, expected = " + EXPECTED_STANDARD, standardCount == 1);
        assertTrue("didnt find expected number of fallback rules, expected = " + EXPECTED_FALLBACK, fallbackCount == 1);
    }
    
    public void testSubsiteRules()
    {
        RequestContext request = new MockRequestContext("/");
        Set principals = new PrincipalsSet();
        principals.add(new RoleImpl("role"));
        request.setSubject(JetspeedSubjectFactory.createSubject(new UserImpl("user"), null, null, principals));
        request.setLocale(new Locale("en", "US"));
        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        servletRequest.setServerName("www.domain.com");
        request.setRequest(servletRequest);
        try
        {
            ProfilingRule rule = profiler.getRule("subsite-by-hostname");
            assertNotNull(rule);
            ProfileLocator locator = profiler.getProfile(request, rule);
            assertNotNull(locator);
            assertEquals("/", locator.getRequestPath());
            assertEquals("www.domain.com", locator.getRequestServerName());
            // default configuration, (see profiler.xml)
            assertEquals("navigation:subsite-root:hostname:www.domain.com:user:user:navigation-2:subsite-root:hostname:www.domain.com:role:role:path:home", locator.getLocatorPath());
            // 'dot prefix' configuration result, (see profiler.xml)
            //assertEquals("navigation:subsite-root:hostname:www:user:user:navigation-2:subsite-root:hostname:www:role:role:path:home", locator.getLocatorPath());
            // 'hostname to domain mapping' configuration result, (see profiler.xml)
            //assertEquals("navigation:subsite-root:hostname:domain.com:user:user:navigation-2:subsite-root:hostname:domain.com:role:role:path:home", locator.getLocatorPath());
        }
        catch (ProfilerException pe)
        {
            fail("Unexpected ProfilerException: "+pe.getMessage());
        }
    }
}
