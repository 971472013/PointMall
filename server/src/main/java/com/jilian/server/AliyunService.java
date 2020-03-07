package com.jilian.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: zhuanggangqing
 * @Description: 阿里云api，处理发送短信，上传图片
 * @Date: Create in 11:10 上午 2020/2/28
 */
@Service
public class AliyunService {
    // 产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    // 产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "accessKeyId"; // TODO 改这里
    static final String accessKeySecret = "accessKeySecret"; // TODO 改这里

    static final String endpoint = "oss-cn-shenzhen.aliyuncs.com";
    static final String bucketName = "xmall123";

    public JSONObject upload(MultipartFile file, String dir) throws Exception {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        String originalFilename = file.getOriginalFilename();
        String url = "https://xmall123.oss-cn-shenzhen.aliyuncs.com/";
        String fileName = dir + "/" + new ObjectId().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        url += fileName;
        // 创建PutObjectRequest对象。
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, file.getInputStream());

        // 上传文件。
        ossClient.putObject(putObjectRequest);

        // 关闭OSSClient。
        ossClient.shutdown();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", fileName);
        jsonObject.put("url", url);
        return jsonObject;
    }

    public boolean sendAliyun(String phone, String code) throws ClientException {
        // 可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        // 初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phone);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName("积链商城"); // TODO 修改成自己的
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode("SMS_183155031"); // TODO 修改成自己的
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");
        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        // hint 此处可能会抛出异常，注意catch
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
            System.out.println("短信发送成功！");
            System.out.println(JSON.toJSON(sendSmsResponse));
            return true;
        } else {
            System.out.println("短信发送失败！");
            System.out.println(JSON.toJSON(sendSmsResponse));
            return false;
        }
    }
}
