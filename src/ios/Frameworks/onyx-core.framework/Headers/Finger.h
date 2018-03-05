/*
 * Finger.h
 *
 *  Created on: Jul 11, 2014
 *      Author: wlucas
 */

#ifndef FINGER_H_
#define FINGER_H_

#include <vector>

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

namespace dft
{

typedef std::vector<cv::Point> Contour;
typedef std::vector<Contour> ContourVector;

typedef cv::Vec3f Circle;
typedef std::vector<Circle> CircleVector;

/// This structure defines the POD for fill properties
struct FillProperties
{
	double heightRatio;
	double overlapRatio;

	FillProperties()
	: heightRatio(0.0), overlapRatio(0.0)
	{}
};

/// This class defines the physical representation of the finger in an image.
class Finger
{
public:
	/// Constructs a default Finger object.
	Finger()
	: circle(-1.0f, -1.0f, -1.0f)
	{}

	/// Constructs a Finger object from given contour and circle.
	/// \param[in] fingerContour a Contour specifying the points of the Finger.
	/// \param[in] circle the Circle defining the tip region of the Finger.
	Finger(const Contour& fingerContour, const Circle& circle)
	: fingerContour(fingerContour), circle(circle)
	{}

	Finger(const Finger& other)
		: fingerContour(other.fingerContour), circle(other.circle)
	{}

	/// This member function determines if the Finger object is valid.
	/// \return true if the Finger object is valid, otherwise false.
	bool isValid() const
	{
		return !fingerContour.empty() && tip().x >= 0.0f && tip().y >= 0.0f;
	}

	/// This member function returns the Contour defining the Finger region.
	/// \return a Contour of the physical finger in the image.
	Contour getFingerContour() const
	{
		return fingerContour;
	}

	/// This member function returns the mask of the Finger region.
	/// \param canvasSize the size of the canvas to draw the contour on.
	/// \return the mask containing the contour.
	cv::Mat getFingerMask(const cv::Size& canvasSize) const
	{
		cv::Mat mask = cv::Mat::zeros(canvasSize, CV_8UC1);
		cv::drawContours(mask, ContourVector(1, fingerContour), 0, cv::Scalar(255, 255, 255), CV_FILLED);

		return mask;
	}

	/// This member function returns the Circle encompassing the finger-tip region.
	/// \return a circle defining the finger-tip region.
	Circle getCircle() const
	{
		return circle;
	}

	/// This member function returns the finger-tip (x,y) location.
	/// \return the detected finger-tip location in (x,y) coordinates. (-1, -1) indicates no detection.
	cv::Point2f tip() const
	{
		return cv::Point2f(circle[0], circle[1]);
	}

	/// This member function returns the amount over over/under-fill the finger contour is filling the rectangle.
	/// \return the FillProperties of the Finger relative to the capture rectangle.
	/// \see FillProperties
	FillProperties getFillProperties(const cv::Rect& r);

private:
	Contour fingerContour;
	Circle circle;
	cv::Rect fingerRect;
};

}

#endif /* FINGER_H_ */
