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
package org.apache.jetspeed.serializer;

import java.io.PrintStream;
import java.util.Date;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.serializer.objects.JSCriterion;
import org.apache.jetspeed.serializer.objects.JSNameValuePair;
import org.apache.jetspeed.serializer.objects.JSPermission;
import org.apache.jetspeed.serializer.objects.JSProfilingRule;
import org.apache.jetspeed.serializer.objects.JSUser;

/**
 * Jetspeed Importer
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class JSImportData 
{
    private String version;
    private String name;
    private Date date;
    private String locale;
    private List roles = new ArrayList();
    private List groups = new ArrayList();
    private List permissions = new ArrayList();
    private List profilingRules = new ArrayList();
    private List users = new ArrayList();
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getVersion()
    {
        return version;
    }
    
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public void debug(PrintStream out)
    {
        out.println("name = " + getName());
        out.println("version = " + getVersion());
        out.println("date = " + getDate());
        out.println("locale = " + getLocale());
        
        Iterator it = roles.iterator();
        while (it.hasNext())
        {
            out.println("role = " + (String)it.next());
        }
        it = groups.iterator();
        while (it.hasNext())
        {
            out.println("groups = " + (String)it.next());
        }
        it = permissions.iterator();
        while (it.hasNext())
        {
            JSPermission permission = (JSPermission)it.next();
            out.println("permission = " + permission.getType() + ", " + permission.getResource());
        }            
        it = profilingRules.iterator();
        while (it.hasNext())
        {
            JSProfilingRule rule = (JSProfilingRule)it.next();
            out.println("rule = " + rule.getName() + ", " + rule.getClassName());
            Iterator criteria = rule.getCriteria().iterator();
            while (criteria.hasNext())
            {
                JSCriterion c = (JSCriterion)criteria.next();
                out.println("criterion = " + c.getName() + ", " + c.getOrder() + ", " + c.getValue());
            }
        }            

        it = users.iterator();
        while (it.hasNext())
        {
            JSUser user = (JSUser)it.next();
            out.println("user = " + user.getName() + ", " + user.getPassword());
            out.println("user roles = " + user.getRoles() + ", " + user.getGroups());
            Iterator infos = user.getUserInfo().iterator();
            while (infos.hasNext())
            {
                JSNameValuePair pair = (JSNameValuePair)infos.next();
                out.println("info = " + pair.getName() + ", " + pair.getValue());
            }
            Iterator profiles = user.getProfileRules().iterator();
            while (profiles.hasNext())
            {
                JSNameValuePair pair = (JSNameValuePair)profiles.next();
                out.println("user profile = " + pair.getName() + ", " + pair.getValue());
            }
            
            
        }            
        
    }

    
    public List getGroups()
    {
        return groups;
    }

    
    public List getRoles()
    {
        return roles;
    }

    
    public Date getDate()
    {
        return date;
    }

    
    public void setDate(Date date)
    {
        this.date = date;
    }

    
    public String getLocale()
    {
        return locale;
    }

    
    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    
    public List getUsers()
    {
        return users;
    }

    
    public void setUsers(List users)
    {
        this.users = users;
    }
}