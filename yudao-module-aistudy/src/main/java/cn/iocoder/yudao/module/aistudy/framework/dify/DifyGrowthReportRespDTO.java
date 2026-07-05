package cn.iocoder.yudao.module.aistudy.framework.dify;

import lombok.Data;

import java.util.List;

@Data
public class DifyGrowthReportRespDTO {

    private String title;

    private String summary;

    private List<String> strongPoints;

    private List<String> weakPoints;

    private List<String> nextPlan;

    private String riskReminder;
}
