package com.valentinobertello.carsys.repository.specifications;

import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.entities.client.ClientEntity;
import com.valentinobertello.carsys.entities.sale.SaleEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Clase que provee una Specification dinámica para buscar entidades SaleEntity
 * filtrando por fechaDesde, fechaHasta, cliente y patente de auto.
 * La Specification se puede combinar con paginación y ordenamiento en el repositorio.
 */
public class SaleSpecifications {

    /**
     * Genera una Specification dinámica para buscar ventas por:
     * por fechaDesde, fechaHasta, cliente o patente de auto.
     * Cada filtro es opcional y solo se añade si el parámetro no es nulo o vacío.
     * */
    public static Specification<SaleEntity> saleSearch(LocalDate sinceDate, LocalDate untilDate, String clientQuery
            , String carQuery, String name) {
        return (root, query, cb) -> {
            // root: representa la raíz de la consulta, es decir la entidad SaleEntity
            // query: objeto CriteriaQuery que define el SELECT, JOINs, ORDER BY, etc.
            // criteriaBuilder: fábrica de predicates y expresiones (AND, OR, LIKE, etc) para el where

            // Empezamos con conjución vacía
            Predicate predicate = cb.conjunction();

            // Filtro por username (que las ventas correspondan al usuario autenticado)
            if(name != null && !name.isEmpty()) {
                Join<SaleEntity, UserEntity> joinUser = root.join("user");
                predicate = cb.and(predicate,
                        cb.equal(joinUser.get("email"), name));
            }

            // Filtro opcional de fecha "desde" y "hasta"
            if(sinceDate != null && untilDate != null) {
                LocalDateTime start = sinceDate.atStartOfDay();
                LocalDateTime end = untilDate.atTime(LocalTime.MAX); // 23:59:59
                predicate = cb.and(predicate,
                        cb.between(root.get("saleDate"),
                                start, end));
            }

            // Filtro opcional por dni, nombre o apellido de cliente
            if (clientQuery != null && !clientQuery.isEmpty()) {
                // Hacemos JOIN con la tabla de clientes (relación SaleEntity -> ClientEntity)
                Join<SaleEntity, ClientEntity> joinClient = root.join("client");

                // Creamos tres condiciones de búsqueda (predicados) independientes:
                Predicate dniPredicate = cb.like(joinClient.get("dni"), "%" + clientQuery + "%");
                Predicate namePredicate = cb.like(joinClient.get("name"), "%" + clientQuery + "%");
                Predicate lastNamePredicate = cb.like(joinClient.get("lastName"), "%" + clientQuery + "%");

                // Combinamos las condiciones con OR (debe cumplir al menos una)
                predicate = cb.and(predicate, cb.or(dniPredicate, namePredicate, lastNamePredicate));
            }

            // Filtro opcional por patente, modelo o marca de auto vendido
            if(carQuery != null && !carQuery.isEmpty()) {
                Join<SaleEntity, CarEntity> joinCar = root.join("car");
                Join<CarEntity, ModelEntity> joinModel = joinCar.join("model");
                Join<ModelEntity, BrandEntity> joinBrand = joinModel.join("brand");

                Predicate licensePlatePredicate = cb.like(joinCar.get("licensePlate"), "%" + carQuery + "%");
                Predicate modelPredicate = cb.like(joinModel.get("name"), "%" + carQuery + "%");
                Predicate brandPredicate = cb.like(joinBrand.get("name"), "%" + carQuery + "%");

                predicate = cb.and(predicate, cb.or(licensePlatePredicate, modelPredicate, brandPredicate));
            }

            return predicate;
        };
    }
}
