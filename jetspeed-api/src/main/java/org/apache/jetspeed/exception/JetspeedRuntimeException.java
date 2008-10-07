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


/**
 * Base exception for all RuntimeExceptions defined within Jetspeed.
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public class JetspeedRuntimeException extends RuntimeException
{

    public static final String KEYED_MESSAGE_BUNDLE = "org.apache.jetspeed.exception.JetspeedExceptionMessages";
    
    private KeyedMessage keyedMessage;
    
    /**
     * 
     */
    public JetspeedRuntimeException()
    {
        super();
    }

    /**
     * @param arg0
     */
    public JetspeedRuntimeException(String arg0)
    {
        super(arg0);
    }

    public JetspeedRuntimeException(KeyedMessage typedMessage) 
    {
        super(typedMessage.getMessage());
        this.keyedMessage = typedMessage;
    }
    
    /**
     * @param arg0
     */
    public JetspeedRuntimeException(Throwable arg0)
    {
        super(arg0);
    }

    /**
     * @param arg0
     * @param arg1
     */
    public JetspeedRuntimeException(String arg0, Throwable arg1)
    {
        super(arg0, arg1);
    }

    public JetspeedRuntimeException(KeyedMessage keyedMessage, Throwable nested)
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
}
