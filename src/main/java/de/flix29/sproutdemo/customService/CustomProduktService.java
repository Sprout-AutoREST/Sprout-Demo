package de.flix29.sproutdemo.customService;

import de.flix29.sproutdemo.entities.ProductEntity;
import de.flix29.sproutdemo.entities.generated.repositories.SproutProductEntityRepository;
import de.flix29.sproutdemo.entities.generated.services.SproutProductEntityService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class CustomProduktService extends SproutProductEntityService {

    public CustomProduktService(SproutProductEntityRepository repository) {
        super(repository);
    }

    @Override
    public List<ProductEntity> findAll() {
        System.out.println("using custom product service");
        return repository.findAll();
    }
}
