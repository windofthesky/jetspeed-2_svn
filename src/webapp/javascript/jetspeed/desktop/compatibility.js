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

// jetspeed javascript to help support portlets in both /portal and /desktop

if ( window.dojo )
{
    dojo.provide( "jetspeed.desktop.compatibility" );
}

// ... jetspeed base object
if ( ! window.jetspeed )
    jetspeed = {};


if ( ! window.dojo )
{
    jetspeed.no_dojo_load_notifying = false;
    jetspeed.no_dojo_post_load = false;
    jetspeed.pageLoadedListeners = [];

    window.onload = function()
    {
        if ( ! window.dojo )
        {
            jetspeed.no_dojo_load_notifying = true;
            jetspeed.no_dojo_post_load = true;
            var pll = jetspeed.pageLoadedListeners;
	        for( var x=0; x < pll.length; x++ )
            {
		        pll[x]();
	        }
            jetspeed.pageLoadedListeners = [];
        }
    };
};

/*
Call styles:
	jetspeed.addOnLoad( functionPointer )
	jetspeed.addOnLoad( object, "functionName" )
*/
jetspeed.addOnLoad = function( obj, fcnName )
{
    if ( window.dojo )
    {
        if ( arguments.length == 1 )
            dojo.addOnLoad( obj );
        else
            dojo.addOnLoad( obj, fcnName );
    }
    else
    {
	    if ( arguments.length == 1 )
        {
		    jetspeed.pageLoadedListeners.push(obj);
	    }
        else if( arguments.length > 1 )
        {
		    jetspeed.pageLoadedListeners.push( function()
            {
			    obj[fcnName]();
		    } );
	    }
        if ( jetspeed.no_dojo_post_load && ! jetspeed.no_dojo_load_notifying )
        {
		    jetspeed.callPageLoaded();
	    }
    }
};

jetspeed.callPageLoaded = function()
{
	if( typeof setTimeout == "object" )  // IE
    {
		setTimeout( "jetspeed.pageLoaded();", 0 );
	}
    else
    {
		jetspeed.pageLoaded();
	}
};

jetspeed.printobj = function( obj )
{
    var props = [];
    for( var prop in obj )
    {
        try
        {
            props.push( prop + ': ' + obj[prop] );
        }
        catch(E)
        {
            props.push( prop + ': ERROR - ' + E.message );
        }
    }
    props.sort();
    var buff = "";
    for( var i = 0; i < props.length; i++ )
    {
        if ( buff.length > 0 )
            buff += "\r\n";
        buff += props[i];
    }
    return buff;
};

jetspeed.println = function( line )
{
    try
    {
        var console = document.getElementById( "debug_container" );
        if( !console )
        {
            console = document.getElementsByTagName( "body" )[0] || document.body;
        }
        var div = document.createElement( "div" );
        div.appendChild( document.createTextNode( line ) );
        console.appendChild( div );
    }
    catch (e)
    {
        try
        {   // safari needs the output wrapped in an element for some reason
            document.write("<div>" + line + "</div>");
        }
        catch(e2)
        {
            window.status = line;
        }
    }
};
