/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.jetspeed.om.common.entity;

import java.util.Collection;
import java.util.Locale;

import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.util.StringUtils;

import org.apache.jetspeed.om.common.BasePreferenceSet;
import org.apache.jetspeed.om.common.DescriptionImpl;
import org.apache.jetspeed.om.common.DescriptionSetImpl;
import org.apache.jetspeed.om.common.MutableDescription;
import org.apache.jetspeed.om.common.MutableDescriptionSet;
import org.apache.jetspeed.om.common.window.PortletWindowListImpl;
import org.apache.jetspeed.util.JetspeedObjectID;

/**
 * Portlet Entity default implementation. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class BasePortletEntity implements PortletEntity, PortletEntityCtrl, java.io.Serializable
{
    private String id = "";
    private String definitionId = "";
    private String description;

    protected BasePreferenceSet preferences = new BasePreferenceSet();
    private BasePreferenceSet origPreferences = new BasePreferenceSet();

    private PortletApplicationEntity applicationEntity = null;

    private PortletWindowList portletWindows = new PortletWindowListImpl();

    private ObjectID objectId = null;

    private PortletEntity modifiedObject = null;

    private PortletDefinition portletDefinition = null;

    private MutableDescriptionSet descriptions;

    public BasePortletEntity(PortletDefinition pd, String id)
    {
        this.portletDefinition = pd;
        preferences.addAll((BasePreferenceSet) pd.getPreferenceSet());
        setId(id);
    }

    public ObjectID getId()
    {
        if (objectId == null)
        {
            objectId = JetspeedObjectID.createFromString(id);
        }
        return objectId;
    }

    public void setId(String id)
    {
        this.id = id;
        this.objectId = JetspeedObjectID.createFromString(id);
    }

    public PreferenceSet getPreferenceSet()
    {
        return preferences;
    }

    public PortletDefinition getPortletDefinition()
    {
        return this.portletDefinition;
    }

    public PortletApplicationEntity getPortletApplicationEntity()
    {
        return applicationEntity;
    }

    public PortletWindowList getPortletWindowList()
    {
        return portletWindows;
    }

    public void store() throws java.io.IOException
    {
        // TODO: implement this PortletEntityRegistry.store();

        //save preferences as original preferences
        origPreferences = new BasePreferenceSet();
        ((BasePreferenceSet) origPreferences).addAll((Collection) preferences);
    }

    public void reset() throws java.io.IOException
    {
        //reset by re-activating original preferences
        preferences = new BasePreferenceSet();
        ((BasePreferenceSet) preferences).addAll((Collection) origPreferences);
    }

    // additional internal methods

    public Collection getCastorPreferences()
    {
        return (BasePreferenceSet) preferences;
    }

    public void postLoad(Object parameter) throws Exception
    {
    }
    public void preBuild(Object parameter) throws Exception
    {
        ((BasePreferenceSet) origPreferences).addAll((Collection) preferences);
        setPortletApplicationEntity((PortletApplicationEntity) parameter);
    }
    public void postBuild(Object parameter) throws Exception
    {
    }
    public void preStore(Object parameter) throws Exception
    {
    }
    public void postStore(Object parameter) throws Exception
    {
    }

    protected void setPortletApplicationEntity(PortletApplicationEntity applicationEntity)
    {
        this.applicationEntity = applicationEntity;
    }

    protected void setPortletWindowList(PortletWindowList portletWindows)
    {
        this.portletWindows = portletWindows;
    }

    // internal methods used for castor only 
    public String getCastorId()
    {
        //ObjectID oid = getId();        
        //if(oid == null) 
        //return null;

        //return  oid.toString();
        return id.length() > 0 ? id : null;
    }

    public void setCastorId(String id)
    {
        setId(id);
    }

    public String getDefinitionId()
    {
        return definitionId;
    }

    public void setDefinitionId(String definitionId)
    {
        this.definitionId = definitionId;
    }

    // internal methods used for debugging purposes only

    public String toString()
    {
        return toString(0);
    }

    public String toString(int indent)
    {
        StringBuffer buffer = new StringBuffer(1000);
        StringUtils.newLine(buffer, indent);
        buffer.append(getClass().toString());
        buffer.append(":");
        StringUtils.newLine(buffer, indent);
        buffer.append("{");
        StringUtils.newLine(buffer, indent);
        buffer.append("id='");
        buffer.append(id);
        buffer.append("'");
        StringUtils.newLine(buffer, indent);
        buffer.append("definition-id='");
        buffer.append(definitionId);
        buffer.append("'");

        StringUtils.newLine(buffer, indent);
        //buffer.append(((BasePreferenceSet)preferences).toString(indent));

        StringUtils.newLine(buffer, indent);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * @see org.apache.pluto.om.entity.PortletEntity#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        if (descriptions != null)
        {
            return descriptions.get(arg0);
        }
        return null;
    }

    /**
     * Remove when Castor is properly mapped
     * @deprecated
     * @return
     */
    public String getDescription()
    {
        Description desc = getDescription(Locale.getDefault());
        if (desc != null)
        {
            return desc.getDescription();
        }
        return null;
    }

    /**
     * Remove when Castor is properly mapped
     * @deprecated
     * @param desc
     */
    public void setDescription(String desc)
    {
        if (descriptions == null)
        {
            descriptions = new DescriptionSetImpl(MutableDescription.TYPE_PORTLET);

        }
        descriptions.addDescription(new DescriptionImpl(Locale.getDefault(), desc, MutableDescription.TYPE_PORTLET));
    }

}
