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
package org.apache.jetspeed.page.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.page.psml.AbstractBaseElement;
import org.apache.jetspeed.om.page.psml.PageMetadataImpl;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.util.ArgUtil;

/**
 * <p>
 * AbstractBaseElementWithMetaData
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver </a>
 * @version $Id$
 *  
 */
public abstract class AbstractNode extends AbstractBaseElement implements Node
{

    private Collection metadataFields = null;
    private Map localizedTitles;
    private Node parent;
    private String path;
    private String url;
    private boolean hidden=false;
    private String profiledPath;

    public AbstractNode()
    {
    }

    public GenericMetadata getMetadata()
    {
        if (metadataFields == null)
        {
            metadataFields = new ArrayList();
        }

        GenericMetadata metadata = new PageMetadataImpl();
        metadata.setFields(metadataFields);
        return metadata;
    }

    public void setMetadata( GenericMetadata metadata )
    {
        this.metadataFields = metadata.getFields();
    }

    /**
     * This should only be used during castor marshalling
     * 
     * @see org.apache.jetspeed.om.page.Page#getMetadataFields()
     */
    public Collection getMetadataFields()
    {
        return metadataFields;
    }

    /**
     * This should only be used during castor unmarshalling
     * 
     * @see org.apache.jetspeed.om.page.Page#setMetadataFields(java.util.Collection)
     */
    public void setMetadataFields( Collection metadataFields )
    {
        this.metadataFields = metadataFields;
    }

    /**
     * <p>
     * getTitle
     * </p>
     * 
     * @see org.apache.jetspeed.om.page.Page#getTitle(java.util.Locale)
     * @param locale
     * @return
     */
    public String getTitle( Locale locale )
    {
        ArgUtil.assertNotNull(Locale.class, locale, this, "getTile(Locale)");
        if (localizedTitles == null && metadataFields != null)
        {
            this.localizedTitles = new HashMap(metadataFields.size());
            Iterator fieldsItr = metadataFields.iterator();
            while (fieldsItr.hasNext())
            {
                LocalizedField field = (LocalizedField) fieldsItr.next();
                if (field.getName().equals("title"))
                {
                    localizedTitles.put(field.getLocale(), field);
                }
            }
        }

        Locale languageOnly = new Locale(locale.getLanguage());
        if (localizedTitles != null
                && (localizedTitles.containsKey(locale) 
                    || localizedTitles.containsKey(languageOnly)))
        {
            if(localizedTitles.containsKey(locale) )
            {
                return ((LocalizedField) localizedTitles.get(locale)).getValue().trim();
            }
            else if(localizedTitles.containsKey(languageOnly))
            {
                return ((LocalizedField) localizedTitles.get(languageOnly)).getValue().trim();
            }
            else
            {
                return getTitle();
            }
        }
        else
        {
            return getTitle();
        }
    }

    /**
     * <p>
     * getParent
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.ChildNode#getParent()
     * @return
     */
    public Node getParent()
    {
        return parent;
    }

    /**
     * <p>
     * setParent
     * </p>
     * 
     * @see org.apache.jetspeed.om.folder.ChildNode#setParent(org.apache.jetspeed.om.folder.Folder)
     * @param parent
     */
    public void setParent( Node parent )
    {
        this.parent = parent;
    }

    /**
     * <p>
     * getRelativeName
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.ChildNode#getRelativeName()
     * @return
     */
    public String getName()
    {
        String path = getPath();
        String parentName = "";
        if(getParent() != null)
        {
            parentName = getParent().getPath();
            if (! parentName.endsWith(PATH_SEPARATOR))
            {
                parentName += PATH_SEPARATOR;
            }
        }
        
        if(path.indexOf(parentName) > -1)
        {
            return path.substring(parentName.length());
        }
        else
        {
            return path;
        }
    }

    /**
     * @return Returns the path.
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * @param path The path to set.
     */
    public void setPath( String path )
    {
        this.path = path;
    }
    /**
     * <p>
     * getUrl
     * </p>
     * Same as invoking <code>Node.getPath()</code> unless url explicitly set.
     *
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     * @return
     */
    public String getUrl()
    {
        if (isUrlSet())
            return url;
        return getPath();
    }
    /**
     * @param url The url to set.
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    /**
     * @return Flag indicating whether url is set.
     */
    public boolean isUrlSet()
    {
        return (url != null);
    }
    /**
     * <p>
     * isHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     * @return
     */
    public boolean isHidden()
    {
        return hidden;
    }
    /**
     * @param hidden The hidden to set.
     */
    public void setHidden( boolean hidden )
    {
        this.hidden = hidden;
    }

    /**
     * @return Returns the profiled path.
     */
    public String getProfiledPath()
    {
        return profiledPath;
    }
    /**
     * @param path The profiled path to set.
     */
    public void setProfiledPath( String profiledPath )
    {
        this.profiledPath = profiledPath;
    }
}
