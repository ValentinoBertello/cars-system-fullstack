package com.valentinobertello.carsys.repository.car;

import com.valentinobertello.carsys.entities.car.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long>,
        JpaSpecificationExecutor<CarEntity> {

    /**
     * Verifica si ya existe un vehículo con la misma placa (licensePlate)
     * asociado al usuario con el email proporcionado.
     *
     * @param email Email del usuario autenticado (para filtrar solo sus vehículos).
     */
    @Query("SELECT COUNT(c) > 0 FROM CarEntity c WHERE c.licensePlate = :licensePlate AND c.user.email = :email")
    boolean existsByLicensePlateAndUserEmail(
            @Param("licensePlate") String licensePlate,
            @Param("email") String email
    );

    boolean existsByLicensePlate(String licensePlate);

    /**
     * Busca coches cuyo campo `licensePlate`, `model.name` o `brand.name`
     * contienen el texto `carQuery` (match parcial, case-insensitive).
     */
    @Query("SELECT c FROM CarEntity c " +
            "LEFT JOIN c.model m " +
            "LEFT JOIN m.brand b " +
            "WHERE c.status = 'DISPONIBLE' AND (" +
            "LOWER(c.licensePlate) LIKE LOWER(CONCAT('%', :carQuery, '%')) " +
            "OR LOWER(m.name) LIKE LOWER(CONCAT('%', :carQuery, '%')) " +
            "OR LOWER(b.name) LIKE LOWER(CONCAT('%', :carQuery, '%'))" +
            ")")
    List<CarEntity> findByCarQuery(String carQuery);
}
