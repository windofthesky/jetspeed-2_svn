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

package org.apache.jetspeed.capability;

//standard Jetspeed stuff
import org.apache.jetspeed.util.MimeType;
import org.apache.jetspeed.om.registry.ClientEntry;
import org.apache.jetspeed.om.registry.MediaTypeEntry;
import org.apache.jetspeed.om.registry.MediaTypeRegistry;
import org.apache.jetspeed.services.registry.JetspeedRegistry;
import org.apache.jetspeed.services.registry.RegistryService;


//standard Java stuff
import java.util.Vector;
import java.util.Iterator;
import java.util.Enumeration;

/**
 * Read only wrapper around a ClientEntry registry entry that
 * implements the CapabilityMap interface
 *
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @version $Id$
 */
public class BaseCapabilityMap implements CapabilityMap
{

    private String      useragent;
    private ClientEntry entry;

    protected BaseCapabilityMap(String agent, ClientEntry entry)
    {
        this.useragent = agent;
        this.entry = entry;
    }

    /**
    @see CapabilityMap#getPreferredType
    */
    public MimeType getPreferredType()
    {
        return entry.getMimetypeMap().getPreferredMimetype();
    }

    /**
    Returns the preferred media type for the current user-agent
    */
    public String getPreferredMediaType()
    {
        Iterator i = listMediaTypes();

        if (i.hasNext())
        {
            return (String)i.next();
        }

        return null;
    }

    /**
     * Returns an ordered list of supported media-types, from most preferred
     * to least preferred
     */
    public Iterator listMediaTypes()
    {
        Vector results = new Vector();
        Vector types = new Vector();

        // first copy the current media type list, ordered by global preference
        Enumeration en = ((MediaTypeRegistry)JetspeedRegistry.get(RegistryService.MEDIA_TYPE)).getEntries();
        while (en.hasMoreElements())
        {
            types.add(en.nextElement());
        }

        //then retrieve a list of supported mime-types, ordered by
        //preference

        Iterator mimes = entry.getMimetypeMap().getMimetypes();

        //now, for each mime-type test if the media is supported
        while(mimes.hasNext())
        {
            String mime = ((MimeType)mimes.next()).getContentType();
            Iterator i = types.iterator();

            while(i.hasNext())
            {
                MediaTypeEntry mte = (MediaTypeEntry)i.next();

                if (mime.equals(mte.getMimeType()))
                {
                    if (entry.getCapabilityMap().containsAll(mte.getCapabilityMap()))
                    {
                        results.add(mte.getName());
                    }
                }
            }
        }

        return results.iterator();
    }

    /**
    @see CapabilityMap#getAgent
    */
    public String getAgent()
    {
        return this.useragent;
    }

    /**
    @see CapabilityMap#hasCapability
    */
    public boolean hasCapability( int cap )
    {
        return false;
    }

    /**
    @see CapabilityMap#hasCapability
    */
    public boolean hasCapability( String capability )
    {
        Iterator i = entry.getCapabilityMap().getCapabilities();

        while (i.hasNext())
        {
            String cap = (String)i.next();

            if (cap.equals(capability))
            {
                return true;
            }
        }

        return false;
    }

    /**
    @see CapabilityMap#getMimeTypes
    */
    public MimeType[] getMimeTypes()
    {
        Vector v = new Vector();
        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();
            v.add(mime);
        }

        return (MimeType[])v.toArray();
    }

    /**
    @see CapabilityMap#supportsMimeType
    */
    public boolean supportsMimeType( MimeType mimeType )
    {
        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();

            if (mime.equals(mimeType))
            {
                return true;
            }
        }

        return false;

    }

    /**
    @see CapabilityMap#supportsMimeType
    */
    public boolean supportsMediaType( String media )
    {
        if (media == null)
        {
            return true;
        }

        MediaTypeEntry mte = (MediaTypeEntry)JetspeedRegistry.getEntry(RegistryService.MEDIA_TYPE, media);

        if (!supportsMimeType(new MimeType(mte.getMimeType())))
        {
            return false;
        }

        return entry.getCapabilityMap().containsAll(mte.getCapabilityMap());

    }

    /**
    Create a map string representation
    */
    public String toString()
    {
        StringBuffer desc = new StringBuffer(entry.getName());

        Iterator i = entry.getMimetypeMap().getMimetypes();

        while (i.hasNext())
        {
            MimeType mime = (MimeType)i.next();
            desc.append( mime ).append("-");
        }

        i = entry.getCapabilityMap().getCapabilities();

        while ( i.hasNext() )
        {
          String capa = (String)i.next();
          desc.append(capa).append("/");
        }

        return desc.toString();
    }

}

