package cn.iocoder.linhai.module.infra.framework.web.config;

import cn.iocoder.linhai.framework.swagger.config.LinhaiSwaggerAutoConfiguration;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * infra 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class InfraWebConfiguration {

    /**
     * infra 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi infraGroupedOpenApi() {
        return LinhaiSwaggerAutoConfiguration.buildGroupedOpenApi("infra");
    }

}
