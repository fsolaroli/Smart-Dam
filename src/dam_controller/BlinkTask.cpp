#include "BlinkTask.h"

extern volatile int systemState;
extern volatile int manualState;

BlinkTask::BlinkTask(Led* led) {
	this->led = led;    
}

void BlinkTask::init(int period) {
	Task::init(period);
	state = OFF;
}

void BlinkTask::tick() {
	if (manualState != AUTO) {
		this->led->switchOn();
	} else {
		switch (systemState) {
		case NORMAL:
		case PRE_ALLARM:
			this->led->switchOff();
			break;
		case ALLARM:
			this->blink();
			break;
		}
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