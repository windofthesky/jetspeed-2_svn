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
package org.apache.jetspeed.administration;

import java.util.ArrayList;

import javax.servlet.jsp.JspException;

import org.apache.jetspeed.om.folder.Folder;
import org.apache.taglibs.random.RandomStrg;

/**
 * Helper for admininstration
 * 
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @author <a href="mailto:chris@bluesunrise.com">Chris Schaefer</a>
 * @version $Id: $
 */
public class AdminUtil
{
    /** the list of characters from which a password can be generatored. */
    protected static final char[] PASS_CHARS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
        'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
        'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        // removed these for aesthetic purposes
        //'!', '&',  '-', '_', '=',
        // '*','@', '#', '$', '%', '^',
        //'+',

    public String generatePassword()
    {
        RandomStrg rs = new RandomStrg();
        
        //TODO put in a more secure random number provider
        //rs.setAlgorithm();   -- ideally call this for super security.  need rnd provider
        
        try
        {
            rs.generateRandomObject();
        } catch (JspException e)
        {
            // this would only get thrown if we tried a secure random and the provider
            // was not available.
            e.printStackTrace();
        }
        rs.setLength(new Integer(12));
        rs.setSingle(PASS_CHARS,PASS_CHARS.length);
        ArrayList upper = new ArrayList();
        ArrayList lower = new ArrayList();
        //upper.add(new Character('A'));
        //lower.add(new Character('B'));
        rs.setRanges(upper,lower);
        String retval = rs.getRandom();
        
        return retval;        
    }
    
    protected String concatenatePaths(String base, String path)
    {
        String result = "";
        if (base == null)
        {
            if (path == null)
            {
                return result;
            }
            return path;
        }
        else
        {
            if (path == null)
            {
                return base;
            }
        }
        if (base.endsWith(Folder.PATH_SEPARATOR)) 
        {
            if (path.startsWith(Folder.PATH_SEPARATOR))
            {
                result = base.concat(path.substring(1));
                return result;
            }
        
        }
        else
        {
            if (!path.startsWith(Folder.PATH_SEPARATOR)) 
            {
                result = base.concat(Folder.PATH_SEPARATOR).concat(path);
                return result;
            }
        }
        return base.concat(path);
    }
    
}
