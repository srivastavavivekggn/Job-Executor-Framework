package com.infra.job.admin.core.route.strategy;

import com.infra.job.admin.core.route.ExecutorRouter;
import com.infra.job.core.biz.model.ReturnT;
import com.infra.job.core.biz.model.TriggerParam;

import java.util.List;

public class ExecutorRouteLast extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {
        return new ReturnT<String>(addressList.get(addressList.size()-1));
    }

}
