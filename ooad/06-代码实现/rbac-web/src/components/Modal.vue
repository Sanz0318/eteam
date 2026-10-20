<template>
  <div
    v-if="visible"
    class="fixed inset-0 z-50 flex items-center justify-center bg-black/40"
    @click.self="$emit('close')"
  >
    <div class="w-full max-w-lg rounded-lg bg-white shadow-xl">
      <div class="flex items-center justify-between border-b px-5 py-3">
        <h3 class="text-lg font-semibold text-gray-800">{{ title }}</h3>
        <button class="text-xl text-gray-400 hover:text-gray-600" @click="$emit('close')">✕</button>
      </div>
      <div class="px-5 py-4">
        <slot />
      </div>
      <div v-if="$slots.footer" class="flex justify-end gap-2 border-t px-5 py-3">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{ visible: boolean; title: string }>()
defineEmits<{ (e: 'close'): void }>()
</script>
