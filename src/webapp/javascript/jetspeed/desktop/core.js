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
var _f=_e.layoutDecorator;
var _10=_e.editMode;
_e.destroy();
var _11=new jetspeed.om.Page(_f,_c,(!djConfig.preventBackButtonFix&&!_d),_10);
jetspeed.page=_11;
_11.retrievePsml();
window.focus();
}
};
jetspeed.doRender=function(_12,_13){
if(!_12){
_12={};
}else{
if((typeof _12=="string"||_12 instanceof String)){
_12={url:_12};
}
}
var _14=jetspeed.page.getPortlet(_13);
if(_14){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_13+"] url: "+_12.url);
}
_14.retrieveContent(null,_12);
}
};
jetspeed.doRenderAll=function(url,_16,_17){
var _18=jetspeed.debug.doRenderDoAction;
var _19=jetspeed.debug.pageLoad&&_17;
if(!_16){
_16=jetspeed.page.getPortletArray();
}
var _1a="";
var _1b=true;
var _1c=null;
if(_17){
_1c=jetspeed.url.parse(jetspeed.page.getPageUrl());
}
for(var i=0;i<_16.length;i++){
var _1e=_16[i];
if((_18||_19)){
if(i>0){
_1a=_1a+", ";
}
var _1f=null;
if(_1e.getProperty!=null){
_1f=_1e.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
}
if(!_1f){
_1f=_1e.widgetId;
}
if(!_1f){
_1f=_1e.toString();
}
if(_1e.entityId){
_1a=_1a+_1e.entityId+"("+_1f+")";
if(_19&&_1e.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE)){
_1a=_1a+" "+_1e.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
}
}else{
_1a=_1a+_1f;
}
}
_1e.retrieveContent(null,{url:url,jsPageUrl:_1c},_1b);
}
if(_18){
dojo.debug("doRenderAll ["+_1a+"] url: "+url);
}else{
if(_19){
dojo.debug("doRenderAll page-url: "+jetspeed.page.getPsmlUrl()+" portlets: ["+_1a+"]"+(url?(" url: "+url):""));
}
}
};
jetspeed.doAction=function(_20,_21){
if(!_20){
_20={};
}else{
if((typeof _20=="string"||_20 instanceof String)){
_20={url:_20};
}
}
var _22=jetspeed.page.getPortlet(_21);
if(_22){
if(jetspeed.debug.doRenderDoAction){
if(!_20.formNode){
dojo.debug("doAction ["+_21+"] url: "+_20.url+" form: null");
}else{
dojo.debug("doAction ["+_21+"] url: "+_20.url+" form: "+jetspeed.debugDumpForm(_20.formNode));
}
}
_22.retrieveContent(new jetspeed.om.PortletActionContentListener(_22,_20),_20);
}
};
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrlForDesktopActionRender:function(_23){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _25=_23;
var _26=null;
if(_23&&_23.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_23.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_23&&_23.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_23.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_26=jetspeed.url.getQueryParameter(_23,"entity");
}
if(!jetspeed.url.validateUrlStartsWithHttp(_25)){
_25=null;
}
return {url:_25,operation:op,portletEntityId:_26};
},generateJSPseudoUrlActionRender:function(_27,_28){
if(!_27||!_27.url||!_27.portletEntityId){
return null;
}
var _29=null;
if(_28){
_29=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_29="javascript:";
var _2a=false;
if(_27.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_29+="doAction(\"";
}else{
if(_27.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_29+="doRender(\"";
}else{
_2a=true;
}
}
if(_2a){
return null;
}
_29+=_27.url+"\",\""+_27.portletEntityId+"\"";
_29+=")";
}
return _29;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_2b){
var _2c=jetspeed.prefs.getPortletDecorationConfig(_2b);
if(_2c!=null&&!_2c._initialized){
var _2d=jetspeed.prefs.getPortletDecorationBaseUrl(_2b);
_2c._initialized=true;
_2c.cssPathCommon=new dojo.uri.Uri(_2d+"/css/styles.css");
_2c.cssPathDesktop=new dojo.uri.Uri(_2d+"/css/desktop.css");
dojo.html.insertCssFile(_2c.cssPathCommon,null,true);
dojo.html.insertCssFile(_2c.cssPathDesktop,null,true);
if(jetspeed.prefs.printModeOnly==null){
_2c.templatePath=_2d+"/templates/PortletWindow.html";
}else{
_2c.templatePath=_2d+"/templates/PortletWindowPrintMode.html";
}
}
return _2c;
};
jetspeed.loadPortletDecorationConfig=function(_2e){
var _2f={};
jetspeed.prefs.portletDecorationsConfig[_2e]=_2f;
_2f.windowActionButtonOrder=jetspeed.prefs.windowActionButtonOrder;
_2f.windowActionNotPortlet=jetspeed.prefs.windowActionNotPortlet;
_2f.windowActionButtonMax=jetspeed.prefs.windowActionButtonMax;
_2f.windowActionButtonHide=jetspeed.prefs.windowActionButtonHide;
_2f.windowActionButtonTooltip=jetspeed.prefs.windowActionButtonTooltip;
_2f.windowActionMenuOrder=jetspeed.prefs.windowActionMenuOrder;
_2f.windowActionNoImage=jetspeed.prefs.windowActionNoImage;
_2f.windowIconEnabled=jetspeed.prefs.windowIconEnabled;
_2f.windowIconPath=jetspeed.prefs.windowIconPath;
var _30=jetspeed.prefs.getPortletDecorationBaseUrl(_2e)+"/"+_2e+".js";
dojo.hostenv.loadUri(_30,function(_31){
for(var j in _31){
_2f[j]=_31[j];
}
if(_2f.windowActionNoImage!=null){
var _33={};
for(var i=0;i<_2f.windowActionNoImage.length;i++){
_33[_2f.windowActionNoImage[i]]=true;
}
_2f.windowActionNoImage=_33;
}
if(_2f.windowIconPath!=null){
_2f.windowIconPath=dojo.string.trim(_2f.windowIconPath);
if(_2f.windowIconPath==null||_2f.windowIconPath.length==0){
_2f.windowIconPath=null;
}else{
var _35=_2f.windowIconPath;
var _36=_35.charAt(0);
if(_36!="/"){
_35="/"+_35;
}
var _37=_35.charAt(_35.length-1);
if(_37!="/"){
_35=_35+"/";
}
_2f.windowIconPath=_35;
}
}
});
};
jetspeed.purifyIdentifier=function(src,_39,_3a){
if(src==null){
return src;
}
var _3b=src.length;
if(_3b==0){
return src;
}
if(_39==null){
_39="_";
}
var _3c=new RegExp("[^a-z_0-9A-Z]","g");
var _3d=src.charCodeAt(0);
var _3e=null;
if((_3d>=65&&_3d<=90)||_3d==95||(_3d>=97&&_3d<=122)){
_3e=src.charAt(0);
}else{
_3e=_39;
}
var _3f=false,_40=false;
if(_3a!=null){
_3a=_3a.toLowerCase();
_3f=(_3a=="hi"?true:false);
_40=(_3a=="lo"?true:false);
}
if(_3b>1){
if(_3f||_40){
upNext=false;
for(var i=1;i<_3b;i++){
_3d=src.charCodeAt(i);
if((_3d>=65&&_3d<=90)||_3d==95||(_3d>=97&&_3d<=122)||(_3d>=48&&_3d<=57)){
if(upNext&&(_3d>=97&&_3d<=122)){
_3e+=String.fromCharCode(_3d-32);
}else{
_3e+=src.charAt(i);
}
upNext=false;
}else{
upNext=true;
_3e+=_39;
}
}
}else{
_3e+=src.substring(1).replace(_3c,_39);
}
}
if(_3f){
_3d=_3e.charCodeAt(0);
if(_3d>=97&&_3d<=122){
_3e=String.fromCharCode(_3d-32)+_3e.substring(1);
}
}
return _3e;
};
jetspeed.notifyRetrieveAllMenusFinished=function(){
jetspeed.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _42=jetspeed.page.getMenuNames();
for(var i=0;i<_42.length;i++){
var _44=_42[i];
var _45=dojo.widget.byId(jetspeed.id.MENU_WIDGET_ID_PREFIX+_44);
if(_45){
_45.createJetspeedMenu(jetspeed.page.getMenu(_44));
}
}
jetspeed.url.loadingIndicatorHide();
jetspeed.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_46){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_46);
}
};
jetspeed.menuNavClickWidget=function(_47,_48){
dojo.debug("jetspeed.menuNavClick");
if(!_47){
return;
}
if(dojo.lang.isString(_47)){
var _49=_47;
_47=dojo.widget.byId(_49);
if(!_47){
dojo.raise("menuNavClick could not find tab widget for "+_49);
}
}
if(_47){
var _4a=_47.jetspeedmenuname;
if(!_4a&&_47.extraArgs){
_4a=_47.extraArgs.jetspeedmenuname;
}
if(!_4a){
dojo.raise("menuNavClick tab widget ["+_47.widgetId+"] does not define jetspeedMenuName");
}
var _4b=jetspeed.page.getMenu(_4a);
if(!_4b){
dojo.raise("menuNavClick Menu lookup for tab widget ["+_47.widgetId+"] failed: "+_4a);
}
var _4c=_4b.getOptionByIndex(_48);
jetspeed.menuNavClick(_4c);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_4d,_4e,_4f){
if(!_4d||jetspeed.pageNavigateSuppress){
return;
}
if(typeof _4f=="undefined"){
_4f=false;
}
if(!_4f&&jetspeed.page&&jetspeed.page.equalsPageUrl(_4d)){
return;
}
_4d=jetspeed.page.makePageUrl(_4d);
if(_4e=="top"){
top.location.href=_4d;
}else{
if(_4e=="parent"){
parent.location.href=_4d;
}else{
window.location.href=_4d;
}
}
};
jetspeed.loadPortletSelector=function(){
var _50={};
_50[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_50[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_50[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.page.getPortletDecorationDefault();
_50[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]=jetspeed.prefs.portletSelectorWindowTitle;
_50[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=jetspeed.prefs.portletSelectorWindowIcon;
_50[jetspeed.id.PORTLET_PROP_WIDGET_ID]=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.SELECTOR;
_50[jetspeed.id.PORTLET_PROP_WIDTH]=jetspeed.prefs.portletSelectorBounds.width;
_50[jetspeed.id.PORTLET_PROP_HEIGHT]=jetspeed.prefs.portletSelectorBounds.height;
_50[jetspeed.id.PORTLET_PROP_LEFT]=jetspeed.prefs.portletSelectorBounds.x;
_50[jetspeed.id.PORTLET_PROP_TOP]=jetspeed.prefs.portletSelectorBounds.y;
_50[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=true;
_50[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.PortletSelectorContentRetriever();
var _51=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_50);
jetspeed.ui.createPortletWindow(_51);
_51.retrieveContent(null,null);
jetspeed.getPortletDefinitions();
};
jetspeed.getPortletDefinitions=function(){
var _52=new jetspeed.om.PortletSelectorAjaxApiContentListener();
var _53="?action=getportlets";
var _54=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_53;
var _55="text/xml";
var _56=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_54,mimetype:_55},_52,_56,jetspeed.debugContentDumpIds);
};
jetspeed.searchForPortletDefinitions=function(_57,_58){
var _59=new jetspeed.om.PortletSelectorSearchContentListener(_58);
var _5a="?action=getportlets&filter="+_57;
var _5b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_5a;
var _5c="text/xml";
var _5d=new jetspeed.om.Id("getportlets",{});
jetspeed.url.retrieveContent({url:_5b,mimetype:_5c},_59,_5d,jetspeed.debugContentDumpIds);
};
jetspeed.getFolders=function(_5e,_5f){
var _60=new jetspeed.om.FoldersListContentListener(_5f);
var _61="?action=getfolders&data="+_5e;
var _62=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_61;
var _63="text/xml";
var _64=new jetspeed.om.Id("getfolders",{});
jetspeed.url.retrieveContent({url:_62,mimetype:_63},_60,_64,jetspeed.debugContentDumpIds);
};
jetspeed.portletDefinitionsforSelector=function(_65,_66,_67,_68,_69){
var _6a=new jetspeed.om.PortletSelectorSearchContentListener(_69);
var _6b="?action=selectorPortlets&category="+_66+"&portletPerPages="+_68+"&pageNumber="+_67+"&filter="+_65;
var _6c=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_6b;
var _6d="text/xml";
var _6e=new jetspeed.om.Id("selectorPortlets",{});
jetspeed.url.retrieveContent({url:_6c,mimetype:_6d},_6a,_6e,jetspeed.debugContentDumpIds);
};
jetspeed.getActionsForPortlet=function(_6f){
if(_6f==null){
return;
}
jetspeed.getActionsForPortlets([_6f]);
};
jetspeed.getActionsForPortlets=function(_70){
if(_70==null){
_70=jetspeed.page.getPortletIds();
}
var _71=new jetspeed.om.PortletActionsContentListener(_70);
var _72="?action=getactions";
for(var i=0;i<_70.length;i++){
_72+="&id="+_70[i];
}
var _74=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_72;
var _75="text/xml";
var _76=new jetspeed.om.Id("getactions",{});
jetspeed.url.retrieveContent({url:_74,mimetype:_75},_71,_76,jetspeed.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_77,_78,_79,_7a){
if(_77==null){
return;
}
if(_7a==null){
_7a=new jetspeed.om.PortletChangeActionContentListener(_77);
}
var _7b="?action=window&id="+(_77!=null?_77:"");
if(_78!=null){
_7b+="&state="+_78;
}
if(_79!=null){
_7b+="&mode="+_79;
}
var _7c=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7b;
var _7d="text/xml";
var _7e=new jetspeed.om.Id("changeaction",{});
jetspeed.url.retrieveContent({url:_7c,mimetype:_7d},_7a,_7e,jetspeed.debugContentDumpIds);
};
jetspeed.addNewPortletDefinition=function(_7f,_80,_81,_82){
var _83=true;
if(_81!=null){
_83=false;
}
var _84=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(_7f,_80,_83);
var _85="?action=add&id="+escape(_7f.getPortletName());
if(_82!=null&&_82.length>0){
_85+="&layoutid="+escape(_82);
}
var _86=null;
if(_81!=null){
_86=_81+_85;
}else{
_86=jetspeed.page.getPsmlUrl()+_85;
}
var _87="text/xml";
var _88=new jetspeed.om.Id("addportlet",{});
jetspeed.url.retrieveContent({url:_86,mimetype:_87},_84,_88,jetspeed.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(){
if(!jetspeed.page.editMode){
var _89=true;
var _8a=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
if(_8a!=null&&_8a=="true"){
_89=false;
}
jetspeed.page.editMode=true;
var _8b=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets(true);
}
if(_8b==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_8b=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PAGE_EDITOR_WIDGET_ID,editorInitiatedFromDesktop:_89});
var _8c=document.getElementById(jetspeed.id.COLUMNS);
_8c.insertBefore(_8b.domNode,_8c.firstChild);
}
catch(e){
jetspeed.url.loadingIndicatorHide();
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets();
}
}
}else{
_8b.editPageShow();
}
jetspeed.page.syncPageControls();
}
};
jetspeed.editPageTerminate=function(){
if(jetspeed.page.editMode){
var _8d=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
_8d.editModeNormal();
jetspeed.page.editMode=false;
if(!_8d.editorInitiatedFromDesktop){
var _8e=jetspeed.page.getPageUrl(true);
_8e=jetspeed.url.removeQueryParameter(_8e,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
_8e=jetspeed.url.removeQueryParameter(_8e,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_8e;
}else{
if(_8d!=null){
_8d.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_8f,_90,_91,_92){
if(!_8f){
_8f={};
}
jetspeed.url.retrieveContent(_8f,_90,_91,_92);
}};
jetspeed.om.PortletSelectorContentRetriever=function(){
};
jetspeed.om.PortletSelectorContentRetriever.prototype={getContent:function(_93,_94,_95,_96){
if(!_93){
_93={};
}
var _97="<div widgetId=\""+jetspeed.id.SELECTOR+"\" dojoType=\"PortletDefContainer\"></div>";
if(!_94){
_94=new jetspeed.om.BasicContentListener();
}
_94.notifySuccess(_97,_93.url,_95);
}};
jetspeed.om.PortletSelectorContentListener=function(){
};
jetspeed.om.PortletSelectorContentListener.prototype={notifySuccess:function(_98,_99,_9a){
var _9b=this.getPortletWindow();
if(_9b){
_9b.setPortletContent(_98,renderUrl);
}
},notifyFailure:function(_9c,_9d,_9e,_9f){
dojo.raise("PortletSelectorContentListener notifyFailure url: "+_9e+" type: "+_9c+jetspeed.url.formatBindError(_9d));
}};
jetspeed.om.PageContentListenerUpdate=function(_a0){
this.previousPage=_a0;
};
jetspeed.om.PageContentListenerUpdate.prototype={notifySuccess:function(_a1,_a2,_a3){
dojo.raise("PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url="+_a2);
},notifyFailure:function(_a4,_a5,_a6,_a7){
dojo.raise("PageContentListenerUpdate notifyFailure url: "+_a6+" type: "+_a4+jetspeed.url.formatBindError(_a5));
}};
jetspeed.om.PageContentListenerCreateWidget=function(){
};
jetspeed.om.PageContentListenerCreateWidget.prototype={notifySuccess:function(_a8,_a9,_aa){
_aa.loadFromPSML(_a8);
},notifyFailure:function(_ab,_ac,_ad,_ae){
dojo.raise("PageContentListenerCreateWidget error url: "+_ad+" type: "+_ab+jetspeed.url.formatBindError(_ac));
}};
jetspeed.om.Id=function(){
var _af="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_af.length>0){
_af+="-";
}
_af+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _b1 in arguments[i]){
this[_b1]=arguments[i][_b1];
}
}
}
}
this.jetspeed_om_id=_af;
};
dojo.lang.extend(jetspeed.om.Id,{getId:function(){
return this.jetspeed_om_id;
}});
jetspeed.om.Page=function(_b2,_b3,_b4,_b5){
if(_b2!=null&&_b3!=null){
this.requiredLayoutDecorator=_b2;
this.setPsmlPathFromDocumentUrl(_b3);
this.pageUrlFallback=_b3;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _b4!="undefined"){
this.addToHistory=_b4;
}
if(typeof _b5!="undefined"){
this.editMode=_b5;
}
this.layouts={};
this.columns=[];
this.portlets=[];
this.menus=[];
};
dojo.inherits(jetspeed.om.Page,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _b6=(this.name!=null&&this.name.length>0?this.name:null);
if(!_b6){
this.getPsmlUrl();
_b6=this.psmlPath;
}
return "page-"+_b6;
},setPsmlPathFromDocumentUrl:function(_b7){
var _b8=jetspeed.url.path.AJAX_API;
var _b9=null;
if(_b7==null){
_b9=window.location.pathname;
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _ba=window.location.hash;
if(_ba!=null&&_ba.length>0){
if(_ba.indexOf("#")==0){
_ba=(_ba.length>1?_ba.substring(1):"");
}
if(_ba!=null&&_ba.length>1&&_ba.indexOf("/")==0){
this.psmlPath=jetspeed.url.path.AJAX_API+_ba;
return;
}
}
}
}else{
var _bb=jetspeed.url.parse(_b7);
_b9=_bb.path;
}
var _bc=jetspeed.url.path.DESKTOP;
var _bd=_b9.indexOf(_bc);
if(_bd!=-1&&_b9.length>(_bd+_bc.length)){
_b8=_b8+_b9.substring(_bd+_bc.length);
}
this.psmlPath=_b8;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _be=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_be=jetspeed.url.addQueryParameter(_be,"layoutid",jetspeed.prefs.printModeOnly.layout);
_be=jetspeed.url.addQueryParameter(_be,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _be;
},retrievePsml:function(_bf){
if(_bf==null){
_bf=new jetspeed.om.PageContentListenerCreateWidget();
}
var _c0=this.getPsmlUrl();
var _c1="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_c0);
}
jetspeed.url.retrieveContent({url:_c0,mimetype:_c1},_bf,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_c2){
var _c3=this._parsePSML(_c2);
if(_c3==null){
return;
}
var _c4={};
this.columnsStructure=this._layoutCreateModel(_c3,null,_c4,true);
this.rootFragmentId=_c3.id;
var _c5=false;
if(this.editMode){
this.editMode=false;
if(jetspeed.prefs.printModeOnly==null){
_c5=true;
}
}
if(jetspeed.prefs.windowTiling){
this._createColumnsStart(document.getElementById(jetspeed.id.DESKTOP));
}
var _c6=new Array();
var _c7=this.columns.length;
for(var _c8=0;_c8<=this.columns.length;_c8++){
var _c9=null;
if(_c8==_c7){
_c9=_c4["z"];
if(_c9!=null){
_c9.sort(this._loadPortletZIndexCompare);
}
}else{
_c9=_c4[_c8.toString()];
}
if(_c9!=null){
for(var i=0;i<_c9.length;i++){
var _cb=_c9[i].portlet;
_c6.push(_cb);
_cb.createPortletWindow(_c8);
}
}
}
if(jetspeed.prefs.printModeOnly==null){
if(_c6&&_c6.length>0){
jetspeed.doRenderAll(null,_c6,true);
}
this._portletsInitializeWindowState(_c4["z"]);
var _cc=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER);
if(_c5||(_cc!=null&&_cc=="true")||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
_c5=false;
if(this.actions!=null&&(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null)){
_c5=true;
}
}
this.retrieveMenuDeclarations(true,_c5);
this.renderPageControls();
this.syncPageControls();
}else{
var _cb=null;
for(var _cd in this.portlets){
_cb=this.portlets[_cd];
break;
}
if(_cb!=null){
_cb.renderAction(null,jetspeed.prefs.printModeOnly.action);
this._portletsInitializeWindowState(_c4["z"]);
}
}
},_parsePSML:function(_ce){
var _cf=_ce.getElementsByTagName("page");
if(!_cf||_cf.length>1){
dojo.raise("unexpected zero or multiple <page> elements in psml");
}
var _d0=_cf[0];
var _d1=_d0.childNodes;
var _d2=new RegExp("(name|path|profiledPath|title|short-title)");
var _d3=null;
var _d4={};
for(var i=0;i<_d1.length;i++){
var _d6=_d1[i];
if(_d6.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _d7=_d6.nodeName;
if(_d7=="fragment"){
_d3=_d6;
}else{
if(_d7=="defaults"){
this.layoutDecorator=_d6.getAttribute("layout-decorator");
this.portletDecorator=_d6.getAttribute("portlet-decorator");
}else{
if(_d7&&_d7.match(_d2)){
this[jetspeed.purifyIdentifier(_d7,"","lo")]=((_d6&&_d6.firstChild)?_d6.firstChild.nodeValue:null);
}else{
if(_d7=="action"){
this._parsePSMLAction(_d6,_d4);
}
}
}
}
}
this.actions=_d4;
if(_d3==null){
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
var _d8=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_d8);
}
jetspeed.updatePage(_d8,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_d8);
}
jetspeed.updatePage(_d8,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _d8=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_d8);
}
jetspeed.updatePage(_d8,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_d8);
}
jetspeed.updatePage(_d8,true);
},changeUrl:escape(this.getPath())});
}
}
var _d9=this._parsePSMLLayoutFragment(_d3,0);
return _d9;
},_parsePSMLLayoutFragment:function(_da,_db){
var _dc=new Array();
var _dd=((_da!=null)?_da.getAttribute("type"):null);
if(_dd!="layout"){
dojo.raise("_parsePSMLLayoutFragment called with non-layout fragment: "+_da);
return null;
}
var _de=false;
var _df=_da.getAttribute("name");
if(_df!=null){
_df=_df.toLowerCase();
if(_df.indexOf("noactions")!=-1){
_de=true;
}
}
var _e0=null,_e1=0;
var _e2={};
var _e3=_da.childNodes;
var _e4,_e5,_e6,_e7,_e8;
for(var i=0;i<_e3.length;i++){
_e4=_e3[i];
if(_e4.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_e5=_e4.nodeName;
if(_e5=="fragment"){
_e8=_e4.getAttribute("type");
if(_e8=="layout"){
var _ea=this._parsePSMLLayoutFragment(_e4,i);
if(_ea!=null){
_dc.push(_ea);
}
}else{
var _eb=this._parsePSMLProperties(_e4,null);
var _ec=_eb[jetspeed.id.PORTLET_PROP_WINDOW_ICON];
if(_ec==null||_ec.length==0){
_ec=this._parsePSMLIcon(_e4);
if(_ec!=null&&_ec.length>0){
_eb[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=_ec;
}
}
_dc.push({id:_e4.getAttribute("id"),type:_e8,name:_e4.getAttribute("name"),properties:_eb,actions:this._parsePSMLActions(_e4,null),currentActionState:this._parsePSMLCurrentActionState(_e4),currentActionMode:this._parsePSMLCurrentActionMode(_e4),decorator:_e4.getAttribute("decorator"),layoutActionsDisabled:_de,documentOrderIndex:i});
}
}else{
if(_e5=="property"){
if(this._parsePSMLProperty(_e4,_e2)=="sizes"){
if(_e0!=null){
dojo.raise("_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: "+_da);
return null;
}
if(jetspeed.prefs.printModeOnly!=null){
_e0=["100"];
_e1=100;
}else{
_e7=_e4.getAttribute("value");
if(_e7!=null&&_e7.length>0){
_e0=_e7.split(",");
for(var j=0;j<_e0.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_e0[j]=_e0[j].replace(re,"$1");
_e1+=new Number(_e0[j]);
}
}
}
}
}
}
}
_dc.sort(this._fragmentRowCompare);
var _ef=new Array();
var _f0=new Array();
for(var i=0;i<_dc.length;i++){
if(_dc[i].type=="layout"){
_ef.push(i);
}else{
_f0.push(i);
}
}
if(_e0==null){
_e0=new Array();
_e0.push("100");
_e1=100;
}
return {id:_da.getAttribute("id"),type:_dd,name:_da.getAttribute("name"),decorator:_da.getAttribute("decorator"),columnSizes:_e0,columnSizesSum:_e1,properties:_e2,fragments:_dc,layoutFragmentIndexes:_ef,otherFragmentIndexes:_f0,layoutActionsDisabled:_de,documentOrderIndex:_db};
},_parsePSMLActions:function(_f1,_f2){
if(_f2==null){
_f2={};
}
var _f3=_f1.getElementsByTagName("action");
for(var _f4=0;_f4<_f3.length;_f4++){
var _f5=_f3[_f4];
this._parsePSMLAction(_f5,_f2);
}
return _f2;
},_parsePSMLAction:function(_f6,_f7){
var _f8=_f6.getAttribute("id");
if(_f8!=null){
var _f9=_f6.getAttribute("type");
var _fa=_f6.getAttribute("name");
var _fb=_f6.getAttribute("url");
var _fc=_f6.getAttribute("alt");
_f7[_f8.toLowerCase()]={id:_f8,type:_f9,label:_fa,url:_fb,alt:_fc};
}
},_parsePSMLCurrentActionState:function(_fd){
var _fe=_fd.getElementsByTagName("state");
if(_fe!=null&&_fe.length==1&&_fe[0].firstChild!=null){
return _fe[0].firstChild.nodeValue;
}
return null;
},_parsePSMLCurrentActionMode:function(_ff){
var _100=_ff.getElementsByTagName("mode");
if(_100!=null&&_100.length==1&&_100[0].firstChild!=null){
return _100[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_101){
var _102=_101.getElementsByTagName("icon");
if(_102!=null&&_102.length==1&&_102[0].firstChild!=null){
return _102[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProperties:function(_103,_104){
if(_104==null){
_104={};
}
var _105=_103.getElementsByTagName("property");
for(var _106=0;_106<_105.length;_106++){
this._parsePSMLProperty(_105[_106],_104);
}
return _104;
},_parsePSMLProperty:function(_107,_108){
var _109=_107.getAttribute("name");
var _10a=_107.getAttribute("value");
_108[_109]=_10a;
return _109;
},_fragmentRowCompare:function(_10b,_10c){
var rowA=_10b.documentOrderIndex*1000;
var rowB=_10c.documentOrderIndex*1000;
var _10f=_10b.properties["row"];
if(_10f!=null){
rowA=_10f;
}
var _110=_10c.properties["row"];
if(_110!=null){
rowB=_110;
}
return (rowA-rowB);
},_layoutCreateModel:function(_111,_112,_113,_114){
var _115=this.columns.length;
var _116=this._layoutRegisterAndCreateColumnsModel(_111,_112,_114);
var _117=_116.columnsInLayout;
if(_116.addedLayoutHeaderColumn){
_115++;
}
var _118=(_117==null?0:_117.length);
if(_111.layoutFragmentIndexes!=null&&_111.layoutFragmentIndexes.length>0){
var _119=null;
var _11a=0;
if(_111.otherFragmentIndexes!=null&&_111.otherFragmentIndexes.length>0){
_119=new Array();
}
for(var i=0;i<_111.fragments.length;i++){
var _11c=_111.fragments[i];
}
var _11d=new Array();
for(var i=0;i<_118;i++){
if(_119!=null){
_119.push(null);
}
_11d.push(false);
}
for(var i=0;i<_111.fragments.length;i++){
var _11c=_111.fragments[i];
var _11e=i;
if(_11c.properties&&_11c.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
if(_11c.properties[jetspeed.id.PORTLET_PROP_COLUMN]!=null&&_11c.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
_11e=_11c.properties[jetspeed.id.PORTLET_PROP_COLUMN];
}
}
if(_11e>=_118){
_11e=(_118>0?(_118-1):0);
}
var _11f=((_119==null)?null:_119[_11e]);
if(_11c.type=="layout"){
_11d[_11e]=true;
if(_11f!=null){
this._layoutCreateModel(_11f,_117[_11e],_113,true);
_119[_11e]=null;
}
this._layoutCreateModel(_11c,_117[_11e],_113,false);
}else{
if(_11f==null){
_11a++;
var _120={};
dojo.lang.mixin(_120,_111);
_120.fragments=new Array();
_120.layoutFragmentIndexes=new Array();
_120.otherFragmentIndexes=new Array();
_120.documentOrderIndex=_111.fragments[i].documentOrderIndex;
_120.clonedFromRootId=_120.id;
_120.clonedLayoutFragmentIndex=_11a;
_120.columnSizes=["100"];
_120.columnSizesSum=[100];
_120.id=_120.id+"-jsclone_"+_11a;
_119[_11e]=_120;
_11f=_120;
}
_11f.fragments.push(_11c);
_11f.otherFragmentIndexes.push(_11f.fragments.length-1);
}
}
if(_119!=null){
for(var i=0;i<_118;i++){
var _11f=_119[i];
if(_11f!=null){
_11d[i]=true;
this._layoutCreateModel(_11f,_117[i],_113,true);
}
}
}
for(var i=0;i<_118;i++){
if(_11d[i]){
_117[i].columnContainer=true;
}
}
if(_111.otherFragmentIndexes!=null&&_111.otherFragmentIndexes.length>0){
var _121=new Array();
for(var i=0;i<_111.fragments.length;i++){
var _122=true;
for(var j=0;j<_111.otherFragmentIndexes.length;j++){
if(_111.otherFragmentIndexes[j]==i){
_122=false;
break;
}
}
if(_122){
_121.push(_111.fragments[i]);
}
}
_111.fragments=_121;
_111.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_111,_117,_115,_113);
return _117;
},_layoutFragmentChildCollapse:function(_124,_125){
var _126=false;
if(_125==null){
_125=_124;
}
if(_124.layoutFragmentIndexes!=null&&_124.layoutFragmentIndexes.length>0){
_126=true;
for(var i=0;i<_124.layoutFragmentIndexes.length;i++){
var _128=_124.fragments[_124.layoutFragmentIndexes[i]];
if(_128.otherFragmentIndexes!=null&&_128.otherFragmentIndexes.length>0){
for(var i=0;i<_128.otherFragmentIndexes.length;i++){
var _129=_128.fragments[_128.otherFragmentIndexes[i]];
_129.properties[jetspeed.id.PORTLET_PROP_COLUMN]=-1;
_129.properties[jetspeed.id.PORTLET_PROP_ROW]=-1;
_129.documentOrderIndex=_125.fragments.length;
_125.fragments.push(_129);
_125.otherFragIndexes.push(_125.fragments.length);
}
}
this._layoutFragmentChildCollapse(_128,_125);
}
}
return _126;
},_layoutRegisterAndCreateColumnsModel:function(_12a,_12b,_12c){
this.layouts[_12a.id]=_12a;
var _12d=false;
var _12e=new Array();
if(jetspeed.prefs.windowTiling&&_12a.columnSizes.length>0){
var _12f=false;
if(jetspeed.browser_IE){
_12f=true;
}
if(_12b!=null&&!_12c){
var _130=new jetspeed.om.Column(0,_12a.id,(_12f?_12a.columnSizesSum-0.1:_12a.columnSizesSum),this.columns.length,_12a.layoutActionsDisabled);
_130.layoutHeader=true;
this.columns.push(_130);
if(_12b.columnChildren==null){
_12b.columnChildren=new Array();
}
_12b.columnChildren.push(_130);
_12b=_130;
_12d=true;
}
for(var i=0;i<_12a.columnSizes.length;i++){
var size=_12a.columnSizes[i];
if(_12f&&i==(_12a.columnSizes.length-1)){
size=size-0.1;
}
var _133=new jetspeed.om.Column(i,_12a.id,size,this.columns.length,_12a.layoutActionsDisabled);
this.columns.push(_133);
if(_12b!=null){
if(_12b.columnChildren==null){
_12b.columnChildren=new Array();
}
_12b.columnChildren.push(_133);
}
_12e.push(_133);
}
}
return {columnsInLayout:_12e,addedLayoutHeaderColumn:_12d};
},_layoutCreatePortletsModel:function(_134,_135,_136,_137){
if(_134.otherFragmentIndexes!=null&&_134.otherFragmentIndexes.length>0){
var _138=new Array();
for(var i=0;i<_135.length;i++){
_138.push(new Array());
}
for(var i=0;i<_134.otherFragmentIndexes.length;i++){
var _13a=_134.fragments[_134.otherFragmentIndexes[i]];
if(jetspeed.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter,_13a.id)){
_13a=null;
}
}
if(_13a!=null){
var _13b="z";
var _13c=_13a.properties[jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED];
var _13d=jetspeed.prefs.windowTiling;
var _13e=jetspeed.prefs.windowHeightExpand;
if(_13c!=null&&jetspeed.prefs.windowTiling&&jetspeed.prefs.printModeOnly==null){
var _13f=_13c.split(jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR);
var _140=null,_141=0,_142=null,_143=null,_144=false;
if(_13f!=null&&_13f.length>0){
var _145=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
for(var _146=0;_146<_13f.length;_146++){
_140=_13f[_146];
_141=((_140!=null)?_140.length:0);
if(_141>0){
var _147=_140.indexOf(_145);
if(_147>0&&_147<(_141-1)){
_142=_140.substring(0,_147);
_143=_140.substring(_147+1);
_144=((_143=="true")?true:false);
if(_142==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS){
_13d=_144;
}else{
if(_142==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT){
_13e=_144;
}
}
}
}
}
}
}else{
if(!jetspeed.prefs.windowTiling){
_13d=false;
}
}
_13a.properties[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_13d;
_13a.properties[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_13e;
if(_13d&&jetspeed.prefs.windowTiling){
var _148=_13a.properties[jetspeed.id.PORTLET_PROP_COLUMN];
if(_148==null||_148==""||_148<0||_148>=_135.length){
var _149=-1;
for(var j=0;j<_135.length;j++){
if(_149==-1||_138[j].length<_149){
_149=_138[j].length;
_148=j;
}
}
}
_138[_148].push(_13a.id);
var _14b=_136+new Number(_148);
_13b=_14b.toString();
}
var _14c=new jetspeed.om.Portlet(_13a.name,_13a.id,null,_13a.properties,_13a.actions,_13a.currentActionState,_13a.currentActionMode,_13a.decorator,_13a.layoutActionsDisabled);
_14c.initialize();
this.putPortlet(_14c);
if(_137[_13b]==null){
_137[_13b]=new Array();
}
_137[_13b].push({portlet:_14c,layout:_134.id});
}
}
}
},_portletsInitializeWindowState:function(_14d){
var _14e={};
this.getPortletCurrentColumnRow(null,false,_14e);
for(var _14f in this.portlets){
var _150=this.portlets[_14f];
var _151=_14e[_150.getId()];
if(_151==null&&_14d){
for(var i=0;i<_14d.length;i++){
if(_14d[i].portlet.getId()==_150.getId()){
_151={layout:_14d[i].layout};
break;
}
}
}
if(_151!=null){
_150._initializeWindowState(_151,false);
}else{
dojo.raise("page._portletsInitializeWindowState could not find window state init data for portlet: "+_150.getId());
}
}
},_loadPortletZIndexCompare:function(_153,_154){
var _155=null;
var _156=null;
var _157=null;
_155=_153.portlet._getInitialZIndex();
_156=_154.portlet._getInitialZIndex();
if(_155&&!_156){
return -1;
}else{
if(_156&&!_155){
return 1;
}else{
if(_155==_156){
return 0;
}
}
}
return (_155-_156);
},_createColumnsStart:function(_158){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _159=document.createElement("div");
_159.id=jetspeed.id.COLUMNS;
_159.setAttribute("id",jetspeed.id.COLUMNS);
for(var _15a=0;_15a<this.columnsStructure.length;_15a++){
var _15b=this.columnsStructure[_15a];
this._createColumns(_15b,_159);
}
_158.appendChild(_159);
},_createColumns:function(_15c,_15d){
_15c.createColumn();
if(_15c.columnChildren!=null&&_15c.columnChildren.length>0){
for(var _15e=0;_15e<_15c.columnChildren.length;_15e++){
var _15f=_15c.columnChildren[_15e];
this._createColumns(_15f,_15c.domNode);
}
}
_15d.appendChild(_15c.domNode);
},_removeColumns:function(_160){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_160){
var _162=jetspeed.ui.getPortletWindowChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_162,function(_163){
_160.appendChild(_163);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _164=dojo.byId(jetspeed.id.COLUMNS);
if(_164){
dojo.dom.removeNode(_164);
}
this.columns=[];
},getPortletCurrentColumnRow:function(_165,_166,_167){
if(!this.columns||this.columns.length==0){
return null;
}
var _168=null;
var _169=((_165!=null)?true:false);
var _16a=0;
var _16b=null;
var _16c=null;
var _16d=0;
var _16e=false;
for(var _16f=0;_16f<this.columns.length;_16f++){
var _170=this.columns[_16f];
var _171=_170.domNode.childNodes;
if(_16c==null||_16c!=_170.getLayoutId()){
_16c=_170.getLayoutId();
_16b=this.layouts[_16c];
if(_16b==null){
dojo.raise("getPortletCurrentColumnRow cannot locate layout id: "+_16c);
return null;
}
_16d=0;
_16e=false;
if(_16b.clonedFromRootId==null){
_16e=true;
}else{
var _172=this.getColumnFromColumnNode(_170.domNode.parentNode);
if(_172==null){
dojo.raise("getPortletCurrentColumnRow cannot locate parent column for column: "+_170);
return null;
}
_170=_172;
}
}
var _173=null;
for(var _174=0;_174<_171.length;_174++){
var _175=_171[_174];
if(dojo.html.hasClass(_175,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS)||(_166&&dojo.html.hasClass(_175,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))||(_169&&dojo.html.hasClass(_175,"desktopColumn"))){
_173=(_173==null?0:_173+1);
if((_173+1)>_16d){
_16d=(_173+1);
}
if(_165==null||_175==_165){
var _176={layout:_16c,column:_170.getLayoutColumnIndex(),row:_173};
if(!_16e){
_176.layout=_16b.clonedFromRootId;
}
if(_165!=null){
_168=_176;
break;
}else{
if(_167!=null){
var _177=this.getPortletWindowFromNode(_175);
if(_177==null){
dojo.raise("getPortletCurrentColumnRow cannot locate PortletWindow for node.");
}else{
var _178=_177.portlet;
if(_178==null){
dojo.raise("getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: "+_177.widgetId);
}else{
_167[_178.getId()]=_176;
}
}
}
}
}
}
}
if(_168!=null){
break;
}
}
return _168;
},_getPortletArrayByZIndex:function(){
var _179=this.getPortletArray();
if(!_179){
return _179;
}
var _17a=[];
for(var i=0;i<_179.length;i++){
if(!_179[i].getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_17a.push(_179[i]);
}
}
_17a.sort(this._portletZIndexCompare);
return _17a;
},_portletZIndexCompare:function(_17c,_17d){
var _17e=null;
var _17f=null;
var _180=null;
_180=_17c.getLastSavedWindowState();
_17e=_180.zIndex;
_180=_17d.getLastSavedWindowState();
_17f=_180.zIndex;
if(_17e&&!_17f){
return -1;
}else{
if(_17f&&!_17e){
return 1;
}else{
if(_17e==_17f){
return 0;
}
}
}
return (_17e-_17f);
},getPortletDecorationDefault:function(){
var pd=null;
if(djConfig.isDebug&&jetspeed.debug.windowDecorationRandom){
pd=jetspeed.prefs.portletDecorationsAllowed[Math.floor(Math.random()*jetspeed.prefs.portletDecorationsAllowed.length)];
}else{
var _182=this.getPortletDecorator();
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_182)!=-1){
pd=_182;
}else{
pd=jetspeed.prefs.windowDecoration;
}
}
return pd;
},getPortletArrayList:function(){
var _183=new dojo.collections.ArrayList();
for(var _184 in this.portlets){
var _185=this.portlets[_184];
_183.add(_185);
}
return _183;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _186=[];
for(var _187 in this.portlets){
var _188=this.portlets[_187];
_186.push(_188);
}
return _186;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _189=[];
for(var _18a in this.portlets){
var _18b=this.portlets[_18a];
_189.push(_18b.getId());
}
return _189;
},getPortletByName:function(_18c){
if(this.portlets&&_18c){
for(var _18d in this.portlets){
var _18e=this.portlets[_18d];
if(_18e.name==_18c){
return _18e;
}
}
}
return null;
},getPortlet:function(_18f){
if(this.portlets&&_18f){
return this.portlets[_18f];
}
return null;
},getPortletWindowFromNode:function(_190){
var _191=null;
if(this.portlets&&_190){
for(var _192 in this.portlets){
var _193=this.portlets[_192];
var _194=_193.getPortletWindow();
if(_194!=null){
if(_194.domNode==_190){
_191=_194;
break;
}
}
}
}
return _191;
},putPortlet:function(_195){
if(!_195){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_195.entityId]=_195;
},removePortlet:function(_196){
if(!_196||!this.portlets){
return;
}
delete this.portlets[_196.entityId];
},_destroyPortlets:function(){
for(var _197 in this.portlets){
var _198=this.portlets[_197];
_198._destroy();
}
},debugLayoutInfo:function(){
var _199="";
var i=0;
for(var _19b in this.layouts){
if(i>0){
_199+="\r\n";
}
_199+="layout["+_19b+"]: "+jetspeed.printobj(this.layouts[_19b],true,true,true);
i++;
}
return _199;
},debugColumnInfo:function(){
var _19c="";
for(var i=0;i<this.columns.length;i++){
if(i>0){
_19c+="\r\n";
}
_19c+=this.columns[i].toString();
}
return _19c;
},debugDumpLastSavedWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(true);
},debugDumpWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(false);
},debugPortletActions:function(){
var _19e=this.getPortletArray();
var _19f="";
for(var i=0;i<_19e.length;i++){
var _1a1=_19e[i];
if(i>0){
_19f+="\r\n";
}
_19f+="portlet ["+_1a1.name+"] actions: {";
for(var _1a2 in _1a1.actions){
_19f+=_1a2+"={"+jetspeed.printobj(_1a1.actions[_1a2],true)+"} ";
}
_19f+="}";
}
return _19f;
},displayAllPortlets:function(_1a3){
var _1a4=this.getPortletArray();
for(var i=0;i<_1a4.length;i++){
var _1a6=_1a4[i];
var _1a7=_1a6.getPortletWindow();
if(_1a7){
if(_1a3){
_1a7.domNode.style.display="none";
}else{
_1a7.domNode.style.display="";
}
}
}
},_debugDumpLastSavedWindowStateAllPortlets:function(_1a8){
var _1a9=this.getPortletArray();
var _1aa="";
for(var i=0;i<_1a9.length;i++){
var _1ac=_1a9[i];
if(i>0){
_1aa+="\r\n";
}
var _1ad=null;
try{
if(_1a8){
_1ad=_1ac.getLastSavedWindowState();
}else{
_1ad=_1ac.getCurrentWindowState();
}
}
catch(e){
}
_1aa+="["+_1ac.name+"] "+((_1ad==null)?"null":jetspeed.printobj(_1ad,true));
}
return _1aa;
},resetWindowLayout:function(){
for(var _1ae in this.portlets){
var _1af=this.portlets[_1ae];
_1af.submitChangedWindowState(false,true);
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
},getColumnFromColumnNode:function(_1b0){
if(_1b0==null){
return null;
}
var _1b1=_1b0.getAttribute("columnIndex");
if(_1b1==null){
return null;
}
var _1b2=new Number(_1b1);
if(_1b2>=0&&_1b2<this.columns.length){
return this.columns[_1b2];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1b4=null;
if(!this.columns){
return _1b4;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1b4=i;
break;
}
}
return _1b4;
},getColumnContainingNode:function(node){
var _1b7=this.getColumnIndexContainingNode(node);
return ((_1b7!=null&&_1b7>=0)?this.columns[_1b7]:null);
},getDescendantColumns:function(_1b8){
var dMap={};
if(_1b8==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1b8&&_1b8.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1bc,_1bd,_1be){
var _1bf=new jetspeed.om.Portlet(_1bc,_1bd);
if(_1be){
_1bf.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1be);
}
_1bf.initialize();
this.putPortlet(_1bf);
_1bf.retrieveContent();
},removePortletFromPage:function(_1c0){
var _1c1=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1c2="?action=remove&id="+escape(portletDef.getPortletName());
var _1c3=jetspeed.page.getPsmlUrl()+_1c2;
var _1c4="text/xml";
var _1c5=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1c3,mimetype:_1c4},_1c1,_1c5,jetspeed.debugContentDumpIds);
},putMenu:function(_1c6){
if(!_1c6){
return;
}
var _1c7=(_1c6.getName?_1c6.getName():null);
if(_1c7!=null){
this.menus[_1c7]=_1c6;
}
},getMenu:function(_1c8){
if(_1c8==null){
return null;
}
return this.menus[_1c8];
},removeMenu:function(_1c9){
if(_1c9==null){
return;
}
var _1ca=null;
if(dojo.lang.isString(_1c9)){
_1ca=_1c9;
}else{
_1ca=(_1c9.getName?_1c9.getName():null);
}
if(_1ca!=null){
delete this.menus[_1ca];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1cb=[];
for(var _1cc in this.menus){
_1cb.push(_1cc);
}
return _1cb;
},retrieveMenuDeclarations:function(_1cd,_1ce){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1cd,_1ce);
this.clearMenus();
var _1cf="?action=getmenus";
if(_1cd){
_1cf+="&includeMenuDefs=true";
}
var _1d0=this.getPsmlUrl()+_1cf;
var _1d1="text/xml";
var _1d2=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1d0,mimetype:_1d1},contentListener,_1d2,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1d3,_1d4,_1d5){
if(_1d5==null){
_1d5=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1d6="?action=getmenu&name="+_1d3;
var _1d7=this.getPsmlUrl()+_1d6;
var _1d8="text/xml";
var _1d9=new jetspeed.om.Id("getmenu-"+_1d3,{page:this,menuName:_1d3,menuType:_1d4});
jetspeed.url.retrieveContent({url:_1d7,mimetype:_1d8},_1d5,_1d9,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1da in this.actionButtons){
var _1db=false;
if(_1da==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1db=true;
}
}else{
if(_1da==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1db=true;
}
}else{
if(_1da==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1db=true;
}
}else{
_1db=true;
}
}
}
if(_1db){
this.actionButtons[_1da].style.display="";
}else{
this.actionButtons[_1da].style.display="none";
}
}
},renderPageControls:function(){
var _1dc=[];
if(this.actions!=null){
for(var _1dd in this.actions){
if(_1dd!=jetspeed.id.ACTION_NAME_HELP){
_1dc.push(_1dd);
}
if(_1dd==jetspeed.id.ACTION_NAME_EDIT){
_1dc.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1dc.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1dc.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1de=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1de!=null&&_1dc!=null&&_1dc.length>0){
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
for(var i=0;i<_1dc.length;i++){
var _1dd=_1dc[i];
var _1e0=document.createElement("div");
_1e0.className="portalPageActionButton";
_1e0.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1dd+".gif)";
_1e0.actionName=_1dd;
this.actionButtons[_1dd]=_1e0;
_1de.appendChild(_1e0);
dojo.event.connect(_1e0,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1e1=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1e1=jetspeed.prefs.desktopActionLabels[_1dd];
}
if(_1e1==null||_1e1.length==0){
_1e1=dojo.string.capitalize(_1dd);
}
var _1e2=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1e1,connectId:_1e0,delay:"100"});
this.actionButtonTooltips.push(_1e2);
document.body.appendChild(_1e2.domNode);
}
}
}
},_destroyEditPage:function(){
var _1e3=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(_1e3!=null){
_1e3.editPageDestroy();
}
},_destroyPageControls:function(){
var _1e4=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1e4!=null&&_1e4.childNodes&&_1e4.childNodes.length>0){
for(var i=(_1e4.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1e4.childNodes[i]);
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
},pageActionProcess:function(_1e7){
if(_1e7==null){
return;
}
if(_1e7==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1e7==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1e7==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1e8=this.getPageAction(_1e7);
alert("pageAction "+_1e7+" : "+_1e8);
if(_1e8==null){
return;
}
if(_1e8.url==null){
return;
}
var _1e9=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1e8.url;
jetspeed.pageNavigate(_1e9);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1eb,_1ec){
if(!_1ec){
_1ec=escape(this.getPagePathAndQuery());
}else{
_1ec=escape(_1ec);
}
var _1ed=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1ec;
if(_1eb!=null){
_1ed+="&jslayoutid="+escape(_1eb);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1ed));
},setPageModePortletActions:function(_1ee){
if(_1ee==null||_1ee.actions==null){
return;
}
if(_1ee.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1ee.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1ef){
if(this.pageUrl!=null&&!_1ef){
return this.pageUrl;
}
var _1f0=jetspeed.url.path.SERVER+((_1ef)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1f1=jetspeed.url.parse(_1f0);
var _1f2=null;
if(this.pageUrlFallback!=null){
_1f2=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1f2=jetspeed.url.parse(window.location.href);
}
if(_1f1!=null&&_1f2!=null){
var _1f3=_1f2.query;
if(_1f3!=null&&_1f3.length>0){
var _1f4=_1f1.query;
if(_1f4!=null&&_1f4.length>0){
_1f0=_1f0+"&"+_1f3;
}else{
_1f0=_1f0+"?"+_1f3;
}
}
}
if(!_1ef){
this.pageUrl=_1f0;
}
return _1f0;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1f5=this.getPath();
var _1f6=jetspeed.url.parse(_1f5);
var _1f7=null;
if(this.pageUrlFallback!=null){
_1f7=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1f7=jetspeed.url.parse(window.location.href);
}
if(_1f6!=null&&_1f7!=null){
var _1f8=_1f7.query;
if(_1f8!=null&&_1f8.length>0){
var _1f9=_1f6.query;
if(_1f9!=null&&_1f9.length>0){
_1f5=_1f5+"&"+_1f8;
}else{
_1f5=_1f5+"?"+_1f8;
}
}
}
this.pagePathAndQuery=_1f5;
return _1f5;
},getPageDirectory:function(_1fa){
var _1fb="/";
var _1fc=(_1fa?this.getRealPath():this.getPath());
if(_1fc!=null){
var _1fd=_1fc.lastIndexOf("/");
if(_1fd!=-1){
if((_1fd+1)<_1fc.length){
_1fb=_1fc.substring(0,_1fd+1);
}else{
_1fb=_1fc;
}
}
}
return _1fb;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_1ff){
if(!_1ff){
_1ff="";
}
if(!jetspeed.url.validateUrlStartsWithHttp(_1ff)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_1ff;
}
return _1ff;
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
jetspeed.om.Column=function(_200,_201,size,_203,_204){
this.layoutColumnIndex=_200;
this.layoutId=_201;
this.size=size;
this.pageColumnIndex=new Number(_203);
if(typeof _204!="undefined"){
this.layoutActionsDisabled=_204;
}
this.id="jscol_"+_203;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_205){
var _206="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_206="desktopColumn desktopColumnClear";
}
var _207=document.createElement("div");
_207.setAttribute("columnIndex",this.getPageColumnIndex());
_207.style.width=this.size+"%";
if(this.layoutHeader){
_206="desktopColumn desktopLayoutHeader";
}else{
_207.style.minHeight="40px";
}
_207.className=_206;
_207.id=this.getId();
this.domNode=_207;
if(_205!=null){
_205.appendChild(_207);
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
var _20b=dojo.html.getAbsolutePosition(this.domNode,true);
var _20c=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_20b.x)+", right:"+(_20b.x+_20c.width)+", top:"+(_20b.y)+", bottom:"+(_20b.y+_20c.height)+"}";
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
jetspeed.om.Portlet=function(_20d,_20e,_20f,_210,_211,_212,_213,_214,_215){
this.name=_20d;
this.entityId=_20e;
if(_210){
this.properties=_210;
}else{
this.properties={};
}
if(_211){
this.actions=_211;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_212;
this.currentActionMode=_213;
if(_20f){
this.contentRetriever=_20f;
}
if(_214!=null&&_214.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_214)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_214);
}
}
this.layoutActionsDisabled=false;
if(typeof _215!="undefined"){
this.layoutActionsDisabled=_215;
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
var _216=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_216=="true"){
_216=true;
}else{
if(_216=="false"){
_216=false;
}else{
if(_216!=true&&_216!=false){
_216=true;
}
}
}
}else{
_216=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_216);
var _217=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_217=="true"){
_217=true;
}else{
if(_216=="false"){
_217=false;
}else{
if(_217!=true&&_217!=false){
_217=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_217);
var _218=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_218&&this.name){
var re=(/^[^:]*:*/);
_218=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_218);
}
},postParseAnnotateHtml:function(_21a){
if(_21a){
var _21b=_21a;
var _21c=_21b.getElementsByTagName("form");
var _21d=jetspeed.debug.postParseAnnotateHtml;
var _21e=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_21c){
for(var i=0;i<_21c.length;i++){
var _220=_21c[i];
var _221=_220.action;
var _222=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_221);
var _223=_222.operation;
if(_223==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_223==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _224=jetspeed.portleturl.generateJSPseudoUrlActionRender(_222,true);
_220.action=_224;
var _225=new jetspeed.om.ActionRenderFormBind(_220,_222.url,_222.portletEntityId,_223);
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_223+") for form with action: "+_221);
}
}else{
if(_221==null||_221.length==0){
var _225=new jetspeed.om.ActionRenderFormBind(_220,null,this.entityId,null);
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_221);
}
}
}
}
}
var _226=_21b.getElementsByTagName("a");
if(_226){
for(var i=0;i<_226.length;i++){
var _227=_226[i];
var _228=_227.href;
var _222=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_228);
var _229=null;
if(!_21e){
_229=jetspeed.portleturl.generateJSPseudoUrlActionRender(_222);
}
if(!_229){
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_228);
}
}else{
if(_229==_228){
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_228);
}
}else{
if(_21d){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_228+" with: "+_229);
}
_227.href=_229;
}
}
}
}
}
},getPortletWindow:function(){
var _22a=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_22a){
return dojo.widget.byId(_22a);
}
return null;
},getCurrentWindowState:function(_22b){
var _22c=this.getPortletWindow();
if(!_22c){
return null;
}
var _22d=_22c.getCurrentWindowStateForPersistence(_22b);
if(!_22b){
if(_22d.layout==null){
_22d.layout=this.lastSavedWindowState.layout;
}
}
return _22d;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_22e,_22f){
if(!_22e){
_22e={};
}
var _230=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _231=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_22e[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_230;
_22e[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_231;
var _232=this.getProperty("width");
if(!_22f&&_232!=null&&_232>0){
_22e.width=Math.floor(_232);
}else{
if(_22f){
_22e.width=-1;
}
}
var _233=this.getProperty("height");
if(!_22f&&_233!=null&&_233>0){
_22e.height=Math.floor(_233);
}else{
if(_22f){
_22e.height=-1;
}
}
if(!_230||!jetspeed.prefs.windowTiling){
var _234=this.getProperty("x");
if(!_22f&&_234!=null&&_234>=0){
_22e.left=Math.floor(((_234>0)?_234:0));
}else{
if(_22f){
_22e.left=-1;
}
}
var _235=this.getProperty("y");
if(!_22f&&_235!=null&&_235>=0){
_22e.top=Math.floor(((_235>0)?_235:0));
}else{
_22e.top=-1;
}
var _236=this._getInitialZIndex(_22f);
if(_236!=null){
_22e.zIndex=_236;
}
}
return _22e;
},_initializeWindowState:function(_237,_238){
var _239=(_237?_237:{});
this.getInitialWindowDimensions(_239,_238);
if(jetspeed.debug.initializeWindowState){
var _23a=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_23a||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_239.zIndex+" x="+_239.left+" y="+_239.top+" width="+_239.width+" height="+_239.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_239.column+" row="+_239.row+" width="+_239.width+" height="+_239.height);
}
}
this.lastSavedWindowState=_239;
return _239;
},_getInitialZIndex:function(_23b){
var _23c=null;
var _23d=this.getProperty("z");
if(!_23b&&_23d!=null&&_23d>=0){
_23c=Math.floor(_23d);
}else{
if(_23b){
_23c=-1;
}
}
return _23c;
},_getChangedWindowState:function(_23e){
var _23f=this.getLastSavedWindowState();
if(_23f&&dojo.lang.isEmpty(_23f)){
_23f=null;
_23e=false;
}
var _240=this.getCurrentWindowState(_23e);
var _241=_240[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _242=!_241;
if(!_23f){
var _243={state:_240,positionChanged:true,extendedPropChanged:true};
if(_242){
_243.zIndexChanged=true;
}
return _243;
}
var _244=false;
var _245=false;
var _246=false;
var _247=false;
for(var _248 in _240){
if(_240[_248]!=_23f[_248]){
if(_248==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_248==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_244=true;
_246=true;
_245=true;
}else{
if(_248=="zIndex"){
if(_242){
_244=true;
_247=true;
}
}else{
_244=true;
_245=true;
}
}
}
}
if(_244){
var _243={state:_240,positionChanged:_245,extendedPropChanged:_246};
if(_242){
_243.zIndexChanged=_247;
}
return _243;
}
return null;
},createPortletWindow:function(_249){
jetspeed.ui.createPortletWindow(this,_249);
},getPortletUrl:function(_24a){
var _24b=null;
if(_24a&&_24a.url){
_24b=_24a.url;
}else{
if(_24a&&_24a.formNode){
var _24c=_24a.formNode.getAttribute("action");
if(_24c){
_24b=_24c;
}
}
}
if(_24b==null){
_24b=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_24a.dontAddQueryArgs){
_24b=jetspeed.url.parse(_24b);
_24b=jetspeed.url.addQueryParameter(_24b,"entity",this.entityId,true);
_24b=jetspeed.url.addQueryParameter(_24b,"portlet",this.name,true);
_24b=jetspeed.url.addQueryParameter(_24b,"encoder","desktop",true);
if(_24a.jsPageUrl!=null){
var _24d=_24a.jsPageUrl.query;
if(_24d!=null&&_24d.length>0){
_24b=_24b.toString()+"&"+_24d;
}
}
}
if(_24a){
_24a.url=_24b.toString();
}
return _24b;
},_submitJetspeedAjaxApi:function(_24e,_24f,_250){
var _251="?action="+_24e+"&id="+this.entityId+_24f;
var _252=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_251;
var _253="text/xml";
var _254=new jetspeed.om.Id(_24e,this.entityId);
_254.portlet=this;
jetspeed.url.retrieveContent({url:_252,mimetype:_253},_250,_254,null);
},submitChangedWindowState:function(_255,_256){
var _257=null;
if(_256){
_257={state:this._initializeWindowState(null,true)};
}else{
_257=this._getChangedWindowState(_255);
}
if(_257){
var _258=_257.state;
var _259=_258[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _25a=_258[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _25b=null;
if(_257.extendedPropChanged){
var _25c=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _25d=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_25b=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_25c+_259.toString();
_25b+=_25d+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_25c+_25a.toString();
_25b=escape(_25b);
}
var _25e="";
var _25f=null;
if(_259){
_25f="moveabs";
if(_258.column!=null){
_25e+="&col="+_258.column;
}
if(_258.row!=null){
_25e+="&row="+_258.row;
}
if(_258.layout!=null){
_25e+="&layoutid="+_258.layout;
}
if(_258.height!=null){
_25e+="&height="+_258.height;
}
}else{
_25f="move";
if(_258.zIndex!=null){
_25e+="&z="+_258.zIndex;
}
if(_258.width!=null){
_25e+="&width="+_258.width;
}
if(_258.height!=null){
_25e+="&height="+_258.height;
}
if(_258.left!=null){
_25e+="&x="+_258.left;
}
if(_258.top!=null){
_25e+="&y="+_258.top;
}
}
if(_25b!=null){
_25e+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_25b;
}
this._submitJetspeedAjaxApi(_25f,_25e,new jetspeed.om.MoveAjaxApiContentListener(this,_258));
if(!_255&&!_256){
if(!_259&&_257.zIndexChanged){
var _260=jetspeed.page.getPortletArrayList();
var _261=dojo.collections.Set.difference(_260,[this]);
if(!_260||!_261||((_261.count+1)!=_260.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_261&&_261.count>0){
dojo.lang.forEach(_261.toArray(),function(_262){
if(!_262.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_262.submitChangedWindowState(true);
}
});
}
}
}else{
if(_259){
}
}
}
}
},retrieveContent:function(_263,_264,_265){
if(_263==null){
_263=new jetspeed.om.PortletContentListener(this,_265,_264);
}
if(!_264){
_264={};
}
var _266=this;
_266.getPortletUrl(_264);
this.contentRetriever.getContent(_264,_263,_266,jetspeed.debugContentDumpIds);
},setPortletContent:function(_267,_268,_269){
var _26a=this.getPortletWindow();
if(_269!=null&&_269.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_269);
if(_26a&&!this.loadingIndicatorIsShown()){
_26a.setPortletTitle(_269);
}
}
if(_26a){
_26a.setPortletContent(_267,_268);
}
},loadingIndicatorIsShown:function(){
var _26b=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _26c=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _26d=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _26e=this.getPortletWindow();
if(_26e&&(_26b||_26c)){
var _26f=_26e.getPortletTitle();
if(_26f&&(_26f==_26b||_26f==_26c)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_270){
var _271=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_271=jetspeed.prefs.desktopActionLabels[_270];
if(_271!=null&&_271.length==0){
_271=null;
}
}
return _271;
},loadingIndicatorShow:function(_272){
if(_272&&!this.loadingIndicatorIsShown()){
var _273=this._getLoadingActionLabel(_272);
var _274=this.getPortletWindow();
if(_274&&_273){
_274.setPortletTitle(_273);
}
}
},loadingIndicatorHide:function(){
var _275=this.getPortletWindow();
if(_275){
_275.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_277){
this.properties[name]=_277;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_27a,_27b){
var _27c=null;
if(_27a!=null){
_27c=this.getAction(_27a);
}
var _27d=_27b;
if(_27d==null&&_27c!=null){
_27d=_27c.url;
}
if(_27d==null){
return;
}
var _27e=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_27d+jetspeed.page.getPath();
if(_27a!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_27e});
}else{
var _27f=jetspeed.page.getPageUrl();
_27f=jetspeed.url.addQueryParameter(_27f,"jsprintmode","true");
_27f=jetspeed.url.addQueryParameter(_27f,"jsaction",escape(_27c.url));
_27f=jetspeed.url.addQueryParameter(_27f,"jsentity",this.entityId);
_27f=jetspeed.url.addQueryParameter(_27f,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_27f.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_281,_282,_283){
if(_281){
this.actions=_281;
}else{
this.actions={};
}
this.currentActionState=_282;
this.currentActionMode=_283;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _284=this.getPortletWindow();
if(_284){
_284.windowActionButtonSync();
}
},_destroy:function(){
var _285=this.getPortletWindow();
if(_285){
_285.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_288,_289){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_288;
this.submitOperation=_289;
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
var _28e=form.getElementsByTagName("input");
for(var i=0;i<_28e.length;i++){
var _28f=_28e[i];
if(_28f.type.toLowerCase()=="image"&&_28f.form==form){
this.connect(_28f,"onclick","click");
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
},onSubmit:function(_291){
var _292=true;
if(this.isFormSubmitInProgress()){
_292=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_292=false;
}
}
}
return _292;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _294=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _295={};
if(_294.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_294.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _296=jetspeed.portleturl.generateJSPseudoUrlActionRender(_294,true);
this.form.action=_296;
this.submitOperation=_294.operation;
this.entityId=_294.portletEntityId;
_295.url=_294.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_295.formFilter=dojo.lang.hitch(this,"formFilter");
_295.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_295),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_295),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_297){
if(_297!=undefined){
this.formSubmitInProgress=_297;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_298,_299){
this.folderName=_298;
this.folderPath=_299;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_29a,_29b,_29c,_29d,_29e){
this.portletName=_29a;
this.portletDisplayName=_29b;
this.portletDescription=_29c;
this.image=_29d;
this.count=_29e;
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
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_29f,_2a0,_2a1){
var _2a2=_2a1.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_2a2){
var _2a3=dojo.widget.byId(_2a2);
if(_2a3){
_2a3.setPortletContent(_29f,_2a0);
}
}
},notifyFailure:function(type,_2a5,_2a6,_2a7){
dojo.raise("BasicContentListener notifyFailure url: "+_2a6+" type: "+type+jetspeed.url.formatBindError(_2a5));
}};
jetspeed.om.PortletContentListener=function(_2a8,_2a9,_2aa){
this.portlet=_2a8;
this.suppressGetActions=_2a9;
this.submittedFormBindObject=null;
if(_2aa!=null&&_2aa.submitFormBindObject!=null){
this.submittedFormBindObject=_2aa.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_2ab){
if(this.portlet==null){
return;
}
if(_2ab){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2ac,_2ad,_2ae,http){
var _2b0=null;
if(http!=null){
_2b0=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2b0!=null){
_2b0=unescape(_2b0);
}
}
_2ae.setPortletContent(_2ac,_2ad,_2b0);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2ae.getId());
}else{
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2b2,_2b3,_2b4){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_2b3+" type: "+type+jetspeed.url.formatBindError(_2b2));
}};
jetspeed.om.PortletActionContentListener=function(_2b5,_2b6){
this.portlet=_2b5;
this.submittedFormBindObject=null;
if(_2b6!=null&&_2b6.submitFormBindObject!=null){
this.submittedFormBindObject=_2b6.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2b7){
if(this.portlet==null){
return;
}
if(_2b7){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2b8,_2b9,_2ba,http){
var _2bc=null;
var _2bd=false;
var _2be=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2b8);
if(_2be.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2be.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2be.operation+"-url in response body: "+_2b8+"  url: "+_2be.url+" entity-id: "+_2be.portletEntityId);
}
_2bc=_2be.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2b8);
}
_2bc=_2b8;
if(_2bc){
var _2bf=_2bc.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2bf==-1){
_2bd=true;
window.location.href=_2bc;
_2bc=null;
}else{
if(_2bf>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2b8);
_2bc=null;
}
}
}
}
if(_2bc!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2bc);
}
jetspeed.doRenderAll(_2bc);
}else{
this._setPortletLoading(false);
}
if(!_2bd&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2c1,_2c2,_2c3){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2c1));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2c4=this.getUrl();
if(_2c4){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2c4,this.getTarget());
}else{
jetspeed.updatePage(_2c4);
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
jetspeed.om.Menu=function(_2c5,_2c6){
this._is_parsed=false;
this.name=_2c5;
this.type=_2c6;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2c7){
if(!_2c7){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2c7);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2c9){
if(!this.hasOptions()){
return null;
}
if(_2c9==0||_2c9>0){
if(_2c9>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2c9];
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
var _2cb=this.options[i];
if(_2cb instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2cd,_2ce){
var _2cf=this.parseMenu(data,_2ce.menuName,_2ce.menuType);
_2ce.page.putMenu(_2cf);
},notifyFailure:function(type,_2d1,_2d2,_2d3){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2d3.toString()+"] url: "+_2d2+" type: "+type+jetspeed.url.formatBindError(_2d1));
},parseMenu:function(node,_2d5,_2d6){
var menu=null;
var _2d8=node.getElementsByTagName("js");
if(!_2d8||_2d8.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2d9=_2d8[0].childNodes;
for(var i=0;i<_2d9.length;i++){
var _2db=_2d9[i];
if(_2db.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2dc=_2db.nodeName;
if(_2dc=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2db,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2d5;
}
if(menu.type==null){
menu.type=_2d6;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2df=null;
var _2e0=node.childNodes;
for(var i=0;i<_2e0.length;i++){
var _2e2=_2e0[i];
if(_2e2.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2e3=_2e2.nodeName;
if(_2e3=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e2,new jetspeed.om.Menu()));
}
}else{
if(_2e3=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e2,new jetspeed.om.MenuOption()));
}
}else{
if(_2e3=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e2,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2e3){
mObj[_2e3]=((_2e2&&_2e2.firstChild)?_2e2.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2e4,_2e5){
this.includeMenuDefs=_2e4;
this.initiateEditMode=_2e5;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2e7,_2e8){
var _2e9=this.getMenuDefs(data,_2e7,_2e8);
for(var i=0;i<_2e9.length;i++){
var mObj=_2e9[i];
_2e8.page.putMenu(mObj);
}
this.notifyFinished(_2e8);
},getMenuDefs:function(data,_2ed,_2ee){
var _2ef=[];
var _2f0=data.getElementsByTagName("menu");
for(var i=0;i<_2f0.length;i++){
var _2f2=_2f0[i].getAttribute("type");
if(this.includeMenuDefs){
_2ef.push(this.parseMenuObject(_2f0[i],new jetspeed.om.Menu(null,_2f2)));
}else{
var _2f3=_2f0[i].firstChild.nodeValue;
_2ef.push(new jetspeed.om.Menu(_2f3,_2f2));
}
}
return _2ef;
},notifyFailure:function(type,_2f5,_2f6,_2f7){
dojo.raise("MenusAjaxApiContentListener error ["+_2f7.toString()+"] url: "+_2f6+" type: "+type+jetspeed.url.formatBindError(_2f5));
},notifyFinished:function(_2f8){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
jetspeed.editPageInitiate();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_2f9){
this.portletEntityId=_2f9;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_2fb,_2fc){
if(jetspeed.url.checkAjaxApiResponse(_2fb,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_2fd){
var _2fe=jetspeed.page.getPortlet(this.portletEntityId);
if(_2fe){
if(_2fd){
_2fe.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_2fe.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_300,_301,_302){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_302.toString()+"] url: "+_301+" type: "+type+jetspeed.url.formatBindError(_300));
}});
jetspeed.om.PageChangeActionContentListener=function(_303){
this.pageActionUrl=_303;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_305,_306){
if(jetspeed.url.checkAjaxApiResponse(_305,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_308,_309,_30a){
dojo.raise("PageChangeActionContentListener error ["+_30a.toString()+"] url: "+_309+" type: "+type+jetspeed.url.formatBindError(_308));
}});
jetspeed.om.PortletActionsContentListener=function(_30b){
this.portletEntityIds=_30b;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_30c){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _30e=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_30e){
if(_30c){
_30e.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_30e.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_310,_311){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_310,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _313=this.parsePortletActionsResponse(node);
for(var i=0;i<_313.length;i++){
var _315=_313[i];
var _316=_315.id;
var _317=jetspeed.page.getPortlet(_316);
if(_317!=null){
_317.updateActions(_315.actions,_315.currentActionState,_315.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _319=new Array();
var _31a=node.getElementsByTagName("js");
if(!_31a||_31a.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _319;
}
var _31b=_31a[0].childNodes;
for(var i=0;i<_31b.length;i++){
var _31d=_31b[i];
if(_31d.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _31e=_31d.nodeName;
if(_31e=="portlets"){
var _31f=_31d;
var _320=_31f.childNodes;
for(var pI=0;pI<_320.length;pI++){
var _322=_320[pI];
if(_322.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _323=_322.nodeName;
if(_323=="portlet"){
var _324=this.parsePortletElement(_322);
if(_324!=null){
_319.push(_324);
}
}
}
}
}
return _319;
},parsePortletElement:function(node){
var _326=node.getAttribute("id");
if(_326!=null){
var _327=jetspeed.page._parsePSMLActions(node,null);
var _328=jetspeed.page._parsePSMLCurrentActionState(node);
var _329=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_326,actions:_327,currentActionState:_328,currentActionMode:_329};
}
return null;
},notifyFailure:function(type,_32b,_32c,_32d){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_32d.toString()+"] url: "+_32c+" type: "+type+jetspeed.url.formatBindError(_32b));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_32e,_32f,_330){
this.portletDef=_32e;
this.windowWidgetId=_32f;
this.addToCurrentPage=_330;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_332,_333){
if(jetspeed.url.checkAjaxApiResponse(_332,data,true,"add-portlet")){
var _334=this.parseAddPortletResponse(data);
if(_334&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_334,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _336=null;
var _337=node.getElementsByTagName("js");
if(!_337||_337.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _338=_337[0].childNodes;
for(var i=0;i<_338.length;i++){
var _33a=_338[i];
if(_33a.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _33b=_33a.nodeName;
if(_33b=="entity"){
_336=((_33a&&_33a.firstChild)?_33a.firstChild.nodeValue:null);
break;
}
}
return _336;
},notifyFailure:function(type,_33d,_33e,_33f){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_33f.toString()+"] url: "+_33e+" type: "+type+jetspeed.url.formatBindError(_33d));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_341,_342){
var _343=this.parsePortlets(data);
var _344=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_344!=null){
for(var i=0;i<_343.length;i++){
_344.addChild(_343[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_342,_343);
}
},notifyFailure:function(type,_347,_348,_349){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_349.toString()+"] url: "+_348+" type: "+type+jetspeed.url.formatBindError(_347));
},parsePortlets:function(node){
var _34b=[];
var _34c=node.getElementsByTagName("js");
if(!_34c||_34c.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _34d=_34c[0].childNodes;
for(var i=0;i<_34d.length;i++){
var _34f=_34d[i];
if(_34f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _350=_34f.nodeName;
if(_350=="portlets"){
var _351=_34f;
var _352=_351.childNodes;
for(var pI=0;pI<_352.length;pI++){
var _354=_352[pI];
if(_354.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _355=_354.nodeName;
if(_355=="portlet"){
var _356=this.parsePortletElement(_354);
_34b.push(_356);
}
}
}
}
return _34b;
},parsePortletElement:function(node){
var _358=node.getAttribute("name");
var _359=node.getAttribute("displayName");
var _35a=node.getAttribute("description");
var _35b=node.getAttribute("image");
var _35c=0;
return new jetspeed.om.PortletDef(_358,_359,_35a,_35b,_35c);
}});
jetspeed.om.FoldersListContentListener=function(_35d){
this.notifyFinished=_35d;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_35f,_360){
var _361=this.parseFolders(data);
var _362=this.parsePages(data);
var _363=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_360,_361,_362,_363);
}
},notifyFailure:function(type,_365,_366,_367){
dojo.raise("FoldersListContentListener error ["+_367.toString()+"] url: "+_366+" type: "+type+jetspeed.url.formatBindError(_365));
},parseFolders:function(node){
var _369=[];
var _36a=node.getElementsByTagName("js");
if(!_36a||_36a.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _36b=_36a[0].childNodes;
for(var i=0;i<_36b.length;i++){
var _36d=_36b[i];
if(_36d.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _36e=_36d.nodeName;
if(_36e=="folders"){
var _36f=_36d;
var _370=_36f.childNodes;
for(var pI=0;pI<_370.length;pI++){
var _372=_370[pI];
if(_372.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _373=_372.nodeName;
if(_373=="folder"){
var _374=this.parsePortletElement(_372);
_369.push(_374);
}
}
}
}
return _369;
},parsePages:function(node){
var _376=[];
var _377=node.getElementsByTagName("js");
if(!_377||_377.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _378=_377[0].childNodes;
for(var i=0;i<_378.length;i++){
var _37a=_378[i];
if(_37a.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _37b=_37a.nodeName;
if(_37b=="folders"){
var _37c=_37a;
var _37d=_37c.childNodes;
for(var pI=0;pI<_37d.length;pI++){
var _37f=_37d[pI];
if(_37f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _380=_37f.nodeName;
if(_380=="page"){
var _381=this.parsePortletElement(_37f);
_376.push(_381);
}
}
}
}
return _376;
},parseLinks:function(node){
var _383=[];
var _384=node.getElementsByTagName("js");
if(!_384||_384.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _385=_384[0].childNodes;
for(var i=0;i<_385.length;i++){
var _387=_385[i];
if(_387.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _388=_387.nodeName;
if(_388=="folders"){
var _389=_387;
var _38a=_389.childNodes;
for(var pI=0;pI<_38a.length;pI++){
var _38c=_38a[pI];
if(_38c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _38d=_38c.nodeName;
if(_38d=="link"){
var _38e=this.parsePortletElement(_38c);
_383.push(_38e);
}
}
}
}
return _383;
},parsePortletElement:function(node){
var _390=node.getAttribute("name");
var _391=node.getAttribute("path");
return new jetspeed.om.FolderDef(_390,_391);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_392){
this.notifyFinished=_392;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_394,_395){
var _396=this.parsePortlets(data);
var _397=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_395,_396,_397);
}
},notifyFailure:function(type,_399,_39a,_39b){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_39b.toString()+"] url: "+_39a+" type: "+type+jetspeed.url.formatBindError(_399));
},parsList:function(node){
var _39d;
var _39e=node.getElementsByTagName("js");
if(!_39e||_39e.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _39f=_39e[0].childNodes;
for(var i=0;i<_39f.length;i++){
var _3a1=_39f[i];
if(_3a1.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3a2=_3a1.nodeName;
if(_3a2=="resultCount"){
_39d=_3a1.textContent;
}
}
return _39d;
},parsePortlets:function(node){
var _3a4=[];
var _3a5=node.getElementsByTagName("js");
if(!_3a5||_3a5.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _3a6=_3a5[0].childNodes;
for(var i=0;i<_3a6.length;i++){
var _3a8=_3a6[i];
if(_3a8.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3a9=_3a8.nodeName;
if(_3a9=="portlets"){
var _3aa=_3a8;
var _3ab=_3aa.childNodes;
for(var pI=0;pI<_3ab.length;pI++){
var _3ad=_3ab[pI];
if(_3ad.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3ae=_3ad.nodeName;
if(_3ae=="portlet"){
var _3af=this.parsePortletElement(_3ad);
_3a4.push(_3af);
}
}
}
}
return _3a4;
},parsePortletElement:function(node){
var _3b1=node.getAttribute("name");
var _3b2=node.getAttribute("displayName");
var _3b3=node.getAttribute("description");
var _3b4=node.getAttribute("image");
var _3b5=node.getAttribute("count");
return new jetspeed.om.PortletDef(_3b1,_3b2,_3b3,_3b4,_3b5);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3b6,_3b7){
this.portlet=_3b6;
this.changedState=_3b7;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3b8){
if(this.portlet==null){
return;
}
if(_3b8){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3ba,_3bb){
this._setPortletLoading(false);
dojo.lang.mixin(_3bb.portlet.lastSavedWindowState,this.changedState);
var _3bc=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3bc=true;
}
jetspeed.url.checkAjaxApiResponse(_3ba,data,_3bc,("move-portlet ["+_3bb.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3be,_3bf,_3c0){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3c0.entityId+"] url: "+_3bf+" type: "+type+jetspeed.url.formatBindError(_3be));
}};
jetspeed.ui.getPortletWindowChildren=function(_3c1,_3c2,_3c3,_3c4){
if(_3c3||_3c4){
_3c3=true;
}
var _3c5=null;
var _3c6=-1;
if(_3c1){
_3c5=[];
var _3c7=_3c1.childNodes;
if(_3c7!=null&&_3c7.length>0){
for(var i=0;i<_3c7.length;i++){
var _3c9=_3c7[i];
if((!_3c4&&dojo.html.hasClass(_3c9,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3c3&&dojo.html.hasClass(_3c9,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3c5.push(_3c9);
if(_3c2&&_3c9==_3c2){
_3c6=_3c5.length-1;
}
}else{
if(_3c2&&_3c9==_3c2){
_3c5.push(_3c9);
_3c6=_3c5.length-1;
}
}
}
}
}
return {portletWindowNodes:_3c5,matchIndex:_3c6};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3ca){
var _3cb=null;
if(_3ca){
_3cb=new Array();
for(var i=0;i<_3ca.length;i++){
var _3cd=dojo.widget.byNode(_3ca[i]);
if(_3cd){
_3cb.push(_3cd);
}
}
}
return _3cb;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3cf=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3cf.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d1=jetspeed.page.columns[i];
var _3d2=jetspeed.ui.getPortletWindowChildren(_3d1.domNode,null);
var _3d3=jetspeed.ui.getPortletWindowsFromNodes(_3d2.portletWindowNodes);
var _3d4={dumpMsg:""};
if(_3d3!=null){
dojo.lang.forEach(_3d3,function(_3d5){
_3d4.dumpMsg=_3d4.dumpMsg+(_3d4.dumpMsg.length>0?", ":"")+_3d5.portlet.entityId;
});
}
_3d4.dumpMsg="column "+i+": "+_3d4.dumpMsg;
dojo.debug(_3d4.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3d6=jetspeed.ui.getAllPortletWindowWidgets();
var _3d7="";
for(var i=0;i<_3d6.length;i++){
if(i>0){
_3d7+=", ";
}
_3d7+=_3d6[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3d7);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3d9=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3da=jetspeed.ui.getPortletWindowsFromNodes(_3d9.portletWindowNodes);
if(_3da==null){
_3da=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3dc=jetspeed.page.columns[i];
var _3dd=jetspeed.ui.getPortletWindowChildren(_3dc.domNode,null);
var _3de=jetspeed.ui.getPortletWindowsFromNodes(_3dd.portletWindowNodes);
if(_3de!=null){
_3da=_3da.concat(_3de);
}
}
return _3da;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3df,_3e0){
var _3e1=_3df.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3e1==null){
_3e1=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3e1=false;
}
}
var _3e2=dojo.widget.byId(_3df.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3e2){
_3e2.resetWindow(_3df);
}else{
_3e2=jetspeed.ui.createPortletWindowWidget(_3df);
}
if(_3e2){
if(!_3e1||_3e0>=jetspeed.page.columns.length){
_3e2.domNode.style.position="absolute";
var _3e3=document.getElementById(jetspeed.id.DESKTOP);
_3e3.appendChild(_3e2.domNode);
}else{
var _3e4=null;
var _3e5=-1;
var _3e6=_3e0;
if(_3e6!=null&&_3e6>=0&&_3e6<jetspeed.page.columns.length){
_3e5=_3e6;
_3e4=jetspeed.page.columns[_3e5];
}
if(_3e5==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3e8=jetspeed.page.columns[i];
if(!_3e8.domNode.hasChildNodes()){
_3e4=_3e8;
_3e5=i;
break;
}
if(_3e4==null||_3e4.domNode.childNodes.length>_3e8.domNode.childNodes.length){
_3e4=_3e8;
_3e5=i;
}
}
}
if(_3e4){
_3e4.domNode.appendChild(_3e2.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3e9,_3ea){
if(!_3ea){
_3ea={};
}
if(_3e9 instanceof jetspeed.om.Portlet){
_3ea.portlet=_3e9;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3ea,_3e9);
}
var _3eb=dojo.widget.createWidget("jetspeed:PortletWindow",_3ea);
return _3eb;
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3ec=jetspeed.debugWindowReadCookie(true);
var _3ed={};
var _3ee=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3ed[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3ee;
_3ed[jetspeed.id.PORTLET_PROP_WIDTH]=_3ec.width;
_3ed[jetspeed.id.PORTLET_PROP_HEIGHT]=_3ec.height;
_3ed[jetspeed.id.PORTLET_PROP_LEFT]=_3ec.left;
_3ed[jetspeed.id.PORTLET_PROP_TOP]=_3ec.top;
_3ed[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3ed[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3ed[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3ec.windowState;
var _3ef=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3ed);
jetspeed.ui.createPortletWindow(_3ef);
_3ef.retrieveContent(null,null);
var _3f0=dojo.widget.byId(_3ee);
var _3f1=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3f0,"contentChanged");
dojo.event.connect(_3f0,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3f0,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3f0,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3f2){
var _3f3={};
if(_3f2){
_3f3={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _3f4=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_3f4!=null&&_3f4.length>0){
var _3f5=_3f4.split("|");
if(_3f5&&_3f5.length>=4){
_3f3.width=_3f5[0];
_3f3.height=_3f5[1];
_3f3.top=_3f5[2];
_3f3.left=_3f5[3];
if(_3f5.length>4&&_3f5[4]!=null&&_3f5[4].length>0){
_3f3.windowState=_3f5[4];
}
}
}
return _3f3;
};
jetspeed.debugWindowRestore=function(){
var _3f6=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3f7=dojo.widget.byId(_3f6);
if(!_3f7){
return;
}
_3f7.restoreWindow();
};
jetspeed.debugWindow=function(){
var _3f8=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_3f8);
};
jetspeed.debugWindowSave=function(){
var _3f9=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3fa=dojo.widget.byId(_3f9);
if(!_3fa){
return null;
}
if(!_3fa.windowPositionStatic){
var _3fb=_3fa.getCurrentWindowStateForPersistence(false);
var _3fc=_3fb.width;
var _3fd=_3fb.height;
var cTop=_3fb.top;
var _3ff=_3fb.left;
if(_3fa.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _400=_3fa.getLastPositionInfo();
if(_400!=null){
if(_400.height!=null&&_400.height>0){
_3fd=_400.height;
}
}else{
var _401=jetspeed.debugWindowReadCookie(false);
if(_401.height!=null&&_401.height>0){
_3fd=_401.height;
}
}
}
var _402=_3fc+"|"+_3fd+"|"+cTop+"|"+_3ff+"|"+_3fa.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_402,30,"/");
}
};
jetspeed.debugDumpForm=function(_403){
if(!_403){
return null;
}
var _404=_403.toString();
if(_403.name){
_404+=" name="+_403.name;
}
if(_403.id){
_404+=" id="+_403.id;
}
var _405=dojo.io.encodeForm(_403);
_404+=" data="+_405;
return _404;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_406,_407,_408,_409){
if(!_406){
_406={};
}
if(!this.initialized){
var _40a="";
if(jetspeed.altDebugWindowContent){
_40a=jetspeed.altDebugWindowContent();
}else{
_40a+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_407){
_407=new jetspeed.om.BasicContentListener();
}
_407.notifySuccess(_40a,_406.url,_408);
this.initialized=true;
}
}};

