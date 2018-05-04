package TeamCity.controllers;

import TeamCity.dao.HistoricalDataDao;
import TeamCity.models.Deploy;
import TeamCity.models.Environment;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import TeamCity.util.PluginUtils;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DeployControllerAction extends BaseController {
    private final PluginDescriptor descriptor;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PowerShellFactory powerShellFactory;
    private final HistoricalDataDao historicalDataDao;
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    public DeployControllerAction(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor descriptor,
            @NotNull PowerShellFactory powerShellFactory,
            @NotNull HistoricalDataDao historicalDataDao
    ) {
        super(server);
        webControllerManager.registerController("/deploy/run.html", this);
        this.descriptor = descriptor;
        this.powerShellFactory = powerShellFactory;
        this.historicalDataDao = historicalDataDao;
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest httpServletRequest, @NotNull HttpServletResponse httpServletResponse) throws Exception {
        String psScriptPath = httpServletRequest.getServletContext().getRealPath(descriptor.getPluginResourcesPath("PSScripts/Deploy.ps1"));
        Log.info(httpServletRequest.getParameterMap().keySet().stream().collect(Collectors.joining()));
        SUser sUser = SessionUser.getUser(httpServletRequest);
        Deploy deploy = getDeploy(httpServletRequest);
        String prefixPath = createRuntimeFolderIfDoesNotExist(httpServletRequest.getServletContext()
                .getRealPath(descriptor.getPluginResourcesPath("Runtime")));
        PowerShellWrapper powerShellWrapper = powerShellFactory.getOrCreatePowerShellRunner(deploy, sUser, psScriptPath, prefixPath);
        CompletableFuture.supplyAsync(powerShellWrapper)
                .thenAcceptAsync((Deploy d) -> historicalDataDao.save(PluginUtils.convertFromDeployToHistoricalData(d, powerShellWrapper.getData().toString())))
                .thenAcceptAsync((Void v) -> powerShellFactory.getCacheWrapper().getPowerShellOutputCache().remove(powerShellWrapper.getDeploy().getFileNameFromDeploy().hashCode()))
                .thenAcceptAsync((Void v) -> removeFileWithCatchingException(Paths.get(powerShellWrapper.getScriptPath())));
        return null;
    }

    //TODO be careful for adding to javascript values from enum
    private Deploy getDeploy(HttpServletRequest request) {
        Deploy deploy = new Deploy();
        deploy.setBuildId(request.getParameter("BuildId"));
        deploy.setProjectName(request.getParameter("ProjectName"));
        deploy.setEnvironment(Environment.valueOf(request.getParameter("Environment")));
        deploy.setPhase(request.getParameter("Phase"));
        Log.info("Deploy: " + deploy.toString());
        return deploy;
    }

    private String createRuntimeFolderIfDoesNotExist(String path) {
        if (!Files.exists(Paths.get(path))) {
            try {
                Files.createDirectory(Paths.get(path));
            } catch (IOException e) {
                Log.error(e.getMessage(), e);
            }
        }
        return path;
    }

    private void removeFileWithCatchingException(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            Log.error(e.getMessage(), e);
        }
    }

}
