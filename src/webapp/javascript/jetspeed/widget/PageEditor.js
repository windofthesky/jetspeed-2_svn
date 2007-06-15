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
},deletePortlet:function(_1d,_1e){
this.deletePortletDialog.portletEntityId=_1d;
this.deletePortletDialog.portletTitle=_1e;
this.deletePortletTitle.innerHTML=_1e;
this.deletePortletDialog.show();
},deletePortletConfirmed:function(_1f){
var _20=new jetspeed.widget.RemovePortletContentManager(_1f,this);
_20.getContent();
},deleteLayout:function(_21){
this.deleteLayoutDialog.layoutId=_21;
this.deleteLayoutDialog.layoutTitle=_21;
this.deleteLayoutTitle.innerHTML=_21;
this.deleteLayoutDialog.show();
},deleteLayoutConfirmed:function(){
var _22=new jetspeed.widget.RemoveLayoutContentManager(this.deleteLayoutDialog.layoutId,this);
_22.getContent();
},openColumnSizesEditor:function(_23){
var _24=null;
if(_23!=null){
_24=jetspeed.page.layouts[_23];
}
if(_24!=null&&_24.columnSizes!=null&&_24.columnSizes.length>0){
var _25=5;
var _26=0;
for(var i=0;i<_25;i++){
var _28=this.columnSizeDialog["spinner"+i];
var _29=this["spinner"+i+"Field"];
if(i<_24.columnSizes.length){
_28.setValue(_24.columnSizes[i]);
_29.style.display="block";
_28.show();
_26++;
}else{
_29.style.display="none";
_28.hide();
}
}
this.columnSizeDialog.layoutId=_23;
this.columnSizeDialog.columnCount=_26;
this.columnSizeDialog.show();
}
},columnSizeConfirmed:function(_2a,_2b){
if(_2a!=null&&_2b!=null&&_2b.length>0){
var _2c=jetspeed.page.layouts[_2a];
var _2d=null;
if(_2c!=null){
_2d=_2c.name;
}
if(_2d!=null){
var _2e="";
for(var i=0;i<_2b.length;i++){
if(i>0){
_2e+=",";
}
_2e+=_2b[i]+"%";
}
var _30=new jetspeed.widget.UpdateFragmentContentManager(_2a,_2d,_2e,this);
_30.getContent();
}
}
},refreshPage:function(){
dojo.lang.setTimeout(this,this._doRefreshPage,10);
},_doRefreshPage:function(){
var _31=jetspeed.page.getPageUrl();
_31=jetspeed.url.addQueryParameter(_31,"editPage","true",true);
window.location.href=_31.toString();
},editModeNormal:function(){
var _32=jetspeed.page.getPortletArray();
for(var i=0;i<_32.length;i++){
var _34=_32[i].getPortletWindow();
if(_34!=null){
_34.restoreFromMinimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _35=this.layoutEditPaneWidgets[i];
if(_35.layoutMoveContainer!=null){
_35.layoutMoveContainer.domNode.style.display="none";
}
}
}
},editModeLayoutMove:function(){
var _36=jetspeed.page.getPortletArray();
for(var i=0;i<_36.length;i++){
var _38=_36[i].getPortletWindow();
if(_38!=null){
_38.minimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _39=this.layoutEditPaneWidgets[i];
if(!_39.isRootLayout&&_39.layoutMoveContainer!=null){
_39.layoutMoveContainer.domNode.style.display="block";
}
}
}
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_3a,_3b,_3c,_3d,_3e,_3f){
this.pageEditorWidget=_3a;
var _40=new Array();
if(_3b){
_40.push(["pageDecorations"]);
}
if(_3c){
_40.push(["portletDecorations"]);
}
if(_3d){
_40.push(["layouts"]);
}
if(_3e){
_40.push(["desktopPageDecorations","pageDecorations"]);
}
if(_3f){
_40.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_40;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _41="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _42=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_41;
var _43=new jetspeed.om.Id("getthemes",{});
var _44={};
_44.url=_42;
_44.mimetype="text/json";
jetspeed.url.retrieveContent(_44,this,_43,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_45,_46,_47){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _48=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_48]=_45;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_49,_4a,_4b,_4c){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_4b+" type: "+_49+jetspeed.url.formatBindError(_4a));
}};
jetspeed.widget.RemovePageContentManager=function(_4d){
this.pageEditorWidget=_4d;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _4e="?action=updatepage&method=remove";
var _4f=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_4e;
var _50=new jetspeed.om.Id("updatepage-remove-page",{});
var _51={};
_51.url=_4f;
_51.mimetype="text/xml";
jetspeed.url.retrieveContent(_51,this,_50,jetspeed.debugContentDumpIds);
},notifySuccess:function(_52,_53,_54){
if(jetspeed.url.checkAjaxApiResponse(_53,_52,true,"updatepage-remove-page")){
var _55=jetspeed.page.makePageUrl("/");
_55+="?"+jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER+"=true";
window.location.href=_55;
}
},notifyFailure:function(_56,_57,_58,_59){
dojo.raise("RemovePageContentManager notifyFailure url: "+_58+" type: "+_56+jetspeed.url.formatBindError(_57));
}};
jetspeed.widget.AddPageContentManager=function(_5a,_5b,_5c,_5d,_5e,_5f,_60){
this.pageRealPath=_5a;
this.pagePath=_5b;
this.pageName=_5c;
if(_5d==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_5d=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_5d;
this.pageTitle=_5e;
this.pageShortTitle=_5f;
this.pageEditorWidget=_60;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _61="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_61+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_61+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_61+="&short-title="+escape(this.pageShortTitle);
}
var _62=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_61;
var _63=new jetspeed.om.Id("updatepage-add-page",{});
var _64={};
_64.url=_62;
_64.mimetype="text/xml";
jetspeed.url.retrieveContent(_64,this,_63,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_65,_66,_67){
if(jetspeed.url.checkAjaxApiResponse(_66,_65,true,"updatepage-add-page")){
var _68=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_68,".psml",true)){
_68+=".psml";
}
_68+="?"+jetspeed.id.PAGE_EDITOR_INITIATE_PARAMETER+"=true";
window.location.href=_68;
}
},notifyFailure:function(_69,_6a,_6b,_6c){
dojo.raise("AddPageContentManager notifyFailure url: "+_6b+" type: "+_69+jetspeed.url.formatBindError(_6a));
}};
jetspeed.widget.MoveLayoutContentManager=function(_6d,_6e,_6f,row,_71){
this.layoutId=_6d;
this.moveToLayoutId=_6e;
this.column=_6f;
this.row=row;
this.pageEditorWidget=_71;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _72="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_72+="&col="+this.column;
}
if(this.row!=null){
_72+="&row="+this.row;
}
var _73=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_72;
var _74=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _75={};
_75.url=_73;
_75.mimetype="text/xml";
jetspeed.url.retrieveContent(_75,this,_74,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_76,_77,_78){
if(jetspeed.url.checkAjaxApiResponse(_77,_76,true,"moveabs-layout")){
}
},notifyFailure:function(_79,_7a,_7b,_7c){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_7b+" type: "+_79+jetspeed.url.formatBindError(_7a));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_7d,_7e,_7f,_80){
this.layoutId=_7d;
this.layoutName=_7e;
this.layoutSizes=_7f;
this.pageEditorWidget=_80;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _81="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_81+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_81+="&sizes="+escape(this.layoutSizes);
}
var _82=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_81;
var _83=new jetspeed.om.Id("updatepage-update-fragment",{});
var _84={};
_84.url=_82;
_84.mimetype="text/xml";
jetspeed.url.retrieveContent(_84,this,_83,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_85,_86,_87){
if(jetspeed.url.checkAjaxApiResponse(_86,_85,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_88,_89,_8a,_8b){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_8a+" type: "+_88+jetspeed.url.formatBindError(_89));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_8c,_8d,_8e){
this.refreshPage=((_8e.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_8c;
this.portletDecorator=_8d;
this.pageEditorWidget=_8e;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _8f="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_8f+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_8f+="&portlet-decorator="+escape(this.portletDecorator);
}
var _90=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_8f;
var _91=new jetspeed.om.Id("updatepage-info",{});
var _92={};
_92.url=_90;
_92.mimetype="text/xml";
jetspeed.url.retrieveContent(_92,this,_91,jetspeed.debugContentDumpIds);
},notifySuccess:function(_93,_94,_95){
if(jetspeed.url.checkAjaxApiResponse(_94,_93,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_96,_97,_98,_99){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_98+" type: "+_96+jetspeed.url.formatBindError(_97));
}};
jetspeed.widget.RemovePortletContentManager=function(_9a,_9b){
this.portletEntityId=_9a;
this.pageEditorWidget=_9b;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _9c="?action=remove&id="+this.portletEntityId;
var _9d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_9c;
var _9e=new jetspeed.om.Id("removeportlet",{});
var _9f={};
_9f.url=_9d;
_9f.mimetype="text/xml";
jetspeed.url.retrieveContent(_9f,this,_9e,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_a0,_a1,_a2){
if(jetspeed.url.checkAjaxApiResponse(_a1,_a0,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_a3,_a4,_a5,_a6){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_a5+" type: "+_a3+jetspeed.url.formatBindError(_a4));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_a7,_a8){
this.layoutId=_a7;
this.pageEditorWidget=_a8;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _a9="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _aa=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_a9;
var _ab=new jetspeed.om.Id("removelayout",{});
var _ac={};
_ac.url=_aa;
_ac.mimetype="text/xml";
jetspeed.url.retrieveContent(_ac,this,_ab,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_ad,_ae,_af){
if(jetspeed.url.checkAjaxApiResponse(_ae,_ad,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_b0,_b1,_b2,_b3){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_b2+" type: "+_b0+jetspeed.url.formatBindError(_b1));
}};
jetspeed.widget.AddLayoutContentManager=function(_b4,_b5,_b6){
this.parentLayoutId=_b4;
this.layoutName=_b5;
this.pageEditorWidget=_b6;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _b7="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _b8=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b7;
var _b9=new jetspeed.om.Id("addlayout",{});
var _ba={};
_ba.url=_b8;
_ba.mimetype="text/xml";
jetspeed.url.retrieveContent(_ba,this,_b9,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_bb,_bc,_bd){
if(jetspeed.url.checkAjaxApiResponse(_bc,_bb,true,"addportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_be,_bf,_c0,_c1){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_c0+" type: "+_be+jetspeed.url.formatBindError(_bf));
}};

