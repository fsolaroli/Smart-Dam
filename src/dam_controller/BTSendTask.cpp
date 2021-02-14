#include "BTSendTask.h"

extern volatile int openPercentage;
extern String damLevel;

extern String message;

extern volatile int systemState;

BTSendTask::BTSendTask() {
}

void BTSendTask::init(int period) {
  	Task::init(period);
  	MsgServiceBT.init();
}

void BTSendTask::tick() {
	MsgServiceBT.sendMsg(message);
}