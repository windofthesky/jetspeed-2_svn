/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.components.portletentity;

import java.util.List;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jetspeed.om.common.portlet.PortletDefinitionComposite;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.jetspeed.om.window.impl.PortletWindowListImpl;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.Description;
import org.apache.pluto.om.common.ObjectID;
import org.apache.pluto.om.common.PreferenceSet;
import org.apache.pluto.om.entity.PortletApplicationEntity;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.entity.PortletEntityCtrl;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.util.StringUtils;

/**
 * Portlet Entity default implementation. 
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 */
public class PortletEntityImpl implements PortletEntity, PortletEntityCtrl
{

    private long oid;
    private long portletId;

    private JetspeedObjectID id;

    public ObjectID getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = JetspeedObjectID.createFromString(id);
    }

    private static final Log log = LogFactory.getLog(PortletEntityImpl.class);

    protected List originalPreferences;

    private PortletApplicationEntity applicationEntity = null;

    private PortletWindowList portletWindows = new PortletWindowListImpl();

    private PortletEntity modifiedObject = null;

    private PortletDefinitionComposite portletDefinition = null;
	
	/**
	 *  
	 * <p>
	 * getPreferenceSet
	 * </p>
	 *  <strong>WARNING!!!<br/></strong>
	 * This method should not be used to alter the contents
	 * of the PreferenceSet directly by user calls to the portal.  
	 * You should be wrapping this with an instance of the
	 * {@link StoreablePortletEntityDelegate#store()}.
	 * 
	 * @see org.apache.pluto.om.entity.PortletEntity#getPreferenceSet()
	 * @return
	 */
    public PreferenceSet getPreferenceSet()
    {
		return new PreferenceSetImpl(originalPreferences);
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
	
	/**
	 *  
	 * <p>
	 * store
	 * </p>
	 * This method is not directly supported. Use
	 * {@link StoreablePortletEntityDelegate#store()}
	 * 
	 */
    public void store() throws java.io.IOException
    {
		throw new UnsupportedOperationException("PortletEntityImpl.store() is not directly accessable.  "+ 
						   "Use the StoreablePortletEntityDelegate instead.");      

    }
    
    /**
     *  
     * <p>
     * reset
     * </p>
     * 
	 * This method is not directly supported. Use
	 * {@link StoreablePortletEntityDelegate#store()}
     */

    public void reset() throws java.io.IOException
    {
        throw new UnsupportedOperationException("PortletEntityImpl.reset() is not directly accessable.  "+ 
                   "Use the StoreablePortletEntityDelegate instead.");
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
        buffer.append(oid);
        buffer.append("'");
        StringUtils.newLine(buffer, indent);
        buffer.append("definition-id='");
        buffer.append(portletDefinition.getId().toString());
        buffer.append("'");

        StringUtils.newLine(buffer, indent);
        //buffer.append(((PreferenceSetImpl)preferences).toString(indent));

        StringUtils.newLine(buffer, indent);
        buffer.append("}");
        return buffer.toString();
    }

    /**
     * @see org.apache.pluto.om.entity.PortletEntity#getDescription(java.util.Locale)
     */
    public Description getDescription(Locale arg0)
    {
        return portletDefinition.getDescription(arg0);
    }

    /**
     * <p>
     * setPortletDefinition
     * </p>
     * 
     * @param composite
     * 
     */
    public void setPortletDefinition(PortletDefinition composite)
    {
        portletDefinition = (PortletDefinitionComposite) composite;
    }

}
