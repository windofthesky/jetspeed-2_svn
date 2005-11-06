/*
 * Copyright 2000-2001,2004 The Apache Software Foundation.
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
package org.apache.jetspeed.util;

import junit.framework.TestCase;

public class TestPathUtil extends TestCase
{
    public void testPath()
    {
        Path path = new Path("/root/sub1/sub2/file.html?foo=bar&name=bob");
        
        Path path2 = new Path("/root/sub1/sub2/file.html?foo=bar&name=bob");
        
        assertEquals(path, path2);
        
        assertEquals(".html", path.getFileExtension());
        assertEquals("foo=bar&name=bob", path.getQueryString());
        assertEquals("file.html", path.getFileName());
        assertEquals("file", path.getBaseName());
        
        assertEquals(4, path.length());
        
        assertEquals("root", path.getSegment(0));
        assertEquals("sub1", path.getSegment(1));
        assertEquals("sub2", path.getSegment(2));
        assertEquals("file.html", path.getSegment(3));  
        assertEquals("/root/sub1/sub2/file.html", path.pathOnly());
        
        assertEquals("/sub1/sub2/file.html?foo=bar&name=bob", path.getSubPath(1).toString());
        
        path = new Path("file.html");
        assertEquals(".html", path.getFileExtension());
        assertEquals("file.html", path.getFileName());
        assertEquals("file", path.getBaseName());
        
        assertNull(path.getQueryString());
        
        assertEquals(1, path.length());
        assertEquals("file.html", path.getSegment(0));  
        
        path = new Path("file");
        
        assertNull(path.getBaseName());
        
        Path pathNoFile = new Path("/root/sub1/sub2");
        assertEquals("root", pathNoFile.getSegment(0));
        assertEquals("sub1", pathNoFile.getSegment(1));
        assertEquals("sub2", pathNoFile.getSegment(2));
        
        assertEquals("/sub1/sub2", pathNoFile.getSubPath(1).toString());
                
    }
}
