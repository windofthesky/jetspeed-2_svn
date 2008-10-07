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

package org.apache.cornerstone.framework.pubsub;

import java.util.*;
import org.apache.cornerstone.framework.api.pubsub.IPubSubManager;
import org.apache.cornerstone.framework.api.pubsub.ISubscriber;
import org.apache.cornerstone.framework.core.BaseObject;
import org.apache.log4j.Logger;

public class BasePubSubManager extends BaseObject implements IPubSubManager
{
    public static final String REVISION = "$Revision$";

    public static BasePubSubManager getSingleton()
    {
        return _Singleton;
    }

    // can have multiple publications per topic
    public void publish(String topic, Object publication)
    {
        List publicationList = (List) _publicationListMap.get(topic);
        if (publicationList == null)
        {
            publicationList = new ArrayList();
            _publicationListMap.put(topic, publicationList);
        }
        publicationList.add(publication);

        deliver(topic);
    }

    // only one subscriber per topic
    public void subscribe(String topic, ISubscriber subscriber)
    {
        _subscriberMap.put(topic, subscriber);
        List publicationList = (List) _publicationListMap.get(topic);
        if (publicationList != null)
        {
            deliver(topic);
        }
    }

    protected void deliver(String topic)
    {
        ISubscriber subscriber = (ISubscriber) _subscriberMap.get(topic);
        if (subscriber != null)
        {
            List publicationList = (List) _publicationListMap.get(topic);
            for (int i = 0; i < publicationList.size(); i++)
            {
                Object publication = publicationList.remove(0);
                subscriber.receive(publication);
            }
            
        }
    }

    private static BasePubSubManager _Singleton = new BasePubSubManager();
    private static Logger _Logger = Logger.getLogger(BasePubSubManager.class);
    protected Map _publicationListMap = new Hashtable();
    protected Map _subscriberMap = new Hashtable();
}