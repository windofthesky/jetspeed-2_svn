/*
 * Copyright 2004 The Apache Software Foundation.
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
package org.apache.jetspeed.om.folder.impl;

import org.apache.jetspeed.om.folder.DocumentSetPath;

/**
 * <p>
 * DocumentSetPathImpl
 * </p>
 * 
 * @author <a href="mailto:rwatler@finali.com">Randy Watler</a>
 * @version $Id$
 */
public class DocumentSetPathImpl implements DocumentSetPath
{
    private String path;
    private boolean regexp;
     
    public DocumentSetPathImpl()
    {
    }
       
    /**
     * <p>
     * getPath
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSetPath#getPath()
     * @return path
     */
    public String getPath()
    {
        return path;
    }
    /**
     * <p>
     * setPath
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSetPath#setPath(java.lang.String)
     * @param path
     */
    public void setPath( String path )
    {
        this.path = path;
    }

    /**
     * <p>
     * isRegexp
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSetPath#isRegexp()
     * @return regexp
     */
    public boolean isRegexp()
    {
        return regexp;
    }
    /**
     * <p>
     * setRegexp
     * </p>
     *
     * @see org.apache.jetspeed.om.folder.DocumentSetPath#setRegexp(boolean)
     * @param regexp
     */
    public void setRegexp( boolean regexp )
    {
        this.regexp = regexp;
    }

}
