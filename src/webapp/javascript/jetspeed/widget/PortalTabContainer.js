dojo.provide("jetspeed.widget.PortalTabContainer");
dojo.require("jetspeed.desktop.core");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.TabContainer");
jetspeed.widget.PortalTabContainer=function(){
this.widgetType="PortalTabContainer";
this.js_addingTab=false;
this.doLayout=false;
this.selectedChildWidget=true;
this.tabsadded=0;
dojo.widget.TabContainer.call(this);
};
dojo.inherits(jetspeed.widget.PortalTabContainer,dojo.widget.TabContainer);
dojo.lang.extend(jetspeed.widget.PortalTabContainer,{postMixInProperties:function(_1,_2,_3){
this.templateCssPath=new dojo.uri.Uri(jetspeed.prefs.getLayoutRootUrl()+"/css/PortalTabContainer.css");
jetspeed.widget.PortalTabContainer.superclass.postMixInProperties.call(this,_1,_2,_3);
},postCreate:function(_4,_5,_6){
jetspeed.widget.PortalTabContainer.superclass.postCreate.call(this,_4,_5,_6);
this.contextMenuCreate();
},addTab:function(_7){
if(!_7){
return;
}
this.js_addingTab=true;
var _8=document.createElement("div");
var _9=new dojo.widget.HtmlWidget();
_9.domNode=_8;
_9.menuOption=_7;
_9.label=_7.getShortTitle();
_9.closable=false;
_9.widgetId=this.widgetId+"-tab-"+this.tabsadded;
this.tabsadded++;
this.addChild(_9);
if(jetspeed.page.equalsPageUrl(_7.getUrl())){
this.selectChild(_9);
}
this.js_addingTab=false;
},_setupChild:function(_a){
dojo.event.topic.publish(this.widgetId+"-addChild",_a);
},selectChild:function(_b,_c){
if(this.tablist._currentChild){
var _d=this.tablist.pane2button[this.tablist._currentChild];
_d.clearSelected();
}
var _e=this.tablist.pane2button[_b];
_e.setSelected();
this.tablist._currentChild=_b;
if(!this.js_addingTab&&!_c){
_b.menuOption.navigateTo();
}
},_showChild:function(_f){
_f.selected=true;
},_hideChild:function(_10){
_10.selected=false;
},createJetspeedMenu:function(_11){
if(!_11){
return;
}
var _12=_11.getOptions();
for(var i=0;i<_12.length;i++){
var _14=_12[i];
if(_14.isLeaf()&&_14.getUrl()&&!_14.isSeparator()){
this.addTab(_14);
}
}
},contextMenuCreate:function(){
}});

