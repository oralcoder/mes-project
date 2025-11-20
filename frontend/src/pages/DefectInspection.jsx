import React, { useState, useEffect, useRef } from 'react';
import * as tf from '@tensorflow/tfjs';

const DefectInspection = () => {
  const [model, setModel] = useState(null);
  const [images, setImages] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modelLoading, setModelLoading] = useState(true);
  const fileInputRef = useRef(null);

  // 클래스 레이블 (모델에 맞게 수정)
  const CLASS_LABELS = ['broken', 'defect','good'];

  // 모델 로드
  useEffect(() => {
    const loadModel = async () => {
      try {
        setModelLoading(true);
        const loadedModel = await tf.loadGraphModel('aug_cnn_model/model.json');
        setModel(loadedModel);
        console.log('모델 로드 완료');
      } catch (error) {
        console.error('모델 로드 실패:', error);
        alert('모델 로드에 실패했습니다.');
      } finally {
        setModelLoading(false);
      }
    };

    loadModel();
  }, []);

  // 이미지 전처리 및 예측
  const predictImage = async (imageFile) => {
    return new Promise((resolve) => {
      const reader = new FileReader();
      reader.onload = async (e) => {
        const img = new Image();
        img.onload = async () => {
          try {
            // 이미지를 텐서로 변환 (모델 입력 크기에 맞게 조정)
            let tensor = tf.browser.fromPixels(img)
              .resizeNearestNeighbor([224, 224]) // 모델 입력 크기에 맞게 수정
              .toFloat()
              .div(255.0) // 정규화
              .expandDims(0);

            // 예측
            const prediction = await model.predict(tensor);
            const probabilities = await prediction.data(); // 예측 결과를 배열로 변환
            // 가장 높은 확률의 클래스 선택
            const predictedClassIndex = probabilities.indexOf(Math.max(...probabilities));
            // 모든 클래스별 확률   
            const allProbabilities = CLASS_LABELS.map((label, index) => ({
              label: label,
              probability: (probabilities[index] * 100).toFixed(2)
            }));

            tensor.dispose();  // 메모리 해제
            prediction.dispose(); // 메모리 해제

            resolve({  // 결과 반환
              file: imageFile,
              preview: e.target.result,
              predictedClass: CLASS_LABELS[predictedClassIndex],
              allProbabilities: allProbabilities
            });
          } catch (error) {
            console.error('예측 실패:', error);
            resolve({
              file: imageFile,
              preview: e.target.result,
              predictedClass: '에러',
              allProbabilities: []
            });
          }
        };
        img.src = e.target.result;
      };
      reader.readAsDataURL(imageFile);
    });
  };

  // 폴더 업로드 핸들러
  const handleFolderUpload = async (e) => {
    const files = Array.from(e.target.files).filter(file => 
      file.type.startsWith('image/')
    );

    if (files.length === 0) {
      alert('이미지 파일이 없습니다.');
      return;
    }

    setLoading(true);
    setImages([]);

    try {
      const predictions = await Promise.all(
        files.map(file => predictImage(file))
      );
      setImages(predictions);
    } catch (error) {
      console.error('이미지 처리 실패:', error);
      alert('이미지 처리 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  // 클래스별 색상
  const getClassColor = (className) => {
    switch(className) {
      case 'good': return 'text-green-600';
      case 'defect': return 'text-yellow-600';
      case 'broken': return 'text-red-600';
      default: return 'text-gray-600';
    }
  };

  return (
    <div className="container mx-auto p-6">
      <h3 className="text-2xl font-bold mb-6">결함 검사</h3>

      {/* 모델 로딩 상태 */}
      {modelLoading && (
        <div className="bg-yellow-100 border border-yellow-400 text-yellow-700 px-4 py-3 rounded mb-4">
          모델을 로딩 중입니다...
        </div>
      )}

      {/* 업로드 버튼 */}
      <div className="mb-6">
        <input
          ref={fileInputRef}
          type="file"
          webkitdirectory="true"
          directory="true"
          multiple
          onChange={handleFolderUpload}
          disabled={!model || loading}
          className="hidden"
        />
        <button
          onClick={() => fileInputRef.current?.click()}
          disabled={!model || loading}
          className="px-5 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed"
        >
          {loading ? '처리 중...' : '폴더 업로드'}
        </button>
        <p className="text-sm text-gray-600 mt-2">
          이미지가 포함된 폴더를 선택하세요
        </p>
      </div>

      {/* 이미지 그리드 */}
      {images.length > 0 && (
        <div className="grid grid-cols-3 gap-4">
          {images.map((img, index) => (
            <div key={index} className="border rounded-lg p-4 shadow-md">
              <img
                src={img.preview}
                alt={img.file.name}
                className="w-full h-48 object-contain rounded mb-3"
              />
              <div className="text-center mb-3">
                <p className="text-sm font-medium text-gray-700 mb-1 truncate">
                  {img.file.name}
                </p>
                <p className={`text-xl font-bold ${getClassColor(img.predictedClass)}`}>
                  {img.predictedClass}
                </p>
              </div>
              
              {/* 모든 클래스 확률 표시 */}
              <div className="space-y-2 text-sm">
                {img.allProbabilities.map((prob, idx) => (
                  <div key={idx} className="flex justify-between items-center">
                    <span className="font-medium">{prob.label}:</span>
                    <div className="flex items-center gap-2 flex-1 ml-2">
                      <div className="flex-1 bg-gray-200 rounded-full h-2">
                        <div
                          className={`h-2 rounded-full ${
                            prob.label === 'good' ? 'bg-green-500' :
                            prob.label === 'defect' ? 'bg-yellow-500' : 'bg-red-500'
                          }`}
                          style={{ width: `${prob.probability}%` }}
                        />
                      </div>
                      <span className="text-xs text-gray-600 w-12 text-right">
                        {prob.probability}%
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* 결과 없을 때 */}
      {!loading && images.length === 0 && model && (
        <div className="text-center text-gray-500 py-12">
          폴더를 업로드하여 이미지 검사를 시작하세요
        </div>
      )}
    </div>
  );
};

export default DefectInspection;