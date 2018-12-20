### This is not free software.  This plugin uses the Onyx sofware development kits (SDKs) for Android and iOS.  It requires a license agreement with: 
[Diamond Fortress Technologies, Inc.](http://www.diamondfortress.com)

# cordova-plugin-onyx

This plugin defines a global `navigator.onyx` object, which provides an API for capturing a fingerprint image, for in-app fingerprint enrollment and verification, and for fingerprint template generation for server side fingerprint authentication.

Although the object is attached to the global scoped `navigator`, it is not available until after the `deviceready` event.

    document.addEventListener("deviceready", onDeviceReady, false);
    function onDeviceReady() {
        console.log(navigator.onyx);
    }


## Installation
Contact [Diamond Fortress Technologies, Inc.](http://www.diamondfortress.com) and sign a license agreement to obtain a license key.

This requires cordova 5.0+

```
cordova plugin add cordova-plugin-onyx
```
```
meteor add cordova:cordova-plugin-onyx@5.0.0
```

### Additonal steps for iOS
Run the app for a device to build the app.
From your meteor project directory
```
meteor run ios-device
```
 **Install the CocoaPod dependencies**
 Open a terminal and navigate to the folder containing the project that was just created.
 From your meteor project directory
 
 ```
 cd .meteor/local/cordova-build/platforms/ios/
 
 pod install
 ```

Then open the project `.xcworkspace` file to run the app on an iOS device from XCode.


## Quick Use Example
#### MeteorJS applications
Create a settings.json file to load at runtime.

```
{
    "public": {
        "onyxLicense": "xxxx-xxxx-xxxx-x-x"
    }
}
```

Reference the Onyx license when you execute an Onyx action.

```
var onyxOptions = {
    onyxLicense = Meteor.settings.public.onyxLicense;
    action = Onyx.ACTION.CAPTURE;
}
navigator.onyx.exec(onyxOptions, onyxSuccessCallback, onyxErrorCallback);
```

Run the application on a mobile device

```
meteor run android-device --settings settings.json
```


# API Reference


* [onyx](#module_onyx)
    * [.exec(options, successCallback, errorCallback)](#module_onyx.exec)
    * [.onError](#module_onyx.onError) : `Function`
    * [.onSuccess](#module_onyx.onSuccess) : `Function`
    

* [Onyx](#module_Onyx)
    * [.OnyxOptions](#module_Onyx.OnyxOptions) : `JSON Object`
    * [.ACTION](#module_Onyx.ACTION) : `Enum`
    * [.RETICLE_ORIENTATION](#module_Onyx.RETICLE_ORIENTATION) : `Enum`
    * [.LAYOUT_PREFERENCE](#module_Onyx.LAYOUT_PREFERENCE) : `Enum`
    * [.IMAGE_ROTATION](#module_Onyx.IMAGE_ROTATION) : `Enum`
    * [.FINGER_DETECT_MODE](#module_Onyx.FINGER_DETECT_MODE) : `Enum`
    * [.OnyxResult](#module_Onyx.OnyxResult) : `JSON Object`
    * [.CaptureMetrics](#module_Onyx.CaptureMetrics) : `JSON Object`
    * [.NfiqMetrics](#module_Onyx.NfiqMetrics) : `JSON Object`
    * [.FillProperties](#module_Onyx.FillProperties) : `JSON Object`

---

<a name="module_onyx"></a>
## onyx
<a name="module_onyx.exec"></a>
### onyx.exec(options, successCallback, errorCallback)
Executes onyx with the specified options.

__Supported Platforms__

- Android
- iOS

**Kind**: static method of `[onyx](#module_onyx)`  

| Param | Type | Description |
| --- | --- | --- |
| options | `[OnyxOptions](#module_onyx.OnyxOptions)` | Parameters telling onyx what to execute. |
| successCallback | `[onSuccess](#module_onyx.onSuccess)` |  |
| errorCallback | `[onError](#module_onyx.onError)` |  |

**Example**  

```
navigator.onyx.exec(onyxOptions, onyxSuccess, onyxError);
```

<a name="module_onyx.onError"></a>
### onyx.onError : `function`
Callback function that provides an error message.

**Kind**: static typedef of `[onyx](#module_onyx)`  

| Param | Type | Description |
| --- | --- | --- |
| error | `Object` | An error object containing an error code and message. |

<a name="module_onyx.onSuccess"></a>
### onyx.onSuccess : `function`
Callback function that provides the image data.

**Kind**: static typedef of `[onyx](#module_onyx)`  

| Param | Type | Description |
| --- | --- | --- |
| onyxResult | `[onyx](#module_onyx.OnyxResult)` | Object containing the results of the executed action. |

**Example**  

```
// Display an image of the processed fingerprint
function onyxSuccess(onyxResult) {
    if (onyxResult.action === Onyx.ACTION.CAPTURE) {
        var image = document.getElementById('myImage');
           image.src = onyxResult.processedFingerprintDataUri;    
    }
}
```

<a name="module_Onyx"></a>
## Onyx

<a name="module_Onyx.OnyxOptions"></a>
### Onyx.OnyxOptions : `JSON Object`
Optional parameters to customize onyx configuration.

**Kind**: static typedef of `[onyx](#module_onyx)`  
**Properties**

| Name | Type | Default | Platform | Description |
| --- | --- | --- | --- | --- |
| onyxLicense | `string` | `xxxx-xxxx-xxxx-x-x` | Both |  **Required**<br> Your license key for Onyx. |
| action | `[Action](#module_Onyx.ACTION)` | `undefined` | Both | **Required**<br> Choose the action for onyx to execute. |
| returnRawImage | `boolean` | `false` | Both | Indicates if the raw fingerprint image will be returned. | 
| returnGrayRawImage | `boolean` | `false` | iOS | Indicates if the gray raw fingerprint image will be returned.| 
| returnProcessedImage | `boolean` | `false` | Both | Indicates if the processed fingerprint image will be returned. | 
| returnEnhancedImage | `boolean` | `false` | Both | Indicates if the enhanced fingerprint image will be returned. | 
| returnBlackWhiteProcessedImage | `boolean` | `false` | iOS | Indicates if the black and white processed fingerprint image will be returned. | 
| returnFingerprintTemplate | `boolean` | `false` | Both | Indicates if the base64 encoded fingerprint template will be returned. | 
| returnWSQ | `boolean` | `false` | Both | Indicates if the base64 encoded WSQ fingerprint will be returned. | 
| returnGrayRawWSQ | `boolean` | `false` | iOS | Indicates if the base64 encoded gray raw WSQ fingerprint will be returned. |
| shouldSegment | `boolean` | `false` | Android | Indicates if the fingerprint image should be segmented to remove background noise from the fingerprint image.| 
| shouldConvertToISOTemplate | `boolean` | `false` | Android | Indicates that the fingerprint template should be returned in ISO format.|
| imageRotation | `number` | [IMAGE_ROTATION.NONE](#module_Onyx.IMAGE_ROTATION) | Both | Integer value of 90 degrees (0, 90, 180, or 270) to rotate the fingerprint image output. |
| fingerDetectMode | `number` | [FINGER_DETECT_MODE.LIVE_FINGER](#module_Onyx.FINGER_DETECT_MODE) | Both | Integer value of 0 or 1.  Determines threshold level of finger coloration for autocapture. |
| wholeFingerCrop | `boolean` | `false` | Android | Will crop less of the image to return more of the fingerprint. | 
| useManualCapture | `boolean` | `false` | Both | Disables the auto-capture feature.  User must tap the screen to trigger capture.| 
| useOnyxLive | `boolean` | `false` | Both | Will send the raw fingerprint image to the Onyx Liveness service to receive a liveness confidence score.| 
| useFlash | `boolean` | `true` | Both | Sets the flash to torch mode to illuminate the finger and help determine alignment with the reticle.  | 
| showLoadingSpinner | `boolean` | `false` | Both | Shows a loading spinner while Onyx is doing asynchronous requests before the capture screen is presented.| 
| reticleOrientation | `string` | [RETICLE_ORIENTATION.LEFT](#module_Onyx.RETICLE_ORIENTATION) | Both | Sets the direction of the finger reticle on the capture screen. |
| reticleAngle | `number` | `0` | Android | Requires [LAYOUT_PREFERENCE.FULL](#module_Onyx.LAYOUT_PREFERENCE)<br>Integer value.<br>Adjusts the angle of the finger reticle. |
| reticleScale | `number` | `1.0` | Android | Float value from 0 to 1.<br>Adjusts the scale of the finger reticle. |
| backgroundColorHexString | `string` | `#3698D3` | Both | Hex color value for the background color of the bottom two-thirds of the capture screen.|
| showBackButton | `boolean` | `false` | Android | Displays a back button on the capture screen.| 
| backButtonText | `string` | `"Back"` | Both | Set text to use for back button (localization).|
| showManualCaptureText | `boolean` | `true` | Both | Show or hide the manual capture text.|
| manualCaptureText | `string` | `"Touch the screen to capture."` | Both | Set custom text to use for manual capture (localization). |
| infoText | `string` | `undefined` | Both | Set custom text to display on the capture screen.|
| infoTextColorHexString | `string` | `undefined` | Both | Set the color of the text to display on the capture screen.  Defaults to `black` or `white` depending on the tint of `backgroundColorHexString` |
| base64ImageData | `string` | `undefined` | Both | Base64 encoded data string of image to display on the capture screen. |
| cropFactor | `float` | `0.8`| Both | Adjusts the zoom level of the fingerprint image. Higher numbers increase the zoom level.|
| cropSize | `JSON Object`<br><br>{<br>width: `number`,<br> height: `number`<br>} | width: `512`<br>height: `300` | Both | Sets the dimensions of the fingerprint image. |
| layoutPreference | `string` | [LAYOUT_PREFERENCE.UPPER_THIRD](#module_Onyx.LAYOUT_PREFERENCE) | Both | Sets the display of the camera view.|
| probe | `string` | `undefined` | Both | **Required for `Onyx.ACTION.MATCH`**<br>Base64 encoded **WSQ data** to match against a reference fingerprint template. |
| reference | `string` | `undefined` | Both | **Required for `Onyx.ACTION.MATCH`**<br>Base64 encoded **fingerprint template** to match against a probe fingerprint template. |
| pyramidScales | `Array<string>` | `["0.8", "1.0", "1.2"]` | Both | **Optional for `Onyx.ACTION.MATCH`**<br>Array of string representations of float values for image pyramiding during matching. |

**Example Interface**

```angular2html
export interface IOnyxConfiguration {
  action?: string,
  onyxLicense?: string,
  returnRawImage?: boolean,
  returnGrayRawImage?: boolean,
  returnProcessedImage?: boolean,
  returnEnhancedImage?: boolean,
  returnBlackWhiteProcessedImage?: boolean,
  returnFingerprintTemplate?: boolean,
  returnWSQ?: boolean,
  returnGrayRawWSQ?: boolean,
  shouldSegment?: boolean,
  shouldConvertToISOTemplate?: boolean,
  imageRotation?: number,
  fingerDetectMode?: number,
  wholeFingerCrop?: boolean,
  useManualCapture?: boolean,
  useOnyxLive?: boolean,
  useFlash?: boolean,
  showLoadingSpinner?: boolean,
  reticleOrientation?: string,
  reticleAngle?: number,
  reticleScale?: number,
  backgroundColorHexString?: string,
  showBackButton?: boolean,
  showManualCaptureText?: boolean,
  manualCaptureText?: string,
  backButtonText?: string,
  infoText?: string,
  infoTextColorHexString?: string,
  base64ImageData?: string,
  cropFactor?: number,
  cropSize?: { width?: number, height?: number },
  layoutPreference?: string,
  probe?: string,
  reference?: string,
  pyramidScales?: Array<string>
}
```

<a name="module_Onyx.ACTION"></a>
### Onyx.ACTION : `Enum`
**Kind**: static constants for actions of `[Onyx](#module_Onyx)`  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| CAPTURE | `string` | "capture" | Launches the Onyx Camera to capture a fingerprint and return an `[OnyxResult](#module_onyx.OnyxResult)` object. **Requires** `OnyxOptions.onyxLicense`|
| MATCH | `string` | "match" | Performs on-device matching of the probe and reference fingerprint templates. **Requires** `OnyxOptions.reference` and `OnyxOptions.probe` |


<a name="module_Onyx.RETICLE_ORIENTATION"></a>
### Onyx.RETICLE_ORIENTATION : `Enum`
**Kind**: static constants for reticleOrientation of `[OnyxOptions](#module_Onyx.OnyxOptions)`  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| LEFT | `string` | "LEFT"| **default**<br>Finger reticle on the capture screen will be oriented for a left hand capture. |
| RIGHT | `string` | "RIGHT"| Finger reticle on the capture screen will be oriented for a right hand capture. |


<a name="module_Onyx.LAYOUT_PREFERENCE"></a>
### Onyx.LAYOUT_PREFERENCE : `Enum`
**Kind**: static constants for layoutPreference of `[OnyxOptions](#module_Onyx.OnyxOptions)`  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| UPPER_THRID | `string` | "UPPER_THRID" | The camera view will only occupy the top third of the capture screen. |
| FULL | `string` | "FULL" | The camera view will be full screen. |


<a name="module_Onyx.IMAGE_ROTATION"></a>
### Onyx.IMAGE_ROTATION : `Enum`
**Kind**: static constants for `imageRotation `of `[OnyxOptions](#module_Onyx.OnyxOptions)`  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| ROTATE_NONE | `number` | `0`| **Default** Does not rotate the image. |
| ROTATE_90_COUNTER_CLOCKWISE | `number` | `90`| Rotates the image 90 degrees counter clockwise. |
| ROTATE_180 | `number` | `180`| Rotates the image 180 degrees. |
| ROTATE_90_CLOCKWISE | `number` | `270` | Rotates the image 90 degrees clockwise (270 degrees counter clockwise). |

<a name="module_Onyx.FINGER_DETECT_MODE"></a>
### Onyx.IMAGE_ROTATION : `Enum`
**Kind**: static constants for `fingerDetectMode `of `[OnyxOptions](#module_Onyx.OnyxOptions)`  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| DEAD_FINGER | `number` | 0 | Color threshold for dead fingers. |
| LIVE_FINGER | `number` | 1 | **default** <br>Color threshold for live fingers. |

<a name="module_Onyx.OnyxResult"></a>
### Onyx.OnyxResult : `JSON Object`
Results return by Onyx.

**Kind**: Return type of  `[onyx.onSuccess](#module_onyx.onSuccess)`  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| action | `string` | The [Onyx.ACTION](#module_Onyx.ACTION) that was specified in the [OnyxOptions](module_Onyx.OnyxOptions) |
| rawFingerprintDataUri | `string` | DataUri prefixed, base64 encoded string representation of the raw fingerprint image. |
| grayRawFingerprintDataUri | `string` | DataUri prefixed, base64 encoded string representation of the gray raw fingerprint image. |
| processedFingerprintDataUri | `string` | DataUri prefixed, base64 encoded string representation of the processed fingerprint image. |
| enhancedFingerprintDataUri | `string` | DataUri prefixed, base64 encoded string representation of the enhanced fingerprint image. |
| blackWhiteProcessedFingerprintDataUri | `string` | DataUri prefixed, base64 encoded string representation of the black and white processed fingerprint image. |
| base64EncodedWsqBytes | `string` | Base64 encoded bytes of the WSQ fingerprint image. |
| base64EncodedGrayRawWsqBytes | `string` | Base64 encoded bytes of the gray raw WSQ fingerprint image. |
| base64EncodedFingerprintTemplate | `string` | Base64 encoded bytes of the fingerprint template. |
| captureMetrics | `[Onyx.CaptureMetrics](#module_Onyx.CaptureMetrics)` | JSON Object containing the capture metrics. |
| isVerified | `boolean` | Indicates if the proble fingerprint template matched the reference fingerprint template |
| matchScore | `number` | Match score of the compared probe and reference fingerprint templates. |

**Example Interface**

```angular2html
export interface IOnyxResult {
    action?: string,
    rawFingerprintDataUri?: string,
    grayRawFingerprintDataUri?: string,
    processedFingerprintDataUri?: string,
    enhancedFingerprintDataUri?: string,
    blackWhiteProcessedFingerprintDataUri?: string,
    base64EncodedFingerprintTemplate?: string,
    base64EncodedWsqBytes?: string,
    base64EncodedGrayRawWsqBytes?: string,
    captureMetrics?: {
        nfiqMetrics?: {
            nfiqScore?: number,
            mlpScore?: number
        },
        livenessConfidence?: number,
        focusQuality?: number,
        distanceToCenter?: number,
        fillProperties?: {
            heightRatio?: number,
            overlapRatio?: number
        }
    },
    isVerified?: boolean,
    matchScore?: number
}
```

<a name="module_Onyx.CaptureMetrics"></a>
### Onyx.CaptureMetrics : `JSON Object`
**Kind**: static typedef for `[Onyx](#module_Onyx)`  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| nfiqMetrics | `[Onyx.NfiqMetrics](#module_Onyx.NfiqMetrics)` | JSON Object containing the NFIQ Metrics of `nfiqScore` and `mlpScore`. |
| livenessConfidence | `number` | Float value 0 - 1 indicating the percent confidence that the image captured was a real live fingerprint. Greater than 0.5 (50%) would suggest the finger was more real than fake.  Less than 0.5 (50%) would suggest the finger was more fake than real. |
| focusQuality | `number` |  |
| distanceToCenter | `number` |  |
| fillProperties | `[Onyx.FillProperties](#module_Onyx.FillProperties)` | **Android only**<br>JSON Object containg the fill properties of `heightRatio` and `overlapRatio`. |

<a name="module_Onyx.NfiqMetrics"></a>
### Onyx.NfiqMetrics : `JSON Object`
**Kind**: static typedef for `[Onyx](#module_Onyx)`  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| nfiqScore | `number` | NIST Fingerprint Image Quality. Integer value 1 - 5.  A value of 5 being the lowest quality fingerprint image and should not be used for matching or saved as a biometric enrollment. |
| mlpScore | `number` | Multi-Layer Perceptrons (MLP) Score is a float value from 0 - 1 describing the percent certainty of the NFIQ Score.|

<a name="module_Onyx.FillProperties"></a>
### Onyx.FillProperties : `JSON Object`
**Kind**: static typedef for `[Onyx](#module_Onyx)`  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| heightRatio | `number` |  |
| overlapRatio | `number` |  |