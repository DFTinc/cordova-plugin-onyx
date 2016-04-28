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
    action = Onyx.Action.IMAGE;
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
    * [.Actions](#module_Onyx.Actions) : <code>enum</code>

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
```js
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
```js
// Show image
//
function onyxSuccess(result) {
    if (result.action === Onyx.Action.IMAGE) {
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
| action | <code>[Action](#module_Onyx.Action)</code> |  | **Required** Choose the action for onyx to execute. |

### onyx.OnyxResult : <code>Object</code>
Results return by Onyx.

**Kind**: static typedef of <code>[onyx](#module_onyx)</code>  
**Properties**

| Name | Type | Action | Description |
| --- | --- | --- | --- |
| imageUri | <code>string</code> | <code>[Onyx.Action.IMAGE](#module_Onyx.Action)</code> | Data URI containing base64 encode fingerprint JPEG image.  `"data:image/jpeg;base64," + base64EncodedString`  |
| template | <code>string</code> | <code>[Onyx.Action.TEMPLATE](#module_Onyx.Action)</code> <code>[Onyx.Action.ENROLL](#module_Onyx.Action)</code> | Base64 encode fingerprint template to send to backend server. |
| nfiqScore | <code>number</code> | <code>[Onyx.Action.VERIFY](#module_Onyx.Action)</code> | The result score of a verify action. |
| isVerified | <code>boolean</code> | <code>[Onyx.Action.VERIFY](#module_Onyx.Action)</code> | The fingerprint match result. |


---

<a name="module_Onyx"></a>
## Onyx
<a name="module_Onyx.Action"></a>
### Onyx.Action : <code>JSON Object</code>
**Kind**: static constants for actions of <code>[Onyx](#module_Onyx)</code>  
**Properties**

| Name | Type | Default | Description |
| --- | --- | --- | --- |
| IMAGE | <code>string</code> | <code>image</code> | Return a base64 encoded fingerprint image URI. |
| TEMPLATE | <code>string</code> | <code>template</code> | Return a base64 encoded fingerprint template |
| ENROLL | <code>string</code> | <code>enroll</code> | Enroll a fingerprint for your application to be stored on the local device and return the base64 encoded fingerprint template. |
| VERIFY | <code>string</code> | <code>verify</code> | Performs on device verification against the enrolled fingerprint. |