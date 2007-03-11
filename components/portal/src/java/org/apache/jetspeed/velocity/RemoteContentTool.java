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
/*
 * Created on Jul 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.velocity;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * <p>
 * ContentTool
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class RemoteContentTool implements ViewTool
{

    /**
     * <p>
     * init
     * </p>
     *
     * @see org.apache.velocity.tools.view.tools.ViewTool#init(java.lang.Object)
     * @param arg0
     */
    public void init( Object arg0 )
    {
        
        
    }
    
    public String include(String remoteContentUrl)
    {
        GetMethod remoteContentGet = null;
        
        try
        {
            HttpClient client = new HttpClient();
            remoteContentGet = new GetMethod(remoteContentUrl);
            client.executeMethod(remoteContentGet);
            return remoteContentGet.getResponseBodyAsString();
        }
        catch (Exception e)
        {
            return e.toString()+" message:"+ e.getMessage();
        }
        finally
        {
            if(remoteContentGet != null)
            {
                remoteContentGet.releaseConnection();
            }
        }
    }

 

}
