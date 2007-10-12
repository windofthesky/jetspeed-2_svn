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
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",P_CLASS:"portlet",PWIN_CLASS:"portletWindow",PWIN_GHOST_CLASS:"ghostPane",PW_ID_PREFIX:"pw_",COL_CLASS:"desktopColumn",COL_LAYOUTHEADER_CLASS:"desktopLayoutHeader",PP_WIDGET_ID:"widgetId",PP_CONTENT_RETRIEVER:"contentRetriever",PP_DESKTOP_EXTENDED:"jsdesktop",PP_WINDOW_POSITION_STATIC:"windowPositionStatic",PP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PP_WINDOW_DECORATION:"windowDecoration",PP_WINDOW_TITLE:"title",PP_WINDOW_ICON:"windowIcon",PP_WIDTH:"width",PP_HEIGHT:"height",PP_LEFT:"left",PP_TOP:"top",PP_COLUMN:"column",PP_ROW:"row",PP_EXCLUDE_PCONTENT:"excludePContent",PP_WINDOW_STATE:"windowState",PP_STATICPOS:"staticpos",PP_FITHEIGHT:"fitheight",PP_PROP_SEPARATOR:"=",PP_PAIR_SEPARATOR:";",ACT_MENU:"menu",ACT_MINIMIZE:"minimized",ACT_MAXIMIZE:"maximized",ACT_RESTORE:"normal",ACT_PRINT:"print",ACT_EDIT:"edit",ACT_VIEW:"view",ACT_HELP:"help",ACT_ADDPORTLET:"addportlet",ACT_REMOVEPORTLET:"removeportlet",ACT_DESKTOP_TILE:"tile",ACT_DESKTOP_UNTILE:"untile",ACT_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACT_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACT_DESKTOP_MOVE_TILED:"movetiled",ACT_DESKTOP_MOVE_UNTILED:"moveuntiled",ACT_LOAD_RENDER:"loadportletrender",ACT_LOAD_ACTION:"loadportletaction",ACT_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",PORTAL_ORIGINATE_PARAMETER:"portal",DEBUG_WINDOW_TAG:"js-db"};
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
},portletSelectorWindowTitle:"Portlet Selector",portletSelectorWindowIcon:"text-x-script.png",portletSelectorBounds:{x:20,y:20,width:400,height:600},windowActionButtonMax:5,windowActionButtonHide:false,windowActionButtonTooltip:true,windowIconEnabled:true,windowIconPath:"/images/portlets/small/",windowTitlebar:true,windowResizebar:true,windowDecoration:"tigris",pageActionButtonTooltip:true,getPortletDecorationBaseUrl:function(_1){
return jetspeed.prefs.getPortletDecorationsRootUrl()+"/"+_1;
},getPortletDecorationConfig:function(_2){
if(jetspeed.prefs.portletDecorationsConfig==null||_2==null){
return null;
}
return jetspeed.prefs.portletDecorationsConfig[_2];
},getActionLabel:function(_3,_4,_5,_6){
if(_3==null){
return null;
}
var _7=null;
var _8=_5.desktopActionLabels;
if(_8!=null){
_7=_8[_3];
}
if(_7==null||_7.length==0){
_7=null;
if(!_4){
_7=_6.string.capitalize(_3);
}
}
return _7;
}};
jetspeed.page=null;
jetspeed.initializeDesktop=function(){
var _9=jetspeed;
var _a=_9.id;
var _b=_9.prefs;
var _c=_9.debug;
var _d=dojo;
_9.getBody();
_9.ui.initCssObj();
_b.windowActionButtonOrder=[_a.ACT_MENU,"edit","view","help",_a.ACT_MINIMIZE,_a.ACT_RESTORE,_a.ACT_MAXIMIZE];
_b.windowActionNotPortlet=[_a.ACT_MENU,_a.ACT_MINIMIZE,_a.ACT_RESTORE,_a.ACT_MAXIMIZE];
_b.windowActionMenuOrder=[_a.ACT_DESKTOP_HEIGHT_EXPAND,_a.ACT_DESKTOP_HEIGHT_NORMAL,_a.ACT_DESKTOP_TILE,_a.ACT_DESKTOP_UNTILE];
_9.url.pathInitialize();
var _e=djConfig.jetspeed;
if(_e!=null){
for(var _f in _e){
var _10=_e[_f];
if(_10!=null){
if(_c[_f]!=null){
_c[_f]=_10;
}else{
_b[_f]=_10;
}
}
}
if(_b.windowWidth==null||isNaN(_b.windowWidth)){
_b.windowWidth="280";
}
if(_b.windowHeight==null||isNaN(_b.windowHeight)){
_b.windowHeight="200";
}
var _11={};
_11[_a.ACT_DESKTOP_HEIGHT_EXPAND]=true;
_11[_a.ACT_DESKTOP_HEIGHT_NORMAL]=true;
_11[_a.ACT_DESKTOP_TILE]=true;
_11[_a.ACT_DESKTOP_UNTILE]=true;
_b.windowActionDesktop=_11;
}
var _12=new _d.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PortletWindow.css");
_d.html.insertCssFile(_12,document,true);
if(_b.portletDecorationsAllowed==null||_b.portletDecorationsAllowed.length==0){
if(_b.windowDecoration!=null){
_b.portletDecorationsAllowed=[_b.windowDecoration];
}
}else{
if(_b.windowDecoration==null){
_b.windowDecoration=_b.portletDecorationsAllowed[0];
}
}
if(_b.windowDecoration==null||_b.portletDecorationsAllowed==null){
_d.raise("No portlet decorations");
return;
}
if(_b.windowActionNoImage!=null){
var _13={};
for(var i=0;i<_b.windowActionNoImage.length;i++){
_13[_b.windowActionNoImage[i]]=true;
}
_b.windowActionNoImage=_13;
}
var _15=_9.url.parse(window.location.href);
var _16=_9.url.getQueryParameter(_15,"jsprintmode")=="true";
if(_16){
_16={};
_16.action=_9.url.getQueryParameter(_15,"jsaction");
_16.entity=_9.url.getQueryParameter(_15,"jsentity");
_16.layout=_9.url.getQueryParameter(_15,"jslayoutid");
_b.printModeOnly=_16;
_b.windowTiling=true;
_b.windowHeightExpand=true;
_b.ajaxPageNavigation=false;
}
_b.portletDecorationsConfig={};
for(var i=0;i<_b.portletDecorationsAllowed.length;i++){
_9.loadPortletDecorationConfig(_b.portletDecorationsAllowed[i]);
}
if(_9.UAie6){
_b.ajaxPageNavigation=false;
}
if(_16){
for(var _17 in _b.portletDecorationsConfig){
var _18=_b.portletDecorationsConfig[_17];
if(_18!=null){
_18.windowActionButtonOrder=null;
_18.windowActionMenuOrder=null;
_18.windowDisableResize=true;
_18.windowDisableMove=true;
}
}
}
_9.url.loadingIndicatorShow();
if(_b.windowActionButtonOrder){
var _19={};
var _1a,_1b;
for(var aI=0;aI<_b.windowActionButtonOrder.length;aI++){
_1a=_b.windowActionButtonOrder[aI];
if(_1a!=null){
_19[_1a]=_b.getActionLabel(_1a,false,_b,_d);
}
}
for(_1a in _b.windowActionDesktop){
if(_1a!=null){
_19[_1a]=_b.getActionLabel(_1a,false,_b,_d);
}
}
_1a=_a.ACT_DESKTOP_MOVE_TILED;
_1b=_b.getActionLabel(_1a,true,_b,_d);
if(_1b!=null){
_19[_1a]=_1b;
}
_1a=_a.ACT_DESKTOP_MOVE_UNTILED;
_1b=_b.getActionLabel(_1a,true,_b,_d);
if(_1b!=null){
_19[_1a]=_1b;
}
_9.widget.PortletWindow.prototype.actionLabels=_19;
}
_9.page=new _9.om.Page();
if(!_16&&djConfig.isDebug){
if(_9.debugWindowLoad){
_9.debugWindowLoad();
}
if(_9.debug.profile&&_d.profile){
_d.profile.start("initializeDesktop");
}else{
_9.debug.profile=false;
}
}else{
_9.debug.profile=false;
}
_9.page.retrievePsml();
if(_9.UAie6){
_9.ui.windowResizeMgr.init(window,_9.docBody);
}else{
}
};
jetspeed.updatePage=function(_1d,_1e){
var _1f=jetspeed;
var _20=false;
if(djConfig.isDebug&&_1f.debug.profile){
_20=true;
dojo.profile.start("updatePage");
}
var _21=_1f.page;
if(!_1d||!_21||_1f.pageNavigateSuppress){
return;
}
if(_21.equalsPageUrl(_1d)){
return;
}
_1d=_21.makePageUrl(_1d);
if(_1d!=null){
_1f.updatePageBegin();
var _22=_21.layoutDecorator;
var _23=_21.editMode;
if(_20){
dojo.profile.start("destroyPage");
}
_21.destroy();
if(_20){
dojo.profile.end("destroyPage");
}
var _24=_21.portlet_windows;
var _25=_21.portlet_window_count;
var _26=new _1f.om.Page(_22,_1d,(!djConfig.preventBackButtonFix&&!_1e),_23,_21.tooltipMgr,_21.iframeCoverByWinId);
_1f.page=_26;
var _27;
if(_25>0){
for(var _28 in _24){
_27=_24[_28];
_27.bringToTop(null,true,false,_1f);
}
}
_26.retrievePsml(new _1f.om.PageCLCreateWidget(true));
if(_25>0){
for(var _28 in _24){
_27=_24[_28];
_26.putPWin(_27);
}
}
window.focus();
}
};
jetspeed.updatePageBegin=function(){
var _29=jetspeed;
if(_29.UAie6){
_29.docBody.attachEvent("onclick",_29.ie6StopMouseEvts);
_29.docBody.setCapture();
}
};
jetspeed.ie6StopMouseEvts=function(e){
if(e){
e.cancelBubble=true;
e.returnValue=false;
}
};
jetspeed.updatePageEnd=function(){
var _2b=jetspeed;
if(_2b.UAie6){
_2b.docBody.releaseCapture();
_2b.docBody.detachEvent("onclick",_2b.ie6StopMouseEvts);
_2b.docBody.releaseCapture();
}
};
jetspeed.doRender=function(_2c,_2d){
if(!_2c){
_2c={};
}else{
if((typeof _2c=="string"||_2c instanceof String)){
_2c={url:_2c};
}
}
var _2e=jetspeed.page.getPortlet(_2d);
if(_2e){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_2d+"] url: "+_2c.url);
}
_2e.retrieveContent(null,_2c);
}
};
jetspeed.doAction=function(_2f,_30){
if(!_2f){
_2f={};
}else{
if((typeof _2f=="string"||_2f instanceof String)){
_2f={url:_2f};
}
}
var _31=jetspeed.page.getPortlet(_30);
if(_31){
if(jetspeed.debug.doRenderDoAction){
if(!_2f.formNode){
dojo.debug("doAction ["+_30+"] url: "+_2f.url+" form: null");
}else{
dojo.debug("doAction ["+_30+"] url: "+_2f.url+" form: "+jetspeed.debugDumpForm(_2f.formNode));
}
}
_31.retrieveContent(new jetspeed.om.PortletActionCL(_31,_2f),_2f);
}
};
jetspeed.PortletRenderer=function(_32,_33,_34,_35,_36){
var _37=jetspeed;
var _38=_37.page;
this._jsObj=_37;
this.createWindows=_32;
this.isPageLoad=_33;
this.isPageUpdate=_34;
this.pageLoadUrl=null;
if(_33){
this.pageLoadUrl=_37.url.parse(_38.getPageUrl());
}
this.renderUrl=_35;
this.suppressGetActions=_36;
this._colLen=_38.columns.length;
this._colIndex=0;
this._portletIndex=0;
this.psByCol=_38.portletsByPageColumn;
this.debugPageLoad=_37.debug.pageLoad&&_33;
this.debugMsg=null;
if(_37.debug.doRenderDoAction||this.debugPageLoad){
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
var _39=this._jsObj;
var _3a=this.debugMsg;
if(_3a!=null){
if(this.debugPageLoad){
dojo.debug("portlet-renderer page-url: "+_39.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPageLoad){
_39.page.loadPostRender(this.isPageUpdate);
}
},_renderCurrent:function(){
var _3b=this._jsObj;
var _3c=this._colLen;
var _3d=this._colIndex;
var _3e=this._portletIndex;
if(_3d<=_3c){
var _3f;
if(_3d<_3c){
_3f=this.psByCol[_3d.toString()];
}else{
_3f=this.psByCol["z"];
_3d=null;
}
var _40=(_3f!=null?_3f.length:0);
if(_40>0){
var _41=_3f[_3e];
if(_41){
var _42=_41.portlet;
if(this.createWindows){
_3b.ui.createPortletWindow(_42,_3d,_3b);
}
var _43=this.debugMsg;
if(_43!=null){
if(_43.length>0){
_43=_43+", ";
}
var _44=null;
if(_42.getProperty!=null){
_44=_42.getProperty(_3b.id.PP_WIDGET_ID);
}
if(!_44){
_44=_42.widgetId;
}
if(!_44){
_44=_42.toString();
}
if(_42.entityId){
_43=_43+_42.entityId+"("+_44+")";
if(this._dbPgLd&&_42.getProperty(_3b.id.PP_WINDOW_TITLE)){
_43=_43+" "+_42.getProperty(_3b.id.PP_WINDOW_TITLE);
}
}else{
_43=_43+_44;
}
}
_42.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}
}
}
},_evalNext:function(){
var _45=false;
var _46=this._colLen;
var _47=this._colIndex;
var _48=this._portletIndex;
var _49=_47;
var _4a;
for(++_47;_47<=_46;_47++){
_4a=this.psByCol[_47==_46?"z":_47.toString()];
if(_48<(_4a!=null?_4a.length:0)){
_45=true;
this._colIndex=_47;
break;
}
}
if(!_45){
++_48;
for(_47=0;_47<=_49;_47++){
_4a=this.psByCol[_47==_46?"z":_47.toString()];
if(_48<(_4a!=null?_4a.length:0)){
_45=true;
this._colIndex=_47;
this._portletIndex=_48;
break;
}
}
}
return _45;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_4b){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _4d=_4b;
var _4e=null;
if(_4b&&_4b.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_4b.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_4b&&_4b.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_4b.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_4e=jetspeed.url.getQueryParameter(_4b,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_4d)){
_4d=null;
}
return {url:_4d,operation:op,portletEntityId:_4e};
},genPseudoUrl:function(_4f,_50){
if(!_4f||!_4f.url||!_4f.portletEntityId){
return null;
}
var _51=null;
if(_50){
_51=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_51="javascript:";
var _52=false;
if(_4f.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_51+="doAction(\"";
}else{
if(_4f.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_51+="doRender(\"";
}else{
_52=true;
}
}
if(_52){
return null;
}
_51+=_4f.url+"\",\""+_4f.portletEntityId+"\"";
_51+=")";
}
return _51;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_53){
var _54=jetspeed.prefs.getPortletDecorationConfig(_53);
if(_54!=null&&!_54._initialized){
var _55=jetspeed.prefs.getPortletDecorationBaseUrl(_53);
_54._initialized=true;
_54.cssPathCommon=new dojo.uri.Uri(_55+"/css/styles.css");
_54.cssPathDesktop=new dojo.uri.Uri(_55+"/css/desktop.css");
dojo.html.insertCssFile(_54.cssPathCommon,null,true);
dojo.html.insertCssFile(_54.cssPathDesktop,null,true);
}
return _54;
};
jetspeed.loadPortletDecorationConfig=function(_56){
var _57=jetspeed.prefs;
var _58={};
_57.portletDecorationsConfig[_56]=_58;
_58.windowActionButtonOrder=_57.windowActionButtonOrder;
_58.windowActionNotPortlet=_57.windowActionNotPortlet;
_58.windowActionButtonMax=_57.windowActionButtonMax;
_58.windowActionButtonHide=_57.windowActionButtonHide;
_58.windowActionButtonTooltip=_57.windowActionButtonTooltip;
_58.windowActionMenuOrder=_57.windowActionMenuOrder;
_58.windowActionNoImage=_57.windowActionNoImage;
_58.windowIconEnabled=_57.windowIconEnabled;
_58.windowIconPath=_57.windowIconPath;
_58.windowTitlebar=_57.windowTitlebar;
_58.windowResizebar=_57.windowResizebar;
var _59=_57.getPortletDecorationBaseUrl(_56)+"/"+_56+".js";
dojo.hostenv.loadUri(_59,function(_5a){
for(var j in _5a){
_58[j]=_5a[j];
}
if(_58.windowActionNoImage!=null){
var _5c={};
for(var i=0;i<_58.windowActionNoImage.length;i++){
_5c[_58.windowActionNoImage[i]]=true;
}
_58.windowActionNoImage=_5c;
}
if(_58.windowIconPath!=null){
_58.windowIconPath=dojo.string.trim(_58.windowIconPath);
if(_58.windowIconPath==null||_58.windowIconPath.length==0){
_58.windowIconPath=null;
}else{
var _5e=_58.windowIconPath;
var _5f=_5e.charAt(0);
if(_5f!="/"){
_5e="/"+_5e;
}
var _60=_5e.charAt(_5e.length-1);
if(_60!="/"){
_5e=_5e+"/";
}
_58.windowIconPath=_5e;
}
}
});
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
var _61=jetspeed;
_61.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _62=_61.page.getMenuNames();
for(var i=0;i<_62.length;i++){
var _64=_62[i];
var _65=dojo.widget.byId(_61.id.MENU_WIDGET_ID_PREFIX+_64);
if(_65){
_65.createJetspeedMenu(_61.page.getMenu(_64));
}
}
_61.url.loadingIndicatorHide();
_61.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_66){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_66);
}
};
jetspeed.menuNavClickWidget=function(_67,_68){
dojo.debug("jetspeed.menuNavClick");
if(!_67){
return;
}
if(dojo.lang.isString(_67)){
var _69=_67;
_67=dojo.widget.byId(_69);
if(!_67){
dojo.raise("Tab widget not found: "+_69);
}
}
if(_67){
var _6a=_67.jetspeedmenuname;
if(!_6a&&_67.extraArgs){
_6a=_67.extraArgs.jetspeedmenuname;
}
if(!_6a){
dojo.raise("Tab widget is invalid: "+_67.widgetId);
}
var _6b=jetspeed.page.getMenu(_6a);
if(!_6b){
dojo.raise("Tab widget "+_67.widgetId+" no menu: "+_6a);
}
var _6c=_6b.getOptionByIndex(_68);
jetspeed.menuNavClick(_6c);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_6d,_6e,_6f){
if(!_6d||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _6f=="undefined"){
_6f=false;
}
if(!_6f&&jetspeed.page&&jetspeed.page.equalsPageUrl(_6d)){
return;
}
_6d=jetspeed.page.makePageUrl(_6d);
if(_6e=="top"){
top.location.href=_6d;
}else{
if(_6e=="parent"){
parent.location.href=_6d;
}else{
window.location.href=_6d;
}
}
};
jetspeed.getActionsForPortlet=function(_70){
if(_70==null){
return;
}
jetspeed.getActionsForPortlets([_70]);
};
jetspeed.getActionsForPortlets=function(_71){
if(_71==null){
_71=jetspeed.page.getPortletIds();
}
var _72=new jetspeed.om.PortletActionsCL(_71);
var _73="?action=getactions";
for(var i=0;i<_71.length;i++){
_73+="&id="+_71[i];
}
var _75=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_73;
var _76="text/xml";
var _77=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_75,mimetype:_76},_72,_77,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_78,_79,_7a,_7b){
if(_78==null){
return;
}
if(_7b==null){
_7b=new jetspeed.om.PortletChangeActionCL(_78);
}
var _7c="?action=window&id="+(_78!=null?_78:"");
if(_79!=null){
_7c+="&state="+_79;
}
if(_7a!=null){
_7c+="&mode="+_7a;
}
var _7d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7c;
var _7e="text/xml";
var _7f=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_7d,mimetype:_7e},_7b,_7f,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_80){
if(!_80.page.editMode){
var _81=true;
var _82=_80.url.getQueryParameter(window.location.href,_80.id.PORTAL_ORIGINATE_PARAMETER);
if(_82!=null&&_82=="true"){
_81=false;
}
_80.page.editMode=true;
var _83=dojo.widget.byId(_80.id.PG_ED_WID);
if(_80.UAie6){
_80.page.displayAllPWins(true);
}
if(_83==null){
try{
_80.url.loadingIndicatorShow("loadpageeditor");
_83=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_80.id.PG_ED_WID,editorInitiatedFromDesktop:_81});
var _84=document.getElementById(_80.id.COLUMNS);
_84.insertBefore(_83.domNode,_84.firstChild);
}
catch(e){
_80.url.loadingIndicatorHide();
if(_80.UAie6){
_80.page.displayAllPWins();
}
}
}else{
_83.editPageShow();
}
_80.page.syncPageControls(_80);
}
};
jetspeed.editPageTerminate=function(_85){
if(_85.page.editMode){
var _86=dojo.widget.byId(_85.id.PG_ED_WID);
_86.editMoveModeExit();
_85.page.editMode=false;
if(!_86.editorInitiatedFromDesktop){
var _87=_85.page.getPageUrl(true);
_87=_85.url.removeQueryParameter(_87,_85.id.PG_ED_PARAM);
_87=_85.url.removeQueryParameter(_87,_85.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_87;
}else{
var _88=_85.url.getQueryParameter(window.location.href,_85.id.PG_ED_PARAM);
if(_88!=null&&_88=="true"){
var _89=window.location.href;
_89=_85.url.removeQueryParameter(_89,_85.id.PG_ED_PARAM);
window.location.href=_89;
}else{
if(_86!=null){
_86.editPageHide();
}
_85.page.syncPageControls(_85);
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_8a,_8b,_8c,_8d){
if(!_8a){
_8a={};
}
jetspeed.url.retrieveContent(_8a,_8b,_8c,_8d);
}};
jetspeed.om.PageCLCreateWidget=function(_8e){
if(typeof _8e=="undefined"){
_8e=false;
}
this.isPageUpdate=_8e;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_8f,_90,_91){
_91.loadFromPSML(_8f,this.isPageUpdate);
},notifyFailure:function(_92,_93,_94,_95){
dojo.raise("PageCLCreateWidget error url: "+_94+" type: "+_92+jetspeed.formatError(_93));
}};
jetspeed.om.Page=function(_96,_97,_98,_99,_9a,_9b){
if(_96!=null&&_97!=null){
this.requiredLayoutDecorator=_96;
this.setPsmlPathFromDocumentUrl(_97);
this.pageUrlFallback=_97;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _98!="undefined"){
this.addToHistory=_98;
}
if(typeof _99!="undefined"){
this.editMode=_99;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets=[];
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_9b!=null){
this.iframeCoverByWinId=_9b;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_9a!=null){
this.tooltipMgr=_9a;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _9c=(this.name!=null&&this.name.length>0?this.name:null);
if(!_9c){
this.getPsmlUrl();
_9c=this.psmlPath;
}
return "page-"+_9c;
},setPsmlPathFromDocumentUrl:function(_9d){
var _9e=jetspeed;
var _9f=_9e.url.path.AJAX_API;
var _a0=null;
if(_9d==null){
_a0=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_9e.prefs.ajaxPageNavigation){
var _a1=window.location.hash;
if(_a1!=null&&_a1.length>0){
if(_a1.indexOf("#")==0){
_a1=(_a1.length>1?_a1.substring(1):"");
}
if(_a1!=null&&_a1.length>1&&_a1.indexOf("/")==0){
this.psmlPath=_9e.url.path.AJAX_API+_a1;
return;
}
}
}
}else{
var _a2=_9e.url.parse(_9d);
_a0=_a2.path;
}
var _a3=_9e.url.path.DESKTOP;
var _a4=_a0.indexOf(_a3);
if(_a4!=-1&&_a0.length>(_a4+_a3.length)){
_9f=_9f+_a0.substring(_a4+_a3.length);
}
this.psmlPath=_9f;
},getPsmlUrl:function(){
var _a5=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _a6=_a5.url.basePortalUrl()+this.psmlPath;
if(_a5.prefs.printModeOnly!=null){
_a6=_a5.url.addQueryParameter(_a6,"layoutid",_a5.prefs.printModeOnly.layout);
_a6=_a5.url.addQueryParameter(_a6,"entity",_a5.prefs.printModeOnly.entity).toString();
}
return _a6;
},retrievePsml:function(_a7){
var _a8=jetspeed;
if(_a7==null){
_a7=new _a8.om.PageCLCreateWidget();
}
var _a9=this.getPsmlUrl();
var _aa="text/xml";
if(_a8.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_a9);
}
_a8.url.retrieveContent({url:_a9,mimetype:_aa},_a7,this,_a8.debugContentDumpIds);
},loadFromPSML:function(_ab,_ac){
var _ad=jetspeed;
var _ae=dojo;
var _af=_ad.prefs.printModeOnly;
if(djConfig.isDebug&&_ad.debug.profile&&_af==null){
_ae.profile.start("loadFromPSML");
}
var _b0=this._parsePSML(_ab);
if(_b0==null){
return;
}
this.portletsByPageColumn={};
this.columnsStructure=this._layoutCreateModel(_b0,null,this.portletsByPageColumn,true,_ae,_ad);
this.rootFragmentId=_b0.id;
var _b1=false;
if(this.editMode){
this.editMode=false;
if(_af==null){
_b1=true;
}
}
if(_ad.prefs.windowTiling){
this._createColsStart(document.getElementById(_ad.id.DESKTOP),_ad.id.COLUMNS);
}
this.createLayoutInfo(_ad);
var _b2=this.portletsByPageColumn["z"];
if(_b2){
_b2.sort(this._loadPortletZIndexCompare);
}
var _b3=new _ad.PortletRenderer(true,true,_ac,null,true);
_b3.renderAllTimeDistribute();
},loadPostRender:function(_b4){
var _b5=jetspeed;
var _b6=_b5.prefs.printModeOnly;
if(_b6==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
var _b7=false;
if(this.editMode){
_b7=true;
}
var _b8=_b5.url.getQueryParameter(window.location.href,_b5.id.PG_ED_PARAM);
if(_b7||(_b8!=null&&_b8=="true")||this.actions[_b5.id.ACT_VIEW]!=null){
_b7=false;
if(this.actions!=null&&(this.actions[_b5.id.ACT_EDIT]!=null||this.actions[_b5.id.ACT_VIEW]!=null)){
_b7=true;
}
}
this.retrieveMenuDeclarations(true,_b7,_b4);
this.renderPageControls(_b5);
this.syncPageControls(_b5);
}else{
for(var _b9 in this.portlets){
var _ba=this.portlets[_b9];
if(_ba!=null){
_ba.renderAction(null,_b6.action);
}
break;
}
if(_b4){
_b5.updatePageEnd();
}
}
if(_b5.UAie6){
_b5.ui.evtConnect("after",window,"onresize",_b5.ui.windowResizeMgr,"onResize",dojo.event);
_b5.ui.windowResizeMgr.onResizeDelayedCompare();
}
var _bb,_bc=this.columns;
if(_bc){
for(var i=0;i<_bc.length;i++){
_bb=_bc[i].domNode;
if(!_bb.childNodes||_bb.childNodes.length==0){
_bb.style.height="1px";
}
}
}
var _be=this.maximizedOnInit;
if(_be!=null){
var _bf=this.getPWin(_be);
if(_bf==null){
dojo.raise("Cannot identify window to maximize");
}else{
dojo.lang.setTimeout(_bf,_bf._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
},_parsePSML:function(_c0){
var _c1=jetspeed;
var _c2=_c0.getElementsByTagName("page");
if(!_c2||_c2.length>1||_c2[0]==null){
dojo.raise("Expected one <page> in PSML");
}
var _c3=_c2[0];
var _c4=_c3.childNodes;
var _c5=new RegExp("(name|path|profiledPath|title|short-title)");
var _c6=null;
var _c7={};
for(var i=0;i<_c4.length;i++){
var _c9=_c4[i];
if(_c9.nodeType!=1){
continue;
}
var _ca=_c9.nodeName;
if(_ca=="fragment"){
_c6=_c9;
}else{
if(_ca=="defaults"){
this.layoutDecorator=_c9.getAttribute("layout-decorator");
this.portletDecorator=_c9.getAttribute("portlet-decorator");
}else{
if(_ca&&_ca.match(_c5)){
if(_ca=="short-title"){
_ca="shortTitle";
}
this[_ca]=((_c9&&_c9.firstChild)?_c9.firstChild.nodeValue:null);
}else{
if(_ca=="action"){
this._parsePSMLAction(_c9,_c7);
}
}
}
}
}
this.actions=_c7;
if(_c6==null){
dojo.raise("No root fragment in PSML");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_c1.debug.ajaxPageNav){
dojo.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_c1.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _cb=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(_c1.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_cb);
}
_c1.updatePage(_cb,true);
},forward:function(){
if(_c1.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_cb);
}
_c1.updatePage(_cb,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_c1.prefs.ajaxPageNavigation){
var _cb=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(_c1.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_cb);
}
_c1.updatePage(_cb,true);
},forward:function(){
if(_c1.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_cb);
}
_c1.updatePage(_cb,true);
},changeUrl:escape(this.getPath())});
}
}
var _cc=this._parsePSMLFrag(_c6,0);
return _cc;
},_parsePSMLFrag:function(_cd,_ce){
var _cf=jetspeed;
var _d0=new Array();
var _d1=((_cd!=null)?_cd.getAttribute("type"):null);
if(_d1!="layout"){
dojo.raise("Expected layout fragment: "+_cd);
return null;
}
var _d2=false;
var _d3=_cd.getAttribute("name");
if(_d3!=null){
_d3=_d3.toLowerCase();
if(_d3.indexOf("noactions")!=-1){
_d2=true;
}
}
var _d4=null,_d5=0;
var _d6={};
var _d7=_cd.childNodes;
var _d8,_d9,_da,_db,_dc;
for(var i=0;i<_d7.length;i++){
_d8=_d7[i];
if(_d8.nodeType!=1){
continue;
}
_d9=_d8.nodeName;
if(_d9=="fragment"){
_dc=_d8.getAttribute("type");
if(_dc=="layout"){
var _de=this._parsePSMLFrag(_d8,i);
if(_de!=null){
_d0.push(_de);
}
}else{
var _df=this._parsePSMLProps(_d8,null);
var _e0=_df[_cf.id.PP_WINDOW_ICON];
if(_e0==null||_e0.length==0){
_e0=this._parsePSMLChildOrAttr(_d8,"icon");
if(_e0!=null&&_e0.length>0){
_df[_cf.id.PP_WINDOW_ICON]=_e0;
}
}
_d0.push({id:_d8.getAttribute("id"),type:_dc,name:_d8.getAttribute("name"),properties:_df,actions:this._parsePSMLActions(_d8,null),currentActionState:this._parsePSMLChildOrAttr(_d8,"state"),currentActionMode:this._parsePSMLChildOrAttr(_d8,"mode"),decorator:_d8.getAttribute("decorator"),layoutActionsDisabled:_d2,documentOrderIndex:i});
}
}else{
if(_d9=="property"){
if(this._parsePSMLProp(_d8,_d6)=="sizes"){
if(_d4!=null){
dojo.raise("Layout fragment has multiple sizes definitions: "+_cd);
return null;
}
if(_cf.prefs.printModeOnly!=null){
_d4=["100"];
_d5=100;
}else{
_db=_d8.getAttribute("value");
if(_db!=null&&_db.length>0){
_d4=_db.split(",");
for(var j=0;j<_d4.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_d4[j]=_d4[j].replace(re,"$1");
_d5+=new Number(_d4[j]);
}
}
}
}
}
}
}
_d0.sort(this._fragmentRowCompare);
if(_d4==null){
_d4=new Array();
_d4.push("100");
_d5=100;
}
return {id:_cd.getAttribute("id"),type:_d1,name:_cd.getAttribute("name"),decorator:_cd.getAttribute("decorator"),columnSizes:_d4,columnSizesSum:_d5,properties:_d6,fragments:_d0,layoutActionsDisabled:_d2,documentOrderIndex:_ce};
},_parsePSMLActions:function(_e3,_e4){
if(_e4==null){
_e4={};
}
var _e5=_e3.getElementsByTagName("action");
for(var _e6=0;_e6<_e5.length;_e6++){
var _e7=_e5[_e6];
this._parsePSMLAction(_e7,_e4);
}
return _e4;
},_parsePSMLAction:function(_e8,_e9){
var _ea=_e8.getAttribute("id");
if(_ea!=null){
var _eb=_e8.getAttribute("type");
var _ec=_e8.getAttribute("name");
var _ed=_e8.getAttribute("url");
var _ee=_e8.getAttribute("alt");
_e9[_ea.toLowerCase()]={id:_ea,type:_eb,label:_ec,url:_ed,alt:_ee};
}
},_parsePSMLChildOrAttr:function(_ef,_f0){
var _f1=null;
var _f2=_ef.getElementsByTagName(_f0);
if(_f2!=null&&_f2.length==1&&_f2[0].firstChild!=null){
_f1=_f2[0].firstChild.nodeValue;
}
if(!_f1){
_f1=_ef.getAttribute(_f0);
}
if(_f1==null||_f1.length==0){
_f1=null;
}
return _f1;
},_parsePSMLProps:function(_f3,_f4){
if(_f4==null){
_f4={};
}
var _f5=_f3.getElementsByTagName("property");
for(var _f6=0;_f6<_f5.length;_f6++){
this._parsePSMLProp(_f5[_f6],_f4);
}
return _f4;
},_parsePSMLProp:function(_f7,_f8){
var _f9=_f7.getAttribute("name");
var _fa=_f7.getAttribute("value");
_f8[_f9]=_fa;
return _f9;
},_fragmentRowCompare:function(_fb,_fc){
var _fd=_fb.documentOrderIndex*1000;
var _fe=_fc.documentOrderIndex*1000;
var _ff=_fb.properties["row"];
if(_ff!=null){
_fd=_ff;
}
var _100=_fc.properties["row"];
if(_100!=null){
_fe=_100;
}
return (_fd-_fe);
},_layoutCreateModel:function(_101,_102,_103,_104,_105,_106){
var _107=this.columns.length;
var _108=this._layoutCreateColsModel(_101,_102,_104);
var _109=_108.columnsInLayout;
if(_108.addedLayoutHeaderColumn){
_107++;
}
var _10a=(_109==null?0:_109.length);
var _10b=new Array(_10a);
var _10c=new Array(_10a);
for(var i=0;i<_101.fragments.length;i++){
var _10e=_101.fragments[i];
if(_10e.type=="layout"){
var _10f=i;
var _10f=(_10e.properties?_10e.properties[_106.id.PP_COLUMN]:i);
if(_10f==null||_10f<0||_10f>=_10a){
_10f=(_10a>0?(_10a-1):0);
}
_10c[_10f]=true;
this._layoutCreateModel(_10e,_109[_10f],_103,false,_105,_106);
}else{
this._layoutCreatePortlet(_10e,_101,_109,_107,_103,_10b,_105,_106);
}
}
return _109;
},_layoutCreatePortlet:function(_110,_111,_112,_113,_114,_115,_116,_117){
if(_110&&_117.debugPortletEntityIdFilter){
if(!_116.lang.inArray(_117.debugPortletEntityIdFilter,_110.id)){
_110=null;
}
}
if(_110){
var _118="z";
var _119=_110.properties[_117.id.PP_DESKTOP_EXTENDED];
var _11a=_117.prefs.windowTiling;
var _11b=_11a;
var _11c=_117.prefs.windowHeightExpand;
if(_119!=null&&_11a&&_117.prefs.printModeOnly==null){
var _11d=_119.split(_117.id.PP_PAIR_SEPARATOR);
var _11e=null,_11f=0,_120=null,_121=null,_122=false;
if(_11d!=null&&_11d.length>0){
var _123=_117.id.PP_PROP_SEPARATOR;
for(var _124=0;_124<_11d.length;_124++){
_11e=_11d[_124];
_11f=((_11e!=null)?_11e.length:0);
if(_11f>0){
var _125=_11e.indexOf(_123);
if(_125>0&&_125<(_11f-1)){
_120=_11e.substring(0,_125);
_121=_11e.substring(_125+1);
_122=((_121=="true")?true:false);
if(_120==_117.id.PP_STATICPOS){
_11b=_122;
}else{
if(_120==_117.id.PP_FITHEIGHT){
_11c=_122;
}
}
}
}
}
}
}else{
if(!_11a){
_11b=false;
}
}
_110.properties[_117.id.PP_WINDOW_POSITION_STATIC]=_11b;
_110.properties[_117.id.PP_WINDOW_HEIGHT_TO_FIT]=_11c;
if(_11b&&_11a){
var _126=_110.properties[_117.id.PP_COLUMN];
if(_126==null||_126==""||_126<0){
var _127=-1;
for(var j=0;j<_112.length;j++){
var _129=(_115[j]?_115[j].length:0);
if(_127==-1||_129<_127){
_127=_129;
_126=j;
}
}
}else{
if(_126>=_112.length){
_126=_112.length-1;
}
}
if(_115[_126]==null){
_115[_126]=new Array();
}
_115[_126].push(_110.id);
var _12a=_113+new Number(_126);
_118=_12a.toString();
}
if(_110.currentActionState==_117.id.ACT_MAXIMIZE){
this.maximizedOnInit=_110.id;
}
var _12b=new _117.om.Portlet(_110.name,_110.id,null,_110.properties,_110.actions,_110.currentActionState,_110.currentActionMode,_110.decorator,_110.layoutActionsDisabled);
_12b.initialize();
this.putPortlet(_12b);
if(_114[_118]==null){
_114[_118]=new Array();
}
_114[_118].push({portlet:_12b,layout:_111.id});
}
},_layoutCreateColsModel:function(_12c,_12d,_12e){
var _12f=jetspeed;
this.layouts[_12c.id]=_12c;
var _130=false;
var _131=new Array();
if(_12f.prefs.windowTiling&&_12c.columnSizes.length>0){
var _132=false;
if(_12f.UAie){
_132=true;
}
if(_12d!=null&&!_12e){
var _133=new _12f.om.Column(0,_12c.id,(_132?_12c.columnSizesSum-0.1:_12c.columnSizesSum),this.columns.length,_12c.layoutActionsDisabled);
_133.layoutHeader=true;
this.columns.push(_133);
if(_12d.buildColChildren==null){
_12d.buildColChildren=new Array();
}
_12d.buildColChildren.push(_133);
_12d=_133;
_130=true;
}
for(var i=0;i<_12c.columnSizes.length;i++){
var size=_12c.columnSizes[i];
if(_132&&i==(_12c.columnSizes.length-1)){
size=size-0.1;
}
var _136=new _12f.om.Column(i,_12c.id,size,this.columns.length,_12c.layoutActionsDisabled);
this.columns.push(_136);
if(_12d!=null){
if(_12d.buildColChildren==null){
_12d.buildColChildren=new Array();
}
_12d.buildColChildren.push(_136);
}
_131.push(_136);
}
}
return {columnsInLayout:_131,addedLayoutHeaderColumn:_130};
},_portletsInitWinState:function(_137){
var _138={};
this.getPortletCurColRow(null,false,_138);
for(var _139 in this.portlets){
var _13a=this.portlets[_139];
var _13b=_138[_13a.getId()];
if(_13b==null&&_137){
for(var i=0;i<_137.length;i++){
if(_137[i].portlet.getId()==_13a.getId()){
_13b={layout:_137[i].layout};
break;
}
}
}
if(_13b!=null){
_13a._initWinState(_13b,false);
}else{
dojo.raise("Window state data not found for portlet: "+_13a.getId());
}
}
},_loadPortletZIndexCompare:function(_13d,_13e){
var _13f=null;
var _140=null;
var _141=null;
_13f=_13d.portlet._getInitialZIndex();
_140=_13e.portlet._getInitialZIndex();
if(_13f&&!_140){
return -1;
}else{
if(_140&&!_13f){
return 1;
}else{
if(_13f==_140){
return 0;
}
}
}
return (_13f-_140);
},_createColsStart:function(_142,_143){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _144=document.createElement("div");
_144.id=_143;
_144.setAttribute("id",_143);
for(var _145=0;_145<this.columnsStructure.length;_145++){
var _146=this.columnsStructure[_145];
this._createCols(_146,_144);
}
_142.appendChild(_144);
},_createCols:function(_147,_148){
_147.createColumn();
if(this.colFirstNormI==-1&&!_147.columnContainer&&!_147.layoutHeader){
this.colFirstNormI=_147.getPageColumnIndex();
}
var _149=_147.buildColChildren;
if(_149!=null&&_149.length>0){
for(var _14a=0;_14a<_149.length;_14a++){
this._createCols(_149[_14a],_147.domNode);
}
}
delete _147.buildColChildren;
_148.appendChild(_147.domNode);
},_removeCols:function(_14b){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_14b){
var _14d=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_14d,function(_14e){
_14b.appendChild(_14e);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _14f=dojo.byId(jetspeed.id.COLUMNS);
if(_14f){
dojo.dom.removeNode(_14f);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},getPortletCurColRow:function(_150,_151,_152){
if(!this.columns||this.columns.length==0){
return null;
}
var _153=null;
var _154=((_150!=null)?true:false);
var _155=0;
var _156=null;
var _157=null;
var _158=0;
var _159=false;
for(var _15a=0;_15a<this.columns.length;_15a++){
var _15b=this.columns[_15a];
var _15c=_15b.domNode.childNodes;
if(_157==null||_157!=_15b.getLayoutId()){
_157=_15b.getLayoutId();
_156=this.layouts[_157];
if(_156==null){
dojo.raise("Layout not found: "+_157);
return null;
}
_158=0;
_159=false;
if(_156.clonedFromRootId==null){
_159=true;
}else{
var _15d=this.getColFromColNode(_15b.domNode.parentNode);
if(_15d==null){
dojo.raise("Parent column not found: "+_15b);
return null;
}
_15b=_15d;
}
}
var _15e=null;
var _15f=jetspeed;
var _160=dojo;
var _161=_15f.id.PWIN_CLASS;
if(_151){
_161+="|"+_15f.id.PWIN_GHOST_CLASS;
}
if(_154){
_161+="|"+_15f.id.COL_CLASS;
}
var _162=new RegExp("(^|\\s+)("+_161+")(\\s+|$)");
for(var _163=0;_163<_15c.length;_163++){
var _164=_15c[_163];
if(_162.test(_160.html.getClass(_164))){
_15e=(_15e==null?0:_15e+1);
if((_15e+1)>_158){
_158=(_15e+1);
}
if(_150==null||_164==_150){
var _165={layout:_157,column:_15b.getLayoutColumnIndex(),row:_15e,columnObj:_15b};
if(!_159){
_165.layout=_156.clonedFromRootId;
}
if(_150!=null){
_153=_165;
break;
}else{
if(_152!=null){
var _166=this.getPWinFromNode(_164);
if(_166==null){
_160.raise("PortletWindow not found for node");
}else{
var _167=_166.portlet;
if(_167==null){
_160.raise("PortletWindow for node has null portlet: "+_166.widgetId);
}else{
_152[_167.getId()]=_165;
}
}
}
}
}
}
}
if(_153!=null){
break;
}
}
return _153;
},_getPortletArrayByZIndex:function(){
var _168=jetspeed;
var _169=this.getPortletArray();
if(!_169){
return _169;
}
var _16a=[];
for(var i=0;i<_169.length;i++){
if(!_169[i].getProperty(_168.id.PP_WINDOW_POSITION_STATIC)){
_16a.push(_169[i]);
}
}
_16a.sort(this._portletZIndexCompare);
return _16a;
},_portletZIndexCompare:function(_16c,_16d){
var _16e=null;
var _16f=null;
var _170=null;
_170=_16c.getSavedWinState();
_16e=_170.zIndex;
_170=_16d.getSavedWinState();
_16f=_170.zIndex;
if(_16e&&!_16f){
return -1;
}else{
if(_16f&&!_16e){
return 1;
}else{
if(_16e==_16f){
return 0;
}
}
}
return (_16e-_16f);
},getPortletDecorationDefault:function(){
var _171=jetspeed;
var pd=null;
if(djConfig.isDebug&&_171.debug.windowDecorationRandom){
pd=_171.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_171.prefs.portletDecorationsAllowed.length)];
}else{
var _173=this.getPortletDecorator();
if(dojo.lang.indexOf(_171.prefs.portletDecorationsAllowed,_173)!=-1){
pd=_173;
}else{
pd=_171.prefs.windowDecoration;
}
}
return pd;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _174=[];
for(var _175 in this.portlets){
var _176=this.portlets[_175];
_174.push(_176);
}
return _174;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _177=[];
for(var _178 in this.portlets){
var _179=this.portlets[_178];
_177.push(_179.getId());
}
return _177;
},getPortletByName:function(_17a){
if(this.portlets&&_17a){
for(var _17b in this.portlets){
var _17c=this.portlets[_17b];
if(_17c.name==_17a){
return _17c;
}
}
}
return null;
},getPortlet:function(_17d){
if(this.portlets&&_17d){
return this.portlets[_17d];
}
return null;
},getPWinFromNode:function(_17e){
var _17f=null;
if(this.portlets&&_17e){
for(var _180 in this.portlets){
var _181=this.portlets[_180];
var _182=_181.getPWin();
if(_182!=null){
if(_182.domNode==_17e){
_17f=_182;
break;
}
}
}
}
return _17f;
},putPortlet:function(_183){
if(!_183){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_183.entityId]=_183;
this.portlet_count++;
},putPWin:function(_184){
if(!_184){
return;
}
var _185=_184.widgetId;
if(!_185){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_185]=_184;
this.portlet_window_count++;
},getPWin:function(_186){
if(this.portlet_windows&&_186){
var pWin=this.portlet_windows[_186];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_186];
if(pWin==null){
var p=this.getPortlet(_186);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_18a){
var _18b=this.portlet_windows;
var pWin;
var _18d=[];
for(var _18e in _18b){
pWin=_18b[_18e];
if(pWin&&(!_18a||pWin.portlet)){
_18d.push(pWin);
}
}
return _18d;
},getPWinTopZIndex:function(_18f){
var _190=0;
if(_18f){
_190=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_190;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_190=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_190;
}
return _190;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_191,_192){
var pWin;
if(!_192){
var _194=this.portlet_windows;
for(var _195 in _194){
pWin=_194[_195];
if(pWin){
pWin.domNode.style.display=(_191?"none":"");
}
}
}else{
for(var i=0;i<_192.length;i++){
pWin=_192[i];
if(pWin){
pWin.domNode.style.display=(_191?"none":"");
}
}
}
},onBrowserWindowResize:function(){
var _197=jetspeed;
if(_197.UAie6){
var _198=this.portlet_windows;
var pWin;
for(var _19a in _198){
pWin=_198[_19a];
pWin.onBrowserWindowResize();
}
if(this.editMode){
var _19b=dojo.widget.byId(_197.id.PG_ED_WID);
if(_19b!=null){
_19b.onBrowserWindowResize();
}
}
}
},regPWinIFrameCover:function(_19c){
if(!_19c){
return;
}
this.iframeCoverByWinId[_19c.widgetId]=true;
},unregPWinIFrameCover:function(_19d){
if(!_19d){
return;
}
delete this.iframeCoverByWinId[_19d.widgetId];
},displayAllPWinIFrameCovers:function(_19e,_19f){
var _1a0=this.portlet_windows;
var _1a1=this.iframeCoverByWinId;
if(!_1a0||!_1a1){
return;
}
for(var _1a2 in _1a1){
if(_1a2==_19f){
continue;
}
var pWin=_1a0[_1a2];
var _1a4=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_1a4){
_1a4.style.display=(_19e?"none":"block");
}
}
},createLayoutInfo:function(_1a5){
var _1a6=dojo;
var _1a7=null;
var _1a8=null;
var _1a9=null;
var _1aa=null;
var _1ab=document.getElementById(_1a5.id.DESKTOP);
if(_1ab!=null){
_1a7=_1a5.ui.getLayoutExtents(_1ab,null,_1a6,_1a5);
}
var _1ac=document.getElementById(_1a5.id.COLUMNS);
if(_1ac!=null){
_1a8=_1a5.ui.getLayoutExtents(_1ac,null,_1a6,_1a5);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_1aa=_1a5.ui.getLayoutExtents(col.domNode,null,_1a6,_1a5);
}else{
if(!col.columnContainer){
_1a9=_1a5.ui.getLayoutExtents(col.domNode,null,_1a6,_1a5);
}
}
if(_1a9!=null&&_1aa!=null){
break;
}
}
}
this.layoutInfo={desktop:(_1a7!=null?_1a7:{}),columns:(_1a8!=null?_1a8:{}),column:(_1a9!=null?_1a9:{}),columnLayoutHeader:(_1aa!=null?_1aa:{})};
_1a5.widget.PortletWindow.prototype.colWidth_pbE=((_1a9&&_1a9.pbE)?_1a9.pbE.w:0);
},destroy:function(){
var _1af=jetspeed;
var _1b0=dojo;
if(_1af.UAie6){
_1af.ui.evtDisconnect("after",window,"onresize",_1af.ui.windowResizeMgr,"onResize",_1b0.event);
}
var _1b1=this.portlet_windows;
var _1b2=this.getPWins(true);
var pWin,_1b4;
for(var i=0;i<_1b2.length;i++){
pWin=_1b2[i];
_1b4=pWin.widgetId;
pWin.closeWindow();
delete _1b1[_1b4];
this.portlet_window_count--;
}
this.portlets=[];
this.portlet_count=0;
var _1b6=_1b0.widget.byId(_1af.id.PG_ED_WID);
if(_1b6!=null){
_1b6.editPageDestroy();
}
this._removeCols(document.getElementById(_1af.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_1b7){
if(_1b7==null){
return null;
}
var _1b8=_1b7.getAttribute("columnindex");
if(_1b8==null){
return null;
}
var _1b9=new Number(_1b8);
if(_1b9>=0&&_1b9<this.columns.length){
return this.columns[_1b9];
}
return null;
},getColIndexForNode:function(node){
var _1bb=null;
if(!this.columns){
return _1bb;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1bb=i;
break;
}
}
return _1bb;
},getColWithNode:function(node){
var _1be=this.getColIndexForNode(node);
return ((_1be!=null&&_1be>=0)?this.columns[_1be]:null);
},getDescendantCols:function(_1bf){
var dMap={};
if(_1bf==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1bf&&_1bf.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_1c3){
if(!_1c3){
return;
}
var _1c4=(_1c3.getName?_1c3.getName():null);
if(_1c4!=null){
this.menus[_1c4]=_1c3;
}
},getMenu:function(_1c5){
if(_1c5==null){
return null;
}
return this.menus[_1c5];
},removeMenu:function(_1c6){
if(_1c6==null){
return;
}
var _1c7=null;
if(dojo.lang.isString(_1c6)){
_1c7=_1c6;
}else{
_1c7=(_1c6.getName?_1c6.getName():null);
}
if(_1c7!=null){
delete this.menus[_1c7];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1c8=[];
for(var _1c9 in this.menus){
_1c8.push(_1c9);
}
return _1c8;
},retrieveMenuDeclarations:function(_1ca,_1cb,_1cc){
contentListener=new jetspeed.om.MenusApiCL(_1ca,_1cb,_1cc);
this.clearMenus();
var _1cd="?action=getmenus";
if(_1ca){
_1cd+="&includeMenuDefs=true";
}
var _1ce=this.getPsmlUrl()+_1cd;
var _1cf="text/xml";
var _1d0=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1ce,mimetype:_1cf},contentListener,_1d0,jetspeed.debugContentDumpIds);
},syncPageControls:function(_1d1){
var jsId=_1d1.id;
if(this.actionButtons==null){
return;
}
for(var _1d3 in this.actionButtons){
var _1d4=false;
if(_1d3==jsId.ACT_EDIT){
if(!this.editMode){
_1d4=true;
}
}else{
if(_1d3==jsId.ACT_VIEW){
if(this.editMode){
_1d4=true;
}
}else{
if(_1d3==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_1d4=true;
}
}else{
_1d4=true;
}
}
}
if(_1d4){
this.actionButtons[_1d3].style.display="";
}else{
this.actionButtons[_1d3].style.display="none";
}
}
},renderPageControls:function(_1d5){
var _1d5=jetspeed;
var jsId=_1d5.id;
var _1d7=dojo;
var _1d8=[];
if(this.actions!=null){
for(var _1d9 in this.actions){
if(_1d9!=jsId.ACT_HELP){
_1d8.push(_1d9);
}
if(_1d9==jsId.ACT_EDIT){
_1d8.push(jsId.ACT_ADDPORTLET);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
if(this.actions[jsId.ACT_VIEW]==null){
_1d8.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
if(this.actions[jsId.ACT_EDIT]==null){
_1d8.push(jsId.ACT_EDIT);
}
}
}
var _1da=_1d7.byId(jsId.PAGE_CONTROLS);
if(_1da!=null&&_1d8!=null&&_1d8.length>0){
var _1db=_1d5.prefs;
var jsUI=_1d5.ui;
var _1dd=_1d7.event;
var _1de=_1d5.page.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _1df=this.actionButtonTooltips;
for(var i=0;i<_1d8.length;i++){
var _1d9=_1d8[i];
var _1e1=document.createElement("div");
_1e1.className="portalPageActionButton";
_1e1.style.backgroundImage="url("+_1db.getLayoutRootUrl()+"/images/desktop/"+_1d9+".gif)";
_1e1.actionName=_1d9;
this.actionButtons[_1d9]=_1e1;
_1da.appendChild(_1e1);
jsUI.evtConnect("after",_1e1,"onclick",this,"pageActionButtonClick",_1dd);
if(_1db.pageActionButtonTooltip){
var _1e2=null;
if(_1db.desktopActionLabels!=null){
_1e2=_1db.desktopActionLabels[_1d9];
}
if(_1e2==null||_1e2.length==0){
_1e2=_1d7.string.capitalize(_1d9);
}
_1df.push(_1de.addNode(_1e1,_1e2,true,null,null,null,_1d5,jsUI,_1dd));
}
}
}
},_destroyPageControls:function(){
var _1e3=jetspeed;
if(this.actionButtons){
for(var _1e4 in this.actionButtons){
var _1e5=this.actionButtons[_1e4];
if(_1e5){
_1e3.ui.evtDisconnect("after",_1e5,"onclick",this,"pageActionButtonClick");
}
}
}
var _1e6=dojo.byId(_1e3.id.PAGE_CONTROLS);
if(_1e6!=null&&_1e6.childNodes&&_1e6.childNodes.length>0){
for(var i=(_1e6.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1e6.childNodes[i]);
}
}
_1e3.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_1e9){
var _1ea=jetspeed;
if(_1e9==null){
return;
}
if(_1e9==_1ea.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1e9==_1ea.id.ACT_EDIT){
_1ea.editPageInitiate(_1ea);
}else{
if(_1e9==_1ea.id.ACT_VIEW){
_1ea.editPageTerminate(_1ea);
}else{
var _1eb=this.getPageAction(_1e9);
alert("pageAction "+_1e9+" : "+_1eb);
if(_1eb==null){
return;
}
if(_1eb.url==null){
return;
}
var _1ec=_1ea.url.basePortalUrl()+_1ea.url.path.DESKTOP+"/"+_1eb.url;
_1ea.pageNavigate(_1ec);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1ee,_1ef){
var _1f0=jetspeed;
if(!_1ef){
_1ef=escape(this.getPagePathAndQuery());
}else{
_1ef=escape(_1ef);
}
var _1f1=_1f0.url.basePortalUrl()+_1f0.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1ef;
if(_1ee!=null){
_1f1+="&jslayoutid="+escape(_1ee);
}
_1f0.changeActionForPortlet(this.rootFragmentId,null,_1f0.id.ACT_EDIT,new jetspeed.om.PageChangeActionCL(_1f1));
},setPageModePortletActions:function(_1f2){
if(_1f2==null||_1f2.actions==null){
return;
}
var jsId=jetspeed.id;
if(_1f2.actions[jsId.ACT_REMOVEPORTLET]==null){
_1f2.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_1f4){
if(this.pageUrl!=null&&!_1f4){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _1f6=jsU.path.SERVER+((_1f4)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _1f7=jsU.parse(_1f6);
var _1f8=null;
if(this.pageUrlFallback!=null){
_1f8=jsU.parse(this.pageUrlFallback);
}else{
_1f8=jsU.parse(window.location.href);
}
if(_1f7!=null&&_1f8!=null){
var _1f9=_1f8.query;
if(_1f9!=null&&_1f9.length>0){
var _1fa=_1f7.query;
if(_1fa!=null&&_1fa.length>0){
_1f6=_1f6+"&"+_1f9;
}else{
_1f6=_1f6+"?"+_1f9;
}
}
}
if(!_1f4){
this.pageUrl=_1f6;
}
return _1f6;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _1fc=this.getPath();
var _1fd=jsU.parse(_1fc);
var _1fe=null;
if(this.pageUrlFallback!=null){
_1fe=jsU.parse(this.pageUrlFallback);
}else{
_1fe=jsU.parse(window.location.href);
}
if(_1fd!=null&&_1fe!=null){
var _1ff=_1fe.query;
if(_1ff!=null&&_1ff.length>0){
var _200=_1fd.query;
if(_200!=null&&_200.length>0){
_1fc=_1fc+"&"+_1ff;
}else{
_1fc=_1fc+"?"+_1ff;
}
}
}
this.pagePathAndQuery=_1fc;
return _1fc;
},getPageDirectory:function(_201){
var _202="/";
var _203=(_201?this.getRealPath():this.getPath());
if(_203!=null){
var _204=_203.lastIndexOf("/");
if(_204!=-1){
if((_204+1)<_203.length){
_202=_203.substring(0,_204+1);
}else{
_202=_203;
}
}
}
return _202;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_206){
if(!_206){
_206="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_206)){
return jsU.path.SERVER+jsU.path.DESKTOP+_206;
}
return _206;
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
jetspeed.om.Column=function(_208,_209,size,_20b,_20c){
this.layoutColumnIndex=_208;
this.layoutId=_209;
this.size=size;
this.pageColumnIndex=new Number(_20b);
if(typeof _20c!="undefined"){
this.layoutActionsDisabled=_20c;
}
this.id="jscol_"+_20b;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_20d){
var _20e=this.styleClass;
var _20f=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_20f>0){
_20e+=" desktopColumnClear-PRIVATE";
}
var _210=document.createElement("div");
_210.setAttribute("columnindex",_20f);
_210.style.width=this.size+"%";
if(this.layoutHeader){
_20e=this.styleLayoutClass;
_210.setAttribute("layoutid",this.layoutId);
}
_210.className=_20e;
_210.id=this.getId();
this.domNode=_210;
if(_20d!=null){
_20d.appendChild(_210);
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
jetspeed.om.Portlet=function(_213,_214,_215,_216,_217,_218,_219,_21a,_21b){
this.name=_213;
this.entityId=_214;
if(_216){
this.properties=_216;
}else{
this.properties={};
}
if(_217){
this.actions=_217;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_218;
this.currentActionMode=_219;
if(_215){
this.contentRetriever=_215;
}
if(_21a!=null&&_21a.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_21a)!=-1){
this.properties[jetspeed.id.PP_WINDOW_DECORATION]=_21a;
}
}
this.layoutActionsDisabled=false;
if(typeof _21b!="undefined"){
this.layoutActionsDisabled=_21b;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _21c=jetspeed;
var jsId=_21c.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _21e=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_21c.prefs.windowTiling){
if(_21e=="true"){
_21e=true;
}else{
if(_21e=="false"){
_21e=false;
}else{
if(_21e!=true&&_21e!=false){
_21e=true;
}
}
}
}else{
_21e=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_21e;
var _21f=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_21f=="true"){
_21f=true;
}else{
if(_21e=="false"){
_21f=false;
}else{
if(_21f!=true&&_21f!=false){
_21f=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_21f;
var _220=this.properties[jsId.PP_WINDOW_TITLE];
if(!_220&&this.name){
var re=(/^[^:]*:*/);
_220=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_220;
}
},postParseAnnotateHtml:function(_222){
var _223=jetspeed;
var _224=_223.portleturl;
if(_222){
var _225=_222;
var _226=_225.getElementsByTagName("form");
var _227=_223.debug.postParseAnnotateHtml;
var _228=_223.debug.postParseAnnotateHtmlDisableAnchors;
if(_226){
for(var i=0;i<_226.length;i++){
var _22a=_226[i];
var _22b=_22a.action;
var _22c=_224.parseContentUrl(_22b);
var _22d=_22c.operation;
if(_22d==_224.PORTLET_REQUEST_ACTION||_22d==_224.PORTLET_REQUEST_RENDER){
var _22e=_224.genPseudoUrl(_22c,true);
_22a.action=_22e;
var _22f=new _223.om.ActionRenderFormBind(_22a,_22c.url,_22c.portletEntityId,_22d);
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_22d+") for form with action: "+_22b);
}
}else{
if(_22b==null||_22b.length==0){
var _22f=new _223.om.ActionRenderFormBind(_22a,null,this.entityId,null);
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_22b);
}
}
}
}
}
var _230=_225.getElementsByTagName("a");
if(_230){
for(var i=0;i<_230.length;i++){
var _231=_230[i];
var _232=_231.href;
var _22c=_224.parseContentUrl(_232);
var _233=null;
if(!_228){
_233=_224.genPseudoUrl(_22c);
}
if(!_233){
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_232);
}
}else{
if(_233==_232){
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_232);
}
}else{
if(_227){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_232+" with: "+_233);
}
_231.href=_233;
}
}
}
}
}
},getPWin:function(){
var _234=jetspeed;
var _235=this.properties[_234.id.PP_WIDGET_ID];
if(_235){
return _234.page.getPWin(_235);
}
return null;
},getCurWinState:function(_236){
var _237=null;
try{
var _238=this.getPWin();
if(!_238){
return null;
}
_237=_238.getCurWinStateForPersist(_236);
if(!_236){
if(_237.layout==null){
_237.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _237;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_239,_23a){
var _23b=jetspeed;
var jsId=_23b.id;
if(!_239){
_239={};
}
var _23d=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _23e=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_239[jsId.PP_WINDOW_POSITION_STATIC]=_23d;
_239[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_23e;
var _23f=this.properties["width"];
if(!_23a&&_23f!=null&&_23f>0){
_239.width=Math.floor(_23f);
}else{
if(_23a){
_239.width=-1;
}
}
var _240=this.properties["height"];
if(!_23a&&_240!=null&&_240>0){
_239.height=Math.floor(_240);
}else{
if(_23a){
_239.height=-1;
}
}
if(!_23d||!_23b.prefs.windowTiling){
var _241=this.properties["x"];
if(!_23a&&_241!=null&&_241>=0){
_239.left=Math.floor(((_241>0)?_241:0));
}else{
if(_23a){
_239.left=-1;
}
}
var _242=this.properties["y"];
if(!_23a&&_242!=null&&_242>=0){
_239.top=Math.floor(((_242>0)?_242:0));
}else{
_239.top=-1;
}
var _243=this._getInitialZIndex(_23a);
if(_243!=null){
_239.zIndex=_243;
}
}
return _239;
},_initWinState:function(_244,_245){
var _246=jetspeed;
var _247=(_244?_244:{});
this.getInitialWinDims(_247,_245);
if(_246.debug.initWinState){
var _248=this.properties[_246.id.PP_WINDOW_POSITION_STATIC];
if(!_248||!_246.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_247.zIndex+" x="+_247.left+" y="+_247.top+" width="+_247.width+" height="+_247.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_247.column+" row="+_247.row+" width="+_247.width+" height="+_247.height);
}
}
this.lastSavedWindowState=_247;
return _247;
},_getInitialZIndex:function(_249){
var _24a=null;
var _24b=this.properties["z"];
if(!_249&&_24b!=null&&_24b>=0){
_24a=Math.floor(_24b);
}else{
if(_249){
_24a=-1;
}
}
return _24a;
},_getChangedWindowState:function(_24c){
var jsId=jetspeed.id;
var _24e=this.getSavedWinState();
if(_24e&&dojo.lang.isEmpty(_24e)){
_24e=null;
_24c=false;
}
var _24f=this.getCurWinState(_24c);
var _250=_24f[jsId.PP_WINDOW_POSITION_STATIC];
var _251=!_250;
if(!_24e){
var _252={state:_24f,positionChanged:true,extendedPropChanged:true};
if(_251){
_252.zIndexChanged=true;
}
return _252;
}
var _253=false;
var _254=false;
var _255=false;
var _256=false;
for(var _257 in _24f){
if(_24f[_257]!=_24e[_257]){
if(_257==jsId.PP_WINDOW_POSITION_STATIC||_257==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_253=true;
_255=true;
_254=true;
}else{
if(_257=="zIndex"){
if(_251){
_253=true;
_256=true;
}
}else{
_253=true;
_254=true;
}
}
}
}
if(_253){
var _252={state:_24f,positionChanged:_254,extendedPropChanged:_255};
if(_251){
_252.zIndexChanged=_256;
}
return _252;
}
return null;
},getPortletUrl:function(_258){
var _259=jetspeed;
var _25a=_259.url;
var _25b=null;
if(_258&&_258.url){
_25b=_258.url;
}else{
if(_258&&_258.formNode){
var _25c=_258.formNode.getAttribute("action");
if(_25c){
_25b=_25c;
}
}
}
if(_25b==null){
_25b=_25a.basePortalUrl()+_25a.path.PORTLET+_259.page.getPath();
}
if(!_258.dontAddQueryArgs){
_25b=_25a.parse(_25b);
_25b=_25a.addQueryParameter(_25b,"entity",this.entityId,true);
_25b=_25a.addQueryParameter(_25b,"portlet",this.name,true);
_25b=_25a.addQueryParameter(_25b,"encoder","desktop",true);
if(_258.jsPageUrl!=null){
var _25d=_258.jsPageUrl.query;
if(_25d!=null&&_25d.length>0){
_25b=_25b.toString()+"&"+_25d;
}
}
}
if(_258){
_258.url=_25b.toString();
}
return _25b;
},_submitAjaxApi:function(_25e,_25f,_260){
var _261=jetspeed;
var _262="?action="+_25e+"&id="+this.entityId+_25f;
var _263=_261.url.basePortalUrl()+_261.url.path.AJAX_API+_261.page.getPath()+_262;
var _264="text/xml";
var _265=new _261.om.Id(_25e,this.entityId);
_265.portlet=this;
_261.url.retrieveContent({url:_263,mimetype:_264},_260,_265,null);
},submitWinState:function(_266,_267){
var _268=jetspeed;
var jsId=_268.id;
var _26a=null;
if(_267){
_26a={state:this._initWinState(null,true)};
}else{
_26a=this._getChangedWindowState(_266);
}
if(_26a){
var _26b=_26a.state;
var _26c=_26b[jsId.PP_WINDOW_POSITION_STATIC];
var _26d=_26b[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _26e=null;
if(_26a.extendedPropChanged){
var _26f=jsId.PP_PROP_SEPARATOR;
var _270=jsId.PP_PAIR_SEPARATOR;
_26e=jsId.PP_STATICPOS+_26f+_26c.toString();
_26e+=_270+jsId.PP_FITHEIGHT+_26f+_26d.toString();
_26e=escape(_26e);
}
var _271="";
var _272=null;
if(_26c){
_272="moveabs";
if(_26b.column!=null){
_271+="&col="+_26b.column;
}
if(_26b.row!=null){
_271+="&row="+_26b.row;
}
if(_26b.layout!=null){
_271+="&layoutid="+_26b.layout;
}
if(_26b.height!=null){
_271+="&height="+_26b.height;
}
}else{
_272="move";
if(_26b.zIndex!=null){
_271+="&z="+_26b.zIndex;
}
if(_26b.width!=null){
_271+="&width="+_26b.width;
}
if(_26b.height!=null){
_271+="&height="+_26b.height;
}
if(_26b.left!=null){
_271+="&x="+_26b.left;
}
if(_26b.top!=null){
_271+="&y="+_26b.top;
}
}
if(_26e!=null){
_271+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_26e;
}
this._submitAjaxApi(_272,_271,new _268.om.MoveApiCL(this,_26b));
if(!_266&&!_267){
if(!_26c&&_26a.zIndexChanged){
var _273=_268.page.getPortletArray();
if(_273&&(_273.length-1)>0){
for(var i=0;i<_273.length;i++){
var _275=_273[i];
if(_275&&_275.entityId!=this.entityId){
if(!_275.properties[_268.id.PP_WINDOW_POSITION_STATIC]){
_275.submitWinState(true);
}
}
}
}
}else{
if(_26c){
}
}
}
}
},retrieveContent:function(_276,_277,_278){
if(_276==null){
_276=new jetspeed.om.PortletCL(this,_278,_277);
}
if(!_277){
_277={};
}
var _279=this;
_279.getPortletUrl(_277);
this.contentRetriever.getContent(_277,_276,_279,jetspeed.debugContentDumpIds);
},setPortletContent:function(_27a,_27b,_27c){
var _27d=this.getPWin();
if(_27c!=null&&_27c.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_27c;
if(_27d&&!this.loadingIndicatorIsShown()){
_27d.setPortletTitle(_27c);
}
}
if(_27d){
_27d.setPortletContent(_27a,_27b);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _27f=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _280=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _281=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _282=this.getPWin();
if(_282&&(_27f||_280)){
var _283=_282.getPortletTitle();
if(_283&&(_283==_27f||_283==_280)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_284){
var _285=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_285=jetspeed.prefs.desktopActionLabels[_284];
if(_285!=null&&_285.length==0){
_285=null;
}
}
return _285;
},loadingIndicatorShow:function(_286){
if(_286&&!this.loadingIndicatorIsShown()){
var _287=this._getLoadingActionLabel(_286);
var _288=this.getPWin();
if(_288&&_287){
_288.setPortletTitle(_287);
}
}
},loadingIndicatorHide:function(){
var _289=this.getPWin();
if(_289){
_289.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_28b,_28c){
var _28d=jetspeed;
var _28e=_28d.url;
var _28f=null;
if(_28b!=null){
_28f=this.getAction(_28b);
}
var _290=_28c;
if(_290==null&&_28f!=null){
_290=_28f.url;
}
if(_290==null){
return;
}
var _291=_28e.basePortalUrl()+_28e.path.PORTLET+"/"+_290+_28d.page.getPath();
if(_28b!=_28d.id.ACT_PRINT){
this.retrieveContent(null,{url:_291});
}else{
var _292=_28d.page.getPageUrl();
_292=_28e.addQueryParameter(_292,"jsprintmode","true");
_292=_28e.addQueryParameter(_292,"jsaction",escape(_28f.url));
_292=_28e.addQueryParameter(_292,"jsentity",this.entityId);
_292=_28e.addQueryParameter(_292,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_292.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_294,_295,_296){
if(_294){
this.actions=_294;
}else{
this.actions={};
}
this.currentActionState=_295;
this.currentActionMode=_296;
this.syncActions();
},syncActions:function(){
var _297=jetspeed;
_297.page.setPageModePortletActions(this);
var _298=this.getPWin();
if(_298){
_298.actionBtnSync(_297,_297.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_29b,_29c){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_29b;
this.submitOperation=_29c;
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
},eventConfMgr:function(_29f){
var fn=(_29f)?"disconnect":"connect";
var _2a1=dojo.event;
var form=this.form;
_2a1[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_2a1[fn]("after",node,"onclick",this,"click",null);
}
}
var _2a5=form.getElementsByTagName("input");
for(var i=0;i<_2a5.length;i++){
var _2a6=_2a5[i];
if(_2a6.type.toLowerCase()=="image"&&_2a6.form==form){
_2a1[fn]("after",_2a6,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_2a1[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_2a8){
var _2a9=true;
if(this.isFormSubmitInProgress()){
_2a9=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_2a9=false;
}
}
}
return _2a9;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _2ab=jetspeed.portleturl.parseContentUrl(this.form.action);
var _2ac={};
if(_2ab.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2ab.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _2ad=jetspeed.portleturl.genPseudoUrl(_2ab,true);
this.form.action=_2ad;
this.submitOperation=_2ab.operation;
this.entityId=_2ab.portletEntityId;
_2ac.url=_2ab.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_2ac.formFilter=dojo.lang.hitch(this,"formFilter");
_2ac.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_2ac),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_2ac),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_2ae){
if(_2ae!=undefined){
this.formSubmitInProgress=_2ae;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_2af,_2b0,_2b1){
this.portlet=_2af;
this.suppressGetActions=_2b0;
this.formbind=null;
if(_2b1!=null&&_2b1.submitFormBindObject!=null){
this.formbind=_2b1.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_2b2){
if(this.portlet==null){
return;
}
if(_2b2){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2b3,_2b4,_2b5,http){
var _2b7=null;
if(http!=null){
_2b7=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2b7!=null){
_2b7=unescape(_2b7);
}
}
_2b5.setPortletContent(_2b3,_2b4,_2b7);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2b5.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2b9,_2ba,_2bb){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_2ba+" type: "+type+jetspeed.formatError(_2b9));
}};
jetspeed.om.PortletActionCL=function(_2bc,_2bd){
this.portlet=_2bc;
this.formbind=null;
if(_2bd!=null&&_2bd.submitFormBindObject!=null){
this.formbind=_2bd.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_2be){
if(this.portlet==null){
return;
}
if(_2be){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2bf,_2c0,_2c1,http){
var _2c3=jetspeed;
var _2c4=null;
var _2c5=false;
var _2c6=_2c3.portleturl.parseContentUrl(_2bf);
if(_2c6.operation==_2c3.portleturl.PORTLET_REQUEST_ACTION||_2c6.operation==_2c3.portleturl.PORTLET_REQUEST_RENDER){
if(_2c3.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_2c6.operation+"-url in response body: "+_2bf+"  url: "+_2c6.url+" entity-id: "+_2c6.portletEntityId);
}
_2c4=_2c6.url;
}else{
if(_2c3.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_2bf);
}
_2c4=_2bf;
if(_2c4){
var _2c7=_2c4.indexOf(_2c3.url.basePortalUrl()+_2c3.url.path.PORTLET);
if(_2c7==-1){
_2c5=true;
window.location.href=_2c4;
_2c4=null;
}else{
if(_2c7>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_2bf);
_2c4=null;
}
}
}
}
if(_2c4!=null){
if(_2c3.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_2c4);
}
var _2c8=new jetspeed.PortletRenderer(false,false,false,_2c4,true);
_2c8.renderAll();
}else{
this._loading(false);
}
if(!_2c5&&this.portlet){
_2c3.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2ca,_2cb,_2cc){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_2ca));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2cd=this.getUrl();
if(_2cd){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2cd,this.getTarget());
}else{
jetspeed.updatePage(_2cd);
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
jetspeed.om.Menu=function(_2ce,_2cf){
this._is_parsed=false;
this.name=_2ce;
this.type=_2cf;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2d0){
if(!_2d0){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2d0);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2d2){
if(!this.hasOptions()){
return null;
}
if(_2d2==0||_2d2>0){
if(_2d2>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_2d2];
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
var _2d4=this.options[i];
if(_2d4 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_2d6,_2d7){
var _2d8=this.parseMenu(data,_2d7.menuName,_2d7.menuType);
_2d7.page.putMenu(_2d8);
},notifyFailure:function(type,_2da,_2db,_2dc){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_2dc.toString()+"] url: "+_2db+" type: "+type+jetspeed.formatError(_2da));
},parseMenu:function(node,_2de,_2df){
var menu=null;
var _2e1=node.getElementsByTagName("js");
if(!_2e1||_2e1.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _2e2=_2e1[0].childNodes;
for(var i=0;i<_2e2.length;i++){
var _2e4=_2e2[i];
if(_2e4.nodeType!=1){
continue;
}
var _2e5=_2e4.nodeName;
if(_2e5=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_2e4,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2de;
}
if(menu.type==null){
menu.type=_2df;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2e8=null;
var _2e9=node.childNodes;
for(var i=0;i<_2e9.length;i++){
var _2eb=_2e9[i];
if(_2eb.nodeType!=1){
continue;
}
var _2ec=_2eb.nodeName;
if(_2ec=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_2eb,new jetspeed.om.Menu()));
}
}else{
if(_2ec=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_2eb,new jetspeed.om.MenuOption()));
}
}else{
if(_2ec=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2eb,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2ec){
mObj[_2ec]=((_2eb&&_2eb.firstChild)?_2eb.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_2ed,_2ee,_2ef){
this.includeMenuDefs=_2ed;
this.initiateEditMode=_2ee;
this.isPageUpdate=_2ef;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_2f1,_2f2){
var _2f3=this.getMenuDefs(data,_2f1,_2f2);
for(var i=0;i<_2f3.length;i++){
var mObj=_2f3[i];
_2f2.page.putMenu(mObj);
}
this.notifyFinished(_2f2);
},getMenuDefs:function(data,_2f7,_2f8){
var _2f9=[];
var _2fa=data.getElementsByTagName("menu");
for(var i=0;i<_2fa.length;i++){
var _2fc=_2fa[i].getAttribute("type");
if(this.includeMenuDefs){
_2f9.push(this.parseMenuObject(_2fa[i],new jetspeed.om.Menu(null,_2fc)));
}else{
var _2fd=_2fa[i].firstChild.nodeValue;
_2f9.push(new jetspeed.om.Menu(_2fd,_2fc));
}
}
return _2f9;
},notifyFailure:function(type,_2ff,_300,_301){
dojo.raise("MenusApiCL error ["+_301.toString()+"] url: "+_300+" type: "+type+jetspeed.formatError(_2ff));
},notifyFinished:function(_302){
var _303=jetspeed;
if(this.includeMenuDefs){
_303.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
_303.editPageInitiate(_303);
}
if(this.isPageUpdate){
_303.updatePageEnd();
}
if(djConfig.isDebug&&_303.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_304){
this.portletEntityId=_304;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_306,_307){
if(jetspeed.url.checkAjaxApiResponse(_306,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_308){
var _309=jetspeed.page.getPortlet(this.portletEntityId);
if(_309){
if(_308){
_309.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_309.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_30b,_30c,_30d){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_30d.toString()+"] url: "+_30c+" type: "+type+jetspeed.formatError(_30b));
}});
jetspeed.om.PageChangeActionCL=function(_30e){
this.pageActionUrl=_30e;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_310,_311){
if(jetspeed.url.checkAjaxApiResponse(_310,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_313,_314,_315){
dojo.raise("PageChangeActionCL error ["+_315.toString()+"] url: "+_314+" type: "+type+jetspeed.formatError(_313));
}});
jetspeed.om.PortletActionsCL=function(_316){
this.portletEntityIds=_316;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_317){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _319=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_319){
if(_317){
_319.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_319.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_31b,_31c){
this._loading(false);
if(jetspeed.url.checkAjaxApiResponse(_31b,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _31e=this.parsePortletActionsResponse(node);
for(var i=0;i<_31e.length;i++){
var _320=_31e[i];
var _321=_320.id;
var _322=jetspeed.page.getPortlet(_321);
if(_322!=null){
_322.updateActions(_320.actions,_320.currentActionState,_320.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _324=new Array();
var _325=node.getElementsByTagName("js");
if(!_325||_325.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _324;
}
var _326=_325[0].childNodes;
for(var i=0;i<_326.length;i++){
var _328=_326[i];
if(_328.nodeType!=1){
continue;
}
var _329=_328.nodeName;
if(_329=="portlets"){
var _32a=_328;
var _32b=_32a.childNodes;
for(var pI=0;pI<_32b.length;pI++){
var _32d=_32b[pI];
if(_32d.nodeType!=1){
continue;
}
var _32e=_32d.nodeName;
if(_32e=="portlet"){
var _32f=this.parsePortletElement(_32d);
if(_32f!=null){
_324.push(_32f);
}
}
}
}
}
return _324;
},parsePortletElement:function(node){
var _331=node.getAttribute("id");
if(_331!=null){
var _332=jetspeed.page._parsePSMLActions(node,null);
var _333=jetspeed.page._parsePSMLChildOrAttr(node,"state");
var _334=jetspeed.page._parsePSMLChildOrAttr(node,"mode");
return {id:_331,actions:_332,currentActionState:_333,currentActionMode:_334};
}
return null;
},notifyFailure:function(type,_336,_337,_338){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_338.toString()+"] url: "+_337+" type: "+type+jetspeed.formatError(_336));
}});
jetspeed.om.MoveApiCL=function(_339,_33a){
this.portlet=_339;
this.changedState=_33a;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_33b){
if(this.portlet==null){
return;
}
if(_33b){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_33d,_33e){
this._loading(false);
dojo.lang.mixin(_33e.portlet.lastSavedWindowState,this.changedState);
var _33f=false;
if(djConfig.isDebug&&jetspeed.debug.submitWinState){
_33f=true;
}
jetspeed.url.checkAjaxApiResponse(_33d,data,_33f,("move-portlet ["+_33e.portlet.entityId+"]"),jetspeed.debug.submitWinState);
},notifyFailure:function(type,_341,_342,_343){
this._loading(false);
dojo.debug("submitWinState error ["+_343.entityId+"] url: "+_342+" type: "+type+jetspeed.formatError(_341));
}};
jetspeed.ui={initCssObj:function(){
var _344=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _345=_344.concat([" height: ","","",";"]);
var _346=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _347=_345.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _348=_347.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_344,cssHeight:_345,cssWidthHeight:_346,cssOverflow:_347,cssPosition:_348,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_349,_34a,_34b,_34c,_34d,_34e){
var djH=dojo.html;
var jsId=jetspeed.id;
var _351=null;
var _352=-1;
var _353=-1;
var _354=-1;
if(_349){
var _355=_349.childNodes;
if(_355){
_354=_355.length;
}
_351=[];
if(_354>0){
var _356="",_357="";
if(!_34e){
_356=jsId.PWIN_CLASS;
}
if(_34b){
_356+=((_356.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_34c){
_356+=((_356.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_34d&&!_34c){
_356+=((_356.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_34c&&!_34d){
_357=((_357.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_356.length>0){
var _358=new RegExp("(^|\\s+)("+_356+")(\\s+|$)");
var _359=null;
if(_357.length>0){
_359=new RegExp("(^|\\s+)("+_357+")(\\s+|$)");
}
var _35a,_35b,_35c;
for(var i=0;i<_354;i++){
_35a=_355[i];
_35b=false;
_35c=djH.getClass(_35a);
if(_358.test(_35c)&&(_359==null||!_359.test(_35c))){
_351.push(_35a);
_35b=true;
}
if(_34a&&_35a==_34a){
if(!_35b){
_351.push(_35a);
}
_352=i;
_353=_351.length-1;
}
}
}
}
}
return {matchingNodes:_351,totalNodes:_354,matchNodeIndex:_352,matchNodeIndexInMatchingNodes:_353};
},getPWinsFromNodes:function(_35e){
var _35f=jetspeed.page;
var _360=null;
if(_35e){
_360=new Array();
for(var i=0;i<_35e.length;i++){
var _362=_35f.getPWin(_35e[i].id);
if(_362){
_360.push(_362);
}
}
}
return _360;
},createPortletWindow:function(_363,_364,_365){
var _366=false;
if(djConfig.isDebug&&_365.debug.profile){
_366=true;
dojo.profile.start("createPortletWindow");
}
var _367=(_364!=null);
var _368=false;
var _369=null;
if(_367&&_364<_365.page.columns.length&&_364>=0){
_369=_365.page.columns[_364].domNode;
}
if(_369==null){
_368=true;
_369=document.getElementById(_365.id.DESKTOP);
}
if(_369==null){
return;
}
var _36a={};
if(_363.isPortlet){
_36a.portlet=_363;
if(_365.prefs.printModeOnly!=null){
_36a.printMode=true;
}
if(_368){
_363.properties[_365.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_365.widget.PortletWindow.prototype.altInitParamsDef(_36a,_363);
if(_368){
pwP.altInitParams[_365.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _36c=new _365.widget.PortletWindow();
_36c.build(_36a,_369);
if(_366){
dojo.profile.end("createPortletWindow");
}
},getLayoutExtents:function(node,_36e,_36f,_370){
if(!_36e){
_36e=_36f.gcs(node);
}
var pad=_36f._getPadExtents(node,_36e);
var _372=_36f._getBorderExtents(node,_36e);
var _373={l:(pad.l+_372.l),t:(pad.t+_372.t),w:(pad.w+_372.w),h:(pad.h+_372.h)};
var _374=_36f._getMarginExtents(node,_36e,_370);
return {bE:_372,pE:pad,pbE:_373,mE:_374,lessW:(_373.w+_374.w),lessH:(_373.h+_374.h)};
},getContentBoxSize:function(node,_376){
var w=node.clientWidth,h,_379;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_379=_376.pbE;
}else{
h=node.clientHeight;
_379=_376.pE;
}
return {w:(w-_379.w),h:(h-_379.h)};
},getMarginBoxSize:function(node,_37b){
return {w:(node.offsetWidth+_37b.mE.w),h:(node.offsetHeight+_37b.mE.h)};
},getMarginBox:function(node,_37d,_37e,_37f){
var l=node.offsetLeft-_37d.mE.l,t=node.offsetTop-_37d.mE.t;
if(_37e&&_37f.UAope){
l-=_37e.bE.l;
t-=_37e.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_37d.mE.w),h:(node.offsetHeight+_37d.mE.h)};
},setMarginBox:function(node,_383,_384,_385,_386,_387,_388,_389){
var pb=_387.pbE,mb=_387.mE;
if(_385!=null&&_385>=0){
_385=Math.max(_385-pb.w-mb.w,0);
}
if(_386!=null&&_386>=0){
_386=Math.max(_386-pb.h-mb.h,0);
}
_389._setBox(node,_383,_384,_385,_386);
},evtConnect:function(_38c,_38d,_38e,_38f,_390,_391,rate){
if(!rate){
rate=0;
}
var _393={adviceType:_38c,srcObj:_38d,srcFunc:_38e,adviceObj:_38f,adviceFunc:_390,rate:rate};
if(_391==null){
_391=dojo.event;
}
_391.connect(_393);
return _393;
},evtDisconnect:function(_394,_395,_396,_397,_398,_399){
if(_399==null){
_399=dojo.event;
}
_399.disconnect({adviceType:_394,srcObj:_395,srcFunc:_396,adviceObj:_397,adviceFunc:_398});
},evtDisconnectWObj:function(_39a,_39b){
if(_39b==null){
_39b=dojo.event;
}
_39b.disconnect(_39a);
},evtDisconnectWObjAry:function(_39c,_39d){
if(_39c&&_39c.length>0){
if(_39d==null){
_39d=dojo.event;
}
for(var i=0;i<_39c.length;i++){
_39d.disconnect(_39c[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _39f=false;
var _3a0=this._popupMenuWidgets;
for(var i=0;i<_3a0.length;i++){
var _3a2=_3a0[i];
if(_3a2&&_3a2.isShowingNow){
_39f=true;
break;
}
}
return _39f;
},addPopupMenuWidget:function(_3a3){
if(_3a3){
this._popupMenuWidgets.push(_3a3);
}
},removePopupMenuWidget:function(_3a4){
if(!_3a4){
return;
}
var _3a5=this._popupMenuWidgets;
for(var i=0;i<_3a5.length;i++){
if(_3a5[i]===_3a4){
_3a5[i]=null;
}
}
}};
if(jetspeed.UAie6){
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_3a8){
this.oldXY=this.getWinDims(win,win.document,_3a8);
},getWinDims:function(win,doc,_3ab){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_3ab.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_3ab;
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
var _3b2=jetspeed;
var _3b3=this.getWinDims(window,window.document,_3b2.docBody);
this.timerId=0;
if((_3b3.x!=this.oldXY.x)||(_3b3.y!=this.oldXY.y)){
this.oldXY=_3b3;
if(_3b2.page){
if(!this.resizing){
try{
this.resizing=true;
_3b2.page.onBrowserWindowResize();
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

