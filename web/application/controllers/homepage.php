<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
 * Default 'index' page. Displays all the tags with their variable size
 * depending on number of times they occurred (tied to images).
 */
class Homepage extends CI_Controller {

	public function index() {
        $timespanInHours = $this->input->get('timespanInHours');
        if($timespanInHours == null) {
            // Last 24 hours by default
            $timespanInHours = 24;
        }

        $api = new ImageWallApi();
        $tags = $api->getTags($timespanInHours*60);

        $data = array(
            'timespanInHours' => $timespanInHours
        );

        if(!isset($tags->error)) {
            // Get min and max occurrences
            // TODO: Make the API do it instead

            // Find min and max
            $occurrencesMin = -1;
            $occurrencesMax = 0;
            foreach($tags as $tag) {
                $occurrences = $tag->occurrences;
                if($occurrencesMin == -1 || $occurrences < $occurrencesMin) {
                    $occurrencesMin = $occurrences;
                }

                if($occurrences > $occurrencesMax) {
                    $occurrencesMax = $occurrences;
                }
            }

            // Add the 'size' value to every tag object so that view can
            // properly render it
            foreach($tags as $tag) {
                $tag->size = $this->getSize($occurrencesMin, $occurrencesMax, $tag->occurrences);
            }

            $data['tags'] = $tags;
        }

		$this->load->view('homepage', $data);
	}

    /**
     * Math function that scales down range [occurrencesMin, occurrencesMin] to [minSize, maxSize] where
     * min and max sizes are CSS styled attributes (variable tag size).
     *
     * @param $occurrencesMin
     * @param $occurrencesMax
     * @param $occurrences
     * @return float
     */
    private function getSize($occurrencesMin, $occurrencesMax, $occurrences) {
        $numerator = (29-12)*($occurrences-$occurrencesMin);
        $denominator = $occurrencesMax-$occurrencesMin;
        $result = $numerator/$denominator + 12;

        return round($result);
    }

}