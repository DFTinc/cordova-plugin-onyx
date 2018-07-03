#import "CDVOnyxPlugin.h"
#import <Cordova/CDV.h>
#import <OnyxCamera/OnyxMatch.h>

@interface CDVOnyxPlugin ()

@property NSString* OnyxAction;
@property NSString* callbackId;
@property NSDictionary* args;

@end

@implementation CDVOnyxPlugin
NSString * const PLUGIN_ACTION = @"action";
NSString * const PLUGIN_ACTION_MATCH = @"match";
NSString * const PLUGIN_ACTION_CAPTURE = @"capture";

NSString * const ONYX_LICENSE = @"onyxLicense";
NSString * const RETURN_RAW_IMAGE = @"returnRawImage";
NSString * const RETURN_GRAY_RAW_IMAGE = @"returnGrayRawImage";
NSString * const RETURN_PROCESSED_IMAGE = @"returnProcessedImage";
NSString * const RETURN_ENHANCED_IMAGE = @"returnEnhancedImage";
NSString * const RETURN_BLACK_WHITE_PROCESSED_IMAGE = @"returnBlackWhiteProcessedImage";
NSString * const RETURN_WSQ = @"returnWSQ";
NSString * const RETURN_GRAY_RAW_WSQ = @"returnGrayRawWSQ";
NSString * const RETURN_FINGERPRINT_TEMPLATE = @"returnFingerprintTemplate";
NSString * const SHOULD_INVERT = @"shouldInvert";
NSString * const SHOULD_SEGMENT = @"shouldSegment";
NSString * const SHOULD_CONVERT_TO_ISO_TEMPLATE = @"shouldConvertToISOTemplate";
NSString * const IMAGE_ROTATION = @"imageRotation";
NSString * const WHOLE_FINGER_CROP = @"wholeFingerCrop";
NSString * const CROP_SIZE = @"cropSize";
NSString * const CROP_SIZE_WIDTH = @"width";
NSString * const CROP_SIZE_HEIGHT = @"height";
NSString * const CROP_FACTOR = @"cropFactor";
NSString * const SHOW_LOADING_SPINNER = @"showLoadingSpinner";
NSString * const LAYOUT_PREFERENCE = @"layoutPreference";
NSString * const LAYOUT_PREFERENCE_UPPER_THIRD = @"UPPER_THIRD";
NSString * const LAYOUT_PREFERENCE_FULL = @"FULL";
NSString * const USE_MANUAL_CAPTURE = @"useManualCapture";
NSString * const USE_ONYX_LIVE = @"useOnyxLive";
NSString * const USE_FLASH = @"useFlash";
NSString * const RETICLE_ORIENTATION = @"reticleOrientation";
NSString * const RETICLE_ORIENTATION_LEFT = @"LEFT";
NSString * const RETICLE_ORIENTATION_RIGHT = @"RIGHT";
NSString * const RETICLE_ANGLE = @"reticleAngle";
NSString * const RETICLE_SCALE = @"reticleScale";
NSString * const BACKGROUND_COLOR_HEX_STRING = @"backgroundColorHexString";
NSString * const FLIP = @"flip";
NSString * const FLIP_HORIZONTAL = @"HORIZONTAL";
NSString * const FLIP_VERTICAL = @"VERTICAL";
NSString * const FLIP_BOTH = @"BOTH";
NSString * const FLIP_NONE = @"NONE";
NSString * const PROBE = @"probe";
NSString * const REFERENCE = @"reference";

- (void)match:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    _args = [command.arguments objectAtIndex:0];
    _OnyxAction = [_args objectForKey:PLUGIN_ACTION];
    NSLog(@"action: %@", _OnyxAction);

    if (_args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_MATCH]) {
        NSString* probeString = [_args objectForKey:PROBE];
        NSString* referenceString = [_args objectForKey:REFERENCE];
        if (nil != PROBE && ![probeString isEqualToString:@""] && nil != referenceString && ![referenceString isEqualToString:@""]) {
            NSData* probe = [[NSData alloc] initWithBase64EncodedString:probeString options:0];
            NSData* reference = [[NSData alloc] initWithBase64EncodedString:referenceString options:0];
            double score = [OnyxMatch match:probe with:reference];
            float threshold = 0.03f;
            Boolean isVerified = score > threshold;
            NSArray* keysArray = [NSArray arrayWithObjects: @"action", @"isVerified", @"matchScore", nil];
            NSArray*valuesArray = [NSArray arrayWithObjects:_OnyxAction, [NSNumber numberWithBool:isVerified], [NSNumber numberWithFloat:score], nil];
            NSMutableDictionary* resultJSON = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultJSON];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
            return;
        }
    }
    
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
}

