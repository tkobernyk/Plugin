package TeamCity.service;

import TeamCity.models.Deploy;
import TeamCity.powershell.PowerShellFactory;
import TeamCity.powershell.PowerShellWrapper;
import jetbrains.buildServer.users.SUser;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Queue;

public class MessageService extends AbstractService {

    private static final com.intellij.openapi.diagnostic.Logger logger =
            com.intellij.openapi.diagnostic.Logger.getInstance(MessageService.class.getName());

    public MessageService(PowerShellFactory powerShellFactory) {
        super(powerShellFactory);
    }

    public void processInputFromPowerShellWrapper(HttpServletResponse response, Deploy deploy, SUser sUser) {
        try (OutputStream outputStream = response.getOutputStream()) {
            PowerShellWrapper powerShellWrapper = getPowerShellFactory().
                    getOrCreatePowerShellRunner(deploy, sUser, null, null);
            String data = processOutput(powerShellWrapper.getQueue());
            outputStream.write(data.getBytes());
            powerShellWrapper.getData().append(data);
        } catch (IOException |
                InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }


    private String processOutput(Queue<String> queue) throws InterruptedException {
        StringBuilder builder = new StringBuilder();
        int i = 0;
        while (!queue.isEmpty() && i < 20) {
            builder.append(queue.poll());
            i++;
        }
        return builder.toString();
    }

}
