dojo.provide("jetspeed.desktop.debug");
dojo.require("jetspeed.debug");
dojo.require("dojo.profile");
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.om){
jetspeed.om={};
}
jetspeed.debug={pageLoad:false,retrievePsml:false,setPortletContent:false,doRenderDoAction:false,postParseAnnotateHtml:false,postParseAnnotateHtmlDisableAnchors:false,confirmOnSubmit:false,createWindow:false,initWinState:false,submitWinState:false,ajaxPageNav:false,dragWindow:false,dragWindowStart:false,profile:true,windowDecorationRandom:false,debugInPortletWindow:true,debugContainerId:(djConfig.debugContainerId?djConfig.debugContainerId:dojo.hostenv.defaultDebugContainerId)};
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
var _24="javascript: void(document.getElementById('"+_22+"').innerHTML='')";
var _25="";
for(var i=0;i<20;i++){
_25+="&nbsp;";
}
var _27=_23.title+_25+"<a href=\""+_24+"\"><span style=\"font-size: xx-small; font-weight: normal\">Clear</span></a>";
_23.tbTextNode.innerHTML=_27;
}
}
}};
jetspeed.debugDumpColWidths=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _29=jetspeed.page.columns[i];
dojo.debug("jetspeed.page.columns["+i+"] outer-width: "+dojo.html.getMarginBox(_29.domNode).width);
}
};
jetspeed.debugDumpWindowsPerCol=function(){
for(var i=0;i<jetspeed.page.columns.length;i++){
var _2b=jetspeed.page.columns[i];
var _2c=jetspeed.ui.getPWinAndColChildren(_2b.domNode,null);
var _2d=jetspeed.ui.getPWinsFromNodes(_2c.matchingNodes);
var _2e={dumpMsg:""};
if(_2d!=null){
dojo.lang.forEach(_2d,function(_2f){
_2e.dumpMsg=_2e.dumpMsg+(_2e.dumpMsg.length>0?", ":"")+_2f.portlet.entityId;
});
}
_2e.dumpMsg="column "+i+": "+_2e.dumpMsg;
dojo.debug(_2e.dumpMsg);
}
};
jetspeed.debugDumpWindows=function(){
var _30=jetspeed.page.getPWins();
var _31="";
for(var i=0;i<_30.length;i++){
if(i>0){
_31+=", ";
}
_31+=_30[i].widgetId;
}
dojo.debug("PortletWindows: "+_31);
};
jetspeed.debugLayoutInfo=function(){
var _33=jetspeed.page;
var _34="";
var i=0;
for(var _36 in _33.layouts){
if(i>0){
_34+="\r\n";
}
_34+="layout["+_36+"]: "+jetspeed.printobj(_33.layouts[_36],true,true,true);
i++;
}
return _34;
};
jetspeed.debugColumns=function(_37,_38){
var _39=jetspeed;
var _3a=_39.page;
var _3b=(!_38);
var _3c=_3a.columns,col;
if(!_3c){
return null;
}
var _3e=dojo.byId(_39.id.COLUMNS);
var _3f="";
var _40=!_37;
return _39._debugColumnTree(_3b,_3e,_3f,"\r\n",_39.debugindentT,_40,_39,_3a);
};
jetspeed._debugColumnTree=function(_41,_42,_43,_44,_45,_46,_47,_48){
var _49=_47.ui.getPWinAndColChildren(_42,null,false,true,true,_46);
var _4a=_49.matchingNodes;
if(!_4a||_4a.length==0){
return _43;
}
var _4b,col,_4d,_4e,_4f=(_44+_45);
for(var i=0;i<_4a.length;i++){
_4b=_4a[i];
col=_48.getColFromColNode(_4b);
_4d=null;
if(!col){
_4d=_48.getPWinFromNode(_4b);
}
_43+=_44;
if(col){
_43+=_47.debugColumn(col,_41);
_43=_47._debugColumnTree(_41,_4b,_43,_4f,_45,_46,_47,_48);
}else{
if(_4d){
_4e=_4d.title;
_43+=_4d.widgetId+((_4e&&_4e.length>0)?(" - "+_4e):"");
}else{
_43+=_47.debugNode(_4b);
}
}
}
return _43;
};
jetspeed.debugColumn=function(col,_52){
if(!col){
return null;
}
var _53=col.domNode;
var out="column["+dojo.string.padLeft(String(col.pageColumnIndex),2," ")+"]";
out+=" colContainer="+(col.columnContainer?"T":"F")+" layoutHeader="+(col.layoutHeader?"T":"F")+" id="+(_53!=null?_53.id:"null")+" layoutCol="+col.layoutColumnIndex+" layoutId="+col.layoutId+" size="+col.size;
if(_53!=null&&!_52){
var _55=dojo.html.getAbsolutePosition(_53,true);
var _56=dojo.html.getMarginBox(_53);
out+=" dims={"+"left:"+(_55.x)+", right:"+(_55.x+_56.width)+", top:"+(_55.y)+", bottom:"+(_55.y+_56.height)+"}";
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
var _57=jetspeed.page.getPortletArray();
var _58="";
for(var i=0;i<_57.length;i++){
var _5a=_57[i];
if(i>0){
_58+="\r\n";
}
_58+="portlet ["+_5a.name+"] actions: {";
for(var _5b in _5a.actions){
_58+=_5b+"={"+jetspeed.printobj(_5a.actions[_5b],true)+"} ";
}
_58+="}";
}
return _58;
};
jetspeed.debugWinStateAll=function(_5c){
var _5d=jetspeed.page.getPortletArray();
var _5e="";
for(var i=0;i<_5d.length;i++){
var _60=_5d[i];
if(i>0){
_5e+="\r\n";
}
var _61=null;
try{
if(_5c){
_61=_60.getSavedWinState();
}else{
_61=_60.getCurWinState();
}
}
catch(e){
}
_5e+="["+_60.name+"] "+((_61==null)?"null":jetspeed.printobj(_61,true));
}
return _5e;
};
jetspeed.debugPWinPos=function(_62){
var _63=jetspeed;
var _64=dojo;
var _65=_63.UAie;
var djH=_64.html;
var _67=_62.domNode;
var _68=_62.containerNode;
var _69=_62.tbNode;
var _6a=_62.rbNode;
var _6b=djH.getAbsolutePosition(_67,true);
var _6c=djH.getAbsolutePosition(_68,true);
var _6d=djH.getAbsolutePosition(_69,true);
var _6e=djH.getAbsolutePosition(_6a,true);
var _6f=_64.gcs(_67),_70=_64.gcs(_68),_71=_64.gcs(_69),_72=_64.gcs(_6a);
var _73=null;
if(_63.UAie6){
_73=djH.getAbsolutePosition(_62.bgIframe.iframe,true);
}
var _74=null;
var _75=null;
var _76=null;
if(_62.iframesInfo!=null&&_62.iframesInfo.iframeCover!=null){
_74=_62.iframesInfo.iframeCover;
_75=djH.getAbsolutePosition(_74,true);
_76=_64.gcs(_74);
}
var _77=_62._getLayoutInfo();
var ind=_63.debugindent;
var _79=_63.debugindentH;
_64.hostenv.println("wnd-dims ["+_62.widgetId+"  "+_62.title+"]"+"  z="+_67.style.zIndex+" hfit="+_62.heightToFit);
_64.hostenv.println(ind+"d.abs {x="+_6b.x+" y="+_6b.y+"}"+(_65?("  hasLayout="+_67.currentStyle.hasLayout):""));
_64.hostenv.println(ind+"c.abs {x="+_6c.x+" y="+_6c.y+"}"+(_65?("  hasLayout="+_68.currentStyle.hasLayout):""));
_64.hostenv.println(ind+"t.abs {x="+_6d.x+" y="+_6d.y+"}"+(_65?("  hasLayout="+_69.currentStyle.hasLayout):""));
_64.hostenv.println(ind+"r.abs {x="+_6e.x+" y="+_6e.y+"}"+(_65?("  hasLayout="+_6a.currentStyle.hasLayout):""));
if(_73!=null){
_64.hostenv.println(ind+"ibg.abs {x="+_73.x+" y="+_73.y+"}"+_79+" z="+_62.bgIframe.iframe.currentStyle.zIndex+(_65?(" hasLayout="+_62.bgIframe.iframe.currentStyle.hasLayout):""));
}
if(_75!=null){
_64.hostenv.println(ind+"icv.abs {x="+_75.x+" y="+_75.y+"}"+_79+" z="+_76.zIndex+(_65?(" hasLayout="+_74.currentStyle.hasLayout):""));
}
_64.hostenv.println(ind+"d.mb "+_63.debugDims(_64.getMarginBox(_67,_6f,_63))+_79+" d.offset {w="+_67.offsetWidth+" h="+_67.offsetHeight+"}");
_64.hostenv.println(ind+"d.cb "+_63.debugDims(_64.getContentBox(_67,_6f,_63))+_79+" d.client {w="+_67.clientWidth+" h="+_67.clientHeight+"}");
_64.hostenv.println(ind+"d.style {"+_63._debugPWinStyle(_67,_6f,"width",true)+_63._debugPWinStyle(_67,_6f,"height")+_79+_63._debugPWinStyle(_67,_6f,"left")+_63._debugPWinStyle(_67,_6f,"top")+_79+" pos="+_6f.position.substring(0,1)+" ofx="+_6f.overflowX.substring(0,1)+" ofy="+_6f.overflowY.substring(0,1)+"}");
_64.hostenv.println(ind+"c.mb "+_63.debugDims(_64.getMarginBox(_68,_70,_63))+_79+" c.offset {w="+_68.offsetWidth+" h="+_68.offsetHeight+"}");
_64.hostenv.println(ind+"c.cb "+_63.debugDims(_64.getContentBox(_68,_70,_63))+_79+" c.client {w="+_68.clientWidth+" h="+_68.clientHeight+"}");
_64.hostenv.println(ind+"c.style {"+_63._debugPWinStyle(_68,_70,"width",true)+_63._debugPWinStyle(_68,_70,"height")+_79+_63._debugPWinStyle(_68,_70,"left")+_63._debugPWinStyle(_68,_70,"top")+_79+" ofx="+_70.overflowX.substring(0,1)+" ofy="+_70.overflowY.substring(0,1)+" d="+_70.display.substring(0,1)+"}");
_64.hostenv.println(ind+"t.mb "+_63.debugDims(_64.getMarginBox(_69,_71,_63))+_79+" t.offset {w="+_69.offsetWidth+" h="+_69.offsetHeight+"}");
_64.hostenv.println(ind+"t.cb "+_63.debugDims(_64.getContentBox(_69,_71,_63))+_79+" t.client {w="+_69.clientWidth+" h="+_69.clientHeight+"}");
_64.hostenv.println(ind+"t.style {"+_63._debugPWinStyle(_69,_71,"width",true)+_63._debugPWinStyle(_69,_71,"height")+_79+_63._debugPWinStyle(_69,_71,"left")+_63._debugPWinStyle(_69,_71,"top")+"}");
_64.hostenv.println(ind+"r.mb "+_63.debugDims(_64.getMarginBox(_6a,_72,_63))+_79+" r.offset {w="+_6a.offsetWidth+" h="+_6a.offsetHeight+"}");
_64.hostenv.println(ind+"r.cb "+_63.debugDims(_64.getContentBox(_6a,_72,_63))+_79+" r.client {w="+_6a.clientWidth+" h="+_6a.clientHeight+"}");
_64.hostenv.println(ind+"r.style {"+_63._debugPWinStyle(_6a,_72,"width",true)+_63._debugPWinStyle(_6a,_72,"height")+_79+_63._debugPWinStyle(_6a,_72,"left")+_63._debugPWinStyle(_6a,_72,"top")+"}");
if(_73!=null){
var _7a=_62.bgIframe.iframe;
var _7b=_64.gcs(_7a);
_64.hostenv.println(ind+"ibg.mb "+_63.debugDims(_64.getMarginBox(_7a,_7b,_63)));
_64.hostenv.println(ind+"ibg.cb "+_63.debugDims(_64.getContentBox(_7a,_7b,_63)));
_64.hostenv.println(ind+"ibg.style {"+_63._debugPWinStyle(_7a,_7b,"width",true)+_63._debugPWinStyle(_7a,_7b,"height")+_79+_63._debugPWinStyle(_7a,_7b,"left")+_63._debugPWinStyle(_7a,_7b,"top")+_79+" pos="+_7b.position.substring(0,1)+" ofx="+_7b.overflowX.substring(0,1)+" ofy="+_7b.overflowY.substring(0,1)+" d="+_7b.display.substring(0,1)+"}");
}
if(_74){
_64.hostenv.println(ind+"icv.mb "+_63.debugDims(_64.getMarginBox(_74,_76,_63)));
_64.hostenv.println(ind+"icv.cb "+_63.debugDims(_64.getContentBox(_74,_76,_63)));
_64.hostenv.println(ind+"icv.style {"+_63._debugPWinStyle(_74,_76,"width",true)+_63._debugPWinStyle(_74,_76,"height")+_79+_63._debugPWinStyle(_74,_76,"left")+_63._debugPWinStyle(_74,_76,"top")+_79+" pos="+_76.position.substring(0,1)+" ofx="+_76.overflowX.substring(0,1)+" ofy="+_76.overflowY.substring(0,1)+" d="+_76.display.substring(0,1)+"}");
}
var leN=_77.dNode;
_64.hostenv.println(ind+"dLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_77.cNode;
_64.hostenv.println(ind+"cLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_77.tbNode;
_64.hostenv.println(ind+"tLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
leN=_77.rbNode;
_64.hostenv.println(ind+"rLE {"+"-w="+leN.lessW+" -h="+leN.lessH+" mw="+leN.mE.w+" mh="+leN.mE.h+" bw="+leN.bE.w+" bh="+leN.bE.h+" pw="+leN.pE.w+" ph="+leN.pE.h+"}");
_64.hostenv.println(ind+"cNode_mBh_LessBars="+_77.cNode_mBh_LessBars);
_64.hostenv.println(ind+"dimsTiled "+_63.debugDims(_62.dimsTiled));
_64.hostenv.println(ind+"dimsUntiled "+_63.debugDims(_62.dimsUntiled));
if(_62.dimsTiledTemp!=null){
_64.hostenv.println(ind+"dimsTiledTemp "+_63.debugDims(_62.dimsTiledTemp));
}
if(_62.dimsUntiledTemp!=null){
_64.hostenv.println(ind+"dimsUntiledTemp="+_63.debugDims(_62.dimsUntiledTemp));
}
_64.hostenv.println(ind+"--------------------");
},jetspeed.debugDims=function(box,_7e){
return ("{w="+(box.w==undefined?(box.width==undefined?"null":box.width):box.w)+" h="+(box.h==undefined?(box.height==undefined?"null":box.height):box.h)+(box.l!=undefined?(" l="+box.l):(box.left==undefined?"":(" l="+box.left)))+(box.t!=undefined?(" t="+box.t):(box.top==undefined?"":(" t="+box.top)))+(box.right!=undefined?(" r="+box.right):"")+(box.bottom!=undefined?(" b="+box.bottom):"")+(!_7e?"}":""));
};
jetspeed._debugPWinStyle=function(_7f,_80,_81,_82){
var _83=_7f.style[_81];
var _84=_80[_81];
if(_83=="auto"){
_83="a";
}
if(_84=="auto"){
_84="a";
}
var _85=null;
if(_83==_84){
_85=("\""+_84+"\"");
}else{
_85=("\""+_83+"\"/"+_84);
}
return ((_82?"":" ")+_81.substring(0,1)+"="+_85);
};
if(jetspeed.debug.profile){
dojo.profile.clearItem=function(_86){
return (this._profiles[_86]={iters:0,total:0});
};
dojo.profile.debugItem=function(_87,_88){
var _89=this._profiles[_87];
if(_89==null){
return null;
}
if(_89.iters==0){
return [_87," not profiled."].join("");
}
var _8a=[_87," took ",_89.total," msec for ",_89.iters," iteration"];
if(_89.iters>1){
_8a.push("s (",(Math.round(_89.total/_89.iters*100)/100)," msec each)");
}
dojo.debug(_8a.join(""));
if(_88){
this.clearItem(_87);
}
};
dojo.profile.debugAllItems=function(_8b){
for(var x=0;x<this._pns.length;x++){
this.debugItem(this._pns[x],_8b);
}
};
}
window.getPWin=function(_8d){
return jetspeed.page.getPWin(_8d);
};

