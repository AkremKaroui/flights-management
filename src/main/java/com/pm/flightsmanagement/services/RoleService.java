package com.pm.flightsmanagement.services;

import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.dtos.RoleDTO;

import java.util.List;

public interface RoleService {
    Response<?> createRole(RoleDTO roleDTO);

    Response<?> updateRole(RoleDTO roleDTO);

    Response<List<RoleDTO>> getAllRoles();
}
