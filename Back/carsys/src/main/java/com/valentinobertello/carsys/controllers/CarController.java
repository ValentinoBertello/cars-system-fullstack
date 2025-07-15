package com.valentinobertello.carsys.controllers;

import com.valentinobertello.carsys.dtos.car.CarResponse;
import com.valentinobertello.carsys.dtos.car.ModelResponse;
import com.valentinobertello.carsys.dtos.car.PostCarDto;
import com.valentinobertello.carsys.dtos.car.UpdateCarDto;
import com.valentinobertello.carsys.entities.car.BrandEntity;
import com.valentinobertello.carsys.entities.car.ModelEntity;
import com.valentinobertello.carsys.service.CarService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*")
@RestController
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * POST /cars/register
     * Crea un nuevo coche en el sistema.
     */
    @PostMapping("/register")
    public ResponseEntity<CarResponse> createCar(@RequestBody @Valid PostCarDto carRequest){
        return ResponseEntity.ok(this.carService.createCar(carRequest));
    }

    /**
     * PUT /cars/update
     * Edita ciertos datos de un auto en particular.
     */
    @PutMapping("/update")
    public ResponseEntity<CarResponse> updateCar(@RequestBody @Valid UpdateCarDto carRequest, Authentication authentication){
        return ResponseEntity.ok(this.carService.updateCar(carRequest, authentication.getName()));
    }

    /**
     * GET /cars/search
     * Busca coches según filtros opcionales: patente, marca y modelo.
     * Solo devuelve los coches del usuario autenticado.
     * @return página de CarResponse con los resultados.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchCarsByFilters(
            Authentication authentication,
            @RequestParam(required = false) String licensePlate,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            Pageable pageable

    ){

            Page<CarResponse> results = carService.searchCarsByFilters(
                    licensePlate, brand, model,
                    authentication.getName(), pageable
            );
            return ResponseEntity.ok(results);

    }

    /**
     * GET /cars/models
     * Devuelve la lista completa de modelos de coche disponibles.
     */
    @GetMapping("/models")
    public ResponseEntity<List<ModelResponse>> getAllModels(){
        return ResponseEntity.ok(this.carService.getAllModels());
    }

    /**
     * GET /cars/brands
     * Devuelve la lista completa de marcas de coche.
     */
    @GetMapping("/brands")
    public ResponseEntity<List<BrandEntity>> getAllBrands(){
        return ResponseEntity.ok(this.carService.getAllBrands());
    }
}
