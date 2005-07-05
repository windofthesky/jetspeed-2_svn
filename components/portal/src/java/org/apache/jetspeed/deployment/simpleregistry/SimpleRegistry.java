/**
 * Created on Jan 13, 2004
 *
 * 
 * @author
 */
package org.apache.jetspeed.deployment.simpleregistry;

import java.util.Collection;

/**
 * <p>
 * SimpleRegistry
 * </p>
 * <p>
 *   This is an interface for creating simple registry systems.  A good example would be an
 *   in memory registry that gets populate at runtime and is lost on shutdown.
 * 
 * @author <a href="mailto:weaver@apache.org">Scott T. Weaver</a>
 * @version $Id: SimpleRegistry.java 186206 2004-03-25 21:42:32Z jford $
 *
 */
public interface SimpleRegistry
{
	/**
	 * Registers the entry.
	 * 
	 * @throws java.lang.IllegalAgrumentException in <code>entry</code> is null or
	 * <code>entry.getId()</code> is null
	 * @throws org.apache.jetspeed.cps.simpleregistry if this <code>entry</code> is 
	 * already registered.
	 * @param entry
	 */
	public void register(Entry entry) throws SimpleRegistryException;
	
	/**
	 * De-registers the entry
	 * @param entry
	 * @throws java.lang.IllegalAgrumentException in <code>entry</code> is null or
	 * <code>entry.getId()</code> is null
	 */
	public void deRegister(Entry entry);
	
	/**
	 * Verifies whether or not this entry is registered.
	 * @param entry
	 * 
	 * @return boolean <code>true</code> is the <code>entry</code> is registered
	 * otherwise <code>false</code>.
	 * @throws java.lang.IllegalAgrumentException in <code>entry</code> is null or
	 * <code>entry.getId()</code> is null
	 */
	public boolean isRegistered(Entry entry);
	
	/**
	 * Provides a Cloolection of <code>org.apache.jetspeed.cps.simpleregistry.Entry</code>
	 * objects that are currently registered to this registery
	 * @return
	 */	
	public Collection getRegistry();

}
