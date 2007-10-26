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
var _125=this.domNode.parentNode;
var _126=document.getElementById(jetspeed.id.DESKTOP);
_126.appendChild(this.domNode);
_124.page.columnEmptyCheck(_125);
if(this.windowState==_124.id.ACT_MINIMIZE){
this.minimizeWindow();
}
if(this.portlet){
this.portlet.submitWinState();
}
this._addUntiledEvents();
},makeTiled:function(){
this.posStatic=true;
var _127=jetspeed;
this._setAsTopZIndex(_127.page,_127.css,this.dNodeCss,true);
this._alterCss(true,true);
this._tileWindow(_127);
_127.page.columnEmptyCheck(this.domNode.parentNode);
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
},makeHeightToFit:function(_128){
var _129=dojo.html.getMarginBox(this.domNode);
this.heightToFit=true;
if(this.ie6){
var _12a=this.iNodeCss;
this.iNodeCss=null;
this._alterCss(false,true);
this._updtDimsObj(false,false,true,false,true);
this.iNodeCss=_12a;
}
this._alterCss(false,true);
if(!_128&&this.portlet){
this.portlet.submitWinState();
}
},makeHeightVariable:function(_12b,_12c){
var _12d=this.getDimsObj(this.posStatic);
var _12e=this._getLayoutInfo().dNode;
var _12f=jetspeed.ui.getMarginBoxSize(this.domNode,_12e);
_12d.w=_12f.w;
_12d.h=_12f.h;
this.heightToFit=false;
this._alterCss(false,true);
if(!_12c&&this.iframesInfo){
dojo.lang.setTimeout(this,this._forceRefreshZIndex,70);
}
if(!_12b&&this.portlet){
this.portlet.submitWinState();
}
},editPageInitiate:function(_130,_131,_132){
this.editPageEnabled=true;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _134=_131.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_134]="block";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=true;
if(this.rbNodeCss&&this.windowState!=_130.id.ACT_MINIMIZE){
this.rbNodeCss[_134]="block";
}
}
this._setTitleBarDragging(true,_131);
if(!_132){
this._alterCss(true,true);
}
}
},editPageTerminate:function(_135,_136){
this.editPageEnabled=false;
var wDC=this.decConfig;
if(!wDC.windowTitlebar||!wDC.windowResizebar){
var _138=_135.cssDis;
if(!wDC.windowTitlebar){
this.titlebarEnabled=false;
if(this.tbNodeCss){
this.tbNodeCss[_138]="none";
}
}
if(!wDC.windowResizebar){
this.resizebarEnabled=false;
if(this.rbNodeCss){
this.rbNodeCss[_138]="none";
}
}
this._setTitleBarDragging(true,_135);
if(!_136){
this._alterCss(true,true);
}
}
},changeDecorator:function(_139){
var _13a=jetspeed;
var _13b=_13a.css;
var jsId=_13a.id;
var jsUI=_13a.ui;
var _13e=_13a.prefs;
var _13f=dojo;
var _140=this.decConfig;
if(_140&&_140.name==_139){
return;
}
var wDC=_13a.loadPortletDecorationStyles(_139,_13e);
if(!wDC){
return;
}
var _142=this.portlet;
if(_142){
_142._submitAjaxApi("updatepage","&method=update-portlet-decorator&portlet-decorator="+_139);
}
this.decConfig=wDC;
this.decName=wDC.name;
var _143=this.domNode;
var _144=this.containerNode;
var _145=this.iframesInfo;
var _146=(_145&&_145.layout);
var _147=(!_146?wDC.layout:wDC.layoutIFrame);
if(!_147){
if(!_146){
this._createLayoutInfo(wDC,false,_143,_144,this.tbNode,this.rbNode,_13f,_13a,jsUI);
}else{
this._createLayoutInfo(wDC,true,_143,_144,this.tbNode,this.rbNode,_13f,_13a,jsUI);
}
}
this._setupTitlebar(wDC,_140,this.portlet,_13a.docBody,document,_13a,_13a.id,_13e,jsUI,_13a.page,_13f);
_143.className=wDC.dNodeClass;
if(_146){
_144.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
}else{
_144.className=wDC.cNodeClass;
}
var _148=_13b.cssDis;
this.titlebarEnabled=true;
if(this.tbNodeCss){
this.tbNodeCss[_148]="block";
}
this.resizebarEnabled=true;
if(this.rbNodeCss&&this.windowState!=jsId.ACT_MINIMIZE){
this.rbNodeCss[_148]="block";
}
if(this.editPageEnabled){
this.editPageInitiate(_13a,_13b,true);
}else{
this.editPageTerminate(_13b,true);
}
this._setTitleBarDragging(true,_13b);
this._alterCss(true,true);
},resizeTo:function(w,h,_14b){
var _14c=this.getDimsObj(this.posStatic);
_14c.w=w;
_14c.h=h;
this._alterCss(false,false,true);
if(!this.windowIsSizing){
var _14d=this.resizeHandle;
if(_14d!=null&&_14d._isSizing){
jetspeed.ui.evtConnect("after",_14d,"_endSizing",this,"endSizing");
this.windowIsSizing=true;
}
}
this.resizeNotifyChildWidgets();
},resizeNotifyChildWidgets:function(){
if(this.childWidgets){
var _14e=this.childWidgets;
var _14f=_14e.length,_150;
for(var i=0;i<_14f;i++){
try{
_150=_14e[i];
if(_150){
_150.checkSize();
}
}
catch(e){
}
}
}
},_getLayoutInfo:function(){
var _152=this.iframesInfo;
return ((!(_152&&_152.layout))?this.decConfig.layout:this.decConfig.layoutIFrame);
},_getLayoutInfoMoveable:function(){
return this._getLayoutInfo().dNode;
},onBrowserWindowResize:function(){
if(this.ie6){
this._resetIE6TiledSize(false);
}
},_resetIE6TiledSize:function(_153){
var _154=this.posStatic;
if(_154){
var _155=this.domNode;
var _156=this.getDimsObj(_154);
_156.w=Math.max(0,this.domNode.parentNode.offsetWidth-this.colWidth_pbE);
this._alterCss(_153,false,false,false,true);
}
},_alterCss:function(_157,_158,_159,_15a,_15b,_15c){
var _15d=jetspeed;
var _15e=_15d.css;
var _15f=this.iframesInfo;
var _160=(_15f&&_15f.layout);
var _161=(!_160?this.decConfig.layout:this.decConfig.layoutIFrame);
var _162=this.dNodeCss,_163=null,_164=null,_165=null,_166=false,_167=this.iNodeCss,_168=null;
if(_167&&_160){
_168=_15f.iframeCoverIE6Css;
}
var _169=this.posStatic;
var _16a=(_169&&_167==null);
var _16b=this.heightToFit;
var _16c=(_157||_15b||(_159&&!_16a));
var _16d=(_158||_159);
var _16e=(_157||_15a);
var _16f=(_158||(_159&&_160));
var _170=this.getDimsObj(_169);
if(_157){
_162[_15e.cssPos]=(_169?"relative":"absolute");
}
var _171=null,_172=null;
if(_158){
if(_160){
var _173=this.getIFramesAndObjects(false,true);
if(_173&&_173.iframes&&_173.iframes.length==1&&_15f.iframesSize&&_15f.iframesSize.length==1){
var _174=_15f.iframesSize[0].h;
if(_174!=null){
_171=_173.iframes[0];
_172=(_16b?_174:(!_15d.UAie?"100%":"99%"));
_15c=false;
}
}
}
}
if(_16f){
_163=this.cNodeCss;
var _175=_15e.cssOx,_176=_15e.cssOy;
if(_16b&&!_160){
_162[_176]="visible";
_163[_176]="visible";
}else{
_162[_176]="hidden";
_163[_176]=(!_160?"auto":"hidden");
}
}
if(_16e){
var lIdx=_15e.cssL,_178=_15e.cssLU;
var tIdx=_15e.cssT,_17a=_15e.cssTU;
if(_169){
_162[lIdx]="auto";
_162[_178]="";
_162[tIdx]="auto";
_162[_17a]="";
}else{
_162[lIdx]=_170.l;
_162[_178]="px";
_162[tIdx]=_170.t;
_162[_17a]="px";
}
}
if(_16d){
_163=this.cNodeCss;
var hIdx=_15e.cssH,_17c=_15e.cssHU;
if(_16b&&_167==null){
_162[hIdx]="";
_162[_17c]="";
_163[hIdx]="";
_163[_17c]="";
}else{
var h=_170.h;
var _17e=_15d.css.cssDis;
var _17f;
var _180;
if(_163[_17e]=="none"){
_17f=_161.tbNode.mBh;
_180="";
_163[_17c]="";
}else{
_17f=(h-_161.dNode.lessH);
_180=_17f-_161.cNode.lessH-_161.cNode_mBh_LessBars;
_163[_17c]="px";
}
_162[hIdx]=_17f;
_162[_17c]="px";
_163[hIdx]=_180;
if(_167){
_167[hIdx]=_17f;
_167[_17c]="px";
_166=true;
if(_168){
_168[hIdx]=_180;
_168[_17c]=_163[_17c];
}
}
}
}
if(_16c){
var w=_170.w;
_163=this.cNodeCss;
_164=this.tbNodeCss;
_165=this.rbNodeCss;
var wIdx=_15e.cssW,_183=_15e.cssWU;
if(_16a&&(!this.ie6||!w)){
_162[wIdx]="";
_162[_183]="";
_163[wIdx]="";
_163[_183]="";
if(_164){
_164[wIdx]="";
_164[_183]="";
}
if(_165){
_165[wIdx]="";
_165[_183]="";
}
}else{
var _184=(w-_161.dNode.lessW);
_162[wIdx]=_184;
_162[_183]="px";
_163[wIdx]=_184-_161.cNode.lessW;
_163[_183]="px";
if(_164){
_164[wIdx]=_184-_161.tbNode.lessW;
_164[_183]="px";
}
if(_165){
_165[wIdx]=_184-_161.rbNode.lessW;
_165[_183]="px";
}
if(_167){
_167[wIdx]=_184;
_167[_183]="px";
_166=true;
if(_168){
_168[wIdx]=_163[wIdx];
_168[_183]=_163[_183];
}
}
}
}
if(!_15c){
this.domNode.style.cssText=_162.join("");
if(_163){
this.containerNode.style.cssText=_163.join("");
}
if(_164){
this.tbNode.style.cssText=_164.join("");
}
if(_165){
this.rbNode.style.cssText=_165.join("");
}
if(_166){
this.bgIframe.iframe.style.cssText=_167.join("");
if(_168){
_15f.iframeCover.style.cssText=_168.join("");
}
}
}
if(_171&&_172){
this._deferSetIFrameH(_171,_172,false,50);
}
},_deferSetIFrameH:function(_185,_186,_187,_188,_189){
if(!_188){
_188=100;
}
var pWin=this;
window.setTimeout(function(){
_185.height=_186;
if(_187){
if(_189==null){
_189=50;
}
if(_189==0){
pWin._forceRefreshZIndexAndForget();
}else{
dojo.lang.setTimeout(pWin,pWin._forceRefreshZIndexAndForget,_189);
}
}
},_188);
},_getWindowMarginBox:function(_18b,_18c){
var _18d=this.domNode;
if(_18b==null){
_18b=this._getLayoutInfo().dNode;
}
var _18e=null;
if(_18c.UAope){
_18e=(this.posStatic?_18c.page.layoutInfo.column:_18c.page.layoutInfo.desktop);
}
return _18c.ui.getMarginBox(_18d,_18b,_18e,_18c);
},_forceRefreshZIndex:function(){
var _18f=jetspeed;
var zTop=this._setAsTopZIndex(_18f.page,_18f.css,this.dNodeCss,this.posStatic);
this.domNode.style.zIndex=zTop;
},_forceRefreshZIndexAndForget:function(){
var zTop=jetspeed.page.getPWinTopZIndex(this.posStatic);
this.domNode.style.zIndex=String(zTop);
},getIFramesAndObjects:function(_192,_193){
var _194=this.containerNode;
var _195={};
var _196=false;
if(!_193){
var _197=_194.getElementsByTagName("object");
if(_197&&_197.length>0){
_195.objects=_197;
_196=true;
}
}
var _198=_194.getElementsByTagName("iframe");
if(_198&&_198.length>0){
_195.iframes=_198;
if(!_192){
return _195;
}
_196=true;
var _199=[];
for(var i=0;i<_198.length;i++){
var ifrm=_198[i];
var w=new Number(String(ifrm.width));
w=(isNaN(w)?null:String(ifrm.width));
var h=new Number(String(ifrm.height));
h=(isNaN(h)?null:String(ifrm.height));
_199.push({w:w,h:h});
}
_195.iframesSize=_199;
}
if(!_196){
return null;
}
return _195;
},contentChanged:function(evt){
if(this.inContentChgd==false){
this.inContentChgd=true;
if(this.heightToFit){
this.makeHeightToFit(true);
}
this.inContentChgd=false;
}
},closeWindow:function(){
var _19f=jetspeed;
var jsUI=_19f.ui;
var _1a1=_19f.page;
var _1a2=dojo;
var _1a3=_1a2.event;
var wDC=this.decConfig;
if(this.iframesInfo){
_1a1.unregPWinIFrameCover(this);
}
this._setupTitlebar(null,wDC,this.portlet,_19f.docBody,document,_19f,_19f.id,_19f.prefs,jsUI,_1a1,_1a2);
if(this.drag){
this.drag.destroy(_1a2,_1a3,_19f,jsUI);
this.drag=null;
}
if(this.resizeHandle){
this.resizeHandle.destroy(_1a3,_19f,jsUI);
this.resizeHandle=null;
}
this._destroyChildWidgets(_1a2);
this._removeUntiledEvents();
var _1a5=this.domNode;
if(_1a5&&_1a5.parentNode){
_1a5.parentNode.removeChild(_1a5);
}
this.domNode=null;
this.containerNode=null;
this.tbNode=null;
this.rbNode=null;
},_destroyChildWidgets:function(_1a6){
if(this.childWidgets){
var _1a7=this.childWidgets;
var _1a8=_1a7.length,_1a9,swT,swI;
_1a6.debug("PortletWindow ["+this.widgetId+"] destroy child widgets ("+_1a8+")");
for(var i=(_1a8-1);i>=0;i--){
try{
_1a9=_1a7[i];
if(_1a9){
swT=_1a9.widgetType;
swI=_1a9.widgetId;
_1a9.destroy();
_1a6.debug("destroyed child widget["+i+"]: "+swT+" "+swI);
}
_1a7[i]=null;
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
},endDragging:function(_1ae,_1af,_1b0){
var _1b1=jetspeed;
var ie6=this.ie6;
if(_1af){
this.posStatic=false;
}else{
if(_1b0){
this.posStatic=true;
}
}
var _1b3=this.posStatic;
if(!_1b3){
var _1b4=this.getDimsObj(_1b3);
if(_1ae&&_1ae.left!=null&&_1ae.top!=null){
_1b4.l=_1ae.left;
_1b4.t=_1ae.top;
if(!_1af){
this._alterCss(false,false,false,true,false,true);
}
}
if(_1af){
this._updtDimsObj(false,false,true);
this._alterCss(true,true,false,true);
this._addUntiledEvents();
}
}else{
if(_1b0){
this._setAsTopZIndex(_1b1.page,_1b1.css,this.dNodeCss,_1b3);
this._updtDimsObj(false,false);
}
if(!ie6){
this._alterCss(true);
this.resizeNotifyChildWidgets();
}else{
this._resetIE6TiledSize(_1b0);
}
}
if(this.portlet&&this.windowState!=_1b1.id.ACT_MAXIMIZE){
this.portlet.submitWinState();
}
if(ie6){
dojo.lang.setTimeout(this,this._IEPostDrag,_1b1.widget.ie6PostDragAddDelay);
}
},getCurWinState:function(_1b5){
var _1b6=this.domNode;
var _1b7=this.posStatic;
if(!_1b6){
return null;
}
var _1b8=_1b6.style;
var _1b9={};
if(!_1b7){
_1b9.zIndex=_1b8.zIndex;
}
if(_1b5){
return _1b9;
}
var _1ba=this.getDimsObj(_1b7);
_1b9.width=(_1ba.w?String(_1ba.w):"");
_1b9.height=(_1ba.h?String(_1ba.h):"");
_1b9[jetspeed.id.PP_WINDOW_POSITION_STATIC]=_1b7;
_1b9[jetspeed.id.PP_WINDOW_HEIGHT_TO_FIT]=this.heightToFit;
if(!_1b7){
_1b9.left=(_1ba.l!=null?String(_1ba.l):"");
_1b9.top=(_1ba.t!=null?String(_1ba.t):"");
}else{
var _1bb=jetspeed.page.getPortletCurColRow(_1b6);
if(_1bb!=null){
_1b9.column=_1bb.column;
_1b9.row=_1bb.row;
_1b9.layout=_1bb.layout;
}else{
throw new Error("Can't find row/col/layout for window: "+this.widgetId);
}
}
return _1b9;
},getCurWinStateForPersist:function(_1bc){
var _1bd=this.getCurWinState(_1bc);
this._mkNumProp(null,_1bd,"left");
this._mkNumProp(null,_1bd,"top");
this._mkNumProp(null,_1bd,"width");
this._mkNumProp(null,_1bd,"height");
return _1bd;
},_mkNumProp:function(_1be,_1bf,_1c0){
var _1c1=(_1bf!=null&&_1c0!=null);
if(_1be==null&&_1c1){
_1be=_1bf[_1c0];
}
if(_1be==null||_1be.length==0){
_1be=0;
}else{
var _1c2="";
for(var i=0;i<_1be.length;i++){
var _1c4=_1be.charAt(i);
if((_1c4>="0"&&_1c4<="9")||_1c4=="."){
_1c2+=_1c4.toString();
}
}
if(_1c2==null||_1c2.length==0){
_1c2="0";
}
if(_1c1){
_1bf[_1c0]=_1c2;
}
_1be=new Number(_1c2);
}
return _1be;
},setPortletContent:function(html,url){
var _1c7=jetspeed;
var _1c8=dojo;
var ie6=this.ie6;
var _1ca=null;
var _1cb=this.containerNode;
if(ie6){
_1ca=this.iNodeCss;
if(this.heightToFit){
this.iNodeCss=null;
this._alterCss(false,true);
}
}
var _1cc=html.toString();
if(!this.exclPContent){
_1cc="<div class=\"PContent\" >"+_1cc+"</div>";
}
var _1cd=this._splitAndFixPaths_scriptsonly(_1cc,url,_1c7);
var doc=_1cb.ownerDocument;
var _1cf=this.setContent(_1cd,doc,_1c8);
this.childWidgets=((_1cf&&_1cf.length>0)?_1cf:null);
if(_1cd.scripts!=null&&_1cd.scripts.length!=null&&_1cd.scripts.length>0){
this._executeScripts(_1cd.scripts,_1c8);
this.onLoad();
}
if(_1c7.debug.setPortletContent){
_1c8.debug("setPortletContent ["+(this.portlet?this.portlet.entityId:this.widgetId)+"]");
}
if(this.portlet){
this.portlet.postParseAnnotateHtml(_1cb);
}
var _1d0=this.iframesInfo;
var _1d1=this.getIFramesAndObjects(true,false);
var _1d2=null,_1d3=false;
if(_1d1!=null){
if(_1d0==null){
this.iframesInfo=_1d0={layout:false};
var _1d4=doc.createElement("div");
var _1d5="portletWindowIFrameCover";
_1d4.className=_1d5;
_1cb.appendChild(_1d4);
if(_1c7.UAie){
_1d4.className=(_1d5+"IE")+" "+_1d5;
if(ie6){
_1d0.iframeCoverIE6Css=_1c7.css.cssWidthHeight.concat();
}
}
_1d0.iframeCover=_1d4;
_1c7.page.regPWinIFrameCover(this);
}
var _1d6=_1d0.iframesSize=_1d1.iframesSize;
var _1d7=_1d1.iframes;
var _1d8=_1d0.layout;
var _1d9=_1d0.layout=(_1d7&&_1d7.length==1&&_1d6[0].h!=null);
if(_1d8!=_1d9){
_1d3=true;
}
if(_1d9){
if(!this.heightToFit){
_1d2=_1d7[0];
}
var wDC=this.decConfig;
var _1cb=this.containerNode;
_1cb.firstChild.className="PContent portletIFramePContent";
_1cb.className=wDC.cNodeClass+" "+this.iframeCoverContainerClass;
if(!wDC.layoutIFrame){
this._createLayoutInfo(wDC,true,this.domNode,_1cb,this.tbNode,this.rbNode,_1c8,_1c7,_1c7.ui);
}
}
var _1db=null;
var _1dc=_1d1.objects;
if(_1dc){
var _1dd=_1c7.page.swfInfo;
if(_1dd){
for(var i=0;i<_1dc.length;i++){
var _1df=_1dc[i];
var _1e0=_1df.id;
if(_1e0){
var swfI=_1dd[_1e0];
if(swfI){
if(_1db==null){
_1db={};
}
_1db[_1e0]=swfI;
}
}
}
}
}
if(_1db){
_1d0.swfInfo=_1db;
}else{
delete _1d0.swfInfo;
}
}else{
if(_1d0!=null){
if(_1d0.layout){
this.containerNode.className=this.decConfig.cNodeClass;
_1d3=true;
}
this.iframesInfo=null;
_1c7.page.unregPWinIFrameCover(this);
}
}
if(_1d3){
this._alterCss(false,false,true);
}
if(this.restoreOnNextRender){
this.restoreOnNextRender=false;
this.restoreWindow();
}
if(ie6){
this._updtDimsObj(false,false,true,false,true);
if(_1ca==null){
var _1e2=_1c7.css;
_1ca=_1e2.cssHeight.concat();
_1ca[_1e2.cssDis]="inline";
}
this.iNodeCss=_1ca;
this._alterCss(false,false,true);
}
if(this.minimizeOnNextRender){
this.minimizeOnNextRender=false;
this.minimizeWindow(true);
this.actionBtnSync(_1c7,_1c7.id);
this.needsRenderOnRestore=true;
}
if(_1d2){
this._deferSetIFrameH(_1d2,(!_1c7.UAie?"100%":"99%"),true);
}
},_setContentObjects:function(){
delete this._objectsInfo;
},setContent:function(data,doc,_1e5){
var _1e6=null;
var step=1;
try{
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=true;
step=2;
this._setContent(data.xml,_1e5);
step=3;
if(this.parseContent){
var node=this.containerNode;
var _1e9=new _1e5.xml.Parse();
var frag=_1e9.parseElement(node,null,true);
_1e6=_1e5.widget.getParser().createSubComponents(frag,null);
}
}
catch(e){
dojo.hostenv.println("ERROR in PortletWindow ["+this.widgetId+"] setContent while "+(step==1?"running onUnload":(step==2?"setting innerHTML":"creating dojo widgets"))+" - "+jetspeed.formatError(e));
}
return _1e6;
},_setContent:function(cont,_1ec){
this._destroyChildWidgets(_1ec);
try{
var node=this.containerNode;
while(node.firstChild){
_1ec.html.destroyNode(node.firstChild);
}
node.innerHTML=cont;
}
catch(e){
e.text="Couldn't load content:"+e.description;
this._handleDefaults(e,"onContentError");
}
},_splitAndFixPaths_scriptsonly:function(s,url,_1f0){
var _1f1=true;
var _1f2,attr;
var _1f4=[];
var _1f5=/<script([^>]*)>([\s\S]*?)<\/script>/i;
var _1f6=/src=(['"]?)([^"']*)\1/i;
while(_1f2=_1f5.exec(s)){
if(_1f1&&_1f2[1]){
if(attr=_1f6.exec(_1f2[1])){
_1f4.push({path:attr[2]});
}
}
if(_1f2[2]){
var sc=_1f2[2];
if(!sc){
continue;
}
if(_1f1){
_1f4.push(sc);
}
}
s=s.substr(0,_1f2.index)+s.substr(_1f2.index+_1f2[0].length);
}
return {"xml":s,"styles":[],"titles":[],"requires":[],"scripts":_1f4,"url":url};
},onLoad:function(e){
this._runStack("_onLoadStack");
this.isLoaded=true;
},onUnload:function(e){
this._runStack("_onUnloadStack");
delete this.scriptScope;
},_runStack:function(_1fa){
var st=this[_1fa];
var err="";
var _1fd=this.scriptScope||window;
for(var i=0;i<st.length;i++){
try{
st[i].call(_1fd);
}
catch(e){
err+="\n"+st[i]+" failed: "+e.description;
}
}
this[_1fa]=[];
if(err.length){
var name=(_1fa=="_onLoadStack")?"addOnLoad":"addOnUnLoad";
this._handleDefaults(name+" failure\n "+err,"onExecError","debug");
}
},_executeScripts:function(_200,_201){
var _202=jetspeed;
var _203=_201.hostenv;
var _204=_202.page;
var _205=document.getElementsByTagName("head")[0];
var tmp,uri,code="";
for(var i=0;i<_200.length;i++){
if(!_200[i].path){
tmp=this._fixScripts(_200[i],true);
if(tmp){
code+=((code.length>0)?";":"")+tmp;
}
continue;
}
var uri=_200[i].path;
var _20a=null;
try{
_20a=_203.getText(uri,null,false);
if(_20a){
_20a=this._fixScripts(_20a,false);
code+=((code.length>0)?";":"")+_20a;
}
}
catch(ex){
_201.debug("Error loading script for portlet ["+this.widgetId+"] url="+uri+" - "+_202.formatError(ex));
}
try{
if(_20a&&!_202.containsElement("script","src",uri,_205)){
_202.addDummyScriptToHead(uri);
}
}
catch(ex){
_201.debug("Error added fake script element to head for portlet ["+this.widgetId+"] url="+uri+" - "+_202.formatError(ex));
}
}
try{
var djg=_201.global();
if(djg.execScript){
djg.execScript(code);
}else{
var djd=_201.doc();
var sc=djd.createElement("script");
sc.appendChild(djd.createTextNode(code));
(this.containerNode||this.domNode).appendChild(sc);
}
}
catch(e){
var _20e="Error running scripts for portlet ["+this.widgetId+"] - "+_202.formatError(e);
e.text=_20e;
_201.hostenv.println(_20e);
_201.hostenv.println(code);
}
},_fixScripts:function(_20f,_210){
var _211=/\b([a-z_A-Z$]\w*)\s*\.\s*(addEventListener|attachEvent)\s*\(/;
var _212,_213,_214;
while(_212=_211.exec(_20f)){
_213=_212[1];
_214=_212[2];
_20f=_20f.substr(0,_212.index)+"jetspeed.postload_"+_214+"("+_213+","+_20f.substr(_212.index+_212[0].length);
}
var _215=/\b(document\s*.\s*write(ln)?)\s*\(/;
while(_212=_215.exec(_20f)){
_20f=_20f.substr(0,_212.index)+"jetspeed.postload_docwrite("+_20f.substr(_212.index+_212[0].length);
}
var _216=/(;\s|\s+)([a-z_A-Z$][\w.]*)\s*\.\s*(URL\s*|(location\s*(\.\s*href\s*){0,1}))=\s*(("[^"]*"|'[^']*'|[^;])[^;]*)/;
while(_212=_216.exec(_20f)){
var _217=_212[3];
_217=_217.replace(/^\s+|\s+$/g,"");
_20f=_20f.substr(0,_212.index)+_212[1]+"jetspeed.setdoclocation("+_212[2]+", \""+_217+"\", ("+_212[6]+"))"+_20f.substr(_212.index+_212[0].length);
}
if(_210){
_20f=_20f.replace(/<!--|-->/g,"");
}
return _20f;
},_cacheSetting:function(_218,_219){
var _21a=dojo.lang;
for(var x in this.bindArgs){
if(_21a.isUndefined(_218[x])){
_218[x]=this.bindArgs[x];
}
}
if(_21a.isUndefined(_218.useCache)){
_218.useCache=_219;
}
if(_21a.isUndefined(_218.preventCache)){
_218.preventCache=!_219;
}
if(_21a.isUndefined(_218.mimetype)){
_218.mimetype="text/html";
}
return _218;
},_handleDefaults:function(e,_21d,_21e){
var _21f=dojo;
if(!_21d){
_21d="onContentError";
}
if(_21f.lang.isString(e)){
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
this[_21d](e);
if(e.returnValue){
switch(_21e){
case true:
case "alert":
alert(e.toString());
break;
case "debug":
_21f.debug(e.toString());
break;
default:
if(this._callOnUnload){
this.onUnload();
}
this._callOnUnload=false;
if(arguments.callee._loopStop){
_21f.debug(e.toString());
}else{
arguments.callee._loopStop=true;
this._setContent(e.toString(),_21f);
}
}
}
arguments.callee._loopStop=false;
},onExecError:function(e){
},onContentError:function(e){
},setPortletTitle:function(_222){
if(_222){
this.title=_222;
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
var _223=this.domNode.parentNode;
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,_223,0);
dojo.lang.setTimeout(this,this._IERemoveHelper,jetspeed.widget.ie6PostDragRmDelay);
},_IERemoveHelper:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
}});
jetspeed.widget.showAllPortletWindows=function(){
var _224=jetspeed;
var _225=_224.css;
var _226=_225.cssDis,_227=_225.cssNoSelNm,_228=_225.cssNoSel,_229=_225.cssNoSelEnd;
var _22a=_224.page.getPWins(false);
var _22b,_22c;
for(var i=0;i<_22a.length;i++){
_22b=_22a[i];
if(_22b){
_22c=_22b.dNodeCss;
_22c[_227]="";
_22c[_228]="";
_22c[_229]="";
_22c[_226]="block";
_22b.domNode.style.display="block";
_22b.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.hideAllPortletWindows=function(_22e){
var _22f=jetspeed;
var _230=_22f.css;
var _231=_230.cssDis,_232=_230.cssNoSelNm,_233=_230.cssNoSel,_234=_230.cssNoSelEnd;
var _235=_22f.page.getPWins(false);
var _236,_237,_238;
for(var i=0;i<_235.length;i++){
_237=_235[i];
_236=true;
if(_237&&_22e&&_22e.length>0){
for(var _23a=0;_23a<_22e.length;_23a++){
if(_237.widgetId==_22e[_23a]){
_236=false;
break;
}
}
}
if(_237){
_238=_237.dNodeCss;
_238[_232]="";
_238[_233]="";
_238[_234]="";
if(_236){
_238[_231]="none";
_237.domNode.style.display="none";
}else{
_238[_231]="block";
_237.domNode.style.display="block";
}
_237.domNode.style.visibility="visible";
}
}
};
jetspeed.widget.WinScroller=function(){
var _23b=this.jsObj;
this.UAmoz=_23b.UAmoz;
this.UAope=_23b.UAope;
};
dojo.extend(jetspeed.widget.WinScroller,{jsObj:jetspeed,djObj:dojo,typeNm:"WinScroller",V_AS_T:32,V_AS_V:16,autoScroll:function(e){
try{
var w=window;
var dy=0;
if(e.clientY<this.V_AS_T){
dy=-this.V_AS_V;
}else{
var _23f=null;
if(this.UAmoz){
_23f=w.innerHeight;
}else{
var doc=document,dd=doc.documentElement;
if(!this.UAope&&w.innerWidth){
_23f=w.innerHeight;
}else{
if(!this.UAope&&dd&&dd.clientWidth){
_23f=dd.clientHeight;
}else{
var b=jetspeed.docBody;
if(b.clientWidth){
_23f=b.clientHeight;
}
}
}
}
if(_23f!=null&&e.clientY>_23f-this.V_AS_T){
dy=this.V_AS_V;
}
}
w.scrollBy(0,dy);
}
catch(ex){
}
},_getErrMsg:function(ex,msg,_245,_246){
return ((_246!=null?(_246+"; "):"")+this.typeNm+" "+(_245==null?"<unknown>":_245.widgetId)+" "+msg+" ("+ex.toString()+")");
}});
jetspeed.widget.CreatePortletWindowResizeHandler=function(_247,_248){
var _249=new jetspeed.widget.PortletWindowResizeHandle(_247,_248);
var doc=document;
var _24b=doc.createElement("div");
_24b.className=_249.rhClass;
var _24c=doc.createElement("div");
_24b.appendChild(_24c);
_247.rbNode.appendChild(_24b);
_249.domNode=_24b;
_249.build();
return _249;
};
jetspeed.widget.PortletWindowResizeHandle=function(_24d,_24e){
this.pWin=_24d;
_24e.widget.WinScroller.call(this);
};
dojo.inherits(jetspeed.widget.PortletWindowResizeHandle,jetspeed.widget.WinScroller);
dojo.extend(jetspeed.widget.PortletWindowResizeHandle,{typeNm:"Resize",rhClass:"portletWindowResizeHandle",build:function(){
this.events=[jetspeed.ui.evtConnect("after",this.domNode,"onmousedown",this,"_beginSizing")];
},destroy:function(_24f,_250,jsUI){
this._cleanUpLastEvt(_24f,_250,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_24f);
this.events=this.pWin=null;
},_cleanUpLastEvt:function(_252,_253,jsUI){
var _255=null;
try{
jsUI.evtDisconnectWObjAry(this.tempEvents,_252);
this.tempEvents=null;
}
catch(ex){
_255=this._getErrMsg(ex,"event clean-up error",this.pWin,_255);
}
try{
_253.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_255=this._getErrMsg(ex,"clean-up error",this.pWin,_255);
}
if(_255!=null){
dojo.raise(_255);
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
var _259=jetspeed;
var jsUI=_259.ui;
var _25b=dojo;
var _25c=_25b.event;
var _25d=_259.docBody;
if(this.tempEvents!=null){
this._cleanUpLastEvt(_25c,_259,jsUI);
}
this._isSizing=true;
this.startPoint={x:e.pageX,y:e.pageY};
var mb=_25b.html.getMarginBox(node);
this.startSize={w:mb.width,h:mb.height};
var d=node.ownerDocument;
var _260=[];
_260.push(jsUI.evtConnect("after",_25d,"onmousemove",this,"_changeSizing",_25c,25));
_260.push(jsUI.evtConnect("after",_25d,"onmouseup",this,"_endSizing",_25c));
_260.push(jsUI.evtConnect("after",d,"ondragstart",_259,"_stopEvent",_25c));
_260.push(jsUI.evtConnect("after",d,"onselectstart",_259,"_stopEvent",_25c));
_259.page.displayAllPWinIFrameCovers(false);
this.tempEvents=_260;
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
var _267=pWin.posStatic;
if(_267){
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
var _26a=jetspeed;
this._cleanUpLastEvt(dojo.event,_26a,_26a.ui);
this._isSizing=false;
}});
jetspeed.widget.ie6PostDragAddDelay=60;
jetspeed.widget.ie6PostDragRmDelay=120;
jetspeed.widget.BackgroundIframe=function(node,_26c,_26d){
if(!_26c){
_26c=this.defaultStyleClass;
}
var html="<iframe src='' frameborder='0' scrolling='no' class='"+_26c+"'>";
this.iframe=_26d.doc().createElement(html);
this.iframe.tabIndex=-1;
node.appendChild(this.iframe);
};
dojo.lang.extend(jetspeed.widget.BackgroundIframe,{defaultStyleClass:"ie6BackgroundIFrame",iframe:null});
if(!dojo.dnd){
dojo.dnd={};
}
dojo.dnd.Mover=function(_26f,_270,_271,_272,e,_274,_275,_276){
var jsUI=_276.ui;
var _278=_275.event;
_276.widget.WinScroller.call(this);
if(_276.widget._movingInProgress){
if(djConfig.isDebug){
_276.debugAlert("ERROR - Mover initiation before previous Mover was destroyed");
}
}
_276.widget._movingInProgress=true;
this.moveInitiated=false;
this.moveableObj=_272;
this.windowOrLayoutWidget=_26f;
this.node=_270;
this.nodeLayoutColumn=_271;
this.posStatic=_26f.posStatic;
this.notifyOnAbsolute=_274;
if(e.ctrlKey&&_26f.moveAllowTilingChg){
if(this.posStatic){
this.changeToUntiled=true;
}else{
if(_276.prefs.windowTiling){
this.changeToTiled=true;
this.changeToTiledStarted=false;
}
}
}
this.posRecord={};
this.disqualifiedColumnIndexes=(_271!=null)?_271.getDescendantCols():{};
this.marginBox={l:e.pageX,t:e.pageY};
var doc=this.node.ownerDocument;
var _27a=[];
var _27b=jsUI.evtConnect("after",doc,"onmousemove",this,"onFirstMove",_278);
_27a.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_278));
_27a.push(jsUI.evtConnect("after",doc,"onmouseup",this,"mouseUpDestroy",_278));
_27a.push(jsUI.evtConnect("after",doc,"ondragstart",_276,"_stopEvent",_278));
_27a.push(jsUI.evtConnect("after",doc,"onselectstart",_276,"_stopEvent",_278));
if(_276.UAie6){
_27a.push(jsUI.evtConnect("before",doc,"onmousedown",this,"mouseDownDestroy",_278));
_27a.push(jsUI.evtConnect("before",_272.handle,"onmouseup",_272,"onMouseUp",_278));
}
_276.page.displayAllPWinIFrameCovers(false);
_27a.push(_27b);
this.events=_27a;
this.pSLastColChgIdx=null;
this.pSLastColChgTime=null;
this.pSLastNaturalColChgYTest=null;
this.pSLastNaturalColChgHistory=null;
this.pSLastNaturalColChgChoiceMap=null;
this.isDebug=false;
if(_276.debug.dragWindow){
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
this.devI=_276.debugindent;
this.devIH=_276.debugindentH;
this.devIT=_276.debugindentT;
this.devI3=_276.debugindent3;
this.devICH=_276.debugindentch;
}
};
dojo.inherits(dojo.dnd.Mover,jetspeed.widget.WinScroller);
dojo.extend(dojo.dnd.Mover,{typeNm:"Mover",pSColChgTimeTh:3000,onMouseMove:function(e){
var _27d=this.jsObj;
var _27e=this.djObj;
var _27f=this.UAmoz;
this.autoScroll(e);
var m=this.marginBox;
var _281=false;
var x=m.l+e.pageX;
var y=m.t+e.pageY;
var _284=this.isDebug;
var _285=false;
var _286=null,_287=null,_288,_289,_28a,_28b,_28c;
if(_284){
_288=this.devI;
_289=this.devIH;
_28a=this.devI3;
_28b=this.devICH,_28c=this.devIT;
_286=(new Date().getTime());
if(this.devLastX==null||this.devLastY==null){
this.devLastX=x;
this.devLastY=y;
}else{
var _28d=(Math.abs(x-this.devLastX)>this.devLrgTh)||(Math.abs(y-this.devLastY)>this.devLrgTh);
if(!_28d&&this.devLastTime!=null&&((this.devLastTime+this.devTimeTh)>_286)){
}else{
if(Math.abs(x-this.devLastX)>this.devChgTh){
this.devLastX=x;
_285=true;
}
if(Math.abs(y-this.devLastY)>this.devChgTh){
this.devLastY=y;
_285=true;
}
}
}
}
if(_27f&&this.firstEvtAdjustXY!=null){
x=x+this.firstEvtAdjustXY.l;
y=y+this.firstEvtAdjustXY.t;
this.firstEvtAdjustXY=null;
_281=true;
}
_27d.ui.setMarginBox(this.node,x,y,null,null,this.nodeLayoutInfo,_27d,_27e);
var _28e=this.posRecord;
_28e.left=x;
_28e.top=y;
var _28f=false;
var _290=this.posStatic;
if(!_290){
if(!_281&&this.changeToTiled&&!this.changeToTiledStarted){
_28f=true;
_290=true;
}
}
if(_290&&!_281){
var _291=this.columnInfoArray;
var _292=_27d.page.columns;
var _293=this.heightHalf;
var _294=_292.length;
var _295=e.pageX;
var _296=y+_293;
var _297=this.pSLastColChgIdx;
var _298=this.pSLastNaturalColChgChoiceMap;
var _299=null,_29a=[],_29b=null;
var _29c,_29d,_29e,_29f,lowY,_2a1,_2a2,_2a3,_2a4;
for(var i=0;i<_294;i++){
_29c=_291[i];
if(_29c!=null){
if(_295>=_29c.left&&_295<=_29c.right){
if(_296>=(_29c.top-30)||(_298!=null&&_298[i]!=null)){
_29d=Math.min(Math.abs(_296-(_29c.top)),Math.abs(e.pageY-(_29c.top)));
_29e=Math.min(Math.abs(_296-(_29c.yhalf)),Math.abs(e.pageY-(_29c.yhalf)));
_29f=Math.min(Math.abs(_296-_29c.bottom),Math.abs(e.pageY-_29c.bottom));
lowY=Math.min(_29d,_29e);
lowY=Math.min(lowY,_29f);
_2a2=null;
_2a4=_299;
while(_2a4!=null){
_2a3=_29a[_2a4];
if(lowY<_2a3.lowY){
break;
}else{
_2a2=_2a3;
_2a4=_2a3.nextIndex;
}
}
_29a.push({index:i,lowY:lowY,nextIndex:_2a4,lowYAlign:((!_284)?null:(lowY==_29d?"^":(lowY==_29e?"~":"_")))});
_2a1=(_29a.length-1);
if(_2a2!=null){
_2a2.nextIndex=_2a1;
}else{
_299=_2a1;
}
if(i==_297){
_29b=lowY;
}
}else{
if(_284){
if(_287==null){
_287=[];
}
var _2a6=(_29c.top-30)-_296;
_287.push(_27e.string.padRight(String(i),2,_28b)+" y! "+_27e.string.padRight(String(_2a6),4,_28b));
}
}
}else{
if(_284&&_295>_29c.width){
if(_287==null){
_287=[];
}
var _2a6=_295-_29c.width;
_287.push(_27e.string.padRight(String(i),2,_28b)+" x! "+_27e.string.padRight(String(_2a6),4,_28b));
}
}
}
}
var _2a7=-1;
var _2a8=-1,_2a9=-1;
var _2aa=null,_2ab=null,_2ac=null,_2ad=null,_2ae=null;
if(_299!=null){
_2a3=_29a[_299];
_2a7=_2a3.index;
_2aa=_2a3.lowY;
if(_2a3.nextIndex!=null){
_2a3=_29a[_2a3.nextIndex];
_2a8=_2a3.index;
_2ab=_2a3.lowY;
_2ad=_2ab-_2aa;
if(_2a3.nextIndex!=null){
_2a3=_29a[_2a3.nextIndex];
_2a9=_2a3.index;
_2ac=_2a3.lowY;
_2ae=_2ac-_2aa;
}
}
}
var _2af=null;
var _2b0=(new Date().getTime());
var _2b1=this.pSLastNaturalColChgYTest;
if(_29b==null||(_2b1!=null&&Math.abs(_296-_2b1)>=Math.max((_293-Math.floor(_293*0.3)),Math.min(_293,21)))){
if(_2a7>=0){
this.pSLastNaturalColChgYTest=_296;
this.pSLastNaturalColChgHistory=[_2a7];
_298={};
_298[_2a7]=true;
this.pSLastNaturalColChgChoiceMap=_298;
}
}else{
if(_2b1==null){
this.pSLastNaturalColChgYTest=_296;
_2a7=_297;
this.pSLastNaturalColChgHistory=[_2a7];
_298={};
_298[_2a7]=true;
this.pSLastNaturalColChgChoiceMap=_298;
}else{
var _2b2=null;
var _2b3=this.pSLastColChgTime+this.pSColChgTimeTh;
if(_2b3<_2b0){
var _2b4=this.pSLastNaturalColChgHistory;
var _2b5=(_2b4==null?0:_2b4.length);
var _2b6=null,_2b7;
_2a4=_299;
while(_2a4!=null){
_2a3=_29a[_2a4];
colI=_2a3.index;
if(_2b5==0){
_2b2=colI;
break;
}else{
_2b7=false;
for(var i=(_2b5-1);i>=0;i--){
if(_2b4[i]==colI){
if(_2b6==null||_2b6>i){
_2b6=i;
_2b2=colI;
}
_2b7=true;
break;
}
}
if(!_2b7){
_2b2=colI;
break;
}
}
_2a4=_2a3.nextIndex;
}
if(_2b2!=null){
_2a7=_2b2;
_298[_2a7]=true;
if(_2b5==0||_2b4[(_2b5-1)]!=_2a7){
_2b4.push(_2a7);
}
}
}else{
_2a7=_297;
}
if(_284&&_2b2!=null){
_27e.hostenv.println(_288+"ColChg YTest="+_2b1+" LeastRecentColI="+_2b2+" History=["+(this.pSLastNaturalColChgHistory?this.pSLastNaturalColChgHistory.join(", "):"")+"] Map={"+_27d.printobj(this.pSLastNaturalColChgChoiceMap)+"} expire="+(_2b0-_2b3)+"}");
}
}
}
if(_284&&_2af!=null){
if(this.devKeepLastMsg!=null){
_27e.hostenv.println(this.devKeepLastMsg);
this.devKeepLastMsg=null;
this.devKeepLastCount=0;
}
_27e.hostenv.println(_2af);
}
var col=(_2a7>=0?_292[_2a7]:null);
if(_284){
if(this.devLastColI!=_2a7){
_285=true;
}
this.devLastColI=_2a7;
}
var _2ba=_27d.widget.pwGhost;
if(_28f){
if(col!=null){
_27d.ui.setMarginBox(_2ba,null,null,null,m.h,this.nodeLayoutInfo,_27d,_27e);
_2ba.col=null;
this.changeToTiledStarted=true;
this.posStatic=true;
}
}
var _2bb=null,_2bc=false,_2bd=false;
if(_2ba.col!=col&&col!=null){
this.pSLastColChgTime=_2b0;
this.pSLastColChgIdx=_2a7;
var _2be=_2ba.col;
if(_2be!=null){
_27e.dom.removeNode(_2ba);
}
_2ba.col=col;
var _2bf=_291[_2a7];
var _2c0=_2bf.childCount+1;
_2bf.childCount=_2c0;
if(_2c0==1){
_292[_2a7].domNode.style.height="";
}
col.domNode.appendChild(_2ba);
_2bd=true;
var _2c1=(_297!=null?((_297!=_2a7)?_291[_297]:null):(_2be!=null?_291[_2be.getPageColumnIndex()]:null));
if(_2c1!=null){
var _2c2=_2c1.childCount-1;
if(_2c2<0){
_2c2=0;
}
_2c1.childCount=_2c2;
if(_2c2==0){
_292[_2c1.pageColIndex].domNode.style.height="1px";
}
}
}
var _2c3=null,_2c4=null;
if(col!=null){
_2c3=_27d.ui.getPWinAndColChildren(col.domNode,_2ba,true,false,true,false);
_2c4=_2c3.matchingNodes;
}
if(_2c4!=null&&_2c4.length>1){
var _2c5=_2c3.matchNodeIndexInMatchingNodes;
var _2c6=-1;
var _2c7=-1;
if(_2c5>0){
var _2c6=_27e.html.getAbsolutePosition(_2c4[_2c5-1],true).y;
if((y-25)<=_2c6){
_27e.dom.removeNode(_2ba);
_2bb=_2c4[_2c5-1];
_27e.dom.insertBefore(_2ba,_2bb,true);
}
}
if(_2c5!=(_2c4.length-1)){
var _2c7=_27e.html.getAbsolutePosition(_2c4[_2c5+1],true).y;
if((y+10)>=_2c7){
if(_2c5+2<_2c4.length){
_2bb=_2c4[_2c5+2];
_27e.dom.insertBefore(_2ba,_2bb,true);
}else{
col.domNode.appendChild(_2ba);
_2bc=true;
}
}
}
}
if(_285){
var _2c8="";
if(_2bb!=null||_2bc||_2bd){
_2c8="put=";
if(_2bb!=null){
_2c8+="before("+_2bb.id+")";
}else{
if(_2bc){
_2c8+="end";
}else{
if(_2bd){
_2c8+="end-default";
}
}
}
}
_27e.hostenv.println(_288+"col="+_2a7+_289+_2c8+_289+"x="+x+_289+"y="+y+_289+"ePGx="+e.pageX+_289+"ePGy="+e.pageY+_289+"yTest="+_296);
var _2c9="",colI,_29c;
_2a4=_299;
while(_2a4!=null){
_2a3=_29a[_2a4];
colI=_2a3.index;
_29c=_291[_2a3.index];
_2c9+=(_2c9.length>0?_28c:"")+colI+_2a3.lowYAlign+(colI<10?_28b:"")+" -> "+_27e.string.padRight(String(_2a3.lowY),4,_28b);
_2a4=_2a3.nextIndex;
}
_27e.hostenv.println(_28a+_2c9);
if(_287!=null){
var _2ca="";
for(i=0;i<_287.length;i++){
_2ca+=(i>0?_28c:"")+_287[i];
}
_27e.hostenv.println(_28a+_2ca);
}
this.devLastTime=_286;
this.devChgTh=this.devChgSubsqTh;
}
}
},onFirstMove:function(){
var _2cb=this.jsObj;
var jsUI=_2cb.ui;
var _2cd=this.djObj;
var _2ce=this.windowOrLayoutWidget;
var node=this.node;
var _2d0=_2ce._getLayoutInfoMoveable();
this.nodeLayoutInfo=_2d0;
var mP=_2ce._getWindowMarginBox(_2d0,_2cb);
this.staticWidth=null;
var _2d2=_2cb.widget.pwGhost;
var _2d3=this.UAmoz;
var _2d4=this.changeToUntiled;
var _2d5=this.changeToTiled;
var m=null;
if(this.posStatic){
if(!_2d4){
var _2d7=_2ce.getPageColumnIndex();
var _2d8=(_2d7>=0?_2cb.page.columns[_2d7]:null);
_2d2.col=_2d8;
this.pSLastColChgTime=new Date().getTime();
this.pSLastColChgIdx=_2d7;
}
m={w:mP.w,h:mP.h};
var _2d9=node.parentNode;
var _2da=document.getElementById(_2cb.id.DESKTOP);
var _2db=node.style;
this.staticWidth=_2db.width;
var _2dc=_2cd.html.getAbsolutePosition(node,true);
var _2dd=_2d0.mE;
m.l=_2dc.left-_2dd.l;
m.t=_2dc.top-_2dd.t;
if(_2d3){
if(!_2d4){
jsUI.setMarginBox(_2d2,null,null,null,mP.h,_2d0,_2cb,_2cd);
}
this.firstEvtAdjustXY={l:m.l,t:m.t};
}
_2db.position="absolute";
if(!_2d4){
_2db.zIndex=_2cb.page.getPWinHighZIndex()+1;
}else{
_2db.zIndex=(_2ce._setAsTopZIndex(_2cb.page,_2cb.css,_2ce.dNodeCss,false));
}
if(!_2d4){
_2d9.insertBefore(_2d2,node);
if(!_2d3){
jsUI.setMarginBox(_2d2,null,null,null,mP.h,_2d0,_2cb,_2cd);
}
_2da.appendChild(node);
var _2de=jsUI.getPWinAndColChildren(_2d9,_2d2,true,false,true);
this.prevColumnNode=_2d9;
this.prevIndexInCol=_2de.matchNodeIndexInMatchingNodes;
}else{
_2ce._updtDimsObj(false,true);
_2da.appendChild(node);
}
}else{
m=mP;
}
this.moveInitiated=true;
m.l-=this.marginBox.l;
m.t-=this.marginBox.t;
this.marginBox=m;
jsUI.evtDisconnectWObj(this.events.pop(),_2cd.event);
var _2df=this.disqualifiedColumnIndexes;
var _2e0=(this.isDebug||_2cb.debug.dragWindowStart),_2e1;
if(_2e0){
_2e1=_2cb.debugindentT;
var _2e2=_2cb.debugindentH;
var _2e3="";
if(_2df!=null){
_2e3=_2e2+"dqCols=["+_2cb.objectKeys(_2df).join(", ")+"]";
}
var _2e4=_2ce.title;
if(_2e4==null){
_2e4=node.id;
}
_2cd.hostenv.println("DRAG \""+_2e4+"\""+_2e2+((this.posStatic&&!_2d4)?("col="+(_2d2.col?_2d2.col.getPageColumnIndex():"null")+_2e2):"")+"m.l = "+m.l+_2e2+"m.t = "+m.t+_2e3);
}
if(this.posStatic||_2d5){
this.heightHalf=mP.h/2;
var _2e5=_2cb.page.columns||[];
var _2e6=_2e5.length;
var _2e7=new Array(_2e6);
var _2e8=_2cd.byId(_2cb.id.COLUMNS);
if(_2e8){
var _2e9=_2cb.page.layoutInfo;
this._getChildColInfo(_2e8,_2e7,_2cb.page.columns,_2df,_2e9,_2e9.columns,_2e9.desktop,node,(_2e0?1:null),_2e1,_2cd,_2cb);
if(_2e0){
_2cd.hostenv.println(_2e1+"--------------------");
}
}
this.columnInfoArray=_2e7;
}
if(this.posStatic){
jsUI.setMarginBox(node,m.l,m.t,mP.w,null,_2d0,_2cb,_2cd);
if(this.notifyOnAbsolute){
_2ce.dragChangeToAbsolute(this,node,this.marginBox,_2cd,_2cb);
}
if(_2d4){
this.posStatic=false;
}
}
},_getChildColInfo:function(_2ea,_2eb,_2ec,_2ed,_2ee,_2ef,_2f0,_2f1,_2f2,_2f3,_2f4,_2f5){
var _2f6=_2ea.childNodes;
var _2f7=(_2f6?_2f6.length:0);
if(_2f7==0){
return;
}
var _2f8=_2f4.html.getAbsolutePosition(_2ea,true);
var _2f9=_2f5.ui.getMarginBox(_2ea,_2ef,_2f0,_2f5);
var _2fa=_2ee.column;
var _2fb,col,_2fd,_2fe,_2ff,_300,_301,_302,_303,_304,_305,_306,_307;
var _308=null,_309=(_2f2!=null?(_2f2+1):null),_30a,_30b;
for(var i=0;i<_2f7;i++){
_2fb=_2f6[i];
_2fd=_2fb.getAttribute("columnindex");
_2fe=(_2fd==null?-1:new Number(_2fd));
if(_2fe>=0){
_2ff=_2fb.getAttribute("layoutid");
_300=(_2ff!=null&&_2ff.length>0);
_307=true;
_30a=_309;
_30b=null;
if(!_300&&(!(_2fb===_2f1))){
col=_2ec[_2fe];
if(col&&!col.layoutActionsDisabled&&(_2ed==null||_2ed[_2fe]==null)){
_301=_2f5.ui.getMarginBox(_2fb,_2fa,_2ef,_2f5);
if(_308==null){
_308=_301.t-_2f9.t;
_306=_2f9.h-_308;
}
_302=_2f8.left+(_301.l-_2f9.l);
_303=_2f8.top+_308;
_304=_301.h;
if(_304<_306){
_304=_306;
}
if(_304<40){
_304=40;
}
var _30d=_2fb.childNodes;
_305={left:_302,top:_303,right:(_302+_301.w),bottom:(_303+_304),childCount:(_30d?_30d.length:0),pageColIndex:_2fe};
_305.height=_305.bottom-_305.top;
_305.width=_305.right-_305.left;
_305.yhalf=_305.top+(_305.height/2);
_2eb[_2fe]=_305;
_307=(_305.childCount>0);
if(_307){
_2fb.style.height="";
}else{
_2fb.style.height="1px";
}
if(_2f2!=null){
_30b=(_2f5.debugDims(_305,true)+" yhalf="+_305.yhalf+(_301.h!=_304?(" hreal="+_301.h):"")+" childC="+_305.childCount+"}");
}
}
}
if(_2f2!=null){
if(_300){
_30a=_309+1;
}
if(_30b==null){
_30b="---";
}
_2f4.hostenv.println(_2f4.string.repeat(_2f3,_2f2)+"["+((_2fe<10?" ":"")+_2fd)+"] "+_30b);
}
if(_307){
this._getChildColInfo(_2fb,_2eb,_2ec,_2ed,_2ee,(_300?_2ee.columnLayoutHeader:_2fa),_2ef,_2f1,_30a,_2f3,_2f4,_2f5);
}
}
}
},mouseDownDestroy:function(e){
var _30f=this.jsObj;
_30f.stopEvent(e);
this.mouseUpDestroy();
},mouseUpDestroy:function(){
var _310=this.djObj;
var _311=this.jsObj;
this.destroy(_310,_310.event,_311,_311.ui);
},destroy:function(_312,_313,_314,jsUI){
var _316=this.windowOrLayoutWidget;
var node=this.node;
var _318=null;
if(this.moveInitiated&&_316&&node){
this.moveInitiated=false;
try{
if(this.posStatic){
var _319=_314.widget.pwGhost;
var _31a=node.style;
if(_319&&_319.col){
_316.column=0;
_312.dom.insertBefore(node,_319,true);
}else{
if(this.prevColumnNode!=null&&this.prevIndexInCol!=null){
_312.dom.insertAtIndex(node,this.prevColumnNode,this.prevIndexInCol);
}else{
var _31b=_314.page.getColumnDefault();
if(_31b!=null){
_312.dom.prependChild(node,_31b.domNode);
}
}
}
if(_319){
_312.dom.removeNode(_319);
}
}
_316.endDragging(this.posRecord,this.changeToUntiled,this.changeToTiled);
}
catch(ex){
_318=this._getErrMsg(ex,"destroy reset-window error",_316,_318);
}
}
try{
jsUI.evtDisconnectWObjAry(this.events,_313);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
this.events=this.node=this.windowOrLayoutWidget=this.moveableObj=this.prevColumnNode=this.prevIndexInCol=null;
}
catch(ex){
_318=this._getErrMsg(ex,"destroy event clean-up error",_316,_318);
if(this.moveableObj!=null){
this.moveableObj.mover=null;
}
}
try{
_314.page.displayAllPWinIFrameCovers(true);
}
catch(ex){
_318=this._getErrMsg(ex,"destroy clean-up error",_316,_318);
}
_314.widget._movingInProgress=false;
if(_318!=null){
_312.raise(_318);
}
}});
dojo.dnd.Moveable=function(_31c,opt){
var _31e=jetspeed;
var jsUI=_31e.ui;
var _320=dojo;
var _321=_320.event;
this.windowOrLayoutWidget=_31c;
this.handle=opt.handle;
var _322=[];
_322.push(jsUI.evtConnect("after",this.handle,"onmousedown",this,"onMouseDown",_321));
_322.push(jsUI.evtConnect("after",this.handle,"ondragstart",_31e,"_stopEvent",_321));
_322.push(jsUI.evtConnect("after",this.handle,"onselectstart",_31e,"_stopEvent",_321));
this.events=_322;
};
dojo.extend(dojo.dnd.Moveable,{minMove:5,enabled:true,mover:null,onMouseDown:function(e){
if(e&&e.button==2){
return;
}
var _324=dojo;
var _325=_324.event;
var _326=jetspeed;
var jsUI=jetspeed.ui;
if(this.mover!=null||this.tempEvents!=null){
this._cleanUpLastEvt(_324,_325,_326,jsUI);
_326.stopEvent(e);
}else{
if(this.enabled){
if(this.tempEvents!=null){
if(djConfig.isDebug){
_326.debugAlert("ERROR: Moveable onmousedown tempEvent already defined");
}
}else{
var _328=[];
var doc=this.handle.ownerDocument;
_328.push(jsUI.evtConnect("after",doc,"onmousemove",this,"onMouseMove",_325));
this.tempEvents=_328;
}
if(!this.windowOrLayoutWidget.posStatic){
this.windowOrLayoutWidget.bringToTop(e,false,true,_326);
}
this._lastX=e.pageX;
this._lastY=e.pageY;
this._mDownEvt=e;
}
}
_326.stopEvent(e);
},onMouseMove:function(e,_32b){
var _32c=jetspeed;
var _32d=dojo;
var _32e=_32d.event;
if(_32b||Math.abs(e.pageX-this._lastX)>this.minMove||Math.abs(e.pageY-this._lastY)>this.minMove){
this._cleanUpLastEvt(_32d,_32e,_32c,_32c.ui);
var _32f=this.windowOrLayoutWidget;
var _330=null;
this.beforeDragColRowInfo=null;
if(!_32f.isLayoutPane){
var _331=_32f.domNode;
if(_331!=null){
this.node=_331;
this.mover=new _32d.dnd.Mover(_32f,_331,_330,this,e,false,_32d,_32c);
}
}else{
_32f.startDragging(e,this,_32d,_32c);
}
}
_32c.stopEvent(e);
},onMouseUp:function(e,_333){
var _334=dojo;
var _335=jetspeed;
this._cleanUpLastEvt(_334,_334.event,_335,_335.ui,_333);
},_cleanUpLastEvt:function(_336,_337,_338,jsUI,_33a){
if(this._mDownEvt!=null){
_338.stopEvent(this._mDownEvt,_33a);
this._mDownEvt=null;
}
if(this.mover!=null){
this.mover.destroy(_336,_337,_338,jsUI);
this.mover=null;
}
jsUI.evtDisconnectWObjAry(this.tempEvents,_337);
this.tempEvents=null;
},destroy:function(_33b,_33c,_33d,jsUI){
this._cleanUpLastEvt(_33b,_33c,_33d,jsUI);
jsUI.evtDisconnectWObjAry(this.events,_33c);
this.events=this.node=this.handle=this.windowOrLayoutWidget=this.beforeDragColRowInfo=null;
},enable:function(){
this.enabled=true;
},disable:function(){
this.enabled=false;
}});
dojo.getMarginBox=function(node,_340,_341){
var s=_340||dojo.gcs(node),me=dojo._getMarginExtents(node,s,_341);
var l=node.offsetLeft-me.l,t=node.offsetTop-me.t;
if(_341.UAmoz){
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
if(_341.UAope){
var p=node.parentNode;
if(p){
var be=dojo._getBorderExtents(p);
l-=be.l,t-=be.t;
}
}
}
return {l:l,t:t,w:node.offsetWidth+me.w,h:node.offsetHeight+me.h};
};
dojo.getContentBox=function(node,_34c,_34d){
var s=_34c||dojo.gcs(node),pe=dojo._getPadExtents(node,s),be=dojo._getBorderExtents(node,s),w=node.clientWidth,h;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
}else{
h=node.clientHeight,be.w=be.h=0;
}
if(_34d.UAope){
pe.l+=be.l;
pe.t+=be.t;
}
return {l:pe.l,t:pe.t,w:w-pe.w-be.w,h:h-pe.h-be.h};
};
dojo.setMarginBox=function(node,_354,_355,_356,_357,_358,_359){
var s=_358||dojo.gcs(node);
var bb=dojo._usesBorderBox(node),pb=bb?{l:0,t:0,w:0,h:0}:dojo._getPadBorderExtents(node,s),mb=dojo._getMarginExtents(node,s,_359);
if(_356!=null&&_356>=0){
_356=Math.max(_356-pb.w-mb.w,0);
}
if(_357!=null&&_357>=0){
_357=Math.max(_357-pb.h-mb.h,0);
}
dojo._setBox(node,_354,_355,_356,_357);
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
dojo._getPadExtents=function(n,_367){
var s=_367||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.paddingLeft),t=px(n,s.paddingTop);
return {l:l,t:t,w:l+px(n,s.paddingRight),h:t+px(n,s.paddingBottom)};
};
dojo._getPadBorderExtents=function(n,_36d){
var s=_36d||dojo.gcs(n),p=dojo._getPadExtents(n,s),b=dojo._getBorderExtents(n,s);
return {l:p.l+b.l,t:p.t+b.t,w:p.w+b.w,h:p.h+b.h};
};
dojo._getMarginExtents=function(n,_372,_373){
var s=_372||dojo.gcs(n),px=dojo._toPixelValue,l=px(n,s.marginLeft),t=px(n,s.marginTop),r=px(n,s.marginRight),b=px(n,s.marginBottom);
if(_373.UAsaf&&(s.position!="absolute")){
r=l;
}
return {l:l,t:t,w:l+r,h:t+b};
};
dojo._getBorderExtents=function(n,_37b){
var ne="none",px=dojo._toPixelValue,s=_37b||dojo.gcs(n),bl=(s.borderLeftStyle!=ne?px(n,s.borderLeftWidth):0),bt=(s.borderTopStyle!=ne?px(n,s.borderTopWidth):0);
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
dojo._toPixelValue=function(_384,_385){
return (parseFloat(_385)||0);
};
}else{
dojo.getComputedStyle=function(node){
return node.currentStyle;
};
dojo._toPixelValue=function(_387,_388){
if(!_388){
return 0;
}
if(_388.slice&&(_388.slice(-2)=="px")){
return parseFloat(_388);
}
with(_387){
var _389=style.left;
var _38a=runtimeStyle.left;
runtimeStyle.left=currentStyle.left;
try{
style.left=_388;
_388=style.pixelLeft;
}
catch(e){
_388=0;
}
style.left=_389;
runtimeStyle.left=_38a;
}
return _388;
};
}
dojo.gcs=dojo.getComputedStyle;

