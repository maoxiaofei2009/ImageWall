ImageWall
=========

Android application made for 'Best Code Challenge 3.0' (student competition in programming) according to competitions
specifications in ~3 days. The goal was to made the 'ImageWall' - think of it like the
TwitterWall but with images.

Every image can have description, tag an location and can be searched on that
specific tag.

Mobile application is part of larger project which consists of REST API, web
and mobile app.

Features
--------
 * 2 levels of cache - [LRU Memory Cache](http://developer.android.com/reference/android/util/LruCache.html) and Disk Cached (SD Card)
 * connects to ImageWall REST API via [Asynchronous Http Client](http://loopj.com/android-async-http/) and parses JSON with [GSON library](http://code.google.com/p/google-gson/)
 * saves information to database via [ORMLite](http://ormlite.com/) library
 * retrieves user location via [Little Fluffy Location](http://code.google.com/p/little-fluffy-location-library/) library
 * for image chooser uses [ImageChooser](https://github.com/svenkapudija/Android-ImageChooser) library
 * implemented [Pull To Refresh](https://github.com/chrisbanes/Android-PullToRefresh/) design pattern
 * Google Maps

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
