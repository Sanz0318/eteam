<template>
  <div class="flex min-h-screen items-center justify-center bg-gray-100">
    <div class="w-full max-w-sm rounded-lg bg-white p-8 shadow-lg">
      <h1 class="mb-1 text-center text-2xl font-bold text-gray-800">统一权限管理系统</h1>
      <p class="mb-6 text-center text-xs text-gray-400">RBAC0 · 界面原型</p>
      <form class="space-y-4" @submit.prevent="login">
        <div>
          <label class="mb-1 block text-sm text-gray-600">账号</label>
          <input
            v-model="username"
            class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            placeholder="请输入账号"
          />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">密码</label>
          <input
            v-model="password"
            type="password"
            class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
            placeholder="请输入密码"
          />
        </div>
        <button
          type="submit"
          class="w-full rounded bg-blue-600 py-2 text-white transition hover:bg-blue-700"
        >
          登 录
        </button>
      </form>
      <p class="mt-4 text-center text-xs text-gray-400">演示账号：admin / 123456</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref('admin')
const password = ref('123456')

function login() {
  // 静态原型：不做真实校验，直接进入系统
  router.push('/users')
}
</script>
