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
package org.apache.jetspeed.portlets.rpad;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RepositoryManager
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory.getLog(RepositoryManager.class);

    private String configFileName;

    private Map repositories;

    private static RepositoryManager repositoryManager;

    public static void init(String configFileName) throws RPADException
    {
        repositoryManager = new RepositoryManager(configFileName);
    }

    public static RepositoryManager getInstance()
    {
        if (repositoryManager == null)
        {
            throw new IllegalStateException(
                    "init() needs to be called before getInstance().");
        }
        return repositoryManager;
    }

    public RepositoryManager(String configFileName) throws RPADException
    {
        this.configFileName = configFileName;
        load();
    }

    protected void load() throws RPADException
    {
        try
        {
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            SAXParser parser = spfactory.newSAXParser();
            RepositoryConfigHandler repoConfigHandler = new RepositoryConfigHandler();
            parser.parse(new File(configFileName), repoConfigHandler);
            repositories = repoConfigHandler.getRepositories();
        }
        catch (ParserConfigurationException e)
        {
            throw new RPADException("Could not configure a parser.", e);
        }
        catch (SAXException e)
        {
            throw new RPADException("An exception occurrs on SAX parser.", e);
        }
        catch (IOException e)
        {
            throw new RPADException(
                    "An exception occurrs when accessing a configuration file: "
                            + configFileName, e);
        }
    }

    public void reload() throws RPADException
    {
        synchronized (repositories)
        {
            load();
        }
    }

    public void addRepository(String name, Repository repository)
            throws RPADException
    {
        synchronized (repositories)
        {
            if (repositories.containsKey(name))
            {
                throw new RPADException(name + "exists.");
            }
            repositories.put(name, repository);
            store();
        }
    }

    public Repository getRepository(String name)
    {
        return (Repository) repositories.get(name);
    }

    public void removeRepository(String name) throws RPADException
    {
        synchronized (repositories)
        {
            if (!repositories.containsKey(name))
            {
                throw new RPADException(name + "does not exist.");
            }
            repositories.remove(name);
            store();
        }
    }

    public List getRepositories()
    {
        return new ArrayList(repositories.values());
    }

    public void store() throws RPADException
    {
        synchronized (repositories)
        {
            BufferedWriter writer = null;
            try
            {
                try
                {
                    writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(configFileName), "UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    writer = new BufferedWriter(new OutputStreamWriter(
                            new FileOutputStream(configFileName)));
                }
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                writer.write("<repositories>\n");
                for (Iterator i = repositories.entrySet().iterator(); i
                        .hasNext();)
                {
                    Map.Entry entry = (Map.Entry) i.next();
                    if (log.isDebugEnabled())
                    {
                        log.debug("Storing a repository: " + entry.getKey());
                    }

                    Repository repo = (Repository) entry.getValue();
                    writer.write(repo.toXMLString());
                }
                writer.write("</repositories>\n");
                writer.flush();
            }
            catch (FileNotFoundException e)
            {
                throw new RPADException("Could not find " + configFileName, e);
            }
            catch (IOException e)
            {
                throw new RPADException("Could not write " + configFileName, e);
            }
            finally
            {
                if (writer != null)
                {
                    try
                    {
                        writer.close();
                    }
                    catch (IOException e)
                    {
                    }
                }
            }
        }
    }

    public List getPortletApplications()
    {
        ArrayList list = new ArrayList();
        for (Iterator i = repositories.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry) i.next();
            Repository repo = (Repository) entry.getValue();
            if (repo.isAvailable())
            {
                List portlets = repo.getPortletApplications();
                if (portlets != null)
                {
                    list.addAll(portlets);
                }
            }
        }
        return list;
    }

    public List getPortletApplications(String name)
    {
        ArrayList list = new ArrayList();

        Repository repo = getRepository(name);
        if (repo != null && repo.isAvailable())
        {
            List portlets = repo.getPortletApplications();
            if (portlets != null)
            {
                list.addAll(portlets);
            }
        }
        return list;
    }

    //TODO search
}
