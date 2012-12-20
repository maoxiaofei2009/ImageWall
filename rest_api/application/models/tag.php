<?php

class Tag {

	private $id;
	private $value;
    private $occurrences;

	public function getId() {
		return $this->id;
	}
	public function setId($id) {
		$this->id = (int) $id;
	}

	public function setValue($value) {
        $value = preg_replace("/[^a-zA-Z0-9 -]/i", "", $value);
		$this->value = $value;
	}

	public function getValue() {
		return $this->value;
	}

    public function setOccurrences($occurrences) {
        $this->occurrences = $occurrences;
    }

    public function getOccurrences() {
        return $this->occurrences;
    }

    public function serialize() {
        $array = array(
            'id' => (int) $this->id,
            'value' => $this->value
        );

        if($this->occurrences != null) {
            $array['occurrences'] = (int) $this->occurrences;
        }

        return $array;
    }

}

?>