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
package org.apache.jetspeed.container.namespace;

import org.apache.pluto.util.NamespaceMapper;

/**
 * Jetspeed version of the Factory implementation for the NamespaceMapper
 *
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id: JetspeedNamespaceMapperFactoryImpl.java 188577 2005-05-17 15:57:13Z ate $
 */
public class JetspeedNamespaceMapperFactoryImpl implements JetspeedNamespaceMapperFactory
{
    private JetspeedNamespaceMapper namespaceMapper;
    
    public void init(javax.servlet.ServletConfig config, java.util.Map properties) throws Exception
    {
        namespaceMapper = (JetspeedNamespaceMapper)properties.get("JetspeedNamespaceMapper");
    }
    
    public void destroy() throws Exception
    {
    }

    public NamespaceMapper getNamespaceMapper()
    {
        return namespaceMapper;
    }

    public JetspeedNamespaceMapper getJetspeedNamespaceMapper()
    {
        return namespaceMapper;
    }
}
