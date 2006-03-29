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

// ... base objects
if ( ! window.jetspeed )
    jetspeed = {} ;
if ( ! jetspeed.om )
    jetspeed.om = {} ;
if ( ! jetspeed.ui )
    jetspeed.ui = {} ;
if ( ! jetspeed.ui.widget )
    jetspeed.ui.widget = {} ;

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

jetspeed.basePortalUrl = function()
{
    return document.location.protocol + "//" + document.location.host ;
}

jetspeed.basePortalDesktopUrl = function()
{
    return jetspeed.basePortalUrl() + "/jetspeed" ;
}

jetspeed.page = null ;   // BOZO: is this it? one page at a time?
jetspeed.initializeDesktop = function()
{
    jetspeed.loadPage();
}
jetspeed.loadPage = function()
{
    jetspeed.testLoadPageCreateWidgetPortlets();
}


// ... jetspeed debug options
//jetspeed.debugPortletEntityIdFilter = [ "dp-7" ]; // NOTE: uncomment causes only the listed portlets to be loaded; all others are ignored; for testing
jetspeed.debugPortletWindowIcons = [ "text-x-generic.png", "text-html.png", "application-x-executable.png" ];
jetspeed.debugPortletWindowThemes = [ "theme1", "theme2" ];
//jetspeed.debugPortletDumpRawContent = [ "dp-7" ];

jetspeed.testLoadPageCreateWidgetPortlets = function()
{
    jetspeed.page = new jetspeed.om.Page() ;
    jetspeed.currentTaskbar = new jetspeed.ui.PortalTaskBar() ;
    jetspeed.page.retrievePsml( new jetspeed.om.PageContentListenerCreateWidget() ) ;
}
jetspeed.testCreatePortletWindows = function( /* Portlet[] */ portlets, portletWindowFactory )
{
    if ( portlets )
    {
        var createdPortlets = [];
        for (var portletIndex in portlets)
        {
            var portlet = portlets[portletIndex];
            if ( jetspeed.debugPortletEntityIdFilter )
            {
                if (! dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter, portletIndex))
                    portlet = null;
            }
            if (portlet)
            {
                createdPortlets.push(portlet);
                portlet.createPortletWindow(portletWindowFactory,null,true);
            }
        }
        for (var i = 0; i < createdPortlets.length; i++)
            createdPortlets[i].retrievePortletContent();
    }
}


// ... jetspeed.doRender
jetspeed.doRender = function(url,portletEntityId,currentForm)
{
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        dojo.debug( "doRender [" + portletEntityId + "] url: " + url );
        targetPortlet.retrievePortletContent(null, url, currentForm);
    }
}
jetspeed.doRenderAll = function(url)
{
    var portlets = jetspeed.page.getPortlets();
    var renderMsg = ""
    var portletCount = 0;
    for (var portletIndex in portlets)
    {
        var portlet = portlets[portletIndex];
        if ( portletCount++ > 0 )
            renderMsg = renderMsg + ", ";
        renderMsg = renderMsg + portlet.entityId;
        portlet.retrievePortletContent(null, url);
    }
    dojo.debug( "doRenderAll [" + renderMsg + "] url: " + url );
}

// ... jetspeed.doAction
jetspeed.doAction = function(url,portletEntityId,currentForm)
{
    var targetPortlet = jetspeed.page.getPortlet( portletEntityId );
    if ( targetPortlet )
    {
        dojo.debug( "doAction [" + portletEntityId + "] url: " + url + " form: " + currentForm);
        targetPortlet.retrievePortletContent(new jetspeed.om.PortletActionContentListener(), url, currentForm);
    }
}

// ... jetspeed.om.PageContentListenerCreateWidget
jetspeed.om.PageContentListenerCreateWidget = function()
{
}
jetspeed.om.PageContentListenerCreateWidget.prototype =
{
    notifySuccess: function( /* Page */ page )
    {
        jetspeed.testCreatePortletWindows(page.getPortlets());
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Page */ page )
    {
        alert( "PageContentListenerCreateWidget notifyFailure type=" + type + " error=" + error ) ;
    }
}


