package com.ddbb.controller;


import com.ddbb.controller.response.ImagePath;
import com.ddbb.internal.utils.MD5Util;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

public class BaseController {
    //硬盘上的路径
    @Value("${cbs.imagesPath}")
    private String imagesPath;

    //url路径中的context
    @Value("${cbs.imagesUrlContext}")
    private String imagesUrlContext;


    public final static BaseResult OK = OK();
    public final static BaseResult ERROR = ERROR();


    public final static BaseResult OK(){
        return OK(null);
    }
    public final static BaseResult OK(Object data){
        return OK(data,"ok");
    }
    public final static BaseResult OK(Object data, String msg){
        return new BaseResult(0,msg,data);
    }

    public final static BaseResult RESULT(int code){
        return RESULT(code,"");
    }
    public final static BaseResult RESULT(int code, String msg){
        return RESULT(code,null,msg);
    }
    public final static BaseResult RESULT(int code, Object data, String msg){
        return new BaseResult(code,msg,data);
    }
    public final static BaseResult ERROR(){
        return ERROR("");
    }
    public final static BaseResult ERROR(Throwable e){
        return ERROR(e.getMessage());
    }
    public final static BaseResult ERROR(String err){
        return new BaseResult(-1,err);
    }

    /**
     * url相对路径转成绝对url路径
     * @param httpServletRequest
     * @param imageRelativePath  /u/4ed5cdf92902390b40472a8c3e7492fe/a/avatar.jpg
     * @return http://127.0.0.1:8080/ddbb_u_images/u/4ed5cdf92902390b40472a8c3e7492fe/a/avatar.jpg
     */
    protected String getImageAbsoluteUrl(HttpServletRequest httpServletRequest,String imageRelativePath){
        String url = httpServletRequest.getRequestURL().toString();//http://127.0.0.1:8080/nearby/coach
        int pos = url.indexOf("//");
        int pos2 = url.indexOf("/",pos+2);
        String url1 = url.substring(0,pos2);
        return url1 + "/" +imagesUrlContext+"/"+ imageRelativePath;
    }


}
