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
package org.apache.jetspeed.portlets.rpad.portlet.deployer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.deployment.DeploymentException;
import org.apache.jetspeed.deployment.DeploymentManager;
import org.apache.jetspeed.deployment.DeploymentStatus;
import org.apache.jetspeed.portlets.rpad.PortletApplication;
import org.apache.jetspeed.portlets.rpad.portlet.deployer.PortletDeployer;
import org.apache.jetspeed.portlets.rpad.portlet.util.FacesMessageUtil;

public class JetspeedPortletDeployer implements PortletDeployer
{
    /**
     * Logger for this class
     */
    private static final Log log = LogFactory
            .getLog(JetspeedPortletDeployer.class);

    private int status;

    private long startTime = 0;

    public JetspeedPortletDeployer()
    {
        status = READY;
    }

    public int getStatus()
    {
        return status;
    }

    synchronized public void deploy(PortletApplication portlet)
    {
        if (status != READY)
        {
            //TODO check timeout

            //TODO i18n
            FacesMessageUtil
                    .addWarnMessage("Other deployment process is running.");
            return;
        }
        DeployerThread deployer = new DeployerThread();
        deployer.setDeploymentManager((DeploymentManager) FacesContext
                .getCurrentInstance().getExternalContext().getApplicationMap()
                .get(CommonPortletServices.CPS_DEPLOYMENT_MANAGER_COMPONENT));
        deployer.setPortletApplication(portlet);
        try
        {
            deployer.start();
            //TODO i18n
            FacesMessageUtil.addInfoMessage("Started a deployment process.");
        }
        catch (Exception e)
        {
            //TODO i18n
            FacesMessageUtil
                    .addErrorMessage("Could not start deployment process.");
            log.error("Could not start deployment process.", e);
        }
    }

    public class DeployerThread extends Thread
    {
        private DeploymentManager deploymentManager;

        private PortletApplication portletApplication;

        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run()
        {
            status = DEPLOYING;
            try
            {
                startTime = Calendar.getInstance().getTimeInMillis();
                if (getDeploymentManager() != null)
                {
                    String binaryUrl = portletApplication.getBinaryUrl();
                    if (binaryUrl != null && !binaryUrl.equals(""))
                    {
                        File targetFile = null;
                        try
                        {
                            File tempFile = File.createTempFile("rpad_", "."
                                    + portletApplication.getPackaging());
                            FileOutputStream out = new FileOutputStream(
                                    tempFile);
                            drain(getInputStream(portletApplication
                                    .getBinaryUrl()), out);
                            try
                            {
                                targetFile = new File(tempFile.getParentFile(),
                                        portletApplication.getArtifactId()
                                                + "."
                                                + portletApplication
                                                        .getPackaging());
                                tempFile.renameTo(targetFile);
                            }
                            catch (Exception e)
                            {
                                targetFile = tempFile;
                            }
                            if (getDeploymentManager().deploy(targetFile)
                                    .getStatus() == DeploymentStatus.STATUS_OKAY)
                            {
                                log.info(portletApplication.getName()
                                        + " was deployed.");
                            }
                            else
                            {
                                log.error("Could not deploy "
                                        + portletApplication.getName());
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            log.error(e);
                        }
                        catch (IOException e)
                        {
                            log.error(e);
                        }
                        catch (DeploymentException e)
                        {
                            log.error(e);
                        }
                        if (targetFile != null && targetFile.exists())
                        {
                            targetFile.delete();
                        }
                    }
                    else
                    {
                        log.error("The target url is invalid. The path is "
                                + binaryUrl);
                    }
                }
                else
                {
                    log.error("Could not find the deployment manager.");
                }
            }
            catch (Exception e)
            {
                log.error("Unexpected exception.", e);
            }
            finally
            {
                status = READY;
            }
        }

        /**
         * @return the portletApplication
         */
        public PortletApplication getPortletApplication()
        {
            return portletApplication;
        }

        /**
         * @param portletApplication the portletApplication to set
         */
        public void setPortletApplication(PortletApplication portletApplication)
        {
            this.portletApplication = portletApplication;
        }

        /**
         * @return the startTime
         */
        public long getStartTime()
        {
            return startTime;
        }

        /**
         * @return the deploymentManager
         */
        public DeploymentManager getDeploymentManager()
        {
            return deploymentManager;
        }

        /**
         * @param deploymentManager the deploymentManager to set
         */
        public void setDeploymentManager(DeploymentManager deploymentManager)
        {
            this.deploymentManager = deploymentManager;
        }

    }

    protected void drain(InputStream in, OutputStream out) throws IOException
    {
        try
        {
            byte[] buf = new byte[8192];
            int len = in.read(buf);

            while (len != -1)
            {
                out.write(buf, 0, len);
                len = in.read(buf);
            }
            out.flush();
        }
        catch (IOException e)
        {
            throw e;
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
            }
            try
            {
                in.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    protected InputStream getInputStream(String path)
    {
        if (path.startsWith("http:") || path.startsWith("https:"))
        {
            try
            {
                URL url = new URL(path);
                return url.openStream();
            }
            catch (MalformedURLException e)
            {
                log.error("Wrong url: " + path, e);
            }
            catch (IOException e)
            {
                log.error("Could not load " + path, e);
            }
        }
        else if (path.startsWith("file:"))
        {
            try
            {
                return new FileInputStream(new File(path.substring(5)));
            }
            catch (FileNotFoundException e)
            {
                log.error("Could not load " + path, e);
            }
        }
        return null;
    }

}
