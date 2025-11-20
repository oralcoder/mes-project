import React, { useState, useRef, useEffect } from 'react';

const LLMChat = () => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [selectedModel, setSelectedModel] = useState('qwen2.5:7b');
  const [chatMode, setChatMode] = useState('chat'); // 'chat' or 'rag'
  const [isLoading, setIsLoading] = useState(false);
  const [fileToUpload, setFileToUpload] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const messagesEndRef = useRef(null);
  const fileInputRef = useRef(null);

  const models = [
    { id: 'gemma3:4b', name: 'Gemma 3 (4B)' },
    { id: 'qwen2.5:7b', name: 'Qwen 2.5 (7B)' },
  ];

  // 자동 스크롤
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 파일 선택
  const handleFileSelect = (e) => {
    const file = e.target.files[0];
    if (file) {
      setFileToUpload(file);
    }
  };

  // 파일 임베드
  const handleEmbedFile = async () => {
    if (!fileToUpload) {
      alert('파일을 선택해주세요.');
      return;
    }

    setIsUploading(true);

    try {
      const formData = new FormData();
      formData.append('upload_file', fileToUpload);

      const response = await fetch('http://localhost:8000/rag/upload', {
        method: 'POST',
        body: formData
      });

      if (!response.ok) {
        throw new Error('파일 업로드 실패');
      }

      const data = await response.json();
      
      setFileToUpload(null);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }

      alert(data.message || '파일이 성공적으로 임베드되었습니다.');

    } catch (error) {
      console.error('임베드 오류:', error);
      alert(`임베드 실패: ${error.message}`);
    } finally {
      setIsUploading(false);
    }
  };

  // 메시지 전송
  const handleSend = async () => {
    if (!input.trim() || isLoading) return;

    const userMessage = {
      role: 'user',
      content: input,
      timestamp: new Date().toLocaleTimeString(),
      mode: chatMode
    };

    setMessages(prev => [...prev, userMessage]);
    const currentInput = input;
    setInput('');
    setIsLoading(true);

    try {
      let response;

      if (chatMode === 'rag') {
        // RAG 채팅
        const formData = new FormData();
        formData.append('user_input', currentInput);
        formData.append('model', selectedModel);

        response = await fetch('http://localhost:8000/rag/chat', {
          method: 'POST',
          body: formData
        });
      } else {
        // 일반 채팅
        response = await fetch('http://localhost:8000/ai_chat', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            model: selectedModel,
            message: currentInput
          })
        });
      }

      if (!response.ok) {
        throw new Error('서버 응답 실패');
      }

      const data = await response.json();

      const assistantMessage = {
        role: 'assistant',
        content: data.message || '응답을 받지 못했습니다.',
        model: selectedModel,
        timestamp: new Date().toLocaleTimeString(),
        mode: chatMode
      };

      setMessages(prev => [...prev, assistantMessage]);

    } catch (error) {
      console.error('채팅 오류:', error);
      const errorMessage = {
        role: 'error',
        content: `오류가 발생했습니다: ${error.message}`,
        timestamp: new Date().toLocaleTimeString()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  // Enter 키 처리
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  // 채팅 초기화
  const handleClear = () => {
    setMessages([]);
  };

  return (
    <div className="container mx-auto p-6 h-screen flex flex-col">
      <div className="flex justify-between items-center mb-4">
        <h1 className="text-3xl font-bold">LLM Chat</h1>
        <button
          onClick={handleClear}
          className="px-4 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors"
        >
          대화 초기화
        </button>
      </div>

      <div className="flex gap-4 flex-1 overflow-hidden">
        {/* 채팅 영역 */}
        <div className="flex-1 flex flex-col bg-white rounded-lg shadow-lg">
          {/* 메시지 목록 */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4">
            {messages.length === 0 && (
              <div className="text-center text-gray-400 mt-10">
                <p className="text-xl">대화를 시작해보세요!</p>
                <p className="text-sm mt-2">오른쪽에서 모델을 선택하고 메시지를 입력하세요.</p>
              </div>
            )}

            {messages.map((msg, index) => (
              <div
                key={index}
                className={`flex ${msg.role === 'user' ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-[70%] rounded-lg px-4 py-3 ${
                    msg.role === 'user'
                      ? 'bg-blue-600 text-white'
                      : msg.role === 'error'
                      ? 'bg-red-100 text-red-700 border border-red-300'
                      : 'bg-gray-100 text-gray-800'
                  }`}
                >
                  {msg.role === 'assistant' && (
                    <div className="text-xs text-gray-500 mb-1 flex items-center gap-2">
                      <span>{msg.model}</span>
                      {msg.mode === 'rag' && (
                        <span className="px-2 py-0.5 bg-purple-500 text-white rounded text-xs font-semibold">
                          RAG
                        </span>
                      )}
                    </div>
                  )}
                  {msg.role === 'user' && msg.mode === 'rag' && (
                    <div className="text-xs mb-1 opacity-80">
                      🔍 RAG
                    </div>
                  )}
                  <div className="whitespace-pre-wrap break-words">{msg.content}</div>
                  <div className="text-xs opacity-70 mt-1">{msg.timestamp}</div>
                </div>
              </div>
            ))}

            {isLoading && (
              <div className="flex justify-start">
                <div className="bg-gray-100 rounded-lg px-4 py-3">
                  <div className="flex space-x-2">
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce"></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.1s' }}></div>
                    <div className="w-2 h-2 bg-gray-400 rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                  </div>
                </div>
              </div>
            )}

            <div ref={messagesEndRef} />
          </div>

          {/* 채팅 모드 선택 */}
          <div className="border-t px-4 py-2 bg-gray-50">
            <div className="flex gap-2">
              <button
                onClick={() => setChatMode('chat')}
                className={`flex-1 px-4 py-2 rounded-lg font-medium transition-colors ${
                  chatMode === 'chat'
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                💬 Chat
              </button>
              <button
                onClick={() => setChatMode('rag')}
                className={`flex-1 px-4 py-2 rounded-lg font-medium transition-colors ${
                  chatMode === 'rag'
                    ? 'bg-purple-600 text-white'
                    : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
                }`}
              >
                🔍 RAG
              </button>
            </div>
          </div>

          {/* 입력 영역 */}
          <div className="border-t p-4">
            <div className="flex gap-2">
              <textarea
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="메시지를 입력하세요... (Shift+Enter: 줄바꿈)"
                disabled={isLoading}
                className="flex-1 px-4 py-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
                rows="2"
              />
              <button
                onClick={handleSend}
                disabled={!input.trim() || isLoading}
                className="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors font-semibold"
              >
                전송
              </button>
            </div>
          </div>
        </div>

        {/* 모델 선택 및 파일 업로드 사이드바 */}
        <div className="w-64 bg-white rounded-lg shadow-lg p-4">
          <h2 className="text-lg font-semibold mb-4">모델 선택</h2>
          <div className="space-y-2 mb-6">
            {models.map((model) => (
              <button
                key={model.id}
                onClick={() => setSelectedModel(model.id)}
                disabled={isLoading}
                className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                  selectedModel === model.id
                    ? 'bg-blue-600 text-white font-semibold'
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                } disabled:opacity-50 disabled:cursor-not-allowed`}
              >
                {model.name}
              </button>
            ))}
          </div>

          <hr className="mb-6" />

          {/* 문서 업로드 */}
          <h2 className="text-lg font-semibold mb-4">문서 업로드 (RAG)</h2>
          <input
            ref={fileInputRef}
            type="file"
            onChange={handleFileSelect}
            className="hidden"
            accept=".pdf,.txt,.doc,.docx"
          />
          <button
            onClick={() => fileInputRef.current?.click()}
            disabled={isUploading}
            className="w-full px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors mb-2"
          >
            📎 파일 선택
          </button>
          
          {fileToUpload && (
            <div className="mb-2 p-2 bg-gray-50 rounded text-sm">
              <span className="text-gray-700 break-all">{fileToUpload.name}</span>
            </div>
          )}

          <button
            onClick={handleEmbedFile}
            disabled={!fileToUpload || isUploading}
            className="w-full px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700 disabled:bg-gray-300 disabled:cursor-not-allowed transition-colors font-semibold"
          >
            {isUploading ? '임베딩 중...' : '임베드'}
          </button>
          
          <p className="text-xs text-gray-500 mt-2">
            지원: PDF, TXT, DOC, DOCX
          </p>

          <div className="mt-6 p-3 bg-gray-50 rounded-lg">
            <h3 className="font-semibold text-sm mb-2">현재 선택</h3>
            <p className="text-sm text-gray-600">
              {models.find(m => m.id === selectedModel)?.name}
            </p>
            <p className="text-sm text-gray-600 mt-1">
              모드: <span className="font-semibold">{chatMode === 'chat' ? 'Chat' : 'RAG'}</span>
            </p>
          </div>

          <div className="mt-6 text-xs text-gray-500">
            <p className="mb-1">💡 팁:</p>
            <ul className="list-disc list-inside space-y-1">
              <li>Enter: 전송</li>
              <li>Shift+Enter: 줄바꿈</li>
              <li>RAG: 문서 기반 답변</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LLMChat;