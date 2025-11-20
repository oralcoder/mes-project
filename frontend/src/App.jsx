// 최상위 컴포넌트
import { Routes, Route, Link, useLocation } from 'react-router-dom';
import ProductList from './pages/ProductList';
import WorkOrderList from './pages/WorkOrderList';
import WorkOrderForm from './pages/WorkOrderForm';
import Dashboard from './pages/Dashboard';
import DefectInspection from './pages/DefectInspection.jsx';
import ChipDetection from './pages/ChipDetection';
import LLMChat from './pages/LLMChat';



function App() {
  const location = useLocation(); // 현재 경로 정보
  
  // 현재 경로가 활성화된 메뉴인지 확인
  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  };
  
  return (
    <div className="min-h-screen bg-gray-50">
      {/* 헤더 */}
      <header className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            {/* 로고 */}
            <Link to="/" className="flex items-center">
              <h1 className="text-xl font-bold text-blue-600">MES 시스템</h1>
            </Link>
            
            {/* 네비게이션 */}
            <nav className="flex space-x-4">
              <Link 
                to="/llm_chat" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/llm_chat')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                LLM CHAT
              </Link>
              <Link 
                to="/defect_inspection" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/defect_inspection')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                결함 검사
              </Link>
              <Link 
                to="/chip_detection" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/chip_detection')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                Chip 탐지
              </Link>
              <Link 
                to="/dashboard" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/dashboard')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                대시보드
              </Link>              
              <Link 
                to="/products" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/products')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                제품
              </Link>
              <Link 
                to="/work-orders" 
                className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                  isActive('/work-orders')
                    ? 'text-white bg-blue-600'
                    : 'text-gray-700 hover:text-gray-900 hover:bg-gray-100'
                }`}
              >
                작업지시
              </Link>
              <a 
                href="#" 
                className="px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
              >
                생산실적
              </a>
              <a 
                href="#" 
                className="px-3 py-2 text-sm font-medium text-gray-700 hover:text-gray-900 hover:bg-gray-100 rounded-md transition-colors"
              >
                대시보드
              </a>
            </nav>
          </div>
        </div>
      </header>

      {/* 메인 컨텐츠 - 라우팅 */}
      <main>
        <Routes>
          {/* 기본 경로: 제품 목록으로 리다이렉트 */}
          <Route path="/" element={<ProductList />} />

          {/* 결함 검사 */}
          <Route path="/defect_inspection" element={<DefectInspection />} />

          {/* Chip 탐지 */}
          <Route path="/chip_detection" element={<ChipDetection />} />   

          {/* LLM CHAT */}        
          <Route path="/llm_chat" element={<LLMChat />} />        

          {/* 대시보드 */}
          <Route path="/dashboard" element={<Dashboard />} />
          
          {/* 제품 관련 경로 */}
          <Route path="/products" element={<ProductList />} />
          
          {/* 작업지시 관련 경로 */}
          <Route path="/work-orders" element={<WorkOrderList />} />
          <Route path="/work-orders/new" element={<WorkOrderForm />} />
          
          {/* 404 페이지 (선택사항) */}
          <Route path="*" element={
            <div className="flex items-center justify-center min-h-screen">
              <div className="text-center">
                <h1 className="text-6xl font-bold text-gray-900">404</h1>
                <p className="mt-4 text-xl text-gray-600">페이지를 찾을 수 없습니다.</p>
                <Link to="/" className="mt-6 inline-block px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">
                  홈으로 돌아가기
                </Link>
              </div>
            </div>
          } />
        </Routes>
      </main>
    </div>
  );
}

export default App;