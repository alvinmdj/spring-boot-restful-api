package com.alvinmdj.springbootrestfulapi.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchContactRequest {
  private String name;

  private String email;

  private String phone;

  // @NotNull constraint won't allow null values for the constrained field(s).
  // However, the field(s) can be empty.
  @NotNull
  private Integer page;

  // @NotNull constraint won't allow null values for the constrained field(s).
  // However, the field(s) can be empty.
  @NotNull
  private Integer size;
}
