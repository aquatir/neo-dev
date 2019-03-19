package ru.neoflex.dev.spring.entity_graphs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.neoflex.dev.spring.entity_graphs.entity.City;

import java.util.List;
import java.util.Set;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    /*
    SELECT n, v
FROM ConfigName n
LEFT JOIN ConfigValue v
ON v.config = n AND v.currencyCode = :currencyCode
ORDER BY n.id
     */
    https://stackoverflow.com/questions/45775868/problems-mapping-hibernate-entities-native-query-containing-left-join-with-con
    c, d, e" +
            " FROM City c" +
            "LEFT JOIN Department d" +
            "on c.id = d.
    @Query("SELECT c, d, e)
    Set<City> findAllBy();
}
