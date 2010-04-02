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
package org.apache.jetspeed.maven.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

/**
 * TestFileEntryCollection
 * 
 * @version $Id$
 */
public class TestFileEntryCollection extends TestCase
{
    private static final String TEST_PA_RESOURCE_PATH = "testdata/test-pa";
    
    private File testPAFolder;
    private File tempZipFile;
    
    @Override
    public void setUp() throws Exception
    {
        URL testPAFolderURL = getClass().getClassLoader().getResource(TEST_PA_RESOURCE_PATH);
        assertEquals("The test pa folder url is expected to be a file: url in this test case", 
                     "file", testPAFolderURL.getProtocol());
        testPAFolder = new File(testPAFolderURL.toURI());
        assertTrue("Test PA folder not found: " + testPAFolder, testPAFolder.isDirectory());
    }
    
    @Override
    public void tearDown()
    {
        if (tempZipFile != null)
        {
            tempZipFile.delete();
        }
    }
    
    public void testFolder() throws Exception
    {
        FileEntryCollection fec = new FileEntryCollection(testPAFolder);
        assertEquals(testPAFolder.getPath(), fec.getName());
        
        List<FileEntry> entryList = getEntryList(fec);

        String entryName = "WEB-INF";
        FileEntry entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertTrue(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());

        entryName = "WEB-INF/web.xml";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());
        InputStream is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/portlet.xml";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();
        
        entryName = "WEB-INF/jetspeed-portlet.xml";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/view";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertTrue(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());

        entryName = "WEB-INF/view/datetime.jsp";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/view/edit-prefs.vm";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        assertEquals(new File(testPAFolder, entryName).lastModified(), entry.getTime());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();
    }
    
    public void testZipFile() throws Exception
    {
        tempZipFile = File.createTempFile(getClass().getName(), ".zip");
        
        OutputStream os = null;
        BufferedOutputStream bos = null;
        ZipOutputStream zipOutput = null;
        
        try
        {
            os = new FileOutputStream(tempZipFile);
            bos = new BufferedOutputStream(os);
            zipOutput = new ZipOutputStream(bos);
            addFileEntryToZipOutput(testPAFolder, testPAFolder, zipOutput);
        }
        finally
        {
            if (zipOutput != null)
            {
                try 
                {
                    zipOutput.close();
                }
                catch (Exception ignore)
                {
                }
            }
            if (bos != null)
            {
                try 
                {
                    bos.close();
                }
                catch (Exception ignore)
                {
                }
            }
            if (os != null)
            {
                try 
                {
                    os.close();
                }
                catch (Exception ignore)
                {
                }
            }
        }
        
        assertTrue(tempZipFile.length() > 0L);
        
        ZipFile zipFile = new ZipFile(tempZipFile);
        FileEntryCollection fec = new FileEntryCollection(zipFile);
        assertEquals(zipFile.getName(), fec.getName());
        
        List<FileEntry> entryList = getEntryList(fec);

        String entryName = "WEB-INF/web.xml";
        FileEntry entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        InputStream is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/portlet.xml";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();
        
        entryName = "WEB-INF/jetspeed-portlet.xml";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/view/datetime.jsp";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();

        entryName = "WEB-INF/view/edit-prefs.vm";
        entry = findEntry(entryList, entryName);
        assertNotNull("Entry not found: " + entryName, entry);
        assertFalse(entry.isDirectory());
        is = fec.getInputStream(entry);
        assertNotNull(is);
        is.close();
        
        zipFile.close();
        
        tempZipFile.delete();
        tempZipFile = null;
    }
    
    private void addFileEntryToZipOutput(File baseFolder, File file, ZipOutputStream zipOutput) throws Exception
    {
        if (file.isDirectory())
        {
            File [] children = file.listFiles();
            
            for (File child : children)
            {
                addFileEntryToZipOutput(baseFolder, child, zipOutput);
            }
        }
        else
        {
            InputStream is = null;
            BufferedInputStream bis = null;
            
            try
            {
                String entryName = file.getPath().substring(baseFolder.getPath().length() + 1);
                if (!File.separator.equals("/"))
                {
                    entryName = entryName.replace(File.separator, "/");
                }
                is = new FileInputStream(file);
                bis = new BufferedInputStream(is);
                ZipEntry zipEntry = new ZipEntry(entryName);
                zipOutput.putNextEntry(zipEntry);
                
                byte [] buffer = new byte[4096];
                int readLen = bis.read(buffer, 0, 4096);
                while (readLen != -1)
                {
                    zipOutput.write(buffer, 0, readLen);
                    readLen = bis.read(buffer, 0, 4096);
                }
                
                zipOutput.closeEntry();
            }
            finally
            {
                if (bis != null)
                {
                    try 
                    {
                        bis.close();
                    }
                    catch (Exception ignore)
                    {
                    }
                }
                if (is != null)
                {
                    try 
                    {
                        is.close();
                    }
                    catch (Exception ignore)
                    {
                    }
                }
            }
        }
    }
    
    private List<FileEntry> getEntryList(final FileEntryCollection fec)
    {
        List<FileEntry> list = new LinkedList<FileEntry>();
        
        Enumeration<? extends FileEntry> entries = fec.entries();
        
        while (entries.hasMoreElements())
        {
            FileEntry entry = entries.nextElement();
            list.add(entry);
        }
        
        return list;
    }
    
    private FileEntry findEntry(final List<FileEntry> list, String name)
    {
        for (FileEntry entry : list)
        {
            if (entry.getName().equals(name))
            {
                return entry;
            }
        }
        
        return null;
    }
    
    
}
