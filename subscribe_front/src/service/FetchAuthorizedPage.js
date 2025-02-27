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
        Authorization: `Bearer ${token}`, // Bearer 형식으로 토큰 설정
      },
      body: body ? JSON.stringify(body) : null, // POST 요청 시 본문 추가
    });

    if (response.ok) {
      return await response.json(); // 성공 시 응답 데이터 반환
    } else {
      const data = await response.json(); // 오류 응답 데이터 가져오기
      console.log('응답 오류:', data);

      if (response.status === 401 && data.code === 'JWT400_1') {
        const reissueSuccess = await FetchReissue();
        console.log('reissueSuccess:', reissueSuccess);

        if (reissueSuccess) {
          const newToken = window.localStorage.getItem('access'); // 새로 발급된 토큰 가져오기
          const retryResponse = await fetch(url, {
            method: method,
            credentials: 'include',
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${newToken}`, // 새 토큰 사용
            },
            body: body ? JSON.stringify(body) : null, // POST 요청 시 본문 추가
          });

          if (retryResponse.ok) {
            return await retryResponse.json(); // 새 요청 결과 반환
          } else {
            const retryData = await retryResponse.json(); // 재요청의 오류 데이터 가져오기
            console.log('재요청 실패:', retryData); // 오류 데이터 전체 출력
            return retryData; // 오류 데이터 반환
          }
        } else {
          alert('세션이 만료되었습니다. 다시 로그인 해주세요.');
          navigate('/login', { state: location.pathname });
        }
      } else {
        return data; // 인증 오류의 경우에도 응답 데이터 반환
      }
    }
  } catch (error) {
    console.log('error: ', error);
    return {
      isSuccess: false,
      code: 'ERROR',
      message: '서버와의 연결에 문제가 발생했습니다.',
      result: null,
    };
  }
};

export default FetchAuthorizedPage;
