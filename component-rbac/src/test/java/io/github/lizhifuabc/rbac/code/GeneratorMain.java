package io.github.lizhifuabc.rbac.code;

import cn.xbatis.generator.core.FastGenerator;
import cn.xbatis.generator.core.config.GeneratorConfig;
import io.github.lizhifuabc.rbac.RbacApplication;

import java.io.File;

/**
 * 代码生成
 *
 * @author lizhifu
 * @since 2025/3/3
 */
public class GeneratorMain {
    public static void main(String[] args) {
        String author = "lizhifu";

        String baseFilePath = "/Volumes/anan/workspace/code-fragment/component-rbac/";
        String javaPath = "src/main/java";
        String resourcePath = "src/main/resources";

        String basePackage = "io.github.lizhifuabc.rbac.system";
        String entityPackage = "domain.entity";

        // 根据数据库链接生成
        new FastGenerator(new GeneratorConfig(
                "jdbc:mysql://192.168.10.202:3306/component_rbac?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false",
                "hxhr",
                "hxhr@1234")
                .author(author)
                .baseFilePath(baseFilePath)
                .javaPath(javaPath)
                .resourcePath(resourcePath)
                .basePackage(basePackage)
                .tableConfig(config -> {
                    config.includeTable("t_sys_user");
                    config.tablePrefixes("t_");
                })
                .columnConfig(columnConfig -> {
                    columnConfig.versionColumn("version");
                    columnConfig.tenantIdColumn("tenant_id");
                    columnConfig.logicDeleteColumn("deleted_flag");
                    // 禁止更新的列
                    // 禁止Select的列
                    // 可动态转换数据库的默认值（由静态值转成动态值）
                })
                .entityConfig(entityConfig -> {
                    entityConfig.packageName(entityPackage);
                    entityConfig.logicDeleteCode("@LogicDelete(beforeValue=\"0\",afterValue=\"1\",deleteTimeField=\"deleted_time\")");
                })
                .mapperConfig(mapperConfig -> {
                    mapperConfig.packageName("mapper");
                })
                .mapperXmlConfig(mapperXmlConfig -> {
                    mapperXmlConfig.enable(true);
                })
                .daoConfig(daoConfig->{
                    daoConfig.enable(true);
                })
                .daoImplConfig(daoImplConfig->{
                    daoImplConfig.enable(true);
                })
                .serviceConfig(serviceConfig->{
                    serviceConfig.enable(true);
                })
                .serviceImplConfig(serviceImplConfig->{
                    serviceImplConfig.injectDao(true);
                })
                .actionConfig(actionConfig->{
                    actionConfig.enable(true);
                    actionConfig.packageName("controller");
                    actionConfig.suffix("Controller");
                })
        ).create();
    }
}
