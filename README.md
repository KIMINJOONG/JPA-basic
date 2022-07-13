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
---

# 요구사항 분석
1. 회원은 상품을 주문 할 수 있다.
2. 주문시 여러 종류의 상품을 선택 할 수 있다.

## 도메인 모델 분석
1. 회원과 주문의 관계: 회원은 여러번 주문 할 수있다.(일대다)
2. 주문과 상품의 관계: 주문할때 여러 상품을 선택 할 수 있다. 반대로 같은 상품도 여러번 주문 될 수 있다.
주문 상품이라는 모델을 만들어서 다대다 관계를, 일다대, 다대일 관계로 풀어냄
   
---
# 데이터 중심 설계의 문제점
1. 현재 방식은 객체 설계를 테이블 설계에 맞춘 방식
2. 테이블의 외래키를 객체에 그대로 가져옴
3. 객체 그래프 탐색이 불가능
4. 참조가 없으므로 UML도 잘못됨

---
# 연관관계 매핑 기초
## 목표
- 객체와 테이블 연관관계의 차이를 이해
- 객체의 참조와 테이블의 외래 키를 매핑
- 용어 이해
1. 방향(Direction): 단방향, 양방향
2. 다중성(Multiplicity): 다대일(N:1), 일대다(1:N), 일대일(1:1), 다대다(N:M)이해
3. 연관 관계의 주인(Owner): 객체 양방향 연관관계는 관리 필요

## 객체를 테이블에 맞춰 모델링(식별자로 다시 조회, 객체 지향적인 방법이 아니다.)
```
// 조회
Member findMember = em.find(Member.class, member.getId());

// 연관 관계가 없음
Team findTeam = em.find(Team.class, findTeamId);
```

## 객체를 테이블에 맞추어 데이터 중심으로 모델링하면, 협력 관계를 만들 수 없다.
- 테이블은 외래 키로 조인을 사용해서 연관된 테이블을 찾는다.
- 객체는 참조를 사용해서 연관된 객체를 찾는다.
- 테이블과 객체 사이에는 이런 큰 간격이 있다.

## 객체 지향 모델링(연관 관계 저장)
```
// 팀 저장
Team team = new Team();
team.setName("TeamA");
em.persist(team);

// 회원 저장
Member member = new Member();
member.setUsername("member1");
member.setTeam(team); // 단방향 연관관계 설정, 참조 저장
em.persist(member);
```

## 객체 지향 모델링(연관 관계 수정)
```
// 새로운 팀 B
Team teamB = new Team();
teamB.setName("TeamB");
em.persist(teamB);

// 회원1에 새로운 팀 B 설정
member.setTeam(teamB);
```
---
# 연관관계의 주인과 mappedBy
- mappedBy = JPA의 멘탈붕괴 난이도
- mappedBy 처음에는 이해하기 어렵다.
- 객체와 테이블간의 연관관계를 맺는 차이를 이해해야한다.

## 객체와 테이블이 관계 맺는 차이
### 객체 연관 관계 = 2개
- 회원 -> 팀 연관관계 1개(단방향)
- 팀 -> 회원 연관관계 1개(단방향)

### 테이블 연관 관계 = 1개
- 회원 <-> 팀의 연관관계 1개(양방향)

# 객체의 양방향 관계
- 객체의 양방향 관계는 사실 양방향 관계가 아니라 서로 다른 단방향 관계 2개다.
- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야한다.
- A -> B
- B -> A

```
class A {
  B b;
}

class B {
  A a;
}
```

# 테이블의 양방향 연관 관계
- 테이블은 외래 키 하나로 두 테이블의 연관 관계를 관리
- MEMBER.TEAM_ID 외래 키 하나로 양방향 연관관계 가짐(양쪽으로 조인할수있다.)

```
SELECT *
FROM MEMBER M
JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

SELECT *
FROM TEAM T
JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID
```

