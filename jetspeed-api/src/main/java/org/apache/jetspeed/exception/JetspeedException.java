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
package org.apache.jetspeed.exception;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.jetspeed.i18n.KeyedMessage;
import org.apache.jetspeed.request.RequestDiagnostics;
import org.apache.jetspeed.request.RequestDiagnosticsHolder;


/**
 * Occurs when anything unexpected happens within Jetspeed.Any defined exceptions
 * within Jetspeed should always extend from this.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 **/

public class JetspeedException extends Exception implements RequestDiagnosticsHolder
{
    public static final String KEYED_MESSAGE_BUNDLE = "org.apache.jetspeed.exception.JetspeedExceptionMessages";
    
    private KeyedMessage keyedMessage;
    private RequestDiagnostics rd;
    
    public JetspeedException() 
    {
        super();
    }
    
    public JetspeedException(String message) 
    {
        super(message);
    }
    
    public JetspeedException(KeyedMessage typedMessage) 
    {
        super(typedMessage.getMessage());
        this.keyedMessage = typedMessage;
    }
    
    public JetspeedException(Throwable nested)
    {
        super(nested);
    }
    
    public JetspeedException(String msg, Throwable nested)
    {
        super(msg, nested);
    }
    
    public JetspeedException(KeyedMessage keyedMessage, Throwable nested)
    {
        super(keyedMessage.getMessage(), nested);
        this.keyedMessage = keyedMessage;
    }
    
    public KeyedMessage getKeyedMessage()
    {
        return keyedMessage;
    }
    
    public String getMessage()
    {
        if ( keyedMessage != null )
        {
            return keyedMessage.getMessage();
        }
        return super.getMessage();
    }
    
    public String getMessage(ResourceBundle bundle)
    {
        if ( keyedMessage != null )
        {
            return keyedMessage.getMessage(bundle);
        }
        return super.getMessage();
    }

    public String getMessage(Locale locale)
    {
        if ( keyedMessage != null )
        {
            return keyedMessage.getMessage(locale);
        }
        return super.getMessage();
    }
    
    public void setRequestDiagnostics(RequestDiagnostics rd)
    {
        this.rd = rd;
    }
    
    public RequestDiagnostics getRequestDiagnostics()
    {
        return rd;
    }
}
