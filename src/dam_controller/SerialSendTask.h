#ifndef __SERIAL_SEND_TASK__
#define __SERIAL_SEND_TASK__

#include "Task.h"
#include "SystemState.h"
#include "ManualState.h"
#include "MsgService.h"
#include "Arduino.h"

class SerialSendTask: public Task {

	public:
		SerialSendTask();
		void init(int period);
		void tick();

};

#endif
