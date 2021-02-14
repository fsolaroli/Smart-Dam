#ifndef __BT_RECEIVE_TASK__
#define __BT_RECEIVE_TASK__

#include "Task.h"
#include "SystemState.h"
#include "ManualState.h"
#include "MsgService.h"

class BTReceiveTask: public Task {

	public:
		BTReceiveTask();
		void init(int period);
		void tick();

};

#endif
