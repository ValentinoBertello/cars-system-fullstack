package com.valentinobertello.carsys.entities.car;

import com.valentinobertello.carsys.entities.auth.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "cars")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate")
    private String licensePlate;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private ModelEntity model;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private Integer year;

    @Column
    private String color;

    @Column(name = "base_price")
    private BigDecimal basePrice;

    @Column
    private BigDecimal mileage;

    @Column
    private String status;
}

