<?php

/**
 * Model for @Location class database manipulation.
 */
class LocationModel {

    /**
     * @var PDO
     */
    private $db;

    private $MIN_LAT;
    private $MAX_LAT;
    private $MIN_LON;
    private $MAX_LON;

    function __construct($db) {
        $this->db = $db;

        $this->MIN_LAT = deg2rad(-90);
        $this->MAX_LAT = deg2rad(90);
        $this->MIN_LON = deg2rad(-180);
        $this->MAX_LON = deg2rad(180);
    }

    /**
     * Insert new location into database.
     *
     * @param $lat
     * @param $lon
     * @return Location
     */
    public function insert($lat, $lon) {
        $location = new Location();
        $location->setLat($lat);
        $location->setLon($lon);

        $query = $this->db->prepare("
            INSERT INTO locations (lat, lon)
            VALUES(:lat, :lon)
        ");
        $query->bindValue(":lat", $lat);
        $query->bindValue(":lon", $lon);
        $query->execute();

        $location->setId($this->db->lastInsertId());

        return $location;
    }

    /**
     * Get location by it's id.
     *
     * @param $id
     * @return Location|null
     */
    public function getLocationById($id) {
        $location = new Location();
        $location->setId($id);

        $query = $this->db->prepare("SELECT lat, lon FROM locations WHERE location_id = :location_id");
        $query->bindValue(":location_id", $id);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $location->setLat($row['lat']);
            $location->setLon($row['lon']);
        } else {
            return null;
        }

        return $location;
    }

    /**
     * Get location by it's latitude and longitude values.
     *
     * @param $lat
     * @param $lon
     * @return Location|null
     */
    public function getLocationByValue($lat, $lon) {
        $location = new Location();
        $location->setLat($lat);
        $location->setLon($lon);

        $query = $this->db->prepare("SELECT location_id FROM locations WHERE lat = :lat_value AND lon = :lon_value");
        $query->bindValue(":lat_value", strval($lat));
        $query->bindValue(":lon_value", strval($lon));
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $location->setId($row['location_id']);
        } else {
            return null;
        }

        return $location;
    }

    /**
     * Get all locations within some radius from source location.
     *
     * @param $lat
     * @param $lon
     * @param $radius
     * @return Location[]
     */
    public function getLocations($lat, $lon, $radius) {
        $locations = array();

        $query = $this->db->prepare("
            SELECT ((ACOS(SIN(:my_lat * PI() / 180) * SIN(lat * PI() / 180) +
            COS(:my_lat * PI() / 180) * COS(lat * PI() / 180) * COS((:my_lon - lon) *
                PI() / 180)) * 180 / PI()) * 60 * 1.1515) AS distance, location_id, lat, lon
            FROM locations
            HAVING distance <= :my_distance ORDER BY distance ASC
        ");
        $query->bindValue(":my_lat", $lat);
        $query->bindValue(":my_lon", $lon);
        $query->bindValue(":my_distance", $radius);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        while($row = $query->fetch()) {
            $location = new Location();
            $location->setId($row['location_id']);
            $location->setLat($row['lat']);
            $location->setLon($row['lon']);

            $locations[] = $location;
        }

        return $locations;
    }

}
