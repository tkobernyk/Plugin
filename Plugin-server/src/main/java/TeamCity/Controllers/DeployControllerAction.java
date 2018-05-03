package TeamCity.Controllers;

import TeamCity.Models.Deploy;
import TeamCity.powershell.DeployStatus;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DeployControllerAction extends BaseController {
    private final PluginDescriptor descriptor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PowerShellFactory powerShellFactory;
    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    public DeployControllerAction(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor descriptor,
            @NotNull PowerShellFactory powerShellFactory
    ) {
        super(server);
        webControllerManager.registerController("/deploy/run.html", this);
        this.descriptor = descriptor;
        this.powerShellFactory = powerShellFactory;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        Log.info(httpServletRequest.getParameterMap().keySet().stream().collect(Collectors.joining()));
        BufferedReader reader = httpServletRequest.getReader();
        reader.lines().forEach(System.out::println);
        Deploy deploy = getDeploy(httpServletRequest);
        SUser sUser = SessionUser.getUser(httpServletRequest);
        String username = sUser.getUsername();
        String name = sUser.getName();
        long id = sUser.getId();
        Log.info("USERNAME : " + username);
        Log.info("NAME : " + name);
        Log.info("ID : " + id);
        String prefixPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("Runtime"));
        createRuntimeFolderIfDoesnotExist(prefixPath);
        PowerShellWrapper powerShellWrapper = powerShellFactory.createPowerShellRunner(deploy, sUser, psScriptPath, prefixPath);
        Future<DeployStatus> future = executorService.submit(powerShellWrapper);
        DeployStatus deployStatus = future.get();
        Log.info("DEPLOY STATUS : " + deployStatus.name());
        return null;
    }

    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(request.getParameter("Environment"));
        deploy.setPhase(request.getParameter("Phase"));
        Log.info("Deploy: " + deploy.toString());
        return deploy;
    }

    private void createRuntimeFolderIfDoesnotExist(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectory(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

