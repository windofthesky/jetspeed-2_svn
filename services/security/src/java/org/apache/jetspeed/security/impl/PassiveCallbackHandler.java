/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2004 The Apache Software Foundation.  All rights
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
package org.apache.jetspeed.security.impl;

import javax.security.auth.callback.*;

/**
 * <p>PassiveCallbackHandler has constructor that takes
 * a username and password so its handle() method does
 * not have to prompt the user for input.</p>
 * <p>Useful for server-side applications.</p>
 * 
 * <p>This code was inspired from an article from:<p>
 * <ul>
 *    <li><a href="http://www.javaworld.com/javaworld/jw-09-2002/jw-0913-jaas.html">
 *    All that JAAS</a></li>
 * </ul> *
 * @author <a href="mailto:david@sensova.com">David Le Strat</a>
 */

public class PassiveCallbackHandler implements CallbackHandler
{

    private String username;
    char[] password;

    /**
     * <p>Creates a callback handler with the give username
     * and password.</p>
     * @param username The username.
     * @param password The password.
     */
    public PassiveCallbackHandler(String username, String password)
    {
        this.username = username;
        this.password = password.toCharArray();
    }

    /**
     * <p>Handles the specified set of Callbacks. Uses the
     * username and password that were supplied to our
     * constructor to popluate the Callbacks.</p>
     * <p>This class supports NameCallback and PasswordCallback.</p>
     *
     * @param   callbacks the callbacks to handle
     * @throws  IOException if an input or output error occurs.
     * @throws  UnsupportedCallbackException if the callback is not an
     *          instance of NameCallback or PasswordCallback
     */
    public void handle(Callback[] callbacks) throws java.io.IOException, UnsupportedCallbackException
    {
        for (int i = 0; i < callbacks.length; i++)
        {
            if (callbacks[i] instanceof NameCallback)
            {
                ((NameCallback) callbacks[i]).setName(username);
            }
            else if (callbacks[i] instanceof PasswordCallback)
            {
                ((PasswordCallback) callbacks[i]).setPassword(password);
            }
            else
            {
                throw new UnsupportedCallbackException(callbacks[i], "Callback class not supported");
            }
        }
    }

    /**
     * <p>Clears out password state.</p>
     */
    public void clearPassword()
    {
        if (password != null)
        {
            for (int i = 0; i < password.length; i++)
            {
                password[i] = ' ';
            }
            password = null;
        }
    }

}
