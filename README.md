# 🌱 2023-TB-AI-Challenge (일 방식 혁신)
  * SKT 고객을 위한 AI 서비스 에이닷 A.
  * 이제 사내 시스템도 AI 회사에 걸맞게 진화가 필요 합니다.
  * '시크릿 T' 팀은 AI Chat bot을 구현하여 사내 업무 프로세스를 개선하고 생산성을 향상 시키기를 희망 합니다.

<br /><br />

# 🖥️ Demo 시연 및 테스트
  * YOUTUBE URL : https://youtu.be/mHzyYxkBXNw
  * 서비스 Demo
    * http://54.180.101.106:3000/user1 (사용자 1)
    * http://54.180.101.106:3000/user2 (사용자 2)
    * http://54.180.101.106:3000/user3 (사용자 3)
    * http://54.180.101.106:3000/user4 (사용자 4)
    * http://54.180.101.106:3000/user5 (사용자 5)

<br /><br />

# 📌 Architecture

| 컴포넌트      |           외부 API 및 라이브러리      | 용도                                           |                              공식 URL                               |
|-------------|-------------------------------------------------------------------------------------------------------------|---------------------------|--------------------------------------|
|   chatbot   |                                  |  Frontend 화면 구성                             |                                                                    | 
|             |          google firebase         |  요청/응답을 저장하기 위한 실시간 데이터 베이스          |      [링크](https://firebase.google.com/?hl=ko)                     |
|      -      |                   -              |                       -                       |                                                                    |
|   core      |                                  |  Backend 서버 구성                              |                                                                    |
|             |          google firebase         |  요청/응답을 저장하기 위한 실시간 데이터 베이스          |       [링크](https://firebase.google.com/?hl=ko)                    |
|             |     google natural Language API  |  사용자의 요청을 분석하기 위한 자연어 처리 API          |       [링크](https://cloud.google.com/natural-language?hl=ko)       |
|             |             KoAlpaca             |  사내 업무와 연관이 없는 범용적인 질문을 처리하기 위한 AI  |       [링크](https://github.com/Beomi/KoAlpaca)                     |


* chatbot pseudo code
```javascript

사용자의 대화 내역을 firebase로 부터 조회 하여 화면에 노출

if (사용자가 채팅창에 요청을 입력하면) {
  firebase에 해당 요청을 insert()
}

core 서버가 처리한 내용을 화면에 노출

```

* core pseudo code
```java

if (firebase에 요청에 insert 되면) {
  google natural language API로 요청을 단어 형태로 분석

  if (요청에 맞는 응답이 준비되어 있다면) {
    firebase에 응답을 insert()
  }

  if (요청에 맞는 응답이 없다면) {
     사내 서비스가 아닌 범용적인 질문으로 파악하여 KoAlpaca에 사용자의 요청을 질의
     응답 내용을 firebase에 insert()
  }
}

```

<br /><br />

# ⚙️ 개발 환경
  * JAVA 17
  * Spring Boot 3.1.5
  * React.js 13.4.0

<br /><br />

# 💬 Maintainers
  * API 사용을 위한 데이터는 암호화 되어 있습니다. Local 환경에서의 실행 등은 아래 메일로 문의해주세요.
  * byungjun.choi@sk.com
  * jusang.jung@sk.com
  * meantiger@sk.com
  * hahyuk.choi@sk.com
  * youngrae.kim@sk.com
