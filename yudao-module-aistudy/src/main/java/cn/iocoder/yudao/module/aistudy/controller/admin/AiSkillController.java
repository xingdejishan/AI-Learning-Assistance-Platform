package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.skill.*;
import cn.iocoder.yudao.module.aistudy.service.AiStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI伴学技能树")
@RestController
@RequestMapping("/aistudy/skill")
@Validated
public class AiSkillController {

    @Resource
    private AiStudyService aiStudyService;

    @PostMapping("/create")
    @Operation(summary = "新增技能")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:create')")
    public CommonResult<Long> createSkill(@Valid @RequestBody AiSkillSaveReqVO reqVO) {
        return success(aiStudyService.createSkill(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改技能")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:update')")
    public CommonResult<Boolean> updateSkill(@Valid @RequestBody AiSkillSaveReqVO reqVO) {
        aiStudyService.updateSkill(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除技能")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:delete')")
    public CommonResult<Boolean> deleteSkill(@RequestParam("id") Long id) {
        aiStudyService.deleteSkill(id);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "查询技能列表")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:query')")
    public CommonResult<List<AiSkillRespVO>> getSkillList(@RequestParam(value = "name", required = false) String name,
                                                          @RequestParam(value = "category", required = false) String category,
                                                          @RequestParam(value = "status", required = false) Integer status) {
        return success(aiStudyService.getSkillList(name, category, status));
    }

    @GetMapping("/tree")
    @Operation(summary = "查询技能树")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:query')")
    public CommonResult<List<AiSkillRespVO>> getSkillTree() {
        return success(aiStudyService.getSkillTree());
    }

    @GetMapping("/progress")
    @Operation(summary = "查询学生技能进度")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:query')")
    public CommonResult<List<AiSkillProgressRespVO>> getSkillProgress(@RequestParam(value = "userId", required = false) Long userId) {
        return success(aiStudyService.getSkillProgressList(userId));
    }

    @PostMapping("/progress/update")
    @Operation(summary = "更新技能学习进度")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:update')")
    public CommonResult<Long> updateSkillProgress(@Valid @RequestBody AiSkillProgressSaveReqVO reqVO) {
        return success(aiStudyService.updateSkillProgress(reqVO));
    }

    @GetMapping("/evaluate")
    @Operation(summary = "技能评估与推荐")
    @PreAuthorize("@ss.hasPermission('aistudy:skill:query')")
    public CommonResult<String> evaluate(@RequestParam("progress") Integer progress) {
        return success(aiStudyService.evaluateProgress(progress));
    }
}
