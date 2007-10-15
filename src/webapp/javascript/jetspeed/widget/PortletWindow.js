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
if(wDC.windowActionButtonOrder!=null){
if(_3f){
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_45.push(aNm);
_4a[aNm]=true;
}
}else{
for(var aI=(wDC.windowActionButtonOrder.length-1);aI>=0;aI--){
aNm=wDC.windowActionButtonOrder[aI];
_47=false;
if(aNm==_42.ACT_MINIMIZE||aNm==_42.ACT_MAXIMIZE||aNm==_42.ACT_RESTORE||aNm==_42.ACT_MENU||_43.windowActionDesktop[aNm]!=null){
_47=true;
}
if(_47){
_45.push(aNm);
_4a[aNm]=true;
}
}
}
var _4c=(wDC.windowActionButtonMax==null?-1:wDC.windowActionButtonMax);
if(_4c!=-1&&_45.length>=_4c){
var _4d=0;
var _4e=_45.length-_4c+1;
for(var i=0;i<_45.length&&_4d<_4e;i++){
aNm=_45[i];
if(aNm!=_42.ACT_MENU){
_49.push(aNm);
_45[i]=null;
delete _4a[aNm];
_4d++;
}
}
}
if(wDC.windowActionNoImage){
for(var i=0;i<_45.length;i++){
aNm=_45[i];
if(wDC.windowActionNoImage[aNm]!=null){
if(aNm==_42.ACT_MENU){
_48=true;
}else{
_49.push(aNm);
}
_45[i]=null;
delete _4a[aNm];
}
}
}
}
if(wDC.windowActionMenuOrder){
if(_3f){
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
_49.push(aNm);
}
}else{
for(var aI=0;aI<wDC.windowActionMenuOrder.length;aI++){
aNm=wDC.windowActionMenuOrder[aI];
if(_43.windowActionDesktop[aNm]!=null){
_49.push(aNm);
}
}
}
}
var _50=_42.ACT_CHANGEPORTLETTHEME;
var _51=_43.portletDecorationsAllowed;
if(_43.pageEditorLabels&&_51&&_51.length>1){
var _52=_43.pageEditorLabels[_50];
if(_52){
_49.push(_50);
this.actionLabels[_50]=_52;
}
}
var _53=new Array();
if(_49.length>0||this.dbOn){
var _54=new Object();
for(var i=0;i<_49.length;i++){
aNm=_49[i];
if(aNm!=null&&_54[aNm]==null&&_4a[aNm]==null){
_53.push(aNm);
_54[aNm]=true;
}
}
if(this.dbOn){
_53.push({aNm:this.dbMenuDims,dev:true});
}
}
var _55=null;
if(_53.length>0){
var _56={};
var aNm,_57,_58,_59,_5a,_5b;
var _5c=wDC.name+"_menu"+(!_3f?"Np":"");
var _5d=_5c;
_55=_44.widget.createWidget("PopupMenu2",{id:_5d,contextMenuForWindow:false},null);
_55.onItemClick=function(mi){
var _aN=mi.jsActNm;
var _60=this.pWin;
if(!mi.jsActDev){
_60.actionProcess(_aN);
}else{
_60.actionProcessDev(_aN);
}
};
for(var i=0;i<_53.length;i++){
aNm=_53[i];
_5a=null;
_5b=false;
if(!aNm.dev){
_57=this.actionLabels[aNm];
if(aNm==_50){
_5a=_5c+"_sub_"+aNm;
_59=_44.widget.createWidget("PopupMenu2",{id:_5a,contextMenuForWindow:false},null);
_59.onItemClick=function(mi){
var _62=mi.jsPDecNm;
var _63=_55.pWin;
_63.changeDecorator(_62);
};
for(var j=0;j<_51.length;j++){
var _65=_51[j];
var _66=_44.widget.createWidget("MenuItem2",{caption:_65,jsPDecNm:_65});
_59.addChild(_66);
}
_40.appendChild(_59.domNode);
_41.ui.addPopupMenuWidget(_59);
}
}else{
_5b=true;
_57=aNm=aNm.aNm;
}
_58=_44.widget.createWidget("MenuItem2",{caption:_57,submenuId:_5a,jsActNm:aNm,jsActDev:_5b});
_56[aNm]=_58;
_55.addChild(_58);
}
_55.menuItemsByName=_56;
_40.appendChild(_55.domNode);
_41.ui.addPopupMenuWidget(_55);
}
wDC.windowActionMenuHasNoImg=_48;
if(_3f){
wDC.windowActionButtonNames=_45;
wDC.windowActionMenuNames=_53;
wDC.windowActionMenuWidget=_55;
}else{
wDC.windowActionButtonNamesNp=_45;
wDC.windowActionMenuNamesNp=_53;
wDC.windowActionMenuWidgetNp=_55;
}
return _45;
},_setupTitlebar:function(wDC,_68,_69,_6a,doc,_6c,_6d,_6e,_6f,_70,_71){
var _72=_71.event;
var aNm;
var _74=_70.tooltipMgr;
var _75=this.tbNode;
var _76=(_68&&wDC);
if(_68){
if(this.actionMenuWidget&&_68.windowActionMenuHasNoImg){
_6f.evtDisconnect("after",_75,"oncontextmenu",this,"actionMenuOpen",_72);
}
_70.tooltipMgr.removeNodes(this.tooltips);
this.tooltips=ttps=[];
var _77=this.actionButtons;
if(_77){
var _78=(_68&&_68.windowActionButtonTooltip);
for(aNm in _77){
var _79=_77[aNm];
if(_79){
_6f.evtDisconnect("after",_79,"onclick",this,"actionBtnClick",_72);
if(!_78){
_6f.evtDisconnect("after",_79,"onmousedown",_6c,"_stopEvent",_72);
}
if(_76){
_71.dom.removeNode(_79);
}
}
}
this.actionButtons=_77={};
}
}
if(wDC){
if(wDC.windowActionButtonTooltip){
if(this.actionLabels[_6d.ACT_DESKTOP_MOVE_TILED]!=null&&this.actionLabels[_6d.ACT_DESKTOP_MOVE_UNTILED]!=null){
this.tooltips.push(_74.addNode(_75,null,true,1200,this,"getTitleBarTooltip",_6c,_6f,_72));
}
}
var _7a=(_69)?wDC.windowActionButtonNames:wDC.windowActionButtonNamesNp;
if(_7a==null){
_7a=this._buildActionStructures(wDC,_69,_6a,_6c,_6d,_6e,_71);
}
for(var i=0;i<_7a.length;i++){
aNm=_7a[i];
if(aNm!=null){
if(!_69||(aNm==_6d.ACT_RESTORE||aNm==_6d.ACT_MENU||_69.getAction(aNm)!=null||_6e.windowActionDesktop[aNm]!=null)){
this._createActionButtonNode(aNm,doc,_6a,_74,wDC,_6c,_6e,_6f,_71,_72);
}
}
}
this.actionMenuWidget=(_69)?wDC.windowActionMenuWidget:wDC.windowActionMenuWidgetNp;
if(this.actionMenuWidget&&wDC.windowActionMenuHasNoImg){
_6f.evtConnect("after",_75,"oncontextmenu",this,"actionMenuOpen",_72);
}
if(this.ie6&&!wDC._ie6used){
wDC._ie6used=true;
this.actionBtnSyncDefer();
}else{
this.actionBtnSync(_6c,_6d);
}
if(wDC.windowDisableResize){
this.resizable=false;
}
if(wDC.windowDisableMove){
this.moveable=false;
}
}
},_createActionButtonNode:function(aNm,doc,_7e,_7f,wDC,_81,_82,_83,_84,_85){
if(aNm!=null){
var _86=doc.createElement("div");
_86.className="portletWindowActionButton";
_86.style.backgroundImage="url("+_82.getPortletDecorationBaseUrl(this.decName)+"/images/desktop/"+aNm+".gif)";
_86.actionName=aNm;
this.actionButtons[aNm]=_86;
this.tbNode.appendChild(_86);
_83.evtConnect("after",_86,"onclick",this,"actionBtnClick",_85);
if(wDC.windowActionButtonTooltip){
var _87=this.actionLabels[aNm];
this.tooltips.push(_7f.addNode(_86,_87,true,null,null,null,_81,_83,_85));
}else{
_83.evtConnect("after",_86,"onmousedown",_81,"_stopEvent",_85);
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
},_createLayoutInfo:function(_88,_89,_8a,_8b,_8c,_8d,_8e,_8f,_90){
var _91=_8e.gcs(_8a);
var _92=_8e.gcs(_8b);
var _93=_90.getLayoutExtents(_8a,_91,_8e,_8f);
var _94=_90.getLayoutExtents(_8b,_92,_8e,_8f);
var _95={dNode:_93,cNode:_94};
var _96=Math.max(0,_94.mE.t);
var _97=Math.max(0,_94.mE.h-_94.mE.t);
var _98=0;
var _99=0;
var _9a=null;
if(_8c){
var _9b=_8e.gcs(_8c);
_9a=_90.getLayoutExtents(_8c,_9b,_8e,_8f);
var _9c=_9b.cursor;
if(_9c==null||_9c.length==0){
_9c="move";
}
_88.dragCursor=_9c;
_9a.mBh=_8e.getMarginBox(_8c,_9b,_8f).h;
var _9d=Math.max(0,_9a.mE.h-_9a.mE.t);
_98=(_9a.mBh-_9d)+Math.max(0,(_9d-_96));
_95.tbNode=_9a;
}
var _9e=null;
if(_8d){
var _9f=_8e.gcs(_8d);
_9e=_90.getLayoutExtents(_8d,_9f,_8e,_8f);
_9e.mBh=_8e.getMarginBox(_8d,_9f,_8f).h;
var _a0=Math.max(0,_9e.mE.t);
_99=(_9e.mBh-_a0)+Math.max(0,(_a0-_97));
_95.rbNode=_9e;
}
_95.cNode_mBh_LessBars=_98+_99;
if(!_89){
_88.layout=_95;
}else{
_88.layoutIFrame=_95;
}
},actionBtnClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.actionProcess(evt.target.actionName,evt);
},actionMenuOpen:function(evt){
var _a3=jetspeed;
var _a4=_a3.id;
var _a5=this.actionMenuWidget;
if(!_a5){
return;
}
if(_a5.isShowingNow){
_a5.close();
}
var _a6=null;
var _a7=null;
if(this.portlet){
_a6=this.portlet.getCurrentActionState();
_a7=this.portlet.getCurrentActionMode();
}
var _a8=_a5.menuItemsByName;
for(var aNm in _a8){
var _aa=_a8[aNm];
var _ab=(this._isActionEnabled(aNm,_a6,_a7,_a3,_a4))?"":"none";
_aa.domNode.style.display=_ab;
}
_a5.pWin=this;
_a5.onOpen(evt);
},actionProcessDev:function(aNm,evt){
if(aNm==this.dbMenuDims&&jetspeed.debugPWinPos){
jetspeed.debugPWinPos(this);
}
},actionProcess:function(aNm,evt){
var _b0=jetspeed;
var _b1=_b0.id;
if(aNm==null){
return;
}
if(_b0.prefs.windowActionDesktop[aNm]!=null){
if(aNm==_b1.ACT_DESKTOP_TILE){
this.makeTiled();
}else{
if(aNm==_b1.ACT_DESKTOP_UNTILE){
this.makeUntiled();
}else{
if(aNm==_b1.ACT_DESKTOP_HEIGHT_EXPAND){
this.makeHeightToFit(false);
}else{
if(aNm==_b1.ACT_DESKTOP_HEIGHT_NORMAL){
this.makeHeightVariable(false,false);
}
}
}
}
}else{
if(aNm==_b1.ACT_MENU){
this.actionMenuOpen(evt);
}else{
if(aNm==_b1.ACT_MINIMIZE){
if(this.portlet&&this.windowState==_b1.ACT_MAXIMIZE){
this.needsRenderOnRestore=true;
}
this.minimizeWindow();
if(this.portlet){
_b0.changeActionForPortlet(this.portlet.getId(),_b1.ACT_MINIMIZE,null);
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_b1.ACT_RESTORE){
var _b2=false;
if(this.portlet){
if(this.windowState==_b1.ACT_MAXIMIZE||this.needsRenderOnRestore){
if(this.needsRenderOnRestore){
_b2=true;
this.restoreOnNextRender=true;
this.needsRenderOnRestore=false;
}
if(this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.portlet.renderAction(aNm);
}else{
_b0.changeActionForPortlet(this.portlet.getId(),_b1.ACT_RESTORE,null);
}
}
if(!_b2){
this.restoreWindow();
}
if(!this.portlet){
this.actionBtnSyncDefer();
}
}else{
if(aNm==_b1.ACT_MAXIMIZE){
if(this.portlet&&this.iframesInfo){
this.iframesInfo.iframesSize=[];
}
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(aNm);
}else{
this.actionBtnSync(_b0,_b1);
}
}else{
if(aNm==_b1.ACT_REMOVEPORTLET){
if(this.portlet){
var _b3=dojo.widget.byId(_b1.PG_ED_WID);
if(_b3!=null){
_b3.deletePortlet(this.portlet.entityId,this.title);
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
},_isActionEnabled:function(aNm,_b5,_b6,_b7,_b8){
var _b7=jetspeed;
var _b8=_b7.id;
var _b9=false;
var _ba=this.windowState;
if(this.minimizeTempRestore!=null){
if(this.portlet){
var _bb=this.portlet.getAction(aNm);
if(_bb!=null){
if(_bb.id==_b8.ACT_REMOVEPORTLET){
if(_b7.page.editMode&&this.getLayoutActionsEnabled()){
_b9=true;
}
}
}
}
}else{
if(aNm==_b8.ACT_MENU){
if(!this._actionMenuIsEmpty(_b7,_b8)){
_b9=true;
}
}else{
if(_b7.prefs.windowActionDesktop[aNm]!=null){
if(this.getLayoutActionsEnabled()){
var _bc=(this.ie6&&_ba==_b8.ACT_MINIMIZE);
if(aNm==_b8.ACT_DESKTOP_HEIGHT_EXPAND){
if(!this.heightToFit&&!_bc){
_b9=true;
}
}else{
if(aNm==_b8.ACT_DESKTOP_HEIGHT_NORMAL){
if(this.heightToFit&&!_bc){
_b9=true;
}
}else{
if(aNm==_b8.ACT_DESKTOP_TILE&&_b7.prefs.windowTiling){
if(!this.posStatic){
_b9=true;
}
}else{
if(aNm==_b8.ACT_DESKTOP_UNTILE){
if(this.posStatic){
_b9=true;
}
}
}
}
}
}
}else{
if(aNm==_b8.ACT_CHANGEPORTLETTHEME){
_b9=this.editPageEnabled;
}else{
if(this.portlet){
var _bb=this.portlet.getAction(aNm);
if(_bb!=null){
if(_bb.id==_b8.ACT_REMOVEPORTLET){
if(_b7.page.editMode&&this.getLayoutActionsEnabled()){
_b9=true;
}
}else{
if(_bb.type==_b8.PORTLET_ACTION_TYPE_MODE){
if(aNm!=_b6){
_b9=true;
}
}else{
if(aNm!=_b5){
_b9=true;
}
}
}
}else{
if(aNm==this.dbMenuDims){
_b9=true;
}
}
}else{
if(aNm==_b8.ACT_MAXIMIZE){
if(aNm!=_ba&&this.minimizeTempRestore==null){
_b9=true;
}
}else{
if(aNm==_b8.ACT_MINIMIZE){
if(aNm!=_ba){
_b9=true;
}
}else{
if(aNm==_b8.ACT_RESTORE){
if(_ba==_b8.ACT_MAXIMIZE||_ba==_b8.ACT_MINIMIZE){
_b9=true;
}
}else{
if(aNm==this.dbMenuDims){
_b9=true;
}
}
}
}
}
}
}
}
}
return _b9;
},_actionMenuIsEmpty:function(_bd,_be){
var _bf=true;
var _c0=this.actionMenuWidget;
if(_c0){
var _c1=null;
var _c2=null;
if(this.portlet){
_c1=this.portlet.getCurrentActionState();
_c2=this.portlet.getCurrentActionMode();
}
for(var aNm in _c0.menuItemsByName){
if(aNm!=_be.ACT_MENU&&this._isActionEnabled(aNm,_c1,_c2,_bd,_be)){
_bf=false;
break;
}
}
}
return _bf;
},actionBtnSyncDefer:function(){
dojo.lang.setTimeout(this,this.actionBtnSync,10);
},actionBtnSync:function(_c4,_c5){
if(!_c4){
_c4=jetspeed;
_c5=_c4.id;
}
var _c6=null;
var _c7=null;
if(this.portlet){
_c6=this.portlet.getCurrentActionState();
_c7=this.portlet.getCurrentActionMode();
}
for(var aNm in this.actionButtons){
var _c9=this._isActionEnabled(aNm,_c6,_c7,_c4,_c5);
var _ca=this.actionButtons[aNm];
_ca.style.display=(_c9)?"block":"none";
}
},_postCreateMaximizeWindow:function(){
var _cb=jetspeed;
var _cc=_cb.id;
this.maximizeWindow();
if(this.portlet){
this.portlet.renderAction(_cc.ACT_MAXIMIZE);
}else{
this.actionBtnSync(_cb,_cc);
}
},minimizeWindowTemporarily:function(){
var _cd=jetspeed;
var _ce=_cd.id;
if(this.minimizeTempRestore==null){
this.minimizeTempRestore=this.windowState;
if(this.windowState!=_ce.ACT_MINIMIZE){
this.minimizeWindow(false);
}
this.actionBtnSync(_cd,_ce);
}
},restoreFromMinimizeWindowTemporarily:function(){
var _cf=jetspeed;
var _d0=_cf.id;
var _d1=this.minimizeTempRestore;
this.minimizeTempRestore=null;
if(_d1){
if(_d1!=_d0.ACT_MINIMIZE){
this.restoreWindow();
}
this.actionBtnSync(_cf,_d0);
}
},minimizeWindow:function(_d2){
if(!this.tbNode){
return;
}
var _d3=jetspeed;
if(this.windowState==jetspeed.id.ACT_MAXIMIZE){
_d3.widget.showAllPortletWindows();
this.restoreWindow();
}else{
if(!_d2){
this._updtDimsObj(false,false);
}
}
var _d4=_d3.css.cssDis;
this.cNodeCss[_d4]="none";
if(this.rbNodeCss){
this.rbNodeCss[_d4]="none";
}
this.windowState=_d3.id.ACT_MINIMIZE;
if(this.ie6){
this.containerNode.style.display="none";
}
this._alterCss(true,true);
},maximizeWindow:function(){
var _d5=jetspeed;
var _d6=_d5.id;
var _d7=this.domNode;
var _d8=[this.widgetId];
_d5.widget.hideAllPortletWindows(_d8);
if(this.windowState==_d6.ACT_MINIMIZE){
this.restoreWindow();
}
var _d9=this.posStatic;
this.preMaxPosStatic=_d9;
this.preMaxHeightToFit=this.heightToFit;
var _da=_d9;
this._updtDimsObj(false,_da);
var _db=document.getElementById(_d6.DESKTOP);
var _dc=dojo.html.getAbsolutePosition(_db,true).y;
var _dd=dojo.html.getViewport();
var _de=dojo.html.getPadding(_d5.docBody);
this.dimsUntiledTemp={w:_dd.width-_de.width-2,h:_dd.height-_de.height-_dc,l:1,t:_dc};
this._setTitleBarDragging(true,_d5.css,false);
this.posStatic=false;
this.heightToFit=false;
this._alterCss(true,true);
if(_d9){
_db.appendChild(_d7);
}
this.windowState=_d6.ACT_MAXIMIZE;
},restoreWindow:function(){
var _df=jetspeed;
var _e0=_df.id;
var _e1=_df.css;
var _e2=this.domNode;
var _e3=false;
if(_e2.style.position=="absolute"){
_e3=true;
}
var _e4=null;
if(this.windowState==_e0.ACT_MAXIMIZE){
_df.widget.showAllPortletWindows();
this.posStatic=this.preMaxPosStatic;
this.heightToFit=this.preMaxHeightToFit;
this.dimsUntiledTemp=null;
}
var _e5=_e1.cssDis;
this.cNodeCss[_e5]="block";
if(this.rbNodeCss&&this.resizebarEnabled){
this.rbNodeCss[_e5]="block";
}
this.windowState=_e0.ACT_RESTORE;
this._setTitleBarDragging(true,_df.css);
var ie6=this.ie6;
if(!ie6){
this._alterCss(true,true);
}else{
var _e7=null;
if(this.heightToFit){
_e7=this.iNodeCss;
this.iNodeCss=null;
}
this._alterCss(true,true);
this._updtDimsObj(false,false,true,false,true);
if(_e7!=null){
this.iNodeCss=_e7;
}
this._alterCss(false,false,true);
}
if(this.posStatic&&_e3){
this._tileWindow(_df);
}
},_tileWindow:function(_e8){
if(!this.posStatic){
return;
}
var _e9=this.domNode;
var _ea=this.getDimsObj(this.posStatic);
var _eb=true;
if(_ea!=null){
var _ec=_ea.colInfo;
if(_ec!=null&&_ec.colI!=null){
var _ed=_e8.page.columns[_ec.colI];
var _ee=((_ed!=null)?_ed.domNode:null);
if(_ee!=null){
var _ef=null;
var _f0=_ee.childNodes.length;
if(_f0==0){
_ee.appendChild(_e9);
_eb=false;
}else{
var _f1,_f2,_f3=0;
if(_ec.pSibId!=null||_ec.nSibId!=null){
_f1=_ee.firstChild;
do{
_f2=_f1.id;
if(_f2==null){
continue;
}
if(_f2==_ec.pSibId){
dojo.dom.insertAfter(_e9,_f1);
_eb=false;
}else{
if(_f2==_ec.nSibId){
dojo.dom.insertBefore(_e9,_f1);
_eb=false;
}else{
if(_f3==_ec.elmtI){
_ef=_f1;
}
}
}
_f1=_f1.nextSibling;
_f3++;
}while(_eb&&_f1!=null);
}
}
if(_eb){
if(_ef!=null){
dojo.dom.insertBefore(_e9,_ef);
}else{
dojo.dom.prependChild(_e9,_ee);
}
_eb=false;
}
}
}
}
if(_eb){
var _f4=_e8.page.getColumnDefault();
if(_f4!=null){
dojo.dom.prependChild(_e9,_f4.domNode);
}
}
},getDimsObj:function(_f5,_f6){
return (_f5?((this.dimsTiledTemp!=null&&!_f6)?this.dimsTiledTemp:this.dimsTiled):((this.dimsUntiledTemp!=null&&!_f6)?this.dimsUntiledTemp:this.dimsUntiled));
},_updtDimsObj:function(_f7,_f8,_f9,_fa,_fb,_fc){
var _fd=jetspeed;
var _fe=dojo;
var _ff=this.domNode;
var _100=this.posStatic;
var _101=this.getDimsObj(_100,_fc);
var _102=(!_f9&&!_100&&(!_f7||_101.l==null||_101.t==null));
var _103=(!_fa&&(!_f7||_102||_fb||_101.w==null||_101.h==null));
if(_103||_102){
var _104=this._getLayoutInfo().dNode;
if(_103){
var _105=_fd.ui.getMarginBoxSize(_ff,_104);
_101.w=_105.w;
_101.h=_105.h;
if(!_100){
_102=true;
}
}
if(_102){
var _106=_fe.html.getAbsolutePosition(_ff,true);
_101.l=_106.x-_104.mE.l-_104.pbE.l;
_101.t=_106.y-_104.mE.t-_104.pbE.t;
}
}
if(_100){
if(_f8||_fc&&_101.colInfo==null){
var _107=0,_108=_ff.previousSibling,_109=_ff.nextSibling;
var _10a=(_108!=null?_108.id:null),_10b=(_109!=null?_109.id:null);
if(_108!=null){
_10a=_108.id;
}
while(_108!=null){
_107++;
_108=_108.previousSibling;
}
_101.colInfo={elmtI:_107,pSibId:_10a,nSibId:_10b,colI:this.getPageColumnIndex()};
}
if(_fc){
this.dimsTiledTemp={w:_101.w,h:_101.h,colInfo:_101.colInfo};
_101=this.dimsTiledTemp;
}
}else{
if(_fc){
this.dimsUntiledTemp={w:_101.w,h:_101.h,l:_101.l,t:_101.t};
_101=this.dimsUntiledTemp;
}
}
return _101;
},getLayoutActionsEnabled:function(){
return (this.windowState!=jetspeed.id.ACT_MAXIMIZE&&(!this.portlet||!this.portlet.layoutActionsDisabled));
},_setTitleBarDragging:function(_10c,_10d,_10e){
var _10f=this.tbNode;
if(!_10f){
return;
}
if(typeof _10e=="undefined"){
_10e=this.getLayoutActionsEnabled();
}
var _110=this.resizeHandle;
var _111=null;
var wDC=this.decConfig;
var _113=_10e;
if(_113&&!this.resizebarEnabled){
_113=false;
}
if(_10e&&!this.titlebarEnabled){
_10e=false;
}
if(_10e){
_111=wDC.dragCursor;
if(this.drag){
this.drag.enable();
}
}else{
_111="default";
if(this.drag){
this.drag.disable();
}
}
if(_113){
if(_110){
_110.domNode.style.display="";
}
}else{
if(_110){
_110.domNode.style.display="none";
}
}
this.tbNodeCss[_10d.cssCur]=_111;
if(!_10c){
_10f.style.cursor=_111;
}
},onMouseDown:function(evt){
this.bringToTop(evt,false,false,jetspeed);
},bringToTop:function(evt,_116,_117,_118){
if(!this.posStatic){
var _119=_118.page;
var _11a=_118.css;
var _11b=this.dNodeCss;
var _11c=_119.getPWinHighZIndex();
var zCur=_11b[_11a.cssZIndex];
if(_11c!=zCur){
var zTop=this._setAsTopZIndex(_119,_11a,_11b,false);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
if(!_117&&this.portlet&&this.windowState!=jetspeed.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
}
}
}else{
if(_116){
var zTop=this._setAsTopZIndex(_119,_11a,_11b,true);
if(this.windowInitialized){
this.domNode.style.zIndex=zTop;
}
}
}
},_setAsTopZIndex:function(_11f,_120,_121,_122){
var zTop=String(_11f.getPWinTopZIndex(_122));
_121[_120.cssZIndex]=zTop;
return zTop;
},makeUntiled:function(){
var _124=jetspeed;
this._updtDimsObj(false,true);
this.posStatic=false;
this._updtDimsObj(true,false);
this._setAsTopZIndex(_124.page,_124.css,this.dNodeCss,false);
this._alterCss(true,true);
var _125=document.getElementById(jetspeed.id.DESKTOP);
_125.appendChild(this.domNode);
if(this.windowState==_124.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},makeTiled:function(){
this.posStatic=true;
var _126=jetspeed;
this._setAsTopZIndex(_126.page,_126.css,this.dNodeCss,true);
this._alterCss(true,true);
this._tileWindow(_126);
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
},makeHeightToFit:function(_127){
var _128=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
if(this.ie6){
var _129=this.iNodeCss;
this.iNodeCss=null;
this._alterCss(false,true);
this._updtDimsObj(false,false,true,false,true);
this.iNodeCss=_129;
}
this._alterCss(false,true);
if(!_127&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_12a,_12b){
var _12c=this.getDimsObj(this.posStatic);
var _12d=this._getLayoutInfo().dNode;
var _12e=jetspeed.ui.getMarginBoxSize(this.domNode,_12d);
_12c.w=_12e.w;
_12c.h=_12e.h;
this.heightToFit=false;
this._alterCss(false,true);
if(!_12b&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_12a&&this.portlet){
this.portlet.submitWinState();
}
},editPageInitiate:function(_12f,_130){
this.editPageEnabled=true;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _132=_12f.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_132]="block";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=true;
if(this.rbNodeCss){
this.rbNodeCss[_132]="block";
}
}
this._setTitleBarDragging(true,_12f);
if(!_130){
this._alterCss(true,true);
}
}
},editPageTerminate:function(_133,_134){
this.editPageEnabled=false;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _136=_133.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=false;
if(this.tbNodeCss){
this.tbNodeCss[_136]="none";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=false;
if(this.rbNodeCss){
this.rbNodeCss[_136]="none";
}
}
this._setTitleBarDragging(true,_133);
if(!_134){
this._alterCss(true,true);
}
}
},changeDecorator:function(_137){
var _138=jetspeed;
var _139=_138.css;
var jsId=_138.id;
var jsUI=_138.ui;
var _13c=_138.prefs;
var _13d=dojo;
var _13e=this.decConfig;
if(_13e&&_13e.name==_137){
return;
}
var wDC=_138.loadPortletDecorationStyles(_137,_13c);
if(!wDC){
return;
}
var _140=this.portlet;
if(_140){
_140._submitAjaxApi("updatepage","&method=update-portlet-decorator&portlet-decorator="+_137);
}
this.decConfig=wDC;
this.decName=wDC.name;
var _141=this.domNode;
var _142=this.containerNode;
var _143=this.iframesInfo;
var _144=(_143&&_143.layout);
var _145=(!_144?wDC.layout:wDC.layoutIFrame);
if(!_145){
if(!_144){
this._createLayoutInfo(wDC,false,_141,_142,this.tbNode,this.rbNode,_13d,_138,jsUI);
}else{
this._createLayoutInfo(wDC,true,_141,_142,this.tbNode,this.rbNode,_13d,_138,jsUI);
}
}
this._setupTitlebar(wDC,_13e,this.portlet,_138.docBody,document,_138,_138.id,_13c,jsUI,_138.page,_13d);
_141.className=wDC.dNodeClass;
if(_144){
_142.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
}else{
_142.className=wDC.cNodeClass;
}
var _146=_139.cssDis;
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_146]="block";
}
this.resizebarEnabled=true;
if(this.rbNodeCss){
this.rbNodeCss[_146]="block";
}
if(this.editPageEnabled){
this.editPageInitiate(_139,true);
}else{
this.editPageTerminate(_139,true);
}
this._setTitleBarDragging(true,_139);
this._alterCss(true,true);
},resizeTo:function(w,h,_149){
var _14a=this.getDimsObj(this.posStatic);
_14a.w=w;
_14a.h=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _14b=this.resizeHandle;
if(_14b!=null&&_14b._isSizing){
jetspeed.ui.evtConnect("after",_14b,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
this.resizeNotifyChildWidgets();
},resizeNotifyChildWidgets:function(){
if(this.childWidgets){
var _14c=this.childWidgets;
var _14d=_14c.length,_14e;
for(var i=0;i<_14d;i++){
try{
_14e=_14c[i];
if(_14e){
_14e.checkSize();
}
}
catch(e){
}
}
}
},_getLayoutInfo:function(){
var _150=this.iframesInfo;
return ((!(_150&&_150.layout))?this.decConfig.layout:this.decConfig.layoutIFrame);
},_getLayoutInfoMoveable:function(){
return this._getLayoutInfo().dNode;
},onBrowserWindowResize:function(){
if(this.ie6){
this._resetIE6TiledSize(false);
}
},_resetIE6TiledSize:function(_151){
var _152=this.posStatic;
if(_152){
var _153=this.domNode;
var _154=this.getDimsObj(_152);
_154.w=Math.max(0,this.domNode.parentNode.offsetWidth-this.colWidth_pbE);
this._alterCss(_151,false,false,false,true);
}
},_alterCss:function(_155,_156,_157,_158,_159,_15a){
var _15b=jetspeed;
var _15c=_15b.css;
var _15d=this.iframesInfo;
var _15e=(_15d&&_15d.layout);
var _15f=(!_15e?this.decConfig.layout:this.decConfig.layoutIFrame);
var _160=this.dNodeCss,_161=null,_162=null,_163=null,_164=false,_165=this.iNodeCss,_166=null;
if(_165&&_15e){
_166=_15d.iframeCoverIE6Css;
}
var _167=this.posStatic;
var _168=(_167&&_165==null);
var _169=this.heightToFit;
var _16a=(_155||_159||(_157&&!_168));
var _16b=(_156||_157);
var _16c=(_155||_158);
var _16d=(_156||(_157&&_15e));
var _16e=this.getDimsObj(_167);
if(_155){
_160[_15c.cssPos]=(_167?"relative":"absolute");
}
var _16f=null,_170=null;
if(_156){
if(_15e){
var _171=this.getIFrames(false);
if(_171&&_171.iframes.length==1&&_15d.iframesSize!=null&&_15d.iframesSize.length==1){
var _172=_15d.iframesSize[0].h;
if(_172!=null){
_16f=_171.iframes[0];
_170=(_169?_172:(!_15b.UAie?"100%":"99%"));
_15a=false;
}
}
}
}
if(_16d){
_161=this.cNodeCss;
var _173=_15c.cssOx,_174=_15c.cssOy;
if(_169&&!_15e){
_160[_174]="visible";
_161[_174]="visible";
}else{
_160[_174]="hidden";
_161[_174]=(!_15e?"auto":"hidden");
}
}
if(_16c){
var lIdx=_15c.cssL,_176=_15c.cssLU;
var tIdx=_15c.cssT,_178=_15c.cssTU;
if(_167){
_160[lIdx]="auto";
_160[_176]="";
_160[tIdx]="auto";
_160[_178]="";
}else{
_160[lIdx]=_16e.l;
_160[_176]="px";
_160[tIdx]=_16e.t;
_160[_178]="px";
}
}
if(_16b){
_161=this.cNodeCss;
var hIdx=_15c.cssH,_17a=_15c.cssHU;
if(_169&&_165==null){
_160[hIdx]="";
_160[_17a]="";
_161[hIdx]="";
_161[_17a]="";
}else{
var h=_16e.h;
var _17c=_15b.css.cssDis;
var _17d;
var _17e;
if(_161[_17c]=="none"){
_17d=_15f.tbNode.mBh;
_17e="";
_161[_17a]="";
}else{
_17d=(h-_15f.dNode.lessH);
_17e=_17d-_15f.cNode.lessH-_15f.cNode_mBh_LessBars;
_161[_17a]="px";
}
_160[hIdx]=_17d;
_160[_17a]="px";
_161[hIdx]=_17e;
if(_165){
_165[hIdx]=_17d;
_165[_17a]="px";
_164=true;
if(_166){
_166[hIdx]=_17e;
_166[_17a]=_161[_17a];
}
}
}
}
if(_16a){
var w=_16e.w;
_161=this.cNodeCss;
_162=this.tbNodeCss;
_163=this.rbNodeCss;
var wIdx=_15c.cssW,_181=_15c.cssWU;
if(_168&&(!this.ie6||!w)){
_160[wIdx]="";
_160[_181]="";
_161[wIdx]="";
_161[_181]="";
if(_162){
_162[wIdx]="";
_162[_181]="";
}
if(_163){
_163[wIdx]="";
_163[_181]="";
}
}else{
var _182=(w-_15f.dNode.lessW);
_160[wIdx]=_182;
_160[_181]="px";
_161[wIdx]=_182-_15f.cNode.lessW;
_161[_181]="px";
if(_162){
_162[wIdx]=_182-_15f.tbNode.lessW;
_162[_181]="px";
}
if(_163){
_163[wIdx]=_182-_15f.rbNode.lessW;
_163[_181]="px";
}
if(_165){
_165[wIdx]=_182;
_165[_181]="px";
_164=true;
if(_166){
_166[wIdx]=_161[wIdx];
_166[_181]=_161[_181];
}
}
}
}
if(!_15a){
this.domNode.style.cssText=_160.join("");
if(_161){
this.containerNode.style.cssText=_161.join("");
}
if(_162){
this.tbNode.style.cssText=_162.join("");
}
if(_163){
this.rbNode.style.cssText=_163.join("");
}
if(_164){
this.bgIframe.iframe.style.cssText=_165.join("");
if(_166){
_15d.iframeCover.style.cssText=_166.join("");
}
}
}
if(_16f&&_170){
this._deferSetIFrameH(_16f,_170,false,50);
}
},_deferSetIFrameH:function(_183,_184,_185,_186,_187){
if(!_186){
_186=100;
}
var pWin=this;
window.setTimeout(function(){
_183.height=_184;
if(_185){
if(_187==null){
_187=50;
}
if(_187==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_187);
}
}
},_186);
},_getWindowMarginBox:function(_189,_18a){
var _18b=this.domNode;
if(_189==null){
_189=this._getLayoutInfo().dNode;
}
var _18c=null;
if(_18a.UAope){
_18c=(this.posStatic?_18a.page.layoutInfo.column:_18a.page.layoutInfo.desktop);
}
return _18a.ui.getMarginBox(_18b,_189,_18c,_18a);
},_forceRefreshZIndex:function(){
var _18d=jetspeed;
var zTop=this._setAsTopZIndex(_18d.page,_18d.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=zTop;
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},getIFrames:function(_190){
var _191=this.containerNode.getElementsByTagName("iframe");
if(_191&&_191.length>0){
if(!_190){
return {iframes:_191};
}
var _192=[];
for(var i=0;i<_191.length;i++){
var ifrm=_191[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_192.push({w:w,h:h});
}
return {iframes:_191,iframesSize:_192};
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
var _198=jetspeed;
var jsUI=_198.ui;
var _19a=_198.page;
var _19b=dojo;
var _19c=_19b.event;
var wDC=this.decConfig;
if(this.iframesInfo){
_19a.unregPWinIFrameCover(this);
}
this._setupTitlebar(null,wDC,this.portlet,_198.docBody,document,_198,_198.id,_198.prefs,jsUI,_19a,_19b);
if(this.drag){
this.drag.destroy(_19b,_19c,_198,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_19c,_198,jsUI);
this.resizeHandle=null;
}
this._destroyChildWidgets(_19b);
this._removeUntiledEvents();
var _19e=this.domNode;
if(_19e&&_19e.parentNode){
_19e.parentNode.removeChild(_19e);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},_destroyChildWidgets:function(_19f){
if(this.childWidgets){
var _1a0=this.childWidgets;
var _1a1=_1a0.length,_1a2,swT,swI;
_19f.debug("PortletWindow ["+this.widgetId+"] destroy child widgets ("+_1a1+")");
for(var i=(_1a1-1);i>=0;i--){
try{
_1a2=_1a0[i];
if(_1a2){
swT=_1a2.widgetType;
swI=_1a2.widgetId;
_1a2.destroy();
_19f.debug("destroyed child widget["+i+"]: "+swT+" "+swI);
}
_1a0[i]=null;
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
},endDragging:function(_1a7,_1a8,_1a9){
var _1aa=jetspeed;
var ie6=this.ie6;
if(_1a8){
this.posStatic=false;
}else{
if(_1a9){
this.posStatic=true;
}
}
var _1ac=this.posStatic;
if(!_1ac){
var _1ad=this.getDimsObj(_1ac);
if(_1a7&&_1a7.left!=null&&_1a7.top!=null){
_1ad.l=_1a7.left;
_1ad.t=_1a7.top;
if(!_1a8){
this._alterCss(false,false,false,true,false,true);
}
}
if(_1a8){
this._updtDimsObj(false,false,true);
this._alterCss(true,true,false,true);
this._addUntiledEvents();
}
}else{
if(_1a9){
this._setAsTopZIndex(_1aa.page,_1aa.css,this.dNodeCss,_1ac);
this._updtDimsObj(false,false);
}
if(!ie6){
this._alterCss(true);
this.resizeNotifyChildWidgets();
}else{
this._resetIE6TiledSize(_1a9);
}
}
if(this.portlet&&this.windowState!=_1aa.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,_1aa.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_1ae){
var _1af=this.domNode;
var _1b0=this.posStatic;
if(!_1af){
return null;
}
var _1b1=_1af.style;
var _1b2={};
if(!_1b0){
_1b2.zIndex=_1b1.zIndex;
}
if(_1ae){
return _1b2;
}
var _1b3=this.getDimsObj(_1b0);
_1b2.width=(_1b3.w?String(_1b3.w):"");
_1b2.height=(_1b3.h?String(_1b3.h):"");
_1b2[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_1b0;
_1b2[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_1b0){
_1b2.left=(_1b3.l!=null?String(_1b3.l):"");
_1b2.top=(_1b3.t!=null?String(_1b3.t):"");
}else{
var _1b4=jetspeed.page.getPortletCurColRow(_1af);
if(_1b4!=null){
_1b2.column=_1b4.column;
_1b2.row=_1b4.row;
_1b2.layout=_1b4.layout;
}else{
throw new Error("Can't find row/col/layout for window: "+this.widgetId);
}
}
return _1b2;
},getCurWinStateForPersist:function(_1b5){
var _1b6=this.getCurWinState(_1b5);
this._mkNumProp(null,_1b6,"left");
this._mkNumProp(null,_1b6,"top");
this._mkNumProp(null,_1b6,"width");
this._mkNumProp(null,_1b6,"height");
return _1b6;
},_mkNumProp:function(_1b7,_1b8,_1b9){
var _1ba=(_1b8!=null&&_1b9!=null);
if(_1b7==null&&_1ba){
_1b7=_1b8[_1b9];
}
if(_1b7==null||_1b7.length==0){
_1b7=0;
}else{
var _1bb="";
for(var i=0;i<_1b7.length;i++){
var _1bd=_1b7.charAt(i);
if((_1bd>="0"&&_1bd<="9")||_1bd=="."){
_1bb+=_1bd.toString();
}
}
if(_1bb==null||_1bb.length==0){
_1bb="0";
}
if(_1ba){
_1b8[_1b9]=_1bb;
}
_1b7=new Number(_1bb);
}
return _1b7;
},setPortletContent:function(html,url){
var _1c0=jetspeed;
var _1c1=dojo;
var ie6=this.ie6;
var _1c3=null;
if(ie6){
_1c3=this.iNodeCss;
if(this.heightToFit){
this.iNodeCss=null;
this._alterCss(false,true);
}
}
var _1c4=html.toString();
if(!this.exclPContent){
_1c4="<div class=\"PContent\" >"+_1c4+"</div>";
}
var _1c5=this._splitAndFixPaths_scriptsonly(_1c4,url);
var _1c6=this.setContent(_1c5,_1c1);
this.childWidgets=((_1c6&&_1c6.length>0)?_1c6:null);
if(_1c5.scripts!=null&&_1c5.scripts.length!=null&&_1c5.scripts.length>0){
this._executeScripts(_1c5.scripts,_1c1);
this.onLoad();
}
if(_1c0.debug.setPortletContent){
_1c1.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
var _1c7=this.containerNode;
if(this.portlet){
this.portlet.postParseAnnotateHtml(_1c7);
}
var _1c8=this.iframesInfo;
var _1c9=this.getIFrames(true);
var _1ca=null,_1cb=false;
if(_1c9!=null){
if(_1c8==null){
this.iframesInfo=_1c8={};
var _1cc=_1c7.ownerDocument.createElement("div");
var _1cd="portletWindowIFrameCover";
_1cc.className=_1cd;
_1c7.appendChild(_1cc);
if(_1c0.UAie){
_1cc.className=(_1cd+"IE")+" "+_1cd;
if(ie6){
_1c8.iframeCoverIE6Css=_1c0.css.cssWidthHeight.concat();
}
}
_1c8.iframeCover=_1cc;
_1c0.page.regPWinIFrameCover(this);
}
var _1ce=_1c8.iframesSize=_1c9.iframesSize;
var _1cf=_1c9.iframes;
var _1d0=_1c8.layout;
var _1d1=_1c8.layout=(_1cf.length==1&&_1ce[0].h!=null);
if(_1d0!=_1d1){
_1cb=true;
}
if(_1d1){
if(!this.heightToFit){
_1ca=_1cf[0];
}
var wDC=this.decConfig;
var _1c7=this.containerNode;
_1c7.firstChild.className="PContent portletIFramePContent";
_1c7.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
if(!wDC.layoutIFrame){
this._createLayoutInfo(wDC,true,this.domNode,_1c7,this.tbNode,this.rbNode,_1c1,_1c0,_1c0.ui);
}
}
}else{
if(_1c8!=null){
if(_1c8.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_1cb=true;
}
this.iframesInfo=null;
_1c0.page.unregPWinIFrameCover(this);
}
}
if(_1cb){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(ie6){
this._updtDimsObj(false,false,true,false,true);
if(_1c3==null){
var _1d3=_1c0.css;
_1c3=_1d3.cssHeight.concat();
_1c3[_1d3.cssDis]="inline";
}
this.iNodeCss=_1c3;
this._alterCss(false,false,true);
}
if(this.minimizeOnNextRender){
this.minimizeOnNextRender=false;
this.minimizeWindow(true);
this.actionBtnSync(_1c0,_1c0.id);
this.needsRenderOnRestore=true;
}
if(_1ca){
this._deferSetIFrameH(_1ca,(!_1c0.UAie?"100%":"99%"),true);
}
},setContent:function(data,_1d5){
var _1d6=null;
var step=1;
try{
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
step=2;
this._setContent(data.xml,_1d5);
step=3;
if(this.parseContent){
var node=this.containerNode;
var _1d9=new _1d5.xml.Parse();
var frag=_1d9.parseElement(node,null,true);
_1d6=_1d5.widget.getParser().createSubComponents(frag,null);
}
}
catch(e){
dojo.hostenv.println("ERROR in PortletWindow ["+this.widgetId+"] setContent while "+(step==1?"running onUnload":(step==2?"setting innerHTML":"creating dojo widgets"))+" - "+jetspeed.formatError(e));
}
return _1d6;
},_setContent:function(cont,_1dc){
this._destroyChildWidgets(_1dc);
try{
var node=this.containerNode;
while(node.firstChild){
_1dc.html.destroyNode(node.firstChild);
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
},_runStack:function(_1e0){
var st=this[_1e0];
var err="";
var _1e3=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_1e3);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_1e0]=[];
if(err.length){
var name=(_1e0=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_1e6,_1e7){
var self=this;
var _1e9=true;
var tmp="",code="";
for(var i=0;i<_1e6.length;i++){
if(_1e6[i].path){
_1e7.io.bind(this._cacheSetting({"url":_1e6[i].path,"load":function(type,_1ee){
dojo.lang.hitch(self,tmp=";"+_1ee);
},"error":function(type,_1f0){
_1f0.text=type+" downloading remote script";
self._handleDefaults.call(self,_1f0,"onExecError","debug");
},"mimetype":"text/plain","sync":true},_1e9));
code+=tmp;
}else{
code+=_1e6[i];
}
}
try{
if(this.scriptSeparation){
}else{
var djg=_1e7.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_1e7.doc();
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
},_cacheSetting:function(_1f4,_1f5){
var _1f6=dojo.lang;
for(var x in this.bindArgs){
if(_1f6.isUndefined(_1f4[x])){
_1f4[x]=this.bindArgs[x];
}
}
if(_1f6.isUndefined(_1f4.useCache)){
_1f4.useCache=_1f5;
}
if(_1f6.isUndefined(_1f4.preventCache)){
_1f4.preventCache=!_1f5;
}
if(_1f6.isUndefined(_1f4.mimetype)){
_1f4.mimetype="text/html";
}
return _1f4;
},_handleDefaults:function(e,_1f9,_1fa){
var _1fb=dojo;
if(!_1f9){
_1f9="onContentError";
}
if(_1fb.lang.isString(e)){
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
this[_1f9](e);
if(e.returnValue){
switch(_1fa){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_1fb.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_1fb.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_1fb);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_1fe){
if(_1fe){
this.title=_1fe;
}else{
this.title="";
}
if(this.windowInitialized&&this.tbTextNode){
this.tbTextNode.innerHTML=this.title;
}
},getPortletTitle:function(){
return this.title;
},_splitAndFixPaths_scriptsonly:function(s,url){
var _201=true;
var _202=[];
var _203=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _204=/src=(['"]?)([^"']*)\1/i;
while(match=_203.exec(s)){
if(_201&&match[1]){
if(attr=_204.exec(match[1])){
_202.push({path:attr[2]});
}
}
if(match[2]){
var sc=match[2];
if(!sc){
continue;
}
if(_201){
_202.push(sc);
}
}
s=s.substr(0,match.index)+s.substr(match.index+match[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_202,"url":url};
},_IEPostDrag:function(){
if(!this.posStatic){
return;
}
var _206=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_206,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.showAllPortletWindows=function(){
var _207=jetspeed;
var _208=_207.css;
var _209=_208.cssDis,_20a=_208.cssNoSelNm,_20b=_208.cssNoSel,_20c=_208.cssNoSelEnd;
var _20d=_207.page.getPWins(false);
var _20e,_20f;
for(var i=0;i<_20d.length;i++){
_20e=_20d[i];
if(_20e){
_20f=_20e.dNodeCss;
_20f[_20a]="";
_20f[_20b]="";
_20f[_20c]="";
_20f[_209]="block";
_20e.domNode.style.display="block";
_20e.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.hideAllPortletWindows=function(_211){
var _212=jetspeed;
var _213=_212.css;
var _214=_213.cssDis,_215=_213.cssNoSelNm,_216=_213.cssNoSel,_217=_213.cssNoSelEnd;
var _218=_212.page.getPWins(false);
var _219,_21a,_21b;
for(var i=0;i<_218.length;i++){
_21a=_218[i];
_219=true;
if(_21a&&_211&&_211.length>0){
for(var _21d=0;_21d<_211.length;_21d++){
if(_21a.widgetId==_211[_21d]){
_219=false;
break;
}
}
}
if(_21a){
_21b=_21a.dNodeCss;
_21b[_215]="";
_21b[_216]="";
_21b[_217]="";
if(_219){
_21b[_214]="none";
_21a.domNode.style.display="none";
}else{
_21b[_214]="block";
_21a.domNode.style.display="block";
}
_21a.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.WinScroller=function(){
var _21e=this.jsObj;
this.UAmoz=_21e.UAmoz;
this.UAope=_21e.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{jsObj:jetspeed,djObj:dojo,typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _222=null;
if(this.UAmoz){
_222=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_222=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_222=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_222=b.clientHeight;
}
}
}
}
if(_222!=null&&e.clientY>_222-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_228,_229){
return ((_229!=null?(_229+"; "):"")+this.typeNm+" "+(_228==null?"<unknown>":_228.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_22a,_22b){
var _22c=new jetspeed.widget.PortletWindowResizeHandle(_22a,_22b);
var doc=document;
var _22e=doc.createElement("div");
_22e.className=_22c.rhClass;
var _22f=doc.createElement("div");
_22e.appendChild(_22f);
_22a.rbNode.appendChild(_22e);
_22c.domNode=_22e;
_22c.build();
return _22c;
};
jetspeed.widget.PortletWindowResizeHandle=function(_230,_231){
this.pWin=_230;
_231.widget.WinScroller.call(this);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_232,_233,jsUI){
this._cleanUpLastEvt(_232,_233,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_232);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_235,_236,jsUI){
var _238=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_235);
this.tempEvents=null;
}
catch(ex){
_238=this._getErrMsg(ex,"event clean-up error",this.pWin,_238);
}
try{
_236.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_238=this._getErrMsg(ex,"clean-up error",this.pWin,_238);
}
if(_238!=null){
dojo.raise(_238);
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
var _23c=jetspeed;
var jsUI=_23c.ui;
var _23e=dojo;
var _23f=_23e.event;
var _240=_23c.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_23f,_23c,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_23e.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _243=[];
_243.push(jsUI.evtConnect("after",_240,"onmousemove",this,"_changeSizing",_23f,25));
_243.push(jsUI.evtConnect("after",_240,"onmouseup",this,"_endSizing",_23f));
_243.push(jsUI.evtConnect("after",d,"ondragstart",_23c,"_stopEvent",_23f));
_243.push(jsUI.evtConnect("after",d,"onselectstart",_23c,"_stopEvent",_23f));
_23c.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_243;
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
var _24a=pWin.posStatic;
if(_24a){
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
var _24d=jetspeed;
this._cleanUpLastEvt(dojo.event,_24d,_24d.ui);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
jetspeed.widget.BackgroundIframe=function(node,_24f,_250){
if(!_24f){
_24f=this.defaultStyleClass;
}
var html="<iframe src='' frameborder='0' scrolling='no' class='"+_24f+"'>";
this.iframe=_250.doc().createElement(html);
this.iframe.tabIndex=-1;
node.appendChild(this.iframe);
};
dojo.lang.extend(jetspeed.widget.BackgroundIframe,{defaultStyleClass:"ie6BackgroundIFrame",iframe:null});
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_252,_253,_254,_255,e,_257,_258,_259){
var jsUI=_259.ui;
var _25b=_258.event;
_259.widget.WinScroller.call(this);
if(_259.widget._movingInProgress){
if(djConfig.isDebug){
_259.debugAlert("ERROR - Mover initiation before previous Mover was destroyed");
}
}
_259.widget._movingInProgress=true;
this.moveInitiated=false;
this.moveableObj=_255;
this.windowOrLayoutWidget=_252;
this.node=_253;
this.nodeLayoutColumn=_254;
this.posStatic=_252.posStatic;
this.notifyOnAbsolute=_257;
if(e.ctrlKey&&_252.moveAllowTilingChg){
if(this.posStatic){
this.changeToUntiled=true;
}else{
if(_259.prefs.windowTiling){
this.changeToTiled=true;
this.changeToTiledStarted=false;
}
}
}
this.posRecord={};
this.disqualifiedColumnIndexes=(_254!=null)?_254.getDescendantCols():{};
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _25d=[];
var _25e=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_25b);
_25d.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_25b));
_25d.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_25b));
_25d.push(jsUI.evtConnect("after",doc,"ondragstart",_259,"_stopEvent",_25b));
_25d.push(jsUI.evtConnect("after",doc,"onselectstart",_259,"_stopEvent",_25b));
if(_259.UAie6){
_25d.push(jsUI.evtConnect("after",doc,"onmousedown",_259,"mouseUpDestroy",_25b));
}
_259.page.displayAllPWinIFrameCovers(false);
_25d.push(_25e);
this.events=_25d;
this.pSLastColChgIdx=null;
this.pSLastColChgTime=null;
this.pSLastNaturalColChgYTest=null;
this.pSLastNaturalColChgHistory=null;
this.pSLastNaturalColChgChoiceMap=null;
this.isDebug=false;
if(_259.debug.dragWindow){
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
this.devI=_259.debugindent;
this.devIH=_259.debugindentH;
this.devIT=_259.debugindentT;
this.devI3=_259.debugindent3;
this.devICH=_259.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",pSColChgTimeTh:3000,onMouseMove:function(e){
var _260=this.jsObj;
var _261=this.djObj;
var _262=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _264=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _267=this.isDebug;
var _268=false;
var _269=null,_26a=null,_26b,_26c,_26d,_26e,_26f;
if(_267){
_26b=this.devI;
_26c=this.devIH;
_26d=this.devI3;
_26e=this.devICH,_26f=this.devIT;
_269=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _270=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_270&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_269)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_268=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_268=true;
}
}
}
}
if(_262&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_264=true;
}
_260.ui.setMarginBox(this.node,x,y,null,null,this.nodeLayoutInfo,_260,_261);
var _271=this.posRecord;
_271.left=x;
_271.top=y;
var _272=false;
var _273=this.posStatic;
if(!_273){
if(!_264&&this.changeToTiled&&!this.changeToTiledStarted){
_272=true;
_273=true;
}
}
if(_273&&!_264){
var _274=this.columnInfoArray;
var _275=_260.page.columns;
var _276=this.heightHalf;
var _277=_275.length;
var _278=e.pageX;
var _279=y+_276;
var _27a=this.pSLastColChgIdx;
var _27b=this.pSLastNaturalColChgChoiceMap;
var _27c=null,_27d=[],_27e=null;
var _27f,_280,_281,_282,lowY,_284,_285,_286,_287;
for(var i=0;i<_277;i++){
_27f=_274[i];
if(_27f!=null){
if(_278>=_27f.left&&_278<=_27f.right){
if(_279>=(_27f.top-30)||(_27b!=null&&_27b[i]!=null)){
_280=Math.min(Math.abs(_279-(_27f.top)),Math.abs(e.pageY-(_27f.top)));
_281=Math.min(Math.abs(_279-(_27f.yhalf)),Math.abs(e.pageY-(_27f.yhalf)));
_282=Math.min(Math.abs(_279-_27f.bottom),Math.abs(e.pageY-_27f.bottom));
lowY=Math.min(_280,_281);
lowY=Math.min(lowY,_282);
_285=null;
_287=_27c;
while(_287!=null){
_286=_27d[_287];
if(lowY<_286.lowY){
break;
}else{
_285=_286;
_287=_286.nextIndex;
}
}
_27d.push({index:i,lowY:lowY,nextIndex:_287,lowYAlign:((!_267)?null:(lowY==_280?"^":(lowY==_281?"~":"_")))});
_284=(_27d.length-1);
if(_285!=null){
_285.nextIndex=_284;
}else{
_27c=_284;
}
if(i==_27a){
_27e=lowY;
}
}else{
if(_267){
if(_26a==null){
_26a=[];
}
var _289=(_27f.top-30)-_279;
_26a.push(_261.string.padRight(String(i),2,_26e)+" y! "+_261.string.padRight(String(_289),4,_26e));
}
}
}else{
if(_267&&_278>_27f.width){
if(_26a==null){
_26a=[];
}
var _289=_278-_27f.width;
_26a.push(_261.string.padRight(String(i),2,_26e)+" x! "+_261.string.padRight(String(_289),4,_26e));
}
}
}
}
var _28a=-1;
var _28b=-1,_28c=-1;
var _28d=null,_28e=null,_28f=null,_290=null,_291=null;
if(_27c!=null){
_286=_27d[_27c];
_28a=_286.index;
_28d=_286.lowY;
if(_286.nextIndex!=null){
_286=_27d[_286.nextIndex];
_28b=_286.index;
_28e=_286.lowY;
_290=_28e-_28d;
if(_286.nextIndex!=null){
_286=_27d[_286.nextIndex];
_28c=_286.index;
_28f=_286.lowY;
_291=_28f-_28d;
}
}
}
var _292=null;
var _293=(new Date().getTime());
var _294=this.pSLastNaturalColChgYTest;
if(_27e==null||(_294!=null&&Math.abs(_279-_294)>=Math.max((_276-Math.floor(_276*0.3)),Math.min(_276,21)))){
if(_28a>=0){
this.pSLastNaturalColChgYTest=_279;
this.pSLastNaturalColChgHistory=[_28a];
_27b={};
_27b[_28a]=true;
this.pSLastNaturalColChgChoiceMap=_27b;
}
}else{
if(_294==null){
this.pSLastNaturalColChgYTest=_279;
_28a=_27a;
this.pSLastNaturalColChgHistory=[_28a];
_27b={};
_27b[_28a]=true;
this.pSLastNaturalColChgChoiceMap=_27b;
}else{
var _295=null;
var _296=this.pSLastColChgTime+this.pSColChgTimeTh;
if(_296<_293){
var _297=this.pSLastNaturalColChgHistory;
var _298=(_297==null?0:_297.length);
var _299=null,_29a;
_287=_27c;
while(_287!=null){
_286=_27d[_287];
colI=_286.index;
if(_298==0){
_295=colI;
break;
}else{
_29a=false;
for(var i=(_298-1);i>=0;i--){
if(_297[i]==colI){
if(_299==null||_299>i){
_299=i;
_295=colI;
}
_29a=true;
break;
}
}
if(!_29a){
_295=colI;
break;
}
}
_287=_286.nextIndex;
}
if(_295!=null){
_28a=_295;
_27b[_28a]=true;
if(_298==0||_297[(_298-1)]!=_28a){
_297.push(_28a);
}
}
}else{
_28a=_27a;
}
if(_267&&_295!=null){
_261.hostenv.println(_26b+"ColChg YTest="+_294+" LeastRecentColI="+_295+" History=["+(this.pSLastNaturalColChgHistory?this.pSLastNaturalColChgHistory.join(", "):"")+"] Map={"+_260.printobj(this.pSLastNaturalColChgChoiceMap)+"} expire="+(_293-_296)+"}");
}
}
}
if(_267&&_292!=null){
if(this.devKeepLastMsg!=null){
_261.hostenv.println(this.devKeepLastMsg);
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
}
_261.hostenv.println(_292);
}
var col=(_28a>=0?_275[_28a]:null);
if(_267){
if(this.devLastColI!=_28a){
_268=true;
}
this.devLastColI=_28a;
}
var _29d=_260.widget.pwGhost;
if(_272){
if(col!=null){
_260.ui.setMarginBox(_29d,null,null,null,m.h,this.nodeLayoutInfo,_260,_261);
_29d.col=null;
this.changeToTiledStarted=true;
this.posStatic=true;
}
}
var _29e=null,_29f=false,_2a0=false;
if(_29d.col!=col&&col!=null){
this.pSLastColChgTime=_293;
this.pSLastColChgIdx=_28a;
var _2a1=_29d.col;
if(_2a1!=null){
_261.dom.removeNode(_29d);
}
_29d.col=col;
var _2a2=_274[_28a];
var _2a3=_2a2.childCount+1;
_2a2.childCount=_2a3;
if(_2a3==1){
_275[_28a].domNode.style.height="";
}
col.domNode.appendChild(_29d);
_2a0=true;
var _2a4=(_27a!=null?((_27a!=_28a)?_274[_27a]:null):(_2a1!=null?_274[_2a1.getPageColumnIndex()]:null));
if(_2a4!=null){
var _2a5=_2a4.childCount-1;
if(_2a5<0){
_2a5=0;
}
_2a4.childCount=_2a5;
if(_2a5==0){
_275[_2a4.pageColIndex].domNode.style.height="1px";
}
}
}
var _2a6=null,_2a7=null;
if(col!=null){
_2a6=_260.ui.getPWinAndColChildren(col.domNode,_29d,true,false,true,false);
_2a7=_2a6.matchingNodes;
}
if(_2a7!=null&&_2a7.length>1){
var _2a8=_2a6.matchNodeIndexInMatchingNodes;
var _2a9=-1;
var _2aa=-1;
if(_2a8>0){
var _2a9=_261.html.getAbsolutePosition(_2a7[_2a8-1],true).y;
if((y-25)<=_2a9){
_261.dom.removeNode(_29d);
_29e=_2a7[_2a8-1];
_261.dom.insertBefore(_29d,_29e,true);
}
}
if(_2a8!=(_2a7.length-1)){
var _2aa=_261.html.getAbsolutePosition(_2a7[_2a8+1],true).y;
if((y+10)>=_2aa){
if(_2a8+2<_2a7.length){
_29e=_2a7[_2a8+2];
_261.dom.insertBefore(_29d,_29e,true);
}else{
col.domNode.appendChild(_29d);
_29f=true;
}
}
}
}
if(_268){
var _2ab="";
if(_29e!=null||_29f||_2a0){
_2ab="put=";
if(_29e!=null){
_2ab+="before("+_29e.id+")";
}else{
if(_29f){
_2ab+="end";
}else{
if(_2a0){
_2ab+="end-default";
}
}
}
}
_261.hostenv.println(_26b+"col="+_28a+_26c+_2ab+_26c+"x="+x+_26c+"y="+y+_26c+"ePGx="+e.pageX+_26c+"ePGy="+e.pageY+_26c+"yTest="+_279);
var _2ac="",colI,_27f;
_287=_27c;
while(_287!=null){
_286=_27d[_287];
colI=_286.index;
_27f=_274[_286.index];
_2ac+=(_2ac.length>0?_26f:"")+colI+_286.lowYAlign+(colI<10?_26e:"")+" -> "+_261.string.padRight(String(_286.lowY),4,_26e);
_287=_286.nextIndex;
}
_261.hostenv.println(_26d+_2ac);
if(_26a!=null){
var _2ad="";
for(i=0;i<_26a.length;i++){
_2ad+=(i>0?_26f:"")+_26a[i];
}
_261.hostenv.println(_26d+_2ad);
}
this.devLastTime=_269;
this.devChgTh=this.devChgSubsqTh;
}
}
},onFirstMove:function(){
var _2ae=this.jsObj;
var jsUI=_2ae.ui;
var _2b0=this.djObj;
var _2b1=this.windowOrLayoutWidget;
var node=this.node;
var _2b3=_2b1._getLayoutInfoMoveable();
this.nodeLayoutInfo=_2b3;
var mP=_2b1._getWindowMarginBox(_2b3,_2ae);
this.staticWidth=null;
var _2b5=_2ae.widget.pwGhost;
var _2b6=this.UAmoz;
var _2b7=this.changeToUntiled;
var _2b8=this.changeToTiled;
var m=null;
if(this.posStatic){
if(!_2b7){
var _2ba=_2b1.getPageColumnIndex();
var _2bb=(_2ba>=0?_2ae.page.columns[_2ba]:null);
_2b5.col=_2bb;
this.pSLastColChgTime=new Date().getTime();
this.pSLastColChgIdx=_2ba;
}
m={w:mP.w,h:mP.h};
var _2bc=node.parentNode;
var _2bd=document.getElementById(_2ae.id.DESKTOP);
var _2be=node.style;
this.staticWidth=_2be.width;
var _2bf=_2b0.html.getAbsolutePosition(node,true);
var _2c0=_2b3.mE;
m.l=_2bf.left-_2c0.l;
m.t=_2bf.top-_2c0.t;
if(_2b6){
if(!_2b7){
jsUI.setMarginBox(_2b5,null,null,null,mP.h,_2b3,_2ae,_2b0);
}
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_2be.position="absolute";
if(!_2b7){
_2be.zIndex=_2ae.page.getPWinHighZIndex()+1;
}else{
_2be.zIndex=(_2b1._setAsTopZIndex(_2ae.page,_2ae.css,_2b1.dNodeCss,false));
}
if(!_2b7){
_2bc.insertBefore(_2b5,node);
if(!_2b6){
jsUI.setMarginBox(_2b5,null,null,null,mP.h,_2b3,_2ae,_2b0);
}
_2bd.appendChild(node);
var _2c1=jsUI.getPWinAndColChildren(_2bc,_2b5,true,false,true);
this.prevColumnNode=_2bc;
this.prevIndexInCol=_2c1.matchNodeIndexInMatchingNodes;
}else{
_2b1._updtDimsObj(false,true);
_2bd.appendChild(node);
}
}else{
m=mP;
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
jsUI.evtDisconnectWObj(this.events.pop(),_2b0.event);
var _2c2=this.disqualifiedColumnIndexes;
var _2c3=(this.isDebug||_2ae.debug.dragWindowStart),_2c4;
if(_2c3){
_2c4=_2ae.debugindentT;
var _2c5=_2ae.debugindentH;
var _2c6="";
if(_2c2!=null){
_2c6=_2c5+"dqCols=["+_2ae.objectKeys(_2c2).join(", ")+"]";
}
var _2c7=_2b1.title;
if(_2c7==null){
_2c7=node.id;
}
_2b0.hostenv.println("DRAG \""+_2c7+"\""+_2c5+((this.posStatic&&!_2b7)?("col="+(_2b5.col?_2b5.col.getPageColumnIndex():"null")+_2c5):"")+"m.l = "+m.l+_2c5+"m.t = "+m.t+_2c6);
}
if(this.posStatic||_2b8){
this.heightHalf=mP.h/2;
if(!_2b7){
var _2c8=_2ae.page.columns||[];
var _2c9=_2c8.length;
var _2ca=new Array(_2c9);
var _2cb=_2b0.byId(_2ae.id.COLUMNS);
if(_2cb){
var _2cc=_2ae.page.layoutInfo;
this._getChildColInfo(_2cb,_2ca,_2ae.page.columns,_2c2,_2cc,_2cc.columns,_2cc.desktop,node,(_2c3?1:null),_2c4,_2b0,_2ae);
if(_2c3){
_2b0.hostenv.println(_2c4+"--------------------");
}
}
this.columnInfoArray=_2ca;
}
}
if(this.posStatic){
jsUI.setMarginBox(node,m.l,m.t,mP.w,null,_2b3,_2ae,_2b0);
if(this.notifyOnAbsolute){
_2b1.dragChangeToAbsolute(this,node,this.marginBox,_2b0,_2ae);
}
if(_2b7){
this.posStatic=false;
}
}
},_getChildColInfo:function(_2cd,_2ce,_2cf,_2d0,_2d1,_2d2,_2d3,_2d4,_2d5,_2d6,_2d7,_2d8){
var _2d9=_2cd.childNodes;
var _2da=(_2d9?_2d9.length:0);
if(_2da==0){
return;
}
var _2db=_2d7.html.getAbsolutePosition(_2cd,true);
var _2dc=_2d8.ui.getMarginBox(_2cd,_2d2,_2d3,_2d8);
var _2dd=_2d1.column;
var _2de,col,_2e0,_2e1,_2e2,_2e3,_2e4,_2e5,_2e6,_2e7,_2e8,_2e9,_2ea;
var _2eb=null,_2ec=(_2d5!=null?(_2d5+1):null),_2ed,_2ee;
for(var i=0;i<_2da;i++){
_2de=_2d9[i];
_2e0=_2de.getAttribute("columnindex");
_2e1=(_2e0==null?-1:new Number(_2e0));
if(_2e1>=0){
_2e2=_2de.getAttribute("layoutid");
_2e3=(_2e2!=null&&_2e2.length>0);
_2ea=true;
_2ed=_2ec;
_2ee=null;
if(!_2e3&&(!(_2de===_2d4))){
col=_2cf[_2e1];
if(col&&!col.layoutActionsDisabled&&(_2d0==null||_2d0[_2e1]==null)){
_2e4=_2d8.ui.getMarginBox(_2de,_2dd,_2d2,_2d8);
if(_2eb==null){
_2eb=_2e4.t-_2dc.t;
_2e9=_2dc.h-_2eb;
}
_2e5=_2db.left+(_2e4.l-_2dc.l);
_2e6=_2db.top+_2eb;
_2e7=_2e4.h;
if(_2e7<_2e9){
_2e7=_2e9;
}
if(_2e7<40){
_2e7=40;
}
var _2f0=_2de.childNodes;
_2e8={left:_2e5,top:_2e6,right:(_2e5+_2e4.w),bottom:(_2e6+_2e7),childCount:(_2f0?_2f0.length:0),pageColIndex:_2e1};
_2e8.height=_2e8.bottom-_2e8.top;
_2e8.width=_2e8.right-_2e8.left;
_2e8.yhalf=_2e8.top+(_2e8.height/2);
_2ce[_2e1]=_2e8;
_2ea=(_2e8.childCount>0);
if(_2d5!=null){
_2ee=(_2d8.debugDims(_2e8,true)+" yhalf="+_2e8.yhalf+(_2e4.h!=_2e7?(" hreal="+_2e4.h):"")+" childC="+_2e8.childCount+"}");
}
}
}
if(_2d5!=null){
if(_2e3){
_2ed=_2ec+1;
}
if(_2ee==null){
_2ee="---";
}
_2d7.hostenv.println(_2d7.string.repeat(_2d6,_2d5)+"["+((_2e1<10?" ":"")+_2e0)+"] "+_2ee);
}
if(_2ea){
this._getChildColInfo(_2de,_2ce,_2cf,_2d0,_2d1,(_2e3?_2d1.columnLayoutHeader:_2dd),_2d2,_2d4,_2ed,_2d6,_2d7,_2d8);
}
}
}
},mouseUpDestroy:function(){
var _2f1=this.djObj;
var _2f2=this.jsObj;
this.destroy(_2f1,_2f1.event,_2f2,_2f2.ui);
},destroy:function(_2f3,_2f4,_2f5,jsUI){
var _2f7=this.windowOrLayoutWidget;
var node=this.node;
var _2f9=null;
if(this.moveInitiated&&_2f7&&node){
this.moveInitiated=false;
try{
if(this.posStatic){
var _2fa=_2f5.widget.pwGhost;
var _2fb=node.style;
if(_2fa&&_2fa.col){
_2f7.column=0;
_2f3.dom.insertBefore(node,_2fa,true);
}else{
if(this.prevColumnNode!=null&&this.prevIndexInCol!=null){
_2f3.dom.insertAtIndex(node,this.prevColumnNode,this.prevIndexInCol);
}else{
var _2fc=_2f5.page.getColumnDefault();
if(_2fc!=null){
_2f3.dom.prependChild(node,_2fc.domNode);
}
}
}
if(_2fa){
_2f3.dom.removeNode(_2fa);
}
}
_2f7.endDragging(this.posRecord,this.changeToUntiled,this.changeToTiled);
}
catch(ex){
_2f9=this._getErrMsg(ex,"destroy reset-window error",_2f7,_2f9);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_2f4);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_2f9=this._getErrMsg(ex,"destroy event clean-up error",_2f7,_2f9);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_2f5.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_2f9=this._getErrMsg(ex,"destroy clean-up error",_2f7,_2f9);
}
_2f5.widget._movingInProgress=false;
if(_2f9!=null){
_2f3.raise(_2f9);
}
}});
dojo.dnd.Moveable=function(_2fd,opt){
var _2ff=jetspeed;
var jsUI=_2ff.ui;
var _301=dojo;
var _302=_301.event;
this.windowOrLayoutWidget=_2fd;
this.handle=opt.handle;
var _303=[];
_303.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_302));
_303.push(jsUI.evtConnect("after",this.handle,"ondragstart",_2ff,"_stopEvent",_302));
_303.push(jsUI.evtConnect("after",this.handle,"onselectstart",_2ff,"_stopEvent",_302));
this.events=_303;
};
dojo.extend(dojo.dnd.Moveable,{minMove:5,enabled:true,mover:null,onMouseDown:function(e){
if(e&&e.button==2){
return;
}
var _305=dojo;
var _306=_305.event;
var _307=jetspeed;
var jsUI=jetspeed.ui;
if(this.mover!=null||this.tempEvents!=null){
this._cleanUpLastEvt(_305,_306,_307,jsUI);
_307.stopEvent(e);
}else{
if(this.enabled){
if(this.tempEvents!=null){
if(djConfig.isDebug){
_307.debugAlert("ERROR: Moveable onmousedown tempEvent already defined");
}
}else{
var _309=[];
var doc=this.handle.ownerDocument;
_309.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_306));
this.tempEvents=_309;
}
if(!this.windowOrLayoutWidget.posStatic){
this.windowOrLayoutWidget.bringToTop(e,false,true,_307);
}
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
_307.stopEvent(e);
},onMouseMove:function(e,_30c){
var _30d=jetspeed;
var _30e=dojo;
var _30f=_30e.event;
if(_30c||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
this._cleanUpLastEvt(_30e,_30f,_30d,_30d.ui);
var _310=this.windowOrLayoutWidget;
var _311=null;
this.beforeDragColRowInfo=null;
if(!_310.isLayoutPane){
var _312=_310.domNode;
if(_312!=null){
this.node=_312;
this.mover=new _30e.dnd.Mover(_310,_312,_311,this,e,false,_30e,_30d);
}
}else{
_310.startDragging(e,this,_30e,_30d);
}
}
_30d.stopEvent(e);
},onMouseUp:function(e){
var _314=dojo;
var _315=jetspeed;
this._cleanUpLastEvt(_314,_314.event,_315,_315.ui);
},_cleanUpLastEvt:function(_316,_317,_318,jsUI){
if(this._mDownEvt!=null){
_318.stopEvent(this._mDownEvt);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_316,_317,_318,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_317);
this.tempEvents=null;
},destroy:function(_31a,_31b,_31c,jsUI){
this._cleanUpLastEvt(_31a,_31b,_31c,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_31b);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_31f,_320){
var s=_31f||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_320);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_320.UAmoz){
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
if(_320.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_32b,_32c){
var s=_32b||dojo.gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_32c.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_333,_334,_335,_336,_337,_338){
var s=_337||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_338);
if(_335!=null&&_335>=0){
_335=Math.max(_335-pb.w-mb.w,0);
}
if(_336!=null&&_336>=0){
_336=Math.max(_336-pb.h-mb.h,0);
}
dojo._setBox(node,_333,_334,_335,_336);
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
dojo._getPadExtents=function(n,_346){
var s=_346||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_34c){
var s=_34c||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_351,_352){
var s=_351||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_352.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_35a){
var ne="none",px=dojo._toPixelValue,s=_35a||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_363,_364){
return (parseFloat(_364)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_366,_367){
if(!_367){
return 0;
}
if(_367.slice&&(_367.slice(-2)=="px")){
return parseFloat(_367);
}
with(_366){
var _368=style.left;
var _369=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_367;
_367=style.pixelLeft;
}
catch(e){
_367=0;
}
style.left=_368;
runtimeStyle.left=_369;
}
return _367;
};
}
dojo.gcs=dojo.getComputedStyle;

