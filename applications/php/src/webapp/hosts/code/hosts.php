<?php
/*******************************************************************************
**	file:	/hosts.php
********************************************************************************
**	author:	Scott Parris
**	date:	2004/6/2
********************************************************************************
**	Configuration Database Demo
*******************************************************************************/

$task = $_GET['task'] ? $_GET['task'] : $_POST['task'];
$id = $_GET['id'] ? $_GET['id'] : $_POST['id'];
$name = $_GET['name'] ? $_GET['name'] : $_POST['name'];
$address = $_GET['address'] ? $_GET['address'] : $_POST['address'];
$os = $_GET['os'] ? $_GET['os'] : $_POST['os'];
$serial = $_GET['serial'] ? $_GET['serial'] : $_POST['serial'];
$type = $_GET['type'] ? $_GET['type'] : $_POST['type'];
$submit = $_GET['submit'] ? $_GET['submit'] : $_POST['submit'];
$order = $_GET['order'] ? $_GET['order'] : $_POST['order'];
$sort = $_GET['sort'] ? $_GET['sort'] : $_POST['sort'];

switch($task) {
	case('add'):
		add();
		break;
	case('delete'):
		delete($id);
		break;
	case('edit'):
		edit();
		break;
	case('detail'):
		detail($id);
		break;
	case('services'):
		services($id);
		break;
	case('interfaces'):
		interfaces($id);
		break;
	default:
		show();
		break;
}


function show() {
	global $order, $sort;
	if (!isset($sort)) {
		$sort = 'asc';
		$newsort = 'desc';
		$order = 'name';
	} else if ($sort == 'asc') {
		$newsort = 'desc';
	} else {
		$newsort = 'asc';
	}
	echo '
<table width=100% bgcolor=#dddddd cellpadding=5 celspacing=0 border=0>
<tr>
<td class=head><a class=head href=hosts/index.php?pagedest=hosts&order=id&sort='.$newsort.'>Id</td>
<td class=head><a class=head href=hosts/index.php?pagedest=hosts&order=name&sort='.$newsort.'>Name</td>
<td class=head><a class=head href=hosts/index.php?pagedest=hosts&order=address&sort='.$newsort.'>Address</td>
<td class=head><a class=head href=hosts/index.php?pagedest=hosts&order=os&sort='.$newsort.'>OS</td>
<td class=head>Serial</td>
<td class=head><a class=head href=hosts/index.php?pagedest=hosts&order=type&sort='.$newsort.'>Type</td>
<td class=head colspan=3>Manage</td>
</tr>';
	$class = 'row1';
	$sql = "select * from hosts order by $order $sort";
	$result = execsql($sql);
	while ($row = mysql_fetch_row($result)) {
		echo '
		<tr>
		<td class='.$class.'>'.$row[0].'</td>
		<td class='.$class.'><a class=std href=hosts/index.php?pagedest=hosts&task=detail&id='.$row[0].'>'.$row[1].'</a></td>
		<td class='.$class.'>'.$row[2].'</td>
		<td class='.$class.'>'.$row[3].'</td>
		<td class='.$class.'>'.$row[4].'</td>
		<td class='.$class.'>'.$row[5].'</td>
		<td class='.$class.'><a class=std href=hosts/index.php?pagedest=hosts&task=detail&id='.$row[0].'>Services / Interfaces</a></td>
		<td class='.$class.'><a class=std href=hosts/index.php?pagedest=hosts&task=edit&id='.$row[0].'>Edit</a></td>
		<td class='.$class.'><a class=std href=hosts/index.php?pagedest=hosts&task=delete&id='.$row[0].'>Delete</a></td>
		</tr>';
		if ($class == 'row1')
			$class = 'row2';
		else
			$class = 'row1';
	}
	echo '
</table>';
}

