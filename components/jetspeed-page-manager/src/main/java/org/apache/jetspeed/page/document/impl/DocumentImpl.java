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
package org.apache.jetspeed.page.document.impl;

import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.impl.SecurityConstraintsImpl;

/**
 * DocumentImpl
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id$
 */
public abstract class DocumentImpl extends NodeImpl implements Document
{
    private String version;
    
    private boolean dirty = false;
    
    public DocumentImpl(SecurityConstraintsImpl constraints)
    {
        super(constraints);
    }
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.BaseElement#getTitle()
     */
    public String getTitle()
    {
        // default title to document name
        String title = super.getTitle();
        if (title == null)
        {
            title = defaultTitleFromName();
            setTitle(title);
        }
        return title;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#setVersion()
     */
    public String getVersion()
    {
        return version;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.page.Document#setVersion(java.lang.String)
     */
    public void setVersion(String version)
    {
        this.version = version;
    }
    
    public boolean isDirty() {
		return dirty;
	}
    
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

}
