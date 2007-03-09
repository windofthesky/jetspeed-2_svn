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
package org.apache.jetspeed.i18n;

import java.util.Locale;

/**
 * Maintains a Locale for the current Thread
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma </a>
 * @version $Id$
 */
public final class CurrentLocale
{
    private static ThreadLocal currentLocale = new ThreadLocal();
    
    private CurrentLocale()
    {    
    }
    
    /** @return the currently {@link #set(Locale) set} Locale in this Thread or Locale.getDefault() otherwise
     */
    public static Locale get()
    {
        Locale locale = (Locale)currentLocale.get();
        return locale != null ? locale : Locale.getDefault();
    }
    
    /**
     * Sets a Locale for this Thread.
     * <br>
     * Use a null parameter to revert back to Locale.getDefault() 
     * @param locale Locale for this Thread 
     */
    public static void set(Locale locale)
    {
        currentLocale.set(locale);
    }
}
