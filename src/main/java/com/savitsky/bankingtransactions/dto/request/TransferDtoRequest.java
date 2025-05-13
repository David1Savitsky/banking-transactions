package com.savitsky.bankingtransactions.dto.request;

import java.math.BigDecimal;

public record TransferDtoRequest(Long toUserId, BigDecimal amount) {
}
