jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
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
var _51=dojo.widget.byId(jetspeed.id.PG_ED_WID);
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
if(dojo.render.html.ie60&&jetspeed.widget.ie6ZappedContentHelper==null){
var _66=document.createElement("span");
_66.id="ie6ZappedContentHelper";
jetspeed.widget.ie6ZappedContentHelper=_66;
}
if(this.containerNode){
if(!this.templateContainerNodeClassName){
this.templateContainerNodeClassName=this.containerNode.className;
}
var _67=this.templateContainerNodeClassName;
if(this.windowDecorationName){
_67=this.windowDecorationName+(_67?(" "+_67):"");
}
this.containerNode.className=jetspeed.id.PORTLET_STYLE_CLASS+(_67?(" "+_67):"");
}
this._adjustPositionToDesktopState();
},resetWindow:function(_68){
this.portlet=_68;
this.portletMixinProperties();
this.portletInitDragHandle();
this.portletInitDimensions();
},postCreate:function(_69,_6a,_6b){
if(this.movable&&this.titleBar){
this.drag=new dojo.dnd.Moveable(this,{handle:this.titleBar});
this.setTitleBarDragging();
}
this.domNode.id=this.widgetId;
this.portletInitDimensions();
if(jetspeed.debug.createWindow){
dojo.debug("createdWindow ["+(this.portlet?this.portlet.entityId:this.widgetId)+(this.portlet?(" / "+this.widgetId):"")+"]"+" width="+this.domNode.style.width+" height="+this.domNode.style.height+" left="+this.domNode.style.left+" top="+this.domNode.style.top);
}
this.portletInitialized=true;
var _6c=null;
if(this.portlet){
_6c=this.portlet.getCurrentActionState();
}else{
_6c=this.getInitProperty(jetspeed.id.PORTLET_PROP_WINDOW_STATE);
}
if(_6c==jetspeed.id.ACTION_NAME_MINIMIZE){
this.minimizeWindow();
this.windowActionButtonSync();
this.needsRenderOnRestore=true;
}else{
if(_6c==jetspeed.id.ACTION_NAME_MAXIMIZE){
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
var _6d=this.minimizeWindowTemporarilyRestoreTo;
this.minimizeWindowTemporarilyRestoreTo=null;
if(_6d){
if(_6d!=jetspeed.id.ACTION_NAME_MINIMIZE){
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
var _6f=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_6f.length;i++){
var _71=_6f[i];
if(_71){
_71.domNode.style.display="";
}
}
},hideAllPortletWindows:function(_72){
var _73=dojo.widget.manager.getWidgetsByType(this.getNamespacedType());
for(var i=0;i<_73.length;i++){
var _75=_73[i];
if(_75&&_72&&_72.length>0){
for(var _76=0;_76<_72.length;_76++){
if(_75.widgetId==_72[_76]){
_75=null;
}
}
}
if(_75){
_75.domNode.style.display="none";
}
}
},maximizeWindow:function(evt){
this.hideAllPortletWindows([this.widgetId]);
if(this.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
this.restoreWindow(evt);
}
var _78=this.windowPositionStatic;
this._setLastPositionInfo(_78,true);
var _79=document.getElementById(jetspeed.id.DESKTOP);
if(this.windowPositionStatic){
this.domNode.style.position="absolute";
_79.appendChild(this.domNode);
}
this.setTitleBarDragging(false);
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
var _7a=dojo.html.getAbsolutePosition(_79,true).y;
this.domNode.style.left="1px";
this.domNode.style.top=_7a;
this.windowPositionStatic=false;
var _7b=document.getElementById(jetspeed.id.PAGE);
var _7c=dojo.html.getViewport();
var _7d=dojo.html.getPadding(dojo.body());
this.resizeTo(_7c.width-_7d.width-2,_7c.height-_7d.height-_7a);
this.windowState=jetspeed.id.ACTION_NAME_MAXIMIZE;
},restoreWindow:function(evt){
var _7f=false;
if(this.domNode.style.position=="absolute"){
_7f=true;
}
var _80=null;
if(this.windowState==jetspeed.id.ACTION_NAME_MAXIMIZE){
this.showAllPortletWindows();
this.windowPositionStatic=(this.lastWindowPositionStatic!=null?this.lastWindowPositionStatic:false);
}
this.containerNode.style.display="";
if(this.resizeBar){
this.resizeBar.style.display="";
}
var _80=this.getLastPositionInfo();
var _81=null;
var _82=null;
if(_80!=null){
_81=_80.width;
_82=_80.height;
for(var _83 in _80){
if(_83!="columnInfo"){
this.domNode.style[_83]=_80[_83];
}
}
}
this._adjustPositionToDesktopState();
if(this.windowPositionStatic&&_7f){
if(_80!=null&&_80.columnInfo!=null&&_80.columnInfo.columnIndex!=null){
var _84=jetspeed.page.columns[_80.columnInfo.columnIndex];
if(_80.columnInfo.previousSibling){
dojo.dom.insertAfter(this.domNode,_80.columnInfo.previousSibling);
}else{
if(_80.columnInfo.nextSibling){
dojo.dom.insertBefore(this.domNode,_80.columnInfo.nextSibling);
}else{
_84.domNode.appendChild(this.domNode);
}
}
}else{
if(jetspeed.page.columns!=null&&jetspeed.page.columns.length>0){
dojo.dom.prependChild(this.domNode,jetspeed.page.columns[0].domNode);
}
}
this.domNode.style.position="static";
}
this.resizeTo(_81,_82,true);
this._adjustPositionToDesktopState();
this.windowState=jetspeed.id.ACTION_NAME_RESTORE;
this.setTitleBarDragging();
},getLastPositionInfo:function(){
if(this.windowPositionStatic){
return this.lastTiledPositionInfo;
}
return this.lastUntiledPositionInfo;
},_setLastPositionInfo:function(_85,_86){
if(_86){
this.lastWindowPositionStatic=this.windowPositionStatic;
}
if(this.windowPositionStatic){
if(this.lastTiledPositionInfo==null){
this.lastTiledPositionInfo={};
}
if(_85){
var _87={};
var _88=dojo.dom.getPreviousSiblingElement(this.domNode);
if(_88){
_87.previousSibling=_88;
}else{
_88=dojo.dom.getNextSiblingElement(this.domNode);
if(_88){
_87.nextSibling=_88;
}
}
_87.columnIndex=this.getPageColumnIndex();
this.lastTiledPositionInfo.columnInfo=_87;
}
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
this.lastTiledPositionInfo.height=this.domNode.style.height;
}
this.lastTiledPositionInfo.width="";
}else{
if(this.windowState!=jetspeed.id.ACTION_NAME_MINIMIZE&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE){
var _89=dojo.html.getMarginBox(this.domNode);
this.lastUntiledPositionInfo={width:_89.width,height:_89.height,left:this.domNode.style.left,top:this.domNode.style.top,bottom:this.domNode.style.bottom,right:this.domNode.style.right};
}
}
},_updateLastPositionInfoPositionOnly:function(){
if(!this.windowPositionStatic&&this.lastUntiledPositionInfo!=null){
this.lastUntiledPositionInfo.left=this.domNode.style.left;
this.lastUntiledPositionInfo.top=this.domNode.style.top;
}
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},setTitleBarDragging:function(_8a){
if(!this.titleBar){
return;
}
if(typeof _8a=="undefined"){
_8a=this.getLayoutActionsEnabled();
}
if(_8a){
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
var _8c=this.domNode.style.zIndex;
jetspeed.widget.PortletWindow.superclass.bringToTop.call(this,evt);
if(this.portlet&&!this.windowPositionStatic&&this.windowState!=jetspeed.id.ACTION_NAME_MAXIMIZE&&this.isPortletWindowInitialized()){
this.portlet.submitChangedWindowState();
}
},makeUntiled:function(){
this._setLastPositionInfo(true,false);
var _8d=null;
var _8e=null;
var _8f=null;
var _90=null;
var _91=this.lastUntiledPositionInfo;
if(_91!=null&&_91.width!=null&&_91.height!=null&&_91.left!=null&&_91.top!=null){
_8d=_91.width;
_8e=_91.height;
_8f=_91.left;
_90=_91.top;
}else{
var _92=this.domNode;
var _93=dojo.html.getAbsolutePosition(_92,true);
var _94=dojo.html.getPixelValue(_92,"margin-top",true);
var _95=dojo.html.getPixelValue(_92,"margin-left",true);
var _96=dojo.html.getMarginBox(this.domNode);
_8d=_96.width;
_8e=_96.height;
_8f=_93.x-_94;
_90=_93.y-_95;
}
this.domNode.style.position="absolute";
this.domNode.style.left=_8f;
this.domNode.style.top=_90;
this.windowPositionStatic=false;
this._adjustPositionToDesktopState();
this.resizeTo(_8d,_8e,true);
var _97=document.getElementById(jetspeed.id.DESKTOP);
_97.appendChild(this.domNode);
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
},makeHeightToFit:function(_98,_99){
var _9a=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=true;
this._adjustPositionToDesktopState();
if(_99==null||_99!=true){
}
this.resizeTo(null,null,true);
this._adjustPositionToDesktopState();
if(!_98&&this.portlet){
this.portlet.submitChangedWindowState();
}
},makeHeightVariable:function(_9b){
var _9c=dojo.html.getMarginBox(this.domNode);
this.windowHeightToFit=false;
this._adjustPositionToDesktopState();
var _9d=dojo.html.getMarginBox(this.domNode);
var w=_9d.width;
var h=_9d.height+3;
this.resizeTo(w,h,true);
if(!_9b&&this.portlet){
this.portlet.submitChangedWindowState();
}
},resizeTo:function(w,h,_a2){
if(w==null||w==0||isNaN(w)||h==null||h==0||isNaN(h)){
var _a3=dojo.html.getMarginBox(this.domNode);
if(w==null||w==0||isNaN(w)){
w=_a3.width;
}
if(h==null||h==0||isNaN(h)){
h=_a3.height;
}
}
if(w==this.lastWidthResizeTo&&h==this.lastHeightResizeTo&&!_a2){
return;
}
this.lastWidthResizeTo=w;
this.lastHeightResizeTo=h;
this.resetLostHeightWidth();
dojo.lang.forEach([this.titleBar,this.resizeBar,this.containerNode],function(_a4){
if(_a4!=null){
dojo.html.setMarginBox(_a4,{width:w-this.lostWidth});
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
},_IEPostDrag:function(){
if(this.windowPositionStatic){
var _a5=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_a5,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
}
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
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
var _a6=dojo.html.getMarginBox(this.domNode);
var _a7=dojo.html.getContentBox(this.domNode);
this.lostHeight=(_a6.height-_a7.height)+(this.titleBar?dojo.html.getMarginBox(this.titleBar).height:0)+(this.resizeBar?dojo.html.getMarginBox(this.resizeBar).height:0);
this.lostWidth=_a6.width-_a7.width;
},contentChanged:function(evt){
if(this.processingContentChanged==false){
this.processingContentChanged=true;
if(this.windowHeightToFit){
this.makeHeightToFit(true,true);
}
this.processingContentChanged=false;
}
},closeWindow:function(){
var _a9=this._getActionMenuPopupWidget();
if(_a9!=null){
_a9.destroy();
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
var _ab=dojo.widget.manager.widgets[i];
if(_ab!=null){
var swT=_ab.widgetType;
var swI=_ab.widgetId;
_ab.destroy();
}
}
}
catch(e){
}
}
}
jetspeed.widget.PortletWindow.superclass.closeWindow.call(this);
},dumpPostionInfo:function(){
var _ae=dojo.html.getAbsolutePosition(this.domNode,true);
var _af=dojo.html.getMarginBox(this.domNode);
var _b0=_af.width;
var _b1=_af.height;
var _b2=dojo.html.getMarginBox(this.containerNode);
var _b3=_b2.width;
var _b4=_b2.height;
dojo.debug("window-position ["+this.widgetId+"] x="+_ae.x+" y="+_ae.y+" width="+_b0+" height="+_b1+" cNode-width="+_b3+" cNode-height="+_b4+" document-width="+dojo.html.getMarginBox(document["body"]).width+" document-height="+dojo.html.getMarginBox(document["body"]).height);
},getPageColumnIndex:function(){
return jetspeed.page.getColumnIndexContainingNode(this.domNode);
},getResizeHandleWidget:function(){
return dojo.widget.byId(this.widgetId+"_resize");
},onResized:function(){
jetspeed.widget.PortletWindow.superclass.onResized.call(this);
if(!this.windowIsSizing){
var _b5=this.getResizeHandleWidget();
if(_b5!=null&&_b5._isSizing){
dojo.event.connect(_b5,"_endSizing",this,"endSizing");
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
dojo.lang.setTimeout(this,this._IEPostDrag,jetspeed.widget.ie6PostDragAddDelay);
}
},titleLight:function(){
var _b7=[];
var _b8=null;
var _b9=null;
if(this.portlet){
_b8=this.portlet.getCurrentActionState();
_b9=this.portlet.getCurrentActionMode();
}
for(var _ba in this.actionButtons){
var _bb=this._isWindowActionEnabled(_ba,_b8,_b9);
if(_bb){
var _bc=this.actionButtons[_ba];
_b7.push(_bc);
}
}
for(var i=0;i<_b7.length;i++){
_b7[i].style.display="";
}
this.titleLit=true;
},titleDim:function(_be){
var _bf=[];
for(var _c0 in this.actionButtons){
var _c1=this.actionButtons[_c0];
if(_c1.style.display!="none"){
_bf.push(_c1);
}
}
for(var i=0;i<_bf.length;i++){
_bf[i].style.display="none";
}
this.titleLit=false;
},titleMouseOver:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _c4=this;
this.titleMouseIn=1;
window.setTimeout(function(){
if(_c4.titleMouseIn>0){
_c4.titleLight();
_c4.titleMouseIn=0;
}
},270);
}
},titleMouseOut:function(evt){
if(this.windowDecorationConfig.windowActionButtonHide){
var _c6=this;
var _c7=this.titleMouseIn;
if(_c7>0){
_c7=0;
this.titleMouseIn=_c7;
}
if(_c7==0&&this.titleLit){
window.setTimeout(function(){
if(_c6.titleMouseIn==0&&_c6.titleLit){
_c6.titleDim();
}
},200);
}
}
},getCurrentVolatileWindowState:function(){
if(!this.domNode){
return null;
}
var _c8={};
if(!this.windowPositionStatic){
_c8.zIndex=this.domNode.style.zIndex;
}
return _c8;
},getCurrentWindowState:function(){
if(!this.domNode){
return null;
}
var _c9=this.getCurrentVolatileWindowState();
_c9.width=this.domNode.style.width;
_c9.height=this.domNode.style.height;
_c9[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=this.windowPositionStatic;
_c9[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=this.windowHeightToFit;
if(!this.windowPositionStatic){
_c9.left=this.domNode.style.left;
_c9.top=this.domNode.style.top;
}else{
var _ca=jetspeed.page.getPortletCurrentColumnRow(this.domNode);
if(_ca!=null){
_c9.column=_ca.column;
_c9.row=_ca.row;
_c9.layout=_ca.layout;
}else{
dojo.raise("PortletWindow.getCurrentWindowState cannot not find row/column/layout of window: "+this.widgetId);
}
}
return _c9;
},getCurrentWindowStateForPersistence:function(_cb){
var _cc=null;
if(_cb){
_cc=this.getCurrentVolatileWindowState();
}else{
_cc=this.getCurrentWindowState();
}
this._purifyWindowStatePropertyAsNumber(_cc,"left");
this._purifyWindowStatePropertyAsNumber(_cc,"top");
this._purifyWindowStatePropertyAsNumber(_cc,"width");
this._purifyWindowStatePropertyAsNumber(_cc,"height");
return _cc;
},_purifyWindowStatePropertyAsNumber:function(_cd,_ce){
var _cf=_cd[_ce];
if(_cf!=null){
var _d0="";
for(var i=0;i<_cf.length;i++){
var _d2=_cf.charAt(i);
if((_d2>="0"&&_d2<="9")||_d2=="."){
_d0+=_d2.toString();
}
}
_cd[_ce]=_d0;
}
},setPortletContent:function(_d3,url){
var _d5=_d3.toString();
if(!this.getInitProperty(jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT)){
_d5="<div class=\"PContent\" >"+_d5+"</div>";
}
var _d6=this._splitAndFixPaths_scriptsonly(_d5,url);
this.subWidgetStartIndex=dojo.widget.manager.widgets.length;
this.setContent(_d6);
if(_d6.scripts!=null&&_d6.scripts.length!=null&&_d6.scripts.length>0){
this._executeScripts(_d6.scripts);
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
},setPortletTitle:function(_d7){
if(_d7){
this.title=_d7;
}else{
this.title="";
}
if(this.portletInitialized&&this.titleBarText){
this.titleBarText.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _da=true;
var _db=[];
var _dc=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _dd=/src=(['"]?)([^"']*)\1/i;
var _de=/.*(\bdojo\b\.js(?:\.uncompressed\.js)?)$/;
var _df=/(?:var )?\bdjConfig\b(?:[\s]*=[\s]*\{[^}]+\}|\.[\w]*[\s]*=[\s]*[^;\n]*)?;?|dojo\.hostenv\.writeIncludes\(\s*\);?/g;
var _e0=/dojo\.(addOn(?:Un)?[lL]oad)/g;
var _e1=/dojo\.(?:(?:require(?:After)?(?:If)?)|(?:widget\.(?:manager\.)?registerWidgetPackage)|(?:(?:hostenv\.)?setModulePrefix|registerModulePath)|defineNamespace)\((['"]).*?\1\)\s*;?/;
while(match=_dc.exec(s)){
if(_da&&match[1]){
if(attr=_dd.exec(match[1])){
if(_de.exec(attr[2])){
dojo.debug("Security note! inhibit:"+attr[2]+" from  being loaded again.");
}else{
_db.push({path:attr[2]});
}
}
}
if(match[2]){
var sc=match[2].replace(_df,"");
if(!sc){
continue;
}
while(tmp=_e1.exec(sc)){
requires.push(tmp[0]);
sc=sc.substring(0,tmp.index)+sc.substr(tmp.index+tmp[0].length);
}
sc=sc.replace(_e0,"dojo.widget.byId('"+this.widgetId+"').$1");
if(_da){
_db.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_db,"url":url};
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
var _e6=this.startSize.w-dx;
var _e7=this.startSize.h-dy;
if(this.portletWindow.windowPositionStatic){
_e6=this.startSize.w;
}
if(this.minSize){
if(_e6<this.minSize.w){
_e6=dojo.html.getMarginBox(this.targetWidget.domNode).width;
}
if(_e7<this.minSize.h){
_e7=dojo.html.getMarginBox(this.targetWidget.domNode).height;
}
}
this.targetWidget.resizeTo(_e6,_e7);
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
dojo.dnd.Mover=function(_f0,_f1,_f2,_f3,e){
this.moveInitiated=false;
this.moveableObj=_f3;
this.windowOrLayoutWidget=_f0;
this.node=_f1;
this.windowPositionStatic=_f0.windowPositionStatic;
this.disqualifiedColumnIndexes=null;
if(_f2!=null){
this.disqualifiedColumnIndexes=_f2.getDescendantColumns();
}
this.marginBox={l:e.pageX,t:e.pageY};
var d=this.node.ownerDocument;
var _f6=[d,"onmousemove",this,"onFirstMove"];
dojo.event.connect.apply(dojo.event,_f6);
this.events=[[d,"onmousemove",this,"onMouseMove"],[d,"onmouseup",this,"destroy"],[d,"ondragstart",dojo.event.browser,"stopEvent"],[d,"onselectstart",dojo.event.browser,"stopEvent"]];
for(var i=0;i<this.events.length;i++){
dojo.event.connect.apply(dojo.event,this.events[i]);
}
this.events.push(_f6);
this.isDebug=false;
if(this.isDebug){
this.devInit=false;
this.devLastX=null;
this.devLastY=null;
this.devLastTime=null;
this.devChgThreshold=30;
this.devLrgThreshold=200;
this.devChgSubsqThreshold=10;
this.devTimeThreshold=6000;
}
};
dojo.extend(dojo.dnd.Mover,{onMouseMove:function(e){
dojo.dnd.autoScroll(e);
var m=this.marginBox;
var _fa=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _fd=false;
var _fe=null;
var _ff=null;
if(this.isDebug){
if(!this.devInit){
var _100="";
if(this.disqualifiedColumnIndexes!=null){
_100=jetspeed.debugindentH+"dqCols=["+this.disqualifiedColumnIndexes.split(", ")+"]";
}
var _101=this.windowOrLayoutWidget.title;
if(_101==null){
_101=this.windowOrLayoutWidget.widgetId;
}
dojo.hostenv.println("DRAG \""+this.windowOrLayoutWidget.title+"\""+jetspeed.debugindentH+"m.l = "+m.l+jetspeed.debugindentH+"m.t = "+m.t+_100);
this.devInit=true;
}
_fe=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _102=(Math.abs(x-this.devLastX)>this.devLrgThreshold)||(Math.abs(y-this.devLastY)>this.devLrgThreshold);
if(!_102&&this.devLastTime!=null&&((this.devLastTime+this.devTimeThreshold)>_fe)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgThreshold){
this.devLastX=x;
_fd=true;
}
if(Math.abs(y-this.devLastY)>this.devChgThreshold){
this.devLastY=y;
_fd=true;
}
}
}
}
if(dojo.render.html.mozilla&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_fa=true;
}
dojo.marginBox(this.node,{l:x,t:y});
var _103=jetspeed.widget.pwGhost;
if(this.windowPositionStatic&&!_fa){
var _104=-1;
var _105=this.widthHalf;
var _106=this.heightHalf;
var _107=_106+(_106*0.2);
var _108=jetspeed.page.columns.length;
var _109=[];
var _10a=e.pageX;
var _10b=y+_106;
for(var i=0;i<_108;i++){
var _10d=this.columnDimensions[i];
if(_10d!=null){
if(_10a>=_10d.left&&_10a<=_10d.right){
if(_10b>=(_10d.top-30)){
_109.push(i);
var _10e=Math.min(Math.abs(_10b-(_10d.top)),Math.abs(e.pageY-(_10d.top)));
var _10f=Math.min(Math.abs(_10b-(_10d.yhalf)),Math.abs(e.pageY-(_10d.yhalf)));
var lowY=Math.min(_10e,_10f);
_109.push(lowY);
}else{
if(_fd){
if(_ff==null){
_ff=[];
}
var _111=(_10d.top-30)-_10b;
_ff.push(jetspeed.debugindent3+dojo.string.padRight(String(i),2,jetspeed.debugindentch)+" y! "+dojo.string.padRight(String(_111),4,jetspeed.debugindentch)+jetspeed.debugindentH+"t="+_10d.top+jetspeed.debugindentH+"b="+_10d.bottom+jetspeed.debugindentH+"l="+_10d.left+jetspeed.debugindentH+"r="+_10d.right);
}
}
}else{
if(_fd&&_10a>_10d.width){
if(_ff==null){
_ff=[];
}
var _111=_10a-_10d.width;
_ff.push(jetspeed.debugindent3+dojo.string.padRight(String(i),2,jetspeed.debugindentch)+" x! "+dojo.string.padRight(String(_111),4,jetspeed.debugindentch)+jetspeed.debugindentH+"t="+_10d.top+jetspeed.debugindentH+"b="+_10d.bottom+jetspeed.debugindentH+"l="+_10d.left+jetspeed.debugindentH+"r="+_10d.right);
}
}
}
}
var _112=_109.length;
if(_112>0){
var _113=-1;
var _114=0;
var i=1;
while(i<_112){
if(_113==-1||_114>_109[i]){
_113=_109[i-1];
_114=_109[i];
}
i=i+2;
}
_104=_113;
}
var col=(_104>=0?jetspeed.page.columns[_104]:null);
if(_fd){
dojo.hostenv.println(jetspeed.debugindent+"x="+x+jetspeed.debugindentH+"y="+y+jetspeed.debugindentH+"col="+_104+jetspeed.debugindentH+"xTest="+_10a+jetspeed.debugindentH+"yTest="+_10b);
var i=0;
while(i<_112){
var colI=_109[i];
var _10d=this.columnDimensions[colI];
dojo.hostenv.println(jetspeed.debugindent3+dojo.string.padRight(String(colI),2,jetspeed.debugindentch)+" -> "+dojo.string.padRight(String(_109[i+1]),4,jetspeed.debugindentch)+jetspeed.debugindentH+"t="+_10d.top+jetspeed.debugindentH+"b="+_10d.bottom+jetspeed.debugindentH+"l="+_10d.left+jetspeed.debugindentH+"r="+_10d.right);
i=i+2;
}
if(_ff!=null){
for(i=0;i<_ff.length;i++){
dojo.hostenv.println(_ff[i]);
}
}
this.devLastTime=_fe;
this.devChgThreshold=this.devChgSubsqThreshold;
}
if(_103.col!=col&&col!=null){
dojo.dom.removeNode(_103);
_103.col=col;
col.domNode.appendChild(_103);
}
var _117=null,_118=null;
if(col!=null){
_117=jetspeed.ui.getPortletWindowChildren(col.domNode,_103);
_118=_117.portletWindowNodes;
}
if(_118!=null&&_118.length>1){
var _119=_117.matchIndex;
var _11a=-1;
var _11b=-1;
if(_119>0){
var _11a=dojo.html.getAbsolutePosition(_118[_119-1],true).y;
if((y-25)<=_11a){
dojo.dom.removeNode(_103);
dojo.dom.insertBefore(_103,_118[_119-1],true);
}
}
if(_119!=(_118.length-1)){
var _11b=dojo.html.getAbsolutePosition(_118[_119+1],true).y;
if((y+10)>=_11b){
if(_119+2<_118.length){
dojo.dom.insertBefore(_103,_118[_119+2],true);
}else{
col.domNode.appendChild(_103);
}
}
}
}
}
},onFirstMove:function(){
var mP=dojo.marginBox(this.node);
this.marginBoxPrev=mP;
this.staticWidth=null;
var _11d=jetspeed.widget.pwGhost;
var _11e=dojo.render.html.mozilla;
var m=null;
if(this.windowPositionStatic){
m={w:mP.w,h:mP.h};
var _120=this.node.parentNode;
var _121=document.getElementById(jetspeed.id.DESKTOP);
this.staticWidth=this.node.style.width;
var _122=dojo.html.getAbsolutePosition(this.node,true);
var _123=dojo._getMarginExtents(this.node);
m.l=_122.left-_123.l;
m.t=_122.top-_123.t;
if(_11e){
dojo.setMarginBox(_11d,null,null,null,mP.h,null);
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
this.node.style.position="absolute";
_120.insertBefore(_11d,this.node);
if(!_11e){
dojo.setMarginBox(_11d,null,null,null,mP.h,null);
}
_121.appendChild(this.node);
var _124=jetspeed.ui.getPortletWindowChildren(_120,_11d);
this.prevColumnNode=_120;
this.prevIndexInCol=_124.matchIndex;
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
var _125=this.windowOrLayoutWidget.getPageColumnIndex();
this.columnDimensions=new Array(jetspeed.page.columns.length);
for(var i=0;i<jetspeed.page.columns.length;i++){
var col=jetspeed.page.columns[i];
if(!col.columnContainer&&!col.layoutHeader){
if(this.qualifyTargetColumn(col)){
var _128=dojo.html.getAbsolutePosition(col.domNode,true);
var _129=dojo.html.getMarginBox(col.domNode);
var _12a={left:(_128.x),right:(_128.x+_129.width),top:(_128.y),bottom:(_128.y+_129.height)};
_12a.height=_12a.bottom-_12a.top;
_12a.width=_12a.right-_12a.left;
_12a.yhalf=_12a.top+(_12a.height/2);
this.columnDimensions[i]=_12a;
}
}
}
var _12b=(_125>=0?jetspeed.page.columns[_125]:null);
_11d.col=_12b;
}
},qualifyTargetColumn:function(_12c){
if(_12c!=null&&!_12c.layoutActionsDisabled){
if(this.disqualifiedColumnIndexes!=null&&this.disqualifiedColumnIndexes[_12c.getPageColumnIndex()]!=null){
return false;
}
return true;
}
return false;
},destroy:function(){
var _12d=this.windowOrLayoutWidget;
if(this.moveInitiated){
try{
var _12e=jetspeed.widget.pwGhost;
if(this.windowPositionStatic){
var n=this.node;
if(_12e&&_12e.col){
this.windowOrLayoutWidget.column=0;
dojo.dom.insertBefore(n,_12e,true);
}else{
dojo.dom.insertAtIndex(n,this.prevColumnNode,this.prevIndexInCol);
}
if(_12e){
dojo.dom.removeNode(_12e);
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
dojo.debug("Mover "+(_12d==null?"<unknown>":_12d.widgetId)+" destroy reset-window error: "+e.toString());
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
dojo.debug("Mover "+(_12d==null?"<unknown>":_12d.widgetId)+" destroy clean-up error: "+e.toString());
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
}});
dojo.dnd.Moveable=function(_131,opt){
this.enabled=true;
this.mover=null;
this.windowOrLayoutWidget=_131;
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
var _135=null;
var _136=this.windowOrLayoutWidget;
var _137=null;
this.beforeDragColumnRowInfo=null;
if(!_136.isLayoutPane){
_135=_136.domNode;
}else{
_137=_136.containingColumn;
if(_137!=null){
_135=_137.domNode;
if(_135!=null){
this.beforeDragColumnRowInfo=jetspeed.page.getPortletCurrentColumnRow(_135);
}
}
}
if(_135!=null){
this.node=_135;
this.mover=new dojo.dnd.Mover(_136,_135,_137,this,e);
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
dojo.getMarginBox=function(node,_13f){
var s=_13f||dojo.gcs(node),me=dojo._getMarginExtents(node,s);
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
dojo.setMarginBox=function(node,_14a,_14b,_14c,_14d,_14e){
var s=_14e||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s);
if(_14c!=null&&_14c>=0){
_14c=Math.max(_14c-pb.w-mb.w,0);
}
if(_14d!=null&&_14d>=0){
_14d=Math.max(_14d-pb.h-mb.h,0);
}
dojo._setBox(node,_14a,_14b,_14c,_14d);
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
dojo._getPadExtents=function(n,_15c){
var s=_15c||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_162){
var s=_162||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_167){
var s=_167||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(dojo.render.html.safari&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_16f){
var ne="none",px=dojo._toPixelValue,s=_16f||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_178,_179){
return (parseFloat(_179)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_17b,_17c){
if(!_17c){
return 0;
}
if(_17c.slice&&(_17c.slice(-2)=="px")){
return parseFloat(_17c);
}
with(_17b){
var _17d=style.left;
var _17e=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_17c;
_17c=style.pixelLeft;
}
catch(e){
_17c=0;
}
style.left=_17d;
runtimeStyle.left=_17e;
}
return _17c;
};
}
dojo.gcs=dojo.getComputedStyle;

