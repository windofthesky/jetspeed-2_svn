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

package org.apache.jetspeed.capability.impl;

import org.apache.jetspeed.capability.Client;
import java.util.Vector;

/**
 * Simple implementation of the ClientRegistry interface.
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Raphaël Luta</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class ClientImpl 
    implements Client, java.io.Serializable
{
    private String userAgentPattern = "";
    private String manufacturer = "";
    private String model = "";
    private String version = "";
    private String name;
    private Vector mimetypes;
    private Vector capabilities;
    
    private int clientId;

    public ClientImpl()
    {
    }

    
    /**
     * Implements the equals operation so that 2 elements are equal if
     * all their member values are equal.
     */
    public boolean equals(Object object)
    {
        if (object==null)
        {
            return false;
        }

        ClientImpl obj = (ClientImpl)object;

        if (userAgentPattern!=null)
        {
            if (!userAgentPattern.equals(obj.userAgentPattern))
            {
                return false;
            }
        }
        else
        {
            if (obj.userAgentPattern!=null)
            {
                return false;
            }
        }

        if (manufacturer!=null)
        {
            if (!manufacturer.equals(obj.manufacturer))
            {
                return false;
            }
        }
        else
        {
            if (obj.manufacturer!=null)
            {
                return false;
            }
        }

        if (model!=null)
        {
            if (!model.equals(obj.model))
            {
                return false;
            }
        }
        else
        {
            if (obj.model!=null)
            {
                return false;
            }
        }

        if (version!=null)
        {
            if (!version.equals(obj.version))
            {
                return false;
            }
        }
        else
        {
            if (obj.version!=null)
            {
                return false;
            }
        }

        if (!mimetypes.contains(obj.mimetypes))
        {
            return false;
        }

        if (!capabilities.contains(obj.capabilities))
        {
            return false;
        }

        return super.equals(object);
    }

    public String getUserAgentPattern()
    {
        return userAgentPattern;
    }

    public void setUserAgentPattern(String userAgentPattern)
    {
        this.userAgentPattern = userAgentPattern;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String name)
    {
        manufacturer = name;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String name)
    {
        model = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String name)
    {
        version = name;
    }


    public Vector getMimetypes()
    {
        return mimetypes;
    }

    public void setMimetypes(Vector mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    public Vector getCapabilities()
    {
        return capabilities;
    }

    public void setCapabilities(Vector capabilities)
    {
        this.capabilities = capabilities;
    }
    
    /**
     * Set Client ID -- Assigns the Client ID
     * @param id
     */
    public void setClientId(int id)
    {
        this.clientId = id;
    }
    
    /**
     * Get Client ID
     * @return Client ID
     */
    public int getClientId()
    {
        return this.clientId;
    }
    /**
     * @return
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param string
     */
    public void setName(String string)
    {
        name = string;
    }

}
