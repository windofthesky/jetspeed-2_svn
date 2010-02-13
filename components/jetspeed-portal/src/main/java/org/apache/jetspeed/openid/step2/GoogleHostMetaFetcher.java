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
package org.apache.jetspeed.openid.step2;

import com.google.step2.discovery.UrlHostMetaFetcher;
import com.google.step2.http.HttpFetcher;

import java.net.URI;
import java.net.URISyntaxException;

public class GoogleHostMetaFetcher extends UrlHostMetaFetcher
{
    private static final String GOOGLE_HOSTED_HOST_META_URI = "https://www.google.com/accounts/o8/.well-known/host-meta";
    private static final String GOOGLE_HOSTED_HOST_META_HOST_PARAM = "hd";

    public GoogleHostMetaFetcher(HttpFetcher fetcher)
    {
        super(fetcher);
    }

    protected URI getHostMetaUriForHost(String host) throws URISyntaxException
    {
        return new URI(GOOGLE_HOSTED_HOST_META_URI+"?"+GOOGLE_HOSTED_HOST_META_HOST_PARAM+"="+host);
    }
}
