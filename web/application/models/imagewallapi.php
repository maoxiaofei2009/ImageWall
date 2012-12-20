<?php

require_once 'curl.php';

/**
 * Wrapper class for direct access to ImageWall REST API.
 */
class ImageWallApi {

    const BASE_API_URL = 'http://api.team36.host25.com';

    private $curl;

    public function __construct() {
        $this->curl = new Curl();
        $this->curl->headers['Accept'] = 'application/json';
    }

    /**
     * Retrieve all tags within some timespan.
     *
     * @param $timespanInMinutes
     * @return mixed
     */
    public function getTags($timespanInMinutes) {
        $response = $this->curl->get(self::BASE_API_URL . '/tags', array('timespanInMinutes' => $timespanInMinutes));
        return json_decode($response);
    }

    /**
     * Get all images that have some specific tag.
     *
     * @param $tag
     * @return mixed
     */
    public function getImagesWithTag($tag) {
        $response = $this->curl->get(self::BASE_API_URL . '/images', array('tag' => $tag));
        return json_decode($response);
    }

    /**
     * Get single image information (description, tag, location...).
     *
     * @param $imageId
     * @return mixed
     */
    public function getImageById($imageId) {
        $response = $this->curl->get(self::BASE_API_URL . '/images/id/' . $imageId);
        return json_decode($response);
    }

}

?>