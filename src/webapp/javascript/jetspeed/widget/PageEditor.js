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
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets();
}
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
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets();
}
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
_32=jetspeed.url.addQueryParameter(_32,jetspeed.id.PG_ED_PARAM,"true",true);
window.location.href=_32.toString();
},editModeNormal:function(){
var _33=dojo.render.html.ie60;
if(_33){
jetspeed.page.displayAllPortlets(true);
}
var _34=jetspeed.page.getPortletArray();
var _35=[];
for(var i=0;i<_34.length;i++){
var _37=_34[i].getPortletWindow();
if(_37!=null){
_37.restoreFromMinimizeWindowTemporarily();
if(_33&&_37.windowPositionStatic){
var _38=_37.domNode.parentNode;
var _39=false;
for(var j=0;j<_35.length;j++){
if(_35[j]==_38){
_39=true;
break;
}
}
if(!_39){
_35.push(_38);
}
}
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _3b=this.layoutEditPaneWidgets[i];
if(_3b.layoutMoveContainer!=null){
_3b.layoutMoveContainer.domNode.style.display="none";
}
}
}
if(_33){
jetspeed.page.displayAllPortlets();
if(_35.length>0){
var _3c=new jetspeed.widget.IE6ZappedContentRestorer(_35);
dojo.lang.setTimeout(_3c,_3c.showNext,20);
}
}
},editModeLayoutMove:function(){
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets(true);
}
var _3d=jetspeed.page.getPortletArray();
for(var i=0;i<_3d.length;i++){
var _3f=_3d[i].getPortletWindow();
if(_3f!=null){
_3f.minimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _40=this.layoutEditPaneWidgets[i];
if(!_40.isRootLayout&&_40.layoutMoveContainer!=null){
_40.layoutMoveContainer.domNode.style.display="block";
}
}
}
if(dojo.render.html.ie60){
jetspeed.page.displayAllPortlets();
}
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_41,_42,_43,_44,_45,_46){
this.pageEditorWidget=_41;
var _47=new Array();
if(_42){
_47.push(["pageDecorations"]);
}
if(_43){
_47.push(["portletDecorations"]);
}
if(_44){
_47.push(["layouts"]);
}
if(_45){
_47.push(["desktopPageDecorations","pageDecorations"]);
}
if(_46){
_47.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_47;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _48="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _49=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_48;
var _4a=new jetspeed.om.Id("getthemes",{});
var _4b={};
_4b.url=_49;
_4b.mimetype="text/json";
jetspeed.url.retrieveContent(_4b,this,_4a,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_4c,_4d,_4e){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _4f=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_4f]=_4c;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_50,_51,_52,_53){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_52+" type: "+_50+jetspeed.url.formatBindError(_51));
}};
jetspeed.widget.RemovePageContentManager=function(_54){
this.pageEditorWidget=_54;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _55="?action=updatepage&method=remove";
var _56=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_55;
var _57=new jetspeed.om.Id("updatepage-remove-page",{});
var _58={};
_58.url=_56;
_58.mimetype="text/xml";
jetspeed.url.retrieveContent(_58,this,_57,jetspeed.debugContentDumpIds);
},notifySuccess:function(_59,_5a,_5b){
if(jetspeed.url.checkAjaxApiResponse(_5a,_59,true,"updatepage-remove-page")){
var _5c=jetspeed.page.makePageUrl("/");
_5c+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_5c;
}
},notifyFailure:function(_5d,_5e,_5f,_60){
dojo.raise("RemovePageContentManager notifyFailure url: "+_5f+" type: "+_5d+jetspeed.url.formatBindError(_5e));
}};
jetspeed.widget.IE6ZappedContentRestorer=function(_61){
this.colNodes=_61;
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
jetspeed.widget.AddPageContentManager=function(_62,_63,_64,_65,_66,_67,_68){
this.pageRealPath=_62;
this.pagePath=_63;
this.pageName=_64;
if(_65==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_65=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_65;
this.pageTitle=_66;
this.pageShortTitle=_67;
this.pageEditorWidget=_68;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _69="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_69+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_69+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_69+="&short-title="+escape(this.pageShortTitle);
}
var _6a=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_69;
var _6b=new jetspeed.om.Id("updatepage-add-page",{});
var _6c={};
_6c.url=_6a;
_6c.mimetype="text/xml";
jetspeed.url.retrieveContent(_6c,this,_6b,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_6d,_6e,_6f){
if(jetspeed.url.checkAjaxApiResponse(_6e,_6d,true,"updatepage-add-page")){
var _70=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_70,".psml",true)){
_70+=".psml";
}
_70+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_70;
}
},notifyFailure:function(_71,_72,_73,_74){
dojo.raise("AddPageContentManager notifyFailure url: "+_73+" type: "+_71+jetspeed.url.formatBindError(_72));
}};
jetspeed.widget.MoveLayoutContentManager=function(_75,_76,_77,row,_79){
this.layoutId=_75;
this.moveToLayoutId=_76;
this.column=_77;
this.row=row;
this.pageEditorWidget=_79;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _7a="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_7a+="&col="+this.column;
}
if(this.row!=null){
_7a+="&row="+this.row;
}
var _7b=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7a;
var _7c=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _7d={};
_7d.url=_7b;
_7d.mimetype="text/xml";
jetspeed.url.retrieveContent(_7d,this,_7c,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_7e,_7f,_80){
if(jetspeed.url.checkAjaxApiResponse(_7f,_7e,true,"moveabs-layout")){
}
},notifyFailure:function(_81,_82,_83,_84){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_83+" type: "+_81+jetspeed.url.formatBindError(_82));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_85,_86,_87,_88){
this.layoutId=_85;
this.layoutName=_86;
this.layoutSizes=_87;
this.pageEditorWidget=_88;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _89="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_89+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_89+="&sizes="+escape(this.layoutSizes);
}
var _8a=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_89;
var _8b=new jetspeed.om.Id("updatepage-update-fragment",{});
var _8c={};
_8c.url=_8a;
_8c.mimetype="text/xml";
jetspeed.url.retrieveContent(_8c,this,_8b,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_8d,_8e,_8f){
if(jetspeed.url.checkAjaxApiResponse(_8e,_8d,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_90,_91,_92,_93){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_92+" type: "+_90+jetspeed.url.formatBindError(_91));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_94,_95,_96){
this.refreshPage=((_96.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_94;
this.portletDecorator=_95;
this.pageEditorWidget=_96;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _97="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_97+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_97+="&portlet-decorator="+escape(this.portletDecorator);
}
var _98=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_97;
var _99=new jetspeed.om.Id("updatepage-info",{});
var _9a={};
_9a.url=_98;
_9a.mimetype="text/xml";
jetspeed.url.retrieveContent(_9a,this,_99,jetspeed.debugContentDumpIds);
},notifySuccess:function(_9b,_9c,_9d){
if(jetspeed.url.checkAjaxApiResponse(_9c,_9b,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_9e,_9f,_a0,_a1){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_a0+" type: "+_9e+jetspeed.url.formatBindError(_9f));
}};
jetspeed.widget.RemovePortletContentManager=function(_a2,_a3){
this.portletEntityId=_a2;
this.pageEditorWidget=_a3;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _a4="?action=remove&id="+this.portletEntityId;
var _a5=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_a4;
var _a6=new jetspeed.om.Id("removeportlet",{});
var _a7={};
_a7.url=_a5;
_a7.mimetype="text/xml";
jetspeed.url.retrieveContent(_a7,this,_a6,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_a8,_a9,_aa){
if(jetspeed.url.checkAjaxApiResponse(_a9,_a8,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_ab,_ac,_ad,_ae){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_ad+" type: "+_ab+jetspeed.url.formatBindError(_ac));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_af,_b0){
this.layoutId=_af;
this.pageEditorWidget=_b0;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _b1="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _b2=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b1;
var _b3=new jetspeed.om.Id("removelayout",{});
var _b4={};
_b4.url=_b2;
_b4.mimetype="text/xml";
jetspeed.url.retrieveContent(_b4,this,_b3,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_b5,_b6,_b7){
if(jetspeed.url.checkAjaxApiResponse(_b6,_b5,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_b8,_b9,_ba,_bb){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_ba+" type: "+_b8+jetspeed.url.formatBindError(_b9));
}};
jetspeed.widget.AddLayoutContentManager=function(_bc,_bd,_be){
this.parentLayoutId=_bc;
this.layoutName=_bd;
this.pageEditorWidget=_be;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _bf="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _c0=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_bf;
var _c1=new jetspeed.om.Id("addlayout",{});
var _c2={};
_c2.url=_c0;
_c2.mimetype="text/xml";
jetspeed.url.retrieveContent(_c2,this,_c1,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_c3,_c4,_c5){
if(jetspeed.url.checkAjaxApiResponse(_c4,_c3,true,"addportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_c6,_c7,_c8,_c9){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_c8+" type: "+_c6+jetspeed.url.formatBindError(_c7));
}};

