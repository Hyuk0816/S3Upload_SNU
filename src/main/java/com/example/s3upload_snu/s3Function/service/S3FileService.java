package com.example.s3upload_snu.s3Function.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.example.s3upload_snu.config.jwt.TokenProvider;
import com.example.s3upload_snu.exception.FileUploadFailedException;
import com.example.s3upload_snu.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class S3FileService {

    private final AmazonS3Client amazonS3Client;
    @Value("${uploadBucket}")
    private String uploadBucket;

    @Value("${downBucket}")
    private String downBucket;

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    /**
     *
     * @param file
     * @param  token
     * @return
     * @throws FileUploadFailedException
     * @apiNote 파일 업로드 api
     */
    public ResponseEntity<?> uploadFile(MultipartFile file, String token) throws FileUploadFailedException {
        String email = tokenProvider.getEmailBytoken(token);
        if(userRepository.findByEmail(email)!=null){
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            try(InputStream inputStream = file.getInputStream()){
                amazonS3Client.putObject(new EncryptedPutObjectRequest(uploadBucket, file.getOriginalFilename(), inputStream,objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));

            }catch (Exception e){
                log.error("Amazon S3 파일 업로드 실패: {}", e.getMessage(), e);
                throw new FileUploadFailedException("파일 업로드에 실패했습니다");
            }
            return ResponseEntity.ok("파일 업로드에 성공했습니다");
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다");
        }
    }

    /**
     *
     * @param folder
     * @param filename
     * @return
     * @throws IOException
     * @apiNote 파일 다운로드 api
     */

    public ResponseEntity<?> downloadFile(String folder, String filename, String token) throws IOException {

        String email = tokenProvider.getEmailBytoken(token);
        if(userRepository.findByEmail(email)!=null){
            String key = folder + "/" + filename; // s3 폴더와 오브젝트를 동적으로 설정하여 필요한 거점별 데이터 다운

            S3Object s3Object = amazonS3Client.getObject(downBucket, key);
            InputStream inputStream = s3Object.getObjectContent();

            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저를 찾을 수 없습니다");
        }

    }

    public ResponseEntity<?> objectsInBucket(String folder,String token){
        String email = tokenProvider.getEmailBytoken(token);
        if(userRepository.findByEmail(email)!=null){
            String prefix = folder + "/";
            ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(downBucket).withPrefix(prefix);
            ListObjectsV2Result result = amazonS3Client.listObjectsV2(request);
            return ResponseEntity.ok(result.getObjectSummaries());
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유저가 없습니다");
        }
    }






}
