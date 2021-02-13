#ifndef __SCHEDULER__
#define __SCHEDULER__

#include "Task.h"
#include "Arduino.h"

#define TIMER_INTERRUPT_DEBUG 0
#define _TIMERINTERRUPT_LOGLEVEL_ 0

#include <ESP8266TimerInterrupt.h>

#define MAX_TASKS 50

class Scheduler {
  
  int basePeriod;
  int nTasks;
  Task* taskList[MAX_TASKS];  

public:
  void init(int basePeriod);  
  virtual bool addTask(Task* task);  
  virtual void schedule();
};

#endif
