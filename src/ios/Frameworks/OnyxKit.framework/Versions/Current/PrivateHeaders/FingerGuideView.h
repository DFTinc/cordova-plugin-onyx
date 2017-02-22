//
//  ONXFingerGuideView.h
//  IDFingerScanApp
//
//  Created by zaknixon on 6/29/13.
//  Copyright (c) 2013 Diamond Fortress Technologies. All rights reserved.
//

#import <UIKit/UIKit.h>

enum {
    FingerCenter,
    FingerLeft,
    FingerRight
};

typedef NSInteger FingerState;

/**
 Class that represents a guide view used to help 
 user's properly position their finger in the camera's view.
 */
@interface FingerGuideView : UIView

@property (nonatomic) CGRect frame;

/** 
 Color of the guide geometry.
 */
@property (nonatomic,strong) UIColor *color;
@property FingerState fingerState;
@property float height;
@property float offset;

@end
