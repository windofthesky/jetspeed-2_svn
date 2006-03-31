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
package org.apache.jetspeed.rewriter;

import java.io.BufferedInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Arrays;

import javax.portlet.PortletException;

import org.w3c.dom.Node;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLInputSource;

import org.cyberneko.html.HTMLConfiguration ;
import org.cyberneko.html.parsers.DOMParser;
import org.cyberneko.html.parsers.SAXParser;
import org.cyberneko.html.filters.Purifier ;
import org.cyberneko.html.filters.Writer ;

import org.apache.jetspeed.rewriter.html.SwingParserAdaptor;
import org.apache.jetspeed.rewriter.html.neko.NekoParserAdaptor;
import org.apache.jetspeed.rewriter.rules.Ruleset;
import org.apache.jetspeed.rewriter.xml.SaxParserAdaptor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * TestNekoRewriter
 * 
 * @author <a href="mailto:dyoung@phase2systems.com">David L Young</a>
 * @version $Id:$
 */
public class TestNekoRewriter extends TestCase
{

    /**
     * Defines the testcase name for JUnit.
     * 
     * @param name
     *            the testcase's name.
     */
    public TestNekoRewriter(String name)
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
        { TestNekoRewriter.class.getName()});
    }

    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestNekoRewriter.class);
    }
    
    
    // DOMParser example
    
    /* BOZO

    public void testDOM() throws Exception
    {
        System.out.println( "testing...DOM" ) ;

        // parse something and echo the DOM tree
        String target = "http://www.google.com" ;
        System.out.println( "Parsing: " + target ) ;
        
        DOMParser parser = new DOMParser() ;
        parser.parse( target ) ;
        System.out.println( "parse() result..." ) ;
        print( parser.getDocument(), "" ) ;
    }

    void print( Node node, String indent )
    {
        System.out.println(indent+node.getClass().getName());
        Node child = node.getFirstChild();
        while (child != null) {
            print(child, indent+" ");
            child = child.getNextSibling();
        }
    }
    */
    
    
    // SAXParser example
    
    /* BOZO

    public void testSAX() throws Exception
    {
        System.out.println( "testing...SAX" ) ;

        // parse something to stdout
        String target = "http://www.google.com" ;
        System.out.println( "Parsing: " + target ) ;
        
        SAXParser parser = new SAXParser() ;

        // create pipeline filters
        org.cyberneko.html.filters.Writer writer = new org.cyberneko.html.filters.Writer();
        Purifier purifier = new Purifier() ;

        // setup filter chain
        XMLDocumentFilter[] filters = {
            purifier,
            writer,
        };

        parser.setProperty("http://cyberneko.org/html/properties/filters", filters);

        // parse documents
        XMLInputSource source = new XMLInputSource(null, target, null);
        parser.parse(source);
    }
    */
    
    
    
    // NekoParserAdapter test
    
    public void testNekoParserAdaptor() throws Exception
    {
        RewriterController controller = getController();
        FileReader rulesReader = getTestReader("test-remove-rules.xml");
        Ruleset ruleset = controller.loadRuleset(rulesReader);
        rulesReader.close();
        assertNotNull("ruleset is null", ruleset);
        RulesetRewriter rewriter = controller.createRewriter(ruleset);
        assertNotNull("ruleset rewriter is null", rewriter);
        
        FileReader htmlReader = getTestReader("test-001.html");
        FileWriter htmlWriter = getTestWriter("test-002-output.html");

        ParserAdaptor adaptor = controller.createParserAdaptor("text/html");
        rewriter.setBaseUrl("http://www.rewriter.com");
        rewriter.rewrite(adaptor, htmlReader, htmlWriter);
        htmlReader.close();
    }
    
    private RewriterController getController() throws Exception
    {
        Class[] rewriterClasses = new Class[]{ RulesetRewriterImpl.class, RulesetRewriterImpl.class};
        
        Class[] adaptorClasses = new Class[]{ NekoParserAdaptor.class, SaxParserAdaptor.class};
        return new JetspeedRewriterController("test/WEB-INF/conf/rewriter-rules-mapping.xml", Arrays.asList(rewriterClasses), Arrays.asList(adaptorClasses));
    }

    private Reader getRemoteReader(String uri) throws IOException
    {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(uri);
        int status = client.executeMethod(get);
        BufferedInputStream bis = new BufferedInputStream(get.getResponseBodyAsStream());
        String encoding = get.getResponseCharSet();
        return new InputStreamReader(bis, encoding);
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
        return new FileWriter("test/rewriter/" + filename);
    }
    
    
    /* BOZO
    public void testNekoWebTarget() throws Exception
    {        
        // parse something with the NekoParserAdaptor
        String target = "http://www.google.com";
                
        RewriterController controller = getController();
        FileReader rulesReader = getTestReader("test-remove-rules.xml");
        Ruleset ruleset = controller.loadRuleset(rulesReader);
        rulesReader.close();
        assertNotNull("ruleset is null", ruleset);
        RulesetRewriter rewriter = controller.createRewriter(ruleset);
        assertNotNull("ruleset rewriter is null", rewriter);

        java.io.Reader htmlReader = getRemoteReader(target);
        java.io.Writer htmlWriter = new OutputStreamWriter(System.out);

        ParserAdaptor adaptor = controller.createParserAdaptor("text/html");
        rewriter.setBaseUrl("http://www.rewriter.com");
        rewriter.rewrite(adaptor, htmlReader, htmlWriter);
        htmlReader.close();
    }
    */
}
