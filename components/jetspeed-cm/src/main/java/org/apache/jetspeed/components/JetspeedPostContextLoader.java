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
package org.apache.jetspeed.components;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.security.JetspeedBeanInitializer;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.spi.JetspeedSecuritySynchronizer;

/**
 * @author <a href="mailto:vkumar@apache.org">Vivek Kumar</a>
 * @version $Id:
 */
public class JetspeedPostContextLoader implements JetspeedBeanInitializer
{
    private static final Log log = LogFactory.getLog(JetspeedPostContextLoader.class);
    private UserManager userManager;
    private JetspeedSecuritySynchronizer synchronizer;
    private boolean synchronizeAllUser;
    private String synchronizeEntityType;

    /**
     * @param synchronizer
     * @param userManager
     */
    public JetspeedPostContextLoader(JetspeedSecuritySynchronizer synchronizer, UserManager userManager, boolean synchronizeAllUser,
                                     String synchronizeEntityType)
    {
        this.synchronizer = synchronizer;
        this.userManager = userManager;
        this.synchronizeAllUser = synchronizeAllUser;
        this.synchronizeEntityType = synchronizeEntityType;
    }
    
    public void initialize()
    {
        if (synchronizer != null)
        {
            try
            {
                if (userManager.getUser(userManager.getAnonymousUser()) == null)
                {
                    synchronizer.synchronizeUserPrincipal(userManager.getAnonymousUser());
                }
                
                if (synchronizeAllUser)
                {
                    synchronizer.synchronizeAll();
                }
                else
                {
                    if (StringUtils.isNotEmpty(synchronizeEntityType))
                    {
                        synchronizer.synchronizePrincipalsByType(synchronizeEntityType);
                    }
                }
            }
            catch (SecurityException secExp)
            {
                if (log.isErrorEnabled())
                {
                    log.error("Error occured while executing JetspeedPostContextLoader", secExp);
                }
            }
        }
    }
}
