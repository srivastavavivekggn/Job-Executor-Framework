package com.infra.job.core.biz;

import com.infra.job.core.biz.model.HandleCallbackParam;
import com.infra.job.core.biz.model.RegistryParam;
import com.infra.job.core.biz.model.ReturnT;

import java.util.List;

public interface AdminBiz {

    public ReturnT<String> callback(List<HandleCallbackParam> callbackParamList);
    
    public ReturnT<String> registry(RegistryParam registryParam);
   
    public ReturnT<String> registryRemove(RegistryParam registryParam);

}
