#include "SonarMeasureTask.h"

#define D1 1
#define D2 0.4

extern double damLevel;

extern int systemState;

SonarMeasureTask::SonarMeasureTask(Sonar* sonar) {
	this->sonar = sonar;
}

void SonarMeasureTask::init(int period) {
	Task::init(period);
	this->sonar->setSpeedOfSound(25);
}

void SonarMeasureTask::tick() {
	damLevel = this->sonar->getDistance();

  Serial.print("DamLevel: ");
  Serial.println(damLevel);
  
//  damLevel = 0.41;

	if (damLevel > D1) {
		systemState = NORMAL;
	} else if (damLevel < D1 && damLevel > D2) {
		systemState = PRE_ALLARM;
	} else if (damLevel < D2) {
		systemState = ALLARM;
	}

  Serial.print("State: ");
  Serial.println(systemState);
}
