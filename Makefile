.PHONY: help up down run test format check clean

help:
	@echo "make up      - start Postgres + Kafka (Docker)"
	@echo "make down    - stop Postgres + Kafka"
	@echo "make run     - run the app"
	@echo "make test    - run the test suite"
	@echo "make format  - auto-format code (Spotless)"
	@echo "make check   - tests + format check + Checkstyle (what CI runs)"
	@echo "make clean   - remove build output"

up:
	docker compose up -d

down:
	docker compose down

MVNW := "$(CURDIR)\mvnw.cmd"

run:
	$(MVNW) spring-boot:run

test:
	$(MVNW) test

format:
	$(MVNW) spotless:apply

check:
	$(MVNW) clean verify

clean:
	$(MVNW) clean
