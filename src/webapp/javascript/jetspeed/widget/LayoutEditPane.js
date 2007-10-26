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
dojo.widget.defineWidget("jetspeed.widget.LayoutEditPane",dojo.widget.HtmlWidget,{layoutId:null,layoutDefinitions:null,layoutColumn:null,layoutInfo:null,parentLayoutInfo:null,pageEditContainer:null,pageEditLNContainer:null,layoutNameSelect:null,buttonGroupRight:null,deleteLayoutButton:null,editMoveModeButton:null,editMoveModeExitButton:null,layoutMoveContainer:null,isContainer:true,widgetsInTemplate:true,isLayoutPane:true,drag:null,posStatic:true,moveModeLayoutRelative:"movemode_layout",moveModes:["movemode_layout","movemode_portlet"],postMixInProperties:function(_1,_2,_3){
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
var _a=dojo;
var _b=_a.html;
var _c=jetspeed.widget.PageEditor.prototype;
if(this.pageEditContainer!=null){
_b.addClass(this.pageEditContainer,_c.styleBaseAdd);
}
if(this.pageEditLNContainer!=null){
_b.addClass(this.pageEditLNContainer,_c.styleDetailAdd);
}
if(this.layoutNameSelect!=null){
var _d=this.getCurrentLayout();
var _e=null;
if(_d!=null){
_e=_d.name;
}
var _f=[];
if(this.layoutDefinitions){
for(var i=0;i<this.layoutDefinitions.length;i++){
var _11=this.layoutDefinitions[i];
if(_11&&_11.length==2){
_f.push([_11[0],_11[1]]);
if(_e==_11[1]){
this.layoutNameSelect.setAllValues(_11[0],_11[1]);
}
}
}
}
this.layoutNameSelect.dataProvider.setData(_f);
}
this.syncButtons();
this.layoutMoveContainer=_a.widget.createWidget("jetspeed:LayoutEditPaneMoveHandle",{layoutImagesRoot:this.layoutImagesRoot});
this.addChild(this.layoutMoveContainer);
this.domNode.appendChild(this.layoutMoveContainer.domNode);
},changeLayout:function(){
var _12=new jetspeed.widget.UpdateFragmentContentManager(this.layoutId,this.layoutNameSelect.getValue(),null,this.pageEditorWidget);
_12.getContent();
},openColumnSizeEditor:function(){
this.pageEditorWidget.openColumnSizesEditor(this.layoutId);
},addPortlet:function(){
var _13=jetspeed.page.getPagePathAndQuery();
_13=jetspeed.url.addQueryParameter(_13,jetspeed.id.PG_ED_PARAM,"true",true);
jetspeed.page.addPortletInitiate(this.layoutId,_13.toString());
},addLayout:function(){
var _14=this.getCurrentLayout();
if(_14!=null){
var _15=new jetspeed.widget.AddLayoutContentManager(this.layoutId,_14.name,this.pageEditorWidget);
_15.getContent();
}else{
alert("Cannot add layout (error: null parent layout).");
}
},deleteLayout:function(){
this.pageEditorWidget.deleteLayout(this.layoutId);
},editMoveModeExit:function(){
this.pageEditorWidget.editMoveModeExit();
if(this.editMoveModeButton!=null){
this.editMoveModeButton.domNode.style.display="block";
}
if(this.editMoveModeExitButton!=null){
this.editMoveModeExitButton.domNode.style.display="none";
}
},editMoveModeStart:function(){
this.pageEditorWidget.editMoveModeStart();
if(this.editMoveModeButton!=null){
this.editMoveModeButton.domNode.style.display="none";
}
if(this.editMoveModeExitButton!=null){
this.editMoveModeExitButton.domNode.style.display="block";
}
},_enableMoveMode:function(){
if(this.layoutMoveContainer&&this.drag){
this.layoutMoveContainer.domNode.style.display="block";
}
},_disableMoveMode:function(){
if(this.layoutMoveContainer&&this.drag){
this.layoutMoveContainer.domNode.style.display="none";
}
},initializeDrag:function(){
var _16=this.layoutColumn;
if(_16!=null&&_16.domNode!=null){
this.dragStartStaticWidth=_16.domNode.style.width;
this.drag=new dojo.dnd.Moveable(this,{handle:this.layoutMoveContainer.domNode});
}
},startDragging:function(e,_18,_19,_1a){
var _1b=this.layoutColumn;
if(_1b!=null){
var _1c=_1b.domNode;
if(_1c){
if(this.buttonGroupRight){
this.buttonGroupRight.style.display="none";
}
var _1d=true;
_18.beforeDragColRowInfo=_1a.page.getPortletCurColRow(_1c);
_18.node=_1c;
_18.mover=new _19.dnd.Mover(this,_1c,_1b,_18,e,_1d,_19,_1a);
}
}
},dragChangeToAbsolute:function(_1e,_1f,_20,_21,_22){
var _23=_21.getMarginBox(_1f,null,_22);
var _24=400-_20.w;
if(_24<0){
_20.l=_20.l+(_24*-1);
_20.w=400;
_21.setMarginBox(_1f,_20.l,null,_20.w,null,null,_22);
}
if(_22.UAie){
var _25=this.pageEditorWidget.bgIframe.iframe;
this.domNode.appendChild(_25);
_25.style.display="block";
_21.setMarginBox(_25,null,null,null,_20.h,null,_22);
}
},endDragging:function(_26){
var _27=jetspeed;
var _28=dojo;
var _29=this.layoutColumn;
if(this.drag==null||_29==null||_29.domNode==null){
return;
}
var _2a=_29.domNode;
_2a.style.position="static";
_2a.style.width=this.dragStartStaticWidth;
_2a.style.left="auto";
_2a.style.top="auto";
if(this.buttonGroupRight){
this.buttonGroupRight.style.display="block";
}
if(_27.UAie){
this.pageEditorWidget.bgIframe.iframe.style.display="none";
if(_27.UAie6){
_27.page.onBrowserWindowResize();
}
}
var _2b=this.drag.beforeDragColRowInfo;
var _2c=_27.page.getPortletCurColRow(_2a);
if(_2b!=null&&_2c!=null){
var ind=_27.debugindent;
if(_2c!=null&&(_2c.row!=_2b.row||_2c.column!=_2b.column||_2c.layout!=_2b.layout)){
var _2e=new _27.widget.MoveLayoutContentManager(this.layoutId,_2c.layout,_2c.column,_2c.row,this.pageEditorWidget);
_2e.getContent();
}
}
},getLayoutColumn:function(){
return this.layoutColumn;
},getPageColumnIndex:function(){
if(this.layoutColumn){
var _2f=jetspeed.page.getColWithNode(this.layoutColumn.domNode);
if(_2f!=null){
return _2f.getPageColumnIndex();
}
}
return null;
},_getLayoutInfoMoveable:function(){
return this.layoutInfo;
},_getWindowMarginBox:function(_30,_31){
if(this.layoutColumn){
var _32=this.parentLayoutInfo;
if(_31.UAope&&_32==null){
var _33=_31.page.layoutInfo;
var _34=_31.page.getColIndexForNode(this.layoutColumn.domNode);
if(_34!=null){
var _35=_31.page.columns[_34];
if(_35.layoutHeader){
_32=_33.columnLayoutHeader;
}else{
_32=_33.column;
}
}else{
_32=_33.columns;
}
this.parentLayoutInfo=_32;
}
return _31.ui.getMarginBox(this.layoutColumn.domNode,_30,_32,_31);
}
return null;
},editModeRedisplay:function(){
this.show();
this.syncButtons();
},syncButtons:function(){
if(this.isRootLayout){
if(this.deleteLayoutButton!=null){
this.deleteLayoutButton.domNode.style.display="none";
}
if(this.editMoveModeButton!=null){
this.editMoveModeButton.domNode.style.display="block";
}
if(this.editMoveModeExitButton!=null){
this.editMoveModeExitButton.domNode.style.display="none";
}
}else{
if(this.editMoveModeButton!=null){
this.editMoveModeButton.domNode.style.display="none";
}
if(this.editMoveModeExitButton!=null){
this.editMoveModeExitButton.domNode.style.display="none";
}
}
},onBrowserWindowResize:function(){
}});
dojo.widget.defineWidget("jetspeed.widget.LayoutEditPaneMoveHandle",dojo.widget.HtmlWidget,{templateString:"<span class=\"layoutMoveContainer\"><img src=\"${this.layoutImagesRoot}layout_move.png\"></span>"});

