package com.printScript.permissionsManager.DTO;

import com.printScript.permissionsManager.entities.ConfigType;

import lombok.Getter;

@Getter
public class DependencyDTO {
    private String userId;
    private ConfigType configType;
}
