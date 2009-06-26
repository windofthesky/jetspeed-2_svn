/* 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jetspeed.sso.impl;

import org.apache.commons.codec.binary.Base64;

public class SSOUtils
{
    /*
     * Simple encryption decryption routines since the API creates credentials 
     * together with an user.
     * TODO: re-implement when Security API is more flexible
     */
    private static char[] scrambler = "Jestspeed-2 is getting ready for release".toCharArray();
    
    public static String scramble(String pwd)
    {
        // xor-ing persistent String values is dangerous because of the (uncommon) way Java encodes UTF-8 0x00 (and some other characters).
        // See: http://en.wikipedia.org/wiki/UTF-8#Java
        // On some database platforms, like PostgreSQL this can lead to something like:
        //   org.postgresql.util.PSQLException: ERROR: invalid byte sequence for encoding "UTF8": 0x00
        // To prevent this, the resulting xored password is encoded in Base64
        String xored = new String(xor(pwd.toCharArray(), scrambler));
        byte[] bytes = Base64.encodeBase64(xored.getBytes());
        String scrambled = new String(bytes);
        return scrambled;
    }
    
    public static String unscramble(String pwd)
    {
        byte[] bytes = pwd.getBytes();
        bytes = Base64.decodeBase64(bytes);
        String chars = new String(bytes);
        String unscrambled = new String(xor(chars.toCharArray(), scrambler));
        return unscrambled;
    }
    
    private static char[] xor(char[] a, char[]b)
    {
        int len = Math.min(a.length, b.length);
        char[] result = new char[len];
        for (int i=0; (i < len); i++)
        {
            result[i] = (char) (a[i] ^ b[i]);
        }
        return result;
    }
}
