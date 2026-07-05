import en from './en'

export default {
  ...en,
  login: {
    ...en.login,
    platformName: 'Plataforma de Estudio con IA',
    welcome: 'Bienvenido a la plataforma de estudio con IA y crecimiento profesional',
    message:
      'Consola de administración para aprendizaje inteligente, desarrollo de habilidades y preparación laboral de estudiantes universitarios',
    otherLogin: 'Otros métodos de inicio',
    register: 'Registrarse',
    registerEntry: 'Crear cuenta',
    remember: 'Recordarme',
    forgetPassword: '¿Olvidaste tu contraseña?',
    usernamePlaceholder: 'Ingresa el usuario',
    passwordPlaceholder: 'Ingresa la contraseña',
    mobileTitle: 'Inicio con móvil',
    phoneLogin: 'Móvil',
    wechatLogin: 'WeChat',
    githubLogin: 'GitHub'
  },
  sys: {
    ...en.sys,
    login: {
      ...en.sys.login,
      backSignIn: 'Volver',
      signInFormTitle: 'Iniciar sesión',
      mobileSignInFormTitle: 'Inicio con móvil',
      signUpFormTitle: 'Registro',
      forgetFormTitle: 'Restablecer contraseña',
      loginButton: 'Iniciar sesión',
      rememberMe: 'Recordarme',
      forgetPassword: '¿Olvidaste tu contraseña?',
      otherSignIn: 'Otros métodos de inicio',
      accountPlaceholder: 'Ingresa el usuario',
      passwordPlaceholder: 'Ingresa la contraseña'
    }
  }
}
