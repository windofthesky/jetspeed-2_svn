/*
 * Copyright 2000-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jetspeed.capabilities.impl;

import org.apache.jetspeed.capabilities.Client;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Simple implementation of the ClientRegistry interface.
 *
 * @author <a href="shesmer@raleigh.ibm.com">Stephan Hesmer</a>
 * @author <a href="mailto:raphael@apache.org">Rapha�l Luta</a>
 * @author <a href="mailto:roger.ruttimann@earthlink.net">Roger Ruttimann</a>
 * @version $Id$
 */
public class ClientImpl implements Client, java.io.Serializable
{
    private String userAgentPattern = "";
    private String manufacturer = "";
    private String model = "";
    private String version = "";
    private String name;
    private Collection mimetypes;
    private Collection capabilities;
    private int preferredMimeTypeId;

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
        if (object == null)
        {
            return false;
        }

        ClientImpl obj = (ClientImpl) object;

        if (userAgentPattern != null)
        {
            if (!userAgentPattern.equals(obj.userAgentPattern))
            {
                return false;
            }
        } else
        {
            if (obj.userAgentPattern != null)
            {
                return false;
            }
        }

        if (manufacturer != null)
        {
            if (!manufacturer.equals(obj.manufacturer))
            {
                return false;
            }
        } else
        {
            if (obj.manufacturer != null)
            {
                return false;
            }
        }

        if (model != null)
        {
            if (!model.equals(obj.model))
            {
                return false;
            }
        } else
        {
            if (obj.model != null)
            {
                return false;
            }
        }

        if (version != null)
        {
            if (!version.equals(obj.version))
            {
                return false;
            }
        } else
        {
            if (obj.version != null)
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

    public Collection getMimetypes()
    {
        if(this.mimetypes == null)
        {
            this.mimetypes = new ArrayList();
        }
        return mimetypes;
    }

    public void setMimetypes(Collection mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    public Collection getCapabilities()
    {
        if(capabilities == null)
        {
            capabilities = new ArrayList();
        }
        return capabilities;
    }

    public void setCapabilities(Collection capabilities)
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

    /**
     * @return Preferred MimeType ID for Client
     */
    public int getPreferredMimeTypeId()
    {
        return this.preferredMimeTypeId;
    }

    /**
     * Set preferred Mimetype ID for Client
     * @param mimeTypeId MimeTypeId
     */
    public void setPreferredMimeTypeId(int mimeTypeId)
    {
        this.preferredMimeTypeId = mimeTypeId;
    }

}
