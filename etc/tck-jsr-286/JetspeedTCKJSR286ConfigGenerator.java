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
import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @version $Id$
 *
 */
public class JetspeedTCKJSR286ConfigGenerator
{
    private static String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                                    + "<!--\n"
                                    + "    Licensed to the Apache Software Foundation (ASF) under one or more\n"
                                    + "    contributor license agreements.  See the NOTICE file distributed with\n"
                                    + "    this work for additional information regarding copyright ownership.\n"
                                    + "    The ASF licenses this file to You under the Apache License, Version 2.0\n"
                                    + "    (the \"License\"); you may not use this file except in compliance with\n"
                                    + "    the License.  You may obtain a copy of the License at\n"
                                    + "\n"    
                                    + "    http://www.apache.org/licenses/LICENSE-2.0\n"
                                    + "\n"    
                                    + "    Unless required by applicable law or agreed to in writing, software\n"
                                    + "    distributed under the License is distributed on an \"AS IS\" BASIS,\n"
                                    + "    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
                                    + "    See the License for the specific language governing permissions and\n"
                                    + "    limitations under the License.\n"
                                    + "-->\n";        
    private static String pageHeader = "<page>\n"
                                       + "  <defaults layout-decorator=\"simple\" portlet-decorator=\"clear\"/>\n"
                                       + "  <title>TCK testcase {0} for test: {1}</title>\n"
                                       + "  <fragment id=\"tck-{0}\" type=\"layout\" name=\"jetspeed-layouts::VelocityOneColumn\">\n";
    private static String fragment = "    <fragment id=\"tck-{0}-{1}\" type=\"portlet\" name=\"{2}\"/>\n";
    private static String pageFooter = "  </fragment>\n" + "</page>\n";

    public static void main(String args[]) throws Exception
    {
        new JetspeedTCKJSR286ConfigGenerator(args[0]);
    }
    
    public JetspeedTCKJSR286ConfigGenerator(String tckTestsFile) throws Exception
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(tckTestsFile));
        NodeList nodes = doc.getDocumentElement().getElementsByTagName("test_case");
        FileWriter urlMappingFile = new FileWriter(new File("jetspeedTestsToURLMapping.xml"));
        
        
        urlMappingFile.write(xmlHeader);
        urlMappingFile.write("<test_case_urls xmlns=\"http://java.sun.com/xml/ns/portlet/portletTCKVendor_1_0.xsd\"\n");
        urlMappingFile.write("                xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        urlMappingFile.write("                xsi:schemaLocation=\"http://java.sun.com/xml/ns/portlet/portletTCKVendor_1_0.xsd\n");
        urlMappingFile.write("                http://java.sun.com/xml/ns/portlet/portletTCKVendor_1_0.xsd\">\n");
        int num;
        Element test_case;
        Element test_portlet;
        FileWriter psmlFile;
        String testName;
        new File("tck-jsr286").mkdirs();
        
        for (num = 0; num < nodes.getLength(); num++)
        {
            test_case = (Element)nodes.item(num);
            testName = getText(test_case, "test_name");
            addTestURLMapping(testName, num + 1, urlMappingFile);
            psmlFile = createTestPage(testName, num+1);
            NodeList portlets = test_case.getElementsByTagName("test_portlet");
            for (int i = 0; i < portlets.getLength(); i++)
            {
                test_portlet = (Element)portlets.item(i);
                addTestPortlet(psmlFile, getText(test_portlet, "app_name") + "::" + getText(test_portlet, "portlet_name"), num+1, i);
            }
            finishTestPage(psmlFile);
        }
        // add missing SignatureTest
        testName = "com/sun/ts/tests/portlet/Signature/PORTLETSigTest.java#SignatureTest";
        addTestURLMapping(testName, num+1, urlMappingFile);
        psmlFile = createTestPage(testName, num+1);
        addTestPortlet(psmlFile, "portlet_jp_sig_web::SignatureTestPortlet", num+1, 0);
        finishTestPage(psmlFile);
        urlMappingFile.write("</test_case_urls>\n");
        urlMappingFile.close();
    }

    private void addTestURLMapping(String testName, int num, FileWriter writer) throws Exception
    {
        writer.write("  <test_case_url>\n");
        writer.write("    <test_name>" + testName + "</test_name>\n");
        writer.write("    <test_url>http://localhost:8080/jetspeed/portal/tck-jsr286/testcase" + num + ".psml</test_url>\n");
        writer.write("  </test_case_url>\n");
    }

    private FileWriter createTestPage(String testName, int num) throws Exception
    {
        FileWriter writer = new FileWriter(new File("tck-jsr286/testcase" + num + ".psml"));
        writer.write(xmlHeader);
        writer.write(MessageFormat.format(pageHeader, Integer.toString(num), testName));
        return writer;
    }
    
    private void finishTestPage(FileWriter writer) throws Exception
    {
        writer.write(pageFooter);
        writer.close();
    }
    
    private void addTestPortlet(FileWriter writer, String uniqueName, int num, int index) throws Exception
    {
        writer.write(MessageFormat.format(fragment, Integer.toString(num), Integer.toString(index), uniqueName));
    }

    private String getText(Element e, String name)
    {
        return e.getElementsByTagName(name).item(0).getTextContent();
    }
}
