package com.spotprice.application.port.in;

import com.spotprice.application.dto.command.RegisterCommand;
import com.spotprice.application.dto.result.AuthResult;

public interface RegisterUseCase {

    AuthResult register(RegisterCommand command);
}
