#ifndef __SONAR__
#define __SONAR__

#include "Arduino.h"

class Sonar {

	public:
		Sonar(int trigPin, int echoPin);
		void setSpeedOfSound(double temperature);
		double getDistance();
		bool detected();

	private:
		int trigPin, echoPin;
		double speedOfSound;

};

#endif
