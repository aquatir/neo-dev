package ru.neoflex.dev.spring.paging_sorting_query;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyEntityController {

    private final MyEntityRepository myEntityRepository;

    public MyEntityController(MyEntityRepository myEntityRepository) {
        this.myEntityRepository = myEntityRepository;
    }

    @GetMapping("/myEntity")
    public PageWithTotalResponse<MyEntity> getMyEntities(Pageable pageable) {
        var page =  this.myEntityRepository.findAll(pageable);
        return new PageWithTotalResponse<>(page.getContent(), page.getTotalElements());
    }
}
