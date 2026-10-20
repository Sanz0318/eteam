import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/login' },
    { path: '/login', component: LoginView },
    {
      path: '/',
      component: () => import('../views/LayoutView.vue'),
      children: [
        { path: 'users', component: () => import('../views/UserManageView.vue') },
        { path: 'roles', component: () => import('../views/RoleManageView.vue') },
        { path: 'permissions', component: () => import('../views/PermissionManageView.vue') },
      ],
    },
  ],
})

export default router
