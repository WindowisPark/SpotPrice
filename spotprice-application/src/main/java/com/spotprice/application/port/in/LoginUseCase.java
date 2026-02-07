package com.spotprice.application.port.in;

import com.spotprice.application.dto.command.LoginCommand;
import com.spotprice.application.dto.result.AuthResult;

public interface LoginUseCase {

    AuthResult login(LoginCommand command);
}
