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
package org.apache.jetspeed.util.ojb;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.accesslayer.QueryCustomizerDefaultImpl;
import org.apache.ojb.broker.metadata.CollectionDescriptor;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;

public class PortletCloneQueryCustomizer extends QueryCustomizerDefaultImpl {

    private static final long serialVersionUID = -1933868940330442307L;
    private static final String CLONE_FIELD = "isClone";

    public Query customizeQuery(Object owner, PersistenceBroker broker, CollectionDescriptor collDescriptor, QueryByCriteria query)
    {
        String isClone = this.getAttribute(CLONE_FIELD, "false");
        if (isClone.equals("false"))
        {
            query.getCriteria().addIsNull("cloneParent");
        }
        else
        {
            query.getCriteria().addNotNull("cloneParent");
        }
        return query;
    }
}
