package org.example.proektnupraktikum.Dto.Application.Request;

import lombok.Getter;
import lombok.Setter;
import org.example.proektnupraktikum.Entity.Enum.ApplicationStatus;

@Getter
@Setter
public class ApplicationStatusUpdateRequest {
    private ApplicationStatus status;
}

