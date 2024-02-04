export default [
  {
    path: '/user',
    layout: false,
    routes: [
      { path: '/user/login', component: './User/Login' },
      { path: '/user/register', component: './User/Register' },
    ],
  },
  { path: '/welcome', icon: 'smile', component: './Welcome', name: '欢迎页' },
  {
    path: '/admin',
    icon: 'crown',
    name: '管理页',
    access: 'canAdmin',
    routes: [
      { path: '/admin', redirect: '/admin/user' },
      { icon: 'user', path: '/admin/user', component: './Admin/User', name: '用户管理' },
      {
        icon: 'setting',
        path: '/admin/generator',
        component: './Admin/Generator',
        name: '生成器管理',
      },
    ],
  },
  { path: '/generator/add', icon: 'plus', component: './Generator/Add', name: '创建生成器' },
  { path: '/generator/update', component: './Generator/Add', hideInMenu: true },
  {
    path: '/generator/detail/:id',
    icon: 'small-dash',
    component: './Generator/Detail',
    name: '生成器详情',
    hideInMenu: true,
  },
  { path: '/', icon: 'home', component: './Index', name: '主页' },
  { path: '*', layout: false, component: './404' },
  {
    path: '/test/file',
    icon: 'home',
    component: './Test/File',
    name: '文件上传下载测试',
    hideInMenu: true,
  },
];
