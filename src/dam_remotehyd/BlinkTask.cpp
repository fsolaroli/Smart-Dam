#include "BlinkTask.h"

extern int systemState;

BlinkTask::BlinkTask(Led* led) {
	this->led = led;    
}

void BlinkTask::init(int period) {
	Task::init(period);
	state = OFF;
}

void BlinkTask::tick() {
	switch (systemState) {
	case NORMAL:
		this->led->switchOff();
		break;
	case PRE_ALLARM:
		this->blink();
		break;
	case ALLARM:
		this->led->switchOn();
		break;
	}
}

void BlinkTask::blink() {
	switch (state){
		case OFF:
			this->led->switchOn();
			state = ON; 
		break;
		case ON:
			this->led->switchOff();
			state = OFF;
		break;
	}
}