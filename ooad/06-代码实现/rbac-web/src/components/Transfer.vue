<template>
  <div class="flex items-center gap-3">
    <!-- 可选 -->
    <div class="h-52 flex-1 overflow-y-auto rounded border border-gray-200 p-2">
      <p class="mb-1 text-xs font-semibold text-gray-400">可选</p>
      <div
        v-for="item in available"
        :key="item.id"
        class="flex items-center justify-between rounded px-2 py-1 text-sm hover:bg-blue-50"
      >
        <span>{{ item.label }}</span>
        <button class="text-blue-600 hover:text-blue-800" @click="$emit('add', item.id)">＋</button>
      </div>
    </div>
    <!-- 操作按钮 -->
    <div class="flex flex-col gap-2">
      <button class="rounded bg-gray-200 px-2 py-1 hover:bg-gray-300" title="全部移入" @click="$emit('addAll')">≫</button>
      <button class="rounded bg-gray-200 px-2 py-1 hover:bg-gray-300" title="全部移出" @click="$emit('removeAll')">≪</button>
    </div>
    <!-- 已选 -->
    <div class="h-52 flex-1 overflow-y-auto rounded border border-gray-200 p-2">
      <p class="mb-1 text-xs font-semibold text-gray-400">已选</p>
      <div
        v-for="item in selected"
        :key="item.id"
        class="flex items-center justify-between rounded px-2 py-1 text-sm hover:bg-blue-50"
      >
        <span>{{ item.label }}</span>
        <button class="text-red-500 hover:text-red-700" @click="$emit('remove', item.id)">－</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Item {
  id: number
  label: string
}
defineProps<{ available: Item[]; selected: Item[] }>()
defineEmits<{
  (e: 'add', id: number): void
  (e: 'remove', id: number): void
  (e: 'addAll'): void
  (e: 'removeAll'): void
}>()
</script>
