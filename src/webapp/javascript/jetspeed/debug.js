if(window.dojo){
dojo.provide("jetspeed.debug");
}
if(!window.jetspeed){
jetspeed={};
}
jetspeed.dumpary=function(_1,_2){
if(!(_1&&_1.length>=0)){
return null;
}
var _3=jetspeed;
if(_2){
_3.println(_2+"  len="+_1.length);
}
for(var i=0;i<_1.length;i++){
_3.println(_3.debugindentH+"["+i+"]: "+_3.printobj(_1[i]));
}
};
jetspeed.printobj=function(_5,_6,_7,_8,_9,_a){
var _b=[];
for(var _c in _5){
try{
var _d=_5[_c];
if(_8){
if(dojo.lang.isArray(_d)){
_d="["+_d.length+"]";
}
}
if(dojo.lang.isFunction(_d)){
if(!_9){
continue;
}
if(!_a){
_d="function";
}
}
_d=_d+"";
if(!_7||_d.length>0){
_b.push(_c+": "+_d);
}
}
catch(E){
_b.push(_c+": ERROR - "+E.message);
}
}
_b.sort();
var _e="";
for(var i=0;i<_b.length;i++){
if(_e.length>0){
_e+=(_6?", ":"\r\n");
}
_e+=_b[i];
}
return _e;
};
jetspeed.println=function(_10){
try{
var _11=jetspeed.getDebugElement();
var div=document.createElement("div");
div.appendChild(document.createTextNode(_10));
_11.appendChild(div);
}
catch(e){
try{
document.write("<div>"+_10+"</div>");
}
catch(e2){
window.status=_10;
}
}
};
jetspeed.objectKeys=function(obj){
var _14=new Array();
if(obj!=null){
for(var key in obj){
_14.push(key);
}
}
return _14;
};
jetspeed.debugNodes=function(_16){
if(!_16||_16.length==null){
return null;
}
var _17=jetspeed;
var _18=dojo;
var out="",_1a;
var _1b=_16.length;
var _1c=(_1b>=100?3:(_1b>=10?2:1));
for(var i=0;i<_1b;i++){
_1a=_16[i];
out+="\r\n";
out+="["+_18.string.padLeft(String(i),_1c,"0")+"] ";
if(!_1a){
out+="null";
}else{
out+=_17.debugNode(_1a);
}
}
return out;
};
jetspeed.debugNode=function(_1e){
if(!_1e){
return null;
}
return _1e.nodeName+" "+_1e.id+" "+_1e.className;
};
jetspeed.debugNodeTree=function(_1f,_20){
if(!_1f){
return;
}
if(_20){
if(_20.length>0){
jetspeed.println(_20);
}
}else{
jetspeed.println("node: ");
}
if(_1f.nodeType!=1&&_1f.nodeType!=3){
if(_1f.length&&_1f.length>0&&(_1f[0].nodeType==1||_1f[0].nodeType==3)){
for(var i=0;i<_1f.length;i++){
jetspeed.debugNodeTree(_1f[i]," ["+i+"]");
}
}else{
jetspeed.println(" node is not a node! "+_1f.length);
}
return;
}
if(_1f.innerXML){
jetspeed.println(_1f.innerXML);
}else{
if(_1f.xml){
jetspeed.println(_1f.xml);
}else{
if(typeof XMLSerializer!="undefined"){
jetspeed.println((new XMLSerializer()).serializeToString(_1f));
}else{
jetspeed.println(" node != null (IE no XMLSerializer)");
}
}
}
};
jetspeed.debugShallow=function(obj,_23){
if(_23){
jetspeed.println(_23);
}else{
jetspeed.println("Object: "+obj);
}
var _24=[];
for(var _25 in obj){
try{
_24.push(_25+": "+obj[_25]);
}
catch(E){
_24.push(_25+": ERROR - "+E.message);
}
}
_24.sort();
for(var i=0;i<_24.length;i++){
jetspeed.println(_24[i]);
}
};
jetspeed.getDebugElement=function(_27){
var _28=null;
var _29=null;
try{
var _2a=jetspeed.debug.debugContainerId;
_29=document.getElementById(_2a);
if(!_29){
_2a="debug_container";
_29=document.getElementById(_2a);
if(!_29){
_28=jetspeed.docBody;
if(_28==null){
_28=jetspeed.getBody();
}
_29=document.createElement("div");
_29.setAttribute("id","debug_container");
_28.appendChild(_29);
}
}
if(_29&&_27){
_29.innerHTML="";
}
}
catch(e){
try{
if(_29==null){
_29=jetspeed.getBody();
}
}
catch(e2){
}
}
return _29;
};
if(window.djConfig!=null&&window.djConfig.isDebug){
var ch=String.fromCharCode(160);
jetspeed.debugindentch=ch;
jetspeed.debugindentH=ch+ch;
jetspeed.debugindentT=ch+ch+ch;
jetspeed.debugindent=ch+ch+ch+ch;
jetspeed.debugindent2=jetspeed.debugindent+jetspeed.debugindent;
jetspeed.debugindent3=jetspeed.debugindent+jetspeed.debugindent+jetspeed.debugindent;
}

