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
package org.apache.jetspeed.aggregator;

import java.util.Iterator;

import org.apache.jetspeed.Jetspeed;
import org.apache.jetspeed.om.common.entity.InitablePortletEntity;
import org.apache.jetspeed.om.common.window.PortletWindowImpl;
import org.apache.jetspeed.services.entity.PortletEntityAccess;
import org.apache.pluto.om.portlet.PortletDefinition;
import org.apache.pluto.om.window.PortletWindow;
import org.apache.pluto.om.window.PortletWindowCtrl;
import org.apache.pluto.om.window.PortletWindowList;
import org.apache.pluto.om.window.PortletWindowListCtrl;

/**
 * PortletWindowFactory
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id$
 */
public class PortletWindowFactory
{
    public static PortletWindow getWindow(PortletDefinition portletDefinition, String portletName)
    {
        InitablePortletEntity entity = PortletEntityAccess.getEntity(portletDefinition, portletName);

        PortletEntityAccess.serviceRequest(entity, Jetspeed.getCurrentRequestContext());

        Iterator prefs = entity.getPortletDefinition().getPreferenceSet().iterator();

        //        Debugging
        //        System.out.println("Preference list for " + entity.getPortletDefinition().getName());
        //        while (prefs.hasNext())
        //        {
        //
        //            PreferenceComposite pref = (PreferenceComposite) prefs.next();
        //            System.out.println("Value list for " + pref.getName());
        //            if (pref != null)
        //            {
        //                Iterator itr = pref.getValues();
        //                System.out.println("Values iterator "+itr);
        //                while (itr.hasNext())
        //                {
        //                    System.out.println("Value: " + ((String) itr.next()));
        //                }
        //            }
        //        }

        PortletWindow portletWindow = new PortletWindowImpl(entity.getId());
        ((PortletWindowCtrl) portletWindow).setPortletEntity(entity);
        PortletWindowList windowList = entity.getPortletWindowList();
        ((PortletWindowListCtrl) windowList).add(portletWindow);
        return portletWindow;
    }
}
