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
package org.apache.jetspeed.container;

/**
 * Container Constants
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class ContainerConstants
{
    public final static String PORTLET_ACTION = "javax.portlet.action";
    public final static String PORTLET_REQUEST = "javax.portlet.request";
    public final static String PORTLET_RESPONSE = "javax.portlet.response";
    public final static String PORTLET_CONFIG = "javax.portlet.config";
    public final static String PORTAL_CONTEXT = "org.apache.jetspeed.context";
    public final static String METHOD_ID = "org.apache.jetspeed.method";
    public final static String PORTLET = "org.apache.jetspeed.portlet";
    public final static String PORTLET_NAME = "org.apache.jetspeed.portlet.name";
    public final static Integer METHOD_RENDER = new Integer(1);
    public final static Integer METHOD_ACTION = new Integer(3);
    public final static Integer METHOD_NOOP = new Integer(5);
    public final static Integer METHOD_RESOURCE = new Integer(6);
    public final static Integer METHOD_EVENT = new Integer(7);
    public final static Integer METHOD_ADMIN = new Integer(8);
    
}
