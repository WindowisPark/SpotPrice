package com.spotprice.application.port.out;

import com.spotprice.application.dto.AuditEvent;

public interface AuditLogPort {

    void log(AuditEvent event);
}
