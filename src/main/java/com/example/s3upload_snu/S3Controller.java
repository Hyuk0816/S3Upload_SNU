package com.example.s3upload_snu;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {


    AmazonS3 amazonS3 = AmazonS3ClientBuilder.standard().withRegion(Regions.AP_NORTHEAST_2).build();

    public S3Controller(AmazonS3 amazonS3){
        this.amazonS3 = amazonS3;
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file){
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            PutObjectRequest request = new PutObjectRequest("upload-snu",file.getOriginalFilename(), file.getInputStream(), metadata);
            //PutObjectResult result = amazonS3.putObject(request);
            return ResponseEntity.ok("Success");

        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Fail: " + e.getMessage());
        }
    }

    @GetMapping("/search/{folder}")
    @ResponseBody
    public List<S3ObjectSummary> listobjectsInFolder(@PathVariable String folder){
        String bucketName = "down-snu";
        String prefix = folder + "/";
        ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName).withPrefix(prefix);
        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return result.getObjectSummaries(); //s3 객체 목록 보여줌
    }

    @GetMapping("/download/{folder}/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String folder, @PathVariable String filename) throws IOException {
        String bucketName = "down-snu"; // S3 버킷 이름
        String key = folder + "/" + filename; // s3 폴더와 오브젝트를 동적으로 설정하여 필요한 거점별 데이터 다운

        S3Object s3Object = amazonS3.getObject(bucketName, key);
        InputStream inputStream = s3Object.getObjectContent();

        InputStreamResource resource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}


