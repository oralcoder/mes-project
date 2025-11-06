// 토스트 알림 유틸리티 함수
import toast from 'react-hot-toast';

/**
 * 성공 토스트
 * @param {string} message - 표시할 메시지
 */
export const showSuccess = (message) => {
  toast.success(message);
};

/**
 * 에러 토스트
 * @param {string} message - 표시할 메시지
 */
export const showError = (message) => {
  toast.error(message);
};

/**
 * 정보 토스트
 * @param {string} message - 표시할 메시지
 */
export const showInfo = (message) => {
  toast(message, {
    icon: 'ℹ️',
  });
};

/**
 * 로딩 토스트
 * @param {string} message - 표시할 메시지
 * @returns {string} toast id (나중에 dismiss 하기 위해)
 */
export const showLoading = (message) => {
  return toast.loading(message);
};

/**
 * 특정 토스트 닫기
 * @param {string} toastId - 닫을 토스트의 ID
 */
export const dismissToast = (toastId) => {
  toast.dismiss(toastId);
};

/**
 * 프로미스를 토스트로 감싸기
 * 로딩 → 성공/실패 자동 처리
 * @param {Promise} promise - 실행할 프로미스
 * @param {Object} messages - 메시지 객체
 * @param {string} messages.loading - 로딩 메시지
 * @param {string} messages.success - 성공 메시지
 * @param {string} messages.error - 에러 메시지
 */
export const toastPromise = (promise, messages) => {
  return toast.promise(promise, messages);
};

// 사용 예시 (주석)
/*
// 기본 사용법
showSuccess('저장되었습니다!');
showError('오류가 발생했습니다.');
showInfo('알림 메시지입니다.');

// 프로미스 사용법
toastPromise(
  saveData(),
  {
    loading: '저장 중...',
    success: '저장되었습니다!',
    error: '저장에 실패했습니다.'
  }
);

// 수동 로딩 제어
const toastId = showLoading('처리 중...');
// ... 작업 수행
dismissToast(toastId);
showSuccess('완료!');
*/