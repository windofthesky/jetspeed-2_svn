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
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",P_CLASS:"portlet",PWIN_CLASS:"portletWindow",PWIN_CLIENT_CLASS:"portletWindowClient",PWIN_GHOST_CLASS:"ghostPane",PW_ID_PREFIX:"pw_",COL_CLASS:"desktopColumn",COL_LAYOUTHEADER_CLASS:"desktopLayoutHeader",PP_WIDGET_ID:"widgetId",PP_CONTENT_RETRIEVER:"contentRetriever",PP_DESKTOP_EXTENDED:"jsdesktop",PP_WINDOW_POSITION_STATIC:"windowPositionStatic",PP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PP_WINDOW_DECORATION:"windowDecoration",PP_WINDOW_TITLE:"title",PP_WINDOW_ICON:"windowIcon",PP_WIDTH:"width",PP_HEIGHT:"height",PP_LEFT:"left",PP_TOP:"top",PP_COLUMN:"column",PP_ROW:"row",PP_EXCLUDE_PCONTENT:"excludePContent",PP_WINDOW_STATE:"windowState",PP_STATICPOS:"staticpos",PP_FITHEIGHT:"fitheight",PP_PROP_SEPARATOR:"=",PP_PAIR_SEPARATOR:";",ACT_MENU:"menu",ACT_MINIMIZE:"minimized",ACT_MAXIMIZE:"maximized",ACT_RESTORE:"normal",ACT_PRINT:"print",ACT_EDIT:"edit",ACT_VIEW:"view",ACT_HELP:"help",ACT_ADDPORTLET:"addportlet",ACT_REMOVEPORTLET:"removeportlet",ACT_CHANGEPORTLETTHEME:"changeportlettheme",ACT_DESKTOP_TILE:"tile",ACT_DESKTOP_UNTILE:"untile",ACT_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACT_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACT_DESKTOP_MOVE_TILED:"movetiled",ACT_DESKTOP_MOVE_UNTILED:"moveuntiled",ACT_LOAD_RENDER:"loadportletrender",ACT_LOAD_ACTION:"loadportletaction",ACT_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",PG_ED_STATE_PARAM:"epst",PG_ED_TITLES_PARAM:"wintitles",PORTAL_ORIGINATE_PARAMETER:"portal",PM_P_AD:256,PM_P_D:1024,PM_MZ_P:2048,DEBUG_WINDOW_TAG:"js-db"};
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
if(_8.UAie6){
_8.ui.windowResizeMgr.init(window,_8.docBody);
}
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
this._jsObj=_40;
this.mkWins=_3a;
this.initEdit=_3f;
this.minimizeTemp=(_3f!=null&&_3f.editModeMove);
this.noRender=(this.minimizeTemp&&_3f.windowTitles!=null);
this.isPgLd=_3b;
this.isPgUp=_3c;
this.pageLoadUrl=null;
if(_3b){
this.pageLoadUrl=_40.url.parse(_41.getPageUrl());
}
this.renderUrl=_3d;
this.suppressGetActions=_3e;
this._colLen=_41.columns.length;
this._colIndex=0;
this._portletIndex=0;
this._renderCount=0;
this.psByCol=_41.portletsByPageColumn;
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
var _42=this._jsObj;
var _43=this.dbgMsg;
if(_43!=null){
if(this.dbgPgLd){
dojo.debug("portlet-renderer page-url: "+_42.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPgLd){
_42.page.loadPostRender(this.isPgUp,this.initEdit);
}
},_renderCurrent:function(){
var _44=this._jsObj;
var _45=this._colLen;
var _46=this._colIndex;
var _47=this._portletIndex;
if(_46<=_45){
var _48;
if(_46<_45){
_48=this.psByCol[_46.toString()];
}else{
_48=this.psByCol["z"];
_46=null;
}
var _49=(_48!=null?_48.length:0);
if(_49>0){
var _4a=_48[_47];
if(_4a){
var _4b=_4a.portlet;
var _4c=null;
if(this.mkWins){
_4c=_44.ui.createPortletWindow(_4b,_46,_44);
if(this.minimizeTemp){
_4c.minimizeWindowTemporarily(this.noRender);
}
}
var _4d=this.dbgMsg;
if(_4d!=null){
if(_4d.length>0){
_4d=_4d+", ";
}
var _4e=null;
if(_4b.getProperty!=null){
_4e=_4b.getProperty(_44.id.PP_WIDGET_ID);
}
if(!_4e){
_4e=_4b.widgetId;
}
if(!_4e){
_4e=_4b.toString();
}
if(_4b.entityId){
_4d=_4d+_4b.entityId+"("+_4e+")";
if(this._dbPgLd&&_4b.getProperty(_44.id.PP_WINDOW_TITLE)){
_4d=_4d+" "+_4b.getProperty(_44.id.PP_WINDOW_TITLE);
}
}else{
_4d=_4d+_4e;
}
}
if(!this.noRender){
_4b.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}else{
if(_4c&&_4c.portlet){
var _4f=this.initEdit.windowTitles[_4c.portlet.entityId];
if(_4f!=null){
_4c.setPortletTitle(_4f);
}
}
}
if((this._renderCount%3)==0){
_44.url.loadingIndicatorStep(_44);
}
this._renderCount++;
}
}
}
},_evalNext:function(){
var _50=false;
var _51=this._colLen;
var _52=this._colIndex;
var _53=this._portletIndex;
var _54=_52;
var _55;
for(++_52;_52<=_51;_52++){
_55=this.psByCol[_52==_51?"z":_52.toString()];
if(_53<(_55!=null?_55.length:0)){
_50=true;
this._colIndex=_52;
break;
}
}
if(!_50){
++_53;
for(_52=0;_52<=_54;_52++){
_55=this.psByCol[_52==_51?"z":_52.toString()];
if(_53<(_55!=null?_55.length:0)){
_50=true;
this._colIndex=_52;
this._portletIndex=_53;
break;
}
}
}
return _50;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_56){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _58=_56;
var _59=null;
if(_56&&_56.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_56.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_56&&_56.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_56.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_59=jetspeed.url.getQueryParameter(_56,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_58)){
_58=null;
}
return {url:_58,operation:op,portletEntityId:_59};
},genPseudoUrl:function(_5a,_5b){
if(!_5a||!_5a.url||!_5a.portletEntityId){
return null;
}
var _5c=null;
if(_5b){
_5c=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_5c="javascript:";
var _5d=false;
if(_5a.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_5c+="doAction(\"";
}else{
if(_5a.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_5c+="doRender(\"";
}else{
_5d=true;
}
}
if(_5d){
return null;
}
_5c+=_5a.url+"\",\""+_5a.portletEntityId+"\"";
_5c+=")";
}
return _5c;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_5e,_5f,_60){
var _61=null;
var _62=_5f.portletDecorationsConfig;
if(_5e&&_62){
_61=_62[_5e];
}
if(_61==null&&!_60){
var _63=_5f.portletDecorationsAllowed;
for(var i=0;i<_63.length;i++){
_5e=_63[i];
_61=_62[_5e];
if(_61!=null){
break;
}
}
}
if(_61!=null&&!_61._initialized){
var _65=jetspeed.prefs.getPortletDecorationBaseUrl(_5e);
_61._initialized=true;
_61.cssPathCommon=new dojo.uri.Uri(_65+"/css/styles.css");
_61.cssPathDesktop=new dojo.uri.Uri(_65+"/css/desktop.css");
dojo.html.insertCssFile(_61.cssPathCommon,null,true);
dojo.html.insertCssFile(_61.cssPathDesktop,null,true);
}
return _61;
};
jetspeed.loadPortletDecorationConfig=function(_66,_67,_68){
var _69={};
_67.portletDecorationsConfig[_66]=_69;
_69.name=_66;
_69.windowActionButtonOrder=_67.windowActionButtonOrder;
_69.windowActionNotPortlet=_67.windowActionNotPortlet;
_69.windowActionButtonMax=_67.windowActionButtonMax;
_69.windowActionButtonTooltip=_67.windowActionButtonTooltip;
_69.windowActionMenuOrder=_67.windowActionMenuOrder;
_69.windowActionNoImage=_67.windowActionNoImage;
_69.windowIconEnabled=_67.windowIconEnabled;
_69.windowIconPath=_67.windowIconPath;
_69.windowTitlebar=_67.windowTitlebar;
_69.windowResizebar=_67.windowResizebar;
_69.dNodeClass=_68.P_CLASS+" "+_66+" "+_68.PWIN_CLASS+" "+_68.PWIN_CLASS+"-"+_66;
_69.cNodeClass=_68.P_CLASS+" "+_66+" "+_68.PWIN_CLIENT_CLASS;
if(_67.portletDecorationsProperties){
var _6a=_67.portletDecorationsProperties[_66];
if(_6a){
for(var _6b in _6a){
_69[_6b]=_6a[_6b];
}
if(_6a.windowActionNoImage!=null){
var _6c={};
for(var i=0;i<_6a.windowActionNoImage.length;i++){
_6c[_6a.windowActionNoImage[i]]=true;
}
_69.windowActionNoImage=_6c;
}
if(_6a.windowIconPath!=null){
_69.windowIconPath=dojo.string.trim(_6a.windowIconPath);
if(_69.windowIconPath==null||_69.windowIconPath.length==0){
_69.windowIconPath=null;
}else{
var _6e=_69.windowIconPath;
var _6f=_6e.charAt(0);
if(_6f!="/"){
_6e="/"+_6e;
}
var _70=_6e.charAt(_6e.length-1);
if(_70!="/"){
_6e=_6e+"/";
}
_69.windowIconPath=_6e;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(_71,_72){
var _73=jetspeed;
_73.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _74=_73.page.getMenuNames();
for(var i=0;i<_74.length;i++){
var _76=_74[i];
var _77=dojo.widget.byId(_73.id.MENU_WIDGET_ID_PREFIX+_76);
if(_77){
_77.createJetspeedMenu(_73.page.getMenu(_76));
}
}
if(!_72){
_73.url.loadingIndicatorHide();
}
_73.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_78){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_78);
}
};
jetspeed.menuNavClickWidget=function(_79,_7a){
if(!_79){
return;
}
if(dojo.lang.isString(_79)){
var _7b=_79;
_79=dojo.widget.byId(_7b);
if(!_79){
dojo.raise("Tab widget not found: "+_7b);
}
}
if(_79){
var _7c=_79.jetspeedmenuname;
if(!_7c&&_79.extraArgs){
_7c=_79.extraArgs.jetspeedmenuname;
}
if(!_7c){
dojo.raise("Tab widget is invalid: "+_79.widgetId);
}
var _7d=jetspeed.page.getMenu(_7c);
if(!_7d){
dojo.raise("Tab widget "+_79.widgetId+" no menu: "+_7c);
}
var _7e=_7d.getOptionByIndex(_7a);
jetspeed.menuNavClick(_7e);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_7f,_80,_81){
var _82=jetspeed;
if(!_7f||_82.pageNavigateSuppress){
return;
}
if(typeof _81=="undefined"){
_81=false;
}
if(!_81&&_82.page&&_82.page.equalsPageUrl(_7f)){
return;
}
_7f=_82.page.makePageUrl(_7f);
if(_80=="top"){
top.location.href=_7f;
}else{
if(_80=="parent"){
parent.location.href=_7f;
}else{
window.location.href=_7f;
}
}
};
jetspeed.getActionsForPortlet=function(_83){
if(_83==null){
return;
}
jetspeed.getActionsForPortlets([_83]);
};
jetspeed.getActionsForPortlets=function(_84){
var _85=jetspeed;
if(_84==null){
_84=_85.page.getPortletIds();
}
var _86=new _85.om.PortletActionsCL(_84);
var _87="?action=getactions";
for(var i=0;i<_84.length;i++){
_87+="&id="+_84[i];
}
var _89=_85.url.basePortalUrl()+_85.url.path.AJAX_API+_85.page.getPath()+_87;
var _8a="text/xml";
var _8b=new _85.om.Id("getactions",{});
_85.url.retrieveContent({url:_89,mimetype:_8a},_86,_8b,_85.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_8c,_8d,_8e,_8f){
var _90=jetspeed;
if(_8c==null){
return;
}
if(_8f==null){
_8f=new _90.om.PortletChangeActionCL(_8c);
}
var _91="?action=window&id="+(_8c!=null?_8c:"");
if(_8d!=null){
_91+="&state="+_8d;
}
if(_8e!=null){
_91+="&mode="+_8e;
}
var _92=_90.url.basePortalUrl()+_90.url.path.AJAX_API+_90.page.getPath()+_91;
var _93="text/xml";
var _94=new _90.om.Id("changeaction",{});
_90.url.retrieveContent({url:_92,mimetype:_93},_8f,_94,_90.debugContentDumpIds);
};
jetspeed.getUserInfo=function(_95){
var _96=jetspeed;
var _97=new _96.om.UserInfoCL();
var _98="?action=getuserinfo";
var _99=_96.url.basePortalUrl()+_96.url.path.AJAX_API+_96.page.getPath()+_98;
var _9a="text/xml";
var _9b=new _96.om.Id("getuserinfo",{});
_96.url.retrieveContent({url:_99,mimetype:_9a,sync:_95},_97,_9b,_96.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_9c,_9d){
var _9e=_9c.page;
if(!_9e.editMode){
var _9f=_9c.css;
var _a0=true;
var _a1=_9c.url.getQueryParameter(window.location.href,_9c.id.PORTAL_ORIGINATE_PARAMETER);
if(_a1!=null&&_a1=="true"){
_a0=false;
}
_9e.editMode=true;
var _a2=dojo.widget.byId(_9c.id.PG_ED_WID);
if(_9c.UAie6){
_9e.displayAllPWins(true);
}
var _a3=((_9d!=null&&_9d.editModeMove)?true:false);
var _a4=_9e._perms(_9c.prefs,-1,String.fromCharCode);
if(_a4&&_a4[2]&&_a4[2].length>0){
if(!_9c.page._getU()){
_9c.getUserInfo(true);
}
}
if(_a2==null){
try{
_9c.url.loadingIndicatorShow("loadpageeditor",true);
_a2=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_9c.id.PG_ED_WID,editorInitiatedFromDesktop:_a0,editModeMove:_a3});
var _a5=document.getElementById(_9c.id.COLUMNS);
_a5.insertBefore(_a2.domNode,_a5.firstChild);
}
catch(e){
_9c.url.loadingIndicatorHide();
if(_9c.UAie6){
_9e.displayAllPWins();
}
}
}else{
_a2.editPageShow();
}
_9e.syncPageControls(_9c);
}
};
jetspeed.editPageTerminate=function(_a6){
var _a7=_a6.page;
if(_a7.editMode){
var _a8=_a6.css;
var _a9=dojo.widget.byId(_a6.id.PG_ED_WID);
_a9.editMoveModeExit(true);
_a7.editMode=false;
if(!_a9.editorInitiatedFromDesktop){
var _aa=_a7.getPageUrl(true);
_aa=_a6.url.removeQueryParameter(_aa,_a6.id.PG_ED_PARAM);
_aa=_a6.url.removeQueryParameter(_aa,_a6.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_aa;
}else{
var _ab=_a6.url.getQueryParameter(window.location.href,_a6.id.PG_ED_PARAM);
if(_ab!=null&&_ab=="true"){
var _ac=window.location.href;
_ac=_a6.url.removeQueryParameter(_ac,_a6.id.PG_ED_PARAM);
window.location.href=_ac;
}else{
if(_a9!=null){
_a9.editPageHide();
}
_a7.syncPageControls(_a6);
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_ad,_ae,_af,_b0){
if(!_ad){
_ad={};
}
jetspeed.url.retrieveContent(_ad,_ae,_af,_b0);
}};
jetspeed.om.PageCLCreateWidget=function(_b1,_b2){
if(typeof _b1=="undefined"){
_b1=false;
}
this.isPageUpdate=_b1;
this.initEditModeConf=_b2;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_b3,_b4,_b5){
_b5.loadFromPSML(_b3,this.isPageUpdate,this.initEditModeConf);
},notifyFailure:function(_b6,_b7,_b8,_b9){
dojo.raise("PageCLCreateWidget error url: "+_b8+" type: "+_b6+jetspeed.formatError(_b7));
}};
jetspeed.om.Page=function(_ba,_bb,_bc,_bd,_be){
if(_ba!=null&&_bb!=null){
this.requiredLayoutDecorator=_ba;
this.setPsmlPathFromDocumentUrl(_bb);
this.pageUrlFallback=_bb;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _bc!="undefined"){
this.addToHistory=_bc;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets={};
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_be!=null){
this.iframeCoverByWinId=_be;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_bd!=null){
this.tooltipMgr=_bd;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,uIA:true,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _bf=(this.name!=null&&this.name.length>0?this.name:null);
if(!_bf){
this.getPsmlUrl();
_bf=this.psmlPath;
}
return "page-"+_bf;
},setPsmlPathFromDocumentUrl:function(_c0){
var _c1=jetspeed;
var _c2=_c1.url.path.AJAX_API;
var _c3=null;
if(_c0==null){
_c3=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_c1.prefs.ajaxPageNavigation){
var _c4=window.location.hash;
if(_c4!=null&&_c4.length>0){
if(_c4.indexOf("#")==0){
_c4=(_c4.length>1?_c4.substring(1):"");
}
if(_c4!=null&&_c4.length>1&&_c4.indexOf("/")==0){
this.psmlPath=_c1.url.path.AJAX_API+_c4;
return;
}
}
}
}else{
var _c5=_c1.url.parse(_c0);
_c3=_c5.path;
}
var _c6=_c1.url.path.DESKTOP;
var _c7=_c3.indexOf(_c6);
if(_c7!=-1&&_c3.length>(_c7+_c6.length)){
_c2=_c2+_c3.substring(_c7+_c6.length);
}
this.psmlPath=_c2;
},getPsmlUrl:function(){
var _c8=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _c9=_c8.url.basePortalUrl()+this.psmlPath;
if(_c8.prefs.printModeOnly!=null){
_c9=_c8.url.addQueryParameter(_c9,"layoutid",_c8.prefs.printModeOnly.layout);
_c9=_c8.url.addQueryParameter(_c9,"entity",_c8.prefs.printModeOnly.entity).toString();
}
return _c9;
},_setU:function(u){
this._u=u;
},_getU:function(){
return this._u;
},retrievePsml:function(_cb){
var _cc=jetspeed;
if(_cb==null){
_cb=new _cc.om.PageCLCreateWidget();
}
var _cd=this.getPsmlUrl();
var _ce="text/xml";
if(_cc.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_cd);
}
_cc.url.retrieveContent({url:_cd,mimetype:_ce},_cb,this,_cc.debugContentDumpIds);
},loadFromPSML:function(_cf,_d0,_d1){
var _d2=jetspeed;
var _d3=_d2.prefs;
var _d4=dojo;
var _d5=_d3.printModeOnly;
if(djConfig.isDebug&&_d2.debug.profile&&_d5==null){
_d4.profile.start("loadFromPSML");
}
var _d6=this._parsePSML(_cf);
jetspeed.rootfrag=_d6;
if(_d6==null){
return;
}
this.portletsByPageColumn={};
var _d7={};
if(this.portletDecorator){
_d7[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_d6,0,null,this.portletsByPageColumn,true,_d7,_d4,_d2);
this.rootFragmentId=_d6.id;
this.editMode=false;
for(var _d8 in _d7){
_d2.loadPortletDecorationStyles(_d8,_d3,true);
}
if(_d3.windowTiling){
this._createColsStart(document.getElementById(_d2.id.DESKTOP),_d2.id.COLUMNS);
}
this.createLayoutInfo(_d2);
var _d9=this.portletsByPageColumn["z"];
if(_d9){
_d9.sort(this._loadPortletZIndexCompare);
}
if(typeof _d1=="undefined"){
_d1=null;
}
if(_d1!=null||(this.actions!=null&&this.actions[_d2.id.ACT_VIEW]!=null)){
if(!this.isUA()&&this.actions!=null&&(this.actions[_d2.id.ACT_EDIT]!=null||this.actions[_d2.id.ACT_VIEW]!=null)){
if(_d1==null){
_d1={};
}
if((typeof _d1.editModeMove=="undefined")&&this._perms(_d3,_d2.id.PM_MZ_P,String.fromCharCode)){
_d1.editModeMove=true;
}
var _da=_d2.url.parse(window.location.href);
if(!_d1.editModeMove){
var _db=_d2.url.getQueryParameter(_da,_d2.id.PG_ED_STATE_PARAM);
if(_db!=null){
_db="0x"+_db;
if((_db&_d2.id.PM_MZ_P)>0){
_d1.editModeMove=true;
}
}
}
if(_d1.editModeMove&&!_d1.windowTitles){
var _dc=_d2.url.getQueryParameter(_da,_d2.id.PG_ED_TITLES_PARAM);
if(_dc!=null){
var _dd=_dc.length;
var _de=new Array(_dd/2);
var _df=String.fromCharCode;
var _e0=0,chI=0;
while(chI<(_dd-1)){
_de[_e0]=_df(Number("0x"+_dc.substring(chI,(chI+2))));
_e0++;
chI+=2;
}
var _e2=null;
try{
_e2=eval("({"+_de.join("")+"})");
}
catch(e){
if(djConfig.isDebug){
dojo.debug("cannot parse json: "+_de.join(""));
}
}
if(_e2!=null){
var _e3=false;
for(var _e4 in this.portlets){
var _e5=this.portlets[_e4];
if(_e5!=null&&!_e2[_e5.entityId]){
_e3=true;
break;
}
}
if(!_e3){
_d1.windowTitles=_e2;
}
}
}
}
}else{
_d1=null;
}
}
if(_d1!=null){
_d2.url.loadingIndicatorShow("loadpageeditor",true);
}
var _e6=new _d2.PortletRenderer(true,true,_d0,null,true,_d1);
_e6.renderAllTimeDistribute();
},loadPostRender:function(_e7,_e8){
var _e9=jetspeed;
var _ea=_e9.prefs.printModeOnly;
if(_ea==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
this.retrieveMenuDeclarations(true,_e7,_e8);
}else{
for(var _eb in this.portlets){
var _ec=this.portlets[_eb];
if(_ec!=null){
_ec.renderAction(null,_ea.action);
}
break;
}
if(_e7){
_e9.updatePageEnd();
}
}
if(_e9.UAie6){
_e9.ui.evtConnect("after",window,"onresize",_e9.ui.windowResizeMgr,"onResize",dojo.event);
_e9.ui.windowResizeMgr.onResizeDelayedCompare();
}
var _ed,_ee=this.columns;
if(_ee){
for(var i=0;i<_ee.length;i++){
_ed=_ee[i].domNode;
if(!_ed.childNodes||_ed.childNodes.length==0){
_ed.style.height="1px";
}
}
}
var _f0=this.maximizedOnInit;
if(_f0!=null){
var _f1=this.getPWin(_f0);
if(_f1==null){
dojo.raise("Cannot identify window to maximize");
}else{
dojo.lang.setTimeout(_f1,_f1._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_e9.url,_e9.url.loadingIndicatorStepPreload,1800);
},loadPostRetrieveMenus:function(_f2,_f3){
var _f4=jetspeed;
this.renderPageControls(_f4);
if(_f3){
_f4.editPageInitiate(_f4,_f3);
}
if(_f2){
_f4.updatePageEnd();
}
this.syncPageControls(_f4);
},_parsePSML:function(_f5){
var _f6=jetspeed;
var _f7=dojo;
var _f8=_f5.getElementsByTagName("page");
if(!_f8||_f8.length>1||_f8[0]==null){
_f7.raise("Expected one <page> in PSML");
}
var _f9=_f8[0];
var _fa=_f9.childNodes;
var _fb=new RegExp("(name|path|profiledPath|title|short-title|uIA|npe)");
var _fc=null;
var _fd={};
for(var i=0;i<_fa.length;i++){
var _ff=_fa[i];
if(_ff.nodeType!=1){
continue;
}
var _100=_ff.nodeName;
if(_100=="fragment"){
_fc=_ff;
}else{
if(_100=="defaults"){
this.layoutDecorator=_ff.getAttribute("layout-decorator");
var _101=_ff.getAttribute("portlet-decorator");
var _102=_f6.prefs.portletDecorationsAllowed;
if(!_102||_f7.lang.indexOf(_102,_101)==-1){
_101=_f6.prefs.windowDecoration;
}
this.portletDecorator=_101;
}else{
if(_100&&_100.match(_fb)){
if(_100=="short-title"){
_100="shortTitle";
}
this[_100]=((_ff&&_ff.firstChild)?_ff.firstChild.nodeValue:null);
}else{
if(_100=="action"){
this._parsePSMLAction(_ff,_fd);
}
}
}
}
}
this.actions=_fd;
if(_fc==null){
_f7.raise("No root fragment in PSML");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_f6.debug.ajaxPageNav){
_f7.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_f6.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _103=this.getPageUrl();
_f7.undo.browser.addToHistory({back:function(){
if(_f6.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_103);
}
_f6.updatePage(_103,true);
},forward:function(){
if(_f6.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_103);
}
_f6.updatePage(_103,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_f6.prefs.ajaxPageNavigation){
var _103=this.getPageUrl();
_f7.undo.browser.setInitialState({back:function(){
if(_f6.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_103);
}
_f6.updatePage(_103,true);
},forward:function(){
if(_f6.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_103);
}
_f6.updatePage(_103,true);
},changeUrl:escape(this.getPath())});
}
}
var _104=this._parsePSMLFrag(_fc,0,false);
return _104;
},_parsePSMLFrag:function(_105,_106,_107){
var _108=jetspeed;
var _109=new Array();
var _10a=((_105!=null)?_105.getAttribute("type"):null);
if(_10a!="layout"){
dojo.raise("Expected layout fragment: "+_105);
return null;
}
if(!_107){
var _10b=_105.getAttribute("name");
if(_10b!=null){
_10b=_10b.toLowerCase();
if(_10b.indexOf("noactions")!=-1){
_107=true;
}
}
}
var _10c=null,_10d=0;
var _10e={};
var _10f=_105.childNodes;
var _110,_111,_112,_113,_114;
for(var i=0;i<_10f.length;i++){
_110=_10f[i];
if(_110.nodeType!=1){
continue;
}
_111=_110.nodeName;
if(_111=="fragment"){
_114=_110.getAttribute("type");
if(_114=="layout"){
var _116=this._parsePSMLFrag(_110,i,_107);
if(_116!=null){
_109.push(_116);
}
}else{
var _117=this._parsePSMLProps(_110,null);
var _118=_117[_108.id.PP_WINDOW_ICON];
if(_118==null||_118.length==0){
_118=this._parsePSMLChildOrAttr(_110,"icon");
if(_118!=null&&_118.length>0){
_117[_108.id.PP_WINDOW_ICON]=_118;
}
}
_109.push({id:_110.getAttribute("id"),type:_114,name:_110.getAttribute("name"),properties:_117,actions:this._parsePSMLActions(_110,null),currentActionState:this._parsePSMLChildOrAttr(_110,"state"),currentActionMode:this._parsePSMLChildOrAttr(_110,"mode"),decorator:_110.getAttribute("decorator"),layoutActionsDisabled:_107,documentOrderIndex:i});
}
}else{
if(_111=="property"){
if(this._parsePSMLProp(_110,_10e)=="sizes"){
if(_10c!=null){
dojo.raise("Layout fragment has multiple sizes definitions: "+_105);
return null;
}
if(_108.prefs.printModeOnly!=null){
_10c=["100"];
_10d=100;
}else{
_113=_110.getAttribute("value");
if(_113!=null&&_113.length>0){
_10c=_113.split(",");
for(var j=0;j<_10c.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_10c[j]=_10c[j].replace(re,"$1");
_10d+=new Number(_10c[j]);
}
}
}
}
}
}
}
if(_10c==null){
_10c=["100"];
_10d=100;
}
var _11b=_10c.length;
var _11c=_109.length;
var pCi=_108.id.PP_COLUMN;
var pRi=_108.id.PP_ROW;
var _11f=new Array(_11b);
var _120=new Array(_11b);
for(var cI=0;cI<_11b;cI++){
_11f[cI]=[];
_120[cI]={head:-1,tail:-1,high:-1};
}
for(var _122=0;_122<_11c;_122++){
var frag=_109[_122];
var _124=frag.properties;
var col=_124[pCi];
var row=_124[pRi];
var _127=null;
if(col==null||col>=_11b){
_127=_11b-1;
}else{
if(col<0){
_127=0;
}
}
if(_127!=null){
col=_124[pCi]=String(_127);
}
var ll=_11f[col];
var _129=ll.length;
var _12a=_120[col];
if(row<0){
row=_124[pRi]=0;
}else{
if(row==null){
row=_12a.high+1;
}
}
var _12b={i:_122,row:row,next:-1};
ll.push(_12b);
if(_129==0){
_12a.head=_12a.tail=0;
_12a.high=row;
}else{
if(row>_12a.high){
ll[_12a.tail].next=_129;
_12a.high=row;
_12a.tail=_129;
}else{
var _12c=_12a.head;
var _12d=-1;
while(ll[_12c].row<row){
_12d=_12c;
_12c=ll[_12c].next;
}
if(ll[_12c].row==row){
var _12e=new Number(row)+1;
ll[_12c].row=_12e;
if(_12a.tail==_12c){
_12a.high=_12e;
}
}
_12b.next=_12c;
if(_12d==-1){
_12a.head=_129;
}else{
ll[_12d].next=_129;
}
}
}
}
var _12f=new Array(_11c);
var _130=0;
for(var cI=0;cI<_11b;cI++){
var ll=_11f[cI];
var _12a=_120[cI];
var _131=0;
var _132=_12a.head;
while(_132!=-1){
var _12b=ll[_132];
var frag=_109[_12b.i];
_12f[_130]=frag;
frag.properties[pRi]=_131;
_130++;
_131++;
_132=_12b.next;
}
}
return {id:_105.getAttribute("id"),type:_10a,name:_105.getAttribute("name"),decorator:_105.getAttribute("decorator"),columnSizes:_10c,columnSizesSum:_10d,properties:_10e,fragments:_12f,layoutActionsDisabled:_107,documentOrderIndex:_106};
},_parsePSMLActions:function(_133,_134){
if(_134==null){
_134={};
}
var _135=_133.getElementsByTagName("action");
for(var _136=0;_136<_135.length;_136++){
var _137=_135[_136];
this._parsePSMLAction(_137,_134);
}
return _134;
},_parsePSMLAction:function(_138,_139){
var _13a=_138.getAttribute("id");
if(_13a!=null){
var _13b=_138.getAttribute("type");
var _13c=_138.getAttribute("name");
var _13d=_138.getAttribute("url");
var _13e=_138.getAttribute("alt");
_139[_13a.toLowerCase()]={id:_13a,type:_13b,label:_13c,url:_13d,alt:_13e};
}
},_parsePSMLChildOrAttr:function(_13f,_140){
var _141=null;
var _142=_13f.getElementsByTagName(_140);
if(_142!=null&&_142.length==1&&_142[0].firstChild!=null){
_141=_142[0].firstChild.nodeValue;
}
if(!_141){
_141=_13f.getAttribute(_140);
}
if(_141==null||_141.length==0){
_141=null;
}
return _141;
},_parsePSMLProps:function(_143,_144){
if(_144==null){
_144={};
}
var _145=_143.getElementsByTagName("property");
for(var _146=0;_146<_145.length;_146++){
this._parsePSMLProp(_145[_146],_144);
}
return _144;
},_parsePSMLProp:function(_147,_148){
var _149=_147.getAttribute("name");
var _14a=_147.getAttribute("value");
_148[_149]=_14a;
return _149;
},_layoutCreateModel:function(_14b,_14c,_14d,_14e,_14f,_150,_151,_152){
var jsId=_152.id;
var _154=this.columns.length;
var _155=this._layoutCreateColsModel(_14b,_14c,_14d,_14f);
var _156=_155.columnsInLayout;
if(_155.addedLayoutHeaderColumn){
_154++;
}
var _157=(_156==null?0:_156.length);
var _158=new Array(_157);
var _159=new Array(_157);
for(var i=0;i<_14b.fragments.length;i++){
var _15b=_14b.fragments[i];
if(_15b.type=="layout"){
var _15c=i;
var _15c=(_15b.properties?_15b.properties[_152.id.PP_COLUMN]:i);
if(_15c==null||_15c<0||_15c>=_157){
_15c=(_157>0?(_157-1):0);
}
_159[_15c]=true;
this._layoutCreateModel(_15b,(_14c+1),_156[_15c],_14e,false,_150,_151,_152);
}else{
this._layoutCreatePortlet(_15b,_14b,_156,_154,_14e,_158,_150,_151,_152);
}
}
return _156;
},_layoutCreatePortlet:function(_15d,_15e,_15f,_160,_161,_162,_163,_164,_165){
if(_15d&&_165.debugPortletEntityIdFilter){
if(!_164.lang.inArray(_165.debugPortletEntityIdFilter,_15d.id)){
_15d=null;
}
}
if(_15d){
var _166="z";
var _167=_15d.properties[_165.id.PP_DESKTOP_EXTENDED];
var _168=_165.prefs.windowTiling;
var _169=_168;
var _16a=_165.prefs.windowHeightExpand;
if(_167!=null&&_168&&_165.prefs.printModeOnly==null){
var _16b=_167.split(_165.id.PP_PAIR_SEPARATOR);
var _16c=null,_16d=0,_16e=null,_16f=null,_170=false;
if(_16b!=null&&_16b.length>0){
var _171=_165.id.PP_PROP_SEPARATOR;
for(var _172=0;_172<_16b.length;_172++){
_16c=_16b[_172];
_16d=((_16c!=null)?_16c.length:0);
if(_16d>0){
var _173=_16c.indexOf(_171);
if(_173>0&&_173<(_16d-1)){
_16e=_16c.substring(0,_173);
_16f=_16c.substring(_173+1);
_170=((_16f=="true")?true:false);
if(_16e==_165.id.PP_STATICPOS){
_169=_170;
}else{
if(_16e==_165.id.PP_FITHEIGHT){
_16a=_170;
}
}
}
}
}
}
}else{
if(!_168){
_169=false;
}
}
_15d.properties[_165.id.PP_WINDOW_POSITION_STATIC]=_169;
_15d.properties[_165.id.PP_WINDOW_HEIGHT_TO_FIT]=_16a;
if(_169&&_168){
var _174=_15f.length;
var _175=_15d.properties[_165.id.PP_COLUMN];
if(_175==null||_175>=_174){
_175=_174-1;
}else{
if(_175<0){
_175=0;
}
}
if(_162[_175]==null){
_162[_175]=new Array();
}
_162[_175].push(_15d.id);
var _176=_160+new Number(_175);
_166=_176.toString();
}
if(_15d.currentActionState==_165.id.ACT_MAXIMIZE){
this.maximizedOnInit=_15d.id;
}
var _177=_15d.decorator;
if(_177!=null&&_177.length>0){
if(_164.lang.indexOf(_165.prefs.portletDecorationsAllowed,_177)==-1){
_177=null;
}
}
if(_177==null||_177.length==0){
if(djConfig.isDebug&&_165.debug.windowDecorationRandom){
_177=_165.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_165.prefs.portletDecorationsAllowed.length)];
}else{
_177=this.portletDecorator;
}
}
var _178=_15d.properties||{};
_178[_165.id.PP_WINDOW_DECORATION]=_177;
_163[_177]=true;
var _179=_15d.actions||{};
var _17a=new _165.om.Portlet(_15d.name,_15d.id,null,_178,_179,_15d.currentActionState,_15d.currentActionMode,_15d.layoutActionsDisabled);
_17a.initialize();
this.putPortlet(_17a);
if(_161[_166]==null){
_161[_166]=new Array();
}
_161[_166].push({portlet:_17a,layout:_15e.id});
}
},_layoutCreateColsModel:function(_17b,_17c,_17d,_17e){
var _17f=jetspeed;
this.layouts[_17b.id]=_17b;
var _180=false;
var _181=new Array();
if(_17f.prefs.windowTiling&&_17b.columnSizes.length>0){
var _182=false;
if(_17f.UAie){
_182=true;
}
if(_17d!=null&&!_17e){
var _183=new _17f.om.Column(0,_17b.id,(_182?_17b.columnSizesSum-0.1:_17b.columnSizesSum),this.columns.length,_17b.layoutActionsDisabled,_17c);
_183.layoutHeader=true;
this.columns.push(_183);
if(_17d.buildColChildren==null){
_17d.buildColChildren=new Array();
}
_17d.buildColChildren.push(_183);
_17d=_183;
_180=true;
}
for(var i=0;i<_17b.columnSizes.length;i++){
var size=_17b.columnSizes[i];
if(_182&&i==(_17b.columnSizes.length-1)){
size=size-0.1;
}
var _186=new _17f.om.Column(i,_17b.id,size,this.columns.length,_17b.layoutActionsDisabled);
this.columns.push(_186);
if(_17d!=null){
if(_17d.buildColChildren==null){
_17d.buildColChildren=new Array();
}
_17d.buildColChildren.push(_186);
}
_181.push(_186);
}
}
return {columnsInLayout:_181,addedLayoutHeaderColumn:_180};
},_portletsInitWinState:function(_187){
var _188={};
this.getPortletCurColRow(null,false,_188);
for(var _189 in this.portlets){
var _18a=this.portlets[_189];
var _18b=_188[_18a.getId()];
if(_18b==null&&_187){
for(var i=0;i<_187.length;i++){
if(_187[i].portlet.getId()==_18a.getId()){
_18b={layout:_187[i].layout};
break;
}
}
}
if(_18b!=null){
_18a._initWinState(_18b,false);
}else{
dojo.raise("Window state data not found for portlet: "+_18a.getId());
}
}
},_loadPortletZIndexCompare:function(_18d,_18e){
var _18f=null;
var _190=null;
var _191=null;
_18f=_18d.portlet._getInitialZIndex();
_190=_18e.portlet._getInitialZIndex();
if(_18f&&!_190){
return -1;
}else{
if(_190&&!_18f){
return 1;
}else{
if(_18f==_190){
return 0;
}
}
}
return (_18f-_190);
},_createColsStart:function(_192,_193){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _194=document.createElement("div");
_194.id=_193;
_194.setAttribute("id",_193);
for(var _195=0;_195<this.columnsStructure.length;_195++){
var _196=this.columnsStructure[_195];
this._createCols(_196,_194);
}
_192.appendChild(_194);
},_createCols:function(_197,_198){
_197.createColumn();
if(this.colFirstNormI==-1&&!_197.columnContainer&&!_197.layoutHeader){
this.colFirstNormI=_197.getPageColumnIndex();
}
var _199=_197.buildColChildren;
if(_199!=null&&_199.length>0){
for(var _19a=0;_19a<_199.length;_19a++){
this._createCols(_199[_19a],_197.domNode);
}
}
delete _197.buildColChildren;
_198.appendChild(_197.domNode);
},_removeCols:function(_19b){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_19b){
var _19d=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_19d,function(_19e){
_19b.appendChild(_19e);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _19f=dojo.byId(jetspeed.id.COLUMNS);
if(_19f){
dojo.dom.removeNode(_19f);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnsEmptyCheck:function(_1a0){
var _1a1=null;
if(_1a0==null){
return _1a1;
}
var _1a2=_1a0.childNodes,_1a3;
if(_1a2){
for(var i=0;i<_1a2.length;i++){
_1a3=_1a2[i];
var _1a5=this.columnEmptyCheck(_1a3,true);
if(_1a5!=null){
_1a1=_1a5;
if(_1a1==false){
break;
}
}
}
}
return _1a1;
},columnEmptyCheck:function(_1a6,_1a7){
var _1a8=null;
if(!_1a6||!_1a6.getAttribute){
return _1a8;
}
var _1a9=_1a6.getAttribute("columnindex");
if(!_1a9||_1a9.length==0){
return _1a8;
}
var _1aa=_1a6.getAttribute("layoutid");
if(_1aa==null||_1aa.length==0){
var _1ab=_1a6.childNodes;
_1a8=(!_1ab||_1ab.length==0);
if(!_1a7){
_1a6.style.height=(_1a8?"1px":"");
}
}
return _1a8;
},getPortletCurColRow:function(_1ac,_1ad,_1ae){
if(!this.columns||this.columns.length==0){
return null;
}
var _1af=null;
var _1b0=((_1ac!=null)?true:false);
var _1b1=0;
var _1b2=null;
var _1b3=null;
var _1b4=0;
var _1b5=false;
for(var _1b6=0;_1b6<this.columns.length;_1b6++){
var _1b7=this.columns[_1b6];
var _1b8=_1b7.domNode.childNodes;
if(_1b3==null||_1b3!=_1b7.getLayoutId()){
_1b3=_1b7.getLayoutId();
_1b2=this.layouts[_1b3];
if(_1b2==null){
dojo.raise("Layout not found: "+_1b3);
return null;
}
_1b4=0;
_1b5=false;
if(_1b2.clonedFromRootId==null){
_1b5=true;
}else{
var _1b9=this.getColFromColNode(_1b7.domNode.parentNode);
if(_1b9==null){
dojo.raise("Parent column not found: "+_1b7);
return null;
}
_1b7=_1b9;
}
}
var _1ba=null;
var _1bb=jetspeed;
var _1bc=dojo;
var _1bd=_1bb.id.PWIN_CLASS;
if(_1ad){
_1bd+="|"+_1bb.id.PWIN_GHOST_CLASS;
}
if(_1b0){
_1bd+="|"+_1bb.id.COL_CLASS;
}
var _1be=new RegExp("(^|\\s+)("+_1bd+")(\\s+|$)");
for(var _1bf=0;_1bf<_1b8.length;_1bf++){
var _1c0=_1b8[_1bf];
if(_1be.test(_1bc.html.getClass(_1c0))){
_1ba=(_1ba==null?0:_1ba+1);
if((_1ba+1)>_1b4){
_1b4=(_1ba+1);
}
if(_1ac==null||_1c0==_1ac){
var _1c1={layout:_1b3,column:_1b7.getLayoutColumnIndex(),row:_1ba,columnObj:_1b7};
if(!_1b5){
_1c1.layout=_1b2.clonedFromRootId;
}
if(_1ac!=null){
_1af=_1c1;
break;
}else{
if(_1ae!=null){
var _1c2=this.getPWinFromNode(_1c0);
if(_1c2==null){
_1bc.raise("PortletWindow not found for node");
}else{
var _1c3=_1c2.portlet;
if(_1c3==null){
_1bc.raise("PortletWindow for node has null portlet: "+_1c2.widgetId);
}else{
_1ae[_1c3.getId()]=_1c1;
}
}
}
}
}
}
}
if(_1af!=null){
break;
}
}
return _1af;
},_getPortletArrayByZIndex:function(){
var _1c4=jetspeed;
var _1c5=this.getPortletArray();
if(!_1c5){
return _1c5;
}
var _1c6=[];
for(var i=0;i<_1c5.length;i++){
if(!_1c5[i].getProperty(_1c4.id.PP_WINDOW_POSITION_STATIC)){
_1c6.push(_1c5[i]);
}
}
_1c6.sort(this._portletZIndexCompare);
return _1c6;
},_portletZIndexCompare:function(_1c8,_1c9){
var _1ca=null;
var _1cb=null;
var _1cc=null;
_1cc=_1c8.getSavedWinState();
_1ca=_1cc.zIndex;
_1cc=_1c9.getSavedWinState();
_1cb=_1cc.zIndex;
if(_1ca&&!_1cb){
return -1;
}else{
if(_1cb&&!_1ca){
return 1;
}else{
if(_1ca==_1cb){
return 0;
}
}
}
return (_1ca-_1cb);
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
var _1df=[];
for(var _1e0 in this.portlets){
var _1e1=this.portlets[_1e0];
_1df.push(_1e1);
}
return _1df;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _1e2=[];
for(var _1e3 in this.portlets){
var _1e4=this.portlets[_1e3];
_1e2.push(_1e4.getId());
}
return _1e2;
},getPortletByName:function(_1e5){
if(this.portlets&&_1e5){
for(var _1e6 in this.portlets){
var _1e7=this.portlets[_1e6];
if(_1e7.name==_1e5){
return _1e7;
}
}
}
return null;
},getPortlet:function(_1e8){
if(this.portlets&&_1e8){
return this.portlets[_1e8];
}
return null;
},getPWinFromNode:function(_1e9){
var _1ea=null;
if(this.portlets&&_1e9){
for(var _1eb in this.portlets){
var _1ec=this.portlets[_1eb];
var _1ed=_1ec.getPWin();
if(_1ed!=null){
if(_1ed.domNode==_1e9){
_1ea=_1ed;
break;
}
}
}
}
return _1ea;
},putPortlet:function(_1ee){
if(!_1ee){
return;
}
if(!this.portlets){
this.portlets={};
}
this.portlets[_1ee.entityId]=_1ee;
this.portlet_count++;
},putPWin:function(_1ef){
if(!_1ef){
return;
}
var _1f0=_1ef.widgetId;
if(!_1f0){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_1f0]=_1ef;
this.portlet_window_count++;
},getPWin:function(_1f1){
if(this.portlet_windows&&_1f1){
var pWin=this.portlet_windows[_1f1];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_1f1];
if(pWin==null){
var p=this.getPortlet(_1f1);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_1f5){
var _1f6=this.portlet_windows;
var pWin;
var _1f8=[];
for(var _1f9 in _1f6){
pWin=_1f6[_1f9];
if(pWin&&(!_1f5||pWin.portlet)){
_1f8.push(pWin);
}
}
return _1f8;
},getPWinTopZIndex:function(_1fa){
var _1fb=0;
if(_1fa){
_1fb=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_1fb;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_1fb=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_1fb;
}
return _1fb;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_1fc,_1fd){
return;
},onBrowserWindowResize:function(){
var _1fe=jetspeed;
if(_1fe.UAie6){
var _1ff=this.portlet_windows;
var pWin;
for(var _201 in _1ff){
pWin=_1ff[_201];
pWin.onBrowserWindowResize();
}
if(this.editMode){
var _202=dojo.widget.byId(_1fe.id.PG_ED_WID);
if(_202!=null){
_202.onBrowserWindowResize();
}
}
}
},regPWinIFrameCover:function(_203){
if(!_203){
return;
}
this.iframeCoverByWinId[_203.widgetId]=true;
},unregPWinIFrameCover:function(_204){
if(!_204){
return;
}
delete this.iframeCoverByWinId[_204.widgetId];
},displayAllPWinIFrameCovers:function(_205,_206){
var _207=this.portlet_windows;
var _208=this.iframeCoverByWinId;
if(!_207||!_208){
return;
}
for(var _209 in _208){
if(_209==_206){
continue;
}
var pWin=_207[_209];
var _20b=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_20b){
_20b.style.display=(_205?"none":"block");
}
}
},createLayoutInfo:function(_20c){
var _20d=dojo;
var _20e=null;
var _20f=null;
var _210=null;
var _211=null;
var _212=document.getElementById(_20c.id.DESKTOP);
if(_212!=null){
_20e=_20c.ui.getLayoutExtents(_212,null,_20d,_20c);
}
var _213=document.getElementById(_20c.id.COLUMNS);
if(_213!=null){
_20f=_20c.ui.getLayoutExtents(_213,null,_20d,_20c);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_211=_20c.ui.getLayoutExtents(col.domNode,null,_20d,_20c);
}else{
if(!col.columnContainer){
_210=_20c.ui.getLayoutExtents(col.domNode,null,_20d,_20c);
}
}
if(_210!=null&&_211!=null){
break;
}
}
}
this.layoutInfo={desktop:(_20e!=null?_20e:{}),columns:(_20f!=null?_20f:{}),column:(_210!=null?_210:{}),columnLayoutHeader:(_211!=null?_211:{})};
_20c.widget.PortletWindow.prototype.colWidth_pbE=((_210&&_210.pbE)?_210.pbE.w:0);
},destroy:function(){
var _216=jetspeed;
var _217=dojo;
if(_216.UAie6){
_216.ui.evtDisconnect("after",window,"onresize",_216.ui.windowResizeMgr,"onResize",_217.event);
}
var _218=this.portlet_windows;
var _219=this.getPWins(true);
var pWin,_21b;
for(var i=0;i<_219.length;i++){
pWin=_219[i];
_21b=pWin.widgetId;
pWin.closeWindow();
delete _218[_21b];
this.portlet_window_count--;
}
this.portlets={};
this.portlet_count=0;
var _21d=_217.widget.byId(_216.id.PG_ED_WID);
if(_21d!=null){
_21d.editPageDestroy();
}
this._removeCols(document.getElementById(_216.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_21e){
if(_21e==null){
return null;
}
var _21f=_21e.getAttribute("columnindex");
if(_21f==null){
return null;
}
var _220=new Number(_21f);
if(_220>=0&&_220<this.columns.length){
return this.columns[_220];
}
return null;
},getColIndexForNode:function(node){
var _222=null;
if(!this.columns){
return _222;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_222=i;
break;
}
}
return _222;
},getColWithNode:function(node){
var _225=this.getColIndexForNode(node);
return ((_225!=null&&_225>=0)?this.columns[_225]:null);
},getDescendantCols:function(_226){
var dMap={};
if(_226==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_226&&_226.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_22a){
if(!_22a){
return;
}
var _22b=(_22a.getName?_22a.getName():null);
if(_22b!=null){
this.menus[_22b]=_22a;
}
},getMenu:function(_22c){
if(_22c==null){
return null;
}
return this.menus[_22c];
},removeMenu:function(_22d){
if(_22d==null){
return;
}
var _22e=null;
if(dojo.lang.isString(_22d)){
_22e=_22d;
}else{
_22e=(_22d.getName?_22d.getName():null);
}
if(_22e!=null){
delete this.menus[_22e];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _22f=[];
for(var _230 in this.menus){
_22f.push(_230);
}
return _22f;
},retrieveMenuDeclarations:function(_231,_232,_233){
contentListener=new jetspeed.om.MenusApiCL(_231,_232,_233);
this.clearMenus();
var _234="?action=getmenus";
if(_231){
_234+="&includeMenuDefs=true";
}
var _235=this.getPsmlUrl()+_234;
var _236="text/xml";
var _237=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_235,mimetype:_236},contentListener,_237,jetspeed.debugContentDumpIds);
},syncPageControls:function(_238){
var jsId=_238.id;
if(this.actionButtons==null){
return;
}
for(var _23a in this.actionButtons){
var _23b=false;
if(_23a==jsId.ACT_EDIT){
if(!this.editMode){
_23b=true;
}
}else{
if(_23a==jsId.ACT_VIEW){
if(this.editMode){
_23b=true;
}
}else{
if(_23a==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_23b=true;
}
}else{
_23b=true;
}
}
}
if(_23b){
this.actionButtons[_23a].style.display="";
}else{
this.actionButtons[_23a].style.display="none";
}
}
},renderPageControls:function(_23c){
var _23c=jetspeed;
var _23d=_23c.page;
var jsId=_23c.id;
var _23f=dojo;
var _240=[];
if(this.actions!=null){
var addP=false;
for(var _242 in this.actions){
if(_242!=jsId.ACT_HELP){
_240.push(_242);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
addP=true;
if(this.actions[jsId.ACT_VIEW]==null){
_240.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
addP=true;
if(this.actions[jsId.ACT_EDIT]==null){
_240.push(jsId.ACT_EDIT);
}
}
var _243=(_23d.rootFragmentId?_23d.layouts[_23d.rootFragmentId]:null);
var _244=(!(_243==null||_243.layoutActionsDisabled));
if(_244){
_244=_23d._perms(_23c.prefs,_23c.id.PM_P_AD,String.fromCharCode);
if(_244&&!this.isUA()&&(addP||_23d.canNPE())){
_240.push(jsId.ACT_ADDPORTLET);
}
}
}
var _245=_23f.byId(jsId.PAGE_CONTROLS);
if(_245!=null&&_240!=null&&_240.length>0){
var _246=_23c.prefs;
var jsUI=_23c.ui;
var _248=_23f.event;
var _249=_23d.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _24a=this.actionButtonTooltips;
for(var i=0;i<_240.length;i++){
var _242=_240[i];
var _24c=document.createElement("div");
_24c.className="portalPageActionButton";
_24c.style.backgroundImage="url("+_246.getLayoutRootUrl()+"/images/desktop/"+_242+".gif)";
_24c.actionName=_242;
this.actionButtons[_242]=_24c;
_245.appendChild(_24c);
jsUI.evtConnect("after",_24c,"onclick",this,"pageActionButtonClick",_248);
if(_246.pageActionButtonTooltip){
var _24d=null;
if(_246.desktopActionLabels!=null){
_24d=_246.desktopActionLabels[_242];
}
if(_24d==null||_24d.length==0){
_24d=_23f.string.capitalize(_242);
}
_24a.push(_249.addNode(_24c,_24d,true,null,null,null,_23c,jsUI,_248));
}
}
}
},_destroyPageControls:function(){
var _24e=jetspeed;
if(this.actionButtons){
for(var _24f in this.actionButtons){
var _250=this.actionButtons[_24f];
if(_250){
_24e.ui.evtDisconnect("after",_250,"onclick",this,"pageActionButtonClick");
}
}
}
var _251=dojo.byId(_24e.id.PAGE_CONTROLS);
if(_251!=null&&_251.childNodes&&_251.childNodes.length>0){
for(var i=(_251.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_251.childNodes[i]);
}
}
_24e.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_254){
var _255=jetspeed;
if(_254==null){
return;
}
if(_254==_255.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_254==_255.id.ACT_EDIT){
_255.changeActionForPortlet(this.rootFragmentId,null,_255.id.ACT_EDIT,new _255.om.PageChangeActionCL());
_255.editPageInitiate(_255);
}else{
if(_254==_255.id.ACT_VIEW){
_255.changeActionForPortlet(this.rootFragmentId,null,_255.id.ACT_VIEW,new _255.om.PageChangeActionCL());
_255.editPageTerminate(_255);
}else{
var _256=this.getPageAction(_254);
if(_256==null){
return;
}
if(_256.url==null){
return;
}
var _257=_255.url.basePortalUrl()+_255.url.path.DESKTOP+"/"+_256.url;
_255.pageNavigate(_257);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_259,_25a){
var _25b=jetspeed;
var jsId=_25b.id;
if(!_25a){
_25a=escape(this.getPagePathAndQuery());
}else{
_25a=escape(_25a);
}
var _25d=_25b.url.basePortalUrl()+_25b.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_25a;
if(_259!=null){
_25d+="&jslayoutid="+escape(_259);
}
if(this.actions&&(this.actions[jsId.ACT_EDIT]||this.actions[jsId.ACT_VIEW])){
_25b.changeActionForPortlet(this.rootFragmentId,null,jsId.ACT_EDIT,new _25b.om.PageChangeActionCL(_25d));
}else{
if(!this.isUA()){
_25b.pageNavigate(_25d);
}
}
},setPageModePortletActions:function(_25e){
if(_25e==null||_25e.actions==null){
return;
}
var jsId=jetspeed.id;
if(_25e.actions[jsId.ACT_REMOVEPORTLET]==null){
_25e.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_260){
if(this.pageUrl!=null&&!_260){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _262=jsU.path.SERVER+((_260)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _263=jsU.parse(_262);
var _264=null;
if(this.pageUrlFallback!=null){
_264=jsU.parse(this.pageUrlFallback);
}else{
_264=jsU.parse(window.location.href);
}
if(_263!=null&&_264!=null){
var _265=_264.query;
if(_265!=null&&_265.length>0){
var _266=_263.query;
if(_266!=null&&_266.length>0){
_262=_262+"&"+_265;
}else{
_262=_262+"?"+_265;
}
}
}
if(!_260){
this.pageUrl=_262;
}
return _262;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _268=this.getPath();
var _269=jsU.parse(_268);
var _26a=null;
if(this.pageUrlFallback!=null){
_26a=jsU.parse(this.pageUrlFallback);
}else{
_26a=jsU.parse(window.location.href);
}
if(_269!=null&&_26a!=null){
var _26b=_26a.query;
if(_26b!=null&&_26b.length>0){
var _26c=_269.query;
if(_26c!=null&&_26c.length>0){
_268=_268+"&"+_26b;
}else{
_268=_268+"?"+_26b;
}
}
}
this.pagePathAndQuery=_268;
return _268;
},getPageDirectory:function(_26d){
var _26e="/";
var _26f=(_26d?this.getRealPath():this.getPath());
if(_26f!=null){
var _270=_26f.lastIndexOf("/");
if(_270!=-1){
if((_270+1)<_26f.length){
_26e=_26f.substring(0,_270+1);
}else{
_26e=_26f;
}
}
}
return _26e;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_272){
if(!_272){
_272="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_272)){
return jsU.path.SERVER+jsU.path.DESKTOP+_272;
}
return _272;
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
jetspeed.om.Column=function(_274,_275,size,_277,_278,_279){
this.layoutColumnIndex=_274;
this.layoutId=_275;
this.size=size;
this.pageColumnIndex=new Number(_277);
if(typeof _278!="undefined"){
this.layoutActionsDisabled=_278;
}
if((typeof _279!="undefined")&&_279!=null){
this.layoutDepth=_279;
}
this.id="jscol_"+_277;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,layoutDepth:null,layoutMaxChildDepth:0,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_27a){
var _27b=this.styleClass;
var _27c=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_27c>0){
_27b+=" desktopColumnClear-PRIVATE";
}
var _27d=document.createElement("div");
_27d.setAttribute("columnindex",_27c);
_27d.style.width=this.size+"%";
if(this.layoutHeader){
_27b=this.styleLayoutClass;
_27d.setAttribute("layoutid",this.layoutId);
}
_27d.className=_27b;
_27d.id=this.getId();
this.domNode=_27d;
if(_27a!=null){
_27a.appendChild(_27d);
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
},_updateLayoutDepth:function(_280){
var _281=this.layoutDepth;
if(_281!=null&&_280!=_281){
this.layoutDepth=_280;
this.layoutDepthChanged();
}
},_updateLayoutChildDepth:function(_282){
this.layoutMaxChildDepth=(_282==null?0:_282);
}});
jetspeed.om.Portlet=function(_283,_284,_285,_286,_287,_288,_289,_28a){
this.name=_283;
this.entityId=_284;
this.properties=_286;
this.actions=_287;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_288;
this.currentActionMode=_289;
if(_285){
this.contentRetriever=_285;
}
this.layoutActionsDisabled=false;
if(typeof _28a!="undefined"){
this.layoutActionsDisabled=_28a;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _28b=jetspeed;
var jsId=_28b.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _28d=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_28b.prefs.windowTiling){
if(_28d=="true"){
_28d=true;
}else{
if(_28d=="false"){
_28d=false;
}else{
if(_28d!=true&&_28d!=false){
_28d=true;
}
}
}
}else{
_28d=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_28d;
var _28e=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_28e=="true"){
_28e=true;
}else{
if(_28d=="false"){
_28e=false;
}else{
if(_28e!=true&&_28e!=false){
_28e=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_28e;
var _28f=this.properties[jsId.PP_WINDOW_TITLE];
if(!_28f&&this.name){
var re=(/^[^:]*:*/);
_28f=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_28f;
}
},postParseAnnotateHtml:function(_291){
var _292=jetspeed;
var _293=_292.portleturl;
if(_291){
var _294=_291;
var _295=_294.getElementsByTagName("form");
var _296=_292.debug.postParseAnnotateHtml;
var _297=_292.debug.postParseAnnotateHtmlDisableAnchors;
if(_295){
for(var i=0;i<_295.length;i++){
var _299=_295[i];
var _29a=_299.action;
var _29b=_293.parseContentUrl(_29a);
var _29c=_29b.operation;
if(_29c==_293.PORTLET_REQUEST_ACTION||_29c==_293.PORTLET_REQUEST_RENDER){
var _29d=_293.genPseudoUrl(_29b,true);
_299.action=_29d;
var _29e=new _292.om.ActionRenderFormBind(_299,_29b.url,_29b.portletEntityId,_29c);
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_29c+") for form with action: "+_29a);
}
}else{
if(_29a==null||_29a.length==0){
var _29e=new _292.om.ActionRenderFormBind(_299,null,this.entityId,null);
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_29a);
}
}
}
}
}
var _29f=_294.getElementsByTagName("a");
if(_29f){
for(var i=0;i<_29f.length;i++){
var _2a0=_29f[i];
var _2a1=_2a0.href;
var _29b=_293.parseContentUrl(_2a1);
var _2a2=null;
if(!_297){
_2a2=_293.genPseudoUrl(_29b);
}
if(!_2a2){
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2a1);
}
}else{
if(_2a2==_2a1){
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2a1);
}
}else{
if(_296){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2a1+" with: "+_2a2);
}
_2a0.href=_2a2;
}
}
}
}
}
},getPWin:function(){
var _2a3=jetspeed;
var _2a4=this.properties[_2a3.id.PP_WIDGET_ID];
if(_2a4){
return _2a3.page.getPWin(_2a4);
}
return null;
},getCurWinState:function(_2a5){
var _2a6=null;
try{
var _2a7=this.getPWin();
if(!_2a7){
return null;
}
_2a6=_2a7.getCurWinStateForPersist(_2a5);
if(!_2a5){
if(_2a6.layout==null){
_2a6.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2a6;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2a8,_2a9){
var _2aa=jetspeed;
var jsId=_2aa.id;
if(!_2a8){
_2a8={};
}
var _2ac=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2ad=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2a8[jsId.PP_WINDOW_POSITION_STATIC]=_2ac;
_2a8[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2ad;
var _2ae=this.properties["width"];
if(!_2a9&&_2ae!=null&&_2ae>0){
_2a8.width=Math.floor(_2ae);
}else{
if(_2a9){
_2a8.width=-1;
}
}
var _2af=this.properties["height"];
if(!_2a9&&_2af!=null&&_2af>0){
_2a8.height=Math.floor(_2af);
}else{
if(_2a9){
_2a8.height=-1;
}
}
if(!_2ac||!_2aa.prefs.windowTiling){
var _2b0=this.properties["x"];
if(!_2a9&&_2b0!=null&&_2b0>=0){
_2a8.left=Math.floor(((_2b0>0)?_2b0:0));
}else{
if(_2a9){
_2a8.left=-1;
}
}
var _2b1=this.properties["y"];
if(!_2a9&&_2b1!=null&&_2b1>=0){
_2a8.top=Math.floor(((_2b1>0)?_2b1:0));
}else{
_2a8.top=-1;
}
var _2b2=this._getInitialZIndex(_2a9);
if(_2b2!=null){
_2a8.zIndex=_2b2;
}
}
return _2a8;
},_initWinState:function(_2b3,_2b4){
var _2b5=jetspeed;
var _2b6=(_2b3?_2b3:{});
this.getInitialWinDims(_2b6,_2b4);
if(_2b5.debug.initWinState){
var _2b7=this.properties[_2b5.id.PP_WINDOW_POSITION_STATIC];
if(!_2b7||!_2b5.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2b6.zIndex+" x="+_2b6.left+" y="+_2b6.top+" width="+_2b6.width+" height="+_2b6.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2b6.column+" row="+_2b6.row+" width="+_2b6.width+" height="+_2b6.height);
}
}
this.lastSavedWindowState=_2b6;
return _2b6;
},_getInitialZIndex:function(_2b8){
var _2b9=null;
var _2ba=this.properties["z"];
if(!_2b8&&_2ba!=null&&_2ba>=0){
_2b9=Math.floor(_2ba);
}else{
if(_2b8){
_2b9=-1;
}
}
return _2b9;
},_getChangedWindowState:function(_2bb){
var jsId=jetspeed.id;
var _2bd=this.getSavedWinState();
if(_2bd&&dojo.lang.isEmpty(_2bd)){
_2bd=null;
_2bb=false;
}
var _2be=this.getCurWinState(_2bb);
var _2bf=_2be[jsId.PP_WINDOW_POSITION_STATIC];
var _2c0=!_2bf;
if(!_2bd){
var _2c1={state:_2be,positionChanged:true,extendedPropChanged:true};
if(_2c0){
_2c1.zIndexChanged=true;
}
return _2c1;
}
var _2c2=false;
var _2c3=false;
var _2c4=false;
var _2c5=false;
for(var _2c6 in _2be){
if(_2be[_2c6]!=_2bd[_2c6]){
if(_2c6==jsId.PP_WINDOW_POSITION_STATIC||_2c6==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2c2=true;
_2c4=true;
_2c3=true;
}else{
if(_2c6=="zIndex"){
if(_2c0){
_2c2=true;
_2c5=true;
}
}else{
_2c2=true;
_2c3=true;
}
}
}
}
if(_2c2){
var _2c1={state:_2be,positionChanged:_2c3,extendedPropChanged:_2c4};
if(_2c0){
_2c1.zIndexChanged=_2c5;
}
return _2c1;
}
return null;
},getPortletUrl:function(_2c7){
var _2c8=jetspeed;
var _2c9=_2c8.url;
var _2ca=null;
if(_2c7&&_2c7.url){
_2ca=_2c7.url;
}else{
if(_2c7&&_2c7.formNode){
var _2cb=_2c7.formNode.getAttribute("action");
if(_2cb){
_2ca=_2cb;
}
}
}
if(_2ca==null){
_2ca=_2c9.basePortalUrl()+_2c9.path.PORTLET+_2c8.page.getPath();
}
if(!_2c7.dontAddQueryArgs){
_2ca=_2c9.parse(_2ca);
_2ca=_2c9.addQueryParameter(_2ca,"entity",this.entityId,true);
_2ca=_2c9.addQueryParameter(_2ca,"portlet",this.name,true);
_2ca=_2c9.addQueryParameter(_2ca,"encoder","desktop",true);
if(_2c7.jsPageUrl!=null){
var _2cc=_2c7.jsPageUrl.query;
if(_2cc!=null&&_2cc.length>0){
_2ca=_2ca.toString()+"&"+_2cc;
}
}
}
if(_2c7){
_2c7.url=_2ca.toString();
}
return _2ca;
},_submitAjaxApi:function(_2cd,_2ce,_2cf){
var _2d0=jetspeed;
var _2d1="?action="+_2cd+"&id="+this.entityId+_2ce;
var _2d2=_2d0.url.basePortalUrl()+_2d0.url.path.AJAX_API+_2d0.page.getPath()+_2d1;
var _2d3="text/xml";
var _2d4=new _2d0.om.Id(_2cd,this.entityId);
_2d4.portlet=this;
_2d0.url.retrieveContent({url:_2d2,mimetype:_2d3},_2cf,_2d4,_2d0.debugContentDumpIds);
},submitWinState:function(_2d5,_2d6){
var _2d7=jetspeed;
var jsId=_2d7.id;
if(_2d7.page.isUA()||(!(_2d7.page.getPageAction(jsId.ACT_EDIT)||_2d7.page.getPageAction(jsId.ACT_VIEW)||_2d7.page.canNPE()))){
return;
}
var _2d9=null;
if(_2d6){
_2d9={state:this._initWinState(null,true)};
}else{
_2d9=this._getChangedWindowState(_2d5);
}
if(_2d9){
var _2da=_2d9.state;
var _2db=_2da[jsId.PP_WINDOW_POSITION_STATIC];
var _2dc=_2da[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _2dd=null;
if(_2d9.extendedPropChanged){
var _2de=jsId.PP_PROP_SEPARATOR;
var _2df=jsId.PP_PAIR_SEPARATOR;
_2dd=jsId.PP_STATICPOS+_2de+_2db.toString();
_2dd+=_2df+jsId.PP_FITHEIGHT+_2de+_2dc.toString();
_2dd=escape(_2dd);
}
var _2e0="";
var _2e1=null;
if(_2db){
_2e1="moveabs";
if(_2da.column!=null){
_2e0+="&col="+_2da.column;
}
if(_2da.row!=null){
_2e0+="&row="+_2da.row;
}
if(_2da.layout!=null){
_2e0+="&layoutid="+_2da.layout;
}
if(_2da.height!=null){
_2e0+="&height="+_2da.height;
}
}else{
_2e1="move";
if(_2da.zIndex!=null){
_2e0+="&z="+_2da.zIndex;
}
if(_2da.width!=null){
_2e0+="&width="+_2da.width;
}
if(_2da.height!=null){
_2e0+="&height="+_2da.height;
}
if(_2da.left!=null){
_2e0+="&x="+_2da.left;
}
if(_2da.top!=null){
_2e0+="&y="+_2da.top;
}
}
if(_2dd!=null){
_2e0+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_2dd;
}
this._submitAjaxApi(_2e1,_2e0,new _2d7.om.MoveApiCL(this,_2da));
if(!_2d5&&!_2d6){
if(!_2db&&_2d9.zIndexChanged){
var _2e2=_2d7.page.getPortletArray();
if(_2e2&&(_2e2.length-1)>0){
for(var i=0;i<_2e2.length;i++){
var _2e4=_2e2[i];
if(_2e4&&_2e4.entityId!=this.entityId){
if(!_2e4.properties[_2d7.id.PP_WINDOW_POSITION_STATIC]){
_2e4.submitWinState(true);
}
}
}
}
}else{
if(_2db){
}
}
}
}
},retrieveContent:function(_2e5,_2e6,_2e7){
if(_2e5==null){
_2e5=new jetspeed.om.PortletCL(this,_2e7,_2e6);
}
if(!_2e6){
_2e6={};
}
var _2e8=this;
_2e8.getPortletUrl(_2e6);
this.contentRetriever.getContent(_2e6,_2e5,_2e8,jetspeed.debugContentDumpIds);
},setPortletContent:function(_2e9,_2ea,_2eb){
var _2ec=this.getPWin();
if(_2eb!=null&&_2eb.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_2eb;
if(_2ec&&!this.loadingIndicatorIsShown()){
_2ec.setPortletTitle(_2eb);
}
}
if(_2ec){
_2ec.setPortletContent(_2e9,_2ea);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _2ee=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _2ef=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _2f0=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _2f1=this.getPWin();
if(_2f1&&(_2ee||_2ef)){
var _2f2=_2f1.getPortletTitle();
if(_2f2&&(_2f2==_2ee||_2f2==_2ef)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_2f3){
var _2f4=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_2f4=jetspeed.prefs.desktopActionLabels[_2f3];
if(_2f4!=null&&_2f4.length==0){
_2f4=null;
}
}
return _2f4;
},loadingIndicatorShow:function(_2f5){
if(_2f5&&!this.loadingIndicatorIsShown()){
var _2f6=this._getLoadingActionLabel(_2f5);
var _2f7=this.getPWin();
if(_2f7&&_2f6){
_2f7.setPortletTitle(_2f6);
}
}
},loadingIndicatorHide:function(){
var _2f8=this.getPWin();
if(_2f8){
_2f8.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_2fa,_2fb){
var _2fc=jetspeed;
var _2fd=_2fc.url;
var _2fe=null;
if(_2fa!=null){
_2fe=this.getAction(_2fa);
}
var _2ff=_2fb;
if(_2ff==null&&_2fe!=null){
_2ff=_2fe.url;
}
if(_2ff==null){
return;
}
var _300=_2fd.basePortalUrl()+_2fd.path.PORTLET+"/"+_2ff+_2fc.page.getPath();
if(_2fa!=_2fc.id.ACT_PRINT){
this.retrieveContent(null,{url:_300});
}else{
var _301=_2fc.page.getPageUrl();
_301=_2fd.addQueryParameter(_301,"jsprintmode","true");
_301=_2fd.addQueryParameter(_301,"jsaction",escape(_2fe.url));
_301=_2fd.addQueryParameter(_301,"jsentity",this.entityId);
_301=_2fd.addQueryParameter(_301,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_301.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_303,_304,_305){
if(_303){
this.actions=_303;
}else{
this.actions={};
}
this.currentActionState=_304;
this.currentActionMode=_305;
this.syncActions();
},syncActions:function(){
var _306=jetspeed;
_306.page.setPageModePortletActions(this);
var _307=this.getPWin();
if(_307){
_307.actionBtnSync(_306,_306.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_30a,_30b){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_30a;
this.submitOperation=_30b;
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
},eventConfMgr:function(_30e){
var fn=(_30e)?"disconnect":"connect";
var _310=dojo.event;
var form=this.form;
_310[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_310[fn]("after",node,"onclick",this,"click",null);
}
}
var _314=form.getElementsByTagName("input");
for(var i=0;i<_314.length;i++){
var _315=_314[i];
if(_315.type.toLowerCase()=="image"&&_315.form==form){
_310[fn]("after",_315,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_310[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_317){
var _318=true;
if(this.isFormSubmitInProgress()){
_318=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_318=false;
}
}
}
return _318;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _31a=jetspeed.portleturl.parseContentUrl(this.form.action);
var _31b={};
if(_31a.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_31a.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _31c=jetspeed.portleturl.genPseudoUrl(_31a,true);
this.form.action=_31c;
this.submitOperation=_31a.operation;
this.entityId=_31a.portletEntityId;
_31b.url=_31a.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_31b.formFilter=dojo.lang.hitch(this,"formFilter");
_31b.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_31b),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_31b),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_31d){
if(_31d!=undefined){
this.formSubmitInProgress=_31d;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_31e,_31f,_320){
this.portlet=_31e;
this.suppressGetActions=_31f;
this.formbind=null;
if(_320!=null&&_320.submitFormBindObject!=null){
this.formbind=_320.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_321){
if(this.portlet==null){
return;
}
if(_321){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_322,_323,_324,http){
var _326=null;
if(http!=null){
try{
_326=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_326!=null){
_326=unescape(_326);
}
}
_324.setPortletContent(_322,_323,_326);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_324.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_328,_329,_32a){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_329+" type: "+type+jetspeed.formatError(_328));
}};
jetspeed.om.PortletActionCL=function(_32b,_32c){
this.portlet=_32b;
this.formbind=null;
if(_32c!=null&&_32c.submitFormBindObject!=null){
this.formbind=_32c.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_32d){
if(this.portlet==null){
return;
}
if(_32d){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_32e,_32f,_330,http){
var _332=jetspeed;
var _333=null;
var _334=false;
var _335=_332.portleturl.parseContentUrl(_32e);
if(_335.operation==_332.portleturl.PORTLET_REQUEST_ACTION||_335.operation==_332.portleturl.PORTLET_REQUEST_RENDER){
if(_332.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_335.operation+"-url in response body: "+_32e+"  url: "+_335.url+" entity-id: "+_335.portletEntityId);
}
_333=_335.url;
}else{
if(_332.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_32e);
}
_333=_32e;
if(_333){
var _336=_333.indexOf(_332.url.basePortalUrl()+_332.url.path.PORTLET);
if(_336==-1){
_334=true;
window.location.href=_333;
_333=null;
}else{
if(_336>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_32e);
_333=null;
}
}
}
}
if(_333!=null&&!_332.noActionRender){
if(_332.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_333);
}
var _337=new jetspeed.PortletRenderer(false,false,false,_333,true);
_337.renderAll();
}else{
this._loading(false);
}
if(!_334&&this.portlet){
_332.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_339,_33a,_33b){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_339));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _33c=this.getUrl();
if(_33c){
var _33d=jetspeed;
if(!_33d.prefs.ajaxPageNavigation||_33d.url.urlStartsWithHttp(_33c)){
_33d.pageNavigate(_33c,this.getTarget());
}else{
_33d.updatePage(_33c);
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
jetspeed.om.Menu=function(_33e,_33f){
this._is_parsed=false;
this.name=_33e;
this.type=_33f;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_340){
if(!_340){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_340);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_342){
if(!this.hasOptions()){
return null;
}
if(_342==0||_342>0){
if(_342>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_342];
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
var _344=this.options[i];
if(_344 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_346,_347){
var _348=this.parseMenu(data,_347.menuName,_347.menuType);
_347.page.putMenu(_348);
},notifyFailure:function(type,_34a,_34b,_34c){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_34c.toString()+"] url: "+_34b+" type: "+type+jetspeed.formatError(_34a));
},parseMenu:function(node,_34e,_34f){
var menu=null;
var _351=node.getElementsByTagName("js");
if(!_351||_351.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _352=_351[0].childNodes;
for(var i=0;i<_352.length;i++){
var _354=_352[i];
if(_354.nodeType!=1){
continue;
}
var _355=_354.nodeName;
if(_355=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_354,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_34e;
}
if(menu.type==null){
menu.type=_34f;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _358=null;
var _359=node.childNodes;
for(var i=0;i<_359.length;i++){
var _35b=_359[i];
if(_35b.nodeType!=1){
continue;
}
var _35c=_35b.nodeName;
if(_35c=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_35b,new jetspeed.om.Menu()));
}
}else{
if(_35c=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_35b,new jetspeed.om.MenuOption()));
}
}else{
if(_35c=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_35b,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_35c){
mObj[_35c]=((_35b&&_35b.firstChild)?_35b.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_35d,_35e,_35f){
this.includeMenuDefs=_35d;
this.isPageUpdate=_35e;
this.initEditModeConf=_35f;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_361,_362){
var _363=this.getMenuDefs(data,_361,_362);
for(var i=0;i<_363.length;i++){
var mObj=_363[i];
_362.page.putMenu(mObj);
}
this.notifyFinished(_362);
},getMenuDefs:function(data,_367,_368){
var _369=[];
var _36a=data.getElementsByTagName("menu");
for(var i=0;i<_36a.length;i++){
var _36c=_36a[i].getAttribute("type");
if(this.includeMenuDefs){
_369.push(this.parseMenuObject(_36a[i],new jetspeed.om.Menu(null,_36c)));
}else{
var _36d=_36a[i].firstChild.nodeValue;
_369.push(new jetspeed.om.Menu(_36d,_36c));
}
}
return _369;
},notifyFailure:function(type,_36f,_370,_371){
dojo.raise("MenusApiCL error ["+_371.toString()+"] url: "+_370+" type: "+type+jetspeed.formatError(_36f));
},notifyFinished:function(_372){
var _373=jetspeed;
if(this.includeMenuDefs){
_373.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_373.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_373.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_374){
this.portletEntityId=_374;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_376,_377){
if(jetspeed.url.checkAjaxApiResponse(_376,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_378){
var _379=jetspeed.page.getPortlet(this.portletEntityId);
if(_379){
if(_378){
_379.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_379.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_37b,_37c,_37d){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_37d.toString()+"] url: "+_37c+" type: "+type+jetspeed.formatError(_37b));
}});
jetspeed.om.PageChangeActionCL=function(_37e){
this.pageActionUrl=_37e;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_380,_381){
if(jetspeed.url.checkAjaxApiResponse(_380,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_383,_384,_385){
dojo.raise("PageChangeActionCL error ["+_385.toString()+"] url: "+_384+" type: "+type+jetspeed.formatError(_383));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_387,_388){
var _389=jetspeed;
if(_389.url.checkAjaxApiResponse(_387,data,null,false,"user-info")){
var _38a=data.getElementsByTagName("js");
if(_38a&&_38a.length==1){
var root=_38a[0];
var un=_389.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _38e=root.getElementsByTagName("role");
if(_38e!=null){
for(var i=0;i<_38e.length;i++){
var role=(_38e[i].firstChild?_38e[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_389.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_392,_393,_394){
dojo.raise("UserInfoCL error ["+_394.toString()+"] url: "+_393+" type: "+type+jetspeed.formatError(_392));
}});
jetspeed.om.PortletActionsCL=function(_395){
this.portletEntityIds=_395;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_396){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _398=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_398){
if(_396){
_398.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_398.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_39a,_39b){
var _39c=jetspeed;
this._loading(false);
if(_39c.url.checkAjaxApiResponse(_39a,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_39c.page);
}
},processPortletActionsResponse:function(node,_39e){
var _39f=this.parsePortletActionsResponse(node,_39e);
for(var i=0;i<_39f.length;i++){
var _3a1=_39f[i];
var _3a2=_3a1.id;
var _3a3=_39e.getPortlet(_3a2);
if(_3a3!=null){
_3a3.updateActions(_3a1.actions,_3a1.currentActionState,_3a1.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3a5){
var _3a6=new Array();
var _3a7=node.getElementsByTagName("js");
if(!_3a7||_3a7.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3a6;
}
var _3a8=_3a7[0].childNodes;
for(var i=0;i<_3a8.length;i++){
var _3aa=_3a8[i];
if(_3aa.nodeType!=1){
continue;
}
var _3ab=_3aa.nodeName;
if(_3ab=="portlets"){
var _3ac=_3aa;
var _3ad=_3ac.childNodes;
for(var pI=0;pI<_3ad.length;pI++){
var _3af=_3ad[pI];
if(_3af.nodeType!=1){
continue;
}
var _3b0=_3af.nodeName;
if(_3b0=="portlet"){
var _3b1=this.parsePortletElement(_3af,_3a5);
if(_3b1!=null){
_3a6.push(_3b1);
}
}
}
}
}
return _3a6;
},parsePortletElement:function(node,_3b3){
var _3b4=node.getAttribute("id");
if(_3b4!=null){
var _3b5=_3b3._parsePSMLActions(node,null);
var _3b6=_3b3._parsePSMLChildOrAttr(node,"state");
var _3b7=_3b3._parsePSMLChildOrAttr(node,"mode");
return {id:_3b4,actions:_3b5,currentActionState:_3b6,currentActionMode:_3b7};
}
return null;
},notifyFailure:function(type,_3b9,_3ba,_3bb){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3bb.toString()+"] url: "+_3ba+" type: "+type+jetspeed.formatError(_3b9));
}});
jetspeed.om.MoveApiCL=function(_3bc,_3bd){
this.portlet=_3bc;
this.changedState=_3bd;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3be){
if(this.portlet==null){
return;
}
if(_3be){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3c0,_3c1){
var _3c2=jetspeed;
this._loading(false);
dojo.lang.mixin(_3c1.portlet.lastSavedWindowState,this.changedState);
var _3c3=true;
if(djConfig.isDebug&&_3c2.debug.submitWinState){
_3c3=true;
}
var _3c4=_3c2.url.checkAjaxApiResponse(_3c0,data,["refresh"],_3c3,("move-portlet ["+_3c1.portlet.entityId+"]"),_3c2.debug.submitWinState);
if(_3c4=="refresh"){
var _3c5=_3c2.page.getPageUrl();
if(!_3c2.prefs.ajaxPageNavigation){
_3c2.pageNavigate(_3c5,null,true);
}else{
_3c2.updatePage(_3c5,false,true);
}
}
},notifyFailure:function(type,_3c7,_3c8,_3c9){
this._loading(false);
dojo.debug("submitWinState error ["+_3c9.entityId+"] url: "+_3c8+" type: "+type+jetspeed.formatError(_3c7));
}};
jetspeed.postload_addEventListener=function(node,_3cb,fnc,_3cd){
if((_3cb=="load"||_3cb=="DOMContentLoaded"||_3cb=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3cb,fnc,_3cd);
}
};
jetspeed.postload_attachEvent=function(node,_3cf,fnc){
if(_3cf=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3cf,fnc);
}
};
jetspeed.postload_docwrite=function(_3d1){
if(!_3d1){
return;
}
_3d1=_3d1.replace(/^\s+|\s+$/g,"");
var _3d2=/^<script\b([^>]*)>.*?<\/script>/i;
var _3d3=_3d2.exec(_3d1);
if(_3d3){
_3d1=null;
var _3d4=_3d3[1];
if(_3d4){
var _3d5=/\bid\s*=\s*([^\s]+)/i;
var _3d6=_3d5.exec(_3d4);
if(_3d6){
var _3d7=_3d6[1];
_3d1="<img id="+_3d7+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3d1){
var _3d9=dojo;
tn=_3d9.doc().createElement("div");
tn.style.visibility="hidden";
_3d9.body().appendChild(tn);
tn.innerHTML=_3d1;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_3da,_3db,_3dc){
if(_3da==document||_3da==window){
if(_3dc&&_3dc.length>0){
var _3dd=jetspeed.portleturl;
if(_3dc.indexOf(_3dd.DESKTOP_ACTION_PREFIX_URL)!=0&&_3dc.indexOf(_3dd.DESKTOP_RENDER_PREFIX_URL)!=0){
_3da.location=_3dc;
}
}
}else{
if(_3da!=null){
var _3de=_3db.indexOf(".");
if(_3de==-1){
_3da[_3db]=_3dc;
}else{
var _3df=_3db.substring(0,_3de);
var _3e0=_3da[_3df];
if(_3e0){
var _3e1=_3db.substring(_3de+1);
if(_3e1){
_3e0[_3e1]=_3dc;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _3e3=document.createElement("script");
_3e3.setAttribute("type","text/plain");
_3e3.setAttribute("language","ignore");
_3e3.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_3e3);
return _3e3;
};
jetspeed.containsElement=function(_3e4,_3e5,_3e6,_3e7){
if(!_3e4||!_3e5||!_3e6){
return false;
}
if(!_3e7){
_3e7=document;
}
var _3e8=_3e7.getElementsByTagName(_3e4);
if(!_3e8){
return false;
}
for(var i=0;i<_3e8.length;++i){
var _3ea=_3e8[i].getAttribute(_3e5);
if(_3ea==_3e6){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _3eb=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _3ec=_3eb.concat([" height: ","","",";"]);
var _3ed=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _3ee=_3ec.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _3ef=_3ee.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_3eb,cssHeight:_3ec,cssWidthHeight:_3ed,cssOverflow:_3ee,cssPosition:_3ef,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_3f0,_3f1,_3f2,_3f3,_3f4,_3f5){
var djH=dojo.html;
var jsId=jetspeed.id;
var _3f8=null;
var _3f9=-1;
var _3fa=-1;
var _3fb=-1;
if(_3f0){
var _3fc=_3f0.childNodes;
if(_3fc){
_3fb=_3fc.length;
}
_3f8=[];
if(_3fb>0){
var _3fd="",_3fe="";
if(!_3f5){
_3fd=jsId.PWIN_CLASS;
}
if(_3f2){
_3fd+=((_3fd.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_3f3){
_3fd+=((_3fd.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_3f4&&!_3f3){
_3fd+=((_3fd.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_3f3&&!_3f4){
_3fe=((_3fe.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_3fd.length>0){
var _3ff=new RegExp("(^|\\s+)("+_3fd+")(\\s+|$)");
var _400=null;
if(_3fe.length>0){
_400=new RegExp("(^|\\s+)("+_3fe+")(\\s+|$)");
}
var _401,_402,_403;
for(var i=0;i<_3fb;i++){
_401=_3fc[i];
_402=false;
_403=djH.getClass(_401);
if(_3ff.test(_403)&&(_400==null||!_400.test(_403))){
_3f8.push(_401);
_402=true;
}
if(_3f1&&_401==_3f1){
if(!_402){
_3f8.push(_401);
}
_3f9=i;
_3fa=_3f8.length-1;
}
}
}
}
}
return {matchingNodes:_3f8,totalNodes:_3fb,matchNodeIndex:_3f9,matchNodeIndexInMatchingNodes:_3fa};
},getPWinsFromNodes:function(_405){
var _406=jetspeed.page;
var _407=null;
if(_405){
_407=new Array();
for(var i=0;i<_405.length;i++){
var _409=_406.getPWin(_405[i].id);
if(_409){
_407.push(_409);
}
}
}
return _407;
},createPortletWindow:function(_40a,_40b,_40c){
var _40d=false;
if(djConfig.isDebug&&_40c.debug.profile){
_40d=true;
dojo.profile.start("createPortletWindow");
}
var _40e=(_40b!=null);
var _40f=false;
var _410=null;
if(_40e&&_40b<_40c.page.columns.length&&_40b>=0){
_410=_40c.page.columns[_40b].domNode;
}
if(_410==null){
_40f=true;
_410=document.getElementById(_40c.id.DESKTOP);
}
if(_410==null){
return;
}
var _411={};
if(_40a.isPortlet){
_411.portlet=_40a;
if(_40c.prefs.printModeOnly!=null){
_411.printMode=true;
}
if(_40f){
_40a.properties[_40c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_40c.widget.PortletWindow.prototype.altInitParamsDef(_411,_40a);
if(_40f){
pwP.altInitParams[_40c.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _413=new _40c.widget.PortletWindow();
_413.build(_411,_410);
if(_40d){
dojo.profile.end("createPortletWindow");
}
return _413;
},getLayoutExtents:function(node,_415,_416,_417){
if(!_415){
_415=_416.gcs(node);
}
var pad=_416._getPadExtents(node,_415);
var _419=_416._getBorderExtents(node,_415);
var _41a={l:(pad.l+_419.l),t:(pad.t+_419.t),w:(pad.w+_419.w),h:(pad.h+_419.h)};
var _41b=_416._getMarginExtents(node,_415,_417);
return {bE:_419,pE:pad,pbE:_41a,mE:_41b,lessW:(_41a.w+_41b.w),lessH:(_41a.h+_41b.h)};
},getContentBoxSize:function(node,_41d){
var w=node.clientWidth,h,_420;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_420=_41d.pbE;
}else{
h=node.clientHeight;
_420=_41d.pE;
}
return {w:(w-_420.w),h:(h-_420.h)};
},getMarginBoxSize:function(node,_422){
return {w:(node.offsetWidth+_422.mE.w),h:(node.offsetHeight+_422.mE.h)};
},getMarginBox:function(node,_424,_425,_426){
var l=node.offsetLeft-_424.mE.l,t=node.offsetTop-_424.mE.t;
if(_425&&_426.UAope){
l-=_425.bE.l;
t-=_425.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_424.mE.w),h:(node.offsetHeight+_424.mE.h)};
},setMarginBox:function(node,_42a,_42b,_42c,_42d,_42e,_42f,_430){
var pb=_42e.pbE,mb=_42e.mE;
if(_42c!=null&&_42c>=0){
_42c=Math.max(_42c-pb.w-mb.w,0);
}
if(_42d!=null&&_42d>=0){
_42d=Math.max(_42d-pb.h-mb.h,0);
}
_430._setBox(node,_42a,_42b,_42c,_42d);
},evtConnect:function(_433,_434,_435,_436,_437,_438,rate){
if(!rate){
rate=0;
}
var _43a={adviceType:_433,srcObj:_434,srcFunc:_435,adviceObj:_436,adviceFunc:_437,rate:rate};
if(_438==null){
_438=dojo.event;
}
_438.connect(_43a);
return _43a;
},evtDisconnect:function(_43b,_43c,_43d,_43e,_43f,_440){
if(_440==null){
_440=dojo.event;
}
_440.disconnect({adviceType:_43b,srcObj:_43c,srcFunc:_43d,adviceObj:_43e,adviceFunc:_43f});
},evtDisconnectWObj:function(_441,_442){
if(_442==null){
_442=dojo.event;
}
_442.disconnect(_441);
},evtDisconnectWObjAry:function(_443,_444){
if(_443&&_443.length>0){
if(_444==null){
_444=dojo.event;
}
for(var i=0;i<_443.length;i++){
_444.disconnect(_443[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _446=false;
var _447=this._popupMenuWidgets;
for(var i=0;i<_447.length;i++){
var _449=_447[i];
if(_449&&_449.isShowingNow){
_446=true;
break;
}
}
return _446;
},addPopupMenuWidget:function(_44a){
if(_44a){
this._popupMenuWidgets.push(_44a);
}
},removePopupMenuWidget:function(_44b){
if(!_44b){
return;
}
var _44c=this._popupMenuWidgets;
for(var i=0;i<_44c.length;i++){
if(_44c[i]===_44b){
_44c[i]=null;
}
}
},updateChildColInfo:function(_44e,_44f,_450,_451,_452,_453){
var _454=jetspeed;
var _455=dojo;
var _456=_455.byId(_454.id.COLUMNS);
if(!_456){
return;
}
var _457=false;
if(_44e!=null){
var _458=_44e.getAttribute("columnindex");
var _459=_44e.getAttribute("layoutid");
var _45a=(_458==null?-1:new Number(_458));
if(_45a>=0&&_459!=null&&_459.length>0){
_457=true;
}
}
var _45b=_454.page.columns||[];
var _45c=new Array(_45b.length);
var _45d=_454.page.layoutInfo;
var fnc=_454.ui._updateChildColInfo;
fnc(fnc,_456,1,_45c,_45b,_44f,_450,_451,_45d,_45d.columns,_45d.desktop,_44e,_457,_452,_453,_455,_454);
return _45c;
},_updateChildColInfo:function(fnc,_460,_461,_462,_463,_464,_465,_466,_467,_468,_469,_46a,_46b,_46c,_46d,_46e,_46f){
var _470=_460.childNodes;
var _471=(_470?_470.length:0);
if(_471==0){
return;
}
var _472=_46e.html.getAbsolutePosition(_460,true);
var _473=_46f.ui.getMarginBox(_460,_468,_469,_46f);
var _474=_467.column;
var _475,col,_477,_478,_479,_47a,_47b,_47c,_47d,_47e,_47f,_480,_481,_482;
var _483=null,_484=(_46c!=null?(_46c+1):null),_485,_486;
var _487=null;
for(var i=0;i<_471;i++){
_475=_470[i];
_478=_475.getAttribute("columnindex");
_479=(_478==null?-1:new Number(_478));
if(_479>=0){
col=_463[_479];
_482=true;
_477=(col?col.layoutActionsDisabled:false);
_47a=_475.getAttribute("layoutid");
_47b=(_47a!=null&&_47a.length>0);
_485=_484;
_486=null;
_477=((!_466)&&_477);
var _489=_461;
var _48a=(_475===_46a);
if(_47b){
if(_487==null){
_487=_461;
}
if(col){
col._updateLayoutDepth(_461);
}
_489++;
}else{
if(!_48a){
if(col&&(!_477||_466)&&(_464==null||_464[_479]==null)&&(_465==null||_461<=_465)){
_47c=_46f.ui.getMarginBox(_475,_474,_468,_46f);
if(_483==null){
_483=_47c.t-_473.t;
_481=_473.h-_483;
}
_47d=_472.left+(_47c.l-_473.l);
_47e=_472.top+_483;
_47f=_47c.h;
if(_47f<_481){
_47f=_481;
}
if(_47f<40){
_47f=40;
}
var _48b=_475.childNodes;
_480={left:_47d,top:_47e,right:(_47d+_47c.w),bottom:(_47e+_47f),childCount:(_48b?_48b.length:0),pageColIndex:_479};
_480.height=_480.bottom-_480.top;
_480.width=_480.right-_480.left;
_480.yhalf=_480.top+(_480.height/2);
_462[_479]=_480;
_482=(_480.childCount>0);
if(_482){
_475.style.height="";
}else{
_475.style.height="1px";
}
if(_46c!=null){
_486=(_46f.debugDims(_480,true)+" yhalf="+_480.yhalf+(_47c.h!=_47f?(" hreal="+_47c.h):"")+" childC="+_480.childCount+"}");
}
}
}
}
if(_46c!=null){
if(_47b){
_485=_484+1;
}
if(_486==null){
_486="---";
}
_46e.hostenv.println(_46e.string.repeat(_46d,_46c)+"["+((_479<10?" ":"")+_478)+"] "+_486);
}
if(_482){
var _48c=fnc(fnc,_475,_489,_462,_463,_464,_465,_466,_467,(_47b?_467.columnLayoutHeader:_474),_468,_46a,_46b,_485,_46d,_46e,_46f);
if(_48c!=null&&(_487==null||_48c>_487)){
_487=_48c;
}
}
}
}
_478=_460.getAttribute("columnindex");
_47a=_460.getAttribute("layoutid");
_479=(_478==null?-1:new Number(_478));
if(_479>=0&&_47a!=null&&_47a.length>0){
col=_463[_479];
col._updateLayoutChildDepth(_487);
}
return _487;
}};
if(jetspeed.UAie6){
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_48e){
this.oldXY=this.getWinDims(win,win.document,_48e);
},getWinDims:function(win,doc,_491){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_491.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_491;
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
var _498=jetspeed;
var _499=this.getWinDims(window,window.document,_498.docBody);
this.timerId=0;
if((_499.x!=this.oldXY.x)||(_499.y!=this.oldXY.y)){
this.oldXY=_499;
if(_498.page){
if(!this.resizing){
try{
this.resizing=true;
_498.page.onBrowserWindowResize();
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
var _49a=jetspeed;
var _49b=null;
var _49c=false;
var ua=function(){
var _49e=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_49e[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_49e[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_49e[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4a1=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_49e=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_49e[0]==6){
_4a1=true;
}
}
if(!_4a1){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4a1&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_49e=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_49e,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
function showExpressInstall(_4a8){
_49c=true;
var obj=document.getElementById(_4a8.id);
if(obj){
var ac=document.getElementById(_4a8.altContentId);
if(ac){
_49b=ac;
}
var w=_4a8.width?_4a8.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4a8.height?_4a8.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4a8.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
}
function createSWF(_4b1,_4b2,el){
_4b2.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4b1){
if(typeof _4b1[i]=="string"){
if(i=="data"){
_4b2.movie=_4b1[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4b1[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4b1[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4b2){
if(typeof _4b2[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4b2[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4b1){
if(typeof _4b1[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4b1[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4b1[m]);
}
}
}
}
for(var n in _4b2){
if(typeof _4b2[n]=="string"&&n!="movie"){
createObjParam(o,n,_4b2[n]);
}
}
el.parentNode.replaceChild(o,el);
}
}
function createObjParam(el,_4bc,_4bd){
var p=document.createElement("param");
p.setAttribute("name",_4bc);
p.setAttribute("value",_4bd);
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
return {embedSWF:function(_4c5,_4c6,_4c7,_4c8,_4c9,_4ca,_4cb,_4cc,_4cd,_4ce){
if(!ua.w3cdom||!_4c5||!_4c6||!_4c7||!_4c8||!_4c9){
return;
}
if(hasPlayerVersion(_4c9.split("."))){
var _4cf=(_4cd?_4cd.id:null);
createCSS("#"+_4c6,"visibility:hidden");
var att=(typeof _4cd=="object")?_4cd:{};
att.data=_4c5;
att.width=_4c7;
att.height=_4c8;
var par=(typeof _4cc=="object")?_4cc:{};
if(typeof _4cb=="object"){
for(var i in _4cb){
if(typeof _4cb[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4cb[i];
}else{
par.flashvars=i+"="+_4cb[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4c6));
createCSS("#"+_4c6,"visibility:visible");
if(_4cf){
var _4d3=_49a.page.swfInfo;
if(_4d3==null){
_4d3=_49a.page.swfInfo={};
}
_4d3[_4cf]=_4ce;
}
}else{
if(_4ca&&!_49c&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4c6,"visibility:hidden");
var _4d4={};
_4d4.id=_4d4.altContentId=_4c6;
_4d4.width=_4c7;
_4d4.height=_4c8;
_4d4.expressInstall=_4ca;
showExpressInstall(_4d4);
createCSS("#"+_4c6,"visibility:visible");
}
}
}};
}();

jetspeed.getFolders = function(data, handler)

{

    var contentListener = new jetspeed.om.FoldersListContentListener(handler);

    var queryString = "?action=getfolders&data=" + data;

    var getPortletsUrl = jetspeed.url.basePortalUrl() + jetspeed.url.path.AJAX_API + queryString ;

    var mimetype = "text/xml";

    var ajaxApiContext = new jetspeed.om.Id( "getfolders", { } );

    jetspeed.url.retrieveContent( { url: getPortletsUrl, mimetype: mimetype }, contentListener, ajaxApiContext, jetspeed.debugContentDumpIds );

};



// ... jetspeed.om.FoldersListContentListener

jetspeed.om.FoldersListContentListener = function(finishedFunction)

{

    this.notifyFinished = finishedFunction;

};

dojo.lang.extend( jetspeed.om.FoldersListContentListener,

{

    notifySuccess: function( /* XMLDocument */ data, /* String */ requestUrl, domainModelObject )

    {

        var folderlist = this.parseFolders( data );

		var pagesList = this.parsePages( data );

		var linksList = this.parseLinks( data );

        if ( dojo.lang.isFunction( this.notifyFinished ) )

        {

            this.notifyFinished( domainModelObject, folderlist,pagesList,linksList);

        }

    },

    notifyFailure: function( /* String */ type, /* String */ error, /* String */ requestUrl, domainModelObject )

    {

        dojo.raise( "FoldersListContentListener error [" + domainModelObject.toString() + "] url: " + requestUrl + " type: " + type + jetspeed.url.formatBindError( error ) );

    },    

    parseFolders: function( /* XMLNode */ node )

    {

        var folderlist = [];

        var jsElements = node.getElementsByTagName( "js" );

        if ( ! jsElements || jsElements.length > 1 )

            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );

        var children = jsElements[0].childNodes;

        

        for ( var i = 0 ; i < children.length ; i++ )

        {

            var child = children[i];

            if ( child.nodeType != dojo.dom.ELEMENT_NODE )

                continue;

            var childLName = child.nodeName;

            if ( childLName == "folders" )

            {

                var portletsNode = child ;

                var portletChildren = portletsNode.childNodes ;

                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )

                {

                    var pChild = portletChildren[pI];

                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )

                        continue;

                    var pChildLName = pChild.nodeName;

                    if (pChildLName == "folder")

                    {

                        var folderdef = this.parsePortletElement( pChild );

                        folderlist.push( folderdef ) ;

                    }					

                }

            }

        }

        return folderlist ;

    },

	parsePages: function( /* XMLNode */ node )

    {

		var pageslist = [];

        var jsElements = node.getElementsByTagName( "js" );

        if ( ! jsElements || jsElements.length > 1 )

            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );

        var children = jsElements[0].childNodes;

        

        for ( var i = 0 ; i < children.length ; i++ )

        {

            var child = children[i];

            if ( child.nodeType != dojo.dom.ELEMENT_NODE )

                continue;

            var childLName = child.nodeName;

            if ( childLName == "folders" )

            {

                var portletsNode = child ;

                var portletChildren = portletsNode.childNodes ;

                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )

                {

                    var pChild = portletChildren[pI];

                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )

                        continue;

                    var pChildLName = pChild.nodeName;

                    if (pChildLName == "page")

                    {

                        var folderdef = this.parsePortletElement( pChild );

                        pageslist.push( folderdef ) ;

                    }

					

                }

            }

        }

        return pageslist ;

    },

	parseLinks: function( /* XMLNode */ node )

    {

		var linkslist = [];

        var jsElements = node.getElementsByTagName( "js" );

        if ( ! jsElements || jsElements.length > 1 )

            dojo.raise( "unexpected zero or multiple <js> elements in portlet selector xml" );

        var children = jsElements[0].childNodes;

        

        for ( var i = 0 ; i < children.length ; i++ )

        {

            var child = children[i];

            if ( child.nodeType != dojo.dom.ELEMENT_NODE )

                continue;

            var childLName = child.nodeName;

            if ( childLName == "folders" )

            {

                var portletsNode = child ;

                var portletChildren = portletsNode.childNodes ;

                for ( var pI = 0 ; pI < portletChildren.length ; pI++ )

                {

                    var pChild = portletChildren[pI];

                    if ( pChild.nodeType != dojo.dom.ELEMENT_NODE )

                        continue;

                    var pChildLName = pChild.nodeName;

                    if (pChildLName == "link")

                    {

                        var folderdef = this.parsePortletElement( pChild );

                        linkslist.push( folderdef ) ;

                    }

					

                }

            }

        }

        return linkslist ;

    },

    parsePortletElement: function( /* XMLNode */ node )

    {

        var folderName = node.getAttribute( "name" );

        var folderPath = node.getAttribute( "path" );

        return new jetspeed.om.FolderDef( folderName, folderPath) ;

    }

});



// ... jetspeed.om.FolderDef

jetspeed.om.FolderDef = function( /* String */ folderName, /* String */ folderPath)

{

    this.folderName = folderName;

    this.folderPath = folderPath;

};

dojo.inherits( jetspeed.om.FolderDef, jetspeed.om.Id);

dojo.lang.extend( jetspeed.om.FolderDef,

{

    folderName: null,

    folderPath: null,

    getName: function()  // jetspeed.om.Id protocol

    {

        return this.folderName;

    },

    getPath: function()

    {

        return this.folderPath;

    }

});


