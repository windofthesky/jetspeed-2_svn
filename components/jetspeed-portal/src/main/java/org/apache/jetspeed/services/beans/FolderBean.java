/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.services.beans;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.jetspeed.om.page.Fragment;
import org.apache.jetspeed.om.page.PageSecurity;
import org.apache.jetspeed.page.document.DocumentNotFoundException;
import org.apache.jetspeed.page.document.NodeException;

/**
 * FolderBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="folder")
public class FolderBean extends NodeBean
{
    private static final long serialVersionUID = 1L;
    
    private String skin;
    private String effectiveDefaultLayoutDecorator;
    private String effectiveDefaultPortletDecorator;
    private String defaultLayoutDecorator;
    private String defaultPortletDecorator;
    private List<String> documentOrder;
    private String defaultPage;
    private boolean reserved;
    private int reservedType;
    private PageSecurityBean pageSecurityBean;
    
    public FolderBean()
    {
        
    }
    
    public FolderBean(Folder folder) throws NodeException
    {
        super(folder);
        skin = folder.getSkin();
        effectiveDefaultLayoutDecorator = folder.getEffectiveDefaultDecorator(Fragment.LAYOUT);
        effectiveDefaultPortletDecorator = folder.getEffectiveDefaultDecorator(Fragment.PORTLET);
        defaultLayoutDecorator = folder.getDefaultDecorator(Fragment.LAYOUT);
        defaultPortletDecorator = folder.getDefaultDecorator(Fragment.PORTLET);
        
        List<String> temp = folder.getDocumentOrder();
        if (temp != null)
        {
            documentOrder = new ArrayList<String>(temp);
        }
        
        defaultPage = folder.getDefaultPage();
        reserved = folder.isReserved();
        reservedType = folder.getReservedType();
        
        PageSecurity pageSecurity = null;
        
        try
        {
            pageSecurity = folder.getPageSecurity();
        }
        catch (DocumentNotFoundException ignore)
        {
        }
        
        if (pageSecurity != null)
        {
            pageSecurityBean = new PageSecurityBean(pageSecurity);
        }
    }

    public String getSkin()
    {
        return skin;
    }

    public void setSkin(String skin)
    {
        this.skin = skin;
    }

    public String getEffectiveDefaultLayoutDecorator()
    {
        return effectiveDefaultLayoutDecorator;
    }

    public void setEffectiveDefaultLayoutDecorator(String effectiveDefaultLayoutDecorator)
    {
        this.effectiveDefaultLayoutDecorator = effectiveDefaultLayoutDecorator;
    }

    public String getEffectiveDefaultPortletDecorator()
    {
        return effectiveDefaultPortletDecorator;
    }

    public void setEffectiveDefaultPortletDecorator(String effectiveDefaultPortletDecorator)
    {
        this.effectiveDefaultPortletDecorator = effectiveDefaultPortletDecorator;
    }

    public String getDefaultLayoutDecorator()
    {
        return defaultLayoutDecorator;
    }

    public void setDefaultLayoutDecorator(String defaultLayoutDecorator)
    {
        this.defaultLayoutDecorator = defaultLayoutDecorator;
    }

    public String getDefaultPortletDecorator()
    {
        return defaultPortletDecorator;
    }

    public void setDefaultPortletDecorator(String defaultPortletDecorator)
    {
        this.defaultPortletDecorator = defaultPortletDecorator;
    }

    public List<String> getDocumentOrder()
    {
        return documentOrder;
    }

    public void setDocumentOrder(List<String> documentOrder)
    {
        this.documentOrder = documentOrder;
    }

    public String getDefaultPage()
    {
        return defaultPage;
    }

    public void setDefaultPage(String defaultPage)
    {
        this.defaultPage = defaultPage;
    }

    public boolean isReserved()
    {
        return reserved;
    }

    public void setReserved(boolean reserved)
    {
        this.reserved = reserved;
    }

    public int getReservedType()
    {
        return reservedType;
    }

    public void setReservedType(int reservedType)
    {
        this.reservedType = reservedType;
    }

    @XmlElement(name="pageSecurity")
    public PageSecurityBean getPageSecurityBean()
    {
        return pageSecurityBean;
    }

    public void setPageSecurityBean(PageSecurityBean pageSecurityBean)
    {
        this.pageSecurityBean = pageSecurityBean;
    }
    
}
