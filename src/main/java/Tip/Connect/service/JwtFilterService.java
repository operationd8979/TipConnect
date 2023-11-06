package Tip.Connect.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JwtFilterService extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AppUserService appUserService;

    private final RequestAttributeSecurityContextRepository repository = new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().contains("/api/v1/auth")||request.getServletPath().contains("/api/v1/registration")||request.getServletPath().contains("/ws")){
            filterChain.doFilter(request,response);
            return;
        }

        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(Cookie cookie : cookies){
                if("access_token".equals(cookie.getName())){
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if(token==null){
            filterChain.doFilter(request,response);
            return;
        }

        String email = jwtService.extractUsername(token);
        if(email!=null && SecurityContextHolder.getContext().getAuthentication()==null){
            UserDetails userDetails = appUserService.loadUserByUsername(email);
            if(userDetails == null){
                filterChain.doFilter(request,response);
                return;
            }
            if(jwtService.isTokenValid(token,userDetails)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //SecurityContextHolder.getContext().setAuthentication(authToken);

                var context = SecurityContextHolder.getContext();
                context.setAuthentication(authToken);

                this.repository.saveContext(context,request,response);

            }
        }
        filterChain.doFilter(request,response);

        SecurityContextHolder.clearContext();
        //this.repository.saveContext(null,request,response);
    }
}
