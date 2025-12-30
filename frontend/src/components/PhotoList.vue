<template>
  <div class="photo-list">
    <div class="header">
      <h2>사진 기록</h2>
      <button @click="loadPhotos" class="refresh-btn" :disabled="loading">
        {{ loading ? '로딩 중...' : '새로고침' }}
      </button>
    </div>

    <div v-if="loading && photos.length === 0" class="loading">
      사진을 불러오는 중...
    </div>

    <div v-else-if="photos.length === 0" class="empty">
      등록된 사진이 없습니다.
    </div>

    <div v-else class="photos-grid">
      <div v-for="photo in photos" :key="photo.id" class="photo-card">
        <div class="photo-image">
          <img
            :src="getPhotoImageUrl(photo.id)"
            :alt="photo.title"
            @error="handleImageError"
          />
          <button @click="deletePhoto(photo.id)" class="delete-btn" title="삭제">
            ×
          </button>
        </div>
        <div class="photo-info">
          <h3>{{ photo.title }}</h3>
          <p v-if="photo.description" class="description">{{ photo.description }}</p>
          <div class="meta">
            <span class="date">{{ formatDate(photo.createdAt) }}</span>
            <span class="size">{{ formatFileSize(photo.fileSize) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { photoService, type PhotoRecord } from '@/services/photoService';

const photos = ref<PhotoRecord[]>([]);
const loading = ref(false);

const loadPhotos = async () => {
  loading.value = true;
  try {
    photos.value = await photoService.getAllPhotos();
  } catch (error) {
    console.error('사진 목록 로드 실패:', error);
    alert('사진 목록을 불러오는 중 오류가 발생했습니다.');
  } finally {
    loading.value = false;
  }
};

const getPhotoImageUrl = (id: number): string => {
  return photoService.getPhotoImageUrl(id);
};

const deletePhoto = async (id: number) => {
  if (!confirm('정말 이 사진을 삭제하시겠습니까?')) {
    return;
  }

  try {
    await photoService.deletePhoto(id);
    await loadPhotos(); // 목록 새로고침
  } catch (error) {
    console.error('사진 삭제 실패:', error);
    alert('사진 삭제 중 오류가 발생했습니다.');
  }
};

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement;
  img.src = 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200"%3E%3Crect width="200" height="200" fill="%23ddd"/%3E%3Ctext x="50%25" y="50%25" text-anchor="middle" dy=".3em" fill="%23999"%3E이미지 없음%3C/text%3E%3C/svg%3E';
};

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
};

onMounted(() => {
  loadPhotos();
});

defineExpose({
  loadPhotos,
});
</script>

<style scoped>
.photo-list {
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h2 {
  color: #2c3e50;
  margin: 0;
}

.refresh-btn {
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.refresh-btn:hover:not(:disabled) {
  background-color: #35a372;
}

.refresh-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.loading,
.empty {
  text-align: center;
  padding: 3rem;
  color: #666;
  font-size: 1.1rem;
}

.photos-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 2rem;
}

.photo-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
}

.photo-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.photo-image {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
  background-color: #f5f5f5;
}

.photo-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.delete-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  width: 32px;
  height: 32px;
  background-color: rgba(255, 0, 0, 0.8);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 1.5rem;
  line-height: 1;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.photo-card:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background-color: rgba(255, 0, 0, 1);
}

.photo-info {
  padding: 1rem;
}

.photo-info h3 {
  margin: 0 0 0.5rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.description {
  color: #666;
  font-size: 0.9rem;
  margin: 0.5rem 0;
  line-height: 1.5;
}

.meta {
  display: flex;
  justify-content: space-between;
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #eee;
  font-size: 0.85rem;
  color: #999;
}

@media (max-width: 768px) {
  .photos-grid {
    grid-template-columns: 1fr;
  }
}
</style>

