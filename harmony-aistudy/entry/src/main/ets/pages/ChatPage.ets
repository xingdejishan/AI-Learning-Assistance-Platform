import { common } from '@kit.AbilityKit';
import { promptAction, router } from '@kit.ArkUI';
import { ChatApi } from '../api/ChatApi';
import { AuthStore } from '../common/AuthStore';
import { ChatRecord, QuizSubmitResponse } from '../model/ChatModels';

@Entry
@Component
struct ChatPage {
  @State skillId: number = 0;
  @State skillName: string = '默认技能';
  @State input: string = '';
  @State userAnswer: string = '';
  @State conversationId: string = '';
  @State loading: boolean = false;
  @State quizLoading: boolean = false;
  @State error: string = '';
  @State reply: ChatRecord | undefined = undefined;
  @State quizResult: QuizSubmitResponse | undefined = undefined;

  aboutToAppear(): void {
    void this.prepare();
  }

  async prepare(): Promise<void> {
    await AuthStore.init(getContext(this) as common.Context);
    const params = router.getParams() as Record<string, Object>;
    if (params) {
      this.skillId = Number(params.skillId || 0);
      this.skillName = `${params.skillName || '默认技能'}`;
    }
  }

  async send(): Promise<void> {
    if (this.input.trim().length === 0) {
      promptAction.showToast({ message: '请输入问题' });
      return;
    }
    const message = this.input.trim();
    this.loading = true;
    this.error = '';
    this.quizResult = undefined;
    try {
      const result = await ChatApi.send({
        skillId: this.skillId > 0 ? this.skillId : undefined,
        message,
        question: message,
        conversationId: this.conversationId.length > 0 ? this.conversationId : undefined,
        tutorCode: 'default'
      });
      this.reply = result;
      this.conversationId = result.conversationId || this.conversationId;
      this.input = '';
    } catch (err) {
      this.error = (err as Error).message || '发送失败';
      promptAction.showToast({ message: this.error });
    } finally {
      this.loading = false;
    }
  }

  async submitQuiz(): Promise<void> {
    if (!this.reply?.quiz?.question || !this.reply.quiz.answer) {
      return;
    }
    if (this.userAnswer.trim().length === 0) {
      promptAction.showToast({ message: '请输入小测答案' });
      return;
    }
    this.quizLoading = true;
    try {
      this.quizResult = await ChatApi.submitQuiz({
        skillId: this.skillId,
        chatId: this.reply.id,
        question: this.reply.quiz.question,
        referenceAnswer: this.reply.quiz.answer,
        userAnswer: this.userAnswer.trim()
      });
    } catch (err) {
      promptAction.showToast({ message: (err as Error).message || '提交失败' });
    } finally {
      this.quizLoading = false;
    }
  }

  build() {
    Scroll() {
      Column({ space: 12 }) {
        Text(`AI伴学：${this.skillName}`).fontSize(24).fontWeight(FontWeight.Bold).width('100%')
        TextArea({ text: this.input, placeholder: '输入你的问题' })
          .height(110)
          .onChange((value: string) => this.input = value)
        Button(this.loading ? '发送中...' : '发送')
          .width('100%')
          .height(44)
          .enabled(!this.loading)
          .onClick(() => this.send())
        if (this.error.length > 0) {
          Text(this.error).fontColor('#D93026').width('100%')
        }
        if (!this.reply && !this.loading && this.error.length === 0) {
          Text('暂无对话，发送一个问题开始学习。').fontColor('#666666').width('100%').textAlign(TextAlign.Center).padding(24)
        }
        if (this.reply) {
          Column({ space: 10 }) {
            Text('AI回答').fontSize(18).fontWeight(FontWeight.Bold).width('100%')
            Text(this.reply.answer || '暂无回答').fontSize(15).width('100%')
            Text(`掌握度：${this.reply.mastery ?? 0}%    XP +${this.reply.xpGained ?? 0}`).fontSize(14).fontColor('#555555').width('100%')
            Text(`总 XP：${this.reply.totalXp ?? 0}    等级：${this.reply.levelNo ?? 0}`).fontSize(14).fontColor('#555555').width('100%')
            if ((this.reply.knowledgePoints || []).length > 0) {
              Text(`知识点：${(this.reply.knowledgePoints || []).join('、')}`).fontSize(14).fontColor('#555555').width('100%')
            }
            if ((this.reply.nextSuggestion || '').length > 0) {
              Text(`下一步：${this.reply.nextSuggestion}`).fontSize(14).fontColor('#555555').width('100%')
            }
          }
          .padding(16)
          .backgroundColor(Color.White)
          .borderRadius(8)
          .width('100%')
        }
        if (this.reply?.quiz?.question) {
          Column({ space: 10 }) {
            Text('小测题').fontSize(18).fontWeight(FontWeight.Bold).width('100%')
            Text(this.reply.quiz.question || '').fontSize(15).width('100%')
            Text(`参考答案：${this.reply.quiz.answer || ''}`).fontSize(14).fontColor('#777777').width('100%')
            TextArea({ text: this.userAnswer, placeholder: '输入你的答案' })
              .height(90)
              .onChange((value: string) => this.userAnswer = value)
            Button(this.quizLoading ? '判题中...' : '提交答案并判题')
              .width('100%')
              .height(42)
              .enabled(!this.quizLoading && this.skillId > 0)
              .onClick(() => this.submitQuiz())
            if (this.quizResult) {
              Text(`结果：${this.quizResult.correct ? '正确' : '需改进'}    分数：${this.quizResult.score ?? 0}`).fontSize(14).width('100%')
              Text(`反馈：${this.quizResult.feedback || ''}`).fontSize(14).fontColor('#555555').width('100%')
              Text(`XP +${this.quizResult.xpGained ?? 0}    掌握度：${this.quizResult.mastery ?? 0}%`).fontSize(14).fontColor('#555555').width('100%')
            }
          }
          .padding(16)
          .backgroundColor(Color.White)
          .borderRadius(8)
          .width('100%')
        }
      }
      .padding(20)
      .width('100%')
    }
    .height('100%')
    .backgroundColor('#F7F8FA')
  }
}
