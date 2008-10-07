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

/**
 * Rule
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface Rule extends Identified
{
    /**
     * Flag indicating whether to use the Base URL for this rewriter.
     * The default setting is true, use the rewriter's Base URL.
     * 
     * @return true if this rule uses the Base URL
     */
    boolean getUseBase();
    
    /**
     * Flag indicating whether to use the Base URL for this rewriter.
     * The default setting is true, use the rewriter's Base URL.
     * 
     * @param true if this rule uses the Base URL
     */    
    void setUseBase(boolean flag);
    
    /**
     * Suffix string to append to the rewritten URL.
     * 
     * @return the value of the suffix string.
     */
    String getSuffix();
    
    /**
     * Suffix string to append to the rewritten URL.
     * 
     * @param the value of the suffix string.
     */    
    void setSuffix(String suffix);
    
    /**
     * Flag indicating whether to rewrite links as popups.
     * The default setting is false, do not rewrite as a popup.
     * 
     * @return true if this rule rewrites links as popups
     */
    boolean getPopup();

    /**
     * Flag indicating whether to rewrite links as popups.
     * The default setting is false, do not rewrite as a popup.
     * 
     * @param true if this rule rewrites links as popups
     */    
    void setPopup(boolean flag);
    
    /**
     * Checks to see if a URL should be rewritten or not.
     * 
     * @param url
     */    
    boolean shouldRewrite(String url);
    
    
                        
}
