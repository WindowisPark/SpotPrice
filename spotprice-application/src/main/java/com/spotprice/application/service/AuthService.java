package com.spotprice.application.service;

import com.spotprice.application.dto.command.LoginCommand;
import com.spotprice.application.dto.command.RegisterCommand;
import com.spotprice.application.dto.result.AuthResult;
import com.spotprice.application.port.in.LoginUseCase;
import com.spotprice.application.port.in.RegisterUseCase;
import com.spotprice.application.port.out.ClockPort;
import com.spotprice.application.port.out.PasswordEncoderPort;
import com.spotprice.application.port.out.UserRepositoryPort;
import com.spotprice.domain.exception.AuthenticationFailedException;
import com.spotprice.domain.exception.EmailAlreadyExistsException;
import com.spotprice.domain.user.User;
import org.springframework.transaction.annotation.Transactional;

public class AuthService implements RegisterUseCase, LoginUseCase {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final ClockPort clock;

    public AuthService(UserRepositoryPort userRepository,
                       PasswordEncoderPort passwordEncoder,
                       ClockPort clock) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
    }

    @Override
    @Transactional
    public AuthResult register(RegisterCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        String hash = passwordEncoder.encode(command.rawPassword());
        User user = new User(command.email(), hash, clock.now());
        User saved = userRepository.save(user);

        return new AuthResult(saved.getId(), saved.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(AuthenticationFailedException::new);

        if (!passwordEncoder.matches(command.rawPassword(), user.getPasswordHash())) {
            throw new AuthenticationFailedException();
        }

        return new AuthResult(user.getId(), user.getEmail());
    }
}
