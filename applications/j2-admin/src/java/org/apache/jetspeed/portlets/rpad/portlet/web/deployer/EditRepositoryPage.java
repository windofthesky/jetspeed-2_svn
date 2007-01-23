/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.rpad.portlet.web.deployer;

import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.jetspeed.portlets.rpad.RPADConstants;
import org.apache.jetspeed.portlets.rpad.RPADException;
import org.apache.jetspeed.portlets.rpad.Repository;
import org.apache.jetspeed.portlets.rpad.RepositoryManager;
import org.apache.jetspeed.portlets.rpad.portlet.util.FacesMessageUtil;
import org.apache.jetspeed.portlets.rpad.simple.SimpleRepository;

public class EditRepositoryPage
{

    private RepositoryManager repositoryManager = null;

    private boolean newRepository;

    private String name;

    private String path;

    public EditRepositoryPage()
    {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap();
        Repository repo = (Repository) sessionMap.get(RPADConstants.REPOSITORY);
        if (repo != null)
        {
            //TODO support repositories other than SimpleRepository
            if (repo instanceof SimpleRepository)
            {
                SimpleRepository simpleRepo = (SimpleRepository) repo;
                newRepository = false;
                setName(simpleRepo.getName());
                setPath(simpleRepo.getConfigPath());
            }
            else
            {
                newRepository = true;
                sessionMap.remove(RPADConstants.REPOSITORY);
            }
        }
        else
        {
            newRepository = true;
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

    public String jumpDisplayRepositories()
    {
        return "deployer_displayRepositories";
    }

    public String doCreateRepository()
    {
        if (getRepositoryManager().getRepository(getName()) == null)
        {
            //TODO support repositories other than SimpleRepository
            SimpleRepository repo = new SimpleRepository();
            repo.setName(getName());
            repo.setConfigPath(getPath());
            repo.init();

            try
            {
                getRepositoryManager().addRepository(getName(), repo);
                getRepositoryManager().reload();
                //  TODO i18n
                FacesMessageUtil.addInfoMessage(getName() + " exits.");
                return "deployer_displayRepositories";
            }
            catch (RPADException e)
            {
                //TODO i18n
                FacesMessageUtil.addErrorMessage("Could not add a repository: "
                        + getName());
            }
        }
        else
        {
            //TODO i18n
            FacesMessageUtil.addWarnMessage(getName() + " exits.");
        }

        return null;
    }

    public String doUpdateRepository()
    {
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap();
        Repository repo = (Repository) sessionMap.get(RPADConstants.REPOSITORY);
        if (repo != null)
        {
            Repository r = (Repository) getRepositoryManager().getRepository(
                    repo.getName());

            //TODO support repositories other than SimpleRepository
            if (r instanceof SimpleRepository)
            {
                SimpleRepository simpleRepo = (SimpleRepository) r;
                simpleRepo.setName(getName());
                simpleRepo.setConfigPath(getPath());
            }
            else
            {
                //TODO i18n
                FacesMessageUtil
                        .addWarnMessage("Could not get the proper repository.");
            }
            try
            {
                getRepositoryManager().store();
                getRepositoryManager().reload();
                //TODO i18n
                FacesMessageUtil
                        .addWarnMessage("Could not get the proper repository.");
                return null;
            }
            catch (RPADException e)
            {
                //TODO i18n
                FacesMessageUtil
                        .addErrorMessage("Could not update the target repository.");
            }

        }
        else
        {
            //TODO i18n
            FacesMessageUtil.addWarnMessage("Your session might be expired.");
        }
        return "deployer_displayRepositories";
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the newRepository
     */
    public boolean isNewRepository()
    {
        return newRepository;
    }

    /**
     * @param newRepository the newRepository to set
     */
    public void setNewRepository(boolean newRepository)
    {
        this.newRepository = newRepository;
    }

    /**
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @param path the path to set
     */
    public void setPath(String path)
    {
        this.path = path;
    }
}
