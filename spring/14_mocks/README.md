# 14_mocks

### Цель

Научиться работать с mock объектами при тестировании Spring Boot приложений

### Теория

Часто хочется протестировать лишь какой-то маленький кусок функционала, а вместо внеших систем подставить Mock объекты. 
Для этих целей в Spring Boot поумолчанию используется [Mockito[1]](https://site.mockito.org/). 
Помимо основным возможностей библиотеки, Spring Boot предоставляет удобные способы быстро конфигурировать необходимые 
мок объекты для бинов из контекста.

#### Mockito.mock(), @MockBean и @Mock

Пусть мы хотим протестировать контроллер ```EmployeeController``` в тесте ```EmployeeControllerTest```. Этот контроллер 
использует внутри себя сервис ```EmployeeService```. Мы хотим замокать 1 его метод - ```findById(Long id)```.
Это можно сделать при помощи аннотации ```@MockBean``` следующим образом:

```
@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeControllerTest {

    @MockBean
    private EmployeeService employeeService;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.employeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }
```

Мы создаем мок для одного из методов. Теперь этот мок всегда возвращает нужный нам объект. Далем это мы при помощи аннотации
```@MockBean``` над ```EmployeeService```. Но тут может возникнуть вопрос. Мы же можем сделать еще и вот так (определить 
мок самостоятельно через ```Mockito.mock()```):

```
@SpringBootTest
@RunWith(SpringRunner.class)
@SqlGroup({
        @Sql(value = "/add-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(value = "/remove-test-data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeControllerTest {

    private EmployeeService mockEmployeeService;

    @Before
    public void setUp() {
        this.mockEmployeeService = Mockito.mock(EmployeeService.class);
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.mockEmployeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }    
```

И все будет работать! 

И даже вот так (Определить мок через аннотацию ```@Mock```):

```
public class EmployeeControllerTest {

    @Mock
    private EmployeeService mockEmployeeService;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.mockEmployeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }
```

И снова все будет работать! Так в чем же разница?

Второй и третий метод (```Mockito.mock()``` и аннотация ```@Mock```) - идентичны. Они создают мок объекта. Но они оба
отличаются от ```@MockBean``` тем, что не кладут мок объект в контекст IoC контейнера спринга. 
Это значит, что при таком моке, мы не сможет получить реальный бин. Например такой код не сработает:

```
    @Mock
    private EmployeeService employeeService;
    @Autowired
    private BeanFactory beanFactory;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var empService = (EmployeeService) beanFactory.getBean("employeeService");

        var emp = empService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }
```

А при использовании ```@MockBean```, так как реальный бин будет заменен моковым, его можно будет найти по имени, и 
все сработет так, как и ожидается:
```
    @MockBean
    private EmployeeService employeeService;
    @Autowired
    private BeanFactory beanFactory;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var empService = (EmployeeService) beanFactory.getBean("employeeService");

        var emp = empService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }
```

#### Mockito.spy(), @SpyBean и @Spy

Помимо мок режима, которые дает заданные ответы там, где они заданы и "ответы поумолчанию" там, где они не заданы, 
у mockito существует еще и Spy режим. Spy режим выдает мокнутые ответы, если они есть, но если их нет - Spy режим 
делает вызов реального метода.

Например вот такие 2 теста не сработают, т.к. второй тест вернет ответ поумолчанию (потому что мы его не замокали):
```
    @MockBean
    private EmployeeService employeeService;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.employeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }

    @Test
    public void test_findAll() {
        var emps = this.employeeService.findAll();
        assertEquals(4, emps.size());
    }
```

А вот при использовании ```@SpyBean``` все работает так, как и ожидается. Один из тестов вернет мокнутый ответ, а второй 
сделает реальный запрос к базе данных и вернет реальный ответ.

```
    @SpyBean
    private EmployeeService employeeService;

    @Before
    public void setUp() {
        when(employeeService.findById(anyLong()))
                .thenReturn(
                        Optional.of(Employee.builder().id(1L).name("TEST").age(1L).build()));
    }

    @Test
    public void test_findById() {
        var emp = this.employeeService.findById(123123L);
        assertEquals("TEST", emp.get().getName());
    }

    @Test
    public void test_findAll() {
        var emps = this.employeeService.findAll();
        assertEquals(4, emps.size());
    }
```

С аннотацией ```@Spy``` и ```Mockito.spy()``` такая же история, как и с ```@Mock``` и ```Mockito.mock()```. Такой способ
создания spy объектов не меняет объекты внутри Spring IoC контейнера. При этом еще следует помнить, что для работы аннотации 
```@Spy``` у объекта должен быть конструктор без аргументов, а значит инъекция через констуктор в принципе становится невозможной.

---

#### Что еще посмотреть.

Более подробно про работу с Mockito советую посмотреть в доке [Spring Boot[2]](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-mocking-beans).

Еще раз о различиях ```Mockiton.mock() / @MockBean / @Mock``` можно почитать [здесь[3]](https://www.baeldung.com/java-spring-mockito-mock-mockbean)

Совсем подробно про Mockito написано в цикле курсов [здесь[4]](https://www.baeldung.com/mockito-series)

### Почитать

1. Mockito https://site.mockito.org/
2. Кусок документации Spring Boot про моки https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html#boot-features-testing-spring-boot-applications-mocking-beans
3. Различия между ```Mockito.mock()```, ```@MockBean``` и ```@Mock``` https://www.baeldung.com/java-spring-mockito-mock-mockbean
4. Цикл курсов по Mockito https://www.baeldung.com/mockito-series

### Задание

Цель задания: немного разобраться в Mockito.

Необходимо, чтобы мокнутая имплементация ```EmployeeService``` возвращала разные объекты ```Employee``` в зависимости от 
переданного в метод ```findById(Long id)``` аргумента.
1. Для пользователя с ID = 1 должен возвращаться пользователь с именем ```TEST-ONE```
2. Для пользователя с ID = 2 должен возвращаться пользователь с именем ```TEST-TWO``` 
3. Для любого другого ID должен возвращаться пользователь с именем ```TEST-ANY```

Тесты прилагаются в ```EmployeeControllerTest```. 
