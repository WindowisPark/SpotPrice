package com.spotprice.application.dto.command;

public record LoginCommand(String email, String rawPassword) {
}
