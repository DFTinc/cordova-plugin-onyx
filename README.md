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
    meteor add cordova:cordova-plugin-onyx
```

## Quick Use Example
####MeteorJS applications
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
    * [.onError](#module_onyx.onError) : <code>function</code>
    * [.onSuccess](#module_onyx.onSuccess) : <code>function</code>
    * [.OnyxOptions](#module_onyx.OnyxOptions) : <code>Object</code>


* [Onyx](#module_Onyx)
    * [.Action](#module_Onyx.ACTION) : <code>enum</code>

---

<a name="module_onyx"></a>
## onyx
<a name="module_onyx.exec"></a>
### onyx.exec(options, successCallback, errorCallback)
Executes onyx with the specified options.

__Supported Platforms__

- Android
- iOS

**Kind**: static method of <code>[onyx](#module_onyx)</code>  

| Param | Type | Description |
| --- | --- | --- |
| options | <code>[OnyxOptions](#module_onyx.OnyxOptions)</code> | Parameters telling onyx what to execute. |
| successCallback | <code>[onSuccess](#module_onyx.onSuccess)</code> |  |
| errorCallback | <code>[onError](#module_onyx.onError)</code> |  |

**Example**  

```
navigator.onyx.exec(onyxOptions, onyxSuccess, onyxError);
```

<a name="module_onyx.onError"></a>
### onyx.onError : <code>function</code>
Callback function that provides an error message.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  

| Param | Type | Description |
| --- | --- | --- |
| error | <code>Object</code> | An error object containing an error code and message. |

<a name="module_onyx.onSuccess"></a>
### onyx.onSuccess : <code>function</code>
Callback function that provides the image data.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  

| Param | Type | Description |
| --- | --- | --- |
| onyxResult | <code>[onyx](#module_onyx.OnyxResult)</code> | Object containing the results of the executed action. |

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
### Onyx.OnyxOptions : <code>Object</code>
Optional parameters to customize onyx configuration.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  
**Properties**

| Name | Type | Default | Platform | Description |
| --- | --- | --- | --- | --- |
| onyxLicense | <code>string</code> | <code>xxxx-xxxx-xxxx-x-x</code> | Both |  **Required**<br> Your license key for Onyx. |
| action | <code>[Action](#module_Onyx.ACTION)</code> | undefined | Both | **Required**<br> Choose the action for onyx to execute. |
| returnRawImage | <code>boolean</code> | <code>false</code> | Both | Indicates if the raw fingerprint image will be returned. | 
| returnGrayRawImage | <code>boolean</code> | <code>false</code> | iOS | Indicates if the gray raw fingerprint image will be returned.| 
| returnProcessedImage | <code>boolean</code> | <code>false</code> | Both | Indicates if the processed fingerprint image will be returned. | 
| returnEnhancedImage | <code>boolean</code> | <code>false</code> | Both | Indicates if the enhanced fingerprint image will be returned. | 
| returnBlackWhiteProcessedImage | <code>boolean</code> | <code>false</code> | iOS | Indicates if the black and white processed fingerprint image will be returned. | 
| returnFingerprintTemplate | <code>boolean</code> | <code>false</code> | Both | Indicates if the base64 encoded fingerprint template will be returned. | 
| returnWSQ | <code>boolean</code> | <code>false</code> | Both | Indicates if the base64 encoded WSQ fingerprint will be returned. | 
| returnGrayRawWSQ | <code>boolean</code> | <code>false</code> | iOS | Indicates if the base64 encoded gray raw WSQ fingerprint will be returned. |
| shouldSegment | <code>boolean</code> | <code>false</code> | Android | Indicates if the fingerprint image should be segmented to remove background noise from the fingerprint image.| 
| shouldConvertToISOTemplate | <code>boolean</code> | <code>false</code> | Android | Indicates that the fingerprint template should be returned in ISO format.|
| imageRotation | <code>number</code> | 0 | Android | Integer value of 90 degrees (0, 90, 180, or 270). |
| wholeFingerCrop | <code>boolean</code> | <code>false</code> | Android | Will crop less of the image to return more of the fingerprint. | 
| useManualCapture | <code>boolean</code> | <code>false</code> | Android | Disables the auto-capture feature.  User must tap the screen to trigger capture.| 
| useOnyxLive | <code>boolean</code> | <code>false</code> | Both | Will send the raw fingerprint image to the Onyx Liveness service to receive a liveness confidence score.| 
| useFlash | <code>boolean</code> | <code>true</code> | Both | Sets the flash to torch mode to illuminate the finger and help determine alignment with the reticle.  | 
| showLoadingSpinner | <code>boolean</code> | <code>false</code> | Both | Shows a loading spinner while Onyx is doing asynchronous requests before the capture screen is presented.| 
| shouldInvert | <code>boolean</code> | <code>true</code> | iOS | Inverts the colors (bitwise) of the processed fingerprint images to make the valleys and ridges the same color as a touch based sensor. | 
| reticleOrientation | <code>string</code> | <code>[RETICLE_ORIENTATION.LEFT](#module_Onyx.RETICLE_ORIENTATION)</code> | Both | Sets the direction of the finger reticle on the capture screen. |
| reticleAngle | <code>number</code> | 0 | Android | Requires [LAYOUT_PREFERENCE.FULL](#module_Onyx.LAYOUT_PREFERENCE)<br>Integer value.<br>Adjusts the angle of the finger reticle. |
| reticleScale | <code>number</code> | 1.0 | Android | Float value from 0 to 1.<br>Adjusts the scale of the finger reticle. |
| backgroundColorHexString | <code>string</code> | "#3698D3" | Both | Hex color value for the background color of the bottom two-thirds of the capture screen.|
| showBackButton | <code>boolean</code> | <code>false</code> | Android | Displays a back button on the capture screen.| 
| cropFactor | <code>float</code> | <strong>Android</strong> 0.8<br><strong>iOS</strong> 0.9| Both | Float value from 0 to 1.<br> Adjusts the crop factor for the fingerprint image.|
| cropSize | <code>JSON Object</code><br><br>{<br>width: <code>number</code>,<br> height: <code>number</code><br>} | <strong>Android</strong><br>width: 512<br>height: 300<br><br><strong>iOS</strong><br>width: 600<br>height: 960 | Both | Sets the dimensions of the fingerprint image. |
| layoutPreference | <code>string</code> | <code>[LAYOUT_PREFERENCE.UPPER_THIRD](#module_Onyx.LAYOUT_PREFERENCE)</code> | Both | Sets the display of the camera view.|
| flip | <code>string</code> | <code>[FLIP.HORIZONTAL](#module_Onyx.FLIP)</code> | Both | Flips the orientation of the fingerprint image. |
| probe | <code>string</code> | <code>undefined</code> | Both | **Required for `Onyx.ACTION.MATCH`**<br>Base64 encoded fingerprint template to match against a reference fingerprint template. |
| reference | <code>string</code> | <code>undefined</code> | Both | **Required for `Onyx.ACTION.MATCH`**<br>Base64 encoded fingerprint template to match against a probe fingerprint template. |

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
    wholeFingerCrop?: boolean,
    useManualCapture?: boolean,
    useOnyxLive?: boolean,
    useFlash?: boolean,
    showLoadingSpinner?: boolean,
    shouldInvert?: boolean,
    reticleOrientation?: string,
    reticleAngle?: number,
    reticleScale?: number,
    backgroundColorHexString?: string,
    showBackButton?: boolean,
    cropFactor?: number,
    cropSize?: { width?: number, height?: number },
    layoutPreference?: string,
    flip?: string,
    probe?: string,
    reference?: string
}
```

