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
package org.apache.jetspeed.locator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jetspeed's default implementation of a template locator.
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class JetspeedTemplateLocator implements TemplateLocator
{
    private final static Logger log = LoggerFactory.getLogger(JetspeedTemplateLocator.class);

    private static final String PATH_SEPARATOR = "/";

    /** the template root directories, all application root relative */
    private List<String> roots;
    
    /** Root of the application running this locator */
    private String appRoot;
       
    /** the Template class is factory created */     
    private Class  templateClass = JetspeedTemplateDescriptor.class;

    /** the TemplateLocator class is factory created */     
    private Class  locatorClass = JetspeedLocatorDescriptor.class;
    
    /** the default locator type */
    private String defaultLocatorType = "layout";

    /** template name cache used to speed up searches for templates */
    private Map templateMap = null;

    /** use the name cache when looking up a template */
    private boolean useNameCache = true;

    private JetspeedTemplateLocator()
    {
        // need to know roots
    }
    
    /**
     * Minimal assembly with a list of resource directory roots.
     * 
     * @param roots A list of resource root directories where templates are located.
     * @param appRoot  Root from where this application runs
     */
    public JetspeedTemplateLocator(List roots, String appRoot) throws FileNotFoundException 
    {
        this.appRoot = appRoot;
        log.info("Locator application root "+new File(appRoot).getAbsolutePath());
        this.roots = roots;   
        Iterator itr = roots.iterator();
        while(itr.hasNext())
        {
            String path  = (String) itr.next();
            File checkFile = new File(path);
            if(!checkFile.exists())
            {
                throw new FileNotFoundException("Locator resource root "+checkFile.getAbsolutePath()+" does not exist.");
            }
        }        
    }

    /**
     * Construct with a root list and a default locator type.
     * 
     * @param roots A list of resource root directories where templates are located.
     * @param defaultLocatorType Under root directories, subdirectories represent locator types.
     *                           A locator type represents a classification of templates.
     *                           Any value is allowed. Use locator types to group templates together. 
     */
    public JetspeedTemplateLocator(List roots, 
                                        String defaultLocatorType,
                                        String appRoot) throws FileNotFoundException
    {
        this(roots, appRoot);
        this.defaultLocatorType = defaultLocatorType;
    }

    /**
     * Assemble with list resource directory roots and OM classes and a defaultLocatorType.
     * 
     * @param roots A list of resource root directories where templates are located.
     * @param omClasses Template replacable object model implementations for Template and TemplateLocator.
     *                  Required order, with second optional: [ <code>Template</code>, <code>TemplateLocator</code> implementations. 
     * @param defaultLocatorType Under root directories, subdirectories represent locator types.
     *                           A locator type represents a classification of templates.
     *                           Any value is allowed. Use locator types to group templates together. 
     */
    public JetspeedTemplateLocator(List roots, 
                                   List omClasses,
                                   String defaultLocatorType,
                                   String appRoot) throws FileNotFoundException
    {
        this(roots,  defaultLocatorType, appRoot);                
      
        if (omClasses.size() > 0)
        {
            this.templateClass = (Class)omClasses.get(0);
            if (omClasses.size() > 1)
            {
                this.locatorClass  = (Class)omClasses.get(1);
            }
        }        
    }
    
    public TemplateDescriptor locateTemplate(LocatorDescriptor locator)
    {
        for (int ix = 0; ix < roots.size(); ix++)
        {        
            TemplateDescriptor template = locateTemplate(locator, (String)roots.get(ix), this.useNameCache);
            if (null == template)
            {
                // Try to locate it directly on file system, perhaps it was recently added
                template = locateTemplate(locator, (String)roots.get(ix), false);
                if (null != template)
                {
                    // add it to the map
                    templateMap.put(template.getAbsolutePath(), null);
                }
            }
            if (template != null)
            {
                return template;
            }
        }
        return null;        
    }
 
    /**
     * General template location algorithm. Starts with the most specific resource,
     * including mediatype + nls specification, and fallsback to least specific.
     *
     * @param locator The template locator 
     * @param root The root directory to search
     *
     * @return TemplateDescriptor the exact path to the template, or null if not found.
     */
    private TemplateDescriptor locateTemplate(LocatorDescriptor locator, String root, boolean useCache)
    {
        String templateName = locator.getName();       
        String path = locator.toPath();
                
        String realPath = null;
        String workingPath = null;

        int lastSeperator;
        while (path !=null && (lastSeperator = path.lastIndexOf(PATH_SEPARATOR))> 0)
        {
            path = path.substring(0, lastSeperator);

            workingPath = path + PATH_SEPARATOR + templateName;
            realPath = root + workingPath;

            // the current template exists, return the corresponding path
            if (templateExists(realPath, useCache))
            {
                if (log.isDebugEnabled())
                {
                    log.debug(
                            "TemplateLocator: template exists: "
                                + realPath
                                + " returning "
                                + workingPath);
                }
                int appRootLength = appRoot.length();
                // remove the application root path from the reall path to
                // give us a app relative path
                String appRelativePath = realPath.substring(appRootLength, realPath.length());
                // return createTemplateFromPath(path, templateName, realPath, "/WEB-INF/templates" + workingPath);
                return createTemplateFromPath(path, templateName, realPath, appRelativePath);
            }
        }
        return null;
    }

    /**
     * Checks for the existence of a template resource given a key.
     * The key are absolute paths to the templates, and are cached
     * in a template cache for performance.
     *
     * @param key The absolute path to the template resource.
     *
     * @return True when the template is found, otherwise false.
     */
    public boolean templateExists(String templateKey, boolean useCache)
    {
        if (null == templateKey)
        {
            return false;
        }
        if (useCache == true)
        {
            return templateMap.containsKey(templateKey);
        }
        return (new File(templateKey).exists());
    }

    public boolean templateExists(String templateKey)
    {
        return templateExists(templateKey, this.useNameCache);
    }
   
    public LocatorDescriptor createFromString(String path)
        throws TemplateLocatorException
    {
        LocatorDescriptor locator = createLocatorDescriptor(this.defaultLocatorType);
        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens())
        {
            String name = tok.nextToken();
            if (name.equals(LocatorDescriptor.PARAM_TYPE) && tok.hasMoreTokens())
            {
                locator.setType( tok.nextToken() );
            }
            else if (name.equals(LocatorDescriptor.PARAM_MEDIA_TYPE) && tok.hasMoreTokens())
            {
                locator.setMediaType(tok.nextToken());
            }
            else if (name.equals(LocatorDescriptor.PARAM_LANGUAGE) && tok.hasMoreTokens())
            {
                locator.setLanguage(tok.nextToken());
            }
            else if (name.equals(LocatorDescriptor.PARAM_COUNTRY) && tok.hasMoreTokens())
            {
                locator.setCountry(tok.nextToken());
            }
        
            else if (name.equals(LocatorDescriptor.PARAM_NAME) && tok.hasMoreTokens())
            {
                locator.setName(tok.nextToken());
            }
        }    
        return locator;    
    }

    /**
     * Given a path, name and realPath creates a new template object
     * 
     * @param path the relative path to the template
     * @param name the template name
     * @param realPath the real path on the file system
     * @return newly created TemplateDescriptor
     */
    private TemplateDescriptor createTemplateFromPath(String path, String name, String realPath, String relativePath)
    {    
        TemplateDescriptor template = this.createTemplate();
        template.setAbsolutePath(realPath);
        if(relativePath.indexOf("/") != 0)
        {
            relativePath = "/"+relativePath;
        }
        template.setAppRelativePath(relativePath);
        template.setName(name);            
        StringTokenizer tok = new StringTokenizer(path, "/");
        int count = 0;
        while (tok.hasMoreTokens())
        {
            String token = tok.nextToken();
            switch (count)
            {
                case 0:
                    template.setType(token);
                    break;
                case 1:
                    template.setMediaType(token);
                    break;
                case 2:
                    template.setLanguage(token);
                    break;
                case 3:                    
                    template.setCountry(token);
                    break;                                    
            }
            count++;
        }    
        return template;                                                    
    }
    
    public LocatorDescriptor createLocatorDescriptor(String type)
        throws TemplateLocatorException
    {
        LocatorDescriptor locator = null;
            
        try
        {
            locator = (LocatorDescriptor)locatorClass.newInstance();
            locator.setType(type);
        }
        catch(Exception e)
        {
            throw new TemplateLocatorException("Failed instantiate a Template Locator implementation object: ", e);
        }
        return locator;    
    }

    private TemplateDescriptor createTemplate()
    {
        TemplateDescriptor template = null;
            
        try
        {
            template = (TemplateDescriptor)templateClass.newInstance();
        }
        catch(Exception e)
        {
            log.error("Failed to create template", e);
            template = new JetspeedTemplateDescriptor();            
        }
        return template;    
    }

    public void start()
    {        
        this.templateMap = Collections.synchronizedMap(new HashMap());

        for (int ix = 0; ix < roots.size(); ix++)
        {
            String templateRoot = (String)roots.get(ix);

            if (!templateRoot.endsWith(PATH_SEPARATOR))
            {
                templateRoot = templateRoot + PATH_SEPARATOR;
            }

            loadNameCache(templateRoot, "");
        }
    }

    public void stop()
    {
    }
  
    public Iterator query(LocatorDescriptor locator)
    {
        return null; // TODO: implement this
    }

    /**
     * Loads the template name cache map to accelerate template searches.
     *
     * @param path The template
     * @param name just the name of the resource
     */
    private void loadNameCache(String path, String name)
    {
        File file = new File(path);
        if (file.isFile())
        {
            // add it to the map
            templateMap.put(path, null);
        }
        else
        {
            if (file.isDirectory())
            {
                if (!path.endsWith(File.separator))
                {
                    path += File.separator;
                }

                String list[] = file.list();

                // Process all files recursivly
                for (int ix = 0; list != null && ix < list.length; ix++)
                {
                    loadNameCache(path + list[ix], list[ix]);
                }
            }
        }
    }
    
    public List<String> getTemplateRoots()
    {
        List<String> result = new ArrayList<String>(this.roots);
        return result;
    }
}
