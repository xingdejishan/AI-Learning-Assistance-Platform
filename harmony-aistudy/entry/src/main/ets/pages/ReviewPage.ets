import { common } from '@kit.AbilityKit';
import { promptAction } from '@kit.ArkUI';
import { LearningFlowApi } from '../api/LearningFlowApi';
import { AuthStore } from '../common/AuthStore';
import { KnowledgePoint, ReviewSubmitResponse } from '../model/LearningFlowModels';

@Entry
@Component
struct ReviewPage {
  @State loading: boolean = false;
  @State submittingId: number = 0;
  @State error: string = '';
  @State reviews: KnowledgePoint[] = [];
  @State lastResult: ReviewSubmitResponse | undefined = undefined;

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
      this.reviews = await LearningFlowApi.todayReviews();
    } catch (err) {
      this.error = (err as Error).message || '加载复习失败';
    } finally {
      this.loading = false;
    }
  }

  async submit(item: KnowledgePoint, remembered: boolean): Promise<void> {
    if (!item.id) {
      return;
    }
    this.submittingId = item.id;
    try {
      this.lastResult = await LearningFlowApi.submitReview(item.id, remembered);
      promptAction.showToast({ message: '已提交' });
      await this.loadData();
    } catch (err) {
      promptAction.showToast({ message: (err as Error).message || '提交失败' });
    } finally {
      this.submittingId = 0;
    }
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Text('今日复习').fontSize(26).fontWeight(FontWeight.Bold).width('100%')
        if (this.lastResult) {
          Text(`最新结果：阶段 ${this.lastResult.newStage ?? 0}，下次 ${this.lastResult.nextReviewTime || '-'}，掌握度 ${this.lastResult.mastery ?? 0}%，XP +${this.lastResult.xpGained ?? 0}`)
            .fontSize(14)
            .fontColor('#256029')
            .width('100%')
        }
        if (this.loading) {
          Text('加载中...').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').textAlign(TextAlign.Center).width('100%').padding(24)
        } else if (this.reviews.length === 0) {
          Text('今天没有待复习内容').fontColor('#666666').textAlign(TextAlign.Center).width('100%').padding(24)
        } else {
          ForEach(this.reviews, (item: KnowledgePoint) => {
            Column({ space: 8 }) {
              Text(item.skillName || '未关联技能').fontSize(18).fontWeight(FontWeight.Bold).width('100%')
              Text(item.content || '').fontSize(15).width('100%')
              Text(`阶段：${item.stage ?? 0}    下次复习：${item.nextReviewTime || '-'}`).fontSize(13).fontColor('#666666').width('100%')
              Row({ space: 10 }) {
                Button(this.submittingId === item.id ? '提交中...' : '记住了')
                  .layoutWeight(1)
                  .height(40)
                  .enabled(this.submittingId === 0)
                  .onClick(() => this.submit(item, true))
                Button(this.submittingId === item.id ? '提交中...' : '没记住')
                  .layoutWeight(1)
                  .height(40)
                  .enabled(this.submittingId === 0)
                  .onClick(() => this.submit(item, false))
              }
            }
            .padding(16)
            .backgroundColor(Color.White)
            .borderRadius(8)
            .width('100%')
          }, (item: KnowledgePoint) => `${item.id}`)
        }
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }
}
