// React 애플리케이션 진입점
import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'  
import App from './App.jsx'
import './index.css'

// React 애플리케이션을 DOM에 렌더링
ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    {/* BrowserRouter로 전체 앱을 감싸서 라우팅 활성화 */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
)