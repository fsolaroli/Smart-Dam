package seiot.dam_service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class HTTPRequestHandler extends AbstractVerticle {

    private Dam dam;

    public HTTPRequestHandler(Dam dam) {
        this.dam = dam;
    }

    @Override
    public void start() throws Exception {
        // Create a Router
        Router router = Router.router(vertx);

        // Mount the handler for all incoming requests at every path and HTTP method
        router.route().handler(context -> {
            // Add permission for request methods
            context.response()
                    .putHeader("content-type", "text/plain")
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .putHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .putHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            // Write a json response
            context.json(this.dam.getHTTPRequestData());
        });

        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(router)
                // Start listening
                .listen(8888)
                // Print failure
                .onFailure(data -> System.out.println("Failed"))
                // Print the port
                .onSuccess(server -> System.out.println("HTTP server started on port " + server.actualPort()));
    }
}