# 연관관계의 주인(Owner)
## 양방향 매핑 규칙
- 객체의 두 관계중 하나를 연관관계의 주인으로 지정
- 연관관계의 주인만이 외래 키를 관리(등록, 수정)
- 주인이 아닌쪽은 읽기만 가능
- 주인은 mappedBy 속성 사용 x
- 주인이 아니면 mappedBy 속성으로 주인 지정

# 누구를 주인으로?
- 외래키가 있는곳을 주인으로 정해라
- 여기서는 Member.team이 연관관계의 주인
1. 진짜매핑 - 연관관계의 주인 (Member.team)
2. 가짜매핑 - 주인의 반대편(Team.members)

---
#양방향 매핑시 가장 많이 하는 실수
- 연관관계의 주인에 값을 입력하지 않음
```
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setMember("member1");

// 역방향(주인이 아닌 방향)만 연관관계 설정
team.getMembers().add(member);
em.persist(member);
```

# 양방향 매핑시 연관관계의 주인에 값을 입력해야한다.
- 순수한 객체 관계를 고려하면 항상 양쪽 다 값을 입력해야한다.

```
Team team = new Team();
team.setName("TeamA");
em.persist(team);

Member member = new Member();
member.setMember("member1");
member.setTeam(team);
em.persist(member);

team.getMembers().add(member);
```
## 양방향 연관관계 주의 - 실습
- 순수 객체 상태를 고려해서 항상 양쪽에 값을 설정하자.
- 연관 관계 편의 메소드를 생성하자
  ```
  // Team.java
  
  public void addMember(Member member) {
        member.setTeam(this);
        members.add(member);
    }
  
  // main.java
  team.addMember(member); // 연관관계 편의 메소드
  ```
- 양방향 매핑시에 무한 루프를 조심하자 
  예: toString(), lombok, JSON 생성 라이브러리
  
#양방향 매핑 정리
- 단방향 매핑만으로도 이미 연관관계 매핑은 완료
- 양방향 매핑은 반대 방향으로 조회(객체 그래프 탐색) 기능이 추가 된 것뿐
- JPQL에서 역방향으로 탐색할 일이 많음
- 단방향 매핑을 잘하고 양방향은 필요할 때 추가해도 됨(테이블에 영향을 주지 않음)

# 연관관계의 주인을 정하는 기준
- 비즈니스 로직을 기준으로 연관관계의 주인을 선택하면 안됨
- 연관관계의 주인은 외래키의 위치를 기준으로 정해야함
---

# 테이블 구조
- 테이블 구조는 이전과 같다.

# 객체 구조
- 참조를 사용하도록 변경

---

# 연관관계 매핑시 고려사항 3가지
- 다중성
- 단방향, 양방향
- 연관관계의 주인

# 다중성
- 다대일: @ManyToOne
- 일대다: @OneToMany
- 일대일: @OneToOne
- 다대다: @ManyToMany

# 단방향, 양방향
- 테이블
1. 외래키 하나로 양쪽 조인 가능
2. 사실 방향이라는 개념이 없음

- 객체
1. 참조용 필드가 있는 쪽으로만 참조가능
2. 한쪽만 참조하면 단방향
3. 양쪽이 서로 참조하면 양방향

# 연관관계 주인
- 테이블은 외래 키 하나로 두 테이블이 연관관계를 맺음
- 객체 양방향 관계는 A -> B, B -> A 처럼 참조가 2군데
- 객체 양방향 관계는 참조가 2군데 있음, 둘중 테이블의 외래 키를 관리할 곳을 지정해야함
- 연관관계의 주인: 외래 키를 관리하는 참조
- 주인의 반대편: 외래 키에 영향을 주지 않음, 단순 조회만 함

# 다대일[N:1]
- 가장 많이 사용하는 연관관계
- 다대일 반대는 일대다

# 다대일 양방향
- 외래키가 있는 쪽이 연관관계의 주인
- 양쪽을 서로 참조하도록 개발

---

# 일대다[N:1]

# 일대다 단방향 권장X
## 객체입장
```
Team
id
name
List members


Member
id
username

```

