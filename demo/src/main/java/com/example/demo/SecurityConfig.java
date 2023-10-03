package com.example.demo;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin(login -> login
	    	.loginPage("/login")
	        .defaultSuccessUrl("/") // ログイン成功後のリダイレクトURL
	        .permitAll()
        ).logout(logout -> logout
            .logoutSuccessUrl("/").permitAll()
        ).authorizeHttpRequests(authz -> authz
	        // 静的リソースへのアクセスを全てのユーザーに許可
	        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
	        // 以下のURLへのアクセスを全てのユーザーに許可します
	        /*
	         * / -> 動的に作成したページであり、そのコンテンツの一部は認証されていないユーザーにも表示される
	         * /error -> エラーを表示するためのSpring Bootエンドポイント
	         * /webjars/** -> 認証されているかどうかにかかわらず、すべての訪問者に対してJavaScriptを実行する
	         */
	        .requestMatchers("/", "/error", "/webjars/**").permitAll()
	        // 上記のルールにマッチしない任意のリクエストは、認証されたユーザーのみがアクセス可能
	        .anyRequest().authenticated()
	        // ).exceptionHandling(e -> e
	        // Ajax を介してバックエンドとインターフェースするため
	        // ログインページにリダイレクトするデフォルトの動作ではなく、401で応答するようにエンドポイントを構成
	        // .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))       
        ).csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        ).oauth2Login(o -> o
                .failureHandler((request, response, exception) -> {
                    request.getSession().setAttribute("error.message", exception.getMessage());
                    response.sendRedirect("login/error");
                })
		); // OAuth2ログインを有効にする
        return http.build();
    }
}