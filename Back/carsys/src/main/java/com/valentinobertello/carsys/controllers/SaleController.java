package com.valentinobertello.carsys.controllers;

import com.valentinobertello.carsys.dtos.sale.PostSaleDto;
import com.valentinobertello.carsys.dtos.sale.PostSaleWithClientDto;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import com.valentinobertello.carsys.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/sales")
public class SaleController {

    private final SaleService saleService;

    public SaleController(SaleService saleService) {
        this.saleService = saleService;
    }

    /**
     * POST /sales/register/with-client
     * Crea una nueva venta junto con un cliente en el sistema.
     */
    @PostMapping("/register/with-client")
    public ResponseEntity<SaleResponse> saveSaleWithClient(@RequestBody @Valid PostSaleWithClientDto postSale,
                                                           Authentication authentication) {
        return ResponseEntity.ok(this.saleService.saveSaleWithClient(postSale, authentication.getName()));
    }

    /**
     * POST /sales/register/
     * Crea una nueva venta de un cliente ya existente
     */
    @PostMapping("/register")
    public ResponseEntity<SaleResponse> saveSale(@RequestBody @Valid PostSaleDto postSale,
                                                           Authentication authentication) {
        return ResponseEntity.ok(this.saleService.saveSale(postSale, authentication.getName()));
    }

    /**
     * GET /sales/search
     * Busca ventas según filtros: fecha "desde" y fecha "hasta", dni de cliente y
     * patente de auto vendido.
     * Solo devuelve las ventas del usuario autenticado.
     * @return página de SaleResponse con los resultados.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchSalesPageByFilters(
            Authentication authentication,
            @RequestParam(required = false) LocalDate sinceDate,
            @RequestParam(required = false) LocalDate untilDate,
            @RequestParam(required = false) String clientQuery,
            @RequestParam(required = false) String carQuery,
            Pageable pageable

    ) {
        Page<SaleResponse> results = saleService.searchSalesPageByFilters(
                sinceDate, untilDate, clientQuery,
                carQuery, authentication.getName(), pageable
        );
        return ResponseEntity.ok(results);
    }

    /**
     * GET /sales/client/{dni}
     * Devuelve todas las ventas del cliente identificado por su DNI.
     */
    @GetMapping("/client/{dni}")
    public ResponseEntity<List<SaleResponse>> getSalesByClientDni(@PathVariable String dni) {
        List<SaleResponse> sales = saleService.getSalesByClientDni(dni);
        return ResponseEntity.ok(sales);
    }
}
