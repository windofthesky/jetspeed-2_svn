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
package org.apache.jetspeed.portlets.security.rolemgt;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;

import org.apache.myfaces.custom.tabbedpane.HtmlPanelTabbedPane;

/**
 * <p>
 * Utility class used for rendering.
 * </p>
 * 
 * @author <a href="mailto:dlestrat@apache.org">David Le Strat </a>
 */
public class RoleMgtRenderUtil
{
    private static final String DETAIL_SUFFIX = "Detail";
    
    /**
     * <p>
     * Utility method used to locate the tabbed panel component.
     * </p>
     * 
     * @param component The component.
     * @return The {@link HtmlPanelTabbedPane}.
     */
    public static HtmlPanelTabbedPane findTabbedPane(UIComponent component)
    {
        HtmlPanelTabbedPane tabbedPane = null;

        UIComponent parent = component.getParent();
        if (parent instanceof HtmlPanelTabbedPane)
        {
            tabbedPane = (HtmlPanelTabbedPane) parent;
        }
        else
        {
            tabbedPane = findTabbedPane(parent);
        }

        return tabbedPane;
    }

    /**
     * <p>
     * Utility to get validation messages.
     * </p>
     * 
     * @param locale The locale.
     * @param severity The severity.
     * @param messageId The messageId.
     * @param bundleName The bundleName.
     * @param args The args.
     * @return The {@link FacesMessage}
     */
    public static FacesMessage getMessage(Locale locale, FacesMessage.Severity severity,
            String messageId, String bundleName, Object args[])
    {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleName, locale);
        String detail = null;

        String summary = getBundleString(resourceBundle, messageId);
        if (summary != null)
        {
            detail = getBundleString(resourceBundle, messageId + DETAIL_SUFFIX);
        }

        if (args != null && args.length > 0)
        {
            MessageFormat format;

            if (summary != null)
            {
                format = new MessageFormat(summary, locale);
                summary = format.format(args);
            }

            if (detail != null)
            {
                format = new MessageFormat(detail, locale);
                detail = format.format(args);
            }
        }

        return new FacesMessage(severity, summary, detail);
    }
    
    /**
     * <p>
     * Utility to get a bundle string.
     * </p>
     * 
     * @param bundle The bundle.
     * @param key The key.
     * @return The message.
     */
    private static String getBundleString(ResourceBundle bundle, String key)
    {
        try
        {
            return bundle == null ? null : bundle.getString(key);
        }
        catch (MissingResourceException e)
        {
            return null;
        }
    }

}