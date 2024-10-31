package com.printScript.permissionsManager.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareSnippetDTO {

    @NotBlank
    private String snippetId;

    @NotBlank
    private String username;
}
