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
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",P_CLASS:"portlet",PWIN_CLASS:"portletWindow",PWIN_GHOST_CLASS:"ghostPane",PW_ID_PREFIX:"pw_",PP_WIDGET_ID:"widgetId",PP_CONTENT_RETRIEVER:"contentRetriever",PP_DESKTOP_EXTENDED:"jsdesktop",PP_WINDOW_POSITION_STATIC:"windowPositionStatic",PP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PP_WINDOW_DECORATION:"windowDecoration",PP_WINDOW_TITLE:"title",PP_WINDOW_ICON:"windowIcon",PP_WIDTH:"width",PP_HEIGHT:"height",PP_LEFT:"left",PP_TOP:"top",PP_COLUMN:"column",PP_ROW:"row",PP_EXCLUDE_PCONTENT:"excludePContent",PP_WINDOW_STATE:"windowState",PP_STATICPOS:"staticpos",PP_FITHEIGHT:"fitheight",PP_PROP_SEPARATOR:"=",PP_PAIR_SEPARATOR:";",ACT_MENU:"menu",ACT_MINIMIZE:"minimized",ACT_MAXIMIZE:"maximized",ACT_RESTORE:"normal",ACT_PRINT:"print",ACT_EDIT:"edit",ACT_VIEW:"view",ACT_HELP:"help",ACT_ADDPORTLET:"addportlet",ACT_REMOVEPORTLET:"removeportlet",ACT_DESKTOP_TILE:"tile",ACT_DESKTOP_UNTILE:"untile",ACT_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACT_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACT_LOAD_RENDER:"loadportletrender",ACT_LOAD_ACTION:"loadportletaction",ACT_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",PORTAL_ORIGINATE_PARAMETER:"portal",DEBUG_WINDOW_TAG:"js-db"};
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
},portletSelectorWindowTitle:"Portlet Selector",portletSelectorWindowIcon:"text-x-script.png",portletSelectorBounds:{x:20,y:20,width:400,height:600},windowActionButtonMax:5,windowActionButtonHide:false,windowActionButtonTooltip:true,windowIconEnabled:true,windowIconPath:"/images/portlets/small/",windowDecoration:"tigris",pageActionButtonTooltip:true,getPortletDecorationBaseUrl:function(_1){
return jetspeed.prefs.getPortletDecorationsRootUrl()+"/"+_1;
},getPortletDecorationConfig:function(_2){
if(jetspeed.prefs.portletDecorationsConfig==null||_2==null){
return null;
}
return jetspeed.prefs.portletDecorationsConfig[_2];
}};
jetspeed.page=null;
jetspeed.initializeDesktop=function(){
var _3=jetspeed;
var _4=_3.id;
var _5=_3.prefs;
var _6=_3.debug;
var _7=dojo;
_3.getBody();
_3.ui.initCssObj();
_5.windowActionButtonOrder=[_4.ACT_MENU,"edit","view","help",_4.ACT_MINIMIZE,_4.ACT_RESTORE,_4.ACT_MAXIMIZE];
_5.windowActionNotPortlet=[_4.ACT_MENU,_4.ACT_MINIMIZE,_4.ACT_RESTORE,_4.ACT_MAXIMIZE];
_5.windowActionMenuOrder=[_4.ACT_DESKTOP_HEIGHT_EXPAND,_4.ACT_DESKTOP_HEIGHT_NORMAL,_4.ACT_DESKTOP_TILE,_4.ACT_DESKTOP_UNTILE];
_3.url.pathInitialize();
var _8=djConfig.jetspeed;
if(_8!=null){
for(var _9 in _8){
var _a=_8[_9];
if(_a!=null){
if(_6[_9]!=null){
_6[_9]=_a;
}else{
_5[_9]=_a;
}
}
}
if(_5.windowWidth==null||isNaN(_5.windowWidth)){
_5.windowWidth="280";
}
if(_5.windowHeight==null||isNaN(_5.windowHeight)){
_5.windowHeight="200";
}
var _b={};
_b[_4.ACT_DESKTOP_HEIGHT_EXPAND]=true;
_b[_4.ACT_DESKTOP_HEIGHT_NORMAL]=true;
_b[_4.ACT_DESKTOP_TILE]=true;
_b[_4.ACT_DESKTOP_UNTILE]=true;
_5.windowActionDesktop=_b;
}
var _c=new _7.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PortletWindow.css");
_7.html.insertCssFile(_c,document,true);
if(_5.portletDecorationsAllowed==null||_5.portletDecorationsAllowed.length==0){
if(_5.windowDecoration!=null){
_5.portletDecorationsAllowed=[_5.windowDecoration];
}
}else{
if(_5.windowDecoration==null){
_5.windowDecoration=_5.portletDecorationsAllowed[0];
}
}
if(_5.windowDecoration==null||_5.portletDecorationsAllowed==null){
_7.raise("No portlet decorations");
return;
}
if(_5.windowActionNoImage!=null){
var _d={};
for(var i=0;i<_5.windowActionNoImage.length;i++){
_d[_5.windowActionNoImage[i]]=true;
}
_5.windowActionNoImage=_d;
}
var _f=_3.url.parse(window.location.href);
var _10=_3.url.getQueryParameter(_f,"jsprintmode")=="true";
if(_10){
_10={};
_10.action=_3.url.getQueryParameter(_f,"jsaction");
_10.entity=_3.url.getQueryParameter(_f,"jsentity");
_10.layout=_3.url.getQueryParameter(_f,"jslayoutid");
_5.printModeOnly=_10;
_5.windowTiling=true;
_5.windowHeightExpand=true;
_5.ajaxPageNavigation=false;
}
_5.portletDecorationsConfig={};
for(var i=0;i<_5.portletDecorationsAllowed.length;i++){
_3.loadPortletDecorationConfig(_5.portletDecorationsAllowed[i]);
}
if(_3.UAie6){
_5.ajaxPageNavigation=false;
}
if(_10){
for(var _11 in _5.portletDecorationsConfig){
var _12=_5.portletDecorationsConfig[_11];
if(_12!=null){
_12.windowActionButtonOrder=null;
_12.windowActionMenuOrder=null;
_12.windowDisableResize=true;
_12.windowDisableMove=true;
}
}
}
_3.url.loadingIndicatorShow();
_3.page=new _3.om.Page();
if(!_10&&djConfig.isDebug){
if(_3.debugWindowLoad){
_3.debugWindowLoad();
}
if(_3.debug.profile&&_7.profile){
_7.profile.start("initializeDesktop");
}else{
_3.debug.profile=false;
}
}else{
_3.debug.profile=false;
}
_3.page.retrievePsml();
};
jetspeed.updatePage=function(_13,_14){
var _15=jetspeed;
var _16=false;
if(djConfig.isDebug&&_15.debug.profile){
_16=true;
dojo.profile.start("updatePage");
}
var _17=_15.page;
if(!_13||!_17||_15.pageNavigateSuppress){
return;
}
if(_17.equalsPageUrl(_13)){
return;
}
_13=_17.makePageUrl(_13);
if(_13!=null){
_15.updatePageBegin();
var _18=_17.layoutDecorator;
var _19=_17.editMode;
if(_16){
dojo.profile.start("destroyPage");
}
_17.destroy();
if(_16){
dojo.profile.end("destroyPage");
}
var _1a=_17.portlet_windows;
var _1b=_17.portlet_window_count;
var _1c=new _15.om.Page(_18,_13,(!djConfig.preventBackButtonFix&&!_14),_19,_17.tooltipMgr,_17.iframeCoverByWinId);
_15.page=_1c;
var _1d;
if(_1b>0){
for(var _1e in _1a){
_1d=_1a[_1e];
_1d.bringToTop(null,true);
}
}
_1c.retrievePsml(new _15.om.PageCLCreateWidget(true));
if(_1b>0){
for(var _1e in _1a){
_1d=_1a[_1e];
_1c.putPWin(_1d);
}
}
window.focus();
}
};
jetspeed.updatePageBegin=function(){
var _1f=jetspeed;
if(_1f.UAie6){
_1f.docBody.attachEvent("onclick",_1f.ie6StopMouseEvts);
_1f.docBody.setCapture();
}
};
jetspeed.ie6StopMouseEvts=function(e){
if(e){
e.cancelBubble=true;
e.returnValue=false;
}
};
jetspeed.updatePageEnd=function(){
var _21=jetspeed;
if(_21.UAie6){
_21.docBody.releaseCapture();
_21.docBody.detachEvent("onclick",_21.ie6StopMouseEvts);
_21.docBody.releaseCapture();
}
};
jetspeed.doRender=function(_22,_23){
if(!_22){
_22={};
}else{
if((typeof _22=="string"||_22 instanceof String)){
_22={url:_22};
}
}
var _24=jetspeed.page.getPortlet(_23);
if(_24){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_23+"] url: "+_22.url);
}
_24.retrieveContent(null,_22);
}
};
jetspeed.doAction=function(_25,_26){
if(!_25){
_25={};
}else{
if((typeof _25=="string"||_25 instanceof String)){
_25={url:_25};
}
}
var _27=jetspeed.page.getPortlet(_26);
if(_27){
if(jetspeed.debug.doRenderDoAction){
if(!_25.formNode){
dojo.debug("doAction ["+_26+"] url: "+_25.url+" form: null");
}else{
dojo.debug("doAction ["+_26+"] url: "+_25.url+" form: "+jetspeed.debugDumpForm(_25.formNode));
}
}
_27.retrieveContent(new jetspeed.om.PortletActionCL(_27,_25),_25);
}
};
jetspeed.PortletRenderer=function(_28,_29,_2a,_2b,_2c){
var _2d=jetspeed;
var _2e=_2d.page;
this._jsObj=_2d;
this.createWindows=_28;
this.isPageLoad=_29;
this.isPageUpdate=_2a;
this.pageLoadUrl=null;
if(_29){
this.pageLoadUrl=_2d.url.parse(_2e.getPageUrl());
}
this.renderUrl=_2b;
this.suppressGetActions=_2c;
this._colLen=_2e.columns.length;
this._colIndex=0;
this._portletIndex=0;
this.psByCol=_2e.portletsByPageColumn;
this.debugPageLoad=_2d.debug.pageLoad&&_29;
this.debugMsg=null;
if(_2d.debug.doRenderDoAction||this.debugPageLoad){
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
var _2f=this._jsObj;
var _30=this.debugMsg;
if(_30!=null){
if(this.debugPageLoad){
dojo.debug("portlet-renderer page-url: "+_2f.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPageLoad){
_2f.page.loadPostRender(this.isPageUpdate);
}
},_renderCurrent:function(){
var _31=this._jsObj;
var _32=this._colLen;
var _33=this._colIndex;
var _34=this._portletIndex;
if(_33<=_32){
var _35;
if(_33<_32){
_35=this.psByCol[_33.toString()];
}else{
_35=this.psByCol["z"];
_33=null;
}
var _36=(_35!=null?_35.length:0);
if(_36>0){
var _37=_35[_34];
if(_37){
var _38=_37.portlet;
if(this.createWindows){
_31.ui.createPortletWindow(_38,_33,_31);
}
var _39=this.debugMsg;
if(_39!=null){
if(_39.length>0){
_39=_39+", ";
}
var _3a=null;
if(_38.getProperty!=null){
_3a=_38.getProperty(_31.id.PP_WIDGET_ID);
}
if(!_3a){
_3a=_38.widgetId;
}
if(!_3a){
_3a=_38.toString();
}
if(_38.entityId){
_39=_39+_38.entityId+"("+_3a+")";
if(this._dbPgLd&&_38.getProperty(_31.id.PP_WINDOW_TITLE)){
_39=_39+" "+_38.getProperty(_31.id.PP_WINDOW_TITLE);
}
}else{
_39=_39+_3a;
}
}
_38.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}
}
}
},_evalNext:function(){
var _3b=false;
var _3c=this._colLen;
var _3d=this._colIndex;
var _3e=this._portletIndex;
var _3f=_3d;
var _40;
for(++_3d;_3d<=_3c;_3d++){
_40=this.psByCol[_3d==_3c?"z":_3d.toString()];
if(_3e<(_40!=null?_40.length:0)){
_3b=true;
this._colIndex=_3d;
break;
}
}
if(!_3b){
++_3e;
for(_3d=0;_3d<=_3f;_3d++){
_40=this.psByCol[_3d==_3c?"z":_3d.toString()];
if(_3e<(_40!=null?_40.length:0)){
_3b=true;
this._colIndex=_3d;
this._portletIndex=_3e;
break;
}
}
}
return _3b;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_41){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _43=_41;
var _44=null;
if(_41&&_41.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_41.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_41&&_41.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_41.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_44=jetspeed.url.getQueryParameter(_41,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_43)){
_43=null;
}
return {url:_43,operation:op,portletEntityId:_44};
},genPseudoUrl:function(_45,_46){
if(!_45||!_45.url||!_45.portletEntityId){
return null;
}
var _47=null;
if(_46){
_47=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_47="javascript:";
var _48=false;
if(_45.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_47+="doAction(\"";
}else{
if(_45.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_47+="doRender(\"";
}else{
_48=true;
}
}
if(_48){
return null;
}
_47+=_45.url+"\",\""+_45.portletEntityId+"\"";
_47+=")";
}
return _47;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_49){
var _4a=jetspeed.prefs.getPortletDecorationConfig(_49);
if(_4a!=null&&!_4a._initialized){
var _4b=jetspeed.prefs.getPortletDecorationBaseUrl(_49);
_4a._initialized=true;
_4a.cssPathCommon=new dojo.uri.Uri(_4b+"/css/styles.css");
_4a.cssPathDesktop=new dojo.uri.Uri(_4b+"/css/desktop.css");
dojo.html.insertCssFile(_4a.cssPathCommon,null,true);
dojo.html.insertCssFile(_4a.cssPathDesktop,null,true);
}
return _4a;
};
jetspeed.loadPortletDecorationConfig=function(_4c){
var _4d=jetspeed.prefs;
var _4e={};
_4d.portletDecorationsConfig[_4c]=_4e;
_4e.windowActionButtonOrder=_4d.windowActionButtonOrder;
_4e.windowActionNotPortlet=_4d.windowActionNotPortlet;
_4e.windowActionButtonMax=_4d.windowActionButtonMax;
_4e.windowActionButtonHide=_4d.windowActionButtonHide;
_4e.windowActionButtonTooltip=_4d.windowActionButtonTooltip;
_4e.windowActionMenuOrder=_4d.windowActionMenuOrder;
_4e.windowActionNoImage=_4d.windowActionNoImage;
_4e.windowIconEnabled=_4d.windowIconEnabled;
_4e.windowIconPath=_4d.windowIconPath;
var _4f=_4d.getPortletDecorationBaseUrl(_4c)+"/"+_4c+".js";
dojo.hostenv.loadUri(_4f,function(_50){
for(var j in _50){
_4e[j]=_50[j];
}
if(_4e.windowActionNoImage!=null){
var _52={};
for(var i=0;i<_4e.windowActionNoImage.length;i++){
_52[_4e.windowActionNoImage[i]]=true;
}
_4e.windowActionNoImage=_52;
}
if(_4e.windowIconPath!=null){
_4e.windowIconPath=dojo.string.trim(_4e.windowIconPath);
if(_4e.windowIconPath==null||_4e.windowIconPath.length==0){
_4e.windowIconPath=null;
}else{
var _54=_4e.windowIconPath;
var _55=_54.charAt(0);
if(_55!="/"){
_54="/"+_54;
}
var _56=_54.charAt(_54.length-1);
if(_56!="/"){
_54=_54+"/";
}
_4e.windowIconPath=_54;
}
}
});
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
var _57=jetspeed;
_57.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _58=_57.page.getMenuNames();
for(var i=0;i<_58.length;i++){
var _5a=_58[i];
var _5b=dojo.widget.byId(_57.id.MENU_WIDGET_ID_PREFIX+_5a);
if(_5b){
_5b.createJetspeedMenu(_57.page.getMenu(_5a));
}
}
_57.url.loadingIndicatorHide();
_57.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_5c){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_5c);
}
};
jetspeed.menuNavClickWidget=function(_5d,_5e){
dojo.debug("jetspeed.menuNavClick");
if(!_5d){
return;
}
if(dojo.lang.isString(_5d)){
var _5f=_5d;
_5d=dojo.widget.byId(_5f);
if(!_5d){
dojo.raise("Tab widget not found: "+_5f);
}
}
if(_5d){
var _60=_5d.jetspeedmenuname;
if(!_60&&_5d.extraArgs){
_60=_5d.extraArgs.jetspeedmenuname;
}
if(!_60){
dojo.raise("Tab widget is invalid: "+_5d.widgetId);
}
var _61=jetspeed.page.getMenu(_60);
if(!_61){
dojo.raise("Tab widget "+_5d.widgetId+" no menu: "+_60);
}
var _62=_61.getOptionByIndex(_5e);
jetspeed.menuNavClick(_62);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_63,_64,_65){
if(!_63||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _65=="undefined"){
_65=false;
}
if(!_65&&jetspeed.page&&jetspeed.page.equalsPageUrl(_63)){
return;
}
_63=jetspeed.page.makePageUrl(_63);
if(_64=="top"){
top.location.href=_63;
}else{
if(_64=="parent"){
parent.location.href=_63;
}else{
window.location.href=_63;
}
}
};
jetspeed.getActionsForPortlet=function(_66){
if(_66==null){
return;
}
jetspeed.getActionsForPortlets([_66]);
};
jetspeed.getActionsForPortlets=function(_67){
if(_67==null){
_67=jetspeed.page.getPortletIds();
}
var _68=new jetspeed.om.PortletActionsCL(_67);
var _69="?action=getactions";
for(var i=0;i<_67.length;i++){
_69+="&id="+_67[i];
}
var _6b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_69;
var _6c="text/xml";
var _6d=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_6b,mimetype:_6c},_68,_6d,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_6e,_6f,_70,_71){
if(_6e==null){
return;
}
if(_71==null){
_71=new jetspeed.om.PortletChangeActionCL(_6e);
}
var _72="?action=window&id="+(_6e!=null?_6e:"");
if(_6f!=null){
_72+="&state="+_6f;
}
if(_70!=null){
_72+="&mode="+_70;
}
var _73=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_72;
var _74="text/xml";
var _75=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_73,mimetype:_74},_71,_75,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(){
var _76=jetspeed;
if(!_76.page.editMode){
var _77=true;
var _78=_76.url.getQueryParameter(window.location.href,_76.id.PORTAL_ORIGINATE_PARAMETER);
if(_78!=null&&_78=="true"){
_77=false;
}
_76.page.editMode=true;
var _79=dojo.widget.byId(_76.id.PG_ED_WID);
if(_76.UAie6){
_76.page.displayAllPWins(true);
}
if(_79==null){
try{
_76.url.loadingIndicatorShow("loadpageeditor");
_79=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_76.id.PG_ED_WID,editorInitiatedFromDesktop:_77});
var _7a=document.getElementById(_76.id.COLUMNS);
_7a.insertBefore(_79.domNode,_7a.firstChild);
}
catch(e){
_76.url.loadingIndicatorHide();
if(_76.UAie6){
_76.page.displayAllPWins();
}
}
}else{
_79.editPageShow();
}
_76.page.syncPageControls();
}
};
jetspeed.editPageTerminate=function(){
if(jetspeed.page.editMode){
var _7b=dojo.widget.byId(jetspeed.id.PG_ED_WID);
_7b.editModeNormal();
jetspeed.page.editMode=false;
if(!_7b.editorInitiatedFromDesktop){
var _7c=jetspeed.page.getPageUrl(true);
_7c=jetspeed.url.removeQueryParameter(_7c,jetspeed.id.PG_ED_PARAM);
_7c=jetspeed.url.removeQueryParameter(_7c,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_7c;
}else{
var _7d=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PG_ED_PARAM);
if(_7d!=null&&_7d=="true"){
var _7e=window.location.href;
_7e=jetspeed.url.removeQueryParameter(_7e,jetspeed.id.PG_ED_PARAM);
window.location.href=_7e;
}else{
if(_7b!=null){
_7b.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_7f,_80,_81,_82){
if(!_7f){
_7f={};
}
jetspeed.url.retrieveContent(_7f,_80,_81,_82);
}};
jetspeed.om.PageCLCreateWidget=function(_83){
if(typeof _83=="undefined"){
_83=false;
}
this.isPageUpdate=_83;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_84,_85,_86){
_86.loadFromPSML(_84,this.isPageUpdate);
},notifyFailure:function(_87,_88,_89,_8a){
dojo.raise("PageCLCreateWidget error url: "+_89+" type: "+_87+jetspeed.url.formatBindError(_88));
}};
jetspeed.om.Page=function(_8b,_8c,_8d,_8e,_8f,_90){
if(_8b!=null&&_8c!=null){
this.requiredLayoutDecorator=_8b;
this.setPsmlPathFromDocumentUrl(_8c);
this.pageUrlFallback=_8c;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _8d!="undefined"){
this.addToHistory=_8d;
}
if(typeof _8e!="undefined"){
this.editMode=_8e;
}
this.layouts={};
this.columns=[];
this.portlets=[];
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_90!=null){
this.iframeCoverByWinId=_90;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_8f!=null){
this.tooltipMgr=_8f;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _91=(this.name!=null&&this.name.length>0?this.name:null);
if(!_91){
this.getPsmlUrl();
_91=this.psmlPath;
}
return "page-"+_91;
},setPsmlPathFromDocumentUrl:function(_92){
var _93=jetspeed.url.path.AJAX_API;
var _94=null;
if(_92==null){
_94=window.location.pathname;
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _95=window.location.hash;
if(_95!=null&&_95.length>0){
if(_95.indexOf("#")==0){
_95=(_95.length>1?_95.substring(1):"");
}
if(_95!=null&&_95.length>1&&_95.indexOf("/")==0){
this.psmlPath=jetspeed.url.path.AJAX_API+_95;
return;
}
}
}
}else{
var _96=jetspeed.url.parse(_92);
_94=_96.path;
}
var _97=jetspeed.url.path.DESKTOP;
var _98=_94.indexOf(_97);
if(_98!=-1&&_94.length>(_98+_97.length)){
_93=_93+_94.substring(_98+_97.length);
}
this.psmlPath=_93;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _99=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_99=jetspeed.url.addQueryParameter(_99,"layoutid",jetspeed.prefs.printModeOnly.layout);
_99=jetspeed.url.addQueryParameter(_99,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _99;
},retrievePsml:function(_9a){
if(_9a==null){
_9a=new jetspeed.om.PageCLCreateWidget();
}
var _9b=this.getPsmlUrl();
var _9c="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_9b);
}
jetspeed.url.retrieveContent({url:_9b,mimetype:_9c},_9a,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_9d,_9e){
var _9f=jetspeed;
var _a0=_9f.prefs.printModeOnly;
if(djConfig.isDebug&&_9f.debug.profile&&_a0==null){
dojo.profile.start("loadFromPSML");
}
var _a1=this._parsePSML(_9d);
if(_a1==null){
return;
}
this.portletsByPageColumn={};
this.columnsStructure=this._layoutCreateModel(_a1,null,this.portletsByPageColumn,true);
this.rootFragmentId=_a1.id;
var _a2=false;
if(this.editMode){
this.editMode=false;
if(_a0==null){
_a2=true;
}
}
if(_9f.prefs.windowTiling){
this._createColsStart(document.getElementById(_9f.id.DESKTOP));
}
var _a3=this.portletsByPageColumn["z"];
if(_a3){
_a3.sort(this._loadPortletZIndexCompare);
}
var _a4=new jetspeed.PortletRenderer(true,true,_9e,null,true);
_a4.renderAllTimeDistribute();
},loadPostRender:function(_a5){
var _a6=jetspeed;
var _a7=_a6.prefs.printModeOnly;
if(_a7==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
var _a8=false;
if(this.editMode){
_a8=true;
}
var _a9=_a6.url.getQueryParameter(window.location.href,_a6.id.PG_ED_PARAM);
if(_a8||(_a9!=null&&_a9=="true")||this.actions[_a6.id.ACT_VIEW]!=null){
_a8=false;
if(this.actions!=null&&(this.actions[_a6.id.ACT_EDIT]!=null||this.actions[_a6.id.ACT_VIEW]!=null)){
_a8=true;
}
}
this.retrieveMenuDeclarations(true,_a8,_a5);
this.renderPageControls();
this.syncPageControls();
}else{
for(var _aa in this.portlets){
var _ab=this.portlets[_aa];
if(_ab!=null){
_ab.renderAction(null,_a7.action);
}
break;
}
if(_a5){
_a6.updatePageEnd();
}
}
},_parsePSML:function(_ac){
var _ad=jetspeed;
var _ae=_ac.getElementsByTagName("page");
if(!_ae||_ae.length>1||_ae[0]==null){
dojo.raise("Expected one <page> in PSML");
}
var _af=_ae[0];
var _b0=_af.childNodes;
var _b1=new RegExp("(name|path|profiledPath|title|short-title)");
var _b2=null;
var _b3={};
for(var i=0;i<_b0.length;i++){
var _b5=_b0[i];
if(_b5.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _b6=_b5.nodeName;
if(_b6=="fragment"){
_b2=_b5;
}else{
if(_b6=="defaults"){
this.layoutDecorator=_b5.getAttribute("layout-decorator");
this.portletDecorator=_b5.getAttribute("portlet-decorator");
}else{
if(_b6&&_b6.match(_b1)){
if(_b6=="short-title"){
_b6="shortTitle";
}
this[_b6]=((_b5&&_b5.firstChild)?_b5.firstChild.nodeValue:null);
}else{
if(_b6=="action"){
this._parsePSMLAction(_b5,_b3);
}
}
}
}
}
this.actions=_b3;
if(_b2==null){
dojo.raise("No root fragment in PSML");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_ad.debug.ajaxPageNav){
dojo.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_ad.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _b7=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(_ad.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_b7);
}
_ad.updatePage(_b7,true);
},forward:function(){
if(_ad.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_b7);
}
_ad.updatePage(_b7,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_ad.prefs.ajaxPageNavigation){
var _b7=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(_ad.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_b7);
}
_ad.updatePage(_b7,true);
},forward:function(){
if(_ad.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_b7);
}
_ad.updatePage(_b7,true);
},changeUrl:escape(this.getPath())});
}
}
var _b8=this._parsePSMLFrag(_b2,0);
return _b8;
},_parsePSMLFrag:function(_b9,_ba){
var _bb=jetspeed;
var _bc=new Array();
var _bd=((_b9!=null)?_b9.getAttribute("type"):null);
if(_bd!="layout"){
dojo.raise("Expected layout fragment: "+_b9);
return null;
}
var _be=false;
var _bf=_b9.getAttribute("name");
if(_bf!=null){
_bf=_bf.toLowerCase();
if(_bf.indexOf("noactions")!=-1){
_be=true;
}
}
var _c0=null,_c1=0;
var _c2={};
var _c3=_b9.childNodes;
var _c4,_c5,_c6,_c7,_c8;
for(var i=0;i<_c3.length;i++){
_c4=_c3[i];
if(_c4.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_c5=_c4.nodeName;
if(_c5=="fragment"){
_c8=_c4.getAttribute("type");
if(_c8=="layout"){
var _ca=this._parsePSMLFrag(_c4,i);
if(_ca!=null){
_bc.push(_ca);
}
}else{
var _cb=this._parsePSMLProps(_c4,null);
var _cc=_cb[_bb.id.PP_WINDOW_ICON];
if(_cc==null||_cc.length==0){
_cc=this._parsePSMLIcon(_c4);
if(_cc!=null&&_cc.length>0){
_cb[_bb.id.PP_WINDOW_ICON]=_cc;
}
}
_bc.push({id:_c4.getAttribute("id"),type:_c8,name:_c4.getAttribute("name"),properties:_cb,actions:this._parsePSMLActions(_c4,null),currentActionState:this._parsePSMLActionState(_c4),currentActionMode:this._parsePSMLActionMode(_c4),decorator:_c4.getAttribute("decorator"),layoutActionsDisabled:_be,documentOrderIndex:i});
}
}else{
if(_c5=="property"){
if(this._parsePSMLProp(_c4,_c2)=="sizes"){
if(_c0!=null){
dojo.raise("Layout fragment has multiple sizes definitions: "+_b9);
return null;
}
if(_bb.prefs.printModeOnly!=null){
_c0=["100"];
_c1=100;
}else{
_c7=_c4.getAttribute("value");
if(_c7!=null&&_c7.length>0){
_c0=_c7.split(",");
for(var j=0;j<_c0.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_c0[j]=_c0[j].replace(re,"$1");
_c1+=new Number(_c0[j]);
}
}
}
}
}
}
}
_bc.sort(this._fragmentRowCompare);
var _cf=new Array();
var _d0=new Array();
for(var i=0;i<_bc.length;i++){
if(_bc[i].type=="layout"){
_cf.push(i);
}else{
_d0.push(i);
}
}
if(_c0==null){
_c0=new Array();
_c0.push("100");
_c1=100;
}
return {id:_b9.getAttribute("id"),type:_bd,name:_b9.getAttribute("name"),decorator:_b9.getAttribute("decorator"),columnSizes:_c0,columnSizesSum:_c1,properties:_c2,fragments:_bc,layoutFragmentIndexes:_cf,otherFragmentIndexes:_d0,layoutActionsDisabled:_be,documentOrderIndex:_ba};
},_parsePSMLActions:function(_d1,_d2){
if(_d2==null){
_d2={};
}
var _d3=_d1.getElementsByTagName("action");
for(var _d4=0;_d4<_d3.length;_d4++){
var _d5=_d3[_d4];
this._parsePSMLAction(_d5,_d2);
}
return _d2;
},_parsePSMLAction:function(_d6,_d7){
var _d8=_d6.getAttribute("id");
if(_d8!=null){
var _d9=_d6.getAttribute("type");
var _da=_d6.getAttribute("name");
var _db=_d6.getAttribute("url");
var _dc=_d6.getAttribute("alt");
_d7[_d8.toLowerCase()]={id:_d8,type:_d9,label:_da,url:_db,alt:_dc};
}
},_parsePSMLActionState:function(_dd){
var _de=_dd.getElementsByTagName("state");
if(_de!=null&&_de.length==1&&_de[0].firstChild!=null){
return _de[0].firstChild.nodeValue;
}
return null;
},_parsePSMLActionMode:function(_df){
var _e0=_df.getElementsByTagName("mode");
if(_e0!=null&&_e0.length==1&&_e0[0].firstChild!=null){
return _e0[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_e1){
var _e2=_e1.getElementsByTagName("icon");
if(_e2!=null&&_e2.length==1&&_e2[0].firstChild!=null){
return _e2[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProps:function(_e3,_e4){
if(_e4==null){
_e4={};
}
var _e5=_e3.getElementsByTagName("property");
for(var _e6=0;_e6<_e5.length;_e6++){
this._parsePSMLProp(_e5[_e6],_e4);
}
return _e4;
},_parsePSMLProp:function(_e7,_e8){
var _e9=_e7.getAttribute("name");
var _ea=_e7.getAttribute("value");
_e8[_e9]=_ea;
return _e9;
},_fragmentRowCompare:function(_eb,_ec){
var _ed=_eb.documentOrderIndex*1000;
var _ee=_ec.documentOrderIndex*1000;
var _ef=_eb.properties["row"];
if(_ef!=null){
_ed=_ef;
}
var _f0=_ec.properties["row"];
if(_f0!=null){
_ee=_f0;
}
return (_ed-_ee);
},_layoutCreateModel:function(_f1,_f2,_f3,_f4){
var _f5=jetspeed;
var _f6=dojo;
var _f7=this.columns.length;
var _f8=this._layoutCreateColsModel(_f1,_f2,_f4);
var _f9=_f8.columnsInLayout;
if(_f8.addedLayoutHeaderColumn){
_f7++;
}
var _fa=(_f9==null?0:_f9.length);
if(_f1.layoutFragmentIndexes!=null&&_f1.layoutFragmentIndexes.length>0){
var _fb=null;
var _fc=0;
if(_f1.otherFragmentIndexes!=null&&_f1.otherFragmentIndexes.length>0){
_fb=new Array();
}
for(var i=0;i<_f1.fragments.length;i++){
var _fe=_f1.fragments[i];
}
var _ff=new Array();
for(var i=0;i<_fa;i++){
if(_fb!=null){
_fb.push(null);
}
_ff.push(false);
}
for(var i=0;i<_f1.fragments.length;i++){
var _fe=_f1.fragments[i];
var _100=i;
if(_fe.properties&&_fe.properties[_f5.id.PP_COLUMN]>=0){
if(_fe.properties[_f5.id.PP_COLUMN]!=null&&_fe.properties[_f5.id.PP_COLUMN]>=0){
_100=_fe.properties[_f5.id.PP_COLUMN];
}
}
if(_100>=_fa){
_100=(_fa>0?(_fa-1):0);
}
var _101=((_fb==null)?null:_fb[_100]);
if(_fe.type=="layout"){
_ff[_100]=true;
if(_101!=null){
this._layoutCreateModel(_101,_f9[_100],_f3,true);
_fb[_100]=null;
}
this._layoutCreateModel(_fe,_f9[_100],_f3,false);
}else{
if(_101==null){
_fc++;
var _102={};
_f6.lang.mixin(_102,_f1);
_102.fragments=new Array();
_102.layoutFragmentIndexes=new Array();
_102.otherFragmentIndexes=new Array();
_102.documentOrderIndex=_f1.fragments[i].documentOrderIndex;
_102.clonedFromRootId=_102.id;
_102.clonedLayoutFragmentIndex=_fc;
_102.columnSizes=["100"];
_102.columnSizesSum=[100];
_102.id=_102.id+"-jsclone_"+_fc;
_fb[_100]=_102;
_101=_102;
}
_101.fragments.push(_fe);
_101.otherFragmentIndexes.push(_101.fragments.length-1);
}
}
if(_fb!=null){
for(var i=0;i<_fa;i++){
var _101=_fb[i];
if(_101!=null){
_ff[i]=true;
this._layoutCreateModel(_101,_f9[i],_f3,true);
}
}
}
for(var i=0;i<_fa;i++){
if(_ff[i]){
_f9[i].columnContainer=true;
}
}
if(_f1.otherFragmentIndexes!=null&&_f1.otherFragmentIndexes.length>0){
var _103=new Array();
for(var i=0;i<_f1.fragments.length;i++){
var _104=true;
for(var j=0;j<_f1.otherFragmentIndexes.length;j++){
if(_f1.otherFragmentIndexes[j]==i){
_104=false;
break;
}
}
if(_104){
_103.push(_f1.fragments[i]);
}
}
_f1.fragments=_103;
_f1.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_f1,_f9,_f7,_f3);
return _f9;
},_layoutFragChildCollapse:function(_106,_107){
var _108=jetspeed;
var _109=false;
if(_107==null){
_107=_106;
}
if(_106.layoutFragmentIndexes!=null&&_106.layoutFragmentIndexes.length>0){
_109=true;
for(var i=0;i<_106.layoutFragmentIndexes.length;i++){
var _10b=_106.fragments[_106.layoutFragmentIndexes[i]];
if(_10b.otherFragmentIndexes!=null&&_10b.otherFragmentIndexes.length>0){
for(var i=0;i<_10b.otherFragmentIndexes.length;i++){
var _10c=_10b.fragments[_10b.otherFragmentIndexes[i]];
_10c.properties[_108.id.PP_COLUMN]=-1;
_10c.properties[_108.id.PP_ROW]=-1;
_10c.documentOrderIndex=_107.fragments.length;
_107.fragments.push(_10c);
_107.otherFragIndexes.push(_107.fragments.length);
}
}
this._layoutFragChildCollapse(_10b,_107);
}
}
return _109;
},_layoutCreateColsModel:function(_10d,_10e,_10f){
var _110=jetspeed;
this.layouts[_10d.id]=_10d;
var _111=false;
var _112=new Array();
if(_110.prefs.windowTiling&&_10d.columnSizes.length>0){
var _113=false;
if(_110.UAie){
_113=true;
}
if(_10e!=null&&!_10f){
var _114=new _110.om.Column(0,_10d.id,(_113?_10d.columnSizesSum-0.1:_10d.columnSizesSum),this.columns.length,_10d.layoutActionsDisabled);
_114.layoutHeader=true;
this.columns.push(_114);
if(_10e.columnChildren==null){
_10e.columnChildren=new Array();
}
_10e.columnChildren.push(_114);
_10e=_114;
_111=true;
}
for(var i=0;i<_10d.columnSizes.length;i++){
var size=_10d.columnSizes[i];
if(_113&&i==(_10d.columnSizes.length-1)){
size=size-0.1;
}
var _117=new _110.om.Column(i,_10d.id,size,this.columns.length,_10d.layoutActionsDisabled);
this.columns.push(_117);
if(_10e!=null){
if(_10e.columnChildren==null){
_10e.columnChildren=new Array();
}
_10e.columnChildren.push(_117);
}
_112.push(_117);
}
}
return {columnsInLayout:_112,addedLayoutHeaderColumn:_111};
},_layoutCreatePortletsModel:function(_118,_119,_11a,_11b){
var _11c=jetspeed;
if(_118.otherFragmentIndexes!=null&&_118.otherFragmentIndexes.length>0){
var _11d=new Array();
for(var i=0;i<_119.length;i++){
_11d.push(new Array());
}
for(var i=0;i<_118.otherFragmentIndexes.length;i++){
var _11f=_118.fragments[_118.otherFragmentIndexes[i]];
if(_11c.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(_11c.debugPortletEntityIdFilter,_11f.id)){
_11f=null;
}
}
if(_11f!=null){
var _120="z";
var _121=_11f.properties[_11c.id.PP_DESKTOP_EXTENDED];
var _122=_11c.prefs.windowTiling;
var _123=_11c.prefs.windowHeightExpand;
if(_121!=null&&_11c.prefs.windowTiling&&_11c.prefs.printModeOnly==null){
var _124=_121.split(_11c.id.PP_PAIR_SEPARATOR);
var _125=null,_126=0,_127=null,_128=null,_129=false;
if(_124!=null&&_124.length>0){
var _12a=_11c.id.PP_PROP_SEPARATOR;
for(var _12b=0;_12b<_124.length;_12b++){
_125=_124[_12b];
_126=((_125!=null)?_125.length:0);
if(_126>0){
var _12c=_125.indexOf(_12a);
if(_12c>0&&_12c<(_126-1)){
_127=_125.substring(0,_12c);
_128=_125.substring(_12c+1);
_129=((_128=="true")?true:false);
if(_127==_11c.id.PP_STATICPOS){
_122=_129;
}else{
if(_127==_11c.id.PP_FITHEIGHT){
_123=_129;
}
}
}
}
}
}
}else{
if(!_11c.prefs.windowTiling){
_122=false;
}
}
_11f.properties[_11c.id.PP_WINDOW_POSITION_STATIC]=_122;
_11f.properties[_11c.id.PP_WINDOW_HEIGHT_TO_FIT]=_123;
if(_122&&_11c.prefs.windowTiling){
var _12d=_11f.properties[_11c.id.PP_COLUMN];
if(_12d==null||_12d==""||_12d<0){
var _12e=-1;
for(var j=0;j<_119.length;j++){
if(_12e==-1||_11d[j].length<_12e){
_12e=_11d[j].length;
_12d=j;
}
}
}else{
if(_12d>=_119.length){
_12d=_119.length-1;
}
}
_11d[_12d].push(_11f.id);
var _130=_11a+new Number(_12d);
_120=_130.toString();
}
var _131=new _11c.om.Portlet(_11f.name,_11f.id,null,_11f.properties,_11f.actions,_11f.currentActionState,_11f.currentActionMode,_11f.decorator,_11f.layoutActionsDisabled);
_131.initialize();
this.putPortlet(_131);
if(_11b[_120]==null){
_11b[_120]=new Array();
}
_11b[_120].push({portlet:_131,layout:_118.id});
}
}
}
},_portletsInitWinState:function(_132){
var _133={};
this.getPortletCurColRow(null,false,_133);
for(var _134 in this.portlets){
var _135=this.portlets[_134];
var _136=_133[_135.getId()];
if(_136==null&&_132){
for(var i=0;i<_132.length;i++){
if(_132[i].portlet.getId()==_135.getId()){
_136={layout:_132[i].layout};
break;
}
}
}
if(_136!=null){
_135._initWinState(_136,false);
}else{
dojo.raise("Window state data not found for portlet: "+_135.getId());
}
}
},_loadPortletZIndexCompare:function(_138,_139){
var _13a=null;
var _13b=null;
var _13c=null;
_13a=_138.portlet._getInitialZIndex();
_13b=_139.portlet._getInitialZIndex();
if(_13a&&!_13b){
return -1;
}else{
if(_13b&&!_13a){
return 1;
}else{
if(_13a==_13b){
return 0;
}
}
}
return (_13a-_13b);
},_createColsStart:function(_13d){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _13e=document.createElement("div");
_13e.id=jetspeed.id.COLUMNS;
_13e.setAttribute("id",jetspeed.id.COLUMNS);
for(var _13f=0;_13f<this.columnsStructure.length;_13f++){
var _140=this.columnsStructure[_13f];
this._createCols(_140,_13e);
}
_13d.appendChild(_13e);
},_createCols:function(_141,_142){
_141.createColumn();
if(_141.columnChildren!=null&&_141.columnChildren.length>0){
for(var _143=0;_143<_141.columnChildren.length;_143++){
var _144=_141.columnChildren[_143];
this._createCols(_144,_141.domNode);
}
}
_142.appendChild(_141.domNode);
},_removeCols:function(_145){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_145){
var _147=jetspeed.ui.getPWinChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_147,function(_148){
_145.appendChild(_148);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _149=dojo.byId(jetspeed.id.COLUMNS);
if(_149){
dojo.dom.removeNode(_149);
}
this.columns=[];
},getPortletCurColRow:function(_14a,_14b,_14c){
if(!this.columns||this.columns.length==0){
return null;
}
var _14d=null;
var _14e=((_14a!=null)?true:false);
var _14f=0;
var _150=null;
var _151=null;
var _152=0;
var _153=false;
for(var _154=0;_154<this.columns.length;_154++){
var _155=this.columns[_154];
var _156=_155.domNode.childNodes;
if(_151==null||_151!=_155.getLayoutId()){
_151=_155.getLayoutId();
_150=this.layouts[_151];
if(_150==null){
dojo.raise("Layout not found: "+_151);
return null;
}
_152=0;
_153=false;
if(_150.clonedFromRootId==null){
_153=true;
}else{
var _157=this.getColFromColNode(_155.domNode.parentNode);
if(_157==null){
dojo.raise("Parent column not found: "+_155);
return null;
}
_155=_157;
}
}
var _158=null;
var _159=jetspeed;
var _15a=dojo;
for(var _15b=0;_15b<_156.length;_15b++){
var _15c=_156[_15b];
if(_15a.html.hasClass(_15c,_159.id.PWIN_CLASS)||(_14b&&_15a.html.hasClass(_15c,_159.id.PWIN_GHOST_CLASS))||(_14e&&_15a.html.hasClass(_15c,"desktopColumn"))){
_158=(_158==null?0:_158+1);
if((_158+1)>_152){
_152=(_158+1);
}
if(_14a==null||_15c==_14a){
var _15d={layout:_151,column:_155.getLayoutColumnIndex(),row:_158};
if(!_153){
_15d.layout=_150.clonedFromRootId;
}
if(_14a!=null){
_14d=_15d;
break;
}else{
if(_14c!=null){
var _15e=this.getPWinFromNode(_15c);
if(_15e==null){
_15a.raise("PortletWindow not found for node");
}else{
var _15f=_15e.portlet;
if(_15f==null){
_15a.raise("PortletWindow for node has null portlet: "+_15e.widgetId);
}else{
_14c[_15f.getId()]=_15d;
}
}
}
}
}
}
}
if(_14d!=null){
break;
}
}
return _14d;
},_getPortletArrayByZIndex:function(){
var _160=jetspeed;
var _161=this.getPortletArray();
if(!_161){
return _161;
}
var _162=[];
for(var i=0;i<_161.length;i++){
if(!_161[i].getProperty(_160.id.PP_WINDOW_POSITION_STATIC)){
_162.push(_161[i]);
}
}
_162.sort(this._portletZIndexCompare);
return _162;
},_portletZIndexCompare:function(_164,_165){
var _166=null;
var _167=null;
var _168=null;
_168=_164.getSavedWinState();
_166=_168.zIndex;
_168=_165.getSavedWinState();
_167=_168.zIndex;
if(_166&&!_167){
return -1;
}else{
if(_167&&!_166){
return 1;
}else{
if(_166==_167){
return 0;
}
}
}
return (_166-_167);
},getPortletDecorationDefault:function(){
var _169=jetspeed;
var pd=null;
if(djConfig.isDebug&&_169.debug.windowDecorationRandom){
pd=_169.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_169.prefs.portletDecorationsAllowed.length)];
}else{
var _16b=this.getPortletDecorator();
if(dojo.lang.indexOf(_169.prefs.portletDecorationsAllowed,_16b)!=-1){
pd=_16b;
}else{
pd=_169.prefs.windowDecoration;
}
}
return pd;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _16c=[];
for(var _16d in this.portlets){
var _16e=this.portlets[_16d];
_16c.push(_16e);
}
return _16c;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _16f=[];
for(var _170 in this.portlets){
var _171=this.portlets[_170];
_16f.push(_171.getId());
}
return _16f;
},getPortletByName:function(_172){
if(this.portlets&&_172){
for(var _173 in this.portlets){
var _174=this.portlets[_173];
if(_174.name==_172){
return _174;
}
}
}
return null;
},getPortlet:function(_175){
if(this.portlets&&_175){
return this.portlets[_175];
}
return null;
},getPWinFromNode:function(_176){
var _177=null;
if(this.portlets&&_176){
for(var _178 in this.portlets){
var _179=this.portlets[_178];
var _17a=_179.getPWin();
if(_17a!=null){
if(_17a.domNode==_176){
_177=_17a;
break;
}
}
}
}
return _177;
},putPortlet:function(_17b){
if(!_17b){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_17b.entityId]=_17b;
this.portlet_count++;
},putPWin:function(_17c){
if(!_17c){
return;
}
var _17d=_17c.widgetId;
if(!_17d){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_17d]=_17c;
this.portlet_window_count++;
},getPWin:function(_17e){
if(this.portlet_windows&&_17e){
return this.portlet_windows[_17e];
}
return null;
},getPWins:function(_17f){
var _180=this.portlet_windows;
var pWin;
var _182=[];
for(var _183 in _180){
pWin=_180[_183];
if(pWin&&(!_17f||pWin.portlet)){
_182.push(pWin);
}
}
return _182;
},getPWinTopZIndex:function(_184){
var _185=0;
if(_184){
_185=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_185;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_185=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_185;
}
return _185;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_186){
var _187=this.getPortletArray();
for(var i=0;i<_187.length;i++){
var _189=_187[i];
var pWin=_189.getPWin();
if(pWin){
if(_186){
pWin.domNode.style.display="none";
}else{
pWin.domNode.style.display="";
}
}
}
},regPWinIFrameCover:function(_18b){
if(!_18b){
return;
}
this.iframeCoverByWinId[_18b.widgetId]=true;
},unregPWinIFrameCover:function(_18c){
if(!_18c){
return;
}
delete this.iframeCoverByWinId[_18c.widgetId];
},displayAllPWinIFrameCovers:function(_18d,_18e){
var _18f=this.portlet_windows;
var _190=this.iframeCoverByWinId;
if(!_18f||!_190){
return;
}
for(var _191 in _190){
if(_191==_18e){
continue;
}
var pWin=_18f[_191];
var _193=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_193){
if(_18d){
_193.style.display="none";
}else{
_193.style.display="block";
}
}
}
},destroy:function(){
var _194=this.portlet_windows;
var _195=this.getPWins(true);
var pWin,_197;
for(var i=0;i<_195.length;i++){
pWin=_195[i];
_197=pWin.widgetId;
pWin.closeWindow();
delete _194[_197];
this.portlet_window_count--;
}
this.portlets=[];
this.portlet_count=0;
var _199=dojo.widget.byId(jetspeed.id.PG_ED_WID);
if(_199!=null){
_199.editPageDestroy();
}
this._removeCols(document.getElementById(jetspeed.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_19a){
if(_19a==null){
return null;
}
var _19b=_19a.getAttribute("columnIndex");
if(_19b==null){
return null;
}
var _19c=new Number(_19b);
if(_19c>=0&&_19c<this.columns.length){
return this.columns[_19c];
}
return null;
},getColIndexForNode:function(node){
var _19e=null;
if(!this.columns){
return _19e;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_19e=i;
break;
}
}
return _19e;
},getColWithNode:function(node){
var _1a1=this.getColIndexForNode(node);
return ((_1a1!=null&&_1a1>=0)?this.columns[_1a1]:null);
},getDescendantCols:function(_1a2){
var dMap={};
if(_1a2==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1a2&&_1a2.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_1a6){
if(!_1a6){
return;
}
var _1a7=(_1a6.getName?_1a6.getName():null);
if(_1a7!=null){
this.menus[_1a7]=_1a6;
}
},getMenu:function(_1a8){
if(_1a8==null){
return null;
}
return this.menus[_1a8];
},removeMenu:function(_1a9){
if(_1a9==null){
return;
}
var _1aa=null;
if(dojo.lang.isString(_1a9)){
_1aa=_1a9;
}else{
_1aa=(_1a9.getName?_1a9.getName():null);
}
if(_1aa!=null){
delete this.menus[_1aa];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1ab=[];
for(var _1ac in this.menus){
_1ab.push(_1ac);
}
return _1ab;
},retrieveMenuDeclarations:function(_1ad,_1ae,_1af){
contentListener=new jetspeed.om.MenusApiCL(_1ad,_1ae,_1af);
this.clearMenus();
var _1b0="?action=getmenus";
if(_1ad){
_1b0+="&includeMenuDefs=true";
}
var _1b1=this.getPsmlUrl()+_1b0;
var _1b2="text/xml";
var _1b3=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1b1,mimetype:_1b2},contentListener,_1b3,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
var jsId=jetspeed.id;
if(this.actionButtons==null){
return;
}
for(var _1b5 in this.actionButtons){
var _1b6=false;
if(_1b5==jsId.ACT_EDIT){
if(!this.editMode){
_1b6=true;
}
}else{
if(_1b5==jsId.ACT_VIEW){
if(this.editMode){
_1b6=true;
}
}else{
if(_1b5==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_1b6=true;
}
}else{
_1b6=true;
}
}
}
if(_1b6){
this.actionButtons[_1b5].style.display="";
}else{
this.actionButtons[_1b5].style.display="none";
}
}
},renderPageControls:function(){
var _1b7=jetspeed;
var jsId=_1b7.id;
var _1b9=dojo;
var _1ba=[];
if(this.actions!=null){
for(var _1bb in this.actions){
if(_1bb!=jsId.ACT_HELP){
_1ba.push(_1bb);
}
if(_1bb==jsId.ACT_EDIT){
_1ba.push(jsId.ACT_ADDPORTLET);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
if(this.actions[jsId.ACT_VIEW]==null){
_1ba.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
if(this.actions[jsId.ACT_EDIT]==null){
_1ba.push(jsId.ACT_EDIT);
}
}
}
var _1bc=_1b9.byId(jsId.PAGE_CONTROLS);
if(_1bc!=null&&_1ba!=null&&_1ba.length>0){
var _1bd=_1b7.prefs;
var jsUI=_1b7.ui;
var _1bf=_1b9.event;
var _1c0=_1b7.page.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _1c1=this.actionButtonTooltips;
for(var i=0;i<_1ba.length;i++){
var _1bb=_1ba[i];
var _1c3=document.createElement("div");
_1c3.className="portalPageActionButton";
_1c3.style.backgroundImage="url("+_1bd.getLayoutRootUrl()+"/images/desktop/"+_1bb+".gif)";
_1c3.actionName=_1bb;
this.actionButtons[_1bb]=_1c3;
_1bc.appendChild(_1c3);
jsUI.evtConnect("after",_1c3,"onclick",this,"pageActionButtonClick",_1bf);
if(_1bd.pageActionButtonTooltip){
var _1c4=null;
if(_1bd.desktopActionLabels!=null){
_1c4=_1bd.desktopActionLabels[_1bb];
}
if(_1c4==null||_1c4.length==0){
_1c4=_1b9.string.capitalize(_1bb);
}
_1c1.push(_1c0.addNode(_1c3,_1c4,true,_1b7,jsUI,_1bf));
}
}
}
},_destroyPageControls:function(){
var _1c5=jetspeed;
if(this.actionButtons){
for(var _1c6 in this.actionButtons){
var _1c7=this.actionButtons[_1c6];
if(_1c7){
_1c5.ui.evtDisconnect("after",_1c7,"onclick",this,"pageActionButtonClick");
}
}
}
var _1c8=dojo.byId(_1c5.id.PAGE_CONTROLS);
if(_1c8!=null&&_1c8.childNodes&&_1c8.childNodes.length>0){
for(var i=(_1c8.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1c8.childNodes[i]);
}
}
_1c5.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_1cb){
var _1cc=jetspeed;
if(_1cb==null){
return;
}
if(_1cb==_1cc.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1cb==_1cc.id.ACT_EDIT){
_1cc.editPageInitiate();
}else{
if(_1cb==_1cc.id.ACT_VIEW){
_1cc.editPageTerminate();
}else{
var _1cd=this.getPageAction(_1cb);
alert("pageAction "+_1cb+" : "+_1cd);
if(_1cd==null){
return;
}
if(_1cd.url==null){
return;
}
var _1ce=_1cc.url.basePortalUrl()+_1cc.url.path.DESKTOP+"/"+_1cd.url;
_1cc.pageNavigate(_1ce);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1d0,_1d1){
var _1d2=jetspeed;
if(!_1d1){
_1d1=escape(this.getPagePathAndQuery());
}else{
_1d1=escape(_1d1);
}
var _1d3=_1d2.url.basePortalUrl()+_1d2.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1d1;
if(_1d0!=null){
_1d3+="&jslayoutid="+escape(_1d0);
}
_1d2.changeActionForPortlet(this.rootFragmentId,null,_1d2.id.ACT_EDIT,new jetspeed.om.PageChangeActionCL(_1d3));
},setPageModePortletActions:function(_1d4){
if(_1d4==null||_1d4.actions==null){
return;
}
var jsId=jetspeed.id;
if(_1d4.actions[jsId.ACT_REMOVEPORTLET]==null){
_1d4.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_1d6){
if(this.pageUrl!=null&&!_1d6){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _1d8=jsU.path.SERVER+((_1d6)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _1d9=jsU.parse(_1d8);
var _1da=null;
if(this.pageUrlFallback!=null){
_1da=jsU.parse(this.pageUrlFallback);
}else{
_1da=jsU.parse(window.location.href);
}
if(_1d9!=null&&_1da!=null){
var _1db=_1da.query;
if(_1db!=null&&_1db.length>0){
var _1dc=_1d9.query;
if(_1dc!=null&&_1dc.length>0){
_1d8=_1d8+"&"+_1db;
}else{
_1d8=_1d8+"?"+_1db;
}
}
}
if(!_1d6){
this.pageUrl=_1d8;
}
return _1d8;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _1de=this.getPath();
var _1df=jsU.parse(_1de);
var _1e0=null;
if(this.pageUrlFallback!=null){
_1e0=jsU.parse(this.pageUrlFallback);
}else{
_1e0=jsU.parse(window.location.href);
}
if(_1df!=null&&_1e0!=null){
var _1e1=_1e0.query;
if(_1e1!=null&&_1e1.length>0){
var _1e2=_1df.query;
if(_1e2!=null&&_1e2.length>0){
_1de=_1de+"&"+_1e1;
}else{
_1de=_1de+"?"+_1e1;
}
}
}
this.pagePathAndQuery=_1de;
return _1de;
},getPageDirectory:function(_1e3){
var _1e4="/";
var _1e5=(_1e3?this.getRealPath():this.getPath());
if(_1e5!=null){
var _1e6=_1e5.lastIndexOf("/");
if(_1e6!=-1){
if((_1e6+1)<_1e5.length){
_1e4=_1e5.substring(0,_1e6+1);
}else{
_1e4=_1e5;
}
}
}
return _1e4;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_1e8){
if(!_1e8){
_1e8="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_1e8)){
return jsU.path.SERVER+jsU.path.DESKTOP+_1e8;
}
return _1e8;
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
jetspeed.om.Column=function(_1ea,_1eb,size,_1ed,_1ee){
this.layoutColumnIndex=_1ea;
this.layoutId=_1eb;
this.size=size;
this.pageColumnIndex=new Number(_1ed);
if(typeof _1ee!="undefined"){
this.layoutActionsDisabled=_1ee;
}
this.id="jscol_"+_1ed;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_1ef){
var _1f0="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_1f0="desktopColumn desktopColumnClear";
}
var _1f1=document.createElement("div");
_1f1.setAttribute("columnIndex",this.getPageColumnIndex());
_1f1.style.width=this.size+"%";
if(this.layoutHeader){
_1f0="desktopColumn desktopLayoutHeader";
}else{
_1f1.style.minHeight="40px";
}
_1f1.className=_1f0;
_1f1.id=this.getId();
this.domNode=_1f1;
if(_1ef!=null){
_1ef.appendChild(_1f1);
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
var out="column["+this.pageColumnIndex+"]";
out+=" layoutCol="+this.layoutColumnIndex+" layoutId="+this.layoutId+" size="+this.size+(this.columnChildren==null?"":(" column-child-count="+this.columnChildren.length))+(this.columnContainer?" colContainer=true":"")+(this.layoutHeader?" layoutHeader=true":"");
if(this.domNode!=null){
var _1f5=dojo.html.getAbsolutePosition(this.domNode,true);
var _1f6=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_1f5.x)+", right:"+(_1f5.x+_1f6.width)+", top:"+(_1f5.y)+", bottom:"+(_1f5.y+_1f6.height)+"}";
}
return out;
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
jetspeed.om.Portlet=function(_1f7,_1f8,_1f9,_1fa,_1fb,_1fc,_1fd,_1fe,_1ff){
this.name=_1f7;
this.entityId=_1f8;
if(_1fa){
this.properties=_1fa;
}else{
this.properties={};
}
if(_1fb){
this.actions=_1fb;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_1fc;
this.currentActionMode=_1fd;
if(_1f9){
this.contentRetriever=_1f9;
}
if(_1fe!=null&&_1fe.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_1fe)!=-1){
this.properties[jetspeed.id.PP_WINDOW_DECORATION]=_1fe;
}
}
this.layoutActionsDisabled=false;
if(typeof _1ff!="undefined"){
this.layoutActionsDisabled=_1ff;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _200=jetspeed;
var jsId=_200.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _202=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_200.prefs.windowTiling){
if(_202=="true"){
_202=true;
}else{
if(_202=="false"){
_202=false;
}else{
if(_202!=true&&_202!=false){
_202=true;
}
}
}
}else{
_202=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_202;
var _203=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_203=="true"){
_203=true;
}else{
if(_202=="false"){
_203=false;
}else{
if(_203!=true&&_203!=false){
_203=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_203;
var _204=this.properties[jsId.PP_WINDOW_TITLE];
if(!_204&&this.name){
var re=(/^[^:]*:*/);
_204=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_204;
}
},postParseAnnotateHtml:function(_206){
var _207=jetspeed;
var _208=_207.portleturl;
if(_206){
var _209=_206;
var _20a=_209.getElementsByTagName("form");
var _20b=_207.debug.postParseAnnotateHtml;
var _20c=_207.debug.postParseAnnotateHtmlDisableAnchors;
if(_20a){
for(var i=0;i<_20a.length;i++){
var _20e=_20a[i];
var _20f=_20e.action;
var _210=_208.parseContentUrl(_20f);
var _211=_210.operation;
if(_211==_208.PORTLET_REQUEST_ACTION||_211==_208.PORTLET_REQUEST_RENDER){
var _212=_208.genPseudoUrl(_210,true);
_20e.action=_212;
var _213=new _207.om.ActionRenderFormBind(_20e,_210.url,_210.portletEntityId,_211);
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_211+") for form with action: "+_20f);
}
}else{
if(_20f==null||_20f.length==0){
var _213=new _207.om.ActionRenderFormBind(_20e,null,this.entityId,null);
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_20f);
}
}
}
}
}
var _214=_209.getElementsByTagName("a");
if(_214){
for(var i=0;i<_214.length;i++){
var _215=_214[i];
var _216=_215.href;
var _210=_208.parseContentUrl(_216);
var _217=null;
if(!_20c){
_217=_208.genPseudoUrl(_210);
}
if(!_217){
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_216);
}
}else{
if(_217==_216){
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_216);
}
}else{
if(_20b){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_216+" with: "+_217);
}
_215.href=_217;
}
}
}
}
}
},getPWin:function(){
var _218=jetspeed;
var _219=this.properties[_218.id.PP_WIDGET_ID];
if(_219){
return _218.page.getPWin(_219);
}
return null;
},getCurWinState:function(_21a){
var _21b=this.getPWin();
if(!_21b){
return null;
}
var _21c=_21b.getCurWinStateForPersist(_21a);
if(!_21a){
if(_21c.layout==null){
_21c.layout=this.lastSavedWindowState.layout;
}
}
return _21c;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_21d,_21e){
var _21f=jetspeed;
var jsId=_21f.id;
if(!_21d){
_21d={};
}
var _221=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _222=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_21d[jsId.PP_WINDOW_POSITION_STATIC]=_221;
_21d[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_222;
var _223=this.properties["width"];
if(!_21e&&_223!=null&&_223>0){
_21d.width=Math.floor(_223);
}else{
if(_21e){
_21d.width=-1;
}
}
var _224=this.properties["height"];
if(!_21e&&_224!=null&&_224>0){
_21d.height=Math.floor(_224);
}else{
if(_21e){
_21d.height=-1;
}
}
if(!_221||!_21f.prefs.windowTiling){
var _225=this.properties["x"];
if(!_21e&&_225!=null&&_225>=0){
_21d.left=Math.floor(((_225>0)?_225:0));
}else{
if(_21e){
_21d.left=-1;
}
}
var _226=this.properties["y"];
if(!_21e&&_226!=null&&_226>=0){
_21d.top=Math.floor(((_226>0)?_226:0));
}else{
_21d.top=-1;
}
var _227=this._getInitialZIndex(_21e);
if(_227!=null){
_21d.zIndex=_227;
}
}
return _21d;
},_initWinState:function(_228,_229){
var _22a=jetspeed;
var _22b=(_228?_228:{});
this.getInitialWinDims(_22b,_229);
if(_22a.debug.initWinState){
var _22c=this.properties[_22a.id.PP_WINDOW_POSITION_STATIC];
if(!_22c||!_22a.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_22b.zIndex+" x="+_22b.left+" y="+_22b.top+" width="+_22b.width+" height="+_22b.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_22b.column+" row="+_22b.row+" width="+_22b.width+" height="+_22b.height);
}
}
this.lastSavedWindowState=_22b;
return _22b;
},_getInitialZIndex:function(_22d){
var _22e=null;
var _22f=this.properties["z"];
if(!_22d&&_22f!=null&&_22f>=0){
_22e=Math.floor(_22f);
}else{
if(_22d){
_22e=-1;
}
}
return _22e;
},_getChangedWindowState:function(_230){
var jsId=jetspeed.id;
var _232=this.getSavedWinState();
if(_232&&dojo.lang.isEmpty(_232)){
_232=null;
_230=false;
}
var _233=this.getCurWinState(_230);
var _234=_233[jsId.PP_WINDOW_POSITION_STATIC];
var _235=!_234;
if(!_232){
var _236={state:_233,positionChanged:true,extendedPropChanged:true};
if(_235){
_236.zIndexChanged=true;
}
return _236;
}
var _237=false;
var _238=false;
var _239=false;
var _23a=false;
for(var _23b in _233){
if(_233[_23b]!=_232[_23b]){
if(_23b==jsId.PP_WINDOW_POSITION_STATIC||_23b==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_237=true;
_239=true;
_238=true;
}else{
if(_23b=="zIndex"){
if(_235){
_237=true;
_23a=true;
}
}else{
_237=true;
_238=true;
}
}
}
}
if(_237){
var _236={state:_233,positionChanged:_238,extendedPropChanged:_239};
if(_235){
_236.zIndexChanged=_23a;
}
return _236;
}
return null;
},getPortletUrl:function(_23c){
var _23d=jetspeed;
var _23e=_23d.url;
var _23f=null;
if(_23c&&_23c.url){
_23f=_23c.url;
}else{
if(_23c&&_23c.formNode){
var _240=_23c.formNode.getAttribute("action");
if(_240){
_23f=_240;
}
}
}
if(_23f==null){
_23f=_23e.basePortalUrl()+_23e.path.PORTLET+_23d.page.getPath();
}
if(!_23c.dontAddQueryArgs){
_23f=_23e.parse(_23f);
_23f=_23e.addQueryParameter(_23f,"entity",this.entityId,true);
_23f=_23e.addQueryParameter(_23f,"portlet",this.name,true);
_23f=_23e.addQueryParameter(_23f,"encoder","desktop",true);
if(_23c.jsPageUrl!=null){
var _241=_23c.jsPageUrl.query;
if(_241!=null&&_241.length>0){
_23f=_23f.toString()+"&"+_241;
}
}
}
if(_23c){
_23c.url=_23f.toString();
}
return _23f;
},_submitAjaxApi:function(_242,_243,_244){
var _245=jetspeed;
var _246="?action="+_242+"&id="+this.entityId+_243;
var _247=_245.url.basePortalUrl()+_245.url.path.AJAX_API+_245.page.getPath()+_246;
var _248="text/xml";
var _249=new _245.om.Id(_242,this.entityId);
_249.portlet=this;
_245.url.retrieveContent({url:_247,mimetype:_248},_244,_249,null);
},submitWinState:function(_24a,_24b){
var _24c=jetspeed;
var jsId=_24c.id;
var _24e=null;
if(_24b){
_24e={state:this._initWinState(null,true)};
}else{
_24e=this._getChangedWindowState(_24a);
}
if(_24e){
var _24f=_24e.state;
var _250=_24f[jsId.PP_WINDOW_POSITION_STATIC];
var _251=_24f[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _252=null;
if(_24e.extendedPropChanged){
var _253=jsId.PP_PROP_SEPARATOR;
var _254=jsId.PP_PAIR_SEPARATOR;
_252=jsId.PP_STATICPOS+_253+_250.toString();
_252+=_254+jsId.PP_FITHEIGHT+_253+_251.toString();
_252=escape(_252);
}
var _255="";
var _256=null;
if(_250){
_256="moveabs";
if(_24f.column!=null){
_255+="&col="+_24f.column;
}
if(_24f.row!=null){
_255+="&row="+_24f.row;
}
if(_24f.layout!=null){
_255+="&layoutid="+_24f.layout;
}
if(_24f.height!=null){
_255+="&height="+_24f.height;
}
}else{
_256="move";
if(_24f.zIndex!=null){
_255+="&z="+_24f.zIndex;
}
if(_24f.width!=null){
_255+="&width="+_24f.width;
}
if(_24f.height!=null){
_255+="&height="+_24f.height;
}
if(_24f.left!=null){
_255+="&x="+_24f.left;
}
if(_24f.top!=null){
_255+="&y="+_24f.top;
}
}
if(_252!=null){
_255+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_252;
}
this._submitAjaxApi(_256,_255,new _24c.om.MoveApiCL(this,_24f));
if(!_24a&&!_24b){
if(!_250&&_24e.zIndexChanged){
var _257=_24c.page.getPortletArray();
if(_257&&(_257.length-1)>0){
for(var i=0;i<_257.length;i++){
var _259=_257[i];
if(_259&&_259.entityId!=this.entityId){
if(!_259.properties[_24c.id.PP_WINDOW_POSITION_STATIC]){
_259.submitWinState(true);
}
}
}
}
}else{
if(_250){
}
}
}
}
},retrieveContent:function(_25a,_25b,_25c){
if(_25a==null){
_25a=new jetspeed.om.PortletCL(this,_25c,_25b);
}
if(!_25b){
_25b={};
}
var _25d=this;
_25d.getPortletUrl(_25b);
this.contentRetriever.getContent(_25b,_25a,_25d,jetspeed.debugContentDumpIds);
},setPortletContent:function(_25e,_25f,_260){
var _261=this.getPWin();
if(_260!=null&&_260.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_260;
if(_261&&!this.loadingIndicatorIsShown()){
_261.setPortletTitle(_260);
}
}
if(_261){
_261.setPortletContent(_25e,_25f);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _263=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _264=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _265=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _266=this.getPWin();
if(_266&&(_263||_264)){
var _267=_266.getPortletTitle();
if(_267&&(_267==_263||_267==_264)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_268){
var _269=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_269=jetspeed.prefs.desktopActionLabels[_268];
if(_269!=null&&_269.length==0){
_269=null;
}
}
return _269;
},loadingIndicatorShow:function(_26a){
if(_26a&&!this.loadingIndicatorIsShown()){
var _26b=this._getLoadingActionLabel(_26a);
var _26c=this.getPWin();
if(_26c&&_26b){
_26c.setPortletTitle(_26b);
}
}
},loadingIndicatorHide:function(){
var _26d=this.getPWin();
if(_26d){
_26d.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_26f,_270){
var _271=jetspeed;
var _272=_271.url;
var _273=null;
if(_26f!=null){
_273=this.getAction(_26f);
}
var _274=_270;
if(_274==null&&_273!=null){
_274=_273.url;
}
if(_274==null){
return;
}
var _275=_272.basePortalUrl()+_272.path.PORTLET+"/"+_274+_271.page.getPath();
if(_26f!=_271.id.ACT_PRINT){
this.retrieveContent(null,{url:_275});
}else{
var _276=_271.page.getPageUrl();
_276=_272.addQueryParameter(_276,"jsprintmode","true");
_276=_272.addQueryParameter(_276,"jsaction",escape(_273.url));
_276=_272.addQueryParameter(_276,"jsentity",this.entityId);
_276=_272.addQueryParameter(_276,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_276.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_278,_279,_27a){
if(_278){
this.actions=_278;
}else{
this.actions={};
}
this.currentActionState=_279;
this.currentActionMode=_27a;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _27b=this.getPWin();
if(_27b){
_27b.windowActionButtonSync();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_27e,_27f){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_27e;
this.submitOperation=_27f;
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
},eventConfMgr:function(_282){
var fn=(_282)?"disconnect":"connect";
var _284=dojo.event;
var form=this.form;
_284[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_284[fn]("after",node,"onclick",this,"click",null);
}
}
var _288=form.getElementsByTagName("input");
for(var i=0;i<_288.length;i++){
var _289=_288[i];
if(_289.type.toLowerCase()=="image"&&_289.form==form){
_284[fn]("after",_289,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_284[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_28b){
var _28c=true;
if(this.isFormSubmitInProgress()){
_28c=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_28c=false;
}
}
}
return _28c;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _28e=jetspeed.portleturl.parseContentUrl(this.form.action);
var _28f={};
if(_28e.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_28e.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _290=jetspeed.portleturl.genPseudoUrl(_28e,true);
this.form.action=_290;
this.submitOperation=_28e.operation;
this.entityId=_28e.portletEntityId;
_28f.url=_28e.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_28f.formFilter=dojo.lang.hitch(this,"formFilter");
_28f.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_28f),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_28f),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_291){
if(_291!=undefined){
this.formSubmitInProgress=_291;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_292,_293,_294){
this.portlet=_292;
this.suppressGetActions=_293;
this.formbind=null;
if(_294!=null&&_294.submitFormBindObject!=null){
this.formbind=_294.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_295){
if(this.portlet==null){
return;
}
if(_295){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_296,_297,_298,http){
var _29a=null;
if(http!=null){
_29a=http.getResponseHeader("JS_PORTLET_TITLE");
if(_29a!=null){
_29a=unescape(_29a);
}
}
_298.setPortletContent(_296,_297,_29a);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_298.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_29c,_29d,_29e){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_29d+" type: "+type+jetspeed.url.formatBindError(_29c));
}};
jetspeed.om.PortletActionCL=function(_29f,_2a0){
this.portlet=_29f;
this.formbind=null;
if(_2a0!=null&&_2a0.submitFormBindObject!=null){
this.formbind=_2a0.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_2a1){
if(this.portlet==null){
return;
}
if(_2a1){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2a2,_2a3,_2a4,http){
var _2a6=jetspeed;
var _2a7=null;
var _2a8=false;
var _2a9=_2a6.portleturl.parseContentUrl(_2a2);
if(_2a9.operation==_2a6.portleturl.PORTLET_REQUEST_ACTION||_2a9.operation==_2a6.portleturl.PORTLET_REQUEST_RENDER){
if(_2a6.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_2a9.operation+"-url in response body: "+_2a2+"  url: "+_2a9.url+" entity-id: "+_2a9.portletEntityId);
}
_2a7=_2a9.url;
}else{
if(_2a6.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_2a2);
}
_2a7=_2a2;
if(_2a7){
var _2aa=_2a7.indexOf(_2a6.url.basePortalUrl()+_2a6.url.path.PORTLET);
if(_2aa==-1){
_2a8=true;
window.location.href=_2a7;
_2a7=null;
}else{
if(_2aa>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_2a2);
_2a7=null;
}
}
}
}
if(_2a7!=null){
if(_2a6.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_2a7);
}
var _2ab=new jetspeed.PortletRenderer(false,false,false,_2a7,true);
_2ab.renderAll();
}else{
this._loading(false);
}
if(!_2a8&&this.portlet){
_2a6.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2ad,_2ae,_2af){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.url.formatBindError(_2ad));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2b0=this.getUrl();
if(_2b0){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2b0,this.getTarget());
}else{
jetspeed.updatePage(_2b0);
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
jetspeed.om.Menu=function(_2b1,_2b2){
this._is_parsed=false;
this.name=_2b1;
this.type=_2b2;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2b3){
if(!_2b3){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2b3);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2b5){
if(!this.hasOptions()){
return null;
}
if(_2b5==0||_2b5>0){
if(_2b5>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_2b5];
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
var _2b7=this.options[i];
if(_2b7 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_2b9,_2ba){
var _2bb=this.parseMenu(data,_2ba.menuName,_2ba.menuType);
_2ba.page.putMenu(_2bb);
},notifyFailure:function(type,_2bd,_2be,_2bf){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_2bf.toString()+"] url: "+_2be+" type: "+type+jetspeed.url.formatBindError(_2bd));
},parseMenu:function(node,_2c1,_2c2){
var menu=null;
var _2c4=node.getElementsByTagName("js");
if(!_2c4||_2c4.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _2c5=_2c4[0].childNodes;
for(var i=0;i<_2c5.length;i++){
var _2c7=_2c5[i];
if(_2c7.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2c8=_2c7.nodeName;
if(_2c8=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_2c7,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2c1;
}
if(menu.type==null){
menu.type=_2c2;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2cb=null;
var _2cc=node.childNodes;
for(var i=0;i<_2cc.length;i++){
var _2ce=_2cc[i];
if(_2ce.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2cf=_2ce.nodeName;
if(_2cf=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_2ce,new jetspeed.om.Menu()));
}
}else{
if(_2cf=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_2ce,new jetspeed.om.MenuOption()));
}
}else{
if(_2cf=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2ce,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2cf){
mObj[_2cf]=((_2ce&&_2ce.firstChild)?_2ce.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_2d0,_2d1,_2d2){
this.includeMenuDefs=_2d0;
this.initiateEditMode=_2d1;
this.isPageUpdate=_2d2;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_2d4,_2d5){
var _2d6=this.getMenuDefs(data,_2d4,_2d5);
for(var i=0;i<_2d6.length;i++){
var mObj=_2d6[i];
_2d5.page.putMenu(mObj);
}
this.notifyFinished(_2d5);
},getMenuDefs:function(data,_2da,_2db){
var _2dc=[];
var _2dd=data.getElementsByTagName("menu");
for(var i=0;i<_2dd.length;i++){
var _2df=_2dd[i].getAttribute("type");
if(this.includeMenuDefs){
_2dc.push(this.parseMenuObject(_2dd[i],new jetspeed.om.Menu(null,_2df)));
}else{
var _2e0=_2dd[i].firstChild.nodeValue;
_2dc.push(new jetspeed.om.Menu(_2e0,_2df));
}
}
return _2dc;
},notifyFailure:function(type,_2e2,_2e3,_2e4){
dojo.raise("MenusApiCL error ["+_2e4.toString()+"] url: "+_2e3+" type: "+type+jetspeed.url.formatBindError(_2e2));
},notifyFinished:function(_2e5){
var _2e6=jetspeed;
if(this.includeMenuDefs){
_2e6.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
_2e6.editPageInitiate();
}
if(this.isPageUpdate){
_2e6.updatePageEnd();
}
if(djConfig.isDebug&&_2e6.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_2e7){
this.portletEntityId=_2e7;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_2e9,_2ea){
if(jetspeed.url.checkAjaxApiResponse(_2e9,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_2eb){
var _2ec=jetspeed.page.getPortlet(this.portletEntityId);
if(_2ec){
if(_2eb){
_2ec.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_2ec.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_2ee,_2ef,_2f0){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_2f0.toString()+"] url: "+_2ef+" type: "+type+jetspeed.url.formatBindError(_2ee));
}});
jetspeed.om.PageChangeActionCL=function(_2f1){
this.pageActionUrl=_2f1;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_2f3,_2f4){
if(jetspeed.url.checkAjaxApiResponse(_2f3,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_2f6,_2f7,_2f8){
dojo.raise("PageChangeActionCL error ["+_2f8.toString()+"] url: "+_2f7+" type: "+type+jetspeed.url.formatBindError(_2f6));
}});
jetspeed.om.PortletActionsCL=function(_2f9){
this.portletEntityIds=_2f9;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_2fa){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _2fc=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_2fc){
if(_2fa){
_2fc.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_2fc.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_2fe,_2ff){
this._loading(false);
if(jetspeed.url.checkAjaxApiResponse(_2fe,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _301=this.parsePortletActionsResponse(node);
for(var i=0;i<_301.length;i++){
var _303=_301[i];
var _304=_303.id;
var _305=jetspeed.page.getPortlet(_304);
if(_305!=null){
_305.updateActions(_303.actions,_303.currentActionState,_303.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _307=new Array();
var _308=node.getElementsByTagName("js");
if(!_308||_308.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _307;
}
var _309=_308[0].childNodes;
for(var i=0;i<_309.length;i++){
var _30b=_309[i];
if(_30b.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _30c=_30b.nodeName;
if(_30c=="portlets"){
var _30d=_30b;
var _30e=_30d.childNodes;
for(var pI=0;pI<_30e.length;pI++){
var _310=_30e[pI];
if(_310.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _311=_310.nodeName;
if(_311=="portlet"){
var _312=this.parsePortletElement(_310);
if(_312!=null){
_307.push(_312);
}
}
}
}
}
return _307;
},parsePortletElement:function(node){
var _314=node.getAttribute("id");
if(_314!=null){
var _315=jetspeed.page._parsePSMLActions(node,null);
var _316=jetspeed.page._parsePSMLActionState(node);
var _317=jetspeed.page._parsePSMLActionMode(node);
return {id:_314,actions:_315,currentActionState:_316,currentActionMode:_317};
}
return null;
},notifyFailure:function(type,_319,_31a,_31b){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_31b.toString()+"] url: "+_31a+" type: "+type+jetspeed.url.formatBindError(_319));
}});
jetspeed.om.MoveApiCL=function(_31c,_31d){
this.portlet=_31c;
this.changedState=_31d;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_31e){
if(this.portlet==null){
return;
}
if(_31e){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_320,_321){
this._loading(false);
dojo.lang.mixin(_321.portlet.lastSavedWindowState,this.changedState);
var _322=false;
if(djConfig.isDebug&&jetspeed.debug.submitWinState){
_322=true;
}
jetspeed.url.checkAjaxApiResponse(_320,data,_322,("move-portlet ["+_321.portlet.entityId+"]"),jetspeed.debug.submitWinState);
},notifyFailure:function(type,_324,_325,_326){
this._loading(false);
dojo.debug("submitWinState error ["+_326.entityId+"] url: "+_325+" type: "+type+jetspeed.url.formatBindError(_324));
}};
jetspeed.ui={initCssObj:function(){
var _327=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _328=_327.concat([" height: ","","",";"]);
var _329=_328.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _32a=_329.concat([" position: ","static",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_327,cssHeight:_328,cssOverflow:_329,cssPosition:_32a,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinChildren:function(_32b,_32c,_32d,_32e){
if(_32d||_32e){
_32d=true;
}
var djH=dojo.html;
var jsId=jetspeed.id;
var _331=null;
var _332=-1;
if(_32b){
_331=[];
var _333=_32b.childNodes;
if(_333!=null&&_333.length>0){
for(var i=0;i<_333.length;i++){
var _335=_333[i];
if((!_32e&&djH.hasClass(_335,jsId.PWIN_CLASS))||(_32d&&djH.hasClass(_335,jsId.PWIN_GHOST_CLASS))){
_331.push(_335);
if(_32c&&_335==_32c){
_332=_331.length-1;
}
}else{
if(_32c&&_335==_32c){
_331.push(_335);
_332=_331.length-1;
}
}
}
}
}
return {portletWindowNodes:_331,matchIndex:_332};
},getPWinsFromNodes:function(_336){
var _337=jetspeed.page;
var _338=null;
if(_336){
_338=new Array();
for(var i=0;i<_336.length;i++){
var _33a=_337.getPWin(_336[i].id);
if(_33a){
_338.push(_33a);
}
}
}
return _338;
},createPortletWindow:function(_33b,_33c,_33d){
var _33e=false;
if(djConfig.isDebug&&_33d.debug.profile){
_33e=true;
dojo.profile.start("createPortletWindow");
}
var _33f=(_33c!=null);
var _340=false;
var _341=null;
if(_33f&&_33c<_33d.page.columns.length&&_33c>=0){
_341=_33d.page.columns[_33c].domNode;
}
if(_341==null){
_340=true;
_341=document.getElementById(_33d.id.DESKTOP);
}
if(_341==null){
return;
}
var _342={};
if(_33b.isPortlet){
_342.portlet=_33b;
if(_33d.prefs.printModeOnly!=null){
_342.printMode=true;
}
if(_340){
_33b.properties[_33d.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_33d.widget.PortletWindow.prototype.altInitParamsDef(_342,_33b);
if(_340){
pwP.altInitParams[_33d.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _344=new _33d.widget.PortletWindow();
_344.build(_342,_341);
if(_33e){
dojo.profile.end("createPortletWindow");
}
},evtConnect:function(_345,_346,_347,_348,_349,_34a,rate){
if(!rate){
rate=0;
}
var _34c={adviceType:_345,srcObj:_346,srcFunc:_347,adviceObj:_348,adviceFunc:_349,rate:rate};
if(_34a==null){
_34a=dojo.event;
}
_34a.connect(_34c);
return _34c;
},evtDisconnect:function(_34d,_34e,_34f,_350,_351,_352){
if(_352==null){
_352=dojo.event;
}
_352.disconnect({adviceType:_34d,srcObj:_34e,srcFunc:_34f,adviceObj:_350,adviceFunc:_351});
},evtDisconnectWObj:function(_353,_354){
if(_354==null){
_354=dojo.event;
}
_354.disconnect(_353);
},evtDisconnectWObjAry:function(_355,_356){
if(_355&&_355.length>0){
if(_356==null){
_356=dojo.event;
}
for(var i=0;i<_355.length;i++){
_356.disconnect(_355[i]);
}
}
}};

