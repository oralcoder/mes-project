// 작업지시 생성 폼 컴포넌트
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllProducts } from '../services/productService';
import { createWorkOrder } from '../services/workOrderService';
import { showSuccess, showError, toastPromise } from '../utils/toast';

function WorkOrderForm() {
  const navigate = useNavigate(); // 페이지 이동을 위한 hook
  
  // 폼 상태 관리
  const [formData, setFormData] = useState({
    orderNo: '',
    productId: '',
    quantity: '',
    orderDate: new Date().toISOString().split('T')[0], // 오늘 날짜
  });
  
  // 제품 목록 상태
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  
  // 폼 검증 에러
  const [validationErrors, setValidationErrors] = useState({});

  // 컴포넌트 마운트 시 제품 목록 로드
  useEffect(() => {
    loadProducts();
  }, []);

  // 제품 목록 로드
  const loadProducts = async () => {
    try {
      const data = await getAllProducts();
      setProducts(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error('제품 목록 로드 실패:', err);
      showError('제품 목록을 불러오는데 실패했습니다.');  
    }
  };

  // 입력값 변경 핸들러
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    
    // 해당 필드의 검증 에러 제거
    if (validationErrors[name]) {
      setValidationErrors(prev => ({
        ...prev,
        [name]: ''
      }));
    }
  };

  // 폼 검증
  const validateForm = () => {
    const errors = {};
    
    if (!formData.orderNo.trim()) {
      errors.orderNo = '작업지시 번호를 입력해주세요.';
    }
    
    if (!formData.productId) {
      errors.productId = '제품을 선택해주세요.';
    }
    
    if (!formData.quantity || formData.quantity <= 0) {
      errors.quantity = '수량은 1 이상이어야 합니다.';
    }
    
    if (!formData.orderDate) {
      errors.orderDate = '지시일을 선택해주세요.';
    }
    
    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  // 폼 제출 핸들러
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // 폼 검증
    if (!validateForm()) {
      //showError('입력값을 확인해주세요.');
      return;
    }
    
    try {
      setLoading(true);
      setError(null);
      
      // API 호출 (productId를 숫자로 변환)
      await createWorkOrder({
        ...formData,
        productId: Number(formData.productId),
        quantity: Number(formData.quantity),
      });
      
      // 성공 시 목록 페이지로 이동
      alert('작업지시가 생성되었습니다.');
      navigate('/work-orders');
      
    } catch (err) {
      console.error('작업지시 생성 실패:', err);
      
      // 서버 에러 메시지 처리
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('작업지시 생성에 실패했습니다.');
      }
    } finally {
      setLoading(false);
    }
  };

  // 취소 핸들러
  const handleCancel = () => {
    if (window.confirm('작성 중인 내용이 삭제됩니다. 취소하시겠습니까?')) {
      navigate('/work-orders');
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        
        {/* 페이지 헤더 */}
        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">작업지시 생성</h1>
          <p className="mt-2 text-sm text-gray-600">
            새로운 작업지시를 생성합니다.
          </p>
        </div>

        {/* 에러 메시지 */}
        {error && (
          <div className="mb-6 bg-red-50 border-l-4 border-red-500 p-4">
            <div className="flex">
              <div className="flex-shrink-0">
                <svg className="h-5 w-5 text-red-400" viewBox="0 0 20 20" fill="currentColor">
                  <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
                </svg>
              </div>
              <div className="ml-3">
                <p className="text-sm text-red-700">{error}</p>
              </div>
            </div>
          </div>
        )}

        {/* 폼 */}
        <form onSubmit={handleSubmit} className="bg-white rounded-lg shadow-sm">
          <div className="px-6 py-6 space-y-6">
            
            {/* 작업지시 번호 */}
            <div>
              <label htmlFor="orderNo" className="block text-sm font-medium text-gray-700 mb-2">
                작업지시 번호 <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                id="orderNo"
                name="orderNo"
                value={formData.orderNo}
                onChange={handleChange}
                placeholder="예: WO-2025-001"
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  validationErrors.orderNo ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {validationErrors.orderNo && (
                <p className="mt-1 text-sm text-red-600">{validationErrors.orderNo}</p>
              )}
            </div>

            {/* 제품 선택 */}
            <div>
              <label htmlFor="productId" className="block text-sm font-medium text-gray-700 mb-2">
                제품 <span className="text-red-500">*</span>
              </label>
              <select
                id="productId"
                name="productId"
                value={formData.productId}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  validationErrors.productId ? 'border-red-500' : 'border-gray-300'
                }`}
              >
                <option value="">제품을 선택하세요</option>
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} ({product.code})
                  </option>
                ))}
              </select>
              {validationErrors.productId && (
                <p className="mt-1 text-sm text-red-600">{validationErrors.productId}</p>
              )}
            </div>

            {/* 수량 */}
            <div>
              <label htmlFor="quantity" className="block text-sm font-medium text-gray-700 mb-2">
                수량 <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                id="quantity"
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                min="1"
                placeholder="예: 1000"
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  validationErrors.quantity ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {validationErrors.quantity && (
                <p className="mt-1 text-sm text-red-600">{validationErrors.quantity}</p>
              )}
            </div>

            {/* 지시일 */}
            <div>
              <label htmlFor="orderDate" className="block text-sm font-medium text-gray-700 mb-2">
                지시일 <span className="text-red-500">*</span>
              </label>
              <input
                type="date"
                id="orderDate"
                name="orderDate"
                value={formData.orderDate}
                onChange={handleChange}
                className={`w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 ${
                  validationErrors.orderDate ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {validationErrors.orderDate && (
                <p className="mt-1 text-sm text-red-600">{validationErrors.orderDate}</p>
              )}
            </div>

          </div>

          {/* 폼 액션 버튼 */}
          <div className="px-6 py-4 bg-gray-50 border-t border-gray-200 flex justify-end space-x-3">
            <button
              type="button"
              onClick={handleCancel}
              className="px-6 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
              disabled={loading}
            >
              취소
            </button>
            <button
              type="submit"
              className="px-6 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors disabled:bg-gray-400 disabled:cursor-not-allowed flex items-center"
              disabled={loading}
            >
              {loading ? (
                <>
                  <svg className="animate-spin -ml-1 mr-2 h-4 w-4 text-white" fill="none" viewBox="0 0 24 24">
                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                  생성 중...
                </>
              ) : (
                '생성'
              )}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
}

export default WorkOrderForm;