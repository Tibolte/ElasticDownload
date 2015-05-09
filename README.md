# We are not Gif makers, We are developers
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tibolte/elasticdownload/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tibolte/elasticdownload)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-ElasticDownload-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1747)

Recently me and my friend came across this downloading animation on Dribble: https://dribbble.com/shots/1887815-Download?list=users&offset=4
We found it so nice that we decided to develop it for Android!

We implemented it with a custom view, playing with canvas rotation and path drawing.

![](https://raw.githubusercontent.com/Tibolte/ElasticDownload/master/success.gif)
![](https://raw.githubusercontent.com/Tibolte/ElasticDownload/master/fail.gif)

Usage
===============================

Grab it from maven:

```groovy
    compile 'com.github.tibolte:elasticdownload:1.0.+'
````


Declare this view in your layout like below, it's also possible to inflate it manually.

```java
    <is.arontibo.library.ElasticDownloadView
        android:id="@+id/elastic_download_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"/>
````

At first, call startIntro() to make the view be able to display any percentage:

```java
    @InjectView(R.id.elastic_download_view) ElasticDownloadView mElasticDownloadView;
    
    mElasticDownloadView.startIntro();
````

Set any progress:

```java
    mElasticDownloadView.setProgress(25);
````

Notify if the download has failed or not:

```java
    mElasticDownloadView.success(); //This function moves the cursor to 100 if the progress has not been set already
    
    mElasticDownloadView.fail();
````

# Participating?
Make your pull requests on feature or bugfix branches.

License
-----------

    Copyright 2015 Thibault Guégan and Aron Ingi Óskarsson

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
