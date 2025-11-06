// 로딩 스피너 컴포넌트
// 재사용 가능한 로딩 표시 컴포넌트

/**
 * @param {Object} props
 * @param {string} props.size - 크기 ('sm' | 'md' | 'lg')
 * @param {string} props.message - 표시할 메시지
 * @param {boolean} props.fullScreen - 전체 화면 표시 여부
 */
function LoadingSpinner({ 
  size = 'md', 
  message = '로딩 중...', 
  fullScreen = false 
}) {
  // 크기에 따른 스피너 클래스
  const sizeClasses = {
    sm: 'w-6 h-6 border-2',
    md: 'w-12 h-12 border-4',
    lg: 'w-16 h-16 border-4',
  };

  // 스피너 UI
  const spinner = (
    <div className="text-center">
      <div 
        className={`${sizeClasses[size]} border-blue-500 border-t-transparent rounded-full animate-spin mx-auto`}
      ></div>
      {message && (
        <p className="mt-4 text-gray-600">{message}</p>
      )}
    </div>
  );

  // 전체 화면 모드
  if (fullScreen) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        {spinner}
      </div>
    );
  }

  // 인라인 모드
  return (
    <div className="flex items-center justify-center p-8">
      {spinner}
    </div>
  );
}

export default LoadingSpinner;