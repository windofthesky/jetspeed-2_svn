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


/**
 * jetspeed is the root variable of (almost all) our public symbols.
 */

// ... jetspeed base objects
if ( ! window.jetspeed )
    jetspeed = {} ;
if ( ! jetspeed.om )
    jetspeed.om = {} ;
if ( ! jetspeed.url )
    jetspeed.url = {} ;
if ( ! jetspeed.ui )
    jetspeed.ui = {} ;
if ( ! jetspeed.ui.widget )
    jetspeed.ui.widget = {} ;


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

// ... jetspeed desktop preferences
jetspeed.prefs = 
{
    windowTiling: false
    
}

// ... jetspeed debug options
jetspeed.debug =
{
    pageLoad: true,
    retrievePsml: false,
    setPortletContent: false,
    doRenderDoAction: false,
    postParseAnnotateHtml: false,
    executeOnSubmit: false,
    confirmOnSubmit: false,
    createWindow: false,
    submitChangedWindowState: false
}
jetspeed.debugPsmlDumpContent = false;
//jetspeed.debugPortletEntityIdFilter = [ "dp-7", "um-3", "um-4" ]; // NOTE: uncomment causes only the listed portlets to be loaded; all others are ignored; for testing
//jetspeed.debugPortletEntityIdFilter = [ "dp-12" ];
jetspeed.debugPortletWindowIcons = [ "text-x-generic.png", "text-html.png", "application-x-executable.png" ];
jetspeed.debugPortletWindowThemes = [ "theme1", "theme2" ];
//jetspeed.debugPortletDumpRawContent = [ "um-4", "dp-7", "jsfGuessNumber1", "jsfCalendar" ];    // "um-4", "dp-7", "jsfGuessNumber1", "jsfCalendar"
//jetspeed.debugPortletDumpRawContent = [ "*" ];


// ... load page /portlets
jetspeed.page = null ;   // BOZO: is this it? one page at a time?
jetspeed.initializeDesktop = function()
{
    jetspeed.loadPage();
}
jetspeed.loadPage = function()
{
    jetspeed.page = new jetspeed.om.Page() ;
    jetspeed.currentTaskbar = new jetspeed.ui.PortalTaskBar() ;
    jetspeed.page.retrievePsml( new jetspeed.om.PageContentListenerCreateWidget() ) ;
}
jetspeed.loadPortletWindows = function( /* Portlet[] */ portletArray, portletWindowFactory )
{
    if ( ! portletArray )
        portletArray = jetspeed.page.getPortletArrayByZIndex();
    if ( portletArray )
    {
        var createdPortlets = [];
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( jetspeed.debugPortletEntityIdFilter )
            {
                if (! dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter, portlet.entityId))
                    portlet = null;
            }
            if (portlet)
            {
                createdPortlets.push(portlet);
                portlet.createPortletWindow(portletWindowFactory,null,true);
            }
        }
        jetspeed.doRenderAll( null, createdPortlets, true );
    }
}

// ... jetspeed.doRender
jetspeed.renderForm = null;
jetspeed.doRender = function( url, portletEntityId, currentForm )
{
    if ( ! currentForm )
        currentForm = jetspeed.renderForm;
    jetspeed.renderForm = null;
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        if ( jetspeed.debug.doRenderDoAction )
            dojo.debug( "doRender [" + portletEntityId + "] url: " + url );
        targetPortlet.retrievePortletContent( null, url, currentForm );
    }
}

// ... jetspeed.doRenderAll
jetspeed.doRenderAll = function( url, portletArray, isPageLoad )
{
    var debugMsg = jetspeed.debug.doRenderDoAction;
    var debugPageLoad = jetspeed.debug.pageLoad && isPageLoad;
    if ( ! portletArray )
        portletArray = jetspeed.page.getPortletArray();
    var renderMsg = "";
    for ( var i = 0; i < portletArray.length; i++ )
    {
        var portlet = portletArray[i];
        if ( debugMsg || debugPageLoad )
        {
            if ( i > 0 ) renderMsg = renderMsg + ", ";
            renderMsg = renderMsg + portlet.entityId;
            if ( debugPageLoad )
                renderMsg = renderMsg + " " + portlet.title;
        }
        portlet.retrievePortletContent( null, url );
    }
    if ( debugMsg )
        dojo.debug( "doRenderAll [" + renderMsg + "] url: " + url );
    else if ( debugPageLoad )   // this.getPsmlUrl() ;
        dojo.debug( "doRenderAll page-url: " + jetspeed.page.getPsmlUrl() + " portlets: [" + renderMsg + "]" + ( url ? ( " url: " + url ) : "" ) );
}

