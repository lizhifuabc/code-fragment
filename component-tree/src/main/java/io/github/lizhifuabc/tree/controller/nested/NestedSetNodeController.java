package io.github.lizhifuabc.tree.controller.nested;

import io.github.lizhifuabc.tree.common.Result;
import io.github.lizhifuabc.tree.domain.nested.NestedSetNode;
import io.github.lizhifuabc.tree.service.nested.NestedSetNodeService;
import io.github.lizhifuabc.tree.service.nested.NestedSetNodeTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 嵌套集模型控制器
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@RestController
@RequestMapping("/api/tree/nested")
public class NestedSetNodeController {

    private final NestedSetNodeService nestedSetNodeService;

    public NestedSetNodeController(NestedSetNodeService nestedSetNodeService) {
        this.nestedSetNodeService = nestedSetNodeService;
    }

    /**
     * 创建根节点
     *
     * @param node 节点
     * @return 创建后的节点
     */
    @PostMapping("/node/root")
    public Result<NestedSetNode> createRootNode(@RequestBody NestedSetNode node) {
        return Result.success(nestedSetNodeService.createRootNode(node));
    }

    /**
     * 创建子节点
     *
     * @param node 节点
     * @param parentId 父节点ID
     * @return 创建后的节点
     */
    @PostMapping("/node/child/{parentId}")
    public Result<NestedSetNode> createChildNode(@RequestBody NestedSetNode node, @PathVariable Long parentId) {
        return Result.success(nestedSetNodeService.createChildNode(node, parentId));
    }

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    @PutMapping("/node")
    public Result<NestedSetNode> updateNode(@RequestBody NestedSetNode node) {
        return Result.success(nestedSetNodeService.updateNode(node));
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    @DeleteMapping("/node/{id}")
    public Result<Boolean> deleteNode(@PathVariable Long id) {
        return Result.success(nestedSetNodeService.deleteNode(id));
    }

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @GetMapping("/node/{id}")
    public Result<NestedSetNode> getNode(@PathVariable Long id) {
        return Result.success(nestedSetNodeService.getNode(id));
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    @GetMapping("/nodes")
    public Result<List<NestedSetNode>> getAllNodes() {
        return Result.success(nestedSetNodeService.getAllNodes());
    }

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @GetMapping("/nodes/children/{parentId}")
    public Result<List<NestedSetNode>> getChildNodes(@PathVariable Long parentId) {
        return Result.success(nestedSetNodeService.getChildNodes(parentId));
    }

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    @GetMapping("/nodes/descendants/{id}")
    public Result<List<NestedSetNode>> getDescendants(@PathVariable Long id) {
        return Result.success(nestedSetNodeService.getDescendants(id));
    }

    /**
     * 获取所有祖先节点
     *
     * @param id 节点ID
     * @return 祖先节点列表
     */
    @GetMapping("/nodes/ancestors/{id}")
    public Result<List<NestedSetNode>> getAncestors(@PathVariable Long id) {
        return Result.success(nestedSetNodeService.getAncestors(id));
    }

    /**
     * 构建树结构
     *
     * @return 树结构
     */
    @GetMapping("/tree")
    public Result<List<NestedSetNodeTree>> buildTree() {
        return Result.success(nestedSetNodeService.buildTree());
    }
}