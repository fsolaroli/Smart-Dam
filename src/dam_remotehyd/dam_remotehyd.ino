#include "SystemState.h"
#include "BlinkTask.h"
#include "MQTTCommunicationTask.h"
#include "SonarMeasureTask.h"
#include "Scheduler.h"

#define GREEN_LED_PIN 4
#define SONAR_ECHO_PIN 0
#define SONAR_TRIG_PIN 2

// System variables

double damLevel;

int systemState;

Scheduler scheduler;

void setup() {
  	Serial.begin(115200);
  
	systemState = NORMAL;

	Sonar* sonar = new Sonar(SONAR_TRIG_PIN, SONAR_ECHO_PIN);
	SonarMeasureTask* sonarMeasureTask = new SonarMeasureTask(sonar);
	sonarMeasureTask->init(1000);

	Led* greenLed = new Led(GREEN_LED_PIN);
	BlinkTask* blinkTask = new BlinkTask(greenLed);
	blinkTask->init(1000);

	MQTTCommunicationTask* mqttCommunicationTask = new MQTTCommunicationTask();
	mqttCommunicationTask->init(10000l);

	scheduler.init(500);

	scheduler.addTask(sonarMeasureTask);
	scheduler.addTask(blinkTask);
	scheduler.addTask(mqttCommunicationTask);
}

void loop() {

	scheduler.schedule();

}
