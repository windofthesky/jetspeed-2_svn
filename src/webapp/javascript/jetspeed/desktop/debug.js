dojo.provide("jetspeed.desktop.debug");
dojo.require("jetspeed.debug");
dojo.require("dojo.profile");
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.om){
jetspeed.om={};
}
jetspeed.debug={pageLoad:false,retrievePsml:false,setPortletContent:false,doRenderDoAction:false,postParseAnnotateHtml:false,postParseAnnotateHtmlDisableAnchors:false,confirmOnSubmit:false,createWindow:false,initWinState:false,submitWinState:false,ajaxPageNav:false,dragWindow:false,profile:true,windowDecorationRandom:false,debugInPortletWindow:true,debugContainerId:(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId)};
jetspeed.debugWindowLoad=function(){
var _1=jetspeed;
var _2=_1.id;
var _3=dojo;
if(djConfig.isDebug&&_1.debug.debugInPortletWindow&&_3.byId(_1.debug.debugContainerId)==null){
var _4=_1.debugWindowReadCookie(true);
var wP={};
var _6=_2.PW_ID_PREFIX+_2.DEBUG_WINDOW_TAG;
wP[_2.PP_WINDOW_POSITION_STATIC]=false;
wP[_2.PP_WINDOW_HEIGHT_TO_FIT]=false;
wP[_2.PP_WINDOW_DECORATION]=_1.prefs.windowDecoration;
wP[_2.PP_WINDOW_TITLE]="Dojo Debug";
wP[_2.PP_WINDOW_ICON]="text-x-script.png";
wP[_2.PP_WIDGET_ID]=_6;
wP[_2.PP_WIDTH]=_4.width;
wP[_2.PP_HEIGHT]=_4.height;
wP[_2.PP_LEFT]=_4.left;
wP[_2.PP_TOP]=_4.top;
wP[_2.PP_EXCLUDE_PCONTENT]=false;
wP[_2.PP_CONTENT_RETRIEVER]=new _1.om.DojoDebugContentRetriever();
wP[_2.PP_WINDOW_STATE]=_4.windowState;
var _7=_1.widget.PortletWindow.prototype.altInitParamsDef(null,wP);
_1.ui.createPortletWindow(_7,null,_1);
_7.retrieveContent(null,null);
var _8=_1.page.getPWin(_6);
_3.event.connect("after",_3.hostenv,"println",_8,"contentChanged");
_3.event.connect(_8,"windowActionButtonSync",_1,"debugWindowSave");
_3.event.connect(_8,"endSizing",_1,"debugWindowSave");
_3.event.connect(_8,"endDragging",_1,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_9){
var _a={};
if(_9){
_a={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACT_MINIMIZE};
}
var _b=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_b!=null&&_b.length>0){
var _c=_b.split("|");
if(_c&&_c.length>=4){
_a.width=_c[0];
_a.height=_c[1];
_a.top=_c[2];
_a.left=_c[3];
if(_c.length>4&&_c[4]!=null&&_c[4].length>0){
_a.windowState=_c[4];
}
}
}
return _a;
};
jetspeed.debugWindowRestore=function(){
var _d=jetspeed.debugWindow();
if(!_d){
return;
}
_d.restoreWindow();
};
jetspeed.debugWindow=function(){
var _e=jetspeed.id.PW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return jetspeed.page.getPWin(_e);
};
jetspeed.debugWindowId=function(){
return jetspeed.id.PW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
};
jetspeed.debugWindowSave=function(){
var _f=jetspeed.debugWindow();
if(!_f){
return null;
}
if(!_f.posStatic){
var _10=_f.getCurWinStateForPersist(false);
var _11=_10.width;
var _12=_10.height;
var _13=_10.top;
var _14=_10.left;
if(_f.windowState==jetspeed.id.ACT_MINIMIZE){
var _15=_f.getDimsObj(_f.posStatic);
if(_15!=null){
if(_15.height!=null&&_15.height>0){
_12=_15.height;
}
}else{
var _16=jetspeed.debugWindowReadCookie(false);
if(_16.height!=null&&_16.height>0){
_12=_16.height;
}
}
}
var _17=_11+"|"+_12+"|"+_13+"|"+_14+"|"+_f.windowState;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_17,30,"/");
}
};
jetspeed.debugDumpForm=function(_18){
if(!_18){
return null;
}
var _19=_18.toString();
if(_18.name){
_19+=" name="+_18.name;
}
if(_18.id){
_19+=" id="+_18.id;
}
var _1a=dojo.io.encodeForm(_18);
_19+=" data="+_1a;
return _19;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_1b,_1c,_1d,_1e){
if(!_1b){
_1b={};
}
if(!this.initialized){
var _1f=jetspeed;
var _20="";
var _21=_1f.debug.debugContainerId;
var _22=_1f.debugWindow();
if(_1f.altDebugWindowContent){
_20=_1f.altDebugWindowContent();
}else{
_20+="<div id=\""+_21+"\"></div>";
}
if(_1c){
_1c.notifySuccess(_20,_1b.url,_1d);
}else{
if(_22){
_22.setPortletContent(_20,_1b.url);
}
}
this.initialized=true;
if(_22){
var _23="javascript: void(document.getElementById('"+_21+"').innerHTML='')";
var _24="";
for(var i=0;i<20;i++){
_24+="&nbsp;";
}
var _26=_22.title+_24+"<a href=\""+_23+"\"><span style=\"font-size: xx-small; font-weight: normal\">Clear</span></a>";
_22.tbTextNode.innerHTML=_26;
}
}
}};
jetspeed.debugDumpColWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _28=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_28.domNode).width);
}
};
jetspeed.debugDumpWindowsPerCol=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _2a=jetspeed.page.columns[i];
var _2b=jetspeed.ui.getPWinChildren(_2a.domNode,null);
var _2c=jetspeed.ui.getPWinsFromNodes(_2b.portletWindowNodes);
var _2d={dumpMsg:""};
if(_2c!=null){
dojo.lang.forEach(_2c,function(_2e){
_2d.dumpMsg=_2d.dumpMsg+(_2d.dumpMsg.length>0?", ":"")+_2e.portlet.entityId;
});
}
_2d.dumpMsg="column "+i+": "+_2d.dumpMsg;
dojo.debug(_2d.dumpMsg);
}
};
jetspeed.debugDumpWindows=function(){
var _2f=jetspeed.page.getPWins();
var _30="";
for(var i=0;i<_2f.length;i++){
if(i>0){
_30+=", ";
}
_30+=_2f[i].widgetId;
}
dojo.debug("PortletWindows: "+_30);
};
jetspeed.debugLayoutInfo=function(){
var _32=jetspeed.page;
var _33="";
var i=0;
for(var _35 in _32.layouts){
if(i>0){
_33+="\r\n";
}
_33+="layout["+_35+"]: "+jetspeed.printobj(_32.layouts[_35],true,true,true);
i++;
}
return _33;
};
jetspeed.debugColumnInfo=function(){
var _36=jetspeed.page;
var _37="";
for(var i=0;i<_36.columns.length;i++){
if(i>0){
_37+="\r\n";
}
_37+=_36.columns[i].toString();
}
return _37;
};
jetspeed.debugSavedWinState=function(){
return jetspeed.debugWinStateAll(true);
};
jetspeed.debugWinState=function(){
return jetspeed.debugWinStateAll(false);
};
jetspeed.debugPortletActions=function(){
var _39=jetspeed.page.getPortletArray();
var _3a="";
for(var i=0;i<_39.length;i++){
var _3c=_39[i];
if(i>0){
_3a+="\r\n";
}
_3a+="portlet ["+_3c.name+"] actions: {";
for(var _3d in _3c.actions){
_3a+=_3d+"={"+jetspeed.printobj(_3c.actions[_3d],true)+"} ";
}
_3a+="}";
}
return _3a;
};
jetspeed.debugWinStateAll=function(_3e){
var _3f=jetspeed.page.getPortletArray();
var _40="";
for(var i=0;i<_3f.length;i++){
var _42=_3f[i];
if(i>0){
_40+="\r\n";
}
var _43=null;
try{
if(_3e){
_43=_42.getSavedWinState();
}else{
_43=_42.getCurWinState();
}
}
catch(e){
}
_40+="["+_42.name+"] "+((_43==null)?"null":jetspeed.printobj(_43,true));
}
return _40;
};
if(jetspeed.debug.profile){
dojo.profile.clearItem=function(_44){
return (this._profiles[_44]={iters:0,total:0});
};
dojo.profile.debugItem=function(_45,_46){
var _47=this._profiles[_45];
if(_47==null){
return null;
}
if(_47.iters==0){
return [_45," not profiled."].join("");
}
var _48=[_45," took ",_47.total," msec for ",_47.iters," iteration"];
if(_47.iters>1){
_48.push("s (",(Math.round(_47.total/_47.iters*100)/100)," msec each)");
}
dojo.debug(_48.join(""));
if(_46){
this.clearItem(_45);
}
};
dojo.profile.debugAllItems=function(_49){
for(var x=0;x<this._pns.length;x++){
this.debugItem(this._pns[x],_49);
}
};
}

