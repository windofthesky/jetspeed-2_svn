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
package org.apache.jetspeed.om.folder;

import java.util.Iterator;

/**
 * <p>
 * FolderSet
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface FolderSet
{
    /**
     * 
     * <p>
     * get
     * </p>
     * Returns a folder based on <code>folderName</code>. <code>folderName</code>
     * can either be the fully quallified path, <code>folder1/folder2/folder3</code>
     * or the folder name relative the <code>Folder</code> that this FolderSet
     * was generated for.
     *
     * @param folderName
     * @return
     */
    Folder get(String folderName);
    
    void add(Folder folder);
    
    Iterator iterator();
    
    int size();

}
