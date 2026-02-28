package com.mango.funflow.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.mango.funflow.common.Code;
import com.mango.funflow.config.OssConfig;
import com.mango.funflow.exception.BusinessException;
import com.mango.funflow.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * OSS 文件上传服务实现类
 * 提供通用的文件上传功能
 */
@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private OSS ossClient;

    @Autowired
    private OssConfig ossConfig;

    @Override
    public String uploadFile(MultipartFile file, String fileName) {
        try {
            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 设置文件元信息
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // 上传到 OSS
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    fileName,
                    inputStream,
                    metadata
            );
            ossClient.putObject(putObjectRequest);

            // 返回文件访问 URL
            return "https://" + ossConfig.getBucketName() + "." + ossConfig.getEndpoint() + "/" + fileName;

        } catch (Exception e) {
            throw new BusinessException(Code.SYSTEM_ERROR, "文件上传失败：" + e.getMessage());
        }
    }
}
