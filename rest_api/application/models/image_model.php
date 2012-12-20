<?php

/**
 * Model for @Image class database manipulation.
 */
class ImageModel {

    /**
     * @var PDO
     */
    private $db;

    function __construct($db) {
        $this->db = $db;
    }

    /**
     * Insert new image into database.
     *
     * @param $sha1
     * @param $fileSize
     * @param $imageDescription
     * @param $tag
     * @param $location
     * @return Image
     */
    public function insert($sha1, $fileSize, $imageDescription, $tag, $location) {
        $image = new Image();
        $image->setHash($sha1);
        $image->setFileSize($fileSize);
        $image->setDescription($imageDescription);

        $dateCreated = gmdate("Y-m-d H:i:s");
        $image->setDateCreated($dateCreated);

        $query = $this->db->prepare("
            INSERT INTO images (description, file_size, sha1_hash, date_created, tag_id, location_id)
            VALUES(:description, :file_size, :sha1_hash, :date_created, :tag_id, :location_id)
        ");
        $query->bindValue(":description", $imageDescription);
        $query->bindValue(":file_size", $fileSize);
        $query->bindValue(":sha1_hash", $sha1);
        $query->bindValue(":date_created", $dateCreated);

        if($tag != null) {
            $tagModel = new TagModel($this->db);
            $myTag = $tagModel->getTagByValue($tag->getValue());
            if($myTag == null) {
                $myTag = $tagModel->insert($tag->getValue());
            }

            $query->bindValue(":tag_id", $myTag->getId());
            $image->setTag($myTag);
        } else {
            $query->bindValue(":tag_id", 0);
        }

        if($location != null) {
            $locationModel = new LocationModel($this->db);
            $myLocation = $locationModel->getLocationByValue($location->getLat(), $location->getLon());
            if($myLocation == null) {
                $myLocation = $locationModel->insert($location->getLat(), $location->getLon());
            }

            $query->bindValue(":location_id", $myLocation->getId());
            $image->setLocation($myLocation);
        } else {
            $query->bindValue(":location_id", 0);
        }

        $query->execute();

        $image->setId($this->db->lastInsertId());

        return $image;
    }

    public function createImage($imageBytes, $imageDescription, $tag, $location) {
        $imageFile = new Upload($imageBytes);
        if ($imageFile->uploaded) {
            $extension = exif_imagetype($imageFile->file_src_pathname);

            // Constant 2 = JPEG
            if($extension != 2) {
                throw new InvalidImageExtensionException();
            }

            $sha1 = sha1_file($imageFile->file_src_pathname);

            if($this->imageExists($sha1)) {
                throw new ImageAlreadyExistsException();
            }

            $fileSize = filesize($imageFile->file_src_pathname);

            $imageModel = new ImageModel($this->db);
            $image = $imageModel->insert($sha1, $fileSize, $imageDescription, $tag, $location);
            $imageName = $image->getId() . "_" . $sha1;

            $this->saveOriginal($imageFile, $imageName);
            $this->saveThumbnailSquare($imageFile, $imageName);
            $this->saveThumbnailEmbedded($imageFile, $imageName);
            $this->saveWebDefault($imageFile, $imageName);
            $this->saveThumbnailAndroid($imageFile, $imageName);

            return $image;
        }
    }

    public function saveWebDefault($imageFile, $imageName) {
        $imageFile->file_new_name_body = $imageName;
        $imageFile->image_resize = true;
        $imageFile->image_x = 890;
        $imageFile->image_y = 1500;
        $imageFile->image_ratio = true;
        $imageFile->Process('files/images/default');
    }

    public function saveThumbnailEmbedded($imageFile, $imageName) {
        $imageFile->file_new_name_body = $imageName;
        $imageFile->image_resize = true;
        $imageFile->image_ratio_crop = true;
        $imageFile->image_x = 300;
        $imageFile->image_y = 100;
        $imageFile->Process('files/images/thumbnail/embedded');
    }

    public function saveThumbnailAndroid($imageFile, $imageName) {
        $imageFile->file_new_name_body = $imageName;
        $imageFile->image_resize = true;
        $imageFile->image_ratio_crop = true;
        $imageFile->image_x = 550;
        $imageFile->image_y = 177;
        $imageFile->Process('files/images/thumbnail/android');
    }

    public function saveOriginal($imageFile, $imageName) {
        $imageFile->file_new_name_body = $imageName;
        $imageFile->image_resize = true;
        $imageFile->image_ratio = true;
        $imageFile->image_x = 1280;
        $imageFile->image_y = 800;
        $imageFile->Process('files/images/original');
    }

    public function saveThumbnailSquare($imageFile, $imageName) {
        $imageFile->file_new_name_body = $imageName;
        $imageFile->image_resize = true;
        $imageFile->image_ratio_crop = true;
        $imageFile->image_x = 170;
        $imageFile->image_y = 170;
        $imageFile->Process('files/images/thumbnail/square');
    }

    private function imageExists($sha1) {
        $imageModel = new ImageModel($this->db);
        return $imageModel->getImageByHash($sha1) != null;
    }

    public function getImageByHash($hash) {
        $image = new Image();

        $locationModel = new LocationModel($this->db);
        $tagModel = new TagModel($this->db);

        $query = $this->db->prepare("SELECT * FROM images WHERE sha1_hash = :hash");
        $query->bindValue(":hash", $hash);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $image = $this->retrieveImage($row, $tagModel, $locationModel);
        } else {
            return null;
        }

        return $image;
    }

