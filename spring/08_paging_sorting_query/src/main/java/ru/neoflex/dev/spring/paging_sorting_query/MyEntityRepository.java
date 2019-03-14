package ru.neoflex.dev.spring.paging_sorting_query;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyEntityRepository extends CrudRepository<MyEntity, Long> {
}
