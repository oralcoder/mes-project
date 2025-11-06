// Chart.js 설정 및 등록
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler,
} from 'chart.js';

// Chart.js 컴포넌트 등록
ChartJS.register(
  CategoryScale,    // x축 카테고리 스케일
  LinearScale,      // y축 선형 스케일
  PointElement,     // 점 요소
  LineElement,      // 선 요소
  BarElement,       // 막대 요소
  ArcElement,       // 원형 요소 (파이, 도넛 차트)
  Title,            // 제목
  Tooltip,          // 툴팁
  Legend,           // 범례
  Filler            // 영역 채우기
);

// 기본 차트 옵션
export const defaultChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
    },
  },
};