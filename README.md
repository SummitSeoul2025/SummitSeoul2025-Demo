# Spring Boot AWS Bedrock 프로젝트

이 프로젝트는 AI/ML 기능을 위해 AWS Bedrock과 통합된 Spring Boot 애플리케이션입니다.

## 프로젝트 개요

이 프로젝트는 다음을 사용하는 Spring Boot 웹 애플리케이션입니다:
- Spring Boot 3.2.3
- Java 17
- Thymeleaf 템플릿 엔진
- AI/ML 기능을 위한 AWS Bedrock SDK

## 사전 요구사항

- Java 17 이상
- Gradle
- Bedrock 접근 권한이 있는 AWS 계정
- AWS 자격 증명 구성

## 프로젝트 빌드

프로젝트를 빌드하려면 다음을 실행하세요:

```bash
./gradlew build
```

## 애플리케이션 실행

로컬에서 애플리케이션을 실행하려면:

```bash
./gradlew bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 시작됩니다.

## 프로젝트 의존성

주요 의존성:
- Spring Boot Starter Web
- Spring Boot Starter Thymeleaf
- AWS SDK for Bedrock
- Jackson Databind

## AWS 구성

Bedrock 서비스를 사용하기 위해 AWS 자격 증명을 올바르게 구성해야 합니다. 다음과 같은 방법으로 구성할 수 있습니다:
1. `~/.aws/credentials`에 AWS 자격 증명 설정
2. 환경 변수 사용
3. AWS 인프라에서 실행 시 IAM 역할 사용

## 테스트

테스트를 실행하려면:

```bash
./gradlew test
```

## 기여하기

이슈와 풀 리퀘스트는 언제든 환영합니다.

## 라이선스

이 프로젝트는 오픈 소스이며 [MIT 라이선스](LICENSE)에 따라 사용할 수 있습니다.


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