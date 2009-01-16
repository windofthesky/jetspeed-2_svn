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
package org.apache.jetspeed.sso.spi;

import java.util.Collection;

import org.apache.jetspeed.sso.SSOException;
import org.apache.jetspeed.sso.SSOSite;
import org.apache.jetspeed.sso.SSOUser;


/**
 * @author <a href="mailto:ddam@apache.org">Dennis Dam</a>
 * @version $Id$
 */
public interface SSOSiteManagerSPI
{
    
    SSOSite getByName(String siteName);

	SSOSite getByUrl(String siteUrl);

	SSOSite getById(int id);

    void update(SSOSite site) throws SSOException;
    
    SSOSite add(SSOSite site) throws SSOException; 
    
    void remove(SSOSite site) throws SSOException;

	Collection<SSOSite> getSites(String filter);

	SSOSite getSite(SSOUser ssoUser);
	
	Collection<SSOSite> getSites(Collection<SSOUser> users);

}
