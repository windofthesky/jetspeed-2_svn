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
if(_2c!=null&&!_2c.css_loaded){
var _2d=jetspeed.prefs.getPortletDecorationBaseUrl(_2b);
_2c.css_loaded=true;
_2c.cssPathCommon=new dojo.uri.Uri(_2d+"/css/styles.css");
_2c.cssPathDesktop=new dojo.uri.Uri(_2d+"/css/desktop.css");
dojo.html.insertCssFile(_2c.cssPathCommon,null,true);
dojo.html.insertCssFile(_2c.cssPathDesktop,null,true);
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
if(_8b==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_8b=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PAGE_EDITOR_WIDGET_ID,editorInitiatedFromDesktop:_89});
var _8c=document.getElementById(jetspeed.id.COLUMNS);
_8c.insertBefore(_8b.domNode,_8c.firstChild);
}
catch(e){
jetspeed.url.loadingIndicatorHide();
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
},_debugDumpLastSavedWindowStateAllPortlets:function(_1a3){
var _1a4=this.getPortletArray();
var _1a5="";
for(var i=0;i<_1a4.length;i++){
var _1a7=_1a4[i];
if(i>0){
_1a5+="\r\n";
}
var _1a8=null;
try{
if(_1a3){
_1a8=_1a7.getLastSavedWindowState();
}else{
_1a8=_1a7.getCurrentWindowState();
}
}
catch(e){
}
_1a5+="["+_1a7.name+"] "+((_1a8==null)?"null":jetspeed.printobj(_1a8,true));
}
return _1a5;
},resetWindowLayout:function(){
for(var _1a9 in this.portlets){
var _1aa=this.portlets[_1a9];
_1aa.submitChangedWindowState(false,true);
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
},getColumnFromColumnNode:function(_1ab){
if(_1ab==null){
return null;
}
var _1ac=_1ab.getAttribute("columnIndex");
if(_1ac==null){
return null;
}
var _1ad=new Number(_1ac);
if(_1ad>=0&&_1ad<this.columns.length){
return this.columns[_1ad];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1af=null;
if(!this.columns){
return _1af;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1af=i;
break;
}
}
return _1af;
},getColumnContainingNode:function(node){
var _1b2=this.getColumnIndexContainingNode(node);
return ((_1b2!=null&&_1b2>=0)?this.columns[_1b2]:null);
},getDescendantColumns:function(_1b3){
var dMap={};
if(_1b3==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1b3&&_1b3.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1b7,_1b8,_1b9){
var _1ba=new jetspeed.om.Portlet(_1b7,_1b8);
if(_1b9){
_1ba.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1b9);
}
_1ba.initialize();
this.putPortlet(_1ba);
_1ba.retrieveContent();
},removePortletFromPage:function(_1bb){
var _1bc=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1bd="?action=remove&id="+escape(portletDef.getPortletName());
var _1be=jetspeed.page.getPsmlUrl()+_1bd;
var _1bf="text/xml";
var _1c0=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1be,mimetype:_1bf},_1bc,_1c0,jetspeed.debugContentDumpIds);
},putMenu:function(_1c1){
if(!_1c1){
return;
}
var _1c2=(_1c1.getName?_1c1.getName():null);
if(_1c2!=null){
this.menus[_1c2]=_1c1;
}
},getMenu:function(_1c3){
if(_1c3==null){
return null;
}
return this.menus[_1c3];
},removeMenu:function(_1c4){
if(_1c4==null){
return;
}
var _1c5=null;
if(dojo.lang.isString(_1c4)){
_1c5=_1c4;
}else{
_1c5=(_1c4.getName?_1c4.getName():null);
}
if(_1c5!=null){
delete this.menus[_1c5];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1c6=[];
for(var _1c7 in this.menus){
_1c6.push(_1c7);
}
return _1c6;
},retrieveMenuDeclarations:function(_1c8,_1c9){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1c8,_1c9);
this.clearMenus();
var _1ca="?action=getmenus";
if(_1c8){
_1ca+="&includeMenuDefs=true";
}
var _1cb=this.getPsmlUrl()+_1ca;
var _1cc="text/xml";
var _1cd=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1cb,mimetype:_1cc},contentListener,_1cd,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1ce,_1cf,_1d0){
if(_1d0==null){
_1d0=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1d1="?action=getmenu&name="+_1ce;
var _1d2=this.getPsmlUrl()+_1d1;
var _1d3="text/xml";
var _1d4=new jetspeed.om.Id("getmenu-"+_1ce,{page:this,menuName:_1ce,menuType:_1cf});
jetspeed.url.retrieveContent({url:_1d2,mimetype:_1d3},_1d0,_1d4,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1d5 in this.actionButtons){
var _1d6=false;
if(_1d5==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1d6=true;
}
}else{
if(_1d5==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1d6=true;
}
}else{
if(_1d5==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1d6=true;
}
}else{
_1d6=true;
}
}
}
if(_1d6){
this.actionButtons[_1d5].style.display="";
}else{
this.actionButtons[_1d5].style.display="none";
}
}
},renderPageControls:function(){
var _1d7=[];
if(this.actions!=null){
for(var _1d8 in this.actions){
if(_1d8!=jetspeed.id.ACTION_NAME_HELP){
_1d7.push(_1d8);
}
if(_1d8==jetspeed.id.ACTION_NAME_EDIT){
_1d7.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1d7.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1d7.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1d9=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1d9!=null&&_1d7!=null&&_1d7.length>0){
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
for(var i=0;i<_1d7.length;i++){
var _1d8=_1d7[i];
var _1db=document.createElement("div");
_1db.className="portalPageActionButton";
_1db.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1d8+".gif)";
_1db.actionName=_1d8;
this.actionButtons[_1d8]=_1db;
_1d9.appendChild(_1db);
dojo.event.connect(_1db,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1dc=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1dc=jetspeed.prefs.desktopActionLabels[_1d8];
}
if(_1dc==null||_1dc.length==0){
_1dc=dojo.string.capitalize(_1d8);
}
var _1dd=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1dc,connectId:_1db,delay:"100"});
this.actionButtonTooltips.push(_1dd);
document.body.appendChild(_1dd.domNode);
}
}
}
},_destroyEditPage:function(){
var _1de=dojo.widget.byId(jetspeed.id.PAGE_EDITOR_WIDGET_ID);
if(_1de!=null){
_1de.editPageDestroy();
}
},_destroyPageControls:function(){
var _1df=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1df!=null&&_1df.childNodes&&_1df.childNodes.length>0){
for(var i=(_1df.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1df.childNodes[i]);
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
},pageActionProcess:function(_1e2){
if(_1e2==null){
return;
}
if(_1e2==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1e2==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1e2==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1e3=this.getPageAction(_1e2);
alert("pageAction "+_1e2+" : "+_1e3);
if(_1e3==null){
return;
}
if(_1e3.url==null){
return;
}
var _1e4=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1e3.url;
jetspeed.pageNavigate(_1e4);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1e6,_1e7){
if(!_1e7){
_1e7=escape(this.getPagePathAndQuery());
}else{
_1e7=escape(_1e7);
}
var _1e8=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1e7;
if(_1e6!=null){
_1e8+="&jslayoutid="+escape(_1e6);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1e8));
},setPageModePortletActions:function(_1e9){
if(_1e9==null||_1e9.actions==null){
return;
}
if(_1e9.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1e9.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1ea){
if(this.pageUrl!=null&&!_1ea){
return this.pageUrl;
}
var _1eb=jetspeed.url.path.SERVER+((_1ea)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1ec=jetspeed.url.parse(_1eb);
var _1ed=null;
if(this.pageUrlFallback!=null){
_1ed=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1ed=jetspeed.url.parse(window.location.href);
}
if(_1ec!=null&&_1ed!=null){
var _1ee=_1ed.query;
if(_1ee!=null&&_1ee.length>0){
var _1ef=_1ec.query;
if(_1ef!=null&&_1ef.length>0){
_1eb=_1eb+"&"+_1ee;
}else{
_1eb=_1eb+"?"+_1ee;
}
}
}
if(!_1ea){
this.pageUrl=_1eb;
}
return _1eb;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1f0=this.getPath();
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
this.pagePathAndQuery=_1f0;
return _1f0;
},getPageDirectory:function(_1f5){
var _1f6="/";
var _1f7=(_1f5?this.getRealPath():this.getPath());
if(_1f7!=null){
var _1f8=_1f7.lastIndexOf("/");
if(_1f8!=-1){
if((_1f8+1)<_1f7.length){
_1f6=_1f7.substring(0,_1f8+1);
}else{
_1f6=_1f7;
}
}
}
return _1f6;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_1fa){
if(!_1fa){
_1fa="";
}
if(!jetspeed.url.validateUrlStartsWithHttp(_1fa)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_1fa;
}
return _1fa;
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
jetspeed.om.Column=function(_1fb,_1fc,size,_1fe,_1ff){
this.layoutColumnIndex=_1fb;
this.layoutId=_1fc;
this.size=size;
this.pageColumnIndex=new Number(_1fe);
if(typeof _1ff!="undefined"){
this.layoutActionsDisabled=_1ff;
}
this.id="jscol_"+_1fe;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_200){
var _201="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_201="desktopColumn desktopColumnClear";
}
var _202=document.createElement("div");
_202.setAttribute("columnIndex",this.getPageColumnIndex());
_202.style.width=this.size+"%";
if(this.layoutHeader){
_201="desktopColumn desktopLayoutHeader";
}else{
_202.style.minHeight="40px";
}
_202.className=_201;
_202.id=this.getId();
this.domNode=_202;
if(_200!=null){
_200.appendChild(_202);
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
var _206=dojo.html.getAbsolutePosition(this.domNode,true);
var _207=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_206.x)+", right:"+(_206.x+_207.width)+", top:"+(_206.y)+", bottom:"+(_206.y+_207.height)+"}";
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
jetspeed.om.Portlet=function(_208,_209,_20a,_20b,_20c,_20d,_20e,_20f,_210){
this.name=_208;
this.entityId=_209;
if(_20b){
this.properties=_20b;
}else{
this.properties={};
}
if(_20c){
this.actions=_20c;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_20d;
this.currentActionMode=_20e;
if(_20a){
this.contentRetriever=_20a;
}
if(_20f!=null&&_20f.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_20f)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_20f);
}
}
this.layoutActionsDisabled=false;
if(typeof _210!="undefined"){
this.layoutActionsDisabled=_210;
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
var _211=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_211=="true"){
_211=true;
}else{
if(_211=="false"){
_211=false;
}else{
if(_211!=true&&_211!=false){
_211=true;
}
}
}
}else{
_211=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_211);
var _212=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_212=="true"){
_212=true;
}else{
if(_211=="false"){
_212=false;
}else{
if(_212!=true&&_212!=false){
_212=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_212);
var _213=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_213&&this.name){
var re=(/^[^:]*:*/);
_213=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_213);
}
},postParseAnnotateHtml:function(_215){
if(_215){
var _216=_215;
var _217=_216.getElementsByTagName("form");
var _218=jetspeed.debug.postParseAnnotateHtml;
var _219=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_217){
for(var i=0;i<_217.length;i++){
var _21b=_217[i];
var _21c=_21b.action;
var _21d=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_21c);
var _21e=_21d.operation;
if(_21e==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_21e==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _21f=jetspeed.portleturl.generateJSPseudoUrlActionRender(_21d,true);
_21b.action=_21f;
var _220=new jetspeed.om.ActionRenderFormBind(_21b,_21d.url,_21d.portletEntityId,_21e);
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_21e+") for form with action: "+_21c);
}
}else{
if(_21c==null||_21c.length==0){
var _220=new jetspeed.om.ActionRenderFormBind(_21b,null,this.entityId,null);
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_21c);
}
}
}
}
}
var _221=_216.getElementsByTagName("a");
if(_221){
for(var i=0;i<_221.length;i++){
var _222=_221[i];
var _223=_222.href;
var _21d=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_223);
var _224=null;
if(!_219){
_224=jetspeed.portleturl.generateJSPseudoUrlActionRender(_21d);
}
if(!_224){
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_223);
}
}else{
if(_224==_223){
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_223);
}
}else{
if(_218){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_223+" with: "+_224);
}
_222.href=_224;
}
}
}
}
}
},getPortletWindow:function(){
var _225=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_225){
return dojo.widget.byId(_225);
}
return null;
},getCurrentWindowState:function(_226){
var _227=this.getPortletWindow();
if(!_227){
return null;
}
var _228=_227.getCurrentWindowStateForPersistence(_226);
if(!_226){
if(_228.layout==null){
_228.layout=this.lastSavedWindowState.layout;
}
}
return _228;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_229,_22a){
if(!_229){
_229={};
}
var _22b=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _22c=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_229[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_22b;
_229[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_22c;
var _22d=this.getProperty("width");
if(!_22a&&_22d!=null&&_22d>0){
_229.width=Math.floor(_22d);
}else{
if(_22a){
_229.width=-1;
}
}
var _22e=this.getProperty("height");
if(!_22a&&_22e!=null&&_22e>0){
_229.height=Math.floor(_22e);
}else{
if(_22a){
_229.height=-1;
}
}
if(!_22b||!jetspeed.prefs.windowTiling){
var _22f=this.getProperty("x");
if(!_22a&&_22f!=null&&_22f>=0){
_229.left=Math.floor(((_22f>0)?_22f:0));
}else{
if(_22a){
_229.left=-1;
}
}
var _230=this.getProperty("y");
if(!_22a&&_230!=null&&_230>=0){
_229.top=Math.floor(((_230>0)?_230:0));
}else{
_229.top=-1;
}
var _231=this._getInitialZIndex(_22a);
if(_231!=null){
_229.zIndex=_231;
}
}
return _229;
},_initializeWindowState:function(_232,_233){
var _234=(_232?_232:{});
this.getInitialWindowDimensions(_234,_233);
if(jetspeed.debug.initializeWindowState){
var _235=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_235||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_234.zIndex+" x="+_234.left+" y="+_234.top+" width="+_234.width+" height="+_234.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_234.column+" row="+_234.row+" width="+_234.width+" height="+_234.height);
}
}
this.lastSavedWindowState=_234;
return _234;
},_getInitialZIndex:function(_236){
var _237=null;
var _238=this.getProperty("z");
if(!_236&&_238!=null&&_238>=0){
_237=Math.floor(_238);
}else{
if(_236){
_237=-1;
}
}
return _237;
},_getChangedWindowState:function(_239){
var _23a=this.getLastSavedWindowState();
if(_23a&&dojo.lang.isEmpty(_23a)){
_23a=null;
_239=false;
}
var _23b=this.getCurrentWindowState(_239);
var _23c=_23b[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _23d=!_23c;
if(!_23a){
var _23e={state:_23b,positionChanged:true,extendedPropChanged:true};
if(_23d){
_23e.zIndexChanged=true;
}
return _23e;
}
var _23f=false;
var _240=false;
var _241=false;
var _242=false;
for(var _243 in _23b){
if(_23b[_243]!=_23a[_243]){
if(_243==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_243==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_23f=true;
_241=true;
_240=true;
}else{
if(_243=="zIndex"){
if(_23d){
_23f=true;
_242=true;
}
}else{
_23f=true;
_240=true;
}
}
}
}
if(_23f){
var _23e={state:_23b,positionChanged:_240,extendedPropChanged:_241};
if(_23d){
_23e.zIndexChanged=_242;
}
return _23e;
}
return null;
},createPortletWindow:function(_244){
jetspeed.ui.createPortletWindow(this,_244);
},getPortletUrl:function(_245){
var _246=null;
if(_245&&_245.url){
_246=_245.url;
}else{
if(_245&&_245.formNode){
var _247=_245.formNode.getAttribute("action");
if(_247){
_246=_247;
}
}
}
if(_246==null){
_246=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_245.dontAddQueryArgs){
_246=jetspeed.url.parse(_246);
_246=jetspeed.url.addQueryParameter(_246,"entity",this.entityId,true);
_246=jetspeed.url.addQueryParameter(_246,"portlet",this.name,true);
_246=jetspeed.url.addQueryParameter(_246,"encoder","desktop",true);
if(_245.jsPageUrl!=null){
var _248=_245.jsPageUrl.query;
if(_248!=null&&_248.length>0){
_246=_246.toString()+"&"+_248;
}
}
}
if(_245){
_245.url=_246.toString();
}
return _246;
},_submitJetspeedAjaxApi:function(_249,_24a,_24b){
var _24c="?action="+_249+"&id="+this.entityId+_24a;
var _24d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_24c;
var _24e="text/xml";
var _24f=new jetspeed.om.Id(_249,this.entityId);
_24f.portlet=this;
jetspeed.url.retrieveContent({url:_24d,mimetype:_24e},_24b,_24f,null);
},submitChangedWindowState:function(_250,_251){
var _252=null;
if(_251){
_252={state:this._initializeWindowState(null,true)};
}else{
_252=this._getChangedWindowState(_250);
}
if(_252){
var _253=_252.state;
var _254=_253[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _255=_253[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _256=null;
if(_252.extendedPropChanged){
var _257=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _258=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_256=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_257+_254.toString();
_256+=_258+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_257+_255.toString();
_256=escape(_256);
}
var _259="";
var _25a=null;
if(_254){
_25a="moveabs";
if(_253.column!=null){
_259+="&col="+_253.column;
}
if(_253.row!=null){
_259+="&row="+_253.row;
}
if(_253.layout!=null){
_259+="&layoutid="+_253.layout;
}
if(_253.height!=null){
_259+="&height="+_253.height;
}
}else{
_25a="move";
if(_253.zIndex!=null){
_259+="&z="+_253.zIndex;
}
if(_253.width!=null){
_259+="&width="+_253.width;
}
if(_253.height!=null){
_259+="&height="+_253.height;
}
if(_253.left!=null){
_259+="&x="+_253.left;
}
if(_253.top!=null){
_259+="&y="+_253.top;
}
}
if(_256!=null){
_259+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_256;
}
this._submitJetspeedAjaxApi(_25a,_259,new jetspeed.om.MoveAjaxApiContentListener(this,_253));
if(!_250&&!_251){
if(!_254&&_252.zIndexChanged){
var _25b=jetspeed.page.getPortletArrayList();
var _25c=dojo.collections.Set.difference(_25b,[this]);
if(!_25b||!_25c||((_25c.count+1)!=_25b.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_25c&&_25c.count>0){
dojo.lang.forEach(_25c.toArray(),function(_25d){
if(!_25d.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_25d.submitChangedWindowState(true);
}
});
}
}
}else{
if(_254){
}
}
}
}
},retrieveContent:function(_25e,_25f,_260){
if(_25e==null){
_25e=new jetspeed.om.PortletContentListener(this,_260,_25f);
}
if(!_25f){
_25f={};
}
var _261=this;
_261.getPortletUrl(_25f);
this.contentRetriever.getContent(_25f,_25e,_261,jetspeed.debugContentDumpIds);
},setPortletContent:function(_262,_263,_264){
var _265=this.getPortletWindow();
if(_264!=null&&_264.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_264);
if(_265&&!this.loadingIndicatorIsShown()){
_265.setPortletTitle(_264);
}
}
if(_265){
_265.setPortletContent(_262,_263);
}
},loadingIndicatorIsShown:function(){
var _266=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _267=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _268=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _269=this.getPortletWindow();
if(_269&&(_266||_267)){
var _26a=_269.getPortletTitle();
if(_26a&&(_26a==_266||_26a==_267)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_26b){
var _26c=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_26c=jetspeed.prefs.desktopActionLabels[_26b];
if(_26c!=null&&_26c.length==0){
_26c=null;
}
}
return _26c;
},loadingIndicatorShow:function(_26d){
if(_26d&&!this.loadingIndicatorIsShown()){
var _26e=this._getLoadingActionLabel(_26d);
var _26f=this.getPortletWindow();
if(_26f&&_26e){
_26f.setPortletTitle(_26e);
}
}
},loadingIndicatorHide:function(){
var _270=this.getPortletWindow();
if(_270){
_270.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_272){
this.properties[name]=_272;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_275,_276){
var _277=null;
if(_275!=null){
_277=this.getAction(_275);
}
var _278=_276;
if(_278==null&&_277!=null){
_278=_277.url;
}
if(_278==null){
return;
}
var _279=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_278+jetspeed.page.getPath();
if(_275!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_279});
}else{
var _27a=jetspeed.page.getPageUrl();
_27a=jetspeed.url.addQueryParameter(_27a,"jsprintmode","true");
_27a=jetspeed.url.addQueryParameter(_27a,"jsaction",escape(_277.url));
_27a=jetspeed.url.addQueryParameter(_27a,"jsentity",this.entityId);
_27a=jetspeed.url.addQueryParameter(_27a,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_27a.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_27c,_27d,_27e){
if(_27c){
this.actions=_27c;
}else{
this.actions={};
}
this.currentActionState=_27d;
this.currentActionMode=_27e;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _27f=this.getPortletWindow();
if(_27f){
_27f.windowActionButtonSync();
}
},_destroy:function(){
var _280=this.getPortletWindow();
if(_280){
_280.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_283,_284){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_283;
this.submitOperation=_284;
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
var _289=form.getElementsByTagName("input");
for(var i=0;i<_289.length;i++){
var _28a=_289[i];
if(_28a.type.toLowerCase()=="image"&&_28a.form==form){
this.connect(_28a,"onclick","click");
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
},onSubmit:function(_28c){
var _28d=true;
if(this.isFormSubmitInProgress()){
_28d=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_28d=false;
}
}
}
return _28d;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _28f=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _290={};
if(_28f.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_28f.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _291=jetspeed.portleturl.generateJSPseudoUrlActionRender(_28f,true);
this.form.action=_291;
this.submitOperation=_28f.operation;
this.entityId=_28f.portletEntityId;
_290.url=_28f.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_290.formFilter=dojo.lang.hitch(this,"formFilter");
_290.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_290),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_290),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_292){
if(_292!=undefined){
this.formSubmitInProgress=_292;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_293,_294){
this.folderName=_293;
this.folderPath=_294;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_295,_296,_297,_298,_299){
this.portletName=_295;
this.portletDisplayName=_296;
this.portletDescription=_297;
this.image=_298;
this.count=_299;
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
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_29a,_29b,_29c){
var _29d=_29c.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_29d){
var _29e=dojo.widget.byId(_29d);
if(_29e){
_29e.setPortletContent(_29a,_29b);
}
}
},notifyFailure:function(type,_2a0,_2a1,_2a2){
dojo.raise("BasicContentListener notifyFailure url: "+_2a1+" type: "+type+jetspeed.url.formatBindError(_2a0));
}};
jetspeed.om.PortletContentListener=function(_2a3,_2a4,_2a5){
this.portlet=_2a3;
this.suppressGetActions=_2a4;
this.submittedFormBindObject=null;
if(_2a5!=null&&_2a5.submitFormBindObject!=null){
this.submittedFormBindObject=_2a5.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_2a6){
if(this.portlet==null){
return;
}
if(_2a6){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2a7,_2a8,_2a9,http){
var _2ab=null;
if(http!=null){
_2ab=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2ab!=null){
_2ab=unescape(_2ab);
}
}
_2a9.setPortletContent(_2a7,_2a8,_2ab);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2a9.getId());
}else{
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2ad,_2ae,_2af){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_2ae+" type: "+type+jetspeed.url.formatBindError(_2ad));
}};
jetspeed.om.PortletActionContentListener=function(_2b0,_2b1){
this.portlet=_2b0;
this.submittedFormBindObject=null;
if(_2b1!=null&&_2b1.submitFormBindObject!=null){
this.submittedFormBindObject=_2b1.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2b2){
if(this.portlet==null){
return;
}
if(_2b2){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2b3,_2b4,_2b5,http){
var _2b7=null;
var _2b8=false;
var _2b9=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2b3);
if(_2b9.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2b9.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2b9.operation+"-url in response body: "+_2b3+"  url: "+_2b9.url+" entity-id: "+_2b9.portletEntityId);
}
_2b7=_2b9.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2b3);
}
_2b7=_2b3;
if(_2b7){
var _2ba=_2b7.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2ba==-1){
_2b8=true;
window.location.href=_2b7;
_2b7=null;
}else{
if(_2ba>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2b3);
_2b7=null;
}
}
}
}
if(_2b7!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2b7);
}
jetspeed.doRenderAll(_2b7);
}else{
this._setPortletLoading(false);
}
if(!_2b8&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2bc,_2bd,_2be){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2bc));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2bf=this.getUrl();
if(_2bf){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2bf,this.getTarget());
}else{
jetspeed.updatePage(_2bf);
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
jetspeed.om.Menu=function(_2c0,_2c1){
this._is_parsed=false;
this.name=_2c0;
this.type=_2c1;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2c2){
if(!_2c2){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2c2);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2c4){
if(!this.hasOptions()){
return null;
}
if(_2c4==0||_2c4>0){
if(_2c4>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2c4];
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
var _2c6=this.options[i];
if(_2c6 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2c8,_2c9){
var _2ca=this.parseMenu(data,_2c9.menuName,_2c9.menuType);
_2c9.page.putMenu(_2ca);
},notifyFailure:function(type,_2cc,_2cd,_2ce){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2ce.toString()+"] url: "+_2cd+" type: "+type+jetspeed.url.formatBindError(_2cc));
},parseMenu:function(node,_2d0,_2d1){
var menu=null;
var _2d3=node.getElementsByTagName("js");
if(!_2d3||_2d3.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2d4=_2d3[0].childNodes;
for(var i=0;i<_2d4.length;i++){
var _2d6=_2d4[i];
if(_2d6.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2d7=_2d6.nodeName;
if(_2d7=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2d6,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2d0;
}
if(menu.type==null){
menu.type=_2d1;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2da=null;
var _2db=node.childNodes;
for(var i=0;i<_2db.length;i++){
var _2dd=_2db[i];
if(_2dd.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2de=_2dd.nodeName;
if(_2de=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2dd,new jetspeed.om.Menu()));
}
}else{
if(_2de=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2dd,new jetspeed.om.MenuOption()));
}
}else{
if(_2de=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2dd,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2de){
mObj[_2de]=((_2dd&&_2dd.firstChild)?_2dd.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2df,_2e0){
this.includeMenuDefs=_2df;
this.initiateEditMode=_2e0;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2e2,_2e3){
var _2e4=this.getMenuDefs(data,_2e2,_2e3);
for(var i=0;i<_2e4.length;i++){
var mObj=_2e4[i];
_2e3.page.putMenu(mObj);
}
this.notifyFinished(_2e3);
},getMenuDefs:function(data,_2e8,_2e9){
var _2ea=[];
var _2eb=data.getElementsByTagName("menu");
for(var i=0;i<_2eb.length;i++){
var _2ed=_2eb[i].getAttribute("type");
if(this.includeMenuDefs){
_2ea.push(this.parseMenuObject(_2eb[i],new jetspeed.om.Menu(null,_2ed)));
}else{
var _2ee=_2eb[i].firstChild.nodeValue;
_2ea.push(new jetspeed.om.Menu(_2ee,_2ed));
}
}
return _2ea;
},notifyFailure:function(type,_2f0,_2f1,_2f2){
dojo.raise("MenusAjaxApiContentListener error ["+_2f2.toString()+"] url: "+_2f1+" type: "+type+jetspeed.url.formatBindError(_2f0));
},notifyFinished:function(_2f3){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
jetspeed.editPageInitiate();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_2f4){
this.portletEntityId=_2f4;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_2f6,_2f7){
if(jetspeed.url.checkAjaxApiResponse(_2f6,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_2f8){
var _2f9=jetspeed.page.getPortlet(this.portletEntityId);
if(_2f9){
if(_2f8){
_2f9.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_2f9.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_2fb,_2fc,_2fd){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_2fd.toString()+"] url: "+_2fc+" type: "+type+jetspeed.url.formatBindError(_2fb));
}});
jetspeed.om.PageChangeActionContentListener=function(_2fe){
this.pageActionUrl=_2fe;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_300,_301){
if(jetspeed.url.checkAjaxApiResponse(_300,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_303,_304,_305){
dojo.raise("PageChangeActionContentListener error ["+_305.toString()+"] url: "+_304+" type: "+type+jetspeed.url.formatBindError(_303));
}});
jetspeed.om.PortletActionsContentListener=function(_306){
this.portletEntityIds=_306;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_307){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _309=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_309){
if(_307){
_309.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_309.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_30b,_30c){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_30b,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _30e=this.parsePortletActionsResponse(node);
for(var i=0;i<_30e.length;i++){
var _310=_30e[i];
var _311=_310.id;
var _312=jetspeed.page.getPortlet(_311);
if(_312!=null){
_312.updateActions(_310.actions,_310.currentActionState,_310.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _314=new Array();
var _315=node.getElementsByTagName("js");
if(!_315||_315.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _314;
}
var _316=_315[0].childNodes;
for(var i=0;i<_316.length;i++){
var _318=_316[i];
if(_318.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _319=_318.nodeName;
if(_319=="portlets"){
var _31a=_318;
var _31b=_31a.childNodes;
for(var pI=0;pI<_31b.length;pI++){
var _31d=_31b[pI];
if(_31d.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _31e=_31d.nodeName;
if(_31e=="portlet"){
var _31f=this.parsePortletElement(_31d);
if(_31f!=null){
_314.push(_31f);
}
}
}
}
}
return _314;
},parsePortletElement:function(node){
var _321=node.getAttribute("id");
if(_321!=null){
var _322=jetspeed.page._parsePSMLActions(node,null);
var _323=jetspeed.page._parsePSMLCurrentActionState(node);
var _324=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_321,actions:_322,currentActionState:_323,currentActionMode:_324};
}
return null;
},notifyFailure:function(type,_326,_327,_328){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_328.toString()+"] url: "+_327+" type: "+type+jetspeed.url.formatBindError(_326));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_329,_32a,_32b){
this.portletDef=_329;
this.windowWidgetId=_32a;
this.addToCurrentPage=_32b;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_32d,_32e){
if(jetspeed.url.checkAjaxApiResponse(_32d,data,true,"add-portlet")){
var _32f=this.parseAddPortletResponse(data);
if(_32f&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_32f,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _331=null;
var _332=node.getElementsByTagName("js");
if(!_332||_332.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _333=_332[0].childNodes;
for(var i=0;i<_333.length;i++){
var _335=_333[i];
if(_335.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _336=_335.nodeName;
if(_336=="entity"){
_331=((_335&&_335.firstChild)?_335.firstChild.nodeValue:null);
break;
}
}
return _331;
},notifyFailure:function(type,_338,_339,_33a){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_33a.toString()+"] url: "+_339+" type: "+type+jetspeed.url.formatBindError(_338));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_33c,_33d){
var _33e=this.parsePortlets(data);
var _33f=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_33f!=null){
for(var i=0;i<_33e.length;i++){
_33f.addChild(_33e[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_33d,_33e);
}
},notifyFailure:function(type,_342,_343,_344){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_344.toString()+"] url: "+_343+" type: "+type+jetspeed.url.formatBindError(_342));
},parsePortlets:function(node){
var _346=[];
var _347=node.getElementsByTagName("js");
if(!_347||_347.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _348=_347[0].childNodes;
for(var i=0;i<_348.length;i++){
var _34a=_348[i];
if(_34a.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _34b=_34a.nodeName;
if(_34b=="portlets"){
var _34c=_34a;
var _34d=_34c.childNodes;
for(var pI=0;pI<_34d.length;pI++){
var _34f=_34d[pI];
if(_34f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _350=_34f.nodeName;
if(_350=="portlet"){
var _351=this.parsePortletElement(_34f);
_346.push(_351);
}
}
}
}
return _346;
},parsePortletElement:function(node){
var _353=node.getAttribute("name");
var _354=node.getAttribute("displayName");
var _355=node.getAttribute("description");
var _356=node.getAttribute("image");
var _357=0;
return new jetspeed.om.PortletDef(_353,_354,_355,_356,_357);
}});
jetspeed.om.FoldersListContentListener=function(_358){
this.notifyFinished=_358;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_35a,_35b){
var _35c=this.parseFolders(data);
var _35d=this.parsePages(data);
var _35e=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_35b,_35c,_35d,_35e);
}
},notifyFailure:function(type,_360,_361,_362){
dojo.raise("FoldersListContentListener error ["+_362.toString()+"] url: "+_361+" type: "+type+jetspeed.url.formatBindError(_360));
},parseFolders:function(node){
var _364=[];
var _365=node.getElementsByTagName("js");
if(!_365||_365.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _366=_365[0].childNodes;
for(var i=0;i<_366.length;i++){
var _368=_366[i];
if(_368.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _369=_368.nodeName;
if(_369=="folders"){
var _36a=_368;
var _36b=_36a.childNodes;
for(var pI=0;pI<_36b.length;pI++){
var _36d=_36b[pI];
if(_36d.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _36e=_36d.nodeName;
if(_36e=="folder"){
var _36f=this.parsePortletElement(_36d);
_364.push(_36f);
}
}
}
}
return _364;
},parsePages:function(node){
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
if(_37b=="page"){
var _37c=this.parsePortletElement(_37a);
_371.push(_37c);
}
}
}
}
return _371;
},parseLinks:function(node){
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
if(_388=="link"){
var _389=this.parsePortletElement(_387);
_37e.push(_389);
}
}
}
}
return _37e;
},parsePortletElement:function(node){
var _38b=node.getAttribute("name");
var _38c=node.getAttribute("path");
return new jetspeed.om.FolderDef(_38b,_38c);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_38d){
this.notifyFinished=_38d;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_38f,_390){
var _391=this.parsePortlets(data);
var _392=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_390,_391,_392);
}
},notifyFailure:function(type,_394,_395,_396){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_396.toString()+"] url: "+_395+" type: "+type+jetspeed.url.formatBindError(_394));
},parsList:function(node){
var _398;
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
if(_39d=="resultCount"){
_398=_39c.textContent;
}
}
return _398;
},parsePortlets:function(node){
var _39f=[];
var _3a0=node.getElementsByTagName("js");
if(!_3a0||_3a0.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _3a1=_3a0[0].childNodes;
for(var i=0;i<_3a1.length;i++){
var _3a3=_3a1[i];
if(_3a3.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3a4=_3a3.nodeName;
if(_3a4=="portlets"){
var _3a5=_3a3;
var _3a6=_3a5.childNodes;
for(var pI=0;pI<_3a6.length;pI++){
var _3a8=_3a6[pI];
if(_3a8.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3a9=_3a8.nodeName;
if(_3a9=="portlet"){
var _3aa=this.parsePortletElement(_3a8);
_39f.push(_3aa);
}
}
}
}
return _39f;
},parsePortletElement:function(node){
var _3ac=node.getAttribute("name");
var _3ad=node.getAttribute("displayName");
var _3ae=node.getAttribute("description");
var _3af=node.getAttribute("image");
var _3b0=node.getAttribute("count");
return new jetspeed.om.PortletDef(_3ac,_3ad,_3ae,_3af,_3b0);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3b1,_3b2){
this.portlet=_3b1;
this.changedState=_3b2;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3b3){
if(this.portlet==null){
return;
}
if(_3b3){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3b5,_3b6){
this._setPortletLoading(false);
dojo.lang.mixin(_3b6.portlet.lastSavedWindowState,this.changedState);
var _3b7=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3b7=true;
}
jetspeed.url.checkAjaxApiResponse(_3b5,data,_3b7,("move-portlet ["+_3b6.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3b9,_3ba,_3bb){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3bb.entityId+"] url: "+_3ba+" type: "+type+jetspeed.url.formatBindError(_3b9));
}};
jetspeed.ui.getPortletWindowChildren=function(_3bc,_3bd,_3be,_3bf){
if(_3be||_3bf){
_3be=true;
}
var _3c0=null;
var _3c1=-1;
if(_3bc){
_3c0=[];
var _3c2=_3bc.childNodes;
if(_3c2!=null&&_3c2.length>0){
for(var i=0;i<_3c2.length;i++){
var _3c4=_3c2[i];
if((!_3bf&&dojo.html.hasClass(_3c4,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3be&&dojo.html.hasClass(_3c4,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3c0.push(_3c4);
if(_3bd&&_3c4==_3bd){
_3c1=_3c0.length-1;
}
}else{
if(_3bd&&_3c4==_3bd){
_3c0.push(_3c4);
_3c1=_3c0.length-1;
}
}
}
}
}
return {portletWindowNodes:_3c0,matchIndex:_3c1};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3c5){
var _3c6=null;
if(_3c5){
_3c6=new Array();
for(var i=0;i<_3c5.length;i++){
var _3c8=dojo.widget.byNode(_3c5[i]);
if(_3c8){
_3c6.push(_3c8);
}
}
}
return _3c6;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3ca=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3ca.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3cc=jetspeed.page.columns[i];
var _3cd=jetspeed.ui.getPortletWindowChildren(_3cc.domNode,null);
var _3ce=jetspeed.ui.getPortletWindowsFromNodes(_3cd.portletWindowNodes);
var _3cf={dumpMsg:""};
if(_3ce!=null){
dojo.lang.forEach(_3ce,function(_3d0){
_3cf.dumpMsg=_3cf.dumpMsg+(_3cf.dumpMsg.length>0?", ":"")+_3d0.portlet.entityId;
});
}
_3cf.dumpMsg="column "+i+": "+_3cf.dumpMsg;
dojo.debug(_3cf.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3d1=jetspeed.ui.getAllPortletWindowWidgets();
var _3d2="";
for(var i=0;i<_3d1.length;i++){
if(i>0){
_3d2+=", ";
}
_3d2+=_3d1[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3d2);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3d4=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3d5=jetspeed.ui.getPortletWindowsFromNodes(_3d4.portletWindowNodes);
if(_3d5==null){
_3d5=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d7=jetspeed.page.columns[i];
var _3d8=jetspeed.ui.getPortletWindowChildren(_3d7.domNode,null);
var _3d9=jetspeed.ui.getPortletWindowsFromNodes(_3d8.portletWindowNodes);
if(_3d9!=null){
_3d5=_3d5.concat(_3d9);
}
}
return _3d5;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3da,_3db){
var _3dc=_3da.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3dc==null){
_3dc=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3dc=false;
}
}
var _3dd=dojo.widget.byId(_3da.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3dd){
_3dd.resetWindow(_3da);
}else{
_3dd=jetspeed.ui.createPortletWindowWidget(_3da);
}
if(_3dd){
if(!_3dc||_3db>=jetspeed.page.columns.length){
_3dd.domNode.style.position="absolute";
var _3de=document.getElementById(jetspeed.id.DESKTOP);
_3de.appendChild(_3dd.domNode);
}else{
var _3df=null;
var _3e0=-1;
var _3e1=_3db;
if(_3e1!=null&&_3e1>=0&&_3e1<jetspeed.page.columns.length){
_3e0=_3e1;
_3df=jetspeed.page.columns[_3e0];
}
if(_3e0==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3e3=jetspeed.page.columns[i];
if(!_3e3.domNode.hasChildNodes()){
_3df=_3e3;
_3e0=i;
break;
}
if(_3df==null||_3df.domNode.childNodes.length>_3e3.domNode.childNodes.length){
_3df=_3e3;
_3e0=i;
}
}
}
if(_3df){
_3df.domNode.appendChild(_3dd.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3e4,_3e5){
if(!_3e5){
_3e5={};
}
if(_3e4 instanceof jetspeed.om.Portlet){
_3e5.portlet=_3e4;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3e5,_3e4);
}
var _3e6=dojo.widget.createWidget("jetspeed:PortletWindow",_3e5);
return _3e6;
};
jetspeed.ui.fadeIn=function(_3e7,_3e8,_3e9){
jetspeed.ui.fade(_3e7,_3e8,_3e9,0,1);
};
jetspeed.ui.fadeOut=function(_3ea,_3eb,_3ec){
jetspeed.ui.fade(_3ea,_3eb,"hidden",1,0,_3ec);
};
jetspeed.ui.fade=function(_3ed,_3ee,_3ef,_3f0,_3f1,_3f2){
if(_3ed.length>0){
for(var i=0;i<_3ed.length;i++){
dojo.lfx.html._makeFadeable(_3ed[i]);
if(_3ef!="none"){
_3ed[i].style.visibility=_3ef;
}
}
var anim=new dojo.animation.Animation(new dojo.math.curves.Line([_3f0],[_3f1]),_3ee,0);
dojo.event.connect(anim,"onAnimate",function(e){
for(var mi=0;mi<_3ed.length;mi++){
dojo.html.setOpacity(_3ed[mi],e.x);
}
});
if(_3ef=="hidden"){
dojo.event.connect(anim,"onEnd",function(e){
for(var mi=0;mi<_3ed.length;mi++){
_3ed[mi].style.visibility=_3ef;
}
if(_3f2){
for(var mi=0;mi<_3f2.length;mi++){
_3f2[mi].style.display="none";
}
}
});
}
anim.play(true);
}
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3f9=jetspeed.debugWindowReadCookie(true);
var _3fa={};
var _3fb=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3fa[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3fb;
_3fa[jetspeed.id.PORTLET_PROP_WIDTH]=_3f9.width;
_3fa[jetspeed.id.PORTLET_PROP_HEIGHT]=_3f9.height;
_3fa[jetspeed.id.PORTLET_PROP_LEFT]=_3f9.left;
_3fa[jetspeed.id.PORTLET_PROP_TOP]=_3f9.top;
_3fa[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3fa[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3fa[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3f9.windowState;
var _3fc=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3fa);
jetspeed.ui.createPortletWindow(_3fc);
_3fc.retrieveContent(null,null);
var _3fd=dojo.widget.byId(_3fb);
var _3fe=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3fd,"contentChanged");
dojo.event.connect(_3fd,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3fd,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3fd,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3ff){
var _400={};
if(_3ff){
_400={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _401=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_401!=null&&_401.length>0){
var _402=_401.split("|");
if(_402&&_402.length>=4){
_400.width=_402[0];
_400.height=_402[1];
_400.top=_402[2];
_400.left=_402[3];
if(_402.length>4&&_402[4]!=null&&_402[4].length>0){
_400.windowState=_402[4];
}
}
}
return _400;
};
jetspeed.debugWindowRestore=function(){
var _403=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _404=dojo.widget.byId(_403);
if(!_404){
return;
}
_404.restoreWindow();
};
jetspeed.debugWindow=function(){
var _405=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_405);
};
jetspeed.debugWindowSave=function(){
var _406=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _407=dojo.widget.byId(_406);
if(!_407){
return null;
}
if(!_407.windowPositionStatic){
var _408=_407.getCurrentWindowStateForPersistence(false);
var _409=_408.width;
var _40a=_408.height;
var cTop=_408.top;
var _40c=_408.left;
if(_407.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _40d=_407.getLastPositionInfo();
if(_40d!=null){
if(_40d.height!=null&&_40d.height>0){
_40a=_40d.height;
}
}else{
var _40e=jetspeed.debugWindowReadCookie(false);
if(_40e.height!=null&&_40e.height>0){
_40a=_40e.height;
}
}
}
var _40f=_409+"|"+_40a+"|"+cTop+"|"+_40c+"|"+_407.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_40f,30,"/");
}
};
jetspeed.debugDumpForm=function(_410){
if(!_410){
return null;
}
var _411=_410.toString();
if(_410.name){
_411+=" name="+_410.name;
}
if(_410.id){
_411+=" id="+_410.id;
}
var _412=dojo.io.encodeForm(_410);
_411+=" data="+_412;
return _411;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_413,_414,_415,_416){
if(!_413){
_413={};
}
if(!this.initialized){
var _417="";
if(jetspeed.altDebugWindowContent){
_417=jetspeed.altDebugWindowContent();
}else{
_417+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_414){
_414=new jetspeed.om.BasicContentListener();
}
_414.notifySuccess(_417,_413.url,_415);
this.initialized=true;
}
}};

