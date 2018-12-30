<?php

if (endsWith($_SERVER['SCRIPT_NAME'], '-dev.php')) {
    $dev=true;
    error_reporting(0);
    $conf = parse_ini_file('../smtng-dev.conf');
}
else {
    $dev=false;
    $conf = parse_ini_file('../smtng-prd.conf');
}

function startsWith($haystack, $needle)
{
     $length = strlen($needle);
     return (substr($haystack, 0, $length) === $needle);
}
function endsWith($haystack, $needle)
{
    $length = strlen($needle);
    if ($length == 0) {
        return true;
    }

    return (substr($haystack, -$length) === $needle);
}

function get(&$var, $default=null) {
    return isset($var) ? $var : $default;
}

// Allow from any origin
if (isset($_SERVER['HTTP_ORIGIN'])) {
    $s = $_SERVER['HTTP_ORIGIN'];
    if (endsWith($s, ".jpkware.com")
    || startsWith($s, 'http://127.0.0.1:') ) {
        header("Access-Control-Allow-Origin: $s");
        header('Access-Control-Allow-Credentials: false');
        header('Access-Control-Max-Age: 86400');    // cache for 1 day
    }
    else {
        $err = date(DATE_ATOM)." CORS denied for:".$s.PHP_EOL;
        file_put_contents('../smtng.err', $err , FILE_APPEND | LOCK_EX);
        echo($err);
        exit(0);
    }
}

// Access-Control headers are received during OPTIONS requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {

    if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_METHOD']))
        header("Access-Control-Allow-Methods: POST");

    if (isset($_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']))
        header("Access-Control-Allow-Headers: {$_SERVER['HTTP_ACCESS_CONTROL_REQUEST_HEADERS']}");

    exit(0);
}

$conn = new mysqli("localhost",$conf['dbuser'],$conf['dbpass'],$conf['dbname']);
// Check connection
if ($conn->connect_error) {
    die("Connection failed" . ($dev ? $conn->connect_error : ""));
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = file_get_contents("php://input");
    if (strlen($data) > 1024) {
        http_response_code(413);
        exit(0);
    }
    $data  = preg_replace('#\R+#', "\t", $data);
    $id = $_SERVER['REMOTE_ADDR'];
    if (isset($_SERVER['PATH_INFO']) && $_SERVER['PATH_INFO']!=='/') {
        $info = $_SERVER['PATH_INFO'];
        $retid = substr($info, 1, strlen($info)-1);
    }
    else {
        $retid = substr(base64_encode(md5($id.$data, true)),0,22);
    }
    $line = $id."\t".$retid."\t".$data;
    file_put_contents('../smtng.log', $line.PHP_EOL , FILE_APPEND | LOCK_EX);
    print($retid);

    $bits = explode("\t", $data);
    // HIGHSCORES
    $stmt = $conn->prepare("INSERT INTO highscores (dt, user_name,field_id,score,bonusoids,device) VALUES (utc_timestamp(), ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE score=GREATEST(score, VALUES(score)), bonusoids=GREATEST(bonusoids, VALUES(bonusoids)), device=VALUES(device), dt=VALUES(dt)");
    $stmt->bind_param("sddds", $bits[9], $bits[2], $bits[4], $bits[8], $retid);
    if (!$stmt->execute()) {
      die("Error1 " . ($dev ? $stmt->error : "*"));
    }
    // LOGGING
    $stmt = $conn->prepare("INSERT INTO log (dt,ip,device,st,pt, field_id,stars,score,lives, time_bonus,bonusoids_collected,bonusoids_total,user_name, field_time) VALUES (utc_timestamp(),?,?,?,?,?,?,?,?,?,?,?,?,?)");
    $st=gmdate("Y-m-d H:i:s", $bits[0]/1000);
    $pt=gmdate("Y-m-d H:i:s", $bits[1]/1000);
    $stmt->bind_param("ssssdddddddsd", $id,$retid,$st,$pt, $bits[2],$bits[3],$bits[4],$bits[5], $bits[6],$bits[7],$bits[8],$bits[9],$bits[10]);
    if (!$stmt->execute()) {
      die("Error2 " . ($dev ? $stmt->error : "*"));
    }
}
else {
    $field = get($_GET['field'], 0);
    $limit = get($_GET['limit'], 10);
    $user = get($_GET['user'], '');
    if ($field>0) {
        // SINGLE FIELD TOP SCORES
        $stmt = $conn->prepare("select field_id, score, user_name, bonusoids,device,dt from highscores where field_id=? order by score desc limit ?");
        if (!$stmt) {
          die("Error3a " . ($dev ? $conn->error : "*"));
        }
        $stmt->bind_param("dd", $field, $limit);
    }
    else if ($user!='') {
        // SINGLE USER SCORES PER FIELD
        $stmt = $conn->prepare("select field_id, score, user_name, bonusoids,device,dt from highscores where user_name=? order by field_id limit ?");
        if (!$stmt) {
          die("Error3b " . ($dev ? $conn->error : "*"));
        }
        $stmt->bind_param("sd", $user, $limit);
    }
    else {
        // OVERALL TOP SCORES
        $stmt = $conn->prepare("SELECT max(field_id),max(score),user_name,max(bonusoids) FROM `highscores` group by user_name order by max(score) desc limit ?");
        if (!$stmt) {
          die("Error4 " . ($dev ? $conn->error : "*"));
        }
        $stmt->bind_param("d", $limit);
    }
    if (!$stmt->execute()) {
      die("Error5 " . ($dev ? $stmt->error : "*"));
    }
    $result = $stmt->get_result();
    $fp = fopen('php://output', 'w');
    if ($fp && $result) {
        header('Content-Type: text/csv');
        header('Pragma: no-cache');
        header('Expires: 0');
        while ($row = $result->fetch_array(MYSQLI_NUM))
        {
            fputcsv($fp, array_values($row), "\t");
        }
       die;
    }
}
$conn->close();

?>
