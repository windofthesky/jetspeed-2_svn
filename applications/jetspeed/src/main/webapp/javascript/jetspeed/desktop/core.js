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
var _3d=_3b.getHead().childNodes;
if(_3d){
for(var i=0;i<_3d.length;i++){
if(_3d[i].nodeType==dojo.dom.ELEMENT_NODE){
_3c.push(_3d[i]);
}
}
}
var _3f=_3a.childNodes;
var _40=0;
for(var i=0;i<_3f.length;i++){
var _41=_3f.item(i);
if(!_41||_41.nodeType!=dojo.dom.ELEMENT_NODE){
continue;
}
var id=_41.getAttribute("id");
if(!id){
id=_41.getAttribute("ID");
}
if(!id){
id=_41.getAttribute("Id");
}
if(!id){
id=_41.getAttribute("iD");
}
var _43=_41.tagName;
var _44=false;
if(id){
for(var j=0;j<_3c.length;j++){
if(id==_3c[j].id){
_44=true;
_40=j+1;
break;
}
}
}
if(!_44){
var _46=jetspeed.createHeadElement(_41);
if(_3b.UAie){
if(_43=="SCRIPT"&&_41.text){
_46.text=_41.value;
}else{
if(_43=="STYLE"&&_41.text){
_46.styleSheet.cssText=_41.text;
}
}
}else{
if(_41.textContent){
_46.appendChild(document.createTextNode(_41.textContent));
}
}
if(_3c[_40]){
_3b.getHead().insertBefore(_46,_3c[_40]);
}else{
_3b.getHead().appendChild(scriptElem);
}
++_40;
}
}
};
jetspeed.doRender=function(_47,_48){
if(!_47){
_47={};
}else{
if((typeof _47=="string"||_47 instanceof String)){
_47={url:_47};
}
}
var _49=jetspeed.page.getPortlet(_48);
if(_49){
if(jetspeed.debug.doRenderDoAction){
dojo.debug("doRender ["+_48+"] url: "+_47.url);
}
_49.retrieveContent(null,_47);
}
};
jetspeed.doAction=function(_4a,_4b){
if(!_4a){
_4a={};
}else{
if((typeof _4a=="string"||_4a instanceof String)){
_4a={url:_4a};
}
}
var _4c=jetspeed.page.getPortlet(_4b);
if(_4c){
if(jetspeed.debug.doRenderDoAction){
if(!_4a.formNode){
dojo.debug("doAction ["+_4b+"] url: "+_4a.url+" form: null");
}else{
dojo.debug("doAction ["+_4b+"] url: "+_4a.url+" form: "+jetspeed.debugDumpForm(_4a.formNode));
}
}
_4c.retrieveContent(new jetspeed.om.PortletActionCL(_4c,_4a),_4a);
}
};
jetspeed.PortletRenderer=function(_4d,_4e,_4f,_50,_51,_52){
var _53=jetspeed;
var _54=_53.page;
var _55=dojo;
this._jsObj=_53;
this.mkWins=_4d;
this.initEdit=_52;
this.minimizeTemp=(_52!=null&&_52.editModeMove);
this.noRender=(this.minimizeTemp&&_52.windowTitles!=null);
this.isPgLd=_4e;
this.isPgUp=_4f;
this.renderUrl=_50;
this.suppressGetActions=_51;
this._colLen=_54.columns.length;
this._colIndex=0;
this._portletIndex=0;
this._renderCount=0;
this.psByCol=_54.portletsByPageColumn;
this.pageLoadUrl=null;
if(_4e){
this.pageLoadUrl=_53.url.parse(_54.getPageUrl());
_53.ui.evtConnect("before",_55,"addOnLoad",_54,"_beforeAddOnLoad",_55.event);
}
this.dbgPgLd=_53.debug.pageLoad&&_4e;
this.dbgMsg=null;
if(_53.debug.doRenderDoAction||this.dbgPgLd){
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
var _56=this._jsObj;
var _57=this.dbgMsg;
if(_57!=null){
if(this.dbgPgLd){
dojo.debug("portlet-renderer page-url: "+_56.page.getPsmlUrl()+" portlets: ["+renderMsg+"]"+(url?(" url: "+url):""));
}else{
dojo.debug("portlet-renderer ["+renderMsg+"] url: "+url);
}
}
if(this.isPgLd){
_56.page.loadPostRender(this.isPgUp,this.initEdit);
}
},_renderCurrent:function(){
var _58=this._jsObj;
var _59=this._colLen;
var _5a=this._colIndex;
var _5b=this._portletIndex;
if(_5a<=_59){
var _5c;
if(_5a<_59){
_5c=this.psByCol[_5a.toString()];
}else{
_5c=this.psByCol["z"];
_5a=null;
}
var _5d=(_5c!=null?_5c.length:0);
if(_5d>0){
var _5e=_5c[_5b];
if(_5e){
var _5f=_5e.portlet;
var _60=null;
if(this.mkWins){
_60=_58.ui.createPortletWindow(_5f,_5a,_58);
if(this.minimizeTemp){
_60.minimizeWindowTemporarily(this.noRender);
}
}
var _61=this.dbgMsg;
if(_61!=null){
if(_61.length>0){
_61=_61+", ";
}
var _62=null;
if(_5f.getProperty!=null){
_62=_5f.getProperty(_58.id.PP_WIDGET_ID);
}
if(!_62){
_62=_5f.widgetId;
}
if(!_62){
_62=_5f.toString();
}
if(_5f.entityId){
_61=_61+_5f.entityId+"("+_62+")";
if(this._dbPgLd&&_5f.getProperty(_58.id.PP_WINDOW_TITLE)){
_61=_61+" "+_5f.getProperty(_58.id.PP_WINDOW_TITLE);
}
}else{
_61=_61+_62;
}
}
if(!this.noRender){
_5f.retrieveContent(null,{url:this.renderUrl,jsPageUrl:this.pageLoadUrl},this.suppressGetActions);
}else{
if(_60&&_60.portlet){
var _63=this.initEdit.windowTitles[_60.portlet.entityId];
if(_63!=null){
_60.setPortletTitle(_63);
}
}
}
if((this._renderCount%3)==0){
_58.url.loadingIndicatorStep(_58);
}
this._renderCount++;
}
}
}
},_evalNext:function(){
var _64=false;
var _65=this._colLen;
var _66=this._colIndex;
var _67=this._portletIndex;
var _68=_66;
var _69;
for(++_66;_66<=_65;_66++){
_69=this.psByCol[_66==_65?"z":_66.toString()];
if(_67<(_69!=null?_69.length:0)){
_64=true;
this._colIndex=_66;
break;
}
}
if(!_64){
++_67;
for(_66=0;_66<=_68;_66++){
_69=this.psByCol[_66==_65?"z":_66.toString()];
if(_67<(_69!=null?_69.length:0)){
_64=true;
this._colIndex=_66;
this._portletIndex=_67;
break;
}
}
}
return _64;
}});
jetspeed.portleturl={DESKTOP_ACTION_PREFIX_URL:null,DESKTOP_RENDER_PREFIX_URL:null,JAVASCRIPT_ARG_QUOTE:"&"+"quot;",PORTLET_REQUEST_ACTION:"action",PORTLET_REQUEST_RENDER:"render",JETSPEED_DO_NOTHING_ACTION:"javascript:jetspeed.doNothingNav()",parseContentUrl:function(_6a){
if(this.DESKTOP_ACTION_PREFIX_URL==null){
this.DESKTOP_ACTION_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.ACTION;
}
if(this.DESKTOP_RENDER_PREFIX_URL==null){
this.DESKTOP_RENDER_PREFIX_URL=jetspeed.url.basePortalUrl()+jetspeed.url.path.RENDER;
}
var op=null;
var _6c=_6a;
var _6d=null;
if(_6a&&_6a.length>this.DESKTOP_ACTION_PREFIX_URL.length&&_6a.indexOf(this.DESKTOP_ACTION_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_ACTION;
}else{
if(_6a&&_6a.length>this.DESKTOP_RENDER_PREFIX_URL.length&&_6a.indexOf(this.DESKTOP_RENDER_PREFIX_URL)==0){
op=jetspeed.portleturl.PORTLET_REQUEST_RENDER;
}
}
if(op!=null){
_6d=jetspeed.url.getQueryParameter(_6a,"entity");
}
if(!jetspeed.url.urlStartsWithHttp(_6c)){
_6c=null;
}
return {url:_6c,operation:op,portletEntityId:_6d};
},genPseudoUrl:function(_6e,_6f){
if(!_6e||!_6e.url||!_6e.portletEntityId){
return null;
}
var _70=null;
if(_6f){
_70=jetspeed.portleturl.JETSPEED_DO_NOTHING_ACTION;
}else{
_70="javascript:";
var _71=false;
if(_6e.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
_70+="doAction(\"";
}else{
if(_6e.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
_70+="doRender(\"";
}else{
_71=true;
}
}
if(_71){
return null;
}
_70+=_6e.url+"\",\""+_6e.portletEntityId+"\"";
_70+=")";
}
return _70;
}};
jetspeed.doNothingNav=function(){
false;
};
jetspeed.loadPortletDecorationStyles=function(_72,_73,_74){
var _75=null;
var _76=_73.portletDecorationsConfig;
if(_72&&_76){
_75=_76[_72];
}
if(_75==null&&!_74){
var _77=_73.portletDecorationsAllowed;
for(var i=0;i<_77.length;i++){
_72=_77[i];
_75=_76[_72];
if(_75!=null){
break;
}
}
}
if(_75!=null&&!_75._initialized){
var _79=jetspeed.prefs.getPortletDecorationBaseUrl(_72);
_75._initialized=true;
_75.cssPathCommon=new dojo.uri.Uri(_79+"/css/styles.css");
_75.cssPathDesktop=new dojo.uri.Uri(_79+"/css/desktop.css");
dojo.html.insertCssFile(_75.cssPathCommon,null,true);
dojo.html.insertCssFile(_75.cssPathDesktop,null,true);
}
return _75;
};
jetspeed.loadPortletDecorationConfig=function(_7a,_7b,_7c){
var _7d={};
_7b.portletDecorationsConfig[_7a]=_7d;
_7d.name=_7a;
_7d.windowActionButtonOrder=_7b.windowActionButtonOrder;
_7d.windowActionNotPortlet=_7b.windowActionNotPortlet;
_7d.windowActionButtonMax=_7b.windowActionButtonMax;
_7d.windowActionButtonTooltip=_7b.windowActionButtonTooltip;
_7d.windowActionMenuOrder=_7b.windowActionMenuOrder;
_7d.windowActionNoImage=_7b.windowActionNoImage;
_7d.windowIconEnabled=_7b.windowIconEnabled;
_7d.windowIconPath=_7b.windowIconPath;
_7d.windowTitlebar=_7b.windowTitlebar;
_7d.windowResizebar=_7b.windowResizebar;
_7d.dNodeClass=_7c.P_CLASS+" "+_7a+" "+_7c.PWIN_CLASS+" "+_7c.PWIN_CLASS+"-"+_7a;
_7d.cNodeClass=_7c.P_CLASS+" "+_7a+" "+_7c.PWIN_CLIENT_CLASS;
if(_7b.portletDecorationsProperties){
var _7e=_7b.portletDecorationsProperties[_7a];
if(_7e){
for(var _7f in _7e){
_7d[_7f]=_7e[_7f];
}
if(_7e.windowActionNoImage!=null){
var _80={};
for(var i=0;i<_7e.windowActionNoImage.length;i++){
_80[_7e.windowActionNoImage[i]]=true;
}
_7d.windowActionNoImage=_80;
}
if(_7e.windowIconPath!=null){
_7d.windowIconPath=dojo.string.trim(_7e.windowIconPath);
if(_7d.windowIconPath==null||_7d.windowIconPath.length==0){
_7d.windowIconPath=null;
}else{
var _82=_7d.windowIconPath;
var _83=_82.charAt(0);
if(_83!="/"){
_82="/"+_82;
}
var _84=_82.charAt(_82.length-1);
if(_84!="/"){
_82=_82+"/";
}
_7d.windowIconPath=_82;
}
}
}
}
};
jetspeed.notifyRetrieveAllMenusFinished=function(_85,_86){
var _87=jetspeed;
_87.pageNavigateSuppress=true;
if(dojo.lang.isFunction(window.doMenuBuildAll)){
window.doMenuBuildAll();
}
var _88=_87.page.getMenuNames();
for(var i=0;i<_88.length;i++){
var _8a=_88[i];
var _8b=dojo.widget.byId(_87.id.MENU_WIDGET_ID_PREFIX+_8a);
if(_8b){
_8b.createJetspeedMenu(_87.page.getMenu(_8a));
}
}
if(!_86){
_87.url.loadingIndicatorHide();
}
_87.pageNavigateSuppress=false;
};
jetspeed.notifyRetrieveMenuFinished=function(_8c){
if(dojo.lang.isFunction(window.doMenuBuild)){
window.doMenuBuild(_8c);
}
};
jetspeed.menuNavClickWidget=function(_8d,_8e){
if(!_8d){
return;
}
if(dojo.lang.isString(_8d)){
var _8f=_8d;
_8d=dojo.widget.byId(_8f);
if(!_8d){
dojo.raise("Tab widget not found: "+_8f);
}
}
if(_8d){
var _90=_8d.jetspeedmenuname;
if(!_90&&_8d.extraArgs){
_90=_8d.extraArgs.jetspeedmenuname;
}
if(!_90){
dojo.raise("Tab widget is invalid: "+_8d.widgetId);
}
var _91=jetspeed.page.getMenu(_90);
if(!_91){
dojo.raise("Tab widget "+_8d.widgetId+" no menu: "+_90);
}
var _92=_91.getOptionByIndex(_8e);
jetspeed.menuNavClick(_92);
}
};
jetspeed.pageNavigateSuppress=false;
jetspeed.pageNavigate=function(_93,_94,_95){
var _96=jetspeed;
if(!_93||_96.pageNavigateSuppress){
return;
}
if(typeof _95=="undefined"){
_95=false;
}
if(!_95&&_96.page&&_96.page.equalsPageUrl(_93)){
return;
}
_93=_96.page.makePageUrl(_93);
if(_94=="top"){
top.location.href=_93;
}else{
if(_94=="parent"){
parent.location.href=_93;
}else{
window.location.href=_93;
}
}
};
jetspeed.getActionsForPortlet=function(_97){
if(_97==null){
return;
}
jetspeed.getActionsForPortlets([_97]);
};
jetspeed.getActionsForPortlets=function(_98){
var _99=jetspeed;
if(_98==null){
_98=_99.page.getPortletIds();
}
var _9a=new _99.om.PortletActionsCL(_98);
var _9b="?action=getactions";
for(var i=0;i<_98.length;i++){
_9b+="&id="+_98[i];
}
var _9d=_99.url.basePortalUrl()+_99.url.path.AJAX_API+_99.page.getPath()+_9b;
var _9e="text/xml";
var _9f=new _99.om.Id("getactions",{});
_99.url.retrieveContent({url:_9d,mimetype:_9e},_9a,_9f,_99.debugContentDumpIds);
};
jetspeed.changeActionForPortlet=function(_a0,_a1,_a2,_a3,_a4){
var _a5=jetspeed;
if(_a0==null){
return;
}
if(_a3==null){
_a3=new _a5.om.PortletChangeActionCL(_a0);
}
var _a6="?action=window&id="+(_a0!=null?_a0:"");
if(_a1!=null){
_a6+="&state="+_a1;
}
if(_a2!=null){
_a6+="&mode="+_a2;
}
var _a7=_a4;
if(!_a7){
_a7=_a5.page.getPath();
}
var _a8=_a5.url.basePortalUrl()+_a5.url.path.AJAX_API+_a7+_a6;
var _a9="text/xml";
var _aa=new _a5.om.Id("changeaction",{});
_a5.url.retrieveContent({url:_a8,mimetype:_a9},_a3,_aa,_a5.debugContentDumpIds);
};
jetspeed.getUserInfo=function(_ab){
var _ac=jetspeed;
var _ad=new _ac.om.UserInfoCL();
var _ae="?action=getuserinfo";
var _af=_ac.url.basePortalUrl()+_ac.url.path.AJAX_API+_ac.page.getPath()+_ae;
var _b0="text/xml";
var _b1=new _ac.om.Id("getuserinfo",{});
_ac.url.retrieveContent({url:_af,mimetype:_b0,sync:_ab},_ad,_b1,_ac.debugContentDumpIds);
};
jetspeed.editPageInitiate=function(_b2,_b3){
var _b4=_b2.page;
if(!_b4.editMode){
var _b5=_b2.css;
var _b6=true;
var _b7=_b2.url.getQueryParameter(window.location.href,_b2.id.PORTAL_ORIGINATE_PARAMETER);
if(_b7!=null&&_b7=="true"){
_b6=false;
}
_b4.editMode=true;
var _b8=dojo.widget.byId(_b2.id.PG_ED_WID);
if(_b2.UAie6){
_b4.displayAllPWins(true);
}
var _b9=((_b3!=null&&_b3.editModeMove)?true:false);
var _ba=_b4._perms(_b2.prefs,-1,String.fromCharCode);
if(_ba&&_ba[2]&&_ba[2].length>0){
if(!_b2.page._getU()){
_b2.getUserInfo(true);
}
}
if(_b8==null){
try{
_b2.url.loadingIndicatorShow("loadpageeditor",true);
_b8=dojo.widget.createWidget("jetspeed:PageEditor",{widgetId:_b2.id.PG_ED_WID,editorInitiatedFromDesktop:_b6,editModeMove:_b9});
var _bb=document.getElementById(_b2.id.COLUMNS);
_bb.insertBefore(_b8.domNode,_bb.firstChild);
}
catch(e){
_b2.url.loadingIndicatorHide();
if(_b2.UAie6){
_b4.displayAllPWins();
}
}
}else{
_b8.editPageShow();
}
_b4.syncPageControls(_b2);
}
};
jetspeed.editPageTerminate=function(_bc,_bd){
var _be=_bc.page;
if(_be.editMode){
var _bf=null;
var _c0=_bc.css;
var _c1=dojo.widget.byId(_bc.id.PG_ED_WID);
if(_c1!=null&&!_c1.editorInitiatedFromDesktop){
var _c2=_be.getPageUrl(true);
_c2=_bc.url.removeQueryParameter(_c2,_bc.id.PG_ED_PARAM);
_c2=_bc.url.removeQueryParameter(_c2,_bc.id.PORTAL_ORIGINATE_PARAMETER);
_bf=_c2;
}else{
var _c3=_bc.url.getQueryParameter(window.location.href,_bc.id.PG_ED_PARAM);
if(_c3!=null&&_c3=="true"){
var _c4=window.location.href;
_c4=_bc.url.removeQueryParameter(_c4,_bc.id.PG_ED_PARAM);
_bf=_c4;
}
}
if(_bf!=null){
_bf=_bf.toString();
}
_be.editMode=false;
_bc.changeActionForPortlet(_be.rootFragmentId,null,_bc.id.ACT_VIEW,new _bc.om.PageChangeActionCL(_bf));
if(_bf==null){
if(_c1!=null){
_c1.editMoveModeExit(true);
_c1.editPageHide();
}
_be.syncPageControls(_bc);
}
}
};
jetspeed.om.PortletContentRetriever=function(){
};
jetspeed.om.PortletContentRetriever.prototype={getContent:function(_c5,_c6,_c7,_c8){
if(!_c5){
_c5={};
}
jetspeed.url.retrieveContent(_c5,_c6,_c7,_c8);
}};
jetspeed.om.PageCLCreateWidget=function(_c9,_ca){
if(typeof _c9=="undefined"){
_c9=false;
}
this.isPageUpdate=_c9;
this.initEditModeConf=_ca;
};
jetspeed.om.PageCLCreateWidget.prototype={notifySuccess:function(_cb,_cc,_cd){
_cd.loadFromPSML(_cb,this.isPageUpdate,this.initEditModeConf);
},notifyFailure:function(_ce,_cf,_d0,_d1){
dojo.raise("PageCLCreateWidget error url: "+_d0+" type: "+_ce+jetspeed.formatError(_cf));
}};
jetspeed.om.Page=function(_d2,_d3,_d4,_d5,_d6){
if(_d2!=null&&_d3!=null){
this.requiredLayoutDecorator=_d2;
this.setPsmlPathFromDocumentUrl(_d3);
this.pageUrlFallback=_d3;
}else{
this.setPsmlPathFromDocumentUrl();
}
if(typeof _d4!="undefined"){
this.addToHistory=_d4;
}
this.layouts={};
this.columns=[];
this.colFirstNormI=-1;
this.portlets={};
this.portlet_count=0;
this.portlet_windows={};
this.portlet_window_count=0;
if(_d6!=null){
this.iframeCoverByWinId=_d6;
}else{
this.iframeCoverByWinId={};
}
this.portlet_tiled_high_z=10;
this.portlet_untiled_high_z=-1;
this.menus=[];
if(_d5!=null){
this.tooltipMgr=_d5;
}else{
this.tooltipMgr=dojo.widget.createWidget("jetspeed:PortalTooltipManager",{isContainer:false,fastMixIn:true});
jetspeed.docBody.appendChild(this.tooltipMgr.domNode);
}
};
dojo.lang.extend(jetspeed.om.Page,{psmlPath:null,name:null,path:null,pageUrl:null,pagePathAndQuery:null,title:null,shortTitle:null,layoutDecorator:null,portletDecorator:null,uIA:true,requiredLayoutDecorator:null,pageUrlFallback:null,addToHistory:false,layouts:null,columns:null,portlets:null,portletsByPageColumn:null,editMode:false,themeDefinitions:null,menus:null,getId:function(){
var _d7=(this.name!=null&&this.name.length>0?this.name:null);
if(!_d7){
this.getPsmlUrl();
_d7=this.psmlPath;
}
return "page-"+_d7;
},setPsmlPathFromDocumentUrl:function(_d8){
var _d9=jetspeed;
var _da=_d9.url.path.AJAX_API;
var _db=null;
if(_d8==null){
_db=window.location.pathname;
if(!djConfig.preventBackButtonFix&&_d9.prefs.ajaxPageNavigation){
var _dc=window.location.hash;
if(_dc!=null&&_dc.length>0){
if(_dc.indexOf("#")==0){
_dc=(_dc.length>1?_dc.substring(1):"");
}
if(_dc!=null&&_dc.length>1&&_dc.indexOf("/")==0){
this.psmlPath=_d9.url.path.AJAX_API+_dc;
return;
}
}
}
}else{
var _dd=_d9.url.parse(_d8);
_db=_dd.path;
}
var _de=_d9.url.path.DESKTOP;
var _df=_db.indexOf(_de);
if(_df!=-1&&_db.length>(_df+_de.length)){
_da=_da+_db.substring(_df+_de.length);
}
this.psmlPath=_da;
},getPsmlUrl:function(){
var _e0=jetspeed;
if(this.psmlPath==null){
this.setPsmlPathFromDocumentUrl();
}
var _e1=_e0.url.basePortalUrl()+this.psmlPath;
if(_e0.prefs.printModeOnly!=null){
_e1=_e0.url.addQueryParameter(_e1,"layoutid",_e0.prefs.printModeOnly.layout);
_e1=_e0.url.addQueryParameter(_e1,"entity",_e0.prefs.printModeOnly.entity).toString();
}
return _e1;
},_setU:function(u){
this._u=u;
},_getU:function(){
return this._u;
},retrievePsml:function(_e3){
var _e4=jetspeed;
if(_e3==null){
_e3=new _e4.om.PageCLCreateWidget();
}
var _e5=this.getPsmlUrl();
var _e6="text/xml";
if(_e4.debug.retrievePsml){
dojo.debug("retrievePsml url: "+_e5);
}
_e4.url.retrieveContent({url:_e5,mimetype:_e6},_e3,this,_e4.debugContentDumpIds);
},loadFromPSML:function(_e7,_e8,_e9){
var _ea=jetspeed;
var _eb=_ea.prefs;
var _ec=dojo;
var _ed=_eb.printModeOnly;
if(djConfig.isDebug&&_ea.debug.profile&&_ed==null){
_ec.profile.start("loadFromPSML");
}
var _ee=this._parsePSML(_e7);
jetspeed.rootfrag=_ee;
if(_ee==null){
return;
}
this.portletsByPageColumn={};
var _ef={};
if(this.portletDecorator){
_ef[this.portletDecorator]=true;
}
this.columnsStructure=this._layoutCreateModel(_ee,0,null,this.portletsByPageColumn,true,_ef,_ec,_ea);
this.rootFragmentId=_ee.id;
this.editMode=false;
for(var _f0 in _ef){
_ea.loadPortletDecorationStyles(_f0,_eb,true);
}
if(_eb.windowTiling){
this._createColsStart(document.getElementById(_ea.id.DESKTOP),_ea.id.COLUMNS);
}
this.createLayoutInfo(_ea);
var _f1=this.portletsByPageColumn["z"];
if(_f1){
_f1.sort(this._loadPortletZIndexCompare);
}
if(typeof _e9=="undefined"){
_e9=null;
}
if(_e9!=null||(this.actions!=null&&this.actions[_ea.id.ACT_VIEW]!=null)){
if(!this.isUA()&&this.actions!=null&&(this.actions[_ea.id.ACT_EDIT]!=null||this.actions[_ea.id.ACT_VIEW]!=null)){
if(_e9==null){
_e9={};
}
if((typeof _e9.editModeMove=="undefined")&&this._perms(_eb,_ea.id.PM_MZ_P,String.fromCharCode)){
_e9.editModeMove=true;
}
var _f2=_ea.url.parse(window.location.href);
if(!_e9.editModeMove){
var _f3=_ea.url.getQueryParameter(_f2,_ea.id.PG_ED_STATE_PARAM);
if(_f3!=null){
_f3="0x"+_f3;
if((_f3&_ea.id.PM_MZ_P)>0){
_e9.editModeMove=true;
}
}
}
if(_e9.editModeMove&&!_e9.windowTitles){
var _f4=_ea.url.getQueryParameter(_f2,_ea.id.PG_ED_TITLES_PARAM);
if(_f4!=null){
var _f5=_f4.length;
var _f6=new Array(_f5/2);
var _f7=String.fromCharCode;
var _f8=0,chI=0;
while(chI<(_f5-1)){
_f6[_f8]=_f7(Number("0x"+_f4.substring(chI,(chI+2))));
_f8++;
chI+=2;
}
var _fa=null;
try{
_fa=eval("({"+_f6.join("")+"})");
}
catch(e){
if(djConfig.isDebug){
dojo.debug("cannot parse json: "+_f6.join(""));
}
}
if(_fa!=null){
var _fb=false;
for(var _fc in this.portlets){
var _fd=this.portlets[_fc];
if(_fd!=null&&!_fa[_fd.entityId]){
_fb=true;
break;
}
}
if(!_fb){
_e9.windowTitles=_fa;
}
}
}
}
}else{
_e9=null;
}
}
if(_e9!=null){
_ea.url.loadingIndicatorShow("loadpageeditor",true);
}
var _fe=new _ea.PortletRenderer(true,true,_e8,null,true,_e9);
_fe.renderAllTimeDistribute();
},loadPostRender:function(_ff,_100){
var _101=jetspeed;
var _102=_101.prefs.printModeOnly;
if(_102==null){
this._portletsInitWinState(this.portletsByPageColumn["z"]);
this.retrieveMenuDeclarations(true,_ff,_100);
}else{
for(var _103 in this.portlets){
var _104=this.portlets[_103];
if(_104!=null){
_104.renderAction(null,_102.action);
}
break;
}
if(_ff){
_101.updatePageEnd();
}
}
_101.ui.evtConnect("after",window,"onresize",_101.ui.windowResizeMgr,"onResize",dojo.event);
_101.ui.windowResizeMgr.onResizeDelayedCompare();
var _105,_106=this.columns;
if(_106){
for(var i=0;i<_106.length;i++){
_105=_106[i].domNode;
if(!_105.childNodes||_105.childNodes.length==0){
_105.style.height="1px";
}
}
}
var _108=this.maximizedOnInit;
if(_108!=null){
var _109=this.getPWin(_108);
if(_109==null){
dojo.raise("no pWin to max");
}else{
dojo.lang.setTimeout(_109,_109._postCreateMaximizeWindow,500);
}
this.maximizedOnInit=null;
}
dojo.lang.setTimeout(_101.url,_101.url.loadingIndicatorStepPreload,1800);
},loadPostRetrieveMenus:function(_10a,_10b){
var _10c=jetspeed;
this.renderPageControls(_10c);
if(_10b){
_10c.editPageInitiate(_10c,_10b);
}
if(_10a){
_10c.updatePageEnd();
}
this.syncPageControls(_10c);
},_parsePSML:function(psml){
var _10e=jetspeed;
var _10f=dojo;
var _110=psml.getElementsByTagName("page");
if(!_110||_110.length>1||_110[0]==null){
_10f.raise("<page>");
}
var _111=_110[0];
var _112=_111.childNodes;
var _113=new RegExp("(name|path|profiledPath|title|short-title|uIA|npe)");
var _114=null;
var _115={};
for(var i=0;i<_112.length;i++){
var _117=_112[i];
if(_117.nodeType!=1){
continue;
}
var _118=_117.nodeName;
if(_118=="fragment"){
_114=_117;
}else{
if(_118=="defaults"){
this.layoutDecorator=_117.getAttribute("layout-decorator");
var _119=_117.getAttribute("portlet-decorator");
var _11a=_10e.prefs.portletDecorationsAllowed;
if(!_11a||_10f.lang.indexOf(_11a,_119)==-1){
_119=_10e.prefs.windowDecoration;
}
this.portletDecorator=_119;
}else{
if(_118&&_118.match(_113)){
if(_118=="short-title"){
_118="shortTitle";
}
this[_118]=((_117&&_117.firstChild)?_117.firstChild.nodeValue:null);
}else{
if(_118=="action"){
this._parsePSMLAction(_117,_115);
}
}
}
}
}
this.actions=_115;
if(_114==null){
_10f.raise("root frag");
return null;
}
if(this.requiredLayoutDecorator!=null&&this.pageUrlFallback!=null){
if(this.layoutDecorator!=this.requiredLayoutDecorator){
if(_10e.debug.ajaxPageNav){
_10f.debug("ajaxPageNavigation _parsePSML different layout decorator ("+this.requiredLayoutDecorator+" != "+this.layoutDecorator+") - fallback to normal page navigation - "+this.pageUrlFallback);
}
_10e.pageNavigate(this.pageUrlFallback,null,true);
return null;
}else{
if(this.addToHistory){
var _11b=this.getPageUrl();
_10f.undo.browser.addToHistory({back:function(){
if(_10e.debug.ajaxPageNav){
dojo.debug("back-nav-button: "+_11b);
}
_10e.updatePage(_11b,true);
},forward:function(){
if(_10e.debug.ajaxPageNav){
dojo.debug("forward-nav-button: "+_11b);
}
_10e.updatePage(_11b,true);
},changeUrl:escape(this.getPath())});
}
}
}else{
if(!djConfig.preventBackButtonFix&&_10e.prefs.ajaxPageNavigation){
var _11b=this.getPageUrl();
_10f.undo.browser.setInitialState({back:function(){
if(_10e.debug.ajaxPageNav){
dojo.debug("back-nav-button initial: "+_11b);
}
_10e.updatePage(_11b,true);
},forward:function(){
if(_10e.debug.ajaxPageNav){
dojo.debug("forward-nav-button initial: "+_11b);
}
_10e.updatePage(_11b,true);
},changeUrl:escape(this.getPath())});
}
}
var _11c=this._parsePSMLFrag(_114,0,false);
return _11c;
},_parsePSMLFrag:function(_11d,_11e,_11f){
var _120=jetspeed;
var _121=new Array();
var _122=((_11d!=null)?_11d.getAttribute("type"):null);
if(_122!="layout"){
dojo.raise("!layout frag="+_11d);
return null;
}
if(!_11f){
var _123=_11d.getAttribute("name");
if(_123!=null){
_123=_123.toLowerCase();
if(_123.indexOf("noactions")!=-1){
_11f=true;
}
}
}
var _124=null,_125=0;
var _126={};
var _127=_11d.childNodes;
var _128,_129,_12a,_12b,_12c;
for(var i=0;i<_127.length;i++){
_128=_127[i];
if(_128.nodeType!=1){
continue;
}
_129=_128.nodeName;
if(_129=="fragment"){
_12c=_128.getAttribute("type");
if(_12c=="layout"){
var _12e=this._parsePSMLFrag(_128,i,_11f);
if(_12e!=null){
_121.push(_12e);
}
}else{
var _12f=this._parsePSMLProps(_128,null);
var _130=_12f[_120.id.PP_WINDOW_ICON];
if(_130==null||_130.length==0){
_130=this._parsePSMLChildOrAttr(_128,"icon");
if(_130!=null&&_130.length>0){
_12f[_120.id.PP_WINDOW_ICON]=_130;
}
}
_121.push({id:_128.getAttribute("id"),type:_12c,name:_128.getAttribute("name"),properties:_12f,actions:this._parsePSMLActions(_128,null),currentActionState:this._parsePSMLChildOrAttr(_128,"state"),currentActionMode:this._parsePSMLChildOrAttr(_128,"mode"),decorator:_128.getAttribute("decorator"),layoutActionsDisabled:_11f,documentOrderIndex:i});
}
}else{
if(_129=="property"){
if(this._parsePSMLProp(_128,_126)=="sizes"){
if(_124!=null){
dojo.raise("<sizes>: "+_11d);
return null;
}
if(_120.prefs.printModeOnly!=null){
_124=["100"];
_125=100;
}else{
_12b=_128.getAttribute("value");
if(_12b!=null&&_12b.length>0){
_124=_12b.split(",");
for(var j=0;j<_124.length;j++){
var re=/^[^0-9]*([0-9]+)[^0-9]*$/;
_124[j]=_124[j].replace(re,"$1");
_125+=new Number(_124[j]);
}
}
}
}
}
}
}
if(_124==null){
_124=["100"];
_125=100;
}
var _133=_124.length;
var _134=_121.length;
var pCi=_120.id.PP_COLUMN;
var pRi=_120.id.PP_ROW;
var _137=new Array(_133);
var _138=new Array(_133);
for(var cI=0;cI<_133;cI++){
_137[cI]=[];
_138[cI]={head:-1,tail:-1,high:-1};
}
for(var _13a=0;_13a<_134;_13a++){
var frag=_121[_13a];
var _13c=frag.properties;
var col=_13c[pCi];
var row=_13c[pRi];
var _13f=null;
if(col==null||col>=_133){
_13f=_133-1;
}else{
if(col<0){
_13f=0;
}
}
if(_13f!=null){
col=_13c[pCi]=String(_13f);
}
var ll=_137[col];
var _141=ll.length;
var _142=_138[col];
if(row<0){
row=_13c[pRi]=0;
}else{
if(row==null){
row=_142.high+1;
}
}
var _143={i:_13a,row:row,next:-1};
ll.push(_143);
if(_141==0){
_142.head=_142.tail=0;
_142.high=row;
}else{
if(row>_142.high){
ll[_142.tail].next=_141;
_142.high=row;
_142.tail=_141;
}else{
var _144=_142.head;
var _145=-1;
while(ll[_144].row<row){
_145=_144;
_144=ll[_144].next;
}
if(ll[_144].row==row){
var _146=new Number(row)+1;
ll[_144].row=_146;
if(_142.tail==_144){
_142.high=_146;
}
}
_143.next=_144;
if(_145==-1){
_142.head=_141;
}else{
ll[_145].next=_141;
}
}
}
}
var _147=new Array(_134);
var _148=0;
for(var cI=0;cI<_133;cI++){
var ll=_137[cI];
var _142=_138[cI];
var _149=0;
var _14a=_142.head;
while(_14a!=-1){
var _143=ll[_14a];
var frag=_121[_143.i];
_147[_148]=frag;
frag.properties[pRi]=_149;
_148++;
_149++;
_14a=_143.next;
}
}
return {id:_11d.getAttribute("id"),type:_122,name:_11d.getAttribute("name"),decorator:_11d.getAttribute("decorator"),columnSizes:_124,columnSizesSum:_125,properties:_126,fragments:_147,layoutActionsDisabled:_11f,documentOrderIndex:_11e};
},_parsePSMLActions:function(_14b,_14c){
if(_14c==null){
_14c={};
}
var _14d=_14b.getElementsByTagName("action");
for(var _14e=0;_14e<_14d.length;_14e++){
var _14f=_14d[_14e];
this._parsePSMLAction(_14f,_14c);
}
return _14c;
},_parsePSMLAction:function(_150,_151){
var _152=_150.getAttribute("id");
if(_152!=null){
var _153=_150.getAttribute("type");
var _154=_150.getAttribute("name");
var _155=_150.getAttribute("url");
var _156=_150.getAttribute("alt");
_151[_152.toLowerCase()]={id:_152,type:_153,label:_154,url:_155,alt:_156};
}
},_parsePSMLChildOrAttr:function(_157,_158){
var _159=null;
var _15a=_157.getElementsByTagName(_158);
if(_15a!=null&&_15a.length==1&&_15a[0].firstChild!=null){
_159=_15a[0].firstChild.nodeValue;
}
if(!_159){
_159=_157.getAttribute(_158);
}
if(_159==null||_159.length==0){
_159=null;
}
return _159;
},_parsePSMLProps:function(_15b,_15c){
if(_15c==null){
_15c={};
}
var _15d=_15b.getElementsByTagName("property");
for(var _15e=0;_15e<_15d.length;_15e++){
this._parsePSMLProp(_15d[_15e],_15c);
}
return _15c;
},_parsePSMLProp:function(_15f,_160){
var _161=_15f.getAttribute("name");
var _162=_15f.getAttribute("value");
_160[_161]=_162;
return _161;
},_layoutCreateModel:function(_163,_164,_165,_166,_167,_168,_169,_16a){
var jsId=_16a.id;
var _16c=this.columns.length;
var _16d=this._layoutCreateColsModel(_163,_164,_165,_167);
var _16e=_16d.columnsInLayout;
if(_16d.addedLayoutHeaderColumn){
_16c++;
}
var _16f=(_16e==null?0:_16e.length);
var _170=new Array(_16f);
var _171=new Array(_16f);
for(var i=0;i<_163.fragments.length;i++){
var _173=_163.fragments[i];
if(_173.type=="layout"){
var _174=i;
var _174=(_173.properties?_173.properties[_16a.id.PP_COLUMN]:i);
if(_174==null||_174<0||_174>=_16f){
_174=(_16f>0?(_16f-1):0);
}
_171[_174]=true;
this._layoutCreateModel(_173,(_164+1),_16e[_174],_166,false,_168,_169,_16a);
}else{
this._layoutCreatePortlet(_173,_163,_16e,_16c,_166,_170,_168,_169,_16a);
}
}
return _16e;
},_layoutCreatePortlet:function(_175,_176,_177,_178,_179,_17a,_17b,_17c,_17d){
if(_175&&_17d.debugPortletEntityIdFilter){
if(!_17c.lang.inArray(_17d.debugPortletEntityIdFilter,_175.id)){
_175=null;
}
}
if(_175){
var _17e="z";
var _17f=_175.properties[_17d.id.PP_DESKTOP_EXTENDED];
var _180=_17d.prefs.windowTiling;
var _181=_180;
var _182=_17d.prefs.windowHeightExpand;
if(_17f!=null&&_180&&_17d.prefs.printModeOnly==null){
var _183=_17f.split(_17d.id.PP_PAIR_SEPARATOR);
var _184=null,_185=0,_186=null,_187=null,_188=false;
if(_183!=null&&_183.length>0){
var _189=_17d.id.PP_PROP_SEPARATOR;
for(var _18a=0;_18a<_183.length;_18a++){
_184=_183[_18a];
_185=((_184!=null)?_184.length:0);
if(_185>0){
var _18b=_184.indexOf(_189);
if(_18b>0&&_18b<(_185-1)){
_186=_184.substring(0,_18b);
_187=_184.substring(_18b+1);
_188=((_187=="true")?true:false);
if(_186==_17d.id.PP_STATICPOS){
_181=_188;
}else{
if(_186==_17d.id.PP_FITHEIGHT){
_182=_188;
}
}
}
}
}
}
}else{
if(!_180){
_181=false;
}
}
_175.properties[_17d.id.PP_WINDOW_POSITION_STATIC]=_181;
_175.properties[_17d.id.PP_WINDOW_HEIGHT_TO_FIT]=_182;
if(_181&&_180){
var _18c=_177.length;
var _18d=_175.properties[_17d.id.PP_COLUMN];
if(_18d==null||_18d>=_18c){
_18d=_18c-1;
}else{
if(_18d<0){
_18d=0;
}
}
if(_17a[_18d]==null){
_17a[_18d]=new Array();
}
_17a[_18d].push(_175.id);
var _18e=_178+new Number(_18d);
_17e=_18e.toString();
}
if(_175.currentActionState==_17d.id.ACT_MAXIMIZE){
this.maximizedOnInit=_175.id;
}
var _18f=_175.decorator;
if(_18f!=null&&_18f.length>0){
if(_17c.lang.indexOf(_17d.prefs.portletDecorationsAllowed,_18f)==-1){
_18f=null;
}
}
if(_18f==null||_18f.length==0){
if(djConfig.isDebug&&_17d.debug.windowDecorationRandom){
_18f=_17d.prefs.portletDecorationsAllowed[Math.floor(Math.random()*_17d.prefs.portletDecorationsAllowed.length)];
}else{
_18f=this.portletDecorator;
}
}
var _190=_175.properties||{};
_190[_17d.id.PP_WINDOW_DECORATION]=_18f;
_17b[_18f]=true;
var _191=_175.actions||{};
var _192=new _17d.om.Portlet(_175.name,_175.id,null,_190,_191,_175.currentActionState,_175.currentActionMode,_175.layoutActionsDisabled);
_192.initialize();
this.putPortlet(_192);
if(_179[_17e]==null){
_179[_17e]=new Array();
}
_179[_17e].push({portlet:_192,layout:_176.id});
}
},_layoutCreateColsModel:function(_193,_194,_195,_196){
var _197=jetspeed;
this.layouts[_193.id]=_193;
var _198=false;
var _199=new Array();
if(_197.prefs.windowTiling&&_193.columnSizes.length>0){
var _19a=false;
if(_197.UAie){
_19a=true;
}
if(_195!=null&&!_196){
var _19b=new _197.om.Column(0,_193.id,(_19a?_193.columnSizesSum-0.1:_193.columnSizesSum),this.columns.length,_193.layoutActionsDisabled,_194);
_19b.layoutHeader=true;
this.columns.push(_19b);
if(_195.buildColChildren==null){
_195.buildColChildren=new Array();
}
_195.buildColChildren.push(_19b);
_195=_19b;
_198=true;
}
for(var i=0;i<_193.columnSizes.length;i++){
var size=_193.columnSizes[i];
if(_19a&&i==(_193.columnSizes.length-1)){
size=size-0.1;
}
var _19e=new _197.om.Column(i,_193.id,size,this.columns.length,_193.layoutActionsDisabled);
this.columns.push(_19e);
if(_195!=null){
if(_195.buildColChildren==null){
_195.buildColChildren=new Array();
}
_195.buildColChildren.push(_19e);
}
_199.push(_19e);
}
}
return {columnsInLayout:_199,addedLayoutHeaderColumn:_198};
},_portletsInitWinState:function(_19f){
var _1a0={};
this.getPortletCurColRow(null,false,_1a0);
for(var _1a1 in this.portlets){
var _1a2=this.portlets[_1a1];
var _1a3=_1a0[_1a2.getId()];
if(_1a3==null&&_19f){
for(var i=0;i<_19f.length;i++){
if(_19f[i].portlet.getId()==_1a2.getId()){
_1a3={layout:_19f[i].layout};
break;
}
}
}
if(_1a3!=null){
_1a2._initWinState(_1a3,false);
}else{
dojo.raise("Window state data not found for portlet: "+_1a2.getId());
}
}
},_loadPortletZIndexCompare:function(_1a5,_1a6){
var _1a7=null;
var _1a8=null;
var _1a9=null;
_1a7=_1a5.portlet._getInitialZIndex();
_1a8=_1a6.portlet._getInitialZIndex();
if(_1a7&&!_1a8){
return -1;
}else{
if(_1a8&&!_1a7){
return 1;
}else{
if(_1a7==_1a8){
return 0;
}
}
}
return (_1a7-_1a8);
},_createColsStart:function(_1aa,_1ab){
if(!this.columnsStructure||this.columnsStructure.length==0){
return;
}
var _1ac=document.createElement("div");
_1ac.id=_1ab;
_1ac.setAttribute("id",_1ab);
for(var _1ad=0;_1ad<this.columnsStructure.length;_1ad++){
var _1ae=this.columnsStructure[_1ad];
this._createCols(_1ae,_1ac);
}
_1aa.appendChild(_1ac);
},_createCols:function(_1af,_1b0){
_1af.createColumn();
if(this.colFirstNormI==-1&&!_1af.columnContainer&&!_1af.layoutHeader){
this.colFirstNormI=_1af.getPageColumnIndex();
}
var _1b1=_1af.buildColChildren;
if(_1b1!=null&&_1b1.length>0){
for(var _1b2=0;_1b2<_1b1.length;_1b2++){
this._createCols(_1b1[_1b2],_1af.domNode);
}
}
delete _1af.buildColChildren;
_1b0.appendChild(_1af.domNode);
},_removeCols:function(_1b3){
if(!this.columns||this.columns.length==0){
return;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i]){
if(_1b3){
var _1b5=jetspeed.ui.getPWinAndColChildren(this.columns[i].domNode,null);
dojo.lang.forEach(_1b5,function(_1b6){
_1b3.appendChild(_1b6);
});
}
dojo.dom.removeNode(this.columns[i]);
this.columns[i]=null;
}
}
var _1b7=dojo.byId(jetspeed.id.COLUMNS);
if(_1b7){
dojo.dom.removeNode(_1b7);
}
this.columns=[];
},getColumnDefault:function(){
if(this.colFirstNormI!=-1){
return this.columns[this.colFirstNormI];
}
return null;
},columnsEmptyCheck:function(_1b8){
var _1b9=null;
if(_1b8==null){
return _1b9;
}
var _1ba=_1b8.childNodes,_1bb;
if(_1ba){
for(var i=0;i<_1ba.length;i++){
_1bb=_1ba[i];
var _1bd=this.columnEmptyCheck(_1bb,true);
if(_1bd!=null){
_1b9=_1bd;
if(_1b9==false){
break;
}
}
}
}
return _1b9;
},columnEmptyCheck:function(_1be,_1bf){
var _1c0=null;
if(!_1be||!_1be.getAttribute){
return _1c0;
}
var _1c1=_1be.getAttribute("columnindex");
if(!_1c1||_1c1.length==0){
return _1c0;
}
var _1c2=_1be.getAttribute("layoutid");
if(_1c2==null||_1c2.length==0){
var _1c3=_1be.childNodes;
_1c0=(!_1c3||_1c3.length==0);
if(!_1bf){
_1be.style.height=(_1c0?"1px":"");
}
}
return _1c0;
},getPortletCurColRow:function(_1c4,_1c5,_1c6){
if(!this.columns||this.columns.length==0){
return null;
}
var _1c7=null;
var _1c8=((_1c4!=null)?true:false);
var _1c9=0;
var _1ca=null;
var _1cb=null;
var _1cc=0;
var _1cd=false;
for(var _1ce=0;_1ce<this.columns.length;_1ce++){
var _1cf=this.columns[_1ce];
var _1d0=_1cf.domNode.childNodes;
if(_1cb==null||_1cb!=_1cf.getLayoutId()){
_1cb=_1cf.getLayoutId();
_1ca=this.layouts[_1cb];
if(_1ca==null){
dojo.raise("Layout not found: "+_1cb);
return null;
}
_1cc=0;
_1cd=false;
if(_1ca.clonedFromRootId==null){
_1cd=true;
}else{
var _1d1=this.getColFromColNode(_1cf.domNode.parentNode);
if(_1d1==null){
dojo.raise("Parent column not found: "+_1cf);
return null;
}
_1cf=_1d1;
}
}
var _1d2=null;
var _1d3=jetspeed;
var _1d4=dojo;
var _1d5=_1d3.id.PWIN_CLASS;
if(_1c5){
_1d5+="|"+_1d3.id.PWIN_GHOST_CLASS;
}
if(_1c8){
_1d5+="|"+_1d3.id.COL_CLASS;
}
var _1d6=new RegExp("(^|\\s+)("+_1d5+")(\\s+|$)");
for(var _1d7=0;_1d7<_1d0.length;_1d7++){
var _1d8=_1d0[_1d7];
if(_1d6.test(_1d4.html.getClass(_1d8))){
_1d2=(_1d2==null?0:_1d2+1);
if((_1d2+1)>_1cc){
_1cc=(_1d2+1);
}
if(_1c4==null||_1d8==_1c4){
var _1d9={layout:_1cb,column:_1cf.getLayoutColumnIndex(),row:_1d2,columnObj:_1cf};
if(!_1cd){
_1d9.layout=_1ca.clonedFromRootId;
}
if(_1c4!=null){
_1c7=_1d9;
break;
}else{
if(_1c6!=null){
var _1da=this.getPWinFromNode(_1d8);
if(_1da==null){
_1d4.raise("PortletWindow not found for node");
}else{
var _1db=_1da.portlet;
if(_1db==null){
_1d4.raise("PortletWindow for node has null portlet: "+_1da.widgetId);
}else{
_1c6[_1db.getId()]=_1d9;
}
}
}
}
}
}
}
if(_1c7!=null){
break;
}
}
return _1c7;
},_getPortletArrayByZIndex:function(){
var _1dc=jetspeed;
var _1dd=this.getPortletArray();
if(!_1dd){
return _1dd;
}
var _1de=[];
for(var i=0;i<_1dd.length;i++){
if(!_1dd[i].getProperty(_1dc.id.PP_WINDOW_POSITION_STATIC)){
_1de.push(_1dd[i]);
}
}
_1de.sort(this._portletZIndexCompare);
return _1de;
},_portletZIndexCompare:function(_1e0,_1e1){
var _1e2=null;
var _1e3=null;
var _1e4=null;
_1e4=_1e0.getSavedWinState();
_1e2=_1e4.zIndex;
_1e4=_1e1.getSavedWinState();
_1e3=_1e4.zIndex;
if(_1e2&&!_1e3){
return -1;
}else{
if(_1e3&&!_1e2){
return 1;
}else{
if(_1e2==_1e3){
return 0;
}
}
}
return (_1e2-_1e3);
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
var _1f7=[];
for(var _1f8 in this.portlets){
var _1f9=this.portlets[_1f8];
_1f7.push(_1f9);
}
return _1f7;
},getPortletIds:function(){
if(!this.portlets){
return null;
}
var _1fa=[];
for(var _1fb in this.portlets){
var _1fc=this.portlets[_1fb];
_1fa.push(_1fc.getId());
}
return _1fa;
},getPortletByName:function(_1fd){
if(this.portlets&&_1fd){
for(var _1fe in this.portlets){
var _1ff=this.portlets[_1fe];
if(_1ff.name==_1fd){
return _1ff;
}
}
}
return null;
},getPortlet:function(_200){
if(this.portlets&&_200){
return this.portlets[_200];
}
return null;
},getPWinFromNode:function(_201){
var _202=null;
if(this.portlets&&_201){
for(var _203 in this.portlets){
var _204=this.portlets[_203];
var _205=_204.getPWin();
if(_205!=null){
if(_205.domNode==_201){
_202=_205;
break;
}
}
}
}
return _202;
},putPortlet:function(_206){
if(!_206){
return;
}
if(!this.portlets){
this.portlets={};
}
this.portlets[_206.entityId]=_206;
this.portlet_count++;
},putPWin:function(_207){
if(!_207){
return;
}
var _208=_207.widgetId;
if(!_208){
dojo.raise("PortletWindow id is null");
}
this.portlet_windows[_208]=_207;
this.portlet_window_count++;
},getPWin:function(_209){
if(this.portlet_windows&&_209){
var pWin=this.portlet_windows[_209];
if(pWin==null){
var jsId=jetspeed.id;
pWin=this.portlet_windows[jsId.PW_ID_PREFIX+_209];
if(pWin==null){
var p=this.getPortlet(_209);
if(p!=null){
pWin=this.portlet_windows[p.properties[jsObj.id.PP_WIDGET_ID]];
}
}
}
return pWin;
}
return null;
},getPWins:function(_20d){
var _20e=this.portlet_windows;
var pWin;
var _210=[];
for(var _211 in _20e){
pWin=_20e[_211];
if(pWin&&(!_20d||pWin.portlet)){
_210.push(pWin);
}
}
return _210;
},getPWinTopZIndex:function(_212){
var _213=0;
if(_212){
_213=this.portlet_tiled_high_z+1;
this.portlet_tiled_high_z=_213;
}else{
if(this.portlet_untiled_high_z==-1){
this.portlet_untiled_high_z=200;
}
_213=this.portlet_untiled_high_z+1;
this.portlet_untiled_high_z=_213;
}
return _213;
},getPWinHighZIndex:function(){
return Math.max(this.portlet_tiled_high_z,this.portlet_untiled_high_z);
},displayAllPWins:function(_214,_215){
return;
},onBrowserWindowResize:function(){
var _216=jetspeed;
var _217=this.portlet_windows;
var pWin;
for(var _219 in _217){
pWin=_217[_219];
pWin.onBrowserWindowResize();
}
if(_216.UAie6&&this.editMode){
var _21a=dojo.widget.byId(_216.id.PG_ED_WID);
if(_21a!=null){
_21a.onBrowserWindowResize();
}
}
},regPWinIFrameCover:function(_21b){
if(!_21b){
return;
}
this.iframeCoverByWinId[_21b.widgetId]=true;
},unregPWinIFrameCover:function(_21c){
if(!_21c){
return;
}
delete this.iframeCoverByWinId[_21c.widgetId];
},displayAllPWinIFrameCovers:function(_21d,_21e){
var _21f=this.portlet_windows;
var _220=this.iframeCoverByWinId;
if(!_21f||!_220){
return;
}
for(var _221 in _220){
if(_221==_21e){
continue;
}
var pWin=_21f[_221];
var _223=(pWin&&pWin.iframesInfo?pWin.iframesInfo.iframeCover:null);
if(_223){
_223.style.display=(_21d?"none":"block");
}
}
},createLayoutInfo:function(_224){
var _225=dojo;
var _226=null;
var _227=null;
var _228=null;
var _229=null;
var _22a=document.getElementById(_224.id.DESKTOP);
if(_22a!=null){
_226=_224.ui.getLayoutExtents(_22a,null,_225,_224);
}
var _22b=document.getElementById(_224.id.COLUMNS);
if(_22b!=null){
_227=_224.ui.getLayoutExtents(_22b,null,_225,_224);
}
if(this.columns){
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col.layoutHeader){
_229=_224.ui.getLayoutExtents(col.domNode,null,_225,_224);
}else{
if(!col.columnContainer){
_228=_224.ui.getLayoutExtents(col.domNode,null,_225,_224);
}
}
if(_228!=null&&_229!=null){
break;
}
}
}
this.layoutInfo={desktop:(_226!=null?_226:{}),columns:(_227!=null?_227:{}),column:(_228!=null?_228:{}),columnLayoutHeader:(_229!=null?_229:{})};
_224.widget.PortletWindow.prototype.colWidth_pbE=((_228&&_228.pbE)?_228.pbE.w:0);
},_beforeAddOnLoad:function(){
this.win_onload=true;
},destroy:function(){
var _22e=jetspeed;
var _22f=dojo;
_22e.ui.evtDisconnect("after",window,"onresize",_22e.ui.windowResizeMgr,"onResize",_22f.event);
_22e.ui.evtDisconnect("before",_22f,"addOnLoad",this,"_beforeAddOnLoad",_22f.event);
var _230=this.portlet_windows;
var _231=this.getPWins(true);
var pWin,_233;
for(var i=0;i<_231.length;i++){
pWin=_231[i];
_233=pWin.widgetId;
pWin.closeWindow();
delete _230[_233];
this.portlet_window_count--;
}
this.portlets={};
this.portlet_count=0;
var _235=_22f.widget.byId(_22e.id.PG_ED_WID);
if(_235!=null){
_235.editPageDestroy();
}
this._removeCols(document.getElementById(_22e.id.DESKTOP));
this._destroyPageControls();
},getColFromColNode:function(_236){
if(_236==null){
return null;
}
var _237=_236.getAttribute("columnindex");
if(_237==null){
return null;
}
var _238=new Number(_237);
if(_238>=0&&_238<this.columns.length){
return this.columns[_238];
}
return null;
},getColIndexForNode:function(node){
var _23a=null;
if(!this.columns){
return _23a;
}
for(var i=0;i<this.columns.length;i++){
if(this.columns[i].containsNode(node)){
_23a=i;
break;
}
}
return _23a;
},getColWithNode:function(node){
var _23d=this.getColIndexForNode(node);
return ((_23d!=null&&_23d>=0)?this.columns[_23d]:null);
},getDescendantCols:function(_23e){
var dMap={};
if(_23e==null){
return dMap;
}
for(var i=0;i<this.columns.length;i++){
var col=this.columns[i];
if(col!=_23e&&_23e.containsDescendantNode(col.domNode)){
dMap[i]=col;
}
}
return dMap;
},putMenu:function(_242){
if(!_242){
return;
}
var _243=(_242.getName?_242.getName():null);
if(_243!=null){
this.menus[_243]=_242;
}
},getMenu:function(_244){
if(_244==null){
return null;
}
return this.menus[_244];
},removeMenu:function(_245){
if(_245==null){
return;
}
var _246=null;
if(dojo.lang.isString(_245)){
_246=_245;
}else{
_246=(_245.getName?_245.getName():null);
}
if(_246!=null){
delete this.menus[_246];
}
},clearMenus:function(){
this.menus=[];
},getMenuNames:function(){
var _247=[];
for(var _248 in this.menus){
_247.push(_248);
}
return _247;
},retrieveMenuDeclarations:function(_249,_24a,_24b){
contentListener=new jetspeed.om.MenusApiCL(_249,_24a,_24b);
this.clearMenus();
var _24c="?action=getmenus";
if(_249){
_24c+="&includeMenuDefs=true";
}
var _24d=this.getPsmlUrl()+_24c;
var _24e="text/xml";
var _24f=new jetspeed.om.Id("getmenus",{page:this});
jetspeed.url.retrieveContent({url:_24d,mimetype:_24e},contentListener,_24f,jetspeed.debugContentDumpIds);
},syncPageControls:function(_250){
var jsId=_250.id;
if(this.actionButtons==null){
return;
}
for(var _252 in this.actionButtons){
var _253=false;
if(_252==jsId.ACT_EDIT){
if(!this.editMode){
_253=true;
}
}else{
if(_252==jsId.ACT_VIEW){
if(this.editMode){
_253=true;
}
}else{
if(_252==jsId.ACT_ADDPORTLET){
if(!this.editMode){
_253=true;
}
}else{
_253=true;
}
}
}
if(_253){
this.actionButtons[_252].style.display="";
}else{
this.actionButtons[_252].style.display="none";
}
}
},renderPageControls:function(_254){
var _254=jetspeed;
var _255=_254.page;
var jsId=_254.id;
var _257=dojo;
var _258=[];
if(this.actions!=null){
var addP=false;
for(var _25a in this.actions){
if(_25a!=jsId.ACT_HELP){
_258.push(_25a);
}
}
if(this.actions[jsId.ACT_EDIT]!=null){
addP=true;
if(this.actions[jsId.ACT_VIEW]==null){
_258.push(jsId.ACT_VIEW);
}
}
if(this.actions[jsId.ACT_VIEW]!=null){
addP=true;
if(this.actions[jsId.ACT_EDIT]==null){
_258.push(jsId.ACT_EDIT);
}
}
var _25b=(_255.rootFragmentId?_255.layouts[_255.rootFragmentId]:null);
var _25c=(!(_25b==null||_25b.layoutActionsDisabled));
if(_25c){
_25c=_255._perms(_254.prefs,_254.id.PM_P_AD,String.fromCharCode);
if(_25c&&!this.isUA()&&(addP||_255.canNPE())){
_258.push(jsId.ACT_ADDPORTLET);
}
}
}
var _25d=_257.byId(jsId.PAGE_CONTROLS);
if(_25d!=null&&_258!=null&&_258.length>0){
var _25e=_254.prefs;
var jsUI=_254.ui;
var _260=_257.event;
var _261=_255.tooltipMgr;
if(this.actionButtons==null){
this.actionButtons={};
this.actionButtonTooltips=[];
}
var _262=this.actionButtonTooltips;
for(var i=0;i<_258.length;i++){
var _25a=_258[i];
var _264=document.createElement("div");
_264.className="portalPageActionButton";
_264.style.backgroundImage="url("+_25e.getLayoutRootUrl()+"/images/desktop/"+_25a+".gif)";
_264.actionName=_25a;
this.actionButtons[_25a]=_264;
_25d.appendChild(_264);
jsUI.evtConnect("after",_264,"onclick",this,"pageActionButtonClick",_260);
if(_25e.pageActionButtonTooltip){
var _265=null;
if(_25e.desktopActionLabels!=null){
_265=_25e.desktopActionLabels[_25a];
}
if(_265==null||_265.length==0){
_265=_257.string.capitalize(_25a);
}
_262.push(_261.addNode(_264,_265,true,null,null,null,_254,jsUI,_260));
}
}
}
},_destroyPageControls:function(){
var _266=jetspeed;
if(this.actionButtons){
for(var _267 in this.actionButtons){
var _268=this.actionButtons[_267];
if(_268){
_266.ui.evtDisconnect("after",_268,"onclick",this,"pageActionButtonClick");
}
}
}
var _269=dojo.byId(_266.id.PAGE_CONTROLS);
if(_269!=null&&_269.childNodes&&_269.childNodes.length>0){
for(var i=(_269.childNodes.length-1);i>=0;i--){
dojo.dom.removeNode(_269.childNodes[i]);
}
}
_266.page.tooltipMgr.removeNodes(this.actionButtonTooltips);
this.actionButtonTooltips=null;
this.actionButtons==null;
},pageActionButtonClick:function(evt){
if(evt==null||evt.target==null){
return;
}
this.pageActionProcess(evt.target.actionName,evt);
},pageActionProcess:function(_26c){
var _26d=jetspeed;
if(_26c==null){
return;
}
if(_26c==_26d.id.ACT_ADDPORTLET){
this.addPortletInitiate();
}else{
if(_26c==_26d.id.ACT_EDIT){
_26d.changeActionForPortlet(this.rootFragmentId,null,_26d.id.ACT_EDIT,new _26d.om.PageChangeActionCL());
_26d.editPageInitiate(_26d);
}else{
if(_26c==_26d.id.ACT_VIEW){
_26d.editPageTerminate(_26d);
}else{
var _26e=this.getPageAction(_26c);
if(_26e==null){
return;
}
if(_26e.url==null){
return;
}
var _26f=_26d.url.basePortalUrl()+_26d.url.path.DESKTOP+"/"+_26e.url;
_26d.pageNavigate(_26f);
}
}
}
},getPageAction:function(name){
if(this.actions==null){
return null;
}
return this.actions[name];
},addPortletInitiate:function(_271,_272){
var _273=jetspeed;
var jsId=_273.id;
if(!_272){
_272=escape(this.getPagePathAndQuery());
}else{
_272=escape(_272);
}
var _275=_273.url.basePortalUrl()+_273.url.path.DESKTOP+"/system/customizer/selector.psml?jspage="+_272;
if(_271!=null){
_275+="&jslayoutid="+escape(_271);
}
if(!this.editMode){
_275+="&"+_273.id.ADDP_RFRAG+"="+escape(this.rootFragmentId);
}
if(this.actions&&(this.actions[jsId.ACT_EDIT]||this.actions[jsId.ACT_VIEW])){
_273.changeActionForPortlet(this.rootFragmentId,null,jsId.ACT_EDIT,new _273.om.PageChangeActionCL(_275));
}else{
if(!this.isUA()){
_273.pageNavigate(_275);
}
}
},addPortletTerminate:function(_276,_277){
var _278=jetspeed;
var _279=_278.url.getQueryParameter(document.location.href,_278.id.ADDP_RFRAG);
if(_279!=null&&_279.length>0){
var _27a=_277;
var qPos=_277.indexOf("?");
if(qPos>0){
_27a.substring(0,qPos);
}
_278.changeActionForPortlet(_279,null,_278.id.ACT_VIEW,new _278.om.PageChangeActionCL(_276),_27a);
}else{
_278.pageNavigate(_276);
}
},setPageModePortletActions:function(_27c){
if(_27c==null||_27c.actions==null){
return;
}
var jsId=jetspeed.id;
if(_27c.actions[jsId.ACT_REMOVEPORTLET]==null){
_27c.actions[jsId.ACT_REMOVEPORTLET]={id:jsId.ACT_REMOVEPORTLET};
}
},getPageUrl:function(_27e){
if(this.pageUrl!=null&&!_27e){
return this.pageUrl;
}
var jsU=jetspeed.url;
var _280=jsU.path.SERVER+((_27e)?jsU.path.PORTAL:jsU.path.DESKTOP)+this.getPath();
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
if(!_27e){
this.pageUrl=_280;
}
return _280;
},getPagePathAndQuery:function(){
if(this.pagePathAndQuery!=null){
return this.pagePathAndQuery;
}
var jsU=jetspeed.url;
var _286=this.getPath();
var _287=jsU.parse(_286);
var _288=null;
if(this.pageUrlFallback!=null){
_288=jsU.parse(this.pageUrlFallback);
}else{
_288=jsU.parse(window.location.href);
}
if(_287!=null&&_288!=null){
var _289=_288.query;
if(_289!=null&&_289.length>0){
var _28a=_287.query;
if(_28a!=null&&_28a.length>0){
_286=_286+"&"+_289;
}else{
_286=_286+"?"+_289;
}
}
}
this.pagePathAndQuery=_286;
return _286;
},getPageDirectory:function(_28b){
var _28c="/";
var _28d=(_28b?this.getRealPath():this.getPath());
if(_28d!=null){
var _28e=_28d.lastIndexOf("/");
if(_28e!=-1){
if((_28e+1)<_28d.length){
_28c=_28d.substring(0,_28e+1);
}else{
_28c=_28d;
}
}
}
return _28c;
},equalsPageUrl:function(url){
if(url==this.getPath()){
return true;
}
if(url==this.getPageUrl()){
return true;
}
return false;
},makePageUrl:function(_290){
if(!_290){
_290="";
}
var jsU=jetspeed.url;
if(!jsU.urlStartsWithHttp(_290)){
return jsU.path.SERVER+jsU.path.DESKTOP+_290;
}
return _290;
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
jetspeed.om.Column=function(_292,_293,size,_295,_296,_297){
this.layoutColumnIndex=_292;
this.layoutId=_293;
this.size=size;
this.pageColumnIndex=new Number(_295);
if(typeof _296!="undefined"){
this.layoutActionsDisabled=_296;
}
if((typeof _297!="undefined")&&_297!=null){
this.layoutDepth=_297;
}
this.id="jscol_"+_295;
this.domNode=null;
};
dojo.lang.extend(jetspeed.om.Column,{styleClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn":""),styleLayoutClass:jetspeed.id.COL_CLASS+(jetspeed.UAie6?" ie6desktopColumn ":" ")+jetspeed.id.COL_LAYOUTHEADER_CLASS,layoutColumnIndex:null,layoutId:null,layoutDepth:null,layoutMaxChildDepth:0,size:null,pageColumnIndex:null,layoutActionsDisabled:false,domNode:null,columnContainer:false,layoutHeader:false,createColumn:function(_298){
var _299=this.styleClass;
var _29a=this.pageColumnIndex;
if(this.isStartOfColumnSet()&&_29a>0){
_299+=" desktopColumnClear-PRIVATE";
}
var _29b=document.createElement("div");
_29b.setAttribute("columnindex",_29a);
_29b.style.width=this.size+"%";
if(this.layoutHeader){
_299=this.styleLayoutClass;
_29b.setAttribute("layoutid",this.layoutId);
}
_29b.className=_299;
_29b.id=this.getId();
this.domNode=_29b;
if(_298!=null){
_298.appendChild(_29b);
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
},_updateLayoutDepth:function(_29e){
var _29f=this.layoutDepth;
if(_29f!=null&&_29e!=_29f){
this.layoutDepth=_29e;
this.layoutDepthChanged();
}
},_updateLayoutChildDepth:function(_2a0){
this.layoutMaxChildDepth=(_2a0==null?0:_2a0);
}});
jetspeed.om.Portlet=function(_2a1,_2a2,_2a3,_2a4,_2a5,_2a6,_2a7,_2a8){
this.name=_2a1;
this.entityId=_2a2;
this.properties=_2a4;
this.actions=_2a5;
jetspeed.page.setPageModePortletActions(this);
this.currentActionState=_2a6;
this.currentActionMode=_2a7;
if(_2a3){
this.contentRetriever=_2a3;
}
this.layoutActionsDisabled=false;
if(typeof _2a8!="undefined"){
this.layoutActionsDisabled=_2a8;
}
};
dojo.lang.extend(jetspeed.om.Portlet,{name:null,entityId:null,isPortlet:true,pageColumnIndex:null,contentRetriever:new jetspeed.om.PortletContentRetriever(),windowFactory:null,lastSavedWindowState:null,initialize:function(){
var _2a9=jetspeed;
var jsId=_2a9.id;
if(!this.properties[jsId.PP_WIDGET_ID]){
this.properties[jsId.PP_WIDGET_ID]=jsId.PW_ID_PREFIX+this.entityId;
}
if(!this.properties[jsId.PP_CONTENT_RETRIEVER]){
this.properties[jsId.PP_CONTENT_RETRIEVER]=this.contentRetriever;
}
var _2ab=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
if(_2a9.prefs.windowTiling){
if(_2ab=="true"){
_2ab=true;
}else{
if(_2ab=="false"){
_2ab=false;
}else{
if(_2ab!=true&&_2ab!=false){
_2ab=true;
}
}
}
}else{
_2ab=false;
}
this.properties[jsId.PP_WINDOW_POSITION_STATIC]=_2ab;
var _2ac=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
if(_2ac=="true"){
_2ac=true;
}else{
if(_2ab=="false"){
_2ac=false;
}else{
if(_2ac!=true&&_2ac!=false){
_2ac=true;
}
}
}
this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2ac;
var _2ad=this.properties[jsId.PP_WINDOW_TITLE];
if(!_2ad&&this.name){
var re=(/^[^:]*:*/);
_2ad=this.name.replace(re,"");
this.properties[jsId.PP_WINDOW_TITLE]=_2ad;
}
},postParseAnnotateHtml:function(_2af){
var _2b0=jetspeed;
var _2b1=_2b0.portleturl;
if(_2af){
var _2b2=_2af;
var _2b3=_2b2.getElementsByTagName("form");
var _2b4=_2b0.debug.postParseAnnotateHtml;
var _2b5=_2b0.debug.postParseAnnotateHtmlDisableAnchors;
if(_2b3){
for(var i=0;i<_2b3.length;i++){
var _2b7=_2b3[i];
var _2b8=_2b7.action;
var _2b9=_2b1.parseContentUrl(_2b8);
var op=_2b9.operation;
var _2bb=(op==_2b1.PORTLET_REQUEST_ACTION||op==_2b1.PORTLET_REQUEST_RENDER);
var _2bc=false;
if(dojo.io.formHasFile(_2b7)){
if(_2bb){
var _2bd=_2b0.url.parse(_2b8);
_2bd=_2b0.url.addQueryParameter(_2bd,"encoder","desktop",true);
_2bd=_2b0.url.addQueryParameter(_2bd,"jsdajax","false",true);
_2b7.action=_2bd.toString();
}else{
_2bc=true;
}
}else{
if(_2bb){
var _2be=_2b1.genPseudoUrl(_2b9,true);
_2b7.action=_2be;
var _2bf=new _2b0.om.ActionRenderFormBind(_2b7,_2b9.url,_2b9.portletEntityId,op);
if(_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] adding FormBind ("+op+") for form with action: "+_2b8);
}
}else{
if(_2b8==null||_2b8.length==0){
var _2bf=new _2b0.om.ActionRenderFormBind(_2b7,null,this.entityId,null);
if(_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute is empty - adding FormBind with expectation that form action will be set via script");
}
}else{
_2bc=true;
}
}
}
if(_2bc&&_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] form action attribute doesn't match annotation criteria, leaving as is: "+_2b8);
}
}
}
var _2c0=_2b2.getElementsByTagName("a");
if(_2c0){
for(var i=0;i<_2c0.length;i++){
var _2c1=_2c0[i];
var _2c2=_2c1.href;
var _2b9=_2b1.parseContentUrl(_2c2);
var _2c3=null;
if(!_2b5){
_2c3=_2b1.genPseudoUrl(_2b9);
}
if(!_2c3){
if(_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] leaving href as is: "+_2c2);
}
}else{
if(_2c3==_2c2){
if(_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed and regenerated identically: "+_2c2);
}
}else{
if(_2b4){
dojo.debug("postParseAnnotateHtml ["+this.entityId+"] href parsed, replacing: "+_2c2+" with: "+_2c3);
}
_2c1.href=_2c3;
}
}
}
}
}
},getPWin:function(){
var _2c4=jetspeed;
var _2c5=this.properties[_2c4.id.PP_WIDGET_ID];
if(_2c5){
return _2c4.page.getPWin(_2c5);
}
return null;
},getCurWinState:function(_2c6){
var _2c7=null;
try{
var _2c8=this.getPWin();
if(!_2c8){
return null;
}
_2c7=_2c8.getCurWinStateForPersist(_2c6);
if(!_2c6){
if(_2c7.layout==null){
_2c7.layout=this.lastSavedWindowState.layout;
}
}
}
catch(e){
dojo.raise("portlet.getCurWinState "+jetspeed.formatError(e));
}
return _2c7;
},getSavedWinState:function(){
if(!this.lastSavedWindowState){
dojo.raise("Portlet not initialized: "+this.name);
}
return this.lastSavedWindowState;
},getInitialWinDims:function(_2c9,_2ca){
var _2cb=jetspeed;
var jsId=_2cb.id;
if(!_2c9){
_2c9={};
}
var _2cd=this.properties[jsId.PP_WINDOW_POSITION_STATIC];
var _2ce=this.properties[jsId.PP_WINDOW_HEIGHT_TO_FIT];
_2c9[jsId.PP_WINDOW_POSITION_STATIC]=_2cd;
_2c9[jsId.PP_WINDOW_HEIGHT_TO_FIT]=_2ce;
var _2cf=this.properties["width"];
if(!_2ca&&_2cf!=null&&_2cf>0){
_2c9.width=Math.floor(_2cf);
}else{
if(_2ca){
_2c9.width=-1;
}
}
var _2d0=this.properties["height"];
if(!_2ca&&_2d0!=null&&_2d0>0){
_2c9.height=Math.floor(_2d0);
}else{
if(_2ca){
_2c9.height=-1;
}
}
if(!_2cd||!_2cb.prefs.windowTiling){
var _2d1=this.properties["x"];
if(!_2ca&&_2d1!=null&&_2d1>=0){
_2c9.left=Math.floor(((_2d1>0)?_2d1:0));
}else{
if(_2ca){
_2c9.left=-1;
}
}
var _2d2=this.properties["y"];
if(!_2ca&&_2d2!=null&&_2d2>=0){
_2c9.top=Math.floor(((_2d2>0)?_2d2:0));
}else{
_2c9.top=-1;
}
var _2d3=this._getInitialZIndex(_2ca);
if(_2d3!=null){
_2c9.zIndex=_2d3;
}
}
return _2c9;
},_initWinState:function(_2d4,_2d5){
var _2d6=jetspeed;
var _2d7=(_2d4?_2d4:{});
this.getInitialWinDims(_2d7,_2d5);
if(_2d6.debug.initWinState){
var _2d8=this.properties[_2d6.id.PP_WINDOW_POSITION_STATIC];
if(!_2d8||!_2d6.prefs.windowTiling){
dojo.debug("initWinState ["+this.entityId+"] z="+_2d7.zIndex+" x="+_2d7.left+" y="+_2d7.top+" width="+_2d7.width+" height="+_2d7.height);
}else{
dojo.debug("initWinState ["+this.entityId+"] column="+_2d7.column+" row="+_2d7.row+" width="+_2d7.width+" height="+_2d7.height);
}
}
this.lastSavedWindowState=_2d7;
return _2d7;
},_getInitialZIndex:function(_2d9){
var _2da=null;
var _2db=this.properties["z"];
if(!_2d9&&_2db!=null&&_2db>=0){
_2da=Math.floor(_2db);
}else{
if(_2d9){
_2da=-1;
}
}
return _2da;
},_getChangedWindowState:function(_2dc){
var jsId=jetspeed.id;
var _2de=this.getSavedWinState();
if(_2de&&dojo.lang.isEmpty(_2de)){
_2de=null;
_2dc=false;
}
var _2df=this.getCurWinState(_2dc);
var _2e0=_2df[jsId.PP_WINDOW_POSITION_STATIC];
var _2e1=!_2e0;
if(!_2de){
var _2e2={state:_2df,positionChanged:true,extendedPropChanged:true};
if(_2e1){
_2e2.zIndexChanged=true;
}
return _2e2;
}
var _2e3=false;
var _2e4=false;
var _2e5=false;
var _2e6=false;
for(var _2e7 in _2df){
if(_2df[_2e7]!=_2de[_2e7]){
if(_2e7==jsId.PP_WINDOW_POSITION_STATIC||_2e7==jsId.PP_WINDOW_HEIGHT_TO_FIT){
_2e3=true;
_2e5=true;
_2e4=true;
}else{
if(_2e7=="zIndex"){
if(_2e1){
_2e3=true;
_2e6=true;
}
}else{
_2e3=true;
_2e4=true;
}
}
}
}
if(_2e3){
var _2e2={state:_2df,positionChanged:_2e4,extendedPropChanged:_2e5};
if(_2e1){
_2e2.zIndexChanged=_2e6;
}
return _2e2;
}
return null;
},getPortletUrl:function(_2e8){
var _2e9=jetspeed;
var _2ea=_2e9.url;
var _2eb=null;
if(_2e8&&_2e8.url){
_2eb=_2e8.url;
}else{
if(_2e8&&_2e8.formNode){
var _2ec=_2e8.formNode.getAttribute("action");
if(_2ec){
_2eb=_2ec;
}
}
}
if(_2eb==null){
_2eb=_2ea.basePortalUrl()+_2ea.path.PORTLET+_2e9.page.getPath();
}
if(!_2e8.dontAddQueryArgs){
_2eb=_2ea.parse(_2eb);
_2eb=_2ea.addQueryParameter(_2eb,"entity",this.entityId,true);
_2eb=_2ea.addQueryParameter(_2eb,"portlet",this.name,true);
_2eb=_2ea.addQueryParameter(_2eb,"encoder","desktop",true);
if(_2e8.jsPageUrl!=null){
var _2ed=_2e8.jsPageUrl.query;
if(_2ed!=null&&_2ed.length>0){
_2eb=_2eb.toString()+"&"+_2ed;
}
}
}
if(_2e8){
_2e8.url=_2eb.toString();
}
return _2eb;
},_submitAjaxApi:function(_2ee,_2ef,_2f0){
var _2f1=jetspeed;
var _2f2="?action="+_2ee+"&id="+this.entityId+_2ef;
var _2f3=_2f1.url.basePortalUrl()+_2f1.url.path.AJAX_API+_2f1.page.getPath()+_2f2;
var _2f4="text/xml";
var _2f5=new _2f1.om.Id(_2ee,this.entityId);
_2f5.portlet=this;
_2f1.url.retrieveContent({url:_2f3,mimetype:_2f4},_2f0,_2f5,_2f1.debugContentDumpIds);
},submitWinState:function(_2f6,_2f7){
var _2f8=jetspeed;
var jsId=_2f8.id;
if(_2f8.page.isUA()||(!(_2f8.page.getPageAction(jsId.ACT_EDIT)||_2f8.page.getPageAction(jsId.ACT_VIEW)||_2f8.page.canNPE()))){
return;
}
var _2fa=null;
if(_2f7){
_2fa={state:this._initWinState(null,true)};
}else{
_2fa=this._getChangedWindowState(_2f6);
}
if(_2fa){
var _2fb=_2fa.state;
var _2fc=_2fb[jsId.PP_WINDOW_POSITION_STATIC];
var _2fd=_2fb[jsId.PP_WINDOW_HEIGHT_TO_FIT];
var _2fe=null;
if(_2fa.extendedPropChanged){
var _2ff=jsId.PP_PROP_SEPARATOR;
var _300=jsId.PP_PAIR_SEPARATOR;
_2fe=jsId.PP_STATICPOS+_2ff+_2fc.toString();
_2fe+=_300+jsId.PP_FITHEIGHT+_2ff+_2fd.toString();
_2fe=escape(_2fe);
}
var _301="";
var _302=null;
if(_2fc){
_302="moveabs";
if(_2fb.column!=null){
_301+="&col="+_2fb.column;
}
if(_2fb.row!=null){
_301+="&row="+_2fb.row;
}
if(_2fb.layout!=null){
_301+="&layoutid="+_2fb.layout;
}
if(_2fb.height!=null){
_301+="&height="+_2fb.height;
}
}else{
_302="move";
if(_2fb.zIndex!=null){
_301+="&z="+_2fb.zIndex;
}
if(_2fb.width!=null){
_301+="&width="+_2fb.width;
}
if(_2fb.height!=null){
_301+="&height="+_2fb.height;
}
if(_2fb.left!=null){
_301+="&x="+_2fb.left;
}
if(_2fb.top!=null){
_301+="&y="+_2fb.top;
}
}
if(_2fe!=null){
_301+="&"+jsId.PP_DESKTOP_EXTENDED+"="+_2fe;
}
this._submitAjaxApi(_302,_301,new _2f8.om.MoveApiCL(this,_2fb));
if(!_2f6&&!_2f7){
if(!_2fc&&_2fa.zIndexChanged){
var _303=_2f8.page.getPortletArray();
if(_303&&(_303.length-1)>0){
for(var i=0;i<_303.length;i++){
var _305=_303[i];
if(_305&&_305.entityId!=this.entityId){
if(!_305.properties[_2f8.id.PP_WINDOW_POSITION_STATIC]){
_305.submitWinState(true);
}
}
}
}
}else{
if(_2fc){
}
}
}
}
},retrieveContent:function(_306,_307,_308){
if(_306==null){
_306=new jetspeed.om.PortletCL(this,_308,_307);
}
if(!_307){
_307={};
}
var _309=this;
_309.getPortletUrl(_307);
this.contentRetriever.getContent(_307,_306,_309,jetspeed.debugContentDumpIds);
},setPortletContent:function(_30a,_30b,_30c){
var _30d=this.getPWin();
if(_30c!=null&&_30c.length>0){
this.properties[jetspeed.id.PP_WINDOW_TITLE]=_30c;
if(_30d&&!this.loadingIndicatorIsShown()){
_30d.setPortletTitle(_30c);
}
}
if(_30d){
_30d.setPortletContent(_30a,_30b);
}
},loadingIndicatorIsShown:function(){
var jsId=jetspeed.id;
var _30f=this._getLoadingActionLabel(jsId.ACT_LOAD_RENDER);
var _310=this._getLoadingActionLabel(jsId.ACT_LOAD_ACTION);
var _311=this._getLoadingActionLabel(jsId.ACT_LOAD_UPDATE);
var _312=this.getPWin();
if(_312&&(_30f||_310)){
var _313=_312.getPortletTitle();
if(_313&&(_313==_30f||_313==_310)){
return true;
}
}
return false;
},_getLoadingActionLabel:function(_314){
var _315=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_315=jetspeed.prefs.desktopActionLabels[_314];
if(_315!=null&&_315.length==0){
_315=null;
}
}
return _315;
},loadingIndicatorShow:function(_316){
if(_316&&!this.loadingIndicatorIsShown()){
var _317=this._getLoadingActionLabel(_316);
var _318=this.getPWin();
if(_318&&_317){
_318.setPortletTitle(_317);
}
}
},loadingIndicatorHide:function(){
var _319=this.getPWin();
if(_319){
_319.setPortletTitle(this.properties[jetspeed.id.PP_WINDOW_TITLE]);
}
},getId:function(){
return this.entityId;
},getProperty:function(name){
return this.properties[name];
},getProperties:function(){
return this.properties;
},renderAction:function(_31b,_31c){
var _31d=jetspeed;
var _31e=_31d.url;
var _31f=null;
if(_31b!=null){
_31f=this.getAction(_31b);
}
var _320=_31c;
if(_320==null&&_31f!=null){
_320=_31f.url;
}
if(_320==null){
return;
}
var _321=_31e.basePortalUrl()+_31e.path.PORTLET+"/"+_320+_31d.page.getPath();
if(_31b!=_31d.id.ACT_PRINT){
this.retrieveContent(null,{url:_321});
}else{
var _322=_31d.page.getPageUrl();
_322=_31e.addQueryParameter(_322,"jsprintmode","true");
_322=_31e.addQueryParameter(_322,"jsaction",escape(_31f.url));
_322=_31e.addQueryParameter(_322,"jsentity",this.entityId);
_322=_31e.addQueryParameter(_322,"jslayoutid",this.lastSavedWindowState.layout);
window.open(_322.toString(),"jsportlet_print","status,scrollbars,resizable,menubar");
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
},updateActions:function(_324,_325,_326){
if(_324){
this.actions=_324;
}else{
this.actions={};
}
this.currentActionState=_325;
this.currentActionMode=_326;
this.syncActions();
},syncActions:function(){
var _327=jetspeed;
_327.page.setPageModePortletActions(this);
var _328=this.getPWin();
if(_328){
_328.actionBtnSync(_327,_327.id);
}
}});
jetspeed.om.ActionRenderFormBind=function(form,url,_32b,_32c){
dojo.io.FormBind.call(this,{url:url,formNode:form});
this.entityId=_32b;
this.submitOperation=_32c;
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
},eventConfMgr:function(_32f){
var fn=(_32f)?"disconnect":"connect";
var _331=dojo.event;
var form=this.form;
_331[fn]("after",form,"onsubmit",this,"submit",null);
for(var i=0;i<form.elements.length;i++){
var node=form.elements[i];
if(node&&node.type&&dojo.lang.inArray(["submit","button"],node.type.toLowerCase())){
_331[fn]("after",node,"onclick",this,"click",null);
}
}
var _335=form.getElementsByTagName("input");
for(var i=0;i<_335.length;i++){
var _336=_335[i];
if(_336.type.toLowerCase()=="image"&&_336.form==form){
_331[fn]("after",_336,"onclick",this,"click",null);
}
}
var as=form.getElementsByTagName("a");
for(var i=0;i<as.length;i++){
_331[fn]("before",as[i],"onclick",this,"click",null);
}
},onSubmit:function(_338){
var _339=true;
if(this.isFormSubmitInProgress()){
_339=false;
}else{
if(jetspeed.debug.confirmOnSubmit){
if(!confirm("Click OK to submit.")){
_339=false;
}
}
}
return _339;
},submit:function(e){
if(e){
e.preventDefault();
}
if(this.isFormSubmitInProgress()){
}else{
if(this.onSubmit(this.form)){
var _33b=jetspeed.portleturl.parseContentUrl(this.form.action);
var _33c={};
if(_33b.operation==jetspeed.portleturl.PORTLET_REQUEST_ACTION||_33b.operation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
var _33d=jetspeed.portleturl.genPseudoUrl(_33b,true);
this.form.action=_33d;
this.submitOperation=_33b.operation;
this.entityId=_33b.portletEntityId;
_33c.url=_33b.url;
}
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER||this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_ACTION){
this.isFormSubmitInProgress(true);
_33c.formFilter=dojo.lang.hitch(this,"formFilter");
_33c.submitFormBindObject=this;
if(this.submitOperation==jetspeed.portleturl.PORTLET_REQUEST_RENDER){
jetspeed.doRender(dojo.lang.mixin(this.bindArgs,_33c),this.entityId);
}else{
jetspeed.doAction(dojo.lang.mixin(this.bindArgs,_33c),this.entityId);
}
}else{
}
}
}
},isFormSubmitInProgress:function(_33e){
if(_33e!=undefined){
this.formSubmitInProgress=_33e;
}
return this.formSubmitInProgress;
}});
jetspeed.om.PortletCL=function(_33f,_340,_341){
this.portlet=_33f;
this.suppressGetActions=_340;
this.formbind=null;
if(_341!=null&&_341.submitFormBindObject!=null){
this.formbind=_341.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletCL.prototype={_loading:function(_342){
if(this.portlet==null){
return;
}
if(_342){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_RENDER);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_343,_344,_345,http){
var _347=null;
var _348=(_343?_343.indexOf("</JS_PORTLET_HEAD_ELEMENTS>"):-1);
if(_348!=-1){
_348+="</JS_PORTLET_HEAD_ELEMENTS>".length;
_347=_343.substring(0,_348);
_343=_343.substring(_348);
jetspeed.contributeHeadElements(dojo.dom.createDocumentFromText(_347).documentElement);
}
var _349=null;
if(http!=null){
try{
_349=http.getResponseHeader("JS_PORTLET_TITLE");
}
catch(ignore){
}
if(_349!=null){
_349=unescape(_349);
}
}
_345.setPortletContent(_343,_344,_349);
if(this.suppressGetActions==null||this.suppressGetActions==false){
jetspeed.getActionsForPortlet(_345.getId());
}else{
this._loading(false);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_34b,_34c,_34d){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletCL notifyFailure url: "+_34c+" type: "+type+jetspeed.formatError(_34b));
}};
jetspeed.om.PortletActionCL=function(_34e,_34f){
this.portlet=_34e;
this.formbind=null;
if(_34f!=null&&_34f.submitFormBindObject!=null){
this.formbind=_34f.submitFormBindObject;
}
this._loading(true);
};
jetspeed.om.PortletActionCL.prototype={_loading:function(_350){
if(this.portlet==null){
return;
}
if(_350){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_ACTION);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(_351,_352,_353,http){
var _355=jetspeed;
var _356=null;
var _357=false;
var _358=_355.portleturl.parseContentUrl(_351);
if(_358.operation==_355.portleturl.PORTLET_REQUEST_ACTION||_358.operation==_355.portleturl.PORTLET_REQUEST_RENDER){
if(_355.debug.doRenderDoAction){
dojo.debug("PortletActionCL "+_358.operation+"-url in response body: "+_351+"  url: "+_358.url+" entity-id: "+_358.portletEntityId);
}
_356=_358.url;
}else{
if(_355.debug.doRenderDoAction){
dojo.debug("PortletActionCL other-url in response body: "+_351);
}
_356=_351;
if(_356){
var _359=_356.indexOf(_355.url.basePortalUrl()+_355.url.path.PORTLET);
if(_359==-1){
_357=true;
window.location.href=_356;
_356=null;
}else{
if(_359>0){
this._loading(false);
dojo.raise("Cannot interpret portlet url in action response: "+_351);
_356=null;
}
}
}
}
if(_356!=null&&!_355.noActionRender){
if(_355.debug.doRenderDoAction){
dojo.debug("PortletActionCL starting portlet-renderer with renderUrl="+_356);
}
var _35a=new jetspeed.PortletRenderer(false,false,false,_356,true);
_35a.renderAll();
}else{
this._loading(false);
}
if(!_357&&this.portlet){
_355.getActionsForPortlet(this.portlet.entityId);
}
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
},notifyFailure:function(type,_35c,_35d,_35e){
this._loading(false);
if(this.formbind!=null){
this.formbind.isFormSubmitInProgress(false);
}
dojo.raise("PortletActionCL notifyFailure type: "+type+jetspeed.formatError(_35c));
}};
jetspeed.om.MenuOption=function(){
};
dojo.lang.extend(jetspeed.om.MenuOption,{navigateTo:function(){
if(this.isLeaf()){
var _35f=this.getUrl();
if(_35f){
var _360=jetspeed;
if(!_360.prefs.ajaxPageNavigation||_360.url.urlStartsWithHttp(_35f)){
_360.pageNavigate(_35f,this.getTarget());
}else{
_360.updatePage(_35f);
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
jetspeed.om.Menu=function(_361,_362){
this._is_parsed=false;
this.name=_361;
this.type=_362;
};
dojo.inherits(jetspeed.om.Menu,jetspeed.om.MenuOption);
dojo.lang.extend(jetspeed.om.Menu,{setParsed:function(){
this._is_parsed=true;
},isParsed:function(){
return this._is_parsed;
},getName:function(){
return this.name;
},addOption:function(_363){
if(!_363){
return;
}
if(!this.options){
this.options=new Array();
}
this.options.push(_363);
},getOptions:function(){
var tAry=new Array();
return (this.options?tAry.concat(this.options):tAry);
},getOptionByIndex:function(_365){
if(!this.hasOptions()){
return null;
}
if(_365==0||_365>0){
if(_365>=this.options.length){
dojo.raise("Menu.getOptionByIndex index out of bounds");
}else{
return this.options[_365];
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
var _367=this.options[i];
if(_367 instanceof jetspeed.om.Menu){
return true;
}
}
return false;
}});
jetspeed.om.MenuApiCL=function(){
};
dojo.lang.extend(jetspeed.om.MenuApiCL,{notifySuccess:function(data,_369,_36a){
var _36b=this.parseMenu(data,_36a.menuName,_36a.menuType);
_36a.page.putMenu(_36b);
},notifyFailure:function(type,_36d,_36e,_36f){
this.notifyCount++;
dojo.raise("MenuApiCL error ["+_36f.toString()+"] url: "+_36e+" type: "+type+jetspeed.formatError(_36d));
},parseMenu:function(node,_371,_372){
var menu=null;
var _374=node.getElementsByTagName("js");
if(!_374||_374.length>1){
dojo.raise("Expected one <js> in menu xml");
}
var _375=_374[0].childNodes;
for(var i=0;i<_375.length;i++){
var _377=_375[i];
if(_377.nodeType!=1){
continue;
}
var _378=_377.nodeName;
if(_378=="menu"){
if(menu!=null){
dojo.raise("Expected one root <menu> in menu xml");
}
menu=this.parseMenuObject(_377,new jetspeed.om.Menu());
}
}
if(menu!=null){
if(menu.name==null){
menu.name==_371;
}
if(menu.type==null){
menu.type=_372;
}
}
return menu;
},parseMenuObject:function(node,mObj){
var _37b=null;
var _37c=node.childNodes;
for(var i=0;i<_37c.length;i++){
var _37e=_37c[i];
if(_37e.nodeType!=1){
continue;
}
var _37f=_37e.nodeName;
if(_37f=="menu"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <menu>");
}else{
mObj.addOption(this.parseMenuObject(_37e,new jetspeed.om.Menu()));
}
}else{
if(_37f=="option"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <option>");
}else{
mObj.addOption(this.parseMenuObject(_37e,new jetspeed.om.MenuOption()));
}
}else{
if(_37f=="separator"){
if(mObj.isLeaf()){
dojo.raise("Unexpected nested <separator>");
}else{
mObj.addOption(this.parseMenuObject(_37e,new jetspeed.om.MenuOptionSeparator()));
}
}else{
if(_37f){
mObj[_37f]=((_37e&&_37e.firstChild)?_37e.firstChild.nodeValue:null);
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
jetspeed.om.MenusApiCL=function(_380,_381,_382){
this.includeMenuDefs=_380;
this.isPageUpdate=_381;
this.initEditModeConf=_382;
};
dojo.inherits(jetspeed.om.MenusApiCL,jetspeed.om.MenuApiCL);
dojo.lang.extend(jetspeed.om.MenusApiCL,{notifySuccess:function(data,_384,_385){
var _386=this.getMenuDefs(data,_384,_385);
for(var i=0;i<_386.length;i++){
var mObj=_386[i];
_385.page.putMenu(mObj);
}
this.notifyFinished(_385);
},getMenuDefs:function(data,_38a,_38b){
var _38c=[];
var _38d=data.getElementsByTagName("menu");
for(var i=0;i<_38d.length;i++){
var _38f=_38d[i].getAttribute("type");
if(this.includeMenuDefs){
_38c.push(this.parseMenuObject(_38d[i],new jetspeed.om.Menu(null,_38f)));
}else{
var _390=_38d[i].firstChild.nodeValue;
_38c.push(new jetspeed.om.Menu(_390,_38f));
}
}
return _38c;
},notifyFailure:function(type,_392,_393,_394){
dojo.raise("MenusApiCL error ["+_394.toString()+"] url: "+_393+" type: "+type+jetspeed.formatError(_392));
},notifyFinished:function(_395){
var _396=jetspeed;
if(this.includeMenuDefs){
_396.notifyRetrieveAllMenusFinished(this.isPageUpdate,this.initEditModeConf);
}
_396.page.loadPostRetrieveMenus(this.isPageUpdate,this.initEditModeConf);
if(djConfig.isDebug&&_396.debug.profile){
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
jetspeed.om.PortletChangeActionCL=function(_397){
this.portletEntityId=_397;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletChangeActionCL,{notifySuccess:function(data,_399,_39a){
if(jetspeed.url.checkAjaxApiResponse(_399,data,null,true,"portlet-change-action")){
jetspeed.getActionsForPortlet(this.portletEntityId);
}else{
this._loading(false);
}
},_loading:function(_39b){
var _39c=jetspeed.page.getPortlet(this.portletEntityId);
if(_39c){
if(_39b){
_39c.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_39c.loadingIndicatorHide();
}
}
},notifyFailure:function(type,_39e,_39f,_3a0){
this._loading(false);
dojo.raise("PortletChangeActionCL error ["+_3a0.toString()+"] url: "+_39f+" type: "+type+jetspeed.formatError(_39e));
}});
jetspeed.om.PageChangeActionCL=function(_3a1){
this.pageActionUrl=_3a1;
};
dojo.lang.extend(jetspeed.om.PageChangeActionCL,{notifySuccess:function(data,_3a3,_3a4){
if(jetspeed.url.checkAjaxApiResponse(_3a3,data,null,true,"page-change-action")){
if(this.pageActionUrl!=null&&this.pageActionUrl.length>0){
jetspeed.pageNavigate(this.pageActionUrl);
}
}
},notifyFailure:function(type,_3a6,_3a7,_3a8){
dojo.raise("PageChangeActionCL error ["+_3a8.toString()+"] url: "+_3a7+" type: "+type+jetspeed.formatError(_3a6));
}});
jetspeed.om.UserInfoCL=function(){
};
dojo.lang.extend(jetspeed.om.UserInfoCL,{notifySuccess:function(data,_3aa,_3ab){
var _3ac=jetspeed;
if(_3ac.url.checkAjaxApiResponse(_3aa,data,null,false,"user-info")){
var _3ad=data.getElementsByTagName("js");
if(_3ad&&_3ad.length==1){
var root=_3ad[0];
var un=_3ac.page._parsePSMLChildOrAttr(root,"username");
var rMap={};
var _3b1=root.getElementsByTagName("role");
if(_3b1!=null){
for(var i=0;i<_3b1.length;i++){
var role=(_3b1[i].firstChild?_3b1[i].firstChild.nodeValue:null);
if(role){
rMap[role]=role;
}
}
}
_3ac.page._setU({un:un,r:rMap});
}
}
},notifyFailure:function(type,_3b5,_3b6,_3b7){
dojo.raise("UserInfoCL error ["+_3b7.toString()+"] url: "+_3b6+" type: "+type+jetspeed.formatError(_3b5));
}});
jetspeed.om.PortletActionsCL=function(_3b8){
this.portletEntityIds=_3b8;
this._loading(true);
};
dojo.lang.extend(jetspeed.om.PortletActionsCL,{_loading:function(_3b9){
if(this.portletEntityIds==null||this.portletEntityIds.length==0){
return;
}
for(var i=0;i<this.portletEntityIds.length;i++){
var _3bb=jetspeed.page.getPortlet(this.portletEntityIds[i]);
if(_3bb){
if(_3b9){
_3bb.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
_3bb.loadingIndicatorHide();
}
}
}
},notifySuccess:function(data,_3bd,_3be){
var _3bf=jetspeed;
this._loading(false);
if(_3bf.url.checkAjaxApiResponse(_3bd,data,null,true,"portlet-actions")){
this.processPortletActionsResponse(data,_3bf.page);
}
},processPortletActionsResponse:function(node,_3c1){
var _3c2=this.parsePortletActionsResponse(node,_3c1);
for(var i=0;i<_3c2.length;i++){
var _3c4=_3c2[i];
var _3c5=_3c4.id;
var _3c6=_3c1.getPortlet(_3c5);
if(_3c6!=null){
_3c6.updateActions(_3c4.actions,_3c4.currentActionState,_3c4.currentActionMode);
}
}
},parsePortletActionsResponse:function(node,_3c8){
var _3c9=new Array();
var _3ca=node.getElementsByTagName("js");
if(!_3ca||_3ca.length>1){
dojo.raise("Expected one <js> in portlet selector xml");
return _3c9;
}
var _3cb=_3ca[0].childNodes;
for(var i=0;i<_3cb.length;i++){
var _3cd=_3cb[i];
if(_3cd.nodeType!=1){
continue;
}
var _3ce=_3cd.nodeName;
if(_3ce=="portlets"){
var _3cf=_3cd;
var _3d0=_3cf.childNodes;
for(var pI=0;pI<_3d0.length;pI++){
var _3d2=_3d0[pI];
if(_3d2.nodeType!=1){
continue;
}
var _3d3=_3d2.nodeName;
if(_3d3=="portlet"){
var _3d4=this.parsePortletElement(_3d2,_3c8);
if(_3d4!=null){
_3c9.push(_3d4);
}
}
}
}
}
return _3c9;
},parsePortletElement:function(node,_3d6){
var _3d7=node.getAttribute("id");
if(_3d7!=null){
var _3d8=_3d6._parsePSMLActions(node,null);
var _3d9=_3d6._parsePSMLChildOrAttr(node,"state");
var _3da=_3d6._parsePSMLChildOrAttr(node,"mode");
return {id:_3d7,actions:_3d8,currentActionState:_3d9,currentActionMode:_3da};
}
return null;
},notifyFailure:function(type,_3dc,_3dd,_3de){
this._loading(false);
dojo.raise("PortletActionsCL error ["+_3de.toString()+"] url: "+_3dd+" type: "+type+jetspeed.formatError(_3dc));
}});
jetspeed.om.MoveApiCL=function(_3df,_3e0){
this.portlet=_3df;
this.changedState=_3e0;
this._loading(true);
};
jetspeed.om.MoveApiCL.prototype={_loading:function(_3e1){
if(this.portlet==null){
return;
}
if(_3e1){
this.portlet.loadingIndicatorShow(jetspeed.id.ACT_LOAD_UPDATE);
}else{
this.portlet.loadingIndicatorHide();
}
},notifySuccess:function(data,_3e3,_3e4){
var _3e5=jetspeed;
this._loading(false);
dojo.lang.mixin(_3e4.portlet.lastSavedWindowState,this.changedState);
var _3e6=true;
if(djConfig.isDebug&&_3e5.debug.submitWinState){
_3e6=true;
}
var _3e7=_3e5.url.checkAjaxApiResponse(_3e3,data,["refresh"],_3e6,("move-portlet ["+_3e4.portlet.entityId+"]"),_3e5.debug.submitWinState);
if(_3e7=="refresh"){
var _3e8=_3e5.page.getPageUrl();
if(!_3e5.prefs.ajaxPageNavigation){
_3e5.pageNavigate(_3e8,null,true);
}else{
_3e5.updatePage(_3e8,false,true);
}
}
},notifyFailure:function(type,_3ea,_3eb,_3ec){
this._loading(false);
dojo.debug("submitWinState error ["+_3ec.entityId+"] url: "+_3eb+" type: "+type+jetspeed.formatError(_3ea));
}};
jetspeed.postload_addEventListener=function(node,_3ee,fnc,_3f0){
if((_3ee=="load"||_3ee=="DOMContentLoaded"||_3ee=="domready")&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.addEventListener(_3ee,fnc,_3f0);
}
};
jetspeed.postload_attachEvent=function(node,_3f2,fnc){
if(_3f2=="onload"&&(node==window||node==document||node==document.body)){
fnc();
}else{
node.attachEvent(_3f2,fnc);
}
};
jetspeed.postload_docwrite=function(_3f4){
if(!_3f4){
return;
}
_3f4=_3f4.replace(/^\s+|\s+$/g,"");
var _3f5=/^<script\b([^>]*)>.*?<\/script>/i;
var _3f6=_3f5.exec(_3f4);
if(_3f6){
_3f4=null;
var _3f7=_3f6[1];
if(_3f7){
var _3f8=/\bid\s*=\s*([^\s]+)/i;
var _3f9=_3f8.exec(_3f7);
if(_3f9){
var _3fa=_3f9[1];
_3f4="<img id="+_3fa+" src=\""+jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/desktop/pixel.gif"+"\"/>";
}
}
}
var tn=null;
if(_3f4){
var _3fc=dojo;
tn=_3fc.doc().createElement("div");
tn.style.visibility="hidden";
_3fc.body().appendChild(tn);
tn.innerHTML=_3f4;
tn.style.display="none";
}
return tn;
};
jetspeed.setdoclocation=function(_3fd,_3fe,_3ff){
if(_3fd==document||_3fd==window){
if(_3ff&&_3ff.length>0){
var _400=jetspeed.portleturl;
if(_3ff.indexOf(_400.DESKTOP_ACTION_PREFIX_URL)!=0&&_3ff.indexOf(_400.DESKTOP_RENDER_PREFIX_URL)!=0){
_3fd.location=_3ff;
}
}
}else{
if(_3fd!=null){
var _401=_3fe.indexOf(".");
if(_401==-1){
_3fd[_3fe]=_3ff;
}else{
var _402=_3fe.substring(0,_401);
var _403=_3fd[_402];
if(_403){
var _404=_3fe.substring(_401+1);
if(_404){
_403[_404]=_3ff;
}
}
}
}
}
};
jetspeed.addDummyScriptToHead=function(src){
var _406=document.createElement("script");
_406.setAttribute("type","text/plain");
_406.setAttribute("language","ignore");
_406.setAttribute("src",src);
document.getElementsByTagName("head")[0].appendChild(_406);
return _406;
};
jetspeed.containsElement=function(_407,_408,_409,_40a){
if(!_407||!_408||!_409){
return false;
}
if(!_40a){
_40a=document;
}
var _40b=_40a.getElementsByTagName(_407);
if(!_40b){
return false;
}
for(var i=0;i<_40b.length;++i){
var _40d=_40b[i].getAttribute(_408);
if(_40d==_409){
return true;
}
}
return false;
};
jetspeed.ui={initCssObj:function(){
var _40e=["display: ","block",";"," cursor: ","default",";"," width: ","","",";","","",""];
var _40f=_40e.concat([" height: ","","",";"]);
var _410=["","","","","","","width: ","","",";","","",""," height: ","","",";"];
var _411=_40f.concat([" overflow-y: ","",";"," overflow-x: ","hidden",";"]);
var _412=_411.concat([" position: ","relative",";"," top: ","auto","",";"," left: ","auto","",";"," z-index: ","",";"]);
jetspeed.css={cssBase:_40e,cssHeight:_40f,cssWidthHeight:_410,cssOverflow:_411,cssPosition:_412,cssDis:1,cssCur:4,cssW:7,cssWU:8,cssNoSelNm:10,cssNoSel:11,cssNoSelEnd:12,cssH:14,cssHU:15,cssOy:18,cssOx:21,cssPos:24,cssT:27,cssTU:28,cssL:31,cssLU:32,cssZIndex:35};
},getPWinAndColChildren:function(_413,_414,_415,_416,_417,_418){
var djH=dojo.html;
var jsId=jetspeed.id;
var _41b=null;
var _41c=-1;
var _41d=-1;
var _41e=-1;
if(_413){
var _41f=_413.childNodes;
if(_41f){
_41e=_41f.length;
}
_41b=[];
if(_41e>0){
var _420="",_421="";
if(!_418){
_420=jsId.PWIN_CLASS;
}
if(_415){
_420+=((_420.length>0)?"|":"")+jsId.PWIN_GHOST_CLASS;
}
if(_416){
_420+=((_420.length>0)?"|":"")+jsId.COL_CLASS;
}
if(_417&&!_416){
_420+=((_420.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_416&&!_417){
_421=((_421.length>0)?"|":"")+jsId.COL_LAYOUTHEADER_CLASS;
}
if(_420.length>0){
var _422=new RegExp("(^|\\s+)("+_420+")(\\s+|$)");
var _423=null;
if(_421.length>0){
_423=new RegExp("(^|\\s+)("+_421+")(\\s+|$)");
}
var _424,_425,_426;
for(var i=0;i<_41e;i++){
_424=_41f[i];
_425=false;
_426=djH.getClass(_424);
if(_422.test(_426)&&(_423==null||!_423.test(_426))){
_41b.push(_424);
_425=true;
}
if(_414&&_424==_414){
if(!_425){
_41b.push(_424);
}
_41c=i;
_41d=_41b.length-1;
}
}
}
}
}
return {matchingNodes:_41b,totalNodes:_41e,matchNodeIndex:_41c,matchNodeIndexInMatchingNodes:_41d};
},getPWinsFromNodes:function(_428){
var _429=jetspeed.page;
var _42a=null;
if(_428){
_42a=new Array();
for(var i=0;i<_428.length;i++){
var _42c=_429.getPWin(_428[i].id);
if(_42c){
_42a.push(_42c);
}
}
}
return _42a;
},createPortletWindow:function(_42d,_42e,_42f){
var _430=false;
if(djConfig.isDebug&&_42f.debug.profile){
_430=true;
dojo.profile.start("createPortletWindow");
}
var _431=(_42e!=null);
var _432=false;
var _433=null;
if(_431&&_42e<_42f.page.columns.length&&_42e>=0){
_433=_42f.page.columns[_42e].domNode;
}
if(_433==null){
_432=true;
_433=document.getElementById(_42f.id.DESKTOP);
}
if(_433==null){
return;
}
var _434={};
if(_42d.isPortlet){
_434.portlet=_42d;
if(_42f.prefs.printModeOnly!=null){
_434.printMode=true;
}
if(_432){
_42d.properties[_42f.id.PP_WINDOW_POSITION_STATIC]=false;
}
}else{
var pwP=_42f.widget.PortletWindow.prototype.altInitParamsDef(_434,_42d);
if(_432){
pwP.altInitParams[_42f.id.PP_WINDOW_POSITION_STATIC]=false;
}
}
var _436=new _42f.widget.PortletWindow();
_436.build(_434,_433);
if(_430){
dojo.profile.end("createPortletWindow");
}
return _436;
},getLayoutExtents:function(node,_438,_439,_43a){
if(!_438){
_438=_439.gcs(node);
}
var pad=_439._getPadExtents(node,_438);
var _43c=_439._getBorderExtents(node,_438);
var _43d={l:(pad.l+_43c.l),t:(pad.t+_43c.t),w:(pad.w+_43c.w),h:(pad.h+_43c.h)};
var _43e=_439._getMarginExtents(node,_438,_43a);
return {bE:_43c,pE:pad,pbE:_43d,mE:_43e,lessW:(_43d.w+_43e.w),lessH:(_43d.h+_43e.h)};
},getContentBoxSize:function(node,_440){
var w=node.clientWidth,h,_443;
if(!w){
w=node.offsetWidth,h=node.offsetHeight;
_443=_440.pbE;
}else{
h=node.clientHeight;
_443=_440.pE;
}
return {w:(w-_443.w),h:(h-_443.h)};
},getMarginBoxSize:function(node,_445){
return {w:(node.offsetWidth+_445.mE.w),h:(node.offsetHeight+_445.mE.h)};
},getMarginBox:function(node,_447,_448,_449){
var l=node.offsetLeft-_447.mE.l,t=node.offsetTop-_447.mE.t;
if(_448&&_449.UAope){
l-=_448.bE.l;
t-=_448.bE.t;
}
return {l:l,t:t,w:(node.offsetWidth+_447.mE.w),h:(node.offsetHeight+_447.mE.h)};
},setMarginBox:function(node,_44d,_44e,_44f,_450,_451,_452,_453){
var pb=_451.pbE,mb=_451.mE;
if(_44f!=null&&_44f>=0){
_44f=Math.max(_44f-pb.w-mb.w,0);
}
if(_450!=null&&_450>=0){
_450=Math.max(_450-pb.h-mb.h,0);
}
_453._setBox(node,_44d,_44e,_44f,_450);
},evtConnect:function(_456,_457,_458,_459,_45a,_45b,rate){
if(!rate){
rate=0;
}
var _45d={adviceType:_456,srcObj:_457,srcFunc:_458,adviceObj:_459,adviceFunc:_45a,rate:rate};
if(_45b==null){
_45b=dojo.event;
}
_45b.connect(_45d);
return _45d;
},evtDisconnect:function(_45e,_45f,_460,_461,_462,_463){
if(_463==null){
_463=dojo.event;
}
_463.disconnect({adviceType:_45e,srcObj:_45f,srcFunc:_460,adviceObj:_461,adviceFunc:_462});
},evtDisconnectWObj:function(_464,_465){
if(_465==null){
_465=dojo.event;
}
_465.disconnect(_464);
},evtDisconnectWObjAry:function(_466,_467){
if(_466&&_466.length>0){
if(_467==null){
_467=dojo.event;
}
for(var i=0;i<_466.length;i++){
_467.disconnect(_466[i]);
}
}
},_popupMenuWidgets:[],isWindowActionMenuOpen:function(){
var _469=false;
var _46a=this._popupMenuWidgets;
for(var i=0;i<_46a.length;i++){
var _46c=_46a[i];
if(_46c&&_46c.isShowingNow){
_469=true;
break;
}
}
return _469;
},addPopupMenuWidget:function(_46d){
if(_46d){
this._popupMenuWidgets.push(_46d);
}
},removePopupMenuWidget:function(_46e){
if(!_46e){
return;
}
var _46f=this._popupMenuWidgets;
for(var i=0;i<_46f.length;i++){
if(_46f[i]===_46e){
_46f[i]=null;
}
}
},updateChildColInfo:function(_471,_472,_473,_474,_475,_476){
var _477=jetspeed;
var _478=dojo;
var _479=_478.byId(_477.id.COLUMNS);
if(!_479){
return;
}
var _47a=false;
if(_471!=null){
var _47b=_471.getAttribute("columnindex");
var _47c=_471.getAttribute("layoutid");
var _47d=(_47b==null?-1:new Number(_47b));
if(_47d>=0&&_47c!=null&&_47c.length>0){
_47a=true;
}
}
var _47e=_477.page.columns||[];
var _47f=new Array(_47e.length);
var _480=_477.page.layoutInfo;
var fnc=_477.ui._updateChildColInfo;
fnc(fnc,_479,1,_47f,_47e,_472,_473,_474,_480,_480.columns,_480.desktop,_471,_47a,_475,_476,_478,_477);
return _47f;
},_updateChildColInfo:function(fnc,_483,_484,_485,_486,_487,_488,_489,_48a,_48b,_48c,_48d,_48e,_48f,_490,_491,_492){
var _493=_483.childNodes;
var _494=(_493?_493.length:0);
if(_494==0){
return;
}
var _495=_491.html.getAbsolutePosition(_483,true);
var _496=_492.ui.getMarginBox(_483,_48b,_48c,_492);
var _497=_48a.column;
var _498,col,_49a,_49b,_49c,_49d,_49e,_49f,_4a0,_4a1,_4a2,_4a3,_4a4,_4a5;
var _4a6=null,_4a7=(_48f!=null?(_48f+1):null),_4a8,_4a9;
var _4aa=null;
for(var i=0;i<_494;i++){
_498=_493[i];
_49b=_498.getAttribute("columnindex");
_49c=(_49b==null?-1:new Number(_49b));
if(_49c>=0){
col=_486[_49c];
_4a5=true;
_49a=(col?col.layoutActionsDisabled:false);
_49d=_498.getAttribute("layoutid");
_49e=(_49d!=null&&_49d.length>0);
_4a8=_4a7;
_4a9=null;
_49a=((!_489)&&_49a);
var _4ac=_484;
var _4ad=(_498===_48d);
if(_49e){
if(_4aa==null){
_4aa=_484;
}
if(col){
col._updateLayoutDepth(_484);
}
_4ac++;
}else{
if(!_4ad){
if(col&&(!_49a||_489)&&(_487==null||_487[_49c]==null)&&(_488==null||_484<=_488)){
_49f=_492.ui.getMarginBox(_498,_497,_48b,_492);
if(_4a6==null){
_4a6=_49f.t-_496.t;
_4a4=_496.h-_4a6;
}
_4a0=_495.left+(_49f.l-_496.l);
_4a1=_495.top+_4a6;
_4a2=_49f.h;
if(_4a2<_4a4){
_4a2=_4a4;
}
if(_4a2<40){
_4a2=40;
}
var _4ae=_498.childNodes;
_4a3={left:_4a0,top:_4a1,right:(_4a0+_49f.w),bottom:(_4a1+_4a2),childCount:(_4ae?_4ae.length:0),pageColIndex:_49c};
_4a3.height=_4a3.bottom-_4a3.top;
_4a3.width=_4a3.right-_4a3.left;
_4a3.yhalf=_4a3.top+(_4a3.height/2);
_485[_49c]=_4a3;
_4a5=(_4a3.childCount>0);
if(_4a5){
_498.style.height="";
}else{
_498.style.height="1px";
}
if(_48f!=null){
_4a9=(_492.debugDims(_4a3,true)+" yhalf="+_4a3.yhalf+(_49f.h!=_4a2?(" hreal="+_49f.h):"")+" childC="+_4a3.childCount+"}");
}
}
}
}
if(_48f!=null){
if(_49e){
_4a8=_4a7+1;
}
if(_4a9==null){
_4a9="---";
}
_491.hostenv.println(_491.string.repeat(_490,_48f)+"["+((_49c<10?" ":"")+_49b)+"] "+_4a9);
}
if(_4a5){
var _4af=fnc(fnc,_498,_4ac,_485,_486,_487,_488,_489,_48a,(_49e?_48a.columnLayoutHeader:_497),_48b,_48d,_48e,_4a8,_490,_491,_492);
if(_4af!=null&&(_4aa==null||_4af>_4aa)){
_4aa=_4af;
}
}
}
}
_49b=_483.getAttribute("columnindex");
_49d=_483.getAttribute("layoutid");
_49c=(_49b==null?-1:new Number(_49b));
if(_49c>=0&&_49d!=null&&_49d.length>0){
col=_486[_49c];
col._updateLayoutChildDepth(_4aa);
}
return _4aa;
},getScrollbar:function(_4b0){
var _4b1=_4b0.ui.scrollWidth;
if(_4b1==null){
var _4b2=document.createElement("div");
var _4b3="width: 100px; height: 100px; top: -300px; left: 0px; overflow: scroll; position: absolute";
_4b2.style.cssText=_4b3;
var test=document.createElement("div");
_4b2.style.cssText="width: 400px; height: 400px";
_4b2.appendChild(test);
var _4b5=_4b0.docBody;
_4b5.appendChild(_4b2);
_4b1=_4b2.offsetWidth-_4b2.clientWidth;
_4b5.removeChild(_4b2);
_4b2.removeChild(test);
_4b2=test=null;
_4b0.ui.scrollWidth=_4b1;
}
return _4b1;
}};
jetspeed.ui.windowResizeMgr={checkTime:500,timerId:0,resizing:false,init:function(win,_4b7){
this.oldXY=this.getWinDims(win,win.document,_4b7);
},getWinDims:function(win,doc,_4ba){
var b,x,y,sx,sy,v;
x=y=sx=sy=0;
if(win.innerWidth&&win.innerHeight){
x=win.innerWidth;
v=_4ba.offsetWidth;
if(v&&(1<v)&&!(x<v)){
x=v-1;
}
y=win.innerHeight;
sx=win.pageXOffset||0;
sy=win.pageYOffset||0;
}else{
b=doc.documentElement.clientWidth?doc.documentElement:_4ba;
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
var _4c1=jetspeed;
var _4c2=this.getWinDims(window,window.document,_4c1.docBody);
this.timerId=0;
if((_4c2.x!=this.oldXY.x)||(_4c2.y!=this.oldXY.y)){
this.oldXY=_4c2;
if(_4c1.page){
if(!this.resizing){
try{
this.resizing=true;
_4c1.page.onBrowserWindowResize();
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
var _4c3=jetspeed;
var _4c4=null;
var _4c5=false;
var ua=function(){
var _4c7=[0,0,0];
var d=null;
if(typeof navigator.plugins!="undefined"&&typeof navigator.plugins["Shockwave Flash"]=="object"){
d=navigator.plugins["Shockwave Flash"].description;
if(d){
d=d.replace(/^.*\s+(\S+\s+\S+$)/,"$1");
_4c7[0]=parseInt(d.replace(/^(.*)\..*$/,"$1"),10);
_4c7[1]=parseInt(d.replace(/^.*\.(.*)\s.*$/,"$1"),10);
_4c7[2]=/r/.test(d)?parseInt(d.replace(/^.*r(.*)$/,"$1"),10):0;
}
}else{
if(typeof window.ActiveXObject!="undefined"){
var a=null;
var _4ca=false;
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.7");
}
catch(e){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash.6");
_4c7=[6,0,21];
a.AllowScriptAccess="always";
}
catch(e){
if(_4c7[0]==6){
_4ca=true;
}
}
if(!_4ca){
try{
a=new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
}
catch(e){
}
}
}
if(!_4ca&&typeof a=="object"){
try{
d=a.GetVariable("$version");
if(d){
d=d.split(" ")[1].split(",");
_4c7=[parseInt(d[0],10),parseInt(d[1],10),parseInt(d[2],10)];
}
}
catch(e){
}
}
}
}
var djR=dojo.render;
var djRH=djR.html;
return {w3cdom:true,playerVersion:_4c7,ie:djRH.ie,win:djR.os.win,mac:djR.os.mac};
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
function showExpressInstall(_4d1){
_4c5=true;
var obj=document.getElementById(_4d1.id);
if(obj){
var ac=document.getElementById(_4d1.altContentId);
if(ac){
_4c4=ac;
}
var w=_4d1.width?_4d1.width:(obj.getAttribute("width")?obj.getAttribute("width"):0);
if(parseInt(w,10)<310){
w="310";
}
var h=_4d1.height?_4d1.height:(obj.getAttribute("height")?obj.getAttribute("height"):0);
if(parseInt(h,10)<137){
h="137";
}
var pt=ua.ie&&ua.win?"ActiveX":"PlugIn";
var dt=document.title;
var fv="MMredirectURL="+window.location+"&MMplayerType="+pt+"&MMdoctitle="+dt;
var el=obj;
createSWF({data:_4d1.expressInstall,id:"SWFObjectExprInst",width:w,height:h},{flashvars:fv},el);
}
};
function createSWF(_4da,_4db,el){
_4db.wmode="transparent";
if(ua.ie&&ua.win){
var att="";
for(var i in _4da){
if(typeof _4da[i]=="string"){
if(i=="data"){
_4db.movie=_4da[i];
}else{
if(i.toLowerCase()=="styleclass"){
att+=" class=\""+_4da[i]+"\"";
}else{
if(i!="classid"){
att+=" "+i+"=\""+_4da[i]+"\"";
}
}
}
}
}
var par="";
for(var j in _4db){
if(typeof _4db[j]=="string"){
par+="<param name=\""+j+"\" value=\""+_4db[j]+"\" />";
}
}
el.outerHTML="<object classid=\"clsid:D27CDB6E-AE6D-11cf-96B8-444553540000\""+att+">"+par+"</object>";
fixObjectLeaks();
}else{
var o=document.createElement("object");
o.setAttribute("type","application/x-shockwave-flash");
for(var m in _4da){
if(typeof _4da[m]=="string"){
if(m.toLowerCase()=="styleclass"){
o.setAttribute("class",_4da[m]);
}else{
if(m!="classid"){
o.setAttribute(m,_4da[m]);
}
}
}
}
for(var n in _4db){
if(typeof _4db[n]=="string"&&n!="movie"){
createObjParam(o,n,_4db[n]);
}
}
el.parentNode.replaceChild(o,el);
}
};
function createObjParam(el,_4e5,_4e6){
var p=document.createElement("param");
p.setAttribute("name",_4e5);
p.setAttribute("value",_4e6);
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
return {embedSWF:function(_4ee,_4ef,_4f0,_4f1,_4f2,_4f3,_4f4,_4f5,_4f6,_4f7){
if(!ua.w3cdom||!_4ee||!_4ef||!_4f0||!_4f1||!_4f2){
return;
}
if(hasPlayerVersion(_4f2.split("."))){
var _4f8=(_4f6?_4f6.id:null);
createCSS("#"+_4ef,"visibility:hidden");
var att=(typeof _4f6=="object")?_4f6:{};
att.data=_4ee;
att.width=_4f0;
att.height=_4f1;
var par=(typeof _4f5=="object")?_4f5:{};
if(typeof _4f4=="object"){
for(var i in _4f4){
if(typeof _4f4[i]=="string"){
if(typeof par.flashvars!="undefined"){
par.flashvars+="&"+i+"="+_4f4[i];
}else{
par.flashvars=i+"="+_4f4[i];
}
}
}
}
createSWF(att,par,document.getElementById(_4ef));
createCSS("#"+_4ef,"visibility:visible");
if(_4f8){
var _4fc=_4c3.page.swfInfo;
if(_4fc==null){
_4fc=_4c3.page.swfInfo={};
}
_4fc[_4f8]=_4f7;
}
}else{
if(_4f3&&!_4c5&&hasPlayerVersion([6,0,65])&&(ua.win||ua.mac)){
createCSS("#"+_4ef,"visibility:hidden");
var _4fd={};
_4fd.id=_4fd.altContentId=_4ef;
_4fd.width=_4f0;
_4fd.height=_4f1;
_4fd.expressInstall=_4f3;
showExpressInstall(_4fd);
createCSS("#"+_4ef,"visibility:visible");
}
}
}};
}();

