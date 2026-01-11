package com.csis231.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long customerId;
    
    @NotBlank
    private String name;
    
    @NotBlank
    private String contact;
}
