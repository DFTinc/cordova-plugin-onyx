# Version 5.2.0
* Updated to use onyxCamera Cocoapod v5.3.0
* Changed OnyxMatch to use pyramidVerify()
* Added new fingerDetect property for OnyxConfiguration
* Removed shouldInvert
* Fixed bug with useFlash
* Added FLAG_ACTIVITY_NEW_TASK for starting OnyxActivity
* Changed default crop size and crop factor
* Removed FLIP enum
* Added IMAGE_ROTATION and FINGER_DETECT_MODE enums

 

# Version 5.1.1
## What's New
* Updated to use OnyxCamera Cocoapod 5.1.1
* Updated to use onyx-camera 5.1.2
* implemented `useManualCapture` on iOS
* Added capture screen customizations to match changes to OnyxCamera CocoaPod.
    * Added new configuration options
        * `backButtonText`
        * `showManualCaptureText`
        * `manualCaptureText`
        * `infoText`
        * `infoTextColorHexString`
        * `base64ImageData`
* Included ability to localize text for back button and manual capture text.
* Added ability to add custom message and image to capture screen.

# Version 5.0.2
## What's New
* Updated to onyx-camera:5.1.1
* Fixed issues with OnyxCallback
* Changed install location of `build-extras.gradle`
* Added `abiFilters` to `build-extras.gradle`
* Restart capture if `AUTOFOCUS_FAILURE` error is received.


# Version 5.0.1
## What's New
* Fixed bugs introduced by updating to onyx-camera:5.0.8 in build-extras.gradle

# Version 5.0.0
## What's New
* Updated to Onyx version 5.0
#### Breaking Changes
* Using the new OnyxCamera CocoaPod for iOS.
* Refactored `Onyx.Action` to `Onyx.ACTION`.
    * Removed actions `ENROLL`, `VERIFY`, `TEMPLATE`, and `IMAGE` and replaced with a single `CAPTURE` action.
    * Added on-device matching through use of the `MATCH` action.
* Brand new `OnyxOptions` for configuring the Onyx capture screen.
* Brand new `OnyxResult` object returned via `onyx.onSuccess` callback.



# Version 0.2.0
## What's New

* Added the ability to request the return of multiple image types
    * Changed `OnyxOptions.imageType` to `OnyxOptions.imageTypes`
        * Changed option argument from a single `Onyx.ImageType` to an `Array<Onyx.ImageType>`
    * Changed `OnyxResult.imageUri` to `OnyxResult.images`
        * `Onyx.ACTION.IMAGE` will now return a JSON Object
        ```
        {
            "raw": "base64EncodedImageUri",
            "preprocessed": "base64EncodedImageUri",
            "enhanced": "base64EncodedImageUri",
            "wsq": {
                "bytes": "base64EncodedBytes",
                "nfiqScore": number
            }
        }
        ```
    * Added `Onyx.ImageType.WSQ`
        * Option argument to request a base64 encoded WSQ image.

## Breaking Changes
 * Changed `OnyxOptions.imageType` to `OnyxOptions.imageTypes`
 * Changed `OnyxResult.imageUri` to `OnyxResult.images`