/// \file FingerprintTemplate.h
/// \copyright Copyright 2014 Diamond Fortress Technologies, Inc. All rights reserved.
/// \author Will Lucas

#ifndef FINGERPRINTTEMPLATE_H_
#define FINGERPRINTTEMPLATE_H_

/// \namespace dft Top-level Diamond Fortress Technologies namespace.
namespace dft
{

/// \brief This class represents a fingerprint template.
class FingerprintTemplate
{
public:
	/// \brief This enumeration defines the usable fingerprint locations.
	enum FingerLocation
	{
		LEFT_THUMB = 0,
		LEFT_INDEX,
		LEFT_MIDDLE,
		LEFT_RING,
		LEFT_LITTLE,

		RIGHT_THUMB,
		RIGHT_INDEX,
		RIGHT_MIDDLE,
		RIGHT_RING,
		RIGHT_LITTLE,

		UNKNOWN
	};

	/// Constructs an empty fingerprint template.
	FingerprintTemplate();

	/// Constructs a fingerprint template using the data and location.
	/// \param data a raw array of bytes describing the fingerprint template.
	/// \param size the size of the template data in bytes.
	/// \param quality the quality of the template.
	/// \param location the particular finger to be tracked (left index, right middle, etc.)
	FingerprintTemplate(const char* data, int size, int quality, FingerLocation location = UNKNOWN);
	FingerprintTemplate(const FingerprintTemplate& other);
	FingerprintTemplate& operator= (const FingerprintTemplate& other);
	virtual ~FingerprintTemplate();

	/// This member function returns the raw template data.
	/// \return a byte pointer containing the raw template data.
	const char* getData() const;

	/// This member function returns the size of the template data in bytes.
	/// \return the size of the template data in bytes.
	int getSize() const;

	/// This member function returns the finger location (default = FingerLocation::UNKOWN).
	/// \return the finger location.
	FingerLocation getFingerLocation() const;

	/// This member function sets the template's finger location.
	/// \param location the FingerLocation enumeration value specifying the finger location.
	void setFingerLocation(const FingerLocation& location);

	/// This member function returns the quality of the fingerprint template.
	/// \return the fingerprint template quality.
	int getQuality() const;

	/// This member function determines if the FingerprintTemplate is empty.
	/// \return true if the FingerprintTemplate is empty, false otherwise.
	bool isEmpty() const;

private:
	char* data;
	int size;
	FingerLocation location;
	int quality;
};

}

#endif
