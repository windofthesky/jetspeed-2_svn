<?php
/*******************************************************************************
**	file:	hosts/index.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	Configuration Database Demo
*******************************************************************************/

require_once "conf/config.php";
require_once "common/forms.php";
require_once "common/functions.php";

$page = $_GET['pagedest'] ? $_GET['pagedest'] : $_POST['pagedest'];
$task = $_GET['task'] ? $_GET['task'] : $_POST['task'];

echo '
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title> GroundWork </title>
<meta name="Author" content="Scott Parris">
<meta name="Description" content="GroundWork Configuration Management">
<link rel="stylesheet" type="text/css" href="/PHP/css/style.css" />
</head>
<body>
<table width=70% cellspacing=0 cellpadding=5 border=1>
<tr>
<!-- <td><img src=images/groundwork.gif></td> -->
</tr>
<tr>
<td>
<table width=100% cellspacing=0 cellpadding=5 border=0>
<td class=head width=15% align=center>
&nbsp;
</td>
<td class=head align=left>
<a class=head href=hosts/index.php>Hosts</a>
</td>
<td class=head align=left>
<a class=head href=hosts/index.php?pagedest=services>Services</a>
</td>
<td class=head align=left>
<a class=head href=hosts/index.php?pagedest=interfaces>Interfaces</a>
</td>
<td class=head align=center>
&nbsp;
</td>
</tr>
</table>
<table width=100% cellspacing=0 cellpadding=5 border=1>
<tr>
<td width=15% valign=top>
<table cellspacing=0 cellpadding=5 border=0>';

switch($page) {
	case('nagios'):
		//external command
		leftDefault();
		formSuccess('Nagios configuration successfully updated.');
		break;
	case('discovery'):
		leftDefault();
		if (/*!copy('c:/temp/x.txt', 'c:/temp/y.txt')*/ false) {
			formError("Failed to start discovery service...");
		} else {
			formSuccess('Discovery service started.');
		}
		break;
	case('services'):
		echo '
<tr>
<td class=nav align=center>
<h2>Services:</h2>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=services>
<input type=hidden name=task value=add>
<input type=submit name=submit value="Add Service">
</form>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=services>
<input type=hidden name=task value=show>
<input type=submit name=submit value="List Services">
</form>
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
</table>
</td>
<td valign=top>';
		break;
	case('interfaces'):
		echo '
<tr>
<td class=nav align=center>
	<h2>Interfaces:</h2>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=interfaces>
<input type=hidden name=task value=add>
<input type=submit name=nedi value="Add Interface">
</form>
</td>
</tr>
<tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=interfaces>
<input type=hidden name=task value=show>
<input type=submit name=nedi value="List Interfaces">
</form>
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
</table>
</td>
<td valign=top>';
		break;
	default:
		leftDefault();
		break;
}

if ($page != 'nagios' && $page != 'discovery') {
	require main_content($page);
}

echo '
</table>
<table width=100% cellspacing=0 cellpadding=5 border=0>
<tr>
<td class=head width=15% align=center>
&nbsp;
</td>
</tr>
</table>
</td>
</tr>
</table>
</body>
</html>';

function leftDefault() {
		echo '
<tr>
<td class=nav align=center>
<h2>Hosts:</h2>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=hosts>
<input type=hidden name=task value=add>
<input type=submit name=nedi value="Add Host">
</form>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=hosts>
<input type=hidden name=task value=show>
<input type=submit name=nedi value="List Hosts">
</form>
</td>
</tr>
<tr>
<td class=nav align=center>
<hr>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=discovery>
<input type=submit name=nedi value="Discover Device(s)">
</form>
</td>
</tr>
<tr>
<td class=nav align=middle>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=nagios>
<input type=submit name=nagios value="Gen Nagios Conf">
</form>
</td>
</tr>
<tr>
<td class=nav align=center>
&nbsp;
</td>
</tr>
</table>
</td>
<td valign=top>';
}


function main_content($page) {
	switch($page) {
		case('services'):
			$main = "code/services.php";
			break;
		case('interfaces'):
			$main = "code/interfaces.php";
			break;
		case('nagios'):
			$main= ' ';
			break;
		case('discovery'):
			$main = ' ';
			break;
		default:
			$main = "code/hosts.php";
			break;
	}
	return $main;
}

?>