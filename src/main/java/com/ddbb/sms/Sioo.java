/**
 * TODO
 * @author jxl
 * @date 2014-6-18  下午12:59:39
 */
package com.ddbb.sms;

import com.alibaba.fastjson.JSONObject;
import com.ddbb.utils.MD5Util;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 短信供应商：希奥短信平台
 *
 */
@Service
public class Sioo extends AbstractSMP {
	private String sp_url;//服务商提供的调用url
	private String our_userid;//服务商提供给我们的用户名
	private String our_pass;//服务商提供给我们的密码
	private String encode;//可选	客户所使用的编码格式,如encode=utf-8


	private String enterpriseCode="haoda";//企业代码
	public Sioo(){
		this.sp_url = "http://210.5.158.31/";//服务商提供的调用url
		this.our_userid="50146";//服务商提供给我们的用户名
		this.our_pass="K!Q4#2vL";//服务商提供给我们的密码haoda1122,要用MD5加密
	}



	/*
	-1	签权失败
	-2	未检索到被叫号码
	-3	 被叫号码过多
	-4	内容未签名
	-5	内容过长
	-6	余额不足
	-7	暂停发送
	-8	保留
	-9	定时发送时间格式错误
	-10	下发内容为空
	-11	账户无效
	-12	Ip地址非法
	-13	操作频率快
	-14	操作失败
	-15	拓展码无效(1-999)
	 */
	@Override
	protected JSONObject disposeReturnVal(Object ret) {
		String r = "";//r=0	操作成功
		String errMsg = "";
		if(ret == null || ret.toString().equals("")){
			r = "-100";
			errMsg="返回值为空";
		}else {
			r = ret.toString().split(",")[0];//成功返回 0,4213  0表示成功,4213表示MSGID
		}
		if(r.equals("-1")){
			errMsg = "签权失败";
		}else if(r.equals("-2")){
			errMsg = "未检索到被叫号码";
		}else if(r.equals("-3")){
			errMsg = "被叫号码过多";
		}else if(r.equals("-4")){
			errMsg = "内容未签名";
		}else if(r.equals("-5")){
			errMsg = "内容过长";
		}else if(r.equals("-6")){
			errMsg = "剩余短信不足";
		}else if(r.equals("-7")){
			errMsg = "暂停发送";
		}else if(r.equals("-8")){
			errMsg = "保留";
		}else if(r.equals("-9")){
			errMsg = "定时发送时间格式错误";
		}else if(r.equals("-10")){
			errMsg = "下发内容为空";
		}else if(r.equals("-11")){
			errMsg = "账户无效";
		}else if(r.equals("-12")){
			errMsg = "Ip地址非法";
		}else if(r.equals("-13")){
			errMsg = "操作频率快";
		}else if(r.equals("-14")){
			errMsg = "操作失败";
		}else if(r.equals("-15")){
			errMsg = "拓展码无效(1-999)";
		}

		JSONObject json = simpleSuccess();
		if(!r.equals("0")){//r=200	操作成功
			json = simpleFailure(errMsg);
		}
		return json;
	}

	@Override
	protected String getSendUrl(String content, List<String> phoneList) {
		StringBuffer sb=new StringBuffer();
		for(int i=0;i< phoneList.size();i++){//手机号码，同时发送给多个号码时，号码间用逗号分隔
			sb.append(phoneList.get(i));
			if(i != (phoneList.size() - 1)){
				sb.append(",");
			}
		}
		//http://210.5.158.31/hy?uid=test&auth=faea920f7412b5da7be0cf42b8c93759&mobile=13612345678&msg=hello&expid=0;
		String auth = MD5Util.encode(this.enterpriseCode+this.our_pass);
		String msg="";
		try {
			msg = URLEncoder.encode(content, "GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		String url = sp_url+"hy?uid="+our_userid+"&auth="+auth+"&mobile="+sb.toString()+"&expid=0&msg="+msg;
		if(encode!=null && !encode.equals("")){
			url += "&encode="+encode;
		}
		return url;

	}

	@Override
	protected int getMaxContentLength() {
		return 335;
	}

	@Override
	protected int getMaxMobile() {
		return 300;
	}

	public static void main(String[] args) throws Exception {
		List<String> phone = new ArrayList<>();
		phone.add("13472644829");
		new Sioo().sendMsg("a test message",phone);
		System.out.println("=========== done ====================");
	}
}
