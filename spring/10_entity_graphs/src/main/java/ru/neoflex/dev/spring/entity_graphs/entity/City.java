package ru.neoflex.dev.spring.entity_graphs.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "CITY")
public class City {

    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = true)
    private String name;

    @OneToMany(mappedBy = "city", fetch = FetchType.LAZY)
    private List<Department> departments;
}
