dojo.provide("jetspeed.widget.PortalTooltipManager");
dojo.require("dojo.widget.PopupContainer");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.uri.Uri");
dojo.require("dojo.widget.*");
dojo.require("dojo.event.*");
dojo.require("dojo.html.style");
dojo.require("dojo.html.util");
dojo.widget.defineWidget("jetspeed.widget.PortalTooltipManager",[dojo.widget.HtmlWidget,dojo.widget.PopupContainerBase],function(){
this.connections=[];
},{templateString:"<div dojoAttachPoint=\"containerNode\" style=\"display:none;position:absolute;\" class=\"portalTooltip\" ></div>",fillInTemplate:function(_1,_2){
var _3=this.getFragNodeRef(_2);
dojo.html.copyStyle(this.domNode,_3);
this.applyPopupBasicStyle();
},_setCurrent:function(_4){
var _5=this._curr;
if(_5!=null){
_5.close();
}
this._curr=_4;
},open:function(x,y,_8,_9,_a,_b){
dojo.widget.PopupContainerBase.prototype.open.call(this,x,y,_8,_9,_a,_b);
},close:function(_c){
dojo.widget.PopupContainerBase.prototype.close.call(this,_c);
},addNode:function(_d,_e,_f,_10,_11,_12){
var _13=new _10.widget.PortalTooltipDisplay(_d,_e,_f,this,_11,_12);
this.connections.push(_13);
return _13;
},removeNodes:function(_14){
if(_14==null||_14.length==0){
return;
}
for(var i=0;i<_14.length;i++){
_14[i].destroy();
}
var _16=[];
var _17=this.connections;
for(var i=0;i<_17.length;i++){
if(!_17[i].isDestroyed){
_16.push(_17[i]);
}
}
this.connections=_16;
},checkSize:function(){
},uninitialize:function(){
var _18=this.connections;
for(var i=0;i<_18.length;i++){
_18[i].destroy();
}
}});
jetspeed.widget.PortalTooltipDisplay=function(_1a,_1b,_1c,_1d,_1e,_1f){
this.connectNode=_1a;
this.caption=_1b;
this.mouseDownStop=_1c;
this.tooltipMgr=_1d;
this.domNode=_1d.domNode;
_1e.evtConnect("after",_1a,"onmouseover",this,"_onMouseOver",_1f);
if(_1c){
_1e.evtConnect("after",_1a,"onmousedown",this,"_onMouseDown",_1f);
}
};
dojo.lang.extend(jetspeed.widget.PortalTooltipDisplay,{showDelay:750,hideDelay:100,_onMouseOver:function(e){
this._mouse={x:e.pageX,y:e.pageY};
this._abort=false;
this.tooltipMgr._setCurrent(this);
if(!this._tracking){
jetspeed.ui.evtConnect("after",document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=true;
}
this._onHover(e);
},_onMouseMove:function(e){
this._mouse={x:e.pageX,y:e.pageY};
if(dojo.html.overElement(this.connectNode,e)||dojo.html.overElement(this.domNode,e)){
this._onHover(e);
}else{
this._onUnHover(e);
}
},_onMouseDown:function(e){
this._abort=true;
dojo.event.browser.stopEvent(e);
if(this.tooltipMgr.isShowingNow){
this.close();
}
},_onHover:function(e){
if(this._hover){
return;
}
this._hover=true;
if(this._hideTimer){
clearTimeout(this._hideTimer);
delete this._hideTimer;
}
if(!this.tooltipMgr.isShowingNow&&!this._showTimer){
this._showTimer=setTimeout(dojo.lang.hitch(this,"open"),this.showDelay);
}
},_onUnHover:function(e){
if(!this._hover){
return;
}
this._hover=false;
if(this._showTimer){
clearTimeout(this._showTimer);
delete this._showTimer;
}
if(this.tooltipMgr.isShowingNow&&!this._hideTimer){
this._hideTimer=setTimeout(dojo.lang.hitch(this,"close"),this.hideDelay);
}
if(!this.tooltipMgr.isShowingNow){
jetspeed.ui.evtDisconnect("after",document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=false;
}
},open:function(){
if(this.tooltipMgr.isShowingNow||this._abort){
return;
}
this.domNode.innerHTML=this.caption;
this.tooltipMgr.open(this._mouse.x,this._mouse.y,null,[this._mouse.x,this._mouse.y],"TL,TR,BL,BR",[10,15]);
},close:function(){
if(this._showTimer){
clearTimeout(this._showTimer);
delete this._showTimer;
}
if(this._hideTimer){
clearTimeout(this._hideTimer);
delete this._hideTimer;
}
jetspeed.ui.evtDisconnect("after",document.documentElement,"onmousemove",this,"_onMouseMove");
this._tracking=false;
this.tooltipMgr.close();
},_position:function(){
this.tooltipMgr.move.call(this.tooltipMgr,this._mouse.x,this._mouse.y,[10,15],"TL,TR,BL,BR");
},destroy:function(){
if(this.isDestroyed){
return;
}
this.close();
var _25=dojo.event;
var _26=jetspeed.ui;
var _27=this.connectNode;
_26.evtDisconnect("after",_27,"onmouseover",this,"_onMouseOver",_25);
if(this.mouseDownStop){
_26.evtDisconnect("after",_27,"onmousedown",this,"_onMouseDown",_25);
}
this.isDestroyed=true;
}});

