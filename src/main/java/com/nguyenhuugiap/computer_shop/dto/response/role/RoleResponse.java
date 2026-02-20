package com.nguyenhuugiap.computer_shop.dto.response.role;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class RoleResponse {
    Long id;
    String name;
    String description;
}
