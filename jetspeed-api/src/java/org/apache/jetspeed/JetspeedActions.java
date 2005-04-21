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
package org.apache.jetspeed;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * Jestpeed Action Declarations
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public interface JetspeedActions
{
    public static final int INDEX_MINIMIZE = 0;    
    public static final int INDEX_MAXIMIZE = 1;
    public static final int INDEX_NORMAL = 2;
    public static final int INDEX_RESTORE = 2;    
    public static final int INDEX_VIEW = 3;
    public static final int INDEX_EDIT = 4;
    public static final int INDEX_HELP = 5;
    public static final int INDEX_SECURE = 6;

    public static final int MASK_MINIMIZE = 0x01;    
    public static final int MASK_MAXIMIZE = 0x02;
    public static final int MASK_NORMAL = 0x04;
    public static final int MASK_RESTORE = 0x04;    
    public static final int MASK_VIEW = 0x08;
    public static final int MASK_EDIT = 0x10;
    public static final int MASK_HELP = 0x20;
    public static final int MASK_SECURE = 0x40;
    
    public final WindowState RESTORED = new WindowState("restore");
    public final WindowState SECURED  = new WindowState("secure");
    
    static public final String VIEW = PortletMode.VIEW.toString();
    static public final String EDIT = PortletMode.EDIT.toString();
    static public final String HELP = PortletMode.HELP.toString();
    static public final String RESTORE = RESTORED.toString();
    static public final String NORMAL = WindowState.NORMAL.toString();
    static public final String MINIMIZE = WindowState.MINIMIZED.toString();
    static public final String MAXIMIZE = WindowState.MAXIMIZED.toString();
    static public final String SECURE = SECURED.toString();
    
    public static final String ACTIONS[] =
    {
            MINIMIZE, MAXIMIZE, RESTORE, VIEW, EDIT, HELP, SECURE
    };    
}
