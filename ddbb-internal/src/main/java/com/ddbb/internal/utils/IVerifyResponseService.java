package com.ddbb.internal.utils;

import okhttp3.Response;
import org.apache.commons.lang3.tuple.Pair;

import javax.servlet.http.HttpServletRequest;

public interface IVerifyResponseService {

    public Pair<Boolean,String> NOT_PASS = Pair.of(false,"");
    /**
     * 验签http的返回
     * @param response
     * @return
     */
    public Pair<Boolean,String> verifyResponse(Response response);

    /**
     * 验签第三方主动回调
     * @param request
     * @return
     */
    public Pair<Boolean,String> verifyRequest(HttpServletRequest request);

}
