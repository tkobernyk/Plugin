package TeamCity.websockets;

import com.google.gson.Gson;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.handler.OnMessage;

import java.io.IOException;


public class WebsocketMessageHandler extends OnMessage<String> {

    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(WebsocketMessageHandler.class.getName());

    private Gson gson = new Gson();

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        Log.info("MESSAGE -----> " + message);
        response.write(gson.toJson(message));
    }
}

