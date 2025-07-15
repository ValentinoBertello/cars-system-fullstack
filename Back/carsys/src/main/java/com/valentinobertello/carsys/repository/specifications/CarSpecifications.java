package com.valentinobertello.carsys.repository.specifications;

import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Este mismo filtro podría implementarse con JPQL algo así:
 *
 * @Query("SELECT c FROM CarEntity c " +
 *         "LEFT JOIN c.model m " +
 *         "LEFT JOIN m.brand b " +
 *         "WHERE (:licensePlate IS NULL OR UPPER(c.licensePlate) LIKE CONCAT('%', UPPER(:licensePlate), '%')) " +
 *         "AND (:brand IS NULL OR UPPER(b.name) LIKE CONCAT('%', UPPER(:brand), '%')) " +
 *         "AND (:model IS NULL OR UPPER(m.name) LIKE CONCAT('%', UPPER(:model), '%'))"
 *         )
 * **/
public class CarSpecifications {

    private CarSpecifications() {
    }

    /**
     * Genera una Specification dinámica para buscar autos por: licensePlate, brand o model.
     * @return Specification<CarEntity> con los predicados (condiciones) combinados.
     * **/
    public static Specification<CarEntity> carSearch(String licensePlate, String brand, String model, String username) {
        return (root, query, cb) -> {

            if (!Long.class.equals(query.getResultType())) {
                // así Spring sabe que también tiene que hacer JOIN para el ORDER BY
                root.fetch("model", JoinType.LEFT)
                        .fetch("brand", JoinType.LEFT);

            }

            query.distinct(true);

            //Empezamos con una conjunción vacía, para ir añadiendo condiciones
            Predicate predicate = cb.conjunction();

            //Filtro por username (que los autos correspondan al usuario autenticado)
            if (username != null && !username.isEmpty()) {
                Join<CarEntity, UserEntity> joinUser = root.join("user");
                predicate = cb.and(predicate,
                        cb.equal(joinUser.get("email"), username));
            }

            //Filtro por patente
            if (licensePlate != null && !licensePlate.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.upper(root.get("licensePlate")), "%" + licensePlate.toUpperCase() + "%"));
            }

            //Filtro por marca
            if (brand != null && !brand.isEmpty()) {

                // Hacemos JOIN desde CarEntity → ModelEntity → BrandEntity
                Join<CarEntity, ModelEntity> joinModel = root.join("model");
                Join<ModelEntity, BrandEntity> joinBrand = joinModel.join("brand");
                predicate = cb.and(predicate,
                        cb.like(cb.upper(joinBrand.get("name")),"%" + brand.toUpperCase() + "%"));
            }

            //Filtro por model
            if (model != null && !model.isEmpty()) {

                /// JOIN desde CarEntity → ModelEntity
                Join<CarEntity, ModelEntity> joinModel = root.join("model");
                predicate = cb.and(predicate,
                        cb.like(cb.upper(joinModel.get("name")),"%" + model.toUpperCase() + "%"));
            }

            return predicate;
        };
    }
}
