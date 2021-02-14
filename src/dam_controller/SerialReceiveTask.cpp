#include "SerialReceiveTask.h"

extern volatile int openPercentage;
extern String damLevel;

extern volatile int systemState;

SerialReceiveTask::SerialReceiveTask() {
}

void SerialReceiveTask::init(int period) {
  	Task::init(period);
  	MsgService.init(); 
}

void SerialReceiveTask::tick() {
	if(MsgService.isMsgAvailable()) {
		Msg* message = MsgService.receiveMsg();
		String content = message->getContent();
		
		if(systemState != MANUAL) {
			String state = content.substring(0, content.indexOf(":"));
			if (state.equals("N")) {
				systemState = NORMAL;
				openPercentage = 0;
			} else if (state.equals("P")) {
				systemState = PRE_ALLARM;				
			} else if (state.equals("A")) {
				systemState = ALLARM;
			} else if (state.equals("M")) {
				systemState = MANUAL;
			}
		}
		
		if(content.indexOf(":") != content.length() - 1) {
			String data = content.substring(content.indexOf(":") + 1);
			
			String level = data.substring(0, data.indexOf(","));
			if (level.substring(0, level.indexOf("=")).equals("L")) {
				damLevel = level.substring(level.indexOf("=") + 1);
			}

			if(data.indexOf(",") != data.length() - 1) {
				String percentage = data.substring(data.indexOf(",") + 1);
				if (systemState != MANUAL && percentage.substring(0, percentage.indexOf("=")).equals("P")) {
					openPercentage = percentage.substring(percentage.indexOf("=") + 1).toInt();
				}
			}
		}

		delete message;		
	}
}