## 테이블 입장
```

TEAM 테이블
TEAM_ID(PK)
NAME

MEMBER 테이블
MEMBER_ID(PK)
TEAM_ID(FK)
USERNAME
```

- 일대다 단방향은 일대다(1:N)에서 일(1)이 연관관계의 주인
- 테이블 일대다 관계는 항상 다(N)쪽에 외래 키가 있음
- 객체와 테이블의 차이때문에 반대편 테이블의 외래키를 관리하는 특이한 구조
- @JoinColumn을 꼭 사용해야함. 그렇지 않으면 조인 테이블 방식을 사용함(중간에 테이블을 하나 추가함)

## 일대다 단방향 단점
- 엔티티가 관리하는 외래 키가 다른테이블에 있음(어마어마한 단점)
- 연관관계 관리를 위해서 추가로 UPDATE SQL 실행
- 일대다 단방향 매핑 보다는 다대일 양방향 매핑을 사용하자

# 일대다 양방향
- 이런 매핑은 공식적으로 존재 X
- @JoinColumn(insertable = false, updatable = false)
- 읽기 전용 필드를 사용해서 양방향 처럼 사용하는 방법
- 다대일 양방향을 사용하자

---

# 일대일
- 일대일 관계는 그 반대도 일대일
- 주 테이블이나 대상 테이블중에 외래키 선택 가능
1. 주 테이블에 외래 키
2. 대상 테이블에 외래 키

- 외래키에 데이터베이스 유니크(UNI) 제약조건 추가

## 일대일: 주테이블에 외래 키 단방향
- 객체 연관 관계
```
Member
id
Locker locker
user name

Locker
id
name
```

- 테이블 연관 관계
```
Member 테이블
MEMBER_ID(PK)
LOCKER_ID(FK, UNI)
USERNMAE

Locker 테이블
LOCKER_ID(PK)
NAME
```
- 다대일 단방향(@ManyToOne)단방향 매핑과 유사

## 일대일: 주테이블에 외래키 양방향
- 객체 연관 관계
```
Member
id
Locker locker
user name

Locker
id
name
Member member
```

- 테이블 연관 관계
```
Member 테이블
MEMBER_ID(PK)
LOCKER_ID(FK, UNI)
USERNMAE

Locker 테이블
LOCKER_ID(PK)
NAME
```
- 다대일 양방향 매핑 처럼 외래키가 있는곳이 연관관계의 주인
- 반대편은 mappedBy 적용

# 일대일: 대상테이블에 외래키 단방향 정리
- 단방향 관계는 JPA 지원 X
- 양방향 관계는 지원

# 일대일: 대상 테이블에 외래 키 양방향
- 사실 일대일 주테이블에 외래 키 양방향 매핑 방법은 같음

# 일대일 정리
## 주 테이블에 외래키
- 주 객체가 대상 객체의 참조를 가지는것처럼 주 테이블에 외래 키를 두고 대상 테이블을 찾음
- 객체지향 개발자 선호
- JPA 매핑 관리
- 장점: 주 테이블만 조회해도 대상 테이블에 데이터가 있는지 확인 가능
- 단점: 값이 없으면 외래 키에 null 허용
## 대상 테이블에 외래 키
- 대상 테이블에 외래 키 존재
- 전통적인 데이터베이스 개발자 선호
- 장점: 주 테이블과 대상 테이블을 일대일에서 일대다 관계로 변경 할 때 테이블 구조 유지
- 단점: 프록시 기능의 한계로 지연 로딩으로 설정해도 항상 즉시 로딩됨(프록시는 뒤에서 설명)

---
# 다대다 [N:M]
- 관계형 데이터베이스는 정규화된 테이블 2개로 다대다 관계를  표현할수없음
- 연결 테이블을 추가해서 일대다, 다대일 관계로 풀어내야함
- 객체는 컬렉션을 사용해서 객체 2개로 다대다 가능

# 다대다 매핑의 한계
- 편리해보이지만 실무에서 사용 X
- 연결 테이블이 단순히 연결만하고 끝나지 않음
- 주문시간, 수량 같은 데이터가 들어올 수 있음

