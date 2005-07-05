/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.velocity;

import org.apache.velocity.tools.view.tools.ViewTool;

/**
 * <p>
 * HtmlUtilTool
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: HtmlUtilTool.java 187354 2004-08-25 00:45:18Z jford $
 *
 */
public class HtmlUtilTool implements ViewTool
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
    
    public String getSafeElementId(Object obj)
    {
        if(obj == null)
        {
            return "null";
        }
        else
        {
            // Convert "/" to "-"
            String initValue = obj.toString();
            return initValue.replaceAll("[\\/,\\.]","-");
        }
    }

}
