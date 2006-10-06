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
package org.apache.jetspeed.layout.impl;

/**
 * PortletPlacement implementation constants 
 *
 * @author <a>David Gurney</a>
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public interface Constants 
{
	public static final String STATUS = "status";
	public static final String ACTION = "action";
    public static final String REASON = "reason";
	public static final String PORTLETID = "id";
    public static final String PORTLETENTITY = "entity";
    public static final String LAYOUTID = "layoutid";
    public static final String WINDOW_STATE = "state";
    public static final String PORTLET_MODE = "mode";
    public static final String OLD_WINDOW_STATE = "oldState";
    public static final String OLD_PORTLET_MODE = "oldMode";    
	public static final String OLDCOL = "oldcol";
	public static final String OLDROW = "oldrow";
	public static final String NEWCOL = "newcol";
	public static final String NEWROW = "newrow";
	public static final String COL = "col";
	public static final String ROW = "row";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String WIDTH = "width";
    public static final String HEIGHT = "height";
    public static final String OLD_X = "oldx";
    public static final String OLD_Y = "oldy";
    public static final String OLD_Z = "oldz";
    public static final String OLD_WIDTH = "oldwidth";
    public static final String OLD_HEIGHT = "oldheight";
    
    public static final String FILTER = "filter";
    public static final String PORTLETS = "portlets";
    public static final String PAGES = "pages";
    public static final String PAGE = "page";
    public static final String FOLDER = "folder";
    public static final String SIZES = "sizes";

    public static final String STANDARD_MENUS = "standardMenus";
    public static final String CUSTOM_MENUS = "customMenus";
    public static final String MENU = "menu";
    public static final String MENU_NAME = "name";
    public static final String MENU_CONTEXT = "menuContext";
    public static final String MENU_LOCALE = "menuLocale";

    public static final String PAGE_DECORATIONS = "pageDecorations";
    public static final String PORTLET_DECORATIONS = "portletDecorations";
    public static final String LAYOUTS = "layouts";
    public static final String DESKTOP_THEMES = "desktopThemes";
    public static final String LINK = "link";
    public static final String FRAGMENTS = "fragments";
    public static final String TYPE = "type";
    public static final String FORMAT = "format";
    
	// Move types
	public static final int ABS = 1;
	public static final int UP = 2;
	public static final int DOWN = 3;
	public static final int LEFT = 4;
	public static final int RIGHT = 5;
	public static final int CARTESIAN = 6;
}
