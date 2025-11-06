// 통계 카드 컴포넌트
// 대시보드의 각 통계 항목을 표시하는 재사용 가능한 카드

/**
 * @param {Object} props
 * @param {string} props.title - 카드 제목
 * @param {string|number} props.value - 표시할 값
 * @param {string} props.icon - 아이콘 (emoji 또는 SVG)
 * @param {string} props.color - 색상 ('blue' | 'green' | 'yellow' | 'red')
 * @param {string} props.subtitle - 부제목 (선택사항)
 */
function StatCard({ title, value, icon, color = 'blue', subtitle }) {
  // 색상별 스타일
  const colorClasses = {
    blue: 'bg-blue-500',
    green: 'bg-green-500',
    yellow: 'bg-yellow-500',
    red: 'bg-red-500',
    purple: 'bg-purple-500',
    indigo: 'bg-indigo-500',
  };

  return (
    <div className="bg-white rounded-lg shadow-sm p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-600 mb-1">
            {title}
          </p>
          <p className="text-3xl font-bold text-gray-900">
            {typeof value === 'number' ? value.toLocaleString() : value}
          </p>
          {subtitle && (
            <p className="text-sm text-gray-500 mt-2">
              {subtitle}
            </p>
          )}
        </div>
        
        {/* 아이콘 영역 */}
        <div className={`flex items-center justify-center w-12 h-12 ${colorClasses[color]} rounded-lg`}>
          <span className="text-2xl text-white">
            {icon}
          </span>
        </div>
      </div>
    </div>
  );
}

export default StatCard;