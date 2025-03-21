package io.github.lizhifuabc.tree.controller.path;

import io.github.lizhifuabc.tree.common.Result;
import io.github.lizhifuabc.tree.domain.path.PathEnumerationNode;
import io.github.lizhifuabc.tree.service.path.PathEnumerationNodeService;
import io.github.lizhifuabc.tree.service.path.PathEnumerationNodeTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路径枚举模型控制器
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@RestController
@RequestMapping("/api/tree/path")
public class PathEnumerationNodeController {

    private final PathEnumerationNodeService pathEnumerationNodeService;

    public PathEnumerationNodeController(PathEnumerationNodeService pathEnumerationNodeService) {
        this.pathEnumerationNodeService = pathEnumerationNodeService;
    }

    /**
     * 创建节点
     *
     * @param node 节点
     * @param parentId 父节点ID，如果为null则创建根节点
     * @return 创建后的节点
     */
    @PostMapping("/node")
    public Result<PathEnumerationNode> createNode(@RequestBody PathEnumerationNode node, @RequestParam(required = false) Long parentId) {
        return Result.success(pathEnumerationNodeService.createNode(node, parentId));
    }

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    @PutMapping("/node")
    public Result<PathEnumerationNode> updateNode(@RequestBody PathEnumerationNode node) {
        return Result.success(pathEnumerationNodeService.updateNode(node));
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    @DeleteMapping("/node/{id}")
    public Result<Boolean> deleteNode(@PathVariable Long id) {
        return Result.success(pathEnumerationNodeService.deleteNode(id));
    }

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @GetMapping("/node/{id}")
    public Result<PathEnumerationNode> getNode(@PathVariable Long id) {
        return Result.success(pathEnumerationNodeService.getNode(id));
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    @GetMapping("/nodes")
    public Result<List<PathEnumerationNode>> getAllNodes() {
        return Result.success(pathEnumerationNodeService.getAllNodes());
    }

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @GetMapping("/nodes/children/{parentId}")
    public Result<List<PathEnumerationNode>> getChildNodes(@PathVariable Long parentId) {
        return Result.success(pathEnumerationNodeService.getChildNodes(parentId));
    }

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    @GetMapping("/nodes/descendants/{id}")
    public Result<List<PathEnumerationNode>> getDescendants(@PathVariable Long id) {
        return Result.success(pathEnumerationNodeService.getDescendants(id));
    }

    /**
     * 构建树结构
     *
     * @return 树结构
     */
    @GetMapping("/tree")
    public Result<List<PathEnumerationNodeTree>> buildTree() {
        return Result.success(pathEnumerationNodeService.buildTree());
    }
}