package seiot.dam_service;

import io.vertx.core.Vertx;

public class Main {

    public static void main(String[] args) throws Exception {
        Dam dam = new Dam();

        DamService damService = new DamService(dam);
        damService.init();

        Vertx vertx = Vertx.vertx();
        HTTPRequestHandler httpRequestHandler = new HTTPRequestHandler(dam);

        vertx.deployVerticle(httpRequestHandler);
        damService.run();
    }

}
