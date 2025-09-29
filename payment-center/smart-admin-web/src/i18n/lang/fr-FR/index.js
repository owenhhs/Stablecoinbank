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
};
