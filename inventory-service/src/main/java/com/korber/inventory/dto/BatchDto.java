package com.korber.inventory.dto;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BatchDto {
	private Long batchId;
	private String batchNumber;
	private LocalDate expiryDate;
	private Integer quantity;
}
