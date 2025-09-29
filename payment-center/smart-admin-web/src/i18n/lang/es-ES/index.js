/*
 * 西班牙语国际化
 *
 * @Author:    ZFX Payment Center
 * @Date:      2024-09-29
 * @Copyright ZFX Payment Center System
 */
import antd from 'ant-design-vue/es/locale/es_ES';
import dayjs from 'dayjs/locale/es';

export default {
  antdLocale: antd,
  dayjsLocale: dayjs,
  'setting.title': 'Configuración del sitio',
  'setting.color': 'Color del tema',
  'setting.menu.layout': 'Diseño del menú',
  'setting.menu.width': 'Ancho del menú',
  'setting.menu.theme': 'Tema del menú',
  'setting.compact': 'Página compacta',
  'setting.border.radius': 'Radio del borde',
  'setting.page.width': 'Ancho de página',
  'setting.bread': 'Migas de pan',
  'setting.pagetag': 'Pestañas',
  'setting.footer': 'Pie de página',
  'setting.helpdoc': 'Documentación',
  'setting.watermark': 'Marca de agua',
  
  // 业务相关翻译
  'common.login': 'Iniciar sesión',
  'common.logout': 'Cerrar sesión',
  'common.submit': 'Enviar',
  'common.cancel': 'Cancelar',
  'common.confirm': 'Confirmar',
  'common.delete': 'Eliminar',
  'common.edit': 'Editar',
  'common.add': 'Agregar',
  'common.search': 'Buscar',
  'common.reset': 'Restablecer',
  'common.export': 'Exportar',
  'common.import': 'Importar',
  'common.save': 'Guardar',
  'common.back': 'Atrás',
  'common.next': 'Siguiente',
  'common.previous': 'Anterior',
  'common.refresh': 'Actualizar',
  'common.operate': 'Operación',
  'common.status': 'Estado',
  'common.createTime': 'Hora de creación',
  'common.updateTime': 'Hora de actualización',
  'common.remark': 'Observación',
  
  // 支付相关
  'payment.order.title': 'Gestión de pedidos de pago',
  'payment.order.no': 'Número de pedido',
  'payment.order.amount': 'Importe',
  'payment.order.status': 'Estado del pago',
  'payment.order.method': 'Método de pago',
  'payment.order.time': 'Hora de pago',
  'payment.channel.title': 'Gestión de canales de pago',
  'payment.channel.name': 'Nombre del canal',
  'payment.channel.code': 'Código del canal',
  'payment.channel.type': 'Tipo de canal',
  'payment.merchant.title': 'Gestión de comerciantes',
  'payment.merchant.name': 'Nombre del comerciante',
  'payment.merchant.code': 'Código del comerciante',
  'payment.settlement.title': 'Gestión de liquidaciones',
  'payment.settlement.no': 'Número de liquidación',
  'payment.settlement.amount': 'Importe de liquidación',
  
  // 状态
  'status.enable': 'Habilitado',
  'status.disable': 'Deshabilitado',
  'status.pending': 'Pendiente',
  'status.success': 'Éxito',
  'status.failed': 'Fallido',
  'status.processing': 'Procesando',
  'status.completed': 'Completado',
  'status.cancelled': 'Cancelado',
  'status.expired': 'Expirado',
  
  // 登录相关
  'login.title': 'Inicio de sesión de cuenta',
  'login.welcome.title': 'Bienvenido a ZFX Payment Center',
  'login.welcome.description': 'SmartAdmin es desarrollado por 1024Lab de Henan·Luoyang, basado en SpringBoot + Sa-Token + Mybatis-Plus y Vue3 + Vite5 + Ant Design Vue 4 (soportando versiones JavaScript y TypeScript). Una plataforma de desarrollo rápido con \'código de alta calidad\' como núcleo, \'simple, eficiente, seguro\'.',
  'login.welcome.wechat': 'Agregar WeChat, acosar a Zhuoda :)',
  'login.username.placeholder': 'Por favor ingrese el nombre de usuario',
  'login.password.placeholder': 'Por favor ingrese la contraseña: al menos 3 tipos de caracteres, mínimo 8 dígitos',
  'login.captcha.placeholder': 'Por favor ingrese el código de verificación',
  'login.remember': 'Recordar contraseña',
  'login.demo.account': 'Cuenta: admin, Contraseña: 123456',
  'login.submit': 'Iniciar sesión',
  'login.other.ways': 'Otros métodos de inicio de sesión',
  'login.validation.username': 'El nombre de usuario no puede estar vacío',
  'login.validation.password': 'La contraseña no puede estar vacía',
  'login.validation.captcha': 'El código de verificación no puede estar vacío',
  
  // 首页相关
  'home.department': 'Departamento',
  'common.home': 'Inicio',
  
  // 设置相关
  'setting.language': 'Idioma',
  'setting.compact.default': 'Por defecto',
  'setting.compact.compact': 'Compacto',
  'setting.unit.pixel': 'píxel (px)',
  'setting.unit.pixel_or_percent': 'píxel (px) o porcentaje',
  'setting.show': 'Mostrar',
  'setting.hide': 'Ocultar',
  'setting.copy.config': 'Copiar configuración',
  'setting.reset.default': 'Restaurar por defecto',
};
