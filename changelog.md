# Version 0.2.0
## What's New

* Added the ability to request the return of multiple image types
    * Changed `OnyxOptions.imageType` to `OnyxOptions.imageTypes`
        * Changed option argument from a single `Onyx.ImageType` to an `Array<Onyx.ImageType>`
    * Changed `OnyxResult.imageUri` to `OnyxResult.images`
        * `Onyx.Action.IMAGE` will now return a JSON Object
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