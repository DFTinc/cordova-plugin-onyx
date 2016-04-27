/// \file Version.h
/// \copyright Copyright 2014 Diamond Fortress Technologies, Inc. All rights reserved.

#ifndef VERSION_H_
#define VERSION_H_

#include <string>

#define API_MAJOR 4
#define API_MINOR 1
#define API_PATCH 0

namespace dft
{
	/// \brief This class provides API version information.
	class Version
	{
	public:
		/// This member function returns the major version number.
		/// \return the major version number.
		static int getMajor();

		/// This member function returns the minor version number.
		/// \return the minor version number.
		static int getMinor();

		/// This member function returns the patch version number.
		/// \return the patch version number.
		static int getPatch();

		/// This member function returns the version string.
		/// \return the version string formatted as MAJOR.MINOR.PATCH .
		static std::string getVersion();

		/// This member function checks if the current API is at least the queried version.
		/// \param major the queried major version number.
		/// \param minor the queried minor version number.
		/// \param patch the queried patch version number.
		/// \return true if the current version is >= the queried version, otherwise false.
		static bool isAtLeast(int major, int minor, int patch);

		/// This member function checks if the current API supplies a certain feature.
		/// \param name a string specifying a particular feature.
		/// \return true if the feature is available, otherwise false.
		static bool hasFeature(const std::string& name);
	};
}

#endif
