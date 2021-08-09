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
NSString * const RETURN_PROCESSED_IMAGE = @"returnProcessedImage";
NSString * const RETURN_ENHANCED_IMAGE = @"returnEnhancedImage";
NSString * const RETURN_WSQ = @"returnWSQ";
NSString * const RETURN_FINGERPRINT_TEMPLATE = @"returnFingerprintTemplate";
NSString * const WHOLE_FINGER_CROP = @"wholeFingerCrop";
NSString * const CROP_SIZE = @"cropSize";
NSString * const CROP_SIZE_WIDTH = @"width";
NSString * const CROP_SIZE_HEIGHT = @"height";
NSString * const CROP_FACTOR = @"cropFactor";
NSString * const SHOW_LOADING_SPINNER = @"showLoadingSpinner";
NSString * const USE_MANUAL_CAPTURE = @"useManualCapture";
NSString * const SHOW_MANUAL_CAPTURE_TEXT = @"showManualCaptureText";
NSString * const MANUAL_CAPTURE_TEXT = @"manualCaptureText";
NSString * const USE_ONYX_LIVE = @"useOnyxLive";
NSString * const USE_FLASH = @"useFlash";
NSString * const RETICLE_ORIENTATION = @"reticleOrientation";
NSString * const RETICLE_ORIENTATION_LEFT = @"LEFT";
NSString * const RETICLE_ORIENTATION_RIGHT = @"RIGHT";
NSString * const RETICLE_ORIENTATION_THUMB_PORTRAIT = @"THUMB_PORTRAIT";
NSString * const BACKGROUND_COLOR_HEX_STRING = @"backgroundColorHexString";
NSString * const PROBE = @"probe";
NSString * const REFERENCE = @"reference";
NSString * const PYRAMID_SCALES = @"pyramidScales";
NSString * const BACK_BUTTON_TEXT = @"backButtonText";

