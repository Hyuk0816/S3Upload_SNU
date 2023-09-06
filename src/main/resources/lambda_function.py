import boto3
import json
import os
import pandas as pd
import numpy as np

h2 = ['Zala', '라굿컴퍼니', '아이디어오션', '에이피그린', '오르바이오', '캠퍼스타운사업단', '프롬서울', '청소업체']
h4 = ['MOEZ', 'SEOULIST LAB', '시공간', '히어', '나눔비타민']
h5 = ['킬링턴머테리얼즈', '소테리아', '다이노즈', '리오', '어쿠스틱스테이지', '애니아이', '노트하우', '뉴지엄', '엘바이오', '맵시', '온아웃', 'HandU', '메타파머스', '고이', '베리타스바이오테라퓨틱스']


def preprocess_data(csv_data):
    # Pandas로 CSV 파일 읽기
    df = pd.read_csv(csv_data, encoding='utf-8-sig')
    col = ['발생일자', '발생시간', '카드번호', '이름', '조직1', '조직2', '조3', '조4', '이벤트상태', '직급',
           '기기명', '설치위치', '기기코드', '현재상태', '사용자', '조치시각', '조치자', '조치내역', '사진유무', '개인식별번호']
    df.columns = col
    df['조직2'].dropna(inplace=True)
    df = df[['발생일자', '이름', '조직2']]
    df[df['조직2'].isnull()]
    df = df.drop_duplicates(['발생일자', '이름', '조직2'])
    df['발생일자'] = df['발생일자'].str.replace("2023년", "")
    df['출입횟수'] = 1
    df = df.groupby(['발생일자', '조직2']).sum('출입횟수')
    df.reset_index(inplace=True)


    df = pd.pivot_table(df,
    index='조직2',
    columns='발생일자',
    values='출입횟수',
    fill_value=0)  # NaN 값을 0으로 채워줌

# 'h5' 리스트에 있는 값을 '조직2' 컬럼의 고유 값들에 추가

    if df.index.any() in h5:
        for i in h5:
            if i not in df.index:
                df.loc[i] = 0
    elif df.index.any() in h2:
        for i in h2:
            if i not in df.index:
                df.loc[i] = 0
    else:
        for i in h4:
            if i not in df.index:
                df.loc[i] = 0

# 인덱스를 정렬
    df = df.sort_index()

# 다시 pivot 테이블 형태로 변환
    df.reset_index(inplace=True)

    # NaN 값을 0으로 대체
    def count_nonzero(row):
        return np.count_nonzero(row.values[1:])

    df['총출근수'] =df.apply(count_nonzero, axis=1)
    df = df.replace(0, "")
    return df

s3 = boto3.client('s3')

def lambda_handler(event, context):
    # 트리거 이벤트에서 소스 버킷과 객체 키 가져오기
    source_bucket = event['Records'][0]['s3']['bucket']['name']
    key = event['Records'][0]['s3']['object']['key']

    # 대상 버킷 설정
    destination_bucket = 'down-snu'  # 대상 버킷 이름을 여기에 입력하세요

    # 객체를 대상 버킷으로 복사
    try:

        # 전처리할 CSV 데이터 다운로드
        csv_data = '/tmp/' + key
        s3.download_file(source_bucket, key, csv_data)

        # 데이터 전처리 함수 호출
        df = preprocess_data(csv_data)

        # 전처리된 데이터를 다운로드 버킷에 업로드
        df.to_csv('/tmp/preprocessed_data.csv', index=False, encoding='utf-8-sig')



        df = df.set_index('조직2')

        if any(item in df.index for item in h2):
            s3.upload_file('/tmp/preprocessed_data.csv', destination_bucket, 'H2/' + key)
        elif any(item in df.index for item in h4):
            s3.upload_file('/tmp/preprocessed_data.csv', destination_bucket, 'H4/' + key)
        else:
            s3.upload_file('/tmp/preprocessed_data.csv', destination_bucket, 'H5/' + key)



        return {
            'statusCode': 200,
            'body': '전처리 및 저장 완료'
        }
    except Exception as e:
        print(f"파일 복사 및 전처리 중 오류 발생: {str(e)}")
        return {
            'statusCode': 500,
            'body': '오류 발생'
        }
