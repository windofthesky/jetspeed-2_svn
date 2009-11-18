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
package org.apache.jetspeed.util.ojb;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * <p style="font-weight: bold">
 * ObjectRelationalBridge field conversion.
 * </p>
 * Converts from a comma-delimited field to a <code>java.util.collection</code>
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class CSVtoCollectionFieldConversion implements FieldConversion
{
    private static final String DELIM = ",";
    private static final String QUOTE = "\"";
    
    private static final Logger log = LoggerFactory.getLogger(CSVtoCollectionFieldConversion.class);
    
    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#javaToSql(java.lang.Object)
     * @task Fix JDK 1.3 complient problem described in the FIXME
     */
    public Object javaToSql(Object arg0) throws ConversionException
    {
        if (arg0 instanceof Collection)
        {
            Collection col = ((Collection) arg0);
            if (col.size() == 0)
            {
                return "";
            }

            Iterator itr = col.iterator();
            // Estimate that the average word is going to be 5 characters long
            StringBuffer buffer = new StringBuffer((col.size() * 5));
            while (itr.hasNext())
            {
                buffer.append(QUOTE);
                String value = getNext(itr);

                // FIXME: The following is not JDK1.3 complient. So I implement a warning 
                //        message as a workaround until this field conversion is no longer
                //        need and delete, or the code is made JDK 1.3 complient. (Paul Spencer)

                // buffer.append(value.replaceAll("\"","\\\\\""));
                if(value != null && value.toString().indexOf("\"") >= 0)
                {
                //  FieldConversionLog.LOG.error("The string '" + value + 
                // "' contains embeded '\"'.  It will not be converted to a CSV correctly.");
                  log.warn("In CSVtoCollectionFieldConversion() - The string '" + value + 
                        "' contains embeded '\"'.  It will not be converted to a CSV correctly.");
                }
                buffer.append(value);
                // End of FIXME:
                buffer.append(QUOTE);
                
                if (itr.hasNext())
                {
                    buffer.append(DELIM);
                }
            }

            return buffer.toString();
        }
        return arg0;
    }

    /**
     * @see org.apache.ojb.broker.accesslayer.conversions.FieldConversion#sqlToJava(java.lang.Object)
     */
    public Object sqlToJava(Object arg0) throws ConversionException
    {

        if (arg0 instanceof String)
        {
            StringReader sr = new StringReader((String) arg0);
            StreamTokenizer st = new StreamTokenizer(sr);
            st.resetSyntax();
            st.whitespaceChars(',', ',');
            st.quoteChar('"');
            st.eolIsSignificant(false);

         
            ArrayList list = new ArrayList();
            try
            {
                while (st.nextToken() != StreamTokenizer.TT_EOF)
                {
                    list.add(createObject(st.sval));
                }
            }
            catch (IOException e)
            {
                String message = "CSV parsing failed during field conversion.";
                log.error(message, e);
                throw new ConversionException("CSV parsing failed during field conversion.", e);
            } 

            return list;
        }

        return arg0;
    }

    /**
     * Makes creation of objects created via csv fields extensible
     * By default simply return the string value.
     * 
     * @param name The string value
     * @return The string value
     */
    protected Object createObject(String name)
    {
        return name;
    }

    /**
     * Makes getting objects via csv fields extensible
     * By default simply return the string value.
     * 
     * @param name The string value
     * @return The string value
     */
    protected String getNext(Iterator iterator)
    {
        return (String) iterator.next();
    }

}
