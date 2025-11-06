// 작업지시 목록 페이지 컴포넌트
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { getAllWorkOrders, updateWorkOrderStatus } from '../services/workOrderService';
import { showSuccess, showInfo, showError, toastPromise } from '../utils/toast';
import usePolling from '../hooks/usePolling';

function WorkOrderList() {
  // 상태 관리
  const [workOrders, setWorkOrders] = useState([]); // 작업지시 목록
  const [loading, setLoading] = useState(true); // 로딩 상태
  const [error, setError] = useState(null); // 에러 상태
  
  // 필터 상태
  const [statusFilter, setStatusFilter] = useState(''); // 상태 필터
  const [startDate, setStartDate] = useState(''); // 시작 날짜
  const [endDate, setEndDate] = useState(''); // 종료 날짜

  // 폴링 활성화 상태 (토글 가능)
  const [pollingEnabled, setPollingEnabled] = useState(false);

  // 컴포넌트 마운트 시 및 필터 변경 시 작업지시 목록 로드
  useEffect(() => {
    loadWorkOrders();
  }, [statusFilter, startDate, endDate]); // 필터가 변경될 때마다 재조회

  // 폴링 설정 (5초마다 자동 새로고침)
  const { isPolling } = usePolling(
    () => {
      // 로딩 중이 아닐 때만 폴링 실행
      if (!loading) {
        loadWorkOrders();
      }
    },
    5000,  // 5초 간격
    pollingEnabled  // 활성화 여부
  );

  // 작업지시 목록 로드 함수
  const loadWorkOrders = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // 쿼리 파라미터 구성
      const params = {};
      if (statusFilter) params.status = statusFilter;
      if (startDate) params.startDate = startDate;
      if (endDate) params.endDate = endDate;
      
      // API 호출
      const data = await getAllWorkOrders(params);
      
      // 안전하게 배열로 변환
      if (Array.isArray(data)) {
        setWorkOrders(data);
      } else {
        console.warn('응답이 배열 형식이 아닙니다:', data);
        setWorkOrders([]);
      }
    } catch (err) {
      console.error('작업지시 목록 로드 실패:', err);
      setError('작업지시 목록을 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 상태 변경 핸들러
  const handleStatusChange = async (id, newStatus) => {
    try {
      await updateWorkOrderStatus(id, newStatus);
      // 목록 새로고침
      loadWorkOrders();
    } catch (err) {
      alert('상태 변경에 실패했습니다.');
    }
  };

  // 상태 뱃지 색상 반환
  const getStatusBadgeColor = (status) => {
    switch (status) {
      case 'PLANNED':
        return 'bg-blue-100 text-blue-800';
      case 'IN_PROGRESS':
        return 'bg-yellow-100 text-yellow-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // 상태 한글 변환
  const getStatusText = (status) => {
    switch (status) {
      case 'PLANNED':
        return '계획';
      case 'IN_PROGRESS':
        return '진행중';
      case 'COMPLETED':
        return '완료';
      case 'CANCELLED':
        return '취소';
      default:
        return status;
    }
  };

  // 필터 초기화
  const handleResetFilters = () => {
    setStatusFilter('');
    setStartDate('');
    setEndDate('');
  };

  // 폴링 토글 핸들러
  const handleTogglePolling = () => {
    setPollingEnabled(!pollingEnabled);
    if (!pollingEnabled) {
      showSuccess('자동 새로고침이 활성화되었습니다.');
    } else {
      showInfo('자동 새로고침이 비활성화되었습니다.');
    }
  };


  // 로딩 중 UI
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="text-center">
          <div className="inline-block w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
          <p className="mt-4 text-gray-600">작업지시 목록을 불러오는 중...</p>
        </div>
      </div>
    );
  }

  // 에러 발생 시 UI
  if (error) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gray-50">
        <div className="text-center">
          <div className="inline-flex items-center justify-center w-16 h-16 mb-4 bg-red-100 rounded-full">
            <svg className="w-8 h-8 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
          <p className="mb-4 text-lg text-red-600">{error}</p>
          <button 
            onClick={loadWorkOrders}
            className="px-6 py-2 text-white bg-red-500 rounded-lg hover:bg-red-600 transition-colors"
          >
            다시 시도
          </button>
        </div>
      </div>
    );
  }

  // 메인 UI
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        
        {/* 페이지 헤더 */}
        <div className="flex justify-between items-center mb-6">
          <div className="flex items-center space-x-4">
            <h1 className="text-3xl font-bold text-gray-900">작업지시 관리</h1>
            
            {/* 폴링 상태 표시 */}
            {isPolling && (
              <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                <span className="w-2 h-2 mr-1.5 bg-green-600 rounded-full animate-pulse"></span>
                실시간 업데이트 중
              </span>
            )}
          </div>

          <div className="flex items-center space-x-3">
            {/* 자동 새로고침 토글 버튼 */}
            <button
              onClick={handleTogglePolling}
              className={`px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
                pollingEnabled
                  ? 'bg-green-100 text-green-700 hover:bg-green-200'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
            >
              <svg className="w-5 h-5 inline-block mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
              </svg>
              {pollingEnabled ? '자동 새로고침 켜짐' : '자동 새로고침 꺼짐'}
            </button>

            {/* 작업지시 생성 버튼 */}
            <Link 
              to="/work-orders/new"
              className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
            >
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M12 4v16m8-8H4" />
              </svg>
              작업지시 생성
            </Link>
          </div>
        </div>

        {/* 필터 영역 */}
        <div className="bg-white rounded-lg shadow-sm p-6 mb-6">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            {/* 상태 필터 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                상태
              </label>
              <select
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                <option value="">전체</option>
                <option value="PLANNED">계획</option>
                <option value="IN_PROGRESS">진행중</option>
                <option value="COMPLETED">완료</option>
                <option value="CANCELLED">취소</option>
              </select>
            </div>

            {/* 시작 날짜 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                시작 날짜
              </label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* 종료 날짜 */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                종료 날짜
              </label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>

            {/* 초기화 버튼 */}
            <div className="flex items-end">
              <button
                onClick={handleResetFilters}
                className="w-full px-4 py-2 text-gray-700 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
              >
                필터 초기화
              </button>
            </div>
          </div>
        </div>

        {/* 작업지시 목록 테이블 */}
        {workOrders.length === 0 ? (
          // 작업지시가 없을 때
          <div className="bg-white rounded-lg shadow-sm p-12 text-center">
            <div className="inline-flex items-center justify-center w-16 h-16 mb-4 bg-gray-100 rounded-full">
              <svg className="w-8 h-8 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
              </svg>
            </div>
            <p className="text-lg text-gray-600 mb-4">등록된 작업지시가 없습니다.</p>
            <Link 
              to="/work-orders/new"
              className="inline-block px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              첫 작업지시 생성하기
            </Link>
          </div>
        ) : (
          // 작업지시가 있을 때
          <>
            {/* 테이블 카드 */}
            <div className="bg-white rounded-lg shadow-sm overflow-hidden">
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  {/* 테이블 헤더 */}
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        지시번호
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        제품명
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        수량
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        상태
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        지시일
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        작업
                      </th>
                    </tr>
                  </thead>
                  
                  {/* 테이블 바디 */}
                  <tbody className="bg-white divide-y divide-gray-200">
                    {workOrders.map((workOrder) => (
                      <tr 
                        key={workOrder.id}
                        className="hover:bg-gray-50 transition-colors"
                      >
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="text-sm font-medium text-gray-900">
                            {workOrder.orderNo}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {workOrder.productName}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {workOrder.quantity.toLocaleString()}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-2 py-1 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadgeColor(workOrder.status)}`}>
                            {getStatusText(workOrder.status)}
                          </span>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                          {new Date(workOrder.orderDate).toLocaleDateString('ko-KR')}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                          {/* 상태 변경 버튼 */}
                          {workOrder.status === 'PLANNED' && (
                            <button
                              onClick={() => handleStatusChange(workOrder.id, 'IN_PROGRESS')}
                              className="text-blue-600 hover:text-blue-900 mr-3"
                            >
                              시작
                            </button>
                          )}
                          {workOrder.status === 'IN_PROGRESS' && (
                            <button
                              onClick={() => handleStatusChange(workOrder.id, 'COMPLETED')}
                              className="text-green-600 hover:text-green-900 mr-3"
                            >
                              완료
                            </button>
                          )}
                          <Link
                            to={`/work-orders/${workOrder.id}`}
                            className="text-indigo-600 hover:text-indigo-900"
                          >
                            상세
                          </Link>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>

            {/* 작업지시 개수 표시 */}
            <div className="mt-4 text-sm text-gray-600 text-right">
              총 <span className="font-semibold text-gray-900">{workOrders.length}</span>개의 작업지시
            </div>
          </>
        )}
      </div>
    </div>
  );
}

export default WorkOrderList;