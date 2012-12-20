<?php

class Image {

    private $id;
    private $description;
    private $dateCreated;
    private $hash;
    private $fileSize;
    private $tag;
    private $location;

    public function setDateCreated($dateCreated) {
        $this->dateCreated = $dateCreated;
    }

    public function getDateCreated() {
        return $this->dateCreated;
    }

    public function setDescription($description) {
        $this->description = $description;
    }

    public function getDescription() {
        return $this->description;
    }

    public function setHash($hash) {
        $this->hash = $hash;
    }

    public function getHash() {
        return $this->hash;
    }

    public function setId($id) {
        $this->id = $id;
    }

    public function getId() {
        return $this->id;
    }

    public function setLocation($location) {
        $this->location = $location;
    }

    public function getLocation() {
        return $this->location;
    }

    public function setTag($tag) {
        $this->tag = $tag;
    }

    public function getTag() {
        return $this->tag;
    }

    public function setFileSize($fileSize) {
        $this->fileSize = $fileSize;
    }

    public function getFileSize() {
        return $this->fileSize;
    }

    public function serialize() {
        $array = array(
            'id' => (int) $this->id,
            'dateCreated' => $this->dateCreated,
            'fileName' => $this->getFileName(),
            'fileSize' => (int) $this->fileSize,
            'sizes' => array(
                'original' => "/files/images/original/" . $this->getFileName(),
                'web_default' => "/files/images/default/" . $this->getFileName(),
                'thumbnails' => array(
                    'embedded' => "/files/images/thumbnail/embedded/" . $this->getFileName(),
                    'square' => "/files/images/thumbnail/square/" . $this->getFileName(),
                    'android' => "/files/images/thumbnail/android/" . $this->getFileName()
                )
            )
        );

        if($this->description != null) {
            $array['description'] = $this->description;
        }

        if($this->tag != null) {
            $array['tag'] = $this->tag->serialize();
        }

        if($this->location != null) {
            $array['location'] = $this->location->serialize();
        }

        return $array;
    }

    public function getFileName() {
        return $this->id . "_" . $this->hash . ".jpg";
    }
}
