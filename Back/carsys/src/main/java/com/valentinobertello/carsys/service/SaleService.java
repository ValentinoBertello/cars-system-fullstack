package com.valentinobertello.carsys.service;

import com.valentinobertello.carsys.dtos.sale.PostSaleDto;
import com.valentinobertello.carsys.dtos.sale.PostSaleWithClientDto;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface SaleService {
    SaleResponse saveSaleWithClient(PostSaleWithClientDto postSale, String name);

    SaleResponse saveSale(PostSaleDto postSale, String name);

    Page<SaleResponse> searchSalesPageByFilters(LocalDate sinceDate, LocalDate untilDate, String clientQuery,
                                           String carQuery, String name, Pageable pageable);

    List<SaleResponse> getSalesByClientDni(String dni);
}
