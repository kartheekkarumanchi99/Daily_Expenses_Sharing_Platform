package com.expensesharing.com.expensesharing.dto;

import jakarta.validation.constraints.NotNull;

public class AddMemberRequest {

    @NotNull(message = "userId is mandatory")
    private Long userId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
