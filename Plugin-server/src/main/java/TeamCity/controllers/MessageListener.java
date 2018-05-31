package TeamCity.controllers;

import TeamCity.models.Deploy;
import TeamCity.models.Environment;
import TeamCity.service.MessageService;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MessageListener extends BaseController {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());
    private MessageService messageService;


    public MessageListener(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull MessageService messageService) {
        super(server);
        webControllerManager.registerController("/messages/getMessage.html", this);
        this.messageService = messageService;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws InterruptedException {
        this.messageService.processInputFromPowerShellWrapper(response, getDeploy(request), SessionUser.getUser(request));
        return null;
    }


    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(Environment.valueOf(request.getParameter("Environment")));
        deploy.setPhase(request.getParameter("Phase"));
        return deploy;
    }


}
