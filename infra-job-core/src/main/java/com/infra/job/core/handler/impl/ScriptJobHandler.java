package com.infra.job.core.handler.impl;

import com.infra.job.core.context.JobContext;
import com.infra.job.core.context.JobHelper;
import com.infra.job.core.glue.GlueTypeEnum;
import com.infra.job.core.handler.IJobHandler;
import com.infra.job.core.log.JobFileAppender;
import com.infra.job.core.util.ScriptUtil;

import java.io.File;

public class ScriptJobHandler extends IJobHandler {

    private int jobId;
    private long glueUpdatetime;
    private String gluesource;
    private GlueTypeEnum glueType;

    public ScriptJobHandler(int jobId, long glueUpdatetime, String gluesource, GlueTypeEnum glueType){
        this.jobId = jobId;
        this.glueUpdatetime = glueUpdatetime;
        this.gluesource = gluesource;
        this.glueType = glueType;

        File glueSrcPath = new File(JobFileAppender.getGlueSrcPath());
        if (glueSrcPath.exists()) {
            File[] glueSrcFileList = glueSrcPath.listFiles();
            if (glueSrcFileList!=null && glueSrcFileList.length>0) {
                for (File glueSrcFileItem : glueSrcFileList) {
                    if (glueSrcFileItem.getName().startsWith(String.valueOf(jobId)+"_")) {
                        glueSrcFileItem.delete();
                    }
                }
            }
        }

    }

    public long getGlueUpdatetime() {
        return glueUpdatetime;
    }

    @Override
    public void execute() throws Exception {

        if (!glueType.isScript()) {
            JobHelper.handleFail("glueType["+ glueType +"] invalid.");
            return;
        }

        String cmd = glueType.getCmd();

        String scriptFileName = JobFileAppender.getGlueSrcPath()
                .concat(File.separator)
                .concat(String.valueOf(jobId))
                .concat("_")
                .concat(String.valueOf(glueUpdatetime))
                .concat(glueType.getSuffix());
        File scriptFile = new File(scriptFileName);
        if (!scriptFile.exists()) {
            ScriptUtil.markScriptFile(scriptFileName, gluesource);
        }

        String logFileName = JobContext.getJobContext().getJobLogFileName();

        String[] scriptParams = new String[3];
        scriptParams[0] = JobHelper.getJobParam();
        scriptParams[1] = String.valueOf(JobContext.getJobContext().getShardIndex());
        scriptParams[2] = String.valueOf(JobContext.getJobContext().getShardTotal());

        JobHelper.log("----------- script file:"+ scriptFileName +" -----------");
        int exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);

        if (exitValue == 0) {
            JobHelper.handleSuccess();
            return;
        } else {
            JobHelper.handleFail("script exit value("+exitValue+") is failed");
            return ;
        }

    }

}
