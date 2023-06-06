package com.alvinmdj.springbootrestfulapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Make WebResponse class as a generic class,
// because 'data' variable type can be a string, object, or list.
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebResponse<T> {
  private T data;

  private String errors;
}
