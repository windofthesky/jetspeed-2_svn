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
package org.apache.jetspeed.services.psml;

// Java imports
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.ConfigElement;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.Security;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.test.JetspeedTest;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.Serializer;
import org.apache.xml.serialize.XMLSerializer;
import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.xml.sax.InputSource;

/**
 * TestMarshalPsml
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TestMarshalPsml extends JetspeedTest 
{    

    /**
     * Defines the testcase name for JUnit.
     *
     * @param name the testcase's name.
     */
    public TestMarshalPsml( String name ) 
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
        junit.awtui.TestRunner.main( new String[] { TestMarshalPsml.class.getName() } );
    }
 
    public void setup() 
    {
        System.out.println("Setup: Testing marshalling of PSML");
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
        return new TestSuite( TestMarshalPsml.class );
    }

    private String getMappingFileName()
    {
        return "./src/webapp/WEB-INF/conf/psml-mapping.xml";
    }

    /**
     * Tests ConfigElement unmarshaling entryset base stuff
     * @throws Exception
     */

    public void testUnmarshalConfigElement() throws Exception 
    {
        System.out.println("Testing marshalling of PSML on base *** ConfigElement ***");

        String psmlFile = "./test/testdata/psml/test/testcase.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                FileReader reader = new FileReader(psmlFile);
                mapping = new Mapping();
                InputSource is = new InputSource( new FileReader(map) );
                is.setSystemId( mapFile );
                mapping.loadMapping( is );
                Unmarshaller unmarshaller = new Unmarshaller(mapping);
                ConfigElement rootset = (ConfigElement)unmarshaller.unmarshal(reader);
                
                assertTrue(rootset.getName().equals("theRootSet"));

                Iterator params = rootset.getParameterIterator();
                Parameter param = (Parameter)params.next();
                assertTrue(param.getName().equals("city"));
                assertTrue(param.getValue().equals("Atlanta"));
                param = (Parameter)params.next();
                assertTrue(param.getName().equals("state"));
                assertTrue(param.getValue().equals("Georgia"));
                param = (Parameter)params.next();
                assertTrue(param.getName().equals("country"));
                assertTrue(param.getValue().equals("USA"));
                assertTrue(rootset.getParameterValue("city").equals("Atlanta"));
                assertTrue(rootset.getParameterValue("country").equals("USA"));
                assertTrue(rootset.getParameter("state").getValue().equals("Georgia"));                
                
            }
            catch (Exception e)
            {
                String errmsg = "Error in psml mapping creation: " + e.toString();
                System.err.println(errmsg);
                assertNotNull(errmsg, null);
            }
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable: ";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }  
    }

    /**
     * Tests IdentityElement unmarshaling entryset base stuff
     * @throws Exception
     */

    public void testUnmarshalPsml() throws Exception 
    {
        System.out.println("Testing marshalling of PSML on base *** IdentityElement ***");

        String psmlFile = "./test/testdata/psml/test/testcase.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                FileReader reader = new FileReader(psmlFile);
                mapping = new Mapping();
                InputSource is = new InputSource( new FileReader(map) );
                is.setSystemId( mapFile );
                mapping.loadMapping( is );
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
                Unmarshaller unmarshaller = new Unmarshaller(mapping);
                Portlets rootset = (Portlets)unmarshaller.unmarshal(reader);
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");

                assertTrue(rootset.getName().equals("theRootSet"));
                assertTrue(rootset.getId().equals("01"));

                MetaInfo meta = rootset.getMetaInfo();
                assertNotNull(meta);
                assertTrue(meta.getTitle().equals("Jetspeed"));
                assertTrue(meta.getDescription().equals("This is the default page for me"));
                assertTrue(meta.getImage().equals("me.png"));
                assertTrue(rootset.getTitle().equals("Jetspeed"));
                assertTrue(rootset.getDescription().equals("This is the default page for me"));
                assertTrue(rootset.getImage().equals("me.png"));

                Security security = rootset.getSecurity();
                assertNotNull(security);
                assertTrue(security.getId().equals("999"));

                Iterator params = rootset.getParameterIterator();
                Parameter param = (Parameter)params.next();
                assertTrue(param.getName().equals("city"));
                assertTrue(param.getValue().equals("Atlanta"));
                param = (Parameter)params.next();
                assertTrue(param.getName().equals("state"));
                assertTrue(param.getValue().equals("Georgia"));
                param = (Parameter)params.next();
                assertTrue(param.getName().equals("country"));
                assertTrue(param.getValue().equals("USA"));

                assertTrue(rootset.getParameterValue("city").equals("Atlanta"));
                assertTrue(rootset.getParameterValue("country").equals("USA"));
                assertTrue(rootset.getParameter("state").getValue().equals("Georgia"));                

                Skin skin = rootset.getSkin();
                assertNotNull(skin);
                assertTrue(skin.getName().equals("skinny"));
                assertTrue(skin.getState().equals("DETACHED"));
                Iterator skinParams = skin.getParameterIterator();
                assertNotNull(skinParams);
                Parameter skinParam = (Parameter)skinParams.next();
                assertTrue(skinParam.getName().equals("a"));
                assertTrue(skinParam.getValue().equals("1"));
                skinParam = (Parameter)skinParams.next();
                assertTrue(skinParam.getName().equals("b"));
                assertTrue(skinParam.getValue().equals("2"));

                Layout layout = rootset.getLayout();
                assertNotNull(layout);
                assertTrue(layout.getName().equals("layout1"));
                assertTrue(layout.getSize() == 1);
                assertTrue(layout.getPosition() == 3);

                Iterator layoutParams = layout.getParameterIterator();
                assertNotNull(layoutParams);
                Parameter layoutParam = (Parameter)layoutParams.next();
                assertTrue(layoutParam.getName().equals("a"));
                assertTrue(layoutParam.getValue().equals("1"));
                layoutParam = (Parameter)layoutParams.next();
                assertTrue(layoutParam.getName().equals("b"));
                assertTrue(layoutParam.getValue().equals("2"));

                Control control = rootset.getControl();
                assertNotNull(control);
                Iterator controlParams = control.getParameterIterator();
                assertNotNull(controlParams);
                Parameter controlParam = (Parameter)controlParams.next();
                assertTrue(control.getName().equals("TabControl"));
                assertTrue(controlParam.getName().equals("a"));
                assertTrue(controlParam.getValue().equals("1"));
                controlParam = (Parameter)controlParams.next();
                assertTrue(controlParam.getName().equals("b"));
                assertTrue(controlParam.getValue().equals("2"));

                Controller controller = rootset.getController();
                assertNotNull(controller);
                Iterator controllerParams = controller.getParameterIterator();
                assertNotNull(controllerParams);
                Parameter controllerParam = (Parameter)controllerParams.next();

                assertTrue(controller.getName().equals("TabController"));
                assertTrue(controllerParam.getName().equals("a"));
                assertTrue(controllerParam.getValue().equals("1"));
                controllerParam = (Parameter)controllerParams.next();
                assertTrue(controllerParam.getName().equals("b"));
                assertTrue(controllerParam.getValue().equals("2"));

                Iterator entries = rootset.getEntriesIterator();
                assertNotNull(entries);
                Entry entry = (Entry)entries.next();
                assertTrue(entry.getParent().equals("LoggedInWelcome"));    
                assertTrue(entry.getId().equals("03"));    

                Layout elayout = entry.getLayout();
                assertNotNull(elayout);
                Iterator elayoutParams = elayout.getParameterIterator();
                assertNotNull(elayoutParams);
                Parameter elayoutParam = (Parameter)elayoutParams.next();
                assertTrue(elayoutParam.getName().equals("column"));
                elayoutParam = (Parameter)elayoutParams.next();
                assertTrue(elayoutParam.getName().equals("row"));

                Iterator pv = rootset.getPortletsIterator();

                Portlets p = (Portlets)pv.next();
                assertNotNull(p);

                Controller pc = p.getController();
                assertNotNull(pc);
                assertTrue(pc.getName().equals("TwoColumns"));

                Iterator pe = p.getEntriesIterator();
                assertNotNull(pe);
                Entry e1 = (Entry)pe.next();
                assertTrue(e1.getParent().equals("HelloWhatever"));    
                assertTrue(e1.getId().equals("99"));    

                Entry e2 = (Entry)pe.next();
                assertTrue(e2.getParent().equals("HelloVelocity"));    
                assertTrue(e2.getId().equals("100"));    

                Entry e3 = (Entry)pe.next();
                assertTrue(e3.getParent().equals("HelloCleveland"));    
                assertTrue(e3.getId().startsWith("P-"));    
                System.out.println(e3.getId());

                Iterator rv = p.getReferenceIterator();
                assertNotNull(rv);
                Reference ref = (Reference)rv.next();
                assertNotNull(ref);
                assertTrue(ref.getName().equals("ReferenceTest"));
                assertTrue(ref.getId().equals("300"));
                Portlets epr = ref.getPortletsReference();
                assertNotNull(epr);
                assertEquals("group/apache/page/news/media-type/html", ref.getPath());
/*
    DST TODO: REFERENCES are broken, not sure why
                assertTrue(epr.getMetaInfo().getTitle().equals("Default Apache News page"));
                // DST: - TODO: only use 'test' psml for unit tests -
                // otherwise the tests are against moving targets;
                // DST: assertTrue(epr.getController().getParameter("mode").getValue().equals("row"));
                // DST: assertTrue(epr.getSkin().getParameter("selected-color").getValue().equals("#990000"));
                Entry ent = epr.getEntry(0);
                assertTrue(ent.getParent().equals("Apacheweek"));

                Iterator itt = p.getPortletsIterator();
                while (itt.hasNext())
                {
                    Portlets pp = (Portlets)itt.next();
                    System.out.println(" PORTLETS %%% " + pp.getId());
                    if (pp instanceof Reference)
                    {
                        System.out.println(" PORTLETS %%% REF: " + pp.getId());
                    }
                }
*/
            }
            catch (Exception e)
            {
                String errmsg = "Error in psml mapping creation: " + e.toString();
                e.printStackTrace();
                System.err.println(errmsg);
                assertNotNull(errmsg, null);
            }
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable: ";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }  
    }

    /**
     * Tests unmarshaling security
     * @throws Exception
     */
    public void testUnmarshalSecurity() throws Exception 
    {
        System.out.println("Testing marshalling of PSML on base *** Security ***");

        String psmlFile = "./test/testdata/psml/test/testsecurity.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                FileReader reader = new FileReader(psmlFile);
                mapping = new Mapping();
                InputSource is = new InputSource( new FileReader(map) );
                is.setSystemId( mapFile );
                mapping.loadMapping( is );
                Unmarshaller unmarshaller = new Unmarshaller(mapping);
                Security security = (Security)unmarshaller.unmarshal(reader);
                assertNotNull(security);
                assertTrue(security.getId().equals("1000"));

            }
            catch (Exception e)
            {
                String errmsg = "Error in psml mapping creation: " + e.toString();
                System.err.println(errmsg);
                assertNotNull(errmsg, null);
            }
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable.";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
   
    }

    /**
     * Tests unmarshaling security
     * @throws Exception
     */
    public void testUnmarshalSecurityRef() throws Exception 
    {
        System.out.println("Testing marshalling of PSML on base *** Security-ref ***");

        String psmlFile = "./test/testdata/psml/test/testcase_securityref.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            FileReader reader = new FileReader(psmlFile);
            mapping = new Mapping();
            InputSource is = new InputSource( new FileReader(map) );
            is.setSystemId( mapFile );
            mapping.loadMapping( is );

            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
            Unmarshaller unmarshaller = new Unmarshaller(mapping);
            Portlets rootset = (Portlets)unmarshaller.unmarshal(reader);
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
            
            assertTrue(rootset.getName().equals("theRootSet"));
            assertTrue(rootset.getId().equals("01"));
            
            SecurityReference securityRef = rootset.getSecurityRef();
            assertNotNull("got SecurityRef", securityRef);
            assertEquals( "Name of parent", "all_users", securityRef.getParent());
                
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable.";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }
   
    }


    public void testMarshalPsml() throws Exception 
    {
        System.out.println("Testing marshalling of PSML on base *** IdentityElement ***");

        String psmlFile = "./test/testdata/psml/test/testcaseMarshall.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                FileReader reader = new FileReader(psmlFile);
                mapping = new Mapping();
                InputSource is = new InputSource( new FileReader(map) );
                is.setSystemId( mapFile );
                mapping.loadMapping( is );
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
                Unmarshaller unmarshaller = new Unmarshaller(mapping);
                Portlets rootset = (Portlets)unmarshaller.unmarshal(reader);
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");

                assertTrue(rootset.getName().equals("theRootSet"));
                assertTrue(rootset.getId().equals("01"));

                Iterator itt = rootset.getPortletsIterator();
                while (itt.hasNext())
                {
                    Portlets pp = (Portlets)itt.next();
                    System.out.println(" PORTLETS %%% " + pp.getId());
                    if (pp instanceof Reference)
                    {
                        System.out.println(" PORTLETS %%% REF: " + pp.getId());
                    }
                }

                Iterator itr = rootset.getReferenceIterator();
                while (itr.hasNext())
                {
                    Reference r = (Reference)itr.next();
                    System.out.println(" REFERENCE %%% " + r.getId());
                }

                OutputFormat format = new OutputFormat();
                format.setIndenting(true);
                format.setIndent(4);
    
                File f = new File("marshalled.psml");
                FileWriter writer = null;

                writer = new FileWriter(f);

                System.out.println("-----------------------------------------------------------------");
                Serializer serializer = new XMLSerializer(writer, format); 
                Marshaller marshaller = new Marshaller(serializer.asDocumentHandler());
                marshaller.setMapping(mapping);
                marshaller.marshal(rootset);
                System.out.println("-----------------------------------------------------------------");
                System.out.println("done");

            }
            catch (Exception e)
            {
                String errmsg = "Error in psml mapping creation: " + e.toString();
                e.printStackTrace();
                System.err.println(errmsg);
                assertNotNull(errmsg, null);
            }
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable: ";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }  
    }

    public void testMetaInfo() throws Exception 
    {
        boolean foundEntry07 = false;
        boolean foundPortlet02 = false;
        
        System.out.println("Testing marshalling of PSML on base *** IdentityElement ***");

        String psmlFile = "./test/testdata/psml/test/testcaseMarshall.psml";

        Mapping mapping = null;
        String mapFile = getMappingFileName();
        File map = new File(mapFile);
        if (map.exists() && map.isFile() && map.canRead())
        {
            try
            {
                FileReader reader = new FileReader(psmlFile);
                mapping = new Mapping();
                InputSource is = new InputSource( new FileReader(map) );
                is.setSystemId( mapFile );
                mapping.loadMapping( is );
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");
                Unmarshaller unmarshaller = new Unmarshaller(mapping);
                Portlets rootset = (Portlets)unmarshaller.unmarshal(reader);
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++");

                assertTrue(rootset.getName().equals("theRootSet"));
                assertTrue(rootset.getId().equals("01"));

                Iterator itt = rootset.getPortletsIterator();
                while (itt.hasNext())
                {
                    Portlets pp = (Portlets)itt.next();
                    System.out.println(" PORTLETS %%% " + pp.getId());
                    if ( pp.getId().equals("02"))
                    {
                        foundPortlet02 = true;
                        MetaInfo pp02MetaInfo = pp.getMetaInfo();
                        assertNotNull( "Portlet ID 02 has metaInfo", pp02MetaInfo);
                        assertEquals( "Portlet ID 02 Title", "Portlet Title", pp02MetaInfo.getTitle());
                        assertEquals( "Portlet ID 02 Title", "Portlet Description", pp02MetaInfo.getDescription());
                        assertEquals( "Portlet ID 02 Title", "Portlet Image", pp02MetaInfo.getImage());
                        Iterator pp02itt = pp.getEntriesIterator();
                        while (pp02itt.hasNext())
                        {
                            Entry pp02Entry = (Entry) pp02itt.next();
                            assertNotNull( "Portlet Id 02 has entry", pp02Entry);
                            if (pp02Entry.getId().equals("07"))
                            {
                                foundEntry07 = true;
                                MetaInfo entry07MetaInfo = pp02Entry.getMetaInfo();
                                assertNotNull( "Entry ID 07 has metaInfo", entry07MetaInfo);
                                assertEquals( "Entry ID 07 Title", "Entry Title", entry07MetaInfo.getTitle());
                                assertEquals( "Entry ID 07 Title", "Entry Description", entry07MetaInfo.getDescription());
                                assertEquals( "Entry ID 07 Title", "Entry Image", entry07MetaInfo.getImage());
                            }
                        }
                    }
                }
                assertTrue( "Tested Portlet 02", foundPortlet02);
                assertTrue( "Tested Entry 07", foundEntry07);

            }
            catch (Exception e)
            {
                String errmsg = "Error in psml mapping creation: " + e.toString();
                e.printStackTrace();
                System.err.println(errmsg);
                assertNotNull(errmsg, null);
            }
        }
        else
        {
            String errmsg = "PSML Mapping not found or not a file or unreadable: ";
            System.err.println(errmsg);
            assertNotNull(errmsg, null);
        }  
    }
}
