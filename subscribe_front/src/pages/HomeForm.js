import React, { useEffect, useState } from 'react';
import { useLogin } from '../contexts/AuthContext';
import FetchAuthorizedPage from '../service/FetchAuthorizedPage';
import '../HomePage.css'; // 스타일링을 위한 CSS 파일 임포트

const HomePage = ({ navigate, location }) => {
  const { isLoggedIn, loginUser } = useLogin();
  const [topics, setTopics] = useState([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0); // 현재 페이지 상태
  const [totalElements, setTotalElements] = useState(0); // 총 요소 수
  const [newTopic, setNewTopic] = useState(''); // 새로운 주제 상태

  // 주제 가져오기 함수
  const fetchTopics = async () => {
    try {
      const apiUrl = `${process.env.REACT_APP_API_URL}/api/topic?page=${page}&size=10`;
      const response = await FetchAuthorizedPage(apiUrl, navigate, location);

      // 응답 체크 및 상태 업데이트
      if (response && response.result) {
        setTopics(response.result.topics);
        setTotalElements(response.result.totalElements);
      } else {
        console.error('주제를 가져오는 중 응답이 비어있습니다:', response);
      }
    } catch (error) {
      console.error('주제를 가져오는 중 오류 발생:', error);
    } finally {
      setLoading(false);
    }
  };

  // 컴포넌트가 처음 렌더링될 때 주제를 가져옴
  useEffect(() => {
    if (isLoggedIn) {
      fetchTopics(); // 사용자가 로그인한 경우에만 구독 주제 가져오기
    }
  }, [isLoggedIn, page, navigate, location]);

  // 주제 생성 함수
  const handleCreateTopic = async () => {
    const apiUrl = `${process.env.REACT_APP_API_URL}/api/topic`;
    const body = { topic: newTopic };

    try {
      const response = await FetchAuthorizedPage(
        apiUrl,
        navigate,
        location,
        'POST',
        body
      );
      if (response && response.isSuccess) {
        alert(`${newTopic} 주제 생성 성공입니다`);
        setNewTopic(''); // 입력 필드 초기화
        await fetchTopics(); // 최신 주제를 가져옵니다.
      } else {
        console.error('주제 생성 실패:', response);
        alert(`주제 생성 실패: ${response.message || '알 수 없는 오류'}`);
      }
    } catch (error) {
      console.error('주제 생성 중 오류 발생:', error);
      alert(`주제 생성 중 오류 발생: ${error.message || '알 수 없는 오류'}`);
    }
  };

  // 구독 함수
  const handleSubscribe = async (topic) => {
    const apiUrl = `${process.env.REACT_APP_API_URL}/api/topic/subscribe`;
    const body = { topic };

    try {
      const response = await FetchAuthorizedPage(
        apiUrl,
        navigate,
        location,
        'POST',
        body
      );
      if (response && response.isSuccess) {
        alert(` ${topic} 구독 성공입니다 `);
      } else {
        alert(`Error: ${response.message || 'An error occurred'}`);
      }
    } catch (error) {
      console.error(`Unexpected error occurred: ${error}`);
      alert(`다시 시도해 주세요`);
    }
  };

  // 구독 취소 함수
  const handleUnsubscribe = async (topic) => {
    const apiUrl = `${process.env.REACT_APP_API_URL}/api/topic/unsubscribe`;
    const body = { topic };

    try {
      const response = await FetchAuthorizedPage(
        apiUrl,
        navigate,
        location,
        'POST',
        body
      );
      if (response && response.isSuccess) {
        alert(`${topic} 구독 취소 성공입니다`);
      } else {
        console.error(`${topic} 구독 취소 실패 `);
      }
    } catch (error) {
      console.error(`Unexpected error occurred: ${error}`);
    }
  };

  // 페이지 네비게이션
  const handleNextPage = () => {
    if ((page + 1) * 10 < totalElements) {
      setPage(page + 1);
    }
  };

  const handlePreviousPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  // 로딩 중 표시
  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="home-page-container">
      <h1>구독 서비스</h1>
      <h2>{loginUser} 환영합니다</h2>

      {/* 주제 생성 섹션 */}
      <div className="create-topic-container">
        <input
          type="text"
          value={newTopic}
          onChange={(e) => setNewTopic(e.target.value)}
          placeholder="새 주제 입력"
        />
        <button onClick={handleCreateTopic}>주제 생성</button>
      </div>

      <div className="subscription-container">
        <div className="topic-list">
          {topics.map((topic) => (
            <div className="topic-card" key={topic.id}>
              <h3>{topic.topic}</h3>
              <div className="button-container">
                <button
                  className="subscribe-button"
                  onClick={() => handleSubscribe(topic.topic)}
                >
                  구독
                </button>
                <button
                  className="unsubscribe-button"
                  onClick={() => handleUnsubscribe(topic.topic)}
                >
                  구독 취소
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
      <div className="pagination">
        <button onClick={handlePreviousPage} disabled={page === 0}>
          이전
        </button>
        <span>페이지 {page + 1}</span>
        <button
          onClick={handleNextPage}
          disabled={(page + 1) * 10 >= totalElements}
        >
          다음
        </button>
      </div>
    </div>
  );
};

export default HomePage;