// ... jetspeed.doAction
jetspeed.actionForm = null;
jetspeed.doAction = function( url, portletEntityId, currentForm )
{
    if ( ! currentForm )
        currentForm = jetspeed.actionForm;
    jetspeed.actionForm = null;
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        if ( jetspeed.debug.doRenderDoAction )
        {
            if ( !currentForm )
                dojo.debug( "doAction [" + portletEntityId + "] url: " + url + " form: null" );
            else
                dojo.debug( "doAction [" + portletEntityId + "] url: " + url + " form: " + jetspeed.debugDumpForm( currentForm ) );
        }
        targetPortlet.retrievePortletContent( new jetspeed.om.PortletActionContentListener(), url, currentForm );
    }
}
jetspeed.debugDumpForm = function( formNode )
{
    if ( ! formNode ) return null ;
    var formDump = formNode.toString() ;
    if ( formNode.name )
        formDump += " name=" + formNode.name;
    if ( formNode.id )
        formDump += " id=" + formNode.id;
    var queryString = dojo.io.encodeForm( formNode );
    formDump += " data=" + queryString; 
    return formDump;
}


// ... jetspeed.url
jetspeed.url.path =
{
    JETSPEED: "/jetspeed",
    AJAX_API: "/jetspeed/ajaxapi",
    DESKTOP: "/jetspeed/desktop",
    PORTLET: "/jetspeed/portlet"
}
jetspeed.url.scheme =
{   // used to make jetspeed.url.validateUrlStartsWithHttp cleaner
    HTTP_PREFIX: "http://",
    HTTP_PREFIX_LEN: "http://".length,
    HTTPS_PREFIX: "https://",
    HTTPS_PREFIX_LEN: "https://".length
}
jetspeed.url.basePortalUrl = function()
{
    return document.location.protocol + "//" + document.location.host ;
}
jetspeed.url.basePortalDesktopUrl = function()
{
    return jetspeed.url.basePortalUrl() + jetspeed.url.path.JETSPEED ;
}
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
}

// ... jetspeed.om.PageContentListenerCreateWidget
jetspeed.om.PageContentListenerCreateWidget = function()
{
}
jetspeed.om.PageContentListenerCreateWidget.prototype =
{
    notifySuccess: function( /* Page */ page )
    {
        jetspeed.loadPortletWindows();
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Page */ page )
    {
        alert( "PageContentListenerCreateWidget notifyFailure type=" + type + " error=" + error ) ;
    }
}

