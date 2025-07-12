package com.valentinobertello.carsys.repository;

import com.valentinobertello.carsys.entities.auth.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findByNameIn(List<String> names);
    RoleEntity findByName(String name);
}
