dojo.provide("jetspeed.widget.PortletWindow");
dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");
jetspeed.widget.PortletWindow=function(){
dojo.widget.FloatingPane.call(this);
this.widgetType="PortletWindow";
this.resizable=true;
this.movable=true;
this.portletInitialized=false;
this.actionButtons={};
this.actionMenus={};
};
dojo.inherits(jetspeed.widget.PortletWindow,dojo.widget.FloatingPane);
dojo.lang.extend(jetspeed.widget.PortletWindow,{title:"Unknown Portlet",contentWrapper:"layout",displayCloseAction:true,displayMinimizeAction:true,displayMaximizeAction:true,displayRestoreAction:true,hasShadow:false,nextIndex:1,windowDecorationName:null,windowDecorationConfig:null,windowPositionStatic:false,windowHeightToFit:false,titleMouseIn:0,titleLit:false,portlet:null,jsAltInitParams:null,templateDomNodeClassName:null,templateContainerNodeClassName:null,processingContentChanged:false,lastUntiledPositionInfo:null,lastTiledPositionInfo:null,minimizeWindowTemporarilyRestoreTo:null,executeScripts:false,scriptSeparation:false,adjustPaths:false,staticDefineAsAltInitParameters:function(_1,_2){
if(!_1){
_1={getProperty:function(_3){
if(!_3){
return null;
}
return this.jsAltInitParams[_3];
},putProperty:function(_4,_5){
if(!_4){
return;
}
this.jsAltInitParams[_4]=_5;
},retrieveContent:function(_6,_7){
var _8=this.getProperty(jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER);
if(_8){
_8.getContent(_7,_6,this,jetspeed.debugPortletDumpRawContent);
}else{
jetspeed.url.retrieveContent(_7,_6,this,jetspeed.debugPortletDumpRawContent);
}
}};
}
if(!_2){
_2={};
}
if(_2.jsAltInitParams){
_1.jsAltInitParams=_2.jsAltInitParams;
}else{
_1.jsAltInitParams=_2;
}
return _1;
},getInitProperty:function(_9,_a){
var _b=null;
if(this.portlet){
_b=this.portlet.getProperty(_9);
if(_b==null&&_a){
_b=this.portlet.getProperty(_a);
}
}else{
if(this.jsAltInitParams){
_b=this.jsAltInitParams[_9];
if(_b==null&&_a){
_b=this.jsAltInitParams[_a];
}
}
}
return _b;
},setInitProperty:function(_c,_d){
if(this.portlet){
dojo.raise("PortletWindow.setInitProperty cannot be called when the window is bound to a portlet");
}else{
if(!this.jsAltInitParams){
this.jsAltInitParams={};
}
this.jsAltInitParams[_c]=_d;
}
},initWindowDecoration:function(_e){
var _f=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION);
if(!_f){
if(this.portletDecorationName){
_f=this.portletDecorationName;
}else{
_f=jetspeed.page.getPortletDecorationDefault();
}
}
this.windowDecorationName=_f;
var _10=jetspeed.loadPortletDecorationStyles(_f);
this.windowDecorationConfig=_10;
this.templateCssPath="";
},initWindowTitle:function(_11){
var _12=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
this.setPortletTitle(_12);
},initWindowIcon:function(_13){
if(this.windowDecorationConfig!=null&&this.windowDecorationConfig.windowIconEnabled&&this.windowDecorationConfig.windowIconPath!=null){
var _14=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_ICON);
if(!_14){
_14="document.gif";
}
this.iconSrc=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+this.windowDecorationConfig.windowIconPath+_14);
if(this.portletInitialized&&this.titleBarIcon){
this.titleBarIcon.src=this.iconSrc.toString();
}
}else{
this.iconSrc=null;
}
},initWindowDimensions:function(_15){
this.windowPositionStatic=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
this.windowHeightToFit=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
this.windowColumnSpan=this.getInitProperty(jetspeed.id.PORTLET_PROP_COLUMN_SPAN);
this.constrainToContainer=0;
var _16=null,_17=null,_18=null,_19=null;
if(this.portlet){
var _1a=this.portlet.getInitialWindowDimensions();
_16=_1a.width;
_17=_1a.height;
_18=_1a.left;
_19=_1a.top;
}else{
_16=this.getInitProperty(jetspeed.id.PORTLET_PROP_WIDTH);
_17=this.getInitProperty(jetspeed.id.PORTLET_PROP_HEIGHT);
_18=this.getInitProperty(jetspeed.id.PORTLET_PROP_LEFT);
_19=this.getInitProperty(jetspeed.id.PORTLET_PROP_TOP);
}
this.lastUntiledPositionInfo={};
this.lastTiledPositionInfo={width:""};
if(_16!=null&&_16>0){
_16=Math.floor(_16);
this.lastUntiledPositionInfo.width=_16;
}else{
_16=jetspeed.prefs.windowWidth;
if(!this.windowPositionStatic){
this.lastUntiledPositionInfo.width=_16;
}
}
if(_17!=null&&_17>0){
_17=Math.floor(_17);
this.lastUntiledPositionInfo.height=_17;
this.lastTiledPositionInfo.height=_17;
}else{
_17=jetspeed.prefs.windowHeight;
this.lastTiledPositionInfo.height=_17;
if(!this.windowPositionStatic){
this.lastUntiledPositionInfo.height=_17;
}
}
if(_18!=null&&_18>=0){
_18=Math.floor(_18);
this.lastUntiledPositionInfo.left=_18;
}else{
_18=(((this.portletIndex-2)*30)+200);
if(!this.windowPositionStatic){
this.lastUntiledPositionInfo.left=_18;
}
}
if(_19!=null&&_19>=0){
_19=Math.floor(_19);
this.lastUntiledPositionInfo.top=_19;
}else{
_19=(((this.portletIndex-2)*30)+170);
if(!this.windowPositionStatic){
this.lastUntiledPositionInfo.top=_19;
}
}
_16=_16+"px";
_17=_17+"px";
_18=_18+"px";
_19=_19+"px";
if(!this.portletInitialized){
var _1b=this.getFragNodeRef(_15);
var _1c="width: "+_16+((_17!=null&&_17.length>0)?("; height: "+_17):"");
if(!this.windowPositionStatic){
_1c+="; left: "+_18+"; top: "+_19+";";
}
_1b.style.cssText=_1c;
}else{
this.domNode.style.position="absolute";
this.domNode.style.width=_16;
this.domNode.style.height=_17;
if(!this.windowPositionStatic){
this.domNode.style.left=_18;
this.domNode.style.top=_19;
}
}
},portletMixinProperties:function(_1d){
this.initWindowDecoration(_1d);
this.initWindowTitle(_1d);
this.initWindowIcon(_1d);
this.initWindowDimensions(_1d);
},postMixInProperties:function(_1e,_1f,_20){
jetspeed.widget.PortletWindow.superclass.postMixInProperties.apply(this,arguments);
this.portletIndex=this._getNextIndex();
var _21=this.getInitProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(this.portlet){
if(this.widgetId){
dojo.raise("PortletWindow.widgetId ("+this.widgetId+") should not be assigned directly");
}
if(!_21){
dojo.raise("PortletWindow.widgetId is not defined for portlet ["+this.portlet.entityId+"] - Portlet.initialize may not have been called");
}
this.widgetId=_21;
}else{
if(_21){
this.widgetId=_21;
}else{
if(!this.widgetId){
this.widgetId=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+this.portletIndex;
}
}
}
this._incrementNextIndex();
this.templatePath=jetspeed.ui.getDefaultFloatingPaneTemplate();
this.portletMixinProperties(_1f);
},_incrementNextIndex:function(){
var _22=jetspeed.widget.PortletWindow.prototype.nextIndex;
if(!_22){
jetspeed.widget.PortletWindow.prototype.nextIndex=1;
}
jetspeed.widget.PortletWindow.prototype.nextIndex++;
return _22;
},_getNextIndex:function(){
return jetspeed.widget.PortletWindow.prototype.nextIndex;
},portletInitDragHandle:function(){
var _23=this.resizable;
if(_23){
this.resizeBar.style.display="block";
var _24=this.widgetId+"_resize";
if(!this.portletInitialized){
this.resizeHandle=dojo.widget.createWidget("jetspeed:PortletWindowResizeHandle",{targetElmId:this.widgetId,id:_24,portletWindow:this});
}else{
this.resizeHandle=dojo.widget.byId(_24);
}
if(this.resizeHandle){
this.resizeHandle.domNode.style.position="static";
if(!this.portletInitialized){
this.resizeBar.appendChild(this.resizeHandle.domNode);
}
}
}
},fillInTemplate:function(_25,_26){
var _27=this.getFragNodeRef(_26);
dojo.html.copyStyle(this.domNode,_27);
document.body.appendChild(this.domNode);
if(!this.isShowing()){
this.windowState=jetspeed.id.ACTION_NAME_MINIMIZE;
}
if(this.iconSrc==null||this.iconSrc==""){
dojo.dom.removeNode(this.titleBarIcon);
}else{
this.titleBarIcon.src=this.iconSrc.toString();
}
if(this.titleBarDisplay!="none"){
this.titleBar.style.display="";
dojo.html.disableSelection(this.titleBar);
this.titleBarIcon.style.display=(this.iconSrc==""?"none":"");
var _28=null;
if(this.windowDecorationConfig!=null){
var _29=new Array();
var _2a=false;
if(this.windowDecorationConfig.windowActionButtonOrder!=null){
var _2b=new Array();
if(this.portlet){
for(var _2c=(this.windowDecorationConfig.windowActionButtonOrder.length-1);_2c>=0;_2c--){
var _2d=this.windowDecorationConfig.windowActionButtonOrder[_2c];
var _2e=false;
if(this.portlet.getAction(_2d)!=null||jetspeed.prefs.windowActionDesktop[_2d]!=null){
_2e=true;
}else{
if(_2d==jetspeed.id.ACTION_NAME_RESTORE||_2d==jetspeed.id.ACTION_NAME_MENU){
_2e=true;
}
}
if(_2e){
_2b.push(_2d);
}
}
}else{
for(var _2c=(this.windowDecorationConfig.windowActionButtonOrder.length-1);_2c>=0;_2c--){
var _2d=this.windowDecorationConfig.windowActionButtonOrder[_2c];
var _2e=false;
if(_2d==jetspeed.id.ACTION_NAME_MINIMIZE||_2d==jetspeed.id.ACTION_NAME_MAXIMIZE||_2d==jetspeed.id.ACTION_NAME_RESTORE||_2d==jetspeed.id.ACTION_NAME_MENU||jetspeed.prefs.windowActionDesktop[_2d]!=null){
_2e=true;
}
if(_2e){
_2b.push(_2d);
}
}
}
var _2f=(this.windowDecorationConfig.windowActionButtonMax==null?-1:this.windowDecorationConfig.windowActionButtonMax);
if(_2f!=-1&&_2b.length>=_2f){
var _30=0;
var _31=_2b.length-_2f+1;
for(var i=0;i<_2b.length&&_30<_31;i++){
if(_2b[i]!=jetspeed.id.ACTION_NAME_MENU){
_29.push(_2b[i]);
_2b[i]=null;
_30++;
}
}
}
if(this.windowDecorationConfig.windowActionNoImage!=null){
for(var i=0;i<_2b.length;i++){
if(this.windowDecorationConfig.windowActionNoImage[_2b[i]]!=null){
if(_2b[i]==jetspeed.id.ACTION_NAME_MENU){
_2a=true;
}else{
_29.push(_2b[i]);
}
_2b[i]=null;
}
}
}
for(var i=0;i<_2b.length;i++){
if(_2b[i]!=null){
this._createActionButtonNode(_2b[i]);
}
}
}
if(this.windowDecorationConfig.windowActionMenuOrder!=null){
if(this.portlet){
for(var _2c=0;_2c<this.windowDecorationConfig.windowActionMenuOrder.length;_2c++){
var _2d=this.windowDecorationConfig.windowActionMenuOrder[_2c];
var _2e=false;
if(this.portlet.getAction(_2d)!=null||jetspeed.prefs.windowActionDesktop[_2d]!=null){
_2e=true;
}
if(_2e){
_29.push(_2d);
}
}
}else{
for(var _2c=0;_2c<this.windowDecorationConfig.windowActionMenuOrder.length;_2c++){
var _2d=this.windowDecorationConfig.windowActionMenuOrder[_2c];
if(jetspeed.prefs.windowActionDesktop[_2d]!=null){
_29.push(_2d);
}
}
}
}
if(_29.length>0){
var _33=new Object();
var _34=new Array();
for(var i=0;i<_29.length;i++){
var _2d=_29[i];
if(_2d!=null&&_33[_2d]==null&&this.actionButtons[_2d]==null){
_34.push(_2d);
_33[_2d]=true;
}
}
if(_34.length>0){
this._createActionMenu(_34);
if(_2a){
dojo.event.kwConnect({srcObj:this.titleBar,srcFunc:"oncontextmenu",targetObj:this,targetFunc:"windowActionMenuOpen",once:true});
}
}
}
this.windowActionButtonSync();
if(this.windowDecorationConfig.windowDisableResize){
this.resizable=false;
}
if(this.windowDecorationConfig.windowDisableMove){
this.movable=false;
}
}
}
this.portletInitDragHandle();
if(this.hasShadow){
this.shadow=new dojo.lfx.shadow(this.domNode);
}
this.bgIframe=new dojo.html.BackgroundIframe(this.domNode);
if(this.taskBarId){
this.taskBarSetup();
}
this.resetLostHeightWidth();
if(dojo.hostenv.post_load_){
this._setInitialWindowState();
}else{
dojo.addOnLoad(this,"_setInitialWindowState");
}
document.body.removeChild(this.domNode);
},_createActionButtonNode:function(_35){
if(_35!=null){
var _36=document.createElement("div");
_36.className="portletWindowActionButton";
_36.style.backgroundImage="url("+jetspeed.prefs.getPortletDecorationBaseUrl(this.windowDecorationName)+"/images/desktop/"+_35+".gif)";
_36.actionName=_35;
this.actionButtons[_35]=_36;
this.titleBar.appendChild(_36);
dojo.event.connect(_36,"onclick",this,"windowActionButtonClick");
if(this.windowDecorationConfig!=null&&this.windowDecorationConfig.windowActionButtonTooltip){
var _37=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:this._getActionLabel(_35),connectId:_36,delay:"100"});
document.body.appendChild(_37.domNode);
}
}
},_getActionMenuPopupWidget:function(){
return dojo.widget.byId(this.widgetId+"_ctxmenu");
},_getActionLabel:function(_38){
if(_38==null){
return null;
}
var _39=null;
var _3a=jetspeed.prefs.desktopActionLabels;
if(_3a!=null){
_39=_3a[_38];
}
if(_39==null||_39.length==0){
if(this.portlet){
var _3b=this.portlet.getAction(_38);
if(_3b!=null){
_39=_3b.label;
}
}
}
if(_39==null||_39.length==0){
_39=dojo.string.capitalize(_38);
}
return _39;
},_createActionMenu:function(_3c){
if(_3c==null||_3c.length==0){
return;
}
var _3d=this;
var _3e=dojo.widget.createWidget("PopupMenu2",{id:this.widgetId+"_ctxmenu",contextMenuForWindow:false},null);
for(var i=0;i<_3c.length;i++){
var _40=_3c[i];
var _41=this._getActionLabel(_40);
var _42=this._createActionMenuItem(_3d,_41,_40);
this.actionMenus[_40]=_42;
_3e.addChild(_42);
}
document.body.appendChild(_3e.domNode);
},_createActionMenuItem:function(_43,_44,_45){
var _46=dojo.widget.createWidget("MenuItem2",{caption:_44});
dojo.event.connect(_46,"onClick",function(e){
_43.windowActionProcess(_45);
});
return _46;
},windowActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.windowActionProcess(evt.target.actionName,evt);
},windowActionMenuOpen:function(evt){
var _4a=null;
var _4b=null;
if(this.portlet){
_4a=this.portlet.getCurrentActionState();
_4b=this.portlet.getCurrentActionMode();
}
for(var _4c in this.actionMenus){
var _4d=this.actionMenus[_4c];
if(this._isWindowActionEnabled(_4c,_4a,_4b)){
_4d.domNode.style.display="";
}else{
_4d.domNode.style.display="none";
}
}
this._getActionMenuPopupWidget().onOpen(evt);
},windowActionProcess:function(_4e,evt){
if(_4e==null){
return;
}
if(jetspeed.prefs.windowActionDesktop[_4e]!=null){
if(_4e==jetspeed.id.ACTION_NAME_DESKTOP_TILE){
this.makeTiled();
}else{
if(_4e==jetspeed.id.ACTION_NAME_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(_4e==jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(_4e==jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false);
}
}
}
}
}else{
if(_4e==jetspeed.id.ACTION_NAME_MENU){
this.windowActionMenuOpen(evt);
}else{
if(_4e==jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
if(this.portlet){
jetspeed.changeActionForPortlet(this.portlet.getId(),jetspeed.id.ACTION_NAME_MINIMIZE,null);
}
if(!this.portlet){
this.windowActionButtonSync();
}
}else{
if(_4e==jetspeed.id.ACTION_NAME_RESTORE){
var _50=false;
if(this.portlet){
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_50=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
this.portlet.renderAction(_4e);
}else{
jetspeed.changeActionForPortlet(this.portlet.getId(),jetspeed.id.ACTION_NAME_RESTORE,null);
}
}
if(!_50){
this.restoreWindow();
}
if(!this.portlet){
this.windowActionButtonSync();
}
}else{
if(_4e==jetspeed.id.ACTION_NAME_MAXIMIZE){
if(this.portlet){
this.portlet.renderAction(_4e);
}
this.maximizeWindow();
if(!this.portlet){
this.windowActionButtonSync();
}
}else{
if(_4e==jetspeed.id.ACTION_NAME_REMOVEPORTLET){
if(this.portlet){
var _51=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(_51!=null){
_51.deletePortlet(this.portlet.entityId,this.title);
}
}
}else{
if(this.portlet){
this.portlet.renderAction(_4e);
}
}
}
}
}
}
}
},_isWindowActionEnabled:function(_52,_53,_54){
var _55=false;
if(this.minimizeWindowTemporarilyRestoreTo!=null){
if(this.portlet){
var _56=this.portlet.getAction(_52);
if(_56!=null){
if(_56.id==jetspeed.id.ACTION_NAME_REMOVEPORTLET){
if(jetspeed.page.editMode&&this.getLayoutActionsEnabled()){
_55=true;
}
}
}
}
}else{
if(_52==jetspeed.id.ACTION_NAME_MENU){
if(!this._windowActionMenuIsEmpty()){
_55=true;
}
}else{
if(jetspeed.prefs.windowActionDesktop[_52]!=null){
var _57=this.getLayoutActionsEnabled();
if(_52==jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND){
if(!this.windowHeightToFit&&_57){
_55=true;
}
}else{
if(_52==jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL){
if(this.windowHeightToFit&&_57){
_55=true;
}
}else{
if(_52==jetspeed.id.ACTION_NAME_DESKTOP_TILE&&jetspeed.prefs.windowTiling){
if(!this.windowPositionStatic&&_57){
_55=true;
}
}else{
if(_52==jetspeed.id.ACTION_NAME_DESKTOP_UNTILE){
if(this.windowPositionStatic&&_57){
_55=true;
}
}
}
}
}
}else{
if(this.portlet){
var _56=this.portlet.getAction(_52);
if(_56!=null){
if(_56.id==jetspeed.id.ACTION_NAME_REMOVEPORTLET){
if(jetspeed.page.editMode&&this.getLayoutActionsEnabled()){
_55=true;
}
}else{
if(_56.type==jetspeed.id.PORTLET_ACTION_TYPE_MODE){
if(_52!=_54){
_55=true;
}
}else{
if(_52!=_53){
_55=true;
}
}
}
}
}else{
if(_52==jetspeed.id.ACTION_NAME_MAXIMIZE){
if(_52!=this.windowState&&this.minimizeWindowTemporarilyRestoreTo==null){
_55=true;
}
}else{
if(_52==jetspeed.id.ACTION_NAME_MINIMIZE){
if(_52!=this.windowState){
_55=true;
}
}else{
if(_52==jetspeed.id.ACTION_NAME_RESTORE){
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE||this.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
_55=true;
}
}
}
}
}
}
}
}
return _55;
},_windowActionMenuIsEmpty:function(){
var _58=null;
var _59=null;
if(this.portlet){
_58=this.portlet.getCurrentActionState();
_59=this.portlet.getCurrentActionMode();
}
var _5a=true;
for(var _5b in this.actionMenus){
var _5c=this.actionMenus[_5b];
if(_5b!=jetspeed.id.ACTION_NAME_MENU&&this._isWindowActionEnabled(_5b,_58,_59)){
_5a=false;
break;
}
}
return _5a;
},windowActionButtonSync:function(){
var _5d=this.windowDecorationConfig.windowActionButtonHide;
var _5e=null;
var _5f=null;
if(this.portlet){
_5e=this.portlet.getCurrentActionState();
_5f=this.portlet.getCurrentActionMode();
}
for(var _60 in this.actionButtons){
var _61=false;
if(!_5d||this.titleLit){
_61=this._isWindowActionEnabled(_60,_5e,_5f);
}
var _62=this.actionButtons[_60];
if(_61){
_62.style.display="";
}else{
_62.style.display="none";
}
}
},portletInitDimensions:function(){
if(!this.templateDomNodeClassName){
this.templateDomNodeClassName=this.domNode.className;
}
var _63=this.templateDomNodeClassName;
if(this.windowDecorationName){
_63=this.windowDecorationName+(_63?(" "+_63):"");
}
this.domNode.className=jetspeed.id.PORTLET_STYLE_CLASS+(_63?(" "+_63):"");
if(this.containerNode){
if(!this.templateContainerNodeClassName){
this.templateContainerNodeClassName=this.containerNode.className;
}
var _64=this.templateContainerNodeClassName;
if(this.windowDecorationName){
_64=this.windowDecorationName+(_64?(" "+_64):"");
}
this.containerNode.className=jetspeed.id.PORTLET_STYLE_CLASS+(_64?(" "+_64):"");
}
this._adjustPositionToDesktopState();
},resetWindow:function(_65){
this.portlet=_65;
this.portletMixinProperties();
this.portletInitDragHandle();
this.portletInitDimensions();
},postCreate:function(_66,_67,_68){
if(this.movable){
this.drag=new jetspeed.widget.PortletWindowDragMoveSource(this);
if(this.constrainToContainer){
this.drag.constrainTo();
}
this.setTitleBarDragging();
}
this.domNode.id=this.widgetId;
this.portletInitDimensions();
if(jetspeed.debug.createWindow){
dojo.debug("createdWindow ["+(this.portlet?this.portlet.entityId:this.widgetId)+(this.portlet?(" / "+this.widgetId):"")+"]"+" width="+this.domNode.style.width+" height="+this.domNode.style.height+" left="+this.domNode.style.left+" top="+this.domNode.style.top);
}
this.portletInitialized=true;
var _69=null;
if(this.portlet){
_69=this.portlet.getCurrentActionState();
}else{
_69=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_STATE);
}
if(_69==jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
this.windowActionButtonSync();
this.needsRenderOnRestore=true;
}else{
if(_69==jetspeed.id.ACTION_NAME_MAXIMIZE){
dojo.lang.setTimeout(this,this._postCreateMaximizeWindow,1500);
}
}
},_postCreateMaximizeWindow:function(){
this.maximizeWindow();
this.windowActionButtonSync();
},loadContents:function(){
},isPortletWindowInitialized:function(){
return this.portletInitialized;
},minimizeWindowTemporarily:function(){
if(this.minimizeWindowTemporarilyRestoreTo==null){
this.minimizeWindowTemporarilyRestoreTo=this.windowState;
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
}
this.windowActionButtonSync();
}
},restoreFromMinimizeWindowTemporarily:function(){
var _6a=this.minimizeWindowTemporarilyRestoreTo;
this.minimizeWindowTemporarilyRestoreTo=null;
if(_6a){
if(_6a!=jetspeed.id.ACTION_NAME_MINIMIZE){
this.restoreWindow();
}
this.windowActionButtonSync();
}
},minimizeWindow:function(evt){
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE){
this.showAllPortletWindows();
this.restoreWindow(evt);
}
this._setLastPositionInfo();
this.containerNode.style.display="none";
this.resizeBar.style.display="none";
dojo.html.setContentBox(this.domNode,{height:dojo.html.getMarginBox(this.titleBar).height});
this.windowState=jetspeed.id.ACTION_NAME_MINIMIZE;
},showAllPortletWindows:function(){
var _6c=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_6c.length;i++){
var _6e=_6c[i];
if(_6e){
_6e.domNode.style.display="";
}
}
},hideAllPortletWindows:function(_6f){
var _70=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_70.length;i++){
var _72=_70[i];
if(_72&&_6f&&_6f.length>0){
for(var _73=0;_73<_6f.length;_73++){
if(_72.widgetId==_6f[_73]){
_72=null;
}
}
}
if(_72){
_72.domNode.style.display="none";
}
}
},maximizeWindow:function(evt){
this.hideAllPortletWindows([this.widgetId]);
if(this.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.restoreWindow(evt);
}
var _75=this.windowPositionStatic;
this._setLastPositionInfo(_75,true);
var _76=document.getElementById(jetspeed.id.DESKTOP);
if(this.windowPositionStatic){
this.domNode.style.position="absolute";
_76.appendChild(this.domNode);
}
this.setTitleBarDragging(false);
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
var _77=dojo.html.getAbsolutePosition(_76,true).y;
this.domNode.style.left="1px";
this.domNode.style.top=_77;
this.windowPositionStatic=false;
var _78=document.getElementById(jetspeed.id.PAGE);
var _79=dojo.html.getViewport();
var _7a=dojo.html.getPadding(dojo.body());
this.resizeTo(_79.width-_7a.width-2,_79.height-_7a.height-_77);
this.windowState=jetspeed.id.ACTION_NAME_MAXIMIZE;
},restoreWindow:function(evt){
var _7c=false;
if(this.domNode.style.position=="absolute"){
_7c=true;
}
var _7d=null;
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE){
this.showAllPortletWindows();
this.windowPositionStatic=(this.lastWindowPositionStatic!=null?this.lastWindowPositionStatic:false);
}
this.containerNode.style.display="";
this.resizeBar.style.display="";
var _7d=this.getLastPositionInfo();
var _7e=null;
var _7f=null;
if(_7d!=null){
_7e=_7d.width;
_7f=_7d.height;
for(var _80 in _7d){
if(_80!="columnInfo"){
this.domNode.style[_80]=_7d[_80];
}
}
}
this._adjustPositionToDesktopState();
if(this.windowPositionStatic&&_7c){
if(_7d!=null&&_7d.columnInfo!=null&&_7d.columnInfo.columnIndex!=null){
var _81=jetspeed.page.columns[_7d.columnInfo.columnIndex];
if(_7d.columnInfo.previousSibling){
dojo.dom.insertAfter(this.domNode,_7d.columnInfo.previousSibling);
}else{
if(_7d.columnInfo.nextSibling){
dojo.dom.insertBefore(this.domNode,_7d.columnInfo.nextSibling);
}else{
_81.domNode.appendChild(this.domNode);
}
}
}else{
if(jetspeed.page.columns!=null&&jetspeed.page.columns.length>0){
dojo.dom.prependChild(this.domNode,jetspeed.page.columns[0].domNode);
}
}
this.domNode.style.position="static";
}
this.resizeTo(_7e,_7f,true);
this._adjustPositionToDesktopState();
this.windowState=jetspeed.id.ACTION_NAME_RESTORE;
this.setTitleBarDragging();
},getLastPositionInfo:function(){
if(this.windowPositionStatic){
return this.lastTiledPositionInfo;
}
return this.lastUntiledPositionInfo;
},_setLastPositionInfo:function(_82,_83){
if(_83){
this.lastWindowPositionStatic=this.windowPositionStatic;
}
if(this.windowPositionStatic){
if(this.lastTiledPositionInfo==null){
this.lastTiledPositionInfo={};
}
if(_82){
var _84={};
var _85=dojo.dom.getPreviousSiblingElement(this.domNode);
if(_85){
_84.previousSibling=_85;
}else{
_85=dojo.dom.getNextSiblingElement(this.domNode);
if(_85){
_84.nextSibling=_85;
}
}
_84.columnIndex=this.getPageColumnIndex();
this.lastTiledPositionInfo.columnInfo=_84;
}
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
this.lastTiledPositionInfo.height=this.domNode.style.height;
}
this.lastTiledPositionInfo.width="";
}else{
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
var _86=dojo.html.getMarginBox(this.domNode);
this.lastUntiledPositionInfo={width:_86.width,height:_86.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
}
}
},_updateLastPositionInfoPositionOnly:function(){
if(!this.windowPositionStatic&&this.lastUntiledPositionInfo!=null){
this.lastUntiledPositionInfo.left=this.domNode.style.left;
this.lastUntiledPositionInfo.top=this.domNode.style.top;
}
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},setTitleBarDragging:function(_87){
if(typeof _87=="undefined"){
_87=this.getLayoutActionsEnabled();
}
if(_87){
if(this.normalTitleBarCursor!=null){
this.titleBar.style.cursor=this.normalTitleBarCursor;
}
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="";
}
this.drag.setDragHandle(this.titleBar);
}else{
if(this.normalTitleBarCursor==null){
this.normalTitleBarCursor=dojo.html.getComputedStyle(this.titleBar,"cursor");
}
this.titleBar.style.cursor="default";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="none";
}
this.drag.setDragHandle(null);
}
},bringToTop:function(evt){
var _89=this.domNode.style.zIndex;
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
if(this.portlet&&!this.windowPositionStatic&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&this.isPortletWindowInitialized()){
this.portlet.submitChangedWindowState();
}
},makeUntiled:function(){
this._setLastPositionInfo(true,false);
var _8a=null;
var _8b=null;
var _8c=null;
var _8d=null;
var _8e=this.lastUntiledPositionInfo;
if(_8e!=null&&_8e.width!=null&&_8e.height!=null&&_8e.left!=null&&_8e.top!=null){
_8a=_8e.width;
_8b=_8e.height;
_8c=_8e.left;
_8d=_8e.top;
}else{
var _8f=this.domNode;
var _90=dojo.html.getAbsolutePosition(_8f,true);
var _91=dojo.html.getPixelValue(_8f,"margin-top",true);
var _92=dojo.html.getPixelValue(_8f,"margin-left",true);
var _93=dojo.html.getMarginBox(this.domNode);
_8a=_93.width;
_8b=_93.height;
_8c=_90.x-_91;
_8d=_90.y-_92;
}
this.domNode.style.position="absolute";
this.domNode.style.left=_8c;
this.domNode.style.top=_8d;
this.windowPositionStatic=false;
this._adjustPositionToDesktopState();
this.resizeTo(_8a,_8b,true);
var _94=document.getElementById(jetspeed.id.DESKTOP);
_94.appendChild(this.domNode);
if(this.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitChangedWindowState();
}
},makeTiled:function(){
this.windowPositionStatic=true;
this.restoreWindow();
if(this.portlet){
this.portlet.submitChangedWindowState();
}
},makeHeightToFit:function(_95,_96){
var _97=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=true;
this._adjustPositionToDesktopState();
if(_96==null||_96!=true){
}
this.resizeTo(null,null,true);
this._adjustPositionToDesktopState();
if(!_95&&this.portlet){
this.portlet.submitChangedWindowState();
}
},makeHeightVariable:function(_98){
var _99=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=false;
this._adjustPositionToDesktopState();
var _9a=dojo.html.getMarginBox(this.domNode);
var w=_9a.width;
var h=_9a.height+3;
this.resizeTo(w,h,true);
if(dojo.render.html.ie){
dojo.lang.setTimeout(this,this._IEPostResize,10);
}
if(!_98&&this.portlet){
this.portlet.submitChangedWindowState();
}
},resizeTo:function(w,h,_9f){
if(w==null||w==0||isNaN(w)||h==null||h==0||isNaN(h)){
var _a0=dojo.html.getMarginBox(this.domNode);
if(w==null||w==0||isNaN(w)){
w=_a0.width;
}
if(h==null||h==0||isNaN(h)){
h=_a0.height;
}
}
if(w==this.lastWidthResizeTo&&h==this.lastHeightResizeTo&&!_9f){
return;
}
this.lastWidthResizeTo=w;
this.lastHeightResizeTo=h;
this.resetLostHeightWidth();
dojo.lang.forEach([this.titleBar,this.resizeBar,this.containerNode],function(_a1){
dojo.html.setMarginBox(_a1,{width:w-this.lostWidth});
},this);
if(this.windowPositionStatic){
this.domNode.style.width="";
if(this.titleBar){
this.titleBar.style.width="";
}
if(this.resizeBar){
this.resizeBar.style.width="";
}
if(this.containerNode){
if(dojo.render.html.ie){
this.containerNode.style.width="";
}else{
this.containerNode.style.width="";
}
}
}else{
dojo.html.setMarginBox(this.domNode,{width:w});
}
this.resetLostHeightWidth();
if(h<(this.lostHeight+60)){
h=this.lostHeight+60;
}
dojo.html.setMarginBox(this.domNode,{height:h});
dojo.html.setMarginBox(this.containerNode,{height:h-this.lostHeight});
this.bgIframe.onResized();
if(this.shadow){
this.shadow.size(w,h);
}
this.onResized();
},_IEPostResize:function(){
},_adjustPositionToDesktopState:function(){
if(this.windowPositionStatic){
this.domNode.style.position="static";
this.domNode.style.left="auto";
this.domNode.style.top="auto";
}else{
this.domNode.style.position="absolute";
}
if(this.windowHeightToFit){
this.domNode.style.overflowY="visible";
this.domNode.style.height="";
}else{
this.domNode.style.overflowY="hidden";
}
if(this.windowPositionStatic){
this.domNode.style.width="";
if(this.titleBar){
this.titleBar.style.width="";
}
if(this.resizeBar){
this.resizeBar.style.width="";
}
}else{
}
if(this.containerNode){
if(this.windowHeightToFit){
this.containerNode.style.overflowY="visible";
this.containerNode.style.height="";
}else{
this.containerNode.style.overflowY="auto";
}
if(dojo.render.html.ie){
this.containerNode.style.width="";
}else{
this.containerNode.style.width="";
}
}
},resetLostHeightWidth:function(){
var _a2=dojo.html.getMarginBox(this.domNode);
var _a3=dojo.html.getContentBox(this.domNode);
this.lostHeight=(_a2.height-_a3.height)+dojo.html.getMarginBox(this.titleBar).height+dojo.html.getMarginBox(this.resizeBar).height;
this.lostWidth=_a2.width-_a3.width;
},contentChanged:function(evt){
if(this.processingContentChanged==false){
this.processingContentChanged=true;
if(this.windowHeightToFit){
this.makeHeightToFit(true,true);
}
this.processingContentChanged=false;
}
},closeWindow:function(){
jetspeed.widget.PortletWindow.superclass.closeWindow.call(this);
var _a5=this.getResizeHandleWidget();
if(_a5){
_a5.destroy();
}
},dumpPostionInfo:function(){
var _a6=dojo.html.getAbsolutePosition(this.domNode,true);
var _a7=dojo.html.getMarginBox(this.domNode);
var _a8=_a7.width;
var _a9=_a7.height;
var _aa=dojo.html.getMarginBox(this.containerNode);
var _ab=_aa.width;
var _ac=_aa.height;
dojo.debug("window-position ["+this.widgetId+"] x="+_a6.x+" y="+_a6.y+" width="+_a8+" height="+_a9+" cNode-width="+_ab+" cNode-height="+_ac+" document-width="+dojo.html.getMarginBox(document["body"]).width+" document-height="+dojo.html.getMarginBox(document["body"]).height);
},getPageColumnIndex:function(){
return jetspeed.page.getColumnIndexContainingNode(this.domNode);
},getResizeHandleWidget:function(){
return dojo.widget.byId(this.widgetId+"_resize");
},onResized:function(){
jetspeed.widget.PortletWindow.superclass.onResized.call(this);
if(!this.windowIsSizing){
var _ad=this.getResizeHandleWidget();
if(_ad!=null&&_ad._isSizing){
dojo.event.connect(_ad,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
},endSizing:function(e){
dojo.event.disconnect(document.body,"onmouseup",this,"endSizing");
this.windowIsSizing=false;
if(this.portlet&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
this.portlet.submitChangedWindowState();
}
},endDragging:function(){
if(this.portlet&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
this.portlet.submitChangedWindowState();
}
},titleLight:function(){
var _af=[];
var _b0=null;
var _b1=null;
if(this.portlet){
_b0=this.portlet.getCurrentActionState();
_b1=this.portlet.getCurrentActionMode();
}
for(var _b2 in this.actionButtons){
var _b3=this._isWindowActionEnabled(_b2,_b0,_b1);
if(_b3){
var _b4=this.actionButtons[_b2];
_af.push(_b4);
}
}
for(var i=0;i<_af.length;i++){
_af[i].style.display="";
}
this.titleLit=true;
},titleDim:function(_b6){
var _b7=[];
for(var _b8 in this.actionButtons){
var _b9=this.actionButtons[_b8];
if(_b9.style.display!="none"){
_b7.push(_b9);
}
}
for(var i=0;i<_b7.length;i++){
_b7[i].style.display="none";
}
this.titleLit=false;
},titleMouseOver:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _bc=this;
this.titleMouseIn=1;
window.setTimeout(function(){
if(_bc.titleMouseIn>0){
_bc.titleLight();
_bc.titleMouseIn=0;
}
},270);
}
},titleMouseOut:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _be=this;
var _bf=this.titleMouseIn;
if(_bf>0){
_bf=0;
this.titleMouseIn=_bf;
}
if(_bf==0&&this.titleLit){
window.setTimeout(function(){
if(_be.titleMouseIn==0&&_be.titleLit){
_be.titleDim();
}
},200);
}
}
},getCurrentVolatileWindowState:function(){
if(!this.domNode){
return null;
}
var _c0={};
if(!this.windowPositionStatic){
_c0.zIndex=this.domNode.style.zIndex;
}
return _c0;
},getCurrentWindowState:function(){
if(!this.domNode){
return null;
}
var _c1=this.getCurrentVolatileWindowState();
_c1.width=this.domNode.style.width;
_c1.height=this.domNode.style.height;
_c1[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=this.windowPositionStatic;
_c1[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=this.windowHeightToFit;
if(!this.windowPositionStatic){
_c1.left=this.domNode.style.left;
_c1.top=this.domNode.style.top;
}else{
var _c2=jetspeed.page.getPortletCurrentColumnRow(this.domNode);
if(_c2!=null){
_c1.column=_c2.column;
_c1.row=_c2.row;
_c1.layout=_c2.layout;
}else{
dojo.raise("PortletWindow.getCurrentWindowState cannot not find row/column/layout of window: "+this.widgetId);
}
}
return _c1;
},getCurrentWindowStateForPersistence:function(_c3){
var _c4=null;
if(_c3){
_c4=this.getCurrentVolatileWindowState();
}else{
_c4=this.getCurrentWindowState();
}
this._purifyWindowStatePropertyAsNumber(_c4,"left");
this._purifyWindowStatePropertyAsNumber(_c4,"top");
this._purifyWindowStatePropertyAsNumber(_c4,"width");
this._purifyWindowStatePropertyAsNumber(_c4,"height");
return _c4;
},_purifyWindowStatePropertyAsNumber:function(_c5,_c6){
var _c7=_c5[_c6];
if(_c7!=null){
var _c8="";
for(var i=0;i<_c7.length;i++){
var _ca=_c7.charAt(i);
if((_ca>="0"&&_ca<="9")||_ca=="."){
_c8+=_ca.toString();
}
}
_c5[_c6]=_c8;
}
},setPortletContent:function(_cb,url){
var _cd=_cb.toString();
if(!this.getInitProperty(jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT)){
_cd="<div class=\"PContent\" >"+_cd+"</div>";
}
var _ce=this._splitAndFixPaths_scriptsonly(_cd,url);
this.setContent(_ce);
if(_ce.scripts!=null&&_ce.scripts.length!=null&&_ce.scripts.length>0){
this._executeScripts(_ce.scripts);
}
if(jetspeed.debug.setPortletContent){
dojo.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
if(this.portlet){
this.portlet.postParseAnnotateHtml(this.containerNode);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
},setPortletTitle:function(_cf){
if(_cf){
this.title=_cf;
}else{
this.title="";
}
if(this.portletInitialized&&this.titleBarText){
this.titleBarText.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _d2=true;
var _d3=[];
var _d4=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _d5=/src=(['"]?)([^"']*)\1/i;
var _d6=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _d7=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _d8=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(match=_d4.exec(s)){
if(_d2&&match[1]){
if(attr=_d5.exec(match[1])){
if(_d6.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_d3.push({path:attr[2]});
}
}
}
if(match[2]){
var sc=match[2].replace(_d7,"");
if(!sc){
continue;
}
while(tmp=_d8.exec(sc)){
requires.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
if(_d2){
_d3.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_d3,"url":url};
}});
jetspeed.widget.pwGhost=document.createElement("div");
jetspeed.widget.pwGhost.id="pwGhost";
jetspeed.widget.PortletWindowResizeHandle=function(){
dojo.widget.ResizeHandle.call(this);
this.widgetType="PortletWindowResizeHandle";
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,dojo.widget.ResizeHandle);
dojo.lang.extend(jetspeed.widget.PortletWindowResizeHandle,{changeSizing:function(e){
if(this.portletWindow.windowHeightToFit){
this.portletWindow.makeHeightVariable(true);
}
try{
if(!e.clientX||!e.clientY){
return;
}
}
catch(e){
return;
}
var dx=this.startPoint.x-e.clientX;
var dy=this.startPoint.y-e.clientY;
var _dd=this.startSize.w-dx;
var _de=this.startSize.h-dy;
if(this.portletWindow.windowPositionStatic){
_dd=this.startSize.w;
}
if(this.minSize){
if(_dd<this.minSize.w){
_dd=dojo.html.getMarginBox(this.targetWidget.domNode).width;
}
if(_de<this.minSize.h){
_de=dojo.html.getMarginBox(this.targetWidget.domNode).height;
}
}
this.targetWidget.resizeTo(_dd,_de);
e.preventDefault();
}});
jetspeed.widget.PortletWindowDragMoveSource=function(_df,_e0){
this.portletWindow=_df;
dojo.dnd.HtmlDragMoveSource.call(this,_df.domNode,_e0);
};
dojo.inherits(jetspeed.widget.PortletWindowDragMoveSource,dojo.dnd.HtmlDragMoveSource);
dojo.lang.extend(jetspeed.widget.PortletWindowDragMoveSource,{onDragStart:function(){
var _e1=new jetspeed.widget.PortletWindowDragMoveObject(this.dragObject,this.type,this.portletWindow);
if(this.constrainToContainer){
_e1.constrainTo(this.constrainingContainer);
}
return _e1;
},onDragEnd:function(){
}});
dojo.declare("jetspeed.widget.PortletWindowDragMoveObject",dojo.dnd.HtmlDragMoveObject,{qualifyTargetColumn:function(_e2){
if(_e2!=null&&!_e2.layoutActionsDisabled){
return true;
}
return false;
},onDragStart:function(e){
this.portletWindow.isDragging=true;
var _e4=this.domNode;
this.initialStyleWidth=_e4.style.width;
this.initialOffsetWidth=_e4.offsetWidth;
dojo.html.clearSelection();
this.dragClone=this.domNode;
this.scrollOffset=dojo.html.getScroll().offset;
this.dragStartPosition=dojo.html.abs(this.domNode,true);
this.dragOffset={y:this.dragStartPosition.y-e.pageY,x:this.dragStartPosition.x-e.pageX};
this.containingBlockPosition=this.domNode.offsetParent?dojo.html.abs(this.domNode.offsetParent,true):{x:0,y:0};
this.dragClone.style.position="absolute";
if(this.constrainToContainer){
this.constraints=this.getConstraints();
}
var _e5=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
_e4.style.width=this.initialOffsetWidth;
_e5.style.height=_e4.offsetHeight+"px";
_e4.parentNode.insertBefore(_e5,_e4);
document.getElementById(jetspeed.id.DESKTOP).appendChild(_e4);
var _e6=this.portletWindow.getPageColumnIndex();
this.columnDimensions=new Array(jetspeed.page.columns.length);
for(var i=0;i<jetspeed.page.columns.length;i++){
var col=jetspeed.page.columns[i];
if(!col.columnContainer&&!col.layoutHeader){
if(this.qualifyTargetColumn(col)){
var _e9=dojo.html.getAbsolutePosition(col.domNode,true);
var _ea=dojo.html.getMarginBox(col.domNode);
this.columnDimensions[i]={left:(_e9.x),right:(_e9.x+_ea.width),top:(_e9.y),bottom:(_e9.y+_ea.height)};
}
}
}
var _eb=(_e6>=0?jetspeed.page.columns[_e6]:null);
_e5.col=_eb;
}
},onDragMove:function(e){
this.updateDragOffset();
var x=this.dragOffset.x+e.pageX;
var y=this.dragOffset.y+e.pageY;
if(this.constrainToContainer){
if(x<this.constraints.minX){
x=this.constraints.minX;
}
if(y<this.constraints.minY){
y=this.constraints.minY;
}
if(x>this.constraints.maxX){
x=this.constraints.maxX;
}
if(y>this.constraints.maxY){
y=this.constraints.maxY;
}
}
this.setAbsolutePosition(x,y);
if(!this.disableY){
this.dragClone.style.top=y+"px";
}
if(!this.disableX){
this.dragClone.style.left=x+"px";
}
var _ef=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
var _f0=-1;
var _f1=this.domNode.offsetWidth/2;
var _f2=this.domNode.offsetHeight/2;
var _f3=jetspeed.page.columns.length;
for(var _f4=1;_f4<=2;_f4++){
for(var i=0;i<_f3;i++){
var _f6=this.columnDimensions[i];
if(_f6!=null){
var _f7=x+_f1;
if(_f7>=_f6.left&&_f7<=_f6.right){
var _f8=y+_f2;
if(_f4==1){
if(_f8>=_f6.top&&_f8<=_f6.bottom){
_f0=i;
break;
}
}else{
if(_f8>=(_f6.top-30)&&_f8<=(_f6.bottom+200)){
_f0=i;
break;
}
}
}
}
}
if(_f0!=-1){
break;
}
}
var col=(_f0>=0?jetspeed.page.columns[_f0]:null);
if(_ef.col!=col&&col!=null){
dojo.dom.removeNode(_ef);
_ef.col=col;
col.domNode.appendChild(_ef);
}
var _fa=null,_fb=null;
if(col!=null){
_fa=jetspeed.ui.getPortletWindowChildren(col.domNode,_ef);
_fb=_fa.portletWindowNodes;
}
if(_fb!=null){
var _fc=_fa.matchIndex;
if(_fc>0){
var _fd=dojo.html.getAbsolutePosition(_fb[_fc-1],true).y;
if(y<=_fd){
dojo.dom.removeNode(_ef);
dojo.dom.insertBefore(_ef,_fb[_fc-1],true);
}else{
}
}
if(_fc!=(_fb.length-1)){
var _fe=dojo.html.getAbsolutePosition(_fb[_fc+1],true).y;
if(y>=_fe){
if(_fc+2<_fb.length){
dojo.dom.insertBefore(_ef,_fb[_fc+2],true);
}else{
col.domNode.appendChild(_ef);
}
}else{
}
}
}
}
},onDragEnd:function(e){
if(this.initialStyleWidth!=this.domNode.style.width){
this.domNode.style.width=this.initialStyleWidth;
}
jetspeed.widget.PortletWindowDragMoveObject.superclass.onDragEnd.call(this,e);
var _100=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
if(_100&&_100.col){
this.portletWindow.column=0;
dojo.dom.insertBefore(this.domNode,_100,true);
}
if(_100){
dojo.dom.removeNode(_100);
}
this.domNode.style.position="static";
}else{
if(_100){
dojo.dom.removeNode(_100);
}
}
this.portletWindow.isDragging=false;
if(this.portletWindow.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.portletWindow._updateLastPositionInfoPositionOnly();
}
this.portletWindow.endDragging();
}},function(node,type,_103){
this.portletWindow=_103;
this.windowPositionStatic=((this.portletWindow!=null)?this.portletWindow.windowPositionStatic:false);
});

