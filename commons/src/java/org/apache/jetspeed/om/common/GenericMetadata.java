/*
 * Created on Feb 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.om.common;

import java.util.Collection;
import java.util.Locale;

import org.apache.pluto.om.common.ObjectID;

/**
 * @author jford
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface GenericMetadata {

    public void addField(Locale locale, String name, String value);
    public void addField(LocalizedField field);
    public Collection getFields(String name);
    public void setFields(String name, Collection values);
    
    public Collection getFields();
    public void setFields(Collection fields);
    
    /**
     * @param objectID
     */
    void setId(String objectID);
    
    /**
     * @see org.apache.pluto.om.portlet.PortletDefinition#getId()
     */
    public ObjectID getId();
}
