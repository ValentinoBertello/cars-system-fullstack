package com.valentinobertello.carsys.dtos.car;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelResponse {
    private Long id;
    private String name;
    private String brandName;
}
