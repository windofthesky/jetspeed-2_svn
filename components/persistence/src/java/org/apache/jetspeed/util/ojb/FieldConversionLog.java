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
package org.apache.jetspeed.util.ojb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that allows convenient access to commons=logging for OJB 
 * FiledConversions without having to define a 
 * <code>org.apache.commons.logging.Log</code> in each of conversion
 * class.
 *
 *@author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 */
public abstract class FieldConversionLog
{
    /**
     * There is only default ("package") access to this Log only as
     * all OJB FieldConversions should be located here.
     */
    static final Log LOG = LogFactory.getLog("org.apache.jetspeed.util.ojb");
}
