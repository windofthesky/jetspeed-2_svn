/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.jetspeed.decoration;

import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.jetspeed.util.Path;

/**
 * 
 * @author <href a="mailto:weaver@apache.org">Scott T. Weaver</a>
 *
 */
public class LayoutDecorationImpl extends BaseDecoration implements LayoutDecoration
{
    private DecorationFactory  decorationFactory;
    
    public LayoutDecorationImpl(Properties config, ResourceValidator validator, Path basePath, PathResolverCache cache)
    {
        super(config, validator, basePath, cache);
    }
    
    public void setDecorationFactory(DecorationFactory decorationFactory)
    {
        this.decorationFactory = decorationFactory;
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
