package TeamCity.Controllers;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class MessageListener extends BaseController {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(DeployControllerAction.class.getName());

    public MessageListener(
            @NotNull SBuildServer server,
            @NotNull WebControllerManager webControllerManager) {
        super(server);
        webControllerManager.registerController("/messages/getMessage.html", this);
    }

    @Nullable
    @Override
    protected ModelAndView doHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response) throws Exception {
        Log.info("INSIDE MESSAGE TRIGGER");
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (!DeployControllerAction.blockingQueue.isEmpty() && i < 20) {
            builder.append(DeployControllerAction.blockingQueue.take());
           i++;
        }
        Log.info("get message from queue " + builder.toString());
        response.getOutputStream().write(builder.toString().getBytes());
        return null;
    }
}
