package com.ecommerce.project.util;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthUtil {
    @Autowired
    UserRepository userRepository;

    public String loggedInEmail() {
        User user = loggedInUser();
        return user.getEmail();
    }

    public Long loggedInUserId() {
        User user = loggedInUser();
        return user.getId();
    }

    public User loggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUserName(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + auth.getName()));
        return user;
    }
}
