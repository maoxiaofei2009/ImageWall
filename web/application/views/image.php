<?php include('assets/inc/header/header.php'); ?>
<?php include('assets/inc/skeleton/header.php'); ?>

<div id="container">
    <div class="wrapper clearfix">
        <div class="title-bar">
            <?php
                if(isset($image->description)) {
                    echo '<h1>' . $image->description;
                    if(isset($image->tag)) {
                        echo ' <span class="gray">' . $image->tag->value . '</span>';
                    }
                    echo '</h1>';
                } else if(isset($image->tag)) {
                    echo '<h1>' . $image->tag->value . '</h1>';
                } else {
                    echo '<h1>Untitled</h1>';
                }
            ?>
        </div>
        <div class="content clearfix">
            <div class="tabs clearfix">
                <ul>
                    <li class="tab-image active"><a href="#">Image</a></li>
                    <?php
                    if(isset($image->location)) { ?>
                        <li class="tab-map"><a href="#">Map</a></li>
                        <?php } ?>
                </ul>
            </div>
            <div class="tab-1 large-image" style="text-align: center; margin-top: 20px; margin-bottom: 20px">
                <a href="http://api.team36.host25.com<?php echo $image->sizes->original ?>" target="_blank"><img src="http://api.team36.host25.com<?php echo $image->sizes->web_default ?>" alt="" title="" ></a>
            </div>
            <?php
                if(isset($image->location)) { ?>
                    <div class="tab-2 google-map" id="map_canvas" style="width: 100%; height: 500px; margin-top: 20px; display: none;">
                    </div>
            <?php } ?>
        </div>
    </div>
</div>

<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCzKXstpY2YFGrRgBKrNnd77EaDXTYEWt0&sensor=false"></script>
<script type="text/javascript">
        $(document).ready(function() {
            $('.tab-map').on('click', function() {
                initMap();
            });
        });

        function initMap() {
            var mapOptions = {
                center: new google.maps.LatLng(<?php echo isset($image->location) ? $image->location->lat . "," . $image->location->lon : '0,0' ?>),
                zoom: 16,
                mapTypeId: google.maps.MapTypeId.ROADMAP
            };
            var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);

            var image = 'assets/images/map_marker.png';
            var myLatLng = new google.maps.LatLng(<?php echo isset($image->location) ? $image->location->lat . "," . $image->location->lon : '0,0' ?>);
            var beachMarker = new google.maps.Marker({
                position: myLatLng,
                map: map,
                icon: image
            });
        }
</script>

<?php include('assets/inc/skeleton/footer.php'); ?>
<?php include('assets/inc/footer/footer.php'); ?>