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
package org.apache.portals.bridges.struts.util;

import java.util.Map;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * DispatchedHttpServletRequestWrapper
 * 
 * @author <a href="mailto:ate@douma.nu">Ate Douma</a>
 * @version $Id$
 */
public class DispatchedHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private static final Log logger = LogFactory.getLog(DispatchedHttpServletRequestWrapper.class);

    /**
     * The request parameters for this request. This is only initialized from
     * the wrapped request when queryString parameters are specified (which are
     * merged on top of them), otherwise the wrapped request parameters will be
     * used.
     */
    private Map parameters;

    public DispatchedHttpServletRequestWrapper(HttpServletRequest request, String queryString) {
        super(request);

        parameters = mergeParameters(getRequest().getParameterMap(), queryString, getRequest().getCharacterEncoding());
        if (parameters != null) parameters = Collections.unmodifiableMap(parameters);
    }

    // The below implementation of handling wrapped parameters and merging
    // query string parameters is borrowed from the
    // Tomcat 4.1.29 catalina core classes
    // org.apache.catalina.core.ApplicationHttpRequest and
    // org.apache.catalina.util.RequestUtils.

    /**
     * Override the <code>getParameter()</code> method of the wrapped
     * request. When no queryString is specified on instantiation the wrapped
     * request will handle it.
     * 
     * @param name
     *            Name of the requested parameter
     */
    public String getParameter(String name) {

        if (parameters != null) {
            Object value = parameters.get(name);
            if (value == null)
                return (null);
            else if (value instanceof String[])
                return (((String[]) value)[0]);
            else if (value instanceof String)
                return ((String) value);
            else
                return (value.toString());
        } else
            return getRequest().getParameter(name);
    }

    /**
     * Override the <code>getParameterMap()</code> method of the wrapped
     * request. When no query string parameters are specified on instantiation
     * the wrapped request will handle it.
     */
    public Map getParameterMap() {

        if (parameters != null)
            return (parameters);
        else
            return getRequest().getParameterMap();

    }

    /**
     * Override the <code>getParameterNames()</code> method of the wrapped
     * request. When no query string parameters are specified on instantiation
     * the wrapped request will handle it.
     */
    public Enumeration getParameterNames() {

        if (parameters != null) {
            return Collections.enumeration(parameters.keySet());
        } else
            return getRequest().getParameterNames();
    }

    /**
     * Override the <code>getParameterValues()</code> method of the wrapped
     * request. When no query string parameters are specified on instantiation
     * the wrapped request will handle it.
     * 
     * @param name
     *            Name of the requested parameter
     */
    public String[] getParameterValues(String name) {

        if (parameters != null) {
            Object value = parameters.get(name);
            if (value == null)
                return ((String[]) null);
            else if (value instanceof String[])
                return ((String[]) value);
            else if (value instanceof String) {
                String values[] = new String[1];
                values[0] = (String) value;
                return (values);
            } else {
                String values[] = new String[1];
                values[0] = value.toString();
                return (values);
            }
        } else
            return getRequest().getParameterValues(name);
    }

    /**
     * Merge the parameters from the specified query string (if any), and a
     * supplied map of parameters (if any), such that the parameter values from
     * the query string show up first if there are duplicate parameter names.
     * 
     * @param parameters
     *            The original parameters map
     * @param queryString
     *            The query string containing parameters to be merged
     * @param encoding
     *            The current encoding of the query string
     * @return null when no changes where made to the original parameters map
     *         or the merged map of parameters
     */
    private static Map mergeParameters(Map parameters, String queryString, String encoding) {

        Map resultMap = null;
        if ((queryString != null) && (queryString.length() > 0)) {

            HashMap queryParameters = new HashMap();
            if (encoding == null) encoding = "ISO-8859-1";
            try {
                parseParameters(queryParameters, queryString, encoding);
            } catch (Exception e) {
                ;
            }
            if (!queryParameters.isEmpty()) {
                if (!parameters.isEmpty()) {
                    Iterator keys = parameters.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        Object value = queryParameters.get(key);
                        if (value == null) {
                            queryParameters.put(key, parameters.get(key));
                            continue;
                        }
                        queryParameters.put(key, mergeValues(value, parameters.get(key)));
                    }
                }
                resultMap = queryParameters;
            }
        }
        return resultMap;
    }

    /**
     * Merge the two sets of parameter values into a single String array.
     * 
     * @param values1
     *            First set of values
     * @param values2
     *            Second set of values
     */
    private static String[] mergeValues(Object values1, Object values2) {

        ArrayList results = new ArrayList();

        if (values1 == null)
            ;
        else if (values1 instanceof String)
            results.add(values1);
        else if (values1 instanceof String[]) {
            String values[] = (String[]) values1;
            for (int i = 0; i < values.length; i++)
                results.add(values[i]);
        } else
            results.add(values1.toString());

        if (values2 == null)
            ;
        else if (values2 instanceof String)
            results.add(values2);
        else if (values2 instanceof String[]) {
            String values[] = (String[]) values2;
            for (int i = 0; i < values.length; i++)
                results.add(values[i]);
        } else
            results.add(values2.toString());

        String values[] = new String[results.size()];
        return ((String[]) results.toArray(values));

    }

    /**
     * Append request parameters from the specified String to the specified
     * Map. It is presumed that the specified Map is not accessed from any
     * other thread, so no synchronization is performed.
     * <p>
     * <strong>IMPLEMENTATION NOTE </strong>: URL decoding is performed
     * individually on the parsed name and value elements, rather than on the
     * entire query string ahead of time, to properly deal with the case where
     * the name or value includes an encoded "=" or "&" character that would
     * otherwise be interpreted as a delimiter.
     * 
     * @param map
     *            Map that accumulates the resulting parameters
     * @param data
     *            Input string containing request parameters
     * @param urlParameters
     *            true if we're parsing parameters on the URL
     * 
     * @exception IllegalArgumentException
     *                if the data is malformed
     */
    private static void parseParameters(Map map, String data, String encoding) throws UnsupportedEncodingException {

        if ((data != null) && (data.length() > 0)) {

            // use the specified encoding to extract bytes out of the
            // given string so that the encoding is not lost. If an
            // encoding is not specified, let it use platform default
            byte[] bytes = null;
            try {
                if (encoding == null) {
                    bytes = data.getBytes();
                } else {
                    bytes = data.getBytes(encoding);
                }
            } catch (UnsupportedEncodingException uee) {
            }

            parseParameters(map, bytes, encoding);
        }

    }

    /**
     * Convert a byte character value to hexidecimal digit value.
     * 
     * @param b
     *            the character value byte
     */
    private static byte convertHexDigit(byte b) {
        if ((b >= '0') && (b <= '9')) return (byte) (b - '0');
        if ((b >= 'a') && (b <= 'f')) return (byte) (b - 'a' + 10);
        if ((b >= 'A') && (b <= 'F')) return (byte) (b - 'A' + 10);
        return 0;
    }

    /**
     * Put name value pair in map.
     * 
     * @param b
     *            the character value byte
     * 
     * Put name and value pair in map. When name already exist, add value to
     * array of values.
     */
    private static void putMapEntry(Map map, String name, String value) {
        String[] newValues = null;
        String[] oldValues = (String[]) map.get(name);
        if (oldValues == null) {
            newValues = new String[1];
            newValues[0] = value;
        } else {
            newValues = new String[oldValues.length + 1];
            System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
            newValues[oldValues.length] = value;
        }
        map.put(name, newValues);
    }

    /**
     * Append request parameters from the specified String to the specified
     * Map. It is presumed that the specified Map is not accessed from any
     * other thread, so no synchronization is performed.
     * <p>
     * <strong>IMPLEMENTATION NOTE </strong>: URL decoding is performed
     * individually on the parsed name and value elements, rather than on the
     * entire query string ahead of time, to properly deal with the case where
     * the name or value includes an encoded "=" or "&" character that would
     * otherwise be interpreted as a delimiter.
     * 
     * NOTE: byte array data is modified by this method. Caller beware.
     * 
     * @param map
     *            Map that accumulates the resulting parameters
     * @param data
     *            Input string containing request parameters
     * @param encoding
     *            Encoding to use for converting hex
     * 
     * @exception UnsupportedEncodingException
     *                if the data is malformed
     */
    private static void parseParameters(Map map, byte[] data, String encoding) throws UnsupportedEncodingException {

        if (data != null && data.length > 0) {
            int pos = 0;
            int ix = 0;
            int ox = 0;
            String key = null;
            String value = null;
            while (ix < data.length) {
                byte c = data[ix++];
                switch ((char) c) {
                case '&':
                    value = new String(data, 0, ox, encoding);
                    if (key != null) {
                        putMapEntry(map, key, value);
                        key = null;
                    }
                    ox = 0;
                    break;
                case '=':
                    if (key == null) {
                        key = new String(data, 0, ox, encoding);
                        ox = 0;
                    } else {
                        data[ox++] = c;
                    }
                    break;
                case '+':
                    data[ox++] = (byte) ' ';
                    break;
                case '%':
                    data[ox++] = (byte) ((convertHexDigit(data[ix++]) << 4) + convertHexDigit(data[ix++]));
                    break;
                default:
                    data[ox++] = c;
                }
            }
            //The last value does not end in '&'. So save it now.
            if (key != null) {
                value = new String(data, 0, ox, encoding);
                putMapEntry(map, key, value);
            }
        }

    }
}
