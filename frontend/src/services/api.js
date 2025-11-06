// API 통신을 위한 axios 설정
import axios from 'axios';

// axios 인스턴스 생성
// 모든 API 요청에 공통으로 적용되는 설정
const api = axios.create({
  // 환경변수에서 API URL 가져오기
  baseURL: import.meta.env.VITE_API_URL,
 
  // 요청 타임아웃 (30초)
  timeout: 30000,
  
  // 공통 헤더 설정
  headers: {
    'Content-Type': 'application/json',
  },
});

// 요청 인터셉터: 요청을 보내기 전에 실행
api.interceptors.request.use(
  (config) => {
    // 요청 로그 출력 (개발 환경에서만)
    if (import.meta.env.DEV) {
      console.log('API 요청:', config.method.toUpperCase(), config.url);
    }
    
    return config;
  },
  (error) => {
    // 요청 에러 처리
    console.error('요청 에러:', error);
    return Promise.reject(error);
  }
);

// 응답 인터셉터: 응답을 받은 후 실행
api.interceptors.response.use(
  (response) => {
    // 응답 로그 출력 (개발 환경에서만)
    if (import.meta.env.DEV) {
      console.log('API 응답:', response.status, response.config.url);
    }
    
    // 응답 데이터만 반환
    return response;
  },
  (error) => {
    // 에러 응답 처리
    if (error.response) {
      // 서버가 응답을 반환한 경우
      console.error('API 에러:', error.response.status, error.response.data);
      
      // 401 Unauthorized: 인증 실패
      if (error.response.status === 401) {
        // 나중에 로그인 페이지로 리다이렉트
        console.log('인증 실패 - 로그인 필요');
      }
      
      // 403 Forbidden: 권한 없음
      if (error.response.status === 403) {
        console.log('권한 없음');
      }
      
      // 404 Not Found: 리소스를 찾을 수 없음
      if (error.response.status === 404) {
        console.log('리소스를 찾을 수 없습니다');
      }
      
      // 500 Internal Server Error: 서버 오류
      if (error.response.status >= 500) {
        console.log('서버 오류가 발생했습니다');
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못한 경우
      console.error('네트워크 에러: 서버로부터 응답이 없습니다');
    } else {
      // 요청 설정 중 에러 발생
      console.error('요청 설정 에러:', error.message);
    }
    
    return Promise.reject(error);
  }
);

// api 인스턴스를 기본 export
export default api;