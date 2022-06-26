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


### 플러시 발생
- 변경 감지
- 수정된 엔티티 쓰기 지연 SQL저장소에 등록
- 쓰기 지연 SQL 저장소의 쿼리를 데이터 베이스에 전송(등록, 수정, 삭제 쿼리)

### 영속성 컨텍스트를 플러시하는방법
- em.flush() - 직접호출
- 트랜잭션 커밋 - 플러시 자동호출
- JPQL 쿼리 실행 - 플러시 자동호출

### JPQL 쿼리 실행시 플러시가 자동으로 호출되는 이유
```
em.persist(memberA);
em.persist(memberB);
em.persist(memberC);
// 중간에 JPQL 실행
query = em.createQuery("select m from Member m", Member.class);
List<member> members = query.getResultList(); 
```

### 플러시 모드 옵션
- FlushModeType.AUTO - 커밋이나 쿼리를 실행할때 플러시 (기본값)
- FlushModeType.COMMIT - 커밋할때만 플러시

## 플러시는!
- 영속성 컨텍스트를 비우지 않음
- 영속성 컨텍스트의 변경내용을 데이터베이스에 동기화
- 트랜잭션이라는 작업 단위가 중요 -> 커밋직전에만 동기화 하면됌

## 준영속 상태
- 영속 -> 준영속
- 영속 상태의 엔티티가 영속성 컨텍스트에서 분리(detached)
- 영속성 컨텍스트가 제공하는 기능을 사용 못함

## 준영속 상태로 만드는 방법
- em.detach(entity) - 특정엔티티만 준영속 상태로 변환
- em.clear() - 영속성 컨텍스트를 완전히 초기화
- em.close() - 영속성 컨텍스트를 종료

## 엔티티 매핑소개
- 객체와 테이블 매핑: @Entity, @Table
- 필드와 컬럼 매핑: @Column
- 기본 키 매핑: @Id
- 연관 관계 매핑: @ManyToOne, @JoinColumn

### @Entity
- @Entity가 붙은 클래스는 JPA가 관리, 엔티티라 한다.
- JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 필수
- 주의
1. 기본생성자 필수(파라미터가 없는 public 또는 protected 생성자)
2. final 클래스, enum, interface, inner 클래스 사용 x
3. 저장할 필드에 final 사용 x
- 속성: name
1. JPA에서 사용할 엔티티 이름을 정한다
2. 기본값: 클래스 이름을 그대로 사용
3. 같은 클래스 이름이 없으면 가급적 기본값을 사용한다.
- @Table
1. 엔티티와 매핑할 테이블을 지정

## 데이터베이스 스키마 자동생성
- DDL을 애플리케이션 실행 시점에 자동생성
- 테이블 중심 -> 객체 중심
- 데이터 베이스 방언을 활용해서 데이터 베이스에 맞는 적절한 DDL 생성
- 생성된 DDL은 운영서버에서 사용하지 않거나, 적절히 다듬은 후 사용

## 데이터베이스 스키마 자동생성 주의!
- 운영장비에는 절대 create, create-drop, update 사용하면 안된다.
- 개발 초기 단계는 create 또는 update
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none

## DDL 생성 기능
- 제약조건 추가: 회원이름은 필수, 10자 초과 x
```
    @Column(nullable = false, length = 10)
```

- 유니크 제약 조건 추가
```
    
```
- DDL생성 기능은 DDL을 자동생할때만 사용되고 JPA의 실행 로직에는 영향을 주지 않는다.
   
## 요구사항 추가
1. 회원은 일반회원과 관리자로 구분해야한다
2. 회원가입일과 수정일이 있어야한다.
3. 회원을 설명할수있는 필드가 있어야한다. 이 필드는 길이 제한이 없다.

## 매핑 어노테이션 정리
- @Column : 컬럼 매핑
- @Temporal: 날짜 타입 매핑
- @Enumerated: enum 타입 매핑
- @Lob: BLOB, CLOB 매핑
- @Transient: 특정 필드를 컬럼에 매핑하지않음 (최신 버전 쓰는 사람은 LocalDate, LocalDateTime 사용)

### @Enumerated 사용시 주의
- ORDINAL 절대 사용 X STRING을 권장
- EnumType.ORDINAL: enum 순서를 데이터 베이스에 저장
- EnumType.STRING: enum 이름을 데이터베이스에 저장

## 기본키 매핑 방법
- 직접할당: @Id만 사용
- 자동생성: @GeneratedValue
1. IDENTITY: 데이터베이스에 위임, MYSQL
2. SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용, ORACLE @SequenceGenerator 필요
3. TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용 @TableGenerator 필요
4. AUTO: 방언에 따라 자동 지정

## IDENTITY 전략 - 특징
- 기본키 생성을 데이터베이스에 위임
- 주로 MYSQL, PostgreSQL, SQL Server, DB2에서 사용
  (예: MySQL의 AUTO_INCREMENT)
- JPA는 보통 트랜잭션 커밋 시점에 INSERT SQL 실행
- AUTO_INCREMENT는 데이터 베이스에 INSERT SQL을 실행한 이후에 ID 값을 알 수 있음
- IDENTITY 전략은 em.persist() 시점에 즉시 INSERT SQL을 실행하고 DB에서 식별자를 조회

## SEQUENCE 전략 - 특징
- 데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트(예: 오라클 시퀀스)
- 오라클, PostgreSQL, DB2, H2 데이터베이스에서 사용

## TABLE 전략 - 특징
- 키 생성 전용 테이블을 하나 만들어서 데이터 베이스 시퀀스를 흉내내는 전략
- 장점: 모든 데이터베이스에 적용 가능
- 단점: 성능

## 권장하는 식별자 전략
- 기본 키 제약조건: null 아님, 유일, 변하면 안된다.
- 미래까지 이 조건을 만족하는 자연키는 찾기 어렵다. 대리키(대체키)를 사용하자
- 예를 들어 주민등록번호도 기본 키로 적절하지않다.
- 권장: Long형 + 대체키 + 키 생성전략 사용
