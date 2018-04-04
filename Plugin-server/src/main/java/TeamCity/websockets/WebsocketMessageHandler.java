package TeamCity.websockets;

import com.google.gson.Gson;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;

import java.io.BufferedReader;
import java.io.IOException;


public class WebsocketMessageHandler extends OnMessage<String> {

    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(WebsocketMessageHandler.class.getName());

    private Gson gson = new Gson();

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {
        AtmosphereResponse atmosphereResponse = resource.getResponse();
        BufferedReader bufferedReader = resource.getRequest().getReader();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            Log.info("MESSAGE : " + line);
            atmosphereResponse.write(gson.toJson(line));
        }

    }

    /**
     * Does not need to handle this because we are pushing data to client.
     */

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {

    }


}

