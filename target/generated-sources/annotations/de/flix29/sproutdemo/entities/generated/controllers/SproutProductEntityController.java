package de.flix29.sproutdemo.entities.generated.controllers;

import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import jakarta.validation.Valid;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        path = "/products",
        produces = "application/json"
)
@Generated("SproutProcessor")
public class SproutProductEntityController {
    private final SproutProductEntityService service;

    public SproutProductEntityController(SproutProductEntityService service) {
        this.service = service;
    }

    /**
     * Returns all ProductEntity items.
     */
    @GetMapping
    public ResponseEntity<List<ProductEntity>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    /**
     * Returns a single ProductEntity item by its ID.
     */
    @GetMapping(
            path = "/{id}"
    )
    public ResponseEntity<ProductEntity> getById(@PathVariable Long id) {
        return service.findById(id)
                        .map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build())
                ;
    }

    /**
     * Creates a new ProductEntity item.
     */
    @PostMapping(
            consumes = "application/json"
    )
    public ResponseEntity<ProductEntity> create(
            @RequestBody @Valid ProductEntity newProductEntity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(newProductEntity));
    }

    /**
     * Updates an existing ProductEntity item by its ID.
     */
    @PutMapping(
            path = "/{id}",
            consumes = "application/json"
    )
    public ResponseEntity<ProductEntity> update(@PathVariable("id") Long id,
            @RequestBody @Valid ProductEntity updatedProductEntity) {
        return service.update(id, updatedProductEntity)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build())
                ;
    }

    /**
     * Deletes an existing ProductEntity item by its ID.
     */
    @DeleteMapping(
            path = "/{id}"
    )
    public ResponseEntity<Void> deleteById(@PathVariable("id") Long id) {
        return service.deleteById(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
