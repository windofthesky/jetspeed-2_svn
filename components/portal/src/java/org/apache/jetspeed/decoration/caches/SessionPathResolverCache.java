package org.apache.jetspeed.decoration.caches;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.jetspeed.PortalReservedParameters;
import org.apache.jetspeed.decoration.PathResolverCache;

public class SessionPathResolverCache extends HashMapPathResolverCache implements PathResolverCache
{
    private final HttpSession session;    
    
    public SessionPathResolverCache(HttpSession session)
    {
        this.session = session;
        cache = (Map) session.getAttribute(PortalReservedParameters.RESOVLER_CACHE_ATTR);
        
        if(cache == null)
        {
            cache = new HashMap();
            session.setAttribute(PortalReservedParameters.RESOVLER_CACHE_ATTR, cache);
        }
    }
}