function add() {
	global $submit, $name, $address, $os, $serial, $type;
	if ($submit == 'Cancel') {
		show();
	} else if ($name != '' && $address != '') {
		$sql = "insert into hosts values(NULL, '$name', '$address', '$os', '$serial', '$type')";
		if (execsql($sql)) {
			formSuccess('Host '.$name.' added.');
		}
	} else {
		$action = "hosts/index.php";
		$title = 'Add Host';
		$rows = array();
		$row = array('hidden', 'pagedest', '/hosts');
		array_push($rows, $row);
		$row = array('hidden', 'task', 'add');
		array_push($rows, $row);
		$row = array('text', "* Name", 'name', $name);
		array_push($rows, $row);
		$row = array('text', "* IP Address", 'address', $address);
		array_push($rows, $row);
		$row = array('text', "Operating Sys", 'os', $os);
		array_push($rows, $row);
		$row = array('text', "Serial No.", 'serial', $serial);
		array_push($rows, $row);
		$row = array('text', "Type", 'type', $type);
		array_push($rows, $row);
		$buttons = array();
		$button = array('submit', 'Add Host', 'submit');
		array_push($buttons, $button);
		$button = array('submit', 'Cancel', 'submit');
		array_push($buttons, $button);
		
		formInput($action, $title, $rows, $buttons);
	}
}

function edit() {
	global $submit, $id, $name, $address, $os, $serial, $type;
	if ($submit == 'Cancel') {
		show();
	} else if ($name != '' && $address != '') {
		$sql = "update hosts set name = '$name', address = '$address', os = '$os', serial = '$serial', type = '$type' where id = '$id'";
		if (execsql($sql)) {
			formSuccess('Host '.$name.' updated.');
		}
	} else {
		$info = getHostInfo($id);
		$action = "hosts/index.php";
		$title = 'Modify Host';
		$rows = array();
		$row = array('hidden', 'id', $id);
		array_push($rows, $row);
		$row = array('hidden', 'pagedest', '/hosts');
		array_push($rows, $row);
		$row = array('hidden', 'task', 'edit');
		array_push($rows, $row);
		$row = array('text', "* Name", 'name', $info[0]);
		array_push($rows, $row);
		$row = array('text', "* IP Address", 'address', $info[1]);
		array_push($rows, $row);
		$row = array('text', "Operating Sys", 'os', $info[2]);
		array_push($rows, $row);
		$row = array('text', "Serial No.", 'serial', $info[3]);
		array_push($rows, $row);
		$row = array('text', "Type", 'type', $info[4]);
		array_push($rows, $row);
		$buttons = array();
		$button = array('submit', 'Update Host', 'submit');
		array_push($buttons, $button);
		$button = array('submit', 'Cancel', 'submit');
		array_push($buttons, $button);
	
		formInput($action, $title, $rows, $buttons);
	}
}

function delete($id) {
	global $page, $submit;
	$info = getHostInfo($id);
	$error = 1;
	if ($submit == 'Cancel') {
		show();
	} else if ($submit == 'Confirm Delete') {		
		$sql = "delete from host_interface where host_id = '$id'";
		if (execsql($sql)) {
			$error = 0;
			$sql = "delete from host_service where host_id = '$id'";
			if (execsql($sql)) {
				$error = 0;
				$sql = "delete from hosts where id = '$id'";
				if (execsql($sql)) {
					$error = 0;
				}
			}
		}
		if ($error == 0) {
			formSuccess("Host $info[0] removed.");
		} else {
			formError("Failed to remove host $info[0]");
		}
	} else {
		$message = "You are about to delete host $info[0].<br>Are you sure?";
		formDelete($message, $page, $id);
	}
}

