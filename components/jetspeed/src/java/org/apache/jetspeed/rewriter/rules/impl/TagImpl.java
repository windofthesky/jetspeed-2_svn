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
package org.apache.jetspeed.rewriter.rules.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.jetspeed.rewriter.rules.Tag;

/**
 * Tag
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class TagImpl extends IdentifiedImpl implements Tag
{
    private boolean remove = false;
    private boolean strip = false;
    private Collection attributes = new ArrayList();    

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Ruleset#setId(java.lang.String)
     */
    public void setId(String id)
    {
        if (id != null)
        {
            this.id  = id.toUpperCase();
        }
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Tag#getRemove()
     */
    public boolean getRemove()
    {
        return remove;
    }

    /* (non-Javadoc)
     * @see org.apache.jetspeed.cps.rewriter.rules.Tag#setRemove(boolean)
     */
    public void setRemove(boolean b)
    {
        remove = b;
    }

    public String toString()
    {
        return id;
    }

    /**
     * Castor setter to set attributes for a Tag.
     * 
     * @param attributes
     */
    public void setAttributes(Collection attributes)
    {
        this.attributes = attributes;
    }

    /**
     * Castor getter to get attributes for a Tag.
     * 
     * @param attributes
     */
    public Collection getAttributes()
    {
        return this.attributes;
    }


    /**
     * @return
     */
    public boolean getStrip()
    {
        return strip;
    }

    /**
     * @param b
     */
    public void setStrip(boolean b)
    {
        strip = b;
    }

}
