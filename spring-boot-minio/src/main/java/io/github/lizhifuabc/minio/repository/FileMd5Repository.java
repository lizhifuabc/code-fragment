package io.github.lizhifuabc.minio.repository;

import io.github.lizhifuabc.minio.entity.FileMd5Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 文件MD5映射Repository
 * 处理文件MD5与路径映射的数据库操作
 *
 * @author lizhifu
 * @since 2024/2/24
 */
@Repository
public interface FileMd5Repository extends JpaRepository<FileMd5Entity, Long> {
    /**
     * 根据MD5值查找文件路径
     * @param md5 文件的MD5值
     * @return 文件路径实体
     */
    FileMd5Entity findByMd5(String md5);
}