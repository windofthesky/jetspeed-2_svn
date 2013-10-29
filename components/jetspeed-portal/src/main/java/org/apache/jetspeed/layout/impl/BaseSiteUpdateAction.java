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

import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.layout.PortletActionSecurityBehavior;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.request.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Abstract Site update action for folders, pages and links
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class BaseSiteUpdateAction
    extends BasePortletAction
    implements AjaxAction, AjaxBuilder, Constants 
{
    protected static final Logger log = LoggerFactory.getLogger(BaseSiteUpdateAction.class);    
    
    public BaseSiteUpdateAction(String template, 
            String errorTemplate, 
            PageManager pageManager)
    {
        super(template, errorTemplate, pageManager);
    }
    
    public BaseSiteUpdateAction(String template, 
                             String errorTemplate, 
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, securityBehavior);
    }

    public BaseSiteUpdateAction(String template, 
                             String errorTemplate, 
                             PageManager pageManager,
                             PortletActionSecurityBehavior securityBehavior)
    {
        super(template, errorTemplate, pageManager, securityBehavior);        
    }

    protected abstract int updateInformation(RequestContext requestContext, Map<String,Object> resultMap, Node node, String path)
    throws AJAXException;
    
    protected int insertMetadata(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String language = getActionParameter(requestContext, "lang");
        String value = getActionParameter(requestContext, "value");
        if (isBlank(name) || isBlank(language))
            throw new AJAXException("Invalid Metadata: name, language invalid data.");
        Locale locale = new Locale(language);
        node.getMetadata().addField(locale, name, value);        
        return 1;
    }

    protected int updateMetadata(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String language = getActionParameter(requestContext, "lang");
        String value = getActionParameter(requestContext, "value");
        String oldName = getActionParameter(requestContext, "oldname");
        String oldLanguage = getActionParameter(requestContext, "oldlang");

        if (isBlank(name) || isBlank(language) || isBlank(oldName) || isBlank(oldLanguage))
            throw new AJAXException("Invalid Metadata: name, language invalid data.");
                
        Collection cfields = node.getMetadata().getFields(oldName);
        if (cfields == null || cfields.size() == 0)
        {
            return insertMetadata(requestContext, resultMap, node);            
        }
        boolean found = false;
        Iterator fields = cfields.iterator();
        while (fields.hasNext())
        {
            LocalizedField field  = (LocalizedField)fields.next();
            if (areFieldsSame(field.getName(), oldName) &&
                areFieldsSame(field.getLocale().toString(), oldLanguage))
            {
                field.setName(name);
                field.setLocale(new Locale(language));
                field.setValue(value);
                found = true;
                break;
            }
        }
        if (!found)
            return insertMetadata(requestContext, resultMap, node);
        return 1;
    }
    
    protected int removeMetadata(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String language = getActionParameter(requestContext, "lang");
        if (isBlank(name) || isBlank(language))
            throw new AJAXException("Invalid Metadata: name, language invalid data.");
        Collection cfields = node.getMetadata().getFields(name);
        Collection allFields = node.getMetadata().getFields();
        if (cfields == null || cfields.size() == 0)
        {
            return 0;            
        }
        boolean found = false;        
        Iterator fields = cfields.iterator();
        while (fields.hasNext())
        {
            LocalizedField field  = (LocalizedField)fields.next();
            if (areFieldsSame(field.getName(), name) &&
                areFieldsSame(field.getLocale().toString(), language))
            {
                cfields.remove(field);
                if (allFields.remove(field))
                {
                    node.getMetadata().setFields(allFields);
                }
                found = true;
                break;
            }
        }    
        
        return (found) ? 1 : 0;
    }

    protected int insertSecurityReference(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String kind = getActionParameter(requestContext, "kind");
        if (isBlank(name) || isBlank(kind))
            throw new AJAXException("Invalid Security Ref: name invalid data.");
        if (node.getSecurityConstraints() == null)
        {
            SecurityConstraints cons = node.newSecurityConstraints();
            node.setSecurityConstraints(cons);             
        }
        if (kind.equals("Owner"))
        {
            node.getSecurityConstraints().setOwner(name);
        }
        else
        {
            List refs = node.getSecurityConstraints().getSecurityConstraintsRefs();
            if (refs.contains(name))
                return 0; // do nothing
            refs.add(name);
        }
        return 1;        
    }

    protected int updateSecurityReference(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String oldName = getActionParameter(requestContext, "oldname");
        String kind = getActionParameter(requestContext, "kind");
        if (isBlank(name) || isBlank(oldName) || isBlank(kind))
            throw new AJAXException("Invalid Security Ref: name invalid data.");
        if (node.getSecurityConstraints() == null)
        {
            SecurityConstraints cons = node.newSecurityConstraints();
            node.setSecurityConstraints(cons);             
        }                
        List refs = node.getSecurityConstraints().getSecurityConstraintsRefs();        
        if (refs == null || refs.size() == 0)
        {
            return insertSecurityReference(requestContext, resultMap, node);            
        }
        boolean found = false;
        if (kind.equals("Owner"))
        {
            node.getSecurityConstraints().setOwner(name);
            found = true;
        }
        else
        {            
            for (int ix = 0; ix < refs.size(); ix++)
            {
                String ref = (String)refs.get(ix);  
                if (areFieldsSame(ref, oldName))
                {
                    refs.set(ix, name);
                    found = true;
                    break;
                }
            }
        }
        if (!found)
            return insertSecurityReference(requestContext, resultMap, node);
        return 1;
    }
    
    protected int removeSecurityReference(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        String kind = getActionParameter(requestContext, "kind");
        if (isBlank(name) || isBlank(kind))
            throw new AJAXException("Invalid Security Ref: name invalid data.");
        if (node.getSecurityConstraints() == null)
        {
            return 0;
        }
        if (kind.equals("Owner"))
        {
            node.getSecurityConstraints().setOwner(null);
        }
        else
        {
            List refs = node.getSecurityConstraints().getSecurityConstraintsRefs();
            if (!refs.contains(name))
                return 0; // nothing to do
            refs.remove(name);
        }
        return 1;
    }

    protected int removeSecurityDef(RequestContext requestContext, Map<String,Object> resultMap, Node node)
    throws AJAXException
    {
        String id = getActionParameter(requestContext, "id");
        if (isBlank(id))
            throw new AJAXException("Invalid Security Ref: id invalid data.");
        if (node.getSecurityConstraints() == null)
        {
            return 0;
        }
        List defs = node.getSecurityConstraints().getSecurityConstraints();
        if (defs == null || defs.size() == 0)
        {
            return 0;
        }
        if (id.length() == 1)
            return 0;
        id = id.substring(1);
        int index = Integer.parseInt(id) - 1;
        if (index < 0)
        {
            return 0;
        }
        defs.remove(index);
        return 1;
    }
    
    protected boolean isBlank(String field)
    {
        if (field == null || field.trim().length() == 0)
            return true;
        return false;
    }
    protected boolean isFieldModified(String paramValue, String prevValue)
    {
        if (paramValue == null)
        {
            if (prevValue == null)
                return false;
            else
                return true;
        }
        else
        {
            if (prevValue == null)
                return true;
            if (prevValue.equals(paramValue))
                return false;
            else
                return true;
        }
    }
    protected boolean areFieldsSame(String f1, String f2)
    {
        return !isFieldModified(f1, f2);
    }
    protected boolean isBooleanModified(String paramValue, boolean prevValue)
    {
        if (paramValue == null)
        {
            if (prevValue == false)
                return false;
            else
                return true;
        }
        else
        {
            if (prevValue == false)
                return true;
            else
                return false;
        }
    }        
}
