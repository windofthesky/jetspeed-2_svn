/*
 * Copyright 2000-2004 The Apache Software Foundation.
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
package org.apache.jetspeed.services.plugin.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <p>
 * CauseExtractor
 * </p>
 * 
 * Strings together all the messages within the exception
 * stack.  Checks for the "getCause" method so as to be pre-1.4
 * compatible.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class CauseExtractor
{

    /**
     * Combines all the exception classes names and there
     * messages from <code>e</code>'s exception stack into a single string. 
     * @param e base exception to extract all exceptions from.
     * @return String containing all exception names and messages in
     * <code>e</code>'s exception hierarchy.
     */
    public static String getCompositeMessage(Exception e)
    {
        if (e == null)
        {
            throw new IllegalArgumentException("The exception cannot be null");
        }
        StringBuffer buf = new StringBuffer();
        walkExceptions(e, buf);
        return buf.toString();
    }

    public static void walkExceptions(Exception e, StringBuffer message)
    {

        try
        {
            Method method = e.getClass().getMethod("getCause", null);

            message.append(e.getClass().getName()).append(": ").append((String) method.invoke(e, null));

            message.append(". Cause: ");
        }
        catch (SecurityException e1)
        {
            e1.printStackTrace();
        }
        catch (NoSuchMethodException e1)
        {
            // we are done
            return;
        }
        catch (IllegalArgumentException e2)
        {
            e2.printStackTrace();
        }
        catch (IllegalAccessException e3)
        {

            e3.printStackTrace();
        }
        catch (InvocationTargetException e4)
        {

            e4.printStackTrace();
        }        
        catch (ClassCastException e5)
        {
            e5.printStackTrace();
        }
    }

}
