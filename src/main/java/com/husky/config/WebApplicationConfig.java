package com.husky.config;

import com.husky.common.JacksonObjectMapper;
import com.husky.filter.LoginCheckFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: 周圣杰
 * Date: 2022/9/30
 * Time: 14:18
 */
@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {

    // 登录页面过滤器
    @Bean
    public FilterRegistrationBean<LoginCheckFilter> filterRegistrationBean() {
        FilterRegistrationBean<LoginCheckFilter> bean = new FilterRegistrationBean<>();
        bean.addUrlPatterns("/*");
        bean.setFilter(new LoginCheckFilter());
        return bean;
    }

    /*
     * 返回的R对象默认使用了消息转换器*/

    // 扩展mvc框架的消息转换器
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置自定义的对象转换器，底层使用Jackson将将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
