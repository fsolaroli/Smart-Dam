#include "BlinkTask.h" 
#include "DamOpeningTask.h"

#include "BTReceiveTask.h"
#include "BTSendTask.h"

#include "SerialReceiveTask.h"
#include "SerialSendTask.h"

#include "Led.h"
#include "Scheduler.h"
#include "ServoMotor.h"
#include "SystemState.h"

#define GREEN_LED_PIN 11
#define SERVO_PIN 3

volatile int openPercentage;
String damLevel;

volatile int systemState;

Scheduler scheduler;

void setup() {
	systemState = NORMAL;
	openPercentage = 0;
	damLevel = "0";

	Led* greenLed = new Led(GREEN_LED_PIN);
	BlinkTask* blinkTask = new BlinkTask(greenLed);
	blinkTask->init(1000);

	ServoMotor* servoMotor = new ServoMotor(SERVO_PIN);
	DamOpeningTask* damOpeningTask = new DamOpeningTask(servoMotor);
	damOpeningTask->init(1000);

	BTReceiveTask* btReceiveTask = new BTReceiveTask();
	btReceiveTask->init(1000);

	BTSendTask* btSendTask = new BTSendTask();
	btSendTask->init(5000);

	SerialReceiveTask* serialReceiveTask = new SerialReceiveTask();
	serialReceiveTask->init(5000);

	SerialSendTask* serialSendTask = new SerialSendTask();
	serialSendTask->init(1000);

	scheduler.init(1000);

	scheduler.addTask(btReceiveTask);
	scheduler.addTask(serialSendTask);
	scheduler.addTask(serialReceiveTask);
	scheduler.addTask(btSendTask);

	scheduler.addTask(blinkTask);
	scheduler.addTask(damOpeningTask);

}

void loop() {

	scheduler.schedule();

}
