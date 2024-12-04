package com.printScript.permissionsManager.services;

import static com.printScript.permissionsManager.utils.TokenUtils.getUsernameByUserId;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.printScript.permissionsManager.DTO.*;
import com.printScript.permissionsManager.DTO.Error;
import com.printScript.permissionsManager.entities.GrantType;
import com.printScript.permissionsManager.entities.SnippetPermission;
import com.printScript.permissionsManager.repositories.SnippetPermissionRepository;

@Service
public class SnippetPermissionService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SnippetPermissionService.class);

    @Autowired
    SnippetPermissionRepository snippetPermissionRepository;

    public Response<Boolean> hasAccess(String snippetId, String userId) {
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findBySnippetIdAndUserId(snippetId,
                userId);
        if (snippetPermission.isEmpty())
            return Response.withError(new Error(404, "Snippet permission not found"));
        return Response.withData(true);
    }

    public Response<String> saveRelation(String snippetId, String userId, GrantType grantType) {
        Optional<SnippetPermission> snippetPermissionOpt = snippetPermissionRepository
                .findBySnippetIdAndUserId(snippetId, userId);
        if (snippetPermissionOpt.isEmpty()) {
            try {
                SnippetPermission snippetPermission = new SnippetPermission();
                snippetPermission.setSnippetId(snippetId);
                snippetPermission.setUserId(userId);
                snippetPermission.setGrantType(grantType);
                snippetPermissionRepository.save(snippetPermission);
                return Response.withData("Relationship saved");
            } catch (Exception e) {
                return Response.withError(new Error(500, e.getMessage()));
            }
        } else {
            return Response.withError(new Error(409, "Relationship already exists"));
        }
    }

    public Response<Boolean> canEdit(String snippetId, String userId) {
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository.findBySnippetIdAndUserId(snippetId,
                userId);
        if (snippetPermission.isEmpty())
            return Response.withError(new Error(404, "Snippet permission not found"));
        boolean canEdit = snippetPermission.get().getGrantType().equals(GrantType.WRITE);
        return Response.withData(canEdit);
    }

    public Response<String> getSnippetAuthor(String snippetId, String token) {
        Optional<SnippetPermission> snippetPermission = snippetPermissionRepository
                .findBySnippetIdAndGrantType(snippetId, GrantType.WRITE);
        if (snippetPermission.isEmpty())
            return Response.withError(new Error(404, "Snippet permission not found"));
        String userId = snippetPermission.get().getUserId();
        try {
            String author = getUsernameByUserId(userId, token);
            return Response.withData(author);
        } catch (IOException e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }

    public Response<List<SnippetPermissionGrantResponse>> getSnippetGrants(String userId, String filterType) {
        FilterType filter = FilterType.valueOf(filterType);
        List<SnippetPermission> snippetPermissions = snippetPermissionRepository.findAllByUserId(userId);
        if (filter != FilterType.ALL) {
            snippetPermissions = snippetPermissions.stream().filter(
                    snippetPermission -> snippetPermission.getGrantType().equals(GrantType.valueOf(filter.name())))
                    .toList();
        }
        return Response.withData(snippetPermissions.stream()
                .map(snippetPermission -> new SnippetPermissionGrantResponse(snippetPermission.getSnippetId(),
                        snippetPermission.getUserId()))
                .toList());
    }

    public Response<List<String>> getAllSnippetsByUser(String userId) {
        List<SnippetPermission> snippetPermissions = snippetPermissionRepository.findAllByUserIdAndGrantType(userId,
                GrantType.WRITE);
        return Response.withData(snippetPermissions.stream().map(SnippetPermission::getSnippetId).toList());
    }

    public Response<String> deleteRelation(String snippetId, String userId) {
        Optional<SnippetPermission> snippetPermissionOpt = snippetPermissionRepository
                .findBySnippetIdAndUserId(snippetId, userId);
        if (snippetPermissionOpt.isEmpty())
            return Response.withError(new Error(404, "Snippet not found"));
        try {
            snippetPermissionRepository.delete(snippetPermissionOpt.get());
            return Response.withData("Relationship deleted");
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }

    public Response<String> deleteAllRelations(String snippetId) {
        List<SnippetPermission> snippetPermissions = snippetPermissionRepository.findAllBySnippetId(snippetId);
        if (snippetPermissions.isEmpty())
            return Response.withError(new Error(404, "Snippet not found"));
        try {
            snippetPermissionRepository.deleteAll(snippetPermissions);
            return Response.withData("All relationships deleted");
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
    }

    public Response<String> saveShareRelation(ShareSnippetDTO shareSnippetDTO, String userId) {
        Response<Boolean> canEdit = canEdit(shareSnippetDTO.getSnippetId(), userId);
        if (canEdit.isError())
            return Response.withError(canEdit.getError());
        if (!canEdit.getData()) {
            return Response.withError(new Error(403, "Share Access Denied"));
        }
        try {
            Response<String> response = saveRelation(shareSnippetDTO.getSnippetId(), shareSnippetDTO.getUserId(),
                    GrantType.READ);
            if (response.isError())
                return response;
        } catch (Exception e) {
            return Response.withError(new Error(500, e.getMessage()));
        }
        return Response.withData("Snippet shared");
    }
}
