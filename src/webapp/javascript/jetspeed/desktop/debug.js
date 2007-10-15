dojo.provide("jetspeed.desktop.debug");
dojo.require("jetspeed.debug");
dojo.require("dojo.profile");
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.om){
jetspeed.om={};
}
jetspeed.debug={pageLoad:false,retrievePsml:false,setPortletContent:false,doRenderDoAction:false,postParseAnnotateHtml:false,postParseAnnotateHtmlDisableAnchors:false,confirmOnSubmit:false,createWindow:false,initWinState:false,submitWinState:false,ajaxPageNav:false,dragWindow:false,dragWindowStart:false,profile:false,windowDecorationRandom:false,debugInPortletWindow:true,debugContainerId:(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId)};
jetspeed.debugAlert=function(_1){
if(_1){
alert(_1);
}
};
jetspeed.debugWindowLoad=function(){
var _2=jetspeed;
var _3=_2.id;
var _4=dojo;
if(djConfig.isDebug&&_2.debug.debugInPortletWindow&&_4.byId(_2.debug.debugContainerId)==null){
var _5=_2.debugWindowReadCookie(true);
var wP={};
var _7=_3.PW_ID_PREFIX+_3.DEBUG_WINDOW_TAG;
wP[_3.PP_WINDOW_POSITION_STATIC]=false;
wP[_3.PP_WINDOW_HEIGHT_TO_FIT]=false;
wP[_3.PP_WINDOW_DECORATION]=_2.prefs.windowDecoration;
wP[_3.PP_WINDOW_TITLE]="Dojo Debug";
wP[_3.PP_WINDOW_ICON]="text-x-script.png";
wP[_3.PP_WIDGET_ID]=_7;
wP[_3.PP_WIDTH]=_5.width;
wP[_3.PP_HEIGHT]=_5.height;
wP[_3.PP_LEFT]=_5.left;
wP[_3.PP_TOP]=_5.top;
wP[_3.PP_EXCLUDE_PCONTENT]=false;
wP[_3.PP_CONTENT_RETRIEVER]=new _2.om.DojoDebugContentRetriever();
wP[_3.PP_WINDOW_STATE]=_5.windowState;
if(_5.windowState==_3.ACT_MAXIMIZE){
_2.page.maximizedOnInit=_7;
}
var _8=_2.widget.PortletWindow.prototype.altInitParamsDef(null,wP);
_2.ui.createPortletWindow(_8,null,_2);
_8.retrieveContent(null,null);
var _9=_2.page.getPWin(_7);
_4.event.connect("after",_4.hostenv,"println",_9,"contentChanged");
_4.event.connect(_9,"actionBtnSync",_2,"debugWindowSave");
_4.event.connect(_9,"endSizing",_2,"debugWindowSave");
_4.event.connect(_9,"endDragging",_2,"debugWindowSave");
}
};
jetspeed.debugWindowReadCookie=function(_a){
var _b={};
if(_a){
_b={width:"400",height:"400",left:"320",top:"0",windowState:jetspeed.id.ACT_MINIMIZE};
}
var _c=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
if(_c!=null&&_c.length>0){
var _d=_c.split("|");
if(_d&&_d.length>=4){
_b.width=_d[0];
_b.height=_d[1];
_b.top=_d[2];
_b.left=_d[3];
if(_d.length>4&&_d[4]!=null&&_d[4].length>0){
_b.windowState=_d[4];
}
}
}
return _b;
};
jetspeed.debugWindowRestore=function(){
var _e=jetspeed.debugWindow();
if(!_e){
return;
}
_e.restoreWindow();
};
jetspeed.debugWindow=function(){
var _f=jetspeed.id.PW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
return jetspeed.page.getPWin(_f);
};
jetspeed.debugWindowId=function(){
return jetspeed.id.PW_ID_PREFIX+jetspeed.id.DEBUG_WINDOW_TAG;
};
jetspeed.debugWindowSave=function(){
var _10=jetspeed.debugWindow();
if(!_10){
return null;
}
if(!_10.posStatic){
var _11=_10.getCurWinStateForPersist(false);
var _12=_11.width,_13=_11.height,_14=_11.top,_15=_11.left;
var _16=_10.windowState;
if(!_16){
_16=jetspeed.id.ACT_RESTORE;
}
var _17=_12+"|"+_13+"|"+_14+"|"+_15+"|"+_16;
dojo.io.cookie.setCookie(jetspeed.id.DEBUG_WINDOW_TAG,_17,30,"/");
var _18=dojo.io.cookie.getCookie(jetspeed.id.DEBUG_WINDOW_TAG);
}
};
jetspeed.debugDumpForm=function(_19){
if(!_19){
return null;
}
var _1a=_19.toString();
if(_19.name){
_1a+=" name="+_19.name;
}
if(_19.id){
_1a+=" id="+_19.id;
}
var _1b=dojo.io.encodeForm(_19);
_1a+=" data="+_1b;
return _1a;
};
jetspeed.om.DojoDebugContentRetriever=function(){
this.initialized=false;
};
jetspeed.om.DojoDebugContentRetriever.prototype={getContent:function(_1c,_1d,_1e,_1f){
if(!_1c){
_1c={};
}
if(!this.initialized){
var _20=jetspeed;
var _21="";
var _22=_20.debug.debugContainerId;
var _23=_20.debugWindow();
if(_20.altDebugWindowContent){
_21=_20.altDebugWindowContent();
}else{
_21+="<div id=\""+_22+"\"></div>";
}
if(_1d){
_1d.notifySuccess(_21,_1c.url,_1e);
}else{
if(_23){
_23.setPortletContent(_21,_1c.url);
}
}
this.initialized=true;
if(_23){
var _24="javascript: void(jetspeed.debugWindowClear())";
var _25="";
for(var i=0;i<20;i++){
_25+="&nbsp;";
}
var _27=_23.title+_25+"<a href=\""+_24+"\"><span style=\"font-size: xx-small; font-weight: normal\">Clear</span></a>";
_23.tbTextNode.innerHTML=_27;
}
}
}};
jetspeed.debugWindowClear=function(){
var _28=jetspeed;
var _29=_28.debug.debugContainerId;
var _2a=_28.debugWindow();
document.getElementById(_29).innerHTML="";
if(_2a&&_2a.drag){
_2a.drag.onMouseUp();
}
};
jetspeed.debugDumpColWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _2c=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_2c.domNode).width);
}
};
jetspeed.debugDumpWindowsPerCol=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _2e=jetspeed.page.columns[i];
var _2f=jetspeed.ui.getPWinAndColChildren(_2e.domNode,null);
var _30=jetspeed.ui.getPWinsFromNodes(_2f.matchingNodes);
var _31={dumpMsg:""};
if(_30!=null){
dojo.lang.forEach(_30,function(_32){
_31.dumpMsg=_31.dumpMsg+(_31.dumpMsg.length>0?", ":"")+_32.portlet.entityId;
});
}
_31.dumpMsg="column "+i+": "+_31.dumpMsg;
dojo.debug(_31.dumpMsg);
}
};
jetspeed.debugDumpWindows=function(){
var _33=jetspeed.page.getPWins();
var _34="";
for(var i=0;i<_33.length;i++){
if(i>0){
_34+=", ";
}
_34+=_33[i].widgetId;
}
dojo.debug("PortletWindows: "+_34);
};
jetspeed.debugLayoutInfo=function(){
var _36=jetspeed.page;
var _37="";
var i=0;
for(var _39 in _36.layouts){
if(i>0){
_37+="\r\n";
}
_37+="layout["+_39+"]: "+jetspeed.printobj(_36.layouts[_39],true,true,true);
i++;
}
return _37;
};
jetspeed.debugColumns=function(_3a,_3b){
var _3c=jetspeed;
var _3d=_3c.page;
var _3e=(!_3b);
var _3f=_3d.columns,col;
if(!_3f){
return null;
}
var _41=dojo.byId(_3c.id.COLUMNS);
var _42="";
var _43=!_3a;
return _3c._debugColumnTree(_3e,_41,_42,"\r\n",_3c.debugindentT,_43,_3c,_3d);
};
jetspeed._debugColumnTree=function(_44,_45,_46,_47,_48,_49,_4a,_4b){
var _4c=_4a.ui.getPWinAndColChildren(_45,null,false,true,true,_49);
var _4d=_4c.matchingNodes;
if(!_4d||_4d.length==0){
return _46;
}
var _4e,col,_50,_51,_52=(_47+_48);
for(var i=0;i<_4d.length;i++){
_4e=_4d[i];
col=_4b.getColFromColNode(_4e);
_50=null;
if(!col){
_50=_4b.getPWinFromNode(_4e);
}
_46+=_47;
if(col){
_46+=_4a.debugColumn(col,_44);
_46=_4a._debugColumnTree(_44,_4e,_46,_52,_48,_49,_4a,_4b);
}else{
if(_50){
_51=_50.title;
_46+=_50.widgetId+((_51&&_51.length>0)?(" - "+_51):"");
}else{
_46+=_4a.debugNode(_4e);
}
}
}
return _46;
};
jetspeed.debugColumn=function(col,_55){
if(!col){
return null;
}
var _56=col.domNode;
var out="column["+dojo.string.padLeft(String(col.pageColumnIndex),2," ")+"]";
out+=" colContainer="+(col.columnContainer?"T":"F")+" layoutHeader="+(col.layoutHeader?"T":"F")+" id="+(_56!=null?_56.id:"null")+" layoutCol="+col.layoutColumnIndex+" layoutId="+col.layoutId+" size="+col.size;
if(_56!=null&&!_55){
var _58=dojo.html.getAbsolutePosition(_56,true);
var _59=dojo.html.getMarginBox(_56);
out+=" dims={"+"l="+(_58.x)+" t="+(_58.y)+" r="+(_58.x+_59.width)+" b="+(_58.y+_59.height)+" wOff="+_56.offsetWidth+" hOff="+_56.offsetHeight+" wCl="+_56.clientWidth+" hCl="+_56.clientHeight+"}";
}
return out;
};
jetspeed.debugSavedWinState=function(){
return jetspeed.debugWinStateAll(true);
};
jetspeed.debugWinState=function(){
return jetspeed.debugWinStateAll(false);
};
jetspeed.debugPortletActions=function(){
var _5a=jetspeed.page.getPortletArray();
var _5b="";
for(var i=0;i<_5a.length;i++){
var _5d=_5a[i];
if(i>0){
_5b+="\r\n";
}
_5b+="portlet ["+_5d.name+"] actions: {";
for(var _5e in _5d.actions){
_5b+=_5e+"={"+jetspeed.printobj(_5d.actions[_5e],true)+"} ";
}
_5b+="}";
}
return _5b;
};
jetspeed.debugWinStateAll=function(_5f){
var _60=jetspeed.page.getPortletArray();
var _61="";
for(var i=0;i<_60.length;i++){
var _63=_60[i];
if(i>0){
_61+="\r\n";
}
var _64=null;
try{
if(_5f){
_64=_63.getSavedWinState();
}else{
_64=_63.getCurWinState();
}
}
catch(e){
}
_61+="["+_63.name+"] "+((_64==null)?"null":jetspeed.printobj(_64,true));
}
return _61;
};
jetspeed.debugPWinPos=function(_65){
var _66=jetspeed;
var _67=dojo;
var _68=_66.UAie;
var djH=_67.html;
var _6a=_65.domNode;
var _6b=_65.containerNode;
var _6c=_65.tbNode;
var _6d=_65.rbNode;
var _6e=djH.getAbsolutePosition(_6a,true);
var _6f=djH.getAbsolutePosition(_6b,true);
var _70=djH.getAbsolutePosition(_6c,true);
var _71=djH.getAbsolutePosition(_6d,true);
var _72=_67.gcs(_6a),_73=_67.gcs(_6b),_74=_67.gcs(_6c),_75=_67.gcs(_6d);
var _76=null;
if(_66.UAie6){
_76=djH.getAbsolutePosition(_65.bgIframe.iframe,true);
}
var _77=null;
var _78=null;
var _79=null;
if(_65.iframesInfo!=null&&_65.iframesInfo.iframeCover!=null){
_77=_65.iframesInfo.iframeCover;
_78=djH.getAbsolutePosition(_77,true);
_79=_67.gcs(_77);
}
var _7a=_65._getLayoutInfo();
var ind=_66.debugindent;
var _7c=_66.debugindentH;
_67.hostenv.println("wnd-dims ["+_65.widgetId+"  "+_65.title+"]"+"  z="+_6a.style.zIndex+" hfit="+_65.heightToFit);
_67.hostenv.println(ind+"d.abs {x="+_6e.x+" y="+_6e.y+"}"+(_68?("  hasLayout="+_6a.currentStyle.hasLayout):""));
_67.hostenv.println(ind+"c.abs {x="+_6f.x+" y="+_6f.y+"}"+(_68?("  hasLayout="+_6b.currentStyle.hasLayout):""));
_67.hostenv.println(ind+"t.abs {x="+_70.x+" y="+_70.y+"}"+(_68?("  hasLayout="+_6c.currentStyle.hasLayout):""));
_67.hostenv.println(ind+"r.abs {x="+_71.x+" y="+_71.y+"}"+(_68?("  hasLayout="+_6d.currentStyle.hasLayout):""));
if(_76!=null){
_67.hostenv.println(ind+"ibg.abs {x="+_76.x+" y="+_76.y+"}"+_7c+" z="+_65.bgIframe.iframe.currentStyle.zIndex+(_68?(" hasLayout="+_65.bgIframe.iframe.currentStyle.hasLayout):""));
}
if(_78!=null){
_67.hostenv.println(ind+"icv.abs {x="+_78.x+" y="+_78.y+"}"+_7c+" z="+_79.zIndex+(_68?(" hasLayout="+_77.currentStyle.hasLayout):""));
}
_67.hostenv.println(ind+"d.mb "+_66.debugDims(_67.getMarginBox(_6a,_72,_66))+_7c+" d.offset {w="+_6a.offsetWidth+" h="+_6a.offsetHeight+"}");
_67.hostenv.println(ind+"d.cb "+_66.debugDims(_67.getContentBox(_6a,_72,_66))+_7c+" d.client {w="+_6a.clientWidth+" h="+_6a.clientHeight+"}");
_67.hostenv.println(ind+"d.style {"+_66._debugPWinStyle(_6a,_72,"width",true)+_66._debugPWinStyle(_6a,_72,"height")+_7c+_66._debugPWinStyle(_6a,_72,"left")+_66._debugPWinStyle(_6a,_72,"top")+_7c+" pos="+_72.position.substring(0,1)+" ofx="+_72.overflowX.substring(0,1)+" ofy="+_72.overflowY.substring(0,1)+"}");
_67.hostenv.println(ind+"c.mb "+_66.debugDims(_67.getMarginBox(_6b,_73,_66))+_7c+" c.offset {w="+_6b.offsetWidth+" h="+_6b.offsetHeight+"}");
_67.hostenv.println(ind+"c.cb "+_66.debugDims(_67.getContentBox(_6b,_73,_66))+_7c+" c.client {w="+_6b.clientWidth+" h="+_6b.clientHeight+"}");
_67.hostenv.println(ind+"c.style {"+_66._debugPWinStyle(_6b,_73,"width",true)+_66._debugPWinStyle(_6b,_73,"height")+_7c+_66._debugPWinStyle(_6b,_73,"left")+_66._debugPWinStyle(_6b,_73,"top")+_7c+" ofx="+_73.overflowX.substring(0,1)+" ofy="+_73.overflowY.substring(0,1)+" d="+_73.display.substring(0,1)+"}");
_67.hostenv.println(ind+"t.mb "+_66.debugDims(_67.getMarginBox(_6c,_74,_66))+_7c+" t.offset {w="+_6c.offsetWidth+" h="+_6c.offsetHeight+"}");
_67.hostenv.println(ind+"t.cb "+_66.debugDims(_67.getContentBox(_6c,_74,_66))+_7c+" t.client {w="+_6c.clientWidth+" h="+_6c.clientHeight+"}");
_67.hostenv.println(ind+"t.style {"+_66._debugPWinStyle(_6c,_74,"width",true)+_66._debugPWinStyle(_6c,_74,"height")+_7c+_66._debugPWinStyle(_6c,_74,"left")+_66._debugPWinStyle(_6c,_74,"top")+"}");
_67.hostenv.println(ind+"r.mb "+_66.debugDims(_67.getMarginBox(_6d,_75,_66))+_7c+" r.offset {w="+_6d.offsetWidth+" h="+_6d.offsetHeight+"}");
_67.hostenv.println(ind+"r.cb "+_66.debugDims(_67.getContentBox(_6d,_75,_66))+_7c+" r.client {w="+_6d.clientWidth+" h="+_6d.clientHeight+"}");
_67.hostenv.println(ind+"r.style {"+_66._debugPWinStyle(_6d,_75,"width",true)+_66._debugPWinStyle(_6d,_75,"height")+_7c+_66._debugPWinStyle(_6d,_75,"left")+_66._debugPWinStyle(_6d,_75,"top")+"}");
if(_76!=null){
var _7d=_65.bgIframe.iframe;
var _7e=_67.gcs(_7d);
_67.hostenv.println(ind+"ibg.mb "+_66.debugDims(_67.getMarginBox(_7d,_7e,_66)));
_67.hostenv.println(ind+"ibg.cb "+_66.debugDims(_67.getContentBox(_7d,_7e,_66)));
_67.hostenv.println(ind+"ibg.style {"+_66._debugPWinStyle(_7d,_7e,"width",true)+_66._debugPWinStyle(_7d,_7e,"height")+_7c+_66._debugPWinStyle(_7d,_7e,"left")+_66._debugPWinStyle(_7d,_7e,"top")+_7c+" pos="+_7e.position.substring(0,1)+" ofx="+_7e.overflowX.substring(0,1)+" ofy="+_7e.overflowY.substring(0,1)+" d="+_7e.display.substring(0,1)+"}");
}
if(_77){
_67.hostenv.println(ind+"icv.mb "+_66.debugDims(_67.getMarginBox(_77,_79,_66)));
_67.hostenv.println(ind+"icv.cb "+_66.debugDims(_67.getContentBox(_77,_79,_66)));
_67.hostenv.println(ind+"icv.style {"+_66._debugPWinStyle(_77,_79,"width",true)+_66._debugPWinStyle(_77,_79,"height")+_7c+_66._debugPWinStyle(_77,_79,"left")+_66._debugPWinStyle(_77,_79,"top")+_7c+" pos="+_79.position.substring(0,1)+" ofx="+_79.overflowX.substring(0,1)+" ofy="+_79.overflowY.substring(0,1)+" d="+_79.display.substring(0,1)+"}");
}
var leN=_7a.dNode;
_67.hostenv.println(ind+"dLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_7a.cNode;
_67.hostenv.println(ind+"cLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_7a.tbNode;
_67.hostenv.println(ind+"tLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_7a.rbNode;
_67.hostenv.println(ind+"rLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
_67.hostenv.println(ind+"cNode_mBh_LessBars="+_7a.cNode_mBh_LessBars);
_67.hostenv.println(ind+"dimsTiled "+_66.debugDims(_65.dimsTiled));
_67.hostenv.println(ind+"dimsUntiled "+_66.debugDims(_65.dimsUntiled));
if(_65.dimsTiledTemp!=null){
_67.hostenv.println(ind+"dimsTiledTemp "+_66.debugDims(_65.dimsTiledTemp));
}
if(_65.dimsUntiledTemp!=null){
_67.hostenv.println(ind+"dimsUntiledTemp="+_66.debugDims(_65.dimsUntiledTemp));
}
_67.hostenv.println(ind+"--------------------");
},jetspeed.debugDims=function(box,_81){
return ("{w="+(box.w==undefined?(box.width==undefined?"null":box.width):box.w)+" h="+(box.h==undefined?(box.height==undefined?"null":box.height):box.h)+(box.l!=undefined?(" l="+box.l):(box.left==undefined?"":(" l="+box.left)))+(box.t!=undefined?(" t="+box.t):(box.top==undefined?"":(" t="+box.top)))+(box.right!=undefined?(" r="+box.right):"")+(box.bottom!=undefined?(" b="+box.bottom):"")+(!_81?"}":""));
};
jetspeed._debugPWinStyle=function(_82,_83,_84,_85){
var _86=_82.style[_84];
var _87=_83[_84];
if(_86=="auto"){
_86="a";
}
if(_87=="auto"){
_87="a";
}
var _88=null;
if(_86==_87){
_88=("\""+_87+"\"");
}else{
_88=("\""+_86+"\"/"+_87);
}
return ((_85?"":" ")+_84.substring(0,1)+"="+_88);
};
if(jetspeed.debug.profile){
dojo.profile.clearItem=function(_89){
return (this._profiles[_89]={iters:0,total:0});
};
dojo.profile.debugItem=function(_8a,_8b){
var _8c=this._profiles[_8a];
if(_8c==null){
return null;
}
if(_8c.iters==0){
return [_8a," not profiled."].join("");
}
var _8d=[_8a," took ",_8c.total," msec for ",_8c.iters," iteration"];
if(_8c.iters>1){
_8d.push("s (",(Math.round(_8c.total/_8c.iters*100)/100)," msec each)");
}
dojo.debug(_8d.join(""));
if(_8b){
this.clearItem(_8a);
}
};
dojo.profile.debugAllItems=function(_8e){
for(var x=0;x<this._pns.length;x++){
this.debugItem(this._pns[x],_8e);
}
};
}
window.getPWin=function(_90){
return jetspeed.page.getPWin(_90);
};

