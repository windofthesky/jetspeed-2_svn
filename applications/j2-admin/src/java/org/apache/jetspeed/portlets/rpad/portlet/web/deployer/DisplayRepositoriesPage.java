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

import java.util.List;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.portlets.rpad.RPADConstants;
import org.apache.jetspeed.portlets.rpad.RPADException;
import org.apache.jetspeed.portlets.rpad.Repository;
import org.apache.jetspeed.portlets.rpad.RepositoryManager;
import org.apache.jetspeed.portlets.rpad.portlet.util.FacesMessageUtil;

public class DisplayRepositoriesPage
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory
            .getLog(DisplayRepositoriesPage.class);

    private RepositoryManager repositoryManager = null;

    public RepositoryManager getRepositoryManager()
    {
        if (repositoryManager == null)
        {
            repositoryManager = RepositoryManager.getInstance();
        }
        return repositoryManager;
    }

    public String jumpDisplayPortlets()
    {
        return "deployer_displayPortlets";
    }

    public String doReloadRepositories()
    {
        try
        {
            getRepositoryManager().reload();
            FacesMessageUtil.addInfoMessage("Reload repositories.");
        }
        catch (Exception e)
        {
            //TODO i18n
            FacesMessageUtil.addErrorMessage("Could not reload repositories.");
            log.error("Could not reload repositories.", e);
        }
        return null;
    }

    public String doAddRepository()
    {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap()
                .remove(RPADConstants.REPOSITORY);
        return "deployer_editRepository";
    }

    public String doEditRepository()
    {
        Repository repo = (Repository) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().get("repository");
        if (repo != null)
        {
            FacesContext.getCurrentInstance().getExternalContext()
                    .getSessionMap().put(RPADConstants.REPOSITORY, repo);
            return "deployer_editRepository";
        }
        else
        {
            //TODO i18n
            FacesMessageUtil
                    .addWarnMessage("Could not find the target repository.");
        }
        return null;
    }

    public String doDeleteRepository()
    {
        Repository repo = (Repository) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().get("repository");
        if (repo != null)
        {
            try
            {
                getRepositoryManager().removeRepository(repo.getName());
                //TODO i18n
                FacesMessageUtil
                        .addInfoMessage("Removed the target repository.");
            }
            catch (RPADException e)
            {
                //TODO i18n
                FacesMessageUtil
                        .addErrorMessage("Could not remove the target repository.");
            }
        }
        else
        {
            //TODO i18n
            FacesMessageUtil
                    .addWarnMessage("Could not find the target repository.");
        }
        return null;
    }

    public List getRepositories()
    {
        return getRepositoryManager().getRepositories();
    }

    public int getPageSize()
    {
        //TODO move to portlet.xml or somewhere..
        return 10;
    }

}
