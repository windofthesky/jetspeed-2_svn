if(window.dojo){
dojo.provide("jetspeed.selector");
dojo.require("jetspeed.common");
}
if(!window.jetspeed){
jetspeed={};
}
if(!jetspeed.selector){
jetspeed.selector={};
}
jetspeed.selector.PortletDef=function(_1,_2,_3,_4,_5){
this.portletName=_1;
this.portletDisplayName=_2;
this.portletDescription=_3;
this.image=_4;
this.count=_5;
};
jetspeed.selector.PortletDef.prototype={portletName:null,portletDisplayName:null,portletDescription:null,portletImage:null,portletCount:null,getId:function(){
return this.portletName;
},getPortletName:function(){
return this.portletName;
},getPortletDisplayName:function(){
return this.portletDisplayName;
},getPortletCount:function(){
return this.portletCount;
},getPortletDescription:function(){
return this.portletDescription;
}};
jetspeed.selector.addNewPortletDefinition=function(_6,_7,_8){
var _9=new jetspeed.selector.PortletAddAjaxApiCallbackCL(_6);
var _a="?action=add&id="+escape(_6.getPortletName());
if(_8!=null&&_8.length>0){
_a+="&layoutid="+escape(_8);
}
var _b=_7+_a;
var _c="text/xml";
var _d=new jetspeed.om.Id("addportlet",{});
jetspeed.url.retrieveContent({url:_b,mimetype:_c},_9,_d,jetspeed.debugContentDumpIds);
};
jetspeed.selector.PortletAddAjaxApiCallbackCL=function(_e){
this.portletDef=_e;
};
jetspeed.selector.PortletAddAjaxApiCallbackCL.prototype={notifySuccess:function(_f,_10,_11){
jetspeed.url.checkAjaxApiResponse(_10,_f,true,"add-portlet");
},parseAddPortletResponse:function(_12){
var _13=null;
var _14=_12.getElementsByTagName("js");
if(!_14||_14.length>1){
dojo.raise("unexpected zero or multiple <js> elements in portlet selector xml");
}
var _15=_14[0].childNodes;
for(var i=0;i<_15.length;i++){
var _17=_15[i];
if(_17.nodeType!=1){
continue;
}
var _18=_17.nodeName;
if(_18=="entity"){
_13=((_17&&_17.firstChild)?_17.firstChild.nodeValue:null);
break;
}
}
return _13;
},notifyFailure:function(_19,_1a,_1b,_1c){
dojo.raise("PortletAddAjaxApiCallbackCL error ["+_1c.toString()+"] url: "+_1b+" type: "+_19+jetspeed.url.formatBindError(_1a));
}};

