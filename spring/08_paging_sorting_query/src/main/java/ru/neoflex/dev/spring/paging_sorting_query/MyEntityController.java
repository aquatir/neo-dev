package ru.neoflex.dev.spring.paging_sorting_query;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
public class MyEntityController {

    private final MyEntityRepository myEntityRepository;

    public MyEntityController(MyEntityRepository myEntityRepository) {
        this.myEntityRepository = myEntityRepository;
    }

    @GetMapping("/myEntity")
    @Transactional
    public List<MyEntityDto> getMyEntities() {
        var entities =  this.myEntityRepository.findAll();
        var dtos = StreamSupport.stream(entities.spliterator(), false).map(MyEntityDto::ofMyEntity)
                .collect(Collectors.toList());
        return dtos;
    }
}
