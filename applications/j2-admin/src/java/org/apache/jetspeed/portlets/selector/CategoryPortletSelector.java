/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.selector;

import java.io.IOException;
import java.security.AccessControlException;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;
import org.apache.jetspeed.headerresource.HeaderResource;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.common.preference.PreferenceComposite;
import org.apache.jetspeed.portlets.CategoryInfo;
import org.apache.jetspeed.portlets.PortletInfo;
import org.apache.jetspeed.search.ParsedObject;
import org.apache.jetspeed.search.SearchEngine;
import org.apache.jetspeed.security.PortletPermission;
import org.apache.pluto.om.common.Parameter;
import org.apache.portals.gems.dojo.AbstractDojoVelocityPortlet;
import org.apache.velocity.context.Context;

/**
 * CategoryPortletSelector selects categories organized by categories
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class CategoryPortletSelector extends AbstractDojoVelocityPortlet implements Comparator
{
    public final String[] DEFAULT_IMAGES = new String[]
    {
            "images/portlets/applications-development.png",
            "images/portlets/applications-system.png",
            "images/portlets/applications-other.png",
            "images/portlets/linux.png"
    };
    protected final Log logger = LogFactory.getLog(this.getClass());
    public final static String PORTLET_ICON = "portlet-icon";
    protected final static String PORTLETS = "category.selector.portlets";
    protected final static String CATEGORIES = "category.selector.categories";
    protected final static String PAGE = "category.selector.page";
    public final static String JSPAGE = "jspage";
    
    protected PortletRegistry registry;
    protected SearchEngine searchEngine;
    protected Random rand;
    
    public void init(PortletConfig config)
    throws PortletException 
    {
        super.init(config);
        PortletContext context = getPortletContext();                
        registry = (PortletRegistry)context.getAttribute(CommonPortletServices.CPS_REGISTRY_COMPONENT);
        if (null == registry)
        {
            throw new PortletException("Failed to find the Portlet Registry on portlet initialization");
        }        
        searchEngine = (SearchEngine)context.getAttribute(CommonPortletServices.CPS_SEARCH_COMPONENT);
        if (null == searchEngine)
        {
            throw new PortletException("Failed to find the Search Engine on portlet initialization");
        }
        rand = new Random( 19580427 );
    }
    
    public void doView(RenderRequest request, RenderResponse response)
            throws PortletException, IOException
    {
        PortletPreferences prefs = request.getPreferences();
        this.getContext(request).put("Columns", prefs.getValue("Columns", "4"));
        this.getContext(request).put("Rows", prefs.getValue("Rows", "6"));
        this.getContext(request).put("portlets", retrievePortlets(request, null));
        this.getContext(request).put("categories", retrieveCategories(request));
        processPage(request);
        super.doView(request, response);
    }

    protected void processPage(RenderRequest request)
    {
        String page = request.getParameter(JSPAGE);
        if (page == null || page.equals(""))
        {
            page = (String)request.getPortletSession().getAttribute(PAGE);
        }
        else
        {
            request.getPortletSession().setAttribute(PAGE, page);
        }
        this.getContext(request).put(JSPAGE, page);
        
    }
    
    public List retrieveCategories(RenderRequest request)
    throws PortletException
    {
        List categories = (List)request.getPortletSession().getAttribute(CATEGORIES);
        if (categories != null)
        {
            return categories;
        }
        Locale locale = request.getLocale();        
        categories = new ArrayList();
        PortletPreferences prefs = request.getPreferences();
        String cats = prefs.getValue("Categories", null);
        if (cats == null)
        {
            throw new PortletException("No categories defined, please add categories via edit mode.");
        }
        StringTokenizer catTokenizer = new StringTokenizer(cats, ",");
        while (catTokenizer.hasMoreTokens())
        {
            String name = catTokenizer.nextToken().trim();
            CategoryInfo cat = new CategoryInfo(name);
            String keywords = prefs.getValue("Keywords:" + name, null);
            if (keywords != null)
            {
                StringTokenizer keyTokenizer = new StringTokenizer(keywords, ",");
                StringBuffer searchString = new StringBuffer();
                int count = 0;
                while (keyTokenizer.hasMoreTokens())
                {
                    String keyword = keyTokenizer.nextToken().trim();
                    if (count > 0)
                    {
                        searchString.append(" | ");
                    }
                    searchString.append(keyword);
                    count++;
                }
                if (count > 0)
                {
                    Iterator portlets = searchEngine.search(searchString.toString()).getResults().iterator();
                    while (portlets.hasNext())
                    {
                        PortletDefinitionComposite portlet = 
                            getPortletFromParsedObject((ParsedObject)portlets.next());
                        PortletInfo portletInfo = filterPortlet(portlet, locale);
                        if (portletInfo != null)
                        {
                            cat.addPortlet(portletInfo);
                        }                    
                    }                
                    Collections.sort(cat.getPortlets(), this);
                    categories.add(cat);
                }
            }
        }
        request.getPortletSession().setAttribute(CATEGORIES, categories);        
        return categories;
    }
    
    public List retrievePortlets(RenderRequest request, String filter)
    {
        List portletsList = (List)request.getPortletSession().getAttribute(PORTLETS);
        if (portletsList != null)
        {
            return portletsList;
        }        
        Iterator portlets = null;
        List list = new ArrayList();
        Locale locale = request.getLocale();                
        if (filter == null)
            portlets = registry.getAllPortletDefinitions().iterator();
        else
            portlets = searchEngine.search(filter).getResults().iterator();
        
        while (portlets.hasNext())
        {
            PortletDefinitionComposite portlet = null;
            if (filter == null)
                portlet = (PortletDefinitionComposite)portlets.next();
            else
                portlet = getPortletFromParsedObject((ParsedObject)portlets.next());
            
            PortletInfo portletInfo = filterPortlet(portlet, locale);
            if (portletInfo != null)
            {
                list.add(portletInfo);
            }
        }            
        Collections.sort(list, this);
        request.getPortletSession().setAttribute(PORTLETS, list);
        return list;
    }

    
    
    /**
     * Filters portlets being added to the based on security checks and layout criteria
     * 
     * @param portlet
     * @return null if filtered, otherwise PortletInfo to be added to list
     */
    protected PortletInfo filterPortlet(PortletDefinitionComposite portlet, Locale locale)
    {
        if (portlet == null)
            return null;
        
        MutablePortletApplication muta = (MutablePortletApplication)portlet.getPortletApplicationDefinition();
        String appName = muta.getName();
        if (appName != null && appName.equals("jetspeed-layouts"))
            return null;                
        
        // SECURITY filtering
        String uniqueName = appName + "::" + portlet.getName();
        try
        {
            AccessController.checkPermission(new PortletPermission(portlet.getUniqueName(), JetspeedActions.MASK_VIEW));
            Parameter param = portlet.getInitParameterSet().get(PORTLET_ICON);
            String image;
            if (param != null)
            {                
                //String relativeImagePath = param.getValue();
                //String context = muta.getWebApplicationDefinition().getContextRoot();
                // Have to use a supported icon in jetspeed, otherwise image can be out of skew
                String  imagePath = param.getValue();
                if (imagePath == null)
                {
                    image = DEFAULT_IMAGES[rand.nextInt(DEFAULT_IMAGES.length)];
                }
                else
                {
                    if (-1 == imagePath.indexOf("/"))
                        image = "images/portlets/" + param.getValue();
                    else
                        image = param.getValue();
                }
            }
            else
            {
                image = DEFAULT_IMAGES[rand.nextInt(DEFAULT_IMAGES.length)];
            }
            return new PortletInfo(uniqueName, cleanup(portlet.getDisplayNameText(locale)), cleanup(portlet.getDescriptionText(locale)), image);
        }
        catch (AccessControlException ace)
        {
            return null;
        }
        
    }
    
    protected String cleanup(String str)
    {
        if (str == null)
            return str;
        return str.replaceAll("\r|\n|\"|\'", "");
    }
    
    protected PortletDefinitionComposite getPortletFromParsedObject(ParsedObject po)
    {
        boolean found = false;
        String name = "";
        Map fields = po.getFields();
        if(fields != null)
        {
            Object id = fields.get("ID");
    
            if(id != null)
            {
                if(id instanceof Collection)
                {
                    Collection coll = (Collection)id;
                    name = (String) coll.iterator().next();
                }
                else
                {
                    name = (String)id;
                }
            }
            
            if(po.getType().equals("portlet"))
            {
                Object pa = fields.get("portlet_application");
                String paName = "";
                if(pa != null)
                {
                    if(id instanceof Collection)
                    {
                        Collection coll = (Collection)pa;
                        paName = (String) coll.iterator().next();
                    }
                    else
                    {
                        paName = (String)pa;
                    }
                }
                name = paName + "::" + name;
                found = true;
            }
        }
        if (found == false)
            return null;
        
        return registry.getPortletDefinitionByUniqueName(name);
    }
        
    
    public void processAction(ActionRequest request,
            ActionResponse actionResponse) throws PortletException, IOException
    {
        if (request.getPortletMode() == PortletMode.EDIT)
        {
            String removes = request.getParameter("jsRemovedCats");
            String modifiedCats = request.getParameter("jsModifiedCats");
            String modifiedKeys = request.getParameter("jsModifiedKeys");
            String addedCats = request.getParameter("jsAddedCats");
            String addedKeys = request.getParameter("jsAddedKeys");
            String columns = request.getParameter("Columns");
            String rows = request.getParameter("Rows");
            String cats = request.getParameter("jsCats");
            String page = request.getParameter(JSPAGE);
            MutablePortletApplication pa = registry.getPortletApplication("j2-admin");
            String portletName = this.getPortletName();
            PortletDefinitionComposite portlet = (PortletDefinitionComposite) pa.getPortletDefinitionByName(portletName);
            boolean updated = updateNumericPref("Columns", columns, 10, portlet);
            updated = updated | updateNumericPref("Rows", rows, 100, portlet);
                        
            // process removes first
            if (!isEmpty(removes))
            {
                StringTokenizer tokenizer = new StringTokenizer(removes, ",");
                while (tokenizer.hasMoreTokens())
                {
                    String name = tokenizer.nextToken().trim();
                    updated = updated | removePref("Keywords:" + name, portlet);
                }                
            }
            
            // process adds
            if (!isEmpty(addedCats))
            {
                StringTokenizer keyTokenizer = new StringTokenizer(addedKeys, "|");
                StringTokenizer tokenizer = new StringTokenizer(addedCats, ",");
                while (tokenizer.hasMoreTokens() && keyTokenizer.hasMoreTokens())
                {
                    String name = tokenizer.nextToken().trim();
                    String keys = keyTokenizer.nextToken().trim();
                    updated = updated | addPref("Keywords:" + name, keys, portlet);
                }                
            }
            
            // process updates
            if (!isEmpty(modifiedCats))
            {
                StringTokenizer keyTokenizer = new StringTokenizer(modifiedKeys, "|");
                StringTokenizer tokenizer = new StringTokenizer(modifiedCats, ",");
                while (tokenizer.hasMoreTokens())
                {
                    String name = tokenizer.nextToken().trim();
                    String keys = keyTokenizer.nextToken().trim();                    
                    updated = updated | modifyPref("Keywords:" + name, keys, portlet);
                }                
            }
                                    
            try
            {
                if (updated)
                {
                    // process category string list
                    // sort it first
                    StringTokenizer catTokenizer = new StringTokenizer(cats, ",");
                    List sorted = new ArrayList();
                    while (catTokenizer.hasMoreTokens())
                    {
                        String name = catTokenizer.nextToken().trim();
                        sorted.add(name);
                    }
                    Collections.sort(sorted);
                    Iterator si = sorted.iterator();
                    StringBuffer temp = new StringBuffer();
                    int count = 0;
                    while (si.hasNext())
                    {
                        String name = (String)si.next();
                        if (count > 0)
                        {
                            temp.append(",");
                        }
                        temp.append(name);
                        count++;
                    }
                    cats = temp.toString();
                    System.out.println("cats = [" + cats + "]");
                    modifyPref("Categories", cats, portlet);
                    
                    // finally save it all
                    registry.savePortletDefinition(portlet);
                }
            }
            catch (Exception e)
            {
                throw new PortletException("Failed to update portlet", e);
            }
            PortletSession session = request.getPortletSession();
            session.removeAttribute(PORTLETS);
            session.removeAttribute(CATEGORIES);
            actionResponse.setPortletMode(PortletMode.VIEW);
            actionResponse.setRenderParameter(JSPAGE, page);
        }
        else
        {
            String reset = request.getParameter("reset");        
            if (reset != null && reset.equals("true"))
            {
                PortletSession session = request.getPortletSession();
                session.removeAttribute(PORTLETS);
                session.removeAttribute(CATEGORIES);
            }
        }
    }

    private boolean addPref(String prefName, String keywords, PortletDefinitionComposite portlet)
    {
        PreferenceComposite pref = (PreferenceComposite) portlet.getPreferenceSet().get(prefName);        
        if (pref == null)
        {
            portlet.addPreference(prefName, new String[] { keywords });
        }
        else
        {
            return modifyPref(prefName, keywords, portlet);           
        }        
        return true;
    }
    
    private boolean modifyPref(String prefName, String keywords, PortletDefinitionComposite portlet)
    {
        PreferenceComposite prefComp = (PreferenceComposite) portlet.getPreferenceSet().get(prefName);
        String[] values = prefComp.getValueArray();
        if(!values[0].equals(keywords))
        {
            prefComp.setValueAt(0, keywords);
            return true;
        }
        return false;
    }
    
    private boolean removePref(String prefName, PortletDefinitionComposite portlet)
    {
        Iterator prefIter = portlet.getPreferenceSet().iterator();
        while (prefIter.hasNext())
        {
            PreferenceComposite pref = (PreferenceComposite) prefIter.next();
            String name = pref.getName();
            if (name.equals(prefName))
            {
                    prefIter.remove();
                    return true;
            }
        }
        return false;
    }
    
    private boolean updateNumericPref(String prefName, String param, int max, PortletDefinitionComposite portlet)
    {
        if (!isEmpty(param))
        {
            int val = 4;
            try
            {
                val = Integer.parseInt(param);
            }
            catch (NumberFormatException e)
            {
                return false;                
            }
            if (val > max)
                return false;
            PreferenceComposite pref = (PreferenceComposite) portlet.getPreferenceSet().get(prefName);
            String[] values = pref.getValueArray();
            if(!values[0].equals(param))
            {
                pref.setValueAt(0, param);
                return true;
            }                           
        }
        return false;
    }
    private boolean isEmpty(String param)
    {
        if (param == null)
            return true;
        param = param.trim();
        if (param.length() == 0)
            return true;
        return false;
    }

    public int compare(Object obj1, Object obj2)
    {
        PortletInfo portlet1 = (PortletInfo)obj1;
        PortletInfo portlet2 = (PortletInfo)obj2;
        String name1 = portlet1.getDisplayName();
        String name2 = portlet2.getDisplayName();
        name1 = (name1 == null) ? "unknown" : name1;
        name2 = (name2 == null) ? "unknown" : name2;
        return name1.compareTo(name2);
    }
        
    protected void includeHeaderContent(HeaderResource headerResource)
    {
        headerResource.dojoAddCoreLibraryRequire( "dojo.widget.Dialog" );
        headerResource.dojoAddCoreLibraryRequire( "dojo.widget.Button" );
        headerResource.dojoAddCoreLibraryRequire( "dojo.widget.ContentPane" );
        headerResource.dojoAddCoreLibraryRequire( "dojo.widget.LayoutContainer" );
        headerResource.dojoAddModuleLibraryRequire( "jetspeed.desktop.core" );        
    }

    /* (non-Javadoc)
     * @see org.apache.portals.bridges.velocity.GenericVelocityPortlet#doEdit(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    public void doEdit(RenderRequest request, RenderResponse response) throws PortletException, IOException
    {
        Context context = getContext(request);
        PortletPreferences prefs = request.getPreferences();
        String cats = prefs.getValue("Categories", null);
        List categories = new ArrayList();
        if (cats != null)
        {
            StringTokenizer catTokenizer = new StringTokenizer(cats, ",");
            while (catTokenizer.hasMoreTokens())
            {
                String name = catTokenizer.nextToken().trim();
                CategoryInfo cat = new CategoryInfo(name);
                String keys = prefs.getValue("Keywords:" + name, "");
                cat.setKeywords(keys);
                categories.add(cat);
            }
        }
        context.put("categories", categories);
        context.put("Rows", prefs.getValue("Rows", "5"));
        context.put("Columns", prefs.getValue("Columns", "4"));
        processPage(request);
        super.doEdit(request, response);
    }
}
