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

import org.apache.jetspeed.om.profile.Control;
import org.apache.jetspeed.om.profile.Controller;
import org.apache.jetspeed.om.profile.IdentityElement;
import org.apache.jetspeed.om.profile.Layout;
import org.apache.jetspeed.om.profile.MetaInfo;
import org.apache.jetspeed.om.profile.Skin;
import org.apache.jetspeed.services.idgenerator.JetspeedIdGenerator;

/**
 * Base simple bean-like implementation of the IdentityElement interface
 * suitable for Castor XML serialization.
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public /*abstract*/ class PsmlIdentityElement 
    extends PsmlConfigElement 
    implements IdentityElement, java.io.Serializable                                                
{

    private String id = null;
    
    private MetaInfo metaInfo = null;

    private Skin skin = null;

    private Layout layout = null;

    private Control control = null;

    private Controller controller = null ;

    public PsmlIdentityElement()
    {}

    /** @see org.apache.jetspeed.om.profile.IdentityElement#getId */
    public String getId()
    {
        if (this.id == null)
        {
            this.id = JetspeedIdGenerator.getNextPeid();
        }
        return this.id;
    }
                                
    /** @see org.apache.jetspeed.om.profile.IdentityElement#setId */
    public void setId( String id )
    {
        this.id = id;
    }
  
    /** @see org.apache.jetspeed.om.profile.IdentityElement#getSkin */
    public Skin getSkin()
    {
        return this.skin;
    }

    /** @see org.apache.jetspeed.om.profile.IdentityElement#setSkin */
    public void setSkin(Skin skin)
    {
        this.skin = skin;
    }

    /** @see org.apache.jetspeed.om.profile.IdentityElement#getLayout */
    public Layout getLayout()
    {
        return this.layout;
    }

    /** @see org.apache.jetspeed.om.profile.IdentityElement#setLayout */
    public void setLayout(Layout layout)
    {
        this.layout = layout;
    }

    /** @see org.apache.jetspeed.om.profile.IdentityElement#getControl */
    public Control getControl()
    {
        return this.control;
    }

    /** @see org.apache.jetspeed.om.profile.IdentityElement#setControl */
    public void setControl(Control control)
    {
        this.control = control;
    }

    public Controller getController()
    {
        return this.controller;
    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }


    // Castor serialization methods
    
    /**
     * Required by Castor 0.8.11 XML serialization for retrieving the metainfo
     * @see org.apache.jetspeed.om.profile.IdentityElement#getMetaInfo 
     */
    public MetaInfo getMetaInfo()
    {
        return this.metaInfo;
    }
                                
    /** 
     * Required by Castor 0.8.11 XML serialization for setting the entry
     * metainfo
     * @see org.apache.jetspeed.om.profile.IdentityElement#setMetaInfo 
     */
    public void setMetaInfo( MetaInfo metaInfo )
    {
        this.metaInfo = metaInfo;
    }

    // helper getter setters into meta info

    /** @see org.apache.jetspeed.om.profile.MetaInfo#getTitle */
    public String getTitle()
    {
        if (this.metaInfo != null)
        {
            return this.metaInfo.getTitle();
        }
        
        return null;
    }
                                
    /** @see org.apache.jetspeed.om.profile.MetaInfo#setTitle */
    public void setTitle(String title)
    {
        if (this.metaInfo == null)
        {
            this.metaInfo = new PsmlMetaInfo();
        }
        
        this.metaInfo.setTitle(title);
    }

    /** @see org.apache.jetspeed.om.profile.MetaInfo#getDescription */
    public String getDescription()
    {
        if (this.metaInfo != null)
        {
            return this.metaInfo.getDescription();
        }
        
        return null;
    }
                                
    /** @see org.apache.jetspeed.om.profile.MetaInfo#setDescription */
    public void setDescription(String description)
    {
        if (this.metaInfo == null)
        {
            this.metaInfo = new PsmlMetaInfo();
        }
        
        this.metaInfo.setDescription(description);
    }

    /** @see org.apache.jetspeed.om.profile.MetaInfo#getImage */
    public String getImage()
    {
        if (this.metaInfo != null)
        {
            return this.metaInfo.getImage();
        }
        
        return null;
    }
                                
    /** @see org.apache.jetspeed.om.profile.MetaInfo#setImage */
    public void setImage(String image)
    {
        if (this.metaInfo == null)
        {
            this.metaInfo = new PsmlMetaInfo();
        }
        
        this.metaInfo.setImage(image);
    }

    /**
     * Create a clone of this object
     */
    public Object clone()
        throws java.lang.CloneNotSupportedException
    {
        Object cloned = super.clone();
        
        // clone some member variables
        ((PsmlIdentityElement)cloned).metaInfo = ((this.metaInfo == null) ? null : (MetaInfo) this.metaInfo.clone());
        ((PsmlIdentityElement)cloned).skin = ((this.skin == null) ? null : (Skin) this.skin.clone());
        ((PsmlIdentityElement)cloned).layout = ((this.layout == null) ? null : (Layout) this.layout.clone());
        ((PsmlIdentityElement)cloned).control = ((this.control == null) ? null : (Control) this.control.clone());
        ((PsmlIdentityElement)cloned).controller = ((this.controller == null) ? null : (Controller) this.controller.clone());
        
        return cloned;

    }   // clone
}