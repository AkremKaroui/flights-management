package com.pm.flightsmanagement.services.impl;

import com.pm.flightsmanagement.dtos.Response;
import com.pm.flightsmanagement.dtos.RoleDTO;
import com.pm.flightsmanagement.entities.Role;
import com.pm.flightsmanagement.exceptions.NotFoundException;
import com.pm.flightsmanagement.repos.RoleRepo;
import com.pm.flightsmanagement.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;
    private final ModelMapper modelMapper;

    @Override
    public Response<?> createRole(RoleDTO roleDTO) {
        log.info("Inside Create Role");
        Role role = modelMapper.map(roleDTO, Role.class);
        role.setName(role.getName().toUpperCase());
        roleRepo.save(role);
        return Response.builder()
                .message("Role Created Successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<?> updateRole(RoleDTO roleDTO) {
        log.info("Inside Update Role");
        Role roleToUpdate = roleRepo.findById(roleDTO.getId())
                .orElseThrow(() -> new NotFoundException("Role Doesn't exists"));
        roleToUpdate.setName(roleDTO.getName().toUpperCase());
        roleRepo.save(roleToUpdate);
        return Response.builder()
                .message("Role Updated Successfully")
                .statusCode(HttpStatus.OK.value())
                .build();
    }

    @Override
    public Response<List<RoleDTO>> getAllRoles() {
        log.info("Inside get all Role");
        List<Role> roles = roleRepo.findAll();
        List<RoleDTO> rolesDTOS =
                roles.stream().map(role -> modelMapper.map(role, RoleDTO.class)).toList();
        return Response.<List<RoleDTO>>builder()
                .message("Roles persisted")
                .statusCode(HttpStatus.OK.value())
                .data(rolesDTOS)
                .build();
    }
}
