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
package org.apache.jetspeed.portlets.rpad.portlet.web.deployer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.jetspeed.portlets.rpad.PortletApplication;
import org.apache.jetspeed.portlets.rpad.Repository;
import org.apache.jetspeed.portlets.rpad.RepositoryManager;
import org.apache.jetspeed.portlets.rpad.portlet.deployer.PortletDeployer;
import org.apache.jetspeed.portlets.rpad.portlet.util.FacesMessageUtil;

public class DisplayPortletsPage
{

    protected static final String SEARCH = "org.apache.jetspeed.portlets.rpad.portlet.web.deployer.Search";

    protected static final String REPOSITORY_NAME = "org.apache.jetspeed.portlets.rpad.portlet.web.deployer.RepositoryNamee";

    private String repositoryName = null;

    private String search = null;

    private RepositoryManager repositoryManager = null;

    private PortletDeployer portletDeployer;

    public DisplayPortletsPage()
    {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap();

        String repositoryName = (String) sessionMap.get(REPOSITORY_NAME);
        if (repositoryName != null)
        {
            this.repositoryName = repositoryName;
        }

        String search = (String) sessionMap.get(SEARCH);
        if (search != null)
        {
            this.search = search;
        }
    }

    public RepositoryManager getRepositoryManager()
    {
        if (repositoryManager == null)
        {
            repositoryManager = RepositoryManager.getInstance();
        }
        return repositoryManager;
    }

    public String doSearch()
    {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap();

        if (getRepositoryName() != null && !getRepositoryName().equals(""))
        {
            sessionMap.put(REPOSITORY_NAME, getRepositoryName());
        }
        else
        {
            sessionMap.remove(REPOSITORY_NAME);
        }

        if (getSearch() != null && !getSearch().equals(""))
        {
            sessionMap.put(SEARCH, getSearch());
        }
        else
        {
            sessionMap.remove(SEARCH);
        }

        return null;
    }

    public String jumpUpdateRepository()
    {
        return "deployer_displayRepositories";
    }

    public String doRefresh()
    {
        return null;
    }

    public String doDeploy()
    {
        PortletApplication portlet = (PortletApplication) FacesContext
                .getCurrentInstance().getExternalContext().getRequestMap().get(
                        "portlet");
        if (portlet != null)
        {
            if (getPortletDeployer() != null)
            {
                getPortletDeployer().deploy(portlet);
            }
            else
            {
                //TODO i18n
                FacesMessageUtil.addMessage(FacesMessage.SEVERITY_ERROR,
                        "Could not find the portlet deployer.", null);
            }
        }
        else
        {
            //TODO i18n
            FacesMessageUtil.addMessage(FacesMessage.SEVERITY_ERROR,
                    "Could not find the target portlet.", null);
        }

        return null;
    }

    public SelectItem[] getRepositoryNames()
    {
        List repos = getRepositoryManager().getRepositories();
        SelectItem[] items = new SelectItem[repos.size() + 1];
        items[0] = new SelectItem("");
        int c = 1;
        for (Iterator i = repos.iterator(); i.hasNext(); c++)
        {
            Repository repo = (Repository) i.next();
            items[c] = new SelectItem(repo.getName());
        }
        return items;
    }

    public List getPortlets()
    {
        //TODO search
        if (repositoryName != null)
        {
            return getRepositoryManager()
                    .getPortletApplications(repositoryName);
        }
        else
        {
            return getRepositoryManager().getPortletApplications();
        }
    }

    public int getPageSize()
    {
        //TODO move to portlet.xml or somewhere..
        return 10;
    }

    /**
     * @return the repositoryName
     */
    public String getRepositoryName()
    {
        return repositoryName;
    }

    /**
     * @param repositoryName the repositoryName to set
     */
    public void setRepositoryName(String repositoryName)
    {
        this.repositoryName = repositoryName;
    }

    /**
     * @return the search
     */
    public String getSearch()
    {
        return search;
    }

    /**
     * @param search the search to set
     */
    public void setSearch(String search)
    {
        this.search = search;
    }

    /**
     * @return the portletDeployer
     */
    public PortletDeployer getPortletDeployer()
    {
        return portletDeployer;
    }

    /**
     * @param portletDeployer the portletDeployer to set
     */
    public void setPortletDeployer(PortletDeployer portletDeployer)
    {
        this.portletDeployer = portletDeployer;
    }

    public boolean isDeployable()
    {
        return portletDeployer.getStatus() == PortletDeployer.READY;
    }

}
