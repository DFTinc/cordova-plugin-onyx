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
    action = Onyx.ACTION.IMAGE;
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
    * [.Actions](#module_Onyx.ACTIONs) : <code>enum</code>

---

<a name="module_onyx"></a>
## onyx
<a name="module_onyx.exec"></a>
### onyx.exec(options, successCallback, errorCallback)
Executes onyx with the specified options.

__Supported Platforms__

- Android

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
| message | <code>string</code> | The message is provided by the device's native code. |

<a name="module_onyx.onSuccess"></a>
### onyx.onSuccess : <code>function</code>
Callback function that provides the image data.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  

| Param | Type | Description |
| --- | --- | --- |
| result | <code>[onyx](#module_onyx.OnyxResult)</code> | Object containing the results of the executed action. |

**Example**  

```
// Show image
//
function onyxSuccess(result) {
    if (result.action === Onyx.ACTION.IMAGE) {
        var image = document.getElementById('myImage');
           image.src = result.imageUri;    
    }
}
```
<a name="module_onyx.OnyxOptions"></a>
### onyx.OnyxOptions : <code>Object</code>
Optional parameters to customize onyx settings.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  
**Properties**

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| onyxLicense | <code>number</code> | <code>xxxx-xxxx-xxxx-x-x</code> | **Required** Your license key for Onyx. |
| action | <code>[Action](#module_Onyx.ACTION)</code> | undefined | **Required** Choose the action for onyx to execute. |
| imageTypes | <code>Array<[ImageType](#module_Onyx.ImageType)></code> | <code>[[ImageType](#module_Onyx.ImageType).PREPROCESSED]</code> | Array of image types to return as a base64 encoded data URI's. |

### onyx.OnyxResult : <code>JSON Object</code>
Results return by Onyx.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  
**Properties**

| Name | Type | Action | Description |
| --- | --- | --- | --- |
| images | <code>JSON Object</code> | <code>[Onyx.ACTION.IMAGE](#module_Onyx.ACTION)</code> | JSON Object containing base64 encoded fingerprint JPEG images.  `"data:image/jpeg;base64," + base64EncodedString`. <br><br> Defaults to imageType: `Onyx.ImageType.PREPROCESSED`<br><br> { <br>&nbsp;&nbsp;&nbsp;&nbsp;"raw": "base64EncodedImageUri",<br>&nbsp;&nbsp;&nbsp;&nbsp;"preprocessed": "base64EncodedImageUri",<br>&nbsp;&nbsp;&nbsp;&nbsp;"enhanced": "base64EncodedImageUri",<br>&nbsp;&nbsp;&nbsp;&nbsp;"wsq": { <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"bytes": "base64EncodedBytes", <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"nfiqScore": number<br>&nbsp;&nbsp;&nbsp;&nbsp;}<br>} |
| template | <code>string</code> | <code>[Onyx.ACTION.TEMPLATE](#module_Onyx.ACTION)</code> <code>[Onyx.ACTION.ENROLL](#module_Onyx.ACTION)</code> | Base64 encoded fingerprint template to send to backend server. |
| nfiqScore | <code>number</code> | <code>[Onyx.ACTION.VERIFY](#module_Onyx.ACTION)</code> | The result score of a verify action. |
| isVerified | <code>boolean</code> | <code>[Onyx.ACTION.VERIFY](#module_Onyx.ACTION)</code> | The fingerprint match result. |


---

<a name="module_Onyx"></a>
## Onyx
<a name="module_Onyx.ACTION"></a>
### Onyx.ACTION : <code>JSON Object</code>
**Kind**: static constants for actions of <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| IMAGE | <code>string</code> | <code>image</code> | Return a base64 encoded fingerprint image URI. |
| TEMPLATE | <code>string</code> | <code>template</code> | Return a base64 encoded fingerprint template |
| ENROLL | <code>string</code> | <code>enroll</code> | Enroll a fingerprint for your application to be stored on the local device and return the base64 encoded fingerprint template. |
| VERIFY | <code>string</code> | <code>verify</code> | Performs on device verification against the enrolled fingerprint. |

<a name="module_Onyx.ImageType"></a>
### Onyx.ImageType : <code>JSON Object</code>
**Kind**: static constants for image types of <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Value | Description |
| --- | --- | --- | --- |
| RAW | <code>string</code> | <code>raw</code> | Return the raw fingerprint image as a base64 encoded data URI. |
| PREPROCESSED | <code>string</code> | <code>preprocessed</code> | Return the preprocessed fingerprint image as a base64 encoded data URI. |
| ENHANCED | <code>string</code> | <code>enhanced</code> | Return the enhanced fingerprint image as a base64 encoded data URI. |
| WSQ | <code>JSON Object</code> | <code>wsq</code> | Return a JSON Object containing the WSQ Image data.<br><br> `{ wsq: { bytes: base64EncodedString, nfiqScore: number } }` |

**Example**  

```
var onyxOptions = {
            onyxLicense: MY_ONYX_LICENSE,
            action: Onyx.ACTION.IMAGE,
            imageTypes: [Onyx.ImageType.ENHANCED, Onyx.ImageType.WSQ]
        }
navigator.onyx.exec(onyxOptions, onyxSuccess, onyxError);

// TypeScript
private onyxSuccess(result) {
    var self = this;
    console.log("successCallback(): " + JSON.stringify(result));
    console.log("action: " + result.action);
    switch (result.action) {
        case Onyx.ACTION.IMAGE:
            if (result.hasOwnProperty("images")) {
                var images:any = result.images;
                var preprocessedImageUri:string;
                var rawImageUri:string;
                var enhancedImageUri:string;

                if (images.hasOwnProperty(Onyx.ImageType.RAW)) {
                    console.log("images contains raw image URI");
                    rawImageUri = images[Onyx.ImageType.RAW];
                }
                if (images.hasOwnProperty(Onyx.ImageType.PREPROCESSED)) {
                    console.log("images contains preprocessed image URI");
                    preprocessedImageUri = images[Onyx.ImageType.PREPROCESSED];
                }
                if (images.hasOwnProperty(Onyx.ImageType.ENHANCED)) {
                    console.log("images contains enhanced image URI");
                    enhancedImageUri = images[Onyx.ImageType.ENHANCED];
                }
                if (images.hasOwnProperty(Onyx.ImageType.WSQ)) {
                    console.log("images contains WSQ image");
                    var wsqImage:any = images[Onyx.ImageType.WSQ];
                    if (wsqImage) {
                        console.log("wsqImageNfiqScore: " + wsqImage.nfiqScore);
                        if (wsqImage.nfiqScore > 0 && wsqImage.nfiqScore < 5) {
                            // Do something with the WSQ image
                        } else {
                            // Show dialog indicating "Poor Image Quality"
                        }
                    }
                }
                Session.set("imageUri", imageUri);
            }
            break;
    }
}
```