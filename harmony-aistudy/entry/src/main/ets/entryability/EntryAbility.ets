import { AbilityConstant, UIAbility, Want } from '@kit.AbilityKit';
import { hilog } from '@kit.PerformanceAnalysisKit';
import { window } from '@kit.ArkUI';
import { AuthStore } from '../common/AuthStore';

export default class EntryAbility extends UIAbility {
  onCreate(want: Want, launchParam: AbilityConstant.LaunchParam): void {
    hilog.info(0x0000, 'AISTUDY', 'EntryAbility onCreate');
    void AuthStore.init(this.context);
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    windowStage.loadContent('pages/LoginPage', (err) => {
      if (err.code) {
        hilog.error(0x0000, 'AISTUDY', 'loadContent failed %{public}s', JSON.stringify(err));
      }
    });
  }
}
