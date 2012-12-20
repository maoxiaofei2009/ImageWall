<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
 * Display all the images (thumbnails) with specific tag.
 */
class Images extends CI_Controller {

	public function index() {
        $tag = $this->input->get('tag');

        $api = new ImageWallApi();
        $images = $api->getImagesWithTag($tag);

        $data = array(
            'images' => $images,
            'tag' => $tag
        );

        $type = $this->input->get('type');
        if($type == 'iframe') {
            $this->load->view('images_iframe', $data);
        } else {
            $this->load->view('images', $data);
        }
	}

}