package TeamCity.controllers;

import TeamCity.models.Deploy;
import TeamCity.models.Environment;
import TeamCity.service.DeployService;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static TeamCity.util.PluginUtils.createRuntimeFolderIfDoesNotExist;

public class DeployControllerAction extends BaseController {
    private static final com.intellij.openapi.diagnostic.Logger loggger =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());
    private final PluginDescriptor descriptor;
    DeployService deployService;


    public DeployControllerAction(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor descriptor,
            @NotNull DeployService deployService
    ) {
        super(server);
        webControllerManager.registerController("/deploy/run.html", this);
        this.descriptor = descriptor;
        this.deployService = deployService;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        SUser sUser = SessionUser.getUser(httpServletRequest);
        Deploy deploy = getDeploy(httpServletRequest);
        String prefixPath = createRuntimeFolderIfDoesNotExist(httpServletRequest.getServletContext()
                .getRealPath(descriptor.getPluginResourcesPath("Runtime")));
        this.deployService.deployProjectToEnvironment(deploy, sUser, psScriptPath, prefixPath);
        return null;
    }

    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(Environment.valueOf(request.getParameter("Environment")));
        deploy.setPhase(request.getParameter("Phase"));
        deploy.setEnabledDeploy(Boolean.valueOf(request.getParameter("EnabledDeploy")));
        logger.info("Deploy: " + deploy.toString());
        return deploy;
    }


}
