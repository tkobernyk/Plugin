package TeamCity.Controllers;

import TeamCity.Helpers.PowerShellRunner;
import TeamCity.Models.Deploy;
import com.profesorfalken.jpowershell.PowerShellResponse;
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
        Deploy deploy = getDeploy(httpServletRequest);
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        PowerShellResponse response = PowerShellRunner.run(psScriptPath, deploy);
        Log.info("MESSAGE INSIDE CYCLE  " + response.getCommandOutput());
        blockingQueue.add(response.getCommandOutput());
        return null;

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

