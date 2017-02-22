#import "CDVOnyxPlugin.h"
#import <Cordova/CDV.h>

@interface CDVOnyxPlugin ()

@property NSString* OnyxAction;
@property NSMutableArray* OnyxImageTypes;
@property NSString* callbackId;
@property NSData* registeredFingerprintTemplate;

@end

@implementation CDVOnyxPlugin
NSString * const PLUGIN_ACTION_ENROLL = @"enroll";
NSString * const PLUGIN_ACTION_VERIFY = @"verify";
NSString * const PLUGIN_ACTION_TEMPLATE = @"template";
NSString * const PLUGIN_ACTION_IMAGE = @"image";

NSString * const ONYX_IMAGE_TYPE_RAW = @"raw";
NSString * const ONYX_IMAGE_TYPE_PREPROCESSED = @"preprocessed";
NSString * const ONYX_IMAGE_TYPE_ENHANCED = @"enhanced";
NSString * const ONYX_IMAGE_TYPE_WSQ = @"wsq";


- (void)image:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    NSDictionary* args = [command.arguments objectAtIndex:0];
    _OnyxAction = [args objectForKey:@"action"];
    _OnyxImageTypes = nil;
    _OnyxImageTypes = [args objectForKey:@"imageTypes"];
    if (_OnyxImageTypes == nil) {
        _OnyxImageTypes =  [[NSMutableArray alloc]init];
        [_OnyxImageTypes addObject:@"preprocessed"];
    }
    NSLog(@"action: %@", _OnyxAction);

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_IMAGE]) {
        [self startOnyxCapture:[args objectForKey:@"onyxLicense"]];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    }
}

- (void)enroll:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    NSDictionary* args = [command.arguments objectAtIndex:0];
    _OnyxAction = [args objectForKey:@"action"];
    NSLog(@"action: %@", _OnyxAction);

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_ENROLL]) {
        [self startOnyxEnrollment:[args objectForKey:@"onyxLicense"]];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    }
}

- (void)verify:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    NSDictionary* args = [command.arguments objectAtIndex:0];
    _OnyxAction = [args objectForKey:@"action"];
    NSLog(@"action: %@", _OnyxAction);

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_VERIFY]) {
        // Retrieve stored fingerprint template
        _registeredFingerprintTemplate = [[NSUserDefaults standardUserDefaults] objectForKey:@"enrolledFingerprintTemplate"];
        if (_registeredFingerprintTemplate != nil) {
            [self startOnyxCapture:[args objectForKey:@"onyxLicense"]];
        } else {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"No fingerprint enrolled"];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
        }
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    }
}

- (void)template:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    NSDictionary* args = [command.arguments objectAtIndex:0];
    _OnyxAction = [args objectForKey:@"action"];
    NSLog(@"action: %@", _OnyxAction);

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:PLUGIN_ACTION_TEMPLATE]) {
        [self startOnyxCapture:[args objectForKey:@"onyxLicense"]];
    } else {
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
    }
}

// private method
- (void)startOnyxEnrollment:(NSString*)onyxLicense {
    NSLog(@"Starting Onyx Enrollment...");
    // Set up OnyxViewController
    OnyxViewController *onyxVC = [[OnyxViewController alloc] init];
    onyxVC.delegate = self;
    onyxVC.state = ONYX_ENROLL;
    onyxVC.showTutorial = true;
    onyxVC.hideHandSelect = false;
    onyxVC.reverseHand = true;
    onyxVC.enrollCount = 3;
    onyxVC.frameCount = 3;
    onyxVC.focusMeasurementRequirement = 0.03;
    onyxVC.license = onyxLicense;

    // Show OnyxViewController
    [self.viewController presentViewController:onyxVC animated:NO completion:nil];
}

// private method
- (void)startOnyxCapture:(NSString*)onyxLicense {
    NSLog(@"Starting Onyx for capture...");
    // Set up OnyxViewController
    OnyxViewController *onyxVC = [[OnyxViewController alloc] init];
    onyxVC.delegate = self;
    onyxVC.state = ONYX_SINGLE;
    onyxVC.showTutorial = false;
    onyxVC.hideHandSelect = false;
    onyxVC.reverseHand = true;
    onyxVC.focusMeasurementRequirement = 0.03;
    onyxVC.license = onyxLicense;

    // Show OnyxViewController
    [self.viewController presentViewController:onyxVC animated:NO completion:nil];
}

