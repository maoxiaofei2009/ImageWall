![ImageWall Android Cover](http://i.imgur.com/6ueAQQm.png)

> Project is far from bug-free and for example Android app is missing some components like [BugSense](http://www.bugsense.com/)/[Crittercism](http://www.crittercism.com/) for bug reporting, [Flurry](http://www.flurry.com/)/[Localytics](http://www.localytics.com/) for
> analytics which are required for production version etc. but the "core" is working.
>
> I tried to experiment with [MongoDB](http://www.mongodb.org/) (because of it's [geolocational native features](http://docs.mongodb.org/manual/core/geospatial-indexes/)) and Java on the server but the jury
> decided that for the sake of simplicity only SQL databases are supported and therefore my fallback was the PHP-MySQL version because I knew
> I could finish on-time (I have some experience in PHP/MySQL).

Project made for **Best Code Challenge 3.0** (student competition in programming at [Faculty of Electrical Engineering and Computing @ Zagreb, Croatia](http://www.fer.unizg.hr/en)) according to competitions
specifications in ~3-4 days (official deadline was 10 days). The goal was to made the 'ImageWall' - think of it like the
TwitterWall but with images.

Every image can have description, tag and location and can be searched on specific tag.

> **UPDATE:** I won **2nd place**. The reasons why I didn't won the first place (according to jury) are the bugs on the server side (division by zero),
> sometimes the refreshed image (via AJAX) was corrupted and (probably code?) documentation was not sufficient.
>
> Notice: I won the **1st place** on **Best Code Challenge v2.0**.

Documentation
--------

###This project takes leverage of...

PHP, MySQL, Java, Android, Google Maps, JavaScript, jQuery, CodeIgniter, Google GSON, AJAX, REST API, MVC pattern,
ORMLite, Little Fluffy Location, Android Asynchronous Http Client, LRU Cache, PullToRefresh...

###Project architecture

![ImageWall project architecture](http://www.svenkapudija.com/projects/image-wall/imagewall_architecture.png)

###Server side (REST API)
--------

    application/models
    application/views
    application/controllers

Mobile application and web-page are connected to REST API. System is based on [CodeIgniter framework](http://ellislab.com/codeigniter) with the help
of [CodeIgniter Rest Server](https://github.com/philsturgeon/codeigniter-restserver) library.

####Database

MySQL database consists of 3 tables:
 - images
 - tags
 - locations

####Image resize

To improve the performance of the smartphone app all resizing is made on the server via [class.upload.php](http://www.verot.net/php_class_upload.htm)
library into 5 different sizes

 - original
 - default size on the web (when single image is viewed)
 - thumbnail size when the collection of images are viewed (for ex. searching on some tag)
 - thumbnail size when looking at collection from `iframe` container
 - thumbnail size for Android devices

####Duplicates

It's not possible to insert two same images on the service, in any timespan - every image is hashed via `SHA-1` hash and that
hash is inserted into the database.
// TODO: Loose the restriction and if the timespan is large enough, still enable the possibility to put 2 identical
images. Requirement: autentification via user accounts (which aren't made in the scope of this project).

###Server side (web page)
--------

    application/models
    application/views
    application/controllers

Here it's also used [CodeIgniter framework](http://ellislab.com/codeigniter) with the clear MVC design pattern. To make a connection
onto REST API there is small wrapper which uses [cURL wrapper](https://github.com/shuber/curl) library and it just uses simple
GET (or POST to upload) and JSON decoding via PHP's `json_decode`.

####Embedded view

If you want to show the images into `embededd` view - inside `iframe` container you should add `type=iframe` parameter onto
`/images?tag=myTag` url which is used to retrieve 'classic' web page. So if you request it without parameter you'll get normal web page,
if you add the parameter you'll get embedded view of the same page.

[![ImageWall Android ScreenShot](http://www.svenkapudija.com/projects/image-wall/iframe_thumb.jpg)](http://www.svenkapudija.com/projects/image-wall/iframe.jpg)

####Live refresh

Live refresh is enabled every 10 seconds via **AJAX** and if there are some new images from REST API - they'll show on the web-page
via [jQuery](http://jquery.com/) library with the help of some JavaScript interval.

####Google Maps
In order to show the image location I used [Google Maps JavaScript API v3](https://developers.google.com/maps/documentation/javascript/).

####Screenshots

[![ImageWall Web ScreenShot 1](http://www.svenkapudija.com/projects/image-wall/web_ss_1_thumb.jpg)](http://www.svenkapudija.com/projects/image-wall/web_ss_1.jpg)
[![ImageWall Web ScreenShot 1](http://www.svenkapudija.com/projects/image-wall/web_ss_2_thumb.jpg)](http://www.svenkapudija.com/projects/image-wall/web_ss_2.jpg)
[![ImageWall Web ScreenShot 1](http://www.svenkapudija.com/projects/image-wall/web_ss_3_thumb.jpg)](http://www.svenkapudija.com/projects/image-wall/web_ss_3.jpg)

###Mobile application (Android)
--------

    src/com/svenkapudija/imagewall/*
    res/*

Application is connected to REST API to get the new information and uses 2 levels of cache to store `Bitmaps` - in memory
caching and disk caching (actually SD-card). For in-memory cache [LRU Memory Cache](http://developer.android.com/reference/android/util/LruCache.html)
is used.

For client-side library that connects to REST API I used [Asynchronous Http Client](http://loopj.com/android-async-http/)
as the main UI thread would remain free and responsive.

Mapping between JSON (from REST API) and Java classes is done with the help of Java reflection - [GSON library](http://code.google.com/p/google-gson/)
which does it without any kind of annotations.

Storing data into SQLite database (information about images, tags and locations) is done with the help of
[ORMLite](http://ormlite.com/) library.

To retrieve user location application uses passive location retrieval (and active if passive wasn't precise enough)
with the help of [Little Fluffy Location](http://code.google.com/p/little-fluffy-location-library/) library.  
*passive location retrieaval means that if some other application on your phone (for ex. Google Maps) already retrieved
your location via GPS, there is no need to retrieve it again and waste the battery but other applications can just
pick it up - better retrieval speed and better efficiency.*

For image retrieaval from camera or gallery I used personal small library - ImageChooser.

####PullToRefresh

There is no button for refresh but application uses UI design pattern 'Pull To Refresh' with the help of
[Pull To Refresh](https://github.com/chrisbanes/Android-PullToRefresh/) library.

####Search
Search (by tag) is implemented on the `Search` key on the device itself as well as in the button in the action bar which
opens default Android implementation of the **Search Bar**.

[![ImageWall Search](http://www.svenkapudija.com/projects/image-wall/android_ss_4_thumb.png)](http://www.svenkapudija.com/projects/image-wall/android_ss_4.png)

####Google Maps API

All images that have location available (user sent the location bundled with the image on the upload) are viewable on the
Google Maps.

####Compatibility

Application supports everything from Android version [2.2, Froyo (API level 8)](http://developer.android.com/about/versions/android-2.2.html).

####Devices

Application is tested on the [HTC Desire](http://www.gsmarena.com/htc_desire-3077.php) [Android 2.3](http://developer.android.com/about/versions/android-2.3.3.html).

####Screenshots

[![ImageWall Android ScreenShot 1](http://www.svenkapudija.com/projects/image-wall/android_ss_1_thumb.png)](http://www.svenkapudija.com/projects/image-wall/android_ss_1.png)
[![ImageWall Android ScreenShot 2](http://www.svenkapudija.com/projects/image-wall/android_ss_2_thumb.png)](http://www.svenkapudija.com/projects/image-wall/android_ss_2.png)
[![ImageWall Android ScreenShot 3](http://www.svenkapudija.com/projects/image-wall/android_ss_3_thumb.png)](http://www.svenkapudija.com/projects/image-wall/android_ss_3.png)

####Other
 - [Git](http://git-scm.com/) is used as the DVCS system
 - design elements (web and mobile) are done with the help of [The Bricks Frameowork](http://designmodo.com/the-bricks-addons/)

Developed by
------------
* Sven Kapuđija

License
-------

    Copyright 2012 Sven Kapuđija
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
