
package com.rest.API.model;

        import javax.persistence.*;
        import javax.validation.constraints.NotBlank;
        import java.math.BigDecimal;
        import java.math.BigInteger;

@Entity
@Table(name = "animal")
public class Animal {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

//    @NotBlank
@Column(name = "name", length = 50)
    private String name;

//    @NotBlank
@Column(name = "type", length = 50)
    private String type;

//    @NotBlank
@Column(name = "price", length = 16, precision = 16, scale = 2)
    private BigDecimal price;

    public Animal(){
        super();
    }

    public Animal(Long id, String name, String type, BigDecimal price) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.price=price;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}