// ... jetspeed.om.Page
jetspeed.om.Page = function(pagePsmlPath, pageName, pageTitle)
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
        var psmlPath = "/jetspeed/ajaxapi" ;
        var docPath = document.location.pathname ;
        
        var contextAndServletPath = "/jetspeed/desktop" ;
        var contextAndServletPathPos = docPath.indexOf( contextAndServletPath ) ;
        if ( contextAndServletPathPos != -1 && docPath.length > ( contextAndServletPathPos + contextAndServletPath.length ) )
        {
            psmlPath = psmlPath + docPath.substring( contextAndServletPathPos + contextAndServletPath.length ) ;
        }
        this.psmlPath = psmlPath ;
    },

    retrievePsml: function( pageContentListener )
    {
        var psmlUrl = null ;
        if ( this.psmlPath == null )
            this.setPsmlPathFromDocumentUrl() ;

        var psmlUrl = jetspeed.basePortalUrl() + this.psmlPath ;

        if ( djConfig.isDebug )
            dojo.debug( "psml url: " + psmlUrl ) ;

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
                page.getPortletsFromPSML(data);
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

    getPortletsFromPSML: function(psml)
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


// ... jetspeed.om.PortletWidgetWindowFactory
jetspeed.om.PortletWidgetWindowFactory = function()
{
}
jetspeed.om.PortletWidgetWindowFactory.prototype =
{
    create: function( /* Portlet */ portlet )
    {
        return new jetspeed.ui.PortletWidgetWindow(portlet);
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
    windowObj: null,
    
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
        return { url: justTheUrl, operation: op, portletEntityId: entityId };
    },

    postParseAnnotateContent: function( /* DOMNode */ containerNode )
    {   
        if ( containerNode )
        {
            var self = this;
            var cNode = containerNode;
            var formList = cNode.getElementsByTagName("form");
            if ( formList )
            {
                for ( var i = 0 ; i < formList.length ; i++ )
                {
                    var cForm = formList[i];                    
                    var cFormAction = cForm.action;
                    var cFormActionType = null;
                    var cFormPortletEntityId = this.entityId;  // BOZO:can I assume that it is always my entity-id (ignoring the one in parsedPseudoUrl)

                    var parsedPseudoUrl = this.parseJSPseudoUrlActionRender( cFormAction );
                    
                    var newActionUrl = parsedPseudoUrl.url;
                    var submitOperation = parsedPseudoUrl.operation;

                    if ( submitOperation == this.PORTLET_REQUEST_ACTION || submitOperation == this.PORTLET_REQUEST_RENDER )
                    {
                        //dojo.debug( "postParseAnnotateContent " + this.entityId + " adding onSubmit (portlet-" + submitOperation + ") and changing form action attribute from: " + cFormAction + " to: " +  newActionUrl );
                        cForm.action = newActionUrl;
                        dojo.event.connect(cForm, "onsubmit", function(e) {
                            e.preventDefault();
                            if ( submitOperation == self.PORTLET_REQUEST_ACTION )
                                doAction( cForm.action, cFormPortletEntityId, cForm);
                            else
                                doRender( cForm.action, cFormPortletEntityId, cForm);
                        });
                    }
                }
            }
        }
    },
    getCurrentWindowState: function()
    {
        if ( ! this.windowObj ) return null;
        return this.windowObj.getCurrentWindowState();
    },
    getChangedWindowState: function()
    {
        var currentState = this.getCurrentWindowState();
        var lastSaved = this.lastSavedWindowState;
        if ( ! lastSaved )
            return currentState;
        
        return currentState;
    },
    createPortletWindow: function(portletWindowFactory, portletContentListener, doNotRetrieveContent)
    {
        if ( portletWindowFactory == null )
            portletWindowFactory = new jetspeed.om.PortletWidgetWindowFactory() ;
        
        this.windowFactory = portletWindowFactory ;
        this.windowObj = portletWindowFactory.create( this ) ;

        if (! doNotRetrieveContent)
            this.retrievePortletContent(portletContentListener) ;
    },

    getPortletUrl: function(renderUrl)
    {
        var queryString = "?entity=" + this.entityId + "&portlet=" + this.name + "&encoder=desktop";
        if (renderUrl)
            return renderUrl + queryString;
        return jetspeed.basePortalUrl() + "/jetspeed/portlet" + queryString;
    },

    retrievePortletContent: function(portletContentListener,renderOrActionUrl,actionForm)
    {
        if ( portletContentListener == null )
            portletContentListener = new jetspeed.om.PortletContentListener() ;
        var portlet = this ;
        var requestUrl = portlet.getPortletUrl(renderOrActionUrl);
        dojo.io.bind({
            formNode: actionForm,
            url: requestUrl,
            load: function(type, data, evt)
            {
                //dojo.debug( "loaded content for url: " + this.url );
                //dojo.debug( "r e t r i e v e P o r t l e t C o n t e n t . l o a d" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  evt:" );
                //dojo.debugShallow( evt ) ;

                if ( jetspeed.debugPortletDumpRawContent )
                {
                    if (dojo.lang.inArray(jetspeed.debugPortletDumpRawContent, portlet.entityId))
                        dojo.debug( portlet.entityId + " content: " + data);  
                }
                if ( portletContentListener && dojo.lang.isFunction( portletContentListener.notifySuccess ) )
                {
                    portletContentListener.notifySuccess(data, requestUrl, portlet);
                }
            },
            error: function(type, error)
            {
                //dojo.debug( "r e t r i e v e P o r t l e t C o n t e n t . e r r o r" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  error:" );
                //dojo.debugShallow( error ) ;
                if ( portletContentListener && dojo.lang.isFunction( portletContentListener.notifyFailure ) )
                {
                    portletContentListener.notifyFailure(type, error, portlet);
                }
            },
            mimetype: "text/html"
        });     

    },
    setPortletContent: function(portletContent, renderUrl)
    {
        if ( this.windowObj )
        {
            this.windowObj.setPortletContent(portletContent, renderUrl);
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
    tbProps.templateCssPath = new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlTaskBar.css") ;
    tbProps.templatePath = new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlTaskBarItemTemplate.html") ;
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


// ... jetspeed.ui.PortletWidgetWindow
jetspeed.ui.PortletWidgetWindow = function(/* Portlet */ portletObj)
{
    this.portlet = portletObj;

    this.title = portletObj.title;
    
    var windowid = portletObj.getProperty("window-id");
    if ( windowid )
        this.widgetId = windowid;
    else
        this.widgetId = "portletWindow_" + portletObj.entityId ;   // this.incrementNextIndex();
    this.incrementNextIndex();

    this.blee = "fred" ;

    var windowtheme = portletObj.getProperty("window-theme");
    
    if (! windowtheme)
    {
        if ( jetspeed.debugPortletWindowThemes )
        {
            windowtheme = jetspeed.debugPortletWindowThemes[Math.floor(Math.random()*jetspeed.debugPortletWindowThemes.length)];
        }
    }
    if (windowtheme)
    {
        this.portletWindowTheme = windowtheme ;
        this.templateCssPath = new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowthemes/" + windowtheme + "/" + windowtheme + ".css");   // BOZO: improve this junk
    }
    this.templatePath = jetspeed.ui.getDefaultFloatingPaneTemplate();

    var windowicon = portletObj.getProperty("window-icon");
    if (! windowicon)
    {
        if ( jetspeed.debugPortletWindowIcons )
        {
            windowicon = jetspeed.debugPortletWindowIcons[Math.floor(Math.random()*jetspeed.debugPortletWindowIcons.length)];
        }
    }
    if ( windowicon )
        this.iconSrc =  new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/" + windowicon ) ;
    else
        this.iconSrc =  new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/document.gif" ) ;

    //dojo.debug("PortletWidgetWindow  widgetId=" + this.widgetId + "  windowtheme=" + windowtheme + "  templateCssPath=" + this.templateCssPath);

    this.createWindow();
}
jetspeed.ui.PortletWidgetWindow.prototype.title = "Unknown Portlet";
jetspeed.ui.PortletWidgetWindow.prototype.constrainToContainer = "1";
jetspeed.ui.PortletWidgetWindow.prototype.contentWrapper = "layout";
jetspeed.ui.PortletWidgetWindow.prototype.displayCloseAction = true;
jetspeed.ui.PortletWidgetWindow.prototype.displayMinimizeAction = true;
jetspeed.ui.PortletWidgetWindow.prototype.displayMaximizeAction = true;
jetspeed.ui.PortletWidgetWindow.prototype.taskBarId = "jetspeedTaskbar";
jetspeed.ui.PortletWidgetWindow.prototype.nextIndex = 1;
jetspeed.ui.PortletWidgetWindow.prototype.titleMouseIn = 0;
jetspeed.ui.PortletWidgetWindow.prototype.titleLit = false;
jetspeed.ui.PortletWidgetWindow.prototype.titleButtonInclude = function(condition, requiredResult, button, included)
{
    if ( button == null ) return included ;
    if (dojo.lang.isFunction(condition))
    {
        if (condition.call(this) == requiredResult)
            included.push(button);
    }
    else if ( condition == requiredResult )
    {
        included.push(button);
    }
    return included;
}
jetspeed.ui.PortletWidgetWindow.prototype.minimizeWindow = function(evt)
{
    var tbiWidget = dojo.widget.byId(this.widgetId + "_tbi");

    if ( tbiWidget && tbiWidget.domNode )
        dojo.fx.html.implode( this.domNode, tbiWidget.domNode, 340 ) ; // began as 300 in ff
    else
        this.hide();
    
    this.windowState = "minimized";
}
jetspeed.ui.PortletWidgetWindow.prototype.titleLight = function(fpWidget)
{
    var mightBeEnlightened = [] ;
    this.titleButtonInclude(fpWidget.displayMinimizeAction, true, fpWidget.minimizeAction, mightBeEnlightened);
    this.titleButtonInclude(fpWidget.displayMaximizeAction, true, fpWidget.maximizeAction, mightBeEnlightened);
    this.titleButtonInclude(fpWidget.displayRestoreAction, true, fpWidget.restoreAction, mightBeEnlightened);
    this.titleButtonInclude(fpWidget.displayCloseAction, true, fpWidget.closeAction, mightBeEnlightened);
    var toBeEnlightened = [] ;
    for ( var i = 0 ; i < mightBeEnlightened.length ; i++ )
    {
        var btn = mightBeEnlightened[i];
        if (btn.style.display == "none")
            toBeEnlightened.push(btn);
    }
    jetspeed.ui.fadeIn(toBeEnlightened, 325, "");
    fpWidget.titleLit = true ;
}
jetspeed.ui.PortletWidgetWindow.prototype.titleDim = function(fpWidget, immediateForce)
{
    var mightBeExtinguished = [ fpWidget.restoreAction, fpWidget.maximizeAction, fpWidget.minimizeAction, fpWidget.closeAction ] ;
    var toBeExtinguished = [] ;
    for ( var i = 0 ; i < mightBeExtinguished.length ; i++ )
    {
        var btn = mightBeExtinguished[i];
        if (immediateForce)
            btn.style.display = "none" ;
        else if (btn.style.display != "none")
            toBeExtinguished.push(btn);
    }
    jetspeed.ui.fadeOut(toBeExtinguished, 280);
    fpWidget.titleLit = false ;
};
jetspeed.ui.PortletWidgetWindow.prototype.titleMouseOver = function(evt)
{
    var self = this ;
    this.titleMouseIn = 1 ;   // was ++
    window.setTimeout( function() { if ( self.titleMouseIn > 0 ) { self.titleLight( self ); self.titleMouseIn = 0; } }, 270 ) ;
        // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
}
jetspeed.ui.PortletWidgetWindow.prototype.titleMouseOut = function(evt)
{
    var self = this ;
    var nTitleMouseIn = this.titleMouseIn ;
    if ( nTitleMouseIn > 0 )
    {
        nTitleMouseIn = 0 ; // was Math.max( 0, ( nTitleMouseIn - 1 ) );
        this.titleMouseIn = nTitleMouseIn ;
    }
    if ( nTitleMouseIn == 0 && this.titleLit )
    {
        window.setTimeout( function() { if ( self.titleMouseIn == 0 && self.titleLit ) { self.titleDim( self ); } }, 200 ) ;
        // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
    }
}

jetspeed.ui.PortletWidgetWindow.prototype.incrementNextIndex = function()
{
    var nextI = jetspeed.ui.PortletWidgetWindow.prototype.nextIndex;
    jetspeed.ui.PortletWidgetWindow.prototype.nextIndex++;
    return nextI;
}
jetspeed.ui.PortletWidgetWindow.prototype.getNextIndex = function()
{
    return jetspeed.ui.PortletWidgetWindow.prototype.nextIndex;
}
jetspeed.ui.PortletWidgetWindow.prototype.createWindow = function()
{
    if ( dojo.render.html.mozilla )  // dojo.render.html.ie
    {
        this.hasShadow = "true";
    }

    var nWidget = dojo.widget.createWidget('FloatingPane', this);

    nWidget.domNode.id = this.widgetId;  // BOZO: must set the id here - it gets defensively cleared by dojo
    if ( this.portletWindowTheme )
        nWidget.domNode.className = this.portletWindowTheme + " " + nWidget.domNode.className ;
    nWidget.domNode.style.width = "280px";
    nWidget.domNode.style.height = "200px";
    // NOTE: the width/height specified get updated when the size is changed by the user
    nWidget.domNode.style.left = (((this.getNextIndex() -2) * 30 ) + 200) + "px";
    nWidget.domNode.style.top = (((this.getNextIndex() -2) * 30 ) + 170) + "px";
    
    this.titleDim(nWidget, true);

    var addToElmt = document.getElementById("jetspeedDesktop");
    addToElmt.appendChild(nWidget.domNode);
    
    //dojo.debug( this.portlet.title + " title-bar css: " + nWidget.titleBar.style.cssText ) ;

    this.windowWidget = nWidget;
}
jetspeed.ui.PortletWidgetWindow.prototype.getCurrentWindowState = function()
{
    if ( ! this.windowWidget || ! this.windowWidget.domNode ) return null;
    var cWinState = {};
    cWinState.zIndex = this.windowWidget.domNode.style.zIndex;
    cWinState.width = this.windowWidget.domNode.style.width;
    cWinState.height = this.windowWidget.domNode.style.heigth;
    cWinState.left = this.windowWidget.domNode.style.left;
    cWinState.top = this.windowWidget.domNode.style.top;
    return cWinState;
}
jetspeed.ui.PortletWidgetWindow.prototype.setPortletContent = function(html,url)
{
    if (this.windowWidget)
    {
        this.windowWidget.setContent( { titles: [], scripts: [], linkStyles: [], styles: [], remoteScripts: [], xml: html.toString(), url: url } );
        this.portlet.postParseAnnotateContent( this.windowWidget.containerNode );
    }
}

// ... jetspeed.ui methods
jetspeed.ui.getDefaultFloatingPaneTemplate = function()
{
    return new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlFloatingPane.html");   // BOZO: improve this junk
}
jetspeed.ui.getDefaultFloatingPaneTemplateCss = function()
{
    return new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/widget/HtmlFloatingPane.css");   // BOZO: improve this junk
}


// ... fade-in convenience methods (work with set of nodes)
jetspeed.ui.fadeIn = function(nodes, duration, displayStyleValue)
{
    jetspeed.ui.fade(nodes, duration, displayStyleValue, 0, 1);
}
jetspeed.ui.fadeOut = function(nodes, duration)
{
    jetspeed.ui.fade(nodes, duration, "none", 1, 0);
}
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
}
