#import "CDVOnyxPlugin.h"
#import <Cordova/CDV.h>

@interface CDVOnyxPlugin ()

@property NSString* OnyxAction;
@property NSString* OnyxImageType;
@property NSString* callbackId;
@property NSData* registeredFingerprintTemplate;

@end

@implementation CDVOnyxPlugin

- (void)image:(CDVInvokedUrlCommand*)command {
    CDVPluginResult* pluginResult = nil;
    _callbackId = command.callbackId;
    NSDictionary* args = [command.arguments objectAtIndex:0];
    _OnyxAction = [args objectForKey:@"action"];
    _OnyxImageType = nil;
    _OnyxImageType = [args objectForKey:@"imageType"];
    if (_OnyxImageType == nil) {
        _OnyxImageType = @"preprocessed";
    }
    NSLog(@"action: %@", _OnyxAction);

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:@"image"]) {
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

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:@"enroll"]) {
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

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:@"verify"]) {
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

    if (args != nil && [_OnyxAction length] > 0 && [_OnyxAction isEqualToString:@"template"]) {
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

    // Get results from Onyx
    if ([_OnyxImageType isEqualToString:@"raw"]) {
        _fingerprintImage = fingerprint.sourceImage;
    } else if ([_OnyxImageType isEqualToString:@"preprocessed"]) {
        _fingerprintImage = fingerprint.processedImage;
    } else if ([_OnyxImageType isEqualToString:@"enhanced"]) {
        _fingerprintImage = fingerprint.enhancedImage;
    }

    _fingerprintTemplate = [NSData dataWithData:fingerprint.fingerprintTemplate];

    // Prepare response keys
    NSArray* keysArray = [NSArray arrayWithObjects: @"action", @"imageUri", @"template", @"isVerified", @"nfiqScore", nil];
    NSString* imageUri = @"";
    NSString* fptBase64EncodedString = @"";
    BOOL isVerified = NO;
    float nfiqScore = 0;

    // Get response values
    if ([_OnyxAction isEqualToString:@"image"]) {
        // Generate imageUri for fingerprint image
        NSData* imageData = UIImageJPEGRepresentation(_fingerprintImage, 1.0);
        imageUri = [NSString stringWithFormat:@"data:image/jpeg;base64,%@", [imageData base64EncodedStringWithOptions:0]];

    } else if ([_OnyxAction isEqualToString:@"enroll"] || [_OnyxAction isEqualToString:@"template"]) {
        if ([_OnyxAction isEqualToString:@"enroll"]) {
            // Store fingerprint template
            [self storeEnrolledFingerprint:_fingerprintTemplate];
        }
        // Generate base64 encoded fingerprint template string
        fptBase64EncodedString = [_fingerprintTemplate base64EncodedStringWithOptions:0];

    } else if ([_OnyxAction isEqualToString:@"verify"]) {
        NSLog(@"Comparing prints");

        nfiqScore = [OnyxMatch match:_registeredFingerprintTemplate with:_fingerprintTemplate];

        NSLog(@"match value (%.2f)", nfiqScore);
        if (nfiqScore > 0.03) {
            NSLog(@"User Authenticated!");
            isVerified = YES;
        }
    }

    // Set response values
    NSArray* valuesArray = [NSArray arrayWithObjects:_OnyxAction, imageUri, fptBase64EncodedString, [NSNumber numberWithBool:isVerified], [NSNumber numberWithFloat:nfiqScore], nil];

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