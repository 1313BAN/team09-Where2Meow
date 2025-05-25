package com.ssafy.where2meow.common.util;

import com.ssafy.where2meow.exception.EntityNotFoundException;
import com.ssafy.where2meow.user.entity.User;
import com.ssafy.where2meow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UuidUserUtil {

    private final UserRepository userRepository;

    public Integer getOptionalUserId(UUID uuid) {
        if (uuid == null) {
            return null;
        }
        User user = userRepository.findByUuidAndIsActiveTrue(uuid).orElse(null);
        return user != null ? user.getUserId() : null;
    }

    public Integer getRequiredUserId(UUID uuid) {
        if (uuid == null) {
            throw new EntityNotFoundException("User", "uuid", "null");
        }
        User user = userRepository.findByUuidAndIsActiveTrue(uuid)
            .orElseThrow(() -> new EntityNotFoundException("User", "uuid", uuid));
        return user.getUserId();
    }

    public UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !auth.getPrincipal().equals("anonymousUser")) {
            try {
                String uuidString = auth.getName();
                return UUID.fromString(uuidString);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

}