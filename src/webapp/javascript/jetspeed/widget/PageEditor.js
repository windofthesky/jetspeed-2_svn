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
dojo.widget.defineWidget("jetspeed.widget.PageEditor",dojo.widget.HtmlWidget,{deletePortletDialog:null,deletePortletDialogBg:null,deletePortletDialogFg:null,deleteLayoutDialog:null,deleteLayoutDialogBg:null,deleteLayoutDialogFg:null,columnSizeDialog:null,columnSizeDialogBg:null,columnSizeDialogFg:null,detail:null,editorInitiatedFromDesktop:false,isContainer:true,widgetsInTemplate:true,loadTimeDistribute:jetspeed.UAie,dbOn:djConfig.isDebug,styleBase:"pageEditorPaneContainer",styleBaseAdd:(jetspeed.UAie?"pageEditorPaneContainerIE":"pageEditorPaneContainerNotIE"),styleDetail:"pageEditorDetailContainer",styleDetailAdd:(jetspeed.UAie?"pageEditorDetailContainerIE":"pageEditorDetailContainerNotIE"),postMixInProperties:function(_1,_2,_3){
var _4=jetspeed;
_4.widget.PageEditor.superclass.postMixInProperties.apply(this,arguments);
this.layoutImagesRoot=_4.prefs.getLayoutRootUrl()+"/images/desktop/";
this.labels=_4.prefs.pageEditorLabels;
this.dialogLabels=_4.prefs.pageEditorDialogLabels;
this.templateCssPath=new dojo.uri.Uri(_4.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.css");
this.templatePath=new dojo.uri.Uri(_4.url.basePortalDesktopUrl()+"/javascript/jetspeed/widget/PageEditor.html");
},fillInTemplate:function(_5,_6){
var _7=jetspeed;
var _8=dojo;
var _9=this;
this.deletePortletDialog=_8.widget.createWidget("dialog",{widgetsInTemplate:true,deletePortletConfirmed:function(){
this.hide();
_9.deletePortletConfirmed(this.portletEntityId);
}},this.deletePortletDialog);
this.deletePortletDialog.setCloseControl(this.deletePortletDialog.deletePortletCancel.domNode);
this.deleteLayoutDialog=_8.widget.createWidget("dialog",{widgetsInTemplate:true,deleteLayoutConfirmed:function(){
this.hide();
_9.deleteLayoutConfirmed(this.portletEntityId);
}},this.deleteLayoutDialog);
this.deleteLayoutDialog.setCloseControl(this.deleteLayoutDialog.deleteLayoutCancel.domNode);
var _a={};
_a.widgetsInTemplate=true;
_a.columnSizeConfirmed=function(){
var _b=0;
var _c=new Array();
for(var i=0;i<this.columnCount;i++){
var _e=this["spinner"+i];
var _f=new Number(_e.getValue());
_c.push(_f);
_b+=_f;
}
if(_b>100){
alert("Sum of column sizes cannot exceed 100.");
}else{
this.hide();
_9.columnSizeConfirmed(this.layoutId,_c);
}
};
this.columnSizeDialog=_8.widget.createWidget("dialog",_a,this.columnSizeDialog);
this.columnSizeDialog.setCloseControl(this.columnSizeDialog.columnSizeCancel.domNode);
_7.widget.PageEditor.superclass.fillInTemplate.call(this);
},postCreate:function(_10,_11,_12){
this.editPageInitiate();
},editPageInitiate:function(){
var _13=null;
if(this.editorInitiatedFromDesktop){
_13=new jetspeed.widget.EditPageGetThemesContentManager(this,false,false,true,true,true);
}else{
_13=new jetspeed.widget.EditPageGetThemesContentManager(this,true,true,true,false,false);
}
_13.getContent();
},editPageBuild:function(){
var _14=jetspeed;
var _15=_14.page;
var _16=dojo;
this.pageEditorWidgets=new Array();
this.layoutEditPaneWidgets=new Array();
var _17=_16.widget.createWidget("jetspeed:PageEditPane",{layoutDecoratorDefinitions:_15.themeDefinitions.pageDecorations,portletDecoratorDefinitions:_15.themeDefinitions.portletDecorations,layoutImagesRoot:this.layoutImagesRoot,labels:this.labels,dialogLabels:this.dialogLabels});
_17.pageEditorWidget=this;
_17.domNode.style.display="none";
_16.dom.insertAfter(_17.domNode,this.domNode);
this.pageEditorWidgets.push(_17);
this.pageEditPaneWidget=_17;
if(!this.loadTimeDistribute){
_14.url.loadingIndicatorStep(_14);
this._buildRootPane();
}else{
_16.lang.setTimeout(this,this._buildRootPane,10);
_14.url.loadingIndicatorStep(_14);
}
},_buildRootPane:function(){
var _18=jetspeed;
var _19=_18.page;
var _1a=dojo;
var _1b=_1a.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_root",layoutId:_19.rootFragmentId,isRootLayout:true,layoutDefinitions:_19.themeDefinitions.layouts,layoutImagesRoot:this.layoutImagesRoot,labels:this.labels,dialogLabels:this.dialogLabels});
_1b.pageEditorWidget=this;
_1b.domNode.style.display="none";
_1a.dom.insertAfter(_1b.domNode,this.pageEditPaneWidget.domNode);
this.pageEditorWidgets.push(_1b);
this.layoutEditPaneWidgets.push(_1b);
this._buildNextColI=0;
this._buildColLen=(_18.prefs.windowTiling?_19.columns.length:0);
if(!this.loadTimeDistribute){
_18.url.loadingIndicatorStep(_18);
this._buildNextPane();
}else{
_1a.lang.setTimeout(this,this._buildNextPane,10);
_18.url.loadingIndicatorStep(_18);
}
},_buildNextPane:function(){
var _1c=jetspeed;
var _1d=_1c.page;
var _1e=dojo;
var i=this._buildNextColI;
var _20=this._buildColLen;
if(i<_20){
var col,_22=null;
while(i<_20&&_22==null){
col=_1d.columns[i];
if(col.layoutHeader){
_22=_1e.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_"+i,layoutColumn:col,layoutId:col.layoutId,layoutInfo:_1d.layoutInfo.columnLayoutHeader,layoutDefinitions:_1d.themeDefinitions.layouts,layoutImagesRoot:this.layoutImagesRoot,labels:this.labels,dialogLabels:this.dialogLabels});
_22.pageEditorWidget=this;
_22.domNode.style.display="none";
if(col.domNode.firstChild!=null){
col.domNode.insertBefore(_22.domNode,col.domNode.firstChild);
}else{
col.domNode.appendChild(_22.domNode);
}
_22.initializeDrag();
this.pageEditorWidgets.push(_22);
this.layoutEditPaneWidgets.push(_22);
}
i++;
}
}
if(i<_20){
this._buildNextColI=i;
if(!this.loadTimeDistribute){
_1c.url.loadingIndicatorStep(_1c);
this._buildNextPane();
}else{
_1e.lang.setTimeout(this,this._buildNextPane,10);
_1c.url.loadingIndicatorStep(_1c);
}
}else{
if(_1c.UAie){
this.bgIframe=new _1c.widget.BackgroundIframe(this.domNode,"ieLayoutBackgroundIFrame",_1e);
}
var _23=this.pageEditorWidgets;
if(_23!=null){
for(var i=0;i<_23.length;i++){
_23[i].domNode.style.display="block";
}
}
this.editPageSyncPortletActions();
_1c.url.loadingIndicatorHide();
if(_1c.UAie6){
_1d.displayAllPWins();
}
}
},editPageSyncPortletActions:function(){
var _24=jetspeed.page.getPortletArray();
if(_24!=null){
for(var i=0;i<_24.length;i++){
_24[i].syncActions();
}
}
},editPageHide:function(){
var _26=this.pageEditorWidgets;
if(_26!=null){
for(var i=0;i<_26.length;i++){
_26[i].hide();
}
}
this.hide();
this.editPageSyncPortletActions();
},editPageShow:function(){
var _28=jetspeed;
var _29=this.pageEditorWidgets;
if(_29!=null){
for(var i=0;i<_29.length;i++){
_29[i].editModeRedisplay();
}
}
this.show();
this.editPageSyncPortletActions();
if(_28.UAie6){
_28.page.displayAllPWins();
}
},editPageDestroy:function(){
var _2b=this.pageEditorWidgets;
if(_2b!=null){
for(var i=0;i<_2b.length;i++){
_2b[i].destroy();
_2b[i]=null;
}
}
this.pageEditorWidgets=null;
this.layoutEditPaneWidgets=null;
this.pageEditPaneWidget=null;
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
},deletePortlet:function(_2d,_2e){
this.deletePortletDialog.portletEntityId=_2d;
this.deletePortletDialog.portletTitle=_2e;
this.deletePortletTitle.innerHTML=_2e;
this._openDialog(this.deletePortletDialog);
},deletePortletConfirmed:function(_2f){
var _30=new jetspeed.widget.RemovePortletContentManager(_2f,this);
_30.getContent();
},deleteLayout:function(_31){
this.deleteLayoutDialog.layoutId=_31;
this.deleteLayoutDialog.layoutTitle=_31;
this.deleteLayoutTitle.innerHTML=_31;
this._openDialog(this.deleteLayoutDialog);
},deleteLayoutConfirmed:function(){
var _32=new jetspeed.widget.RemoveLayoutContentManager(this.deleteLayoutDialog.layoutId,this);
_32.getContent();
},openColumnSizesEditor:function(_33){
var _34=null;
if(_33!=null){
_34=jetspeed.page.layouts[_33];
}
if(_34!=null&&_34.columnSizes!=null&&_34.columnSizes.length>0){
var _35=5;
var _36=0;
for(var i=0;i<_35;i++){
var _38=this.columnSizeDialog["spinner"+i];
var _39=this["spinner"+i+"Field"];
if(i<_34.columnSizes.length){
_38.setValue(_34.columnSizes[i]);
_39.style.display="block";
_38.show();
_36++;
}else{
_39.style.display="none";
_38.hide();
}
}
this.columnSizeDialog.layoutId=_33;
this.columnSizeDialog.columnCount=_36;
this._openDialog(this.columnSizeDialog);
}
},columnSizeConfirmed:function(_3a,_3b){
if(_3a!=null&&_3b!=null&&_3b.length>0){
var _3c=jetspeed.page.layouts[_3a];
var _3d=null;
if(_3c!=null){
_3d=_3c.name;
}
if(_3d!=null){
var _3e="";
for(var i=0;i<_3b.length;i++){
if(i>0){
_3e+=",";
}
_3e+=_3b[i]+"%";
}
var _40=new jetspeed.widget.UpdateFragmentContentManager(_3a,_3d,_3e,this);
_40.getContent();
}
}
},refreshPage:function(){
dojo.lang.setTimeout(this,this._doRefreshPage,10);
},_doRefreshPage:function(){
var _41=jetspeed.page.getPageUrl();
_41=jetspeed.url.addQueryParameter(_41,jetspeed.id.PG_ED_PARAM,"true",true);
window.location.href=_41.toString();
},editMoveModeExit:function(){
var _42=jetspeed;
var _43=_42.UAie6;
if(_43){
_42.page.displayAllPWins(true);
}
var _44;
var _45=[];
var _46=_42.page.getPWins();
for(var i=0;i<_46.length;i++){
_44=_46[i];
_44.restoreFromMinimizeWindowTemporarily();
if(_43&&_44.posStatic){
var _48=_44.domNode.parentNode;
var _49=false;
for(var j=0;j<_45.length;j++){
if(_45[j]==_48){
_49=true;
break;
}
}
if(!_49){
_45.push(_48);
}
}
}
var _4b=this.layoutEditPaneWidgets;
if(_4b!=null){
for(var i=0;i<_4b.length;i++){
_4b[i]._disableMoveMode();
}
}
_42.widget.showAllPortletWindows();
if(_43){
_42.page.displayAllPWins();
if(_45.length>0){
var _4c=new jetspeed.widget.IE6ZappedContentRestorer(_45);
dojo.lang.setTimeout(_4c,_4c.showNext,20);
}
}
},editMoveModeStart:function(){
var _4d=jetspeed;
var _4e=false;
if(_4d.UAie6){
_4d.page.displayAllPWins(true);
}
var _4f=[];
var _50=[];
if(this.dbOn){
var _51=_4d.debugWindow();
if(_51&&(!_4e||!_51.posStatic||_4d.debug.dragWindow)){
_4f.push(_51);
_50.push(_51.widgetId);
}
}
if(!_4e){
var _52;
var _53=_4d.page.getPWins();
for(var i=0;i<_53.length;i++){
_52=_53[i];
if(_52.posStatic){
_4f.push(_52);
_50.push(_52.widgetId);
_52.minimizeWindowTemporarily();
}
}
}
_4d.widget.hideAllPortletWindows(_50);
var _55=this.layoutEditPaneWidgets;
if(_55!=null){
for(var i=0;i<_55.length;i++){
_55[i]._enableMoveMode();
}
}
if(_4d.UAie6){
setTimeout(function(){
_4d.page.displayAllPWins(false,_4f);
},20);
}
},onBrowserWindowResize:function(){
var _56=this.deletePortletDialog;
var _57=this.deleteLayoutDialog;
var _58=this.columnSizeDialog;
if(_56&&_56.isShowing()){
_56.domNode.style.display="none";
_56.domNode.style.display="block";
}
if(_57&&_57.isShowing()){
_57.domNode.style.display="none";
_57.domNode.style.display="block";
}
if(_58&&_58.isShowing()){
_58.domNode.style.display="none";
_58.domNode.style.display="block";
}
var _59=this.pageEditorWidgets;
if(_59!=null){
for(var i=0;i<_59.length;i++){
_59[i].onBrowserWindowResize();
}
}
},_openDialog:function(_5b){
var _5c=jetspeed.UAmoz;
if(_5c){
_5b.domNode.style.position="fixed";
if(!_5b._fixedIPtBug){
var _5d=_5b;
_5d.placeModalDialog=function(){
var _5e=dojo.html.getScroll().offset;
var _5f=dojo.html.getViewport();
var mb;
if(_5d.isShowing()){
mb=dojo.html.getMarginBox(_5d.domNode);
}else{
dojo.html.setVisibility(_5d.domNode,false);
dojo.html.show(_5d.domNode);
mb=dojo.html.getMarginBox(_5d.domNode);
dojo.html.hide(_5d.domNode);
dojo.html.setVisibility(_5d.domNode,true);
}
var x=(_5f.width-mb.width)/2;
var y=(_5f.height-mb.height)/2;
with(_5d.domNode.style){
left=x+"px";
top=y+"px";
}
};
_5d._fixedIPtBug=true;
}
}
_5b.show();
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_63,_64,_65,_66,_67,_68){
this.pageEditorWidget=_63;
var _69=new Array();
if(_64){
_69.push(["pageDecorations"]);
}
if(_65){
_69.push(["portletDecorations"]);
}
if(_66){
_69.push(["layouts"]);
}
if(_67){
_69.push(["desktopPageDecorations","pageDecorations"]);
}
if(_68){
_69.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_69;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _6a="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _6b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_6a;
var _6c=new jetspeed.om.Id("getthemes",{});
var _6d={};
_6d.url=_6b;
_6d.mimetype="text/json";
jetspeed.url.retrieveContent(_6d,this,_6c,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_6e,_6f,_70){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _71=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_71]=_6e;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_72,_73,_74,_75){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_74+" type: "+_72+jetspeed.formatError(_73));
}};
jetspeed.widget.RemovePageContentManager=function(_76){
this.pageEditorWidget=_76;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _77="?action=updatepage&method=remove";
var _78=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_77;
var _79=new jetspeed.om.Id("updatepage-remove-page",{});
var _7a={};
_7a.url=_78;
_7a.mimetype="text/xml";
jetspeed.url.retrieveContent(_7a,this,_79,jetspeed.debugContentDumpIds);
},notifySuccess:function(_7b,_7c,_7d){
if(jetspeed.url.checkAjaxApiResponse(_7c,_7b,true,"updatepage-remove-page")){
var _7e=jetspeed.page.makePageUrl("/");
_7e+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_7e;
}
},notifyFailure:function(_7f,_80,_81,_82){
dojo.raise("RemovePageContentManager notifyFailure url: "+_81+" type: "+_7f+jetspeed.formatError(_80));
}};
jetspeed.widget.IE6ZappedContentRestorer=function(_83){
this.colNodes=_83;
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
jetspeed.widget.AddPageContentManager=function(_84,_85,_86,_87,_88,_89,_8a){
this.pageRealPath=_84;
this.pagePath=_85;
this.pageName=_86;
if(_87==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_87=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_87;
this.pageTitle=_88;
this.pageShortTitle=_89;
this.pageEditorWidget=_8a;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _8b="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_8b+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_8b+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_8b+="&short-title="+escape(this.pageShortTitle);
}
var _8c=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_8b;
var _8d=new jetspeed.om.Id("updatepage-add-page",{});
var _8e={};
_8e.url=_8c;
_8e.mimetype="text/xml";
jetspeed.url.retrieveContent(_8e,this,_8d,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_8f,_90,_91){
if(jetspeed.url.checkAjaxApiResponse(_90,_8f,true,"updatepage-add-page")){
var _92=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_92,".psml",true)){
_92+=".psml";
}
_92+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_92;
}
},notifyFailure:function(_93,_94,_95,_96){
dojo.raise("AddPageContentManager notifyFailure url: "+_95+" type: "+_93+jetspeed.formatError(_94));
}};
jetspeed.widget.MoveLayoutContentManager=function(_97,_98,_99,row,_9b){
this.layoutId=_97;
this.moveToLayoutId=_98;
this.column=_99;
this.row=row;
this.pageEditorWidget=_9b;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _9c="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_9c+="&col="+this.column;
}
if(this.row!=null){
_9c+="&row="+this.row;
}
var _9d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_9c;
var _9e=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _9f={};
_9f.url=_9d;
_9f.mimetype="text/xml";
jetspeed.url.retrieveContent(_9f,this,_9e,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_a0,_a1,_a2){
if(jetspeed.url.checkAjaxApiResponse(_a1,_a0,true,"moveabs-layout")){
}
},notifyFailure:function(_a3,_a4,_a5,_a6){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_a5+" type: "+_a3+jetspeed.formatError(_a4));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_a7,_a8,_a9,_aa){
this.layoutId=_a7;
this.layoutName=_a8;
this.layoutSizes=_a9;
this.pageEditorWidget=_aa;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _ab="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_ab+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_ab+="&sizes="+escape(this.layoutSizes);
}
var _ac=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_ab;
var _ad=new jetspeed.om.Id("updatepage-update-fragment",{});
var _ae={};
_ae.url=_ac;
_ae.mimetype="text/xml";
jetspeed.url.retrieveContent(_ae,this,_ad,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_af,_b0,_b1){
if(jetspeed.url.checkAjaxApiResponse(_b0,_af,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_b2,_b3,_b4,_b5){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_b4+" type: "+_b2+jetspeed.formatError(_b3));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_b6,_b7,_b8){
this.refreshPage=((_b8.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_b6;
this.portletDecorator=_b7;
this.pageEditorWidget=_b8;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _b9="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_b9+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_b9+="&portlet-decorator="+escape(this.portletDecorator);
}
var _ba=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b9;
var _bb=new jetspeed.om.Id("updatepage-info",{});
var _bc={};
_bc.url=_ba;
_bc.mimetype="text/xml";
jetspeed.url.retrieveContent(_bc,this,_bb,jetspeed.debugContentDumpIds);
},notifySuccess:function(_bd,_be,_bf){
if(jetspeed.url.checkAjaxApiResponse(_be,_bd,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_c0,_c1,_c2,_c3){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_c2+" type: "+_c0+jetspeed.formatError(_c1));
}};
jetspeed.widget.RemovePortletContentManager=function(_c4,_c5){
this.portletEntityId=_c4;
this.pageEditorWidget=_c5;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _c6="?action=remove&id="+this.portletEntityId;
var _c7=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_c6;
var _c8=new jetspeed.om.Id("removeportlet",{});
var _c9={};
_c9.url=_c7;
_c9.mimetype="text/xml";
jetspeed.url.retrieveContent(_c9,this,_c8,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_ca,_cb,_cc){
if(jetspeed.url.checkAjaxApiResponse(_cb,_ca,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_cd,_ce,_cf,_d0){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_cf+" type: "+_cd+jetspeed.formatError(_ce));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_d1,_d2){
this.layoutId=_d1;
this.pageEditorWidget=_d2;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _d3="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _d4=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_d3;
var _d5=new jetspeed.om.Id("removelayout",{});
var _d6={};
_d6.url=_d4;
_d6.mimetype="text/xml";
jetspeed.url.retrieveContent(_d6,this,_d5,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_d7,_d8,_d9){
if(jetspeed.url.checkAjaxApiResponse(_d8,_d7,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_da,_db,_dc,_dd){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_dc+" type: "+_da+jetspeed.formatError(_db));
}};
jetspeed.widget.AddLayoutContentManager=function(_de,_df,_e0){
this.parentLayoutId=_de;
this.layoutName=_df;
this.pageEditorWidget=_e0;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _e1="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _e2=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_e1;
var _e3=new jetspeed.om.Id("addlayout",{});
var _e4={};
_e4.url=_e2;
_e4.mimetype="text/xml";
jetspeed.url.retrieveContent(_e4,this,_e3,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_e5,_e6,_e7){
if(jetspeed.url.checkAjaxApiResponse(_e6,_e5,true,"addlayout")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_e8,_e9,_ea,_eb){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_ea+" type: "+_e8+jetspeed.formatError(_e9));
}};

