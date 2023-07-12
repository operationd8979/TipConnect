package Tip.Connect.service;

import Tip.Connect.model.AppUser;
import Tip.Connect.model.AuthenticationReponse;
import Tip.Connect.model.ConfirmationToken;
import Tip.Connect.model.LoginRequest;
import Tip.Connect.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final String USER_NOT_FOUND_MSG = "user with email %s not found";

    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,email)));
    }

    @Transactional
    public String signUp(AppUser appUser) {
        try{
            boolean userExists = appUserRepository.findByEmail(appUser.getEmail()).isPresent();
            if(userExists){
                throw new IllegalStateException("Email already taken");
            }
            String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
            appUser.setPassword(encodedPassword);
            appUserRepository.save(appUser);
            String token = UUID.randomUUID().toString();
            ConfirmationToken confirmationToken = new ConfirmationToken(
                    token,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    appUser
            );
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            return token;
        }catch (Exception ex){
            return "Email already taken";
        }
    }

    public AuthenticationReponse login(LoginRequest request) {
        try{
            var userDetails = appUserRepository.findByEmail(request.email()).orElseThrow(()->new IllegalStateException("User not found!"));
            final String accessToken = jwtService.generateToken(userDetails);
            final String refreshToken = jwtService.generateRefreshToken(userDetails);
            return AuthenticationReponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        }catch (IllegalStateException ex){
            return null;
        }

    }

    public int enableAppUser(String email) {
        return appUserRepository.enableAppUser(email);
    }

    public AuthenticationReponse refreshToken(String refreshToken) {
        try{
            final String username = jwtService.extractUsername(refreshToken);
            if(username!= null){
                UserDetails userDetails = loadUserByUsername(username);
                if(jwtService.isTokenValid(refreshToken,userDetails)){
                    return AuthenticationReponse.builder()
                            .accessToken(jwtService.generateToken(userDetails)).build();
                }
            }
            return null;
        }catch (Exception ex){
            return AuthenticationReponse.builder()
                    .accessToken(ex.getMessage()).build();
        }
    }
}
