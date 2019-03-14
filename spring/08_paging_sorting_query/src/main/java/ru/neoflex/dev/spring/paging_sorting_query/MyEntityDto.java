package ru.neoflex.dev.spring.paging_sorting_query;

public class MyEntityDto {
    private Long id;
    private String name;

    public MyEntityDto() {
    }

    public MyEntityDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyEntityDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static MyEntityDto ofMyEntity(MyEntity myEntity) {
        return new MyEntityDto(myEntity.getId(), myEntity.getName());
    }
}
