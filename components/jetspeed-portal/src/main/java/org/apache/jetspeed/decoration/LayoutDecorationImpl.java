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
package org.apache.jetspeed.decoration;

import java.util.Properties;

import org.apache.jetspeed.util.Path;

/**
 * Default implementation of <code>org.apache.jetspeed.decoration.LayoutDecoration</code>
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 * 
 * @see org.apache.jetspeed.decoration.LayoutDecoration
 *
 */
public class LayoutDecorationImpl extends BaseDecoration implements LayoutDecoration
{
    public LayoutDecorationImpl(Properties config, ResourceValidator validator, Path basePath, Path baseClientPath, PathResolverCache cache)
    {
        super(config, validator, basePath, baseClientPath, cache);
    }
    
    public void setDecorationFactory(DecorationFactory decorationFactory)
    {
        // TODO Ate: this seems like an obsolete constructor to me, no?
    }    

    public String getHeader()
    {
        String headerTemplate = config.getProperty("header", "header.vm");
        return getResource(headerTemplate);
    }

    public String getFooter()
    {
        String footerTemplate = config.getProperty("footer", "footer.vm");
        return getResource(footerTemplate);
    }

}
