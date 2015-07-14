SimpleUi
========

SimpleUI is a small library to generate out of the box usable and good looking UIs for Android. It follows the model view controller principle and you create a controller which can generate a working connected view for your model. It is possible to specify the appearance of the views but normally this is not necessary. The framework is designed to create usable UIs in a very fast and intuitive way using the advantages of auto completion and anonymous classes. 

Whenever there is something important new I will probably post it here: http://andrdev.blogspot.com/

##Screenshots

If you want to see the source code for each screenshot go to http://andrdev.blogspot.de/2015/01/simpleui-now-has-material-design.html and click on the images.

![](http://1.bp.blogspot.com/-4iaDhOgG5Es/VL1BF7vsB_I/AAAAAAAAu5E/EJHRkIzBw2I/w786-h715-no/m_imageview.jpg  "")

![](http://1.bp.blogspot.com/-DaOQ8IsWfyQ/VL1BGqMoiLI/AAAAAAAAu5Q/X-HNs2ASAuA/w786-h715-no/overviewIntro.jpg  "")

![](http://1.bp.blogspot.com/-iVXPSohHths/VL1BF-GMdTI/AAAAAAAAu5I/XcqPZaOkLo4/w786-h715-no/m_buttonAndHalfHalf.jpg  "")

![](http://4.bp.blogspot.com/-w9UrvjjRcIY/VL1BGNdu9hI/AAAAAAAAu5A/1zMTySBKALM/w786-h715-no/m_progressBar.jpg  "")

![](http://4.bp.blogspot.com/-zmRGYm1tqfI/VL1BExJzwjI/AAAAAAAAu40/P3WR2sxA7cU/w786-h715-no/containerTypes.jpg  "")

![](http://1.bp.blogspot.com/-4Cc0VXcxRdg/VL1BE4Xd3WI/AAAAAAAAu5M/qE8E_UjLOhQ/w786-h715-no/cardExamples.jpg  "")

##A code example

The following code snippet shows a basic example of a created controller which can interact with the user and which generates the UI shown in the screenshot below. Four controller elements are added to a composite container called M_Container. This container is then passed to SimpleUI activity to be displayed to the user. The modifiers like M_Checkbox and M_Button are abstract classes and use the template method pattern to pass down events like the onClick-event for the button or the save event for the checkbox.
 
The code for the controller:

![](https://lh4.googleusercontent.com/-lV5X50L-oJY/U8ZERJjRhLI/AAAAAAAAmz8/iD--Fu80caU/s2048/2014-07-16%25252011_21_56-Java%252520-%252520SimpleUiExamples_src_de_rwth_StartExampleUi.java%252520-%252520Eclipse.png)

This will generate the following fully functional UI:

![](http://3.bp.blogspot.com/-6xn_kTLpSMQ/VL1BE8pPTEI/AAAAAAAAu4o/Ball-Oic5m8/w786-h715-no/dialog.jpg  "")

#Details about the ideas behind SimpleUI
The SimpleUI component is a user interface generator based on the model view controller pattern. It was created for fast prototyping and to generate dynamic views based on the presented content. It is built in a modular way to allow including single independent components into an existing architecture, but it can also be used as a complete replacement for the activity system in Android. 

### Structure of the SimpleUi project

- src: Contains Android classes which do not have dependencies to any other library
- srcModifiers: Contains all code related to UI generation based on the MCV pattern, so all modifiers etc.
- srcJava: Contains pure Java classes to be used not only in Android but also Java projects
- srcJavaEE: Contains JavaEE related sources like a JaxRS Proxy
- srcAddons: Contains Android classes which relate to another library like Otto, Picasso, Butterknife etc.
- srcOtherAddons: Optional Android classes for some less frequently needed libraries


***

# Using SimpleUI in other Java projects
We moved all Android unrelated classes to a separate source folder: srcJava

You need to include the jsr311-api-1.1.1.jar in your Java project as well


***

 ![13](https://lh3.googleusercontent.com/-McqXOnZT8Ps/Uda8FJA-frI/AAAAAAAAXEU/sc8kyxsE9T0/w1082-h709-no/13.png)

(Figure 1)  UI generation with SimpleUI


***

The different modifiers represent best practice use cases and hide all the internal logic to simplify the development process. They wrap compositions of views and provide interfaces for logical interaction. The template method pattern is used to provide the fundamental methods to the developer and all modifiers follow the same lifecycle concept. Similar to web forms a modifier loads its content when it is displayed to the user and listens to a save event to push the changes back to the model when the user confirms the changes.

***

 ![14](https://lh3.googleusercontent.com/-PDVy9A-KPBE/Uda8Fhg7sII/AAAAAAAAXEY/37np33-BDxk/w1169-h605-no/14.png)

(Figure 2)	Example modifiers

***

### Error Handler

The error handler can be used to catch any type of exception to give the user a better feedback and the possibility to report an existing problem with a shipped application. The Error-Handler implements the UncaughtExceptionHandler-interface provided by Android and thus can be registered as the default uncaught exception handler. The same way crash reports are collected, users can also report problems manually. The error reports are sent via email to give the user the full control what is send and when it should be send.
It should be noted that the ErrorHandler-activity cannot be displayed by the same Android process that reported the crash since this process is in an undefined state. Therefore the error handler has to be started via an intent and handled like an external application. This is done by setting both the android:taskAffinity and android:process attributes to values different from the main activity (Google n.d.).


##Setup in Eclipse
1. Import the appcompat_v7 + appcompat_v7_cardview + SimpleUi projects ("Import existing Eclipse project") 
2. If you want to try the examples import "SimpleUiExamples" as well

##Screencast tutorials

<a href="http://www.youtube.com/watch?feature=player_embedded&v=PWwyYP0ck3Y
" target="_blank"><img src="http://img.youtube.com/vi/PWwyYP0ck3Y/0.jpg" 
alt="Link to the DroidAR video" width="240" height="180" border="10" /></a>
<a href="http://www.youtube.com/watch?feature=player_embedded&v=tMLi3OVEUCY
" target="_blank"><img src="http://img.youtube.com/vi/tMLi3OVEUCY/0.jpg" 
alt="Link to the DroidAR video" width="240" height="180" border="10" /></a>

<a href="http://www.youtube.com/watch?feature=player_embedded&v=hcZ8AHGL4Oc
" target="_blank"><img src="http://img.youtube.com/vi/hcZ8AHGL4Oc/0.jpg" 
alt="Link to the DroidAR video" width="240" height="180" border="10" /></a>
<a href="http://www.youtube.com/watch?feature=player_embedded&v=VEqCZdWmUnw
" target="_blank"><img src="http://img.youtube.com/vi/VEqCZdWmUnw/0.jpg" 
alt="Link to the DroidAR video" width="240" height="180" border="10" /></a>
