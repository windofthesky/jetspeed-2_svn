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

import org.apache.jetspeed.decoration.DecoratorAction;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * DecoratorActionBean
 * 
 * @version $Id$
 */
@XmlRootElement(name="decoratorAction")
public class DecoratorActionBean implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    private String action;
    private String actionName;
    private String actionType;
    private String alt;
    private String link;
    private String name;
    private String target;
    
    public DecoratorActionBean()
    {
        
    }
    
    public DecoratorActionBean(final DecoratorAction decoratorAction)
    {
        action = decoratorAction.getAction();
        actionName = decoratorAction.getActionName();
        actionType = decoratorAction.getActionType();
        alt = decoratorAction.getAlt();
        link = decoratorAction.getLink();
        name = decoratorAction.getName();
        target = decoratorAction.getTarget();
    }

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getActionName()
    {
        return actionName;
    }

    public void setActionName(String actionName)
    {
        this.actionName = actionName;
    }

    public String getActionType()
    {
        return actionType;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public String getAlt()
    {
        return alt;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }

    public String getLink()
    {
        return link;
    }

    public void setLink(String link)
    {
        this.link = link;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTarget()
    {
        return target;
    }

    public void setTarget(String target)
    {
        this.target = target;
    }
    
}
