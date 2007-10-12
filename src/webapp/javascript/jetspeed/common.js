if(window.dojo){
dojo.provide("jetspeed.common");
dojo.require("dojo.io.*");
dojo.require("dojo.uri.Uri");
}
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.url){
jetspeed.url={};
}
if(!jetspeed.om){
jetspeed.om={};
}
jetspeed.version={major:2,minor:1,patch:0,flag:"dev",revision:"",toString:function(){
with(jetspeed.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
jetspeed.initcommon=function(){
var _1=jetspeed;
if(!window.dojo){
var _1=jetspeed;
_1.no_dojo_load_notifying=false;
_1.no_dojo_post_load=false;
_1.pageLoadedListeners=[];
window.onload=function(){
if(!window.dojo){
var _2=jetspeed;
_2.no_dojo_load_notifying=true;
_2.no_dojo_post_load=true;
var _3=_2.pageLoadedListeners;
for(var x=0;x<_3.length;x++){
_3[x]();
}
_2.pageLoadedListeners=[];
}
};
}else{
var _5=dojo.render.html;
if(_5.ie){
_1.UAie=true;
if(_5.ie60||_5.ie50||_5.ie55){
_1.UAie6=true;
}
_1.stopEvent=function(_6){
_6=_6||window.event;
_6.cancelBubble=true;
_6.returnValue=false;
};
_1._stopEvent=function(_7){
jetspeed.stopEvent(_7);
};
}else{
if(_5.mozilla){
_1.UAmoz=true;
}else{
if(_5.safari){
_1.UAsaf=true;
}else{
if(_5.opera){
_1.UAope=true;
}
}
}
_1.stopEvent=function(_8){
_8.preventDefault();
_8.stopPropagation();
};
_1._stopEvent=function(_9){
jetspeed.stopEvent(_9);
};
}
}
};
jetspeed.addOnLoad=function(_a,_b){
if(window.dojo){
if(arguments.length==1){
dojo.addOnLoad(_a);
}else{
dojo.addOnLoad(_a,_b);
}
}else{
if(arguments.length==1){
jetspeed.pageLoadedListeners.push(_a);
}else{
if(arguments.length>1){
jetspeed.pageLoadedListeners.push(function(){
_a[_b]();
});
}
}
if(jetspeed.no_dojo_post_load&&!jetspeed.no_dojo_load_notifying){
jetspeed.callPageLoaded();
}
}
};
jetspeed.callPageLoaded=function(){
if(typeof setTimeout=="object"){
setTimeout("jetspeed.pageLoaded();",0);
}else{
jetspeed.pageLoaded();
}
};
jetspeed.getBody=function(){
var _c=jetspeed;
if(_c.docBody==null){
_c.docBody=document.body||document.getElementsByTagName("body")[0];
}
return _c.docBody;
};
jetspeed.formatError=function(ex){
if(ex==null){
return "";
}
var _e=" error:";
if(ex.message!=null){
_e+=" "+ex.message;
}
var _f=ex.number||ex.lineNumber||ex.lineNo;
if(_f==null||_f=="0"||_f.length==0){
_f=null;
}
var _10=ex.fileName;
if(_10!=null){
var _11=_10.lastIndexOf("/");
if(_11!=-1&&_11<(_10.length-1)){
_10=_10.substring(_11+1);
}
}
if(_10==null||_10.length==0){
_10=null;
}
var _12=ex.type;
if(_12==null||_12.length==0||_12=="unknown"){
_12=null;
}
if(_f!=null||_10!=null||_12!=null){
_e+=" ("+(_10!=null?(" "+_10):"");
_e+=(_f!=null?(" line "+_f):"");
_e+=(_12!=null?(" type "+_12):"");
_e+=" )";
}
return _e;
};
jetspeed.url.LOADING_INDICATOR_ID="js-showloading";
jetspeed.url.path={SERVER:null,JETSPEED:null,AJAX_API:null,DESKTOP:null,PORTAL:null,PORTLET:null,ACTION:null,RENDER:null,initialized:false};
jetspeed.url.pathInitialize=function(_13){
var jsU=jetspeed.url;
var _15=jsU.path;
if(!_13&&_15.initialized){
return;
}
var _16=document.getElementsByTagName("base");
var _17=null;
if(_16&&_16.length==1){
_17=_16[0].href;
}else{
_17=window.location.href;
}
var _18=jsU.parse(_17);
var _19=_18.path;
var _1a=-1;
for(var _1b=1;_1a<=_1b;_1b++){
_1a=_19.indexOf("/",_1b);
if(_1a==-1){
break;
}
}
var _1c="";
if(_18.scheme!=null){
_1c+=_18.scheme+":";
}
if(_18.authority!=null){
_1c+="//"+_18.authority;
}
var _1d=null;
if(_1a==-1){
_1d=_19;
}else{
_1d=_19.substring(0,_1a);
}
_15.JETSPEED=_1d;
_15.SERVER=_1c;
_15.AJAX_API=_15.JETSPEED+"/ajaxapi";
_15.DESKTOP=_15.JETSPEED+"/desktop";
_15.PORTAL=_15.JETSPEED+"/portal";
_15.PORTLET=_15.JETSPEED+"/portlet";
_15.ACTION=_15.JETSPEED+"/action";
_15.RENDER=_15.JETSPEED+"/render";
_15.initialized=true;
};
jetspeed.url.parse=function(url){
if(url==null){
return null;
}
if(window.dojo&&window.dojo.uri){
return new dojo.uri.Uri(url);
}
return new jetspeed.url.JSUri(url);
};
jetspeed.url.JSUri=function(url){
if(url!=null){
if(!url.path){
var _20="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=url.toString().match(new RegExp(_20));
var _22={};
this.scheme=r[2]||(r[1]?"":null);
this.authority=r[4]||(r[3]?"":null);
this.path=r[5];
this.query=r[7]||(r[6]?"":null);
this.fragment=r[9]||(r[8]?"":null);
}else{
this.scheme=url.scheme;
this.authority=url.authority;
this.path=url.path;
this.query=url.query;
this.fragment=url.fragment;
}
}
};
jetspeed.url.JSUri.prototype={scheme:null,authority:null,path:null,query:null,fragment:null,toString:function(){
var uri="";
uri+=(this.scheme!=null&&this.scheme.length>0)?(this.scheme+"://"):"";
uri+=(this.authority!=null&&this.authority.length>0)?this.authority:"";
uri+=(this.path!=null&&this.path.length>0)?this.path:"";
uri+=(this.query!=null&&this.query.length>0)?("?"+this.query):"";
uri+=(this.fragment!=null&&this.fragment>0)?("#"+this.fragment):"";
return uri;
}};
jetspeed.url.scheme={HTTP_PREFIX:"http://",HTTP_PREFIX_LEN:"http://".length,HTTPS_PREFIX:"https://",HTTPS_PREFIX_LEN:"https://".length};
jetspeed.url.isPortal=function(){
if(window.djConfig&&window.djConfig.jetspeed){
var _24=window.djConfig.jetspeed.servletPath;
if(_24!=null&&_24.toLowerCase().indexOf("/desktop")==0){
return false;
}
}
return true;
};
jetspeed.url.isDesktop=function(){
return !jetspeed.url.isPortal();
};
jetspeed.url.servletPath=function(){
if(jetspeed.url.isPortal()){
return "/portal";
}else{
return "/desktop";
}
};
jetspeed.url.basePortalUrl=function(){
if(!jetspeed.url.path.initialized){
jetspeed.url.pathInitialize();
}
return jetspeed.url.path.SERVER;
};
jetspeed.url.basePortalDesktopUrl=function(){
if(!jetspeed.url.path.initialized){
jetspeed.url.pathInitialize();
}
return jetspeed.url.basePortalUrl()+jetspeed.url.path.JETSPEED;
};
jetspeed.url.addPath=function(url,_26){
if(_26==null||_26.length==0){
return url;
}
var _27=new jetspeed.url.JSUri(url);
var _28=_27.path;
if(_28!=null&&_28.length>0){
if(_27.path.charCodeAt(_28.length-1)==47){
if(_26.charCodeAt(0)==47){
if(_26.length>1){
_27.path+=_26.substring(1);
}
}else{
_27.path+=_26;
}
}else{
if(_26.charCodeAt(0)==47){
_27.path+=_26;
}else{
if(_26.length>1){
_27.path+="/"+_26;
}
}
}
}
var _29=jetspeed.url.parse(_27);
return _29.toString();
};
jetspeed.url.urlStartsWithHttp=function(url){
if(url){
var len=url.length;
var _2c=jetspeed.url.scheme.HTTPS_PREFIX_LEN;
if(len>_2c){
var _2d=jetspeed.url.scheme.HTTP_PREFIX_LEN;
if(url.substring(0,_2d)==jetspeed.url.scheme.HTTP_PREFIX){
return true;
}
if(url.substring(0,_2c)==jetspeed.url.scheme.HTTPS_PREFIX){
return true;
}
}
}
return false;
};
jetspeed.url.addQueryParameter=function(_2e,_2f,_30,_31){
if(_2e==null){
return _2e;
}
if(!_2e.path){
_2e=jetspeed.url.parse(_2e);
}
if(_2e==null){
return null;
}
if(_2f==null){
return _2e;
}
_2e.jsQParamN=null;
if(_31){
_2e=jetspeed.url.removeQueryParameter(_2e,_2f,false);
}
var _32=_2e.query;
if(_32==null){
_32="";
}
var _33=_32.length;
if(_33>0){
_32+="&";
}
_32+=_2f+"="+(_30!=null?_30:"");
_2e.query=_32;
var _34=new jetspeed.url.JSUri(_2e);
_2e=jetspeed.url.parse(_34);
return _2e;
};
jetspeed.url.removeAllQueryParameters=function(_35){
return jetspeed.url.removeQueryParameter(_35,null,true);
};
jetspeed.url.removeQueryParameter=function(_36,_37,_38){
if(_36==null){
return _36;
}
if(!_36.path){
_36=jetspeed.url.parse(_36);
}
if(_36==null){
return null;
}
_36.jsQParamN=null;
var _39=_36.query;
var _3a=((_39!=null)?_39.length:0);
if(_3a>0){
if(_38){
_39=null;
}else{
if(_37==null){
return _36;
}else{
var _3b=_37;
var _3c=_39.indexOf(_3b);
if(_3c==0){
_39=jetspeed.url._removeQP(_39,_3a,_3b,_3c);
}
_3b="&"+_37;
while(true){
_3a=((_39!=null)?_39.length:0);
_3c=_39.indexOf(_3b,0);
if(_3c==-1){
break;
}
var _3d=jetspeed.url._removeQP(_39,_3a,_3b,_3c);
if(_3d==_39){
break;
}
_39=_3d;
}
if(_39.length>0){
if(_39.charCodeAt(0)==38){
_39=((_39.length>1)?_39.substring(1):"");
}
if(_39.length>0&&_39.charCodeAt(0)==63){
_39=((_39.length>1)?_39.substring(1):"");
}
}
}
}
_36.query=_39;
var _3e=new jetspeed.url.JSUri(_36);
_36=jetspeed.url.parse(_3e);
}
return _36;
};
jetspeed.url._removeQP=function(_3f,_40,_41,_42){
if(_42==-1){
return _3f;
}
if(_40>(_42+_41.length)){
var _43=_3f.charCodeAt(_42+_41.length);
if(_43==61){
var _44=_3f.indexOf("&",_42+_41.length+1);
if(_44!=-1){
if(_42>0){
_3f=_3f.substring(0,_42)+_3f.substring(_44);
}else{
_3f=((_44<(_40-1))?_3f.substring(_44):"");
}
}else{
if(_42>0){
_3f=_3f.substring(0,_42);
}else{
_3f="";
}
}
}else{
if(_43==38){
if(_42>0){
_3f=_3f.substring(0,_42)+_3f.substring(_42+_41.length);
}else{
_3f=_3f.substring(_42+_41.length);
}
}
}
}else{
if(_40==(_42+_41.length)){
_3f="";
}
}
return _3f;
};
jetspeed.url.getQueryParameter=function(_45,_46){
if(_45==null){
return null;
}
if(!_45.authority||!_45.scheme){
_45=jetspeed.url.parse(_45);
}
if(_45==null){
return null;
}
if(_45.jsQParamN==null&&_45.query){
var _47=new Array();
var _48=_45.query.split("&");
for(var i=0;i<_48.length;i++){
if(_48[i]==null){
_48[i]="";
}
var _4a=_48[i].indexOf("=");
if(_4a>0&&_4a<(_48[i].length-1)){
_47[i]=unescape(_48[i].substring(_4a+1));
_48[i]=unescape(_48[i].substring(0,_4a));
}else{
_47[i]="";
}
}
_45.jsQParamN=_48;
_45.jsQParamV=_47;
}
if(_45.jsQParamN!=null){
for(var i=0;i<_45.jsQParamN.length;i++){
if(_45.jsQParamN[i]==_46){
return _45.jsQParamV[i];
}
}
}
return null;
};
jetspeed.om.Id=function(){
var _4b="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_4b.length>0){
_4b+="-";
}
_4b+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _4d in arguments[i]){
this[_4d]=arguments[i][_4d];
}
}
}
}
this.id=_4b;
};
jetspeed.om.Id.prototype={getId:function(){
return this.id;
}};
if(window.dojo){
jetspeed.url.BindArgs=function(_4e){
dojo.lang.mixin(this,_4e);
if(!this.mimetype){
this.mimetype="text/html";
}
};
dojo.lang.extend(jetspeed.url.BindArgs,{createIORequest:function(){
var _4f=new dojo.io.Request(this.url,this.mimetype);
_4f.fromKwArgs(this);
return _4f;
},load:function(_50,_51,_52){
try{
var _53=null;
if(this.debugContentDumpIds){
_53=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():((this.domainModelObject&&this.domainModelObject.id)?String(this.domainModelObject.id):""));
for(var _54=0;_54<this.debugContentDumpIds.length;_54++){
if(_53.match(new RegExp(this.debugContentDumpIds[_54]))){
if(dojo.lang.isString(_51)){
dojo.debug("retrieveContent ["+(_53?_53:this.url)+"] content: "+_51);
}else{
var _55=dojo.dom.innerXML(_51);
if(!_55){
_55=(_51!=null?"!= null (IE no XMLSerializer)":"null");
}
dojo.debug("retrieveContent ["+(_53?_53:this.url)+"] xml-content: "+_55);
}
}
}
}
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifySuccess)){
this.contentListener.notifySuccess(_51,this.url,this.domainModelObject,_52);
}else{
_53=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():"");
dojo.debug("retrieveContent ["+(_53?_53:this.url)+"] no valid contentListener");
}
if(this.hideLoadingIndicator){
jetspeed.url.loadingIndicatorHide();
}
}
catch(e){
if(this.hideLoadingIndicator){
jetspeed.url.loadingIndicatorHide();
}
dojo.raise("dojo.io.bind "+jetspeed.formatError(e));
}
},error:function(_56,_57){
try{
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifyFailure)){
this.contentListener.notifyFailure(_56,_57,this.url,this.domainModelObject);
}
if(this.hideLoadingIndicator){
jetspeed.url.loadingIndicatorHide();
}
}
catch(e){
if(this.hideLoadingIndicator){
jetspeed.url.loadingIndicatorHide();
}
throw e;
}
}});
jetspeed.url.retrieveContent=function(_58,_59,_5a,_5b){
if(!_58){
_58={};
}
_58.contentListener=_59;
_58.domainModelObject=_5a;
_58.debugContentDumpIds=_5b;
var _5c=new jetspeed.url.BindArgs(_58);
if(_58.showLoadingIndicator||(_59&&!_59.suppressLoadingIndicator&&_58.showLoadingIndicator!=false)){
if(jetspeed.url.loadingIndicatorShow()){
_5c.hideLoadingIndicator=true;
}
}
dojo.io.bind(_5c.createIORequest());
};
jetspeed.url.checkAjaxApiResponse=function(_5d,_5e,_5f,_60,_61){
var _62=false;
var _63=_5e.getElementsByTagName("status");
if(_63!=null){
var _64=_63[0].firstChild.nodeValue;
if(_64=="success"){
_62=true;
}
}
if((!_62&&_5f)||_61){
var _65=dojo.dom.innerXML(_5e);
if(!_65){
_65=(_5e!=null?"!= null (IE no XMLSerializer)":"null");
}
if(_60==null){
_60="ajax-api";
}
if(_62){
dojo.debug(_60+" success  url="+_5d+"  xml-content="+_65);
}else{
dojo.raise(_60+" failure  url="+_5d+"  xml-content="+_65);
}
}
return _62;
};
jetspeed.url.loadingIndicatorShow=function(_66){
if(typeof _66=="undefined"){
_66="loadpage";
}
var _67=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_67!=null&&_67.style){
var _68=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_68=jetspeed.prefs.desktopActionLabels[_66];
}
if(_68!=null&&_68.length>0&&_67.style["display"]=="none"){
_67.style["display"]="";
if(_66!=null){
if(_68!=null&&_68.length>0){
var _69=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID+"-content");
if(_69!=null){
_69.innerHTML=_68;
}
}
}
return true;
}
}
return false;
};
jetspeed.url.loadingIndicatorHide=function(){
var _6a=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_6a!=null&&_6a.style){
_6a.style["display"]="none";
}
};
}
jetspeed.initcommon();

