### JPQL 과 SQL
- JPA는 SQL을 추상화한 JPQL이라는 객체 지향 쿼리 언어 제공
- SQL과 문법은 유사, SELECT, FROM, WHERE, GROUP BY, HAVING, JOIN 지원
- JPQL은 엔티티를 대상으로 쿼리
- SQL은 데이터 베이스 테이블을 대상으로 쿼리


## JPQL
- 테이블이 아닌 객체를 대상으로 검색하는 객체 지향 쿼리
- SQL을 추상화해서 특정 데이터베이스 SQL에 의존 X
- JPQL을 한마디로 정의하면 객체지향 SQL

## JPA
- JPA의 모든 데이터변경은 트랜잭션안에서 일어나야한다.

## 가장 중요한 2가지
- 객체와 관계형 데이터베이스 매핑하기
- 영속성 컨테스트

## 영속성 컨테스트
- JPA를 이해하는데 가장 중요한 용어
- "엔티티를 영구 저장하는 환경"이라는 뜻
- EntityManager.persist(entity);

### 엔티티매니저? 영속성 컨테스트?
- 영속성 컨텍스트는 논리적인 개념
- 눈에 보이지 않는다.
- 엔티티 매니저를 통해서 영속성 컨텍스트에 접근

### J2SE환경
- 엔티티 매니저와 영속성 컨텍스트가 1:1

### J2EE, 스프링 프레임워크같은 컨테이너 환경
- 엔티티 매니저와 영속성 컨텍스트가 N:1

## 엔티티의 생명주기

### 비영속(new/transient)
- 영속성 컨텍스트와 전혀 관계가없는 새로운 상태

```
//객체를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원");
```  

### 영속(managed)
- 영속성 컨텍스트에 관리되는상태

```
EntityManagerFactory emf =  Persistence.createEntityManagerFactory("hello");
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

//객체를 저장한 상태(영속)
em.persist(member);

```

### 준영속(detached)
- 영속성 컨텍스트에 저장되었다가 분리된 상태

```
//회원 엔티티를 영속성 컨텍스트에서 분리, 준영속 상태
em.detach(member);
```

### 삭제(removed)
- 삭제된 상태

```
//객체를 삭제한 상태(삭제)
em.remove(member)
```

## 영속성 컨텍스트의 이점
- 1차 캐시
- 동일성(identity) 보장
- 트랜잭션을 지원하는 쓰기 지연(transactional write-behind)
- 변경 감지(dirty checking)
- 지연 로딩(Lazy Loading)

## 엔티티 조회, 1차 캐시
```
//엔티티를 생성한 상태(비영속)
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

// 엔티티를 영속
em.persist(member);
```

### 1차 캐시에서 조회
```
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");

// 1차 캐시에 저장됨
em.persist(member);

// 1차 캐시에서 조회
Member findMember = em.find(Membmer.class, "member1");
```

### 데이터베이스에서 조회
```
Member findMember = em.find(Membmer.class, "member2");
```
1. find("member2") 1차캐시에 없음
2. DB 조회
3. 1차 캐시에 저장
4. 반환

## 영속 엔티티의 동일성 보장
```
Member a = em.find(Member.class, "member1");
Member b = em.find(Member.class, "member1");

System.out.println(a == b); // 동일성 비교 true
```
- 1차 캐시로 반복 가능한 읽기(REPEATABLE READ)등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공


## 엔티티 등록
### 트랜잭션을 지원하는 쓰기 지연
```
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
//엔티티 매니저는 데이터 변경시 트랜잭션을 시작해야 한다.
transaction.begin(); // [트랜잭션] 시작

em.persist(memberA);
em.persist(memberB);
// 여기까지 INSERT SQL을 데이터베이스에 보내지 않는다.

// 커밋하는 순간 데이터베이스에 INSERT SQL을 보낸다.
transaction.commit(); // [트랜잭션] 커밋
```

## 엔티티 수정
### 변경 감지
```
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
transaction.begin(); // [트랜잭션] 시작

// 영속 엔티티 조회
Member memberA = em.find(Member.class, "memberA");

// 영속  엔티티 데이터 수정
memberA.setUsername("hi");
memberA.setAge(10);

// em.update(member) 이런 코드가 있어야 하지 않을까?

transaction.commit(); // [트랜잭션] 커밋
```


## 변경 감지(Dirty Checkking)
- 영속성 컨텍스트안에서 일어나는일
1. flush()
2. 엔티티와 스냅샷 비교
3. UPDATE SQL 생성(쓰기 지연 SQL 저장소)
4. flush
5. commit


## 엔티티 삭제
```
Member memberA = em.find(Member.class, "memberA");
em.remove(memberA);
```

## 플러시
- 영속성 컨텍스트의 변경내용을 데이터베이스에 반영
