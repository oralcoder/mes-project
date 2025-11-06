// 대시보드 페이지 컴포넌트
import { useState, useEffect } from 'react';
import { Line, Bar, Doughnut } from 'react-chartjs-2';
import StatCard from '../components/StatCard';
import LoadingSpinner from '../components/LoadingSpinner';
import { getAllProducts } from '../services/productService';
import { getAllWorkOrders } from '../services/workOrderService';
import {
  getDailyProduction,
  getWorkOrdersByStatus,
  getProductionByProduct,
} from '../services/dashboardService';
import { showError } from '../utils/toast';
import '../utils/chartConfig'; // Chart.js 설정 import
import usePolling from '../hooks/usePolling';

function Dashboard() {
  // 상태 관리
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalProducts: 0,
    totalWorkOrders: 0,
    inProgressOrders: 0,
    completedOrders: 0,
  });
  
  // 차트 데이터
  const [dailyProductionData, setDailyProductionData] = useState([]);
  const [statusData, setStatusData] = useState({});
  const [productionByProduct, setProductionByProduct] = useState([]);

  // 컴포넌트 마운트 시 데이터 로드
  useEffect(() => {
    loadDashboardData();
  }, []);

  // 자동 새로고침 (30초마다)
  usePolling(
    () => {
      if (!loading) {
        loadDashboardData();
      }
    },
    30000,  // 30초
    true    // 항상 활성화
  );

  // 대시보드 데이터 로드
  const loadDashboardData = async () => {
    try {
      setLoading(true);

      // 병렬로 모든 데이터 로드
      const [products, workOrders, dailyProduction, statusCounts, productProduction] = 
        await Promise.all([
          getAllProducts(),
          getAllWorkOrders(),
          getDailyProduction(7),
          getWorkOrdersByStatus(),
          getProductionByProduct(),
        ]);

      // 통계 계산
      const workOrdersArray = Array.isArray(workOrders) ? workOrders : [];
      const inProgress = workOrdersArray.filter(wo => wo.status === 'IN_PROGRESS').length;
      const completed = workOrdersArray.filter(wo => wo.status === 'COMPLETED').length;

      setStats({
        totalProducts: Array.isArray(products) ? products.length : 0,
        totalWorkOrders: workOrdersArray.length,
        inProgressOrders: inProgress,
        completedOrders: completed,
      });

      // 차트 데이터 설정
      setDailyProductionData(dailyProduction);
      setStatusData(statusCounts);
      setProductionByProduct(productProduction);

    } catch (error) {
      console.error('대시보드 데이터 로드 실패:', error);
      showError('대시보드 데이터를 불러오는데 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 로딩 중
  if (loading) {
    return <LoadingSpinner fullScreen message="대시보드를 불러오는 중..." />;
  }

  // 일별 생산량 차트 데이터
  const dailyProductionChartData = {
    labels: dailyProductionData.map(item => {
      const date = new Date(item.date);
      return `${date.getMonth() + 1}/${date.getDate()}`;
    }),
    datasets: [
      {
        label: '생산량',
        data: dailyProductionData.map(item => item.quantity),
        borderColor: 'rgb(59, 130, 246)',
        backgroundColor: 'rgba(59, 130, 246, 0.1)',
        fill: true,
        tension: 0.4, // 곡선
      },
    ],
  };

  // 상태별 작업지시 차트 데이터
  const statusChartData = {
    labels: ['계획', '진행중', '완료', '취소'],
    datasets: [
      {
        label: '작업지시',
        data: [
          statusData.PLANNED || 0,
          statusData.IN_PROGRESS || 0,
          statusData.COMPLETED || 0,
          statusData.CANCELLED || 0,
        ],
        backgroundColor: [
          'rgba(59, 130, 246, 0.8)',   // 파랑 - 계획
          'rgba(234, 179, 8, 0.8)',    // 노랑 - 진행중
          'rgba(34, 197, 94, 0.8)',    // 초록 - 완료
          'rgba(239, 68, 68, 0.8)',    // 빨강 - 취소
        ],
      },
    ],
  };

  // 제품별 생산량 차트 데이터
  const productionByProductChartData = {
    labels: productionByProduct.map(item => item.productName),
    datasets: [
      {
        label: '생산량',
        data: productionByProduct.map(item => item.quantity),
        backgroundColor: [
          'rgba(59, 130, 246, 0.8)',
          'rgba(168, 85, 247, 0.8)',
          'rgba(236, 72, 153, 0.8)',
        ],
      },
    ],
  };

  // 차트 옵션
  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'bottom',
      },
    },
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        
        {/* 페이지 헤더 */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">대시보드</h1>
          <p className="mt-2 text-sm text-gray-600">
            실시간 생산 현황을 확인하세요
          </p>
        </div>

        {/* 통계 카드 그리드 */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <StatCard
            title="전체 제품"
            value={stats.totalProducts}
            icon="📦"
            color="blue"
            subtitle="등록된 제품"
          />
          <StatCard
            title="전체 작업지시"
            value={stats.totalWorkOrders}
            icon="📋"
            color="purple"
            subtitle="총 작업지시 수"
          />
          <StatCard
            title="진행중 작업"
            value={stats.inProgressOrders}
            icon="⚙️"
            color="yellow"
            subtitle="현재 진행중"
          />
          <StatCard
            title="완료된 작업"
            value={stats.completedOrders}
            icon="✅"
            color="green"
            subtitle="완료된 작업"
          />
        </div>

        {/* 차트 그리드 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          
          {/* 일별 생산량 추이 (라인 차트) */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              최근 7일 생산량 추이
            </h2>
            <div style={{ height: '300px' }}>
              <Line data={dailyProductionChartData} options={chartOptions} />
            </div>
          </div>

          {/* 상태별 작업지시 (도넛 차트) */}
          <div className="bg-white rounded-lg shadow-sm p-6">
            <h2 className="text-lg font-semibold text-gray-900 mb-4">
              작업지시 상태 분포
            </h2>
            <div style={{ height: '300px' }}>
              <Doughnut data={statusChartData} options={chartOptions} />
            </div>
          </div>

        </div>

        {/* 제품별 생산량 (바 차트) */}
        <div className="bg-white rounded-lg shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">
            제품별 생산량
          </h2>
          <div style={{ height: '300px' }}>
            <Bar data={productionByProductChartData} options={chartOptions} />
          </div>
        </div>

        {/* 자동 새로고침 안내 */}
        <div className="mt-6 flex items-center justify-center text-sm text-gray-500">
          <svg className="w-4 h-4 mr-2 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          30초마다 자동으로 업데이트됩니다
        </div>

      </div>
    </div>
  );
}

export default Dashboard;