// ... jetspeed.om.Page
jetspeed.om.Page = function( pagePsmlPath, pageName, pageTitle )
{
    this.psmlPath = pagePsmlPath ;
    if ( this.psmlPath == null )
        this.setPsmlPathFromDocumentUrl() ;
    this.name = pageName ;
    this.title = pageTitle ;
    this.portlets = [] ;
}
jetspeed.om.Page.prototype =
{
    psmlPath: null,
    name: null,
    title: null,
    portlets: null,

    setPsmlPathFromDocumentUrl: function()
    {
        var psmlPath = jetspeed.url.path.AJAX_API ;
        var docPath = document.location.pathname ;
        
        var contextAndServletPath = jetspeed.url.path.DESKTOP ;
        var contextAndServletPathPos = docPath.indexOf( contextAndServletPath ) ;
        if ( contextAndServletPathPos != -1 && docPath.length > ( contextAndServletPathPos + contextAndServletPath.length ) )
        {
            psmlPath = psmlPath + docPath.substring( contextAndServletPathPos + contextAndServletPath.length ) ;
        }
        this.psmlPath = psmlPath ;
    },
    
    getPsmlUrl: function()
    {
        if ( this.psmlPath == null )
            this.setPsmlPathFromDocumentUrl() ;

        return jetspeed.url.basePortalUrl() + this.psmlPath ;
    },
    
    retrievePsml: function( pageContentListener )
    {
        var psmlUrl = this.getPsmlUrl() ;

        if ( jetspeed.debug.retrievePsml )
            dojo.debug( "retrievePsml url: " + psmlUrl ) ;

        var page = this ;  // NOTE: bind calls like this cannot generally be further encapsulated due to need for a closure
        dojo.io.bind({     //       (in this case page and pageContentListener locals create a closure due to their use in load/error functions)
            url: psmlUrl,
            load: function(type, data, evt)
            {
                //dojo.debug( "r e t r i e v e P s m l . l o a d" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  evt:" );
                //dojo.debugShallow( evt ) ;
                if ( jetspeed.debugPsmlDumpContent )
                {
                    dojo.debug( "retrievePsml content: " + dojo.dom.innerXML( data ) );
                }
                page.getPortletsFromPSML( data );
                if ( pageContentListener && dojo.lang.isFunction( pageContentListener.notifySuccess ) )
                {
                    pageContentListener.notifySuccess(page);
                    }
            },
            error: function(type, error)
            {
                //dojo.debug( "r e t r i e v e P s m l . e r r o r" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  error:" );
                //dojo.debugShallow( error ) ;
                if ( pageContentListener && dojo.lang.isFunction( pageContentListener.notifyFailure ) )
                {
                    pageContentListener.notifyFailure(type, error, page);
                }
            },
            mimetype: "text/xml"
        });            
    },

    getPortletsFromPSML: function( psml )
    {
        var lis = psml.getElementsByTagName("fragment");
        for( var x=0; x < lis.length; x++ )
        {
            var fragType = lis[x].getAttribute("type");
            if ( fragType == "portlet" )
            {
                var portletName = lis[x].getAttribute("name");
                var portletEntityId = lis[x].getAttribute("id");
                var portlet = new jetspeed.om.Portlet( portletName, portletEntityId ) ;

                var props = lis[x].getElementsByTagName("property");
                for( var propsIdx=0; propsIdx < props.length; propsIdx++ )
                {
                    var propName = props[propsIdx].getAttribute("name") ;
                    var propValue = props[propsIdx].getAttribute("value") ;
                    portlet.putProperty( propName, propValue ) ;
                }
                this.putPortlet( portlet ) ;
            }
        }
    },

    _portletZIndexCompare: function( portletA, portletB )
    {   // uses saved state only - does not check with window widget
        var aZIndex = null;
        var bZIndex = null;
        var windowState = null;
        windowState = portletA.getLastSavedWindowState();
        aZIndex = windowState.zIndex;
        windowState = portletB.getLastSavedWindowState();
        bZIndex = windowState.zIndex;
        if ( aZIndex && ! bZIndex )
            return -1;
        else if ( bZIndex && ! aZIndex )
            return 1;
        else if ( aZIndex == bZIndex )
            return 0;
        return ( aZIndex - bZIndex );
    },

    _debugDumpPortletZIndexInfo: function()
    {
        var portletArray = this.getPortletArrayByZIndex();
        var dumpMsg = "";
        for ( var i = 0; i < portletArray.length; i++ )
        {
            var portlet = portletArray[i];
            if ( i > 0 ) dumpMsg += ", ";
            var windowState = portlet.getLastSavedWindowState();
            var zIndex = ( windowState ? windowState.zIndex : "null" );
            dumpMsg += "[" + portlet.entityId + "] zIndex=" + zIndex;
        }
        return dumpMsg;
    },

    getPortletArrayByZIndex: function()
    {
        var portletArray = this.getPortletArray();
        portletArray.sort( this._portletZIndexCompare );
        return portletArray;
    },
    getPortletArrayList: function()
    {
        var portletArrayList = new dojo.collections.ArrayList();
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            portletArrayList.add(portlet);
        }
        return portletArrayList;
    },
    getPortletArray: function()
    {
        if (! this.portlets) return null ;
        var portletArray = [];
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            portletArray.push(portlet);
        }
        return portletArray;
    },
    getPortlets: function()
    {
        if (this.portlets)
            return dojo.lang.shallowCopy(this.portlets) ;
        return null ;
    },
    getPortlet: function( /* String */ portletEntityId )
    {
        if (this.portlets && portletEntityId)
            return this.portlets[portletEntityId];
        return null;
    },
    putPortlet: function( /* Portlet */ portlet )
    {
        if (!portlet) return ;
        if (! this.portlets) this.portlets = [] ;
        this.portlets[ portlet.entityId ] = portlet ;
    },
    removePortlet: function( /* Portlet */ portlet )
    {
        if (! portlet || ! this.portlets) return ;
        delete this.portlets[ portlet.entityId ] ;
    },
    debugDumpWindowStateAllPortlets: function()
    {
        dojo.debug("==== window-state all-portlets ====" );
        for (var portletIndex in this.portlets)
        {
            var portlet = this.portlets[portletIndex];
            var portletWindowState = portlet.getCurrentWindowState();
            var dumpMsg = "portlet " + portlet.entityId + " : ";    
            if ( ! portletWindowState )
                dumpMsg = dumpMsg + "null window state";
            else
            {   
                var propCount = 0;
                for (var propName in portletWindowState)
                {
                    if (propCount++ > 0) dumpMsg = dumpMsg + ", ";
                    dumpMsg = dumpMsg + propName + "=" + portletWindowState[propName];
                }
            }
            dojo.debug( dumpMsg );
        }
    }
}

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletContentListener = function()
{
}
jetspeed.om.PortletContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        portlet.setPortletContent( portletContent, requestUrl );
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Portlet */ portlet )
    {
        alert( "PortletContentListener notifyFailure type=" + type + " error=" + error ) ;
    }
}

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletActionContentListener = function()
{
}
jetspeed.om.PortletActionContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* String */ requestUrl, /* Portlet */ portlet )
    {
        var renderUrl = null;
        var parsedPseudoUrl = portlet.parseJSPseudoUrlActionRender( portletContent );
        if ( parsedPseudoUrl.operation == portlet.PORTLET_REQUEST_ACTION || parsedPseudoUrl.operation == portlet.PORTLET_REQUEST_RENDER )
        {
            //dojo.debug( "PortletActionContentListener extracted from javascript-pseudo-url: " + portletContent + "  url: " + parsedPseudoUrl.url + " operation: " + parsedPseudoUrl.operation + " entity-id: " + parsedPseudoUrl.portletEntityId ) ;
            renderUrl = parsedPseudoUrl.url;
        }
        else
        {
            //dojo.debug( "PortletActionContentListener: " + portletContent ) ;
            renderUrl = portletContent;
        }
        if ( renderUrl )
        {
            jetspeed.doRenderAll( renderUrl );    // render all portlets
            //  portlet.retrievePortletContent(null,renderUrl);    // render just the one portlet
        }        
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Portlet */ portlet )
    {
        alert( "PortletActionContentListener notifyFailure type=" + type ) ;
        dojo.debugShallow( error );
    }
}


