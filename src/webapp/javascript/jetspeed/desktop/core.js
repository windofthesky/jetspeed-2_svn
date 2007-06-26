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
jetspeed.id={PAGE:"jetspeedPage",DESKTOP_CELL:"jetspeedDesktopCell",DESKTOP:"jetspeedDesktop",COLUMNS:"jetspeedColumns",PAGE_CONTROLS:"jetspeedPageControls",TASKBAR:"jetspeedTaskbar",SELECTOR:"jetspeedSelector",PORTLET_STYLE_CLASS:"portlet",PORTLET_WINDOW_STYLE_CLASS:"dojoFloatingPane",PORTLET_WINDOW_GHOST_STYLE_CLASS:"ghostPane",PORTLET_WINDOW_ID_PREFIX:"portletWindow_",PORTLET_PROP_WIDGET_ID:"widgetId",PORTLET_PROP_CONTENT_RETRIEVER:"contentRetriever",PORTLET_PROP_DESKTOP_EXTENDED:"jsdesktop",PORTLET_PROP_WINDOW_POSITION_STATIC:"windowPositionStatic",PORTLET_PROP_WINDOW_HEIGHT_TO_FIT:"windowHeightToFit",PORTLET_PROP_WINDOW_DECORATION:"windowDecoration",PORTLET_PROP_WINDOW_TITLE:"title",PORTLET_PROP_WINDOW_ICON:"windowIcon",PORTLET_PROP_WIDTH:"width",PORTLET_PROP_HEIGHT:"height",PORTLET_PROP_LEFT:"left",PORTLET_PROP_TOP:"top",PORTLET_PROP_COLUMN:"column",PORTLET_PROP_ROW:"row",PORTLET_PROP_EXCLUDE_PCONTENT:"excludePContent",PORTLET_PROP_WINDOW_STATE:"windowState",PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS:"staticpos",PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT:"fitheight",PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR:"=",PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR:";",ACTION_NAME_MENU:"menu",ACTION_NAME_MINIMIZE:"minimized",ACTION_NAME_MAXIMIZE:"maximized",ACTION_NAME_RESTORE:"normal",ACTION_NAME_PRINT:"print",ACTION_NAME_EDIT:"edit",ACTION_NAME_VIEW:"view",ACTION_NAME_HELP:"help",ACTION_NAME_ADDPORTLET:"addportlet",ACTION_NAME_REMOVEPORTLET:"removeportlet",ACTION_NAME_DESKTOP_TILE:"tile",ACTION_NAME_DESKTOP_UNTILE:"untile",ACTION_NAME_DESKTOP_HEIGHT_EXPAND:"heightexpand",ACTION_NAME_DESKTOP_HEIGHT_NORMAL:"heightnormal",ACTION_NAME_LOAD_RENDER:"loadportletrender",ACTION_NAME_LOAD_ACTION:"loadportletaction",ACTION_NAME_LOAD_UPDATE:"loadportletupdate",PORTLET_ACTION_TYPE_MODE:"mode",PORTLET_ACTION_TYPE_STATE:"state",MENU_WIDGET_ID_PREFIX:"jetspeed-menu-",PAGE_EDITOR_WIDGET_ID:"jetspeed-page-editor",PAGE_EDITOR_INITIATE_PARAMETER:"editPage",PORTAL_ORIGINATE_PARAMETER:"portal",DEBUG_WINDOW_TAG:"js-dojo-debug"};
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
var _8=jetspeed.url.parse(document.location.href);
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
jetspeed.debugWindowLoad();
if(jetspeed.prefs.printModeOnly!=null){
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
if(!_c||jetspeed.pageNavigateSuppress){
return;
}
if(_e&&_e.equalsPageUrl(_c)){
return;
}
_c=jetspeed.page.makePageUrl(_c);
if(_e!=null&&_c!=null){
var _f=_e.getPageUrl();
_e.destroy();
var _10=new jetspeed.om.Page(jetspeed.page.layoutDecorator,_c,(!djConfig.preventBackButtonFix&&!_d));
jetspeed.page=_10;
_10.retrievePsml();
}
};
jetspeed.doRender=function(_11,_12){
if(!_11){
_11={};
}else{
if((typeof _11=="string"||_11 instanceof String)){
_11={url:_11};
}
}
var _13=jetspeed.page.getPortlet(_12);
if(_13){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_12+"] url: "+_11.url);
}
_13.retrieveContent(null,_11);
}
};
jetspeed.doRenderAll=function(url,_15,_16){
var _17=jetspeed.debug.doRenderDoAction;
var _18=jetspeed.debug.pageLoad&&_16;
if(!_15){
_15=jetspeed.page.getPortletArray();
}
var _19="";
var _1a=true;
var _1b=null;
if(_16){
_1b=jetspeed.url.parse(jetspeed.page.getPageUrl());
}
for(var i=0;i<_15.length;i++){
var _1d=_15[i];
if((_17||_18)){
if(i>0){
_19=_19+", ";
}
var _1e=null;
if(_1d.getProperty!=null){
_1e=_1d.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
}
if(!_1e){
_1e=_1d.widgetId;
}
if(!_1e){
_1e=_1d.toString();
}
if(_1d.entityId){
_19=_19+_1d.entityId+"("+_1e+")";
if(_18&&_1d.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE)){
_19=_19+" "+_1d.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
}
}else{
_19=_19+_1e;
}
}
_1d.retrieveContent(null,{url:url,jsPageUrl:_1b},_1a);
}
if(_17){
dojo.debug("doRenderAll ["+_19+"] url: "+url);
}else{
if(_18){
dojo.debug("doRenderAll page-url: "+jetspeed.page.getPsmlUrl()+" portlets: ["+_19+"]"+(url?(" url: "+url):""));
}
}
};
jetspeed.doAction=function(_1f,_20){
if(!_1f){
_1f={};
}else{
if((typeof _1f=="string"||_1f instanceof String)){
_1f={url:_1f};
}
}
var _21=jetspeed.page.getPortlet(_20);
if(_21){
if(jetspeed.debug.doRenderDoAction){
if(!_1f.formNode){
dojo.debug("doAction ["+_20+"] url: "+_1f.url+" form: null");
}else{
dojo.debug("doAction ["+_20+"] url: "+_1f.url+" form: "+jetspeed.debugDumpForm(_1f.formNode));
}
}
_21.retrieveContent(new jetspeed.om.PortletActionContentListener(_21,_1f),_1f);
}
};
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrlForDesktopActionRender:function(_22){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _24=_22;
var _25=null;
if(_22&&_22.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_22.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_22&&_22.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_22.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_25=jetspeed.url.getQueryParameter(_22,"entity");
}
if(!jetspeed.url.validateUrlStartsWithHttp(_24)){
_24=null;
}
return {url:_24,operation:op,portletEntityId:_25};
},generateJSPseudoUrlActionRender:function(_26,_27){
if(!_26||!_26.url||!_26.portletEntityId){
return null;
}
var _28=null;
if(_27){
_28=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_28="javascript:";
var _29=false;
if(_26.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_28+="doAction(\"";
}else{
if(_26.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_28+="doRender(\"";
}else{
_29=true;
}
}
if(_29){
return null;
}
_28+=_26.url+"\",\""+_26.portletEntityId+"\"";
_28+=")";
}
return _28;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_2a){
var _2b=jetspeed.prefs.getPortletDecorationConfig(_2a);
if(_2b!=null&&!_2b.css_loaded){
var _2c=jetspeed.prefs.getPortletDecorationBaseUrl(_2a);
_2b.css_loaded=true;
_2b.cssPathCommon=new dojo.uri.Uri(_2c+"/css/styles.css");
_2b.cssPathDesktop=new dojo.uri.Uri(_2c+"/css/desktop.css");
dojo.html.insertCssFile(_2b.cssPathCommon,null,true);
dojo.html.insertCssFile(_2b.cssPathDesktop,null,true);
}
return _2b;
};
jetspeed.loadPortletDecorationConfig=function(_2d){
var _2e={};
jetspeed.prefs.portletDecorationsConfig[_2d]=_2e;
_2e.windowActionButtonOrder=jetspeed.prefs.windowActionButtonOrder;
_2e.windowActionNotPortlet=jetspeed.prefs.windowActionNotPortlet;
_2e.windowActionButtonMax=jetspeed.prefs.windowActionButtonMax;
_2e.windowActionButtonHide=jetspeed.prefs.windowActionButtonHide;
_2e.windowActionButtonTooltip=jetspeed.prefs.windowActionButtonTooltip;
_2e.windowActionMenuOrder=jetspeed.prefs.windowActionMenuOrder;
_2e.windowActionNoImage=jetspeed.prefs.windowActionNoImage;
_2e.windowIconEnabled=jetspeed.prefs.windowIconEnabled;
_2e.windowIconPath=jetspeed.prefs.windowIconPath;
var _2f=jetspeed.prefs.getPortletDecorationBaseUrl(_2d)+"/"+_2d+".js";
dojo.hostenv.loadUri(_2f,function(_30){
for(var j in _30){
_2e[j]=_30[j];
}
if(_2e.windowActionNoImage!=null){
var _32={};
for(var i=0;i<_2e.windowActionNoImage.length;i++){
_32[_2e.windowActionNoImage[i]]=true;
}
_2e.windowActionNoImage=_32;
}
if(_2e.windowIconPath!=null){
_2e.windowIconPath=dojo.string.trim(_2e.windowIconPath);
if(_2e.windowIconPath==null||_2e.windowIconPath.length==0){
_2e.windowIconPath=null;
}else{
var _34=_2e.windowIconPath;
var _35=_34.charAt(0);
if(_35!="/"){
_34="/"+_34;
}
var _36=_34.charAt(_34.length-1);
if(_36!="/"){
_34=_34+"/";
}
_2e.windowIconPath=_34;
}
}
});
};
jetspeed.purifyIdentifier=function(src,_38,_39){
if(src==null){
return src;
}
var _3a=src.length;
if(_3a==0){
return src;
}
if(_38==null){
_38="_";
}
var _3b=new RegExp("[^a-z_0-9A-Z]","g");
var _3c=src.charCodeAt(0);
var _3d=null;
if((_3c>=65&&_3c<=90)||_3c==95||(_3c>=97&&_3c<=122)){
_3d=src.charAt(0);
}else{
_3d=_38;
}
var _3e=false,_3f=false;
if(_39!=null){
_39=_39.toLowerCase();
_3e=(_39=="hi"?true:false);
_3f=(_39=="lo"?true:false);
}
if(_3a>1){
if(_3e||_3f){
upNext=false;
for(var i=1;i<_3a;i++){
_3c=src.charCodeAt(i);
if((_3c>=65&&_3c<=90)||_3c==95||(_3c>=97&&_3c<=122)||(_3c>=48&&_3c<=57)){
if(upNext&&(_3c>=97&&_3c<=122)){
_3d+=String.fromCharCode(_3c-32);
}else{
_3d+=src.charAt(i);
}
upNext=false;
}else{
upNext=true;
_3d+=_38;
}
}
}else{
_3d+=src.substring(1).replace(_3b,_38);
}
}
if(_3e){
_3c=_3d.charCodeAt(0);
if(_3c>=97&&_3c<=122){
_3d=String.fromCharCode(_3c-32)+_3d.substring(1);
}
}
return _3d;
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
jetspeed.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _41=jetspeed.page.getMenuNames();
for(var i=0;i<_41.length;i++){
var _43=_41[i];
var _44=dojo.widget.byId(jetspeed.id.MENU_WIDGET_ID_PREFIX+_43);
if(_44){
_44.createJetspeedMenu(jetspeed.page.getMenu(_43));
}
}
jetspeed.url.loadingIndicatorHide();
jetspeed.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_45){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_45);
}
};
jetspeed.menuNavClickWidget=function(_46,_47){
dojo.debug("jetspeed.menuNavClick");
if(!_46){
return;
}
if(dojo.lang.isString(_46)){
var _48=_46;
_46=dojo.widget.byId(_48);
if(!_46){
dojo.raise("menuNavClick could not find tab widget for "+_48);
}
}
if(_46){
var _49=_46.jetspeedmenuname;
if(!_49&&_46.extraArgs){
_49=_46.extraArgs.jetspeedmenuname;
}
if(!_49){
dojo.raise("menuNavClick tab widget ["+_46.widgetId+"] does not define jetspeedMenuName");
}
var _4a=jetspeed.page.getMenu(_49);
if(!_4a){
dojo.raise("menuNavClick Menu lookup for tab widget ["+_46.widgetId+"] failed: "+_49);
}
var _4b=_4a.getOptionByIndex(_47);
jetspeed.menuNavClick(_4b);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_4c,_4d,_4e){
if(!_4c||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _4e=="undefined"){
_4e=false;
}
if(!_4e&&jetspeed.page&&jetspeed.page.equalsPageUrl(_4c)){
return;
}
_4c=jetspeed.page.makePageUrl(_4c);
if(_4d=="top"){
top.location.href=_4c;
}else{
if(_4d=="parent"){
parent.location.href=_4c;
}else{
window.location.href=_4c;
}
}
};
jetspeed.loadPortletSelector=function(){
var _4f={};
_4f[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_4f[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_4f[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.page.getPortletDecorationDefault();
_4f[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]=jetspeed.prefs.portletSelectorWindowTitle;
_4f[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=jetspeed.prefs.portletSelectorWindowIcon;
_4f[jetspeed.id.PORTLET_PROP_WIDGET_ID]=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.SELECTOR;
_4f[jetspeed.id.PORTLET_PROP_WIDTH]=jetspeed.prefs.portletSelectorBounds.width;
_4f[jetspeed.id.PORTLET_PROP_HEIGHT]=jetspeed.prefs.portletSelectorBounds.height;
_4f[jetspeed.id.PORTLET_PROP_LEFT]=jetspeed.prefs.portletSelectorBounds.x;
_4f[jetspeed.id.PORTLET_PROP_TOP]=jetspeed.prefs.portletSelectorBounds.y;
_4f[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=true;
_4f[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.PortletSelectorContentRetriever();
var _50=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_4f);
jetspeed.ui.createPortletWindow(_50);
_50.retrieveContent(null,null);
jetspeed.getPortletDefinitions();
};
jetspeed.getPortletDefinitions=function(){
var _51=new jetspeed.om.PortletSelectorAjaxApiContentListener();
var _52="?action=getportlets";
var _53=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_52;
var _54="text/xml";
var _55=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_53,mimetype:_54},_51,_55,jetspeed.debugContentDumpIds);
};
jetspeed.searchForPortletDefinitions=function(_56,_57){
var _58=new jetspeed.om.PortletSelectorSearchContentListener(_57);
var _59="?action=getportlets&filter="+_56;
var _5a=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_59;
var _5b="text/xml";
var _5c=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_5a,mimetype:_5b},_58,_5c,jetspeed.debugContentDumpIds);
};
jetspeed.getFolders=function(_5d,_5e){
var _5f=new jetspeed.om.FoldersListContentListener(_5e);
var _60="?action=getfolders&data="+_5d;
var _61=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_60;
var _62="text/xml";
var _63=new jetspeed.om.Id("getfolders",{});
jetspeed.url.retrieveContent({url:_61,mimetype:_62},_5f,_63,jetspeed.debugContentDumpIds);
};
jetspeed.portletDefinitionsforSelector=function(_64,_65,_66,_67,_68){
var _69=new jetspeed.om.PortletSelectorSearchContentListener(_68);
var _6a="?action=selectorPortlets&category="+_65+"&portletPerPages="+_67+"&pageNumber="+_66+"&filter="+_64;
var _6b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_6a;
var _6c="text/xml";
var _6d=new jetspeed.om.Id("selectorPortlets",{});
jetspeed.url.retrieveContent({url:_6b,mimetype:_6c},_69,_6d,jetspeed.debugContentDumpIds);
};
jetspeed.getActionsForPortlet=function(_6e){
if(_6e==null){
return;
}
jetspeed.getActionsForPortlets([_6e]);
};
jetspeed.getActionsForPortlets=function(_6f){
if(_6f==null){
_6f=jetspeed.page.getPortletIds();
}
var _70=new jetspeed.om.PortletActionsContentListener(_6f);
var _71="?action=getactions";
for(var i=0;i<_6f.length;i++){
_71+="&id="+_6f[i];
}
var _73=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_71;
var _74="text/xml";
var _75=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_73,mimetype:_74},_70,_75,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_76,_77,_78,_79){
if(_76==null){
return;
}
if(_79==null){
_79=new jetspeed.om.PortletChangeActionContentListener(_76);
}
var _7a="?action=window&id="+(_76!=null?_76:"");
if(_77!=null){
_7a+="&state="+_77;
}
if(_78!=null){
_7a+="&mode="+_78;
}
var _7b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7a;
var _7c="text/xml";
var _7d=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_7b,mimetype:_7c},_79,_7d,jetspeed.debugContentDumpIds);
};
jetspeed.addNewPortletDefinition=function(_7e,_7f,_80,_81){
var _82=true;
if(_80!=null){
_82=false;
}
var _83=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(_7e,_7f,_82);
var _84="?action=add&id="+escape(_7e.getPortletName());
if(_81!=null&&_81.length>0){
_84+="&layoutid="+escape(_81);
}
var _85=null;
if(_80!=null){
_85=_80+_84;
}else{
_85=jetspeed.page.getPsmlUrl()+_84;
}
var _86="text/xml";
var _87=new jetspeed.om.Id("addportlet",{});
jetspeed.url.retrieveContent({url:_85,mimetype:_86},_83,_87,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(){
if(!jetspeed.page.editMode){
var _88=true;
var _89=jetspeed.url.getQueryParameter(document.location.href,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
if(_89!=null&&_89=="true"){
_88=false;
}
jetspeed.page.editMode=true;
var _8a=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(_8a==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_8a=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PAGE_EDITOR_WIDGET_ID,editorInitiatedFromDesktop:_88});
var _8b=document.getElementById(jetspeed.id.COLUMNS);
_8b.insertBefore(_8a.domNode,_8b.firstChild);
}
catch(e){
jetspeed.url.loadingIndicatorHide();
}
}else{
_8a.editPageShow();
}
jetspeed.page.syncPageControls();
}
};
jetspeed.editPageTerminate=function(){
if(jetspeed.page.editMode){
var _8c=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
_8c.editModeNormal();
jetspeed.page.editMode=false;
if(!_8c.editorInitiatedFromDesktop){
var _8d=jetspeed.page.getPageUrl(true);
_8d=jetspeed.url.removeQueryParameter(_8d,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
_8d=jetspeed.url.removeQueryParameter(_8d,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_8d;
}else{
if(_8c!=null){
_8c.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_8e,_8f,_90,_91){
if(!_8e){
_8e={};
}
jetspeed.url.retrieveContent(_8e,_8f,_90,_91);
}};
jetspeed.om.PortletSelectorContentRetriever=function(){
};
jetspeed.om.PortletSelectorContentRetriever.prototype={getContent:function(_92,_93,_94,_95){
if(!_92){
_92={};
}
var _96="<div widgetId=\""+jetspeed.id.SELECTOR+"\" dojoType=\"PortletDefContainer\"></div>";
if(!_93){
_93=new jetspeed.om.BasicContentListener();
}
_93.notifySuccess(_96,_92.url,_94);
}};
jetspeed.om.PortletSelectorContentListener=function(){
};
jetspeed.om.PortletSelectorContentListener.prototype={notifySuccess:function(_97,_98,_99){
var _9a=this.getPortletWindow();
if(_9a){
_9a.setPortletContent(_97,renderUrl);
}
},notifyFailure:function(_9b,_9c,_9d,_9e){
dojo.raise("PortletSelectorContentListener notifyFailure url: "+_9d+" type: "+_9b+jetspeed.url.formatBindError(_9c));
}};
jetspeed.om.PageContentListenerUpdate=function(_9f){
this.previousPage=_9f;
};
jetspeed.om.PageContentListenerUpdate.prototype={notifySuccess:function(_a0,_a1,_a2){
dojo.raise("PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url="+_a1);
},notifyFailure:function(_a3,_a4,_a5,_a6){
dojo.raise("PageContentListenerUpdate notifyFailure url: "+_a5+" type: "+_a3+jetspeed.url.formatBindError(_a4));
}};
jetspeed.om.PageContentListenerCreateWidget=function(){
};
jetspeed.om.PageContentListenerCreateWidget.prototype={notifySuccess:function(_a7,_a8,_a9){
_a9.loadFromPSML(_a7);
},notifyFailure:function(_aa,_ab,_ac,_ad){
dojo.raise("PageContentListenerCreateWidget error url: "+_ac+" type: "+_aa+jetspeed.url.formatBindError(_ab));
}};
jetspeed.om.Id=function(){
var _ae="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_ae.length>0){
_ae+="-";
}
_ae+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _b0 in arguments[i]){
this[_b0]=arguments[i][_b0];
}
}
}
}
this.jetspeed_om_id=_ae;
};
dojo.lang.extend(jetspeed.om.Id,{getId:function(){
return this.jetspeed_om_id;
}});
jetspeed.om.Page=function(_b1,_b2,_b3){
if(_b1!=null&&_b2!=null){
this.requiredLayoutDecorator=_b1;
this.setPsmlPathFromDocumentUrl(_b2);
this.pageUrlFallback=_b2;
}else{
this.setPsmlPathFromDocumentUrl();
}
this.addToHistory=_b3;
this.layouts={};
this.columns=[];
this.portlets=[];
this.menus=[];
};
dojo.inherits(jetspeed.om.Page,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,layouts:null,columns:null,portlets:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _b4=(this.name!=null&&this.name.length>0?this.name:null);
if(!_b4){
this.getPsmlUrl();
_b4=this.psmlPath;
}
return "page-"+_b4;
},setPsmlPathFromDocumentUrl:function(_b5){
var _b6=jetspeed.url.path.AJAX_API;
var _b7=null;
if(_b5==null){
_b7=document.location.pathname;
}else{
var _b8=jetspeed.url.parse(_b5);
_b7=_b8.path;
}
var _b9=jetspeed.url.path.DESKTOP;
var _ba=_b7.indexOf(_b9);
if(_ba!=-1&&_b7.length>(_ba+_b9.length)){
_b6=_b6+_b7.substring(_ba+_b9.length);
}
this.psmlPath=_b6;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _bb=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_bb=jetspeed.url.addQueryParameter(_bb,"layoutid",jetspeed.prefs.printModeOnly.layout);
_bb=jetspeed.url.addQueryParameter(_bb,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _bb;
},retrievePsml:function(_bc){
if(_bc==null){
_bc=new jetspeed.om.PageContentListenerCreateWidget();
}
var _bd=this.getPsmlUrl();
var _be="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_bd);
}
jetspeed.url.retrieveContent({url:_bd,mimetype:_be},_bc,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_bf){
var _c0=this._parsePSML(_bf);
if(_c0==null){
return;
}
var _c1={};
this.columnsStructure=this._layoutCreateModel(_c0,null,_c1,true);
this.rootFragmentId=_c0.id;
if(jetspeed.prefs.windowTiling){
this._createColumnsStart(document.getElementById(jetspeed.id.DESKTOP));
}
var _c2=new Array();
var _c3=this.columns.length;
for(var _c4=0;_c4<=this.columns.length;_c4++){
var _c5=null;
if(_c4==_c3){
_c5=_c1["z"];
if(_c5!=null){
_c5.sort(this._loadPortletZIndexCompare);
}
}else{
_c5=_c1[_c4.toString()];
}
if(_c5!=null){
for(var i=0;i<_c5.length;i++){
var _c7=_c5[i].portlet;
_c2.push(_c7);
_c7.createPortletWindow(_c4);
}
}
}
if(jetspeed.prefs.printModeOnly==null){
if(_c2&&_c2.length>0){
jetspeed.doRenderAll(null,_c2,true);
}
this._portletsInitializeWindowState(_c1["z"]);
this.retrieveAllMenus();
this.renderPageControls();
this.syncPageControls();
var _c8=jetspeed.url.getQueryParameter(document.location.href,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
if((_c8!=null&&_c8=="true")||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions!=null&&(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null)){
jetspeed.editPageInitiate();
}
}
}else{
var _c7=null;
for(var _c9 in this.portlets){
_c7=this.portlets[_c9];
break;
}
if(_c7!=null){
_c7.renderAction(null,jetspeed.prefs.printModeOnly.action);
this._portletsInitializeWindowState(_c1["z"]);
}
}
},_parsePSML:function(_ca){
var _cb=_ca.getElementsByTagName("page");
if(!_cb||_cb.length>1){
dojo.raise("unexpected zero or multiple <page> elements in psml");
}
var _cc=_cb[0];
var _cd=_cc.childNodes;
var _ce=new RegExp("(name|path|profiledPath|title|short-title)");
var _cf=null;
var _d0={};
for(var i=0;i<_cd.length;i++){
var _d2=_cd[i];
if(_d2.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _d3=_d2.nodeName;
if(_d3=="fragment"){
_cf=_d2;
}else{
if(_d3=="defaults"){
this.layoutDecorator=_d2.getAttribute("layout-decorator");
this.portletDecorator=_d2.getAttribute("portlet-decorator");
}else{
if(_d3&&_d3.match(_ce)){
this[jetspeed.purifyIdentifier(_d3,"","lo")]=((_d2&&_d2.firstChild)?_d2.firstChild.nodeValue:null);
}else{
if(_d3=="action"){
this._parsePSMLAction(_d2,_d0);
}
}
}
}
}
this.actions=_d0;
if(_cf==null){
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
var _d4=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_d4);
}
jetspeed.updatePage(_d4,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_d4);
}
jetspeed.updatePage(_d4,true);
},changeUrl:false});
}
}
}else{
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _d4=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_d4);
}
jetspeed.updatePage(_d4,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_d4);
}
jetspeed.updatePage(_d4,true);
},changeUrl:false});
}
}
var _d5=this._parsePSMLLayoutFragment(_cf,0);
return _d5;
},_parsePSMLLayoutFragment:function(_d6,_d7){
var _d8=new Array();
var _d9=((_d6!=null)?_d6.getAttribute("type"):null);
if(_d9!="layout"){
dojo.raise("_parsePSMLLayoutFragment called with non-layout fragment: "+_d6);
return null;
}
var _da=false;
var _db=_d6.getAttribute("name");
if(_db!=null){
_db=_db.toLowerCase();
if(_db.indexOf("noactions")!=-1){
_da=true;
}
}
var _dc=null,_dd=0;
var _de={};
var _df=_d6.childNodes;
var _e0,_e1,_e2,_e3,_e4;
for(var i=0;i<_df.length;i++){
_e0=_df[i];
if(_e0.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_e1=_e0.nodeName;
if(_e1=="fragment"){
_e4=_e0.getAttribute("type");
if(_e4=="layout"){
var _e6=this._parsePSMLLayoutFragment(_e0,i);
if(_e6!=null){
_d8.push(_e6);
}
}else{
var _e7=this._parsePSMLProperties(_e0,null);
var _e8=_e7[jetspeed.id.PORTLET_PROP_WINDOW_ICON];
if(_e8==null||_e8.length==0){
_e8=this._parsePSMLIcon(_e0);
if(_e8!=null&&_e8.length>0){
_e7[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=_e8;
}
}
_d8.push({id:_e0.getAttribute("id"),type:_e4,name:_e0.getAttribute("name"),properties:_e7,actions:this._parsePSMLActions(_e0,null),currentActionState:this._parsePSMLCurrentActionState(_e0),currentActionMode:this._parsePSMLCurrentActionMode(_e0),decorator:_e0.getAttribute("decorator"),layoutActionsDisabled:_da,documentOrderIndex:i});
}
}else{
if(_e1=="property"){
if(this._parsePSMLProperty(_e0,_de)=="sizes"){
if(_dc!=null){
dojo.raise("_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: "+_d6);
return null;
}
if(jetspeed.prefs.printModeOnly!=null){
_dc=["100"];
_dd=100;
}else{
_e3=_e0.getAttribute("value");
if(_e3!=null&&_e3.length>0){
_dc=_e3.split(",");
for(var j=0;j<_dc.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_dc[j]=_dc[j].replace(re,"$1");
_dd+=new Number(_dc[j]);
}
}
}
}
}
}
}
_d8.sort(this._fragmentRowCompare);
var _eb=new Array();
var _ec=new Array();
for(var i=0;i<_d8.length;i++){
if(_d8[i].type=="layout"){
_eb.push(i);
}else{
_ec.push(i);
}
}
if(_dc==null){
_dc=new Array();
_dc.push("100");
_dd=100;
}
return {id:_d6.getAttribute("id"),type:_d9,name:_d6.getAttribute("name"),decorator:_d6.getAttribute("decorator"),columnSizes:_dc,columnSizesSum:_dd,properties:_de,fragments:_d8,layoutFragmentIndexes:_eb,otherFragmentIndexes:_ec,layoutActionsDisabled:_da,documentOrderIndex:_d7};
},_parsePSMLActions:function(_ed,_ee){
if(_ee==null){
_ee={};
}
var _ef=_ed.getElementsByTagName("action");
for(var _f0=0;_f0<_ef.length;_f0++){
var _f1=_ef[_f0];
this._parsePSMLAction(_f1,_ee);
}
return _ee;
},_parsePSMLAction:function(_f2,_f3){
var _f4=_f2.getAttribute("id");
if(_f4!=null){
var _f5=_f2.getAttribute("type");
var _f6=_f2.getAttribute("name");
var _f7=_f2.getAttribute("url");
var _f8=_f2.getAttribute("alt");
_f3[_f4.toLowerCase()]={id:_f4,type:_f5,label:_f6,url:_f7,alt:_f8};
}
},_parsePSMLCurrentActionState:function(_f9){
var _fa=_f9.getElementsByTagName("state");
if(_fa!=null&&_fa.length==1&&_fa[0].firstChild!=null){
return _fa[0].firstChild.nodeValue;
}
return null;
},_parsePSMLCurrentActionMode:function(_fb){
var _fc=_fb.getElementsByTagName("mode");
if(_fc!=null&&_fc.length==1&&_fc[0].firstChild!=null){
return _fc[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_fd){
var _fe=_fd.getElementsByTagName("icon");
if(_fe!=null&&_fe.length==1&&_fe[0].firstChild!=null){
return _fe[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProperties:function(_ff,_100){
if(_100==null){
_100={};
}
var _101=_ff.getElementsByTagName("property");
for(var _102=0;_102<_101.length;_102++){
this._parsePSMLProperty(_101[_102],_100);
}
return _100;
},_parsePSMLProperty:function(_103,_104){
var _105=_103.getAttribute("name");
var _106=_103.getAttribute("value");
_104[_105]=_106;
return _105;
},_fragmentRowCompare:function(_107,_108){
var rowA=_107.documentOrderIndex*1000;
var rowB=_108.documentOrderIndex*1000;
var _10b=_107.properties["row"];
if(_10b!=null){
rowA=_10b;
}
var _10c=_108.properties["row"];
if(_10c!=null){
rowB=_10c;
}
return (rowA-rowB);
},_layoutCreateModel:function(_10d,_10e,_10f,_110){
var _111=this.columns.length;
var _112=this._layoutRegisterAndCreateColumnsModel(_10d,_10e,_110);
var _113=_112.columnsInLayout;
if(_112.addedLayoutHeaderColumn){
_111++;
}
var _114=(_113==null?0:_113.length);
if(_10d.layoutFragmentIndexes!=null&&_10d.layoutFragmentIndexes.length>0){
var _115=null;
var _116=0;
if(_10d.otherFragmentIndexes!=null&&_10d.otherFragmentIndexes.length>0){
_115=new Array();
}
for(var i=0;i<_10d.fragments.length;i++){
var _118=_10d.fragments[i];
}
var _119=new Array();
for(var i=0;i<_114;i++){
if(_115!=null){
_115.push(null);
}
_119.push(false);
}
for(var i=0;i<_10d.fragments.length;i++){
var _118=_10d.fragments[i];
var _11a=i;
if(_118.properties&&_118.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
if(_118.properties[jetspeed.id.PORTLET_PROP_COLUMN]!=null&&_118.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
_11a=_118.properties[jetspeed.id.PORTLET_PROP_COLUMN];
}
}
if(_11a>=_114){
_11a=(_114>0?(_114-1):0);
}
var _11b=((_115==null)?null:_115[_11a]);
if(_118.type=="layout"){
_119[_11a]=true;
if(_11b!=null){
this._layoutCreateModel(_11b,_113[_11a],_10f,true);
_115[_11a]=null;
}
this._layoutCreateModel(_118,_113[_11a],_10f,false);
}else{
if(_11b==null){
_116++;
var _11c={};
dojo.lang.mixin(_11c,_10d);
_11c.fragments=new Array();
_11c.layoutFragmentIndexes=new Array();
_11c.otherFragmentIndexes=new Array();
_11c.documentOrderIndex=_10d.fragments[i].documentOrderIndex;
_11c.clonedFromRootId=_11c.id;
_11c.clonedLayoutFragmentIndex=_116;
_11c.columnSizes=["100"];
_11c.columnSizesSum=[100];
_11c.id=_11c.id+"-jsclone_"+_116;
_115[_11a]=_11c;
_11b=_11c;
}
_11b.fragments.push(_118);
_11b.otherFragmentIndexes.push(_11b.fragments.length-1);
}
}
if(_115!=null){
for(var i=0;i<_114;i++){
var _11b=_115[i];
if(_11b!=null){
_119[i]=true;
this._layoutCreateModel(_11b,_113[i],_10f,true);
}
}
}
for(var i=0;i<_114;i++){
if(_119[i]){
_113[i].columnContainer=true;
}
}
if(_10d.otherFragmentIndexes!=null&&_10d.otherFragmentIndexes.length>0){
var _11d=new Array();
for(var i=0;i<_10d.fragments.length;i++){
var _11e=true;
for(var j=0;j<_10d.otherFragmentIndexes.length;j++){
if(_10d.otherFragmentIndexes[j]==i){
_11e=false;
break;
}
}
if(_11e){
_11d.push(_10d.fragments[i]);
}
}
_10d.fragments=_11d;
_10d.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_10d,_113,_111,_10f);
return _113;
},_layoutFragmentChildCollapse:function(_120,_121){
var _122=false;
if(_121==null){
_121=_120;
}
if(_120.layoutFragmentIndexes!=null&&_120.layoutFragmentIndexes.length>0){
_122=true;
for(var i=0;i<_120.layoutFragmentIndexes.length;i++){
var _124=_120.fragments[_120.layoutFragmentIndexes[i]];
if(_124.otherFragmentIndexes!=null&&_124.otherFragmentIndexes.length>0){
for(var i=0;i<_124.otherFragmentIndexes.length;i++){
var _125=_124.fragments[_124.otherFragmentIndexes[i]];
_125.properties[jetspeed.id.PORTLET_PROP_COLUMN]=-1;
_125.properties[jetspeed.id.PORTLET_PROP_ROW]=-1;
_125.documentOrderIndex=_121.fragments.length;
_121.fragments.push(_125);
_121.otherFragIndexes.push(_121.fragments.length);
}
}
this._layoutFragmentChildCollapse(_124,_121);
}
}
return _122;
},_layoutRegisterAndCreateColumnsModel:function(_126,_127,_128){
this.layouts[_126.id]=_126;
var _129=false;
var _12a=new Array();
if(jetspeed.prefs.windowTiling&&_126.columnSizes.length>0){
var _12b=false;
if(jetspeed.browser_IE){
_12b=true;
}
if(_127!=null&&!_128){
var _12c=new jetspeed.om.Column(0,_126.id,(_12b?_126.columnSizesSum-0.1:_126.columnSizesSum),this.columns.length,_126.layoutActionsDisabled);
_12c.layoutHeader=true;
this.columns.push(_12c);
if(_127.columnChildren==null){
_127.columnChildren=new Array();
}
_127.columnChildren.push(_12c);
_127=_12c;
_129=true;
}
for(var i=0;i<_126.columnSizes.length;i++){
var size=_126.columnSizes[i];
if(_12b&&i==(_126.columnSizes.length-1)){
size=size-0.1;
}
var _12f=new jetspeed.om.Column(i,_126.id,size,this.columns.length,_126.layoutActionsDisabled);
this.columns.push(_12f);
if(_127!=null){
if(_127.columnChildren==null){
_127.columnChildren=new Array();
}
_127.columnChildren.push(_12f);
}
_12a.push(_12f);
}
}
return {columnsInLayout:_12a,addedLayoutHeaderColumn:_129};
},_layoutCreatePortletsModel:function(_130,_131,_132,_133){
if(_130.otherFragmentIndexes!=null&&_130.otherFragmentIndexes.length>0){
var _134=new Array();
for(var i=0;i<_131.length;i++){
_134.push(new Array());
}
for(var i=0;i<_130.otherFragmentIndexes.length;i++){
var _136=_130.fragments[_130.otherFragmentIndexes[i]];
if(jetspeed.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter,_136.id)){
_136=null;
}
}
if(_136!=null){
var _137="z";
var _138=_136.properties[jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED];
var _139=jetspeed.prefs.windowTiling;
var _13a=jetspeed.prefs.windowHeightExpand;
if(_138!=null&&jetspeed.prefs.windowTiling&&jetspeed.prefs.printModeOnly==null){
var _13b=_138.split(jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR);
var _13c=null,_13d=0,_13e=null,_13f=null,_140=false;
if(_13b!=null&&_13b.length>0){
var _141=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
for(var _142=0;_142<_13b.length;_142++){
_13c=_13b[_142];
_13d=((_13c!=null)?_13c.length:0);
if(_13d>0){
var _143=_13c.indexOf(_141);
if(_143>0&&_143<(_13d-1)){
_13e=_13c.substring(0,_143);
_13f=_13c.substring(_143+1);
_140=((_13f=="true")?true:false);
if(_13e==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS){
_139=_140;
}else{
if(_13e==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT){
_13a=_140;
}
}
}
}
}
}
}else{
if(!jetspeed.prefs.windowTiling){
_139=false;
}
}
_136.properties[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_139;
_136.properties[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_13a;
if(_139&&jetspeed.prefs.windowTiling){
var _144=_136.properties[jetspeed.id.PORTLET_PROP_COLUMN];
if(_144==null||_144==""||_144<0||_144>=_131.length){
var _145=-1;
for(var j=0;j<_131.length;j++){
if(_145==-1||_134[j].length<_145){
_145=_134[j].length;
_144=j;
}
}
}
_134[_144].push(_136.id);
var _147=_132+new Number(_144);
_137=_147.toString();
}
var _148=new jetspeed.om.Portlet(_136.name,_136.id,null,_136.properties,_136.actions,_136.currentActionState,_136.currentActionMode,_136.decorator,_136.layoutActionsDisabled);
_148.initialize();
this.putPortlet(_148);
if(_133[_137]==null){
_133[_137]=new Array();
}
_133[_137].push({portlet:_148,layout:_130.id});
}
}
}
},_portletsInitializeWindowState:function(_149){
var _14a={};
this.getPortletCurrentColumnRow(null,false,_14a);
for(var _14b in this.portlets){
var _14c=this.portlets[_14b];
var _14d=_14a[_14c.getId()];
if(_14d==null&&_149){
for(var i=0;i<_149.length;i++){
if(_149[i].portlet.getId()==_14c.getId()){
_14d={layout:_149[i].layout};
break;
}
}
}
if(_14d!=null){
_14c._initializeWindowState(_14d,false);
}else{
dojo.raise("page._portletsInitializeWindowState could not find window state init data for portlet: "+_14c.getId());
}
}
},_loadPortletZIndexCompare:function(_14f,_150){
var _151=null;
var _152=null;
var _153=null;
_151=_14f.portlet._getInitialZIndex();
_152=_150.portlet._getInitialZIndex();
if(_151&&!_152){
return -1;
}else{
if(_152&&!_151){
return 1;
}else{
if(_151==_152){
return 0;
}
}
}
return (_151-_152);
},_createColumnsStart:function(_154){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _155=document.createElement("div");
_155.id=jetspeed.id.COLUMNS;
_155.setAttribute("id",jetspeed.id.COLUMNS);
for(var _156=0;_156<this.columnsStructure.length;_156++){
var _157=this.columnsStructure[_156];
this._createColumns(_157,_155);
}
_154.appendChild(_155);
},_createColumns:function(_158,_159){
_158.createColumn();
if(_158.columnChildren!=null&&_158.columnChildren.length>0){
for(var _15a=0;_15a<_158.columnChildren.length;_15a++){
var _15b=_158.columnChildren[_15a];
this._createColumns(_15b,_158.domNode);
}
}
_159.appendChild(_158.domNode);
},_removeColumns:function(_15c){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_15c){
var _15e=jetspeed.ui.getPortletWindowChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_15e,function(_15f){
_15c.appendChild(_15f);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _160=dojo.byId(jetspeed.id.COLUMNS);
if(_160){
dojo.dom.removeNode(_160);
}
this.columns=[];
},getPortletCurrentColumnRow:function(_161,_162,_163){
if(!this.columns||this.columns.length==0){
return null;
}
var _164=null;
var _165=((_161!=null)?true:false);
var _166=0;
var _167=null;
var _168=null;
var _169=0;
var _16a=false;
for(var _16b=0;_16b<this.columns.length;_16b++){
var _16c=this.columns[_16b];
var _16d=_16c.domNode.childNodes;
if(_168==null||_168!=_16c.getLayoutId()){
_168=_16c.getLayoutId();
_167=this.layouts[_168];
if(_167==null){
dojo.raise("getPortletCurrentColumnRow cannot locate layout id: "+_168);
return null;
}
_169=0;
_16a=false;
if(_167.clonedFromRootId==null){
_16a=true;
}else{
var _16e=this.getColumnFromColumnNode(_16c.domNode.parentNode);
if(_16e==null){
dojo.raise("getPortletCurrentColumnRow cannot locate parent column for column: "+_16c);
return null;
}
_16c=_16e;
}
}
var _16f=null;
for(var _170=0;_170<_16d.length;_170++){
var _171=_16d[_170];
if(dojo.html.hasClass(_171,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS)||(_162&&dojo.html.hasClass(_171,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))||(_165&&dojo.html.hasClass(_171,"desktopColumn"))){
_16f=(_16f==null?0:_16f+1);
if((_16f+1)>_169){
_169=(_16f+1);
}
if(_161==null||_171==_161){
var _172={layout:_168,column:_16c.getLayoutColumnIndex(),row:_16f};
if(!_16a){
_172.layout=_167.clonedFromRootId;
}
if(_161!=null){
_164=_172;
break;
}else{
if(_163!=null){
var _173=this.getPortletWindowFromNode(_171);
if(_173==null){
dojo.raise("getPortletCurrentColumnRow cannot locate PortletWindow for node.");
}else{
var _174=_173.portlet;
if(_174==null){
dojo.raise("getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: "+_173.widgetId);
}else{
_163[_174.getId()]=_172;
}
}
}
}
}
}
}
if(_164!=null){
break;
}
}
return _164;
},_getPortletArrayByZIndex:function(){
var _175=this.getPortletArray();
if(!_175){
return _175;
}
var _176=[];
for(var i=0;i<_175.length;i++){
if(!_175[i].getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_176.push(_175[i]);
}
}
_176.sort(this._portletZIndexCompare);
return _176;
},_portletZIndexCompare:function(_178,_179){
var _17a=null;
var _17b=null;
var _17c=null;
_17c=_178.getLastSavedWindowState();
_17a=_17c.zIndex;
_17c=_179.getLastSavedWindowState();
_17b=_17c.zIndex;
if(_17a&&!_17b){
return -1;
}else{
if(_17b&&!_17a){
return 1;
}else{
if(_17a==_17b){
return 0;
}
}
}
return (_17a-_17b);
},getPortletDecorationDefault:function(){
var pd=null;
if(djConfig.isDebug&&jetspeed.debug.windowDecorationRandom){
pd=jetspeed.prefs.portletDecorationsAllowed[Math.floor(Math.random()*jetspeed.prefs.portletDecorationsAllowed.length)];
}else{
var _17e=this.getPortletDecorator();
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_17e)!=-1){
pd=_17e;
}else{
pd=jetspeed.prefs.windowDecoration;
}
}
return pd;
},getPortletArrayList:function(){
var _17f=new dojo.collections.ArrayList();
for(var _180 in this.portlets){
var _181=this.portlets[_180];
_17f.add(_181);
}
return _17f;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _182=[];
for(var _183 in this.portlets){
var _184=this.portlets[_183];
_182.push(_184);
}
return _182;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _185=[];
for(var _186 in this.portlets){
var _187=this.portlets[_186];
_185.push(_187.getId());
}
return _185;
},getPortletByName:function(_188){
if(this.portlets&&_188){
for(var _189 in this.portlets){
var _18a=this.portlets[_189];
if(_18a.name==_188){
return _18a;
}
}
}
return null;
},getPortlet:function(_18b){
if(this.portlets&&_18b){
return this.portlets[_18b];
}
return null;
},getPortletWindowFromNode:function(_18c){
var _18d=null;
if(this.portlets&&_18c){
for(var _18e in this.portlets){
var _18f=this.portlets[_18e];
var _190=_18f.getPortletWindow();
if(_190!=null){
if(_190.domNode==_18c){
_18d=_190;
break;
}
}
}
}
return _18d;
},putPortlet:function(_191){
if(!_191){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_191.entityId]=_191;
},removePortlet:function(_192){
if(!_192||!this.portlets){
return;
}
delete this.portlets[_192.entityId];
},_destroyPortlets:function(){
for(var _193 in this.portlets){
var _194=this.portlets[_193];
_194._destroy();
}
},debugLayoutInfo:function(){
var _195="";
var i=0;
for(var _197 in this.layouts){
if(i>0){
_195+="\r\n";
}
_195+="layout["+_197+"]: "+jetspeed.printobj(this.layouts[_197],true,true,true);
i++;
}
return _195;
},debugColumnInfo:function(){
var _198="";
for(var i=0;i<this.columns.length;i++){
if(i>0){
_198+="\r\n";
}
_198+=this.columns[i].toString();
}
return _198;
},debugDumpLastSavedWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(true);
},debugDumpWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(false);
},debugPortletActions:function(){
var _19a=this.getPortletArray();
var _19b="";
for(var i=0;i<_19a.length;i++){
var _19d=_19a[i];
if(i>0){
_19b+="\r\n";
}
_19b+="portlet ["+_19d.name+"] actions: {";
for(var _19e in _19d.actions){
_19b+=_19e+"={"+jetspeed.printobj(_19d.actions[_19e],true)+"} ";
}
_19b+="}";
}
return _19b;
},_debugDumpLastSavedWindowStateAllPortlets:function(_19f){
var _1a0=this.getPortletArray();
var _1a1="";
for(var i=0;i<_1a0.length;i++){
var _1a3=_1a0[i];
if(i>0){
_1a1+="\r\n";
}
var _1a4=null;
try{
if(_19f){
_1a4=_1a3.getLastSavedWindowState();
}else{
_1a4=_1a3.getCurrentWindowState();
}
}
catch(e){
}
_1a1+="["+_1a3.name+"] "+((_1a4==null)?"null":jetspeed.printobj(_1a4,true));
}
return _1a1;
},resetWindowLayout:function(){
for(var _1a5 in this.portlets){
var _1a6=this.portlets[_1a5];
_1a6.submitChangedWindowState(false,true);
}
this.reload();
},reload:function(){
this._removeColumns(document.getElementById(jetspeed.id.DESKTOP));
jetspeed.loadPage();
},destroy:function(){
this._destroyPortlets();
this._removeColumns(document.getElementById(jetspeed.id.DESKTOP));
this._destroyPageControls();
},getColumnFromColumnNode:function(_1a7){
if(_1a7==null){
return null;
}
var _1a8=_1a7.getAttribute("columnIndex");
if(_1a8==null){
return null;
}
var _1a9=new Number(_1a8);
if(_1a9>=0&&_1a9<this.columns.length){
return this.columns[_1a9];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1ab=null;
if(!this.columns){
return _1ab;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1ab=i;
break;
}
}
return _1ab;
},getColumnContainingNode:function(node){
var _1ae=this.getColumnIndexContainingNode(node);
return ((_1ae!=null&&_1ae>=0)?this.columns[_1ae]:null);
},getDescendantColumns:function(_1af){
var dMap={};
if(_1af==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1af&&_1af.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1b3,_1b4,_1b5){
var _1b6=new jetspeed.om.Portlet(_1b3,_1b4);
if(_1b5){
_1b6.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1b5);
}
_1b6.initialize();
this.putPortlet(_1b6);
_1b6.retrieveContent();
},removePortletFromPage:function(_1b7){
var _1b8=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1b9="?action=remove&id="+escape(portletDef.getPortletName());
var _1ba=jetspeed.page.getPsmlUrl()+_1b9;
var _1bb="text/xml";
var _1bc=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1ba,mimetype:_1bb},_1b8,_1bc,jetspeed.debugContentDumpIds);
},putMenu:function(_1bd){
if(!_1bd){
return;
}
var _1be=(_1bd.getName?_1bd.getName():null);
if(_1be!=null){
this.menus[_1be]=_1bd;
}
},getMenu:function(_1bf){
if(_1bf==null){
return null;
}
return this.menus[_1bf];
},removeMenu:function(_1c0){
if(_1c0==null){
return;
}
var _1c1=null;
if(dojo.lang.isString(_1c0)){
_1c1=_1c0;
}else{
_1c1=(_1c0.getName?_1c0.getName():null);
}
if(_1c1!=null){
delete this.menus[_1c1];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1c2=[];
for(var _1c3 in this.menus){
_1c2.push(_1c3);
}
return _1c2;
},retrieveAllMenus:function(){
this.retrieveMenuDeclarations(true);
},retrieveMenuDeclarations:function(_1c4){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1c4);
this.clearMenus();
var _1c5="?action=getmenus";
if(_1c4){
_1c5+="&includeMenuDefs=true";
}
var _1c6=this.getPsmlUrl()+_1c5;
var _1c7="text/xml";
var _1c8=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1c6,mimetype:_1c7},contentListener,_1c8,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1c9,_1ca,_1cb){
if(_1cb==null){
_1cb=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1cc="?action=getmenu&name="+_1c9;
var _1cd=this.getPsmlUrl()+_1cc;
var _1ce="text/xml";
var _1cf=new jetspeed.om.Id("getmenu-"+_1c9,{page:this,menuName:_1c9,menuType:_1ca});
jetspeed.url.retrieveContent({url:_1cd,mimetype:_1ce},_1cb,_1cf,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1d0 in this.actionButtons){
var _1d1=false;
if(_1d0==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1d1=true;
}
}else{
if(_1d0==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1d1=true;
}
}else{
if(_1d0==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1d1=true;
}
}else{
_1d1=true;
}
}
}
if(_1d1){
this.actionButtons[_1d0].style.display="";
}else{
this.actionButtons[_1d0].style.display="none";
}
}
},renderPageControls:function(){
var _1d2=[];
if(this.actions!=null){
for(var _1d3 in this.actions){
if(_1d3!=jetspeed.id.ACTION_NAME_HELP){
_1d2.push(_1d3);
}
if(_1d3==jetspeed.id.ACTION_NAME_EDIT){
_1d2.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1d2.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1d2.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1d4=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1d4!=null&&_1d2!=null&&_1d2.length>0){
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
for(var i=0;i<_1d2.length;i++){
var _1d3=_1d2[i];
var _1d6=document.createElement("div");
_1d6.className="portalPageActionButton";
_1d6.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1d3+".gif)";
_1d6.actionName=_1d3;
this.actionButtons[_1d3]=_1d6;
_1d4.appendChild(_1d6);
dojo.event.connect(_1d6,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1d7=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1d7=jetspeed.prefs.desktopActionLabels[_1d3];
}
if(_1d7==null||_1d7.length==0){
_1d7=dojo.string.capitalize(_1d3);
}
var _1d8=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1d7,connectId:_1d6,delay:"100"});
this.actionButtonTooltips.push(_1d8);
document.body.appendChild(_1d8.domNode);
}
}
}
},_destroyPageControls:function(){
var _1d9=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1d9!=null&&_1d9.childNodes&&_1d9.childNodes.length>0){
for(var i=(_1d9.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1d9.childNodes[i]);
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
},pageActionProcess:function(_1dc){
if(_1dc==null){
return;
}
if(_1dc==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1dc==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1dc==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1dd=this.getPageAction(_1dc);
alert("pageAction "+_1dc+" : "+_1dd);
if(_1dd==null){
return;
}
if(_1dd.url==null){
return;
}
var _1de=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1dd.url;
jetspeed.pageNavigate(_1de);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1e0,_1e1){
if(!_1e1){
_1e1=escape(this.getPagePathAndQuery());
}else{
_1e1=escape(_1e1);
}
var _1e2=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1e1;
if(_1e0!=null){
_1e2+="&jslayoutid="+escape(_1e0);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1e2));
},setPageModePortletActions:function(_1e3){
if(_1e3==null||_1e3.actions==null){
return;
}
if(_1e3.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1e3.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1e4){
if(this.pageUrl!=null&&!_1e4){
return this.pageUrl;
}
var _1e5=jetspeed.url.path.SERVER+((_1e4)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1e6=jetspeed.url.parse(_1e5);
var _1e7=null;
if(this.pageUrlFallback!=null){
_1e7=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1e7=jetspeed.url.parse(document.location.href);
}
if(_1e6!=null&&_1e7!=null){
var _1e8=_1e7.query;
if(_1e8!=null&&_1e8.length>0){
var _1e9=_1e6.query;
if(_1e9!=null&&_1e9.length>0){
_1e5=_1e5+"&"+_1e8;
}else{
_1e5=_1e5+"?"+_1e8;
}
}
}
if(!_1e4){
this.pageUrl=_1e5;
}
return _1e5;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1ea=this.getPath();
var _1eb=jetspeed.url.parse(_1ea);
var _1ec=null;
if(this.pageUrlFallback!=null){
_1ec=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1ec=jetspeed.url.parse(document.location.href);
}
if(_1eb!=null&&_1ec!=null){
var _1ed=_1ec.query;
if(_1ed!=null&&_1ed.length>0){
var _1ee=_1eb.query;
if(_1ee!=null&&_1ee.length>0){
_1ea=_1ea+"&"+_1ed;
}else{
_1ea=_1ea+"?"+_1ed;
}
}
}
this.pagePathAndQuery=_1ea;
return _1ea;
},getPageDirectory:function(_1ef){
var _1f0="/";
var _1f1=(_1ef?this.getRealPath():this.getPath());
if(_1f1!=null){
var _1f2=_1f1.lastIndexOf("/");
if(_1f2!=-1){
if((_1f2+1)<_1f1.length){
_1f0=_1f1.substring(0,_1f2+1);
}else{
_1f0=_1f1;
}
}
}
return _1f0;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_1f4){
if(!_1f4){
_1f4="";
}
if(!jetspeed.url.validateUrlStartsWithHttp(_1f4)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_1f4;
}
return _1f4;
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
jetspeed.om.Column=function(_1f5,_1f6,size,_1f8,_1f9){
this.layoutColumnIndex=_1f5;
this.layoutId=_1f6;
this.size=size;
this.pageColumnIndex=new Number(_1f8);
if(typeof _1f9!="undefined"){
this.layoutActionsDisabled=_1f9;
}
this.id="jscol_"+_1f8;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_1fa){
var _1fb="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_1fb="desktopColumn desktopColumnClear";
}
var _1fc=document.createElement("div");
_1fc.setAttribute("columnIndex",this.getPageColumnIndex());
_1fc.style.width=this.size+"%";
if(this.layoutHeader){
_1fb="desktopColumn desktopLayoutHeader";
}else{
_1fc.style.minHeight="40px";
}
_1fc.className=_1fb;
_1fc.id=this.getId();
this.domNode=_1fc;
if(_1fa!=null){
_1fa.appendChild(_1fc);
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
var _200=dojo.html.getAbsolutePosition(this.domNode,true);
var _201=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_200.x)+", right:"+(_200.x+_201.width)+", top:"+(_200.y)+", bottom:"+(_200.y+_201.height)+"}";
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
jetspeed.om.Portlet=function(_202,_203,_204,_205,_206,_207,_208,_209,_20a){
this.name=_202;
this.entityId=_203;
if(_205){
this.properties=_205;
}else{
this.properties={};
}
if(_206){
this.actions=_206;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_207;
this.currentActionMode=_208;
if(_204){
this.contentRetriever=_204;
}
if(_209!=null&&_209.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_209)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_209);
}
}
this.layoutActionsDisabled=false;
if(typeof _20a!="undefined"){
this.layoutActionsDisabled=_20a;
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
var _20b=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_20b=="true"){
_20b=true;
}else{
if(_20b=="false"){
_20b=false;
}else{
if(_20b!=true&&_20b!=false){
_20b=true;
}
}
}
}else{
_20b=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_20b);
var _20c=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_20c=="true"){
_20c=true;
}else{
if(_20b=="false"){
_20c=false;
}else{
if(_20c!=true&&_20c!=false){
_20c=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_20c);
var _20d=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_20d&&this.name){
var re=(/^[^:]*:*/);
_20d=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_20d);
}
},postParseAnnotateHtml:function(_20f){
if(_20f){
var _210=_20f;
var _211=_210.getElementsByTagName("form");
var _212=jetspeed.debug.postParseAnnotateHtml;
var _213=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_211){
for(var i=0;i<_211.length;i++){
var _215=_211[i];
var _216=_215.action;
var _217=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_216);
var _218=_217.operation;
if(_218==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_218==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _219=jetspeed.portleturl.generateJSPseudoUrlActionRender(_217,true);
_215.action=_219;
var _21a=new jetspeed.om.ActionRenderFormBind(_215,_217.url,_217.portletEntityId,_218);
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_218+") for form with action: "+_216);
}
}else{
if(_216==null||_216.length==0){
var _21a=new jetspeed.om.ActionRenderFormBind(_215,null,this.entityId,null);
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_216);
}
}
}
}
}
var _21b=_210.getElementsByTagName("a");
if(_21b){
for(var i=0;i<_21b.length;i++){
var _21c=_21b[i];
var _21d=_21c.href;
var _217=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_21d);
var _21e=null;
if(!_213){
_21e=jetspeed.portleturl.generateJSPseudoUrlActionRender(_217);
}
if(!_21e){
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_21d);
}
}else{
if(_21e==_21d){
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_21d);
}
}else{
if(_212){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_21d+" with: "+_21e);
}
_21c.href=_21e;
}
}
}
}
}
},getPortletWindow:function(){
var _21f=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_21f){
return dojo.widget.byId(_21f);
}
return null;
},getCurrentWindowState:function(_220){
var _221=this.getPortletWindow();
if(!_221){
return null;
}
var _222=_221.getCurrentWindowStateForPersistence(_220);
if(!_220){
if(_222.layout==null){
_222.layout=this.lastSavedWindowState.layout;
}
}
return _222;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_223,_224){
if(!_223){
_223={};
}
var _225=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _226=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_223[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_225;
_223[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_226;
var _227=this.getProperty("width");
if(!_224&&_227!=null&&_227>0){
_223.width=Math.floor(_227);
}else{
if(_224){
_223.width=-1;
}
}
var _228=this.getProperty("height");
if(!_224&&_228!=null&&_228>0){
_223.height=Math.floor(_228);
}else{
if(_224){
_223.height=-1;
}
}
if(!_225||!jetspeed.prefs.windowTiling){
var _229=this.getProperty("x");
if(!_224&&_229!=null&&_229>=0){
_223.left=Math.floor(((_229>0)?_229:0));
}else{
if(_224){
_223.left=-1;
}
}
var _22a=this.getProperty("y");
if(!_224&&_22a!=null&&_22a>=0){
_223.top=Math.floor(((_22a>0)?_22a:0));
}else{
_223.top=-1;
}
var _22b=this._getInitialZIndex(_224);
if(_22b!=null){
_223.zIndex=_22b;
}
}
return _223;
},_initializeWindowState:function(_22c,_22d){
var _22e=(_22c?_22c:{});
this.getInitialWindowDimensions(_22e,_22d);
if(jetspeed.debug.initializeWindowState){
var _22f=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_22f||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_22e.zIndex+" x="+_22e.left+" y="+_22e.top+" width="+_22e.width+" height="+_22e.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_22e.column+" row="+_22e.row+" width="+_22e.width+" height="+_22e.height);
}
}
this.lastSavedWindowState=_22e;
return _22e;
},_getInitialZIndex:function(_230){
var _231=null;
var _232=this.getProperty("z");
if(!_230&&_232!=null&&_232>=0){
_231=Math.floor(_232);
}else{
if(_230){
_231=-1;
}
}
return _231;
},_getChangedWindowState:function(_233){
var _234=this.getLastSavedWindowState();
if(_234&&dojo.lang.isEmpty(_234)){
_234=null;
_233=false;
}
var _235=this.getCurrentWindowState(_233);
var _236=_235[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _237=!_236;
if(!_234){
var _238={state:_235,positionChanged:true,extendedPropChanged:true};
if(_237){
_238.zIndexChanged=true;
}
return _238;
}
var _239=false;
var _23a=false;
var _23b=false;
var _23c=false;
for(var _23d in _235){
if(_235[_23d]!=_234[_23d]){
if(_23d==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_23d==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_239=true;
_23b=true;
_23a=true;
}else{
if(_23d=="zIndex"){
if(_237){
_239=true;
_23c=true;
}
}else{
_239=true;
_23a=true;
}
}
}
}
if(_239){
var _238={state:_235,positionChanged:_23a,extendedPropChanged:_23b};
if(_237){
_238.zIndexChanged=_23c;
}
return _238;
}
return null;
},createPortletWindow:function(_23e){
jetspeed.ui.createPortletWindow(this,_23e);
},getPortletUrl:function(_23f){
var _240=null;
if(_23f&&_23f.url){
_240=_23f.url;
}else{
if(_23f&&_23f.formNode){
var _241=_23f.formNode.getAttribute("action");
if(_241){
_240=_241;
}
}
}
if(_240==null){
_240=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_23f.dontAddQueryArgs){
_240=jetspeed.url.parse(_240);
_240=jetspeed.url.addQueryParameter(_240,"entity",this.entityId,true);
_240=jetspeed.url.addQueryParameter(_240,"portlet",this.name,true);
_240=jetspeed.url.addQueryParameter(_240,"encoder","desktop",true);
if(_23f.jsPageUrl!=null){
var _242=_23f.jsPageUrl.query;
if(_242!=null&&_242.length>0){
_240=_240.toString()+"&"+_242;
}
}
}
if(_23f){
_23f.url=_240.toString();
}
return _240;
},_submitJetspeedAjaxApi:function(_243,_244,_245){
var _246="?action="+_243+"&id="+this.entityId+_244;
var _247=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_246;
var _248="text/xml";
var _249=new jetspeed.om.Id(_243,this.entityId);
_249.portlet=this;
jetspeed.url.retrieveContent({url:_247,mimetype:_248},_245,_249,null);
},submitChangedWindowState:function(_24a,_24b){
var _24c=null;
if(_24b){
_24c={state:this._initializeWindowState(null,true)};
}else{
_24c=this._getChangedWindowState(_24a);
}
if(_24c){
var _24d=_24c.state;
var _24e=_24d[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _24f=_24d[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _250=null;
if(_24c.extendedPropChanged){
var _251=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _252=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_250=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_251+_24e.toString();
_250+=_252+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_251+_24f.toString();
_250=escape(_250);
}
var _253="";
var _254=null;
if(_24e){
_254="moveabs";
if(_24d.column!=null){
_253+="&col="+_24d.column;
}
if(_24d.row!=null){
_253+="&row="+_24d.row;
}
if(_24d.layout!=null){
_253+="&layoutid="+_24d.layout;
}
if(_24d.height!=null){
_253+="&height="+_24d.height;
}
}else{
_254="move";
if(_24d.zIndex!=null){
_253+="&z="+_24d.zIndex;
}
if(_24d.width!=null){
_253+="&width="+_24d.width;
}
if(_24d.height!=null){
_253+="&height="+_24d.height;
}
if(_24d.left!=null){
_253+="&x="+_24d.left;
}
if(_24d.top!=null){
_253+="&y="+_24d.top;
}
}
if(_250!=null){
_253+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_250;
}
this._submitJetspeedAjaxApi(_254,_253,new jetspeed.om.MoveAjaxApiContentListener(this,_24d));
if(!_24a&&!_24b){
if(!_24e&&_24c.zIndexChanged){
var _255=jetspeed.page.getPortletArrayList();
var _256=dojo.collections.Set.difference(_255,[this]);
if(!_255||!_256||((_256.count+1)!=_255.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_256&&_256.count>0){
dojo.lang.forEach(_256.toArray(),function(_257){
if(!_257.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_257.submitChangedWindowState(true);
}
});
}
}
}else{
if(_24e){
}
}
}
}
},retrieveContent:function(_258,_259,_25a){
if(_258==null){
_258=new jetspeed.om.PortletContentListener(this,_25a,_259);
}
if(!_259){
_259={};
}
var _25b=this;
_25b.getPortletUrl(_259);
this.contentRetriever.getContent(_259,_258,_25b,jetspeed.debugContentDumpIds);
},setPortletContent:function(_25c,_25d,_25e){
var _25f=this.getPortletWindow();
if(_25e!=null&&_25e.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_25e);
if(_25f&&!this.loadingIndicatorIsShown()){
_25f.setPortletTitle(_25e);
}
}
if(_25f){
_25f.setPortletContent(_25c,_25d);
}
},loadingIndicatorIsShown:function(){
var _260=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _261=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _262=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _263=this.getPortletWindow();
if(_263&&(_260||_261)){
var _264=_263.getPortletTitle();
if(_264&&(_264==_260||_264==_261)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_265){
var _266=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_266=jetspeed.prefs.desktopActionLabels[_265];
if(_266!=null&&_266.length==0){
_266=null;
}
}
return _266;
},loadingIndicatorShow:function(_267){
if(_267&&!this.loadingIndicatorIsShown()){
var _268=this._getLoadingActionLabel(_267);
var _269=this.getPortletWindow();
if(_269&&_268){
_269.setPortletTitle(_268);
}
}
},loadingIndicatorHide:function(){
var _26a=this.getPortletWindow();
if(_26a){
_26a.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_26c){
this.properties[name]=_26c;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_26f,_270){
var _271=null;
if(_26f!=null){
_271=this.getAction(_26f);
}
var _272=_270;
if(_272==null&&_271!=null){
_272=_271.url;
}
if(_272==null){
return;
}
var _273=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_272+jetspeed.page.getPath();
if(_26f!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_273});
}else{
var _274=jetspeed.page.getPageUrl();
_274=jetspeed.url.addQueryParameter(_274,"jsprintmode","true");
_274=jetspeed.url.addQueryParameter(_274,"jsaction",escape(_271.url));
_274=jetspeed.url.addQueryParameter(_274,"jsentity",this.entityId);
_274=jetspeed.url.addQueryParameter(_274,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_274.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_276,_277,_278){
if(_276){
this.actions=_276;
}else{
this.actions={};
}
this.currentActionState=_277;
this.currentActionMode=_278;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _279=this.getPortletWindow();
if(_279){
_279.windowActionButtonSync();
}
},_destroy:function(){
var _27a=this.getPortletWindow();
if(_27a){
_27a.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_27d,_27e){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_27d;
this.submitOperation=_27e;
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
var _283=form.getElementsByTagName("input");
for(var i=0;i<_283.length;i++){
var _284=_283[i];
if(_284.type.toLowerCase()=="image"&&_284.form==form){
this.connect(_284,"onclick","click");
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
},onSubmit:function(_286){
var _287=true;
if(this.isFormSubmitInProgress()){
_287=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_287=false;
}
}
}
return _287;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _289=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _28a={};
if(_289.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_289.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _28b=jetspeed.portleturl.generateJSPseudoUrlActionRender(_289,true);
this.form.action=_28b;
this.submitOperation=_289.operation;
this.entityId=_289.portletEntityId;
_28a.url=_289.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_28a.formFilter=dojo.lang.hitch(this,"formFilter");
_28a.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_28a),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_28a),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_28c){
if(_28c!=undefined){
this.formSubmitInProgress=_28c;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_28d,_28e){
this.folderName=_28d;
this.folderPath=_28e;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_28f,_290,_291,_292,_293){
this.portletName=_28f;
this.portletDisplayName=_290;
this.portletDescription=_291;
this.image=_292;
this.count=_293;
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
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_294,_295,_296){
var _297=_296.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_297){
var _298=dojo.widget.byId(_297);
if(_298){
_298.setPortletContent(_294,_295);
}
}
},notifyFailure:function(type,_29a,_29b,_29c){
dojo.raise("BasicContentListener notifyFailure url: "+_29b+" type: "+type+jetspeed.url.formatBindError(_29a));
}};
jetspeed.om.PortletContentListener=function(_29d,_29e,_29f){
this.portlet=_29d;
this.suppressGetActions=_29e;
this.submittedFormBindObject=null;
if(_29f!=null&&_29f.submitFormBindObject!=null){
this.submittedFormBindObject=_29f.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_2a0){
if(this.portlet==null){
return;
}
if(_2a0){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2a1,_2a2,_2a3,http){
var _2a5=null;
if(http!=null){
_2a5=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2a5!=null){
_2a5=unescape(_2a5);
}
}
_2a3.setPortletContent(_2a1,_2a2,_2a5);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2a3.getId());
}else{
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2a7,_2a8,_2a9){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_2a8+" type: "+type+jetspeed.url.formatBindError(_2a7));
}};
jetspeed.om.PortletActionContentListener=function(_2aa,_2ab){
this.portlet=_2aa;
this.submittedFormBindObject=null;
if(_2ab!=null&&_2ab.submitFormBindObject!=null){
this.submittedFormBindObject=_2ab.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2ac){
if(this.portlet==null){
return;
}
if(_2ac){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2ad,_2ae,_2af,http){
var _2b1=null;
var _2b2=false;
var _2b3=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2ad);
if(_2b3.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2b3.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2b3.operation+"-url in response body: "+_2ad+"  url: "+_2b3.url+" entity-id: "+_2b3.portletEntityId);
}
_2b1=_2b3.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2ad);
}
_2b1=_2ad;
if(_2b1){
var _2b4=_2b1.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2b4==-1){
_2b2=true;
window.location.href=_2b1;
_2b1=null;
}else{
if(_2b4>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2ad);
_2b1=null;
}
}
}
}
if(_2b1!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2b1);
}
jetspeed.doRenderAll(_2b1);
}else{
this._setPortletLoading(false);
}
if(!_2b2&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2b6,_2b7,_2b8){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2b6));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2b9=this.getUrl();
if(_2b9){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2b9,this.getTarget());
}else{
jetspeed.updatePage(_2b9);
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
jetspeed.om.Menu=function(_2ba,_2bb){
this._is_parsed=false;
this.name=_2ba;
this.type=_2bb;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2bc){
if(!_2bc){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2bc);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2be){
if(!this.hasOptions()){
return null;
}
if(_2be==0||_2be>0){
if(_2be>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2be];
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
var _2c0=this.options[i];
if(_2c0 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2c2,_2c3){
var _2c4=this.parseMenu(data,_2c3.menuName,_2c3.menuType);
_2c3.page.putMenu(_2c4);
},notifyFailure:function(type,_2c6,_2c7,_2c8){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2c8.toString()+"] url: "+_2c7+" type: "+type+jetspeed.url.formatBindError(_2c6));
},parseMenu:function(node,_2ca,_2cb){
var menu=null;
var _2cd=node.getElementsByTagName("js");
if(!_2cd||_2cd.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2ce=_2cd[0].childNodes;
for(var i=0;i<_2ce.length;i++){
var _2d0=_2ce[i];
if(_2d0.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2d1=_2d0.nodeName;
if(_2d1=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2d0,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2ca;
}
if(menu.type==null){
menu.type=_2cb;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2d4=null;
var _2d5=node.childNodes;
for(var i=0;i<_2d5.length;i++){
var _2d7=_2d5[i];
if(_2d7.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2d8=_2d7.nodeName;
if(_2d8=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2d7,new jetspeed.om.Menu()));
}
}else{
if(_2d8=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2d7,new jetspeed.om.MenuOption()));
}
}else{
if(_2d8=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2d7,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2d8){
mObj[_2d8]=((_2d7&&_2d7.firstChild)?_2d7.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2d9){
this.includeMenuDefs=_2d9;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2db,_2dc){
var _2dd=this.getMenuDefs(data,_2db,_2dc);
for(var i=0;i<_2dd.length;i++){
var mObj=_2dd[i];
_2dc.page.putMenu(mObj);
}
this.notifyFinished(_2dc);
},getMenuDefs:function(data,_2e1,_2e2){
var _2e3=[];
var _2e4=data.getElementsByTagName("menu");
for(var i=0;i<_2e4.length;i++){
var _2e6=_2e4[i].getAttribute("type");
if(this.includeMenuDefs){
_2e3.push(this.parseMenuObject(_2e4[i],new jetspeed.om.Menu(null,_2e6)));
}else{
var _2e7=_2e4[i].firstChild.nodeValue;
_2e3.push(new jetspeed.om.Menu(_2e7,_2e6));
}
}
return _2e3;
},notifyFailure:function(type,_2e9,_2ea,_2eb){
dojo.raise("MenusAjaxApiContentListener error ["+_2eb.toString()+"] url: "+_2ea+" type: "+type+jetspeed.url.formatBindError(_2e9));
},notifyFinished:function(_2ec){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_2ed){
this.portletEntityId=_2ed;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_2ef,_2f0){
if(jetspeed.url.checkAjaxApiResponse(_2ef,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_2f1){
var _2f2=jetspeed.page.getPortlet(this.portletEntityId);
if(_2f2){
if(_2f1){
_2f2.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_2f2.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_2f4,_2f5,_2f6){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_2f6.toString()+"] url: "+_2f5+" type: "+type+jetspeed.url.formatBindError(_2f4));
}});
jetspeed.om.PageChangeActionContentListener=function(_2f7){
this.pageActionUrl=_2f7;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_2f9,_2fa){
if(jetspeed.url.checkAjaxApiResponse(_2f9,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_2fc,_2fd,_2fe){
dojo.raise("PageChangeActionContentListener error ["+_2fe.toString()+"] url: "+_2fd+" type: "+type+jetspeed.url.formatBindError(_2fc));
}});
jetspeed.om.PortletActionsContentListener=function(_2ff){
this.portletEntityIds=_2ff;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_300){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _302=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_302){
if(_300){
_302.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_302.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_304,_305){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_304,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _307=this.parsePortletActionsResponse(node);
for(var i=0;i<_307.length;i++){
var _309=_307[i];
var _30a=_309.id;
var _30b=jetspeed.page.getPortlet(_30a);
if(_30b!=null){
_30b.updateActions(_309.actions,_309.currentActionState,_309.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _30d=new Array();
var _30e=node.getElementsByTagName("js");
if(!_30e||_30e.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _30d;
}
var _30f=_30e[0].childNodes;
for(var i=0;i<_30f.length;i++){
var _311=_30f[i];
if(_311.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _312=_311.nodeName;
if(_312=="portlets"){
var _313=_311;
var _314=_313.childNodes;
for(var pI=0;pI<_314.length;pI++){
var _316=_314[pI];
if(_316.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _317=_316.nodeName;
if(_317=="portlet"){
var _318=this.parsePortletElement(_316);
if(_318!=null){
_30d.push(_318);
}
}
}
}
}
return _30d;
},parsePortletElement:function(node){
var _31a=node.getAttribute("id");
if(_31a!=null){
var _31b=jetspeed.page._parsePSMLActions(node,null);
var _31c=jetspeed.page._parsePSMLCurrentActionState(node);
var _31d=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_31a,actions:_31b,currentActionState:_31c,currentActionMode:_31d};
}
return null;
},notifyFailure:function(type,_31f,_320,_321){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_321.toString()+"] url: "+_320+" type: "+type+jetspeed.url.formatBindError(_31f));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_322,_323,_324){
this.portletDef=_322;
this.windowWidgetId=_323;
this.addToCurrentPage=_324;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_326,_327){
if(jetspeed.url.checkAjaxApiResponse(_326,data,true,"add-portlet")){
var _328=this.parseAddPortletResponse(data);
if(_328&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_328,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _32a=null;
var _32b=node.getElementsByTagName("js");
if(!_32b||_32b.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _32c=_32b[0].childNodes;
for(var i=0;i<_32c.length;i++){
var _32e=_32c[i];
if(_32e.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _32f=_32e.nodeName;
if(_32f=="entity"){
_32a=((_32e&&_32e.firstChild)?_32e.firstChild.nodeValue:null);
break;
}
}
return _32a;
},notifyFailure:function(type,_331,_332,_333){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_333.toString()+"] url: "+_332+" type: "+type+jetspeed.url.formatBindError(_331));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_335,_336){
var _337=this.parsePortlets(data);
var _338=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_338!=null){
for(var i=0;i<_337.length;i++){
_338.addChild(_337[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_336,_337);
}
},notifyFailure:function(type,_33b,_33c,_33d){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_33d.toString()+"] url: "+_33c+" type: "+type+jetspeed.url.formatBindError(_33b));
},parsePortlets:function(node){
var _33f=[];
var _340=node.getElementsByTagName("js");
if(!_340||_340.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _341=_340[0].childNodes;
for(var i=0;i<_341.length;i++){
var _343=_341[i];
if(_343.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _344=_343.nodeName;
if(_344=="portlets"){
var _345=_343;
var _346=_345.childNodes;
for(var pI=0;pI<_346.length;pI++){
var _348=_346[pI];
if(_348.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _349=_348.nodeName;
if(_349=="portlet"){
var _34a=this.parsePortletElement(_348);
_33f.push(_34a);
}
}
}
}
return _33f;
},parsePortletElement:function(node){
var _34c=node.getAttribute("name");
var _34d=node.getAttribute("displayName");
var _34e=node.getAttribute("description");
var _34f=node.getAttribute("image");
var _350=0;
return new jetspeed.om.PortletDef(_34c,_34d,_34e,_34f,_350);
}});
jetspeed.om.FoldersListContentListener=function(_351){
this.notifyFinished=_351;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_353,_354){
var _355=this.parseFolders(data);
var _356=this.parsePages(data);
var _357=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_354,_355,_356,_357);
}
},notifyFailure:function(type,_359,_35a,_35b){
dojo.raise("FoldersListContentListener error ["+_35b.toString()+"] url: "+_35a+" type: "+type+jetspeed.url.formatBindError(_359));
},parseFolders:function(node){
var _35d=[];
var _35e=node.getElementsByTagName("js");
if(!_35e||_35e.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _35f=_35e[0].childNodes;
for(var i=0;i<_35f.length;i++){
var _361=_35f[i];
if(_361.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _362=_361.nodeName;
if(_362=="folders"){
var _363=_361;
var _364=_363.childNodes;
for(var pI=0;pI<_364.length;pI++){
var _366=_364[pI];
if(_366.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _367=_366.nodeName;
if(_367=="folder"){
var _368=this.parsePortletElement(_366);
_35d.push(_368);
}
}
}
}
return _35d;
},parsePages:function(node){
var _36a=[];
var _36b=node.getElementsByTagName("js");
if(!_36b||_36b.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _36c=_36b[0].childNodes;
for(var i=0;i<_36c.length;i++){
var _36e=_36c[i];
if(_36e.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _36f=_36e.nodeName;
if(_36f=="folders"){
var _370=_36e;
var _371=_370.childNodes;
for(var pI=0;pI<_371.length;pI++){
var _373=_371[pI];
if(_373.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _374=_373.nodeName;
if(_374=="page"){
var _375=this.parsePortletElement(_373);
_36a.push(_375);
}
}
}
}
return _36a;
},parseLinks:function(node){
var _377=[];
var _378=node.getElementsByTagName("js");
if(!_378||_378.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _379=_378[0].childNodes;
for(var i=0;i<_379.length;i++){
var _37b=_379[i];
if(_37b.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _37c=_37b.nodeName;
if(_37c=="folders"){
var _37d=_37b;
var _37e=_37d.childNodes;
for(var pI=0;pI<_37e.length;pI++){
var _380=_37e[pI];
if(_380.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _381=_380.nodeName;
if(_381=="link"){
var _382=this.parsePortletElement(_380);
_377.push(_382);
}
}
}
}
return _377;
},parsePortletElement:function(node){
var _384=node.getAttribute("name");
var _385=node.getAttribute("path");
return new jetspeed.om.FolderDef(_384,_385);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_386){
this.notifyFinished=_386;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_388,_389){
var _38a=this.parsePortlets(data);
var _38b=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_389,_38a,_38b);
}
},notifyFailure:function(type,_38d,_38e,_38f){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_38f.toString()+"] url: "+_38e+" type: "+type+jetspeed.url.formatBindError(_38d));
},parsList:function(node){
var _391;
var _392=node.getElementsByTagName("js");
if(!_392||_392.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _393=_392[0].childNodes;
for(var i=0;i<_393.length;i++){
var _395=_393[i];
if(_395.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _396=_395.nodeName;
if(_396=="resultCount"){
_391=_395.textContent;
}
}
return _391;
},parsePortlets:function(node){
var _398=[];
var _399=node.getElementsByTagName("js");
if(!_399||_399.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _39a=_399[0].childNodes;
for(var i=0;i<_39a.length;i++){
var _39c=_39a[i];
if(_39c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _39d=_39c.nodeName;
if(_39d=="portlets"){
var _39e=_39c;
var _39f=_39e.childNodes;
for(var pI=0;pI<_39f.length;pI++){
var _3a1=_39f[pI];
if(_3a1.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3a2=_3a1.nodeName;
if(_3a2=="portlet"){
var _3a3=this.parsePortletElement(_3a1);
_398.push(_3a3);
}
}
}
}
return _398;
},parsePortletElement:function(node){
var _3a5=node.getAttribute("name");
var _3a6=node.getAttribute("displayName");
var _3a7=node.getAttribute("description");
var _3a8=node.getAttribute("image");
var _3a9=node.getAttribute("count");
return new jetspeed.om.PortletDef(_3a5,_3a6,_3a7,_3a8,_3a9);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3aa,_3ab){
this.portlet=_3aa;
this.changedState=_3ab;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3ac){
if(this.portlet==null){
return;
}
if(_3ac){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3ae,_3af){
this._setPortletLoading(false);
dojo.lang.mixin(_3af.portlet.lastSavedWindowState,this.changedState);
var _3b0=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3b0=true;
}
jetspeed.url.checkAjaxApiResponse(_3ae,data,_3b0,("move-portlet ["+_3af.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3b2,_3b3,_3b4){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3b4.entityId+"] url: "+_3b3+" type: "+type+jetspeed.url.formatBindError(_3b2));
}};
jetspeed.ui.getPortletWindowChildren=function(_3b5,_3b6,_3b7,_3b8){
if(_3b7||_3b8){
_3b7=true;
}
var _3b9=null;
var _3ba=-1;
if(_3b5){
_3b9=[];
var _3bb=_3b5.childNodes;
if(_3bb!=null&&_3bb.length>0){
for(var i=0;i<_3bb.length;i++){
var _3bd=_3bb[i];
if((!_3b8&&dojo.html.hasClass(_3bd,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3b7&&dojo.html.hasClass(_3bd,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3b9.push(_3bd);
if(_3b6&&_3bd==_3b6){
_3ba=_3b9.length-1;
}
}else{
if(_3b6&&_3bd==_3b6){
_3b9.push(_3bd);
_3ba=_3b9.length-1;
}
}
}
}
}
return {portletWindowNodes:_3b9,matchIndex:_3ba};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3be){
var _3bf=null;
if(_3be){
_3bf=new Array();
for(var i=0;i<_3be.length;i++){
var _3c1=dojo.widget.byNode(_3be[i]);
if(_3c1){
_3bf.push(_3c1);
}
}
}
return _3bf;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3c3=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3c3.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3c5=jetspeed.page.columns[i];
var _3c6=jetspeed.ui.getPortletWindowChildren(_3c5.domNode,null);
var _3c7=jetspeed.ui.getPortletWindowsFromNodes(_3c6.portletWindowNodes);
var _3c8={dumpMsg:""};
if(_3c7!=null){
dojo.lang.forEach(_3c7,function(_3c9){
_3c8.dumpMsg=_3c8.dumpMsg+(_3c8.dumpMsg.length>0?", ":"")+_3c9.portlet.entityId;
});
}
_3c8.dumpMsg="column "+i+": "+_3c8.dumpMsg;
dojo.debug(_3c8.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3ca=jetspeed.ui.getAllPortletWindowWidgets();
var _3cb="";
for(var i=0;i<_3ca.length;i++){
if(i>0){
_3cb+=", ";
}
_3cb+=_3ca[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3cb);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3cd=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3ce=jetspeed.ui.getPortletWindowsFromNodes(_3cd.portletWindowNodes);
if(_3ce==null){
_3ce=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d0=jetspeed.page.columns[i];
var _3d1=jetspeed.ui.getPortletWindowChildren(_3d0.domNode,null);
var _3d2=jetspeed.ui.getPortletWindowsFromNodes(_3d1.portletWindowNodes);
if(_3d2!=null){
_3ce=_3ce.concat(_3d2);
}
}
return _3ce;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3d3,_3d4){
var _3d5=_3d3.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3d5==null){
_3d5=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3d5=false;
}
}
var _3d6=dojo.widget.byId(_3d3.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3d6){
_3d6.resetWindow(_3d3);
}else{
_3d6=jetspeed.ui.createPortletWindowWidget(_3d3);
}
if(_3d6){
if(!_3d5||_3d4>=jetspeed.page.columns.length){
_3d6.domNode.style.position="absolute";
var _3d7=document.getElementById(jetspeed.id.DESKTOP);
_3d7.appendChild(_3d6.domNode);
}else{
var _3d8=null;
var _3d9=-1;
var _3da=_3d4;
if(_3da!=null&&_3da>=0&&_3da<jetspeed.page.columns.length){
_3d9=_3da;
_3d8=jetspeed.page.columns[_3d9];
}
if(_3d9==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3dc=jetspeed.page.columns[i];
if(!_3dc.domNode.hasChildNodes()){
_3d8=_3dc;
_3d9=i;
break;
}
if(_3d8==null||_3d8.domNode.childNodes.length>_3dc.domNode.childNodes.length){
_3d8=_3dc;
_3d9=i;
}
}
}
if(_3d8){
_3d8.domNode.appendChild(_3d6.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3dd,_3de){
if(!_3de){
_3de={};
}
if(_3dd instanceof jetspeed.om.Portlet){
_3de.portlet=_3dd;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3de,_3dd);
}
var _3df=dojo.widget.createWidget("jetspeed:PortletWindow",_3de);
return _3df;
};
jetspeed.ui.fadeIn=function(_3e0,_3e1,_3e2){
jetspeed.ui.fade(_3e0,_3e1,_3e2,0,1);
};
jetspeed.ui.fadeOut=function(_3e3,_3e4,_3e5){
jetspeed.ui.fade(_3e3,_3e4,"hidden",1,0,_3e5);
};
jetspeed.ui.fade=function(_3e6,_3e7,_3e8,_3e9,_3ea,_3eb){
if(_3e6.length>0){
for(var i=0;i<_3e6.length;i++){
dojo.lfx.html._makeFadeable(_3e6[i]);
if(_3e8!="none"){
_3e6[i].style.visibility=_3e8;
}
}
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([_3e9],[_3ea]),_3e7,0);
dojo.event.connect(anim,"onAnimate",function(e){
for(var mi=0;mi<_3e6.length;mi++){
dojo.html.setOpacity(_3e6[mi],e.x);
}
});
if(_3e8=="hidden"){
dojo.event.connect(anim,"onEnd",function(e){
for(var mi=0;mi<_3e6.length;mi++){
_3e6[mi].style.visibility=_3e8;
}
if(_3eb){
for(var mi=0;mi<_3eb.length;mi++){
_3eb[mi].style.display="none";
}
}
});
}
anim.play(true);
}
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3f2=jetspeed.debugWindowReadCookie(true);
var _3f3={};
var _3f4=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3f3[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3f4;
_3f3[jetspeed.id.PORTLET_PROP_WIDTH]=_3f2.width;
_3f3[jetspeed.id.PORTLET_PROP_HEIGHT]=_3f2.height;
_3f3[jetspeed.id.PORTLET_PROP_LEFT]=_3f2.left;
_3f3[jetspeed.id.PORTLET_PROP_TOP]=_3f2.top;
_3f3[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3f3[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3f3[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3f2.windowState;
var _3f5=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3f3);
jetspeed.ui.createPortletWindow(_3f5);
_3f5.retrieveContent(null,null);
var _3f6=dojo.widget.byId(_3f4);
var _3f7=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3f6,"contentChanged");
dojo.event.connect(_3f6,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3f6,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3f6,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3f8){
var _3f9={};
if(_3f8){
_3f9={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _3fa=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_3fa!=null&&_3fa.length>0){
var _3fb=_3fa.split("|");
if(_3fb&&_3fb.length>=4){
_3f9.width=_3fb[0];
_3f9.height=_3fb[1];
_3f9.top=_3fb[2];
_3f9.left=_3fb[3];
if(_3fb.length>4&&_3fb[4]!=null&&_3fb[4].length>0){
_3f9.windowState=_3fb[4];
}
}
}
return _3f9;
};
jetspeed.debugWindowRestore=function(){
var _3fc=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3fd=dojo.widget.byId(_3fc);
if(!_3fd){
return;
}
_3fd.restoreWindow();
};
jetspeed.debugWindow=function(){
var _3fe=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_3fe);
};
jetspeed.debugWindowSave=function(){
var _3ff=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _400=dojo.widget.byId(_3ff);
if(!_400){
return null;
}
if(!_400.windowPositionStatic){
var _401=_400.getCurrentWindowStateForPersistence(false);
var _402=_401.width;
var _403=_401.height;
var cTop=_401.top;
var _405=_401.left;
if(_400.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _406=_400.getLastPositionInfo();
if(_406!=null){
if(_406.height!=null&&_406.height>0){
_403=_406.height;
}
}else{
var _407=jetspeed.debugWindowReadCookie(false);
if(_407.height!=null&&_407.height>0){
_403=_407.height;
}
}
}
var _408=_402+"|"+_403+"|"+cTop+"|"+_405+"|"+_400.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_408,30,"/");
}
};
jetspeed.debugDumpForm=function(_409){
if(!_409){
return null;
}
var _40a=_409.toString();
if(_409.name){
_40a+=" name="+_409.name;
}
if(_409.id){
_40a+=" id="+_409.id;
}
var _40b=dojo.io.encodeForm(_409);
_40a+=" data="+_40b;
return _40a;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_40c,_40d,_40e,_40f){
if(!_40c){
_40c={};
}
if(!this.initialized){
var _410="";
if(jetspeed.altDebugWindowContent){
_410=jetspeed.altDebugWindowContent();
}else{
_410+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_40d){
_40d=new jetspeed.om.BasicContentListener();
}
_40d.notifySuccess(_410,_40c.url,_40e);
this.initialized=true;
}
}};

