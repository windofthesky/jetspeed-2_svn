/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.cps.template;

import java.util.Iterator;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picocontainer.Startable;

/**
 * TemplateLocatorComponentImpl
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TemplateLocatorComponentImpl implements TemplateLocatorComponent, Startable
{
    private final static Log log = LogFactory.getLog(TemplateLocatorServiceImpl.class);

    private static final String PATH_SEPARATOR = "/";

    /** the template root directories, all application root relative */
    private List roots;
       
    /** the Template class is factory created */     
    private Class  templateClass = TemplateImpl.class;

    /** the TemplateLocator class is factory created */     
    private Class  locatorClass = TemplateLocatorImpl.class;
    
    /** the default locator type */
    private String defaultLocatorType = "layout";

    /** template name cache used to speed up searches for templates */
    private Map templateMap = null;

    /** use the name cache when looking up a template */
    private boolean useNameCache = true;

    private TemplateLocatorComponentImpl()
    {
        // need to know roots
    }
    
    /**
     * Minimal assembly with a list of resource directory roots.
     * 
     * @param roots A list of resource root directories where templates are located. 
     */
    public TemplateLocatorComponentImpl(List roots) 
    {
        this.roots = roots;        
    }

    /**
     * Construct with a root list and a default locator type.
     * 
     * @param roots A list of resource root directories where templates are located.
     * @param defaultLocatorType Under root directories, subdirectories represent locator types.
     *                           A locator type represents a classification of templates.
     *                           Any value is allowed. Use locator types to group templates together. 
     */
    public TemplateLocatorComponentImpl(List roots, 
                                        String defaultLocatorType)
    {
        this.roots = roots;        
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
    public TemplateLocatorComponentImpl(List roots, 
                                        List omClasses,
                                        String defaultLocatorType)
    {
        System.out.println("Initializing template locator component: " + locatorClass.getName()); 
        this.roots = roots;
        this.defaultLocatorType = defaultLocatorType;
        if (omClasses.size() > 0)
        {
            this.templateClass = (Class)omClasses.get(0);
            if (omClasses.size() > 1)
            {
                this.locatorClass  = (Class)omClasses.get(1);
            }
        }        
    }
    

            
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocatorService#locateTemplate(org.apache.jetspeed.cps.template.TemplateLocator)
     */
    public Template locateTemplate(TemplateLocator locator)
    {
        for (int ix = 0; ix < roots.size(); ix++)
        {        
            Template template = locateTemplate(locator, (String)roots.get(ix));
            if (null == template)
            {
                // Try to locate it directly on file system, perhaps it was recently added
                useNameCache = false;
                template = locateTemplate(locator, (String)roots.get(ix));
                if (null != template)
                {
                    // add it to the map
                    templateMap.put(template.getAbsolutePath(), null);
                }
                useNameCache = true;   
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
     * @return Template the exact path to the template, or null if not found.
     */
    private Template locateTemplate(TemplateLocator locator, String root)
    {
        String templateName = locator.getName();       
        String path = locator.toPath();
        
        int last = path.lastIndexOf(PATH_SEPARATOR);
        if (last > -1)
        {
            // drop off the name
            path = path.substring(0, last);
        }
        else
        {
            path = null;
        }                
                
        String basePath = root; 
        String realPath = null;
        String workingPath = null;
        
        do // fallback
        {
            workingPath = path + PATH_SEPARATOR + templateName;
            realPath = root + workingPath;

            // the current template exists, return the corresponding path
            if (templateExists(realPath))
            {
                if (log.isDebugEnabled())
                {
                    log.debug(
                            "TemplateLocator: template exists: "
                                + realPath
                                + " returning "
                                + workingPath);
                }
                return createTemplateFromPath(path, templateName, realPath, root + workingPath);
            }
            // else strip path of one of its components and loop
            int pt = path.lastIndexOf(PATH_SEPARATOR);
            if (pt > -1)
            {
                path = path.substring(0, pt);
            }
            else
            {
                path = null;
            }
        }
        while (path != null);

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
    public boolean templateExists(String templateKey)
    {
        if (null == templateKey)
        {
            return false;
        }
        if (useNameCache == true)
        {
            return templateMap.containsKey(templateKey);
        }
        return (new File(templateKey).exists());
    }
    
   
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocatorService#createFromString(java.lang.String)
     */
    public TemplateLocator createFromString(String path)
        throws TemplateLocatorException
    {
        TemplateLocator locator = createLocator(this.defaultLocatorType);
        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens())
        {
            String name = (String)tok.nextToken();
            if (name.equals(TemplateLocator.PARAM_TYPE) && tok.hasMoreTokens())
            {
                locator.setType( tok.nextToken() );
            }
            else if (name.equals(TemplateLocator.PARAM_MEDIA_TYPE) && tok.hasMoreTokens())
            {
                locator.setMediaType(tok.nextToken());
            }
            else if (name.equals(TemplateLocator.PARAM_LANGUAGE) && tok.hasMoreTokens())
            {
                locator.setLanguage(tok.nextToken());
            }
            else if (name.equals(TemplateLocator.PARAM_COUNTRY) && tok.hasMoreTokens())
            {
                locator.setCountry(tok.nextToken());
            }
        
            else if (name.equals(TemplateLocator.PARAM_NAME) && tok.hasMoreTokens())
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
     * @return newly created Template
     */
    private Template createTemplateFromPath(String path, String name, String realPath, String relativePath)
    {    
        Template template = this.createTemplate();
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
            String token = (String)tok.nextToken();
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
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocatorService#createLocator(String type)
     */
    public TemplateLocator createLocator(String type)
        throws TemplateLocatorException
    {
        TemplateLocator locator = null;
            
        try
        {
            locator = (TemplateLocator)locatorClass.newInstance();
            locator.setType(type);
        }
        catch(Exception e)
        {
            throw new TemplateLocatorException("Failed instantiate a Template Locator implementation object: ", e);
        }
        return locator;    
    }

    private Template createTemplate()
    {
        Template template= null;
            
        try
        {
            template = (Template)templateClass.newInstance();
        }
        catch(Exception e)
        {
            log.error("Failed to create template", e);
            template = new TemplateImpl();            
        }
        return template;    
    }

    public void start()
    {        
        this.templateMap = new HashMap();

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
  
    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.template.TemplateLocatorService#query(org.apache.jetspeed.cps.template.TemplateLocator)
     */
    public Iterator query(TemplateLocator locator)
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
}
