<?php defined('BASEPATH') OR exit('No direct script access allowed');

require APPPATH.'/libraries/REST_Controller.php';

require APPPATH.'/models/tag.php';
require APPPATH.'/models/tag_model.php';
require APPPATH.'/models/image.php';
require APPPATH.'/models/image_model.php';
require APPPATH.'/models/location.php';
require APPPATH.'/models/location_model.php';
require APPPATH.'/models/image_already_exists_exception.php';
require APPPATH.'/models/invalid_image_extension_exception.php';

/**
 * Retrieve image information and upload images via form-data.
 */
class Images extends REST_Controller {

    /**
     * Retrieve all the images.
     *
     * /images - get all images or
     * /images?tag=someTag - get all images that have specific tag
     */
	function index_get() {
        header('Access-Control-Allow-Origin: *');

        $tag = $this->get('tag');

        $imageModel = new ImageModel($this->db);

        if($tag != null) {
            $images = $imageModel->getImagesWithTag($tag);
        } else {
            $images = $imageModel->getImages();
        }

        // To make the newest first
        // TODO: Make the database query do it instead
        $images = array_reverse($images);

        $imagesJson = array();
        foreach($images as $image) {
            $imagesJson[] = $image->serialize();
        }

        if(empty($imagesJson)) {
            $this->response(error('No images found.', 404), 404);
        } else {
            $this->response($imagesJson);
        }
    }

    /**
     * Retrieve single image information.
     *
     * /images/id/1 - get image with ID = 1
     */
    function id_get() {
        header('Access-Control-Allow-Origin: *');

        $id = $this->uri->segment(3);

        $imageModel = new ImageModel($this->db);
        $image = $imageModel->getImageById($id);

        if($image == null) {
            $this->response(error('Image not found.', 404), 404);
        } else {
            $this->response($image->serialize());
        }
    }

    /**
     * Upload an image via form-data.
     * Allowed only .jpg format.
     *
     * Optional parameters: (lat, lon), tag, description.
     */
    function index_post() {
        header('Access-Control-Allow-Origin: *');

        if(!isset($_FILES['image'])) {
            $this->response(error('Provide \'image\' parameter.', 400), 400);
            return;
        }

        $imageBytes = $_FILES['image'];

        $imageDescription = $this->post('description');

        $lat = $this->post('lat');
        $lon = $this->post('lon');

        $location = null;
        if($lat != null && $lon != null) {
            $location = new Location();
            $location->setLat((double) $lat);
            $location->setLon((double) $lon);
        }

        $tagValue = $this->post('tag');

        $tag = null;
        if($tagValue != null) {
            $tag = new Tag();
            $tag->setValue($tagValue);
        }

        $imageModel = new ImageModel($this->db);

        try {
            $image = $imageModel->createImage($imageBytes, $imageDescription, $tag, $location);
            $this->response($image->serialize(), 201);
        } catch(ImageAlreadyExistsException $e) {
            $this->response(error('Image already exists.', 409), 409);
        } catch(InvalidImageExtensionException $e) {
            $this->response(error('Invalid image extension. Allowed only .jpg.', 400), 400);
        }
    }

}