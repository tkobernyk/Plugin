package TeamCity.Controllers;

import TeamCity.Helpers.PowerShellRunner;
import TeamCity.Models.Deploy;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DeployControllerAction extends BaseController {
    public static BlockingQueue<String> blockingQueue = new LinkedBlockingQueue<>();
    private final PluginDescriptor descriptor;
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    public DeployControllerAction(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor descriptor
    ) {
        super(server);
        webControllerManager.registerController("/deploy/run.html", this);
        this.descriptor = descriptor;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        if(DeployControllerAction.blockingQueue.isEmpty()) {
            Deploy deploy = getDeploy(httpServletRequest);
            PowerShellRunner.run(blockingQueue, psScriptPath, deploy.toString());
        }
        return null;

    }


    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(request.getParameter("Environment"));
        deploy.setPhase(request.getParameter("Phase"));
        return deploy;
    }
}

