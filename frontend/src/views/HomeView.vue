<template>
  <div class="home">
    <PhotoUpload @uploaded="handlePhotoUploaded" />
    <PhotoList ref="photoListRef" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import PhotoUpload from '@/components/PhotoUpload.vue';
import PhotoList from '@/components/PhotoList.vue';

const photoListRef = ref<InstanceType<typeof PhotoList> | null>(null);

const handlePhotoUploaded = () => {
  // 사진 업로드 후 목록 새로고침
  if (photoListRef.value) {
    photoListRef.value.loadPhotos();
  }
};
</script>

<style scoped>
.home {
  min-height: 100vh;
  background-color: #f5f5f5;
}
</style>
