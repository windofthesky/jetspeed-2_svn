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
package org.apache.jetspeed.page.document.psml;

import org.apache.jetspeed.JetspeedActions;
import org.apache.jetspeed.idgenerator.IdGenerator;
import org.apache.jetspeed.om.common.SecurityConstraints;
import org.apache.jetspeed.om.page.PageMetadataImpl;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.om.page.psml.AbstractBaseElement;
import org.apache.jetspeed.om.page.psml.SecurityConstraintsImpl;
import org.apache.jetspeed.om.portlet.GenericMetadata;
import org.apache.jetspeed.om.portlet.LocalizedField;
import org.apache.jetspeed.page.document.Node;

import java.util.Collection;
import java.util.List;
import java.util.Locale;


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
    private PageMetadataImpl metadata;
    private Node parent;
    private String path;
    private String url;
    private boolean hidden=false;
    private String profiledPath;
    private boolean dirty=false;
    
    public AbstractNode()
    {
    }

    /**
     * getMetadata - get/construct metadata
     *
     * @return metadata
     */
    public GenericMetadata getMetadata()
    {
        return getPageMetadata();
    }

    /**
     * setMetadata - set metadata fields
     *
     * @param metadata metadata
     */
    public void setMetadata(GenericMetadata metadata)
    {
        getPageMetadata().setFields(metadata.getFields());
    }

    /**
     * getMetadataFields - get metadata fields collection for
     *                     marshalling/unmarshalling
     *
     * @return metadata fields collection
     */
    public Collection<LocalizedField> getMetadataFields()
    {
        // return metadata fields collection that
        // may in fact be side effected on unmarshall
        return getPageMetadata().getFields();
    }

    /**
     * setMetadataFields - set metadata fields collection
     *
     * @param metadataFields metadata fields collection
     */
    public void setMetadataFields(Collection<LocalizedField> metadataFields)
    {
        // set metadata fields collection that
        // may in fact be side effected after
        // invocation on unmarshall
        getPageMetadata().setFields(metadataFields);
    }

    /**
     * getPageMetadata - get/construct page metadata instance
     *
     * @return metadata instance
     */
    private PageMetadataImpl getPageMetadata()
    {
        if (metadata == null)
        {
            metadata = new PageMetadataImpl();
        }
        return metadata;
    }

    /**
     * <p>
     * getTitle
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#getTitle(java.util.Locale)
     * @param locale
     * @return title in specified locale
     */
    public String getTitle(Locale locale)
    {
        // get title from metadata or use default title
        String title = getPageMetadata().getText("title", locale);
        if (title == null)
        {
            title = getTitle();
        }
        return title;
    }

    /**
     * <p>
     * getShortTitle
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#getShortTitle(java.util.Locale)
     * @param locale
     * @return short title in specified locale
     */
    public String getShortTitle( Locale locale )
    {
        // get short title from metadata or use title from metadata,
        // default short title, or default title
        String shortTitle = getPageMetadata().getText("short-title", locale);
        if (shortTitle == null)
        {
            shortTitle = getPageMetadata().getText("title", locale);
            if (shortTitle == null)
            {
                shortTitle = getShortTitle();
                if (shortTitle == null)
                {
                    shortTitle = getTitle();
                }
            }
        }
        return shortTitle;
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
            parent.checkAccess(JetspeedActions.VIEW);
        }
        return parent;
    }

    /**
     * <p>
     * getParent
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#getParent()
     * @return parent node
     */
    public Node getParent()
    {
        // by default disable access checks since it is assumed
        // that by accessing this node, access to parent must
        // also be granted
        return getParent(false);
    }

    /**
     * <p>
     * setParent
     * </p>
     * 
     * @see org.apache.jetspeed.page.document.Node#setParent(Node)
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
     * @return Name
     */
    public String getName()
    {
        // simply strip path to determine name
        String name = getPath();
        if ((name != null) && !name.equals(PATH_SEPARATOR))
        {
            if (name.endsWith(PATH_SEPARATOR))
            {
                name = name.substring(0, name.length()-1);
            }
            name = name.substring(name.lastIndexOf(PATH_SEPARATOR)+1);
        }
        return name;
    }

    /**
     * getTitleName - get name for use as default titles
     *
     * @return title name
     */
    public String getTitleName()
    {
        String titleName = getName();
        if (titleName != null)
        {
            // transform file system name to title
            if (titleName.endsWith(getType()) && (titleName.length() > getType().length()))
            {
                titleName = titleName.substring(0, titleName.length()-getType().length());
            }
            else if (titleName.equals(PATH_SEPARATOR))
            {
                titleName = "top";
            }
            titleName = titleName.replace('_', ' ');
            titleName = titleName.replace('-', ' ');
            titleName = titleName.trim();
            int wordIndex = -1;
            do
            {
                if (!Character.isTitleCase(titleName.charAt(wordIndex+1)))
                {
                    StringBuffer makeTitle = new StringBuffer();
                    makeTitle.append(titleName.substring(0, wordIndex+1));
                    makeTitle.append(Character.toTitleCase(titleName.charAt(wordIndex+1)));
                    makeTitle.append(titleName.substring(wordIndex+2));
                    titleName = makeTitle.toString();
                }
                wordIndex = titleName.indexOf(' ', wordIndex+1);
            }
            while (wordIndex != -1);
        }
        return titleName;
    }

    /**
     * @return Returns the path.
     */
    public String getPath()
    {
        return path;
    }
    
    /**
     * <p>
     * setPath
     * </p>
     *
     * @param path The path to set.
     */
    public void setPath( String path )
    {
        // PSML id is always kept in sync with path, despite how the
        // id may be loaded from the persistent store
        this.path = path;
        setId(path);
    }

    /**
     * <p>
     * getUrl
     * </p>
     * Same as invoking <code>Node.getPath()</code> unless url explicitly set.
     *
     * @see org.apache.jetspeed.page.document.Node#getUrl()
     * @return url as string
     */
    public String getUrl()
    {
        if (url != null)
        {
            return url;
        }
        return getPath();
    }

    /**
     * <p>
     * setUrl
     * </p>
     *
     * @param url The url to set.
     */
    public void setUrl( String url )
    {
        this.url = url;
    }

    /**
     * <p>
     * isHidden
     * </p>
     *
     * @see org.apache.jetspeed.page.document.Node#isHidden()
     * @return hidden
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
     * @param profiledPath The profiled path to set.
     */
    public void setProfiledPath( String profiledPath )
    {
        this.profiledPath = profiledPath;
    }

    /**
     * getEffectivePageSecurity
     *
     * @see org.apache.jetspeed.om.page.psml.AbstractBaseElement#getEffectivePageSecurity()
     */
    public PageSecurity getEffectivePageSecurity()
    {
        // by default, delegate to parent node implementation
        if (parent != null)
        {
            return ((AbstractNode)parent).getEffectivePageSecurity();
        }
        return null;
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
    public void checkConstraints(List<String> actions, List<String> userPrincipals, List<String> rolePrincipals, List<String> groupPrincipals, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check constraints in node hierarchy
        if (checkNodeOnly)
        {
            // check node constraints if available; otherwise,
            // recursively check parent constraints until
            // default constraints for node are checked
            SecurityConstraints constraints = getSecurityConstraints();
            if ((constraints != null) && !constraints.isEmpty())
            {
                ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
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
                if ((constraints != null) && !constraints.isEmpty())
                {
                    ((SecurityConstraintsImpl)constraints).checkConstraints(actions, userPrincipals, rolePrincipals, groupPrincipals, getEffectivePageSecurity());
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
     * @param mask Mask of actions requested
     * @param checkNodeOnly
     * @param checkParentsOnly
     * @throws SecurityException
     */
    public void checkPermissions(String path, int mask, boolean checkNodeOnly, boolean checkParentsOnly) throws SecurityException
    {
        // check granted node permissions unless the check is
        // to be skipped due to explicity granted access
        if (!checkParentsOnly)
        {
            super.checkPermissions(path, mask, true, false);
        }
        
        // if not checking node only, recursively check
        // all parent permissions in hierarchy
        if (!checkNodeOnly && (parent != null))
        {
            ((AbstractNode)parent).checkPermissions(mask, false, false);
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

        // force metadata update after unmarshalled since
        // metadata collection can be side effected by
        // unmarshalling collection accessors
        Collection<LocalizedField> metadataFields = getMetadataFields();
        if (metadataFields != null)
        {
            setMetadataFields(metadataFields);
        }
        
        return dirty;
    }

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty=dirty;
	}
    
    
}
