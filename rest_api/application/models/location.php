<?php

class Location {

    private $id;
    private $lat;
    private $lon;

    public function setLat($lat) {
        $this->lat = $lat;
    }

    public function getLat() {
        return $this->lat;
    }

    public function setLon($lon) {
        $this->lon = $lon;
    }

    public function getLon() {
        return $this->lon;
    }

    public function setId($id) {
        $this->id = $id;
    }

    public function getId() {
        return $this->id;
    }

    public function serialize() {
        return array(
            'id' => (int) $this->id,
            'lat' => (double) $this->lat,
            'lon' => (double) $this->lon
        );
    }
}
