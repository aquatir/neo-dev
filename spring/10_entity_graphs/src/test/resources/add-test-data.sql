INSERT INTO CITY(ID, NAME) values
  (1, 'Moscow'),
  (2, 'London');

INSERT INTO DEPARTMENT(ID, NAME, CITY_ID) values
  (1, 'DEP1', 1),
  (2, 'DEP2', 1),
  (3, 'DEP3', 2),
  (4, 'DEP4', 2);

INSERT INTO EMPLOYEE(ID, NAME, AGE, DEPARTMENT_ID) values
  (1, 'John', 22, 1),
  (2, 'Mark', 25, 1),
  (3, 'Samantha', 30, 2),
  (4, 'Kate', 18, 2),
  (5, 'Ivan', 19, 3),
  (6, 'Pavel', 20, 3),
  (7, 'Alexandra', 22, 4),
  (8, 'Lika', 23, 4);
