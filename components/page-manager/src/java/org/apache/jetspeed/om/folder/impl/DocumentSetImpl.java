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
package org.apache.jetspeed.om.folder.impl;

import java.util.Vector;

import org.apache.jetspeed.om.folder.DocumentSet;
import org.apache.jetspeed.om.folder.DocumentSetPath;
import org.apache.jetspeed.page.document.AbstractNode;

/**
 * <p>
 * DocumentSetImpl
 * </p>
 * 
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 */
public class DocumentSetImpl extends AbstractNode implements DocumentSet
{
    private String profileLocatorName;
    private Vector documentPaths;
     
    public DocumentSetImpl()
    {
        documentPaths = new Vector();
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
     * <p>
     * getProfileLocatorName
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSet#getProfileLocatorName()
     * @return name
     */
    public String getProfileLocatorName()
    {
        return profileLocatorName;
    }
    /**
     * <p>
     * setProfileLocatorName
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSet#setProfileLocatorName(java.lang.String)
     * @param locatorName
     */
    public void setProfileLocatorName( String locatorName )
    {
        this.profileLocatorName = locatorName;
    }


    /**
     * <p>
     * getDocumentPaths
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSet#getDocumentPaths()
     * @return
     */
    public Vector getDocumentPaths()
    {
        return documentPaths;
    }
    /**
     * <p>
     * setDocumentPaths
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSet#setDocumentPaths(java.util.List)
     * @param paths
     */
    public void setDocumentPaths( Vector paths )
    {
        this.documentPaths = paths;
    }
    /**
     * <p>
     * getDefaultedDocumentPaths
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSet#getDefaultedDocumentPaths()
     * @return
     */
    public Vector getDefaultedDocumentPaths()
    {
        if (documentPaths.isEmpty())
        {
            // default document set paths
            DocumentSetPath defaultPath = new DocumentSetPathImpl();
            String name = getName();
            if (name.endsWith(DOCUMENT_TYPE))
            {
                name = name.substring(0, name.length() - DOCUMENT_TYPE.length());
            }
            defaultPath.setPath(PATH_SEPARATOR + name + PATH_SEPARATOR + "*.*");
            defaultPath.setRegexp(true);
            documentPaths.add(defaultPath);
        }
        return documentPaths;
    }

}
