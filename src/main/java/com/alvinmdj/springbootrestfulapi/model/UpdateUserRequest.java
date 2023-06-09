package com.alvinmdj.springbootrestfulapi.model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
  // since either name or password is optional to update, no need for @NotBlank validation.
  @Size(max = 100)
  private String name;

  @Size(max = 100)
  private String password;
}
