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
dojo.widget.defineWidget("jetspeed.widget.PageEditor",dojo.widget.HtmlWidget,{deletePortletDialog:null,deletePortletDialogBg:null,deletePortletDialogFg:null,deleteLayoutDialog:null,deleteLayoutDialogBg:null,deleteLayoutDialogFg:null,columnSizeDialog:null,columnSizeDialogBg:null,columnSizeDialogFg:null,detail:null,editorInitiatedFromDesktop:false,isContainer:true,widgetsInTemplate:true,dbOn:djConfig.isDebug,styleBase:"pageEditorPaneContainer",styleBaseAdd:(jetspeed.UAie?"pageEditorPaneContainerIE":"pageEditorPaneContainerNotIE"),styleDetail:"pageEditorDetailContainer",styleDetailAdd:(jetspeed.UAie?"pageEditorDetailContainerIE":"pageEditorDetailContainerNotIE"),postMixInProperties:function(_1,_2,_3){
var _4=jetspeed;
_4.widget.PageEditor.superclass.postMixInProperties.apply(this,arguments);
this.layoutImagesRoot=_4.prefs.getLayoutRootUrl()+"/images/desktop/";
this.labels=_4.prefs.pageEditorLabels;
this.dialogLabels=_4.prefs.pageEditorDialogLabels;
this.templateCssPath=new dojo.uri.Uri(_4.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.css");
this.templatePath=new dojo.uri.Uri(_4.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.html");
},fillInTemplate:function(_5,_6){
var _7=dojo;
var _8=this;
this.deletePortletDialog=_7.widget.createWidget("dialog",{widgetsInTemplate:true,deletePortletConfirmed:function(){
this.hide();
_8.deletePortletConfirmed(this.portletEntityId);
}},this.deletePortletDialog);
this.deletePortletDialog.setCloseControl(this.deletePortletDialog.deletePortletCancel.domNode);
this.deleteLayoutDialog=_7.widget.createWidget("dialog",{widgetsInTemplate:true,deleteLayoutConfirmed:function(){
this.hide();
_8.deleteLayoutConfirmed(this.portletEntityId);
}},this.deleteLayoutDialog);
this.deleteLayoutDialog.setCloseControl(this.deleteLayoutDialog.deleteLayoutCancel.domNode);
var _9={};
_9.widgetsInTemplate=true;
_9.columnSizeConfirmed=function(){
var _a=0;
var _b=new Array();
for(var i=0;i<this.columnCount;i++){
var _d=this["spinner"+i];
var _e=new Number(_d.getValue());
_b.push(_e);
_a+=_e;
}
if(_a>100){
alert("Sum of column sizes cannot exceed 100.");
}else{
this.hide();
_8.columnSizeConfirmed(this.layoutId,_b);
}
};
this.columnSizeDialog=_7.widget.createWidget("dialog",_9,this.columnSizeDialog);
this.columnSizeDialog.setCloseControl(this.columnSizeDialog.columnSizeCancel.domNode);
jetspeed.widget.PageEditor.superclass.fillInTemplate.call(this);
},postCreate:function(_f,_10,_11){
this.editPageInitiate();
},editPageInitiate:function(){
var _12=null;
if(this.editorInitiatedFromDesktop){
_12=new jetspeed.widget.EditPageGetThemesContentManager(this,false,false,true,true,true);
}else{
_12=new jetspeed.widget.EditPageGetThemesContentManager(this,true,true,true,false,false);
}
_12.getContent();
},editPageBuild:function(){
var _13=jetspeed;
var _14=_13.page;
var _15=dojo;
var _16=this.layoutImagesRoot;
var _17=this.labels;
var _18=this.dialogLabels;
var _19=new Array();
var _1a=new Array();
var _1b=_15.widget.createWidget("jetspeed:PageEditPane",{layoutDecoratorDefinitions:_14.themeDefinitions.pageDecorations,portletDecoratorDefinitions:_14.themeDefinitions.portletDecorations,layoutImagesRoot:_16,labels:_17,dialogLabels:_18});
_1b.pageEditorWidget=this;
_15.dom.insertAfter(_1b.domNode,this.domNode);
_19.push(_1b);
var _1c=_15.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_root",layoutId:_14.rootFragmentId,isRootLayout:true,layoutDefinitions:_14.themeDefinitions.layouts,layoutImagesRoot:_16,labels:_17,dialogLabels:_18});
_1c.pageEditorWidget=this;
_15.dom.insertAfter(_1c.domNode,_1b.domNode);
_19.push(_1c);
_1a.push(_1c);
if(_13.prefs.windowTiling){
var doc=document;
var _1e=_14.layoutInfo.columnLayoutHeader;
var col,_20;
for(var i=0;i<_14.columns.length;i++){
col=_14.columns[i];
if(col.layoutHeader){
_20=_15.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_"+i,layoutColumn:col,layoutId:col.layoutId,layoutInfo:_1e,layoutDefinitions:_14.themeDefinitions.layouts,layoutImagesRoot:_16,labels:_17,dialogLabels:_18});
_20.pageEditorWidget=this;
if(col.domNode.firstChild!=null){
col.domNode.insertBefore(_20.domNode,col.domNode.firstChild);
}else{
col.domNode.appendChild(_20.domNode);
}
_20.initializeDrag();
_19.push(_20);
_1a.push(_20);
}
}
if(_13.UAie){
this.bgIframe=new _13.widget.BackgroundIframe(this.domNode,"ieLayoutBackgroundIFrame",_15);
}
}
this.pageEditorWidgets=_19;
this.layoutEditPaneWidgets=_1a;
this.editPageSyncPortletActions();
_13.url.loadingIndicatorHide();
if(_13.UAie6){
_14.displayAllPWins();
}
},editPageSyncPortletActions:function(){
var _22=jetspeed.page.getPortletArray();
if(_22!=null){
for(var i=0;i<_22.length;i++){
_22[i].syncActions();
}
}
},editPageHide:function(){
var _24=this.pageEditorWidgets;
if(_24!=null){
for(var i=0;i<_24.length;i++){
_24[i].hide();
}
}
this.hide();
this.editPageSyncPortletActions();
},editPageShow:function(){
var _26=jetspeed;
var _27=this.pageEditorWidgets;
if(_27!=null){
for(var i=0;i<_27.length;i++){
_27[i].editModeRedisplay();
}
}
this.show();
this.editPageSyncPortletActions();
if(_26.UAie6){
_26.page.displayAllPWins();
}
},editPageDestroy:function(){
var _29=this.pageEditorWidgets;
if(_29!=null){
for(var i=0;i<_29.length;i++){
_29[i].destroy();
_29[i]=null;
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
},deletePortlet:function(_2b,_2c){
this.deletePortletDialog.portletEntityId=_2b;
this.deletePortletDialog.portletTitle=_2c;
this.deletePortletTitle.innerHTML=_2c;
this._openDialog(this.deletePortletDialog);
},deletePortletConfirmed:function(_2d){
var _2e=new jetspeed.widget.RemovePortletContentManager(_2d,this);
_2e.getContent();
},deleteLayout:function(_2f){
this.deleteLayoutDialog.layoutId=_2f;
this.deleteLayoutDialog.layoutTitle=_2f;
this.deleteLayoutTitle.innerHTML=_2f;
this._openDialog(this.deleteLayoutDialog);
},deleteLayoutConfirmed:function(){
var _30=new jetspeed.widget.RemoveLayoutContentManager(this.deleteLayoutDialog.layoutId,this);
_30.getContent();
},openColumnSizesEditor:function(_31){
var _32=null;
if(_31!=null){
_32=jetspeed.page.layouts[_31];
}
if(_32!=null&&_32.columnSizes!=null&&_32.columnSizes.length>0){
var _33=5;
var _34=0;
for(var i=0;i<_33;i++){
var _36=this.columnSizeDialog["spinner"+i];
var _37=this["spinner"+i+"Field"];
if(i<_32.columnSizes.length){
_36.setValue(_32.columnSizes[i]);
_37.style.display="block";
_36.show();
_34++;
}else{
_37.style.display="none";
_36.hide();
}
}
this.columnSizeDialog.layoutId=_31;
this.columnSizeDialog.columnCount=_34;
this._openDialog(this.columnSizeDialog);
}
},columnSizeConfirmed:function(_38,_39){
if(_38!=null&&_39!=null&&_39.length>0){
var _3a=jetspeed.page.layouts[_38];
var _3b=null;
if(_3a!=null){
_3b=_3a.name;
}
if(_3b!=null){
var _3c="";
for(var i=0;i<_39.length;i++){
if(i>0){
_3c+=",";
}
_3c+=_39[i]+"%";
}
var _3e=new jetspeed.widget.UpdateFragmentContentManager(_38,_3b,_3c,this);
_3e.getContent();
}
}
},refreshPage:function(){
dojo.lang.setTimeout(this,this._doRefreshPage,10);
},_doRefreshPage:function(){
var _3f=jetspeed.page.getPageUrl();
_3f=jetspeed.url.addQueryParameter(_3f,jetspeed.id.PG_ED_PARAM,"true",true);
window.location.href=_3f.toString();
},editMoveModeExit:function(){
var _40=jetspeed;
var _41=_40.UAie6;
if(_41){
_40.page.displayAllPWins(true);
}
var _42;
var _43=[];
var _44=_40.page.getPWins();
for(var i=0;i<_44.length;i++){
_42=_44[i];
_42.restoreFromMinimizeWindowTemporarily();
if(_41&&_42.posStatic){
var _46=_42.domNode.parentNode;
var _47=false;
for(var j=0;j<_43.length;j++){
if(_43[j]==_46){
_47=true;
break;
}
}
if(!_47){
_43.push(_46);
}
}
}
var _49=this.layoutEditPaneWidgets;
if(_49!=null){
for(var i=0;i<_49.length;i++){
_49[i]._disableMoveMode();
}
}
_40.widget.showAllPortletWindows();
if(_41){
_40.page.displayAllPWins();
if(_43.length>0){
var _4a=new jetspeed.widget.IE6ZappedContentRestorer(_43);
dojo.lang.setTimeout(_4a,_4a.showNext,20);
}
}
},editMoveModeStart:function(){
var _4b=jetspeed;
var _4c=false;
if(_4b.UAie6){
_4b.page.displayAllPWins(true);
}
var _4d=[];
var _4e=[];
if(this.dbOn){
var _4f=_4b.debugWindow();
if(_4f&&(!_4c||!_4f.posStatic||_4b.debug.dragWindow)){
_4d.push(_4f);
_4e.push(_4f.widgetId);
}
}
if(!_4c){
var _50;
var _51=_4b.page.getPWins();
for(var i=0;i<_51.length;i++){
_50=_51[i];
if(_50.posStatic){
_4d.push(_50);
_4e.push(_50.widgetId);
_50.minimizeWindowTemporarily();
}
}
}
_4b.widget.hideAllPortletWindows(_4e);
var _53=this.layoutEditPaneWidgets;
if(_53!=null){
for(var i=0;i<_53.length;i++){
_53[i]._enableMoveMode();
}
}
if(_4b.UAie6){
setTimeout(function(){
_4b.page.displayAllPWins(false,_4d);
},20);
}
},onBrowserWindowResize:function(){
var _54=this.deletePortletDialog;
var _55=this.deleteLayoutDialog;
var _56=this.columnSizeDialog;
if(_54&&_54.isShowing()){
_54.domNode.style.display="none";
_54.domNode.style.display="block";
}
if(_55&&_55.isShowing()){
_55.domNode.style.display="none";
_55.domNode.style.display="block";
}
if(_56&&_56.isShowing()){
_56.domNode.style.display="none";
_56.domNode.style.display="block";
}
var _57=this.pageEditorWidgets;
if(_57!=null){
for(var i=0;i<_57.length;i++){
_57[i].onBrowserWindowResize();
}
}
},_openDialog:function(_59){
var _5a=jetspeed.UAmoz;
if(_5a){
_59.domNode.style.position="fixed";
if(!_59._fixedIPtBug){
var _5b=_59;
_5b.placeModalDialog=function(){
var _5c=dojo.html.getScroll().offset;
var _5d=dojo.html.getViewport();
var mb;
if(_5b.isShowing()){
mb=dojo.html.getMarginBox(_5b.domNode);
}else{
dojo.html.setVisibility(_5b.domNode,false);
dojo.html.show(_5b.domNode);
mb=dojo.html.getMarginBox(_5b.domNode);
dojo.html.hide(_5b.domNode);
dojo.html.setVisibility(_5b.domNode,true);
}
var x=(_5d.width-mb.width)/2;
var y=(_5d.height-mb.height)/2;
with(_5b.domNode.style){
left=x+"px";
top=y+"px";
}
};
_5b._fixedIPtBug=true;
}
}
_59.show();
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_61,_62,_63,_64,_65,_66){
this.pageEditorWidget=_61;
var _67=new Array();
if(_62){
_67.push(["pageDecorations"]);
}
if(_63){
_67.push(["portletDecorations"]);
}
if(_64){
_67.push(["layouts"]);
}
if(_65){
_67.push(["desktopPageDecorations","pageDecorations"]);
}
if(_66){
_67.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_67;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _68="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _69=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_68;
var _6a=new jetspeed.om.Id("getthemes",{});
var _6b={};
_6b.url=_69;
_6b.mimetype="text/json";
jetspeed.url.retrieveContent(_6b,this,_6a,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_6c,_6d,_6e){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _6f=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_6f]=_6c;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_70,_71,_72,_73){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_72+" type: "+_70+jetspeed.formatError(_71));
}};
jetspeed.widget.RemovePageContentManager=function(_74){
this.pageEditorWidget=_74;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _75="?action=updatepage&method=remove";
var _76=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_75;
var _77=new jetspeed.om.Id("updatepage-remove-page",{});
var _78={};
_78.url=_76;
_78.mimetype="text/xml";
jetspeed.url.retrieveContent(_78,this,_77,jetspeed.debugContentDumpIds);
},notifySuccess:function(_79,_7a,_7b){
if(jetspeed.url.checkAjaxApiResponse(_7a,_79,true,"updatepage-remove-page")){
var _7c=jetspeed.page.makePageUrl("/");
_7c+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_7c;
}
},notifyFailure:function(_7d,_7e,_7f,_80){
dojo.raise("RemovePageContentManager notifyFailure url: "+_7f+" type: "+_7d+jetspeed.formatError(_7e));
}};
jetspeed.widget.IE6ZappedContentRestorer=function(_81){
this.colNodes=_81;
this.nextColNodeIndex=0;
};
jetspeed.widget.IE6ZappedContentRestorer.prototype={showNext:function(){
if(this.colNodes&&this.colNodes.length>this.nextColNodeIndex){
dojo.dom.insertAtIndex(jetspeed.widget.ie6ZappedContentHelper,this.colNodes[this.nextColNodeIndex],0);
dojo.lang.setTimeout(this,this.removeAndShowNext,20);
}
},removeAndShowNext:function(){
dojo.dom.removeNode(jetspeed.widget.ie6ZappedContentHelper);
this.nextColNodeIndex++;
if(this.colNodes&&this.colNodes.length>this.nextColNodeIndex){
dojo.lang.setTimeout(this,this.showNext,20);
}
}};
jetspeed.widget.AddPageContentManager=function(_82,_83,_84,_85,_86,_87,_88){
this.pageRealPath=_82;
this.pagePath=_83;
this.pageName=_84;
if(_85==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_85=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_85;
this.pageTitle=_86;
this.pageShortTitle=_87;
this.pageEditorWidget=_88;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _89="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_89+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_89+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_89+="&short-title="+escape(this.pageShortTitle);
}
var _8a=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_89;
var _8b=new jetspeed.om.Id("updatepage-add-page",{});
var _8c={};
_8c.url=_8a;
_8c.mimetype="text/xml";
jetspeed.url.retrieveContent(_8c,this,_8b,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_8d,_8e,_8f){
if(jetspeed.url.checkAjaxApiResponse(_8e,_8d,true,"updatepage-add-page")){
var _90=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_90,".psml",true)){
_90+=".psml";
}
_90+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_90;
}
},notifyFailure:function(_91,_92,_93,_94){
dojo.raise("AddPageContentManager notifyFailure url: "+_93+" type: "+_91+jetspeed.formatError(_92));
}};
jetspeed.widget.MoveLayoutContentManager=function(_95,_96,_97,row,_99){
this.layoutId=_95;
this.moveToLayoutId=_96;
this.column=_97;
this.row=row;
this.pageEditorWidget=_99;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _9a="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_9a+="&col="+this.column;
}
if(this.row!=null){
_9a+="&row="+this.row;
}
var _9b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_9a;
var _9c=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _9d={};
_9d.url=_9b;
_9d.mimetype="text/xml";
jetspeed.url.retrieveContent(_9d,this,_9c,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_9e,_9f,_a0){
if(jetspeed.url.checkAjaxApiResponse(_9f,_9e,true,"moveabs-layout")){
}
},notifyFailure:function(_a1,_a2,_a3,_a4){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_a3+" type: "+_a1+jetspeed.formatError(_a2));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_a5,_a6,_a7,_a8){
this.layoutId=_a5;
this.layoutName=_a6;
this.layoutSizes=_a7;
this.pageEditorWidget=_a8;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _a9="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_a9+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_a9+="&sizes="+escape(this.layoutSizes);
}
var _aa=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_a9;
var _ab=new jetspeed.om.Id("updatepage-update-fragment",{});
var _ac={};
_ac.url=_aa;
_ac.mimetype="text/xml";
jetspeed.url.retrieveContent(_ac,this,_ab,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_ad,_ae,_af){
if(jetspeed.url.checkAjaxApiResponse(_ae,_ad,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_b0,_b1,_b2,_b3){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_b2+" type: "+_b0+jetspeed.formatError(_b1));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_b4,_b5,_b6){
this.refreshPage=((_b6.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_b4;
this.portletDecorator=_b5;
this.pageEditorWidget=_b6;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _b7="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_b7+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_b7+="&portlet-decorator="+escape(this.portletDecorator);
}
var _b8=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b7;
var _b9=new jetspeed.om.Id("updatepage-info",{});
var _ba={};
_ba.url=_b8;
_ba.mimetype="text/xml";
jetspeed.url.retrieveContent(_ba,this,_b9,jetspeed.debugContentDumpIds);
},notifySuccess:function(_bb,_bc,_bd){
if(jetspeed.url.checkAjaxApiResponse(_bc,_bb,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_be,_bf,_c0,_c1){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_c0+" type: "+_be+jetspeed.formatError(_bf));
}};
jetspeed.widget.RemovePortletContentManager=function(_c2,_c3){
this.portletEntityId=_c2;
this.pageEditorWidget=_c3;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _c4="?action=remove&id="+this.portletEntityId;
var _c5=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_c4;
var _c6=new jetspeed.om.Id("removeportlet",{});
var _c7={};
_c7.url=_c5;
_c7.mimetype="text/xml";
jetspeed.url.retrieveContent(_c7,this,_c6,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_c8,_c9,_ca){
if(jetspeed.url.checkAjaxApiResponse(_c9,_c8,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_cb,_cc,_cd,_ce){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_cd+" type: "+_cb+jetspeed.formatError(_cc));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_cf,_d0){
this.layoutId=_cf;
this.pageEditorWidget=_d0;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _d1="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _d2=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_d1;
var _d3=new jetspeed.om.Id("removelayout",{});
var _d4={};
_d4.url=_d2;
_d4.mimetype="text/xml";
jetspeed.url.retrieveContent(_d4,this,_d3,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_d5,_d6,_d7){
if(jetspeed.url.checkAjaxApiResponse(_d6,_d5,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_d8,_d9,_da,_db){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_da+" type: "+_d8+jetspeed.formatError(_d9));
}};
jetspeed.widget.AddLayoutContentManager=function(_dc,_dd,_de){
this.parentLayoutId=_dc;
this.layoutName=_dd;
this.pageEditorWidget=_de;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _df="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _e0=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_df;
var _e1=new jetspeed.om.Id("addlayout",{});
var _e2={};
_e2.url=_e0;
_e2.mimetype="text/xml";
jetspeed.url.retrieveContent(_e2,this,_e1,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_e3,_e4,_e5){
if(jetspeed.url.checkAjaxApiResponse(_e4,_e3,true,"addlayout")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_e6,_e7,_e8,_e9){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_e8+" type: "+_e6+jetspeed.formatError(_e7));
}};