    public function getImageById($id) {
        $image = new Image();

        $locationModel = new LocationModel($this->db);
        $tagModel = new TagModel($this->db);

        $query = $this->db->prepare("SELECT * FROM images WHERE image_id = :image_id");
        $query->bindValue(":image_id", $id);
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        if($row = $query->fetch()) {
            $image = $this->retrieveImage($row, $tagModel, $locationModel);
        } else {
            return null;
        }

        return $image;
    }

    public function olderThan($row, $seconds) {
        $now = strtotime(date("Y-m-d H:i:s"));
        $dateCreated = strtotime($row['date_created']);

        return ($now-$dateCreated) > $seconds;
    }

    public function getImages() {
        $images = array();

        $locationModel = new LocationModel($this->db);
        $tagModel = new TagModel($this->db);

        $query = $this->db->prepare("SELECT * FROM images");
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        while($row = $query->fetch()) {
            if($this->olderThan($row, 15)) {
                $image = $this->retrieveImage($row, $tagModel, $locationModel);
                $images[] = $image;
            }
        }

        return $images;
    }

    public function getImagesWithTag($tagValue) {
        $images = array();

        $locationModel = new LocationModel($this->db);
        $tagModel = new TagModel($this->db);

        $tag = $tagModel->getTagByValue($tagValue);
        if($tag == null) {
            return $images;
        }

        $query = $this->db->prepare("SELECT * FROM images WHERE tag_id = :tag_id");
        $query->bindParam(':tag_id', $tag->getId());
        $query->setFetchMode(PDO::FETCH_ASSOC);
        $query->execute();
        while($row = $query->fetch()) {
            if($this->olderThan($row, 15)) {
                $image = $this->retrieveImage($row, $tagModel, $locationModel);
                $images[] = $image;
            }
        }

        return $images;
    }

    /**
     * @param $row
     * @param $tagModel         TagModel
     * @param $locationModel    LocationModel
     * @return Image
     */
    public function retrieveImage($row, $tagModel, $locationModel) {
        $image = new Image();
        $image->setId($row['image_id']);
        $image->setDescription($row['description']);
        $image->setDateCreated($row['date_created']);
        $image->setHash($row['sha1_hash']);
        $image->setFileSize($row['file_size']);

        $tag = $tagModel->getTagById($row['tag_id']);
        $image->setTag($tag);

        $location = $locationModel->getLocationById($row['location_id']);
        $image->setLocation($location);
        return $image;
    }

}
