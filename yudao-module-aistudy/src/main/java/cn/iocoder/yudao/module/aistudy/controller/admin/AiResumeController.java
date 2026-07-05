package cn.iocoder.yudao.module.aistudy.controller.admin;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.aistudy.controller.admin.vo.resume.AiResumeRespVO;
import cn.iocoder.yudao.module.aistudy.service.AiStudyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 简历诊断")
@RestController
@RequestMapping("/aistudy/resume")
@Validated
public class AiResumeController {

    @Resource
    private AiStudyService aiStudyService;

    @PostMapping("/upload")
    @Operation(summary = "上传简历并诊断")
    @PreAuthorize("@ss.hasPermission('aistudy:resume:diagnose')")
    public CommonResult<AiResumeRespVO> upload(@RequestParam("file") MultipartFile file,
                                               @RequestParam(value = "userId", required = false) Long userId) {
        String fileName = file.getOriginalFilename();
        String fileUrl = "/mock/resume/" + fileName;
        return success(aiStudyService.diagnoseResume(fileUrl, fileName, userId));
    }

    @PostMapping("/diagnose")
    @Operation(summary = "按文件地址诊断简历")
    @PreAuthorize("@ss.hasPermission('aistudy:resume:diagnose')")
    public CommonResult<AiResumeRespVO> diagnose(@RequestParam("fileUrl") String fileUrl,
                                                 @RequestParam("fileName") String fileName,
                                                 @RequestParam(value = "userId", required = false) Long userId) {
        return success(aiStudyService.diagnoseResume(fileUrl, fileName, userId));
    }

    @GetMapping("/list")
    @Operation(summary = "查询诊断记录")
    @PreAuthorize("@ss.hasPermission('aistudy:resume:query')")
    public CommonResult<List<AiResumeRespVO>> getList(@RequestParam(value = "userId", required = false) Long userId) {
        return success(aiStudyService.getResumeList(userId));
    }

    @GetMapping("/get")
    @Operation(summary = "查询诊断详情")
    @PreAuthorize("@ss.hasPermission('aistudy:resume:query')")
    public CommonResult<AiResumeRespVO> get(@RequestParam("id") Long id) {
        return success(aiStudyService.getResume(id));
    }
}
