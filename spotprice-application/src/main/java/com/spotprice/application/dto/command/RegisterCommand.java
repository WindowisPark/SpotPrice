package com.spotprice.application.dto.command;

public record RegisterCommand(String email, String rawPassword) {
}
