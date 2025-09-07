package de.flix29.sproutdemo.entities.generated.repositories;

import de.flix29.sproutdemo.entities.ProductEntity;
import javax.annotation.processing.Generated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Generated("SproutProcessor")
public interface SproutProductEntityRepository extends JpaRepository<ProductEntity, Long> {
    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Transactional
    @Query("delete from Product e where e.id = :id")
    void deleteById(@Param("id") Long id);
}
