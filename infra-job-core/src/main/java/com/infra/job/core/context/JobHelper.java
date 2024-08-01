package com.infra.job.core.context;

import com.infra.job.core.log.JobFileAppender;
import com.infra.job.core.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

public class JobHelper {

    public static long getJobId() {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return -1;
        }

        return JobContext.getJobId();
    }

  
    public static String getJobParam() {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return null;
        }

        return JobContext.getJobParam();
    }

   
    public static String getJobLogFileName() {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return null;
        }

        return JobContext.getJobLogFileName();
    }

  
    public static int getShardIndex() {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return -1;
        }

        return JobContext.getShardIndex();
    }

    
    public static int getShardTotal() {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return -1;
        }

        return JobContext.getShardTotal();
    }

    private static Logger logger = LoggerFactory.getLogger("job logger");

    
    public static boolean log(String appendLogPattern, Object ... appendLogArguments) {

        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

   
    public static boolean log(Throwable e) {

        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        String appendLog = stringWriter.toString();

        StackTraceElement callInfo = new Throwable().getStackTrace()[1];
        return logDetail(callInfo, appendLog);
    }

    private static boolean logDetail(StackTraceElement callInfo, String appendLog) {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return false;
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(DateUtil.formatDateTime(new Date())).append(" ")
                .append("["+ callInfo.getClassName() + "#" + callInfo.getMethodName() +"]").append("-")
                .append("["+ callInfo.getLineNumber() +"]").append("-")
                .append("["+ Thread.currentThread().getName() +"]").append(" ")
                .append(appendLog!=null?appendLog:"");
        String formatAppendLog = stringBuffer.toString();

        String logFileName = JobContext.getJobLogFileName();

        if (logFileName!=null && logFileName.trim().length()>0) {
            JobFileAppender.appendLog(logFileName, formatAppendLog);
            return true;
        } else {
            logger.info(">>>>>>>>>>> {}", formatAppendLog);
            return false;
        }
    }

  
    public static boolean handleSuccess(){
        return handleResult(JobContext.HANDLE_CODE_SUCCESS, null);
    }

    public static boolean handleSuccess(String handleMsg) {
        return handleResult(JobContext.HANDLE_CODE_SUCCESS, handleMsg);
    }

    public static boolean handleFail(){
        return handleResult(JobContext.HANDLE_CODE_FAIL, null);
    }

    public static boolean handleFail(String handleMsg) {
        return handleResult(JobContext.HANDLE_CODE_FAIL, handleMsg);
    }

    public static boolean handleTimeout(){
        return handleResult(JobContext.HANDLE_CODE_TIMEOUT, null);
    }

    public static boolean handleTimeout(String handleMsg){
        return handleResult(JobContext.HANDLE_CODE_TIMEOUT, handleMsg);
    }

    
    public static boolean handleResult(int handleCode, String handleMsg) {
        JobContext JobContext = JobContext.getJobContext();
        if (JobContext == null) {
            return false;
        }

        JobContext.setHandleCode(handleCode);
        if (handleMsg != null) {
            JobContext.setHandleMsg(handleMsg);
        }
        return true;
    }


}
