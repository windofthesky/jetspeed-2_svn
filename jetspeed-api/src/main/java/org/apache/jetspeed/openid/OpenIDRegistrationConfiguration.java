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
package org.apache.jetspeed.openid;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * OpenID login registration configuration.
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public class OpenIDRegistrationConfiguration implements Serializable
{
    private static final long serialVersionUID = 1L;

    transient private boolean enableRegistration;
    transient private String userTemplateDirectory;
    transient private String subsiteRootFolder;
    transient private List<String> roles;
    transient private List<String> groups;
    transient private Map<String,String> profilerRules;

    /**
     * Merge in default configuration.
     * 
     * @param defaults default configuration
     */
    public void merge(OpenIDRegistrationConfiguration defaults)
    {
        if (defaults != null)
        {
            if (userTemplateDirectory == null)
            {
                userTemplateDirectory = defaults.userTemplateDirectory;
            }
            if (subsiteRootFolder == null)
            {
                subsiteRootFolder = defaults.subsiteRootFolder;
            }
            if (roles == null)
            {
                roles = defaults.roles;
            }
            if (groups == null)
            {
                groups = defaults.groups;
            }
            if (profilerRules == null)
            {
                profilerRules = defaults.profilerRules;
            }
        }
    }
    
    /**
     * @return the enableRegistration
     */
    public boolean isEnableRegistration()
    {
        return enableRegistration;
    }

    /**
     * @param enableRegistration the enableRegistration to set
     */
    public void setEnableRegistration(boolean enableRegistration)
    {
        this.enableRegistration = enableRegistration;
    }

    /**
     * @param enableRegistration the enableRegistration to set
     */
    public void setEnableRegistration(String enableRegistration)
    {
        this.enableRegistration = Boolean.parseBoolean(enableRegistration);
    }

    /**
     * @return the userTemplateDirectory
     */
    public String getUserTemplateDirectory()
    {
        return userTemplateDirectory;
    }
    
    /**
     * @param userTemplateDirectory the userTemplateDirectory to set
     */
    public void setUserTemplateDirectory(String userTemplateDirectory)
    {
        this.userTemplateDirectory = userTemplateDirectory;
    }
    
    /**
     * @return the subsiteRootFolder
     */
    public String getSubsiteRootFolder()
    {
        return subsiteRootFolder;
    }
    
    /**
     * @param subsiteRootFolder the subsiteRootFolder to set
     */
    public void setSubsiteRootFolder(String subsiteRootFolder)
    {
        this.subsiteRootFolder = subsiteRootFolder;
    }
    
    /**
     * @return the roles
     */
    public List<String> getRoles()
    {
        return roles;
    }
    
    /**
     * @param roles the roles to set
     */
    public void setRoles(List<String> roles)
    {
        this.roles = roles;
    }
    
    /**
     * @param roles the roles to set
     */
    public void setRoles(String roles)
    {
        this.roles = parseParameterList(roles);
    }
    
    /**
     * @return the groups
     */
    public List<String> getGroups()
    {
        return groups;
    }
    
    /**
     * @param groups the groups to set
     */
    public void setGroups(List<String> groups)
    {
        this.groups = groups;
    }
    
    /**
     * @param groups the groups to set
     */
    public void setGroups(String groups)
    {
        this.groups = parseParameterList(groups);
    }
    
    /**
     * @return the profilerRules
     */
    public Map<String, String> getProfilerRules()
    {
        return profilerRules;
    }
    
    /**
     * @param profilerRules the profilerRules to set
     */
    public void setProfilerRules(Map<String, String> profilerRules)
    {
        this.profilerRules = profilerRules;
    }

    /**
     * @param profilerRuleNames the profiler rule names to set
     * @param profilerRuleValues the profiler rule values to set
     */
    public void setProfilerRules(List<String> profilerRuleNames, List<String> profilerRuleValues)
    {
        if ((profilerRuleNames != null) && (profilerRuleValues != null))
        {
            profilerRules = new HashMap<String,String>();
            Iterator<String> namesIter = profilerRuleNames.iterator();
            Iterator<String> valuesIter = profilerRuleValues.iterator();
            while (namesIter.hasNext() && valuesIter.hasNext())
            {
                profilerRules.put(namesIter.next(), valuesIter.next());
            }
        }
        else
        {
            profilerRules = null;
        }
    }
    
    /**
     * @param profilerRuleNames the profiler rule names to set
     * @param profilerRuleValues the profiler rule values to set
     */
    public void setProfilerRules(String profilerRuleNames, String profilerRuleValues)
    {
        setProfilerRules(parseParameterList(profilerRuleNames), parseParameterList(profilerRuleValues));
    }
    
    /**
     * Parse parameter list.
     * 
     * @param parameterList parameter name
     * @return parameter list
     */
    public static List<String> parseParameterList(String parameterList)
    {
        if (parameterList != null)
        {
            String [] params = parameterList.split("[,]");
            for (int i = 0; (i < params.length); i++)
            {
                params[i] = params[i].trim();
            }
            return Arrays.asList(params);            
        }
        return null;
    }
}
