package com.jhon.barbearia.config;

import com.jhon.barbearia.repository.ClienteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final ClienteRepository clienteRepository;

    public SecurityConfig(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/cadastro", "/recuperar-senha", "/api/agendamentos/horarios-disponiveis",
                        "/css/**", "/js/**", "/image/**").permitAll()
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "PROPRIETARIO")
                .requestMatchers("/gestao/**").hasAnyRole("ADMIN", "PROPRIETARIO", "BARBEIRO")
                .requestMatchers("/agendamento/**", "/meus-agendamentos/**", "/perfil/**").authenticated()
                .anyRequest().permitAll()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("senha")
                .successHandler((request, response, authentication) -> {
                    String email = authentication.getName();
                    clienteRepository.findByEmailIgnoreCase(email).ifPresent(cliente ->
                            request.getSession().setAttribute("usuarioLogado", cliente));
                    boolean gestor = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_PROPRIETARIO"));
                    response.sendRedirect(gestor ? "/admin" : "/");
                })
                .failureUrl("/login?erro=true")
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/sair")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
        );

        return http.build();
    }
}
