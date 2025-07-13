package com.valentinobertello.carsys.repository.car;

import com.valentinobertello.carsys.entities.car.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<CarEntity, Long> {
}
