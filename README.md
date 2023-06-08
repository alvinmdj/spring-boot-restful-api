# Spring Boot RESTful API

Spring Boot RESTful API for Contact Managements.

API Specification can be found at `docs` directory.

Dependencies:

- Spring Web
- Spring Data JPA
- Validation
- Lombok
- MySQL Driver
- Spring Test (default)

## Requirements

- Java 17
- Maven
- MySQL

## Useful Links

- [Spring Initializr Config](https://start.spring.io/#!type=maven-project&language=java&platformVersion=3.1.0&packaging=jar&jvmVersion=17&groupId=com.alvinmdj&artifactId=spring-boot-restful-api&name=spring-boot-restful-api&description=Spring%20Boot%20RESTful%20API%20-%20Spring%20Web%20%7C%20Spring%20Data%20JPA%20%7C%20Validation%20%7C%20Lombok%20%7C%20MySQL%20Driver&packageName=com.alvinmdj.spring-boot-restful-api&dependencies=web,data-jpa,mysql,lombok,validation)
- [Spring Framework Guru - Hikari Configuration for MySQL in Spring Boot](https://springframework.guru/hikari-configuration-for-mysql-in-spring-boot-2/)
- [Spring Security - Class BCrypt](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCrypt.html)
- [Spring Security - GitHub - Bcrypt.java](https://github.com/spring-projects/spring-security/blob/main/crypto/src/main/java/org/springframework/security/crypto/bcrypt/BCrypt.java)
- [Spring Guide - Testing the Web Layer](https://spring.io/guides/gs/testing-web/)
- [Baeldung - Integration Testing in Spring](https://www.baeldung.com/integration-testing-in-spring)
- [PZN Google Slides - Spring Web MVC - Argument Resolver](https://docs.google.com/presentation/d/14QkKKAC5Pbf9vJU8goV9BmctgK2WtcF9E_lWe2NWr3g/edit#slide=id.g238e37a1bdf_0_852)

## Manage Local MySQL (XAMPP)

- Run XAMPP
- Start MySQL
- Click Shell button in XAMPP Control Panel
- Inside the Shell, type: `mysql -u root`
- Show databases: `SHOW DATABASES;`

Or the easy way: `http://localhost/phpmyadmin`

Create database: `CREATE DATABASE springboot_restful_api;`