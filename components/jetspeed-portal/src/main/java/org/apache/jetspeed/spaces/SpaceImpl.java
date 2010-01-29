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

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;

/**
 * Space implementation, wrappers around a root level folder 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class SpaceImpl extends BaseSpaceImpl implements Space, Serializable
{    
    private static final long serialVersionUID = 1L;    
    
    public SpaceImpl(Folder folder)
    {
        this.backingFolder = folder;
    }   
    
	@Override
	protected String getOwnerFieldName() 
	{
		return META_SPACE_OWNER;
	}

    public void setTheme(String themeName)
    {
    	backingFolder.setDefaultDecorator(themeName, Fragment.LAYOUT);
    	backingFolder.setDefaultDecorator(themeName, Fragment.PORTLET);
    }

    public String getTheme()
    {
    	return backingFolder.getDefaultDecorator(Fragment.LAYOUT);
    }

    public String getDomainPath()
    {
        return SpacesServiceImpl.retrieveField(backingFolder, Locale.ENGLISH, META_SPACE_DOMAIN_PATH);
    }
    
    public void setDomainPath(String domainPath)
    {
        SpacesServiceImpl.updateField(backingFolder, Locale.ENGLISH, META_SPACE_DOMAIN_PATH, domainPath);    	
    }

    public String getImage()
    {
        return SpacesServiceImpl.retrieveField(backingFolder, Locale.ENGLISH, META_SPACE_IMAGE);
    }
	    
    public void setImage(String pathToImage)
    {
        SpacesServiceImpl.updateField(backingFolder, Locale.ENGLISH, META_SPACE_DOMAIN_PATH, pathToImage);    	    	
    }
    
	public String getDashboard() 
	{
		String name = backingFolder.getDefaultPage();
		if (name == null)
			name = Folder.FALLBACK_DEFAULT_PAGE;
		return name;
	}

    public void setDashboard(String dashboard)
    {
    	backingFolder.setDefaultPage(dashboard);
    }
    	
    public void addSecuredGroup(String group)
    {
    	// TODO Auto-generated method stub
    }

    public void addSecuredRole(String role)
    {
        // TODO Auto-generated method stub
        
    }

    public void addSecuredUser(String user)
    {
        // TODO Auto-generated method stub
        
    }

    public List<String> getSecuredGroup()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getSecuredRoles()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getSecuredUsers()
    {
        // TODO Auto-generated method stub
        return null;
    }


    public void removeSecuredGroup(String group)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeSecuredRole(String role)
    {
        // TODO Auto-generated method stub
        
    }

    public void removeSecuredUser(String user)
    {
        // TODO Auto-generated method stub
        
    }
    
    @SuppressWarnings("unchecked")
	public String getSecurityConstraint()
    {
    	if (backingFolder.getSecurityConstraints() == null)
    		return "";
    	List<String> refs = (List<String>)backingFolder.getSecurityConstraints().getSecurityConstraintsRefs();
    	if (refs == null || refs.isEmpty())
    		return "";
    	return refs.get(0); // TODO: support 0..n constraints
    }

    public void setSecurityConstraint(String constraint)
    {
    	if (backingFolder.getSecurityConstraints() == null)
    	{
            SecurityConstraints cons = backingFolder.newSecurityConstraints();
            backingFolder.setSecurityConstraints(cons);    		
    	}
    	List<String> refs = (List<String>)backingFolder.getSecurityConstraints().getSecurityConstraintsRefs();
    	if (refs.size() == 0)
    	{
    		refs.add(constraint); 
    	}
    	else
    	{
    		refs.set(0, constraint);
    	}
    }
    
 }
