package seiot.dam_service;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Vertx;
import io.vertx.mqtt.MqttServer;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static seiot.dam_service.DamSystemState.*;

public class DamService {

//    private static final double D1 = 1;
    private static final double D2 = 0.4;
    private static final double DeltaD = 0.04;

    private Dam dam;

    private CommChannel serialChannel;

    private Vertx vertx;
    private static MqttServer mqttServer;

    public DamService(Dam dam) {
        this.dam = dam;
    }

    public void init() throws Exception {
         mqttInit();
         commChannelInit();
    }

    private void commChannelInit() throws Exception {
        String serialPortName = "/dev/cu.usbmodem14201";
        serialChannel = new ExtendedSerialCommChannel(serialPortName, 9600, 80);
    }

    private void mqttInit() {
        vertx = Vertx.vertx();
        mqttServer = MqttServer.create(vertx);
    }

    private void mqttConnection() {
        mqttServer.endpointHandler(endpoint -> {

            endpoint.accept(true);

            endpoint.publishHandler(message -> {

                String messageContent = message.payload().toString(Charset.defaultCharset());

                System.out.println(messageContent);

                parseESPMessage(messageContent);

                if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
                    endpoint.publishAcknowledge(message.messageId());
                } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
                    endpoint.publishReceived(message.messageId());
                }

            }).publishReleaseHandler(messageId -> {
                endpoint.publishComplete(messageId);
            });
        })
        .listen(ar -> {
            if (ar.succeeded()) {

                System.out.println("MQTT server is listening on port " + ar.result().actualPort());
            } else {

                System.out.println("Error on starting the server");
                ar.cause().printStackTrace();
            }
        });
    }

    private void parseArduinoMessage(String message) {
        String mode = message.split(":")[0];
        if (mode == "M") {
            dam.setStatus(MANUAL);
        }

        if (message.split(":").length > 1) {
            String data = message.split(":")[1];
            String dataType = data.split("=")[0];
            String dataValue = data.split("=")[1];
            if (dataType == "Pm") {
                this.dam.setDamOpenPercentage(Integer.parseInt(dataValue));
            }
        }
    }

    private int getDamOpening(double distance) {
        int damOpening;

        if (distance > D2) {
            damOpening = 0;
        } else if (distance > (D2 - 1 * DeltaD)) {
            damOpening = 20;
        } else if (distance > (D2 - 2 * DeltaD)) {
            damOpening = 40;
        } else if (distance > (D2 - 3 * DeltaD)) {
            damOpening = 60;
        } else if (distance > (D2 - 4 * DeltaD)) {
            damOpening = 80;
        } else {
            damOpening = 100;
        }
        
        return damOpening;
    }

    private void parseESPMessage(String message) {
        String arduinoMessage = "";

        String mode = message.split(":")[0];
        if (this.dam.getStatus() != MANUAL) {
            switch (mode) {
                case "N" :
                    dam.setStatus(NORMAL);
                    break;
                case "P" :
                    dam.setStatus(PRE_ALLARM);
                    break;
                case "A" :
                    dam.setStatus(ALLARM);
                    break;
            }
        }
        arduinoMessage += (mode + ":");

        if (message.split(":").length > 1) {

            String data = message.split(":")[1];

            String dataType = data.split("=")[0];
            String dataValue = data.split("=")[1];
            if (dataType == "L") {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).split("\\.")[0];
                double distance = Double.parseDouble(dataValue);
                this.dam.addDamLevel(Pair.of(timestamp, distance));
                arduinoMessage += ("L=" + distance + ",");

                if(this.dam.getStatus() != MANUAL) {
                    this.dam.setDamOpenPercentage(this.getDamOpening(distance));
                    arduinoMessage += ("P=" + this.dam.getDamOpenPercentage());
                }
            }
        } else {
            this.dam.setDamOpenPercentage(0);
        }

        serialChannel.sendMsg(arduinoMessage);
//        System.out.println("Sending to Arduino: '" + arduinoMessage + "'");

    }

    public void run() throws Exception {
        mqttConnection();
        while(true) {
            if(this.dam.getStatus() == MANUAL && serialChannel.isMsgAvailable()) {
                String message = serialChannel.receiveMsg();
                System.out.println("From Arduino: " + message);
                parseESPMessage(message);
            }
        }

    }
}
