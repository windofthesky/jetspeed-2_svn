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
package org.apache.portals.gems.util;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * ValidationHelper using regular expressions
 *
 * @author <a href="mailto:taylor@apache.org">David Sean Taylor</a>
 * @version $Id: $
 */
public abstract class ValidationHelper
{
    public static SimpleDateFormat EUROPEAN_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static SimpleDateFormat EUROPEAN_DATETIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public static SimpleDateFormat AMERICAN_DATE_FORMAT = new SimpleDateFormat("MM-dd-yyyy");
    public static SimpleDateFormat AMERICAN_DATETIME_FORMAT = new SimpleDateFormat("MM-dd-yyyy HH:mm");
    
    /**
     * Tests that the input string contains only alpha numeric or white spaces
     * <P>
     * @param evalString The string that is to be evaluated
     * @param required indicates whether the field is required or not
     * @return True if the input is alpha numeric, false otherwise.
     **/
    public static boolean isAlphaNumeric(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }        
        return evalString.matches("^[\\w\\s]+$");
    }

    public static boolean isAlphaNumeric(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isAlphaNumeric(evalString, required);
    }

    public static boolean isLooseAlphaNumeric(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        return evalString.matches("^[\\w\\s\\.\\,\\/\\-\\(\\)\\+]+$");
    }

    public static boolean isLooseAlphaNumeric(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isLooseAlphaNumeric(evalString, required);
    }
        
    /**
     * Tests that the input string contains only numeric
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is numeric, false otherwise.
     **/
    public static boolean isDecimal(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        return evalString.matches("^(\\d+\\.)?\\d+$");
    }
    
    public static boolean isDecimal(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isDecimal(evalString, required);
    }

    /**
     * Tests that the input string contains only an integer
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is numeric, false otherwise.
     **/
    public static boolean isInteger (String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        return evalString.matches("^\\d+$");
    }
    
    public static boolean isInteger(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isInteger(evalString, required);
    }

    /**
     * Tests that the input string contains a valid email addess
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is a valid email address.
     **/
    public static boolean isEmailAddress(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        return evalString.matches("^(?:\\w[\\w-]*\\.)*\\w[\\w-]*@(?:\\w[\\w-]*\\.)+\\w[\\w-]*$");
    }
    
    public static boolean isEmailAddress(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isEmailAddress(evalString, required);
    }
    
    /**
     * Tests that the input string contains a valid URL
     * <P>
     * @param evalString The string that is to be evaluated
     * @return True if the input is a valid URL.
     **/
    public static boolean isURL(String evalString, boolean required)
    {
        try
        {
            if (StringUtils.isEmpty(evalString))
            {
                if (true == required)
                {
                    return false;
                }
                return true;
            }
            
            URL url = new URL(evalString);

            /*
            Perl5Util util = new Perl5Util();
            System.out.println("looking at " +evalString);
            return evalString.matches("^[\\w%?-_~]$", evalString);
             */
            //Object content = url.getContent();
            //System.out.println("url contains :["+content+"]");
            return true;
        }
        catch (Exception e)
        {
            System.err.println(evalString+" is not a valid URL: "+ e);
            return false;
        }
    }

    public static boolean isURL(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isURL(evalString, required);
    }

    public static boolean isValidIdentifier(String folderName)
    {
        boolean valid = true;

        char[] chars = folderName.toCharArray();
        for (int ix = 0; ix < chars.length; ix++)
        {
            if (!Character.isJavaIdentifierPart(chars[ix]))
            {
                valid = false; 
                break;
            }
        }
        return valid;
    }

    public static boolean isTooLong(String evalString, int maxLength)
    {
        if (null != evalString)
        {
            return (evalString.length() > maxLength);
        }
        return false;
    }

    public static boolean isPhoneNumber(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isPhoneNumber(evalString, required);
    }
    
    public static boolean isPhoneNumber(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        //return evalString.matches("[(][0-9]{3}[)][ ]*[0-9]{3}-[0-9]{4}", evalString);
        return evalString.matches("(\\+[0-9]{2})?(\\({0,1}[0-9]{3}\\){0,1} {0,1}[ |-]{0,1} {0,1}){0,1}[0-9]{3,5}[ |-][0-9]{4,6}");
    }

    public static Date parseDate(String formatted)
    {
        Date date = null;
        if (null == formatted)
        {
            return null;
        }
        try
        {
            date = EUROPEAN_DATE_FORMAT.parse(formatted);
        }
        catch (ParseException e)
        {
            try
            {
                date = AMERICAN_DATE_FORMAT.parse(formatted);
            }
            catch (ParseException ee)
            {
            }            
        }
        return date;
    }

    public static Date parseDatetime(String formatted)
    {
        Date date = null;
        if (null == formatted)
        {
            return null;
        }
        
        try
        {
            date = EUROPEAN_DATETIME_FORMAT.parse(formatted);
        }
        catch (ParseException e)
        {
            try
            {
                date = AMERICAN_DATETIME_FORMAT.parse(formatted);
            }
            catch (ParseException ee)
            {
            }            
        }
        return date;
    }
    
    public static String formatEuropeanDate(Date date)
    {
        if (null == date)
        {
            return null;
        }
        return EUROPEAN_DATE_FORMAT.format(date);        
    }
    
    public static String formatAmericanDate(Date date)
    {
        if (null == date)
        {
            return null;
        }        
        return AMERICAN_DATE_FORMAT.format(date);        
    }

    public static String formatEuropeanDatetime(Date date)
    {
        if (null == date)
        {
            return null;
        }        
        return EUROPEAN_DATETIME_FORMAT.format(date);        
    }
    
    public static String formatAmericanDatetime(Date date)
    {
        if (null == date)
        {
            return null;
        }        
        return AMERICAN_DATETIME_FORMAT.format(date);        
    }
    
    public static boolean isValidDate(String formatted)
    {
        if (formatted == null || formatted.trim().length() == 0)
            return true;
            
        try
        {
            Date date = EUROPEAN_DATE_FORMAT.parse(formatted);
        }
        catch (ParseException e)
        {
            try
            {
                Date date = AMERICAN_DATE_FORMAT.parse(formatted);
            }
            catch (ParseException ee)
            {
                return false;
            }            
        }
        return true;        
    }
    
    public static boolean isValidDatetime(String formatted)
    {
        if (formatted == null || formatted.trim().length() == 0)
            return true;
            
        try
        {
            Date date = EUROPEAN_DATETIME_FORMAT.parse(formatted);
        }
        catch (ParseException e)
        {
            try
            {
                Date date = AMERICAN_DATETIME_FORMAT.parse(formatted);
            }
            catch (ParseException ee)
            {
                return false;
            }            
        }
        return true;        
    }

    public static boolean isAny(String evalString, boolean required)
    {
        if (StringUtils.isEmpty(evalString))
        {
            if (true == required)
            {
                return false;
            }
            return true;
        }
        return true;
    }

    public static boolean isAny(String evalString, boolean required, int maxLength)
    {
        if (isTooLong(evalString, maxLength))
        {
            return false;
        }
        return isAny(evalString, required);
    }
    
}
