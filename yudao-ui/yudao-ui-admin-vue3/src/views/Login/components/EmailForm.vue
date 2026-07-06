<template>
  <el-form
    v-show="getShow"
    ref="formEmailLogin"
    :model="loginData.loginForm"
    :rules="rules"
    class="login-form"
    label-position="top"
    label-width="120px"
    size="large"
  >
    <el-row class="mx-[-10px]">
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <LoginFormTitle class="w-full" />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item v-if="loginData.tenantEnable === 'true'" prop="tenantName">
          <el-input
            v-model="loginData.loginForm.tenantName"
            :placeholder="t('login.tenantNamePlaceholder')"
            :prefix-icon="iconHouse"
            type="primary"
            link
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="email">
          <el-input
            v-model="loginData.loginForm.email"
            :placeholder="t('login.emailPlaceholder')"
            :prefix-icon="iconMessage"
          />
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item prop="code">
          <el-row :gutter="5" justify="space-between" style="width: 100%">
            <el-col :span="24">
              <el-input
                v-model="loginData.loginForm.code"
                :placeholder="t('login.codePlaceholder')"
                :prefix-icon="iconCircleCheck"
              >
                <template #append>
                  <span
                    v-if="emailCodeTimer <= 0"
                    class="getEmailCode"
                    style="cursor: pointer"
                    @click="getEmailCode"
                  >
                    {{ t('login.getSmsCode') }}
                  </span>
                  <span v-if="emailCodeTimer > 0" class="getEmailCode" style="cursor: pointer">
                    {{ emailCodeTimer }}秒后可重新获取
                  </span>
                </template>
              </el-input>
            </el-col>
          </el-row>
        </el-form-item>
      </el-col>
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <el-button :loading="loginLoading" class="w-full" type="primary" @click="getCode()">
            {{ t('login.login') }}
          </el-button>
        </el-form-item>
      </el-col>
      <Verify
        v-if="loginData.captchaEnable === 'true'"
        ref="verify"
        :captchaType="captchaType"
        :imgSize="{ width: '400px', height: '200px' }"
        mode="pop"
        @success="signIn"
      />
      <el-col :span="24" class="px-10px">
        <el-form-item>
          <el-button :loading="loginLoading" class="w-full" @click="handleBackLogin()">
            {{ t('login.backLogin') }}
          </el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script lang="ts" setup>
import type { RouteLocationNormalizedLoaded } from 'vue-router'

import { useIcon } from '@/hooks/web/useIcon'

import { setTenantId, setToken } from '@/utils/auth'
import { usePermissionStore } from '@/store/modules/permission'
import { getTenantIdByName, sendEmailCode, emailLogin } from '@/api/login'
import LoginFormTitle from './LoginFormTitle.vue'
import { LoginStateEnum, useFormValid, useLoginState } from './useLogin'
import { ElLoading } from 'element-plus'
import { Verify } from '@/components/Verifition'

defineOptions({ name: 'EmailForm' })

const { t } = useI18n()
const message = useMessage()
const permissionStore = usePermissionStore()
const { currentRoute, push } = useRouter()
const formEmailLogin = ref()
const loginLoading = ref(false)
const iconHouse = useIcon({ icon: 'ep:house' })
const iconMessage = useIcon({ icon: 'ep:message' })
const iconCircleCheck = useIcon({ icon: 'ep:circle-check' })
const { validForm } = useFormValid(formEmailLogin)
const { handleBackLogin, getLoginState } = useLoginState()
const getShow = computed(() => unref(getLoginState) === LoginStateEnum.EMAIL)

const rules = {
  tenantName: [required],
  email: [required],
  code: [required]
}
const loginData = reactive({
  captchaEnable: import.meta.env.VITE_APP_CAPTCHA_ENABLE,
  tenantEnable: import.meta.env.VITE_APP_TENANT_ENABLE,
  token: '',
  loading: {
    signIn: false
  },
  loginForm: {
    uuid: '',
    tenantName: import.meta.env.VITE_APP_DEFAULT_LOGIN_TENANT || 'AI伴学平台',
    email: '',
    code: ''
  }
})
const emailCodeTimer = ref(0)
const redirect = ref<string>('')
const verify = ref()
const captchaType = ref('blockPuzzle')
const getEmailCode = async () => {
  await getTenantId()
  if (!loginData.loginForm.email) {
    message.warning(t('login.emailPlaceholder'))
    return
  }
  await sendEmailCode({ email: loginData.loginForm.email }).then(async () => {
    message.success(t('login.SmsSendMsg'))
    emailCodeTimer.value = 60
    let msgTimer = setInterval(() => {
      emailCodeTimer.value = emailCodeTimer.value - 1
      if (emailCodeTimer.value <= 0) {
        clearInterval(msgTimer)
      }
    }, 1000)
  }).catch(() => {})
}

const getCode = () => {
  if (loginData.captchaEnable === 'false') {
    signIn()
  } else {
    verify.value.show()
  }
}
watch(
  () => currentRoute.value,
  (route: RouteLocationNormalizedLoaded) => {
    redirect.value = route?.query?.redirect as string
  },
  {
    immediate: true
  }
)
const getTenantId = async () => {
  if (loginData.tenantEnable === 'true') {
    const res = await getTenantIdByName(loginData.loginForm.tenantName)
    setTenantId(res)
  }
}
const signIn = async () => {
  await getTenantId()
  const data = await validForm()
  if (!data) return
  ElLoading.service({
    lock: true,
    text: '正在加载系统中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  loginLoading.value = true
  await emailLogin({
    email: loginData.loginForm.email,
    code: loginData.loginForm.code
  })
    .then(async (res) => {
      setToken(res)
      if (!redirect.value) {
        redirect.value = '/'
      }
      push({ path: redirect.value || permissionStore.addRouters[0].path })
    })
    .catch(() => {})
    .finally(() => {
      loginLoading.value = false
      setTimeout(() => {
        const loadingInstance = ElLoading.service()
        loadingInstance.close()
      }, 400)
    })
}
</script>

<style lang="scss" scoped>
:deep(.anticon) {
  &:hover {
    color: var(--el-color-primary) !important;
  }
}
</style>