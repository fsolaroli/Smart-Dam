#include "ServoMotor.h"

ServoMotor::ServoMotor(int pin) {
	this->pin = pin;
}

void ServoMotor::setAngle(int angle) {
	this->on();
	float coeff = (2250.0-750.0)/180;
	this->motor.write(750 + angle*coeff);
	delay(450);
	this->off();
}

void ServoMotor::on() {
	this->motor.attach(this->pin);
}

void ServoMotor::off() {
	this->motor.detach();
}
