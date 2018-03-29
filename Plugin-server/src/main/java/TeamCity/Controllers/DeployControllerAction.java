package TeamCity.Controllers;

import TeamCity.Helpers.PowerShellRunner;
import TeamCity.Models.Deploy;
import TeamCity.websocket.BrowserNotificationHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jetbrains.buildServer.controllers.BaseAjaxActionController;
import jetbrains.buildServer.web.openapi.ControllerAction;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereInterceptor;
import org.atmosphere.cpr.AtmosphereRequest;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.atmosphere.interceptor.IdleResourceInterceptor;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class DeployControllerAction extends BaseAjaxActionController implements ControllerAction {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());
    private final PluginDescriptor _descriptor;
    private final AtmosphereFramework atmosphereFramework;
    private static final String URL_PATH = "/deploy/run.html";
    private static final String ASYNC_SUPPORTED = "org.apache.catalina.ASYNC_SUPPORTED";

    public DeployControllerAction(@NotNull WebControllerManager controllerManager, @NotNull PluginDescriptor descriptor, @NotNull BrowserNotificationHandler browserNotificationHandler) {
        super(controllerManager);
        controllerManager.registerController("/deploy/run.html", this);
        atmosphereFramework = createAtmosphereFramework(browserNotificationHandler);
        registerAction(this);
        _descriptor = descriptor;
    }

    @Override
    public boolean canProcess(@NotNull HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    @Override
    public void process(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @Nullable Element ajaxResponse) {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            Deploy deploy = getDeploy(request);
            String psScriptPath = request.getServletContext().getRealPath(_descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
            Log.info(psScriptPath);
            String results = PowerShellRunner.Run(psScriptPath, deploy);
            request.setAttribute(ASYNC_SUPPORTED, Boolean.TRUE);
            atmosphereFramework.doCometSupport(AtmosphereRequest.wrap(request), AtmosphereResponse.wrap(response).write(results.getBytes()));
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.BuildId = request.getParameter("BuildId");
        deploy.ProjectName = request.getParameter("ProjectName");
        deploy.Environment = request.getParameter("Environment");
        deploy.Phase = request.getParameter("Phase");
        Log.info("Deploy: " + deploy.toString());
        return deploy;
    }

    private AtmosphereFramework createAtmosphereFramework(@NotNull BrowserNotificationHandler handler) {
        AtmosphereFramework atmosphereFramework = new AtmosphereFramework();

        List<AtmosphereInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new AtmosphereResourceLifecycleInterceptor());
        interceptors.add(new HeartbeatInterceptor());
        interceptors.add(new IdleResourceInterceptor());
        interceptors.add(new TrackMessageSizeInterceptor());
        interceptors.add(new BroadcastOnPostAtmosphereInterceptor());

        atmosphereFramework.getBroadcasterFactory().lookup(URL_PATH, true);
        atmosphereFramework.addAtmosphereHandler(URL_PATH, handler, interceptors);

        atmosphereFramework.init();
        return atmosphereFramework;
    }
}

