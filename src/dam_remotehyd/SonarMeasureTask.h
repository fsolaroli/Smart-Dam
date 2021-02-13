#ifndef __SONAR_MEASURE_TASK__
#define __SONAR_MEASURE_TASK__

#include "Task.h"
#include "Sonar.h"
#include "SystemState.h"
#include "Arduino.h"

class SonarMeasureTask: public Task {

	public:
		SonarMeasureTask(Sonar* sonar);
		void init(int period);
		void tick();

	private:
		Sonar* sonar;

};

#endif
