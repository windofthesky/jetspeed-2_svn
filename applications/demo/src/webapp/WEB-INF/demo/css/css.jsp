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
<portlet:defineObjects/>
<br/>
<div class="portlet-section-header">CSS Style Definitions</div><br/>
<div class="portlet-section-subheader">PLT.C.1 Links: </div><br/>
<div class="portlet-section-text">
<a href="">Link Test</a><br/>
</div>
<br/>
<div class="portlet-section-subheader">PLT.C.2 Fonts: </div><br/>
<div class="portlet-section-text">
portlet-font style: <font class="portlet-font">Normal Text</font><br/>
portlet-font-dim style: <font class="portlet-font-dim">Dim Text</font><br/>
</div>
<br/>
<div class="portlet-section-subheader">PLT.C.3 Messages: </div><br/>
<div class="portlet-section-text">
portlet-msg-status style: <div class="portlet-msg-status">Progress: 80%</div><br/>
portlet-msg-info style: <div class="portlet-msg-info">Info about</div><br/>
portlet-msg-error style: <div class="portlet-msg-error">Portal not available</div><br/>
portlet-msg-alert style: <div class="portlet-msg-alert">Time out occurred, try again later</div><br/>
portlet-msg-success style: <div class="portlet-msg-success">Operation completed successfully</div><br/>
</div>
<br/>
<div class="portlet-section-subheader">PLT.C.4 Sections: </div><br/>
<div class="portlet-section-text">
portlet-section-header style: <div class="portlet-section-header">Table or section header</div><br/>
portlet-section-body style: <div class="portlet-section-body">Normal text in a table cell</div><br/>
portlet-section-alternate style: <div class="portlet-section-alternate">Text in every other row in the cell</div><br/>
portlet-section-selected style: <div class="portlet-section-selected">Text in a selected cell range</div><br/>
portlet-section-subheader style: <div class="portlet-section-subheader">Text of a subheading</div><br/>
portlet-section-footer style: <div class="portlet-section-footer">Table or section footnote</div><br/>
portlet-section-text style: <div class="portlet-section-text">Text that belongs to the table but does not fall in one of the other categories (e.g. explanatory or help text that is associated with the section)</div><br/>
</div>
<br/>
<div class="portlet-section-subheader">PLT.C.5 Forms: </div><br/>
<div class="portlet-section-text">
portlet-form-label style: <font class="portlet-form-label">This is form.</font><br/>
portlet-form-label-field style: <input type="text" value="Value" class="portlet-form-label"/><br/>
portlet-form-button style: <input type="button" value="Button" class="portlet-form-button"/><br/>
portlet-icon-label style: <font class="portlet-icon-label">Save</font><br/>
portlet-dlg-icon-label style: <font class="portlet-dlg-icon-label">OK</font><br/>
portlet-form-field-label style: <font class="portlet-form-field-label">Checkbox 1</font><br/>
portlet-form-field style: <font class="portlet-form-field">Label 1:</font><br/>
</div>
<br/>
<div class="portlet-section-subheader">PLT.C.6 Menus: </div><br/>
<div class="portlet-section-text">
portlet-menu style: <div class="portlet-menu">General menu settings</div><br/>
portlet-menu-item style: <div class="portlet-menu-item">Normal, unselected menu item</div><br/>
portlet-menu-item-selected style: <div class="portlet-menu-item-selected">Selected menu item</div><br/>
portlet-menu-item-hover style: <div class="portlet-menu-item-hover">Normal, unselected menu item when the mouse hovers over it</div><br/>
portlet-menu-item-hover-selected style: <div class="portlet-menu-item-hover-selected">Selected menu item when the mouse hovers overit</div><br/>
portlet-menu-cascade-item style: <div class="portlet-menu-cascade-item">Normal, unselected menu item that has sub-menu</div><br/>
portlet-menu-cascade-item-selected style: <div class="portlet-menu-cascade-item-selected">Selected sub-menu item that has sub-menu</div><br/>
portlet-menu-description style: <div class="portlet-menu-description">Descriptive text for the menu</div><br/>
portlet-menu-caption style: <div class="portlet-menu-caption">Menu Caption</div><br/>
</div>
<div class="portlet-section-subheader">Examples: </div><br/>
<div class="portlet-section-text">
<div class="portlet-section-header">Example Form</div>
<table border="0" cellspacing="2" cellpadding="3">
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label">Jetspeed ID</font></th>
    <td>
      <input type="text" name="#" value="admin" size="15" maxlength="15"  class="portlet-form-label-field"/>
      <p class="portlet-form-field">ID consists of a-z, 0-9.</p>
    </td>
  </tr>
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label">Password</font></th>
    <td>
      <input type="password" name="#" value="abcdefg" size="10" maxlength="10" class="portlet-form-label-field"/> 
    </td>
  </tr>
  <tr>
    <th class="portlet-section-alternate"><font class="portlet-form-field-label">Language</font></th>
    <td>
      <div>
        <input type="radio" name="#" checked="checked" /> <font class="portlet-form-field-label">English</font>
      </div>
      <div>
        <input type="radio" name="#" value="J" /> <font class="portlet-form-field-label">Japanese</font>
      </div>
      <div>
        <input type="radio" name="#" value="F" /> <font class="portlet-form-field-label">French </font>
      </div>
      <p class="portlet-form-field">The verbosity of logging produced by system operation and events</p>
    </td>
  </tr>
