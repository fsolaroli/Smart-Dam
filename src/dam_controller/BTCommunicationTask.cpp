#include "BTCommunicationTask.h"

extern int openPercentage;
extern String damLevel;

extern int systemState;

BTCommunicationTask::BTCommunicationTask() {
}

void BTCommunicationTask::init(int period) {
  	Task::init(period);
  	MsgServiceBT.init();
}

void BTCommunicationTask::tick() {
	if(MsgServiceBT.isMsgAvailable()) {
		Msg* message = MsgServiceBT.receiveMsg();
		if (message != NULL){
			String content = message->getContent();
			
			String state = content.substring(0, content.indexOf(":"));
			if (state == "M") {
				systemState = MANUAL;

				String data = content.substring(content.indexOf(":") + 1);
									
				if (data.substring(0, data.indexOf("=")) == "Pm") {
					openPercentage = data.substring(data.indexOf("=") + 1).toInt();
				}
			} else if (state == "A") {
				systemState = ALLARM;
			}
		}
		delete message;
	}

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
	toSend += ":L=" + damLevel + ",P=" + openPercentage;
	MsgServiceBT.sendMsg(toSend);
}
