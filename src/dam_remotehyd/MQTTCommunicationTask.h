#ifndef __SERIAL_COMMUNICATION_TASK__
#define __SERIAL_COMMUNICATION_TASK__

#include "Task.h"
#include "SystemState.h"
#include <string.h>

class MQTTCommunicationTask: public Task {

	public:
		MQTTCommunicationTask();
		void init(int period);
		void tick();

};

#endif
