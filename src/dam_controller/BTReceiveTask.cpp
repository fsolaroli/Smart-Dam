#include "BTReceiveTask.h"

extern volatile int openPercentage;
extern String damLevel;

extern volatile int systemState;
extern volatile int manualState;

BTReceiveTask::BTReceiveTask() {
}

void BTReceiveTask::init(int period) {
  	Task::init(period);
  	MsgServiceBT.init();
}

void BTReceiveTask::tick() {
	if(MsgServiceBT.isMsgAvailable()) {
		Msg* message = MsgServiceBT.receiveMsg();
		if (message != NULL){
			String content = message->getContent();
			
			String state = content.substring(0, content.indexOf(":"));
			if (state.equals("M")) {
				manualState = MANUAL;

				String data = content.substring(content.indexOf(":") + 1);

				if (data.substring(0, data.indexOf("=")).equals("Pm")) {
					openPercentage = data.substring(data.indexOf("=") + 1).toInt();
				}
			} else if (state.equals("EM")) {
				manualState = EXIT_MANUAL;
			}
		}
		delete message;
	}
}