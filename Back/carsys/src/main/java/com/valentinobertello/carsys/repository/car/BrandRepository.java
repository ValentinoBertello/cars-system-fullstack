package com.valentinobertello.carsys.repository.car;

import com.valentinobertello.carsys.entities.car.BrandEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<BrandEntity, Long> {
}
