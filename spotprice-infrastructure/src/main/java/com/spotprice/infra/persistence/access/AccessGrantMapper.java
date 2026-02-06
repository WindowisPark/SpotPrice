package com.spotprice.infra.persistence.access;

import com.spotprice.domain.access.AccessGrant;
import com.spotprice.domain.access.AccessGrantStatus;
import com.spotprice.domain.access.GrantType;

public class AccessGrantMapper {

    private AccessGrantMapper() {
    }

    public static AccessGrant toDomain(AccessGrantEntity entity) {
        return AccessGrant.restore(
                entity.getId(),
                entity.getOrderId(),
                GrantType.valueOf(entity.getGrantType()),
                entity.getGrantValue(),
                entity.getValidFrom(),
                entity.getValidTo(),
                AccessGrantStatus.valueOf(entity.getStatus())
        );
    }

    public static AccessGrantEntity toEntity(AccessGrant grant) {
        return new AccessGrantEntity(
                grant.getId(),
                grant.getOrderId(),
                grant.getGrantType().name(),
                grant.getGrantValue(),
                grant.getValidFrom(),
                grant.getValidTo(),
                grant.getStatus().name()
        );
    }
}
