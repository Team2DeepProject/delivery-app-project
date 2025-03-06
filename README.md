

# 먹슐랭
> 배달 스타트업 클라이언트의 요청대로 배달 어플리케이션 개발 아웃소싱 프로젝트를 진행하였습니다. 
> <br> Live demo [_here_]().

<br><br>

## Table of Contents
* [📌 General Information](#general-information)
* [🛠️ Technologies Used](#technologies-used)
* [📂 API Documentation](#api-documentation)
* [🏗️ Wireframe](#Wireframe)
* [📊 ERD](#erd)
* [📬 Contact](#contact)
<!-- * [License](#license) -->

<br><br>

## 📌 General Information

**1. 회원관리 (users)**

- 회원 정보를 CRUD로 관리합니다.
  
  - 회원 가입
    
    - 사용자 아이디는 이메일 형식입니다.
    
    - 비밀번호는 최소 8글자 이상이며, 대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함합니다.(`Bcrypt` 를 사용해 DB에는 다른 형식으로 비밀번호가 저장됩니다)
    
    - 중복된 아이디, 비밀번호와 비밀번호 확인이 맞지 않으면 가입이 불가능합니다.
    - 가입 시 권한(USER, OWNER, ADMIN, RIDER) 을 입력하면, 그에 맞게 기능을 사용할 수 있습니다.
  
  - 회원 정보 조회
  
    - 전체 회원 목록 조회: 가입한 회원의 모든 정보를 페이지로 나눠 조회할 수 있습니다.
    
    - 프로필 조회: 현재 로그인한 회원의 정보를 조회할 수 있습니다.
    
  - 회원 탈퇴
    
    - 탈퇴 시 비밀번호를 입력 후 일치할 때 탈퇴 처리가 됩니다.
    
    - 탈퇴한 사용자의 아이디는 재사용할 수 없고, 복구할 수 없습니다.(SOFT DELETE)

<br>

**2. 인증 및 인가(authentication)**

- jwt를 통한 로그인 기능을 구현하였습니다.

- refresh token을 cookie에 담아 토큰 재발급이 가능합니다.

- refresh token 만료로 로그아웃이 가능합니다.

- filter를 사용한 로그인 필터, 권한에 따른 interceptor를 사용하였습니다. 

<br>

**3. 가게(store)**

- 가게주인이 가게 C, U, D를 할 수 있습니다.

- 즐겨찾기 개수를 포함한 가게 다건 조회가 가능합니다.

- 가게 단건 조회시 메뉴 조회를 할 수 있습니다.

<br>

**4. 가게 카테고리(category)**

- 관리자는 카테고리 생성/삭제가 가능합니다.

- 가게 주인은 카테고리 등록/해제가 가능합니다.
    
- 카테고리별 가게 다건 조회가 가능합니다. 

<br>

**5. 메뉴 (menus)**

- 메뉴 정보를 CRUD로 관리합니다.

  - 메뉴 생성

    - 사장님(OWNER)만 생성이 가능합니다.
    
    - 같은 가게에선 동일한 이름의 메뉴 생성이 불가능합니다.


  - 메뉴 조회
    
    - 전체 메뉴 목록 조회: 생성한 모든 메뉴를 페이지로 나눠 조회할 수 있습니다 (삭제된 메뉴도 조회 가능)
    
    - 단건 메뉴 조회: 지정한 메뉴의 정보를 조회할 수 있습니다.
    
    - 가게별 메뉴 조회: 지정한 가게의 모든 메뉴를 페이지로 나눠 조회할 수 있습니다.(판매하는 메뉴만 조회 가능)
  
  - 메뉴 수정
    
    - 사장님(OWNER)만 수정이 가능합니다.
  
  - 메뉴 삭제
    
    - 사장님(OWNER) 본인 가게의 메뉴만 삭제할 수 있습니다.

    - 삭제 시, 메뉴의 상태만 삭제 상태로 변경됩니다.(SOFT DELETE)
            


<br>

**6. 즐겨찾기 (Bookmarks)**

- 토글 방식으로 즐겨찾기 추가 및 삭제가 가능합니다. 
        
- 페이징을 지원하여 즐겨찾기 목록 조회가 가능합니다.
        
- 즐겨찾기는 로그인한 유저만 접근이 가능합니다.

- 이 때, `@Auth AuthUser authUser` 을 통해 인증된 유저의 정보를 활용합니다.

<br>

**7. 공지 (Notices)**

- 점주(OWNER)만 가게 공지사항 생성, 수정, 삭제를 할 수 있습니다. 

- 모든 사용자가 공지사항을 조회할 수 있습니다.

- 페이징을 통한 공지사항 목록을 조회할 수 있습니다.
        
  - 점주(OWNER)만 공지사항을 관리할 수 있습니다. 

<br>

**8. 리뷰 (Reviews)**

- 리뷰 생성
  
  - 리뷰 작성
  
    - 사용자는 각 가게에 대한 리뷰를 작성할 수 있습니다. 
    
    - 리뷰는 가게에 대한 평가와 내용을 포함합니다.
    
    - 사용자는 한 가게에 한 개의 리뷰만 작성할 수 있습니다.
  
  - 중복 리뷰 방지
    
    - 사용자가 이미 해당 가게에 리뷰를 작성한 경우, 다시 작성할 수 없습니다.

- 리뷰 조회

  - 리뷰 목록 조회
  
    - 특정 가게에 대한 모든 리뷰를 조회할 수 있으며, 이를 통해 다른 사용자의 피드백을 확인할 수 있습니다.

- 리뷰 수정

  - 사용자는 자신이 작성한 리뷰의 내용을 수정할 수 있습니다. 
  
  - 다른 사용자의 리뷰는 수정할 수 없습니다.

- 리뷰 삭제

  - 사용자는 자신이 작성한 리뷰를 삭제할 수 있으며, 삭제된 리뷰는 데이터베이스에서 완전히 제거됩니다.

<br>

**9. 사장님 댓글 (Comments)**
- 댓글 작성

  - 리뷰에 대해 가게의 사장님만 댓글을 작성할 수 있습니다. 
  
  - 각 리뷰에 대해 사장님은 한 개의 댓글만 작성할 수 있습니다.

- 댓글 조회
  
  - 사장님이 작성한 댓글을 확인할 수 있습니다.

- 댓글 수정 및 삭제

  - 사장님은 자신이 작성한 댓글을 수정하거나 삭제할 수 있습니다.

<br>

**10. 주문관리 (order)**
 
 - 장바구니의 메뉴로 주문 할 수 있습니다.
    
    - 가게 주인이 주문 승인하면 포인트 미 사용 시 주문 금액의 3% 포인트로 적립합니다.
    
    - 가게 주인이 주문 거절할 수 있습니다.

<br>

**11. 장바구니 (cart)**

- 여러개의 메뉴를 담을 수 있습니다.

- 다른 가게의 메뉴를 담을 시 기존 장바구니 초기화 하고 새로 담습니다.

- 장바구니를 비울 수 있습니다.

- 같은 가게의 같은 메뉴를 담을 시 수량이 증가합니다.

- 메뉴의 수량을 감소할 수 있습니다.

<br>

**12. 배송 (delivery)**

- 승인 된 주문을 배송할 수 있습니다.
    


<br><br>

## 🛠️ Technologies Used
<div align=center> 
  <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">  
  <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
  <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
  <img src="https://img.shields.io/badge/redis-F80000?style=for-the-badge&logo=redis&logoColor=white"> 
  <br>
  <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
  <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
</div>

<br><br>


<br><br>

## 📂 API Documentation
[원본 링크](https://spiffy-pluto-ce9.notion.site/API-Document-1ae4e613996780999e73df3bfbde3ae5)

<br>

### 인증(Authentication) /auth
![image](https://github.com/user-attachments/assets/053203c8-7fc9-4958-a30b-74d593570644)

<br>


### 유저(Users) /user
![image](https://github.com/user-attachments/assets/727b2db4-10e6-4ced-bf44-f4de7bbcf072)

![image](https://github.com/user-attachments/assets/e2c6bf7f-bf5e-45b1-b964-864891af59fc)

<br>

### 즐겨찾(Bookmarks) /bookmarks



![image](https://github.com/user-attachments/assets/359f4d7b-c01a-4176-a1c0-9b7f6da89cc4)

<br>


### 리뷰(Reviews) /reviews


![image](https://github.com/user-attachments/assets/a4f6eaf1-713f-4698-a9cb-98f24d0b33c8)


<br>

### 사장님 댓글(OwnerComment) /comments


![image](https://github.com/user-attachments/assets/d05335b7-1031-4e25-8758-4813529d6235)

<br>

### 가게(Store) /stores


![image](https://github.com/user-attachments/assets/195e4e01-b29c-48e4-afa7-cbae7fca668b)

![image](https://github.com/user-attachments/assets/cf3dc209-d122-4748-a830-7fe92bb480ce)


![image](https://github.com/user-attachments/assets/385fec20-f976-42a2-97b0-79715e2c22d3)

<br>

### 카테고리(Category) /categorys
![image](https://github.com/user-attachments/assets/31b54e50-f3b5-4467-a9dc-7943d5a007c8)

<br>

### 주문(Order) /orders


![image](https://github.com/user-attachments/assets/8a1db301-c924-40e6-9799-ffa4618be79b)

![image](https://github.com/user-attachments/assets/7adc6398-6dd3-41ab-a188-1c7ba16310fd)

<br>

### 장바구니(Cart) /carts


![image](https://github.com/user-attachments/assets/48dc34aa-596f-4f28-9998-b876878ae863)

<br>

### 배송(Delivery) /deliveries


![image](https://github.com/user-attachments/assets/d3ecf123-081e-47dc-bf0a-8d32a40a32a7)

### 가게 공지(Notices) /notices

![image](https://github.com/user-attachments/assets/891478e7-a18c-4c10-ac69-bf7a913f8d6c)


### 메뉴(Menu) /menus

![image](https://github.com/user-attachments/assets/392c7747-f6b2-4ac3-bfa5-01421bcd2476)

![image](https://github.com/user-attachments/assets/f570f904-5447-4ae3-aa23-7b65cc3aace2)

![image](https://github.com/user-attachments/assets/b0744afe-ece9-45c1-bf6d-45774b625323)

<br><br>

## 🏗️ Wireframe
![image (10)](https://github.com/user-attachments/assets/1b71c5a9-360d-4caf-a320-224c9d3aff71)

<br><br>

## 📊 ERD
[원본 링크](https://www.erdcloud.com/d/sYg7EMsH2wZrhoBX7)

<br>

<img width="1019" alt="image (11)" src="https://github.com/user-attachments/assets/ff9d504f-4ac6-4bff-8982-2439390e0b2a" />

<br><br>

## 📬 Contact
Created by 
[@bopeep934](https://github.com/bopeep934)
[@JoeMinKyung](https://github.com/JoeMinKyung)
[@juno-soodal](https://github.com/juno-soodal)
[@queenriwon](https://github.com/queenriwon)
[@Roloya28](https://github.com/Roloya28)
<br>

문의사항이 있다면 언제든지 연락주세요!
