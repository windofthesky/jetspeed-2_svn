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
package org.apache.portals.bridges.struts.taglib;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.List;

/**
 * TagBeanInfo required for the {@link ELLinkTag}.
 * <p>
 * Extends the Struts {@link org.apache.strutsel.taglib.html.ELLinkTagBeanInfo ELLinkTagBeanInfo}
 * to provide struts-el support for the {@link ELLinkTag#setActionURL(String) actionURL} and
 * {@link ELLinkTag#setRenderURL(String) renderURL} attributes.
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class ELLinkTagBeanInfo extends org.apache.strutsel.taglib.html.ELLinkTagBeanInfo
{
    public  PropertyDescriptor[] getPropertyDescriptors()
    {
        List proplist = Arrays.asList(super.getPropertyDescriptors());
        try {
            proplist.add(new PropertyDescriptor("actionURL", ELLinkTag.class,
                    null, "setActionURLExpr"));
            proplist.add(new PropertyDescriptor("renderURL", ELLinkTag.class,
                    null, "setRenderURLExpr"));
        } catch (IntrospectionException ex) {}
        PropertyDescriptor[] result =
            new PropertyDescriptor[proplist.size()];
        return ((PropertyDescriptor[]) proplist.toArray(result));
    }
}
