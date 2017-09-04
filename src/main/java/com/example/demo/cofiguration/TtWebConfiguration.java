package com.example.demo.cofiguration;

import com.example.demo.interceptor.LoginRequiredInterceptor;
import com.example.demo.interceptor.PasswportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Crys at 2017/8/20
 * 网页设置
 */
@Component
public class TtWebConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    PasswportInterceptor passwportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器
        registry.addInterceptor(passwportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
        super.addInterceptors(registry);
    }
}
