/*
 * 葡萄牙语国际化
 *
 * @Author:    ZFX Payment Center
 * @Date:      2024-09-29
 * @Copyright ZFX Payment Center System
 */
import antd from 'ant-design-vue/es/locale/pt_PT';
import dayjs from 'dayjs/locale/pt';

export default {
  antdLocale: antd,
  dayjsLocale: dayjs,
  'setting.title': 'Configurações do site',
  'setting.color': 'Cor do tema',
  'setting.menu.layout': 'Layout do menu',
  'setting.menu.width': 'Largura do menu',
  'setting.menu.theme': 'Tema do menu',
  'setting.compact': 'Página compacta',
  'setting.border.radius': 'Raio da borda',
  'setting.page.width': 'Largura da página',
  'setting.bread': 'Navegação estrutural',
  'setting.pagetag': 'Abas',
  'setting.footer': 'Rodapé',
  'setting.helpdoc': 'Documentação',
  'setting.watermark': 'Marca d\'água',
  
  // 业务相关翻译
  'common.login': 'Entrar',
  'common.logout': 'Sair',
  'common.submit': 'Enviar',
  'common.cancel': 'Cancelar',
  'common.confirm': 'Confirmar',
  'common.delete': 'Excluir',
  'common.edit': 'Editar',
  'common.add': 'Adicionar',
  'common.search': 'Pesquisar',
  'common.reset': 'Redefinir',
  'common.export': 'Exportar',
  'common.import': 'Importar',
  'common.save': 'Salvar',
  'common.back': 'Voltar',
  'common.next': 'Próximo',
  'common.previous': 'Anterior',
  'common.refresh': 'Atualizar',
  'common.operate': 'Operação',
  'common.status': 'Status',
  'common.createTime': 'Hora de criação',
  'common.updateTime': 'Hora de atualização',
  'common.remark': 'Observação',
  
  // 支付相关
  'payment.order.title': 'Gestão de pedidos de pagamento',
  'payment.order.no': 'Número do pedido',
  'payment.order.amount': 'Valor',
  'payment.order.status': 'Status do pagamento',
  'payment.order.method': 'Método de pagamento',
  'payment.order.time': 'Hora do pagamento',
  'payment.channel.title': 'Gestão de canais de pagamento',
  'payment.channel.name': 'Nome do canal',
  'payment.channel.code': 'Código do canal',
  'payment.channel.type': 'Tipo de canal',
  'payment.merchant.title': 'Gestão de comerciantes',
  'payment.merchant.name': 'Nome do comerciante',
  'payment.merchant.code': 'Código do comerciante',
  'payment.settlement.title': 'Gestão de liquidações',
  'payment.settlement.no': 'Número da liquidação',
  'payment.settlement.amount': 'Valor da liquidação',
  
  // 状态
  'status.enable': 'Habilitado',
  'status.disable': 'Desabilitado',
  'status.pending': 'Pendente',
  'status.success': 'Sucesso',
  'status.failed': 'Falhou',
  'status.processing': 'Processando',
  'status.completed': 'Concluído',
  'status.cancelled': 'Cancelado',
  'status.expired': 'Expirado',
  
  // 登录相关
  'login.title': 'Login da conta',
  'login.welcome.title': 'Bem-vindo ao SmartAdmin V3',
  'login.username.placeholder': 'Por favor insira o nome de usuário',
  'login.password.placeholder': 'Por favor insira a senha: pelo menos 3 tipos de caracteres, mínimo 8 dígitos',
  'login.captcha.placeholder': 'Por favor insira o código de verificação',
  'login.remember': 'Lembrar senha',
  'login.demo.account': 'Conta: admin, Senha: 123456',
  'login.submit': 'Entrar',
  'login.other.ways': 'Outros métodos de login',
  'login.validation.username': 'O nome de usuário não pode estar vazio',
  'login.validation.password': 'A senha não pode estar vazia',
  'login.validation.captcha': 'O código de verificação não pode estar vazio',
  
  // 首页相关
  'home.department': 'Departamento',
  'common.home': 'Início',
  
  // 设置相关
  'setting.language': 'Idioma',
  'setting.compact.default': 'Padrão',
  'setting.compact.compact': 'Compacto',
  'setting.unit.pixel': 'pixel (px)',
  'setting.unit.pixel_or_percent': 'pixel (px) ou percentual',
  'setting.show': 'Mostrar',
  'setting.hide': 'Ocultar',
  'setting.copy.config': 'Copiar configuração',
  'setting.reset.default': 'Restaurar padrão',
};
