<%--
Copyright 2004 The Apache Software Foundation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
--%>
<%@ page session="false"%>
<%@ taglib uri='/WEB-INF/portlet.tld' prefix='portlet'%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="org.apache.jetspeed.demo.css.resources.CSSResources" />
<portlet:defineObjects/>
<br/>
<div class="portlet-section-header"><fmt:message key="css.label.CSSStyleDefinitions"/></div><br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC1"/></div><br/>
<div class="portlet-section-text">
<a href=""><fmt:message key="css.label.LinkTest"/></a><br/>
</div>
<br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC2"/></div><br/>
<div class="portlet-section-text">
portlet-font <fmt:message key="css.label.style"/>: <font class="portlet-font"><fmt:message key="css.label.NormalText"/></font><br/>
portlet-font-dim <fmt:message key="css.label.style"/>: <font class="portlet-font-dim"><fmt:message key="css.label.DimText"/></font><br/>
</div>
<br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC3"/></div><br/>
<div class="portlet-section-text">
portlet-msg-status <fmt:message key="css.label.style"/>: <div class="portlet-msg-status"><fmt:message key="css.label.Progress"/></div><br/>
portlet-msg-info <fmt:message key="css.label.style"/>: <div class="portlet-msg-info"><fmt:message key="css.label.Infoabout"/></div><br/>
portlet-msg-error <fmt:message key="css.label.style"/>: <div class="portlet-msg-error"><fmt:message key="css.label.PortalNotAvailable"/></div><br/>
portlet-msg-alert <fmt:message key="css.label.style"/>: <div class="portlet-msg-alert"><fmt:message key="css.label.TimeOutOccurredTryAgainLater"/></div><br/>
portlet-msg-success <fmt:message key="css.label.style"/>: <div class="portlet-msg-success"><fmt:message key="css.label.OperationCompletedSuccessfully"/></div><br/>
</div>
<br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC4"/></div><br/>
<div class="portlet-section-text">
portlet-section-header <fmt:message key="css.label.style"/>: <div class="portlet-section-header"><fmt:message key="css.label.TableOrSectionHeader"/></div><br/>
portlet-section-body <fmt:message key="css.label.style"/>: <div class="portlet-section-body"><fmt:message key="css.label.NormalTextInATableCell"/></div><br/>
portlet-section-alternate <fmt:message key="css.label.style"/>: <div class="portlet-section-alternate"><fmt:message key="css.label.TextInEveryOtherRowInTheCell"/></div><br/>
portlet-section-selected <fmt:message key="css.label.style"/>: <div class="portlet-section-selected"><fmt:message key="css.label.TextInASelectedCellRange"/></div><br/>
portlet-section-subheader <fmt:message key="css.label.style"/>: <div class="portlet-section-subheader"><fmt:message key="css.label.TextOfASubheading"/></div><br/>
portlet-section-footer <fmt:message key="css.label.style"/>: <div class="portlet-section-footer"><fmt:message key="css.label.TableOrSectionFootnote"/></div><br/>
portlet-section-text <fmt:message key="css.label.style"/>: <div class="portlet-section-text"><fmt:message key="css.label.TextThatBelongsToTheTableButDoesNotFallInOneOfTheOtherCategories"/></div><br/>
</div>
<br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC5"/></div><br/>
<div class="portlet-section-text">
portlet-form-label <fmt:message key="css.label.style"/>: <font class="portlet-form-label"><fmt:message key="css.label.ThisIsForm"/></font><br/>
portlet-form-label-field <fmt:message key="css.label.style"/>: <input type="text" value="<fmt:message key="css.label.Value"/>" class="portlet-form-label"/><br/>
portlet-form-button <fmt:message key="css.label.style"/>: <input type="button" value="<fmt:message key="css.label.Button"/>" class="portlet-form-button"/><br/>
portlet-icon-label <fmt:message key="css.label.style"/>: <font class="portlet-icon-label"><fmt:message key="css.label.Save"/></font><br/>
portlet-dlg-icon-label <fmt:message key="css.label.style"/>: <font class="portlet-dlg-icon-label"><fmt:message key="css.label.OK"/></font><br/>
portlet-form-field-label <fmt:message key="css.label.style"/>: <font class="portlet-form-field-label"><fmt:message key="css.label.Checkbox1"/></font><br/>
portlet-form-field <fmt:message key="css.label.style"/>: <font class="portlet-form-field"><fmt:message key="css.label.Label1"/></font><br/>
</div>
<br/>
<div class="portlet-section-subheader"><fmt:message key="css.label.PLTC6"/></div><br/>
<div class="portlet-section-text">
portlet-menu <fmt:message key="css.label.style"/>: <div class="portlet-menu"><fmt:message key="css.label.GeneralMenuSettings"/></div><br/>
portlet-menu-item <fmt:message key="css.label.style"/>: <div class="portlet-menu-item"><fmt:message key="css.label.NormalUnselectedMenuItem"/></div><br/>
portlet-menu-item-selected <fmt:message key="css.label.style"/>: <div class="portlet-menu-item-selected"><fmt:message key="css.label.SelectedMenuItem"/></div><br/>
portlet-menu-item-hover <fmt:message key="css.label.style"/>: <div class="portlet-menu-item-hover"><fmt:message key="css.label.NormalUnselectedMenuItemWhenTheMouseHoversOverIt"/></div><br/>
portlet-menu-item-hover-selected <fmt:message key="css.label.style"/>: <div class="portlet-menu-item-hover-selected"><fmt:message key="css.label.SelectedMenuItemWhenTheMouseHoversOverIt"/></div><br/>
portlet-menu-cascade-item <fmt:message key="css.label.style"/>: <div class="portlet-menu-cascade-item"><fmt:message key="css.label.NormalUnselectedMenuItemThatHasSubMenu"/></div><br/>
portlet-menu-cascade-item-selected <fmt:message key="css.label.style"/>: <div class="portlet-menu-cascade-item-selected"><fmt:message key="css.label.SelectedSubMenuItemThatHasSubMenu"/></div><br/>
portlet-menu-description <fmt:message key="css.label.style"/>: <div class="portlet-menu-description"><fmt:message key="css.label.DescriptiveTextForTheMenu"/></div><br/>
portlet-menu-caption <fmt:message key="css.label.style"/>: <div class="portlet-menu-caption"><fmt:message key="css.label.MenuCaption"/></div><br/>
</div>
<div class="portlet-section-subheader"><fmt:message key="css.label.Examples"/></div><br/>
<div class="portlet-section-text">
<div class="portlet-section-header"><fmt:message key="css.label.ExampleForm"/></div>
<table border="0" cellspacing="2" cellpadding="3">
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label"><fmt:message key="css.label.JetspeedID"/></font></th>
    <td>
      <input type="text" name="#" value="admin" size="15" maxlength="15"  class="portlet-form-label-field"/>
      <p class="portlet-form-field"><fmt:message key="css.label.IDConsistsOf"/></p>
    </td>
  </tr>
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label"><fmt:message key="css.label.Password"/></font></th>
    <td>
      <input type="password" name="#" value="abcdefg" size="10" maxlength="10" class="portlet-form-label-field"/> 
    </td>
  </tr>
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label"><fmt:message key="css.label.Language"/></font></th>
    <td>
      <div>
        <input type="radio" name="#" checked="checked" /> <font class="portlet-form-field-label"><fmt:message key="css.label.English"/></font>
      </div>
      <div>
        <input type="radio" name="#" value="J" /> <font class="portlet-form-field-label"><fmt:message key="css.label.Japanese"/></font>
      </div>
      <div>
        <input type="radio" name="#" value="F" /> <font class="portlet-form-field-label"><fmt:message key="css.label.French"/></font>
      </div>
      <p class="portlet-form-field"><fmt:message key="css.label.PleaseSelectYourPreferredLanguage"/></p>
    </td>
  </tr>
