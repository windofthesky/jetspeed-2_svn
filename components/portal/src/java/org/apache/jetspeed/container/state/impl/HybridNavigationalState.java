package org.apache.jetspeed.container.state.impl;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

import org.apache.pluto.om.window.PortletWindow;

/**
 * HybridNavigationalState
 * 
 * Only encodes render parameters that start with a given prefix
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: AbstractNavigationalState.java 333093 2005-11-13 18:42:42Z taylor $
 */
public class HybridNavigationalState extends SessionNavigationalState
{
    protected String prefix;
    
    public HybridNavigationalState(NavigationalStateCodec codec, String prefix)
    {
        super(codec);
        this.prefix = prefix;
    }
    
    public String encode(PortletWindow window, Map parameters, PortletMode mode, WindowState state, boolean action)
    throws UnsupportedEncodingException
    {
        Map subset = new HashMap();
        Iterator params = parameters.keySet().iterator();
        while (params.hasNext())
        {
            String key = (String)params.next();
            if (key.startsWith(prefix))
            {
                // only encode params that start with prefix
                subset.put(key, parameters.get(key));
            }
        }
        return super.encode(window, subset, mode, state, action);
    }

    public boolean isNavigationalParameterStateFull()
    {
        return true;
    }

    public boolean isRenderParameterStateFull()
    {
        return false;
    }
    
    
}
