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
package org.apache.jetspeed.util;

/**
 * <p>
 * ArgUtil
 * </p>
 * 
 * Misc. utilities for rudimentary argument validation
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public final class ArgUtil
{
    private static final String MSG_1 = "Argument \"";
    private static final String MSG_2 = "\" cannot be null for method ";

    /**
     * 
     * @throws java.lang.IllegalArgumentException If ANY of the arguments are <code>null</code>
     * @param args array of arguments to validate as not nul
     * @param argNames array of arguments names, idexes should match with args. 
     * @param methodName Name of method we are validating arguments for.
     */
    public static void notNull(Object[] args, String[] argNames, String methodName)
    {
        for (int i = 0; i < args.length; i++)
        {
            Object arg = args[i];
            if (arg == null)
            {
                StringBuffer buf = new StringBuffer(150);
                String argName = null;
                if (i < argNames.length)
                {
                    argName = argNames[i];
                }
                else
                {
                    argName = String.valueOf(i);
                }

                buf.append(MSG_1 + argName + MSG_2 + methodName);
                throw new IllegalArgumentException(buf.toString());
            }
        }
    }
    
    /**
     * 
     * <p>
     * notNull
     * </p>
     *
     * @param nonNullObject
     * @param thisObject
     * @throws IllegalArgumentException
     */
    public static final void assertNotNull(Class nonNullClass, Object nonNullObject, Object thisObject) throws IllegalArgumentException
    {
        if(nonNullObject == null)
        {
            throw new IllegalArgumentException(thisObject.getClass().getName()+" requires a non-null "+nonNullClass.getName()+" as an argument.");
        }
    }
    
    public static final void assertNotNull(Class nonNullClass, Object nonNullObject, Object thisObject, String methodName) throws IllegalArgumentException
    {
        if(nonNullObject == null)
        {
            throw new IllegalArgumentException(thisObject.getClass().getName()+"."+methodName+" requires a non-null "+nonNullClass.getName()+" as an argument.");
        }
    }
    
    public static final void assertPropertyNotNull(Object nonNullObject, Object thisObject, String methodName, String property) throws IllegalArgumentException
    {
        if(nonNullObject == null)
        {
            throw new IllegalStateException(thisObject.getClass().getName()+"."+methodName+" cannot be invoked until the property "+property+" has been set.");
        }
    }
}
