package de.flix29.sproutdemo.entities;

import de.flix29.sprout.annotations.SproutId;
import de.flix29.sprout.annotations.SproutPolicy;
import de.flix29.sprout.annotations.SproutResource;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@SproutResource(path = "/products")
@SproutPolicy(
        read = "hasRole('ROLE_USER')",
        create = "hasRole('ROLE_ADMIN')",
        update = "hasRole('ROLE_ADMIN')",
        delete = "hasRole('ROLE_ADMIN')"
)
@Entity(name = "Product")
public class ProductEntity {

    @SproutId
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal price;

    protected ProductEntity() {
        // JPA
    }

    public ProductEntity(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}