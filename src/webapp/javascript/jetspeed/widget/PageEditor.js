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
var _11=jetspeed;
_11.url.loadingIndicatorHide();
var _12=_11.prefs.getLayoutRootUrl()+"/images/desktop/";
var _13=new Array();
var _14=new Array();
var _15=dojo.widget.createWidget("jetspeed:PageEditPane",{layoutDecoratorDefinitions:_11.page.themeDefinitions.pageDecorations,portletDecoratorDefinitions:_11.page.themeDefinitions.portletDecorations,layoutImagesRoot:_12});
_15.pageEditorWidget=this;
dojo.dom.insertAfter(_15.domNode,this.domNode);
_13.push(_15);
var _16=dojo.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_root",layoutId:_11.page.rootFragmentId,isRootLayout:true,layoutDefinitions:_11.page.themeDefinitions.layouts,layoutImagesRoot:_12});
_16.pageEditorWidget=this;
dojo.dom.insertAfter(_16.domNode,_15.domNode);
_13.push(_16);
_14.push(_16);
if(_11.prefs.windowTiling){
for(var i=0;i<_11.page.columns.length;i++){
var col=_11.page.columns[i];
if(col.layoutHeader){
var _19=dojo.widget.createWidget("jetspeed:LayoutEditPane",{widgetId:"layoutEdit_"+i,layoutId:col.layoutId,layoutDefinitions:_11.page.themeDefinitions.layouts,layoutImagesRoot:_12});
_19.pageEditorWidget=this;
if(col.domNode.firstChild!=null){
col.domNode.insertBefore(_19.domNode,col.domNode.firstChild);
}else{
col.domNode.appendChild(_19.domNode);
}
_19.initializeDrag();
_13.push(_19);
_14.push(_19);
}
}
}
this.pageEditorWidgets=_13;
this.layoutEditPaneWidgets=_14;
this.editPageSyncPortletActions();
if(_11.UAie6){
_11.page.displayAllPWins();
}
},editPageSyncPortletActions:function(){
var _1a=jetspeed.page.getPortletArray();
if(_1a!=null){
for(var i=0;i<_1a.length;i++){
_1a[i].syncActions();
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
var _1d=jetspeed;
if(this.pageEditorWidgets!=null){
for(var i=0;i<this.pageEditorWidgets.length;i++){
this.pageEditorWidgets[i].editModeRedisplay();
}
}
this.show();
this.editPageSyncPortletActions();
if(_1d.UAie6){
_1d.page.displayAllPWins();
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
},deletePortlet:function(_20,_21){
this.deletePortletDialog.portletEntityId=_20;
this.deletePortletDialog.portletTitle=_21;
this.deletePortletTitle.innerHTML=_21;
this.deletePortletDialog.show();
},deletePortletConfirmed:function(_22){
var _23=new jetspeed.widget.RemovePortletContentManager(_22,this);
_23.getContent();
},deleteLayout:function(_24){
this.deleteLayoutDialog.layoutId=_24;
this.deleteLayoutDialog.layoutTitle=_24;
this.deleteLayoutTitle.innerHTML=_24;
this.deleteLayoutDialog.show();
},deleteLayoutConfirmed:function(){
var _25=new jetspeed.widget.RemoveLayoutContentManager(this.deleteLayoutDialog.layoutId,this);
_25.getContent();
},openColumnSizesEditor:function(_26){
var _27=null;
if(_26!=null){
_27=jetspeed.page.layouts[_26];
}
if(_27!=null&&_27.columnSizes!=null&&_27.columnSizes.length>0){
var _28=5;
var _29=0;
for(var i=0;i<_28;i++){
var _2b=this.columnSizeDialog["spinner"+i];
var _2c=this["spinner"+i+"Field"];
if(i<_27.columnSizes.length){
_2b.setValue(_27.columnSizes[i]);
_2c.style.display="block";
_2b.show();
_29++;
}else{
_2c.style.display="none";
_2b.hide();
}
}
this.columnSizeDialog.layoutId=_26;
this.columnSizeDialog.columnCount=_29;
this.columnSizeDialog.show();
}
},columnSizeConfirmed:function(_2d,_2e){
if(_2d!=null&&_2e!=null&&_2e.length>0){
var _2f=jetspeed.page.layouts[_2d];
var _30=null;
if(_2f!=null){
_30=_2f.name;
}
if(_30!=null){
var _31="";
for(var i=0;i<_2e.length;i++){
if(i>0){
_31+=",";
}
_31+=_2e[i]+"%";
}
var _33=new jetspeed.widget.UpdateFragmentContentManager(_2d,_30,_31,this);
_33.getContent();
}
}
},refreshPage:function(){
dojo.lang.setTimeout(this,this._doRefreshPage,10);
},_doRefreshPage:function(){
var _34=jetspeed.page.getPageUrl();
_34=jetspeed.url.addQueryParameter(_34,jetspeed.id.PG_ED_PARAM,"true",true);
window.location.href=_34.toString();
},editModeNormal:function(){
var _35=jetspeed;
var _36=_35.UAie6;
if(_36){
_35.page.displayAllPWins(true);
}
var _37=_35.page.getPortletArray();
var _38=[];
for(var i=0;i<_37.length;i++){
var _3a=_37[i].getPWin();
if(_3a!=null){
_3a.restoreFromMinimizeWindowTemporarily();
if(_36&&_3a.windowPositionStatic){
var _3b=_3a.domNode.parentNode;
var _3c=false;
for(var j=0;j<_38.length;j++){
if(_38[j]==_3b){
_3c=true;
break;
}
}
if(!_3c){
_38.push(_3b);
}
}
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _3e=this.layoutEditPaneWidgets[i];
if(_3e.layoutMoveContainer!=null){
_3e.layoutMoveContainer.domNode.style.display="none";
}
}
}
if(_36){
_35.page.displayAllPWins();
if(_38.length>0){
var _3f=new jetspeed.widget.IE6ZappedContentRestorer(_38);
dojo.lang.setTimeout(_3f,_3f.showNext,20);
}
}
},editModeLayoutMove:function(){
var _40=jetspeed;
if(_40.UAie6){
_40.page.displayAllPWins(true);
}
var _41=_40.page.getPortletArray();
for(var i=0;i<_41.length;i++){
var _43=_41[i].getPWin();
if(_43!=null){
_43.minimizeWindowTemporarily();
}
}
if(this.layoutEditPaneWidgets!=null){
for(var i=0;i<this.layoutEditPaneWidgets.length;i++){
var _44=this.layoutEditPaneWidgets[i];
if(!_44.isRootLayout&&_44.layoutMoveContainer!=null){
_44.layoutMoveContainer.domNode.style.display="block";
}
}
}
if(_40.UAie6){
_40.page.displayAllPWins();
}
}});
jetspeed.widget.EditPageGetThemesContentManager=function(_45,_46,_47,_48,_49,_4a){
this.pageEditorWidget=_45;
var _4b=new Array();
if(_46){
_4b.push(["pageDecorations"]);
}
if(_47){
_4b.push(["portletDecorations"]);
}
if(_48){
_4b.push(["layouts"]);
}
if(_49){
_4b.push(["desktopPageDecorations","pageDecorations"]);
}
if(_4a){
_4b.push(["desktopPortletDecorations","portletDecorations"]);
}
this.getThemeTypes=_4b;
this.getThemeTypeNextIndex=0;
};
jetspeed.widget.EditPageGetThemesContentManager.prototype={getContent:function(){
if(this.getThemeTypes!=null&&this.getThemeTypes.length>this.getThemeTypeNextIndex){
var _4c="?action=getthemes&type="+this.getThemeTypes[this.getThemeTypeNextIndex][0]+"&format=json";
var _4d=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_4c;
var _4e=new jetspeed.om.Id("getthemes",{});
var _4f={};
_4f.url=_4d;
_4f.mimetype="text/json";
jetspeed.url.retrieveContent(_4f,this,_4e,jetspeed.debugContentDumpIds);
}else{
this.pageEditorWidget.editPageBuild();
}
},notifySuccess:function(_50,_51,_52){
if(jetspeed.page.themeDefinitions==null){
jetspeed.page.themeDefinitions={};
}
var _53=((this.getThemeTypes[this.getThemeTypeNextIndex].length>1)?this.getThemeTypes[this.getThemeTypeNextIndex][1]:this.getThemeTypes[this.getThemeTypeNextIndex][0]);
jetspeed.page.themeDefinitions[_53]=_50;
this.getThemeTypeNextIndex++;
this.getContent();
},notifyFailure:function(_54,_55,_56,_57){
dojo.raise("EditPageGetThemesContentManager notifyFailure url: "+_56+" type: "+_54+jetspeed.url.formatBindError(_55));
}};
jetspeed.widget.RemovePageContentManager=function(_58){
this.pageEditorWidget=_58;
};
jetspeed.widget.RemovePageContentManager.prototype={getContent:function(){
var _59="?action=updatepage&method=remove";
var _5a=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_59;
var _5b=new jetspeed.om.Id("updatepage-remove-page",{});
var _5c={};
_5c.url=_5a;
_5c.mimetype="text/xml";
jetspeed.url.retrieveContent(_5c,this,_5b,jetspeed.debugContentDumpIds);
},notifySuccess:function(_5d,_5e,_5f){
if(jetspeed.url.checkAjaxApiResponse(_5e,_5d,true,"updatepage-remove-page")){
var _60=jetspeed.page.makePageUrl("/");
_60+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_60;
}
},notifyFailure:function(_61,_62,_63,_64){
dojo.raise("RemovePageContentManager notifyFailure url: "+_63+" type: "+_61+jetspeed.url.formatBindError(_62));
}};
jetspeed.widget.IE6ZappedContentRestorer=function(_65){
this.colNodes=_65;
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
jetspeed.widget.AddPageContentManager=function(_66,_67,_68,_69,_6a,_6b,_6c){
this.pageRealPath=_66;
this.pagePath=_67;
this.pageName=_68;
if(_69==null){
if(jetspeed.page.themeDefinitions!=null&&jetspeed.page.themeDefinitions.layouts!=null&&jetspeed.page.themeDefinitions.layouts.length>0&&jetspeed.page.themeDefinitions.layouts[0]!=null&&jetspeed.page.themeDefinitions.layouts[0].length==2){
_69=jetspeed.page.themeDefinitions.layouts[0][1];
}
}
this.layoutName=_69;
this.pageTitle=_6a;
this.pageShortTitle=_6b;
this.pageEditorWidget=_6c;
};
jetspeed.widget.AddPageContentManager.prototype={getContent:function(){
if(this.pageRealPath!=null&&this.pageName!=null){
var _6d="?action=updatepage&method=add&path="+escape(this.pageRealPath)+"&name="+escape(this.pageName);
if(this.layoutName!=null){
_6d+="&defaultLayout="+escape(this.layoutName);
}
if(this.pageTitle!=null){
_6d+="&title="+escape(this.pageTitle);
}
if(this.pageShortTitle!=null){
_6d+="&short-title="+escape(this.pageShortTitle);
}
var _6e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+_6d;
var _6f=new jetspeed.om.Id("updatepage-add-page",{});
var _70={};
_70.url=_6e;
_70.mimetype="text/xml";
jetspeed.url.retrieveContent(_70,this,_6f,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_71,_72,_73){
if(jetspeed.url.checkAjaxApiResponse(_72,_71,true,"updatepage-add-page")){
var _74=jetspeed.page.makePageUrl(this.pagePath);
if(!dojo.string.endsWith(_74,".psml",true)){
_74+=".psml";
}
_74+="?"+jetspeed.id.PG_ED_PARAM+"=true";
window.location.href=_74;
}
},notifyFailure:function(_75,_76,_77,_78){
dojo.raise("AddPageContentManager notifyFailure url: "+_77+" type: "+_75+jetspeed.url.formatBindError(_76));
}};
jetspeed.widget.MoveLayoutContentManager=function(_79,_7a,_7b,row,_7d){
this.layoutId=_79;
this.moveToLayoutId=_7a;
this.column=_7b;
this.row=row;
this.pageEditorWidget=_7d;
};
jetspeed.widget.MoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null&&this.moveToLayoutId!=null){
var _7e="?action=moveabs&id="+this.layoutId+"&layoutid="+this.moveToLayoutId;
if(this.column!=null){
_7e+="&col="+this.column;
}
if(this.row!=null){
_7e+="&row="+this.row;
}
var _7f=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_7e;
var _80=new jetspeed.om.Id("moveabs-layout",this.layoutId);
var _81={};
_81.url=_7f;
_81.mimetype="text/xml";
jetspeed.url.retrieveContent(_81,this,_80,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_82,_83,_84){
if(jetspeed.url.checkAjaxApiResponse(_83,_82,true,"moveabs-layout")){
}
},notifyFailure:function(_85,_86,_87,_88){
dojo.raise("MoveLayoutContentManager notifyFailure url: "+_87+" type: "+_85+jetspeed.url.formatBindError(_86));
}};
jetspeed.widget.UpdateFragmentContentManager=function(_89,_8a,_8b,_8c){
this.layoutId=_89;
this.layoutName=_8a;
this.layoutSizes=_8b;
this.pageEditorWidget=_8c;
};
jetspeed.widget.UpdateFragmentContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _8d="?action=updatepage&method=update-fragment&id="+this.layoutId;
if(this.layoutName!=null){
_8d+="&layout="+escape(this.layoutName);
}
if(this.layoutSizes!=null){
_8d+="&sizes="+escape(this.layoutSizes);
}
var _8e=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_8d;
var _8f=new jetspeed.om.Id("updatepage-update-fragment",{});
var _90={};
_90.url=_8e;
_90.mimetype="text/xml";
jetspeed.url.retrieveContent(_90,this,_8f,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_91,_92,_93){
if(jetspeed.url.checkAjaxApiResponse(_92,_91,true,"updatepage-update-fragment")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_94,_95,_96,_97){
dojo.raise("UpdateFragmentContentManager notifyFailure url: "+_96+" type: "+_94+jetspeed.url.formatBindError(_95));
}};
jetspeed.widget.UpdatePageInfoContentManager=function(_98,_99,_9a){
this.refreshPage=((_9a.editorInitiatedFromDesktop)?true:false);
this.layoutDecorator=_98;
this.portletDecorator=_99;
this.pageEditorWidget=_9a;
};
jetspeed.widget.UpdatePageInfoContentManager.prototype={getContent:function(){
var _9b="?action=updatepage&method=info";
if(this.layoutDecorator!=null){
_9b+="&layout-decorator="+escape(this.layoutDecorator);
}
if(this.portletDecorator!=null){
_9b+="&portlet-decorator="+escape(this.portletDecorator);
}
var _9c=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_9b;
var _9d=new jetspeed.om.Id("updatepage-info",{});
var _9e={};
_9e.url=_9c;
_9e.mimetype="text/xml";
jetspeed.url.retrieveContent(_9e,this,_9d,jetspeed.debugContentDumpIds);
},notifySuccess:function(_9f,_a0,_a1){
if(jetspeed.url.checkAjaxApiResponse(_a0,_9f,true,"updatepage-info")){
if(this.refreshPage){
this.pageEditorWidget.refreshPage();
}
}
},notifyFailure:function(_a2,_a3,_a4,_a5){
dojo.raise("UpdatePageInfoContentManager notifyFailure url: "+_a4+" type: "+_a2+jetspeed.url.formatBindError(_a3));
}};
jetspeed.widget.RemovePortletContentManager=function(_a6,_a7){
this.portletEntityId=_a6;
this.pageEditorWidget=_a7;
};
jetspeed.widget.RemovePortletContentManager.prototype={getContent:function(){
if(this.portletEntityId!=null){
var _a8="?action=remove&id="+this.portletEntityId;
var _a9=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_a8;
var _aa=new jetspeed.om.Id("removeportlet",{});
var _ab={};
_ab.url=_a9;
_ab.mimetype="text/xml";
jetspeed.url.retrieveContent(_ab,this,_aa,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_ac,_ad,_ae){
if(jetspeed.url.checkAjaxApiResponse(_ad,_ac,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_af,_b0,_b1,_b2){
dojo.raise("RemovePortletContentManager notifyFailure url: "+_b1+" type: "+_af+jetspeed.url.formatBindError(_b0));
}};
jetspeed.widget.RemoveLayoutContentManager=function(_b3,_b4){
this.layoutId=_b3;
this.pageEditorWidget=_b4;
};
jetspeed.widget.RemoveLayoutContentManager.prototype={getContent:function(){
if(this.layoutId!=null){
var _b5="?action=updatepage&method=remove-fragment&id="+this.layoutId;
var _b6=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_b5;
var _b7=new jetspeed.om.Id("removelayout",{});
var _b8={};
_b8.url=_b6;
_b8.mimetype="text/xml";
jetspeed.url.retrieveContent(_b8,this,_b7,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_b9,_ba,_bb){
if(jetspeed.url.checkAjaxApiResponse(_ba,_b9,true,"removeportlet")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_bc,_bd,_be,_bf){
dojo.raise("RemoveLayoutContentManager notifyFailure url: "+_be+" type: "+_bc+jetspeed.url.formatBindError(_bd));
}};
jetspeed.widget.AddLayoutContentManager=function(_c0,_c1,_c2){
this.parentLayoutId=_c0;
this.layoutName=_c1;
this.pageEditorWidget=_c2;
};
jetspeed.widget.AddLayoutContentManager.prototype={getContent:function(){
if(this.parentLayoutId!=null){
var _c3="?action=updatepage&method=add-fragment&layoutid="+this.parentLayoutId+(this.layoutName!=null?("&layout="+this.layoutName):"");
var _c4=jetspeed.url.basePortalUrl()+jetspeed.url.path.AJAX_API+jetspeed.page.getPath()+_c3;
var _c5=new jetspeed.om.Id("addlayout",{});
var _c6={};
_c6.url=_c4;
_c6.mimetype="text/xml";
jetspeed.url.retrieveContent(_c6,this,_c5,jetspeed.debugContentDumpIds);
}
},notifySuccess:function(_c7,_c8,_c9){
if(jetspeed.url.checkAjaxApiResponse(_c8,_c7,true,"addlayout")){
this.pageEditorWidget.refreshPage();
}
},notifyFailure:function(_ca,_cb,_cc,_cd){
dojo.raise("AddLayoutContentManager notifyFailure url: "+_cc+" type: "+_ca+jetspeed.url.formatBindError(_cb));
}};

