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
package org.apache.jetspeed.contentserver;

import java.io.OutputStream;
import java.util.List;


/**
 * <p>
 * ContentLocator
 * </p>
 *
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public interface ContentLocator
{
    /**
     * 
     * <p>
     *  mergeContent
     * </p>
     * <p>
     *  Merges the content that is located in the provided <code>URI</code>     * 
     * </p>
     * @param URI Content to locate
     * @param os OutputStream to write the content to.
     * @return int the length of actual content in bytes or -1
     * if the <code>URI</code> was not found.
     */
    long mergeContent(String URI, List lookupPathes, OutputStream os);
}