</table>
<div class="portlet-section-footer">
  <input type="submit" value="Submit" class="portlet-dlg-icon-label"/>
  <input type="button" value="OK" class="portlet-dlg-icon-label"/>
</div>

<table border="0" cellspacing="2" cellpadding="3">
  <tr>
    <th class="portlet-section-header">Edit</th>
    <th class="portlet-section-header">Style</th>
    <th class="portlet-section-header">Description</th>
  </tr>
  <tr>
    <td class="portlet-section-body"><input type="checkbox"/></th>
    <td class="portlet-section-body">portlet-msg-status</th>
    <td class="portlet-section-body">Status of the current operation.</th>
  </tr>
  <tr>
    <td class="portlet-section-alternate"><input type="checkbox"/></th>
    <td class="portlet-section-alternate">portlet-msg-info</th>
    <td class="portlet-section-alternate">Help messages, general additional information, etc.</th>
  </tr>
  <tr>
    <td class="portlet-section-body"><input type="checkbox"/></th>
    <td class="portlet-section-body">portlet-msg-error</th>
    <td class="portlet-section-body">Error messages.</th>
  </tr>
  <tr>
    <td class="portlet-section-alternate"><input type="checkbox"/></th>
    <td class="portlet-section-alternate">portlet-msg-alert</th>
    <td class="portlet-section-alternate">Warning messages.</th>
  </tr>
</table>
<div class="portlet-section-footer">
  <input type="button" value="Prev" class="portlet-dlg-icon-label"/>
  <input type="button" value="Next" class="portlet-dlg-icon-label"/>
</div>

<div class="portlet-menu-caption">
Project Documentation
</div>
<div class="portlet-menu">
<div class="portlet-menu-item">
<a href="#">About Jetspeed 2 Enterprise Portal</a>
</div>
<div class="portlet-menu-item-selected">
<a href="#">Project Info</a>
</div>
<div class="portlet-menu-cascade-item">
&nbsp;<a href="#">Mailing Lists</a>
</div>
<div class="portlet-menu-item-hover-selected">
&nbsp;Project Team
</div>
<div class="portlet-menu-cascade-item">
&nbsp;<a href="#">Dependencies</a>
</div>
<div class="portlet-menu-cascade-item">
&nbsp;<a href="#">Source Repository</a>
</div>
<div class="portlet-menu-cascade-item">
&nbsp;<a href="#">Issue Tracking</a>
</div>
<div class="portlet-menu-item">
<a href="#">Project Reports</a>
</div>
<div class="portlet-menu-item">
<a href="#">Development Process</a>
</div>
</div>

</div>