<a name="module_Onyx.ACTION"></a>
### Onyx.ACTION : <code>JSON Object</code>
**Kind**: static constants for actions of <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| CAPTURE | <code>string</code> | "capture" | Launches the Onyx Camera to capture a fingerprint and return an <code>[OnyxResult](#module_onyx.OnyxResult)</code> object. **Requires** `OnyxOptions.onyxLicense`|
| MATCH | <code>string</code> | "match" | Performs on-device matching of the probe and reference fingerprint templates. **Requires** `OnyxOptions.reference` and `OnyxOptions.probe` |


<a name="module_Onyx.RETICLE_ORIENTATION"></a>
### Onyx.RETICLE_ORIENTATION : <code>JSON Object</code>
**Kind**: static constants for reticleOrientation of <code>[OnyxOptions](#module_Onyx.OnyxOptions)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| LEFT | <code>string</code> | "LEFT"| Finger reticle on the capture screen will be oriented for a left hand capture. |
| RIGHT | <code>string</code> | "RIGHT"| Finger reticle on the capture screen will be oriented for a right hand capture. |


<a name="module_Onyx.LAYOUT_PREFERENCE"></a>
### Onyx.LAYOUT_PREFERENCE : <code>JSON Object</code>
**Kind**: static constants for layoutPreference of <code>[OnyxOptions](#module_Onyx.OnyxOptions)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| UPPER_THRID | <code>string</code> | "UPPER_THRID" | The camera view will only occupy the top third of the capture screen. |
| FULL | <code>string</code> | "FULL" | The camera view will be full screen. |


<a name="module_Onyx.FLIP"></a>
### Onyx.FLIP : <code>JSON Object</code>
**Kind**: static constants for flip of <code>[OnyxOptions](#module_Onyx.OnyxOptions)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| HORIZONTAL | <code>string</code> | "HORIZONTAL"| Flips the fingerprint image horizontally so that it is oriented as a touch based sensor. |
| VERTICAL | <code>string</code> | "VERTICAL"| Flips the fingerprint image vertically. |
| BOTH | <code>string</code> | "BOTH"| Flips the fingerprint image horizontally and vertically. |
| NONE | <code>string</code> | "NONE"| Leaves the fingerprint image as is captured by the camera. |

<a name="module_Onyx.OnyxResult"></a>
### Onyx.OnyxResult : <code>JSON Object</code>
Results return by Onyx.

**Kind**: Return type of  <code>[onyx.onSuccess](#module_onyx.onSuccess)</code>  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| action | <code>string</code> | The [Onyx.ACTION](#module_Onyx.ACTION) that was specified in the [OnyxOptions](module_Onyx.OnyxOptions) |
| rawFingerprintDataUri | <code>string</code> | DataUri prefixed, base64 encoded string representation of the raw fingerprint image. |
| grayRawFingerprintDataUri | <code>string</code> | DataUri prefixed, base64 encoded string representation of the gray raw fingerprint image. |
| processedFingerprintDataUri | <code>string</code> | DataUri prefixed, base64 encoded string representation of the processed fingerprint image. |
| enhancedFingerprintDataUri | <code>string</code> | DataUri prefixed, base64 encoded string representation of the enhanced fingerprint image. |
| blackWhiteProcessedFingerprintDataUri | <code>string</code> | DataUri prefixed, base64 encoded string representation of the black and white processed fingerprint image. |
| base64EncodedWsqBytes | <code>string</code> | Base64 encoded bytes of the WSQ fingerprint image. |
| base64EncodedGrayRawWsqBytes | <code>string</code> | Base64 encoded bytes of the gray raw WSQ fingerprint image. |
| base64EncodedFingerprintTemplate | <code>string</code> | Base64 encoded bytes of the fingerprint template. |
| captureMetrics | <code>[Onyx.CaptureMetrics](#module_Onyx.CaptureMetrics)</code> | JSON Object containing the capture metrics. |
| isVerified | <code>boolean</code> | Indicates if the proble fingerprint template matched the reference fingerprint template |
| matchScore | <code>number</code> | Match score of the compared probe and reference fingerprint templates. |

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
### Onyx.CaptureMetrics : <code>JSON Object</code>
**Kind**: static typedef for <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| nfiqMetrics | <code>[Onyx.NfiqMetrics](#module_Onyx.NfiqMetrics)</code> | JSON Object containing the NFIQ Metrics of `nfiqScore` and `mlpScore`. |
| livenessConfidence | <code>number</code> | Float value 0 - 1 indicating the percent confidence that the image captured was a real live fingerprint. Greater than 0.5 (50%) would suggest the finger was more real than fake.  Less than 0.5 (50%) would suggest the finger was more fake than real. |
| focusQuality | <code>number</code> |  |
| distanceToCenter | <code>number</code> |  |
| fillProperties | <code>[Onyx.FillProperties](#module_Onyx.FillProperties)</code> | JSON Object containg the fill properties of `heightRatio` and `overlapRatio`. |

<a name="module_Onyx.NfiqMetrics"></a>
### Onyx.NfiqMetrics : <code>JSON Object</code>
**Kind**: static typedef for <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| nfiqScore | <code>number</code> | NIST Fingerprint Image Quality. Integer value 1 - 5.  A value of 5 being the lowest quality fingerprint image and should not be used for matching or saved as a biometric enrollment. |
| mlpScore | <code>number</code> | Multi-Layer Perceptrons (MLP) Score |

<a name="module_Onyx.FillProperties"></a>
### Onyx.FillProperties : <code>JSON Object</code>
**Kind**: static typedef for <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Description |
| --- | --- | --- |
| heightRatio | <code>number</code> |  |
| overlapRatio | <code>number</code> |  |