package com.jilian.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/** @Author: zhuanggangqing
 *  @Description: 处理跨域请求
 *  @Date: Create in 9:51 上午 2020/2/5 */
@Configuration
public class CorsConfig {

//  private static String[] originsVal = new String[] {"localhost:9529", "localhost:9528"};
  private static String[] originsVal = new String[] {"120.77.183.132:9529", "120.77.183.132:9528"};

  private CorsConfiguration buildConfig() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    addAllowedOrigins(corsConfiguration);
        corsConfiguration.addAllowedOrigin("*"); // 1允许任何域名使用
    corsConfiguration.addAllowedHeader("*"); // 2允许任何头
    corsConfiguration.addAllowedMethod("*"); // 3允许任何方法（post、get等）
    return corsConfiguration;
  }

  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", buildConfig()); // 4
    return new CorsFilter(source);
  }

  private void addAllowedOrigins(CorsConfiguration corsConfiguration) {
    for (String origin : originsVal) {
      corsConfiguration.addAllowedOrigin("http://" + origin);
      corsConfiguration.addAllowedOrigin("https://" + origin);
    }
  }
}
