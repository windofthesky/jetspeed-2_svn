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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jetspeed.om.folder.FolderMetaData;
import org.apache.jetspeed.page.document.AbstractNode;

/**
 * <p>
 * FolderMetaDataImpl
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public class FolderMetaDataImpl extends AbstractNode implements FolderMetaData
{
    private Vector docOrder;
    private String defaultPage;

    /**
     * menuDefinitions - menu definitions for folder
     */
    private List menuDefinitions;
    
    public FolderMetaDataImpl()
    {
        docOrder = new Vector();
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
     * getUrl
     * </p>
     *
     * @see org.apache.jetspeed.om.page.Document#getUrl()
     * @return
     */
    public String getUrl()
    {
        return getParent(false).getPath() + PATH_SEPARATOR + getType();
    }
    /**
     * <p>
     * getDocumentOrder
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderMetaData#getDocumentOrder()
     * @return
     */
    public Vector getDocumentOrder()
    {
        return docOrder;
    }
    /**
     * <p>
     * setDocumentOrder
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.FolderMetaData#setDocumentOrder(java.util.List)
     * @param docIndexes
     */
    public void setDocumentOrder( Vector docIndexes )
    {
        docOrder = docIndexes;
    }
    /**
     * @return Returns the defaultPage.
     */
    public String getDefaultPage()
    {
        return defaultPage;
    }
    /**
     * @param defaultPage The defaultPage to set.
     */
    public void setDefaultPage( String defaultPage )
    {
        this.defaultPage = defaultPage;
    }

    /**
     * getMenuDefinitions - get list of menu definitions
     *
     * @return definition list
     */
    public List getMenuDefinitions()
    {
        return menuDefinitions;
    }

    /**
     * setMenuDefinitions - set list of menu definitions
     *
     * @param definitions definition list
     */
    public void setMenuDefinitions(List definitions)
    {
        menuDefinitions = definitions;
    }

    /**
     * unmarshalled - notification that this instance has been
     *                loaded from the persistent store
     */
    public void unmarshalled()
    {
        // notify super class implementation
        super.unmarshalled();

        // propagate unmarshalled notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            Iterator menuIter = menuDefinitions.iterator();
            while (menuIter.hasNext())
            {
                ((MenuDefinitionImpl)menuIter.next()).unmarshalled();
            }
        }
    }

    /**
     * marshalling - notification that this instance is to
     *               be saved to the persistent store
     */
    public void marshalling()
    {
        // propagate marshalling notification
        // to all menu definitions
        if (menuDefinitions != null)
        {
            Iterator menuIter = menuDefinitions.iterator();
            while (menuIter.hasNext())
            {
                ((MenuDefinitionImpl)menuIter.next()).marshalling();
            }
        }

        // notify super class implementation
        super.marshalling();
    }
}
