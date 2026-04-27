package org.example.proektnupraktikum.Dto.Resume;

import lombok.Getter;
import lombok.Setter;
import org.example.proektnupraktikum.Entity.StudentProfile;

@Getter
@Setter
public class ResponseResume {
    private Long id;

    private StudentProfile student;

    private String fileUrl;
}
