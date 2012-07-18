<?php


require_once(dirname(dirname(dirname(__FILE__))).'/config.php');
require_once('./locallib.php');
//echo "hello world";
//$a  = optional_param('a', 0, PARAM_INT);  // ipal instance ID
//$id = optional_param('id', 0, PARAM_INT); // course_module ID, or
//$i  = optional_param('i', 0, PARAM_INT);  // ipal instance ID - it should be named as the first character of the module
$username = optional_param('user','',PARAM_ALPHANUMEXT);
$passcode = optional_param('p', 0, PARAM_INT);
$qtypemessage='';
$set = true;

$i_time_create = fmod($passcode, 100);
$i = floor($passcode/100);

//Testing statement here
// echo "time create: ". $i_time_create. "<br>";
// echo "\nipal id: " .$i. "\n<br>";
if ($i) {
	try {
		$ipal  = $DB->get_record('ipal', array('id' => $i), '*', MUST_EXIST);
		$course     = $DB->get_record('course', array('id' => $ipal->course), '*', MUST_EXIST);
		$cm         = get_coursemodule_from_instance('ipal', $ipal->id, $course->id, false, MUST_EXIST);
	}
	catch(dml_exception $e) {
		//error('You must specify valid passcode and username to access the ipal instance');
		$qtypemessage='invalidpasscode';
		$set = false;
	}
	if (fmod($ipal->timecreated, 100) != $i_time_create) {
		//error('You must specify valid passcode to access the ipal instance');
		$qtypemessage='invalidpasscode';
		$set = false;
	}
}
else {
	//error('You must specify valid passcode to access the ipal instance');
	$qtypemessage='invalidpasscode';
	$set = false;
}

if ($set) {
	$context = get_context_instance(CONTEXT_COURSE, $course->id);
	$students = get_role_users(5, $context);

	foreach ($students as $s) {
		//echo "checking the list of student enrolled in the course<br>";
		//echo "with id: " .$s->username. "\n </br>";
		if (strcasecmp($username, $s->username) == 0) {
			$found_user = 1;
			$userid = $s->id;
		}
	}

	if ($found_user != 1) {
		//error('You must specify valid passcode and username to access the ipal instance');
		$qtypemessage = 'invalidusername';
		$set = false;
	}
}
echo "<html>\n<head>\n<title>IPAL: ". $ipal->name."</title>\n</head>\n";
echo "<body>\n";
//ipal_print_anonymous_message();
//ipal_display_student_interface();
if (!$set) {
	echo "<p id=\"questiontype\">".$qtypemessage."<p>";
}
else {
	ipal_tempview_display_question($userid, $passcode, $username);
}
echo "</body>\n</html>";


?>