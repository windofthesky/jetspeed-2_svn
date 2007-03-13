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
package org.apache.jetspeed.security.util;

import junit.framework.TestCase;

/**
 * <p>
 * TestPBEPasswordTool
 * </p>
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class TestPBEPasswordTool extends TestCase
{

    /*
     * Test method for 'org.apache.jetspeed.security.util.PBEPasswordTool.encode(String, String)'
     */
    public void testEncode() throws Exception
    {
        PBEPasswordTool pbe = new PBEPasswordTool("123");
        // check the same password is encoded differently for different usernames
        assertNotSame("Encoded password should not be the same for different users", pbe.encode("user1","abc123"), pbe.encode("user2","abc123"));
    }

    /*
     * Test method for 'org.apache.jetspeed.security.util.PBEPasswordTool.decode(String, String)'
     */
    public void testDecode() throws Exception
    {
        PBEPasswordTool pbe = new PBEPasswordTool("123");
        // check the same password is encoded differently for different usernames
        assertEquals("Decoded password doesn't match original", "abc123", pbe.decode("user1", pbe.encode("user1","abc123")));
    }

}
