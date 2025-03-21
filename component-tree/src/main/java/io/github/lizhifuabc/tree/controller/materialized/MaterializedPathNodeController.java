package io.github.lizhifuabc.tree.controller.materialized;

import io.github.lizhifuabc.tree.common.Result;
import io.github.lizhifuabc.tree.domain.materialized.MaterializedPathNode;
import io.github.lizhifuabc.tree.service.materialized.MaterializedPathNodeService;
import io.github.lizhifuabc.tree.service.materialized.MaterializedPathNodeTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 物化路径模型控制器
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@RestController
@RequestMapping("/api/tree/materialized")
public class MaterializedPathNodeController {

    private final MaterializedPathNodeService materializedPathNodeService;

    public MaterializedPathNodeController(MaterializedPathNodeService materializedPathNodeService) {
        this.materializedPathNodeService = materializedPathNodeService;
    }

    /**
     * 创建节点
     *
     * @param node 节点
     * @param parentId 父节点ID，如果为null则创建根节点
     * @return 创建后的节点
     */
    @PostMapping("/node")
    public Result<MaterializedPathNode> createNode(@RequestBody MaterializedPathNode node, @RequestParam(required = false) Long parentId) {
        return Result.success(materializedPathNodeService.createNode(node, parentId));
    }

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    @PutMapping("/node")
    public Result<MaterializedPathNode> updateNode(@RequestBody MaterializedPathNode node) {
        return Result.success(materializedPathNodeService.updateNode(node));
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    @DeleteMapping("/node/{id}")
    public Result<Boolean> deleteNode(@PathVariable Long id) {
        return Result.success(materializedPathNodeService.deleteNode(id));
    }

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @GetMapping("/node/{id}")
    public Result<MaterializedPathNode> getNode(@PathVariable Long id) {
        return Result.success(materializedPathNodeService.getNode(id));
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    @GetMapping("/nodes")
    public Result<List<MaterializedPathNode>> getAllNodes() {
        return Result.success(materializedPathNodeService.getAllNodes());
    }

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @GetMapping("/nodes/children/{parentId}")
    public Result<List<MaterializedPathNode>> getChildNodes(@PathVariable Long parentId) {
        return Result.success(materializedPathNodeService.getChildNodes(parentId));
    }

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    @GetMapping("/nodes/descendants/{id}")
    public Result<List<MaterializedPathNode>> getDescendants(@PathVariable Long id) {
        return Result.success(materializedPathNodeService.getDescendants(id));
    }

    /**
     * 获取所有祖先节点
     *
     * @param id 节点ID
     * @return 祖先节点列表
     */
    @GetMapping("/nodes/ancestors/{id}")
    public Result<List<MaterializedPathNode>> getAncestors(@PathVariable Long id) {
        return Result.success(materializedPathNodeService.getAncestors(id));
    }

    /**
     * 构建树结构
     *
     * @return 树结构
     */
    @GetMapping("/tree")
    public Result<List<MaterializedPathNodeTree>> buildTree() {
        return Result.success(materializedPathNodeService.buildTree());
    }
}