<?php defined('BASEPATH') OR exit('No direct script access allowed');

require APPPATH.'/libraries/REST_Controller.php';

require APPPATH.'/models/tag.php';
require APPPATH.'/models/tag_model.php';
require APPPATH.'/models/location.php';
require APPPATH.'/models/location_model.php';

/**
 * Retrieve tags information.
 */
class Tags extends REST_Controller {

    const LOCATION_RADIUS_METERS = 200;
    const TIMESPAN_DEFAULT_DAYS = 7;

    /**
     * Retrieve all the tags.
     */
    function index_get() {
        header('Access-Control-Allow-Origin: *');

        $lat = $this->get('lat');
        $lon = $this->get('lon');
        $radiusInMeters = $this->get('radius');
        $timespanInMinutes = $this->get('timespanInMinutes');

        $tagModel = new TagModel($this->db);

        if($lat != null && $lon != null) {
            if($radiusInMeters == null) {
                $radiusInMeters = self::LOCATION_RADIUS_METERS;
            }

            $tags = $tagModel->getTagsAroundLocation($lat, $lon, $radiusInMeters);
        } else {
            if($timespanInMinutes == null) {
                $timespanInMinutes = self::TIMESPAN_DEFAULT_DAYS*24*60;
            }

            $fromDate = date("Y-m-d H:i:s", strtotime(date("Y-m-d H:i:s")) - $timespanInMinutes*60);
            $tags = $tagModel->getTags($fromDate);
        }

        $tagsJson = array();
        foreach($tags as $tag) {
            $tagsJson[] = $tag->serialize();
        }

        if(empty($tagsJson)) {
            $this->response(error('No tags found.', 404), 404);
        } else {
            $this->response($tagsJson);
        }
    }

}