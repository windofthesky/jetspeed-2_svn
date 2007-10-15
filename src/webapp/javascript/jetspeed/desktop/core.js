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
},portletSelectorWindowTitle:"Portlet Selector",portletSelectorWindowIcon:"text-x-script.png",portletSelectorBounds:{x:20,y:20,width:400,height:600},windowActionButtonMax:5,windowActionButtonTooltip:true,windowIconEnabled:true,windowIconPath:"/images/portlets/small/",windowTitlebar:true,windowResizebar:true,windowDecoration:"tigris",pageActionButtonTooltip:true,getPortletDecorationBaseUrl:function(_1){
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
}else{
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
_84.url.loadingIndicatorShow("loadpageeditor");
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
_8d.editPageInitiate(_86);
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
},getPortletCurColRow:function(_16a,_16b,_16c){
if(!this.columns||this.columns.length==0){
return null;
}
var _16d=null;
var _16e=((_16a!=null)?true:false);
var _16f=0;
var _170=null;
var _171=null;
var _172=0;
var _173=false;
for(var _174=0;_174<this.columns.length;_174++){
var _175=this.columns[_174];
var _176=_175.domNode.childNodes;
if(_171==null||_171!=_175.getLayoutId()){
_171=_175.getLayoutId();
_170=this.layouts[_171];
if(_170==null){
dojo.raise("Layout not found: "+_171);
return null;
}
_172=0;
_173=false;
if(_170.clonedFromRootId==null){
_173=true;
}else{
var _177=this.getColFromColNode(_175.domNode.parentNode);
if(_177==null){
dojo.raise("Parent column not found: "+_175);
return null;
}
_175=_177;
}
}
var _178=null;
var _179=jetspeed;
var _17a=dojo;
var _17b=_179.id.PWIN_CLASS;
if(_16b){
_17b+="|"+_179.id.PWIN_GHOST_CLASS;
}
if(_16e){
_17b+="|"+_179.id.COL_CLASS;
}
var _17c=new RegExp("(^|\\s+)("+_17b+")(\\s+|$)");
for(var _17d=0;_17d<_176.length;_17d++){
var _17e=_176[_17d];
if(_17c.test(_17a.html.getClass(_17e))){
_178=(_178==null?0:_178+1);
if((_178+1)>_172){
_172=(_178+1);
}
if(_16a==null||_17e==_16a){
var _17f={layout:_171,column:_175.getLayoutColumnIndex(),row:_178,columnObj:_175};
if(!_173){
_17f.layout=_170.clonedFromRootId;
}
if(_16a!=null){
_16d=_17f;
break;
}else{
if(_16c!=null){
var _180=this.getPWinFromNode(_17e);
if(_180==null){
_17a.raise("PortletWindow not found for node");
}else{
var _181=_180.portlet;
if(_181==null){
_17a.raise("PortletWindow for node has null portlet: "+_180.widgetId);
}else{
_16c[_181.getId()]=_17f;
}
}
}
}
}
}
}
if(_16d!=null){
break;
}
}
return _16d;
},_getPortletArrayByZIndex:function(){
var _182=jetspeed;
var _183=this.getPortletArray();
if(!_183){
return _183;
}
var _184=[];
for(var i=0;i<_183.length;i++){
if(!_183[i].getProperty(_182.id.PP_WINDOW_POSITION_STATIC)){
_184.push(_183[i]);
}
}
_184.sort(this._portletZIndexCompare);
return _184;
},_portletZIndexCompare:function(_186,_187){
var _188=null;
var _189=null;
var _18a=null;
_18a=_186.getSavedWinState();
_188=_18a.zIndex;
_18a=_187.getSavedWinState();
_189=_18a.zIndex;
if(_188&&!_189){
return -1;
}else{
if(_189&&!_188){
return 1;
}else{
if(_188==_189){
return 0;
}
}
}
return (_188-_189);
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _18b=[];
for(var _18c in this.portlets){
var _18d=this.portlets[_18c];
_18b.push(_18d);
}
return _18b;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _18e=[];
for(var _18f in this.portlets){
var _190=this.portlets[_18f];
_18e.push(_190.getId());
}
return _18e;
},getPortletByName:function(_191){
if(this.portlets&&_191){
for(var _192 in this.portlets){
var _193=this.portlets[_192];
if(_193.name==_191){
return _193;
}
}
}
return null;
},getPortlet:function(_194){
if(this.portlets&&_194){
return this.portlets[_194];
}
return null;
},getPWinFromNode:function(_195){
var _196=null;
if(this.portlets&&_195){
for(var _197 in this.portlets){
var _198=this.portlets[_197];
var _199=_198.getPWin();
if(_199!=null){
if(_199.domNode==_195){
_196=_199;
break;
}
}
}
}
return _196;
},putPortlet:function(_19a){
if(!_19a){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_19a.entityId]=_19a;
this.portlet_count++;
},putPWin:function(_19b){
if(!_19b){
return;
}
var _19c=_19b.widgetId;
if(!_19c){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_19c]=_19b;
this.portlet_window_count++;
},getPWin:function(_19d){
if(this.portlet_windows&&_19d){
var pWin=this.portlet_windows[_19d];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_19d];
if(pWin==null){
var p=this.getPortlet(_19d);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_1a1){
var _1a2=this.portlet_windows;
var pWin;
var _1a4=[];
for(var _1a5 in _1a2){
pWin=_1a2[_1a5];
if(pWin&&(!_1a1||pWin.portlet)){
_1a4.push(pWin);
}
}
return _1a4;
},getPWinTopZIndex:function(_1a6){
var _1a7=0;
if(_1a6){
_1a7=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_1a7;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_1a7=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_1a7;
}
return _1a7;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_1a8,_1a9){
return;
},onBrowserWindowResize:function(){
var _1aa=jetspeed;
if(_1aa.UAie6){
var _1ab=this.portlet_windows;
var pWin;
for(var _1ad in _1ab){
pWin=_1ab[_1ad];
pWin.onBrowserWindowResize();
}
if(this.editMode){
var _1ae=dojo.widget.byId(_1aa.id.PG_ED_WID);
if(_1ae!=null){
_1ae.onBrowserWindowResize();
}
}
}
},regPWinIFrameCover:function(_1af){
if(!_1af){
return;
}
this.iframeCoverByWinId[_1af.widgetId]=true;
},unregPWinIFrameCover:function(_1b0){
if(!_1b0){
return;
}
delete this.iframeCoverByWinId[_1b0.widgetId];
},displayAllPWinIFrameCovers:function(_1b1,_1b2){
var _1b3=this.portlet_windows;
var _1b4=this.iframeCoverByWinId;
if(!_1b3||!_1b4){
return;
}
for(var _1b5 in _1b4){
if(_1b5==_1b2){
continue;
}
var pWin=_1b3[_1b5];
var _1b7=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_1b7){
_1b7.style.display=(_1b1?"none":"block");
}
}
},createLayoutInfo:function(_1b8){
var _1b9=dojo;
var _1ba=null;
var _1bb=null;
var _1bc=null;
var _1bd=null;
var _1be=document.getElementById(_1b8.id.DESKTOP);
if(_1be!=null){
_1ba=_1b8.ui.getLayoutExtents(_1be,null,_1b9,_1b8);
}
var _1bf=document.getElementById(_1b8.id.COLUMNS);
if(_1bf!=null){
_1bb=_1b8.ui.getLayoutExtents(_1bf,null,_1b9,_1b8);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_1bd=_1b8.ui.getLayoutExtents(col.domNode,null,_1b9,_1b8);
}else{
if(!col.columnContainer){
_1bc=_1b8.ui.getLayoutExtents(col.domNode,null,_1b9,_1b8);
}
}
if(_1bc!=null&&_1bd!=null){
break;
}
}
}
this.layoutInfo={desktop:(_1ba!=null?_1ba:{}),columns:(_1bb!=null?_1bb:{}),column:(_1bc!=null?_1bc:{}),columnLayoutHeader:(_1bd!=null?_1bd:{})};
_1b8.widget.PortletWindow.prototype.colWidth_pbE=((_1bc&&_1bc.pbE)?_1bc.pbE.w:0);
},destroy:function(){
var _1c2=jetspeed;
var _1c3=dojo;
if(_1c2.UAie6){
_1c2.ui.evtDisconnect("after",window,"onresize",_1c2.ui.windowResizeMgr,"onResize",_1c3.event);
}
var _1c4=this.portlet_windows;
var _1c5=this.getPWins(true);
var pWin,_1c7;
for(var i=0;i<_1c5.length;i++){
pWin=_1c5[i];
_1c7=pWin.widgetId;
pWin.closeWindow();
delete _1c4[_1c7];
this.portlet_window_count--;
}
this.portlets=[];
this.portlet_count=0;
var _1c9=_1c3.widget.byId(_1c2.id.PG_ED_WID);
if(_1c9!=null){
_1c9.editPageDestroy();
}
this._removeCols(document.getElementById(_1c2.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_1ca){
if(_1ca==null){
return null;
}
var _1cb=_1ca.getAttribute("columnindex");
if(_1cb==null){
return null;
}
var _1cc=new Number(_1cb);
if(_1cc>=0&&_1cc<this.columns.length){
return this.columns[_1cc];
}
return null;
},getColIndexForNode:function(node){
var _1ce=null;
if(!this.columns){
return _1ce;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1ce=i;
break;
}
}
return _1ce;
},getColWithNode:function(node){
var _1d1=this.getColIndexForNode(node);
return ((_1d1!=null&&_1d1>=0)?this.columns[_1d1]:null);
},getDescendantCols:function(_1d2){
var dMap={};
if(_1d2==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1d2&&_1d2.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_1d6){
if(!_1d6){
return;
}
var _1d7=(_1d6.getName?_1d6.getName():null);
if(_1d7!=null){
this.menus[_1d7]=_1d6;
}
},getMenu:function(_1d8){
if(_1d8==null){
return null;
}
return this.menus[_1d8];
},removeMenu:function(_1d9){
if(_1d9==null){
return;
}
var _1da=null;
if(dojo.lang.isString(_1d9)){
_1da=_1d9;
}else{
_1da=(_1d9.getName?_1d9.getName():null);
}
if(_1da!=null){
delete this.menus[_1da];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1db=[];
for(var _1dc in this.menus){
_1db.push(_1dc);
}
return _1db;
},retrieveMenuDeclarations:function(_1dd,_1de,_1df){
contentListener=new jetspeed.om.MenusApiCL(_1dd,_1de,_1df);
this.clearMenus();
var _1e0="?action=getmenus";
if(_1dd){
_1e0+="&includeMenuDefs=true";
}
var _1e1=this.getPsmlUrl()+_1e0;
var _1e2="text/xml";
var _1e3=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1e1,mimetype:_1e2},contentListener,_1e3,jetspeed.debugContentDumpIds);
},syncPageControls:function(_1e4){
var jsId=_1e4.id;
if(this.actionButtons==null){
return;
}
for(var _1e6 in this.actionButtons){
var _1e7=false;
if(_1e6==jsId.ACT_EDIT){
if(!this.editMode){
_1e7=true;
}
}else{
if(_1e6==jsId.ACT_VIEW){
if(this.editMode){
_1e7=true;
}
}else{
if(_1e6==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_1e7=true;
}
}else{
_1e7=true;
}
}
}
if(_1e7){
this.actionButtons[_1e6].style.display="";
}else{
this.actionButtons[_1e6].style.display="none";
}
}
},renderPageControls:function(_1e8){
var _1e8=jetspeed;
var jsId=_1e8.id;
var _1ea=dojo;
var _1eb=[];
if(this.actions!=null){
for(var _1ec in this.actions){
if(_1ec!=jsId.ACT_HELP){
_1eb.push(_1ec);
}
if(_1ec==jsId.ACT_EDIT){
_1eb.push(jsId.ACT_ADDPORTLET);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
if(this.actions[jsId.ACT_VIEW]==null){
_1eb.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
if(this.actions[jsId.ACT_EDIT]==null){
_1eb.push(jsId.ACT_EDIT);
}
}
}
var _1ed=_1ea.byId(jsId.PAGE_CONTROLS);
if(_1ed!=null&&_1eb!=null&&_1eb.length>0){
var _1ee=_1e8.prefs;
var jsUI=_1e8.ui;
var _1f0=_1ea.event;
var _1f1=_1e8.page.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _1f2=this.actionButtonTooltips;
for(var i=0;i<_1eb.length;i++){
var _1ec=_1eb[i];
var _1f4=document.createElement("div");
_1f4.className="portalPageActionButton";
_1f4.style.backgroundImage="url("+_1ee.getLayoutRootUrl()+"/images/desktop/"+_1ec+".gif)";
_1f4.actionName=_1ec;
this.actionButtons[_1ec]=_1f4;
_1ed.appendChild(_1f4);
jsUI.evtConnect("after",_1f4,"onclick",this,"pageActionButtonClick",_1f0);
if(_1ee.pageActionButtonTooltip){
var _1f5=null;
if(_1ee.desktopActionLabels!=null){
_1f5=_1ee.desktopActionLabels[_1ec];
}
if(_1f5==null||_1f5.length==0){
_1f5=_1ea.string.capitalize(_1ec);
}
_1f2.push(_1f1.addNode(_1f4,_1f5,true,null,null,null,_1e8,jsUI,_1f0));
}
}
}
},_destroyPageControls:function(){
var _1f6=jetspeed;
if(this.actionButtons){
for(var _1f7 in this.actionButtons){
var _1f8=this.actionButtons[_1f7];
if(_1f8){
_1f6.ui.evtDisconnect("after",_1f8,"onclick",this,"pageActionButtonClick");
}
}
}
var _1f9=dojo.byId(_1f6.id.PAGE_CONTROLS);
if(_1f9!=null&&_1f9.childNodes&&_1f9.childNodes.length>0){
for(var i=(_1f9.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1f9.childNodes[i]);
}
}
_1f6.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_1fc){
var _1fd=jetspeed;
if(_1fc==null){
return;
}
if(_1fc==_1fd.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1fc==_1fd.id.ACT_EDIT){
_1fd.editPageInitiate(_1fd);
}else{
if(_1fc==_1fd.id.ACT_VIEW){
_1fd.editPageTerminate(_1fd);
}else{
var _1fe=this.getPageAction(_1fc);
alert("pageAction "+_1fc+" : "+_1fe);
if(_1fe==null){
return;
}
if(_1fe.url==null){
return;
}
var _1ff=_1fd.url.basePortalUrl()+_1fd.url.path.DESKTOP+"/"+_1fe.url;
_1fd.pageNavigate(_1ff);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_201,_202){
var _203=jetspeed;
if(!_202){
_202=escape(this.getPagePathAndQuery());
}else{
_202=escape(_202);
}
var _204=_203.url.basePortalUrl()+_203.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_202;
if(_201!=null){
_204+="&jslayoutid="+escape(_201);
}
_203.changeActionForPortlet(this.rootFragmentId,null,_203.id.ACT_EDIT,new jetspeed.om.PageChangeActionCL(_204));
},setPageModePortletActions:function(_205){
if(_205==null||_205.actions==null){
return;
}
var jsId=jetspeed.id;
if(_205.actions[jsId.ACT_REMOVEPORTLET]==null){
_205.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_207){
if(this.pageUrl!=null&&!_207){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _209=jsU.path.SERVER+((_207)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _20a=jsU.parse(_209);
var _20b=null;
if(this.pageUrlFallback!=null){
_20b=jsU.parse(this.pageUrlFallback);
}else{
_20b=jsU.parse(window.location.href);
}
if(_20a!=null&&_20b!=null){
var _20c=_20b.query;
if(_20c!=null&&_20c.length>0){
var _20d=_20a.query;
if(_20d!=null&&_20d.length>0){
_209=_209+"&"+_20c;
}else{
_209=_209+"?"+_20c;
}
}
}
if(!_207){
this.pageUrl=_209;
}
return _209;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _20f=this.getPath();
var _210=jsU.parse(_20f);
var _211=null;
if(this.pageUrlFallback!=null){
_211=jsU.parse(this.pageUrlFallback);
}else{
_211=jsU.parse(window.location.href);
}
if(_210!=null&&_211!=null){
var _212=_211.query;
if(_212!=null&&_212.length>0){
var _213=_210.query;
if(_213!=null&&_213.length>0){
_20f=_20f+"&"+_212;
}else{
_20f=_20f+"?"+_212;
}
}
}
this.pagePathAndQuery=_20f;
return _20f;
},getPageDirectory:function(_214){
var _215="/";
var _216=(_214?this.getRealPath():this.getPath());
if(_216!=null){
var _217=_216.lastIndexOf("/");
if(_217!=-1){
if((_217+1)<_216.length){
_215=_216.substring(0,_217+1);
}else{
_215=_216;
}
}
}
return _215;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_219){
if(!_219){
_219="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_219)){
return jsU.path.SERVER+jsU.path.DESKTOP+_219;
}
return _219;
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
jetspeed.om.Column=function(_21b,_21c,size,_21e,_21f){
this.layoutColumnIndex=_21b;
this.layoutId=_21c;
this.size=size;
this.pageColumnIndex=new Number(_21e);
if(typeof _21f!="undefined"){
this.layoutActionsDisabled=_21f;
}
this.id="jscol_"+_21e;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_220){
var _221=this.styleClass;
var _222=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_222>0){
_221+=" desktopColumnClear-PRIVATE";
}
var _223=document.createElement("div");
_223.setAttribute("columnindex",_222);
_223.style.width=this.size+"%";
if(this.layoutHeader){
_221=this.styleLayoutClass;
_223.setAttribute("layoutid",this.layoutId);
}
_223.className=_221;
_223.id=this.getId();
this.domNode=_223;
if(_220!=null){
_220.appendChild(_223);
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
jetspeed.om.Portlet=function(_226,_227,_228,_229,_22a,_22b,_22c,_22d){
this.name=_226;
this.entityId=_227;
this.properties=_229;
this.actions=_22a;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_22b;
this.currentActionMode=_22c;
if(_228){
this.contentRetriever=_228;
}
this.layoutActionsDisabled=false;
if(typeof _22d!="undefined"){
this.layoutActionsDisabled=_22d;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _22e=jetspeed;
var jsId=_22e.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _230=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_22e.prefs.windowTiling){
if(_230=="true"){
_230=true;
}else{
if(_230=="false"){
_230=false;
}else{
if(_230!=true&&_230!=false){
_230=true;
}
}
}
}else{
_230=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_230;
var _231=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_231=="true"){
_231=true;
}else{
if(_230=="false"){
_231=false;
}else{
if(_231!=true&&_231!=false){
_231=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_231;
var _232=this.properties[jsId.PP_WINDOW_TITLE];
if(!_232&&this.name){
var re=(/^[^:]*:*/);
_232=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_232;
}
},postParseAnnotateHtml:function(_234){
var _235=jetspeed;
var _236=_235.portleturl;
if(_234){
var _237=_234;
var _238=_237.getElementsByTagName("form");
var _239=_235.debug.postParseAnnotateHtml;
var _23a=_235.debug.postParseAnnotateHtmlDisableAnchors;
if(_238){
for(var i=0;i<_238.length;i++){
var _23c=_238[i];
var _23d=_23c.action;
var _23e=_236.parseContentUrl(_23d);
var _23f=_23e.operation;
if(_23f==_236.PORTLET_REQUEST_ACTION||_23f==_236.PORTLET_REQUEST_RENDER){
var _240=_236.genPseudoUrl(_23e,true);
_23c.action=_240;
var _241=new _235.om.ActionRenderFormBind(_23c,_23e.url,_23e.portletEntityId,_23f);
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_23f+") for form with action: "+_23d);
}
}else{
if(_23d==null||_23d.length==0){
var _241=new _235.om.ActionRenderFormBind(_23c,null,this.entityId,null);
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_23d);
}
}
}
}
}
var _242=_237.getElementsByTagName("a");
if(_242){
for(var i=0;i<_242.length;i++){
var _243=_242[i];
var _244=_243.href;
var _23e=_236.parseContentUrl(_244);
var _245=null;
if(!_23a){
_245=_236.genPseudoUrl(_23e);
}
if(!_245){
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_244);
}
}else{
if(_245==_244){
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_244);
}
}else{
if(_239){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_244+" with: "+_245);
}
_243.href=_245;
}
}
}
}
}
},getPWin:function(){
var _246=jetspeed;
var _247=this.properties[_246.id.PP_WIDGET_ID];
if(_247){
return _246.page.getPWin(_247);
}
return null;
},getCurWinState:function(_248){
var _249=null;
try{
var _24a=this.getPWin();
if(!_24a){
return null;
}
_249=_24a.getCurWinStateForPersist(_248);
if(!_248){
if(_249.layout==null){
_249.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _249;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_24b,_24c){
var _24d=jetspeed;
var jsId=_24d.id;
if(!_24b){
_24b={};
}
var _24f=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _250=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_24b[jsId.PP_WINDOW_POSITION_STATIC]=_24f;
_24b[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_250;
var _251=this.properties["width"];
if(!_24c&&_251!=null&&_251>0){
_24b.width=Math.floor(_251);
}else{
if(_24c){
_24b.width=-1;
}
}
var _252=this.properties["height"];
if(!_24c&&_252!=null&&_252>0){
_24b.height=Math.floor(_252);
}else{
if(_24c){
_24b.height=-1;
}
}
if(!_24f||!_24d.prefs.windowTiling){
var _253=this.properties["x"];
if(!_24c&&_253!=null&&_253>=0){
_24b.left=Math.floor(((_253>0)?_253:0));
}else{
if(_24c){
_24b.left=-1;
}
}
var _254=this.properties["y"];
if(!_24c&&_254!=null&&_254>=0){
_24b.top=Math.floor(((_254>0)?_254:0));
}else{
_24b.top=-1;
}
var _255=this._getInitialZIndex(_24c);
if(_255!=null){
_24b.zIndex=_255;
}
}
return _24b;
},_initWinState:function(_256,_257){
var _258=jetspeed;
var _259=(_256?_256:{});
this.getInitialWinDims(_259,_257);
if(_258.debug.initWinState){
var _25a=this.properties[_258.id.PP_WINDOW_POSITION_STATIC];
if(!_25a||!_258.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_259.zIndex+" x="+_259.left+" y="+_259.top+" width="+_259.width+" height="+_259.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_259.column+" row="+_259.row+" width="+_259.width+" height="+_259.height);
}
}
this.lastSavedWindowState=_259;
return _259;
},_getInitialZIndex:function(_25b){
var _25c=null;
var _25d=this.properties["z"];
if(!_25b&&_25d!=null&&_25d>=0){
_25c=Math.floor(_25d);
}else{
if(_25b){
_25c=-1;
}
}
return _25c;
},_getChangedWindowState:function(_25e){
var jsId=jetspeed.id;
var _260=this.getSavedWinState();
if(_260&&dojo.lang.isEmpty(_260)){
_260=null;
_25e=false;
}
var _261=this.getCurWinState(_25e);
var _262=_261[jsId.PP_WINDOW_POSITION_STATIC];
var _263=!_262;
if(!_260){
var _264={state:_261,positionChanged:true,extendedPropChanged:true};
if(_263){
_264.zIndexChanged=true;
}
return _264;
}
var _265=false;
var _266=false;
var _267=false;
var _268=false;
for(var _269 in _261){
if(_261[_269]!=_260[_269]){
if(_269==jsId.PP_WINDOW_POSITION_STATIC||_269==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_265=true;
_267=true;
_266=true;
}else{
if(_269=="zIndex"){
if(_263){
_265=true;
_268=true;
}
}else{
_265=true;
_266=true;
}
}
}
}
if(_265){
var _264={state:_261,positionChanged:_266,extendedPropChanged:_267};
if(_263){
_264.zIndexChanged=_268;
}
return _264;
}
return null;
},getPortletUrl:function(_26a){
var _26b=jetspeed;
var _26c=_26b.url;
var _26d=null;
if(_26a&&_26a.url){
_26d=_26a.url;
}else{
if(_26a&&_26a.formNode){
var _26e=_26a.formNode.getAttribute("action");
if(_26e){
_26d=_26e;
}
}
}
if(_26d==null){
_26d=_26c.basePortalUrl()+_26c.path.PORTLET+_26b.page.getPath();
}
if(!_26a.dontAddQueryArgs){
_26d=_26c.parse(_26d);
_26d=_26c.addQueryParameter(_26d,"entity",this.entityId,true);
_26d=_26c.addQueryParameter(_26d,"portlet",this.name,true);
_26d=_26c.addQueryParameter(_26d,"encoder","desktop",true);
if(_26a.jsPageUrl!=null){
var _26f=_26a.jsPageUrl.query;
if(_26f!=null&&_26f.length>0){
_26d=_26d.toString()+"&"+_26f;
}
}
}
if(_26a){
_26a.url=_26d.toString();
}
return _26d;
},_submitAjaxApi:function(_270,_271,_272){
var _273=jetspeed;
var _274="?action="+_270+"&id="+this.entityId+_271;
var _275=_273.url.basePortalUrl()+_273.url.path.AJAX_API+_273.page.getPath()+_274;
var _276="text/xml";
var _277=new _273.om.Id(_270,this.entityId);
_277.portlet=this;
_273.url.retrieveContent({url:_275,mimetype:_276},_272,_277,null);
},submitWinState:function(_278,_279){
var _27a=jetspeed;
var jsId=_27a.id;
var _27c=null;
if(_279){
_27c={state:this._initWinState(null,true)};
}else{
_27c=this._getChangedWindowState(_278);
}
if(_27c){
var _27d=_27c.state;
var _27e=_27d[jsId.PP_WINDOW_POSITION_STATIC];
var _27f=_27d[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _280=null;
if(_27c.extendedPropChanged){
var _281=jsId.PP_PROP_SEPARATOR;
var _282=jsId.PP_PAIR_SEPARATOR;
_280=jsId.PP_STATICPOS+_281+_27e.toString();
_280+=_282+jsId.PP_FITHEIGHT+_281+_27f.toString();
_280=escape(_280);
}
var _283="";
var _284=null;
if(_27e){
_284="moveabs";
if(_27d.column!=null){
_283+="&col="+_27d.column;
}
if(_27d.row!=null){
_283+="&row="+_27d.row;
}
if(_27d.layout!=null){
_283+="&layoutid="+_27d.layout;
}
if(_27d.height!=null){
_283+="&height="+_27d.height;
}
}else{
_284="move";
if(_27d.zIndex!=null){
_283+="&z="+_27d.zIndex;
}
if(_27d.width!=null){
_283+="&width="+_27d.width;
}
if(_27d.height!=null){
_283+="&height="+_27d.height;
}
if(_27d.left!=null){
_283+="&x="+_27d.left;
}
if(_27d.top!=null){
_283+="&y="+_27d.top;
}
}
if(_280!=null){
_283+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_280;
}
this._submitAjaxApi(_284,_283,new _27a.om.MoveApiCL(this,_27d));
if(!_278&&!_279){
if(!_27e&&_27c.zIndexChanged){
var _285=_27a.page.getPortletArray();
if(_285&&(_285.length-1)>0){
for(var i=0;i<_285.length;i++){
var _287=_285[i];
if(_287&&_287.entityId!=this.entityId){
if(!_287.properties[_27a.id.PP_WINDOW_POSITION_STATIC]){
_287.submitWinState(true);
}
}
}
}
}else{
if(_27e){
}
}
}
}
},retrieveContent:function(_288,_289,_28a){
if(_288==null){
_288=new jetspeed.om.PortletCL(this,_28a,_289);
}
if(!_289){
_289={};
}
var _28b=this;
_28b.getPortletUrl(_289);
this.contentRetriever.getContent(_289,_288,_28b,jetspeed.debugContentDumpIds);
},setPortletContent:function(_28c,_28d,_28e){
var _28f=this.getPWin();
if(_28e!=null&&_28e.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_28e;
if(_28f&&!this.loadingIndicatorIsShown()){
_28f.setPortletTitle(_28e);
}
}
if(_28f){
_28f.setPortletContent(_28c,_28d);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _291=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _292=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _293=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _294=this.getPWin();
if(_294&&(_291||_292)){
var _295=_294.getPortletTitle();
if(_295&&(_295==_291||_295==_292)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_296){
var _297=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_297=jetspeed.prefs.desktopActionLabels[_296];
if(_297!=null&&_297.length==0){
_297=null;
}
}
return _297;
},loadingIndicatorShow:function(_298){
if(_298&&!this.loadingIndicatorIsShown()){
var _299=this._getLoadingActionLabel(_298);
var _29a=this.getPWin();
if(_29a&&_299){
_29a.setPortletTitle(_299);
}
}
},loadingIndicatorHide:function(){
var _29b=this.getPWin();
if(_29b){
_29b.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_29d,_29e){
var _29f=jetspeed;
var _2a0=_29f.url;
var _2a1=null;
if(_29d!=null){
_2a1=this.getAction(_29d);
}
var _2a2=_29e;
if(_2a2==null&&_2a1!=null){
_2a2=_2a1.url;
}
if(_2a2==null){
return;
}
var _2a3=_2a0.basePortalUrl()+_2a0.path.PORTLET+"/"+_2a2+_29f.page.getPath();
if(_29d!=_29f.id.ACT_PRINT){
this.retrieveContent(null,{url:_2a3});
}else{
var _2a4=_29f.page.getPageUrl();
_2a4=_2a0.addQueryParameter(_2a4,"jsprintmode","true");
_2a4=_2a0.addQueryParameter(_2a4,"jsaction",escape(_2a1.url));
_2a4=_2a0.addQueryParameter(_2a4,"jsentity",this.entityId);
_2a4=_2a0.addQueryParameter(_2a4,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_2a4.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_2a6,_2a7,_2a8){
if(_2a6){
this.actions=_2a6;
}else{
this.actions={};
}
this.currentActionState=_2a7;
this.currentActionMode=_2a8;
this.syncActions();
},syncActions:function(){
var _2a9=jetspeed;
_2a9.page.setPageModePortletActions(this);
var _2aa=this.getPWin();
if(_2aa){
_2aa.actionBtnSync(_2a9,_2a9.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_2ad,_2ae){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_2ad;
this.submitOperation=_2ae;
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
},eventConfMgr:function(_2b1){
var fn=(_2b1)?"disconnect":"connect";
var _2b3=dojo.event;
var form=this.form;
_2b3[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_2b3[fn]("after",node,"onclick",this,"click",null);
}
}
var _2b7=form.getElementsByTagName("input");
for(var i=0;i<_2b7.length;i++){
var _2b8=_2b7[i];
if(_2b8.type.toLowerCase()=="image"&&_2b8.form==form){
_2b3[fn]("after",_2b8,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_2b3[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_2ba){
var _2bb=true;
if(this.isFormSubmitInProgress()){
_2bb=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_2bb=false;
}
}
}
return _2bb;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _2bd=jetspeed.portleturl.parseContentUrl(this.form.action);
var _2be={};
if(_2bd.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2bd.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _2bf=jetspeed.portleturl.genPseudoUrl(_2bd,true);
this.form.action=_2bf;
this.submitOperation=_2bd.operation;
this.entityId=_2bd.portletEntityId;
_2be.url=_2bd.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_2be.formFilter=dojo.lang.hitch(this,"formFilter");
_2be.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_2be),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_2be),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_2c0){
if(_2c0!=undefined){
this.formSubmitInProgress=_2c0;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_2c1,_2c2,_2c3){
this.portlet=_2c1;
this.suppressGetActions=_2c2;
this.formbind=null;
if(_2c3!=null&&_2c3.submitFormBindObject!=null){
this.formbind=_2c3.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_2c4){
if(this.portlet==null){
return;
}
if(_2c4){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2c5,_2c6,_2c7,http){
var _2c9=null;
if(http!=null){
_2c9=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2c9!=null){
_2c9=unescape(_2c9);
}
}
_2c7.setPortletContent(_2c5,_2c6,_2c9);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2c7.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2cb,_2cc,_2cd){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_2cc+" type: "+type+jetspeed.formatError(_2cb));
}};
jetspeed.om.PortletActionCL=function(_2ce,_2cf){
this.portlet=_2ce;
this.formbind=null;
if(_2cf!=null&&_2cf.submitFormBindObject!=null){
this.formbind=_2cf.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_2d0){
if(this.portlet==null){
return;
}
if(_2d0){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2d1,_2d2,_2d3,http){
var _2d5=jetspeed;
var _2d6=null;
var _2d7=false;
var _2d8=_2d5.portleturl.parseContentUrl(_2d1);
if(_2d8.operation==_2d5.portleturl.PORTLET_REQUEST_ACTION||_2d8.operation==_2d5.portleturl.PORTLET_REQUEST_RENDER){
if(_2d5.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_2d8.operation+"-url in response body: "+_2d1+"  url: "+_2d8.url+" entity-id: "+_2d8.portletEntityId);
}
_2d6=_2d8.url;
}else{
if(_2d5.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_2d1);
}
_2d6=_2d1;
if(_2d6){
var _2d9=_2d6.indexOf(_2d5.url.basePortalUrl()+_2d5.url.path.PORTLET);
if(_2d9==-1){
_2d7=true;
window.location.href=_2d6;
_2d6=null;
}else{
if(_2d9>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_2d1);
_2d6=null;
}
}
}
}
if(_2d6!=null){
if(_2d5.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_2d6);
}
var _2da=new jetspeed.PortletRenderer(false,false,false,_2d6,true);
_2da.renderAll();
}else{
this._loading(false);
}
if(!_2d7&&this.portlet){
_2d5.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2dc,_2dd,_2de){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_2dc));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2df=this.getUrl();
if(_2df){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2df,this.getTarget());
}else{
jetspeed.updatePage(_2df);
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
jetspeed.om.Menu=function(_2e0,_2e1){
this._is_parsed=false;
this.name=_2e0;
this.type=_2e1;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2e2){
if(!_2e2){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2e2);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2e4){
if(!this.hasOptions()){
return null;
}
if(_2e4==0||_2e4>0){
if(_2e4>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_2e4];
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
var _2e6=this.options[i];
if(_2e6 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_2e8,_2e9){
var _2ea=this.parseMenu(data,_2e9.menuName,_2e9.menuType);
_2e9.page.putMenu(_2ea);
},notifyFailure:function(type,_2ec,_2ed,_2ee){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_2ee.toString()+"] url: "+_2ed+" type: "+type+jetspeed.formatError(_2ec));
},parseMenu:function(node,_2f0,_2f1){
var menu=null;
var _2f3=node.getElementsByTagName("js");
if(!_2f3||_2f3.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _2f4=_2f3[0].childNodes;
for(var i=0;i<_2f4.length;i++){
var _2f6=_2f4[i];
if(_2f6.nodeType!=1){
continue;
}
var _2f7=_2f6.nodeName;
if(_2f7=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_2f6,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2f0;
}
if(menu.type==null){
menu.type=_2f1;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2fa=null;
var _2fb=node.childNodes;
for(var i=0;i<_2fb.length;i++){
var _2fd=_2fb[i];
if(_2fd.nodeType!=1){
continue;
}
var _2fe=_2fd.nodeName;
if(_2fe=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_2fd,new jetspeed.om.Menu()));
}
}else{
if(_2fe=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_2fd,new jetspeed.om.MenuOption()));
}
}else{
if(_2fe=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2fd,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2fe){
mObj[_2fe]=((_2fd&&_2fd.firstChild)?_2fd.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_2ff,_300,_301){
this.includeMenuDefs=_2ff;
this.initiateEditMode=_300;
this.isPageUpdate=_301;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_303,_304){
var _305=this.getMenuDefs(data,_303,_304);
for(var i=0;i<_305.length;i++){
var mObj=_305[i];
_304.page.putMenu(mObj);
}
this.notifyFinished(_304);
},getMenuDefs:function(data,_309,_30a){
var _30b=[];
var _30c=data.getElementsByTagName("menu");
for(var i=0;i<_30c.length;i++){
var _30e=_30c[i].getAttribute("type");
if(this.includeMenuDefs){
_30b.push(this.parseMenuObject(_30c[i],new jetspeed.om.Menu(null,_30e)));
}else{
var _30f=_30c[i].firstChild.nodeValue;
_30b.push(new jetspeed.om.Menu(_30f,_30e));
}
}
return _30b;
},notifyFailure:function(type,_311,_312,_313){
dojo.raise("MenusApiCL error ["+_313.toString()+"] url: "+_312+" type: "+type+jetspeed.formatError(_311));
},notifyFinished:function(_314){
var _315=jetspeed;
if(this.includeMenuDefs){
_315.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
_315.editPageInitiate(_315);
}
if(this.isPageUpdate){
_315.updatePageEnd();
}
if(djConfig.isDebug&&_315.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_316){
this.portletEntityId=_316;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_318,_319){
if(jetspeed.url.checkAjaxApiResponse(_318,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_31a){
var _31b=jetspeed.page.getPortlet(this.portletEntityId);
if(_31b){
if(_31a){
_31b.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_31b.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_31d,_31e,_31f){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_31f.toString()+"] url: "+_31e+" type: "+type+jetspeed.formatError(_31d));
}});
jetspeed.om.PageChangeActionCL=function(_320){
this.pageActionUrl=_320;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_322,_323){
if(jetspeed.url.checkAjaxApiResponse(_322,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_325,_326,_327){
dojo.raise("PageChangeActionCL error ["+_327.toString()+"] url: "+_326+" type: "+type+jetspeed.formatError(_325));
}});
jetspeed.om.PortletActionsCL=function(_328){
this.portletEntityIds=_328;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_329){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _32b=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_32b){
if(_329){
_32b.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_32b.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_32d,_32e){
this._loading(false);
if(jetspeed.url.checkAjaxApiResponse(_32d,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _330=this.parsePortletActionsResponse(node);
for(var i=0;i<_330.length;i++){
var _332=_330[i];
var _333=_332.id;
var _334=jetspeed.page.getPortlet(_333);
if(_334!=null){
_334.updateActions(_332.actions,_332.currentActionState,_332.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _336=new Array();
var _337=node.getElementsByTagName("js");
if(!_337||_337.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _336;
}
var _338=_337[0].childNodes;
for(var i=0;i<_338.length;i++){
var _33a=_338[i];
if(_33a.nodeType!=1){
continue;
}
var _33b=_33a.nodeName;
if(_33b=="portlets"){
var _33c=_33a;
var _33d=_33c.childNodes;
for(var pI=0;pI<_33d.length;pI++){
var _33f=_33d[pI];
if(_33f.nodeType!=1){
continue;
}
var _340=_33f.nodeName;
if(_340=="portlet"){
var _341=this.parsePortletElement(_33f);
if(_341!=null){
_336.push(_341);
}
}
}
}
}
return _336;
},parsePortletElement:function(node){
var _343=node.getAttribute("id");
if(_343!=null){
var _344=jetspeed.page._parsePSMLActions(node,null);
var _345=jetspeed.page._parsePSMLChildOrAttr(node,"state");
var _346=jetspeed.page._parsePSMLChildOrAttr(node,"mode");
return {id:_343,actions:_344,currentActionState:_345,currentActionMode:_346};
}
return null;
},notifyFailure:function(type,_348,_349,_34a){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_34a.toString()+"] url: "+_349+" type: "+type+jetspeed.formatError(_348));
}});
jetspeed.om.MoveApiCL=function(_34b,_34c){
this.portlet=_34b;
this.changedState=_34c;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_34d){
if(this.portlet==null){
return;
}
if(_34d){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_34f,_350){
this._loading(false);
dojo.lang.mixin(_350.portlet.lastSavedWindowState,this.changedState);
var _351=false;
if(djConfig.isDebug&&jetspeed.debug.submitWinState){
_351=true;
}
jetspeed.url.checkAjaxApiResponse(_34f,data,_351,("move-portlet ["+_350.portlet.entityId+"]"),jetspeed.debug.submitWinState);
},notifyFailure:function(type,_353,_354,_355){
this._loading(false);
dojo.debug("submitWinState error ["+_355.entityId+"] url: "+_354+" type: "+type+jetspeed.formatError(_353));
}};
jetspeed.ui={initCssObj:function(){
var _356=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _357=_356.concat([" height: ","","",";"]);
var _358=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _359=_357.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _35a=_359.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_356,cssHeight:_357,cssWidthHeight:_358,cssOverflow:_359,cssPosition:_35a,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_35b,_35c,_35d,_35e,_35f,_360){
var djH=dojo.html;
var jsId=jetspeed.id;
var _363=null;
var _364=-1;
var _365=-1;
var _366=-1;
if(_35b){
var _367=_35b.childNodes;
if(_367){
_366=_367.length;
}
_363=[];
if(_366>0){
var _368="",_369="";
if(!_360){
_368=jsId.PWIN_CLASS;
}
if(_35d){
_368+=((_368.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_35e){
_368+=((_368.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_35f&&!_35e){
_368+=((_368.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_35e&&!_35f){
_369=((_369.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_368.length>0){
var _36a=new RegExp("(^|\\s+)("+_368+")(\\s+|$)");
var _36b=null;
if(_369.length>0){
_36b=new RegExp("(^|\\s+)("+_369+")(\\s+|$)");
}
var _36c,_36d,_36e;
for(var i=0;i<_366;i++){
_36c=_367[i];
_36d=false;
_36e=djH.getClass(_36c);
if(_36a.test(_36e)&&(_36b==null||!_36b.test(_36e))){
_363.push(_36c);
_36d=true;
}
if(_35c&&_36c==_35c){
if(!_36d){
_363.push(_36c);
}
_364=i;
_365=_363.length-1;
}
}
}
}
}
return {matchingNodes:_363,totalNodes:_366,matchNodeIndex:_364,matchNodeIndexInMatchingNodes:_365};
},getPWinsFromNodes:function(_370){
var _371=jetspeed.page;
var _372=null;
if(_370){
_372=new Array();
for(var i=0;i<_370.length;i++){
var _374=_371.getPWin(_370[i].id);
if(_374){
_372.push(_374);
}
}
}
return _372;
},createPortletWindow:function(_375,_376,_377){
var _378=false;
if(djConfig.isDebug&&_377.debug.profile){
_378=true;
dojo.profile.start("createPortletWindow");
}
var _379=(_376!=null);
var _37a=false;
var _37b=null;
if(_379&&_376<_377.page.columns.length&&_376>=0){
_37b=_377.page.columns[_376].domNode;
}
if(_37b==null){
_37a=true;
_37b=document.getElementById(_377.id.DESKTOP);
}
if(_37b==null){
return;
}
var _37c={};
if(_375.isPortlet){
_37c.portlet=_375;
if(_377.prefs.printModeOnly!=null){
_37c.printMode=true;
}
if(_37a){
_375.properties[_377.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_377.widget.PortletWindow.prototype.altInitParamsDef(_37c,_375);
if(_37a){
pwP.altInitParams[_377.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _37e=new _377.widget.PortletWindow();
_37e.build(_37c,_37b);
if(_378){
dojo.profile.end("createPortletWindow");
}
},getLayoutExtents:function(node,_380,_381,_382){
if(!_380){
_380=_381.gcs(node);
}
var pad=_381._getPadExtents(node,_380);
var _384=_381._getBorderExtents(node,_380);
var _385={l:(pad.l+_384.l),t:(pad.t+_384.t),w:(pad.w+_384.w),h:(pad.h+_384.h)};
var _386=_381._getMarginExtents(node,_380,_382);
return {bE:_384,pE:pad,pbE:_385,mE:_386,lessW:(_385.w+_386.w),lessH:(_385.h+_386.h)};
},getContentBoxSize:function(node,_388){
var w=node.clientWidth,h,_38b;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_38b=_388.pbE;
}else{
h=node.clientHeight;
_38b=_388.pE;
}
return {w:(w-_38b.w),h:(h-_38b.h)};
},getMarginBoxSize:function(node,_38d){
return {w:(node.offsetWidth+_38d.mE.w),h:(node.offsetHeight+_38d.mE.h)};
},getMarginBox:function(node,_38f,_390,_391){
var l=node.offsetLeft-_38f.mE.l,t=node.offsetTop-_38f.mE.t;
if(_390&&_391.UAope){
l-=_390.bE.l;
t-=_390.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_38f.mE.w),h:(node.offsetHeight+_38f.mE.h)};
},setMarginBox:function(node,_395,_396,_397,_398,_399,_39a,_39b){
var pb=_399.pbE,mb=_399.mE;
if(_397!=null&&_397>=0){
_397=Math.max(_397-pb.w-mb.w,0);
}
if(_398!=null&&_398>=0){
_398=Math.max(_398-pb.h-mb.h,0);
}
_39b._setBox(node,_395,_396,_397,_398);
},evtConnect:function(_39e,_39f,_3a0,_3a1,_3a2,_3a3,rate){
if(!rate){
rate=0;
}
var _3a5={adviceType:_39e,srcObj:_39f,srcFunc:_3a0,adviceObj:_3a1,adviceFunc:_3a2,rate:rate};
if(_3a3==null){
_3a3=dojo.event;
}
_3a3.connect(_3a5);
return _3a5;
},evtDisconnect:function(_3a6,_3a7,_3a8,_3a9,_3aa,_3ab){
if(_3ab==null){
_3ab=dojo.event;
}
_3ab.disconnect({adviceType:_3a6,srcObj:_3a7,srcFunc:_3a8,adviceObj:_3a9,adviceFunc:_3aa});
},evtDisconnectWObj:function(_3ac,_3ad){
if(_3ad==null){
_3ad=dojo.event;
}
_3ad.disconnect(_3ac);
},evtDisconnectWObjAry:function(_3ae,_3af){
if(_3ae&&_3ae.length>0){
if(_3af==null){
_3af=dojo.event;
}
for(var i=0;i<_3ae.length;i++){
_3af.disconnect(_3ae[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _3b1=false;
var _3b2=this._popupMenuWidgets;
for(var i=0;i<_3b2.length;i++){
var _3b4=_3b2[i];
if(_3b4&&_3b4.isShowingNow){
_3b1=true;
break;
}
}
return _3b1;
},addPopupMenuWidget:function(_3b5){
if(_3b5){
this._popupMenuWidgets.push(_3b5);
}
},removePopupMenuWidget:function(_3b6){
if(!_3b6){
return;
}
var _3b7=this._popupMenuWidgets;
for(var i=0;i<_3b7.length;i++){
if(_3b7[i]===_3b6){
_3b7[i]=null;
}
}
}};
if(jetspeed.UAie6){
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_3ba){
this.oldXY=this.getWinDims(win,win.document,_3ba);
},getWinDims:function(win,doc,_3bd){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_3bd.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_3bd;
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
var _3c4=jetspeed;
var _3c5=this.getWinDims(window,window.document,_3c4.docBody);
this.timerId=0;
if((_3c5.x!=this.oldXY.x)||(_3c5.y!=this.oldXY.y)){
this.oldXY=_3c5;
if(_3c4.page){
if(!this.resizing){
try{
this.resizing=true;
_3c4.page.onBrowserWindowResize();
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

