// React 애플리케이션 진입점
import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { Toaster } from 'react-hot-toast'  
import App from './App.jsx'
import './index.css'

// React 애플리케이션을 DOM에 렌더링
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* BrowserRouter로 전체 앱을 감싸서 라우팅 활성화 */}
    <BrowserRouter>
      <App />
      {/* 토스트 알림 컨테이너 - 모든 토스트가 여기에 표시됨 */}
      <Toaster 
        position="top-right"  // 화면 우측 상단에 표시
        toastOptions={{
          // 기본 옵션
          duration: 3000,  // 3초 후 자동 사라짐
          style: {
            background: '#363636',
            color: '#fff',
          },
          // 성공 토스트 스타일
          success: {
            duration: 3000,
            iconTheme: {
              primary: '#10b981',
              secondary: '#fff',
            },
          },
          // 에러 토스트 스타일
          error: {
            duration: 4000,  // 에러는 조금 더 오래 표시
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </BrowserRouter>
  </React.StrictMode>,
)