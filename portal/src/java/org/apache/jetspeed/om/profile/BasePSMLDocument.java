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

package org.apache.jetspeed.om.profile;

import org.apache.jetspeed.om.profile.Portlets;
import org.apache.jetspeed.om.profile.Entry;
import java.util.Iterator;

/**
 * This class represents a loaded PSML document in memory, providing
 * all facilities for finding and updating specific parts of the 
 * document.
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BasePSMLDocument implements PSMLDocument
{
    /**
     * The name of this PSML document, will be typically the URL of the file
     * for file-based implementations
     */
    private String name = null;

    /**
     * The PortletSet descriptions that make up this document
     */
    private Portlets portlets = null;

    /**
     * Construct a new empty PSMLDocument
     */
    public BasePSMLDocument()
    {
        // empty constructor
    }

    /**
     * Construct a new named PSMLDocument associated with the specified
     * PSML portlet set description
     *
     * @param name the name of this document
     * @param portlets the PSML memory structure
     */
    public BasePSMLDocument( String name, Portlets portlets )
    {
        this.name = name;
        this.portlets = portlets;
    }
    
    /**
     * Return the name of this document
     */
    public final String getName()
    {
        return this.name;
    }
        
    /**
     * Sets a new name for this document
     * 
     * @param name the new document name
     */
    public final void setName(String name)
    {
        this.name = name;
    }

    /**
     * Return ths portlet set PSML description of this document
     *
     * @return a PSML object model hierarchy, or null if none is 
     * defined for this document
     */
    public final Portlets getPortlets()
    {
        return this.portlets;
    }

    /**
     * Sets a new PSML object model for this document
     * 
     * @param portlets the PSML object model
     */
    public final void setPortlets(Portlets portlets)
    {
        this.portlets = portlets;
    }

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given portlet name
     * 
     *  @param name the portlet name to seek
     *  @return the found entry description or null
     */
    public Entry getEntry(String name)
    {
        return getEntry(this.portlets, name);
    }

    /** Returns the first entry in the current PSML resource corresponding 
     *  to the given entry id
     * 
     *  @param entryId the portlet's entry id to seek
     *  @return the found entry description or null
     */
    public Entry getEntryById(String entryId)
    {
        return getEntryById(this.portlets, entryId);
    }

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given name
     * 
     *  @param name the portlets name to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortlets(String name)
    {        
        Portlets p = getPortlets(this.portlets, name);
        
        if (p == null)
        {
            //maybe name is a position...
            try
            {
                p = getPortlets(Integer.parseInt(name));
            }
            catch (NumberFormatException e)
            {
                // this will happen if name is not parseable, ignore
            }
        }
        
        return p;
    }

    /** Returns the first portlets element in the current PSML resource corresponding 
     *  to the given id
     * 
     *  @param name the portlets id to seek
     *  @return the found portlets description or null
     */
    public Portlets getPortletsById(String portletId)
    {        
        Portlets p = getPortletsById(this.portlets, portletId);
        return p;
    }

    /** Returns the first portlets element in the current PSML resource 
     *  found at the specified position. The position is computed using
     *  a left-most tree traversal algorithm of the existing portlets (thus
     *  not counting other entry objects)
     * 
     *  @param position the sought position
     *  @return the found portlets object or null if we did not find such an
     *  object
     */
    public Portlets getPortlets(int position)
    {
        return getPortlets(this.portlets, position, 0);
    }

    /** Returns the first entry in the specified PSML resource corresponding 
     *  to the given portlet name
     * 
     *  @param portlets the PSML description to look into
     *  @param name the portlet name to seek
     *  @return the found entry description or null
     */
    public static Entry getEntry(Portlets portlets, String name)
    {
        Entry entry = null;

        for (Iterator it1 = portlets.getEntriesIterator(); it1.hasNext(); )
        {
            entry = (Entry) it1.next();
            if (entry.getParent().equals (name))
                 return (entry);
        }

        entry = null;

        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();

            entry = getEntry(p, name);

            if (entry != null)
                 break;
        }

        return (entry);
    }

    /** Returns the first entry in the specified PSML resource corresponding 
     *  to the given portlet Id
     * 
     *  @param portlets the PSML description to look into
     *  @param entryId the portlet's entry id to seek
     *  @return the found entry description or null
     */
    public static Entry getEntryById(Portlets portlets, String entryId)
    {
        Entry entry = null;

        for (Iterator it1 = portlets.getEntriesIterator(); it1.hasNext(); )
        {
            entry = (Entry) it1.next();
            if ((entry.getId()!=null) && entry.getId().equals (entryId))
                 return (entry);
        }

        entry = null;

        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();

            entry = getEntryById(p, entryId);

            if (entry != null)
                 break;
        }

        return (entry);
    }

    /** Returns the first portlets element in the specified PSML resource corresponding 
     *  to the given Id
     * 
     *  @param portlets the PSML description to look into
     *  @param portletId the portlet's id to seek
     *  @return the found portlets description or null
     */
    public static Portlets getPortletsById(Portlets portlets, String portletId)
    {
        Portlets entry = portlets;
        
        if ( (entry.getId()!=null) && entry.getId().equals(portletId) )
        {
            return entry;
        }

        entry = null;
        
        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();

            entry = getPortletsById(p, portletId);

            if (entry != null) break;
        }

        return (entry);
    }

    /** Returns the first portlets element in the specified PSML resource corresponding 
     *  to the given name
     * 
     *  @param portlets the PSML description to look into
     *  @param name the portlets name to seek
     *  @return the found portlets description or null
     */
    public static Portlets getPortlets(Portlets portlets, String name)
    {
        Portlets entry = portlets;
        
        if ( (entry.getName()!=null) && entry.getName().equals(name) )
        {
            return entry;
        }

        entry = null;
        
        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();

            entry = getPortlets(p, name);

            if (entry != null) break;
        }

        return (entry);
    }
    /** Returns the first portlets element in the specified PSML resource 
     *  in the given position
     * 
     *  @param portlets the PSML description to look into
     *  @param position the position to look for
     *  @param count the numbering for the portlets passed as parameter
     *  @return the found portlets description or null
     */
    public static Portlets getPortlets(Portlets portlets, int position, int count)
    {
        // sanity check, we never look after the sought position
        if (position < count)
        {
            return null;
        }
        
        // the base portlets is the one we're looking for...
        if (position == count)
        {
            return portlets;
        }

        // we need to recurse in the children
        Portlets result = null;
        
        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();
            count++;
            result = getPortlets(p, position, count);

            if (result != null) break;
        }

        return result;
    }
    /**
     * Remove the Entry in the specified PSML resource corresponding 
     * to the given portlet Id
     * 
     * @param entryId the portlet's entry id to seek
     */
    public boolean removeEntryById(String entryId)
    {        
        return removeEntryById(this.portlets, entryId);
    }

    /**
     * Remove the Entry in the specified PSML resource corresponding 
     * to the given portlet Id
     * 
     * @param portlets the PSML description to look into
     * @param entryId the portlet's entry id to seek
     */
    public static boolean removeEntryById(Portlets portlets, String entryId)
    {
        for (int i=0; i < portlets.getEntryCount(); i++)
        {
            if ( entryId.equals(portlets.getEntry(i).getId()) )
            {
                portlets.removeEntry(i);
                return true;
            }
        }

        for (Iterator it2 = portlets.getPortletsIterator(); it2.hasNext(); )
        {
            Portlets p = (Portlets) it2.next();

            if (removeEntryById(p, entryId) == true)
                return true;
        }

        return false;
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();
        
        // clone the portlets
        ((BasePSMLDocument)cloned).portlets = ((this.portlets == null) ? null : (Portlets) this.portlets.clone());
        
        return cloned;
    }
}

