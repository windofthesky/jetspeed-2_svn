/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.om.common.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.om.common.GenericMetadata;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;

/**
 * @author jford
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GenericMetadataImpl implements GenericMetadata
{

    private int id;
    
    private Collection fields = null;
    private transient MultiHashMap fieldMap = new MultiHashMap();
    
    
    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#addField(java.util.Locale, java.lang.String, java.lang.String)
     */
    public void addField(Locale locale, String name, String value)
    {
        LocalizedFieldImpl field = new LocalizedFieldImpl();
        field.setName(name);
        field.setValue(value);
        field.setLocale(locale);
        
        if(fields == null)
        {
            fields = new ArrayList();
        }
        
        fields.add(field);
        fieldMap.put(name, field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#addField(org.apache.jetspeed.om.common.LocalizedField)
     */
    public void addField(LocalizedField field)
    {
        if(fields == null)
        {
            fields = new ArrayList();
        }
        
        fields.add(field);
        fieldMap.put(field.getName(), field);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields(java.lang.String)
     */
    public Collection getFields(String name)
    {
        return (Collection)fieldMap.get(name);
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setFields(java.lang.String, java.util.Collection)
     */
    public void setFields(String name, Collection values)
    {
        fieldMap.remove(name);
        fieldMap.put(name, values);
        fields.removeAll(values);
    }
    
    /**
     * 
     */
    public ObjectID getId()
    {
        return new JetspeedObjectID(id);
    }

    /**
     * 
     */
    public void setId(String oid)
    {
        id = JetspeedObjectID.createFromString(oid).intValue();
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#getFields()
     */
    public Collection getFields() {
        return fields;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.GenericMetadata#setField(java.util.Collection)
     */
    public void setFields(Collection fields)
    {
        this.fields = fields;
        
        if(fields != null)
        {    
            Iterator fieldIter = fields.iterator();
            while(fieldIter.hasNext())
            {
                LocalizedField field = (LocalizedField)fieldIter.next();
                fieldMap.put(field.getName(), field);
            }
        }
        
    }
}
