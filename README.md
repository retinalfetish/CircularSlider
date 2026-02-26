# CircularSlider
An elliptical slider for Android [Jetpack Compose](https://developer.android.com/compose) that is LTR / RTL aware. It supports configurable start and sweep angles, adjustable track/thumb thickness, custom colors, Material 3 interaction sources, and accessibility.

## Screenshots
<img src="/art/screenshot-default.gif" height=600 alt="Screenshot"> <img src="/art/screenshot-ellipse.gif" height=600 alt="Screenshot">

## Getting Started
The latest build is available via [JitPack](https://jitpack.io/#com.unary/circularslider). Simply add the repository to your `settings.gradle.kts` and the library dependency to your app `build.gradle.kts`.
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        // ...
        maven { url = uri("https://jitpack.io") }
    }
}
```
```
dependencies {
    // ...
    implementation("com.unary:circularslider:1.0.0")
}
```
## Usage
The slider can be a circle or an ellipse based on the given `Modifier` dimensions. It is left to the consumer to decide how to interpret the 0f to 1f value range and any animations derived from it.
```
var value by rememberSaveable { mutableFloatStateOf(value = 0f) }

CircularSlider(
    value = value,
    onValueChange = { value = it },
    modifier = Modifier.size(size = 280.dp),
    startAngle = 135f,
    sweepAngle = 270f,
    trackThickness = 4.dp,
    thumbRadius = 10.dp
)
```
An overloaded version that uses `CircularSliderState` through `rememberCircularSliderState` is also available for state hoisting.
```
val state = rememberCircularSliderState(initialValue = 0.5f)

CircularSlider(
    state = state,
    // ...
)
```
## Customization
In addition to styling with `CircularSliderDefaults.colors` in the standard Jetpack Compose pattern, you can animate the value change for spring, stepping, and gravity.
```
var value by rememberSaveable { mutableFloatStateOf(value = 0f) }

val animatedValue by animateFloatAsState(
    targetValue = value,
    animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
)

CircularSlider(
    value = animatedValue,
    onValueChange = { value = it },
    // ...
)
```
## License
This project is licensed under the Apache License 2.0. You may use, distribute, and modify this software under the terms of the license.
