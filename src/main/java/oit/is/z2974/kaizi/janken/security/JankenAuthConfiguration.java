package oit.is.z2974.kaizi.janken.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity

public class JankenAuthConfiguration {
  /**
   * 認可処理に関する設定（認証されたユーザがどこにアクセスできるか）
   *
   * @param http
   * @return
   * @throws Exception
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.formLogin(login -> login
        .permitAll())
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")) // ログアウト後に / にリダイレクト
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/janken/**").authenticated() // /sample3/以下は認証済みであること
            .anyRequest().permitAll()) // 上記以外は全員アクセス可能
        .csrf(csrf -> csrf
            .ignoringRequestMatchers("/h2-console/*", "/janken*/**"))
        .headers(headers -> headers
            .frameOptions(frameOptions -> frameOptions
                .sameOrigin())); // sample2用にCSRF対策を無効化
    return http.build();
  }

  /**
   * 認証処理に関する設定（誰がどのようなロールでログインできるか）
   *
   * @return
   */
  @Bean
  public InMemoryUserDetailsManager userDetailsService() {

    // ユーザ名，パスワード，ロールを指定してbuildする
    // このときパスワードはBCryptでハッシュ化されているため，{bcrypt}とつける
    // ハッシュ化せずに平文でパスワードを指定する場合は{noop}をつける
    // user1/p@ss,user2/p@ss,admin/p@ss

    UserDetails user1 = User.withUsername("user1")
        .password("{bcrypt}$2y$05$cGDRulsz2Yt1TdnUfAEvwe/nw8.1xV4sOmz8ryXkQBwJyFdbq4C86").roles("USER").build();
    UserDetails user2 = User.withUsername("user2")
        .password("{bcrypt}$2y$05$uorCGpa9i/F5.btgqb7x9O.oFwci45/LWXqbMavVasK60gcESELjC").roles("USER").build();
    UserDetails ほんだ = User.withUsername("ほんだ")
        .password("{bcrypt}$2y$05$PbEztNrlA0IU2HbEHsC7IeM3RP7K8tVF2c40aF58kX.sT2CiMbbLW").roles("USER").build();
    UserDetails いがき = User.withUsername("いがき")
        .password("{bcrypt}$2y$05$vZG2dLwEyWuqWhlwbr/kGeEG3nglDC0Ap6epi18A1YwVrK/vfqB3m").roles("USER").build();
    UserDetails admin = User.withUsername("admin")
        .password("{bcrypt}$2y$05$yrvUWhG5jMEB6btvpufFH.tC6jvvVhoo40CLey0cgfPFzl57WBlTS").roles("ADMIN").build();

    // 生成したユーザをImMemoryUserDetailsManagerに渡す（いくつでも良い）
    return new InMemoryUserDetailsManager(user1, user2, ほんだ, いがき, admin);
  }
}
