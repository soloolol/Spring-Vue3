<template>
  <v-card title="첨부이미지">
    <v-img :src="imageUrl" />
    <template v-slot:actions>
      <v-btn class="ms-auto" variant="tonal" text="닫기" @click="$emit('onClose')"></v-btn>
    </template>
  </v-card>
</template>
<script setup lang="ts">
defineProps<{
  imageUrl?: string;
}>();
</script>
