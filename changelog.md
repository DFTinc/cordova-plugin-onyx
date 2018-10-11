# Version 5.0.2
## What's New
* Updated to onyx-camera:5.1.1
* Fixed issues with OnyxCallback
* Changed install location of `build-extras.gradle`
* Added `abiFilters` to `build-extras.gradle`
* Restart capture iF `AUTOFOCUS_FAILURE` error is received.


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