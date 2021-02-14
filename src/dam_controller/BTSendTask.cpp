#include "BTSendTask.h"

extern volatile int openPercentage;
extern String damLevel;

extern volatile int systemState;

BTSendTask::BTSendTask() {
}

void BTSendTask::init(int period) {
  	Task::init(period);
  	MsgServiceBT.init();
}

void BTSendTask::tick() {
	String toSend = "";
	switch (systemState) {
		case NORMAL:
			toSend += "N";
			break;
		case PRE_ALLARM:
			toSend += "P";
			break;
		case ALLARM:
			toSend += "A";
			break;
		case MANUAL:
			toSend += "M";
			break;
	}
	toSend += ":L=" + damLevel + ",P=" + String(openPercentage);
	MsgServiceBT.sendMsg(toSend);
}