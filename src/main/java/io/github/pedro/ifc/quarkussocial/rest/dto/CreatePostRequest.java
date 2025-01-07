package io.github.pedro.ifc.quarkussocial.rest.dto;

import io.github.pedro.ifc.quarkussocial.domain.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreatePostRequest {
    @NotBlank(message = "PostText is Required")
    private String post_text;
}
