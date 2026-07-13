package com.example.user.api;

import com.example.user.api.dto.UserDto;
import com.example.user.user.AppUser;

public final class UserMapper {
  private UserMapper() {}

  public static UserDto toDto(AppUser u) {
    return new UserDto(String.valueOf(u.getId()), u.getEmail(), u.getDisplayName());
  }
}

