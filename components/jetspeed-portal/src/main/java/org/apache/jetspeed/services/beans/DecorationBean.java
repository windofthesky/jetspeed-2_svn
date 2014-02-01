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

import org.apache.jetspeed.decoration.Decoration;
import org.apache.jetspeed.decoration.DecoratorAction;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * DecorationBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="decoration")
public class DecorationBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String styleSheet;
    private String basePath;
    private String baseCSSClass;
    private Decoration.ActionsOption actionsOption;
    private Collection<DecoratorActionBean> actionBeans;
    
    public DecorationBean()
    {
        
    }
    
    public DecorationBean(final Decoration decoration)
    {
        name = decoration.getName();
        styleSheet = decoration.getStyleSheet();
        basePath = decoration.getBasePath();
        baseCSSClass = decoration.getBaseCSSClass();
        actionsOption = decoration.getActionsOption();
        actionBeans = new ArrayList<DecoratorActionBean>();
        
        for (DecoratorAction action : decoration.getActions())
        {
            actionBeans.add(new DecoratorActionBean(action));
        }
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStyleSheet()
    {
        return styleSheet;
    }

    public void setStyleSheet(String styleSheet)
    {
        this.styleSheet = styleSheet;
    }

    public String getBasePath()
    {
        return basePath;
    }

    public void setBasePath(String basePath)
    {
        this.basePath = basePath;
    }

    public String getBaseCSSClass()
    {
        return baseCSSClass;
    }

    public void setBaseCSSClass(String baseCSSClass)
    {
        this.baseCSSClass = baseCSSClass;
    }

    public String getActionsOption()
    {
        return actionsOption.toString();
    }

    public void setActionsOption(String actionsOption)
    {
        this.actionsOption = Decoration.ActionsOption.valueOf(actionsOption);
    }

    @XmlElementWrapper(name="decoratorActions")
    @XmlElements(@XmlElement(name="decoratorAction"))
    public Collection<DecoratorActionBean> getActionBeans()
    {
        return actionBeans;
    }

    public void setActionBeans(Collection<DecoratorActionBean> actionBeans)
    {
        this.actionBeans = actionBeans;
    }
    
}
