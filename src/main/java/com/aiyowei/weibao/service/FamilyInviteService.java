package com.aiyowei.weibao.service;

import com.aiyowei.weibao.entity.FamilyMember;
import com.aiyowei.weibao.entity.InviteCode;
import com.aiyowei.weibao.mapper.FamilyMemberMapper;
import com.aiyowei.weibao.mapper.InviteCodeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FamilyInviteService {

    private static final ZoneOffset ZONE_OFFSET = ZoneOffset.ofHours(8);
    private final InviteCodeMapper inviteCodeMapper;
    private final FamilyMemberMapper familyMemberMapper;
    private final MockDataService mockDataService;

    public Map<String, Object> createInvite(String type) {
        InviteCode invite = new InviteCode();
        invite.setFamilyId(mockDataService.getCurrentFamilyId());
        invite.setCreatorId(mockDataService.getCurrentUserId());
        invite.setType(type);
        invite.setCode(randomCode());
        invite.setExpireAt(LocalDateTime.now().plusHours(2));
        invite.setStatus(1);
        inviteCodeMapper.insert(invite);
        Map<String, Object> payload = new HashMap<>();
        payload.put("code", invite.getCode());
        payload.put("type", invite.getType());
        payload.put("qrUrl", "https://fake.qr/" + invite.getCode());
        payload.put("expireAt", invite.getExpireAt().atOffset(ZONE_OFFSET));
        return payload;
    }

    @Transactional
    public Map<String, Object> joinFamily(String code) {
        InviteCode invite = inviteCodeMapper.selectOne(
            new LambdaQueryWrapper<InviteCode>().eq(InviteCode::getCode, code).last("limit 1")
        );
        if (invite == null || invite.getStatus() == null || invite.getStatus() == 0
            || (invite.getExpireAt() != null && invite.getExpireAt().isBefore(LocalDateTime.now()))) {
            throw new IllegalArgumentException("邀请码无效或已过期");
        }
        Long userId = mockDataService.getCurrentUserId();
        FamilyMember existing = familyMemberMapper.selectOne(new LambdaQueryWrapper<FamilyMember>()
            .eq(FamilyMember::getFamilyId, invite.getFamilyId())
            .eq(FamilyMember::getUserId, userId));
        if (existing == null) {
            FamilyMember member = new FamilyMember();
            member.setFamilyId(invite.getFamilyId());
            member.setUserId(userId);
            member.setRole("partner");
            familyMemberMapper.insert(member);
        }
        invite.setStatus(0);
        inviteCodeMapper.updateById(invite);
        Map<String, Object> payload = new HashMap<>();
        payload.put("familyId", invite.getFamilyId());
        payload.put("status", "joined");
        payload.put("code", code);
        return payload;
    }

    @Transactional
    public void unbindCurrentUser() {
        Long userId = mockDataService.getCurrentUserId();
        familyMemberMapper.delete(new LambdaQueryWrapper<FamilyMember>().eq(FamilyMember::getUserId, userId));
    }

    private String randomCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
    }
}


