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
package org.apache.jetspeed.util.ojb;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
                  System.out.println("In CSVtoCollectionFieldConversion() - The string '" + value + 
                        "' contains embeded '\"'.  It will not be converted to a CSV correctly.");
                }
                buffer.append(value);
                // End of FIXME:
                buffer.append(QUOTE);
                System.out.println("String encoded ");
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
                    System.out.println("Parsed token value: "+st.sval);
                }
            }
            catch (IOException e)
            {
                String message = "CSV parsing failed during field conversion.";
             //    FieldConversionLog.LOG.error(message, e);
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
