<h1 align="center">Lingoparent - Mobile</h1></br>

<h5 align="center">
Mobile Repository For Lingoparent Main App
</h5>

<p align="center">
The Lingo Parent application is built with the aim that parents can monitor the activities and development of the child's learning on the Lingotalk platform
</p>

<p align="center">
<img src="./app/src/main/res/drawable/logo_lingoparents_big.png">
</p>

## Prerequisites

Make sure you use Android Studio Arctic Fox (2020.3.1 Patch 4) or later and Kotlin 203-1.6.10 or later

## Built with

* Android Studio with Kotlin
* MVVM Architecture
* Coroutines
  * Flow
* [Android Jetpack](https://developer.android.com/jetpack)
  * [Android KTX](https://developer.android.com/kotlin/ktx)
  * [Navigation](https://developer.android.com/guide/navigation)
  * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
  * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
  * [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
  * [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
* [Retrofit]()
* [Shimmer]()
* [Coil]()
* [Midtrans]()

## Features

##### Authentication

- Parent can login to lingoparent using email and password or google account
- Parent can register to lingoparent using email and password or google account
- Parent can use the android app to proceed with verify email after register successfull
- Parent can send forgot password request by email

##### Home

- Parent can see the list of recent live event, insight, and linked children
- Parent can navigate to detail event, detail insight, or children progress by clicking the item

##### Live Event

- Parent can see three different category of live event ("Live", "Upcoming", "Completed")
- Parent can search live event with keyword
- Parent can register the event and pay the event with several payment method
- Parent can join the zoom link after they has been registered

##### Insight

- Parent can see three different category of live event ("Live", "Upcoming", "Completed")
- Parent can search live event with keyword
- Parent can like or dislike the insight detail or comment
- Parent can add, remove, and reply comment in insight detail

##### Course

- Parent can see many list of course that exist in lingotalk
- Parent can see course detail

##### Progress Children

- Parent can see the list of linked children
- Parent can track their learning progress in certain courses
- Parent can children's profile, which is name, phone number, or their behaviour
- Parent can see the result of children's current session in certain course

##### Account Setting

- Parent can see and edit their profile, include the photo profile
- Parent can change their current password
- Parent can see the list of children who are want to be connected
- Parent can invite children to connect with them by children's referral code
- Parent can accept or decline the linking request from children and cancel the invitation request

## Resources
* UI/UX: https://www.figma.com/file/ee3YNx56mFkjWmgaWABydM/Grand-Design

## Teams
* Amin Mahendra
* Chaterina Maria Fransisca
* Fikran Akbar

## Screenshots

Home | Live Event
--- | ---
<img align="center" src="./app/src/main/res/drawable/home.gif" width="240"> | <img align="center" src="./app/src/main/res/drawable/live_event.gif" width="240">

Course | Progress Children | Account Setting
--- | --- | --- 
<img align="center" src="./app/src/main/res/drawable/course.gif" width="240"> | <img align="center" src="./app/src/main/res/drawable/progress_children.gif" width="240"> | <img align="center" src="./app/src/main/res/drawable/account_setting.gif" width="240"> 

Insight | Auth
--- | ---
<img align="center" src="./app/src/main/res/drawable/insight.gif" width="240"> | <img align="center" src="./app/src/main/res/drawable/authentication.gif" width="240">
