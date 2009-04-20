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
var _35=jetspeed;
var _36=document.createElement(_34.tagName);
var _37=_34.attributes;
for(var i=0;i<_37.length;i++){
var _39=_37.item(i);
if(_39&&(_39.nodeValue)&&(typeof _39.nodeValue!="object")){
_36.setAttribute(_39.nodeName,_39.nodeValue);
}
}
return _36;
};
jetspeed.contributeHeadElements=function(_3a){
var _3b=jetspeed;
var _3c=[];
var _3d=[];
var _3e=_3b.getHead().childNodes;
if(_3e){
var _3f=/^header\.dojo\.requires/;
var _40=/^header\.dojo\./;
for(var i=0;i<_3e.length;i++){
if(_3e[i].nodeType==dojo.dom.ELEMENT_NODE){
_3c.push(_3e[i]);
if(_3e[i].tagName=="SCRIPT"){
var _42=_3e[i].getAttribute("org.apache.portals.portal.page.head.element.contribution.merge.hint");
if(_42=="header.dojo.parameters"){
_42="header.dojo.config";
}else{
if(_3f.test(_42)){
_42="header.dojo.requires";
}
}
if(_42&&_40.test(_42)){
if(!_3d[_42]){
_3d[_42]=[];
}
_3d[_42].push(_3e[i]);
}
}
}
}
}
var _43=_3a.childNodes;
var _44=0;
for(var i=0;i<_43.length;i++){
var _45=_43.item(i);
if(!_45||_45.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var id=_45.getAttribute("id");
if(!id){
id=_45.getAttribute("ID");
}
if(!id){
id=_45.getAttribute("Id");
}
if(!id){
id=_45.getAttribute("iD");
}
var _42=_45.getAttribute("org.apache.portals.portal.page.head.element.contribution.merge.hint");
var _47=_45.tagName;
var _48=false;
if(id){
for(var j=0;j<_3c.length;j++){
if(id==_3c[j].id){
_48=true;
_44=j+1;
break;
}
}
}
if(!_48){
if(_3d[_42]){
if(_3b.UAie){
var _4a=_3d[_42];
var _4b=_45.text.split(/\n/);
for(var i=0;i<_4b.length;i++){
var _4c=false;
for(var j=0;j<_4a.length;j++){
var _4d=_4a[j].text;
if(_4d&&_4d.indexOf(_4b[i])>=0){
_4c=true;
break;
}
}
if(!_4c){
var _4e=_4a[_4a.length-1].text;
_4a[_4a.length-1].text=(_4e?_4e+"\r\n":"")+_4b[i];
}
}
}else{
if(_45.textContent){
var _4a=_3d[_42];
var _4b=_45.textContent.split(/\n/);
for(var i=0;i<_4b.length;i++){
var _4c=false;
for(var j=0;j<_4a.length;j++){
var _4d=_4a.textContent;
if(_4d&&_4d.indexOf(_4b[i])>=0){
_4c=true;
break;
}
}
if(!_4c){
var _4e=_4a[_4a.length-1].textContent;
_4a[_4a.length-1].textContent=(_4e?_4e+"\r\n":"")+_4b[i];
}
}
}
}
}else{
var _4f=jetspeed.createHeadElement(_45);
if(_3b.UAie){
if(_47=="SCRIPT"&&_45.text){
_4f.text=_45.value;
}else{
if(_47=="STYLE"&&_45.text){
_4f.styleSheet.cssText=_45.text;
}
}
}else{
if(_45.textContent){
_4f.appendChild(document.createTextNode(_45.textContent));
}
}
if(_3c[_44]){
_3b.getHead().insertBefore(_4f,_3c[_44]);
}else{
_3b.getHead().appendChild(scriptElem);
}
++_44;
}
}
}
};
jetspeed.doRender=function(_50,_51){
if(!_50){
_50={};
}else{
if((typeof _50=="string"||_50 instanceof String)){
_50={url:_50};
}
}
var _52=jetspeed.page.getPortlet(_51);
if(_52){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_51+"] url: "+_50.url);
}
_52.retrieveContent(null,_50);
}
};
jetspeed.doAction=function(_53,_54){
if(!_53){
_53={};
}else{
if((typeof _53=="string"||_53 instanceof String)){
_53={url:_53};
}
}
var _55=jetspeed.page.getPortlet(_54);
if(_55){
if(jetspeed.debug.doRenderDoAction){
if(!_53.formNode){
dojo.debug("doAction ["+_54+"] url: "+_53.url+" form: null");
}else{
dojo.debug("doAction ["+_54+"] url: "+_53.url+" form: "+jetspeed.debugDumpForm(_53.formNode));
}
}
_55.retrieveContent(new jetspeed.om.PortletActionCL(_55,_53),_53);
}
};
jetspeed.PortletRenderer=function(_56,_57,_58,_59,_5a,_5b){
var _5c=jetspeed;
var _5d=_5c.page;
var _5e=dojo;
this._jsObj=_5c;
this.mkWins=_56;
this.initEdit=_5b;
this.minimizeTemp=(_5b!=null&&_5b.editModeMove);
this.noRender=(this.minimizeTemp&&_5b.windowTitles!=null);
this.isPgLd=_57;
this.isPgUp=_58;
this.renderUrl=_59;
this.suppressGetActions=_5a;
this._colLen=_5d.columns.length;
this._colIndex=0;
this._portletIndex=0;
this._renderCount=0;
this.psByCol=_5d.portletsByPageColumn;
this.pageLoadUrl=null;
if(_57){
this.pageLoadUrl=_5c.url.parse(_5d.getPageUrl());
_5c.ui.evtConnect("before",_5e,"addOnLoad",_5d,"_beforeAddOnLoad",_5e.event);
}
this.dbgPgLd=_5c.debug.pageLoad&&_57;
this.dbgMsg=null;
if(_5c.debug.doRenderDoAction||this.dbgPgLd){
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
var _5f=this._jsObj;
var _60=this.dbgMsg;
if(_60!=null){
if(this.dbgPgLd){
dojo.debug("portlet-renderer page-url: "+_5f.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPgLd){
_5f.page.loadPostRender(this.isPgUp,this.initEdit);
}
},_renderCurrent:function(){
var _61=this._jsObj;
var _62=this._colLen;
var _63=this._colIndex;
var _64=this._portletIndex;
if(_63<=_62){
var _65;
if(_63<_62){
_65=this.psByCol[_63.toString()];
}else{
_65=this.psByCol["z"];
_63=null;
}
var _66=(_65!=null?_65.length:0);
if(_66>0){
var _67=_65[_64];
if(_67){
var _68=_67.portlet;
var _69=null;
if(this.mkWins){
_69=_61.ui.createPortletWindow(_68,_63,_61);
if(this.minimizeTemp){
_69.minimizeWindowTemporarily(this.noRender);
}
}
var _6a=this.dbgMsg;
if(_6a!=null){
if(_6a.length>0){
_6a=_6a+", ";
}
var _6b=null;
if(_68.getProperty!=null){
_6b=_68.getProperty(_61.id.PP_WIDGET_ID);
}
if(!_6b){
_6b=_68.widgetId;
}
if(!_6b){
_6b=_68.toString();
}
if(_68.entityId){
_6a=_6a+_68.entityId+"("+_6b+")";
if(this._dbPgLd&&_68.getProperty(_61.id.PP_WINDOW_TITLE)){
_6a=_6a+" "+_68.getProperty(_61.id.PP_WINDOW_TITLE);
}
}else{
_6a=_6a+_6b;
}
}
if(!this.noRender){
_68.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}else{
if(_69&&_69.portlet){
var _6c=this.initEdit.windowTitles[_69.portlet.entityId];
if(_6c!=null){
_69.setPortletTitle(_6c);
}
}
}
if((this._renderCount%3)==0){
_61.url.loadingIndicatorStep(_61);
}
this._renderCount++;
}
}
}
},_evalNext:function(){
var _6d=false;
var _6e=this._colLen;
var _6f=this._colIndex;
var _70=this._portletIndex;
var _71=_6f;
var _72;
for(++_6f;_6f<=_6e;_6f++){
_72=this.psByCol[_6f==_6e?"z":_6f.toString()];
if(_70<(_72!=null?_72.length:0)){
_6d=true;
this._colIndex=_6f;
break;
}
}
if(!_6d){
++_70;
for(_6f=0;_6f<=_71;_6f++){
_72=this.psByCol[_6f==_6e?"z":_6f.toString()];
if(_70<(_72!=null?_72.length:0)){
_6d=true;
this._colIndex=_6f;
this._portletIndex=_70;
break;
}
}
}
return _6d;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_73){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _75=_73;
var _76=null;
if(_73&&_73.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_73.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_73&&_73.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_73.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_76=jetspeed.url.getQueryParameter(_73,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_75)){
_75=null;
}
return {url:_75,operation:op,portletEntityId:_76};
},genPseudoUrl:function(_77,_78){
if(!_77||!_77.url||!_77.portletEntityId){
return null;
}
var _79=null;
if(_78){
_79=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_79="javascript:";
var _7a=false;
if(_77.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_79+="doAction(\"";
}else{
if(_77.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_79+="doRender(\"";
}else{
_7a=true;
}
}
if(_7a){
return null;
}
_79+=_77.url+"\",\""+_77.portletEntityId+"\"";
_79+=")";
}
return _79;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_7b,_7c,_7d){
var _7e=null;
var _7f=_7c.portletDecorationsConfig;
if(_7b&&_7f){
_7e=_7f[_7b];
}
if(_7e==null&&!_7d){
var _80=_7c.portletDecorationsAllowed;
for(var i=0;i<_80.length;i++){
_7b=_80[i];
_7e=_7f[_7b];
if(_7e!=null){
break;
}
}
}
if(_7e!=null&&!_7e._initialized){
var _82=jetspeed.prefs.getPortletDecorationBaseUrl(_7b);
_7e._initialized=true;
_7e.cssPathCommon=new dojo.uri.Uri(_82+"/css/styles.css");
_7e.cssPathDesktop=new dojo.uri.Uri(_82+"/css/desktop.css");
dojo.html.insertCssFile(_7e.cssPathCommon,null,true);
dojo.html.insertCssFile(_7e.cssPathDesktop,null,true);
}
return _7e;
};
jetspeed.loadPortletDecorationConfig=function(_83,_84,_85){
var _86={};
_84.portletDecorationsConfig[_83]=_86;
_86.name=_83;
_86.windowActionButtonOrder=_84.windowActionButtonOrder;
_86.windowActionNotPortlet=_84.windowActionNotPortlet;
_86.windowActionButtonMax=_84.windowActionButtonMax;
_86.windowActionButtonTooltip=_84.windowActionButtonTooltip;
_86.windowActionMenuOrder=_84.windowActionMenuOrder;
_86.windowActionNoImage=_84.windowActionNoImage;
_86.windowIconEnabled=_84.windowIconEnabled;
_86.windowIconPath=_84.windowIconPath;
_86.windowTitlebar=_84.windowTitlebar;
_86.windowResizebar=_84.windowResizebar;
_86.dNodeClass=_85.P_CLASS+" "+_83+" "+_85.PWIN_CLASS+" "+_85.PWIN_CLASS+"-"+_83;
_86.cNodeClass=_85.P_CLASS+" "+_83+" "+_85.PWIN_CLIENT_CLASS;
if(_84.portletDecorationsProperties){
var _87=_84.portletDecorationsProperties[_83];
if(_87){
for(var _88 in _87){
_86[_88]=_87[_88];
}
if(_87.windowActionNoImage!=null){
var _89={};
for(var i=0;i<_87.windowActionNoImage.length;i++){
_89[_87.windowActionNoImage[i]]=true;
}
_86.windowActionNoImage=_89;
}
if(_87.windowIconPath!=null){
_86.windowIconPath=dojo.string.trim(_87.windowIconPath);
if(_86.windowIconPath==null||_86.windowIconPath.length==0){
_86.windowIconPath=null;
}else{
var _8b=_86.windowIconPath;
var _8c=_8b.charAt(0);
if(_8c!="/"){
_8b="/"+_8b;
}
var _8d=_8b.charAt(_8b.length-1);
if(_8d!="/"){
_8b=_8b+"/";
}
_86.windowIconPath=_8b;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(_8e,_8f){
var _90=jetspeed;
_90.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _91=_90.page.getMenuNames();
for(var i=0;i<_91.length;i++){
var _93=_91[i];
var _94=dojo.widget.byId(_90.id.MENU_WIDGET_ID_PREFIX+_93);
if(_94){
_94.createJetspeedMenu(_90.page.getMenu(_93));
}
}
if(!_8f){
_90.url.loadingIndicatorHide();
}
_90.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_95){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_95);
}
};
jetspeed.menuNavClickWidget=function(_96,_97){
if(!_96){
return;
}
if(dojo.lang.isString(_96)){
var _98=_96;
_96=dojo.widget.byId(_98);
if(!_96){
dojo.raise("Tab widget not found: "+_98);
}
}
if(_96){
var _99=_96.jetspeedmenuname;
if(!_99&&_96.extraArgs){
_99=_96.extraArgs.jetspeedmenuname;
}
if(!_99){
dojo.raise("Tab widget is invalid: "+_96.widgetId);
}
var _9a=jetspeed.page.getMenu(_99);
if(!_9a){
dojo.raise("Tab widget "+_96.widgetId+" no menu: "+_99);
}
var _9b=_9a.getOptionByIndex(_97);
jetspeed.menuNavClick(_9b);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_9c,_9d,_9e){
var _9f=jetspeed;
if(!_9c||_9f.pageNavigateSuppress){
return;
}
if(typeof _9e=="undefined"){
_9e=false;
}
if(!_9e&&_9f.page&&_9f.page.equalsPageUrl(_9c)){
return;
}
_9c=_9f.page.makePageUrl(_9c);
if(_9d=="top"){
top.location.href=_9c;
}else{
if(_9d=="parent"){
parent.location.href=_9c;
}else{
window.location.href=_9c;
}
}
};
jetspeed.getActionsForPortlet=function(_a0){
if(_a0==null){
return;
}
jetspeed.getActionsForPortlets([_a0]);
};
jetspeed.getActionsForPortlets=function(_a1){
var _a2=jetspeed;
if(_a1==null){
_a1=_a2.page.getPortletIds();
}
var _a3=new _a2.om.PortletActionsCL(_a1);
var _a4="?action=getactions";
for(var i=0;i<_a1.length;i++){
_a4+="&id="+_a1[i];
}
var _a6=_a2.url.basePortalUrl()+_a2.url.path.AJAX_API+_a2.page.getPath()+_a4;
var _a7="text/xml";
var _a8=new _a2.om.Id("getactions",{});
_a2.url.retrieveContent({url:_a6,mimetype:_a7},_a3,_a8,_a2.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_a9,_aa,_ab,_ac,_ad){
var _ae=jetspeed;
if(_a9==null){
return;
}
if(_ac==null){
_ac=new _ae.om.PortletChangeActionCL(_a9);
}
var _af="?action=window&id="+(_a9!=null?_a9:"");
if(_aa!=null){
_af+="&state="+_aa;
}
if(_ab!=null){
_af+="&mode="+_ab;
}
var _b0=_ad;
if(!_b0){
_b0=_ae.page.getPath();
}
var _b1=_ae.url.basePortalUrl()+_ae.url.path.AJAX_API+_b0+_af;
var _b2="text/xml";
var _b3=new _ae.om.Id("changeaction",{});
_ae.url.retrieveContent({url:_b1,mimetype:_b2},_ac,_b3,_ae.debugContentDumpIds);
};
jetspeed.getUserInfo=function(_b4){
var _b5=jetspeed;
var _b6=new _b5.om.UserInfoCL();
var _b7="?action=getuserinfo";
var _b8=_b5.url.basePortalUrl()+_b5.url.path.AJAX_API+_b5.page.getPath()+_b7;
var _b9="text/xml";
var _ba=new _b5.om.Id("getuserinfo",{});
_b5.url.retrieveContent({url:_b8,mimetype:_b9,sync:_b4},_b6,_ba,_b5.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_bb,_bc){
var _bd=_bb.page;
if(!_bd.editMode){
var _be=_bb.css;
var _bf=true;
var _c0=_bb.url.getQueryParameter(window.location.href,_bb.id.PORTAL_ORIGINATE_PARAMETER);
if(_c0!=null&&_c0=="true"){
_bf=false;
}
_bd.editMode=true;
var _c1=dojo.widget.byId(_bb.id.PG_ED_WID);
if(_bb.UAie6){
_bd.displayAllPWins(true);
}
var _c2=((_bc!=null&&_bc.editModeMove)?true:false);
var _c3=_bd._perms(_bb.prefs,-1,String.fromCharCode);
if(_c3&&_c3[2]&&_c3[2].length>0){
if(!_bb.page._getU()){
_bb.getUserInfo(true);
}
}
if(_c1==null){
try{
_bb.url.loadingIndicatorShow("loadpageeditor",true);
_c1=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_bb.id.PG_ED_WID,editorInitiatedFromDesktop:_bf,editModeMove:_c2});
var _c4=document.getElementById(_bb.id.COLUMNS);
_c4.insertBefore(_c1.domNode,_c4.firstChild);
}
catch(e){
_bb.url.loadingIndicatorHide();
if(_bb.UAie6){
_bd.displayAllPWins();
}
}
}else{
_c1.editPageShow();
}
_bd.syncPageControls(_bb);
}
};
jetspeed.editPageTerminate=function(_c5,_c6){
var _c7=_c5.page;
if(_c7.editMode){
var _c8=null;
var _c9=_c5.css;
var _ca=dojo.widget.byId(_c5.id.PG_ED_WID);
if(_ca!=null&&!_ca.editorInitiatedFromDesktop){
var _cb=_c7.getPageUrl(true);
_cb=_c5.url.removeQueryParameter(_cb,_c5.id.PG_ED_PARAM);
_cb=_c5.url.removeQueryParameter(_cb,_c5.id.PORTAL_ORIGINATE_PARAMETER);
_c8=_cb;
}else{
var _cc=_c5.url.getQueryParameter(window.location.href,_c5.id.PG_ED_PARAM);
if(_cc!=null&&_cc=="true"){
var _cd=window.location.href;
_cd=_c5.url.removeQueryParameter(_cd,_c5.id.PG_ED_PARAM);
_c8=_cd;
}
}
if(_c8!=null){
_c8=_c8.toString();
}
_c7.editMode=false;
_c5.changeActionForPortlet(_c7.rootFragmentId,null,_c5.id.ACT_VIEW,new _c5.om.PageChangeActionCL(_c8));
if(_c8==null){
if(_ca!=null){
_ca.editMoveModeExit(true);
_ca.editPageHide();
}
_c7.syncPageControls(_c5);
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_ce,_cf,_d0,_d1){
if(!_ce){
_ce={};
}
jetspeed.url.retrieveContent(_ce,_cf,_d0,_d1);
}};
jetspeed.om.PageCLCreateWidget=function(_d2,_d3){
if(typeof _d2=="undefined"){
_d2=false;
}
this.isPageUpdate=_d2;
this.initEditModeConf=_d3;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_d4,_d5,_d6){
_d6.loadFromPSML(_d4,this.isPageUpdate,this.initEditModeConf);
},notifyFailure:function(_d7,_d8,_d9,_da){
dojo.raise("PageCLCreateWidget error url: "+_d9+" type: "+_d7+jetspeed.formatError(_d8));
}};
jetspeed.om.Page=function(_db,_dc,_dd,_de,_df){
if(_db!=null&&_dc!=null){
this.requiredLayoutDecorator=_db;
this.setPsmlPathFromDocumentUrl(_dc);
this.pageUrlFallback=_dc;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _dd!="undefined"){
this.addToHistory=_dd;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets={};
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_df!=null){
this.iframeCoverByWinId=_df;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_de!=null){
this.tooltipMgr=_de;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,uIA:true,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _e0=(this.name!=null&&this.name.length>0?this.name:null);
if(!_e0){
this.getPsmlUrl();
_e0=this.psmlPath;
}
return "page-"+_e0;
},setPsmlPathFromDocumentUrl:function(_e1){
var _e2=jetspeed;
var _e3=_e2.url.path.AJAX_API;
var _e4=null;
if(_e1==null){
_e4=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_e2.prefs.ajaxPageNavigation){
var _e5=window.location.hash;
if(_e5!=null&&_e5.length>0){
if(_e5.indexOf("#")==0){
_e5=(_e5.length>1?_e5.substring(1):"");
}
if(_e5!=null&&_e5.length>1&&_e5.indexOf("/")==0){
this.psmlPath=_e2.url.path.AJAX_API+_e5;
return;
}
}
}
}else{
var _e6=_e2.url.parse(_e1);
_e4=_e6.path;
}
var _e7=_e2.url.path.DESKTOP;
var _e8=_e4.indexOf(_e7);
if(_e8!=-1&&_e4.length>(_e8+_e7.length)){
_e3=_e3+_e4.substring(_e8+_e7.length);
}
this.psmlPath=_e3;
},getPsmlUrl:function(){
var _e9=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _ea=_e9.url.basePortalUrl()+this.psmlPath;
if(_e9.prefs.printModeOnly!=null){
_ea=_e9.url.addQueryParameter(_ea,"layoutid",_e9.prefs.printModeOnly.layout);
_ea=_e9.url.addQueryParameter(_ea,"entity",_e9.prefs.printModeOnly.entity).toString();
}
return _ea;
},_setU:function(u){
this._u=u;
},_getU:function(){
return this._u;
},retrievePsml:function(_ec){
var _ed=jetspeed;
if(_ec==null){
_ec=new _ed.om.PageCLCreateWidget();
}
var _ee=this.getPsmlUrl();
var _ef="text/xml";
if(_ed.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_ee);
}
_ed.url.retrieveContent({url:_ee,mimetype:_ef},_ec,this,_ed.debugContentDumpIds);
},loadFromPSML:function(_f0,_f1,_f2){
var _f3=jetspeed;
var _f4=_f3.prefs;
var _f5=dojo;
var _f6=_f4.printModeOnly;
if(djConfig.isDebug&&_f3.debug.profile&&_f6==null){
_f5.profile.start("loadFromPSML");
}
var _f7=this._parsePSML(_f0);
jetspeed.rootfrag=_f7;
if(_f7==null){
return;
}
this.portletsByPageColumn={};
var _f8={};
if(this.portletDecorator){
_f8[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_f7,0,null,this.portletsByPageColumn,true,_f8,_f5,_f3);
this.rootFragmentId=_f7.id;
this.editMode=false;
for(var _f9 in _f8){
_f3.loadPortletDecorationStyles(_f9,_f4,true);
}
if(_f4.windowTiling){
this._createColsStart(document.getElementById(_f3.id.DESKTOP),_f3.id.COLUMNS);
}
this.createLayoutInfo(_f3);
var _fa=this.portletsByPageColumn["z"];
if(_fa){
_fa.sort(this._loadPortletZIndexCompare);
}
if(typeof _f2=="undefined"){
_f2=null;
}
if(_f2!=null||(this.actions!=null&&this.actions[_f3.id.ACT_VIEW]!=null)){
if(!this.isUA()&&this.actions!=null&&(this.actions[_f3.id.ACT_EDIT]!=null||this.actions[_f3.id.ACT_VIEW]!=null)){
if(_f2==null){
_f2={};
}
if((typeof _f2.editModeMove=="undefined")&&this._perms(_f4,_f3.id.PM_MZ_P,String.fromCharCode)){
_f2.editModeMove=true;
}
var _fb=_f3.url.parse(window.location.href);
if(!_f2.editModeMove){
var _fc=_f3.url.getQueryParameter(_fb,_f3.id.PG_ED_STATE_PARAM);
if(_fc!=null){
_fc="0x"+_fc;
if((_fc&_f3.id.PM_MZ_P)>0){
_f2.editModeMove=true;
}
}
}
if(_f2.editModeMove&&!_f2.windowTitles){
var _fd=_f3.url.getQueryParameter(_fb,_f3.id.PG_ED_TITLES_PARAM);
if(_fd!=null){
var _fe=_fd.length;
var _ff=new Array(_fe/2);
var sfcc=String.fromCharCode;
var _101=0,chI=0;
while(chI<(_fe-1)){
_ff[_101]=sfcc(Number("0x"+_fd.substring(chI,(chI+2))));
_101++;
chI+=2;
}
var _103=null;
try{
_103=eval("({"+_ff.join("")+"})");
}
catch(e){
if(djConfig.isDebug){
dojo.debug("cannot parse json: "+_ff.join(""));
}
}
if(_103!=null){
var _104=false;
for(var _105 in this.portlets){
var _106=this.portlets[_105];
if(_106!=null&&!_103[_106.entityId]){
_104=true;
break;
}
}
if(!_104){
_f2.windowTitles=_103;
}
}
}
}
}else{
_f2=null;
}
}
if(_f2!=null){
_f3.url.loadingIndicatorShow("loadpageeditor",true);
}
var _107=new _f3.PortletRenderer(true,true,_f1,null,true,_f2);
_107.renderAllTimeDistribute();
},loadPostRender:function(_108,_109){
var _10a=jetspeed;
var _10b=_10a.prefs.printModeOnly;
if(_10b==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
this.retrieveMenuDeclarations(true,_108,_109);
}else{
for(var _10c in this.portlets){
var _10d=this.portlets[_10c];
if(_10d!=null){
_10d.renderAction(null,_10b.action);
}
break;
}
if(_108){
_10a.updatePageEnd();
}
}
_10a.ui.evtConnect("after",window,"onresize",_10a.ui.windowResizeMgr,"onResize",dojo.event);
_10a.ui.windowResizeMgr.onResizeDelayedCompare();
var _10e,_10f=this.columns;
if(_10f){
for(var i=0;i<_10f.length;i++){
_10e=_10f[i].domNode;
if(!_10e.childNodes||_10e.childNodes.length==0){
_10e.style.height="1px";
}
}
}
var _111=this.maximizedOnInit;
if(_111!=null){
var _112=this.getPWin(_111);
if(_112==null){
dojo.raise("no pWin to max");
}else{
dojo.lang.setTimeout(_112,_112._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_10a.url,_10a.url.loadingIndicatorStepPreload,1800);
},loadPostRetrieveMenus:function(_113,_114){
var _115=jetspeed;
this.renderPageControls(_115);
if(_114){
_115.editPageInitiate(_115,_114);
}
if(_113){
_115.updatePageEnd();
}
this.syncPageControls(_115);
},_parsePSML:function(psml){
var _117=jetspeed;
var _118=dojo;
var _119=psml.getElementsByTagName("page");
if(!_119||_119.length>1||_119[0]==null){
_118.raise("<page>");
}
var _11a=_119[0];
var _11b=_11a.childNodes;
var _11c=new RegExp("(name|path|profiledPath|title|short-title|uIA|npe)");
var _11d=null;
var _11e={};
for(var i=0;i<_11b.length;i++){
var _120=_11b[i];
if(_120.nodeType!=1){
continue;
}
var _121=_120.nodeName;
if(_121=="fragment"){
_11d=_120;
}else{
if(_121=="defaults"){
this.layoutDecorator=_120.getAttribute("layout-decorator");
var _122=_120.getAttribute("portlet-decorator");
var _123=_117.prefs.portletDecorationsAllowed;
if(!_123||_118.lang.indexOf(_123,_122)==-1){
_122=_117.prefs.windowDecoration;
}
this.portletDecorator=_122;
}else{
if(_121&&_121.match(_11c)){
if(_121=="short-title"){
_121="shortTitle";
}
this[_121]=((_120&&_120.firstChild)?_120.firstChild.nodeValue:null);
}else{
if(_121=="action"){
this._parsePSMLAction(_120,_11e);
}
}
}
}
}
this.actions=_11e;
if(_11d==null){
_118.raise("root frag");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_117.debug.ajaxPageNav){
_118.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_117.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _124=this.getPageUrl();
_118.undo.browser.addToHistory({back:function(){
if(_117.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_124);
}
_117.updatePage(_124,true);
},forward:function(){
if(_117.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_124);
}
_117.updatePage(_124,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_117.prefs.ajaxPageNavigation){
var _124=this.getPageUrl();
_118.undo.browser.setInitialState({back:function(){
if(_117.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_124);
}
_117.updatePage(_124,true);
},forward:function(){
if(_117.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_124);
}
_117.updatePage(_124,true);
},changeUrl:escape(this.getPath())});
}
}
var _125=this._parsePSMLFrag(_11d,0,false);
return _125;
},_parsePSMLFrag:function(_126,_127,_128){
var _129=jetspeed;
var _12a=new Array();
var _12b=((_126!=null)?_126.getAttribute("type"):null);
if(_12b!="layout"){
dojo.raise("!layout frag="+_126);
return null;
}
if(!_128){
var _12c=_126.getAttribute("name");
if(_12c!=null){
_12c=_12c.toLowerCase();
if(_12c.indexOf("noactions")!=-1){
_128=true;
}
}
}
var _12d=null,_12e=0;
var _12f={};
var _130=_126.childNodes;
var _131,_132,_133,_134,_135;
for(var i=0;i<_130.length;i++){
_131=_130[i];
if(_131.nodeType!=1){
continue;
}
_132=_131.nodeName;
if(_132=="fragment"){
_135=_131.getAttribute("type");
if(_135=="layout"){
var _137=this._parsePSMLFrag(_131,i,_128);
if(_137!=null){
_12a.push(_137);
}
}else{
var _138=this._parsePSMLProps(_131,null);
var _139=_138[_129.id.PP_WINDOW_ICON];
if(_139==null||_139.length==0){
_139=this._parsePSMLChildOrAttr(_131,"icon");
if(_139!=null&&_139.length>0){
_138[_129.id.PP_WINDOW_ICON]=_139;
}
}
_12a.push({id:_131.getAttribute("id"),type:_135,name:_131.getAttribute("name"),properties:_138,actions:this._parsePSMLActions(_131,null),currentActionState:this._parsePSMLChildOrAttr(_131,"state"),currentActionMode:this._parsePSMLChildOrAttr(_131,"mode"),decorator:_131.getAttribute("decorator"),layoutActionsDisabled:_128,documentOrderIndex:i});
}
}else{
if(_132=="property"){
if(this._parsePSMLProp(_131,_12f)=="sizes"){
if(_12d!=null){
dojo.raise("<sizes>: "+_126);
return null;
}
if(_129.prefs.printModeOnly!=null){
_12d=["100"];
_12e=100;
}else{
_134=_131.getAttribute("value");
if(_134!=null&&_134.length>0){
_12d=_134.split(",");
for(var j=0;j<_12d.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_12d[j]=_12d[j].replace(re,"$1");
_12e+=new Number(_12d[j]);
}
}
}
}
}
}
}
if(_12d==null){
_12d=["100"];
_12e=100;
}
var _13c=_12d.length;
var _13d=_12a.length;
var pCi=_129.id.PP_COLUMN;
var pRi=_129.id.PP_ROW;
var _140=new Array(_13c);
var _141=new Array(_13c);
for(var cI=0;cI<_13c;cI++){
_140[cI]=[];
_141[cI]={head:-1,tail:-1,high:-1};
}
for(var _143=0;_143<_13d;_143++){
var frag=_12a[_143];
var _145=frag.properties;
var col=_145[pCi];
var row=_145[pRi];
var _148=null;
if(col==null||col>=_13c){
_148=_13c-1;
}else{
if(col<0){
_148=0;
}
}
if(_148!=null){
col=_145[pCi]=String(_148);
}
var ll=_140[col];
var _14a=ll.length;
var _14b=_141[col];
if(row<0){
row=_145[pRi]=0;
}else{
if(row==null){
row=_14b.high+1;
}
}
var _14c={i:_143,row:row,next:-1};
ll.push(_14c);
if(_14a==0){
_14b.head=_14b.tail=0;
_14b.high=row;
}else{
if(row>_14b.high){
ll[_14b.tail].next=_14a;
_14b.high=row;
_14b.tail=_14a;
}else{
var _14d=_14b.head;
var _14e=-1;
while(ll[_14d].row<row){
_14e=_14d;
_14d=ll[_14d].next;
}
if(ll[_14d].row==row){
var _14f=new Number(row)+1;
ll[_14d].row=_14f;
if(_14b.tail==_14d){
_14b.high=_14f;
}
}
_14c.next=_14d;
if(_14e==-1){
_14b.head=_14a;
}else{
ll[_14e].next=_14a;
}
}
}
}
var _150=new Array(_13d);
var _151=0;
for(var cI=0;cI<_13c;cI++){
var ll=_140[cI];
var _14b=_141[cI];
var _152=0;
var _153=_14b.head;
while(_153!=-1){
var _14c=ll[_153];
var frag=_12a[_14c.i];
_150[_151]=frag;
frag.properties[pRi]=_152;
_151++;
_152++;
_153=_14c.next;
}
}
return {id:_126.getAttribute("id"),type:_12b,name:_126.getAttribute("name"),decorator:_126.getAttribute("decorator"),columnSizes:_12d,columnSizesSum:_12e,properties:_12f,fragments:_150,layoutActionsDisabled:_128,documentOrderIndex:_127};
},_parsePSMLActions:function(_154,_155){
if(_155==null){
_155={};
}
var _156=_154.getElementsByTagName("action");
for(var _157=0;_157<_156.length;_157++){
var _158=_156[_157];
this._parsePSMLAction(_158,_155);
}
return _155;
},_parsePSMLAction:function(_159,_15a){
var _15b=_159.getAttribute("id");
if(_15b!=null){
var _15c=_159.getAttribute("type");
var _15d=_159.getAttribute("name");
var _15e=_159.getAttribute("url");
var _15f=_159.getAttribute("alt");
_15a[_15b.toLowerCase()]={id:_15b,type:_15c,label:_15d,url:_15e,alt:_15f};
}
},_parsePSMLChildOrAttr:function(_160,_161){
var _162=null;
var _163=_160.getElementsByTagName(_161);
if(_163!=null&&_163.length==1&&_163[0].firstChild!=null){
_162=_163[0].firstChild.nodeValue;
}
if(!_162){
_162=_160.getAttribute(_161);
}
if(_162==null||_162.length==0){
_162=null;
}
return _162;
},_parsePSMLProps:function(_164,_165){
if(_165==null){
_165={};
}
var _166=_164.getElementsByTagName("property");
for(var _167=0;_167<_166.length;_167++){
this._parsePSMLProp(_166[_167],_165);
}
return _165;
},_parsePSMLProp:function(_168,_169){
var _16a=_168.getAttribute("name");
var _16b=_168.getAttribute("value");
_169[_16a]=_16b;
return _16a;
},_layoutCreateModel:function(_16c,_16d,_16e,_16f,_170,_171,_172,_173){
var jsId=_173.id;
var _175=this.columns.length;
var _176=this._layoutCreateColsModel(_16c,_16d,_16e,_170);
var _177=_176.columnsInLayout;
if(_176.addedLayoutHeaderColumn){
_175++;
}
var _178=(_177==null?0:_177.length);
var _179=new Array(_178);
var _17a=new Array(_178);
for(var i=0;i<_16c.fragments.length;i++){
var _17c=_16c.fragments[i];
if(_17c.type=="layout"){
var _17d=i;
var _17d=(_17c.properties?_17c.properties[_173.id.PP_COLUMN]:i);
if(_17d==null||_17d<0||_17d>=_178){
_17d=(_178>0?(_178-1):0);
}
_17a[_17d]=true;
this._layoutCreateModel(_17c,(_16d+1),_177[_17d],_16f,false,_171,_172,_173);
}else{
this._layoutCreatePortlet(_17c,_16c,_177,_175,_16f,_179,_171,_172,_173);
}
}
return _177;
},_layoutCreatePortlet:function(_17e,_17f,_180,_181,_182,_183,_184,_185,_186){
if(_17e&&_186.debugPortletEntityIdFilter){
if(!_185.lang.inArray(_186.debugPortletEntityIdFilter,_17e.id)){
_17e=null;
}
}
if(_17e){
var _187="z";
var _188=_17e.properties[_186.id.PP_DESKTOP_EXTENDED];
var _189=_186.prefs.windowTiling;
var _18a=_189;
var _18b=_186.prefs.windowHeightExpand;
if(_188!=null&&_189&&_186.prefs.printModeOnly==null){
var _18c=_188.split(_186.id.PP_PAIR_SEPARATOR);
var _18d=null,_18e=0,_18f=null,_190=null,_191=false;
if(_18c!=null&&_18c.length>0){
var _192=_186.id.PP_PROP_SEPARATOR;
for(var _193=0;_193<_18c.length;_193++){
_18d=_18c[_193];
_18e=((_18d!=null)?_18d.length:0);
if(_18e>0){
var _194=_18d.indexOf(_192);
if(_194>0&&_194<(_18e-1)){
_18f=_18d.substring(0,_194);
_190=_18d.substring(_194+1);
_191=((_190=="true")?true:false);
if(_18f==_186.id.PP_STATICPOS){
_18a=_191;
}else{
if(_18f==_186.id.PP_FITHEIGHT){
_18b=_191;
}
}
}
}
}
}
}else{
if(!_189){
_18a=false;
}
}
_17e.properties[_186.id.PP_WINDOW_POSITION_STATIC]=_18a;
_17e.properties[_186.id.PP_WINDOW_HEIGHT_TO_FIT]=_18b;
if(_18a&&_189){
var _195=_180.length;
var _196=_17e.properties[_186.id.PP_COLUMN];
if(_196==null||_196>=_195){
_196=_195-1;
}else{
if(_196<0){
_196=0;
}
}
if(_183[_196]==null){
_183[_196]=new Array();
}
_183[_196].push(_17e.id);
var _197=_181+new Number(_196);
_187=_197.toString();
}
if(_17e.currentActionState==_186.id.ACT_MAXIMIZE){
this.maximizedOnInit=_17e.id;
}
var _198=_17e.decorator;
if(_198!=null&&_198.length>0){
if(_185.lang.indexOf(_186.prefs.portletDecorationsAllowed,_198)==-1){
_198=null;
}
}
if(_198==null||_198.length==0){
if(djConfig.isDebug&&_186.debug.windowDecorationRandom){
_198=_186.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_186.prefs.portletDecorationsAllowed.length)];
}else{
_198=this.portletDecorator;
}
}
var _199=_17e.properties||{};
_199[_186.id.PP_WINDOW_DECORATION]=_198;
_184[_198]=true;
var _19a=_17e.actions||{};
var _19b=new _186.om.Portlet(_17e.name,_17e.id,null,_199,_19a,_17e.currentActionState,_17e.currentActionMode,_17e.layoutActionsDisabled);
_19b.initialize();
this.putPortlet(_19b);
if(_182[_187]==null){
_182[_187]=new Array();
}
_182[_187].push({portlet:_19b,layout:_17f.id});
}
},_layoutCreateColsModel:function(_19c,_19d,_19e,_19f){
var _1a0=jetspeed;
this.layouts[_19c.id]=_19c;
var _1a1=false;
var _1a2=new Array();
if(_1a0.prefs.windowTiling&&_19c.columnSizes.length>0){
var _1a3=false;
if(_1a0.UAie){
_1a3=true;
}
if(_19e!=null&&!_19f){
var _1a4=new _1a0.om.Column(0,_19c.id,(_1a3?_19c.columnSizesSum-0.1:_19c.columnSizesSum),this.columns.length,_19c.layoutActionsDisabled,_19d);
_1a4.layoutHeader=true;
this.columns.push(_1a4);
if(_19e.buildColChildren==null){
_19e.buildColChildren=new Array();
}
_19e.buildColChildren.push(_1a4);
_19e=_1a4;
_1a1=true;
}
for(var i=0;i<_19c.columnSizes.length;i++){
var size=_19c.columnSizes[i];
if(_1a3&&i==(_19c.columnSizes.length-1)){
size=size-0.1;
}
var _1a7=new _1a0.om.Column(i,_19c.id,size,this.columns.length,_19c.layoutActionsDisabled);
this.columns.push(_1a7);
if(_19e!=null){
if(_19e.buildColChildren==null){
_19e.buildColChildren=new Array();
}
_19e.buildColChildren.push(_1a7);
}
_1a2.push(_1a7);
}
}
return {columnsInLayout:_1a2,addedLayoutHeaderColumn:_1a1};
},_portletsInitWinState:function(_1a8){
var _1a9={};
this.getPortletCurColRow(null,false,_1a9);
for(var _1aa in this.portlets){
var _1ab=this.portlets[_1aa];
var _1ac=_1a9[_1ab.getId()];
if(_1ac==null&&_1a8){
for(var i=0;i<_1a8.length;i++){
if(_1a8[i].portlet.getId()==_1ab.getId()){
_1ac={layout:_1a8[i].layout};
break;
}
}
}
if(_1ac!=null){
_1ab._initWinState(_1ac,false);
}else{
dojo.raise("Window state data not found for portlet: "+_1ab.getId());
}
}
},_loadPortletZIndexCompare:function(_1ae,_1af){
var _1b0=null;
var _1b1=null;
var _1b2=null;
_1b0=_1ae.portlet._getInitialZIndex();
_1b1=_1af.portlet._getInitialZIndex();
if(_1b0&&!_1b1){
return -1;
}else{
if(_1b1&&!_1b0){
return 1;
}else{
if(_1b0==_1b1){
return 0;
}
}
}
return (_1b0-_1b1);
},_createColsStart:function(_1b3,_1b4){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _1b5=document.createElement("div");
_1b5.id=_1b4;
_1b5.setAttribute("id",_1b4);
for(var _1b6=0;_1b6<this.columnsStructure.length;_1b6++){
var _1b7=this.columnsStructure[_1b6];
this._createCols(_1b7,_1b5);
}
_1b3.appendChild(_1b5);
},_createCols:function(_1b8,_1b9){
_1b8.createColumn();
if(this.colFirstNormI==-1&&!_1b8.columnContainer&&!_1b8.layoutHeader){
this.colFirstNormI=_1b8.getPageColumnIndex();
}
var _1ba=_1b8.buildColChildren;
if(_1ba!=null&&_1ba.length>0){
for(var _1bb=0;_1bb<_1ba.length;_1bb++){
this._createCols(_1ba[_1bb],_1b8.domNode);
}
}
delete _1b8.buildColChildren;
_1b9.appendChild(_1b8.domNode);
},_removeCols:function(_1bc){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_1bc){
var _1be=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_1be,function(_1bf){
_1bc.appendChild(_1bf);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _1c0=dojo.byId(jetspeed.id.COLUMNS);
if(_1c0){
dojo.dom.removeNode(_1c0);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnsEmptyCheck:function(_1c1){
var _1c2=null;
if(_1c1==null){
return _1c2;
}
var _1c3=_1c1.childNodes,_1c4;
if(_1c3){
for(var i=0;i<_1c3.length;i++){
_1c4=_1c3[i];
var _1c6=this.columnEmptyCheck(_1c4,true);
if(_1c6!=null){
_1c2=_1c6;
if(_1c2==false){
break;
}
}
}
}
return _1c2;
},columnEmptyCheck:function(_1c7,_1c8){
var _1c9=null;
if(!_1c7||!_1c7.getAttribute){
return _1c9;
}
var _1ca=_1c7.getAttribute("columnindex");
if(!_1ca||_1ca.length==0){
return _1c9;
}
var _1cb=_1c7.getAttribute("layoutid");
if(_1cb==null||_1cb.length==0){
var _1cc=_1c7.childNodes;
_1c9=(!_1cc||_1cc.length==0);
if(!_1c8){
_1c7.style.height=(_1c9?"1px":"");
}
}
return _1c9;
},getPortletCurColRow:function(_1cd,_1ce,_1cf){
if(!this.columns||this.columns.length==0){
return null;
}
var _1d0=null;
var _1d1=((_1cd!=null)?true:false);
var _1d2=0;
var _1d3=null;
var _1d4=null;
var _1d5=0;
var _1d6=false;
for(var _1d7=0;_1d7<this.columns.length;_1d7++){
var _1d8=this.columns[_1d7];
var _1d9=_1d8.domNode.childNodes;
if(_1d4==null||_1d4!=_1d8.getLayoutId()){
_1d4=_1d8.getLayoutId();
_1d3=this.layouts[_1d4];
if(_1d3==null){
dojo.raise("Layout not found: "+_1d4);
return null;
}
_1d5=0;
_1d6=false;
if(_1d3.clonedFromRootId==null){
_1d6=true;
}else{
var _1da=this.getColFromColNode(_1d8.domNode.parentNode);
if(_1da==null){
dojo.raise("Parent column not found: "+_1d8);
return null;
}
_1d8=_1da;
}
}
var _1db=null;
var _1dc=jetspeed;
var _1dd=dojo;
var _1de=_1dc.id.PWIN_CLASS;
if(_1ce){
_1de+="|"+_1dc.id.PWIN_GHOST_CLASS;
}
if(_1d1){
_1de+="|"+_1dc.id.COL_CLASS;
}
var _1df=new RegExp("(^|\\s+)("+_1de+")(\\s+|$)");
for(var _1e0=0;_1e0<_1d9.length;_1e0++){
var _1e1=_1d9[_1e0];
if(_1df.test(_1dd.html.getClass(_1e1))){
_1db=(_1db==null?0:_1db+1);
if((_1db+1)>_1d5){
_1d5=(_1db+1);
}
if(_1cd==null||_1e1==_1cd){
var _1e2={layout:_1d4,column:_1d8.getLayoutColumnIndex(),row:_1db,columnObj:_1d8};
if(!_1d6){
_1e2.layout=_1d3.clonedFromRootId;
}
if(_1cd!=null){
_1d0=_1e2;
break;
}else{
if(_1cf!=null){
var _1e3=this.getPWinFromNode(_1e1);
if(_1e3==null){
_1dd.raise("PortletWindow not found for node");
}else{
var _1e4=_1e3.portlet;
if(_1e4==null){
_1dd.raise("PortletWindow for node has null portlet: "+_1e3.widgetId);
}else{
_1cf[_1e4.getId()]=_1e2;
}
}
}
}
}
}
}
if(_1d0!=null){
break;
}
}
return _1d0;
},_getPortletArrayByZIndex:function(){
var _1e5=jetspeed;
var _1e6=this.getPortletArray();
if(!_1e6){
return _1e6;
}
var _1e7=[];
for(var i=0;i<_1e6.length;i++){
if(!_1e6[i].getProperty(_1e5.id.PP_WINDOW_POSITION_STATIC)){
_1e7.push(_1e6[i]);
}
}
_1e7.sort(this._portletZIndexCompare);
return _1e7;
},_portletZIndexCompare:function(_1e9,_1ea){
var _1eb=null;
var _1ec=null;
var _1ed=null;
_1ed=_1e9.getSavedWinState();
_1eb=_1ed.zIndex;
_1ed=_1ea.getSavedWinState();
_1ec=_1ed.zIndex;
if(_1eb&&!_1ec){
return -1;
}else{
if(_1ec&&!_1eb){
return 1;
}else{
if(_1eb==_1ec){
return 0;
}
}
}
return (_1eb-_1ec);
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
var _200=[];
for(var _201 in this.portlets){
var _202=this.portlets[_201];
_200.push(_202);
}
return _200;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _203=[];
for(var _204 in this.portlets){
var _205=this.portlets[_204];
_203.push(_205.getId());
}
return _203;
},getPortletByName:function(_206){
if(this.portlets&&_206){
for(var _207 in this.portlets){
var _208=this.portlets[_207];
if(_208.name==_206){
return _208;
}
}
}
return null;
},getPortlet:function(_209){
if(this.portlets&&_209){
return this.portlets[_209];
}
return null;
},getPWinFromNode:function(_20a){
var _20b=null;
if(this.portlets&&_20a){
for(var _20c in this.portlets){
var _20d=this.portlets[_20c];
var _20e=_20d.getPWin();
if(_20e!=null){
if(_20e.domNode==_20a){
_20b=_20e;
break;
}
}
}
}
return _20b;
},putPortlet:function(_20f){
if(!_20f){
return;
}
if(!this.portlets){
this.portlets={};
}
this.portlets[_20f.entityId]=_20f;
this.portlet_count++;
},putPWin:function(_210){
if(!_210){
return;
}
var _211=_210.widgetId;
if(!_211){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_211]=_210;
this.portlet_window_count++;
},getPWin:function(_212){
if(this.portlet_windows&&_212){
var pWin=this.portlet_windows[_212];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_212];
if(pWin==null){
var p=this.getPortlet(_212);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_216){
var _217=this.portlet_windows;
var pWin;
var _219=[];
for(var _21a in _217){
pWin=_217[_21a];
if(pWin&&(!_216||pWin.portlet)){
_219.push(pWin);
}
}
return _219;
},getPWinTopZIndex:function(_21b){
var _21c=0;
if(_21b){
_21c=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_21c;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_21c=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_21c;
}
return _21c;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_21d,_21e){
return;
},onBrowserWindowResize:function(){
var _21f=jetspeed;
var _220=this.portlet_windows;
var pWin;
for(var _222 in _220){
pWin=_220[_222];
pWin.onBrowserWindowResize();
}
if(_21f.UAie6&&this.editMode){
var _223=dojo.widget.byId(_21f.id.PG_ED_WID);
if(_223!=null){
_223.onBrowserWindowResize();
}
}
},regPWinIFrameCover:function(_224){
if(!_224){
return;
}
this.iframeCoverByWinId[_224.widgetId]=true;
},unregPWinIFrameCover:function(_225){
if(!_225){
return;
}
delete this.iframeCoverByWinId[_225.widgetId];
},displayAllPWinIFrameCovers:function(_226,_227){
var _228=this.portlet_windows;
var _229=this.iframeCoverByWinId;
if(!_228||!_229){
return;
}
for(var _22a in _229){
if(_22a==_227){
continue;
}
var pWin=_228[_22a];
var _22c=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_22c){
_22c.style.display=(_226?"none":"block");
}
}
},createLayoutInfo:function(_22d){
var _22e=dojo;
var _22f=null;
var _230=null;
var _231=null;
var _232=null;
var _233=document.getElementById(_22d.id.DESKTOP);
if(_233!=null){
_22f=_22d.ui.getLayoutExtents(_233,null,_22e,_22d);
}
var _234=document.getElementById(_22d.id.COLUMNS);
if(_234!=null){
_230=_22d.ui.getLayoutExtents(_234,null,_22e,_22d);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_232=_22d.ui.getLayoutExtents(col.domNode,null,_22e,_22d);
}else{
if(!col.columnContainer){
_231=_22d.ui.getLayoutExtents(col.domNode,null,_22e,_22d);
}
}
if(_231!=null&&_232!=null){
break;
}
}
}
this.layoutInfo={desktop:(_22f!=null?_22f:{}),columns:(_230!=null?_230:{}),column:(_231!=null?_231:{}),columnLayoutHeader:(_232!=null?_232:{})};
_22d.widget.PortletWindow.prototype.colWidth_pbE=((_231&&_231.pbE)?_231.pbE.w:0);
},_beforeAddOnLoad:function(){
this.win_onload=true;
},destroy:function(){
var _237=jetspeed;
var _238=dojo;
_237.ui.evtDisconnect("after",window,"onresize",_237.ui.windowResizeMgr,"onResize",_238.event);
_237.ui.evtDisconnect("before",_238,"addOnLoad",this,"_beforeAddOnLoad",_238.event);
var _239=this.portlet_windows;
var _23a=this.getPWins(true);
var pWin,_23c;
for(var i=0;i<_23a.length;i++){
pWin=_23a[i];
_23c=pWin.widgetId;
pWin.closeWindow();
delete _239[_23c];
this.portlet_window_count--;
}
this.portlets={};
this.portlet_count=0;
var _23e=_238.widget.byId(_237.id.PG_ED_WID);
if(_23e!=null){
_23e.editPageDestroy();
}
this._removeCols(document.getElementById(_237.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_23f){
if(_23f==null){
return null;
}
var _240=_23f.getAttribute("columnindex");
if(_240==null){
return null;
}
var _241=new Number(_240);
if(_241>=0&&_241<this.columns.length){
return this.columns[_241];
}
return null;
},getColIndexForNode:function(node){
var _243=null;
if(!this.columns){
return _243;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_243=i;
break;
}
}
return _243;
},getColWithNode:function(node){
var _246=this.getColIndexForNode(node);
return ((_246!=null&&_246>=0)?this.columns[_246]:null);
},getDescendantCols:function(_247){
var dMap={};
if(_247==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_247&&_247.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_24b){
if(!_24b){
return;
}
var _24c=(_24b.getName?_24b.getName():null);
if(_24c!=null){
this.menus[_24c]=_24b;
}
},getMenu:function(_24d){
if(_24d==null){
return null;
}
return this.menus[_24d];
},removeMenu:function(_24e){
if(_24e==null){
return;
}
var _24f=null;
if(dojo.lang.isString(_24e)){
_24f=_24e;
}else{
_24f=(_24e.getName?_24e.getName():null);
}
if(_24f!=null){
delete this.menus[_24f];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _250=[];
for(var _251 in this.menus){
_250.push(_251);
}
return _250;
},retrieveMenuDeclarations:function(_252,_253,_254){
contentListener=new jetspeed.om.MenusApiCL(_252,_253,_254);
this.clearMenus();
var _255="?action=getmenus";
if(_252){
_255+="&includeMenuDefs=true";
}
var _256=this.getPsmlUrl()+_255;
var _257="text/xml";
var _258=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_256,mimetype:_257},contentListener,_258,jetspeed.debugContentDumpIds);
},syncPageControls:function(_259){
var jsId=_259.id;
if(this.actionButtons==null){
return;
}
for(var _25b in this.actionButtons){
var _25c=false;
if(_25b==jsId.ACT_EDIT){
if(!this.editMode){
_25c=true;
}
}else{
if(_25b==jsId.ACT_VIEW){
if(this.editMode){
_25c=true;
}
}else{
if(_25b==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_25c=true;
}
}else{
_25c=true;
}
}
}
if(_25c){
this.actionButtons[_25b].style.display="";
}else{
this.actionButtons[_25b].style.display="none";
}
}
},renderPageControls:function(_25d){
var _25d=jetspeed;
var _25e=_25d.page;
var jsId=_25d.id;
var _260=dojo;
var _261=[];
if(this.actions!=null){
var addP=false;
for(var _263 in this.actions){
if(_263!=jsId.ACT_HELP){
_261.push(_263);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
addP=true;
if(this.actions[jsId.ACT_VIEW]==null){
_261.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
addP=true;
if(this.actions[jsId.ACT_EDIT]==null){
_261.push(jsId.ACT_EDIT);
}
}
var _264=(_25e.rootFragmentId?_25e.layouts[_25e.rootFragmentId]:null);
var _265=(!(_264==null||_264.layoutActionsDisabled));
if(_265){
_265=_25e._perms(_25d.prefs,_25d.id.PM_P_AD,String.fromCharCode);
if(_265&&!this.isUA()&&(addP||_25e.canNPE())){
_261.push(jsId.ACT_ADDPORTLET);
}
}
}
var _266=_260.byId(jsId.PAGE_CONTROLS);
if(_266!=null&&_261!=null&&_261.length>0){
var _267=_25d.prefs;
var jsUI=_25d.ui;
var _269=_260.event;
var _26a=_25e.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _26b=this.actionButtonTooltips;
for(var i=0;i<_261.length;i++){
var _263=_261[i];
var _26d=document.createElement("div");
_26d.className="portalPageActionButton";
_26d.style.backgroundImage="url("+_267.getLayoutRootUrl()+"/images/desktop/"+_263+".gif)";
_26d.actionName=_263;
this.actionButtons[_263]=_26d;
_266.appendChild(_26d);
jsUI.evtConnect("after",_26d,"onclick",this,"pageActionButtonClick",_269);
if(_267.pageActionButtonTooltip){
var _26e=null;
if(_267.desktopActionLabels!=null){
_26e=_267.desktopActionLabels[_263];
}
if(_26e==null||_26e.length==0){
_26e=_260.string.capitalize(_263);
}
_26b.push(_26a.addNode(_26d,_26e,true,null,null,null,_25d,jsUI,_269));
}
}
}
},_destroyPageControls:function(){
var _26f=jetspeed;
if(this.actionButtons){
for(var _270 in this.actionButtons){
var _271=this.actionButtons[_270];
if(_271){
_26f.ui.evtDisconnect("after",_271,"onclick",this,"pageActionButtonClick");
}
}
}
var _272=dojo.byId(_26f.id.PAGE_CONTROLS);
if(_272!=null&&_272.childNodes&&_272.childNodes.length>0){
for(var i=(_272.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_272.childNodes[i]);
}
}
_26f.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_275){
var _276=jetspeed;
if(_275==null){
return;
}
if(_275==_276.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_275==_276.id.ACT_EDIT){
_276.changeActionForPortlet(this.rootFragmentId,null,_276.id.ACT_EDIT,new _276.om.PageChangeActionCL());
_276.editPageInitiate(_276);
}else{
if(_275==_276.id.ACT_VIEW){
_276.editPageTerminate(_276);
}else{
var _277=this.getPageAction(_275);
if(_277==null){
return;
}
if(_277.url==null){
return;
}
var _278=_276.url.basePortalUrl()+_276.url.path.DESKTOP+"/"+_277.url;
_276.pageNavigate(_278);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_27a,_27b){
var _27c=jetspeed;
var jsId=_27c.id;
if(!_27b){
_27b=escape(this.getPagePathAndQuery());
}else{
_27b=escape(_27b);
}
var _27e=_27c.url.basePortalUrl()+_27c.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_27b;
if(_27a!=null){
_27e+="&jslayoutid="+escape(_27a);
}
if(!this.editMode){
_27e+="&"+_27c.id.ADDP_RFRAG+"="+escape(this.rootFragmentId);
}
if(this.actions&&(this.actions[jsId.ACT_EDIT]||this.actions[jsId.ACT_VIEW])){
_27c.changeActionForPortlet(this.rootFragmentId,null,jsId.ACT_EDIT,new _27c.om.PageChangeActionCL(_27e));
}else{
if(!this.isUA()){
_27c.pageNavigate(_27e);
}
}
},addPortletTerminate:function(_27f,_280){
var _281=jetspeed;
var _282=_281.url.getQueryParameter(document.location.href,_281.id.ADDP_RFRAG);
if(_282!=null&&_282.length>0){
var _283=_280;
var qPos=_280.indexOf("?");
if(qPos>0){
_283.substring(0,qPos);
}
_281.changeActionForPortlet(_282,null,_281.id.ACT_VIEW,new _281.om.PageChangeActionCL(_27f),_283);
}else{
_281.pageNavigate(_27f);
}
},setPageModePortletActions:function(_285){
if(_285==null||_285.actions==null){
return;
}
var jsId=jetspeed.id;
if(_285.actions[jsId.ACT_REMOVEPORTLET]==null){
_285.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_287){
if(this.pageUrl!=null&&!_287){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _289=jsU.path.SERVER+((_287)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
var _28a=jsU.parse(_289);
var _28b=null;
if(this.pageUrlFallback!=null){
_28b=jsU.parse(this.pageUrlFallback);
}else{
_28b=jsU.parse(window.location.href);
}
if(_28a!=null&&_28b!=null){
var _28c=_28b.query;
if(_28c!=null&&_28c.length>0){
var _28d=_28a.query;
if(_28d!=null&&_28d.length>0){
_289=_289+"&"+_28c;
}else{
_289=_289+"?"+_28c;
}
}
}
if(!_287){
this.pageUrl=_289;
}
return _289;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _28f=this.getPath();
var _290=jsU.parse(_28f);
var _291=null;
if(this.pageUrlFallback!=null){
_291=jsU.parse(this.pageUrlFallback);
}else{
_291=jsU.parse(window.location.href);
}
if(_290!=null&&_291!=null){
var _292=_291.query;
if(_292!=null&&_292.length>0){
var _293=_290.query;
if(_293!=null&&_293.length>0){
_28f=_28f+"&"+_292;
}else{
_28f=_28f+"?"+_292;
}
}
}
this.pagePathAndQuery=_28f;
return _28f;
},getPageDirectory:function(_294){
var _295="/";
var _296=(_294?this.getRealPath():this.getPath());
if(_296!=null){
var _297=_296.lastIndexOf("/");
if(_297!=-1){
if((_297+1)<_296.length){
_295=_296.substring(0,_297+1);
}else{
_295=_296;
}
}
}
return _295;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_299){
if(!_299){
_299="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_299)){
return jsU.path.SERVER+jsU.path.DESKTOP+_299;
}
return _299;
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
jetspeed.om.Column=function(_29b,_29c,size,_29e,_29f,_2a0){
this.layoutColumnIndex=_29b;
this.layoutId=_29c;
this.size=size;
this.pageColumnIndex=new Number(_29e);
if(typeof _29f!="undefined"){
this.layoutActionsDisabled=_29f;
}
if((typeof _2a0!="undefined")&&_2a0!=null){
this.layoutDepth=_2a0;
}
this.id="jscol_"+_29e;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,layoutDepth:null,layoutMaxChildDepth:0,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_2a1){
var _2a2=this.styleClass;
var _2a3=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_2a3>0){
_2a2+=" desktopColumnClear-PRIVATE";
}
var _2a4=document.createElement("div");
_2a4.setAttribute("columnindex",_2a3);
_2a4.style.width=this.size+"%";
if(this.layoutHeader){
_2a2=this.styleLayoutClass;
_2a4.setAttribute("layoutid",this.layoutId);
}
_2a4.className=_2a2;
_2a4.id=this.getId();
this.domNode=_2a4;
if(_2a1!=null){
_2a1.appendChild(_2a4);
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
},_updateLayoutDepth:function(_2a7){
var _2a8=this.layoutDepth;
if(_2a8!=null&&_2a7!=_2a8){
this.layoutDepth=_2a7;
this.layoutDepthChanged();
}
},_updateLayoutChildDepth:function(_2a9){
this.layoutMaxChildDepth=(_2a9==null?0:_2a9);
}});
jetspeed.om.Portlet=function(_2aa,_2ab,_2ac,_2ad,_2ae,_2af,_2b0,_2b1){
this.name=_2aa;
this.entityId=_2ab;
this.properties=_2ad;
this.actions=_2ae;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_2af;
this.currentActionMode=_2b0;
if(_2ac){
this.contentRetriever=_2ac;
}
this.layoutActionsDisabled=false;
if(typeof _2b1!="undefined"){
this.layoutActionsDisabled=_2b1;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _2b2=jetspeed;
var jsId=_2b2.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _2b4=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_2b2.prefs.windowTiling){
if(_2b4=="true"){
_2b4=true;
}else{
if(_2b4=="false"){
_2b4=false;
}else{
if(_2b4!=true&&_2b4!=false){
_2b4=true;
}
}
}
}else{
_2b4=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_2b4;
var _2b5=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_2b5=="true"){
_2b5=true;
}else{
if(_2b4=="false"){
_2b5=false;
}else{
if(_2b5!=true&&_2b5!=false){
_2b5=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2b5;
var _2b6=this.properties[jsId.PP_WINDOW_TITLE];
if(!_2b6&&this.name){
var re=(/^[^:]*:*/);
_2b6=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_2b6;
}
},postParseAnnotateHtml:function(_2b8){
var _2b9=jetspeed;
var _2ba=_2b9.portleturl;
if(_2b8){
var _2bb=_2b8;
var _2bc=_2bb.getElementsByTagName("form");
var _2bd=_2b9.debug.postParseAnnotateHtml;
var _2be=_2b9.debug.postParseAnnotateHtmlDisableAnchors;
if(_2bc){
for(var i=0;i<_2bc.length;i++){
var _2c0=_2bc[i];
var _2c1=_2c0.action;
var _2c2=_2ba.parseContentUrl(_2c1);
var op=_2c2.operation;
var _2c4=(op==_2ba.PORTLET_REQUEST_ACTION||op==_2ba.PORTLET_REQUEST_RENDER);
var _2c5=false;
if(dojo.io.formHasFile(_2c0)){
if(_2c4){
var _2c6=_2b9.url.parse(_2c1);
_2c6=_2b9.url.addQueryParameter(_2c6,"encoder","desktop",true);
_2c6=_2b9.url.addQueryParameter(_2c6,"jsdajax","false",true);
_2c0.action=_2c6.toString();
}else{
_2c5=true;
}
}else{
if(_2c4){
var _2c7=_2ba.genPseudoUrl(_2c2,true);
_2c0.action=_2c7;
var _2c8=new _2b9.om.ActionRenderFormBind(_2c0,_2c2.url,_2c2.portletEntityId,op);
if(_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+op+") for form with action: "+_2c1);
}
}else{
if(_2c1==null||_2c1.length==0){
var _2c8=new _2b9.om.ActionRenderFormBind(_2c0,null,this.entityId,null);
if(_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
_2c5=true;
}
}
}
if(_2c5&&_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_2c1);
}
}
}
var _2c9=_2bb.getElementsByTagName("a");
if(_2c9){
for(var i=0;i<_2c9.length;i++){
var _2ca=_2c9[i];
var _2cb=_2ca.href;
var _2c2=_2ba.parseContentUrl(_2cb);
var _2cc=null;
if(!_2be){
_2cc=_2ba.genPseudoUrl(_2c2);
}
if(!_2cc){
if(_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2cb);
}
}else{
if(_2cc==_2cb){
if(_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2cb);
}
}else{
if(_2bd){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2cb+" with: "+_2cc);
}
_2ca.href=_2cc;
}
}
}
}
}
},getPWin:function(){
var _2cd=jetspeed;
var _2ce=this.properties[_2cd.id.PP_WIDGET_ID];
if(_2ce){
return _2cd.page.getPWin(_2ce);
}
return null;
},getCurWinState:function(_2cf){
var _2d0=null;
try{
var _2d1=this.getPWin();
if(!_2d1){
return null;
}
_2d0=_2d1.getCurWinStateForPersist(_2cf);
if(!_2cf){
if(_2d0.layout==null){
_2d0.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2d0;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2d2,_2d3){
var _2d4=jetspeed;
var jsId=_2d4.id;
if(!_2d2){
_2d2={};
}
var _2d6=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2d7=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2d2[jsId.PP_WINDOW_POSITION_STATIC]=_2d6;
_2d2[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2d7;
var _2d8=this.properties["width"];
if(!_2d3&&_2d8!=null&&_2d8>0){
_2d2.width=Math.floor(_2d8);
}else{
if(_2d3){
_2d2.width=-1;
}
}
var _2d9=this.properties["height"];
if(!_2d3&&_2d9!=null&&_2d9>0){
_2d2.height=Math.floor(_2d9);
}else{
if(_2d3){
_2d2.height=-1;
}
}
if(!_2d6||!_2d4.prefs.windowTiling){
var _2da=this.properties["x"];
if(!_2d3&&_2da!=null&&_2da>=0){
_2d2.left=Math.floor(((_2da>0)?_2da:0));
}else{
if(_2d3){
_2d2.left=-1;
}
}
var _2db=this.properties["y"];
if(!_2d3&&_2db!=null&&_2db>=0){
_2d2.top=Math.floor(((_2db>0)?_2db:0));
}else{
_2d2.top=-1;
}
var _2dc=this._getInitialZIndex(_2d3);
if(_2dc!=null){
_2d2.zIndex=_2dc;
}
}
return _2d2;
},_initWinState:function(_2dd,_2de){
var _2df=jetspeed;
var _2e0=(_2dd?_2dd:{});
this.getInitialWinDims(_2e0,_2de);
if(_2df.debug.initWinState){
var _2e1=this.properties[_2df.id.PP_WINDOW_POSITION_STATIC];
if(!_2e1||!_2df.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2e0.zIndex+" x="+_2e0.left+" y="+_2e0.top+" width="+_2e0.width+" height="+_2e0.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2e0.column+" row="+_2e0.row+" width="+_2e0.width+" height="+_2e0.height);
}
}
this.lastSavedWindowState=_2e0;
return _2e0;
},_getInitialZIndex:function(_2e2){
var _2e3=null;
var _2e4=this.properties["z"];
if(!_2e2&&_2e4!=null&&_2e4>=0){
_2e3=Math.floor(_2e4);
}else{
if(_2e2){
_2e3=-1;
}
}
return _2e3;
},_getChangedWindowState:function(_2e5){
var jsId=jetspeed.id;
var _2e7=this.getSavedWinState();
if(_2e7&&dojo.lang.isEmpty(_2e7)){
_2e7=null;
_2e5=false;
}
var _2e8=this.getCurWinState(_2e5);
var _2e9=_2e8[jsId.PP_WINDOW_POSITION_STATIC];
var _2ea=!_2e9;
if(!_2e7){
var _2eb={state:_2e8,positionChanged:true,extendedPropChanged:true};
if(_2ea){
_2eb.zIndexChanged=true;
}
return _2eb;
}
var _2ec=false;
var _2ed=false;
var _2ee=false;
var _2ef=false;
for(var _2f0 in _2e8){
if(_2e8[_2f0]!=_2e7[_2f0]){
if(_2f0==jsId.PP_WINDOW_POSITION_STATIC||_2f0==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2ec=true;
_2ee=true;
_2ed=true;
}else{
if(_2f0=="zIndex"){
if(_2ea){
_2ec=true;
_2ef=true;
}
}else{
_2ec=true;
_2ed=true;
}
}
}
}
if(_2ec){
var _2eb={state:_2e8,positionChanged:_2ed,extendedPropChanged:_2ee};
if(_2ea){
_2eb.zIndexChanged=_2ef;
}
return _2eb;
}
return null;
},getPortletUrl:function(_2f1){
var _2f2=jetspeed;
var _2f3=_2f2.url;
var _2f4=null;
if(_2f1&&_2f1.url){
_2f4=_2f1.url;
}else{
if(_2f1&&_2f1.formNode){
var _2f5=_2f1.formNode.getAttribute("action");
if(_2f5){
_2f4=_2f5;
}
}
}
if(_2f4==null){
_2f4=_2f3.basePortalUrl()+_2f3.path.PORTLET+_2f2.page.getPath();
}
if(!_2f1.dontAddQueryArgs){
_2f4=_2f3.parse(_2f4);
_2f4=_2f3.addQueryParameter(_2f4,"entity",this.entityId,true);
_2f4=_2f3.addQueryParameter(_2f4,"portlet",this.name,true);
_2f4=_2f3.addQueryParameter(_2f4,"encoder","desktop",true);
if(_2f1.jsPageUrl!=null){
var _2f6=_2f1.jsPageUrl.query;
if(_2f6!=null&&_2f6.length>0){
_2f4=_2f4.toString()+"&"+_2f6;
}
}
}
if(_2f1){
_2f1.url=_2f4.toString();
}
return _2f4;
},_submitAjaxApi:function(_2f7,_2f8,_2f9){
var _2fa=jetspeed;
var _2fb="?action="+_2f7+"&id="+this.entityId+_2f8;
var _2fc=_2fa.url.basePortalUrl()+_2fa.url.path.AJAX_API+_2fa.page.getPath()+_2fb;
var _2fd="text/xml";
var _2fe=new _2fa.om.Id(_2f7,this.entityId);
_2fe.portlet=this;
_2fa.url.retrieveContent({url:_2fc,mimetype:_2fd},_2f9,_2fe,_2fa.debugContentDumpIds);
},submitWinState:function(_2ff,_300){
var _301=jetspeed;
var jsId=_301.id;
if(_301.page.isUA()||(!(_301.page.getPageAction(jsId.ACT_EDIT)||_301.page.getPageAction(jsId.ACT_VIEW)||_301.page.canNPE()))){
return;
}
var _303=null;
if(_300){
_303={state:this._initWinState(null,true)};
}else{
_303=this._getChangedWindowState(_2ff);
}
if(_303){
var _304=_303.state;
var _305=_304[jsId.PP_WINDOW_POSITION_STATIC];
var _306=_304[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _307=null;
if(_303.extendedPropChanged){
var _308=jsId.PP_PROP_SEPARATOR;
var _309=jsId.PP_PAIR_SEPARATOR;
_307=jsId.PP_STATICPOS+_308+_305.toString();
_307+=_309+jsId.PP_FITHEIGHT+_308+_306.toString();
_307=escape(_307);
}
var _30a="";
var _30b=null;
if(_305){
_30b="moveabs";
if(_304.column!=null){
_30a+="&col="+_304.column;
}
if(_304.row!=null){
_30a+="&row="+_304.row;
}
if(_304.layout!=null){
_30a+="&layoutid="+_304.layout;
}
if(_304.height!=null){
_30a+="&height="+_304.height;
}
}else{
_30b="move";
if(_304.zIndex!=null){
_30a+="&z="+_304.zIndex;
}
if(_304.width!=null){
_30a+="&width="+_304.width;
}
if(_304.height!=null){
_30a+="&height="+_304.height;
}
if(_304.left!=null){
_30a+="&x="+_304.left;
}
if(_304.top!=null){
_30a+="&y="+_304.top;
}
}
if(_307!=null){
_30a+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_307;
}
this._submitAjaxApi(_30b,_30a,new _301.om.MoveApiCL(this,_304));
if(!_2ff&&!_300){
if(!_305&&_303.zIndexChanged){
var _30c=_301.page.getPortletArray();
if(_30c&&(_30c.length-1)>0){
for(var i=0;i<_30c.length;i++){
var _30e=_30c[i];
if(_30e&&_30e.entityId!=this.entityId){
if(!_30e.properties[_301.id.PP_WINDOW_POSITION_STATIC]){
_30e.submitWinState(true);
}
}
}
}
}else{
if(_305){
}
}
}
}
},retrieveContent:function(_30f,_310,_311){
if(_30f==null){
_30f=new jetspeed.om.PortletCL(this,_311,_310);
}
if(!_310){
_310={};
}
var _312=this;
_312.getPortletUrl(_310);
this.contentRetriever.getContent(_310,_30f,_312,jetspeed.debugContentDumpIds);
},setPortletContent:function(_313,_314,_315){
var _316=this.getPWin();
if(_315!=null&&_315.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_315;
if(_316&&!this.loadingIndicatorIsShown()){
_316.setPortletTitle(_315);
}
}
if(_316){
_316.setPortletContent(_313,_314);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _318=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _319=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _31a=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _31b=this.getPWin();
if(_31b&&(_318||_319)){
var _31c=_31b.getPortletTitle();
if(_31c&&(_31c==_318||_31c==_319)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_31d){
var _31e=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_31e=jetspeed.prefs.desktopActionLabels[_31d];
if(_31e!=null&&_31e.length==0){
_31e=null;
}
}
return _31e;
},loadingIndicatorShow:function(_31f){
if(_31f&&!this.loadingIndicatorIsShown()){
var _320=this._getLoadingActionLabel(_31f);
var _321=this.getPWin();
if(_321&&_320){
_321.setPortletTitle(_320);
}
}
},loadingIndicatorHide:function(){
var _322=this.getPWin();
if(_322){
_322.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_324,_325){
var _326=jetspeed;
var _327=_326.url;
var _328=null;
if(_324!=null){
_328=this.getAction(_324);
}
var _329=_325;
if(_329==null&&_328!=null){
_329=_328.url;
}
if(_329==null){
return;
}
var _32a=_327.basePortalUrl()+_327.path.PORTLET+"/"+_329+_326.page.getPath();
if(_324!=_326.id.ACT_PRINT){
this.retrieveContent(null,{url:_32a});
}else{
var _32b=_326.page.getPageUrl();
_32b=_327.addQueryParameter(_32b,"jsprintmode","true");
_32b=_327.addQueryParameter(_32b,"jsaction",escape(_328.url));
_32b=_327.addQueryParameter(_32b,"jsentity",this.entityId);
_32b=_327.addQueryParameter(_32b,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_32b.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_32d,_32e,_32f){
if(_32d){
this.actions=_32d;
}else{
this.actions={};
}
this.currentActionState=_32e;
this.currentActionMode=_32f;
this.syncActions();
},syncActions:function(){
var _330=jetspeed;
_330.page.setPageModePortletActions(this);
var _331=this.getPWin();
if(_331){
_331.actionBtnSync(_330,_330.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_334,_335){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_334;
this.submitOperation=_335;
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
},eventConfMgr:function(_338){
var fn=(_338)?"disconnect":"connect";
var _33a=dojo.event;
var form=this.form;
_33a[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_33a[fn]("after",node,"onclick",this,"click",null);
}
}
var _33e=form.getElementsByTagName("input");
for(var i=0;i<_33e.length;i++){
var _33f=_33e[i];
if(_33f.type.toLowerCase()=="image"&&_33f.form==form){
_33a[fn]("after",_33f,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_33a[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_341){
var _342=true;
if(this.isFormSubmitInProgress()){
_342=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_342=false;
}
}
}
return _342;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _344=jetspeed.portleturl.parseContentUrl(this.form.action);
var _345={};
if(_344.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_344.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _346=jetspeed.portleturl.genPseudoUrl(_344,true);
this.form.action=_346;
this.submitOperation=_344.operation;
this.entityId=_344.portletEntityId;
_345.url=_344.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_345.formFilter=dojo.lang.hitch(this,"formFilter");
_345.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_345),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_345),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_347){
if(_347!=undefined){
this.formSubmitInProgress=_347;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_348,_349,_34a){
this.portlet=_348;
this.suppressGetActions=_349;
this.formbind=null;
if(_34a!=null&&_34a.submitFormBindObject!=null){
this.formbind=_34a.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_34b){
if(this.portlet==null){
return;
}
if(_34b){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_34c,_34d,_34e,http){
var _350=null;
var _351=(_34c?_34c.indexOf("</JS_PORTLET_HEAD_ELEMENTS>"):-1);
if(_351!=-1){
_351+="</JS_PORTLET_HEAD_ELEMENTS>".length;
_350=_34c.substring(0,_351);
_34c=_34c.substring(_351);
jetspeed.contributeHeadElements(dojo.dom.createDocumentFromText(_350).documentElement);
}
var _352=null;
if(http!=null){
try{
_352=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_352!=null){
_352=unescape(_352);
}
}
_34e.setPortletContent(_34c,_34d,_352);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_34e.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_354,_355,_356){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_355+" type: "+type+jetspeed.formatError(_354));
}};
jetspeed.om.PortletActionCL=function(_357,_358){
this.portlet=_357;
this.formbind=null;
if(_358!=null&&_358.submitFormBindObject!=null){
this.formbind=_358.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_359){
if(this.portlet==null){
return;
}
if(_359){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_35a,_35b,_35c,http){
var _35e=jetspeed;
var _35f=null;
var _360=false;
var _361=_35e.portleturl.parseContentUrl(_35a);
if(_361.operation==_35e.portleturl.PORTLET_REQUEST_ACTION||_361.operation==_35e.portleturl.PORTLET_REQUEST_RENDER){
if(_35e.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_361.operation+"-url in response body: "+_35a+"  url: "+_361.url+" entity-id: "+_361.portletEntityId);
}
_35f=_361.url;
}else{
if(_35e.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_35a);
}
_35f=_35a;
if(_35f){
var _362=_35f.indexOf(_35e.url.basePortalUrl()+_35e.url.path.PORTLET);
if(_362==-1){
_360=true;
window.location.href=_35f;
_35f=null;
}else{
if(_362>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_35a);
_35f=null;
}
}
}
}
if(_35f!=null&&!_35e.noActionRender){
if(_35e.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_35f);
}
var _363=new jetspeed.PortletRenderer(false,false,false,_35f,true);
_363.renderAll();
}else{
this._loading(false);
}
if(!_360&&this.portlet){
_35e.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_365,_366,_367){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_365));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _368=this.getUrl();
if(_368){
var _369=jetspeed;
if(!_369.prefs.ajaxPageNavigation||_369.url.urlStartsWithHttp(_368)){
_369.pageNavigate(_368,this.getTarget());
}else{
_369.updatePage(_368);
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
jetspeed.om.Menu=function(_36a,_36b){
this._is_parsed=false;
this.name=_36a;
this.type=_36b;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_36c){
if(!_36c){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_36c);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_36e){
if(!this.hasOptions()){
return null;
}
if(_36e==0||_36e>0){
if(_36e>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_36e];
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
var _370=this.options[i];
if(_370 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_372,_373){
var _374=this.parseMenu(data,_373.menuName,_373.menuType);
_373.page.putMenu(_374);
},notifyFailure:function(type,_376,_377,_378){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_378.toString()+"] url: "+_377+" type: "+type+jetspeed.formatError(_376));
},parseMenu:function(node,_37a,_37b){
var menu=null;
var _37d=node.getElementsByTagName("js");
if(!_37d||_37d.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _37e=_37d[0].childNodes;
for(var i=0;i<_37e.length;i++){
var _380=_37e[i];
if(_380.nodeType!=1){
continue;
}
var _381=_380.nodeName;
if(_381=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_380,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_37a;
}
if(menu.type==null){
menu.type=_37b;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _384=null;
var _385=node.childNodes;
for(var i=0;i<_385.length;i++){
var _387=_385[i];
if(_387.nodeType!=1){
continue;
}
var _388=_387.nodeName;
if(_388=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_387,new jetspeed.om.Menu()));
}
}else{
if(_388=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_387,new jetspeed.om.MenuOption()));
}
}else{
if(_388=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_387,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_388){
mObj[_388]=((_387&&_387.firstChild)?_387.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_389,_38a,_38b){
this.includeMenuDefs=_389;
this.isPageUpdate=_38a;
this.initEditModeConf=_38b;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_38d,_38e){
var _38f=this.getMenuDefs(data,_38d,_38e);
for(var i=0;i<_38f.length;i++){
var mObj=_38f[i];
_38e.page.putMenu(mObj);
}
this.notifyFinished(_38e);
},getMenuDefs:function(data,_393,_394){
var _395=[];
var _396=data.getElementsByTagName("menu");
for(var i=0;i<_396.length;i++){
var _398=_396[i].getAttribute("type");
if(this.includeMenuDefs){
_395.push(this.parseMenuObject(_396[i],new jetspeed.om.Menu(null,_398)));
}else{
var _399=_396[i].firstChild.nodeValue;
_395.push(new jetspeed.om.Menu(_399,_398));
}
}
return _395;
},notifyFailure:function(type,_39b,_39c,_39d){
dojo.raise("MenusApiCL error ["+_39d.toString()+"] url: "+_39c+" type: "+type+jetspeed.formatError(_39b));
},notifyFinished:function(_39e){
var _39f=jetspeed;
if(this.includeMenuDefs){
_39f.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_39f.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_39f.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_3a0){
this.portletEntityId=_3a0;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_3a2,_3a3){
if(jetspeed.url.checkAjaxApiResponse(_3a2,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_3a4){
var _3a5=jetspeed.page.getPortlet(this.portletEntityId);
if(_3a5){
if(_3a4){
_3a5.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3a5.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_3a7,_3a8,_3a9){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_3a9.toString()+"] url: "+_3a8+" type: "+type+jetspeed.formatError(_3a7));
}});
jetspeed.om.PageChangeActionCL=function(_3aa){
this.pageActionUrl=_3aa;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_3ac,_3ad){
if(jetspeed.url.checkAjaxApiResponse(_3ac,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_3af,_3b0,_3b1){
dojo.raise("PageChangeActionCL error ["+_3b1.toString()+"] url: "+_3b0+" type: "+type+jetspeed.formatError(_3af));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_3b3,_3b4){
var _3b5=jetspeed;
if(_3b5.url.checkAjaxApiResponse(_3b3,data,null,false,"user-info")){
var _3b6=data.getElementsByTagName("js");
if(_3b6&&_3b6.length==1){
var root=_3b6[0];
var un=_3b5.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _3ba=root.getElementsByTagName("role");
if(_3ba!=null){
for(var i=0;i<_3ba.length;i++){
var role=(_3ba[i].firstChild?_3ba[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_3b5.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_3be,_3bf,_3c0){
dojo.raise("UserInfoCL error ["+_3c0.toString()+"] url: "+_3bf+" type: "+type+jetspeed.formatError(_3be));
}});
jetspeed.om.PortletActionsCL=function(_3c1){
this.portletEntityIds=_3c1;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_3c2){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _3c4=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_3c4){
if(_3c2){
_3c4.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3c4.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_3c6,_3c7){
var _3c8=jetspeed;
this._loading(false);
if(_3c8.url.checkAjaxApiResponse(_3c6,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_3c8.page);
}
},processPortletActionsResponse:function(node,_3ca){
var _3cb=this.parsePortletActionsResponse(node,_3ca);
for(var i=0;i<_3cb.length;i++){
var _3cd=_3cb[i];
var _3ce=_3cd.id;
var _3cf=_3ca.getPortlet(_3ce);
if(_3cf!=null){
_3cf.updateActions(_3cd.actions,_3cd.currentActionState,_3cd.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3d1){
var _3d2=new Array();
var _3d3=node.getElementsByTagName("js");
if(!_3d3||_3d3.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3d2;
}
var _3d4=_3d3[0].childNodes;
for(var i=0;i<_3d4.length;i++){
var _3d6=_3d4[i];
if(_3d6.nodeType!=1){
continue;
}
var _3d7=_3d6.nodeName;
if(_3d7=="portlets"){
var _3d8=_3d6;
var _3d9=_3d8.childNodes;
for(var pI=0;pI<_3d9.length;pI++){
var _3db=_3d9[pI];
if(_3db.nodeType!=1){
continue;
}
var _3dc=_3db.nodeName;
if(_3dc=="portlet"){
var _3dd=this.parsePortletElement(_3db,_3d1);
if(_3dd!=null){
_3d2.push(_3dd);
}
}
}
}
}
return _3d2;
},parsePortletElement:function(node,_3df){
var _3e0=node.getAttribute("id");
if(_3e0!=null){
var _3e1=_3df._parsePSMLActions(node,null);
var _3e2=_3df._parsePSMLChildOrAttr(node,"state");
var _3e3=_3df._parsePSMLChildOrAttr(node,"mode");
return {id:_3e0,actions:_3e1,currentActionState:_3e2,currentActionMode:_3e3};
}
return null;
},notifyFailure:function(type,_3e5,_3e6,_3e7){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3e7.toString()+"] url: "+_3e6+" type: "+type+jetspeed.formatError(_3e5));
}});
jetspeed.om.MoveApiCL=function(_3e8,_3e9){
this.portlet=_3e8;
this.changedState=_3e9;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3ea){
if(this.portlet==null){
return;
}
if(_3ea){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3ec,_3ed){
var _3ee=jetspeed;
this._loading(false);
dojo.lang.mixin(_3ed.portlet.lastSavedWindowState,this.changedState);
var _3ef=true;
if(djConfig.isDebug&&_3ee.debug.submitWinState){
_3ef=true;
}
var _3f0=_3ee.url.checkAjaxApiResponse(_3ec,data,["refresh"],_3ef,("move-portlet ["+_3ed.portlet.entityId+"]"),_3ee.debug.submitWinState);
if(_3f0=="refresh"){
var _3f1=_3ee.page.getPageUrl();
if(!_3ee.prefs.ajaxPageNavigation){
_3ee.pageNavigate(_3f1,null,true);
}else{
_3ee.updatePage(_3f1,false,true);
}
}
},notifyFailure:function(type,_3f3,_3f4,_3f5){
this._loading(false);
dojo.debug("submitWinState error ["+_3f5.entityId+"] url: "+_3f4+" type: "+type+jetspeed.formatError(_3f3));
}};
jetspeed.postload_addEventListener=function(node,_3f7,fnc,_3f9){
if((_3f7=="load"||_3f7=="DOMContentLoaded"||_3f7=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3f7,fnc,_3f9);
}
};
jetspeed.postload_attachEvent=function(node,_3fb,fnc){
if(_3fb=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3fb,fnc);
}
};
jetspeed.postload_docwrite=function(_3fd){
if(!_3fd){
return;
}
_3fd=_3fd.replace(/^\s+|\s+$/g,"");
var _3fe=/^<script\b([^>]*)>.*?<\/script>/i;
var _3ff=_3fe.exec(_3fd);
if(_3ff){
_3fd=null;
var _400=_3ff[1];
if(_400){
var _401=/\bid\s*=\s*([^\s]+)/i;
var _402=_401.exec(_400);
if(_402){
var _403=_402[1];
_3fd="<img id="+_403+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3fd){
var _405=dojo;
tn=_405.doc().createElement("div");
tn.style.visibility="hidden";
_405.body().appendChild(tn);
tn.innerHTML=_3fd;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_406,_407,_408){
if(_406==document||_406==window){
if(_408&&_408.length>0){
var _409=jetspeed.portleturl;
if(_408.indexOf(_409.DESKTOP_ACTION_PREFIX_URL)!=0&&_408.indexOf(_409.DESKTOP_RENDER_PREFIX_URL)!=0){
_406.location=_408;
}
}
}else{
if(_406!=null){
var _40a=_407.indexOf(".");
if(_40a==-1){
_406[_407]=_408;
}else{
var _40b=_407.substring(0,_40a);
var _40c=_406[_40b];
if(_40c){
var _40d=_407.substring(_40a+1);
if(_40d){
_40c[_40d]=_408;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _40f=document.createElement("script");
_40f.setAttribute("type","text/plain");
_40f.setAttribute("language","ignore");
_40f.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_40f);
return _40f;
};
jetspeed.containsElement=function(_410,_411,_412,_413){
if(!_410||!_411||!_412){
return false;
}
if(!_413){
_413=document;
}
var _414=_413.getElementsByTagName(_410);
if(!_414){
return false;
}
for(var i=0;i<_414.length;++i){
var _416=_414[i].getAttribute(_411);
if(_416==_412){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _417=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _418=_417.concat([" height: ","","",";"]);
var _419=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _41a=_418.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _41b=_41a.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_417,cssHeight:_418,cssWidthHeight:_419,cssOverflow:_41a,cssPosition:_41b,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_41c,_41d,_41e,_41f,_420,_421){
var djH=dojo.html;
var jsId=jetspeed.id;
var _424=null;
var _425=-1;
var _426=-1;
var _427=-1;
if(_41c){
var _428=_41c.childNodes;
if(_428){
_427=_428.length;
}
_424=[];
if(_427>0){
var _429="",_42a="";
if(!_421){
_429=jsId.PWIN_CLASS;
}
if(_41e){
_429+=((_429.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_41f){
_429+=((_429.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_420&&!_41f){
_429+=((_429.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_41f&&!_420){
_42a=((_42a.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_429.length>0){
var _42b=new RegExp("(^|\\s+)("+_429+")(\\s+|$)");
var _42c=null;
if(_42a.length>0){
_42c=new RegExp("(^|\\s+)("+_42a+")(\\s+|$)");
}
var _42d,_42e,_42f;
for(var i=0;i<_427;i++){
_42d=_428[i];
_42e=false;
_42f=djH.getClass(_42d);
if(_42b.test(_42f)&&(_42c==null||!_42c.test(_42f))){
_424.push(_42d);
_42e=true;
}
if(_41d&&_42d==_41d){
if(!_42e){
_424.push(_42d);
}
_425=i;
_426=_424.length-1;
}
}
}
}
}
return {matchingNodes:_424,totalNodes:_427,matchNodeIndex:_425,matchNodeIndexInMatchingNodes:_426};
},getPWinsFromNodes:function(_431){
var _432=jetspeed.page;
var _433=null;
if(_431){
_433=new Array();
for(var i=0;i<_431.length;i++){
var _435=_432.getPWin(_431[i].id);
if(_435){
_433.push(_435);
}
}
}
return _433;
},createPortletWindow:function(_436,_437,_438){
var _439=false;
if(djConfig.isDebug&&_438.debug.profile){
_439=true;
dojo.profile.start("createPortletWindow");
}
var _43a=(_437!=null);
var _43b=false;
var _43c=null;
if(_43a&&_437<_438.page.columns.length&&_437>=0){
_43c=_438.page.columns[_437].domNode;
}
if(_43c==null){
_43b=true;
_43c=document.getElementById(_438.id.DESKTOP);
}
if(_43c==null){
return;
}
var _43d={};
if(_436.isPortlet){
_43d.portlet=_436;
if(_438.prefs.printModeOnly!=null){
_43d.printMode=true;
}
if(_43b){
_436.properties[_438.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_438.widget.PortletWindow.prototype.altInitParamsDef(_43d,_436);
if(_43b){
pwP.altInitParams[_438.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _43f=new _438.widget.PortletWindow();
_43f.build(_43d,_43c);
if(_439){
dojo.profile.end("createPortletWindow");
}
return _43f;
},getLayoutExtents:function(node,_441,_442,_443){
if(!_441){
_441=_442.gcs(node);
}
var pad=_442._getPadExtents(node,_441);
var _445=_442._getBorderExtents(node,_441);
var _446={l:(pad.l+_445.l),t:(pad.t+_445.t),w:(pad.w+_445.w),h:(pad.h+_445.h)};
var _447=_442._getMarginExtents(node,_441,_443);
return {bE:_445,pE:pad,pbE:_446,mE:_447,lessW:(_446.w+_447.w),lessH:(_446.h+_447.h)};
},getContentBoxSize:function(node,_449){
var w=node.clientWidth,h,_44c;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_44c=_449.pbE;
}else{
h=node.clientHeight;
_44c=_449.pE;
}
return {w:(w-_44c.w),h:(h-_44c.h)};
},getMarginBoxSize:function(node,_44e){
return {w:(node.offsetWidth+_44e.mE.w),h:(node.offsetHeight+_44e.mE.h)};
},getMarginBox:function(node,_450,_451,_452){
var l=node.offsetLeft-_450.mE.l,t=node.offsetTop-_450.mE.t;
if(_451&&_452.UAope){
l-=_451.bE.l;
t-=_451.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_450.mE.w),h:(node.offsetHeight+_450.mE.h)};
},setMarginBox:function(node,_456,_457,_458,_459,_45a,_45b,_45c){
var pb=_45a.pbE,mb=_45a.mE;
if(_458!=null&&_458>=0){
_458=Math.max(_458-pb.w-mb.w,0);
}
if(_459!=null&&_459>=0){
_459=Math.max(_459-pb.h-mb.h,0);
}
_45c._setBox(node,_456,_457,_458,_459);
},evtConnect:function(_45f,_460,_461,_462,_463,_464,rate){
if(!rate){
rate=0;
}
var _466={adviceType:_45f,srcObj:_460,srcFunc:_461,adviceObj:_462,adviceFunc:_463,rate:rate};
if(_464==null){
_464=dojo.event;
}
_464.connect(_466);
return _466;
},evtDisconnect:function(_467,_468,_469,_46a,_46b,_46c){
if(_46c==null){
_46c=dojo.event;
}
_46c.disconnect({adviceType:_467,srcObj:_468,srcFunc:_469,adviceObj:_46a,adviceFunc:_46b});
},evtDisconnectWObj:function(_46d,_46e){
if(_46e==null){
_46e=dojo.event;
}
_46e.disconnect(_46d);
},evtDisconnectWObjAry:function(_46f,_470){
if(_46f&&_46f.length>0){
if(_470==null){
_470=dojo.event;
}
for(var i=0;i<_46f.length;i++){
_470.disconnect(_46f[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _472=false;
var _473=this._popupMenuWidgets;
for(var i=0;i<_473.length;i++){
var _475=_473[i];
if(_475&&_475.isShowingNow){
_472=true;
break;
}
}
return _472;
},addPopupMenuWidget:function(_476){
if(_476){
this._popupMenuWidgets.push(_476);
}
},removePopupMenuWidget:function(_477){
if(!_477){
return;
}
var _478=this._popupMenuWidgets;
for(var i=0;i<_478.length;i++){
if(_478[i]===_477){
_478[i]=null;
}
}
},updateChildColInfo:function(_47a,_47b,_47c,_47d,_47e,_47f){
var _480=jetspeed;
var _481=dojo;
var _482=_481.byId(_480.id.COLUMNS);
if(!_482){
return;
}
var _483=false;
if(_47a!=null){
var _484=_47a.getAttribute("columnindex");
var _485=_47a.getAttribute("layoutid");
var _486=(_484==null?-1:new Number(_484));
if(_486>=0&&_485!=null&&_485.length>0){
_483=true;
}
}
var _487=_480.page.columns||[];
var _488=new Array(_487.length);
var _489=_480.page.layoutInfo;
var fnc=_480.ui._updateChildColInfo;
fnc(fnc,_482,1,_488,_487,_47b,_47c,_47d,_489,_489.columns,_489.desktop,_47a,_483,_47e,_47f,_481,_480);
return _488;
},_updateChildColInfo:function(fnc,_48c,_48d,_48e,_48f,_490,_491,_492,_493,_494,_495,_496,_497,_498,_499,_49a,_49b){
var _49c=_48c.childNodes;
var _49d=(_49c?_49c.length:0);
if(_49d==0){
return;
}
var _49e=_49a.html.getAbsolutePosition(_48c,true);
var _49f=_49b.ui.getMarginBox(_48c,_494,_495,_49b);
var _4a0=_493.column;
var _4a1,col,_4a3,_4a4,_4a5,_4a6,_4a7,_4a8,_4a9,_4aa,_4ab,_4ac,_4ad,_4ae;
var _4af=null,_4b0=(_498!=null?(_498+1):null),_4b1,_4b2;
var _4b3=null;
for(var i=0;i<_49d;i++){
_4a1=_49c[i];
_4a4=_4a1.getAttribute("columnindex");
_4a5=(_4a4==null?-1:new Number(_4a4));
if(_4a5>=0){
col=_48f[_4a5];
_4ae=true;
_4a3=(col?col.layoutActionsDisabled:false);
_4a6=_4a1.getAttribute("layoutid");
_4a7=(_4a6!=null&&_4a6.length>0);
_4b1=_4b0;
_4b2=null;
_4a3=((!_492)&&_4a3);
var _4b5=_48d;
var _4b6=(_4a1===_496);
if(_4a7){
if(_4b3==null){
_4b3=_48d;
}
if(col){
col._updateLayoutDepth(_48d);
}
_4b5++;
}else{
if(!_4b6){
if(col&&(!_4a3||_492)&&(_490==null||_490[_4a5]==null)&&(_491==null||_48d<=_491)){
_4a8=_49b.ui.getMarginBox(_4a1,_4a0,_494,_49b);
if(_4af==null){
_4af=_4a8.t-_49f.t;
_4ad=_49f.h-_4af;
}
_4a9=_49e.left+(_4a8.l-_49f.l);
_4aa=_49e.top+_4af;
_4ab=_4a8.h;
if(_4ab<_4ad){
_4ab=_4ad;
}
if(_4ab<40){
_4ab=40;
}
var _4b7=_4a1.childNodes;
_4ac={left:_4a9,top:_4aa,right:(_4a9+_4a8.w),bottom:(_4aa+_4ab),childCount:(_4b7?_4b7.length:0),pageColIndex:_4a5};
_4ac.height=_4ac.bottom-_4ac.top;
_4ac.width=_4ac.right-_4ac.left;
_4ac.yhalf=_4ac.top+(_4ac.height/2);
_48e[_4a5]=_4ac;
_4ae=(_4ac.childCount>0);
if(_4ae){
_4a1.style.height="";
}else{
_4a1.style.height="1px";
}
if(_498!=null){
_4b2=(_49b.debugDims(_4ac,true)+" yhalf="+_4ac.yhalf+(_4a8.h!=_4ab?(" hreal="+_4a8.h):"")+" childC="+_4ac.childCount+"}");
}
}
}
}
if(_498!=null){
if(_4a7){
_4b1=_4b0+1;
}
if(_4b2==null){
_4b2="---";
}
_49a.hostenv.println(_49a.string.repeat(_499,_498)+"["+((_4a5<10?" ":"")+_4a4)+"] "+_4b2);
}
if(_4ae){
var _4b8=fnc(fnc,_4a1,_4b5,_48e,_48f,_490,_491,_492,_493,(_4a7?_493.columnLayoutHeader:_4a0),_494,_496,_497,_4b1,_499,_49a,_49b);
if(_4b8!=null&&(_4b3==null||_4b8>_4b3)){
_4b3=_4b8;
}
}
}
}
_4a4=_48c.getAttribute("columnindex");
_4a6=_48c.getAttribute("layoutid");
_4a5=(_4a4==null?-1:new Number(_4a4));
if(_4a5>=0&&_4a6!=null&&_4a6.length>0){
col=_48f[_4a5];
col._updateLayoutChildDepth(_4b3);
}
return _4b3;
},getScrollbar:function(_4b9){
var _4ba=_4b9.ui.scrollWidth;
if(_4ba==null){
var _4bb=document.createElement("div");
var _4bc="width: 100px; height: 100px; top: -300px; left: 0px; overflow: scroll; position: absolute";
_4bb.style.cssText=_4bc;
var test=document.createElement("div");
_4bb.style.cssText="width: 400px; height: 400px";
_4bb.appendChild(test);
var _4be=_4b9.docBody;
_4be.appendChild(_4bb);
_4ba=_4bb.offsetWidth-_4bb.clientWidth;
_4be.removeChild(_4bb);
_4bb.removeChild(test);
_4bb=test=null;
_4b9.ui.scrollWidth=_4ba;
}
return _4ba;
}};
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_4c0){
this.oldXY=this.getWinDims(win,win.document,_4c0);
},getWinDims:function(win,doc,_4c3){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_4c3.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_4c3;
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
var _4ca=jetspeed;
var _4cb=this.getWinDims(window,window.document,_4ca.docBody);
this.timerId=0;
if((_4cb.x!=this.oldXY.x)||(_4cb.y!=this.oldXY.y)){
this.oldXY=_4cb;
if(_4ca.page){
if(!this.resizing){
try{
this.resizing=true;
_4ca.page.onBrowserWindowResize();
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
var _4cc=jetspeed;
var _4cd=null;
var _4ce=false;
var ua=function(){
var _4d0=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_4d0[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_4d0[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_4d0[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4d3=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_4d0=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_4d0[0]==6){
_4d3=true;
}
}
if(!_4d3){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4d3&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_4d0=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_4d0,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
function showExpressInstall(_4da){
_4ce=true;
var obj=document.getElementById(_4da.id);
if(obj){
var ac=document.getElementById(_4da.altContentId);
if(ac){
_4cd=ac;
}
var w=_4da.width?_4da.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4da.height?_4da.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4da.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
};
function createSWF(_4e3,_4e4,el){
_4e4.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4e3){
if(typeof _4e3[i]=="string"){
if(i=="data"){
_4e4.movie=_4e3[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4e3[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4e3[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4e4){
if(typeof _4e4[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4e4[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4e3){
if(typeof _4e3[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4e3[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4e3[m]);
}
}
}
}
for(var n in _4e4){
if(typeof _4e4[n]=="string"&&n!="movie"){
createObjParam(o,n,_4e4[n]);
}
}
el.parentNode.replaceChild(o,el);
}
};
function createObjParam(el,_4ee,_4ef){
var p=document.createElement("param");
p.setAttribute("name",_4ee);
p.setAttribute("value",_4ef);
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
return {embedSWF:function(_4f7,_4f8,_4f9,_4fa,_4fb,_4fc,_4fd,_4fe,_4ff,_500){
if(!ua.w3cdom||!_4f7||!_4f8||!_4f9||!_4fa||!_4fb){
return;
}
if(hasPlayerVersion(_4fb.split("."))){
var _501=(_4ff?_4ff.id:null);
createCSS("#"+_4f8,"visibility:hidden");
var att=(typeof _4ff=="object")?_4ff:{};
att.data=_4f7;
att.width=_4f9;
att.height=_4fa;
var par=(typeof _4fe=="object")?_4fe:{};
if(typeof _4fd=="object"){
for(var i in _4fd){
if(typeof _4fd[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4fd[i];
}else{
par.flashvars=i+"="+_4fd[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4f8));
createCSS("#"+_4f8,"visibility:visible");
if(_501){
var _505=_4cc.page.swfInfo;
if(_505==null){
_505=_4cc.page.swfInfo={};
}
_505[_501]=_500;
}
}else{
if(_4fc&&!_4ce&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4f8,"visibility:hidden");
var _506={};
_506.id=_506.altContentId=_4f8;
_506.width=_4f9;
_506.height=_4fa;
_506.expressInstall=_4fc;
showExpressInstall(_506);
createCSS("#"+_4f8,"visibility:visible");
}
}
}};
}();

