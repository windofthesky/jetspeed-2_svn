/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.appservers.security.jboss;

import org.apache.jetspeed.security.UserManager;

public interface JetspeedSecurityServiceMBean
{

    /**
     * Set the JNDI name of the <code>DataSource</code> to be used to access the database.
     * 
     * @param jndiName
     */
    void setDataSourceJndiName(String jndiName);

    /**
     * Get the JNDI name of the <code>DataSource</code> used to access the database.
     * 
     * @return jndiName
     */
    String getDataSourceJndiName();

    /**
     * Get the user manager.
     * 
     * @return user manager
     */
    UserManager getUserManager();
}
