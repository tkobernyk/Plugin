package TeamCity.controllers;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class SettingAdminController extends BaseController {

    private final PluginDescriptor pluginDescriptor;
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(SettingAdminController.class.getName());

    public SettingAdminController(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager,
            @NotNull PluginDescriptor pluginDescriptor) {
        super(server);
        this.pluginDescriptor = pluginDescriptor;
        webControllerManager.registerController("/saveHelixDeploySettings.html", this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        Path path = Paths.get(request.getServletContext().getRealPath(pluginDescriptor.getPluginResourcesPath("settings.properties")));
        saveHelixDeploySettings(request.getInputStream(), path);
        return null;
    }


    private void saveHelixDeploySettings(InputStream inputStream, Path path) {
        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            Log.error(e.getMessage(), e);
        }
    }
}