</table>
<div class="portlet-section-footer">
  <input type="submit" value="<fmt:message key="css.label.Submit"/>" class="portlet-dlg-icon-label"/>
  <input type="button" value="<fmt:message key="css.label.OK"/>" class="portlet-dlg-icon-label"/>
</div>

<table border="0" cellspacing="2" cellpadding="3" width="100%">
  <tr>
    <th class="portlet-section-header"><fmt:message key="css.label.Edit"/></th>
    <th class="portlet-section-header"><fmt:message key="css.label.Style"/></th>
    <th class="portlet-section-header"><fmt:message key="css.label.Description"/></th>
  </tr>
  <tr>
    <td class="portlet-section-body"><input type="checkbox"/></th>
    <td class="portlet-section-body"><fmt:message key="css.label.portlet-msg-status"/></th>
    <td class="portlet-section-body"><fmt:message key="css.label.StatusOfTheCurrentOperation"/></th>
  </tr>
  <tr>
    <td class="portlet-section-alternate"><input type="checkbox"/></th>
    <td class="portlet-section-alternate"><fmt:message key="css.label.portlet-msg-info"/></th>
    <td class="portlet-section-alternate"><fmt:message key="css.label.HelpMessagesGeneralAdditionalInformationEtc"/></th>
  </tr>
  <tr>
    <td class="portlet-section-body"><input type="checkbox"/></th>
    <td class="portlet-section-body"><fmt:message key="css.label.portlet-msg-error"/></th>
    <td class="portlet-section-body"><fmt:message key="css.label.ErrorMessages"/></th>
  </tr>
  <tr>
    <td class="portlet-section-alternate"><input type="checkbox"/></th>
    <td class="portlet-section-alternate"><fmt:message key="css.label.portlet-msg-alert"/></th>
    <td class="portlet-section-alternate"><fmt:message key="css.label.WarningMessages"/></th>
  </tr>
