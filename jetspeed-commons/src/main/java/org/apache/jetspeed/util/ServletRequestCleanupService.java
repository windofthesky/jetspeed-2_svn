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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version $Id$
 *
 */
public class ServletRequestCleanupService
{
    private static ThreadLocal<List<ServletRequestCleanupCallback>> cleanups = new ThreadLocal<List<ServletRequestCleanupCallback>>();
    private static ThreadLocal<Object> firstCleaner = new ThreadLocal<Object>();
    
    private static List<ServletRequestCleanupCallback> getCleanups(boolean create)
    {
        List<ServletRequestCleanupCallback> list = cleanups.get();
        if (list == null && create)
        {
            list = new ArrayList<ServletRequestCleanupCallback>();
            cleanups.set(list);
        }
        return list;
    }
    
    public static void setCleaner(Object cleaner)
    {
        if (cleaner == null)
        {
            throw new IllegalArgumentException("Cleaner may not be null");
        }
        if (firstCleaner.get() == null)
        {
            firstCleaner.set(cleaner);
        }
    }
        
    public static void addCleanupCallback(ServletRequestCleanupCallback callback)
    {
        if (firstCleaner.get() == null)
        {
            try
            {
                throw new RuntimeException();
            }
            catch (RuntimeException jre)
            {
                // log missing cleaner and stacktrace for addCleanupCallback call
                JetspeedLoggerUtil.getSharedLogger(ServletRequestCleanupService.class)
                    .error("No request cleaner set for ServletRequestCleanupService: cleanup callback ignored", jre);
                return;
            }
        }
        getCleanups(true).add(callback);
    }
    
    public static void cleanup(Object cleaner, ServletContext context, HttpServletRequest request, HttpServletResponse response)
    {
        if (cleaner != null && cleaner.equals(firstCleaner.get()))
        {
            List<ServletRequestCleanupCallback> list = getCleanups(false);
            if (list != null)
            {
                for (ServletRequestCleanupCallback callback : cleanups.get())
                {
                    try
                    {
                        callback.cleanup(context, request, response);
                    }
                    catch (Exception e)
                    {
                        JetspeedLoggerUtil.getSharedLogger(ServletRequestCleanupService.class)
                            .error("Request cleanup operation failed", e);
                    }
                }
                cleanups.remove();
            }
            firstCleaner.remove();
        }
    }
}
