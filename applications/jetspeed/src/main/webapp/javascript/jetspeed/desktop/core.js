dojo.provide("jetspeed.desktop.core");
dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.xml.Parse");
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
_8.getHead();
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
jetspeed.createHeadElement=function(_34){
var _35=document.createElement(_34.tagName);
for(var _36 in _34[_34.tagName]){
var _37=_34[_34.tagName].nodeRef.getAttribute(_36);
if(_37){
_35.setAttribute(_36,_37);
}
}
return _35;
};
jetspeed.contributeHeadElements=function(_38){
var _39=jetspeed;
if(_38.script){
for(var i=0;i<_38.script.length;i++){
var _3b=_38.script[i];
var _3c=jetspeed.createHeadElement(_3b);
if(!_3c.id||!document.getElementById(_3c.id)){
if(_39.UAie){
_3c.text=_3b.value;
}else{
_3c.appendChild(document.createTextNode(_3b.value));
}
_39.getHead().appendChild(_3c);
}
}
}
if(_38.link){
for(var i=0;i<_38.link.length;i++){
var _3d=_38.link[i];
var _3e=jetspeed.createHeadElement(_3d);
if(!_3e.id||!document.getElementById(_3e.id)){
_39.getHead().appendChild(_3e);
}
}
}
if(_38.style){
for(var i=0;i<_38.style.length;i++){
var _3f=_38.style[i];
var _40=jetspeed.createHeadElement(_3f);
if(!_40.id||!document.getElementById(_40.id)){
if(_39.UAie){
_40.styleSheet.cssText=_3f.value;
}else{
_40.appendChild(document.createTextNode(_3f.value));
}
_39.getHead().appendChild(_40);
}
}
}
};
jetspeed.doRender=function(_41,_42){
if(!_41){
_41={};
}else{
if((typeof _41=="string"||_41 instanceof String)){
_41={url:_41};
}
}
var _43=jetspeed.page.getPortlet(_42);
if(_43){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_42+"] url: "+_41.url);
}
_43.retrieveContent(null,_41);
}
};
jetspeed.doAction=function(_44,_45){
if(!_44){
_44={};
}else{
if((typeof _44=="string"||_44 instanceof String)){
_44={url:_44};
}
}
var _46=jetspeed.page.getPortlet(_45);
if(_46){
if(jetspeed.debug.doRenderDoAction){
if(!_44.formNode){
dojo.debug("doAction ["+_45+"] url: "+_44.url+" form: null");
}else{
dojo.debug("doAction ["+_45+"] url: "+_44.url+" form: "+jetspeed.debugDumpForm(_44.formNode));
}
}
_46.retrieveContent(new jetspeed.om.PortletActionCL(_46,_44),_44);
}
};
jetspeed.PortletRenderer=function(_47,_48,_49,_4a,_4b,_4c){
var _4d=jetspeed;
var _4e=_4d.page;
var _4f=dojo;
this._jsObj=_4d;
this.mkWins=_47;
this.initEdit=_4c;
this.minimizeTemp=(_4c!=null&&_4c.editModeMove);
this.noRender=(this.minimizeTemp&&_4c.windowTitles!=null);
this.isPgLd=_48;
this.isPgUp=_49;
this.renderUrl=_4a;
this.suppressGetActions=_4b;
this._colLen=_4e.columns.length;
this._colIndex=0;
this._portletIndex=0;
this._renderCount=0;
this.psByCol=_4e.portletsByPageColumn;
this.pageLoadUrl=null;
if(_48){
this.pageLoadUrl=_4d.url.parse(_4e.getPageUrl());
_4d.ui.evtConnect("before",_4f,"addOnLoad",_4e,"_beforeAddOnLoad",_4f.event);
}
this.dbgPgLd=_4d.debug.pageLoad&&_48;
this.dbgMsg=null;
if(_4d.debug.doRenderDoAction||this.dbgPgLd){
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
var _50=this._jsObj;
var _51=this.dbgMsg;
if(_51!=null){
if(this.dbgPgLd){
dojo.debug("portlet-renderer page-url: "+_50.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPgLd){
_50.page.loadPostRender(this.isPgUp,this.initEdit);
}
},_renderCurrent:function(){
var _52=this._jsObj;
var _53=this._colLen;
var _54=this._colIndex;
var _55=this._portletIndex;
if(_54<=_53){
var _56;
if(_54<_53){
_56=this.psByCol[_54.toString()];
}else{
_56=this.psByCol["z"];
_54=null;
}
var _57=(_56!=null?_56.length:0);
if(_57>0){
var _58=_56[_55];
if(_58){
var _59=_58.portlet;
var _5a=null;
if(this.mkWins){
_5a=_52.ui.createPortletWindow(_59,_54,_52);
if(this.minimizeTemp){
_5a.minimizeWindowTemporarily(this.noRender);
}
}
var _5b=this.dbgMsg;
if(_5b!=null){
if(_5b.length>0){
_5b=_5b+", ";
}
var _5c=null;
if(_59.getProperty!=null){
_5c=_59.getProperty(_52.id.PP_WIDGET_ID);
}
if(!_5c){
_5c=_59.widgetId;
}
if(!_5c){
_5c=_59.toString();
}
if(_59.entityId){
_5b=_5b+_59.entityId+"("+_5c+")";
if(this._dbPgLd&&_59.getProperty(_52.id.PP_WINDOW_TITLE)){
_5b=_5b+" "+_59.getProperty(_52.id.PP_WINDOW_TITLE);
}
}else{
_5b=_5b+_5c;
}
}
if(!this.noRender){
_59.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}else{
if(_5a&&_5a.portlet){
var _5d=this.initEdit.windowTitles[_5a.portlet.entityId];
if(_5d!=null){
_5a.setPortletTitle(_5d);
}
}
}
if((this._renderCount%3)==0){
_52.url.loadingIndicatorStep(_52);
}
this._renderCount++;
}
}
}
},_evalNext:function(){
var _5e=false;
var _5f=this._colLen;
var _60=this._colIndex;
var _61=this._portletIndex;
var _62=_60;
var _63;
for(++_60;_60<=_5f;_60++){
_63=this.psByCol[_60==_5f?"z":_60.toString()];
if(_61<(_63!=null?_63.length:0)){
_5e=true;
this._colIndex=_60;
break;
}
}
if(!_5e){
++_61;
for(_60=0;_60<=_62;_60++){
_63=this.psByCol[_60==_5f?"z":_60.toString()];
if(_61<(_63!=null?_63.length:0)){
_5e=true;
this._colIndex=_60;
this._portletIndex=_61;
break;
}
}
}
return _5e;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_64){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _66=_64;
var _67=null;
if(_64&&_64.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_64.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_64&&_64.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_64.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_67=jetspeed.url.getQueryParameter(_64,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_66)){
_66=null;
}
return {url:_66,operation:op,portletEntityId:_67};
},genPseudoUrl:function(_68,_69){
if(!_68||!_68.url||!_68.portletEntityId){
return null;
}
var _6a=null;
if(_69){
_6a=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_6a="javascript:";
var _6b=false;
if(_68.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_6a+="doAction(\"";
}else{
if(_68.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_6a+="doRender(\"";
}else{
_6b=true;
}
}
if(_6b){
return null;
}
_6a+=_68.url+"\",\""+_68.portletEntityId+"\"";
_6a+=")";
}
return _6a;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_6c,_6d,_6e){
var _6f=null;
var _70=_6d.portletDecorationsConfig;
if(_6c&&_70){
_6f=_70[_6c];
}
if(_6f==null&&!_6e){
var _71=_6d.portletDecorationsAllowed;
for(var i=0;i<_71.length;i++){
_6c=_71[i];
_6f=_70[_6c];
if(_6f!=null){
break;
}
}
}
if(_6f!=null&&!_6f._initialized){
var _73=jetspeed.prefs.getPortletDecorationBaseUrl(_6c);
_6f._initialized=true;
_6f.cssPathCommon=new dojo.uri.Uri(_73+"/css/styles.css");
_6f.cssPathDesktop=new dojo.uri.Uri(_73+"/css/desktop.css");
dojo.html.insertCssFile(_6f.cssPathCommon,null,true);
dojo.html.insertCssFile(_6f.cssPathDesktop,null,true);
}
return _6f;
};
jetspeed.loadPortletDecorationConfig=function(_74,_75,_76){
var _77={};
_75.portletDecorationsConfig[_74]=_77;
_77.name=_74;
_77.windowActionButtonOrder=_75.windowActionButtonOrder;
_77.windowActionNotPortlet=_75.windowActionNotPortlet;
_77.windowActionButtonMax=_75.windowActionButtonMax;
_77.windowActionButtonTooltip=_75.windowActionButtonTooltip;
_77.windowActionMenuOrder=_75.windowActionMenuOrder;
_77.windowActionNoImage=_75.windowActionNoImage;
_77.windowIconEnabled=_75.windowIconEnabled;
_77.windowIconPath=_75.windowIconPath;
_77.windowTitlebar=_75.windowTitlebar;
_77.windowResizebar=_75.windowResizebar;
_77.dNodeClass=_76.P_CLASS+" "+_74+" "+_76.PWIN_CLASS+" "+_76.PWIN_CLASS+"-"+_74;
_77.cNodeClass=_76.P_CLASS+" "+_74+" "+_76.PWIN_CLIENT_CLASS;
if(_75.portletDecorationsProperties){
var _78=_75.portletDecorationsProperties[_74];
if(_78){
for(var _79 in _78){
_77[_79]=_78[_79];
}
if(_78.windowActionNoImage!=null){
var _7a={};
for(var i=0;i<_78.windowActionNoImage.length;i++){
_7a[_78.windowActionNoImage[i]]=true;
}
_77.windowActionNoImage=_7a;
}
if(_78.windowIconPath!=null){
_77.windowIconPath=dojo.string.trim(_78.windowIconPath);
if(_77.windowIconPath==null||_77.windowIconPath.length==0){
_77.windowIconPath=null;
}else{
var _7c=_77.windowIconPath;
var _7d=_7c.charAt(0);
if(_7d!="/"){
_7c="/"+_7c;
}
var _7e=_7c.charAt(_7c.length-1);
if(_7e!="/"){
_7c=_7c+"/";
}
_77.windowIconPath=_7c;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(_7f,_80){
var _81=jetspeed;
_81.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _82=_81.page.getMenuNames();
for(var i=0;i<_82.length;i++){
var _84=_82[i];
var _85=dojo.widget.byId(_81.id.MENU_WIDGET_ID_PREFIX+_84);
if(_85){
_85.createJetspeedMenu(_81.page.getMenu(_84));
}
}
if(!_80){
_81.url.loadingIndicatorHide();
}
_81.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_86){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_86);
}
};
jetspeed.menuNavClickWidget=function(_87,_88){
if(!_87){
return;
}
if(dojo.lang.isString(_87)){
var _89=_87;
_87=dojo.widget.byId(_89);
if(!_87){
dojo.raise("Tab widget not found: "+_89);
}
}
if(_87){
var _8a=_87.jetspeedmenuname;
if(!_8a&&_87.extraArgs){
_8a=_87.extraArgs.jetspeedmenuname;
}
if(!_8a){
dojo.raise("Tab widget is invalid: "+_87.widgetId);
}
var _8b=jetspeed.page.getMenu(_8a);
if(!_8b){
dojo.raise("Tab widget "+_87.widgetId+" no menu: "+_8a);
}
var _8c=_8b.getOptionByIndex(_88);
jetspeed.menuNavClick(_8c);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_8d,_8e,_8f){
var _90=jetspeed;
if(!_8d||_90.pageNavigateSuppress){
return;
}
if(typeof _8f=="undefined"){
_8f=false;
}
if(!_8f&&_90.page&&_90.page.equalsPageUrl(_8d)){
return;
}
_8d=_90.page.makePageUrl(_8d);
if(_8e=="top"){
top.location.href=_8d;
}else{
if(_8e=="parent"){
parent.location.href=_8d;
}else{
window.location.href=_8d;
}
}
};
jetspeed.getActionsForPortlet=function(_91){
if(_91==null){
return;
}
jetspeed.getActionsForPortlets([_91]);
};
jetspeed.getActionsForPortlets=function(_92){
var _93=jetspeed;
if(_92==null){
_92=_93.page.getPortletIds();
}
var _94=new _93.om.PortletActionsCL(_92);
var _95="?action=getactions";
for(var i=0;i<_92.length;i++){
_95+="&id="+_92[i];
}
var _97=_93.url.basePortalUrl()+_93.url.path.AJAX_API+_93.page.getPath()+_95;
var _98="text/xml";
var _99=new _93.om.Id("getactions",{});
_93.url.retrieveContent({url:_97,mimetype:_98},_94,_99,_93.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_9a,_9b,_9c,_9d,_9e){
var _9f=jetspeed;
if(_9a==null){
return;
}
if(_9d==null){
_9d=new _9f.om.PortletChangeActionCL(_9a);
}
var _a0="?action=window&id="+(_9a!=null?_9a:"");
if(_9b!=null){
_a0+="&state="+_9b;
}
if(_9c!=null){
_a0+="&mode="+_9c;
}
var _a1=_9e;
if(!_a1){
_a1=_9f.page.getPath();
}
var _a2=_9f.url.basePortalUrl()+_9f.url.path.AJAX_API+_a1+_a0;
var _a3="text/xml";
var _a4=new _9f.om.Id("changeaction",{});
_9f.url.retrieveContent({url:_a2,mimetype:_a3},_9d,_a4,_9f.debugContentDumpIds);
};
jetspeed.getUserInfo=function(_a5){
var _a6=jetspeed;
var _a7=new _a6.om.UserInfoCL();
var _a8="?action=getuserinfo";
var _a9=_a6.url.basePortalUrl()+_a6.url.path.AJAX_API+_a6.page.getPath()+_a8;
var _aa="text/xml";
var _ab=new _a6.om.Id("getuserinfo",{});
_a6.url.retrieveContent({url:_a9,mimetype:_aa,sync:_a5},_a7,_ab,_a6.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_ac,_ad){
var _ae=_ac.page;
if(!_ae.editMode){
var _af=_ac.css;
var _b0=true;
var _b1=_ac.url.getQueryParameter(window.location.href,_ac.id.PORTAL_ORIGINATE_PARAMETER);
if(_b1!=null&&_b1=="true"){
_b0=false;
}
_ae.editMode=true;
var _b2=dojo.widget.byId(_ac.id.PG_ED_WID);
if(_ac.UAie6){
_ae.displayAllPWins(true);
}
var _b3=((_ad!=null&&_ad.editModeMove)?true:false);
var _b4=_ae._perms(_ac.prefs,-1,String.fromCharCode);
if(_b4&&_b4[2]&&_b4[2].length>0){
if(!_ac.page._getU()){
_ac.getUserInfo(true);
}
}
if(_b2==null){
try{
_ac.url.loadingIndicatorShow("loadpageeditor",true);
_b2=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_ac.id.PG_ED_WID,editorInitiatedFromDesktop:_b0,editModeMove:_b3});
var _b5=document.getElementById(_ac.id.COLUMNS);
_b5.insertBefore(_b2.domNode,_b5.firstChild);
}
catch(e){
_ac.url.loadingIndicatorHide();
if(_ac.UAie6){
_ae.displayAllPWins();
}
}
}else{
_b2.editPageShow();
}
_ae.syncPageControls(_ac);
}
};
jetspeed.editPageTerminate=function(_b6,_b7){
var _b8=_b6.page;
if(_b8.editMode){
var _b9=null;
var _ba=_b6.css;
var _bb=dojo.widget.byId(_b6.id.PG_ED_WID);
if(_bb!=null&&!_bb.editorInitiatedFromDesktop){
var _bc=_b8.getPageUrl(true);
_bc=_b6.url.removeQueryParameter(_bc,_b6.id.PG_ED_PARAM);
_bc=_b6.url.removeQueryParameter(_bc,_b6.id.PORTAL_ORIGINATE_PARAMETER);
_b9=_bc;
}else{
var _bd=_b6.url.getQueryParameter(window.location.href,_b6.id.PG_ED_PARAM);
if(_bd!=null&&_bd=="true"){
var _be=window.location.href;
_be=_b6.url.removeQueryParameter(_be,_b6.id.PG_ED_PARAM);
_b9=_be;
}
}
if(_b9!=null){
_b9=_b9.toString();
}
_b8.editMode=false;
_b6.changeActionForPortlet(_b8.rootFragmentId,null,_b6.id.ACT_VIEW,new _b6.om.PageChangeActionCL(_b9));
if(_b9==null){
if(_bb!=null){
_bb.editMoveModeExit(true);
_bb.editPageHide();
}
_b8.syncPageControls(_b6);
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_bf,_c0,_c1,_c2){
if(!_bf){
_bf={};
}
jetspeed.url.retrieveContent(_bf,_c0,_c1,_c2);
}};
jetspeed.om.PageCLCreateWidget=function(_c3,_c4){
if(typeof _c3=="undefined"){
_c3=false;
}
this.isPageUpdate=_c3;
this.initEditModeConf=_c4;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_c5,_c6,_c7){
_c7.loadFromPSML(_c5,this.isPageUpdate,this.initEditModeConf);
},notifyFailure:function(_c8,_c9,_ca,_cb){
dojo.raise("PageCLCreateWidget error url: "+_ca+" type: "+_c8+jetspeed.formatError(_c9));
}};
jetspeed.om.Page=function(_cc,_cd,_ce,_cf,_d0){
if(_cc!=null&&_cd!=null){
this.requiredLayoutDecorator=_cc;
this.setPsmlPathFromDocumentUrl(_cd);
this.pageUrlFallback=_cd;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _ce!="undefined"){
this.addToHistory=_ce;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets={};
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_d0!=null){
this.iframeCoverByWinId=_d0;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_cf!=null){
this.tooltipMgr=_cf;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,uIA:true,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _d1=(this.name!=null&&this.name.length>0?this.name:null);
if(!_d1){
this.getPsmlUrl();
_d1=this.psmlPath;
}
return "page-"+_d1;
},setPsmlPathFromDocumentUrl:function(_d2){
var _d3=jetspeed;
var _d4=_d3.url.path.AJAX_API;
var _d5=null;
if(_d2==null){
_d5=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_d3.prefs.ajaxPageNavigation){
var _d6=window.location.hash;
if(_d6!=null&&_d6.length>0){
if(_d6.indexOf("#")==0){
_d6=(_d6.length>1?_d6.substring(1):"");
}
if(_d6!=null&&_d6.length>1&&_d6.indexOf("/")==0){
this.psmlPath=_d3.url.path.AJAX_API+_d6;
return;
}
}
}
}else{
var _d7=_d3.url.parse(_d2);
_d5=_d7.path;
}
var _d8=_d3.url.path.DESKTOP;
var _d9=_d5.indexOf(_d8);
if(_d9!=-1&&_d5.length>(_d9+_d8.length)){
_d4=_d4+_d5.substring(_d9+_d8.length);
}
this.psmlPath=_d4;
},getPsmlUrl:function(){
var _da=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _db=_da.url.basePortalUrl()+this.psmlPath;
if(_da.prefs.printModeOnly!=null){
_db=_da.url.addQueryParameter(_db,"layoutid",_da.prefs.printModeOnly.layout);
_db=_da.url.addQueryParameter(_db,"entity",_da.prefs.printModeOnly.entity).toString();
}
return _db;
},_setU:function(u){
this._u=u;
},_getU:function(){
return this._u;
},retrievePsml:function(_dd){
var _de=jetspeed;
if(_dd==null){
_dd=new _de.om.PageCLCreateWidget();
}
var _df=this.getPsmlUrl();
var _e0="text/xml";
if(_de.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_df);
}
_de.url.retrieveContent({url:_df,mimetype:_e0},_dd,this,_de.debugContentDumpIds);
},loadFromPSML:function(_e1,_e2,_e3){
var _e4=jetspeed;
var _e5=_e4.prefs;
var _e6=dojo;
var _e7=_e5.printModeOnly;
if(djConfig.isDebug&&_e4.debug.profile&&_e7==null){
_e6.profile.start("loadFromPSML");
}
var _e8=this._parsePSML(_e1);
jetspeed.rootfrag=_e8;
if(_e8==null){
return;
}
this.portletsByPageColumn={};
var _e9={};
if(this.portletDecorator){
_e9[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_e8,0,null,this.portletsByPageColumn,true,_e9,_e6,_e4);
this.rootFragmentId=_e8.id;
this.editMode=false;
for(var _ea in _e9){
_e4.loadPortletDecorationStyles(_ea,_e5,true);
}
if(_e5.windowTiling){
this._createColsStart(document.getElementById(_e4.id.DESKTOP),_e4.id.COLUMNS);
}
this.createLayoutInfo(_e4);
var _eb=this.portletsByPageColumn["z"];
if(_eb){
_eb.sort(this._loadPortletZIndexCompare);
}
if(typeof _e3=="undefined"){
_e3=null;
}
if(_e3!=null||(this.actions!=null&&this.actions[_e4.id.ACT_VIEW]!=null)){
if(!this.isUA()&&this.actions!=null&&(this.actions[_e4.id.ACT_EDIT]!=null||this.actions[_e4.id.ACT_VIEW]!=null)){
if(_e3==null){
_e3={};
}
if((typeof _e3.editModeMove=="undefined")&&this._perms(_e5,_e4.id.PM_MZ_P,String.fromCharCode)){
_e3.editModeMove=true;
}
var _ec=_e4.url.parse(window.location.href);
if(!_e3.editModeMove){
var _ed=_e4.url.getQueryParameter(_ec,_e4.id.PG_ED_STATE_PARAM);
if(_ed!=null){
_ed="0x"+_ed;
if((_ed&_e4.id.PM_MZ_P)>0){
_e3.editModeMove=true;
}
}
}
if(_e3.editModeMove&&!_e3.windowTitles){
var _ee=_e4.url.getQueryParameter(_ec,_e4.id.PG_ED_TITLES_PARAM);
if(_ee!=null){
var _ef=_ee.length;
var _f0=new Array(_ef/2);
var _f1=String.fromCharCode;
var _f2=0,chI=0;
while(chI<(_ef-1)){
_f0[_f2]=_f1(Number("0x"+_ee.substring(chI,(chI+2))));
_f2++;
chI+=2;
}
var _f4=null;
try{
_f4=eval("({"+_f0.join("")+"})");
}
catch(e){
if(djConfig.isDebug){
dojo.debug("cannot parse json: "+_f0.join(""));
}
}
if(_f4!=null){
var _f5=false;
for(var _f6 in this.portlets){
var _f7=this.portlets[_f6];
if(_f7!=null&&!_f4[_f7.entityId]){
_f5=true;
break;
}
}
if(!_f5){
_e3.windowTitles=_f4;
}
}
}
}
}else{
_e3=null;
}
}
if(_e3!=null){
_e4.url.loadingIndicatorShow("loadpageeditor",true);
}
var _f8=new _e4.PortletRenderer(true,true,_e2,null,true,_e3);
_f8.renderAllTimeDistribute();
},loadPostRender:function(_f9,_fa){
var _fb=jetspeed;
var _fc=_fb.prefs.printModeOnly;
if(_fc==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
this.retrieveMenuDeclarations(true,_f9,_fa);
}else{
for(var _fd in this.portlets){
var _fe=this.portlets[_fd];
if(_fe!=null){
_fe.renderAction(null,_fc.action);
}
break;
}
if(_f9){
_fb.updatePageEnd();
}
}
_fb.ui.evtConnect("after",window,"onresize",_fb.ui.windowResizeMgr,"onResize",dojo.event);
_fb.ui.windowResizeMgr.onResizeDelayedCompare();
var _ff,_100=this.columns;
if(_100){
for(var i=0;i<_100.length;i++){
_ff=_100[i].domNode;
if(!_ff.childNodes||_ff.childNodes.length==0){
_ff.style.height="1px";
}
}
}
var _102=this.maximizedOnInit;
if(_102!=null){
var _103=this.getPWin(_102);
if(_103==null){
dojo.raise("no pWin to max");
}else{
dojo.lang.setTimeout(_103,_103._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_fb.url,_fb.url.loadingIndicatorStepPreload,1800);
},loadPostRetrieveMenus:function(_104,_105){
var _106=jetspeed;
this.renderPageControls(_106);
if(_105){
_106.editPageInitiate(_106,_105);
}
if(_104){
_106.updatePageEnd();
}
this.syncPageControls(_106);
},_parsePSML:function(psml){
var _108=jetspeed;
var _109=dojo;
var _10a=psml.getElementsByTagName("page");
if(!_10a||_10a.length>1||_10a[0]==null){
_109.raise("<page>");
}
var _10b=_10a[0];
var _10c=_10b.childNodes;
var _10d=new RegExp("(name|path|profiledPath|title|short-title|uIA|npe)");
var _10e=null;
var _10f={};
for(var i=0;i<_10c.length;i++){
var _111=_10c[i];
if(_111.nodeType!=1){
continue;
}
var _112=_111.nodeName;
if(_112=="fragment"){
_10e=_111;
}else{
if(_112=="defaults"){
this.layoutDecorator=_111.getAttribute("layout-decorator");
var _113=_111.getAttribute("portlet-decorator");
var _114=_108.prefs.portletDecorationsAllowed;
if(!_114||_109.lang.indexOf(_114,_113)==-1){
_113=_108.prefs.windowDecoration;
}
this.portletDecorator=_113;
}else{
if(_112&&_112.match(_10d)){
if(_112=="short-title"){
_112="shortTitle";
}
this[_112]=((_111&&_111.firstChild)?_111.firstChild.nodeValue:null);
}else{
if(_112=="action"){
this._parsePSMLAction(_111,_10f);
}
}
}
}
}
this.actions=_10f;
if(_10e==null){
_109.raise("root frag");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_108.debug.ajaxPageNav){
_109.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_108.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _115=this.getPageUrl();
_109.undo.browser.addToHistory({back:function(){
if(_108.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_115);
}
_108.updatePage(_115,true);
},forward:function(){
if(_108.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_115);
}
_108.updatePage(_115,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_108.prefs.ajaxPageNavigation){
var _115=this.getPageUrl();
_109.undo.browser.setInitialState({back:function(){
if(_108.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_115);
}
_108.updatePage(_115,true);
},forward:function(){
if(_108.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_115);
}
_108.updatePage(_115,true);
},changeUrl:escape(this.getPath())});
}
}
var _116=this._parsePSMLFrag(_10e,0,false);
return _116;
},_parsePSMLFrag:function(_117,_118,_119){
var _11a=jetspeed;
var _11b=new Array();
var _11c=((_117!=null)?_117.getAttribute("type"):null);
if(_11c!="layout"){
dojo.raise("!layout frag="+_117);
return null;
}
if(!_119){
var _11d=_117.getAttribute("name");
if(_11d!=null){
_11d=_11d.toLowerCase();
if(_11d.indexOf("noactions")!=-1){
_119=true;
}
}
}
var _11e=null,_11f=0;
var _120={};
var _121=_117.childNodes;
var _122,_123,_124,_125,_126;
for(var i=0;i<_121.length;i++){
_122=_121[i];
if(_122.nodeType!=1){
continue;
}
_123=_122.nodeName;
if(_123=="fragment"){
_126=_122.getAttribute("type");
if(_126=="layout"){
var _128=this._parsePSMLFrag(_122,i,_119);
if(_128!=null){
_11b.push(_128);
}
}else{
var _129=this._parsePSMLProps(_122,null);
var _12a=_129[_11a.id.PP_WINDOW_ICON];
if(_12a==null||_12a.length==0){
_12a=this._parsePSMLChildOrAttr(_122,"icon");
if(_12a!=null&&_12a.length>0){
_129[_11a.id.PP_WINDOW_ICON]=_12a;
}
}
_11b.push({id:_122.getAttribute("id"),type:_126,name:_122.getAttribute("name"),properties:_129,actions:this._parsePSMLActions(_122,null),currentActionState:this._parsePSMLChildOrAttr(_122,"state"),currentActionMode:this._parsePSMLChildOrAttr(_122,"mode"),decorator:_122.getAttribute("decorator"),layoutActionsDisabled:_119,documentOrderIndex:i});
}
}else{
if(_123=="property"){
if(this._parsePSMLProp(_122,_120)=="sizes"){
if(_11e!=null){
dojo.raise("<sizes>: "+_117);
return null;
}
if(_11a.prefs.printModeOnly!=null){
_11e=["100"];
_11f=100;
}else{
_125=_122.getAttribute("value");
if(_125!=null&&_125.length>0){
_11e=_125.split(",");
for(var j=0;j<_11e.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_11e[j]=_11e[j].replace(re,"$1");
_11f+=new Number(_11e[j]);
}
}
}
}
}
}
}
if(_11e==null){
_11e=["100"];
_11f=100;
}
var _12d=_11e.length;
var _12e=_11b.length;
var pCi=_11a.id.PP_COLUMN;
var pRi=_11a.id.PP_ROW;
var _131=new Array(_12d);
var _132=new Array(_12d);
for(var cI=0;cI<_12d;cI++){
_131[cI]=[];
_132[cI]={head:-1,tail:-1,high:-1};
}
for(var _134=0;_134<_12e;_134++){
var frag=_11b[_134];
var _136=frag.properties;
var col=_136[pCi];
var row=_136[pRi];
var _139=null;
if(col==null||col>=_12d){
_139=_12d-1;
}else{
if(col<0){
_139=0;
}
}
if(_139!=null){
col=_136[pCi]=String(_139);
}
var ll=_131[col];
var _13b=ll.length;
var _13c=_132[col];
if(row<0){
row=_136[pRi]=0;
}else{
if(row==null){
row=_13c.high+1;
}
}
var _13d={i:_134,row:row,next:-1};
ll.push(_13d);
if(_13b==0){
_13c.head=_13c.tail=0;
_13c.high=row;
}else{
if(row>_13c.high){
ll[_13c.tail].next=_13b;
_13c.high=row;
_13c.tail=_13b;
}else{
var _13e=_13c.head;
var _13f=-1;
while(ll[_13e].row<row){
_13f=_13e;
_13e=ll[_13e].next;
}
if(ll[_13e].row==row){
var _140=new Number(row)+1;
ll[_13e].row=_140;
if(_13c.tail==_13e){
_13c.high=_140;
}
}
_13d.next=_13e;
if(_13f==-1){
_13c.head=_13b;
}else{
ll[_13f].next=_13b;
}
}
}
}
var _141=new Array(_12e);
var _142=0;
for(var cI=0;cI<_12d;cI++){
var ll=_131[cI];
var _13c=_132[cI];
var _143=0;
var _144=_13c.head;
while(_144!=-1){
var _13d=ll[_144];
var frag=_11b[_13d.i];
_141[_142]=frag;
frag.properties[pRi]=_143;
_142++;
_143++;
_144=_13d.next;
}
}
return {id:_117.getAttribute("id"),type:_11c,name:_117.getAttribute("name"),decorator:_117.getAttribute("decorator"),columnSizes:_11e,columnSizesSum:_11f,properties:_120,fragments:_141,layoutActionsDisabled:_119,documentOrderIndex:_118};
},_parsePSMLActions:function(_145,_146){
if(_146==null){
_146={};
}
var _147=_145.getElementsByTagName("action");
for(var _148=0;_148<_147.length;_148++){
var _149=_147[_148];
this._parsePSMLAction(_149,_146);
}
return _146;
},_parsePSMLAction:function(_14a,_14b){
var _14c=_14a.getAttribute("id");
if(_14c!=null){
var _14d=_14a.getAttribute("type");
var _14e=_14a.getAttribute("name");
var _14f=_14a.getAttribute("url");
var _150=_14a.getAttribute("alt");
_14b[_14c.toLowerCase()]={id:_14c,type:_14d,label:_14e,url:_14f,alt:_150};
}
},_parsePSMLChildOrAttr:function(_151,_152){
var _153=null;
var _154=_151.getElementsByTagName(_152);
if(_154!=null&&_154.length==1&&_154[0].firstChild!=null){
_153=_154[0].firstChild.nodeValue;
}
if(!_153){
_153=_151.getAttribute(_152);
}
if(_153==null||_153.length==0){
_153=null;
}
return _153;
},_parsePSMLProps:function(_155,_156){
if(_156==null){
_156={};
}
var _157=_155.getElementsByTagName("property");
for(var _158=0;_158<_157.length;_158++){
this._parsePSMLProp(_157[_158],_156);
}
return _156;
},_parsePSMLProp:function(_159,_15a){
var _15b=_159.getAttribute("name");
var _15c=_159.getAttribute("value");
_15a[_15b]=_15c;
return _15b;
},_layoutCreateModel:function(_15d,_15e,_15f,_160,_161,_162,_163,_164){
var jsId=_164.id;
var _166=this.columns.length;
var _167=this._layoutCreateColsModel(_15d,_15e,_15f,_161);
var _168=_167.columnsInLayout;
if(_167.addedLayoutHeaderColumn){
_166++;
}
var _169=(_168==null?0:_168.length);
var _16a=new Array(_169);
var _16b=new Array(_169);
for(var i=0;i<_15d.fragments.length;i++){
var _16d=_15d.fragments[i];
if(_16d.type=="layout"){
var _16e=i;
var _16e=(_16d.properties?_16d.properties[_164.id.PP_COLUMN]:i);
if(_16e==null||_16e<0||_16e>=_169){
_16e=(_169>0?(_169-1):0);
}
_16b[_16e]=true;
this._layoutCreateModel(_16d,(_15e+1),_168[_16e],_160,false,_162,_163,_164);
}else{
this._layoutCreatePortlet(_16d,_15d,_168,_166,_160,_16a,_162,_163,_164);
}
}
return _168;
},_layoutCreatePortlet:function(_16f,_170,_171,_172,_173,_174,_175,_176,_177){
if(_16f&&_177.debugPortletEntityIdFilter){
if(!_176.lang.inArray(_177.debugPortletEntityIdFilter,_16f.id)){
_16f=null;
}
}
if(_16f){
var _178="z";
var _179=_16f.properties[_177.id.PP_DESKTOP_EXTENDED];
var _17a=_177.prefs.windowTiling;
var _17b=_17a;
var _17c=_177.prefs.windowHeightExpand;
if(_179!=null&&_17a&&_177.prefs.printModeOnly==null){
var _17d=_179.split(_177.id.PP_PAIR_SEPARATOR);
var _17e=null,_17f=0,_180=null,_181=null,_182=false;
if(_17d!=null&&_17d.length>0){
var _183=_177.id.PP_PROP_SEPARATOR;
for(var _184=0;_184<_17d.length;_184++){
_17e=_17d[_184];
_17f=((_17e!=null)?_17e.length:0);
if(_17f>0){
var _185=_17e.indexOf(_183);
if(_185>0&&_185<(_17f-1)){
_180=_17e.substring(0,_185);
_181=_17e.substring(_185+1);
_182=((_181=="true")?true:false);
if(_180==_177.id.PP_STATICPOS){
_17b=_182;
}else{
if(_180==_177.id.PP_FITHEIGHT){
_17c=_182;
}
}
}
}
}
}
}else{
if(!_17a){
_17b=false;
}
}
_16f.properties[_177.id.PP_WINDOW_POSITION_STATIC]=_17b;
_16f.properties[_177.id.PP_WINDOW_HEIGHT_TO_FIT]=_17c;
if(_17b&&_17a){
var _186=_171.length;
var _187=_16f.properties[_177.id.PP_COLUMN];
if(_187==null||_187>=_186){
_187=_186-1;
}else{
if(_187<0){
_187=0;
}
}
if(_174[_187]==null){
_174[_187]=new Array();
}
_174[_187].push(_16f.id);
var _188=_172+new Number(_187);
_178=_188.toString();
}
if(_16f.currentActionState==_177.id.ACT_MAXIMIZE){
this.maximizedOnInit=_16f.id;
}
var _189=_16f.decorator;
if(_189!=null&&_189.length>0){
if(_176.lang.indexOf(_177.prefs.portletDecorationsAllowed,_189)==-1){
_189=null;
}
}
if(_189==null||_189.length==0){
if(djConfig.isDebug&&_177.debug.windowDecorationRandom){
_189=_177.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_177.prefs.portletDecorationsAllowed.length)];
}else{
_189=this.portletDecorator;
}
}
var _18a=_16f.properties||{};
_18a[_177.id.PP_WINDOW_DECORATION]=_189;
_175[_189]=true;
var _18b=_16f.actions||{};
var _18c=new _177.om.Portlet(_16f.name,_16f.id,null,_18a,_18b,_16f.currentActionState,_16f.currentActionMode,_16f.layoutActionsDisabled);
_18c.initialize();
this.putPortlet(_18c);
if(_173[_178]==null){
_173[_178]=new Array();
}
_173[_178].push({portlet:_18c,layout:_170.id});
}
},_layoutCreateColsModel:function(_18d,_18e,_18f,_190){
var _191=jetspeed;
this.layouts[_18d.id]=_18d;
var _192=false;
var _193=new Array();
if(_191.prefs.windowTiling&&_18d.columnSizes.length>0){
var _194=false;
if(_191.UAie){
_194=true;
}
if(_18f!=null&&!_190){
var _195=new _191.om.Column(0,_18d.id,(_194?_18d.columnSizesSum-0.1:_18d.columnSizesSum),this.columns.length,_18d.layoutActionsDisabled,_18e);
_195.layoutHeader=true;
this.columns.push(_195);
if(_18f.buildColChildren==null){
_18f.buildColChildren=new Array();
}
_18f.buildColChildren.push(_195);
_18f=_195;
_192=true;
}
for(var i=0;i<_18d.columnSizes.length;i++){
var size=_18d.columnSizes[i];
if(_194&&i==(_18d.columnSizes.length-1)){
size=size-0.1;
}
var _198=new _191.om.Column(i,_18d.id,size,this.columns.length,_18d.layoutActionsDisabled);
this.columns.push(_198);
if(_18f!=null){
if(_18f.buildColChildren==null){
_18f.buildColChildren=new Array();
}
_18f.buildColChildren.push(_198);
}
_193.push(_198);
}
}
return {columnsInLayout:_193,addedLayoutHeaderColumn:_192};
},_portletsInitWinState:function(_199){
var _19a={};
this.getPortletCurColRow(null,false,_19a);
for(var _19b in this.portlets){
var _19c=this.portlets[_19b];
var _19d=_19a[_19c.getId()];
if(_19d==null&&_199){
for(var i=0;i<_199.length;i++){
if(_199[i].portlet.getId()==_19c.getId()){
_19d={layout:_199[i].layout};
break;
}
}
}
if(_19d!=null){
_19c._initWinState(_19d,false);
}else{
dojo.raise("Window state data not found for portlet: "+_19c.getId());
}
}
},_loadPortletZIndexCompare:function(_19f,_1a0){
var _1a1=null;
var _1a2=null;
var _1a3=null;
_1a1=_19f.portlet._getInitialZIndex();
_1a2=_1a0.portlet._getInitialZIndex();
if(_1a1&&!_1a2){
return -1;
}else{
if(_1a2&&!_1a1){
return 1;
}else{
if(_1a1==_1a2){
return 0;
}
}
}
return (_1a1-_1a2);
},_createColsStart:function(_1a4,_1a5){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _1a6=document.createElement("div");
_1a6.id=_1a5;
_1a6.setAttribute("id",_1a5);
for(var _1a7=0;_1a7<this.columnsStructure.length;_1a7++){
var _1a8=this.columnsStructure[_1a7];
this._createCols(_1a8,_1a6);
}
_1a4.appendChild(_1a6);
},_createCols:function(_1a9,_1aa){
_1a9.createColumn();
if(this.colFirstNormI==-1&&!_1a9.columnContainer&&!_1a9.layoutHeader){
this.colFirstNormI=_1a9.getPageColumnIndex();
}
var _1ab=_1a9.buildColChildren;
if(_1ab!=null&&_1ab.length>0){
for(var _1ac=0;_1ac<_1ab.length;_1ac++){
this._createCols(_1ab[_1ac],_1a9.domNode);
}
}
delete _1a9.buildColChildren;
_1aa.appendChild(_1a9.domNode);
},_removeCols:function(_1ad){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_1ad){
var _1af=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_1af,function(_1b0){
_1ad.appendChild(_1b0);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _1b1=dojo.byId(jetspeed.id.COLUMNS);
if(_1b1){
dojo.dom.removeNode(_1b1);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnsEmptyCheck:function(_1b2){
var _1b3=null;
if(_1b2==null){
return _1b3;
}
var _1b4=_1b2.childNodes,_1b5;
if(_1b4){
for(var i=0;i<_1b4.length;i++){
_1b5=_1b4[i];
var _1b7=this.columnEmptyCheck(_1b5,true);
if(_1b7!=null){
_1b3=_1b7;
if(_1b3==false){
break;
}
}
}
}
return _1b3;
},columnEmptyCheck:function(_1b8,_1b9){
var _1ba=null;
if(!_1b8||!_1b8.getAttribute){
return _1ba;
}
var _1bb=_1b8.getAttribute("columnindex");
if(!_1bb||_1bb.length==0){
return _1ba;
}
var _1bc=_1b8.getAttribute("layoutid");
if(_1bc==null||_1bc.length==0){
var _1bd=_1b8.childNodes;
_1ba=(!_1bd||_1bd.length==0);
if(!_1b9){
_1b8.style.height=(_1ba?"1px":"");
}
}
return _1ba;
},getPortletCurColRow:function(_1be,_1bf,_1c0){
if(!this.columns||this.columns.length==0){
return null;
}
var _1c1=null;
var _1c2=((_1be!=null)?true:false);
var _1c3=0;
var _1c4=null;
var _1c5=null;
var _1c6=0;
var _1c7=false;
for(var _1c8=0;_1c8<this.columns.length;_1c8++){
var _1c9=this.columns[_1c8];
var _1ca=_1c9.domNode.childNodes;
if(_1c5==null||_1c5!=_1c9.getLayoutId()){
_1c5=_1c9.getLayoutId();
_1c4=this.layouts[_1c5];
if(_1c4==null){
dojo.raise("Layout not found: "+_1c5);
return null;
}
_1c6=0;
_1c7=false;
if(_1c4.clonedFromRootId==null){
_1c7=true;
}else{
var _1cb=this.getColFromColNode(_1c9.domNode.parentNode);
if(_1cb==null){
dojo.raise("Parent column not found: "+_1c9);
return null;
}
_1c9=_1cb;
}
}
var _1cc=null;
var _1cd=jetspeed;
var _1ce=dojo;
var _1cf=_1cd.id.PWIN_CLASS;
if(_1bf){
_1cf+="|"+_1cd.id.PWIN_GHOST_CLASS;
}
if(_1c2){
_1cf+="|"+_1cd.id.COL_CLASS;
}
var _1d0=new RegExp("(^|\\s+)("+_1cf+")(\\s+|$)");
for(var _1d1=0;_1d1<_1ca.length;_1d1++){
var _1d2=_1ca[_1d1];
if(_1d0.test(_1ce.html.getClass(_1d2))){
_1cc=(_1cc==null?0:_1cc+1);
if((_1cc+1)>_1c6){
_1c6=(_1cc+1);
}
if(_1be==null||_1d2==_1be){
var _1d3={layout:_1c5,column:_1c9.getLayoutColumnIndex(),row:_1cc,columnObj:_1c9};
if(!_1c7){
_1d3.layout=_1c4.clonedFromRootId;
}
if(_1be!=null){
_1c1=_1d3;
break;
}else{
if(_1c0!=null){
var _1d4=this.getPWinFromNode(_1d2);
if(_1d4==null){
_1ce.raise("PortletWindow not found for node");
}else{
var _1d5=_1d4.portlet;
if(_1d5==null){
_1ce.raise("PortletWindow for node has null portlet: "+_1d4.widgetId);
}else{
_1c0[_1d5.getId()]=_1d3;
}
}
}
}
}
}
}
if(_1c1!=null){
break;
}
}
return _1c1;
},_getPortletArrayByZIndex:function(){
var _1d6=jetspeed;
var _1d7=this.getPortletArray();
if(!_1d7){
return _1d7;
}
var _1d8=[];
for(var i=0;i<_1d7.length;i++){
if(!_1d7[i].getProperty(_1d6.id.PP_WINDOW_POSITION_STATIC)){
_1d8.push(_1d7[i]);
}
}
_1d8.sort(this._portletZIndexCompare);
return _1d8;
},_portletZIndexCompare:function(_1da,_1db){
var _1dc=null;
var _1dd=null;
var _1de=null;
_1de=_1da.getSavedWinState();
_1dc=_1de.zIndex;
_1de=_1db.getSavedWinState();
_1dd=_1de.zIndex;
if(_1dc&&!_1dd){
return -1;
}else{
if(_1dd&&!_1dc){
return 1;
}else{
if(_1dc==_1dd){
return 0;
}
}
}
return (_1dc-_1dd);
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
var _1f1=[];
for(var _1f2 in this.portlets){
var _1f3=this.portlets[_1f2];
_1f1.push(_1f3);
}
return _1f1;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _1f4=[];
for(var _1f5 in this.portlets){
var _1f6=this.portlets[_1f5];
_1f4.push(_1f6.getId());
}
return _1f4;
},getPortletByName:function(_1f7){
if(this.portlets&&_1f7){
for(var _1f8 in this.portlets){
var _1f9=this.portlets[_1f8];
if(_1f9.name==_1f7){
return _1f9;
}
}
}
return null;
},getPortlet:function(_1fa){
if(this.portlets&&_1fa){
return this.portlets[_1fa];
}
return null;
},getPWinFromNode:function(_1fb){
var _1fc=null;
if(this.portlets&&_1fb){
for(var _1fd in this.portlets){
var _1fe=this.portlets[_1fd];
var _1ff=_1fe.getPWin();
if(_1ff!=null){
if(_1ff.domNode==_1fb){
_1fc=_1ff;
break;
}
}
}
}
return _1fc;
},putPortlet:function(_200){
if(!_200){
return;
}
if(!this.portlets){
this.portlets={};
}
this.portlets[_200.entityId]=_200;
this.portlet_count++;
},putPWin:function(_201){
if(!_201){
return;
}
var _202=_201.widgetId;
if(!_202){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_202]=_201;
this.portlet_window_count++;
},getPWin:function(_203){
if(this.portlet_windows&&_203){
var pWin=this.portlet_windows[_203];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_203];
if(pWin==null){
var p=this.getPortlet(_203);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_207){
var _208=this.portlet_windows;
var pWin;
var _20a=[];
for(var _20b in _208){
pWin=_208[_20b];
if(pWin&&(!_207||pWin.portlet)){
_20a.push(pWin);
}
}
return _20a;
},getPWinTopZIndex:function(_20c){
var _20d=0;
if(_20c){
_20d=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_20d;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_20d=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_20d;
}
return _20d;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_20e,_20f){
return;
},onBrowserWindowResize:function(){
var _210=jetspeed;
var _211=this.portlet_windows;
var pWin;
for(var _213 in _211){
pWin=_211[_213];
pWin.onBrowserWindowResize();
}
if(_210.UAie6&&this.editMode){
var _214=dojo.widget.byId(_210.id.PG_ED_WID);
if(_214!=null){
_214.onBrowserWindowResize();
}
}
},regPWinIFrameCover:function(_215){
if(!_215){
return;
}
this.iframeCoverByWinId[_215.widgetId]=true;
},unregPWinIFrameCover:function(_216){
if(!_216){
return;
}
delete this.iframeCoverByWinId[_216.widgetId];
},displayAllPWinIFrameCovers:function(_217,_218){
var _219=this.portlet_windows;
var _21a=this.iframeCoverByWinId;
if(!_219||!_21a){
return;
}
for(var _21b in _21a){
if(_21b==_218){
continue;
}
var pWin=_219[_21b];
var _21d=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_21d){
_21d.style.display=(_217?"none":"block");
}
}
},createLayoutInfo:function(_21e){
var _21f=dojo;
var _220=null;
var _221=null;
var _222=null;
var _223=null;
var _224=document.getElementById(_21e.id.DESKTOP);
if(_224!=null){
_220=_21e.ui.getLayoutExtents(_224,null,_21f,_21e);
}
var _225=document.getElementById(_21e.id.COLUMNS);
if(_225!=null){
_221=_21e.ui.getLayoutExtents(_225,null,_21f,_21e);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_223=_21e.ui.getLayoutExtents(col.domNode,null,_21f,_21e);
}else{
if(!col.columnContainer){
_222=_21e.ui.getLayoutExtents(col.domNode,null,_21f,_21e);
}
}
if(_222!=null&&_223!=null){
break;
}
}
}
this.layoutInfo={desktop:(_220!=null?_220:{}),columns:(_221!=null?_221:{}),column:(_222!=null?_222:{}),columnLayoutHeader:(_223!=null?_223:{})};
_21e.widget.PortletWindow.prototype.colWidth_pbE=((_222&&_222.pbE)?_222.pbE.w:0);
},_beforeAddOnLoad:function(){
this.win_onload=true;
},destroy:function(){
var _228=jetspeed;
var _229=dojo;
_228.ui.evtDisconnect("after",window,"onresize",_228.ui.windowResizeMgr,"onResize",_229.event);
_228.ui.evtDisconnect("before",_229,"addOnLoad",this,"_beforeAddOnLoad",_229.event);
var _22a=this.portlet_windows;
var _22b=this.getPWins(true);
var pWin,_22d;
for(var i=0;i<_22b.length;i++){
pWin=_22b[i];
_22d=pWin.widgetId;
pWin.closeWindow();
delete _22a[_22d];
this.portlet_window_count--;
}
this.portlets={};
this.portlet_count=0;
var _22f=_229.widget.byId(_228.id.PG_ED_WID);
if(_22f!=null){
_22f.editPageDestroy();
}
this._removeCols(document.getElementById(_228.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_230){
if(_230==null){
return null;
}
var _231=_230.getAttribute("columnindex");
if(_231==null){
return null;
}
var _232=new Number(_231);
if(_232>=0&&_232<this.columns.length){
return this.columns[_232];
}
return null;
},getColIndexForNode:function(node){
var _234=null;
if(!this.columns){
return _234;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_234=i;
break;
}
}
return _234;
},getColWithNode:function(node){
var _237=this.getColIndexForNode(node);
return ((_237!=null&&_237>=0)?this.columns[_237]:null);
},getDescendantCols:function(_238){
var dMap={};
if(_238==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_238&&_238.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_23c){
if(!_23c){
return;
}
var _23d=(_23c.getName?_23c.getName():null);
if(_23d!=null){
this.menus[_23d]=_23c;
}
},getMenu:function(_23e){
if(_23e==null){
return null;
}
return this.menus[_23e];
},removeMenu:function(_23f){
if(_23f==null){
return;
}
var _240=null;
if(dojo.lang.isString(_23f)){
_240=_23f;
}else{
_240=(_23f.getName?_23f.getName():null);
}
if(_240!=null){
delete this.menus[_240];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _241=[];
for(var _242 in this.menus){
_241.push(_242);
}
return _241;
},retrieveMenuDeclarations:function(_243,_244,_245){
contentListener=new jetspeed.om.MenusApiCL(_243,_244,_245);
this.clearMenus();
var _246="?action=getmenus";
if(_243){
_246+="&includeMenuDefs=true";
}
var _247=this.getPsmlUrl()+_246;
var _248="text/xml";
var _249=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_247,mimetype:_248},contentListener,_249,jetspeed.debugContentDumpIds);
},syncPageControls:function(_24a){
var jsId=_24a.id;
if(this.actionButtons==null){
return;
}
for(var _24c in this.actionButtons){
var _24d=false;
if(_24c==jsId.ACT_EDIT){
if(!this.editMode){
_24d=true;
}
}else{
if(_24c==jsId.ACT_VIEW){
if(this.editMode){
_24d=true;
}
}else{
if(_24c==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_24d=true;
}
}else{
_24d=true;
}
}
}
if(_24d){
this.actionButtons[_24c].style.display="";
}else{
this.actionButtons[_24c].style.display="none";
}
}
},renderPageControls:function(_24e){
var _24e=jetspeed;
var _24f=_24e.page;
var jsId=_24e.id;
var _251=dojo;
var _252=[];
if(this.actions!=null){
var addP=false;
for(var _254 in this.actions){
if(_254!=jsId.ACT_HELP){
_252.push(_254);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
addP=true;
if(this.actions[jsId.ACT_VIEW]==null){
_252.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
addP=true;
if(this.actions[jsId.ACT_EDIT]==null){
_252.push(jsId.ACT_EDIT);
}
}
var _255=(_24f.rootFragmentId?_24f.layouts[_24f.rootFragmentId]:null);
var _256=(!(_255==null||_255.layoutActionsDisabled));
if(_256){
_256=_24f._perms(_24e.prefs,_24e.id.PM_P_AD,String.fromCharCode);
if(_256&&!this.isUA()&&(addP||_24f.canNPE())){
_252.push(jsId.ACT_ADDPORTLET);
}
}
}
var _257=_251.byId(jsId.PAGE_CONTROLS);
if(_257!=null&&_252!=null&&_252.length>0){
var _258=_24e.prefs;
var jsUI=_24e.ui;
var _25a=_251.event;
var _25b=_24f.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _25c=this.actionButtonTooltips;
for(var i=0;i<_252.length;i++){
var _254=_252[i];
var _25e=document.createElement("div");
_25e.className="portalPageActionButton";
_25e.style.backgroundImage="url("+_258.getLayoutRootUrl()+"/images/desktop/"+_254+".gif)";
_25e.actionName=_254;
this.actionButtons[_254]=_25e;
_257.appendChild(_25e);
jsUI.evtConnect("after",_25e,"onclick",this,"pageActionButtonClick",_25a);
if(_258.pageActionButtonTooltip){
var _25f=null;
if(_258.desktopActionLabels!=null){
_25f=_258.desktopActionLabels[_254];
}
if(_25f==null||_25f.length==0){
_25f=_251.string.capitalize(_254);
}
_25c.push(_25b.addNode(_25e,_25f,true,null,null,null,_24e,jsUI,_25a));
}
}
}
},_destroyPageControls:function(){
var _260=jetspeed;
if(this.actionButtons){
for(var _261 in this.actionButtons){
var _262=this.actionButtons[_261];
if(_262){
_260.ui.evtDisconnect("after",_262,"onclick",this,"pageActionButtonClick");
}
}
}
var _263=dojo.byId(_260.id.PAGE_CONTROLS);
if(_263!=null&&_263.childNodes&&_263.childNodes.length>0){
for(var i=(_263.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_263.childNodes[i]);
}
}
_260.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_266){
var _267=jetspeed;
if(_266==null){
return;
}
if(_266==_267.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_266==_267.id.ACT_EDIT){
_267.changeActionForPortlet(this.rootFragmentId,null,_267.id.ACT_EDIT,new _267.om.PageChangeActionCL());
_267.editPageInitiate(_267);
}else{
if(_266==_267.id.ACT_VIEW){
_267.editPageTerminate(_267);
}else{
var _268=this.getPageAction(_266);
if(_268==null){
return;
}
if(_268.url==null){
return;
}
var _269=_267.url.basePortalUrl()+_267.url.path.DESKTOP+"/"+_268.url;
_267.pageNavigate(_269);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_26b,_26c){
var _26d=jetspeed;
var jsId=_26d.id;
if(!_26c){
_26c=escape(this.getPagePathAndQuery());
}else{
_26c=escape(_26c);
}
var _26f=_26d.url.basePortalUrl()+_26d.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_26c;
if(_26b!=null){
_26f+="&jslayoutid="+escape(_26b);
}
if(!this.editMode){
_26f+="&"+_26d.id.ADDP_RFRAG+"="+escape(this.rootFragmentId);
}
if(this.actions&&(this.actions[jsId.ACT_EDIT]||this.actions[jsId.ACT_VIEW])){
_26d.changeActionForPortlet(this.rootFragmentId,null,jsId.ACT_EDIT,new _26d.om.PageChangeActionCL(_26f));
}else{
if(!this.isUA()){
_26d.pageNavigate(_26f);
}
}
},addPortletTerminate:function(_270,_271){
var _272=jetspeed;
var _273=_272.url.getQueryParameter(document.location.href,_272.id.ADDP_RFRAG);
if(_273!=null&&_273.length>0){
var _274=_271;
var qPos=_271.indexOf("?");
if(qPos>0){
_274.substring(0,qPos);
}
_272.changeActionForPortlet(_273,null,_272.id.ACT_VIEW,new _272.om.PageChangeActionCL(_270),_274);
}else{
_272.pageNavigate(_270);
}
},setPageModePortletActions:function(_276){
if(_276==null||_276.actions==null){
return;
}
var jsId=jetspeed.id;
if(_276.actions[jsId.ACT_REMOVEPORTLET]==null){
_276.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_278){
if(this.pageUrl!=null&&!_278){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _27a=jsU.path.SERVER+((_278)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _27b=jsU.parse(_27a);
var _27c=null;
if(this.pageUrlFallback!=null){
_27c=jsU.parse(this.pageUrlFallback);
}else{
_27c=jsU.parse(window.location.href);
}
if(_27b!=null&&_27c!=null){
var _27d=_27c.query;
if(_27d!=null&&_27d.length>0){
var _27e=_27b.query;
if(_27e!=null&&_27e.length>0){
_27a=_27a+"&"+_27d;
}else{
_27a=_27a+"?"+_27d;
}
}
}
if(!_278){
this.pageUrl=_27a;
}
return _27a;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _280=this.getPath();
var _281=jsU.parse(_280);
var _282=null;
if(this.pageUrlFallback!=null){
_282=jsU.parse(this.pageUrlFallback);
}else{
_282=jsU.parse(window.location.href);
}
if(_281!=null&&_282!=null){
var _283=_282.query;
if(_283!=null&&_283.length>0){
var _284=_281.query;
if(_284!=null&&_284.length>0){
_280=_280+"&"+_283;
}else{
_280=_280+"?"+_283;
}
}
}
this.pagePathAndQuery=_280;
return _280;
},getPageDirectory:function(_285){
var _286="/";
var _287=(_285?this.getRealPath():this.getPath());
if(_287!=null){
var _288=_287.lastIndexOf("/");
if(_288!=-1){
if((_288+1)<_287.length){
_286=_287.substring(0,_288+1);
}else{
_286=_287;
}
}
}
return _286;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_28a){
if(!_28a){
_28a="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_28a)){
return jsU.path.SERVER+jsU.path.DESKTOP+_28a;
}
return _28a;
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
jetspeed.om.Column=function(_28c,_28d,size,_28f,_290,_291){
this.layoutColumnIndex=_28c;
this.layoutId=_28d;
this.size=size;
this.pageColumnIndex=new Number(_28f);
if(typeof _290!="undefined"){
this.layoutActionsDisabled=_290;
}
if((typeof _291!="undefined")&&_291!=null){
this.layoutDepth=_291;
}
this.id="jscol_"+_28f;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,layoutDepth:null,layoutMaxChildDepth:0,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_292){
var _293=this.styleClass;
var _294=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_294>0){
_293+=" desktopColumnClear-PRIVATE";
}
var _295=document.createElement("div");
_295.setAttribute("columnindex",_294);
_295.style.width=this.size+"%";
if(this.layoutHeader){
_293=this.styleLayoutClass;
_295.setAttribute("layoutid",this.layoutId);
}
_295.className=_293;
_295.id=this.getId();
this.domNode=_295;
if(_292!=null){
_292.appendChild(_295);
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
},_updateLayoutDepth:function(_298){
var _299=this.layoutDepth;
if(_299!=null&&_298!=_299){
this.layoutDepth=_298;
this.layoutDepthChanged();
}
},_updateLayoutChildDepth:function(_29a){
this.layoutMaxChildDepth=(_29a==null?0:_29a);
}});
jetspeed.om.Portlet=function(_29b,_29c,_29d,_29e,_29f,_2a0,_2a1,_2a2){
this.name=_29b;
this.entityId=_29c;
this.properties=_29e;
this.actions=_29f;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_2a0;
this.currentActionMode=_2a1;
if(_29d){
this.contentRetriever=_29d;
}
this.layoutActionsDisabled=false;
if(typeof _2a2!="undefined"){
this.layoutActionsDisabled=_2a2;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _2a3=jetspeed;
var jsId=_2a3.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _2a5=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_2a3.prefs.windowTiling){
if(_2a5=="true"){
_2a5=true;
}else{
if(_2a5=="false"){
_2a5=false;
}else{
if(_2a5!=true&&_2a5!=false){
_2a5=true;
}
}
}
}else{
_2a5=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_2a5;
var _2a6=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_2a6=="true"){
_2a6=true;
}else{
if(_2a5=="false"){
_2a6=false;
}else{
if(_2a6!=true&&_2a6!=false){
_2a6=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2a6;
var _2a7=this.properties[jsId.PP_WINDOW_TITLE];
if(!_2a7&&this.name){
var re=(/^[^:]*:*/);
_2a7=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_2a7;
}
},postParseAnnotateHtml:function(_2a9){
var _2aa=jetspeed;
var _2ab=_2aa.portleturl;
if(_2a9){
var _2ac=_2a9;
var _2ad=_2ac.getElementsByTagName("form");
var _2ae=_2aa.debug.postParseAnnotateHtml;
var _2af=_2aa.debug.postParseAnnotateHtmlDisableAnchors;
if(_2ad){
for(var i=0;i<_2ad.length;i++){
var _2b1=_2ad[i];
var _2b2=_2b1.action;
var _2b3=_2ab.parseContentUrl(_2b2);
var op=_2b3.operation;
var _2b5=(op==_2ab.PORTLET_REQUEST_ACTION||op==_2ab.PORTLET_REQUEST_RENDER);
var _2b6=false;
if(dojo.io.formHasFile(_2b1)){
if(_2b5){
var _2b7=_2aa.url.parse(_2b2);
_2b7=_2aa.url.addQueryParameter(_2b7,"encoder","desktop",true);
_2b7=_2aa.url.addQueryParameter(_2b7,"jsdajax","false",true);
_2b1.action=_2b7.toString();
}else{
_2b6=true;
}
}else{
if(_2b5){
var _2b8=_2ab.genPseudoUrl(_2b3,true);
_2b1.action=_2b8;
var _2b9=new _2aa.om.ActionRenderFormBind(_2b1,_2b3.url,_2b3.portletEntityId,op);
if(_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+op+") for form with action: "+_2b2);
}
}else{
if(_2b2==null||_2b2.length==0){
var _2b9=new _2aa.om.ActionRenderFormBind(_2b1,null,this.entityId,null);
if(_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
_2b6=true;
}
}
}
if(_2b6&&_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_2b2);
}
}
}
var _2ba=_2ac.getElementsByTagName("a");
if(_2ba){
for(var i=0;i<_2ba.length;i++){
var _2bb=_2ba[i];
var _2bc=_2bb.href;
var _2b3=_2ab.parseContentUrl(_2bc);
var _2bd=null;
if(!_2af){
_2bd=_2ab.genPseudoUrl(_2b3);
}
if(!_2bd){
if(_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2bc);
}
}else{
if(_2bd==_2bc){
if(_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2bc);
}
}else{
if(_2ae){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2bc+" with: "+_2bd);
}
_2bb.href=_2bd;
}
}
}
}
}
},getPWin:function(){
var _2be=jetspeed;
var _2bf=this.properties[_2be.id.PP_WIDGET_ID];
if(_2bf){
return _2be.page.getPWin(_2bf);
}
return null;
},getCurWinState:function(_2c0){
var _2c1=null;
try{
var _2c2=this.getPWin();
if(!_2c2){
return null;
}
_2c1=_2c2.getCurWinStateForPersist(_2c0);
if(!_2c0){
if(_2c1.layout==null){
_2c1.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2c1;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2c3,_2c4){
var _2c5=jetspeed;
var jsId=_2c5.id;
if(!_2c3){
_2c3={};
}
var _2c7=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2c8=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2c3[jsId.PP_WINDOW_POSITION_STATIC]=_2c7;
_2c3[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2c8;
var _2c9=this.properties["width"];
if(!_2c4&&_2c9!=null&&_2c9>0){
_2c3.width=Math.floor(_2c9);
}else{
if(_2c4){
_2c3.width=-1;
}
}
var _2ca=this.properties["height"];
if(!_2c4&&_2ca!=null&&_2ca>0){
_2c3.height=Math.floor(_2ca);
}else{
if(_2c4){
_2c3.height=-1;
}
}
if(!_2c7||!_2c5.prefs.windowTiling){
var _2cb=this.properties["x"];
if(!_2c4&&_2cb!=null&&_2cb>=0){
_2c3.left=Math.floor(((_2cb>0)?_2cb:0));
}else{
if(_2c4){
_2c3.left=-1;
}
}
var _2cc=this.properties["y"];
if(!_2c4&&_2cc!=null&&_2cc>=0){
_2c3.top=Math.floor(((_2cc>0)?_2cc:0));
}else{
_2c3.top=-1;
}
var _2cd=this._getInitialZIndex(_2c4);
if(_2cd!=null){
_2c3.zIndex=_2cd;
}
}
return _2c3;
},_initWinState:function(_2ce,_2cf){
var _2d0=jetspeed;
var _2d1=(_2ce?_2ce:{});
this.getInitialWinDims(_2d1,_2cf);
if(_2d0.debug.initWinState){
var _2d2=this.properties[_2d0.id.PP_WINDOW_POSITION_STATIC];
if(!_2d2||!_2d0.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2d1.zIndex+" x="+_2d1.left+" y="+_2d1.top+" width="+_2d1.width+" height="+_2d1.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2d1.column+" row="+_2d1.row+" width="+_2d1.width+" height="+_2d1.height);
}
}
this.lastSavedWindowState=_2d1;
return _2d1;
},_getInitialZIndex:function(_2d3){
var _2d4=null;
var _2d5=this.properties["z"];
if(!_2d3&&_2d5!=null&&_2d5>=0){
_2d4=Math.floor(_2d5);
}else{
if(_2d3){
_2d4=-1;
}
}
return _2d4;
},_getChangedWindowState:function(_2d6){
var jsId=jetspeed.id;
var _2d8=this.getSavedWinState();
if(_2d8&&dojo.lang.isEmpty(_2d8)){
_2d8=null;
_2d6=false;
}
var _2d9=this.getCurWinState(_2d6);
var _2da=_2d9[jsId.PP_WINDOW_POSITION_STATIC];
var _2db=!_2da;
if(!_2d8){
var _2dc={state:_2d9,positionChanged:true,extendedPropChanged:true};
if(_2db){
_2dc.zIndexChanged=true;
}
return _2dc;
}
var _2dd=false;
var _2de=false;
var _2df=false;
var _2e0=false;
for(var _2e1 in _2d9){
if(_2d9[_2e1]!=_2d8[_2e1]){
if(_2e1==jsId.PP_WINDOW_POSITION_STATIC||_2e1==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2dd=true;
_2df=true;
_2de=true;
}else{
if(_2e1=="zIndex"){
if(_2db){
_2dd=true;
_2e0=true;
}
}else{
_2dd=true;
_2de=true;
}
}
}
}
if(_2dd){
var _2dc={state:_2d9,positionChanged:_2de,extendedPropChanged:_2df};
if(_2db){
_2dc.zIndexChanged=_2e0;
}
return _2dc;
}
return null;
},getPortletUrl:function(_2e2){
var _2e3=jetspeed;
var _2e4=_2e3.url;
var _2e5=null;
if(_2e2&&_2e2.url){
_2e5=_2e2.url;
}else{
if(_2e2&&_2e2.formNode){
var _2e6=_2e2.formNode.getAttribute("action");
if(_2e6){
_2e5=_2e6;
}
}
}
if(_2e5==null){
_2e5=_2e4.basePortalUrl()+_2e4.path.PORTLET+_2e3.page.getPath();
}
if(!_2e2.dontAddQueryArgs){
_2e5=_2e4.parse(_2e5);
_2e5=_2e4.addQueryParameter(_2e5,"entity",this.entityId,true);
_2e5=_2e4.addQueryParameter(_2e5,"portlet",this.name,true);
_2e5=_2e4.addQueryParameter(_2e5,"encoder","desktop",true);
if(_2e2.jsPageUrl!=null){
var _2e7=_2e2.jsPageUrl.query;
if(_2e7!=null&&_2e7.length>0){
_2e5=_2e5.toString()+"&"+_2e7;
}
}
}
if(_2e2){
_2e2.url=_2e5.toString();
}
return _2e5;
},_submitAjaxApi:function(_2e8,_2e9,_2ea){
var _2eb=jetspeed;
var _2ec="?action="+_2e8+"&id="+this.entityId+_2e9;
var _2ed=_2eb.url.basePortalUrl()+_2eb.url.path.AJAX_API+_2eb.page.getPath()+_2ec;
var _2ee="text/xml";
var _2ef=new _2eb.om.Id(_2e8,this.entityId);
_2ef.portlet=this;
_2eb.url.retrieveContent({url:_2ed,mimetype:_2ee},_2ea,_2ef,_2eb.debugContentDumpIds);
},submitWinState:function(_2f0,_2f1){
var _2f2=jetspeed;
var jsId=_2f2.id;
if(_2f2.page.isUA()||(!(_2f2.page.getPageAction(jsId.ACT_EDIT)||_2f2.page.getPageAction(jsId.ACT_VIEW)||_2f2.page.canNPE()))){
return;
}
var _2f4=null;
if(_2f1){
_2f4={state:this._initWinState(null,true)};
}else{
_2f4=this._getChangedWindowState(_2f0);
}
if(_2f4){
var _2f5=_2f4.state;
var _2f6=_2f5[jsId.PP_WINDOW_POSITION_STATIC];
var _2f7=_2f5[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _2f8=null;
if(_2f4.extendedPropChanged){
var _2f9=jsId.PP_PROP_SEPARATOR;
var _2fa=jsId.PP_PAIR_SEPARATOR;
_2f8=jsId.PP_STATICPOS+_2f9+_2f6.toString();
_2f8+=_2fa+jsId.PP_FITHEIGHT+_2f9+_2f7.toString();
_2f8=escape(_2f8);
}
var _2fb="";
var _2fc=null;
if(_2f6){
_2fc="moveabs";
if(_2f5.column!=null){
_2fb+="&col="+_2f5.column;
}
if(_2f5.row!=null){
_2fb+="&row="+_2f5.row;
}
if(_2f5.layout!=null){
_2fb+="&layoutid="+_2f5.layout;
}
if(_2f5.height!=null){
_2fb+="&height="+_2f5.height;
}
}else{
_2fc="move";
if(_2f5.zIndex!=null){
_2fb+="&z="+_2f5.zIndex;
}
if(_2f5.width!=null){
_2fb+="&width="+_2f5.width;
}
if(_2f5.height!=null){
_2fb+="&height="+_2f5.height;
}
if(_2f5.left!=null){
_2fb+="&x="+_2f5.left;
}
if(_2f5.top!=null){
_2fb+="&y="+_2f5.top;
}
}
if(_2f8!=null){
_2fb+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_2f8;
}
this._submitAjaxApi(_2fc,_2fb,new _2f2.om.MoveApiCL(this,_2f5));
if(!_2f0&&!_2f1){
if(!_2f6&&_2f4.zIndexChanged){
var _2fd=_2f2.page.getPortletArray();
if(_2fd&&(_2fd.length-1)>0){
for(var i=0;i<_2fd.length;i++){
var _2ff=_2fd[i];
if(_2ff&&_2ff.entityId!=this.entityId){
if(!_2ff.properties[_2f2.id.PP_WINDOW_POSITION_STATIC]){
_2ff.submitWinState(true);
}
}
}
}
}else{
if(_2f6){
}
}
}
}
},retrieveContent:function(_300,_301,_302){
if(_300==null){
_300=new jetspeed.om.PortletCL(this,_302,_301);
}
if(!_301){
_301={};
}
var _303=this;
_303.getPortletUrl(_301);
this.contentRetriever.getContent(_301,_300,_303,jetspeed.debugContentDumpIds);
},setPortletContent:function(_304,_305,_306){
var _307=this.getPWin();
if(_306!=null&&_306.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_306;
if(_307&&!this.loadingIndicatorIsShown()){
_307.setPortletTitle(_306);
}
}
if(_307){
_307.setPortletContent(_304,_305);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _309=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _30a=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _30b=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _30c=this.getPWin();
if(_30c&&(_309||_30a)){
var _30d=_30c.getPortletTitle();
if(_30d&&(_30d==_309||_30d==_30a)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_30e){
var _30f=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_30f=jetspeed.prefs.desktopActionLabels[_30e];
if(_30f!=null&&_30f.length==0){
_30f=null;
}
}
return _30f;
},loadingIndicatorShow:function(_310){
if(_310&&!this.loadingIndicatorIsShown()){
var _311=this._getLoadingActionLabel(_310);
var _312=this.getPWin();
if(_312&&_311){
_312.setPortletTitle(_311);
}
}
},loadingIndicatorHide:function(){
var _313=this.getPWin();
if(_313){
_313.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_315,_316){
var _317=jetspeed;
var _318=_317.url;
var _319=null;
if(_315!=null){
_319=this.getAction(_315);
}
var _31a=_316;
if(_31a==null&&_319!=null){
_31a=_319.url;
}
if(_31a==null){
return;
}
var _31b=_318.basePortalUrl()+_318.path.PORTLET+"/"+_31a+_317.page.getPath();
if(_315!=_317.id.ACT_PRINT){
this.retrieveContent(null,{url:_31b});
}else{
var _31c=_317.page.getPageUrl();
_31c=_318.addQueryParameter(_31c,"jsprintmode","true");
_31c=_318.addQueryParameter(_31c,"jsaction",escape(_319.url));
_31c=_318.addQueryParameter(_31c,"jsentity",this.entityId);
_31c=_318.addQueryParameter(_31c,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_31c.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_31e,_31f,_320){
if(_31e){
this.actions=_31e;
}else{
this.actions={};
}
this.currentActionState=_31f;
this.currentActionMode=_320;
this.syncActions();
},syncActions:function(){
var _321=jetspeed;
_321.page.setPageModePortletActions(this);
var _322=this.getPWin();
if(_322){
_322.actionBtnSync(_321,_321.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_325,_326){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_325;
this.submitOperation=_326;
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
},eventConfMgr:function(_329){
var fn=(_329)?"disconnect":"connect";
var _32b=dojo.event;
var form=this.form;
_32b[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_32b[fn]("after",node,"onclick",this,"click",null);
}
}
var _32f=form.getElementsByTagName("input");
for(var i=0;i<_32f.length;i++){
var _330=_32f[i];
if(_330.type.toLowerCase()=="image"&&_330.form==form){
_32b[fn]("after",_330,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_32b[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_332){
var _333=true;
if(this.isFormSubmitInProgress()){
_333=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_333=false;
}
}
}
return _333;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _335=jetspeed.portleturl.parseContentUrl(this.form.action);
var _336={};
if(_335.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_335.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _337=jetspeed.portleturl.genPseudoUrl(_335,true);
this.form.action=_337;
this.submitOperation=_335.operation;
this.entityId=_335.portletEntityId;
_336.url=_335.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_336.formFilter=dojo.lang.hitch(this,"formFilter");
_336.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_336),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_336),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_338){
if(_338!=undefined){
this.formSubmitInProgress=_338;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_339,_33a,_33b){
this.portlet=_339;
this.suppressGetActions=_33a;
this.formbind=null;
if(_33b!=null&&_33b.submitFormBindObject!=null){
this.formbind=_33b.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_33c){
if(this.portlet==null){
return;
}
if(_33c){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_33d,_33e,_33f,http){
var _341=null;
var _342=(_33d?_33d.indexOf("</JS_PORTLET_HEAD_ELEMENTS>"):-1);
if(_342!=-1){
_342+="</JS_PORTLET_HEAD_ELEMENTS>".length;
_341=_33d.substring(0,_342);
_33d=_33d.substring(_342);
var _343=new dojo.xml.Parse();
var _344=_343.parseElement(dojo.dom.createDocumentFromText(_341).documentElement);
jetspeed.contributeHeadElements(_344);
}
var _345=null;
if(http!=null){
try{
_345=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_345!=null){
_345=unescape(_345);
}
}
_33f.setPortletContent(_33d,_33e,_345);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_33f.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_347,_348,_349){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_348+" type: "+type+jetspeed.formatError(_347));
}};
jetspeed.om.PortletActionCL=function(_34a,_34b){
this.portlet=_34a;
this.formbind=null;
if(_34b!=null&&_34b.submitFormBindObject!=null){
this.formbind=_34b.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_34c){
if(this.portlet==null){
return;
}
if(_34c){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_34d,_34e,_34f,http){
var _351=jetspeed;
var _352=null;
var _353=false;
var _354=_351.portleturl.parseContentUrl(_34d);
if(_354.operation==_351.portleturl.PORTLET_REQUEST_ACTION||_354.operation==_351.portleturl.PORTLET_REQUEST_RENDER){
if(_351.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_354.operation+"-url in response body: "+_34d+"  url: "+_354.url+" entity-id: "+_354.portletEntityId);
}
_352=_354.url;
}else{
if(_351.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_34d);
}
_352=_34d;
if(_352){
var _355=_352.indexOf(_351.url.basePortalUrl()+_351.url.path.PORTLET);
if(_355==-1){
_353=true;
window.location.href=_352;
_352=null;
}else{
if(_355>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_34d);
_352=null;
}
}
}
}
if(_352!=null&&!_351.noActionRender){
if(_351.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_352);
}
var _356=new jetspeed.PortletRenderer(false,false,false,_352,true);
_356.renderAll();
}else{
this._loading(false);
}
if(!_353&&this.portlet){
_351.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_358,_359,_35a){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_358));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _35b=this.getUrl();
if(_35b){
var _35c=jetspeed;
if(!_35c.prefs.ajaxPageNavigation||_35c.url.urlStartsWithHttp(_35b)){
_35c.pageNavigate(_35b,this.getTarget());
}else{
_35c.updatePage(_35b);
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
jetspeed.om.Menu=function(_35d,_35e){
this._is_parsed=false;
this.name=_35d;
this.type=_35e;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_35f){
if(!_35f){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_35f);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_361){
if(!this.hasOptions()){
return null;
}
if(_361==0||_361>0){
if(_361>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_361];
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
var _363=this.options[i];
if(_363 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_365,_366){
var _367=this.parseMenu(data,_366.menuName,_366.menuType);
_366.page.putMenu(_367);
},notifyFailure:function(type,_369,_36a,_36b){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_36b.toString()+"] url: "+_36a+" type: "+type+jetspeed.formatError(_369));
},parseMenu:function(node,_36d,_36e){
var menu=null;
var _370=node.getElementsByTagName("js");
if(!_370||_370.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _371=_370[0].childNodes;
for(var i=0;i<_371.length;i++){
var _373=_371[i];
if(_373.nodeType!=1){
continue;
}
var _374=_373.nodeName;
if(_374=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_373,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_36d;
}
if(menu.type==null){
menu.type=_36e;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _377=null;
var _378=node.childNodes;
for(var i=0;i<_378.length;i++){
var _37a=_378[i];
if(_37a.nodeType!=1){
continue;
}
var _37b=_37a.nodeName;
if(_37b=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_37a,new jetspeed.om.Menu()));
}
}else{
if(_37b=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_37a,new jetspeed.om.MenuOption()));
}
}else{
if(_37b=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_37a,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_37b){
mObj[_37b]=((_37a&&_37a.firstChild)?_37a.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_37c,_37d,_37e){
this.includeMenuDefs=_37c;
this.isPageUpdate=_37d;
this.initEditModeConf=_37e;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_380,_381){
var _382=this.getMenuDefs(data,_380,_381);
for(var i=0;i<_382.length;i++){
var mObj=_382[i];
_381.page.putMenu(mObj);
}
this.notifyFinished(_381);
},getMenuDefs:function(data,_386,_387){
var _388=[];
var _389=data.getElementsByTagName("menu");
for(var i=0;i<_389.length;i++){
var _38b=_389[i].getAttribute("type");
if(this.includeMenuDefs){
_388.push(this.parseMenuObject(_389[i],new jetspeed.om.Menu(null,_38b)));
}else{
var _38c=_389[i].firstChild.nodeValue;
_388.push(new jetspeed.om.Menu(_38c,_38b));
}
}
return _388;
},notifyFailure:function(type,_38e,_38f,_390){
dojo.raise("MenusApiCL error ["+_390.toString()+"] url: "+_38f+" type: "+type+jetspeed.formatError(_38e));
},notifyFinished:function(_391){
var _392=jetspeed;
if(this.includeMenuDefs){
_392.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_392.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_392.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_393){
this.portletEntityId=_393;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_395,_396){
if(jetspeed.url.checkAjaxApiResponse(_395,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_397){
var _398=jetspeed.page.getPortlet(this.portletEntityId);
if(_398){
if(_397){
_398.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_398.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_39a,_39b,_39c){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_39c.toString()+"] url: "+_39b+" type: "+type+jetspeed.formatError(_39a));
}});
jetspeed.om.PageChangeActionCL=function(_39d){
this.pageActionUrl=_39d;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_39f,_3a0){
if(jetspeed.url.checkAjaxApiResponse(_39f,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_3a2,_3a3,_3a4){
dojo.raise("PageChangeActionCL error ["+_3a4.toString()+"] url: "+_3a3+" type: "+type+jetspeed.formatError(_3a2));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_3a6,_3a7){
var _3a8=jetspeed;
if(_3a8.url.checkAjaxApiResponse(_3a6,data,null,false,"user-info")){
var _3a9=data.getElementsByTagName("js");
if(_3a9&&_3a9.length==1){
var root=_3a9[0];
var un=_3a8.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _3ad=root.getElementsByTagName("role");
if(_3ad!=null){
for(var i=0;i<_3ad.length;i++){
var role=(_3ad[i].firstChild?_3ad[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_3a8.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_3b1,_3b2,_3b3){
dojo.raise("UserInfoCL error ["+_3b3.toString()+"] url: "+_3b2+" type: "+type+jetspeed.formatError(_3b1));
}});
jetspeed.om.PortletActionsCL=function(_3b4){
this.portletEntityIds=_3b4;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_3b5){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _3b7=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_3b7){
if(_3b5){
_3b7.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3b7.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_3b9,_3ba){
var _3bb=jetspeed;
this._loading(false);
if(_3bb.url.checkAjaxApiResponse(_3b9,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_3bb.page);
}
},processPortletActionsResponse:function(node,_3bd){
var _3be=this.parsePortletActionsResponse(node,_3bd);
for(var i=0;i<_3be.length;i++){
var _3c0=_3be[i];
var _3c1=_3c0.id;
var _3c2=_3bd.getPortlet(_3c1);
if(_3c2!=null){
_3c2.updateActions(_3c0.actions,_3c0.currentActionState,_3c0.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3c4){
var _3c5=new Array();
var _3c6=node.getElementsByTagName("js");
if(!_3c6||_3c6.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3c5;
}
var _3c7=_3c6[0].childNodes;
for(var i=0;i<_3c7.length;i++){
var _3c9=_3c7[i];
if(_3c9.nodeType!=1){
continue;
}
var _3ca=_3c9.nodeName;
if(_3ca=="portlets"){
var _3cb=_3c9;
var _3cc=_3cb.childNodes;
for(var pI=0;pI<_3cc.length;pI++){
var _3ce=_3cc[pI];
if(_3ce.nodeType!=1){
continue;
}
var _3cf=_3ce.nodeName;
if(_3cf=="portlet"){
var _3d0=this.parsePortletElement(_3ce,_3c4);
if(_3d0!=null){
_3c5.push(_3d0);
}
}
}
}
}
return _3c5;
},parsePortletElement:function(node,_3d2){
var _3d3=node.getAttribute("id");
if(_3d3!=null){
var _3d4=_3d2._parsePSMLActions(node,null);
var _3d5=_3d2._parsePSMLChildOrAttr(node,"state");
var _3d6=_3d2._parsePSMLChildOrAttr(node,"mode");
return {id:_3d3,actions:_3d4,currentActionState:_3d5,currentActionMode:_3d6};
}
return null;
},notifyFailure:function(type,_3d8,_3d9,_3da){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3da.toString()+"] url: "+_3d9+" type: "+type+jetspeed.formatError(_3d8));
}});
jetspeed.om.MoveApiCL=function(_3db,_3dc){
this.portlet=_3db;
this.changedState=_3dc;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3dd){
if(this.portlet==null){
return;
}
if(_3dd){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3df,_3e0){
var _3e1=jetspeed;
this._loading(false);
dojo.lang.mixin(_3e0.portlet.lastSavedWindowState,this.changedState);
var _3e2=true;
if(djConfig.isDebug&&_3e1.debug.submitWinState){
_3e2=true;
}
var _3e3=_3e1.url.checkAjaxApiResponse(_3df,data,["refresh"],_3e2,("move-portlet ["+_3e0.portlet.entityId+"]"),_3e1.debug.submitWinState);
if(_3e3=="refresh"){
var _3e4=_3e1.page.getPageUrl();
if(!_3e1.prefs.ajaxPageNavigation){
_3e1.pageNavigate(_3e4,null,true);
}else{
_3e1.updatePage(_3e4,false,true);
}
}
},notifyFailure:function(type,_3e6,_3e7,_3e8){
this._loading(false);
dojo.debug("submitWinState error ["+_3e8.entityId+"] url: "+_3e7+" type: "+type+jetspeed.formatError(_3e6));
}};
jetspeed.postload_addEventListener=function(node,_3ea,fnc,_3ec){
if((_3ea=="load"||_3ea=="DOMContentLoaded"||_3ea=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3ea,fnc,_3ec);
}
};
jetspeed.postload_attachEvent=function(node,_3ee,fnc){
if(_3ee=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3ee,fnc);
}
};
jetspeed.postload_docwrite=function(_3f0){
if(!_3f0){
return;
}
_3f0=_3f0.replace(/^\s+|\s+$/g,"");
var _3f1=/^<script\b([^>]*)>.*?<\/script>/i;
var _3f2=_3f1.exec(_3f0);
if(_3f2){
_3f0=null;
var _3f3=_3f2[1];
if(_3f3){
var _3f4=/\bid\s*=\s*([^\s]+)/i;
var _3f5=_3f4.exec(_3f3);
if(_3f5){
var _3f6=_3f5[1];
_3f0="<img id="+_3f6+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3f0){
var _3f8=dojo;
tn=_3f8.doc().createElement("div");
tn.style.visibility="hidden";
_3f8.body().appendChild(tn);
tn.innerHTML=_3f0;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_3f9,_3fa,_3fb){
if(_3f9==document||_3f9==window){
if(_3fb&&_3fb.length>0){
var _3fc=jetspeed.portleturl;
if(_3fb.indexOf(_3fc.DESKTOP_ACTION_PREFIX_URL)!=0&&_3fb.indexOf(_3fc.DESKTOP_RENDER_PREFIX_URL)!=0){
_3f9.location=_3fb;
}
}
}else{
if(_3f9!=null){
var _3fd=_3fa.indexOf(".");
if(_3fd==-1){
_3f9[_3fa]=_3fb;
}else{
var _3fe=_3fa.substring(0,_3fd);
var _3ff=_3f9[_3fe];
if(_3ff){
var _400=_3fa.substring(_3fd+1);
if(_400){
_3ff[_400]=_3fb;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _402=document.createElement("script");
_402.setAttribute("type","text/plain");
_402.setAttribute("language","ignore");
_402.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_402);
return _402;
};
jetspeed.containsElement=function(_403,_404,_405,_406){
if(!_403||!_404||!_405){
return false;
}
if(!_406){
_406=document;
}
var _407=_406.getElementsByTagName(_403);
if(!_407){
return false;
}
for(var i=0;i<_407.length;++i){
var _409=_407[i].getAttribute(_404);
if(_409==_405){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _40a=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _40b=_40a.concat([" height: ","","",";"]);
var _40c=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _40d=_40b.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _40e=_40d.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_40a,cssHeight:_40b,cssWidthHeight:_40c,cssOverflow:_40d,cssPosition:_40e,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_40f,_410,_411,_412,_413,_414){
var djH=dojo.html;
var jsId=jetspeed.id;
var _417=null;
var _418=-1;
var _419=-1;
var _41a=-1;
if(_40f){
var _41b=_40f.childNodes;
if(_41b){
_41a=_41b.length;
}
_417=[];
if(_41a>0){
var _41c="",_41d="";
if(!_414){
_41c=jsId.PWIN_CLASS;
}
if(_411){
_41c+=((_41c.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_412){
_41c+=((_41c.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_413&&!_412){
_41c+=((_41c.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_412&&!_413){
_41d=((_41d.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_41c.length>0){
var _41e=new RegExp("(^|\\s+)("+_41c+")(\\s+|$)");
var _41f=null;
if(_41d.length>0){
_41f=new RegExp("(^|\\s+)("+_41d+")(\\s+|$)");
}
var _420,_421,_422;
for(var i=0;i<_41a;i++){
_420=_41b[i];
_421=false;
_422=djH.getClass(_420);
if(_41e.test(_422)&&(_41f==null||!_41f.test(_422))){
_417.push(_420);
_421=true;
}
if(_410&&_420==_410){
if(!_421){
_417.push(_420);
}
_418=i;
_419=_417.length-1;
}
}
}
}
}
return {matchingNodes:_417,totalNodes:_41a,matchNodeIndex:_418,matchNodeIndexInMatchingNodes:_419};
},getPWinsFromNodes:function(_424){
var _425=jetspeed.page;
var _426=null;
if(_424){
_426=new Array();
for(var i=0;i<_424.length;i++){
var _428=_425.getPWin(_424[i].id);
if(_428){
_426.push(_428);
}
}
}
return _426;
},createPortletWindow:function(_429,_42a,_42b){
var _42c=false;
if(djConfig.isDebug&&_42b.debug.profile){
_42c=true;
dojo.profile.start("createPortletWindow");
}
var _42d=(_42a!=null);
var _42e=false;
var _42f=null;
if(_42d&&_42a<_42b.page.columns.length&&_42a>=0){
_42f=_42b.page.columns[_42a].domNode;
}
if(_42f==null){
_42e=true;
_42f=document.getElementById(_42b.id.DESKTOP);
}
if(_42f==null){
return;
}
var _430={};
if(_429.isPortlet){
_430.portlet=_429;
if(_42b.prefs.printModeOnly!=null){
_430.printMode=true;
}
if(_42e){
_429.properties[_42b.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_42b.widget.PortletWindow.prototype.altInitParamsDef(_430,_429);
if(_42e){
pwP.altInitParams[_42b.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _432=new _42b.widget.PortletWindow();
_432.build(_430,_42f);
if(_42c){
dojo.profile.end("createPortletWindow");
}
return _432;
},getLayoutExtents:function(node,_434,_435,_436){
if(!_434){
_434=_435.gcs(node);
}
var pad=_435._getPadExtents(node,_434);
var _438=_435._getBorderExtents(node,_434);
var _439={l:(pad.l+_438.l),t:(pad.t+_438.t),w:(pad.w+_438.w),h:(pad.h+_438.h)};
var _43a=_435._getMarginExtents(node,_434,_436);
return {bE:_438,pE:pad,pbE:_439,mE:_43a,lessW:(_439.w+_43a.w),lessH:(_439.h+_43a.h)};
},getContentBoxSize:function(node,_43c){
var w=node.clientWidth,h,_43f;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_43f=_43c.pbE;
}else{
h=node.clientHeight;
_43f=_43c.pE;
}
return {w:(w-_43f.w),h:(h-_43f.h)};
},getMarginBoxSize:function(node,_441){
return {w:(node.offsetWidth+_441.mE.w),h:(node.offsetHeight+_441.mE.h)};
},getMarginBox:function(node,_443,_444,_445){
var l=node.offsetLeft-_443.mE.l,t=node.offsetTop-_443.mE.t;
if(_444&&_445.UAope){
l-=_444.bE.l;
t-=_444.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_443.mE.w),h:(node.offsetHeight+_443.mE.h)};
},setMarginBox:function(node,_449,_44a,_44b,_44c,_44d,_44e,_44f){
var pb=_44d.pbE,mb=_44d.mE;
if(_44b!=null&&_44b>=0){
_44b=Math.max(_44b-pb.w-mb.w,0);
}
if(_44c!=null&&_44c>=0){
_44c=Math.max(_44c-pb.h-mb.h,0);
}
_44f._setBox(node,_449,_44a,_44b,_44c);
},evtConnect:function(_452,_453,_454,_455,_456,_457,rate){
if(!rate){
rate=0;
}
var _459={adviceType:_452,srcObj:_453,srcFunc:_454,adviceObj:_455,adviceFunc:_456,rate:rate};
if(_457==null){
_457=dojo.event;
}
_457.connect(_459);
return _459;
},evtDisconnect:function(_45a,_45b,_45c,_45d,_45e,_45f){
if(_45f==null){
_45f=dojo.event;
}
_45f.disconnect({adviceType:_45a,srcObj:_45b,srcFunc:_45c,adviceObj:_45d,adviceFunc:_45e});
},evtDisconnectWObj:function(_460,_461){
if(_461==null){
_461=dojo.event;
}
_461.disconnect(_460);
},evtDisconnectWObjAry:function(_462,_463){
if(_462&&_462.length>0){
if(_463==null){
_463=dojo.event;
}
for(var i=0;i<_462.length;i++){
_463.disconnect(_462[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _465=false;
var _466=this._popupMenuWidgets;
for(var i=0;i<_466.length;i++){
var _468=_466[i];
if(_468&&_468.isShowingNow){
_465=true;
break;
}
}
return _465;
},addPopupMenuWidget:function(_469){
if(_469){
this._popupMenuWidgets.push(_469);
}
},removePopupMenuWidget:function(_46a){
if(!_46a){
return;
}
var _46b=this._popupMenuWidgets;
for(var i=0;i<_46b.length;i++){
if(_46b[i]===_46a){
_46b[i]=null;
}
}
},updateChildColInfo:function(_46d,_46e,_46f,_470,_471,_472){
var _473=jetspeed;
var _474=dojo;
var _475=_474.byId(_473.id.COLUMNS);
if(!_475){
return;
}
var _476=false;
if(_46d!=null){
var _477=_46d.getAttribute("columnindex");
var _478=_46d.getAttribute("layoutid");
var _479=(_477==null?-1:new Number(_477));
if(_479>=0&&_478!=null&&_478.length>0){
_476=true;
}
}
var _47a=_473.page.columns||[];
var _47b=new Array(_47a.length);
var _47c=_473.page.layoutInfo;
var fnc=_473.ui._updateChildColInfo;
fnc(fnc,_475,1,_47b,_47a,_46e,_46f,_470,_47c,_47c.columns,_47c.desktop,_46d,_476,_471,_472,_474,_473);
return _47b;
},_updateChildColInfo:function(fnc,_47f,_480,_481,_482,_483,_484,_485,_486,_487,_488,_489,_48a,_48b,_48c,_48d,_48e){
var _48f=_47f.childNodes;
var _490=(_48f?_48f.length:0);
if(_490==0){
return;
}
var _491=_48d.html.getAbsolutePosition(_47f,true);
var _492=_48e.ui.getMarginBox(_47f,_487,_488,_48e);
var _493=_486.column;
var _494,col,_496,_497,_498,_499,_49a,_49b,_49c,_49d,_49e,_49f,_4a0,_4a1;
var _4a2=null,_4a3=(_48b!=null?(_48b+1):null),_4a4,_4a5;
var _4a6=null;
for(var i=0;i<_490;i++){
_494=_48f[i];
_497=_494.getAttribute("columnindex");
_498=(_497==null?-1:new Number(_497));
if(_498>=0){
col=_482[_498];
_4a1=true;
_496=(col?col.layoutActionsDisabled:false);
_499=_494.getAttribute("layoutid");
_49a=(_499!=null&&_499.length>0);
_4a4=_4a3;
_4a5=null;
_496=((!_485)&&_496);
var _4a8=_480;
var _4a9=(_494===_489);
if(_49a){
if(_4a6==null){
_4a6=_480;
}
if(col){
col._updateLayoutDepth(_480);
}
_4a8++;
}else{
if(!_4a9){
if(col&&(!_496||_485)&&(_483==null||_483[_498]==null)&&(_484==null||_480<=_484)){
_49b=_48e.ui.getMarginBox(_494,_493,_487,_48e);
if(_4a2==null){
_4a2=_49b.t-_492.t;
_4a0=_492.h-_4a2;
}
_49c=_491.left+(_49b.l-_492.l);
_49d=_491.top+_4a2;
_49e=_49b.h;
if(_49e<_4a0){
_49e=_4a0;
}
if(_49e<40){
_49e=40;
}
var _4aa=_494.childNodes;
_49f={left:_49c,top:_49d,right:(_49c+_49b.w),bottom:(_49d+_49e),childCount:(_4aa?_4aa.length:0),pageColIndex:_498};
_49f.height=_49f.bottom-_49f.top;
_49f.width=_49f.right-_49f.left;
_49f.yhalf=_49f.top+(_49f.height/2);
_481[_498]=_49f;
_4a1=(_49f.childCount>0);
if(_4a1){
_494.style.height="";
}else{
_494.style.height="1px";
}
if(_48b!=null){
_4a5=(_48e.debugDims(_49f,true)+" yhalf="+_49f.yhalf+(_49b.h!=_49e?(" hreal="+_49b.h):"")+" childC="+_49f.childCount+"}");
}
}
}
}
if(_48b!=null){
if(_49a){
_4a4=_4a3+1;
}
if(_4a5==null){
_4a5="---";
}
_48d.hostenv.println(_48d.string.repeat(_48c,_48b)+"["+((_498<10?" ":"")+_497)+"] "+_4a5);
}
if(_4a1){
var _4ab=fnc(fnc,_494,_4a8,_481,_482,_483,_484,_485,_486,(_49a?_486.columnLayoutHeader:_493),_487,_489,_48a,_4a4,_48c,_48d,_48e);
if(_4ab!=null&&(_4a6==null||_4ab>_4a6)){
_4a6=_4ab;
}
}
}
}
_497=_47f.getAttribute("columnindex");
_499=_47f.getAttribute("layoutid");
_498=(_497==null?-1:new Number(_497));
if(_498>=0&&_499!=null&&_499.length>0){
col=_482[_498];
col._updateLayoutChildDepth(_4a6);
}
return _4a6;
},getScrollbar:function(_4ac){
var _4ad=_4ac.ui.scrollWidth;
if(_4ad==null){
var _4ae=document.createElement("div");
var _4af="width: 100px; height: 100px; top: -300px; left: 0px; overflow: scroll; position: absolute";
_4ae.style.cssText=_4af;
var test=document.createElement("div");
_4ae.style.cssText="width: 400px; height: 400px";
_4ae.appendChild(test);
var _4b1=_4ac.docBody;
_4b1.appendChild(_4ae);
_4ad=_4ae.offsetWidth-_4ae.clientWidth;
_4b1.removeChild(_4ae);
_4ae.removeChild(test);
_4ae=test=null;
_4ac.ui.scrollWidth=_4ad;
}
return _4ad;
}};
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_4b3){
this.oldXY=this.getWinDims(win,win.document,_4b3);
},getWinDims:function(win,doc,_4b6){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_4b6.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_4b6;
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
var _4bd=jetspeed;
var _4be=this.getWinDims(window,window.document,_4bd.docBody);
this.timerId=0;
if((_4be.x!=this.oldXY.x)||(_4be.y!=this.oldXY.y)){
this.oldXY=_4be;
if(_4bd.page){
if(!this.resizing){
try{
this.resizing=true;
_4bd.page.onBrowserWindowResize();
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
var _4bf=jetspeed;
var _4c0=null;
var _4c1=false;
var ua=function(){
var _4c3=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_4c3[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_4c3[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_4c3[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4c6=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_4c3=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_4c3[0]==6){
_4c6=true;
}
}
if(!_4c6){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4c6&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_4c3=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_4c3,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
function showExpressInstall(_4cd){
_4c1=true;
var obj=document.getElementById(_4cd.id);
if(obj){
var ac=document.getElementById(_4cd.altContentId);
if(ac){
_4c0=ac;
}
var w=_4cd.width?_4cd.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4cd.height?_4cd.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4cd.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
};
function createSWF(_4d6,_4d7,el){
_4d7.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4d6){
if(typeof _4d6[i]=="string"){
if(i=="data"){
_4d7.movie=_4d6[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4d6[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4d6[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4d7){
if(typeof _4d7[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4d7[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4d6){
if(typeof _4d6[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4d6[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4d6[m]);
}
}
}
}
for(var n in _4d7){
if(typeof _4d7[n]=="string"&&n!="movie"){
createObjParam(o,n,_4d7[n]);
}
}
el.parentNode.replaceChild(o,el);
}
};
function createObjParam(el,_4e1,_4e2){
var p=document.createElement("param");
p.setAttribute("name",_4e1);
p.setAttribute("value",_4e2);
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
return {embedSWF:function(_4ea,_4eb,_4ec,_4ed,_4ee,_4ef,_4f0,_4f1,_4f2,_4f3){
if(!ua.w3cdom||!_4ea||!_4eb||!_4ec||!_4ed||!_4ee){
return;
}
if(hasPlayerVersion(_4ee.split("."))){
var _4f4=(_4f2?_4f2.id:null);
createCSS("#"+_4eb,"visibility:hidden");
var att=(typeof _4f2=="object")?_4f2:{};
att.data=_4ea;
att.width=_4ec;
att.height=_4ed;
var par=(typeof _4f1=="object")?_4f1:{};
if(typeof _4f0=="object"){
for(var i in _4f0){
if(typeof _4f0[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4f0[i];
}else{
par.flashvars=i+"="+_4f0[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4eb));
createCSS("#"+_4eb,"visibility:visible");
if(_4f4){
var _4f8=_4bf.page.swfInfo;
if(_4f8==null){
_4f8=_4bf.page.swfInfo={};
}
_4f8[_4f4]=_4f3;
}
}else{
if(_4ef&&!_4c1&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4eb,"visibility:hidden");
var _4f9={};
_4f9.id=_4f9.altContentId=_4eb;
_4f9.width=_4ec;
_4f9.height=_4ed;
_4f9.expressInstall=_4ef;
showExpressInstall(_4f9);
createCSS("#"+_4eb,"visibility:visible");
}
}
}};
}();

