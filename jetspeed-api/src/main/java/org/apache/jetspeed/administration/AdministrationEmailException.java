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
package org.apache.jetspeed.administration;

import org.apache.jetspeed.exception.JetspeedException;
import org.apache.jetspeed.i18n.KeyedMessage;

/**
 * Administration Email exceptions
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public class AdministrationEmailException extends JetspeedException
{
    public AdministrationEmailException()
    {
        super();
    }

    public AdministrationEmailException(KeyedMessage keyedMessage, Throwable nested)
    {
        super(keyedMessage, nested);
    }

    public AdministrationEmailException(KeyedMessage keyedMessage)
    {
        super(keyedMessage);
    }

    public AdministrationEmailException(String msg, Throwable nested)
    {
        super(msg, nested);
    }

    public AdministrationEmailException(String message)
    {
        super(message);
    }

    public AdministrationEmailException(Throwable nested)
    {
        super(nested);
    }
}
