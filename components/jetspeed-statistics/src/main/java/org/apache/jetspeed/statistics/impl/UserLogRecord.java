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
 * UserLogRecord
 * <P>
 * Extends the abstract class LogRecord to holds the fields of a User Logout log
 * entry.
 * 
 * @author <a href="mailto:rklein@bluesunrise.com">Richard D. Klein </a>
 * @version $Id: LogRecord.java 188420 2005-03-23 22:25:50Z rdk $
 */
public class UserLogRecord extends LogRecord
{

    public UserLogRecord()
    {
        super(LogRecord.TYPE_USER);
    }
}
