package com.infra.job.core.context;

public class JobContext {

    public static final int HANDLE_CODE_SUCCESS = 200;
    public static final int HANDLE_CODE_FAIL = 500;
    public static final int HANDLE_CODE_TIMEOUT = 502;

    private final long jobId;

    private final String jobParam;

    private final String jobLogFileName;

    private final int shardIndex;

    private final int shardTotal;

    private int handleCode;

    private String handleMsg;


    public JobContext(long jobId, String jobParam, String jobLogFileName, int shardIndex, int shardTotal) {
        this.jobId = jobId;
        this.jobParam = jobParam;
        this.jobLogFileName = jobLogFileName;
        this.shardIndex = shardIndex;
        this.shardTotal = shardTotal;

        this.handleCode = HANDLE_CODE_SUCCESS; 
    }

    public long getJobId() {
        return jobId;
    }

    public String getJobParam() {
        return jobParam;
    }

    public String getJobLogFileName() {
        return jobLogFileName;
    }

    public int getShardIndex() {
        return shardIndex;
    }

    public int getShardTotal() {
        return shardTotal;
    }

    public void setHandleCode(int handleCode) {
        this.handleCode = handleCode;
    }

    public int getHandleCode() {
        return handleCode;
    }

    public void setHandleMsg(String handleMsg) {
        this.handleMsg = handleMsg;
    }

    public String getHandleMsg() {
        return handleMsg;
    }

    private static InheritableThreadLocal<JobContext> contextHolder = new InheritableThreadLocal<JobContext>(); // support for child thread of job handler)

    public static void setJobContext(JobContext JobContext){
        contextHolder.set(JobContext);
    }

    public static JobContext getJobContext(){
        return contextHolder.get();
    }

}