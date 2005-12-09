/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.util.List;

/**
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public interface Decoration
{
    /** Default style sheet location */
    String DEFAULT_STYLE_SHEET = "css/styles.css";
    
    /** Decoration configruation filename */
    String CONFIG_FILE_NAME = "decorator.properties";

    public static final String BASE_CSS_CLASS_PROP = "base.css.class";
    
    /**
     * The name of this Decoration.
     *  
     * @return Name of this decoration.
     */
    String getName();
    
    /**
     * <p>
     * Returns the correct path to the resource based on the
     * relative <code>path</code> argument.  This usually entails
     * locating the resource that is most appropriate for the
     * current users client and locale.
     * </p>
     * <pre>
     * Example Criterion:
     * 
     * Relative Path: images/myimage.gif
     * Client:        web browser
     * Language:      en 
     * Country:       US 
     * 
     * </pre>
     * 
     * <p>
     * The implementation should now attempt to resolve the resource using
     * logic that starts at the most specific and ends at the most general 
     * path.
     * </p>
     * 
     * <p>
     * For exmaples sake, lets say we are concerned with finding the image,
     * myimage.gif, within the layout decoration, tigris.  The logical progression
     * to find the resourc, myimage.gif, would be as follows:
     * </p>
     * 
     * <pre> 
     * /decorations/layout/tigris/html/en/US/images/myimage.gif
     * /decorations/layout/tigris/html/en/images/myimage.gif
     * /decorations/layout/tigris/html/images/myimage.gif
     * /decorations/layout/tigris/images/myimage.gif
     * /decorations/layout/images/myimage.gif
     * /decorations/layout/images/myimage.gif
     * </pre>
     * 
     * @param path
     * @return
     */
    String getResource(String path);
    
    /**
     * 
     * @return The appropriate stylesheet to be used with this 
     * decoration.
     */
    String getStyleSheet();    
    
    /**
     * Returns the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     * 
     * @see org.apache.jetspeed.velocity.DecoratorAction
     * 
     * @return the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     */
    List getActions();
    
    /**
     * Set the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     * @see org.apache.jetspeed.velocity.DecoratorAction
     * 
     * @param actions actions to displayed within this portlet window. 
     */
    void setActions(List actions);
    
    /**
     * Allows access to abritrary properties configured
     * within your <code>decorator.properties</code> config
     * file.
     * @param name
     * @return
     */
    String getProperty(String name);
    
    /**
     * Returns the base CSS class the template should use to
     * create a proper CSS cascade and style isolation for a 
     * decoration.
     * 
     * @return the base CSS class the template should use.
     */
    String getBaseCSSClass();
}
