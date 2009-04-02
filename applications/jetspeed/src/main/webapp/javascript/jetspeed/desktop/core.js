dojo.provide("jetspeed.desktop.core");
dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("jetspeed.common");
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.om){
jetspeed.om={};
}
if(!jetspeed.debug){
jetspeed.debug={};
}
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",P_CLASS:"portlet",PWIN_CLASS:"portletWindow",PWIN_CLIENT_CLASS:"portletWindowClient",PWIN_GHOST_CLASS:"ghostPane",PW_ID_PREFIX:"pw_",COL_CLASS:"desktopColumn",COL_LAYOUTHEADER_CLASS:"desktopLayoutHeader",PP_WIDGET_ID:"widgetId",PP_CONTENT_RETRIEVER:"contentRetriever",PP_DESKTOP_EXTENDED:"jsdesktop",PP_WINDOW_POSITION_STATIC:"windowPositionStatic",PP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PP_WINDOW_DECORATION:"windowDecoration",PP_WINDOW_TITLE:"title",PP_WINDOW_ICON:"windowIcon",PP_WIDTH:"width",PP_HEIGHT:"height",PP_LEFT:"left",PP_TOP:"top",PP_COLUMN:"column",PP_ROW:"row",PP_EXCLUDE_PCONTENT:"excludePContent",PP_WINDOW_STATE:"windowState",PP_STATICPOS:"staticpos",PP_FITHEIGHT:"fitheight",PP_PROP_SEPARATOR:"=",PP_PAIR_SEPARATOR:";",ACT_MENU:"menu",ACT_MINIMIZE:"minimized",ACT_MAXIMIZE:"maximized",ACT_RESTORE:"normal",ACT_PRINT:"print",ACT_EDIT:"edit",ACT_VIEW:"view",ACT_HELP:"help",ACT_ADDPORTLET:"addportlet",ACT_REMOVEPORTLET:"removeportlet",ACT_CHANGEPORTLETTHEME:"changeportlettheme",ACT_DESKTOP_TILE:"tile",ACT_DESKTOP_UNTILE:"untile",ACT_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACT_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACT_DESKTOP_MOVE_TILED:"movetiled",ACT_DESKTOP_MOVE_UNTILED:"moveuntiled",ACT_LOAD_RENDER:"loadportletrender",ACT_LOAD_ACTION:"loadportletaction",ACT_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",ADDP_RFRAG:"aR",PG_ED_STATE_PARAM:"epst",PG_ED_TITLES_PARAM:"wintitles",PORTAL_ORIGINATE_PARAMETER:"portal",PM_P_AD:256,PM_P_D:1024,PM_MZ_P:2048,DEBUG_WINDOW_TAG:"js-db"};
jetspeed.prefs={windowTiling:true,windowHeightExpand:false,ajaxPageNavigation:false,windowWidth:null,windowHeight:null,layoutName:null,layoutRootUrl:null,getLayoutName:function(){
if(jetspeed.prefs.layoutName==null&&djConfig.jetspeed!=null){
jetspeed.prefs.layoutName=djConfig.jetspeed.layoutName;
}
return jetspeed.prefs.layoutName;
},getLayoutRootUrl:function(){
if(jetspeed.prefs.layoutRootUrl==null&&djConfig.jetspeed!=null){
jetspeed.prefs.layoutRootUrl=jetspeed.url.basePortalDesktopUrl()+djConfig.jetspeed.layoutDecorationPath;
}
return jetspeed.prefs.layoutRootUrl;
},getPortletDecorationsRootUrl:function(){
if(jetspeed.prefs.portletDecorationsRootUrl==null&&djConfig.jetspeed!=null){
jetspeed.prefs.portletDecorationsRootUrl=jetspeed.url.basePortalDesktopUrl()+djConfig.jetspeed.portletDecorationsPath;
}
return jetspeed.prefs.portletDecorationsRootUrl;
},portletSelectorWindowTitle:"Portlet Selector",portletSelectorWindowIcon:"text-x-script.png",portletSelectorBounds:{x:20,y:20,width:400,height:600},windowActionButtonMax:5,windowActionButtonTooltip:true,windowIconEnabled:true,windowIconPath:"/images/portlets/small",windowTitlebar:true,windowResizebar:true,windowDecoration:"tigris",pageActionButtonTooltip:true,getPortletDecorationBaseUrl:function(_1){
return jetspeed.prefs.getPortletDecorationsRootUrl()+"/"+_1;
},getActionLabel:function(_2,_3,_4,_5){
if(_2==null){
return null;
}
var _6=null;
var _7=_4.desktopActionLabels;
if(_7!=null){
_6=_7[_2];
}
if(_6==null||_6.length==0){
_6=null;
if(!_3){
_6=_5.string.capitalize(_2);
}
}
return _6;
}};
jetspeed.page=null;
jetspeed.initializeDesktop=function(){
var _8=jetspeed;
var _9=_8.id;
var _a=_8.prefs;
var _b=_8.debug;
var _c=dojo;
_8.getBody();
_8.ui.initCssObj();
_a.windowActionButtonOrder=[_9.ACT_MENU,"edit","view","help",_9.ACT_MINIMIZE,_9.ACT_RESTORE,_9.ACT_MAXIMIZE];
_a.windowActionNotPortlet=[_9.ACT_MENU,_9.ACT_MINIMIZE,_9.ACT_RESTORE,_9.ACT_MAXIMIZE];
_a.windowActionMenuOrder=[_9.ACT_DESKTOP_HEIGHT_EXPAND,_9.ACT_DESKTOP_HEIGHT_NORMAL,_9.ACT_DESKTOP_TILE,_9.ACT_DESKTOP_UNTILE];
_8.url.pathInitialize();
var _d=djConfig.jetspeed;
if(_d!=null){
for(var _e in _d){
var _f=_d[_e];
if(_f!=null){
if(_b[_e]!=null){
_b[_e]=_f;
}else{
_a[_e]=_f;
}
}
}
if(_a.windowWidth==null||isNaN(_a.windowWidth)){
_a.windowWidth="280";
}
if(_a.windowHeight==null||isNaN(_a.windowHeight)){
_a.windowHeight="200";
}
var _10=[_9.ACT_DESKTOP_HEIGHT_EXPAND,_9.ACT_DESKTOP_HEIGHT_NORMAL,_9.ACT_DESKTOP_TILE,_9.ACT_DESKTOP_UNTILE];
var _11={};
for(var i=0;i<_10.length;i++){
_11[_10[i]]=true;
}
_10.push(_9.ACT_DESKTOP_MOVE_TILED);
_10.push(_9.ACT_DESKTOP_MOVE_UNTILED);
_a.windowActionDesktopAll=_10;
_a.windowActionDesktop=_11;
}
var _13=new _c.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PortletWindow.css");
_c.html.insertCssFile(_13,document,true);
if(_a.portletDecorationsAllowed==null||_a.portletDecorationsAllowed.length==0){
if(_a.windowDecoration!=null){
_a.portletDecorationsAllowed=[_a.windowDecoration];
}
}else{
if(_a.windowDecoration==null){
_a.windowDecoration=_a.portletDecorationsAllowed[0];
}
}
if(_a.windowDecoration==null||_a.portletDecorationsAllowed==null){
_c.raise("No portlet decorations");
return;
}
if(_a.windowActionNoImage!=null){
var _14={};
for(var i=0;i<_a.windowActionNoImage.length;i++){
_14[_a.windowActionNoImage[i]]=true;
}
_a.windowActionNoImage=_14;
}
var _15=_8.url.parse(window.location.href);
var _16=_8.url.getQueryParameter(_15,"jsprintmode")=="true";
if(_16){
_16={};
_16.action=_8.url.getQueryParameter(_15,"jsaction");
_16.entity=_8.url.getQueryParameter(_15,"jsentity");
_16.layout=_8.url.getQueryParameter(_15,"jslayoutid");
_a.printModeOnly=_16;
_a.windowTiling=true;
_a.windowHeightExpand=true;
_a.ajaxPageNavigation=false;
}
_a.portletDecorationsConfig={};
for(var i=0;i<_a.portletDecorationsAllowed.length;i++){
_8.loadPortletDecorationConfig(_a.portletDecorationsAllowed[i],_a,_9);
}
if(_8.UAie6){
_a.ajaxPageNavigation=false;
}
if(_16){
for(var _17 in _a.portletDecorationsConfig){
var _18=_a.portletDecorationsConfig[_17];
if(_18!=null){
_18.windowActionButtonOrder=null;
_18.windowActionMenuOrder=null;
_18.windowDisableResize=true;
_18.windowDisableMove=true;
}
}
}
_8.url.loadingIndicatorShow();
var _19={};
if(_a.windowActionButtonOrder){
var _1a,_1b,_1c;
var _1d=[_a.windowActionButtonOrder,_a.windowActionMenuOrder,_a.windowActionDesktopAll];
for(var _1e=0;_1e<_1d.length;_1e++){
var _1c=_1d[_1e];
if(!_1c){
continue;
}
for(var aI=0;aI<_1c.length;aI++){
_1a=_1c[aI];
if(_1a!=null&&!_19[_1a]){
_19[_1a]=_a.getActionLabel(_1a,false,_a,_c);
}
}
}
}
_8.widget.PortletWindow.prototype.actionLabels=_19;
_8.page=new _8.om.Page();
if(!_16&&djConfig.isDebug){
if(_8.debugWindowLoad){
_8.debugWindowLoad();
}
if(_8.debug.profile&&_c.profile){
_c.profile.start("initializeDesktop");
}else{
_8.debug.profile=false;
}
}else{
_8.debug.profile=false;
}
_8.page.retrievePsml();
_8.ui.windowResizeMgr.init(window,_8.docBody);
};
jetspeed.updatePage=function(_20,_21,_22,_23){
var _24=jetspeed;
var _25=false;
if(djConfig.isDebug&&_24.debug.profile){
_25=true;
dojo.profile.start("updatePage");
}
var _26=_24.page;
if(!_20||!_26||_24.pageNavigateSuppress){
return;
}
if(!_22&&_26.equalsPageUrl(_20)){
return;
}
_20=_26.makePageUrl(_20);
if(_20!=null){
_24.updatePageBegin();
if(_23!=null&&_23.editModeMove){
var _27={};
var _28=_26.getPWins();
for(var i=0;i<_28.length;i++){
_2a=_28[i];
if(_2a&&_2a.portlet){
_27[_2a.portlet.entityId]=_2a.getPortletTitle();
}
}
_23.windowTitles=_27;
}
var _2b=_26.layoutDecorator;
var _2c=_26.editMode;
if(_25){
dojo.profile.start("destroyPage");
}
_26.destroy();
if(_25){
dojo.profile.end("destroyPage");
}
var _2d=_26.portlet_windows;
var _2e=_26.portlet_window_count;
var _2f=new _24.om.Page(_2b,_20,(!djConfig.preventBackButtonFix&&!_21),_26.tooltipMgr,_26.iframeCoverByWinId);
_24.page=_2f;
var _2a;
if(_2e>0){
for(var _30 in _2d){
_2a=_2d[_30];
_2a.bringToTop(null,true,false,_24);
}
}
_2f.retrievePsml(new _24.om.PageCLCreateWidget(true,_23));
if(_2e>0){
for(var _30 in _2d){
_2a=_2d[_30];
_2f.putPWin(_2a);
}
}
window.focus();
}
};
jetspeed.updatePageBegin=function(){
var _31=jetspeed;
if(_31.UAie6){
_31.docBody.attachEvent("onclick",_31.ie6StopMouseEvts);
_31.docBody.setCapture();
}
};
jetspeed.ie6StopMouseEvts=function(e){
if(e){
e.cancelBubble=true;
e.returnValue=false;
}
};
jetspeed.updatePageEnd=function(){
var _33=jetspeed;
if(_33.UAie6){
_33.docBody.releaseCapture();
_33.docBody.detachEvent("onclick",_33.ie6StopMouseEvts);
_33.docBody.releaseCapture();
}
};
jetspeed.doRender=function(_34,_35){
if(!_34){
_34={};
}else{
if((typeof _34=="string"||_34 instanceof String)){
_34={url:_34};
}
}
var _36=jetspeed.page.getPortlet(_35);
if(_36){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_35+"] url: "+_34.url);
}
_36.retrieveContent(null,_34);
}
};
jetspeed.doAction=function(_37,_38){
if(!_37){
_37={};
}else{
if((typeof _37=="string"||_37 instanceof String)){
_37={url:_37};
}
}
var _39=jetspeed.page.getPortlet(_38);
if(_39){
if(jetspeed.debug.doRenderDoAction){
if(!_37.formNode){
dojo.debug("doAction ["+_38+"] url: "+_37.url+" form: null");
}else{
dojo.debug("doAction ["+_38+"] url: "+_37.url+" form: "+jetspeed.debugDumpForm(_37.formNode));
}
}
_39.retrieveContent(new jetspeed.om.PortletActionCL(_39,_37),_37);
}
};
jetspeed.PortletRenderer=function(_3a,_3b,_3c,_3d,_3e,_3f){
var _40=jetspeed;
var _41=_40.page;
var _42=dojo;
this._jsObj=_40;
this.mkWins=_3a;
this.initEdit=_3f;
this.minimizeTemp=(_3f!=null&&_3f.editModeMove);
this.noRender=(this.minimizeTemp&&_3f.windowTitles!=null);
this.isPgLd=_3b;
this.isPgUp=_3c;
this.renderUrl=_3d;
this.suppressGetActions=_3e;
this._colLen=_41.columns.length;
this._colIndex=0;
this._portletIndex=0;
this._renderCount=0;
this.psByCol=_41.portletsByPageColumn;
this.pageLoadUrl=null;
if(_3b){
this.pageLoadUrl=_40.url.parse(_41.getPageUrl());
_40.ui.evtConnect("before",_42,"addOnLoad",_41,"_beforeAddOnLoad",_42.event);
}
this.dbgPgLd=_40.debug.pageLoad&&_3b;
this.dbgMsg=null;
if(_40.debug.doRenderDoAction||this.dbgPgLd){
this.dbgMsg="";
}
};
dojo.lang.extend(jetspeed.PortletRenderer,{renderAll:function(){
do{
this._renderCurrent();
}while(this._evalNext());
this._finished();
},renderAllTimeDistribute:function(){
this._renderCurrent();
if(this._evalNext()){
dojo.lang.setTimeout(this,this.renderAllTimeDistribute,10);
}else{
this._finished();
}
},_finished:function(){
var _43=this._jsObj;
var _44=this.dbgMsg;
if(_44!=null){
if(this.dbgPgLd){
dojo.debug("portlet-renderer page-url: "+_43.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPgLd){
_43.page.loadPostRender(this.isPgUp,this.initEdit);
}
},_renderCurrent:function(){
var _45=this._jsObj;
var _46=this._colLen;
var _47=this._colIndex;
var _48=this._portletIndex;
if(_47<=_46){
var _49;
if(_47<_46){
_49=this.psByCol[_47.toString()];
}else{
_49=this.psByCol["z"];
_47=null;
}
var _4a=(_49!=null?_49.length:0);
if(_4a>0){
var _4b=_49[_48];
if(_4b){
var _4c=_4b.portlet;
var _4d=null;
if(this.mkWins){
_4d=_45.ui.createPortletWindow(_4c,_47,_45);
if(this.minimizeTemp){
_4d.minimizeWindowTemporarily(this.noRender);
}
}
var _4e=this.dbgMsg;
if(_4e!=null){
if(_4e.length>0){
_4e=_4e+", ";
}
var _4f=null;
if(_4c.getProperty!=null){
_4f=_4c.getProperty(_45.id.PP_WIDGET_ID);
}
if(!_4f){
_4f=_4c.widgetId;
}
if(!_4f){
_4f=_4c.toString();
}
if(_4c.entityId){
_4e=_4e+_4c.entityId+"("+_4f+")";
if(this._dbPgLd&&_4c.getProperty(_45.id.PP_WINDOW_TITLE)){
_4e=_4e+" "+_4c.getProperty(_45.id.PP_WINDOW_TITLE);
}
}else{
_4e=_4e+_4f;
}
}
if(!this.noRender){
_4c.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}else{
if(_4d&&_4d.portlet){
var _50=this.initEdit.windowTitles[_4d.portlet.entityId];
if(_50!=null){
_4d.setPortletTitle(_50);
}
}
}
if((this._renderCount%3)==0){
_45.url.loadingIndicatorStep(_45);
}
this._renderCount++;
}
}
}
},_evalNext:function(){
var _51=false;
var _52=this._colLen;
var _53=this._colIndex;
var _54=this._portletIndex;
var _55=_53;
var _56;
for(++_53;_53<=_52;_53++){
_56=this.psByCol[_53==_52?"z":_53.toString()];
if(_54<(_56!=null?_56.length:0)){
_51=true;
this._colIndex=_53;
break;
}
}
if(!_51){
++_54;
for(_53=0;_53<=_55;_53++){
_56=this.psByCol[_53==_52?"z":_53.toString()];
if(_54<(_56!=null?_56.length:0)){
_51=true;
this._colIndex=_53;
this._portletIndex=_54;
break;
}
}
}
return _51;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_57){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _59=_57;
var _5a=null;
if(_57&&_57.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_57.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_57&&_57.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_57.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_5a=jetspeed.url.getQueryParameter(_57,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_59)){
_59=null;
}
return {url:_59,operation:op,portletEntityId:_5a};
},genPseudoUrl:function(_5b,_5c){
if(!_5b||!_5b.url||!_5b.portletEntityId){
return null;
}
var _5d=null;
if(_5c){
_5d=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_5d="javascript:";
var _5e=false;
if(_5b.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_5d+="doAction(\"";
}else{
if(_5b.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_5d+="doRender(\"";
}else{
_5e=true;
}
}
if(_5e){
return null;
}
_5d+=_5b.url+"\",\""+_5b.portletEntityId+"\"";
_5d+=")";
}
return _5d;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_5f,_60,_61){
var _62=null;
var _63=_60.portletDecorationsConfig;
if(_5f&&_63){
_62=_63[_5f];
}
if(_62==null&&!_61){
var _64=_60.portletDecorationsAllowed;
for(var i=0;i<_64.length;i++){
_5f=_64[i];
_62=_63[_5f];
if(_62!=null){
break;
}
}
}
if(_62!=null&&!_62._initialized){
var _66=jetspeed.prefs.getPortletDecorationBaseUrl(_5f);
_62._initialized=true;
_62.cssPathCommon=new dojo.uri.Uri(_66+"/css/styles.css");
_62.cssPathDesktop=new dojo.uri.Uri(_66+"/css/desktop.css");
dojo.html.insertCssFile(_62.cssPathCommon,null,true);
dojo.html.insertCssFile(_62.cssPathDesktop,null,true);
}
return _62;
};
jetspeed.loadPortletDecorationConfig=function(_67,_68,_69){
var _6a={};
_68.portletDecorationsConfig[_67]=_6a;
_6a.name=_67;
_6a.windowActionButtonOrder=_68.windowActionButtonOrder;
_6a.windowActionNotPortlet=_68.windowActionNotPortlet;
_6a.windowActionButtonMax=_68.windowActionButtonMax;
_6a.windowActionButtonTooltip=_68.windowActionButtonTooltip;
_6a.windowActionMenuOrder=_68.windowActionMenuOrder;
_6a.windowActionNoImage=_68.windowActionNoImage;
_6a.windowIconEnabled=_68.windowIconEnabled;
_6a.windowIconPath=_68.windowIconPath;
_6a.windowTitlebar=_68.windowTitlebar;
_6a.windowResizebar=_68.windowResizebar;
_6a.dNodeClass=_69.P_CLASS+" "+_67+" "+_69.PWIN_CLASS+" "+_69.PWIN_CLASS+"-"+_67;
_6a.cNodeClass=_69.P_CLASS+" "+_67+" "+_69.PWIN_CLIENT_CLASS;
if(_68.portletDecorationsProperties){
var _6b=_68.portletDecorationsProperties[_67];
if(_6b){
for(var _6c in _6b){
_6a[_6c]=_6b[_6c];
}
if(_6b.windowActionNoImage!=null){
var _6d={};
for(var i=0;i<_6b.windowActionNoImage.length;i++){
_6d[_6b.windowActionNoImage[i]]=true;
}
_6a.windowActionNoImage=_6d;
}
if(_6b.windowIconPath!=null){
_6a.windowIconPath=dojo.string.trim(_6b.windowIconPath);
if(_6a.windowIconPath==null||_6a.windowIconPath.length==0){
_6a.windowIconPath=null;
}else{
var _6f=_6a.windowIconPath;
var _70=_6f.charAt(0);
if(_70!="/"){
_6f="/"+_6f;
}
var _71=_6f.charAt(_6f.length-1);
if(_71!="/"){
_6f=_6f+"/";
}
_6a.windowIconPath=_6f;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(_72,_73){
var _74=jetspeed;
_74.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _75=_74.page.getMenuNames();
for(var i=0;i<_75.length;i++){
var _77=_75[i];
var _78=dojo.widget.byId(_74.id.MENU_WIDGET_ID_PREFIX+_77);
if(_78){
_78.createJetspeedMenu(_74.page.getMenu(_77));
}
}
if(!_73){
_74.url.loadingIndicatorHide();
}
_74.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_79){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_79);
}
};
jetspeed.menuNavClickWidget=function(_7a,_7b){
if(!_7a){
return;
}
if(dojo.lang.isString(_7a)){
var _7c=_7a;
_7a=dojo.widget.byId(_7c);
if(!_7a){
dojo.raise("Tab widget not found: "+_7c);
}
}
if(_7a){
var _7d=_7a.jetspeedmenuname;
if(!_7d&&_7a.extraArgs){
_7d=_7a.extraArgs.jetspeedmenuname;
}
if(!_7d){
dojo.raise("Tab widget is invalid: "+_7a.widgetId);
}
var _7e=jetspeed.page.getMenu(_7d);
if(!_7e){
dojo.raise("Tab widget "+_7a.widgetId+" no menu: "+_7d);
}
var _7f=_7e.getOptionByIndex(_7b);
jetspeed.menuNavClick(_7f);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_80,_81,_82){
var _83=jetspeed;
if(!_80||_83.pageNavigateSuppress){
return;
}
if(typeof _82=="undefined"){
_82=false;
}
if(!_82&&_83.page&&_83.page.equalsPageUrl(_80)){
return;
}
_80=_83.page.makePageUrl(_80);
if(_81=="top"){
top.location.href=_80;
}else{
if(_81=="parent"){
parent.location.href=_80;
}else{
window.location.href=_80;
}
}
};
jetspeed.getActionsForPortlet=function(_84){
if(_84==null){
return;
}
jetspeed.getActionsForPortlets([_84]);
};
jetspeed.getActionsForPortlets=function(_85){
var _86=jetspeed;
if(_85==null){
_85=_86.page.getPortletIds();
}
var _87=new _86.om.PortletActionsCL(_85);
var _88="?action=getactions";
for(var i=0;i<_85.length;i++){
_88+="&id="+_85[i];
}
var _8a=_86.url.basePortalUrl()+_86.url.path.AJAX_API+_86.page.getPath()+_88;
var _8b="text/xml";
var _8c=new _86.om.Id("getactions",{});
_86.url.retrieveContent({url:_8a,mimetype:_8b},_87,_8c,_86.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_8d,_8e,_8f,_90,_91){
var _92=jetspeed;
if(_8d==null){
return;
}
if(_90==null){
_90=new _92.om.PortletChangeActionCL(_8d);
}
var _93="?action=window&id="+(_8d!=null?_8d:"");
if(_8e!=null){
_93+="&state="+_8e;
}
if(_8f!=null){
_93+="&mode="+_8f;
}
var _94=_91;
if(!_94){
_94=_92.page.getPath();
}
var _95=_92.url.basePortalUrl()+_92.url.path.AJAX_API+_94+_93;
var _96="text/xml";
var _97=new _92.om.Id("changeaction",{});
_92.url.retrieveContent({url:_95,mimetype:_96},_90,_97,_92.debugContentDumpIds);
};
jetspeed.getUserInfo=function(_98){
var _99=jetspeed;
var _9a=new _99.om.UserInfoCL();
var _9b="?action=getuserinfo";
var _9c=_99.url.basePortalUrl()+_99.url.path.AJAX_API+_99.page.getPath()+_9b;
var _9d="text/xml";
var _9e=new _99.om.Id("getuserinfo",{});
_99.url.retrieveContent({url:_9c,mimetype:_9d,sync:_98},_9a,_9e,_99.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_9f,_a0){
var _a1=_9f.page;
if(!_a1.editMode){
var _a2=_9f.css;
var _a3=true;
var _a4=_9f.url.getQueryParameter(window.location.href,_9f.id.PORTAL_ORIGINATE_PARAMETER);
if(_a4!=null&&_a4=="true"){
_a3=false;
}
_a1.editMode=true;
var _a5=dojo.widget.byId(_9f.id.PG_ED_WID);
if(_9f.UAie6){
_a1.displayAllPWins(true);
}
var _a6=((_a0!=null&&_a0.editModeMove)?true:false);
var _a7=_a1._perms(_9f.prefs,-1,String.fromCharCode);
if(_a7&&_a7[2]&&_a7[2].length>0){
if(!_9f.page._getU()){
_9f.getUserInfo(true);
}
}
if(_a5==null){
try{
_9f.url.loadingIndicatorShow("loadpageeditor",true);
_a5=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_9f.id.PG_ED_WID,editorInitiatedFromDesktop:_a3,editModeMove:_a6});
var _a8=document.getElementById(_9f.id.COLUMNS);
_a8.insertBefore(_a5.domNode,_a8.firstChild);
}
catch(e){
_9f.url.loadingIndicatorHide();
if(_9f.UAie6){
_a1.displayAllPWins();
}
}
}else{
_a5.editPageShow();
}
_a1.syncPageControls(_9f);
}
};
jetspeed.editPageTerminate=function(_a9,_aa){
var _ab=_a9.page;
if(_ab.editMode){
var _ac=null;
var _ad=_a9.css;
var _ae=dojo.widget.byId(_a9.id.PG_ED_WID);
if(_ae!=null&&!_ae.editorInitiatedFromDesktop){
var _af=_ab.getPageUrl(true);
_af=_a9.url.removeQueryParameter(_af,_a9.id.PG_ED_PARAM);
_af=_a9.url.removeQueryParameter(_af,_a9.id.PORTAL_ORIGINATE_PARAMETER);
_ac=_af;
}else{
var _b0=_a9.url.getQueryParameter(window.location.href,_a9.id.PG_ED_PARAM);
if(_b0!=null&&_b0=="true"){
var _b1=window.location.href;
_b1=_a9.url.removeQueryParameter(_b1,_a9.id.PG_ED_PARAM);
_ac=_b1;
}
}
if(_ac!=null){
_ac=_ac.toString();
}
_ab.editMode=false;
_a9.changeActionForPortlet(_ab.rootFragmentId,null,_a9.id.ACT_VIEW,new _a9.om.PageChangeActionCL(_ac));
if(_ac==null){
if(_ae!=null){
_ae.editMoveModeExit(true);
_ae.editPageHide();
}
_ab.syncPageControls(_a9);
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_b2,_b3,_b4,_b5){
if(!_b2){
_b2={};
}
jetspeed.url.retrieveContent(_b2,_b3,_b4,_b5);
}};
jetspeed.om.PageCLCreateWidget=function(_b6,_b7){
if(typeof _b6=="undefined"){
_b6=false;
}
this.isPageUpdate=_b6;
this.initEditModeConf=_b7;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_b8,_b9,_ba){
_ba.loadFromPSML(_b8,this.isPageUpdate,this.initEditModeConf);
},notifyFailure:function(_bb,_bc,_bd,_be){
dojo.raise("PageCLCreateWidget error url: "+_bd+" type: "+_bb+jetspeed.formatError(_bc));
}};
jetspeed.om.Page=function(_bf,_c0,_c1,_c2,_c3){
if(_bf!=null&&_c0!=null){
this.requiredLayoutDecorator=_bf;
this.setPsmlPathFromDocumentUrl(_c0);
this.pageUrlFallback=_c0;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _c1!="undefined"){
this.addToHistory=_c1;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets={};
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_c3!=null){
this.iframeCoverByWinId=_c3;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_c2!=null){
this.tooltipMgr=_c2;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,uIA:true,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _c4=(this.name!=null&&this.name.length>0?this.name:null);
if(!_c4){
this.getPsmlUrl();
_c4=this.psmlPath;
}
return "page-"+_c4;
},setPsmlPathFromDocumentUrl:function(_c5){
var _c6=jetspeed;
var _c7=_c6.url.path.AJAX_API;
var _c8=null;
if(_c5==null){
_c8=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_c6.prefs.ajaxPageNavigation){
var _c9=window.location.hash;
if(_c9!=null&&_c9.length>0){
if(_c9.indexOf("#")==0){
_c9=(_c9.length>1?_c9.substring(1):"");
}
if(_c9!=null&&_c9.length>1&&_c9.indexOf("/")==0){
this.psmlPath=_c6.url.path.AJAX_API+_c9;
return;
}
}
}
}else{
var _ca=_c6.url.parse(_c5);
_c8=_ca.path;
}
var _cb=_c6.url.path.DESKTOP;
var _cc=_c8.indexOf(_cb);
if(_cc!=-1&&_c8.length>(_cc+_cb.length)){
_c7=_c7+_c8.substring(_cc+_cb.length);
}
this.psmlPath=_c7;
},getPsmlUrl:function(){
var _cd=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _ce=_cd.url.basePortalUrl()+this.psmlPath;
if(_cd.prefs.printModeOnly!=null){
_ce=_cd.url.addQueryParameter(_ce,"layoutid",_cd.prefs.printModeOnly.layout);
_ce=_cd.url.addQueryParameter(_ce,"entity",_cd.prefs.printModeOnly.entity).toString();
}
return _ce;
},_setU:function(u){
this._u=u;
},_getU:function(){
return this._u;
},retrievePsml:function(_d0){
var _d1=jetspeed;
if(_d0==null){
_d0=new _d1.om.PageCLCreateWidget();
}
var _d2=this.getPsmlUrl();
var _d3="text/xml";
if(_d1.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_d2);
}
_d1.url.retrieveContent({url:_d2,mimetype:_d3},_d0,this,_d1.debugContentDumpIds);
},loadFromPSML:function(_d4,_d5,_d6){
var _d7=jetspeed;
var _d8=_d7.prefs;
var _d9=dojo;
var _da=_d8.printModeOnly;
if(djConfig.isDebug&&_d7.debug.profile&&_da==null){
_d9.profile.start("loadFromPSML");
}
var _db=this._parsePSML(_d4);
jetspeed.rootfrag=_db;
if(_db==null){
return;
}
this.portletsByPageColumn={};
var _dc={};
if(this.portletDecorator){
_dc[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_db,0,null,this.portletsByPageColumn,true,_dc,_d9,_d7);
this.rootFragmentId=_db.id;
this.editMode=false;
for(var _dd in _dc){
_d7.loadPortletDecorationStyles(_dd,_d8,true);
}
if(_d8.windowTiling){
this._createColsStart(document.getElementById(_d7.id.DESKTOP),_d7.id.COLUMNS);
}
this.createLayoutInfo(_d7);
var _de=this.portletsByPageColumn["z"];
if(_de){
_de.sort(this._loadPortletZIndexCompare);
}
if(typeof _d6=="undefined"){
_d6=null;
}
if(_d6!=null||(this.actions!=null&&this.actions[_d7.id.ACT_VIEW]!=null)){
if(!this.isUA()&&this.actions!=null&&(this.actions[_d7.id.ACT_EDIT]!=null||this.actions[_d7.id.ACT_VIEW]!=null)){
if(_d6==null){
_d6={};
}
if((typeof _d6.editModeMove=="undefined")&&this._perms(_d8,_d7.id.PM_MZ_P,String.fromCharCode)){
_d6.editModeMove=true;
}
var _df=_d7.url.parse(window.location.href);
if(!_d6.editModeMove){
var _e0=_d7.url.getQueryParameter(_df,_d7.id.PG_ED_STATE_PARAM);
if(_e0!=null){
_e0="0x"+_e0;
if((_e0&_d7.id.PM_MZ_P)>0){
_d6.editModeMove=true;
}
}
}
if(_d6.editModeMove&&!_d6.windowTitles){
var _e1=_d7.url.getQueryParameter(_df,_d7.id.PG_ED_TITLES_PARAM);
if(_e1!=null){
var _e2=_e1.length;
var _e3=new Array(_e2/2);
var _e4=String.fromCharCode;
var _e5=0,chI=0;
while(chI<(_e2-1)){
_e3[_e5]=_e4(Number("0x"+_e1.substring(chI,(chI+2))));
_e5++;
chI+=2;
}
var _e7=null;
try{
_e7=eval("({"+_e3.join("")+"})");
}
catch(e){
if(djConfig.isDebug){
dojo.debug("cannot parse json: "+_e3.join(""));
}
}
if(_e7!=null){
var _e8=false;
for(var _e9 in this.portlets){
var _ea=this.portlets[_e9];
if(_ea!=null&&!_e7[_ea.entityId]){
_e8=true;
break;
}
}
if(!_e8){
_d6.windowTitles=_e7;
}
}
}
}
}else{
_d6=null;
}
}
if(_d6!=null){
_d7.url.loadingIndicatorShow("loadpageeditor",true);
}
var _eb=new _d7.PortletRenderer(true,true,_d5,null,true,_d6);
_eb.renderAllTimeDistribute();
},loadPostRender:function(_ec,_ed){
var _ee=jetspeed;
var _ef=_ee.prefs.printModeOnly;
if(_ef==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
this.retrieveMenuDeclarations(true,_ec,_ed);
}else{
for(var _f0 in this.portlets){
var _f1=this.portlets[_f0];
if(_f1!=null){
_f1.renderAction(null,_ef.action);
}
break;
}
if(_ec){
_ee.updatePageEnd();
}
}
_ee.ui.evtConnect("after",window,"onresize",_ee.ui.windowResizeMgr,"onResize",dojo.event);
_ee.ui.windowResizeMgr.onResizeDelayedCompare();
var _f2,_f3=this.columns;
if(_f3){
for(var i=0;i<_f3.length;i++){
_f2=_f3[i].domNode;
if(!_f2.childNodes||_f2.childNodes.length==0){
_f2.style.height="1px";
}
}
}
var _f5=this.maximizedOnInit;
if(_f5!=null){
var _f6=this.getPWin(_f5);
if(_f6==null){
dojo.raise("no pWin to max");
}else{
dojo.lang.setTimeout(_f6,_f6._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_ee.url,_ee.url.loadingIndicatorStepPreload,1800);
},loadPostRetrieveMenus:function(_f7,_f8){
var _f9=jetspeed;
this.renderPageControls(_f9);
if(_f8){
_f9.editPageInitiate(_f9,_f8);
}
if(_f7){
_f9.updatePageEnd();
}
this.syncPageControls(_f9);
},_parsePSML:function(_fa){
var _fb=jetspeed;
var _fc=dojo;
var _fd=_fa.getElementsByTagName("page");
if(!_fd||_fd.length>1||_fd[0]==null){
_fc.raise("<page>");
}
var _fe=_fd[0];
var _ff=_fe.childNodes;
var _100=new RegExp("(name|path|profiledPath|title|short-title|uIA|npe)");
var _101=null;
var _102={};
for(var i=0;i<_ff.length;i++){
var _104=_ff[i];
if(_104.nodeType!=1){
continue;
}
var _105=_104.nodeName;
if(_105=="fragment"){
_101=_104;
}else{
if(_105=="defaults"){
this.layoutDecorator=_104.getAttribute("layout-decorator");
var _106=_104.getAttribute("portlet-decorator");
var _107=_fb.prefs.portletDecorationsAllowed;
if(!_107||_fc.lang.indexOf(_107,_106)==-1){
_106=_fb.prefs.windowDecoration;
}
this.portletDecorator=_106;
}else{
if(_105&&_105.match(_100)){
if(_105=="short-title"){
_105="shortTitle";
}
this[_105]=((_104&&_104.firstChild)?_104.firstChild.nodeValue:null);
}else{
if(_105=="action"){
this._parsePSMLAction(_104,_102);
}
}
}
}
}
this.actions=_102;
if(_101==null){
_fc.raise("root frag");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_fb.debug.ajaxPageNav){
_fc.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_fb.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _108=this.getPageUrl();
_fc.undo.browser.addToHistory({back:function(){
if(_fb.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_108);
}
_fb.updatePage(_108,true);
},forward:function(){
if(_fb.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_108);
}
_fb.updatePage(_108,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_fb.prefs.ajaxPageNavigation){
var _108=this.getPageUrl();
_fc.undo.browser.setInitialState({back:function(){
if(_fb.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_108);
}
_fb.updatePage(_108,true);
},forward:function(){
if(_fb.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_108);
}
_fb.updatePage(_108,true);
},changeUrl:escape(this.getPath())});
}
}
var _109=this._parsePSMLFrag(_101,0,false);
return _109;
},_parsePSMLFrag:function(_10a,_10b,_10c){
var _10d=jetspeed;
var _10e=new Array();
var _10f=((_10a!=null)?_10a.getAttribute("type"):null);
if(_10f!="layout"){
dojo.raise("!layout frag="+_10a);
return null;
}
if(!_10c){
var _110=_10a.getAttribute("name");
if(_110!=null){
_110=_110.toLowerCase();
if(_110.indexOf("noactions")!=-1){
_10c=true;
}
}
}
var _111=null,_112=0;
var _113={};
var _114=_10a.childNodes;
var _115,_116,_117,_118,_119;
for(var i=0;i<_114.length;i++){
_115=_114[i];
if(_115.nodeType!=1){
continue;
}
_116=_115.nodeName;
if(_116=="fragment"){
_119=_115.getAttribute("type");
if(_119=="layout"){
var _11b=this._parsePSMLFrag(_115,i,_10c);
if(_11b!=null){
_10e.push(_11b);
}
}else{
var _11c=this._parsePSMLProps(_115,null);
var _11d=_11c[_10d.id.PP_WINDOW_ICON];
if(_11d==null||_11d.length==0){
_11d=this._parsePSMLChildOrAttr(_115,"icon");
if(_11d!=null&&_11d.length>0){
_11c[_10d.id.PP_WINDOW_ICON]=_11d;
}
}
_10e.push({id:_115.getAttribute("id"),type:_119,name:_115.getAttribute("name"),properties:_11c,actions:this._parsePSMLActions(_115,null),currentActionState:this._parsePSMLChildOrAttr(_115,"state"),currentActionMode:this._parsePSMLChildOrAttr(_115,"mode"),decorator:_115.getAttribute("decorator"),layoutActionsDisabled:_10c,documentOrderIndex:i});
}
}else{
if(_116=="property"){
if(this._parsePSMLProp(_115,_113)=="sizes"){
if(_111!=null){
dojo.raise("<sizes>: "+_10a);
return null;
}
if(_10d.prefs.printModeOnly!=null){
_111=["100"];
_112=100;
}else{
_118=_115.getAttribute("value");
if(_118!=null&&_118.length>0){
_111=_118.split(",");
for(var j=0;j<_111.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_111[j]=_111[j].replace(re,"$1");
_112+=new Number(_111[j]);
}
}
}
}
}
}
}
if(_111==null){
_111=["100"];
_112=100;
}
var _120=_111.length;
var _121=_10e.length;
var pCi=_10d.id.PP_COLUMN;
var pRi=_10d.id.PP_ROW;
var _124=new Array(_120);
var _125=new Array(_120);
for(var cI=0;cI<_120;cI++){
_124[cI]=[];
_125[cI]={head:-1,tail:-1,high:-1};
}
for(var _127=0;_127<_121;_127++){
var frag=_10e[_127];
var _129=frag.properties;
var col=_129[pCi];
var row=_129[pRi];
var _12c=null;
if(col==null||col>=_120){
_12c=_120-1;
}else{
if(col<0){
_12c=0;
}
}
if(_12c!=null){
col=_129[pCi]=String(_12c);
}
var ll=_124[col];
var _12e=ll.length;
var _12f=_125[col];
if(row<0){
row=_129[pRi]=0;
}else{
if(row==null){
row=_12f.high+1;
}
}
var _130={i:_127,row:row,next:-1};
ll.push(_130);
if(_12e==0){
_12f.head=_12f.tail=0;
_12f.high=row;
}else{
if(row>_12f.high){
ll[_12f.tail].next=_12e;
_12f.high=row;
_12f.tail=_12e;
}else{
var _131=_12f.head;
var _132=-1;
while(ll[_131].row<row){
_132=_131;
_131=ll[_131].next;
}
if(ll[_131].row==row){
var _133=new Number(row)+1;
ll[_131].row=_133;
if(_12f.tail==_131){
_12f.high=_133;
}
}
_130.next=_131;
if(_132==-1){
_12f.head=_12e;
}else{
ll[_132].next=_12e;
}
}
}
}
var _134=new Array(_121);
var _135=0;
for(var cI=0;cI<_120;cI++){
var ll=_124[cI];
var _12f=_125[cI];
var _136=0;
var _137=_12f.head;
while(_137!=-1){
var _130=ll[_137];
var frag=_10e[_130.i];
_134[_135]=frag;
frag.properties[pRi]=_136;
_135++;
_136++;
_137=_130.next;
}
}
return {id:_10a.getAttribute("id"),type:_10f,name:_10a.getAttribute("name"),decorator:_10a.getAttribute("decorator"),columnSizes:_111,columnSizesSum:_112,properties:_113,fragments:_134,layoutActionsDisabled:_10c,documentOrderIndex:_10b};
},_parsePSMLActions:function(_138,_139){
if(_139==null){
_139={};
}
var _13a=_138.getElementsByTagName("action");
for(var _13b=0;_13b<_13a.length;_13b++){
var _13c=_13a[_13b];
this._parsePSMLAction(_13c,_139);
}
return _139;
},_parsePSMLAction:function(_13d,_13e){
var _13f=_13d.getAttribute("id");
if(_13f!=null){
var _140=_13d.getAttribute("type");
var _141=_13d.getAttribute("name");
var _142=_13d.getAttribute("url");
var _143=_13d.getAttribute("alt");
_13e[_13f.toLowerCase()]={id:_13f,type:_140,label:_141,url:_142,alt:_143};
}
},_parsePSMLChildOrAttr:function(_144,_145){
var _146=null;
var _147=_144.getElementsByTagName(_145);
if(_147!=null&&_147.length==1&&_147[0].firstChild!=null){
_146=_147[0].firstChild.nodeValue;
}
if(!_146){
_146=_144.getAttribute(_145);
}
if(_146==null||_146.length==0){
_146=null;
}
return _146;
},_parsePSMLProps:function(_148,_149){
if(_149==null){
_149={};
}
var _14a=_148.getElementsByTagName("property");
for(var _14b=0;_14b<_14a.length;_14b++){
this._parsePSMLProp(_14a[_14b],_149);
}
return _149;
},_parsePSMLProp:function(_14c,_14d){
var _14e=_14c.getAttribute("name");
var _14f=_14c.getAttribute("value");
_14d[_14e]=_14f;
return _14e;
},_layoutCreateModel:function(_150,_151,_152,_153,_154,_155,_156,_157){
var jsId=_157.id;
var _159=this.columns.length;
var _15a=this._layoutCreateColsModel(_150,_151,_152,_154);
var _15b=_15a.columnsInLayout;
if(_15a.addedLayoutHeaderColumn){
_159++;
}
var _15c=(_15b==null?0:_15b.length);
var _15d=new Array(_15c);
var _15e=new Array(_15c);
for(var i=0;i<_150.fragments.length;i++){
var _160=_150.fragments[i];
if(_160.type=="layout"){
var _161=i;
var _161=(_160.properties?_160.properties[_157.id.PP_COLUMN]:i);
if(_161==null||_161<0||_161>=_15c){
_161=(_15c>0?(_15c-1):0);
}
_15e[_161]=true;
this._layoutCreateModel(_160,(_151+1),_15b[_161],_153,false,_155,_156,_157);
}else{
this._layoutCreatePortlet(_160,_150,_15b,_159,_153,_15d,_155,_156,_157);
}
}
return _15b;
},_layoutCreatePortlet:function(_162,_163,_164,_165,_166,_167,_168,_169,_16a){
if(_162&&_16a.debugPortletEntityIdFilter){
if(!_169.lang.inArray(_16a.debugPortletEntityIdFilter,_162.id)){
_162=null;
}
}
if(_162){
var _16b="z";
var _16c=_162.properties[_16a.id.PP_DESKTOP_EXTENDED];
var _16d=_16a.prefs.windowTiling;
var _16e=_16d;
var _16f=_16a.prefs.windowHeightExpand;
if(_16c!=null&&_16d&&_16a.prefs.printModeOnly==null){
var _170=_16c.split(_16a.id.PP_PAIR_SEPARATOR);
var _171=null,_172=0,_173=null,_174=null,_175=false;
if(_170!=null&&_170.length>0){
var _176=_16a.id.PP_PROP_SEPARATOR;
for(var _177=0;_177<_170.length;_177++){
_171=_170[_177];
_172=((_171!=null)?_171.length:0);
if(_172>0){
var _178=_171.indexOf(_176);
if(_178>0&&_178<(_172-1)){
_173=_171.substring(0,_178);
_174=_171.substring(_178+1);
_175=((_174=="true")?true:false);
if(_173==_16a.id.PP_STATICPOS){
_16e=_175;
}else{
if(_173==_16a.id.PP_FITHEIGHT){
_16f=_175;
}
}
}
}
}
}
}else{
if(!_16d){
_16e=false;
}
}
_162.properties[_16a.id.PP_WINDOW_POSITION_STATIC]=_16e;
_162.properties[_16a.id.PP_WINDOW_HEIGHT_TO_FIT]=_16f;
if(_16e&&_16d){
var _179=_164.length;
var _17a=_162.properties[_16a.id.PP_COLUMN];
if(_17a==null||_17a>=_179){
_17a=_179-1;
}else{
if(_17a<0){
_17a=0;
}
}
if(_167[_17a]==null){
_167[_17a]=new Array();
}
_167[_17a].push(_162.id);
var _17b=_165+new Number(_17a);
_16b=_17b.toString();
}
if(_162.currentActionState==_16a.id.ACT_MAXIMIZE){
this.maximizedOnInit=_162.id;
}
var _17c=_162.decorator;
if(_17c!=null&&_17c.length>0){
if(_169.lang.indexOf(_16a.prefs.portletDecorationsAllowed,_17c)==-1){
_17c=null;
}
}
if(_17c==null||_17c.length==0){
if(djConfig.isDebug&&_16a.debug.windowDecorationRandom){
_17c=_16a.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_16a.prefs.portletDecorationsAllowed.length)];
}else{
_17c=this.portletDecorator;
}
}
var _17d=_162.properties||{};
_17d[_16a.id.PP_WINDOW_DECORATION]=_17c;
_168[_17c]=true;
var _17e=_162.actions||{};
var _17f=new _16a.om.Portlet(_162.name,_162.id,null,_17d,_17e,_162.currentActionState,_162.currentActionMode,_162.layoutActionsDisabled);
_17f.initialize();
this.putPortlet(_17f);
if(_166[_16b]==null){
_166[_16b]=new Array();
}
_166[_16b].push({portlet:_17f,layout:_163.id});
}
},_layoutCreateColsModel:function(_180,_181,_182,_183){
var _184=jetspeed;
this.layouts[_180.id]=_180;
var _185=false;
var _186=new Array();
if(_184.prefs.windowTiling&&_180.columnSizes.length>0){
var _187=false;
if(_184.UAie){
_187=true;
}
if(_182!=null&&!_183){
var _188=new _184.om.Column(0,_180.id,(_187?_180.columnSizesSum-0.1:_180.columnSizesSum),this.columns.length,_180.layoutActionsDisabled,_181);
_188.layoutHeader=true;
this.columns.push(_188);
if(_182.buildColChildren==null){
_182.buildColChildren=new Array();
}
_182.buildColChildren.push(_188);
_182=_188;
_185=true;
}
for(var i=0;i<_180.columnSizes.length;i++){
var size=_180.columnSizes[i];
if(_187&&i==(_180.columnSizes.length-1)){
size=size-0.1;
}
var _18b=new _184.om.Column(i,_180.id,size,this.columns.length,_180.layoutActionsDisabled);
this.columns.push(_18b);
if(_182!=null){
if(_182.buildColChildren==null){
_182.buildColChildren=new Array();
}
_182.buildColChildren.push(_18b);
}
_186.push(_18b);
}
}
return {columnsInLayout:_186,addedLayoutHeaderColumn:_185};
},_portletsInitWinState:function(_18c){
var _18d={};
this.getPortletCurColRow(null,false,_18d);
for(var _18e in this.portlets){
var _18f=this.portlets[_18e];
var _190=_18d[_18f.getId()];
if(_190==null&&_18c){
for(var i=0;i<_18c.length;i++){
if(_18c[i].portlet.getId()==_18f.getId()){
_190={layout:_18c[i].layout};
break;
}
}
}
if(_190!=null){
_18f._initWinState(_190,false);
}else{
dojo.raise("Window state data not found for portlet: "+_18f.getId());
}
}
},_loadPortletZIndexCompare:function(_192,_193){
var _194=null;
var _195=null;
var _196=null;
_194=_192.portlet._getInitialZIndex();
_195=_193.portlet._getInitialZIndex();
if(_194&&!_195){
return -1;
}else{
if(_195&&!_194){
return 1;
}else{
if(_194==_195){
return 0;
}
}
}
return (_194-_195);
},_createColsStart:function(_197,_198){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _199=document.createElement("div");
_199.id=_198;
_199.setAttribute("id",_198);
for(var _19a=0;_19a<this.columnsStructure.length;_19a++){
var _19b=this.columnsStructure[_19a];
this._createCols(_19b,_199);
}
_197.appendChild(_199);
},_createCols:function(_19c,_19d){
_19c.createColumn();
if(this.colFirstNormI==-1&&!_19c.columnContainer&&!_19c.layoutHeader){
this.colFirstNormI=_19c.getPageColumnIndex();
}
var _19e=_19c.buildColChildren;
if(_19e!=null&&_19e.length>0){
for(var _19f=0;_19f<_19e.length;_19f++){
this._createCols(_19e[_19f],_19c.domNode);
}
}
delete _19c.buildColChildren;
_19d.appendChild(_19c.domNode);
},_removeCols:function(_1a0){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_1a0){
var _1a2=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_1a2,function(_1a3){
_1a0.appendChild(_1a3);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _1a4=dojo.byId(jetspeed.id.COLUMNS);
if(_1a4){
dojo.dom.removeNode(_1a4);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnsEmptyCheck:function(_1a5){
var _1a6=null;
if(_1a5==null){
return _1a6;
}
var _1a7=_1a5.childNodes,_1a8;
if(_1a7){
for(var i=0;i<_1a7.length;i++){
_1a8=_1a7[i];
var _1aa=this.columnEmptyCheck(_1a8,true);
if(_1aa!=null){
_1a6=_1aa;
if(_1a6==false){
break;
}
}
}
}
return _1a6;
},columnEmptyCheck:function(_1ab,_1ac){
var _1ad=null;
if(!_1ab||!_1ab.getAttribute){
return _1ad;
}
var _1ae=_1ab.getAttribute("columnindex");
if(!_1ae||_1ae.length==0){
return _1ad;
}
var _1af=_1ab.getAttribute("layoutid");
if(_1af==null||_1af.length==0){
var _1b0=_1ab.childNodes;
_1ad=(!_1b0||_1b0.length==0);
if(!_1ac){
_1ab.style.height=(_1ad?"1px":"");
}
}
return _1ad;
},getPortletCurColRow:function(_1b1,_1b2,_1b3){
if(!this.columns||this.columns.length==0){
return null;
}
var _1b4=null;
var _1b5=((_1b1!=null)?true:false);
var _1b6=0;
var _1b7=null;
var _1b8=null;
var _1b9=0;
var _1ba=false;
for(var _1bb=0;_1bb<this.columns.length;_1bb++){
var _1bc=this.columns[_1bb];
var _1bd=_1bc.domNode.childNodes;
if(_1b8==null||_1b8!=_1bc.getLayoutId()){
_1b8=_1bc.getLayoutId();
_1b7=this.layouts[_1b8];
if(_1b7==null){
dojo.raise("Layout not found: "+_1b8);
return null;
}
_1b9=0;
_1ba=false;
if(_1b7.clonedFromRootId==null){
_1ba=true;
}else{
var _1be=this.getColFromColNode(_1bc.domNode.parentNode);
if(_1be==null){
dojo.raise("Parent column not found: "+_1bc);
return null;
}
_1bc=_1be;
}
}
var _1bf=null;
var _1c0=jetspeed;
var _1c1=dojo;
var _1c2=_1c0.id.PWIN_CLASS;
if(_1b2){
_1c2+="|"+_1c0.id.PWIN_GHOST_CLASS;
}
if(_1b5){
_1c2+="|"+_1c0.id.COL_CLASS;
}
var _1c3=new RegExp("(^|\\s+)("+_1c2+")(\\s+|$)");
for(var _1c4=0;_1c4<_1bd.length;_1c4++){
var _1c5=_1bd[_1c4];
if(_1c3.test(_1c1.html.getClass(_1c5))){
_1bf=(_1bf==null?0:_1bf+1);
if((_1bf+1)>_1b9){
_1b9=(_1bf+1);
}
if(_1b1==null||_1c5==_1b1){
var _1c6={layout:_1b8,column:_1bc.getLayoutColumnIndex(),row:_1bf,columnObj:_1bc};
if(!_1ba){
_1c6.layout=_1b7.clonedFromRootId;
}
if(_1b1!=null){
_1b4=_1c6;
break;
}else{
if(_1b3!=null){
var _1c7=this.getPWinFromNode(_1c5);
if(_1c7==null){
_1c1.raise("PortletWindow not found for node");
}else{
var _1c8=_1c7.portlet;
if(_1c8==null){
_1c1.raise("PortletWindow for node has null portlet: "+_1c7.widgetId);
}else{
_1b3[_1c8.getId()]=_1c6;
}
}
}
}
}
}
}
if(_1b4!=null){
break;
}
}
return _1b4;
},_getPortletArrayByZIndex:function(){
var _1c9=jetspeed;
var _1ca=this.getPortletArray();
if(!_1ca){
return _1ca;
}
var _1cb=[];
for(var i=0;i<_1ca.length;i++){
if(!_1ca[i].getProperty(_1c9.id.PP_WINDOW_POSITION_STATIC)){
_1cb.push(_1ca[i]);
}
}
_1cb.sort(this._portletZIndexCompare);
return _1cb;
},_portletZIndexCompare:function(_1cd,_1ce){
var _1cf=null;
var _1d0=null;
var _1d1=null;
_1d1=_1cd.getSavedWinState();
_1cf=_1d1.zIndex;
_1d1=_1ce.getSavedWinState();
_1d0=_1d1.zIndex;
if(_1cf&&!_1d0){
return -1;
}else{
if(_1d0&&!_1cf){
return 1;
}else{
if(_1cf==_1d0){
return 0;
}
}
}
return (_1cf-_1d0);
},_perms:function(p,w,f){
var rId=f(112);
var rL=1;
rId+=f(101);
var c=null,a=null;
rId+=f(99);
var r=p[rId];
d=10;
rL=((!r||!r.length)?0:((w<0)?r.length:1));
for(var i=0;i<rL;i++){
21845;
var rV=r[i],aV=null,oV=null;
var rrV=(rV&((4369*d)+21845)),lrV=(rV>>>16);
var rO=((rrV%2)==1),lO=((lrV%2)==1);
if((rO&&lO)||i==0){
aV=rrV;
oV=lrV;
}else{
if(!rO&&lO){
aV=lrV;
oV=rrV;
}
}
if(aV!=null&&oV!=null){
var oVT=Math.floor(oV/d),oVTE=(((oVT%2)==1)?Math.max(oVT-1,2):oVT);
aV=aV-oVTE;
if(i>0){
aV=(aV>>>4);
}
if(i==0){
c=aV;
}else{
a=(a==null?"":a)+f(aV);
}
}
}
return (w>0?((c&w)>0):[c,(c&15),a]);
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _1e4=[];
for(var _1e5 in this.portlets){
var _1e6=this.portlets[_1e5];
_1e4.push(_1e6);
}
return _1e4;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _1e7=[];
for(var _1e8 in this.portlets){
var _1e9=this.portlets[_1e8];
_1e7.push(_1e9.getId());
}
return _1e7;
},getPortletByName:function(_1ea){
if(this.portlets&&_1ea){
for(var _1eb in this.portlets){
var _1ec=this.portlets[_1eb];
if(_1ec.name==_1ea){
return _1ec;
}
}
}
return null;
},getPortlet:function(_1ed){
if(this.portlets&&_1ed){
return this.portlets[_1ed];
}
return null;
},getPWinFromNode:function(_1ee){
var _1ef=null;
if(this.portlets&&_1ee){
for(var _1f0 in this.portlets){
var _1f1=this.portlets[_1f0];
var _1f2=_1f1.getPWin();
if(_1f2!=null){
if(_1f2.domNode==_1ee){
_1ef=_1f2;
break;
}
}
}
}
return _1ef;
},putPortlet:function(_1f3){
if(!_1f3){
return;
}
if(!this.portlets){
this.portlets={};
}
this.portlets[_1f3.entityId]=_1f3;
this.portlet_count++;
},putPWin:function(_1f4){
if(!_1f4){
return;
}
var _1f5=_1f4.widgetId;
if(!_1f5){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_1f5]=_1f4;
this.portlet_window_count++;
},getPWin:function(_1f6){
if(this.portlet_windows&&_1f6){
var pWin=this.portlet_windows[_1f6];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_1f6];
if(pWin==null){
var p=this.getPortlet(_1f6);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_1fa){
var _1fb=this.portlet_windows;
var pWin;
var _1fd=[];
for(var _1fe in _1fb){
pWin=_1fb[_1fe];
if(pWin&&(!_1fa||pWin.portlet)){
_1fd.push(pWin);
}
}
return _1fd;
},getPWinTopZIndex:function(_1ff){
var _200=0;
if(_1ff){
_200=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_200;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_200=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_200;
}
return _200;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_201,_202){
return;
},onBrowserWindowResize:function(){
var _203=jetspeed;
var _204=this.portlet_windows;
var pWin;
for(var _206 in _204){
pWin=_204[_206];
pWin.onBrowserWindowResize();
}
if(_203.UAie6&&this.editMode){
var _207=dojo.widget.byId(_203.id.PG_ED_WID);
if(_207!=null){
_207.onBrowserWindowResize();
}
}
},regPWinIFrameCover:function(_208){
if(!_208){
return;
}
this.iframeCoverByWinId[_208.widgetId]=true;
},unregPWinIFrameCover:function(_209){
if(!_209){
return;
}
delete this.iframeCoverByWinId[_209.widgetId];
},displayAllPWinIFrameCovers:function(_20a,_20b){
var _20c=this.portlet_windows;
var _20d=this.iframeCoverByWinId;
if(!_20c||!_20d){
return;
}
for(var _20e in _20d){
if(_20e==_20b){
continue;
}
var pWin=_20c[_20e];
var _210=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_210){
_210.style.display=(_20a?"none":"block");
}
}
},createLayoutInfo:function(_211){
var _212=dojo;
var _213=null;
var _214=null;
var _215=null;
var _216=null;
var _217=document.getElementById(_211.id.DESKTOP);
if(_217!=null){
_213=_211.ui.getLayoutExtents(_217,null,_212,_211);
}
var _218=document.getElementById(_211.id.COLUMNS);
if(_218!=null){
_214=_211.ui.getLayoutExtents(_218,null,_212,_211);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_216=_211.ui.getLayoutExtents(col.domNode,null,_212,_211);
}else{
if(!col.columnContainer){
_215=_211.ui.getLayoutExtents(col.domNode,null,_212,_211);
}
}
if(_215!=null&&_216!=null){
break;
}
}
}
this.layoutInfo={desktop:(_213!=null?_213:{}),columns:(_214!=null?_214:{}),column:(_215!=null?_215:{}),columnLayoutHeader:(_216!=null?_216:{})};
_211.widget.PortletWindow.prototype.colWidth_pbE=((_215&&_215.pbE)?_215.pbE.w:0);
},_beforeAddOnLoad:function(){
this.win_onload=true;
},destroy:function(){
var _21b=jetspeed;
var _21c=dojo;
_21b.ui.evtDisconnect("after",window,"onresize",_21b.ui.windowResizeMgr,"onResize",_21c.event);
_21b.ui.evtDisconnect("before",_21c,"addOnLoad",this,"_beforeAddOnLoad",_21c.event);
var _21d=this.portlet_windows;
var _21e=this.getPWins(true);
var pWin,_220;
for(var i=0;i<_21e.length;i++){
pWin=_21e[i];
_220=pWin.widgetId;
pWin.closeWindow();
delete _21d[_220];
this.portlet_window_count--;
}
this.portlets={};
this.portlet_count=0;
var _222=_21c.widget.byId(_21b.id.PG_ED_WID);
if(_222!=null){
_222.editPageDestroy();
}
this._removeCols(document.getElementById(_21b.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_223){
if(_223==null){
return null;
}
var _224=_223.getAttribute("columnindex");
if(_224==null){
return null;
}
var _225=new Number(_224);
if(_225>=0&&_225<this.columns.length){
return this.columns[_225];
}
return null;
},getColIndexForNode:function(node){
var _227=null;
if(!this.columns){
return _227;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_227=i;
break;
}
}
return _227;
},getColWithNode:function(node){
var _22a=this.getColIndexForNode(node);
return ((_22a!=null&&_22a>=0)?this.columns[_22a]:null);
},getDescendantCols:function(_22b){
var dMap={};
if(_22b==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_22b&&_22b.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_22f){
if(!_22f){
return;
}
var _230=(_22f.getName?_22f.getName():null);
if(_230!=null){
this.menus[_230]=_22f;
}
},getMenu:function(_231){
if(_231==null){
return null;
}
return this.menus[_231];
},removeMenu:function(_232){
if(_232==null){
return;
}
var _233=null;
if(dojo.lang.isString(_232)){
_233=_232;
}else{
_233=(_232.getName?_232.getName():null);
}
if(_233!=null){
delete this.menus[_233];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _234=[];
for(var _235 in this.menus){
_234.push(_235);
}
return _234;
},retrieveMenuDeclarations:function(_236,_237,_238){
contentListener=new jetspeed.om.MenusApiCL(_236,_237,_238);
this.clearMenus();
var _239="?action=getmenus";
if(_236){
_239+="&includeMenuDefs=true";
}
var _23a=this.getPsmlUrl()+_239;
var _23b="text/xml";
var _23c=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_23a,mimetype:_23b},contentListener,_23c,jetspeed.debugContentDumpIds);
},syncPageControls:function(_23d){
var jsId=_23d.id;
if(this.actionButtons==null){
return;
}
for(var _23f in this.actionButtons){
var _240=false;
if(_23f==jsId.ACT_EDIT){
if(!this.editMode){
_240=true;
}
}else{
if(_23f==jsId.ACT_VIEW){
if(this.editMode){
_240=true;
}
}else{
if(_23f==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_240=true;
}
}else{
_240=true;
}
}
}
if(_240){
this.actionButtons[_23f].style.display="";
}else{
this.actionButtons[_23f].style.display="none";
}
}
},renderPageControls:function(_241){
var _241=jetspeed;
var _242=_241.page;
var jsId=_241.id;
var _244=dojo;
var _245=[];
if(this.actions!=null){
var addP=false;
for(var _247 in this.actions){
if(_247!=jsId.ACT_HELP){
_245.push(_247);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
addP=true;
if(this.actions[jsId.ACT_VIEW]==null){
_245.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
addP=true;
if(this.actions[jsId.ACT_EDIT]==null){
_245.push(jsId.ACT_EDIT);
}
}
var _248=(_242.rootFragmentId?_242.layouts[_242.rootFragmentId]:null);
var _249=(!(_248==null||_248.layoutActionsDisabled));
if(_249){
_249=_242._perms(_241.prefs,_241.id.PM_P_AD,String.fromCharCode);
if(_249&&!this.isUA()&&(addP||_242.canNPE())){
_245.push(jsId.ACT_ADDPORTLET);
}
}
}
var _24a=_244.byId(jsId.PAGE_CONTROLS);
if(_24a!=null&&_245!=null&&_245.length>0){
var _24b=_241.prefs;
var jsUI=_241.ui;
var _24d=_244.event;
var _24e=_242.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _24f=this.actionButtonTooltips;
for(var i=0;i<_245.length;i++){
var _247=_245[i];
var _251=document.createElement("div");
_251.className="portalPageActionButton";
_251.style.backgroundImage="url("+_24b.getLayoutRootUrl()+"/images/desktop/"+_247+".gif)";
_251.actionName=_247;
this.actionButtons[_247]=_251;
_24a.appendChild(_251);
jsUI.evtConnect("after",_251,"onclick",this,"pageActionButtonClick",_24d);
if(_24b.pageActionButtonTooltip){
var _252=null;
if(_24b.desktopActionLabels!=null){
_252=_24b.desktopActionLabels[_247];
}
if(_252==null||_252.length==0){
_252=_244.string.capitalize(_247);
}
_24f.push(_24e.addNode(_251,_252,true,null,null,null,_241,jsUI,_24d));
}
}
}
},_destroyPageControls:function(){
var _253=jetspeed;
if(this.actionButtons){
for(var _254 in this.actionButtons){
var _255=this.actionButtons[_254];
if(_255){
_253.ui.evtDisconnect("after",_255,"onclick",this,"pageActionButtonClick");
}
}
}
var _256=dojo.byId(_253.id.PAGE_CONTROLS);
if(_256!=null&&_256.childNodes&&_256.childNodes.length>0){
for(var i=(_256.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_256.childNodes[i]);
}
}
_253.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_259){
var _25a=jetspeed;
if(_259==null){
return;
}
if(_259==_25a.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_259==_25a.id.ACT_EDIT){
_25a.changeActionForPortlet(this.rootFragmentId,null,_25a.id.ACT_EDIT,new _25a.om.PageChangeActionCL());
_25a.editPageInitiate(_25a);
}else{
if(_259==_25a.id.ACT_VIEW){
_25a.editPageTerminate(_25a);
}else{
var _25b=this.getPageAction(_259);
if(_25b==null){
return;
}
if(_25b.url==null){
return;
}
var _25c=_25a.url.basePortalUrl()+_25a.url.path.DESKTOP+"/"+_25b.url;
_25a.pageNavigate(_25c);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_25e,_25f){
var _260=jetspeed;
var jsId=_260.id;
if(!_25f){
_25f=escape(this.getPagePathAndQuery());
}else{
_25f=escape(_25f);
}
var _262=_260.url.basePortalUrl()+_260.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_25f;
if(_25e!=null){
_262+="&jslayoutid="+escape(_25e);
}
if(!this.editMode){
_262+="&"+_260.id.ADDP_RFRAG+"="+escape(this.rootFragmentId);
}
if(this.actions&&(this.actions[jsId.ACT_EDIT]||this.actions[jsId.ACT_VIEW])){
_260.changeActionForPortlet(this.rootFragmentId,null,jsId.ACT_EDIT,new _260.om.PageChangeActionCL(_262));
}else{
if(!this.isUA()){
_260.pageNavigate(_262);
}
}
},addPortletTerminate:function(_263,_264){
var _265=jetspeed;
var _266=_265.url.getQueryParameter(document.location.href,_265.id.ADDP_RFRAG);
if(_266!=null&&_266.length>0){
var _267=_264;
var qPos=_264.indexOf("?");
if(qPos>0){
_267.substring(0,qPos);
}
_265.changeActionForPortlet(_266,null,_265.id.ACT_VIEW,new _265.om.PageChangeActionCL(_263),_267);
}else{
_265.pageNavigate(_263);
}
},setPageModePortletActions:function(_269){
if(_269==null||_269.actions==null){
return;
}
var jsId=jetspeed.id;
if(_269.actions[jsId.ACT_REMOVEPORTLET]==null){
_269.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_26b){
if(this.pageUrl!=null&&!_26b){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _26d=jsU.path.SERVER+((_26b)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _26e=jsU.parse(_26d);
var _26f=null;
if(this.pageUrlFallback!=null){
_26f=jsU.parse(this.pageUrlFallback);
}else{
_26f=jsU.parse(window.location.href);
}
if(_26e!=null&&_26f!=null){
var _270=_26f.query;
if(_270!=null&&_270.length>0){
var _271=_26e.query;
if(_271!=null&&_271.length>0){
_26d=_26d+"&"+_270;
}else{
_26d=_26d+"?"+_270;
}
}
}
if(!_26b){
this.pageUrl=_26d;
}
return _26d;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _273=this.getPath();
var _274=jsU.parse(_273);
var _275=null;
if(this.pageUrlFallback!=null){
_275=jsU.parse(this.pageUrlFallback);
}else{
_275=jsU.parse(window.location.href);
}
if(_274!=null&&_275!=null){
var _276=_275.query;
if(_276!=null&&_276.length>0){
var _277=_274.query;
if(_277!=null&&_277.length>0){
_273=_273+"&"+_276;
}else{
_273=_273+"?"+_276;
}
}
}
this.pagePathAndQuery=_273;
return _273;
},getPageDirectory:function(_278){
var _279="/";
var _27a=(_278?this.getRealPath():this.getPath());
if(_27a!=null){
var _27b=_27a.lastIndexOf("/");
if(_27b!=-1){
if((_27b+1)<_27a.length){
_279=_27a.substring(0,_27b+1);
}else{
_279=_27a;
}
}
}
return _279;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_27d){
if(!_27d){
_27d="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_27d)){
return jsU.path.SERVER+jsU.path.DESKTOP+_27d;
}
return _27d;
},getName:function(){
return this.name;
},getPath:function(){
return this.profiledPath;
},getRealPath:function(){
return this.path;
},getTitle:function(){
return this.title;
},getShortTitle:function(){
return this.shortTitle;
},getLayoutDecorator:function(){
return this.layoutDecorator;
},getPortletDecorator:function(){
return this.portletDecorator;
},isUA:function(){
return ((typeof this.uIA=="undefined")?true:(this.uIA=="false"?false:true));
},canNPE:function(){
return ((typeof this.npe=="undefined")?false:(this.npe=="true"?true:false));
}});
jetspeed.om.Column=function(_27f,_280,size,_282,_283,_284){
this.layoutColumnIndex=_27f;
this.layoutId=_280;
this.size=size;
this.pageColumnIndex=new Number(_282);
if(typeof _283!="undefined"){
this.layoutActionsDisabled=_283;
}
if((typeof _284!="undefined")&&_284!=null){
this.layoutDepth=_284;
}
this.id="jscol_"+_282;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,layoutDepth:null,layoutMaxChildDepth:0,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_285){
var _286=this.styleClass;
var _287=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_287>0){
_286+=" desktopColumnClear-PRIVATE";
}
var _288=document.createElement("div");
_288.setAttribute("columnindex",_287);
_288.style.width=this.size+"%";
if(this.layoutHeader){
_286=this.styleLayoutClass;
_288.setAttribute("layoutid",this.layoutId);
}
_288.className=_286;
_288.id=this.getId();
this.domNode=_288;
if(_285!=null){
_285.appendChild(_288);
}
},containsNode:function(node){
return ((this.domNode!=null&&node!=null&&this.domNode==node.parentNode)?true:false);
},containsDescendantNode:function(node){
return ((this.domNode!=null&&node!=null&&dojo.dom.isDescendantOf(node,this.domNode,true))?true:false);
},getDescendantCols:function(){
return jetspeed.page.getDescendantCols(this);
},isStartOfColumnSet:function(){
return this.layoutColumnIndex==0;
},toString:function(){
if(jetspeed.debugColumn){
return jetspeed.debugColumn(this);
}
return "column["+this.pageColumnIndex+"]";
},getId:function(){
return this.id;
},getLayoutId:function(){
return this.layoutId;
},getLayoutColumnIndex:function(){
return this.layoutColumnIndex;
},getSize:function(){
return this.size;
},getPageColumnIndex:function(){
return this.pageColumnIndex;
},getLayoutDepth:function(){
return this.layoutDepth;
},getLayoutMaxChildDepth:function(){
return this.layoutMaxChildDepth;
},layoutDepthChanged:function(){
},_updateLayoutDepth:function(_28b){
var _28c=this.layoutDepth;
if(_28c!=null&&_28b!=_28c){
this.layoutDepth=_28b;
this.layoutDepthChanged();
}
},_updateLayoutChildDepth:function(_28d){
this.layoutMaxChildDepth=(_28d==null?0:_28d);
}});
jetspeed.om.Portlet=function(_28e,_28f,_290,_291,_292,_293,_294,_295){
this.name=_28e;
this.entityId=_28f;
this.properties=_291;
this.actions=_292;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_293;
this.currentActionMode=_294;
if(_290){
this.contentRetriever=_290;
}
this.layoutActionsDisabled=false;
if(typeof _295!="undefined"){
this.layoutActionsDisabled=_295;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _296=jetspeed;
var jsId=_296.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _298=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_296.prefs.windowTiling){
if(_298=="true"){
_298=true;
}else{
if(_298=="false"){
_298=false;
}else{
if(_298!=true&&_298!=false){
_298=true;
}
}
}
}else{
_298=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_298;
var _299=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_299=="true"){
_299=true;
}else{
if(_298=="false"){
_299=false;
}else{
if(_299!=true&&_299!=false){
_299=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_299;
var _29a=this.properties[jsId.PP_WINDOW_TITLE];
if(!_29a&&this.name){
var re=(/^[^:]*:*/);
_29a=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_29a;
}
},postParseAnnotateHtml:function(_29c){
var _29d=jetspeed;
var _29e=_29d.portleturl;
if(_29c){
var _29f=_29c;
var _2a0=_29f.getElementsByTagName("form");
var _2a1=_29d.debug.postParseAnnotateHtml;
var _2a2=_29d.debug.postParseAnnotateHtmlDisableAnchors;
if(_2a0){
for(var i=0;i<_2a0.length;i++){
var _2a4=_2a0[i];
var _2a5=_2a4.action;
var _2a6=_29e.parseContentUrl(_2a5);
var op=_2a6.operation;
var _2a8=(op==_29e.PORTLET_REQUEST_ACTION||op==_29e.PORTLET_REQUEST_RENDER);
var _2a9=false;
if(dojo.io.formHasFile(_2a4)){
if(_2a8){
var _2aa=_29d.url.parse(_2a5);
_2aa=_29d.url.addQueryParameter(_2aa,"encoder","desktop",true);
_2aa=_29d.url.addQueryParameter(_2aa,"jsdajax","false",true);
_2a4.action=_2aa.toString();
}else{
_2a9=true;
}
}else{
if(_2a8){
var _2ab=_29e.genPseudoUrl(_2a6,true);
_2a4.action=_2ab;
var _2ac=new _29d.om.ActionRenderFormBind(_2a4,_2a6.url,_2a6.portletEntityId,op);
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+op+") for form with action: "+_2a5);
}
}else{
if(_2a5==null||_2a5.length==0){
var _2ac=new _29d.om.ActionRenderFormBind(_2a4,null,this.entityId,null);
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
_2a9=true;
}
}
}
if(_2a9&&_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_2a5);
}
}
}
var _2ad=_29f.getElementsByTagName("a");
if(_2ad){
for(var i=0;i<_2ad.length;i++){
var _2ae=_2ad[i];
var _2af=_2ae.href;
var _2a6=_29e.parseContentUrl(_2af);
var _2b0=null;
if(!_2a2){
_2b0=_29e.genPseudoUrl(_2a6);
}
if(!_2b0){
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2af);
}
}else{
if(_2b0==_2af){
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2af);
}
}else{
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2af+" with: "+_2b0);
}
_2ae.href=_2b0;
}
}
}
}
}
},getPWin:function(){
var _2b1=jetspeed;
var _2b2=this.properties[_2b1.id.PP_WIDGET_ID];
if(_2b2){
return _2b1.page.getPWin(_2b2);
}
return null;
},getCurWinState:function(_2b3){
var _2b4=null;
try{
var _2b5=this.getPWin();
if(!_2b5){
return null;
}
_2b4=_2b5.getCurWinStateForPersist(_2b3);
if(!_2b3){
if(_2b4.layout==null){
_2b4.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2b4;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2b6,_2b7){
var _2b8=jetspeed;
var jsId=_2b8.id;
if(!_2b6){
_2b6={};
}
var _2ba=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2bb=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2b6[jsId.PP_WINDOW_POSITION_STATIC]=_2ba;
_2b6[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2bb;
var _2bc=this.properties["width"];
if(!_2b7&&_2bc!=null&&_2bc>0){
_2b6.width=Math.floor(_2bc);
}else{
if(_2b7){
_2b6.width=-1;
}
}
var _2bd=this.properties["height"];
if(!_2b7&&_2bd!=null&&_2bd>0){
_2b6.height=Math.floor(_2bd);
}else{
if(_2b7){
_2b6.height=-1;
}
}
if(!_2ba||!_2b8.prefs.windowTiling){
var _2be=this.properties["x"];
if(!_2b7&&_2be!=null&&_2be>=0){
_2b6.left=Math.floor(((_2be>0)?_2be:0));
}else{
if(_2b7){
_2b6.left=-1;
}
}
var _2bf=this.properties["y"];
if(!_2b7&&_2bf!=null&&_2bf>=0){
_2b6.top=Math.floor(((_2bf>0)?_2bf:0));
}else{
_2b6.top=-1;
}
var _2c0=this._getInitialZIndex(_2b7);
if(_2c0!=null){
_2b6.zIndex=_2c0;
}
}
return _2b6;
},_initWinState:function(_2c1,_2c2){
var _2c3=jetspeed;
var _2c4=(_2c1?_2c1:{});
this.getInitialWinDims(_2c4,_2c2);
if(_2c3.debug.initWinState){
var _2c5=this.properties[_2c3.id.PP_WINDOW_POSITION_STATIC];
if(!_2c5||!_2c3.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2c4.zIndex+" x="+_2c4.left+" y="+_2c4.top+" width="+_2c4.width+" height="+_2c4.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2c4.column+" row="+_2c4.row+" width="+_2c4.width+" height="+_2c4.height);
}
}
this.lastSavedWindowState=_2c4;
return _2c4;
},_getInitialZIndex:function(_2c6){
var _2c7=null;
var _2c8=this.properties["z"];
if(!_2c6&&_2c8!=null&&_2c8>=0){
_2c7=Math.floor(_2c8);
}else{
if(_2c6){
_2c7=-1;
}
}
return _2c7;
},_getChangedWindowState:function(_2c9){
var jsId=jetspeed.id;
var _2cb=this.getSavedWinState();
if(_2cb&&dojo.lang.isEmpty(_2cb)){
_2cb=null;
_2c9=false;
}
var _2cc=this.getCurWinState(_2c9);
var _2cd=_2cc[jsId.PP_WINDOW_POSITION_STATIC];
var _2ce=!_2cd;
if(!_2cb){
var _2cf={state:_2cc,positionChanged:true,extendedPropChanged:true};
if(_2ce){
_2cf.zIndexChanged=true;
}
return _2cf;
}
var _2d0=false;
var _2d1=false;
var _2d2=false;
var _2d3=false;
for(var _2d4 in _2cc){
if(_2cc[_2d4]!=_2cb[_2d4]){
if(_2d4==jsId.PP_WINDOW_POSITION_STATIC||_2d4==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2d0=true;
_2d2=true;
_2d1=true;
}else{
if(_2d4=="zIndex"){
if(_2ce){
_2d0=true;
_2d3=true;
}
}else{
_2d0=true;
_2d1=true;
}
}
}
}
if(_2d0){
var _2cf={state:_2cc,positionChanged:_2d1,extendedPropChanged:_2d2};
if(_2ce){
_2cf.zIndexChanged=_2d3;
}
return _2cf;
}
return null;
},getPortletUrl:function(_2d5){
var _2d6=jetspeed;
var _2d7=_2d6.url;
var _2d8=null;
if(_2d5&&_2d5.url){
_2d8=_2d5.url;
}else{
if(_2d5&&_2d5.formNode){
var _2d9=_2d5.formNode.getAttribute("action");
if(_2d9){
_2d8=_2d9;
}
}
}
if(_2d8==null){
_2d8=_2d7.basePortalUrl()+_2d7.path.PORTLET+_2d6.page.getPath();
}
if(!_2d5.dontAddQueryArgs){
_2d8=_2d7.parse(_2d8);
_2d8=_2d7.addQueryParameter(_2d8,"entity",this.entityId,true);
_2d8=_2d7.addQueryParameter(_2d8,"portlet",this.name,true);
_2d8=_2d7.addQueryParameter(_2d8,"encoder","desktop",true);
if(_2d5.jsPageUrl!=null){
var _2da=_2d5.jsPageUrl.query;
if(_2da!=null&&_2da.length>0){
_2d8=_2d8.toString()+"&"+_2da;
}
}
}
if(_2d5){
_2d5.url=_2d8.toString();
}
return _2d8;
},_submitAjaxApi:function(_2db,_2dc,_2dd){
var _2de=jetspeed;
var _2df="?action="+_2db+"&id="+this.entityId+_2dc;
var _2e0=_2de.url.basePortalUrl()+_2de.url.path.AJAX_API+_2de.page.getPath()+_2df;
var _2e1="text/xml";
var _2e2=new _2de.om.Id(_2db,this.entityId);
_2e2.portlet=this;
_2de.url.retrieveContent({url:_2e0,mimetype:_2e1},_2dd,_2e2,_2de.debugContentDumpIds);
},submitWinState:function(_2e3,_2e4){
var _2e5=jetspeed;
var jsId=_2e5.id;
if(_2e5.page.isUA()||(!(_2e5.page.getPageAction(jsId.ACT_EDIT)||_2e5.page.getPageAction(jsId.ACT_VIEW)||_2e5.page.canNPE()))){
return;
}
var _2e7=null;
if(_2e4){
_2e7={state:this._initWinState(null,true)};
}else{
_2e7=this._getChangedWindowState(_2e3);
}
if(_2e7){
var _2e8=_2e7.state;
var _2e9=_2e8[jsId.PP_WINDOW_POSITION_STATIC];
var _2ea=_2e8[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _2eb=null;
if(_2e7.extendedPropChanged){
var _2ec=jsId.PP_PROP_SEPARATOR;
var _2ed=jsId.PP_PAIR_SEPARATOR;
_2eb=jsId.PP_STATICPOS+_2ec+_2e9.toString();
_2eb+=_2ed+jsId.PP_FITHEIGHT+_2ec+_2ea.toString();
_2eb=escape(_2eb);
}
var _2ee="";
var _2ef=null;
if(_2e9){
_2ef="moveabs";
if(_2e8.column!=null){
_2ee+="&col="+_2e8.column;
}
if(_2e8.row!=null){
_2ee+="&row="+_2e8.row;
}
if(_2e8.layout!=null){
_2ee+="&layoutid="+_2e8.layout;
}
if(_2e8.height!=null){
_2ee+="&height="+_2e8.height;
}
}else{
_2ef="move";
if(_2e8.zIndex!=null){
_2ee+="&z="+_2e8.zIndex;
}
if(_2e8.width!=null){
_2ee+="&width="+_2e8.width;
}
if(_2e8.height!=null){
_2ee+="&height="+_2e8.height;
}
if(_2e8.left!=null){
_2ee+="&x="+_2e8.left;
}
if(_2e8.top!=null){
_2ee+="&y="+_2e8.top;
}
}
if(_2eb!=null){
_2ee+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_2eb;
}
this._submitAjaxApi(_2ef,_2ee,new _2e5.om.MoveApiCL(this,_2e8));
if(!_2e3&&!_2e4){
if(!_2e9&&_2e7.zIndexChanged){
var _2f0=_2e5.page.getPortletArray();
if(_2f0&&(_2f0.length-1)>0){
for(var i=0;i<_2f0.length;i++){
var _2f2=_2f0[i];
if(_2f2&&_2f2.entityId!=this.entityId){
if(!_2f2.properties[_2e5.id.PP_WINDOW_POSITION_STATIC]){
_2f2.submitWinState(true);
}
}
}
}
}else{
if(_2e9){
}
}
}
}
},retrieveContent:function(_2f3,_2f4,_2f5){
if(_2f3==null){
_2f3=new jetspeed.om.PortletCL(this,_2f5,_2f4);
}
if(!_2f4){
_2f4={};
}
var _2f6=this;
_2f6.getPortletUrl(_2f4);
this.contentRetriever.getContent(_2f4,_2f3,_2f6,jetspeed.debugContentDumpIds);
},setPortletContent:function(_2f7,_2f8,_2f9){
var _2fa=this.getPWin();
if(_2f9!=null&&_2f9.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_2f9;
if(_2fa&&!this.loadingIndicatorIsShown()){
_2fa.setPortletTitle(_2f9);
}
}
if(_2fa){
_2fa.setPortletContent(_2f7,_2f8);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _2fc=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _2fd=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _2fe=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _2ff=this.getPWin();
if(_2ff&&(_2fc||_2fd)){
var _300=_2ff.getPortletTitle();
if(_300&&(_300==_2fc||_300==_2fd)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_301){
var _302=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_302=jetspeed.prefs.desktopActionLabels[_301];
if(_302!=null&&_302.length==0){
_302=null;
}
}
return _302;
},loadingIndicatorShow:function(_303){
if(_303&&!this.loadingIndicatorIsShown()){
var _304=this._getLoadingActionLabel(_303);
var _305=this.getPWin();
if(_305&&_304){
_305.setPortletTitle(_304);
}
}
},loadingIndicatorHide:function(){
var _306=this.getPWin();
if(_306){
_306.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_308,_309){
var _30a=jetspeed;
var _30b=_30a.url;
var _30c=null;
if(_308!=null){
_30c=this.getAction(_308);
}
var _30d=_309;
if(_30d==null&&_30c!=null){
_30d=_30c.url;
}
if(_30d==null){
return;
}
var _30e=_30b.basePortalUrl()+_30b.path.PORTLET+"/"+_30d+_30a.page.getPath();
if(_308!=_30a.id.ACT_PRINT){
this.retrieveContent(null,{url:_30e});
}else{
var _30f=_30a.page.getPageUrl();
_30f=_30b.addQueryParameter(_30f,"jsprintmode","true");
_30f=_30b.addQueryParameter(_30f,"jsaction",escape(_30c.url));
_30f=_30b.addQueryParameter(_30f,"jsentity",this.entityId);
_30f=_30b.addQueryParameter(_30f,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_30f.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
}
},getAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},getCurrentActionState:function(){
return this.currentActionState;
},getCurrentActionMode:function(){
return this.currentActionMode;
},updateActions:function(_311,_312,_313){
if(_311){
this.actions=_311;
}else{
this.actions={};
}
this.currentActionState=_312;
this.currentActionMode=_313;
this.syncActions();
},syncActions:function(){
var _314=jetspeed;
_314.page.setPageModePortletActions(this);
var _315=this.getPWin();
if(_315){
_315.actionBtnSync(_314,_314.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_318,_319){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_318;
this.submitOperation=_319;
this.formSubmitInProgress=false;
};
dojo.inherits(jetspeed.om.ActionRenderFormBind,dojo.io.FormBind);
dojo.lang.extend(jetspeed.om.ActionRenderFormBind,{init:function(args){
var form=dojo.byId(args.formNode);
if(!form||!form.tagName||form.tagName.toLowerCase()!="form"){
throw new Error("FormBind: Couldn't apply, invalid form");
}else{
if(this.form==form){
return;
}else{
if(this.form){
throw new Error("FormBind: Already applied to a form");
}
}
}
dojo.lang.mixin(this.bindArgs,args);
this.form=form;
this.eventConfMgr(false);
form.oldSubmit=form.submit;
form.submit=function(){
form.onsubmit();
};
},eventConfMgr:function(_31c){
var fn=(_31c)?"disconnect":"connect";
var _31e=dojo.event;
var form=this.form;
_31e[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_31e[fn]("after",node,"onclick",this,"click",null);
}
}
var _322=form.getElementsByTagName("input");
for(var i=0;i<_322.length;i++){
var _323=_322[i];
if(_323.type.toLowerCase()=="image"&&_323.form==form){
_31e[fn]("after",_323,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_31e[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_325){
var _326=true;
if(this.isFormSubmitInProgress()){
_326=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_326=false;
}
}
}
return _326;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _328=jetspeed.portleturl.parseContentUrl(this.form.action);
var _329={};
if(_328.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_328.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _32a=jetspeed.portleturl.genPseudoUrl(_328,true);
this.form.action=_32a;
this.submitOperation=_328.operation;
this.entityId=_328.portletEntityId;
_329.url=_328.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_329.formFilter=dojo.lang.hitch(this,"formFilter");
_329.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_329),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_329),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_32b){
if(_32b!=undefined){
this.formSubmitInProgress=_32b;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_32c,_32d,_32e){
this.portlet=_32c;
this.suppressGetActions=_32d;
this.formbind=null;
if(_32e!=null&&_32e.submitFormBindObject!=null){
this.formbind=_32e.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_32f){
if(this.portlet==null){
return;
}
if(_32f){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_330,_331,_332,http){
var _334=null;
var _335=(_330?_330.indexOf("</JS_PORTLET_HEAD_ELEMENTS>"):-1);
if(_335!=-1){
_335+="</JS_PORTLET_HEAD_ELEMENTS>".length;
_334=_330.substring(0,_335);
_330=_330.substring(_335);
}
var _336=null;
if(http!=null){
try{
_336=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_336!=null){
_336=unescape(_336);
}
}
_332.setPortletContent(_330,_331,_336);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_332.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_338,_339,_33a){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_339+" type: "+type+jetspeed.formatError(_338));
}};
jetspeed.om.PortletActionCL=function(_33b,_33c){
this.portlet=_33b;
this.formbind=null;
if(_33c!=null&&_33c.submitFormBindObject!=null){
this.formbind=_33c.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_33d){
if(this.portlet==null){
return;
}
if(_33d){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_33e,_33f,_340,http){
var _342=jetspeed;
var _343=null;
var _344=false;
var _345=_342.portleturl.parseContentUrl(_33e);
if(_345.operation==_342.portleturl.PORTLET_REQUEST_ACTION||_345.operation==_342.portleturl.PORTLET_REQUEST_RENDER){
if(_342.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_345.operation+"-url in response body: "+_33e+"  url: "+_345.url+" entity-id: "+_345.portletEntityId);
}
_343=_345.url;
}else{
if(_342.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_33e);
}
_343=_33e;
if(_343){
var _346=_343.indexOf(_342.url.basePortalUrl()+_342.url.path.PORTLET);
if(_346==-1){
_344=true;
window.location.href=_343;
_343=null;
}else{
if(_346>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_33e);
_343=null;
}
}
}
}
if(_343!=null&&!_342.noActionRender){
if(_342.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_343);
}
var _347=new jetspeed.PortletRenderer(false,false,false,_343,true);
_347.renderAll();
}else{
this._loading(false);
}
if(!_344&&this.portlet){
_342.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_349,_34a,_34b){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_349));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _34c=this.getUrl();
if(_34c){
var _34d=jetspeed;
if(!_34d.prefs.ajaxPageNavigation||_34d.url.urlStartsWithHttp(_34c)){
_34d.pageNavigate(_34c,this.getTarget());
}else{
_34d.updatePage(_34c);
}
}
}
},navigateUrl:function(){
return jetspeed.page.makePageUrl(this.getUrl());
},getType:function(){
return this.type;
},getTitle:function(){
return this.title;
},getShortTitle:function(){
return this["short-title"];
},getSkin:function(){
return this.skin;
},getUrl:function(){
return this.url;
},getTarget:function(){
return this.target;
},getHidden:function(){
return this.hidden;
},getSelected:function(){
return this.selected;
},getText:function(){
return this.text;
},isLeaf:function(){
return true;
},isMenu:function(){
return false;
},isSeparator:function(){
return false;
}});
jetspeed.om.MenuOptionSeparator=function(){
};
dojo.inherits(jetspeed.om.MenuOptionSeparator,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.MenuOptionSeparator,{isSeparator:function(){
return true;
}});
jetspeed.om.Menu=function(_34e,_34f){
this._is_parsed=false;
this.name=_34e;
this.type=_34f;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_350){
if(!_350){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_350);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_352){
if(!this.hasOptions()){
return null;
}
if(_352==0||_352>0){
if(_352>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_352];
}
}
},hasOptions:function(){
return ((this.options&&this.options.length>0)?true:false);
},isMenu:function(){
return true;
},isLeaf:function(){
return false;
},hasNestedMenus:function(){
if(!this.options){
return false;
}
for(var i=0;i<this.options.length;i++){
var _354=this.options[i];
if(_354 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_356,_357){
var _358=this.parseMenu(data,_357.menuName,_357.menuType);
_357.page.putMenu(_358);
},notifyFailure:function(type,_35a,_35b,_35c){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_35c.toString()+"] url: "+_35b+" type: "+type+jetspeed.formatError(_35a));
},parseMenu:function(node,_35e,_35f){
var menu=null;
var _361=node.getElementsByTagName("js");
if(!_361||_361.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _362=_361[0].childNodes;
for(var i=0;i<_362.length;i++){
var _364=_362[i];
if(_364.nodeType!=1){
continue;
}
var _365=_364.nodeName;
if(_365=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_364,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_35e;
}
if(menu.type==null){
menu.type=_35f;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _368=null;
var _369=node.childNodes;
for(var i=0;i<_369.length;i++){
var _36b=_369[i];
if(_36b.nodeType!=1){
continue;
}
var _36c=_36b.nodeName;
if(_36c=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_36b,new jetspeed.om.Menu()));
}
}else{
if(_36c=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_36b,new jetspeed.om.MenuOption()));
}
}else{
if(_36c=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_36b,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_36c){
mObj[_36c]=((_36b&&_36b.firstChild)?_36b.firstChild.nodeValue:null);
}
}
}
}
}
if(mObj.setParsed){
mObj.setParsed();
}
return mObj;
}});
jetspeed.om.MenusApiCL=function(_36d,_36e,_36f){
this.includeMenuDefs=_36d;
this.isPageUpdate=_36e;
this.initEditModeConf=_36f;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_371,_372){
var _373=this.getMenuDefs(data,_371,_372);
for(var i=0;i<_373.length;i++){
var mObj=_373[i];
_372.page.putMenu(mObj);
}
this.notifyFinished(_372);
},getMenuDefs:function(data,_377,_378){
var _379=[];
var _37a=data.getElementsByTagName("menu");
for(var i=0;i<_37a.length;i++){
var _37c=_37a[i].getAttribute("type");
if(this.includeMenuDefs){
_379.push(this.parseMenuObject(_37a[i],new jetspeed.om.Menu(null,_37c)));
}else{
var _37d=_37a[i].firstChild.nodeValue;
_379.push(new jetspeed.om.Menu(_37d,_37c));
}
}
return _379;
},notifyFailure:function(type,_37f,_380,_381){
dojo.raise("MenusApiCL error ["+_381.toString()+"] url: "+_380+" type: "+type+jetspeed.formatError(_37f));
},notifyFinished:function(_382){
var _383=jetspeed;
if(this.includeMenuDefs){
_383.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_383.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_383.debug.profile){
dojo.profile.end("loadFromPSML");
if(!this.isPageUpdate){
dojo.profile.end("initializeDesktop");
}else{
dojo.profile.end("updatePage");
}
dojo.profile.debugAllItems(true);
dojo.debug("-------------------------");
}
}});
jetspeed.om.PortletChangeActionCL=function(_384){
this.portletEntityId=_384;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_386,_387){
if(jetspeed.url.checkAjaxApiResponse(_386,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_388){
var _389=jetspeed.page.getPortlet(this.portletEntityId);
if(_389){
if(_388){
_389.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_389.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_38b,_38c,_38d){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_38d.toString()+"] url: "+_38c+" type: "+type+jetspeed.formatError(_38b));
}});
jetspeed.om.PageChangeActionCL=function(_38e){
this.pageActionUrl=_38e;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_390,_391){
if(jetspeed.url.checkAjaxApiResponse(_390,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_393,_394,_395){
dojo.raise("PageChangeActionCL error ["+_395.toString()+"] url: "+_394+" type: "+type+jetspeed.formatError(_393));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_397,_398){
var _399=jetspeed;
if(_399.url.checkAjaxApiResponse(_397,data,null,false,"user-info")){
var _39a=data.getElementsByTagName("js");
if(_39a&&_39a.length==1){
var root=_39a[0];
var un=_399.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _39e=root.getElementsByTagName("role");
if(_39e!=null){
for(var i=0;i<_39e.length;i++){
var role=(_39e[i].firstChild?_39e[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_399.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_3a2,_3a3,_3a4){
dojo.raise("UserInfoCL error ["+_3a4.toString()+"] url: "+_3a3+" type: "+type+jetspeed.formatError(_3a2));
}});
jetspeed.om.PortletActionsCL=function(_3a5){
this.portletEntityIds=_3a5;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_3a6){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _3a8=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_3a8){
if(_3a6){
_3a8.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3a8.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_3aa,_3ab){
var _3ac=jetspeed;
this._loading(false);
if(_3ac.url.checkAjaxApiResponse(_3aa,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_3ac.page);
}
},processPortletActionsResponse:function(node,_3ae){
var _3af=this.parsePortletActionsResponse(node,_3ae);
for(var i=0;i<_3af.length;i++){
var _3b1=_3af[i];
var _3b2=_3b1.id;
var _3b3=_3ae.getPortlet(_3b2);
if(_3b3!=null){
_3b3.updateActions(_3b1.actions,_3b1.currentActionState,_3b1.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3b5){
var _3b6=new Array();
var _3b7=node.getElementsByTagName("js");
if(!_3b7||_3b7.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3b6;
}
var _3b8=_3b7[0].childNodes;
for(var i=0;i<_3b8.length;i++){
var _3ba=_3b8[i];
if(_3ba.nodeType!=1){
continue;
}
var _3bb=_3ba.nodeName;
if(_3bb=="portlets"){
var _3bc=_3ba;
var _3bd=_3bc.childNodes;
for(var pI=0;pI<_3bd.length;pI++){
var _3bf=_3bd[pI];
if(_3bf.nodeType!=1){
continue;
}
var _3c0=_3bf.nodeName;
if(_3c0=="portlet"){
var _3c1=this.parsePortletElement(_3bf,_3b5);
if(_3c1!=null){
_3b6.push(_3c1);
}
}
}
}
}
return _3b6;
},parsePortletElement:function(node,_3c3){
var _3c4=node.getAttribute("id");
if(_3c4!=null){
var _3c5=_3c3._parsePSMLActions(node,null);
var _3c6=_3c3._parsePSMLChildOrAttr(node,"state");
var _3c7=_3c3._parsePSMLChildOrAttr(node,"mode");
return {id:_3c4,actions:_3c5,currentActionState:_3c6,currentActionMode:_3c7};
}
return null;
},notifyFailure:function(type,_3c9,_3ca,_3cb){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3cb.toString()+"] url: "+_3ca+" type: "+type+jetspeed.formatError(_3c9));
}});
jetspeed.om.MoveApiCL=function(_3cc,_3cd){
this.portlet=_3cc;
this.changedState=_3cd;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3ce){
if(this.portlet==null){
return;
}
if(_3ce){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3d0,_3d1){
var _3d2=jetspeed;
this._loading(false);
dojo.lang.mixin(_3d1.portlet.lastSavedWindowState,this.changedState);
var _3d3=true;
if(djConfig.isDebug&&_3d2.debug.submitWinState){
_3d3=true;
}
var _3d4=_3d2.url.checkAjaxApiResponse(_3d0,data,["refresh"],_3d3,("move-portlet ["+_3d1.portlet.entityId+"]"),_3d2.debug.submitWinState);
if(_3d4=="refresh"){
var _3d5=_3d2.page.getPageUrl();
if(!_3d2.prefs.ajaxPageNavigation){
_3d2.pageNavigate(_3d5,null,true);
}else{
_3d2.updatePage(_3d5,false,true);
}
}
},notifyFailure:function(type,_3d7,_3d8,_3d9){
this._loading(false);
dojo.debug("submitWinState error ["+_3d9.entityId+"] url: "+_3d8+" type: "+type+jetspeed.formatError(_3d7));
}};
jetspeed.postload_addEventListener=function(node,_3db,fnc,_3dd){
if((_3db=="load"||_3db=="DOMContentLoaded"||_3db=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3db,fnc,_3dd);
}
};
jetspeed.postload_attachEvent=function(node,_3df,fnc){
if(_3df=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3df,fnc);
}
};
jetspeed.postload_docwrite=function(_3e1){
if(!_3e1){
return;
}
_3e1=_3e1.replace(/^\s+|\s+$/g,"");
var _3e2=/^<script\b([^>]*)>.*?<\/script>/i;
var _3e3=_3e2.exec(_3e1);
if(_3e3){
_3e1=null;
var _3e4=_3e3[1];
if(_3e4){
var _3e5=/\bid\s*=\s*([^\s]+)/i;
var _3e6=_3e5.exec(_3e4);
if(_3e6){
var _3e7=_3e6[1];
_3e1="<img id="+_3e7+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3e1){
var _3e9=dojo;
tn=_3e9.doc().createElement("div");
tn.style.visibility="hidden";
_3e9.body().appendChild(tn);
tn.innerHTML=_3e1;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_3ea,_3eb,_3ec){
if(_3ea==document||_3ea==window){
if(_3ec&&_3ec.length>0){
var _3ed=jetspeed.portleturl;
if(_3ec.indexOf(_3ed.DESKTOP_ACTION_PREFIX_URL)!=0&&_3ec.indexOf(_3ed.DESKTOP_RENDER_PREFIX_URL)!=0){
_3ea.location=_3ec;
}
}
}else{
if(_3ea!=null){
var _3ee=_3eb.indexOf(".");
if(_3ee==-1){
_3ea[_3eb]=_3ec;
}else{
var _3ef=_3eb.substring(0,_3ee);
var _3f0=_3ea[_3ef];
if(_3f0){
var _3f1=_3eb.substring(_3ee+1);
if(_3f1){
_3f0[_3f1]=_3ec;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _3f3=document.createElement("script");
_3f3.setAttribute("type","text/plain");
_3f3.setAttribute("language","ignore");
_3f3.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_3f3);
return _3f3;
};
jetspeed.containsElement=function(_3f4,_3f5,_3f6,_3f7){
if(!_3f4||!_3f5||!_3f6){
return false;
}
if(!_3f7){
_3f7=document;
}
var _3f8=_3f7.getElementsByTagName(_3f4);
if(!_3f8){
return false;
}
for(var i=0;i<_3f8.length;++i){
var _3fa=_3f8[i].getAttribute(_3f5);
if(_3fa==_3f6){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _3fb=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _3fc=_3fb.concat([" height: ","","",";"]);
var _3fd=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _3fe=_3fc.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _3ff=_3fe.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_3fb,cssHeight:_3fc,cssWidthHeight:_3fd,cssOverflow:_3fe,cssPosition:_3ff,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_400,_401,_402,_403,_404,_405){
var djH=dojo.html;
var jsId=jetspeed.id;
var _408=null;
var _409=-1;
var _40a=-1;
var _40b=-1;
if(_400){
var _40c=_400.childNodes;
if(_40c){
_40b=_40c.length;
}
_408=[];
if(_40b>0){
var _40d="",_40e="";
if(!_405){
_40d=jsId.PWIN_CLASS;
}
if(_402){
_40d+=((_40d.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_403){
_40d+=((_40d.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_404&&!_403){
_40d+=((_40d.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_403&&!_404){
_40e=((_40e.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_40d.length>0){
var _40f=new RegExp("(^|\\s+)("+_40d+")(\\s+|$)");
var _410=null;
if(_40e.length>0){
_410=new RegExp("(^|\\s+)("+_40e+")(\\s+|$)");
}
var _411,_412,_413;
for(var i=0;i<_40b;i++){
_411=_40c[i];
_412=false;
_413=djH.getClass(_411);
if(_40f.test(_413)&&(_410==null||!_410.test(_413))){
_408.push(_411);
_412=true;
}
if(_401&&_411==_401){
if(!_412){
_408.push(_411);
}
_409=i;
_40a=_408.length-1;
}
}
}
}
}
return {matchingNodes:_408,totalNodes:_40b,matchNodeIndex:_409,matchNodeIndexInMatchingNodes:_40a};
},getPWinsFromNodes:function(_415){
var _416=jetspeed.page;
var _417=null;
if(_415){
_417=new Array();
for(var i=0;i<_415.length;i++){
var _419=_416.getPWin(_415[i].id);
if(_419){
_417.push(_419);
}
}
}
return _417;
},createPortletWindow:function(_41a,_41b,_41c){
var _41d=false;
if(djConfig.isDebug&&_41c.debug.profile){
_41d=true;
dojo.profile.start("createPortletWindow");
}
var _41e=(_41b!=null);
var _41f=false;
var _420=null;
if(_41e&&_41b<_41c.page.columns.length&&_41b>=0){
_420=_41c.page.columns[_41b].domNode;
}
if(_420==null){
_41f=true;
_420=document.getElementById(_41c.id.DESKTOP);
}
if(_420==null){
return;
}
var _421={};
if(_41a.isPortlet){
_421.portlet=_41a;
if(_41c.prefs.printModeOnly!=null){
_421.printMode=true;
}
if(_41f){
_41a.properties[_41c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_41c.widget.PortletWindow.prototype.altInitParamsDef(_421,_41a);
if(_41f){
pwP.altInitParams[_41c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _423=new _41c.widget.PortletWindow();
_423.build(_421,_420);
if(_41d){
dojo.profile.end("createPortletWindow");
}
return _423;
},getLayoutExtents:function(node,_425,_426,_427){
if(!_425){
_425=_426.gcs(node);
}
var pad=_426._getPadExtents(node,_425);
var _429=_426._getBorderExtents(node,_425);
var _42a={l:(pad.l+_429.l),t:(pad.t+_429.t),w:(pad.w+_429.w),h:(pad.h+_429.h)};
var _42b=_426._getMarginExtents(node,_425,_427);
return {bE:_429,pE:pad,pbE:_42a,mE:_42b,lessW:(_42a.w+_42b.w),lessH:(_42a.h+_42b.h)};
},getContentBoxSize:function(node,_42d){
var w=node.clientWidth,h,_430;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_430=_42d.pbE;
}else{
h=node.clientHeight;
_430=_42d.pE;
}
return {w:(w-_430.w),h:(h-_430.h)};
},getMarginBoxSize:function(node,_432){
return {w:(node.offsetWidth+_432.mE.w),h:(node.offsetHeight+_432.mE.h)};
},getMarginBox:function(node,_434,_435,_436){
var l=node.offsetLeft-_434.mE.l,t=node.offsetTop-_434.mE.t;
if(_435&&_436.UAope){
l-=_435.bE.l;
t-=_435.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_434.mE.w),h:(node.offsetHeight+_434.mE.h)};
},setMarginBox:function(node,_43a,_43b,_43c,_43d,_43e,_43f,_440){
var pb=_43e.pbE,mb=_43e.mE;
if(_43c!=null&&_43c>=0){
_43c=Math.max(_43c-pb.w-mb.w,0);
}
if(_43d!=null&&_43d>=0){
_43d=Math.max(_43d-pb.h-mb.h,0);
}
_440._setBox(node,_43a,_43b,_43c,_43d);
},evtConnect:function(_443,_444,_445,_446,_447,_448,rate){
if(!rate){
rate=0;
}
var _44a={adviceType:_443,srcObj:_444,srcFunc:_445,adviceObj:_446,adviceFunc:_447,rate:rate};
if(_448==null){
_448=dojo.event;
}
_448.connect(_44a);
return _44a;
},evtDisconnect:function(_44b,_44c,_44d,_44e,_44f,_450){
if(_450==null){
_450=dojo.event;
}
_450.disconnect({adviceType:_44b,srcObj:_44c,srcFunc:_44d,adviceObj:_44e,adviceFunc:_44f});
},evtDisconnectWObj:function(_451,_452){
if(_452==null){
_452=dojo.event;
}
_452.disconnect(_451);
},evtDisconnectWObjAry:function(_453,_454){
if(_453&&_453.length>0){
if(_454==null){
_454=dojo.event;
}
for(var i=0;i<_453.length;i++){
_454.disconnect(_453[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _456=false;
var _457=this._popupMenuWidgets;
for(var i=0;i<_457.length;i++){
var _459=_457[i];
if(_459&&_459.isShowingNow){
_456=true;
break;
}
}
return _456;
},addPopupMenuWidget:function(_45a){
if(_45a){
this._popupMenuWidgets.push(_45a);
}
},removePopupMenuWidget:function(_45b){
if(!_45b){
return;
}
var _45c=this._popupMenuWidgets;
for(var i=0;i<_45c.length;i++){
if(_45c[i]===_45b){
_45c[i]=null;
}
}
},updateChildColInfo:function(_45e,_45f,_460,_461,_462,_463){
var _464=jetspeed;
var _465=dojo;
var _466=_465.byId(_464.id.COLUMNS);
if(!_466){
return;
}
var _467=false;
if(_45e!=null){
var _468=_45e.getAttribute("columnindex");
var _469=_45e.getAttribute("layoutid");
var _46a=(_468==null?-1:new Number(_468));
if(_46a>=0&&_469!=null&&_469.length>0){
_467=true;
}
}
var _46b=_464.page.columns||[];
var _46c=new Array(_46b.length);
var _46d=_464.page.layoutInfo;
var fnc=_464.ui._updateChildColInfo;
fnc(fnc,_466,1,_46c,_46b,_45f,_460,_461,_46d,_46d.columns,_46d.desktop,_45e,_467,_462,_463,_465,_464);
return _46c;
},_updateChildColInfo:function(fnc,_470,_471,_472,_473,_474,_475,_476,_477,_478,_479,_47a,_47b,_47c,_47d,_47e,_47f){
var _480=_470.childNodes;
var _481=(_480?_480.length:0);
if(_481==0){
return;
}
var _482=_47e.html.getAbsolutePosition(_470,true);
var _483=_47f.ui.getMarginBox(_470,_478,_479,_47f);
var _484=_477.column;
var _485,col,_487,_488,_489,_48a,_48b,_48c,_48d,_48e,_48f,_490,_491,_492;
var _493=null,_494=(_47c!=null?(_47c+1):null),_495,_496;
var _497=null;
for(var i=0;i<_481;i++){
_485=_480[i];
_488=_485.getAttribute("columnindex");
_489=(_488==null?-1:new Number(_488));
if(_489>=0){
col=_473[_489];
_492=true;
_487=(col?col.layoutActionsDisabled:false);
_48a=_485.getAttribute("layoutid");
_48b=(_48a!=null&&_48a.length>0);
_495=_494;
_496=null;
_487=((!_476)&&_487);
var _499=_471;
var _49a=(_485===_47a);
if(_48b){
if(_497==null){
_497=_471;
}
if(col){
col._updateLayoutDepth(_471);
}
_499++;
}else{
if(!_49a){
if(col&&(!_487||_476)&&(_474==null||_474[_489]==null)&&(_475==null||_471<=_475)){
_48c=_47f.ui.getMarginBox(_485,_484,_478,_47f);
if(_493==null){
_493=_48c.t-_483.t;
_491=_483.h-_493;
}
_48d=_482.left+(_48c.l-_483.l);
_48e=_482.top+_493;
_48f=_48c.h;
if(_48f<_491){
_48f=_491;
}
if(_48f<40){
_48f=40;
}
var _49b=_485.childNodes;
_490={left:_48d,top:_48e,right:(_48d+_48c.w),bottom:(_48e+_48f),childCount:(_49b?_49b.length:0),pageColIndex:_489};
_490.height=_490.bottom-_490.top;
_490.width=_490.right-_490.left;
_490.yhalf=_490.top+(_490.height/2);
_472[_489]=_490;
_492=(_490.childCount>0);
if(_492){
_485.style.height="";
}else{
_485.style.height="1px";
}
if(_47c!=null){
_496=(_47f.debugDims(_490,true)+" yhalf="+_490.yhalf+(_48c.h!=_48f?(" hreal="+_48c.h):"")+" childC="+_490.childCount+"}");
}
}
}
}
if(_47c!=null){
if(_48b){
_495=_494+1;
}
if(_496==null){
_496="---";
}
_47e.hostenv.println(_47e.string.repeat(_47d,_47c)+"["+((_489<10?" ":"")+_488)+"] "+_496);
}
if(_492){
var _49c=fnc(fnc,_485,_499,_472,_473,_474,_475,_476,_477,(_48b?_477.columnLayoutHeader:_484),_478,_47a,_47b,_495,_47d,_47e,_47f);
if(_49c!=null&&(_497==null||_49c>_497)){
_497=_49c;
}
}
}
}
_488=_470.getAttribute("columnindex");
_48a=_470.getAttribute("layoutid");
_489=(_488==null?-1:new Number(_488));
if(_489>=0&&_48a!=null&&_48a.length>0){
col=_473[_489];
col._updateLayoutChildDepth(_497);
}
return _497;
},getScrollbar:function(_49d){
var _49e=_49d.ui.scrollWidth;
if(_49e==null){
var _49f=document.createElement("div");
var _4a0="width: 100px; height: 100px; top: -300px; left: 0px; overflow: scroll; position: absolute";
_49f.style.cssText=_4a0;
var test=document.createElement("div");
_49f.style.cssText="width: 400px; height: 400px";
_49f.appendChild(test);
var _4a2=_49d.docBody;
_4a2.appendChild(_49f);
_49e=_49f.offsetWidth-_49f.clientWidth;
_4a2.removeChild(_49f);
_49f.removeChild(test);
_49f=test=null;
_49d.ui.scrollWidth=_49e;
}
return _49e;
}};
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_4a4){
this.oldXY=this.getWinDims(win,win.document,_4a4);
},getWinDims:function(win,doc,_4a7){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_4a7.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_4a7;
if(b){
x=b.clientWidth||0;
y=b.clientHeight||0;
sx=b.scrollLeft||0;
sy=b.scrollTop||0;
}
}
return {x:x,y:y,sx:sx,sy:sy};
},onResize:function(){
if(this.timerId){
window.clearTimeout(this.timerId);
}
this.timerId=dojo.lang.setTimeout(this,this.onResizeDelayedCompare,this.checkTime);
},onResizeDelayedCompare:function(){
var _4ae=jetspeed;
var _4af=this.getWinDims(window,window.document,_4ae.docBody);
this.timerId=0;
if((_4af.x!=this.oldXY.x)||(_4af.y!=this.oldXY.y)){
this.oldXY=_4af;
if(_4ae.page){
if(!this.resizing){
try{
this.resizing=true;
_4ae.page.onBrowserWindowResize();
}
catch(e){
}
finally{
this.resizing=false;
}
}
}
}
}};
jetspeed.ui.swfobject=function(){
var _4b0=jetspeed;
var _4b1=null;
var _4b2=false;
var ua=function(){
var _4b4=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_4b4[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_4b4[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_4b4[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4b7=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_4b4=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_4b4[0]==6){
_4b7=true;
}
}
if(!_4b7){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4b7&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_4b4=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_4b4,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
}();
function fixObjectLeaks(){
if(ua.ie&&ua.win&&hasPlayerVersion([8,0,0])){
window.attachEvent("onunload",function(){
var o=document.getElementsByTagName("object");
if(o){
var ol=o.length;
for(var i=0;i<ol;i++){
o[i].style.display="none";
for(var x in o[i]){
if(typeof o[i][x]=="function"){
o[i][x]=function(){
};
}
}
}
}
});
}
};
function showExpressInstall(_4be){
_4b2=true;
var obj=document.getElementById(_4be.id);
if(obj){
var ac=document.getElementById(_4be.altContentId);
if(ac){
_4b1=ac;
}
var w=_4be.width?_4be.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4be.height?_4be.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4be.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
};
function createSWF(_4c7,_4c8,el){
_4c8.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4c7){
if(typeof _4c7[i]=="string"){
if(i=="data"){
_4c8.movie=_4c7[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4c7[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4c7[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4c8){
if(typeof _4c8[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4c8[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4c7){
if(typeof _4c7[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4c7[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4c7[m]);
}
}
}
}
for(var n in _4c8){
if(typeof _4c8[n]=="string"&&n!="movie"){
createObjParam(o,n,_4c8[n]);
}
}
el.parentNode.replaceChild(o,el);
}
};
function createObjParam(el,_4d2,_4d3){
var p=document.createElement("param");
p.setAttribute("name",_4d2);
p.setAttribute("value",_4d3);
el.appendChild(p);
};
function hasPlayerVersion(rv){
return (ua.playerVersion[0]>rv[0]||(ua.playerVersion[0]==rv[0]&&ua.playerVersion[1]>rv[1])||(ua.playerVersion[0]==rv[0]&&ua.playerVersion[1]==rv[1]&&ua.playerVersion[2]>=rv[2]))?true:false;
};
function createCSS(sel,decl){
if(ua.ie&&ua.mac){
return;
}
var h=document.getElementsByTagName("head")[0];
var s=document.createElement("style");
s.setAttribute("type","text/css");
s.setAttribute("media","screen");
if(!(ua.ie&&ua.win)&&typeof document.createTextNode!="undefined"){
s.appendChild(document.createTextNode(sel+" {"+decl+"}"));
}
h.appendChild(s);
if(ua.ie&&ua.win&&typeof document.styleSheets!="undefined"&&document.styleSheets.length>0){
var ls=document.styleSheets[document.styleSheets.length-1];
if(typeof ls.addRule=="object"){
ls.addRule(sel,decl);
}
}
};
return {embedSWF:function(_4db,_4dc,_4dd,_4de,_4df,_4e0,_4e1,_4e2,_4e3,_4e4){
if(!ua.w3cdom||!_4db||!_4dc||!_4dd||!_4de||!_4df){
return;
}
if(hasPlayerVersion(_4df.split("."))){
var _4e5=(_4e3?_4e3.id:null);
createCSS("#"+_4dc,"visibility:hidden");
var att=(typeof _4e3=="object")?_4e3:{};
att.data=_4db;
att.width=_4dd;
att.height=_4de;
var par=(typeof _4e2=="object")?_4e2:{};
if(typeof _4e1=="object"){
for(var i in _4e1){
if(typeof _4e1[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4e1[i];
}else{
par.flashvars=i+"="+_4e1[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4dc));
createCSS("#"+_4dc,"visibility:visible");
if(_4e5){
var _4e9=_4b0.page.swfInfo;
if(_4e9==null){
_4e9=_4b0.page.swfInfo={};
}
_4e9[_4e5]=_4e4;
}
}else{
if(_4e0&&!_4b2&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4dc,"visibility:hidden");
var _4ea={};
_4ea.id=_4ea.altContentId=_4dc;
_4ea.width=_4dd;
_4ea.height=_4de;
_4ea.expressInstall=_4e0;
showExpressInstall(_4ea);
createCSS("#"+_4dc,"visibility:visible");
}
}
}};
}();

