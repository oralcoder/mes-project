// 작업지시 API 호출 함수 모음
import api from './api';
import { handleApiError } from '../utils/errorHandler';


// WorkOrder API의 기본 경로
const WORK_ORDER_BASE_URL = '/work-orders';

/**
 * 전체 작업지시 목록 조회
 * @param {Object} params - 쿼리 파라미터
 * @param {string} params.status - 상태 필터 (PLANNED, IN_PROGRESS, COMPLETED)
 * @param {string} params.startDate - 시작 날짜 (YYYY-MM-DD)
 * @param {string} params.endDate - 종료 날짜 (YYYY-MM-DD)
 * @returns {Promise} 작업지시 목록 배열
 */
export const getAllWorkOrders = async (params = {}) => {
  try {
    const response = await api.get(WORK_ORDER_BASE_URL, { params });
    return response.data;
  } catch (error) {
    handleApiError(error, '작업지시 목록을 불러오는데 실패했습니다.');
    throw error;
  }
};

/**
 * 작업지시 단건 조회
 * @param {number} id - 작업지시 ID
 * @returns {Promise} 작업지시 객체
 */
export const getWorkOrderById = async (id) => {
  try {
    const response = await api.get(`${WORK_ORDER_BASE_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`작업지시 조회 실패 (ID: ${id}):`, error);
    throw error;
  }
};

/**
 * 지시번호로 작업지시 조회
 * @param {string} orderNo - 작업지시 번호
 * @returns {Promise} 작업지시 객체
 */
export const getWorkOrderByOrderNo = async (orderNo) => {
  try {
    const response = await api.get(`${WORK_ORDER_BASE_URL}/order-no/${orderNo}`);
    return response.data;
  } catch (error) {
    console.error(`작업지시 조회 실패 (지시번호: ${orderNo}):`, error);
    throw error;
  }
};

/**
 * 작업지시 생성
 * @param {Object} workOrderData - 작업지시 데이터
 * @param {string} workOrderData.orderNo - 작업지시 번호
 * @param {number} workOrderData.productId - 제품 ID
 * @param {number} workOrderData.quantity - 수량
 * @param {string} workOrderData.orderDate - 지시 날짜 (YYYY-MM-DD)
 * @returns {Promise} 생성된 작업지시 객체
 */
export const createWorkOrder = async (workOrderData) => {
  try {
    const response = await api.post(WORK_ORDER_BASE_URL, workOrderData);
    return response.data;
  } catch (error) {
    handleApiError(error, '작업지시 생성에 실패했습니다.');
    throw error;
  }
};

/**
 * 작업지시 상태 변경
 * @param {number} id - 작업지시 ID
 * @param {string} status - 변경할 상태 (PLANNED, IN_PROGRESS, COMPLETED, CANCELLED)
 * @returns {Promise} 수정된 작업지시 객체
 */
export const updateWorkOrderStatus = async (id, status) => {
  try {
    const response = await api.patch(`${WORK_ORDER_BASE_URL}/${id}/status`, { status });
    return response.data;
  } catch (error) {
    handleApiError(error, `작업지시(ID: ${id}) 상태 변경에 실패했습니다.`);
    throw error;
  }
};

/**
 * 작업지시 삭제
 * @param {number} id - 작업지시 ID
 * @returns {Promise}
 */
export const deleteWorkOrder = async (id) => {
  try {
    await api.delete(`${WORK_ORDER_BASE_URL}/${id}`);
  } catch (error) {
    console.error(`작업지시 삭제 실패 (ID: ${id}):`, error);
    throw error;
  }
};