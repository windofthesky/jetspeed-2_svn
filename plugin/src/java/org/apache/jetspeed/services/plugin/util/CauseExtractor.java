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
    }

}
