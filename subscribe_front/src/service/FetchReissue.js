import { Cookies } from 'react-cookie';

const FetchReissue = async () => {
  try {
    const apiUrl = process.env.REACT_APP_API_URL;
    const response = await fetch(`${apiUrl}/auth/reissue`, {
      method: 'POST',
      credentials: 'include', // 쿠키 포함
      headers: {
        'Content-Type': 'application/json',
      },
    });

    if (response.ok) {
      console.log('response.status:', response.status);
      console.log('response.json()', response.json());
      const accessToken = response.headers.get('Authorization'); // fetch에서는 get 메서드 사용

      // 토큰 재발급 성공
      if (accessToken) {
        window.localStorage.setItem('access', accessToken.substring(7)); // "Bearer " 제거
        console.log('access 토큰:', accessToken);
        console.log('토큰 발급 성공');
        return true;
      } else {
        console.log('accessToken이 없습니다.'); // 추가된 로그
      }
    } else {
      // 토큰 재발급 실패
      localStorage.removeItem('access');
      const cookies = new Cookies();
      cookies.set('refresh', null, { maxAge: 0 }); // 리프레시 토큰 삭제
    }
  } catch (error) {
    console.error('Fetch error:', error);
  }

  return false;
};

export default FetchReissue;
