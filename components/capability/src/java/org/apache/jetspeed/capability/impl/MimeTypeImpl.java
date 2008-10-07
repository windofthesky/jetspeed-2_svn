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

package org.apache.jetspeed.capability.impl;

import org.apache.jetspeed.capability.MimeType;

/**
 * Mimetype implementation class.
 *
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */

public class MimeTypeImpl implements MimeType
{
    private int mimeTypeId;
    private String name;

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.MimeType#setMimetypeId(int)
     */
    public void setMimetypeId(int id)
    {
        this.mimeTypeId = id;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.MimeType#getMimetypeId()
     */
    public int getMimetypeId()
    {
        return this.mimeTypeId;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.MimeType#setName(java.lang.String)
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.om.registry.MimeType#getName()
     */
    public String getName()
    {
        return this.name;
    }

}
