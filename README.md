# Moneybook Service

Spring Boot 기반의 마이크로서비스 애플리케이션  
개인 자산 관리 및 예산 분석 제공  
Redis와 JPA 활용으로 효율적인 데이터 처리 지원

---

## 프로젝트 구조

- **moneybook-service-user**: 사용자 인증 및 관리
- **moneybook-service-account**: 계좌 관리 (은행/카드)
- **moneybook-service-category**: 카테고리 관리 (계층형 구조)
- **moneybook-service-budget**: 예산 관리
- **moneybook-service-analyze**: 예산 및 계좌 분석
- **moneybook-service-shorturl**: 단축 URL 생성 및 관리

---

### 1. User Service

사용자 인증 및 관리 모듈  
이메일/토큰 기반 로그인과 OAuth2 소셜 로그인 제공  
Redis를 통한 토큰과 캐시 관리

#### 주요 기능
- 이메일 및 토큰 기반 회원가입/로그인
- OAuth2 소셜 로그인 (외부 제공자 연동)
- 사용자 정보 조회/수정 (이름, 비밀번호)
- 토큰 갱신 및 로그아웃
- 사용자 차단/해제 및 회원 탈퇴

#### API 엔드포인트
- **GET /user/info**: 사용자 정보 조회
- **POST /user/register**: 이메일 회원가입
- **POST /user/login/email**: 이메일 로그인
- **POST /user/login/token**: 토큰 로그인
- **POST /user/token/refresh**: 토큰 갱신
- **POST /user/logout**: 로그아웃
- **PUT /user/name**: 이름 변경
- **PUT /user/password**: 비밀번호 변경
- **DELETE /user**: 회원 탈퇴
- **PUT /user/manager/block**: 사용자 차단
- **PUT /user/manager/unblock**: 사용자 차단 해제

---

### 2. Account Service

사용자 계좌 관리 모듈 (은행/카드)  
Redis Pub/Sub을 통한 캐시 동기화  
Warmup 시뮬레이션으로 초기 성능 최적화

#### 주요 기능
- 계좌 생성, 조회, 수정, 삭제
- 사용자별 계좌 데이터 캐싱 및 동기화
- Warmup 시뮬레이션 (시작 시 더미 데이터 생성 및 롤백)

#### API 엔드포인트
- **GET /account**: 계좌 목록 조회
- **POST /account**: 계좌 생성
- **PUT /account/{accountIdx}**: 계좌 수정
- **DELETE /account/{accountIdx}**: 계좌 삭제

---

### 3. Category Service

계층형 카테고리 관리 모듈  
Redis를 통한 캐시 동기화  
하위 카테고리 삭제 포함

#### 주요 기능
- 계층형 카테고리 생성, 조회, 수정, 삭제
- 사용자별 카테고리 캐시 동기화
- Warmup 시뮬레이션 (시작 시 더미 데이터 생성 및 롤백)

#### API 엔드포인트
- **GET /category**: 카테고리 목록 조회
- **POST /category**: 카테고리 생성
- **PUT /category/{categoryIdx}**: 카테고리 수정
- **DELETE /category/{categoryIdx}**: 카테고리 삭제 (하위 포함)

---

### 4. Budget Service

사용자 예산 관리 모듈  
카테고리와 계좌 정보 연계  
Redis Pub/Sub을 통한 삭제 이벤트 처리 및 Warmup 시뮬레이션 지원

#### 주요 기능
- 예산 생성, 조회, 수정, 삭제
- 카테고리 및 계좌와 연계된 예산 관리
- Warmup 시뮬레이션 (시작 시 더미 데이터 생성 및 롤백)

#### API 엔드포인트
- **GET /budget**: 예산 목록 조회
- **POST /budget**: 예산 생성
- **PUT /budget/{budgetIdx}**: 예산 수정
- **DELETE /budget/{budgetIdx}**: 예산 삭제

---

### 5. Analyze Service

예산 및 계좌 분석 모듈  
월간/주간 분석 제공  
읽기 전용 트랜잭션으로 데이터 일관성 유지

#### 주요 기능
- 월간/주간 예산 및 계좌 분석
- 사용자별 분석 결과 생성

#### API 엔드포인트
- **POST /analyze**: 분석 요청

---

### 6. ShortUrl Service

단축 URL 생성 및 관리 모듈  
MongoDB 기반 데이터 저장  
URL 리다이렉션과 관리 기능 포함

#### 주요 기능
- 단축 URL 생성, 조회, 수정, 삭제
- 원본 URL로의 리다이렉션
- 전체 단축 URL 목록 조회

#### API 엔드포인트
- **GET /{shortKey}**: 단축 URL 리다이렉션
- **GET /shorturl/manager**: 단축 URL 목록 조회
- **POST /shorturl/manager**: 단축 URL 생성
- **PUT /shorturl/manager/{shortKey}**: 단축 URL 수정
- **DELETE /shorturl/manager/{shortKey}**: 단축 URL 삭제

---

## 인프라 모듈 구조