</table>
<div class="portlet-section-footer">
  <input type="button" value="<fmt:message key="css.label.Prev"/>" class="portlet-dlg-icon-label"/>
  <input type="button" value="<fmt:message key="css.label.Next"/>" class="portlet-dlg-icon-label"/>
</div>

<!-- Default Menu Style -->
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="portlet-menu-caption">
      <fmt:message key="css.label.ProjectDocumentation"/>
    </td>
  </tr>
  <tr>
    <td class="portlet-menu">
      <table border="0" cellspacing="2" cellpadding="0">
        <tr>
          <td>
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.AboutJetspeed2EnterprisePortal"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item-selected">
              <a href="#"><fmt:message key="css.label.ProjectInfo"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.MailingLists"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item-selected">
              <fmt:message key="css.label.ProjectTeam"/>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.Dependencies"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.SourceRepository"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.IssueTracking"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item">
              <a href="#"><fmt:message key="css.label.ProjectReports"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item">
              <a href="#"><fmt:message key="css.label.DevelopmentProcess"/></a>
            </font>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<br/>

<!-- Default Menu Style -->
<div class="arrowfolder">
<table border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td class="portlet-menu-caption">
      <fmt:message key="css.label.ProjectDocumentation"/>
    </td>
  </tr>
  <tr>
    <td class="portlet-menu">
      <table border="0" cellspacing="2" cellpadding="0">
        <tr>
          <td>
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.AboutJetspeed2EnterprisePortal"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item-selected">
              <a href="#"><fmt:message key="css.label.ProjectInfo"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.MailingLists"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item-selected">
              <fmt:message key="css.label.ProjectTeam"/>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.Dependencies"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.SourceRepository"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            &nbsp;
            <font class="portlet-menu-item">
              <a href="#"><fmt:message key="css.label.IssueTracking"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item">
              <a href="#"><fmt:message key="css.label.ProjectReports"/></a>
            </font>
          </td>
        </tr>
        <tr>
          <td>
            <font class="portlet-menu-cascade-item">
              <a href="#"><fmt:message key="css.label.DevelopmentProcess"/></a>
            </font>
          </td>
        </tr>
      </table>
    </td>
  </tr>
</table>
</div>


</div>

