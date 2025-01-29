package main.AuthTemplate.user.service;

import main.AuthTemplate.apiPayload.code.status.ErrorStatus;
import main.AuthTemplate.apiPayload.exception.GeneralException;
import main.AuthTemplate.user.dto.CustomUserDetails;
import main.AuthTemplate.user.entity.User;
import main.AuthTemplate.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static main.AuthTemplate.apiPayload.code.status.ErrorStatus.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userdata = userRepository.findByUsername(username);

        if (userdata == null)
        {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new CustomUserDetails(userdata);
    }
}
