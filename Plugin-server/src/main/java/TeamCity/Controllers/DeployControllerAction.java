package TeamCity.Controllers;

import TeamCity.Models.Deploy;
import TeamCity.websockets.AsynchronoysMessageProcessing;
import TeamCity.websockets.WebsocketMessageHandler;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class DeployControllerAction extends BaseController {
    private ExecutorService excecutor = Executors.newFixedThreadPool(20);
    private AtmosphereFramework atmosphereFramework;
    private final PluginDescriptor descriptor;
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    public DeployControllerAction(
            @NotNull SBuildServer server,
            @NotNull WebsocketMessageHandler websocketMessageHandler,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor descriptor
    ) {
        super(server);
        this.atmosphereFramework = createAtmosphereFramework(websocketMessageHandler);
        webControllerManager.registerController("/deploy/run.html", this);
        this.descriptor = descriptor;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        Deploy deploy = getDeploy(httpServletRequest);
        Log.info("REQUEST METHOD " + httpServletRequest.getMethod());
        Log.info("DEPLOY : " + deploy.toString());
        Log.info("REQUEST BODY " + httpServletRequest.getReader().lines().collect(Collectors.joining()));
        Log.info("Thread name inside DeployControllerAction : " + Thread.currentThread().getName());
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        httpServletRequest.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", Boolean.TRUE);
        AsynchronoysMessageProcessing asynchronoysMessageProcessing = new AsynchronoysMessageProcessing();
        asynchronoysMessageProcessing.setAtmosphereFramework(atmosphereFramework);
        asynchronoysMessageProcessing.setAtmosphereRequest(AtmosphereRequest.wrap(httpServletRequest));
        asynchronoysMessageProcessing.setAtmosphereResponse(AtmosphereResponse.wrap(httpServletResponse));
        asynchronoysMessageProcessing.setScriptPath(psScriptPath);
        asynchronoysMessageProcessing.setDeploy(getDeploy(httpServletRequest));
        CompletableFuture<String> completableFuture = supplyAsync(asynchronoysMessageProcessing, excecutor);
        while (!completableFuture.isDone()) {
            completableFuture.get();
        }
        return null;

    }

    private AtmosphereFramework createAtmosphereFramework(@NotNull WebsocketMessageHandler websocketMessageHandler) {
        AtmosphereFramework atmosphereFramework = new AtmosphereFramework();

        List<AtmosphereInterceptor> interceptors = new ArrayList<AtmosphereInterceptor>();
        interceptors.add(new AtmosphereResourceLifecycleInterceptor());
        interceptors.add(new HeartbeatInterceptor());
        interceptors.add(new IdleResourceInterceptor());
        interceptors.add(new TrackMessageSizeInterceptor());
        interceptors.add(new BroadcastOnPostAtmosphereInterceptor());

        atmosphereFramework.addAtmosphereHandler("/", websocketMessageHandler, interceptors);

        atmosphereFramework.init();
        return atmosphereFramework;
    }

    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.BuildId = request.getParameter("BuildId");
        deploy.ProjectName = request.getParameter("ProjectName");
        deploy.Environment = request.getParameter("Environment");
        deploy.Phase = request.getParameter("Phase");
        return deploy;
    }
}