// ... jetspeed.om.PortletWindowFactory
jetspeed.om.PortletWindowFactory = function()
{
}
jetspeed.om.PortletWindowFactory.prototype =
{
    create: function( /* Portlet */ portlet )
    {
        return jetspeed.ui.createPortletWindowWidget(portlet);
    }
}


// ... jetspeed.om.Portlet
jetspeed.om.Portlet = function( /* String */ portletName, /* String */ portletEntityId, /* String */ portletTitle )
{
    this.name = portletName;
    this.entityId = portletEntityId;
    if (portletTitle == null && portletName)
    {
        var re = (/^[^:]*:*/);
        portletTitle = portletName.replace( re, "" );
    }
    this.title = portletTitle;
    this.properties = {};
}
jetspeed.om.Portlet.prototype =   /* defining prototypes like this is not cool if the object uses dojo.inherits (this would replace pt)  */
{                                 /* dojo.lang.extend would allow this syntax instead of [<type>.prototype.<propname> = <propval>]*      */
    name: null,
    entityId: null,
    title: null,
    
    windowFactory: null,

    windowWidgetId: null,
    
    lastSavedWindowState: null,

    JAVASCRIPT_ACTION_PREFIX: "javascript:doAction(",
    JAVASCRIPT_RENDER_PREFIX: "javascript:doRender(",
    JAVASCRIPT_ARG_QUOTE: "&" + "quot;",
    PORTLET_REQUEST_ACTION: "action",
    PORTLET_REQUEST_RENDER: "render",
    

    parseJSPseudoUrlActionRender: function( /* String */ javascriptPseudoUrl )
    {
        var op = null;
        var justTheUrl = javascriptPseudoUrl;
        var entityId = null;
        var argsSuffix = null;
        if ( javascriptPseudoUrl && javascriptPseudoUrl.length > this.JAVASCRIPT_ACTION_PREFIX.length && javascriptPseudoUrl.indexOf( this.JAVASCRIPT_ACTION_PREFIX ) == 0 )
        {   // annotate away javascript invocation in form action
            justTheUrl = null;
            op = this.PORTLET_REQUEST_ACTION;
            argsSuffix = javascriptPseudoUrl.substring( this.JAVASCRIPT_ACTION_PREFIX.length );
        }
        else if ( javascriptPseudoUrl && javascriptPseudoUrl.length > this.JAVASCRIPT_RENDER_PREFIX.length && javascriptPseudoUrl.indexOf( this.JAVASCRIPT_RENDER_PREFIX ) == 0 )
        {
            justTheUrl = null;
            op = this.PORTLET_REQUEST_RENDER;
            argsSuffix = javascriptPseudoUrl.substring( this.JAVASCRIPT_RENDER_PREFIX.length );
        }
        if ( argsSuffix )
        {
            var quoteDelim = "\"";
            var argsEnd = argsSuffix.lastIndexOf( quoteDelim );
            var altargsEnd = argsSuffix.lastIndexOf( this.JAVASCRIPT_ARG_QUOTE );
            if ( altargsEnd > argsEnd )
            {
                quoteDelim = this.JAVASCRIPT_ARG_QUOTE;
                argsEnd = altargsEnd;
            }
            if ( argsEnd >= 0 )
            {
                argsSuffix = dojo.string.trim( argsSuffix.substring( 0, argsEnd + quoteDelim.length ) );
                var argsData = argsSuffix.split( quoteDelim );
                if ( argsData && argsData.length >=4 )
                {
                    justTheUrl = argsData[1];
                    entityId = argsData[3];
                }
            }
        }
        else
        {
            op = null;
        }
        
        if ( ! jetspeed.url.validateUrlStartsWithHttp( justTheUrl ) )
            justTheUrl = null;

        return { url: justTheUrl, operation: op, portletEntityId: entityId };
    },
    preParseAnnotateHtml: function( /* String */ portletContent )
    {   // deal with embedded script tags -  /=/=/=/=/=  taken from dojo ContentPane.js  splitAndFixPaths()  =/=/=/=/=/
        var scripts = [];
        var remoteScripts = [];
        // cut out all script tags, stuff them into scripts array
		var match = [];
		while ( match )
        {
			match = portletContent.match(/<script([^>]*)>([\s\S]*?)<\/script>/i);
			if(!match){ break; }
			if(match[1]){
				attr = match[1].match(/src=(['"]?)([^"']*)\1/i);
				if ( attr )
                {
					// remove a dojo.js or dojo.js.uncompressed.js from remoteScripts
					if ( (attr[2].search(/\/?\bdojo.js(?:\.uncompressed.js)?/i) != -1) && (dojo.hostenv.getBaseScriptUri() == attr[2].match(/[.\/]*/)[0]) )
					{	
						dojo.debug("Security note! inhibit:"+attr[2]+" from  beeing loaded again.");
					}
                    else
                    {
						remoteScripts.push( attr[2] );
					}
				}
			}
			if ( match[2] )
            {
                // get rid of html comment blanket
                var scriptText = match[2].replace(/^\s*<!--/, "");
                scriptText = scriptText.replace(/-->\s*$/, "");

                scriptText = scriptText.replace(/function\s+([a-zA-Z_][a-zA-Z0-9_]*)\s*\(/g, "window.$1 = function(" );

				// strip out all djConfig variables from script tags nodeValue
				// this is ABSOLUTLY needed as reinitialize djConfig after dojo is initialised
				// makes a dissaster greater than Titanic                
				scripts.push(scriptText.replace(/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo.hostenv.writeIncludes\(\s*\);?/g, ""));
			}
			portletContent = portletContent.replace(/<script[^>]*>[\s\S]*?<\/script>/i, "");
		}
        //     /=/=/=/=/=  end of taken from dojo ContentPane.js  splitAndFixPaths()  =/=/=/=/=/
        //dojo.debug( "preParse  scripts: " + ( scripts ? scripts.length : "0" ) + " remoteScripts: " + ( remoteScripts ? remoteScripts.length : "0" ) );
        return { portletContent: portletContent, scripts: scripts, remoteScripts: remoteScripts };
    },
    postParseAnnotateHtml: function( /* DOMNode */ containerNode )
    {   
        if ( containerNode )
        {
            var cNode = containerNode;
            var formList = cNode.getElementsByTagName( "form" );
            var debugOn = jetspeed.debug.postParseAnnotateHtml;
            if ( formList )
            {
                for ( var i = 0 ; i < formList.length ; i++ )
                {
                    var cForm = formList[i];                    
                    var cFormAction = cForm.action;
                    var cFormPortletEntityId = this.entityId;  // BOZO:can I assume that it is always my entity-id (ignoring the one in parsedPseudoUrl)

                    var parsedPseudoUrl = this.parseJSPseudoUrlActionRender( cFormAction );
                    
                    var submitOperation = parsedPseudoUrl.operation;

                    if ( submitOperation == this.PORTLET_REQUEST_ACTION || submitOperation == this.PORTLET_REQUEST_RENDER )
                    {
                        var replacementActionUrl = this._generateJSPseudoUrlActionRender( parsedPseudoUrl );
                        if ( replacementActionUrl == cFormAction )
                        {
                            if ( debugOn )
                                dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] adding onSubmit (portlet-" + submitOperation + ") and leaving form action as is: " + cFormAction );
                        }
                        else
                        {
                            cForm.action = replacementActionUrl;
                            if ( debugOn )
                                dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] adding onSubmit (portlet-" + submitOperation + ") and changing form action attribute from: " + cFormAction + " to: " +  replacementActionUrl );
                        }
                        this._addOnSubmitActionRender( cForm, cFormPortletEntityId, submitOperation );
                    }
                    else
                    {
                        if ( djConfig.isDebug )  // want to see this, for now
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] form action attribute doesn't match annotation criteria, leaving as is: " + cFormAction ) ;
                    }
                }
            }
            var aList = cNode.getElementsByTagName( "a" );
            if ( aList )
            {
                for ( var i = 0 ; i < aList.length ; i++ )
                {
                    var aNode = aList[i];
                    var aHref = aNode.href;
                    
                    var parsedPseudoUrl = this.parseJSPseudoUrlActionRender( aHref );
                    var replacementHref = this._generateJSPseudoUrlActionRender( parsedPseudoUrl );

                    if ( ! replacementHref )
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] leaving href as is: " + aHref );
                    }
                    else if ( replacementHref == aHref )
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] href parsed and regenerated identically: " + aHref );
                    }
                    else
                    {
                        if ( debugOn )
                            dojo.debug( "postParseAnnotateHtml [" + this.entityId + "] href parsed, replacing: " + aHref + " with: " + replacementHref );
                        aNode.href = replacementHref;
                    }
                }
            }
        }
    },

    _addOnSubmitActionRender: function( cForm, cFormPortletEntityId, submitOperation )
    {   // NOTE: must be broken out of loop to caputure separate instances of closure variables
        var self = this;
        dojo.event.connect(cForm, "onsubmit", function(e) {
            var abort = false;
            if ( jetspeed.debug.executeOnSubmit || jetspeed.debug.confirmOnSubmit )
            {
                var submitMsg = "execute onsubmit : " + jetspeed.debugDumpForm( cForm );
                if ( jetspeed.debug.executeOnSubmit )
                    dojo.debug( submitMsg );
                if ( jetspeed.debug.confirmOnSubmit )
                {
                    if ( e )
                    {
                        if ( ! confirm( "Hit OK to submit, or hit Cancel: " + submitMsg ) )
                        {
                            abort = true;
                            e.preventDefault();
                        }
                    }
                    else
                    {
                        alert( "Hit OK to submit (cannot be cancelled): " + submitMsg );
                    }
                }
            }
            if ( ! abort )
            {
                if ( submitOperation == self.PORTLET_REQUEST_ACTION )
                {
                    jetspeed.actionForm = cForm;
                }
                else
                {
                    jetspeed.renderForm = cForm;
                }
            }
        });
    },
    _generateJSPseudoUrlActionRender: function( parsedPseudoUrl )
    {   // NOTE: no form can be passed in one of these
        if ( ! parsedPseudoUrl || ! parsedPseudoUrl.url || ! parsedPseudoUrl.portletEntityId ) return null;
        var hrefJScolon = "javascript:";
        var badnews = false;
        if ( parsedPseudoUrl.operation == this.PORTLET_REQUEST_ACTION )
            hrefJScolon += "doAction(\"";
        else if ( parsedPseudoUrl.operation == this.PORTLET_REQUEST_RENDER )
            hrefJScolon += "doRender(\"";
        else badnews = true;
        if ( badnews ) return null;
        hrefJScolon += parsedPseudoUrl.url + "\",\"" + parsedPseudoUrl.portletEntityId + "\")"
        return hrefJScolon;
    },

    getPortletWindow: function()
    {
        if ( this.windowWidgetId )
            return dojo.widget.byId( this.windowWidgetId );
        return null;
    },
    getCurrentWindowState: function( /* boolean */ volatileOnly )
    {
        var windowWidget = this.getPortletWindow();
        if ( ! windowWidget ) return null;

        var rawState = null;
        if ( volatileOnly )
            rawState = windowWidget.getCurrentVolatileWindowState();
        else
            rawState = windowWidget.getCurrentWindowState();

        var currentState = {};
        var pxre = (/px/i);
        for ( var stateKey in rawState )
        {
            currentState[stateKey] = new String( rawState[stateKey] ).replace( pxre, "" );
        }
        return currentState;
    },
    getLastSavedWindowState: function()
    {
        if ( ! this.lastSavedWindowState )
            this.initializeWindowState();
        return this.lastSavedWindowState;
    },
    _getChangedWindowState: function( /* boolean */ volatileOnly )
    {
        var lastSaved = this.getLastSavedWindowState();
        var hasChange = false;
        var zIndexTrack = ! jetspeed.prefs.windowTiling;
        var zIndexChange = false;

        if ( lastSaved && dojo.lang.isEmpty( lastSaved ) )
        {
            lastSaved = null;
            volatileOnly = false;  // so that current state we obtain is the full representation
        }
        var currentState = this.getCurrentWindowState( volatileOnly );
        if ( ! lastSaved )
        {
            var result = { state: currentState };
            if ( zIndexTrack )
                result.zIndexChanged = true;   // BOZO: this could lead to an early submission for each portlet (may not be too cool?)
            return result;
        }
        
        for (var stateKey in currentState)
        {
            //if ( stateKey == "zIndex" )
            //    dojo.debug( "portlet zIndex compare [" + this.entityId + "]  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) );
            if ( currentState[stateKey] != lastSaved[stateKey] )
            {
                hasChange = true;

                //dojo.debug( "portlet [" + this.entityId + "] windowstate changed: " + stateKey + "  " + ( currentState[stateKey] ? currentState[stateKey] : "null" ) + " != " + ( lastSaved[stateKey] ? lastSaved[stateKey] : "null" ) ) ;

                if ( ! zIndexTrack )
                    break;
                else if ( stateKey == "zIndex" )
                    zIndexChange = true;
            }
        }
        if ( hasChange )
        {
            var result = { state: currentState };
            if ( zIndexTrack )
                result.zIndexChanged = zIndexChange;
            return result;
        }
        return null;
    },
    
    initializeWindowState: function()
    {
        var initialWindowState = {};
        
        var portletWidth = this.getProperty( "width" );
        if ( portletWidth && portletWidth > 0 )
            initialWindowState.width = Math.floor( portletWidth );
    
        var portletHeight = this.getProperty( "height" );
        if ( portletHeight && portletHeight > 0  )
            initialWindowState.height = Math.floor( portletHeight );
        
        var portletLeft = this.getProperty( "x" );
        if ( portletLeft && portletLeft > 0 )
            initialWindowState.left = Math.floor( portletLeft );

        var portletTop = this.getProperty( "y" );
        if ( portletTop && portletTop > 0 )
            initialWindowState.top = Math.floor( portletTop );

        var portletZIndex = this.getProperty( "z" );
        if ( portletZIndex )
            initialWindowState.zIndex = Math.floor( portletZIndex );

        //dojo.debug( "initial window state [" + this.entityId + "] :" ); dojo.debugShallow( initialWindowState );

        this.lastSavedWindowState = initialWindowState;
    },
    createPortletWindow: function(portletWindowFactory, portletContentListener, doNotRetrieveContent)
    {
        if ( portletWindowFactory == null )
            portletWindowFactory = new jetspeed.om.PortletWindowFactory() ;

        this.windowFactory = portletWindowFactory ;
        this.windowWidgetId = portletWindowFactory.create( this ) ;

        if (! doNotRetrieveContent)
            this.retrievePortletContent(portletContentListener) ;
    },

    getPortletUrl: function(renderUrl)
    {
        var queryString = "?entity=" + this.entityId + "&portlet=" + this.name + "&encoder=desktop";
        if (renderUrl)
            return renderUrl + queryString;
        return jetspeed.url.basePortalUrl() + jetspeed.url.path.PORTLET + queryString;
    },

    submitChangedWindowState: function( /* boolean */ volatileOnly )
    {
        var changedStateResult = this._getChangedWindowState( volatileOnly );
        if ( changedStateResult )
        {
            var changedState = changedStateResult.state;
            var portlet = this;
            var queryString = "?action=move&id=" + this.entityId;

            if ( changedState.zIndex )
                queryString += "&z=" + changedState.zIndex;
            if ( changedState.width )
                queryString += "&width=" + changedState.width;
            if ( changedState.height )
                queryString += "&height=" + changedState.height;
            if ( changedState.left )
                queryString += "&x=" + changedState.left;
            if ( changedState.top )
                queryString += "&y=" + changedState.top;

            var psmlMoveActionUrl = jetspeed.page.getPsmlUrl() + queryString;
            dojo.io.bind({
                url: psmlMoveActionUrl,
                load: function( type, data, evt )
                {
                    if ( jetspeed.debug.submitChangedWindowState )
                        dojo.debug( "submitChangedWindowState [" + portlet.entityId + "] url: " + psmlMoveActionUrl + " content: " + data );
                    dojo.lang.mixin( portlet.lastSavedWindowState, changedState );
                },
                error: function( type, error )
                {
                    dojo.debug( "submitChangedWindowState error [" + portlet.entityId + "] url: " + psmlMoveActionUrl + " type: " + type + " error: " + error );
                },
                mimetype: "text/html"
            });

            if ( ! volatileOnly )
            {
                if ( ! jetspeed.prefs.windowTiling && changedStateResult.zIndexChanged )  // current condition for whether 
                {                                                                         // volatile (zIndex) changes are possible
                    var portletArrayList = jetspeed.page.getPortletArrayList();
                    var autoUpdatePortlets = dojo.collections.Set.difference( portletArrayList, [ this ] );
                    if ( ! portletArrayList || ! autoUpdatePortlets || ((autoUpdatePortlets.count + 1) != portletArrayList.count) )
                        dojo.raise( "Portlet.submitChangedWindowState invalid conditions for starting auto update" );
                    else if ( autoUpdatePortlets && autoUpdatePortlets.count > 0 )
                    {
                        dojo.lang.forEach( autoUpdatePortlets.toArray(),
                                function(portlet) { portlet.submitChangedWindowState( true ); } );
                    }
                }
            }
        }
    },

    retrievePortletContent: function( portletContentListener, renderOrActionUrl, actionForm )
    {
        if ( portletContentListener == null )
            portletContentListener = new jetspeed.om.PortletContentListener() ;
        var portlet = this ;
        var requestUrl = portlet.getPortletUrl( renderOrActionUrl ) ;
        dojo.io.bind({
            formNode: actionForm,
            url: requestUrl,
            load: function( type, data, evt )
            {
                //dojo.debug( "loaded content for url: " + this.url );
                //dojo.debug( "r e t r i e v e P o r t l e t C o n t e n t . l o a d" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  evt:" );
                //dojo.debugShallow( evt ) ;

                if ( jetspeed.debugPortletDumpRawContent )
                {
                    if ( dojo.lang.inArray( jetspeed.debugPortletDumpRawContent, portlet.entityId ) || dojo.lang.inArray( jetspeed.debugPortletDumpRawContent, "*" ) )
                        dojo.debug( "retrievePortletContent [" + portlet.entityId + "] content: " + data );
                }
                if ( portletContentListener && dojo.lang.isFunction( portletContentListener.notifySuccess ) )
                {
                    portletContentListener.notifySuccess( data, requestUrl, portlet ) ;
                }
            },
            error: function( type, error )
            {
                //dojo.debug( "r e t r i e v e P o r t l e t C o n t e n t . e r r o r" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  error:" );
                //dojo.debugShallow( error ) ;
                if ( portletContentListener && dojo.lang.isFunction( portletContentListener.notifyFailure ) )
                {
                    portletContentListener.notifyFailure( type, error, portlet );
                }
            },
            mimetype: "text/html"
        });     

    },
    setPortletContent: function(portletContent, renderUrl)
    {
        var windowWidget = this.getPortletWindow();
        if ( windowWidget )
        {
            windowWidget.setPortletContent( portletContent, renderUrl );
        }
    },

    putProperty: function(name, value)
    {
        this.properties[name] = value;
    },
    getProperty: function(name)
    {
        return this.properties[name];
    },
    removeProperty: function(name)
    {
        delete properties[name];
    }
}