- (void) Onyx:(OnyxViewController *)controller didOutputProcessedFingerprint:(ProcessedFingerprint *)fingerprint fromSet:(NSArray *)fingerprints {
    NSLog(@"Onyx: didOutputProcessedFingerprint()");
    CDVPluginResult* pluginResult = nil;
    
    NSData* fingerprintTemplate = [NSData dataWithData:fingerprint.fingerprintTemplate];

    // Prepare variables for response values
    NSMutableDictionary* imagesJSON;
    NSString* fptBase64EncodedString = @"";
    BOOL isVerified = NO;
    float nfiqScore = 0;

    // Get response values
    if ([_OnyxAction isEqualToString:PLUGIN_ACTION_IMAGE]) {
        NSLog(@"imageTypes: %@", _OnyxImageTypes);
        
        UIImage* fingerprintImage;
        NSString* imageUriPrefix = @"data:image/jpeg;base64,%@";
        NSData* imageData;
        
        NSString* rawImageUri = @"";
        NSString* preprocessedImageUri = @"";
        NSString* enhancedImageUri = @"";
        NSMutableDictionary* wsqJSON;

        // Get results from Onyx
        if ([_OnyxImageTypes containsObject:ONYX_IMAGE_TYPE_RAW]) {
            fingerprintImage = fingerprint.sourceImage;
            // Generate imageUri for fingerprint image
            imageData = UIImageJPEGRepresentation(fingerprintImage, 1.0);
            rawImageUri = [NSString stringWithFormat:imageUriPrefix, [imageData base64EncodedStringWithOptions:0]];
        }
        if ([_OnyxImageTypes containsObject:ONYX_IMAGE_TYPE_PREPROCESSED]) {
            fingerprintImage = fingerprint.processedImage;
            // Generate imageUri for fingerprint image
            imageData = UIImageJPEGRepresentation(fingerprintImage, 1.0);
            preprocessedImageUri = [NSString stringWithFormat:imageUriPrefix, [imageData base64EncodedStringWithOptions:0]];
        }
        if ([_OnyxImageTypes containsObject:ONYX_IMAGE_TYPE_ENHANCED]) {
            fingerprintImage = fingerprint.enhancedImage;
            // Generate imageUri for fingerprint image
            imageData = UIImageJPEGRepresentation(fingerprintImage, 1.0);
            enhancedImageUri = [NSString stringWithFormat:imageUriPrefix, [imageData base64EncodedStringWithOptions:0]];
        }
        if ([_OnyxImageTypes containsObject:ONYX_IMAGE_TYPE_WSQ]) {
            NSString* encodedBytes = [NSString stringWithFormat:@"%@", [fingerprint.WSQ base64EncodedStringWithOptions:0]];
            NSArray* wsqKeys = [NSArray arrayWithObjects: @"bytes", @"nfiqScore", nil];
            NSArray* wsqValues = [NSArray arrayWithObjects:encodedBytes, [NSNumber numberWithFloat:fingerprint.nfiqscore], nil];
            wsqJSON = [NSMutableDictionary dictionaryWithObjects:wsqValues forKeys:wsqKeys];
        }

        NSArray* imageKeys = [NSArray arrayWithObjects: @"raw", @"preprocessed", @"enhanced", @"wsq", nil];
        NSArray* imageValues = [NSArray arrayWithObjects:rawImageUri, preprocessedImageUri, enhancedImageUri, wsqJSON, nil];
        
        imagesJSON = [NSMutableDictionary dictionaryWithObjects:imageValues forKeys:imageKeys];
        
    } else if ([_OnyxAction isEqualToString:PLUGIN_ACTION_ENROLL] || [_OnyxAction isEqualToString:PLUGIN_ACTION_TEMPLATE]) {
        if ([_OnyxAction isEqualToString:PLUGIN_ACTION_ENROLL]) {
            // Store fingerprint template
            [self storeEnrolledFingerprint:fingerprintTemplate];
        }
        // Generate base64 encoded fingerprint template string
        fptBase64EncodedString = [fingerprintTemplate base64EncodedStringWithOptions:0];

    } else if ([_OnyxAction isEqualToString:PLUGIN_ACTION_VERIFY]) {
        NSLog(@"Comparing prints");

        nfiqScore = [OnyxMatch match:_registeredFingerprintTemplate with:fingerprintTemplate];

        NSLog(@"match value (%.2f)", nfiqScore);
        if (nfiqScore > 0.03) {
            NSLog(@"User Authenticated!");
            isVerified = YES;
        }
    }

    // Set response keys
    NSArray* keysArray = [NSArray arrayWithObjects: @"action", @"images", @"template", @"isVerified", @"nfiqScore", nil];
    // Set response values
    NSArray* valuesArray = [NSArray arrayWithObjects:_OnyxAction, imagesJSON, fptBase64EncodedString, [NSNumber numberWithBool:isVerified], [NSNumber numberWithFloat:nfiqScore], nil];

    // Create response object
    NSMutableDictionary* resultJSON = [NSMutableDictionary dictionaryWithObjects:valuesArray forKeys:keysArray];
    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultJSON];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:_callbackId];
}

- (void) storeEnrolledFingerprint:(NSData*) enrolledFingerprintTemplate {
    NSUserDefaults* defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:enrolledFingerprintTemplate forKey:@"enrolledFingerprintTemplate"];
    [defaults synchronize];

    NSLog(@"Saved users to defaults.");
}


@end
