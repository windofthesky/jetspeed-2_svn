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

package org.apache.cornerstone.framework.mvc.action;

import java.util.ArrayList;
import java.util.List;

public class ActionResult
{
    public static final String REVISION = "$Revision$";

    public String getExitName()
    {
        return _exitName;
    }

    public void setExitName(String exitName)
    {
        _exitName = exitName;
    }

    public String getExitUrl()
    {
        return _exitUrl;
    }

    public void setExitUrl(String exitUrl)
    {
        _exitUrl = exitUrl;
    }

    public Object getPresentationBean()
    {
        return _pbean;
    }

    public void setPresentationBean(Object pbean)
    {
        _pbean = pbean;
    }

    public List getMessageList()
    {
        return _messageList;
    }

    public void addMessage(String message)
    {
        if (_messageList == null) _messageList = new ArrayList();
        _messageList.add(message);
    }

    public void combinePreviousResult(ActionResult previousResult)
    {
        if (previousResult != null)
        {
            List previousMessageList = previousResult.getMessageList();
            if (previousMessageList != null)
            {
                List combinedMessageList = previousMessageList;
                List currentMessageList = getMessageList();
                if (currentMessageList != null)
                    combinedMessageList.addAll(currentMessageList);
                _messageList = combinedMessageList;
            }
        }
    }

    protected String _exitName = BasePresentationAction.CONFIG_EXIT_DEFAULT;
    protected String _exitUrl;
    protected Object _pbean;
    protected List _messageList;
}