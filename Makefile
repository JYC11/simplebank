.PHONY: jooq
jooq:
	rm -rf build/org/jooq/generated && ./gradlew generateJooq --stacktrace

.PHONY: local-infra
local-infra: ## Setup local infrastructure with docker compose
	 docker compose -f docker-compose.infra.yaml up -d

.PHONY: local-infra-down
local-infra-down: ## Setup local infrastructure with docker compose
	 docker compose -f docker-compose.infra.yaml down