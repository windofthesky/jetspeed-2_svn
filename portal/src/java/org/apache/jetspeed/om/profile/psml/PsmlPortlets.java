/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.jetspeed.om.profile.psml;

// Java imports
import java.util.Iterator;
import java.util.Vector;

import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.Security;

/**
 * Base simple bean-like implementation of the Portlets interface
 * suitable for Castor XML serialization.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PsmlPortlets 
    extends PsmlIdentityElement 
    implements Portlets, java.io.Serializable                                                
{
    private Controller controller = null;

    private Security security = null;

    private Vector portlets = new Vector();

    private Vector entries = new Vector();

    private Vector refs = new Vector();
    
    /** Holds value of property securityRef. */
    private SecurityReference securityRef = null;    

    private Portlets parentPortlets;  

    public PsmlPortlets()
    { }

    public Controller getController()
    {
        return this.controller;
    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    public void setSecurity(Security security)
    {
        this.security = security;
    }
 
    public Security getSecurity()
    {
        return this.security;
    }

    public Vector getEntries()
    {
        return this.entries;
    }

    public void setEntries(Vector entries)
    {
        this.entries = entries;
    }

    /**
     * Return a list of portlet.  Portlets that where added via a reference, see
     * addReference(), are excluded.
     *
     * @return Vector of portlet
     */    
    public Vector getPortlets()
    {
        Vector v = new Vector();
        for (int ix = 0; ix < this.portlets.size(); ix++)
        {
            Portlets p = (Portlets) this.portlets.get(ix);
            if (p instanceof Reference)
            {
                // Do not want to include portlets that where added via reference 
                continue;
            }
            v.add(p);
        }
        return v;
    }

    public void setPortlets(Vector portlets)
    {
        this.portlets = portlets;
    }

    public Vector getReferences()
    {
        return this.refs;
    }

    public void addPortlets(PsmlPortlets p)
    {
        portlets.addElement(p);
    } 

    public void addReference(PsmlReference ref) 
    {
       this.refs.addElement(ref);
       portlets.addElement(ref);
    }

    public void addReference(Reference ref)
        throws java.lang.IndexOutOfBoundsException
    {
        this.refs.addElement(ref);
        portlets.addElement(ref);
    }

    public int getEntryCount()
    {
        return this.entries.size();
    }

    public int getReferenceCount()
    {
        return this.refs.size();
    }
    
    public int getPortletsCount()
    {
        return this.portlets.size();
    }

    public Entry removeEntry(int index)
    {
        Object obj = entries.elementAt(index);
        entries.removeElementAt(index);
        return (Entry) obj;
    } 

    public Portlets removePortlets(int index)
    {
        Object obj = portlets.elementAt(index);
        if (null == obj)
        {
            return (Portlets) obj;
        }

        portlets.removeElementAt(index);
        if (obj instanceof Reference)
        {
            refs.remove(obj);                    
        }
        return (Portlets) obj;
    } 

    public Reference removeReference(int index)
    {
        Object obj = refs.elementAt(index);
        refs.removeElementAt(index);
        portlets.remove(obj);
        return (Reference) obj;
    }


    public Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > entries.size()))
        {
            throw new IndexOutOfBoundsException();
        }
        
        return (Entry) entries.elementAt(index);
    } 

    public Portlets getPortlets(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > portlets.size()))
        {
            throw new IndexOutOfBoundsException();
        }
        
        return (Portlets) portlets.elementAt(index);
    } 

    public Reference getReference(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        if ((index < 0) || (index > refs.size()))
        {
            throw new IndexOutOfBoundsException();
        }
        
        return (Reference) refs.elementAt(index);
    }

    public Iterator getEntriesIterator()
    {
        return entries.iterator();
    }

    public Iterator getPortletsIterator()
    {
        return portlets.iterator();
    }

    public Iterator getReferenceIterator()
    {
        return refs.iterator();
    }

    public void addEntry(Entry entry)
        throws java.lang.IndexOutOfBoundsException
    {
        entries.addElement(entry);
    } 

    public void addPortlets(Portlets p)
        throws java.lang.IndexOutOfBoundsException
    {
        portlets.addElement(p);
        // STW make sure layout gets set
        int end = getEntryCount();
        Layout layout = p.getLayout();
        if(layout != null)
        {
            layout.setPosition(end);
            layout.setSize(-1);
        }
    } 


    public Entry[] getEntriesArray()
    {
        int size = entries.size();
        Entry[] mArray = new Entry[size];
        for (int index = 0; index < size; index++)
        {
            mArray[index] = (Entry) entries.elementAt(index);
        }
        return mArray;
    }

    public Portlets[] getPortletsArray()
    {
        int size = portlets.size();
        Portlets[] mArray = new Portlets[size];
        for (int index = 0; index < size; index++)
        {
            mArray[index] = (Portlets) portlets.elementAt(index);
        }
        return mArray;
    }

    public Reference[] getReferenceArray()
    {
        int size = refs.size();
        Reference[] mArray = new Reference[size];
        for (int index = 0; index < size; index++)
        {
            mArray[index] = (Reference) refs.elementAt(index);
        }
        return mArray;
    }

    /** Getter for property securityRef.
     * @return Value of property securityRef.
     */
    public SecurityReference getSecurityRef()
    {
        return securityRef;
    }    

    /** Setter for property securityRef.
     * @param securityRef New value of property securityRef.
     */
    public void setSecurityRef(SecurityReference securityRef)
    {
        this.securityRef = securityRef;
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();

        ((PsmlPortlets)cloned).controller = ((this.controller == null) ? null : (Controller) this.controller.clone());
        ((PsmlPortlets)cloned).security = ((this.security == null) ? null : (Security) this.security.clone());

        if (this.portlets != null)
        {
            ((PsmlPortlets)cloned).portlets = new Vector(this.portlets.size());
            Iterator it = this.portlets.iterator();
            while (it.hasNext())
            {
                ((PsmlPortlets)cloned).portlets.add(((Portlets)it.next()).clone());
            }
        }

        if (this.entries != null)
        {
            ((PsmlPortlets)cloned).entries = new Vector(this.entries.size());
            Iterator it = this.entries.iterator();
            while (it.hasNext())
            {
                ((PsmlPortlets)cloned).entries.add(((Entry)it.next()).clone());
            }
        }

        if (this.refs != null)
        {
            ((PsmlPortlets)cloned).refs = new Vector(this.refs.size());
            Iterator it = this.refs.iterator();
            while (it.hasNext())
            {
                ((PsmlPortlets)cloned).refs.add(((Reference)it.next()).clone());
            }
        }

        ((PsmlPortlets)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());

        return cloned;

    }   // clone

    /**
     * Returns the parent.
     * @return Portlets
     */
    public Portlets getParentPortlets()
    {
        return parentPortlets;
    }

    /**
     * Sets the parent.
     * @param parent The parent to set
     */
    public void setParentPortlets(Portlets parent)
    {
        this.parentPortlets = parent;
    }

    /**
     * @see org.apache.jetspeed.om.profile.IdentityElement#getSkin()
     */
//    public Skin getSkin()
//    {
//       Skin useSkin = super.getSkin();
//        if(useSkin == null && parentPortlets != null)
//        {
//            useSkin = parentPortlets.getSkin();
//        }
//        
//       return useSkin;
//    }
 
}