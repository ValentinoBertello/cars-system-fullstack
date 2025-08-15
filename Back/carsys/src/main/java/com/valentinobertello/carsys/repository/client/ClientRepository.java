package com.valentinobertello.carsys.repository.client;

import com.valentinobertello.carsys.entities.client.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    boolean existsByDni(String dni);
    boolean existsByPhone(String phone);

    /**
     * Busca clientes que pertenezcan al usuario (por su email)
     * y cuyo DNI, nombre o apellido contengan el filtro especificado.
     *
     * @param filter texto a buscar en dni, name o lastName
     * @param email  email del usuario dueño de los clientes
     * @return lista de ClientEntity con los clientes coincidentes
     */
    @Query("""
       SELECT c
         FROM ClientEntity c
        WHERE c.user.email = :email
          AND (
                c.dni       LIKE   (CONCAT('%', :filter, '%'))\s
             OR LOWER(c.name)     LIKE LOWER(CONCAT('%', :filter, '%'))
             OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :filter, '%'))
          )
      \s""")
    List<ClientEntity> searchByFilter(
            @Param("filter") String filter,
            @Param("email")  String email
    );

    /**
     * Busca clientes que pertenezcan al usuario (por su email)
     * y cuyo DNI, nombre o apellido contengan el filtro especificado.
     *
     * @return un objeto Page<ClientEntity> que contiene la página actual
     * de resultados, junto con información adicional.
     */
    @Query("""
   SELECT c
     FROM ClientEntity c
    WHERE c.user.email = :email
      AND (:filter IS NULL OR :filter = ''  OR c.dni LIKE CONCAT('%', :filter, '%')
           OR LOWER(c.name)     LIKE LOWER(CONCAT('%', :filter, '%'))
           OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :filter, '%'))
          )
""")
    Page<ClientEntity> searchByFilter(
            @Param("filter") String filter,
            @Param("email")  String email,
            Pageable pageable
    );

    /**
     * Verifica si ya existe un cliente con el mismo numero de celular
     * asociado al usuario con el email proporcionado.
     *
     * @param email Email del usuario autenticado (para filtrar solo sus vehículos).
     */
    @Query("SELECT COUNT(c) > 0 FROM ClientEntity c WHERE c.phone = :phone AND c.user.email = :email")
    boolean existsByPhoneAndUserEmail(
            @Param("phone") String phone,
            @Param("email") String email
    );

    /**
     * Verifica si ya existe un cliente con el mismo dni
     * asociado al usuario con el email proporcionado.
     *
     * @param email Email del usuario autenticado (para filtrar solo sus vehículos).
     */
    @Query("SELECT COUNT(c) > 0 FROM ClientEntity c WHERE c.dni = :dni AND c.user.email = :email")
    boolean existsByDniAndUserEmail(
            @Param("dni") String dni,
            @Param("email") String email
    );
}
