package cn.irving.api;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Irving on 2020.04.05
 */
@Component
public class AliyunCloudService {

    private Logger logger = LoggerFactory.getLogger(AliyunCloudService.class);

    @Value("${aliyun.accesskey}")
    private String ACCESS_KEY;
    @Value("${aliyun.serectkey}")
    private String SECRET_KEY;
    /**
     * 仓库
     */
    @Value("${aliyun.bucket}")
    private String BUCKET;
    /**
     * 阿里云外网访问地址
     */
    @Value("${aliyun.cdn.url}")
    public String ALIYUN_UPLOAD_SITE;
    /**
     * 阿里云endPoint
     */
    @Value("${aliyun.endPoint}")
    public String ALIYUN_ENDPOINT;

    public String upload(MultipartFile file, String fileName) {

        try {

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(ALIYUN_ENDPOINT, ACCESS_KEY, SECRET_KEY);

            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, fileName, file.getInputStream());

            // 如果需要上传时设置存储类型与访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            // 上传文件。
            ossClient.putObject(putObjectRequest);

            // 关闭OSSClient。
            ossClient.shutdown();

            return putObjectRequest.getKey();

        } catch (Exception ex) {
            logger.info("[阿里云OSS上传异常]" + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

}
