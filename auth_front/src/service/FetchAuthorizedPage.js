import FetchReissue from './FetchReissue';

// 권한이 있는 페이지 접근 시 access 토큰을 검증
const FetchAuthorizedPage = async (
  url,
  navigate,
  location,
  method = 'GET',
  body = null
) => {
  try {
    const token = window.localStorage.getItem('access');
    const response = await fetch(url, {
      method: method,
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`, // local storage 의 access 토큰을 요청 헤더에 추가
      },
      body: body ? JSON.stringify(body) : null, // POST 요청 시 본문 추가
    });

    if (response.ok) {
      return await response.json();
    } else if (response.status === 401) {
      const data = await response.json();
      console.log('첫응답 오류:', data);
      if (data.code === 'JWT400_1') {
        const reissueSuccess = await FetchReissue();
        console.log('reissueSuccess:', reissueSuccess);
        if (reissueSuccess) {
          const newToken = window.localStorage.getItem('access'); // 새로 발급된 토큰 가져오기
          console.log('새 발급된 토큰:', newToken); // 새로 발급된 토큰 확인

          // 새로운 토큰으로 원래 요청 다시 시도
          const retryResponse = await fetch(url, {
            method: method,
            credentials: 'include',
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${newToken}`, // 새 토큰 사용
            },
            body: body ? JSON.stringify(body) : null, // POST 요청 시 본문 추가
          });

          console.log('retryResponse', retryResponse);

          if (retryResponse.ok) {
            return await retryResponse.json(); // 새 요청 결과 반환
          } else {
            const errorData = await retryResponse.json(); // 오류 메시지를 포함하는 데이터 가져오기
            console.log('재요청 실패:', errorData); // 오류 데이터 전체 출력
            alert('재요청 실패: ' + (errorData.message || '알 수 없는 오류'));
          }
        } else {
          alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
          window.localStorage.removeItem('access');
          navigate('/login', { state: location.pathname });
        }
      } else {
        alert(data.message || '인증 오류가 발생했습니다.');
      }
    } else {
      console.error('Error occurred:', response.status);
      alert('문제가 발생했습니다. 다시 시도해 주세요.');
    }
  } catch (error) {
    console.log('error: ', error);
  }
  return;
};

export default FetchAuthorizedPage;
