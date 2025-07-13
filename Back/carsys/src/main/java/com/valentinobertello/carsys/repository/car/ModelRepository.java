package com.valentinobertello.carsys.repository.car;

import com.valentinobertello.carsys.entities.car.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelRepository extends JpaRepository<ModelEntity, Long> {
}
