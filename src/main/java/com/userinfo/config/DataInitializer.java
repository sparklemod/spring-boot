package com.userinfo.config;

import com.userinfo.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.InitializingBean;

@Component
public class DataInitializer implements InitializingBean {

    @Autowired
    private DataInitializationService dataInitializationService;

    @Override
    public void afterPropertiesSet() {
        dataInitializationService.initializeData();
    }
}