- **moneybook-infra-rds**: 관계형 데이터베이스(MySQL) 관리
- **moneybook-infra-redis**: Redis 캐싱 및 데이터 관리
- **moneybook-infra-redis-pubsub**: Redis Pub/Sub 메시징
- **moneybook-infra-redisson**: Redisson 기반 분산 락 및 캐싱
- **moneybook-infra-ehcache**: Ehcache 기반 로컬 캐싱
- **moneybook-infra-ehcache-redisson**: Ehcache와 Redisson 통합 캐싱
- **moneybook-infra-mongodb**: MongoDB 관리

---

### 1. RDS (Relational Database Service)

MySQL 기반 관계형 데이터베이스 관리 모듈  
샤딩(RANGE, MODULAR)과 Master-Slave 복제 지원  
JPA와 Querydsl 활용으로 데이터 액세스 최적화

#### 주요 기능
- Master-Slave 복제 및 읽기/쓰기 분리 (`@UseSlave`)
- 샤딩 지원 (`@UseShard`, RANGE/MODULAR 전략)
- 동적 데이터소스 라우팅 (RoutingDataSource)
- JPA 기반 엔티티 관리 (BaseEntity, MutableBaseEntity)
- Boolean-to-YN 변환 및 UUID 생성

---

### 2. Redis

Redis 활용 캐싱 및 데이터 저장 모듈  
단일 노드와 클러스터 모드 지원  
List 및 Key-Value 작업을 위한 래퍼 제공

#### 주요 기능
- Redis 단일/클러스터 연결 설정
- Key-Value 및 List 작업 (`RedisWrapper`)
- TTL 기반 캐시 만료 설정

---

### 3. Redis Pub/Sub

Redis Pub/Sub 기능 활용 메시징 모듈  
사용자 캐시 동기화 및 삭제 이벤트 처리  
`CommandMessage`를 통한 메시지 구조화

#### 주요 기능
- Pub/Sub 메시지 발행 (`RedisPublisher`)
- 메시지 수신 및 처리 (`RedisSubscriber`)
- 정의된 명령어 (`COMMAND_SYNC`, `COMMAND_DELETE`)

---

### 4. Redisson

Redisson 활용 분산 락 및 캐싱 모듈  
단일 노드와 클러스터 모드 지원  
고급 Redis 기능 제공

#### 주요 기능
- Redisson 클라이언트 설정 (단일/클러스터)
- 분산 락 관리 (`RLock`)

---

### 5. Ehcache

Ehcache 활용 로컬 캐싱 모듈  
힙 메모리 기반 캐시 제공  
TTL/TTI 정책 설정 가능

#### 주요 기능
- Ehcache 기반 캐시 관리 (`JCacheCacheManager`)
- 캐시 설정 동적 로드 (`EhcacheProperties`)
- 힙 크기 및 만료 정책 설정

---

### 6. Ehcache-Redisson

Ehcache와 Redisson 통합 캐싱 모듈  
로컬 캐시와 분산 락 결합  
데이터 일관성 유지

#### 주요 기능
- 로컬 캐시 관리 (`LocalCacheMap`, `LocalCacheWrapper`)
- Redisson 분산 락을 통한 캐시 업데이트 (`updateWithLock`)
- 캐시 조회/삽입/삭제 기능

---

### 7. MongoDB

NoSQL 데이터베이스 관리 모듈  
단일 노드와 복제셋 지원  
트랜잭션과 기본 엔티티 제공

#### 주요 기능
- MongoDB 단일/복제셋 연결 설정
- 문서 삽입/조회/수정/삭제 (`MongoDBWrapper`)
- 기본 엔티티 제공 (`BaseEntity`, `MutableBaseEntity`)

---

## 기술 스택

- **언어**: Java 17
- **프레임워크**: Spring Boot 3.4.3
- **데이터베이스**: MySQL (JPA), MongoDB (ShortUrl)
- **캐싱/메시징**: Redis (Pub/Sub, 캐시)
- **인증**: Spring Security, JWT, OAuth2
- **빌드 도구**: Gradle
- **의존성**:
    - `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-data-mongodb`
    - `spring-security`, `jjwt`, `redisson`, `ehcache`
    - `swagger-ui` (API 문서화)

---

## 실행

### Prerequisites
- Docker 3
- Java 17
- MySQL 8.0+
- Redis 6.0+
- MongoDB 4.0+

---

## 환경 설정

- **중앙 설정**: `moneybook-config` 모듈의 `src/main/resources/application.properties`에서 기본 설정 정의
- **서비스별 설정**: 각 서비스 모듈의 `src/main/resources/application.properties`에서 중앙 설정 오버라이드
- **환경 변수**: 필요 시 시스템 환경 변수 또는 Docker Compose에서 설정 값 주입

---

## 데이터베이스

- **설정**: `docker/docker-compose.yml` 파일에서 MySQL, Redis, MongoDB 컨테이너 정의
- **실행**: 프로젝트 루트에서 `docker-compose up -d` 명령어로 데이터베이스 실행
- **초기화**: 각 서비스 모듈 실행 시 자동 스키마 생성 (JPA `ddl-auto` 설정 기반)

---
