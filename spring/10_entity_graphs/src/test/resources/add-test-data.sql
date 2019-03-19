INSERT INTO CITY(ID, NAME) values
  (1, 'Moscow');

INSERT INTO DEPARTMENT(ID, NAME, CITY_ID) values
  (1, 'DEP1', 1),
  (2, 'DEP2', 1);

INSERT INTO EMPLOYEE(ID, NAME, AGE, DEPARTMENT_ID) values
  (1, 'John', 22, 1),
  (2, 'Mark', 25, 1),
  (3, 'Samantha', 30, 2),
  (4, 'Kate', 23, 2);
