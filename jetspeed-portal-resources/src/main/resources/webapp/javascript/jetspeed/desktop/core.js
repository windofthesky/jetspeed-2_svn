dojo.provide("jetspeed.desktop.core");
dojo.require("dojo.lang.*");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.collections.ArrayList");
dojo.require("dojo.collections.Set");
dojo.require("jetspeed.common");
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.om){
jetspeed.om={};
}
if(!jetspeed.ui){
jetspeed.ui={};
}
if(!jetspeed.ui.widget){
jetspeed.ui.widget={};
}
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",TASKBAR:"jetspeedTaskbar",SELECTOR:"jetspeedSelector",PORTLET_STYLE_CLASS:"portlet",PORTLET_WINDOW_STYLE_CLASS:"dojoFloatingPane",PORTLET_WINDOW_GHOST_STYLE_CLASS:"ghostPane",PORTLET_WINDOW_ID_PREFIX:"portletWindow_",PORTLET_PROP_WIDGET_ID:"widgetId",PORTLET_PROP_CONTENT_RETRIEVER:"contentRetriever",PORTLET_PROP_DESKTOP_EXTENDED:"jsdesktop",PORTLET_PROP_WINDOW_POSITION_STATIC:"windowPositionStatic",PORTLET_PROP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PORTLET_PROP_WINDOW_DECORATION:"windowDecoration",PORTLET_PROP_WINDOW_TITLE:"title",PORTLET_PROP_WINDOW_ICON:"windowIcon",PORTLET_PROP_WIDTH:"width",PORTLET_PROP_HEIGHT:"height",PORTLET_PROP_LEFT:"left",PORTLET_PROP_TOP:"top",PORTLET_PROP_COLUMN:"column",PORTLET_PROP_ROW:"row",PORTLET_PROP_EXCLUDE_PCONTENT:"excludePContent",PORTLET_PROP_WINDOW_STATE:"windowState",PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS:"staticpos",PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT:"fitheight",PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR:"=",PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR:";",ACTION_NAME_MENU:"menu",ACTION_NAME_MINIMIZE:"minimized",ACTION_NAME_MAXIMIZE:"maximized",ACTION_NAME_RESTORE:"normal",ACTION_NAME_PRINT:"print",ACTION_NAME_EDIT:"edit",ACTION_NAME_VIEW:"view",ACTION_NAME_HELP:"help",ACTION_NAME_ADDPORTLET:"addportlet",ACTION_NAME_REMOVEPORTLET:"removeportlet",ACTION_NAME_DESKTOP_TILE:"tile",ACTION_NAME_DESKTOP_UNTILE:"untile",ACTION_NAME_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACTION_NAME_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACTION_NAME_LOAD_RENDER:"loadportletrender",ACTION_NAME_LOAD_ACTION:"loadportletaction",ACTION_NAME_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PG_ED_WID:"jetspeed-page-editor",PG_ED_PARAM:"editPage",PORTAL_ORIGINATE_PARAMETER:"portal",DEBUG_WINDOW_TAG:"js-dojo-debug"};
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
},portletSelectorWindowTitle:"Portlet Selector",portletSelectorWindowIcon:"text-x-script.png",portletSelectorBounds:{x:20,y:20,width:400,height:600},windowActionButtonOrder:[jetspeed.id.ACTION_NAME_MENU,"edit","view","help",jetspeed.id.ACTION_NAME_MINIMIZE,jetspeed.id.ACTION_NAME_RESTORE,jetspeed.id.ACTION_NAME_MAXIMIZE],windowActionNotPortlet:[jetspeed.id.ACTION_NAME_MENU,jetspeed.id.ACTION_NAME_MINIMIZE,jetspeed.id.ACTION_NAME_RESTORE,jetspeed.id.ACTION_NAME_MAXIMIZE],windowActionButtonMax:5,windowActionButtonHide:false,windowActionButtonTooltip:true,windowActionMenuOrder:[jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND,jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL,jetspeed.id.ACTION_NAME_DESKTOP_TILE,jetspeed.id.ACTION_NAME_DESKTOP_UNTILE],windowIconEnabled:true,windowIconPath:"/images/portlets/small/",windowDecoration:"tigris",pageActionButtonTooltip:true,getPortletDecorationBaseUrl:function(_1){
return jetspeed.prefs.getPortletDecorationsRootUrl()+"/"+_1;
},getPortletDecorationConfig:function(_2){
if(jetspeed.prefs.portletDecorationsConfig==null||_2==null){
return null;
}
return jetspeed.prefs.portletDecorationsConfig[_2];
}};
jetspeed.debug={pageLoad:false,retrievePsml:false,setPortletContent:false,doRenderDoAction:false,postParseAnnotateHtml:false,postParseAnnotateHtmlDisableAnchors:false,confirmOnSubmit:false,createWindow:false,initializeWindowState:false,submitChangedWindowState:false,ajaxPageNav:false,windowDecorationRandom:false,debugContainerId:(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId)};
jetspeed.debugInPortletWindow=true;
jetspeed.page=null;
jetspeed.initializeDesktop=function(){
jetspeed.url.pathInitialize();
jetspeed.browser_IE=dojo.render.html.ie;
jetspeed.browser_IEpre7=(dojo.render.html.ie50||dojo.render.html.ie55||dojo.render.html.ie60);
if(djConfig.jetspeed!=null){
for(var _3 in djConfig.jetspeed){
var _4=djConfig.jetspeed[_3];
if(_4!=null){
if(jetspeed.debug[_3]!=null){
jetspeed.debug[_3]=_4;
}else{
jetspeed.prefs[_3]=_4;
}
}
}
if(jetspeed.prefs.windowWidth==null||isNaN(jetspeed.prefs.windowWidth)){
jetspeed.prefs.windowWidth="280";
}
if(jetspeed.prefs.windowHeight==null||isNaN(jetspeed.prefs.windowHeight)){
jetspeed.prefs.windowHeight="200";
}
var _5={};
_5[jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_EXPAND]=true;
_5[jetspeed.id.ACTION_NAME_DESKTOP_HEIGHT_NORMAL]=true;
_5[jetspeed.id.ACTION_NAME_DESKTOP_TILE]=true;
_5[jetspeed.id.ACTION_NAME_DESKTOP_UNTILE]=true;
jetspeed.prefs.windowActionDesktop=_5;
}
dojo.html.insertCssFile(jetspeed.ui.getDefaultFloatingPaneTemplateCss(),document,true);
if(jetspeed.prefs.portletDecorationsAllowed==null||jetspeed.prefs.portletDecorationsAllowed.length==0){
if(jetspeed.prefs.windowDecoration!=null){
jetspeed.prefs.portletDecorationsAllowed=[jetspeed.prefs.windowDecoration];
}
}else{
if(jetspeed.prefs.windowDecoration==null){
jetspeed.prefs.windowDecoration=jetspeed.prefs.portletDecorationsAllowed[0];
}
}
if(jetspeed.prefs.windowDecoration==null||jetspeed.prefs.portletDecorationsAllowed==null){
dojo.raise("Cannot load page because there are no defined jetspeed portlet decorations");
return;
}
if(jetspeed.prefs.windowActionNoImage!=null){
var _6={};
for(var i=0;i<jetspeed.prefs.windowActionNoImage.length;i++){
_6[jetspeed.prefs.windowActionNoImage[i]]=true;
}
jetspeed.prefs.windowActionNoImage=_6;
}
var _8=jetspeed.url.parse(window.location.href);
var _9=jetspeed.url.getQueryParameter(_8,"jsprintmode")=="true";
if(_9){
_9={};
_9.action=jetspeed.url.getQueryParameter(_8,"jsaction");
_9.entity=jetspeed.url.getQueryParameter(_8,"jsentity");
_9.layout=jetspeed.url.getQueryParameter(_8,"jslayoutid");
jetspeed.prefs.printModeOnly=_9;
jetspeed.prefs.windowTiling=true;
jetspeed.prefs.windowHeightExpand=true;
}
jetspeed.prefs.portletDecorationsConfig={};
for(var i=0;i<jetspeed.prefs.portletDecorationsAllowed.length;i++){
jetspeed.loadPortletDecorationConfig(jetspeed.prefs.portletDecorationsAllowed[i]);
}
if(jetspeed.prefs.printModeOnly==null){
jetspeed.debugWindowLoad();
}else{
for(var _a in jetspeed.prefs.portletDecorationsConfig){
var _b=jetspeed.prefs.portletDecorationsConfig[_a];
if(_b!=null){
_b.windowActionButtonOrder=null;
_b.windowActionMenuOrder=null;
_b.windowDisableResize=true;
_b.windowDisableMove=true;
}
}
}
jetspeed.url.loadingIndicatorShow();
jetspeed.loadPage();
};
jetspeed.loadPage=function(){
jetspeed.page=new jetspeed.om.Page();
jetspeed.page.retrievePsml();
};
jetspeed.updatePage=function(_c,_d){
var _e=jetspeed.page;
if(!_c||!_e||jetspeed.pageNavigateSuppress){
return;
}
if(_e.equalsPageUrl(_c)){
return;
}
_c=_e.makePageUrl(_c);
if(_c!=null){
jetspeed.updatePageBegin();
var _f=_e.layoutDecorator;
var _10=_e.editMode;
_e.destroy();
var _11=new jetspeed.om.Page(_f,_c,(!djConfig.preventBackButtonFix&&!_d),_10);
jetspeed.page=_11;
_11.retrievePsml(new jetspeed.om.PageContentListenerCreateWidget(true));
window.focus();
}
};
jetspeed.updatePageBegin=function(){
if(dojo.render.html.ie60){
document.body.attachEvent("onclick",jetspeed.ie6StopMouseEvts);
document.body.setCapture();
}
};
jetspeed.ie6StopMouseEvts=function(e){
if(e){
e.cancelBubble=true;
e.returnValue=false;
}
};
jetspeed.updatePageEnd=function(){
if(dojo.render.html.ie60){
document.body.releaseCapture();
document.body.detachEvent("onclick",jetspeed.ie6StopMouseEvts);
document.body.releaseCapture();
}
};
jetspeed.doRender=function(_13,_14){
if(!_13){
_13={};
}else{
if((typeof _13=="string"||_13 instanceof String)){
_13={url:_13};
}
}
var _15=jetspeed.page.getPortlet(_14);
if(_15){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_14+"] url: "+_13.url);
}
_15.retrieveContent(null,_13);
}
};
jetspeed.doRenderAll=function(url,_17,_18,_19){
var _1a=jetspeed.debug.doRenderDoAction;
var _1b=jetspeed.debug.pageLoad&&_18;
if(!_17||_17==null){
_17=jetspeed.page.getPortletArray();
}
var _1c="";
var _1d=true;
var _1e=null;
if(_18){
_1e=jetspeed.url.parse(jetspeed.page.getPageUrl());
}
for(var i=0;i<_17.length;i++){
var _20=_17[i];
if((_1a||_1b)){
if(i>0){
_1c=_1c+", ";
}
var _21=null;
if(_20.getProperty!=null){
_21=_20.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
}
if(!_21){
_21=_20.widgetId;
}
if(!_21){
_21=_20.toString();
}
if(_20.entityId){
_1c=_1c+_20.entityId+"("+_21+")";
if(_1b&&_20.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE)){
_1c=_1c+" "+_20.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
}
}else{
_1c=_1c+_21;
}
}
_20.retrieveContent(null,{url:url,jsPageUrl:_1e},_1d);
}
if(_1a){
dojo.debug("doRenderAll ["+_1c+"] url: "+url);
}else{
if(_1b){
dojo.debug("doRenderAll page-url: "+jetspeed.page.getPsmlUrl()+" portlets: ["+_1c+"]"+(url?(" url: "+url):""));
}
}
};
jetspeed.doAction=function(_22,_23){
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
if(!_22.formNode){
dojo.debug("doAction ["+_23+"] url: "+_22.url+" form: null");
}else{
dojo.debug("doAction ["+_23+"] url: "+_22.url+" form: "+jetspeed.debugDumpForm(_22.formNode));
}
}
_24.retrieveContent(new jetspeed.om.PortletActionContentListener(_24,_22),_22);
}
};
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrlForDesktopActionRender:function(_25){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _27=_25;
var _28=null;
if(_25&&_25.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_25.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_25&&_25.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_25.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_28=jetspeed.url.getQueryParameter(_25,"entity");
}
if(!jetspeed.url.validateUrlStartsWithHttp(_27)){
_27=null;
}
return {url:_27,operation:op,portletEntityId:_28};
},generateJSPseudoUrlActionRender:function(_29,_2a){
if(!_29||!_29.url||!_29.portletEntityId){
return null;
}
var _2b=null;
if(_2a){
_2b=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_2b="javascript:";
var _2c=false;
if(_29.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_2b+="doAction(\"";
}else{
if(_29.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_2b+="doRender(\"";
}else{
_2c=true;
}
}
if(_2c){
return null;
}
_2b+=_29.url+"\",\""+_29.portletEntityId+"\"";
_2b+=")";
}
return _2b;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_2d){
var _2e=jetspeed.prefs.getPortletDecorationConfig(_2d);
if(_2e!=null&&!_2e._initialized){
var _2f=jetspeed.prefs.getPortletDecorationBaseUrl(_2d);
_2e._initialized=true;
_2e.cssPathCommon=new dojo.uri.Uri(_2f+"/css/styles.css");
_2e.cssPathDesktop=new dojo.uri.Uri(_2f+"/css/desktop.css");
dojo.html.insertCssFile(_2e.cssPathCommon,null,true);
dojo.html.insertCssFile(_2e.cssPathDesktop,null,true);
if(jetspeed.prefs.printModeOnly==null){
_2e.templatePath=_2f+"/templates/PortletWindow.html";
}else{
_2e.templatePath=_2f+"/templates/PortletWindowPrintMode.html";
}
}
return _2e;
};
jetspeed.loadPortletDecorationConfig=function(_30){
var _31={};
jetspeed.prefs.portletDecorationsConfig[_30]=_31;
_31.windowActionButtonOrder=jetspeed.prefs.windowActionButtonOrder;
_31.windowActionNotPortlet=jetspeed.prefs.windowActionNotPortlet;
_31.windowActionButtonMax=jetspeed.prefs.windowActionButtonMax;
_31.windowActionButtonHide=jetspeed.prefs.windowActionButtonHide;
_31.windowActionButtonTooltip=jetspeed.prefs.windowActionButtonTooltip;
_31.windowActionMenuOrder=jetspeed.prefs.windowActionMenuOrder;
_31.windowActionNoImage=jetspeed.prefs.windowActionNoImage;
_31.windowIconEnabled=jetspeed.prefs.windowIconEnabled;
_31.windowIconPath=jetspeed.prefs.windowIconPath;
var _32=jetspeed.prefs.getPortletDecorationBaseUrl(_30)+"/"+_30+".js";
dojo.hostenv.loadUri(_32,function(_33){
for(var j in _33){
_31[j]=_33[j];
}
if(_31.windowActionNoImage!=null){
var _35={};
for(var i=0;i<_31.windowActionNoImage.length;i++){
_35[_31.windowActionNoImage[i]]=true;
}
_31.windowActionNoImage=_35;
}
if(_31.windowIconPath!=null){
_31.windowIconPath=dojo.string.trim(_31.windowIconPath);
if(_31.windowIconPath==null||_31.windowIconPath.length==0){
_31.windowIconPath=null;
}else{
var _37=_31.windowIconPath;
var _38=_37.charAt(0);
if(_38!="/"){
_37="/"+_37;
}
var _39=_37.charAt(_37.length-1);
if(_39!="/"){
_37=_37+"/";
}
_31.windowIconPath=_37;
}
}
});
};
jetspeed.purifyIdentifier=function(src,_3b,_3c){
if(src==null){
return src;
}
var _3d=src.length;
if(_3d==0){
return src;
}
if(_3b==null){
_3b="_";
}
var _3e=new RegExp("[^a-z_0-9A-Z]","g");
var _3f=src.charCodeAt(0);
var _40=null;
if((_3f>=65&&_3f<=90)||_3f==95||(_3f>=97&&_3f<=122)){
_40=src.charAt(0);
}else{
_40=_3b;
}
var _41=false,_42=false;
if(_3c!=null){
_3c=_3c.toLowerCase();
_41=(_3c=="hi"?true:false);
_42=(_3c=="lo"?true:false);
}
if(_3d>1){
if(_41||_42){
upNext=false;
for(var i=1;i<_3d;i++){
_3f=src.charCodeAt(i);
if((_3f>=65&&_3f<=90)||_3f==95||(_3f>=97&&_3f<=122)||(_3f>=48&&_3f<=57)){
if(upNext&&(_3f>=97&&_3f<=122)){
_40+=String.fromCharCode(_3f-32);
}else{
_40+=src.charAt(i);
}
upNext=false;
}else{
upNext=true;
_40+=_3b;
}
}
}else{
_40+=src.substring(1).replace(_3e,_3b);
}
}
if(_41){
_3f=_40.charCodeAt(0);
if(_3f>=97&&_3f<=122){
_40=String.fromCharCode(_3f-32)+_40.substring(1);
}
}
return _40;
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
jetspeed.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _44=jetspeed.page.getMenuNames();
for(var i=0;i<_44.length;i++){
var _46=_44[i];
var _47=dojo.widget.byId(jetspeed.id.MENU_WIDGET_ID_PREFIX+_46);
if(_47){
_47.createJetspeedMenu(jetspeed.page.getMenu(_46));
}
}
jetspeed.url.loadingIndicatorHide();
jetspeed.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_48){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_48);
}
};
jetspeed.menuNavClickWidget=function(_49,_4a){
dojo.debug("jetspeed.menuNavClick");
if(!_49){
return;
}
if(dojo.lang.isString(_49)){
var _4b=_49;
_49=dojo.widget.byId(_4b);
if(!_49){
dojo.raise("menuNavClick could not find tab widget for "+_4b);
}
}
if(_49){
var _4c=_49.jetspeedmenuname;
if(!_4c&&_49.extraArgs){
_4c=_49.extraArgs.jetspeedmenuname;
}
if(!_4c){
dojo.raise("menuNavClick tab widget ["+_49.widgetId+"] does not define jetspeedMenuName");
}
var _4d=jetspeed.page.getMenu(_4c);
if(!_4d){
dojo.raise("menuNavClick Menu lookup for tab widget ["+_49.widgetId+"] failed: "+_4c);
}
var _4e=_4d.getOptionByIndex(_4a);
jetspeed.menuNavClick(_4e);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_4f,_50,_51){
if(!_4f||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _51=="undefined"){
_51=false;
}
if(!_51&&jetspeed.page&&jetspeed.page.equalsPageUrl(_4f)){
return;
}
_4f=jetspeed.page.makePageUrl(_4f);
if(_50=="top"){
top.location.href=_4f;
}else{
if(_50=="parent"){
parent.location.href=_4f;
}else{
window.location.href=_4f;
}
}
};
jetspeed.loadPortletSelector=function(){
var _52={};
_52[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_52[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_52[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.page.getPortletDecorationDefault();
_52[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]=jetspeed.prefs.portletSelectorWindowTitle;
_52[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=jetspeed.prefs.portletSelectorWindowIcon;
_52[jetspeed.id.PORTLET_PROP_WIDGET_ID]=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.SELECTOR;
_52[jetspeed.id.PORTLET_PROP_WIDTH]=jetspeed.prefs.portletSelectorBounds.width;
_52[jetspeed.id.PORTLET_PROP_HEIGHT]=jetspeed.prefs.portletSelectorBounds.height;
_52[jetspeed.id.PORTLET_PROP_LEFT]=jetspeed.prefs.portletSelectorBounds.x;
_52[jetspeed.id.PORTLET_PROP_TOP]=jetspeed.prefs.portletSelectorBounds.y;
_52[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=true;
_52[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.PortletSelectorContentRetriever();
var _53=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_52);
jetspeed.ui.createPortletWindow(_53);
_53.retrieveContent(null,null);
jetspeed.getPortletDefinitions();
};
jetspeed.getPortletDefinitions=function(){
var _54=new jetspeed.om.PortletSelectorAjaxApiContentListener();
var _55="?action=getportlets";
var _56=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_55;
var _57="text/xml";
var _58=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_56,mimetype:_57},_54,_58,jetspeed.debugContentDumpIds);
};
jetspeed.searchForPortletDefinitions=function(_59,_5a){
var _5b=new jetspeed.om.PortletSelectorSearchContentListener(_5a);
var _5c="?action=getportlets&filter="+_59;
var _5d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_5c;
var _5e="text/xml";
var _5f=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_5d,mimetype:_5e},_5b,_5f,jetspeed.debugContentDumpIds);
};
jetspeed.getFolders=function(_60,_61){
var _62=new jetspeed.om.FoldersListContentListener(_61);
var _63="?action=getfolders&data="+_60;
var _64=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_63;
var _65="text/xml";
var _66=new jetspeed.om.Id("getfolders",{});
jetspeed.url.retrieveContent({url:_64,mimetype:_65},_62,_66,jetspeed.debugContentDumpIds);
};
jetspeed.portletDefinitionsforSelector=function(_67,_68,_69,_6a,_6b){
var _6c=new jetspeed.om.PortletSelectorSearchContentListener(_6b);
var _6d="?action=selectorPortlets&category="+_68+"&portletPerPages="+_6a+"&pageNumber="+_69+"&filter="+_67;
var _6e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_6d;
var _6f="text/xml";
var _70=new jetspeed.om.Id("selectorPortlets",{});
jetspeed.url.retrieveContent({url:_6e,mimetype:_6f},_6c,_70,jetspeed.debugContentDumpIds);
};
jetspeed.getActionsForPortlet=function(_71){
if(_71==null){
return;
}
jetspeed.getActionsForPortlets([_71]);
};
jetspeed.getActionsForPortlets=function(_72){
if(_72==null){
_72=jetspeed.page.getPortletIds();
}
var _73=new jetspeed.om.PortletActionsContentListener(_72);
var _74="?action=getactions";
for(var i=0;i<_72.length;i++){
_74+="&id="+_72[i];
}
var _76=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_74;
var _77="text/xml";
var _78=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_76,mimetype:_77},_73,_78,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_79,_7a,_7b,_7c){
if(_79==null){
return;
}
if(_7c==null){
_7c=new jetspeed.om.PortletChangeActionContentListener(_79);
}
var _7d="?action=window&id="+(_79!=null?_79:"");
if(_7a!=null){
_7d+="&state="+_7a;
}
if(_7b!=null){
_7d+="&mode="+_7b;
}
var _7e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7d;
var _7f="text/xml";
var _80=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_7e,mimetype:_7f},_7c,_80,jetspeed.debugContentDumpIds);
};
jetspeed.addNewPortletDefinition=function(_81,_82,_83,_84){
var _85=true;
if(_83!=null){
_85=false;
}
var _86=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(_81,_82,_85);
var _87="?action=add&id="+escape(_81.getPortletName());
if(_84!=null&&_84.length>0){
_87+="&layoutid="+escape(_84);
}
var _88=null;
if(_83!=null){
_88=_83+_87;
}else{
_88=jetspeed.page.getPsmlUrl()+_87;
}
var _89="text/xml";
var _8a=new jetspeed.om.Id("addportlet",{});
jetspeed.url.retrieveContent({url:_88,mimetype:_89},_86,_8a,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(){
if(!jetspeed.page.editMode){
var _8b=true;
var _8c=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
if(_8c!=null&&_8c=="true"){
_8b=false;
}
jetspeed.page.editMode=true;
var _8d=dojo.widget.byId(jetspeed.id.PG_ED_WID);
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets(true);
}
if(_8d==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_8d=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PG_ED_WID,editorInitiatedFromDesktop:_8b});
var _8e=document.getElementById(jetspeed.id.COLUMNS);
_8e.insertBefore(_8d.domNode,_8e.firstChild);
}
catch(e){
jetspeed.url.loadingIndicatorHide();
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets();
}
}
}else{
_8d.editPageShow();
}
jetspeed.page.syncPageControls();
}
};
jetspeed.editPageTerminate=function(){
if(jetspeed.page.editMode){
var _8f=dojo.widget.byId(jetspeed.id.PG_ED_WID);
_8f.editModeNormal();
jetspeed.page.editMode=false;
if(!_8f.editorInitiatedFromDesktop){
var _90=jetspeed.page.getPageUrl(true);
_90=jetspeed.url.removeQueryParameter(_90,jetspeed.id.PG_ED_PARAM);
_90=jetspeed.url.removeQueryParameter(_90,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_90;
}else{
var _91=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PG_ED_PARAM);
if(_91!=null&&_91=="true"){
var _92=window.location.href;
_92=jetspeed.url.removeQueryParameter(_92,jetspeed.id.PG_ED_PARAM);
window.location.href=_92;
}else{
if(_8f!=null){
_8f.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_93,_94,_95,_96){
if(!_93){
_93={};
}
jetspeed.url.retrieveContent(_93,_94,_95,_96);
}};
jetspeed.om.PortletSelectorContentRetriever=function(){
};
jetspeed.om.PortletSelectorContentRetriever.prototype={getContent:function(_97,_98,_99,_9a){
if(!_97){
_97={};
}
var _9b="<div widgetId=\""+jetspeed.id.SELECTOR+"\" dojoType=\"PortletDefContainer\"></div>";
if(!_98){
_98=new jetspeed.om.BasicContentListener();
}
_98.notifySuccess(_9b,_97.url,_99);
}};
jetspeed.om.PortletSelectorContentListener=function(){
};
jetspeed.om.PortletSelectorContentListener.prototype={notifySuccess:function(_9c,_9d,_9e){
var _9f=this.getPortletWindow();
if(_9f){
_9f.setPortletContent(_9c,renderUrl);
}
},notifyFailure:function(_a0,_a1,_a2,_a3){
dojo.raise("PortletSelectorContentListener notifyFailure url: "+_a2+" type: "+_a0+jetspeed.url.formatBindError(_a1));
}};
jetspeed.om.PageContentListenerUpdate=function(_a4){
this.previousPage=_a4;
};
jetspeed.om.PageContentListenerUpdate.prototype={notifySuccess:function(_a5,_a6,_a7){
dojo.raise("PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url="+_a6);
},notifyFailure:function(_a8,_a9,_aa,_ab){
dojo.raise("PageContentListenerUpdate notifyFailure url: "+_aa+" type: "+_a8+jetspeed.url.formatBindError(_a9));
}};
jetspeed.om.PageContentListenerCreateWidget=function(_ac){
if(typeof _ac=="undefined"){
_ac=false;
}
this.isUpdatePage=_ac;
};
jetspeed.om.PageContentListenerCreateWidget.prototype={notifySuccess:function(_ad,_ae,_af){
_af.loadFromPSML(_ad,this.isUpdatePage);
},notifyFailure:function(_b0,_b1,_b2,_b3){
dojo.raise("PageContentListenerCreateWidget error url: "+_b2+" type: "+_b0+jetspeed.url.formatBindError(_b1));
}};
jetspeed.om.Id=function(){
var _b4="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_b4.length>0){
_b4+="-";
}
_b4+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _b6 in arguments[i]){
this[_b6]=arguments[i][_b6];
}
}
}
}
this.jetspeed_om_id=_b4;
};
dojo.lang.extend(jetspeed.om.Id,{getId:function(){
return this.jetspeed_om_id;
}});
jetspeed.om.Page=function(_b7,_b8,_b9,_ba){
if(_b7!=null&&_b8!=null){
this.requiredLayoutDecorator=_b7;
this.setPsmlPathFromDocumentUrl(_b8);
this.pageUrlFallback=_b8;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _b9!="undefined"){
this.addToHistory=_b9;
}
if(typeof _ba!="undefined"){
this.editMode=_ba;
}
this.layouts={};
this.columns=[];
this.portlets=[];
this.menus=[];
};
dojo.inherits(jetspeed.om.Page,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _bb=(this.name!=null&&this.name.length>0?this.name:null);
if(!_bb){
this.getPsmlUrl();
_bb=this.psmlPath;
}
return "page-"+_bb;
},setPsmlPathFromDocumentUrl:function(_bc){
var _bd=jetspeed.url.path.AJAX_API;
var _be=null;
if(_bc==null){
_be=window.location.pathname;
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _bf=window.location.hash;
if(_bf!=null&&_bf.length>0){
if(_bf.indexOf("#")==0){
_bf=(_bf.length>1?_bf.substring(1):"");
}
if(_bf!=null&&_bf.length>1&&_bf.indexOf("/")==0){
this.psmlPath=jetspeed.url.path.AJAX_API+_bf;
return;
}
}
}
}else{
var _c0=jetspeed.url.parse(_bc);
_be=_c0.path;
}
var _c1=jetspeed.url.path.DESKTOP;
var _c2=_be.indexOf(_c1);
if(_c2!=-1&&_be.length>(_c2+_c1.length)){
_bd=_bd+_be.substring(_c2+_c1.length);
}
this.psmlPath=_bd;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _c3=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_c3=jetspeed.url.addQueryParameter(_c3,"layoutid",jetspeed.prefs.printModeOnly.layout);
_c3=jetspeed.url.addQueryParameter(_c3,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _c3;
},retrievePsml:function(_c4){
if(_c4==null){
_c4=new jetspeed.om.PageContentListenerCreateWidget();
}
var _c5=this.getPsmlUrl();
var _c6="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_c5);
}
jetspeed.url.retrieveContent({url:_c5,mimetype:_c6},_c4,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_c7,_c8){
var _c9=this._parsePSML(_c7);
if(_c9==null){
return;
}
var _ca={};
this.columnsStructure=this._layoutCreateModel(_c9,null,_ca,true);
this.rootFragmentId=_c9.id;
var _cb=false;
if(this.editMode){
this.editMode=false;
if(jetspeed.prefs.printModeOnly==null){
_cb=true;
}
}
if(jetspeed.prefs.windowTiling){
this._createColumnsStart(document.getElementById(jetspeed.id.DESKTOP));
}
var _cc=new Array();
var _cd=this.columns.length;
for(var _ce=0;_ce<=this.columns.length;_ce++){
var _cf=null;
if(_ce==_cd){
_cf=_ca["z"];
if(_cf!=null){
_cf.sort(this._loadPortletZIndexCompare);
}
}else{
_cf=_ca[_ce.toString()];
}
if(_cf!=null){
for(var i=0;i<_cf.length;i++){
var _d1=_cf[i].portlet;
_cc.push(_d1);
_d1.createPortletWindow(_ce);
}
}
}
if(jetspeed.prefs.printModeOnly==null){
if(_cc&&_cc.length>0){
jetspeed.doRenderAll(null,_cc,true,_c8);
}
this._portletsInitializeWindowState(_ca["z"]);
var _d2=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PG_ED_PARAM);
if(_cb||(_d2!=null&&_d2=="true")||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
_cb=false;
if(this.actions!=null&&(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null)){
_cb=true;
}
}
this.retrieveMenuDeclarations(true,_cb,_c8);
this.renderPageControls();
this.syncPageControls();
}else{
var _d1=null;
for(var _d3 in this.portlets){
_d1=this.portlets[_d3];
break;
}
if(_d1!=null){
_d1.renderAction(null,jetspeed.prefs.printModeOnly.action);
this._portletsInitializeWindowState(_ca["z"]);
}
if(_c8){
jetspeed.updatePageEnd();
}
}
},_parsePSML:function(_d4){
var _d5=_d4.getElementsByTagName("page");
if(!_d5||_d5.length>1){
dojo.raise("unexpected zero or multiple <page> elements in psml");
}
var _d6=_d5[0];
var _d7=_d6.childNodes;
var _d8=new RegExp("(name|path|profiledPath|title|short-title)");
var _d9=null;
var _da={};
for(var i=0;i<_d7.length;i++){
var _dc=_d7[i];
if(_dc.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _dd=_dc.nodeName;
if(_dd=="fragment"){
_d9=_dc;
}else{
if(_dd=="defaults"){
this.layoutDecorator=_dc.getAttribute("layout-decorator");
this.portletDecorator=_dc.getAttribute("portlet-decorator");
}else{
if(_dd&&_dd.match(_d8)){
this[jetspeed.purifyIdentifier(_dd,"","lo")]=((_dc&&_dc.firstChild)?_dc.firstChild.nodeValue:null);
}else{
if(_dd=="action"){
this._parsePSMLAction(_dc,_da);
}
}
}
}
}
this.actions=_da;
if(_d9==null){
dojo.raise("No root fragment in PSML.");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
jetspeed.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _de=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_de);
}
jetspeed.updatePage(_de,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_de);
}
jetspeed.updatePage(_de,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _de=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_de);
}
jetspeed.updatePage(_de,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_de);
}
jetspeed.updatePage(_de,true);
},changeUrl:escape(this.getPath())});
}
}
var _df=this._parsePSMLLayoutFragment(_d9,0);
return _df;
},_parsePSMLLayoutFragment:function(_e0,_e1){
var _e2=new Array();
var _e3=((_e0!=null)?_e0.getAttribute("type"):null);
if(_e3!="layout"){
dojo.raise("_parsePSMLLayoutFragment called with non-layout fragment: "+_e0);
return null;
}
var _e4=false;
var _e5=_e0.getAttribute("name");
if(_e5!=null){
_e5=_e5.toLowerCase();
if(_e5.indexOf("noactions")!=-1){
_e4=true;
}
}
var _e6=null,_e7=0;
var _e8={};
var _e9=_e0.childNodes;
var _ea,_eb,_ec,_ed,_ee;
for(var i=0;i<_e9.length;i++){
_ea=_e9[i];
if(_ea.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_eb=_ea.nodeName;
if(_eb=="fragment"){
_ee=_ea.getAttribute("type");
if(_ee=="layout"){
var _f0=this._parsePSMLLayoutFragment(_ea,i);
if(_f0!=null){
_e2.push(_f0);
}
}else{
var _f1=this._parsePSMLProperties(_ea,null);
var _f2=_f1[jetspeed.id.PORTLET_PROP_WINDOW_ICON];
if(_f2==null||_f2.length==0){
_f2=this._parsePSMLIcon(_ea);
if(_f2!=null&&_f2.length>0){
_f1[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=_f2;
}
}
_e2.push({id:_ea.getAttribute("id"),type:_ee,name:_ea.getAttribute("name"),properties:_f1,actions:this._parsePSMLActions(_ea,null),currentActionState:this._parsePSMLCurrentActionState(_ea),currentActionMode:this._parsePSMLCurrentActionMode(_ea),decorator:_ea.getAttribute("decorator"),layoutActionsDisabled:_e4,documentOrderIndex:i});
}
}else{
if(_eb=="property"){
if(this._parsePSMLProperty(_ea,_e8)=="sizes"){
if(_e6!=null){
dojo.raise("_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: "+_e0);
return null;
}
if(jetspeed.prefs.printModeOnly!=null){
_e6=["100"];
_e7=100;
}else{
_ed=_ea.getAttribute("value");
if(_ed!=null&&_ed.length>0){
_e6=_ed.split(",");
for(var j=0;j<_e6.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_e6[j]=_e6[j].replace(re,"$1");
_e7+=new Number(_e6[j]);
}
}
}
}
}
}
}
_e2.sort(this._fragmentRowCompare);
var _f5=new Array();
var _f6=new Array();
for(var i=0;i<_e2.length;i++){
if(_e2[i].type=="layout"){
_f5.push(i);
}else{
_f6.push(i);
}
}
if(_e6==null){
_e6=new Array();
_e6.push("100");
_e7=100;
}
return {id:_e0.getAttribute("id"),type:_e3,name:_e0.getAttribute("name"),decorator:_e0.getAttribute("decorator"),columnSizes:_e6,columnSizesSum:_e7,properties:_e8,fragments:_e2,layoutFragmentIndexes:_f5,otherFragmentIndexes:_f6,layoutActionsDisabled:_e4,documentOrderIndex:_e1};
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
},_parsePSMLCurrentActionState:function(_103){
var _104=_103.getElementsByTagName("state");
if(_104!=null&&_104.length==1&&_104[0].firstChild!=null){
return _104[0].firstChild.nodeValue;
}
return null;
},_parsePSMLCurrentActionMode:function(_105){
var _106=_105.getElementsByTagName("mode");
if(_106!=null&&_106.length==1&&_106[0].firstChild!=null){
return _106[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_107){
var _108=_107.getElementsByTagName("icon");
if(_108!=null&&_108.length==1&&_108[0].firstChild!=null){
return _108[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProperties:function(_109,_10a){
if(_10a==null){
_10a={};
}
var _10b=_109.getElementsByTagName("property");
for(var _10c=0;_10c<_10b.length;_10c++){
this._parsePSMLProperty(_10b[_10c],_10a);
}
return _10a;
},_parsePSMLProperty:function(_10d,_10e){
var _10f=_10d.getAttribute("name");
var _110=_10d.getAttribute("value");
_10e[_10f]=_110;
return _10f;
},_fragmentRowCompare:function(_111,_112){
var rowA=_111.documentOrderIndex*1000;
var rowB=_112.documentOrderIndex*1000;
var _115=_111.properties["row"];
if(_115!=null){
rowA=_115;
}
var _116=_112.properties["row"];
if(_116!=null){
rowB=_116;
}
return (rowA-rowB);
},_layoutCreateModel:function(_117,_118,_119,_11a){
var _11b=this.columns.length;
var _11c=this._layoutRegisterAndCreateColumnsModel(_117,_118,_11a);
var _11d=_11c.columnsInLayout;
if(_11c.addedLayoutHeaderColumn){
_11b++;
}
var _11e=(_11d==null?0:_11d.length);
if(_117.layoutFragmentIndexes!=null&&_117.layoutFragmentIndexes.length>0){
var _11f=null;
var _120=0;
if(_117.otherFragmentIndexes!=null&&_117.otherFragmentIndexes.length>0){
_11f=new Array();
}
for(var i=0;i<_117.fragments.length;i++){
var _122=_117.fragments[i];
}
var _123=new Array();
for(var i=0;i<_11e;i++){
if(_11f!=null){
_11f.push(null);
}
_123.push(false);
}
for(var i=0;i<_117.fragments.length;i++){
var _122=_117.fragments[i];
var _124=i;
if(_122.properties&&_122.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
if(_122.properties[jetspeed.id.PORTLET_PROP_COLUMN]!=null&&_122.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
_124=_122.properties[jetspeed.id.PORTLET_PROP_COLUMN];
}
}
if(_124>=_11e){
_124=(_11e>0?(_11e-1):0);
}
var _125=((_11f==null)?null:_11f[_124]);
if(_122.type=="layout"){
_123[_124]=true;
if(_125!=null){
this._layoutCreateModel(_125,_11d[_124],_119,true);
_11f[_124]=null;
}
this._layoutCreateModel(_122,_11d[_124],_119,false);
}else{
if(_125==null){
_120++;
var _126={};
dojo.lang.mixin(_126,_117);
_126.fragments=new Array();
_126.layoutFragmentIndexes=new Array();
_126.otherFragmentIndexes=new Array();
_126.documentOrderIndex=_117.fragments[i].documentOrderIndex;
_126.clonedFromRootId=_126.id;
_126.clonedLayoutFragmentIndex=_120;
_126.columnSizes=["100"];
_126.columnSizesSum=[100];
_126.id=_126.id+"-jsclone_"+_120;
_11f[_124]=_126;
_125=_126;
}
_125.fragments.push(_122);
_125.otherFragmentIndexes.push(_125.fragments.length-1);
}
}
if(_11f!=null){
for(var i=0;i<_11e;i++){
var _125=_11f[i];
if(_125!=null){
_123[i]=true;
this._layoutCreateModel(_125,_11d[i],_119,true);
}
}
}
for(var i=0;i<_11e;i++){
if(_123[i]){
_11d[i].columnContainer=true;
}
}
if(_117.otherFragmentIndexes!=null&&_117.otherFragmentIndexes.length>0){
var _127=new Array();
for(var i=0;i<_117.fragments.length;i++){
var _128=true;
for(var j=0;j<_117.otherFragmentIndexes.length;j++){
if(_117.otherFragmentIndexes[j]==i){
_128=false;
break;
}
}
if(_128){
_127.push(_117.fragments[i]);
}
}
_117.fragments=_127;
_117.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_117,_11d,_11b,_119);
return _11d;
},_layoutFragmentChildCollapse:function(_12a,_12b){
var _12c=false;
if(_12b==null){
_12b=_12a;
}
if(_12a.layoutFragmentIndexes!=null&&_12a.layoutFragmentIndexes.length>0){
_12c=true;
for(var i=0;i<_12a.layoutFragmentIndexes.length;i++){
var _12e=_12a.fragments[_12a.layoutFragmentIndexes[i]];
if(_12e.otherFragmentIndexes!=null&&_12e.otherFragmentIndexes.length>0){
for(var i=0;i<_12e.otherFragmentIndexes.length;i++){
var _12f=_12e.fragments[_12e.otherFragmentIndexes[i]];
_12f.properties[jetspeed.id.PORTLET_PROP_COLUMN]=-1;
_12f.properties[jetspeed.id.PORTLET_PROP_ROW]=-1;
_12f.documentOrderIndex=_12b.fragments.length;
_12b.fragments.push(_12f);
_12b.otherFragIndexes.push(_12b.fragments.length);
}
}
this._layoutFragmentChildCollapse(_12e,_12b);
}
}
return _12c;
},_layoutRegisterAndCreateColumnsModel:function(_130,_131,_132){
this.layouts[_130.id]=_130;
var _133=false;
var _134=new Array();
if(jetspeed.prefs.windowTiling&&_130.columnSizes.length>0){
var _135=false;
if(jetspeed.browser_IE){
_135=true;
}
if(_131!=null&&!_132){
var _136=new jetspeed.om.Column(0,_130.id,(_135?_130.columnSizesSum-0.1:_130.columnSizesSum),this.columns.length,_130.layoutActionsDisabled);
_136.layoutHeader=true;
this.columns.push(_136);
if(_131.columnChildren==null){
_131.columnChildren=new Array();
}
_131.columnChildren.push(_136);
_131=_136;
_133=true;
}
for(var i=0;i<_130.columnSizes.length;i++){
var size=_130.columnSizes[i];
if(_135&&i==(_130.columnSizes.length-1)){
size=size-0.1;
}
var _139=new jetspeed.om.Column(i,_130.id,size,this.columns.length,_130.layoutActionsDisabled);
this.columns.push(_139);
if(_131!=null){
if(_131.columnChildren==null){
_131.columnChildren=new Array();
}
_131.columnChildren.push(_139);
}
_134.push(_139);
}
}
return {columnsInLayout:_134,addedLayoutHeaderColumn:_133};
},_layoutCreatePortletsModel:function(_13a,_13b,_13c,_13d){
if(_13a.otherFragmentIndexes!=null&&_13a.otherFragmentIndexes.length>0){
var _13e=new Array();
for(var i=0;i<_13b.length;i++){
_13e.push(new Array());
}
for(var i=0;i<_13a.otherFragmentIndexes.length;i++){
var _140=_13a.fragments[_13a.otherFragmentIndexes[i]];
if(jetspeed.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter,_140.id)){
_140=null;
}
}
if(_140!=null){
var _141="z";
var _142=_140.properties[jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED];
var _143=jetspeed.prefs.windowTiling;
var _144=jetspeed.prefs.windowHeightExpand;
if(_142!=null&&jetspeed.prefs.windowTiling&&jetspeed.prefs.printModeOnly==null){
var _145=_142.split(jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR);
var _146=null,_147=0,_148=null,_149=null,_14a=false;
if(_145!=null&&_145.length>0){
var _14b=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
for(var _14c=0;_14c<_145.length;_14c++){
_146=_145[_14c];
_147=((_146!=null)?_146.length:0);
if(_147>0){
var _14d=_146.indexOf(_14b);
if(_14d>0&&_14d<(_147-1)){
_148=_146.substring(0,_14d);
_149=_146.substring(_14d+1);
_14a=((_149=="true")?true:false);
if(_148==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS){
_143=_14a;
}else{
if(_148==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT){
_144=_14a;
}
}
}
}
}
}
}else{
if(!jetspeed.prefs.windowTiling){
_143=false;
}
}
_140.properties[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_143;
_140.properties[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_144;
if(_143&&jetspeed.prefs.windowTiling){
var _14e=_140.properties[jetspeed.id.PORTLET_PROP_COLUMN];
if(_14e==null||_14e==""||_14e<0){
var _14f=-1;
for(var j=0;j<_13b.length;j++){
if(_14f==-1||_13e[j].length<_14f){
_14f=_13e[j].length;
_14e=j;
}
}
}else{
if(_14e>=_13b.length){
_14e=_13b.length-1;
}
}
_13e[_14e].push(_140.id);
var _151=_13c+new Number(_14e);
_141=_151.toString();
}
var _152=new jetspeed.om.Portlet(_140.name,_140.id,null,_140.properties,_140.actions,_140.currentActionState,_140.currentActionMode,_140.decorator,_140.layoutActionsDisabled);
_152.initialize();
this.putPortlet(_152);
if(_13d[_141]==null){
_13d[_141]=new Array();
}
_13d[_141].push({portlet:_152,layout:_13a.id});
}
}
}
},_portletsInitializeWindowState:function(_153){
var _154={};
this.getPortletCurrentColumnRow(null,false,_154);
for(var _155 in this.portlets){
var _156=this.portlets[_155];
var _157=_154[_156.getId()];
if(_157==null&&_153){
for(var i=0;i<_153.length;i++){
if(_153[i].portlet.getId()==_156.getId()){
_157={layout:_153[i].layout};
break;
}
}
}
if(_157!=null){
_156._initializeWindowState(_157,false);
}else{
dojo.raise("page._portletsInitializeWindowState could not find window state init data for portlet: "+_156.getId());
}
}
},_loadPortletZIndexCompare:function(_159,_15a){
var _15b=null;
var _15c=null;
var _15d=null;
_15b=_159.portlet._getInitialZIndex();
_15c=_15a.portlet._getInitialZIndex();
if(_15b&&!_15c){
return -1;
}else{
if(_15c&&!_15b){
return 1;
}else{
if(_15b==_15c){
return 0;
}
}
}
return (_15b-_15c);
},_createColumnsStart:function(_15e){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _15f=document.createElement("div");
_15f.id=jetspeed.id.COLUMNS;
_15f.setAttribute("id",jetspeed.id.COLUMNS);
for(var _160=0;_160<this.columnsStructure.length;_160++){
var _161=this.columnsStructure[_160];
this._createColumns(_161,_15f);
}
_15e.appendChild(_15f);
},_createColumns:function(_162,_163){
_162.createColumn();
if(_162.columnChildren!=null&&_162.columnChildren.length>0){
for(var _164=0;_164<_162.columnChildren.length;_164++){
var _165=_162.columnChildren[_164];
this._createColumns(_165,_162.domNode);
}
}
_163.appendChild(_162.domNode);
},_removeColumns:function(_166){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_166){
var _168=jetspeed.ui.getPortletWindowChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_168,function(_169){
_166.appendChild(_169);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _16a=dojo.byId(jetspeed.id.COLUMNS);
if(_16a){
dojo.dom.removeNode(_16a);
}
this.columns=[];
},getPortletCurrentColumnRow:function(_16b,_16c,_16d){
if(!this.columns||this.columns.length==0){
return null;
}
var _16e=null;
var _16f=((_16b!=null)?true:false);
var _170=0;
var _171=null;
var _172=null;
var _173=0;
var _174=false;
for(var _175=0;_175<this.columns.length;_175++){
var _176=this.columns[_175];
var _177=_176.domNode.childNodes;
if(_172==null||_172!=_176.getLayoutId()){
_172=_176.getLayoutId();
_171=this.layouts[_172];
if(_171==null){
dojo.raise("getPortletCurrentColumnRow cannot locate layout id: "+_172);
return null;
}
_173=0;
_174=false;
if(_171.clonedFromRootId==null){
_174=true;
}else{
var _178=this.getColumnFromColumnNode(_176.domNode.parentNode);
if(_178==null){
dojo.raise("getPortletCurrentColumnRow cannot locate parent column for column: "+_176);
return null;
}
_176=_178;
}
}
var _179=null;
for(var _17a=0;_17a<_177.length;_17a++){
var _17b=_177[_17a];
if(dojo.html.hasClass(_17b,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS)||(_16c&&dojo.html.hasClass(_17b,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))||(_16f&&dojo.html.hasClass(_17b,"desktopColumn"))){
_179=(_179==null?0:_179+1);
if((_179+1)>_173){
_173=(_179+1);
}
if(_16b==null||_17b==_16b){
var _17c={layout:_172,column:_176.getLayoutColumnIndex(),row:_179};
if(!_174){
_17c.layout=_171.clonedFromRootId;
}
if(_16b!=null){
_16e=_17c;
break;
}else{
if(_16d!=null){
var _17d=this.getPortletWindowFromNode(_17b);
if(_17d==null){
dojo.raise("getPortletCurrentColumnRow cannot locate PortletWindow for node.");
}else{
var _17e=_17d.portlet;
if(_17e==null){
dojo.raise("getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: "+_17d.widgetId);
}else{
_16d[_17e.getId()]=_17c;
}
}
}
}
}
}
}
if(_16e!=null){
break;
}
}
return _16e;
},_getPortletArrayByZIndex:function(){
var _17f=this.getPortletArray();
if(!_17f){
return _17f;
}
var _180=[];
for(var i=0;i<_17f.length;i++){
if(!_17f[i].getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_180.push(_17f[i]);
}
}
_180.sort(this._portletZIndexCompare);
return _180;
},_portletZIndexCompare:function(_182,_183){
var _184=null;
var _185=null;
var _186=null;
_186=_182.getLastSavedWindowState();
_184=_186.zIndex;
_186=_183.getLastSavedWindowState();
_185=_186.zIndex;
if(_184&&!_185){
return -1;
}else{
if(_185&&!_184){
return 1;
}else{
if(_184==_185){
return 0;
}
}
}
return (_184-_185);
},getPortletDecorationDefault:function(){
var pd=null;
if(djConfig.isDebug&&jetspeed.debug.windowDecorationRandom){
pd=jetspeed.prefs.portletDecorationsAllowed[Math.floor(Math.random()*jetspeed.prefs.portletDecorationsAllowed.length)];
}else{
var _188=this.getPortletDecorator();
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_188)!=-1){
pd=_188;
}else{
pd=jetspeed.prefs.windowDecoration;
}
}
return pd;
},getPortletArrayList:function(){
var _189=new dojo.collections.ArrayList();
for(var _18a in this.portlets){
var _18b=this.portlets[_18a];
_189.add(_18b);
}
return _189;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _18c=[];
for(var _18d in this.portlets){
var _18e=this.portlets[_18d];
_18c.push(_18e);
}
return _18c;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _18f=[];
for(var _190 in this.portlets){
var _191=this.portlets[_190];
_18f.push(_191.getId());
}
return _18f;
},getPortletByName:function(_192){
if(this.portlets&&_192){
for(var _193 in this.portlets){
var _194=this.portlets[_193];
if(_194.name==_192){
return _194;
}
}
}
return null;
},getPortlet:function(_195){
if(this.portlets&&_195){
return this.portlets[_195];
}
return null;
},getPortletWindowFromNode:function(_196){
var _197=null;
if(this.portlets&&_196){
for(var _198 in this.portlets){
var _199=this.portlets[_198];
var _19a=_199.getPortletWindow();
if(_19a!=null){
if(_19a.domNode==_196){
_197=_19a;
break;
}
}
}
}
return _197;
},putPortlet:function(_19b){
if(!_19b){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_19b.entityId]=_19b;
},removePortlet:function(_19c){
if(!_19c||!this.portlets){
return;
}
delete this.portlets[_19c.entityId];
},_destroyPortlets:function(){
for(var _19d in this.portlets){
var _19e=this.portlets[_19d];
_19e._destroy();
}
},debugLayoutInfo:function(){
var _19f="";
var i=0;
for(var _1a1 in this.layouts){
if(i>0){
_19f+="\r\n";
}
_19f+="layout["+_1a1+"]: "+jetspeed.printobj(this.layouts[_1a1],true,true,true);
i++;
}
return _19f;
},debugColumnInfo:function(){
var _1a2="";
for(var i=0;i<this.columns.length;i++){
if(i>0){
_1a2+="\r\n";
}
_1a2+=this.columns[i].toString();
}
return _1a2;
},debugDumpLastSavedWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(true);
},debugDumpWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(false);
},debugPortletActions:function(){
var _1a4=this.getPortletArray();
var _1a5="";
for(var i=0;i<_1a4.length;i++){
var _1a7=_1a4[i];
if(i>0){
_1a5+="\r\n";
}
_1a5+="portlet ["+_1a7.name+"] actions: {";
for(var _1a8 in _1a7.actions){
_1a5+=_1a8+"={"+jetspeed.printobj(_1a7.actions[_1a8],true)+"} ";
}
_1a5+="}";
}
return _1a5;
},displayAllPortlets:function(_1a9){
var _1aa=this.getPortletArray();
for(var i=0;i<_1aa.length;i++){
var _1ac=_1aa[i];
var _1ad=_1ac.getPortletWindow();
if(_1ad){
if(_1a9){
_1ad.domNode.style.display="none";
}else{
_1ad.domNode.style.display="";
}
}
}
},_debugDumpLastSavedWindowStateAllPortlets:function(_1ae){
var _1af=this.getPortletArray();
var _1b0="";
for(var i=0;i<_1af.length;i++){
var _1b2=_1af[i];
if(i>0){
_1b0+="\r\n";
}
var _1b3=null;
try{
if(_1ae){
_1b3=_1b2.getLastSavedWindowState();
}else{
_1b3=_1b2.getCurrentWindowState();
}
}
catch(e){
}
_1b0+="["+_1b2.name+"] "+((_1b3==null)?"null":jetspeed.printobj(_1b3,true));
}
return _1b0;
},resetWindowLayout:function(){
for(var _1b4 in this.portlets){
var _1b5=this.portlets[_1b4];
_1b5.submitChangedWindowState(false,true);
}
this.reload();
},reload:function(){
this._removeColumns(document.getElementById(jetspeed.id.DESKTOP));
jetspeed.loadPage();
},destroy:function(){
this._destroyPortlets();
this._destroyEditPage();
this._removeColumns(document.getElementById(jetspeed.id.DESKTOP));
this._destroyPageControls();
},getColumnFromColumnNode:function(_1b6){
if(_1b6==null){
return null;
}
var _1b7=_1b6.getAttribute("columnIndex");
if(_1b7==null){
return null;
}
var _1b8=new Number(_1b7);
if(_1b8>=0&&_1b8<this.columns.length){
return this.columns[_1b8];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1ba=null;
if(!this.columns){
return _1ba;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1ba=i;
break;
}
}
return _1ba;
},getColumnContainingNode:function(node){
var _1bd=this.getColumnIndexContainingNode(node);
return ((_1bd!=null&&_1bd>=0)?this.columns[_1bd]:null);
},getDescendantColumns:function(_1be){
var dMap={};
if(_1be==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1be&&_1be.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1c2,_1c3,_1c4){
var _1c5=new jetspeed.om.Portlet(_1c2,_1c3);
if(_1c4){
_1c5.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1c4);
}
_1c5.initialize();
this.putPortlet(_1c5);
_1c5.retrieveContent();
},removePortletFromPage:function(_1c6){
var _1c7=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1c8="?action=remove&id="+escape(portletDef.getPortletName());
var _1c9=jetspeed.page.getPsmlUrl()+_1c8;
var _1ca="text/xml";
var _1cb=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1c9,mimetype:_1ca},_1c7,_1cb,jetspeed.debugContentDumpIds);
},putMenu:function(_1cc){
if(!_1cc){
return;
}
var _1cd=(_1cc.getName?_1cc.getName():null);
if(_1cd!=null){
this.menus[_1cd]=_1cc;
}
},getMenu:function(_1ce){
if(_1ce==null){
return null;
}
return this.menus[_1ce];
},removeMenu:function(_1cf){
if(_1cf==null){
return;
}
var _1d0=null;
if(dojo.lang.isString(_1cf)){
_1d0=_1cf;
}else{
_1d0=(_1cf.getName?_1cf.getName():null);
}
if(_1d0!=null){
delete this.menus[_1d0];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1d1=[];
for(var _1d2 in this.menus){
_1d1.push(_1d2);
}
return _1d1;
},retrieveMenuDeclarations:function(_1d3,_1d4,_1d5){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1d3,_1d4,_1d5);
this.clearMenus();
var _1d6="?action=getmenus";
if(_1d3){
_1d6+="&includeMenuDefs=true";
}
var _1d7=this.getPsmlUrl()+_1d6;
var _1d8="text/xml";
var _1d9=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1d7,mimetype:_1d8},contentListener,_1d9,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1da,_1db,_1dc){
if(_1dc==null){
_1dc=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1dd="?action=getmenu&name="+_1da;
var _1de=this.getPsmlUrl()+_1dd;
var _1df="text/xml";
var _1e0=new jetspeed.om.Id("getmenu-"+_1da,{page:this,menuName:_1da,menuType:_1db});
jetspeed.url.retrieveContent({url:_1de,mimetype:_1df},_1dc,_1e0,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1e1 in this.actionButtons){
var _1e2=false;
if(_1e1==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1e2=true;
}
}else{
if(_1e1==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1e2=true;
}
}else{
if(_1e1==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1e2=true;
}
}else{
_1e2=true;
}
}
}
if(_1e2){
this.actionButtons[_1e1].style.display="";
}else{
this.actionButtons[_1e1].style.display="none";
}
}
},renderPageControls:function(){
var _1e3=[];
if(this.actions!=null){
for(var _1e4 in this.actions){
if(_1e4!=jetspeed.id.ACTION_NAME_HELP){
_1e3.push(_1e4);
}
if(_1e4==jetspeed.id.ACTION_NAME_EDIT){
_1e3.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1e3.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1e3.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1e5=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1e5!=null&&_1e3!=null&&_1e3.length>0){
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
for(var i=0;i<_1e3.length;i++){
var _1e4=_1e3[i];
var _1e7=document.createElement("div");
_1e7.className="portalPageActionButton";
_1e7.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1e4+".gif)";
_1e7.actionName=_1e4;
this.actionButtons[_1e4]=_1e7;
_1e5.appendChild(_1e7);
dojo.event.connect(_1e7,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1e8=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1e8=jetspeed.prefs.desktopActionLabels[_1e4];
}
if(_1e8==null||_1e8.length==0){
_1e8=dojo.string.capitalize(_1e4);
}
var _1e9=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1e8,connectId:_1e7,delay:"100"});
this.actionButtonTooltips.push(_1e9);
document.body.appendChild(_1e9.domNode);
}
}
}
},_destroyEditPage:function(){
var _1ea=dojo.widget.byId(jetspeed.id.PG_ED_WID);
if(_1ea!=null){
_1ea.editPageDestroy();
}
},_destroyPageControls:function(){
var _1eb=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1eb!=null&&_1eb.childNodes&&_1eb.childNodes.length>0){
for(var i=(_1eb.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1eb.childNodes[i]);
}
}
if(this.actionButtonTooltips&&this.actionButtonTooltips.length>0){
for(var i=(this.actionButtonTooltips.length-1);i>=0;i--){
this.actionButtonTooltips[i].destroy();
this.actionButtonTooltips[i]=null;
}
this.actionButtonTooltips=[];
}
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_1ee){
if(_1ee==null){
return;
}
if(_1ee==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1ee==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1ee==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1ef=this.getPageAction(_1ee);
alert("pageAction "+_1ee+" : "+_1ef);
if(_1ef==null){
return;
}
if(_1ef.url==null){
return;
}
var _1f0=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1ef.url;
jetspeed.pageNavigate(_1f0);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1f2,_1f3){
if(!_1f3){
_1f3=escape(this.getPagePathAndQuery());
}else{
_1f3=escape(_1f3);
}
var _1f4=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1f3;
if(_1f2!=null){
_1f4+="&jslayoutid="+escape(_1f2);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1f4));
},setPageModePortletActions:function(_1f5){
if(_1f5==null||_1f5.actions==null){
return;
}
if(_1f5.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1f5.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1f6){
if(this.pageUrl!=null&&!_1f6){
return this.pageUrl;
}
var _1f7=jetspeed.url.path.SERVER+((_1f6)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1f8=jetspeed.url.parse(_1f7);
var _1f9=null;
if(this.pageUrlFallback!=null){
_1f9=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1f9=jetspeed.url.parse(window.location.href);
}
if(_1f8!=null&&_1f9!=null){
var _1fa=_1f9.query;
if(_1fa!=null&&_1fa.length>0){
var _1fb=_1f8.query;
if(_1fb!=null&&_1fb.length>0){
_1f7=_1f7+"&"+_1fa;
}else{
_1f7=_1f7+"?"+_1fa;
}
}
}
if(!_1f6){
this.pageUrl=_1f7;
}
return _1f7;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1fc=this.getPath();
var _1fd=jetspeed.url.parse(_1fc);
var _1fe=null;
if(this.pageUrlFallback!=null){
_1fe=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1fe=jetspeed.url.parse(window.location.href);
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
if(!jetspeed.url.validateUrlStartsWithHttp(_206)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_206;
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
jetspeed.om.Column=function(_207,_208,size,_20a,_20b){
this.layoutColumnIndex=_207;
this.layoutId=_208;
this.size=size;
this.pageColumnIndex=new Number(_20a);
if(typeof _20b!="undefined"){
this.layoutActionsDisabled=_20b;
}
this.id="jscol_"+_20a;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_20c){
var _20d="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_20d="desktopColumn desktopColumnClear";
}
var _20e=document.createElement("div");
_20e.setAttribute("columnIndex",this.getPageColumnIndex());
_20e.style.width=this.size+"%";
if(this.layoutHeader){
_20d="desktopColumn desktopLayoutHeader";
}else{
_20e.style.minHeight="40px";
}
_20e.className=_20d;
_20e.id=this.getId();
this.domNode=_20e;
if(_20c!=null){
_20c.appendChild(_20e);
}
},containsNode:function(node){
return ((this.domNode!=null&&node!=null&&this.domNode==node.parentNode)?true:false);
},containsDescendantNode:function(node){
return ((this.domNode!=null&&node!=null&&dojo.dom.isDescendantOf(node,this.domNode,true))?true:false);
},getDescendantColumns:function(){
return jetspeed.page.getDescendantColumns(this);
},isStartOfColumnSet:function(){
return this.layoutColumnIndex==0;
},toString:function(){
var out="column["+this.pageColumnIndex+"]";
out+=" layoutCol="+this.layoutColumnIndex+" layoutId="+this.layoutId+" size="+this.size+(this.columnChildren==null?"":(" column-child-count="+this.columnChildren.length))+(this.columnContainer?" colContainer=true":"")+(this.layoutHeader?" layoutHeader=true":"");
if(this.domNode!=null){
var _212=dojo.html.getAbsolutePosition(this.domNode,true);
var _213=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_212.x)+", right:"+(_212.x+_213.width)+", top:"+(_212.y)+", bottom:"+(_212.y+_213.height)+"}";
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
jetspeed.om.Portlet=function(_214,_215,_216,_217,_218,_219,_21a,_21b,_21c){
this.name=_214;
this.entityId=_215;
if(_217){
this.properties=_217;
}else{
this.properties={};
}
if(_218){
this.actions=_218;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_219;
this.currentActionMode=_21a;
if(_216){
this.contentRetriever=_216;
}
if(_21b!=null&&_21b.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_21b)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_21b);
}
}
this.layoutActionsDisabled=false;
if(typeof _21c!="undefined"){
this.layoutActionsDisabled=_21c;
}
};
dojo.inherits(jetspeed.om.Portlet,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
if(!this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID)){
this.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,jetspeed.id.PORTLET_WINDOW_ID_PREFIX+this.entityId);
}
if(!this.getProperty(jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER)){
this.putProperty(jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER,this.contentRetriever);
}
var _21d=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_21d=="true"){
_21d=true;
}else{
if(_21d=="false"){
_21d=false;
}else{
if(_21d!=true&&_21d!=false){
_21d=true;
}
}
}
}else{
_21d=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_21d);
var _21e=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_21e=="true"){
_21e=true;
}else{
if(_21d=="false"){
_21e=false;
}else{
if(_21e!=true&&_21e!=false){
_21e=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_21e);
var _21f=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_21f&&this.name){
var re=(/^[^:]*:*/);
_21f=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_21f);
}
},postParseAnnotateHtml:function(_221){
if(_221){
var _222=_221;
var _223=_222.getElementsByTagName("form");
var _224=jetspeed.debug.postParseAnnotateHtml;
var _225=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_223){
for(var i=0;i<_223.length;i++){
var _227=_223[i];
var _228=_227.action;
var _229=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_228);
var _22a=_229.operation;
if(_22a==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_22a==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _22b=jetspeed.portleturl.generateJSPseudoUrlActionRender(_229,true);
_227.action=_22b;
var _22c=new jetspeed.om.ActionRenderFormBind(_227,_229.url,_229.portletEntityId,_22a);
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_22a+") for form with action: "+_228);
}
}else{
if(_228==null||_228.length==0){
var _22c=new jetspeed.om.ActionRenderFormBind(_227,null,this.entityId,null);
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_228);
}
}
}
}
}
var _22d=_222.getElementsByTagName("a");
if(_22d){
for(var i=0;i<_22d.length;i++){
var _22e=_22d[i];
var _22f=_22e.href;
var _229=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_22f);
var _230=null;
if(!_225){
_230=jetspeed.portleturl.generateJSPseudoUrlActionRender(_229);
}
if(!_230){
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_22f);
}
}else{
if(_230==_22f){
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_22f);
}
}else{
if(_224){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_22f+" with: "+_230);
}
_22e.href=_230;
}
}
}
}
}
},getPortletWindow:function(){
var _231=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_231){
return dojo.widget.byId(_231);
}
return null;
},getCurrentWindowState:function(_232){
var _233=this.getPortletWindow();
if(!_233){
return null;
}
var _234=_233.getCurrentWindowStateForPersistence(_232);
if(!_232){
if(_234.layout==null){
_234.layout=this.lastSavedWindowState.layout;
}
}
return _234;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_235,_236){
if(!_235){
_235={};
}
var _237=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _238=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_235[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_237;
_235[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_238;
var _239=this.getProperty("width");
if(!_236&&_239!=null&&_239>0){
_235.width=Math.floor(_239);
}else{
if(_236){
_235.width=-1;
}
}
var _23a=this.getProperty("height");
if(!_236&&_23a!=null&&_23a>0){
_235.height=Math.floor(_23a);
}else{
if(_236){
_235.height=-1;
}
}
if(!_237||!jetspeed.prefs.windowTiling){
var _23b=this.getProperty("x");
if(!_236&&_23b!=null&&_23b>=0){
_235.left=Math.floor(((_23b>0)?_23b:0));
}else{
if(_236){
_235.left=-1;
}
}
var _23c=this.getProperty("y");
if(!_236&&_23c!=null&&_23c>=0){
_235.top=Math.floor(((_23c>0)?_23c:0));
}else{
_235.top=-1;
}
var _23d=this._getInitialZIndex(_236);
if(_23d!=null){
_235.zIndex=_23d;
}
}
return _235;
},_initializeWindowState:function(_23e,_23f){
var _240=(_23e?_23e:{});
this.getInitialWindowDimensions(_240,_23f);
if(jetspeed.debug.initializeWindowState){
var _241=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_241||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_240.zIndex+" x="+_240.left+" y="+_240.top+" width="+_240.width+" height="+_240.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_240.column+" row="+_240.row+" width="+_240.width+" height="+_240.height);
}
}
this.lastSavedWindowState=_240;
return _240;
},_getInitialZIndex:function(_242){
var _243=null;
var _244=this.getProperty("z");
if(!_242&&_244!=null&&_244>=0){
_243=Math.floor(_244);
}else{
if(_242){
_243=-1;
}
}
return _243;
},_getChangedWindowState:function(_245){
var _246=this.getLastSavedWindowState();
if(_246&&dojo.lang.isEmpty(_246)){
_246=null;
_245=false;
}
var _247=this.getCurrentWindowState(_245);
var _248=_247[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _249=!_248;
if(!_246){
var _24a={state:_247,positionChanged:true,extendedPropChanged:true};
if(_249){
_24a.zIndexChanged=true;
}
return _24a;
}
var _24b=false;
var _24c=false;
var _24d=false;
var _24e=false;
for(var _24f in _247){
if(_247[_24f]!=_246[_24f]){
if(_24f==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_24f==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_24b=true;
_24d=true;
_24c=true;
}else{
if(_24f=="zIndex"){
if(_249){
_24b=true;
_24e=true;
}
}else{
_24b=true;
_24c=true;
}
}
}
}
if(_24b){
var _24a={state:_247,positionChanged:_24c,extendedPropChanged:_24d};
if(_249){
_24a.zIndexChanged=_24e;
}
return _24a;
}
return null;
},createPortletWindow:function(_250){
jetspeed.ui.createPortletWindow(this,_250);
},getPortletUrl:function(_251){
var _252=null;
if(_251&&_251.url){
_252=_251.url;
}else{
if(_251&&_251.formNode){
var _253=_251.formNode.getAttribute("action");
if(_253){
_252=_253;
}
}
}
if(_252==null){
_252=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_251.dontAddQueryArgs){
_252=jetspeed.url.parse(_252);
_252=jetspeed.url.addQueryParameter(_252,"entity",this.entityId,true);
_252=jetspeed.url.addQueryParameter(_252,"portlet",this.name,true);
_252=jetspeed.url.addQueryParameter(_252,"encoder","desktop",true);
if(_251.jsPageUrl!=null){
var _254=_251.jsPageUrl.query;
if(_254!=null&&_254.length>0){
_252=_252.toString()+"&"+_254;
}
}
}
if(_251){
_251.url=_252.toString();
}
return _252;
},_submitJetspeedAjaxApi:function(_255,_256,_257){
var _258="?action="+_255+"&id="+this.entityId+_256;
var _259=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_258;
var _25a="text/xml";
var _25b=new jetspeed.om.Id(_255,this.entityId);
_25b.portlet=this;
jetspeed.url.retrieveContent({url:_259,mimetype:_25a},_257,_25b,null);
},submitChangedWindowState:function(_25c,_25d){
var _25e=null;
if(_25d){
_25e={state:this._initializeWindowState(null,true)};
}else{
_25e=this._getChangedWindowState(_25c);
}
if(_25e){
var _25f=_25e.state;
var _260=_25f[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _261=_25f[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _262=null;
if(_25e.extendedPropChanged){
var _263=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _264=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_262=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_263+_260.toString();
_262+=_264+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_263+_261.toString();
_262=escape(_262);
}
var _265="";
var _266=null;
if(_260){
_266="moveabs";
if(_25f.column!=null){
_265+="&col="+_25f.column;
}
if(_25f.row!=null){
_265+="&row="+_25f.row;
}
if(_25f.layout!=null){
_265+="&layoutid="+_25f.layout;
}
if(_25f.height!=null){
_265+="&height="+_25f.height;
}
}else{
_266="move";
if(_25f.zIndex!=null){
_265+="&z="+_25f.zIndex;
}
if(_25f.width!=null){
_265+="&width="+_25f.width;
}
if(_25f.height!=null){
_265+="&height="+_25f.height;
}
if(_25f.left!=null){
_265+="&x="+_25f.left;
}
if(_25f.top!=null){
_265+="&y="+_25f.top;
}
}
if(_262!=null){
_265+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_262;
}
this._submitJetspeedAjaxApi(_266,_265,new jetspeed.om.MoveAjaxApiContentListener(this,_25f));
if(!_25c&&!_25d){
if(!_260&&_25e.zIndexChanged){
var _267=jetspeed.page.getPortletArrayList();
var _268=dojo.collections.Set.difference(_267,[this]);
if(!_267||!_268||((_268.count+1)!=_267.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_268&&_268.count>0){
dojo.lang.forEach(_268.toArray(),function(_269){
if(!_269.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_269.submitChangedWindowState(true);
}
});
}
}
}else{
if(_260){
}
}
}
}
},retrieveContent:function(_26a,_26b,_26c){
if(_26a==null){
_26a=new jetspeed.om.PortletContentListener(this,_26c,_26b);
}
if(!_26b){
_26b={};
}
var _26d=this;
_26d.getPortletUrl(_26b);
this.contentRetriever.getContent(_26b,_26a,_26d,jetspeed.debugContentDumpIds);
},setPortletContent:function(_26e,_26f,_270){
var _271=this.getPortletWindow();
if(_270!=null&&_270.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_270);
if(_271&&!this.loadingIndicatorIsShown()){
_271.setPortletTitle(_270);
}
}
if(_271){
_271.setPortletContent(_26e,_26f);
}
},loadingIndicatorIsShown:function(){
var _272=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _273=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _274=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _275=this.getPortletWindow();
if(_275&&(_272||_273)){
var _276=_275.getPortletTitle();
if(_276&&(_276==_272||_276==_273)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_277){
var _278=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_278=jetspeed.prefs.desktopActionLabels[_277];
if(_278!=null&&_278.length==0){
_278=null;
}
}
return _278;
},loadingIndicatorShow:function(_279){
if(_279&&!this.loadingIndicatorIsShown()){
var _27a=this._getLoadingActionLabel(_279);
var _27b=this.getPortletWindow();
if(_27b&&_27a){
_27b.setPortletTitle(_27a);
}
}
},loadingIndicatorHide:function(){
var _27c=this.getPortletWindow();
if(_27c){
_27c.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_27e){
this.properties[name]=_27e;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_281,_282){
var _283=null;
if(_281!=null){
_283=this.getAction(_281);
}
var _284=_282;
if(_284==null&&_283!=null){
_284=_283.url;
}
if(_284==null){
return;
}
var _285=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_284+jetspeed.page.getPath();
if(_281!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_285});
}else{
var _286=jetspeed.page.getPageUrl();
_286=jetspeed.url.addQueryParameter(_286,"jsprintmode","true");
_286=jetspeed.url.addQueryParameter(_286,"jsaction",escape(_283.url));
_286=jetspeed.url.addQueryParameter(_286,"jsentity",this.entityId);
_286=jetspeed.url.addQueryParameter(_286,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_286.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_288,_289,_28a){
if(_288){
this.actions=_288;
}else{
this.actions={};
}
this.currentActionState=_289;
this.currentActionMode=_28a;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _28b=this.getPortletWindow();
if(_28b){
_28b.windowActionButtonSync();
}
},_destroy:function(){
var _28c=this.getPortletWindow();
if(_28c){
_28c.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_28f,_290){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_28f;
this.submitOperation=_290;
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
this.connect(form,"onsubmit","submit");
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
this.connect(node,"onclick","click");
}
}
var _295=form.getElementsByTagName("input");
for(var i=0;i<_295.length;i++){
var _296=_295[i];
if(_296.type.toLowerCase()=="image"&&_296.form==form){
this.connect(_296,"onclick","click");
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
dojo.event.connectBefore(as[i],"onclick",this,"click");
}
form.oldSubmit=form.submit;
form.submit=function(){
form.onsubmit();
};
},onSubmit:function(_298){
var _299=true;
if(this.isFormSubmitInProgress()){
_299=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_299=false;
}
}
}
return _299;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _29b=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _29c={};
if(_29b.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_29b.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _29d=jetspeed.portleturl.generateJSPseudoUrlActionRender(_29b,true);
this.form.action=_29d;
this.submitOperation=_29b.operation;
this.entityId=_29b.portletEntityId;
_29c.url=_29b.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_29c.formFilter=dojo.lang.hitch(this,"formFilter");
_29c.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_29c),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_29c),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_29e){
if(_29e!=undefined){
this.formSubmitInProgress=_29e;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_29f,_2a0){
this.folderName=_29f;
this.folderPath=_2a0;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_2a1,_2a2,_2a3,_2a4,_2a5){
this.portletName=_2a1;
this.portletDisplayName=_2a2;
this.portletDescription=_2a3;
this.image=_2a4;
this.count=_2a5;
};
dojo.inherits(jetspeed.om.PortletDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.PortletDef,{portletName:null,portletDisplayName:null,portletDescription:null,portletImage:null,portletCount:null,getId:function(){
return this.portletName;
},getPortletName:function(){
return this.portletName;
},getPortletDisplayName:function(){
return this.portletDisplayName;
},getPortletCount:function(){
return this.portletCount;
},getPortletDescription:function(){
return this.portletDescription;
}});
jetspeed.om.BasicContentListener=function(){
};
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_2a6,_2a7,_2a8){
var _2a9=_2a8.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_2a9){
var _2aa=dojo.widget.byId(_2a9);
if(_2aa){
_2aa.setPortletContent(_2a6,_2a7);
}
}
},notifyFailure:function(type,_2ac,_2ad,_2ae){
dojo.raise("BasicContentListener notifyFailure url: "+_2ad+" type: "+type+jetspeed.url.formatBindError(_2ac));
}};
jetspeed.om.PortletContentListener=function(_2af,_2b0,_2b1){
this.portlet=_2af;
this.suppressGetActions=_2b0;
this.submittedFormBindObject=null;
if(_2b1!=null&&_2b1.submitFormBindObject!=null){
this.submittedFormBindObject=_2b1.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_2b2){
if(this.portlet==null){
return;
}
if(_2b2){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
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
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2b9,_2ba,_2bb){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_2ba+" type: "+type+jetspeed.url.formatBindError(_2b9));
}};
jetspeed.om.PortletActionContentListener=function(_2bc,_2bd){
this.portlet=_2bc;
this.submittedFormBindObject=null;
if(_2bd!=null&&_2bd.submitFormBindObject!=null){
this.submittedFormBindObject=_2bd.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2be){
if(this.portlet==null){
return;
}
if(_2be){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2bf,_2c0,_2c1,http){
var _2c3=null;
var _2c4=false;
var _2c5=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2bf);
if(_2c5.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2c5.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2c5.operation+"-url in response body: "+_2bf+"  url: "+_2c5.url+" entity-id: "+_2c5.portletEntityId);
}
_2c3=_2c5.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2bf);
}
_2c3=_2bf;
if(_2c3){
var _2c6=_2c3.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2c6==-1){
_2c4=true;
window.location.href=_2c3;
_2c3=null;
}else{
if(_2c6>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2bf);
_2c3=null;
}
}
}
}
if(_2c3!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2c3);
}
jetspeed.doRenderAll(_2c3,null,false,false);
}else{
this._setPortletLoading(false);
}
if(!_2c4&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2c8,_2c9,_2ca){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2c8));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2cb=this.getUrl();
if(_2cb){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2cb,this.getTarget());
}else{
jetspeed.updatePage(_2cb);
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
jetspeed.om.Menu=function(_2cc,_2cd){
this._is_parsed=false;
this.name=_2cc;
this.type=_2cd;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2ce){
if(!_2ce){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2ce);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2d0){
if(!this.hasOptions()){
return null;
}
if(_2d0==0||_2d0>0){
if(_2d0>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2d0];
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
var _2d2=this.options[i];
if(_2d2 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2d4,_2d5){
var _2d6=this.parseMenu(data,_2d5.menuName,_2d5.menuType);
_2d5.page.putMenu(_2d6);
},notifyFailure:function(type,_2d8,_2d9,_2da){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2da.toString()+"] url: "+_2d9+" type: "+type+jetspeed.url.formatBindError(_2d8));
},parseMenu:function(node,_2dc,_2dd){
var menu=null;
var _2df=node.getElementsByTagName("js");
if(!_2df||_2df.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2e0=_2df[0].childNodes;
for(var i=0;i<_2e0.length;i++){
var _2e2=_2e0[i];
if(_2e2.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2e3=_2e2.nodeName;
if(_2e3=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2e2,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2dc;
}
if(menu.type==null){
menu.type=_2dd;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2e6=null;
var _2e7=node.childNodes;
for(var i=0;i<_2e7.length;i++){
var _2e9=_2e7[i];
if(_2e9.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2ea=_2e9.nodeName;
if(_2ea=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e9,new jetspeed.om.Menu()));
}
}else{
if(_2ea=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e9,new jetspeed.om.MenuOption()));
}
}else{
if(_2ea=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e9,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2ea){
mObj[_2ea]=((_2e9&&_2e9.firstChild)?_2e9.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2eb,_2ec,_2ed){
this.includeMenuDefs=_2eb;
this.initiateEditMode=_2ec;
this.isUpdatePage=_2ed;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2ef,_2f0){
var _2f1=this.getMenuDefs(data,_2ef,_2f0);
for(var i=0;i<_2f1.length;i++){
var mObj=_2f1[i];
_2f0.page.putMenu(mObj);
}
this.notifyFinished(_2f0);
},getMenuDefs:function(data,_2f5,_2f6){
var _2f7=[];
var _2f8=data.getElementsByTagName("menu");
for(var i=0;i<_2f8.length;i++){
var _2fa=_2f8[i].getAttribute("type");
if(this.includeMenuDefs){
_2f7.push(this.parseMenuObject(_2f8[i],new jetspeed.om.Menu(null,_2fa)));
}else{
var _2fb=_2f8[i].firstChild.nodeValue;
_2f7.push(new jetspeed.om.Menu(_2fb,_2fa));
}
}
return _2f7;
},notifyFailure:function(type,_2fd,_2fe,_2ff){
dojo.raise("MenusAjaxApiContentListener error ["+_2ff.toString()+"] url: "+_2fe+" type: "+type+jetspeed.url.formatBindError(_2fd));
},notifyFinished:function(_300){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
jetspeed.editPageInitiate();
}
if(this.isUpdatePage){
jetspeed.updatePageEnd();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_301){
this.portletEntityId=_301;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_303,_304){
if(jetspeed.url.checkAjaxApiResponse(_303,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_305){
var _306=jetspeed.page.getPortlet(this.portletEntityId);
if(_306){
if(_305){
_306.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_306.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_308,_309,_30a){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_30a.toString()+"] url: "+_309+" type: "+type+jetspeed.url.formatBindError(_308));
}});
jetspeed.om.PageChangeActionContentListener=function(_30b){
this.pageActionUrl=_30b;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_30d,_30e){
if(jetspeed.url.checkAjaxApiResponse(_30d,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_310,_311,_312){
dojo.raise("PageChangeActionContentListener error ["+_312.toString()+"] url: "+_311+" type: "+type+jetspeed.url.formatBindError(_310));
}});
jetspeed.om.PortletActionsContentListener=function(_313){
this.portletEntityIds=_313;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_314){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _316=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_316){
if(_314){
_316.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_316.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_318,_319){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_318,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _31b=this.parsePortletActionsResponse(node);
for(var i=0;i<_31b.length;i++){
var _31d=_31b[i];
var _31e=_31d.id;
var _31f=jetspeed.page.getPortlet(_31e);
if(_31f!=null){
_31f.updateActions(_31d.actions,_31d.currentActionState,_31d.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _321=new Array();
var _322=node.getElementsByTagName("js");
if(!_322||_322.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _321;
}
var _323=_322[0].childNodes;
for(var i=0;i<_323.length;i++){
var _325=_323[i];
if(_325.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _326=_325.nodeName;
if(_326=="portlets"){
var _327=_325;
var _328=_327.childNodes;
for(var pI=0;pI<_328.length;pI++){
var _32a=_328[pI];
if(_32a.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _32b=_32a.nodeName;
if(_32b=="portlet"){
var _32c=this.parsePortletElement(_32a);
if(_32c!=null){
_321.push(_32c);
}
}
}
}
}
return _321;
},parsePortletElement:function(node){
var _32e=node.getAttribute("id");
if(_32e!=null){
var _32f=jetspeed.page._parsePSMLActions(node,null);
var _330=jetspeed.page._parsePSMLCurrentActionState(node);
var _331=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_32e,actions:_32f,currentActionState:_330,currentActionMode:_331};
}
return null;
},notifyFailure:function(type,_333,_334,_335){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_335.toString()+"] url: "+_334+" type: "+type+jetspeed.url.formatBindError(_333));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_336,_337,_338){
this.portletDef=_336;
this.windowWidgetId=_337;
this.addToCurrentPage=_338;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_33a,_33b){
if(jetspeed.url.checkAjaxApiResponse(_33a,data,true,"add-portlet")){
var _33c=this.parseAddPortletResponse(data);
if(_33c&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_33c,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _33e=null;
var _33f=node.getElementsByTagName("js");
if(!_33f||_33f.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _340=_33f[0].childNodes;
for(var i=0;i<_340.length;i++){
var _342=_340[i];
if(_342.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _343=_342.nodeName;
if(_343=="entity"){
_33e=((_342&&_342.firstChild)?_342.firstChild.nodeValue:null);
break;
}
}
return _33e;
},notifyFailure:function(type,_345,_346,_347){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_347.toString()+"] url: "+_346+" type: "+type+jetspeed.url.formatBindError(_345));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_349,_34a){
var _34b=this.parsePortlets(data);
var _34c=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_34c!=null){
for(var i=0;i<_34b.length;i++){
_34c.addChild(_34b[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_34a,_34b);
}
},notifyFailure:function(type,_34f,_350,_351){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_351.toString()+"] url: "+_350+" type: "+type+jetspeed.url.formatBindError(_34f));
},parsePortlets:function(node){
var _353=[];
var _354=node.getElementsByTagName("js");
if(!_354||_354.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _355=_354[0].childNodes;
for(var i=0;i<_355.length;i++){
var _357=_355[i];
if(_357.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _358=_357.nodeName;
if(_358=="portlets"){
var _359=_357;
var _35a=_359.childNodes;
for(var pI=0;pI<_35a.length;pI++){
var _35c=_35a[pI];
if(_35c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _35d=_35c.nodeName;
if(_35d=="portlet"){
var _35e=this.parsePortletElement(_35c);
_353.push(_35e);
}
}
}
}
return _353;
},parsePortletElement:function(node){
var _360=node.getAttribute("name");
var _361=node.getAttribute("displayName");
var _362=node.getAttribute("description");
var _363=node.getAttribute("image");
var _364=0;
return new jetspeed.om.PortletDef(_360,_361,_362,_363,_364);
}});
jetspeed.om.FoldersListContentListener=function(_365){
this.notifyFinished=_365;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_367,_368){
var _369=this.parseFolders(data);
var _36a=this.parsePages(data);
var _36b=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_368,_369,_36a,_36b);
}
},notifyFailure:function(type,_36d,_36e,_36f){
dojo.raise("FoldersListContentListener error ["+_36f.toString()+"] url: "+_36e+" type: "+type+jetspeed.url.formatBindError(_36d));
},parseFolders:function(node){
var _371=[];
var _372=node.getElementsByTagName("js");
if(!_372||_372.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _373=_372[0].childNodes;
for(var i=0;i<_373.length;i++){
var _375=_373[i];
if(_375.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _376=_375.nodeName;
if(_376=="folders"){
var _377=_375;
var _378=_377.childNodes;
for(var pI=0;pI<_378.length;pI++){
var _37a=_378[pI];
if(_37a.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _37b=_37a.nodeName;
if(_37b=="folder"){
var _37c=this.parsePortletElement(_37a);
_371.push(_37c);
}
}
}
}
return _371;
},parsePages:function(node){
var _37e=[];
var _37f=node.getElementsByTagName("js");
if(!_37f||_37f.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _380=_37f[0].childNodes;
for(var i=0;i<_380.length;i++){
var _382=_380[i];
if(_382.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _383=_382.nodeName;
if(_383=="folders"){
var _384=_382;
var _385=_384.childNodes;
for(var pI=0;pI<_385.length;pI++){
var _387=_385[pI];
if(_387.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _388=_387.nodeName;
if(_388=="page"){
var _389=this.parsePortletElement(_387);
_37e.push(_389);
}
}
}
}
return _37e;
},parseLinks:function(node){
var _38b=[];
var _38c=node.getElementsByTagName("js");
if(!_38c||_38c.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _38d=_38c[0].childNodes;
for(var i=0;i<_38d.length;i++){
var _38f=_38d[i];
if(_38f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _390=_38f.nodeName;
if(_390=="folders"){
var _391=_38f;
var _392=_391.childNodes;
for(var pI=0;pI<_392.length;pI++){
var _394=_392[pI];
if(_394.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _395=_394.nodeName;
if(_395=="link"){
var _396=this.parsePortletElement(_394);
_38b.push(_396);
}
}
}
}
return _38b;
},parsePortletElement:function(node){
var _398=node.getAttribute("name");
var _399=node.getAttribute("path");
return new jetspeed.om.FolderDef(_398,_399);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_39a){
this.notifyFinished=_39a;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_39c,_39d){
var _39e=this.parsePortlets(data);
var _39f=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_39d,_39e,_39f);
}
},notifyFailure:function(type,_3a1,_3a2,_3a3){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_3a3.toString()+"] url: "+_3a2+" type: "+type+jetspeed.url.formatBindError(_3a1));
},parsList:function(node){
var _3a5;
var _3a6=node.getElementsByTagName("js");
if(!_3a6||_3a6.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _3a7=_3a6[0].childNodes;
for(var i=0;i<_3a7.length;i++){
var _3a9=_3a7[i];
if(_3a9.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3aa=_3a9.nodeName;
if(_3aa=="resultCount"){
_3a5=_3a9.textContent;
}
}
return _3a5;
},parsePortlets:function(node){
var _3ac=[];
var _3ad=node.getElementsByTagName("js");
if(!_3ad||_3ad.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _3ae=_3ad[0].childNodes;
for(var i=0;i<_3ae.length;i++){
var _3b0=_3ae[i];
if(_3b0.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3b1=_3b0.nodeName;
if(_3b1=="portlets"){
var _3b2=_3b0;
var _3b3=_3b2.childNodes;
for(var pI=0;pI<_3b3.length;pI++){
var _3b5=_3b3[pI];
if(_3b5.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3b6=_3b5.nodeName;
if(_3b6=="portlet"){
var _3b7=this.parsePortletElement(_3b5);
_3ac.push(_3b7);
}
}
}
}
return _3ac;
},parsePortletElement:function(node){
var _3b9=node.getAttribute("name");
var _3ba=node.getAttribute("displayName");
var _3bb=node.getAttribute("description");
var _3bc=node.getAttribute("image");
var _3bd=node.getAttribute("count");
return new jetspeed.om.PortletDef(_3b9,_3ba,_3bb,_3bc,_3bd);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3be,_3bf){
this.portlet=_3be;
this.changedState=_3bf;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3c0){
if(this.portlet==null){
return;
}
if(_3c0){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3c2,_3c3){
this._setPortletLoading(false);
dojo.lang.mixin(_3c3.portlet.lastSavedWindowState,this.changedState);
var _3c4=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3c4=true;
}
jetspeed.url.checkAjaxApiResponse(_3c2,data,_3c4,("move-portlet ["+_3c3.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3c6,_3c7,_3c8){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3c8.entityId+"] url: "+_3c7+" type: "+type+jetspeed.url.formatBindError(_3c6));
}};
jetspeed.ui.getPortletWindowChildren=function(_3c9,_3ca,_3cb,_3cc){
if(_3cb||_3cc){
_3cb=true;
}
var _3cd=null;
var _3ce=-1;
if(_3c9){
_3cd=[];
var _3cf=_3c9.childNodes;
if(_3cf!=null&&_3cf.length>0){
for(var i=0;i<_3cf.length;i++){
var _3d1=_3cf[i];
if((!_3cc&&dojo.html.hasClass(_3d1,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3cb&&dojo.html.hasClass(_3d1,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3cd.push(_3d1);
if(_3ca&&_3d1==_3ca){
_3ce=_3cd.length-1;
}
}else{
if(_3ca&&_3d1==_3ca){
_3cd.push(_3d1);
_3ce=_3cd.length-1;
}
}
}
}
}
return {portletWindowNodes:_3cd,matchIndex:_3ce};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3d2){
var _3d3=null;
if(_3d2){
_3d3=new Array();
for(var i=0;i<_3d2.length;i++){
var _3d5=dojo.widget.byNode(_3d2[i]);
if(_3d5){
_3d3.push(_3d5);
}
}
}
return _3d3;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d7=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3d7.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d9=jetspeed.page.columns[i];
var _3da=jetspeed.ui.getPortletWindowChildren(_3d9.domNode,null);
var _3db=jetspeed.ui.getPortletWindowsFromNodes(_3da.portletWindowNodes);
var _3dc={dumpMsg:""};
if(_3db!=null){
dojo.lang.forEach(_3db,function(_3dd){
_3dc.dumpMsg=_3dc.dumpMsg+(_3dc.dumpMsg.length>0?", ":"")+_3dd.portlet.entityId;
});
}
_3dc.dumpMsg="column "+i+": "+_3dc.dumpMsg;
dojo.debug(_3dc.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3de=jetspeed.ui.getAllPortletWindowWidgets();
var _3df="";
for(var i=0;i<_3de.length;i++){
if(i>0){
_3df+=", ";
}
_3df+=_3de[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3df);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3e1=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3e2=jetspeed.ui.getPortletWindowsFromNodes(_3e1.portletWindowNodes);
if(_3e2==null){
_3e2=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3e4=jetspeed.page.columns[i];
var _3e5=jetspeed.ui.getPortletWindowChildren(_3e4.domNode,null);
var _3e6=jetspeed.ui.getPortletWindowsFromNodes(_3e5.portletWindowNodes);
if(_3e6!=null){
_3e2=_3e2.concat(_3e6);
}
}
return _3e2;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3e7,_3e8){
var _3e9=_3e7.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3e9==null){
_3e9=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3e9=false;
}
}
var _3ea=dojo.widget.byId(_3e7.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3ea){
_3ea.resetWindow(_3e7);
}else{
_3ea=jetspeed.ui.createPortletWindowWidget(_3e7);
}
if(_3ea){
if(!_3e9||_3e8>=jetspeed.page.columns.length){
_3ea.domNode.style.position="absolute";
var _3eb=document.getElementById(jetspeed.id.DESKTOP);
_3eb.appendChild(_3ea.domNode);
}else{
var _3ec=null;
var _3ed=-1;
var _3ee=_3e8;
if(_3ee!=null&&_3ee>=0&&_3ee<jetspeed.page.columns.length){
_3ed=_3ee;
_3ec=jetspeed.page.columns[_3ed];
}
if(_3ed==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3f0=jetspeed.page.columns[i];
if(!_3f0.domNode.hasChildNodes()){
_3ec=_3f0;
_3ed=i;
break;
}
if(_3ec==null||_3ec.domNode.childNodes.length>_3f0.domNode.childNodes.length){
_3ec=_3f0;
_3ed=i;
}
}
}
if(_3ec){
_3ec.domNode.appendChild(_3ea.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3f1,_3f2){
if(!_3f2){
_3f2={};
}
if(_3f1 instanceof jetspeed.om.Portlet){
_3f2.portlet=_3f1;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3f2,_3f1);
}
var _3f3=dojo.widget.createWidget("jetspeed:PortletWindow",_3f2);
return _3f3;
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3f4=jetspeed.debugWindowReadCookie(true);
var _3f5={};
var _3f6=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3f5[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3f6;
_3f5[jetspeed.id.PORTLET_PROP_WIDTH]=_3f4.width;
_3f5[jetspeed.id.PORTLET_PROP_HEIGHT]=_3f4.height;
_3f5[jetspeed.id.PORTLET_PROP_LEFT]=_3f4.left;
_3f5[jetspeed.id.PORTLET_PROP_TOP]=_3f4.top;
_3f5[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3f5[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3f5[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3f4.windowState;
var _3f7=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3f5);
jetspeed.ui.createPortletWindow(_3f7);
_3f7.retrieveContent(null,null);
var _3f8=dojo.widget.byId(_3f6);
var _3f9=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3f8,"contentChanged");
dojo.event.connect(_3f8,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3f8,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3f8,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3fa){
var _3fb={};
if(_3fa){
_3fb={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _3fc=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_3fc!=null&&_3fc.length>0){
var _3fd=_3fc.split("|");
if(_3fd&&_3fd.length>=4){
_3fb.width=_3fd[0];
_3fb.height=_3fd[1];
_3fb.top=_3fd[2];
_3fb.left=_3fd[3];
if(_3fd.length>4&&_3fd[4]!=null&&_3fd[4].length>0){
_3fb.windowState=_3fd[4];
}
}
}
return _3fb;
};
jetspeed.debugWindowRestore=function(){
var _3fe=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3ff=dojo.widget.byId(_3fe);
if(!_3ff){
return;
}
_3ff.restoreWindow();
};
jetspeed.debugWindow=function(){
var _400=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_400);
};
jetspeed.debugWindowSave=function(){
var _401=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _402=dojo.widget.byId(_401);
if(!_402){
return null;
}
if(!_402.windowPositionStatic){
var _403=_402.getCurrentWindowStateForPersistence(false);
var _404=_403.width;
var _405=_403.height;
var cTop=_403.top;
var _407=_403.left;
if(_402.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _408=_402.getLastPositionInfo();
if(_408!=null){
if(_408.height!=null&&_408.height>0){
_405=_408.height;
}
}else{
var _409=jetspeed.debugWindowReadCookie(false);
if(_409.height!=null&&_409.height>0){
_405=_409.height;
}
}
}
var _40a=_404+"|"+_405+"|"+cTop+"|"+_407+"|"+_402.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_40a,30,"/");
}
};
jetspeed.debugDumpForm=function(_40b){
if(!_40b){
return null;
}
var _40c=_40b.toString();
if(_40b.name){
_40c+=" name="+_40b.name;
}
if(_40b.id){
_40c+=" id="+_40b.id;
}
var _40d=dojo.io.encodeForm(_40b);
_40c+=" data="+_40d;
return _40c;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_40e,_40f,_410,_411){
if(!_40e){
_40e={};
}
if(!this.initialized){
var _412="";
if(jetspeed.altDebugWindowContent){
_412=jetspeed.altDebugWindowContent();
}else{
_412+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_40f){
_40f=new jetspeed.om.BasicContentListener();
}
_40f.notifySuccess(_412,_40e.url,_410);
this.initialized=true;
var _413=jetspeed.debugWindow();
var _414="javascript: void(document.getElementById('"+jetspeed.debug.debugContainerId+"').innerHTML='')";
var _415="";
for(var i=0;i<20;i++){
_415+="&nbsp;";
}
var _417=_413.title+_415+"<a href=\""+_414+"\"><span style=\"font-size: xx-small; font-weight: normal\">Clear</span></a>";
_413.titleBarText.innerHTML=_417;
}
}};

