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
dojo.extend(jetspeed.widget.PortletWindow,{title:"",nextIndex:1,resizable:true,moveable:true,moveAllowTilingChg:true,posStatic:false,heightToFit:false,decName:null,decConfig:null,titlebarEnabled:true,resizebarEnabled:true,editPageEnabled:false,iframeCoverContainerClass:"portletWindowIFrameClient",colWidth_pbE:0,portlet:null,altInitParams:null,inContentChgd:false,exclPContent:false,minimizeTempRestore:null,executeScripts:false,scriptSeparation:false,adjustPaths:false,parseContent:true,childWidgets:null,dbProfile:(djConfig.isDebug&&jetspeed.debug.profile),dbOn:djConfig.isDebug,dbMenuDims:"Dump Dimensions",altInitParamsDef:function(_1,_2){
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
this.windowIndex=_12;
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
this.decName=_18;
var wDC=_9.loadPortletDecorationStyles(_18,_b);
if(wDC==null){
_11.raise("No portlet decoration is available: "+this.widgetId);
}
this.decConfig=wDC;
var _1a=wDC.dNodeClass;
var _1b=wDC.cNodeClass;
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
_2e=new _11.uri.Uri(_9.url.basePortalDesktopUrl()+wDC.windowIconPath+"/"+wI);
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
this._setupTitlebar(wDC,null,_15,_10,_f,_9,_a,_b,_e,_c,_11);
}
var _30=this.resizable;
var _31=null;
if(_30&&_1f){
var _32=_17+"_resize";
var _31=_9.widget.CreatePortletWindowResizeHandler(this,_9);
this.resizeHandle=_31;
if(_31){
_1f.appendChild(_31.domNode);
}
}else{
this.resizable=false;
}
_10.removeChild(_1c);
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _33=_9.css.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=false;
if(this.tbNodeCss){
this.tbNodeCss[_33]="none";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=false;
if(this.rbNodeCss){
this.rbNodeCss[_33]="none";
}
}
}
var _34=false;
var _35=_8.childNodes;
if(_25&&_35){
var _36=iP[_a.PP_ROW];
if(_36!=null){
var _37=new Number(_36);
if(_37>=0){
var _38=_35.length-1;
if(_38>=_37){
var _39=_35[_37];
if(_39){
_8.insertBefore(_1c,_39);
_34=true;
}
}
}
}
}
if(!_34){
_8.appendChild(_1c);
}
if(!wDC.layout){
var _3a="display: block; visibility: hidden; width: "+_27+"px"+((_28!=null&&_28>0)?("; height: "+_28+"px"):"");
_1c.style.cssText=_3a;
this._createLayoutInfo(wDC,false,_1c,_1d,_1e,_1f,_11,_9,_e);
}
if(_1e){
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
var _3c=_f.createElement("div");
_3c.id="pwGhost";
_3c.className=_1a;
_3c.style.position="static";
_3c.style.width="";
_3c.style.left="auto";
_3c.style.top="auto";
_9.widget.pwGhost=_3c;
}
if(ie6&&_9.widget.ie6ZappedContentHelper==null){
var _3d=_f.createElement("span");
_3d.id="ie6ZappedContentHelper";
_9.widget.ie6ZappedContentHelper=_3d;
}
},_buildActionStructures:function(wDC,_3f,_40,_41,_42,_43,_44){
var _45=new Array();
var aNm,_47,_48=false;
var _49=new Array();
var _4a=new Object();
var _4b=wDC.windowActionButtonOrder;
var _4c=wDC.windowActionMenuOrder;
var _4d=new Object();
var _4e=wDC.windowActionNoImage;
var _4f=wDC.windowActionButtonMax;
_4f=(_4f==null?-1:_4f);
if(_4c){
for(var aI=0;aI<_4c.length;aI++){
aNm=_4c[aI];
if(aNm){
_4d[aNm]=true;
}
}
}
if(_4b!=null){
for(var aI=(_4b.length-1);aI>=0;aI--){
aNm=_4b[aI];
_47=false;
if(_3f){
_47=true;
}else{
if(aNm==_42.ACT_MINIMIZE||aNm==_42.ACT_MAXIMIZE||aNm==_42.ACT_RESTORE||aNm==_42.ACT_MENU||_43.windowActionDesktop[aNm]!=null){
_47=true;
}
}
if(_47&&_4e&&_4e[aNm]){
if(!_4d[aNm]){
_49.push(aNm);
}
_47=false;
}
if(_47){
_45.push(aNm);
_4a[aNm]=true;
}
}
if(!_4a[_42.ACT_MENU]){
_48=true;
}
var _51=_45.length;
if(_4f!=-1&&_51>_4f){
var _52=0;
var _53=_51-_4f;
for(var j=0;j<2&&_52<_53;j++){
for(var i=(_45.length-1);i>=0&&_52<_53;i--){
aNm=_45[i];
if(aNm==null||aNm==_42.ACT_MENU){
continue;
}
if(j==0){
var _56=new RegExp("\b"+aNm+"\b");
if(_56.test(_43.windowActionNotPortlet)||aNm==_42.ACT_VIEW){
continue;
}
}
_49.push(aNm);
_45[i]=null;
delete _4a[aNm];
_52++;
}
}
}
}
var _57=new Array();
var _58=new Object();
var _59=_42.ACT_CHANGEPORTLETTHEME;
var _5a=_43.portletDecorationsAllowed;
if(_43.pageEditorLabels&&_5a&&_5a.length>1){
aNm=_59;
var _5b=_43.pageEditorLabels[aNm];
if(_5b){
_57.push(aNm);
_58[aNm];
this.actionLabels[aNm]=_5b;
}
}
for(var i=0;i<_49.length;i++){
aNm=_49[i];
if(aNm!=null&&!_58[aNm]&&!_4a[aNm]){
_57.push(aNm);
_58[aNm]=true;
}
}
if(_4c){
for(var aI=0;aI<_4c.length;aI++){
aNm=_4c[aI];
if(aNm!=null&&!_58[aNm]&&!_4a[aNm]&&(_3f||_43.windowActionDesktop[aNm])){
_57.push(aNm);
_58[aNm]=true;
}
}
}
if(this.dbOn){
_57.push({aNm:this.dbMenuDims,dev:true});
}
var _5c=null;
if(_57.length>0){
var _5d={};
var aNm,_5e,_5f,_60,_61,_62;
var _63=wDC.name+"_menu"+(!_3f?"Np":"");
var _64=_63;
_5c=_44.widget.createWidget("PopupMenu2",{id:_64,contextMenuForWindow:false},null);
_5c.onItemClick=function(mi){
var _aN=mi.jsActNm;
var _67=this.pWin;
if(!mi.jsActDev){
_67.actionProcess(_aN);
}else{
_67.actionProcessDev(_aN);
}
};
for(var i=0;i<_57.length;i++){
aNm=_57[i];
_61=null;
_62=false;
if(!aNm.dev){
_5e=this.actionLabels[aNm];
if(aNm==_59){
_61=_63+"_sub_"+aNm;
_60=_44.widget.createWidget("PopupMenu2",{id:_61,contextMenuForWindow:false},null);
_60.onItemClick=function(mi){
var _69=mi.jsPDecNm;
var _6a=_5c.pWin;
_6a.changeDecorator(_69);
};
for(var j=0;j<_5a.length;j++){
var _6b=_5a[j];
var _6c=_44.widget.createWidget("MenuItem2",{caption:_6b,jsPDecNm:_6b});
_60.addChild(_6c);
}
_40.appendChild(_60.domNode);
_41.ui.addPopupMenuWidget(_60);
}
}else{
_62=true;
_5e=aNm=aNm.aNm;
}
_5f=_44.widget.createWidget("MenuItem2",{caption:_5e,submenuId:_61,jsActNm:aNm,jsActDev:_62});
_5d[aNm]=_5f;
_5c.addChild(_5f);
}
_5c.menuItemsByName=_5d;
_40.appendChild(_5c.domNode);
_41.ui.addPopupMenuWidget(_5c);
}
wDC.windowActionMenuHasNoImg=_48;
if(_3f){
wDC.windowActionButtonNames=_45;
wDC.windowActionMenuNames=_57;
wDC.windowActionMenuWidget=_5c;
}else{
wDC.windowActionButtonNamesNp=_45;
wDC.windowActionMenuNamesNp=_57;
wDC.windowActionMenuWidgetNp=_5c;
}
return _45;
},_setupTitlebar:function(wDC,_6e,_6f,_70,doc,_72,_73,_74,_75,_76,_77){
var _78=_77.event;
var aNm;
var _7a=_76.tooltipMgr;
var _7b=this.tbNode;
var _7c=(_6e&&wDC);
if(_6e){
if(this.actionMenuWidget&&_6e.windowActionMenuHasNoImg){
_75.evtDisconnect("after",_7b,"oncontextmenu",this,"actionMenuOpen",_78);
}
_76.tooltipMgr.removeNodes(this.tooltips);
this.tooltips=ttps=[];
var _7d=this.actionButtons;
if(_7d){
var _7e=(_6e&&_6e.windowActionButtonTooltip);
for(aNm in _7d){
var _7f=_7d[aNm];
if(_7f){
_75.evtDisconnect("after",_7f,"onclick",this,"actionBtnClick",_78);
if(!_7e){
_75.evtDisconnect("after",_7f,"onmousedown",_72,"_stopEvent",_78);
}
if(_7c){
_77.dom.removeNode(_7f);
}
}
}
this.actionButtons=_7d={};
}
}
if(wDC){
if(wDC.windowActionButtonTooltip){
if(this.actionLabels[_73.ACT_DESKTOP_MOVE_TILED]!=null&&this.actionLabels[_73.ACT_DESKTOP_MOVE_UNTILED]!=null){
this.tooltips.push(_7a.addNode(_7b,null,true,1200,this,"getTitleBarTooltip",_72,_75,_78));
}
}
var _80=(_6f)?wDC.windowActionButtonNames:wDC.windowActionButtonNamesNp;
if(_80==null){
_80=this._buildActionStructures(wDC,_6f,_70,_72,_73,_74,_77);
}
for(var i=0;i<_80.length;i++){
aNm=_80[i];
if(aNm!=null){
if(!_6f||(aNm==_73.ACT_RESTORE||aNm==_73.ACT_MENU||_6f.getAction(aNm)!=null||_74.windowActionDesktop[aNm]!=null)){
this._createActionButtonNode(aNm,doc,_70,_7a,wDC,_72,_74,_75,_77,_78);
}
}
}
this.actionMenuWidget=(_6f)?wDC.windowActionMenuWidget:wDC.windowActionMenuWidgetNp;
if(this.actionMenuWidget&&wDC.windowActionMenuHasNoImg){
_75.evtConnect("after",_7b,"oncontextmenu",this,"actionMenuOpen",_78);
}
if(this.ie6&&!wDC._ie6used){
wDC._ie6used=true;
this.actionBtnSyncDefer(false,_72,_77);
}else{
this.actionBtnSync(_72,_73);
}
if(wDC.windowDisableResize){
this.resizable=false;
}
if(wDC.windowDisableMove){
this.moveable=false;
}
}
},_createActionButtonNode:function(aNm,doc,_84,_85,wDC,_87,_88,_89,_8a,_8b){
if(aNm!=null){
var _8c=doc.createElement("div");
_8c.className="portletWindowActionButton";
_8c.style.backgroundImage="url("+_88.getPortletDecorationBaseUrl(this.decName)+"/images/desktop/"+aNm+".gif)";
_8c.actionName=aNm;
this.actionButtons[aNm]=_8c;
this.tbNode.appendChild(_8c);
_89.evtConnect("after",_8c,"onclick",this,"actionBtnClick",_8b);
if(wDC.windowActionButtonTooltip){
var _8d=this.actionLabels[aNm];
this.tooltips.push(_85.addNode(_8c,_8d,true,null,null,null,_87,_89,_8b));
}else{
_89.evtConnect("after",_8c,"onmousedown",_87,"_stopEvent",_8b);
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
},_createLayoutInfo:function(_8e,_8f,_90,_91,_92,_93,_94,_95,_96){
var _97=_94.gcs(_90);
var _98=_94.gcs(_91);
var _99=_96.getLayoutExtents(_90,_97,_94,_95);
var _9a=_96.getLayoutExtents(_91,_98,_94,_95);
var _9b={dNode:_99,cNode:_9a};
var _9c=Math.max(0,_9a.mE.t);
var _9d=Math.max(0,_9a.mE.h-_9a.mE.t);
var _9e=0;
var _9f=0;
var _a0=null;
if(_92){
var _a1=_94.gcs(_92);
_a0=_96.getLayoutExtents(_92,_a1,_94,_95);
if(!_8e.dragCursor){
var _a2=_a1.cursor;
if(_a2==null||_a2.length==0){
_a2="move";
}
_8e.dragCursor=_a2;
}
_a0.mBh=_94.getMarginBox(_92,_a1,_95).h;
var _a3=Math.max(0,_a0.mE.h-_a0.mE.t);
_9e=(_a0.mBh-_a3)+Math.max(0,(_a3-_9c));
_9b.tbNode=_a0;
}
var _a4=null;
if(_93){
var _a5=_94.gcs(_93);
_a4=_96.getLayoutExtents(_93,_a5,_94,_95);
_a4.mBh=_94.getMarginBox(_93,_a5,_95).h;
var _a6=Math.max(0,_a4.mE.t);
_9f=(_a4.mBh-_a6)+Math.max(0,(_a6-_9d));
_9b.rbNode=_a4;
}
_9b.cNode_mBh_LessBars=_9e+_9f;
if(!_8f){
_8e.layout=_9b;
}else{
_8e.layoutIFrame=_9b;
}
},actionBtnClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.actionProcess(evt.target.actionName,evt);
},actionMenuOpen:function(evt){
var _a9=jetspeed;
var _aa=_a9.id;
var _ab=this.actionMenuWidget;
if(!_ab){
return;
}
if(_ab.isShowingNow){
_ab.close();
}
var _ac=null;
var _ad=null;
if(this.portlet){
_ac=this.portlet.getCurrentActionState();
_ad=this.portlet.getCurrentActionMode();
}
var _ae=_ab.menuItemsByName;
for(var aNm in _ae){
var _b0=_ae[aNm];
var _b1=(this._isActionEnabled(aNm,_ac,_ad,_a9,_aa))?"":"none";
_b0.domNode.style.display=_b1;
}
_ab.pWin=this;
_ab.onOpen(evt);
},actionProcessDev:function(aNm,evt){
if(aNm==this.dbMenuDims&&jetspeed.debugPWinPos){
jetspeed.debugPWinPos(this);
}
},actionProcess:function(aNm,evt){
var _b6=jetspeed;
var _b7=_b6.id;
if(aNm==null){
return;
}
if(_b6.prefs.windowActionDesktop[aNm]!=null){
if(aNm==_b7.ACT_DESKTOP_TILE){
this.makeTiled();
}else{
if(aNm==_b7.ACT_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(aNm==_b7.ACT_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(aNm==_b7.ACT_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false,false);
}
}
}
}
}else{
if(aNm==_b7.ACT_MENU){
this.actionMenuOpen(evt);
}else{
if(aNm==_b7.ACT_MINIMIZE){
if(this.portlet&&this.windowState==_b7.ACT_MAXIMIZE){
this.needsRenderOnRestore=true;
}
this.minimizeWindow();
if(this.portlet){
_b6.changeActionForPortlet(this.portlet.getId(),_b7.ACT_MINIMIZE,null);
}
if(!this.portlet){
this.actionBtnSyncDefer(false,_b6,dojo);
}
}else{
if(aNm==_b7.ACT_RESTORE){
var _b8=false;
if(this.portlet){
if(this.windowState==_b7.ACT_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_b8=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
this.portlet.renderAction(aNm);
}else{
_b6.changeActionForPortlet(this.portlet.getId(),_b7.ACT_RESTORE,null);
}
}
if(!_b8){
this.restoreWindow();
}
if(!this.portlet){
this.actionBtnSyncDefer(false,_b6,dojo);
}
}else{
if(aNm==_b7.ACT_MAXIMIZE){
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(aNm);
}else{
this.actionBtnSync(_b6,_b7);
}
}else{
if(aNm==_b7.ACT_REMOVEPORTLET){
if(this.portlet){
var _b9=dojo.widget.byId(_b7.PG_ED_WID);
if(_b9!=null){
_b9.deletePortlet(this.portlet.entityId,this.title);
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
},_isActionEnabled:function(aNm,_bb,_bc,_bd,_be){
var _bd=jetspeed;
var _be=_bd.id;
var _bf=false;
var _c0=this.windowState;
if(aNm==_be.ACT_MENU){
if(!this._actionMenuIsEmpty(_bd,_be)){
_bf=true;
}
}else{
if(_bd.prefs.windowActionDesktop[aNm]!=null){
if(this.getLayoutActionsEnabled()){
var _c1=(this.ie6&&_c0==_be.ACT_MINIMIZE);
if(aNm==_be.ACT_DESKTOP_HEIGHT_EXPAND){
if(!this.heightToFit&&!_c1){
_bf=true;
}
}else{
if(aNm==_be.ACT_DESKTOP_HEIGHT_NORMAL){
if(this.heightToFit&&!_c1){
_bf=true;
}
}else{
if(aNm==_be.ACT_DESKTOP_TILE&&_bd.prefs.windowTiling){
if(!this.posStatic){
_bf=true;
}
}else{
if(aNm==_be.ACT_DESKTOP_UNTILE){
if(this.posStatic){
_bf=true;
}
}
}
}
}
}
}else{
if(aNm==_be.ACT_CHANGEPORTLETTHEME){
if(this.cP_D&&this.editPageEnabled&&this.getLayoutActionsEnabled()){
_bf=true;
}
}else{
if(aNm==this.dbMenuDims){
_bf=true;
}else{
if(this.minimizeTempRestore!=null){
if(this.portlet){
var _c2=this.portlet.getAction(aNm);
if(_c2!=null){
if(_c2.id==_be.ACT_REMOVEPORTLET){
if(_bd.page.editMode&&this.getLayoutActionsEnabled()){
_bf=true;
}
}
}
}
}else{
if(this.portlet){
var _c2=this.portlet.getAction(aNm);
if(_c2!=null){
if(_c2.id==_be.ACT_REMOVEPORTLET){
if(_bd.page.editMode&&this.getLayoutActionsEnabled()){
_bf=true;
}
}else{
if(_c2.type==_be.PORTLET_ACTION_TYPE_MODE){
if(aNm!=_bc){
_bf=true;
}
}else{
if(aNm!=_bb){
_bf=true;
}
}
}
}
}else{
if(aNm==_be.ACT_MAXIMIZE){
if(aNm!=_c0&&this.minimizeTempRestore==null){
_bf=true;
}
}else{
if(aNm==_be.ACT_MINIMIZE){
if(aNm!=_c0){
_bf=true;
}
}else{
if(aNm==_be.ACT_RESTORE){
if(_c0==_be.ACT_MAXIMIZE||_c0==_be.ACT_MINIMIZE){
_bf=true;
}
}else{
if(aNm==this.dbMenuDims){
_bf=true;
}
}
}
}
}
}
}
}
}
}
return _bf;
},_actionMenuIsEmpty:function(_c3,_c4){
var _c5=true;
var _c6=this.actionMenuWidget;
if(_c6){
var _c7=null;
var _c8=null;
if(this.portlet){
_c7=this.portlet.getCurrentActionState();
_c8=this.portlet.getCurrentActionMode();
}
for(var aNm in _c6.menuItemsByName){
if(aNm!=_c4.ACT_MENU&&this._isActionEnabled(aNm,_c7,_c8,_c3,_c4)){
_c5=false;
break;
}
}
}
return _c5;
},actionBtnSyncDefer:function(_ca,_cb,_cc){
if(_ca&&_cb.UAie){
_ca=false;
}
if(_ca){
var _cd=_cc.gcs(this.domNode).opacity;
if(typeof _cd=="undefined"||_cd==null){
_ca=false;
}else{
_cd=Number(_cd);
this._savedOpacity=_cd;
var _ce=_cd-0.005;
_ce=((_ce<=0.1)?(_cd+0.005):_ce);
this.domNode.style.opacity=_ce;
_cc.lang.setTimeout(this,this._actionBtnSyncRepaint,20);
}
}
if(!_ca){
_cc.lang.setTimeout(this,this.actionBtnSync,10);
}
},_actionBtnSyncRepaint:function(_cf,_d0){
this.actionBtnSync(_cf,_d0);
if(this._savedOpacity!=null){
this.domNode.style.opacity=this._savedOpacity;
delete this._savedOpacity;
}
},actionBtnSync:function(_d1,_d2){
if(!_d1){
_d1=jetspeed;
_d2=_d1.id;
}
var _d3=null;
var _d4=null;
if(this.portlet){
_d3=this.portlet.getCurrentActionState();
_d4=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionButtons){
var _d6=this._isActionEnabled(aNm,_d3,_d4,_d1,_d2);
var _d7=this.actionButtons[aNm];
_d7.style.display=(_d6)?"block":"none";
}
},_postCreateMaximizeWindow:function(){
var _d8=jetspeed;
var _d9=_d8.id;
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(_d9.ACT_MAXIMIZE);
}else{
this.actionBtnSync(_d8,_d9);
}
},minimizeWindowTemporarily:function(_da){
var _db=jetspeed;
var _dc=_db.id;
if(_da){
this.needsRenderOnRestore=true;
}
if(!this.minimizeTempRestore){
this.minimizeTempRestore=this.windowState;
if(this.windowState!=_dc.ACT_MINIMIZE){
this.minimizeWindow(false);
}
this.actionBtnSync(_db,_dc);
}
},restoreAllFromMinimizeWindowTemporarily:function(){
var _dd=jetspeed;
var _de=_dd.id;
var _df=_de.ACT_MINIMIZE,_e0=_de.ACT_MAXIMIZE;
var _e1;
var _e2=[];
var _e3=null;
var _e4=_dd.page.getPWins();
for(var i=0;i<_e4.length;i++){
_e1=_e4[i];
var _e6=_e1.minimizeTempRestore;
delete _e1.minimizeTempRestore;
if(_e6){
if(_e6==_e0){
_e3=_e1;
}
if(_e6==_df){
}else{
if(_e1.needsRenderOnRestore&&_e1.portlet){
deferRestoreWindow=true;
if(_e6!=_e0){
_e1.restoreOnNextRender=true;
}
delete _e1.needsRenderOnRestore;
_e1.portlet.renderAction(_e6);
}else{
_e1.restoreWindow();
if(!_e1.portlet){
_e1.actionBtnSyncDefer(false,_dd,dojo);
}
}
}
_e1.actionBtnSync(_dd,_de);
}
if(_e1.ie6&&_e1.posStatic){
var _e7=_e1.domNode.parentNode;
var _e8=false;
for(var j=0;j<_e2.length;j++){
if(_e2[j]==_e7){
_e8=true;
break;
}
}
if(!_e8){
_e2.push(_e7);
}
}
}
_dd.widget.showAllPortletWindows();
if(_e3!=null){
_e3.maximizeWindow();
}
if(_dd.UAie6){
if(_e2.length>0){
var _ea=new jetspeed.widget.IE6ZappedContentRestorer(_e2);
dojo.lang.setTimeout(_ea,_ea.showNext,20);
}
}
},minimizeWindow:function(_eb){
if(!this.tbNode){
return;
}
var _ec=jetspeed;
if(this.windowState==jetspeed.id.ACT_MAXIMIZE){
_ec.widget.showAllPortletWindows();
this.restoreWindow();
}else{
if(!_eb){
this._updtDimsObj(false,false);
}
}
var _ed=_ec.css.cssDis;
this.cNodeCss[_ed]="none";
if(this.rbNodeCss){
this.rbNodeCss[_ed]="none";
}
this.windowState=_ec.id.ACT_MINIMIZE;
if(this.ie6){
this.containerNode.style.display="none";
}
this._alterCss(true,true);
},maximizeWindow:function(){
var _ee=jetspeed;
var _ef=_ee.id;
var _f0=this.domNode;
var _f1=[this.widgetId];
_ee.widget.hideAllPortletWindows(_f1);
if(this.windowState==_ef.ACT_MINIMIZE){
this.restoreWindow();
}
var _f2=this.posStatic;
this.preMaxPosStatic=_f2;
this.preMaxHeightToFit=this.heightToFit;
var _f3=_f2;
this._updtDimsObj(false,_f3);
var _f4=document.getElementById(_ef.DESKTOP);
var _f5=dojo.html.getAbsolutePosition(_f4,true).y;
var _f6=dojo.html.getViewport();
var _f7=dojo.html.getPadding(_ee.docBody);
this.dimsUntiledTemp={w:_f6.width-_f7.width-2,h:_f6.height-_f7.height-_f5,l:1,t:_f5};
this._setTitleBarDragging(true,_ee.css,false);
this.posStatic=false;
this.heightToFit=false;
this._alterCss(true,true);
if(_f2){
_f4.appendChild(_f0);
}
this.windowState=_ef.ACT_MAXIMIZE;
},restoreWindow:function(){
var _f8=jetspeed;
var _f9=_f8.id;
var _fa=_f8.css;
var _fb=this.domNode;
var _fc=false;
if(_fb.style.position=="absolute"){
_fc=true;
}
var _fd=null;
if(this.windowState==_f9.ACT_MAXIMIZE){
_f8.widget.showAllPortletWindows();
this.posStatic=this.preMaxPosStatic;
this.heightToFit=this.preMaxHeightToFit;
this.dimsUntiledTemp=null;
}
var _fe=_fa.cssDis;
this.cNodeCss[_fe]="block";
if(this.rbNodeCss&&this.resizebarEnabled){
this.rbNodeCss[_fe]="block";
}
this.windowState=_f9.ACT_RESTORE;
this._setTitleBarDragging(true,_f8.css);
var ie6=this.ie6;
if(!ie6){
this._alterCss(true,true);
}else{
var _100=null;
if(this.heightToFit){
_100=this.iNodeCss;
this.iNodeCss=null;
}
this._alterCss(true,true);
this._updtDimsObj(false,false,true,false,true);
if(_100!=null){
this.iNodeCss=_100;
}
this._alterCss(false,false,true);
}
if(this.posStatic&&_fc){
this._tileWindow(_f8);
}
},_tileWindow:function(_101){
if(!this.posStatic){
return;
}
var _102=this.domNode;
var _103=this.getDimsObj(this.posStatic);
var _104=true;
if(_103!=null){
var _105=_103.colInfo;
if(_105!=null&&_105.colI!=null){
var _106=_101.page.columns[_105.colI];
var _107=((_106!=null)?_106.domNode:null);
if(_107!=null){
var _108=null;
var _109=_107.childNodes.length;
if(_109==0){
_107.appendChild(_102);
_104=false;
}else{
var _10a,_10b,_10c=0;
if(_105.pSibId!=null||_105.nSibId!=null){
_10a=_107.firstChild;
do{
_10b=_10a.id;
if(_10b==null){
continue;
}
if(_10b==_105.pSibId){
dojo.dom.insertAfter(_102,_10a);
_104=false;
}else{
if(_10b==_105.nSibId){
dojo.dom.insertBefore(_102,_10a);
_104=false;
}else{
if(_10c==_105.elmtI){
_108=_10a;
}
}
}
_10a=_10a.nextSibling;
_10c++;
}while(_104&&_10a!=null);
}
}
if(_104){
if(_108!=null){
dojo.dom.insertBefore(_102,_108);
}else{
dojo.dom.prependChild(_102,_107);
}
_104=false;
}
}
}
}
if(_104){
var _10d=_101.page.getColumnDefault();
if(_10d!=null){
dojo.dom.prependChild(_102,_10d.domNode);
}
}
},getDimsObj:function(_10e,_10f){
return (_10e?((this.dimsTiledTemp!=null&&!_10f)?this.dimsTiledTemp:this.dimsTiled):((this.dimsUntiledTemp!=null&&!_10f)?this.dimsUntiledTemp:this.dimsUntiled));
},_updtDimsObj:function(_110,_111,_112,_113,_114,_115){
var _116=jetspeed;
var _117=dojo;
var _118=this.domNode;
var _119=this.posStatic;
var _11a=this.getDimsObj(_119,_115);
var _11b=(!_112&&!_119&&(!_110||_11a.l==null||_11a.t==null));
var _11c=(!_113&&(!_110||_11b||_114||_11a.w==null||_11a.h==null));
if(_11c||_11b){
var _11d=this._getLayoutInfo().dNode;
if(_11c){
var _11e=_116.ui.getMarginBoxSize(_118,_11d);
_11a.w=_11e.w;
_11a.h=_11e.h;
if(!_119){
_11b=true;
}
}
if(_11b){
var _11f=_117.html.getAbsolutePosition(_118,true);
_11a.l=_11f.x-_11d.mE.l-_11d.pbE.l;
_11a.t=_11f.y-_11d.mE.t-_11d.pbE.t;
}
}
if(_119){
if(_111||_115&&_11a.colInfo==null){
var _120=0,_121=_118.previousSibling,_122=_118.nextSibling;
var _123=(_121!=null?_121.id:null),_124=(_122!=null?_122.id:null);
if(_121!=null){
_123=_121.id;
}
while(_121!=null){
_120++;
_121=_121.previousSibling;
}
_11a.colInfo={elmtI:_120,pSibId:_123,nSibId:_124,colI:this.getPageColumnIndex()};
}
if(_115){
this.dimsTiledTemp={w:_11a.w,h:_11a.h,colInfo:_11a.colInfo};
_11a=this.dimsTiledTemp;
}
}else{
if(_115){
this.dimsUntiledTemp={w:_11a.w,h:_11a.h,l:_11a.l,t:_11a.t};
_11a=this.dimsUntiledTemp;
}
}
return _11a;
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACT_MAXIMIZE&&(this.portlet==null||(!this.portlet.layoutActionsDisabled||(this.cL_NA_ED==true))));
},_setTitleBarDragging:function(_125,_126,_127){
var _128=this.tbNode;
if(!_128){
return;
}
if(typeof _127=="undefined"){
_127=this.getLayoutActionsEnabled();
}
var _129=this.resizeHandle;
var _12a=null;
var wDC=this.decConfig;
var _12c=_127;
if(_12c&&!this.resizebarEnabled){
_12c=false;
}
if(_127&&!this.titlebarEnabled){
_127=false;
}
if(_127){
_12a=wDC.dragCursor;
if(this.drag){
this.drag.enable();
}
}else{
_12a="default";
if(this.drag){
this.drag.disable();
}
}
if(_12c){
if(_129){
_129.domNode.style.display="";
}
}else{
if(_129){
_129.domNode.style.display="none";
}
}
this.tbNodeCss[_126.cssCur]=_12a;
if(!_125){
_128.style.cursor=_12a;
}
},onMouseDown:function(evt){
this.bringToTop(evt,false,false,jetspeed);
},bringToTop:function(evt,_12f,_130,_131){
if(!this.posStatic){
var _132=_131.page;
var _133=_131.css;
var _134=this.dNodeCss;
var _135=_132.getPWinHighZIndex();
var zCur=_134[_133.cssZIndex];
if(_135!=zCur){
var zTop=this._setAsTopZIndex(_132,_133,_134,false);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
if(!_130&&this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
}
}
}else{
if(_12f){
var zTop=this._setAsTopZIndex(_132,_133,_134,true);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
}
}
}
},_setAsTopZIndex:function(_138,_139,_13a,_13b){
var zTop=String(_138.getPWinTopZIndex(_13b));
_13a[_139.cssZIndex]=zTop;
return zTop;
},makeUntiled:function(){
var _13d=jetspeed;
this._updtDimsObj(false,true);
this.posStatic=false;
this._updtDimsObj(true,false);
this._setAsTopZIndex(_13d.page,_13d.css,this.dNodeCss,false);
this._alterCss(true,true);
var _13e=this.domNode.parentNode;
var _13f=document.getElementById(jetspeed.id.DESKTOP);
_13f.appendChild(this.domNode);
_13d.page.columnEmptyCheck(_13e);
if(this.windowState==_13d.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},makeTiled:function(){
this.posStatic=true;
var _140=jetspeed;
this._setAsTopZIndex(_140.page,_140.css,this.dNodeCss,true);
this._alterCss(true,true);
this._tileWindow(_140);
_140.page.columnEmptyCheck(this.domNode.parentNode);
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
},makeHeightToFit:function(_141){
var _142=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
if(this.ie6){
var _143=this.iNodeCss;
this.iNodeCss=null;
this._alterCss(false,true);
this._updtDimsObj(false,false,true,false,true);
this.iNodeCss=_143;
}
this._alterCss(false,true);
if(!_141&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_144,_145){
var _146=this.getDimsObj(this.posStatic);
var _147=this._getLayoutInfo().dNode;
var _148=jetspeed.ui.getMarginBoxSize(this.domNode,_147);
_146.w=_148.w;
_146.h=_148.h;
this.heightToFit=false;
this._alterCss(false,true);
if(!_145&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_144&&this.portlet){
this.portlet.submitWinState();
}
},editPageInitiate:function(cP_D,_14a,_14b,_14c,_14d){
this.editPageEnabled=true;
this.cP_D=cP_D;
this.cL_NA_ED=_14a;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _14f=_14c.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_14f]="block";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=true;
if(this.rbNodeCss&&this.windowState!=_14b.id.ACT_MINIMIZE){
this.rbNodeCss[_14f]="block";
}
}
this._setTitleBarDragging(false,_14c);
if(!_14d){
this._alterCss(true,true);
}
}else{
this._setTitleBarDragging(false,_14c);
}
},editPageTerminate:function(_150,_151){
this.editPageEnabled=false;
delete this.cP_D;
delete this.cL_NA_ED;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _153=_150.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=false;
if(this.tbNodeCss){
this.tbNodeCss[_153]="none";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=false;
if(this.rbNodeCss){
this.rbNodeCss[_153]="none";
}
}
this._setTitleBarDragging(false,_150);
if(!_151){
this._alterCss(true,true);
}
}else{
this._setTitleBarDragging(false,_150);
}
},changeDecorator:function(_154){
var _155=jetspeed;
var _156=_155.css;
var jsId=_155.id;
var jsUI=_155.ui;
var _159=_155.prefs;
var _15a=dojo;
var _15b=this.decConfig;
if(_15b&&_15b.name==_154){
return;
}
var wDC=_155.loadPortletDecorationStyles(_154,_159);
if(!wDC){
return;
}
var _15d=this.portlet;
if(_15d){
_15d._submitAjaxApi("updatepage","&method=update-portlet-decorator&portlet-decorator="+_154);
}
this.decConfig=wDC;
this.decName=wDC.name;
var _15e=this.domNode;
var _15f=this.containerNode;
var _160=this.iframesInfo;
var _161=(_160&&_160.layout);
var _162=(!_161?wDC.layout:wDC.layoutIFrame);
if(!_162){
if(!_161){
this._createLayoutInfo(wDC,false,_15e,_15f,this.tbNode,this.rbNode,_15a,_155,jsUI);
}else{
this._createLayoutInfo(wDC,true,_15e,_15f,this.tbNode,this.rbNode,_15a,_155,jsUI);
}
}
this._setupTitlebar(wDC,_15b,this.portlet,_155.docBody,document,_155,_155.id,_159,jsUI,_155.page,_15a);
_15e.className=wDC.dNodeClass;
if(_161){
_15f.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
}else{
_15f.className=wDC.cNodeClass;
}
var _163=_156.cssDis;
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_163]="block";
}
this.resizebarEnabled=true;
if(this.rbNodeCss&&this.windowState!=jsId.ACT_MINIMIZE){
this.rbNodeCss[_163]="block";
}
if(this.editPageEnabled){
this.editPageInitiate(this.cP_D,this.cL_NA_ED,_155,_156,true);
}else{
this.editPageTerminate(_156,true);
}
this._setTitleBarDragging(true,_156);
this._alterCss(true,true);
},resizeTo:function(w,h,_166){
var _167=this.getDimsObj(this.posStatic);
_167.w=w;
_167.h=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _168=this.resizeHandle;
if(_168!=null&&_168._isSizing){
jetspeed.ui.evtConnect("after",_168,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
this.resizeNotifyChildWidgets();
},resizeNotifyChildWidgets:function(){
if(this.childWidgets){
var _169=this.childWidgets;
var _16a=_169.length,_16b;
for(var i=0;i<_16a;i++){
try{
_16b=_169[i];
if(_16b){
_16b.checkSize();
}
}
catch(e){
}
}
}
},_getLayoutInfo:function(){
var _16d=this.iframesInfo;
return ((!(_16d&&_16d.layout))?this.decConfig.layout:this.decConfig.layoutIFrame);
},_getLayoutInfoMoveable:function(){
return this._getLayoutInfo().dNode;
},onBrowserWindowResize:function(){
if(this.ie6){
this._resetIE6TiledSize(false);
}
},_resetIE6TiledSize:function(_16e){
var _16f=this.posStatic;
if(_16f){
var _170=this.domNode;
var _171=this.getDimsObj(_16f);
_171.w=Math.max(0,this.domNode.parentNode.offsetWidth-this.colWidth_pbE);
this._alterCss(_16e,false,false,false,true);
}
},_alterCss:function(_172,_173,_174,_175,_176,_177){
var _178=jetspeed;
var _179=_178.css;
var _17a=this.iframesInfo;
var _17b=(_17a&&_17a.layout);
var _17c=(!_17b?this.decConfig.layout:this.decConfig.layoutIFrame);
var _17d=this.dNodeCss,_17e=null,_17f=null,_180=null,_181=false,_182=this.iNodeCss,_183=null;
if(_182&&_17b){
_183=_17a.iframeCoverIE6Css;
}
var _184=this.posStatic;
var _185=(_184&&_182==null);
var _186=this.heightToFit;
var _187=(_172||_176||(_174&&!_185));
var _188=(_173||_174);
var _189=(_172||_175);
var _18a=(_173||(_174&&_17b));
var _18b=this.getDimsObj(_184);
if(_172){
_17d[_179.cssPos]=(_184?"relative":"absolute");
}
var _18c=null,_18d=null;
if(_173){
if(_17b){
var _18e=this.getIFramesAndObjects(false,true);
if(_18e&&_18e.iframes&&_18e.iframes.length==1&&_17a.iframesSize&&_17a.iframesSize.length==1){
var _18f=_17a.iframesSize[0].h;
if(_18f!=null){
_18c=_18e.iframes[0];
_18d=(_186?_18f:(!_178.UAie?"100%":"99%"));
_177=false;
}
}
}
}
if(_18a){
_17e=this.cNodeCss;
var _190=_179.cssOx,_191=_179.cssOy;
if(_186&&!_17b){
_17d[_191]="hidden";
_17e[_191]="visible";
}else{
_17d[_191]="hidden";
_17e[_191]=(!_17b?"auto":"hidden");
}
}
if(_189){
var lIdx=_179.cssL,_193=_179.cssLU;
var tIdx=_179.cssT,_195=_179.cssTU;
if(_184){
_17d[lIdx]="auto";
_17d[_193]="";
_17d[tIdx]="auto";
_17d[_195]="";
}else{
_17d[lIdx]=_18b.l;
_17d[_193]="px";
_17d[tIdx]=_18b.t;
_17d[_195]="px";
}
}
if(_188){
_17e=this.cNodeCss;
var hIdx=_179.cssH,_197=_179.cssHU;
if(_186&&_182==null){
_17d[hIdx]="";
_17d[_197]="";
_17e[hIdx]="";
_17e[_197]="";
}else{
var h=_18b.h;
var _199=_178.css.cssDis;
var _19a;
var _19b;
if(_17e[_199]=="none"){
_19a=_17c.tbNode.mBh;
_19b="";
_17e[_197]="";
}else{
_19a=(h-_17c.dNode.lessH);
_19b=_19a-_17c.cNode.lessH-_17c.cNode_mBh_LessBars;
_17e[_197]="px";
}
_17d[hIdx]=_19a;
_17d[_197]="px";
_17e[hIdx]=_19b;
if(_182){
_182[hIdx]=_19a;
_182[_197]="px";
_181=true;
if(_183){
_183[hIdx]=_19b;
_183[_197]=_17e[_197];
}
}
}
}
if(_187){
var w=_18b.w;
_17e=this.cNodeCss;
_17f=this.tbNodeCss;
_180=this.rbNodeCss;
var wIdx=_179.cssW,_19e=_179.cssWU;
if(_185&&(!this.ie6||!w)){
_17d[wIdx]="";
_17d[_19e]="";
_17e[wIdx]="";
_17e[_19e]="";
if(_17f){
_17f[wIdx]="";
_17f[_19e]="";
}
if(_180){
_180[wIdx]="";
_180[_19e]="";
}
}else{
var _19f=(w-_17c.dNode.lessW);
_17d[wIdx]=_19f;
_17d[_19e]="px";
_17e[wIdx]=_19f-_17c.cNode.lessW;
_17e[_19e]="px";
if(_17f){
_17f[wIdx]=_19f-_17c.tbNode.lessW;
_17f[_19e]="px";
}
if(_180){
_180[wIdx]=_19f-_17c.rbNode.lessW;
_180[_19e]="px";
}
if(_182){
_182[wIdx]=_19f;
_182[_19e]="px";
_181=true;
if(_183){
_183[wIdx]=_17e[wIdx];
_183[_19e]=_17e[_19e];
}
}
}
}
if(!_177){
this.domNode.style.cssText=_17d.join("");
if(_17e){
this.containerNode.style.cssText=_17e.join("");
}
if(_17f){
this.tbNode.style.cssText=_17f.join("");
}
if(_180){
this.rbNode.style.cssText=_180.join("");
}
if(_181){
this.bgIframe.iframe.style.cssText=_182.join("");
if(_183){
_17a.iframeCover.style.cssText=_183.join("");
}
}
}
if(_18c&&_18d){
this._deferSetIFrameH(_18c,_18d,false,50);
}
},_deferSetIFrameH:function(_1a0,_1a1,_1a2,_1a3,_1a4){
if(!_1a3){
_1a3=100;
}
var pWin=this;
window.setTimeout(function(){
_1a0.height=_1a1;
if(_1a2){
if(_1a4==null){
_1a4=50;
}
if(_1a4==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_1a4);
}
}
},_1a3);
},_getWindowMarginBox:function(_1a6,_1a7){
var _1a8=this.domNode;
if(_1a6==null){
_1a6=this._getLayoutInfo().dNode;
}
var _1a9=null;
if(_1a7.UAope){
_1a9=(this.posStatic?_1a7.page.layoutInfo.column:_1a7.page.layoutInfo.desktop);
}
return _1a7.ui.getMarginBox(_1a8,_1a6,_1a9,_1a7);
},_forceRefreshZIndex:function(){
var _1aa=jetspeed;
var zTop=this._setAsTopZIndex(_1aa.page,_1aa.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=zTop;
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},getIFramesAndObjects:function(_1ad,_1ae){
var _1af=this.containerNode;
var _1b0={};
var _1b1=false;
if(!_1ae){
var _1b2=_1af.getElementsByTagName("object");
if(_1b2&&_1b2.length>0){
_1b0.objects=_1b2;
_1b1=true;
}
}
var _1b3=_1af.getElementsByTagName("iframe");
if(_1b3&&_1b3.length>0){
_1b0.iframes=_1b3;
if(!_1ad){
return _1b0;
}
_1b1=true;
var _1b4=[];
for(var i=0;i<_1b3.length;i++){
var ifrm=_1b3[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_1b4.push({w:w,h:h});
}
_1b0.iframesSize=_1b4;
}
if(!_1b1){
return null;
}
return _1b0;
},contentChanged:function(evt){
if(this.inContentChgd==false){
this.inContentChgd=true;
if(this.heightToFit){
this.makeHeightToFit(true);
}
this.inContentChgd=false;
}
},closeWindow:function(){
var _1ba=jetspeed;
var jsUI=_1ba.ui;
var _1bc=_1ba.page;
var _1bd=dojo;
var _1be=_1bd.event;
var wDC=this.decConfig;
if(this.iframesInfo){
_1bc.unregPWinIFrameCover(this);
}
this._setupTitlebar(null,wDC,this.portlet,_1ba.docBody,document,_1ba,_1ba.id,_1ba.prefs,jsUI,_1bc,_1bd);
if(this.drag){
this.drag.destroy(_1bd,_1be,_1ba,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_1be,_1ba,jsUI);
this.resizeHandle=null;
}
this._destroyChildWidgets(_1bd);
this._removeUntiledEvents();
var _1c0=this.domNode;
if(_1c0&&_1c0.parentNode){
_1c0.parentNode.removeChild(_1c0);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},_destroyChildWidgets:function(_1c1){
if(this.childWidgets){
var _1c2=this.childWidgets;
var _1c3=_1c2.length,_1c4,swT,swI;
_1c1.debug("PortletWindow ["+this.widgetId+"] destroy child widgets ("+_1c3+")");
for(var i=(_1c3-1);i>=0;i--){
try{
_1c4=_1c2[i];
if(_1c4){
swT=_1c4.widgetType;
swI=_1c4.widgetId;
_1c4.destroy();
_1c1.debug("destroyed child widget["+i+"]: "+swT+" "+swI);
}
_1c2[i]=null;
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
},endDragging:function(_1c9,_1ca,_1cb){
var _1cc=jetspeed;
var ie6=this.ie6;
if(_1ca){
this.posStatic=false;
}else{
if(_1cb){
this.posStatic=true;
}
}
var _1ce=this.posStatic;
if(!_1ce){
var _1cf=this.getDimsObj(_1ce);
if(_1c9&&_1c9.left!=null&&_1c9.top!=null){
_1cf.l=_1c9.left;
_1cf.t=_1c9.top;
if(!_1ca){
this._alterCss(false,false,false,true,false,true);
}
}
if(_1ca){
this._updtDimsObj(false,false,true);
this._alterCss(true,true,false,true);
this._addUntiledEvents();
}
}else{
if(_1cb){
this._setAsTopZIndex(_1cc.page,_1cc.css,this.dNodeCss,_1ce);
this._updtDimsObj(false,false);
}
if(!ie6){
this._alterCss(true);
this.resizeNotifyChildWidgets();
}else{
this._resetIE6TiledSize(_1cb);
}
}
if(this.portlet&&this.windowState!=_1cc.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,_1cc.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_1d0){
var _1d1=this.domNode;
var _1d2=this.posStatic;
if(!_1d1){
return null;
}
var _1d3=_1d1.style;
var _1d4={};
if(!_1d2){
_1d4.zIndex=_1d3.zIndex;
}
if(_1d0){
return _1d4;
}
var _1d5=this.getDimsObj(_1d2);
_1d4.width=(_1d5.w?String(_1d5.w):"");
_1d4.height=(_1d5.h?String(_1d5.h):"");
_1d4[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_1d2;
_1d4[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_1d2){
_1d4.left=(_1d5.l!=null?String(_1d5.l):"");
_1d4.top=(_1d5.t!=null?String(_1d5.t):"");
}else{
var _1d6=jetspeed.page.getPortletCurColRow(_1d1);
if(_1d6!=null){
_1d4.column=_1d6.column;
_1d4.row=_1d6.row;
_1d4.layout=_1d6.layout;
}else{
throw new Error("Can't find row/col/layout for window: "+this.widgetId);
}
}
return _1d4;
},getCurWinStateForPersist:function(_1d7){
var _1d8=this.getCurWinState(_1d7);
this._mkNumProp(null,_1d8,"left");
this._mkNumProp(null,_1d8,"top");
this._mkNumProp(null,_1d8,"width");
this._mkNumProp(null,_1d8,"height");
return _1d8;
},_mkNumProp:function(_1d9,_1da,_1db){
var _1dc=(_1da!=null&&_1db!=null);
if(_1d9==null&&_1dc){
_1d9=_1da[_1db];
}
if(_1d9==null||_1d9.length==0){
_1d9=0;
}else{
var _1dd="";
for(var i=0;i<_1d9.length;i++){
var _1df=_1d9.charAt(i);
if((_1df>="0"&&_1df<="9")||_1df=="."){
_1dd+=_1df.toString();
}
}
if(_1dd==null||_1dd.length==0){
_1dd="0";
}
if(_1dc){
_1da[_1db]=_1dd;
}
_1d9=new Number(_1dd);
}
return _1d9;
},setPortletContent:function(html,url){
var _1e2=jetspeed;
var _1e3=dojo;
var ie6=this.ie6;
var _1e5=null;
var _1e6=this.containerNode;
if(ie6){
_1e5=this.iNodeCss;
if(this.heightToFit){
this.iNodeCss=null;
this._alterCss(false,true);
}
}
var _1e7=html.toString();
if(!this.exclPContent){
_1e7="<div class=\"PContent\" >"+_1e7+"</div>";
}
var _1e8=this._splitAndFixPaths_scriptsonly(_1e7,url,_1e2);
var doc=_1e6.ownerDocument;
var _1ea=this.setContent(_1e8,doc,_1e3);
this.childWidgets=((_1ea&&_1ea.length>0)?_1ea:null);
if(_1e8.scripts!=null&&_1e8.scripts.length!=null&&_1e8.scripts.length>0){
this._executeScripts(_1e8.scripts,_1e3);
this.onLoad();
}
if(_1e2.debug.setPortletContent){
_1e3.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
if(this.portlet){
this.portlet.postParseAnnotateHtml(_1e6);
}
var _1eb=this.iframesInfo;
var _1ec=this.getIFramesAndObjects(true,false);
var _1ed=null,_1ee=false;
if(_1ec!=null){
if(_1eb==null){
this.iframesInfo=_1eb={layout:false};
var _1ef=doc.createElement("div");
var _1f0="portletWindowIFrameCover";
_1ef.className=_1f0;
_1e6.appendChild(_1ef);
if(_1e2.UAie){
_1ef.className=(_1f0+"IE")+" "+_1f0;
if(ie6){
_1eb.iframeCoverIE6Css=_1e2.css.cssWidthHeight.concat();
}
}
_1eb.iframeCover=_1ef;
_1e2.page.regPWinIFrameCover(this);
}
var _1f1=_1eb.iframesSize=_1ec.iframesSize;
var _1f2=_1ec.iframes;
var _1f3=_1eb.layout;
var _1f4=_1eb.layout=(_1f2&&_1f2.length==1&&_1f1[0].h!=null);
if(_1f3!=_1f4){
_1ee=true;
}
if(_1f4){
if(!this.heightToFit){
_1ed=_1f2[0];
}
var wDC=this.decConfig;
var _1e6=this.containerNode;
_1e6.firstChild.className="PContent portletIFramePContent";
_1e6.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
if(!wDC.layoutIFrame){
this._createLayoutInfo(wDC,true,this.domNode,_1e6,this.tbNode,this.rbNode,_1e3,_1e2,_1e2.ui);
}
}
var _1f6=null;
var _1f7=_1ec.objects;
if(_1f7){
var _1f8=_1e2.page.swfInfo;
if(_1f8){
for(var i=0;i<_1f7.length;i++){
var _1fa=_1f7[i];
var _1fb=_1fa.id;
if(_1fb){
var swfI=_1f8[_1fb];
if(swfI){
if(_1f6==null){
_1f6={};
}
_1f6[_1fb]=swfI;
}
}
}
}
}
if(_1f6){
_1eb.swfInfo=_1f6;
}else{
delete _1eb.swfInfo;
}
}else{
if(_1eb!=null){
if(_1eb.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_1ee=true;
}
this.iframesInfo=null;
_1e2.page.unregPWinIFrameCover(this);
}
}
if(_1ee){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(ie6){
this._updtDimsObj(false,false,true,false,true);
if(_1e5==null){
var _1fd=_1e2.css;
_1e5=_1fd.cssHeight.concat();
_1e5[_1fd.cssDis]="inline";
}
this.iNodeCss=_1e5;
this._alterCss(false,false,true);
}
if(this.minimizeOnNextRender){
this.minimizeOnNextRender=false;
this.minimizeWindow(true);
this.actionBtnSync(_1e2,_1e2.id);
this.needsRenderOnRestore=true;
}
if(_1ed){
this._deferSetIFrameH(_1ed,(!_1e2.UAie?"100%":"99%"),true);
}
},_setContentObjects:function(){
delete this._objectsInfo;
},setContent:function(data,doc,_200){
var _201=null;
var step=1;
try{
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
step=2;
this._setContent(data.xml,_200);
step=3;
if(this.parseContent){
var node=this.containerNode;
var _204=new _200.xml.Parse();
var frag=_204.parseElement(node,null,true);
_201=_200.widget.getParser().createSubComponents(frag,null);
}
}
catch(e){
dojo.hostenv.println("ERROR in PortletWindow ["+this.widgetId+"] setContent while "+(step==1?"running onUnload":(step==2?"setting innerHTML":"creating dojo widgets"))+" - "+jetspeed.formatError(e));
}
return _201;
},_setContent:function(cont,_207){
this._destroyChildWidgets(_207);
try{
var node=this.containerNode;
while(node.firstChild){
_207.html.destroyNode(node.firstChild);
}
node.innerHTML=cont;
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},_splitAndFixPaths_scriptsonly:function(s,url,_20b){
var _20c=true;
var _20d,attr;
var _20f=[];
var _210=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _211=/src=(['"]?)([^"']*)\1/i;
while(_20d=_210.exec(s)){
if(_20c&&_20d[1]){
if(attr=_211.exec(_20d[1])){
_20f.push({path:attr[2]});
}
}
if(_20d[2]){
var sc=_20d[2];
if(!sc){
continue;
}
if(_20c){
_20f.push(sc);
}
}
s=s.substr(0,_20d.index)+s.substr(_20d.index+_20d[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_20f,"url":url};
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
},_runStack:function(_215){
var st=this[_215];
var err="";
var _218=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_218);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_215]=[];
if(err.length){
var name=(_215=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_21b,_21c){
var _21d=jetspeed;
var _21e=_21c.hostenv;
var _21f=_21d.page;
var _220=document.getElementsByTagName("head")[0];
var tmp,uri,code="";
for(var i=0;i<_21b.length;i++){
if(!_21b[i].path){
tmp=this._fixScripts(_21b[i],true);
if(tmp){
code+=((code.length>0)?";":"")+tmp;
}
continue;
}
var uri=_21b[i].path;
var _225=null;
try{
_225=_21e.getText(uri,null,false);
if(_225){
_225=this._fixScripts(_225,false);
code+=((code.length>0)?";":"")+_225;
}
}
catch(ex){
_21c.debug("Error loading script for portlet ["+this.widgetId+"] url="+uri+" - "+_21d.formatError(ex));
}
try{
if(_225&&!_21d.containsElement("script","src",uri,_220)){
_21d.addDummyScriptToHead(uri);
}
}
catch(ex){
_21c.debug("Error added fake script element to head for portlet ["+this.widgetId+"] url="+uri+" - "+_21d.formatError(ex));
}
}
try{
var djg=_21c.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_21c.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
catch(e){
var _229="Error running scripts for portlet ["+this.widgetId+"] - "+_21d.formatError(e);
e.text=_229;
_21c.hostenv.println(_229);
_21c.hostenv.println(code);
}
},_fixScripts:function(_22a,_22b){
var _22c=/\b([a-z_A-Z$]\w*)\s*\.\s*(addEventListener|attachEvent)\s*\(/;
var _22d,_22e,_22f;
while(_22d=_22c.exec(_22a)){
_22e=_22d[1];
_22f=_22d[2];
_22a=_22a.substr(0,_22d.index)+"jetspeed.postload_"+_22f+"("+_22e+","+_22a.substr(_22d.index+_22d[0].length);
}
var _230=/\b(document\s*.\s*write(ln)?)\s*\(/;
while(_22d=_230.exec(_22a)){
_22a=_22a.substr(0,_22d.index)+"jetspeed.postload_docwrite("+_22a.substr(_22d.index+_22d[0].length);
}
var _231=/(;\s|\s+)([a-z_A-Z$][\w.]*)\s*\.\s*(URL\s*|(location\s*(\.\s*href\s*){0,1}))=\s*(("[^"]*"|'[^']*'|[^;])[^;]*)/;
while(_22d=_231.exec(_22a)){
var _232=_22d[3];
_232=_232.replace(/^\s+|\s+$/g,"");
_22a=_22a.substr(0,_22d.index)+_22d[1]+"jetspeed.setdoclocation("+_22d[2]+", \""+_232+"\", ("+_22d[6]+"))"+_22a.substr(_22d.index+_22d[0].length);
}
if(_22b){
_22a=_22a.replace(/<!--|-->/g,"");
}
return _22a;
},_cacheSetting:function(_233,_234){
var _235=dojo.lang;
for(var x in this.bindArgs){
if(_235.isUndefined(_233[x])){
_233[x]=this.bindArgs[x];
}
}
if(_235.isUndefined(_233.useCache)){
_233.useCache=_234;
}
if(_235.isUndefined(_233.preventCache)){
_233.preventCache=!_234;
}
if(_235.isUndefined(_233.mimetype)){
_233.mimetype="text/html";
}
return _233;
},_handleDefaults:function(e,_238,_239){
var _23a=dojo;
if(!_238){
_238="onContentError";
}
if(_23a.lang.isString(e)){
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
this[_238](e);
if(e.returnValue){
switch(_239){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_23a.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_23a.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_23a);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_23d){
if(_23d){
this.title=_23d;
}else{
this.title="";
}
if(this.windowInitialized&&this.tbTextNode){
this.tbTextNode.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_IEPostDrag:function(){
if(!this.posStatic){
return;
}
var _23e=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_23e,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.showAllPortletWindows=function(){
var _23f=jetspeed;
var _240=_23f.css;
var _241=_240.cssDis,_242=_240.cssNoSelNm,_243=_240.cssNoSel,_244=_240.cssNoSelEnd;
var _245=_23f.page.getPWins(false);
var _246,_247;
for(var i=0;i<_245.length;i++){
_246=_245[i];
if(_246){
_247=_246.dNodeCss;
_247[_242]="";
_247[_243]="";
_247[_244]="";
_247[_241]="block";
_246.domNode.style.display="block";
_246.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.hideAllPortletWindows=function(_249){
var _24a=jetspeed;
var _24b=_24a.css;
var _24c=_24b.cssDis,_24d=_24b.cssNoSelNm,_24e=_24b.cssNoSel,_24f=_24b.cssNoSelEnd;
var _250=_24a.page.getPWins(false);
var _251,_252,_253;
for(var i=0;i<_250.length;i++){
_252=_250[i];
_251=true;
if(_252&&_249&&_249.length>0){
for(var _255=0;_255<_249.length;_255++){
if(_252.widgetId==_249[_255]){
_251=false;
break;
}
}
}
if(_252){
_253=_252.dNodeCss;
_253[_24d]="";
_253[_24e]="";
_253[_24f]="";
if(_251){
_253[_24c]="none";
_252.domNode.style.display="none";
}else{
_253[_24c]="block";
_252.domNode.style.display="block";
}
_252.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.WinScroller=function(){
var _256=this.jsObj;
this.UAmoz=_256.UAmoz;
this.UAope=_256.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{jsObj:jetspeed,djObj:dojo,typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _25a=null;
if(this.UAmoz){
_25a=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_25a=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_25a=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_25a=b.clientHeight;
}
}
}
}
if(_25a!=null&&e.clientY>_25a-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_260,_261){
return ((_261!=null?(_261+"; "):"")+this.typeNm+" "+(_260==null?"<unknown>":_260.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_262,_263){
var _264=new jetspeed.widget.PortletWindowResizeHandle(_262,_263);
var doc=document;
var _266=doc.createElement("div");
_266.className=_264.rhClass;
var _267=doc.createElement("div");
_266.appendChild(_267);
_262.rbNode.appendChild(_266);
_264.domNode=_266;
_264.build();
return _264;
};
jetspeed.widget.PortletWindowResizeHandle=function(_268,_269){
this.pWin=_268;
_269.widget.WinScroller.call(this);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_26a,_26b,jsUI){
this._cleanUpLastEvt(_26a,_26b,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_26a);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_26d,_26e,jsUI){
var _270=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_26d);
this.tempEvents=null;
}
catch(ex){
_270=this._getErrMsg(ex,"event clean-up error",this.pWin,_270);
}
try{
_26e.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_270=this._getErrMsg(ex,"clean-up error",this.pWin,_270);
}
if(_270!=null){
dojo.raise(_270);
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
var _274=jetspeed;
var jsUI=_274.ui;
var _276=dojo;
var _277=_276.event;
var _278=_274.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_277,_274,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_276.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _27b=[];
_27b.push(jsUI.evtConnect("after",_278,"onmousemove",this,"_changeSizing",_277,25));
_27b.push(jsUI.evtConnect("after",_278,"onmouseup",this,"_endSizing",_277));
_27b.push(jsUI.evtConnect("after",d,"ondragstart",_274,"_stopEvent",_277));
_27b.push(jsUI.evtConnect("after",d,"onselectstart",_274,"_stopEvent",_277));
_274.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_27b;
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
var _282=pWin.posStatic;
if(_282){
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
var _285=jetspeed;
var _286=dojo;
this._cleanUpLastEvt(_286.event,_285,_285.ui);
this.pWin.actionBtnSyncDefer(true,_285,_286);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
jetspeed.widget.BackgroundIframe=function(node,_288,_289){
if(!_288){
_288=this.defaultStyleClass;
}
var html="<iframe src='' frameborder='0' scrolling='no' class='"+_288+"'>";
this.iframe=_289.doc().createElement(html);
this.iframe.tabIndex=-1;
node.appendChild(this.iframe);
};
dojo.lang.extend(jetspeed.widget.BackgroundIframe,{defaultStyleClass:"ie6BackgroundIFrame",iframe:null});
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_28b,_28c,_28d,_28e,_28f,e,_291,_292,_293){
var jsUI=_293.ui;
var _295=_292.event;
_293.widget.WinScroller.call(this);
if(_293.widget._movingInProgress){
if(djConfig.isDebug){
_293.debugAlert("ERROR - Mover initiation before previous Mover was destroyed");
}
}
_293.widget._movingInProgress=true;
this.moveInitiated=false;
this.moveableObj=_28f;
this.windowOrLayoutWidget=_28b;
this.node=_28c;
this.dragLayoutColumn=_28d;
this.cL_NA_ED=_28e;
this.posStatic=_28b.posStatic;
this.notifyOnAbsolute=_291;
if(e.ctrlKey&&_28b.moveAllowTilingChg){
if(this.posStatic){
this.changeToUntiled=true;
}else{
if(_293.prefs.windowTiling){
this.changeToTiled=true;
this.changeToTiledStarted=false;
}
}
}
this.posRecord={};
this.disqualifiedColumnIndexes={};
if(_28d!=null){
this.disqualifiedColumnIndexes=_28d.col.getDescendantCols();
}
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _297=[];
var _298=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_295);
_297.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_295));
_297.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_295));
_297.push(jsUI.evtConnect("after",doc,"ondragstart",_293,"_stopEvent",_295));
_297.push(jsUI.evtConnect("after",doc,"onselectstart",_293,"_stopEvent",_295));
if(_293.UAie6){
_297.push(jsUI.evtConnect("before",doc,"onmousedown",this,"mouseDownDestroy",_295));
_297.push(jsUI.evtConnect("before",_28f.handle,"onmouseup",_28f,"onMouseUp",_295));
}
_293.page.displayAllPWinIFrameCovers(false);
_297.push(_298);
this.events=_297;
this.pSLastColChgIdx=null;
this.pSLastColChgTime=null;
this.pSLastNaturalColChgYTest=null;
this.pSLastNaturalColChgHistory=null;
this.pSLastNaturalColChgChoiceMap=null;
this.isDebug=false;
if(_293.debug.dragWindow){
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
this.devI=_293.debugindent;
this.devIH=_293.debugindentH;
this.devIT=_293.debugindentT;
this.devI3=_293.debugindent3;
this.devICH=_293.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",pSColChgTimeTh:3000,onMouseMove:function(e){
var _29a=this.jsObj;
var _29b=this.djObj;
var _29c=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _29e=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _2a1=this.isDebug;
var _2a2=false;
var _2a3=null,_2a4=null,_2a5,_2a6,_2a7,_2a8,_2a9;
if(_2a1){
_2a5=this.devI;
_2a6=this.devIH;
_2a7=this.devI3;
_2a8=this.devICH,_2a9=this.devIT;
_2a3=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _2aa=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_2aa&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_2a3)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_2a2=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_2a2=true;
}
}
}
}
if(_29c&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_29e=true;
}
_29a.ui.setMarginBox(this.node,x,y,null,null,this.nodeLayoutInfo,_29a,_29b);
var _2ab=this.posRecord;
_2ab.left=x;
_2ab.top=y;
var _2ac=false;
var _2ad=this.posStatic;
if(!_2ad){
if(!_29e&&this.changeToTiled&&!this.changeToTiledStarted){
_2ac=true;
_2ad=true;
}
}
if(_2ad&&!_29e){
var _2ae=this.columnInfoArray;
var _2af=_29a.page.columns;
var _2b0=this.heightHalf;
var _2b1=_2af.length;
var _2b2=e.pageX;
var _2b3=y+_2b0;
var _2b4=this.pSLastColChgIdx;
var _2b5=this.pSLastNaturalColChgChoiceMap;
var _2b6=null,_2b7=[],_2b8=null;
var _2b9,_2ba,_2bb,_2bc,lowY,_2be,_2bf,_2c0,_2c1;
for(var i=0;i<_2b1;i++){
_2b9=_2ae[i];
if(_2b9!=null){
if(_2b2>=_2b9.left&&_2b2<=_2b9.right){
if(_2b3>=(_2b9.top-30)||(_2b5!=null&&_2b5[i]!=null)){
_2ba=Math.min(Math.abs(_2b3-(_2b9.top)),Math.abs(e.pageY-(_2b9.top)));
_2bb=Math.min(Math.abs(_2b3-(_2b9.yhalf)),Math.abs(e.pageY-(_2b9.yhalf)));
_2bc=Math.min(Math.abs(_2b3-_2b9.bottom),Math.abs(e.pageY-_2b9.bottom));
lowY=Math.min(_2ba,_2bb);
lowY=Math.min(lowY,_2bc);
_2bf=null;
_2c1=_2b6;
while(_2c1!=null){
_2c0=_2b7[_2c1];
if(lowY<_2c0.lowY){
break;
}else{
_2bf=_2c0;
_2c1=_2c0.nextIndex;
}
}
_2b7.push({index:i,lowY:lowY,nextIndex:_2c1,lowYAlign:((!_2a1)?null:(lowY==_2ba?"^":(lowY==_2bb?"~":"_")))});
_2be=(_2b7.length-1);
if(_2bf!=null){
_2bf.nextIndex=_2be;
}else{
_2b6=_2be;
}
if(i==_2b4){
_2b8=lowY;
}
}else{
if(_2a1){
if(_2a4==null){
_2a4=[];
}
var _2c3=(_2b9.top-30)-_2b3;
_2a4.push(_29b.string.padRight(String(i),2,_2a8)+" y! "+_29b.string.padRight(String(_2c3),4,_2a8));
}
}
}else{
if(_2a1&&_2b2>_2b9.width){
if(_2a4==null){
_2a4=[];
}
var _2c3=_2b2-_2b9.width;
_2a4.push(_29b.string.padRight(String(i),2,_2a8)+" x! "+_29b.string.padRight(String(_2c3),4,_2a8));
}
}
}
}
var _2c4=-1;
var _2c5=-1,_2c6=-1;
var _2c7=null,_2c8=null,_2c9=null,_2ca=null,_2cb=null;
if(_2b6!=null){
_2c0=_2b7[_2b6];
_2c4=_2c0.index;
_2c7=_2c0.lowY;
if(_2c0.nextIndex!=null){
_2c0=_2b7[_2c0.nextIndex];
_2c5=_2c0.index;
_2c8=_2c0.lowY;
_2ca=_2c8-_2c7;
if(_2c0.nextIndex!=null){
_2c0=_2b7[_2c0.nextIndex];
_2c6=_2c0.index;
_2c9=_2c0.lowY;
_2cb=_2c9-_2c7;
}
}
}
var _2cc=null;
var _2cd=(new Date().getTime());
var _2ce=this.pSLastNaturalColChgYTest;
if(_2b8==null||(_2ce!=null&&Math.abs(_2b3-_2ce)>=Math.max((_2b0-Math.floor(_2b0*0.3)),Math.min(_2b0,21)))){
if(_2c4>=0){
this.pSLastNaturalColChgYTest=_2b3;
this.pSLastNaturalColChgHistory=[_2c4];
_2b5={};
_2b5[_2c4]=true;
this.pSLastNaturalColChgChoiceMap=_2b5;
}
}else{
if(_2ce==null){
this.pSLastNaturalColChgYTest=_2b3;
_2c4=_2b4;
this.pSLastNaturalColChgHistory=[_2c4];
_2b5={};
_2b5[_2c4]=true;
this.pSLastNaturalColChgChoiceMap=_2b5;
}else{
var _2cf=null;
var _2d0=this.pSLastColChgTime+this.pSColChgTimeTh;
if(_2d0<_2cd){
var _2d1=this.pSLastNaturalColChgHistory;
var _2d2=(_2d1==null?0:_2d1.length);
var _2d3=null,_2d4;
_2c1=_2b6;
while(_2c1!=null){
_2c0=_2b7[_2c1];
colI=_2c0.index;
if(_2d2==0){
_2cf=colI;
break;
}else{
_2d4=false;
for(var i=(_2d2-1);i>=0;i--){
if(_2d1[i]==colI){
if(_2d3==null||_2d3>i){
_2d3=i;
_2cf=colI;
}
_2d4=true;
break;
}
}
if(!_2d4){
_2cf=colI;
break;
}
}
_2c1=_2c0.nextIndex;
}
if(_2cf!=null){
_2c4=_2cf;
_2b5[_2c4]=true;
if(_2d2==0||_2d1[(_2d2-1)]!=_2c4){
_2d1.push(_2c4);
}
}
}else{
_2c4=_2b4;
}
if(_2a1&&_2cf!=null){
_29b.hostenv.println(_2a5+"ColChg YTest="+_2ce+" LeastRecentColI="+_2cf+" History=["+(this.pSLastNaturalColChgHistory?this.pSLastNaturalColChgHistory.join(", "):"")+"] Map={"+_29a.printobj(this.pSLastNaturalColChgChoiceMap)+"} expire="+(_2cd-_2d0)+"}");
}
}
}
if(_2a1&&_2cc!=null){
if(this.devKeepLastMsg!=null){
_29b.hostenv.println(this.devKeepLastMsg);
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
}
_29b.hostenv.println(_2cc);
}
var col=(_2c4>=0?_2af[_2c4]:null);
if(_2a1){
if(this.devLastColI!=_2c4){
_2a2=true;
}
this.devLastColI=_2c4;
}
var _2d7=_29a.widget.pwGhost;
if(_2ac){
if(col!=null){
_29a.ui.setMarginBox(_2d7,null,null,null,m.h,this.nodeLayoutInfo,_29a,_29b);
_2d7.col=null;
this.changeToTiledStarted=true;
this.posStatic=true;
}
}
var _2d8=null,_2d9=false,_2da=false;
if(_2d7.col!=col&&col!=null){
this.pSLastColChgTime=_2cd;
this.pSLastColChgIdx=_2c4;
var _2db=_2d7.col;
if(_2db!=null){
_29b.dom.removeNode(_2d7);
}
_2d7.col=col;
var _2dc=_2ae[_2c4];
var _2dd=_2dc.childCount+1;
_2dc.childCount=_2dd;
if(_2dd==1){
_2af[_2c4].domNode.style.height="";
}
col.domNode.appendChild(_2d7);
_2da=true;
var _2de=(_2b4!=null?((_2b4!=_2c4)?_2ae[_2b4]:null):(_2db!=null?_2ae[_2db.getPageColumnIndex()]:null));
if(_2de!=null){
var _2df=_2de.childCount-1;
if(_2df<0){
_2df=0;
}
_2de.childCount=_2df;
if(_2df==0){
_2af[_2de.pageColIndex].domNode.style.height="1px";
}
}
}
var _2e0=null,_2e1=null;
if(col!=null){
_2e0=_29a.ui.getPWinAndColChildren(col.domNode,_2d7,true,false,true,false);
_2e1=_2e0.matchingNodes;
}
if(_2e1!=null&&_2e1.length>1){
var _2e2=_2e0.matchNodeIndexInMatchingNodes;
var _2e3=-1;
var _2e4=-1;
if(_2e2>0){
var _2e3=_29b.html.getAbsolutePosition(_2e1[_2e2-1],true).y;
if((y-25)<=_2e3){
_29b.dom.removeNode(_2d7);
_2d8=_2e1[_2e2-1];
_29b.dom.insertBefore(_2d7,_2d8,true);
}
}
if(_2e2!=(_2e1.length-1)){
var _2e4=_29b.html.getAbsolutePosition(_2e1[_2e2+1],true).y;
if((y+10)>=_2e4){
if(_2e2+2<_2e1.length){
_2d8=_2e1[_2e2+2];
_29b.dom.insertBefore(_2d7,_2d8,true);
}else{
col.domNode.appendChild(_2d7);
_2d9=true;
}
}
}
}
if(_2a2){
var _2e5="";
if(_2d8!=null||_2d9||_2da){
_2e5="put=";
if(_2d8!=null){
_2e5+="before("+_2d8.id+")";
}else{
if(_2d9){
_2e5+="end";
}else{
if(_2da){
_2e5+="end-default";
}
}
}
}
_29b.hostenv.println(_2a5+"col="+_2c4+_2a6+_2e5+_2a6+"x="+x+_2a6+"y="+y+_2a6+"ePGx="+e.pageX+_2a6+"ePGy="+e.pageY+_2a6+"yTest="+_2b3);
var _2e6="",colI,_2b9;
_2c1=_2b6;
while(_2c1!=null){
_2c0=_2b7[_2c1];
colI=_2c0.index;
_2b9=_2ae[_2c0.index];
_2e6+=(_2e6.length>0?_2a9:"")+colI+_2c0.lowYAlign+(colI<10?_2a8:"")+" -> "+_29b.string.padRight(String(_2c0.lowY),4,_2a8);
_2c1=_2c0.nextIndex;
}
_29b.hostenv.println(_2a7+_2e6);
if(_2a4!=null){
var _2e7="";
for(i=0;i<_2a4.length;i++){
_2e7+=(i>0?_2a9:"")+_2a4[i];
}
_29b.hostenv.println(_2a7+_2e7);
}
this.devLastTime=_2a3;
this.devChgTh=this.devChgSubsqTh;
}
}
},onFirstMove:function(){
var _2e8=this.jsObj;
var jsUI=_2e8.ui;
var _2ea=this.djObj;
var _2eb=this.windowOrLayoutWidget;
var node=this.node;
var _2ed=_2eb._getLayoutInfoMoveable();
this.nodeLayoutInfo=_2ed;
var mP=_2eb._getWindowMarginBox(_2ed,_2e8);
this.staticWidth=null;
var _2ef=_2e8.widget.pwGhost;
var _2f0=this.UAmoz;
var _2f1=this.changeToUntiled;
var _2f2=this.changeToTiled;
var m=null;
if(this.posStatic){
if(!_2f1){
var _2f4=_2eb.getPageColumnIndex();
var _2f5=(_2f4>=0?_2e8.page.columns[_2f4]:null);
_2ef.col=_2f5;
this.pSLastColChgTime=new Date().getTime();
this.pSLastColChgIdx=_2f4;
}
m={w:mP.w,h:mP.h};
var _2f6=node.parentNode;
var _2f7=document.getElementById(_2e8.id.DESKTOP);
var _2f8=node.style;
this.staticWidth=_2f8.width;
var _2f9=_2ea.html.getAbsolutePosition(node,true);
var _2fa=_2ed.mE;
m.l=_2f9.left-_2fa.l;
m.t=_2f9.top-_2fa.t;
if(_2f0){
if(!_2f1){
jsUI.setMarginBox(_2ef,null,null,null,mP.h,_2ed,_2e8,_2ea);
}
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_2f8.position="absolute";
if(!_2f1){
_2f8.zIndex=_2e8.page.getPWinHighZIndex()+1;
}else{
_2f8.zIndex=(_2eb._setAsTopZIndex(_2e8.page,_2e8.css,_2eb.dNodeCss,false));
}
if(!_2f1){
_2f6.insertBefore(_2ef,node);
if(!_2f0){
jsUI.setMarginBox(_2ef,null,null,null,mP.h,_2ed,_2e8,_2ea);
}
_2f7.appendChild(node);
var _2fb=jsUI.getPWinAndColChildren(_2f6,_2ef,true,false,true);
this.prevColumnNode=_2f6;
this.prevIndexInCol=_2fb.matchNodeIndexInMatchingNodes;
}else{
_2eb._updtDimsObj(false,true);
_2f7.appendChild(node);
}
}else{
m=mP;
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
jsUI.evtDisconnectWObj(this.events.pop(),_2ea.event);
var _2fc=this.disqualifiedColumnIndexes;
var _2fd=(this.isDebug||_2e8.debug.dragWindowStart),_2fe;
if(_2fd){
_2fe=_2e8.debugindentT;
var _2ff=_2e8.debugindentH;
var _300="";
if(_2fc!=null){
_300=_2ff+"dqCols=["+_2e8.objectKeys(_2fc).join(", ")+"]";
}
var _301=_2eb.title;
if(_301==null){
_301=node.id;
}
_2ea.hostenv.println("DRAG \""+_301+"\""+_2ff+((this.posStatic&&!_2f1)?("col="+(_2ef.col?_2ef.col.getPageColumnIndex():"null")+_2ff):"")+"m.l = "+m.l+_2ff+"m.t = "+m.t+_300);
}
if(this.posStatic||_2f2){
this.heightHalf=mP.h/2;
var _302=this.dragLayoutColumn||{};
var _303=jsUI.updateChildColInfo(node,_2fc,_302.maxdepth,this.cL_NA_ED,(_2fd?1:null),_2fe);
if(_2fd){
_2ea.hostenv.println(_2fe+"--------------------");
}
this.columnInfoArray=_303;
}
if(this.posStatic){
jsUI.setMarginBox(node,m.l,m.t,mP.w,null,_2ed,_2e8,_2ea);
if(this.notifyOnAbsolute){
_2eb.dragChangeToAbsolute(this,node,this.marginBox,_2ea,_2e8);
}
if(_2f1){
this.posStatic=false;
}
}
},mouseDownDestroy:function(e){
var _305=this.jsObj;
_305.stopEvent(e);
this.mouseUpDestroy();
},mouseUpDestroy:function(){
var _306=this.djObj;
var _307=this.jsObj;
this.destroy(_306,_306.event,_307,_307.ui);
},destroy:function(_308,_309,_30a,jsUI){
var _30c=this.windowOrLayoutWidget;
var node=this.node;
var _30e=null;
if(this.moveInitiated&&_30c&&node){
this.moveInitiated=false;
try{
if(this.posStatic){
var _30f=_30a.widget.pwGhost;
var _310=node.style;
if(_30f&&_30f.col){
_30c.column=0;
_308.dom.insertBefore(node,_30f,true);
}else{
if(this.prevColumnNode!=null&&this.prevIndexInCol!=null){
_308.dom.insertAtIndex(node,this.prevColumnNode,this.prevIndexInCol);
}else{
var _311=_30a.page.getColumnDefault();
if(_311!=null){
_308.dom.prependChild(node,_311.domNode);
}
}
}
if(_30f){
_308.dom.removeNode(_30f);
}
}
_30c.endDragging(this.posRecord,this.changeToUntiled,this.changeToTiled);
}
catch(ex){
_30e=this._getErrMsg(ex,"destroy reset-window error",_30c,_30e);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_309);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_30e=this._getErrMsg(ex,"destroy event clean-up error",_30c,_30e);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_30a.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_30e=this._getErrMsg(ex,"destroy clean-up error",_30c,_30e);
}
_30a.widget._movingInProgress=false;
if(_30e!=null){
_308.raise(_30e);
}
}});
dojo.dnd.Moveable=function(_312,opt){
var _314=jetspeed;
var jsUI=_314.ui;
var _316=dojo;
var _317=_316.event;
this.windowOrLayoutWidget=_312;
this.handle=opt.handle;
var _318=[];
_318.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_317));
_318.push(jsUI.evtConnect("after",this.handle,"ondragstart",_314,"_stopEvent",_317));
_318.push(jsUI.evtConnect("after",this.handle,"onselectstart",_314,"_stopEvent",_317));
this.events=_318;
};
dojo.extend(dojo.dnd.Moveable,{minMove:5,enabled:true,mover:null,onMouseDown:function(e){
if(e&&e.button==2){
return;
}
var _31a=dojo;
var _31b=_31a.event;
var _31c=jetspeed;
var jsUI=jetspeed.ui;
if(this.mover!=null||this.tempEvents!=null){
this._cleanUpLastEvt(_31a,_31b,_31c,jsUI);
_31c.stopEvent(e);
}else{
if(this.enabled){
if(this.tempEvents!=null){
if(djConfig.isDebug){
_31c.debugAlert("ERROR: Moveable onmousedown tempEvent already defined");
}
}else{
var _31e=[];
var doc=this.handle.ownerDocument;
_31e.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_31b));
this.tempEvents=_31e;
}
if(!this.windowOrLayoutWidget.posStatic){
this.windowOrLayoutWidget.bringToTop(e,false,true,_31c);
}
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
_31c.stopEvent(e);
},onMouseMove:function(e,_321){
var _322=jetspeed;
var _323=dojo;
var _324=_323.event;
if(_321||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
this._cleanUpLastEvt(_323,_324,_322,_322.ui);
var _325=this.windowOrLayoutWidget;
this.beforeDragColRowInfo=null;
if(!_325.isLayoutPane){
var _326=_325.domNode;
if(_326!=null){
this.node=_326;
this.mover=new _323.dnd.Mover(_325,_326,null,_325.cL_NA_ED,this,e,false,_323,_322);
}
}else{
_325.startDragging(e,this,_323,_322);
}
}
_322.stopEvent(e);
},onMouseUp:function(e,_328){
var _329=dojo;
var _32a=jetspeed;
this._cleanUpLastEvt(_329,_329.event,_32a,_32a.ui,_328);
},_cleanUpLastEvt:function(_32b,_32c,_32d,jsUI,_32f){
if(this._mDownEvt!=null){
_32d.stopEvent(this._mDownEvt,_32f);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_32b,_32c,_32d,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_32c);
this.tempEvents=null;
},destroy:function(_330,_331,_332,jsUI){
this._cleanUpLastEvt(_330,_331,_332,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_331);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_335,_336){
var s=_335||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_336);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_336.UAmoz){
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
if(_336.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_341,_342){
var s=_341||dojo.gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_342.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_349,_34a,_34b,_34c,_34d,_34e){
var s=_34d||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_34e);
if(_34b!=null&&_34b>=0){
_34b=Math.max(_34b-pb.w-mb.w,0);
}
if(_34c!=null&&_34c>=0){
_34c=Math.max(_34c-pb.h-mb.h,0);
}
dojo._setBox(node,_349,_34a,_34b,_34c);
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
dojo._getPadExtents=function(n,_35c){
var s=_35c||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_362){
var s=_362||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_367,_368){
var s=_367||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_368.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_370){
var ne="none",px=dojo._toPixelValue,s=_370||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_379,_37a){
return (parseFloat(_37a)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_37c,_37d){
if(!_37d){
return 0;
}
if(_37d.slice&&(_37d.slice(-2)=="px")){
return parseFloat(_37d);
}
with(_37c){
var _37e=style.left;
var _37f=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_37d;
_37d=style.pixelLeft;
}
catch(e){
_37d=0;
}
style.left=_37e;
runtimeStyle.left=_37f;
}
return _37d;
};
}
dojo.gcs=dojo.getComputedStyle;

