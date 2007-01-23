/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.rpad;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class RepositoryConfigHandler extends DefaultHandler
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory
            .getLog(RepositoryConfigHandler.class);

    private String className;

    private String propertyName;

    private String repositoryName;

    private Map properties;

    //TODO
    //    private String typeName;
    //    private Map types;

    private String currentQName;

    private Map repositories;

    public RepositoryConfigHandler()
    {
        className = null;
        propertyName = null;
        properties = new HashMap();
        currentQName = null;
        repositories = new HashMap();
    }

    protected Repository loadRepository(String className, Map properties)
    {
        try
        {
            Class cls = Class.forName(className);
            Object obj = cls.newInstance();
            if (obj instanceof Repository)
            {
                Repository repo = (Repository) obj;
                for (Iterator i = properties.entrySet().iterator(); i.hasNext();)
                {
                    try
                    {
                        Map.Entry entry = (Map.Entry) i.next();
                        String propertyName = (String) entry.getKey();
                        Class[] clsArgs = new Class[1];
                        //TODO set type
                        clsArgs[0] = String.class;
                        Method method = cls.getMethod("set"
                                + propertyName.substring(0, 1).toUpperCase(
                                        Locale.ENGLISH)
                                + propertyName.substring(1), clsArgs);
                        Object[] args = new Object[1];
                        args[0] = entry.getValue();
                        method.invoke(repo, args);
                    }
                    catch (Exception e)
                    {
                        log.error("Could invoke a method for property: "
                                + propertyName, e);
                    }
                }

                try
                {
                    // call init()
                    Method initMethod = cls.getMethod("init", null);
                    initMethod.invoke(repo, null);
                }
                catch (Exception e)
                {
                    log.error("Could not initialize an instance: " + className,
                            e);
                    repo.setAvailable(false);
                }

                return repo;
            }
        }
        catch (Exception e)
        {
            log.error("Could not create an instance: " + className, e);
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException
    {
        if ("repository".equals(qName))
        {
            if (className != null && repositoryName != null
                    && !repositories.equals(""))
            {
                Repository repo = loadRepository(className, properties);
                if (repo != null)
                {
                    repositories.put(repositoryName, repo);
                }
                else
                {
                    log.warn("Could not load " + className);
                }
            }
            else
            {
                log.warn("The class name or repository name are null.");
            }
        }

        if (currentQName != null && currentQName.equals(qName))
        {
            currentQName = null;
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException
    {
        currentQName = qName;

        if ("repository".equals(qName))
        {
            className = null;
            propertyName = null;
            properties = new HashMap();
        }
        else if ("class".equals(qName))
        {
            className = attributes.getValue("name");
        }
        else if ("property".equals(qName))
        {
            propertyName = attributes.getValue("name");
        }
    }

    /* (non-Javadoc)
     * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException
    {

        if ("property".equals(currentQName))
        {
            if (propertyName != null)
            {
                properties.put(propertyName, new String(ch, start, length));
            }
            propertyName = null;
        }
        else if ("name".equals(currentQName))
        {
            repositoryName = new String(ch, start, length);
            properties.put("name", repositoryName);
        }
    }

    /**
     * @return the repositories
     */
    public Map getRepositories()
    {
        return repositories;
    }

}
