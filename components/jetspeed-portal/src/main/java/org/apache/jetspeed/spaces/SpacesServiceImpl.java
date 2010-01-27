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
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.administration.AdminUtil;
import org.apache.jetspeed.locator.TemplateLocator;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.folder.FolderNotFoundException;
import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Spaces Services
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SpacesServiceImpl implements Spaces
{
    protected static Logger log = LoggerFactory.getLogger(SpacesServiceImpl.class);    
  	
    private PageManager pageManager;
    private TemplateLocator decoratorLocator;
    
    public SpacesServiceImpl(PageManager pageManager, TemplateLocator decoratorLocator)
    {
        this.pageManager = pageManager;
        this.decoratorLocator = decoratorLocator;
    }

    public List<Environment> listEnvironments()
    {
        List<Environment> envs = new LinkedList<Environment>();
        Folder envFolder = null;
        try
        {
            envFolder = pageManager.getFolder(ENVIRONMENTS_LOCATION);
            Iterator<Folder> it = envFolder.getFolders().iterator(); 
            while (it.hasNext())
            {
                Folder backingFolder = it.next();
                Environment env = loadEnvironment(backingFolder);
                envs.add(env);
            }            
        }
        catch (Exception e)
        {
            // its OK to have no environments, optional feature
        }
        return envs;
    }
    
    public Environment createEnvironment(String envName, String owner) throws SpacesException
    {
    	Folder folder = pageManager.newFolder(makeEnvironmentPath(envName));
    	// TODO: store owner in security constraints
    	updateField(folder, Locale.ENGLISH, Environment.META_ENV_OWNER, owner);
    	try
    	{
    		pageManager.updateFolder(folder);
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    	return new EnvironmentImpl(folder);
    }

    public void storeEnvironment(Environment env) throws SpacesException
    {
    	try
    	{
	    	if (env instanceof EnvironmentImpl)
	    	{
	    		pageManager.updateFolder(((EnvironmentImpl)env).getBackingFolder());
	    	}
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    }

    public void deleteEnvironment(Environment env) throws SpacesException    
    {
    	try
    	{
	    	if (env instanceof EnvironmentImpl)
	    	{
	    		pageManager.removeFolder(((EnvironmentImpl)env).getBackingFolder());
	    	}
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    }
    
    public Environment lookupEnvironment(String envName) 
    {
    	try 
    	{
			Environment env = new EnvironmentImpl(pageManager.getFolder(makeEnvironmentPath(envName)));
			return env;
		} 
    	catch (FolderNotFoundException e) 
    	{
		} 
    	catch (Exception e) 
    	{
    		log.error("lookupEnvironment", e);        	    		
		}
    	return null;
    }
    
    public List<Space> listSpaces()
    {
        List<Space> result = new ArrayList<Space>();
        try
        {
            Folder root = pageManager.getFolder(Folder.PATH_SEPARATOR);
            Space defaultSpace = loadSpace(root);
            result.add(defaultSpace);            
            Iterator<Folder> spaces = root.getFolders().iterator();
            for (int ix = 0; spaces.hasNext(); ix++)
            {
                Folder folder = spaces.next();
                if (folder.isHidden() || folder.isReserved())
                    continue;
                Space space = loadSpace(folder);
                result.add(space);
            }
        }
        catch (Exception e)
        {
    		log.error("listSpaces", e);        	
        }            
        return result;
    }
    
    public List<Space> listSpaces(String envName)
    {
        List<Space> result = new ArrayList<Space>();
        Folder envFolder = null;
        try
        {    	
        	envFolder = pageManager.getFolder(makeEnvironmentPath(envName));        	
        }
    	catch (FolderNotFoundException e) 
    	{
    		return result;
    	}
        catch (Exception e)
        {
    		log.error("listSpaces", e);        	
        }
        try
        {
	        Iterator<Link> links = envFolder.getLinks().iterator();
	        while (links.hasNext())
	        {
	        	Link link = links.next();
	        	String spacePath = link.getPath();
	        	Folder folder = pageManager.getFolder(spacePath);
                if (folder.isHidden() || folder.isReserved())
                	continue;
	        	result.add(loadSpace(folder));
	        }
        }
    	catch (FolderNotFoundException e) 
    	{
		} 
    	catch (Exception e) 
    	{
    		log.error("listSpaces", e);        	
		}        
        return result;
    }

    public Space createSpace(String spaceName, Folder templateFolder, String owner) throws SpacesException
    {
    	String spacePath = makeSpacePath(spaceName);
        Folder spaceFolder = null;
        boolean found = false;        
        try
        {
            spaceFolder = this.pageManager.getFolder(spacePath);
            found = (spaceFolder != null);
        }
        catch (Exception ignore)
        {}        
        try
        {
            if (!found)
            {
                pageManager.deepCopyFolder(templateFolder, spacePath, owner);
            }
            else
            {
                pageManager.deepMergeFolder(templateFolder, spacePath, owner);
            }
            
            spaceFolder = pageManager.getFolder(spacePath);
            Space space = loadSpace(spaceFolder);
            space.setOwner(owner);
            storeSpace(space);
            return space;    	
        }
        catch (Exception e)
        {
        	throw new SpacesException(e);
        }        
    }
    
    public Space createSpace(String spaceName, String owner) throws SpacesException
    {
    	Folder folder = pageManager.newFolder(makeSpacePath(spaceName));
    	// TODO: store owner in security constraints
    	updateField(folder, Locale.ENGLISH, Space.META_SPACE_OWNER, owner);
    	try
    	{
    		pageManager.updateFolder(folder);
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    	return new SpaceImpl(folder);    	
    }

    public void storeSpace(Space space) throws SpacesException
    {
    	try
    	{
	    	if (space instanceof SpaceImpl)
	    	{
	    		pageManager.updateFolder(((SpaceImpl)space).getBackingFolder());
	    	}
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}    	
    }
    
    public void deleteSpace(Space space) throws SpacesException
    {
    	try
    	{
	    	if (space instanceof SpaceImpl)
	    	{
	    		pageManager.removeFolder(((SpaceImpl)space).getBackingFolder());
	    		// TODO: remove from environments
	    	}
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    }
        
    public Space lookupSpace(String spaceName) 
    {
    	try 
    	{
			Space space = new SpaceImpl(pageManager.getFolder(makeSpacePath(spaceName)));
			return space;
		} 
    	catch (FolderNotFoundException e) 
    	{
		} 
    	catch (Exception e) 
    	{
    		log.error("lookupSpace", e);        	
		}
    	return null;
    }
    
    public void addSpaceToEnvironment(Space space, Environment env) throws SpacesException
    {
		try
		{
    		String path = AdminUtil.concatenatePaths(ENVIRONMENTS_LOCATION, env.getPath());
    		path = AdminUtil.concatenatePaths(path, space.getName());
			Link link = pageManager.newLink(path);
    		link.setUrl(space.getPath());
    		pageManager.updateLink(link);
		}
		catch (Exception e)
		{
			throw new SpacesException(e);
		}
    }
    
    public void removeSpaceFromEnvironment(Space space, Environment env) throws SpacesException
    {
		try
		{
    		String path = AdminUtil.concatenatePaths(ENVIRONMENTS_LOCATION, env.getPath());
    		path = AdminUtil.concatenatePaths(path, space.getName());
    		Link link ;
    		try
    		{
    			link = pageManager.getLink(path);
    		}
    		catch (Exception e)
    		{
    			return; // not found
    		}    		
    		pageManager.removeLink(link);
		}
		catch (Exception e)
		{
			throw new SpacesException(e);
		}
    }
    
    public boolean isSpaceInEnvironment(Space space, Environment env)
    {
		String path = AdminUtil.concatenatePaths(ENVIRONMENTS_LOCATION, env.getPath());
		path = AdminUtil.concatenatePaths(path, space.getName());
    	try
		{
    		Link link = pageManager.getLink(path);
    		return true;
		}
		catch (Exception e)
		{
		}    		    
		return false; // not found		
    }
        
    public void deletePage(Page page) throws SpacesException
    {
    	try
    	{
    		pageManager.removePage(page);    		
    	}
    	catch (Exception e)
    	{
    		throw new SpacesException(e);
    	}
    }
 
	public List<Folder> listFolders(Space space) 
	{
        List<Folder> result = new ArrayList<Folder>();
        try
        {
            Folder root = pageManager.getFolder(space.getPath());
            Iterator folders = root.getFolders().iterator();
            for (int ix = 0; folders.hasNext(); ix++)
            {
                Node folder = (Node)folders.next();
                result.add((Folder)folder);
            }
        }
        catch (Exception e)
        {
        	log.error("listFolders", e);
        }
        return result;        
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
        	log.error("listLinks", e);
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
        	log.error("listPages", e);
        }
        return result;        
    }
    
    /*
     * Helpers
     */
    static protected boolean updateMetaField(Collection<LocalizedField> fields, Locale locale, String name, String value)
    {
        Iterator<LocalizedField> it = fields.iterator();
        while (it.hasNext())
        {
            LocalizedField field = it.next();
            if (locale == null || field.getLocale().equals(locale))
            {
                field.setValue(value);
                return true;
            }
        }       
        return false;
    }

    static protected String retrieveField(Folder folder, Locale locale, String name)
    {
        GenericMetadata metadata = folder.getMetadata();
        Collection<LocalizedField> fields = metadata.getFields();
        if (fields != null)
        {
            Iterator<LocalizedField> it = fields.iterator();
            while (it.hasNext())
            {
                LocalizedField field = it.next();
                if (locale == null || field.getLocale().equals(locale))
                {                
                    return field.getValue();
                }
            }
        }
        return null;
    }
    
    static protected void updateField(Folder folder, Locale locale, String name, String value)
    {
    	Locale addLocale = (locale == null) ? Locale.ENGLISH : locale;
        GenericMetadata metadata = folder.getMetadata();
        Collection<LocalizedField> fields = metadata.getFields();
        if (fields == null || fields.size() == 0)
            metadata.addField(addLocale, name, value);
        else
        {
            if (!updateMetaField(fields, locale, name, value))
            {
                metadata.addField(addLocale, name, value); 
            }
        }
        
    }

    protected Space loadSpace(Folder f)
    {
        return new SpaceImpl(f);
    }

    protected Environment loadEnvironment(Folder f)
    {
        return new EnvironmentImpl(f);
    }

    protected String makeSpacePath(String spaceName)
    {
    	return AdminUtil.concatenatePaths(Folder.PATH_SEPARATOR, spaceName);
    }

    protected String makeEnvironmentPath(String envName)
    {
    	return AdminUtil.concatenatePaths(ENVIRONMENTS_LOCATION, envName); 
    }
    
}
