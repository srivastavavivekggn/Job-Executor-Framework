package com.infra.job.core.biz;

import com.infra.job.core.biz.model.*;

public interface ExecutorBiz {

    public ReturnT<String> beat();

    public ReturnT<String> idleBeat(IdleBeatParam idleBeatParam);

    public ReturnT<String> run(TriggerParam triggerParam);

    public ReturnT<String> kill(KillParam killParam);

    public ReturnT<LogResult> log(LogParam logParam);

}
