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

package org.apache.jetspeed.capability;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.Test;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.Log4jFactory;
import org.apache.jetspeed.components.AbstractComponentAwareTestCase;
import org.apache.jetspeed.components.ComponentAwareTestSuite;
import org.apache.log4j.PropertyConfigurator;
import org.picocontainer.MutablePicoContainer;

/**
 * Test Capability Service
 *
 * @author <a href="roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class TestCapability extends AbstractComponentAwareTestCase
{
    private Capabilities capabilities = null;
    private MutablePicoContainer container;

    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
//    public void overrideProperties(Configuration properties)
//    {
//        super.overrideProperties(properties);
//    }

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestCapability(String name)
    {
        super(name);
    }

    public static final String LOG4J_CONFIG_FILE = "log4j.file";

    /** The default value for the Log4J File */
    public static final String LOG4J_CONFIG_FILE_DEFAULT = "src/webapp/WEB-INF/conf/Log4j.properties";
    
    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[])
    {
        junit.awtui.TestRunner.main(
            new String[] { TestCapability.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        
        System.out.println("MAIN --------------");
        String log4jFile = LOG4J_CONFIG_FILE_DEFAULT;
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(log4jFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        PropertyConfigurator.configure(p);
        
        System.getProperties().setProperty(LogFactory.class.getName(), Log4jFactory.class.getName());
        
        container = (MutablePicoContainer) getContainer();
        capabilities = (Capabilities) container.getComponentInstance(Capabilities.class);        
    }
    
    /**
     * Creates the test suite.
     *
     * @return a test suite (<code>TestSuite</code>) that includes all methods
     *         starting with "test"
     */
    public static Test suite()
    {
        ComponentAwareTestSuite suite = new ComponentAwareTestSuite(TestCapability.class);
        suite.setScript("org/apache/jetspeed/capability/containers/capability-container.groovy");
        return suite;
    }

    /**
     * Tests categories
     * @throws Exception
     */
    public void testCapability() throws Exception
    {
        assertNotNull("capabilities component is null", capabilities);

        // Find specific client -- testing pattern matching
        String userAgent;
        System.out.println("Testing all supported Clients...");
/*
        userAgent = "Opera/7.0";
        System.out.println("Find pattern: " + userAgent);
        CapabilityMap cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);
*/
        userAgent = "Mozilla/4.0";
        System.out.println("Find pattern: " + userAgent);
        CapabilityMap cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

        userAgent = "MSIE 5.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

        userAgent = "Mozilla/5.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

        userAgent = "Lynx";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

        userAgent = "Nokia";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

    }

    private void capabilityMapReport(CapabilityMap cm)
    {
        MediaType mediaType = cm.getPreferredMediaType();
        assertNotNull("Preferred MediaType is null", mediaType);

        MimeType mimeTypeObj = cm.getPreferredType();
        assertNotNull("Preferred MimeType is null", mimeTypeObj);
        String mimeType = mimeTypeObj.getName();

        String encoding = mediaType.getCharacterSet();

        System.out.println("Preferred MediaType = " + mediaType.getName());
        System.out.println("Preferred Mimetype = " + mimeType);
        System.out.println("Encoding = " + encoding);
        System.out.println("Supported MediaTypes");
        Iterator cmIterator = cm.listMediaTypes();

        while (cmIterator.hasNext())
        {
            System.out.println(((MediaType) cmIterator.next()).getName());
        }

        System.out.println("Supported MimeTypes");
        Iterator mtIterator = cm.getMimeTypes();

        while (mtIterator.hasNext())
        {
            System.out.println(((MimeType) mtIterator.next()).getName());
        }
    }

}
