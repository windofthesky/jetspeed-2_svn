dojo.provide("jetspeed.widget.LayoutEditPane");
dojo.provide("jetspeed.widget.LayoutEditPaneMoveHandle");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");
dojo.require("dojo.html.common");
dojo.require("dojo.html.display");
jetspeed.widget.LayoutEditPane=function(){
};
dojo.widget.defineWidget("jetspeed.widget.LayoutEditPane",dojo.widget.HtmlWidget,{layoutId:null,layoutDefinitions:null,pageEditContainer:null,pageEditLNContainer:null,layoutNameSelect:null,deleteLayoutButton:null,editModeLayoutMoveButton:null,editModeNormalButton:null,layoutMoveContainer:null,isContainer:true,widgetsInTemplate:true,isLayoutPane:true,containingColumn:null,windowPositionStatic:true,postMixInProperties:function(_1,_2,_3){
jetspeed.widget.LayoutEditPane.superclass.postMixInProperties.apply(this,arguments);
this.templateCssPath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/LayoutEditPane.css");
this.templatePath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/LayoutEditPane.html");
},fillInTemplate:function(_4,_5){
jetspeed.widget.LayoutEditPane.superclass.fillInTemplate.call(this);
},getCurrentLayout:function(){
var _6=null;
if(this.layoutId!=null){
_6=jetspeed.page.layouts[this.layoutId];
}
return _6;
},postCreate:function(_7,_8,_9){
if(!jetspeed.browser_IE){
if(this.pageEditContainer!=null){
this.pageEditContainer.style.backgroundColor="#d3d3d3";
}
if(this.pageEditLNContainer!=null){
this.pageEditLNContainer.style.backgroundColor="#eeeeee";
}
}
if(this.layoutNameSelect!=null){
var _a=this.getCurrentLayout();
var _b=null;
if(_a!=null){
_b=_a.name;
}
var _c=[];
if(this.layoutDefinitions){
for(var i=0;i<this.layoutDefinitions.length;i++){
var _e=this.layoutDefinitions[i];
if(_e&&_e.length==2){
_c.push([_e[0],_e[1]]);
if(_b==_e[1]){
this.layoutNameSelect.setAllValues(_e[0],_e[1]);
}
}
}
}
this.layoutNameSelect.dataProvider.setData(_c);
}
this.syncButtons();
this.layoutMoveContainer=dojo.widget.createWidget("jetspeed:LayoutEditPaneMoveHandle",{layoutImagesRoot:this.layoutImagesRoot});
this.addChild(this.layoutMoveContainer);
this.domNode.appendChild(this.layoutMoveContainer.domNode);
},initializeDrag:function(){
this.containingColumn=this.getContainingColumn();
this.drag=new dojo.dnd.Moveable(this,{handle:this.layoutMoveContainer.domNode});
},changeLayout:function(){
var _f=new jetspeed.widget.UpdateFragmentContentManager(this.layoutId,this.layoutNameSelect.getValue(),null,this.pageEditorWidget);
_f.getContent();
},openColumnSizeEditor:function(){
this.pageEditorWidget.openColumnSizesEditor(this.layoutId);
},addPortlet:function(){
var _10=jetspeed.page.getPagePathAndQuery();
_10=jetspeed.url.addQueryParameter(_10,jetspeed.id.PG_ED_PARAM,"true",true);
jetspeed.page.addPortletInitiate(this.layoutId,_10.toString());
},addLayout:function(){
var _11=this.getCurrentLayout();
if(_11!=null){
var _12=new jetspeed.widget.AddLayoutContentManager(this.layoutId,_11.name,this.pageEditorWidget);
_12.getContent();
}else{
alert("Cannot add layout (error: null parent layout).");
}
},deleteLayout:function(){
this.pageEditorWidget.deleteLayout(this.layoutId);
},editModeNormal:function(){
this.pageEditorWidget.editModeNormal();
if(this.editModeLayoutMoveButton!=null){
this.editModeLayoutMoveButton.domNode.style.display="block";
}
if(this.editModeNormalButton!=null){
this.editModeNormalButton.domNode.style.display="none";
}
},editModeLayoutMove:function(){
this.pageEditorWidget.editModeLayoutMove();
if(this.editModeLayoutMoveButton!=null){
this.editModeLayoutMoveButton.domNode.style.display="none";
}
if(this.editModeNormalButton!=null){
this.editModeNormalButton.domNode.style.display="block";
}
},endDragging:function(){
if(this.drag==null||this.containingColumn==null||this.containingColumn.domNode==null){
return;
}
var _13=this.drag.beforeDragColumnRowInfo;
if(_13!=null){
var _14=jetspeed.page.getPortletCurrentColumnRow(this.containingColumn.domNode);
if(_14!=null&&(_14.row!=_13.row||_14.column!=_13.column||_14.layout!=_13.layout)){
}
}
},getContainingColumn:function(){
return jetspeed.page.getColumnContainingNode(this.domNode);
},getPageColumnIndex:function(){
return jetspeed.page.getColumnIndexContainingNode(this.domNode);
},editModeRedisplay:function(){
this.show();
this.syncButtons();
},syncButtons:function(){
if(this.isRootLayout){
if(this.deleteLayoutButton!=null){
this.deleteLayoutButton.domNode.style.display="none";
}
if(this.editModeLayoutMoveButton!=null){
this.editModeLayoutMoveButton.domNode.style.display="block";
}
if(this.editModeNormalButton!=null){
this.editModeNormalButton.domNode.style.display="none";
}
}else{
if(this.editModeLayoutMoveButton!=null){
this.editModeLayoutMoveButton.domNode.style.display="none";
}
if(this.editModeNormalButton!=null){
this.editModeNormalButton.domNode.style.display="none";
}
}
}});
dojo.widget.defineWidget("jetspeed.widget.LayoutEditPaneMoveHandle",dojo.widget.HtmlWidget,{templateString:"<span class=\"layoutMoveContainer\"><img src=\"${this.layoutImagesRoot}layout_move.png\"></span>"});