# 다대다 한계 극복
- 연결 테이블용 엔티티 추가(연결 테이블을 엔티티로 승격)
- @ManyToMany -> @OneToMany, @ManyToOne

---
# 실전
## 배송, 카테고리 추가 - 엔티티
1. 주문과 배송은 1:1(@OneToOne)
2. 상품과 카테고리는 N:M(@ManyToMany)

# N:M 관계는 1:N, N:1로
- 테이블의 N:M 관계는 중간 테이블을 이용해서 1:N, N:1
- 실전에서는 중간 테이블이 단순하지 않다.
- @ManyToMany는 제약: 필드 추가 X, 엔티티 테이블 불일치
- 실전에서는 @ManyToMany 사용 X

---
# 상속관계 매핑
- 관계형 데이터베이스는 상속 관계 X
- 슈퍼타입 서브타입 관계라는 모델링 기법이 객체 상속과 유사
- 상속관계 매핑: 객체의 상속과 구조와 DB의 슈퍼타입 서브타입 관계를 매핑
- 슈퍼타입 서브타입 논리 모델을 실제 물리 모델로 구현하는 방법
1. 각각 테이블로 변환 -> 조인전략
2. 통합 테이블로 변환 -> 단일 테이블 전략
3. 서브타입 테이블로 변환 -> 구현 클래스마다 테이블 전략

# 주요 어노테이션
- @Inheritance(strategy = InheritanceType.XXX)
1. JOINED: 조인 전략
2. SINGLE_TALBE: 단일 테이블 전략
3. TABLE_PER_CLASS: 구현 클래스마다 테이블 전략
- @DiscriminatorColumn(name = "DTYPE")
- @DiscriminatorValue("XXX")

# 조인전략
- 장점
1. 테이블 정규화
2. 외래 키 참조 무결성 제약조건 활용 가능
3. 저장공간 효율화

- 단점
1. 조회시 조인을 많이 사용, 성능 저하
2. 조회 쿼리가 복잡함
3. 데이터 저장시 INSERT SQL 2번호출

# 단일테이블 전략
- 장점
1. 조인이 필요 없으므로 일반적으로 조회 성능이 빠름
2. 조회 쿼리가 단순함

- 단점
1. 자식 엔티티가 매핑한 컬럼은 모두 null 허용
2. 단일 테이블에 모든것을 저장하므로 테이블이 커질수 있고, 상황에 따라서 조회성능이 오히려 느려질 수 있다.

# 구현클래스마다 테이블 전략(쓰면 안되는 전략)
## 이 전략은 데이터베이스 설계자와 ORM 전문가 둘다 추천 X 
- 장점
1. 서브타입을 명확하게 구분해서 처리할 때 효과적
2. not null 제약 조건 가능

- 단점
1. 여러 자식 테이블을 함께 조회할 때 성능이 느림(UNION SQL)
2. 자식 테이블을 통합해서 쿼리하기 어려움

---

# @MappedSuperclass
- 공통 매핑 정보가 필요할 때 사용(id, name)
- 상속 관계 매핑 X
- 엔티티 X, 테이블과 매핑 X
- 부모 클래스를 상속 받는 자식 클래스에 매핑 정보만 제공
- 조회, 검색불가(em.find(BaseEntity) 불가)
- 직접 생성해서 사용 할 일이 없으므로 추상 클래스 권장
- 테이블과 관계 없고, 단순히 엔티티가 공통으로 사용하는 매핑 정보를 모으는 역할
- 주로 등록일, 수정일, 등록자, 수정자 같은 전체 엔티티에서 공통으로 적용하는 정보를 모을 때 사용
- 참고: @Entity 클래스는 엔티티나 @MappedSuperclass로 지정한 클래스만 상속 가능

---

# 요구사항 추가
- 상품의 종류는 음반, 도서, 영화가 있고 이후 더 확장될수도 있다.
- 모든 데이터는 등록일과 수정일이 필수이다. 

---