- (void)capture:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    _args = [command.arguments objectAtIndex:0];
    _OnyxAction = [_args objectForKey:PLUGIN_ACTION];
    NSLog(@"action: %@", _OnyxAction);

    if (_args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_CAPTURE]) {
        [self setupOnyx];
        return;
    }
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    
}

// private method
- (void)setupOnyx {
    NSString* onyxLicense = [_args objectForKey:ONYX_LICENSE];
    
    OnyxConfigurationBuilder* onyxConfigBuilder = [[OnyxConfigurationBuilder alloc] init];
    onyxConfigBuilder.setViewController(self.viewController)
    .setLicenseKey(onyxLicense)
    .setSuccessCallback([self onyxSuccessCallback])
    .setErrorCallback([self onyxErrorCallback])
    .setOnyxCallback([self onyxCallback]);
    
    if ([[_args objectForKey:RETURN_RAW_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnRawImage([[_args objectForKey:RETURN_RAW_IMAGE] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_GRAY_RAW_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnGrayRawImage([[_args objectForKey:RETURN_GRAY_RAW_IMAGE] boolValue]);
    }
    if ([[_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnProcessedImage([[_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnEnhancedImage([[_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_BLACK_WHITE_PROCESSED_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnBlackWhiteProcessedImage([[_args objectForKey:RETURN_BLACK_WHITE_PROCESSED_IMAGE] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_WSQ] boolValue]) {
        onyxConfigBuilder.setReturnWSQ([[_args objectForKey:RETURN_WSQ] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_GRAY_RAW_WSQ] boolValue]) {
        onyxConfigBuilder.setReturnGrayRawWSQ([[_args objectForKey:RETURN_GRAY_RAW_WSQ] boolValue]);
    }
    
    if ([[_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]) {
        onyxConfigBuilder.setReturnFingerprintTemplate([[_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]);
    }
    
    if ([[_args objectForKey:USE_FLASH] boolValue]) {
        onyxConfigBuilder.setUseFlash([[_args objectForKey:USE_FLASH] boolValue]);
    }
    
    if ([[_args objectForKey:USE_ONYX_LIVE] boolValue]) {
        onyxConfigBuilder.setUseOnyxLive([[_args objectForKey:USE_ONYX_LIVE] boolValue]);
    }
    
    if ([[_args objectForKey:SHOULD_INVERT] boolValue]) {
        onyxConfigBuilder.setShouldInvert([[_args objectForKey:SHOULD_INVERT] boolValue]);
    }
    
    if ([[_args objectForKey:SHOULD_SEGMENT] boolValue]) {
        onyxConfigBuilder.setShouldSegment([[_args objectForKey:SHOULD_SEGMENT] boolValue]);
    }
    
    if ([[_args objectForKey:SHOULD_CONVERT_TO_ISO_TEMPLATE] boolValue]) {
        onyxConfigBuilder.setShouldConvertToISOTemplate([[_args objectForKey:SHOULD_CONVERT_TO_ISO_TEMPLATE] boolValue]);
    }
    
    if ([[_args objectForKey:WHOLE_FINGER_CROP] boolValue]) {
        onyxConfigBuilder.setWholeFingerCrop([[_args objectForKey:WHOLE_FINGER_CROP] boolValue]);
    }
    
    if ([[_args objectForKey:SHOW_LOADING_SPINNER] boolValue]) {
        onyxConfigBuilder.setShowLoadingSpinner([[_args objectForKey:SHOW_LOADING_SPINNER] boolValue]);
    }
    
    if ([_args objectForKey:BACKGROUND_COLOR_HEX_STRING]) {
        NSString *backgroundColorHexString = [_args objectForKey:BACKGROUND_COLOR_HEX_STRING];
        if (![backgroundColorHexString isEqualToString:@""]) {
            onyxConfigBuilder.setBackgroundColorHexString(backgroundColorHexString);
        }
    }
    
    if ([_args objectForKey:FLIP]) {
        NSString* flipString = [_args objectForKey:FLIP];
        if (![flipString isEqualToString:@""]) {
            Flip flip = HORIZONTAL;
            if ([flipString isEqualToString:FLIP_HORIZONTAL]) {
                flip = HORIZONTAL;
            } else if ([flipString isEqualToString:FLIP_VERTICAL]) {
                flip = VERTICAL;
            } else if ([flipString isEqualToString:FLIP_BOTH]) {
                flip = BOTH;
            } else if ([flipString isEqualToString:FLIP_NONE]) {
                flip = NONE;
            }
            onyxConfigBuilder.setFlip(flip);
        }
    }
    
    if ([_args objectForKey:RETICLE_ORIENTATION]) {
        NSString* reticleOrientationString = [_args objectForKey:RETICLE_ORIENTATION];
        if (![reticleOrientationString isEqualToString:@""]) {
            ReticleOrientation orientation = LEFT;
            if ([reticleOrientationString isEqualToString:RETICLE_ORIENTATION_LEFT]) {
                orientation = LEFT;
            } else if ([reticleOrientationString isEqualToString:RETICLE_ORIENTATION_RIGHT]) {
                orientation = RIGHT;
            }
            onyxConfigBuilder.setReticleOrientation(orientation);
        }
    }
    
    if ([_args objectForKey:RETICLE_ANGLE]) {
        onyxConfigBuilder.setReticleAngle((int)[[_args objectForKey:RETICLE_ANGLE] integerValue]);
    }
    
    if ([_args objectForKey:RETICLE_SCALE]) {
        onyxConfigBuilder.setReticleScale([[_args objectForKey:RETICLE_SCALE] floatValue]);
    }
    
    if ([_args objectForKey:CROP_FACTOR]) {
        onyxConfigBuilder.setCropFactor([[_args objectForKey:CROP_FACTOR] floatValue]);
    }
    
    if ([_args objectForKey:CROP_SIZE]) {
        NSDictionary* cropSize = [_args objectForKey:CROP_SIZE];
        float width = 600;
        float height = 960;
        float floatValue = 0;
        floatValue = [[cropSize objectForKey:CROP_SIZE_WIDTH] floatValue];
        if (floatValue != 0) {
            width = floatValue;
        }
        floatValue = [[cropSize objectForKey:CROP_SIZE_HEIGHT] floatValue];
        if (floatValue != 0) {
            width = floatValue;
        }
        onyxConfigBuilder.setCropSize(CGSizeMake(width, height));
    }
    
    if ([_args objectForKey:IMAGE_ROTATION]) {
        onyxConfigBuilder.setImageRotation((int)[[_args objectForKey:IMAGE_ROTATION] integerValue]);
    }
    
    [onyxConfigBuilder buildOnyxConfiguration];
}

- (void(^)(Onyx* configuredOnyx))onyxCallback {
    return ^(Onyx* configuredOnyx) {
        NSLog(@"Onyx Callback");
        [configuredOnyx capture:self.viewController];
    };
}

- (void(^)(OnyxResult* onyxResult))onyxSuccessCallback {
    return ^(OnyxResult* onyxResult) {
        NSLog(@"Onyx Success Callback");
        self->_onyxResult = onyxResult;
        
        CDVPluginResult* pluginResult = nil;
        
        NSString* rawImageUri = @"";
        NSString* grayRawImageUri = @"";
        NSString* processedImageUri = @"";
        NSString* enhancedImageUri = @"";
        NSString* blackWhiteProcessedImageUri = @"";
        NSString* base64EncodedWsq = @"";
        NSString* base64EncodedGrayRawWsq = @"";
        NSString* base64EncodedFingerprintTemplate = @"";
        NSMutableDictionary* captureMetricsJson;
        
        NSArray* keysArray;
        NSArray* valuesArray;
        
        if ([[_args objectForKey:RETURN_RAW_IMAGE] boolValue]) {
            rawImageUri = [_onyxResult getFingerprintImageUri:[_onyxResult getRawFingerprintImage]];
        }
        
        if ([[_args objectForKey:RETURN_GRAY_RAW_IMAGE] boolValue]) {
            grayRawImageUri = [_onyxResult getFingerprintImageUri:[_onyxResult getGrayRawFingerprintImage]];
        }
        
        if ([[_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]) {
            processedImageUri = [_onyxResult getFingerprintImageUri:[_onyxResult getProcessedFingerprintImage]];
        }
        
        if ([[_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]) {
            enhancedImageUri = [_onyxResult getFingerprintImageUri:[_onyxResult getEnhancedFingerprintImage]];
        }
        
        if ([[_args objectForKey:RETURN_BLACK_WHITE_PROCESSED_IMAGE] boolValue]) {
            blackWhiteProcessedImageUri = [_onyxResult getFingerprintImageUri:[_onyxResult getBlackWhiteProcessedFingerprintImage]];
        }
        
        if ([[_args objectForKey:RETURN_WSQ] boolValue]) {
            base64EncodedWsq = [_onyxResult getBase64EncodedString:[_onyxResult getWsqData]];
        }

        if ([[_args objectForKey:RETURN_GRAY_RAW_WSQ] boolValue]) {
            base64EncodedGrayRawWsq = [_onyxResult getBase64EncodedString:[_onyxResult getGrayRawWsqData]];
        }

        if ([[_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]) {
            base64EncodedFingerprintTemplate = [_onyxResult getBase64EncodedString:[_onyxResult getFingerprintTemplate]];
        }

        if (nil != [_onyxResult getMetrics]) {
            NSMutableDictionary* nfiqMetricsJson;
            CaptureMetrics* metrics = [_onyxResult getMetrics];
            int nfiqScore = 0;
            float mlpScore = 0;
            float focusQuality = [metrics getFocusQuality];
            float distanceToCenter = [metrics getDistanceToCenter];
            float livenessConfidence = [metrics getLivenessConfidence];
            
            if (nil != [metrics getNfiqMetrics]) {
                NfiqMetrics* nfiqMetrics = [metrics getNfiqMetrics];
                nfiqScore = [nfiqMetrics getNfiqScore];
                mlpScore = [nfiqMetrics getMlpScore];
                
                keysArray = [NSArray arrayWithObjects: @"nfiqScore", @"mlpScore", nil];
                valuesArray = [NSArray arrayWithObjects: [NSNumber numberWithInteger: nfiqScore], [NSNumber numberWithFloat: mlpScore], nil];
                
                nfiqMetricsJson = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
            }
            
            keysArray = [NSArray arrayWithObjects: @"livenessConfidence", @"focusQuality", @"distanceToCenter", @"nfiqMetrics", nil];
            valuesArray = [NSArray arrayWithObjects: [NSNumber numberWithFloat: livenessConfidence], [NSNumber numberWithFloat: focusQuality], [NSNumber numberWithFloat: distanceToCenter], nfiqMetricsJson, nil];
            
            captureMetricsJson = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
        }
        
        keysArray = [NSArray arrayWithObjects: @"action", @"rawFingerprintDataUri", @"grayRawFingerprintDataUri", @"processedFingerprintDataUri", @"enhancedFingerprintDataUri", @"blackWhiteProcessedFingerprintDataUri", @"base64EncodedWsqBytes", @"base64EncodedGrayRawWsqBytes", @"base64EncodedFingerprintTemplate", @"captureMetrics", nil];
        
        valuesArray = [NSArray arrayWithObjects:_OnyxAction, rawImageUri, grayRawImageUri, processedImageUri, enhancedImageUri, blackWhiteProcessedImageUri, base64EncodedWsq, base64EncodedGrayRawWsq, base64EncodedFingerprintTemplate, captureMetricsJson, nil];
        
        NSMutableDictionary* resultJSON = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultJSON];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    };
}

- (void(^)(OnyxError* onyxError)) onyxErrorCallback {
    return ^(OnyxError* onyxError) {
        NSLog(@"Onyx Error Callback");
        // Set response keys
        NSArray* keysArray = [NSArray arrayWithObjects: @"error", @"message", nil];
        // Set response values
        NSArray* valuesArray = [NSArray arrayWithObjects: @(onyxError.error), onyxError.errorMessage, nil];
        
        // Create response object
        NSMutableDictionary* resultJSON = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:resultJSON];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    };
}

@end
