🏷️ 프로젝트 이름: Flash Deal
이 이름은 프로젝트의 핵심 도전 과제인 선착순 한정 판매 시의 대규모 트래픽 폭주 상황을 가장 명확하게 나타냅니다.

이 프로젝트는 다음 기술 스택을 활용하여 Flash Deal 상황에서 시스템의 안정성과 성능을 검증하는 데 초점을 맞춥니다:

성능 코어: Spring Boot (WebFlux), GraphQL, MongoDB

속도 및 안정성 계층: Redis (재고 카운터 및 캐싱), Apache Kafka (비동기 주문 및 완충)

인프라: Kubernetes

테스트 및 분석: k6, OpenTelemetry, Prometheus, Grafana

이 스택을 통해 Flash Deal이 시작될 때 Redis의 원자적 연산을 통한 재고 초과 방지 훈련과, Kafka를 통한 주문 트래픽의 안정적인 수용 능력을 집중적으로 테스트하게 됩니다.
