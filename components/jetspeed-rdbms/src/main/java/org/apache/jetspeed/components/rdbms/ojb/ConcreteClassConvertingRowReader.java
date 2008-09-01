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

package org.apache.jetspeed.components.rdbms.ojb;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.accesslayer.RowReaderDefaultImpl;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;

/**
 * <p>
 * ConcreteClassConvertingRowReader overrides the extractOjbConcreteClass method to allow OJB sqlToJava field conversion for the ojbConcreteClass field
 * with OJB 1.0.3 (OJB 1.0.4 has this fixed).
 * </p>
 * <p>
 *   See also: <a href="http://www.nabble.com/Minor-1.0.1-%3D%3E-1.0.3-migration-issue-p1229220.html">Minor 1.0.1 => 1.0.3 migration issue</a>
 * </>
 * <p>
 * This extension allows to store the objConcreteClass field as needed for Polymorphism support in OJB differently,
 * e.g. not as its className but allow lookup of if based on a different value, e.g. like "user" -> org.apache.jetspeed.security.JetspeedUserImpl
 * </p>
 * @version $Id$
 *
 */
public class ConcreteClassConvertingRowReader extends RowReaderDefaultImpl
{
    private static final long serialVersionUID = -1888890958151364686L;

    private ClassDescriptor m_cld;
    
    /**
     * @param cld
     */
    public ConcreteClassConvertingRowReader(ClassDescriptor cld)
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
        FieldDescriptor fld = m_cld.getOjbConcreteClassField();
        if (fld == null)
        {
            return null;
        }
        try
        {
            Object tmp = fld.getJdbcType().getObjectFromColumn(rs, fld.getColumnName());
            // allow field-conversion for discriminator column too
            String result = (String) fld.getFieldConversion().sqlToJava(tmp);
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

}
