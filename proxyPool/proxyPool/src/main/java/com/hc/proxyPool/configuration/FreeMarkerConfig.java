package com.hc.proxyPool.configuration;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

/**
 * 主要是配置ftl模板的默认时间格式  
 * @author yinwenjie
 *
 */
@Configuration
public class FreeMarkerConfig {
	@Autowired
    protected freemarker.template.Configuration configuration;  
    @Autowired  
    protected FreeMarkerViewResolver resolver;  
    @Autowired  
    protected InternalResourceViewResolver springResolver;
    
    @PostConstruct
    public void  setSharedVariable(){  
    	configuration.setDateFormat("yyyy/MM/dd");  
        configuration.setDateTimeFormat("yyyy-MM-dd HH:mm:ss");  
    }
}