function detail($id) {
	$info = getHostInfo($id);
	echo '
<table width=100% bgcolor=#dddddd cellpadding=5 cellspacing=0 border=0>
<tr>
<td class=head align=center>Host '.$info[0].' Detail</td>
</tr>
</table>
<table width=100% cellpadding=0 cellspacing=0 border=1>
<tr>
<td>
<table width=100% bgcolor=#ffffff cellpadding=5 celspacing=0 border=0>
<tr>
<td width=20% align=right><b>IP address:</b></td>
<td align=left><b>'.$info[1].'</b></td>
<td align=right rowspan=4>
	<form action=hosts/index.php method=get>
	<input type=hidden name=pagedest value=hosts>
	<input type=hidden name=task value=edit>
	<input type=hidden name=id value='.$id.'>
	<input type=submit name=submit value="Edit Host">
	</form>
</td>
<td align=left rowspan=4>
	<form action=hosts/index.php method=get>
	<input type=hidden name=pagedest value=hosts>
	<input type=hidden name=task value=detail>
	<input type=hidden name=id value='.$id.'>
	<input type=submit name=submit value="Refresh">
	</form>
</td>
</tr>
<tr>
<td width=20% align=right><b>Operating System:</b></td>
<td align=left><b>'.$info[2].'</b></td>
</tr>
<tr>
<td width=20% align=right><b>Serial Number:</b></td>
<td align=left><b>'.$info[3].'</b></td>
</tr>
<tr>
<td align=right><b>Type:</b></td>
<td align=left><b>'.$info[4].'</b></td>
</tr>
</table>
</td>
</tr>
</table>
<table width=100% bgcolor=#ffffcc cellpadding=5 cellspacing=0 border=0>
<tr>
<td class=head align=center>Services</td>
</tr>
</table>
<table width=100% cellpadding=5 cellspacing=0 border=1>
<tr>
<td>
<table width=100% bgcolor=#dddddd cellpadding=5 cellspacing=1 border=0>';
	$services = array();
	$services = gethostservices($id);
	$class = 'row1';
	if (! is_null($services)) {
		$colspan = 'colspan=3';
		while ($row = array_pop($services)) {
			echo '
			<tr>
			<td class='.$class.' width=10%>'.$row[0].'</td>
			<td class='.$class.'>'.$row['name'].'</td>
			</tr>';
			if ($class == 'row1')
				$class = 'row2';
			else
				$class = 'row1';
		}
	} else {
		echo '
		<tr>
		<td class=row1>No services defined for this host</td>
		</tr>';
	}
	echo '
</td>
</tr>
</table>
<tr>
<td '.$colspan.' align=left>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=/hosts>
<input type=hidden name=task value=services>
<input type=hidden name=id value='.$id.'>
<input type=submit name=submit value="Modify Service List">
</form>
</tr>
</td>
</table>
<table width=100% bgcolor=#ffffcc cellpadding=5 cellspacing=0 border=0>
<tr>
<td class=head align=center>Interfaces</td>
</tr>
</table>
<table width=100% cellpadding=5 cellspacing=0 border=1>
<tr>
<td>
<table width=100% bgcolor=#dddddd cellpadding=5 cellspacing=1 border=0>';
	$interfaces = array();
	$interfaces = getHostInterfaces($id);
	$class = 'row1';
	if (! is_null($interfaces)) {
		$colspan = 'colspan=3';
		while ($row = array_pop($interfaces)) {
			echo '
			<tr>
			<td class='.$class.' width=10%>'.$row[0].'</td>
			<td class='.$class.'>'.$row['name'].'</td>
			</tr>';
			if ($class == 'row1')
				$class = 'row2';
			else
				$class = 'row1';
		}
	} else {
		echo '
		<tr>
		<td class=row1>No interfaces defined for this host</td>
		</tr>';
	}
	echo '
</td>
</tr>
</table>
<tr>
<td '.$colspan.' align=left>
<form action=hosts/index.php method=get>
<input type=hidden name=pagedest value=/hosts>
<input type=hidden name=task value=interfaces>
<input type=hidden name=id value='.$id.'>
<input type=submit name=submit value="Modify Interface List">
</form>
</tr>
</td>
</table>';
}

