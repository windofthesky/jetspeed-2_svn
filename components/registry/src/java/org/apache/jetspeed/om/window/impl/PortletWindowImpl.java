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
package org.apache.jetspeed.om.window.impl;

import java.io.Serializable;

import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;
import org.apache.pluto.om.entity.PortletEntity;
import org.apache.pluto.om.common.ObjectID;
import org.apache.jetspeed.util.JetspeedObjectID;

/**
 * <P>
 * The <CODE>PortletWindow</CODE> implementation represents a single window
 * of an portlet instance as it can be shown only once on a single page. 
 * Adding the same portlet e.g. twice on a page results in two different windows.
 * </P>
 *
 * @author <a href="mailto:david@bluesunrise.com">David Sean Taylor</a>
 * @version $Id$
 **/
public class PortletWindowImpl implements PortletWindow, PortletWindowCtrl, Serializable
{
    private ObjectID objectId = null;
    private int id;
    private int portletEntityId;
    private PortletEntity portletEntity = null;

    //counter used to generate unique id's
    private static int counter;

    public PortletWindowImpl(String id)
    {
        this.id = JetspeedObjectID.createFromString(id).intValue();
    }

    public PortletWindowImpl(ObjectID oid)
    {
        this.objectId = oid;
    }

    public PortletWindowImpl()
    {
        super();
    }

    /**
    * Returns the identifier of this portlet instance window as object id
    *
    * @return the object identifier
    **/
    public ObjectID getId()
    {
        if (objectId == null)
        {
            objectId = new JetspeedObjectID(id);
        }
        return objectId;
    }
    /**
     * Returns the portlet entity
     *
     * @return the portlet entity
     **/
    public PortletEntity getPortletEntity()
    {
        return portletEntity;
    }

    // controller impl
    /**
     * binds an identifier to this portlet window
     *
     * @param id the new identifier
     */
    public void setId(String id)
    {
        this.id = JetspeedObjectID.createFromString(id).intValue();
        objectId = null;
    }

    /**
     * binds a portlet instance to this portlet window
     * 
     * @param portletEntity a portlet entity object
     **/
    public void setPortletEntity(PortletEntity portletEntity)
    {
        this.portletEntity = portletEntity;
    }

}
