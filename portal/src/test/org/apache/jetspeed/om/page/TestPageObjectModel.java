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
package org.apache.jetspeed.om.page;

// Java imports
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.test.JetspeedTest;
import org.apache.jetspeed.test.JetspeedTestSuite;
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
        return new JetspeedTestSuite( TestPageObjectModel.class );
    }

    private Page buildBasePage()
    {
        PageImpl page = new PageImpl();
        page.setId("MyPageID");

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
        Fragment root = page.getRootFragment();
        assertNotNull(root);
        assertTrue(root.getId().equals("Frag1"));
        assertTrue(root.getType().equals(Fragment.LAYOUT));
        assertNull(root.getTitle());
    }

    public void testFragmentManipulation() throws Exception
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

        frag3 = page.getFragmentById("F3");
        assertNotNull(frag3);
        f.getFragments().remove(frag3);
        frag3 = page.getFragmentById("F3");
        assertNull(frag3);
        f.getFragments().add(frag2);
        assertTrue(f.getFragments().size()==1);
        f = (Fragment)f.getFragments().get(0);
        assertNotNull(f);
        assertTrue(f.getName().equals("P4"));
    }
}
