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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.SecuredResource;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.psml.AbstractBaseElement;
import org.apache.jetspeed.om.page.psml.PageMetadataImpl;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsImpl;
import org.apache.jetspeed.page.document.Node;
import org.apache.jetspeed.util.ArgUtil;


/**
 * <p>
 * AbstractNode
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
    private Map localizedShortTitles;
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
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
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
        }

        return getTitle();
    }

    /**
     * <p>
     * getShortTitle
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     * @param locale
     * @return
     */
    public String getShortTitle( Locale locale )
    {
        ArgUtil.assertNotNull(Locale.class, locale, this, "getShortTitle(Locale)");
        if (localizedShortTitles == null && metadataFields != null)
        {
            this.localizedShortTitles = new HashMap(metadataFields.size());
            Iterator fieldsItr = metadataFields.iterator();
            while (fieldsItr.hasNext())
            {
                LocalizedField field = (LocalizedField) fieldsItr.next();
                if (field.getName().equals("short-title"))
                {
                    localizedShortTitles.put(field.getLocale(), field);
                }
            }
        }

        Locale languageOnly = new Locale(locale.getLanguage());
        if (localizedShortTitles != null
            && (localizedShortTitles.containsKey(locale) 
                || localizedShortTitles.containsKey(languageOnly)))
        {
            if(localizedShortTitles.containsKey(locale) )
            {
                return ((LocalizedField) localizedShortTitles.get(locale)).getValue().trim();
            }
            else if(localizedShortTitles.containsKey(languageOnly))
            {
                return ((LocalizedField) localizedShortTitles.get(languageOnly)).getValue().trim();
            }
        }

        // default to localized title, default short title, or default
        // title if not specified
        String title = getTitle(locale);
        if (title == getTitle())
        {
            title = getShortTitle();
        }
        return title;
    }

    /**
     * <p>
     * getParent
     * </p>
     * 
     * @param checkAccess flag
     * @return parent node
     */
    public Node getParent(boolean checkAccess)
    {
        AbstractNode parent = (AbstractNode) this.parent;

        // check access
        if ((parent != null) && checkAccess)
        {
            parent.checkAccess(SecuredResource.VIEW_ACTION);
        }
        return parent;
    }

    /**
     * <p>
     * getParent
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#getParent()
     * @return
     */
    public Node getParent()
    {
        // by default disable access checks to facilitate navigation
        return getParent(false);
    }

    /**
     * <p>
     * setParent
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#setParent(org.apache.jetspeed.om.folder.Folder)
     * @param parent
     */
    public void setParent( Node parent )
    {
        this.parent = parent;
    }

    /**
     * <p>
     * getName
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#getName()
     * @return
     */
    public String getName()
    {
        String path = getPath();
        String parentName = "";
        if(getParent(false) != null)
        {
            parentName = getParent(false).getPath();
            if (! parentName.endsWith(PATH_SEPARATOR))
            {
                parentName += PATH_SEPARATOR;
            }
        }
        
        if (path.startsWith(parentName))
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

    /**
     * <p>
     * checkConstraints
     * </p>
     *
     * @param actions
     * @param userPrincipals
     * @param rolePrincipals
     * @param groupPrincipals
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkConstraints(List actions, List userPrincipals, List rolePrincipals, List groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
String debug=""; Iterator i = actions.iterator(); while(i.hasNext())debug+=(String)i.next()+" ";
        // check constraints in node hierarchy
        if (checkNodeOnly)
        {
            // check node constraints if available; otherwise,
            // recursively check parent constraints until
            // default constraints for node are checked
            SecurityConstraints constraints = getSecurityConstraints();
            if (constraints != null)
            {
                ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getHandlerFactory());
            }
            else if (parent != null)
            {
                ((AbstractNode)parent).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, checkNodeOnly, false);
            }
        }
        else
        {
            // check node constraints if available and not
            // to be skipped due to explicity granted access
            if (!checkParentsOnly)
            {
                SecurityConstraints constraints = getSecurityConstraints();
                if (constraints != null)
                {
                    ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getHandlerFactory());
                }
            }

            // recursively check all parent constraints in hierarchy
            if (parent != null)
            {
                ((AbstractNode)parent).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, false, false);
            }
        }
    }

    /**
     * <p>
     * checkPermissions
     * </p>
     *
     * @param path
     * @param actions
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkPermissions(String path, String actions, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check granted node permissions unless the check is
        // to be skipped due to explicity granted access
        if (!checkParentsOnly)
        {
            super.checkPermissions(path, actions, true, false);
        }
        
        // if not checking node only, recursively check
        // all parent permissions in hierarchy
        if (!checkNodeOnly && (parent != null))
        {
            ((AbstractNode)parent).checkPermissions(actions, false, false);
        }
    }

    /**
     * <p>
     * getLogicalPermissionPath
     * </p>
     *
     * @return path used for permissions checks
     */
    public String getLogicalPermissionPath()
    {
        return profiledPath;
    }

    /**
     * <p>
     * getPhysicalPermissionPath
     * </p>
     *
     * @return path used for permissions checks
     */
    public String getPhysicalPermissionPath()
    {
        return path;
    }
}
