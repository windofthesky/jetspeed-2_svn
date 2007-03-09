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
package org.apache.jetspeed.rewriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Attribute;
import org.apache.jetspeed.rewriter.rules.Rule;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.rules.Tag;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;

/**
 * TestRewriterRules
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: TestRewriterController.java,v 1.3 2004/10/13 15:53:22 weaver
 *          Exp $
 */
public class TestRewriterController extends TestCase
{

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestRewriterController(String name)
    {
        super(name);
    }

    public String getBaseProject()
    {
        return "components/jetspeed";
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
        { TestRewriterController.class.getName()});
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestRewriterController.class);
    }

    public void testFactories() throws Exception
    {
        RewriterController component = getController();
        assertNotNull("template component is null", component);

        Rewriter basic = component.createRewriter();
        assertNotNull("basic rewriter is null", basic);
        FileReader reader = getTestReader("test-rewriter-rules.xml");
        Ruleset ruleset = component.loadRuleset(reader);
        assertNotNull("ruleset is null", ruleset);
        RulesetRewriter rewriter = component.createRewriter(ruleset);
        assertNotNull("ruleset rewriter is null", rewriter);
        assertNotNull("ruleset is null", rewriter.getRuleset());
    }

    public void testRules() throws Exception
    {
        RewriterController component = getController();
        assertNotNull("template component is null", component);

        assertNotNull("rewriter component is null", component);
        FileReader reader = getTestReader("test-rewriter-rules.xml");
        Ruleset ruleset = component.loadRuleset(reader);
        assertNotNull("ruleset is null", ruleset);
        assertEquals("ruleset id", "test-set-101", ruleset.getId());
        Iterator rules = ruleset.getRules().iterator();
        assertNotNull("rules is null", rules);

        //
        // test tags
        //                   
        Iterator tags = ruleset.getTags().iterator();
        while (tags.hasNext())
        {
            Tag tag = (Tag) tags.next();
            if (tag.getId().equalsIgnoreCase("FORM"))
            {
                assertFalse("Remove", tag.getRemove());
                Iterator attributes = tag.getAttributes().iterator();
                while (attributes.hasNext())
                {
                    Attribute attribute = (Attribute) attributes.next();
                    assertTrue("attribute is not ACTION", attribute.getId().equals("ACTION"));
                    assertEquals("attribute rule not equal", attribute.getRule().getId(), "merge");
                }
            }
            else if (tag.getId().equalsIgnoreCase("INPUT"))
            {
                assertFalse("Remove", tag.getRemove());
                Iterator attributes = tag.getAttributes().iterator();
                while (attributes.hasNext())
                {
                    Attribute attribute = (Attribute) attributes.next();
                    assertTrue("attribute is not SOURCE", attribute.getId().equals("SOURCE"));
                    assertEquals("attribute rule not equal", attribute.getRule().getId(), "test");
                }

            }
            else if (tag.getId().equalsIgnoreCase("LINK"))
            {
                assertFalse("Remove", tag.getRemove());
                Iterator attributes = tag.getAttributes().iterator();
                while (attributes.hasNext())
                {
                    Attribute attribute = (Attribute) attributes.next();
                    assertTrue("attribute is not HREF", attribute.getId().equals("HREF"));
                    assertEquals("attribute rule not equal", attribute.getRule().getId(), "merge");
                }
            }
            else if (tag.getId().equalsIgnoreCase("HEAD"))
            {
                assertTrue("Remove", tag.getRemove());
                Iterator attributes = tag.getAttributes().iterator();
                while (attributes.hasNext())
                {
                    Attribute attribute = (Attribute) attributes.next();
                }
            }
            else
            {
                assertTrue("tag name unexpected: " + tag.getId(), false);
            }

        }
        assertNotNull("tags is null", tags);

        //
        // test rules
        //           
        while (rules.hasNext())
        {
            Rule rule = (Rule) rules.next();
            assertNotNull("rule is null", rule);
            if (rule.getId().equals("merge"))
            {
                assertEquals("Rule id", rule.getId(), "merge");
                assertTrue("Rule Use Base", rule.getUseBase());
                assertFalse("Rule Popup", rule.getPopup());
                assertEquals("Rule Suffix", rule.getSuffix(), "/web");
            }
            else if (rule.getId().equals("test"))
            {
                assertEquals("Rule id", rule.getId(), "test");
                assertFalse("Rule Use Base", rule.getUseBase());
                assertTrue("Rule Popup", rule.getPopup());
                assertEquals("Rule Suffix", rule.getSuffix(), "/whatever&xxx=1");
            }
            else
            {
                assertTrue("rule name unexpected: " + rule.getId(), false);
            }
        }

    }

    public void testRewriting() throws Exception
    {
        RewriterController component = getController();
        assertNotNull("template component is null", component);

        assertNotNull("rewriter component is null", component);

        FileReader reader = getTestReader("test-remove-rules.xml");

        Ruleset ruleset = component.loadRuleset(reader);
        reader.close();
        assertNotNull("ruleset is null", ruleset);
        RulesetRewriter rewriter = component.createRewriter(ruleset);
        assertNotNull("ruleset rewriter is null", rewriter);
        assertNotNull("ruleset is null", rewriter.getRuleset());

        FileReader htmlReader = getTestReader("test-001.html");
        FileWriter htmlWriter = getTestWriter("test-001-output.html");

        ParserAdaptor adaptor = component.createParserAdaptor("text/html");
        rewriter.setBaseUrl("http://www.rewriter.com");
        rewriter.rewrite(adaptor, htmlReader, htmlWriter);
        htmlWriter.close();
        htmlReader.close();

        // validate result
        FileReader testReader = getTestReader("test-001-output.html");
        UnitTestRewriter testRewriter = new UnitTestRewriter();
        testRewriter.parse(component.createParserAdaptor("text/html"), testReader);
        assertTrue("1st rewritten anchor: " + testRewriter.getAnchorValue("1"), testRewriter.getAnchorValue("1")
                .equals("http://www.bluesunrise.com/suffix"));
        assertTrue("2nd rewritten anchor: " + testRewriter.getAnchorValue("2"), testRewriter.getAnchorValue("2")
                .equals("http://www.rewriter.com/stuff/junk/stuffedjunk.html/suffix"));
        assertTrue("3rd rewritten anchor: " + testRewriter.getAnchorValue("3"), testRewriter.getAnchorValue("3")
                .equals("http://www.rewriter.com/stuff/junk/stuffedjunk.html/suffix"));
        assertTrue("4th rewritten anchor: " + testRewriter.getAnchorValue("4"), testRewriter.getAnchorValue("4")
                .equals("javascript:whatever()"));
        assertTrue("5th rewritten anchor: " + testRewriter.getAnchorValue("5"), testRewriter.getAnchorValue("5")
                .equals("mailto:david@bluesunrise.com"));
        assertTrue("6th rewritten anchor: " + testRewriter.getAnchorValue("6"), testRewriter.getAnchorValue("6")
                .equals("#INTERNAL"));

        assertTrue("Paragraph text: " + testRewriter.getParagraph(), testRewriter.getParagraph().equals(
                "This is a test"));
    }

    /**
     * Gets a reader for a given filename in the test directory.
     * 
     * @return A file reader to the test rules file
     * @throws IOException
     */
    private FileReader getTestReader(String filename) throws IOException
    {
        return new FileReader("test/rewriter/" + filename);
    }

    /**
     * Gets a writer for a given filename in the test directory.
     * 
     * @return A file reader to the test rules file
     * @throws IOException
     */
    private FileWriter getTestWriter(String filename) throws IOException
    {
        String cwd = System.getProperty("user.dir");
        String path;
        return new FileWriter("test/rewriter/" + filename);
    }


    private RewriterController getController() throws Exception
    {
        Class[] rewriterClasses = new Class[]
        { RulesetRewriterImpl.class, RulesetRewriterImpl.class};
        
        //Class[] rewriterClasses = new Class[]
          //     { WebContentRewriter.class, WebContentRewriter.class};
        
        Class[] adaptorClasses = new Class[]
        { SwingParserAdaptor.class, SaxParserAdaptor.class};
        return new JetspeedRewriterController("test/WEB-INF/conf/rewriter-rules-mapping.xml", Arrays
                .asList(rewriterClasses), Arrays.asList(adaptorClasses));
    }
    
    public void XXXtestExternalRewriting() throws Exception
    {
        RewriterController component = getController();
        assertNotNull("template component is null", component);

        assertNotNull("rewriter component is null", component);

        Reader reader = getTestReader("default-rewriter-rules.xml");

        Ruleset ruleset = component.loadRuleset(reader);
        reader.close();
        assertNotNull("ruleset is null", ruleset);
        RulesetRewriter rewriter = component.createRewriter(ruleset);
        assertNotNull("ruleset rewriter is null", rewriter);
        assertNotNull("ruleset is null", rewriter.getRuleset());

        String source = "http://jakarta.apache.org/tomcat/";
        Reader htmlReader = getRemoteReader(source);
        FileWriter htmlWriter = getTestWriter("test-002-output.html");

        ParserAdaptor adaptor = component.createParserAdaptor("text/html");
        
        URL baseURL = new URL(source);
        String baseurl = baseURL.getProtocol() + "://" + baseURL.getHost();        
        
        rewriter.setBaseUrl(baseurl);
        rewriter.rewrite(adaptor, htmlReader, htmlWriter);
        htmlWriter.close();
        htmlReader.close();
    }

    private Reader getRemoteReader(String uri) throws IOException
    {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(uri);
        int status = client.executeMethod(get);
        InputStream is = get.getResponseBodyAsStream();
        return new InputStreamReader(is);
    }
    
}
