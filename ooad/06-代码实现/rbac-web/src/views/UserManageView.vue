<template>
  <div class="rounded-lg bg-white p-6 shadow-sm">
    <div class="mb-4 flex items-center justify-between">
      <h3 class="text-base font-semibold text-gray-800">用户管理</h3>
      <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="openCreate">
        ＋ 新增用户
      </button>
    </div>

    <table class="w-full text-sm">
      <thead>
        <tr class="border-b text-left text-gray-500">
          <th class="py-2">账号</th>
          <th>昵称</th>
          <th>状态</th>
          <th>角色</th>
          <th class="text-right">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="u in store.users" :key="u.id" class="border-b hover:bg-gray-50">
          <td class="py-2">{{ u.username }}</td>
          <td>{{ u.nickname }}</td>
          <td>
            <span
              class="rounded-full px-2 py-0.5 text-xs"
              :class="u.status === 1 ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-500'"
            >
              {{ u.status === 1 ? '启用' : '停用' }}
            </span>
          </td>
          <td>{{ roleNames(u.roleIds) }}</td>
          <td class="text-right">
            <button class="text-blue-600 hover:underline" @click="openEdit(u)">编辑</button>
            <button class="ml-3 text-blue-600 hover:underline" @click="openAssign(u)">分配角色</button>
            <button class="ml-3 text-red-500 hover:underline" @click="remove(u)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增 / 编辑弹窗 -->
    <Modal :visible="showForm" :title="editing ? '编辑用户' : '新增用户'" @close="showForm = false">
      <div class="space-y-3">
        <div>
          <label class="mb-1 block text-sm text-gray-600">账号</label>
          <input
            v-model="form.username"
            :disabled="!!editing"
            class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none disabled:bg-gray-100"
          />
        </div>
        <div v-if="!editing">
          <label class="mb-1 block text-sm text-gray-600">密码</label>
          <input v-model="form.password" type="password" class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">昵称</label>
          <input v-model="form.nickname" class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none" />
        </div>
        <div v-if="editing">
          <label class="mb-1 block text-sm text-gray-600">状态</label>
          <select v-model="form.status" class="w-full rounded border border-gray-300 px-3 py-2">
            <option :value="1">启用</option>
            <option :value="0">停用</option>
          </select>
        </div>
      </div>
      <template #footer>
        <button class="rounded bg-gray-100 px-4 py-2 text-sm" @click="showForm = false">取消</button>
        <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="save">保存</button>
      </template>
    </Modal>

    <!-- 分配角色弹窗 -->
    <Modal :visible="showAssign" title="分配角色" @close="showAssign = false">
      <Transfer
        :available="availableRoles"
        :selected="selectedRoles"
        @add="assignRoleIds.push($event)"
        @remove="assignRoleIds = assignRoleIds.filter(id => id !== $event)"
        @addAll="assignRoleIds = store.roles.map(r => r.id)"
        @removeAll="assignRoleIds = []"
      />
      <template #footer>
        <button class="rounded bg-gray-100 px-4 py-2 text-sm" @click="showAssign = false">取消</button>
        <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="saveAssign">保存</button>
      </template>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import Modal from '../components/Modal.vue'
import Transfer from '../components/Transfer.vue'
import { store, roleNames, nextId } from '../mock/data'
import type { User } from '../types'

const showForm = ref(false)
const showAssign = ref(false)
const editing = ref<User | null>(null)
const assigning = ref<User | null>(null)
const form = ref({ username: '', password: '', nickname: '', status: 1 })
const assignRoleIds = ref<number[]>([])

function openCreate() {
  editing.value = null
  form.value = { username: '', password: '', nickname: '', status: 1 }
  showForm.value = true
}

function openEdit(u: User) {
  editing.value = u
  form.value = { username: u.username, password: '', nickname: u.nickname, status: u.status }
  showForm.value = true
}

function save() {
  if (editing.value) {
    editing.value.nickname = form.value.nickname
    editing.value.status = form.value.status
  } else {
    store.users.push({
      id: nextId(store.users),
      username: form.value.username,
      nickname: form.value.nickname,
      status: 1,
      roleIds: [],
    })
  }
  showForm.value = false
}

function remove(u: User) {
  if (confirm(`确认删除用户「${u.username}」？`)) {
    store.users = store.users.filter(x => x.id !== u.id)
  }
}

function openAssign(u: User) {
  assigning.value = u
  assignRoleIds.value = [...u.roleIds]
  showAssign.value = true
}

function saveAssign() {
  if (assigning.value) assigning.value.roleIds = [...assignRoleIds.value]
  showAssign.value = false
}

const availableRoles = computed(() =>
  store.roles.filter(r => !assignRoleIds.value.includes(r.id)).map(r => ({ id: r.id, label: r.roleName })),
)
const selectedRoles = computed(() =>
  store.roles.filter(r => assignRoleIds.value.includes(r.id)).map(r => ({ id: r.id, label: r.roleName })),
)
</script>
