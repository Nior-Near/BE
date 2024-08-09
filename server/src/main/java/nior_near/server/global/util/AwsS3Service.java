package nior_near.server.global.util;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import nior_near.server.global.common.AwsS3;
import nior_near.server.global.common.ResponseCode;
import nior_near.server.global.error.handler.AwsS3Handler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service implements FileService {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public AwsS3 upload(MultipartFile multipartFile, String directoryName) throws IOException {
        File file = convertMultipartFileToFile(multipartFile)
                .orElseThrow(() -> new AwsS3Handler(ResponseCode.S3_UPLOAD_FAIL));

        return upload(file, directoryName);
    }

    public void remove(Object object) {
        AwsS3 awsS3 = (AwsS3)object;

        if (!amazonS3.doesObjectExist(bucket, awsS3.getKey())) {
            throw new AmazonS3Exception("Object " + awsS3.getKey() + " does not exist!");
        }
        amazonS3.deleteObject(bucket, awsS3.getKey());
    }

    private AwsS3 upload(File file, String directoryName) {
        String key = randomFileName(file, directoryName);
        String path = putS3(file, key);

        removeFile(file);

        return AwsS3
                .builder()
                .key(key)
                .path(path)
                .build();
    }

    private String randomFileName(File file, String directoryName) {
        return directoryName + "/" + UUID.randomUUID() + file.getName();
    }

    private String putS3(File uploadFile, String fileName) {
        amazonS3.putObject(new PutObjectRequest(bucket, fileName, uploadFile)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return getS3(bucket, fileName);
    }

    private String getS3(String bucket, String fileName) {
        return amazonS3.getUrl(bucket, fileName).toString();
    }

    private void removeFile(File file) {
        file.delete();
    }

    private Optional<File> convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {

        File file = new File(System.getProperty("user.dir") + "/" + multipartFile.getOriginalFilename());

        // 이미 파일이 있을 시에, 성공적인 파일 생성을 위해 삭제
        if(file.exists()) {
            FileSystemUtils.deleteRecursively(file);
        }

        if(file.createNewFile()) {
            try(FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }
            return Optional.of(file);
        }

        return Optional.empty();
    }


}
