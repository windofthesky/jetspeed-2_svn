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
jetspeed.debugNodeTree=function(_13,_14){
if(!_13){
return;
}
if(_14){
if(_14.length>0){
jetspeed.println(_14);
}
}else{
jetspeed.println("node: ");
}
if(_13.nodeType!=1&&_13.nodeType!=3){
if(_13.length&&_13.length>0&&(_13[0].nodeType==1||_13[0].nodeType==3)){
for(var i=0;i<_13.length;i++){
jetspeed.debugNodeTree(_13[i]," ["+i+"]");
}
}else{
jetspeed.println(" node is not a node! "+_13.length);
}
return;
}
if(_13.innerXML){
jetspeed.println(_13.innerXML);
}else{
if(_13.xml){
jetspeed.println(_13.xml);
}else{
if(typeof XMLSerializer!="undefined"){
jetspeed.println((new XMLSerializer()).serializeToString(_13));
}else{
jetspeed.println(" node != null (IE no XMLSerializer)");
}
}
}
};
jetspeed.debugShallow=function(obj,_17){
if(_17){
jetspeed.println(_17);
}else{
jetspeed.println("Object: "+obj);
}
var _18=[];
for(var _19 in obj){
try{
_18.push(_19+": "+obj[_19]);
}
catch(E){
_18.push(_19+": ERROR - "+E.message);
}
}
_18.sort();
for(var i=0;i<_18.length;i++){
jetspeed.println(_18[i]);
}
};
jetspeed.getDebugElement=function(_1b){
var _1c=null;
var _1d=null;
try{
var _1e=jetspeed.debug.debugContainerId;
_1d=document.getElementById(_1e);
if(!_1d){
_1e="debug_container";
_1d=document.getElementById(_1e);
if(!_1d){
_1c=jetspeed.docBody;
if(_1c==null){
_1c=jetspeed.getBody();
}
_1d=document.createElement("div");
_1d.setAttribute("id","debug_container");
_1c.appendChild(_1d);
}
}
if(_1d&&_1b){
_1d.innerHTML="";
}
}
catch(e){
try{
if(_1d==null){
_1d=jetspeed.getBody();
}
}
catch(e2){
}
}
return _1d;
};
if(window.djConfig!=null&&window.djConfig.isDebug){
var ch=String.fromCharCode(160);
jetspeed.debugindentch=ch;
jetspeed.debugindentH=ch+ch;
jetspeed.debugindent=ch+ch+ch+ch;
jetspeed.debugindent2=jetspeed.debugindent+jetspeed.debugindent;
jetspeed.debugindent3=jetspeed.debugindent+jetspeed.debugindent+jetspeed.debugindent;
}

