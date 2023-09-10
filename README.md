# S3Upload_SNU

## 0. 프로젝트 개요 
이 프로젝트는 에스원 경비 시스템에 찍히는 출입 로그로 서울대학교 캠퍼스타운 입주 기업들의 출근 데이터를 생성하기 위하여 만든 웹 애플리케이션 입니다. 이 깃허브에 올라온 웹 애플리케이션은 로컬 환경에서 지정된 AWS S3에 멀티파트 업로드 방식으로 로그 파일을 업로드 합니다. (RAW 데이터를 전처리하는 AWS Lambda 함수도 별도로 업로드 하였습니다.) 

## 1. 주요 기능 
- 파일 업로드: AWS SDK를 이용해서 로컬에서 AWS S3 버킷으로 멀티파트 업로드 형식으로 파일을 업로드 합니다. 파일 업로드 후 S3 버킷에 객체 생성 이벤트가 발생하여 자동으로 lambda 함수가 트리거 됩니다. 
- 파일 다운로드: AWS SDK를 이용해서 지정된 AWS S3 버킷에서 전처리된 데이터를 다운로드 합니다.
- 유동적 경로 생성: 후에 다룰 내용이지만 S3 버킷 내 저장된 데이터를 경로를 직접 설정하여 다운로드 할 수 있게 해줍니다.(왜 버킷 내 폴더를 여러개 두었는지 추후 설명)

## 2. AWS SDK & MultiPart Upload?? 

## 3. 내가 마주한 오류들 
1) EC2 Instance Metadata Service is disabled
   
![image](https://github.com/Hyuk0816/S3Upload_SNU/assets/88131652/09e416b4-98dd-4e76-ab16-0ea0606c177b)

  - AWS EC2 환경에서 어플리케이션을 실행하는 것이 아닌 로컬 환경에서 애플리케이션을 실행해서 AWS SDK를 이용할 때 나는 오류

    <PRE>
      <code>
        #해결방법 1. 아래코드를 application.yml에 추가해준다. 
        logging:
          level:
            com:
              amazonaws:
                  util:
                    EC2MetadataUtils: error
      </code>
    </PRE>

    - 해결방법 2:
      
      ![image](https://github.com/Hyuk0816/S3Upload_SNU/assets/88131652/24b4119a-c43c-4bd4-88fd-500a37a39b45)

      VM Option에 위의 구문을 추가한다. 


  2) Error creating bean with name 'org.springframework.cloud.aws.core.env.ResourceIdResolver.BEAN_NAME'
     
     ![image](https://github.com/Hyuk0816/S3Upload_SNU/assets/88131652/b6a4fda7-3153-4f8a-a95b-1210ab58f881)

     - 프로젝트 배포 시 기본으로 CloudFormation 구성을 시작하기 떄문에 설정한 CloudFormation이 없으면 프로젝트 실행이 되지 않는다.

     - 해결방법: application.yml에 aws.cloud.stack.auto:false 로 설정 (해당 기능을 사용하지 않도록 설정한다) 

       <PRE>
         <CODE>
               stack:
                  auto: false
         </CODE>
       </PRE>

     
     

    



## 4. 특이사항 




  



  



