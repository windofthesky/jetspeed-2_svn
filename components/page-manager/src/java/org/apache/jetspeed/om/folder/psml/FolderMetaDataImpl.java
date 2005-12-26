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
package org.apache.jetspeed.om.folder.psml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.jetspeed.om.page.Document;
import org.apache.jetspeed.om.page.psml.DefaultsImpl;
import org.apache.jetspeed.om.page.psml.DocumentImpl;

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
public class FolderMetaDataImpl extends DocumentImpl implements Document
{
    public static String DOCUMENT_TYPE = "folder.metadata";

    private DefaultsImpl defaults = new DefaultsImpl();
    private List docOrder;
    private String defaultPage;

    /**
     * menuDefinitions - menu definitions for folder
     */
    private List menuDefinitions;
    
    public FolderMetaDataImpl()
    {
        docOrder = new ArrayList(4);
    }
       
    /**
     * <p>
     * getType
     * </p>
     *
     * @return document type
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
     * @return url of folder
     */
    public String getUrl()
    {
        return getParent(false).getPath() + PATH_SEPARATOR + getType();
    }

    /**
     * <p>
     * getSkin
     * </p>
     *
     * @return skin for folder
     */
    public String getSkin()
    {
        // delegate to defaults implementation
        return defaults.getSkin();
    }

    /**
     * <p>
     * setSkin
     * </p>
     *
     * @param skinName skin for folder
     */
    public void setSkin( String skinName )
    {
        // delegate to defaults implementation
        defaults.setSkin(skinName);
    }

    /**
     * <p>
     * getDefaultDecorator
     * </p>
     *
     * @param fragmentType portlet or layout fragment type
     * @return decorator name
     */
    public String getDefaultDecorator( String fragmentType )
    {
        // delegate to defaults implementation
        return defaults.getDecorator(fragmentType);
    }

    /**
     * <p>
     * setDefaultDecorator
     * </p>
     *
     * @param decoratorName decorator name
     * @param fragmentType portlet or layout fragment type
     */
    public void setDefaultDecorator( String decoratorName, String fragmentType )
    {
        // delegate to defaults implementation
        defaults.setDecorator(fragmentType, decoratorName);
    }

    /**
     * <p>
     * getDocumentOrder
     * </p>
     *
     * @return document order
     */
    public List getDocumentOrder()
    {
        return docOrder;
    }

    /**
     * <p>
     * setDocumentOrder
     * </p>
     *
     * @param docIndexes
     */
    public void setDocumentOrder(List docIndexes)
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
     * getDefaults - Castor access method for Defaults.
     *
     * @return defaults instance
     */
    public DefaultsImpl getDefaults()
    {
        return this.defaults;
    }

    /**
     * setDefaults - Castor access method for Defaults.
     *
     * @param defaults defaults instance
     */
    public void setDefaults( DefaultsImpl defaults )
    {
        this.defaults = defaults;
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
