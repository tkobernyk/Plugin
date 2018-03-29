package TeamCity.Controllers;

import TeamCity.Helpers.PowerShellRunner;
import TeamCity.Models.Deploy;
import com.fasterxml.jackson.databind.ObjectMapper;
import jetbrains.buildServer.controllers.BaseAjaxActionController;
import jetbrains.buildServer.web.openapi.ControllerAction;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeployControllerAction extends BaseAjaxActionController implements ControllerAction {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());
    private final PluginDescriptor _descriptor;

    public DeployControllerAction(@NotNull WebControllerManager controllerManager, @NotNull PluginDescriptor descriptor) {
        super(controllerManager);
        controllerManager.registerController("/deploy/run.html", this);
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
}

