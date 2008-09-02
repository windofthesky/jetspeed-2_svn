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

import org.apache.jetspeed.security.JetspeedPrincipalManagerProvider;
import org.apache.ojb.broker.accesslayer.conversions.ConversionException;
import org.apache.ojb.broker.accesslayer.conversions.FieldConversion;

/**
 * @version $Id$
 *
 */
public class JetspeedPrincipalTypeFieldConversion implements FieldConversion
{
    private static final long serialVersionUID = -4210390887451224757L;
    
    private static JetspeedPrincipalManagerProvider jpmp;
    
    public static void setJetspeedPrincipalManagerProvider(JetspeedPrincipalManagerProvider jpmp)
    {
        JetspeedPrincipalTypeFieldConversion.jpmp = jpmp;
    }
    
    public Object javaToSql(Object javaObject) throws ConversionException
    {
      return jpmp.getPrincipalTypeByClassName((String)javaObject).getName();
    }

    public Object sqlToJava(Object sqlObject) throws ConversionException
    {
        return jpmp.getPrincipalType((String)sqlObject).getClassName();
    }

}