- (void)match:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    _args = [command.arguments objectAtIndex:0];
    _OnyxAction = [_args objectForKey:PLUGIN_ACTION];
    NSLog(@"action: %@", _OnyxAction);

    if (_args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_MATCH]) {
        NSString* probeString = [_args objectForKey:PROBE];
        NSString* referenceString = [_args objectForKey:REFERENCE];
        NSArray* scalesArray = [_args objectForKey:PYRAMID_SCALES];
        if (nil != PROBE && ![probeString isEqualToString:@""] && nil != referenceString && ![referenceString isEqualToString:@""]) {
            NSData* referenceData = [[NSData alloc] initWithBase64EncodedString:referenceString options:0];
            NSString* probeEncodedDataString = [probeString substringFromIndex:[IMAGE_URI_PREFIX length]];
            NSData* probeData = [[NSData alloc] initWithBase64EncodedString:probeEncodedDataString options:0];
            UIImage* probeImage = [UIImage imageWithData:probeData];
            double score = [OnyxMatch pyramidMatch:referenceData withImage:probeImage scales:scalesArray];
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

    if ([[_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnProcessedImage([[_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]);
    }

    if ([[_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]) {
        onyxConfigBuilder.setReturnEnhancedImage([[_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]);
    }

    if ([[_args objectForKey:RETURN_WSQ] boolValue]) {
        onyxConfigBuilder.setReturnWSQ([[_args objectForKey:RETURN_WSQ] boolValue]);
    }

    if ([[_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]) {
        onyxConfigBuilder.setReturnFingerprintTemplate([[_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]);
    }

    onyxConfigBuilder.setUseFlash([[_args objectForKey:USE_FLASH] boolValue]);

    if ([[_args objectForKey:USE_ONYX_LIVE] boolValue]) {
        onyxConfigBuilder.setUseOnyxLive([[_args objectForKey:USE_ONYX_LIVE] boolValue]);
    }

    if ([[_args objectForKey:WHOLE_FINGER_CROP] boolValue]) {
        onyxConfigBuilder.setWholeFingerCrop([[_args objectForKey:WHOLE_FINGER_CROP] boolValue]);
    }

    if ([[_args objectForKey:SHOW_LOADING_SPINNER] boolValue]) {
        onyxConfigBuilder.setShowLoadingSpinner([[_args objectForKey:SHOW_LOADING_SPINNER] boolValue]);
    }

    if ([[_args objectForKey:USE_MANUAL_CAPTURE] boolValue]) {
        onyxConfigBuilder.setUseManualCapture([[_args objectForKey:USE_MANUAL_CAPTURE] boolValue]);
    }

    // Default is true so check if it should be hidden
    if (![[_args objectForKey:SHOW_MANUAL_CAPTURE_TEXT] boolValue]) {
        onyxConfigBuilder.setShowManualCaptureText([[_args objectForKey:SHOW_MANUAL_CAPTURE_TEXT] boolValue]);
    }

    if ([_args objectForKey:BACKGROUND_COLOR_HEX_STRING]) {
        NSString *backgroundColorHexString = [_args objectForKey:BACKGROUND_COLOR_HEX_STRING];
        if (![backgroundColorHexString isEqualToString:@""]) {
            onyxConfigBuilder.setBackgroundColorHexString(backgroundColorHexString);
        }
    }

    if ([_args objectForKey:BACK_BUTTON_TEXT]) {
        NSString *backButtonText = [_args objectForKey:BACK_BUTTON_TEXT];
        if (![backButtonText isEqualToString:@""]) {
            onyxConfigBuilder.setBackButtonText(backButtonText);
        }
    }

    if ([_args objectForKey:MANUAL_CAPTURE_TEXT]) {
        NSString *manualCaptureText = [_args objectForKey:MANUAL_CAPTURE_TEXT];
        if (![manualCaptureText isEqualToString:@""]) {
            onyxConfigBuilder.setManualCaptureText(manualCaptureText);
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
            } else if ([reticleOrientationString isEqualToString:RETICLE_ORIENTATION_THUMB_PORTRAIT]) {
                orientation = THUMB_PORTRAIT;
            }
            onyxConfigBuilder.setReticleOrientation(orientation);
        }
    }

    if ([_args objectForKey:CROP_FACTOR]) {
        onyxConfigBuilder.setCropFactor([[_args objectForKey:CROP_FACTOR] floatValue]);
    }

    if ([_args objectForKey:CROP_SIZE]) {
        NSDictionary* cropSize = [_args objectForKey:CROP_SIZE];
        float width = 512;
        float height = 300;
        float floatValue = 0;
        floatValue = [[cropSize objectForKey:CROP_SIZE_WIDTH] floatValue];
        if (floatValue != 0) {
            width = floatValue;
        }
        floatValue = 0;
        floatValue = [[cropSize objectForKey:CROP_SIZE_HEIGHT] floatValue];
        if (floatValue != 0) {
            height = floatValue;
        }
        onyxConfigBuilder.setCropSize(CGSizeMake(width, height));
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

        NSMutableArray* onyxResults = [[NSMutableArray alloc] init];
        NSMutableArray* rawFingerprintImages = nil;
        NSMutableArray* processedFingerprintImages = nil;
        NSMutableArray* enhancedFingerprintImages = nil;
        NSMutableArray* wsqDataArray = nil;
        NSMutableArray* fingerprintTemplates = nil;
        NSUInteger numberFingersProcessed = 0;

        if (nil != [self->_onyxResult getRawFingerprintImages]) {
          rawFingerprintImages = [self->_onyxResult getRawFingerprintImages];
          if ([rawFingerprintImages count] > numberFingersProcessed) {
            numberFingersProcessed = [rawFingerprintImages count];
          }
        }
        if (nil != [self->_onyxResult getProcessedFingerprintImages]) {
          processedFingerprintImages = [self->_onyxResult getProcessedFingerprintImages];
          if ([processedFingerprintImages count] > numberFingersProcessed) {
            numberFingersProcessed = [processedFingerprintImages count];
          }
        }
        if (nil != [self->_onyxResult getEnhancedFingerprintImages]) {
          enhancedFingerprintImages = [self->_onyxResult getEnhancedFingerprintImages];
          if ([enhancedFingerprintImages count] > numberFingersProcessed) {
            numberFingersProcessed = [enhancedFingerprintImages count];
          }
        }
        if (nil != [self->_onyxResult getWsqData]) {
          wsqDataArray = [self->_onyxResult getWsqData];
          if ([wsqDataArray count] > numberFingersProcessed) {
            numberFingersProcessed = [wsqDataArray count];
          }
        }
        if (nil != [self->_onyxResult getFingerprintTemplates]) {
            fingerprintTemplates = [self->_onyxResult getFingerprintTemplates];
          if ([fingerprintTemplates count] > numberFingersProcessed) {
            numberFingersProcessed = [fingerprintTemplates count];
          }
        }

        NSArray* keysArray;
        NSArray* valuesArray;

        for (int i = 0; i < numberFingersProcessed; i++) {
            NSString* rawImageUri = @"";
            NSString* processedImageUri = @"";
            NSString* enhancedImageUri = @"";
            NSString* base64EncodedWsq = @"";
            NSString* base64EncodedFingerprintTemplate = @"";
            NSMutableDictionary* captureMetricsJson;

            if ([[self->_args objectForKey:RETURN_RAW_IMAGE] boolValue]) {
                if (nil != rawFingerprintImages && [rawFingerprintImages count] == numberFingersProcessed) {
                    rawImageUri = [self getFingerprintImageUri:rawFingerprintImages[i]];
                }
            }
            if ([[self->_args objectForKey:RETURN_PROCESSED_IMAGE] boolValue]) {
                if (nil != processedFingerprintImages && [processedFingerprintImages count] == numberFingersProcessed) {
                    processedImageUri = [self getFingerprintImageUri:processedFingerprintImages[i]];
                }
            }
            if ([[self->_args objectForKey:RETURN_ENHANCED_IMAGE] boolValue]) {
                if (nil != enhancedFingerprintImages && [enhancedFingerprintImages count] == numberFingersProcessed) {
                    enhancedImageUri = [self getFingerprintImageUri:enhancedFingerprintImages[i]];
                }
            }
            if ([[self->_args objectForKey:RETURN_WSQ] boolValue]) {
                if (nil != rawFingerprintImages && [rawFingerprintImages count] == numberFingersProcessed) {
                    base64EncodedWsq = [self getBase64EncodedString:wsqDataArray[i]];
                }
            }
            if ([[self->_args objectForKey:RETURN_FINGERPRINT_TEMPLATE] boolValue]) {
                if (nil != fingerprintTemplates && [fingerprintTemplates count] == numberFingersProcessed) {
                    base64EncodedFingerprintTemplate = [self getBase64EncodedString:fingerprintTemplates[i]];
                }
            }

            if (nil != [self->_onyxResult getMetrics]) {
                NSMutableDictionary* nfiqMetricsJson = [[NSMutableDictionary alloc] init];
                CaptureMetrics* metrics = [self->_onyxResult getMetrics];
                int nfiqScore = 0;
                float mlpScore = 0;
                float focusQuality = [metrics getFocusQuality];
                float livenessConfidence = [metrics getLivenessConfidence];

                if (nil != [metrics getNfiqMetrics]) {
                    NSMutableArray* nfiqMetricsArray = [metrics getNfiqMetrics];
                    if ([nfiqMetricsArray count] == numberFingersProcessed) {
                        NfiqMetrics* nfiqMetrics = nfiqMetricsArray[i];
                        nfiqScore = [nfiqMetrics getNfiqScore];
                        mlpScore = [nfiqMetrics getMlpScore];

                        keysArray = [NSArray arrayWithObjects: @"nfiqScore", @"mlpScore", nil];
                        valuesArray = [NSArray arrayWithObjects: [NSNumber numberWithInteger: nfiqScore], [NSNumber numberWithFloat: mlpScore], nil];
                        nfiqMetricsJson = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
                    }
                }

                keysArray = [NSArray arrayWithObjects: @"livenessConfidence", @"focusQuality", @"nfiqMetrics", nil];
                valuesArray = [NSArray arrayWithObjects: [NSNumber numberWithFloat: livenessConfidence], [NSNumber numberWithFloat: focusQuality], nfiqMetricsJson, nil];
                captureMetricsJson = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
            }
            keysArray = [NSArray arrayWithObjects: @"rawFingerprintDataUri", @"processedFingerprintDataUri", @"enhancedFingerprintDataUri", @"base64EncodedWsqBytes", @"base64EncodedFingerprintTemplate", @"captureMetrics", nil];
            valuesArray = [NSArray arrayWithObjects:rawImageUri, processedImageUri, enhancedImageUri, base64EncodedWsq, base64EncodedFingerprintTemplate, captureMetricsJson, nil];
            NSMutableDictionary* iOnyxResult = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
            onyxResults[i] = iOnyxResult;
        }

        keysArray = [NSArray arrayWithObjects: @"action", @"onyxResults", nil];
        valuesArray = [NSArray arrayWithObjects:self->_OnyxAction, onyxResults, nil];
        NSMutableDictionary* resultJSON = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];

        CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultJSON];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self->_callbackId];
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
        [self.commandDelegate sendPluginResult:pluginResult callbackId:self->_callbackId];
    };
}

- (NSString*) getFingerprintImageUri:(UIImage*)fingerprintImage {
    NSString* imageUriPrefix = [IMAGE_URI_PREFIX stringByAppendingString:@"%@"]; //@"data:image/jpeg;base64,%@";
    NSData* imageData = UIImageJPEGRepresentation(fingerprintImage, 1.0);
    return [NSString stringWithFormat:imageUriPrefix, [imageData base64EncodedStringWithOptions:0]];
}

- (NSString*) getBase64EncodedString:(NSData*)data {
    return [data base64EncodedStringWithOptions:0];
}

@end