# Member를 조회할때 Team도 함께 조회해야 할까?
```
public static void main(String[] args) {

//        SpringApplication.run(Ex1HelloJpaApplication.class, args);
        EntityManagerFactory emf =  Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx =  em.getTransaction();
        tx.begin();

        try {
            Member member = em.find(Member.class, 1L);
//            printMemberAndTeam(member);
            printMember(member);
            tx.commit();

        } catch(Exception e) {
            tx.rollback();
        } finally {
            em.close();

        }
        emf.close();
    }

    private static void printMember(Member member) {
        String username = member.getUsername();
        System.out.println("username :" + username);
    }

    private static void printMemberAndTeam(Member member) {
        String username = member.getUsername();
        System.out.println("username :" + username);
        Team team = member.getTeam();
        System.out.println("username :" + team.getName());
    }
```
- member데이터만 가져와서 써도 되는데 team까지 같이 가져오면 비효율

# 해결방법(프록시 기초)
- em.find() vs em.getReference()
- em.find(): 데이터베이스를 통해서 실제 엔티티 객체 조회
- em.getReference(): 데이터베이스 조회를 미루는 가짜(프록시) 엔티티 객체 조회

# 프록시 특징
- 실제 클래스를 상속받아서 만들어짐
- 실제 클래스와 겉 모양이 같다.
- 사용하는 입장에서 진짜 객체인지 프록시 객체인지 구분않고 사용하면됨(이론상)
- 프록시 객체는 실제 객체의 참조(target)를 보관
- 프록시 객체를 호출하면 프록시 객체는 실제 객체의 메소드 호출

```
// 프록시 객체
Member findMember = em.getReference(Member.class, member.getId());

// username을 실제로 가져다쓰는 시점에 내부적으로 영속성 컨텍스트에 요청
// 레퍼런스가 실제 값을 가지면서 멤버가 값을 알게된다.
System.out.println("findMember.getUsername() :" + findMember.getUsername());
```

- 프록시 객체는 처음 사용 할 때 한번만 초기화
- 프록시 객체를 초기화 할 때, 프록시 객체가 실제 엔티티로 바뀌는것은 아님, 초기화되면 프록시 객체를 통해서 실제 엔티티에 접근가능
- 프록시 객체는 원본 엔티티를 상속받음, 따라서 타입 체크시 주의해야함 (== 비교 실패, 대신 instance of 사용)
- 영속성 컨텍스트에 찾는 엔티티가 이미 있으면 em.getReference()를 호출해도 실제 엔티티 반환
- 영속성 컨텍스트의 도움을 받을 수 없는 준영속 상태일 때, 프록시를 초기화하면 문제 발생
  (하이버네이트는 org.hibernate.LazyInitializationException예외를 터뜨림)

# 프록시 확인
- 프록시 인스턴스의 초기화 여부 확인
1. PersistenceUnitUtil.isLoaded(Object entity)
- 프록시 클래스 확인 방법
1. entity.getClass().getName() 출력
- 프록시 강제 초기화
1. org.hibernate.Hibernate.initialaize(entity);

---
# 프록시와 즉시 로딩 주의
- 가급적 지연 로딩만 사용(특히 실무에서)
- 즉시 로딩을 적용하면 예상하지 못한 SQL발생
- 즉시 로딩은 JPQL에서 N+1 문제를 일으킨다.
- @ManyToOne, @OneToMany은 기본이 즉시로딩 -> LAZY로 설정
- @OneToMany, @ManyToMany는 기본이 지연 로딩

# 지연로딩 활용
- Member와 Team은 자주 함께 사용 -> 즉시 로딩
- Member와 Order는 가끔 사용 -> 지연로딩
- Order와 Product는 자주 함께 사용 -> 즉시 로딩

# 지연 로딩 활용(실무)
- 모든 연관관계에 지연 로딩을 사용해라!
- 실무에서 즉시로딩을 사용하지마라!
- JPQL fetch조인이나, 엔티티 그래프 기능을 사용해라!
- 즉시로딩은 상상하지 못한 쿼리가 나간다.
