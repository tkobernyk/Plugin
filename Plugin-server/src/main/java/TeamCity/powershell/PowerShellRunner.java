package TeamCity.powershell;

import TeamCity.exception.NoSupportedOSException;
import TeamCity.models.Deploy;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

@Getter
@Setter
public class PowerShellRunner {
    private static final com.intellij.openapi.diagnostic.Logger LOGGER =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellRunner.class.getName());

    private static Predicate<String> predicate = s -> !s.contains("Windows PowerShell")
            && !s.contains("Copyright (C) 2016 Microsoft Corporation. All rights reserved.");

    //TODO: rewrite to separate methods
    public Deploy run(BlockingQueue<String> blockingQueue, String scriptPath, Deploy deploy) {
        ProcessBuilder processBuilder = createProcessWindowsOS(scriptPath, deploy.getParametersAsString());
        Process powerShellProcess = null;
        try {
            powerShellProcess = processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        deploy.setDeployStatus(DeployStatus.IN_PROGRESS);
        //powerShellProcess.getOutputStream().close();
        processOutput(powerShellProcess,blockingQueue, deploy);
        processErrorOutput(powerShellProcess,blockingQueue, deploy);
        deploy.setDeployStatus(DeployStatus.SUCCESS);
        return deploy;
    }

    private ProcessBuilder createProcessWindowsOS(String scriptPath, String params) {
        LOGGER.info("OS NAME " + System.getProperty("os.name"));
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String powerShellExecutable = "powershell.exe";
            LOGGER.info("SCRIPT PATH = " + scriptPath);
            return new ProcessBuilder(powerShellExecutable,
                    "-ExecutionPolicy", "Bypass", "-NoExit", "-File", scriptPath, params);
        }
        throw new NoSupportedOSException("os.name should be windows");
    }

    private void processOutput(Process powerShellProcess, BlockingQueue<String> blockingQueue, Deploy deploy) {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()))) {
            LOGGER.info("PROCESS OUTPUT");
            bufferedReader.lines().filter(predicate).forEach(line -> addStringToBlockingQueue(blockingQueue, line));
        } catch (Exception e) {
            deploy.setDeployStatus(DeployStatus.FAILED);
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void processErrorOutput(Process powerShellProcess, BlockingQueue<String> blockingQueue, Deploy deploy) {
        try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()))) {
            if (errorReader.ready()) {
                blockingQueue.add("<div class='error'>");
                errorReader.lines().forEach(blockingQueue::add);
                blockingQueue.add("</div>");
            }
        } catch (Exception e) {
            deploy.setDeployStatus(DeployStatus.FAILED);
            LOGGER.error(e.getMessage(), e);
        }
    }

    private boolean addStringToBlockingQueue(BlockingQueue<String> blockingQueue, String line) {
        return blockingQueue.add(line + (!line.isEmpty() ? " <br />" : ""));
    }

}