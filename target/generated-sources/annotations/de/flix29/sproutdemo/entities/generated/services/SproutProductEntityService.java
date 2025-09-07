package de.flix29.sproutdemo.entities.generated.services;

import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.generated.repositories.SproutProductEntityRepository;
import java.util.List;
import java.util.Optional;
import javax.annotation.processing.Generated;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Generated("SproutProcessor")
public class SproutProductEntityService {
    private final SproutProductEntityRepository repository;

    public SproutProductEntityService(SproutProductEntityRepository repository) {
        this.repository = repository;
    }

    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    public Optional<ProductEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public ProductEntity save(ProductEntity entity) {
        return repository.save(entity);
    }

    @Transactional
    public Optional<ProductEntity> update(Long id, ProductEntity entity) {
        return repository.findById(id).map(existing -> { BeanUtils.copyProperties(entity, existing, "id"); return repository.save(existing); });
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
