import React, { useState, useEffect, useRef } from 'react';
import * as tf from '@tensorflow/tfjs';

const ChipDetection = () => {
  const [model, setModel] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isDetecting, setIsDetecting] = useState(false);
  const [fps, setFps] = useState(0);
  
  const videoRef = useRef(null);
  const canvasRef = useRef(null);
  const streamRef = useRef(null);
  const animationRef = useRef(null);

  // 클래스 레이블 (실제 모델 클래스에 맞게 수정)
  const CLASS_LABELS = ['DIP-14', 'QFN', 'SOP-16', 'SOP-8', 'SOT-223', 'TO-252', 'TSSOP-8']; // 실제 클래스명으로 변경

  // 모델 로드
  useEffect(() => {
    const loadModel = async () => {
      try {
        setLoading(true);
        const loadedModel = await tf.loadGraphModel('/chip_detection_model/model.json');
        setModel(loadedModel);
        console.log('모델 로드 완료');
      } catch (error) {
        console.error('모델 로드 실패:', error);
        alert('모델 로드에 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadModel();
  }, []);

  // 웹캠 시작
  const startWebcam = async () => {
  try {
    const stream = await navigator.mediaDevices.getUserMedia({
      video: { width: 640, height: 480 }
    });
    
    if (videoRef.current) {
      videoRef.current.srcObject = stream;
      streamRef.current = stream;
      
      // 비디오가 준비될 때까지 대기
      videoRef.current.onloadedmetadata = () => {
        videoRef.current.play();
        setIsDetecting(true);
      };
    }
  } catch (error) {
    console.error('웹캠 접근 실패:', error);
    alert('웹캠에 접근할 수 없습니다.');
  }
};

  // 웹캠 중지
  const stopWebcam = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach(track => track.stop());
      streamRef.current = null;
    }
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current);
    }
    if (canvasRef.current) {
      const ctx = canvasRef.current.getContext('2d');
      ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
    }
    setIsDetecting(false);
  };

// 객체 탐지 루프
const detectObjects = async () => {
  if (!videoRef.current || !canvasRef.current || !model || !isDetecting) {
    return;
  }

  const video = videoRef.current;
  const canvas = canvasRef.current;
  const ctx = canvas.getContext('2d');

  if (video.readyState === video.HAVE_ENOUGH_DATA) {
    const startTime = performance.now();

    const image = tf.tidy(() => {
      return tf.image.resizeBilinear(tf.browser.fromPixels(video), [640, 640])
        .div(255.0)
        .expandDims(0);
    });

    const predictions = model.execute(image);
    const output = predictions.arraySync()[0]; // [11, 8400]
    
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    const confThreshold = 0.6;
    
    for (let i = 0; i < 8400; i++) {
      // 클래스 확률 최대값 찾기
      let maxConf = 0;
      let maxClassId = 0;
      
      for (let j = 0; j < 7; j++) {
        const conf = output[4 + j][i];
        if (conf > maxConf) {
          maxConf = conf;
          maxClassId = j;
        }
      }
      
      if (maxConf > confThreshold) {
        // center format에서 corner format으로 변환
        const cx = output[0][i];
        const cy = output[1][i];
        const w = output[2][i];
        const h = output[3][i];
        
        let x1 = cx - w / 2;
        let y1 = cy - h / 2;
        let x2 = cx + w / 2;
        let y2 = cy + h / 2;
        
        // 이전 코드처럼 캔버스 크기에 맞게 스케일링
        x1 = x1 * canvas.width / 640;
        x2 = x2 * canvas.width / 640;
        y1 = y1 * canvas.height / 640;
        y2 = y2 * canvas.height / 640;
        
        const width = x2 - x1;
        const height = y2 - y1;
        const label = CLASS_LABELS[maxClassId] || `Class ${maxClassId}`;
        
        // 박스 그리기
        ctx.beginPath();
        ctx.lineWidth = 2;
        ctx.strokeStyle = "#00ff00";
        ctx.strokeRect(x1, y1, width, height);
        
        ctx.fillStyle = "#00ff00";
        ctx.fillRect(x1, y1 - 20, width, 20);
        
        ctx.font = "16px Arial";
        ctx.fillStyle = "#ffffff";
        ctx.fillText(`${label}: ${maxConf.toFixed(2)}`, x1, y1);
      }
    }

    tf.dispose(predictions);
    tf.dispose(image);

    const endTime = performance.now();
    setFps(Math.round(1000 / (endTime - startTime)));
  }

  animationRef.current = requestAnimationFrame(detectObjects);
};

  // 탐지 시작/중지
  useEffect(() => {
    if (isDetecting && model) {
      detectObjects();
    }
    
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, [isDetecting, model]);

  // 컴포넌트 언마운트 시 정리
  useEffect(() => {
    return () => {
      stopWebcam();
    };
  }, []);

  return (
    <div className="container mx-auto p-6">
      <h1 className="text-3xl font-bold mb-6">실시간 칩 탐지</h1>

      {/* 모델 로딩 상태 */}
      {loading && (
        <div className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded mb-4">
          모델을 로딩 중입니다...
        </div>
      )}

      {/* 컨트롤 버튼 */}
      <div className="mb-6 flex gap-4 items-center">
        <button
          onClick={startWebcam}
          disabled={!model || isDetecting || loading}
          className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
        >
          웹캠 시작
        </button>
        <button
          onClick={stopWebcam}
          disabled={!isDetecting}
          className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
        >
          웹캠 중지
        </button>
        {isDetecting && (
          <div className="flex items-center">
            <span className="text-lg font-semibold">FPS:</span>
            <span className="ml-2 text-xl font-bold text-blue-600">{fps}</span>
          </div>
        )}
      </div>

      {/* 비디오 및 캔버스 */}
      <div className="relative inline-block">
        <video
          ref={videoRef}
          autoPlay
          playsInline
          muted
          width="640"
          height="480"
          className="hidden"
        />
        <canvas
          ref={canvasRef}
          width="640"
          height="480"
          className="border-2 border-gray-300 rounded-lg shadow-lg"
        />
      </div>

      {/* 안내 메시지 */}
      {!isDetecting && !loading && (
        <div className="mt-6 text-center text-gray-500">
          '웹캠 시작' 버튼을 클릭하여 실시간 탐지를 시작하세요
        </div>
      )}

      {/* 범례 */}
      {isDetecting && (
        <div className="mt-6 p-4 bg-gray-50 rounded-lg">
          <h3 className="font-semibold mb-2">탐지 클래스:</h3>
          <div className="flex gap-4">
            {CLASS_LABELS.map((label, idx) => (
              <div key={idx} className="flex items-center gap-2">
                <div className="w-4 h-4 bg-green-500"></div>
                <span>{label}</span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default ChipDetection;