import en from './en'

export default {
  ...en,
  login: {
    ...en.login,
    platformName: 'Plateforme d’apprentissage IA',
    welcome: 'Bienvenue sur la plateforme d’apprentissage IA et de croissance professionnelle',
    message:
      'Console d’administration pour l’apprentissage intelligent, la montée en compétences et la préparation à l’emploi des étudiants',
    otherLogin: 'Autres méthodes de connexion',
    register: 'Inscription',
    registerEntry: 'Créer un compte',
    remember: 'Se souvenir de moi',
    forgetPassword: 'Mot de passe oublié ?',
    usernamePlaceholder: 'Saisissez le nom d’utilisateur',
    passwordPlaceholder: 'Saisissez le mot de passe',
    mobileTitle: 'Connexion mobile',
    phoneLogin: 'Mobile',
    wechatLogin: 'WeChat',
    githubLogin: 'GitHub'
  },
  sys: {
    ...en.sys,
    login: {
      ...en.sys.login,
      backSignIn: 'Retour',
      signInFormTitle: 'Connexion',
      mobileSignInFormTitle: 'Connexion mobile',
      signUpFormTitle: 'Inscription',
      forgetFormTitle: 'Réinitialiser le mot de passe',
      loginButton: 'Connexion',
      rememberMe: 'Se souvenir de moi',
      forgetPassword: 'Mot de passe oublié ?',
      otherSignIn: 'Autres méthodes de connexion',
      accountPlaceholder: 'Saisissez le nom d’utilisateur',
      passwordPlaceholder: 'Saisissez le mot de passe'
    }
  }
}
