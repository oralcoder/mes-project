// API 에러 처리 유틸리티
import { showError } from './toast';

/**
 * API 에러를 사용자 친화적인 메시지로 변환
 * @param {Error} error - 에러 객체
 * @param {string} defaultMessage - 기본 에러 메시지
 * @returns {string} 사용자에게 표시할 메시지
 */
export const getErrorMessage = (error, defaultMessage = '오류가 발생했습니다.') => {
  // 서버 응답이 있는 경우
  if (error.response) {
    const { status, data } = error.response;
    
    // 상태 코드별 처리
    switch (status) {
      case 400:
        // Bad Request: 서버의 검증 에러 메시지 사용
        return data.message || '입력값을 확인해주세요.';
      
      case 401:
        // Unauthorized: 인증 필요
        return '로그인이 필요합니다.';
      
      case 403:
        // Forbidden: 권한 없음
        return '접근 권한이 없습니다.';
      
      case 404:
        // Not Found: 리소스를 찾을 수 없음
        return data.message || '요청한 데이터를 찾을 수 없습니다.';
      
      case 409:
        // Conflict: 중복 등 충돌
        return data.message || '이미 존재하는 데이터입니다.';
      
      case 422:
        // Unprocessable Entity: 검증 실패
        return data.message || '입력값이 올바르지 않습니다.';
      
      case 500:
      case 502:
      case 503:
        // Server Error: 서버 오류
        return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
      
      default:
        return data.message || defaultMessage;
    }
  }
  
  // 네트워크 에러
  if (error.request) {
    return '서버에 연결할 수 없습니다. 네트워크 연결을 확인해주세요.';
  }
  
  // 그 외 에러
  return error.message || defaultMessage;
};

/**
 * API 에러를 처리하고 토스트로 표시
 * @param {Error} error - 에러 객체
 * @param {string} defaultMessage - 기본 에러 메시지
 */
export const handleApiError = (error, defaultMessage) => {
  const message = getErrorMessage(error, defaultMessage);
  showError(message);
  console.error('API Error:', error);
};

/**
 * 폼 검증 에러를 객체로 변환
 * @param {Error} error - 에러 객체
 * @returns {Object} 필드별 에러 메시지 객체
 */
export const getValidationErrors = (error) => {
  if (error.response?.data?.errors) {
    // Spring Boot의 FieldError 형식
    const errors = {};
    error.response.data.errors.forEach(fieldError => {
      errors[fieldError.field] = fieldError.message;
    });
    return errors;
  }
  return {};
};