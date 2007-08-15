dojo.provide("jetspeed.widget.PageEditPane");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");
dojo.require("dojo.html.common");
dojo.require("dojo.html.display");
jetspeed.widget.PageEditPane=function(){
};
dojo.widget.defineWidget("jetspeed.widget.PageEditPane",dojo.widget.HtmlWidget,{pageEditContainer:null,pageEditLDContainer:null,pageEditPDContainer:null,deletePageDialog:null,deletePageDialogBg:null,deletePageDialogFg:null,createPageDialog:null,createPageDialogBg:null,createPageDialogFg:null,layoutDecoratorSelect:null,portletDecoratorSelect:null,isContainer:true,widgetsInTemplate:true,layoutDecoratorDefinitions:null,portletDecoratorDefinitions:null,postMixInProperties:function(_1,_2,_3){
jetspeed.widget.PageEditPane.superclass.postMixInProperties.apply(this,arguments);
this.templateCssPath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditPane.css");
this.templatePath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditPane.html");
},fillInTemplate:function(_4,_5){
var _6=this;
this.deletePageDialog=dojo.widget.createWidget("dialog",{widgetsInTemplate:true,deletePageConfirmed:function(){
this.hide();
_6.deletePageConfirmed();
}},this.deletePageDialog);
this.deletePageDialog.setCloseControl(this.deletePageDialog.deletePageCancel.domNode);
var _7={};
_7.widgetsInTemplate=true;
_7.createPageConfirmed=function(){
var _8=this.createPageNameTextbox.textbox.value;
var _9=this.createPageTitleTextbox.textbox.value;
var _a=this.createPageShortTitleTextbox.textbox.value;
this.hide();
_6.createPageConfirmed(_8,_9,_a);
};
this.createPageDialog=dojo.widget.createWidget("dialog",_7,this.createPageDialog);
this.createPageDialog.setCloseControl(this.createPageDialog.createPageCancel.domNode);
jetspeed.widget.PageEditPane.superclass.fillInTemplate.call(this);
},destroy:function(){
if(this.deletePageDialog!=null){
this.deletePageDialog.destroy();
}
if(this.createPageDialog!=null){
this.createPageDialog.destroy();
}
jetspeed.widget.PageEditPane.superclass.destroy.apply(this,arguments);
},postCreate:function(_b,_c,_d){
jetspeed.widget.PageEditPane.superclass.postCreate.apply(this,arguments);
if(!dojo.render.html.ie){
if(this.pageEditContainer!=null){
this.pageEditContainer.style.backgroundColor="#d3d3d3";
}
if(this.pageEditLDContainer!=null){
this.pageEditLDContainer.style.backgroundColor="#eeeeee";
}
if(this.pageEditPDContainer!=null){
this.pageEditPDContainer.style.backgroundColor="#eeeeee";
}
}
if(this.layoutDecoratorSelect!=null){
var _e=jetspeed.page.layoutDecorator;
var _f=[];
if(this.layoutDecoratorDefinitions){
for(var i=0;i<this.layoutDecoratorDefinitions.length;i++){
var _11=this.layoutDecoratorDefinitions[i];
if(_11&&_11.length==2){
_f.push([_11[0],_11[1]]);
if(_e==_11[1]){
this.layoutDecoratorSelect.setAllValues(_11[0],_11[1]);
}
}
}
}
this.layoutDecoratorSelect.dataProvider.setData(_f);
}
if(this.portletDecoratorSelect!=null){
var _12=jetspeed.page.portletDecorator;
var _13=[];
if(this.portletDecoratorDefinitions){
for(var i=0;i<this.portletDecoratorDefinitions.length;i++){
var _14=this.portletDecoratorDefinitions[i];
if(_14&&_14.length==2){
_13.push([_14[0],_14[1]]);
if(_12==_14[1]){
this.portletDecoratorSelect.setAllValues(_14[0],_14[1]);
}
}
}
}
this.portletDecoratorSelect.dataProvider.setData(_13);
}
},deletePage:function(){
this.deletePageDialog.show();
},deletePageConfirmed:function(){
var _15=new jetspeed.widget.RemovePageContentManager(this.pageEditorWidget);
_15.getContent();
},createPage:function(){
this.createPageDialog.show();
},createPageConfirmed:function(_16,_17,_18){
if(_16!=null&&_16.length>0){
var _19=jetspeed.page.getPageDirectory(true)+_16;
var _1a=jetspeed.page.getPageDirectory()+_16;
var _1b=new jetspeed.widget.AddPageContentManager(_19,_1a,_16,null,_17,_18,this.pageEditorWidget);
_1b.getContent();
}
},changeLayoutDecorator:function(){
var _1c=new jetspeed.widget.UpdatePageInfoContentManager(this.layoutDecoratorSelect.getValue(),null,this.pageEditorWidget);
_1c.getContent();
},changePortletDecorator:function(){
var _1d=new jetspeed.widget.UpdatePageInfoContentManager(null,this.portletDecoratorSelect.getValue(),this.pageEditorWidget);
_1d.getContent();
},editModeRedisplay:function(){
this.show();
}});

