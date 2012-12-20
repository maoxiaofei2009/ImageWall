<?php include('assets/inc/header/header.php'); ?>

<div id="iframe">
    
    <div id="header">
        <div class="wrapper clearfix">

            <div id="logo">
                <a href="index.php" target="_blank"><img src="assets/css/i/logo.png" alt="Image Wall" title="Image Wall" /></a>
            </div>

            <div id="tag">
                <ul>
                    <li><a href="/images?tag=<?php echo $tag ?>" target="_blank"><?php echo '#' . $tag ?></a></li>
                </ul>
            </div>

        </div>
    </div>

    <div id="container">
        <div class="content clearfix">
            <div class="search-results clearfix">
                <?php
                if(isset($images->error)) {
                    echo '<h1>No images found.</h1>';
                } else {
                    foreach($images as $image) { ?>
                        <div class="image embedded-image">
                            <a href="/image?id=<?php echo $image->id ?>" target="_blank"><img src="<?php echo 'http://api.team36.host25.com' . $image->sizes->thumbnails->embedded ?>" alt="" title="" ></a>
                            <?php if(isset($image->description)) { ?>
                                <span class="mytag"><?php echo $image->description ?></span>
                            <?php } ?>
                        </div>
                        <?php }
                }
                ?>
            </div>
        </div>
    </div>

    <!--
    <div id="footer">
        <div class="wrapper clearfix">
            <div id="tag">
                <a href="image.php">Map View</a>
            </div>
        </div>
    </div>
    -->

    <script type="text/javascript">

        var refreshRateInSeconds = 10;

        var images;
        var jsonUrl = "http://api.team36.host25.com/images";

        $(document).ready(function() {
            images = $('.search-results');

            setInterval(function() {
                refreshImages();
            }, refreshRateInSeconds*1000);
        });

        function refreshImages() {
            var firstImageUrl = getFirstImageUrl();

            $.getJSON(
                jsonUrl,
                {tag: '<?php echo $tag ?>'},
                function(json) {
                    jQuery.each(json, function() {
                        var url = this.sizes.thumbnails.embedded;
                        var id = this.id;

                        console.log(url);
                        if(url != firstImageUrl) {
                            images.prepend('<div class="image embedded-image"><a href="/image?id=' + id + '"><img src="http://api.team36.host25.com' + url + '" alt="" title="" ></a></div>');
                        } else {
                            return false;
                        }
                    });
                }
            );
        }

        function getFirstImageUrl() {
            var firstImage = $('.embedded-image img:first');
            var firstImageUrl = firstImage.attr('src').replace('http://api.team36.host25.com', '');
            return firstImageUrl;
        }
    </script>

<?php include('assets/inc/footer/footer.php'); ?>