<template>
  <div class="photo-upload">
    <h2>사진 업로드</h2>
    <form @submit.prevent="handleUpload" class="upload-form">
      <div class="form-group">
        <label for="file">사진 선택</label>
        <input
          type="file"
          id="file"
          ref="fileInput"
          @change="handleFileChange"
          accept="image/*"
          required
        />
        <div v-if="selectedFile" class="preview">
          <img :src="previewUrl" alt="미리보기" />
          <p>{{ selectedFile.name }}</p>
        </div>
      </div>

      <div class="form-group">
        <label for="title">제목</label>
        <input
          type="text"
          id="title"
          v-model="title"
          placeholder="사진 제목을 입력하세요"
        />
      </div>

      <div class="form-group">
        <label for="description">설명</label>
        <textarea
          id="description"
          v-model="description"
          placeholder="사진에 대한 설명을 입력하세요"
          rows="4"
        ></textarea>
      </div>

      <button type="submit" :disabled="uploading || !selectedFile" class="upload-btn">
        {{ uploading ? '업로드 중...' : '업로드' }}
      </button>
    </form>

    <div v-if="uploadSuccess" class="success-message">
      사진이 성공적으로 업로드되었습니다!
    </div>
    <div v-if="uploadError" class="error-message">
      업로드 실패: {{ uploadError }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { photoService } from '@/services/photoService';

const fileInput = ref<HTMLInputElement | null>(null);
const selectedFile = ref<File | null>(null);
const previewUrl = ref<string>('');
const title = ref('');
const description = ref('');
const uploading = ref(false);
const uploadSuccess = ref(false);
const uploadError = ref('');

const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  
  if (file) {
    selectedFile.value = file;
    // 미리보기 URL 생성
    const reader = new FileReader();
    reader.onload = (e) => {
      previewUrl.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const handleUpload = async () => {
  if (!selectedFile.value) return;

  uploading.value = true;
  uploadSuccess.value = false;
  uploadError.value = '';

  try {
    await photoService.uploadPhoto(
      selectedFile.value,
      title.value || undefined,
      description.value || undefined
    );

    uploadSuccess.value = true;
    // 폼 초기화
    selectedFile.value = null;
    previewUrl.value = '';
    title.value = '';
    description.value = '';
    if (fileInput.value) {
      fileInput.value.value = '';
    }

    // 성공 메시지 3초 후 제거
    setTimeout(() => {
      uploadSuccess.value = false;
    }, 3000);

    // 부모 컴포넌트에 업로드 완료 알림 (emit)
    emit('uploaded');
  } catch (error: any) {
    uploadError.value = error.response?.data?.message || '업로드 중 오류가 발생했습니다.';
    setTimeout(() => {
      uploadError.value = '';
    }, 5000);
  } finally {
    uploading.value = false;
  }
};

const emit = defineEmits<{
  uploaded: [];
}>();
</script>

<style scoped>
.photo-upload {
  max-width: 600px;
  margin: 0 auto;
  padding: 2rem;
}

.photo-upload h2 {
  margin-bottom: 1.5rem;
  color: #2c3e50;
}

.upload-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-group label {
  font-weight: 600;
  color: #34495e;
}

.form-group input[type="file"] {
  padding: 0.5rem;
  border: 2px dashed #ddd;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.3s;
}

.form-group input[type="file"]:hover {
  border-color: #42b983;
}

.form-group input[type="text"],
.form-group textarea {
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  font-family: inherit;
}

.form-group textarea {
  resize: vertical;
}

.preview {
  margin-top: 1rem;
  text-align: center;
}

.preview img {
  max-width: 100%;
  max-height: 300px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.preview p {
  margin-top: 0.5rem;
  color: #666;
  font-size: 0.9rem;
}

.upload-btn {
  padding: 0.75rem 2rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s;
}

.upload-btn:hover:not(:disabled) {
  background-color: #35a372;
}

.upload-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.success-message {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #d4edda;
  color: #155724;
  border-radius: 4px;
  border: 1px solid #c3e6cb;
}

.error-message {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f8d7da;
  color: #721c24;
  border-radius: 4px;
  border: 1px solid #f5c6cb;
}
</style>

