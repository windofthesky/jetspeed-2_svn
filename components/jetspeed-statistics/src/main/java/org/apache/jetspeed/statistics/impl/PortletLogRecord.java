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
package org.apache.jetspeed.statistics.impl;

/**
 * PortletLogRecord
 * <P>
 * Extends the abstract class LogRecord to holds the fields of a Portlet Access
 * log entry.
 * 
 * @author <a href="mailto:rklein@bluesunrise.com">Richard D. Klein </a>
 * @version $Id: LogRecord.java 188420 2005-03-23 22:25:50Z rdk $
 */
public class PortletLogRecord extends LogRecord
{

    protected String portletName = null;

    protected String pagePath = null;

    public PortletLogRecord()
    {
        super(LogRecord.TYPE_PORTLET);
    }

    /**
     * @return Returns the pagePath.
     */
    public String getPagePath()
    {
        return pagePath;
    }

    /**
     * @param pagePath
     *            The pagePath to set.
     */
    public void setPagePath(String pagePath)
    {
        this.pagePath = pagePath;
    }

    /**
     * @return Returns the portletName.
     */
    public String getPortletName()
    {
        return portletName;
    }

    /**
     * @param portletName
     *            The portletName to set.
     */
    public void setPortletName(String portletName)
    {
        this.portletName = portletName;
    }
}
