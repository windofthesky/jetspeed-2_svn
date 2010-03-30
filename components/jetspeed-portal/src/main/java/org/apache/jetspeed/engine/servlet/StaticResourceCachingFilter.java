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
package org.apache.jetspeed.engine.servlet;

import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticResourceCachingFilter implements Filter
{
    // constants

    private static final String HTTP_HEADER_EXPIRES = "Expires" ;
    private static final String HTTP_HEADER_CACHE_CONTROL = "Cache-Control" ;
    private static final String HTTP_HEADER_CACHE_MAX_AGE = "max-age" ;
    private static final String HTTP_HEADER_CACHE_MAX_AGE_EQ = "max-age=" ;

    private static String PARAM_EXPIRES_HOURS = "ExpireHours";

    private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone( "GMT" );


    // members
    
    private double expires_in_hours = 0.0;
    private int max_age = 0;

    // constructor

    public StaticResourceCachingFilter()
    {
        super() ;
    }


    // protocol

    public void init( FilterConfig config )
    {
        try
        {
            expires_in_hours = Double.parseDouble( config.getInitParameter( PARAM_EXPIRES_HOURS ) );
        }
        catch ( NumberFormatException ex )
        {
            expires_in_hours = 0;
        }
        max_age = (int)(expires_in_hours * 3600);
    }
    
    public void doFilter( ServletRequest aRequest, ServletResponse aResponse, FilterChain chain )
        throws java.io.IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest)aRequest;
        HttpServletResponse response = (HttpServletResponse)aResponse;
        if ( max_age > 0 )
        {
            String cacheControlVal = HTTP_HEADER_CACHE_MAX_AGE_EQ + max_age;
            response.setHeader( HTTP_HEADER_CACHE_CONTROL, cacheControlVal );
        }
        chain.doFilter( request, response );
    }

    public void destroy()
    {
    }


    /* unused (we're only doing Cache-Control max-age), but works for generating Expires header
    private String createExpiresHeader( int expiresInHours )
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US );
        sdf.setTimeZone( GMT_TIME_ZONE );
        Calendar cal = Calendar.getInstance();
        cal.add( Calendar.HOUR, expiresInHours );
        long millis = cal.getTimeInMillis();
        Date d = new Date( millis );
        return sdf.format( d );
    }
     */
}
