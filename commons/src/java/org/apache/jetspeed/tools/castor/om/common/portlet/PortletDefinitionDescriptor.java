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
package org.apache.jetspeed.tools.castor.om.common.portlet;

import java.util.Collection;
import java.util.ArrayList;

import org.apache.jetspeed.om.common.portlet.PortletDefinitionImpl;
import org.apache.jetspeed.tools.castor.om.common.LanguageSetDescriptor;
import org.apache.jetspeed.tools.castor.om.common.PreferenceSetDescriptor;
import org.apache.jetspeed.om.common.LanguageSetImpl;
import org.apache.jetspeed.om.common.PreferenceSetImpl;

/**
 * Used to help Castor in mapping XML portlet types to Java objects 
 *
 * @author <a href="taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletDefinitionDescriptor extends PortletDefinitionImpl
{
    // NOTE: this needs to be mapped later in a post load phase
    private ArrayList castorSupportedLocales = new ArrayList();   
    private String resourceBundle = null;
    
    public String getResourceBundle()
    {
        return this.resourceBundle;
    }    

    public void setResourceBundle(String resourceBundle)
    {
        this.resourceBundle = resourceBundle;
    }    
    
    public Collection getCastorInitParams()
    {
        return (Collection)this.getInitParameterSet();        
    }
    
    public Collection getCastorContentTypes()
    {
        return (Collection)this.getContentTypeSet();
    }

    public Collection getCastorResources()
    {
        LanguageSetImpl bls = (LanguageSetImpl)this.getLanguageSet();
        if (null == bls)
        {
            bls = new LanguageSetDescriptor();
            this.setLanguageSet(bls);
        }
        return (Collection)bls;        
    }
    
    public void setCastorResources(LanguageSetDescriptor descriptor)
    {
        this.setLanguageSet(descriptor);
    }
    
    public Collection getCastorSupportedLocales()
    {
        return castorSupportedLocales;
    }
    
    public Collection getCastorPreferences()
    {
        PreferenceSetImpl bps = (PreferenceSetImpl)this.getPreferenceSet();
        if (null == bps)
        {
            bps = new PreferenceSetDescriptor();
            this.setPreferenceSet(bps);
        }
        
        return (Collection)this.getPreferenceSet();
    }
    
    public void setCastorPreferences(PreferenceSetDescriptor descriptor)
    {
        this.setPreferenceSet(descriptor);
    }
    
    public Collection getCastorInitSecurityRoleRefs()
    {
        return (Collection)this.getInitSecurityRoleRefSet();
    }

    public void postLoad(Object parameter) throws Exception
    {
        LanguageSetDescriptor language = (LanguageSetDescriptor)this.getLanguageSet();
        language.postLoad(parameter);                    
    }
}
