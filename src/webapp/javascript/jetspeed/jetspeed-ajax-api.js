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
jetspeed = {} ;
jetspeed.om = {} ;
jetspeed.ui = {} ;

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

jetspeed.testLoadPageCreateWidgetPortlets = function()
{
    var page = new jetspeed.om.Page() ;
    page.retrievePsml( new jetspeed.om.PageContentListenerCreateWidget() ) ;
}

jetspeed.testLoadPageCreateDivPortlets = function()
{
    var page = new jetspeed.om.Page() ;
    page.retrievePsml( new jetspeed.om.PageContentListenerCreateDiv() ) ;
}

jetspeed.testCreatePortletWindows = function( /* Portlet[] */ portlets, portletWindowFactory )
{
    //if ( ! dojo.lang.isArray(portlets) && ! dojo.lang.isObject(portlets) )
    //{
    //    if ( portlets && dojo.lang.isSubOf(portlets, jetspeed.om.Portlet))
    //        portlets = [portlets];
    //    else
    //        portlets = null;
    //}
    if ( portlets )
    {
        for (var portletIndex in portlets)
        {
            var portlet = portlets[portletIndex];
            portlet.createPortletWindow(portletWindowFactory);
        }
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

// ... jetspeed.om.PageContentListenerCreateDiv
jetspeed.om.PageContentListenerCreateDiv = function()
{
}
jetspeed.om.PageContentListenerCreateDiv.prototype =
{
    notifySuccess: function( /* Page */ page )
    {
        jetspeed.testCreatePortletWindows(page.getPortlets(), new jetspeed.om.PortletDivWindowFactory());
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Page */ page )
    {
        alert( "PageContentListenerCreateDiv notifyFailure type=" + type + " error=" + error ) ;
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

        var psmlUrl = jetspeed.basePortalUrl() + this.psmlPath ;   // BOZO: is it appropriate to fix the baseUrl as done here?

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
    }
}

// ... jetspeed.om.PortletContentListener
jetspeed.om.PortletContentListener = function()
{
}
jetspeed.om.PortletContentListener.prototype =
{
    notifySuccess: function( /* String */ portletContent, /* Portlet */ portlet )
    {
        portlet.setPortletContent( portletContent );
    },
    notifyFailure: function( /* String */ type, /* String */ error, /* Portlet */ portlet )
    {
        alert( "PortletContentListener notifyFailure type=" + type + " error=" + error ) ;
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

// ... jetspeed.om.PortletDivWindowFactory
jetspeed.om.PortletDivWindowFactory = function()
{
}
jetspeed.om.PortletDivWindowFactory.prototype =
{
    create: function( /* Portlet */ portlet )
    {
        return new jetspeed.ui.PortletDivWindow(portlet);
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
jetspeed.om.Portlet.prototype =   /* defining prototypes like this is not cool if the object uses dojo.inherits (this would replace pt)*/
{                                 /* dojo.lang.extend would allow this syntax instead of [<type>.prototype.<propname> = <propval>]* */
    name: null,
    entityId: null,
    title: null,
    
    windowFactory: null,
    windowObj: null,
    
    createPortletWindow: function(portletWindowFactory, portletContentListener)
    {
        if ( portletWindowFactory == null )
            portletWindowFactory = new jetspeed.om.PortletWidgetWindowFactory() ;
        
        this.windowFactory = portletWindowFactory ;
        this.windowObj = portletWindowFactory.create( this ) ;

        this.retrievePortletContent(portletContentListener) ;
    },

    getPortletUrl: function()
    {
        return jetspeed.basePortalUrl() + "/jetspeed/portlet?entity=" + this.entityId + "&portlet=" + this.name + "&encoder=desktop";
    },

    retrievePortletContent: function(portletContentListener)
    {
        if ( portletContentListener == null )
            portletContentListener = new jetspeed.om.PortletContentListener() ;

        var portlet = this ;
        dojo.io.bind({
            url: portlet.getPortletUrl(),
            load: function(type, data, evt)
            {
                //dojo.debug( "r e t r i e v e P o r t l e t C o n t e n t . l o a d" ) ;
                //dojo.debug( "  type:" );
                //dojo.debugShallow( type ) ;
                //dojo.debug( "  evt:" );
                //dojo.debugShallow( evt ) ;
                if ( portletContentListener && dojo.lang.isFunction( portletContentListener.notifySuccess ) )
                {
                    portletContentListener.notifySuccess(data, portlet);
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
    setPortletContent: function( portletContent )
    {
        if ( this.windowObj )
        {
            this.windowObj.setPortletContent(portletContent);
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
    if ( windowtheme )
    {
        this.portletWindowTheme = windowtheme ;
        this.templateCssPath = new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowthemes/" + windowtheme + "/" + windowtheme + ".css");   // BOZO: improve this junk
    }

    var windowicon = portletObj.getProperty("window-icon");
    if ( windowicon )
        this.iconSrc =  new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/" + windowicon ) ;
    else
        this.iconSrc =  new dojo.uri.Uri(jetspeed.basePortalDesktopUrl(), "jetspeed/javascript/desktop/windowicons/document.gif" ) ;

    dojo.debug("PortletWidgetWindow  widgetId=" + this.widgetId + "  windowtheme=" + windowtheme + "  templateCssPath=" + this.templateCssPath);


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
jetspeed.ui.PortletWidgetWindow.prototype.titleMouseOver = function(evt)
{
    this.titleLight( this ) ;  // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
}
jetspeed.ui.PortletWidgetWindow.prototype.titleLight = function(fpWidget)
{
    fpWidget.minimizeAction.style.display = (fpWidget.displayMinimizeAction ? "" : "none");
    fpWidget.maximizeAction.style.display = (fpWidget.displayMaximizeAction && fpWidget.windowState!="maximized" ? "" : "none");
    fpWidget.restoreAction.style.display = (fpWidget.displayMaximizeAction && fpWidget.windowState!="normal" ? "" : "none");
    fpWidget.closeAction.style.display = (fpWidget.displayCloseAction ? "" : "none");
};
jetspeed.ui.PortletWidgetWindow.prototype.titleMouseOut = function(evt)
{
    this.titleDim( this ) ;   // NOTE: setup in template HtmlFloatingPane.html: dojoAttachEvent="onMouseOver:titleMouseOver;onMouseOut:titleMouseOut"
}
jetspeed.ui.PortletWidgetWindow.prototype.titleDim = function(fpWidget)
{
    fpWidget.restoreAction.style.display="none";
    fpWidget.maximizeAction.style.display="none";
    fpWidget.minimizeAction.style.display="none";
    fpWidget.closeAction.style.display="none";
};

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
    var nWidget = dojo.widget.createWidget('FloatingPane', this);

    nWidget.domNode.id = this.widgetId;  // BOZO: must set the id here - it gets defensively cleared by dojo
    if ( this.portletWindowTheme )
        nWidget.domNode.className = this.portletWindowTheme + " " + nWidget.domNode.className ;
    nWidget.domNode.style.width = "280px";
    nWidget.domNode.style.height = "200px";
    // NOTE: the width/height specified get updated when the size is changed by the user
    nWidget.domNode.style.left = (((this.getNextIndex() -2) * 30 ) + 100) + "px";
    nWidget.domNode.style.top = (((this.getNextIndex() -2) * 30 ) + 100) + "px";
    
    this.titleDim(nWidget);

    var addToElmt = document.getElementById("jetspeedDesktop");
    addToElmt.appendChild(nWidget.domNode);
    
    //dojo.debug( this.portlet.title + " title-bar css: " + nWidget.titleBar.style.cssText ) ;

    this.windowWidget = nWidget;
}
jetspeed.ui.PortletWidgetWindow.prototype.setPortletContent = function(html)
{
    if (this.windowWidget)
        this.windowWidget.setContent(html);
}


// ... jetspeed.ui.PortletDivWindow
jetspeed.ui.PortletDivWindow = function( /* Portlet */ portletObj )
{
	this.portlet = portletObj;
	this.loaded = false;
    this.buildPortlet();
}
jetspeed.ui.PortletDivWindow.prototype.buildPortlet = function()
{
    var self = this;
	var divPortlet = document.createElement("div");
    this.portlet_element = divPortlet;
	divPortlet.className = "portletBody";
	divPortlet.dataObj = this;

	var divPortletFrame = document.createElement("div");
	this.child_portletFrame = divPortletFrame;
	divPortletFrame.className = "portletFrame";

	var divPortletHeader = document.createElement("div");
	this.child_portletHeader = divPortletHeader;
	divPortletHeader.className = "portletHeader";
	
	divPortletHeader.onmouseover = function(){
		self.highlight();
	}
	divPortletHeader.onmouseout = function(){
		self.unHighlight();
	}

	var divShowHide = document.createElement("div");
	this.child_showHide = divShowHide;
	divShowHide.className = "showHide";
	divShowHide.innerHTML = (this.portlet.status==0) ? '<img src="/jetspeed/themes/blue/images/showMod.gif"/>' : '<img src="/jetspeed/themes/blue/images/hideMod.gif"/>';
	divShowHide.style.visibility = "hidden";		
    dojo.event.connect( divShowHide, "onmousedown", showHide ) ;

	var divTitle = document.createElement("div");
	this.child_title = divTitle;
	divTitle.className = "title";
	divTitle.appendChild(document.createTextNode(this.portlet.name));

	var divClose = document.createElement("div");
	this.child_close = divClose;
	divClose.className = "close";
	divClose.innerHTML = '<img src="/jetspeed/themes/blue/images/closeMod.gif"/>';
	divClose.style.display = "none";
    dojo.event.connect( divClose, "onmousedown", close ) ;
	
	var divEditContent = document.createElement("div");
	this.child_editContent = divEditContent;
	divEditContent.className = "editContent";

	var divPortletContent = document.createElement("div");
	this.child_moduleContent = divPortletContent;
	divPortletContent.className = "moduleContent";
	divPortletContent.innerHTML = "Loading ...";
	if (this.portlet.status==0) divPortletContent.style.display = "none";

	divPortletHeader.appendChild(divShowHide);
	divPortletHeader.appendChild(divClose);
	divPortletHeader.appendChild(divTitle);

	divPortletFrame.appendChild(divPortletHeader);
	divPortletFrame.appendChild(divEditContent);
	divPortletFrame.appendChild(divPortletContent);

	divPortlet.appendChild(divPortletFrame);

	function showHide(e) {
		e.cancelBubble = true;
		self.showHideModule();
	}
	function close(e) {
		e.cancelBubble = true;
		self.close();
		delete self;
	}
	
    var addtoElmt = document.getElementById( "jetspeedDesktop" ) ;
    addtoElmt.appendChild(divPortlet);

    var dragSource = new dojo.dnd.HtmlDragMoveSource(divPortlet) ;
    dragSource.setDragHandle( divPortletHeader ) ;
    
    //Drag.init(divPortletHeader, divPortlet);
    
}
jetspeed.ui.PortletDivWindow.prototype.setPortletContent = function( html )
{
    this.child_moduleContent.innerHTML = html ;
}
jetspeed.ui.PortletDivWindow.prototype.highlight = function() {
    this.child_showHide.style.visibility = "visible";
    this.child_close.style.display = "block";
}
jetspeed.ui.PortletDivWindow.prototype.unHighlight = function() {
    this.child_portletFrame.style.border = "1px solid #79A7E2";
    this.child_showHide.style.visibility = "hidden";
    this.child_close.style.display = "none";
}
jetspeed.ui.PortletDivWindow.prototype.showHideModule = function() {
	if (arguments[0] != undefined) {
		arguments[0] ? this.show() : this.hide();
	} else {
		this.child_moduleContent.style.display=='none' ? this.show() : this.hide();
	}
}
jetspeed.ui.PortletDivWindow.prototype.close = function()
{
    this.portlet_element.parentNode.removeChild(this.portlet_element);
}
jetspeed.ui.PortletDivWindow.prototype.show = function()
{
	this.child_moduleContent.style.display = 'block';
	this.child_showHide.firstChild.setAttribute("src", "/jetspeed/themes/blue/images/hideMod.gif");
	this.portlet.status = 1;
}
jetspeed.ui.PortletDivWindow.prototype.hide = function()
{
	this.child_moduleContent.style.display = 'none';
	this.child_showHide.firstChild.setAttribute("src", "/jetspeed/themes/blue/images/showMod.gif");
	this.child_editContent.style.display = "none";
	this.portlet.status = 0;
}

