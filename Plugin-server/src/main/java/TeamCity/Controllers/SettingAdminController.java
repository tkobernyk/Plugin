package TeamCity.Controllers;

import TeamCity.Models.HelixDeploySettings;
import jetbrains.buildServer.controllers.BaseController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SettingAdminController extends BaseController {


    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        HelixDeploySettings settings = new HelixDeploySettings();
        settings.PowerShellScriptPath = request.getParameter("txtPSScriptPath");
        return null;
    }
}
