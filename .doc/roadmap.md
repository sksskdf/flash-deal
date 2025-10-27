# Flash Deal

이 프로젝트의 핵심 도전 과제는 대규모 트래픽 폭주 상황을 시뮬레이션, 안정화하는 것<br>
다음 기술 스택을 활용하여 시스템의 안정성과 성능을 검증하는 데 초점을 맞춤

- **성능 코어**: Spring Boot (WebFlux), GraphQL, MongoDB<br>
- **속도 및 안정성 계층**: Redis (재고 카운터 및 캐싱), Apache Kafka (비동기 주문 및 완충)<br>
- **인프라**: Kubernetes<br>
- **테스트 및 분석**: k6, OpenTelemetry, Prometheus, Grafana

이 스택을 통해 트래픽의 안정적인 수용 능력을 집중적으로 테스트함

# 단계별 로드맵
- **1. 프로젝트 스캐폴딩**
  - 개발 컨테이너, 앱 초기화
  - Observability 기본 세팅(OTel Collector, Prometheus, Grafana, Loki, Promtail)
  - k6 컨테이너 런타임 베이스 및 샘플 스크립트 구성

- **2. 화면 디자인**
  - IA/사용자 흐름 디자인
  - 컴포넌트 디자인
  - 퍼블리싱 작업

- **3. 도메인 모델링**
  - 유비쿼터스 언어 정의
  - Aggregates 경계 정의
  - 도메인 이벤트 정의

- **4. 도메인 레이어 구현**
  - Aggregates 명세서 기반 TDD

- **5. 데이터 레이어 모델링**
  - MongoDB 스키마: 상품/재고/주문 모델 정의
  - Redis 전략: 사용 전략 정의, MongoDB 동기화 전략 설계
  - Kafka 이벤트: 토픽 설계, 이벤트 스키마 정의
  - GraphQL API: 상품/재고/주문 스키마 설계
  
- **6. 시스템 정책 작성**
  - 시스템 플로우 설계
  - 비즈니스 플로우 설계
  - 상태 관리 정책 설계
  - 에러 핸들링 설계

- **7. 구현**
  - **7.1 Infrastructure 레이어 구현**
    - Repository Port 인터페이스 정의 (Product, Inventory, Order)
    - MongoDB Persistence Adapter 구현 (Document, Mapper, Repository)
    - Redis Cache Adapter 구현 (재고 캐싱, 원자적 연산)
    - Kafka Messaging Adapter 구현 (Event Publisher/Consumer)
    - Infrastructure 설정 (MongoDB, Redis, Kafka Configuration)
    - Testcontainers 기반 통합 테스트 작성
  
  - **7.2 Application 레이어 구현**
    - Use Case 인터페이스 정의 (Port In)
    - Application Service 구현 (주문 생성, 재고 관리, 상품 조회)
    - Domain Service 구현 (비즈니스 로직)
    - Event Handler 구현 (도메인 이벤트 처리)
    - Application Service 테스트 작성
  
  - **7.3 GraphQL API 레이어 구현**
    - GraphQL Schema 정의 (Query, Mutation, Subscription)
    - Resolver 구현 (Product, Inventory, Order)
    - DataLoader 구현 (N+1 문제 해결)
    - Cursor-based Pagination 구현
    - Error Handling 구현 (Union Types)
    - GraphQL API 테스트 작성
  
  - **7.4 핵심 비즈니스 플로우 구현**
    - 주문 생성 플로우 (재고 확인 → 예약 → 주문 생성)
    - 결제 처리 플로우 (결제 요청 → 완료 → 재고 확정)
    - 재고 관리 플로우 (감소, 증가, 예약 해제)
    - 주문 취소 플로우 (취소 요청 → 재고 복구 → 환불)
    - 플래시딜 상태 관리 (시작, 종료, 품절 처리)
  
  - **7.5 이벤트 기반 아키텍처 구현**
    - 도메인 이벤트 발행 (OrderCreated, PaymentCompleted, InventoryReserved)
    - 이벤트 핸들러 구현 (비동기 처리)
    - 이벤트 스토어 구현 (이벤트 저장)
    - 이벤트 리플레이 기능 구현
    - 이벤트 기반 테스트 작성
  
  - **7.6 성능 최적화 구현**
    - Redis 파이프라이닝 구현 (배치 연산)
    - MongoDB 인덱스 최적화
    - Reactor 백프레셔 설정
    - Connection Pool 튜닝
    - 캐시 전략 구현 (Redis, Application Cache)
  
  - **7.7 보안 및 검증 구현**
    - 입력 검증 (Bean Validation)
    - 멱등성 처리 (Idempotency Key)
    - Rate Limiting 구현
    - 인증/인가 기본 구조
    - 보안 테스트 작성
  
  - **7.8 프론트엔드 연결 작업**
  
  - **7.9 모니터링 및 로깅 구현**
    - OpenTelemetry 트레이싱 설정
    - Prometheus 메트릭 수집
    - 구조화된 로깅 구현
    - Health Check 엔드포인트
    - 모니터링 테스트 작성

- **8. 관측 강화와 병목 진단**
  - OpenTelemetry 트레이싱: 주요 경로(주문/재고) 스팬 태그 표준화
  - Prometheus 지표: 재고 감소 성공/실패, Kafka 레이턴시/레그
  - Grafana 대시보드: 핫패스 대시보드, 에러버짓 소모율 패널

- **9. 부하 테스트와 튜닝**
  - k6 시나리오: 플래시 세일 트래픽 모델링(웜업/스파이크/지속)
  - 목표: 초당 X→Y rps 단계적 상향, P95 ≤ 150ms 유지
  - 병목 튜닝: Redis 파이프라이닝/클러스터링, Mongo 인덱스, Reactor 백프레셔

- **10. 운영화/확장성**
  - Kubernetes 배포: HPA, 자원 리밋/요청, Readiness/Liveness 프로브
  - 카나리/블루그린 전략 초안, 장애 훈련(카오스 테스트) 초도 실시
  - 비용/성능 밸런스 검토와 SLO 확정

# 마일스톤 및 성과지표
- 기본 기능+과판매 방지 PoC, P95 ≤ 200ms @ 500 rps
- Kafka 비동기 파이프라인, 대시보드 1차 완성, P95 ≤ 180ms @ 1k rps
- 튜닝 1차 완료, P95 ≤ 150ms @ 2k rps, 에러율 ≤ 0.5%
- K8s 운영 배포, HPA 자동 확장 검증, 카오스 테스트 리포트

# 위험요인과 대응전략
- **Redis 단일 노드 병목/단일 장애점**: 샤딩/클러스터링, 복제, 장애 조치 리허설
- **Kafka 적체/지연**: 파티션 설계, 배치/압축 설정, 컨슈머 스케일 아웃
- **Mongo 인덱스/락 경합**: 적절한 Compound Index, 읽기/쓰기 분리(필요시)
- **스파이크 시 자원 한계**: HPA 튜닝, Rate Limiting/큐잉, 캐시 히트율 개선

# 테스트 전략
- **k6 테스트**: 기능/스모크/스파이크/지속/스트레스 프로파일 분리
- **성능 회귀 방지**: 주요 지표에 대한 게이팅(Thresholds) 정의