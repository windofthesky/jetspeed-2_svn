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

package org.apache.jetspeed.capabilities;

import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.capabilities.impl.JetspeedCapabilities;
import org.apache.jetspeed.components.persistence.store.util.PersistenceSupportedTestCase;

/**
 * Test Capability Service
 *
 * @author <a href="roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class TestCapability extends PersistenceSupportedTestCase
{
    private Capabilities capabilities = null;
    
    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestCapability(String name)
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
        junit.awtui.TestRunner.main(
            new String[] { TestCapability.class.getName()});
    }

    protected void setUp() throws Exception
    {
        super.setUp();               

        capabilities = new JetspeedCapabilities(persistenceStore);        
    }
    
    public static Test suite()
    {
        // All methods starting with "test" will be executed in the test suite.
        return new TestSuite(TestCapability.class);
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

        userAgent = "Opera/7.0";
        System.out.println("Find pattern: " + userAgent);
        CapabilityMap cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        capabilityMapReport(cm);

        userAgent = "Mozilla/4.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
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
