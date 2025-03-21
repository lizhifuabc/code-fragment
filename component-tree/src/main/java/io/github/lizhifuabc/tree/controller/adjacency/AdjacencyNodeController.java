package io.github.lizhifuabc.tree.controller.adjacency;

import io.github.lizhifuabc.tree.common.Result;
import io.github.lizhifuabc.tree.domain.adjacency.AdjacencyNode;
import io.github.lizhifuabc.tree.service.adjacency.AdjacencyNodeService;
import io.github.lizhifuabc.tree.service.adjacency.AdjacencyNodeTree;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 邻接表模型控制器
 *
 * @author lizhifu
 * @since 2023/7/1
 */
@RestController
@RequestMapping("/api/tree/adjacency")
public class AdjacencyNodeController {

    private final AdjacencyNodeService adjacencyNodeService;

    public AdjacencyNodeController(AdjacencyNodeService adjacencyNodeService) {
        this.adjacencyNodeService = adjacencyNodeService;
    }

    /**
     * 创建节点
     *
     * @param node 节点
     * @return 创建后的节点
     */
    @PostMapping("/node")
    public Result<AdjacencyNode> createNode(@RequestBody AdjacencyNode node) {
        return Result.success(adjacencyNodeService.createNode(node));
    }

    /**
     * 更新节点
     *
     * @param node 节点
     * @return 更新后的节点
     */
    @PutMapping("/node")
    public Result<AdjacencyNode> updateNode(@RequestBody AdjacencyNode node) {
        return Result.success(adjacencyNodeService.updateNode(node));
    }

    /**
     * 删除节点
     *
     * @param id 节点ID
     * @return 是否删除成功
     */
    @DeleteMapping("/node/{id}")
    public Result<Boolean> deleteNode(@PathVariable Long id) {
        return Result.success(adjacencyNodeService.deleteNode(id));
    }

    /**
     * 获取节点
     *
     * @param id 节点ID
     * @return 节点
     */
    @GetMapping("/node/{id}")
    public Result<AdjacencyNode> getNode(@PathVariable Long id) {
        return Result.success(adjacencyNodeService.getNode(id));
    }

    /**
     * 获取所有节点
     *
     * @return 节点列表
     */
    @GetMapping("/nodes")
    public Result<List<AdjacencyNode>> getAllNodes() {
        return Result.success(adjacencyNodeService.getAllNodes());
    }

    /**
     * 获取子节点
     *
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @GetMapping("/nodes/children/{parentId}")
    public Result<List<AdjacencyNode>> getChildNodes(@PathVariable Long parentId) {
        return Result.success(adjacencyNodeService.getChildNodes(parentId));
    }

    /**
     * 获取所有子孙节点
     *
     * @param id 节点ID
     * @return 子孙节点列表
     */
    @GetMapping("/nodes/descendants/{id}")
    public Result<List<AdjacencyNode>> getDescendants(@PathVariable Long id) {
        return Result.success(adjacencyNodeService.getDescendants(id));
    }

    /**
     * 构建树结构
     *
     * @return 树结构
     */
    @GetMapping("/tree")
    public Result<List<AdjacencyNodeTree>> buildTree() {
        return Result.success(adjacencyNodeService.buildTree());
    }
}