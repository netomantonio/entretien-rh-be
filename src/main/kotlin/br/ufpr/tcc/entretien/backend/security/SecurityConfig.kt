package br.ufpr.tcc.entretien.backend.security


import br.ufpr.tcc.entretien.backend.model.ERole
import br.ufpr.tcc.entretien.backend.model.Role
import br.ufpr.tcc.entretien.backend.model.User
import br.ufpr.tcc.entretien.backend.security.jwt.AuthEntryPointJwt
import br.ufpr.tcc.entretien.backend.security.jwt.AuthTokenFilter
import br.ufpr.tcc.entretien.backend.service.AuthService
import br.ufpr.tcc.entretien.backend.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.annotation.PostConstruct





@Configuration
//@EnableWebSecurity
@EnableGlobalMethodSecurity(
    // securedEnabled = true,
    // jsr250Enabled = true,
    prePostEnabled = true
)
class SecurityConfig {

    @Autowired
    private lateinit var userDetailsService: UserDetailsServiceImpl

    @Autowired
    private lateinit var unauthorizedHandler: AuthEntryPointJwt

//    @Autowired
//    private lateinit var authService: AuthService

    @Bean
    fun authenticationJwtTokenFilter(): AuthTokenFilter {
        return AuthTokenFilter()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {

        val authProvider = DaoAuthenticationProvider()

        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authConfig: AuthenticationConfiguration): AuthenticationManager {
        return authConfig.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun config(http: HttpSecurity): SecurityFilterChain {
        http
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http
            .authorizeHttpRequests()
//            .requestMatchers(RegexRequestMatcher("/api/auth", "POST")).permitAll()
            .anyRequest().permitAll()
//            .anyRequest().authenticated()
        http
            .authenticationProvider(authenticationProvider())
//        http.formLogin().disable()
//        http.httpBasic().disable()
        http
            .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
        http
//            .httpBasic().authenticationEntryPoint(authenticationEntryPoint).and().
            .cors().and()
            .csrf().disable()

        return http.build()
    }

//    @PostConstruct
//    fun addFirstUser() {
//        val user = User(
//                username = "admin",
//                email = "email@gmai.com",
//                password = passwordEncoder()!!.encode("password"),
//                roles = setOf(Role(ERole.ROLE_ADMIN)))
//        if(!authService.userAlreadyExists(user)){
//            authService.registerUser(user)
//        }
//    }
}