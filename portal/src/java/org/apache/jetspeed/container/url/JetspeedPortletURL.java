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


