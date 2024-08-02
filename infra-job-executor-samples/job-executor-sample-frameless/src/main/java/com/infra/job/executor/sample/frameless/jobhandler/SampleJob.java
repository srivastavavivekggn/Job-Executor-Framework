package com.infra.job.executor.sample.frameless.jobhandler;

import com.infra.job.core.context.JobHelper;
import com.infra.job.core.handler.annotation.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class SampleJob {
    private static Logger logger = LoggerFactory.getLogger(SampleJob.class);

    @Job("demoJobHandler")
    public void demoJobHandler() throws Exception {
        JobHelper.log("JOB, Hello World.");

        for (int i = 0; i < 5; i++) {
            JobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
    }


    @Job("shardingJobHandler")
    public void shardingJobHandler() throws Exception {

        int shardIndex = JobHelper.getShardIndex();
        int shardTotal = JobHelper.getShardTotal();

        JobHelper.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        for (int i = 0; i < shardTotal; i++) {
            if (i == shardIndex) {
                JobHelper.log("第 {} 片, 命中分片开始处理", i);
            } else {
                JobHelper.log("第 {} 片, 忽略", i);
            }
        }

    }


    @Job("commandJobHandler")
    public void commandJobHandler() throws Exception {
        String command = JobHelper.getJobParam();
        int exitValue = -1;

        BufferedReader bufferedReader = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                JobHelper.log(line);
            }

            process.waitFor();
            exitValue = process.exitValue();
        } catch (Exception e) {
            JobHelper.log(e);
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        if (exitValue == 0) {
        } else {
            JobHelper.handleFail("command exit value("+exitValue+") is failed");
        }

    }


    @Job("httpJobHandler")
    public void httpJobHandler() throws Exception {

        String param = JobHelper.getJobParam();
        if (param==null || param.trim().length()==0) {
            JobHelper.log("param["+ param +"] invalid.");

            JobHelper.handleFail();
            return;
        }

        String[] httpParams = param.split("\n");
        String url = null;
        String method = null;
        String data = null;
        for (String httpParam: httpParams) {
            if (httpParam.startsWith("url:")) {
                url = httpParam.substring(httpParam.indexOf("url:") + 4).trim();
            }
            if (httpParam.startsWith("method:")) {
                method = httpParam.substring(httpParam.indexOf("method:") + 7).trim().toUpperCase();
            }
            if (httpParam.startsWith("data:")) {
                data = httpParam.substring(httpParam.indexOf("data:") + 5).trim();
            }
        }

        if (url==null || url.trim().length()==0) {
            JobHelper.log("url["+ url +"] invalid.");

            JobHelper.handleFail();
            return;
        }
        if (method==null || !Arrays.asList("GET", "POST").contains(method)) {
            JobHelper.log("method["+ method +"] invalid.");

            JobHelper.handleFail();
            return;
        }
        boolean isPostMethod = method.equals("POST");

        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            connection.setRequestMethod(method);
            connection.setDoOutput(isPostMethod);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(5 * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            connection.connect();

            if (isPostMethod && data!=null && data.trim().length()>0) {
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(data.getBytes("UTF-8"));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                throw new RuntimeException("Http Request StatusCode(" + statusCode + ") Invalid.");
            }

            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String responseMsg = result.toString();

            JobHelper.log(responseMsg);

            return;
        } catch (Exception e) {
            JobHelper.log(e);

            JobHelper.handleFail();
            return;
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                JobHelper.log(e2);
            }
        }

    }

    @Job(value = "demoJobHandler2", init = "init", destroy = "destroy")
    public void demoJobHandler2() throws Exception {
        JobHelper.log("JOB, Hello World.");
    }
    public void init(){
        logger.info("init");
    }
    public void destroy(){
        logger.info("destroy");
    }


}
