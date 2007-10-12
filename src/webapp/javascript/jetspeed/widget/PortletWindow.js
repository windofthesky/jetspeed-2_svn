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
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _39=_9.css.cssDis;
if(this.tbNodeCss&&!wDC.windowTitlebar){
this.tbNodeCss[_39]="none";
}
if(this.rbNodeCss&&!wDC.windowResizebar){
this.rbNodeCss[_39]="none";
}
}
var _3a=false;
var _3b=_8.childNodes;
if(_25&&_3b){
var _3c=iP[_a.PP_ROW];
if(_3c!=null){
var _3d=new Number(_3c);
if(_3d>=0){
var _3e=_3b.length-1;
if(_3e>=_3d){
var _3f=_3b[_3d];
if(_3f){
_8.insertBefore(_1c,_3f);
_3a=true;
}
}
}
}
}
if(!_3a){
_8.appendChild(_1c);
}
if(!wDC.layout){
var _40="display: block; visibility: hidden; width: "+_27+"px"+((_28!=null&&_28>0)?("; height: "+_28+"px"):"");
_1c.style.cssText=_40;
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
var _42=_f.createElement("div");
_42.id="pwGhost";
var _43=_c.getPortletDecorationDefault();
if(!_43){
_43=_18;
}
_42.className=_1a;
_42.style.position="static";
_42.style.width="";
_42.style.left="auto";
_42.style.top="auto";
_9.widget.pwGhost=_42;
}
if(ie6&&_9.widget.ie6ZappedContentHelper==null){
var _44=_f.createElement("span");
_44.id="ie6ZappedContentHelper";
_9.widget.ie6ZappedContentHelper=_44;
}
},_buildActionStructures:function(wDC,_46,_47,_48,_49,_4a,_4b){
var _4c=new Array();
var aNm,_4e,_4f=false;
var _50=new Array();
var _51=new Object();
if(wDC.windowActionButtonOrder!=null){
if(_46){
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_4c.push(aNm);
_51[aNm]=true;
}
}else{
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_4e=false;
if(aNm==_49.ACT_MINIMIZE||aNm==_49.ACT_MAXIMIZE||aNm==_49.ACT_RESTORE||aNm==_49.ACT_MENU||_4a.windowActionDesktop[aNm]!=null){
_4e=true;
}
if(_4e){
_4c.push(aNm);
_51[aNm]=true;
}
}
}
var _53=(wDC.windowActionButtonMax==null?-1:wDC.windowActionButtonMax);
if(_53!=-1&&_4c.length>=_53){
var _54=0;
var _55=_4c.length-_53+1;
for(var i=0;i<_4c.length&&_54<_55;i++){
aNm=_4c[i];
if(aNm!=_49.ACT_MENU){
_50.push(aNm);
_4c[i]=null;
delete _51[aNm];
_54++;
}
}
}
if(wDC.windowActionNoImage){
for(var i=0;i<_4c.length;i++){
aNm=_4c[i];
if(wDC.windowActionNoImage[aNm]!=null){
if(aNm==_49.ACT_MENU){
_4f=true;
}else{
_50.push(aNm);
}
_4c[i]=null;
delete _51[aNm];
}
}
}
}
if(wDC.windowActionMenuOrder){
if(_46){
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
_50.push(aNm);
}
}else{
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
if(_4a.windowActionDesktop[aNm]!=null){
_50.push(aNm);
}
}
}
}
var _57=new Array();
if(_50.length>0||this.dbOn){
var _58=new Object();
for(var i=0;i<_50.length;i++){
aNm=_50[i];
if(aNm!=null&&_58[aNm]==null&&_51[aNm]==null){
_57.push(aNm);
_58[aNm]=true;
}
}
if(this.dbOn){
_57.push({aNm:this.dbMenuDims,dev:true});
}
}
var _59=null;
if(_57.length>0){
var _5a={};
var aNm,_5b,_5c,_5d;
_59=_4b.widget.createWidget("PopupMenu2",{id:this.widgetId+"_ctxmenu",contextMenuForWindow:false},null);
_59.onItemClick=function(mi){
var _aN=mi.jsActNm;
var _60=this.pWin;
if(!mi.jsActDev){
_60.actionProcess(_aN);
}else{
_60.actionProcessDev(_aN);
}
};
for(var i=0;i<_57.length;i++){
aNm=_57[i];
_5d=false;
if(!aNm.dev){
_5b=this.actionLabels[aNm];
}else{
_5d=true;
_5b=aNm=aNm.aNm;
}
_5c=_4b.widget.createWidget("MenuItem2",{caption:_5b,jsActNm:aNm,jsActDev:_5d});
_5a[aNm]=_5c;
_59.addChild(_5c);
}
_59.menuItemsByName=_5a;
_47.appendChild(_59.domNode);
_48.ui.addPopupMenuWidget(_59);
}
wDC.windowActionMenuHasNoImg=_4f;
if(_46){
wDC.windowActionButtonNames=_4c;
wDC.windowActionMenuNames=_57;
wDC.windowActionMenuWidget=_59;
}else{
wDC.windowActionButtonNamesNp=_4c;
wDC.windowActionMenuNamesNp=_57;
wDC.windowActionMenuWidgetNp=_59;
}
return _4c;
},_createActionButtonNode:function(aNm,doc,_63,_64,wDC,_66,_67,_68,_69,_6a){
if(aNm!=null){
var _6b=doc.createElement("div");
_6b.className="portletWindowActionButton";
_6b.style.backgroundImage="url("+_67.getPortletDecorationBaseUrl(this.decName)+"/images/desktop/"+aNm+".gif)";
_6b.actionName=aNm;
this.actionButtons[aNm]=_6b;
this.tbNode.appendChild(_6b);
_68.evtConnect("after",_6b,"onclick",this,"actionBtnClick",_6a);
if(wDC.windowActionButtonTooltip){
var _6c=this.actionLabels[aNm];
this.tooltips.push(_64.addNode(_6b,_6c,true,null,null,null,_66,_68,_6a));
}else{
_68.evtConnect("after",_6b,"onmousedown",_66,"_stopEvent",_6a);
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
},_createLayoutInfo:function(_6d,_6e,_6f,_70,_71,_72,_73,_74,_75){
var _76=_73.gcs(_6f);
var _77=_73.gcs(_70);
var _78=_75.getLayoutExtents(_6f,_76,_73,_74);
var _79=_75.getLayoutExtents(_70,_77,_73,_74);
var _7a={dNode:_78,cNode:_79};
var _7b=Math.max(0,_79.mE.t);
var _7c=Math.max(0,_79.mE.h-_79.mE.t);
var _7d=0;
var _7e=0;
var _7f=null;
if(_71){
var _80=_73.gcs(_71);
_7f=_75.getLayoutExtents(_71,_80,_73,_74);
var _81=_80.cursor;
if(_81==null||_81.length==0){
_81="move";
}
_6d.dragCursor=_81;
_7f.mBh=_73.getMarginBox(_71,_80,_74).h;
var _82=Math.max(0,_7f.mE.h-_7f.mE.t);
_7d=(_7f.mBh-_82)+Math.max(0,(_82-_7b));
_7a.tbNode=_7f;
}
var _83=null;
if(_72){
var _84=_73.gcs(_72);
_83=_75.getLayoutExtents(_72,_84,_73,_74);
_83.mBh=_73.getMarginBox(_72,_84,_74).h;
var _85=Math.max(0,_83.mE.t);
_7e=(_83.mBh-_85)+Math.max(0,(_85-_7c));
_7a.rbNode=_83;
}
_7a.cNode_mBh_LessBars=_7d+_7e;
if(!_6e){
_6d.layout=_7a;
}else{
_6d.layoutIFrame=_7a;
}
},actionBtnClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.actionProcess(evt.target.actionName,evt);
},actionMenuOpen:function(evt){
var _88=jetspeed;
var _89=_88.id;
var _8a=this.actionMenuWidget;
if(!_8a){
return;
}
if(_8a.isShowingNow){
_8a.close();
}
var _8b=null;
var _8c=null;
if(this.portlet){
_8b=this.portlet.getCurrentActionState();
_8c=this.portlet.getCurrentActionMode();
}
var _8d=_8a.menuItemsByName;
var _8e,_8f;
for(var aNm in _8d){
_8e=_8d[aNm];
_8f=(this._isActionEnabled(aNm,_8b,_8c,_88,_89))?"":"none";
_8e.domNode.style.display=_8f;
}
_8a.pWin=this;
_8a.onOpen(evt);
},actionProcessDev:function(aNm,evt){
if(aNm==this.dbMenuDims&&jetspeed.debugPWinPos){
jetspeed.debugPWinPos(this);
}
},actionProcess:function(aNm,evt){
var _95=jetspeed;
var _96=_95.id;
if(aNm==null){
return;
}
if(_95.prefs.windowActionDesktop[aNm]!=null){
if(aNm==_96.ACT_DESKTOP_TILE){
this.makeTiled();
}else{
if(aNm==_96.ACT_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(aNm==_96.ACT_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(aNm==_96.ACT_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false,false);
}
}
}
}
}else{
if(aNm==_96.ACT_MENU){
this.actionMenuOpen(evt);
}else{
if(aNm==_96.ACT_MINIMIZE){
if(this.portlet&&this.windowState==_96.ACT_MAXIMIZE){
this.needsRenderOnRestore=true;
}
this.minimizeWindow();
if(this.portlet){
_95.changeActionForPortlet(this.portlet.getId(),_96.ACT_MINIMIZE,null);
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_96.ACT_RESTORE){
var _97=false;
if(this.portlet){
if(this.windowState==_96.ACT_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_97=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
if(this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.portlet.renderAction(aNm);
}else{
_95.changeActionForPortlet(this.portlet.getId(),_96.ACT_RESTORE,null);
}
}
if(!_97){
this.restoreWindow();
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_96.ACT_MAXIMIZE){
if(this.portlet&&this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(aNm);
}else{
this.actionBtnSync(_95,_96);
}
}else{
if(aNm==_96.ACT_REMOVEPORTLET){
if(this.portlet){
var _98=dojo.widget.byId(_96.PG_ED_WID);
if(_98!=null){
_98.deletePortlet(this.portlet.entityId,this.title);
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
},_isActionEnabled:function(aNm,_9a,_9b,_9c,_9d){
var _9c=jetspeed;
var _9d=_9c.id;
var _9e=false;
var _9f=this.windowState;
if(this.minimizeTempRestore!=null){
if(this.portlet){
var _a0=this.portlet.getAction(aNm);
if(_a0!=null){
if(_a0.id==_9d.ACT_REMOVEPORTLET){
if(_9c.page.editMode&&this.getLayoutActionsEnabled()){
_9e=true;
}
}
}
}
}else{
if(aNm==_9d.ACT_MENU){
if(!this._actionMenuIsEmpty(_9c,_9d)){
_9e=true;
}
}else{
if(_9c.prefs.windowActionDesktop[aNm]!=null){
if(this.getLayoutActionsEnabled()){
var _a1=(this.ie6&&_9f==_9d.ACT_MINIMIZE);
if(aNm==_9d.ACT_DESKTOP_HEIGHT_EXPAND){
if(!this.heightToFit&&!_a1){
_9e=true;
}
}else{
if(aNm==_9d.ACT_DESKTOP_HEIGHT_NORMAL){
if(this.heightToFit&&!_a1){
_9e=true;
}
}else{
if(aNm==_9d.ACT_DESKTOP_TILE&&_9c.prefs.windowTiling){
if(!this.posStatic){
_9e=true;
}
}else{
if(aNm==_9d.ACT_DESKTOP_UNTILE){
if(this.posStatic){
_9e=true;
}
}
}
}
}
}
}else{
if(this.portlet){
var _a0=this.portlet.getAction(aNm);
if(_a0!=null){
if(_a0.id==_9d.ACT_REMOVEPORTLET){
if(_9c.page.editMode&&this.getLayoutActionsEnabled()){
_9e=true;
}
}else{
if(_a0.type==_9d.PORTLET_ACTION_TYPE_MODE){
if(aNm!=_9b){
_9e=true;
}
}else{
if(aNm!=_9a){
_9e=true;
}
}
}
}else{
_9e=true;
}
}else{
if(aNm==_9d.ACT_MAXIMIZE){
if(aNm!=_9f&&this.minimizeTempRestore==null){
_9e=true;
}
}else{
if(aNm==_9d.ACT_MINIMIZE){
if(aNm!=_9f){
_9e=true;
}
}else{
if(aNm==_9d.ACT_RESTORE){
if(_9f==_9d.ACT_MAXIMIZE||_9f==_9d.ACT_MINIMIZE){
_9e=true;
}
}else{
_9e=true;
}
}
}
}
}
}
}
return _9e;
},_actionMenuIsEmpty:function(_a2,_a3){
var _a4=true;
var _a5=this.actionMenuWidget;
if(_a5){
var _a6=null;
var _a7=null;
if(this.portlet){
_a6=this.portlet.getCurrentActionState();
_a7=this.portlet.getCurrentActionMode();
}
for(var aNm in _a5.menuItemsByName){
if(aNm!=_a3.ACT_MENU&&this._isActionEnabled(aNm,_a6,_a7,_a2,_a3)){
_a4=false;
break;
}
}
}
return _a4;
},actionBtnSyncDefer:function(){
dojo.lang.setTimeout(this,this.actionBtnSync,10);
},actionBtnSync:function(_a9,_aa){
if(!_a9){
_a9=jetspeed;
_aa=_a9.id;
}
var _ab=this.decConfig.windowActionButtonHide;
var _ac=null;
var _ad=null;
if(this.portlet){
_ac=this.portlet.getCurrentActionState();
_ad=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionButtons){
var _af=false;
if(!_ab||this.titleLit){
_af=this._isActionEnabled(aNm,_ac,_ad,_a9,_aa);
}
var _b0=this.actionButtons[aNm];
_b0.style.display=(_af)?"":"none";
}
},_postCreateMaximizeWindow:function(){
var _b1=jetspeed;
var _b2=_b1.id;
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(_b2.ACT_MAXIMIZE);
}else{
this.actionBtnSync(_b1,_b2);
}
},minimizeWindowTemporarily:function(){
var _b3=jetspeed;
var _b4=_b3.id;
if(this.minimizeTempRestore==null){
this.minimizeTempRestore=this.windowState;
if(this.windowState!=_b4.ACT_MINIMIZE){
this.minimizeWindow(false);
}
this.actionBtnSync(_b3,_b4);
}
},restoreFromMinimizeWindowTemporarily:function(){
var _b5=jetspeed;
var _b6=_b5.id;
var _b7=this.minimizeTempRestore;
this.minimizeTempRestore=null;
if(_b7){
if(_b7!=_b6.ACT_MINIMIZE){
this.restoreWindow();
}
this.actionBtnSync(_b5,_b6);
}
},minimizeWindow:function(_b8){
if(!this.tbNode){
return;
}
var _b9=jetspeed;
if(this.windowState==jetspeed.id.ACT_MAXIMIZE){
_b9.widget.showAllPortletWindows();
this.restoreWindow();
}else{
if(!_b8){
this._updtDimsObj(false,false);
}
}
var _ba=_b9.css.cssDis;
this.cNodeCss[_ba]="none";
if(this.rbNodeCss){
this.rbNodeCss[_ba]="none";
}
this.windowState=_b9.id.ACT_MINIMIZE;
if(this.ie6){
this.containerNode.style.display="none";
}
this._alterCss(true,true);
},maximizeWindow:function(){
var _bb=jetspeed;
var _bc=_bb.id;
var _bd=this.domNode;
var _be=[this.widgetId];
_bb.widget.hideAllPortletWindows(_be);
if(this.windowState==_bc.ACT_MINIMIZE){
this.restoreWindow();
}
var _bf=this.posStatic;
this.preMaxPosStatic=_bf;
this.preMaxHeightToFit=this.heightToFit;
var _c0=_bf;
this._updtDimsObj(false,_c0);
var _c1=document.getElementById(_bc.DESKTOP);
var _c2=dojo.html.getAbsolutePosition(_c1,true).y;
var _c3=dojo.html.getViewport();
var _c4=dojo.html.getPadding(_bb.docBody);
this.dimsUntiledTemp={w:_c3.width-_c4.width-2,h:_c3.height-_c4.height-_c2,l:1,t:_c2};
this._setTitleBarDragging(true,_bb.css,false);
this.posStatic=false;
this.heightToFit=false;
this._alterCss(true,true);
if(_bf){
_c1.appendChild(_bd);
}
this.windowState=_bc.ACT_MAXIMIZE;
},restoreWindow:function(){
var _c5=jetspeed;
var _c6=_c5.id;
var _c7=_c5.css;
var _c8=this.domNode;
var _c9=false;
if(_c8.style.position=="absolute"){
_c9=true;
}
var _ca=null;
if(this.windowState==_c6.ACT_MAXIMIZE){
_c5.widget.showAllPortletWindows();
this.posStatic=this.preMaxPosStatic;
this.heightToFit=this.preMaxHeightToFit;
this.dimsUntiledTemp=null;
}
var _cb=_c7.cssDis;
this.cNodeCss[_cb]="block";
if(this.rbNodeCss){
this.rbNodeCss[_cb]="block";
}
this.windowState=_c6.ACT_RESTORE;
this._setTitleBarDragging(true,_c5.css);
var ie6=this.ie6;
if(!ie6){
this._alterCss(true,true);
}else{
var _cd=null;
if(this.heightToFit){
_cd=this.iNodeCss;
this.iNodeCss=null;
}
this._alterCss(true,true);
this._updtDimsObj(false,false,true,false,true);
if(_cd!=null){
this.iNodeCss=_cd;
}
this._alterCss(false,false,true);
}
if(this.posStatic&&_c9){
this._tileWindow(_c5);
}
},_tileWindow:function(_ce){
if(!this.posStatic){
return;
}
var _cf=this.domNode;
var _d0=this.getDimsObj(this.posStatic);
var _d1=true;
if(_d0!=null){
var _d2=_d0.colInfo;
if(_d2!=null&&_d2.colI!=null){
var _d3=_ce.page.columns[_d2.colI];
var _d4=((_d3!=null)?_d3.domNode:null);
if(_d4!=null){
var _d5=null;
var _d6=_d4.childNodes.length;
if(_d6==0){
_d4.appendChild(_cf);
_d1=false;
}else{
var _d7,_d8,_d9=0;
if(_d2.pSibId!=null||_d2.nSibId!=null){
_d7=_d4.firstChild;
do{
_d8=_d7.id;
if(_d8==null){
continue;
}
if(_d8==_d2.pSibId){
dojo.dom.insertAfter(_cf,_d7);
_d1=false;
}else{
if(_d8==_d2.nSibId){
dojo.dom.insertBefore(_cf,_d7);
_d1=false;
}else{
if(_d9==_d2.elmtI){
_d5=_d7;
}
}
}
_d7=_d7.nextSibling;
_d9++;
}while(_d1&&_d7!=null);
}
}
if(_d1){
if(_d5!=null){
dojo.dom.insertBefore(_cf,_d5);
}else{
dojo.dom.prependChild(_cf,_d4);
}
_d1=false;
}
}
}
}
if(_d1){
var _da=_ce.page.getColumnDefault();
if(_da!=null){
dojo.dom.prependChild(_cf,_da.domNode);
}
}
},getDimsObj:function(_db,_dc){
return (_db?((this.dimsTiledTemp!=null&&!_dc)?this.dimsTiledTemp:this.dimsTiled):((this.dimsUntiledTemp!=null&&!_dc)?this.dimsUntiledTemp:this.dimsUntiled));
},_updtDimsObj:function(_dd,_de,_df,_e0,_e1,_e2){
var _e3=jetspeed;
var _e4=dojo;
var _e5=this.domNode;
var _e6=this.posStatic;
var _e7=this.getDimsObj(_e6,_e2);
var _e8=(!_df&&!_e6&&(!_dd||_e7.l==null||_e7.t==null));
var _e9=(!_e0&&(!_dd||_e8||_e1||_e7.w==null||_e7.h==null));
if(_e9||_e8){
var _ea=this._getLayoutInfo().dNode;
if(_e9){
var _eb=_e3.ui.getMarginBoxSize(_e5,_ea);
_e7.w=_eb.w;
_e7.h=_eb.h;
if(!_e6){
_e8=true;
}
}
if(_e8){
var _ec=_e4.html.getAbsolutePosition(_e5,true);
_e7.l=_ec.x-_ea.mE.l-_ea.pbE.l;
_e7.t=_ec.y-_ea.mE.t-_ea.pbE.t;
}
}
if(_e6){
if(_de||_e2&&_e7.colInfo==null){
var _ed=0,_ee=_e5.previousSibling,_ef=_e5.nextSibling;
var _f0=(_ee!=null?_ee.id:null),_f1=(_ef!=null?_ef.id:null);
if(_ee!=null){
_f0=_ee.id;
}
while(_ee!=null){
_ed++;
_ee=_ee.previousSibling;
}
_e7.colInfo={elmtI:_ed,pSibId:_f0,nSibId:_f1,colI:this.getPageColumnIndex()};
}
if(_e2){
this.dimsTiledTemp={w:_e7.w,h:_e7.h,colInfo:_e7.colInfo};
_e7=this.dimsTiledTemp;
}
}else{
if(_e2){
this.dimsUntiledTemp={w:_e7.w,h:_e7.h,l:_e7.l,t:_e7.t};
_e7=this.dimsUntiledTemp;
}
}
return _e7;
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACT_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},_setTitleBarDragging:function(_f2,_f3,_f4){
var _f5=this.tbNode;
if(!_f5){
return;
}
if(typeof _f4=="undefined"){
_f4=this.getLayoutActionsEnabled();
}
var _f6=this.resizeHandle;
var _f7=null;
if(_f4){
_f7=this.decConfig.dragCursor;
if(_f6){
_f6.domNode.style.display="";
}
if(this.drag){
this.drag.enable();
}
}else{
_f7="default";
if(_f6){
_f6.domNode.style.display="none";
}
if(this.drag){
this.drag.disable();
}
}
this.tbNodeCss[_f3.cssCur]=_f7;
if(!_f2){
_f5.style.cursor=_f7;
}
},onMouseDown:function(evt){
this.bringToTop(evt,false,false,jetspeed);
},bringToTop:function(evt,_fa,_fb,_fc){
if(!this.posStatic){
var _fd=_fc.page;
var _fe=_fc.css;
var _ff=this.dNodeCss;
var _100=_fd.getPWinHighZIndex();
var zCur=_ff[_fe.cssZIndex];
if(_100!=zCur){
var zTop=this._setAsTopZIndex(_fd,_fe,_ff,false);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
if(!_fb&&this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
}
}
}else{
if(_fa){
var zTop=this._setAsTopZIndex(_fd,_fe,_ff,true);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
}
}
}
},_setAsTopZIndex:function(_103,_104,_105,_106){
var zTop=String(_103.getPWinTopZIndex(_106));
_105[_104.cssZIndex]=zTop;
return zTop;
},makeUntiled:function(){
var _108=jetspeed;
this._updtDimsObj(false,true);
this.posStatic=false;
this._updtDimsObj(true,false);
this._setAsTopZIndex(_108.page,_108.css,this.dNodeCss,false);
this._alterCss(true,true);
var _109=document.getElementById(jetspeed.id.DESKTOP);
_109.appendChild(this.domNode);
if(this.windowState==_108.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},makeTiled:function(){
this.posStatic=true;
var _10a=jetspeed;
this._setAsTopZIndex(_10a.page,_10a.css,this.dNodeCss,true);
this._alterCss(true,true);
this._tileWindow(_10a);
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
},makeHeightToFit:function(_10b){
var _10c=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
if(this.ie6){
var _10d=this.iNodeCss;
this.iNodeCss=null;
this._alterCss(false,true);
this._updtDimsObj(false,false,true,false,true);
this.iNodeCss=_10d;
}
this._alterCss(false,true);
if(!_10b&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_10e,_10f){
var _110=this.getDimsObj(this.posStatic);
var _111=this._getLayoutInfo().dNode;
var _112=jetspeed.ui.getMarginBoxSize(this.domNode,_111);
_110.w=_112.w;
_110.h=_112.h;
this.heightToFit=false;
this._alterCss(false,true);
if(!_10f&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_10e&&this.portlet){
this.portlet.submitWinState();
}
},resizeTo:function(w,h,_115){
var _116=this.getDimsObj(this.posStatic);
_116.w=w;
_116.h=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _117=this.resizeHandle;
if(_117!=null&&_117._isSizing){
jetspeed.ui.evtConnect("after",_117,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
this.resizeNotifyChildWidgets();
},resizeNotifyChildWidgets:function(){
if(this.childWidgets){
var _118=this.childWidgets;
var _119=_118.length,_11a;
for(var i=0;i<_119;i++){
try{
_11a=_118[i];
if(_11a){
_11a.checkSize();
}
}
catch(e){
}
}
}
},_getLayoutInfo:function(){
var _11c=this.iframesInfo;
return ((!(_11c&&_11c.layout))?this.decConfig.layout:this.decConfig.layoutIFrame);
},_getLayoutInfoMoveable:function(){
return this._getLayoutInfo().dNode;
},onBrowserWindowResize:function(){
if(this.ie6){
this._resetIE6TiledSize(false);
}
},_resetIE6TiledSize:function(_11d){
var _11e=this.posStatic;
if(_11e){
var _11f=this.domNode;
var _120=this.getDimsObj(_11e);
_120.w=Math.max(0,this.domNode.parentNode.offsetWidth-this.colWidth_pbE);
this._alterCss(_11d,false,false,false,true);
}
},_alterCss:function(_121,_122,_123,_124,_125,_126){
var _127=jetspeed;
var _128=_127.css;
var _129=this.iframesInfo;
var _12a=(_129&&_129.layout);
var _12b=(!_12a?this.decConfig.layout:this.decConfig.layoutIFrame);
var _12c=this.dNodeCss,_12d=null,_12e=null,_12f=null,_130=false,_131=this.iNodeCss,_132=null;
if(_131&&_12a){
_132=_129.iframeCoverIE6Css;
}
var _133=this.posStatic;
var _134=(_133&&_131==null);
var _135=this.heightToFit;
var _136=(_121||_125||(_123&&!_134));
var _137=(_122||_123);
var _138=(_121||_124);
var _139=(_122||(_123&&_12a));
var _13a=this.getDimsObj(_133);
if(_121){
_12c[_128.cssPos]=(_133?"relative":"absolute");
}
var _13b=null,_13c=null;
if(_122){
if(_12a){
var _13d=this.getIFrames(false);
if(_13d&&_13d.iframes.length==1&&_129.iframesSize!=null&&_129.iframesSize.length==1){
var _13e=_129.iframesSize[0].h;
if(_13e!=null){
_13b=_13d.iframes[0];
_13c=(_135?_13e:(!_127.UAie?"100%":"99%"));
_126=false;
}
}
}
}
if(_139){
_12d=this.cNodeCss;
var _13f=_128.cssOx,_140=_128.cssOy;
if(_135&&!_12a){
_12c[_140]="visible";
_12d[_140]="visible";
}else{
_12c[_140]="hidden";
_12d[_140]=(!_12a?"auto":"hidden");
}
}
if(_138){
var lIdx=_128.cssL,_142=_128.cssLU;
var tIdx=_128.cssT,_144=_128.cssTU;
if(_133){
_12c[lIdx]="auto";
_12c[_142]="";
_12c[tIdx]="auto";
_12c[_144]="";
}else{
_12c[lIdx]=_13a.l;
_12c[_142]="px";
_12c[tIdx]=_13a.t;
_12c[_144]="px";
}
}
if(_137){
_12d=this.cNodeCss;
var hIdx=_128.cssH,_146=_128.cssHU;
if(_135&&_131==null){
_12c[hIdx]="";
_12c[_146]="";
_12d[hIdx]="";
_12d[_146]="";
}else{
var h=_13a.h;
var _148=_127.css.cssDis;
var _149;
var _14a;
if(_12d[_148]=="none"){
_149=_12b.tbNode.mBh;
_14a="";
_12d[_146]="";
}else{
_149=(h-_12b.dNode.lessH);
_14a=_149-_12b.cNode.lessH-_12b.cNode_mBh_LessBars;
_12d[_146]="px";
}
_12c[hIdx]=_149;
_12c[_146]="px";
_12d[hIdx]=_14a;
if(_131){
_131[hIdx]=_149;
_131[_146]="px";
_130=true;
if(_132){
_132[hIdx]=_14a;
_132[_146]=_12d[_146];
}
}
}
}
if(_136){
var w=_13a.w;
_12d=this.cNodeCss;
_12e=this.tbNodeCss;
_12f=this.rbNodeCss;
var wIdx=_128.cssW,_14d=_128.cssWU;
if(_134&&(!this.ie6||!w)){
_12c[wIdx]="";
_12c[_14d]="";
_12d[wIdx]="";
_12d[_14d]="";
if(_12e){
_12e[wIdx]="";
_12e[_14d]="";
}
if(_12f){
_12f[wIdx]="";
_12f[_14d]="";
}
}else{
var _14e=(w-_12b.dNode.lessW);
_12c[wIdx]=_14e;
_12c[_14d]="px";
_12d[wIdx]=_14e-_12b.cNode.lessW;
_12d[_14d]="px";
if(_12e){
_12e[wIdx]=_14e-_12b.tbNode.lessW;
_12e[_14d]="px";
}
if(_12f){
_12f[wIdx]=_14e-_12b.rbNode.lessW;
_12f[_14d]="px";
}
if(_131){
_131[wIdx]=_14e;
_131[_14d]="px";
_130=true;
if(_132){
_132[wIdx]=_12d[wIdx];
_132[_14d]=_12d[_14d];
}
}
}
}
if(!_126){
this.domNode.style.cssText=_12c.join("");
if(_12d){
this.containerNode.style.cssText=_12d.join("");
}
if(_12e){
this.tbNode.style.cssText=_12e.join("");
}
if(_12f){
this.rbNode.style.cssText=_12f.join("");
}
if(_130){
this.bgIframe.iframe.style.cssText=_131.join("");
if(_132){
_129.iframeCover.style.cssText=_132.join("");
}
}
}
if(_13b&&_13c){
this._deferSetIFrameH(_13b,_13c,false,50);
}
},_deferSetIFrameH:function(_14f,_150,_151,_152,_153){
if(!_152){
_152=100;
}
var pWin=this;
window.setTimeout(function(){
_14f.height=_150;
if(_151){
if(_153==null){
_153=50;
}
if(_153==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_153);
}
}
},_152);
},_getWindowMarginBox:function(_155,_156){
var _157=this.domNode;
if(_155==null){
_155=this._getLayoutInfo().dNode;
}
var _158=null;
if(_156.UAope){
_158=(this.posStatic?_156.page.layoutInfo.column:_156.page.layoutInfo.desktop);
}
return _156.ui.getMarginBox(_157,_155,_158,_156);
},_forceRefreshZIndex:function(){
var _159=jetspeed;
var zTop=this._setAsTopZIndex(_159.page,_159.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=zTop;
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},getIFrames:function(_15c){
var _15d=this.containerNode.getElementsByTagName("iframe");
if(_15d&&_15d.length>0){
if(!_15c){
return {iframes:_15d};
}
var _15e=[];
for(var i=0;i<_15d.length;i++){
var ifrm=_15d[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_15e.push({w:w,h:h});
}
return {iframes:_15d,iframesSize:_15e};
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
var _164=jetspeed;
var jsUI=_164.ui;
var _166=_164.page;
var _167=dojo;
var _168=_167.event;
var wDC=this.decConfig;
if(this.actionMenuWidget&&wDC&&wDC.windowActionMenuHasNoImg){
jsUI.evtDisconnect("after",this.tbNode,"oncontextmenu",this,"actionMenuOpen",_168);
}
_166.tooltipMgr.removeNodes(this.tooltips);
this.tooltips=ttps=null;
if(this.iframesInfo){
_166.unregPWinIFrameCover(this);
}
var _16a=this.actionButtons;
if(_16a){
var _16b=(wDC&&wDC.windowActionButtonTooltip);
for(var aNm in _16a){
var aBtn=_16a[aNm];
if(aBtn){
jsUI.evtDisconnect("after",aBtn,"onclick",this,"actionBtnClick",_168);
if(!_16b){
jsUI.evtDisconnect("after",aBtn,"onmousedown",_164,"_stopEvent",_168);
}
}
}
this.actionButtons=_16a=null;
}
if(this.drag){
this.drag.destroy(_167,_168,_164,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_168,_164,jsUI);
this.resizeHandle=null;
}
this._destroyChildWidgets(_167);
this._removeUntiledEvents();
var _16e=this.domNode;
if(_16e&&_16e.parentNode){
_16e.parentNode.removeChild(_16e);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},_destroyChildWidgets:function(_16f){
if(this.childWidgets){
var _170=this.childWidgets;
var _171=_170.length,_172,swT,swI;
_16f.debug("PortletWindow ["+this.widgetId+"] destroy child widgets ("+_171+")");
for(var i=(_171-1);i>=0;i--){
try{
_172=_170[i];
if(_172){
swT=_172.widgetType;
swI=_172.widgetId;
_172.destroy();
_16f.debug("destroyed child widget["+i+"]: "+swT+" "+swI);
}
_170[i]=null;
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
},endDragging:function(_177,_178,_179){
var _17a=jetspeed;
var ie6=this.ie6;
if(_178){
this.posStatic=false;
}else{
if(_179){
this.posStatic=true;
}
}
var _17c=this.posStatic;
if(!_17c){
var _17d=this.getDimsObj(_17c);
if(_177&&_177.left!=null&&_177.top!=null){
_17d.l=_177.left;
_17d.t=_177.top;
if(!_178){
this._alterCss(false,false,false,true,false,true);
}
}
if(_178){
this._updtDimsObj(false,false,true);
this._alterCss(true,true,false,true);
this._addUntiledEvents();
}
}else{
if(_179){
this._setAsTopZIndex(_17a.page,_17a.css,this.dNodeCss,_17c);
this._updtDimsObj(false,false);
}
if(!ie6){
this._alterCss(true);
this.resizeNotifyChildWidgets();
}else{
this._resetIE6TiledSize(_179);
}
}
if(this.portlet&&this.windowState!=_17a.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,_17a.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_17e){
var _17f=this.domNode;
var _180=this.posStatic;
if(!_17f){
return null;
}
var _181=_17f.style;
var _182={};
if(!_180){
_182.zIndex=_181.zIndex;
}
if(_17e){
return _182;
}
var _183=this.getDimsObj(_180);
_182.width=(_183.w?String(_183.w):"");
_182.height=(_183.h?String(_183.h):"");
_182[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_180;
_182[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_180){
_182.left=(_183.l!=null?String(_183.l):"");
_182.top=(_183.t!=null?String(_183.t):"");
}else{
var _184=jetspeed.page.getPortletCurColRow(_17f);
if(_184!=null){
_182.column=_184.column;
_182.row=_184.row;
_182.layout=_184.layout;
}else{
throw new Error("Can't find row/col/layout for window: "+this.widgetId);
}
}
return _182;
},getCurWinStateForPersist:function(_185){
var _186=this.getCurWinState(_185);
this._mkNumProp(null,_186,"left");
this._mkNumProp(null,_186,"top");
this._mkNumProp(null,_186,"width");
this._mkNumProp(null,_186,"height");
return _186;
},_mkNumProp:function(_187,_188,_189){
var _18a=(_188!=null&&_189!=null);
if(_187==null&&_18a){
_187=_188[_189];
}
if(_187==null||_187.length==0){
_187=0;
}else{
var _18b="";
for(var i=0;i<_187.length;i++){
var _18d=_187.charAt(i);
if((_18d>="0"&&_18d<="9")||_18d=="."){
_18b+=_18d.toString();
}
}
if(_18b==null||_18b.length==0){
_18b="0";
}
if(_18a){
_188[_189]=_18b;
}
_187=new Number(_18b);
}
return _187;
},setPortletContent:function(html,url){
var _190=jetspeed;
var _191=dojo;
var ie6=this.ie6;
var _193=null;
if(ie6){
_193=this.iNodeCss;
if(this.heightToFit){
this.iNodeCss=null;
this._alterCss(false,true);
}
}
var _194=html.toString();
if(!this.exclPContent){
_194="<div class=\"PContent\" >"+_194+"</div>";
}
var _195=this._splitAndFixPaths_scriptsonly(_194,url);
var _196=this.setContent(_195,_191);
this.childWidgets=((_196&&_196.length>0)?_196:null);
if(_195.scripts!=null&&_195.scripts.length!=null&&_195.scripts.length>0){
this._executeScripts(_195.scripts,_191);
this.onLoad();
}
if(_190.debug.setPortletContent){
_191.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
var _197=this.containerNode;
if(this.portlet){
this.portlet.postParseAnnotateHtml(_197);
}
var _198=this.iframesInfo;
var _199=this.getIFrames(true);
var _19a=null,_19b=false;
if(_199!=null){
if(_198==null){
this.iframesInfo=_198={};
var _19c=_197.ownerDocument.createElement("div");
var _19d="portletWindowIFrameCover";
_19c.className=_19d;
_197.appendChild(_19c);
if(_190.UAie){
_19c.className=(_19d+"IE")+" "+_19d;
if(ie6){
_198.iframeCoverIE6Css=_190.css.cssWidthHeight.concat();
}
}
_198.iframeCover=_19c;
_190.page.regPWinIFrameCover(this);
}
var _19e=_198.iframesSize=_199.iframesSize;
var _19f=_199.iframes;
var _1a0=_198.layout;
var _1a1=_198.layout=(_19f.length==1&&_19e[0].h!=null);
if(_1a0!=_1a1){
_19b=true;
}
if(_1a1){
if(!this.heightToFit){
_19a=_19f[0];
}
var wDC=this.decConfig;
var _197=this.containerNode;
_197.firstChild.className="PContent portletIFramePContent";
_197.className=wDC.cNodeClass+" portletWindowIFrameClient";
if(!wDC.layoutIFrame){
this._createLayoutInfo(wDC,true,this.domNode,_197,this.tbNode,this.rbNode,_191,_190,_190.ui);
}
}
}else{
if(_198!=null){
if(_198.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_19b=true;
}
this.iframesInfo=null;
_190.page.unregPWinIFrameCover(this);
}
}
if(_19b){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(ie6){
this._updtDimsObj(false,false,true,false,true);
if(_193==null){
var _1a3=_190.css;
_193=_1a3.cssHeight.concat();
_193[_1a3.cssDis]="inline";
}
this.iNodeCss=_193;
this._alterCss(false,false,true);
}
if(this.minimizeOnNextRender){
this.minimizeOnNextRender=false;
this.minimizeWindow(true);
this.actionBtnSync(_190,_190.id);
this.needsRenderOnRestore=true;
}
if(_19a){
this._deferSetIFrameH(_19a,(!_190.UAie?"100%":"99%"),true);
}
},setContent:function(data,_1a5){
var _1a6=null;
var step=1;
try{
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
step=2;
this._setContent(data.xml,_1a5);
step=3;
if(this.parseContent){
var node=this.containerNode;
var _1a9=new _1a5.xml.Parse();
var frag=_1a9.parseElement(node,null,true);
_1a6=_1a5.widget.getParser().createSubComponents(frag,null);
}
}
catch(e){
dojo.hostenv.println("ERROR in PortletWindow ["+this.widgetId+"] setContent while "+(step==1?"running onUnload":(step==2?"setting innerHTML":"creating dojo widgets"))+" - "+jetspeed.formatError(e));
}
return _1a6;
},_setContent:function(cont,_1ac){
this._destroyChildWidgets(_1ac);
try{
var node=this.containerNode;
while(node.firstChild){
_1ac.html.destroyNode(node.firstChild);
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
},_runStack:function(_1b0){
var st=this[_1b0];
var err="";
var _1b3=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_1b3);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_1b0]=[];
if(err.length){
var name=(_1b0=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_1b6,_1b7){
var self=this;
var _1b9=true;
var tmp="",code="";
for(var i=0;i<_1b6.length;i++){
if(_1b6[i].path){
_1b7.io.bind(this._cacheSetting({"url":_1b6[i].path,"load":function(type,_1be){
dojo.lang.hitch(self,tmp=";"+_1be);
},"error":function(type,_1c0){
_1c0.text=type+" downloading remote script";
self._handleDefaults.call(self,_1c0,"onExecError","debug");
},"mimetype":"text/plain","sync":true},_1b9));
code+=tmp;
}else{
code+=_1b6[i];
}
}
try{
if(this.scriptSeparation){
}else{
var djg=_1b7.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_1b7.doc();
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
},_cacheSetting:function(_1c4,_1c5){
var _1c6=dojo.lang;
for(var x in this.bindArgs){
if(_1c6.isUndefined(_1c4[x])){
_1c4[x]=this.bindArgs[x];
}
}
if(_1c6.isUndefined(_1c4.useCache)){
_1c4.useCache=_1c5;
}
if(_1c6.isUndefined(_1c4.preventCache)){
_1c4.preventCache=!_1c5;
}
if(_1c6.isUndefined(_1c4.mimetype)){
_1c4.mimetype="text/html";
}
return _1c4;
},_handleDefaults:function(e,_1c9,_1ca){
var _1cb=dojo;
if(!_1c9){
_1c9="onContentError";
}
if(_1cb.lang.isString(e)){
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
this[_1c9](e);
if(e.returnValue){
switch(_1ca){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_1cb.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_1cb.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_1cb);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_1ce){
if(_1ce){
this.title=_1ce;
}else{
this.title="";
}
if(this.windowInitialized&&this.tbTextNode){
this.tbTextNode.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _1d1=true;
var _1d2=[];
var _1d3=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _1d4=/src=(['"]?)([^"']*)\1/i;
while(match=_1d3.exec(s)){
if(_1d1&&match[1]){
if(attr=_1d4.exec(match[1])){
_1d2.push({path:attr[2]});
}
}
if(match[2]){
var sc=match[2];
if(!sc){
continue;
}
if(_1d1){
_1d2.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_1d2,"url":url};
},_IEPostDrag:function(){
if(!this.posStatic){
return;
}
var _1d6=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_1d6,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.showAllPortletWindows=function(){
var _1d7=jetspeed;
var _1d8=_1d7.css;
var _1d9=_1d8.cssDis,_1da=_1d8.cssNoSelNm,_1db=_1d8.cssNoSel,_1dc=_1d8.cssNoSelEnd;
var _1dd=_1d7.page.getPWins(false);
var _1de,_1df;
for(var i=0;i<_1dd.length;i++){
_1de=_1dd[i];
if(_1de){
_1df=_1de.dNodeCss;
_1df[_1da]="";
_1df[_1db]="";
_1df[_1dc]="";
_1df[_1d9]="block";
_1de.domNode.style.display="block";
_1de.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.hideAllPortletWindows=function(_1e1){
var _1e2=jetspeed;
var _1e3=_1e2.css;
var _1e4=_1e3.cssDis,_1e5=_1e3.cssNoSelNm,_1e6=_1e3.cssNoSel,_1e7=_1e3.cssNoSelEnd;
var _1e8=_1e2.page.getPWins(false);
var _1e9,_1ea,_1eb;
for(var i=0;i<_1e8.length;i++){
_1ea=_1e8[i];
_1e9=true;
if(_1ea&&_1e1&&_1e1.length>0){
for(var _1ed=0;_1ed<_1e1.length;_1ed++){
if(_1ea.widgetId==_1e1[_1ed]){
_1e9=false;
break;
}
}
}
if(_1ea){
_1eb=_1ea.dNodeCss;
_1eb[_1e5]="";
_1eb[_1e6]="";
_1eb[_1e7]="";
if(_1e9){
_1eb[_1e4]="none";
_1ea.domNode.style.display="none";
}else{
_1eb[_1e4]="block";
_1ea.domNode.style.display="block";
}
_1ea.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.WinScroller=function(){
var _1ee=this.jsObj;
this.UAmoz=_1ee.UAmoz;
this.UAope=_1ee.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{jsObj:jetspeed,djObj:dojo,typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _1f2=null;
if(this.UAmoz){
_1f2=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_1f2=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_1f2=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_1f2=b.clientHeight;
}
}
}
}
if(_1f2!=null&&e.clientY>_1f2-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_1f8,_1f9){
return ((_1f9!=null?(_1f9+"; "):"")+this.typeNm+" "+(_1f8==null?"<unknown>":_1f8.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_1fa,_1fb){
var _1fc=new jetspeed.widget.PortletWindowResizeHandle(_1fa,_1fb);
var doc=document;
var _1fe=doc.createElement("div");
_1fe.className=_1fc.rhClass;
var _1ff=doc.createElement("div");
_1fe.appendChild(_1ff);
_1fa.rbNode.appendChild(_1fe);
_1fc.domNode=_1fe;
_1fc.build();
return _1fc;
};
jetspeed.widget.PortletWindowResizeHandle=function(_200,_201){
this.pWin=_200;
_201.widget.WinScroller.call(this);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_202,_203,jsUI){
this._cleanUpLastEvt(_202,_203,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_202);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_205,_206,jsUI){
var _208=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_205);
this.tempEvents=null;
}
catch(ex){
_208=this._getErrMsg(ex,"event clean-up error",this.pWin,_208);
}
try{
_206.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_208=this._getErrMsg(ex,"clean-up error",this.pWin,_208);
}
if(_208!=null){
dojo.raise(_208);
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
var _20c=jetspeed;
var jsUI=_20c.ui;
var _20e=dojo;
var _20f=_20e.event;
var _210=_20c.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_20f,_20c,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_20e.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _213=[];
_213.push(jsUI.evtConnect("after",_210,"onmousemove",this,"_changeSizing",_20f,25));
_213.push(jsUI.evtConnect("after",_210,"onmouseup",this,"_endSizing",_20f));
_213.push(jsUI.evtConnect("after",d,"ondragstart",_20c,"_stopEvent",_20f));
_213.push(jsUI.evtConnect("after",d,"onselectstart",_20c,"_stopEvent",_20f));
_20c.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_213;
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
var _21a=pWin.posStatic;
if(_21a){
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
var _21d=jetspeed;
this._cleanUpLastEvt(dojo.event,_21d,_21d.ui);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
jetspeed.widget.BackgroundIframe=function(node,_21f,_220){
if(!_21f){
_21f=this.defaultStyleClass;
}
var html="<iframe src='' frameborder='0' scrolling='no' class='"+_21f+"'>";
this.iframe=_220.doc().createElement(html);
this.iframe.tabIndex=-1;
node.appendChild(this.iframe);
};
dojo.lang.extend(jetspeed.widget.BackgroundIframe,{defaultStyleClass:"ie6BackgroundIFrame",iframe:null});
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_222,_223,_224,_225,e,_227,_228,_229){
var jsUI=_229.ui;
var _22b=_228.event;
_229.widget.WinScroller.call(this);
if(_229.widget._movingInProgress){
if(djConfig.isDebug){
_229.debugAlert("ERROR - Mover initiation before previous Mover was destroyed");
}
}
_229.widget._movingInProgress=true;
this.moveInitiated=false;
this.moveableObj=_225;
this.windowOrLayoutWidget=_222;
this.node=_223;
this.nodeLayoutColumn=_224;
this.posStatic=_222.posStatic;
this.notifyOnAbsolute=_227;
if(e.ctrlKey&&_222.moveAllowTilingChg){
if(this.posStatic){
this.changeToUntiled=true;
}else{
if(_229.prefs.windowTiling){
this.changeToTiled=true;
this.changeToTiledStarted=false;
}
}
}
this.posRecord={};
this.disqualifiedColumnIndexes=(_224!=null)?_224.getDescendantCols():{};
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _22d=[];
var _22e=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_22b);
_22d.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_22b));
_22d.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_22b));
_22d.push(jsUI.evtConnect("after",doc,"ondragstart",_229,"_stopEvent",_22b));
_22d.push(jsUI.evtConnect("after",doc,"onselectstart",_229,"_stopEvent",_22b));
if(_229.UAie6){
_22d.push(jsUI.evtConnect("after",doc,"onmousedown",_229,"mouseUpDestroy",_22b));
}
_229.page.displayAllPWinIFrameCovers(false);
_22d.push(_22e);
this.events=_22d;
this.pSLastColChgIdx=null;
this.pSLastColChgTime=null;
this.pSLastNaturalColChgYTest=null;
this.pSLastNaturalColChgHistory=null;
this.pSLastNaturalColChgChoiceMap=null;
this.isDebug=false;
if(_229.debug.dragWindow){
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
this.devI=_229.debugindent;
this.devIH=_229.debugindentH;
this.devIT=_229.debugindentT;
this.devI3=_229.debugindent3;
this.devICH=_229.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",pSColChgTimeTh:3000,onMouseMove:function(e){
var _230=this.jsObj;
var _231=this.djObj;
var _232=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _234=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _237=this.isDebug;
var _238=false;
var _239=null,_23a=null,_23b,_23c,_23d,_23e,_23f;
if(_237){
_23b=this.devI;
_23c=this.devIH;
_23d=this.devI3;
_23e=this.devICH,_23f=this.devIT;
_239=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _240=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_240&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_239)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_238=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_238=true;
}
}
}
}
if(_232&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_234=true;
}
_230.ui.setMarginBox(this.node,x,y,null,null,this.nodeLayoutInfo,_230,_231);
var _241=this.posRecord;
_241.left=x;
_241.top=y;
var _242=false;
var _243=this.posStatic;
if(!_243){
if(!_234&&this.changeToTiled&&!this.changeToTiledStarted){
_242=true;
_243=true;
}
}
if(_243&&!_234){
var _244=this.columnInfoArray;
var _245=_230.page.columns;
var _246=this.heightHalf;
var _247=_245.length;
var _248=e.pageX;
var _249=y+_246;
var _24a=this.pSLastColChgIdx;
var _24b=this.pSLastNaturalColChgChoiceMap;
var _24c=null,_24d=[],_24e=null;
var _24f,_250,_251,_252,lowY,_254,_255,_256,_257;
for(var i=0;i<_247;i++){
_24f=_244[i];
if(_24f!=null){
if(_248>=_24f.left&&_248<=_24f.right){
if(_249>=(_24f.top-30)||(_24b!=null&&_24b[i]!=null)){
_250=Math.min(Math.abs(_249-(_24f.top)),Math.abs(e.pageY-(_24f.top)));
_251=Math.min(Math.abs(_249-(_24f.yhalf)),Math.abs(e.pageY-(_24f.yhalf)));
_252=Math.min(Math.abs(_249-_24f.bottom),Math.abs(e.pageY-_24f.bottom));
lowY=Math.min(_250,_251);
lowY=Math.min(lowY,_252);
_255=null;
_257=_24c;
while(_257!=null){
_256=_24d[_257];
if(lowY<_256.lowY){
break;
}else{
_255=_256;
_257=_256.nextIndex;
}
}
_24d.push({index:i,lowY:lowY,nextIndex:_257,lowYAlign:((!_237)?null:(lowY==_250?"^":(lowY==_251?"~":"_")))});
_254=(_24d.length-1);
if(_255!=null){
_255.nextIndex=_254;
}else{
_24c=_254;
}
if(i==_24a){
_24e=lowY;
}
}else{
if(_237){
if(_23a==null){
_23a=[];
}
var _259=(_24f.top-30)-_249;
_23a.push(_231.string.padRight(String(i),2,_23e)+" y! "+_231.string.padRight(String(_259),4,_23e));
}
}
}else{
if(_237&&_248>_24f.width){
if(_23a==null){
_23a=[];
}
var _259=_248-_24f.width;
_23a.push(_231.string.padRight(String(i),2,_23e)+" x! "+_231.string.padRight(String(_259),4,_23e));
}
}
}
}
var _25a=-1;
var _25b=-1,_25c=-1;
var _25d=null,_25e=null,_25f=null,_260=null,_261=null;
if(_24c!=null){
_256=_24d[_24c];
_25a=_256.index;
_25d=_256.lowY;
if(_256.nextIndex!=null){
_256=_24d[_256.nextIndex];
_25b=_256.index;
_25e=_256.lowY;
_260=_25e-_25d;
if(_256.nextIndex!=null){
_256=_24d[_256.nextIndex];
_25c=_256.index;
_25f=_256.lowY;
_261=_25f-_25d;
}
}
}
var _262=null;
var _263=(new Date().getTime());
var _264=this.pSLastNaturalColChgYTest;
if(_24e==null||(_264!=null&&Math.abs(_249-_264)>=Math.max((_246-Math.floor(_246*0.3)),Math.min(_246,21)))){
if(_25a>=0){
this.pSLastNaturalColChgYTest=_249;
this.pSLastNaturalColChgHistory=[_25a];
_24b={};
_24b[_25a]=true;
this.pSLastNaturalColChgChoiceMap=_24b;
}
}else{
if(_264==null){
this.pSLastNaturalColChgYTest=_249;
_25a=_24a;
this.pSLastNaturalColChgHistory=[_25a];
_24b={};
_24b[_25a]=true;
this.pSLastNaturalColChgChoiceMap=_24b;
}else{
var _265=null;
var _266=this.pSLastColChgTime+this.pSColChgTimeTh;
if(_266<_263){
var _267=this.pSLastNaturalColChgHistory;
var _268=(_267==null?0:_267.length);
var _269=null,_26a;
_257=_24c;
while(_257!=null){
_256=_24d[_257];
colI=_256.index;
if(_268==0){
_265=colI;
break;
}else{
_26a=false;
for(var i=(_268-1);i>=0;i--){
if(_267[i]==colI){
if(_269==null||_269>i){
_269=i;
_265=colI;
}
_26a=true;
break;
}
}
if(!_26a){
_265=colI;
break;
}
}
_257=_256.nextIndex;
}
if(_265!=null){
_25a=_265;
_24b[_25a]=true;
if(_268==0||_267[(_268-1)]!=_25a){
_267.push(_25a);
}
}
}else{
_25a=_24a;
}
if(_237&&_265!=null){
_231.hostenv.println(_23b+"ColChg YTest="+_264+" LeastRecentColI="+_265+" History=["+(this.pSLastNaturalColChgHistory?this.pSLastNaturalColChgHistory.join(", "):"")+"] Map={"+_230.printobj(this.pSLastNaturalColChgChoiceMap)+"} expire="+(_263-_266)+"}");
}
}
}
if(_237&&_262!=null){
if(this.devKeepLastMsg!=null){
_231.hostenv.println(this.devKeepLastMsg);
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
}
_231.hostenv.println(_262);
}
var col=(_25a>=0?_245[_25a]:null);
if(_237){
if(this.devLastColI!=_25a){
_238=true;
}
this.devLastColI=_25a;
}
var _26d=_230.widget.pwGhost;
if(_242){
if(col!=null){
_230.ui.setMarginBox(_26d,null,null,null,m.h,this.nodeLayoutInfo,_230,_231);
_26d.col=null;
this.changeToTiledStarted=true;
this.posStatic=true;
}
}
var _26e=null,_26f=false,_270=false;
if(_26d.col!=col&&col!=null){
this.pSLastColChgTime=_263;
this.pSLastColChgIdx=_25a;
var _271=_26d.col;
if(_271!=null){
_231.dom.removeNode(_26d);
}
_26d.col=col;
var _272=_244[_25a];
var _273=_272.childCount+1;
_272.childCount=_273;
if(_273==1){
_245[_25a].domNode.style.height="";
}
col.domNode.appendChild(_26d);
_270=true;
var _274=(_24a!=null?((_24a!=_25a)?_244[_24a]:null):(_271!=null?_244[_271.getPageColumnIndex()]:null));
if(_274!=null){
var _275=_274.childCount-1;
if(_275<0){
_275=0;
}
_274.childCount=_275;
if(_275==0){
_245[_274.pageColIndex].domNode.style.height="1px";
}
}
}
var _276=null,_277=null;
if(col!=null){
_276=_230.ui.getPWinAndColChildren(col.domNode,_26d,true,false,true,false);
_277=_276.matchingNodes;
}
if(_277!=null&&_277.length>1){
var _278=_276.matchNodeIndexInMatchingNodes;
var _279=-1;
var _27a=-1;
if(_278>0){
var _279=_231.html.getAbsolutePosition(_277[_278-1],true).y;
if((y-25)<=_279){
_231.dom.removeNode(_26d);
_26e=_277[_278-1];
_231.dom.insertBefore(_26d,_26e,true);
}
}
if(_278!=(_277.length-1)){
var _27a=_231.html.getAbsolutePosition(_277[_278+1],true).y;
if((y+10)>=_27a){
if(_278+2<_277.length){
_26e=_277[_278+2];
_231.dom.insertBefore(_26d,_26e,true);
}else{
col.domNode.appendChild(_26d);
_26f=true;
}
}
}
}
if(_238){
var _27b="";
if(_26e!=null||_26f||_270){
_27b="put=";
if(_26e!=null){
_27b+="before("+_26e.id+")";
}else{
if(_26f){
_27b+="end";
}else{
if(_270){
_27b+="end-default";
}
}
}
}
_231.hostenv.println(_23b+"col="+_25a+_23c+_27b+_23c+"x="+x+_23c+"y="+y+_23c+"ePGx="+e.pageX+_23c+"ePGy="+e.pageY+_23c+"yTest="+_249);
var _27c="",colI,_24f;
_257=_24c;
while(_257!=null){
_256=_24d[_257];
colI=_256.index;
_24f=_244[_256.index];
_27c+=(_27c.length>0?_23f:"")+colI+_256.lowYAlign+(colI<10?_23e:"")+" -> "+_231.string.padRight(String(_256.lowY),4,_23e);
_257=_256.nextIndex;
}
_231.hostenv.println(_23d+_27c);
if(_23a!=null){
var _27d="";
for(i=0;i<_23a.length;i++){
_27d+=(i>0?_23f:"")+_23a[i];
}
_231.hostenv.println(_23d+_27d);
}
this.devLastTime=_239;
this.devChgTh=this.devChgSubsqTh;
}
}
},onFirstMove:function(){
var _27e=this.jsObj;
var jsUI=_27e.ui;
var _280=this.djObj;
var _281=this.windowOrLayoutWidget;
var node=this.node;
var _283=_281._getLayoutInfoMoveable();
this.nodeLayoutInfo=_283;
var mP=_281._getWindowMarginBox(_283,_27e);
this.staticWidth=null;
var _285=_27e.widget.pwGhost;
var _286=this.UAmoz;
var _287=this.changeToUntiled;
var _288=this.changeToTiled;
var m=null;
if(this.posStatic){
if(!_287){
var _28a=_281.getPageColumnIndex();
var _28b=(_28a>=0?_27e.page.columns[_28a]:null);
_285.col=_28b;
this.pSLastColChgTime=new Date().getTime();
this.pSLastColChgIdx=_28a;
}
m={w:mP.w,h:mP.h};
var _28c=node.parentNode;
var _28d=document.getElementById(_27e.id.DESKTOP);
var _28e=node.style;
this.staticWidth=_28e.width;
var _28f=_280.html.getAbsolutePosition(node,true);
var _290=_283.mE;
m.l=_28f.left-_290.l;
m.t=_28f.top-_290.t;
if(_286){
if(!_287){
jsUI.setMarginBox(_285,null,null,null,mP.h,_283,_27e,_280);
}
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_28e.position="absolute";
if(!_287){
_28e.zIndex=_27e.page.getPWinHighZIndex()+1;
}else{
_28e.zIndex=(_281._setAsTopZIndex(_27e.page,_27e.css,_281.dNodeCss,false));
}
if(!_287){
_28c.insertBefore(_285,node);
if(!_286){
jsUI.setMarginBox(_285,null,null,null,mP.h,_283,_27e,_280);
}
_28d.appendChild(node);
var _291=jsUI.getPWinAndColChildren(_28c,_285,true,false,true);
this.prevColumnNode=_28c;
this.prevIndexInCol=_291.matchNodeIndexInMatchingNodes;
}else{
_281._updtDimsObj(false,true);
_28d.appendChild(node);
}
}else{
m=mP;
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
jsUI.evtDisconnectWObj(this.events.pop(),_280.event);
var _292=this.disqualifiedColumnIndexes;
var _293=(this.isDebug||_27e.debug.dragWindowStart),_294;
if(_293){
_294=_27e.debugindentT;
var _295=_27e.debugindentH;
var _296="";
if(_292!=null){
_296=_295+"dqCols=["+_27e.objectKeys(_292).join(", ")+"]";
}
var _297=_281.title;
if(_297==null){
_297=node.id;
}
_280.hostenv.println("DRAG \""+_297+"\""+_295+((this.posStatic&&!_287)?("col="+(_285.col?_285.col.getPageColumnIndex():"null")+_295):"")+"m.l = "+m.l+_295+"m.t = "+m.t+_296);
}
if(this.posStatic||_288){
this.heightHalf=mP.h/2;
if(!_287){
var _298=_27e.page.columns||[];
var _299=_298.length;
var _29a=new Array(_299);
var _29b=_280.byId(_27e.id.COLUMNS);
if(_29b){
var _29c=_27e.page.layoutInfo;
this._getChildColInfo(_29b,_29a,_27e.page.columns,_292,_29c,_29c.columns,_29c.desktop,node,(_293?1:null),_294,_280,_27e);
if(_293){
_280.hostenv.println(_294+"--------------------");
}
}
this.columnInfoArray=_29a;
}
}
if(this.posStatic){
jsUI.setMarginBox(node,m.l,m.t,mP.w,null,_283,_27e,_280);
if(this.notifyOnAbsolute){
_281.dragChangeToAbsolute(this,node,this.marginBox,_280,_27e);
}
if(_287){
this.posStatic=false;
}
}
},_getChildColInfo:function(_29d,_29e,_29f,_2a0,_2a1,_2a2,_2a3,_2a4,_2a5,_2a6,_2a7,_2a8){
var _2a9=_29d.childNodes;
var _2aa=(_2a9?_2a9.length:0);
if(_2aa==0){
return;
}
var _2ab=_2a7.html.getAbsolutePosition(_29d,true);
var _2ac=_2a8.ui.getMarginBox(_29d,_2a2,_2a3,_2a8);
var _2ad=_2a1.column;
var _2ae,col,_2b0,_2b1,_2b2,_2b3,_2b4,_2b5,_2b6,_2b7,_2b8,_2b9,_2ba;
var _2bb=null,_2bc=(_2a5!=null?(_2a5+1):null),_2bd,_2be;
for(var i=0;i<_2aa;i++){
_2ae=_2a9[i];
_2b0=_2ae.getAttribute("columnindex");
_2b1=(_2b0==null?-1:new Number(_2b0));
if(_2b1>=0){
_2b2=_2ae.getAttribute("layoutid");
_2b3=(_2b2!=null&&_2b2.length>0);
_2ba=true;
_2bd=_2bc;
_2be=null;
if(!_2b3&&(!(_2ae===_2a4))){
col=_29f[_2b1];
if(col&&!col.layoutActionsDisabled&&(_2a0==null||_2a0[_2b1]==null)){
_2b4=_2a8.ui.getMarginBox(_2ae,_2ad,_2a2,_2a8);
if(_2bb==null){
_2bb=_2b4.t-_2ac.t;
_2b9=_2ac.h-_2bb;
}
_2b5=_2ab.left+(_2b4.l-_2ac.l);
_2b6=_2ab.top+_2bb;
_2b7=_2b4.h;
if(_2b7<_2b9){
_2b7=_2b9;
}
if(_2b7<40){
_2b7=40;
}
var _2c0=_2ae.childNodes;
_2b8={left:_2b5,top:_2b6,right:(_2b5+_2b4.w),bottom:(_2b6+_2b7),childCount:(_2c0?_2c0.length:0),pageColIndex:_2b1};
_2b8.height=_2b8.bottom-_2b8.top;
_2b8.width=_2b8.right-_2b8.left;
_2b8.yhalf=_2b8.top+(_2b8.height/2);
_29e[_2b1]=_2b8;
_2ba=(_2b8.childCount>0);
if(_2a5!=null){
_2be=(_2a8.debugDims(_2b8,true)+" yhalf="+_2b8.yhalf+(_2b4.h!=_2b7?(" hreal="+_2b4.h):"")+" childC="+_2b8.childCount+"}");
}
}
}
if(_2a5!=null){
if(_2b3){
_2bd=_2bc+1;
}
if(_2be==null){
_2be="---";
}
_2a7.hostenv.println(_2a7.string.repeat(_2a6,_2a5)+"["+((_2b1<10?" ":"")+_2b0)+"] "+_2be);
}
if(_2ba){
this._getChildColInfo(_2ae,_29e,_29f,_2a0,_2a1,(_2b3?_2a1.columnLayoutHeader:_2ad),_2a2,_2a4,_2bd,_2a6,_2a7,_2a8);
}
}
}
},mouseUpDestroy:function(){
var _2c1=this.djObj;
var _2c2=this.jsObj;
this.destroy(_2c1,_2c1.event,_2c2,_2c2.ui);
},destroy:function(_2c3,_2c4,_2c5,jsUI){
var _2c7=this.windowOrLayoutWidget;
var node=this.node;
var _2c9=null;
if(this.moveInitiated&&_2c7&&node){
this.moveInitiated=false;
try{
if(this.posStatic){
var _2ca=_2c5.widget.pwGhost;
var _2cb=node.style;
if(_2ca&&_2ca.col){
_2c7.column=0;
_2c3.dom.insertBefore(node,_2ca,true);
}else{
if(this.prevColumnNode!=null&&this.prevIndexInCol!=null){
_2c3.dom.insertAtIndex(node,this.prevColumnNode,this.prevIndexInCol);
}else{
var _2cc=_2c5.page.getColumnDefault();
if(_2cc!=null){
_2c3.dom.prependChild(node,_2cc.domNode);
}
}
}
if(_2ca){
_2c3.dom.removeNode(_2ca);
}
}
_2c7.endDragging(this.posRecord,this.changeToUntiled,this.changeToTiled);
}
catch(ex){
_2c9=this._getErrMsg(ex,"destroy reset-window error",_2c7,_2c9);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_2c4);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_2c9=this._getErrMsg(ex,"destroy event clean-up error",_2c7,_2c9);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_2c5.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_2c9=this._getErrMsg(ex,"destroy clean-up error",_2c7,_2c9);
}
_2c5.widget._movingInProgress=false;
if(_2c9!=null){
_2c3.raise(_2c9);
}
}});
dojo.dnd.Moveable=function(_2cd,opt){
var _2cf=jetspeed;
var jsUI=_2cf.ui;
var _2d1=dojo;
var _2d2=_2d1.event;
this.windowOrLayoutWidget=_2cd;
this.handle=opt.handle;
var _2d3=[];
_2d3.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_2d2));
_2d3.push(jsUI.evtConnect("after",this.handle,"ondragstart",_2cf,"_stopEvent",_2d2));
_2d3.push(jsUI.evtConnect("after",this.handle,"onselectstart",_2cf,"_stopEvent",_2d2));
this.events=_2d3;
};
dojo.extend(dojo.dnd.Moveable,{minMove:5,enabled:true,mover:null,onMouseDown:function(e){
if(e&&e.button==2){
return;
}
var _2d5=dojo;
var _2d6=_2d5.event;
var _2d7=jetspeed;
var jsUI=jetspeed.ui;
if(this.mover!=null||this.tempEvents!=null){
this._cleanUpLastEvt(_2d5,_2d6,_2d7,jsUI);
_2d7.stopEvent(e);
}else{
if(this.enabled){
if(this.tempEvents!=null){
if(djConfig.isDebug){
_2d7.debugAlert("ERROR: Moveable onmousedown tempEvent already defined");
}
}else{
var _2d9=[];
var doc=this.handle.ownerDocument;
_2d9.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_2d6));
this.tempEvents=_2d9;
}
if(!this.windowOrLayoutWidget.posStatic){
this.windowOrLayoutWidget.bringToTop(e,false,true,_2d7);
}
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
_2d7.stopEvent(e);
},onMouseMove:function(e,_2dc){
var _2dd=jetspeed;
var _2de=dojo;
var _2df=_2de.event;
if(_2dc||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
this._cleanUpLastEvt(_2de,_2df,_2dd,_2dd.ui);
var _2e0=this.windowOrLayoutWidget;
var _2e1=null;
this.beforeDragColRowInfo=null;
if(!_2e0.isLayoutPane){
var _2e2=_2e0.domNode;
if(_2e2!=null){
this.node=_2e2;
this.mover=new _2de.dnd.Mover(_2e0,_2e2,_2e1,this,e,false,_2de,_2dd);
}
}else{
_2e0.startDragging(e,this,_2de,_2dd);
}
}
_2dd.stopEvent(e);
},onMouseUp:function(e){
var _2e4=dojo;
var _2e5=jetspeed;
this._cleanUpLastEvt(_2e4,_2e4.event,_2e5,_2e5.ui);
},_cleanUpLastEvt:function(_2e6,_2e7,_2e8,jsUI){
if(this._mDownEvt!=null){
_2e8.stopEvent(this._mDownEvt);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_2e6,_2e7,_2e8,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_2e7);
this.tempEvents=null;
},destroy:function(_2ea,_2eb,_2ec,jsUI){
this._cleanUpLastEvt(_2ea,_2eb,_2ec,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_2eb);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_2ef,_2f0){
var s=_2ef||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_2f0);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_2f0.UAmoz){
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
if(_2f0.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_2fb,_2fc){
var s=_2fb||dojo.gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_2fc.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_303,_304,_305,_306,_307,_308){
var s=_307||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_308);
if(_305!=null&&_305>=0){
_305=Math.max(_305-pb.w-mb.w,0);
}
if(_306!=null&&_306>=0){
_306=Math.max(_306-pb.h-mb.h,0);
}
dojo._setBox(node,_303,_304,_305,_306);
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
dojo._getPadExtents=function(n,_316){
var s=_316||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_31c){
var s=_31c||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_321,_322){
var s=_321||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_322.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_32a){
var ne="none",px=dojo._toPixelValue,s=_32a||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_333,_334){
return (parseFloat(_334)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_336,_337){
if(!_337){
return 0;
}
if(_337.slice&&(_337.slice(-2)=="px")){
return parseFloat(_337);
}
with(_336){
var _338=style.left;
var _339=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_337;
_337=style.pixelLeft;
}
catch(e){
_337=0;
}
style.left=_338;
runtimeStyle.left=_339;
}
return _337;
};
}
dojo.gcs=dojo.getComputedStyle;

