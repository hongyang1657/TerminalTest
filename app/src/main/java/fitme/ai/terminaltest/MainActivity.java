package fitme.ai.terminaltest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity{

    private TextView content;
    private EditText input;
    private static final String TAG = "result";
    private String result = "";
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    result = (String) msg.obj+"   错误码"+msg.arg1+"\n"+result;
                    content.setText(result);
                    input.setText("");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        content = (TextView) findViewById(R.id.content);
        input = (EditText) findViewById(R.id.input);
        /*executeShellCommand("setprop service.adb.tcp.port 5555");
        executeShellCommand("stop adbd");
        executeShellCommand("start adbd");
        executeShellCommand("netcfg");*/
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:

                executeShellCommand(input.getText().toString().trim());
                input.setText("");
                break;
            case R.id.button2:
                executeShellCommand("setprop service.adb.tcp.port 5555");
                break;
            case R.id.button3:
                executeShellCommand("stop adbd");
                break;
            case R.id.button4:
                executeShellCommand("start adbd");
                break;
            case R.id.button5:
                executeShellCommand("netcfg");
                break;
            default:
                break;
        }

    }


    //
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

            Log.i(TAG,"processResult : " + processResult);
            Log.i(TAG,"shellMessage : " + shellMessage);
            Log.i(TAG,"errorMessage : " + errorMessage);
            Message message = new Message();
            message.what = 1;
            message.arg1 = processResult;
            message.obj = shellMessage;
            handler.sendMessage(message);

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
