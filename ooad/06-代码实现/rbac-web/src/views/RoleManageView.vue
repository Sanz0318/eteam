<template>
  <div class="rounded-lg bg-white p-6 shadow-sm">
    <div class="mb-4 flex items-center justify-between">
      <h3 class="text-base font-semibold text-gray-800">角色管理</h3>
      <button class="rounded bg-blue-600 px-4 py-2 text-sm text-white hover:bg-blue-700" @click="openCreate">
        ＋ 新增角色
      </button>
    </div>

    <table class="w-full text-sm">
      <thead>
        <tr class="border-b text-left text-gray-500">
          <th class="py-2">角色名</th>
          <th>编码</th>
          <th>等级</th>
          <th>状态</th>
          <th class="text-right">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="r in store.roles" :key="r.id" class="border-b hover:bg-gray-50">
          <td class="py-2">{{ r.roleName }}</td>
          <td>{{ r.roleCode }}</td>
          <td>
            <span class="rounded bg-slate-100 px-2 py-0.5 text-xs text-slate-600">L{{ r.level }}</span>
          </td>
          <td>
            <span
              class="rounded-full px-2 py-0.5 text-xs"
              :class="r.status === 1 ? 'bg-green-100 text-green-700' : 'bg-gray-200 text-gray-500'"
            >
              {{ r.status === 1 ? '启用' : '停用' }}
            </span>
          </td>
          <td class="text-right">
            <button class="text-blue-600 hover:underline" @click="openEdit(r)">编辑</button>
            <button class="ml-3 text-blue-600 hover:underline" @click="openAssign(r)">分配权限</button>
            <button class="ml-3 text-red-500 hover:underline" @click="remove(r)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增 / 编辑弹窗 -->
    <Modal :visible="showForm" :title="editing ? '编辑角色' : '新增角色'" @close="showForm = false">
      <div class="space-y-3">
        <div>
          <label class="mb-1 block text-sm text-gray-600">角色名</label>
          <input v-model="form.roleName" class="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">编码</label>
          <input v-model="form.roleCode" :disabled="!!editing" class="w-full rounded border border-gray-300 px-3 py-2 disabled:bg-gray-100" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">管理员等级（可自定义，越小越高）</label>
          <input v-model.number="form.level" type="number" class="w-full rounded border border-gray-300 px-3 py-2" />
        </div>
        <div>
          <label class="mb-1 block text-sm text-gray-600">描述</label>
          <input v-model="form.description" class="w-full rounded border border-gray-300 px-3 py-2" />
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

    <!-- 分配权限弹窗 -->
    <Modal :visible="showAssign" title="分配权限" @close="showAssign = false">
      <Transfer
        :available="availablePerms"
        :selected="selectedPerms"
        @add="assignPermIds.push($event)"
        @remove="assignPermIds = assignPermIds.filter(id => id !== $event)"
        @addAll="assignPermIds = store.permissions.map(p => p.id)"
        @removeAll="assignPermIds = []"
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
import { store, nextId } from '../mock/data'
import type { Role } from '../types'

const showForm = ref(false)
const showAssign = ref(false)
const editing = ref<Role | null>(null)
const assigning = ref<Role | null>(null)
const form = ref({ roleName: '', roleCode: '', level: 0, description: '', status: 1 })
const assignPermIds = ref<number[]>([])

function openCreate() {
  editing.value = null
  form.value = { roleName: '', roleCode: '', level: 0, description: '', status: 1 }
  showForm.value = true
}

function openEdit(r: Role) {
  editing.value = r
  form.value = { roleName: r.roleName, roleCode: r.roleCode, level: r.level, description: r.description ?? '', status: r.status }
  showForm.value = true
}

function save() {
  if (editing.value) {
    editing.value.roleName = form.value.roleName
    editing.value.level = form.value.level
    editing.value.description = form.value.description
    editing.value.status = form.value.status
  } else {
    store.roles.push({
      id: nextId(store.roles),
      roleName: form.value.roleName,
      roleCode: form.value.roleCode,
      level: form.value.level,
      description: form.value.description,
      status: 1,
    })
  }
  showForm.value = false
}

function remove(r: Role) {
  if (confirm(`确认删除角色「${r.roleName}」？`)) {
    store.roles = store.roles.filter(x => x.id !== r.id)
    store.users.forEach(u => (u.roleIds = u.roleIds.filter(id => id !== r.id)))
  }
}

function openAssign(r: Role) {
  assigning.value = r
  assignPermIds.value = [...(r.permissionIds ?? [])]
  showAssign.value = true
}

function saveAssign() {
  if (assigning.value) assigning.value.permissionIds = [...assignPermIds.value]
  showAssign.value = false
}

const availablePerms = computed(() =>
  store.permissions.filter(p => !assignPermIds.value.includes(p.id)).map(p => ({ id: p.id, label: `${p.permissionName} (${p.permissionCode})` })),
)
const selectedPerms = computed(() =>
  store.permissions.filter(p => assignPermIds.value.includes(p.id)).map(p => ({ id: p.id, label: `${p.permissionName} (${p.permissionCode})` })),
)
</script>
