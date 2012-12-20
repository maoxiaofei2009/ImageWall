<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

function error($message, $code) {
    $json = array('message' => $message, 'code' => $code);
    return array('error' => $json);
}