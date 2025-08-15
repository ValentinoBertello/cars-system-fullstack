package com.valentinobertello.carsys.mapper;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.client.ClientResponse;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import com.valentinobertello.carsys.entities.sale.SaleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * La clase "SaleDataMapper" se encarga de mapear objetos "request", "entity" y "response"
 * entre ellos.
 */
@Component
public class SaleDataMapper {

    private final CarDataMapper carDataMapper;
    private final ClientDataMapper clientDataMapper;

    public SaleDataMapper(CarDataMapper carDataMapper,
                          ClientDataMapper clientDataMapper) {
        this.carDataMapper    = carDataMapper;
        this.clientDataMapper = clientDataMapper;
    }

    /**
     * Mapea las entidades de cliente, venta y coche a un SaleResponse completo.
     */
    public SaleResponse mapSaleEntityToSaleResponse(SaleEntity saleEntity) {
        // Mapea el auto
        CarResponse carResp = carDataMapper.mapCarEntityToCarResponse(saleEntity.getCar());

        // Mapea el cliente
        ClientResponse clientResp = clientDataMapper.mapClientEntityToClientResponse(saleEntity.getClient());

        return SaleResponse.builder()
                .id(saleEntity.getId())
                .car(carResp)
                .client(clientResp)
                .saleDate(saleEntity.getSaleDate())
                .salePrice(saleEntity.getSalePrice())
                .build();
    }

    /**
     * Mapea una "Page" de "saleEntity" a una "Page" de DTOs de respuesta.
     */
    public Page<SaleResponse> mapSaleEntitiesPageToSaleResponsesPage(Page<SaleEntity> saleEntitiesPage) {
        List<SaleResponse> saleResponses = mapSaleEntitiesToSaleResponses(saleEntitiesPage.getContent());
        return new PageImpl<>(
                saleResponses,
                saleEntitiesPage.getPageable(),
                saleEntitiesPage.getTotalElements()
        );
    }

    /**
     * Mapea una lista de SaleEntity a una lista de SaleResponse.
     */
    public List<SaleResponse> mapSaleEntitiesToSaleResponses(List<SaleEntity> saleEntities) {
        List<SaleResponse> saleResponses = new ArrayList<>(saleEntities.size());
        for (SaleEntity entity : saleEntities) {
            saleResponses.add(this.mapSaleEntityToSaleResponse(entity));
        }
        return saleResponses;
    }
}
