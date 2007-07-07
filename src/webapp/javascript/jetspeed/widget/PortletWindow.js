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
this.tooltips=[];
this.subWidgetStartIndex=-1;
this.subWidgetEndIndex=-1;
};
dojo.inherits(jetspeed.widget.PortletWindow,dojo.widget.FloatingPane);
dojo.lang.extend(jetspeed.widget.PortletWindow,{title:"Unknown Portlet",contentWrapper:"layout",displayCloseAction:true,displayMinimizeAction:true,displayMaximizeAction:true,displayRestoreAction:true,taskBarId:null,nextIndex:1,windowDecorationName:null,windowDecorationConfig:null,windowPositionStatic:false,windowHeightToFit:false,titleMouseIn:0,titleLit:false,portlet:null,jsAltInitParams:null,templateDomNodeClassName:null,templateContainerNodeClassName:null,processingContentChanged:false,lastUntiledPositionInfo:null,lastTiledPositionInfo:null,minimizeWindowTemporarilyRestoreTo:null,executeScripts:false,scriptSeparation:false,adjustPaths:false,staticDefineAsAltInitParameters:function(_1,_2){
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
this.templatePath=_10.templatePath;
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
if(_23&&this.resizeBar){
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
}else{
this.resizable=false;
}
},fillInTemplate:function(_25,_26){
var _27=this.getFragNodeRef(_26);
dojo.html.copyStyle(this.domNode,_27);
document.body.appendChild(this.domNode);
if(!this.isShowing()){
this.windowState=jetspeed.id.ACTION_NAME_MINIMIZE;
}
if(this.titleBarIcon){
if(this.iconSrc==null||this.iconSrc==""){
dojo.dom.removeNode(this.titleBarIcon);
}else{
this.titleBarIcon.src=this.iconSrc.toString();
}
}
if(this.titleBarDisplay&&this.titleBar){
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
this.tooltips.push(_37);
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
if(jetspeed.widget.pwGhost==null&&jetspeed.page!=null){
var _64=document.createElement("div");
_64.id="pwGhost";
var _65=jetspeed.page.getPortletDecorationDefault();
if(!_65){
_65=this.windowDecorationName;
}
_64.className=jetspeed.id.PORTLET_STYLE_CLASS+(_65?(" "+_65):"")+(this.templateDomNodeClassName?(" "+this.templateDomNodeClassName):"");
_64.style.position="static";
_64.style.width="";
_64.style.left="auto";
_64.style.top="auto";
jetspeed.widget.pwGhost=_64;
}
if(this.containerNode){
if(!this.templateContainerNodeClassName){
this.templateContainerNodeClassName=this.containerNode.className;
}
var _66=this.templateContainerNodeClassName;
if(this.windowDecorationName){
_66=this.windowDecorationName+(_66?(" "+_66):"");
}
this.containerNode.className=jetspeed.id.PORTLET_STYLE_CLASS+(_66?(" "+_66):"");
}
this._adjustPositionToDesktopState();
},resetWindow:function(_67){
this.portlet=_67;
this.portletMixinProperties();
this.portletInitDragHandle();
this.portletInitDimensions();
},postCreate:function(_68,_69,_6a){
if(this.movable&&this.titleBar){
this.drag=new dojo.dnd.Moveable(this,{handle:this.titleBar});
}
this.domNode.id=this.widgetId;
this.portletInitDimensions();
if(jetspeed.debug.createWindow){
dojo.debug("createdWindow ["+(this.portlet?this.portlet.entityId:this.widgetId)+(this.portlet?(" / "+this.widgetId):"")+"]"+" width="+this.domNode.style.width+" height="+this.domNode.style.height+" left="+this.domNode.style.left+" top="+this.domNode.style.top);
}
this.portletInitialized=true;
var _6b=null;
if(this.portlet){
_6b=this.portlet.getCurrentActionState();
}else{
_6b=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_STATE);
}
if(_6b==jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
this.windowActionButtonSync();
this.needsRenderOnRestore=true;
}else{
if(_6b==jetspeed.id.ACTION_NAME_MAXIMIZE){
dojo.lang.setTimeout(this,this._postCreateMaximizeWindow,1500);
return;
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
var _6c=this.minimizeWindowTemporarilyRestoreTo;
this.minimizeWindowTemporarilyRestoreTo=null;
if(_6c){
if(_6c!=jetspeed.id.ACTION_NAME_MINIMIZE){
this.restoreWindow();
}
this.windowActionButtonSync();
}
},minimizeWindow:function(evt){
if(!this.titleBar){
return;
}
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE){
this.showAllPortletWindows();
this.restoreWindow(evt);
}
this._setLastPositionInfo();
this.containerNode.style.display="none";
if(this.resizeBar){
this.resizeBar.style.display="none";
}
dojo.html.setContentBox(this.domNode,{height:dojo.html.getMarginBox(this.titleBar).height});
this.windowState=jetspeed.id.ACTION_NAME_MINIMIZE;
},showAllPortletWindows:function(){
var _6e=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_6e.length;i++){
var _70=_6e[i];
if(_70){
_70.domNode.style.display="";
}
}
},hideAllPortletWindows:function(_71){
var _72=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_72.length;i++){
var _74=_72[i];
if(_74&&_71&&_71.length>0){
for(var _75=0;_75<_71.length;_75++){
if(_74.widgetId==_71[_75]){
_74=null;
}
}
}
if(_74){
_74.domNode.style.display="none";
}
}
},maximizeWindow:function(evt){
this.hideAllPortletWindows([this.widgetId]);
if(this.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.restoreWindow(evt);
}
var _77=this.windowPositionStatic;
this._setLastPositionInfo(_77,true);
var _78=document.getElementById(jetspeed.id.DESKTOP);
if(this.windowPositionStatic){
this.domNode.style.position="absolute";
_78.appendChild(this.domNode);
}
this.setTitleBarDragging(false);
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
var _79=dojo.html.getAbsolutePosition(_78,true).y;
this.domNode.style.left="1px";
this.domNode.style.top=_79;
this.windowPositionStatic=false;
var _7a=document.getElementById(jetspeed.id.PAGE);
var _7b=dojo.html.getViewport();
var _7c=dojo.html.getPadding(dojo.body());
this.resizeTo(_7b.width-_7c.width-2,_7b.height-_7c.height-_79);
this.windowState=jetspeed.id.ACTION_NAME_MAXIMIZE;
},restoreWindow:function(evt){
var _7e=false;
if(this.domNode.style.position=="absolute"){
_7e=true;
}
var _7f=null;
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE){
this.showAllPortletWindows();
this.windowPositionStatic=(this.lastWindowPositionStatic!=null?this.lastWindowPositionStatic:false);
}
this.containerNode.style.display="";
if(this.resizeBar){
this.resizeBar.style.display="";
}
var _7f=this.getLastPositionInfo();
var _80=null;
var _81=null;
if(_7f!=null){
_80=_7f.width;
_81=_7f.height;
for(var _82 in _7f){
if(_82!="columnInfo"){
this.domNode.style[_82]=_7f[_82];
}
}
}
this._adjustPositionToDesktopState();
if(this.windowPositionStatic&&_7e){
if(_7f!=null&&_7f.columnInfo!=null&&_7f.columnInfo.columnIndex!=null){
var _83=jetspeed.page.columns[_7f.columnInfo.columnIndex];
if(_7f.columnInfo.previousSibling){
dojo.dom.insertAfter(this.domNode,_7f.columnInfo.previousSibling);
}else{
if(_7f.columnInfo.nextSibling){
dojo.dom.insertBefore(this.domNode,_7f.columnInfo.nextSibling);
}else{
_83.domNode.appendChild(this.domNode);
}
}
}else{
if(jetspeed.page.columns!=null&&jetspeed.page.columns.length>0){
dojo.dom.prependChild(this.domNode,jetspeed.page.columns[0].domNode);
}
}
this.domNode.style.position="static";
}
this.resizeTo(_80,_81,true);
this._adjustPositionToDesktopState();
this.windowState=jetspeed.id.ACTION_NAME_RESTORE;
this.setTitleBarDragging();
},getLastPositionInfo:function(){
if(this.windowPositionStatic){
return this.lastTiledPositionInfo;
}
return this.lastUntiledPositionInfo;
},_setLastPositionInfo:function(_84,_85){
if(_85){
this.lastWindowPositionStatic=this.windowPositionStatic;
}
if(this.windowPositionStatic){
if(this.lastTiledPositionInfo==null){
this.lastTiledPositionInfo={};
}
if(_84){
var _86={};
var _87=dojo.dom.getPreviousSiblingElement(this.domNode);
if(_87){
_86.previousSibling=_87;
}else{
_87=dojo.dom.getNextSiblingElement(this.domNode);
if(_87){
_86.nextSibling=_87;
}
}
_86.columnIndex=this.getPageColumnIndex();
this.lastTiledPositionInfo.columnInfo=_86;
}
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
this.lastTiledPositionInfo.height=this.domNode.style.height;
}
this.lastTiledPositionInfo.width="";
}else{
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
var _88=dojo.html.getMarginBox(this.domNode);
this.lastUntiledPositionInfo={width:_88.width,height:_88.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
}
}
},_updateLastPositionInfoPositionOnly:function(){
if(!this.windowPositionStatic&&this.lastUntiledPositionInfo!=null){
this.lastUntiledPositionInfo.left=this.domNode.style.left;
this.lastUntiledPositionInfo.top=this.domNode.style.top;
}
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},setTitleBarDragging:function(_89){
if(!this.titleBar){
return;
}
if(typeof _89=="undefined"){
_89=this.getLayoutActionsEnabled();
}
if(_89){
if(this.normalTitleBarCursor!=null){
this.titleBar.style.cursor=this.normalTitleBarCursor;
}
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="";
}
if(this.drag){
this.drag.enable();
}
}else{
if(this.normalTitleBarCursor==null){
this.normalTitleBarCursor=dojo.html.getComputedStyle(this.titleBar,"cursor");
}
this.titleBar.style.cursor="default";
if(this.resizeHandle){
this.resizeHandle.domNode.style.display="none";
}
if(this.drag){
this.drag.disable();
}
}
},bringToTop:function(evt){
var _8b=this.domNode.style.zIndex;
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
if(this.portlet&&!this.windowPositionStatic&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&this.isPortletWindowInitialized()){
this.portlet.submitChangedWindowState();
}
},makeUntiled:function(){
this._setLastPositionInfo(true,false);
var _8c=null;
var _8d=null;
var _8e=null;
var _8f=null;
var _90=this.lastUntiledPositionInfo;
if(_90!=null&&_90.width!=null&&_90.height!=null&&_90.left!=null&&_90.top!=null){
_8c=_90.width;
_8d=_90.height;
_8e=_90.left;
_8f=_90.top;
}else{
var _91=this.domNode;
var _92=dojo.html.getAbsolutePosition(_91,true);
var _93=dojo.html.getPixelValue(_91,"margin-top",true);
var _94=dojo.html.getPixelValue(_91,"margin-left",true);
var _95=dojo.html.getMarginBox(this.domNode);
_8c=_95.width;
_8d=_95.height;
_8e=_92.x-_93;
_8f=_92.y-_94;
}
this.domNode.style.position="absolute";
this.domNode.style.left=_8e;
this.domNode.style.top=_8f;
this.windowPositionStatic=false;
this._adjustPositionToDesktopState();
this.resizeTo(_8c,_8d,true);
var _96=document.getElementById(jetspeed.id.DESKTOP);
_96.appendChild(this.domNode);
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
},makeHeightToFit:function(_97,_98){
var _99=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=true;
this._adjustPositionToDesktopState();
if(_98==null||_98!=true){
}
this.resizeTo(null,null,true);
this._adjustPositionToDesktopState();
if(!_97&&this.portlet){
this.portlet.submitChangedWindowState();
}
},makeHeightVariable:function(_9a){
var _9b=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=false;
this._adjustPositionToDesktopState();
var _9c=dojo.html.getMarginBox(this.domNode);
var w=_9c.width;
var h=_9c.height+3;
this.resizeTo(w,h,true);
if(!_9a&&this.portlet){
this.portlet.submitChangedWindowState();
}
},resizeTo:function(w,h,_a1){
if(w==null||w==0||isNaN(w)||h==null||h==0||isNaN(h)){
var _a2=dojo.html.getMarginBox(this.domNode);
if(w==null||w==0||isNaN(w)){
w=_a2.width;
}
if(h==null||h==0||isNaN(h)){
h=_a2.height;
}
}
if(w==this.lastWidthResizeTo&&h==this.lastHeightResizeTo&&!_a1){
return;
}
this.lastWidthResizeTo=w;
this.lastHeightResizeTo=h;
this.resetLostHeightWidth();
dojo.lang.forEach([this.titleBar,this.resizeBar,this.containerNode],function(_a3){
if(_a3!=null){
dojo.html.setMarginBox(_a3,{width:w-this.lostWidth});
}
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
if(dojo.render.html.ie60){
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
this.onResized();
},_IEPostResize:function(){
this.containerNode.style.width="99%";
this.containerNode.style.width="";
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
var _a4=dojo.html.getMarginBox(this.domNode);
var _a5=dojo.html.getContentBox(this.domNode);
this.lostHeight=(_a4.height-_a5.height)+(this.titleBar?dojo.html.getMarginBox(this.titleBar).height:0)+(this.resizeBar?dojo.html.getMarginBox(this.resizeBar).height:0);
this.lostWidth=_a4.width-_a5.width;
},contentChanged:function(evt){
if(this.processingContentChanged==false){
this.processingContentChanged=true;
if(this.windowHeightToFit){
this.makeHeightToFit(true,true);
}
this.processingContentChanged=false;
}
},closeWindow:function(){
var _a7=this._getActionMenuPopupWidget();
if(_a7!=null){
_a7.destroy();
}
if(this.tooltips&&this.tooltips.length>0){
for(var i=(this.tooltips.length-1);i>=0;i--){
this.tooltips[i].destroy();
this.tooltips[i]=null;
}
this.tooltips=[];
}
if(this.drag){
this.drag.destroy();
}
if(this.subWidgetEndIndex>this.subWidgetStartIndex){
for(var i=this.subWidgetEndIndex-1;i>=this.subWidgetStartIndex;i--){
try{
if(dojo.widget.manager.widgets.length>i){
var _a9=dojo.widget.manager.widgets[i];
if(_a9!=null){
var swT=_a9.widgetType;
var swI=_a9.widgetId;
_a9.destroy();
}
}
}
catch(e){
}
}
}
jetspeed.widget.PortletWindow.superclass.closeWindow.call(this);
},dumpPostionInfo:function(){
var _ac=dojo.html.getAbsolutePosition(this.domNode,true);
var _ad=dojo.html.getMarginBox(this.domNode);
var _ae=_ad.width;
var _af=_ad.height;
var _b0=dojo.html.getMarginBox(this.containerNode);
var _b1=_b0.width;
var _b2=_b0.height;
dojo.debug("window-position ["+this.widgetId+"] x="+_ac.x+" y="+_ac.y+" width="+_ae+" height="+_af+" cNode-width="+_b1+" cNode-height="+_b2+" document-width="+dojo.html.getMarginBox(document["body"]).width+" document-height="+dojo.html.getMarginBox(document["body"]).height);
},getPageColumnIndex:function(){
return jetspeed.page.getColumnIndexContainingNode(this.domNode);
},getResizeHandleWidget:function(){
return dojo.widget.byId(this.widgetId+"_resize");
},onResized:function(){
jetspeed.widget.PortletWindow.superclass.onResized.call(this);
if(!this.windowIsSizing){
var _b3=this.getResizeHandleWidget();
if(_b3!=null&&_b3._isSizing){
dojo.event.connect(_b3,"_endSizing",this,"endSizing");
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
if(dojo.render.html.ie60){
dojo.lang.setTimeout(this,this._IEPostResize,10);
}
},titleLight:function(){
var _b5=[];
var _b6=null;
var _b7=null;
if(this.portlet){
_b6=this.portlet.getCurrentActionState();
_b7=this.portlet.getCurrentActionMode();
}
for(var _b8 in this.actionButtons){
var _b9=this._isWindowActionEnabled(_b8,_b6,_b7);
if(_b9){
var _ba=this.actionButtons[_b8];
_b5.push(_ba);
}
}
for(var i=0;i<_b5.length;i++){
_b5[i].style.display="";
}
this.titleLit=true;
},titleDim:function(_bc){
var _bd=[];
for(var _be in this.actionButtons){
var _bf=this.actionButtons[_be];
if(_bf.style.display!="none"){
_bd.push(_bf);
}
}
for(var i=0;i<_bd.length;i++){
_bd[i].style.display="none";
}
this.titleLit=false;
},titleMouseOver:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _c2=this;
this.titleMouseIn=1;
window.setTimeout(function(){
if(_c2.titleMouseIn>0){
_c2.titleLight();
_c2.titleMouseIn=0;
}
},270);
}
},titleMouseOut:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _c4=this;
var _c5=this.titleMouseIn;
if(_c5>0){
_c5=0;
this.titleMouseIn=_c5;
}
if(_c5==0&&this.titleLit){
window.setTimeout(function(){
if(_c4.titleMouseIn==0&&_c4.titleLit){
_c4.titleDim();
}
},200);
}
}
},getCurrentVolatileWindowState:function(){
if(!this.domNode){
return null;
}
var _c6={};
if(!this.windowPositionStatic){
_c6.zIndex=this.domNode.style.zIndex;
}
return _c6;
},getCurrentWindowState:function(){
if(!this.domNode){
return null;
}
var _c7=this.getCurrentVolatileWindowState();
_c7.width=this.domNode.style.width;
_c7.height=this.domNode.style.height;
_c7[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=this.windowPositionStatic;
_c7[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=this.windowHeightToFit;
if(!this.windowPositionStatic){
_c7.left=this.domNode.style.left;
_c7.top=this.domNode.style.top;
}else{
var _c8=jetspeed.page.getPortletCurrentColumnRow(this.domNode);
if(_c8!=null){
_c7.column=_c8.column;
_c7.row=_c8.row;
_c7.layout=_c8.layout;
}else{
dojo.raise("PortletWindow.getCurrentWindowState cannot not find row/column/layout of window: "+this.widgetId);
}
}
return _c7;
},getCurrentWindowStateForPersistence:function(_c9){
var _ca=null;
if(_c9){
_ca=this.getCurrentVolatileWindowState();
}else{
_ca=this.getCurrentWindowState();
}
this._purifyWindowStatePropertyAsNumber(_ca,"left");
this._purifyWindowStatePropertyAsNumber(_ca,"top");
this._purifyWindowStatePropertyAsNumber(_ca,"width");
this._purifyWindowStatePropertyAsNumber(_ca,"height");
return _ca;
},_purifyWindowStatePropertyAsNumber:function(_cb,_cc){
var _cd=_cb[_cc];
if(_cd!=null){
var _ce="";
for(var i=0;i<_cd.length;i++){
var _d0=_cd.charAt(i);
if((_d0>="0"&&_d0<="9")||_d0=="."){
_ce+=_d0.toString();
}
}
_cb[_cc]=_ce;
}
},setPortletContent:function(_d1,url){
var _d3=_d1.toString();
if(!this.getInitProperty(jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT)){
_d3="<div class=\"PContent\" >"+_d3+"</div>";
}
var _d4=this._splitAndFixPaths_scriptsonly(_d3,url);
this.subWidgetStartIndex=dojo.widget.manager.widgets.length;
this.setContent(_d4);
if(_d4.scripts!=null&&_d4.scripts.length!=null&&_d4.scripts.length>0){
this._executeScripts(_d4.scripts);
this.onLoad();
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
this.subWidgetEndIndex=dojo.widget.manager.widgets.length;
},setPortletTitle:function(_d5){
if(_d5){
this.title=_d5;
}else{
this.title="";
}
if(this.portletInitialized&&this.titleBarText){
this.titleBarText.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _d8=true;
var _d9=[];
var _da=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _db=/src=(['"]?)([^"']*)\1/i;
var _dc=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _dd=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _de=/dojo\.(addOn(?:Un)?[lL]oad)/g;
var _df=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(match=_da.exec(s)){
if(_d8&&match[1]){
if(attr=_db.exec(match[1])){
if(_dc.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_d9.push({path:attr[2]});
}
}
}
if(match[2]){
var sc=match[2].replace(_dd,"");
if(!sc){
continue;
}
while(tmp=_df.exec(sc)){
requires.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
sc=sc.replace(_de,"dojo.widget.byId('"+this.widgetId+"').$1");
if(_d8){
_d9.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_d9,"url":url};
}});
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
var _e4=this.startSize.w-dx;
var _e5=this.startSize.h-dy;
if(this.portletWindow.windowPositionStatic){
_e4=this.startSize.w;
}
if(this.minSize){
if(_e4<this.minSize.w){
_e4=dojo.html.getMarginBox(this.targetWidget.domNode).width;
}
if(_e5<this.minSize.h){
_e5=dojo.html.getMarginBox(this.targetWidget.domNode).height;
}
}
this.targetWidget.resizeTo(_e4,_e5);
e.preventDefault();
}});
dojo.dnd.V_TRIGGER_AUTOSCROLL=32;
dojo.dnd.H_TRIGGER_AUTOSCROLL=32;
dojo.dnd.V_AUTOSCROLL_VALUE=16;
dojo.dnd.H_AUTOSCROLL_VALUE=16;
dojo.dnd.getViewport=function(){
var d=dojo.doc(),dd=d.documentElement,w=window,b=dojo.body();
if(dojo.render.html.mozilla){
return {w:dd.clientWidth,h:w.innerHeight};
}else{
if(!dojo.render.html.opera&&w.innerWidth){
return {w:w.innerWidth,h:w.innerHeight};
}else{
if(!dojo.render.html.opera&&dd&&dd.clientWidth){
return {w:dd.clientWidth,h:dd.clientHeight};
}else{
if(b.clientWidth){
return {w:b.clientWidth,h:b.clientHeight};
}
}
}
}
return null;
};
dojo.dnd.autoScroll=function(e){
var v=dojo.dnd.getViewport(),dx=0,dy=0;
if(e.clientY<dojo.dnd.V_TRIGGER_AUTOSCROLL){
dy=-dojo.dnd.V_AUTOSCROLL_VALUE;
}else{
if(e.clientY>v.h-dojo.dnd.V_TRIGGER_AUTOSCROLL){
dy=dojo.dnd.V_AUTOSCROLL_VALUE;
}
}
window.scrollBy(dx,dy);
};
dojo.dnd.Mover=function(_ee,_ef,_f0,_f1,e){
this.moveInitiated=false;
this.moveableObj=_f1;
this.windowOrLayoutWidget=_ee;
this.node=_ef;
this.windowPositionStatic=_ee.windowPositionStatic;
this.disqualifiedColumnIndexes=null;
if(_f0!=null){
this.disqualifiedColumnIndexes=_f0.getDescendantColumns();
}
this.marginBox={l:e.pageX,t:e.pageY};
var d=this.node.ownerDocument;
var _f4=[d,"onmousemove",this,"onFirstMove"];
dojo.event.connect.apply(dojo.event,_f4);
this.events=[[d,"onmousemove",this,"onMouseMove"],[d,"onmouseup",this,"destroy"],[d,"ondragstart",dojo.event.browser,"stopEvent"],[d,"onselectstart",dojo.event.browser,"stopEvent"]];
for(var i=0;i<this.events.length;i++){
dojo.event.connect.apply(dojo.event,this.events[i]);
}
this.events.push(_f4);
};
dojo.extend(dojo.dnd.Mover,{onMouseMove:function(e){
dojo.dnd.autoScroll(e);
var m=this.marginBox;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
dojo.marginBox(this.node,{l:x,t:y});
var _fa=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
var _fb=-1;
var _fc=this.widthHalf;
var _fd=this.heightHalf;
var _fe=_fd+(_fd*0.2);
var _ff=jetspeed.page.columns.length;
var _100=[];
var _101=x+_fc;
var _102=y+_fd;
for(var i=0;i<_ff;i++){
var _104=this.columnDimensions[i];
if(_104!=null){
if(_101>=_104.left&&_101<=_104.right){
if(_102>=(_104.top-30)){
_100.push(i);
_100.push(Math.abs(_102-(_104.top+((_104.bottom-_104.top)/2))));
}
}
}
}
var _105=_100.length;
if(_105>0){
var _106=-1;
var _107=0;
var i=1;
while(i<_105){
if(_106==-1||_107>_100[i]){
_106=_100[i-1];
_107=_100[i];
}
i=i+2;
}
_fb=_106;
}
var col=(_fb>=0?jetspeed.page.columns[_fb]:null);
if(_fa.col!=col&&col!=null){
dojo.dom.removeNode(_fa);
_fa.col=col;
col.domNode.appendChild(_fa);
}
var _109=null,_10a=null;
if(col!=null){
_109=jetspeed.ui.getPortletWindowChildren(col.domNode,_fa);
_10a=_109.portletWindowNodes;
}
if(_10a!=null&&_10a.length>1){
var _10b=_109.matchIndex;
var _10c=-1;
var _10d=-1;
if(_10b>0){
var _10c=dojo.html.getAbsolutePosition(_10a[_10b-1],true).y;
if((y-25)<=_10c){
dojo.dom.removeNode(_fa);
dojo.dom.insertBefore(_fa,_10a[_10b-1],true);
}
}
if(_10b!=(_10a.length-1)){
var _10d=dojo.html.getAbsolutePosition(_10a[_10b+1],true).y;
if((y+10)>=_10d){
if(_10b+2<_10a.length){
dojo.dom.insertBefore(_fa,_10a[_10b+2],true);
}else{
col.domNode.appendChild(_fa);
}
}
}
}
}
},onFirstMove:function(){
var mP=dojo.marginBox(this.node);
this.marginBoxPrev=mP;
this.staticWidth=null;
var _10f=jetspeed.widget.pwGhost;
var m=null;
if(this.windowPositionStatic){
this.staticWidth=this.node.style.width;
this.node.style.position="absolute";
m=dojo.marginBox(this.node);
var _111=this.node.parentNode;
_111.insertBefore(_10f,this.node);
dojo.setMarginBox(_10f,null,null,null,mP.h,null);
document.getElementById(jetspeed.id.DESKTOP).appendChild(this.node);
var _112=jetspeed.ui.getPortletWindowChildren(_111,_10f);
this.prevColumnNode=_111;
this.prevIndexInCol=_112.matchIndex;
}else{
m=dojo.marginBox(this.node);
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
dojo.event.disconnect.apply(dojo.event,this.events.pop());
if(this.windowPositionStatic){
dojo.setMarginBox(this.node,m.l,m.t,mP.w,null);
this.widthHalf=mP.w/2;
this.heightHalf=mP.h/2;
var _113=this.windowOrLayoutWidget.getPageColumnIndex();
this.columnDimensions=new Array(jetspeed.page.columns.length);
for(var i=0;i<jetspeed.page.columns.length;i++){
var col=jetspeed.page.columns[i];
if(!col.columnContainer&&!col.layoutHeader){
if(this.qualifyTargetColumn(col)){
var _116=dojo.html.getAbsolutePosition(col.domNode,true);
var _117=dojo.html.getMarginBox(col.domNode);
this.columnDimensions[i]={left:(_116.x),right:(_116.x+_117.width),top:(_116.y),bottom:(_116.y+_117.height)};
}
}
}
var _118=(_113>=0?jetspeed.page.columns[_113]:null);
_10f.col=_118;
}
},qualifyTargetColumn:function(_119){
if(_119!=null&&!_119.layoutActionsDisabled){
if(this.disqualifiedColumnIndexes!=null&&this.disqualifiedColumnIndexes[_119.getPageColumnIndex()]!=null){
dojo.debug("disqualified: "+_119.toString());
return false;
}
return true;
}
return false;
},destroy:function(){
var _11a=this.windowOrLayoutWidget;
if(this.moveInitiated){
try{
var _11b=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
var n=this.node;
if(_11b&&_11b.col){
this.windowOrLayoutWidget.column=0;
dojo.dom.insertBefore(n,_11b,true);
}else{
dojo.dom.insertAtIndex(n,this.prevColumnNode,this.prevIndexInCol);
}
if(_11b){
dojo.dom.removeNode(_11b);
}
n.style.position="static";
n.style.width=this.staticWidth;
n.style.left="auto";
n.style.top="auto";
}
if(this.windowOrLayoutWidget.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.windowOrLayoutWidget._updateLastPositionInfoPositionOnly();
}
this.windowOrLayoutWidget.endDragging();
}
catch(e){
dojo.debug("Mover "+(_11a==null?"<unknown>":_11a.widgetId)+" destroy reset-window error: "+e.toString());
}
}
try{
if(this.events&&this.events.length){
for(var i=0;i<this.events.length;i++){
dojo.event.disconnect.apply(dojo.event,this.events[i]);
}
}
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(e){
dojo.debug("Mover "+(_11a==null?"<unknown>":_11a.widgetId)+" destroy clean-up error: "+e.toString());
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
}});
dojo.dnd.Moveable=function(_11e,opt){
this.enabled=true;
this.mover=null;
this.windowOrLayoutWidget=_11e;
this.handle=opt.handle;
this.events=[[this.handle,"onmousedown",this,"onMouseDown"],[this.handle,"ondragstart",dojo.event.browser,"stopEvent"],[this.handle,"onselectstart",dojo.event.browser,"stopEvent"]];
for(var i=0;i<this.events.length;i++){
dojo.event.connect.apply(dojo.event,this.events[i]);
}
};
dojo.extend(dojo.dnd.Moveable,{onMouseDown:function(e){
if(this.mover!=null){
this.mover.destroy();
}else{
if(this.enabled){
var _122=null;
var _123=this.windowOrLayoutWidget;
var _124=null;
this.beforeDragColumnRowInfo=null;
if(!_123.isLayoutPane){
_122=_123.domNode;
}else{
_124=_123.containingColumn;
if(_124!=null){
_122=_124.domNode;
if(_122!=null){
this.beforeDragColumnRowInfo=jetspeed.page.getPortletCurrentColumnRow(_122);
}
}
}
if(_122!=null){
this.node=_122;
this.mover=new dojo.dnd.Mover(_123,_122,_124,this,e);
}
}
}
dojo.event.browser.stopEvent(e);
},destroy:function(){
if(this.events&&this.events.length){
for(var i=0;i<this.events.length;i++){
dojo.event.disconnect.apply(dojo.event,this.events[i]);
}
}
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColumnRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.marginBox=function(node,box){
var n=dojo.byId(node),s=dojo.gcs(n),b=box;
return !b?dojo.getMarginBox(n,s):dojo.setMarginBox(n,b.l,b.t,b.w,b.h,s);
};
dojo.getMarginBox=function(node,_12c){
var s=_12c||dojo.gcs(node),me=dojo._getMarginExtents(node,s);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(dojo.render.html.mozilla){
var sl=parseFloat(s.left),st=parseFloat(s.top);
if(!isNaN(sl)&&!isNaN(st)){
l=sl,t=st;
}else{
var p=node.parentNode;
if(p){
var pcs=dojo.gcs(p);
if(pcs.overflow!="visible"){
var be=dojo._getBorderExtents(p,pcs);
l+=be.l,t+=be.t;
}
}
}
}else{
if(dojo.render.html.opera){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.setMarginBox=function(node,_137,_138,_139,_13a,_13b){
var s=_13b||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s);
if(_139!=null&&_139>=0){
_139=Math.max(_139-pb.w-mb.w,0);
}
if(_13a!=null&&_13a>=0){
_13a=Math.max(_13a-pb.h-mb.h,0);
}
dojo._setBox(node,_137,_138,_139,_13a);
};
dojo._setBox=function(node,l,t,w,h,u){
u=u||"px";
with(node.style){
if(l!=null&&!isNaN(l)){
left=l+u;
}
if(t!=null&&!isNaN(t)){
top=t+u;
}
if(w!=null&&w>=0){
width=w+u;
}
if(h!=null&&h>=0){
height=h+u;
}
}
};
dojo._usesBorderBox=function(node){
var n=node.tagName;
return false;
};
dojo._getPadExtents=function(n,_149){
var s=_149||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_14f){
var s=_14f||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_154){
var s=_154||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(dojo.render.html.safari&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_15c){
var ne="none",px=dojo._toPixelValue,s=_15c||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
return {l:bl,t:bt,w:bl+(s.borderRightStyle!=ne?px(n,s.borderRightWidth):0),h:bt+(s.borderBottomStyle!=ne?px(n,s.borderBottomWidth):0)};
};
if(!dojo.render.html.ie){
var dv=document.defaultView;
dojo.getComputedStyle=((dojo.render.html.safari)?function(node){
var s=dv.getComputedStyle(node,null);
if(!s&&node.style){
node.style.display="";
s=dv.getComputedStyle(node,null);
}
return s||{};
}:function(node){
return dv.getComputedStyle(node,null);
});
dojo._toPixelValue=function(_165,_166){
return (parseFloat(_166)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_168,_169){
if(!_169){
return 0;
}
if(_169.slice&&(_169.slice(-2)=="px")){
return parseFloat(_169);
}
with(_168){
var _16a=style.left;
var _16b=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_169;
_169=style.pixelLeft;
}
catch(e){
_169=0;
}
style.left=_16a;
runtimeStyle.left=_16b;
}
return _169;
};
}
dojo.gcs=dojo.getComputedStyle;

