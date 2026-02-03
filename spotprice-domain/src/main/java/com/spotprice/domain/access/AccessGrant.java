package com.spotprice.domain.access;

import java.time.Instant;

/**
 * 접근 권한 VO
 * TODO: 필드 및 검증 로직 구현
 */
public class AccessGrant {

    private Long id;
    private Long offerId;
    private String grantee;
    private GrantType grantType;
    private Instant expiresAt;

    // TODO: 생성자 및 검증 로직 구현
}
