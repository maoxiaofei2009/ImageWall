<?php

/**
 * Model for @Tag class database manipulation.
 */
class TagModel {

    /**
     * @var PDO
     */
	private $db;

	function __construct($db) {
		$this->db = $db;
	}

    /**
     * Insert new tag into database.
     *
     * @param $tag Tag
     * @return bool
     */
    public function insert($tagValue) {
        $tag = new Tag();
        $tag->setValue($tagValue);

        $query = $this->db->prepare("INSERT INTO tags (value) VALUES(:tag_value)");
        $query->bindValue(":tag_value", $tagValue);
        $query->execute();

        $tag->setId($this->db->lastInsertId());

        return $tag;
    }

    /**
     * Get tag by it's id.
     *
     * @param $id
     * @return null|Tag
     */
	public function getTagById($id) {
        $tag = new Tag();
        $tag->setId($id);

		$query = $this->db->prepare("SELECT * FROM tags WHERE tag_id = :tag_id");
		$query->bindValue(":tag_id", $id);
		$query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $tag->setValue($row['value']);
        } else {
            return null;
        }

        return $tag;
	}

    /**
     * Get tag by it's name/value.
     *
     * @param $value
     * @return null|Tag
     */
    public function getTagByValue($value) {
        $tag = new Tag();
        $tag->setValue($value);

        $query = $this->db->prepare("SELECT * FROM tags WHERE value = :value");
        $query->bindValue(":value", $value);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $tag->setId($row['tag_id']);
        } else {
            return null;
        }

        return $tag;
    }

    /**
     * Get all tags from specific date till now.
     *
     * @param $fromDate
     * @return array
     */
    public function getTags($fromDate) {
        $tags = array();

        $tagModel = new TagModel($this->db);

        $query = $this->db->prepare("SELECT tag_id, date_created, COUNT(*) as occurrences FROM images WHERE date_created > :from_date GROUP BY tag_id;");
        $query->bindValue(":from_date", $fromDate);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        while($row = $query->fetch()) {
            $tag = $tagModel->getTagById($row['tag_id']);
            if($tag != null) {
                $tag->setOccurrences($row['occurrences']);
                $tags[] = $tag;
            }
        }

        return $tags;
    }

    /**
     * Get all tags around some location within some $radius.
     *
     * @param $lat
     * @param $lon
     * @param $radius
     * @return array
     */
    public function getTagsAroundLocation($lat, $lon, $radius) {
        $locationModel = new LocationModel($this->db);
        $locations = $locationModel->getLocations($lat, $lon, $radius);

        $locationsIds = array();
        foreach($locations as $location) {
            $locationsIds[] = $location->getId();
        }

        $tagIds = array();
        $query = $this->db->prepare("SELECT tag_id FROM images WHERE location_id IN(" . implode(',', $locationsIds) . ")");
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        while($row = $query->fetch()) {
            $tagId = $row['tag_id'];
            if(!in_array($tagId, $tagIds)) {
                $tagIds[] = $tagId;
            }
        }

        $tags = array();
        $tagModel = new TagModel($this->db);
        foreach($tagIds as $id) {
            $tag = $tagModel->getTagById($id);
            if($tag != null) {
                $tags[] = $tag;
            }
        }

        return $tags;
    }
}

?>