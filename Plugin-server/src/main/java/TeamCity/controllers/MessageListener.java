package TeamCity.controllers;

import TeamCity.dto.DeploymentDTO;
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
import java.util.concurrent.BlockingQueue;


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
            DeploymentDTO deploymentDTO = objectMapper.readValue(request.getReader(), DeploymentDTO.class);
            SUser sUser = SessionUser.getUser(request);
            PowerShellWrapper powerShellWrapper = powerShellFactory.
                    getOrCreatePowerShellRunner(deploymentDTO.getDeploy(), sUser, deploymentDTO.getScriptPath(), deploymentDTO.getPathPrefix());
            outputStream.write(processOutput(powerShellWrapper.getBlockingQueue()).getBytes());
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
        }
        return null;
    }

    private String processOutput(BlockingQueue<String> blockingQueue) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (!blockingQueue.isEmpty() && i < 20) {
            builder.append(blockingQueue.take());
            i++;
        }
        Log.info("get message from queue " + builder.toString());
        return builder.toString();
    }

}
