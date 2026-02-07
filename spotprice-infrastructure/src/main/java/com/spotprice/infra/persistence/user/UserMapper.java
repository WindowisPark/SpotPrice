package com.spotprice.infra.persistence.user;

import com.spotprice.domain.user.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toDomain(UserEntity entity) {
        return User.restore(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getCreatedAt()
        );
    }

    public static UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getCreatedAt()
        );
    }
}
