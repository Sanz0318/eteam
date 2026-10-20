<template>
  <div class="flex min-h-screen bg-gray-50">
    <!-- 侧边栏 -->
    <aside class="flex w-56 flex-col bg-slate-800 text-white">
      <div class="px-5 py-5 text-lg font-bold">权限管理系统</div>
      <nav class="flex-1">
        <router-link
          v-for="item in menus"
          :key="item.path"
          :to="item.path"
          class="block px-5 py-3 text-sm transition hover:bg-slate-700"
          :class="{ 'bg-blue-600 hover:bg-blue-600': isActive(item.path) }"
        >
          {{ item.name }}
        </router-link>
      </nav>
      <div class="px-5 py-4 text-xs text-slate-400">RBAC0 框架演示</div>
    </aside>

    <!-- 主区域 -->
    <div class="flex flex-1 flex-col">
      <header class="flex items-center justify-between border-b bg-white px-6 py-3">
        <h2 class="text-lg font-semibold text-gray-800">{{ currentTitle }}</h2>
        <div class="flex items-center gap-3 text-sm">
          <span class="text-gray-600">管理员</span>
          <button class="rounded bg-gray-100 px-3 py-1 transition hover:bg-gray-200" @click="logout">
            退出
          </button>
        </div>
      </header>
      <main class="flex-1 p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menus = [
  { path: '/users', name: '用户管理' },
  { path: '/roles', name: '角色管理' },
  { path: '/permissions', name: '权限管理' },
]

const isActive = (p: string) => route.path === p
const currentTitle = computed(() => menus.find(m => m.path === route.path)?.name ?? '')

function logout() {
  router.push('/login')
}
</script>
