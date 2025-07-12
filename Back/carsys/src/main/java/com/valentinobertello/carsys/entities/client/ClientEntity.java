package com.valentinobertello.carsys.entities.client;

import com.valentinobertello.carsys.entities.auth.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String phone;

    @Column
    private String dni;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;
}
