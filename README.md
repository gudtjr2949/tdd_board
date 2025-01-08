# 게시판 - TDD

</br>
</br>

## 개요
TDD 기반 게시판 구현 프로젝트입니다.


</br>
</br>

## ERD

<img width="774" alt="스크린샷 2025-01-08 오후 11 29 13" src="https://github.com/user-attachments/assets/6f81605f-7d39-4d75-8229-a1f670921915" />

</br>
</br>

## 주요 기능

</br>

### 1. 회원가입, 로그인

회원 정보를 입력한 후 가입을 시도합니다.

회원가입에 입력했던 정보를 토대로 로그인을 시도합니다.

</br>

### 2. 게시판・댓글 CRUD

게시판・댓글 작성, 조회, 수정, 삭제를 수행합니다.

<br>
<br>

## 사용 기술

<br>

### 1. 테스트 코드 작성을 통한 코드 신뢰성 확보

... 여기에 SnarQube 이미지 넣기


<br>
<br>

### 2. Spring Batch를 사용한 대량 데이터 저장

Spring Batch를 사용해 100,000개 데이터 저장 소요시간을 약 **96%** 단축시켰습니다.

<br>

**Batch 적용 전 데이터 삽입 소요시간**
![before_batch](https://github.com/user-attachments/assets/97019bca-db28-4a3f-987f-5b9aa2b46a37)

<br>

**Batch 적용 후 데이터 삽입 소요시간**
![after_batch](https://github.com/user-attachments/assets/17732e66-94ce-4cb4-a2b5-83d387baee44)

<br>
<br>

### 3. No Offset을 사용한 데이터 조회 성능 향상

Spring JPA에서 No Offset을 적용해 게시글 조회(페이징) 성능을 향상시켰습니다. 

<br>

**No Offset 적용 전 소요시간**
![before_nooffset](https://github.com/user-attachments/assets/718b78f2-bc41-461e-b0db-db98ebe4ef6b)

<br>

**No Offset 적용 후 소요시간**
![after_nooffset](https://github.com/user-attachments/assets/99641747-f633-48a1-ba25-ed27173eda06)

<br>
<br>

## 예시 코드


<br>

### No Offset 구현 코드

```java
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    ...

    List<Board> findByIdLessThanOrderByIdDesc(Long boardId, Pageable pageable);

    ...
}
```
- findByIdLessThanOrderByIdDesc() 메서드를 통해 가장 최근에 조회한 게시글 ID보다 작은 게시글을 한 페이지당 보여줄 게시글만큼 조회할 수 있도록 한다.

- 이를 통해 Offset으로 인해 불필요한(조회는 하지만 사용하지 않은 게시글) 데이터를 조회하는 상황을 해결할 수 있다.
