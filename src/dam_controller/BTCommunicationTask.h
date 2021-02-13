#ifndef __BT_COMMUNICATION_TASK__
#define __BT_COMMUNICATION_TASK__

#include "Task.h"
#include "SystemState.h"
#include "MsgService.h"

class BTCommunicationTask: public Task {

	public:
		BTCommunicationTask();
		void init(int period);
		void tick();

};

#endif
