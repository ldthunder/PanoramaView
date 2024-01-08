# PanoramaView
Auto-scrolling View that adjusts to device rotation.

[![](https://jitpack.io/v/ldthunder/PanoramaView.svg)](https://jitpack.io/#ldthunder/PanoramaView)
## Including in Your Project 

### Step 1. Add the JitPack repository
In settings.gradle:
```kotlin
dependencyResolutionManagement {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
### Step 2. Add the dependency 
In build.gradle:
```groovy
implementation 'com.github.ldthunder:PanoramaView:1.0'
```

## PanoramaView in Layout File

```xml
<com.ldthunder.panorama_view.PanoramaView
        android:id="@+id/panorama_image_view"
        android:src="@drawable/img"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:enablePanoramaMode="true"
        app:invertScrollDirection="false"
        app:showScrollbar="true" />
```
## Description of Attributes:
|        Attributes         | Format  | Default |             Description             |
| :-----------------------: | :-----: | :-----: | :---------------------------------: |
|      enablePanoramaMode   | boolean |  true   |       Enable panorama effect        |
|         showScrollbar     | boolean |  false  |          Display scrollbar          |
|     invertScrollDirection | boolean |  false  |    Inverts the scroll direction     |

### All the attributes can also be set in code

```kotlin
panoramaView.setEnablePanoramaMode(true)
panoramaView.setEnableScrollbar(true)
panoramaView.setInvertScrollDirection(false)
```
## Register the GyroscopeObserver

### There are two ways to register an observer in your Activity or Fragment:

### 1. Using delegate:

```kotlin
class MainActivity : AppCompatActivity() {
    // Register and Unregister the observer with delegate function
    private val gyroscopeObserver by GyroscopeObserverDelegate(this, GyroscopeObserver())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val panoramaImageView = findViewById<View>(R.id.panorama_image_view) as PanoramaView
        // Adding Gyroscope Observer to the view
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver)

        // Changing the maximum rotation radian(also affects the rotation speed)
        gyroscopeObserver.setMaxRotateRadian(GyroscopeObserver.SLOW)
        // Saving position of the view upon unregistering the observer
        gyroscopeObserver.setPositionSaving(false)
    }
}
```
### 2. Manually:
```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ...
    }

    override fun onResume() {
        super.onResume()
        // Register listener
        gyroscopeObserver.register(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister listener
        gyroscopeObserver.unregister()
    }
}
```
## Observing Scroll State

To receive callbacks during image scrolling, assign an OnPanoramaScrollListener object to PanoramaView:

```kotlin
panoramaImageView.setOnPanoramaScrollListener(object : PanoramaView.OnPanoramaScrollListener {
    override fun onScrolled(view: PanoramaView?, offsetProgress: Float) {
        /* The offsetProgress range is from -1 to 1, indicating the scrolling of the image
        from left(top) to right(bottom) */
    }
})
```
## License
[MIT License][1]

[1]: https://github.com/ldthunder/PanoramaView/blob/True/LICENSE.txt

