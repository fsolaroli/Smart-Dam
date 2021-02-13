#ifndef __SERIAL_COMMUNICATION_TASK__
#define __SERIAL_COMMUNICATION_TASK__

#include "Task.h"
#include "SystemState.h"
#include "MsgService.h"
#include "Arduino.h"

class SerialCommunicationTask: public Task {

	public:
		SerialCommunicationTask();
		void init(int period);
		void tick();

};

#endif
