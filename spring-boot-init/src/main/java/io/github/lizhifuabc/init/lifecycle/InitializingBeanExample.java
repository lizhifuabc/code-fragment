package io.github.lizhifuabc.init.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * InitializingBean接口实现类
 * 在所有属性设置完成后执行初始化
 * 执行顺序：构造方法 -> 依赖注入 -> afterPropertiesSet方法
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
public class InitializingBeanExample implements InitializingBean {
    public InitializingBeanExample() {
        log.info("[1.1][InitializingBeanExample] 构造方法执行");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("[1.2][InitializingBeanExample] afterPropertiesSet方法执行，开始初始化...");
        // 在这里执行初始化逻辑
        log.info("[1.3][InitializingBeanExample] 初始化完成");
    }
}