<template>
  <div class="rounded-lg bg-white p-6 shadow-sm">
    <div class="mb-4 flex items-center justify-between">
      <h3 class="text-base font-semibold text-gray-800">权限管理</h3>
      <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="openCreate">
        ＋ 新增权限
      </button>
    </div>

    <table class="w-full text-sm">
      <thead>
        <tr class="border-b text-left text-gray-500">
          <th class="py-2">权限名</th>
          <th>资源</th>
          <th>操作</th>
          <th>编码</th>
          <th class="text-right">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="p in store.permissions" :key="p.id" class="border-b hover:bg-gray-50">
          <td class="py-2">{{ p.permissionName }}</td>
          <td>{{ p.resource }}</td>
          <td>{{ p.operation }}</td>
          <td>
            <code class="rounded bg-slate-100 px-1.5 py-0.5 text-xs text-slate-700">{{ p.permissionCode }}</code>
          </td>
          <td class="text-right">
            <button class="text-blue-600 hover:underline" @click="openEdit(p)">编辑</button>
            <button class="ml-3 text-red-500 hover:underline" @click="remove(p)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增 / 编辑弹窗 -->
    <Modal :visible="showForm" :title="editing ? '编辑权限' : '新增权限'" @close="showForm = false">
      <div class="space-y-3">
        <div>
          <label class="mb-1 block text-sm text-gray-600">权限名</label>
          <input v-model="form.permissionName" class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none" />
        </div>
        <div class="flex gap-3">
          <div class="flex-1">
            <label class="mb-1 block text-sm text-gray-600">资源</label>
            <input v-model="form.resource" class="w-full rounded border border-gray-300 px-3 py-2" />
          </div>
          <div class="flex-1">
            <label class="mb-1 block text-sm text-gray-600">操作</label>
            <input v-model="form.operation" class="w-full rounded border border-gray-300 px-3 py-2" />
          </div>
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">权限编码</label>
          <input v-model="form.permissionCode" :disabled="!!editing" class="w-full rounded border border-gray-300 px-3 py-2 disabled:bg-gray-100" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">描述</label>
          <input v-model="form.description" class="w-full rounded border border-gray-300 px-3 py-2" />
        </div>
      </div>
      <template #footer>
        <button class="rounded bg-gray-100 px-4 py-2 text-sm" @click="showForm = false">取消</button>
        <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="save">保存</button>
      </template>
    </Modal>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Modal from '../components/Modal.vue'
import { store, nextId } from '../mock/data'
import type { Permission } from '../types'

const showForm = ref(false)
const editing = ref<Permission | null>(null)
const form = ref({ permissionName: '', resource: '', operation: '', permissionCode: '', description: '' })

function openCreate() {
  editing.value = null
  form.value = { permissionName: '', resource: '', operation: '', permissionCode: '', description: '' }
  showForm.value = true
}

function openEdit(p: Permission) {
  editing.value = p
  form.value = {
    permissionName: p.permissionName,
    resource: p.resource,
    operation: p.operation,
    permissionCode: p.permissionCode,
    description: p.description ?? '',
  }
  showForm.value = true
}

function save() {
  if (editing.value) {
    editing.value.permissionName = form.value.permissionName
    editing.value.resource = form.value.resource
    editing.value.operation = form.value.operation
    editing.value.description = form.value.description
  } else {
    store.permissions.push({
      id: nextId(store.permissions),
      permissionName: form.value.permissionName,
      resource: form.value.resource,
      operation: form.value.operation,
      permissionCode: form.value.permissionCode,
      description: form.value.description,
    })
  }
  showForm.value = false
}

function remove(p: Permission) {
  if (confirm(`确认删除权限「${p.permissionName}」？`)) {
    store.permissions = store.permissions.filter(x => x.id !== p.id)
    store.roles.forEach(r => (r.permissionIds = (r.permissionIds ?? []).filter(id => id !== p.id)))
  }
}
</script>
