package com.spotprice.application.port.out;

import com.spotprice.domain.user.User;

import java.util.Optional;

public interface UserRepositoryPort {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    User save(User user);
}
