# 가톨릭대학교 컴퓨터정보공학부 회의실 예약 시스템

## 🛠 개발한 기능
### 1️⃣ FAQ 관리 (CRUD)
- 자주 묻는 질문(FAQ)을 등록, 조회, 수정, 삭제 가능

### 2️⃣ 학생회비 납부자 검증
- 학생회비를 납부한 사용자만 특정 기능을 이용할 수 있도록 검증
- CRUD(생성, 조회, 수정, 삭제) 기능 제공

### 3️⃣ 예약 시스템
- 예약 생성, 조회, 수정, 삭제 (CRUD)
- 예약 유효성 검증 (중복 예약 방지, 시간 제한 등)

### 4️⃣ 예약 자동 알림 시스템 (Quartz Scheduler)
- 예약 확정 문자 자동 발송
- 예약 10분 전 알림 문자 자동 발송
- 기존 `@Scheduled` 대신 Quartz Scheduler를 사용하여 비동기적 스케줄링 처리  
  - 📌 [@Scheduled과 Quartz 스케쥴링 (문자 자동 발송 구현)](https://blog.naver.com/qjwlfk_/223730949560)

### 5️⃣ 예약 데이터 유지 및 성능 최적화
- Quartz Scheduler + MySQL 연동  
  - 기존 인메모리 방식 → MySQL 저장 방식 변경  
- 예약 내역 자동 삭제  
  - 30일이 지난 예약 내역은 자동 삭제하여 DB 성능 유지

---

## 🔧 리팩토링 작업
- 트랜잭션 최적화 (`@Transactional(readOnly = true)`)
- 코드 모듈화  
  - `ReservationService`의 Quartz Job 로직을 별도 클래스로 분리하여 가독성 향상
- N+1 문제 해결 (Lazy Loading)
- 패키지 구조 정리
- DTO 적용하여 API 유지보수 용이성 향상  
  - Entity와 API 응답(JSON) 분리  
  - 불필요한 데이터 노출 방지
- Mapper 적용  
  - DTO ↔ Entity 변환을 담당하는 별도 클래스를 분리하여 유지보수 편의성 증가

---

## 🚀 성능 및 유지보수를 위한 개선
### 1️⃣ Docker 및 배포 환경 구성
- Docker + EC2 + Nginx 기반 HTTPS 서버 배포
- 기존 Docker MySQL 컨테이너 → AWS RDS MySQL 전환  
  - 컨테이너 종료 시 DB 초기화 문제 해결

### 2️⃣ REST API 최적화
- DTO 활용하여 API 응답 최적화
- Operation 어노테이션 적용  
  - Swagger 문서에서 API 설명 추가  
  - 프론트엔드 개발자가 API 이해도를 높일 수 있도록 명확한 설명 제공

### 3️⃣ 시간 데이터 한국 표준시(KST) 적용
- 기본 MySQL UTC 저장 → `ZonedDateTime` 변환 후 KST 저장
- Quartz Scheduler의 예약 문자 발송 시 한국 시간으로 계산
- DB 조회 시 자동 KST 변환 적용

---

## 🛠 관련 기술 문제 해결 및 성능 개선 블로그
### 1️⃣ Session 방식 로그인 문제 해결
- 📌 [Session 방식 로그인의 iOS 서드파티 쿠키 문제(Safari ITP)](https://blog.naver.com/qjwlfk_/223790157592)
- 📌 [Session 방식 로그인 Samesite 쿠키 설정 이슈](https://blog.naver.com/qjwlfk_/223790105304)

### 2️⃣ 성능 최적화 및 유지보수 고려 사항
- 📌 [성능 및 유지 보수를 위해 개발 중 신경 쓴 것들 (1)](https://blog.naver.com/qjwlfk_/223740203735)
- 📌 [성능 및 유지 보수를 위해 신경 쓴 것들 (2)](https://blog.naver.com/qjwlfk_/223740216071)

---

## 📌 개발 환경
- Spring Boot
- JPA (Hibernate)
- MySQL (AWS RDS)
- Quartz Scheduler
- Docker, EC2, Nginx
- Swagger (API 문서 자동화)
- GitHub Actions (CI/CD 자동화 예정)
