/**
 * @Author：qzt
 * @CreateTime: 2026 -05-14
 **/

package cn.iocoder.linhai.module.ai.controller.admin.learn;

import cn.iocoder.linhai.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Parameter;

import static cn.iocoder.linhai.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 知识库")
@RestController
@RequestMapping("/ai/learning")
@Validated
public class AiLearningPlan {

    @GetMapping("/plan/get")
    @Operation(summary = "获得学习计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:learning:query')")
//    public CommonResult<AiLearningPlanRespVO> getLearningPlan(@RequestParam("id") Long id) {
    public CommonResult<String> getLearningPlan(@RequestParam("id") Long id) {
//        AiLearningPlanDO plan = learningService.getLearningPlan(id);
//        return success(BeanUtils.toBean(plan, AiLearningPlanRespVO.class));
        return success(null);
    }
}
