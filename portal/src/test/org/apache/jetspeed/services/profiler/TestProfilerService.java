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
package org.apache.jetspeed.services.profiler;

import java.io.File;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.PSMLDocument;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Profile;
import org.apache.jetspeed.om.profile.ProfileLocator;
import org.apache.jetspeed.om.profile.QueryLocator;
import org.apache.jetspeed.om.profile.psml.PsmlControl;
import org.apache.jetspeed.om.profile.psml.PsmlController;
import org.apache.jetspeed.om.profile.psml.PsmlPortlets;
import org.apache.jetspeed.test.JetspeedTest;


/**
 * TestProfilerService
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */

public class TestProfilerService extends JetspeedTest 
{

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestProfilerService( String name ) 
    {
        super( name );
    }

    /**
     * Start the tests.
     *
     * @param args the arguments. Not used
     */
    public static void main(String args[]) 
    {
        junit.awtui.TestRunner.main( new String[] { TestProfilerService.class.getName() } );
    }

    public void setup() 
    {
        System.out.println("Setup: Testing categories of Profiler Service");
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
        return new TestSuite( TestProfilerService.class );
    }

    public void testRun() throws Exception
    {
        getAnonymous();
        createProfileTest();
        locatorTest();
    }

    public void getAnonymous()
    {
        QueryLocator locator = new QueryLocator(QueryLocator.QUERY_USER); // TODO: replace with factory
        locator.setUser( "anon" );
        locator.setMediaType( "html" );
        Iterator it = Profiler.query( locator );
        Profile profile = (Profile)it.next();
        assertNotNull(profile);
        PSMLDocument doc = profile.getDocument();
        assertNotNull(doc);
        System.out.println("Test passed");
    }
    
    public void locatorTest() throws Exception
    {
        QueryLocator locator = new QueryLocator( QueryLocator.QUERY_USER );
        Iterator it = Profiler.query( locator );
        while (it.hasNext())
        {
            Profile profile = (Profile)it.next();
            assertNotNull(profile);
            dumpProfile(profile);
            if (profile.getUser().equals("cachetest"))
            {
                continue;
            }
            assertTrue("User = " + profile.getUser(), profile.getUser().equals("anon"));
            //assertTrue(profile.getAnonymous() == true);
            assertTrue(profile.getName().equals("default.psml"));
        }

        QueryLocator locator2 = new QueryLocator( QueryLocator.QUERY_USER );
        locator2.setUser( "anon" );
        it = Profiler.query( locator2 );
        if (it.hasNext())
        {
            Profile profile = (Profile)it.next();
            assertNotNull(profile);
            assertTrue(profile.getUser().equals("anon"));
            System.out.println("prfile = " + profile.getName());
            assertTrue(profile.getName().equals("default.psml"));
        }

        QueryLocator locator3 = new QueryLocator( QueryLocator.QUERY_GROUP );
        locator3.setGroup("apache");
        locator3.setName("news");
        it = Profiler.query( locator3 );
        if (it.hasNext())
        {
            Profile profile = (Profile)it.next();
            assertNotNull(profile);
            if (profile.getName().equals("create-test.psml"))
            {
                profile = (Profile)it.next();
                assertNotNull(profile);                
            }
            assertTrue(profile.getGroup().equals("apache"));
            assertTrue(profile.getAnonymous() == false);
            System.out.println("profile = " + profile.getName());
            assertTrue(profile.getName().equals("news.psml"));
        }

    }

    /**
     * Tests categories
     * @throws Exception
     */
    public void createProfileTest() throws Exception
    {
        try
        {
            ProfileLocator locator = Profiler.createLocator();
            locator.setGroup("apache");
            locator.setName("create-test");

            Portlets portlets = new PsmlPortlets();
            Control control = new PsmlControl();
            Controller controller = new PsmlController();
            control.setName("BoxControl");
            controller.setName("GridPortletController");
            portlets.setControl(control);
            portlets.setController(controller);
            Profile profile = Profiler.createProfile(locator, portlets);
            PSMLDocument doc = profile.getDocument();

            System.out.println("doc = " + doc.getName());

            // this only works with the default configuration (Castor/Filebased)
            File file = new File(doc.getName());
            assertTrue(file.exists());
            file.delete();
        }
        catch (Exception e)
        {
            String errmsg = "Error in Profiler Service: " + e.toString();
            e.printStackTrace();
            assertNotNull(errmsg, null);
        }
    }

    protected static void dump( Iterator it )
    {
        System.out.println("===============================================");
        while (it.hasNext() )
        {
            Profile profile = (Profile)it.next();
            dumpProfile(profile);
        }
        System.out.println("===============================================");
    }

    protected static void dumpProfile(Profile profile)
    {
        String user = profile.getUser();
        String group = profile.getGroup();
        String role = profile.getRole();
        if (profile.getAnonymous() == true)
            System.out.println("ANON USER");
        System.out.println("RESOURCE = " + profile.getName());
        if (null != user)
            System.out.println("USER = " + user );
        if (null != group)
            System.out.println("GROUP = " + group );
        if (null != role)
            System.out.println("ROLE = " + role );
        System.out.println("MEDIA TYPE = " + profile.getMediaType());
        System.out.println("LANGUAGE = " + profile.getLanguage());
        System.out.println("COUNTRY = " + profile.getCountry());
        PSMLDocument doc = profile.getDocument();
        if (null == doc)
            System.out.println("Document is null");
        else
        {
            if (null == profile.getName())
                System.out.println("profile name is null");
            else
                System.out.println("Doc.name=" + profile.getName());
        }

        System.out.println("----------------------");
    }


}
