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
package org.apache.jetspeed.cps;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;


/**
 * Base class for CPS tests.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @since 2.0
 * @version $Id$
 */
public abstract class CPSTest
    extends TestCase
    implements CPSConstants
{
    Configuration configuration = null;
    
    /**
     * Creates a new instance.
     */
    public CPSTest(String testName) 
    {
        super(testName);
    }

    /**
     * Return the Test
     */
    public static Test suite() 
    {
        return new TestSuite(CPSTest.class);
    }

    protected CommonPortletServices cps = null;

    /**
     * Setup the test.
     */
    public void setUp() 
    {
        try
        {
            if (cps != null)
            {
                return;
            }
            String propertiesFilename = getPropertiesFile();
            String applicationRoot = getApplicationRoot();
            configuration = (Configuration) 
                new PropertiesConfiguration(propertiesFilename);
            
            configuration.setProperty(APPLICATION_ROOT_KEY, applicationRoot);
            //properties.setProperty(WEBAPP_ROOT_KEY, null);

            cps = CommonPortletServices.getInstance();
            cps.init(configuration, applicationRoot);

        }
        catch (Exception e)
        {
            e.printStackTrace();
            assertTrue(false);
        }
    }
   
    /**
     * Override to set your own properties file
     *
     */
    public String getPropertiesFile()
    {
        return getApplicationRoot() + "/WEB-INF/conf/cps.properties";
    }

    public Configuration getConfiguration()
    {
        return this.configuration;
    }
    
    /**
     * Override to set your own application root
     * If the default directory does not exist, then look in
     * the cps directory.  If the directory exist in the cps directory,
     * then return this directory.  Yes this is a hack, but it works.
     */
    public String getApplicationRoot()
    {
        String applicationRoot = "test";
        File testPath = new File(applicationRoot);
        if (!testPath.exists())
        {
            testPath = new File( "cps" + File.separator + applicationRoot);
            if (testPath.exists())
            {
                applicationRoot = testPath.getAbsolutePath();
            }
        }
        return applicationRoot;
    }

    /**
     * Tear down the test.
     */
    public void tearDown() 
    {
        try
        {
            if (cps != null)
            {
                cps.shutdown();
            }
        }
        catch (CPSException e)
        {
            e.printStackTrace();
        }
        finally
        {
            cps = null;
        }
    }

}