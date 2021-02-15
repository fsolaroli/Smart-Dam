#include "SonarMeasureTask.h"

#define D1 1
#define D2 0.4

#define PERIOD1 10000l
#define PERIOD2 5000l

extern double damLevel;

extern int systemState;

SonarMeasureTask::SonarMeasureTask(Sonar* sonar, MQTTCommunicationTask* mqttCommunicationTask) {
	this->sonar = sonar;
	this->mqttCommunicationTask = mqttCommunicationTask;
}

void SonarMeasureTask::init(int period) {
	Task::init(period);
	this->sonar->setSpeedOfSound(25);
}

void SonarMeasureTask::tick() {
	damLevel = this->sonar->getDistance();

	if (damLevel > D1) {
		systemState = NORMAL;
		this->mqttCommunicationTask->changePeriod(PERIOD1);
	} else if (damLevel < D1 && damLevel > D2) {
		systemState = PRE_ALLARM;
		this->mqttCommunicationTask->changePeriod(PERIOD1);
	} else if (damLevel < D2) {
		systemState = ALLARM;
		this->mqttCommunicationTask->changePeriod(PERIOD2);
	}

#ifdef DEBUG
	Serial.print("DamLevel: ");
	Serial.println(damLevel);
	Serial.print("State: ");
	Serial.println(systemState);
#endif

}
