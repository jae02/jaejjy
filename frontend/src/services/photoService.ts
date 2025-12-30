import axios from 'axios';

const API_BASE_URL = '/api/photos';

export interface PhotoRecord {
  id: number;
  title: string;
  description: string;
  fileName: string;
  filePath: string;
  fileType: string;
  fileSize: number;
  createdAt: string;
  updatedAt: string;
}

export const photoService = {
  // 사진 업로드
  async uploadPhoto(file: File, title?: string, description?: string): Promise<PhotoRecord> {
    const formData = new FormData();
    formData.append('file', file);
    if (title) formData.append('title', title);
    if (description) formData.append('description', description);

    const response = await axios.post<PhotoRecord>(`${API_BASE_URL}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  },

  // 모든 사진 조회
  async getAllPhotos(): Promise<PhotoRecord[]> {
    const response = await axios.get<PhotoRecord[]>(API_BASE_URL);
    return response.data;
  },

  // 사진 ID로 조회
  async getPhotoById(id: number): Promise<PhotoRecord> {
    const response = await axios.get<PhotoRecord>(`${API_BASE_URL}/${id}`);
    return response.data;
  },

  // 사진 이미지 URL 가져오기
  getPhotoImageUrl(id: number): string {
    return `${API_BASE_URL}/${id}/image`;
  },

  // 사진 삭제
  async deletePhoto(id: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/${id}`);
  },
};

