dojo.provide("jetspeed.widget.PortletWindow");
dojo.require("jetspeed.desktop.core");
jetspeed.widget.PortletWindow=function(){
this.windowInitialized=false;
this.actionButtons={};
this.actionMenuWidget=null;
this.tooltips=[];
this._onLoadStack=[];
this._onUnloadStack=[];
this._callOnUnload=false;
};
dojo.extend(jetspeed.widget.PortletWindow,{title:"",nextIndex:1,resizable:true,moveable:true,moveAllowTilingChg:true,decName:null,decConfig:null,posStatic:false,heightToFit:false,titleMouseIn:0,titleLit:false,colWidth_pbE:0,portlet:null,altInitParams:null,inContentChgd:false,exclPContent:false,minimizeTempRestore:null,executeScripts:false,scriptSeparation:false,adjustPaths:false,parseContent:true,childWidgets:null,dbProfile:(djConfig.isDebug&&jetspeed.debug.profile),dbOn:djConfig.isDebug,dbMenuDims:"Dump Dimensions",altInitParamsDef:function(_1,_2){
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
if(_c.maximizedOnInit!=null){
_23[_d.cssNoSelNm]=" visibility: ";
_23[_d.cssNoSel]="hidden";
_23[_d.cssNoSelEnd]=";";
}
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
var _2d={w:null};
if(_27!=null&&_27>0){
_2c.w=_27=Math.floor(_27);
}else{
_2c.w=_27=_b.windowWidth;
}
if(_28!=null&&_28>0){
_2c.h=_2d.h=_28=Math.floor(_28);
}else{
_2c.h=_2d.h=_28=_b.windowHeight;
}
if(_29!=null&&_29>=0){
_2c.l=Math.floor(_29);
}else{
if(!_25){
_2c.l=(((_12-2)*30)+200);
}
}
if(_2a!=null&&_2a>=0){
_2c.t=Math.floor(_2a);
}else{
if(!_25){
_2c.t=(((_12-2)*30)+170);
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
var _32=_c.tooltipMgr;
if(wDC.windowActionButtonTooltip){
if(this.actionLabels[_a.ACT_DESKTOP_MOVE_TILED]!=null&&this.actionLabels[_a.ACT_DESKTOP_MOVE_UNTILED]!=null){
this.tooltips.push(_32.addNode(_1e,null,true,1200,this,"getTitleBarTooltip",_9,_e,_31));
}
}
var _33=(_15)?wDC.windowActionButtonNames:wDC.windowActionButtonNamesNp;
if(_33==null){
_33=this._buildActionStructures(wDC,_15,_10,_9,_a,_b,_11);
}
var aNm;
for(var i=0;i<_33.length;i++){
aNm=_33[i];
if(aNm!=null){
if(!_15||(aNm==_a.ACT_RESTORE||aNm==_a.ACT_MENU||_15.getAction(aNm)!=null||_b.windowActionDesktop[aNm]!=null)){
this._createActionButtonNode(aNm,_f,_10,_32,wDC,_9,_b,_e,_11,_31);
}
}
}
this.actionMenuWidget=(_15)?wDC.windowActionMenuWidget:wDC.windowActionMenuWidgetNp;
if(this.actionMenuWidget&&wDC.windowActionMenuHasNoImg){
_e.evtConnect("after",_1e,"oncontextmenu",this,"actionMenuOpen",_31);
}
this.actionBtnSync(_9,_a);
if(wDC.windowDisableResize){
this.resizable=false;
}
if(wDC.windowDisableMove){
this.moveable=false;
}
}
var _36=this.resizable;
var _37=null;
if(_36&&_1f){
var _38=_17+"_resize";
var _37=_9.widget.CreatePortletWindowResizeHandler(this,_9);
this.resizeHandle=_37;
if(_37){
_1f.appendChild(_37.domNode);
}
}else{
this.resizable=false;
}
_10.removeChild(_1c);
var _39=false;
var _3a=_8.childNodes;
if(_25&&_3a){
var _3b=iP[_a.PP_ROW];
if(_3b!=null){
var _3c=new Number(_3b);
if(_3c>=0){
var _3d=_3a.length-1;
if(_3d>=_3c){
var _3e=_3a[_3c];
if(_3e){
_8.insertBefore(_1c,_3e);
_39=true;
}
}
}
}
}
if(!_39){
_8.appendChild(_1c);
}
if(!wDC.layout){
var _3f="display: block; visibility: hidden; width: "+_27+"px"+((_28!=null&&_28>0)?("; height: "+_28+"px"):"");
_1c.style.cssText=_3f;
this._createLayoutInfo(wDC,false,_1c,_1d,_1e,_1f,_11,_9,_e);
}
if(this.moveable&&_1e){
this.drag=new _11.dnd.Moveable(this,{handle:_1e});
this._setTitleBarDragging(true,_d);
}
if(ie6&&_25){
_2d.w=Math.max(0,_8.offsetWidth-this.colWidth_pbE);
}
this._setAsTopZIndex(_c,_d,_23,_25);
this._alterCss(true,true);
if(!_25){
this._addUntiledEvents();
}
if(ie6){
this.bgIframe=new _9.widget.BackgroundIframe(_1c,null,_11);
}
this.windowInitialized=true;
if(_9.debug.createWindow){
_11.debug("createdWindow ["+(_15?_15.entityId:_17)+(_15?(" / "+_17):"")+"]"+" width="+_1c.style.width+" height="+_1c.style.height+" left="+_1c.style.left+" top="+_1c.style.top);
}
this.windowState=_a.ACT_RESTORE;
var iWS=null;
if(_15){
iWS=_15.getCurrentActionState();
}else{
iWS=iP[_a.PP_WINDOW_STATE];
}
if(iWS==_a.ACT_MINIMIZE){
this.minimizeOnNextRender=true;
}
if(_9.widget.pwGhost==null&&_c!=null){
var _41=_f.createElement("div");
_41.id="pwGhost";
var _42=_c.getPortletDecorationDefault();
if(!_42){
_42=_18;
}
_41.className=_1a;
_41.style.position="static";
_41.style.width="";
_41.style.left="auto";
_41.style.top="auto";
_9.widget.pwGhost=_41;
}
if(ie6&&_9.widget.ie6ZappedContentHelper==null){
var _43=_f.createElement("span");
_43.id="ie6ZappedContentHelper";
_9.widget.ie6ZappedContentHelper=_43;
}
},_buildActionStructures:function(wDC,_45,_46,_47,_48,_49,_4a){
var _4b=new Array();
var aNm,_4d,_4e=false;
var _4f=new Array();
var _50=new Object();
if(wDC.windowActionButtonOrder!=null){
if(_45){
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_4b.push(aNm);
_50[aNm]=true;
}
}else{
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_4d=false;
if(aNm==_48.ACT_MINIMIZE||aNm==_48.ACT_MAXIMIZE||aNm==_48.ACT_RESTORE||aNm==_48.ACT_MENU||_49.windowActionDesktop[aNm]!=null){
_4d=true;
}
if(_4d){
_4b.push(aNm);
_50[aNm]=true;
}
}
}
var _52=(wDC.windowActionButtonMax==null?-1:wDC.windowActionButtonMax);
if(_52!=-1&&_4b.length>=_52){
var _53=0;
var _54=_4b.length-_52+1;
for(var i=0;i<_4b.length&&_53<_54;i++){
aNm=_4b[i];
if(aNm!=_48.ACT_MENU){
_4f.push(aNm);
_4b[i]=null;
delete _50[aNm];
_53++;
}
}
}
if(wDC.windowActionNoImage){
for(var i=0;i<_4b.length;i++){
aNm=_4b[i];
if(wDC.windowActionNoImage[aNm]!=null){
if(aNm==_48.ACT_MENU){
_4e=true;
}else{
_4f.push(aNm);
}
_4b[i]=null;
delete _50[aNm];
}
}
}
}
if(wDC.windowActionMenuOrder){
if(_45){
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
_4f.push(aNm);
}
}else{
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
if(_49.windowActionDesktop[aNm]!=null){
_4f.push(aNm);
}
}
}
}
var _56=new Array();
if(_4f.length>0||this.dbOn){
var _57=new Object();
for(var i=0;i<_4f.length;i++){
aNm=_4f[i];
if(aNm!=null&&_57[aNm]==null&&_50[aNm]==null){
_56.push(aNm);
_57[aNm]=true;
}
}
if(this.dbOn){
_56.push({aNm:this.dbMenuDims,dev:true});
}
}
var _58=null;
if(_56.length>0){
var _59={};
var aNm,_5a,_5b,_5c;
_58=_4a.widget.createWidget("PopupMenu2",{id:this.widgetId+"_ctxmenu",contextMenuForWindow:false},null);
_58.onItemClick=function(mi){
var _aN=mi.jsActNm;
var _5f=this.pWin;
if(!mi.jsActDev){
_5f.actionProcess(_aN);
}else{
_5f.actionProcessDev(_aN);
}
};
for(var i=0;i<_56.length;i++){
aNm=_56[i];
_5c=false;
if(!aNm.dev){
_5a=this.actionLabels[aNm];
}else{
_5c=true;
_5a=aNm=aNm.aNm;
}
_5b=_4a.widget.createWidget("MenuItem2",{caption:_5a,jsActNm:aNm,jsActDev:_5c});
_59[aNm]=_5b;
_58.addChild(_5b);
}
_58.menuItemsByName=_59;
_46.appendChild(_58.domNode);
_47.ui.addPopupMenuWidget(_58);
}
wDC.windowActionMenuHasNoImg=_4e;
if(_45){
wDC.windowActionButtonNames=_4b;
wDC.windowActionMenuNames=_56;
wDC.windowActionMenuWidget=_58;
}else{
wDC.windowActionButtonNamesNp=_4b;
wDC.windowActionMenuNamesNp=_56;
wDC.windowActionMenuWidgetNp=_58;
}
return _4b;
},_createActionButtonNode:function(aNm,doc,_62,_63,wDC,_65,_66,_67,_68,_69){
if(aNm!=null){
var _6a=doc.createElement("div");
_6a.className="portletWindowActionButton";
_6a.style.backgroundImage="url("+_66.getPortletDecorationBaseUrl(this.decName)+"/images/desktop/"+aNm+".gif)";
_6a.actionName=aNm;
this.actionButtons[aNm]=_6a;
this.tbNode.appendChild(_6a);
_67.evtConnect("after",_6a,"onclick",this,"actionBtnClick",_69);
if(wDC.windowActionButtonTooltip){
var _6b=this.actionLabels[aNm];
this.tooltips.push(_63.addNode(_6a,_6b,true,null,null,null,_65,_67,_69));
}else{
_67.evtConnect("after",_6a,"onmousedown",_65,"_stopEvent",_69);
}
}
},getTitleBarTooltip:function(){
if(!this.getLayoutActionsEnabled()){
return null;
}
if(this.posStatic){
return this.actionLabels[jetspeed.id.ACT_DESKTOP_MOVE_TILED];
}else{
return this.actionLabels[jetspeed.id.ACT_DESKTOP_MOVE_UNTILED];
}
},_createLayoutInfo:function(_6c,_6d,_6e,_6f,_70,_71,_72,_73,_74){
var _75=_72.gcs(_6e);
var _76=_72.gcs(_6f);
var _77=_74.getLayoutExtents(_6e,_75,_72,_73);
var _78=_74.getLayoutExtents(_6f,_76,_72,_73);
var _79={dNode:_77,cNode:_78};
var _7a=Math.max(0,_78.mE.t);
var _7b=Math.max(0,_78.mE.h-_78.mE.t);
var _7c=0;
var _7d=0;
var _7e=null;
if(_70){
var _7f=_72.gcs(_70);
_7e=_74.getLayoutExtents(_70,_7f,_72,_73);
var _80=_7f.cursor;
if(_80==null||_80.length==0){
_80="move";
}
_6c.dragCursor=_80;
_7e.mBh=_72.getMarginBox(_70,_7f,_73).h;
var _81=Math.max(0,_7e.mE.h-_7e.mE.t);
_7c=(_7e.mBh-_81)+Math.max(0,(_81-_7a));
_79.tbNode=_7e;
}
var _82=null;
if(_71){
var _83=_72.gcs(_71);
_82=_74.getLayoutExtents(_71,_83,_72,_73);
_82.mBh=_72.getMarginBox(_71,_83,_73).h;
var _84=Math.max(0,_82.mE.t);
_7d=(_82.mBh-_84)+Math.max(0,(_84-_7b));
_79.rbNode=_82;
}
_79.cNode_mBh_LessBars=_7c+_7d;
if(!_6d){
_6c.layout=_79;
}else{
_6c.layoutIFrame=_79;
}
},actionBtnClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.actionProcess(evt.target.actionName,evt);
},actionMenuOpen:function(evt){
var _87=jetspeed;
var _88=_87.id;
var _89=this.actionMenuWidget;
if(!_89){
return;
}
if(_89.isShowingNow){
_89.close();
}
var _8a=null;
var _8b=null;
if(this.portlet){
_8a=this.portlet.getCurrentActionState();
_8b=this.portlet.getCurrentActionMode();
}
var _8c=_89.menuItemsByName;
var _8d,_8e;
for(var aNm in _8c){
_8d=_8c[aNm];
_8e=(this._isActionEnabled(aNm,_8a,_8b,_87,_88))?"":"none";
_8d.domNode.style.display=_8e;
}
_89.pWin=this;
_89.onOpen(evt);
},actionProcessDev:function(aNm,evt){
if(aNm==this.dbMenuDims&&jetspeed.debugPWinPos){
jetspeed.debugPWinPos(this);
}
},actionProcess:function(aNm,evt){
var _94=jetspeed;
var _95=_94.id;
if(aNm==null){
return;
}
if(_94.prefs.windowActionDesktop[aNm]!=null){
if(aNm==_95.ACT_DESKTOP_TILE){
this.makeTiled();
}else{
if(aNm==_95.ACT_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(aNm==_95.ACT_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(aNm==_95.ACT_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false,false);
}
}
}
}
}else{
if(aNm==_95.ACT_MENU){
this.actionMenuOpen(evt);
}else{
if(aNm==_95.ACT_MINIMIZE){
if(this.portlet&&this.windowState==_95.ACT_MAXIMIZE){
this.needsRenderOnRestore=true;
}
this.minimizeWindow();
if(this.portlet){
_94.changeActionForPortlet(this.portlet.getId(),_95.ACT_MINIMIZE,null);
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_95.ACT_RESTORE){
var _96=false;
if(this.portlet){
if(this.windowState==_95.ACT_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_96=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
if(this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.portlet.renderAction(aNm);
}else{
_94.changeActionForPortlet(this.portlet.getId(),_95.ACT_RESTORE,null);
}
}
if(!_96){
this.restoreWindow();
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_95.ACT_MAXIMIZE){
if(this.portlet&&this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(aNm);
}else{
this.actionBtnSync(_94,_95);
}
}else{
if(aNm==_95.ACT_REMOVEPORTLET){
if(this.portlet){
var _97=dojo.widget.byId(_95.PG_ED_WID);
if(_97!=null){
_97.deletePortlet(this.portlet.entityId,this.title);
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
},_isActionEnabled:function(aNm,_99,_9a,_9b,_9c){
var _9b=jetspeed;
var _9c=_9b.id;
var _9d=false;
var _9e=this.windowState;
if(this.minimizeTempRestore!=null){
if(this.portlet){
var _9f=this.portlet.getAction(aNm);
if(_9f!=null){
if(_9f.id==_9c.ACT_REMOVEPORTLET){
if(_9b.page.editMode&&this.getLayoutActionsEnabled()){
_9d=true;
}
}
}
}
}else{
if(aNm==_9c.ACT_MENU){
if(!this._actionMenuIsEmpty(_9b,_9c)){
_9d=true;
}
}else{
if(_9b.prefs.windowActionDesktop[aNm]!=null){
if(this.getLayoutActionsEnabled()){
var _a0=(this.ie6&&_9e==_9c.ACT_MINIMIZE);
if(aNm==_9c.ACT_DESKTOP_HEIGHT_EXPAND){
if(!this.heightToFit&&!_a0){
_9d=true;
}
}else{
if(aNm==_9c.ACT_DESKTOP_HEIGHT_NORMAL){
if(this.heightToFit&&!_a0){
_9d=true;
}
}else{
if(aNm==_9c.ACT_DESKTOP_TILE&&_9b.prefs.windowTiling){
if(!this.posStatic){
_9d=true;
}
}else{
if(aNm==_9c.ACT_DESKTOP_UNTILE){
if(this.posStatic){
_9d=true;
}
}
}
}
}
}
}else{
if(this.portlet){
var _9f=this.portlet.getAction(aNm);
if(_9f!=null){
if(_9f.id==_9c.ACT_REMOVEPORTLET){
if(_9b.page.editMode&&this.getLayoutActionsEnabled()){
_9d=true;
}
}else{
if(_9f.type==_9c.PORTLET_ACTION_TYPE_MODE){
if(aNm!=_9a){
_9d=true;
}
}else{
if(aNm!=_99){
_9d=true;
}
}
}
}else{
_9d=true;
}
}else{
if(aNm==_9c.ACT_MAXIMIZE){
if(aNm!=_9e&&this.minimizeTempRestore==null){
_9d=true;
}
}else{
if(aNm==_9c.ACT_MINIMIZE){
if(aNm!=_9e){
_9d=true;
}
}else{
if(aNm==_9c.ACT_RESTORE){
if(_9e==_9c.ACT_MAXIMIZE||_9e==_9c.ACT_MINIMIZE){
_9d=true;
}
}else{
_9d=true;
}
}
}
}
}
}
}
return _9d;
},_actionMenuIsEmpty:function(_a1,_a2){
var _a3=true;
var _a4=this.actionMenuWidget;
if(_a4){
var _a5=null;
var _a6=null;
if(this.portlet){
_a5=this.portlet.getCurrentActionState();
_a6=this.portlet.getCurrentActionMode();
}
for(var aNm in _a4.menuItemsByName){
if(aNm!=_a2.ACT_MENU&&this._isActionEnabled(aNm,_a5,_a6,_a1,_a2)){
_a3=false;
break;
}
}
}
return _a3;
},actionBtnSyncDefer:function(){
dojo.lang.setTimeout(this,this.actionBtnSync,10);
},actionBtnSync:function(_a8,_a9){
if(!_a8){
_a8=jetspeed;
_a9=_a8.id;
}
var _aa=this.decConfig.windowActionButtonHide;
var _ab=null;
var _ac=null;
if(this.portlet){
_ab=this.portlet.getCurrentActionState();
_ac=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionButtons){
var _ae=false;
if(!_aa||this.titleLit){
_ae=this._isActionEnabled(aNm,_ab,_ac,_a8,_a9);
}
var _af=this.actionButtons[aNm];
_af.style.display=(_ae)?"":"none";
}
},_postCreateMaximizeWindow:function(){
var _b0=jetspeed;
var _b1=_b0.id;
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(_b1.ACT_MAXIMIZE);
}else{
this.actionBtnSync(_b0,_b1);
}
},minimizeWindowTemporarily:function(){
var _b2=jetspeed;
var _b3=_b2.id;
if(this.minimizeTempRestore==null){
this.minimizeTempRestore=this.windowState;
if(this.windowState!=_b3.ACT_MINIMIZE){
this.minimizeWindow(false);
}
this.actionBtnSync(_b2,_b3);
}
},restoreFromMinimizeWindowTemporarily:function(){
var _b4=jetspeed;
var _b5=_b4.id;
var _b6=this.minimizeTempRestore;
this.minimizeTempRestore=null;
if(_b6){
if(_b6!=_b5.ACT_MINIMIZE){
this.restoreWindow();
}
this.actionBtnSync(_b4,_b5);
}
},minimizeWindow:function(_b7){
if(!this.tbNode){
return;
}
var _b8=jetspeed;
if(this.windowState==jetspeed.id.ACT_MAXIMIZE){
_b8.widget.showAllPortletWindows();
this.restoreWindow();
}else{
if(!_b7){
this._updtDimsObj(false,false);
}
}
var _b9=_b8.css.cssDis;
this.cNodeCss[_b9]="none";
if(this.rbNodeCss){
this.rbNodeCss[_b9]="none";
}
this.windowState=_b8.id.ACT_MINIMIZE;
if(this.ie6&&!this.posStatic&&this.heightToFit){
if(!this.ie6){
this.containerNode.style.display="none";
}
}
if(this.ie6){
dojo.lang.setTimeout(this,this._ie6DeferredMinimize,100);
}
},_ie6DeferredMinimize:function(){
this._alterCss(true,true);
if(!this.posStatic&&this.heightToFit){
this.containerNode.style.display="none";
}
},maximizeWindow:function(){
var _ba=jetspeed;
var _bb=_ba.id;
var _bc=this.domNode;
var _bd=[this.widgetId];
_ba.widget.hideAllPortletWindows(_bd);
if(this.windowState==_bb.ACT_MINIMIZE){
this.restoreWindow();
}
var _be=this.posStatic;
this.preMaxPosStatic=_be;
this.preMaxHeightToFit=this.heightToFit;
var _bf=_be;
this._updtDimsObj(false,_bf);
var _c0=document.getElementById(_bb.DESKTOP);
var _c1=dojo.html.getAbsolutePosition(_c0,true).y;
var _c2=dojo.html.getViewport();
var _c3=dojo.html.getPadding(_ba.docBody);
this.dimsUntiledTemp={w:_c2.width-_c3.width-2,h:_c2.height-_c3.height-_c1,l:1,t:_c1};
this._setTitleBarDragging(true,_ba.css,false);
this.posStatic=false;
this.heightToFit=false;
this._alterCss(true,true);
if(_be){
_c0.appendChild(_bc);
}
this.windowState=_bb.ACT_MAXIMIZE;
},restoreWindow:function(){
var _c4=jetspeed;
var _c5=_c4.id;
var _c6=_c4.css;
var _c7=this.domNode;
var _c8=false;
if(_c7.style.position=="absolute"){
_c8=true;
}
var _c9=null;
if(this.windowState==_c5.ACT_MAXIMIZE){
_c4.widget.showAllPortletWindows();
this.posStatic=this.preMaxPosStatic;
this.heightToFit=this.preMaxHeightToFit;
this.dimsUntiledTemp=null;
}
var _ca=_c6.cssDis;
this.cNodeCss[_ca]="block";
if(this.rbNodeCss){
this.rbNodeCss[_ca]="block";
}
this.windowState=_c5.ACT_RESTORE;
this._setTitleBarDragging(true,_c4.css);
var ie6=this.ie6;
if(!ie6){
this._alterCss(true,true);
}else{
var _cc=null;
if(this.heightToFit){
_cc=this.iNodeCss;
this.iNodeCss=null;
}
this._alterCss(true,true);
this._updtDimsObj(false,false,true,false,true);
if(_cc!=null){
this.iNodeCss=_cc;
}
this._alterCss(false,false,true);
}
if(this.posStatic&&_c8){
this._tileWindow(_c4);
}
},_tileWindow:function(_cd){
if(!this.posStatic){
return;
}
var _ce=this.domNode;
var _cf=this.getDimsObj(this.posStatic);
var _d0=true;
if(_cf!=null){
var _d1=_cf.colInfo;
if(_d1!=null&&_d1.colI!=null){
var _d2=_cd.page.columns[_d1.colI];
var _d3=((_d2!=null)?_d2.domNode:null);
if(_d3!=null){
var _d4=null;
var _d5=_d3.childNodes.length;
if(_d5==0){
_d3.appendChild(_ce);
_d0=false;
}else{
var _d6,_d7,_d8=0;
if(_d1.pSibId!=null||_d1.nSibId!=null){
_d6=_d3.firstChild;
do{
_d7=_d6.id;
if(_d7==null){
continue;
}
if(_d7==_d1.pSibId){
dojo.dom.insertAfter(_ce,_d6);
_d0=false;
}else{
if(_d7==_d1.nSibId){
dojo.dom.insertBefore(_ce,_d6);
_d0=false;
}else{
if(_d8==_d1.elmtI){
_d4=_d6;
}
}
}
_d6=_d6.nextSibling;
_d8++;
}while(_d0&&_d6!=null);
}
}
if(_d0){
if(_d4!=null){
dojo.dom.insertBefore(_ce,_d4);
}else{
dojo.dom.prependChild(_ce,_d3);
}
_d0=false;
}
}
}
}
if(_d0){
var _d9=_cd.page.getColumnDefault();
if(_d9!=null){
dojo.dom.prependChild(_ce,_d9.domNode);
}
}
},getDimsObj:function(_da,_db){
return (_da?((this.dimsTiledTemp!=null&&!_db)?this.dimsTiledTemp:this.dimsTiled):((this.dimsUntiledTemp!=null&&!_db)?this.dimsUntiledTemp:this.dimsUntiled));
},_updtDimsObj:function(_dc,_dd,_de,_df,_e0,_e1){
var _e2=jetspeed;
var _e3=dojo;
var _e4=this.domNode;
var _e5=this.posStatic;
var _e6=this.getDimsObj(_e5,_e1);
var _e7=(!_de&&!_e5&&(!_dc||_e6.l==null||_e6.t==null));
var _e8=(!_df&&(!_dc||_e7||_e0||_e6.w==null||_e6.h==null));
if(_e8||_e7){
var _e9=this._getLayoutInfo().dNode;
if(_e8){
var _ea=_e2.ui.getMarginBoxSize(_e4,_e9);
_e6.w=_ea.w;
_e6.h=_ea.h;
if(!_e5){
_e7=true;
}
}
if(_e7){
var _eb=_e3.html.getAbsolutePosition(_e4,true);
_e6.l=_eb.x-_e9.mE.l-_e9.pbE.l;
_e6.t=_eb.y-_e9.mE.t-_e9.pbE.t;
}
}
if(_e5){
if(_dd||_e1&&_e6.colInfo==null){
var _ec=0,_ed=_e4.previousSibling,_ee=_e4.nextSibling;
var _ef=(_ed!=null?_ed.id:null),_f0=(_ee!=null?_ee.id:null);
if(_ed!=null){
_ef=_ed.id;
}
while(_ed!=null){
_ec++;
_ed=_ed.previousSibling;
}
_e6.colInfo={elmtI:_ec,pSibId:_ef,nSibId:_f0,colI:this.getPageColumnIndex()};
}
if(_e1){
this.dimsTiledTemp={w:_e6.w,h:_e6.h,colInfo:_e6.colInfo};
_e6=this.dimsTiledTemp;
}
}else{
if(_e1){
this.dimsUntiledTemp={w:_e6.w,h:_e6.h,l:_e6.l,t:_e6.t};
_e6=this.dimsUntiledTemp;
}
}
return _e6;
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACT_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},_setTitleBarDragging:function(_f1,_f2,_f3){
var _f4=this.tbNode;
if(!_f4){
return;
}
if(typeof _f3=="undefined"){
_f3=this.getLayoutActionsEnabled();
}
var _f5=this.resizeHandle;
var _f6=null;
if(_f3){
_f6=this.decConfig.dragCursor;
if(_f5){
_f5.domNode.style.display="";
}
if(this.drag){
this.drag.enable();
}
}else{
_f6="default";
if(_f5){
_f5.domNode.style.display="none";
}
if(this.drag){
this.drag.disable();
}
}
this.tbNodeCss[_f2.cssCur]=_f6;
if(!_f1){
_f4.style.cursor=_f6;
}
},onMouseDown:function(evt){
this.bringToTop(evt,false,false,jetspeed);
},bringToTop:function(evt,_f9,_fa,_fb){
if(!this.posStatic){
var _fc=_fb.page;
var _fd=_fb.css;
var _fe=this.dNodeCss;
var _ff=_fc.getPWinHighZIndex();
var zCur=_fe[_fd.cssZIndex];
if(_ff!=zCur){
var zTop=this._setAsTopZIndex(_fc,_fd,_fe,false);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
if(!_fa&&this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
}
}
}else{
if(_f9){
var zTop=this._setAsTopZIndex(_fc,_fd,_fe,true);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
}
}
}
},_setAsTopZIndex:function(_102,_103,_104,_105){
var zTop=String(_102.getPWinTopZIndex(_105));
_104[_103.cssZIndex]=zTop;
return zTop;
},makeUntiled:function(){
var _107=jetspeed;
this._updtDimsObj(false,true);
this.posStatic=false;
this._updtDimsObj(true,false);
this._setAsTopZIndex(_107.page,_107.css,this.dNodeCss,false);
this._alterCss(true,true);
var _108=document.getElementById(jetspeed.id.DESKTOP);
_108.appendChild(this.domNode);
if(this.windowState==_107.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},makeTiled:function(){
this.posStatic=true;
var _109=jetspeed;
this._setAsTopZIndex(_109.page,_109.css,this.dNodeCss,true);
this._alterCss(true,true);
this._tileWindow(_109);
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
},makeHeightToFit:function(_10a){
var _10b=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
if(this.ie6){
var _10c=this.iNodeCss;
this.iNodeCss=null;
this._alterCss(false,true);
this._updtDimsObj(false,false,true,false,true);
this.iNodeCss=_10c;
}
this._alterCss(false,true);
if(!_10a&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_10d,_10e){
var _10f=this.getDimsObj(this.posStatic);
var _110=this._getLayoutInfo().dNode;
var _111=jetspeed.ui.getMarginBoxSize(this.domNode,_110);
_10f.w=_111.w;
_10f.h=_111.h;
this.heightToFit=false;
this._alterCss(false,true);
if(!_10e&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_10d&&this.portlet){
this.portlet.submitWinState();
}
},resizeTo:function(w,h,_114){
var _115=this.getDimsObj(this.posStatic);
_115.w=w;
_115.h=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _116=this.resizeHandle;
if(_116!=null&&_116._isSizing){
jetspeed.ui.evtConnect("after",_116,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
this.resizeNotifyChildWidgets();
},resizeNotifyChildWidgets:function(){
if(this.childWidgets){
var _117=this.childWidgets;
var _118=_117.length,_119;
for(var i=0;i<_118;i++){
try{
_119=_117[i];
if(_119){
_119.checkSize();
}
}
catch(e){
}
}
}
},_getLayoutInfo:function(){
var _11b=this.iframesInfo;
return ((!(_11b&&_11b.layout))?this.decConfig.layout:this.decConfig.layoutIFrame);
},_getLayoutInfoMoveable:function(){
return this._getLayoutInfo().dNode;
},onBrowserWindowResize:function(){
if(this.ie6){
this._resetIE6TiledSize(false);
}
},_resetIE6TiledSize:function(_11c){
var _11d=this.posStatic;
if(_11d){
var _11e=this.domNode;
var _11f=this.getDimsObj(_11d);
_11f.w=Math.max(0,this.domNode.parentNode.offsetWidth-this.colWidth_pbE);
this._alterCss(_11c,false,false,false,true);
}
},_alterCss:function(_120,_121,_122,_123,_124,_125){
var _126=jetspeed;
var _127=_126.css;
var _128=this.iframesInfo;
var _129=(_128&&_128.layout);
var _12a=(!_129?this.decConfig.layout:this.decConfig.layoutIFrame);
var _12b=this.dNodeCss,_12c=null,_12d=null,_12e=null,_12f=false,_130=this.iNodeCss,_131=null;
if(_130&&_129){
_131=_128.iframeCoverIE6Css;
}
var _132=this.posStatic;
var _133=(_132&&_130==null);
var _134=this.heightToFit;
var _135=(_120||_124||(_122&&!_133));
var _136=(_121||_122);
var _137=(_120||_123);
var _138=(_121||(_122&&_129));
var _139=this.getDimsObj(_132);
if(_120){
_12b[_127.cssPos]=(_132?"relative":"absolute");
}
var _13a=null,_13b=null;
if(_121){
if(_129){
var _13c=this.getIFrames(false);
if(_13c&&_13c.iframes.length==1&&_128.iframesSize!=null&&_128.iframesSize.length==1){
var _13d=_128.iframesSize[0].h;
if(_13d!=null){
_13a=_13c.iframes[0];
_13b=(_134?_13d:(!_126.UAie?"100%":"99%"));
_125=false;
}
}
}
}
if(_138){
_12c=this.cNodeCss;
var _13e=_127.cssOx,_13f=_127.cssOy;
if(_134&&!_129){
_12b[_13f]="visible";
_12c[_13f]="visible";
}else{
_12b[_13f]="hidden";
_12c[_13f]=(!_129?"auto":"hidden");
}
}
if(_137){
var lIdx=_127.cssL,_141=_127.cssLU;
var tIdx=_127.cssT,_143=_127.cssTU;
if(_132){
_12b[lIdx]="auto";
_12b[_141]="";
_12b[tIdx]="auto";
_12b[_143]="";
}else{
_12b[lIdx]=_139.l;
_12b[_141]="px";
_12b[tIdx]=_139.t;
_12b[_143]="px";
}
}
if(_136){
_12c=this.cNodeCss;
var hIdx=_127.cssH,_145=_127.cssHU;
if(_134&&_130==null){
_12b[hIdx]="";
_12b[_145]="";
_12c[hIdx]="";
_12c[_145]="";
}else{
var h=_139.h;
var _147=_126.css.cssDis;
var _148;
var _149;
if(_12c[_147]=="none"){
_148=_12a.tbNode.mBh;
_149="";
_12c[_145]="";
}else{
_148=(h-_12a.dNode.lessH);
_149=_148-_12a.cNode.lessH-_12a.cNode_mBh_LessBars;
_12c[_145]="px";
}
_12b[hIdx]=_148;
_12b[_145]="px";
_12c[hIdx]=_149;
if(_130){
_130[hIdx]=_148;
_130[_145]="px";
_12f=true;
if(_131){
_131[hIdx]=_149;
_131[_145]=_12c[_145];
}
}
}
}
if(_135){
var w=_139.w;
_12c=this.cNodeCss;
_12d=this.tbNodeCss;
_12e=this.rbNodeCss;
var wIdx=_127.cssW,_14c=_127.cssWU;
if(_133&&(!this.ie6||!w)){
_12b[wIdx]="";
_12b[_14c]="";
_12c[wIdx]="";
_12c[_14c]="";
if(_12d){
_12d[wIdx]="";
_12d[_14c]="";
}
if(_12e){
_12e[wIdx]="";
_12e[_14c]="";
}
}else{
var _14d=(w-_12a.dNode.lessW);
_12b[wIdx]=_14d;
_12b[_14c]="px";
_12c[wIdx]=_14d-_12a.cNode.lessW;
_12c[_14c]="px";
if(_12d){
_12d[wIdx]=_14d-_12a.tbNode.lessW;
_12d[_14c]="px";
}
if(_12e){
_12e[wIdx]=_14d-_12a.rbNode.lessW;
_12e[_14c]="px";
}
if(_130){
_130[wIdx]=_14d;
_130[_14c]="px";
_12f=true;
if(_131){
_131[wIdx]=_12c[wIdx];
_131[_14c]=_12c[_14c];
}
}
}
}
if(!_125){
this.domNode.style.cssText=_12b.join("");
if(_12c){
this.containerNode.style.cssText=_12c.join("");
}
if(_12d){
this.tbNode.style.cssText=_12d.join("");
}
if(_12e){
this.rbNode.style.cssText=_12e.join("");
}
if(_12f){
this.bgIframe.iframe.style.cssText=_130.join("");
if(_131){
_128.iframeCover.style.cssText=_131.join("");
}
}
}
if(_13a&&_13b){
this._deferSetIFrameH(_13a,_13b,false,50);
}
},_deferSetIFrameH:function(_14e,_14f,_150,_151,_152){
if(!_151){
_151=100;
}
var pWin=this;
window.setTimeout(function(){
_14e.height=_14f;
if(_150){
if(_152==null){
_152=50;
}
if(_152==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_152);
}
}
},_151);
},_getWindowMarginBox:function(_154,_155){
var _156=this.domNode;
if(_154==null){
_154=this._getLayoutInfo().dNode;
}
var _157=null;
if(_155.UAope){
_157=(this.posStatic?_155.page.layoutInfo.column:_155.page.layoutInfo.desktop);
}
return _155.ui.getMarginBox(_156,_154,_157,_155);
},_forceRefreshZIndex:function(){
var _158=jetspeed;
var zTop=this._setAsTopZIndex(_158.page,_158.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=zTop;
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},getIFrames:function(_15b){
var _15c=this.containerNode.getElementsByTagName("iframe");
if(_15c&&_15c.length>0){
if(!_15b){
return {iframes:_15c};
}
var _15d=[];
for(var i=0;i<_15c.length;i++){
var ifrm=_15c[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_15d.push({w:w,h:h});
}
return {iframes:_15c,iframesSize:_15d};
}
return null;
},contentChanged:function(evt){
if(this.inContentChgd==false){
this.inContentChgd=true;
if(this.heightToFit){
this.makeHeightToFit(true);
}
this.inContentChgd=false;
}
},closeWindow:function(){
var _163=jetspeed;
var jsUI=_163.ui;
var _165=_163.page;
var _166=dojo;
var _167=_166.event;
var wDC=this.decConfig;
if(this.actionMenuWidget&&wDC&&wDC.windowActionMenuHasNoImg){
jsUI.evtDisconnect("after",this.tbNode,"oncontextmenu",this,"actionMenuOpen",_167);
}
_165.tooltipMgr.removeNodes(this.tooltips);
this.tooltips=ttps=null;
if(this.iframesInfo){
_165.unregPWinIFrameCover(this);
}
var _169=this.actionButtons;
if(_169){
var _16a=(wDC&&wDC.windowActionButtonTooltip);
for(var aNm in _169){
var aBtn=_169[aNm];
if(aBtn){
jsUI.evtDisconnect("after",aBtn,"onclick",this,"actionBtnClick",_167);
if(!_16a){
jsUI.evtDisconnect("after",aBtn,"onmousedown",_163,"_stopEvent",_167);
}
}
}
this.actionButtons=_169=null;
}
if(this.drag){
this.drag.destroy(_166,_167,_163,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_167,_163,jsUI);
this.resizeHandle=null;
}
this._destroyChildWidgets(_166);
this._removeUntiledEvents();
var _16d=this.domNode;
if(_16d&&_16d.parentNode){
_16d.parentNode.removeChild(_16d);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},_destroyChildWidgets:function(_16e){
if(this.childWidgets){
var _16f=this.childWidgets;
var _170=_16f.length,_171,swT,swI;
_16e.debug("PortletWindow ["+this.widgetId+"] destroy child widgets ("+_170+")");
for(var i=(_170-1);i>=0;i--){
try{
_171=_16f[i];
if(_171){
swT=_171.widgetType;
swI=_171.widgetId;
_171.destroy();
_16e.debug("destroyed child widget["+i+"]: "+swT+" "+swI);
}
_16f[i]=null;
}
catch(e){
}
}
this.childWidgets=null;
}
},getPageColumnIndex:function(){
return jetspeed.page.getColIndexForNode(this.domNode);
},endSizing:function(e){
jetspeed.ui.evtDisconnect("after",this.resizeHandle,"_endSizing",this,"endSizing");
this.windowIsSizing=false;
if(this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
},endDragging:function(_176,_177,_178){
var _179=jetspeed;
var ie6=this.ie6;
if(_177){
this.posStatic=false;
}else{
if(_178){
this.posStatic=true;
}
}
var _17b=this.posStatic;
if(!_17b){
var _17c=this.getDimsObj(_17b);
if(_176&&_176.left!=null&&_176.top!=null){
_17c.l=_176.left;
_17c.t=_176.top;
if(!_177){
this._alterCss(false,false,false,true,false,true);
}
}
if(_177){
this._updtDimsObj(false,false,true);
this._alterCss(true,true,false,true);
this._addUntiledEvents();
}
}else{
if(_178){
this._setAsTopZIndex(_179.page,_179.css,this.dNodeCss,_17b);
this._updtDimsObj(false,false);
}
if(!ie6){
this._alterCss(true);
this.resizeNotifyChildWidgets();
}else{
this._resetIE6TiledSize(_178);
}
}
if(this.portlet&&this.windowState!=_179.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,_179.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_17d){
var _17e=this.domNode;
var _17f=this.posStatic;
if(!_17e){
return null;
}
var _180=_17e.style;
var _181={};
if(!_17f){
_181.zIndex=_180.zIndex;
}
if(_17d){
return _181;
}
var _182=this.getDimsObj(_17f);
_181.width=(_182.w?String(_182.w):"");
_181.height=(_182.h?String(_182.h):"");
_181[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_17f;
_181[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_17f){
_181.left=(_182.l!=null?String(_182.l):"");
_181.top=(_182.t!=null?String(_182.t):"");
}else{
var _183=jetspeed.page.getPortletCurColRow(_17e);
if(_183!=null){
_181.column=_183.column;
_181.row=_183.row;
_181.layout=_183.layout;
}else{
throw new Error("Can't find row/col/layout for window: "+this.widgetId);
}
}
return _181;
},getCurWinStateForPersist:function(_184){
var _185=this.getCurWinState(_184);
this._mkNumProp(null,_185,"left");
this._mkNumProp(null,_185,"top");
this._mkNumProp(null,_185,"width");
this._mkNumProp(null,_185,"height");
return _185;
},_mkNumProp:function(_186,_187,_188){
var _189=(_187!=null&&_188!=null);
if(_186==null&&_189){
_186=_187[_188];
}
if(_186==null||_186.length==0){
_186=0;
}else{
var _18a="";
for(var i=0;i<_186.length;i++){
var _18c=_186.charAt(i);
if((_18c>="0"&&_18c<="9")||_18c=="."){
_18a+=_18c.toString();
}
}
if(_18a==null||_18a.length==0){
_18a="0";
}
if(_189){
_187[_188]=_18a;
}
_186=new Number(_18a);
}
return _186;
},setPortletContent:function(html,url){
var _18f=jetspeed;
var _190=dojo;
var ie6=this.ie6;
var _192=null;
if(ie6){
_192=this.iNodeCss;
if(this.heightToFit){
this.iNodeCss=null;
this._alterCss(false,true);
}
}
var _193=html.toString();
if(!this.exclPContent){
_193="<div class=\"PContent\" >"+_193+"</div>";
}
var _194=this._splitAndFixPaths_scriptsonly(_193,url);
var _195=this.setContent(_194,_190);
this.childWidgets=((_195&&_195.length>0)?_195:null);
if(_194.scripts!=null&&_194.scripts.length!=null&&_194.scripts.length>0){
this._executeScripts(_194.scripts,_190);
this.onLoad();
}
if(_18f.debug.setPortletContent){
_190.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
var _196=this.containerNode;
if(this.portlet){
this.portlet.postParseAnnotateHtml(_196);
}
var _197=this.iframesInfo;
var _198=this.getIFrames(true);
var _199=null,_19a=false;
if(_198!=null){
if(_197==null){
this.iframesInfo=_197={};
var _19b=_196.ownerDocument.createElement("div");
var _19c="portletWindowIFrameCover";
_19b.className=_19c;
_196.appendChild(_19b);
if(_18f.UAie){
_19b.className=(_19c+"IE")+" "+_19c;
if(ie6){
_197.iframeCoverIE6Css=_18f.css.cssWidthHeight.concat();
}
}
_197.iframeCover=_19b;
_18f.page.regPWinIFrameCover(this);
}
var _19d=_197.iframesSize=_198.iframesSize;
var _19e=_198.iframes;
var _19f=_197.layout;
var _1a0=_197.layout=(_19e.length==1&&_19d[0].h!=null);
if(_19f!=_1a0){
_19a=true;
}
if(_1a0){
if(!this.heightToFit){
_199=_19e[0];
}
var wDC=this.decConfig;
var _196=this.containerNode;
_196.firstChild.className="PContent portletIFramePContent";
_196.className=wDC.cNodeClass+" portletWindowIFrameClient";
if(!wDC.layoutIFrame){
this._createLayoutInfo(wDC,true,this.domNode,_196,this.tbNode,this.rbNode,_190,_18f,_18f.ui);
}
}
}else{
if(_197!=null){
if(_197.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_19a=true;
}
this.iframesInfo=null;
_18f.page.unregPWinIFrameCover(this);
}
}
if(_19a){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(ie6){
this._updtDimsObj(false,false,true,false,true);
if(_192==null){
var _1a2=_18f.css;
_192=_1a2.cssHeight.concat();
_192[_1a2.cssDis]="inline";
}
this.iNodeCss=_192;
this._alterCss(false,false,true);
}
if(this.minimizeOnNextRender){
this.minimizeOnNextRender=false;
this.minimizeWindow(true);
this.actionBtnSync(_18f,_18f.id);
this.needsRenderOnRestore=true;
}
if(_199){
this._deferSetIFrameH(_199,(!_18f.UAie?"100%":"99%"),true);
}
},setContent:function(data,_1a4){
var _1a5=null;
var step=1;
try{
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
step=2;
this._setContent(data.xml,_1a4);
step=3;
if(this.parseContent){
var node=this.containerNode;
var _1a8=new _1a4.xml.Parse();
var frag=_1a8.parseElement(node,null,true);
_1a5=_1a4.widget.getParser().createSubComponents(frag,null);
}
}
catch(e){
dojo.hostenv.println("ERROR in PortletWindow ["+this.widgetId+"] setContent while "+(step==1?"running onUnload":(step==2?"setting innerHTML":"creating dojo widgets"))+" - "+jetspeed.formatError(e));
}
return _1a5;
},_setContent:function(cont,_1ab){
this._destroyChildWidgets(_1ab);
try{
var node=this.containerNode;
while(node.firstChild){
_1ab.html.destroyNode(node.firstChild);
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
},_runStack:function(_1af){
var st=this[_1af];
var err="";
var _1b2=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_1b2);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_1af]=[];
if(err.length){
var name=(_1af=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_1b5,_1b6){
var self=this;
var _1b8=true;
var tmp="",code="";
for(var i=0;i<_1b5.length;i++){
if(_1b5[i].path){
_1b6.io.bind(this._cacheSetting({"url":_1b5[i].path,"load":function(type,_1bd){
dojo.lang.hitch(self,tmp=";"+_1bd);
},"error":function(type,_1bf){
_1bf.text=type+" downloading remote script";
self._handleDefaults.call(self,_1bf,"onExecError","debug");
},"mimetype":"text/plain","sync":true},_1b8));
code+=tmp;
}else{
code+=_1b5[i];
}
}
try{
if(this.scriptSeparation){
}else{
var djg=_1b6.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_1b6.doc();
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
},_cacheSetting:function(_1c3,_1c4){
var _1c5=dojo.lang;
for(var x in this.bindArgs){
if(_1c5.isUndefined(_1c3[x])){
_1c3[x]=this.bindArgs[x];
}
}
if(_1c5.isUndefined(_1c3.useCache)){
_1c3.useCache=_1c4;
}
if(_1c5.isUndefined(_1c3.preventCache)){
_1c3.preventCache=!_1c4;
}
if(_1c5.isUndefined(_1c3.mimetype)){
_1c3.mimetype="text/html";
}
return _1c3;
},_handleDefaults:function(e,_1c8,_1c9){
var _1ca=dojo;
if(!_1c8){
_1c8="onContentError";
}
if(_1ca.lang.isString(e)){
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
this[_1c8](e);
if(e.returnValue){
switch(_1c9){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_1ca.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_1ca.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_1ca);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_1cd){
if(_1cd){
this.title=_1cd;
}else{
this.title="";
}
if(this.windowInitialized&&this.tbTextNode){
this.tbTextNode.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _1d0=true;
var _1d1=[];
var _1d2=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _1d3=/src=(['"]?)([^"']*)\1/i;
while(match=_1d2.exec(s)){
if(_1d0&&match[1]){
if(attr=_1d3.exec(match[1])){
_1d1.push({path:attr[2]});
}
}
if(match[2]){
var sc=match[2];
if(!sc){
continue;
}
if(_1d0){
_1d1.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_1d1,"url":url};
},_IEPostDrag:function(){
if(!this.posStatic){
return;
}
var _1d5=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_1d5,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.showAllPortletWindows=function(){
var _1d6=jetspeed;
var _1d7=_1d6.css;
var _1d8=_1d7.cssDis,_1d9=_1d7.cssNoSelNm,_1da=_1d7.cssNoSel,_1db=_1d7.cssNoSelEnd;
var _1dc=_1d6.page.getPWins(false);
var _1dd,_1de;
for(var i=0;i<_1dc.length;i++){
_1dd=_1dc[i];
if(_1dd){
_1de=_1dd.dNodeCss;
_1de[_1d9]="";
_1de[_1da]="";
_1de[_1db]="";
_1de[_1d8]="block";
_1dd.domNode.style.display="block";
_1dd.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.hideAllPortletWindows=function(_1e0){
var _1e1=jetspeed;
var _1e2=_1e1.css;
var _1e3=_1e2.cssDis,_1e4=_1e2.cssNoSelNm,_1e5=_1e2.cssNoSel,_1e6=_1e2.cssNoSelEnd;
var _1e7=_1e1.page.getPWins(false);
var _1e8,_1e9,_1ea;
for(var i=0;i<_1e7.length;i++){
_1e9=_1e7[i];
_1e8=true;
if(_1e9&&_1e0&&_1e0.length>0){
for(var _1ec=0;_1ec<_1e0.length;_1ec++){
if(_1e9.widgetId==_1e0[_1ec]){
_1e8=false;
break;
}
}
}
if(_1e9){
_1ea=_1e9.dNodeCss;
_1ea[_1e4]="";
_1ea[_1e5]="";
_1ea[_1e6]="";
if(_1e8){
_1ea[_1e3]="none";
_1e9.domNode.style.display="none";
}else{
_1ea[_1e3]="block";
_1e9.domNode.style.display="block";
}
_1e9.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.WinScroller=function(){
var _1ed=this.jsObj;
this.UAmoz=_1ed.UAmoz;
this.UAope=_1ed.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{jsObj:jetspeed,djObj:dojo,typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _1f1=null;
if(this.UAmoz){
_1f1=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_1f1=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_1f1=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_1f1=b.clientHeight;
}
}
}
}
if(_1f1!=null&&e.clientY>_1f1-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_1f7,_1f8){
return ((_1f8!=null?(_1f8+"; "):"")+this.typeNm+" "+(_1f7==null?"<unknown>":_1f7.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_1f9,_1fa){
var _1fb=new jetspeed.widget.PortletWindowResizeHandle(_1f9,_1fa);
var doc=document;
var _1fd=doc.createElement("div");
_1fd.className=_1fb.rhClass;
var _1fe=doc.createElement("div");
_1fd.appendChild(_1fe);
_1f9.rbNode.appendChild(_1fd);
_1fb.domNode=_1fd;
_1fb.build();
return _1fb;
};
jetspeed.widget.PortletWindowResizeHandle=function(_1ff,_200){
this.pWin=_1ff;
_200.widget.WinScroller.call(this);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_201,_202,jsUI){
this._cleanUpLastEvt(_201,_202,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_201);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_204,_205,jsUI){
var _207=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_204);
this.tempEvents=null;
}
catch(ex){
_207=this._getErrMsg(ex,"event clean-up error",this.pWin,_207);
}
try{
_205.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_207=this._getErrMsg(ex,"clean-up error",this.pWin,_207);
}
if(_207!=null){
dojo.raise(_207);
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
var _20b=jetspeed;
var jsUI=_20b.ui;
var _20d=dojo;
var _20e=_20d.event;
var _20f=_20b.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_20e,_20b,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_20d.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _212=[];
_212.push(jsUI.evtConnect("after",_20f,"onmousemove",this,"_changeSizing",_20e,25));
_212.push(jsUI.evtConnect("after",_20f,"onmouseup",this,"_endSizing",_20e));
_212.push(jsUI.evtConnect("after",d,"ondragstart",_20b,"_stopEvent",_20e));
_212.push(jsUI.evtConnect("after",d,"onselectstart",_20b,"_stopEvent",_20e));
_20b.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_212;
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
var _219=pWin.posStatic;
if(_219){
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
var _21c=jetspeed;
this._cleanUpLastEvt(dojo.event,_21c,_21c.ui);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
jetspeed.widget.BackgroundIframe=function(node,_21e,_21f){
if(!_21e){
_21e=this.defaultStyleClass;
}
var html="<iframe src='' frameborder='0' scrolling='no' class='"+_21e+"'>";
this.iframe=_21f.doc().createElement(html);
this.iframe.tabIndex=-1;
node.appendChild(this.iframe);
};
dojo.lang.extend(jetspeed.widget.BackgroundIframe,{defaultStyleClass:"ie6BackgroundIFrame",iframe:null});
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_221,_222,_223,_224,e,_226,_227,_228){
var jsUI=_228.ui;
var _22a=_227.event;
_228.widget.WinScroller.call(this);
if(_228.widget._movingInProgress){
if(djConfig.isDebug){
_228.debugAlert("ERROR - Mover initiation before previous Mover was destroyed");
}
}
_228.widget._movingInProgress=true;
this.moveInitiated=false;
this.moveableObj=_224;
this.windowOrLayoutWidget=_221;
this.node=_222;
this.nodeLayoutColumn=_223;
this.posStatic=_221.posStatic;
this.notifyOnAbsolute=_226;
if(e.ctrlKey&&_221.moveAllowTilingChg){
if(this.posStatic){
this.changeToUntiled=true;
}else{
if(_228.prefs.windowTiling){
this.changeToTiled=true;
this.changeToTiledStarted=false;
}
}
}
this.posRecord={};
this.disqualifiedColumnIndexes=(_223!=null)?_223.getDescendantCols():{};
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _22c=[];
var _22d=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_22a);
_22c.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_22a));
_22c.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_22a));
_22c.push(jsUI.evtConnect("after",doc,"ondragstart",_228,"_stopEvent",_22a));
_22c.push(jsUI.evtConnect("after",doc,"onselectstart",_228,"_stopEvent",_22a));
if(_228.UAie6){
_22c.push(jsUI.evtConnect("after",doc,"onmousedown",_228,"mouseUpDestroy",_22a));
}
_228.page.displayAllPWinIFrameCovers(false);
_22c.push(_22d);
this.events=_22c;
this.pSLastColChgIdx=null;
this.pSLastColChgTime=null;
this.pSLastNaturalColChgYTest=null;
this.pSLastNaturalColChgHistory=null;
this.pSLastNaturalColChgChoiceMap=null;
this.isDebug=false;
if(_228.debug.dragWindow){
this.isDebug=true;
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
this.devLastX=null;
this.devLastY=null;
this.devLastTime=null,this.devLastColI=null;
this.devChgTh=30;
this.devLrgTh=200;
this.devChgSubsqTh=10;
this.devTimeTh=6000;
this.devI=_228.debugindent;
this.devIH=_228.debugindentH;
this.devIT=_228.debugindentT;
this.devI3=_228.debugindent3;
this.devICH=_228.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",pSColChgTimeTh:3000,onMouseMove:function(e){
var _22f=this.jsObj;
var _230=this.djObj;
var _231=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _233=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _236=this.isDebug;
var _237=false;
var _238=null,_239=null,_23a,_23b,_23c,_23d,_23e;
if(_236){
_23a=this.devI;
_23b=this.devIH;
_23c=this.devI3;
_23d=this.devICH,_23e=this.devIT;
_238=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _23f=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_23f&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_238)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_237=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_237=true;
}
}
}
}
if(_231&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_233=true;
}
_22f.ui.setMarginBox(this.node,x,y,null,null,this.nodeLayoutInfo,_22f,_230);
var _240=this.posRecord;
_240.left=x;
_240.top=y;
var _241=false;
var _242=this.posStatic;
if(!_242){
if(!_233&&this.changeToTiled&&!this.changeToTiledStarted){
_241=true;
_242=true;
}
}
if(_242&&!_233){
var _243=this.columnInfoArray;
var _244=_22f.page.columns;
var _245=this.heightHalf;
var _246=_244.length;
var _247=e.pageX;
var _248=y+_245;
var _249=this.pSLastColChgIdx;
var _24a=this.pSLastNaturalColChgChoiceMap;
var _24b=null,_24c=[],_24d=null;
var _24e,_24f,_250,_251,lowY,_253,_254,_255,_256;
for(var i=0;i<_246;i++){
_24e=_243[i];
if(_24e!=null){
if(_247>=_24e.left&&_247<=_24e.right){
if(_248>=(_24e.top-30)||(_24a!=null&&_24a[i]!=null)){
_24f=Math.min(Math.abs(_248-(_24e.top)),Math.abs(e.pageY-(_24e.top)));
_250=Math.min(Math.abs(_248-(_24e.yhalf)),Math.abs(e.pageY-(_24e.yhalf)));
_251=Math.min(Math.abs(_248-_24e.bottom),Math.abs(e.pageY-_24e.bottom));
lowY=Math.min(_24f,_250);
lowY=Math.min(lowY,_251);
_254=null;
_256=_24b;
while(_256!=null){
_255=_24c[_256];
if(lowY<_255.lowY){
break;
}else{
_254=_255;
_256=_255.nextIndex;
}
}
_24c.push({index:i,lowY:lowY,nextIndex:_256,lowYAlign:((!_236)?null:(lowY==_24f?"^":(lowY==_250?"~":"_")))});
_253=(_24c.length-1);
if(_254!=null){
_254.nextIndex=_253;
}else{
_24b=_253;
}
if(i==_249){
_24d=lowY;
}
}else{
if(_236){
if(_239==null){
_239=[];
}
var _258=(_24e.top-30)-_248;
_239.push(_230.string.padRight(String(i),2,_23d)+" y! "+_230.string.padRight(String(_258),4,_23d));
}
}
}else{
if(_236&&_247>_24e.width){
if(_239==null){
_239=[];
}
var _258=_247-_24e.width;
_239.push(_230.string.padRight(String(i),2,_23d)+" x! "+_230.string.padRight(String(_258),4,_23d));
}
}
}
}
var _259=-1;
var _25a=-1,_25b=-1;
var _25c=null,_25d=null,_25e=null,_25f=null,_260=null;
if(_24b!=null){
_255=_24c[_24b];
_259=_255.index;
_25c=_255.lowY;
if(_255.nextIndex!=null){
_255=_24c[_255.nextIndex];
_25a=_255.index;
_25d=_255.lowY;
_25f=_25d-_25c;
if(_255.nextIndex!=null){
_255=_24c[_255.nextIndex];
_25b=_255.index;
_25e=_255.lowY;
_260=_25e-_25c;
}
}
}
var _261=null;
var _262=(new Date().getTime());
var _263=this.pSLastNaturalColChgYTest;
if(_24d==null||(_263!=null&&Math.abs(_248-_263)>=Math.max((_245-Math.floor(_245*0.3)),Math.min(_245,21)))){
if(_259>=0){
this.pSLastNaturalColChgYTest=_248;
this.pSLastNaturalColChgHistory=[_259];
_24a={};
_24a[_259]=true;
this.pSLastNaturalColChgChoiceMap=_24a;
}
}else{
if(_263==null){
this.pSLastNaturalColChgYTest=_248;
_259=_249;
this.pSLastNaturalColChgHistory=[_259];
_24a={};
_24a[_259]=true;
this.pSLastNaturalColChgChoiceMap=_24a;
}else{
var _264=null;
var _265=this.pSLastColChgTime+this.pSColChgTimeTh;
if(_265<_262){
var _266=this.pSLastNaturalColChgHistory;
var _267=(_266==null?0:_266.length);
var _268=null,_269;
_256=_24b;
while(_256!=null){
_255=_24c[_256];
colI=_255.index;
if(_267==0){
_264=colI;
break;
}else{
_269=false;
for(var i=(_267-1);i>=0;i--){
if(_266[i]==colI){
if(_268==null||_268>i){
_268=i;
_264=colI;
}
_269=true;
break;
}
}
if(!_269){
_264=colI;
break;
}
}
_256=_255.nextIndex;
}
if(_264!=null){
_259=_264;
_24a[_259]=true;
if(_267==0||_266[(_267-1)]!=_259){
_266.push(_259);
}
}
}else{
_259=_249;
}
if(_236&&_264!=null){
_230.hostenv.println(_23a+"ColChg YTest="+_263+" LeastRecentColI="+_264+" History=["+(this.pSLastNaturalColChgHistory?this.pSLastNaturalColChgHistory.join(", "):"")+"] Map={"+_22f.printobj(this.pSLastNaturalColChgChoiceMap)+"} expire="+(_262-_265)+"}");
}
}
}
if(_236&&_261!=null){
if(this.devKeepLastMsg!=null){
_230.hostenv.println(this.devKeepLastMsg);
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
}
_230.hostenv.println(_261);
}
var col=(_259>=0?_244[_259]:null);
if(_236){
if(this.devLastColI!=_259){
_237=true;
}
this.devLastColI=_259;
}
var _26c=_22f.widget.pwGhost;
if(_241){
if(col!=null){
_22f.ui.setMarginBox(_26c,null,null,null,m.h,this.nodeLayoutInfo,_22f,_230);
_26c.col=null;
this.changeToTiledStarted=true;
this.posStatic=true;
}
}
var _26d=null,_26e=false,_26f=false;
if(_26c.col!=col&&col!=null){
this.pSLastColChgTime=_262;
this.pSLastColChgIdx=_259;
var _270=_26c.col;
if(_270!=null){
_230.dom.removeNode(_26c);
}
_26c.col=col;
var _271=_243[_259];
var _272=_271.childCount+1;
_271.childCount=_272;
if(_272==1){
_244[_259].domNode.style.height="";
}
col.domNode.appendChild(_26c);
_26f=true;
var _273=(_249!=null?((_249!=_259)?_243[_249]:null):(_270!=null?_243[_270.getPageColumnIndex()]:null));
if(_273!=null){
var _274=_273.childCount-1;
if(_274<0){
_274=0;
}
_273.childCount=_274;
if(_274==0){
_244[_273.pageColIndex].domNode.style.height="1px";
}
}
}
var _275=null,_276=null;
if(col!=null){
_275=_22f.ui.getPWinAndColChildren(col.domNode,_26c,true,false,true,false);
_276=_275.matchingNodes;
}
if(_276!=null&&_276.length>1){
var _277=_275.matchNodeIndexInMatchingNodes;
var _278=-1;
var _279=-1;
if(_277>0){
var _278=_230.html.getAbsolutePosition(_276[_277-1],true).y;
if((y-25)<=_278){
_230.dom.removeNode(_26c);
_26d=_276[_277-1];
_230.dom.insertBefore(_26c,_26d,true);
}
}
if(_277!=(_276.length-1)){
var _279=_230.html.getAbsolutePosition(_276[_277+1],true).y;
if((y+10)>=_279){
if(_277+2<_276.length){
_26d=_276[_277+2];
_230.dom.insertBefore(_26c,_26d,true);
}else{
col.domNode.appendChild(_26c);
_26e=true;
}
}
}
}
if(_237){
var _27a="";
if(_26d!=null||_26e||_26f){
_27a="put=";
if(_26d!=null){
_27a+="before("+_26d.id+")";
}else{
if(_26e){
_27a+="end";
}else{
if(_26f){
_27a+="end-default";
}
}
}
}
_230.hostenv.println(_23a+"col="+_259+_23b+_27a+_23b+"x="+x+_23b+"y="+y+_23b+"ePGx="+e.pageX+_23b+"ePGy="+e.pageY+_23b+"yTest="+_248);
var _27b="",colI,_24e;
_256=_24b;
while(_256!=null){
_255=_24c[_256];
colI=_255.index;
_24e=_243[_255.index];
_27b+=(_27b.length>0?_23e:"")+colI+_255.lowYAlign+(colI<10?_23d:"")+" -> "+_230.string.padRight(String(_255.lowY),4,_23d);
_256=_255.nextIndex;
}
_230.hostenv.println(_23c+_27b);
if(_239!=null){
var _27c="";
for(i=0;i<_239.length;i++){
_27c+=(i>0?_23e:"")+_239[i];
}
_230.hostenv.println(_23c+_27c);
}
this.devLastTime=_238;
this.devChgTh=this.devChgSubsqTh;
}
}
},onFirstMove:function(){
var _27d=this.jsObj;
var jsUI=_27d.ui;
var _27f=this.djObj;
var _280=this.windowOrLayoutWidget;
var node=this.node;
var _282=_280._getLayoutInfoMoveable();
this.nodeLayoutInfo=_282;
var mP=_280._getWindowMarginBox(_282,_27d);
this.staticWidth=null;
var _284=_27d.widget.pwGhost;
var _285=this.UAmoz;
var _286=this.changeToUntiled;
var _287=this.changeToTiled;
var m=null;
if(this.posStatic){
if(!_286){
var _289=_280.getPageColumnIndex();
var _28a=(_289>=0?_27d.page.columns[_289]:null);
_284.col=_28a;
this.pSLastColChgTime=new Date().getTime();
this.pSLastColChgIdx=_289;
}
m={w:mP.w,h:mP.h};
var _28b=node.parentNode;
var _28c=document.getElementById(_27d.id.DESKTOP);
var _28d=node.style;
this.staticWidth=_28d.width;
var _28e=_27f.html.getAbsolutePosition(node,true);
var _28f=_282.mE;
m.l=_28e.left-_28f.l;
m.t=_28e.top-_28f.t;
if(_285){
if(!_286){
jsUI.setMarginBox(_284,null,null,null,mP.h,_282,_27d,_27f);
}
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_28d.position="absolute";
if(!_286){
_28d.zIndex=_27d.page.getPWinHighZIndex()+1;
}else{
_28d.zIndex=(_280._setAsTopZIndex(_27d.page,_27d.css,_280.dNodeCss,false));
}
if(!_286){
_28b.insertBefore(_284,node);
if(!_285){
jsUI.setMarginBox(_284,null,null,null,mP.h,_282,_27d,_27f);
}
_28c.appendChild(node);
var _290=jsUI.getPWinAndColChildren(_28b,_284,true,false,true);
this.prevColumnNode=_28b;
this.prevIndexInCol=_290.matchNodeIndexInMatchingNodes;
}else{
_280._updtDimsObj(false,true);
_28c.appendChild(node);
}
}else{
m=mP;
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
jsUI.evtDisconnectWObj(this.events.pop(),_27f.event);
var _291=this.disqualifiedColumnIndexes;
var _292=(this.isDebug||_27d.debug.dragWindowStart),_293;
if(_292){
_293=_27d.debugindentT;
var _294=_27d.debugindentH;
var _295="";
if(_291!=null){
_295=_294+"dqCols=["+_27d.objectKeys(_291).join(", ")+"]";
}
var _296=_280.title;
if(_296==null){
_296=node.id;
}
_27f.hostenv.println("DRAG \""+_296+"\""+_294+((this.posStatic&&!_286)?("col="+(_284.col?_284.col.getPageColumnIndex():"null")+_294):"")+"m.l = "+m.l+_294+"m.t = "+m.t+_295);
}
if(this.posStatic||_287){
this.heightHalf=mP.h/2;
if(!_286){
var _297=_27d.page.columns||[];
var _298=_297.length;
var _299=new Array(_298);
var _29a=_27f.byId(_27d.id.COLUMNS);
if(_29a){
var _29b=_27d.page.layoutInfo;
this._getChildColInfo(_29a,_299,_27d.page.columns,_291,_29b,_29b.columns,_29b.desktop,node,(_292?1:null),_293,_27f,_27d);
if(_292){
_27f.hostenv.println(_293+"--------------------");
}
}
this.columnInfoArray=_299;
}
}
if(this.posStatic){
jsUI.setMarginBox(node,m.l,m.t,mP.w,null,_282,_27d,_27f);
if(this.notifyOnAbsolute){
_280.dragChangeToAbsolute(this,node,this.marginBox,_27f,_27d);
}
if(_286){
this.posStatic=false;
}
}
},_getChildColInfo:function(_29c,_29d,_29e,_29f,_2a0,_2a1,_2a2,_2a3,_2a4,_2a5,_2a6,_2a7){
var _2a8=_29c.childNodes;
var _2a9=(_2a8?_2a8.length:0);
if(_2a9==0){
return;
}
var _2aa=_2a6.html.getAbsolutePosition(_29c,true);
var _2ab=_2a7.ui.getMarginBox(_29c,_2a1,_2a2,_2a7);
var _2ac=_2a0.column;
var _2ad,col,_2af,_2b0,_2b1,_2b2,_2b3,_2b4,_2b5,_2b6,_2b7,_2b8,_2b9;
var _2ba=null,_2bb=(_2a4!=null?(_2a4+1):null),_2bc,_2bd;
for(var i=0;i<_2a9;i++){
_2ad=_2a8[i];
_2af=_2ad.getAttribute("columnindex");
_2b0=(_2af==null?-1:new Number(_2af));
if(_2b0>=0){
_2b1=_2ad.getAttribute("layoutid");
_2b2=(_2b1!=null&&_2b1.length>0);
_2b9=true;
_2bc=_2bb;
_2bd=null;
if(!_2b2&&(!(_2ad===_2a3))){
col=_29e[_2b0];
if(col&&!col.layoutActionsDisabled&&(_29f==null||_29f[_2b0]==null)){
_2b3=_2a7.ui.getMarginBox(_2ad,_2ac,_2a1,_2a7);
if(_2ba==null){
_2ba=_2b3.t-_2ab.t;
_2b8=_2ab.h-_2ba;
}
_2b4=_2aa.left+(_2b3.l-_2ab.l);
_2b5=_2aa.top+_2ba;
_2b6=_2b3.h;
if(_2b6<_2b8){
_2b6=_2b8;
}
if(_2b6<40){
_2b6=40;
}
var _2bf=_2ad.childNodes;
_2b7={left:_2b4,top:_2b5,right:(_2b4+_2b3.w),bottom:(_2b5+_2b6),childCount:(_2bf?_2bf.length:0),pageColIndex:_2b0};
_2b7.height=_2b7.bottom-_2b7.top;
_2b7.width=_2b7.right-_2b7.left;
_2b7.yhalf=_2b7.top+(_2b7.height/2);
_29d[_2b0]=_2b7;
_2b9=(_2b7.childCount>0);
if(_2a4!=null){
_2bd=(_2a7.debugDims(_2b7,true)+" yhalf="+_2b7.yhalf+(_2b3.h!=_2b6?(" hreal="+_2b3.h):"")+" childC="+_2b7.childCount+"}");
}
}
}
if(_2a4!=null){
if(_2b2){
_2bc=_2bb+1;
}
if(_2bd==null){
_2bd="---";
}
_2a6.hostenv.println(_2a6.string.repeat(_2a5,_2a4)+"["+((_2b0<10?" ":"")+_2af)+"] "+_2bd);
}
if(_2b9){
this._getChildColInfo(_2ad,_29d,_29e,_29f,_2a0,(_2b2?_2a0.columnLayoutHeader:_2ac),_2a1,_2a3,_2bc,_2a5,_2a6,_2a7);
}
}
}
},mouseUpDestroy:function(){
var _2c0=this.djObj;
var _2c1=this.jsObj;
this.destroy(_2c0,_2c0.event,_2c1,_2c1.ui);
},destroy:function(_2c2,_2c3,_2c4,jsUI){
var _2c6=this.windowOrLayoutWidget;
var node=this.node;
var _2c8=null;
if(this.moveInitiated&&_2c6&&node){
this.moveInitiated=false;
try{
if(this.posStatic){
var _2c9=_2c4.widget.pwGhost;
var _2ca=node.style;
if(_2c9&&_2c9.col){
_2c6.column=0;
_2c2.dom.insertBefore(node,_2c9,true);
}else{
if(this.prevColumnNode!=null&&this.prevIndexInCol!=null){
_2c2.dom.insertAtIndex(node,this.prevColumnNode,this.prevIndexInCol);
}else{
var _2cb=_2c4.page.getColumnDefault();
if(_2cb!=null){
_2c2.dom.prependChild(node,_2cb.domNode);
}
}
}
if(_2c9){
_2c2.dom.removeNode(_2c9);
}
}
_2c6.endDragging(this.posRecord,this.changeToUntiled,this.changeToTiled);
}
catch(ex){
_2c8=this._getErrMsg(ex,"destroy reset-window error",_2c6,_2c8);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_2c3);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_2c8=this._getErrMsg(ex,"destroy event clean-up error",_2c6,_2c8);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_2c4.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_2c8=this._getErrMsg(ex,"destroy clean-up error",_2c6,_2c8);
}
_2c4.widget._movingInProgress=false;
if(_2c8!=null){
_2c2.raise(_2c8);
}
}});
dojo.dnd.Moveable=function(_2cc,opt){
var _2ce=jetspeed;
var jsUI=_2ce.ui;
var _2d0=dojo;
var _2d1=_2d0.event;
this.windowOrLayoutWidget=_2cc;
this.handle=opt.handle;
var _2d2=[];
_2d2.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_2d1));
_2d2.push(jsUI.evtConnect("after",this.handle,"ondragstart",_2ce,"_stopEvent",_2d1));
_2d2.push(jsUI.evtConnect("after",this.handle,"onselectstart",_2ce,"_stopEvent",_2d1));
this.events=_2d2;
};
dojo.extend(dojo.dnd.Moveable,{minMove:5,enabled:true,mover:null,onMouseDown:function(e){
if(e&&e.button==2){
return;
}
var _2d4=dojo;
var _2d5=_2d4.event;
var _2d6=jetspeed;
var jsUI=jetspeed.ui;
if(this.mover!=null||this.tempEvents!=null){
this._cleanUpLastEvt(_2d4,_2d5,_2d6,jsUI);
_2d6.stopEvent(e);
}else{
if(this.enabled){
if(this.tempEvents!=null){
if(djConfig.isDebug){
_2d6.debugAlert("ERROR: Moveable onmousedown tempEvent already defined");
}
}else{
var _2d8=[];
var doc=this.handle.ownerDocument;
_2d8.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_2d5));
this.tempEvents=_2d8;
}
if(!this.windowOrLayoutWidget.posStatic){
this.windowOrLayoutWidget.bringToTop(e,false,true,_2d6);
}
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
_2d6.stopEvent(e);
},onMouseMove:function(e,_2db){
var _2dc=jetspeed;
var _2dd=dojo;
var _2de=_2dd.event;
if(_2db||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
this._cleanUpLastEvt(_2dd,_2de,_2dc,_2dc.ui);
var _2df=this.windowOrLayoutWidget;
var _2e0=null;
this.beforeDragColRowInfo=null;
if(!_2df.isLayoutPane){
var _2e1=_2df.domNode;
if(_2e1!=null){
this.node=_2e1;
this.mover=new _2dd.dnd.Mover(_2df,_2e1,_2e0,this,e,false,_2dd,_2dc);
}
}else{
_2df.startDragging(e,this,_2dd,_2dc);
}
}
_2dc.stopEvent(e);
},onMouseUp:function(e){
var _2e3=dojo;
var _2e4=jetspeed;
this._cleanUpLastEvt(_2e3,_2e3.event,_2e4,_2e4.ui);
},_cleanUpLastEvt:function(_2e5,_2e6,_2e7,jsUI){
if(this._mDownEvt!=null){
_2e7.stopEvent(this._mDownEvt);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_2e5,_2e6,_2e7,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_2e6);
this.tempEvents=null;
},destroy:function(_2e9,_2ea,_2eb,jsUI){
this._cleanUpLastEvt(_2e9,_2ea,_2eb,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_2ea);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_2ee,_2ef){
var s=_2ee||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_2ef);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_2ef.UAmoz){
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
if(_2ef.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_2fa,_2fb){
var s=_2fa||dojo.gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_2fb.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_302,_303,_304,_305,_306,_307){
var s=_306||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_307);
if(_304!=null&&_304>=0){
_304=Math.max(_304-pb.w-mb.w,0);
}
if(_305!=null&&_305>=0){
_305=Math.max(_305-pb.h-mb.h,0);
}
dojo._setBox(node,_302,_303,_304,_305);
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
dojo._getPadExtents=function(n,_315){
var s=_315||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_31b){
var s=_31b||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_320,_321){
var s=_320||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_321.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_329){
var ne="none",px=dojo._toPixelValue,s=_329||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_332,_333){
return (parseFloat(_333)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_335,_336){
if(!_336){
return 0;
}
if(_336.slice&&(_336.slice(-2)=="px")){
return parseFloat(_336);
}
with(_335){
var _337=style.left;
var _338=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_336;
_336=style.pixelLeft;
}
catch(e){
_336=0;
}
style.left=_337;
runtimeStyle.left=_338;
}
return _336;
};
}
dojo.gcs=dojo.getComputedStyle;

