#ifndef FINGER_DETECT_H_
#define FINGER_DETECT_H_

#include <vector>
#include <opencv2/core/core.hpp>

#include "dft/Finger.h"

namespace dft
{

Finger findFingerImpl(const cv::Mat& src);

}

#endif
