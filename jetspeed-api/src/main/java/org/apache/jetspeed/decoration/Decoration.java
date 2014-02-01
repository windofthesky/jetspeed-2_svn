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
package org.apache.jetspeed.decoration;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public interface Decoration
{
    /** Default style sheet location */
    String DEFAULT_COMMON_STYLE_SHEET = "css/styles.css";
    String DEFAULT_PORTAL_STYLE_SHEET = "css/portal.css";
    String DEFAULT_DESKTOP_STYLE_SHEET = "css/desktop.css";
    
    /** Decoration configuration filename */
    String CONFIG_FILE_NAME = "decorator.properties";
    
    /** Decoration desktop configuration filename */
    String CONFIG_DESKTOP_FILE_NAME = "decoratordesktop.properties";
    
    /** Property which indicates whether or not decoration supports desktop mode */
    String DESKTOP_SUPPORTED_PROPERTY = "desktop.supported";

    /**
     * Property for specifying the base CSS class to be used to
     * create a proper CSS cascade and style isolation for a decoration.
     */
    String BASE_CSS_CLASS_PROP = "base.css.class";

    /** Property which specifies the resource bundle locator prefix */
    String RESOURCE_BUNDLE_PROP = "resource.file";
    
    /** Property which specifies the directory name for resource bundle */
    String RESOURCES_DIRECTORY_NAME = "resources";

    String OPTION_TITLE = "option.title";
    String OPTION_ACTIONS = "option.actions";
    String OPTION_DRAGHANDLE = "option.draghandle";
    
    enum ActionsOption 
    {
        SHOW, HIDE, DROPDOWN, HOVER
    };
    enum TitleOption 
    {
        SHOW, HIDE
    };

    ActionsOption getActionsOption();
    TitleOption getTitleOption();
    String getDragHandle(); // returns null for not supported
    
    /**
     * The name of this Decoration.
     *  
     * @return Name of this decoration.
     */
    String getName();
    
    /**
     * <p>
     * Returns the base path for the decoration.
     * </p>
     * 
     * @return the base path for the decoration.
     */
    String getBasePath();
    
    /**
     * <p>
     * Returns the base path for the decoration
     * with the relativePath argument added.
     * </p>
     * 
     * @param relativePath
     * @return the base path for the decoration with the relativePath argument added.
     */
    String getBasePath( String relativePath );
    
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
     * @return the correct path to the resource based on the
     * relative <code>path</code> argument.
     */
    String getResource(String path);
    
    /**
     * 
     * @return The appropriate stylesheet to be used with this 
     * decoration.
     */
    String getStyleSheet();
    
    /**
     * 
     * @return the /portal specific stylesheet to be used with this 
     * decoration; defined only when decoration supports /desktop.
     */
    String getStyleSheetPortal();
    
    /**
     * 
     * @return the /desktop specific stylesheet to be used with this 
     * decoration; defined only when decoration supports /desktop.
     */
    String getStyleSheetDesktop();
            
    /**
     * Returns the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     * 
     * @see org.apache.jetspeed.decoration.DecoratorAction
     * 
     * @return the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     */
    List<DecoratorAction> getActions();
    
    /**
     * Set the list of <code>DecoratorAction</code>s to be displayed
     * within the portlet window.
     * @see org.apache.jetspeed.decoration.DecoratorAction
     * 
     * @param actions actions to displayed within this portlet window. 
     */
    void setActions(List<DecoratorAction> actions);
    
    /**
     * Allows access to abritrary properties configured
     * within your <code>decorator.properties</code> config
     * file.
     * @param name
     * @return value of decoration property which matches name argument.
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

    /**
     * Returns the name of the currently active mode action
     * 
     * @return the name of the currently active mode action
     */
    String getCurrentModeAction();

    /**
     * Set the name of the currently active mode action
     * 
     */
    void setCurrentModeAction( String currentModeAction );
    
    /**
     * Returns the name of the currently active state action
     * 
     * @return the name of the currently active state action
     */
    String getCurrentStateAction();
    
    /**
     * Set the name of the currently active state action
     * 
     */
    void setCurrentStateAction( String currentStateAction );
    
    /**
     * @return the resource bundle locator prefix.
     */
    String getResourceBundleName();
    
    /**
     * @return the resource bundle for the given Locale and RequestContext.
     */
    ResourceBundle getResourceBundle( Locale locale, org.apache.jetspeed.request.RequestContext context );

    /**
     * Indicates whether the decorator supports /desktop
     * 
     */
    boolean supportsDesktop();
}
