package com.codesolutions.pmt.dto;

import com.codesolutions.pmt.domain.Role;

public record MemberDto(Long memberId, Long userId, String username, String email, Role role) {
}
