package io.github.lizhifuabc.tree.controller.closure;

import io.github.lizhifuabc.tree.common.Result;
import io.github.lizhifuabc.tree.domain.closure.ClosureNode;
import io.github.lizhifuabc.tree.service.closure.ClosureNodeService;
import io.github.lizhifuabc.tree.service.closure.ClosureNodeTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 闭包表模型控制器
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@RestController
@RequestMapping("/api/tree/closure")
public class ClosureNodeController {

    private final ClosureNodeService closureNodeService;

    public ClosureNodeController(ClosureNodeService closureNodeService) {
        this.closureNodeService = closureNodeService;
    }

    /**
     * 创建节点
     *
     * @param node 节点
     * @param parentId 父节点ID，如果为null则创建根节点
     * @return 创建后的节点
     */
    @PostMapping("/node")
    public Result<ClosureNode> createNode(@RequestBody ClosureNode node, @RequestParam(required = false) Long parentId) {
        return Result.success(closureNodeService.createNode(node, parentId));
    }

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    @PutMapping("/node")
    public Result<ClosureNode> updateNode(@RequestBody ClosureNode node) {
        return Result.success(closureNodeService.updateNode(node));
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    @DeleteMapping("/node/{id}")
    public Result<Boolean> deleteNode(@PathVariable Long id) {
        return Result.success(closureNodeService.deleteNode(id));
    }

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @GetMapping("/node/{id}")
    public Result<ClosureNode> getNode(@PathVariable Long id) {
        return Result.success(closureNodeService.getNode(id));
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    @GetMapping("/nodes")
    public Result<List<ClosureNode>> getAllNodes() {
        return Result.success(closureNodeService.getAllNodes());
    }

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @GetMapping("/nodes/children/{parentId}")
    public Result<List<ClosureNode>> getChildNodes(@PathVariable Long parentId) {
        return Result.success(closureNodeService.getChildNodes(parentId));
    }

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    @GetMapping("/nodes/descendants/{id}")
    public Result<List<ClosureNode>> getDescendants(@PathVariable Long id) {
        return Result.success(closureNodeService.getDescendants(id));
    }

    /**
     * 获取所有祖先节点
     *
     * @param id 节点ID
     * @return 祖先节点列表
     */
    @GetMapping("/nodes/ancestors/{id}")
    public Result<List<ClosureNode>> getAncestors(@PathVariable Long id) {
        return Result.success(closureNodeService.getAncestors(id));
    }

    /**
     * 构建树结构
     *
     * @return 树结构
     */
    @GetMapping("/tree")
    public Result<List<ClosureNodeTree>> buildTree() {
        return Result.success(closureNodeService.buildTree());
    }
}