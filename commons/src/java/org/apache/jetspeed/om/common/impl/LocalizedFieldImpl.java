/* Copyright 2004 Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.om.common.impl;
import java.util.Locale;

import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.util.JetspeedObjectID;
import org.apache.pluto.om.common.ObjectID;

/**
 * LocalizedFieldImpl
 * <br/>
 * Implementation that represents a string value and the locale of that string
 * 
 * @author <a href="mailto:jford@apache.org">Jeremy Ford</a>
 * @version $Id$
 *
 */
public class LocalizedFieldImpl implements LocalizedField
{
    protected String value;
    protected Locale locale;
    
    protected long parentId;
    protected int id;
    

    public LocalizedFieldImpl()
    {
        
    }
    
    public LocalizedFieldImpl(Locale locale, String value)
    {
        this.locale = locale;
        this.value = value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#getLocale()
     */
    public Locale getLocale()
    {
        return locale;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#setLocale(java.util.Locale)
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;        
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#getValue()
     */
    public String getValue()
    {
        return value;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.common.LocalizedField#setValue(java.lang.String)
     */
    public void setValue(String value)
    {
        this.value = value;        
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
}
