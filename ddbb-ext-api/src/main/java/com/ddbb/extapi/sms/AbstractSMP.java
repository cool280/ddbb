package com.ddbb.extapi.sms;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.internal.utils.OkHttpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public abstract class AbstractSMP {
    @Value("${ddbb.allowSendSms:false}")
    private boolean allowSendSms;


    protected static JSONObject simpleSuccess() {
        JSONObject json = new JSONObject();
        json.put("resultCode", 0);
        return json;
    }

    protected static JSONObject simpleFailure(String err) {
        JSONObject json = new JSONObject();
        json.put("resultCode", -1);
        json.put("errMsg", err);
        return json;
    }

    /**
     * TODO	设置号码个数最大不能超过
     */
    protected abstract int getMaxMobile();

    /**
     * TODO	设置内容长度不能超过
     */
    protected abstract int getMaxContentLength();

    /**
     * TODO	获取调用的url
     */
    protected abstract String getSendUrl(String content, List<String> phoneList);

    /**
     * TODO	处理服务商的返回值
     * @param ret
     * @return
     * @author jxl
     * @date 2014-6-18 下午12:57:49
     */
    protected abstract JSONObject disposeReturnVal(Object ret);
    public final JSONObject sendMsg(String content, String phone) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(phone);
        return sendMsg(content,list);
    }
    /**
     * 发送短信
     */
    public final JSONObject sendMsg(String content, List<String> phoneList) throws Exception {
        if(!allowSendSms){
            return simpleFailure("不允许发送短信");
        }
        if (phoneList == null || phoneList.size() <= 0) {
            throw new Exception("目标手机号个数为0！");
        }
        if (phoneList.size() > getMaxMobile()) {
            throw new Exception("目标手机号个数不能超过" + getMaxMobile());
        }
        if (content == null || content.equals("")) {
            throw new Exception("发送内容不可为空！");
        }
        if (content.length() > getMaxContentLength()) {
            throw new Exception("发送内容过多，长度不能超过" + getMaxContentLength());
        }
        String url = getSendUrl(content, phoneList);
        String responseValue = OkHttpUtil.doGet(url);
        return disposeReturnVal(responseValue);
    }
}
