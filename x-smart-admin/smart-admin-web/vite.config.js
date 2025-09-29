/*
 * vite配置
 *
 * @Author:    1024创新实验室-主任：卓大
 * @Date:      2022-05-02 23:44:56
 * @Wechat:    zhuda1024
 * @Email:     lab1024@163.com
 * @Copyright  1024创新实验室 （ https://1024lab.net ），Since 2012
 */
import {resolve} from 'path';
import vue from '@vitejs/plugin-vue';
import customVariables from '/@/theme/custom-variables.js';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { VantResolver } from '@vant/auto-import-resolver';
import pxtovw from 'postcss-px-to-viewport';

const loder_pxtovw = pxtovw({
  //这里是设计稿宽度 自己修改
  viewportWidth: 375,
  viewportUnit: 'vw',
  exclude: [
    /^(?!.*?(\.mobile))/,
    /node_modules/,
    /assets/,
  ]
})

const pathResolve = (dir) => {
    return resolve(__dirname, '.', dir);
};

const cdnPath = process.env.NODE_ENV === 'production' ?
    'https://payment-prod-1327269739.cos.accelerate.myqcloud.com' :
    "https://payment-test-1327269739.cos.accelerate.myqcloud.com"

export default {
    base: process.env.NODE_ENV === 'development' ? '/' : cdnPath + '/admin-web/static/',
    root: process.cwd(),
    resolve: {
        alias: [
            // 国际化替换
            {
                find: 'vue-i18n',
                replacement: 'vue-i18n/dist/vue-i18n.cjs.js',
            },
            // 绝对路径重命名：/@/xxxx => src/xxxx
            {
                find: /\/@\//,
                replacement: pathResolve('src') + '/',
            },
            {
                find: /^~/,
                replacement: '',
            },
        ],
    },
    // 服务端渲染
    server: {
        host: '0.0.0.0',
        port: 8081,
    },
    plugins: [
      vue(),
      AutoImport({
        resolvers: [VantResolver()],
      }),
      Components({
        resolvers: [VantResolver()],
      }),
    ],
    optimizeDeps: {
        include: ['ant-design-vue/es/locale/zh_CN', 'dayjs/locale/zh-cn', 'ant-design-vue/es/locale/en_US'],
        exclude: ['vue-demi'],
    },
    build: {
        // 清除console和debugger
        terserOptions: {
            compress: {
                drop_console: true,
                drop_debugger: true,
            },
        },
        rollupOptions: {
            output: {
                //配置这个是让不同类型文件放在不同文件夹，不会显得太乱
                chunkFileNames: 'js/[name]-[hash].js',
                entryFileNames: 'js/[name]-[hash].js',
                assetFileNames: '[ext]/[name]-[hash].[ext]',
                manualChunks(id) {
                    //静态资源分拆打包
                    if (id.includes('node_modules')) {
                        return id.toString().split('node_modules/')[1].split('/')[0].toString();
                    }
                },
            },
        },
        sourcemap: false, // 减少内存占用
        target: 'modules',
        outDir: 'dist', // 指定输出路径
        assetsDir: 'assets', // 指定生成静态文件目录
        assetsInlineLimit: '4096', // 小于此阈值的导入或引用资源将内联为 base64 编码
        chunkSizeWarningLimit: 2000, // chunk 大小警告的限制
        minify: 'terser', // 混淆器，terser构建后文件体积更小
        emptyOutDir: true, //打包前先清空原有打包文件
    },
    css: {
        preprocessorOptions: {
            less: {
                modifyVars: customVariables,
                javascriptEnabled: true,
            },
        },
        postcss: {
          plugins: [loder_pxtovw]
        }
    },
    define: {
        __INTLIFY_PROD_DEVTOOLS__: false,
        'process.env': process.env,
    },
};
