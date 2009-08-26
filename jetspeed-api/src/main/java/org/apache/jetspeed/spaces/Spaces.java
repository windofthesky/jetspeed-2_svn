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
package org.apache.jetspeed.spaces;

import java.util.List;

import org.apache.jetspeed.om.page.Link;
import org.apache.jetspeed.om.page.Page;

/**
 * Spaces Services
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Spaces
{
    List<Environment> listEnvironments();
    Environment addEnvironment(Environment env);
    void removeEnvironment(Environment env);    
    List<Page> listPages(Space space);
    List<Link> listLinks(Space space);
    List<Space> listSpaces();    
    List<Space> listSpaces(Environment env);    
    Space addSpace(Environment env, Space space);
    void removeSpace(Environment env, Space space);
    Space addPage(Space space, Page page);
    void removePage(Space space, Page page);
}
