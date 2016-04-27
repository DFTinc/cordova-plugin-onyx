/// \file core.h
/// \brief This file defines the base functionality of the onyx-core SDK.
/// \copyright Copyright 2014 Diamond Fortress Technologies, Inc. All rights reserved.
/// \author Will Lucas

#ifndef CORE_H_
#define CORE_H_

#include "Finger.h"
#include "FingerprintTemplate.h"
#include "MatchResult.h"
#include <opencv2/core/core.hpp>

/// \namespace dft Top-level Diamond Fortress Technologies namespace.
namespace dft
{

/// This function preprocesses a fingerprint image for use with generateFingerprintTemplate.
/// \param[in] src an 8-bit grayscale fingerprint image to be preprocessed.
/// \param[out] dst an 8-bit grayscale preprocessed fingerprint image.
/// \param[in] sigma1 amount of blur for the mean calculation (default = 2)
/// \param[in] sigma2 amount of blur for the standard deviation (default = 1)
/// \return an image quality measure, a value greater than or equal to 60 is good.
/// \throws DftException if an error occurs.
/// \see generateFingerprintTemplate
double preprocessFingerprint(const cv::Mat& src, cv::Mat& dst, double sigma1 = 2.0, double sigma2 = 1.0);

/// This function enhances a fingerprint image for use with generateFingerprintTemplate.
/// \param[in] src an 8-bit grayscale image that has been preprocessed first.
/// \param[out] enhanced an 8-bit grayscale image that has been enhanced.
/// \param[out] energyMask an 8-bit binary mask for quality filtering purposes.
/// \throws DftException if an error occurs.
/// \see preprocessFingerprint
void enhanceFingerprint(const cv::Mat& src, cv::Mat& enhanced, cv::Mat& energyMask);

/// This function generates a fingerprint template from a preprocessed image.
/// \param[in] src an 8-bit grayscale preprocessed fingerprint image.
/// \param[in] mask an 8-bit mask used to filter the detected feature locations.
/// \return a pointer to a FingerprintTemplate.
/// \note It is up to the client to manage the returned pointer.
/// \throws DftException if an error occurs.
/// \see FingerprintTemplate
FingerprintTemplate* generateFingerprintTemplate(const cv::Mat& src, const cv::Mat& mask = cv::Mat());

/// This function performs a 1:1 match on two fingerprint templates.
/// \param[in] reference the first fingerprint template to verify with.
/// \param[in] probe the second fingerprint template to verify with.
/// \return the match score between [0, 1]. Higher is better.
/// \throws DftException if an error occurs.
/// \see FingerprintTemplate
float verify(const FingerprintTemplate& reference, const FingerprintTemplate& probe);

/// This function performs a 1:N match on templates supplied in the gallery.
/// \param[in] gallery a vector of FingerprintTemplates.
/// \param[in] probe a single FingerprintTemplate to match against the gallery.
/// \return the MatchResult containing the top matching score as well as the matched index in the gallery.
/// \note All matches are performed in parallel.
/// \throws DftException if an error occurs.
/// \see FingerprintTemplate
/// \see MatchResult
MatchResult identify(const std::vector<FingerprintTemplate>& gallery, const FingerprintTemplate& probe);

/// This member locates a fingertip in a grayscale image.
/// \param[in] src the 8-bit grayscale image possibly containing a finger.
/// \return a Finger object containing the location of the finger as well as its contour.
/// \throws DftException if an error occurs.
Finger findFinger(const cv::Mat& src);

/// This member function analyzes the fingertip focus.
/// \param[in] src the 8-bit grayscale image to analyze for focus.
/// \param[in] finger a Finger object describing the detected finger.
/// \return a focus measure from [0, 1]. 0 being the worst and 1 being the best.
/// \throws DftException if an error occurs.
/// \see findFinger
double focusMeasure(const cv::Mat& src, const Finger& finger);
}

#endif /* CORE_H_ */
