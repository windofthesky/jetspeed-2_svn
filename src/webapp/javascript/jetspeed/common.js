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
jetspeed.version={major:2,minor:1,patch:0,flag:"dev",revision:"",toString:function(){
with(jetspeed.version){
return major+"."+minor+"."+patch+flag+" ("+revision+")";
}
}};
if(!window.dojo){
jetspeed.no_dojo_load_notifying=false;
jetspeed.no_dojo_post_load=false;
jetspeed.pageLoadedListeners=[];
window.onload=function(){
if(!window.dojo){
jetspeed.no_dojo_load_notifying=true;
jetspeed.no_dojo_post_load=true;
var _1=jetspeed.pageLoadedListeners;
for(var x=0;x<_1.length;x++){
_1[x]();
}
jetspeed.pageLoadedListeners=[];
}
};
}
jetspeed.addOnLoad=function(_3,_4){
if(window.dojo){
if(arguments.length==1){
dojo.addOnLoad(_3);
}else{
dojo.addOnLoad(_3,_4);
}
}else{
if(arguments.length==1){
jetspeed.pageLoadedListeners.push(_3);
}else{
if(arguments.length>1){
jetspeed.pageLoadedListeners.push(function(){
_3[_4]();
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
jetspeed.printobj=function(_5,_6,_7,_8){
var _9=[];
for(var _a in _5){
try{
var _b=_5[_a];
if(_8){
if(dojo.lang.isArray(_b)){
_b="["+_b.length+"]";
}
}
_b=_b+"";
if(!_7||_b.length>0){
_9.push(_a+": "+_b);
}
}
catch(E){
_9.push(_a+": ERROR - "+E.message);
}
}
_9.sort();
var _c="";
for(var i=0;i<_9.length;i++){
if(_c.length>0){
_c+=(_6?", ":"\r\n");
}
_c+=_9[i];
}
return _c;
};
jetspeed.println=function(_e){
try{
var _f=jetspeed.getDebugElement();
if(!_f){
_f=document.getElementsByTagName("body")[0]||document.body;
}
var div=document.createElement("div");
div.appendChild(document.createTextNode(_e));
_f.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_e+"</div>");
}
catch(e2){
window.status=_e;
}
}
};
jetspeed.debugNodeTree=function(_11,_12){
if(!_11){
return;
}
if(_12){
if(_12.length>0){
jetspeed.println(_12);
}
}else{
jetspeed.println("node: ");
}
if(_11.nodeType!=1&&_11.nodeType!=3){
if(_11.length&&_11.length>0&&(_11[0].nodeType==1||_11[0].nodeType==3)){
for(var i=0;i<_11.length;i++){
jetspeed.debugNodeTree(_11[i]," ["+i+"]");
}
}else{
jetspeed.println(" node is not a node! "+_11.length);
}
return;
}
if(_11.innerXML){
jetspeed.println(_11.innerXML);
}else{
if(_11.xml){
jetspeed.println(_11.xml);
}else{
if(typeof XMLSerializer!="undefined"){
jetspeed.println((new XMLSerializer()).serializeToString(_11));
}else{
jetspeed.println(" node != null (IE no XMLSerializer)");
}
}
}
};
jetspeed.debugShallow=function(obj,_15){
if(_15){
jetspeed.println(_15);
}else{
jetspeed.println("Object: "+obj);
}
var _16=[];
for(var _17 in obj){
try{
_16.push(_17+": "+obj[_17]);
}
catch(E){
_16.push(_17+": ERROR - "+E.message);
}
}
_16.sort();
for(var i=0;i<_16.length;i++){
jetspeed.println(_16[i]);
}
};
jetspeed.getDebugElement=function(_19){
var _1a=null;
try{
var _1a=document.getElementById("debug_container");
if(!_1a){
var _1b=document.getElementsByTagName("body")[0]||document.body;
var _1a=document.createElement("div");
_1a.setAttribute("id","debug_container");
_1b.appendChild(_1a);
}else{
if(_19){
_1a.innerHTML="";
}
}
}
catch(e){
try{
}
catch(e2){
}
}
return _1a;
};
if(window.djConfig!=null&&window.djConfig.isDebug){
var ch=String.fromCharCode(160);
jetspeed.debugindentch=ch;
jetspeed.debugindentH=ch+ch;
jetspeed.debugindent=ch+ch+ch+ch;
jetspeed.debugindent2=jetspeed.debugindent+jetspeed.debugindent;
jetspeed.debugindent3=jetspeed.debugindent+jetspeed.debugindent+jetspeed.debugindent;
}
jetspeed.url.LOADING_INDICATOR_ID="js-showloading";
jetspeed.url.path={SERVER:null,JETSPEED:null,AJAX_API:null,DESKTOP:null,PORTAL:null,PORTLET:null,ACTION:null,RENDER:null,initialized:false};
jetspeed.url.pathInitialize=function(_1c){
if(!_1c&&jetspeed.url.path.initialized){
return;
}
var _1d=document.getElementsByTagName("base");
var _1e=null;
if(_1d&&_1d.length==1){
_1e=_1d[0].href;
}else{
_1e=window.location.href;
}
var _1f=jetspeed.url.parse(_1e);
var _20=_1f.path;
var _21=-1;
for(var _22=1;_21<=_22;_22++){
_21=_20.indexOf("/",_22);
if(_21==-1){
break;
}
}
var _23="";
if(_1f.scheme!=null){
_23+=_1f.scheme+":";
}
if(_1f.authority!=null){
_23+="//"+_1f.authority;
}
var _24=null;
if(_21==-1){
_24=_20;
}else{
_24=_20.substring(0,_21);
}
jetspeed.url.path.JETSPEED=_24;
jetspeed.url.path.SERVER=_23;
jetspeed.url.path.AJAX_API=jetspeed.url.path.JETSPEED+"/ajaxapi";
jetspeed.url.path.DESKTOP=jetspeed.url.path.JETSPEED+"/desktop";
jetspeed.url.path.PORTAL=jetspeed.url.path.JETSPEED+"/portal";
jetspeed.url.path.PORTLET=jetspeed.url.path.JETSPEED+"/portlet";
jetspeed.url.path.ACTION=jetspeed.url.path.JETSPEED+"/action";
jetspeed.url.path.RENDER=jetspeed.url.path.JETSPEED+"/render";
jetspeed.url.path.initialized=true;
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
var _27="^(([^:/?#]+):)?(//([^/?#]*))?([^?#]*)(\\?([^#]*))?(#(.*))?$";
var r=url.toString().match(new RegExp(_27));
var _29={};
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
var _2b=window.djConfig.jetspeed.servletPath;
if(_2b!=null&&_2b.toLowerCase().indexOf("/desktop")==0){
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
jetspeed.url.addPath=function(url,_2d){
if(_2d==null||_2d.length==0){
return url;
}
var _2e=new jetspeed.url.JSUri(url);
var _2f=_2e.path;
if(_2f!=null&&_2f.length>0){
if(_2e.path.charCodeAt(_2f.length-1)==47){
if(_2d.charCodeAt(0)==47){
if(_2d.length>1){
_2e.path+=_2d.substring(1);
}
}else{
_2e.path+=_2d;
}
}else{
if(_2d.charCodeAt(0)==47){
_2e.path+=_2d;
}else{
if(_2d.length>1){
_2e.path+="/"+_2d;
}
}
}
}
var _30=jetspeed.url.parse(_2e);
return _30.toString();
};
jetspeed.url.validateUrlStartsWithHttp=function(url){
if(url){
var len=url.length;
var _33=jetspeed.url.scheme.HTTPS_PREFIX_LEN;
if(len>_33){
var _34=jetspeed.url.scheme.HTTP_PREFIX_LEN;
if(url.substring(0,_34)==jetspeed.url.scheme.HTTP_PREFIX){
return true;
}
if(url.substring(0,_33)==jetspeed.url.scheme.HTTPS_PREFIX){
return true;
}
}
}
return false;
};
jetspeed.url.addQueryParameter=function(_35,_36,_37,_38){
if(_35==null){
return _35;
}
if(!_35.path){
_35=jetspeed.url.parse(_35);
}
if(_35==null){
return null;
}
if(_36==null){
return _35;
}
_35.jsQParamN=null;
if(_38){
_35=jetspeed.url.removeQueryParameter(_35,_36,false);
}
var _39=_35.query;
if(_39==null){
_39="";
}
var _3a=_39.length;
if(_3a>0){
_39+="&";
}
_39+=_36+"="+(_37!=null?_37:"");
_35.query=_39;
var _3b=new jetspeed.url.JSUri(_35);
_35=jetspeed.url.parse(_3b);
return _35;
};
jetspeed.url.removeAllQueryParameters=function(_3c){
return jetspeed.url.removeQueryParameter(_3c,null,true);
};
jetspeed.url.removeQueryParameter=function(_3d,_3e,_3f){
if(_3d==null){
return _3d;
}
if(!_3d.path){
_3d=jetspeed.url.parse(_3d);
}
if(_3d==null){
return null;
}
_3d.jsQParamN=null;
var _40=_3d.query;
var _41=((_40!=null)?_40.length:0);
if(_41>0){
if(_3f){
_40=null;
}else{
if(_3e==null){
return _3d;
}else{
var _42=_3e;
var _43=_40.indexOf(_42);
if(_43==0){
_40=jetspeed.url._removeQP(_40,_41,_42,_43);
}
_42="&"+_3e;
while(true){
_41=((_40!=null)?_40.length:0);
_43=_40.indexOf(_42,0);
if(_43==-1){
break;
}
var _44=jetspeed.url._removeQP(_40,_41,_42,_43);
if(_44==_40){
break;
}
_40=_44;
}
if(_40.length>0){
if(_40.charCodeAt(0)==38){
_40=((_40.length>1)?_40.substring(1):"");
}
if(_40.length>0&&_40.charCodeAt(0)==63){
_40=((_40.length>1)?_40.substring(1):"");
}
}
}
}
_3d.query=_40;
var _45=new jetspeed.url.JSUri(_3d);
_3d=jetspeed.url.parse(_45);
}
return _3d;
};
jetspeed.url._removeQP=function(_46,_47,_48,_49){
if(_49==-1){
return _46;
}
if(_47>(_49+_48.length)){
var _4a=_46.charCodeAt(_49+_48.length);
if(_4a==61){
var _4b=_46.indexOf("&",_49+_48.length+1);
if(_4b!=-1){
if(_49>0){
_46=_46.substring(0,_49)+_46.substring(_4b);
}else{
_46=((_4b<(_47-1))?_46.substring(_4b):"");
}
}else{
if(_49>0){
_46=_46.substring(0,_49);
}else{
_46="";
}
}
}else{
if(_4a==38){
if(_49>0){
_46=_46.substring(0,_49)+_46.substring(_49+_48.length);
}else{
_46=_46.substring(_49+_48.length);
}
}
}
}else{
if(_47==(_49+_48.length)){
_46="";
}
}
return _46;
};
jetspeed.url.getQueryParameter=function(_4c,_4d){
if(_4c==null){
return null;
}
if(!_4c.authority||!_4c.scheme){
_4c=jetspeed.url.parse(_4c);
}
if(_4c==null){
return null;
}
if(_4c.jsQParamN==null&&_4c.query){
var _4e=new Array();
var _4f=_4c.query.split("&");
for(var i=0;i<_4f.length;i++){
if(_4f[i]==null){
_4f[i]="";
}
var _51=_4f[i].indexOf("=");
if(_51>0&&_51<(_4f[i].length-1)){
_4e[i]=unescape(_4f[i].substring(_51+1));
_4f[i]=unescape(_4f[i].substring(0,_51));
}else{
_4e[i]="";
}
}
_4c.jsQParamN=_4f;
_4c.jsQParamV=_4e;
}
if(_4c.jsQParamN!=null){
for(var i=0;i<_4c.jsQParamN.length;i++){
if(_4c.jsQParamN[i]==_4d){
return _4c.jsQParamV[i];
}
}
}
return null;
};
if(window.dojo){
jetspeed.url.BindArgs=function(_52){
dojo.lang.mixin(this,_52);
if(!this.mimetype){
this.mimetype="text/html";
}
};
dojo.lang.extend(jetspeed.url.BindArgs,{createIORequest:function(){
var _53=new dojo.io.Request(this.url,this.mimetype);
_53.fromKwArgs(this);
return _53;
},load:function(_54,_55,_56){
try{
var _57=null;
if(this.debugContentDumpIds){
_57=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():"");
for(var _58=0;_58<this.debugContentDumpIds.length;_58++){
if(_57.match(new RegExp(this.debugContentDumpIds[_58]))){
if(dojo.lang.isString(_55)){
dojo.debug("retrieveContent ["+(_57?_57:this.url)+"] content: "+_55);
}else{
var _59=dojo.dom.innerXML(_55);
if(!_59){
_59=(_55!=null?"!= null (IE no XMLSerializer)":"null");
}
dojo.debug("retrieveContent ["+(_57?_57:this.url)+"] xml-content: "+_59);
}
}
}
}
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifySuccess)){
this.contentListener.notifySuccess(_55,this.url,this.domainModelObject,_56);
}else{
_57=((this.domainModelObject&&dojo.lang.isFunction(this.domainModelObject.getId))?this.domainModelObject.getId():"");
dojo.debug("retrieveContent ["+(_57?_57:this.url)+"] no valid contentListener");
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
},error:function(_5a,_5b){
try{
if(this.contentListener&&dojo.lang.isFunction(this.contentListener.notifyFailure)){
this.contentListener.notifyFailure(_5a,_5b,this.url,this.domainModelObject);
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
jetspeed.url.retrieveContent=function(_5c,_5d,_5e,_5f){
if(!_5c){
_5c={};
}
_5c.contentListener=_5d;
_5c.domainModelObject=_5e;
_5c.debugContentDumpIds=_5f;
var _60=new jetspeed.url.BindArgs(_5c);
if(_5c.showLoadingIndicator||(_5d&&!_5d.suppressLoadingIndicator&&_5c.showLoadingIndicator!=false)){
if(jetspeed.url.loadingIndicatorShow()){
_60.hideLoadingIndicator=true;
}
}
dojo.io.bind(_60.createIORequest());
};
jetspeed.url.checkAjaxApiResponse=function(_61,_62,_63,_64,_65){
var _66=false;
var _67=_62.getElementsByTagName("status");
if(_67!=null){
var _68=_67[0].firstChild.nodeValue;
if(_68=="success"){
_66=true;
}
}
if((!_66&&_63)||_65){
var _69=dojo.dom.innerXML(_62);
if(!_69){
_69=(_62!=null?"!= null (IE no XMLSerializer)":"null");
}
if(_64==null){
_64="ajax-api";
}
if(_66){
dojo.debug(_64+" success  url="+_61+"  xml-content="+_69);
}else{
dojo.raise(_64+" failure  url="+_61+"  xml-content="+_69);
}
}
return _66;
};
jetspeed.url.formatBindError=function(_6a){
if(_6a==null){
return "";
}
var msg=" error:";
if(_6a.message!=null){
msg+=" "+_6a.message;
}
if(_6a.number!=null&&_6a.number!="0"){
msg+=" ("+_6a.number;
if(_6a.type!=null&&_6a.type!="unknown"){
msg+="/"+_6a.type;
}
msg+=")";
}else{
if(_6a.type!=null&&_6a.type!="unknown"){
msg+=" ("+_6a.type+")";
}
}
return msg;
};
jetspeed.url.loadingIndicatorShow=function(_6c){
if(typeof _6c=="undefined"){
_6c="loadpage";
}
var _6d=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_6d!=null&&_6d.style){
var _6e=null;
if(jetspeed.prefs!=null&&jetspeed.prefs.desktopActionLabels!=null){
_6e=jetspeed.prefs.desktopActionLabels[_6c];
}
if(_6e!=null&&_6e.length>0&&_6d.style["display"]=="none"){
_6d.style["display"]="";
if(_6c!=null){
if(_6e!=null&&_6e.length>0){
var _6f=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID+"-content");
if(_6f!=null){
_6f.innerHTML=_6e;
}
}
}
return true;
}
}
return false;
};
jetspeed.url.loadingIndicatorHide=function(){
var _70=document.getElementById(jetspeed.url.LOADING_INDICATOR_ID);
if(_70!=null&&_70.style){
_70.style["display"]="none";
}
};
}

