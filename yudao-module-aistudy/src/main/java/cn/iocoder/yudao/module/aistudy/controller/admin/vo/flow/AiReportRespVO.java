package cn.iocoder.yudao.module.aistudy.controller.admin.vo.flow;

import lombok.Data;

import java.util.List;

@Data
public class AiReportRespVO {
    private String title;
    private String summary;
    private List<String> strongPoints;
    private List<String> weakPoints;
    private List<String> nextPlan;
}
