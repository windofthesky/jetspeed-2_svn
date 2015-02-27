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

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for profile search result list to be displayed in the view. The object will
 * be transformed to JSON to be transferred to the JS client.
 *
 */
@XmlRootElement(name = "profile")
public class ProfileEditBean extends ProfileListBean implements Serializable {

    private List<ProfileCriterionBean> criteria = new ArrayList<>();

    public ProfileEditBean() {}

    public ProfileEditBean(String id, String title, String concreteClass) {
        super(id, title, concreteClass);
    }

    public void add(ProfileCriterionBean bean) {
        criteria.add(bean);
    }

    public List<ProfileCriterionBean> getCriteria() {
        return criteria;
    }

    public void setCriteria(List<ProfileCriterionBean> criteria) {
        this.criteria = criteria;
    }
}
