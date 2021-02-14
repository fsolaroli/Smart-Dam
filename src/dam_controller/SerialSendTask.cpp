#include "SerialSendTask.h"

extern volatile int openPercentage;

extern volatile int systemState;

SerialSendTask::SerialSendTask() {
}

void SerialSendTask::init(int period) {
  	Task::init(period);
  	MsgService.init(); 
}

void SerialSendTask::tick() {
	if (systemState == MANUAL) {
		String toSend = "M:Pm=" + String(openPercentage);
		MsgService.sendMsg(toSend);
	}
}
