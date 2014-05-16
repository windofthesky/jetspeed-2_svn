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
package org.apache.jetspeed.tools.deploy;

import org.w3c.dom.Document;

/**
 * Utilities for manipulating the web.xml deployment descriptor version 2.5
 *
 * @author <a href="mailto:rwatler@apache.org">Randy Watler</a>
 * @version $Id: JetspeedWebApplicationRewriter2_5.java $
 */
public class JetspeedWebApplicationRewriter2_5 extends JetspeedWebApplicationRewriter2_4 {

    /**
     * Constructor taking XML document for web.xml and portlet application context name.
     *
     * @param doc parsed web.xml document
     * @param portletApplication portlet application context name
     */
    public JetspeedWebApplicationRewriter2_5(Document doc, String portletApplication) {
        super(doc, portletApplication);
    }

    /**
     * Constructor taking XML document for web.xml.
     *
     * @param doc parsed web.xml document
     */
    public JetspeedWebApplicationRewriter2_5(Document doc) {
        super(doc);
    }
}
