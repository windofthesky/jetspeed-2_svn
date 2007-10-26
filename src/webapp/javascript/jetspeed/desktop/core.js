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
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",P_CLASS:"portlet",PWIN_CLASS:"portletWindow",PWIN_CLIENT_CLASS:"portletWindowClient",PWIN_GHOST_CLASS:"ghostPane",PW_ID_PREFIX:"pw_",COL_CLASS:"desktopColumn",COL_LAYOUTHEADER_CLASS:"desktopLayoutHeader",PP_WIDGET_ID:"widgetId",PP_CONTENT_RETRIEVER:"contentRetriever",PP_DESKTOP_EXTENDED:"jsdesktop",PP_WINDOW_POSITION_STATIC:"windowPositionStatic",PP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PP_WINDOW_DECORATION:"windowDecoration",PP_WINDOW_TITLE:"title",PP_WINDOW_ICON:"windowIcon",PP_WIDTH:"width",PP_HEIGHT:"height",PP_LEFT:"left",PP_TOP:"top",PP_COLUMN:"column",PP_ROW:"row",PP_EXCLUDE_PCONTENT:"excludePContent",PP_WINDOW_STATE:"windowState",PP_STATICPOS:"staticpos",PP_FITHEIGHT:"fitheight",PP_PROP_SEPARATOR:"=",PP_PAIR_SEPARATOR:";",ACT_MENU:"menu",ACT_MINIMIZE:"minimized",ACT_MAXIMIZE:"maximized",ACT_RESTORE:"normal",ACT_PRINT:"print",ACT_EDIT:"edit",ACT_VIEW:"view",ACT_HELP:"help",ACT_ADDPORTLET:"addportlet",ACT_REMOVEPORTLET:"removeportlet",ACT_CHANGEPORTLETTHEME:"changeportlettheme",ACT_DESKTOP_TILE:"tile",ACT_DESKTOP_UNTILE:"untile",ACT_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACT_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACT_DESKTOP_MOVE_TILED:"movetiled",ACT_DESKTOP_MOVE_UNTILED:"moveuntiled",ACT_LOAD_RENDER:"loadportletrender",ACT_LOAD_ACTION:"loadportletaction",ACT_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",PORTAL_ORIGINATE_PARAMETER:"portal",DEBUG_WINDOW_TAG:"js-db"};
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
var _10={};
_10[_9.ACT_DESKTOP_HEIGHT_EXPAND]=true;
_10[_9.ACT_DESKTOP_HEIGHT_NORMAL]=true;
_10[_9.ACT_DESKTOP_TILE]=true;
_10[_9.ACT_DESKTOP_UNTILE]=true;
_a.windowActionDesktop=_10;
}
var _11=new _c.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PortletWindow.css");
_c.html.insertCssFile(_11,document,true);
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
var _12={};
for(var i=0;i<_a.windowActionNoImage.length;i++){
_12[_a.windowActionNoImage[i]]=true;
}
_a.windowActionNoImage=_12;
}
var _14=_8.url.parse(window.location.href);
var _15=_8.url.getQueryParameter(_14,"jsprintmode")=="true";
if(_15){
_15={};
_15.action=_8.url.getQueryParameter(_14,"jsaction");
_15.entity=_8.url.getQueryParameter(_14,"jsentity");
_15.layout=_8.url.getQueryParameter(_14,"jslayoutid");
_a.printModeOnly=_15;
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
if(_15){
for(var _16 in _a.portletDecorationsConfig){
var _17=_a.portletDecorationsConfig[_16];
if(_17!=null){
_17.windowActionButtonOrder=null;
_17.windowActionMenuOrder=null;
_17.windowDisableResize=true;
_17.windowDisableMove=true;
}
}
}
_8.url.loadingIndicatorShow();
var _18={};
if(_a.windowActionButtonOrder){
var _19,_1a;
for(var aI=0;aI<_a.windowActionButtonOrder.length;aI++){
_19=_a.windowActionButtonOrder[aI];
if(_19!=null){
_18[_19]=_a.getActionLabel(_19,false,_a,_c);
}
}
for(_19 in _a.windowActionDesktop){
if(_19!=null){
_18[_19]=_a.getActionLabel(_19,false,_a,_c);
}
}
_19=_9.ACT_DESKTOP_MOVE_TILED;
_1a=_a.getActionLabel(_19,true,_a,_c);
if(_1a!=null){
_18[_19]=_1a;
}
_19=_9.ACT_DESKTOP_MOVE_UNTILED;
_1a=_a.getActionLabel(_19,true,_a,_c);
if(_1a!=null){
_18[_19]=_1a;
}
}
_8.widget.PortletWindow.prototype.actionLabels=_18;
_8.page=new _8.om.Page();
if(!_15&&djConfig.isDebug){
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
if(_8.UAie6){
_8.ui.windowResizeMgr.init(window,_8.docBody);
}
};
jetspeed.updatePage=function(_1c,_1d){
var _1e=jetspeed;
var _1f=false;
if(djConfig.isDebug&&_1e.debug.profile){
_1f=true;
dojo.profile.start("updatePage");
}
var _20=_1e.page;
if(!_1c||!_20||_1e.pageNavigateSuppress){
return;
}
if(_20.equalsPageUrl(_1c)){
return;
}
_1c=_20.makePageUrl(_1c);
if(_1c!=null){
_1e.updatePageBegin();
var _21=_20.layoutDecorator;
var _22=_20.editMode;
if(_1f){
dojo.profile.start("destroyPage");
}
_20.destroy();
if(_1f){
dojo.profile.end("destroyPage");
}
var _23=_20.portlet_windows;
var _24=_20.portlet_window_count;
var _25=new _1e.om.Page(_21,_1c,(!djConfig.preventBackButtonFix&&!_1d),_22,_20.tooltipMgr,_20.iframeCoverByWinId);
_1e.page=_25;
var _26;
if(_24>0){
for(var _27 in _23){
_26=_23[_27];
_26.bringToTop(null,true,false,_1e);
}
}
_25.retrievePsml(new _1e.om.PageCLCreateWidget(true));
if(_24>0){
for(var _27 in _23){
_26=_23[_27];
_25.putPWin(_26);
}
}
window.focus();
}
};
jetspeed.updatePageBegin=function(){
var _28=jetspeed;
if(_28.UAie6){
_28.docBody.attachEvent("onclick",_28.ie6StopMouseEvts);
_28.docBody.setCapture();
}
};
jetspeed.ie6StopMouseEvts=function(e){
if(e){
e.cancelBubble=true;
e.returnValue=false;
}
};
jetspeed.updatePageEnd=function(){
var _2a=jetspeed;
if(_2a.UAie6){
_2a.docBody.releaseCapture();
_2a.docBody.detachEvent("onclick",_2a.ie6StopMouseEvts);
_2a.docBody.releaseCapture();
}
};
jetspeed.doRender=function(_2b,_2c){
if(!_2b){
_2b={};
}else{
if((typeof _2b=="string"||_2b instanceof String)){
_2b={url:_2b};
}
}
var _2d=jetspeed.page.getPortlet(_2c);
if(_2d){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_2c+"] url: "+_2b.url);
}
_2d.retrieveContent(null,_2b);
}
};
jetspeed.doAction=function(_2e,_2f){
if(!_2e){
_2e={};
}else{
if((typeof _2e=="string"||_2e instanceof String)){
_2e={url:_2e};
}
}
var _30=jetspeed.page.getPortlet(_2f);
if(_30){
if(jetspeed.debug.doRenderDoAction){
if(!_2e.formNode){
dojo.debug("doAction ["+_2f+"] url: "+_2e.url+" form: null");
}else{
dojo.debug("doAction ["+_2f+"] url: "+_2e.url+" form: "+jetspeed.debugDumpForm(_2e.formNode));
}
}
_30.retrieveContent(new jetspeed.om.PortletActionCL(_30,_2e),_2e);
}
};
jetspeed.PortletRenderer=function(_31,_32,_33,_34,_35){
var _36=jetspeed;
var _37=_36.page;
this._jsObj=_36;
this.createWindows=_31;
this.isPageLoad=_32;
this.isPageUpdate=_33;
this.pageLoadUrl=null;
if(_32){
this.pageLoadUrl=_36.url.parse(_37.getPageUrl());
}
this.renderUrl=_34;
this.suppressGetActions=_35;
this._colLen=_37.columns.length;
this._colIndex=0;
this._portletIndex=0;
this.psByCol=_37.portletsByPageColumn;
this.debugPageLoad=_36.debug.pageLoad&&_32;
this.debugMsg=null;
if(_36.debug.doRenderDoAction||this.debugPageLoad){
this.debugMsg="";
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
var _38=this._jsObj;
var _39=this.debugMsg;
if(_39!=null){
if(this.debugPageLoad){
dojo.debug("portlet-renderer page-url: "+_38.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPageLoad){
_38.page.loadPostRender(this.isPageUpdate);
}
},_renderCurrent:function(){
var _3a=this._jsObj;
var _3b=this._colLen;
var _3c=this._colIndex;
var _3d=this._portletIndex;
if(_3c<=_3b){
var _3e;
if(_3c<_3b){
_3e=this.psByCol[_3c.toString()];
}else{
_3e=this.psByCol["z"];
_3c=null;
}
var _3f=(_3e!=null?_3e.length:0);
if(_3f>0){
var _40=_3e[_3d];
if(_40){
var _41=_40.portlet;
if(this.createWindows){
_3a.ui.createPortletWindow(_41,_3c,_3a);
}
var _42=this.debugMsg;
if(_42!=null){
if(_42.length>0){
_42=_42+", ";
}
var _43=null;
if(_41.getProperty!=null){
_43=_41.getProperty(_3a.id.PP_WIDGET_ID);
}
if(!_43){
_43=_41.widgetId;
}
if(!_43){
_43=_41.toString();
}
if(_41.entityId){
_42=_42+_41.entityId+"("+_43+")";
if(this._dbPgLd&&_41.getProperty(_3a.id.PP_WINDOW_TITLE)){
_42=_42+" "+_41.getProperty(_3a.id.PP_WINDOW_TITLE);
}
}else{
_42=_42+_43;
}
}
_41.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}
}
}
},_evalNext:function(){
var _44=false;
var _45=this._colLen;
var _46=this._colIndex;
var _47=this._portletIndex;
var _48=_46;
var _49;
for(++_46;_46<=_45;_46++){
_49=this.psByCol[_46==_45?"z":_46.toString()];
if(_47<(_49!=null?_49.length:0)){
_44=true;
this._colIndex=_46;
break;
}
}
if(!_44){
++_47;
for(_46=0;_46<=_48;_46++){
_49=this.psByCol[_46==_45?"z":_46.toString()];
if(_47<(_49!=null?_49.length:0)){
_44=true;
this._colIndex=_46;
this._portletIndex=_47;
break;
}
}
}
return _44;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_4a){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _4c=_4a;
var _4d=null;
if(_4a&&_4a.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_4a.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_4a&&_4a.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_4a.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_4d=jetspeed.url.getQueryParameter(_4a,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_4c)){
_4c=null;
}
return {url:_4c,operation:op,portletEntityId:_4d};
},genPseudoUrl:function(_4e,_4f){
if(!_4e||!_4e.url||!_4e.portletEntityId){
return null;
}
var _50=null;
if(_4f){
_50=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_50="javascript:";
var _51=false;
if(_4e.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_50+="doAction(\"";
}else{
if(_4e.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_50+="doRender(\"";
}else{
_51=true;
}
}
if(_51){
return null;
}
_50+=_4e.url+"\",\""+_4e.portletEntityId+"\"";
_50+=")";
}
return _50;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_52,_53,_54){
var _55=null;
var _56=_53.portletDecorationsConfig;
if(_52&&_56){
_55=_56[_52];
}
if(_55==null&&!_54){
var _57=_53.portletDecorationsAllowed;
for(var i=0;i<_57.length;i++){
_52=_57[i];
_55=_56[_52];
if(_55!=null){
break;
}
}
}
if(_55!=null&&!_55._initialized){
var _59=jetspeed.prefs.getPortletDecorationBaseUrl(_52);
_55._initialized=true;
_55.cssPathCommon=new dojo.uri.Uri(_59+"/css/styles.css");
_55.cssPathDesktop=new dojo.uri.Uri(_59+"/css/desktop.css");
dojo.html.insertCssFile(_55.cssPathCommon,null,true);
dojo.html.insertCssFile(_55.cssPathDesktop,null,true);
}
return _55;
};
jetspeed.loadPortletDecorationConfig=function(_5a,_5b,_5c){
var _5d={};
_5b.portletDecorationsConfig[_5a]=_5d;
_5d.name=_5a;
_5d.windowActionButtonOrder=_5b.windowActionButtonOrder;
_5d.windowActionNotPortlet=_5b.windowActionNotPortlet;
_5d.windowActionButtonMax=_5b.windowActionButtonMax;
_5d.windowActionButtonTooltip=_5b.windowActionButtonTooltip;
_5d.windowActionMenuOrder=_5b.windowActionMenuOrder;
_5d.windowActionNoImage=_5b.windowActionNoImage;
_5d.windowIconEnabled=_5b.windowIconEnabled;
_5d.windowIconPath=_5b.windowIconPath;
_5d.windowTitlebar=_5b.windowTitlebar;
_5d.windowResizebar=_5b.windowResizebar;
_5d.dNodeClass=_5c.P_CLASS+" "+_5a+" "+_5c.PWIN_CLASS+" "+_5c.PWIN_CLASS+"-"+_5a;
_5d.cNodeClass=_5c.P_CLASS+" "+_5a+" "+_5c.PWIN_CLIENT_CLASS;
if(_5b.portletDecorationsProperties){
var _5e=_5b.portletDecorationsProperties[_5a];
if(_5e){
for(var _5f in _5e){
_5d[_5f]=_5e[_5f];
}
if(_5e.windowActionNoImage!=null){
var _60={};
for(var i=0;i<_5e.windowActionNoImage.length;i++){
_60[_5e.windowActionNoImage[i]]=true;
}
_5d.windowActionNoImage=_60;
}
if(_5e.windowIconPath!=null){
_5d.windowIconPath=dojo.string.trim(_5e.windowIconPath);
if(_5d.windowIconPath==null||_5d.windowIconPath.length==0){
_5d.windowIconPath=null;
}else{
var _62=_5d.windowIconPath;
var _63=_62.charAt(0);
if(_63!="/"){
_62="/"+_62;
}
var _64=_62.charAt(_62.length-1);
if(_64!="/"){
_62=_62+"/";
}
_5d.windowIconPath=_62;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
var _65=jetspeed;
_65.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _66=_65.page.getMenuNames();
for(var i=0;i<_66.length;i++){
var _68=_66[i];
var _69=dojo.widget.byId(_65.id.MENU_WIDGET_ID_PREFIX+_68);
if(_69){
_69.createJetspeedMenu(_65.page.getMenu(_68));
}
}
_65.url.loadingIndicatorHide();
_65.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_6a){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_6a);
}
};
jetspeed.menuNavClickWidget=function(_6b,_6c){
dojo.debug("jetspeed.menuNavClick");
if(!_6b){
return;
}
if(dojo.lang.isString(_6b)){
var _6d=_6b;
_6b=dojo.widget.byId(_6d);
if(!_6b){
dojo.raise("Tab widget not found: "+_6d);
}
}
if(_6b){
var _6e=_6b.jetspeedmenuname;
if(!_6e&&_6b.extraArgs){
_6e=_6b.extraArgs.jetspeedmenuname;
}
if(!_6e){
dojo.raise("Tab widget is invalid: "+_6b.widgetId);
}
var _6f=jetspeed.page.getMenu(_6e);
if(!_6f){
dojo.raise("Tab widget "+_6b.widgetId+" no menu: "+_6e);
}
var _70=_6f.getOptionByIndex(_6c);
jetspeed.menuNavClick(_70);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_71,_72,_73){
if(!_71||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _73=="undefined"){
_73=false;
}
if(!_73&&jetspeed.page&&jetspeed.page.equalsPageUrl(_71)){
return;
}
_71=jetspeed.page.makePageUrl(_71);
if(_72=="top"){
top.location.href=_71;
}else{
if(_72=="parent"){
parent.location.href=_71;
}else{
window.location.href=_71;
}
}
};
jetspeed.getActionsForPortlet=function(_74){
if(_74==null){
return;
}
jetspeed.getActionsForPortlets([_74]);
};
jetspeed.getActionsForPortlets=function(_75){
if(_75==null){
_75=jetspeed.page.getPortletIds();
}
var _76=new jetspeed.om.PortletActionsCL(_75);
var _77="?action=getactions";
for(var i=0;i<_75.length;i++){
_77+="&id="+_75[i];
}
var _79=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_77;
var _7a="text/xml";
var _7b=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_79,mimetype:_7a},_76,_7b,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_7c,_7d,_7e,_7f){
if(_7c==null){
return;
}
if(_7f==null){
_7f=new jetspeed.om.PortletChangeActionCL(_7c);
}
var _80="?action=window&id="+(_7c!=null?_7c:"");
if(_7d!=null){
_80+="&state="+_7d;
}
if(_7e!=null){
_80+="&mode="+_7e;
}
var _81=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_80;
var _82="text/xml";
var _83=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_81,mimetype:_82},_7f,_83,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_84){
var _85=_84.page;
if(!_85.editMode){
var _86=_84.css;
var _87=true;
var _88=_84.url.getQueryParameter(window.location.href,_84.id.PORTAL_ORIGINATE_PARAMETER);
if(_88!=null&&_88=="true"){
_87=false;
}
_85.editMode=true;
var _89=dojo.widget.byId(_84.id.PG_ED_WID);
if(_84.UAie6){
_85.displayAllPWins(true);
}
if(_89==null){
try{
_84.url.loadingIndicatorShow("loadpageeditor",true);
_89=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_84.id.PG_ED_WID,editorInitiatedFromDesktop:_87});
var _8a=document.getElementById(_84.id.COLUMNS);
_8a.insertBefore(_89.domNode,_8a.firstChild);
}
catch(e){
_84.url.loadingIndicatorHide();
if(_84.UAie6){
_85.displayAllPWins();
}
}
}else{
_89.editPageShow();
}
if(_87){
var _8b=_85.portlet_windows;
for(var _8c in _8b){
var _8d=_8b[_8c];
if(_8d){
_8d.editPageInitiate(_84,_86);
}
}
}
_85.syncPageControls(_84);
}
};
jetspeed.editPageTerminate=function(_8e){
var _8f=_8e.page;
if(_8f.editMode){
var _90=_8e.css;
var _91=dojo.widget.byId(_8e.id.PG_ED_WID);
_91.editMoveModeExit();
_8f.editMode=false;
if(!_91.editorInitiatedFromDesktop){
var _92=_8f.getPageUrl(true);
_92=_8e.url.removeQueryParameter(_92,_8e.id.PG_ED_PARAM);
_92=_8e.url.removeQueryParameter(_92,_8e.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_92;
}else{
var _93=_8e.url.getQueryParameter(window.location.href,_8e.id.PG_ED_PARAM);
if(_93!=null&&_93=="true"){
var _94=window.location.href;
_94=_8e.url.removeQueryParameter(_94,_8e.id.PG_ED_PARAM);
window.location.href=_94;
}else{
if(_91!=null){
_91.editPageHide();
}
_8f.syncPageControls(_8e);
}
var _95=_8f.portlet_windows;
for(var _96 in _95){
var _97=_95[_96];
if(_97){
_97.editPageTerminate(_90);
}
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_98,_99,_9a,_9b){
if(!_98){
_98={};
}
jetspeed.url.retrieveContent(_98,_99,_9a,_9b);
}};
jetspeed.om.PageCLCreateWidget=function(_9c){
if(typeof _9c=="undefined"){
_9c=false;
}
this.isPageUpdate=_9c;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_9d,_9e,_9f){
_9f.loadFromPSML(_9d,this.isPageUpdate);
},notifyFailure:function(_a0,_a1,_a2,_a3){
dojo.raise("PageCLCreateWidget error url: "+_a2+" type: "+_a0+jetspeed.formatError(_a1));
}};
jetspeed.om.Page=function(_a4,_a5,_a6,_a7,_a8,_a9){
if(_a4!=null&&_a5!=null){
this.requiredLayoutDecorator=_a4;
this.setPsmlPathFromDocumentUrl(_a5);
this.pageUrlFallback=_a5;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _a6!="undefined"){
this.addToHistory=_a6;
}
if(typeof _a7!="undefined"){
this.editMode=_a7;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets=[];
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_a9!=null){
this.iframeCoverByWinId=_a9;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_a8!=null){
this.tooltipMgr=_a8;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _aa=(this.name!=null&&this.name.length>0?this.name:null);
if(!_aa){
this.getPsmlUrl();
_aa=this.psmlPath;
}
return "page-"+_aa;
},setPsmlPathFromDocumentUrl:function(_ab){
var _ac=jetspeed;
var _ad=_ac.url.path.AJAX_API;
var _ae=null;
if(_ab==null){
_ae=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_ac.prefs.ajaxPageNavigation){
var _af=window.location.hash;
if(_af!=null&&_af.length>0){
if(_af.indexOf("#")==0){
_af=(_af.length>1?_af.substring(1):"");
}
if(_af!=null&&_af.length>1&&_af.indexOf("/")==0){
this.psmlPath=_ac.url.path.AJAX_API+_af;
return;
}
}
}
}else{
var _b0=_ac.url.parse(_ab);
_ae=_b0.path;
}
var _b1=_ac.url.path.DESKTOP;
var _b2=_ae.indexOf(_b1);
if(_b2!=-1&&_ae.length>(_b2+_b1.length)){
_ad=_ad+_ae.substring(_b2+_b1.length);
}
this.psmlPath=_ad;
},getPsmlUrl:function(){
var _b3=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _b4=_b3.url.basePortalUrl()+this.psmlPath;
if(_b3.prefs.printModeOnly!=null){
_b4=_b3.url.addQueryParameter(_b4,"layoutid",_b3.prefs.printModeOnly.layout);
_b4=_b3.url.addQueryParameter(_b4,"entity",_b3.prefs.printModeOnly.entity).toString();
}
return _b4;
},retrievePsml:function(_b5){
var _b6=jetspeed;
if(_b5==null){
_b5=new _b6.om.PageCLCreateWidget();
}
var _b7=this.getPsmlUrl();
var _b8="text/xml";
if(_b6.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_b7);
}
_b6.url.retrieveContent({url:_b7,mimetype:_b8},_b5,this,_b6.debugContentDumpIds);
},loadFromPSML:function(_b9,_ba){
var _bb=jetspeed;
var _bc=_bb.prefs;
var _bd=dojo;
var _be=_bc.printModeOnly;
if(djConfig.isDebug&&_bb.debug.profile&&_be==null){
_bd.profile.start("loadFromPSML");
}
var _bf=this._parsePSML(_b9);
if(_bf==null){
return;
}
this.portletsByPageColumn={};
var _c0={};
if(this.portletDecorator){
_c0[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_bf,null,this.portletsByPageColumn,true,_c0,_bd,_bb);
this.rootFragmentId=_bf.id;
var _c1=false;
if(this.editMode){
this.editMode=false;
if(_be==null){
_c1=true;
}
}
for(var _c2 in _c0){
_bb.loadPortletDecorationStyles(_c2,_bc,true);
}
if(_bc.windowTiling){
this._createColsStart(document.getElementById(_bb.id.DESKTOP),_bb.id.COLUMNS);
}
this.createLayoutInfo(_bb);
var _c3=this.portletsByPageColumn["z"];
if(_c3){
_c3.sort(this._loadPortletZIndexCompare);
}
var _c4=new _bb.PortletRenderer(true,true,_ba,null,true);
_c4.renderAllTimeDistribute();
},loadPostRender:function(_c5){
var _c6=jetspeed;
var _c7=_c6.prefs.printModeOnly;
if(_c7==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
var _c8=false;
if(this.editMode){
_c8=true;
}
var _c9=_c6.url.getQueryParameter(window.location.href,_c6.id.PG_ED_PARAM);
if(_c8||(_c9!=null&&_c9=="true")||this.actions[_c6.id.ACT_VIEW]!=null){
_c8=false;
if(this.actions!=null&&(this.actions[_c6.id.ACT_EDIT]!=null||this.actions[_c6.id.ACT_VIEW]!=null)){
_c8=true;
}
}
this.retrieveMenuDeclarations(true,_c8,_c5);
this.renderPageControls(_c6);
this.syncPageControls(_c6);
}else{
for(var _ca in this.portlets){
var _cb=this.portlets[_ca];
if(_cb!=null){
_cb.renderAction(null,_c7.action);
}
break;
}
if(_c5){
_c6.updatePageEnd();
}
}
if(_c6.UAie6){
_c6.ui.evtConnect("after",window,"onresize",_c6.ui.windowResizeMgr,"onResize",dojo.event);
_c6.ui.windowResizeMgr.onResizeDelayedCompare();
}
var _cc,_cd=this.columns;
if(_cd){
for(var i=0;i<_cd.length;i++){
_cc=_cd[i].domNode;
if(!_cc.childNodes||_cc.childNodes.length==0){
_cc.style.height="1px";
}
}
}
var _cf=this.maximizedOnInit;
if(_cf!=null){
var _d0=this.getPWin(_cf);
if(_d0==null){
dojo.raise("Cannot identify window to maximize");
}else{
dojo.lang.setTimeout(_d0,_d0._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_c6.url,_c6.url.loadingIndicatorStepPreload,1800);
},_parsePSML:function(_d1){
var _d2=jetspeed;
var _d3=dojo;
var _d4=_d1.getElementsByTagName("page");
if(!_d4||_d4.length>1||_d4[0]==null){
_d3.raise("Expected one <page> in PSML");
}
var _d5=_d4[0];
var _d6=_d5.childNodes;
var _d7=new RegExp("(name|path|profiledPath|title|short-title)");
var _d8=null;
var _d9={};
for(var i=0;i<_d6.length;i++){
var _db=_d6[i];
if(_db.nodeType!=1){
continue;
}
var _dc=_db.nodeName;
if(_dc=="fragment"){
_d8=_db;
}else{
if(_dc=="defaults"){
this.layoutDecorator=_db.getAttribute("layout-decorator");
var _dd=_db.getAttribute("portlet-decorator");
var _de=_d2.prefs.portletDecorationsAllowed;
if(!_de||_d3.lang.indexOf(_de,_dd)==-1){
_dd=_d2.prefs.windowDecoration;
}
this.portletDecorator=_dd;
}else{
if(_dc&&_dc.match(_d7)){
if(_dc=="short-title"){
_dc="shortTitle";
}
this[_dc]=((_db&&_db.firstChild)?_db.firstChild.nodeValue:null);
}else{
if(_dc=="action"){
this._parsePSMLAction(_db,_d9);
}
}
}
}
}
this.actions=_d9;
if(_d8==null){
_d3.raise("No root fragment in PSML");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_d2.debug.ajaxPageNav){
_d3.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_d2.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _df=this.getPageUrl();
_d3.undo.browser.addToHistory({back:function(){
if(_d2.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_df);
}
_d2.updatePage(_df,true);
},forward:function(){
if(_d2.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_df);
}
_d2.updatePage(_df,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_d2.prefs.ajaxPageNavigation){
var _df=this.getPageUrl();
_d3.undo.browser.setInitialState({back:function(){
if(_d2.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_df);
}
_d2.updatePage(_df,true);
},forward:function(){
if(_d2.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_df);
}
_d2.updatePage(_df,true);
},changeUrl:escape(this.getPath())});
}
}
var _e0=this._parsePSMLFrag(_d8,0);
return _e0;
},_parsePSMLFrag:function(_e1,_e2){
var _e3=jetspeed;
var _e4=new Array();
var _e5=((_e1!=null)?_e1.getAttribute("type"):null);
if(_e5!="layout"){
dojo.raise("Expected layout fragment: "+_e1);
return null;
}
var _e6=false;
var _e7=_e1.getAttribute("name");
if(_e7!=null){
_e7=_e7.toLowerCase();
if(_e7.indexOf("noactions")!=-1){
_e6=true;
}
}
var _e8=null,_e9=0;
var _ea={};
var _eb=_e1.childNodes;
var _ec,_ed,_ee,_ef,_f0;
for(var i=0;i<_eb.length;i++){
_ec=_eb[i];
if(_ec.nodeType!=1){
continue;
}
_ed=_ec.nodeName;
if(_ed=="fragment"){
_f0=_ec.getAttribute("type");
if(_f0=="layout"){
var _f2=this._parsePSMLFrag(_ec,i);
if(_f2!=null){
_e4.push(_f2);
}
}else{
var _f3=this._parsePSMLProps(_ec,null);
var _f4=_f3[_e3.id.PP_WINDOW_ICON];
if(_f4==null||_f4.length==0){
_f4=this._parsePSMLChildOrAttr(_ec,"icon");
if(_f4!=null&&_f4.length>0){
_f3[_e3.id.PP_WINDOW_ICON]=_f4;
}
}
_e4.push({id:_ec.getAttribute("id"),type:_f0,name:_ec.getAttribute("name"),properties:_f3,actions:this._parsePSMLActions(_ec,null),currentActionState:this._parsePSMLChildOrAttr(_ec,"state"),currentActionMode:this._parsePSMLChildOrAttr(_ec,"mode"),decorator:_ec.getAttribute("decorator"),layoutActionsDisabled:_e6,documentOrderIndex:i});
}
}else{
if(_ed=="property"){
if(this._parsePSMLProp(_ec,_ea)=="sizes"){
if(_e8!=null){
dojo.raise("Layout fragment has multiple sizes definitions: "+_e1);
return null;
}
if(_e3.prefs.printModeOnly!=null){
_e8=["100"];
_e9=100;
}else{
_ef=_ec.getAttribute("value");
if(_ef!=null&&_ef.length>0){
_e8=_ef.split(",");
for(var j=0;j<_e8.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_e8[j]=_e8[j].replace(re,"$1");
_e9+=new Number(_e8[j]);
}
}
}
}
}
}
}
_e4.sort(this._fragmentRowCompare);
if(_e8==null){
_e8=new Array();
_e8.push("100");
_e9=100;
}
return {id:_e1.getAttribute("id"),type:_e5,name:_e1.getAttribute("name"),decorator:_e1.getAttribute("decorator"),columnSizes:_e8,columnSizesSum:_e9,properties:_ea,fragments:_e4,layoutActionsDisabled:_e6,documentOrderIndex:_e2};
},_parsePSMLActions:function(_f7,_f8){
if(_f8==null){
_f8={};
}
var _f9=_f7.getElementsByTagName("action");
for(var _fa=0;_fa<_f9.length;_fa++){
var _fb=_f9[_fa];
this._parsePSMLAction(_fb,_f8);
}
return _f8;
},_parsePSMLAction:function(_fc,_fd){
var _fe=_fc.getAttribute("id");
if(_fe!=null){
var _ff=_fc.getAttribute("type");
var _100=_fc.getAttribute("name");
var _101=_fc.getAttribute("url");
var _102=_fc.getAttribute("alt");
_fd[_fe.toLowerCase()]={id:_fe,type:_ff,label:_100,url:_101,alt:_102};
}
},_parsePSMLChildOrAttr:function(_103,_104){
var _105=null;
var _106=_103.getElementsByTagName(_104);
if(_106!=null&&_106.length==1&&_106[0].firstChild!=null){
_105=_106[0].firstChild.nodeValue;
}
if(!_105){
_105=_103.getAttribute(_104);
}
if(_105==null||_105.length==0){
_105=null;
}
return _105;
},_parsePSMLProps:function(_107,_108){
if(_108==null){
_108={};
}
var _109=_107.getElementsByTagName("property");
for(var _10a=0;_10a<_109.length;_10a++){
this._parsePSMLProp(_109[_10a],_108);
}
return _108;
},_parsePSMLProp:function(_10b,_10c){
var _10d=_10b.getAttribute("name");
var _10e=_10b.getAttribute("value");
_10c[_10d]=_10e;
return _10d;
},_fragmentRowCompare:function(_10f,_110){
var rowA=_10f.documentOrderIndex*1000;
var rowB=_110.documentOrderIndex*1000;
var _113=_10f.properties["row"];
if(_113!=null){
rowA=_113;
}
var _114=_110.properties["row"];
if(_114!=null){
rowB=_114;
}
return (rowA-rowB);
},_layoutCreateModel:function(_115,_116,_117,_118,_119,_11a,_11b){
var jsId=_11b.id;
var _11d=this.columns.length;
var _11e=this._layoutCreateColsModel(_115,_116,_118);
var _11f=_11e.columnsInLayout;
if(_11e.addedLayoutHeaderColumn){
_11d++;
}
var _120=(_11f==null?0:_11f.length);
var _121=new Array(_120);
var _122=new Array(_120);
for(var i=0;i<_115.fragments.length;i++){
var _124=_115.fragments[i];
if(_124.type=="layout"){
var _125=i;
var _125=(_124.properties?_124.properties[_11b.id.PP_COLUMN]:i);
if(_125==null||_125<0||_125>=_120){
_125=(_120>0?(_120-1):0);
}
_122[_125]=true;
this._layoutCreateModel(_124,_11f[_125],_117,false,_119,_11a,_11b);
}else{
this._layoutCreatePortlet(_124,_115,_11f,_11d,_117,_121,_119,_11a,_11b);
}
}
return _11f;
},_layoutCreatePortlet:function(_126,_127,_128,_129,_12a,_12b,_12c,_12d,_12e){
if(_126&&_12e.debugPortletEntityIdFilter){
if(!_12d.lang.inArray(_12e.debugPortletEntityIdFilter,_126.id)){
_126=null;
}
}
if(_126){
var _12f="z";
var _130=_126.properties[_12e.id.PP_DESKTOP_EXTENDED];
var _131=_12e.prefs.windowTiling;
var _132=_131;
var _133=_12e.prefs.windowHeightExpand;
if(_130!=null&&_131&&_12e.prefs.printModeOnly==null){
var _134=_130.split(_12e.id.PP_PAIR_SEPARATOR);
var _135=null,_136=0,_137=null,_138=null,_139=false;
if(_134!=null&&_134.length>0){
var _13a=_12e.id.PP_PROP_SEPARATOR;
for(var _13b=0;_13b<_134.length;_13b++){
_135=_134[_13b];
_136=((_135!=null)?_135.length:0);
if(_136>0){
var _13c=_135.indexOf(_13a);
if(_13c>0&&_13c<(_136-1)){
_137=_135.substring(0,_13c);
_138=_135.substring(_13c+1);
_139=((_138=="true")?true:false);
if(_137==_12e.id.PP_STATICPOS){
_132=_139;
}else{
if(_137==_12e.id.PP_FITHEIGHT){
_133=_139;
}
}
}
}
}
}
}else{
if(!_131){
_132=false;
}
}
_126.properties[_12e.id.PP_WINDOW_POSITION_STATIC]=_132;
_126.properties[_12e.id.PP_WINDOW_HEIGHT_TO_FIT]=_133;
if(_132&&_131){
var _13d=_126.properties[_12e.id.PP_COLUMN];
if(_13d==null||_13d==""||_13d<0){
var _13e=-1;
for(var j=0;j<_128.length;j++){
var _140=(_12b[j]?_12b[j].length:0);
if(_13e==-1||_140<_13e){
_13e=_140;
_13d=j;
}
}
}else{
if(_13d>=_128.length){
_13d=_128.length-1;
}
}
if(_12b[_13d]==null){
_12b[_13d]=new Array();
}
_12b[_13d].push(_126.id);
var _141=_129+new Number(_13d);
_12f=_141.toString();
}
if(_126.currentActionState==_12e.id.ACT_MAXIMIZE){
this.maximizedOnInit=_126.id;
}
var _142=_126.decorator;
if(_142!=null&&_142.length>0){
if(_12d.lang.indexOf(_12e.prefs.portletDecorationsAllowed,_142)==-1){
_142=null;
}
}
if(_142==null||_142.length==0){
if(djConfig.isDebug&&_12e.debug.windowDecorationRandom){
_142=_12e.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_12e.prefs.portletDecorationsAllowed.length)];
}else{
_142=this.portletDecorator;
}
}
var _143=_126.properties||{};
_143[_12e.id.PP_WINDOW_DECORATION]=_142;
_12c[_142]=true;
var _144=_126.actions||{};
var _145=new _12e.om.Portlet(_126.name,_126.id,null,_143,_144,_126.currentActionState,_126.currentActionMode,_126.layoutActionsDisabled);
_145.initialize();
this.putPortlet(_145);
if(_12a[_12f]==null){
_12a[_12f]=new Array();
}
_12a[_12f].push({portlet:_145,layout:_127.id});
}
},_layoutCreateColsModel:function(_146,_147,_148){
var _149=jetspeed;
this.layouts[_146.id]=_146;
var _14a=false;
var _14b=new Array();
if(_149.prefs.windowTiling&&_146.columnSizes.length>0){
var _14c=false;
if(_149.UAie){
_14c=true;
}
if(_147!=null&&!_148){
var _14d=new _149.om.Column(0,_146.id,(_14c?_146.columnSizesSum-0.1:_146.columnSizesSum),this.columns.length,_146.layoutActionsDisabled);
_14d.layoutHeader=true;
this.columns.push(_14d);
if(_147.buildColChildren==null){
_147.buildColChildren=new Array();
}
_147.buildColChildren.push(_14d);
_147=_14d;
_14a=true;
}
for(var i=0;i<_146.columnSizes.length;i++){
var size=_146.columnSizes[i];
if(_14c&&i==(_146.columnSizes.length-1)){
size=size-0.1;
}
var _150=new _149.om.Column(i,_146.id,size,this.columns.length,_146.layoutActionsDisabled);
this.columns.push(_150);
if(_147!=null){
if(_147.buildColChildren==null){
_147.buildColChildren=new Array();
}
_147.buildColChildren.push(_150);
}
_14b.push(_150);
}
}
return {columnsInLayout:_14b,addedLayoutHeaderColumn:_14a};
},_portletsInitWinState:function(_151){
var _152={};
this.getPortletCurColRow(null,false,_152);
for(var _153 in this.portlets){
var _154=this.portlets[_153];
var _155=_152[_154.getId()];
if(_155==null&&_151){
for(var i=0;i<_151.length;i++){
if(_151[i].portlet.getId()==_154.getId()){
_155={layout:_151[i].layout};
break;
}
}
}
if(_155!=null){
_154._initWinState(_155,false);
}else{
dojo.raise("Window state data not found for portlet: "+_154.getId());
}
}
},_loadPortletZIndexCompare:function(_157,_158){
var _159=null;
var _15a=null;
var _15b=null;
_159=_157.portlet._getInitialZIndex();
_15a=_158.portlet._getInitialZIndex();
if(_159&&!_15a){
return -1;
}else{
if(_15a&&!_159){
return 1;
}else{
if(_159==_15a){
return 0;
}
}
}
return (_159-_15a);
},_createColsStart:function(_15c,_15d){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _15e=document.createElement("div");
_15e.id=_15d;
_15e.setAttribute("id",_15d);
for(var _15f=0;_15f<this.columnsStructure.length;_15f++){
var _160=this.columnsStructure[_15f];
this._createCols(_160,_15e);
}
_15c.appendChild(_15e);
},_createCols:function(_161,_162){
_161.createColumn();
if(this.colFirstNormI==-1&&!_161.columnContainer&&!_161.layoutHeader){
this.colFirstNormI=_161.getPageColumnIndex();
}
var _163=_161.buildColChildren;
if(_163!=null&&_163.length>0){
for(var _164=0;_164<_163.length;_164++){
this._createCols(_163[_164],_161.domNode);
}
}
delete _161.buildColChildren;
_162.appendChild(_161.domNode);
},_removeCols:function(_165){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_165){
var _167=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_167,function(_168){
_165.appendChild(_168);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _169=dojo.byId(jetspeed.id.COLUMNS);
if(_169){
dojo.dom.removeNode(_169);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnEmptyCheck:function(_16a){
if(!_16a||!_16a.getAttribute){
return;
}
var _16b=_16a.getAttribute("columnindex");
if(!_16b||_16b.length==0){
return;
}
var _16c=_16a.getAttribute("layoutid");
if(_16c==null||_16c.length==0){
var _16d=_16a.childNodes;
if(!_16d||_16d.length==0){
_16a.style.height="1px";
}else{
_16a.style.height="";
}
}
},getPortletCurColRow:function(_16e,_16f,_170){
if(!this.columns||this.columns.length==0){
return null;
}
var _171=null;
var _172=((_16e!=null)?true:false);
var _173=0;
var _174=null;
var _175=null;
var _176=0;
var _177=false;
for(var _178=0;_178<this.columns.length;_178++){
var _179=this.columns[_178];
var _17a=_179.domNode.childNodes;
if(_175==null||_175!=_179.getLayoutId()){
_175=_179.getLayoutId();
_174=this.layouts[_175];
if(_174==null){
dojo.raise("Layout not found: "+_175);
return null;
}
_176=0;
_177=false;
if(_174.clonedFromRootId==null){
_177=true;
}else{
var _17b=this.getColFromColNode(_179.domNode.parentNode);
if(_17b==null){
dojo.raise("Parent column not found: "+_179);
return null;
}
_179=_17b;
}
}
var _17c=null;
var _17d=jetspeed;
var _17e=dojo;
var _17f=_17d.id.PWIN_CLASS;
if(_16f){
_17f+="|"+_17d.id.PWIN_GHOST_CLASS;
}
if(_172){
_17f+="|"+_17d.id.COL_CLASS;
}
var _180=new RegExp("(^|\\s+)("+_17f+")(\\s+|$)");
for(var _181=0;_181<_17a.length;_181++){
var _182=_17a[_181];
if(_180.test(_17e.html.getClass(_182))){
_17c=(_17c==null?0:_17c+1);
if((_17c+1)>_176){
_176=(_17c+1);
}
if(_16e==null||_182==_16e){
var _183={layout:_175,column:_179.getLayoutColumnIndex(),row:_17c,columnObj:_179};
if(!_177){
_183.layout=_174.clonedFromRootId;
}
if(_16e!=null){
_171=_183;
break;
}else{
if(_170!=null){
var _184=this.getPWinFromNode(_182);
if(_184==null){
_17e.raise("PortletWindow not found for node");
}else{
var _185=_184.portlet;
if(_185==null){
_17e.raise("PortletWindow for node has null portlet: "+_184.widgetId);
}else{
_170[_185.getId()]=_183;
}
}
}
}
}
}
}
if(_171!=null){
break;
}
}
return _171;
},_getPortletArrayByZIndex:function(){
var _186=jetspeed;
var _187=this.getPortletArray();
if(!_187){
return _187;
}
var _188=[];
for(var i=0;i<_187.length;i++){
if(!_187[i].getProperty(_186.id.PP_WINDOW_POSITION_STATIC)){
_188.push(_187[i]);
}
}
_188.sort(this._portletZIndexCompare);
return _188;
},_portletZIndexCompare:function(_18a,_18b){
var _18c=null;
var _18d=null;
var _18e=null;
_18e=_18a.getSavedWinState();
_18c=_18e.zIndex;
_18e=_18b.getSavedWinState();
_18d=_18e.zIndex;
if(_18c&&!_18d){
return -1;
}else{
if(_18d&&!_18c){
return 1;
}else{
if(_18c==_18d){
return 0;
}
}
}
return (_18c-_18d);
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _18f=[];
for(var _190 in this.portlets){
var _191=this.portlets[_190];
_18f.push(_191);
}
return _18f;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _192=[];
for(var _193 in this.portlets){
var _194=this.portlets[_193];
_192.push(_194.getId());
}
return _192;
},getPortletByName:function(_195){
if(this.portlets&&_195){
for(var _196 in this.portlets){
var _197=this.portlets[_196];
if(_197.name==_195){
return _197;
}
}
}
return null;
},getPortlet:function(_198){
if(this.portlets&&_198){
return this.portlets[_198];
}
return null;
},getPWinFromNode:function(_199){
var _19a=null;
if(this.portlets&&_199){
for(var _19b in this.portlets){
var _19c=this.portlets[_19b];
var _19d=_19c.getPWin();
if(_19d!=null){
if(_19d.domNode==_199){
_19a=_19d;
break;
}
}
}
}
return _19a;
},putPortlet:function(_19e){
if(!_19e){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_19e.entityId]=_19e;
this.portlet_count++;
},putPWin:function(_19f){
if(!_19f){
return;
}
var _1a0=_19f.widgetId;
if(!_1a0){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_1a0]=_19f;
this.portlet_window_count++;
},getPWin:function(_1a1){
if(this.portlet_windows&&_1a1){
var pWin=this.portlet_windows[_1a1];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_1a1];
if(pWin==null){
var p=this.getPortlet(_1a1);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_1a5){
var _1a6=this.portlet_windows;
var pWin;
var _1a8=[];
for(var _1a9 in _1a6){
pWin=_1a6[_1a9];
if(pWin&&(!_1a5||pWin.portlet)){
_1a8.push(pWin);
}
}
return _1a8;
},getPWinTopZIndex:function(_1aa){
var _1ab=0;
if(_1aa){
_1ab=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_1ab;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_1ab=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_1ab;
}
return _1ab;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_1ac,_1ad){
return;
},onBrowserWindowResize:function(){
var _1ae=jetspeed;
if(_1ae.UAie6){
var _1af=this.portlet_windows;
var pWin;
for(var _1b1 in _1af){
pWin=_1af[_1b1];
pWin.onBrowserWindowResize();
}
if(this.editMode){
var _1b2=dojo.widget.byId(_1ae.id.PG_ED_WID);
if(_1b2!=null){
_1b2.onBrowserWindowResize();
}
}
}
},regPWinIFrameCover:function(_1b3){
if(!_1b3){
return;
}
this.iframeCoverByWinId[_1b3.widgetId]=true;
},unregPWinIFrameCover:function(_1b4){
if(!_1b4){
return;
}
delete this.iframeCoverByWinId[_1b4.widgetId];
},displayAllPWinIFrameCovers:function(_1b5,_1b6){
var _1b7=this.portlet_windows;
var _1b8=this.iframeCoverByWinId;
if(!_1b7||!_1b8){
return;
}
for(var _1b9 in _1b8){
if(_1b9==_1b6){
continue;
}
var pWin=_1b7[_1b9];
var _1bb=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_1bb){
_1bb.style.display=(_1b5?"none":"block");
}
}
},createLayoutInfo:function(_1bc){
var _1bd=dojo;
var _1be=null;
var _1bf=null;
var _1c0=null;
var _1c1=null;
var _1c2=document.getElementById(_1bc.id.DESKTOP);
if(_1c2!=null){
_1be=_1bc.ui.getLayoutExtents(_1c2,null,_1bd,_1bc);
}
var _1c3=document.getElementById(_1bc.id.COLUMNS);
if(_1c3!=null){
_1bf=_1bc.ui.getLayoutExtents(_1c3,null,_1bd,_1bc);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_1c1=_1bc.ui.getLayoutExtents(col.domNode,null,_1bd,_1bc);
}else{
if(!col.columnContainer){
_1c0=_1bc.ui.getLayoutExtents(col.domNode,null,_1bd,_1bc);
}
}
if(_1c0!=null&&_1c1!=null){
break;
}
}
}
this.layoutInfo={desktop:(_1be!=null?_1be:{}),columns:(_1bf!=null?_1bf:{}),column:(_1c0!=null?_1c0:{}),columnLayoutHeader:(_1c1!=null?_1c1:{})};
_1bc.widget.PortletWindow.prototype.colWidth_pbE=((_1c0&&_1c0.pbE)?_1c0.pbE.w:0);
},destroy:function(){
var _1c6=jetspeed;
var _1c7=dojo;
if(_1c6.UAie6){
_1c6.ui.evtDisconnect("after",window,"onresize",_1c6.ui.windowResizeMgr,"onResize",_1c7.event);
}
var _1c8=this.portlet_windows;
var _1c9=this.getPWins(true);
var pWin,_1cb;
for(var i=0;i<_1c9.length;i++){
pWin=_1c9[i];
_1cb=pWin.widgetId;
pWin.closeWindow();
delete _1c8[_1cb];
this.portlet_window_count--;
}
this.portlets=[];
this.portlet_count=0;
var _1cd=_1c7.widget.byId(_1c6.id.PG_ED_WID);
if(_1cd!=null){
_1cd.editPageDestroy();
}
this._removeCols(document.getElementById(_1c6.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_1ce){
if(_1ce==null){
return null;
}
var _1cf=_1ce.getAttribute("columnindex");
if(_1cf==null){
return null;
}
var _1d0=new Number(_1cf);
if(_1d0>=0&&_1d0<this.columns.length){
return this.columns[_1d0];
}
return null;
},getColIndexForNode:function(node){
var _1d2=null;
if(!this.columns){
return _1d2;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1d2=i;
break;
}
}
return _1d2;
},getColWithNode:function(node){
var _1d5=this.getColIndexForNode(node);
return ((_1d5!=null&&_1d5>=0)?this.columns[_1d5]:null);
},getDescendantCols:function(_1d6){
var dMap={};
if(_1d6==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1d6&&_1d6.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_1da){
if(!_1da){
return;
}
var _1db=(_1da.getName?_1da.getName():null);
if(_1db!=null){
this.menus[_1db]=_1da;
}
},getMenu:function(_1dc){
if(_1dc==null){
return null;
}
return this.menus[_1dc];
},removeMenu:function(_1dd){
if(_1dd==null){
return;
}
var _1de=null;
if(dojo.lang.isString(_1dd)){
_1de=_1dd;
}else{
_1de=(_1dd.getName?_1dd.getName():null);
}
if(_1de!=null){
delete this.menus[_1de];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1df=[];
for(var _1e0 in this.menus){
_1df.push(_1e0);
}
return _1df;
},retrieveMenuDeclarations:function(_1e1,_1e2,_1e3){
contentListener=new jetspeed.om.MenusApiCL(_1e1,_1e2,_1e3);
this.clearMenus();
var _1e4="?action=getmenus";
if(_1e1){
_1e4+="&includeMenuDefs=true";
}
var _1e5=this.getPsmlUrl()+_1e4;
var _1e6="text/xml";
var _1e7=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1e5,mimetype:_1e6},contentListener,_1e7,jetspeed.debugContentDumpIds);
},syncPageControls:function(_1e8){
var jsId=_1e8.id;
if(this.actionButtons==null){
return;
}
for(var _1ea in this.actionButtons){
var _1eb=false;
if(_1ea==jsId.ACT_EDIT){
if(!this.editMode){
_1eb=true;
}
}else{
if(_1ea==jsId.ACT_VIEW){
if(this.editMode){
_1eb=true;
}
}else{
if(_1ea==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_1eb=true;
}
}else{
_1eb=true;
}
}
}
if(_1eb){
this.actionButtons[_1ea].style.display="";
}else{
this.actionButtons[_1ea].style.display="none";
}
}
},renderPageControls:function(_1ec){
var _1ec=jetspeed;
var jsId=_1ec.id;
var _1ee=dojo;
var _1ef=[];
if(this.actions!=null){
for(var _1f0 in this.actions){
if(_1f0!=jsId.ACT_HELP){
_1ef.push(_1f0);
}
if(_1f0==jsId.ACT_EDIT){
_1ef.push(jsId.ACT_ADDPORTLET);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
if(this.actions[jsId.ACT_VIEW]==null){
_1ef.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
if(this.actions[jsId.ACT_EDIT]==null){
_1ef.push(jsId.ACT_EDIT);
}
}
}
var _1f1=_1ee.byId(jsId.PAGE_CONTROLS);
if(_1f1!=null&&_1ef!=null&&_1ef.length>0){
var _1f2=_1ec.prefs;
var jsUI=_1ec.ui;
var _1f4=_1ee.event;
var _1f5=_1ec.page.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _1f6=this.actionButtonTooltips;
for(var i=0;i<_1ef.length;i++){
var _1f0=_1ef[i];
var _1f8=document.createElement("div");
_1f8.className="portalPageActionButton";
_1f8.style.backgroundImage="url("+_1f2.getLayoutRootUrl()+"/images/desktop/"+_1f0+".gif)";
_1f8.actionName=_1f0;
this.actionButtons[_1f0]=_1f8;
_1f1.appendChild(_1f8);
jsUI.evtConnect("after",_1f8,"onclick",this,"pageActionButtonClick",_1f4);
if(_1f2.pageActionButtonTooltip){
var _1f9=null;
if(_1f2.desktopActionLabels!=null){
_1f9=_1f2.desktopActionLabels[_1f0];
}
if(_1f9==null||_1f9.length==0){
_1f9=_1ee.string.capitalize(_1f0);
}
_1f6.push(_1f5.addNode(_1f8,_1f9,true,null,null,null,_1ec,jsUI,_1f4));
}
}
}
},_destroyPageControls:function(){
var _1fa=jetspeed;
if(this.actionButtons){
for(var _1fb in this.actionButtons){
var _1fc=this.actionButtons[_1fb];
if(_1fc){
_1fa.ui.evtDisconnect("after",_1fc,"onclick",this,"pageActionButtonClick");
}
}
}
var _1fd=dojo.byId(_1fa.id.PAGE_CONTROLS);
if(_1fd!=null&&_1fd.childNodes&&_1fd.childNodes.length>0){
for(var i=(_1fd.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1fd.childNodes[i]);
}
}
_1fa.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_200){
var _201=jetspeed;
if(_200==null){
return;
}
if(_200==_201.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_200==_201.id.ACT_EDIT){
_201.editPageInitiate(_201);
}else{
if(_200==_201.id.ACT_VIEW){
_201.editPageTerminate(_201);
}else{
var _202=this.getPageAction(_200);
alert("pageAction "+_200+" : "+_202);
if(_202==null){
return;
}
if(_202.url==null){
return;
}
var _203=_201.url.basePortalUrl()+_201.url.path.DESKTOP+"/"+_202.url;
_201.pageNavigate(_203);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_205,_206){
var _207=jetspeed;
if(!_206){
_206=escape(this.getPagePathAndQuery());
}else{
_206=escape(_206);
}
var _208=_207.url.basePortalUrl()+_207.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_206;
if(_205!=null){
_208+="&jslayoutid="+escape(_205);
}
_207.changeActionForPortlet(this.rootFragmentId,null,_207.id.ACT_EDIT,new jetspeed.om.PageChangeActionCL(_208));
},setPageModePortletActions:function(_209){
if(_209==null||_209.actions==null){
return;
}
var jsId=jetspeed.id;
if(_209.actions[jsId.ACT_REMOVEPORTLET]==null){
_209.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_20b){
if(this.pageUrl!=null&&!_20b){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _20d=jsU.path.SERVER+((_20b)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _20e=jsU.parse(_20d);
var _20f=null;
if(this.pageUrlFallback!=null){
_20f=jsU.parse(this.pageUrlFallback);
}else{
_20f=jsU.parse(window.location.href);
}
if(_20e!=null&&_20f!=null){
var _210=_20f.query;
if(_210!=null&&_210.length>0){
var _211=_20e.query;
if(_211!=null&&_211.length>0){
_20d=_20d+"&"+_210;
}else{
_20d=_20d+"?"+_210;
}
}
}
if(!_20b){
this.pageUrl=_20d;
}
return _20d;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _213=this.getPath();
var _214=jsU.parse(_213);
var _215=null;
if(this.pageUrlFallback!=null){
_215=jsU.parse(this.pageUrlFallback);
}else{
_215=jsU.parse(window.location.href);
}
if(_214!=null&&_215!=null){
var _216=_215.query;
if(_216!=null&&_216.length>0){
var _217=_214.query;
if(_217!=null&&_217.length>0){
_213=_213+"&"+_216;
}else{
_213=_213+"?"+_216;
}
}
}
this.pagePathAndQuery=_213;
return _213;
},getPageDirectory:function(_218){
var _219="/";
var _21a=(_218?this.getRealPath():this.getPath());
if(_21a!=null){
var _21b=_21a.lastIndexOf("/");
if(_21b!=-1){
if((_21b+1)<_21a.length){
_219=_21a.substring(0,_21b+1);
}else{
_219=_21a;
}
}
}
return _219;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_21d){
if(!_21d){
_21d="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_21d)){
return jsU.path.SERVER+jsU.path.DESKTOP+_21d;
}
return _21d;
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
}});
jetspeed.om.Column=function(_21f,_220,size,_222,_223){
this.layoutColumnIndex=_21f;
this.layoutId=_220;
this.size=size;
this.pageColumnIndex=new Number(_222);
if(typeof _223!="undefined"){
this.layoutActionsDisabled=_223;
}
this.id="jscol_"+_222;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_224){
var _225=this.styleClass;
var _226=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_226>0){
_225+=" desktopColumnClear-PRIVATE";
}
var _227=document.createElement("div");
_227.setAttribute("columnindex",_226);
_227.style.width=this.size+"%";
if(this.layoutHeader){
_225=this.styleLayoutClass;
_227.setAttribute("layoutid",this.layoutId);
}
_227.className=_225;
_227.id=this.getId();
this.domNode=_227;
if(_224!=null){
_224.appendChild(_227);
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
}});
jetspeed.om.Portlet=function(_22a,_22b,_22c,_22d,_22e,_22f,_230,_231){
this.name=_22a;
this.entityId=_22b;
this.properties=_22d;
this.actions=_22e;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_22f;
this.currentActionMode=_230;
if(_22c){
this.contentRetriever=_22c;
}
this.layoutActionsDisabled=false;
if(typeof _231!="undefined"){
this.layoutActionsDisabled=_231;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _232=jetspeed;
var jsId=_232.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _234=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_232.prefs.windowTiling){
if(_234=="true"){
_234=true;
}else{
if(_234=="false"){
_234=false;
}else{
if(_234!=true&&_234!=false){
_234=true;
}
}
}
}else{
_234=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_234;
var _235=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_235=="true"){
_235=true;
}else{
if(_234=="false"){
_235=false;
}else{
if(_235!=true&&_235!=false){
_235=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_235;
var _236=this.properties[jsId.PP_WINDOW_TITLE];
if(!_236&&this.name){
var re=(/^[^:]*:*/);
_236=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_236;
}
},postParseAnnotateHtml:function(_238){
var _239=jetspeed;
var _23a=_239.portleturl;
if(_238){
var _23b=_238;
var _23c=_23b.getElementsByTagName("form");
var _23d=_239.debug.postParseAnnotateHtml;
var _23e=_239.debug.postParseAnnotateHtmlDisableAnchors;
if(_23c){
for(var i=0;i<_23c.length;i++){
var _240=_23c[i];
var _241=_240.action;
var _242=_23a.parseContentUrl(_241);
var _243=_242.operation;
if(_243==_23a.PORTLET_REQUEST_ACTION||_243==_23a.PORTLET_REQUEST_RENDER){
var _244=_23a.genPseudoUrl(_242,true);
_240.action=_244;
var _245=new _239.om.ActionRenderFormBind(_240,_242.url,_242.portletEntityId,_243);
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_243+") for form with action: "+_241);
}
}else{
if(_241==null||_241.length==0){
var _245=new _239.om.ActionRenderFormBind(_240,null,this.entityId,null);
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_241);
}
}
}
}
}
var _246=_23b.getElementsByTagName("a");
if(_246){
for(var i=0;i<_246.length;i++){
var _247=_246[i];
var _248=_247.href;
var _242=_23a.parseContentUrl(_248);
var _249=null;
if(!_23e){
_249=_23a.genPseudoUrl(_242);
}
if(!_249){
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_248);
}
}else{
if(_249==_248){
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_248);
}
}else{
if(_23d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_248+" with: "+_249);
}
_247.href=_249;
}
}
}
}
}
},getPWin:function(){
var _24a=jetspeed;
var _24b=this.properties[_24a.id.PP_WIDGET_ID];
if(_24b){
return _24a.page.getPWin(_24b);
}
return null;
},getCurWinState:function(_24c){
var _24d=null;
try{
var _24e=this.getPWin();
if(!_24e){
return null;
}
_24d=_24e.getCurWinStateForPersist(_24c);
if(!_24c){
if(_24d.layout==null){
_24d.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _24d;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_24f,_250){
var _251=jetspeed;
var jsId=_251.id;
if(!_24f){
_24f={};
}
var _253=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _254=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_24f[jsId.PP_WINDOW_POSITION_STATIC]=_253;
_24f[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_254;
var _255=this.properties["width"];
if(!_250&&_255!=null&&_255>0){
_24f.width=Math.floor(_255);
}else{
if(_250){
_24f.width=-1;
}
}
var _256=this.properties["height"];
if(!_250&&_256!=null&&_256>0){
_24f.height=Math.floor(_256);
}else{
if(_250){
_24f.height=-1;
}
}
if(!_253||!_251.prefs.windowTiling){
var _257=this.properties["x"];
if(!_250&&_257!=null&&_257>=0){
_24f.left=Math.floor(((_257>0)?_257:0));
}else{
if(_250){
_24f.left=-1;
}
}
var _258=this.properties["y"];
if(!_250&&_258!=null&&_258>=0){
_24f.top=Math.floor(((_258>0)?_258:0));
}else{
_24f.top=-1;
}
var _259=this._getInitialZIndex(_250);
if(_259!=null){
_24f.zIndex=_259;
}
}
return _24f;
},_initWinState:function(_25a,_25b){
var _25c=jetspeed;
var _25d=(_25a?_25a:{});
this.getInitialWinDims(_25d,_25b);
if(_25c.debug.initWinState){
var _25e=this.properties[_25c.id.PP_WINDOW_POSITION_STATIC];
if(!_25e||!_25c.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_25d.zIndex+" x="+_25d.left+" y="+_25d.top+" width="+_25d.width+" height="+_25d.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_25d.column+" row="+_25d.row+" width="+_25d.width+" height="+_25d.height);
}
}
this.lastSavedWindowState=_25d;
return _25d;
},_getInitialZIndex:function(_25f){
var _260=null;
var _261=this.properties["z"];
if(!_25f&&_261!=null&&_261>=0){
_260=Math.floor(_261);
}else{
if(_25f){
_260=-1;
}
}
return _260;
},_getChangedWindowState:function(_262){
var jsId=jetspeed.id;
var _264=this.getSavedWinState();
if(_264&&dojo.lang.isEmpty(_264)){
_264=null;
_262=false;
}
var _265=this.getCurWinState(_262);
var _266=_265[jsId.PP_WINDOW_POSITION_STATIC];
var _267=!_266;
if(!_264){
var _268={state:_265,positionChanged:true,extendedPropChanged:true};
if(_267){
_268.zIndexChanged=true;
}
return _268;
}
var _269=false;
var _26a=false;
var _26b=false;
var _26c=false;
for(var _26d in _265){
if(_265[_26d]!=_264[_26d]){
if(_26d==jsId.PP_WINDOW_POSITION_STATIC||_26d==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_269=true;
_26b=true;
_26a=true;
}else{
if(_26d=="zIndex"){
if(_267){
_269=true;
_26c=true;
}
}else{
_269=true;
_26a=true;
}
}
}
}
if(_269){
var _268={state:_265,positionChanged:_26a,extendedPropChanged:_26b};
if(_267){
_268.zIndexChanged=_26c;
}
return _268;
}
return null;
},getPortletUrl:function(_26e){
var _26f=jetspeed;
var _270=_26f.url;
var _271=null;
if(_26e&&_26e.url){
_271=_26e.url;
}else{
if(_26e&&_26e.formNode){
var _272=_26e.formNode.getAttribute("action");
if(_272){
_271=_272;
}
}
}
if(_271==null){
_271=_270.basePortalUrl()+_270.path.PORTLET+_26f.page.getPath();
}
if(!_26e.dontAddQueryArgs){
_271=_270.parse(_271);
_271=_270.addQueryParameter(_271,"entity",this.entityId,true);
_271=_270.addQueryParameter(_271,"portlet",this.name,true);
_271=_270.addQueryParameter(_271,"encoder","desktop",true);
if(_26e.jsPageUrl!=null){
var _273=_26e.jsPageUrl.query;
if(_273!=null&&_273.length>0){
_271=_271.toString()+"&"+_273;
}
}
}
if(_26e){
_26e.url=_271.toString();
}
return _271;
},_submitAjaxApi:function(_274,_275,_276){
var _277=jetspeed;
var _278="?action="+_274+"&id="+this.entityId+_275;
var _279=_277.url.basePortalUrl()+_277.url.path.AJAX_API+_277.page.getPath()+_278;
var _27a="text/xml";
var _27b=new _277.om.Id(_274,this.entityId);
_27b.portlet=this;
_277.url.retrieveContent({url:_279,mimetype:_27a},_276,_27b,_277.debugContentDumpIds);
},submitWinState:function(_27c,_27d){
var _27e=jetspeed;
var jsId=_27e.id;
if(!_27e.page.getPageAction(jsId.ACT_EDIT)){
return;
}
var _280=null;
if(_27d){
_280={state:this._initWinState(null,true)};
}else{
_280=this._getChangedWindowState(_27c);
}
if(_280){
var _281=_280.state;
var _282=_281[jsId.PP_WINDOW_POSITION_STATIC];
var _283=_281[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _284=null;
if(_280.extendedPropChanged){
var _285=jsId.PP_PROP_SEPARATOR;
var _286=jsId.PP_PAIR_SEPARATOR;
_284=jsId.PP_STATICPOS+_285+_282.toString();
_284+=_286+jsId.PP_FITHEIGHT+_285+_283.toString();
_284=escape(_284);
}
var _287="";
var _288=null;
if(_282){
_288="moveabs";
if(_281.column!=null){
_287+="&col="+_281.column;
}
if(_281.row!=null){
_287+="&row="+_281.row;
}
if(_281.layout!=null){
_287+="&layoutid="+_281.layout;
}
if(_281.height!=null){
_287+="&height="+_281.height;
}
}else{
_288="move";
if(_281.zIndex!=null){
_287+="&z="+_281.zIndex;
}
if(_281.width!=null){
_287+="&width="+_281.width;
}
if(_281.height!=null){
_287+="&height="+_281.height;
}
if(_281.left!=null){
_287+="&x="+_281.left;
}
if(_281.top!=null){
_287+="&y="+_281.top;
}
}
if(_284!=null){
_287+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_284;
}
this._submitAjaxApi(_288,_287,new _27e.om.MoveApiCL(this,_281));
if(!_27c&&!_27d){
if(!_282&&_280.zIndexChanged){
var _289=_27e.page.getPortletArray();
if(_289&&(_289.length-1)>0){
for(var i=0;i<_289.length;i++){
var _28b=_289[i];
if(_28b&&_28b.entityId!=this.entityId){
if(!_28b.properties[_27e.id.PP_WINDOW_POSITION_STATIC]){
_28b.submitWinState(true);
}
}
}
}
}else{
if(_282){
}
}
}
}
},retrieveContent:function(_28c,_28d,_28e){
if(_28c==null){
_28c=new jetspeed.om.PortletCL(this,_28e,_28d);
}
if(!_28d){
_28d={};
}
var _28f=this;
_28f.getPortletUrl(_28d);
this.contentRetriever.getContent(_28d,_28c,_28f,jetspeed.debugContentDumpIds);
},setPortletContent:function(_290,_291,_292){
var _293=this.getPWin();
if(_292!=null&&_292.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_292;
if(_293&&!this.loadingIndicatorIsShown()){
_293.setPortletTitle(_292);
}
}
if(_293){
_293.setPortletContent(_290,_291);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _295=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _296=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _297=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _298=this.getPWin();
if(_298&&(_295||_296)){
var _299=_298.getPortletTitle();
if(_299&&(_299==_295||_299==_296)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_29a){
var _29b=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_29b=jetspeed.prefs.desktopActionLabels[_29a];
if(_29b!=null&&_29b.length==0){
_29b=null;
}
}
return _29b;
},loadingIndicatorShow:function(_29c){
if(_29c&&!this.loadingIndicatorIsShown()){
var _29d=this._getLoadingActionLabel(_29c);
var _29e=this.getPWin();
if(_29e&&_29d){
_29e.setPortletTitle(_29d);
}
}
},loadingIndicatorHide:function(){
var _29f=this.getPWin();
if(_29f){
_29f.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_2a1,_2a2){
var _2a3=jetspeed;
var _2a4=_2a3.url;
var _2a5=null;
if(_2a1!=null){
_2a5=this.getAction(_2a1);
}
var _2a6=_2a2;
if(_2a6==null&&_2a5!=null){
_2a6=_2a5.url;
}
if(_2a6==null){
return;
}
var _2a7=_2a4.basePortalUrl()+_2a4.path.PORTLET+"/"+_2a6+_2a3.page.getPath();
if(_2a1!=_2a3.id.ACT_PRINT){
this.retrieveContent(null,{url:_2a7});
}else{
var _2a8=_2a3.page.getPageUrl();
_2a8=_2a4.addQueryParameter(_2a8,"jsprintmode","true");
_2a8=_2a4.addQueryParameter(_2a8,"jsaction",escape(_2a5.url));
_2a8=_2a4.addQueryParameter(_2a8,"jsentity",this.entityId);
_2a8=_2a4.addQueryParameter(_2a8,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_2a8.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_2aa,_2ab,_2ac){
if(_2aa){
this.actions=_2aa;
}else{
this.actions={};
}
this.currentActionState=_2ab;
this.currentActionMode=_2ac;
this.syncActions();
},syncActions:function(){
var _2ad=jetspeed;
_2ad.page.setPageModePortletActions(this);
var _2ae=this.getPWin();
if(_2ae){
_2ae.actionBtnSync(_2ad,_2ad.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_2b1,_2b2){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_2b1;
this.submitOperation=_2b2;
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
},eventConfMgr:function(_2b5){
var fn=(_2b5)?"disconnect":"connect";
var _2b7=dojo.event;
var form=this.form;
_2b7[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_2b7[fn]("after",node,"onclick",this,"click",null);
}
}
var _2bb=form.getElementsByTagName("input");
for(var i=0;i<_2bb.length;i++){
var _2bc=_2bb[i];
if(_2bc.type.toLowerCase()=="image"&&_2bc.form==form){
_2b7[fn]("after",_2bc,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_2b7[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_2be){
var _2bf=true;
if(this.isFormSubmitInProgress()){
_2bf=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_2bf=false;
}
}
}
return _2bf;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _2c1=jetspeed.portleturl.parseContentUrl(this.form.action);
var _2c2={};
if(_2c1.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2c1.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _2c3=jetspeed.portleturl.genPseudoUrl(_2c1,true);
this.form.action=_2c3;
this.submitOperation=_2c1.operation;
this.entityId=_2c1.portletEntityId;
_2c2.url=_2c1.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_2c2.formFilter=dojo.lang.hitch(this,"formFilter");
_2c2.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_2c2),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_2c2),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_2c4){
if(_2c4!=undefined){
this.formSubmitInProgress=_2c4;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_2c5,_2c6,_2c7){
this.portlet=_2c5;
this.suppressGetActions=_2c6;
this.formbind=null;
if(_2c7!=null&&_2c7.submitFormBindObject!=null){
this.formbind=_2c7.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_2c8){
if(this.portlet==null){
return;
}
if(_2c8){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2c9,_2ca,_2cb,http){
var _2cd=null;
if(http!=null){
try{
_2cd=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_2cd!=null){
_2cd=unescape(_2cd);
}
}
_2cb.setPortletContent(_2c9,_2ca,_2cd);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2cb.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2cf,_2d0,_2d1){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_2d0+" type: "+type+jetspeed.formatError(_2cf));
}};
jetspeed.om.PortletActionCL=function(_2d2,_2d3){
this.portlet=_2d2;
this.formbind=null;
if(_2d3!=null&&_2d3.submitFormBindObject!=null){
this.formbind=_2d3.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_2d4){
if(this.portlet==null){
return;
}
if(_2d4){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2d5,_2d6,_2d7,http){
var _2d9=jetspeed;
var _2da=null;
var _2db=false;
var _2dc=_2d9.portleturl.parseContentUrl(_2d5);
if(_2dc.operation==_2d9.portleturl.PORTLET_REQUEST_ACTION||_2dc.operation==_2d9.portleturl.PORTLET_REQUEST_RENDER){
if(_2d9.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_2dc.operation+"-url in response body: "+_2d5+"  url: "+_2dc.url+" entity-id: "+_2dc.portletEntityId);
}
_2da=_2dc.url;
}else{
if(_2d9.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_2d5);
}
_2da=_2d5;
if(_2da){
var _2dd=_2da.indexOf(_2d9.url.basePortalUrl()+_2d9.url.path.PORTLET);
if(_2dd==-1){
_2db=true;
window.location.href=_2da;
_2da=null;
}else{
if(_2dd>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_2d5);
_2da=null;
}
}
}
}
if(_2da!=null&&!_2d9.noActionRender){
if(_2d9.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_2da);
}
var _2de=new jetspeed.PortletRenderer(false,false,false,_2da,true);
_2de.renderAll();
}else{
this._loading(false);
}
if(!_2db&&this.portlet){
_2d9.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2e0,_2e1,_2e2){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_2e0));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2e3=this.getUrl();
if(_2e3){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2e3,this.getTarget());
}else{
jetspeed.updatePage(_2e3);
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
jetspeed.om.Menu=function(_2e4,_2e5){
this._is_parsed=false;
this.name=_2e4;
this.type=_2e5;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2e6){
if(!_2e6){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2e6);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2e8){
if(!this.hasOptions()){
return null;
}
if(_2e8==0||_2e8>0){
if(_2e8>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_2e8];
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
var _2ea=this.options[i];
if(_2ea instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_2ec,_2ed){
var _2ee=this.parseMenu(data,_2ed.menuName,_2ed.menuType);
_2ed.page.putMenu(_2ee);
},notifyFailure:function(type,_2f0,_2f1,_2f2){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_2f2.toString()+"] url: "+_2f1+" type: "+type+jetspeed.formatError(_2f0));
},parseMenu:function(node,_2f4,_2f5){
var menu=null;
var _2f7=node.getElementsByTagName("js");
if(!_2f7||_2f7.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _2f8=_2f7[0].childNodes;
for(var i=0;i<_2f8.length;i++){
var _2fa=_2f8[i];
if(_2fa.nodeType!=1){
continue;
}
var _2fb=_2fa.nodeName;
if(_2fb=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_2fa,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2f4;
}
if(menu.type==null){
menu.type=_2f5;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2fe=null;
var _2ff=node.childNodes;
for(var i=0;i<_2ff.length;i++){
var _301=_2ff[i];
if(_301.nodeType!=1){
continue;
}
var _302=_301.nodeName;
if(_302=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_301,new jetspeed.om.Menu()));
}
}else{
if(_302=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_301,new jetspeed.om.MenuOption()));
}
}else{
if(_302=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_301,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_302){
mObj[_302]=((_301&&_301.firstChild)?_301.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_303,_304,_305){
this.includeMenuDefs=_303;
this.initiateEditMode=_304;
this.isPageUpdate=_305;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_307,_308){
var _309=this.getMenuDefs(data,_307,_308);
for(var i=0;i<_309.length;i++){
var mObj=_309[i];
_308.page.putMenu(mObj);
}
this.notifyFinished(_308);
},getMenuDefs:function(data,_30d,_30e){
var _30f=[];
var _310=data.getElementsByTagName("menu");
for(var i=0;i<_310.length;i++){
var _312=_310[i].getAttribute("type");
if(this.includeMenuDefs){
_30f.push(this.parseMenuObject(_310[i],new jetspeed.om.Menu(null,_312)));
}else{
var _313=_310[i].firstChild.nodeValue;
_30f.push(new jetspeed.om.Menu(_313,_312));
}
}
return _30f;
},notifyFailure:function(type,_315,_316,_317){
dojo.raise("MenusApiCL error ["+_317.toString()+"] url: "+_316+" type: "+type+jetspeed.formatError(_315));
},notifyFinished:function(_318){
var _319=jetspeed;
if(this.includeMenuDefs){
_319.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
_319.editPageInitiate(_319);
}
if(this.isPageUpdate){
_319.updatePageEnd();
}
if(djConfig.isDebug&&_319.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_31a){
this.portletEntityId=_31a;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_31c,_31d){
if(jetspeed.url.checkAjaxApiResponse(_31c,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_31e){
var _31f=jetspeed.page.getPortlet(this.portletEntityId);
if(_31f){
if(_31e){
_31f.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_31f.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_321,_322,_323){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_323.toString()+"] url: "+_322+" type: "+type+jetspeed.formatError(_321));
}});
jetspeed.om.PageChangeActionCL=function(_324){
this.pageActionUrl=_324;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_326,_327){
if(jetspeed.url.checkAjaxApiResponse(_326,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_329,_32a,_32b){
dojo.raise("PageChangeActionCL error ["+_32b.toString()+"] url: "+_32a+" type: "+type+jetspeed.formatError(_329));
}});
jetspeed.om.PortletActionsCL=function(_32c){
this.portletEntityIds=_32c;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_32d){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _32f=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_32f){
if(_32d){
_32f.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_32f.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_331,_332){
this._loading(false);
if(jetspeed.url.checkAjaxApiResponse(_331,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _334=this.parsePortletActionsResponse(node);
for(var i=0;i<_334.length;i++){
var _336=_334[i];
var _337=_336.id;
var _338=jetspeed.page.getPortlet(_337);
if(_338!=null){
_338.updateActions(_336.actions,_336.currentActionState,_336.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _33a=new Array();
var _33b=node.getElementsByTagName("js");
if(!_33b||_33b.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _33a;
}
var _33c=_33b[0].childNodes;
for(var i=0;i<_33c.length;i++){
var _33e=_33c[i];
if(_33e.nodeType!=1){
continue;
}
var _33f=_33e.nodeName;
if(_33f=="portlets"){
var _340=_33e;
var _341=_340.childNodes;
for(var pI=0;pI<_341.length;pI++){
var _343=_341[pI];
if(_343.nodeType!=1){
continue;
}
var _344=_343.nodeName;
if(_344=="portlet"){
var _345=this.parsePortletElement(_343);
if(_345!=null){
_33a.push(_345);
}
}
}
}
}
return _33a;
},parsePortletElement:function(node){
var _347=node.getAttribute("id");
if(_347!=null){
var _348=jetspeed.page._parsePSMLActions(node,null);
var _349=jetspeed.page._parsePSMLChildOrAttr(node,"state");
var _34a=jetspeed.page._parsePSMLChildOrAttr(node,"mode");
return {id:_347,actions:_348,currentActionState:_349,currentActionMode:_34a};
}
return null;
},notifyFailure:function(type,_34c,_34d,_34e){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_34e.toString()+"] url: "+_34d+" type: "+type+jetspeed.formatError(_34c));
}});
jetspeed.om.MoveApiCL=function(_34f,_350){
this.portlet=_34f;
this.changedState=_350;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_351){
if(this.portlet==null){
return;
}
if(_351){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_353,_354){
this._loading(false);
dojo.lang.mixin(_354.portlet.lastSavedWindowState,this.changedState);
var _355=false;
if(djConfig.isDebug&&jetspeed.debug.submitWinState){
_355=true;
}
jetspeed.url.checkAjaxApiResponse(_353,data,_355,("move-portlet ["+_354.portlet.entityId+"]"),jetspeed.debug.submitWinState);
},notifyFailure:function(type,_357,_358,_359){
this._loading(false);
dojo.debug("submitWinState error ["+_359.entityId+"] url: "+_358+" type: "+type+jetspeed.formatError(_357));
}};
jetspeed.postload_addEventListener=function(node,_35b,fnc,_35d){
if((_35b=="load"||_35b=="DOMContentLoaded"||_35b=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_35b,fnc,_35d);
}
};
jetspeed.postload_attachEvent=function(node,_35f,fnc){
if(_35f=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_35f,fnc);
}
};
jetspeed.postload_docwrite=function(_361){
if(!_361){
return;
}
_361=_361.replace(/^\s+|\s+$/g,"");
var _362=/^<script\b([^>]*)>.*?<\/script>/i;
var _363=_362.exec(_361);
if(_363){
_361=null;
var _364=_363[1];
if(_364){
var _365=/\bid\s*=\s*([^\s]+)/i;
var _366=_365.exec(_364);
if(_366){
var _367=_366[1];
_361="<img id="+_367+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_361){
var _369=dojo;
tn=_369.doc().createElement("div");
tn.style.visibility="hidden";
_369.body().appendChild(tn);
tn.innerHTML=_361;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_36a,_36b,_36c){
if(_36a==document||_36a==window){
if(_36c&&_36c.length>0){
var _36d=jetspeed.portleturl;
if(_36c.indexOf(_36d.DESKTOP_ACTION_PREFIX_URL)!=0&&_36c.indexOf(_36d.DESKTOP_RENDER_PREFIX_URL)!=0){
_36a.location=_36c;
}
}
}else{
if(_36a!=null){
var _36e=_36b.indexOf(".");
if(_36e==-1){
_36a[_36b]=_36c;
}else{
var _36f=_36b.substring(0,_36e);
var _370=_36a[_36f];
if(_370){
var _371=_36b.substring(_36e+1);
if(_371){
_370[_371]=_36c;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _373=document.createElement("script");
_373.setAttribute("type","text/plain");
_373.setAttribute("language","ignore");
_373.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_373);
return _373;
};
jetspeed.containsElement=function(_374,_375,_376,_377){
if(!_374||!_375||!_376){
return false;
}
if(!_377){
_377=document;
}
var _378=_377.getElementsByTagName(_374);
if(!_378){
return false;
}
for(var i=0;i<_378.length;++i){
var _37a=_378[i].getAttribute(_375);
if(_37a==_376){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _37b=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _37c=_37b.concat([" height: ","","",";"]);
var _37d=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _37e=_37c.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _37f=_37e.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_37b,cssHeight:_37c,cssWidthHeight:_37d,cssOverflow:_37e,cssPosition:_37f,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_380,_381,_382,_383,_384,_385){
var djH=dojo.html;
var jsId=jetspeed.id;
var _388=null;
var _389=-1;
var _38a=-1;
var _38b=-1;
if(_380){
var _38c=_380.childNodes;
if(_38c){
_38b=_38c.length;
}
_388=[];
if(_38b>0){
var _38d="",_38e="";
if(!_385){
_38d=jsId.PWIN_CLASS;
}
if(_382){
_38d+=((_38d.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_383){
_38d+=((_38d.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_384&&!_383){
_38d+=((_38d.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_383&&!_384){
_38e=((_38e.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_38d.length>0){
var _38f=new RegExp("(^|\\s+)("+_38d+")(\\s+|$)");
var _390=null;
if(_38e.length>0){
_390=new RegExp("(^|\\s+)("+_38e+")(\\s+|$)");
}
var _391,_392,_393;
for(var i=0;i<_38b;i++){
_391=_38c[i];
_392=false;
_393=djH.getClass(_391);
if(_38f.test(_393)&&(_390==null||!_390.test(_393))){
_388.push(_391);
_392=true;
}
if(_381&&_391==_381){
if(!_392){
_388.push(_391);
}
_389=i;
_38a=_388.length-1;
}
}
}
}
}
return {matchingNodes:_388,totalNodes:_38b,matchNodeIndex:_389,matchNodeIndexInMatchingNodes:_38a};
},getPWinsFromNodes:function(_395){
var _396=jetspeed.page;
var _397=null;
if(_395){
_397=new Array();
for(var i=0;i<_395.length;i++){
var _399=_396.getPWin(_395[i].id);
if(_399){
_397.push(_399);
}
}
}
return _397;
},createPortletWindow:function(_39a,_39b,_39c){
var _39d=false;
if(djConfig.isDebug&&_39c.debug.profile){
_39d=true;
dojo.profile.start("createPortletWindow");
}
var _39e=(_39b!=null);
var _39f=false;
var _3a0=null;
if(_39e&&_39b<_39c.page.columns.length&&_39b>=0){
_3a0=_39c.page.columns[_39b].domNode;
}
if(_3a0==null){
_39f=true;
_3a0=document.getElementById(_39c.id.DESKTOP);
}
if(_3a0==null){
return;
}
var _3a1={};
if(_39a.isPortlet){
_3a1.portlet=_39a;
if(_39c.prefs.printModeOnly!=null){
_3a1.printMode=true;
}
if(_39f){
_39a.properties[_39c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_39c.widget.PortletWindow.prototype.altInitParamsDef(_3a1,_39a);
if(_39f){
pwP.altInitParams[_39c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _3a3=new _39c.widget.PortletWindow();
_3a3.build(_3a1,_3a0);
if(_39d){
dojo.profile.end("createPortletWindow");
}
},getLayoutExtents:function(node,_3a5,_3a6,_3a7){
if(!_3a5){
_3a5=_3a6.gcs(node);
}
var pad=_3a6._getPadExtents(node,_3a5);
var _3a9=_3a6._getBorderExtents(node,_3a5);
var _3aa={l:(pad.l+_3a9.l),t:(pad.t+_3a9.t),w:(pad.w+_3a9.w),h:(pad.h+_3a9.h)};
var _3ab=_3a6._getMarginExtents(node,_3a5,_3a7);
return {bE:_3a9,pE:pad,pbE:_3aa,mE:_3ab,lessW:(_3aa.w+_3ab.w),lessH:(_3aa.h+_3ab.h)};
},getContentBoxSize:function(node,_3ad){
var w=node.clientWidth,h,_3b0;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_3b0=_3ad.pbE;
}else{
h=node.clientHeight;
_3b0=_3ad.pE;
}
return {w:(w-_3b0.w),h:(h-_3b0.h)};
},getMarginBoxSize:function(node,_3b2){
return {w:(node.offsetWidth+_3b2.mE.w),h:(node.offsetHeight+_3b2.mE.h)};
},getMarginBox:function(node,_3b4,_3b5,_3b6){
var l=node.offsetLeft-_3b4.mE.l,t=node.offsetTop-_3b4.mE.t;
if(_3b5&&_3b6.UAope){
l-=_3b5.bE.l;
t-=_3b5.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_3b4.mE.w),h:(node.offsetHeight+_3b4.mE.h)};
},setMarginBox:function(node,_3ba,_3bb,_3bc,_3bd,_3be,_3bf,_3c0){
var pb=_3be.pbE,mb=_3be.mE;
if(_3bc!=null&&_3bc>=0){
_3bc=Math.max(_3bc-pb.w-mb.w,0);
}
if(_3bd!=null&&_3bd>=0){
_3bd=Math.max(_3bd-pb.h-mb.h,0);
}
_3c0._setBox(node,_3ba,_3bb,_3bc,_3bd);
},evtConnect:function(_3c3,_3c4,_3c5,_3c6,_3c7,_3c8,rate){
if(!rate){
rate=0;
}
var _3ca={adviceType:_3c3,srcObj:_3c4,srcFunc:_3c5,adviceObj:_3c6,adviceFunc:_3c7,rate:rate};
if(_3c8==null){
_3c8=dojo.event;
}
_3c8.connect(_3ca);
return _3ca;
},evtDisconnect:function(_3cb,_3cc,_3cd,_3ce,_3cf,_3d0){
if(_3d0==null){
_3d0=dojo.event;
}
_3d0.disconnect({adviceType:_3cb,srcObj:_3cc,srcFunc:_3cd,adviceObj:_3ce,adviceFunc:_3cf});
},evtDisconnectWObj:function(_3d1,_3d2){
if(_3d2==null){
_3d2=dojo.event;
}
_3d2.disconnect(_3d1);
},evtDisconnectWObjAry:function(_3d3,_3d4){
if(_3d3&&_3d3.length>0){
if(_3d4==null){
_3d4=dojo.event;
}
for(var i=0;i<_3d3.length;i++){
_3d4.disconnect(_3d3[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _3d6=false;
var _3d7=this._popupMenuWidgets;
for(var i=0;i<_3d7.length;i++){
var _3d9=_3d7[i];
if(_3d9&&_3d9.isShowingNow){
_3d6=true;
break;
}
}
return _3d6;
},addPopupMenuWidget:function(_3da){
if(_3da){
this._popupMenuWidgets.push(_3da);
}
},removePopupMenuWidget:function(_3db){
if(!_3db){
return;
}
var _3dc=this._popupMenuWidgets;
for(var i=0;i<_3dc.length;i++){
if(_3dc[i]===_3db){
_3dc[i]=null;
}
}
}};
if(jetspeed.UAie6){
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_3df){
this.oldXY=this.getWinDims(win,win.document,_3df);
},getWinDims:function(win,doc,_3e2){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_3e2.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_3e2;
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
var _3e9=jetspeed;
var _3ea=this.getWinDims(window,window.document,_3e9.docBody);
this.timerId=0;
if((_3ea.x!=this.oldXY.x)||(_3ea.y!=this.oldXY.y)){
this.oldXY=_3ea;
if(_3e9.page){
if(!this.resizing){
try{
this.resizing=true;
_3e9.page.onBrowserWindowResize();
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
}
jetspeed.ui.swfobject=function(){
var _3eb=jetspeed;
var _3ec=null;
var _3ed=false;
var ua=function(){
var _3ef=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_3ef[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_3ef[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_3ef[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _3f2=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_3ef=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_3ef[0]==6){
_3f2=true;
}
}
if(!_3f2){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_3f2&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_3ef=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_3ef,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
function showExpressInstall(_3f9){
_3ed=true;
var obj=document.getElementById(_3f9.id);
if(obj){
var ac=document.getElementById(_3f9.altContentId);
if(ac){
_3ec=ac;
}
var w=_3f9.width?_3f9.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_3f9.height?_3f9.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_3f9.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
}
function createSWF(_402,_403,el){
_403.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _402){
if(typeof _402[i]=="string"){
if(i=="data"){
_403.movie=_402[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_402[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_402[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _403){
if(typeof _403[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_403[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _402){
if(typeof _402[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_402[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_402[m]);
}
}
}
}
for(var n in _403){
if(typeof _403[n]=="string"&&n!="movie"){
createObjParam(o,n,_403[n]);
}
}
el.parentNode.replaceChild(o,el);
}
}
function createObjParam(el,_40d,_40e){
var p=document.createElement("param");
p.setAttribute("name",_40d);
p.setAttribute("value",_40e);
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
return {embedSWF:function(_416,_417,_418,_419,_41a,_41b,_41c,_41d,_41e,_41f){
if(!ua.w3cdom||!_416||!_417||!_418||!_419||!_41a){
return;
}
if(hasPlayerVersion(_41a.split("."))){
var _420=(_41e?_41e.id:null);
createCSS("#"+_417,"visibility:hidden");
var att=(typeof _41e=="object")?_41e:{};
att.data=_416;
att.width=_418;
att.height=_419;
var par=(typeof _41d=="object")?_41d:{};
if(typeof _41c=="object"){
for(var i in _41c){
if(typeof _41c[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_41c[i];
}else{
par.flashvars=i+"="+_41c[i];
}
}
}
}
createSWF(att,par,document.getElementById(_417));
createCSS("#"+_417,"visibility:visible");
if(_420){
var _424=_3eb.page.swfInfo;
if(_424==null){
_424=_3eb.page.swfInfo={};
}
_424[_420]=_41f;
}
}else{
if(_41b&&!_3ed&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_417,"visibility:hidden");
var _425={};
_425.id=_425.altContentId=_417;
_425.width=_418;
_425.height=_419;
_425.expressInstall=_41b;
showExpressInstall(_425);
createCSS("#"+_417,"visibility:visible");
}
}
}};
}();

