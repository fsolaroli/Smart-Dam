#ifndef __SERIAL_RECEIVE_TASK__
#define __SERIAL_RECEIVE_TASK__

#include "Task.h"
#include "SystemState.h"
#include "MsgService.h"
#include "Arduino.h"

class SerialReceiveTask: public Task {

	public:
		SerialReceiveTask();
		void init(int period);
		void tick();

};

#endif
