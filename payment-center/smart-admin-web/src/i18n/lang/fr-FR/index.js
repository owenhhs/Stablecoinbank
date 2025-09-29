/*
 * 法语国际化
 *
 * @Author:    ZFX Payment Center
 * @Date:      2024-09-29
 * @Copyright ZFX Payment Center System
 */
import antd from 'ant-design-vue/es/locale/fr_FR';
import dayjs from 'dayjs/locale/fr';

export default {
  antdLocale: antd,
  dayjsLocale: dayjs,
  'setting.title': 'Paramètres du site',
  'setting.color': 'Couleur du thème',
  'setting.menu.layout': 'Disposition du menu',
  'setting.menu.width': 'Largeur du menu',
  'setting.menu.theme': 'Thème du menu',
  'setting.compact': 'Page compacte',
  'setting.border.radius': 'Rayon de bordure',
  'setting.page.width': 'Largeur de page',
  'setting.bread': 'Fil d\'Ariane',
  'setting.pagetag': 'Onglets',
  'setting.footer': 'Pied de page',
  'setting.helpdoc': 'Documentation',
  'setting.watermark': 'Filigrane',
  
  // 业务相关翻译
  'common.login': 'Connexion',
  'common.logout': 'Déconnexion',
  'common.submit': 'Soumettre',
  'common.cancel': 'Annuler',
  'common.confirm': 'Confirmer',
  'common.delete': 'Supprimer',
  'common.edit': 'Modifier',
  'common.add': 'Ajouter',
  'common.search': 'Rechercher',
  'common.reset': 'Réinitialiser',
  'common.export': 'Exporter',
  'common.import': 'Importer',
  'common.save': 'Enregistrer',
  'common.back': 'Retour',
  'common.next': 'Suivant',
  'common.previous': 'Précédent',
  'common.refresh': 'Actualiser',
  'common.operate': 'Opération',
  'common.status': 'Statut',
  'common.createTime': 'Heure de création',
  'common.updateTime': 'Heure de mise à jour',
  'common.remark': 'Remarque',
  
  // 支付相关
  'payment.order.title': 'Gestion des commandes de paiement',
  'payment.order.no': 'Numéro de commande',
  'payment.order.amount': 'Montant',
  'payment.order.status': 'Statut du paiement',
  'payment.order.method': 'Méthode de paiement',
  'payment.order.time': 'Heure de paiement',
  'payment.channel.title': 'Gestion des canaux de paiement',
  'payment.channel.name': 'Nom du canal',
  'payment.channel.code': 'Code du canal',
  'payment.channel.type': 'Type de canal',
  'payment.merchant.title': 'Gestion des marchands',
  'payment.merchant.name': 'Nom du marchand',
  'payment.merchant.code': 'Code du marchand',
  'payment.settlement.title': 'Gestion des règlements',
  'payment.settlement.no': 'Numéro de règlement',
  'payment.settlement.amount': 'Montant du règlement',
  
  // 状态
  'status.enable': 'Activé',
  'status.disable': 'Désactivé',
  'status.pending': 'En attente',
  'status.success': 'Succès',
  'status.failed': 'Échec',
  'status.processing': 'En cours',
  'status.completed': 'Terminé',
  'status.cancelled': 'Annulé',
  'status.expired': 'Expiré',
  
  // 登录相关
  'login.title': 'Connexion au compte',
  'login.welcome.title': 'Bienvenue sur SmartAdmin V3',
  'login.username.placeholder': 'Veuillez saisir le nom d\'utilisateur',
  'login.password.placeholder': 'Veuillez saisir le mot de passe : au moins 3 types de caractères, minimum 8 chiffres',
  'login.captcha.placeholder': 'Veuillez saisir le code de vérification',
  'login.remember': 'Se souvenir du mot de passe',
  'login.demo.account': 'Compte : admin, Mot de passe : 123456',
  'login.submit': 'Se connecter',
  'login.other.ways': 'Autres méthodes de connexion',
  'login.validation.username': 'Le nom d\'utilisateur ne peut pas être vide',
  'login.validation.password': 'Le mot de passe ne peut pas être vide',
  'login.validation.captcha': 'Le code de vérification ne peut pas être vide',
  
  // 首页相关
  'home.department': 'Département',
  'common.home': 'Accueil',
  
  // 设置相关
  'setting.language': 'Langue',
  'setting.compact.default': 'Par défaut',
  'setting.compact.compact': 'Compact',
  'setting.unit.pixel': 'pixel (px)',
  'setting.unit.pixel_or_percent': 'pixel (px) ou pourcentage',
  'setting.show': 'Afficher',
  'setting.hide': 'Masquer',
  'setting.copy.config': 'Copier la configuration',
  'setting.reset.default': 'Restaurer par défaut',
};
