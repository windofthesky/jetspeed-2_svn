/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.page.document;

import java.util.Iterator;

/**
 * <p>
 * NodeSet
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface NodeSet
{

    void add(Node node);

    /**
     * <p>
     * getNode
     * </p>     
     * Returns a Node based on <code>name</code>. <code>name</code>
     * can either be the fully quallified path, <code>folder1/folder2/myPage.psml</code>
     * as returned by Node.getPath(), or the page name relative the <code>Node.getParent().getPath()</code>
     * as return by Node.getName()that this DocumentSet was generated for.
     * 
     * @param name
     * @return
     */
    Node get(String name);

    Iterator iterator();
    
    NodeSet subset(String type);

    int size();

}
