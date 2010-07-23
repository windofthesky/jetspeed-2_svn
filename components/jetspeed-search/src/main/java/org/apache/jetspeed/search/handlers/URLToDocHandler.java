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
package org.apache.jetspeed.search.handlers;

// Java imports
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.apache.jetspeed.search.AbstractObjectHandler;
import org.apache.jetspeed.search.BaseParsedObject;

/**
 * This object handler deals with URLs.
 * 
 * @author <a href="mailto:morciuch@apache.org">Mark Orciuch</a>
 * @version $Id$
 */
public class URLToDocHandler extends AbstractObjectHandler
{
    /**
     * Static initialization of the logger for this class
     */    
    //private static final JetspeedLogger logger = JetspeedLogFactoryService.getLogger(URLToDocHandler.class.getName());
    
    /**
     * Parses a specific object into a document suitable for index placement
     * 
     * @param o
     * @return 
     */
    public org.apache.jetspeed.search.ParsedObject parseObject(Object o)
    {
        org.apache.jetspeed.search.ParsedObject result = new BaseParsedObject();

        if ((o instanceof URL) == false)
        {
            //logger.error("URLToDocHandler: invalid object type: " + o);
            return null;
        }

        URL pageToAdd = (URL) o;
        String content = getContentFromURL(pageToAdd);
        
        if (content != null)
        {
            try
            {
                result.setKey(java.net.URLEncoder.encode(pageToAdd.toString(),"UTF-8"));
                result.setType(org.apache.jetspeed.search.ParsedObject.OBJECT_TYPE_URL);
                // TODO: We should extract the <title> tag here.
                result.setTitle(pageToAdd.toString());
                result.setContent(content);
                result.setDescription("");
                result.setLanguage("");
                result.setURL(pageToAdd);
                result.setClassName(o.getClass().getName());
                //logger.info("Parsed '" + pageToAdd.toString() + "'");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                //logger.error("Adding document to index", e);
            }
        }
        
        return result;

    }
    
    private String getContentFromURL(final URL url)
    {
        if ("file".equals(url.getProtocol()))
        {
            return readContentFromURL(url);
        }
        
        String content = null;
        
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url.toString());
        method.setFollowRedirects(true);
        int statusCode = -1;
        int attempt = 0;

        try
        {
            // We will retry up to 3 times.
            while (statusCode == -1 && attempt < 3)
            {
                try
                {
                    // execute the method.
                    client.executeMethod(method);
                    statusCode = method.getStatusCode();
                    //if (logger.isDebugEnabled())
                    {
                        //logger.debug("URL = " + pageToAdd.toString() + "Status code = " + statusCode);
                    }
                }
                catch (HttpException e)
                {
                    // We will retry
                    attempt++;
                }
                catch (IOException e)
                {
                    return null;
                }
            }
            // Check that we didn't run out of retries.
            if (statusCode != -1)
            {
                try
                {
                    content = method.getResponseBodyAsString();
                }
                catch (Exception ioe)
                {
                    //logger.error("Getting content for " + pageToAdd.toString(), ioe);
                }
            }
        }
        finally
        {
            method.releaseConnection();
        }

        return content;
    }
    
    private String readContentFromURL(final URL url)
    {
        String content = null;
        InputStream input = null;
        
        try
        {
            input = url.openStream();
            content = IOUtils.toString(input);
        }
        catch (Exception e)
        {
        }
        finally
        {
            if (input != null)
            {
                try
                {
                    input.close();
                }
                catch (Exception ce)
                {
                }
            }
        }
        
        return content;
    }
}

