/*
 * Created on Oct 20, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.cache.general;

/**
 * <p>
 * InvocationCountingCache
 * </p>
 * <p>
 *
 * </p>
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id$
 *
 */
public class InvocationCountingCache extends SimpleHashMapCache
{
    int getCount, putCount, removeCount, successGetCount, containsCount;
   

    public Object get( String key )
    {
        getCount++;
        
        Object value =  super.get(key);
        if(value != null)
        {
            successGetCount++;
        }
        
        return value;
    }
    
    public void put( String key, Object value )
    {
        putCount++;
        super.put(key, value);
    }
    
    public Object remove( String key )
    {
        removeCount++;
        return super.remove(key);
    }
    
    public boolean contains( String key )
    {
        containsCount++;
        return super.contains(key);
    }
}
