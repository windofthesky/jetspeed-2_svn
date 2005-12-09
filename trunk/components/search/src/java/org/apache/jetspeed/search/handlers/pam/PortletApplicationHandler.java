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
package org.apache.jetspeed.search.handlers.pam;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.MultiHashMap;
import org.apache.jetspeed.om.common.LocalizedField;
import org.apache.jetspeed.om.common.portlet.MutablePortletApplication;
import org.apache.jetspeed.search.AbstractObjectHandler;
import org.apache.jetspeed.search.BaseParsedObject;
import org.apache.jetspeed.search.ParsedObject;

/**
 *
 * @author <a href="mailto: jford@apache.org">Jeremy Ford</a>
 */
public class PortletApplicationHandler extends AbstractObjectHandler
{
    private static final String KEY_PREFIX = "PortletApplication::";
    private static final String ID = "ID";
    
    {
        fields.add(ID);        
    }

    /** 
     * @see org.apache.jetspeed.search.ObjectHandler#parseObject(java.lang.Object)
     */
    public ParsedObject parseObject(Object o)
    {
        BaseParsedObject result = null;
        
        if(o instanceof MutablePortletApplication)
        {
            result = new BaseParsedObject();
	        MutablePortletApplication pa = (MutablePortletApplication) o;
	        
	        result.setDescription(pa.getDescription());
	        result.setTitle(pa.getName());
	        result.setKey(KEY_PREFIX + pa.getName());
	        result.setType(ParsedObject.OBJECT_TYPE_PORTLET_APPLICATION);
	        result.setClassName(pa.getClass().getName());
	        
	        MultiHashMap fieldMap = new MultiHashMap();
	        fieldMap.put(ID, pa.getName());
	        
	        Collection fields = pa.getMetadata().getFields();
	        for (Iterator fieldIter = fields.iterator(); fieldIter.hasNext();)
            {
                LocalizedField field = (LocalizedField) fieldIter.next();
                fieldMap.put(field.getName(), field.getValue());
                //this.fields.add(field.getName());
            }
	        
	        result.setFields(fieldMap);
        }
        
        return result;
    }

}
