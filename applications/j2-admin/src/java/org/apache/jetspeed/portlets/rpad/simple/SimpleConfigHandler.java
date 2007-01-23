/*
 * Copyright 2007 The Apache Software Foundation.
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
package org.apache.jetspeed.portlets.rpad.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.jetspeed.portlets.rpad.PortletApplication;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleConfigHandler extends DefaultHandler
{

    private List portletApplications;

    private PortletApplication portletApplication;

    private List qNameList;

    public SimpleConfigHandler()
    {
        portletApplications = new ArrayList();
        qNameList = new ArrayList();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes)
    {
        if ("portlet".equals(qName))
        {
            portletApplication = new PortletApplication();
            String created = attributes.getValue("created");
            if (created != null)
            {
                //TODO
            }
            String lastModified = attributes.getValue("last-modified");
            if (lastModified != null)
            {
                //TODO
            }
        }
        synchronized (qNameList)
        {
            qNameList.add(qName);
        }
    }

    public void characters(char[] ch, int start, int length)
    {
        if (qNameList.size() < 1)
        {
            return;
        }

        String value = new String(ch, start, length);
        String qName = (String) qNameList.get(qNameList.size() - 1);
        String parentQName;
        if (qNameList.size() - 2 >= 0)
        {
            parentQName = (String) qNameList.get(qNameList.size() - 2);
        }
        else
        {
            parentQName = "";
        }
        if ("portlet-spec-version".equals(qName))
        {
            portletApplication.setPortletSpecVersion(value);
        }
        else if ("group-id".equals(qName))
        {
            portletApplication.setGroupId(value);
        }
        else if ("artifact-id".equals(qName))
        {
            portletApplication.setArtifactId(value);
        }
        else if ("packaging".equals(qName))
        {
            portletApplication.setPackaging(value);
        }
        else if ("version".equals(qName))
        {
            portletApplication.setVersion(value);
        }
        else if ("name".equals(qName))
        {
            if ("publisher".equals(parentQName))
            {
                portletApplication.setPublisherName(value);
            }
            else if ("license".equals(parentQName))
            {
                portletApplication.setPublisherName(value);
            }
            else
            {
                portletApplication.setName(value);
            }
        }
        else if ("description".equals(qName))
        {
            portletApplication.setDescription(value);
        }
        else if ("cateogry".equals(qName))
        {
            portletApplication.addCategory(value);
        }
        else if ("url".equals(qName))
        {
            if ("publisher".equals(parentQName))
            {
                portletApplication.setPublisherUrl(value);
            }
        }
        else if ("binary".equals(qName))
        {
            portletApplication.setBinaryUrl(value);
        }
        else if ("source".equals(qName))
        {
            portletApplication.setSourceUrl(value);
        }
        else if ("thumbnail".equals(qName))
        {
            portletApplication.setThumbnailUrl(value);
        }
        //TODO dependencies
        //TODO license
        else if ("build".equals(qName))
        {
            portletApplication.setJavaBuildVersion(value);
        }
        else if ("runtime".equals(qName))
        {
            portletApplication.setJavaRuntimeVersion(value);
        }
        else if ("default-locale".equals(qName))
        {
            Locale l = getLocaleFromString(value);
            if (l != null)
            {
                portletApplication.setDefaultLocale(l);
            }
        }
        else if ("supported-locale".equals(qName))
        {
            Locale l = getLocaleFromString(value);
            if (l != null)
            {
                portletApplication.addSupportedLocale(l);
            }
        }

    }

    private Locale getLocaleFromString(String localeString)
    {
        StringTokenizer st = new StringTokenizer(localeString, "_-");
        String[] buf = new String[3];
        int count = 0;
        while (st.hasMoreTokens())
        {
            buf[count] = st.nextToken();
            count++;
        }
        if (count > 2)
        {
            return new Locale(buf[0], buf[1], buf[2]);
        }
        else if (count > 1)
        {
            return new Locale(buf[0], buf[1]);
        }
        else if (count > 0)
        {
            return new Locale(buf[0]);
        }
        return null;
    }

    public void endElement(String uri, String localName, String qName)
    {
        if ("portlet".equals(qName))
        {
            portletApplications.add(portletApplication);
            portletApplication = null;
        }

        synchronized (qNameList)
        {
            if (qNameList.size() < 1)
            {
                throw new IllegalStateException("The stacked QName is 0.");
            }
            String stackedQName = (String) qNameList
                    .remove(qNameList.size() - 1);
            if (!qName.equals(stackedQName))
            {
                throw new IllegalStateException("The expected QName is "
                        + stackedQName + ". But the current value is " + qName);
            }
        }
    }

    /**
     * @return the portletApplications
     */
    public List getPortletApplications()
    {
        return portletApplications;
    }

    /**
     * @param portletApplications the portletApplications to set
     */
    public void setPortletApplications(List portletApplications)
    {
        this.portletApplications = portletApplications;
    }
}
