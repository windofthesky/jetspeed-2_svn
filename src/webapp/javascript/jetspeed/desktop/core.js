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
var _2a7=_2a6.operation;
if(_2a7==_29e.PORTLET_REQUEST_ACTION||_2a7==_29e.PORTLET_REQUEST_RENDER){
var _2a8=_29e.genPseudoUrl(_2a6,true);
_2a4.action=_2a8;
var _2a9=new _29d.om.ActionRenderFormBind(_2a4,_2a6.url,_2a6.portletEntityId,_2a7);
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_2a7+") for form with action: "+_2a5);
}
}else{
if(_2a5==null||_2a5.length==0){
var _2a9=new _29d.om.ActionRenderFormBind(_2a4,null,this.entityId,null);
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_2a5);
}
}
}
}
}
var _2aa=_29f.getElementsByTagName("a");
if(_2aa){
for(var i=0;i<_2aa.length;i++){
var _2ab=_2aa[i];
var _2ac=_2ab.href;
var _2a6=_29e.parseContentUrl(_2ac);
var _2ad=null;
if(!_2a2){
_2ad=_29e.genPseudoUrl(_2a6);
}
if(!_2ad){
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2ac);
}
}else{
if(_2ad==_2ac){
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2ac);
}
}else{
if(_2a1){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2ac+" with: "+_2ad);
}
_2ab.href=_2ad;
}
}
}
}
}
},getPWin:function(){
var _2ae=jetspeed;
var _2af=this.properties[_2ae.id.PP_WIDGET_ID];
if(_2af){
return _2ae.page.getPWin(_2af);
}
return null;
},getCurWinState:function(_2b0){
var _2b1=null;
try{
var _2b2=this.getPWin();
if(!_2b2){
return null;
}
_2b1=_2b2.getCurWinStateForPersist(_2b0);
if(!_2b0){
if(_2b1.layout==null){
_2b1.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2b1;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2b3,_2b4){
var _2b5=jetspeed;
var jsId=_2b5.id;
if(!_2b3){
_2b3={};
}
var _2b7=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2b8=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2b3[jsId.PP_WINDOW_POSITION_STATIC]=_2b7;
_2b3[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2b8;
var _2b9=this.properties["width"];
if(!_2b4&&_2b9!=null&&_2b9>0){
_2b3.width=Math.floor(_2b9);
}else{
if(_2b4){
_2b3.width=-1;
}
}
var _2ba=this.properties["height"];
if(!_2b4&&_2ba!=null&&_2ba>0){
_2b3.height=Math.floor(_2ba);
}else{
if(_2b4){
_2b3.height=-1;
}
}
if(!_2b7||!_2b5.prefs.windowTiling){
var _2bb=this.properties["x"];
if(!_2b4&&_2bb!=null&&_2bb>=0){
_2b3.left=Math.floor(((_2bb>0)?_2bb:0));
}else{
if(_2b4){
_2b3.left=-1;
}
}
var _2bc=this.properties["y"];
if(!_2b4&&_2bc!=null&&_2bc>=0){
_2b3.top=Math.floor(((_2bc>0)?_2bc:0));
}else{
_2b3.top=-1;
}
var _2bd=this._getInitialZIndex(_2b4);
if(_2bd!=null){
_2b3.zIndex=_2bd;
}
}
return _2b3;
},_initWinState:function(_2be,_2bf){
var _2c0=jetspeed;
var _2c1=(_2be?_2be:{});
this.getInitialWinDims(_2c1,_2bf);
if(_2c0.debug.initWinState){
var _2c2=this.properties[_2c0.id.PP_WINDOW_POSITION_STATIC];
if(!_2c2||!_2c0.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2c1.zIndex+" x="+_2c1.left+" y="+_2c1.top+" width="+_2c1.width+" height="+_2c1.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2c1.column+" row="+_2c1.row+" width="+_2c1.width+" height="+_2c1.height);
}
}
this.lastSavedWindowState=_2c1;
return _2c1;
},_getInitialZIndex:function(_2c3){
var _2c4=null;
var _2c5=this.properties["z"];
if(!_2c3&&_2c5!=null&&_2c5>=0){
_2c4=Math.floor(_2c5);
}else{
if(_2c3){
_2c4=-1;
}
}
return _2c4;
},_getChangedWindowState:function(_2c6){
var jsId=jetspeed.id;
var _2c8=this.getSavedWinState();
if(_2c8&&dojo.lang.isEmpty(_2c8)){
_2c8=null;
_2c6=false;
}
var _2c9=this.getCurWinState(_2c6);
var _2ca=_2c9[jsId.PP_WINDOW_POSITION_STATIC];
var _2cb=!_2ca;
if(!_2c8){
var _2cc={state:_2c9,positionChanged:true,extendedPropChanged:true};
if(_2cb){
_2cc.zIndexChanged=true;
}
return _2cc;
}
var _2cd=false;
var _2ce=false;
var _2cf=false;
var _2d0=false;
for(var _2d1 in _2c9){
if(_2c9[_2d1]!=_2c8[_2d1]){
if(_2d1==jsId.PP_WINDOW_POSITION_STATIC||_2d1==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2cd=true;
_2cf=true;
_2ce=true;
}else{
if(_2d1=="zIndex"){
if(_2cb){
_2cd=true;
_2d0=true;
}
}else{
_2cd=true;
_2ce=true;
}
}
}
}
if(_2cd){
var _2cc={state:_2c9,positionChanged:_2ce,extendedPropChanged:_2cf};
if(_2cb){
_2cc.zIndexChanged=_2d0;
}
return _2cc;
}
return null;
},getPortletUrl:function(_2d2){
var _2d3=jetspeed;
var _2d4=_2d3.url;
var _2d5=null;
if(_2d2&&_2d2.url){
_2d5=_2d2.url;
}else{
if(_2d2&&_2d2.formNode){
var _2d6=_2d2.formNode.getAttribute("action");
if(_2d6){
_2d5=_2d6;
}
}
}
if(_2d5==null){
_2d5=_2d4.basePortalUrl()+_2d4.path.PORTLET+_2d3.page.getPath();
}
if(!_2d2.dontAddQueryArgs){
_2d5=_2d4.parse(_2d5);
_2d5=_2d4.addQueryParameter(_2d5,"entity",this.entityId,true);
_2d5=_2d4.addQueryParameter(_2d5,"portlet",this.name,true);
_2d5=_2d4.addQueryParameter(_2d5,"encoder","desktop",true);
if(_2d2.jsPageUrl!=null){
var _2d7=_2d2.jsPageUrl.query;
if(_2d7!=null&&_2d7.length>0){
_2d5=_2d5.toString()+"&"+_2d7;
}
}
}
if(_2d2){
_2d2.url=_2d5.toString();
}
return _2d5;
},_submitAjaxApi:function(_2d8,_2d9,_2da){
var _2db=jetspeed;
var _2dc="?action="+_2d8+"&id="+this.entityId+_2d9;
var _2dd=_2db.url.basePortalUrl()+_2db.url.path.AJAX_API+_2db.page.getPath()+_2dc;
var _2de="text/xml";
var _2df=new _2db.om.Id(_2d8,this.entityId);
_2df.portlet=this;
_2db.url.retrieveContent({url:_2dd,mimetype:_2de},_2da,_2df,_2db.debugContentDumpIds);
},submitWinState:function(_2e0,_2e1){
var _2e2=jetspeed;
var jsId=_2e2.id;
if(_2e2.page.isUA()||(!(_2e2.page.getPageAction(jsId.ACT_EDIT)||_2e2.page.getPageAction(jsId.ACT_VIEW)||_2e2.page.canNPE()))){
return;
}
var _2e4=null;
if(_2e1){
_2e4={state:this._initWinState(null,true)};
}else{
_2e4=this._getChangedWindowState(_2e0);
}
if(_2e4){
var _2e5=_2e4.state;
var _2e6=_2e5[jsId.PP_WINDOW_POSITION_STATIC];
var _2e7=_2e5[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _2e8=null;
if(_2e4.extendedPropChanged){
var _2e9=jsId.PP_PROP_SEPARATOR;
var _2ea=jsId.PP_PAIR_SEPARATOR;
_2e8=jsId.PP_STATICPOS+_2e9+_2e6.toString();
_2e8+=_2ea+jsId.PP_FITHEIGHT+_2e9+_2e7.toString();
_2e8=escape(_2e8);
}
var _2eb="";
var _2ec=null;
if(_2e6){
_2ec="moveabs";
if(_2e5.column!=null){
_2eb+="&col="+_2e5.column;
}
if(_2e5.row!=null){
_2eb+="&row="+_2e5.row;
}
if(_2e5.layout!=null){
_2eb+="&layoutid="+_2e5.layout;
}
if(_2e5.height!=null){
_2eb+="&height="+_2e5.height;
}
}else{
_2ec="move";
if(_2e5.zIndex!=null){
_2eb+="&z="+_2e5.zIndex;
}
if(_2e5.width!=null){
_2eb+="&width="+_2e5.width;
}
if(_2e5.height!=null){
_2eb+="&height="+_2e5.height;
}
if(_2e5.left!=null){
_2eb+="&x="+_2e5.left;
}
if(_2e5.top!=null){
_2eb+="&y="+_2e5.top;
}
}
if(_2e8!=null){
_2eb+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_2e8;
}
this._submitAjaxApi(_2ec,_2eb,new _2e2.om.MoveApiCL(this,_2e5));
if(!_2e0&&!_2e1){
if(!_2e6&&_2e4.zIndexChanged){
var _2ed=_2e2.page.getPortletArray();
if(_2ed&&(_2ed.length-1)>0){
for(var i=0;i<_2ed.length;i++){
var _2ef=_2ed[i];
if(_2ef&&_2ef.entityId!=this.entityId){
if(!_2ef.properties[_2e2.id.PP_WINDOW_POSITION_STATIC]){
_2ef.submitWinState(true);
}
}
}
}
}else{
if(_2e6){
}
}
}
}
},retrieveContent:function(_2f0,_2f1,_2f2){
if(_2f0==null){
_2f0=new jetspeed.om.PortletCL(this,_2f2,_2f1);
}
if(!_2f1){
_2f1={};
}
var _2f3=this;
_2f3.getPortletUrl(_2f1);
this.contentRetriever.getContent(_2f1,_2f0,_2f3,jetspeed.debugContentDumpIds);
},setPortletContent:function(_2f4,_2f5,_2f6){
var _2f7=this.getPWin();
if(_2f6!=null&&_2f6.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_2f6;
if(_2f7&&!this.loadingIndicatorIsShown()){
_2f7.setPortletTitle(_2f6);
}
}
if(_2f7){
_2f7.setPortletContent(_2f4,_2f5);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _2f9=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _2fa=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _2fb=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _2fc=this.getPWin();
if(_2fc&&(_2f9||_2fa)){
var _2fd=_2fc.getPortletTitle();
if(_2fd&&(_2fd==_2f9||_2fd==_2fa)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_2fe){
var _2ff=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_2ff=jetspeed.prefs.desktopActionLabels[_2fe];
if(_2ff!=null&&_2ff.length==0){
_2ff=null;
}
}
return _2ff;
},loadingIndicatorShow:function(_300){
if(_300&&!this.loadingIndicatorIsShown()){
var _301=this._getLoadingActionLabel(_300);
var _302=this.getPWin();
if(_302&&_301){
_302.setPortletTitle(_301);
}
}
},loadingIndicatorHide:function(){
var _303=this.getPWin();
if(_303){
_303.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_305,_306){
var _307=jetspeed;
var _308=_307.url;
var _309=null;
if(_305!=null){
_309=this.getAction(_305);
}
var _30a=_306;
if(_30a==null&&_309!=null){
_30a=_309.url;
}
if(_30a==null){
return;
}
var _30b=_308.basePortalUrl()+_308.path.PORTLET+"/"+_30a+_307.page.getPath();
if(_305!=_307.id.ACT_PRINT){
this.retrieveContent(null,{url:_30b});
}else{
var _30c=_307.page.getPageUrl();
_30c=_308.addQueryParameter(_30c,"jsprintmode","true");
_30c=_308.addQueryParameter(_30c,"jsaction",escape(_309.url));
_30c=_308.addQueryParameter(_30c,"jsentity",this.entityId);
_30c=_308.addQueryParameter(_30c,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_30c.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_30e,_30f,_310){
if(_30e){
this.actions=_30e;
}else{
this.actions={};
}
this.currentActionState=_30f;
this.currentActionMode=_310;
this.syncActions();
},syncActions:function(){
var _311=jetspeed;
_311.page.setPageModePortletActions(this);
var _312=this.getPWin();
if(_312){
_312.actionBtnSync(_311,_311.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_315,_316){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_315;
this.submitOperation=_316;
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
},eventConfMgr:function(_319){
var fn=(_319)?"disconnect":"connect";
var _31b=dojo.event;
var form=this.form;
_31b[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_31b[fn]("after",node,"onclick",this,"click",null);
}
}
var _31f=form.getElementsByTagName("input");
for(var i=0;i<_31f.length;i++){
var _320=_31f[i];
if(_320.type.toLowerCase()=="image"&&_320.form==form){
_31b[fn]("after",_320,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_31b[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_322){
var _323=true;
if(this.isFormSubmitInProgress()){
_323=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_323=false;
}
}
}
return _323;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _325=jetspeed.portleturl.parseContentUrl(this.form.action);
var _326={};
if(_325.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_325.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _327=jetspeed.portleturl.genPseudoUrl(_325,true);
this.form.action=_327;
this.submitOperation=_325.operation;
this.entityId=_325.portletEntityId;
_326.url=_325.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_326.formFilter=dojo.lang.hitch(this,"formFilter");
_326.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_326),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_326),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_328){
if(_328!=undefined){
this.formSubmitInProgress=_328;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_329,_32a,_32b){
this.portlet=_329;
this.suppressGetActions=_32a;
this.formbind=null;
if(_32b!=null&&_32b.submitFormBindObject!=null){
this.formbind=_32b.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_32c){
if(this.portlet==null){
return;
}
if(_32c){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_32d,_32e,_32f,http){
var _331=null;
if(http!=null){
try{
_331=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_331!=null){
_331=unescape(_331);
}
}
_32f.setPortletContent(_32d,_32e,_331);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_32f.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_333,_334,_335){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_334+" type: "+type+jetspeed.formatError(_333));
}};
jetspeed.om.PortletActionCL=function(_336,_337){
this.portlet=_336;
this.formbind=null;
if(_337!=null&&_337.submitFormBindObject!=null){
this.formbind=_337.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_338){
if(this.portlet==null){
return;
}
if(_338){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_339,_33a,_33b,http){
var _33d=jetspeed;
var _33e=null;
var _33f=false;
var _340=_33d.portleturl.parseContentUrl(_339);
if(_340.operation==_33d.portleturl.PORTLET_REQUEST_ACTION||_340.operation==_33d.portleturl.PORTLET_REQUEST_RENDER){
if(_33d.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_340.operation+"-url in response body: "+_339+"  url: "+_340.url+" entity-id: "+_340.portletEntityId);
}
_33e=_340.url;
}else{
if(_33d.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_339);
}
_33e=_339;
if(_33e){
var _341=_33e.indexOf(_33d.url.basePortalUrl()+_33d.url.path.PORTLET);
if(_341==-1){
_33f=true;
window.location.href=_33e;
_33e=null;
}else{
if(_341>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_339);
_33e=null;
}
}
}
}
if(_33e!=null&&!_33d.noActionRender){
if(_33d.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_33e);
}
var _342=new jetspeed.PortletRenderer(false,false,false,_33e,true);
_342.renderAll();
}else{
this._loading(false);
}
if(!_33f&&this.portlet){
_33d.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_344,_345,_346){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_344));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _347=this.getUrl();
if(_347){
var _348=jetspeed;
if(!_348.prefs.ajaxPageNavigation||_348.url.urlStartsWithHttp(_347)){
_348.pageNavigate(_347,this.getTarget());
}else{
_348.updatePage(_347);
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
jetspeed.om.Menu=function(_349,_34a){
this._is_parsed=false;
this.name=_349;
this.type=_34a;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_34b){
if(!_34b){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_34b);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_34d){
if(!this.hasOptions()){
return null;
}
if(_34d==0||_34d>0){
if(_34d>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_34d];
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
var _34f=this.options[i];
if(_34f instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_351,_352){
var _353=this.parseMenu(data,_352.menuName,_352.menuType);
_352.page.putMenu(_353);
},notifyFailure:function(type,_355,_356,_357){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_357.toString()+"] url: "+_356+" type: "+type+jetspeed.formatError(_355));
},parseMenu:function(node,_359,_35a){
var menu=null;
var _35c=node.getElementsByTagName("js");
if(!_35c||_35c.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _35d=_35c[0].childNodes;
for(var i=0;i<_35d.length;i++){
var _35f=_35d[i];
if(_35f.nodeType!=1){
continue;
}
var _360=_35f.nodeName;
if(_360=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_35f,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_359;
}
if(menu.type==null){
menu.type=_35a;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _363=null;
var _364=node.childNodes;
for(var i=0;i<_364.length;i++){
var _366=_364[i];
if(_366.nodeType!=1){
continue;
}
var _367=_366.nodeName;
if(_367=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_366,new jetspeed.om.Menu()));
}
}else{
if(_367=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_366,new jetspeed.om.MenuOption()));
}
}else{
if(_367=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_366,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_367){
mObj[_367]=((_366&&_366.firstChild)?_366.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_368,_369,_36a){
this.includeMenuDefs=_368;
this.isPageUpdate=_369;
this.initEditModeConf=_36a;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_36c,_36d){
var _36e=this.getMenuDefs(data,_36c,_36d);
for(var i=0;i<_36e.length;i++){
var mObj=_36e[i];
_36d.page.putMenu(mObj);
}
this.notifyFinished(_36d);
},getMenuDefs:function(data,_372,_373){
var _374=[];
var _375=data.getElementsByTagName("menu");
for(var i=0;i<_375.length;i++){
var _377=_375[i].getAttribute("type");
if(this.includeMenuDefs){
_374.push(this.parseMenuObject(_375[i],new jetspeed.om.Menu(null,_377)));
}else{
var _378=_375[i].firstChild.nodeValue;
_374.push(new jetspeed.om.Menu(_378,_377));
}
}
return _374;
},notifyFailure:function(type,_37a,_37b,_37c){
dojo.raise("MenusApiCL error ["+_37c.toString()+"] url: "+_37b+" type: "+type+jetspeed.formatError(_37a));
},notifyFinished:function(_37d){
var _37e=jetspeed;
if(this.includeMenuDefs){
_37e.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_37e.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_37e.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_37f){
this.portletEntityId=_37f;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_381,_382){
if(jetspeed.url.checkAjaxApiResponse(_381,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_383){
var _384=jetspeed.page.getPortlet(this.portletEntityId);
if(_384){
if(_383){
_384.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_384.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_386,_387,_388){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_388.toString()+"] url: "+_387+" type: "+type+jetspeed.formatError(_386));
}});
jetspeed.om.PageChangeActionCL=function(_389){
this.pageActionUrl=_389;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_38b,_38c){
if(jetspeed.url.checkAjaxApiResponse(_38b,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_38e,_38f,_390){
dojo.raise("PageChangeActionCL error ["+_390.toString()+"] url: "+_38f+" type: "+type+jetspeed.formatError(_38e));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_392,_393){
var _394=jetspeed;
if(_394.url.checkAjaxApiResponse(_392,data,null,false,"user-info")){
var _395=data.getElementsByTagName("js");
if(_395&&_395.length==1){
var root=_395[0];
var un=_394.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _399=root.getElementsByTagName("role");
if(_399!=null){
for(var i=0;i<_399.length;i++){
var role=(_399[i].firstChild?_399[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_394.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_39d,_39e,_39f){
dojo.raise("UserInfoCL error ["+_39f.toString()+"] url: "+_39e+" type: "+type+jetspeed.formatError(_39d));
}});
jetspeed.om.PortletActionsCL=function(_3a0){
this.portletEntityIds=_3a0;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_3a1){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _3a3=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_3a3){
if(_3a1){
_3a3.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3a3.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_3a5,_3a6){
var _3a7=jetspeed;
this._loading(false);
if(_3a7.url.checkAjaxApiResponse(_3a5,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_3a7.page);
}
},processPortletActionsResponse:function(node,_3a9){
var _3aa=this.parsePortletActionsResponse(node,_3a9);
for(var i=0;i<_3aa.length;i++){
var _3ac=_3aa[i];
var _3ad=_3ac.id;
var _3ae=_3a9.getPortlet(_3ad);
if(_3ae!=null){
_3ae.updateActions(_3ac.actions,_3ac.currentActionState,_3ac.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3b0){
var _3b1=new Array();
var _3b2=node.getElementsByTagName("js");
if(!_3b2||_3b2.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3b1;
}
var _3b3=_3b2[0].childNodes;
for(var i=0;i<_3b3.length;i++){
var _3b5=_3b3[i];
if(_3b5.nodeType!=1){
continue;
}
var _3b6=_3b5.nodeName;
if(_3b6=="portlets"){
var _3b7=_3b5;
var _3b8=_3b7.childNodes;
for(var pI=0;pI<_3b8.length;pI++){
var _3ba=_3b8[pI];
if(_3ba.nodeType!=1){
continue;
}
var _3bb=_3ba.nodeName;
if(_3bb=="portlet"){
var _3bc=this.parsePortletElement(_3ba,_3b0);
if(_3bc!=null){
_3b1.push(_3bc);
}
}
}
}
}
return _3b1;
},parsePortletElement:function(node,_3be){
var _3bf=node.getAttribute("id");
if(_3bf!=null){
var _3c0=_3be._parsePSMLActions(node,null);
var _3c1=_3be._parsePSMLChildOrAttr(node,"state");
var _3c2=_3be._parsePSMLChildOrAttr(node,"mode");
return {id:_3bf,actions:_3c0,currentActionState:_3c1,currentActionMode:_3c2};
}
return null;
},notifyFailure:function(type,_3c4,_3c5,_3c6){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3c6.toString()+"] url: "+_3c5+" type: "+type+jetspeed.formatError(_3c4));
}});
jetspeed.om.MoveApiCL=function(_3c7,_3c8){
this.portlet=_3c7;
this.changedState=_3c8;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3c9){
if(this.portlet==null){
return;
}
if(_3c9){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3cb,_3cc){
var _3cd=jetspeed;
this._loading(false);
dojo.lang.mixin(_3cc.portlet.lastSavedWindowState,this.changedState);
var _3ce=true;
if(djConfig.isDebug&&_3cd.debug.submitWinState){
_3ce=true;
}
var _3cf=_3cd.url.checkAjaxApiResponse(_3cb,data,["refresh"],_3ce,("move-portlet ["+_3cc.portlet.entityId+"]"),_3cd.debug.submitWinState);
if(_3cf=="refresh"){
var _3d0=_3cd.page.getPageUrl();
if(!_3cd.prefs.ajaxPageNavigation){
_3cd.pageNavigate(_3d0,null,true);
}else{
_3cd.updatePage(_3d0,false,true);
}
}
},notifyFailure:function(type,_3d2,_3d3,_3d4){
this._loading(false);
dojo.debug("submitWinState error ["+_3d4.entityId+"] url: "+_3d3+" type: "+type+jetspeed.formatError(_3d2));
}};
jetspeed.postload_addEventListener=function(node,_3d6,fnc,_3d8){
if((_3d6=="load"||_3d6=="DOMContentLoaded"||_3d6=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3d6,fnc,_3d8);
}
};
jetspeed.postload_attachEvent=function(node,_3da,fnc){
if(_3da=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3da,fnc);
}
};
jetspeed.postload_docwrite=function(_3dc){
if(!_3dc){
return;
}
_3dc=_3dc.replace(/^\s+|\s+$/g,"");
var _3dd=/^<script\b([^>]*)>.*?<\/script>/i;
var _3de=_3dd.exec(_3dc);
if(_3de){
_3dc=null;
var _3df=_3de[1];
if(_3df){
var _3e0=/\bid\s*=\s*([^\s]+)/i;
var _3e1=_3e0.exec(_3df);
if(_3e1){
var _3e2=_3e1[1];
_3dc="<img id="+_3e2+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3dc){
var _3e4=dojo;
tn=_3e4.doc().createElement("div");
tn.style.visibility="hidden";
_3e4.body().appendChild(tn);
tn.innerHTML=_3dc;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_3e5,_3e6,_3e7){
if(_3e5==document||_3e5==window){
if(_3e7&&_3e7.length>0){
var _3e8=jetspeed.portleturl;
if(_3e7.indexOf(_3e8.DESKTOP_ACTION_PREFIX_URL)!=0&&_3e7.indexOf(_3e8.DESKTOP_RENDER_PREFIX_URL)!=0){
_3e5.location=_3e7;
}
}
}else{
if(_3e5!=null){
var _3e9=_3e6.indexOf(".");
if(_3e9==-1){
_3e5[_3e6]=_3e7;
}else{
var _3ea=_3e6.substring(0,_3e9);
var _3eb=_3e5[_3ea];
if(_3eb){
var _3ec=_3e6.substring(_3e9+1);
if(_3ec){
_3eb[_3ec]=_3e7;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _3ee=document.createElement("script");
_3ee.setAttribute("type","text/plain");
_3ee.setAttribute("language","ignore");
_3ee.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_3ee);
return _3ee;
};
jetspeed.containsElement=function(_3ef,_3f0,_3f1,_3f2){
if(!_3ef||!_3f0||!_3f1){
return false;
}
if(!_3f2){
_3f2=document;
}
var _3f3=_3f2.getElementsByTagName(_3ef);
if(!_3f3){
return false;
}
for(var i=0;i<_3f3.length;++i){
var _3f5=_3f3[i].getAttribute(_3f0);
if(_3f5==_3f1){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _3f6=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _3f7=_3f6.concat([" height: ","","",";"]);
var _3f8=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _3f9=_3f7.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _3fa=_3f9.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_3f6,cssHeight:_3f7,cssWidthHeight:_3f8,cssOverflow:_3f9,cssPosition:_3fa,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_3fb,_3fc,_3fd,_3fe,_3ff,_400){
var djH=dojo.html;
var jsId=jetspeed.id;
var _403=null;
var _404=-1;
var _405=-1;
var _406=-1;
if(_3fb){
var _407=_3fb.childNodes;
if(_407){
_406=_407.length;
}
_403=[];
if(_406>0){
var _408="",_409="";
if(!_400){
_408=jsId.PWIN_CLASS;
}
if(_3fd){
_408+=((_408.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_3fe){
_408+=((_408.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_3ff&&!_3fe){
_408+=((_408.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_3fe&&!_3ff){
_409=((_409.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_408.length>0){
var _40a=new RegExp("(^|\\s+)("+_408+")(\\s+|$)");
var _40b=null;
if(_409.length>0){
_40b=new RegExp("(^|\\s+)("+_409+")(\\s+|$)");
}
var _40c,_40d,_40e;
for(var i=0;i<_406;i++){
_40c=_407[i];
_40d=false;
_40e=djH.getClass(_40c);
if(_40a.test(_40e)&&(_40b==null||!_40b.test(_40e))){
_403.push(_40c);
_40d=true;
}
if(_3fc&&_40c==_3fc){
if(!_40d){
_403.push(_40c);
}
_404=i;
_405=_403.length-1;
}
}
}
}
}
return {matchingNodes:_403,totalNodes:_406,matchNodeIndex:_404,matchNodeIndexInMatchingNodes:_405};
},getPWinsFromNodes:function(_410){
var _411=jetspeed.page;
var _412=null;
if(_410){
_412=new Array();
for(var i=0;i<_410.length;i++){
var _414=_411.getPWin(_410[i].id);
if(_414){
_412.push(_414);
}
}
}
return _412;
},createPortletWindow:function(_415,_416,_417){
var _418=false;
if(djConfig.isDebug&&_417.debug.profile){
_418=true;
dojo.profile.start("createPortletWindow");
}
var _419=(_416!=null);
var _41a=false;
var _41b=null;
if(_419&&_416<_417.page.columns.length&&_416>=0){
_41b=_417.page.columns[_416].domNode;
}
if(_41b==null){
_41a=true;
_41b=document.getElementById(_417.id.DESKTOP);
}
if(_41b==null){
return;
}
var _41c={};
if(_415.isPortlet){
_41c.portlet=_415;
if(_417.prefs.printModeOnly!=null){
_41c.printMode=true;
}
if(_41a){
_415.properties[_417.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_417.widget.PortletWindow.prototype.altInitParamsDef(_41c,_415);
if(_41a){
pwP.altInitParams[_417.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _41e=new _417.widget.PortletWindow();
_41e.build(_41c,_41b);
if(_418){
dojo.profile.end("createPortletWindow");
}
return _41e;
},getLayoutExtents:function(node,_420,_421,_422){
if(!_420){
_420=_421.gcs(node);
}
var pad=_421._getPadExtents(node,_420);
var _424=_421._getBorderExtents(node,_420);
var _425={l:(pad.l+_424.l),t:(pad.t+_424.t),w:(pad.w+_424.w),h:(pad.h+_424.h)};
var _426=_421._getMarginExtents(node,_420,_422);
return {bE:_424,pE:pad,pbE:_425,mE:_426,lessW:(_425.w+_426.w),lessH:(_425.h+_426.h)};
},getContentBoxSize:function(node,_428){
var w=node.clientWidth,h,_42b;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_42b=_428.pbE;
}else{
h=node.clientHeight;
_42b=_428.pE;
}
return {w:(w-_42b.w),h:(h-_42b.h)};
},getMarginBoxSize:function(node,_42d){
return {w:(node.offsetWidth+_42d.mE.w),h:(node.offsetHeight+_42d.mE.h)};
},getMarginBox:function(node,_42f,_430,_431){
var l=node.offsetLeft-_42f.mE.l,t=node.offsetTop-_42f.mE.t;
if(_430&&_431.UAope){
l-=_430.bE.l;
t-=_430.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_42f.mE.w),h:(node.offsetHeight+_42f.mE.h)};
},setMarginBox:function(node,_435,_436,_437,_438,_439,_43a,_43b){
var pb=_439.pbE,mb=_439.mE;
if(_437!=null&&_437>=0){
_437=Math.max(_437-pb.w-mb.w,0);
}
if(_438!=null&&_438>=0){
_438=Math.max(_438-pb.h-mb.h,0);
}
_43b._setBox(node,_435,_436,_437,_438);
},evtConnect:function(_43e,_43f,_440,_441,_442,_443,rate){
if(!rate){
rate=0;
}
var _445={adviceType:_43e,srcObj:_43f,srcFunc:_440,adviceObj:_441,adviceFunc:_442,rate:rate};
if(_443==null){
_443=dojo.event;
}
_443.connect(_445);
return _445;
},evtDisconnect:function(_446,_447,_448,_449,_44a,_44b){
if(_44b==null){
_44b=dojo.event;
}
_44b.disconnect({adviceType:_446,srcObj:_447,srcFunc:_448,adviceObj:_449,adviceFunc:_44a});
},evtDisconnectWObj:function(_44c,_44d){
if(_44d==null){
_44d=dojo.event;
}
_44d.disconnect(_44c);
},evtDisconnectWObjAry:function(_44e,_44f){
if(_44e&&_44e.length>0){
if(_44f==null){
_44f=dojo.event;
}
for(var i=0;i<_44e.length;i++){
_44f.disconnect(_44e[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _451=false;
var _452=this._popupMenuWidgets;
for(var i=0;i<_452.length;i++){
var _454=_452[i];
if(_454&&_454.isShowingNow){
_451=true;
break;
}
}
return _451;
},addPopupMenuWidget:function(_455){
if(_455){
this._popupMenuWidgets.push(_455);
}
},removePopupMenuWidget:function(_456){
if(!_456){
return;
}
var _457=this._popupMenuWidgets;
for(var i=0;i<_457.length;i++){
if(_457[i]===_456){
_457[i]=null;
}
}
},updateChildColInfo:function(_459,_45a,_45b,_45c,_45d,_45e){
var _45f=jetspeed;
var _460=dojo;
var _461=_460.byId(_45f.id.COLUMNS);
if(!_461){
return;
}
var _462=false;
if(_459!=null){
var _463=_459.getAttribute("columnindex");
var _464=_459.getAttribute("layoutid");
var _465=(_463==null?-1:new Number(_463));
if(_465>=0&&_464!=null&&_464.length>0){
_462=true;
}
}
var _466=_45f.page.columns||[];
var _467=new Array(_466.length);
var _468=_45f.page.layoutInfo;
var fnc=_45f.ui._updateChildColInfo;
fnc(fnc,_461,1,_467,_466,_45a,_45b,_45c,_468,_468.columns,_468.desktop,_459,_462,_45d,_45e,_460,_45f);
return _467;
},_updateChildColInfo:function(fnc,_46b,_46c,_46d,_46e,_46f,_470,_471,_472,_473,_474,_475,_476,_477,_478,_479,_47a){
var _47b=_46b.childNodes;
var _47c=(_47b?_47b.length:0);
if(_47c==0){
return;
}
var _47d=_479.html.getAbsolutePosition(_46b,true);
var _47e=_47a.ui.getMarginBox(_46b,_473,_474,_47a);
var _47f=_472.column;
var _480,col,_482,_483,_484,_485,_486,_487,_488,_489,_48a,_48b,_48c,_48d;
var _48e=null,_48f=(_477!=null?(_477+1):null),_490,_491;
var _492=null;
for(var i=0;i<_47c;i++){
_480=_47b[i];
_483=_480.getAttribute("columnindex");
_484=(_483==null?-1:new Number(_483));
if(_484>=0){
col=_46e[_484];
_48d=true;
_482=(col?col.layoutActionsDisabled:false);
_485=_480.getAttribute("layoutid");
_486=(_485!=null&&_485.length>0);
_490=_48f;
_491=null;
_482=((!_471)&&_482);
var _494=_46c;
var _495=(_480===_475);
if(_486){
if(_492==null){
_492=_46c;
}
if(col){
col._updateLayoutDepth(_46c);
}
_494++;
}else{
if(!_495){
if(col&&(!_482||_471)&&(_46f==null||_46f[_484]==null)&&(_470==null||_46c<=_470)){
_487=_47a.ui.getMarginBox(_480,_47f,_473,_47a);
if(_48e==null){
_48e=_487.t-_47e.t;
_48c=_47e.h-_48e;
}
_488=_47d.left+(_487.l-_47e.l);
_489=_47d.top+_48e;
_48a=_487.h;
if(_48a<_48c){
_48a=_48c;
}
if(_48a<40){
_48a=40;
}
var _496=_480.childNodes;
_48b={left:_488,top:_489,right:(_488+_487.w),bottom:(_489+_48a),childCount:(_496?_496.length:0),pageColIndex:_484};
_48b.height=_48b.bottom-_48b.top;
_48b.width=_48b.right-_48b.left;
_48b.yhalf=_48b.top+(_48b.height/2);
_46d[_484]=_48b;
_48d=(_48b.childCount>0);
if(_48d){
_480.style.height="";
}else{
_480.style.height="1px";
}
if(_477!=null){
_491=(_47a.debugDims(_48b,true)+" yhalf="+_48b.yhalf+(_487.h!=_48a?(" hreal="+_487.h):"")+" childC="+_48b.childCount+"}");
}
}
}
}
if(_477!=null){
if(_486){
_490=_48f+1;
}
if(_491==null){
_491="---";
}
_479.hostenv.println(_479.string.repeat(_478,_477)+"["+((_484<10?" ":"")+_483)+"] "+_491);
}
if(_48d){
var _497=fnc(fnc,_480,_494,_46d,_46e,_46f,_470,_471,_472,(_486?_472.columnLayoutHeader:_47f),_473,_475,_476,_490,_478,_479,_47a);
if(_497!=null&&(_492==null||_497>_492)){
_492=_497;
}
}
}
}
_483=_46b.getAttribute("columnindex");
_485=_46b.getAttribute("layoutid");
_484=(_483==null?-1:new Number(_483));
if(_484>=0&&_485!=null&&_485.length>0){
col=_46e[_484];
col._updateLayoutChildDepth(_492);
}
return _492;
},getScrollbar:function(_498){
var _499=_498.ui.scrollWidth;
if(_499==null){
var _49a=document.createElement("div");
var _49b="width: 100px; height: 100px; top: -300px; left: 0px; overflow: scroll; position: absolute";
_49a.style.cssText=_49b;
var test=document.createElement("div");
_49a.style.cssText="width: 400px; height: 400px";
_49a.appendChild(test);
var _49d=_498.docBody;
_49d.appendChild(_49a);
_499=_49a.offsetWidth-_49a.clientWidth;
_49d.removeChild(_49a);
_49a.removeChild(test);
_49a=test=null;
_498.ui.scrollWidth=_499;
}
return _499;
}};
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_49f){
this.oldXY=this.getWinDims(win,win.document,_49f);
},getWinDims:function(win,doc,_4a2){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_4a2.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_4a2;
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
var _4a9=jetspeed;
var _4aa=this.getWinDims(window,window.document,_4a9.docBody);
this.timerId=0;
if((_4aa.x!=this.oldXY.x)||(_4aa.y!=this.oldXY.y)){
this.oldXY=_4aa;
if(_4a9.page){
if(!this.resizing){
try{
this.resizing=true;
_4a9.page.onBrowserWindowResize();
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
var _4ab=jetspeed;
var _4ac=null;
var _4ad=false;
var ua=function(){
var _4af=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_4af[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_4af[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_4af[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4b2=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_4af=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_4af[0]==6){
_4b2=true;
}
}
if(!_4b2){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4b2&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_4af=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_4af,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
}
function showExpressInstall(_4b9){
_4ad=true;
var obj=document.getElementById(_4b9.id);
if(obj){
var ac=document.getElementById(_4b9.altContentId);
if(ac){
_4ac=ac;
}
var w=_4b9.width?_4b9.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4b9.height?_4b9.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4b9.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
}
function createSWF(_4c2,_4c3,el){
_4c3.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4c2){
if(typeof _4c2[i]=="string"){
if(i=="data"){
_4c3.movie=_4c2[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4c2[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4c2[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4c3){
if(typeof _4c3[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4c3[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4c2){
if(typeof _4c2[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4c2[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4c2[m]);
}
}
}
}
for(var n in _4c3){
if(typeof _4c3[n]=="string"&&n!="movie"){
createObjParam(o,n,_4c3[n]);
}
}
el.parentNode.replaceChild(o,el);
}
}
function createObjParam(el,_4cd,_4ce){
var p=document.createElement("param");
p.setAttribute("name",_4cd);
p.setAttribute("value",_4ce);
el.appendChild(p);
}
function hasPlayerVersion(rv){
return (ua.playerVersion[0]>rv[0]||(ua.playerVersion[0]==rv[0]&&ua.playerVersion[1]>rv[1])||(ua.playerVersion[0]==rv[0]&&ua.playerVersion[1]==rv[1]&&ua.playerVersion[2]>=rv[2]))?true:false;
}
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
}
return {embedSWF:function(_4d6,_4d7,_4d8,_4d9,_4da,_4db,_4dc,_4dd,_4de,_4df){
if(!ua.w3cdom||!_4d6||!_4d7||!_4d8||!_4d9||!_4da){
return;
}
if(hasPlayerVersion(_4da.split("."))){
var _4e0=(_4de?_4de.id:null);
createCSS("#"+_4d7,"visibility:hidden");
var att=(typeof _4de=="object")?_4de:{};
att.data=_4d6;
att.width=_4d8;
att.height=_4d9;
var par=(typeof _4dd=="object")?_4dd:{};
if(typeof _4dc=="object"){
for(var i in _4dc){
if(typeof _4dc[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4dc[i];
}else{
par.flashvars=i+"="+_4dc[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4d7));
createCSS("#"+_4d7,"visibility:visible");
if(_4e0){
var _4e4=_4ab.page.swfInfo;
if(_4e4==null){
_4e4=_4ab.page.swfInfo={};
}
_4e4[_4e0]=_4df;
}
}else{
if(_4db&&!_4ad&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4d7,"visibility:hidden");
var _4e5={};
_4e5.id=_4e5.altContentId=_4d7;
_4e5.width=_4d8;
_4e5.height=_4d9;
_4e5.expressInstall=_4db;
showExpressInstall(_4e5);
createCSS("#"+_4d7,"visibility:visible");
}
}
}};
}();

