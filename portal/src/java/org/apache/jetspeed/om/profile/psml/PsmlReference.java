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
import java.util.Vector;
import java.util.Iterator;

// Jetspeed imports
import org.apache.jetspeed.om.SecurityReference;
import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.Entry;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Parameter;
import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Reference;
import org.apache.jetspeed.om.profile.Security;

/**
 * Base simple bean-like implementation of the Portlets interface
 * suitable for Castor XML serialization.
 *
 * sure wish I could figure out how to use Proxies with Castor...
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PsmlReference extends PsmlPortlets implements Reference, java.io.Serializable
{
    protected String path;

    protected PsmlPortlets ref = new PsmlPortlets();

    /** Holds value of property securityRef. */
    private SecurityReference securityRef;
    
    public Portlets getPortletsReference()
    {
        return ref;
    }

    public void setPath(String path)
    {
        this.path = path;
        // TODO: ref = (PsmlPortlets)PortalToolkit.getReference(path);
    }

    public String getPath()
    {
        return this.path;
    }

    public PsmlReference()
    {
        super();
    }

    public Controller getController()
    {
        return ref.getController();
    }

    public void setController(Controller controller)
    {
        ref.setController(controller);       
    }

    public void setSecurity(Security security)
    {
        ref.setSecurity(security);
    }
 
    public Security getSecurity()
    {
        return ref.getSecurity();
    }

    public Vector getEntries()
    {
        return ref.getEntries();
    }

    public void setEntries(Vector entries)
    {
        ref.setEntries(entries);
    }

    public Vector getPortlets()
    {
        return ref.getPortlets();
    }

    public void setPortlets(Vector portlets)
    {
        ref.setPortlets(portlets);
    }

    public int getEntryCount()
    {
        return ref.getEntryCount();
    }

    public int getPortletsCount()
    {
        return ref.getPortletsCount();
    }

    public Entry removeEntry(int index)
    {
        return ref.removeEntry(index);
    } 

    public Portlets removePortlets(int index)
    {
        return ref.removePortlets(index);
    } 

    public Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getEntry(index);
    } 

    public Portlets getPortlets(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getPortlets(index);
    } 


    public Iterator getEntriesIterator()
    {
        return ref.getEntriesIterator();
    }

    public Iterator getPortletsIterator()
    {
        return ref.getPortletsIterator();
    }

    public void addEntry(Entry entry)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addEntry(entry);
    } 

    public void addPortlets(Portlets p)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addPortlets(p);
    } 

    public Entry[] getEntriesArray()
    {
        return ref.getEntriesArray();
    }

    public Portlets[] getPortletsArray()
    {
        return ref.getPortletsArray();
    }

    //////////////////////////////////////////////////////////////////////////

    public Control getControl()
    {
        return ref.getControl();
    }

    public void setControl(Control control)
    {
        ref.setControl(control);
    }


    // Castor serialization methods
    
    /** Required by Castor 0.8.11 XML serialization for retrieving the metainfo
      */
    public MetaInfo getMetaInfo()
    {
        MetaInfo info = super.getMetaInfo();
        if (info == null)
        {
            info = ref.getMetaInfo();
        }        
        return info;
    }
                                
// helper getter setters into meta info

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getTitle */
    public String getTitle()
    {
        return ref.getTitle();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setTitle */
    public void setTitle(String title)
    {
        ref.setTitle(title);
    }

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getDescription */
    public String getDescription()
    {
        return ref.getDescription();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setDescription */
    public void setDescription(String description)
    {
        ref.setDescription(description);
    }

    /** @see org.apache.jetspeed.om.registry.MetaInfo#getImage */
    public String getImage()
    {
        return ref.getImage();
    }
                                
    /** @see org.apache.jetspeed.om.registry.MetaInfo#setImage */
    public void setImage(String image)
    {
        ref.setImage(image);
    }

    /////////////////////////////////////////////////////////////////////////

   /** @return the parameters */
    public Vector getParameters()
    {
        return ref.getParameters();
    }
                                
    /** Sets the parameters for this element
     * @param parameters 
     */
    public void setParameters(Vector parameters)
    {
        ref.setParameters(parameters);
    }

    public String getParameterValue(String name)
    {
        return ref.getParameterValue(name);
    }

    public Parameter getParameter(String name)
    {
        return ref.getParameter(name);
    }

    public Iterator getParameterIterator()
    {
        return ref.getParameterIterator();
    }

    public Parameter getParameter(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getParameter(index);
    } 

    public int getParameterCount()
    {
        return ref.getParameterCount();
    } 

    public int getReferenceCount()
    {
        return ref.getReferenceCount();
    }

    public void removeAllParameter()
    {
        ref.removeAllParameter();
    } 

    public Parameter removeParameter(int index)
    {
        return ref.removeParameter(index);
    } 

    public void setParameter(int index, Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.setParameter(index,vParameter);
    } 

    public Parameter[] getParameter()
    {
        return ref.getParameter();
    } 

    public void addParameter(Parameter vParameter)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addParameter(vParameter);
    } 

    public Reference getReference(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        return ref.getReference(index);
    }

    public Reference removeReference(int index)
    {
        return ref.removeReference(index);
    }

    public Iterator getReferenceIterator()
    {
        return ref.getReferenceIterator();
    }

    public void addReference(Reference ref)
        throws java.lang.IndexOutOfBoundsException
    {
        ref.addReference(ref);
    }

    public Reference[] getReferenceArray()
    {
        return ref.getReferenceArray();
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

        ((PsmlReference)cloned).ref = ((this.ref == null) ? null : (PsmlPortlets) this.ref.clone());
        ((PsmlReference)cloned).securityRef = ((this.securityRef == null) ? null : (SecurityReference) this.securityRef.clone());

        return cloned;

    }   // clone

}

