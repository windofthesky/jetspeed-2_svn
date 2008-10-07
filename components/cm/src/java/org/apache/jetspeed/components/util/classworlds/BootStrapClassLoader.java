/*
 * Created on Apr 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util.classworlds;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class BootStrapClassLoader extends ClassLoader
{
    private ClassLoader cl;
    private ThreadLocal alreadyRequested;

    public BootStrapClassLoader(ClassLoader cl)
    {
        this.cl = cl;
        this.alreadyRequested = new ThreadLocal();
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getSystemClassLoader()
    {
        return ClassLoader.getSystemClassLoader();
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getSystemResource(java.lang.String)
     */
    public static URL getSystemResource( String name )
    {
        return ClassLoader.getSystemResource(name);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getSystemResourceAsStream(java.lang.String)
     */
    public static InputStream getSystemResourceAsStream( String name )
    {
        return ClassLoader.getSystemResourceAsStream(name);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getSystemResources(java.lang.String)
     */
    public static Enumeration getSystemResources( String name )
            throws IOException
    {
        return ClassLoader.getSystemResources(name);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#clearAssertionStatus()
     */
    public void clearAssertionStatus()
    {
        cl.clearAssertionStatus();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return cl.equals(obj);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getResource(java.lang.String)
     */
    public URL getResource( String name )
    {
        return cl.getResource(name);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#getResourceAsStream(java.lang.String)
     */
    public InputStream getResourceAsStream( String name )
    {
        return cl.getResourceAsStream(name);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return cl.hashCode();
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#loadClass(java.lang.String)
     */
    public Class loadClass( String name ) throws ClassNotFoundException
    {
        Set alreadyRequestedSet = (Set) alreadyRequested.get();
        try
        {
            if(alreadyRequestedSet == null || !alreadyRequestedSet.contains(name))
            {
                if(alreadyRequestedSet == null)
                {
                    alreadyRequestedSet = new HashSet();
                }
                alreadyRequestedSet.add(name);
                alreadyRequested.set(alreadyRequestedSet);
                return cl.loadClass(name);
            }
            else
            {
                throw new ClassNotFoundException("Failed to locate class: "+name);
            }
        }
        finally 
        {
           alreadyRequested.set(null);
        }
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#setClassAssertionStatus(java.lang.String, boolean)
     */
    public void setClassAssertionStatus( String className, boolean enabled )
    {
        cl.setClassAssertionStatus(className, enabled);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#setDefaultAssertionStatus(boolean)
     */
    public void setDefaultAssertionStatus( boolean enabled )
    {
        cl.setDefaultAssertionStatus(enabled);
    }
    /* (non-Javadoc)
     * @see java.lang.ClassLoader#setPackageAssertionStatus(java.lang.String, boolean)
     */
    public void setPackageAssertionStatus( String packageName, boolean enabled )
    {
        cl.setPackageAssertionStatus(packageName, enabled);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return cl.toString();
    }
}
