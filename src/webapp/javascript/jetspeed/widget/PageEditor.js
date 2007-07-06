dojo.provide("jetspeed.widget.PageEditor");
dojo.require("dojo.widget.*");
dojo.require("dojo.io.*");
dojo.require("dojo.event.*");
dojo.require("dojo.string.extras");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("dojo.widget.Dialog");
dojo.require("dojo.widget.Select");
dojo.require("dojo.widget.Button");
dojo.require("dojo.widget.Spinner");
dojo.require("dojo.html.common");
dojo.require("dojo.html.display");
dojo.require("jetspeed.widget.PageEditPane");
dojo.require("jetspeed.widget.LayoutEditPane");
jetspeed.widget.PageEditor=function(){
};
dojo.widget.defineWidget("jetspeed.widget.PageEditor",dojo.widget.HtmlWidget,{deletePortletDialog:null,deletePortletDialogBg:null,deletePortletDialogFg:null,deleteLayoutDialog:null,deleteLayoutDialogBg:null,deleteLayoutDialogFg:null,columnSizeDialog:null,columnSizeDialogBg:null,columnSizeDialogFg:null,detail:null,editorInitiatedFromDesktop:false,isContainer:true,widgetsInTemplate:true,postMixInProperties:function(_1,_2,_3){
jetspeed.widget.PageEditor.superclass.postMixInProperties.apply(this,arguments);
this.templateCssPath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.css");
this.templatePath=new dojo.uri.Uri(jetspeed.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.html");
},fillInTemplate:function(_4,_5){
var _6=this;
this.deletePortletDialog=dojo.widget.createWidget("dialog",{widgetsInTemplate:true,deletePortletConfirmed:function(){
this.hide();
_6.deletePortletConfirmed(this.portletEntityId);
}},this.deletePortletDialog);
this.deletePortletDialog.setCloseControl(this.deletePortletDialog.deletePortletCancel.domNode);
this.deleteLayoutDialog=dojo.widget.createWidget("dialog",{widgetsInTemplate:true,deleteLayoutConfirmed:function(){
this.hide();
_6.deleteLayoutConfirmed(this.portletEntityId);
}},this.deleteLayoutDialog);
this.deleteLayoutDialog.setCloseControl(this.deleteLayoutDialog.deleteLayoutCancel.domNode);
var _7={};
_7.widgetsInTemplate=true;
_7.columnSizeConfirmed=function(){
var _8=0;
var _9=new Array();
for(var i=0;i<this.columnCount;i++){
var _b=this["spinner"+i];
var _c=new Number(_b.getValue());
_9.push(_c);
_8+=_c;
}
if(_8>100){
alert("Sum of column sizes cannot exceed 100.");
}else{
this.hide();
_6.columnSizeConfirmed(this.layoutId,_9);
}
};
this.columnSizeDialog=dojo.widget.createWidget("dialog",_7,this.columnSizeDialog);
this.columnSizeDialog.setCloseControl(this.columnSizeDialog.columnSizeCancel.domNode);
jetspeed.widget.PageEditor.superclass.fillInTemplate.call(this);
},postCreate:function(_d,_e,_f){
this.editPageInitiate();
},editPageInitiate:function(){
var _10=null;
if(this.editorInitiatedFromDesktop){
_10=new jetspeed.widget.EditPageGetThemesContentManager(this,false,false,true,true,true);
}else{
_10=new jetspeed.widget.EditPageGetThemesContentManager(this,true,true,true,false,false);
}
_10.getContent();
},editPageBuild:function(){
jetspeed.url.loadingIndicatorHide();
var _11=jetspeed.prefs.getLayoutRootUrl()+"/images/desktop/";
var _12=new Array();
var _13=new Array();
var _14=dojo.widget.createWidget("jetspeed:PageEditPane",{layoutDecoratorDefinitions:jetspeed.page.themeDefinitions.pageDecorations,portletDecoratorDefinitions:jetspeed.page.themeDefinitions.portletDecorations,layoutImagesRoot:_11});
_14.pageEditorWidget=this;
dojo.dom.insertAfter(_14.domNode,this.domNode);
_12.push(_14);
var _15=dojo.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_root",layoutId:jetspeed.page.rootFragmentId,isRootLayout:true,layoutDefinitions:jetspeed.page.themeDefinitions.layouts,layoutImagesRoot:_11});
_15.pageEditorWidget=this;
dojo.dom.insertAfter(_15.domNode,_14.domNode);
_12.push(_15);
_13.push(_15);
if(jetspeed.prefs.windowTiling){
for(var i=0;i<jetspeed.page.columns.length;i++){
var col=jetspeed.page.columns[i];
if(col.layoutHeader){
var _18=dojo.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_"+i,layoutId:col.layoutId,layoutDefinitions:jetspeed.page.themeDefinitions.layouts,layoutImagesRoot:_11});
_18.pageEditorWidget=this;
if(col.domNode.firstChild!=null){
col.domNode.insertBefore(_18.domNode,col.domNode.firstChild);
}else{
col.domNode.appendChild(_18.domNode);
}
_18.initializeDrag();
_12.push(_18);
_13.push(_18);
}
}
}
this.pageEditorWidgets=_12;
this.layoutEditPaneWidgets=_13;
this.editPageSyncPortletActions();
},editPageSyncPortletActions:function(){
var _19=jetspeed.page.getPortletArray();
if(_19!=null){
for(var i=0;i<_19.length;i++){
_19[i].syncActions();
}
}
},editPageHide:function(){
if(this.pageEditorWidgets!=null){
for(var i=0;i<this.pageEditorWidgets.length;i++){
this.pageEditorWidgets[i].hide();
}
}
this.hide();
this.editPageSyncPortletActions();
},editPageShow:function(){
if(this.pageEditorWidgets!=null){
for(var i=0;i<this.pageEditorWidgets.length;i++){
this.pageEditorWidgets[i].editModeRedisplay();
}
}
this.show();
this.editPageSyncPortletActions();
},editPageDestroy:function(){
if(this.pageEditorWidgets!=null){
for(var i=0;i<this.pageEditorWidgets.length;i++){
this.pageEditorWidgets[i].destroy();
this.pageEditorWidgets[i]=null;
}
}
if(this.deletePortletDialog!=null){
this.deletePortletDialog.destroy();
}
if(this.deleteLayoutDialog!=null){
this.deleteLayoutDialog.destroy();
}
if(this.columnSizeDialog!=null){
this.columnSizeDialog.destroy();
}
this.destroy();
},deletePortlet:function(_1e,_1f){
this.deletePortletDialog.portletEntityId=_1e;
this.deletePortletDialog.portletTitle=_1f;
this.deletePortletTitle.innerHTML=_1f;
this.deletePortletDialog.show();
},deletePortletConfirmed:function(_20){
var _21=new jetspeed.widget.RemovePortletContentManager(_20,this);
_21.getContent();
},deleteLayout:function(_22){
this.deleteLayoutDialog.layoutId=_22;
this.deleteLayoutDialog.layoutTitle=_22;
this.deleteLayoutTitle.innerHTML=_22;
this.deleteLayoutDialog.show();
},deleteLayoutConfirmed:function(){
var _23=new jetspeed.widget.RemoveLayoutContentManager(this.deleteLayoutDialog.layoutId,this);
_23.getContent();
},openColumnSizesEditor:function(_24){
var _25=null;
if(_24!=null){
_25=jetspeed.page.layouts[_24];
}
if(_25!=null&&_25.columnSizes!=null&&_25.columnSizes.length>0){
var _26=5;
var _27=0;
for(var i=0;i<_26;i++){
var _29=this.columnSizeDialog["spinner"+i];
var _2a=this["spinner"+i+"Field"];
if(i<_25.columnSizes.length){
_29.setValue(_25.columnSizes[i]);
_2a.style.display="block";
_29.show();
_27++;
}else{
_2a.style.display="none";
_29.hide();
}
}
this.columnSizeDialog.layoutId=_24;
this.columnSizeDialog.columnCount=_27;
this.columnSizeDialog.show();
}
},columnSizeConfirmed:function(_2b,_2c){
if(_2b!=null&&_2c!=null&&_2c.length>0){
var _2d=jetspeed.page.layouts[_2b];
var _2e=null;
if(_2d!=null){
_2e=_2d.name;
}
if(_2e!=null){
var _2f="";
for(var i=0;i<_2c.length;i++){
if(i>0){
_2f+=",";
}
_2f+=_2c[i]+"%";
}
var _31=new jetspeed.widget.UpdateFragmentContentManager(_2b,_2e,_2f,this);
_31.getContent();
}
}
},refreshPage:function(){
dojo.lang.setTimeout(this,this._doRefreshPage,10);
},_doRefreshPage:function(){
var _32=jetspeed.page.getPageUrl();
_32=jetspeed.url.addQueryParameter(_32,"editPage","true",true);
window.location.href=_32.toString();
},editModeNormal:function(){
var _33=jetspeed.page.getPortletArray();
for(var i=0;i<_33.length;i++){
var _35=_33[i].getPortletWindow();
if(_35!=null){
_35.restoreFromMinimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _36=this.layoutEditPaneWidgets[i];
if(_36.layoutMoveContainer!=null){
_36.layoutMoveContainer.domNode.style.display="none";
}
}
}
},editModeLayoutMove:function(){
var _37=jetspeed.page.getPortletArray();
for(var i=0;i<_37.length;i++){
var _39=_37[i].getPortletWindow();
if(_39!=null){
_39.minimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _3a=this.layoutEditPaneWidgets[i];
if(!_3a.isRootLayout&&_3a.layoutMoveContainer!=null){
_3a.layoutMoveContainer.domNode.style.display="block";
}
}
}
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_3b,_3c,_3d,_3e,_3f,_40){
this.pageEditorWidget=_3b;
var _41=new Array();
if(_3c){
_41.push(["pageDecorations"]);
}
if(_3d){
_41.push(["portletDecorations"]);
}
if(_3e){
_41.push(["layouts"]);
}
if(_3f){
_41.push(["desktopPageDecorations","pageDecorations"]);
}
if(_40){
_41.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_41;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _42="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _43=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_42;
var _44=new jetspeed.om.Id("getthemes",{});
var _45={};
_45.url=_43;
_45.mimetype="text/json";
jetspeed.url.retrieveContent(_45,this,_44,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_46,_47,_48){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _49=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_49]=_46;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_4a,_4b,_4c,_4d){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_4c+" type: "+_4a+jetspeed.url.formatBindError(_4b));
}};
jetspeed.widget.RemovePageContentManager=function(_4e){
this.pageEditorWidget=_4e;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _4f="?action=updatepage&method=remove";
var _50=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_4f;
var _51=new jetspeed.om.Id("updatepage-remove-page",{});
var _52={};
_52.url=_50;
_52.mimetype="text/xml";
jetspeed.url.retrieveContent(_52,this,_51,jetspeed.debugContentDumpIds);
},notifySuccess:function(_53,_54,_55){
if(jetspeed.url.checkAjaxApiResponse(_54,_53,true,"updatepage-remove-page")){
var _56=jetspeed.page.makePageUrl("/");
_56+="?"+jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER+"=true";
window.location.href=_56;
}
},notifyFailure:function(_57,_58,_59,_5a){
dojo.raise("RemovePageContentManager notifyFailure url: "+_59+" type: "+_57+jetspeed.url.formatBindError(_58));
}};
jetspeed.widget.AddPageContentManager=function(_5b,_5c,_5d,_5e,_5f,_60,_61){
this.pageRealPath=_5b;
this.pagePath=_5c;
this.pageName=_5d;
if(_5e==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_5e=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_5e;
this.pageTitle=_5f;
this.pageShortTitle=_60;
this.pageEditorWidget=_61;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _62="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_62+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_62+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_62+="&short-title="+escape(this.pageShortTitle);
}
var _63=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_62;
var _64=new jetspeed.om.Id("updatepage-add-page",{});
var _65={};
_65.url=_63;
_65.mimetype="text/xml";
jetspeed.url.retrieveContent(_65,this,_64,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_66,_67,_68){
if(jetspeed.url.checkAjaxApiResponse(_67,_66,true,"updatepage-add-page")){
var _69=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_69,".psml",true)){
_69+=".psml";
}
_69+="?"+jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER+"=true";
window.location.href=_69;
}
},notifyFailure:function(_6a,_6b,_6c,_6d){
dojo.raise("AddPageContentManager notifyFailure url: "+_6c+" type: "+_6a+jetspeed.url.formatBindError(_6b));
}};
jetspeed.widget.MoveLayoutContentManager=function(_6e,_6f,_70,row,_72){
this.layoutId=_6e;
this.moveToLayoutId=_6f;
this.column=_70;
this.row=row;
this.pageEditorWidget=_72;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _73="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_73+="&col="+this.column;
}
if(this.row!=null){
_73+="&row="+this.row;
}
var _74=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_73;
var _75=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _76={};
_76.url=_74;
_76.mimetype="text/xml";
jetspeed.url.retrieveContent(_76,this,_75,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_77,_78,_79){
if(jetspeed.url.checkAjaxApiResponse(_78,_77,true,"moveabs-layout")){
}
},notifyFailure:function(_7a,_7b,_7c,_7d){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_7c+" type: "+_7a+jetspeed.url.formatBindError(_7b));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_7e,_7f,_80,_81){
this.layoutId=_7e;
this.layoutName=_7f;
this.layoutSizes=_80;
this.pageEditorWidget=_81;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _82="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_82+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_82+="&sizes="+escape(this.layoutSizes);
}
var _83=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_82;
var _84=new jetspeed.om.Id("updatepage-update-fragment",{});
var _85={};
_85.url=_83;
_85.mimetype="text/xml";
jetspeed.url.retrieveContent(_85,this,_84,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_86,_87,_88){
if(jetspeed.url.checkAjaxApiResponse(_87,_86,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_89,_8a,_8b,_8c){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_8b+" type: "+_89+jetspeed.url.formatBindError(_8a));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_8d,_8e,_8f){
this.refreshPage=((_8f.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_8d;
this.portletDecorator=_8e;
this.pageEditorWidget=_8f;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _90="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_90+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_90+="&portlet-decorator="+escape(this.portletDecorator);
}
var _91=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_90;
var _92=new jetspeed.om.Id("updatepage-info",{});
var _93={};
_93.url=_91;
_93.mimetype="text/xml";
jetspeed.url.retrieveContent(_93,this,_92,jetspeed.debugContentDumpIds);
},notifySuccess:function(_94,_95,_96){
if(jetspeed.url.checkAjaxApiResponse(_95,_94,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_97,_98,_99,_9a){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_99+" type: "+_97+jetspeed.url.formatBindError(_98));
}};
jetspeed.widget.RemovePortletContentManager=function(_9b,_9c){
this.portletEntityId=_9b;
this.pageEditorWidget=_9c;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _9d="?action=remove&id="+this.portletEntityId;
var _9e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_9d;
var _9f=new jetspeed.om.Id("removeportlet",{});
var _a0={};
_a0.url=_9e;
_a0.mimetype="text/xml";
jetspeed.url.retrieveContent(_a0,this,_9f,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_a1,_a2,_a3){
if(jetspeed.url.checkAjaxApiResponse(_a2,_a1,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_a4,_a5,_a6,_a7){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_a6+" type: "+_a4+jetspeed.url.formatBindError(_a5));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_a8,_a9){
this.layoutId=_a8;
this.pageEditorWidget=_a9;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _aa="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _ab=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_aa;
var _ac=new jetspeed.om.Id("removelayout",{});
var _ad={};
_ad.url=_ab;
_ad.mimetype="text/xml";
jetspeed.url.retrieveContent(_ad,this,_ac,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_ae,_af,_b0){
if(jetspeed.url.checkAjaxApiResponse(_af,_ae,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_b1,_b2,_b3,_b4){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_b3+" type: "+_b1+jetspeed.url.formatBindError(_b2));
}};
jetspeed.widget.AddLayoutContentManager=function(_b5,_b6,_b7){
this.parentLayoutId=_b5;
this.layoutName=_b6;
this.pageEditorWidget=_b7;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _b8="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _b9=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b8;
var _ba=new jetspeed.om.Id("addlayout",{});
var _bb={};
_bb.url=_b9;
_bb.mimetype="text/xml";
jetspeed.url.retrieveContent(_bb,this,_ba,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_bc,_bd,_be){
if(jetspeed.url.checkAjaxApiResponse(_bd,_bc,true,"addportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_bf,_c0,_c1,_c2){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_c1+" type: "+_bf+jetspeed.url.formatBindError(_c0));
}};

