/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2000-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Jetspeed" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Jetspeed", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.cornerstone.framework.util;

import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.cornerstone.framework.constant.Constant;
import org.apache.log4j.Logger;

public class Util
{
    public static final String REVISION = "$Revision$";

    public static final SimpleDateFormat _DateFormatter = new SimpleDateFormat("dd/MMM/yyyy");

    /**
     * Counts the number of occurances of the first char in cs in s.
     * @param s
     * @param cs
     * @return Number of occurances of the first char in cs in s.
     */
    public static int countChar(String s, String cs)
    {
        char c = cs.charAt(0);
        int count = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (c == s.charAt(i))
                count++;
        }
        return count;
    }

    /**
     * Converts deliminated strings to an array of strings.
     * @param strings
     * @return Array of strings deliminated.
     */
    public static String[] convertStringsToArray(String strings)
    {
        return convertStringsToArray(strings, Constant.TOKEN_DELIM);
    }

    public static String[] convertStringsToArray(String strings, String delim)
    {
        if (strings.trim().length() == 0)
        {
            return new String[0];
        }

        int nDelim = countChar(strings, delim);
        String[] a = new String[nDelim + 1];

        int i = 0;
        for (
            StringTokenizer st = new StringTokenizer(strings, delim);
            st.hasMoreTokens();
        )
        {
            String s = st.nextToken();
            a[i++] = s;
        }

        return a;
    }

    /**
     * Converts deliminated strings to a List of strings.
     * @param strings
     * @return List of strings deliminated.
     */
    public static List convertStringsToList(String strings)
    {
        List l = new ArrayList();

        if (strings != null)
        {
            for ( StringTokenizer st = new StringTokenizer(strings, Constant.TOKEN_DELIM); st.hasMoreTokens();)
            {
                l.add(st.nextToken());
            }
        }

        return l;
    }

    public static Set convertStringsToSet(String strings)
    {
        Set set = new HashSet();

        if (strings != null)
        {
            for ( StringTokenizer st = new StringTokenizer(strings, Constant.TOKEN_DELIM); st.hasMoreTokens();)
            {
                set.add(st.nextToken());
            }
        }

        return set;
    }

    public static String convertArrayToStrings(String[] array)
    {
        if (array == null) return null;

        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < array.length; i++)
        {
            buffer.append(array[i]);
            buffer.append(Constant.TOKEN_DELIM);
        }

        // Get rid of the trailing delimiter.
        if (buffer.length() > 0)
        {
            buffer.deleteCharAt(buffer.length() - 1);
        }

        return buffer.toString();
    }

    /**
     * Converts a list of strings to strings deliminated.
     * @param list
     * @return Strings deliminated.
     */
    public static String convertListToStrings(List list)
    {
        if (list == null)
        {
            return null;
        }

        StringBuffer buffer = new StringBuffer();

        try
        {
            for (Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                String string = (String) iterator.next();
                buffer.append(string);
                buffer.append(Constant.TOKEN_DELIM);
            }
        }
        catch (ClassCastException cce)
        {
            _Logger.error("Error: Trying to convert a list that is not Strings. Returning null.");
            return null;
        }


        // Get rid of the trailing delimiter.
        if (buffer.length() >0 )
        {
            buffer.deleteCharAt(buffer.length()-1);
        }

        return buffer.toString();
    }

    /**
     * Concatenates 2 paths using the path deliminator.
     * @param p1
     * @param p2
     * @return Concatenated path.
     */
    public static String concatPath(String p1, String p2)
    {
        return p1 + Constant.CONF_DELIM + p2;
    }

    /**
     * Concatenates 3 paths using the path deliminator.
     * @param p1
     * @param p2
     * @param p3
     * @return Concatenated path.
     */
    public static String concatPath(String p1, String p2, String p3)
    {
        return p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3;
    }

    /**
     * Concatenates 4 paths using the path deliminator.
     * @param p1
     * @param p2
     * @param p3
     * @param p4
     * @return Concatenated path.
     */
    public static String concatPath(String p1, String p2, String p3, String p4)
    {
        return p1 + Constant.CONF_DELIM + p2 + Constant.CONF_DELIM + p3 + Constant.CONF_DELIM + p4;
    }

    /**
     * Gets a random integer in range.
     * @param range
     * @return A random integer in range.
     */
    public static int getRandomInt(int range)
    {
        return (int) (Math.random() * range);
    }

    public static final String[] MONTH_NAME = {
        "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
        "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    /**
     * Converts a time stamp to the Cisco standard date format (DD-MMM-YYYY).
     * @param ts
     * @return Cisco standard date format.
     */
    public static String convertToStringDate(Timestamp ts)
    {
        if (ts == null) return null;

        int year = ts.getYear() + 1900;
        int month = ts.getMonth();
        int date = ts.getDate();

        return "" + date + "-" + MONTH_NAME[month] + "-" + year;
    }

    public static final SimpleDateFormat DATE_TIME_FORMAT =
        new SimpleDateFormat(" hh:mma z");

    /**
     * Converts a time stamp to time of format "hh:mma z"
     * @param ts
     * @return String of time.
     */
    public static String convertToStringDateTime(Timestamp ts)
    {
        return convertToStringDate(ts) + DATE_TIME_FORMAT.format(ts);
    }

    /**
     * Converts a date of format dd/MMM/yyyy to a timestamp.
     * @param dateString
     * @return Date string.
     */
    public static Timestamp convertStringToTimestamp(String dateString)
    {
        Date date = null;
        Timestamp resultDate = null;

        try
        {
            if ( dateString != null )
            {
                dateString = dateString.trim();
                if (dateString.length() > 0)
                {
                    date = _DateFormatter.parse(dateString);
                    resultDate = new Timestamp(date.getTime());
                }
            }
        }
        catch(ParseException pe)
        {
            _Logger.debug("FormatException: Expecting date string with the formalt dd/MMM/yyyy");
        }

        return resultDate;
    }

    /**
     * Converts a timestamp to a string of the format dd/MMM/YYYY.
     * @param date
     * @return Date string.
     */
    public static String convertTimestampToString(Timestamp date)
    {
        if (date == null)
            return "";
        else
            return _DateFormatter.format(date);
    }

    /**
     * Convert a double another with limited number of decimal places.
     * @param value
     * @param numPlaces
     * @return New double with limited number of decimal places.
     */
    public static double limitDecimalPlaces(double value, int numPlaces)
    {
        double power = Math.pow(10.0, numPlaces);
        long longValue = (long) (value * power);
        return ((double) longValue) / power;
    }

    /**
     * Removes all occurances of substring from string.
     * @param string
     * @param substring
     * @return String with substring removed.
     */
    public static String removeSubstrings(String string, String substring)
    {
        if (string.indexOf(substring) < 0)
            return string;

        int stringLength = string.length();
        int substringLength = substring.length();
        StringBuffer buf = new StringBuffer(string);
        for (int i = 0; i < stringLength; i++)
        {
            boolean equal = true;
            for (int j = 0; j < substringLength; j++)
            {
                if (buf.charAt(i + j) != substring.charAt(j))
                {
                    equal = false;
                    break;
                }
            }

            if (equal)
            {
                buf.delete(i, i + substringLength);
                stringLength -= substringLength;
            }
        }

        return buf.toString();
    }

    /**
     * @param words
     * @return Last word in a string of words separated by dot (.).
     */
    public static String getLastWord(String words)
    {
        int lastDot = words.lastIndexOf('.');
        if (lastDot < 0)
        {
            return words;
        }
        else
        {
            return words.substring(lastDot + 1);
        }
    }

    /**
     * Reads input stream completely into a string.
     * @param is input stream.
     * @return String read from input stream.
     */
    public static String getStringFromInputStream(InputStream is)
        throws Exception
    {
        byte[] byteBuffer = new byte[1024];
        StringBuffer stringBuffer = new StringBuffer();
        int dataSize = is.read(byteBuffer);
        while (dataSize >= 0)
        {
            if (dataSize > 0)
            {
                String s = new String(byteBuffer, 0, dataSize, "UTF-8");
                stringBuffer.append(s);
            }

            dataSize = is.read(byteBuffer);
        }

        return stringBuffer.toString();
    }

    public static Set convertEnumerationToSet(Enumeration e)
    {
        Set s = new HashSet();

        while (e != null && e.hasMoreElements())
        {
            s.add(e.nextElement());
        }

        return s;
    }

    public static String getRandomTimestamp()
    {
        long ts = System.currentTimeMillis();
        int suffix = (int) (Math.random() * 1000) + 1000;
        return ts + "-" + suffix;
    }

    public static boolean isEmpty(String s)
    {
        return s == null || s.trim().length() == 0;
    }

    private static Logger _Logger = Logger.getLogger(Util.class);
}