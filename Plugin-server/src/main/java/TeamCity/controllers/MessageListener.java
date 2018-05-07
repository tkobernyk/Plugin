package TeamCity.controllers;

import TeamCity.models.Deploy;
import TeamCity.models.Environment;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.users.SUser;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import jetbrains.buildServer.web.util.SessionUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.Queue;


public class MessageListener extends BaseController {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    private ObjectMapper objectMapper = new ObjectMapper();
    private PowerShellFactory powerShellFactory;

    public MessageListener(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PowerShellFactory powerShellFactory) {
        super(server);
        webControllerManager.registerController("/messages/getMessage.html", this);
        this.powerShellFactory = powerShellFactory;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            Log.info("INSIDE MESSAGE TRIGGER");
            Deploy deploy = getDeploy(request);
            SUser sUser = SessionUser.getUser(request);
            PowerShellWrapper powerShellWrapper = powerShellFactory.
                    getOrCreatePowerShellRunner(deploy, sUser, null, null);
            String data = processOutput(powerShellWrapper.getQueue());
            outputStream.write(data.getBytes());
            powerShellWrapper.getData().append(data);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
        return null;
    }

    private String processOutput(Queue<String> queue) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (!queue.isEmpty() && i < 20) {
            builder.append(queue.poll());
            i++;
        }
        Log.info("get message from queue " + builder.toString());
        return builder.toString();
    }

    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(Environment.valueOf(request.getParameter("Environment")));
        deploy.setPhase(request.getParameter("Phase"));
        Log.info("Deploy: " + deploy.toString());
        return deploy;
    }


}
