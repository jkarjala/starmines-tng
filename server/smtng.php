<?php
error_reporting(0);
function cors() {

    // Allow from any origin
    if (isset($_SERVER['HTTP_ORIGIN'])) {
        $s = $_SERVER['HTTP_ORIGIN'];
        if ($s=='https://jpkware.com' || $s=='http://jpkware.com'
        || substr($s, 0, strlen('http://127.0.0.1:')) === 'http://127.0.0.1:') {
            header("Access-Control-Allow-Origin: {$_SERVER['HTTP_ORIGIN']}");
            header('Access-Control-Allow-Credentials: false');
            header('Access-Control-Max-Age: 86400');    // cache for 1 day
        }
        else {
            $err = "CORS from not allowed from:".$s.PHP_EOL;
            file_put_contents('../smtng.err', $err , FILE_APPEND | LOCK_EX);
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
}
cors();
$data = file_get_contents("php://input");
if (strlen($data) > 1024) {
    http_response_code(413);
    exit(0);
}
$data  = preg_replace('#\R+#', "\t", $data);
$id = $_SERVER['REMOTE_ADDR']."\t".$_SERVER['REMOTE_PORT']."\t".time()."\t";
if (isset($_SERVER['PATH_INFO']) && $_SERVER['PATH_INFO']!=='/') {
    $info = $_SERVER['PATH_INFO'];
    $retid = substr($info, 1, strlen($info)-1);
}
else {
    $retid = substr(base64_encode(md5($id.$data, true)),0,22);
}
$line = $id.$retid."\t".$data;
file_put_contents('../smtng.log', $line.PHP_EOL , FILE_APPEND | LOCK_EX);
print($retid)
?>
