/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.jetspeed.page;

import org.apache.commons.lang.StringUtils;
import org.apache.jetspeed.util.DirectoryHelper;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.Map;


/**
 * @author ddam
 *
 */
public class DirectoryXMLTransform extends DirectoryHelper
{
    private SAXTransformerFactory transformerFactory;

    private SAXParserFactory saxFactory;
    
    private Map<String,Object> xsltMapping;
    
        public DirectoryXMLTransform(File base, Map<String,Object> extensionToXslt) {
          super(base);
          this.xsltMapping=extensionToXslt;
          System.setProperty("javax.xml.transform.TransformerFactory",
          "org.apache.xalan.processor.TransformerFactoryImpl");
          System.setProperty("javax.xml.parsers.SAXParserFactory", "org.apache.xerces.jaxp.SAXParserFactoryImpl");
          System.setProperty("org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
          transformerFactory = (SAXTransformerFactory) TransformerFactory.newInstance();
          saxFactory = SAXParserFactory.newInstance();
          saxFactory.setValidating(false);

        }
        
        protected void setBaseDirectory(File directory){
            if(!directory.exists())
            {
                directory.mkdirs();
            }
            
            if(!directory.isDirectory())
            {
                throw new IllegalArgumentException("DirectoryHelper(File) requires directory not a file.");
            }
            this.directory = directory;
            
        }

        private Transformer getXSLTForFile(File f){
            String extension = StringUtils.substringAfterLast(f.getName(),".");
            
            if (!StringUtils.isEmpty(extension) && xsltMapping.containsKey(extension.toLowerCase())){
                
                Object t_obj = xsltMapping.get(extension.toLowerCase());
                if (t_obj instanceof Transformer){
                    return (Transformer)t_obj;
                }
                if (t_obj instanceof String){
                    String t_path = (String) t_obj;
                    Transformer transformer; 
                    try{
                        transformer = transformerFactory.newTransformer(new StreamSource(t_path));    
                        xsltMapping.put(extension, transformer);
                        return transformer;
                    } catch(TransformerConfigurationException e){
                        
                    }                    
                }
            }
            
            return null;
        }
        
        /**
         * <p>
         * copyFrom
         * </p>
         *
         * @see org.apache.jetspeed.util.FileSystemHelper#copyFrom(java.io.File, java.io.FileFilter)
         * @param srcDirectory
         * @param fileFilter
         * @throws IOException
         */
        public void copyFromAndTransform( File srcDirectory, FileFilter fileFilter ) throws IOException
        {
            if(!srcDirectory.isDirectory())
            {
                throw new IllegalArgumentException("DirectoryHelper.copyFrom(File) requires directory not a file.");
            }
            copyFilesAndTransform(srcDirectory, directory, fileFilter);        

        }

        /**
         * 
         * <p>
         * copyFiles
         * </p>
         *
         * @param srcDir Source directory to copy from.
         * @param dstDir Destination directory to copy to.
         * @throws IOException
         * @throws FileNotFoundException

         */
        protected void copyFilesAndTransform(File srcDir, File dstDir, FileFilter fileFilter) throws IOException
        {
            FileChannel srcChannel = null;
            FileChannel dstChannel = null;

            try
            {
            File[] children = srcDir.listFiles(fileFilter);
            for(int i=0; i<children.length; i++)
            {
                File child = children[i];
                if(child.isFile())
                {
                    File toFile = new File(dstDir, child.getName());
                    
                    toFile.createNewFile();
                    srcChannel = new FileInputStream(child).getChannel();
                    
                    Transformer transformer = getXSLTForFile(child);
                    if (transformer != null){
                        FileOutputStream f_out = new FileOutputStream(toFile);
                        try{
                            transformer.transform(new StreamSource(child), new StreamResult(f_out));
                            f_out.flush();
                            f_out.close();
                        } catch (TransformerException e){
                            System.out.println("Error transforming file "+child.getCanonicalPath());
                        }
                        
                    } else {
                        dstChannel = new FileOutputStream(toFile).getChannel();
                        dstChannel.transferFrom(srcChannel, 0, srcChannel.size());  
                        dstChannel.close();
                    }
                    
                    srcChannel.close();
                   
                }
                else
                {
                    File newSubDir = new File(dstDir, child.getName());
                    newSubDir.mkdir();
                    copyFilesAndTransform(child, newSubDir, fileFilter);
                }
            }
            }
            finally
            {
                if ( srcChannel != null && srcChannel.isOpen() )
                {
                    try
                    {
                        srcChannel.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
                if ( dstChannel != null && dstChannel.isOpen() )
                {
                    try
                    {
                        dstChannel.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
        }
        
        public void transform(Transformer transformer, InputStream in, Writer out) throws TransformerException
        {
            transformer.transform(new StreamSource(in), new StreamResult(out));
        }
}
