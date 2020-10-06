# SpringBoot-Sample-Admin Project

> 스프링 부트를 사용해서 만든 간단한 쇼핑몰 Admin Page

HTTP API를 이용해서 주문, 사용자, 배송 관련 CRUD를 만들었고 Thymeleaf를 뷰 템플릿으로 사용해서 만들었습니다. 

**main화면**

![main frame](https://user-images.githubusercontent.com/34825405/95225105-62932a00-0836-11eb-966d-770515f02d71.png)

**사용자 관리 화면**

![Untitled](https://user-images.githubusercontent.com/34825405/95225262-8fdfd800-0836-11eb-9639-b5e509a8cd26.png)

# 의존성 및 환경



- Java 1.8
- SpringBoot 2.1.8.RELEASE
- Thymeleaf
- Admin LTE
- MySQL
- Gradle
- IDE: IntelliJ Ultimate

# 설치



#### 1. git clone

```bash
git clone https://github.com/bosuksh/springProject.git
```

##### 2. db연결: `resources` 디렉토리 밑에 있는 `application.yaml` 파일에 db정보를 추가

```yaml
spring:
  datasource:
    url: {db_url/db_name}
    username: {id}
    password: {password}
```

# 실행



#### 1. IDE로 실행

IntelliJ를 실행 후 프로젝트 import 후 Application 실행

#### 2. Gradle로 실행

```bash
$ ./gradlew build && java -jar build/libs/study-0.1.0.jar
```



# To-Do

- [ ]  Spring Security적용
- [ ]  test 코드 추가
- [ ]  어드민 페이지와 연결할 수 있는 실제 웹서비스 개발
- [ ]  API를 더 Restful하게 리팩토링
- [ ]  클라우드 빌드 배포
- [ ]  빌드 배포 자동화
