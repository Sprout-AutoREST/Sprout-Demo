package de.flix29.sproutdemo.entities.generated.marker;

import de.flix29.sproutdemo.entities.ProductEntity;
import javax.annotation.processing.Generated;

@Generated("SproutProcessor")
public final class ProductEntitySproutMarker {
    public static final String PATH = "/products";

    public static final Class ID_CLASS = ProductEntity.class;

    public static final String ENTITY_NAME = "Product";

    public static final String ID_PROPERTY = "id";

    public static final String READ_POLICY = "hasRole('ROLE_USER')";

    public static final String WRITE_POLICY = "hasRole('ROLE_ADMIN')";

    public static final String UPDATE_POLICY = "hasRole('ROLE_ADMIN')";

    public static final String DELETE_POLICY = "hasRole('ROLE_ADMIN')";
}
