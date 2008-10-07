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

package org.apache.jetspeed.security.util.ojb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;

/**
 * OJB RowReader for PersistentJetspeedPrincipal to derive the concrete principal className *and* instance dynamically
 * as OJB itself makes a mess of it...
 * @version $Id$
 */
public class JetspeedPrincipalConcreteClassRowReader extends RowReaderDefaultImpl
{
    private static final long serialVersionUID = 1L;

    private ClassDescriptor m_cld;
    
    private static JetspeedPrincipalManagerProvider jpmp;
    
    public static void setJetspeedPrincipalManagerProvider(JetspeedPrincipalManagerProvider jpmp)
    {
        JetspeedPrincipalConcreteClassRowReader.jpmp = jpmp;
    }
    
    private static final String OJB_CONCRETE_CLASS_KEY = "ojbTemporaryNoneColumnKey";
    
    /**
     * @param cld
     */
    public JetspeedPrincipalConcreteClassRowReader(ClassDescriptor cld)
    {
        super(cld);
        this.m_cld = cld;
    }

    /* (non-Javadoc)
     * @see org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl#extractOjbConcreteClass(org.apache.ojb.broker.metadata.ClassDescriptor, java.sql.ResultSet)
     */
    @Override
    protected String extractOjbConcreteClass(ClassDescriptor cld, ResultSet rs)
    {
        FieldDescriptor fld = m_cld.getFieldDescriptorByName("type");
        if (fld == null)
        {
            return null;
        }
        try
        {
            Object tmp = fld.getJdbcType().getObjectFromColumn(rs, fld.getColumnName());
            String result = jpmp.getPrincipalType((String)tmp).getClassName();
            result = result != null ? result.trim() : null;
            if (result == null || result.length() == 0)
            {
                throw new PersistenceBrokerException(
                        "ojbConcreteClass field for class " + cld.getClassNameOfObject()
                        + " returned null or 0-length string");
            }
            else
            {
                return result;
            }
        }
        catch(SQLException e)
        {
            throw new PersistenceBrokerException("Unexpected error while try to read 'ojbConcretClass'" +
                    " field from result set using column name " + fld.getColumnName() + " main class" +
                    " was " + m_cld.getClassNameOfObject(), e);
        }
    }

    protected Object buildOrRefreshObject(Map row, ClassDescriptor targetClassDescriptor, Object targetObject)
    {
        Object result = targetObject;
        FieldDescriptor fmd = null;

        if(targetObject == null)
        {
            // 1. create new object instance if needed
//            result = ClassHelper.buildNewObjectInstance(targetClassDescriptor);
            try
            {
                result = Class.forName((String) row.get(OJB_CONCRETE_CLASS_KEY)).newInstance();
            }
            catch (Exception e)
            {
                throw new PersistenceBrokerException("Unexpected error while try to instantiate concrete PersistentJetspeedPrincipal instance of class: [" +row.get(OJB_CONCRETE_CLASS_KEY), e);
            }
        }

        // 2. fill all scalar attributes of the new object
        FieldDescriptor[] fields = targetClassDescriptor.getFieldDescriptions();
        for (int i = 0; i < fields.length; i++)
        {
            fmd = fields[i];
            fmd.getPersistentField().set(result, row.get(fmd.getColumnName()));
        }

        return result;
    }
    
    protected ClassDescriptor selectClassDescriptor(Map row) throws PersistenceBrokerException
    {
        ClassDescriptor result = m_cld;
        String ojbConcreteClass = (String) row.get(OJB_CONCRETE_CLASS_KEY);
        if(ojbConcreteClass != null)
        {
            result = m_cld.getRepository().getDescriptorFor(ojbConcreteClass);
            // if we can't find class-descriptor for concrete class, something wrong with mapping
            if (result == null)
            {
                throw new PersistenceBrokerException("Can't find class-descriptor for ojbConcreteClass '"
                        + ojbConcreteClass + "', the main class was " + m_cld.getClassNameOfObject());
            }
        }
        return result;
    }

}
