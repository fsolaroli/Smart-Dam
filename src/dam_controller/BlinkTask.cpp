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
	case PRE_ALLARM:
		this->led->switchOff();
		break;
	case ALLARM:
		this->blink();
		break;
	case MANUAL:
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