// ... jetspeed.ui.PortalTaskBar
jetspeed.ui.PortalTaskBar = function()
{
    var tbProps = {};
    tbProps.templateCssPath = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlTaskBar.css") ;
    tbProps.templatePath = new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlTaskBarItemTemplate.html") ;
    // BOZO: improve this junk ^^^ 

    this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();
    this.templateCssPath = jetspeed.ui.getDefaultFloatingPaneTemplateCss();   // BOZO: this currently is responsible for assuring that 
                                                                              //       the base FloatingPane styles get included;
                                                                              //       so, if the taskbar is not included and/or an override
                                                                              //       css file is needed, the base FloatingPane styles may be absent
    this.taskbarProps = tbProps ;
    this.widgetId = "jetspeedTaskbar";

    this.createTaskBar();
}
jetspeed.ui.PortalTaskBar.prototype.createTaskBar = function()
{
    var nWidget = dojo.widget.createWidget('PortalTaskBar', this);
    nWidget.domNode.id = "jetspeedTaskbar";  // BOZO: must set the id here - it gets defensively cleared by dojo
    nWidget.domNode.style.cssText = "background-color: #666; width: 100%; bottom: 5px; height: 100px";

    var addToElmt = document.getElementById("jetspeedDesktop");
    addToElmt.appendChild(nWidget.domNode);
    
    this.taskbarWidget = nWidget;
}


