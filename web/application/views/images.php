<?php include('assets/inc/header/header.php'); ?>
<?php include('assets/inc/skeleton/header.php'); ?>

<div id="container">
    <div class="wrapper clearfix">
        <div class="title-bar">
            <h1><?php echo '#' . $tag ?></h1>
        </div>
    </div>
    <div class="content clearfix">
        <div class="search-results clearfix">
            <?php
                if(isset($images->error)) {
                    echo '<h1>No images found.</h1>';
                } else {
                    foreach($images as $image) { ?>
                        <div class="image small-image">
                            <a href="/image?id=<?php echo $image->id ?>"><img src="<?php echo 'http://api.team36.host25.com' . $image->sizes->thumbnails->square ?>" alt="" title="" ></a>
                        </div>
                     <?php }
                }
            ?>
        </div>
    </div>
</div>

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
                    var url = this.sizes.thumbnails.square;
                    var id = this.id;

                    console.log(url);
                    if(url != firstImageUrl) {
                        images.prepend('<div class="image small-image"><a href="/image?id=' + id + '"><img src="http://api.team36.host25.com' + url + '" alt="" title="" ></a></div>');
                    } else {
                        return false;
                    }
                });
            }
        );
    }

    function getFirstImageUrl() {
        var firstImage = $('.small-image img:first');
        var firstImageUrl = firstImage.attr('src').replace('http://api.team36.host25.com', '');
        return firstImageUrl;
    }
</script>

<?php include('assets/inc/skeleton/footer.php'); ?>
<?php include('assets/inc/footer/footer.php'); ?>