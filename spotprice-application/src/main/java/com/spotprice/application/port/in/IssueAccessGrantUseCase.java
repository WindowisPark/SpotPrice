package com.spotprice.application.port.in;

import com.spotprice.application.dto.result.AccessGrantResult;

public interface IssueAccessGrantUseCase {

    AccessGrantResult issue(Long orderId);
}
