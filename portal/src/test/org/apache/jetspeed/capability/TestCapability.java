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


import junit.framework.Test;
import junit.framework.TestSuite;
import org.apache.commons.configuration.Configuration;
import org.apache.jetspeed.cps.CommonPortletServices;
import org.apache.jetspeed.test.JetspeedTest;

/**
 * Test Capability Service
 *
 * @author <a href="roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class TestCapability extends JetspeedTest 
{    
    
    /**
     * @see org.apache.jetspeed.test.JetspeedTest#overrideProperties(org.apache.commons.configuration.Configuration)
     */
    public void overrideProperties(Configuration properties)
    {
        super.overrideProperties(properties);
    }
    
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
         junit.awtui.TestRunner.main(new String[] { TestCapability.class.getName()});
    }
    
    public void setup()
    {
        System.out.println("Setup: Testing Capability Service");
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
        return new TestSuite(TestCapability.class);
    }
    
    protected CapabilityService getService()
    {
        return (CapabilityService) CommonPortletServices.getPortalService(CapabilityService.SERVICE_NAME);
    }
    
    /**
     * Tests categories
     * @throws Exception
     */
    public void testCapability() throws Exception
    {
        CapabilityService service = getService();               
        assertNotNull("capability service is null", service);

        // Find specific client -- testing pattern matching
        String userAgent;
        System.out.println("Test pattern matching...")  ;   
        
        userAgent = "Mozilla/4.0";
        System.out.println("Find pattern: " + userAgent)  ;   
        
        CapabilityMap cm = service.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        
        MediaType mediaType = cm.getPreferredMediaType();
        assertNotNull("MediaType is null", mediaType); 
        
        MimeType mimeTypeObj =    cm.getPreferredType();
        assertNotNull("MimeType is null", mimeTypeObj);         
        String mimeType = mimeTypeObj.getName();
         
        String encoding = mediaType.getCharacterSet();
        
        System.out.println("MediaType = " + mediaType.getName());
        System.out.println("Mimetype = " + mimeType);
        System.out.println("Encoding = " + encoding);
                
    }

}
