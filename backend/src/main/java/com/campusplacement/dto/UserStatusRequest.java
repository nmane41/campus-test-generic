package com.campusplacement.dto;

import javax.validation.constraints.NotNull;

public class UserStatusRequest {
    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    public UserStatusRequest() {
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

