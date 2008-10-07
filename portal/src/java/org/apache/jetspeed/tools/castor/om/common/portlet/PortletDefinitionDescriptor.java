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
package org.apache.jetspeed.tools.castor.om.common.portlet;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;

import javax.portlet.PortletMode;

import org.apache.jetspeed.tools.castor.om.common.LanguageSetDescriptor;
import org.apache.jetspeed.tools.castor.om.common.PreferenceDescriptor;
import org.apache.jetspeed.tools.castor.om.common.PreferenceSetDescriptor;
import org.apache.jetspeed.om.impl.LanguageSetImpl;
import org.apache.jetspeed.om.portlet.impl.PortletDefinitionImpl;
import org.apache.jetspeed.om.preference.impl.PreferenceSetImpl;
import org.apache.pluto.om.portlet.ContentTypeSet;

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
        Iterator prefs = getCastorPreferences().iterator();
        while (prefs.hasNext())
        {
            PreferenceDescriptor pd  = (PreferenceDescriptor)prefs.next();
            pd.setCastorValues(pd.getCastorValues());
        }
        
        // convert string collection representation to portlet mode collection
        ContentTypeSet contentSet = this.getContentTypeSet();
        Iterator contents = contentSet.iterator();
        while (contents.hasNext())
        {
            ContentTypeDescriptor type = (ContentTypeDescriptor)contents.next();
            Iterator modes = type.getCastorPortletModes().iterator();
            while (modes.hasNext())
            {
                String mode = (String)modes.next();
                type.addPortletMode(new PortletMode(mode));        
            }
        }
        
    }
}
