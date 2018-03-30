package TeamCity.Controllers;

import org.atmosphere.cpr.AtmosphereServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Created by Mykhailo_Moskura on 3/30/2018.
 */
@WebServlet("/websocketHandler/*")
public class WebSocketListener extends AtmosphereServlet {
}
