// 실시간 데이터 폴링을 위한 Custom Hook
import { useEffect, useRef, useState } from 'react';

/**
 * 주기적으로 함수를 실행하는 Hook
 * @param {Function} callback - 실행할 함수
 * @param {number} interval - 실행 간격 (밀리초)
 * @param {boolean} enabled - 폴링 활성화 여부
 */
function usePolling(callback, interval = 5000, enabled = true) {
  const savedCallback = useRef();
  const [isPolling, setIsPolling] = useState(false);

  // callback을 ref에 저장 (최신 상태 유지)
  useEffect(() => {
    savedCallback.current = callback;
  }, [callback]);

  // 폴링 설정
  useEffect(() => {
    // 비활성화 상태면 실행하지 않음
    if (!enabled) {
      setIsPolling(false);
      return;
    }

    setIsPolling(true);

    // 첫 실행
    savedCallback.current?.();

    // 주기적 실행
    const id = setInterval(() => {
      savedCallback.current?.();
    }, interval);

    // cleanup: 컴포넌트 언마운트 시 인터벌 제거
    return () => {
      clearInterval(id);
      setIsPolling(false);
    };
  }, [interval, enabled]);

  return { isPolling };
}

export default usePolling;