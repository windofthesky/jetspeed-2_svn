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

public class ProfileCriterionBean {
    private String name;
    private String value;
    private String resolverType;
    private int fallback;
    private int order;

    public ProfileCriterionBean() {}

    public ProfileCriterionBean(String name, String value, String resolverType, int fallback, int order) {
        this.name = name;
        this.value = value;
        this.resolverType = resolverType;
        this.fallback = fallback;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getResolverType() {
        return resolverType;
    }

    public void setResolverType(String resolverType) {
        this.resolverType = resolverType;
    }

    public int getFallback() {
        return fallback;
    }

    public void setFallback(int fallback) {
        this.fallback = fallback;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