// ... jetspeed.ui methods
jetspeed.ui.createPortletWindowWidget = function(/* Portlet */ portletObj, portletParameters)
{
    if ( ! portletParameters )
        portletParameters = {};
    portletParameters.portlet = portletObj;
    // NOTE: other parameters, such as widgetId could be set here (to override what PortletWindow does)
    var nWidget = dojo.widget.createWidget( 'PortletWindow', portletParameters );
    
    var addToElmt = document.getElementById( "jetspeedDesktop" );
    addToElmt.appendChild( nWidget.domNode );

    return nWidget.widgetId;
}

jetspeed.ui.getDefaultFloatingPaneTemplate = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlFloatingPane.html");   // BOZO: improve this junk
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss = function()
{
    return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlFloatingPane.css");   // BOZO: improve this junk
};


// ... fade-in convenience methods (work with set of nodes)
jetspeed.ui.fadeIn = function(nodes, duration, displayStyleValue)
{
    jetspeed.ui.fade(nodes, duration, displayStyleValue, 0, 1);
};
jetspeed.ui.fadeOut = function(nodes, duration)
{
    jetspeed.ui.fade(nodes, duration, "none", 1, 0);
};
jetspeed.ui.fade = function(nodes, duration, displayStyleValue, startOpac, endOpac)
{
    if ( nodes.length > 0 )
    {   // mimick dojo.fx.html.fade, but for all objects together
        for ( var i = 0 ; i < nodes.length ; i++ )
        {
            dojo.fx.html._makeFadeable(nodes[i]);
            if (displayStyleValue != "none")
                nodes[i].style.display = displayStyleValue ;
        }
        var anim = new dojo.animation.Animation(
		                new dojo.math.curves.Line([startOpac],[endOpac]),
		                duration, 0);
	    dojo.event.connect(anim, "onAnimate", function(e) {
            for ( var mi = 0 ; mi < nodes.length ; mi++ )
            {
                dojo.style.setOpacity(nodes[mi], e.x);
	        }});
        
        if (displayStyleValue == "none")
        {
            dojo.event.connect(anim, "onEnd", function(e) {
			    for ( var mi = 0 ; mi < nodes.length ; mi++ )
                    nodes[mi].style.display = displayStyleValue ;
		    });
        }
        anim.play(true);
    }
};
