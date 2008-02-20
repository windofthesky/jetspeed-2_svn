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
package org.apache.jetspeed.portlets.security.users;

import java.io.Serializable;
import java.io.NotSerializableException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import javax.portlet.PortletRequest;

import org.apache.wicket.RequestContext;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;

import org.apache.portals.messaging.PortletMessaging;

import org.apache.jetspeed.CommonPortletServices;
import org.apache.jetspeed.security.UserManager;
import org.apache.jetspeed.security.SecurityException;
import org.apache.jetspeed.portlets.security.SecurityResources;
import org.apache.jetspeed.portlets.security.SecurityUtil;
import org.apache.jetspeed.audit.AuditActivity;
import org.apache.jetspeed.components.portletregistry.PortletRegistry;

import org.apache.jetspeed.portlets.wicket.component.SelectionImagePropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.LinkPropertyColumn;
import org.apache.jetspeed.portlets.wicket.component.PortletOddEvenItem;

/**
 * User Browser Wicket WebPage
 * 
 * @author <a href="mailto:woonsan@apache.org">Woonsan Ko</a>
 * @version $Id: $
 */
public class WicketUserBrowser extends WicketUserAdmin
{

    protected List userNameList;
    protected String searchString;
    protected boolean filtered;
    protected String selectedUserName;
    
	public WicketUserBrowser()
	{
        SortableDataProvider dataProvider = new SortableDataProvider()
        {
            public int size()
            {
                return getUserNameList().size();
            }
            
            public IModel model(Object object)
            {
                String userName = (String) object;
                return new Model(userName);
            }
            
            public Iterator iterator(int first, int count)
            {
                return getUserNameList().subList(first, first + count).iterator();
            }
        };
        
        IColumn [] columns = 
        {
            new SelectionImagePropertyColumn(new Model(" "), "")
            {
                protected boolean isSelected(Item item, String componentId, IModel model)
                {
                    String userName = (String) model.getObject();
                    return userName.equals(selectedUserName);
                }
            },
            new LinkPropertyColumn(new ResourceModel("user"), "")
            {
                public void onClick(Item item, String componentId, IModel model)
                {
                    setSelectedUserName((String) model.getObject());
                    
                    if (getSelectedUserName() != null)
                    {
                        try
                        {
                            PortletMessaging.publish(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED, getSelectedUserName());
                        }
                        catch (NotSerializableException e)
                        {
                        }
                    }
                }
            }
        };
        
        DataTable userDataTable = new DataTable("entries", columns, dataProvider, 10)
        {
            protected Item newRowItem(String id, int index, IModel model)
            {
                return new PortletOddEvenItem(id, index, model);
            }
        };
        
        userDataTable.addTopToolbar(new HeadersToolbar(userDataTable, dataProvider));
        userDataTable.addBottomToolbar(new NavigationToolbar(userDataTable));
        
        add(userDataTable);
        
        Form searchForm = new Form("searchForm")
        {
            protected void onSubmit()
            {
            }
        };
        
        TextField searchStringField = new TextField("searchString", new PropertyModel(this, "searchString"));
        searchForm.add(searchStringField);
        
        CheckBox filteredField = new CheckBox("filtered", new PropertyModel(this, "filtered"));
        searchForm.add(filteredField);
        
        add(searchForm);
	}

    public void setSearchString(String searchString)
    {
        this.searchString = (searchString == null ? "" : searchString.trim());
    }
    
    public String getSearchString()
    {
        return (this.searchString == null ? "" : this.searchString);
    }
    
    public void setFiltered(boolean filtered)
    {
        this.filtered = filtered;
    }
    
    public boolean getFiltered()
    {
        return this.filtered;
    }
    
    public void setSelectedUserName(String selectedUserName)
    {
        this.selectedUserName = selectedUserName;
    }
    
    public String getSelectedUserName()
    {
        return this.selectedUserName;
    }
    
    public void setUserNameList(List userNameList)
    {
        this.userNameList = userNameList;
    }
    
    public List getUserNameList()
    {
        return this.userNameList;
    }
    
    protected void onBeforeRender()
    {
        try
        {
            if (getFiltered())
            {
                PortletMessaging.publish(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED, "on");            
            }
            else
            {
                PortletMessaging.cancel(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_FILTERED);
            }

            this.userNameList = new ArrayList();
            Iterator users = getUserManager().getUserNames(getSearchString());

            while (users.hasNext())
            {
                this.userNameList.add(users.next());
            }
            
            String userName = (String) PortletMessaging.receive(getPortletRequest(), SecurityResources.TOPIC_USERS, SecurityResources.MESSAGE_SELECTED);
            setSelectedUserName(userName);
            
            if (userName != null)
            {
                DataTable userDataTable = (DataTable) get("userDataTable");
                
                if (userDataTable != null)
                {
                    int index = this.userNameList.indexOf(getSelectedUserName());
                    
                    if (index != -1)
                    {
                        int currentPage = Math.max(0, userDataTable.getRowCount() - 1) / userDataTable.getRowsPerPage();
                        userDataTable.setCurrentPage(currentPage);
                    }
                }
            }
        }
        catch (NotSerializableException e)
        {
        }
        catch (SecurityException e)
        {
            SecurityUtil.publishErrorMessage(getPortletRequest(), SecurityResources.TOPIC_USERS, e.getMessage());
        }                                    
        
        super.onBeforeRender();
    }
    
}