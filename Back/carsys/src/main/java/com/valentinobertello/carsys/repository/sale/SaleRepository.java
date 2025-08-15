package com.valentinobertello.carsys.repository.sale;

import com.valentinobertello.carsys.entities.sale.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, Long>,
        JpaSpecificationExecutor<SaleEntity> {

    /**
     * Nos retorna todas las ventas hechas por un determinado cliente
     * */
    @Query("SELECT s FROM SaleEntity s WHERE s.client.dni = :dni ORDER BY s.saleDate DESC")
    List<SaleEntity> findAllByClientDni(@Param("dni") String dni);
}
