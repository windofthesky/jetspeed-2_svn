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
import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.page.document.psml.AbstractNode;


/**
 * <p>
 * Link
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: LinkImpl.java 314803 2005-10-12 06:35:19Z rwatler $
 * 
 */
public abstract class DocumentImpl extends AbstractNode implements Document 
{
    
    private String version;
    private boolean dirty=false;

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

        // default version of pages to name
        if (getVersion() == null)
        {
            setVersion(getVersion());
        }
        
        return dirty;
    }
    /**
     * @return Returns the version.
     */
    public String getVersion()
    {
        return version;
    }
    /**
     * @param version The version to set.
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
