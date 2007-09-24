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
if(!window.dojo){
var jsObj=jetspeed;
jsObj.no_dojo_load_notifying=false;
jsObj.no_dojo_post_load=false;
jsObj.pageLoadedListeners=[];
window.onload=function(){
if(!window.dojo){
var _1=jetspeed;
_1.no_dojo_load_notifying=true;
_1.no_dojo_post_load=true;
var _2=_1.pageLoadedListeners;
for(var x=0;x<_2.length;x++){
_2[x]();
}
_1.pageLoadedListeners=[];
}
};
}else{
var jsObj=jetspeed;
var djRH=dojo.render.html;
if(djRH.mozilla){
jsObj.UAmoz=true;
}else{
if(djRH.ie){
jsObj.UAie=true;
if(djRH.ie60||djRH.ie50||djRH.ie55){
jsObj.UAie6=true;
}
}else{
if(djRH.safari){
jsObj.UAsaf=true;
}else{
if(djRH.opera){
jsObj.UAope=true;
}
}
}
}
}
jetspeed.addOnLoad=function(_4,_5){
if(window.dojo){
if(arguments.length==1){
dojo.addOnLoad(_4);
}else{
dojo.addOnLoad(_4,_5);
}
}else{
if(arguments.length==1){
jetspeed.pageLoadedListeners.push(_4);
}else{
if(arguments.length>1){
jetspeed.pageLoadedListeners.push(function(){
_4[_5]();
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
var _6=jetspeed;
if(_6.docBody==null){
_6.docBody=document.body||document.getElementsByTagName("body")[0];
}
return _6.docBody;
};
jetspeed.url.LOADING_INDICATOR_ID="js-showloading";
jetspeed.url.path={SERVER:null,JETSPEED:null,AJAX_API:null,DESKTOP:null,PORTAL:null,PORTLET:null,ACTION:null,RENDER:null,initialized:false};
jetspeed.url.pathInitialize=function(_7){
var _8=jetspeed.url;
var _9=_8.path;
if(!_7&&_9.initialized){
return;
}
var _a=document.getElementsByTagName("base");
var _b=null;
if(_a&&_a.length==1){
_b=_a[0].href;
}else{
_b=window.location.href;
}
var _c=_8.parse(_b);
var _d=_c.path;
var _e=-1;
for(var _f=1;_e<=_f;_f++){
_e=_d.indexOf("/",_f);
if(_e==-1){
break;
}
}
var _10="";
if(_c.scheme!=null){
_10+=_c.scheme+":";
}
if(_c.authority!=null){
_10+="//"+_c.authority;
}
var _11=null;
if(_e==-1){
_11=_d;
}else{
_11=_d.substring(0,_e);
}
_9.JETSPEED=_11;
_9.SERVER=_10;
_9.AJAX_API=_9.JETSPEED+"/ajaxapi";
_9.DESKTOP=_9.JETSPEED+"/desktop";
_9.PORTAL=_9.JETSPEED+"/portal";
_9.PORTLET=_9.JETSPEED+"/portlet";
_9.ACTION=_9.JETSPEED+"/action";
_9.RENDER=_9.JETSPEED+"/render";
_9.initialized=true;
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
var _14="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=url.toString().match(new RegExp(_14));
var _16={};
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
var _18=window.djConfig.jetspeed.servletPath;
if(_18!=null&&_18.toLowerCase().indexOf("/desktop")==0){
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
jetspeed.url.addPath=function(url,_1a){
if(_1a==null||_1a.length==0){
return url;
}
var _1b=new jetspeed.url.JSUri(url);
var _1c=_1b.path;
if(_1c!=null&&_1c.length>0){
if(_1b.path.charCodeAt(_1c.length-1)==47){
if(_1a.charCodeAt(0)==47){
if(_1a.length>1){
_1b.path+=_1a.substring(1);
}
}else{
_1b.path+=_1a;
}
}else{
if(_1a.charCodeAt(0)==47){
_1b.path+=_1a;
}else{
if(_1a.length>1){
_1b.path+="/"+_1a;
}
}
}
}
var _1d=jetspeed.url.parse(_1b);
return _1d.toString();
};
jetspeed.url.urlStartsWithHttp=function(url){
if(url){
var len=url.length;
var _20=jetspeed.url.scheme.HTTPS_PREFIX_LEN;
if(len>_20){
var _21=jetspeed.url.scheme.HTTP_PREFIX_LEN;
if(url.substring(0,_21)==jetspeed.url.scheme.HTTP_PREFIX){
return true;
}
if(url.substring(0,_20)==jetspeed.url.scheme.HTTPS_PREFIX){
return true;
}
}
}
return false;
};
jetspeed.url.addQueryParameter=function(_22,_23,_24,_25){
if(_22==null){
return _22;
}
if(!_22.path){
_22=jetspeed.url.parse(_22);
}
if(_22==null){
return null;
}
if(_23==null){
return _22;
}
_22.jsQParamN=null;
if(_25){
_22=jetspeed.url.removeQueryParameter(_22,_23,false);
}
var _26=_22.query;
if(_26==null){
_26="";
}
var _27=_26.length;
if(_27>0){
_26+="&";
}
_26+=_23+"="+(_24!=null?_24:"");
_22.query=_26;
var _28=new jetspeed.url.JSUri(_22);
_22=jetspeed.url.parse(_28);
return _22;
};
jetspeed.url.removeAllQueryParameters=function(_29){
return jetspeed.url.removeQueryParameter(_29,null,true);
};
jetspeed.url.removeQueryParameter=function(_2a,_2b,_2c){
if(_2a==null){
return _2a;
}
if(!_2a.path){
_2a=jetspeed.url.parse(_2a);
}
if(_2a==null){
return null;
}
_2a.jsQParamN=null;
var _2d=_2a.query;
var _2e=((_2d!=null)?_2d.length:0);
if(_2e>0){
if(_2c){
_2d=null;
}else{
if(_2b==null){
return _2a;
}else{
var _2f=_2b;
var _30=_2d.indexOf(_2f);
if(_30==0){
_2d=jetspeed.url._removeQP(_2d,_2e,_2f,_30);
}
_2f="&"+_2b;
while(true){
_2e=((_2d!=null)?_2d.length:0);
_30=_2d.indexOf(_2f,0);
if(_30==-1){
break;
}
var _31=jetspeed.url._removeQP(_2d,_2e,_2f,_30);
if(_31==_2d){
break;
}
_2d=_31;
}
if(_2d.length>0){
if(_2d.charCodeAt(0)==38){
_2d=((_2d.length>1)?_2d.substring(1):"");
}
if(_2d.length>0&&_2d.charCodeAt(0)==63){
_2d=((_2d.length>1)?_2d.substring(1):"");
}
}
}
}
_2a.query=_2d;
var _32=new jetspeed.url.JSUri(_2a);
_2a=jetspeed.url.parse(_32);
}
return _2a;
};
jetspeed.url._removeQP=function(_33,_34,_35,_36){
if(_36==-1){
return _33;
}
if(_34>(_36+_35.length)){
var _37=_33.charCodeAt(_36+_35.length);
if(_37==61){
var _38=_33.indexOf("&",_36+_35.length+1);
if(_38!=-1){
if(_36>0){
_33=_33.substring(0,_36)+_33.substring(_38);
}else{
_33=((_38<(_34-1))?_33.substring(_38):"");
}
}else{
if(_36>0){
_33=_33.substring(0,_36);
}else{
_33="";
}
}
}else{
if(_37==38){
if(_36>0){
_33=_33.substring(0,_36)+_33.substring(_36+_35.length);
}else{
_33=_33.substring(_36+_35.length);
}
}
}
}else{
if(_34==(_36+_35.length)){
_33="";
}
}
return _33;
};
jetspeed.url.getQueryParameter=function(_39,_3a){
if(_39==null){
return null;
}
if(!_39.authority||!_39.scheme){
_39=jetspeed.url.parse(_39);
}
if(_39==null){
return null;
}
if(_39.jsQParamN==null&&_39.query){
var _3b=new Array();
var _3c=_39.query.split("&");
for(var i=0;i<_3c.length;i++){
if(_3c[i]==null){
_3c[i]="";
}
var _3e=_3c[i].indexOf("=");
if(_3e>0&&_3e<(_3c[i].length-1)){
_3b[i]=unescape(_3c[i].substring(_3e+1));
_3c[i]=unescape(_3c[i].substring(0,_3e));
}else{
_3b[i]="";
}
}
_39.jsQParamN=_3c;
_39.jsQParamV=_3b;
}
if(_39.jsQParamN!=null){
for(var i=0;i<_39.jsQParamN.length;i++){
if(_39.jsQParamN[i]==_3a){
return _39.jsQParamV[i];
}
}
}
return null;
};
jetspeed.om.Id=function(){
var _3f="";
for(var i=0;i<arguments.length;i++){
if(dojo.lang.isString(arguments[i])){
if(_3f.length>0){
_3f+="-";
}
_3f+=arguments[i];
}else{
if(dojo.lang.isObject(arguments[i])){
for(var _41 in arguments[i]){
this[_41]=arguments[i][_41];
}
}
}
}
this.id=_3f;
};
jetspeed.om.Id.prototype={getId:function(){
return this.id;
}};
if(window.dojo){
jetspeed.url.BindArgs=function(_42){
dojo.lang.mixin(this,_42);
if(!this.mimetype){
this.mimetype="text/html";
}
};
dojo.lang.extend(jetspeed.url.BindArgs,{createIORequest:function(){
var _43=new dojo.io.Request(this.url,this.mimetype);
_43.fromKwArgs(this);
return _43;
},load:function(_44,_45,_46){
try{
var _47=null;
if(this.debugContentDumpIds){
_47=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():((this.domainModelObject&&this.domainModelObject.id)?String(this.domainModelObject.id):""));
for(var _48=0;_48<this.debugContentDumpIds.length;_48++){
if(_47.match(new RegExp(this.debugContentDumpIds[_48]))){
if(dojo.lang.isString(_45)){
dojo.debug("retrieveContent ["+(_47?_47:this.url)+"] content: "+_45);
}else{
var _49=dojo.dom.innerXML(_45);
if(!_49){
_49=(_45!=null?"!= null (IE no XMLSerializer)":"null");
}
dojo.debug("retrieveContent ["+(_47?_47:this.url)+"] xml-content: "+_49);
}
}
}
}
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifySuccess)){
this.contentListener.notifySuccess(_45,this.url,this.domainModelObject,_46);
}else{
_47=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():"");
dojo.debug("retrieveContent ["+(_47?_47:this.url)+"] no valid contentListener");
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
},error:function(_4a,_4b){
try{
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifyFailure)){
this.contentListener.notifyFailure(_4a,_4b,this.url,this.domainModelObject);
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
jetspeed.url.retrieveContent=function(_4c,_4d,_4e,_4f){
if(!_4c){
_4c={};
}
_4c.contentListener=_4d;
_4c.domainModelObject=_4e;
_4c.debugContentDumpIds=_4f;
var _50=new jetspeed.url.BindArgs(_4c);
if(_4c.showLoadingIndicator||(_4d&&!_4d.suppressLoadingIndicator&&_4c.showLoadingIndicator!=false)){
if(jetspeed.url.loadingIndicatorShow()){
_50.hideLoadingIndicator=true;
}
}
dojo.io.bind(_50.createIORequest());
};
jetspeed.url.checkAjaxApiResponse=function(_51,_52,_53,_54,_55){
var _56=false;
var _57=_52.getElementsByTagName("status");
if(_57!=null){
var _58=_57[0].firstChild.nodeValue;
if(_58=="success"){
_56=true;
}
}
if((!_56&&_53)||_55){
var _59=dojo.dom.innerXML(_52);
if(!_59){
_59=(_52!=null?"!= null (IE no XMLSerializer)":"null");
}
if(_54==null){
_54="ajax-api";
}
if(_56){
dojo.debug(_54+" success  url="+_51+"  xml-content="+_59);
}else{
dojo.raise(_54+" failure  url="+_51+"  xml-content="+_59);
}
}
return _56;
};
jetspeed.url.formatBindError=function(_5a){
if(_5a==null){
return "";
}
var msg=" error:";
if(_5a.message!=null){
msg+=" "+_5a.message;
}
if(_5a.number!=null&&_5a.number!="0"){
msg+=" ("+_5a.number;
if(_5a.type!=null&&_5a.type!="unknown"){
msg+="/"+_5a.type;
}
msg+=")";
}else{
if(_5a.type!=null&&_5a.type!="unknown"){
msg+=" ("+_5a.type+")";
}
}
return msg;
};
jetspeed.url.loadingIndicatorShow=function(_5c){
if(typeof _5c=="undefined"){
_5c="loadpage";
}
var _5d=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_5d!=null&&_5d.style){
var _5e=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_5e=jetspeed.prefs.desktopActionLabels[_5c];
}
if(_5e!=null&&_5e.length>0&&_5d.style["display"]=="none"){
_5d.style["display"]="";
if(_5c!=null){
if(_5e!=null&&_5e.length>0){
var _5f=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID+"-content");
if(_5f!=null){
_5f.innerHTML=_5e;
}
}
}
return true;
}
}
return false;
};
jetspeed.url.loadingIndicatorHide=function(){
var _60=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_60!=null&&_60.style){
_60.style["display"]="none";
}
};
}

