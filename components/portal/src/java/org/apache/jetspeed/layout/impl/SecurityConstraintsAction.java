/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.layout.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.ajax.AJAXException;
import org.apache.jetspeed.ajax.AjaxAction;
import org.apache.jetspeed.ajax.AjaxBuilder;
import org.apache.jetspeed.om.common.SecurityConstraint;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.SecurityConstraintsDef;
import org.apache.jetspeed.page.PageManager;
import org.apache.jetspeed.request.RequestContext;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Security Permission action
 * 
 * AJAX Parameters: 
 *    action = constraints
 *    method = add-def | update-def | remove-def | add-global | remove-global   
 *    name = name of constraint definition or global definition
 *    xml = the constraints payload, same format as PSML constraint defs
 *    
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor </a>
 * @version $Id: $
 */
public class SecurityConstraintsAction 
    extends BasePortletAction 
    implements AjaxAction, AjaxBuilder, Constants
{
    protected Log log = LogFactory.getLog(SecurityConstraintsAction.class);
    protected PageManager pm = null;

    public SecurityConstraintsAction(String template, 
                                     String errorTemplate, 
                                     PageManager pm)
    {
        super(template, errorTemplate, null); 
        this.pm = pm;
    }
    
    public boolean run(RequestContext requestContext, Map resultMap)
            throws AJAXException
    {
        boolean success = true;
        String status = "success";
        try
        {
            resultMap.put(ACTION, "constraints");
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
            if (method.equals("add-def") || method.equals("update-def"))
            {
                count = updateConstraintDefinition(requestContext, resultMap);
            }
            else if (method.equals("remove-def"))
            {
                count = removeConstraintDefinition(requestContext, resultMap);
            }
            else if (method.equals("add-global"))
            {
                count = addGlobal(requestContext, resultMap);
            }
            else if (method.equals("remove-global"))
            {
                count = removeGlobal(requestContext, resultMap);
            }
            else
            {
                success = false;
                resultMap.put(REASON, "Unsupported portal constraints method: " + method);                
                return success;                
            }
            resultMap.put("count", Integer.toString(count));
            resultMap.put(STATUS, status);
        } 
        catch (Exception e)
        {
            log.error("exception administering portal permissions", e);
            resultMap.put(REASON, e.toString());
            success = false;
        }
        return success;
    }
    
    protected int removeConstraintDefinition(RequestContext requestContext, Map resultMap)
    throws AJAXException
    {
        String name = getActionParameter(requestContext, "name");
        if (name == null)
            throw new AJAXException("Missing 'name' parameter");
        
        try
        {
            PageSecurity pageSecurity = pm.getPageSecurity();        
            SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(name);
            if (def == null)
            {
                return 0;
            }
            List defs = pageSecurity.getSecurityConstraintsDefs();
            defs.remove(def);
            pageSecurity.setSecurityConstraintsDefs(defs);
            pm.updatePageSecurity(pageSecurity);
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return 1;
    }
    
    protected int updateConstraintDefinition(RequestContext requestContext, Map resultMap)
    throws AJAXException
    {
        int count = 0;
        boolean added = false;
        String xml = getActionParameter(requestContext, "xml");
        if (xml == null)
            throw new AJAXException("Missing 'xml' parameter");
        try
        {
            SAXBuilder saxBuilder = new SAXBuilder();
            StringReader reader = new StringReader(xml);
            Document document = saxBuilder.build(reader);
            Element root = document.getRootElement();
            String name = root.getAttribute("name").getValue();
            System.out.println("name = " + name);
            PageSecurity pageSecurity = pm.getPageSecurity();
            SecurityConstraintsDef def = pageSecurity.getSecurityConstraintsDef(name);
            int defsSize = 0;
            if (def == null)
            {
                def = pm.newSecurityConstraintsDef();
                def.setName(name);
                added = true;
            }
            int xmlSize = root.getChildren("security-constraint").size();
            if (added == false)
            {
                defsSize = def.getSecurityConstraints().size();
            }
            int min = (xmlSize < defsSize) ? xmlSize : defsSize;
            List xmlConstraints = root.getChildren("security-constraint");
            List constraints = def.getSecurityConstraints();
            Element owner = root.getChild("owner");
            if (owner != null)
            {
            }
            for (int ix = 0; ix < min; ix++)
            {
                Element xmlConstraint = (Element)xmlConstraints.get(ix);
                SecurityConstraint constraint =  (SecurityConstraint)constraints.get(ix);                
                updateConstraintValues(xmlConstraint, constraint);
                count++;                
            }
            if (xmlSize < defsSize)
            {
                // remove constraints
                List deletes = new ArrayList(defsSize - xmlSize);
                for (int ix = min; ix < defsSize; ix++)
                {
                    deletes.add(constraints.get(ix));
                }
                for (int ix = 0; ix < deletes.size(); ix++)
                {
                    constraints.remove(deletes.get(ix));
                    count++;                    
                }                
            }
            else if (xmlSize > defsSize)
            {
                // add new constraints
                for (int ix = min; ix < xmlSize; ix++)
                {
                    Element xmlConstraint = (Element)xmlConstraints.get(ix);
                    SecurityConstraint constraint =  pm.newPageSecuritySecurityConstraint();                    
                    updateConstraintValues(xmlConstraint, constraint);
                    constraints.add(constraint);                    
                    count++;
                }                
            }
            if (added)
            {                
                pageSecurity.getSecurityConstraintsDefs().add(def);
                pageSecurity.setSecurityConstraintsDefs(pageSecurity.getSecurityConstraintsDefs());
            }
            pm.updatePageSecurity(pageSecurity);
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }
        return count;
    }
    
    protected void updateConstraintValues(Element xmlConstraint, SecurityConstraint constraint)
    {
        constraint.setRoles(parseCSVList(xmlConstraint.getChildText("roles")));
        constraint.setGroups(parseCSVList(xmlConstraint.getChildText("groups")));
        constraint.setPermissions(parseCSVList(xmlConstraint.getChildText("permissions")));
        constraint.setUsers(parseCSVList(xmlConstraint.getChildText("users")));        
    }
    
    protected List parseCSVList(String csv)
    {
        if (csv != null)
        {
            List csvList = new ArrayList(4);
            if (csv.indexOf(',') != -1)
            {
                StringTokenizer csvTokens = new StringTokenizer(csv, ",");
                while (csvTokens.hasMoreTokens())
                {
                    csvList.add(csvTokens.nextToken().trim());
                }
            }
            else
            {
                csvList.add(csv);
            }
            return csvList;
        }
        return null;
    }
    
    protected int removeGlobal(RequestContext requestContext, Map resultMap)
    throws AJAXException
    {
        int count = 0;
        String name = getActionParameter(requestContext, "name");
        if (name == null)
            throw new AJAXException("Missing 'name' parameter");
        
        try
        {
            PageSecurity pageSecurity = pm.getPageSecurity();        
            List globals = pageSecurity.getGlobalSecurityConstraintsRefs();
            if (!globals.contains(name))
            {
                return 0;
            }
            globals.remove(name);
            pageSecurity.setGlobalSecurityConstraintsRefs(globals);
            pm.updatePageSecurity(pageSecurity);
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
       
    protected int addGlobal(RequestContext requestContext, Map resultMap)
    throws AJAXException
    {
        int count = 0;
        boolean found = false;
        String name = getActionParameter(requestContext, "name");
        if (name == null)
            throw new AJAXException("Missing 'name' parameter");
        
        try
        {
            PageSecurity pageSecurity = pm.getPageSecurity();        
            List globals = pageSecurity.getGlobalSecurityConstraintsRefs();
            if (pageSecurity.getSecurityConstraintsDef(name) == null)
            {
                throw new AJAXException("global name doesnt exist in definitions");
            }
            if (globals.contains(name))
            {
                // already exist;
                return count;
            }
            globals.add(name);
            pageSecurity.setGlobalSecurityConstraintsRefs(globals);
            pm.updatePageSecurity(pageSecurity);
            count++;
        }
        catch (Exception e)
        {
            throw new AJAXException(e);
        }        
        return count;
    }
    
}
