### JPQL 과 SQL
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법은 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- JPQL은 엔티티를 대상으로 쿼리
- SQL은 데이터 베이스 테이블을 대상으로 쿼리


## JPQL
- 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존 X
- JPQL을 한마디로 정의하면 객체지향 SQL

### JPA
- JPA의 모든 데이터변경은 트랜잭션안에서 일어나야한다.

