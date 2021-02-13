#include "Sonar.h"

Sonar::Sonar(int trigPin, int echoPin) {
    this->trigPin = trigPin;
    pinMode(this->trigPin, OUTPUT);
    this->echoPin = echoPin;
    pinMode(this->echoPin, INPUT);
    this->setSpeedOfSound(20.0);
}

void Sonar::setSpeedOfSound(double temperature) {
    this->speedOfSound = 331.45 + 0.62 * temperature;
}

double Sonar::getDistance() {
    /* invio impulso */
    digitalWrite(this->trigPin,LOW);
    delayMicroseconds(3);
    digitalWrite(this->trigPin,HIGH);
    delayMicroseconds(5);
    digitalWrite(this->trigPin,LOW);
    
    /* ricevi l’eco */
    double tUS = pulseIn(this->echoPin, HIGH);
    double t = tUS / 1000.0 / 1000.0 / 2;
    double d = t*this->speedOfSound;
    return d;
}

bool Sonar::detected() {
	return this->getDistance() != 0;
}
