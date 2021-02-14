#include "SerialSendTask.h"

extern volatile int openPercentage;

extern volatile int systemState;
extern volatile int manualState;

SerialSendTask::SerialSendTask() {
}

void SerialSendTask::init(int period) {
  	Task::init(period);
  	MsgService.init(); 
}

void SerialSendTask::tick() {
	switch (manualState) {
	case MANUAL:
		MsgService.sendMsg(String("M:Pm=") + String(openPercentage));
		break;
	case EXIT_MANUAL:
		manualState = AUTO;
		MsgService.sendMsg(String("EM:"));
		break;
	default:
		break;
	}
}
