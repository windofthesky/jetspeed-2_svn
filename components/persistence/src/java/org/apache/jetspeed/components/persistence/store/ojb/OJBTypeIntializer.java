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
package org.apache.jetspeed.components.persistence.store.ojb;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.components.util.system.SystemResourceUtil;
import org.picocontainer.Startable;

/**
 * <p>
 * OJBTypeIntializer
 * </p>
 * 
 *  @
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $ $
 *  
 */
public class OJBTypeIntializer implements Startable
{
    private static final Log log = LogFactory.getLog(OJBTypeIntializer.class);
    private SystemResourceUtil resourceUtil;
    private String pathToOJBProps;
    private String ojbPropsFileName;
    private ClassLoader cl;
    public OJBTypeIntializer(SystemResourceUtil resourceUtil, String pathToOJBProps, String ojbPropsFileName,
            ClassLoader cl)
    {
        this.resourceUtil = resourceUtil;
        this.pathToOJBProps = pathToOJBProps;
        this.ojbPropsFileName = ojbPropsFileName;
        if (cl != null)
        {
            this.cl = cl;
        }
        else
        {
            this.cl = Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * <p>
     * init
     * </p>
     * 
     * @see org.apache.jetspeed.components.persistence.store.PersistenceStoreTypeInitializer#init(org.apache.commons.configuration.Configuration,
     *      java.lang.Class)
     * @param conf
     * @param type
     */
    public void start()
    {
        try
        {
            URL ojbPropsUrl = resourceUtil.getURL(pathToOJBProps + "/" + ojbPropsFileName);
            File ojbPropsFile = new File(ojbPropsUrl.getFile());
            if (ojbPropsFile.exists())
            {
                String ojbDir = ojbPropsFile.getParentFile().getCanonicalPath();
                log.info("URL to OJB resources: " + resourceUtil.getURL(pathToOJBProps));
                URLClassLoader urlClassLoader = 
                URLClassLoader.newInstance(new URL[]{resourceUtil.getURL(pathToOJBProps)}, cl);
                
                Thread.currentThread().setContextClassLoader(urlClassLoader);
            }
            else
            {
                throw new IllegalStateException(
                "Could not locate the OJB load directory. " + ojbPropsFile.getParent());
            }
        }
        catch (Exception e1)
        {
            log.error("Could not locate the OJB load directory.  Bad URL.  " + e1.toString(), e1);
            throw new IllegalStateException("Could not locate the OJB load directory.  Bad URL. " + e1.toString());
        }
    }

    /**
     * @see org.picocontainer.Startable#stop()
     */
    public void stop()
    {
    }
}
