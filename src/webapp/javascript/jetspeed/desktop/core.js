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
jetspeed.prefs={windowTiling:true,windowHeightExpand:false,windowWidth:null,windowHeight:null,layoutName:null,layoutRootUrl:null,getLayoutName:function(){
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
jetspeed.debug={pageLoad:true,retrievePsml:false,setPortletContent:false,doRenderDoAction:false,postParseAnnotateHtml:false,postParseAnnotateHtmlDisableAnchors:false,confirmOnSubmit:false,createWindow:false,initializeWindowState:false,submitChangedWindowState:false,windowDecorationRandom:false,debugContainerId:(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId)};
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
jetspeed.updatePage=function(){
var _c=jetspeed.page;
if(_c!=null){
jetspeed.page=new jetspeed.om.Page();
jetspeed.page.retrievePsml(jetspeed.om.PageContentListenerUpdate(_c));
}
};
jetspeed.doRender=function(_d,_e){
if(!_d){
_d={};
}else{
if((typeof _d=="string"||_d instanceof String)){
_d={url:_d};
}
}
var _f=jetspeed.page.getPortlet(_e);
if(_f){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_e+"] url: "+_d.url);
}
_f.retrieveContent(null,_d);
}
};
jetspeed.doRenderAll=function(url,_11,_12){
var _13=jetspeed.debug.doRenderDoAction;
var _14=jetspeed.debug.pageLoad&&_12;
if(!_11){
_11=jetspeed.page.getPortletArray();
}
var _15="";
var _16=true;
var _17=null;
if(_12){
_17=jetspeed.url.parse(jetspeed.page.getPageUrl());
}
for(var i=0;i<_11.length;i++){
var _19=_11[i];
if((_13||_14)){
if(i>0){
_15=_15+", ";
}
var _1a=null;
if(_19.getProperty!=null){
_1a=_19.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
}
if(!_1a){
_1a=_19.widgetId;
}
if(!_1a){
_1a=_19.toString();
}
if(_19.entityId){
_15=_15+_19.entityId+"("+_1a+")";
if(_14&&_19.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE)){
_15=_15+" "+_19.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
}
}else{
_15=_15+_1a;
}
}
_19.retrieveContent(null,{url:url,jsPageUrl:_17},_16);
}
if(_13){
dojo.debug("doRenderAll ["+_15+"] url: "+url);
}else{
if(_14){
dojo.debug("doRenderAll page-url: "+jetspeed.page.getPsmlUrl()+" portlets: ["+_15+"]"+(url?(" url: "+url):""));
}
}
};
jetspeed.doAction=function(_1b,_1c){
if(!_1b){
_1b={};
}else{
if((typeof _1b=="string"||_1b instanceof String)){
_1b={url:_1b};
}
}
var _1d=jetspeed.page.getPortlet(_1c);
if(_1d){
if(jetspeed.debug.doRenderDoAction){
if(!_1b.formNode){
dojo.debug("doAction ["+_1c+"] url: "+_1b.url+" form: null");
}else{
dojo.debug("doAction ["+_1c+"] url: "+_1b.url+" form: "+jetspeed.debugDumpForm(_1b.formNode));
}
}
_1d.retrieveContent(new jetspeed.om.PortletActionContentListener(_1d,_1b),_1b);
}
};
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrlForDesktopActionRender:function(_1e){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _20=_1e;
var _21=null;
if(_1e&&_1e.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_1e.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_1e&&_1e.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_1e.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_21=jetspeed.url.getQueryParameter(_1e,"entity");
}
if(!jetspeed.url.validateUrlStartsWithHttp(_20)){
_20=null;
}
return {url:_20,operation:op,portletEntityId:_21};
},generateJSPseudoUrlActionRender:function(_22,_23){
if(!_22||!_22.url||!_22.portletEntityId){
return null;
}
var _24=null;
if(_23){
_24=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_24="javascript:";
var _25=false;
if(_22.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_24+="doAction(\"";
}else{
if(_22.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_24+="doRender(\"";
}else{
_25=true;
}
}
if(_25){
return null;
}
_24+=_22.url+"\",\""+_22.portletEntityId+"\"";
_24+=")";
}
return _24;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_26){
var _27=jetspeed.prefs.getPortletDecorationConfig(_26);
if(_27!=null&&!_27.css_loaded){
var _28=jetspeed.prefs.getPortletDecorationBaseUrl(_26);
_27.css_loaded=true;
_27.cssPathCommon=new dojo.uri.Uri(_28+"/css/styles.css");
_27.cssPathDesktop=new dojo.uri.Uri(_28+"/css/desktop.css");
dojo.html.insertCssFile(_27.cssPathCommon,null,true);
dojo.html.insertCssFile(_27.cssPathDesktop,null,true);
}
return _27;
};
jetspeed.loadPortletDecorationConfig=function(_29){
var _2a={};
jetspeed.prefs.portletDecorationsConfig[_29]=_2a;
_2a.windowActionButtonOrder=jetspeed.prefs.windowActionButtonOrder;
_2a.windowActionNotPortlet=jetspeed.prefs.windowActionNotPortlet;
_2a.windowActionButtonMax=jetspeed.prefs.windowActionButtonMax;
_2a.windowActionButtonHide=jetspeed.prefs.windowActionButtonHide;
_2a.windowActionButtonTooltip=jetspeed.prefs.windowActionButtonTooltip;
_2a.windowActionMenuOrder=jetspeed.prefs.windowActionMenuOrder;
_2a.windowActionNoImage=jetspeed.prefs.windowActionNoImage;
_2a.windowIconEnabled=jetspeed.prefs.windowIconEnabled;
_2a.windowIconPath=jetspeed.prefs.windowIconPath;
var _2b=jetspeed.prefs.getPortletDecorationBaseUrl(_29)+"/"+_29+".js";
dojo.hostenv.loadUri(_2b,function(_2c){
for(var j in _2c){
_2a[j]=_2c[j];
}
if(_2a.windowActionNoImage!=null){
var _2e={};
for(var i=0;i<_2a.windowActionNoImage.length;i++){
_2e[_2a.windowActionNoImage[i]]=true;
}
_2a.windowActionNoImage=_2e;
}
if(_2a.windowIconPath!=null){
_2a.windowIconPath=dojo.string.trim(_2a.windowIconPath);
if(_2a.windowIconPath==null||_2a.windowIconPath.length==0){
_2a.windowIconPath=null;
}else{
var _30=_2a.windowIconPath;
var _31=_30.charAt(0);
if(_31!="/"){
_30="/"+_30;
}
var _32=_30.charAt(_30.length-1);
if(_32!="/"){
_30=_30+"/";
}
_2a.windowIconPath=_30;
}
}
});
};
jetspeed.purifyIdentifier=function(src,_34,_35){
if(src==null){
return src;
}
var _36=src.length;
if(_36==0){
return src;
}
if(_34==null){
_34="_";
}
var _37=new RegExp("[^a-z_0-9A-Z]","g");
var _38=src.charCodeAt(0);
var _39=null;
if((_38>=65&&_38<=90)||_38==95||(_38>=97&&_38<=122)){
_39=src.charAt(0);
}else{
_39=_34;
}
var _3a=false,_3b=false;
if(_35!=null){
_35=_35.toLowerCase();
_3a=(_35=="hi"?true:false);
_3b=(_35=="lo"?true:false);
}
if(_36>1){
if(_3a||_3b){
upNext=false;
for(var i=1;i<_36;i++){
_38=src.charCodeAt(i);
if((_38>=65&&_38<=90)||_38==95||(_38>=97&&_38<=122)||(_38>=48&&_38<=57)){
if(upNext&&(_38>=97&&_38<=122)){
_39+=String.fromCharCode(_38-32);
}else{
_39+=src.charAt(i);
}
upNext=false;
}else{
upNext=true;
_39+=_34;
}
}
}else{
_39+=src.substring(1).replace(_37,_34);
}
}
if(_3a){
_38=_39.charCodeAt(0);
if(_38>=97&&_38<=122){
_39=String.fromCharCode(_38-32)+_39.substring(1);
}
}
return _39;
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
jetspeed.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _3d=jetspeed.page.getMenuNames();
for(var i=0;i<_3d.length;i++){
var _3f=_3d[i];
var _40=dojo.widget.byId(jetspeed.id.MENU_WIDGET_ID_PREFIX+_3f);
if(_40){
_40.createJetspeedMenu(jetspeed.page.getMenu(_3f));
}
}
jetspeed.url.loadingIndicatorHide();
jetspeed.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_41){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_41);
}
};
jetspeed.menuNavClickWidget=function(_42,_43){
dojo.debug("jetspeed.menuNavClick");
if(!_42){
return;
}
if(dojo.lang.isString(_42)){
var _44=_42;
_42=dojo.widget.byId(_44);
if(!_42){
dojo.raise("menuNavClick could not find tab widget for "+_44);
}
}
if(_42){
var _45=_42.jetspeedmenuname;
if(!_45&&_42.extraArgs){
_45=_42.extraArgs.jetspeedmenuname;
}
if(!_45){
dojo.raise("menuNavClick tab widget ["+_42.widgetId+"] does not define jetspeedMenuName");
}
var _46=jetspeed.page.getMenu(_45);
if(!_46){
dojo.raise("menuNavClick Menu lookup for tab widget ["+_42.widgetId+"] failed: "+_45);
}
var _47=_46.getOptionByIndex(_43);
jetspeed.menuNavClick(_47);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_48,_49){
if(!_48||jetspeed.pageNavigateSuppress){
return;
}
if(jetspeed.page&&jetspeed.page.equalsPageUrl(_48)){
return;
}
_48=jetspeed.page.makePageUrl(_48);
if(_49=="top"){
top.location.href=_48;
}else{
if(_49=="parent"){
parent.location.href=_48;
}else{
window.location.href=_48;
}
}
};
jetspeed.loadPortletSelector=function(){
var _4a={};
_4a[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_4a[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_4a[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.page.getPortletDecorationDefault();
_4a[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]=jetspeed.prefs.portletSelectorWindowTitle;
_4a[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=jetspeed.prefs.portletSelectorWindowIcon;
_4a[jetspeed.id.PORTLET_PROP_WIDGET_ID]=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.SELECTOR;
_4a[jetspeed.id.PORTLET_PROP_WIDTH]=jetspeed.prefs.portletSelectorBounds.width;
_4a[jetspeed.id.PORTLET_PROP_HEIGHT]=jetspeed.prefs.portletSelectorBounds.height;
_4a[jetspeed.id.PORTLET_PROP_LEFT]=jetspeed.prefs.portletSelectorBounds.x;
_4a[jetspeed.id.PORTLET_PROP_TOP]=jetspeed.prefs.portletSelectorBounds.y;
_4a[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=true;
_4a[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.PortletSelectorContentRetriever();
var _4b=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_4a);
jetspeed.ui.createPortletWindow(_4b);
_4b.retrieveContent(null,null);
jetspeed.getPortletDefinitions();
};
jetspeed.getPortletDefinitions=function(){
var _4c=new jetspeed.om.PortletSelectorAjaxApiContentListener();
var _4d="?action=getportlets";
var _4e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_4d;
var _4f="text/xml";
var _50=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_4e,mimetype:_4f},_4c,_50,jetspeed.debugContentDumpIds);
};
jetspeed.searchForPortletDefinitions=function(_51,_52){
var _53=new jetspeed.om.PortletSelectorSearchContentListener(_52);
var _54="?action=getportlets&filter="+_51;
var _55=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_54;
var _56="text/xml";
var _57=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_55,mimetype:_56},_53,_57,jetspeed.debugContentDumpIds);
};
jetspeed.getFolders=function(_58,_59){
var _5a=new jetspeed.om.FoldersListContentListener(_59);
var _5b="?action=getfolders&data="+_58;
var _5c=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_5b;
var _5d="text/xml";
var _5e=new jetspeed.om.Id("getfolders",{});
jetspeed.url.retrieveContent({url:_5c,mimetype:_5d},_5a,_5e,jetspeed.debugContentDumpIds);
};
jetspeed.portletDefinitionsforSelector=function(_5f,_60,_61,_62,_63){
var _64=new jetspeed.om.PortletSelectorSearchContentListener(_63);
var _65="?action=selectorPortlets&category="+_60+"&portletPerPages="+_62+"&pageNumber="+_61+"&filter="+_5f;
var _66=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_65;
var _67="text/xml";
var _68=new jetspeed.om.Id("selectorPortlets",{});
jetspeed.url.retrieveContent({url:_66,mimetype:_67},_64,_68,jetspeed.debugContentDumpIds);
};
jetspeed.getActionsForPortlet=function(_69){
if(_69==null){
return;
}
jetspeed.getActionsForPortlets([_69]);
};
jetspeed.getActionsForPortlets=function(_6a){
if(_6a==null){
_6a=jetspeed.page.getPortletIds();
}
var _6b=new jetspeed.om.PortletActionsContentListener(_6a);
var _6c="?action=getactions";
for(var i=0;i<_6a.length;i++){
_6c+="&id="+_6a[i];
}
var _6e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_6c;
var _6f="text/xml";
var _70=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_6e,mimetype:_6f},_6b,_70,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_71,_72,_73,_74){
if(_71==null){
return;
}
if(_74==null){
_74=new jetspeed.om.PortletChangeActionContentListener(_71);
}
var _75="?action=window&id="+(_71!=null?_71:"");
if(_72!=null){
_75+="&state="+_72;
}
if(_73!=null){
_75+="&mode="+_73;
}
var _76=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_75;
var _77="text/xml";
var _78=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_76,mimetype:_77},_74,_78,jetspeed.debugContentDumpIds);
};
jetspeed.addNewPortletDefinition=function(_79,_7a,_7b,_7c){
var _7d=true;
if(_7b!=null){
_7d=false;
}
var _7e=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(_79,_7a,_7d);
var _7f="?action=add&id="+escape(_79.getPortletName());
if(_7c!=null&&_7c.length>0){
_7f+="&layoutid="+escape(_7c);
}
var _80=null;
if(_7b!=null){
_80=_7b+_7f;
}else{
_80=jetspeed.page.getPsmlUrl()+_7f;
}
var _81="text/xml";
var _82=new jetspeed.om.Id("addportlet",{});
jetspeed.url.retrieveContent({url:_80,mimetype:_81},_7e,_82,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(){
if(!jetspeed.page.editMode){
var _83=true;
var _84=jetspeed.url.getQueryParameter(document.location.href,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
if(_84!=null&&_84=="true"){
_83=false;
}
jetspeed.page.editMode=true;
var _85=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(_85==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_85=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PAGE_EDITOR_WIDGET_ID,editorInitiatedFromDesktop:_83});
var _86=document.getElementById(jetspeed.id.COLUMNS);
_86.insertBefore(_85.domNode,_86.firstChild);
}
catch(e){
jetspeed.url.loadingIndicatorHide();
}
}else{
_85.editPageShow();
}
jetspeed.page.syncPageControls();
}
};
jetspeed.editPageTerminate=function(){
if(jetspeed.page.editMode){
var _87=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
_87.editModeNormal();
jetspeed.page.editMode=false;
if(!_87.editorInitiatedFromDesktop){
var _88=jetspeed.page.getPageUrl(true);
_88=jetspeed.url.removeQueryParameter(_88,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
_88=jetspeed.url.removeQueryParameter(_88,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_88;
}else{
if(_87!=null){
_87.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_89,_8a,_8b,_8c){
if(!_89){
_89={};
}
jetspeed.url.retrieveContent(_89,_8a,_8b,_8c);
}};
jetspeed.om.PortletSelectorContentRetriever=function(){
};
jetspeed.om.PortletSelectorContentRetriever.prototype={getContent:function(_8d,_8e,_8f,_90){
if(!_8d){
_8d={};
}
var _91="<div widgetId=\""+jetspeed.id.SELECTOR+"\" dojoType=\"PortletDefContainer\"></div>";
if(!_8e){
_8e=new jetspeed.om.BasicContentListener();
}
_8e.notifySuccess(_91,_8d.url,_8f);
}};
jetspeed.om.PortletSelectorContentListener=function(){
};
jetspeed.om.PortletSelectorContentListener.prototype={notifySuccess:function(_92,_93,_94){
var _95=this.getPortletWindow();
if(_95){
_95.setPortletContent(_92,renderUrl);
}
},notifyFailure:function(_96,_97,_98,_99){
dojo.raise("PortletSelectorContentListener notifyFailure url: "+_98+" type: "+_96+jetspeed.url.formatBindError(_97));
}};
jetspeed.om.PageContentListenerUpdate=function(_9a){
this.previousPage=_9a;
};
jetspeed.om.PageContentListenerUpdate.prototype={notifySuccess:function(_9b,_9c,_9d){
dojo.raise("PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url="+_9c);
},notifyFailure:function(_9e,_9f,_a0,_a1){
dojo.raise("PageContentListenerUpdate notifyFailure url: "+_a0+" type: "+_9e+jetspeed.url.formatBindError(_9f));
}};
jetspeed.om.PageContentListenerCreateWidget=function(){
};
jetspeed.om.PageContentListenerCreateWidget.prototype={notifySuccess:function(_a2,_a3,_a4){
_a4.loadFromPSML(_a2);
},notifyFailure:function(_a5,_a6,_a7,_a8){
dojo.raise("PageContentListenerCreateWidget error url: "+_a7+" type: "+_a5+jetspeed.url.formatBindError(_a6));
}};
jetspeed.om.Id=function(){
var _a9="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_a9.length>0){
_a9+="-";
}
_a9+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _ab in arguments[i]){
this[_ab]=arguments[i][_ab];
}
}
}
}
this.jetspeed_om_id=_a9;
};
dojo.lang.extend(jetspeed.om.Id,{getId:function(){
return this.jetspeed_om_id;
}});
jetspeed.om.Page=function(_ac,_ad,_ae){
this.psmlPath=_ac;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
this.name=_ad;
this.title=_ae;
this.layouts={};
this.columns=[];
this.portlets=[];
this.menus=[];
};
dojo.inherits(jetspeed.om.Page,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,layouts:null,columns:null,portlets:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _af=(this.name!=null&&this.name.length>0?this.name:null);
if(!_af){
this.getPsmlUrl();
_af=this.psmlPath;
}
return "page-"+_af;
},setPsmlPathFromDocumentUrl:function(){
var _b0=jetspeed.url.path.AJAX_API;
var _b1=document.location.pathname;
var _b2=jetspeed.url.path.DESKTOP;
var _b3=_b1.indexOf(_b2);
if(_b3!=-1&&_b1.length>(_b3+_b2.length)){
_b0=_b0+_b1.substring(_b3+_b2.length);
}
this.psmlPath=_b0;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _b4=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_b4=jetspeed.url.addQueryParameter(_b4,"layoutid",jetspeed.prefs.printModeOnly.layout);
_b4=jetspeed.url.addQueryParameter(_b4,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _b4;
},retrievePsml:function(_b5){
if(_b5==null){
_b5=new jetspeed.om.PageContentListenerCreateWidget();
}
var _b6=this.getPsmlUrl();
var _b7="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_b6);
}
jetspeed.url.retrieveContent({url:_b6,mimetype:_b7},_b5,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_b8){
var _b9=this._parsePSML(_b8);
var _ba={};
this.columnsStructure=this._layoutCreateModel(_b9,null,_ba,true);
this.rootFragmentId=_b9.id;
if(jetspeed.prefs.windowTiling){
this._createColumnsStart(document.getElementById(jetspeed.id.DESKTOP));
}
var _bb=new Array();
var _bc=this.columns.length;
for(var _bd=0;_bd<=this.columns.length;_bd++){
var _be=null;
if(_bd==_bc){
_be=_ba["z"];
if(_be!=null){
_be.sort(this._loadPortletZIndexCompare);
}
}else{
_be=_ba[_bd.toString()];
}
if(_be!=null){
for(var i=0;i<_be.length;i++){
var _c0=_be[i].portlet;
_bb.push(_c0);
_c0.createPortletWindow(_bd);
}
}
}
if(jetspeed.prefs.printModeOnly==null){
if(_bb&&_bb.length>0){
jetspeed.doRenderAll(null,_bb,true);
}
this._portletsInitializeWindowState(_ba["z"]);
this.retrieveAllMenus();
this.renderPageControls();
this.syncPageControls();
var _c1=jetspeed.url.getQueryParameter(document.location.href,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
if((_c1!=null&&_c1=="true")||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions!=null&&(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null)){
jetspeed.editPageInitiate();
}
}
}else{
var _c0=null;
for(var _c2 in this.portlets){
_c0=this.portlets[_c2];
break;
}
if(_c0!=null){
_c0.renderAction(null,jetspeed.prefs.printModeOnly.action);
this._portletsInitializeWindowState(_ba["z"]);
}
}
},_parsePSML:function(_c3){
var _c4=_c3.getElementsByTagName("page");
if(!_c4||_c4.length>1){
dojo.raise("unexpected zero or multiple <page> elements in psml");
}
var _c5=_c4[0];
var _c6=_c5.childNodes;
var _c7=new RegExp("(name|path|profiledPath|title|short-title)");
var _c8=null;
var _c9={};
for(var i=0;i<_c6.length;i++){
var _cb=_c6[i];
if(_cb.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _cc=_cb.nodeName;
if(_cc=="fragment"){
_c8=_cb;
}else{
if(_cc=="defaults"){
this.layoutDecorator=_cb.getAttribute("layout-decorator");
this.portletDecorator=_cb.getAttribute("portlet-decorator");
}else{
if(_cc&&_cc.match(_c7)){
this[jetspeed.purifyIdentifier(_cc,"","lo")]=((_cb&&_cb.firstChild)?_cb.firstChild.nodeValue:null);
}else{
if(_cc=="action"){
this._parsePSMLAction(_cb,_c9);
}
}
}
}
}
this.actions=_c9;
if(_c8==null){
dojo.raise("No root fragment in PSML.");
return null;
}
var _cd=this._parsePSMLLayoutFragment(_c8,0);
return _cd;
},_parsePSMLLayoutFragment:function(_ce,_cf){
var _d0=new Array();
var _d1=((_ce!=null)?_ce.getAttribute("type"):null);
if(_d1!="layout"){
dojo.raise("_parsePSMLLayoutFragment called with non-layout fragment: "+_ce);
return null;
}
var _d2=false;
var _d3=_ce.getAttribute("name");
if(_d3!=null){
_d3=_d3.toLowerCase();
if(_d3.indexOf("noactions")!=-1){
_d2=true;
}
}
var _d4=null,_d5=0;
var _d6={};
var _d7=_ce.childNodes;
var _d8,_d9,_da,_db,_dc;
for(var i=0;i<_d7.length;i++){
_d8=_d7[i];
if(_d8.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_d9=_d8.nodeName;
if(_d9=="fragment"){
_dc=_d8.getAttribute("type");
if(_dc=="layout"){
var _de=this._parsePSMLLayoutFragment(_d8,i);
if(_de!=null){
_d0.push(_de);
}
}else{
var _df=this._parsePSMLProperties(_d8,null);
var _e0=_df[jetspeed.id.PORTLET_PROP_WINDOW_ICON];
if(_e0==null||_e0.length==0){
_e0=this._parsePSMLIcon(_d8);
if(_e0!=null&&_e0.length>0){
_df[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=_e0;
}
}
_d0.push({id:_d8.getAttribute("id"),type:_dc,name:_d8.getAttribute("name"),properties:_df,actions:this._parsePSMLActions(_d8,null),currentActionState:this._parsePSMLCurrentActionState(_d8),currentActionMode:this._parsePSMLCurrentActionMode(_d8),decorator:_d8.getAttribute("decorator"),layoutActionsDisabled:_d2,documentOrderIndex:i});
}
}else{
if(_d9=="property"){
if(this._parsePSMLProperty(_d8,_d6)=="sizes"){
if(_d4!=null){
dojo.raise("_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: "+_ce);
return null;
}
if(jetspeed.prefs.printModeOnly!=null){
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
var _e3=new Array();
var _e4=new Array();
for(var i=0;i<_d0.length;i++){
if(_d0[i].type=="layout"){
_e3.push(i);
}else{
_e4.push(i);
}
}
if(_d4==null){
_d4=new Array();
_d4.push("100");
_d5=100;
}
return {id:_ce.getAttribute("id"),type:_d1,name:_ce.getAttribute("name"),decorator:_ce.getAttribute("decorator"),columnSizes:_d4,columnSizesSum:_d5,properties:_d6,fragments:_d0,layoutFragmentIndexes:_e3,otherFragmentIndexes:_e4,layoutActionsDisabled:_d2,documentOrderIndex:_cf};
},_parsePSMLActions:function(_e5,_e6){
if(_e6==null){
_e6={};
}
var _e7=_e5.getElementsByTagName("action");
for(var _e8=0;_e8<_e7.length;_e8++){
var _e9=_e7[_e8];
this._parsePSMLAction(_e9,_e6);
}
return _e6;
},_parsePSMLAction:function(_ea,_eb){
var _ec=_ea.getAttribute("id");
if(_ec!=null){
var _ed=_ea.getAttribute("type");
var _ee=_ea.getAttribute("name");
var _ef=_ea.getAttribute("url");
var _f0=_ea.getAttribute("alt");
_eb[_ec.toLowerCase()]={id:_ec,type:_ed,label:_ee,url:_ef,alt:_f0};
}
},_parsePSMLCurrentActionState:function(_f1){
var _f2=_f1.getElementsByTagName("state");
if(_f2!=null&&_f2.length==1&&_f2[0].firstChild!=null){
return _f2[0].firstChild.nodeValue;
}
return null;
},_parsePSMLCurrentActionMode:function(_f3){
var _f4=_f3.getElementsByTagName("mode");
if(_f4!=null&&_f4.length==1&&_f4[0].firstChild!=null){
return _f4[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_f5){
var _f6=_f5.getElementsByTagName("icon");
if(_f6!=null&&_f6.length==1&&_f6[0].firstChild!=null){
return _f6[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProperties:function(_f7,_f8){
if(_f8==null){
_f8={};
}
var _f9=_f7.getElementsByTagName("property");
for(var _fa=0;_fa<_f9.length;_fa++){
this._parsePSMLProperty(_f9[_fa],_f8);
}
return _f8;
},_parsePSMLProperty:function(_fb,_fc){
var _fd=_fb.getAttribute("name");
var _fe=_fb.getAttribute("value");
_fc[_fd]=_fe;
return _fd;
},_fragmentRowCompare:function(_ff,_100){
var rowA=_ff.documentOrderIndex*1000;
var rowB=_100.documentOrderIndex*1000;
var _103=_ff.properties["row"];
if(_103!=null){
rowA=_103;
}
var _104=_100.properties["row"];
if(_104!=null){
rowB=_104;
}
return (rowA-rowB);
},_layoutCreateModel:function(_105,_106,_107,_108){
var _109=this.columns.length;
var _10a=this._layoutRegisterAndCreateColumnsModel(_105,_106,_108);
var _10b=_10a.columnsInLayout;
if(_10a.addedLayoutHeaderColumn){
_109++;
}
var _10c=(_10b==null?0:_10b.length);
if(_105.layoutFragmentIndexes!=null&&_105.layoutFragmentIndexes.length>0){
var _10d=null;
var _10e=0;
if(_105.otherFragmentIndexes!=null&&_105.otherFragmentIndexes.length>0){
_10d=new Array();
}
for(var i=0;i<_105.fragments.length;i++){
var _110=_105.fragments[i];
}
var _111=new Array();
for(var i=0;i<_10c;i++){
if(_10d!=null){
_10d.push(null);
}
_111.push(false);
}
for(var i=0;i<_105.fragments.length;i++){
var _110=_105.fragments[i];
var _112=i;
if(_110.properties&&_110.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
if(_110.properties[jetspeed.id.PORTLET_PROP_COLUMN]!=null&&_110.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
_112=_110.properties[jetspeed.id.PORTLET_PROP_COLUMN];
}
}
if(_112>=_10c){
_112=(_10c>0?(_10c-1):0);
}
var _113=((_10d==null)?null:_10d[_112]);
if(_110.type=="layout"){
_111[_112]=true;
if(_113!=null){
this._layoutCreateModel(_113,_10b[_112],_107,true);
_10d[_112]=null;
}
this._layoutCreateModel(_110,_10b[_112],_107,false);
}else{
if(_113==null){
_10e++;
var _114={};
dojo.lang.mixin(_114,_105);
_114.fragments=new Array();
_114.layoutFragmentIndexes=new Array();
_114.otherFragmentIndexes=new Array();
_114.documentOrderIndex=_105.fragments[i].documentOrderIndex;
_114.clonedFromRootId=_114.id;
_114.clonedLayoutFragmentIndex=_10e;
_114.columnSizes=["100"];
_114.columnSizesSum=[100];
_114.id=_114.id+"-jsclone_"+_10e;
_10d[_112]=_114;
_113=_114;
}
_113.fragments.push(_110);
_113.otherFragmentIndexes.push(_113.fragments.length-1);
}
}
if(_10d!=null){
for(var i=0;i<_10c;i++){
var _113=_10d[i];
if(_113!=null){
_111[i]=true;
this._layoutCreateModel(_113,_10b[i],_107,true);
}
}
}
for(var i=0;i<_10c;i++){
if(_111[i]){
_10b[i].columnContainer=true;
}
}
if(_105.otherFragmentIndexes!=null&&_105.otherFragmentIndexes.length>0){
var _115=new Array();
for(var i=0;i<_105.fragments.length;i++){
var _116=true;
for(var j=0;j<_105.otherFragmentIndexes.length;j++){
if(_105.otherFragmentIndexes[j]==i){
_116=false;
break;
}
}
if(_116){
_115.push(_105.fragments[i]);
}
}
_105.fragments=_115;
_105.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_105,_10b,_109,_107);
return _10b;
},_layoutFragmentChildCollapse:function(_118,_119){
var _11a=false;
if(_119==null){
_119=_118;
}
if(_118.layoutFragmentIndexes!=null&&_118.layoutFragmentIndexes.length>0){
_11a=true;
for(var i=0;i<_118.layoutFragmentIndexes.length;i++){
var _11c=_118.fragments[_118.layoutFragmentIndexes[i]];
if(_11c.otherFragmentIndexes!=null&&_11c.otherFragmentIndexes.length>0){
for(var i=0;i<_11c.otherFragmentIndexes.length;i++){
var _11d=_11c.fragments[_11c.otherFragmentIndexes[i]];
_11d.properties[jetspeed.id.PORTLET_PROP_COLUMN]=-1;
_11d.properties[jetspeed.id.PORTLET_PROP_ROW]=-1;
_11d.documentOrderIndex=_119.fragments.length;
_119.fragments.push(_11d);
_119.otherFragIndexes.push(_119.fragments.length);
}
}
this._layoutFragmentChildCollapse(_11c,_119);
}
}
return _11a;
},_layoutRegisterAndCreateColumnsModel:function(_11e,_11f,_120){
this.layouts[_11e.id]=_11e;
var _121=false;
var _122=new Array();
if(jetspeed.prefs.windowTiling&&_11e.columnSizes.length>0){
var _123=false;
if(jetspeed.browser_IE){
_123=true;
}
if(_11f!=null&&!_120){
var _124=new jetspeed.om.Column(0,_11e.id,(_123?_11e.columnSizesSum-0.1:_11e.columnSizesSum),this.columns.length,_11e.layoutActionsDisabled);
_124.layoutHeader=true;
this.columns.push(_124);
if(_11f.columnChildren==null){
_11f.columnChildren=new Array();
}
_11f.columnChildren.push(_124);
_11f=_124;
_121=true;
}
for(var i=0;i<_11e.columnSizes.length;i++){
var size=_11e.columnSizes[i];
if(_123&&i==(_11e.columnSizes.length-1)){
size=size-0.1;
}
var _127=new jetspeed.om.Column(i,_11e.id,size,this.columns.length,_11e.layoutActionsDisabled);
this.columns.push(_127);
if(_11f!=null){
if(_11f.columnChildren==null){
_11f.columnChildren=new Array();
}
_11f.columnChildren.push(_127);
}
_122.push(_127);
}
}
return {columnsInLayout:_122,addedLayoutHeaderColumn:_121};
},_layoutCreatePortletsModel:function(_128,_129,_12a,_12b){
if(_128.otherFragmentIndexes!=null&&_128.otherFragmentIndexes.length>0){
var _12c=new Array();
for(var i=0;i<_129.length;i++){
_12c.push(new Array());
}
for(var i=0;i<_128.otherFragmentIndexes.length;i++){
var _12e=_128.fragments[_128.otherFragmentIndexes[i]];
if(jetspeed.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter,_12e.id)){
_12e=null;
}
}
if(_12e!=null){
var _12f="z";
var _130=_12e.properties[jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED];
var _131=jetspeed.prefs.windowTiling;
var _132=jetspeed.prefs.windowHeightExpand;
if(_130!=null&&jetspeed.prefs.windowTiling&&jetspeed.prefs.printModeOnly==null){
var _133=_130.split(jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR);
var _134=null,_135=0,_136=null,_137=null,_138=false;
if(_133!=null&&_133.length>0){
var _139=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
for(var _13a=0;_13a<_133.length;_13a++){
_134=_133[_13a];
_135=((_134!=null)?_134.length:0);
if(_135>0){
var _13b=_134.indexOf(_139);
if(_13b>0&&_13b<(_135-1)){
_136=_134.substring(0,_13b);
_137=_134.substring(_13b+1);
_138=((_137=="true")?true:false);
if(_136==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS){
_131=_138;
}else{
if(_136==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT){
_132=_138;
}
}
}
}
}
}
}else{
if(!jetspeed.prefs.windowTiling){
_131=false;
}
}
_12e.properties[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_131;
_12e.properties[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_132;
if(_131&&jetspeed.prefs.windowTiling){
var _13c=_12e.properties[jetspeed.id.PORTLET_PROP_COLUMN];
if(_13c==null||_13c==""||_13c<0||_13c>=_129.length){
var _13d=-1;
for(var j=0;j<_129.length;j++){
if(_13d==-1||_12c[j].length<_13d){
_13d=_12c[j].length;
_13c=j;
}
}
}
_12c[_13c].push(_12e.id);
var _13f=_12a+new Number(_13c);
_12f=_13f.toString();
}
var _140=new jetspeed.om.Portlet(_12e.name,_12e.id,null,_12e.properties,_12e.actions,_12e.currentActionState,_12e.currentActionMode,_12e.decorator,_12e.layoutActionsDisabled);
_140.initialize();
this.putPortlet(_140);
if(_12b[_12f]==null){
_12b[_12f]=new Array();
}
_12b[_12f].push({portlet:_140,layout:_128.id});
}
}
}
},_portletsInitializeWindowState:function(_141){
var _142={};
this.getPortletCurrentColumnRow(null,false,_142);
for(var _143 in this.portlets){
var _144=this.portlets[_143];
var _145=_142[_144.getId()];
if(_145==null&&_141){
for(var i=0;i<_141.length;i++){
if(_141[i].portlet.getId()==_144.getId()){
_145={layout:_141[i].layout};
break;
}
}
}
if(_145!=null){
_144._initializeWindowState(_145,false);
}else{
dojo.raise("page._portletsInitializeWindowState could not find window state init data for portlet: "+_144.getId());
}
}
},_loadPortletZIndexCompare:function(_147,_148){
var _149=null;
var _14a=null;
var _14b=null;
_149=_147.portlet._getInitialZIndex();
_14a=_148.portlet._getInitialZIndex();
if(_149&&!_14a){
return -1;
}else{
if(_14a&&!_149){
return 1;
}else{
if(_149==_14a){
return 0;
}
}
}
return (_149-_14a);
},_createColumnsStart:function(_14c){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _14d=document.createElement("div");
_14d.id=jetspeed.id.COLUMNS;
_14d.setAttribute("id",jetspeed.id.COLUMNS);
for(var _14e=0;_14e<this.columnsStructure.length;_14e++){
var _14f=this.columnsStructure[_14e];
this._createColumns(_14f,_14d);
}
_14c.appendChild(_14d);
},_createColumns:function(_150,_151){
_150.createColumn();
if(_150.columnChildren!=null&&_150.columnChildren.length>0){
for(var _152=0;_152<_150.columnChildren.length;_152++){
var _153=_150.columnChildren[_152];
this._createColumns(_153,_150.domNode);
}
}
_151.appendChild(_150.domNode);
},_removeColumns:function(_154){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_154){
var _156=jetspeed.ui.getPortletWindowChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_156,function(_157){
_154.appendChild(_157);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _158=dojo.byId(jetspeed.id.COLUMNS);
if(_158){
dojo.dom.removeNode(_158);
}
this.columns=[];
},getPortletCurrentColumnRow:function(_159,_15a,_15b){
if(!this.columns||this.columns.length==0){
return null;
}
var _15c=null;
var _15d=((_159!=null)?true:false);
var _15e=0;
var _15f=null;
var _160=null;
var _161=0;
var _162=false;
for(var _163=0;_163<this.columns.length;_163++){
var _164=this.columns[_163];
var _165=_164.domNode.childNodes;
if(_160==null||_160!=_164.getLayoutId()){
_160=_164.getLayoutId();
_15f=this.layouts[_160];
if(_15f==null){
dojo.raise("getPortletCurrentColumnRow cannot locate layout id: "+_160);
return null;
}
_161=0;
_162=false;
if(_15f.clonedFromRootId==null){
_162=true;
}else{
var _166=this.getColumnFromColumnNode(_164.domNode.parentNode);
if(_166==null){
dojo.raise("getPortletCurrentColumnRow cannot locate parent column for column: "+_164);
return null;
}
_164=_166;
}
}
var _167=null;
for(var _168=0;_168<_165.length;_168++){
var _169=_165[_168];
if(dojo.html.hasClass(_169,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS)||(_15a&&dojo.html.hasClass(_169,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))||(_15d&&dojo.html.hasClass(_169,"desktopColumn"))){
_167=(_167==null?0:_167+1);
if((_167+1)>_161){
_161=(_167+1);
}
if(_159==null||_169==_159){
var _16a={layout:_160,column:_164.getLayoutColumnIndex(),row:_167};
if(!_162){
_16a.layout=_15f.clonedFromRootId;
}
if(_159!=null){
_15c=_16a;
break;
}else{
if(_15b!=null){
var _16b=this.getPortletWindowFromNode(_169);
if(_16b==null){
dojo.raise("getPortletCurrentColumnRow cannot locate PortletWindow for node.");
}else{
var _16c=_16b.portlet;
if(_16c==null){
dojo.raise("getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: "+_16b.widgetId);
}else{
_15b[_16c.getId()]=_16a;
}
}
}
}
}
}
}
if(_15c!=null){
break;
}
}
return _15c;
},_getPortletArrayByZIndex:function(){
var _16d=this.getPortletArray();
if(!_16d){
return _16d;
}
var _16e=[];
for(var i=0;i<_16d.length;i++){
if(!_16d[i].getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_16e.push(_16d[i]);
}
}
_16e.sort(this._portletZIndexCompare);
return _16e;
},_portletZIndexCompare:function(_170,_171){
var _172=null;
var _173=null;
var _174=null;
_174=_170.getLastSavedWindowState();
_172=_174.zIndex;
_174=_171.getLastSavedWindowState();
_173=_174.zIndex;
if(_172&&!_173){
return -1;
}else{
if(_173&&!_172){
return 1;
}else{
if(_172==_173){
return 0;
}
}
}
return (_172-_173);
},getPortletDecorationDefault:function(){
var pd=null;
if(djConfig.isDebug&&jetspeed.debug.windowDecorationRandom){
pd=jetspeed.prefs.portletDecorationsAllowed[Math.floor(Math.random()*jetspeed.prefs.portletDecorationsAllowed.length)];
}else{
var _176=this.getPortletDecorator();
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_176)!=-1){
pd=_176;
}else{
pd=jetspeed.prefs.windowDecoration;
}
}
return pd;
},getPortletArrayList:function(){
var _177=new dojo.collections.ArrayList();
for(var _178 in this.portlets){
var _179=this.portlets[_178];
_177.add(_179);
}
return _177;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _17a=[];
for(var _17b in this.portlets){
var _17c=this.portlets[_17b];
_17a.push(_17c);
}
return _17a;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _17d=[];
for(var _17e in this.portlets){
var _17f=this.portlets[_17e];
_17d.push(_17f.getId());
}
return _17d;
},getPortletByName:function(_180){
if(this.portlets&&_180){
for(var _181 in this.portlets){
var _182=this.portlets[_181];
if(_182.name==_180){
return _182;
}
}
}
return null;
},getPortlet:function(_183){
if(this.portlets&&_183){
return this.portlets[_183];
}
return null;
},getPortletWindowFromNode:function(_184){
var _185=null;
if(this.portlets&&_184){
for(var _186 in this.portlets){
var _187=this.portlets[_186];
var _188=_187.getPortletWindow();
if(_188!=null){
if(_188.domNode==_184){
_185=_188;
break;
}
}
}
}
return _185;
},putPortlet:function(_189){
if(!_189){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_189.entityId]=_189;
},removePortlet:function(_18a){
if(!_18a||!this.portlets){
return;
}
delete this.portlets[_18a.entityId];
},_destroyPortlets:function(){
for(var _18b in this.portlets){
var _18c=this.portlets[_18b];
_18c._destroy();
}
},debugLayoutInfo:function(){
var _18d="";
var i=0;
for(var _18f in this.layouts){
if(i>0){
_18d+="\r\n";
}
_18d+="layout["+_18f+"]: "+jetspeed.printobj(this.layouts[_18f],true,true,true);
i++;
}
return _18d;
},debugColumnInfo:function(){
var _190="";
for(var i=0;i<this.columns.length;i++){
if(i>0){
_190+="\r\n";
}
_190+=this.columns[i].toString();
}
return _190;
},debugDumpLastSavedWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(true);
},debugDumpWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(false);
},debugPortletActions:function(){
var _192=this.getPortletArray();
var _193="";
for(var i=0;i<_192.length;i++){
var _195=_192[i];
if(i>0){
_193+="\r\n";
}
_193+="portlet ["+_195.name+"] actions: {";
for(var _196 in _195.actions){
_193+=_196+"={"+jetspeed.printobj(_195.actions[_196],true)+"} ";
}
_193+="}";
}
return _193;
},_debugDumpLastSavedWindowStateAllPortlets:function(_197){
var _198=this.getPortletArray();
var _199="";
for(var i=0;i<_198.length;i++){
var _19b=_198[i];
if(i>0){
_199+="\r\n";
}
var _19c=null;
try{
if(_197){
_19c=_19b.getLastSavedWindowState();
}else{
_19c=_19b.getCurrentWindowState();
}
}
catch(e){
}
_199+="["+_19b.name+"] "+((_19c==null)?"null":jetspeed.printobj(_19c,true));
}
return _199;
},resetWindowLayout:function(){
for(var _19d in this.portlets){
var _19e=this.portlets[_19d];
_19e.submitChangedWindowState(false,true);
}
this.reload();
},reload:function(){
this._removeColumns(document.getElementById(jetspeed.id.DESKTOP));
jetspeed.loadPage();
},getColumnFromColumnNode:function(_19f){
if(_19f==null){
return null;
}
var _1a0=_19f.getAttribute("columnIndex");
if(_1a0==null){
return null;
}
var _1a1=new Number(_1a0);
if(_1a1>=0&&_1a1<this.columns.length){
return this.columns[_1a1];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1a3=null;
if(!this.columns){
return _1a3;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1a3=i;
break;
}
}
return _1a3;
},getColumnContainingNode:function(node){
var _1a6=this.getColumnIndexContainingNode(node);
return ((_1a6!=null&&_1a6>=0)?this.columns[_1a6]:null);
},getDescendantColumns:function(_1a7){
var dMap={};
if(_1a7==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1a7&&_1a7.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1ab,_1ac,_1ad){
var _1ae=new jetspeed.om.Portlet(_1ab,_1ac);
if(_1ad){
_1ae.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1ad);
}
_1ae.initialize();
this.putPortlet(_1ae);
_1ae.retrieveContent();
},removePortletFromPage:function(_1af){
var _1b0=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1b1="?action=remove&id="+escape(portletDef.getPortletName());
var _1b2=jetspeed.page.getPsmlUrl()+_1b1;
var _1b3="text/xml";
var _1b4=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1b2,mimetype:_1b3},_1b0,_1b4,jetspeed.debugContentDumpIds);
},putMenu:function(_1b5){
if(!_1b5){
return;
}
var _1b6=(_1b5.getName?_1b5.getName():null);
if(_1b6!=null){
this.menus[_1b6]=_1b5;
}
},getMenu:function(_1b7){
if(_1b7==null){
return null;
}
return this.menus[_1b7];
},removeMenu:function(_1b8){
if(_1b8==null){
return;
}
var _1b9=null;
if(dojo.lang.isString(_1b8)){
_1b9=_1b8;
}else{
_1b9=(_1b8.getName?_1b8.getName():null);
}
if(_1b9!=null){
delete this.menus[_1b9];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1ba=[];
for(var _1bb in this.menus){
_1ba.push(_1bb);
}
return _1ba;
},retrieveAllMenus:function(){
this.retrieveMenuDeclarations(true);
},retrieveMenuDeclarations:function(_1bc){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1bc);
this.clearMenus();
var _1bd="?action=getmenus";
if(_1bc){
_1bd+="&includeMenuDefs=true";
}
var _1be=this.getPsmlUrl()+_1bd;
var _1bf="text/xml";
var _1c0=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1be,mimetype:_1bf},contentListener,_1c0,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1c1,_1c2,_1c3){
if(_1c3==null){
_1c3=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1c4="?action=getmenu&name="+_1c1;
var _1c5=this.getPsmlUrl()+_1c4;
var _1c6="text/xml";
var _1c7=new jetspeed.om.Id("getmenu-"+_1c1,{page:this,menuName:_1c1,menuType:_1c2});
jetspeed.url.retrieveContent({url:_1c5,mimetype:_1c6},_1c3,_1c7,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1c8 in this.actionButtons){
var _1c9=false;
if(_1c8==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1c9=true;
}
}else{
if(_1c8==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1c9=true;
}
}else{
if(_1c8==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1c9=true;
}
}else{
_1c9=true;
}
}
}
if(_1c9){
this.actionButtons[_1c8].style.display="";
}else{
this.actionButtons[_1c8].style.display="none";
}
}
},renderPageControls:function(){
var _1ca=[];
if(this.actions!=null){
for(var _1cb in this.actions){
if(_1cb!=jetspeed.id.ACTION_NAME_HELP){
_1ca.push(_1cb);
}
if(_1cb==jetspeed.id.ACTION_NAME_EDIT){
_1ca.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1ca.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1ca.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1cc=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1cc!=null&&_1ca!=null&&_1ca.length>0){
if(this.actionButtons==null){
this.actionButtons={};
}
for(var i=0;i<_1ca.length;i++){
var _1cb=_1ca[i];
var _1ce=document.createElement("div");
_1ce.className="portalPageActionButton";
_1ce.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1cb+".gif)";
_1ce.actionName=_1cb;
this.actionButtons[_1cb]=_1ce;
_1cc.appendChild(_1ce);
dojo.event.connect(_1ce,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1cf=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1cf=jetspeed.prefs.desktopActionLabels[_1cb];
}
if(_1cf==null||_1cf.length==0){
_1cf=dojo.string.capitalize(_1cb);
}
var _1d0=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1cf,connectId:_1ce,delay:"100"});
document.body.appendChild(_1d0.domNode);
}
}
}
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_1d2){
if(_1d2==null){
return;
}
if(_1d2==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1d2==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1d2==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1d3=this.getPageAction(_1d2);
alert("pageAction "+_1d2+" : "+_1d3);
if(_1d3==null){
return;
}
if(_1d3.url==null){
return;
}
var _1d4=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1d3.url;
jetspeed.pageNavigate(_1d4);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1d6,_1d7){
if(!_1d7){
_1d7=escape(this.getPagePathAndQuery());
}else{
_1d7=escape(_1d7);
}
var _1d8=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1d7;
if(_1d6!=null){
_1d8+="&jslayoutid="+escape(_1d6);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1d8));
},setPageModePortletActions:function(_1d9){
if(_1d9==null||_1d9.actions==null){
return;
}
if(_1d9.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1d9.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1da){
if(this.pageUrl!=null&&!_1da){
return this.pageUrl;
}
var _1db=jetspeed.url.path.SERVER+((_1da)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1dc=jetspeed.url.parse(_1db);
var _1dd=jetspeed.url.parse(document.location.href);
if(_1dc!=null&&_1dd!=null){
var _1de=_1dd.query;
if(_1de!=null&&_1de.length>0){
var _1df=_1dc.query;
if(_1df!=null&&_1df.length>0){
_1db=_1db+"&"+_1de;
}else{
_1db=_1db+"?"+_1de;
}
}
}
if(!_1da){
this.pageUrl=_1db;
}
return _1db;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1e0=this.getPath();
var _1e1=jetspeed.url.parse(_1e0);
var _1e2=jetspeed.url.parse(document.location.href);
if(_1e1!=null&&_1e2!=null){
var _1e3=_1e2.query;
if(_1e3!=null&&_1e3.length>0){
var _1e4=_1e1.query;
if(_1e4!=null&&_1e4.length>0){
_1e0=_1e0+"&"+_1e3;
}else{
_1e0=_1e0+"?"+_1e3;
}
}
}
this.pagePathAndQuery=_1e0;
return _1e0;
},getPageDirectory:function(_1e5){
var _1e6="/";
var _1e7=(_1e5?this.getRealPath():this.getPath());
if(_1e7!=null){
var _1e8=_1e7.lastIndexOf("/");
if(_1e8!=-1){
if((_1e8+1)<_1e7.length){
_1e6=_1e7.substring(0,_1e8+1);
}else{
_1e6=_1e7;
}
}
}
return _1e6;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_1ea){
if(!_1ea){
_1ea="";
}
if(!jetspeed.url.validateUrlStartsWithHttp(_1ea)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_1ea;
}
return _1ea;
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
jetspeed.om.Column=function(_1eb,_1ec,size,_1ee,_1ef){
this.layoutColumnIndex=_1eb;
this.layoutId=_1ec;
this.size=size;
this.pageColumnIndex=new Number(_1ee);
if(typeof _1ef!="undefined"){
this.layoutActionsDisabled=_1ef;
}
this.id="jscol_"+_1ee;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_1f0){
var _1f1="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_1f1="desktopColumn desktopColumnClear";
}
var _1f2=document.createElement("div");
_1f2.setAttribute("columnIndex",this.getPageColumnIndex());
_1f2.style.width=this.size+"%";
if(this.layoutHeader){
_1f1="desktopColumn desktopLayoutHeader";
}else{
_1f2.style.minHeight="40px";
}
_1f2.className=_1f1;
_1f2.id=this.getId();
this.domNode=_1f2;
if(_1f0!=null){
_1f0.appendChild(_1f2);
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
var _1f6=dojo.html.getAbsolutePosition(this.domNode,true);
var _1f7=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_1f6.x)+", right:"+(_1f6.x+_1f7.width)+", top:"+(_1f6.y)+", bottom:"+(_1f6.y+_1f7.height)+"}";
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
jetspeed.om.Portlet=function(_1f8,_1f9,_1fa,_1fb,_1fc,_1fd,_1fe,_1ff,_200){
this.name=_1f8;
this.entityId=_1f9;
if(_1fb){
this.properties=_1fb;
}else{
this.properties={};
}
if(_1fc){
this.actions=_1fc;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_1fd;
this.currentActionMode=_1fe;
if(_1fa){
this.contentRetriever=_1fa;
}
if(_1ff!=null&&_1ff.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_1ff)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_1ff);
}
}
this.layoutActionsDisabled=false;
if(typeof _200!="undefined"){
this.layoutActionsDisabled=_200;
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
var _201=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_201=="true"){
_201=true;
}else{
if(_201=="false"){
_201=false;
}else{
if(_201!=true&&_201!=false){
_201=true;
}
}
}
}else{
_201=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_201);
var _202=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_202=="true"){
_202=true;
}else{
if(_201=="false"){
_202=false;
}else{
if(_202!=true&&_202!=false){
_202=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_202);
var _203=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_203&&this.name){
var re=(/^[^:]*:*/);
_203=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_203);
}
},postParseAnnotateHtml:function(_205){
if(_205){
var _206=_205;
var _207=_206.getElementsByTagName("form");
var _208=jetspeed.debug.postParseAnnotateHtml;
var _209=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_207){
for(var i=0;i<_207.length;i++){
var _20b=_207[i];
var _20c=_20b.action;
var _20d=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_20c);
var _20e=_20d.operation;
if(_20e==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_20e==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _20f=jetspeed.portleturl.generateJSPseudoUrlActionRender(_20d,true);
_20b.action=_20f;
var _210=new jetspeed.om.ActionRenderFormBind(_20b,_20d.url,_20d.portletEntityId,_20e);
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_20e+") for form with action: "+_20c);
}
}else{
if(_20c==null||_20c.length==0){
var _210=new jetspeed.om.ActionRenderFormBind(_20b,null,this.entityId,null);
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_20c);
}
}
}
}
}
var _211=_206.getElementsByTagName("a");
if(_211){
for(var i=0;i<_211.length;i++){
var _212=_211[i];
var _213=_212.href;
var _20d=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_213);
var _214=null;
if(!_209){
_214=jetspeed.portleturl.generateJSPseudoUrlActionRender(_20d);
}
if(!_214){
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_213);
}
}else{
if(_214==_213){
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_213);
}
}else{
if(_208){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_213+" with: "+_214);
}
_212.href=_214;
}
}
}
}
}
},getPortletWindow:function(){
var _215=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_215){
return dojo.widget.byId(_215);
}
return null;
},getCurrentWindowState:function(_216){
var _217=this.getPortletWindow();
if(!_217){
return null;
}
var _218=_217.getCurrentWindowStateForPersistence(_216);
if(!_216){
if(_218.layout==null){
_218.layout=this.lastSavedWindowState.layout;
}
}
return _218;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_219,_21a){
if(!_219){
_219={};
}
var _21b=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _21c=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_219[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_21b;
_219[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_21c;
var _21d=this.getProperty("width");
if(!_21a&&_21d!=null&&_21d>0){
_219.width=Math.floor(_21d);
}else{
if(_21a){
_219.width=-1;
}
}
var _21e=this.getProperty("height");
if(!_21a&&_21e!=null&&_21e>0){
_219.height=Math.floor(_21e);
}else{
if(_21a){
_219.height=-1;
}
}
if(!_21b||!jetspeed.prefs.windowTiling){
var _21f=this.getProperty("x");
if(!_21a&&_21f!=null&&_21f>=0){
_219.left=Math.floor(((_21f>0)?_21f:0));
}else{
if(_21a){
_219.left=-1;
}
}
var _220=this.getProperty("y");
if(!_21a&&_220!=null&&_220>=0){
_219.top=Math.floor(((_220>0)?_220:0));
}else{
_219.top=-1;
}
var _221=this._getInitialZIndex(_21a);
if(_221!=null){
_219.zIndex=_221;
}
}
return _219;
},_initializeWindowState:function(_222,_223){
var _224=(_222?_222:{});
this.getInitialWindowDimensions(_224,_223);
if(jetspeed.debug.initializeWindowState){
var _225=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_225||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_224.zIndex+" x="+_224.left+" y="+_224.top+" width="+_224.width+" height="+_224.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_224.column+" row="+_224.row+" width="+_224.width+" height="+_224.height);
}
}
this.lastSavedWindowState=_224;
return _224;
},_getInitialZIndex:function(_226){
var _227=null;
var _228=this.getProperty("z");
if(!_226&&_228!=null&&_228>=0){
_227=Math.floor(_228);
}else{
if(_226){
_227=-1;
}
}
return _227;
},_getChangedWindowState:function(_229){
var _22a=this.getLastSavedWindowState();
if(_22a&&dojo.lang.isEmpty(_22a)){
_22a=null;
_229=false;
}
var _22b=this.getCurrentWindowState(_229);
var _22c=_22b[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _22d=!_22c;
if(!_22a){
var _22e={state:_22b,positionChanged:true,extendedPropChanged:true};
if(_22d){
_22e.zIndexChanged=true;
}
return _22e;
}
var _22f=false;
var _230=false;
var _231=false;
var _232=false;
for(var _233 in _22b){
if(_22b[_233]!=_22a[_233]){
if(_233==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_233==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_22f=true;
_231=true;
_230=true;
}else{
if(_233=="zIndex"){
if(_22d){
_22f=true;
_232=true;
}
}else{
_22f=true;
_230=true;
}
}
}
}
if(_22f){
var _22e={state:_22b,positionChanged:_230,extendedPropChanged:_231};
if(_22d){
_22e.zIndexChanged=_232;
}
return _22e;
}
return null;
},createPortletWindow:function(_234){
jetspeed.ui.createPortletWindow(this,_234);
},getPortletUrl:function(_235){
var _236=null;
if(_235&&_235.url){
_236=_235.url;
}else{
if(_235&&_235.formNode){
var _237=_235.formNode.getAttribute("action");
if(_237){
_236=_237;
}
}
}
if(_236==null){
_236=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_235.dontAddQueryArgs){
_236=jetspeed.url.parse(_236);
_236=jetspeed.url.addQueryParameter(_236,"entity",this.entityId,true);
_236=jetspeed.url.addQueryParameter(_236,"portlet",this.name,true);
_236=jetspeed.url.addQueryParameter(_236,"encoder","desktop",true);
if(_235.jsPageUrl!=null){
var _238=_235.jsPageUrl.query;
if(_238!=null&&_238.length>0){
_236=_236.toString()+"&"+_238;
}
}
}
if(_235){
_235.url=_236.toString();
}
return _236;
},_submitJetspeedAjaxApi:function(_239,_23a,_23b){
var _23c="?action="+_239+"&id="+this.entityId+_23a;
var _23d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_23c;
var _23e="text/xml";
var _23f=new jetspeed.om.Id(_239,this.entityId);
_23f.portlet=this;
jetspeed.url.retrieveContent({url:_23d,mimetype:_23e},_23b,_23f,null);
},submitChangedWindowState:function(_240,_241){
var _242=null;
if(_241){
_242={state:this._initializeWindowState(null,true)};
}else{
_242=this._getChangedWindowState(_240);
}
if(_242){
var _243=_242.state;
var _244=_243[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _245=_243[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _246=null;
if(_242.extendedPropChanged){
var _247=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _248=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_246=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_247+_244.toString();
_246+=_248+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_247+_245.toString();
_246=escape(_246);
}
var _249="";
var _24a=null;
if(_244){
_24a="moveabs";
if(_243.column!=null){
_249+="&col="+_243.column;
}
if(_243.row!=null){
_249+="&row="+_243.row;
}
if(_243.layout!=null){
_249+="&layoutid="+_243.layout;
}
if(_243.height!=null){
_249+="&height="+_243.height;
}
}else{
_24a="move";
if(_243.zIndex!=null){
_249+="&z="+_243.zIndex;
}
if(_243.width!=null){
_249+="&width="+_243.width;
}
if(_243.height!=null){
_249+="&height="+_243.height;
}
if(_243.left!=null){
_249+="&x="+_243.left;
}
if(_243.top!=null){
_249+="&y="+_243.top;
}
}
if(_246!=null){
_249+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_246;
}
this._submitJetspeedAjaxApi(_24a,_249,new jetspeed.om.MoveAjaxApiContentListener(this,_243));
if(!_240&&!_241){
if(!_244&&_242.zIndexChanged){
var _24b=jetspeed.page.getPortletArrayList();
var _24c=dojo.collections.Set.difference(_24b,[this]);
if(!_24b||!_24c||((_24c.count+1)!=_24b.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_24c&&_24c.count>0){
dojo.lang.forEach(_24c.toArray(),function(_24d){
if(!_24d.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_24d.submitChangedWindowState(true);
}
});
}
}
}else{
if(_244){
}
}
}
}
},retrieveContent:function(_24e,_24f,_250){
if(_24e==null){
_24e=new jetspeed.om.PortletContentListener(this,_250,_24f);
}
if(!_24f){
_24f={};
}
var _251=this;
_251.getPortletUrl(_24f);
this.contentRetriever.getContent(_24f,_24e,_251,jetspeed.debugContentDumpIds);
},setPortletContent:function(_252,_253,_254){
var _255=this.getPortletWindow();
if(_254!=null&&_254.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_254);
if(_255&&!this.loadingIndicatorIsShown()){
_255.setPortletTitle(_254);
}
}
if(_255){
_255.setPortletContent(_252,_253);
}
},loadingIndicatorIsShown:function(){
var _256=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _257=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _258=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _259=this.getPortletWindow();
if(_259&&(_256||_257)){
var _25a=_259.getPortletTitle();
if(_25a&&(_25a==_256||_25a==_257)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_25b){
var _25c=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_25c=jetspeed.prefs.desktopActionLabels[_25b];
if(_25c!=null&&_25c.length==0){
_25c=null;
}
}
return _25c;
},loadingIndicatorShow:function(_25d){
if(_25d&&!this.loadingIndicatorIsShown()){
var _25e=this._getLoadingActionLabel(_25d);
var _25f=this.getPortletWindow();
if(_25f&&_25e){
_25f.setPortletTitle(_25e);
}
}
},loadingIndicatorHide:function(){
var _260=this.getPortletWindow();
if(_260){
_260.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_262){
this.properties[name]=_262;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_265,_266){
var _267=null;
if(_265!=null){
_267=this.getAction(_265);
}
var _268=_266;
if(_268==null&&_267!=null){
_268=_267.url;
}
if(_268==null){
return;
}
var _269=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_268+jetspeed.page.getPath();
if(_265!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_269});
}else{
var _26a=jetspeed.page.getPageUrl();
_26a=jetspeed.url.addQueryParameter(_26a,"jsprintmode","true");
_26a=jetspeed.url.addQueryParameter(_26a,"jsaction",escape(_267.url));
_26a=jetspeed.url.addQueryParameter(_26a,"jsentity",this.entityId);
_26a=jetspeed.url.addQueryParameter(_26a,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_26a.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_26c,_26d,_26e){
if(_26c){
this.actions=_26c;
}else{
this.actions={};
}
this.currentActionState=_26d;
this.currentActionMode=_26e;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _26f=this.getPortletWindow();
if(_26f){
_26f.windowActionButtonSync();
}
},_destroy:function(){
var _270=this.getPortletWindow();
if(_270){
_270.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_273,_274){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_273;
this.submitOperation=_274;
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
var _279=form.getElementsByTagName("input");
for(var i=0;i<_279.length;i++){
var _27a=_279[i];
if(_27a.type.toLowerCase()=="image"&&_27a.form==form){
this.connect(_27a,"onclick","click");
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
},onSubmit:function(_27c){
var _27d=true;
if(this.isFormSubmitInProgress()){
_27d=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_27d=false;
}
}
}
return _27d;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _27f=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _280={};
if(_27f.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_27f.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _281=jetspeed.portleturl.generateJSPseudoUrlActionRender(_27f,true);
this.form.action=_281;
this.submitOperation=_27f.operation;
this.entityId=_27f.portletEntityId;
_280.url=_27f.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_280.formFilter=dojo.lang.hitch(this,"formFilter");
_280.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_280),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_280),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_282){
if(_282!=undefined){
this.formSubmitInProgress=_282;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_283,_284){
this.folderName=_283;
this.folderPath=_284;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_285,_286,_287,_288,_289){
this.portletName=_285;
this.portletDisplayName=_286;
this.portletDescription=_287;
this.image=_288;
this.count=_289;
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
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_28a,_28b,_28c){
var _28d=_28c.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_28d){
var _28e=dojo.widget.byId(_28d);
if(_28e){
_28e.setPortletContent(_28a,_28b);
}
}
},notifyFailure:function(type,_290,_291,_292){
dojo.raise("BasicContentListener notifyFailure url: "+_291+" type: "+type+jetspeed.url.formatBindError(_290));
}};
jetspeed.om.PortletContentListener=function(_293,_294,_295){
this.portlet=_293;
this.suppressGetActions=_294;
this.submittedFormBindObject=null;
if(_295!=null&&_295.submitFormBindObject!=null){
this.submittedFormBindObject=_295.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_296){
if(this.portlet==null){
return;
}
if(_296){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_297,_298,_299,http){
var _29b=null;
if(http!=null){
_29b=http.getResponseHeader("JS_PORTLET_TITLE");
if(_29b!=null){
_29b=unescape(_29b);
}
}
_299.setPortletContent(_297,_298,_29b);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_299.getId());
}else{
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_29d,_29e,_29f){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_29e+" type: "+type+jetspeed.url.formatBindError(_29d));
}};
jetspeed.om.PortletActionContentListener=function(_2a0,_2a1){
this.portlet=_2a0;
this.submittedFormBindObject=null;
if(_2a1!=null&&_2a1.submitFormBindObject!=null){
this.submittedFormBindObject=_2a1.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2a2){
if(this.portlet==null){
return;
}
if(_2a2){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2a3,_2a4,_2a5,http){
var _2a7=null;
var _2a8=false;
var _2a9=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2a3);
if(_2a9.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2a9.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2a9.operation+"-url in response body: "+_2a3+"  url: "+_2a9.url+" entity-id: "+_2a9.portletEntityId);
}
_2a7=_2a9.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2a3);
}
_2a7=_2a3;
if(_2a7){
var _2aa=_2a7.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2aa==-1){
_2a8=true;
window.location.href=_2a7;
_2a7=null;
}else{
if(_2aa>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2a3);
_2a7=null;
}
}
}
}
if(_2a7!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2a7);
}
jetspeed.doRenderAll(_2a7);
}else{
this._setPortletLoading(false);
}
if(!_2a8&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2ac,_2ad,_2ae){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2ac));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2af=this.getUrl();
if(_2af){
jetspeed.pageNavigate(_2af,this.getTarget());
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
jetspeed.om.Menu=function(_2b0,_2b1){
this._is_parsed=false;
this.name=_2b0;
this.type=_2b1;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2b2){
if(!_2b2){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2b2);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2b4){
if(!this.hasOptions()){
return null;
}
if(_2b4==0||_2b4>0){
if(_2b4>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2b4];
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
var _2b6=this.options[i];
if(_2b6 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2b8,_2b9){
var _2ba=this.parseMenu(data,_2b9.menuName,_2b9.menuType);
_2b9.page.putMenu(_2ba);
},notifyFailure:function(type,_2bc,_2bd,_2be){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2be.toString()+"] url: "+_2bd+" type: "+type+jetspeed.url.formatBindError(_2bc));
},parseMenu:function(node,_2c0,_2c1){
var menu=null;
var _2c3=node.getElementsByTagName("js");
if(!_2c3||_2c3.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2c4=_2c3[0].childNodes;
for(var i=0;i<_2c4.length;i++){
var _2c6=_2c4[i];
if(_2c6.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2c7=_2c6.nodeName;
if(_2c7=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2c6,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2c0;
}
if(menu.type==null){
menu.type=_2c1;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2ca=null;
var _2cb=node.childNodes;
for(var i=0;i<_2cb.length;i++){
var _2cd=_2cb[i];
if(_2cd.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2ce=_2cd.nodeName;
if(_2ce=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2cd,new jetspeed.om.Menu()));
}
}else{
if(_2ce=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2cd,new jetspeed.om.MenuOption()));
}
}else{
if(_2ce=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2cd,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2ce){
mObj[_2ce]=((_2cd&&_2cd.firstChild)?_2cd.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2cf){
this.includeMenuDefs=_2cf;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2d1,_2d2){
var _2d3=this.getMenuDefs(data,_2d1,_2d2);
for(var i=0;i<_2d3.length;i++){
var mObj=_2d3[i];
_2d2.page.putMenu(mObj);
}
this.notifyFinished(_2d2);
},getMenuDefs:function(data,_2d7,_2d8){
var _2d9=[];
var _2da=data.getElementsByTagName("menu");
for(var i=0;i<_2da.length;i++){
var _2dc=_2da[i].getAttribute("type");
if(this.includeMenuDefs){
_2d9.push(this.parseMenuObject(_2da[i],new jetspeed.om.Menu(null,_2dc)));
}else{
var _2dd=_2da[i].firstChild.nodeValue;
_2d9.push(new jetspeed.om.Menu(_2dd,_2dc));
}
}
return _2d9;
},notifyFailure:function(type,_2df,_2e0,_2e1){
dojo.raise("MenusAjaxApiContentListener error ["+_2e1.toString()+"] url: "+_2e0+" type: "+type+jetspeed.url.formatBindError(_2df));
},notifyFinished:function(_2e2){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_2e3){
this.portletEntityId=_2e3;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_2e5,_2e6){
if(jetspeed.url.checkAjaxApiResponse(_2e5,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_2e7){
var _2e8=jetspeed.page.getPortlet(this.portletEntityId);
if(_2e8){
if(_2e7){
_2e8.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_2e8.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_2ea,_2eb,_2ec){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_2ec.toString()+"] url: "+_2eb+" type: "+type+jetspeed.url.formatBindError(_2ea));
}});
jetspeed.om.PageChangeActionContentListener=function(_2ed){
this.pageActionUrl=_2ed;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_2ef,_2f0){
if(jetspeed.url.checkAjaxApiResponse(_2ef,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_2f2,_2f3,_2f4){
dojo.raise("PageChangeActionContentListener error ["+_2f4.toString()+"] url: "+_2f3+" type: "+type+jetspeed.url.formatBindError(_2f2));
}});
jetspeed.om.PortletActionsContentListener=function(_2f5){
this.portletEntityIds=_2f5;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_2f6){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _2f8=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_2f8){
if(_2f6){
_2f8.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_2f8.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_2fa,_2fb){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_2fa,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _2fd=this.parsePortletActionsResponse(node);
for(var i=0;i<_2fd.length;i++){
var _2ff=_2fd[i];
var _300=_2ff.id;
var _301=jetspeed.page.getPortlet(_300);
if(_301!=null){
_301.updateActions(_2ff.actions,_2ff.currentActionState,_2ff.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _303=new Array();
var _304=node.getElementsByTagName("js");
if(!_304||_304.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _303;
}
var _305=_304[0].childNodes;
for(var i=0;i<_305.length;i++){
var _307=_305[i];
if(_307.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _308=_307.nodeName;
if(_308=="portlets"){
var _309=_307;
var _30a=_309.childNodes;
for(var pI=0;pI<_30a.length;pI++){
var _30c=_30a[pI];
if(_30c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _30d=_30c.nodeName;
if(_30d=="portlet"){
var _30e=this.parsePortletElement(_30c);
if(_30e!=null){
_303.push(_30e);
}
}
}
}
}
return _303;
},parsePortletElement:function(node){
var _310=node.getAttribute("id");
if(_310!=null){
var _311=jetspeed.page._parsePSMLActions(node,null);
var _312=jetspeed.page._parsePSMLCurrentActionState(node);
var _313=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_310,actions:_311,currentActionState:_312,currentActionMode:_313};
}
return null;
},notifyFailure:function(type,_315,_316,_317){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_317.toString()+"] url: "+_316+" type: "+type+jetspeed.url.formatBindError(_315));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_318,_319,_31a){
this.portletDef=_318;
this.windowWidgetId=_319;
this.addToCurrentPage=_31a;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_31c,_31d){
if(jetspeed.url.checkAjaxApiResponse(_31c,data,true,"add-portlet")){
var _31e=this.parseAddPortletResponse(data);
if(_31e&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_31e,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _320=null;
var _321=node.getElementsByTagName("js");
if(!_321||_321.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _322=_321[0].childNodes;
for(var i=0;i<_322.length;i++){
var _324=_322[i];
if(_324.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _325=_324.nodeName;
if(_325=="entity"){
_320=((_324&&_324.firstChild)?_324.firstChild.nodeValue:null);
break;
}
}
return _320;
},notifyFailure:function(type,_327,_328,_329){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_329.toString()+"] url: "+_328+" type: "+type+jetspeed.url.formatBindError(_327));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_32b,_32c){
var _32d=this.parsePortlets(data);
var _32e=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_32e!=null){
for(var i=0;i<_32d.length;i++){
_32e.addChild(_32d[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_32c,_32d);
}
},notifyFailure:function(type,_331,_332,_333){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_333.toString()+"] url: "+_332+" type: "+type+jetspeed.url.formatBindError(_331));
},parsePortlets:function(node){
var _335=[];
var _336=node.getElementsByTagName("js");
if(!_336||_336.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _337=_336[0].childNodes;
for(var i=0;i<_337.length;i++){
var _339=_337[i];
if(_339.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _33a=_339.nodeName;
if(_33a=="portlets"){
var _33b=_339;
var _33c=_33b.childNodes;
for(var pI=0;pI<_33c.length;pI++){
var _33e=_33c[pI];
if(_33e.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _33f=_33e.nodeName;
if(_33f=="portlet"){
var _340=this.parsePortletElement(_33e);
_335.push(_340);
}
}
}
}
return _335;
},parsePortletElement:function(node){
var _342=node.getAttribute("name");
var _343=node.getAttribute("displayName");
var _344=node.getAttribute("description");
var _345=node.getAttribute("image");
var _346=0;
return new jetspeed.om.PortletDef(_342,_343,_344,_345,_346);
}});
jetspeed.om.FoldersListContentListener=function(_347){
this.notifyFinished=_347;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_349,_34a){
var _34b=this.parseFolders(data);
var _34c=this.parsePages(data);
var _34d=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_34a,_34b,_34c,_34d);
}
},notifyFailure:function(type,_34f,_350,_351){
dojo.raise("FoldersListContentListener error ["+_351.toString()+"] url: "+_350+" type: "+type+jetspeed.url.formatBindError(_34f));
},parseFolders:function(node){
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
if(_358=="folders"){
var _359=_357;
var _35a=_359.childNodes;
for(var pI=0;pI<_35a.length;pI++){
var _35c=_35a[pI];
if(_35c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _35d=_35c.nodeName;
if(_35d=="folder"){
var _35e=this.parsePortletElement(_35c);
_353.push(_35e);
}
}
}
}
return _353;
},parsePages:function(node){
var _360=[];
var _361=node.getElementsByTagName("js");
if(!_361||_361.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _362=_361[0].childNodes;
for(var i=0;i<_362.length;i++){
var _364=_362[i];
if(_364.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _365=_364.nodeName;
if(_365=="folders"){
var _366=_364;
var _367=_366.childNodes;
for(var pI=0;pI<_367.length;pI++){
var _369=_367[pI];
if(_369.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _36a=_369.nodeName;
if(_36a=="page"){
var _36b=this.parsePortletElement(_369);
_360.push(_36b);
}
}
}
}
return _360;
},parseLinks:function(node){
var _36d=[];
var _36e=node.getElementsByTagName("js");
if(!_36e||_36e.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _36f=_36e[0].childNodes;
for(var i=0;i<_36f.length;i++){
var _371=_36f[i];
if(_371.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _372=_371.nodeName;
if(_372=="folders"){
var _373=_371;
var _374=_373.childNodes;
for(var pI=0;pI<_374.length;pI++){
var _376=_374[pI];
if(_376.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _377=_376.nodeName;
if(_377=="link"){
var _378=this.parsePortletElement(_376);
_36d.push(_378);
}
}
}
}
return _36d;
},parsePortletElement:function(node){
var _37a=node.getAttribute("name");
var _37b=node.getAttribute("path");
return new jetspeed.om.FolderDef(_37a,_37b);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_37c){
this.notifyFinished=_37c;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_37e,_37f){
var _380=this.parsePortlets(data);
var _381=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_37f,_380,_381);
}
},notifyFailure:function(type,_383,_384,_385){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_385.toString()+"] url: "+_384+" type: "+type+jetspeed.url.formatBindError(_383));
},parsList:function(node){
var _387;
var _388=node.getElementsByTagName("js");
if(!_388||_388.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _389=_388[0].childNodes;
for(var i=0;i<_389.length;i++){
var _38b=_389[i];
if(_38b.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _38c=_38b.nodeName;
if(_38c=="resultCount"){
_387=_38b.textContent;
}
}
return _387;
},parsePortlets:function(node){
var _38e=[];
var _38f=node.getElementsByTagName("js");
if(!_38f||_38f.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _390=_38f[0].childNodes;
for(var i=0;i<_390.length;i++){
var _392=_390[i];
if(_392.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _393=_392.nodeName;
if(_393=="portlets"){
var _394=_392;
var _395=_394.childNodes;
for(var pI=0;pI<_395.length;pI++){
var _397=_395[pI];
if(_397.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _398=_397.nodeName;
if(_398=="portlet"){
var _399=this.parsePortletElement(_397);
_38e.push(_399);
}
}
}
}
return _38e;
},parsePortletElement:function(node){
var _39b=node.getAttribute("name");
var _39c=node.getAttribute("displayName");
var _39d=node.getAttribute("description");
var _39e=node.getAttribute("image");
var _39f=node.getAttribute("count");
return new jetspeed.om.PortletDef(_39b,_39c,_39d,_39e,_39f);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3a0,_3a1){
this.portlet=_3a0;
this.changedState=_3a1;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3a2){
if(this.portlet==null){
return;
}
if(_3a2){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3a4,_3a5){
this._setPortletLoading(false);
dojo.lang.mixin(_3a5.portlet.lastSavedWindowState,this.changedState);
var _3a6=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3a6=true;
}
jetspeed.url.checkAjaxApiResponse(_3a4,data,_3a6,("move-portlet ["+_3a5.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3a8,_3a9,_3aa){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3aa.entityId+"] url: "+_3a9+" type: "+type+jetspeed.url.formatBindError(_3a8));
}};
jetspeed.ui.getPortletWindowChildren=function(_3ab,_3ac,_3ad,_3ae){
if(_3ad||_3ae){
_3ad=true;
}
var _3af=null;
var _3b0=-1;
if(_3ab){
_3af=[];
var _3b1=_3ab.childNodes;
if(_3b1!=null&&_3b1.length>0){
for(var i=0;i<_3b1.length;i++){
var _3b3=_3b1[i];
if((!_3ae&&dojo.html.hasClass(_3b3,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3ad&&dojo.html.hasClass(_3b3,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3af.push(_3b3);
if(_3ac&&_3b3==_3ac){
_3b0=_3af.length-1;
}
}else{
if(_3ac&&_3b3==_3ac){
_3af.push(_3b3);
_3b0=_3af.length-1;
}
}
}
}
}
return {portletWindowNodes:_3af,matchIndex:_3b0};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3b4){
var _3b5=null;
if(_3b4){
_3b5=new Array();
for(var i=0;i<_3b4.length;i++){
var _3b7=dojo.widget.byNode(_3b4[i]);
if(_3b7){
_3b5.push(_3b7);
}
}
}
return _3b5;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3b9=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3b9.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3bb=jetspeed.page.columns[i];
var _3bc=jetspeed.ui.getPortletWindowChildren(_3bb.domNode,null);
var _3bd=jetspeed.ui.getPortletWindowsFromNodes(_3bc.portletWindowNodes);
var _3be={dumpMsg:""};
if(_3bd!=null){
dojo.lang.forEach(_3bd,function(_3bf){
_3be.dumpMsg=_3be.dumpMsg+(_3be.dumpMsg.length>0?", ":"")+_3bf.portlet.entityId;
});
}
_3be.dumpMsg="column "+i+": "+_3be.dumpMsg;
dojo.debug(_3be.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3c0=jetspeed.ui.getAllPortletWindowWidgets();
var _3c1="";
for(var i=0;i<_3c0.length;i++){
if(i>0){
_3c1+=", ";
}
_3c1+=_3c0[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3c1);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3c3=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3c4=jetspeed.ui.getPortletWindowsFromNodes(_3c3.portletWindowNodes);
if(_3c4==null){
_3c4=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3c6=jetspeed.page.columns[i];
var _3c7=jetspeed.ui.getPortletWindowChildren(_3c6.domNode,null);
var _3c8=jetspeed.ui.getPortletWindowsFromNodes(_3c7.portletWindowNodes);
if(_3c8!=null){
_3c4=_3c4.concat(_3c8);
}
}
return _3c4;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3c9,_3ca){
var _3cb=_3c9.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3cb==null){
_3cb=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3cb=false;
}
}
var _3cc=dojo.widget.byId(_3c9.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3cc){
_3cc.resetWindow(_3c9);
}else{
_3cc=jetspeed.ui.createPortletWindowWidget(_3c9);
}
if(_3cc){
if(!_3cb||_3ca>=jetspeed.page.columns.length){
_3cc.domNode.style.position="absolute";
var _3cd=document.getElementById(jetspeed.id.DESKTOP);
_3cd.appendChild(_3cc.domNode);
}else{
var _3ce=null;
var _3cf=-1;
var _3d0=_3ca;
if(_3d0!=null&&_3d0>=0&&_3d0<jetspeed.page.columns.length){
_3cf=_3d0;
_3ce=jetspeed.page.columns[_3cf];
}
if(_3cf==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d2=jetspeed.page.columns[i];
if(!_3d2.domNode.hasChildNodes()){
_3ce=_3d2;
_3cf=i;
break;
}
if(_3ce==null||_3ce.domNode.childNodes.length>_3d2.domNode.childNodes.length){
_3ce=_3d2;
_3cf=i;
}
}
}
if(_3ce){
_3ce.domNode.appendChild(_3cc.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3d3,_3d4){
if(!_3d4){
_3d4={};
}
if(_3d3 instanceof jetspeed.om.Portlet){
_3d4.portlet=_3d3;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3d4,_3d3);
}
var _3d5=dojo.widget.createWidget("jetspeed:PortletWindow",_3d4);
return _3d5;
};
jetspeed.ui.fadeIn=function(_3d6,_3d7,_3d8){
jetspeed.ui.fade(_3d6,_3d7,_3d8,0,1);
};
jetspeed.ui.fadeOut=function(_3d9,_3da,_3db){
jetspeed.ui.fade(_3d9,_3da,"hidden",1,0,_3db);
};
jetspeed.ui.fade=function(_3dc,_3dd,_3de,_3df,_3e0,_3e1){
if(_3dc.length>0){
for(var i=0;i<_3dc.length;i++){
dojo.lfx.html._makeFadeable(_3dc[i]);
if(_3de!="none"){
_3dc[i].style.visibility=_3de;
}
}
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([_3df],[_3e0]),_3dd,0);
dojo.event.connect(anim,"onAnimate",function(e){
for(var mi=0;mi<_3dc.length;mi++){
dojo.html.setOpacity(_3dc[mi],e.x);
}
});
if(_3de=="hidden"){
dojo.event.connect(anim,"onEnd",function(e){
for(var mi=0;mi<_3dc.length;mi++){
_3dc[mi].style.visibility=_3de;
}
if(_3e1){
for(var mi=0;mi<_3e1.length;mi++){
_3e1[mi].style.display="none";
}
}
});
}
anim.play(true);
}
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3e8=jetspeed.debugWindowReadCookie(true);
var _3e9={};
var _3ea=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3e9[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3ea;
_3e9[jetspeed.id.PORTLET_PROP_WIDTH]=_3e8.width;
_3e9[jetspeed.id.PORTLET_PROP_HEIGHT]=_3e8.height;
_3e9[jetspeed.id.PORTLET_PROP_LEFT]=_3e8.left;
_3e9[jetspeed.id.PORTLET_PROP_TOP]=_3e8.top;
_3e9[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3e9[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3e9[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3e8.windowState;
var _3eb=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3e9);
jetspeed.ui.createPortletWindow(_3eb);
_3eb.retrieveContent(null,null);
var _3ec=dojo.widget.byId(_3ea);
var _3ed=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3ec,"contentChanged");
dojo.event.connect(_3ec,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3ec,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3ec,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3ee){
var _3ef={};
if(_3ee){
_3ef={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _3f0=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_3f0!=null&&_3f0.length>0){
var _3f1=_3f0.split("|");
if(_3f1&&_3f1.length>=4){
_3ef.width=_3f1[0];
_3ef.height=_3f1[1];
_3ef.top=_3f1[2];
_3ef.left=_3f1[3];
if(_3f1.length>4&&_3f1[4]!=null&&_3f1[4].length>0){
_3ef.windowState=_3f1[4];
}
}
}
return _3ef;
};
jetspeed.debugWindowRestore=function(){
var _3f2=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3f3=dojo.widget.byId(_3f2);
if(!_3f3){
return;
}
_3f3.restoreWindow();
};
jetspeed.debugWindow=function(){
var _3f4=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_3f4);
};
jetspeed.debugWindowSave=function(){
var _3f5=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3f6=dojo.widget.byId(_3f5);
if(!_3f6){
return null;
}
if(!_3f6.windowPositionStatic){
var _3f7=_3f6.getCurrentWindowStateForPersistence(false);
var _3f8=_3f7.width;
var _3f9=_3f7.height;
var cTop=_3f7.top;
var _3fb=_3f7.left;
if(_3f6.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _3fc=_3f6.getLastPositionInfo();
if(_3fc!=null){
if(_3fc.height!=null&&_3fc.height>0){
_3f9=_3fc.height;
}
}else{
var _3fd=jetspeed.debugWindowReadCookie(false);
if(_3fd.height!=null&&_3fd.height>0){
_3f9=_3fd.height;
}
}
}
var _3fe=_3f8+"|"+_3f9+"|"+cTop+"|"+_3fb+"|"+_3f6.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_3fe,30,"/");
}
};
jetspeed.debugDumpForm=function(_3ff){
if(!_3ff){
return null;
}
var _400=_3ff.toString();
if(_3ff.name){
_400+=" name="+_3ff.name;
}
if(_3ff.id){
_400+=" id="+_3ff.id;
}
var _401=dojo.io.encodeForm(_3ff);
_400+=" data="+_401;
return _400;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_402,_403,_404,_405){
if(!_402){
_402={};
}
if(!this.initialized){
var _406="";
if(jetspeed.altDebugWindowContent){
_406=jetspeed.altDebugWindowContent();
}else{
_406+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_403){
_403=new jetspeed.om.BasicContentListener();
}
_403.notifySuccess(_406,_402.url,_404);
this.initialized=true;
}
}};

