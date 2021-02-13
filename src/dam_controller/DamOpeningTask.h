#ifndef __TACHIMETER_TASK__
#define __TACHIMETER_TASK__

#include "Task.h"
#include "ServoMotor.h"
#include "SystemState.h"

class DamOpeningTask: public Task {

	public:
		DamOpeningTask(ServoMotor* servoMotor);
		void init(int period);
		void tick();

	private:
		ServoMotor* servoMotor;
		int currentOpenPercentage;

};

#endif