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
    dojo.provide("jetspeed.desktop.compatibility");
}

// ... jetspeed base objects
if ( ! window.jetspeed )
    jetspeed = {} ;


jetspeed.addOnLoad = function( adviceFnc )
{
    if ( dojo.hostenv.post_load_ )
    {
        adviceFnc.call( this );
    }
    else
    {
        dojo.event.connect( dojo, "loaded", adviceFnc );
    }
};

jetspeed.printobj = function( obj )
{
    var props = [];
    for(var prop in obj){
        try {
            props.push(prop + ': ' + obj[prop]);
        } catch(E) {
            props.push(prop + ': ERROR - ' + E.message);
        }
    }
    props.sort();
    var buff = "" ;
    for(var i = 0; i < props.length; i++) {
        if ( buff.length > 0 )
            buff += "\r\n" ;
        buff += props[i] ;
    }
    return buff ;
};

jetspeed.println = function( line )
{
    try {
        var console = document.getElementById("debug_container");
        if(!console) { console = document.getElementsByTagName("body")[0] || document.body; }

        var div = document.createElement("div");
        div.appendChild(document.createTextNode(line));
        console.appendChild(div);
    } catch (e) {
        try{
            // safari needs the output wrapped in an element for some reason
            document.write("<div>" + line + "</div>");
        }catch(e2){
            window.status = line;
        }
    }
};
