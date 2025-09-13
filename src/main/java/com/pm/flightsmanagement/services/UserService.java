package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.dtos.UserDTO;
import com.pm.flightsmanagement.entities.User;

import java.util.List;

public interface UserService {
    User currentUser();
    Response<?> updateMyAccount(UserDTO userDTO);
    Response<List<UserDTO>> getAllPilots();
    Response <UserDTO> getAccountDetails();
}
