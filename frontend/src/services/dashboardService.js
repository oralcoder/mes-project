// 대시보드 통계 API 호출 함수
import api from './api';

/**
 * 전체 통계 조회
 * @returns {Promise} 통계 객체
 */
export const getDashboardStats = async () => {
  try {
    // 실제 통계 API가 있다면 사용
    // const response = await api.get('/dashboard/stats');
    // return response.data;
    
    // 임시 데이터 (백엔드에 통계 API가 없는 경우)
    return {
      totalProducts: 0,
      totalWorkOrders: 0,
      inProgressOrders: 0,
      completedOrders: 0,
    };
  } catch (error) {
    console.error('통계 조회 실패:', error);
    throw error;
  }
};

/**
 * 일별 생산량 추이 조회
 * @param {number} days - 최근 며칠
 * @returns {Promise} 일별 생산량 배열
 */
export const getDailyProduction = async (days = 7) => {
  try {
    // 실제 API가 있다면 사용
    // const response = await api.get(`/dashboard/daily-production?days=${days}`);
    // return response.data;
    
    // 임시 데이터 생성
    const today = new Date();
    const data = [];
    
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      
      data.push({
        date: date.toISOString().split('T')[0],
        quantity: Math.floor(Math.random() * 500) + 500, // 500-1000 사이 랜덤
      });
    }
    
    return data;
  } catch (error) {
    console.error('일별 생산량 조회 실패:', error);
    throw error;
  }
};

/**
 * 상태별 작업지시 집계
 * @returns {Promise} 상태별 집계 객체
 */
export const getWorkOrdersByStatus = async () => {
  try {
    // 실제 API가 있다면 사용
    // const response = await api.get('/dashboard/work-orders-by-status');
    // return response.data;
    
    // 임시 데이터
    return {
      PLANNED: Math.floor(Math.random() * 20) + 10,
      IN_PROGRESS: Math.floor(Math.random() * 15) + 5,
      COMPLETED: Math.floor(Math.random() * 50) + 30,
      CANCELLED: Math.floor(Math.random() * 5),
    };
  } catch (error) {
    console.error('상태별 작업지시 조회 실패:', error);
    throw error;
  }
};

/**
 * 제품별 생산량 집계
 * @returns {Promise} 제품별 생산량 배열
 */
export const getProductionByProduct = async () => {
  try {
    // 실제 API가 있다면 사용
    // const response = await api.get('/dashboard/production-by-product');
    // return response.data;
    
    // 임시 데이터
    return [
      { productName: '온도센서 모듈 TS-100', quantity: 1500 },
      { productName: '압력센서 모듈 PS-200', quantity: 1200 },
      { productName: '제어 보드 CB-300', quantity: 800 },
    ];
  } catch (error) {
    console.error('제품별 생산량 조회 실패:', error);
    throw error;
  }
};