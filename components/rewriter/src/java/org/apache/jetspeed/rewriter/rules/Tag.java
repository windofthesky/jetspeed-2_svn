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
package org.apache.jetspeed.rewriter.rules;

import java.util.Collection;

/**
 * Tag
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Tag extends Identified
{
    /**
     * Get a collection of attributes for the given Tag.
     * 
     * @return A collection of attributes.
     */
    Collection getAttributes();
    
    
    /**
     * Represents whether this tag is to be removed during rewrite phase.
     * Removing a tag only removes the tag but not the contents in 
     * between the start and end tag.
     * 
     * @return true if this tag should be removed
     */
    public boolean getRemove();

    /**
     * Represents whether this tag is to be removed during rewrite phase.
     * Removing a tag only removes the tag but not the contents in 
     * between the start and end tag.
     * 
     * @param flag true if this tag should be removed
     */    
    public void setRemove(boolean flag);

    /**
     * Represents whether this tag is to be removed during rewrite phase.
     * Stripping tags removes the start and end tag, plus all tags
     * and content in between the start and end tag.
     * 
     * @return true if this tag should be stripped.
     */
    public boolean getStrip();

    /**
     * Represents whether this tag is to be removed during rewrite phase.
     * Stripping tags removes the start and end tag, plus all tags
     * and content in between the start and end tag.
     * 
     * @param flag true if this tag should be stripped.
     */    
    public void setStrip(boolean flag);
    
}
