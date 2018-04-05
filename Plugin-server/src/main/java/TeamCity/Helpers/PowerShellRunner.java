package TeamCity.Helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class PowerShellRunner {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellRunner.class.getName());

    public static void run(BlockingQueue<String> blockingQueue, String scriptPath, String params) throws IOException {
        ProcessBuilder pb = null;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String powerShellExecutable = "powershell.exe";
            pb = new ProcessBuilder("cmd.exe", "/c", "chcp", "65001", ">", "NUL", "&", powerShellExecutable,
                    "-ExecutionPolicy", "Bypass", "-NoExit", "-Command", "-");
        }
        Process powerShellProcess = pb.start();
        if (!powerShellProcess.isAlive()) {
            throw new RuntimeException(
                    "Cannot execute PowerShell. Please make sure that it is installed in your system. Errorcode:" + powerShellProcess.exitValue());
        }
        powerShellProcess.getOutputStream().close();
        String line;
        System.out.println("Standard Output:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = bufferedReader.readLine()) != null) {
            blockingQueue.add(line);
        }
        bufferedReader.close();
        BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = bufferedReader1.readLine()) != null) {
            blockingQueue.add(line);
        }
        bufferedReader1.close();
        System.out.println("Done");

    }
}
