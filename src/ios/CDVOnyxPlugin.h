#import <Cordova/CDVPlugin.h>
#import <OnyxKit/OnyxViewController.h>
#import <OnyxKit/ProcessedFingerprint.h>
#import <OnyxKit/OnyxMatch.h>
#import <OnyxKit/NetworkUtil.h>

@interface CDVOnyxPlugin :CDVPlugin <UINavigationControllerDelegate, UIAlertViewDelegate, OnyxViewControllerDelegate> {
        NSData* _fingerprintTemplate;
        UIImage* _fingerprintImage;
}

- (void) image:(CDVInvokedUrlCommand*)command;
- (void) enroll:(CDVInvokedUrlCommand*)command;
- (void) verify:(CDVInvokedUrlCommand*)command;
- (void) template:(CDVInvokedUrlCommand*)command;

@end