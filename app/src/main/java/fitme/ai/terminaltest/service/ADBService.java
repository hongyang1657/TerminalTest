package fitme.ai.terminaltest.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import fitme.ai.terminaltest.utils.L;

/**
 * Created by hongy on 2018/2/6.
 */

public class ADBService extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        L.i("开启远程ADB调试");
        executeShellCommand("setprop service.adb.tcp.port 5555");
        executeShellCommand("stop adbd");
        executeShellCommand("start adbd");
        executeShellCommand("netcfg");
    }

    public void executeShellCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        BufferedReader osReader = null;
        BufferedReader osErrorReader = null;

        try {
            //执行命令
            process = Runtime.getRuntime().exec(command);

            //获得进程的输入输出流
            os = new DataOutputStream(process.getOutputStream());
            osReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            osErrorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            //输入 exit 命令以确保进程退出
            os.writeBytes("exit\n");
            os.flush();

            int processResult;
            String shellMessage;
            String errorMessage;

            //获取命令执行信息
            shellMessage = readOSMessage(osReader);
            errorMessage = readOSMessage(osErrorReader);

            //获得退出状态
            processResult = process.waitFor();

            L.i("processResult : " + processResult);
            L.i("shellMessage : " + shellMessage);
            L.i("errorMessage : " + errorMessage);


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (osReader != null) {
                try {
                    osReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (osErrorReader != null) {
                try {
                    osErrorReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (process != null) {
                process.destroy();
            }
        }
    }

    //读取执行命令后返回的信息
    private static String readOSMessage(BufferedReader messageReader) throws IOException {
        StringBuilder content = new StringBuilder();
        String lineString;
        while ((lineString = messageReader.readLine()) != null) {

            System.out.println("lineString : " + lineString);

            content.append(lineString).append("\n");
        }

        return content.toString();
    }

}
