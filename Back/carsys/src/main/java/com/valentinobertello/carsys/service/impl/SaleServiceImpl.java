package com.valentinobertello.carsys.service.impl;

import com.valentinobertello.carsys.dtos.sale.PostSaleDto;
import com.valentinobertello.carsys.dtos.sale.PostSaleWithClientDto;
import com.valentinobertello.carsys.dtos.sale.SaleResponse;
import com.valentinobertello.carsys.entities.auth.UserEntity;
import com.valentinobertello.carsys.entities.car.CarEntity;
import com.valentinobertello.carsys.entities.client.ClientEntity;
import com.valentinobertello.carsys.entities.sale.SaleEntity;
import com.valentinobertello.carsys.enums.CarStatus;
import com.valentinobertello.carsys.mapper.ClientDataMapper;
import com.valentinobertello.carsys.mapper.SaleDataMapper;
import com.valentinobertello.carsys.repository.auth.UserRepository;
import com.valentinobertello.carsys.repository.car.CarRepository;
import com.valentinobertello.carsys.repository.client.ClientRepository;
import com.valentinobertello.carsys.repository.sale.SaleRepository;
import com.valentinobertello.carsys.repository.specifications.SaleSpecifications;
import com.valentinobertello.carsys.service.SaleService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Toda la lógica de negocio relacionada con las ventas de autos
 * de la aplicación.
 */
@Service
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final CarRepository carRepository;

    private final SaleDataMapper saleDataMapper;
    private final ClientDataMapper clientDataMapper;

    public SaleServiceImpl(SaleRepository saleRepository, UserRepository userRepository, ClientRepository clientRepository, CarRepository carRepository, SaleDataMapper saleDataMapper, ClientDataMapper clientDataMapper) {
        this.saleRepository = saleRepository;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
        this.saleDataMapper = saleDataMapper;
        this.clientDataMapper = clientDataMapper;
    }

    /**
     * Crea y persiste un nueva venta y cliente en la base de datos.
     */
    @Override
    @Transactional
    public SaleResponse saveSaleWithClient(PostSaleWithClientDto postSale, String name) {
        // Obtenemos usuario que realiza la operación
        UserEntity userEntity = getUserByEmail(name);
        // Obtenemos el auto a vender
        CarEntity carEntity = getCarById(postSale.getCarId());
        carEntity.setStatus(CarStatus.VENDIDO);
        carEntity = this.carRepository.save(carEntity);
        // Creamos y guardamos el cliente
        ClientEntity clientEntity = createAndSaveClient(postSale, userEntity);
        // Creamos y guardamos la venta, referenciando auto, cliente y usuario
        SaleEntity saleEntity = createAndSaveSale(postSale.getSalePrice(), userEntity, clientEntity, carEntity);

        return this.saleDataMapper.mapSaleEntityToSaleResponse(saleEntity);
    }

    /**
     * Crea y persiste un nueva venta.
     */
    @Override
    @Transactional
    public SaleResponse saveSale(PostSaleDto postSale, String name) {
        // Obtenemos usuario que realiza la operación
        UserEntity userEntity = getUserByEmail(name);
        // Obtenemos el auto a vender
        CarEntity carEntity = getCarById(postSale.getCarId());
        carEntity.setStatus(CarStatus.VENDIDO);
        carEntity = this.carRepository.save(carEntity);
        // Obtenemos el cliente
        ClientEntity clientEntity = this.clientRepository.findById(postSale.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado"));
        // Creamos y guardamos la venta
        SaleEntity saleEntity = this.createAndSaveSale(postSale.getSalePrice(), userEntity, clientEntity, carEntity);
        return this.saleDataMapper.mapSaleEntityToSaleResponse(saleEntity);
    }

    /**
     * Busca ventas según filtros: fecha "desde" y fecha "hasta", dni de cliente y
     * patente de auto vendido.
     * Solo devuelve las ventas del usuario autenticado.
     * @return página de SaleResponse con los resultados.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<SaleResponse> searchSalesPageByFilters(LocalDate sinceDate, LocalDate untilDate, String clientQuery,
                                                  String carQuery, String name, Pageable pageable) {
        Page<SaleEntity> saleEntities = this.saleRepository.findAll(
                SaleSpecifications.saleSearch(sinceDate, untilDate, clientQuery, carQuery, name), pageable);
        return this.saleDataMapper.mapSaleEntitiesPageToSaleResponsesPage(saleEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getSalesByClientDni(String dni) {
        List<SaleEntity> salesEntities = this.saleRepository.findAllByClientDni(dni);
        return this.saleDataMapper.mapSaleEntitiesToSaleResponses(salesEntities);
    }

    /**
     * Busca un usuario por su email.
     */
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con email: " + email));
    }

    /**
     * Busca un auto por su ID.
     */
    public CarEntity getCarById(Long id) {
        CarEntity car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Auto no encontrado con ID: " + id));
        if (car.getStatus().equals(CarStatus.VENDIDO)) {
            throw new RuntimeException("Auto ya vendido.");
        }
        return car;
    }

    /**
     * Construye y persiste un nuevo cliente a partir
     * de los datos recibidos en el DTO.
     */
    public ClientEntity createAndSaveClient(PostSaleWithClientDto dto, UserEntity user) {
        this.validateClient(dto);
        return clientRepository.save(clientDataMapper.mapRequestToClientEntity(dto, user));
    }

    /**
     * Validamos que datos del cliente nuevo no existan en la bd
     */
    public void validateClient(PostSaleWithClientDto dto) {
        String dni = dto.getClientDni();
        if (clientRepository.existsByDni(dni)) {
            throw new EntityExistsException("Ya existe un cliente con DNI: " + dni);
        }

        String phone = dto.getClientPhone();
        if (phone != null && !phone.isBlank() && clientRepository.existsByPhone(phone)) {
            throw new EntityExistsException("Ya existe un cliente con teléfono: " + phone);
        }
    }

    /**
     * Construye y persiste una nueva venta a partir
     * de los datos recibidos y las entidades relacionadas.
     */
    public SaleEntity createAndSaveSale(BigDecimal salePrice, UserEntity user, ClientEntity client, CarEntity car) {
        SaleEntity sale = new SaleEntity();
        sale.setSaleDate(LocalDateTime.now());
        sale.setSalePrice(salePrice);
        sale.setUser(user);
        sale.setClient(client);
        sale.setCar(car);
        return saleRepository.save(sale);
    }
}
