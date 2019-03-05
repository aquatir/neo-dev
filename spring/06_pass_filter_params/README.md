# 06_pass_filter_params

### Цель

Разобраться с тем, каким образом можно передавать данные в Spring контроллеры

### Теория

Spring WEB предоставляет возможности для определения параметров тела запроса и параметров в url'е сразу в целевые
типы данных. Для преобразования между HTTP запросом и ```Java POJO``` Spring используется реализации интерфейса
```HttpMessageConverter```. 

При работе по http можно использовать разные форматы данных. Как правило самым популярным для работы с web фронтом являетяс 
JSON. Работу между частями приложения (например, микросервисами) происходит обычно либо также в JSON, либо на бинарных протоколах,
реже (в страшных банках) - XML. Spring позволяет автоматизироть работу по преобразованию данных к целевым Java объектам.

Поумолчанию при обработке запроса Spring Boot подразумевается взаимодействие по JSON, а для преобразования JSON в 
объекты используется библиотеку ```Jackson```. В таком случае используется конвертор ```MappingJackson2HttpMessageConverter```.

Для большинства нужных вам форматов (XML, Protobuf, Avro) стандартные конверторы либо уже написаны и они включен в starter'ы, 
либо они не включены в стартеры, но тем неменее уже написаны.

Для работы с HTTP в Spring необходимо пометить класс аннотацией ```@Controller```. Это мартер для Spring, который говорит, 
что в методах этого класса нужно начать искать методы, помечанные с ```@RequestMapping``` (поведение поумолчанию).

Аннотация ```@Controller``` также несет с собой аннотацию ```@Component``` (Это видно, если провалиться в исходники). Т.е.
контроллер - это самый обычный bean со всеми вытекающими. Например, его можно инжектить точно также, как и любой другой 
бин (но нужно ли?). 

--- 

Когда-то давным давно большинство бекендов в основном занимались тем, что генерили страницы, которые отображались на фронте. 
Сейчас же обычно мы имеет stateless бекенд и stateful фронтенд, который по GRAPHQL / REST / etc API запрашивает какие-то 
кусочки данных, а не целые страницы. 

С точки зрения Spring этими "кусочками данных" являются объекты, которые необходимо преобразовать в определенный формат и 
отдать вызывающей стороне в качестве http response. Чтобы пометить метод как метод, который возвращает свой результат в 
response запроса - необходимо пометить этот метод аннотацией ```@ResponseBody```. Но можно просто пометить контроллер аннотацией 
```@RestController```, которая уже внутри себя содержит ```@ResponseBody``` (Это называется "композитная аннотация")

Note: ```@RestController``` будет обрабатываться только если присуствутет и настроены пара ```HandlerMapping``` и ```HandlerAdapter```.
В Spring boot приложениях поумолчанию такая пара есть в виде ```RequestMappingHandlerMapping``` и ```RequestMappingHandlerAdapter```.
Это важно только если вы каким-то образом хачите ```@RequestMapping```. В общем случае все должно работать из коробки.

После этого поведение и обработка http в Spring задается при помощи кучи различных аннотаций или объектов - аргументов методов. 
Читать официальную доку можно начинать [отсюда[1]](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-requestmapping)


#### Аннотации для работы с запросами при использовании Jackson

По традиции, для изменения поведения Spring/Jackson приложения обычно требуется использовать тысячу различных аннотаций. 
Рассмотрим некоторые из них.

##### @RequestBody
```@RequestBody``` - преоразовывает входные параметры из JSON в целевой объект. Пример:
```
@Controller
public class MyController {

    public MyObject call(@RequestBody MyRequest request) {
        return null;
    }

    private class MyRequest {
        private Long id;
        private Optional<String> name;
        private LocalDate date;
    }

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    private class MyObject {

        private Long idId;
        private String nameName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime date;

        @JsonProperty("compelte_different_name")
        private String otherValue;
    }
}
```

При вызове этого метода body http запроса будет пропарсено и преобразовано к объекту ```MyRequest```. Поумолчанию вместо полей,
которых не будет в изначальном запросе будет проставлен null (или другое значение поумолчанию для поля).

