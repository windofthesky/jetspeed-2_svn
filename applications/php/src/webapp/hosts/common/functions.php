<?php
/*******************************************************************************
**	file:	functions.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	Configuration Database
*******************************************************************************/

/***********************************************************************************************************
**	function execsql():
************************************************************************************************************/
function execsql($query) {
	global $mysql_user, $mysql_pwd, $mysql_db, $mysql_host, $queries, $debug;
	$connection = mysql_connect($mysql_host, $mysql_user, $mysql_pwd);
	mysql_select_db($mysql_db);
	if($debug == 1) {
		echo $query . "<br>";
	}
	if(!$result = mysql_query($query, $connection)) {
		echo mysql_errno() . " " . mysql_error();
		exit;
	}
	return $result;
}

/***********************************************************************************************************
**	function getHostInfo():
************************************************************************************************************/
function getHostInfo($id) {
	$sql = "select name, address, os, serial, type from hosts where id = '$id'";
	$result = execsql($sql);
	$row = mysql_fetch_array($result);
	return $row;
}

/***********************************************************************************************************
**	function getServiceInfo():
************************************************************************************************************/
function getServiceInfo($id) {
	$sql = "select name from services where id = '$id'";
	$result = execsql($sql);
	$row = mysql_fetch_array($result);
	return $row;
}

/***********************************************************************************************************
**	function getInterfaceInfo():
************************************************************************************************************/
function getInterfaceInfo($id) {
	$sql = "select name from interfaces where id = '$id'";
	$result = execsql($sql);
	$row = mysql_fetch_array($result);
	return $row;
}

/***********************************************************************************************************
**	function getHostServices():
************************************************************************************************************/
function getHostServices($id) {
	$sql = "select services.id, services.name from services left join host_service on host_service.service_id = services.id where host_service.host_id = '$id'";
	$result = execsql($sql);
	while ($row = mysql_fetch_array($result)) 
		$services[] = $row;
	return $services;
}

/***********************************************************************************************************
**	function getHostInterfaces():
************************************************************************************************************/
function getHostInterfaces($id) {
	$sql = "select interfaces.id, interfaces.name from interfaces left join host_interface on host_interface.interface_id = interfaces.id where host_interface.host_id = '$id'";
	$result = execsql($sql);
	while ($row = mysql_fetch_array($result)) 
		$interfaces[] = $row;
	return $interfaces;
}

/***********************************************************************************************************
**	function debug():
************************************************************************************************************/
function debug($debug) {
	echo '
<table cellSpacing=0 cellPadding=0 width=100% align=center border=0>
<tr>
<td>
<table cellSpacing=1 cellPadding=5 width=100% border=0>
<tr>
<td class=info align=middle>
<b>'.$debug.'</b>
</td>
</tr>
</table>
</td>
</tr>
</table>';
}

?>