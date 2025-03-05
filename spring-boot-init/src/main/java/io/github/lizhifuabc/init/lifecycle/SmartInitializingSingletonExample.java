package io.github.lizhifuabc.init.lifecycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

/**
 * SmartInitializingSingleton接口实现示例
 * 在所有单例bean初始化完成后执行回调
 * 执行顺序：构造方法 -> 依赖注入 -> 初始化方法 -> afterSingletonsInstantiated
 *
 * @author lizhifu
 * @since 2024/3/12
 */
@Slf4j
@Component
public class SmartInitializingSingletonExample implements SmartInitializingSingleton {
    public SmartInitializingSingletonExample() {
        log.info("[4.1][SmartInitializingSingletonExample] 构造方法执行");
    }

    @Override
    public void afterSingletonsInstantiated() {
        log.info("[4.2][SmartInitializingSingletonExample] afterSingletonsInstantiated方法执行，所有单例bean已完成初始化");
        // 在这里可以安全地访问其他已初始化完成的单例bean
        log.info("[4.3][SmartInitializingSingletonExample] 执行完成");
    }
}