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
var _8b=dojo.widget.byId(jetspeed.id.PG_ED_WID);
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets(true);
}
if(_8b==null){
try{
jetspeed.url.loadingIndicatorShow("loadpageeditor");
_8b=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:jetspeed.id.PG_ED_WID,editorInitiatedFromDesktop:_89});
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
var _8d=dojo.widget.byId(jetspeed.id.PG_ED_WID);
_8d.editModeNormal();
jetspeed.page.editMode=false;
if(!_8d.editorInitiatedFromDesktop){
var _8e=jetspeed.page.getPageUrl(true);
_8e=jetspeed.url.removeQueryParameter(_8e,jetspeed.id.PG_ED_PARAM);
_8e=jetspeed.url.removeQueryParameter(_8e,jetspeed.id.PORTAL_ORIGINATE_PARAMETER);
window.location.href=_8e;
}else{
var _8f=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PG_ED_PARAM);
if(_8f!=null&&_8f=="true"){
var _90=window.location.href;
_90=jetspeed.url.removeQueryParameter(_90,jetspeed.id.PG_ED_PARAM);
window.location.href=_90;
}else{
if(_8d!=null){
_8d.editPageHide();
}
jetspeed.page.syncPageControls();
}
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_91,_92,_93,_94){
if(!_91){
_91={};
}
jetspeed.url.retrieveContent(_91,_92,_93,_94);
}};
jetspeed.om.PortletSelectorContentRetriever=function(){
};
jetspeed.om.PortletSelectorContentRetriever.prototype={getContent:function(_95,_96,_97,_98){
if(!_95){
_95={};
}
var _99="<div widgetId=\""+jetspeed.id.SELECTOR+"\" dojoType=\"PortletDefContainer\"></div>";
if(!_96){
_96=new jetspeed.om.BasicContentListener();
}
_96.notifySuccess(_99,_95.url,_97);
}};
jetspeed.om.PortletSelectorContentListener=function(){
};
jetspeed.om.PortletSelectorContentListener.prototype={notifySuccess:function(_9a,_9b,_9c){
var _9d=this.getPortletWindow();
if(_9d){
_9d.setPortletContent(_9a,renderUrl);
}
},notifyFailure:function(_9e,_9f,_a0,_a1){
dojo.raise("PortletSelectorContentListener notifyFailure url: "+_a0+" type: "+_9e+jetspeed.url.formatBindError(_9f));
}};
jetspeed.om.PageContentListenerUpdate=function(_a2){
this.previousPage=_a2;
};
jetspeed.om.PageContentListenerUpdate.prototype={notifySuccess:function(_a3,_a4,_a5){
dojo.raise("PageContentListenerUpdate notifySuccess - BUT NOT SUPPORTED - url="+_a4);
},notifyFailure:function(_a6,_a7,_a8,_a9){
dojo.raise("PageContentListenerUpdate notifyFailure url: "+_a8+" type: "+_a6+jetspeed.url.formatBindError(_a7));
}};
jetspeed.om.PageContentListenerCreateWidget=function(){
};
jetspeed.om.PageContentListenerCreateWidget.prototype={notifySuccess:function(_aa,_ab,_ac){
_ac.loadFromPSML(_aa);
},notifyFailure:function(_ad,_ae,_af,_b0){
dojo.raise("PageContentListenerCreateWidget error url: "+_af+" type: "+_ad+jetspeed.url.formatBindError(_ae));
}};
jetspeed.om.Id=function(){
var _b1="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_b1.length>0){
_b1+="-";
}
_b1+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _b3 in arguments[i]){
this[_b3]=arguments[i][_b3];
}
}
}
}
this.jetspeed_om_id=_b1;
};
dojo.lang.extend(jetspeed.om.Id,{getId:function(){
return this.jetspeed_om_id;
}});
jetspeed.om.Page=function(_b4,_b5,_b6,_b7){
if(_b4!=null&&_b5!=null){
this.requiredLayoutDecorator=_b4;
this.setPsmlPathFromDocumentUrl(_b5);
this.pageUrlFallback=_b5;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _b6!="undefined"){
this.addToHistory=_b6;
}
if(typeof _b7!="undefined"){
this.editMode=_b7;
}
this.layouts={};
this.columns=[];
this.portlets=[];
this.menus=[];
};
dojo.inherits(jetspeed.om.Page,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _b8=(this.name!=null&&this.name.length>0?this.name:null);
if(!_b8){
this.getPsmlUrl();
_b8=this.psmlPath;
}
return "page-"+_b8;
},setPsmlPathFromDocumentUrl:function(_b9){
var _ba=jetspeed.url.path.AJAX_API;
var _bb=null;
if(_b9==null){
_bb=window.location.pathname;
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _bc=window.location.hash;
if(_bc!=null&&_bc.length>0){
if(_bc.indexOf("#")==0){
_bc=(_bc.length>1?_bc.substring(1):"");
}
if(_bc!=null&&_bc.length>1&&_bc.indexOf("/")==0){
this.psmlPath=jetspeed.url.path.AJAX_API+_bc;
return;
}
}
}
}else{
var _bd=jetspeed.url.parse(_b9);
_bb=_bd.path;
}
var _be=jetspeed.url.path.DESKTOP;
var _bf=_bb.indexOf(_be);
if(_bf!=-1&&_bb.length>(_bf+_be.length)){
_ba=_ba+_bb.substring(_bf+_be.length);
}
this.psmlPath=_ba;
},getPsmlUrl:function(){
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _c0=jetspeed.url.basePortalUrl()+this.psmlPath;
if(jetspeed.prefs.printModeOnly!=null){
_c0=jetspeed.url.addQueryParameter(_c0,"layoutid",jetspeed.prefs.printModeOnly.layout);
_c0=jetspeed.url.addQueryParameter(_c0,"entity",jetspeed.prefs.printModeOnly.entity).toString();
}
return _c0;
},retrievePsml:function(_c1){
if(_c1==null){
_c1=new jetspeed.om.PageContentListenerCreateWidget();
}
var _c2=this.getPsmlUrl();
var _c3="text/xml";
if(jetspeed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_c2);
}
jetspeed.url.retrieveContent({url:_c2,mimetype:_c3},_c1,this,jetspeed.debugContentDumpIds);
},loadFromPSML:function(_c4){
var _c5=this._parsePSML(_c4);
if(_c5==null){
return;
}
var _c6={};
this.columnsStructure=this._layoutCreateModel(_c5,null,_c6,true);
this.rootFragmentId=_c5.id;
var _c7=false;
if(this.editMode){
this.editMode=false;
if(jetspeed.prefs.printModeOnly==null){
_c7=true;
}
}
if(jetspeed.prefs.windowTiling){
this._createColumnsStart(document.getElementById(jetspeed.id.DESKTOP));
}
var _c8=new Array();
var _c9=this.columns.length;
for(var _ca=0;_ca<=this.columns.length;_ca++){
var _cb=null;
if(_ca==_c9){
_cb=_c6["z"];
if(_cb!=null){
_cb.sort(this._loadPortletZIndexCompare);
}
}else{
_cb=_c6[_ca.toString()];
}
if(_cb!=null){
for(var i=0;i<_cb.length;i++){
var _cd=_cb[i].portlet;
_c8.push(_cd);
_cd.createPortletWindow(_ca);
}
}
}
if(jetspeed.prefs.printModeOnly==null){
if(_c8&&_c8.length>0){
jetspeed.doRenderAll(null,_c8,true);
}
this._portletsInitializeWindowState(_c6["z"]);
var _ce=jetspeed.url.getQueryParameter(window.location.href,jetspeed.id.PG_ED_PARAM);
if(_c7||(_ce!=null&&_ce=="true")||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
_c7=false;
if(this.actions!=null&&(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null||this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null)){
_c7=true;
}
}
this.retrieveMenuDeclarations(true,_c7);
this.renderPageControls();
this.syncPageControls();
}else{
var _cd=null;
for(var _cf in this.portlets){
_cd=this.portlets[_cf];
break;
}
if(_cd!=null){
_cd.renderAction(null,jetspeed.prefs.printModeOnly.action);
this._portletsInitializeWindowState(_c6["z"]);
}
}
},_parsePSML:function(_d0){
var _d1=_d0.getElementsByTagName("page");
if(!_d1||_d1.length>1){
dojo.raise("unexpected zero or multiple <page> elements in psml");
}
var _d2=_d1[0];
var _d3=_d2.childNodes;
var _d4=new RegExp("(name|path|profiledPath|title|short-title)");
var _d5=null;
var _d6={};
for(var i=0;i<_d3.length;i++){
var _d8=_d3[i];
if(_d8.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _d9=_d8.nodeName;
if(_d9=="fragment"){
_d5=_d8;
}else{
if(_d9=="defaults"){
this.layoutDecorator=_d8.getAttribute("layout-decorator");
this.portletDecorator=_d8.getAttribute("portlet-decorator");
}else{
if(_d9&&_d9.match(_d4)){
this[jetspeed.purifyIdentifier(_d9,"","lo")]=((_d8&&_d8.firstChild)?_d8.firstChild.nodeValue:null);
}else{
if(_d9=="action"){
this._parsePSMLAction(_d8,_d6);
}
}
}
}
}
this.actions=_d6;
if(_d5==null){
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
var _da=this.getPageUrl();
dojo.undo.browser.addToHistory({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_da);
}
jetspeed.updatePage(_da,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_da);
}
jetspeed.updatePage(_da,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&jetspeed.prefs.ajaxPageNavigation){
var _da=this.getPageUrl();
dojo.undo.browser.setInitialState({back:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_da);
}
jetspeed.updatePage(_da,true);
},forward:function(){
if(jetspeed.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_da);
}
jetspeed.updatePage(_da,true);
},changeUrl:escape(this.getPath())});
}
}
var _db=this._parsePSMLLayoutFragment(_d5,0);
return _db;
},_parsePSMLLayoutFragment:function(_dc,_dd){
var _de=new Array();
var _df=((_dc!=null)?_dc.getAttribute("type"):null);
if(_df!="layout"){
dojo.raise("_parsePSMLLayoutFragment called with non-layout fragment: "+_dc);
return null;
}
var _e0=false;
var _e1=_dc.getAttribute("name");
if(_e1!=null){
_e1=_e1.toLowerCase();
if(_e1.indexOf("noactions")!=-1){
_e0=true;
}
}
var _e2=null,_e3=0;
var _e4={};
var _e5=_dc.childNodes;
var _e6,_e7,_e8,_e9,_ea;
for(var i=0;i<_e5.length;i++){
_e6=_e5[i];
if(_e6.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
_e7=_e6.nodeName;
if(_e7=="fragment"){
_ea=_e6.getAttribute("type");
if(_ea=="layout"){
var _ec=this._parsePSMLLayoutFragment(_e6,i);
if(_ec!=null){
_de.push(_ec);
}
}else{
var _ed=this._parsePSMLProperties(_e6,null);
var _ee=_ed[jetspeed.id.PORTLET_PROP_WINDOW_ICON];
if(_ee==null||_ee.length==0){
_ee=this._parsePSMLIcon(_e6);
if(_ee!=null&&_ee.length>0){
_ed[jetspeed.id.PORTLET_PROP_WINDOW_ICON]=_ee;
}
}
_de.push({id:_e6.getAttribute("id"),type:_ea,name:_e6.getAttribute("name"),properties:_ed,actions:this._parsePSMLActions(_e6,null),currentActionState:this._parsePSMLCurrentActionState(_e6),currentActionMode:this._parsePSMLCurrentActionMode(_e6),decorator:_e6.getAttribute("decorator"),layoutActionsDisabled:_e0,documentOrderIndex:i});
}
}else{
if(_e7=="property"){
if(this._parsePSMLProperty(_e6,_e4)=="sizes"){
if(_e2!=null){
dojo.raise("_parsePSMLLayoutFragment called with layout fragment that contains more than one sizes property: "+_dc);
return null;
}
if(jetspeed.prefs.printModeOnly!=null){
_e2=["100"];
_e3=100;
}else{
_e9=_e6.getAttribute("value");
if(_e9!=null&&_e9.length>0){
_e2=_e9.split(",");
for(var j=0;j<_e2.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_e2[j]=_e2[j].replace(re,"$1");
_e3+=new Number(_e2[j]);
}
}
}
}
}
}
}
_de.sort(this._fragmentRowCompare);
var _f1=new Array();
var _f2=new Array();
for(var i=0;i<_de.length;i++){
if(_de[i].type=="layout"){
_f1.push(i);
}else{
_f2.push(i);
}
}
if(_e2==null){
_e2=new Array();
_e2.push("100");
_e3=100;
}
return {id:_dc.getAttribute("id"),type:_df,name:_dc.getAttribute("name"),decorator:_dc.getAttribute("decorator"),columnSizes:_e2,columnSizesSum:_e3,properties:_e4,fragments:_de,layoutFragmentIndexes:_f1,otherFragmentIndexes:_f2,layoutActionsDisabled:_e0,documentOrderIndex:_dd};
},_parsePSMLActions:function(_f3,_f4){
if(_f4==null){
_f4={};
}
var _f5=_f3.getElementsByTagName("action");
for(var _f6=0;_f6<_f5.length;_f6++){
var _f7=_f5[_f6];
this._parsePSMLAction(_f7,_f4);
}
return _f4;
},_parsePSMLAction:function(_f8,_f9){
var _fa=_f8.getAttribute("id");
if(_fa!=null){
var _fb=_f8.getAttribute("type");
var _fc=_f8.getAttribute("name");
var _fd=_f8.getAttribute("url");
var _fe=_f8.getAttribute("alt");
_f9[_fa.toLowerCase()]={id:_fa,type:_fb,label:_fc,url:_fd,alt:_fe};
}
},_parsePSMLCurrentActionState:function(_ff){
var _100=_ff.getElementsByTagName("state");
if(_100!=null&&_100.length==1&&_100[0].firstChild!=null){
return _100[0].firstChild.nodeValue;
}
return null;
},_parsePSMLCurrentActionMode:function(_101){
var _102=_101.getElementsByTagName("mode");
if(_102!=null&&_102.length==1&&_102[0].firstChild!=null){
return _102[0].firstChild.nodeValue;
}
return null;
},_parsePSMLIcon:function(_103){
var _104=_103.getElementsByTagName("icon");
if(_104!=null&&_104.length==1&&_104[0].firstChild!=null){
return _104[0].firstChild.nodeValue;
}
return null;
},_parsePSMLProperties:function(_105,_106){
if(_106==null){
_106={};
}
var _107=_105.getElementsByTagName("property");
for(var _108=0;_108<_107.length;_108++){
this._parsePSMLProperty(_107[_108],_106);
}
return _106;
},_parsePSMLProperty:function(_109,_10a){
var _10b=_109.getAttribute("name");
var _10c=_109.getAttribute("value");
_10a[_10b]=_10c;
return _10b;
},_fragmentRowCompare:function(_10d,_10e){
var rowA=_10d.documentOrderIndex*1000;
var rowB=_10e.documentOrderIndex*1000;
var _111=_10d.properties["row"];
if(_111!=null){
rowA=_111;
}
var _112=_10e.properties["row"];
if(_112!=null){
rowB=_112;
}
return (rowA-rowB);
},_layoutCreateModel:function(_113,_114,_115,_116){
var _117=this.columns.length;
var _118=this._layoutRegisterAndCreateColumnsModel(_113,_114,_116);
var _119=_118.columnsInLayout;
if(_118.addedLayoutHeaderColumn){
_117++;
}
var _11a=(_119==null?0:_119.length);
if(_113.layoutFragmentIndexes!=null&&_113.layoutFragmentIndexes.length>0){
var _11b=null;
var _11c=0;
if(_113.otherFragmentIndexes!=null&&_113.otherFragmentIndexes.length>0){
_11b=new Array();
}
for(var i=0;i<_113.fragments.length;i++){
var _11e=_113.fragments[i];
}
var _11f=new Array();
for(var i=0;i<_11a;i++){
if(_11b!=null){
_11b.push(null);
}
_11f.push(false);
}
for(var i=0;i<_113.fragments.length;i++){
var _11e=_113.fragments[i];
var _120=i;
if(_11e.properties&&_11e.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
if(_11e.properties[jetspeed.id.PORTLET_PROP_COLUMN]!=null&&_11e.properties[jetspeed.id.PORTLET_PROP_COLUMN]>=0){
_120=_11e.properties[jetspeed.id.PORTLET_PROP_COLUMN];
}
}
if(_120>=_11a){
_120=(_11a>0?(_11a-1):0);
}
var _121=((_11b==null)?null:_11b[_120]);
if(_11e.type=="layout"){
_11f[_120]=true;
if(_121!=null){
this._layoutCreateModel(_121,_119[_120],_115,true);
_11b[_120]=null;
}
this._layoutCreateModel(_11e,_119[_120],_115,false);
}else{
if(_121==null){
_11c++;
var _122={};
dojo.lang.mixin(_122,_113);
_122.fragments=new Array();
_122.layoutFragmentIndexes=new Array();
_122.otherFragmentIndexes=new Array();
_122.documentOrderIndex=_113.fragments[i].documentOrderIndex;
_122.clonedFromRootId=_122.id;
_122.clonedLayoutFragmentIndex=_11c;
_122.columnSizes=["100"];
_122.columnSizesSum=[100];
_122.id=_122.id+"-jsclone_"+_11c;
_11b[_120]=_122;
_121=_122;
}
_121.fragments.push(_11e);
_121.otherFragmentIndexes.push(_121.fragments.length-1);
}
}
if(_11b!=null){
for(var i=0;i<_11a;i++){
var _121=_11b[i];
if(_121!=null){
_11f[i]=true;
this._layoutCreateModel(_121,_119[i],_115,true);
}
}
}
for(var i=0;i<_11a;i++){
if(_11f[i]){
_119[i].columnContainer=true;
}
}
if(_113.otherFragmentIndexes!=null&&_113.otherFragmentIndexes.length>0){
var _123=new Array();
for(var i=0;i<_113.fragments.length;i++){
var _124=true;
for(var j=0;j<_113.otherFragmentIndexes.length;j++){
if(_113.otherFragmentIndexes[j]==i){
_124=false;
break;
}
}
if(_124){
_123.push(_113.fragments[i]);
}
}
_113.fragments=_123;
_113.otherFragmentIndexes=new Array();
}
}
this._layoutCreatePortletsModel(_113,_119,_117,_115);
return _119;
},_layoutFragmentChildCollapse:function(_126,_127){
var _128=false;
if(_127==null){
_127=_126;
}
if(_126.layoutFragmentIndexes!=null&&_126.layoutFragmentIndexes.length>0){
_128=true;
for(var i=0;i<_126.layoutFragmentIndexes.length;i++){
var _12a=_126.fragments[_126.layoutFragmentIndexes[i]];
if(_12a.otherFragmentIndexes!=null&&_12a.otherFragmentIndexes.length>0){
for(var i=0;i<_12a.otherFragmentIndexes.length;i++){
var _12b=_12a.fragments[_12a.otherFragmentIndexes[i]];
_12b.properties[jetspeed.id.PORTLET_PROP_COLUMN]=-1;
_12b.properties[jetspeed.id.PORTLET_PROP_ROW]=-1;
_12b.documentOrderIndex=_127.fragments.length;
_127.fragments.push(_12b);
_127.otherFragIndexes.push(_127.fragments.length);
}
}
this._layoutFragmentChildCollapse(_12a,_127);
}
}
return _128;
},_layoutRegisterAndCreateColumnsModel:function(_12c,_12d,_12e){
this.layouts[_12c.id]=_12c;
var _12f=false;
var _130=new Array();
if(jetspeed.prefs.windowTiling&&_12c.columnSizes.length>0){
var _131=false;
if(jetspeed.browser_IE){
_131=true;
}
if(_12d!=null&&!_12e){
var _132=new jetspeed.om.Column(0,_12c.id,(_131?_12c.columnSizesSum-0.1:_12c.columnSizesSum),this.columns.length,_12c.layoutActionsDisabled);
_132.layoutHeader=true;
this.columns.push(_132);
if(_12d.columnChildren==null){
_12d.columnChildren=new Array();
}
_12d.columnChildren.push(_132);
_12d=_132;
_12f=true;
}
for(var i=0;i<_12c.columnSizes.length;i++){
var size=_12c.columnSizes[i];
if(_131&&i==(_12c.columnSizes.length-1)){
size=size-0.1;
}
var _135=new jetspeed.om.Column(i,_12c.id,size,this.columns.length,_12c.layoutActionsDisabled);
this.columns.push(_135);
if(_12d!=null){
if(_12d.columnChildren==null){
_12d.columnChildren=new Array();
}
_12d.columnChildren.push(_135);
}
_130.push(_135);
}
}
return {columnsInLayout:_130,addedLayoutHeaderColumn:_12f};
},_layoutCreatePortletsModel:function(_136,_137,_138,_139){
if(_136.otherFragmentIndexes!=null&&_136.otherFragmentIndexes.length>0){
var _13a=new Array();
for(var i=0;i<_137.length;i++){
_13a.push(new Array());
}
for(var i=0;i<_136.otherFragmentIndexes.length;i++){
var _13c=_136.fragments[_136.otherFragmentIndexes[i]];
if(jetspeed.debugPortletEntityIdFilter){
if(!dojo.lang.inArray(jetspeed.debugPortletEntityIdFilter,_13c.id)){
_13c=null;
}
}
if(_13c!=null){
var _13d="z";
var _13e=_13c.properties[jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED];
var _13f=jetspeed.prefs.windowTiling;
var _140=jetspeed.prefs.windowHeightExpand;
if(_13e!=null&&jetspeed.prefs.windowTiling&&jetspeed.prefs.printModeOnly==null){
var _141=_13e.split(jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR);
var _142=null,_143=0,_144=null,_145=null,_146=false;
if(_141!=null&&_141.length>0){
var _147=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
for(var _148=0;_148<_141.length;_148++){
_142=_141[_148];
_143=((_142!=null)?_142.length:0);
if(_143>0){
var _149=_142.indexOf(_147);
if(_149>0&&_149<(_143-1)){
_144=_142.substring(0,_149);
_145=_142.substring(_149+1);
_146=((_145=="true")?true:false);
if(_144==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS){
_13f=_146;
}else{
if(_144==jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT){
_140=_146;
}
}
}
}
}
}
}else{
if(!jetspeed.prefs.windowTiling){
_13f=false;
}
}
_13c.properties[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_13f;
_13c.properties[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_140;
if(_13f&&jetspeed.prefs.windowTiling){
var _14a=_13c.properties[jetspeed.id.PORTLET_PROP_COLUMN];
if(_14a==null||_14a==""||_14a<0){
var _14b=-1;
for(var j=0;j<_137.length;j++){
if(_14b==-1||_13a[j].length<_14b){
_14b=_13a[j].length;
_14a=j;
}
}
}else{
if(_14a>=_137.length){
_14a=_137.length-1;
}
}
_13a[_14a].push(_13c.id);
var _14d=_138+new Number(_14a);
_13d=_14d.toString();
}
var _14e=new jetspeed.om.Portlet(_13c.name,_13c.id,null,_13c.properties,_13c.actions,_13c.currentActionState,_13c.currentActionMode,_13c.decorator,_13c.layoutActionsDisabled);
_14e.initialize();
this.putPortlet(_14e);
if(_139[_13d]==null){
_139[_13d]=new Array();
}
_139[_13d].push({portlet:_14e,layout:_136.id});
}
}
}
},_portletsInitializeWindowState:function(_14f){
var _150={};
this.getPortletCurrentColumnRow(null,false,_150);
for(var _151 in this.portlets){
var _152=this.portlets[_151];
var _153=_150[_152.getId()];
if(_153==null&&_14f){
for(var i=0;i<_14f.length;i++){
if(_14f[i].portlet.getId()==_152.getId()){
_153={layout:_14f[i].layout};
break;
}
}
}
if(_153!=null){
_152._initializeWindowState(_153,false);
}else{
dojo.raise("page._portletsInitializeWindowState could not find window state init data for portlet: "+_152.getId());
}
}
},_loadPortletZIndexCompare:function(_155,_156){
var _157=null;
var _158=null;
var _159=null;
_157=_155.portlet._getInitialZIndex();
_158=_156.portlet._getInitialZIndex();
if(_157&&!_158){
return -1;
}else{
if(_158&&!_157){
return 1;
}else{
if(_157==_158){
return 0;
}
}
}
return (_157-_158);
},_createColumnsStart:function(_15a){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _15b=document.createElement("div");
_15b.id=jetspeed.id.COLUMNS;
_15b.setAttribute("id",jetspeed.id.COLUMNS);
for(var _15c=0;_15c<this.columnsStructure.length;_15c++){
var _15d=this.columnsStructure[_15c];
this._createColumns(_15d,_15b);
}
_15a.appendChild(_15b);
},_createColumns:function(_15e,_15f){
_15e.createColumn();
if(_15e.columnChildren!=null&&_15e.columnChildren.length>0){
for(var _160=0;_160<_15e.columnChildren.length;_160++){
var _161=_15e.columnChildren[_160];
this._createColumns(_161,_15e.domNode);
}
}
_15f.appendChild(_15e.domNode);
},_removeColumns:function(_162){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_162){
var _164=jetspeed.ui.getPortletWindowChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_164,function(_165){
_162.appendChild(_165);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _166=dojo.byId(jetspeed.id.COLUMNS);
if(_166){
dojo.dom.removeNode(_166);
}
this.columns=[];
},getPortletCurrentColumnRow:function(_167,_168,_169){
if(!this.columns||this.columns.length==0){
return null;
}
var _16a=null;
var _16b=((_167!=null)?true:false);
var _16c=0;
var _16d=null;
var _16e=null;
var _16f=0;
var _170=false;
for(var _171=0;_171<this.columns.length;_171++){
var _172=this.columns[_171];
var _173=_172.domNode.childNodes;
if(_16e==null||_16e!=_172.getLayoutId()){
_16e=_172.getLayoutId();
_16d=this.layouts[_16e];
if(_16d==null){
dojo.raise("getPortletCurrentColumnRow cannot locate layout id: "+_16e);
return null;
}
_16f=0;
_170=false;
if(_16d.clonedFromRootId==null){
_170=true;
}else{
var _174=this.getColumnFromColumnNode(_172.domNode.parentNode);
if(_174==null){
dojo.raise("getPortletCurrentColumnRow cannot locate parent column for column: "+_172);
return null;
}
_172=_174;
}
}
var _175=null;
for(var _176=0;_176<_173.length;_176++){
var _177=_173[_176];
if(dojo.html.hasClass(_177,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS)||(_168&&dojo.html.hasClass(_177,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))||(_16b&&dojo.html.hasClass(_177,"desktopColumn"))){
_175=(_175==null?0:_175+1);
if((_175+1)>_16f){
_16f=(_175+1);
}
if(_167==null||_177==_167){
var _178={layout:_16e,column:_172.getLayoutColumnIndex(),row:_175};
if(!_170){
_178.layout=_16d.clonedFromRootId;
}
if(_167!=null){
_16a=_178;
break;
}else{
if(_169!=null){
var _179=this.getPortletWindowFromNode(_177);
if(_179==null){
dojo.raise("getPortletCurrentColumnRow cannot locate PortletWindow for node.");
}else{
var _17a=_179.portlet;
if(_17a==null){
dojo.raise("getPortletCurrentColumnRow PortletWindow.portlet is for widgetId: "+_179.widgetId);
}else{
_169[_17a.getId()]=_178;
}
}
}
}
}
}
}
if(_16a!=null){
break;
}
}
return _16a;
},_getPortletArrayByZIndex:function(){
var _17b=this.getPortletArray();
if(!_17b){
return _17b;
}
var _17c=[];
for(var i=0;i<_17b.length;i++){
if(!_17b[i].getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_17c.push(_17b[i]);
}
}
_17c.sort(this._portletZIndexCompare);
return _17c;
},_portletZIndexCompare:function(_17e,_17f){
var _180=null;
var _181=null;
var _182=null;
_182=_17e.getLastSavedWindowState();
_180=_182.zIndex;
_182=_17f.getLastSavedWindowState();
_181=_182.zIndex;
if(_180&&!_181){
return -1;
}else{
if(_181&&!_180){
return 1;
}else{
if(_180==_181){
return 0;
}
}
}
return (_180-_181);
},getPortletDecorationDefault:function(){
var pd=null;
if(djConfig.isDebug&&jetspeed.debug.windowDecorationRandom){
pd=jetspeed.prefs.portletDecorationsAllowed[Math.floor(Math.random()*jetspeed.prefs.portletDecorationsAllowed.length)];
}else{
var _184=this.getPortletDecorator();
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_184)!=-1){
pd=_184;
}else{
pd=jetspeed.prefs.windowDecoration;
}
}
return pd;
},getPortletArrayList:function(){
var _185=new dojo.collections.ArrayList();
for(var _186 in this.portlets){
var _187=this.portlets[_186];
_185.add(_187);
}
return _185;
},getPortletArray:function(){
if(!this.portlets){
return null;
}
var _188=[];
for(var _189 in this.portlets){
var _18a=this.portlets[_189];
_188.push(_18a);
}
return _188;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _18b=[];
for(var _18c in this.portlets){
var _18d=this.portlets[_18c];
_18b.push(_18d.getId());
}
return _18b;
},getPortletByName:function(_18e){
if(this.portlets&&_18e){
for(var _18f in this.portlets){
var _190=this.portlets[_18f];
if(_190.name==_18e){
return _190;
}
}
}
return null;
},getPortlet:function(_191){
if(this.portlets&&_191){
return this.portlets[_191];
}
return null;
},getPortletWindowFromNode:function(_192){
var _193=null;
if(this.portlets&&_192){
for(var _194 in this.portlets){
var _195=this.portlets[_194];
var _196=_195.getPortletWindow();
if(_196!=null){
if(_196.domNode==_192){
_193=_196;
break;
}
}
}
}
return _193;
},putPortlet:function(_197){
if(!_197){
return;
}
if(!this.portlets){
this.portlets=[];
}
this.portlets[_197.entityId]=_197;
},removePortlet:function(_198){
if(!_198||!this.portlets){
return;
}
delete this.portlets[_198.entityId];
},_destroyPortlets:function(){
for(var _199 in this.portlets){
var _19a=this.portlets[_199];
_19a._destroy();
}
},debugLayoutInfo:function(){
var _19b="";
var i=0;
for(var _19d in this.layouts){
if(i>0){
_19b+="\r\n";
}
_19b+="layout["+_19d+"]: "+jetspeed.printobj(this.layouts[_19d],true,true,true);
i++;
}
return _19b;
},debugColumnInfo:function(){
var _19e="";
for(var i=0;i<this.columns.length;i++){
if(i>0){
_19e+="\r\n";
}
_19e+=this.columns[i].toString();
}
return _19e;
},debugDumpLastSavedWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(true);
},debugDumpWindowState:function(){
return this._debugDumpLastSavedWindowStateAllPortlets(false);
},debugPortletActions:function(){
var _1a0=this.getPortletArray();
var _1a1="";
for(var i=0;i<_1a0.length;i++){
var _1a3=_1a0[i];
if(i>0){
_1a1+="\r\n";
}
_1a1+="portlet ["+_1a3.name+"] actions: {";
for(var _1a4 in _1a3.actions){
_1a1+=_1a4+"={"+jetspeed.printobj(_1a3.actions[_1a4],true)+"} ";
}
_1a1+="}";
}
return _1a1;
},displayAllPortlets:function(_1a5){
var _1a6=this.getPortletArray();
for(var i=0;i<_1a6.length;i++){
var _1a8=_1a6[i];
var _1a9=_1a8.getPortletWindow();
if(_1a9){
if(_1a5){
_1a9.domNode.style.display="none";
}else{
_1a9.domNode.style.display="";
}
}
}
},_debugDumpLastSavedWindowStateAllPortlets:function(_1aa){
var _1ab=this.getPortletArray();
var _1ac="";
for(var i=0;i<_1ab.length;i++){
var _1ae=_1ab[i];
if(i>0){
_1ac+="\r\n";
}
var _1af=null;
try{
if(_1aa){
_1af=_1ae.getLastSavedWindowState();
}else{
_1af=_1ae.getCurrentWindowState();
}
}
catch(e){
}
_1ac+="["+_1ae.name+"] "+((_1af==null)?"null":jetspeed.printobj(_1af,true));
}
return _1ac;
},resetWindowLayout:function(){
for(var _1b0 in this.portlets){
var _1b1=this.portlets[_1b0];
_1b1.submitChangedWindowState(false,true);
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
},getColumnFromColumnNode:function(_1b2){
if(_1b2==null){
return null;
}
var _1b3=_1b2.getAttribute("columnIndex");
if(_1b3==null){
return null;
}
var _1b4=new Number(_1b3);
if(_1b4>=0&&_1b4<this.columns.length){
return this.columns[_1b4];
}
return null;
},getColumnIndexContainingNode:function(node){
var _1b6=null;
if(!this.columns){
return _1b6;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_1b6=i;
break;
}
}
return _1b6;
},getColumnContainingNode:function(node){
var _1b9=this.getColumnIndexContainingNode(node);
return ((_1b9!=null&&_1b9>=0)?this.columns[_1b9]:null);
},getDescendantColumns:function(_1ba){
var dMap={};
if(_1ba==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_1ba&&_1ba.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},addNewPortlet:function(_1be,_1bf,_1c0){
var _1c1=new jetspeed.om.Portlet(_1be,_1bf);
if(_1c0){
_1c1.putProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID,_1c0);
}
_1c1.initialize();
this.putPortlet(_1c1);
_1c1.retrieveContent();
},removePortletFromPage:function(_1c2){
var _1c3=new jetspeed.om.PortletAddAjaxApiCallbackContentListener(portletDef,windowWidgetId,false);
var _1c4="?action=remove&id="+escape(portletDef.getPortletName());
var _1c5=jetspeed.page.getPsmlUrl()+_1c4;
var _1c6="text/xml";
var _1c7=new jetspeed.om.Id("removeportlet",{});
jetspeed.url.retrieveContent({url:_1c5,mimetype:_1c6},_1c3,_1c7,jetspeed.debugContentDumpIds);
},putMenu:function(_1c8){
if(!_1c8){
return;
}
var _1c9=(_1c8.getName?_1c8.getName():null);
if(_1c9!=null){
this.menus[_1c9]=_1c8;
}
},getMenu:function(_1ca){
if(_1ca==null){
return null;
}
return this.menus[_1ca];
},removeMenu:function(_1cb){
if(_1cb==null){
return;
}
var _1cc=null;
if(dojo.lang.isString(_1cb)){
_1cc=_1cb;
}else{
_1cc=(_1cb.getName?_1cb.getName():null);
}
if(_1cc!=null){
delete this.menus[_1cc];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _1cd=[];
for(var _1ce in this.menus){
_1cd.push(_1ce);
}
return _1cd;
},retrieveMenuDeclarations:function(_1cf,_1d0){
contentListener=new jetspeed.om.MenusAjaxApiContentListener(_1cf,_1d0);
this.clearMenus();
var _1d1="?action=getmenus";
if(_1cf){
_1d1+="&includeMenuDefs=true";
}
var _1d2=this.getPsmlUrl()+_1d1;
var _1d3="text/xml";
var _1d4=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_1d2,mimetype:_1d3},contentListener,_1d4,jetspeed.debugContentDumpIds);
},retrieveMenu:function(_1d5,_1d6,_1d7){
if(_1d7==null){
_1d7=new jetspeed.om.MenuAjaxApiCallbackContentListener();
}
var _1d8="?action=getmenu&name="+_1d5;
var _1d9=this.getPsmlUrl()+_1d8;
var _1da="text/xml";
var _1db=new jetspeed.om.Id("getmenu-"+_1d5,{page:this,menuName:_1d5,menuType:_1d6});
jetspeed.url.retrieveContent({url:_1d9,mimetype:_1da},_1d7,_1db,jetspeed.debugContentDumpIds);
},syncPageControls:function(){
if(this.actionButtons==null){
return;
}
for(var _1dc in this.actionButtons){
var _1dd=false;
if(_1dc==jetspeed.id.ACTION_NAME_EDIT){
if(!this.editMode){
_1dd=true;
}
}else{
if(_1dc==jetspeed.id.ACTION_NAME_VIEW){
if(this.editMode){
_1dd=true;
}
}else{
if(_1dc==jetspeed.id.ACTION_NAME_ADDPORTLET){
if(!this.editMode){
_1dd=true;
}
}else{
_1dd=true;
}
}
}
if(_1dd){
this.actionButtons[_1dc].style.display="";
}else{
this.actionButtons[_1dc].style.display="none";
}
}
},renderPageControls:function(){
var _1de=[];
if(this.actions!=null){
for(var _1df in this.actions){
if(_1df!=jetspeed.id.ACTION_NAME_HELP){
_1de.push(_1df);
}
if(_1df==jetspeed.id.ACTION_NAME_EDIT){
_1de.push(jetspeed.id.ACTION_NAME_ADDPORTLET);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]==null){
_1de.push(jetspeed.id.ACTION_NAME_VIEW);
}
}
if(this.actions[jetspeed.id.ACTION_NAME_VIEW]!=null){
if(this.actions[jetspeed.id.ACTION_NAME_EDIT]==null){
_1de.push(jetspeed.id.ACTION_NAME_EDIT);
}
}
}
var _1e0=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1e0!=null&&_1de!=null&&_1de.length>0){
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
for(var i=0;i<_1de.length;i++){
var _1df=_1de[i];
var _1e2=document.createElement("div");
_1e2.className="portalPageActionButton";
_1e2.style.backgroundImage="url("+jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/"+_1df+".gif)";
_1e2.actionName=_1df;
this.actionButtons[_1df]=_1e2;
_1e0.appendChild(_1e2);
dojo.event.connect(_1e2,"onclick",this,"pageActionButtonClick");
if(jetspeed.prefs.pageActionButtonTooltip){
var _1e3=null;
if(jetspeed.prefs.desktopActionLabels!=null){
_1e3=jetspeed.prefs.desktopActionLabels[_1df];
}
if(_1e3==null||_1e3.length==0){
_1e3=dojo.string.capitalize(_1df);
}
var _1e4=dojo.widget.createWidget("Tooltip",{isContainer:false,fastMixIn:true,caption:_1e3,connectId:_1e2,delay:"100"});
this.actionButtonTooltips.push(_1e4);
document.body.appendChild(_1e4.domNode);
}
}
}
},_destroyEditPage:function(){
var _1e5=dojo.widget.byId(jetspeed.id.PG_ED_WID);
if(_1e5!=null){
_1e5.editPageDestroy();
}
},_destroyPageControls:function(){
var _1e6=dojo.byId(jetspeed.id.PAGE_CONTROLS);
if(_1e6!=null&&_1e6.childNodes&&_1e6.childNodes.length>0){
for(var i=(_1e6.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_1e6.childNodes[i]);
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
},pageActionProcess:function(_1e9){
if(_1e9==null){
return;
}
if(_1e9==jetspeed.id.ACTION_NAME_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_1e9==jetspeed.id.ACTION_NAME_EDIT){
jetspeed.editPageInitiate();
}else{
if(_1e9==jetspeed.id.ACTION_NAME_VIEW){
jetspeed.editPageTerminate();
}else{
var _1ea=this.getPageAction(_1e9);
alert("pageAction "+_1e9+" : "+_1ea);
if(_1ea==null){
return;
}
if(_1ea.url==null){
return;
}
var _1eb=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/"+_1ea.url;
jetspeed.pageNavigate(_1eb);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_1ed,_1ee){
if(!_1ee){
_1ee=escape(this.getPagePathAndQuery());
}else{
_1ee=escape(_1ee);
}
var _1ef=jetspeed.url.basePortalUrl()+jetspeed.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_1ee;
if(_1ed!=null){
_1ef+="&jslayoutid="+escape(_1ed);
}
jetspeed.changeActionForPortlet(this.rootFragmentId,null,jetspeed.id.ACTION_NAME_EDIT,new jetspeed.om.PageChangeActionContentListener(_1ef));
},setPageModePortletActions:function(_1f0){
if(_1f0==null||_1f0.actions==null){
return;
}
if(_1f0.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]==null){
_1f0.actions[jetspeed.id.ACTION_NAME_REMOVEPORTLET]={id:jetspeed.id.ACTION_NAME_REMOVEPORTLET};
}
},getPageUrl:function(_1f1){
if(this.pageUrl!=null&&!_1f1){
return this.pageUrl;
}
var _1f2=jetspeed.url.path.SERVER+((_1f1)?jetspeed.url.path.PORTAL:jetspeed.url.path.DESKTOP)+this.getPath();
var _1f3=jetspeed.url.parse(_1f2);
var _1f4=null;
if(this.pageUrlFallback!=null){
_1f4=jetspeed.url.parse(this.pageUrlFallback);
}else{
_1f4=jetspeed.url.parse(window.location.href);
}
if(_1f3!=null&&_1f4!=null){
var _1f5=_1f4.query;
if(_1f5!=null&&_1f5.length>0){
var _1f6=_1f3.query;
if(_1f6!=null&&_1f6.length>0){
_1f2=_1f2+"&"+_1f5;
}else{
_1f2=_1f2+"?"+_1f5;
}
}
}
if(!_1f1){
this.pageUrl=_1f2;
}
return _1f2;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var _1f7=this.getPath();
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
this.pagePathAndQuery=_1f7;
return _1f7;
},getPageDirectory:function(_1fc){
var _1fd="/";
var _1fe=(_1fc?this.getRealPath():this.getPath());
if(_1fe!=null){
var _1ff=_1fe.lastIndexOf("/");
if(_1ff!=-1){
if((_1ff+1)<_1fe.length){
_1fd=_1fe.substring(0,_1ff+1);
}else{
_1fd=_1fe;
}
}
}
return _1fd;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_201){
if(!_201){
_201="";
}
if(!jetspeed.url.validateUrlStartsWithHttp(_201)){
return jetspeed.url.path.SERVER+jetspeed.url.path.DESKTOP+_201;
}
return _201;
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
jetspeed.om.Column=function(_202,_203,size,_205,_206){
this.layoutColumnIndex=_202;
this.layoutId=_203;
this.size=size;
this.pageColumnIndex=new Number(_205);
if(typeof _206!="undefined"){
this.layoutActionsDisabled=_206;
}
this.id="jscol_"+_205;
this.domNode=null;
};
dojo.inherits(jetspeed.om.Column,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.Column,{layoutColumnIndex:null,layoutId:null,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_207){
var _208="desktopColumn";
if(this.isStartOfColumnSet()&&this.getPageColumnIndex()>0){
_208="desktopColumn desktopColumnClear";
}
var _209=document.createElement("div");
_209.setAttribute("columnIndex",this.getPageColumnIndex());
_209.style.width=this.size+"%";
if(this.layoutHeader){
_208="desktopColumn desktopLayoutHeader";
}else{
_209.style.minHeight="40px";
}
_209.className=_208;
_209.id=this.getId();
this.domNode=_209;
if(_207!=null){
_207.appendChild(_209);
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
var _20d=dojo.html.getAbsolutePosition(this.domNode,true);
var _20e=dojo.html.getMarginBox(this.domNode);
out+=" dims={"+"left:"+(_20d.x)+", right:"+(_20d.x+_20e.width)+", top:"+(_20d.y)+", bottom:"+(_20d.y+_20e.height)+"}";
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
jetspeed.om.Portlet=function(_20f,_210,_211,_212,_213,_214,_215,_216,_217){
this.name=_20f;
this.entityId=_210;
if(_212){
this.properties=_212;
}else{
this.properties={};
}
if(_213){
this.actions=_213;
}else{
this.actions={};
}
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_214;
this.currentActionMode=_215;
if(_211){
this.contentRetriever=_211;
}
if(_216!=null&&_216.length>0){
if(dojo.lang.indexOf(jetspeed.prefs.portletDecorationsAllowed,_216)!=-1){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_DECORATION,_216);
}
}
this.layoutActionsDisabled=false;
if(typeof _217!="undefined"){
this.layoutActionsDisabled=_217;
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
var _218=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(jetspeed.prefs.windowTiling){
if(_218=="true"){
_218=true;
}else{
if(_218=="false"){
_218=false;
}else{
if(_218!=true&&_218!=false){
_218=true;
}
}
}
}else{
_218=false;
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC,_218);
var _219=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
if(_219=="true"){
_219=true;
}else{
if(_218=="false"){
_219=false;
}else{
if(_219!=true&&_219!=false){
_219=true;
}
}
}
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT,_219);
var _21a=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE);
if(!_21a&&this.name){
var re=(/^[^:]*:*/);
_21a=this.name.replace(re,"");
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_21a);
}
},postParseAnnotateHtml:function(_21c){
if(_21c){
var _21d=_21c;
var _21e=_21d.getElementsByTagName("form");
var _21f=jetspeed.debug.postParseAnnotateHtml;
var _220=jetspeed.debug.postParseAnnotateHtmlDisableAnchors;
if(_21e){
for(var i=0;i<_21e.length;i++){
var _222=_21e[i];
var _223=_222.action;
var _224=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_223);
var _225=_224.operation;
if(_225==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_225==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _226=jetspeed.portleturl.generateJSPseudoUrlActionRender(_224,true);
_222.action=_226;
var _227=new jetspeed.om.ActionRenderFormBind(_222,_224.url,_224.portletEntityId,_225);
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+_225+") for form with action: "+_223);
}
}else{
if(_223==null||_223.length==0){
var _227=new jetspeed.om.ActionRenderFormBind(_222,null,this.entityId,null);
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_223);
}
}
}
}
}
var _228=_21d.getElementsByTagName("a");
if(_228){
for(var i=0;i<_228.length;i++){
var _229=_228[i];
var _22a=_229.href;
var _224=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_22a);
var _22b=null;
if(!_220){
_22b=jetspeed.portleturl.generateJSPseudoUrlActionRender(_224);
}
if(!_22b){
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_22a);
}
}else{
if(_22b==_22a){
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_22a);
}
}else{
if(_21f){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_22a+" with: "+_22b);
}
_229.href=_22b;
}
}
}
}
}
},getPortletWindow:function(){
var _22c=this.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_22c){
return dojo.widget.byId(_22c);
}
return null;
},getCurrentWindowState:function(_22d){
var _22e=this.getPortletWindow();
if(!_22e){
return null;
}
var _22f=_22e.getCurrentWindowStateForPersistence(_22d);
if(!_22d){
if(_22f.layout==null){
_22f.layout=this.lastSavedWindowState.layout;
}
}
return _22f;
},getLastSavedWindowState:function(){
if(!this.lastSavedWindowState){
dojo.raise("portlet.getLastSavedWindowState() is null - portlet ("+this.name+") not properly initialized.");
}
return this.lastSavedWindowState;
},getInitialWindowDimensions:function(_230,_231){
if(!_230){
_230={};
}
var _232=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
var _233=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT);
_230[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=_232;
_230[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=_233;
var _234=this.getProperty("width");
if(!_231&&_234!=null&&_234>0){
_230.width=Math.floor(_234);
}else{
if(_231){
_230.width=-1;
}
}
var _235=this.getProperty("height");
if(!_231&&_235!=null&&_235>0){
_230.height=Math.floor(_235);
}else{
if(_231){
_230.height=-1;
}
}
if(!_232||!jetspeed.prefs.windowTiling){
var _236=this.getProperty("x");
if(!_231&&_236!=null&&_236>=0){
_230.left=Math.floor(((_236>0)?_236:0));
}else{
if(_231){
_230.left=-1;
}
}
var _237=this.getProperty("y");
if(!_231&&_237!=null&&_237>=0){
_230.top=Math.floor(((_237>0)?_237:0));
}else{
_230.top=-1;
}
var _238=this._getInitialZIndex(_231);
if(_238!=null){
_230.zIndex=_238;
}
}
return _230;
},_initializeWindowState:function(_239,_23a){
var _23b=(_239?_239:{});
this.getInitialWindowDimensions(_23b,_23a);
if(jetspeed.debug.initializeWindowState){
var _23c=this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(!_23c||!jetspeed.prefs.windowTiling){
dojo.debug("initializeWindowState ["+this.entityId+"] z="+_23b.zIndex+" x="+_23b.left+" y="+_23b.top+" width="+_23b.width+" height="+_23b.height);
}else{
dojo.debug("initializeWindowState ["+this.entityId+"] column="+_23b.column+" row="+_23b.row+" width="+_23b.width+" height="+_23b.height);
}
}
this.lastSavedWindowState=_23b;
return _23b;
},_getInitialZIndex:function(_23d){
var _23e=null;
var _23f=this.getProperty("z");
if(!_23d&&_23f!=null&&_23f>=0){
_23e=Math.floor(_23f);
}else{
if(_23d){
_23e=-1;
}
}
return _23e;
},_getChangedWindowState:function(_240){
var _241=this.getLastSavedWindowState();
if(_241&&dojo.lang.isEmpty(_241)){
_241=null;
_240=false;
}
var _242=this.getCurrentWindowState(_240);
var _243=_242[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _244=!_243;
if(!_241){
var _245={state:_242,positionChanged:true,extendedPropChanged:true};
if(_244){
_245.zIndexChanged=true;
}
return _245;
}
var _246=false;
var _247=false;
var _248=false;
var _249=false;
for(var _24a in _242){
if(_242[_24a]!=_241[_24a]){
if(_24a==jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC||_24a==jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT){
_246=true;
_248=true;
_247=true;
}else{
if(_24a=="zIndex"){
if(_244){
_246=true;
_249=true;
}
}else{
_246=true;
_247=true;
}
}
}
}
if(_246){
var _245={state:_242,positionChanged:_247,extendedPropChanged:_248};
if(_244){
_245.zIndexChanged=_249;
}
return _245;
}
return null;
},createPortletWindow:function(_24b){
jetspeed.ui.createPortletWindow(this,_24b);
},getPortletUrl:function(_24c){
var _24d=null;
if(_24c&&_24c.url){
_24d=_24c.url;
}else{
if(_24c&&_24c.formNode){
var _24e=_24c.formNode.getAttribute("action");
if(_24e){
_24d=_24e;
}
}
}
if(_24d==null){
_24d=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+jetspeed.page.getPath();
}
if(!_24c.dontAddQueryArgs){
_24d=jetspeed.url.parse(_24d);
_24d=jetspeed.url.addQueryParameter(_24d,"entity",this.entityId,true);
_24d=jetspeed.url.addQueryParameter(_24d,"portlet",this.name,true);
_24d=jetspeed.url.addQueryParameter(_24d,"encoder","desktop",true);
if(_24c.jsPageUrl!=null){
var _24f=_24c.jsPageUrl.query;
if(_24f!=null&&_24f.length>0){
_24d=_24d.toString()+"&"+_24f;
}
}
}
if(_24c){
_24c.url=_24d.toString();
}
return _24d;
},_submitJetspeedAjaxApi:function(_250,_251,_252){
var _253="?action="+_250+"&id="+this.entityId+_251;
var _254=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_253;
var _255="text/xml";
var _256=new jetspeed.om.Id(_250,this.entityId);
_256.portlet=this;
jetspeed.url.retrieveContent({url:_254,mimetype:_255},_252,_256,null);
},submitChangedWindowState:function(_257,_258){
var _259=null;
if(_258){
_259={state:this._initializeWindowState(null,true)};
}else{
_259=this._getChangedWindowState(_257);
}
if(_259){
var _25a=_259.state;
var _25b=_25a[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC];
var _25c=_25a[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT];
var _25d=null;
if(_259.extendedPropChanged){
var _25e=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PROP_SEPARATOR;
var _25f=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_PAIR_SEPARATOR;
_25d=jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_STATICPOS+_25e+_25b.toString();
_25d+=_25f+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED_FITHEIGHT+_25e+_25c.toString();
_25d=escape(_25d);
}
var _260="";
var _261=null;
if(_25b){
_261="moveabs";
if(_25a.column!=null){
_260+="&col="+_25a.column;
}
if(_25a.row!=null){
_260+="&row="+_25a.row;
}
if(_25a.layout!=null){
_260+="&layoutid="+_25a.layout;
}
if(_25a.height!=null){
_260+="&height="+_25a.height;
}
}else{
_261="move";
if(_25a.zIndex!=null){
_260+="&z="+_25a.zIndex;
}
if(_25a.width!=null){
_260+="&width="+_25a.width;
}
if(_25a.height!=null){
_260+="&height="+_25a.height;
}
if(_25a.left!=null){
_260+="&x="+_25a.left;
}
if(_25a.top!=null){
_260+="&y="+_25a.top;
}
}
if(_25d!=null){
_260+="&"+jetspeed.id.PORTLET_PROP_DESKTOP_EXTENDED+"="+_25d;
}
this._submitJetspeedAjaxApi(_261,_260,new jetspeed.om.MoveAjaxApiContentListener(this,_25a));
if(!_257&&!_258){
if(!_25b&&_259.zIndexChanged){
var _262=jetspeed.page.getPortletArrayList();
var _263=dojo.collections.Set.difference(_262,[this]);
if(!_262||!_263||((_263.count+1)!=_262.count)){
dojo.raise("Portlet.submitChangedWindowState invalid conditions for starting auto update");
}else{
if(_263&&_263.count>0){
dojo.lang.forEach(_263.toArray(),function(_264){
if(!_264.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC)){
_264.submitChangedWindowState(true);
}
});
}
}
}else{
if(_25b){
}
}
}
}
},retrieveContent:function(_265,_266,_267){
if(_265==null){
_265=new jetspeed.om.PortletContentListener(this,_267,_266);
}
if(!_266){
_266={};
}
var _268=this;
_268.getPortletUrl(_266);
this.contentRetriever.getContent(_266,_265,_268,jetspeed.debugContentDumpIds);
},setPortletContent:function(_269,_26a,_26b){
var _26c=this.getPortletWindow();
if(_26b!=null&&_26b.length>0){
this.putProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE,_26b);
if(_26c&&!this.loadingIndicatorIsShown()){
_26c.setPortletTitle(_26b);
}
}
if(_26c){
_26c.setPortletContent(_269,_26a);
}
},loadingIndicatorIsShown:function(){
var _26d=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_RENDER);
var _26e=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_ACTION);
var _26f=this._getLoadingActionLabel(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
var _270=this.getPortletWindow();
if(_270&&(_26d||_26e)){
var _271=_270.getPortletTitle();
if(_271&&(_271==_26d||_271==_26e)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_272){
var _273=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_273=jetspeed.prefs.desktopActionLabels[_272];
if(_273!=null&&_273.length==0){
_273=null;
}
}
return _273;
},loadingIndicatorShow:function(_274){
if(_274&&!this.loadingIndicatorIsShown()){
var _275=this._getLoadingActionLabel(_274);
var _276=this.getPortletWindow();
if(_276&&_275){
_276.setPortletTitle(_275);
}
}
},loadingIndicatorHide:function(){
var _277=this.getPortletWindow();
if(_277){
_277.setPortletTitle(this.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_TITLE));
}
},getId:function(){
return this.entityId;
},putProperty:function(name,_279){
this.properties[name]=_279;
},getProperty:function(name){
return this.properties[name];
},removeProperty:function(name){
delete this.properties[name];
},renderAction:function(_27c,_27d){
var _27e=null;
if(_27c!=null){
_27e=this.getAction(_27c);
}
var _27f=_27d;
if(_27f==null&&_27e!=null){
_27f=_27e.url;
}
if(_27f==null){
return;
}
var _280=jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET+"/"+_27f+jetspeed.page.getPath();
if(_27c!=jetspeed.id.ACTION_NAME_PRINT){
this.retrieveContent(null,{url:_280});
}else{
var _281=jetspeed.page.getPageUrl();
_281=jetspeed.url.addQueryParameter(_281,"jsprintmode","true");
_281=jetspeed.url.addQueryParameter(_281,"jsaction",escape(_27e.url));
_281=jetspeed.url.addQueryParameter(_281,"jsentity",this.entityId);
_281=jetspeed.url.addQueryParameter(_281,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_281.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_283,_284,_285){
if(_283){
this.actions=_283;
}else{
this.actions={};
}
this.currentActionState=_284;
this.currentActionMode=_285;
this.syncActions();
},syncActions:function(){
jetspeed.page.setPageModePortletActions(this);
var _286=this.getPortletWindow();
if(_286){
_286.windowActionButtonSync();
}
},_destroy:function(){
var _287=this.getPortletWindow();
if(_287){
_287.closeWindow();
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_28a,_28b){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_28a;
this.submitOperation=_28b;
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
var _290=form.getElementsByTagName("input");
for(var i=0;i<_290.length;i++){
var _291=_290[i];
if(_291.type.toLowerCase()=="image"&&_291.form==form){
this.connect(_291,"onclick","click");
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
},onSubmit:function(_293){
var _294=true;
if(this.isFormSubmitInProgress()){
_294=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_294=false;
}
}
}
return _294;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _296=jetspeed.portleturl.parseContentUrlForDesktopActionRender(this.form.action);
var _297={};
if(_296.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_296.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _298=jetspeed.portleturl.generateJSPseudoUrlActionRender(_296,true);
this.form.action=_298;
this.submitOperation=_296.operation;
this.entityId=_296.portletEntityId;
_297.url=_296.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_297.formFilter=dojo.lang.hitch(this,"formFilter");
_297.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_297),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_297),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_299){
if(_299!=undefined){
this.formSubmitInProgress=_299;
}
return this.formSubmitInProgress;
}});
jetspeed.om.FolderDef=function(_29a,_29b){
this.folderName=_29a;
this.folderPath=_29b;
};
dojo.inherits(jetspeed.om.FolderDef,jetspeed.om.Id);
dojo.lang.extend(jetspeed.om.FolderDef,{folderName:null,folderPath:null,getName:function(){
return this.folderName;
},getPath:function(){
return this.folderPath;
}});
jetspeed.om.PortletDef=function(_29c,_29d,_29e,_29f,_2a0){
this.portletName=_29c;
this.portletDisplayName=_29d;
this.portletDescription=_29e;
this.image=_29f;
this.count=_2a0;
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
jetspeed.om.BasicContentListener.prototype={notifySuccess:function(_2a1,_2a2,_2a3){
var _2a4=_2a3.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID);
if(_2a4){
var _2a5=dojo.widget.byId(_2a4);
if(_2a5){
_2a5.setPortletContent(_2a1,_2a2);
}
}
},notifyFailure:function(type,_2a7,_2a8,_2a9){
dojo.raise("BasicContentListener notifyFailure url: "+_2a8+" type: "+type+jetspeed.url.formatBindError(_2a7));
}};
jetspeed.om.PortletContentListener=function(_2aa,_2ab,_2ac){
this.portlet=_2aa;
this.suppressGetActions=_2ab;
this.submittedFormBindObject=null;
if(_2ac!=null&&_2ac.submitFormBindObject!=null){
this.submittedFormBindObject=_2ac.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletContentListener.prototype={_setPortletLoading:function(_2ad){
if(this.portlet==null){
return;
}
if(_2ad){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2ae,_2af,_2b0,http){
var _2b2=null;
if(http!=null){
_2b2=http.getResponseHeader("JS_PORTLET_TITLE");
if(_2b2!=null){
_2b2=unescape(_2b2);
}
}
_2b0.setPortletContent(_2ae,_2af,_2b2);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_2b0.getId());
}else{
this._setPortletLoading(false);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2b4,_2b5,_2b6){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletContentListener notifyFailure url: "+_2b5+" type: "+type+jetspeed.url.formatBindError(_2b4));
}};
jetspeed.om.PortletActionContentListener=function(_2b7,_2b8){
this.portlet=_2b7;
this.submittedFormBindObject=null;
if(_2b8!=null&&_2b8.submitFormBindObject!=null){
this.submittedFormBindObject=_2b8.submitFormBindObject;
}
this._setPortletLoading(true);
};
jetspeed.om.PortletActionContentListener.prototype={_setPortletLoading:function(_2b9){
if(this.portlet==null){
return;
}
if(_2b9){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_2ba,_2bb,_2bc,http){
var _2be=null;
var _2bf=false;
var _2c0=jetspeed.portleturl.parseContentUrlForDesktopActionRender(_2ba);
if(_2c0.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_2c0.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener "+_2c0.operation+"-url in response body: "+_2ba+"  url: "+_2c0.url+" entity-id: "+_2c0.portletEntityId);
}
_2be=_2c0.url;
}else{
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener other-url in response body: "+_2ba);
}
_2be=_2ba;
if(_2be){
var _2c1=_2be.indexOf(jetspeed.url.basePortalUrl()+jetspeed.url.path.PORTLET);
if(_2c1==-1){
_2bf=true;
window.location.href=_2be;
_2be=null;
}else{
if(_2c1>0){
this._setPortletLoading(false);
dojo.raise("PortletActionContentListener cannot interpret portlet url in action response: "+_2ba);
_2be=null;
}
}
}
}
if(_2be!=null){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("PortletActionContentListener calling doRenderAll="+_2be);
}
jetspeed.doRenderAll(_2be);
}else{
this._setPortletLoading(false);
}
if(!_2bf&&this.portlet){
jetspeed.getActionsForPortlet(this.portlet.entityId);
}
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_2c3,_2c4,_2c5){
this._setPortletLoading(false);
if(this.submittedFormBindObject!=null){
this.submittedFormBindObject.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionContentListener notifyFailure type: "+type+jetspeed.url.formatBindError(_2c3));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _2c6=this.getUrl();
if(_2c6){
if(!jetspeed.prefs.ajaxPageNavigation){
jetspeed.pageNavigate(_2c6,this.getTarget());
}else{
jetspeed.updatePage(_2c6);
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
jetspeed.om.Menu=function(_2c7,_2c8){
this._is_parsed=false;
this.name=_2c7;
this.type=_2c8;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_2c9){
if(!_2c9){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_2c9);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_2cb){
if(!this.hasOptions()){
return null;
}
if(_2cb==0||_2cb>0){
if(_2cb>=this.options.length){
dojo.raise("Menu.getOptionByIndex argument index out of bounds");
}else{
return this.options[_2cb];
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
var _2cd=this.options[i];
if(_2cd instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.MenuAjaxApiContentListener,{notifySuccess:function(data,_2cf,_2d0){
var _2d1=this.parseMenu(data,_2d0.menuName,_2d0.menuType);
_2d0.page.putMenu(_2d1);
},notifyFailure:function(type,_2d3,_2d4,_2d5){
this.notifyCount++;
dojo.raise("MenuAjaxApiContentListener error ["+_2d5.toString()+"] url: "+_2d4+" type: "+type+jetspeed.url.formatBindError(_2d3));
},parseMenu:function(node,_2d7,_2d8){
var menu=null;
var _2da=node.getElementsByTagName("js");
if(!_2da||_2da.length>1){
dojo.raise("unexpected zero or multiple <js> elements in menu xml");
}
var _2db=_2da[0].childNodes;
for(var i=0;i<_2db.length;i++){
var _2dd=_2db[i];
if(_2dd.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2de=_2dd.nodeName;
if(_2de=="menu"){
if(menu!=null){
dojo.raise("unexpected multiple top level <menu> elements in menu xml");
}
menu=this.parseMenuObject(_2dd,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_2d7;
}
if(menu.type==null){
menu.type=_2d8;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _2e1=null;
var _2e2=node.childNodes;
for(var i=0;i<_2e2.length;i++){
var _2e4=_2e2[i];
if(_2e4.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _2e5=_2e4.nodeName;
if(_2e5=="menu"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <menu> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e4,new jetspeed.om.Menu()));
}
}else{
if(_2e5=="option"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <option> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e4,new jetspeed.om.MenuOption()));
}
}else{
if(_2e5=="separator"){
if(mObj.isLeaf()){
dojo.raise("unexpected nested <separator> in <option> or <separator>");
}else{
mObj.addOption(this.parseMenuObject(_2e4,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_2e5){
mObj[_2e5]=((_2e4&&_2e4.firstChild)?_2e4.firstChild.nodeValue:null);
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
jetspeed.om.MenusAjaxApiContentListener=function(_2e6,_2e7){
this.includeMenuDefs=_2e6;
this.initiateEditMode=_2e7;
};
dojo.inherits(jetspeed.om.MenusAjaxApiContentListener,jetspeed.om.MenuAjaxApiContentListener);
dojo.lang.extend(jetspeed.om.MenusAjaxApiContentListener,{notifySuccess:function(data,_2e9,_2ea){
var _2eb=this.getMenuDefs(data,_2e9,_2ea);
for(var i=0;i<_2eb.length;i++){
var mObj=_2eb[i];
_2ea.page.putMenu(mObj);
}
this.notifyFinished(_2ea);
},getMenuDefs:function(data,_2ef,_2f0){
var _2f1=[];
var _2f2=data.getElementsByTagName("menu");
for(var i=0;i<_2f2.length;i++){
var _2f4=_2f2[i].getAttribute("type");
if(this.includeMenuDefs){
_2f1.push(this.parseMenuObject(_2f2[i],new jetspeed.om.Menu(null,_2f4)));
}else{
var _2f5=_2f2[i].firstChild.nodeValue;
_2f1.push(new jetspeed.om.Menu(_2f5,_2f4));
}
}
return _2f1;
},notifyFailure:function(type,_2f7,_2f8,_2f9){
dojo.raise("MenusAjaxApiContentListener error ["+_2f9.toString()+"] url: "+_2f8+" type: "+type+jetspeed.url.formatBindError(_2f7));
},notifyFinished:function(_2fa){
if(this.includeMenuDefs){
jetspeed.notifyRetrieveAllMenusFinished();
}
if(this.initiateEditMode){
jetspeed.editPageInitiate();
}
}});
jetspeed.om.PortletChangeActionContentListener=function(_2fb){
this.portletEntityId=_2fb;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionContentListener,{notifySuccess:function(data,_2fd,_2fe){
if(jetspeed.url.checkAjaxApiResponse(_2fd,data,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._setPortletLoading(false);
}
},_setPortletLoading:function(_2ff){
var _300=jetspeed.page.getPortlet(this.portletEntityId);
if(_300){
if(_2ff){
_300.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_300.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_302,_303,_304){
this._setPortletLoading(false);
dojo.raise("PortletChangeActionContentListener error ["+_304.toString()+"] url: "+_303+" type: "+type+jetspeed.url.formatBindError(_302));
}});
jetspeed.om.PageChangeActionContentListener=function(_305){
this.pageActionUrl=_305;
};
dojo.lang.extend(jetspeed.om.PageChangeActionContentListener,{notifySuccess:function(data,_307,_308){
if(jetspeed.url.checkAjaxApiResponse(_307,data,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_30a,_30b,_30c){
dojo.raise("PageChangeActionContentListener error ["+_30c.toString()+"] url: "+_30b+" type: "+type+jetspeed.url.formatBindError(_30a));
}});
jetspeed.om.PortletActionsContentListener=function(_30d){
this.portletEntityIds=_30d;
this._setPortletLoading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsContentListener,{_setPortletLoading:function(_30e){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _310=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_310){
if(_30e){
_310.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
_310.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_312,_313){
this._setPortletLoading(false);
if(jetspeed.url.checkAjaxApiResponse(_312,data,true,"portlet-actions")){
this.processPortletActionsResponse(data);
}
},processPortletActionsResponse:function(node){
var _315=this.parsePortletActionsResponse(node);
for(var i=0;i<_315.length;i++){
var _317=_315[i];
var _318=_317.id;
var _319=jetspeed.page.getPortlet(_318);
if(_319!=null){
_319.updateActions(_317.actions,_317.currentActionState,_317.currentActionMode);
}
}
},parsePortletActionsResponse:function(node){
var _31b=new Array();
var _31c=node.getElementsByTagName("js");
if(!_31c||_31c.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
return _31b;
}
var _31d=_31c[0].childNodes;
for(var i=0;i<_31d.length;i++){
var _31f=_31d[i];
if(_31f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _320=_31f.nodeName;
if(_320=="portlets"){
var _321=_31f;
var _322=_321.childNodes;
for(var pI=0;pI<_322.length;pI++){
var _324=_322[pI];
if(_324.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _325=_324.nodeName;
if(_325=="portlet"){
var _326=this.parsePortletElement(_324);
if(_326!=null){
_31b.push(_326);
}
}
}
}
}
return _31b;
},parsePortletElement:function(node){
var _328=node.getAttribute("id");
if(_328!=null){
var _329=jetspeed.page._parsePSMLActions(node,null);
var _32a=jetspeed.page._parsePSMLCurrentActionState(node);
var _32b=jetspeed.page._parsePSMLCurrentActionMode(node);
return {id:_328,actions:_329,currentActionState:_32a,currentActionMode:_32b};
}
return null;
},notifyFailure:function(type,_32d,_32e,_32f){
this._setPortletLoading(false);
dojo.raise("PortletActionsContentListener error ["+_32f.toString()+"] url: "+_32e+" type: "+type+jetspeed.url.formatBindError(_32d));
}});
jetspeed.om.PortletAddAjaxApiCallbackContentListener=function(_330,_331,_332){
this.portletDef=_330;
this.windowWidgetId=_331;
this.addToCurrentPage=_332;
};
dojo.lang.extend(jetspeed.om.PortletAddAjaxApiCallbackContentListener,{notifySuccess:function(data,_334,_335){
if(jetspeed.url.checkAjaxApiResponse(_334,data,true,"add-portlet")){
var _336=this.parseAddPortletResponse(data);
if(_336&&this.addToCurrentPage){
jetspeed.page.addNewPortlet(this.portletDef.getPortletName(),_336,this.windowWidgetId);
}
}
},parseAddPortletResponse:function(node){
var _338=null;
var _339=node.getElementsByTagName("js");
if(!_339||_339.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _33a=_339[0].childNodes;
for(var i=0;i<_33a.length;i++){
var _33c=_33a[i];
if(_33c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _33d=_33c.nodeName;
if(_33d=="entity"){
_338=((_33c&&_33c.firstChild)?_33c.firstChild.nodeValue:null);
break;
}
}
return _338;
},notifyFailure:function(type,_33f,_340,_341){
dojo.raise("PortletAddAjaxApiCallbackContentListener error ["+_341.toString()+"] url: "+_340+" type: "+type+jetspeed.url.formatBindError(_33f));
}});
jetspeed.om.PortletSelectorAjaxApiContentListener=function(){
};
dojo.lang.extend(jetspeed.om.PortletSelectorAjaxApiContentListener,{notifySuccess:function(data,_343,_344){
var _345=this.parsePortlets(data);
var _346=dojo.widget.byId(jetspeed.id.SELECTOR);
if(_346!=null){
for(var i=0;i<_345.length;i++){
_346.addChild(_345[i]);
}
}
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_344,_345);
}
},notifyFailure:function(type,_349,_34a,_34b){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_34b.toString()+"] url: "+_34a+" type: "+type+jetspeed.url.formatBindError(_349));
},parsePortlets:function(node){
var _34d=[];
var _34e=node.getElementsByTagName("js");
if(!_34e||_34e.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _34f=_34e[0].childNodes;
for(var i=0;i<_34f.length;i++){
var _351=_34f[i];
if(_351.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _352=_351.nodeName;
if(_352=="portlets"){
var _353=_351;
var _354=_353.childNodes;
for(var pI=0;pI<_354.length;pI++){
var _356=_354[pI];
if(_356.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _357=_356.nodeName;
if(_357=="portlet"){
var _358=this.parsePortletElement(_356);
_34d.push(_358);
}
}
}
}
return _34d;
},parsePortletElement:function(node){
var _35a=node.getAttribute("name");
var _35b=node.getAttribute("displayName");
var _35c=node.getAttribute("description");
var _35d=node.getAttribute("image");
var _35e=0;
return new jetspeed.om.PortletDef(_35a,_35b,_35c,_35d,_35e);
}});
jetspeed.om.FoldersListContentListener=function(_35f){
this.notifyFinished=_35f;
};
dojo.lang.extend(jetspeed.om.FoldersListContentListener,{notifySuccess:function(data,_361,_362){
var _363=this.parseFolders(data);
var _364=this.parsePages(data);
var _365=this.parseLinks(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_362,_363,_364,_365);
}
},notifyFailure:function(type,_367,_368,_369){
dojo.raise("FoldersListContentListener error ["+_369.toString()+"] url: "+_368+" type: "+type+jetspeed.url.formatBindError(_367));
},parseFolders:function(node){
var _36b=[];
var _36c=node.getElementsByTagName("js");
if(!_36c||_36c.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _36d=_36c[0].childNodes;
for(var i=0;i<_36d.length;i++){
var _36f=_36d[i];
if(_36f.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _370=_36f.nodeName;
if(_370=="folders"){
var _371=_36f;
var _372=_371.childNodes;
for(var pI=0;pI<_372.length;pI++){
var _374=_372[pI];
if(_374.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _375=_374.nodeName;
if(_375=="folder"){
var _376=this.parsePortletElement(_374);
_36b.push(_376);
}
}
}
}
return _36b;
},parsePages:function(node){
var _378=[];
var _379=node.getElementsByTagName("js");
if(!_379||_379.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _37a=_379[0].childNodes;
for(var i=0;i<_37a.length;i++){
var _37c=_37a[i];
if(_37c.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _37d=_37c.nodeName;
if(_37d=="folders"){
var _37e=_37c;
var _37f=_37e.childNodes;
for(var pI=0;pI<_37f.length;pI++){
var _381=_37f[pI];
if(_381.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _382=_381.nodeName;
if(_382=="page"){
var _383=this.parsePortletElement(_381);
_378.push(_383);
}
}
}
}
return _378;
},parseLinks:function(node){
var _385=[];
var _386=node.getElementsByTagName("js");
if(!_386||_386.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _387=_386[0].childNodes;
for(var i=0;i<_387.length;i++){
var _389=_387[i];
if(_389.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _38a=_389.nodeName;
if(_38a=="folders"){
var _38b=_389;
var _38c=_38b.childNodes;
for(var pI=0;pI<_38c.length;pI++){
var _38e=_38c[pI];
if(_38e.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _38f=_38e.nodeName;
if(_38f=="link"){
var _390=this.parsePortletElement(_38e);
_385.push(_390);
}
}
}
}
return _385;
},parsePortletElement:function(node){
var _392=node.getAttribute("name");
var _393=node.getAttribute("path");
return new jetspeed.om.FolderDef(_392,_393);
}});
jetspeed.om.PortletSelectorSearchContentListener=function(_394){
this.notifyFinished=_394;
};
dojo.lang.extend(jetspeed.om.PortletSelectorSearchContentListener,{notifySuccess:function(data,_396,_397){
var _398=this.parsePortlets(data);
var _399=this.parsList(data);
if(dojo.lang.isFunction(this.notifyFinished)){
this.notifyFinished(_397,_398,_399);
}
},notifyFailure:function(type,_39b,_39c,_39d){
dojo.raise("PortletSelectorAjaxApiContentListener error ["+_39d.toString()+"] url: "+_39c+" type: "+type+jetspeed.url.formatBindError(_39b));
},parsList:function(node){
var _39f;
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
if(_3a4=="resultCount"){
_39f=_3a3.textContent;
}
}
return _39f;
},parsePortlets:function(node){
var _3a6=[];
var _3a7=node.getElementsByTagName("js");
if(!_3a7||_3a7.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _3a8=_3a7[0].childNodes;
for(var i=0;i<_3a8.length;i++){
var _3aa=_3a8[i];
if(_3aa.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3ab=_3aa.nodeName;
if(_3ab=="portlets"){
var _3ac=_3aa;
var _3ad=_3ac.childNodes;
for(var pI=0;pI<_3ad.length;pI++){
var _3af=_3ad[pI];
if(_3af.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var _3b0=_3af.nodeName;
if(_3b0=="portlet"){
var _3b1=this.parsePortletElement(_3af);
_3a6.push(_3b1);
}
}
}
}
return _3a6;
},parsePortletElement:function(node){
var _3b3=node.getAttribute("name");
var _3b4=node.getAttribute("displayName");
var _3b5=node.getAttribute("description");
var _3b6=node.getAttribute("image");
var _3b7=node.getAttribute("count");
return new jetspeed.om.PortletDef(_3b3,_3b4,_3b5,_3b6,_3b7);
}});
jetspeed.om.MoveAjaxApiContentListener=function(_3b8,_3b9){
this.portlet=_3b8;
this.changedState=_3b9;
this._setPortletLoading(true);
};
jetspeed.om.MoveAjaxApiContentListener.prototype={_setPortletLoading:function(_3ba){
if(this.portlet==null){
return;
}
if(_3ba){
this.portlet.loadingIndicatorShow(jetspeed.id.ACTION_NAME_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3bc,_3bd){
this._setPortletLoading(false);
dojo.lang.mixin(_3bd.portlet.lastSavedWindowState,this.changedState);
var _3be=false;
if(djConfig.isDebug&&jetspeed.debug.submitChangedWindowState){
_3be=true;
}
jetspeed.url.checkAjaxApiResponse(_3bc,data,_3be,("move-portlet ["+_3bd.portlet.entityId+"]"),jetspeed.debug.submitChangedWindowState);
},notifyFailure:function(type,_3c0,_3c1,_3c2){
this._setPortletLoading(false);
dojo.debug("submitChangedWindowState error ["+_3c2.entityId+"] url: "+_3c1+" type: "+type+jetspeed.url.formatBindError(_3c0));
}};
jetspeed.ui.getPortletWindowChildren=function(_3c3,_3c4,_3c5,_3c6){
if(_3c5||_3c6){
_3c5=true;
}
var _3c7=null;
var _3c8=-1;
if(_3c3){
_3c7=[];
var _3c9=_3c3.childNodes;
if(_3c9!=null&&_3c9.length>0){
for(var i=0;i<_3c9.length;i++){
var _3cb=_3c9[i];
if((!_3c6&&dojo.html.hasClass(_3cb,jetspeed.id.PORTLET_WINDOW_STYLE_CLASS))||(_3c5&&dojo.html.hasClass(_3cb,jetspeed.id.PORTLET_WINDOW_GHOST_STYLE_CLASS))){
_3c7.push(_3cb);
if(_3c4&&_3cb==_3c4){
_3c8=_3c7.length-1;
}
}else{
if(_3c4&&_3cb==_3c4){
_3c7.push(_3cb);
_3c8=_3c7.length-1;
}
}
}
}
}
return {portletWindowNodes:_3c7,matchIndex:_3c8};
};
jetspeed.ui.getPortletWindowsFromNodes=function(_3cc){
var _3cd=null;
if(_3cc){
_3cd=new Array();
for(var i=0;i<_3cc.length;i++){
var _3cf=dojo.widget.byNode(_3cc[i]);
if(_3cf){
_3cd.push(_3cf);
}
}
}
return _3cd;
};
jetspeed.ui.dumpColumnWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d1=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_3d1.domNode).width);
}
};
jetspeed.ui.dumpPortletWindowsPerColumn=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3d3=jetspeed.page.columns[i];
var _3d4=jetspeed.ui.getPortletWindowChildren(_3d3.domNode,null);
var _3d5=jetspeed.ui.getPortletWindowsFromNodes(_3d4.portletWindowNodes);
var _3d6={dumpMsg:""};
if(_3d5!=null){
dojo.lang.forEach(_3d5,function(_3d7){
_3d6.dumpMsg=_3d6.dumpMsg+(_3d6.dumpMsg.length>0?", ":"")+_3d7.portlet.entityId;
});
}
_3d6.dumpMsg="column "+i+": "+_3d6.dumpMsg;
dojo.debug(_3d6.dumpMsg);
}
};
jetspeed.ui.dumpPortletWindowWidgets=function(){
var _3d8=jetspeed.ui.getAllPortletWindowWidgets();
var _3d9="";
for(var i=0;i<_3d8.length;i++){
if(i>0){
_3d9+=", ";
}
_3d9+=_3d8[i].widgetId;
}
dojo.debug("PortletWindow widgets: "+_3d9);
};
jetspeed.ui.getAllPortletWindowWidgets=function(){
var _3db=jetspeed.ui.getPortletWindowChildren(dojo.byId(jetspeed.id.DESKTOP),null);
var _3dc=jetspeed.ui.getPortletWindowsFromNodes(_3db.portletWindowNodes);
if(_3dc==null){
_3dc=new Array();
}
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3de=jetspeed.page.columns[i];
var _3df=jetspeed.ui.getPortletWindowChildren(_3de.domNode,null);
var _3e0=jetspeed.ui.getPortletWindowsFromNodes(_3df.portletWindowNodes);
if(_3e0!=null){
_3dc=_3dc.concat(_3e0);
}
}
return _3dc;
};
jetspeed.ui.getDefaultFloatingPaneTemplate=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.html");
};
jetspeed.ui.getDefaultFloatingPaneTemplateCss=function(){
return new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/HtmlFloatingPane.css");
};
jetspeed.ui.createPortletWindow=function(_3e1,_3e2){
var _3e3=_3e1.getProperty(jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC);
if(_3e3==null){
_3e3=(jetspeed.prefs.windowTiling?true:false);
}else{
if(!jetspeed.prefs.windowTiling){
_3e3=false;
}
}
var _3e4=dojo.widget.byId(_3e1.getProperty(jetspeed.id.PORTLET_PROP_WIDGET_ID));
if(_3e4){
_3e4.resetWindow(_3e1);
}else{
_3e4=jetspeed.ui.createPortletWindowWidget(_3e1);
}
if(_3e4){
if(!_3e3||_3e2>=jetspeed.page.columns.length){
_3e4.domNode.style.position="absolute";
var _3e5=document.getElementById(jetspeed.id.DESKTOP);
_3e5.appendChild(_3e4.domNode);
}else{
var _3e6=null;
var _3e7=-1;
var _3e8=_3e2;
if(_3e8!=null&&_3e8>=0&&_3e8<jetspeed.page.columns.length){
_3e7=_3e8;
_3e6=jetspeed.page.columns[_3e7];
}
if(_3e7==-1){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _3ea=jetspeed.page.columns[i];
if(!_3ea.domNode.hasChildNodes()){
_3e6=_3ea;
_3e7=i;
break;
}
if(_3e6==null||_3e6.domNode.childNodes.length>_3ea.domNode.childNodes.length){
_3e6=_3ea;
_3e7=i;
}
}
}
if(_3e6){
_3e6.domNode.appendChild(_3e4.domNode);
}
}
}
};
jetspeed.ui.createPortletWindowWidget=function(_3eb,_3ec){
if(!_3ec){
_3ec={};
}
if(_3eb instanceof jetspeed.om.Portlet){
_3ec.portlet=_3eb;
}else{
jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(_3ec,_3eb);
}
var _3ed=dojo.widget.createWidget("jetspeed:PortletWindow",_3ec);
return _3ed;
};
jetspeed.debugWindowLoad=function(){
if(djConfig.isDebug&&jetspeed.debugInPortletWindow&&dojo.byId(jetspeed.debug.debugContainerId)==null){
var _3ee=jetspeed.debugWindowReadCookie(true);
var _3ef={};
var _3f0=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_POSITION_STATIC]=false;
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_HEIGHT_TO_FIT]=false;
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_DECORATION]=jetspeed.prefs.windowDecoration;
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_TITLE]="Dojo Debug";
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_ICON]="text-x-script.png";
_3ef[jetspeed.id.PORTLET_PROP_WIDGET_ID]=_3f0;
_3ef[jetspeed.id.PORTLET_PROP_WIDTH]=_3ee.width;
_3ef[jetspeed.id.PORTLET_PROP_HEIGHT]=_3ee.height;
_3ef[jetspeed.id.PORTLET_PROP_LEFT]=_3ee.left;
_3ef[jetspeed.id.PORTLET_PROP_TOP]=_3ee.top;
_3ef[jetspeed.id.PORTLET_PROP_EXCLUDE_PCONTENT]=false;
_3ef[jetspeed.id.PORTLET_PROP_CONTENT_RETRIEVER]=new jetspeed.om.DojoDebugContentRetriever();
_3ef[jetspeed.id.PORTLET_PROP_WINDOW_STATE]=_3ee.windowState;
var _3f1=jetspeed.widget.PortletWindow.prototype.staticDefineAsAltInitParameters(null,_3ef);
jetspeed.ui.createPortletWindow(_3f1);
_3f1.retrieveContent(null,null);
var _3f2=dojo.widget.byId(_3f0);
var _3f3=dojo.byId(jetspeed.debug.debugContainerId);
dojo.event.connect("after",dojo.hostenv,"println",_3f2,"contentChanged");
dojo.event.connect(_3f2,"windowActionButtonSync",jetspeed,"debugWindowSave");
dojo.event.connect(_3f2,"endSizing",jetspeed,"debugWindowSave");
dojo.event.connect(_3f2,"endDragging",jetspeed,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_3f4){
var _3f5={};
if(_3f4){
_3f5={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACTION_NAME_MINIMIZE};
}
var _3f6=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_3f6!=null&&_3f6.length>0){
var _3f7=_3f6.split("|");
if(_3f7&&_3f7.length>=4){
_3f5.width=_3f7[0];
_3f5.height=_3f7[1];
_3f5.top=_3f7[2];
_3f5.left=_3f7[3];
if(_3f7.length>4&&_3f7[4]!=null&&_3f7[4].length>0){
_3f5.windowState=_3f7[4];
}
}
}
return _3f5;
};
jetspeed.debugWindowRestore=function(){
var _3f8=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3f9=dojo.widget.byId(_3f8);
if(!_3f9){
return;
}
_3f9.restoreWindow();
};
jetspeed.debugWindow=function(){
var _3fa=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return dojo.widget.byId(_3fa);
};
jetspeed.debugWindowSave=function(){
var _3fb=jetspeed.id.PORTLET_WINDOW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
var _3fc=dojo.widget.byId(_3fb);
if(!_3fc){
return null;
}
if(!_3fc.windowPositionStatic){
var _3fd=_3fc.getCurrentWindowStateForPersistence(false);
var _3fe=_3fd.width;
var _3ff=_3fd.height;
var cTop=_3fd.top;
var _401=_3fd.left;
if(_3fc.windowState==jetspeed.id.ACTION_NAME_MINIMIZE){
var _402=_3fc.getLastPositionInfo();
if(_402!=null){
if(_402.height!=null&&_402.height>0){
_3ff=_402.height;
}
}else{
var _403=jetspeed.debugWindowReadCookie(false);
if(_403.height!=null&&_403.height>0){
_3ff=_403.height;
}
}
}
var _404=_3fe+"|"+_3ff+"|"+cTop+"|"+_401+"|"+_3fc.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_404,30,"/");
}
};
jetspeed.debugDumpForm=function(_405){
if(!_405){
return null;
}
var _406=_405.toString();
if(_405.name){
_406+=" name="+_405.name;
}
if(_405.id){
_406+=" id="+_405.id;
}
var _407=dojo.io.encodeForm(_405);
_406+=" data="+_407;
return _406;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_408,_409,_40a,_40b){
if(!_408){
_408={};
}
if(!this.initialized){
var _40c="";
if(jetspeed.altDebugWindowContent){
_40c=jetspeed.altDebugWindowContent();
}else{
_40c+="<div id=\""+jetspeed.debug.debugContainerId+"\"></div>";
}
if(!_409){
_409=new jetspeed.om.BasicContentListener();
}
_409.notifySuccess(_40c,_408.url,_40a);
this.initialized=true;
var _40d=jetspeed.debugWindow();
var _40e="javascript: void(document.getElementById('"+jetspeed.debug.debugContainerId+"').innerHTML='')";
var _40f="";
for(var i=0;i<20;i++){
_40f+="&nbsp;";
}
var _411=_40d.title+_40f+"<a href=\""+_40e+"\"><span style=\"font-size: xx-small; font-weight: normal\">Clear</span></a>";
_40d.titleBarText.innerHTML=_411;
}
}};