Здесь также продемонстированы следующие фишки Jackson:
1. Поле с типом ```Optional<T>``` будет корректно преобразовано к нужному типу. Поведение такое как и ожидается от Optional
2. При передаче дат можно указать четкий формат, который будет мапится в нашу дату при помощи аннотации ```@JsonFormat```
3. Можно задавать разные имя для фактического параметра в JSON и для поля, на которое оно будет мапится при помощи
аннотации ```@JsonProperty()```
4. Можно задавать разные стратегии для именования JSON. Например в примере выше используется ```@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)```.
Эта аннотации преобразовывает все поля в snake-case, например ```int idId``` превратится в ```id_id```.


##### @PathVariable
```@PathVariable``` используется для получения 1 значения-примитива из URL'а запроса.
Пример:
```
    @GetMapping("/object/{id}/blabla/{something}")
    public String callz(@PathVariable Integer id, @PathVariable(name = "something") String id2) {
        System.out.println(id);
        System.out.println(id2);
        return "String";
    }
```


В данном примере при обработке url запроса значение, которые стоит на месте ```{id}``` будет замаплено в поле ```Integer id```,
а значение после ```blabla/``` будет замаплено в ```String id2```.

##### @RequestParam и отсутствие аннотации

Можно также мапить параметры запроса.

Примитины мапятся напрямую через аннотацию ```@RequestParam```, как в примере:
```
curl localhost:8080/object?value=5

    @GetMapping("/object")
    public Integer call(@RequestParam Integer value) {
        System.out.println(value);
        return value;
    }
```

Иногда хочется замапить целый объект. Тут, внзеапно, мапинг сработает, если опустить все аннотации.
```
curl localhost:8080/test?firstValue=5&second=aaa&third=bbb

    @GetMapping("/test")
    public String call(ComplexObx complexObx) {
        return complexObx.firstValue + complexObx.second + complexObx.third;
    }

    public class ComplexObx {
        Integer firstValue;
        String second;
        String third;
        
        public ComplexObx() {};
        public void setFirstValue(Integer firstValue) {this.firstValue = firstValue;}
        public void setSecond(String second) {this.second = second;}
        public void setThird(String third) {this.third = third;}
    }
```

NOTE: При передачи параметров такм образом необходимо чтобы объект удовлетворял одному из условий:
- У объекта есть конструктор, включающий все передаваемые поля
- У объекта есть пустой конструктор поумолчанию, а у полей есть сеттеры.


##### Прочее

Это не исчерпывающий список доступных аннотаций и объектов, к которым можно получить доступ из запроса. 
Для более исчерпывающего можно обратить в [официальную доку[2]](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-methods)
Или в кратце [здесь[3]]

Из интересного можно мапить например:
1. Хедеры при помощи ```@RequestHeader```
2. Аттрибуты редиректа при указании объекта в методе котроллера с типом ```RedirectAttributes```
3. Аттрибуты сессии (если используются сессии) при помощи ```@SessionAttribute```
4. Cookie при помощи ```@CookieValue```
5. [Matrix-variable[4]](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-matrix-variables)
6. И еще целую кучу всего

#### Валидация запросов

По мимо автоматического мапинга полей запроса в объекты Java, Spring предоставляет возможности автоматической валидации запросов.
Валидация - параметров запросов в частности, как и валидация бинов в общем - это довольно большая тема, которой возможно в 
будущем будет посвящена отдельная часть. Пока же при необходимости добавить валидацию можно почитать [здесь[5]](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation)


### Почитать

1. Официальная дока https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-requestmapping
2. Аннотации для обработки HTTP в Spring https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-methods
3. Краткая выжимка по функционалу ```@RequestMapping``` https://www.baeldung.com/spring-requestmapping
4. Про Matrix Variable https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-matrix-variables
5. https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#validation

### Задание

1. В контроллере MyController есть 2 метода. Метод ```/testcall1``` принимает объект MyObject из сторонней системы с очень странным форматом
 (формат можно посмотреть в тесте MyControllerTest). Необходимо, чтобы объек формат другой системы можно было мапить в 
 объекты MyObject.
2. Метод ```/testcall2/{path}``` должен вычитывать первый аргумент из параметров урла, а параметр фильтра должен передаваться,
 как параметр GET запроса.
 
 Тесты должны проходить.



