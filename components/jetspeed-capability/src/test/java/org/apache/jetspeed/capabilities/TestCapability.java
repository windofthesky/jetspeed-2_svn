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

package org.apache.jetspeed.capabilities;

import junit.framework.Test;
import org.apache.jetspeed.components.util.DatasourceEnabledSpringTestCase;
import org.apache.jetspeed.serializer.JetspeedSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


/**
 * Test Capability Service
 * 
 * @author <a href="roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class TestCapability extends DatasourceEnabledSpringTestCase
{
    private Capabilities capabilities = null;

    protected void setUp() throws Exception
    {
        super.setUp();
        capabilities = scm.lookupComponent("capabilities");
    }

    public static Test suite()
    {
        return createFixturedTestSuite(TestCapability.class, "firstTestSetup", "lastTestTeardown");
    }

    public void firstTestSetup() throws Exception
    {
        JetspeedSerializer serializer = scm.lookupComponent("serializer");
        serializer.deleteData();
        serializer.importData(getBaseDir()+"target/test-classes/j2-seed.xml");
    }

    public void lastTestTeardown() throws Exception
    {
        JetspeedSerializer serializer = scm.lookupComponent("serializer");
        serializer.deleteData();
    }
    
    /**
     * Tests categories
     * 
     * @throws Exception
     */
    public void testCapability() throws Exception
    {
        assertNotNull("capabilities component is null", capabilities);
        int lastOrder = 0;
        Iterator caps = capabilities.getClients();
        while (caps.hasNext())
        {
            Client client = (Client) caps.next();
            int evalOrder = client.getEvalOrder();
            if (lastOrder > evalOrder)
            {
                assertTrue("Client result set is not ordered!", false);
            }
            lastOrder = evalOrder;
        }

        // Find specific client -- testing pattern matching
        String userAgent;
        System.out.println("Testing all supported Clients...");
        userAgent = "Opera/7.0";
        System.out.println("Find pattern: " + userAgent);
        CapabilityMap cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("Opera", cm.getClient().getName().equals("opera7"));
        capabilityMapReport(cm);

        userAgent = "Mozilla/4.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("Netscape/Mozilla4", cm.getClient().getName().equals("ns4"));
        capabilityMapReport(cm);

        userAgent = "MSIE 5.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("MSIE 5", cm.getClient().getName().equals("ie5"));
        capabilityMapReport(cm);

        userAgent = "Mozilla/5.0";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("Mozilla 5.0", cm.getClient().getName().equals("mozilla"));
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

        userAgent = "Mozilla/5.0 (Macintosh; U; PPC Mac OS X; en-us) AppleWebKit/125.5.6 (KHTML, like Gecko) Safari/125.12";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("found Safari", cm.getClient().getName().equals("safari"));
        capabilityMapReport(cm);

        userAgent = "Mozilla/4.0 (compatible; MSIE 5.23; Mac_PowerPC)";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("IE for Mac " + cm.getClient().getName(), cm.getClient().getName().equals("ie5mac"));
        capabilityMapReport(cm);

        userAgent = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322)";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("IE 6 Windows", cm.getClient().getName().equals("ie6"));
        capabilityMapReport(cm);

        userAgent = "SonyEricssonK800i/R1CB Browser/NetFront/3.3 Profile/MIDP-2.0 Configuration/CLDC-1.1";
        System.out.println("Find pattern: " + userAgent);
        cm = capabilities.getCapabilityMap(userAgent);
        assertNotNull("getCapabilityMap is null", cm);
        assertTrue("Ericsson", cm.getClient().getName().equals("sonyericsson"));
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
        Iterator<MimeType> mtIterator = cm.getMimeTypes();
        while (mtIterator.hasNext())
        {
            System.out.println(mtIterator.next().getName());
        }
    }

    private HashMap getCapabilities(int howMany)
    {
       	Capability capability = null;
    	Iterator _it = capabilities.getCapabilities();
    	HashMap _hash = new HashMap();
    	int count = 0;
    	while (_it.hasNext())
    	{
    		capability = (Capability)_it.next();
    		_hash.put(capability.getName(), capability);
    		count++;
    		if (howMany > 0)
    			if (count >= howMany)
    				return _hash;
    	}
    	return _hash;
    }
    
    private HashMap getMimeTypes(int howMany)
    {
       	MimeType mimeType = null;
    	Iterator _it = capabilities.getMimeTypes();
    	HashMap _hash = new HashMap();
    	int count = 0;
    	while (_it.hasNext())
    	{
    		mimeType = (MimeType)_it.next();
    		_hash.put(mimeType.getName(), mimeType);
    		count++;
    		if (howMany > 0)
    			if (count >= howMany)
    				return _hash;
    	}
    	return _hash;
    }
    
    public void testNewMimeType() throws Exception
    {
    	MimeType mimeType = null;
    	Iterator _it = null;
    	HashMap _hash = getMimeTypes(0);
    	int count = _hash.size();
        assertTrue("MimeTypes do not exist", (count > 0));

    	_it = _hash.keySet().iterator();
    	
    	int pos = count/2;
    	
    	for (int i = 0; i < pos; i++)
    		_it.next();
    	
    	String existingKey = (String)_it.next();
    	MimeType existingObject = (MimeType)_hash.get(existingKey);
        assertNotNull("Couldn't identify existing mime object to run test",existingObject);

    	
    	// "create" existing one
        mimeType = capabilities.createMimeType(existingKey);
        assertNotNull("creating 'existing' mimetype returns null", mimeType);
        assertTrue("creating 'existing' mimetype didn't return existing object", (mimeType.equals(existingObject)));
        
        // create a new one:
        mimeType = capabilities.createMimeType("TEST MIME TYPE");
        assertNotNull("creating new mimetype returns null", mimeType);
        
        // ensure it doesn't exist in the capabilities
        Set existing = _hash.entrySet();
        assertTrue("creating new mimetype already in existing list", (!(existing.contains(mimeType))));
        
    	existingObject = capabilities.getMimeType("TEST MIME TYPE");
        assertNull("creating new mimetype already in existing capabilities",existingObject);
        
        capabilities.storeMimeType(mimeType);
    	existingObject = capabilities.getMimeType("TEST MIME TYPE");
        assertNotNull("creating and saving new mimetype didn't store object",existingObject);
        
        
        capabilities.deleteMimeType(mimeType);
    	existingObject = capabilities.getMimeType("TEST MIME TYPE");
        assertNull("creating new mimetype delete from storage didn't work",existingObject);
        
    }


    
    
    
    public void testNewCapability() throws Exception
    {
    	Capability capability = null;
    	Iterator _it = null;
       	HashMap _hash = getCapabilities(0);
    	int count = _hash.size();
        assertTrue("Capabilitys do not exist", (count > 0));

    	_it = _hash.keySet().iterator();
    	
    	int pos = count/2;
    	
    	for (int i = 0; i < pos; i++)
    		_it.next();
    	
    	String existingKey = (String)_it.next();
    	Capability existingObject = (Capability)_hash.get(existingKey);
        assertNotNull("Couldn't identify existing mime object to run test",existingObject);

    	
    	// "create" existing one
        capability = capabilities.createCapability(existingKey);
        assertNotNull("creating 'existing' capability returns null", capability);
        assertTrue("creating 'existing' capability didn't return existing object", (capability.equals(existingObject)));
        
        // create a new one:
        capability = capabilities.createCapability("TEST CAPABILITY TYPE");
        assertNotNull("creating new capability returns null", capability);
        
        // ensure it doesn't exist in the capabilities
        Set existing = _hash.entrySet();
        assertTrue("creating new capability already in existing list", (!(existing.contains(capability))));
        
    	existingObject = capabilities.getCapability("TEST CAPABILITY TYPE");
        assertNull("creating new capability already in existing capabilities",existingObject);
        
        capabilities.storeCapability(capability);
    	existingObject = capabilities.getCapability("TEST CAPABILITY TYPE");
        assertNotNull("creating and saving new capability didn't store object",existingObject);
        
        
        capabilities.deleteCapability(capability);
    	existingObject = capabilities.getCapability("TEST CAPABILITY TYPE");
        assertNull("creating new capability delete from storage didn't work",existingObject);
        
    }

    
    
    public void testNewMediaType() throws Exception
    {
    	MediaType mediaType = null;
    	Iterator _it = capabilities.getMediaTypes();
    	HashMap _hash = new HashMap();
    	int count = 0;
    	while (_it.hasNext())
    	{
    		mediaType = (MediaType)_it.next();
    		_hash.put(mediaType.getName(), mediaType);
    		count++;
    	}
        assertTrue("Mediatypes do not exist", (count > 0));

    	_it = _hash.keySet().iterator();
    	
    	int pos = count/2;
    	
    	for (int i = 0; i < pos; i++)
    		_it.next();
    	
    	String existingKey = (String)_it.next();
    	MediaType existingObject = (MediaType)_hash.get(existingKey);
        assertNotNull("Couldn't identify existing object to run test",existingObject);

    	
    	// "create" existing one
    	mediaType = capabilities.createMediaType(existingKey);
        assertNotNull("creating 'existing' mediatype returns null", mediaType);
        assertTrue("creating 'existing' mediatype didn't return existing object", (mediaType.equals(existingObject)));

        
        // setting fields
        String name = "TEST MEDIA TYPE";
        String utf = "UTF-8";
        String title = "TEST MEDIA TYPE - Title";
        String description = "TEST MEDIA TYPE - Description";
        
        int numCapabilities = 2;
        int numMimeTypes = 3;
        
        HashMap someCapabilities  = getCapabilities(numCapabilities);
        HashMap someMimeTypes  = getMimeTypes(numMimeTypes);
        
        
        
        // create a new one:
        mediaType = capabilities.createMediaType(name);
        assertNotNull("creating new mediatype returns null", mediaType);
        
        // ensure it doesn't exist in the capabilities
        Set existing = _hash.entrySet();
        assertTrue("creating new mediaType already in existing list", (!(existing.contains(mediaType))));
        
    	existingObject = capabilities.getMediaType(name);
        assertNull("creating new mediaType already in existing capabilities",existingObject);
        
        
// set object fields               
        mediaType.setCharacterSet(utf);
        mediaType.setTitle(title);
        mediaType.setDescription(description);
        
        _it = someMimeTypes.values().iterator();
        int added = 0;
        while (_it.hasNext())
        {
        	mediaType.addMimetype((MimeType)_it.next());
        	added++;
        }
        assertTrue("number of Mimetypes added (" + added + ") not the same as expected ("+numMimeTypes+")",(added==numMimeTypes));
        
        // setting links:
        
        
        ArrayList set = new ArrayList(someCapabilities.values());
        mediaType.setCapabilities(set);
        assertTrue("number of Capabilities added (" + set.size() + ") not the same as expected ("+numCapabilities+")",(set.size()==numCapabilities));
        
        capabilities.storeMediaType(mediaType);
    	existingObject = capabilities.getMediaType(name);
        assertNotNull("creating and saving new mediaType didn't store object",existingObject);
        
        capabilities.deleteMediaType(mediaType);
    	existingObject = capabilities.getMediaType(name);
        assertNull("creating new mediaType delete from storage didn't work",existingObject);
    }
    
    public void testNewClient() throws Exception
    {
    	Client client = null;
    	Iterator _it = capabilities.getClients();
    	HashMap _hash = new HashMap();
    	int count = 0;
    	while (_it.hasNext())
    	{
    		client = (Client)_it.next();
    		_hash.put(client.getName(), client);
    		count++;
    	}
        assertTrue("Clients do not exist", (count > 0));

    	_it = _hash.keySet().iterator();
    	
    	int pos = count/2;
    	
    	for (int i = 0; i < pos; i++)
    		_it.next();
    	
    	String existingKey = (String)_it.next();
    	Client existingObject = (Client)_hash.get(existingKey);
        assertNotNull("Couldn't identify existing object to run test",existingObject);
    	
    	// "create" existing one
    	client = capabilities.createClient(existingKey);
        assertNotNull("creating 'existing' client returns null", client);
        assertTrue("creating 'existing' client didn't return existing object", (client.equals(existingObject)));
        
        // setting fields        
        String name  = "TEST CLIENT";
        int numCapabilities = 3;
        int numMimeTypes = 4;
        
        HashMap someCapabilities  = getCapabilities(numCapabilities);
        HashMap someMimeTypes  = getMimeTypes(numMimeTypes);

        // create a new one:
        client = capabilities.createClient(name);
        assertNotNull("creating new client returns null", client);
        
        // ensure it doesn't exist in the capabilities
        Set existing = _hash.entrySet();
        assertTrue("creating new client already in existing list", (!(existing.contains(client))));
        
    	existingObject = capabilities.getClient(name);
        assertNull("creating new client already in existing capabilities",existingObject);
        
        String userAgentPattern = "TEST.*|TESTBROWSER.*";
        String manufacturer = "Test Manufacturer";
        String model = "XYZ";
        
        // set object fields               
        client.setUserAgentPattern(userAgentPattern);
        client.setManufacturer(manufacturer);
        client.setModel(model);

        ArrayList set = new ArrayList(someCapabilities.values());
        client.setCapabilities(set);
        assertTrue("number of Capabilities added (" + set.size() + ") not the same as expected ("+numCapabilities+")",(set.size()==numCapabilities));
        
        set = new ArrayList(someMimeTypes.values());
        client.setCapabilities(set);
        assertTrue("number of MimeTypes added (" + set.size() + ") not the same as expected ("+numCapabilities+")",(set.size()==numMimeTypes));
        
        // setting links:
        capabilities.storeClient(client);
    	existingObject = capabilities.getClient(name);
        assertNotNull("creating and saving new client didn't store object",existingObject);
        
        capabilities.deleteClient(client);
    	existingObject = capabilities.getClient(name);
        assertNull("creating new client delete from storage didn't work",existingObject);
    }
    
    public void testCapabilityRepeat() throws Exception
    {
    	capabilities.deleteCapabilityMapCache();
        testCapability();
    }

    
    protected String[] getConfigurations()
    {
        return new String[] 
        { "capabilities.xml", "transaction.xml", "serializer.xml" };
    }

    protected String getBeanDefinitionFilterCategories()
    {
        return "default,jdbcDS";
    }
}
