#ifndef __SERVO_MOTOR__
#define __SERVO_MOTOR__

#include "ServoTimer2.h"

class ServoMotor {

	public:
		ServoMotor(int pin);
		void setAngle(int angle);
		void on();
		void off();
		
	private:
		ServoTimer2 motor;
		int pin;

};

#endif
