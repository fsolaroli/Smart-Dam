#include "BlinkTask.h" 
#include "BTCommunicationTask.h"
#include "DamOpeningTask.h"
#include "Led.h"
#include "Scheduler.h"
#include "SerialCommunicationTask.h"
#include "ServoMotor.h"
#include "SystemState.h"

#define GREEN_LED_PIN 11
#define SERVO_PIN 3

int openPercentage;
String damLevel;

int systemState;

Scheduler scheduler;

ServoMotor* servoMotor = new ServoMotor(SERVO_PIN);

void setup() {
	systemState = NORMAL;
	openPercentage = 0;
	damLevel = "0";

	Led* greenLed = new Led(GREEN_LED_PIN);
	BlinkTask* blinkTask = new BlinkTask(greenLed);
	blinkTask->init(1000);

	// ServoMotor* servoMotor = new ServoMotor(SERVO_PIN);
	DamOpeningTask* damOpeningTask = new DamOpeningTask(servoMotor);
	damOpeningTask->init(500);

	BTCommunicationTask* btCommunicationTask = new BTCommunicationTask();
	btCommunicationTask->init(3000);

	SerialCommunicationTask* serialCommunicationTask = new SerialCommunicationTask();
	serialCommunicationTask->init(3000);

	scheduler.init(500);

	scheduler.addTask(btCommunicationTask);
	scheduler.addTask(serialCommunicationTask);
	scheduler.addTask(damOpeningTask);
	scheduler.addTask(blinkTask);

}

void loop() {

	scheduler.schedule();

}
