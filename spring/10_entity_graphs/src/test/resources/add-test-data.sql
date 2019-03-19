CREATE TABLE DEPARTMENT (
  ID identity,
  NAME VARCHAR(50) not null
);

CREATE TABLE EMPLOYEE (
   ID IDENTITY,
   DEPARTMENT_ID INT,
   foreign key (DEPARTMENT_ID) references DEPARTMENT(ID),
   name VARCHAR(50) NOT NULL
);

INSERT INTO EMPLOYEE(ID, NAME) values
  (1, 'John'),
  (2, 'Mark'),
  (3, 'Samanta');

INSERT INTO DEPARTMENT(ID, NAME) values
  (1, 'dep-dep');

INSERT INTO EMPLOYEE(ID, NAME, DEPARTMENT_ID) values
  (4, 'Susan', 1),
  (5, 'Vlad', 1);
