package ru.neoflex.dev.spring.paging_sorting_query;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MY_ENTITY")
public class MyEntity {

    @Id
    @GeneratedValue()
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;


    public MyEntity() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MyEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
