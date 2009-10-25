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
package org.apache.jetspeed.om.page.psml;

import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.page.Link;

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
public class LinkImpl extends DocumentImpl implements Link 
{
    
    private String skin;

    private String target;
    
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

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#getSkin()
     */
    public String getSkin()
    {
        return skin;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Link#setSkin(java.lang.String)
     */
    public void setSkin( String skin )
    {
        this.skin = skin;
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

    /**
     * <p>
     * grantViewActionAccess
     * </p>
     *
     * @return granted access for view action
     */
    public boolean grantViewActionAccess()
    {
        // always allow links that reference absolute urls since these
        // are probably not a security related concern but rather
        // should always be viewable, (subject to folder access)
        String hrefUrl = getUrl();
        return ((hrefUrl != null) && (hrefUrl.startsWith("http://") || hrefUrl.startsWith("https://")));
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     * @param generator id generator
     * @return dirty flag
     */
    public boolean unmarshalled(IdGenerator generator)
    {
        // notify super class implementation
        boolean dirty = super.unmarshalled(generator);

        // default title of pages to name
        if (getTitle() == null)
        {
            setTitle(getTitleName());
        }
        
        return dirty;
    }
}
