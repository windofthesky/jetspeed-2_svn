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
 *
 * author: Steve Milek
 */

// jetspeed javascript to help support portlets in both /portal and /desktop

if ( window.dojo )
{
    dojo.provide( "jetspeed.common" );
    dojo.require( "dojo.io.*" );
    dojo.require( "dojo.uri.Uri" );
}

// ... jetspeed base objects
if ( ! window.jetspeed )
    jetspeed = {};
if ( ! jetspeed.url )
    jetspeed.url = {};

// ... jetspeed version
jetspeed.version = 
{
    major: 2, minor: 1, patch: 0, flag: "dev",
    revision: "",
    toString: function() 
    {
        with (jetspeed.version) 
        {
            return major + "." + minor + "." + patch + flag + " (" + revision + ")";
        }
    }
};

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

jetspeed.printobj = function( obj, omitLineBreaks, omitEmptyValsProperties, arrayLengthsOnly )
{
    var props = [];
    for( var prop in obj )
    {
        try
        {
            var propVal = obj[prop];
            if ( arrayLengthsOnly )
            {
                if ( dojo.lang.isArray( propVal ) )
                {
                    propVal = "[" + propVal.length + "]";
                }
            }
            propVal = propVal + "";
            if ( ! omitEmptyValsProperties || propVal.length > 0 )
                props.push( prop + ': ' + propVal );
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
            buff += ( omitLineBreaks ? ", " : "\r\n" );
        buff += props[i];
    }
    return buff;
};

jetspeed.println = function( line )
{
    try
    {
        var console = jetspeed.getDebugElement();
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

jetspeed.debugNodeTree = function( node, string )
{
    if ( ! node ) return ;
    
    if ( string )
    {
        if ( string.length > 0 )
            jetspeed.println( string );
    }
    else
    {
        jetspeed.println( 'node: ' );
    }
    if ( node.nodeType != 1 && node.nodeType != 3 )
    {
        if ( node.length && node.length > 0 && ( node[0].nodeType == 1 || node[0].nodeType == 3 ) )
        {
            for ( var i = 0 ; i < node.length ; i++ )
            {
                jetspeed.debugNodeTree( node[i], " [" + i + "]" )
            }
        }
        else
        {
            jetspeed.println( " node is not a node! " + node.length );
        }   
        return ;
    }
    if ( node.innerXML )
    {
        jetspeed.println( node.innerXML );
    }
    else if ( node.xml )
    {
        jetspeed.println( node.xml );
    }
    else if ( typeof XMLSerializer != "undefined" )
    {
        jetspeed.println( (new XMLSerializer()).serializeToString( node ) );
    }
    else
    {
        jetspeed.println( " node != null (IE no XMLSerializer)" );
    }
};
jetspeed.debugShallow = function( obj, string )
{
    if ( string )
        jetspeed.println( string );
    else
        jetspeed.println( 'Object: ' + obj );
    var props = [];
    for(var prop in obj){
        try {
            props.push(prop + ': ' + obj[prop]);
        } catch(E) {
            props.push(prop + ': ERROR - ' + E.message);
        }
    }
    props.sort();
    for(var i = 0; i < props.length; i++) {
        jetspeed.println( props[i] );
    }
};
jetspeed.getDebugElement = function( clear )
{
    var console = null ;
    try {
        var console = document.getElementById("debug_container");
        if(!console)
        {
            var consoleContainer = document.getElementsByTagName("body")[0] || document.body;
            var console = document.createElement("div");
            console.setAttribute( "id", "debug_container" );
            consoleContainer.appendChild(console);
        }
        else if ( clear )
        {
            console.innerHTML = "";
        }
    } catch (e) {
        try {

        } catch(e2){}
    }
    return console ;   
};


// ... jetspeed.url
jetspeed.url.LOADING_INDICATOR_ID = "js-showloading";
jetspeed.url.path =
{
    SERVER: null,     //   http://localhost:8080
    JETSPEED: null,   //   /jetspeed
    AJAX_API: null,   //   /jetspeed/ajaxapi
    DESKTOP: null,    //   /jetspeed/desktop
    PORTAL: null,     //   /jetspeed/portal
    PORTLET: null,    //   /jetspeed/portlet
    ACTION: null,     //   /jetspeed/action
    RENDER: null,     //   /jetspeed/render
    initialized: false
};

jetspeed.url.pathInitialize = function( force )
{
    if ( ! force && jetspeed.url.path.initialized ) return;
    var baseTags = document.getElementsByTagName( "base" );

    var baseTagHref = null;
    if ( baseTags && baseTags.length == 1 )
        baseTagHref = baseTags[0].href;
    else
        baseTagHref = window.location.href;

    var baseTag = jetspeed.url.parse( baseTagHref );

    var basePath = baseTag.path;
    
    var sepPos = -1;
    for( var startPos =1 ; sepPos <= startPos ; startPos++ )
    {
        sepPos = basePath.indexOf( "/", startPos );
        if ( sepPos == -1 )
            break;
    }

    var serverUri = "";
    if ( baseTag.scheme != null) { serverUri += baseTag.scheme + ":"; }
    if ( baseTag.authority != null) { serverUri += "//" + baseTag.authority; }

    var jetspeedPath = null;
    if ( sepPos == -1 )
        jetspeedPath = basePath;
    else
        jetspeedPath = basePath.substring( 0, sepPos );
    
    //dojo.debug( "pathInitialize  new-JETSPEED=" + jetspeedPath + " orig-JETSPEED=" + jetspeed.url.path.JETSPEED + " new-SERVER=" + serverUri + " orig-SERVER=" + document.location.protocol + "//" + document.location.host );
    
    jetspeed.url.path.JETSPEED = jetspeedPath;
    jetspeed.url.path.SERVER = serverUri;
    jetspeed.url.path.AJAX_API = jetspeed.url.path.JETSPEED + "/ajaxapi";
    jetspeed.url.path.DESKTOP = jetspeed.url.path.JETSPEED + "/desktop";
    jetspeed.url.path.PORTAL = jetspeed.url.path.JETSPEED + "/portal";
    jetspeed.url.path.PORTLET = jetspeed.url.path.JETSPEED + "/portlet";
    jetspeed.url.path.ACTION = jetspeed.url.path.JETSPEED + "/action";
    jetspeed.url.path.RENDER = jetspeed.url.path.JETSPEED + "/render";

    jetspeed.url.path.initialized = true;
};
jetspeed.url.parse = function( url )
{   // taken from dojo.uri.Uri
    if ( url == null )
        return null;
    if ( window.dojo && window.dojo.uri )
        return new dojo.uri.Uri( url );
    return new jetspeed.url.JSUri( url );
};
jetspeed.url.JSUri = function( url )
{
    if ( url != null )
    {
        if ( ! url.path )
        {
            var regexp = "^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
            var r = url.toString().match( new RegExp( regexp ) );
            var parsedUrl = {};
            this.scheme = r[2] || (r[1] ? "" : null);
            this.authority = r[4] || (r[3] ? "" : null);
            this.path = r[5]; // can never be undefined
            this.query = r[7] || (r[6] ? "" : null);
            this.fragment  = r[9] || (r[8] ? "" : null);
        }
        else
        {
            this.scheme = url.scheme;
            this.authority = url.authority;
            this.path = url.path;
            this.query= url.query;
            this.fragment = url.fragment;
        }
    }
};
jetspeed.url.JSUri.prototype =
{
    scheme: null,
    authority: null,
    path: null,
    query: null,
    fragment: null,
    toString: function()
    {
        var uri = "";
        uri += ( this.scheme != null && this.scheme.length > 0 ) ? ( this.scheme + "://" ) : "";
        uri += ( this.authority != null && this.authority.length > 0 ) ? this.authority : "";
        uri += ( this.path != null && this.path.length > 0 ) ? this.path : "";
        uri += ( this.query != null && this.query.length > 0 ) ? ( "?" + this.query ) : "";
        uri += ( this.fragment != null && this.fragment > 0 ) ? ( "#" + this.fragment ) : "";
        return uri;
    }
};
jetspeed.url.scheme =
{   // used to make jetspeed.url.validateUrlStartsWithHttp cleaner
    HTTP_PREFIX: "http://",
    HTTP_PREFIX_LEN: "http://".length,
    HTTPS_PREFIX: "https://",
    HTTPS_PREFIX_LEN: "https://".length
};
jetspeed.url.isPortal = function()
{
    if ( window.djConfig && window.djConfig.jetspeed )
    {
        var servletPath = window.djConfig.jetspeed.servletPath;
        if ( servletPath != null && servletPath.toLowerCase().indexOf( "/desktop" ) == 0 )
            return false;
    }
    return true;
};
jetspeed.url.isDesktop = function()
{
    return ! jetspeed.url.isPortal();
};
jetspeed.url.servletPath = function()
{
    if ( jetspeed.url.isPortal() )
        return "/portal";
    else
        return "/desktop";
};
jetspeed.url.basePortalUrl = function()
{
    if ( ! jetspeed.url.path.initialized )
        jetspeed.url.pathInitialize();
    return jetspeed.url.path.SERVER;    // return document.location.protocol + "//" + document.location.host ;
};
jetspeed.url.basePortalDesktopUrl = function()
{
    if ( ! jetspeed.url.path.initialized )
        jetspeed.url.pathInitialize();
    return jetspeed.url.basePortalUrl() + jetspeed.url.path.JETSPEED ;
};
jetspeed.url.addPath = function( url, path )
{
    if ( path == null || path.length == 0 )
        return url;
    var modUri = new jetspeed.url.JSUri( url );
    var origPath = modUri.path;
    if ( origPath != null && origPath.length > 0 )
    {
        if ( modUri.path.charCodeAt( origPath.length -1 ) == 47 )
        {
            if ( path.charCodeAt( 0 ) == 47 )
            {
                if ( path.length > 1 )
                    modUri.path += path.substring( 1 );
            }
            else
            {
                modUri.path += path;
            }
        }
        else
        {
            if ( path.charCodeAt( 0 ) == 47 )
            {
                modUri.path += path;
            }
            else
            {
                if ( path.length > 1 )
                    modUri.path += "/" + path;
            }
        }
    }
    var urlObj = jetspeed.url.parse( modUri );
    return urlObj.toString();
};
jetspeed.url.validateUrlStartsWithHttp = function( url )
{
    if ( url )
    {
        var len = url.length;
        var hSLen = jetspeed.url.scheme.HTTPS_PREFIX_LEN;
        if ( len > hSLen )  // has to be at least longer than as https://
        {
            var hLen = jetspeed.url.scheme.HTTP_PREFIX_LEN;
            if ( url.substring( 0, hLen ) == jetspeed.url.scheme.HTTP_PREFIX )
                return true;
            if ( url.substring( 0, hSLen ) == jetspeed.url.scheme.HTTPS_PREFIX )
                return true;
        }
    }
    return false;
};
jetspeed.url.addQueryParameter = function( urlObj, paramname, paramvalue, removeExisting )
{
    if ( urlObj == null )
        return urlObj;
    if ( ! urlObj.path )
        urlObj = jetspeed.url.parse( urlObj );
    if ( urlObj == null )
        return null;
    if ( paramname == null )
        return urlObj;
    urlObj.jsQParamN = null;
    if ( removeExisting )
        urlObj = jetspeed.url.removeQueryParameter( urlObj, paramname, false );
    
    var urlQuery = urlObj.query;
    if ( urlQuery == null )
        urlQuery = "";
    var urlQueryLen = urlQuery.length;
    if ( urlQueryLen > 0 )
        urlQuery += "&";
    urlQuery += paramname + "=" + ( paramvalue != null ? paramvalue : "" );
    urlObj.query = urlQuery;
    var modUri = new jetspeed.url.JSUri( urlObj );        
    urlObj = jetspeed.url.parse( modUri );
    return urlObj;
};
jetspeed.url.removeAllQueryParameters = function( urlObj )
{
    return jetspeed.url.removeQueryParameter( urlObj, null, true );
};
jetspeed.url.removeQueryParameter = function( urlObj, paramname, removeAllParameters )
{
    if ( urlObj == null )
        return urlObj;
    if ( ! urlObj.path )
        urlObj = jetspeed.url.parse( urlObj );
    if ( urlObj == null )
        return null;
    urlObj.jsQParamN = null;
    var urlQuery = urlObj.query;
    var urlQueryLen = ( ( urlQuery != null ) ? urlQuery.length : 0 );
    if ( urlQueryLen > 0 )
    {
        if ( removeAllParameters )
            urlQuery = null;
        else if ( paramname == null )
            return urlObj;
        else
        {
            var matchParam = paramname;
            var matchPos = urlQuery.indexOf( matchParam );
            if ( matchPos == 0 )
                urlQuery = jetspeed.url._removeQP( urlQuery, urlQueryLen, matchParam, matchPos );
            
            matchParam = "&" + paramname;
            while ( true )
            {
                urlQueryLen = ( ( urlQuery != null ) ? urlQuery.length : 0 );
                matchPos = urlQuery.indexOf( matchParam, 0 );
                if ( matchPos == -1 )
                    break;
                var modUrlQuery = jetspeed.url._removeQP( urlQuery, urlQueryLen, matchParam, matchPos );
                if ( modUrlQuery == urlQuery )
                    break;
                urlQuery = modUrlQuery;
            }
            if ( urlQuery.length > 0 )
            {
                if ( urlQuery.charCodeAt( 0 ) == 38 ) // "&"
                    urlQuery = ( ( urlQuery.length > 1 ) ? urlQuery.substring( 1 ) : "" );
                if ( urlQuery.length > 0 && urlQuery.charCodeAt( 0 ) == 63 ) // "?"
                    urlQuery = ( ( urlQuery.length > 1 ) ? urlQuery.substring( 1 ) : "" );
            }
        }
        urlObj.query = urlQuery;
        var modUri = new jetspeed.url.JSUri( urlObj );        
        urlObj = jetspeed.url.parse( modUri );
    }
    return urlObj;
};

jetspeed.url._removeQP = function( urlQuery, urlQueryLen, matchParam, matchPos )
{
    if ( matchPos == -1 ) return urlQuery;
    if ( urlQueryLen > ( matchPos + matchParam.length ) )
    {
        var nextCh = urlQuery.charCodeAt( matchPos + matchParam.length );
        if ( nextCh == 61 )  // "="
        {
            var ampPos = urlQuery.indexOf( "&", matchPos + matchParam.length + 1 );
            if ( ampPos != -1 )
            {
                if ( matchPos > 0 )
                    urlQuery = urlQuery.substring( 0, matchPos ) + urlQuery.substring( ampPos );
                else
                    urlQuery = ( ( ampPos < (urlQueryLen -1) ) ? urlQuery.substring( ampPos ) : "" );
            }
            else
            {
                if ( matchPos > 0 )
                    urlQuery = urlQuery.substring( 0, matchPos )
                else
                    urlQuery = "";
            }
        }
        else if ( nextCh == 38 ) // "&"
        {
            if ( matchPos > 0 )
                urlQuery = urlQuery.substring( 0, matchPos ) + urlQuery.substring( matchPos + matchParam.length );
            else
                urlQuery = urlQuery.substring( matchPos + matchParam.length );
        }
    }
    else if ( urlQueryLen == ( matchPos + matchParam.length ) )
    {
        urlQuery = "";
    }
    return urlQuery;
};

jetspeed.url.getQueryParameter = function( urlObj, paramname )
{
    if ( urlObj == null )
        return null;
    if ( ! urlObj.authority || ! urlObj.scheme )
        urlObj = jetspeed.url.parse( urlObj );
    if ( urlObj == null )
        return null;
    if ( urlObj.jsQParamN == null && urlObj.query )
    {
        var vAry=new Array() ;
        var nAry = urlObj.query.split( "&" );
        for ( var i=0; i < nAry.length; i++ )
        {
            if ( nAry[i] == null )
                nAry[i]="";
            var sepP = nAry[i].indexOf( "=" );
            if ( sepP > 0 && sepP < (nAry[i].length -1) )
            {
                vAry[i] = unescape( nAry[i].substring( sepP + 1 ) );
                nAry[i] = unescape( nAry[i].substring( 0, sepP ) );
            }
            else
            {
                vAry[i] = "";
            }
        }
        urlObj.jsQParamN = nAry;
        urlObj.jsQParamV = vAry;
    }
    if ( urlObj.jsQParamN != null )
    {
        for ( var i=0; i < urlObj.jsQParamN.length; i++ )
        {
            if ( urlObj.jsQParamN[i] == paramname )
            {
                return urlObj.jsQParamV[i];
            }
        }
    }
    return null;
};

if ( window.dojo )
{
    jetspeed.url.BindArgs = function( bindArgs )
    {
        dojo.lang.mixin( this, bindArgs );
    
        if ( ! this.mimetype )
            this.mimetype = "text/html";
    };
    
    dojo.lang.extend( jetspeed.url.BindArgs,
    {
        createIORequest: function()
        {
            var ioReq = new dojo.io.Request( this.url, this.mimetype );
            ioReq.fromKwArgs( this );  // doing this cause dojo.io.Request tests arg0 for ctor == Object; we want out own obj here
            return ioReq;
        },
    
        load: function( type, data, http )
        {
            //dojo.debug( "loaded content for url: " + this.url );
            //dojo.debug( "r e t r i e v e C o n t e n t . l o a d" ) ;
            //dojo.debug( "  type:" );
            //dojo.debugShallow( type ) ;
            //dojo.debug( "  http:" );
            //dojo.debugShallow( http ) ;
            try
            {
                var dmId = null;
                if ( this.debugContentDumpIds )
                {
                    dmId = ( ( this.domainModelObject && dojo.lang.isFunction( this.domainModelObject.getId ) ) ? this.domainModelObject.getId() : "" );
                    for ( var debugContentIndex = 0 ; debugContentIndex < this.debugContentDumpIds.length; debugContentIndex++ )
                    {
                        if ( dmId.match( new RegExp( this.debugContentDumpIds[ debugContentIndex ] ) ) )
                        {
                            if ( dojo.lang.isString( data ) )
                                dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] content: " + data );
                            else
                            {
                                var textContent = dojo.dom.innerXML( data );
                                if ( ! textContent )
                                    textContent = ( data != null ? "!= null (IE no XMLSerializer)" : "null" );
                                dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] xml-content: " + textContent );
                            }
                        }
                    }
                }
                if ( this.contentListener && dojo.lang.isFunction( this.contentListener.notifySuccess ) )
                {
                    this.contentListener.notifySuccess( data, this.url, this.domainModelObject, http ) ;
                }
                else
                {
                    dmId = ( ( this.domainModelObject && dojo.lang.isFunction( this.domainModelObject.getId ) ) ? this.domainModelObject.getId() : "" );
                    dojo.debug( "retrieveContent [" + ( dmId ? dmId : this.url ) + "] no valid contentListener" );
                }
                if ( this.hideLoadingIndicator )
                    jetspeed.url.loadingIndicatorHide();
            }
            catch(e)
            {
                if ( this.hideLoadingIndicator )
                    jetspeed.url.loadingIndicatorHide();
                throw e;
            }
        },
    
        error: function( type, error )
        {
            //dojo.debug( "r e t r i e v e C o n t e n t . e r r o r" ) ;
            //dojo.debug( "  type:" );
            //dojo.debugShallow( type ) ;
            //dojo.debug( "  error:" );
            //dojo.debugShallow( error ) ;
            try
            {
                if ( this.contentListener && dojo.lang.isFunction( this.contentListener.notifyFailure ) )
                {
                    this.contentListener.notifyFailure( type, error, this.url, this.domainModelObject );
                }
                if ( this.hideLoadingIndicator )
                    jetspeed.url.loadingIndicatorHide();
            }
            catch(e)
            {
                if ( this.hideLoadingIndicator )
                    jetspeed.url.loadingIndicatorHide();
                throw e;
            }
        }
    });
    
    jetspeed.url.retrieveContent = function( bindArgs, contentListener, domainModelObject, debugContentDumpIds )
    {
        if ( ! bindArgs ) bindArgs = {};
        bindArgs.contentListener = contentListener ;
        bindArgs.domainModelObject = domainModelObject ;
        bindArgs.debugContentDumpIds = debugContentDumpIds ;
        
        var jetspeedBindArgs = new jetspeed.url.BindArgs( bindArgs );

        if ( bindArgs.showLoadingIndicator || ( contentListener && ! contentListener.suppressLoadingIndicator && bindArgs.showLoadingIndicator != false ) )
        {
            if ( jetspeed.url.loadingIndicatorShow() )
                jetspeedBindArgs.hideLoadingIndicator = true ;
        }
        dojo.io.bind( jetspeedBindArgs.createIORequest() ) ;
    };
    
    jetspeed.url.checkAjaxApiResponse = function( requestUrl, data, reportError, apiRequestDescription, dumpOutput )
    {
        var success = false;
        var statusElmt = data.getElementsByTagName( "status" );
        if ( statusElmt != null )
        {
            var successVal = statusElmt[0].firstChild.nodeValue;
            if ( successVal == "success" )
            {
                success = true;
            }
        }
        if ( ( ! success && reportError ) || dumpOutput )
        {
            var textContent = dojo.dom.innerXML( data );
            if ( ! textContent )
                textContent = ( data != null ? "!= null (IE no XMLSerializer)" : "null" );
            if ( apiRequestDescription == null )
                apiRequestDescription = "ajax-api";
            if ( success )
                dojo.debug( apiRequestDescription + " success  url=" + requestUrl + "  xml-content=" + textContent );
            else
                dojo.raise( apiRequestDescription + " failure  url=" + requestUrl + "  xml-content=" + textContent );
        }
        return success;
    };
    
    jetspeed.url.formatBindError = function( /* Object */ bindError )
    {
        if ( bindError == null ) return "";
        var msg = " error:";
        if ( bindError.message != null )
            msg += " " + bindError.message;
        if ( bindError.number != null && bindError.number != "0" )
        {
            msg += " (" + bindError.number;
            if ( bindError.type != null && bindError.type != "unknown" )
                msg += "/" + bindError.type;
            msg += ")";
        }
        else if ( bindError.type != null && bindError.type != "unknown" )
        {
            msg += " (" + bindError.type + ")";
        }
        return msg;
    };
    jetspeed.url.loadingIndicatorShow = function( actionName )
    {
        if ( typeof actionName == "undefined" )
            actionName = "loadpage";
        var loading = document.getElementById( jetspeed.url.LOADING_INDICATOR_ID );
        if ( loading != null && loading.style )
        {
            var actionlabel = null;
            if ( jetspeed.prefs != null && jetspeed.prefs.desktopActionLabels != null )
                actionlabel = jetspeed.prefs.desktopActionLabels[ actionName ];

            if ( actionlabel != null && actionlabel.length > 0 && loading.style[ "display" ] == "none" )
            {
                loading.style[ "display" ] = "";

                if ( actionName != null )
                {
                    if ( actionlabel != null && actionlabel.length > 0 )
                    {
                        var loadingContent = document.getElementById( jetspeed.url.LOADING_INDICATOR_ID + "-content" );
                        if ( loadingContent != null )
                        {
                            loadingContent.innerHTML = actionlabel;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    };
    jetspeed.url.loadingIndicatorHide = function()
    {
        var loading = document.getElementById( jetspeed.url.LOADING_INDICATOR_ID );
        if ( loading != null && loading.style )
            loading.style[ "display" ] = "none";
    };
}
