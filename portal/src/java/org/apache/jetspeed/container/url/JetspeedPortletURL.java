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
package org.apache.jetspeed.container.url;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.PortletMode;


/**
 * This is the class implementing PortletURLs
 * Naive, rough first implementation
 * 
 */
public class JetspeedPortletURL implements PortletURL
{
    //protected String peid;
    //protected boolean isAction;
    protected StringBuffer url;
    protected boolean isSecure = false;
    protected HashMap parameters = new HashMap();

    protected JetspeedPortletURL( String contextPath, String portletID, boolean secure, boolean action )
    {
        //this.peid = portletID;
        this.isSecure = secure;
        //this.isAction = action;
        this.url = new StringBuffer( 50 );
        url.append( "http://" );
        url.append( contextPath );
        url.append( "/portal" );
        if( action )
        {
            url.append( "/action/true" );
        }
        url.append( "/peid/");
        url.append( portletID );
    }

    public void setWindowState( WindowState state )
    {
        //TBD: Substitute by an implementation
        //throw new IllegalStateException("Not implemented yet");
        url.append( "/state/" );
        url.append( state );
    }

    public void setPortletMode( PortletMode mode )
    {
        //TBD: Substitute by an implementation
        //throw new IllegalStateException("Not yet implemented. Please write it! ;-)");
        url.append( "/mode/" );
        url.append( mode );
        
    }

    public void setParameter( String name, String value )
    {
        //TBD: Substitute by an implementation
        throw new IllegalStateException("Not yet implemented. Please write it! ;-)");
    }

    public void setParameter( String name, String[] value )
    {
        //TBD: Substitute by an implementation
        throw new IllegalStateException("Not yet implemented. Please write it! ;-)");
    }

    public void setParameters( Map params )
    {
        //TBD: Substitute by an implementation
        throw new IllegalStateException("Not yet implemented. Please write it! ;-)");
    }

    public void setSecure( boolean secure )
    {
        //TBD: Substitute by an implementation
        //throw new IllegalStateException("Not yet implemented. Please write it! ;-)");
        this.isSecure = secure;
    }

    public String toString()
    {
        if( this.isSecure )
        {
            url.insert(4, 's');
        }
        //TBD: should we nuke/reinit the object? The spec does not specify it.
        return url.toString();
    }

    public void addParameter(String name, String value)
    {
        parameters.put( name, value);
    }

    public void addParameter (String name, String[] values)
    {
        parameters.put( name, values);
    }
    
    public String getParameter(String name)
    {
        return (String)parameters.get(name);
    }

    public String[] getParameters(String name)
    {
        return (String[])parameters.get(name);
    }

}


