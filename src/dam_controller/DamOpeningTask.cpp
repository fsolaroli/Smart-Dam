#include "DamOpeningTask.h"

extern volatile int openPercentage;

extern volatile int systemState;

DamOpeningTask::DamOpeningTask(ServoMotor* servoMotor) {
	this->servoMotor = servoMotor;
	this->currentOpenPercentage = -1;
}

void DamOpeningTask::init(int period) {
	Task::init(period);
}

void DamOpeningTask::tick() {
	if (openPercentage != currentOpenPercentage) {
		this->servoMotor->setAngle(180*openPercentage/100);
		currentOpenPercentage = openPercentage;
	}
}
