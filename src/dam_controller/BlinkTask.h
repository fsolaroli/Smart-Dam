#ifndef __BLINK_TASK__
#define __BLINK_TASK__

#include "Task.h"
#include "Led.h"
#include "SystemState.h"
#include "ManualState.h"

class BlinkTask: public Task {

	public:
		BlinkTask(Led* led);  
		void init(int period);  
		void tick();

	private:
		Led* led;
		enum { ON, OFF} state;
		void blink();

};

#endif

