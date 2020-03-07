package com.jilian.server.admin.controller;

import com.alibaba.fastjson.JSONObject;
import com.jilian.server.AliyunService;
import com.jilian.server.admin.service.AdminPointService;
import com.jilian.server.bean.PointRecord;
import com.jilian.server.jwt.PASS;
import com.jilian.server.result.Result;
import com.jilian.server.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Author: zhuanggangqing
 * @Description:
 * @Date: Create in 7:31 下午 2020/2/16
 */
@RestController
public class UploadController {
    @Autowired
    AliyunService aliyunService;
    @Autowired
    AdminPointService pointService;

    /**
     * 上传图片
     *
     * @param file
     * @param dir
     * @return
     * @throws Exception
     */
    @PostMapping("/api/admin/uploadImage")
    @PASS
    public Result uploadImage(@RequestParam("file") MultipartFile file, @RequestParam("dir") String dir) throws Exception {
        JSONObject res = aliyunService.upload(file, dir);
        return ResultFactory.buildSuccessResult(res);
    }

    /**
     * 上传批量发放积分模板
     *
     * @param file
     * @param mallID
     * @return
     * @throws Exception
     */
    @PostMapping("/api/admin/uploadFile")
    @PASS
    public Result uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("mallID") String mallID) throws Exception {
        List<PointRecord> res = pointService.distributeAll(file, mallID);
        return ResultFactory.buildSuccessResult(res);
    }
}
