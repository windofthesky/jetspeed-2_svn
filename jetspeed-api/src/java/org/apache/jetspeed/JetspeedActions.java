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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;


/**
 * Jestpeed Action Declarations
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class JetspeedActions
{
  public static final PortletMode PRINT_MODE = new PortletMode("print");
  public static final WindowState SOLO_STATE = new WindowState("solo");
  
    public static final int MASK_MINIMIZE = 0x01;    
    public static final int MASK_MAXIMIZE = 0x02;
    public static final int MASK_NORMAL = 0x04;
    public static final int MASK_VIEW = 0x08;
    public static final int MASK_EDIT = 0x10;
    public static final int MASK_HELP = 0x20;
    
    static public final String VIEW = PortletMode.VIEW.toString();
    static public final String EDIT = PortletMode.EDIT.toString();
    static public final String HELP = PortletMode.HELP.toString();
    static public final String PRINT = PRINT_MODE.toString();
    static public final String NORMAL = WindowState.NORMAL.toString();
    static public final String MINIMIZE = WindowState.MINIMIZED.toString();
    static public final String MAXIMIZE = WindowState.MAXIMIZED.toString();
    static public final String SOLO = SOLO_STATE.toString();
    
    private static final List standardPortletModes;
    private static final List standardWindowStates;
   
    static
    {
        ArrayList list = new ArrayList(3);
        list.add(PortletMode.VIEW);
        list.add(PortletMode.EDIT);
        list.add(PortletMode.HELP);
        standardPortletModes = Collections.unmodifiableList(list);
        list = new ArrayList(3);
        list.add(WindowState.NORMAL);
        list.add(WindowState.MINIMIZED);
        list.add(WindowState.MAXIMIZED);
        standardWindowStates = Collections.unmodifiableList(list);
    }

    private static JetspeedActions instance = new JetspeedActions(new String[]{}, new String[]{});
        
    private final List extendedPortletModes;
    private final List extendedWindowStates;
    private final Map actionsMap;
    private final Object[] actions;
    
    public static List getStandardPortletModes()
    {
        return standardPortletModes;
    }
    
    public static List getStandardWindowStates()
    {
        return standardWindowStates;
    }
    
    public JetspeedActions(String[] supportedPortletModes, String[] supportedWindowStates)
    {
        int index = 0;
        
        ArrayList actionsList = new ArrayList();
        
        actionsMap = new HashMap();
        
        actionsMap.put(WindowState.MINIMIZED.toString(),new Integer(index++));
        actionsList.add(WindowState.MINIMIZED);
        actionsMap.put(WindowState.MAXIMIZED.toString(),new Integer(index++));
        actionsList.add(WindowState.MAXIMIZED);
        actionsMap.put(WindowState.NORMAL.toString(),new Integer(index++));
        actionsList.add(WindowState.NORMAL);
        actionsMap.put(PortletMode.VIEW.toString(), new Integer(index++));
        actionsList.add(PortletMode.VIEW);
        actionsMap.put(PortletMode.EDIT.toString(),new Integer(index++));
        actionsList.add(PortletMode.EDIT);
        actionsMap.put(PortletMode.HELP.toString(),new Integer(index++));
        actionsList.add(PortletMode.HELP);
        
        ArrayList list = new ArrayList();
        
        for (int i=0; index < 32 && i<supportedWindowStates.length; i++) 
        {
            WindowState state = new WindowState(supportedWindowStates[i]);
            if ( !actionsMap.containsKey(state.toString()) )
            {
                actionsMap.put(state.toString(), new Integer(index++));
                actionsList.add(state);
                list.add(state);
            }
            else if (!standardWindowStates.contains(state))
            {
                throw new IllegalArgumentException("WindowState "+state+" already defined as extended PortletMode or WindowState");
            }
        }
        extendedWindowStates = Collections.unmodifiableList(list);
        
        list = new ArrayList();
        
        for (int i=0; index < 32 && i<supportedPortletModes.length; i++) 
        {
            PortletMode mode = new PortletMode(supportedPortletModes[i]);
            if ( !actionsMap.containsKey(mode.toString()) )
            {
                actionsMap.put(mode.toString(), new Integer(index++));
                actionsList.add(mode);
                list.add(mode);
            }
            else if (!standardPortletModes.contains(mode))
            {
                throw new IllegalArgumentException("PortletMode "+mode+" already defined as extended PortletMode or WindowState");
            }
        }
        extendedPortletModes = Collections.unmodifiableList(list);
        
        actions = actionsList.toArray();
        
        instance = this;
    }

    public static List getExtendedPortletModes()
    {
        return instance.extendedPortletModes;
    }
    
    public static List getExtendedWindowStates()
    {
        return instance.extendedWindowStates;
    }
    
    public static int getContainerActionMask(String action)
    {
        Integer index = (Integer)instance.actionsMap.get(action);
        if ( index == null )
        {
            throw new IllegalArgumentException("Unknown action: "+action);
        }
        return 1<<index.intValue();
    }
    
    public static String getContainerAction(int index)
    {
        JetspeedActions ja = JetspeedActions.instance;
        return index > -1 && index < ja.actions.length ? ja.actions[index].toString() : null;
    }
    
    public static String getContainerActions(int mask)
    {
        JetspeedActions ja = JetspeedActions.instance;
        StringBuffer buffer = new StringBuffer();
        boolean append = false;
        
        for ( int i = 0, j=1<<i; i < ja.actions.length; i++, j=1<<i )
        {
            if ( (mask & j) == j )
            {
                if ( append )
                    buffer.append(", ");
                else
                    append = true;
                buffer.append(ja.actions[i].toString());
            }
        }
        return buffer.toString();
    }
    
    public static int getContainerActionsMask(String actions)
    {
        int mask = 0;
        
        if ( actions != null )
        {
            JetspeedActions ja = JetspeedActions.instance;
            
            StringTokenizer tokenizer = new StringTokenizer(actions, ",\t ");

            Integer index;
            while (tokenizer.hasMoreTokens())
            {
                String action = tokenizer.nextToken();
                index = (Integer)ja.actionsMap.get(action);
                if ( index == null )
                    throw new IllegalArgumentException("Unknown action: " + action);
                mask |= (1 << index.intValue());
            }
        }        
        return mask;
    }
}
