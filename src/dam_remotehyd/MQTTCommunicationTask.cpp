#include "MQTTCommunicationTask.h"
#include <ESP8266WiFi.h>
#include <PubSubClient.h>

#define MSG_BUFFER_SIZE  (50)

const char* ssid = "home";
const char* password = "trustno1";
const char* mqtt_server = "192.168.178.25";

WiFiClient espClient;
PubSubClient client(espClient);
char msg[MSG_BUFFER_SIZE];

extern double damLevel;

extern int systemState;

MQTTCommunicationTask::MQTTCommunicationTask() {
}

void setup_wifi() {
	delay(10);
	// We start by connecting to a WiFi network
	Serial.println();
	Serial.print("Connecting to ");
	Serial.println(ssid);

	WiFi.mode(WIFI_STA);
	WiFi.begin(ssid, password);

	while (WiFi.status() != WL_CONNECTED) {
		delay(500);
		Serial.print(".");
	}

	randomSeed(micros());

	Serial.println("");
	Serial.println("WiFi connected");
	Serial.println("IP address: ");
	Serial.println(WiFi.localIP());
}

void MQTTCommunicationTask::init(int period) {
  	setup_wifi();
	Task::init(period);
  	client.setServer(mqtt_server, 1883);
}

void reconnect() {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    // Attempt to connect
    if (client.connect(clientId.c_str())) {
		Serial.println("connected");
    } else {
		Serial.print("failed, rc=");
		Serial.print(client.state());
	}
}

void MQTTCommunicationTask::tick() {
  client.loop();
  
  if (!client.connected()) {
    reconnect();
  } else {
    switch(systemState) {
      case NORMAL:
        snprintf (msg, MSG_BUFFER_SIZE, "N");
        break;
      case PRE_ALLARM: 
        snprintf (msg, MSG_BUFFER_SIZE, "P:L=%f", damLevel);
        break;
      case ALLARM:
        snprintf (msg, MSG_BUFFER_SIZE, "A:L=%f", damLevel);
        break;
    }
    client.publish("outTopic", msg);
  }
}
