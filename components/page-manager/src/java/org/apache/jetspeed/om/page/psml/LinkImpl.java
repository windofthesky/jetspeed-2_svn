/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.page.document.AbstractNode;


/**
 * <p>
 * Link
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class LinkImpl extends AbstractNode implements Link 
{
    
    private String url;
    
    private String target;
    
    /**
     * <p>
     * getUrl
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Document#getUrl()
     * @return
     */
    public String getUrl()
    {       
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * <p>
     * getType
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Document#getType()
     * @return
     */
    public String getType()
    {       
        return DOCUMENT_TYPE;
    }

    /**
     * @return Returns the target.
     */
    public String getTarget()
    {
        return target;
    }
    
    /**
     * @param target The target to set.
     */
    public void setTarget( String target )
    {
        this.target = target;
    }
}
