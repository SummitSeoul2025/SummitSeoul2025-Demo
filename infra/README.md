# ChillApp Infrastructure

이 디렉토리는 ChillApp의 AWS 인프라 코드를 포함하고 있습니다. AWS CDK를 사용하여 TypeScript로 작성되었습니다.

## 사전 요구사항

* Node.js (>= 14.x)
* AWS CLI 설치 및 구성
* AWS CDK 설치 (`npm install -g aws-cdk`)

## 프로젝트 설정

1. 의존성 설치:
   ```
   cd infra
   npm install
   ```

2. 환경 부트스트랩 (처음 한 번만):
   ```
   cdk bootstrap
   ```

## 유용한 명령어

* `npm run build` - TypeScript 코드 컴파일
* `npm run watch` - 파일 변경 감지 및 컴파일
* `npm run test` - Jest를 사용한 단위 테스트 실행
* `cdk synth` - CloudFormation 템플릿 생성
* `cdk diff` - 현재 배포된 스택과 코드 비교
* `cdk deploy` - 인프라 배포

## 인프라 구성

* **VPC**: 2개의 가용 영역을 가진 VPC
* **ECS Cluster**: Fargate를 사용한 컨테이너 실행 환경
* **ECR Repository**: Docker 이미지 저장소
* **Load Balancer**: 애플리케이션 로드 밸런서
* **IAM Role**: Bedrock 접근 권한을 가진 태스크 역할
* **Secrets Manager**: Bedrock 설정을 위한 시크릿

## 배포

ChillApp 서비스를 배포하려면:

```bash
cdk deploy
```

배포 후, 출력된 로드 밸런서 DNS 이름으로 애플리케이션에 접근할 수 있습니다. 