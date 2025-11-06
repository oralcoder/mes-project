// 제품 API 호출 함수 모음
import api from './api';

// Product API의 기본 경로
const PRODUCT_BASE_URL = '/products';

/**
 * 전체 제품 목록 조회
 * @returns {Promise} 제품 목록 배열
 */
export const getAllProducts = async () => {
  try {
    const response = await api.get(PRODUCT_BASE_URL);
    // 응답 구조 확인 후 적절히 처리
    console.log('API 응답:', response.data); // 디버깅용
    
    return response.data;
  } catch (error) {
    console.error('제품 목록 조회 실패:', error);
    throw error;
  }
};

/**
 * 제품 단건 조회
 * @param {number} id - 제품 ID
 * @returns {Promise} 제품 객체
 */
export const getProductById = async (id) => {
  try {
    const response = await api.get(`${PRODUCT_BASE_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`제품 조회 실패 (ID: ${id}):`, error);
    throw error;
  }
};

/**
 * 제품 생성
 * @param {Object} productData - 제품 데이터
 * @param {string} productData.name - 제품명
 * @param {string} productData.code - 제품 코드
 * @param {string} productData.spec - 제품 사양
 * @param {string} productData.unit - 단위
 * @returns {Promise} 생성된 제품 객체
 */
export const createProduct = async (productData) => {
  try {
    const response = await api.post(PRODUCT_BASE_URL, productData);
    return response.data;
  } catch (error) {
    console.error('제품 생성 실패:', error);
    throw error;
  }
};

/**
 * 제품 수정
 * @param {number} id - 제품 ID
 * @param {Object} productData - 수정할 제품 데이터
 * @returns {Promise} 수정된 제품 객체
 */
export const updateProduct = async (id, productData) => {
  try {
    const response = await api.put(`${PRODUCT_BASE_URL}/${id}`, productData);
    return response.data;
  } catch (error) {
    console.error(`제품 수정 실패 (ID: ${id}):`, error);
    throw error;
  }
};

/**
 * 제품 삭제
 * @param {number} id - 제품 ID
 * @returns {Promise}
 */
export const deleteProduct = async (id) => {
  try {
    await api.delete(`${PRODUCT_BASE_URL}/${id}`);
  } catch (error) {
    console.error(`제품 삭제 실패 (ID: ${id}):`, error);
    throw error;
  }
};