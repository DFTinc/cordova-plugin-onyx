#import <Cordova/CDVPlugin.h>
#import <OnyxKit/OnyxConfigurationBuilder.h>
#import <OnyxKit/Onyx.h>

@interface CDVOnyxPlugin :CDVPlugin <UINavigationControllerDelegate> {
}
@property OnyxResult* onyxResult;
- (void) match:(CDVInvokedUrlCommand*)command;
- (void) capture:(CDVInvokedUrlCommand*)command;
- (void(^)(OnyxResult* onyxResult))onyxSuccessCallback;
- (void(^)(OnyxError* onyxError)) onyxErrorCallback;
- (void(^)(Onyx* configuredOnyx))onyxCallback;
@end
