/*
 * Created on Apr 30, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.apache.jetspeed.components.util.classworlds;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.classworlds.NoSuchRealmException;

/**
 * @author <a href="mailto:sweaver@einnovation.com">Scott T. Weaver</a>
 *
 */
public class BootStrapClassRealm implements ClassRealm
{
    
    private ClassRealm wrappedRealm;
    private ThreadLocal alreadyRequested;
    private BootStrapClassLoader bscl;
    
    public BootStrapClassRealm(ClassRealm realm)
    {
        this.wrappedRealm = realm;
        alreadyRequested = new ThreadLocal();
        bscl = new BootStrapClassLoader(this.wrappedRealm.getClassLoader());
    }

    /**
     * @param name
     * @param b
     * @throws java.lang.ClassNotFoundException
     */
    public void addConstituent( String name, byte[] b )
            throws ClassNotFoundException
    {
        wrappedRealm.addConstituent(name, b);
    }
    /**
     * @param constituent
     */
    public void addConstituent( URL constituent )
    {
        wrappedRealm.addConstituent(constituent);
    }
    /**
     * @param id
     * @return
     */
    public ClassRealm createChildRealm( String id )
    {
        return wrappedRealm.createChildRealm(id);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj )
    {
        return wrappedRealm.equals(obj);
    }
    /**
     * @param name
     * @return
     * @throws java.io.IOException
     */
    public Enumeration findResources( String name ) throws IOException
    {
        return wrappedRealm.findResources(name);
    }
    /**
     * @return
     */
    public ClassLoader getClassLoader()
    {
        return bscl;
    }
    /**
     * @return
     */
    public String getId()
    {
        return wrappedRealm.getId();
    }
    /**
     * @param name
     * @return
     */
    public URL getResource( String name )
    {
        return wrappedRealm.getResource(name);
    }
    /**
     * @param name
     * @return
     * @throws java.io.IOException
     */
    public Enumeration getResources( String name ) throws IOException
    {
        return wrappedRealm.getResources(name);
    }
    /**
     * @return
     */
    public ClassWorld getWorld()
    {
        return wrappedRealm.getWorld();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return wrappedRealm.hashCode();
    }
    /**
     * @param realmId
     * @param pkgName
     * @throws org.codehaus.classworlds.NoSuchRealmException
     */
    public void importFrom( String realmId, String pkgName )
            throws NoSuchRealmException
    {
        wrappedRealm.importFrom(realmId, pkgName);
    }
    /**
     * @param name
     * @return
     * @throws java.lang.ClassNotFoundException
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
                return wrappedRealm.loadClass(name);
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
    /**
     * 
     */
    public void reload()
    {
        wrappedRealm.reload();
    }
    /**
     * @param reloadParent
     */
    public void reload( boolean reloadParent )
    {
        wrappedRealm.reload(reloadParent);
    }
    /**
     * @param classRealm
     */
    public void setParent( ClassRealm classRealm )
    {
        wrappedRealm.setParent(classRealm);
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return wrappedRealm.toString();
    }
}
