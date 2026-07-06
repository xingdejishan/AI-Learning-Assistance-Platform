import { common } from '@kit.AbilityKit';
import { LearningFlowApi } from '../api/LearningFlowApi';
import { AuthStore } from '../common/AuthStore';
import { LearningFlowSummary } from '../model/LearningFlowModels';

@Entry
@Component
struct DashboardPage {
  @State loading: boolean = false;
  @State error: string = '';
  @State summary: LearningFlowSummary | undefined = undefined;

  aboutToAppear(): void {
    void this.prepare();
  }

  async prepare(): Promise<void> {
    await AuthStore.init(getContext(this) as common.Context);
    await this.loadData();
  }

  async loadData(): Promise<void> {
    this.loading = true;
    this.error = '';
    try {
      this.summary = await LearningFlowApi.summary();
    } catch (err) {
      this.error = (err as Error).message || '加载看板失败';
    } finally {
      this.loading = false;
    }
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Text('学习闭环看板').fontSize(26).fontWeight(FontWeight.Bold).width('100%')
        if (this.loading) {
          Text('加载中...').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (!this.summary) {
          Text('暂无看板数据').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else {
          Column({ space: 10 }) {
            Text(`总 XP：${this.summary.profile?.totalXp ?? 0}`).fontSize(18).fontWeight(FontWeight.Bold).width('100%')
            Text(`等级：${this.summary.profile?.levelNo ?? 0}`).fontSize(16).width('100%')
            Text(`今日复习数：${this.summary.todayReviewCount ?? 0}`).fontSize(16).width('100%')
          }
          .padding(16)
          .backgroundColor(Color.White)
          .borderRadius(8)
          .width('100%')
          this.JsonList('掌握度排行', this.summary.masteryRanking || [])
          this.JsonList('阶段分布', this.summary.stageDistribution || [])
          this.JsonList('最近事件', this.summary.recentEvents || [])
        }
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }

  @Builder
  JsonList(title: string, items: Object[]) {
    Column({ space: 8 }) {
      Text(title).fontSize(18).fontWeight(FontWeight.Bold).width('100%')
      if (items.length === 0) {
        Text('暂无').fontSize(14).fontColor('#777777').width('100%')
      } else {
        ForEach(items, (item: Object, index: number) => {
          Text(JSON.stringify(item)).fontSize(13).fontColor('#555555').width('100%')
        }, (item: Object, index: number) => `${title}-${index}`)
      }
    }
    .padding(16)
    .backgroundColor(Color.White)
    .borderRadius(8)
    .width('100%')
  }
}
