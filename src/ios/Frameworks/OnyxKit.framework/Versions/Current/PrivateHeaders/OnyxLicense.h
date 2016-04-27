//
//  OnyxLicense.h
//  onyx
//
//  Created by Devan Buggay on 3/28/14.
//  Copyright (c) 2014 Devan Buggay. All rights reserved.
//

#import <Foundation/Foundation.h>
//#import "Reachability.h"
#import "NetworkUtil.h"

//#include <onyx-core/License.h>

@interface OnyxLicense : NSObject {
    NSString *baseurl;
    
    NSString *deviceKey;
    NSUUID *deviceUDID;
    
    NSInteger licenseType;
    
    //Type 0 and 1 license
    NSInteger validateThreshold;
    
    //Type 0 license
    NSDate *lastValidateDate;
    

    
}

@property NSInteger usageCount;
@property bool isKeySet;
@property bool isLicenseTypeSet;
@property bool hasRecievedResponse;

@property Reachability* internetReachable;
@property Reachability* hostReachable;

@property BOOL internetActive;
@property BOOL hostActive;

+(OnyxLicense *)sharedInstance;

-(void)setKey:(NSString *)key;

-(void)loadDefaults;
-(void)saveDefaults;

-(void)increaseUsageCount;

-(void)validate;
-(bool)validateWithKey:(NSString *) key;
-(bool)recievedValidateJSON:(NSData *) data;
-(bool)recievedValidateJSONError:(NSError *) error;
-(bool) checkValidity;

//-(BOOL) checkNetworkStatus;

@property (nonatomic) BOOL isValid;

@end
