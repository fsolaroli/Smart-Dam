#include "SerialCommunicationTask.h"

extern int openPercentage;
extern String damLevel;

extern int systemState;

SerialCommunicationTask::SerialCommunicationTask() {
}

void SerialCommunicationTask::init(int period) {
  	Task::init(period);
  	MsgService.init(); 
}

void SerialCommunicationTask::tick() {
	if (systemState == MANUAL) {
		MsgService.sendMsg("M:Pm=" + openPercentage);
	}

	if(MsgService.isMsgAvailable()) {
		Msg* message = MsgService.receiveMsg();
		String content = message->getContent();
		
		String state = content.substring(0, content.indexOf(":"));
		if(systemState != MANUAL) {
			if (state == "N") {
				systemState = NORMAL;
				openPercentage = 0;
			} else if (state == "P") {
				systemState = PRE_ALLARM;				
			} else if (state == "A") {
				systemState = ALLARM;
			}
		}
		
		if(content.indexOf(":") != content.length() - 1) {
			String data = content.substring(content.indexOf(":") + 1);
			
			String level = data.substring(0, data.indexOf(","));
			if (level.substring(0, level.indexOf("=")) == "L") {
				damLevel = level.substring(level.indexOf("=") + 1);
			}

			if(data.indexOf(",") != data.length() - 1) {
				String percentage = data.substring(data.indexOf(",") + 1);
				if (percentage.substring(0, percentage.indexOf("=")) == "P" && systemState != MANUAL) {
					openPercentage = percentage.substring(percentage.indexOf("=") + 1).toInt();
				}	
			}
		}

		delete message;
	}
}
