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
package org.apache.jetspeed.om.page;

// Java imports
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.psml.FragmentImpl;
import org.apache.jetspeed.om.page.psml.PageImpl;

/**
 * TestMarshalPsml
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestPageObjectModel extends JetspeedTest
{

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestPageObjectModel( String name )
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
        junit.awtui.TestRunner.main( new String[] { TestPageObjectModel.class.getName() } );
    }

    public void setup()
    {
        System.out.println("Setup: Testing Page Object Model Implementation");
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
        return new TestSuite( TestPageObjectModel.class );
    }

    private Page buildBasePage()
    {
        PageImpl page = new PageImpl();
        page.setId("MyPageID");
        page.setName("Test");

        Fragment frag = new FragmentImpl();
        frag.setId("Frag1");
        frag.setType(Fragment.LAYOUT);

        page.setRootFragment(frag);

        return page;
    }

    public void testBasicPage() throws Exception
    {
        System.out.println("Testing simple Page creation");

        Page page = buildBasePage();
        assertTrue(page.getId().equals("MyPageID"));
        assertTrue(page.getName().equals("Test"));
        Fragment root = page.getRootFragment();
        assertNotNull(root);
        assertTrue(root.getId().equals("Frag1"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNull(root.getTitle());
    }

    public void tesFragmentManipulation() throws Exception
    {
        System.out.println("Testing Fragments manipulation");

        // Build a page with a few fragments
        Page page = buildBasePage();
        Fragment root = page.getRootFragment();
        assertNotNull(root.getFragments());

        Fragment frag1 = new FragmentImpl();
        frag1.setId("F1");
        frag1.setType(Fragment.PORTLET);
        frag1.setName("Portlet1");
        root.getFragments().add(frag1);

        Fragment frag2 = new FragmentImpl();
        frag2.setId("F2");
        frag2.setType(Fragment.LAYOUT);
        frag2.setName("TwoColumns");
        frag2.setDecorator("test");
        frag2.setAcl("private");

        Fragment frag3 = new FragmentImpl();
        frag3.setId("F3");
        frag3.setType(Fragment.PORTLET);
        frag3.setName("Portlet3");
        frag3.setDecorator("test");
        frag3.setState("minimized");
        frag2.getFragments().add(frag3);
        root.getFragments().add(frag2);

        //Check the construct
        assertTrue(root.getFragments().size()==2);
        Iterator i = root.getFragments().iterator();
        Fragment f = (Fragment)i.next();
        assertNotNull(f);
        assertTrue(f.getName().equals("Portlet1"));
        assertTrue(f.getType().equals(Fragment.PORTLET));
        assertTrue(f.getId().equals("F1"));
        assertNull(f.getTitle());
        assertNull(f.getAcl());
        assertNull(f.getDecorator());
        assertNull(f.getState());
        assertTrue(f.getFragments().size()==0);
        f = (Fragment)i.next();
        assertNotNull(f);
        assertTrue(f.getName().equals("TwoColumns"));
        assertTrue(f.getType().equals(Fragment.LAYOUT));
        assertTrue(f.getFragments().size()==1);
        assertTrue(f.getDecorator().equals("test"));
        assertTrue(f.getAcl().equals("private"));
        assertTrue(f.getFragments().size()==1);
        i = f.getFragments().iterator();
        frag1 = (Fragment)i.next();
        assertNotNull(frag1);
        assertTrue(frag1.getName().equals("Portlet3"));
        assertTrue(frag1.getType().equals(Fragment.PORTLET));

        //Now change the inner child to a new portlet
        frag2 = new FragmentImpl();
        frag2.setId("FR4");
        frag2.setType(Fragment.PORTLET);
        frag2.setName("P4");

        frag3 = page.getFragmentById("Portlet3");
        assertNotNull(frag3);
        f.getFragments().remove(frag3);
        frag3 = page.getFragmentById("Portlet3");
        assertNull(frag3);
        f.getFragments().add(frag2);
        assertTrue(f.getFragments().size()==1);
        f = (Fragment)f.getFragments().get(0);
        assertNotNull(f);
        assertTrue(f.getName().equals("P4"));
    }
}
