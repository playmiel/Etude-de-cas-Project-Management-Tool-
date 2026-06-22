package com.codesolutions.pmt.web;

import com.codesolutions.pmt.dto.AssignRoleRequest;
import com.codesolutions.pmt.dto.InviteMemberRequest;
import com.codesolutions.pmt.dto.MemberDto;
import com.codesolutions.pmt.service.MembershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Gestion des membres d'un projet et de leurs roles. */
@RestController
@RequestMapping("/api/projects/{projectId}/members")
@Tag(name = "Membres")
public class MemberController {

    private final MembershipService membershipService;

    public MemberController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @Operation(summary = "Inviter un membre par e-mail et lui attribuer un role (admin)")
    @PostMapping
    public ResponseEntity<MemberDto> invite(@RequestHeader("X-User-Id") Long userId,
                                            @PathVariable Long projectId,
                                            @Valid @RequestBody InviteMemberRequest request) {
        MemberDto member = membershipService.invite(projectId, userId, request.email(), request.role());
        return ResponseEntity.status(HttpStatus.CREATED).body(member);
    }

    @Operation(summary = "Modifier le role d'un membre (admin)")
    @PutMapping("/{memberId}/role")
    public MemberDto changeRole(@RequestHeader("X-User-Id") Long userId,
                                @PathVariable Long projectId,
                                @PathVariable Long memberId,
                                @Valid @RequestBody AssignRoleRequest request) {
        return membershipService.changeRole(projectId, userId, memberId, request.role());
    }

    @Operation(summary = "Lister les membres du projet")
    @GetMapping
    public List<MemberDto> list(@RequestHeader("X-User-Id") Long userId, @PathVariable Long projectId) {
        return membershipService.listMembers(projectId, userId);
    }
}
