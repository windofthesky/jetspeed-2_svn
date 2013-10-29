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
package org.apache.jetspeed.layout.impl;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.BaseFragmentElement;
import org.apache.jetspeed.om.page.BaseFragmentsElement;
import org.apache.jetspeed.om.page.DynamicPage;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.Page;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Update Page action -- updates various parts of the PSML page
 * 
 * AJAX Parameters: 
 *    action = updatepage
 *    General methods:
 *    method = add | remove 
 *    Info methods:
 *    | info 
 *    Meta methods:
 *    | add-meta | update-meta | remove-meta
 *    Security methods:
 *    | add-secref | remove-secref
 *    Fragment methods:
 *    | update-fragment | add-fragment | remove-fragment
 *    
 *    update-fragment params: id, layout(name), sizes, layoutid (add)
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class UpdatePageAction 
    extends BaseSiteUpdateAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Logger log = LoggerFactory.getLogger(UpdatePageAction.class);
    
    public UpdatePageAction(String template, 
                            String errorTemplate, 
                            PageManager pm,
                            PortletActionSecurityBehavior securityBehavior)                            
                            
    {
        super(template, errorTemplate, pm, securityBehavior);
    }
    
    public boolean run(RequestContext requestContext, Map<String,Object> resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "updatepage");
            // Get the necessary parameters off of the request
            String method = getActionParameter(requestContext, "method");
            if (method == null) 
            { 
                throw new RuntimeException("Method not provided"); 
            }            
            resultMap.put("method", method);
            if (false == checkAccess(requestContext, JetspeedActions.EDIT))
            {
                success = false;
                resultMap.put(REASON, "Insufficient access to administer portal permissions");                
                return success;
            }           
            int count = 0;
            BaseFragmentsElement page = null;            
            String path = getActionParameter(requestContext, "path");
            if (path == null)
            {
                page = requestContext.getPage().getPageOrTemplate();
            }
            else
            {
                if (!method.equals("add"))
                {
                    if (path.endsWith(Page.DOCUMENT_TYPE))
                    {
                        page = pageManager.getPage(path);
                    }
                    else if (path.endsWith(DynamicPage.DOCUMENT_TYPE))
                    {
                        page = pageManager.getDynamicPage(path);
                    }
                    else
                    {
                        success = false;
                        resultMap.put(REASON, "Can't lookup page by document type: " + path);                
                        return success;                        
                    }
                }
                else
                {
                    if (pageManager.pageExists(path) || pageManager.dynamicPageExists(path))
                    {
                        success = false;
                        resultMap.put(REASON, "Can't create: Page already exists: " + path);                
                        return success;                
                    }
                }
            }
            if (page == null)
            {
                throw new AJAXException("Missing current page or 'path' parameter");
            }
            
            if (method.equals("info"))
            {
                count = updateInformation(requestContext, resultMap, page, path);
            }
            else if (method.equals("add-meta"))
            {
                count = insertMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("update-meta"))
            {
                count = updateMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("remove-meta"))
            {
                count = removeMetadata(requestContext, resultMap, page);
            }
            else if (method.equals("add-secref"))
            {
                count = insertSecurityReference(requestContext, resultMap, page);
            }
            else if (method.equals("update-secref"))
            {
                count = updateSecurityReference(requestContext, resultMap, page);
            }                        
            else if (method.equals("remove-secref"))
            {
                count = removeSecurityReference(requestContext, resultMap, page);
            }
            else if (method.equals("remove-secdef"))
            {
                count = removeSecurityDef(requestContext, resultMap, page);
            }            
            else if (method.equals("add"))
            {
                page = pageManager.newPage(path);
                page.setTitle(getActionParameter(requestContext, TITLE));
                String s = getActionParameter(requestContext, SHORT_TITLE );
                if (!isBlank(s))
                    page.setShortTitle(s);
                String l = getActionParameter(requestContext, DEFAULT_LAYOUT);
                if (!isBlank(l) && (page.getRootFragment() instanceof Fragment))
                {
                    Fragment rootFragment = (Fragment)page.getRootFragment();
                    rootFragment.setName(getActionParameter(requestContext, DEFAULT_LAYOUT));
                }
                count++;                
            }
            else if (method.equals("copy"))
            {            	
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);
            	destination = destination + Folder.PATH_SEPARATOR + name;
            	if (page instanceof Page)
            	{
            	    Page newPage = pageManager.copyPage((Page)page,destination);
            	    pageManager.updatePage(newPage);
            	}
            	else if (page instanceof DynamicPage)
            	{
                    DynamicPage newPage = pageManager.copyDynamicPage((DynamicPage)page,destination);
                    pageManager.updateDynamicPage(newPage);            	    
            	}
            }
            else if (method.equals("move"))
            {            	
            	String destination = getActionParameter(requestContext, "destination");
            	String name = getActionParameter(requestContext, RESOURCE_NAME);            	
            	destination = destination + Folder.PATH_SEPARATOR + name;
                if (page instanceof Page)
                {
                    Page newPage = pageManager.copyPage((Page)page, destination, true);
                    pageManager.updatePage(newPage);
                    pageManager.removePage((Page)page);
                }
                else if (page instanceof DynamicPage)
                {
                    DynamicPage newPage = pageManager.copyDynamicPage((DynamicPage)page, destination, true);
                    pageManager.updateDynamicPage(newPage);
                    pageManager.removeDynamicPage((DynamicPage)page);
                }
            } 
            else if (method.equals("remove"))
            {
                if (page instanceof Page)
                {
                    pageManager.removePage((Page)page);
                }
                else if (page instanceof DynamicPage)
                {
                    pageManager.removeDynamicPage((DynamicPage)page);
                }
            }
            else if (method.equals("update-fragment"))
            {
                String fragmentId = getActionParameter(requestContext, PORTLETID);
                String layout = getActionParameter(requestContext, LAYOUT);                
                if (isBlank(fragmentId) || isBlank(layout))
                {
                    resultMap.put(REASON, "Missing parameter to update fragment");                
                    return false;                    
                }                
                count = updateFragment(requestContext, resultMap, page, fragmentId, layout);
            }
            else if (method.equals("add-fragment"))
            {
                String parentId = getActionParameter(requestContext, LAYOUTID);
                String layout = getActionParameter(requestContext, LAYOUT);                
                if (isBlank(parentId) || isBlank(layout))
                {
                    resultMap.put(REASON, "Missing parameter to add fragment");                
                    return false;                    
                }                
                count = addFragment(requestContext, resultMap, page, parentId, layout);
            }
            else if (method.equals("remove-fragment"))
            {
                String fragmentId = getActionParameter(requestContext, PORTLETID);
                if (isBlank(fragmentId))
                {
                    resultMap.put(REASON, "Missing parameter to remove fragment");                
                    return false;                    
                }                
                count = removeFragment(requestContext, resultMap, page, fragmentId);                
            }
            else if (method.equals("update-portlet-decorator"))
            {
                String fragmentId = getActionParameter(requestContext, PORTLETID);
            	String portletDecorator = getActionParameter(requestContext, "portlet-decorator");
                if (isBlank(fragmentId) || isBlank(portletDecorator))
                {
                    resultMap.put(REASON, "Missing parameter to update portlet decorator");                
                    return false;                    
                }                
                count = updatePortletDecorator(requestContext, resultMap, page, fragmentId, portletDecorator);
            }
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported Site Update method: " + method);                
                return success;                
            }
            if (count > 0)
            {
                if (page instanceof Page)
                {
                    pageManager.updatePage((Page)page);
                }
                else
                {
                    pageManager.updateDynamicPage((DynamicPage)page);                    
                }
            }                        
            resultMap.put("count", Integer.toString(count));
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception administering Site update", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }
    
    protected int updatePortletDecorator(RequestContext requestContext, Map<String,Object> resultMap, BaseFragmentsElement page, String fragmentId, String portletDecorator)
    {
    	int count = 0;
    	BaseFragmentElement fragment = page.getFragmentById(fragmentId);
        if (fragment != null)
        {                
        	fragment.setDecorator( portletDecorator );
        	count++;
        }
    	return count;
    }
    
    protected int updateFragment(RequestContext requestContext, Map<String,Object> resultMap, BaseFragmentsElement page, String fragmentId, String layout)
    {
        int count = 0;
        String sizes = getActionParameter(requestContext, SIZES);
        BaseFragmentElement updateFragment = page.getFragmentById(fragmentId);
        if (updateFragment instanceof Fragment)
        {
            Fragment fragment = (Fragment)updateFragment;
            if (!layout.equals(fragment.getName()))
            {
                fragment.setName(layout);
                count++;
                if ( isBlank(sizes) )
                {
                    fragment.setLayoutSizes(null);
                }
                else
                {
                    fragment.setLayoutSizes(sizes);
                }
                count++;
            }
            else
            {
                if (!isBlank(sizes))
                {
                    fragment.setLayoutSizes(sizes);
                    count++;
                }
            }
        }
        return count;
    }

    protected int addFragment(RequestContext requestContext, Map<String,Object> resultMap, BaseFragmentsElement page, String parentFragmentId, String layout)
    {
        int count = 0;
        String sizes = getActionParameter(requestContext, SIZES);
        BaseFragmentElement parentFragment = page.getFragmentById(parentFragmentId);
        if (parentFragment instanceof Fragment)
        {
            Fragment fragment = (Fragment)parentFragment;
            Fragment newFragment = pageManager.newFragment();
            newFragment.setType(Fragment.LAYOUT);            
            newFragment.setName(layout);
            fragment.getFragments().add(newFragment);            
            resultMap.put(PORTLETID, newFragment.getId());                        
            count++;
            if (!isBlank(sizes))
            {
                newFragment.setLayoutSizes(sizes);
                count++;
            }
        }
        return count;
    }

    protected int removeFragment(RequestContext requestContext, Map<String,Object> resultMap, BaseFragmentsElement page, String fragmentId)
    {
        int count = 0;
        BaseFragmentElement fragment = page.getFragmentById(fragmentId);
        if (fragment != null)
        {
            page.removeFragmentById(fragment.getId());
            count++;
        }
        return count;
    }    
        
    protected int updateInformation(RequestContext requestContext, Map<String,Object> resultMap, Node node, String path)
    throws AJAXException    
    {
        int count = 0;
        try
        {
            Page page = (Page)node;            
            String title = getActionParameter(requestContext, "title");
            if (title != null && isFieldModified(title, page.getTitle()))
                page.setTitle(title);
            String shortTitle = getActionParameter(requestContext, "short-title");
            if (shortTitle != null && isFieldModified(shortTitle, page.getShortTitle()))
                page.setShortTitle(shortTitle);
            String layoutDecorator = getActionParameter(requestContext, "layout-decorator");
            if (layoutDecorator != null && isFieldModified(layoutDecorator, page.getDefaultDecorator(Fragment.LAYOUT)))
            {
                if (isBlank(layoutDecorator))
                    layoutDecorator = null; 
                page.setDefaultDecorator(layoutDecorator, Fragment.LAYOUT);
            }
            String portletDecorator = getActionParameter(requestContext, "portlet-decorator");
            if (portletDecorator != null && isFieldModified(portletDecorator, page.getDefaultDecorator(Fragment.PORTLET)))
            {
                if (isBlank(portletDecorator))
                    portletDecorator = null;                 
                page.setDefaultDecorator(portletDecorator, Fragment.PORTLET);
            }
            String theme = getActionParameter(requestContext, "theme");
            if (theme != null && isFieldModified(theme, page.getSkin()))
            {
                if (isBlank(theme))
                    theme = null;                 
                page.setSkin(theme);
            }
            String hidden = getActionParameter(requestContext, "hidden");
            if (hidden != null && isBooleanModified(hidden, page.isHidden()))
                page.setHidden(!page.isHidden());                                    
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
    
}
