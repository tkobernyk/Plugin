package TeamCity.Helpers;

import java.io.*;
import java.util.concurrent.BlockingQueue;

public class PowerShellRunner {
    private static final com.intellij.openapi.diagnostic.Logger Log =
            com.intellij.openapi.diagnostic.Logger.getInstance(PowerShellRunner.class.getName());

    public static void run(BlockingQueue<String> blockingQueue, String scriptPath, String params) throws IOException {
        String line;
        ProcessBuilder pb = null;
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            String powerShellExecutable = "powershell.exe";
            pb = new ProcessBuilder( powerShellExecutable,
                    "-ExecutionPolicy", "Bypass", "-NoExit", "-File", "D:\\IDCO\\Source\\SectorSPDR\\spdrs\\Scripts\\SetupSpdr\\RecreateSpdr.ps1");//scriptPath, params);
        }
        Process powerShellProcess = pb.start();
        if (!powerShellProcess.isAlive()) {
            throw new RuntimeException(
                    "Cannot execute PowerShell. Please make sure that it is installed in your system. Errorcode:" + powerShellProcess.exitValue());
        }
        //PrintWriter commandWriter = new PrintWriter(new OutputStreamWriter(new BufferedOutputStream(powerShellProcess.getOutputStream())), true);
        //commandWriter.println(scriptPath + " " + params);
        powerShellProcess.getOutputStream().close();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = bufferedReader.readLine()) != null) {
            if(!line.contains("Windows PowerShell") && !line.contains("Copyright (C) 2016 Microsoft Corporation. All rights reserved."))
                blockingQueue.add(line + (!line.isEmpty() ? " <br />" : ""));
        }
        bufferedReader.close();
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        if(errorReader.ready()) {
            blockingQueue.add("<div class='error'>");
            while ((line = errorReader.readLine()) != null) {
                blockingQueue.add(line);
            }
            blockingQueue.add("</div>");
        }
        errorReader.close();
        System.out.println("Done");

    }
}
