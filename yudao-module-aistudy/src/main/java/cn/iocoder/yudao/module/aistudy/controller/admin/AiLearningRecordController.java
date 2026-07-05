package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.learning.*;
import cn.iocoder.yudao.module.aistudy.service.AiStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI伴学学习记录")
@RestController
@RequestMapping("/aistudy/learning-record")
@Validated
public class AiLearningRecordController {

    @Resource
    private AiStudyService aiStudyService;

    @PostMapping("/create")
    @Operation(summary = "新增学习记录")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:create')")
    public CommonResult<Long> createLearningRecord(@Valid @RequestBody AiLearningRecordSaveReqVO reqVO) {
        return success(aiStudyService.createLearningRecord(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改学习记录")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:update')")
    public CommonResult<Boolean> updateLearningRecord(@Valid @RequestBody AiLearningRecordSaveReqVO reqVO) {
        aiStudyService.updateLearningRecord(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习记录")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:delete')")
    public CommonResult<Boolean> deleteLearningRecord(@RequestParam("id") Long id) {
        aiStudyService.deleteLearningRecord(id);
        return success(true);
    }

    @GetMapping("/page")
    @Operation(summary = "查询学习记录分页")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:query')")
    public CommonResult<PageResult<AiLearningRecordRespVO>> getLearningRecordPage(@Valid AiLearningRecordPageReqVO reqVO) {
        return success(aiStudyService.getLearningRecordPage(reqVO));
    }

    @GetMapping("/duration-by-user")
    @Operation(summary = "按用户统计学习时长")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:query')")
    public CommonResult<List<Map<String, Object>>> getDurationByUser() {
        return success(aiStudyService.getDurationByUser());
    }

    @GetMapping("/duration-by-skill")
    @Operation(summary = "按技能统计学习时长")
    @PreAuthorize("@ss.hasPermission('aistudy:learning-record:query')")
    public CommonResult<List<Map<String, Object>>> getDurationBySkill() {
        return success(aiStudyService.getDurationBySkill());
    }
}
