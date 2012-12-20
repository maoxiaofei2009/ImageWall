<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
 * Display single image and it's information if available (description, tag, location).
 */
class Image extends CI_Controller {

	public function index() {
        $imageId = $this->input->get('id');

        $api = new ImageWallApi();
        $image = $api->getImageById($imageId);

        $data = array(
            'image' => $image
        );

		$this->load->view('image', $data);
	}

}