dojo.provide("jetspeed.widget.PortletWindow");
dojo.require("jetspeed.desktop.core");
jetspeed.widget.PortletWindow=function(){
this.windowInitialized=false;
this.actionButtons={};
this.actionMenus={};
this.actionMenuWidget=null;
this.tooltips=[];
this.subWidgetStartIndex=-1;
this.subWidgetEndIndex=-1;
this._onLoadStack=[];
this._onUnloadStack=[];
this._callOnUnload=false;
};
dojo.extend(jetspeed.widget.PortletWindow,{title:"",nextIndex:1,resizable:true,movable:true,decName:null,decConfig:null,posStatic:false,heightToFit:false,titleMouseIn:0,titleLit:false,portlet:null,altInitParams:null,inContentChgd:false,exclPContent:false,minimizeTempRestore:null,executeScripts:false,scriptSeparation:false,adjustPaths:false,parseContent:true,dbProfile:(djConfig.isDebug&&jetspeed.debug.profile),dbOn:djConfig.isDebug,dbMenuDims:"Dump Dimensions",altInitParamsDef:function(_1,_2){
if(!_1){
_1={getProperty:function(_3){
if(!_3){
return null;
}
return this.altInitParams[_3];
},retrieveContent:function(_4,_5){
var _6=this.altInitParams[jetspeed.id.PP_CONTENT_RETRIEVER];
if(_6){
_6.getContent(_5,_4,this,jetspeed.debugPortletDumpRawContent);
}else{
jetspeed.url.retrieveContent(_5,_4,this,jetspeed.debugPortletDumpRawContent);
}
}};
}
if(!_2){
_2={};
}
if(_2.altInitParams){
_1.altInitParams=_2.altInitParams;
}else{
_1.altInitParams=_2;
}
return _1;
},build:function(_7,_8){
var _9=jetspeed;
var _a=_9.id;
var _b=_9.prefs;
var _c=_9.page;
var _d=_9.css;
var _e=_9.ui;
var _f=document;
var _10=_9.docBody;
var _11=dojo;
var _12=_9.widget.PortletWindow.prototype.nextIndex;
this.portletIndex=_12;
var ie6=_9.UAie6;
this.ie6=ie6;
var _14=false;
if(_7){
if(_7.portlet){
this.portlet=_7.portlet;
}
if(_7.altInitParams){
this.altInitParams=_7.altInitParams;
}
if(_7.printMode){
_14=true;
}
}
var _15=this.portlet;
var iP=(_15?_15.getProperties():(this.altInitParams?this.altInitParams:{}));
var _17=iP[_a.PP_WIDGET_ID];
if(!_17){
if(_15){
_11.raise("PortletWindow is null for portlet: "+_15.entityId);
}else{
_17=_a.PW_ID_PREFIX+_12;
}
}
this.widgetId=_17;
_9.widget.PortletWindow.prototype.nextIndex++;
var _18=iP[_a.PP_WINDOW_DECORATION];
if(!_18){
_18=this.portletDecorationName;
if(!_18){
_18=_c.getPortletDecorationDefault();
}
}
this.decName=_18;
var wDC=_9.loadPortletDecorationStyles(_18);
if(wDC==null){
wDC={};
}
this.decConfig=wDC;
var _1a=wDC.dNodeClass;
var _1b=wDC.cNodeClass;
if(_1a==null||_1b==null){
_1a=_a.PWIN_CLASS;
_1b="portletWindowClient";
if(_18){
_1a=_18+" "+_1a;
_1b=_18+" "+_1b;
}
_1a=_a.P_CLASS+" "+_1a;
_1b=_a.P_CLASS+" "+_1b;
wDC.dNodeClass=_1a;
wDC.cNodeClass=_1b;
}
var _1c=_f.createElement("div");
_1c.id=_17;
_1c.className=_1a;
_1c.style.display="none";
var _1d=_f.createElement("div");
_1d.className=_1b;
var _1e=null,_1f=null,_20=null,_21=null;
if(!_14){
_1e=_f.createElement("div");
_1e.className="portletWindowTitleBar";
_20=_f.createElement("img");
_20.className="portletWindowTitleBarIcon";
var _22=_f.createElement("div");
_22.className="portletWindowTitleText";
_1e.appendChild(_20);
_1e.appendChild(_22);
_1f=_f.createElement("div");
_1f.className="portletWindowResizebar";
this.tbNode=_1e;
_21=_d.cssBase.concat();
this.tbNodeCss=_21;
this.tbIconNode=_20;
this.tbTextNode=_22;
this.rbNode=_1f;
this.rbNodeCss=_d.cssBase.concat();
}
if(_1e!=null){
_1c.appendChild(_1e);
}
_1c.appendChild(_1d);
if(_1f!=null){
_1c.appendChild(_1f);
}
this.domNode=_1c;
var _23=_d.cssPosition.concat();
this.dNodeCss=_23;
this.containerNode=_1d;
var _24=_d.cssOverflow.concat();
this.cNodeCss=_24;
this.setPortletTitle(iP[_a.PP_WINDOW_TITLE]);
var _25=iP[_a.PP_WINDOW_POSITION_STATIC];
this.posStatic=this.preMaxPosStatic=_25;
var _26=iP[_a.PP_WINDOW_HEIGHT_TO_FIT];
this.heightToFit=this.preMaxHeightToFit=_26;
var _27=null,_28=null,_29=null,_2a=null;
if(_15){
var _2b=_15.getInitialWinDims();
_27=_2b.width;
_28=_2b.height;
_29=_2b.left;
_2a=_2b.top;
}else{
_27=iP[_a.PP_WIDTH];
_28=iP[_a.PP_HEIGHT];
_29=iP[_a.PP_LEFT];
_2a=iP[_a.PP_TOP];
}
var _2c={};
var _2d={width:null};
if(_27!=null&&_27>0){
_2c.width=_27=Math.floor(_27);
}else{
_2c.width=_27=_b.windowWidth;
}
if(_28!=null&&_28>0){
_2c.height=_2d.height=_28=Math.floor(_28);
}else{
_2c.height=_2d.height=_28=_b.windowHeight;
}
if(_29!=null&&_29>=0){
_2c.left=Math.floor(_29);
}else{
if(!_25){
_2c.left=(((_12-2)*30)+200);
}
}
if(_2a!=null&&_2a>=0){
_2c.top=Math.floor(_2a);
}else{
if(!_25){
_2c.top=(((_12-2)*30)+170);
}
}
this.dimsUntiled=_2c;
this.dimsTiled=_2d;
this.exclPContent=iP[_a.PP_EXCLUDE_PCONTENT];
_c.putPWin(this);
_10.appendChild(_1c);
if(_20){
var _2e=null;
if(wDC.windowIconEnabled&&wDC.windowIconPath!=null){
var wI=iP[_a.PP_WINDOW_ICON];
if(!wI){
wI="document.gif";
}
_2e=new _11.uri.Uri(_9.url.basePortalDesktopUrl()+wDC.windowIconPath+wI);
_2e=_2e.toString();
if(_2e.length==0){
_2e=null;
}
this.iconSrc=_2e;
}
if(_2e){
_20.src=_2e;
}else{
_11.dom.removeNode(_20);
this.tbIconNode=_20=null;
}
}
if(_1e){
if(_9.UAmoz||_9.UAsaf){
if(_9.UAmoz){
_21[_d.cssNoSelNm]=" -moz-user-select: ";
}else{
_21[_d.cssNoSelNm]=" -khtml-user-select: ";
}
_21[_d.cssNoSel]="none";
_21[_d.cssNoSelEnd]=";";
}else{
if(_9.UAie){
_1e.unselectable="on";
}
}
var _30=null;
var _31=_11.event;
var _32,aNm;
var _34=new Array();
var _35=false;
if(wDC.windowActionButtonOrder!=null){
var _36=new Array();
if(_15){
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_32=false;
if(_15.getAction(aNm)!=null||_b.windowActionDesktop[aNm]!=null){
_32=true;
}else{
if(aNm==_a.ACT_RESTORE||aNm==_a.ACT_MENU){
_32=true;
}
}
if(_32){
_36.push(aNm);
}
}
}else{
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_32=false;
if(aNm==_a.ACT_MINIMIZE||aNm==_a.ACT_MAXIMIZE||aNm==_a.ACT_RESTORE||aNm==_a.ACT_MENU||_b.windowActionDesktop[aNm]!=null){
_32=true;
}
if(_32){
_36.push(aNm);
}
}
}
var _38=(wDC.windowActionButtonMax==null?-1:wDC.windowActionButtonMax);
if(_38!=-1&&_36.length>=_38){
var _39=0;
var _3a=_36.length-_38+1;
for(var i=0;i<_36.length&&_39<_3a;i++){
if(_36[i]!=_a.ACT_MENU){
_34.push(_36[i]);
_36[i]=null;
_39++;
}
}
}
if(wDC.windowActionNoImage){
for(var i=0;i<_36.length;i++){
if(wDC.windowActionNoImage[_36[i]]!=null){
if(_36[i]==_a.ACT_MENU){
_35=true;
}else{
_34.push(_36[i]);
}
_36[i]=null;
}
}
}
var _3c=_c.tooltipMgr;
for(var i=0;i<_36.length;i++){
if(_36[i]!=null){
this._createActionButtonNode(_36[i],_f,_10,_3c,_9,_b,_e,_31);
}
}
}
if(wDC.windowActionMenuOrder){
if(_15){
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
_32=false;
if(_15.getAction(aNm)!=null||_b.windowActionDesktop[aNm]!=null){
_32=true;
}
if(_32){
_34.push(aNm);
}
}
}else{
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
if(_b.windowActionDesktop[aNm]!=null){
_34.push(aNm);
}
}
}
}
if(_34.length>0||this.dbOn){
var _3d=new Object();
var _3e=new Array();
for(var i=0;i<_34.length;i++){
aNm=_34[i];
if(aNm!=null&&_3d[aNm]==null&&this.actionButtons[aNm]==null){
_3e.push(aNm);
_3d[aNm]=true;
}
}
if(this.dbOn){
_3e.push({aNm:this.dbMenuDims,dev:true});
}
if(_3e.length>0){
this._createActionMenu(_3e,_10);
if(_35){
_e.evtConnect("after",_1e,"oncontextmenu",this,"windowActionMenuOpen",_31);
}
}
}
this.windowActionButtonSync();
if(wDC.windowDisableResize){
this.resizable=false;
}
if(wDC.windowDisableMove){
this.movable=false;
}
}
var _3f=this.resizable;
var _40=null;
if(_3f&&_1f){
var _41=_17+"_resize";
var _40=_9.widget.CreatePortletWindowResizeHandler(this,_9);
this.resizeHandle=_40;
if(_40){
_40.domNode.style.position="static";
_1f.appendChild(_40.domNode);
}
}else{
this.resizable=false;
}
if(ie6){
this.bgIframe=new _11.html.BackgroundIframe(_1c);
}
_10.removeChild(_1c);
_8.appendChild(_1c);
if(!wDC.layoutExtents){
var _42="display: block; width: "+_27+"px"+((_28!=null&&_28>0)?("; height: "+_28+"px"):"");
_1c.style.cssText=_42;
this._createLayoutExtents(wDC,false,_1c,_1d,_1e,_1f,_11,_9);
}
if(this.movable&&_1e){
this.drag=new _11.dnd.Moveable(this,{handle:_1e});
this._setTitleBarDragging(true,_d);
}
this._setAsTopZIndex(_c,_d,_23,_25);
this._alterCss(true,true);
if(!_25){
this._addUntiledEvents();
}
this.windowInitialized=true;
if(_9.debug.createWindow){
_11.debug("createdWindow ["+(_15?_15.entityId:_17)+(_15?(" / "+_17):"")+"]"+" width="+_1c.style.width+" height="+_1c.style.height+" left="+_1c.style.left+" top="+_1c.style.top);
}
var iWS=null;
if(_15){
iWS=_15.getCurrentActionState();
}else{
iWS=iP[_a.PP_WINDOW_STATE];
}
if(iWS==_a.ACT_MINIMIZE){
this.minimizeWindow();
this.windowActionButtonSync();
this.needsRenderOnRestore=true;
}else{
if(iWS==_a.ACT_MAXIMIZE){
_11.lang.setTimeout(this,this._postCreateMaximizeWindow,1500);
}
}
if(ie6&&_9.widget.ie6ZappedContentHelper==null){
var _44=_f.createElement("span");
_44.id="ie6ZappedContentHelper";
_9.widget.ie6ZappedContentHelper=_44;
}
if(_9.widget.pwGhost==null&&_c!=null){
var _45=_f.createElement("div");
_45.id="pwGhost";
var _46=_c.getPortletDecorationDefault();
if(!_46){
_46=_18;
}
_45.className=_a.P_CLASS+(_46?(" "+_46):"")+" "+_1a;
_45.style.position="static";
_45.style.width="";
_45.style.left="auto";
_45.style.top="auto";
_9.widget.pwGhost=_45;
}
},_createActionButtonNode:function(aNm,doc,_49,_4a,_4b,_4c,_4d,_4e){
if(aNm!=null){
var _4f=doc.createElement("div");
_4f.className="portletWindowActionButton";
_4f.style.backgroundImage="url("+_4c.getPortletDecorationBaseUrl(this.decName)+"/images/desktop/"+aNm+".gif)";
_4f.actionName=aNm;
this.actionButtons[aNm]=_4f;
this.tbNode.appendChild(_4f);
_4d.evtConnect("after",_4f,"onclick",this,"windowActionButtonClick",_4e);
if(this.decConfig!=null&&this.decConfig.windowActionButtonTooltip){
this.tooltips.push(_4a.addNode(_4f,this._getActionLabel(aNm),true,_4b,_4d,_4e));
}else{
_4d.evtConnect("after",_4f,"onmousedown",_4e.browser,"stopEvent",_4e);
}
}
},_getActionLabel:function(aNm){
if(aNm==null){
return null;
}
var _51=null;
var _52=jetspeed.prefs.desktopActionLabels;
if(_52!=null){
_51=_52[aNm];
}
if(_51==null||_51.length==0){
if(this.portlet){
var _53=this.portlet.getAction(aNm);
if(_53!=null){
_51=_53.label;
}
}
}
if(_51==null||_51.length==0){
_51=dojo.string.capitalize(aNm);
}
return _51;
},_createActionMenu:function(_54,_55){
if(_54==null||_54.length==0){
return;
}
var _56=this;
var aNm,_58,_59,_5a;
var _5b=function(mi){
var _aN=mi.jsActNm;
if(!mi.jsActDev){
_56.windowActionProcess(_aN);
}else{
_56.windowActionProcessDev(_aN);
}
};
var _5e=dojo.widget.createWidget("PopupMenu2",{id:this.widgetId+"_ctxmenu",contextMenuForWindow:false,onItemClick:_5b},null);
for(var i=0;i<_54.length;i++){
aNm=_54[i];
_5a=false;
if(!aNm.dev){
_58=this._getActionLabel(aNm);
}else{
_5a=true;
_58=aNm=aNm.aNm;
}
_59=dojo.widget.createWidget("MenuItem2",{caption:_58,jsActNm:aNm,jsActDev:_5a});
this.actionMenus[aNm]=_59;
_5e.addChild(_59);
}
_55.appendChild(_5e.domNode);
this.actionMenuWidget=_5e;
},_createLayoutExtents:function(_60,_61,_62,_63,_64,_65,_66,_67){
var _68=_66.gcs(_62);
var _69=_66.gcs(_63);
var _6a=null,_6b=null;
var _6c={dNode:this._createNodeLEs(_62,_68,_66,_67),cNode:this._createNodeLEs(_63,_69,_66,_67)};
if(_64){
_6a=_66.gcs(_64);
_6c.tbNode=this._createNodeLEs(_64,_6a,_66,_67);
var _6d=_6a.cursor;
if(_6d==null||_6d.length==0){
_6d="move";
}
_60.dragCursor=_6d;
}
if(_65){
_6b=_66.gcs(_65);
_6c.rbNode=this._createNodeLEs(_65,_6b,_66,_67);
}
var _6e=_66.getMarginBox(_62,_68,_67);
var _6f=_66.getContentBox(_62,_68,_67);
_6c.lostHeight=(_6e.h-_6f.h)+(_64?_66.getMarginBox(_64,_6a,_67).h:0)+(_65?_66.getMarginBox(_65,_6b,_67).h:0);
_6c.lostWidth=_6e.w-_6f.w;
if(!_61){
_60.layoutExtents=_6c;
}else{
_60.layoutExtentsIFrame=_6c;
}
},testLost:function(){
var _70=dojo;
var _71=jetspeed;
var _72=this.domNode;
var _73=this.tbNode;
var _74=this.rbNode;
var _75=_70.gcs(_72);
var _76=_70.gcs(_73);
var _77=_70.gcs(_74);
var _78=_70.getMarginBox(_72,_75,_71);
var _79=_70.getContentBox(_72,_75,_71);
var _7a=_70.getMarginBox(_73,_76,_71);
var _7b=_70.getMarginBox(_74,_77,_71);
var _7c={id:this.widgetId,dMBw:_78.w,dMBh:_78.h,dCBw:_79.h,dCBh:_79.w,tbMBh:_7a.h,rbMBh:_7b.h,dNodePos:_75.position,dNodeDis:_75.display,dNodeWidth:_75.width,dNodeHeight:_75.height};
_7c.lostHeight=(_78.h-_79.h)+(_73?_70.getMarginBox(_73,_76,_71).h:0)+(_74?_70.getMarginBox(_74,_77,_71).h:0);
_7c.lostWidth=_78.w-_79.w;
var _7d=jetspeed.printobj(_7c);
if(jetspeed.lostFirst==null){
jetspeed.lostFirst=_7d;
}
return _7d;
},_createNodeLEs:function(_7e,_7f,_80,_81){
var _82=_80._getPadBorderExtents(_7e,_7f);
var _83=_80._getMarginExtents(_7e,_7f,_81);
return {padborder:_82,margin:_83,lessW:(_82.w+_83.w),lessH:(_82.h+_83.h)};
},windowActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.windowActionProcess(evt.target.actionName,evt);
},windowActionMenuOpen:function(evt){
var _86=null;
var _87=null;
if(this.portlet){
_86=this.portlet.getCurrentActionState();
_87=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionMenus){
var _89=this.actionMenus[aNm];
if(this._isWindowActionEnabled(aNm,_86,_87)){
_89.domNode.style.display="";
}else{
_89.domNode.style.display="none";
}
}
this.actionMenuWidget.onOpen(evt);
},windowActionProcessDev:function(aNm,evt){
if(aNm==this.dbMenuDims){
this.dumpPos();
}
},windowActionProcess:function(aNm,evt){
var _8e=jetspeed;
var _8f=_8e.id;
if(aNm==null){
return;
}
if(_8e.prefs.windowActionDesktop[aNm]!=null){
if(aNm==_8f.ACT_DESKTOP_TILE){
this.makeTiled();
}else{
if(aNm==_8f.ACT_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(aNm==_8f.ACT_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(aNm==_8f.ACT_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false,false);
}
}
}
}
}else{
if(aNm==_8f.ACT_MENU){
this.windowActionMenuOpen(evt);
}else{
if(aNm==_8f.ACT_MINIMIZE){
if(this.portlet&&this.windowState==_8f.ACT_MAXIMIZE){
this.needsRenderOnRestore=true;
}
this.minimizeWindow();
if(this.portlet){
_8e.changeActionForPortlet(this.portlet.getId(),_8f.ACT_MINIMIZE,null);
}
if(!this.portlet){
this.windowActionButtonSync();
}
}else{
if(aNm==_8f.ACT_RESTORE){
var _90=false;
if(this.portlet){
if(this.windowState==_8f.ACT_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_90=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
if(this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.portlet.renderAction(aNm);
}else{
_8e.changeActionForPortlet(this.portlet.getId(),_8f.ACT_RESTORE,null);
}
}
if(!_90){
this.restoreWindow();
}
if(!this.portlet){
this.windowActionButtonSync();
}
}else{
if(aNm==_8f.ACT_MAXIMIZE){
if(this.portlet&&this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(aNm);
}else{
this.windowActionButtonSync();
}
}else{
if(aNm==_8f.ACT_REMOVEPORTLET){
if(this.portlet){
var _91=dojo.widget.byId(_8f.PG_ED_WID);
if(_91!=null){
_91.deletePortlet(this.portlet.entityId,this.title);
}
}
}else{
if(this.portlet){
this.portlet.renderAction(aNm);
}
}
}
}
}
}
}
},_isWindowActionEnabled:function(aNm,_93,_94){
var _95=jetspeed;
var _96=_95.id;
var _97=false;
if(this.minimizeTempRestore!=null){
if(this.portlet){
var _98=this.portlet.getAction(aNm);
if(_98!=null){
if(_98.id==_96.ACT_REMOVEPORTLET){
if(_95.page.editMode&&this.getLayoutActionsEnabled()){
_97=true;
}
}
}
}
}else{
if(aNm==_96.ACT_MENU){
if(!this._windowActionMenuIsEmpty()){
_97=true;
}
}else{
if(_95.prefs.windowActionDesktop[aNm]!=null){
if(this.getLayoutActionsEnabled()){
if(aNm==_96.ACT_DESKTOP_HEIGHT_EXPAND){
if(!this.heightToFit){
_97=true;
}
}else{
if(aNm==_96.ACT_DESKTOP_HEIGHT_NORMAL){
if(this.heightToFit){
_97=true;
}
}else{
if(aNm==_96.ACT_DESKTOP_TILE&&_95.prefs.windowTiling){
if(!this.posStatic){
_97=true;
}
}else{
if(aNm==_96.ACT_DESKTOP_UNTILE){
if(this.posStatic){
_97=true;
}
}
}
}
}
}
}else{
if(this.portlet){
var _98=this.portlet.getAction(aNm);
if(_98!=null){
if(_98.id==_96.ACT_REMOVEPORTLET){
if(_95.page.editMode&&this.getLayoutActionsEnabled()){
_97=true;
}
}else{
if(_98.type==_96.PORTLET_ACTION_TYPE_MODE){
if(aNm!=_94){
_97=true;
}
}else{
if(aNm!=_93){
_97=true;
}
}
}
}else{
_97=true;
}
}else{
if(aNm==_96.ACT_MAXIMIZE){
if(aNm!=this.windowState&&this.minimizeTempRestore==null){
_97=true;
}
}else{
if(aNm==_96.ACT_MINIMIZE){
if(aNm!=this.windowState){
_97=true;
}
}else{
if(aNm==_96.ACT_RESTORE){
if(this.windowState==_96.ACT_MAXIMIZE||this.windowState==_96.ACT_MINIMIZE){
_97=true;
}
}else{
_97=true;
}
}
}
}
}
}
}
return _97;
},_windowActionMenuIsEmpty:function(){
var _99=null;
var _9a=null;
if(this.portlet){
_99=this.portlet.getCurrentActionState();
_9a=this.portlet.getCurrentActionMode();
}
var _9b=true;
for(var aNm in this.actionMenus){
var _9d=this.actionMenus[aNm];
if(aNm!=jetspeed.id.ACT_MENU&&this._isWindowActionEnabled(aNm,_99,_9a)){
_9b=false;
break;
}
}
return _9b;
},windowActionButtonSync:function(){
var _9e=this.decConfig.windowActionButtonHide;
var _9f=null;
var _a0=null;
if(this.portlet){
_9f=this.portlet.getCurrentActionState();
_a0=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionButtons){
var _a2=false;
if(!_9e||this.titleLit){
_a2=this._isWindowActionEnabled(aNm,_9f,_a0);
}
var _a3=this.actionButtons[aNm];
if(_a2){
_a3.style.display="";
}else{
_a3.style.display="none";
}
}
},_postCreateMaximizeWindow:function(){
this.maximizeWindow();
this.windowActionButtonSync();
},minimizeWindowTemporarily:function(){
if(this.minimizeTempRestore==null){
this.minimizeTempRestore=this.windowState;
if(this.windowState!=jetspeed.id.ACT_MINIMIZE){
this.minimizeWindow();
}
this.windowActionButtonSync();
}
},restoreFromMinimizeWindowTemporarily:function(){
var _a4=this.minimizeTempRestore;
this.minimizeTempRestore=null;
if(_a4){
if(_a4!=jetspeed.id.ACT_MINIMIZE){
this.restoreWindow();
}
this.windowActionButtonSync();
}
},minimizeWindow:function(evt){
if(!this.tbNode){
return;
}
var _a6=jetspeed;
if(this.windowState==jetspeed.id.ACT_MAXIMIZE){
this.showAllPortletWindows();
this.restoreWindow(evt);
}
this._updtDimsObj(false);
var _a7=_a6.css.cssDis;
this.cNodeCss[_a7]="none";
if(this.rbNodeCss){
this.rbNodeCss[_a7]="none";
}
this.containerNode.style.display="none";
if(this.rbNode){
this.rbNode.style.display="none";
}
dojo.html.setContentBox(this.domNode,{height:dojo.html.getMarginBox(this.tbNode).height});
this.windowState=_a6.id.ACT_MINIMIZE;
},showAllPortletWindows:function(){
var _a8=jetspeed.page.getPWins(false);
for(var i=0;i<_a8.length;i++){
var _aa=_a8[i];
if(_aa){
_aa.domNode.style.display="";
}
}
},hideAllPortletWindows:function(_ab){
var _ac=jetspeed.page.getPWins(false);
for(var i=0;i<_ac.length;i++){
var _ae=_ac[i];
if(_ae&&_ab&&_ab.length>0){
for(var _af=0;_af<_ab.length;_af++){
if(_ae.widgetId==_ab[_af]){
_ae=null;
break;
}
}
}
if(_ae){
_ae.domNode.style.display="none";
}
}
},maximizeWindow:function(evt){
var _b1=jetspeed;
var _b2=_b1.id;
var _b3=this.domNode;
var _b4=[this.widgetId];
this.hideAllPortletWindows(_b4);
if(this.windowState==_b2.ACT_MINIMIZE){
this.restoreWindow(evt);
}
var _b5=this.posStatic;
this.preMaxPosStatic=_b5;
this.preMaxHeightToFit=this.heightToFit;
var _b6=_b5;
this._updtDimsObj(_b6);
var _b7=document.getElementById(_b2.DESKTOP);
var _b8=dojo.html.getAbsolutePosition(_b7,true).y;
var _b9=dojo.html.getViewport();
var _ba=dojo.html.getPadding(_b1.docBody);
this.dimsUntiledTemp={width:_b9.width-_ba.width-2,height:_b9.height-_ba.height-_b8,left:1,top:_b8};
this._setTitleBarDragging(true,_b1.css,false);
this.posStatic=false;
this.heightToFit=false;
this._alterCss(true,true);
if(_b5){
_b7.appendChild(_b3);
}
this.windowState=_b2.ACT_MAXIMIZE;
},restoreWindow:function(evt){
var _bc=jetspeed;
var _bd=_bc.id;
var _be=_bc.css;
var _bf=this.domNode;
var _c0=false;
if(_bf.style.position=="absolute"){
_c0=true;
}
var _c1=null;
if(this.windowState==_bd.ACT_MAXIMIZE){
this.showAllPortletWindows();
this.posStatic=this.preMaxPosStatic;
this.heightToFit=this.preMaxHeightToFit;
this.dimsUntiledTemp=null;
}
var _c2=_be.cssDis;
this.cNodeCss[_c2]="block";
if(this.rbNodeCss){
this.rbNodeCss[_c2]="block";
}
var _c3=this.getDimsObj(this.posStatic);
this.windowState=_bd.ACT_RESTORE;
this._setTitleBarDragging(true,_bc.css);
this._alterCss(true,true);
if(this.posStatic&&_c0){
if(_c3!=null&&_c3.columnInfo!=null&&_c3.columnInfo.columnIndex!=null){
var _c4=_bc.page.columns[_c3.columnInfo.columnIndex];
if(_c3.columnInfo.previousSibling){
dojo.dom.insertAfter(_bf,_c3.columnInfo.previousSibling);
}else{
if(_c3.columnInfo.nextSibling){
dojo.dom.insertBefore(_bf,_c3.columnInfo.nextSibling);
}else{
_c4.domNode.appendChild(_bf);
}
}
}else{
if(_bc.page.columns!=null&&_bc.page.columns.length>0){
dojo.dom.prependChild(_bf,_bc.page.columns[0].domNode);
}
}
}
},_updtDimsObj:function(_c5){
var _c6=jetspeed;
var _c7=_c6.id;
var _c8=dojo;
var _c9=this.domNode;
var _ca=this.posStatic;
var _cb=this.getDimsObj(_ca);
if(_ca){
if(_c5){
var _cc={};
var _cd=_c8.dom.getPreviousSiblingElement(_c9);
if(_cd){
_cc.previousSibling=_cd;
}else{
_cd=_c8.dom.getNextSiblingElement(_c9);
if(_cd){
_cc.nextSibling=_cd;
}
}
_cc.columnIndex=this.getPageColumnIndex();
_cb.columnInfo=_cc;
}
}else{
}
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACT_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},_setTitleBarDragging:function(_ce,_cf,_d0){
var _d1=this.tbNode;
if(!_d1){
return;
}
if(typeof _d0=="undefined"){
_d0=this.getLayoutActionsEnabled();
}
var _d2=this.resizeHandle;
var _d3=null;
if(_d0){
_d3=this.decConfig.dragCursor;
if(_d2){
_d2.domNode.style.display="";
}
if(this.drag){
this.drag.enable();
}
}else{
_d3="default";
if(_d2){
_d2.domNode.style.display="none";
}
if(this.drag){
this.drag.disable();
}
}
this.tbNodeCss[_cf.cssCur]=_d3;
if(!_ce){
_d1.style.cursor=_d3;
}
},onMouseDown:function(evt){
this.bringToTop();
},bringToTop:function(evt,_d6){
if(!this.posStatic){
var _d7=jetspeed;
var _d8=_d7.page;
var _d9=_d7.css;
var _da=this.dNodeCss;
var _db=_d8.getPWinHighZIndex();
var _dc=_da[_d9.cssZIndex];
if(_db!=_dc){
var _dd=this._setAsTopZIndex(_d8,_d9,_da,false);
if(this.windowInitialized){
this.domNode.style.zIndex=String(_dd);
if(this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
}
}
}else{
if(_d6){
var _dd=this._setAsTopZIndex(_d8,_d9,_da,true);
if(this.windowInitialized){
this.domNode.style.zIndex=String(_dd);
}
}
}
},_setAsTopZIndex:function(_de,_df,_e0,_e1){
var _e2=_de.getPWinTopZIndex(_e1);
_e0[_df.cssZIndex]=_e2;
return _e2;
},makeUntiled:function(){
var _e3=jetspeed;
this._updtDimsObj(true);
this._makeUntiledDims();
this._setAsTopZIndex(_e3.page,_e3.css,this.dNodeCss,false);
this._alterCss(true,true);
var _e4=document.getElementById(jetspeed.id.DESKTOP);
_e4.appendChild(this.domNode);
if(this.windowState==_e3.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},_makeUntiledDims:function(){
var _e5=this.domNode;
this.posStatic=false;
var _e6=this.getDimsObj(false);
if(_e6.width==null||_e6.height==null||_e6.left==null||_e6.top==null){
var djH=dojo.html;
var _e8=djH.getAbsolutePosition(_e5,true);
var _e9=djH.getPixelValue(_e5,"margin-top",true);
var _ea=djH.getPixelValue(_e5,"margin-left",true);
var _eb=djH.getMarginBox(_e5);
_e6.width=_eb.width;
_e6.height=_eb.height;
_e6.left=_e8.x-_e9;
_e6.top=_e8.y-_ea;
}
},makeTiled:function(){
this.posStatic=true;
var _ec=jetspeed;
var _ed=this._setAsTopZIndex(_ec.page,_ec.css,this.dNodeCss,true);
this.restoreWindow();
if(this.portlet){
this.portlet.submitWinState();
}
this._removeUntiledEvents();
},_addUntiledEvents:function(){
if(this._untiledEvts==null){
this._untiledEvts=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"onMouseDown")];
}
},_removeUntiledEvents:function(){
if(this._untiledEvts!=null){
jetspeed.ui.evtDisconnectWObjAry(this._untiledEvts);
delete this._untiledEvts;
}
},makeHeightToFit:function(_ee,_ef){
var _f0=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
this._alterCss(false,true);
if(!_ee&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_f1,_f2){
var _f3=this.getDimsObj(this.posStatic);
var _f4=dojo.html.getMarginBox(this.domNode);
_f3.width=_f4.width;
_f3.height=_f4.height+3;
this.heightToFit=false;
this._alterCss(false,true);
if(!_f2&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_f1&&this.portlet){
this.portlet.submitWinState();
}
},resizeTo:function(w,h,_f7){
var _f8=this.getDimsObj(this.posStatic);
_f8.width=w;
_f8.height=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _f9=this.resizeHandle;
if(_f9!=null&&_f9._isSizing){
jetspeed.ui.evtConnect("after",_f9,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
},getDimsObj:function(_fa){
return (_fa?((this.dimsTiledTemp!=null)?this.dimsTiledTemp:this.dimsTiled):((this.dimsUntiledTemp!=null)?this.dimsUntiledTemp:this.dimsUntiled));
},_alterCss:function(_fb,_fc,_fd,_fe,_ff){
var _100=jetspeed;
var _101=_100.css;
var _102=this.iframesInfo;
var _103=(_102&&_102.layout);
var _104=(!_103?this.decConfig.layoutExtents:this.decConfig.layoutExtentsIFrame);
var _105=this.posStatic;
var _106=this.heightToFit;
var _107=(_fb||(_fd&&!_105));
var _108=(_fc||_fd);
var _109=(_fb||_fe);
var _10a=(_fc||(_fd&&_103));
var _10b=this.dNodeCss,_10c=null,_10d=null,_10e=null;
var _10f=this.getDimsObj(_105);
if(_fb){
_10b[_101.cssPos]=(_105?"static":"absolute");
}
var _110=null,_111=null;
if(_fc){
if(_103){
var _112=this.getIFrames(false);
if(_112&&_112.iframes.length==1&&_102.iframesSize!=null&&_102.iframesSize.length==1){
var _113=_102.iframesSize[0].h;
if(_113!=null){
_110=_112.iframes[0];
_111=(_106?_113:(!_100.UAie?"100%":"99%"));
_ff=false;
}
}
}
}
if(_10a){
_10c=this.cNodeCss;
var _114=_101.cssOx,_115=_101.cssOy;
if(_106&&!_103){
_10b[_115]="visible";
_10c[_115]="visible";
}else{
_10b[_115]="hidden";
_10c[_115]=(!_103?"auto":"hidden");
}
}
if(_109){
var lIdx=_101.cssL,_117=_101.cssLU;
var tIdx=_101.cssT,_119=_101.cssTU;
if(_105){
_10b[lIdx]="auto";
_10b[_117]="";
_10b[tIdx]="auto";
_10b[_119]="";
}else{
_10b[lIdx]=_10f.left;
_10b[_117]="px";
_10b[tIdx]=_10f.top;
_10b[_119]="px";
}
}
if(_108){
_10c=this.cNodeCss;
var hIdx=_101.cssH,_11b=_101.cssHU;
if(_106){
_10b[hIdx]="";
_10b[_11b]="";
_10c[hIdx]="";
_10c[_11b]="";
}else{
var h=_10f.height;
_10b[hIdx]=(h-_104.dNode.lessH);
_10b[_11b]="px";
_10c[hIdx]=(h-_104.cNode.lessH-_104.lostHeight);
_10c[_11b]="px";
}
}
if(_107){
_10c=this.cNodeCss;
_10d=this.tbNodeCss;
_10e=this.rbNodeCss;
var wIdx=_101.cssW,_11e=_101.cssWU;
if(_105){
_10b[wIdx]="";
_10b[_11e]="";
_10c[wIdx]="";
_10c[_11e]="";
if(_10d){
_10d[wIdx]="";
_10d[_11e]="";
}
if(_10e){
_10e[wIdx]="";
_10e[_11e]="";
}
}else{
var w=_10f.width;
var _120=(w-_104.lostWidth);
_10b[wIdx]=(w-_104.dNode.lessW);
_10b[_11e]="px";
_10c[wIdx]=(_120-_104.cNode.lessW);
_10c[_11e]="px";
if(_10d){
_10d[wIdx]=(_120-_104.tbNode.lessW);
_10d[_11e]="px";
}
if(_10e){
_10e[wIdx]=(_120-_104.rbNode.lessW);
_10e[_11e]="px";
}
}
}
if(!_ff){
this.domNode.style.cssText=_10b.join("");
if(_10c){
this.containerNode.style.cssText=_10c.join("");
}
if(_10d){
this.tbNode.style.cssText=_10d.join("");
}
if(_10e){
this.rbNode.style.cssText=_10e.join("");
}
}
if(_110&&_111){
this._deferSetIFrameH(_110,_111,false,50);
}
},_deferSetIFrameH:function(_121,_122,_123,_124,_125){
if(!_124){
_124=100;
}
var pWin=this;
window.setTimeout(function(){
_121.height=_122;
if(_123){
if(_125==null){
_125=50;
}
if(_125==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_125);
}
}
},_124);
},_forceRefreshZIndex:function(){
var _127=jetspeed;
var zTop=this._setAsTopZIndex(_127.page,_127.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=String(zTop);
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},_forceRefreshFromCss:function(){
this.domNode.style.cssText=this.dNodeCss.join("");
},getIFrames:function(_12a){
var _12b=this.containerNode.getElementsByTagName("iframe");
if(_12b&&_12b.length>0){
if(!_12a){
return {iframes:_12b};
}
var _12c=[];
for(var i=0;i<_12b.length;i++){
var ifrm=_12b[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_12c.push({w:w,h:h});
}
return {iframes:_12b,iframesSize:_12c};
}
return null;
},contentChanged:function(evt){
if(this.inContentChgd==false){
this.inContentChgd=true;
if(this.heightToFit){
this.makeHeightToFit(true,true);
}
this.inContentChgd=false;
}
},closeWindow:function(){
var _132=jetspeed;
var jsUI=_132.ui;
var _134=_132.page;
var _135=dojo;
var _136=_135.event;
var _137=this.actionMenuWidget;
if(_137!=null){
_137.destroy();
this.actionMenuWidget=_137=null;
}
_134.tooltipMgr.removeNodes(this.tooltips);
this.tooltips=ttps=null;
if(this.iframesInfo){
_134.unregPWinIFrameCover(this);
}
var _138=this.actionButtons;
if(_138){
var _139=(this.decConfig!=null&&this.decConfig.windowActionButtonTooltip);
for(var aNm in _138){
var aBtn=_138[aNm];
if(aBtn){
jsUI.evtDisconnect("after",aBtn,"onclick",this,"windowActionButtonClick",_136);
if(!_139){
jsUI.evtDisconnect("after",aBtn,"onmousedown",_136.browser,"stopEvent",_136);
}
}
}
this.actionButtons=_138=null;
}
if(this.drag){
this.drag.destroy(_135,_136,_132,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_136,_132,jsUI);
this.resizeHandle=null;
}
if(this.subWidgetEndIndex>this.subWidgetStartIndex){
_135.debug("closeWindow subwidgets "+this.subWidgetStartIndex+" / "+this.subWidgetEndIndex);
var _13c=_135.widget.manager;
for(var i=this.subWidgetEndIndex-1;i>=this.subWidgetStartIndex;i--){
try{
if(_13c.widgets.length>i){
var _13e=_13c.widgets[i];
if(_13e!=null){
var swT=_13e.widgetType;
var swI=_13e.widgetId;
_13e.destroy();
_135.debug("destroyed sub-widget["+i+"]: "+swT+" "+swI);
}
}
}
catch(e){
}
}
}
this._removeUntiledEvents();
var _141=this.domNode;
if(_141&&_141.parentNode){
_141.parentNode.removeChild(_141);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},dumpPos:function(){
var _142=dojo;
var djH=_142.html;
var _144=this.domNode;
var _145=this.containerNode;
var _146=djH.getAbsolutePosition(_144,true);
var _147=djH.getMarginBox(_144);
var _148=djH.getMarginBox(_145);
var _149=djH.getContentBox(_145);
var _14a=this.decConfig.layoutExtents;
var ind=jetspeed.debugindent;
_142.hostenv.println("wnd-dims ["+this.widgetId+"]  abs.x="+_146.x+"  abs.y="+_146.y+"  z="+_144.style.zIndex);
_142.hostenv.println(ind+"mb.width="+_147.width+"  mb.height="+_147.height);
_142.hostenv.println(ind+"style.width="+_144.style.width+"  style.height="+_144.style.height);
_142.hostenv.println(ind+"cnt.mb.width="+_148.width+"  cnt.mb.height="+_148.height);
_142.hostenv.println(ind+"cnt.cb.width="+_149.width+"  cnt.cb.height="+_149.height);
_142.hostenv.println(ind+"cnt.style.width="+_145.style.width+"  cnt.style.height="+_145.style.height);
_142.hostenv.println(ind+"dNodeCss="+this.dNodeCss.join(""));
_142.hostenv.println(ind+"cNodeCss="+this.cNodeCss.join(""));
_142.hostenv.println(ind+"layoutExtents: "+"dNode.lessW="+_14a.dNode.lessW+" dNode.lessH="+_14a.dNode.lessH+" lostW="+_14a.lostWidth+" lostH="+_14a.lostHeight+" cNode.lessW="+_14a.cNode.lessW+" cNode.lessH="+_14a.cNode.lessH);
_142.hostenv.println(ind+"dimsTiled="+jetspeed.printobj(this.dimsTiled));
_142.hostenv.println(ind+"dimsUntiled="+jetspeed.printobj(this.dimsUntiled));
if(this.dimsTiledTemp!=null){
_142.hostenv.println(ind+"dimsTiledTemp="+jetspeed.printobj(this.dimsTiledTemp));
}
if(this.dimsUntiledTemp!=null){
_142.hostenv.println(ind+"dimsUntiledTemp="+jetspeed.printobj(this.dimsUntiledTemp));
}
},getPageColumnIndex:function(){
return jetspeed.page.getColIndexForNode(this.domNode);
},endSizing:function(e){
jetspeed.ui.evtDisconnect("after",this.resizeHandle,"_endSizing",this,"endSizing");
this.windowIsSizing=false;
if(this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
},endDragging:function(_14d){
var _14e=this.posStatic;
if(!_14e){
if(_14d&&_14d.left!=null&&_14d.top!=null){
var _14f=this.getDimsObj(_14e);
_14f.left=_14d.left;
_14f.top=_14d.top;
this._alterCss(false,false,false,true,true);
}
}else{
this._alterCss(true);
}
if(this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(this.ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,jetspeed.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_150){
var _151=this.domNode;
var _152=this.posStatic;
if(!_151){
return null;
}
var _153=_151.style;
var _154={};
if(!_152){
_154.zIndex=_153.zIndex;
}
if(_150){
return _154;
}
_154.width=_153.width;
_154.height=_153.height;
_154[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_152;
_154[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_152){
_154.left=_153.left;
_154.top=_153.top;
}else{
var _155=jetspeed.page.getPortletCurColRow(_151);
if(_155!=null){
_154.column=_155.column;
_154.row=_155.row;
_154.layout=_155.layout;
}else{
dojo.raise("Cannot not find row/col/layout for window: "+this.widgetId);
}
}
return _154;
},getCurWinStateForPersist:function(_156){
var _157=this.getCurWinState(_156);
this._mkNumProp(null,_157,"left");
this._mkNumProp(null,_157,"top");
this._mkNumProp(null,_157,"width");
this._mkNumProp(null,_157,"height");
return _157;
},_mkNumProp:function(_158,_159,_15a){
var _15b=(_159!=null&&_15a!=null);
if(_158==null&&_15b){
_158=_159[_15a];
}
if(_158==null||_158.length==0){
_158=0;
}else{
var _15c="";
for(var i=0;i<_158.length;i++){
var _15e=_158.charAt(i);
if((_15e>="0"&&_15e<="9")||_15e=="."){
_15c+=_15e.toString();
}
}
if(_15c==null||_15c.length==0){
_15c="0";
}
if(_15b){
_159[_15a]=_15c;
}
_158=new Number(_15c);
}
return _158;
},setPortletContent:function(html,url){
var _161=jetspeed;
var _162=dojo;
var _163=html.toString();
if(!this.exclPContent){
_163="<div class=\"PContent\" >"+_163+"</div>";
}
var _164=this._splitAndFixPaths_scriptsonly(_163,url);
this.subWidgetStartIndex=_162.widget.manager.widgets.length;
this.setContent(_164,_162);
if(_164.scripts!=null&&_164.scripts.length!=null&&_164.scripts.length>0){
this._executeScripts(_164.scripts,_162);
this.onLoad();
}
if(_161.debug.setPortletContent){
_162.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
var _165=this.containerNode;
if(this.portlet){
this.portlet.postParseAnnotateHtml(_165);
}
var _166=this.iframesInfo;
var _167=this.getIFrames(true);
var _168=null,_169=false;
if(_167!=null){
if(_166==null){
this.iframesInfo=_166={};
var _16a=_165.ownerDocument.createElement("div");
var _16b="portletWindowIFrameCover";
_16a.className=_16b;
_165.appendChild(_16a);
if(_161.UAie){
_16a.className=(_16b+"IE")+" "+_16b;
_162.html.setOpacity(_16a,0.1);
}
_166.iframeCover=_16a;
_161.page.regPWinIFrameCover(this);
}
var _16c=_166.iframesSize=_167.iframesSize;
var _16d=_167.iframes;
var _16e=_166.layout;
var _16f=_166.layout=(_16d.length==1&&_16c[0].h!=null);
if(_16e!=_16f){
_169=true;
}
if(_16f){
if(!this.heightToFit){
_168=_16d[0];
}
var wDC=this.decConfig;
var _165=this.containerNode;
_165.firstChild.className="PContent portletIFramePContent";
_165.className=wDC.cNodeClass+" portletWindowIFrameClient";
if(!wDC.layoutExtentsIFrame){
this._createLayoutExtents(wDC,true,this.domNode,_165,this.tbNode,this.rbNode,_162,_161);
}
}
}else{
if(_166!=null){
if(_166.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_169=true;
}
this.iframesInfo=null;
_161.page.unregPWinIFrameCover(this);
}
}
if(_169){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(_168){
this._deferSetIFrameH(_168,(!_161.UAie?"100%":"99%"),true);
}
this.subWidgetEndIndex=_162.widget.manager.widgets.length;
},setContent:function(data,_172){
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
this._setContent(data.xml,_172);
if(this.parseContent){
var node=this.containerNode;
var _174=new _172.xml.Parse();
var frag=_174.parseElement(node,null,true);
_172.widget.getParser().createSubComponents(frag,this);
}
},_setContent:function(cont,_177){
try{
var node=this.containerNode;
while(node.firstChild){
_177.html.destroyNode(node.firstChild);
}
node.innerHTML=cont;
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
},_runStack:function(_17b){
var st=this[_17b];
var err="";
var _17e=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_17e);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_17b]=[];
if(err.length){
var name=(_17b=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_181,_182){
var self=this;
var _184=true;
var tmp="",code="";
for(var i=0;i<_181.length;i++){
if(_181[i].path){
_182.io.bind(this._cacheSetting({"url":_181[i].path,"load":function(type,_189){
dojo.lang.hitch(self,tmp=";"+_189);
},"error":function(type,_18b){
_18b.text=type+" downloading remote script";
self._handleDefaults.call(self,_18b,"onExecError","debug");
},"mimetype":"text/plain","sync":true},_184));
code+=tmp;
}else{
code+=_181[i];
}
}
try{
if(this.scriptSeparation){
}else{
var djg=_182.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_182.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
}
catch(e){
e.text="Error running scripts from content:\n"+e.description;
this._handleDefaults(e,"onExecError","debug");
}
},_cacheSetting:function(_18f,_190){
var _191=dojo.lang;
for(var x in this.bindArgs){
if(_191.isUndefined(_18f[x])){
_18f[x]=this.bindArgs[x];
}
}
if(_191.isUndefined(_18f.useCache)){
_18f.useCache=_190;
}
if(_191.isUndefined(_18f.preventCache)){
_18f.preventCache=!_190;
}
if(_191.isUndefined(_18f.mimetype)){
_18f.mimetype="text/html";
}
return _18f;
},_handleDefaults:function(e,_194,_195){
var _196=dojo;
if(!_194){
_194="onContentError";
}
if(_196.lang.isString(e)){
e={text:e};
}
if(!e.text){
e.text=e.toString();
}
e.toString=function(){
return this.text;
};
if(typeof e.returnValue!="boolean"){
e.returnValue=true;
}
if(typeof e.preventDefault!="function"){
e.preventDefault=function(){
this.returnValue=false;
};
}
this[_194](e);
if(e.returnValue){
switch(_195){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_196.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_196.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_196);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_199){
if(_199){
this.title=_199;
}else{
this.title="";
}
if(this.windowInitialized&&this.tbTextNode){
this.tbTextNode.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _19c=true;
var _19d=[];
var _19e=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _19f=/src=(['"]?)([^"']*)\1/i;
while(match=_19e.exec(s)){
if(_19c&&match[1]){
if(attr=_19f.exec(match[1])){
_19d.push({path:attr[2]});
}
}
if(match[2]){
var sc=match[2];
if(!sc){
continue;
}
if(_19c){
_19d.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_19d,"url":url};
},_IEPostDrag:function(){
if(!this.posStatic){
return;
}
var _1a1=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_1a1,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.WinScroller=function(_1a2){
if(!_1a2){
_1a2=jetspeed;
}
this.UAmoz=_1a2.UAmoz;
this.UAope=_1a2.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _1a6=null;
if(this.UAmoz){
_1a6=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_1a6=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_1a6=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_1a6=b.clientHeight;
}
}
}
}
if(_1a6!=null&&e.clientY>_1a6-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_1ac,_1ad){
return ((_1ad!=null?(_1ad+"; "):"")+this.typeNm+" "+(_1ac==null?"<unknown>":_1ac.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_1ae,_1af){
var _1b0=new jetspeed.widget.PortletWindowResizeHandle(_1ae,_1af);
var doc=document;
var _1b2=doc.createElement("div");
_1b2.className=_1b0.rhClass;
var _1b3=doc.createElement("div");
_1b2.appendChild(_1b3);
_1ae.rbNode.appendChild(_1b2);
_1b0.domNode=_1b2;
_1b0.build();
return _1b0;
};
jetspeed.widget.PortletWindowResizeHandle=function(_1b4,_1b5){
this.pWin=_1b4;
_1b5.widget.WinScroller.call(this,_1b5);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_1b6,_1b7,jsUI){
this._cleanUpLastEvt(_1b6,_1b7,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_1b6);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_1b9,_1ba,jsUI){
var _1bc=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_1b9);
this.tempEvents=null;
}
catch(ex){
_1bc=this._getErrMsg(ex,"event clean-up error",this.pWin,_1bc);
}
try{
_1ba.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_1bc=this._getErrMsg(ex,"clean-up error",this.pWin,_1bc);
}
if(_1bc!=null){
dojo.raise(_1bc);
}
},_beginSizing:function(e){
if(this._isSizing){
return false;
}
var pWin=this.pWin;
var node=pWin.domNode;
if(!node){
return false;
}
this.targetDomNode=node;
var _1c0=jetspeed;
var jsUI=_1c0.ui;
var _1c2=dojo;
var _1c3=_1c2.event;
var _1c4=_1c0.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_1c3,_1c0,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_1c2.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _1c7=[];
_1c7.push(jsUI.evtConnect("after",_1c4,"onmousemove",this,"_changeSizing",_1c3,25));
_1c7.push(jsUI.evtConnect("after",_1c4,"onmouseup",this,"_endSizing",_1c3));
_1c7.push(jsUI.evtConnect("after",d,"ondragstart",_1c3.browser,"stopEvent",_1c3));
_1c7.push(jsUI.evtConnect("after",d,"onselectstart",_1c3.browser,"stopEvent",_1c3));
_1c0.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_1c7;
e.preventDefault();
},_changeSizing:function(e){
var pWin=this.pWin;
if(pWin.heightToFit){
pWin.makeHeightVariable(true,true);
}
try{
if(!e.pageX||!e.pageY){
return;
}
}
catch(ex){
return;
}
this.autoScroll(e);
var dx=this.startPoint.x-e.pageX;
var dy=this.startPoint.y-e.pageY;
var newW=this.startSize.w-dx;
var newH=this.startSize.h-dy;
var _1ce=pWin.posStatic;
if(_1ce){
newW=this.startSize.w;
}
if(this.minSize){
var mb=dojo.html.getMarginBox(this.targetDomNode);
if(newW<this.minSize.w){
newW=mb.width;
}
if(newH<this.minSize.h){
newH=mb.height;
}
}
pWin.resizeTo(newW,newH);
e.preventDefault();
},_endSizing:function(e){
var _1d1=jetspeed;
this._cleanUpLastEvt(dojo.event,_1d1,_1d1.ui);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_1d2,_1d3,_1d4,_1d5,e,_1d7,_1d8){
var jsUI=_1d8.ui;
var _1da=_1d7.event;
_1d8.widget.WinScroller.call(this,_1d8);
this.moveInitiated=false;
this.moveableObj=_1d5;
this.windowOrLayoutWidget=_1d2;
this.node=_1d3;
this.posStatic=_1d2.posStatic;
if(e.ctrlKey&&this.posStatic){
this.changeToUntiled=true;
}
this.posRecord={};
this.disqualifiedColumnIndexes=null;
if(_1d4!=null){
this.disqualifiedColumnIndexes=_1d4.getDescendantCols();
}
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _1dc=[];
var _1dd=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_1da);
_1dc.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_1da));
_1dc.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_1da));
_1dc.push(jsUI.evtConnect("after",doc,"ondragstart",_1da.browser,"stopEvent",_1da));
_1dc.push(jsUI.evtConnect("after",doc,"onselectstart",_1da.browser,"stopEvent",_1da));
_1d8.page.displayAllPWinIFrameCovers(false);
_1dc.push(_1dd);
this.events=_1dc;
this.isDebug=false;
if(_1d8.debug.dragWindow){
this.isDebug=true;
this.devInit=false;
this.devLastX=null;
this.devLastY=null;
this.devLastTime=null;
this.devChgTh=30;
this.devLrgTh=200;
this.devChgSubsqTh=10;
this.devTimeTh=6000;
this.devI=_1d8.debugindent;
this.devIH=_1d8.debugindentH;
this.devI3=_1d8.debugindent3;
this.devICH=_1d8.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",onMouseMove:function(e){
var _1df=jetspeed;
var _1e0=dojo;
var _1e1=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _1e3=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _1e6=false;
var _1e7=null,_1e8=null,_1e9,_1ea,_1eb,_1ec;
if(this.isDebug){
_1e9=this.devI;
_1ea=this.devIH;
_1eb=this.devI3;
_1ec=this.devICH;
if(!this.devInit){
var _1ed="";
if(this.disqualifiedColumnIndexes!=null){
_1ed=_1ea+"dqCols=["+this.disqualifiedColumnIndexes.split(", ")+"]";
}
var _1ee=this.windowOrLayoutWidget.title;
if(_1ee==null){
_1ee=this.windowOrLayoutWidget.widgetId;
}
_1e0.hostenv.println("DRAG \""+this.windowOrLayoutWidget.title+"\""+_1ea+"m.l = "+m.l+_1ea+"m.t = "+m.t+_1ed);
this.devInit=true;
}
_1e7=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _1ef=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_1ef&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_1e7)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_1e6=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_1e6=true;
}
}
}
}
if(_1e1&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_1e3=true;
}
_1e0.setMarginBox(this.node,x,y,null,null,_1e0.gcs(this.node),_1df);
var _1f0=this.posRecord;
_1f0.left=x;
_1f0.top=y;
var _1f1=_1df.widget.pwGhost;
if(this.posStatic&&!_1e3){
var _1f2=-1;
var _1f3=this.widthHalf;
var _1f4=this.heightHalf;
var _1f5=_1f4+(_1f4*0.2);
var _1f6=_1df.page.columns.length;
var _1f7=[];
var _1f8=e.pageX;
var _1f9=y+_1f4;
for(var i=0;i<_1f6;i++){
var _1fb=this.columnDimensions[i];
if(_1fb!=null){
if(_1f8>=_1fb.left&&_1f8<=_1fb.right){
if(_1f9>=(_1fb.top-30)){
_1f7.push(i);
var _1fc=Math.min(Math.abs(_1f9-(_1fb.top)),Math.abs(e.pageY-(_1fb.top)));
var _1fd=Math.min(Math.abs(_1f9-(_1fb.yhalf)),Math.abs(e.pageY-(_1fb.yhalf)));
var lowY=Math.min(_1fc,_1fd);
_1f7.push(lowY);
}else{
if(_1e6){
if(_1e8==null){
_1e8=[];
}
var _1ff=(_1fb.top-30)-_1f9;
_1e8.push(_1eb+_1e0.string.padRight(String(i),2,_1ec)+" y! "+_1e0.string.padRight(String(_1ff),4,_1ec)+_1ea+"t="+_1fb.top+_1ea+"b="+_1fb.bottom+_1ea+"l="+_1fb.left+_1ea+"r="+_1fb.right);
}
}
}else{
if(_1e6&&_1f8>_1fb.width){
if(_1e8==null){
_1e8=[];
}
var _1ff=_1f8-_1fb.width;
_1e8.push(_1eb+_1e0.string.padRight(String(i),2,_1ec)+" x! "+_1e0.string.padRight(String(_1ff),4,_1ec)+_1ea+"t="+_1fb.top+_1ea+"b="+_1fb.bottom+_1ea+"l="+_1fb.left+_1ea+"r="+_1fb.right);
}
}
}
}
var _200=_1f7.length;
if(_200>0){
var _201=-1;
var _202=0;
var i=1;
while(i<_200){
if(_201==-1||_202>_1f7[i]){
_201=_1f7[i-1];
_202=_1f7[i];
}
i=i+2;
}
_1f2=_201;
}
var col=(_1f2>=0?_1df.page.columns[_1f2]:null);
if(_1e6){
_1e0.hostenv.println(_1e9+"x="+x+_1ea+"y="+y+_1ea+"col="+_1f2+_1ea+"xTest="+_1f8+_1ea+"yTest="+_1f9);
var i=0;
while(i<_200){
var colI=_1f7[i];
var _1fb=this.columnDimensions[colI];
_1e0.hostenv.println(_1eb+_1e0.string.padRight(String(colI),2,_1ec)+" -> "+_1e0.string.padRight(String(_1f7[i+1]),4,_1ec)+_1ea+"t="+_1fb.top+_1ea+"b="+_1fb.bottom+_1ea+"l="+_1fb.left+_1ea+"r="+_1fb.right);
i=i+2;
}
if(_1e8!=null){
for(i=0;i<_1e8.length;i++){
_1e0.hostenv.println(_1e8[i]);
}
}
this.devLastTime=_1e7;
this.devChgTh=this.devChgSubsqTh;
}
if(_1f1.col!=col&&col!=null){
_1e0.dom.removeNode(_1f1);
_1f1.col=col;
col.domNode.appendChild(_1f1);
}
var _205=null,_206=null;
if(col!=null){
_205=_1df.ui.getPWinChildren(col.domNode,_1f1);
_206=_205.portletWindowNodes;
}
if(_206!=null&&_206.length>1){
var _207=_205.matchIndex;
var _208=-1;
var _209=-1;
if(_207>0){
var _208=_1e0.html.getAbsolutePosition(_206[_207-1],true).y;
if((y-25)<=_208){
_1e0.dom.removeNode(_1f1);
_1e0.dom.insertBefore(_1f1,_206[_207-1],true);
}
}
if(_207!=(_206.length-1)){
var _209=_1e0.html.getAbsolutePosition(_206[_207+1],true).y;
if((y+10)>=_209){
if(_207+2<_206.length){
_1e0.dom.insertBefore(_1f1,_206[_207+2],true);
}else{
col.domNode.appendChild(_1f1);
}
}
}
}
}
},onFirstMove:function(){
var _20a=jetspeed;
var _20b=dojo;
var _20c=this.windowOrLayoutWidget;
var node=this.node;
var _20e=dojo.gcs(node);
var mP=_20b.getMarginBox(node,_20e,_20a);
this.marginBoxPrev=mP;
this.staticWidth=null;
var _210=_20a.widget.pwGhost;
var _211=this.UAmoz;
var _212=this.changeToUntiled;
var m=null;
if(this.posStatic){
m={w:mP.w,h:mP.h};
var _214=node.parentNode;
var _215=document.getElementById(_20a.id.DESKTOP);
var _216=node.style;
this.staticWidth=_216.width;
var _217=_20b.html.getAbsolutePosition(node,true);
var _218=_20b._getMarginExtents(node,_20e,_20a);
m.l=_217.left-_218.l;
m.t=_217.top-_218.t;
if(_211&&!_212){
_20b.setMarginBox(_210,null,null,null,mP.h,null,_20a);
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_216.position="absolute";
_216.zIndex=_20a.page.getPWinHighZIndex()+1;
if(!_212){
_214.insertBefore(_210,node);
if(!_211){
_20b.setMarginBox(_210,null,null,null,mP.h,null,_20a);
}
_215.appendChild(node);
var _219=_20a.ui.getPWinChildren(_214,_210);
this.prevColumnNode=_214;
this.prevIndexInCol=_219.matchIndex;
}else{
_20c._updtDimsObj(true);
_215.appendChild(node);
}
}else{
m=_20b.getMarginBox(node,_20e,_20a);
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
_20a.ui.evtDisconnectWObj(this.events.pop(),_20b.event);
if(this.posStatic){
_20b.setMarginBox(node,m.l,m.t,mP.w,null,null,_20a);
this.widthHalf=mP.w/2;
this.heightHalf=mP.h/2;
if(!_212){
var _21a=_20c.getPageColumnIndex();
this.columnDimensions=new Array(_20a.page.columns.length);
for(var i=0;i<_20a.page.columns.length;i++){
var col=_20a.page.columns[i];
if(!col.columnContainer&&!col.layoutHeader){
if(this.qualifyTargetColumn(col)){
var _21d=_20b.html.getAbsolutePosition(col.domNode,true);
var _21e=_20b.html.getMarginBox(col.domNode);
var _21f={left:(_21d.x),right:(_21d.x+_21e.width),top:(_21d.y),bottom:(_21d.y+_21e.height)};
_21f.height=_21f.bottom-_21f.top;
_21f.width=_21f.right-_21f.left;
_21f.yhalf=_21f.top+(_21f.height/2);
this.columnDimensions[i]=_21f;
}
}
}
var _220=(_21a>=0?_20a.page.columns[_21a]:null);
_210.col=_220;
}else{
_20c._makeUntiledDims();
this.posStatic=false;
}
}
},qualifyTargetColumn:function(_221){
if(_221!=null&&!_221.layoutActionsDisabled){
if(this.disqualifiedColumnIndexes!=null&&this.disqualifiedColumnIndexes[_221.getPageColumnIndex()]!=null){
return false;
}
return true;
}
return false;
},mouseUpDestroy:function(){
var _222=dojo;
var _223=jetspeed;
this.destroy(_222,_222.event,_223,_223.ui);
},destroy:function(_224,_225,_226,jsUI){
var _228=this.windowOrLayoutWidget;
var _229=null;
if(this.moveInitiated){
try{
var _22a=_226.widget.pwGhost;
if(this.posStatic){
var n=this.node;
var _22c=n.style;
if(_22a&&_22a.col){
_228.column=0;
_224.dom.insertBefore(n,_22a,true);
}else{
_224.dom.insertAtIndex(n,this.prevColumnNode,this.prevIndexInCol);
}
if(_22a){
_224.dom.removeNode(_22a);
}
}
_228.endDragging(this.posRecord);
}
catch(ex){
_229=this._getErrMsg(ex,"destroy reset-window error",_228,_229);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_225);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_229=this._getErrMsg(ex,"destroy event clean-up error",_228,_229);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_226.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_229=this._getErrMsg(ex,"destroy clean-up error",_228,_229);
}
if(_229!=null){
_224.raise(_229);
}
}});
dojo.dnd.Moveable=function(_22d,opt){
var jsUI=jetspeed.ui;
var _230=dojo.event;
this.enabled=true;
this.mover=null;
this.windowOrLayoutWidget=_22d;
this.handle=opt.handle;
this.minMove=20;
var _231=[];
_231.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_230));
_231.push(jsUI.evtConnect("after",this.handle,"ondragstart",_230.browser,"stopEvent",_230));
_231.push(jsUI.evtConnect("after",this.handle,"onselectstart",_230.browser,"stopEvent",_230));
this.events=_231;
};
dojo.extend(dojo.dnd.Moveable,{onMouseDown:function(e){
if(e&&e.button==2){
return;
}
if(this.mover!=null||this.tempEvents!=null){
var _233=dojo;
var _234=_233.event;
var _235=jetspeed;
this._cleanUpLastEvt(_233,_234,_235,_235.ui);
_234.browser.stopEvent(e);
}else{
if(this.enabled){
var jsUI=jetspeed.ui;
var _234=dojo.event;
var _237=[];
_237.push(jsUI.evtConnect("after",this.handle,"onmousemove",this,"onMouseMove",_234));
_237.push(jsUI.evtConnect("after",this.handle,"onmouseup",this,"onMouseUp",_234));
_237.push(jsUI.evtConnect("after",this.handle,"onmouseout",this,"onMouseOut",_234));
this.tempEvents=_237;
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
},onMouseOut:function(e){
this.onMouseMove(e,true);
},onMouseMove:function(e,_23a){
var _23b=dojo;
var _23c=_23b.event;
if(_23a||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
var _23d=jetspeed;
this._cleanUpLastEvt(_23b,_23c,_23d,_23d.ui);
var _23e=null;
var _23f=this.windowOrLayoutWidget;
var _240=null;
this.beforeDragColumnRowInfo=null;
if(!_23f.isLayoutPane){
_23e=_23f.domNode;
}else{
_240=_23f.containingColumn;
if(_240!=null){
_23e=_240.domNode;
if(_23e!=null){
this.beforeDragColumnRowInfo=_23d.page.getPortletCurColRow(_23e);
}
}
}
if(_23e!=null){
this.node=_23e;
this.mover=new _23b.dnd.Mover(_23f,_23e,_240,this,e,_23b,_23d);
}
}
_23c.browser.stopEvent(e);
},onMouseUp:function(e){
var _242=dojo;
var _243=jetspeed;
this._cleanUpLastEvt(_242,_242.event,_243,_243.ui);
},_cleanUpLastEvt:function(_244,_245,_246,jsUI){
if(this._mDownEvt!=null){
_245.browser.stopEvent(this._mDownEvt);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_244,_245,_246,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_245);
this.tempEvents=null;
},destroy:function(_248,_249,_24a,jsUI){
this._cleanUpLastEvt(_248,_249,_24a,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_249);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColumnRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_24d,_24e){
var s=_24d||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_24e);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_24e.UAmoz){
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
if(_24e.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_259,_25a){
var s=_259||gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_25a.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_261,_262,_263,_264,_265,_266){
var s=_265||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_266);
if(_263!=null&&_263>=0){
_263=Math.max(_263-pb.w-mb.w,0);
}
if(_264!=null&&_264>=0){
_264=Math.max(_264-pb.h-mb.h,0);
}
dojo._setBox(node,_261,_262,_263,_264);
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
dojo._getPadExtents=function(n,_274){
var s=_274||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_27a){
var s=_27a||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_27f,_280){
var s=_27f||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_280.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_288){
var ne="none",px=dojo._toPixelValue,s=_288||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
return {l:bl,t:bt,w:bl+(s.borderRightStyle!=ne?px(n,s.borderRightWidth):0),h:bt+(s.borderBottomStyle!=ne?px(n,s.borderBottomWidth):0)};
};
if(!jetspeed.UAie){
var dv=document.defaultView;
dojo.getComputedStyle=((jetspeed.UAsaf)?function(node){
var s=dv.getComputedStyle(node,null);
if(!s&&node.style){
node.style.display="";
s=dv.getComputedStyle(node,null);
}
return s||{};
}:function(node){
return dv.getComputedStyle(node,null);
});
dojo._toPixelValue=function(_291,_292){
return (parseFloat(_292)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_294,_295){
if(!_295){
return 0;
}
if(_295.slice&&(_295.slice(-2)=="px")){
return parseFloat(_295);
}
with(_294){
var _296=style.left;
var _297=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_295;
_295=style.pixelLeft;
}
catch(e){
_295=0;
}
style.left=_296;
runtimeStyle.left=_297;
}
return _295;
};
}
dojo.gcs=dojo.getComputedStyle;

