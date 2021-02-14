#ifndef __BT_SEND_TASK__
#define __BT_SEND_TASK__

#include "Task.h"
#include "SystemState.h"
#include "MsgService.h"

class BTSendTask: public Task {

	public:
		BTSendTask();
		void init(int period);
		void tick();

};

#endif
