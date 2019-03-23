package com.tiansi.annotation.config;

import com.tiansi.annotation.security.*;
import com.tiansi.annotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter; // JWT 拦截器
    @Autowired
    private UserService userService;

    // 装载BCrypt密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TiansiAuthenticationSuccessHandler tiansiAuthenticationSuccessHandler() {
        return new TiansiAuthenticationSuccessHandler();
    }

    @Bean
    public TiansiAuthenticationEntryPoint tiansiAuthenticationEntryPoint() {
        return new TiansiAuthenticationEntryPoint();
    }

    @Bean
    public TiansiAuthenticationFailureHandler tiansiAuthenticationFailureHandler() {
        return new TiansiAuthenticationFailureHandler();
    }

    @Bean
    public TiansiLogoutSuccessHandler tiansiLogoutSuccessHandler() {
        return new TiansiLogoutSuccessHandler();
    }

    @Bean
    public TiansiAccessDeniedHandler tiansiAccessDeniedHandler() {
        return new TiansiAccessDeniedHandler();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 加入自定义的安全认证
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用 JWT，关闭token
                .and()
                .httpBasic()
                // 未经过认证的用户访问受保护的资源
                .authenticationEntryPoint(tiansiAuthenticationEntryPoint())

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 任何用户都可以访问URL以"/resources/", equals "/signup", 或者 "/about"开头的URL。
                .antMatchers("/login", "/swagger-ui.html", "/webjars/**", "/swagger-resources", "/v2/**").permitAll()
                //以 "/admin/" 开头的URL只能由拥有 "ROLE_ADMIN"角色的用户访问。请注意我们使用 hasRole 方法
                // .antMatchers("/admin").hasRole("ADMIN")
                // .antMatchers("/uuu").access("hasRole('USER') or hasRole('ADMIN') ")
                .antMatchers("/type/add", "/type/delete", "/type/update", "/user/add","/user/reset").hasRole("ADMIN")
                .anyRequest().access("hasRole('USER') or hasRole('ADMIN') ")
                .and()

                .formLogin()
                // .loginPage("/login")
                //.loginProcessingUrl("/login").defaultSuccessUrl("/index", true).failureUrl("/login?error")
                .successHandler(tiansiAuthenticationSuccessHandler())
                // 认证失败
                .failureHandler(tiansiAuthenticationFailureHandler())
                .permitAll()

                .and()
                .logout()
                .logoutSuccessHandler(tiansiLogoutSuccessHandler())
                .permitAll();
        // 记住我
        http.rememberMe().rememberMeParameter("remember-me")
                .userDetailsService(userService).tokenValiditySeconds(300);


        http.exceptionHandling()
                // 已经认证的用户访问自己没有权限的资源处理
                .accessDeniedHandler(tiansiAccessDeniedHandler())
                .and().addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