function services($id) {
	global $submit;
	$info = getHostInfo($id);
	$title = "Add/Remove Services for Host $info[0]";
	if ($submit == "add_members") {
		if (array_key_exists('nonmembers', $_GET)) {
			$alist = $_GET['nonmembers'];
			while(!is_null($row = array_pop($alist))) {
				$service_id = $row;
				$result = mysql_query("select count(*) from host_service where host_id = $id and service_id = '$service_id'");
				$count = mysql_fetch_row($result);
				if ($count[0] == 0) {
					$sql = "insert into host_service values(NULL, $id, $service_id)";
					execsql($sql);
				}
			}
		}
		viewServices($id, $title);
	} else if ($submit == "remove_members") {
		if (array_key_exists('members', $_GET)) {
			$dlist = $_GET['members'];
			while(!is_null($row = array_pop($dlist))) {
				$service_id = $row;
				$sql = "delete from host_service where host_id = '$id' and service_id = '$service_id'";
				execsql($sql);
			}
		}
		viewServices($id, $title);
	} else {
		viewServices($id, $title);
	}
}

function viewServices($id, $title) {
	global $page, $task;
	$sql = "select services.id, services.name from services left join host_service on services.id=host_service.service_id where host_service.host_id = $id order by services.name";
	$memlist = array();
	$row = array();
	$nonmembers = array();
	$result = execsql($sql);
	while($row = mysql_fetch_array($result)) {
		$service_id = $row['id'];
		$name = $row['name'];
		$memlist[] = $service_id;
		$members[] = array($service_id, $name);
	}
	
	$sql = "select id, name from services order by 'name' desc";
	$result = execsql($sql);
	while($row = mysql_fetch_array($result)) {
		$service_id = $row['id'];
		$name = $row['name'];
		if (!in_array($service_id, $memlist))
			$nonmembers[] = array($service_id, $name);
	}
	formMembers($id, $title, $page, $task, $members, $nonmembers, 'Available', 'Assigned');
}

function interfaces($id) {
	global $submit;
	$info = getHostInfo($id);
	$title = "Add/Remove Interfaces for Host $info[0]";
	if ($submit == "add_members") {
		if (array_key_exists('nonmembers', $_GET)) {
			$alist = $_GET['nonmembers'];
			while(!is_null($row = array_pop($alist))) {
				$interface_id = $row;
				$result = mysql_query("select count(*) from host_interface where host_id = $id and interface_id = '$interface_id'");
				$count = mysql_fetch_row($result);
				if ($count[0] == 0) {
					$sql = "insert into host_interface values(NULL, $id, $interface_id)";
					execsql($sql);
				}
			}
		}
		viewInterfaces($id, $title);
	} else if ($submit == "remove_members") {
		if (array_key_exists('members', $_GET)) {
			$dlist = $_GET['members'];
			while(!is_null($row = array_pop($dlist))) {
				$interface_id = $row;
				$sql = "delete from host_interface where host_id = '$id' and interface_id = '$interface_id'";
				execsql($sql);
			}
		}
		viewInterfaces($id, $title);
	} else {
		viewInterfaces($id, $title);
	}
}

function viewInterfaces($id, $title) {
	global $page, $task;
	$sql = "select interfaces.id, interfaces.name from interfaces left join host_interface on interfaces.id=host_interface.interface_id where host_interface.host_id = $id order by interfaces.name";
	$memlist = array();
	$row = array();
	$nonmembers = array();
	$result = execsql($sql);
	while($row = mysql_fetch_array($result)) {
		$interface_id = $row['id'];
		$name = $row['name'];
		$memlist[] = $interface_id;
		$members[] = array($interface_id, $name);
	}
	
	$sql = "select id, name from interfaces order by 'name' desc";
	$result = execsql($sql);
	while($row = mysql_fetch_array($result)) {
		$interface_id = $row['id'];
		$name = $row['name'];
		if (!in_array($interface_id, $memlist))
			$nonmembers[] = array($interface_id, $name);
	}
	formMembers($id, $title, $page, $task, $members, $nonmembers, 'Available', 'Assigned');
}

?>