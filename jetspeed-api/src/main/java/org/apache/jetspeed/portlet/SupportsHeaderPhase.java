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
package org.apache.jetspeed.portlet;

import javax.portlet.PortletException;

/**
 * Indicates that a portlet supports the pre-286 header phase.
 * <P>
 * This interface is deprecated because head contribution from portlet is possible
 * by the native portlet API in a JSR-286 compliant portal.
 * </P>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 * @deprecated
 */
public interface SupportsHeaderPhase
{
    void doHeader(PortletHeaderRequest request, PortletHeaderResponse response) throws PortletException;    
}
