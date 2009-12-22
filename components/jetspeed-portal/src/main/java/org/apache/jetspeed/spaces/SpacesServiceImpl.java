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
package org.apache.jetspeed.spaces;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.folder.InvalidFolderException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.page.document.NodeException;
import org.apache.jetspeed.spaces.Space;
import org.apache.jetspeed.spaces.SpaceImpl;
import org.apache.jetspeed.spaces.Spaces;

/**
 * Spaces Services
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SpacesServiceImpl implements Spaces
{
    private PageManager pageManager;
    private TemplateLocator decoratorLocator;
    
    public SpacesServiceImpl(PageManager pageManager, TemplateLocator decoratorLocator)
    {
        this.pageManager = pageManager;
        this.decoratorLocator = decoratorLocator;
    }
    
    public Space addSpace(Space space)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Space> listSpaces()
    {
        List<Space> result = new ArrayList<Space>();
        try
        {
            Space defaultSpace = new SpaceImpl("Home", "/", "admin");
            result.add(defaultSpace);
            Folder root = pageManager.getFolder("/");
            defaultSpace.setTitle(root.getTitle());
            Iterator spaces = root.getFolders().iterator();
            for (int ix = 0; spaces.hasNext(); ix++)
            {
                Node node = (Node)spaces.next();
                if (node.isHidden())
                    continue;
                Collection<LocalizedField> fields = node.getMetadata().getFields("space-owner");
                if (fields != null)
                {
                    Iterator<LocalizedField> it = fields.iterator();
                    while (it.hasNext())
                    {
                        LocalizedField field = it.next();
                        Space space = new SpaceImpl(node.getName(), node.getPath(), field.getValue());
                        space.setTitle(node.getTitle());
                        result.add(space);
                    }
                }
            }
        }
        catch (FolderNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InvalidFolderException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NodeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }            
        return result;
    }

    public void removeSpace(Space space)
    {
        // TODO Auto-generated method stub

    }

    public Environment addEnvironment(Environment env)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Space addPage(Space space, Page page)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Space addSpace(Environment env, Space space)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Environment> listEnvironments()
    {
        // lets just give em the default folder / as an environment for now
        List<Environment> env = new ArrayList<Environment>();
        Environment ev = new EnvironmentImpl("Public", "/", "admin");
        ev.getSpaces().clear();
        ev.getSpaces().addAll(this.listSpaces());
        env.add(ev);
        return env;
    }

    public List<Link> listLinks(Space space)
    {
        List<Link> result = new ArrayList<Link>();
        try
        {
            Folder root = pageManager.getFolder(space.getPath());
            Iterator links = root.getLinks().iterator();
            for (int ix = 0; links.hasNext(); ix++)
            {
                Node link = (Node)links.next();
                result.add((Link)link);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;        
    }

    public List<Page> listPages(Space space)
    {
        List<Page> result = new ArrayList<Page>();
        try
        {
            Folder root = pageManager.getFolder(space.getPath());
            Iterator pages = root.getPages().iterator();
            for (int ix = 0; pages.hasNext(); ix++)
            {
                Node page = (Node)pages.next();
                result.add((Page)page);
            }
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;        
    }

    public List<Space> listSpaces(Environment env)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEnvironment(Environment env)
    {
        // TODO Auto-generated method stub
        
    }

    public void removePage(Space space, Page page)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeSpace(Environment env, Space space)
    {
        // TODO Auto-generated method stub
        
    }        

}
