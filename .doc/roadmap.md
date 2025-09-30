### 프로젝트 로드맵 (Flash Deal)

#### 1) 단계별 로드맵
- **Phase 0 — 프로젝트 스캐폴딩**
  - 앱 초기화(WebFlux), 기본 라우팅 및 `/health`
  - Observability 기본 세팅(OTel Collector, Prometheus, Grafana, Loki, Promtail)
  - k6 컨테이너 런타임 베이스 및 샘플 스크립트 구성

- **Phase 1 — 핵심 도메인 모델링/기본 기능**
  - 상품/재고/주문 모델 정의(Mongo 스키마)
  - GraphQL 스키마: 상품 조회, 재고 조회, 주문 생성 Mutation(기본)
  - Redis 재고 카운터(원자적 감소) PoC 및 단위/통합 테스트

- **Phase 2 — 과판매 방지와 일관성**
  - Redis 원자 연산 + 분산락(필요 시) 설계/적용
  - 주문 생성 → Kafka 이벤트 발행(비동기 처리) 파이프라인 구현
  - 실패/보상 시나리오(주문 실패/재고 롤백 정책) 정의

- **Phase 3 — 관측 강화와 병목 진단**
  - OpenTelemetry 트레이싱: 주요 경로(주문/재고) 스팬 태그 표준화
  - Prometheus 지표: 재고 감소 성공/실패, Kafka 레이턴시/레그
  - Grafana 대시보드: 핫패스 대시보드, 에러버짓 소모율 패널

- **Phase 4 — 부하 테스트와 튜닝**
  - k6 시나리오: 플래시 세일 트래픽 모델링(웜업/스파이크/지속)
  - 목표: 초당 X→Y rps 단계적 상향, P95 ≤ 150ms 유지
  - 병목 튜닝: Redis 파이프라이닝/클러스터링, Mongo 인덱스, Reactor 백프레셔

- **Phase 5 — 운영화/확장성**
  - Kubernetes 배포: HPA, 자원 리밋/요청, Readiness/Liveness 프로브
  - 카나리/블루그린 전략 초안, 장애 훈련(카오스 테스트) 초도 실시
  - 비용/성능 밸런스 검토와 SLO 확정

#### 2) 마일스톤 및 성과지표
- **M1**: 기본 기능+과판매 방지 PoC, P95 ≤ 200ms @ 500 rps
- **M2**: Kafka 비동기 파이프라인, 대시보드 1차 완성, P95 ≤ 180ms @ 1k rps
- **M3**: 튜닝 1차 완료, P95 ≤ 150ms @ 2k rps, 에러율 ≤ 0.5%
- **M4**: K8s 운영 배포, HPA 자동 확장 검증, 카오스 테스트 리포트

#### 3) 위험요인과 대응전략
- **Redis 단일 노드 병목/단일 장애점**: 샤딩/클러스터링, 복제, 장애 조치 리허설
- **Kafka 적체/지연**: 파티션 설계, 배치/압축 설정, 컨슈머 스케일 아웃
- **Mongo 인덱스/락 경합**: 적절한 Compound Index, 읽기/쓰기 분리(필요시)
- **스파이크 시 자원 한계**: HPA 튜닝, Rate Limiting/큐잉, 캐시 히트율 개선

#### 4) 품질보증/테스트 전략
- 단위/통합/계약 테스트 구성, Testcontainers로 Mongo/Redis/Kafka 통합 확인
- k6 테스트: 기능/스모크/스파이크/지속/스트레스 프로파일 분리
- 성능 회귀 방지: 주요 지표에 대한 게이팅(Thresholds) 정의

#### 5) 산출물
- 애플리케이션: `app/` (WebFlux, GraphQL, Actuator)
- 인프라/관측: `.devcontainer/docker-compose.yml`, `observability/*`
- 부하 스크립트: `k6/` (시나리오, thresholds, summary)
- 문서: `.doc/project.md`, `.doc/roadmap.md`