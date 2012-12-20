<?php include('assets/inc/header/header.php'); ?>
<?php include('assets/inc/skeleton/header.php'); ?>

<div id="container">
    <div class="wrapper clearfix">
        <div class="title-bar">
            <h1>Tags</h1>
            <div class="title-options">
                <div class="dummy-select"><label>Last <?php echo $timespanInHours . ' hour' . ($timespanInHours > 1 ? 's' : ''); ?></label>
                    <ul>
                        <li><a href="/?timespanInHours=1">Last 1 hour</a></li>
                        <li><a href="/?timespanInHours=6">Last 6 hours</a></li>
                        <li><a href="/?timespanInHours=12">Last 12 hours</a></li>
                        <li><a href="/?timespanInHours=24">Last 24 hours</a></li>
                    </ul>
                </div>
            </div>
        </div>
        <div class="content clearfix">
            <div class="tag-cloud clearfix">
                <?php
                    if(isset($tags)) {
                        foreach($tags as $tag) {
                            echo '<a href="/images?tag=' . $tag->value . '"><span class="size-' . $tag->size . '">' . $tag->value . '</span></a> ';
                        }
                    } else {
                        echo '<h1>No images in provided timespan.</h1>';
                    }
                ?>
            </div>
        </div>
    </div>
</div>

<?php include('assets/inc/skeleton/footer.php'); ?>
<?php include('assets/inc/footer/footer.php'); ?>