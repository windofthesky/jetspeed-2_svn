

jetspeed.testLoadPageCreateDivPortlets = function()
{
    jetspeed.currentPage = new jetspeed.om.Page() ;
    jetspeed.page.retrievePsml( new jetspeed.om.PageContentListenerCreateDiv() ) ;
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
